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
name|now
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
name|InetSocketAddress
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
name|Executors
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
name|ThreadFactory
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
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
DECL|field|activeNNAddress
specifier|private
name|String
name|activeNNAddress
decl_stmt|;
DECL|field|myNNAddress
specifier|private
name|InetSocketAddress
name|myNNAddress
decl_stmt|;
DECL|field|cancelLock
specifier|private
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
name|String
name|myAddrString
init|=
name|getHttpAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Look up the active node's address
name|Configuration
name|confForActive
init|=
name|HAUtil
operator|.
name|getConfForOtherNode
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|activeNNAddress
operator|=
name|getHttpAddress
argument_list|(
name|confForActive
argument_list|)
expr_stmt|;
comment|// Sanity-check.
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
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|checkAddress
argument_list|(
name|myAddrString
argument_list|)
argument_list|,
literal|"Bad address for standby NN: %s"
argument_list|,
name|myAddrString
argument_list|)
expr_stmt|;
name|myNNAddress
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|myAddrString
argument_list|)
expr_stmt|;
block|}
DECL|method|getHttpAddress (Configuration conf)
specifier|private
name|String
name|getHttpAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|configuredAddr
init|=
name|DFSUtil
operator|.
name|getInfoServer
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Use the hostname from the RPC address as a default, in case
comment|// the HTTP address is configured to 0.0.0.0.
name|String
name|hostnameFromRpc
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
try|try
block|{
return|return
name|DFSUtil
operator|.
name|substituteForWildcardAddress
argument_list|(
name|configuredAddr
argument_list|,
name|hostnameFromRpc
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Ensure that the given address is valid and has a port    * specified.    */
DECL|method|checkAddress (String addrStr)
specifier|private
name|boolean
name|checkAddress
parameter_list|(
name|String
name|addrStr
parameter_list|)
block|{
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addrStr
argument_list|)
decl_stmt|;
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
literal|"Checkpointing active NN at "
operator|+
name|activeNNAddress
operator|+
literal|"\n"
operator|+
literal|"Serving checkpoints at "
operator|+
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
name|namesystem
operator|.
name|writeLockInterruptibly
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
name|getLastAppliedOrWrittenTxId
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
literal|"received any transactions since the last checkpoint at txid "
operator|+
name|thisCheckpointTxId
operator|+
literal|". Skipping..."
argument_list|)
expr_stmt|;
return|return;
block|}
name|img
operator|.
name|saveNamespace
argument_list|(
name|namesystem
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
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
comment|// Upload the saved checkpoint back to the active
comment|// Do this in a separate thread to avoid blocking transition to active
comment|// See HDFS-4816
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|(
name|uploadThreadFactory
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|Void
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
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|IOException
block|{
name|TransferFsImage
operator|.
name|uploadImageFromStorage
argument_list|(
name|activeNNAddress
argument_list|,
name|myNNAddress
argument_list|,
name|namesystem
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
argument_list|,
name|txid
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|upload
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception during image upload: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
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
name|thread
operator|.
name|preventCheckpointsFor
argument_list|(
name|PREVENT_AFTER_CANCEL_MS
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|cancelLock
init|)
block|{
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
name|getLastAppliedOrWrittenTxId
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
name|now
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
comment|// Reset checkpoint time so that we don't always checkpoint
comment|// on startup.
name|lastCheckpointTime
operator|=
name|now
argument_list|()
expr_stmt|;
while|while
condition|(
name|shouldRun
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
operator|*
name|checkpointConf
operator|.
name|getCheckPeriod
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{         }
if|if
condition|(
operator|!
name|shouldRun
condition|)
block|{
break|break;
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
name|long
name|now
init|=
name|now
argument_list|()
decl_stmt|;
name|long
name|uncheckpointed
init|=
name|countUncheckpointedTxns
argument_list|()
decl_stmt|;
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
name|boolean
name|needCheckpoint
init|=
literal|false
decl_stmt|;
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
literal|"Triggering checkpoint because there have been "
operator|+
name|uncheckpointed
operator|+
literal|" txns since the last checkpoint, which "
operator|+
literal|"exceeds the configured threshold "
operator|+
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
literal|"Triggering checkpoint because it has been "
operator|+
name|secsSinceLast
operator|+
literal|" seconds since the last checkpoint, which "
operator|+
literal|"exceeds the configured interval "
operator|+
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
if|if
condition|(
name|needCheckpoint
condition|)
block|{
name|doCheckpoint
argument_list|()
expr_stmt|;
name|lastCheckpointTime
operator|=
name|now
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
literal|"Checkpoint was cancelled: "
operator|+
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
DECL|method|getActiveNNAddress ()
name|String
name|getActiveNNAddress
parameter_list|()
block|{
return|return
name|activeNNAddress
return|;
block|}
block|}
end_class

end_unit

