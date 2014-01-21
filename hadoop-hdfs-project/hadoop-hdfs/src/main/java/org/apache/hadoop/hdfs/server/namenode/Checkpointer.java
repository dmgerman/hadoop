begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_DEFAULT
import|;
end_import

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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY
import|;
end_import

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
name|File
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
name|net
operator|.
name|MalformedURLException
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
name|util
operator|.
name|List
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
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|NamenodeRole
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
name|CheckpointCommand
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
name|NamenodeCommand
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
name|NamenodeProtocol
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
name|RemoteEditLog
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
name|RemoteEditLogManifest
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
name|MD5Hash
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * The Checkpointer is responsible for supporting periodic checkpoints   * of the HDFS metadata.  *  * The Checkpointer is a daemon that periodically wakes up  * up (determined by the schedule specified in the configuration),  * triggers a periodic checkpoint and then goes back to sleep.  *   * The start of a checkpoint is triggered by one of the two factors:  * (1) time or (2) the size of the edits file.  */
end_comment

begin_class
DECL|class|Checkpointer
class|class
name|Checkpointer
extends|extends
name|Daemon
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Checkpointer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|backupNode
specifier|private
name|BackupNode
name|backupNode
decl_stmt|;
DECL|field|shouldRun
specifier|volatile
name|boolean
name|shouldRun
decl_stmt|;
DECL|field|infoBindAddress
specifier|private
name|String
name|infoBindAddress
decl_stmt|;
DECL|field|checkpointConf
specifier|private
name|CheckpointConf
name|checkpointConf
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|method|getFSImage ()
specifier|private
name|BackupImage
name|getFSImage
parameter_list|()
block|{
return|return
operator|(
name|BackupImage
operator|)
name|backupNode
operator|.
name|getFSImage
argument_list|()
return|;
block|}
DECL|method|getRemoteNamenodeProxy ()
specifier|private
name|NamenodeProtocol
name|getRemoteNamenodeProxy
parameter_list|()
block|{
return|return
name|backupNode
operator|.
name|namenode
return|;
block|}
comment|/**    * Create a connection to the primary namenode.    */
DECL|method|Checkpointer (Configuration conf, BackupNode bnNode)
name|Checkpointer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|BackupNode
name|bnNode
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|backupNode
operator|=
name|bnNode
expr_stmt|;
try|try
block|{
name|initialize
argument_list|(
name|conf
argument_list|)
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
literal|"Checkpointer got exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|shutdown
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Initialize checkpoint.    */
DECL|method|initialize (Configuration conf)
specifier|private
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Create connection to the namenode.
name|shouldRun
operator|=
literal|true
expr_stmt|;
comment|// Initialize other scheduling parameters from the configuration
name|checkpointConf
operator|=
operator|new
name|CheckpointConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Pull out exact http address for posting url to avoid ip aliasing issues
name|String
name|fullInfoAddr
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY
argument_list|,
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
name|infoBindAddress
operator|=
name|fullInfoAddr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|fullInfoAddr
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Checkpoint Period : "
operator|+
name|checkpointConf
operator|.
name|getPeriod
argument_list|()
operator|+
literal|" secs "
operator|+
literal|"("
operator|+
name|checkpointConf
operator|.
name|getPeriod
argument_list|()
operator|/
literal|60
operator|+
literal|" min)"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Transactions count is  : "
operator|+
name|checkpointConf
operator|.
name|getTxnCount
argument_list|()
operator|+
literal|", to trigger checkpoint"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shut down the checkpointer.    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
block|{
name|shouldRun
operator|=
literal|false
expr_stmt|;
name|backupNode
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|//
comment|// The main work loop
comment|//
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// Check the size of the edit log once every 5 minutes.
name|long
name|periodMSec
init|=
literal|5
operator|*
literal|60
decl_stmt|;
comment|// 5 minutes
if|if
condition|(
name|checkpointConf
operator|.
name|getPeriod
argument_list|()
operator|<
name|periodMSec
condition|)
block|{
name|periodMSec
operator|=
name|checkpointConf
operator|.
name|getPeriod
argument_list|()
expr_stmt|;
block|}
name|periodMSec
operator|*=
literal|1000
expr_stmt|;
name|long
name|lastCheckpointTime
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|backupNode
operator|.
name|shouldCheckpointAtStartup
argument_list|()
condition|)
block|{
name|lastCheckpointTime
operator|=
name|now
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|shouldRun
condition|)
block|{
try|try
block|{
name|long
name|now
init|=
name|now
argument_list|()
decl_stmt|;
name|boolean
name|shouldCheckpoint
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|now
operator|>=
name|lastCheckpointTime
operator|+
name|periodMSec
condition|)
block|{
name|shouldCheckpoint
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|long
name|txns
init|=
name|countUncheckpointedTxns
argument_list|()
decl_stmt|;
if|if
condition|(
name|txns
operator|>=
name|checkpointConf
operator|.
name|getTxnCount
argument_list|()
condition|)
name|shouldCheckpoint
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|shouldCheckpoint
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
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in doCheckpoint: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Throwable Exception in doCheckpoint: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|shutdown
argument_list|()
expr_stmt|;
break|break;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|periodMSec
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
DECL|method|countUncheckpointedTxns ()
specifier|private
name|long
name|countUncheckpointedTxns
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|curTxId
init|=
name|getRemoteNamenodeProxy
argument_list|()
operator|.
name|getTransactionID
argument_list|()
decl_stmt|;
name|long
name|uncheckpointedTxns
init|=
name|curTxId
operator|-
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
decl_stmt|;
assert|assert
name|uncheckpointedTxns
operator|>=
literal|0
assert|;
return|return
name|uncheckpointedTxns
return|;
block|}
comment|/**    * Create a new checkpoint    */
DECL|method|doCheckpoint ()
name|void
name|doCheckpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|BackupImage
name|bnImage
init|=
name|getFSImage
argument_list|()
decl_stmt|;
name|NNStorage
name|bnStorage
init|=
name|bnImage
operator|.
name|getStorage
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
name|now
argument_list|()
decl_stmt|;
name|bnImage
operator|.
name|freezeNamespaceAtNextRoll
argument_list|()
expr_stmt|;
name|NamenodeCommand
name|cmd
init|=
name|getRemoteNamenodeProxy
argument_list|()
operator|.
name|startCheckpoint
argument_list|(
name|backupNode
operator|.
name|getRegistration
argument_list|()
argument_list|)
decl_stmt|;
name|CheckpointCommand
name|cpCmd
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|cmd
operator|.
name|getAction
argument_list|()
condition|)
block|{
case|case
name|NamenodeProtocol
operator|.
name|ACT_SHUTDOWN
case|:
name|shutdown
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Name-node "
operator|+
name|backupNode
operator|.
name|nnRpcAddress
operator|+
literal|" requested shutdown."
argument_list|)
throw|;
case|case
name|NamenodeProtocol
operator|.
name|ACT_CHECKPOINT
case|:
name|cpCmd
operator|=
operator|(
name|CheckpointCommand
operator|)
name|cmd
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported NamenodeCommand: "
operator|+
name|cmd
operator|.
name|getAction
argument_list|()
argument_list|)
throw|;
block|}
name|bnImage
operator|.
name|waitUntilNamespaceFrozen
argument_list|()
expr_stmt|;
name|CheckpointSignature
name|sig
init|=
name|cpCmd
operator|.
name|getSignature
argument_list|()
decl_stmt|;
comment|// Make sure we're talking to the same NN!
name|sig
operator|.
name|validateStorageInfo
argument_list|(
name|bnImage
argument_list|)
expr_stmt|;
name|long
name|lastApplied
init|=
name|bnImage
operator|.
name|getLastAppliedTxId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Doing checkpoint. Last applied: "
operator|+
name|lastApplied
argument_list|)
expr_stmt|;
name|RemoteEditLogManifest
name|manifest
init|=
name|getRemoteNamenodeProxy
argument_list|()
operator|.
name|getEditLogManifest
argument_list|(
name|bnImage
operator|.
name|getLastAppliedTxId
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|boolean
name|needReloadImage
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|manifest
operator|.
name|getLogs
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|RemoteEditLog
name|firstRemoteLog
init|=
name|manifest
operator|.
name|getLogs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// we don't have enough logs to roll forward using only logs. Need
comment|// to download and load the image.
if|if
condition|(
name|firstRemoteLog
operator|.
name|getStartTxId
argument_list|()
operator|>
name|lastApplied
operator|+
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unable to roll forward using only logs. Downloading "
operator|+
literal|"image with txid "
operator|+
name|sig
operator|.
name|mostRecentCheckpointTxId
argument_list|)
expr_stmt|;
name|MD5Hash
name|downloadedHash
init|=
name|TransferFsImage
operator|.
name|downloadImageToStorage
argument_list|(
name|backupNode
operator|.
name|nnHttpAddress
argument_list|,
name|sig
operator|.
name|mostRecentCheckpointTxId
argument_list|,
name|bnStorage
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|bnImage
operator|.
name|saveDigestAndRenameCheckpointImage
argument_list|(
name|sig
operator|.
name|mostRecentCheckpointTxId
argument_list|,
name|downloadedHash
argument_list|)
expr_stmt|;
name|lastApplied
operator|=
name|sig
operator|.
name|mostRecentCheckpointTxId
expr_stmt|;
name|needReloadImage
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|firstRemoteLog
operator|.
name|getStartTxId
argument_list|()
operator|>
name|lastApplied
operator|+
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No logs to roll forward from "
operator|+
name|lastApplied
argument_list|)
throw|;
block|}
comment|// get edits files
for|for
control|(
name|RemoteEditLog
name|log
range|:
name|manifest
operator|.
name|getLogs
argument_list|()
control|)
block|{
name|TransferFsImage
operator|.
name|downloadEditsToStorage
argument_list|(
name|backupNode
operator|.
name|nnHttpAddress
argument_list|,
name|log
argument_list|,
name|bnStorage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|needReloadImage
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading image with txid "
operator|+
name|sig
operator|.
name|mostRecentCheckpointTxId
argument_list|)
expr_stmt|;
name|File
name|file
init|=
name|bnStorage
operator|.
name|findImageFile
argument_list|(
name|sig
operator|.
name|mostRecentCheckpointTxId
argument_list|)
decl_stmt|;
name|bnImage
operator|.
name|reloadFromImageFile
argument_list|(
name|file
argument_list|,
name|backupNode
operator|.
name|getNamesystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rollForwardByApplyingLogs
argument_list|(
name|manifest
argument_list|,
name|bnImage
argument_list|,
name|backupNode
operator|.
name|getNamesystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|txid
init|=
name|bnImage
operator|.
name|getLastAppliedTxId
argument_list|()
decl_stmt|;
name|backupNode
operator|.
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|backupNode
operator|.
name|namesystem
operator|.
name|dir
operator|.
name|setReady
argument_list|()
expr_stmt|;
if|if
condition|(
name|backupNode
operator|.
name|namesystem
operator|.
name|getBlocksTotal
argument_list|()
operator|>
literal|0
condition|)
block|{
name|backupNode
operator|.
name|namesystem
operator|.
name|setBlockTotal
argument_list|()
expr_stmt|;
block|}
name|bnImage
operator|.
name|saveFSImageInAllDirs
argument_list|(
name|backupNode
operator|.
name|getNamesystem
argument_list|()
argument_list|,
name|txid
argument_list|)
expr_stmt|;
name|bnStorage
operator|.
name|writeAll
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|backupNode
operator|.
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cpCmd
operator|.
name|needToReturnImage
argument_list|()
condition|)
block|{
name|TransferFsImage
operator|.
name|uploadImageFromStorage
argument_list|(
name|backupNode
operator|.
name|nnHttpAddress
argument_list|,
name|getImageListenAddress
argument_list|()
argument_list|,
name|bnStorage
argument_list|,
name|txid
argument_list|)
expr_stmt|;
block|}
name|getRemoteNamenodeProxy
argument_list|()
operator|.
name|endCheckpoint
argument_list|(
name|backupNode
operator|.
name|getRegistration
argument_list|()
argument_list|,
name|sig
argument_list|)
expr_stmt|;
if|if
condition|(
name|backupNode
operator|.
name|getRole
argument_list|()
operator|==
name|NamenodeRole
operator|.
name|BACKUP
condition|)
block|{
name|bnImage
operator|.
name|convergeJournalSpool
argument_list|()
expr_stmt|;
block|}
name|backupNode
operator|.
name|setRegistration
argument_list|()
expr_stmt|;
comment|// keep registration up to date
name|long
name|imageSize
init|=
name|bnImage
operator|.
name|getStorage
argument_list|()
operator|.
name|getFsImageName
argument_list|(
name|txid
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Checkpoint completed in "
operator|+
operator|(
name|now
argument_list|()
operator|-
name|startTime
operator|)
operator|/
literal|1000
operator|+
literal|" seconds."
operator|+
literal|" New Image Size: "
operator|+
name|imageSize
argument_list|)
expr_stmt|;
block|}
DECL|method|getImageListenAddress ()
specifier|private
name|URL
name|getImageListenAddress
parameter_list|()
block|{
name|InetSocketAddress
name|httpSocAddr
init|=
name|backupNode
operator|.
name|getHttpAddress
argument_list|()
decl_stmt|;
name|int
name|httpPort
init|=
name|httpSocAddr
operator|.
name|getPort
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|URL
argument_list|(
name|DFSUtil
operator|.
name|getHttpClientScheme
argument_list|(
name|conf
argument_list|)
operator|+
literal|"://"
operator|+
name|infoBindAddress
operator|+
literal|":"
operator|+
name|httpPort
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
comment|// Unreachable
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|rollForwardByApplyingLogs ( RemoteEditLogManifest manifest, FSImage dstImage, FSNamesystem dstNamesystem)
specifier|static
name|void
name|rollForwardByApplyingLogs
parameter_list|(
name|RemoteEditLogManifest
name|manifest
parameter_list|,
name|FSImage
name|dstImage
parameter_list|,
name|FSNamesystem
name|dstNamesystem
parameter_list|)
throws|throws
name|IOException
block|{
name|NNStorage
name|dstStorage
init|=
name|dstImage
operator|.
name|getStorage
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|EditLogInputStream
argument_list|>
name|editsStreams
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|RemoteEditLog
name|log
range|:
name|manifest
operator|.
name|getLogs
argument_list|()
control|)
block|{
if|if
condition|(
name|log
operator|.
name|getEndTxId
argument_list|()
operator|>
name|dstImage
operator|.
name|getLastAppliedTxId
argument_list|()
condition|)
block|{
name|File
name|f
init|=
name|dstStorage
operator|.
name|findFinalizedEditsFile
argument_list|(
name|log
operator|.
name|getStartTxId
argument_list|()
argument_list|,
name|log
operator|.
name|getEndTxId
argument_list|()
argument_list|)
decl_stmt|;
name|editsStreams
operator|.
name|add
argument_list|(
operator|new
name|EditLogFileInputStream
argument_list|(
name|f
argument_list|,
name|log
operator|.
name|getStartTxId
argument_list|()
argument_list|,
name|log
operator|.
name|getEndTxId
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Checkpointer about to load edits from "
operator|+
name|editsStreams
operator|.
name|size
argument_list|()
operator|+
literal|" stream(s)."
argument_list|)
expr_stmt|;
name|dstImage
operator|.
name|loadEdits
argument_list|(
name|editsStreams
argument_list|,
name|dstNamesystem
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

