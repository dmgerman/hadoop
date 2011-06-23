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
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
name|util
operator|.
name|Collections
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|protocol
operator|.
name|FSConstants
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
name|server
operator|.
name|balancer
operator|.
name|Balancer
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
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
implements|,
name|FSConstants
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
DECL|field|ss
name|ServerSocket
name|ss
decl_stmt|;
DECL|field|datanode
name|DataNode
name|datanode
decl_stmt|;
comment|// Record all sockets opened for data transfer
DECL|field|childSockets
name|Map
argument_list|<
name|Socket
argument_list|,
name|Socket
argument_list|>
name|childSockets
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Socket
argument_list|,
name|Socket
argument_list|>
argument_list|()
argument_list|)
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
comment|/**Constructor     *      * @param bandwidth Total amount of bandwidth can be used for balancing      */
DECL|method|BlockBalanceThrottler (long bandwidth)
specifier|private
name|BlockBalanceThrottler
parameter_list|(
name|long
name|bandwidth
parameter_list|)
block|{
name|super
argument_list|(
name|bandwidth
argument_list|)
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
name|Balancer
operator|.
name|MAX_NUM_CONCURRENT_MOVES
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
name|BlockBalanceThrottler
name|balanceThrottler
decl_stmt|;
comment|/**    * We need an estimate for block size to check if the disk partition has    * enough space. For now we set it to be the default block size set    * in the server side configuration, which is not ideal because the    * default block size should be a client-size configuration.     * A better solution is to include in the header the estimated block size,    * i.e. either the actual block size or the default block size.    */
DECL|field|estimateBlockSize
name|long
name|estimateBlockSize
decl_stmt|;
DECL|method|DataXceiverServer (ServerSocket ss, Configuration conf, DataNode datanode)
name|DataXceiverServer
parameter_list|(
name|ServerSocket
name|ss
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
name|ss
operator|=
name|ss
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
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
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
while|while
condition|(
name|datanode
operator|.
name|shouldRun
condition|)
block|{
try|try
block|{
name|Socket
name|s
init|=
name|ss
operator|.
name|accept
argument_list|()
decl_stmt|;
name|s
operator|.
name|setTcpNoDelay
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|DataXceiver
name|exciver
decl_stmt|;
try|try
block|{
name|exciver
operator|=
operator|new
name|DataXceiver
argument_list|(
name|s
argument_list|,
name|datanode
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|IOUtils
operator|.
name|closeSocket
argument_list|(
name|s
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
operator|new
name|Daemon
argument_list|(
name|datanode
operator|.
name|threadGroup
argument_list|,
name|exciver
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
name|getMachineName
argument_list|()
operator|+
literal|":DataXceiveServer: "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
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
name|getMachineName
argument_list|()
operator|+
literal|":DataXceiveServer: Exiting due to: "
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
try|try
block|{
name|ss
operator|.
name|close
argument_list|()
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
name|getMachineName
argument_list|()
operator|+
literal|":DataXceiveServer: Close exception due to: "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|kill ()
name|void
name|kill
parameter_list|()
block|{
assert|assert
name|datanode
operator|.
name|shouldRun
operator|==
literal|false
operator|:
literal|"shoudRun should be set to false before killing"
assert|;
try|try
block|{
name|this
operator|.
name|ss
operator|.
name|close
argument_list|()
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
name|getMachineName
argument_list|()
operator|+
literal|":DataXceiveServer.kill(): "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ie
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// close all the sockets that were accepted earlier
synchronized|synchronized
init|(
name|childSockets
init|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Socket
argument_list|>
name|it
init|=
name|childSockets
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Socket
name|thissock
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|thissock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
block|}
block|}
block|}
end_class

end_unit

