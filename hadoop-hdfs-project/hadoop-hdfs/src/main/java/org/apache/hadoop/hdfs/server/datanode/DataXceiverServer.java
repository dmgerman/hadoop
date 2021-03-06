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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Semaphore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Condition
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Server used for receiving/sending a block of data. This is created to listen  * for requests from clients or other DataNodes. This small server does not use  * the Hadoop IPC mechanism.  */
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
name|Logger
name|LOG
init|=
name|DataNode
operator|.
name|LOG
decl_stmt|;
comment|/**    * Default time to wait (in seconds) for the number of running threads to drop    * below the newly requested maximum before giving up.    */
DECL|field|DEFAULT_RECONFIGURE_WAIT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_RECONFIGURE_WAIT
init|=
literal|30
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
argument_list|<>
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
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|noPeers
specifier|private
specifier|final
name|Condition
name|noPeers
init|=
name|lock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|maxReconfigureWaitTime
specifier|private
name|int
name|maxReconfigureWaitTime
init|=
name|DEFAULT_RECONFIGURE_WAIT
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
comment|/**    * A manager to make sure that cluster balancing does not take too much    * resources.    *    * It limits the number of block moves for balancing and the total amount of    * bandwidth they can use.    */
DECL|class|BlockBalanceThrottler
specifier|static
class|class
name|BlockBalanceThrottler
extends|extends
name|DataTransferThrottler
block|{
DECL|field|semaphore
specifier|private
specifier|final
name|Semaphore
name|semaphore
decl_stmt|;
DECL|field|maxThreads
specifier|private
name|int
name|maxThreads
decl_stmt|;
comment|/**     * Constructor.     *     * @param bandwidth Total amount of bandwidth can be used for balancing     */
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
name|semaphore
operator|=
operator|new
name|Semaphore
argument_list|(
name|maxThreads
argument_list|,
literal|true
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
literal|"Balancing bandwidth is "
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
comment|/**      * Update the number of threads which may be used concurrently for moving      * blocks. The number of threads available can be scaled up or down. If      * increasing the number of threads, the request will be serviced      * immediately. However, if decreasing the number of threads, this method      * will block any new request for moves, wait for any existing backlog of      * move requests to clear, and wait for enough threads to have finished such      * that the total number of threads actively running is less than or equal      * to the new cap. If this method has been unable to successfully set the      * new, lower, cap within 'duration' seconds, the attempt will be aborted      * and the original cap will remain.      *      * @param newMaxThreads The new maximum number of threads for block moving      * @param duration The number of seconds to wait if decreasing threads      * @return true if new maximum was successfully applied; false otherwise      */
DECL|method|setMaxConcurrentMovers (final int newMaxThreads, final int duration)
specifier|private
name|boolean
name|setMaxConcurrentMovers
parameter_list|(
specifier|final
name|int
name|newMaxThreads
parameter_list|,
specifier|final
name|int
name|duration
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|newMaxThreads
operator|>
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|int
name|delta
init|=
name|newMaxThreads
operator|-
name|this
operator|.
name|maxThreads
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Change concurrent thread count to {} from {}"
argument_list|,
name|newMaxThreads
argument_list|,
name|this
operator|.
name|maxThreads
argument_list|)
expr_stmt|;
if|if
condition|(
name|delta
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|delta
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding thread capacity: {}"
argument_list|,
name|delta
argument_list|)
expr_stmt|;
name|this
operator|.
name|semaphore
operator|.
name|release
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxThreads
operator|=
name|newMaxThreads
expr_stmt|;
return|return
literal|true
return|;
block|}
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removing thread capacity: {}. Max wait: {}"
argument_list|,
name|delta
argument_list|,
name|duration
argument_list|)
expr_stmt|;
name|boolean
name|acquired
init|=
name|this
operator|.
name|semaphore
operator|.
name|tryAcquire
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|delta
argument_list|)
argument_list|,
name|duration
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|acquired
condition|)
block|{
name|this
operator|.
name|maxThreads
operator|=
name|newMaxThreads
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not lower thread count to {} from {}. Too busy."
argument_list|,
name|newMaxThreads
argument_list|,
name|this
operator|.
name|maxThreads
argument_list|)
expr_stmt|;
block|}
return|return
name|acquired
return|;
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
literal|"Interrupted before adjusting thread count: {}"
argument_list|,
name|delta
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getMaxConcurrentMovers ()
name|int
name|getMaxConcurrentMovers
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxThreads
return|;
block|}
comment|/**     * Check if the block move can start     *     * Return true if the thread quota is not exceeded and     * the counter is incremented; False otherwise.     */
DECL|method|acquire ()
name|boolean
name|acquire
parameter_list|()
block|{
return|return
name|this
operator|.
name|semaphore
operator|.
name|tryAcquire
argument_list|()
return|;
block|}
comment|/**      * Mark that the move is completed. The thread counter is decremented.      */
DECL|method|release ()
name|void
name|release
parameter_list|()
block|{
name|this
operator|.
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|balanceThrottler
specifier|final
name|BlockBalanceThrottler
name|balanceThrottler
decl_stmt|;
DECL|field|transferThrottler
specifier|private
specifier|final
name|DataTransferThrottler
name|transferThrottler
decl_stmt|;
DECL|field|writeThrottler
specifier|private
specifier|final
name|DataTransferThrottler
name|writeThrottler
decl_stmt|;
comment|/**    * Stores an estimate for block size to check if the disk partition has enough    * space. Newer clients pass the expected block size to the DataNode. For    * older clients, just use the server-side default block size.    */
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
name|getLongBytes
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
name|long
name|bandwidthPerSec
init|=
name|conf
operator|.
name|getLongBytes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_TRANSFER_BANDWIDTHPERSEC_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_TRANSFER_BANDWIDTHPERSEC_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|bandwidthPerSec
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|transferThrottler
operator|=
operator|new
name|DataTransferThrottler
argument_list|(
name|bandwidthPerSec
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|transferThrottler
operator|=
literal|null
expr_stmt|;
block|}
name|bandwidthPerSec
operator|=
name|conf
operator|.
name|getLongBytes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_WRITE_BANDWIDTHPERSEC_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_WRITE_BANDWIDTHPERSEC_DEFAULT
argument_list|)
expr_stmt|;
if|if
condition|(
name|bandwidthPerSec
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|writeThrottler
operator|=
operator|new
name|DataTransferThrottler
argument_list|(
name|bandwidthPerSec
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|writeThrottler
operator|=
literal|null
expr_stmt|;
block|}
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
literal|"{}:DataXceiverServer"
argument_list|,
name|datanode
operator|.
name|getDisplayName
argument_list|()
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
name|closeQuietly
argument_list|(
name|peer
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"{}:DataXceiverServer"
argument_list|,
name|datanode
operator|.
name|getDisplayName
argument_list|()
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
name|closeQuietly
argument_list|(
name|peer
argument_list|)
expr_stmt|;
comment|// DataNode can run out of memory if there is too many transfers.
comment|// Log the event, Sleep for 30 seconds, other transfers may complete by
comment|// then.
name|LOG
operator|.
name|error
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
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|30L
argument_list|)
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
literal|"{}:DataXceiverServer: Exiting."
argument_list|,
name|datanode
operator|.
name|getDisplayName
argument_list|()
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
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|closed
condition|)
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
literal|"{}:DataXceiverServer: close exception"
argument_list|,
name|datanode
operator|.
name|getDisplayName
argument_list|()
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
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
name|waitAllPeers
argument_list|(
literal|2L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
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
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|closed
condition|)
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
literal|"{}:DataXceiverServer.kill()"
argument_list|,
name|datanode
operator|.
name|getDisplayName
argument_list|()
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addPeer (Peer peer, Thread t, DataXceiver xceiver)
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
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
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
name|datanode
operator|.
name|metrics
operator|.
name|incrDataNodeActiveXceiversCount
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|closePeer (Peer peer)
name|void
name|closePeer
parameter_list|(
name|Peer
name|peer
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
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
name|datanode
operator|.
name|metrics
operator|.
name|decrDataNodeActiveXceiversCount
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|peer
argument_list|)
expr_stmt|;
if|if
condition|(
name|peers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|noPeers
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Sending OOB to all peers
DECL|method|sendOOBToPeers ()
specifier|public
name|void
name|sendOOBToPeers
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
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
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|stopWriters ()
specifier|public
name|void
name|stopWriters
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|peers
operator|.
name|keySet
argument_list|()
operator|.
name|forEach
argument_list|(
name|p
lambda|->
name|peersXceiver
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|.
name|stopWriter
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Notify all peers of the shutdown and restart. 'datanode.shouldRun' should    * still be true and 'datanode.restarting' should be set true before calling    * this method.    */
DECL|method|restartNotifyPeers ()
name|void
name|restartNotifyPeers
parameter_list|()
block|{
assert|assert
operator|(
name|datanode
operator|.
name|shouldRun
operator|&&
name|datanode
operator|.
name|shutdownForUpgrade
operator|)
assert|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// interrupt each and every DataXceiver thread.
name|peers
operator|.
name|values
argument_list|()
operator|.
name|forEach
argument_list|(
name|t
lambda|->
name|t
operator|.
name|interrupt
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Close all peers and clear the map.    */
DECL|method|closeAllPeers ()
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
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|peers
operator|.
name|keySet
argument_list|()
operator|.
name|forEach
argument_list|(
name|p
lambda|->
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
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
name|datanode
operator|.
name|metrics
operator|.
name|setDataNodeActiveXceiversCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|noPeers
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Causes a thread to block until all peers are removed, a certain amount of    * time has passed, or the thread is interrupted.    *    * @param timeout the maximum time to wait, in nanoseconds    * @param unit the unit of time to wait    * @return true if thread returned because all peers were removed; false    *         otherwise    */
DECL|method|waitAllPeers (long timeout, TimeUnit unit)
specifier|private
name|boolean
name|waitAllPeers
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|long
name|nanos
init|=
name|unit
operator|.
name|toNanos
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
while|while
condition|(
operator|!
name|peers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|nanos
operator|<=
literal|0L
condition|)
block|{
return|return
literal|false
return|;
block|}
name|nanos
operator|=
name|noPeers
operator|.
name|awaitNanos
argument_list|(
name|nanos
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Interrupted waiting for peers to close"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Return the number of peers.    *    * @return the number of active peers    */
DECL|method|getNumPeers ()
name|int
name|getNumPeers
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|peers
operator|.
name|size
argument_list|()
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Return the number of peers and DataXceivers.    *    * @return the number of peers and DataXceivers.    */
annotation|@
name|VisibleForTesting
DECL|method|getNumPeersXceiver ()
name|int
name|getNumPeersXceiver
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|peersXceiver
operator|.
name|size
argument_list|()
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPeerServer ()
name|PeerServer
name|getPeerServer
parameter_list|()
block|{
return|return
name|peerServer
return|;
block|}
DECL|method|getTransferThrottler ()
specifier|public
name|DataTransferThrottler
name|getTransferThrottler
parameter_list|()
block|{
return|return
name|transferThrottler
return|;
block|}
DECL|method|getWriteThrottler ()
specifier|public
name|DataTransferThrottler
name|getWriteThrottler
parameter_list|()
block|{
return|return
name|writeThrottler
return|;
block|}
comment|/**    * Release a peer.    *    * @param peer The peer to release    */
DECL|method|releasePeer (Peer peer)
name|void
name|releasePeer
parameter_list|(
name|Peer
name|peer
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
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
name|datanode
operator|.
name|metrics
operator|.
name|decrDataNodeActiveXceiversCount
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Update the number of threads which may be used concurrently for moving    * blocks.    *    * @param movers The new maximum number of threads for block moving    * @return true if new maximum was successfully applied; false otherwise    */
DECL|method|updateBalancerMaxConcurrentMovers (final int movers)
specifier|public
name|boolean
name|updateBalancerMaxConcurrentMovers
parameter_list|(
specifier|final
name|int
name|movers
parameter_list|)
block|{
return|return
name|balanceThrottler
operator|.
name|setMaxConcurrentMovers
argument_list|(
name|movers
argument_list|,
name|this
operator|.
name|maxReconfigureWaitTime
argument_list|)
return|;
block|}
comment|/**    * Update the maximum amount of time to wait for reconfiguration of the    * maximum number of block mover threads to complete.    *    * @param max The new maximum number of threads for block moving, in seconds    */
annotation|@
name|VisibleForTesting
DECL|method|setMaxReconfigureWaitTime (int max)
name|void
name|setMaxReconfigureWaitTime
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|this
operator|.
name|maxReconfigureWaitTime
operator|=
name|max
expr_stmt|;
block|}
block|}
end_class

end_unit

