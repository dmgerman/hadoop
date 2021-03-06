begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
name|namenode
operator|.
name|ha
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
name|util
operator|.
name|Time
operator|.
name|monotonicNow
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
name|collect
operator|.
name|Lists
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|HashMap
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
name|Map
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
name|*
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
name|ha
operator|.
name|ServiceFailedException
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
name|DFSUtil
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
name|HAUtil
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
name|namenode
operator|.
name|CheckpointConf
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
name|namenode
operator|.
name|CheckpointFaultInjector
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
name|namenode
operator|.
name|FSImage
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
name|namenode
operator|.
name|FSNamesystem
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
name|namenode
operator|.
name|NNStorage
operator|.
name|NameNodeFile
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
name|namenode
operator|.
name|NameNode
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
name|namenode
operator|.
name|SaveNamespaceCancelledException
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
name|namenode
operator|.
name|TransferFsImage
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
name|Canceler
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
name|MultipleIOException
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
name|SecurityUtil
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
name|UserGroupInformation
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
comment|/**  * Thread which runs inside the NN when it's in Standby state,  * periodically waking up to take a checkpoint of the namespace.  * When it takes a checkpoint, it saves it to its local  * storage and then uploads it to the remote NameNode.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StandbyCheckpointer
specifier|public
class|class
name|StandbyCheckpointer
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
name|StandbyCheckpointer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PREVENT_AFTER_CANCEL_MS
specifier|private
specifier|static
specifier|final
name|long
name|PREVENT_AFTER_CANCEL_MS
init|=
literal|2
operator|*
literal|60
operator|*
literal|1000L
decl_stmt|;
DECL|field|checkpointConf
specifier|private
specifier|final
name|CheckpointConf
name|checkpointConf
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|namesystem
specifier|private
specifier|final
name|FSNamesystem
name|namesystem
decl_stmt|;
DECL|field|lastCheckpointTime
specifier|private
name|long
name|lastCheckpointTime
decl_stmt|;
DECL|field|thread
specifier|private
specifier|final
name|CheckpointerThread
name|thread
decl_stmt|;
DECL|field|uploadThreadFactory
specifier|private
specifier|final
name|ThreadFactory
name|uploadThreadFactory
decl_stmt|;
DECL|field|activeNNAddresses
specifier|private
name|List
argument_list|<
name|URL
argument_list|>
name|activeNNAddresses
decl_stmt|;
DECL|field|myNNAddress
specifier|private
name|URL
name|myNNAddress
decl_stmt|;
DECL|field|cancelLock
specifier|private
specifier|final
name|Object
name|cancelLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|canceler
specifier|private
name|Canceler
name|canceler
decl_stmt|;
comment|// Keep track of how many checkpoints were canceled.
comment|// This is for use in tests.
DECL|field|canceledCount
specifier|private
specifier|static
name|int
name|canceledCount
init|=
literal|0
decl_stmt|;
comment|// A map from NN url to the most recent image upload time.
DECL|field|checkpointReceivers
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|CheckpointReceiverEntry
argument_list|>
name|checkpointReceivers
decl_stmt|;
DECL|method|StandbyCheckpointer (Configuration conf, FSNamesystem ns)
specifier|public
name|StandbyCheckpointer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FSNamesystem
name|ns
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|namesystem
operator|=
name|ns
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|checkpointConf
operator|=
operator|new
name|CheckpointConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|thread
operator|=
operator|new
name|CheckpointerThread
argument_list|()
expr_stmt|;
name|this
operator|.
name|uploadThreadFactory
operator|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"TransferFsImageUpload-%d"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|setNameNodeAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|checkpointReceivers
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|URL
name|address
range|:
name|activeNNAddresses
control|)
block|{
name|this
operator|.
name|checkpointReceivers
operator|.
name|put
argument_list|(
name|address
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|CheckpointReceiverEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CheckpointReceiverEntry
specifier|private
specifier|static
specifier|final
class|class
name|CheckpointReceiverEntry
block|{
DECL|field|lastUploadTime
specifier|private
name|long
name|lastUploadTime
decl_stmt|;
DECL|field|isPrimary
specifier|private
name|boolean
name|isPrimary
decl_stmt|;
DECL|method|CheckpointReceiverEntry ()
name|CheckpointReceiverEntry
parameter_list|()
block|{
name|this
operator|.
name|lastUploadTime
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|isPrimary
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|setLastUploadTime (long lastUploadTime)
name|void
name|setLastUploadTime
parameter_list|(
name|long
name|lastUploadTime
parameter_list|)
block|{
name|this
operator|.
name|lastUploadTime
operator|=
name|lastUploadTime
expr_stmt|;
block|}
DECL|method|setIsPrimary (boolean isPrimaryFor)
name|void
name|setIsPrimary
parameter_list|(
name|boolean
name|isPrimaryFor
parameter_list|)
block|{
name|this
operator|.
name|isPrimary
operator|=
name|isPrimaryFor
expr_stmt|;
block|}
DECL|method|getLastUploadTime ()
name|long
name|getLastUploadTime
parameter_list|()
block|{
return|return
name|lastUploadTime
return|;
block|}
DECL|method|isPrimary ()
name|boolean
name|isPrimary
parameter_list|()
block|{
return|return
name|isPrimary
return|;
block|}
block|}
comment|/**    * Determine the address of the NN we are checkpointing    * as well as our own HTTP address from the configuration.    * @throws IOException     */
DECL|method|setNameNodeAddresses (Configuration conf)
specifier|private
name|void
name|setNameNodeAddresses
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Look up our own address.
name|myNNAddress
operator|=
name|getHttpAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Look up the active node's address
name|List
argument_list|<
name|Configuration
argument_list|>
name|confForActive
init|=
name|HAUtil
operator|.
name|getConfForOtherNodes
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|activeNNAddresses
operator|=
operator|new
name|ArrayList
argument_list|<
name|URL
argument_list|>
argument_list|(
name|confForActive
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Configuration
name|activeConf
range|:
name|confForActive
control|)
block|{
name|URL
name|activeNNAddress
init|=
name|getHttpAddress
argument_list|(
name|activeConf
argument_list|)
decl_stmt|;
comment|// sanity check each possible active NN
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|checkAddress
argument_list|(
name|activeNNAddress
argument_list|)
argument_list|,
literal|"Bad address for active NN: %s"
argument_list|,
name|activeNNAddress
argument_list|)
expr_stmt|;
name|activeNNAddresses
operator|.
name|add
argument_list|(
name|activeNNAddress
argument_list|)
expr_stmt|;
block|}
comment|// Sanity-check.
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|checkAddress
argument_list|(
name|myNNAddress
argument_list|)
argument_list|,
literal|"Bad address for standby NN: %s"
argument_list|,
name|myNNAddress
argument_list|)
expr_stmt|;
block|}
DECL|method|getHttpAddress (Configuration conf)
specifier|private
name|URL
name|getHttpAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|scheme
init|=
name|DFSUtil
operator|.
name|getHttpClientScheme
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|defaultHost
init|=
name|NameNode
operator|.
name|getServiceAddress
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
operator|.
name|getHostName
argument_list|()
decl_stmt|;
name|URI
name|addr
init|=
name|DFSUtil
operator|.
name|getInfoServerWithDefaultHost
argument_list|(
name|defaultHost
argument_list|,
name|conf
argument_list|,
name|scheme
argument_list|)
decl_stmt|;
return|return
name|addr
operator|.
name|toURL
argument_list|()
return|;
block|}
comment|/**    * Ensure that the given address is valid and has a port    * specified.    */
DECL|method|checkAddress (URL addr)
specifier|private
specifier|static
name|boolean
name|checkAddress
parameter_list|(
name|URL
name|addr
parameter_list|)
block|{
return|return
name|addr
operator|.
name|getPort
argument_list|()
operator|!=
literal|0
return|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting standby checkpoint thread...\n"
operator|+
literal|"Checkpointing active NN to possible NNs: {}\n"
operator|+
literal|"Serving checkpoints at {}"
argument_list|,
name|activeNNAddresses
argument_list|,
name|myNNAddress
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|cancelAndPreventCheckpoints
argument_list|(
literal|"Stopping checkpointer"
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setShouldRun
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|thread
operator|.
name|join
argument_list|()
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
literal|"Edit log tailer thread exited with an exception"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|triggerRollbackCheckpoint ()
specifier|public
name|void
name|triggerRollbackCheckpoint
parameter_list|()
block|{
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
DECL|method|doCheckpoint ()
specifier|private
name|void
name|doCheckpoint
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
assert|assert
name|canceler
operator|!=
literal|null
assert|;
specifier|final
name|long
name|txid
decl_stmt|;
specifier|final
name|NameNodeFile
name|imageType
decl_stmt|;
comment|// Acquire cpLock to make sure no one is modifying the name system.
comment|// It does not need the full namesystem write lock, since the only thing
comment|// that modifies namesystem on standby node is edit log replaying.
name|namesystem
operator|.
name|cpLockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
assert|assert
name|namesystem
operator|.
name|getEditLog
argument_list|()
operator|.
name|isOpenForRead
argument_list|()
operator|:
literal|"Standby Checkpointer should only attempt a checkpoint when "
operator|+
literal|"NN is in standby mode, but the edit logs are in an unexpected state"
assert|;
name|FSImage
name|img
init|=
name|namesystem
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
name|long
name|prevCheckpointTxId
init|=
name|img
operator|.
name|getStorage
argument_list|()
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
decl_stmt|;
name|long
name|thisCheckpointTxId
init|=
name|img
operator|.
name|getCorrectLastAppliedOrWrittenTxId
argument_list|()
decl_stmt|;
assert|assert
name|thisCheckpointTxId
operator|>=
name|prevCheckpointTxId
assert|;
if|if
condition|(
name|thisCheckpointTxId
operator|==
name|prevCheckpointTxId
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"A checkpoint was triggered but the Standby Node has not "
operator|+
literal|"received any transactions since the last checkpoint at txid {}. "
operator|+
literal|"Skipping..."
argument_list|,
name|thisCheckpointTxId
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|namesystem
operator|.
name|isRollingUpgrade
argument_list|()
operator|&&
operator|!
name|namesystem
operator|.
name|getFSImage
argument_list|()
operator|.
name|hasRollbackFSImage
argument_list|()
condition|)
block|{
comment|// if we will do rolling upgrade but have not created the rollback image
comment|// yet, name this checkpoint as fsimage_rollback
name|imageType
operator|=
name|NameNodeFile
operator|.
name|IMAGE_ROLLBACK
expr_stmt|;
block|}
else|else
block|{
name|imageType
operator|=
name|NameNodeFile
operator|.
name|IMAGE
expr_stmt|;
block|}
name|img
operator|.
name|saveNamespace
argument_list|(
name|namesystem
argument_list|,
name|imageType
argument_list|,
name|canceler
argument_list|)
expr_stmt|;
name|txid
operator|=
name|img
operator|.
name|getStorage
argument_list|()
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
expr_stmt|;
assert|assert
name|txid
operator|==
name|thisCheckpointTxId
operator|:
literal|"expected to save checkpoint at txid="
operator|+
name|thisCheckpointTxId
operator|+
literal|" but instead saved at txid="
operator|+
name|txid
assert|;
comment|// Save the legacy OIV image, if the output dir is defined.
name|String
name|outputDir
init|=
name|checkpointConf
operator|.
name|getLegacyOivImageDir
argument_list|()
decl_stmt|;
if|if
condition|(
name|outputDir
operator|!=
literal|null
operator|&&
operator|!
name|outputDir
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|img
operator|.
name|saveLegacyOIVImage
argument_list|(
name|namesystem
argument_list|,
name|outputDir
argument_list|,
name|canceler
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception encountered while saving legacy OIV image; "
operator|+
literal|"continuing with other checkpointing steps"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|namesystem
operator|.
name|cpUnlock
argument_list|()
expr_stmt|;
block|}
comment|// Upload the saved checkpoint back to the active
comment|// Do this in a separate thread to avoid blocking transition to active, but don't allow more
comment|// than the expected number of tasks to run or queue up
comment|// See HDFS-4816
name|ExecutorService
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|activeNNAddresses
operator|.
name|size
argument_list|()
argument_list|,
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|activeNNAddresses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|uploadThreadFactory
argument_list|)
decl_stmt|;
comment|// for right now, just match the upload to the nn address by convention. There is no need to
comment|// directly tie them together by adding a pair class.
name|HashMap
argument_list|<
name|String
argument_list|,
name|Future
argument_list|<
name|TransferFsImage
operator|.
name|TransferResult
argument_list|>
argument_list|>
name|uploads
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|URL
name|activeNNAddress
range|:
name|activeNNAddresses
control|)
block|{
comment|// Upload image if at least 1 of 2 following conditions met:
comment|// 1. has been quiet for long enough, try to contact the node.
comment|// 2. this standby IS the primary checkpointer of target NN.
name|String
name|addressString
init|=
name|activeNNAddress
operator|.
name|toString
argument_list|()
decl_stmt|;
assert|assert
name|checkpointReceivers
operator|.
name|containsKey
argument_list|(
name|addressString
argument_list|)
assert|;
name|CheckpointReceiverEntry
name|receiverEntry
init|=
name|checkpointReceivers
operator|.
name|get
argument_list|(
name|addressString
argument_list|)
decl_stmt|;
name|long
name|secsSinceLastUpload
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toSeconds
argument_list|(
name|monotonicNow
argument_list|()
operator|-
name|receiverEntry
operator|.
name|getLastUploadTime
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|shouldUpload
init|=
name|receiverEntry
operator|.
name|isPrimary
argument_list|()
operator|||
name|secsSinceLastUpload
operator|>=
name|checkpointConf
operator|.
name|getQuietPeriod
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldUpload
condition|)
block|{
name|Future
argument_list|<
name|TransferFsImage
operator|.
name|TransferResult
argument_list|>
name|upload
init|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|TransferFsImage
operator|.
name|TransferResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TransferFsImage
operator|.
name|TransferResult
name|call
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|CheckpointFaultInjector
operator|.
name|getInstance
argument_list|()
operator|.
name|duringUploadInProgess
argument_list|()
expr_stmt|;
return|return
name|TransferFsImage
operator|.
name|uploadImageFromStorage
argument_list|(
name|activeNNAddress
argument_list|,
name|conf
argument_list|,
name|namesystem
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
argument_list|,
name|imageType
argument_list|,
name|txid
argument_list|,
name|canceler
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|uploads
operator|.
name|put
argument_list|(
name|addressString
argument_list|,
name|upload
argument_list|)
expr_stmt|;
block|}
block|}
name|InterruptedException
name|ie
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|IOException
argument_list|>
name|ioes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Future
argument_list|<
name|TransferFsImage
operator|.
name|TransferResult
argument_list|>
argument_list|>
name|entry
range|:
name|uploads
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|url
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|TransferFsImage
operator|.
name|TransferResult
argument_list|>
name|upload
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
comment|// TODO should there be some smarts here about retries nodes that
comment|//  are not the active NN?
name|CheckpointReceiverEntry
name|receiverEntry
init|=
name|checkpointReceivers
operator|.
name|get
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|TransferFsImage
operator|.
name|TransferResult
name|uploadResult
init|=
name|upload
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|uploadResult
operator|==
name|TransferFsImage
operator|.
name|TransferResult
operator|.
name|SUCCESS
condition|)
block|{
name|receiverEntry
operator|.
name|setLastUploadTime
argument_list|(
name|monotonicNow
argument_list|()
argument_list|)
expr_stmt|;
name|receiverEntry
operator|.
name|setIsPrimary
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Getting here means image upload is explicitly rejected
comment|// by the other node. This could happen if:
comment|// 1. the other is also a standby, or
comment|// 2. the other is active, but already accepted another
comment|// newer image, or
comment|// 3. the other is active but has a recent enough image.
comment|// All these are valid cases, just log for information.
name|LOG
operator|.
name|info
argument_list|(
literal|"Image upload rejected by the other NameNode: {}"
argument_list|,
name|uploadResult
argument_list|)
expr_stmt|;
name|receiverEntry
operator|.
name|setIsPrimary
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// Even if exception happens, still proceeds to next NN url.
comment|// so that fail to upload to previous NN does not cause the
comment|// remaining NN not getting the fsImage.
name|ioes
operator|.
name|add
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Exception during image upload"
argument_list|,
name|e
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
name|ie
operator|=
name|e
expr_stmt|;
break|break;
block|}
block|}
comment|// cleaner than copying code for multiple catch statements and better than catching all
comment|// exceptions, so we just handle the ones we expect.
if|if
condition|(
name|ie
operator|!=
literal|null
condition|)
block|{
comment|// cancel the rest of the tasks, and close the pool
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Future
argument_list|<
name|TransferFsImage
operator|.
name|TransferResult
argument_list|>
argument_list|>
name|entry
range|:
name|uploads
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Future
argument_list|<
name|TransferFsImage
operator|.
name|TransferResult
argument_list|>
name|upload
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// The background thread may be blocked waiting in the throttler, so
comment|// interrupt it.
name|upload
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// shutdown so we interrupt anything running and don't start anything new
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
comment|// this is a good bit longer than the thread timeout, just to make sure all the threads
comment|// that are not doing any work also stop
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|500
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// re-throw the exception we got, since one of these two must be non-null
throw|throw
name|ie
throw|;
block|}
if|if
condition|(
operator|!
name|ioes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|MultipleIOException
operator|.
name|createIOException
argument_list|(
name|ioes
argument_list|)
throw|;
block|}
block|}
comment|/**    * Cancel any checkpoint that's currently being made,    * and prevent any new checkpoints from starting for the next    * minute or so.    */
DECL|method|cancelAndPreventCheckpoints (String msg)
specifier|public
name|void
name|cancelAndPreventCheckpoints
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|ServiceFailedException
block|{
synchronized|synchronized
init|(
name|cancelLock
init|)
block|{
comment|// The checkpointer thread takes this lock and checks if checkpointing is
comment|// postponed.
name|thread
operator|.
name|preventCheckpointsFor
argument_list|(
name|PREVENT_AFTER_CANCEL_MS
argument_list|)
expr_stmt|;
comment|// Before beginning a checkpoint, the checkpointer thread
comment|// takes this lock, and creates a canceler object.
comment|// If the canceler is non-null, then a checkpoint is in
comment|// progress and we need to cancel it. If it's null, then
comment|// the operation has not started, meaning that the above
comment|// time-based prevention will take effect.
if|if
condition|(
name|canceler
operator|!=
literal|null
condition|)
block|{
name|canceler
operator|.
name|cancel
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCanceledCount ()
specifier|static
name|int
name|getCanceledCount
parameter_list|()
block|{
return|return
name|canceledCount
return|;
block|}
DECL|method|countUncheckpointedTxns ()
specifier|private
name|long
name|countUncheckpointedTxns
parameter_list|()
block|{
name|FSImage
name|img
init|=
name|namesystem
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
return|return
name|img
operator|.
name|getCorrectLastAppliedOrWrittenTxId
argument_list|()
operator|-
name|img
operator|.
name|getStorage
argument_list|()
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
return|;
block|}
DECL|class|CheckpointerThread
specifier|private
class|class
name|CheckpointerThread
extends|extends
name|Thread
block|{
DECL|field|shouldRun
specifier|private
specifier|volatile
name|boolean
name|shouldRun
init|=
literal|true
decl_stmt|;
DECL|field|preventCheckpointsUntil
specifier|private
specifier|volatile
name|long
name|preventCheckpointsUntil
init|=
literal|0
decl_stmt|;
DECL|method|CheckpointerThread ()
specifier|private
name|CheckpointerThread
parameter_list|()
block|{
name|super
argument_list|(
literal|"Standby State Checkpointer"
argument_list|)
expr_stmt|;
block|}
DECL|method|setShouldRun (boolean shouldRun)
specifier|private
name|void
name|setShouldRun
parameter_list|(
name|boolean
name|shouldRun
parameter_list|)
block|{
name|this
operator|.
name|shouldRun
operator|=
name|shouldRun
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
comment|// We have to make sure we're logged in as far as JAAS
comment|// is concerned, in order to use kerberized SSL properly.
name|SecurityUtil
operator|.
name|doAsLoginUserOrFatal
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|doWork
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Prevent checkpoints from occurring for some time period      * in the future. This is used when preparing to enter active      * mode. We need to not only cancel any concurrent checkpoint,      * but also prevent any checkpoints from racing to start just      * after the cancel call.      *       * @param delayMs the number of MS for which checkpoints will be      * prevented      */
DECL|method|preventCheckpointsFor (long delayMs)
specifier|private
name|void
name|preventCheckpointsFor
parameter_list|(
name|long
name|delayMs
parameter_list|)
block|{
name|preventCheckpointsUntil
operator|=
name|monotonicNow
argument_list|()
operator|+
name|delayMs
expr_stmt|;
block|}
DECL|method|doWork ()
specifier|private
name|void
name|doWork
parameter_list|()
block|{
specifier|final
name|long
name|checkPeriod
init|=
literal|1000
operator|*
name|checkpointConf
operator|.
name|getCheckPeriod
argument_list|()
decl_stmt|;
comment|// Reset checkpoint time so that we don't always checkpoint
comment|// on startup.
name|lastCheckpointTime
operator|=
name|monotonicNow
argument_list|()
expr_stmt|;
while|while
condition|(
name|shouldRun
condition|)
block|{
name|boolean
name|needRollbackCheckpoint
init|=
name|namesystem
operator|.
name|isNeedRollbackFsImage
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|needRollbackCheckpoint
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|checkPeriod
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{           }
if|if
condition|(
operator|!
name|shouldRun
condition|)
block|{
break|break;
block|}
block|}
try|try
block|{
comment|// We may have lost our ticket since last checkpoint, log in again, just in case
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|checkTGTAndReloginFromKeytab
argument_list|()
expr_stmt|;
block|}
specifier|final
name|long
name|now
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
specifier|final
name|long
name|uncheckpointed
init|=
name|countUncheckpointedTxns
argument_list|()
decl_stmt|;
specifier|final
name|long
name|secsSinceLast
init|=
operator|(
name|now
operator|-
name|lastCheckpointTime
operator|)
operator|/
literal|1000
decl_stmt|;
comment|// if we need a rollback checkpoint, always attempt to checkpoint
name|boolean
name|needCheckpoint
init|=
name|needRollbackCheckpoint
decl_stmt|;
if|if
condition|(
name|needCheckpoint
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Triggering a rollback fsimage for rolling upgrade."
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|uncheckpointed
operator|>=
name|checkpointConf
operator|.
name|getTxnCount
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Triggering checkpoint because there have been {} txns "
operator|+
literal|"since the last checkpoint, "
operator|+
literal|"which exceeds the configured threshold {}"
argument_list|,
name|uncheckpointed
argument_list|,
name|checkpointConf
operator|.
name|getTxnCount
argument_list|()
argument_list|)
expr_stmt|;
name|needCheckpoint
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|secsSinceLast
operator|>=
name|checkpointConf
operator|.
name|getPeriod
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Triggering checkpoint because it has been {} seconds "
operator|+
literal|"since the last checkpoint, which exceeds the configured "
operator|+
literal|"interval {}"
argument_list|,
name|secsSinceLast
argument_list|,
name|checkpointConf
operator|.
name|getPeriod
argument_list|()
argument_list|)
expr_stmt|;
name|needCheckpoint
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|needCheckpoint
condition|)
block|{
synchronized|synchronized
init|(
name|cancelLock
init|)
block|{
if|if
condition|(
name|now
operator|<
name|preventCheckpointsUntil
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"But skipping this checkpoint since we are about to failover!"
argument_list|)
expr_stmt|;
name|canceledCount
operator|++
expr_stmt|;
continue|continue;
block|}
assert|assert
name|canceler
operator|==
literal|null
assert|;
name|canceler
operator|=
operator|new
name|Canceler
argument_list|()
expr_stmt|;
block|}
comment|// on all nodes, we build the checkpoint. However, we only ship the checkpoint if have a
comment|// rollback request, are the checkpointer, are outside the quiet period.
name|doCheckpoint
argument_list|()
expr_stmt|;
comment|// reset needRollbackCheckpoint to false only when we finish a ckpt
comment|// for rollback image
if|if
condition|(
name|needRollbackCheckpoint
operator|&&
name|namesystem
operator|.
name|getFSImage
argument_list|()
operator|.
name|hasRollbackFSImage
argument_list|()
condition|)
block|{
name|namesystem
operator|.
name|setCreatedRollbackImages
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|setNeedRollbackFsImage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|lastCheckpointTime
operator|=
name|now
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Checkpoint finished successfully."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SaveNamespaceCancelledException
name|ce
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checkpoint was cancelled: {}"
argument_list|,
name|ce
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|canceledCount
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Interrupted during checkpointing"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
comment|// Probably requested shutdown.
continue|continue;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in doCheckpoint"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|cancelLock
init|)
block|{
name|canceler
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getActiveNNAddresses ()
name|List
argument_list|<
name|URL
argument_list|>
name|getActiveNNAddresses
parameter_list|()
block|{
return|return
name|activeNNAddresses
return|;
block|}
block|}
end_class

end_unit

