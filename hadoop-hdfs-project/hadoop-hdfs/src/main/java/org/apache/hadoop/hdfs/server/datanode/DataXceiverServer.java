begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketTimeoutException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|AsynchronousCloseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|net
operator|.
name|Peer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|net
operator|.
name|PeerServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
operator|.
name|DataTransferThrottler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Daemon
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * Server used for receiving/sending a block of data.  * This is created to listen for requests from clients or   * other DataNodes.  This small server does not use the   * Hadoop IPC mechanism.  */
end_comment

begin_class
DECL|class|DataXceiverServer
class|class
name|DataXceiverServer
implements|implements
name|Runnable
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|DataNode
operator|.
name|LOG
decl_stmt|;
DECL|field|peerServer
specifier|private
specifier|final
name|PeerServer
name|peerServer
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|final
name|DataNode
name|datanode
decl_stmt|;
DECL|field|peers
specifier|private
specifier|final
name|HashMap
argument_list|<
name|Peer
argument_list|,
name|Thread
argument_list|>
name|peers
init|=
operator|new
name|HashMap
argument_list|<
name|Peer
argument_list|,
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|peersXceiver
specifier|private
specifier|final
name|HashMap
argument_list|<
name|Peer
argument_list|,
name|DataXceiver
argument_list|>
name|peersXceiver
init|=
operator|new
name|HashMap
argument_list|<
name|Peer
argument_list|,
name|DataXceiver
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
comment|/**    * Maximal number of concurrent xceivers per node.    * Enforcing the limit is required in order to avoid data-node    * running out of memory.    */
DECL|field|maxXceiverCount
name|int
name|maxXceiverCount
init|=
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_RECEIVER_THREADS_DEFAULT
decl_stmt|;
comment|/** A manager to make sure that cluster balancing does not    * take too much resources.    *     * It limits the number of block moves for balancing and    * the total amount of bandwidth they can use.    */
DECL|class|BlockBalanceThrottler
specifier|static
class|class
name|BlockBalanceThrottler
extends|extends
name|DataTransferThrottler
block|{
DECL|field|numThreads
specifier|private
name|int
name|numThreads
decl_stmt|;
DECL|field|maxThreads
specifier|private
name|int
name|maxThreads
decl_stmt|;
comment|/**Constructor     *      * @param bandwidth Total amount of bandwidth can be used for balancing      */
DECL|method|BlockBalanceThrottler (long bandwidth, int maxThreads)
specifier|private
name|BlockBalanceThrottler
parameter_list|(
name|long
name|bandwidth
parameter_list|,
name|int
name|maxThreads
parameter_list|)
block|{
name|super
argument_list|(
name|bandwidth
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxThreads
operator|=
name|maxThreads
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Balancing bandwith is "
operator|+
name|bandwidth
operator|+
literal|" bytes/s"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number threads for balancing is "
operator|+
name|maxThreads
argument_list|)
expr_stmt|;
block|}
comment|/** Check if the block move can start.      *      * Return true if the thread quota is not exceeded and      * the counter is incremented; False otherwise.     */
DECL|method|acquire ()
specifier|synchronized
name|boolean
name|acquire
parameter_list|()
block|{
if|if
condition|(
name|numThreads
operator|>=
name|maxThreads
condition|)
block|{
return|return
literal|false
return|;
block|}
name|numThreads
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** Mark that the move is completed. The thread counter is decremented. */
DECL|method|release ()
specifier|synchronized
name|void
name|release
parameter_list|()
block|{
name|numThreads
operator|--
expr_stmt|;
block|}
block|}
DECL|field|balanceThrottler
specifier|final
name|BlockBalanceThrottler
name|balanceThrottler
decl_stmt|;
comment|/**    * We need an estimate for block size to check if the disk partition has    * enough space. For now we set it to be the default block size set    * in the server side configuration, which is not ideal because the    * default block size should be a client-size configuration.     * A better solution is to include in the header the estimated block size,    * i.e. either the actual block size or the default block size.    */
DECL|field|estimateBlockSize
specifier|final
name|long
name|estimateBlockSize
decl_stmt|;
DECL|method|DataXceiverServer (PeerServer peerServer, Configuration conf, DataNode datanode)
name|DataXceiverServer
parameter_list|(
name|PeerServer
name|peerServer
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|DataNode
name|datanode
parameter_list|)
block|{
name|this
operator|.
name|peerServer
operator|=
name|peerServer
expr_stmt|;
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
name|this
operator|.
name|maxXceiverCount
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_RECEIVER_THREADS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_RECEIVER_THREADS_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|estimateBlockSize
operator|=
name|conf
operator|.
name|getLongBytes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_DEFAULT
argument_list|)
expr_stmt|;
comment|//set up parameter for cluster balancing
name|this
operator|.
name|balanceThrottler
operator|=
operator|new
name|BlockBalanceThrottler
argument_list|(
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_DEFAULT
argument_list|)
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Peer
name|peer
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|datanode
operator|.
name|shouldRun
operator|&&
operator|!
name|datanode
operator|.
name|shutdownForUpgrade
condition|)
block|{
try|try
block|{
name|peer
operator|=
name|peerServer
operator|.
name|accept
argument_list|()
expr_stmt|;
comment|// Make sure the xceiver count is not exceeded
name|int
name|curXceiverCount
init|=
name|datanode
operator|.
name|getXceiverCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|curXceiverCount
operator|>
name|maxXceiverCount
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Xceiver count "
operator|+
name|curXceiverCount
operator|+
literal|" exceeds the limit of concurrent xcievers: "
operator|+
name|maxXceiverCount
argument_list|)
throw|;
block|}
operator|new
name|Daemon
argument_list|(
name|datanode
operator|.
name|threadGroup
argument_list|,
name|DataXceiver
operator|.
name|create
argument_list|(
name|peer
argument_list|,
name|datanode
argument_list|,
name|this
argument_list|)
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|ignored
parameter_list|)
block|{
comment|// wake up to see if should continue to run
block|}
catch|catch
parameter_list|(
name|AsynchronousCloseException
name|ace
parameter_list|)
block|{
comment|// another thread closed our listener socket - that's expected during shutdown,
comment|// but not in other circumstances
if|if
condition|(
name|datanode
operator|.
name|shouldRun
operator|&&
operator|!
name|datanode
operator|.
name|shutdownForUpgrade
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|datanode
operator|.
name|getDisplayName
argument_list|()
operator|+
literal|":DataXceiverServer: "
argument_list|,
name|ace
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|peer
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|datanode
operator|.
name|getDisplayName
argument_list|()
operator|+
literal|":DataXceiverServer: "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|ie
parameter_list|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|peer
argument_list|)
expr_stmt|;
comment|// DataNode can run out of memory if there is too many transfers.
comment|// Log the event, Sleep for 30 seconds, other transfers may complete by
comment|// then.
name|LOG
operator|.
name|warn
argument_list|(
literal|"DataNode is out of memory. Will retry in 30 seconds."
argument_list|,
name|ie
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|30
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|te
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|datanode
operator|.
name|getDisplayName
argument_list|()
operator|+
literal|":DataXceiverServer: Exiting due to: "
argument_list|,
name|te
argument_list|)
expr_stmt|;
name|datanode
operator|.
name|shouldRun
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|// Close the server to stop reception of more requests.
try|try
block|{
name|peerServer
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|datanode
operator|.
name|getDisplayName
argument_list|()
operator|+
literal|" :DataXceiverServer: close exception"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
comment|// if in restart prep stage, notify peers before closing them.
if|if
condition|(
name|datanode
operator|.
name|shutdownForUpgrade
condition|)
block|{
name|restartNotifyPeers
argument_list|()
expr_stmt|;
comment|// Each thread needs some time to process it. If a thread needs
comment|// to send an OOB message to the client, but blocked on network for
comment|// long time, we need to force its termination.
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down DataXceiverServer before restart"
argument_list|)
expr_stmt|;
comment|// Allow roughly up to 2 seconds.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|getNumPeers
argument_list|()
operator|>
literal|0
operator|&&
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
comment|// Close all peers.
name|closeAllPeers
argument_list|()
expr_stmt|;
block|}
DECL|method|kill ()
name|void
name|kill
parameter_list|()
block|{
assert|assert
operator|(
name|datanode
operator|.
name|shouldRun
operator|==
literal|false
operator|||
name|datanode
operator|.
name|shutdownForUpgrade
operator|)
operator|:
literal|"shoudRun should be set to false or restarting should be true"
operator|+
literal|" before killing"
assert|;
try|try
block|{
name|this
operator|.
name|peerServer
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|datanode
operator|.
name|getDisplayName
argument_list|()
operator|+
literal|":DataXceiverServer.kill(): "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addPeer (Peer peer, Thread t, DataXceiver xceiver)
specifier|synchronized
name|void
name|addPeer
parameter_list|(
name|Peer
name|peer
parameter_list|,
name|Thread
name|t
parameter_list|,
name|DataXceiver
name|xceiver
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Server closed."
argument_list|)
throw|;
block|}
name|peers
operator|.
name|put
argument_list|(
name|peer
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|peersXceiver
operator|.
name|put
argument_list|(
name|peer
argument_list|,
name|xceiver
argument_list|)
expr_stmt|;
block|}
DECL|method|closePeer (Peer peer)
specifier|synchronized
name|void
name|closePeer
parameter_list|(
name|Peer
name|peer
parameter_list|)
block|{
name|peers
operator|.
name|remove
argument_list|(
name|peer
argument_list|)
expr_stmt|;
name|peersXceiver
operator|.
name|remove
argument_list|(
name|peer
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|peer
argument_list|)
expr_stmt|;
block|}
comment|// Sending OOB to all peers
DECL|method|sendOOBToPeers ()
specifier|public
specifier|synchronized
name|void
name|sendOOBToPeers
parameter_list|()
block|{
if|if
condition|(
operator|!
name|datanode
operator|.
name|shutdownForUpgrade
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Peer
name|p
range|:
name|peers
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|peersXceiver
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|.
name|sendOOB
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Got error when sending OOB message."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted when sending OOB message."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Notify all peers of the shutdown and restart.
comment|// datanode.shouldRun should still be true and datanode.restarting should
comment|// be set true before calling this method.
DECL|method|restartNotifyPeers ()
specifier|synchronized
name|void
name|restartNotifyPeers
parameter_list|()
block|{
assert|assert
operator|(
name|datanode
operator|.
name|shouldRun
operator|==
literal|true
operator|&&
name|datanode
operator|.
name|shutdownForUpgrade
operator|)
assert|;
for|for
control|(
name|Peer
name|p
range|:
name|peers
operator|.
name|keySet
argument_list|()
control|)
block|{
comment|// interrupt each and every DataXceiver thread.
name|peers
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Close all peers and clear the map.
DECL|method|closeAllPeers ()
specifier|synchronized
name|void
name|closeAllPeers
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing all peers."
argument_list|)
expr_stmt|;
for|for
control|(
name|Peer
name|p
range|:
name|peers
operator|.
name|keySet
argument_list|()
control|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
name|peers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|peersXceiver
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Return the number of peers.
DECL|method|getNumPeers ()
specifier|synchronized
name|int
name|getNumPeers
parameter_list|()
block|{
return|return
name|peers
operator|.
name|size
argument_list|()
return|;
block|}
comment|// Return the number of peers and DataXceivers.
annotation|@
name|VisibleForTesting
DECL|method|getNumPeersXceiver ()
specifier|synchronized
name|int
name|getNumPeersXceiver
parameter_list|()
block|{
return|return
name|peersXceiver
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|releasePeer (Peer peer)
specifier|synchronized
name|void
name|releasePeer
parameter_list|(
name|Peer
name|peer
parameter_list|)
block|{
name|peers
operator|.
name|remove
argument_list|(
name|peer
argument_list|)
expr_stmt|;
name|peersXceiver
operator|.
name|remove
argument_list|(
name|peer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

