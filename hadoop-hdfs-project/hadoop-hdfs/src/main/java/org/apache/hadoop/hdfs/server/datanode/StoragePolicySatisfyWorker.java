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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolPB
operator|.
name|PBHelperClient
operator|.
name|vintPrefixed
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Callable
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
name|CompletionService
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
name|ExecutionException
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
name|ExecutorCompletionService
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
name|ExecutorService
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
name|Future
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
name|SynchronousQueue
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
name|ThreadPoolExecutor
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
name|atomic
operator|.
name|AtomicInteger
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
name|classification
operator|.
name|InterfaceAudience
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
name|fs
operator|.
name|StorageType
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
name|DFSUtilClient
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
name|Block
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
name|DatanodeInfo
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
name|ExtendedBlock
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
name|datatransfer
operator|.
name|DataTransferProtoUtil
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
name|datatransfer
operator|.
name|IOStreamPair
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
name|datatransfer
operator|.
name|Sender
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
name|datatransfer
operator|.
name|sasl
operator|.
name|DataEncryptionKeyFactory
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|BlockOpResponseProto
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|Status
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|protocol
operator|.
name|BlockStorageMovementCommand
operator|.
name|BlockMovingInfo
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
name|net
operator|.
name|NetUtils
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * StoragePolicySatisfyWorker handles the storage policy satisfier commands.  * These commands would be issued from NameNode as part of Datanode's heart beat  * response. BPOfferService delegates the work to this class for handling  * BlockStorageMovement commands.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StoragePolicySatisfyWorker
specifier|public
class|class
name|StoragePolicySatisfyWorker
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StoragePolicySatisfyWorker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|final
name|DataNode
name|datanode
decl_stmt|;
DECL|field|ioFileBufferSize
specifier|private
specifier|final
name|int
name|ioFileBufferSize
decl_stmt|;
DECL|field|moverThreads
specifier|private
specifier|final
name|int
name|moverThreads
decl_stmt|;
DECL|field|moveExecutor
specifier|private
specifier|final
name|ExecutorService
name|moveExecutor
decl_stmt|;
DECL|field|moverExecutorCompletionService
specifier|private
specifier|final
name|CompletionService
argument_list|<
name|Void
argument_list|>
name|moverExecutorCompletionService
decl_stmt|;
DECL|field|moverTaskFutures
specifier|private
specifier|final
name|List
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|moverTaskFutures
decl_stmt|;
DECL|method|StoragePolicySatisfyWorker (Configuration conf, DataNode datanode)
specifier|public
name|StoragePolicySatisfyWorker
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DataNode
name|datanode
parameter_list|)
block|{
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
name|this
operator|.
name|ioFileBufferSize
operator|=
name|DFSUtilClient
operator|.
name|getIoFileBufferSize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|moverThreads
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_MOVER_MOVERTHREADS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_MOVER_MOVERTHREADS_DEFAULT
argument_list|)
expr_stmt|;
name|moveExecutor
operator|=
name|initializeBlockMoverThreadPool
argument_list|(
name|moverThreads
argument_list|)
expr_stmt|;
name|moverExecutorCompletionService
operator|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|moveExecutor
argument_list|)
expr_stmt|;
name|moverTaskFutures
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
comment|// TODO: Needs to manage the number of concurrent moves per DataNode.
block|}
DECL|method|initializeBlockMoverThreadPool (int num)
specifier|private
name|ThreadPoolExecutor
name|initializeBlockMoverThreadPool
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Block mover to satisfy storage policy; pool threads={}"
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|ThreadPoolExecutor
name|moverThreadPool
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
name|num
argument_list|,
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|Daemon
operator|.
name|DaemonFactory
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicInteger
name|threadIndex
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|t
init|=
name|super
operator|.
name|newThread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
literal|"BlockMoverTask-"
operator|+
name|threadIndex
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|,
operator|new
name|ThreadPoolExecutor
operator|.
name|CallerRunsPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|rejectedExecution
parameter_list|(
name|Runnable
name|runnable
parameter_list|,
name|ThreadPoolExecutor
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Execution for block movement to satisfy storage policy"
operator|+
literal|" got rejected, Executing in current thread"
argument_list|)
expr_stmt|;
comment|// will run in the current thread.
name|super
operator|.
name|rejectedExecution
argument_list|(
name|runnable
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|moverThreadPool
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|moverThreadPool
return|;
block|}
comment|/**    * Handles the given set of block movement tasks. This will iterate over the    * block movement list and submit each block movement task asynchronously in a    * separate thread. Each task will move the block replica to the target node&    * wait for the completion.    *    * TODO: Presently this function is a blocking call, this has to be refined by    * moving the tracking logic to another tracker thread. HDFS-10884 jira    * addresses the same.    *    * @param trackID    *          unique tracking identifier    * @param blockPoolID    *          block pool ID    * @param blockMovingInfos    *          list of blocks to be moved    */
DECL|method|processBlockMovingTasks (long trackID, String blockPoolID, Collection<BlockMovingInfo> blockMovingInfos)
specifier|public
name|void
name|processBlockMovingTasks
parameter_list|(
name|long
name|trackID
parameter_list|,
name|String
name|blockPoolID
parameter_list|,
name|Collection
argument_list|<
name|BlockMovingInfo
argument_list|>
name|blockMovingInfos
parameter_list|)
block|{
name|Future
argument_list|<
name|Void
argument_list|>
name|moveCallable
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BlockMovingInfo
name|blkMovingInfo
range|:
name|blockMovingInfos
control|)
block|{
assert|assert
name|blkMovingInfo
operator|.
name|getSources
argument_list|()
operator|.
name|length
operator|==
name|blkMovingInfo
operator|.
name|getTargets
argument_list|()
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blkMovingInfo
operator|.
name|getSources
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BlockMovingTask
name|blockMovingTask
init|=
operator|new
name|BlockMovingTask
argument_list|(
name|blkMovingInfo
operator|.
name|getBlock
argument_list|()
argument_list|,
name|blockPoolID
argument_list|,
name|blkMovingInfo
operator|.
name|getSources
argument_list|()
index|[
name|i
index|]
argument_list|,
name|blkMovingInfo
operator|.
name|getTargets
argument_list|()
index|[
name|i
index|]
argument_list|,
name|blkMovingInfo
operator|.
name|getTargetStorageTypes
argument_list|()
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|moveCallable
operator|=
name|moverExecutorCompletionService
operator|.
name|submit
argument_list|(
name|blockMovingTask
argument_list|)
expr_stmt|;
name|moverTaskFutures
operator|.
name|add
argument_list|(
name|moveCallable
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|moverTaskFutures
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|moveCallable
operator|=
name|moverExecutorCompletionService
operator|.
name|take
argument_list|()
expr_stmt|;
name|moveCallable
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// TODO: Failure retries and report back the error to NameNode.
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while moving block replica to target storage type"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * This class encapsulates the process of moving the block replica to the    * given target.    */
DECL|class|BlockMovingTask
specifier|private
class|class
name|BlockMovingTask
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
DECL|field|block
specifier|private
specifier|final
name|Block
name|block
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|DatanodeInfo
name|source
decl_stmt|;
DECL|field|target
specifier|private
specifier|final
name|DatanodeInfo
name|target
decl_stmt|;
DECL|field|targetStorageType
specifier|private
specifier|final
name|StorageType
name|targetStorageType
decl_stmt|;
DECL|field|blockPoolID
specifier|private
name|String
name|blockPoolID
decl_stmt|;
DECL|method|BlockMovingTask (Block block, String blockPoolID, DatanodeInfo source, DatanodeInfo target, StorageType targetStorageType)
name|BlockMovingTask
parameter_list|(
name|Block
name|block
parameter_list|,
name|String
name|blockPoolID
parameter_list|,
name|DatanodeInfo
name|source
parameter_list|,
name|DatanodeInfo
name|target
parameter_list|,
name|StorageType
name|targetStorageType
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|blockPoolID
operator|=
name|blockPoolID
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|targetStorageType
operator|=
name|targetStorageType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
block|{
name|moveBlock
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|moveBlock ()
specifier|private
name|void
name|moveBlock
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Start moving block {}"
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Start moving block:{} from src:{} to destin:{} to satisfy "
operator|+
literal|"storageType:{}"
argument_list|,
name|block
argument_list|,
name|source
argument_list|,
name|target
argument_list|,
name|targetStorageType
argument_list|)
expr_stmt|;
name|Socket
name|sock
init|=
literal|null
decl_stmt|;
name|DataOutputStream
name|out
init|=
literal|null
decl_stmt|;
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|DNConf
name|dnConf
init|=
name|datanode
operator|.
name|getDnConf
argument_list|()
decl_stmt|;
name|String
name|dnAddr
init|=
name|target
operator|.
name|getXferAddr
argument_list|(
name|dnConf
operator|.
name|getConnectToDnViaHostname
argument_list|()
argument_list|)
decl_stmt|;
name|sock
operator|=
name|datanode
operator|.
name|newSocket
argument_list|()
expr_stmt|;
name|NetUtils
operator|.
name|connect
argument_list|(
name|sock
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|dnAddr
argument_list|)
argument_list|,
name|dnConf
operator|.
name|getSocketTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|sock
operator|.
name|setSoTimeout
argument_list|(
literal|2
operator|*
name|dnConf
operator|.
name|getSocketTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to datanode {}"
argument_list|,
name|dnAddr
argument_list|)
expr_stmt|;
name|OutputStream
name|unbufOut
init|=
name|sock
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|InputStream
name|unbufIn
init|=
name|sock
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|ExtendedBlock
name|extendedBlock
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|blockPoolID
argument_list|,
name|block
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|accessToken
init|=
name|datanode
operator|.
name|getBlockAccessToken
argument_list|(
name|extendedBlock
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|BlockTokenIdentifier
operator|.
name|AccessMode
operator|.
name|WRITE
argument_list|)
argument_list|)
decl_stmt|;
name|DataEncryptionKeyFactory
name|keyFactory
init|=
name|datanode
operator|.
name|getDataEncryptionKeyFactoryForBlock
argument_list|(
name|extendedBlock
argument_list|)
decl_stmt|;
name|IOStreamPair
name|saslStreams
init|=
name|datanode
operator|.
name|getSaslClient
argument_list|()
operator|.
name|socketSend
argument_list|(
name|sock
argument_list|,
name|unbufOut
argument_list|,
name|unbufIn
argument_list|,
name|keyFactory
argument_list|,
name|accessToken
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|unbufOut
operator|=
name|saslStreams
operator|.
name|out
expr_stmt|;
name|unbufIn
operator|=
name|saslStreams
operator|.
name|in
expr_stmt|;
name|out
operator|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|unbufOut
argument_list|,
name|ioFileBufferSize
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|unbufIn
argument_list|,
name|ioFileBufferSize
argument_list|)
argument_list|)
expr_stmt|;
name|sendRequest
argument_list|(
name|out
argument_list|,
name|extendedBlock
argument_list|,
name|accessToken
argument_list|,
name|source
argument_list|,
name|targetStorageType
argument_list|)
expr_stmt|;
name|receiveResponse
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully moved block:{} from src:{} to destin:{} for"
operator|+
literal|" satisfying storageType:{}"
argument_list|,
name|block
argument_list|,
name|source
argument_list|,
name|target
argument_list|,
name|targetStorageType
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: handle failure retries
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to move block:{} from src:{} to destin:{} to satisfy "
operator|+
literal|"storageType:{}"
argument_list|,
name|block
argument_list|,
name|source
argument_list|,
name|target
argument_list|,
name|targetStorageType
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Send a reportedBlock replace request to the output stream. */
DECL|method|sendRequest (DataOutputStream out, ExtendedBlock eb, Token<BlockTokenIdentifier> accessToken, DatanodeInfo srcDn, StorageType destinStorageType)
specifier|private
name|void
name|sendRequest
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|ExtendedBlock
name|eb
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|accessToken
parameter_list|,
name|DatanodeInfo
name|srcDn
parameter_list|,
name|StorageType
name|destinStorageType
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|Sender
argument_list|(
name|out
argument_list|)
operator|.
name|replaceBlock
argument_list|(
name|eb
argument_list|,
name|destinStorageType
argument_list|,
name|accessToken
argument_list|,
name|srcDn
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|srcDn
argument_list|)
expr_stmt|;
block|}
comment|/** Receive a reportedBlock copy response from the input stream. */
DECL|method|receiveResponse (DataInputStream in)
specifier|private
name|void
name|receiveResponse
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockOpResponseProto
name|response
init|=
name|BlockOpResponseProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|IN_PROGRESS
condition|)
block|{
comment|// read intermediate responses
name|response
operator|=
name|BlockOpResponseProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|logInfo
init|=
literal|"reportedBlock move is failed"
decl_stmt|;
name|DataTransferProtoUtil
operator|.
name|checkBlockOpStatus
argument_list|(
name|response
argument_list|,
name|logInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

