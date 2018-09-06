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
name|FilenameFilter
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Iterator
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
name|TreeSet
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
name|server
operator|.
name|namenode
operator|.
name|FSImageStorageInspector
operator|.
name|FSImageFile
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
name|FileJournalManager
operator|.
name|EditLogFile
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
name|util
operator|.
name|MD5FileUtils
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
name|collect
operator|.
name|ComparisonChain
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_comment
comment|/**  * The NNStorageRetentionManager is responsible for inspecting the storage  * directories of the NN and enforcing a retention policy on checkpoints  * and edit logs.  *   * It delegates the actual removal of files to a StoragePurger  * implementation, which might delete the files or instead copy them to  * a filer or HDFS for later analysis.  */
end_comment

begin_class
DECL|class|NNStorageRetentionManager
specifier|public
class|class
name|NNStorageRetentionManager
block|{
DECL|field|numCheckpointsToRetain
specifier|private
specifier|final
name|int
name|numCheckpointsToRetain
decl_stmt|;
DECL|field|numExtraEditsToRetain
specifier|private
specifier|final
name|long
name|numExtraEditsToRetain
decl_stmt|;
DECL|field|maxExtraEditsSegmentsToRetain
specifier|private
specifier|final
name|int
name|maxExtraEditsSegmentsToRetain
decl_stmt|;
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
name|NNStorageRetentionManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|storage
specifier|private
specifier|final
name|NNStorage
name|storage
decl_stmt|;
DECL|field|purger
specifier|private
specifier|final
name|StoragePurger
name|purger
decl_stmt|;
DECL|field|purgeableLogs
specifier|private
specifier|final
name|LogsPurgeable
name|purgeableLogs
decl_stmt|;
DECL|method|NNStorageRetentionManager ( Configuration conf, NNStorage storage, LogsPurgeable purgeableLogs, StoragePurger purger)
specifier|public
name|NNStorageRetentionManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NNStorage
name|storage
parameter_list|,
name|LogsPurgeable
name|purgeableLogs
parameter_list|,
name|StoragePurger
name|purger
parameter_list|)
block|{
name|this
operator|.
name|numCheckpointsToRetain
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NUM_CHECKPOINTS_RETAINED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NUM_CHECKPOINTS_RETAINED_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|numExtraEditsToRetain
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxExtraEditsSegmentsToRetain
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_EXTRA_EDITS_SEGMENTS_RETAINED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_EXTRA_EDITS_SEGMENTS_RETAINED_DEFAULT
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|numCheckpointsToRetain
operator|>
literal|0
argument_list|,
literal|"Must retain at least one checkpoint"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|numExtraEditsToRetain
operator|>=
literal|0
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_KEY
operator|+
literal|" must not be negative"
argument_list|)
expr_stmt|;
name|this
operator|.
name|storage
operator|=
name|storage
expr_stmt|;
name|this
operator|.
name|purgeableLogs
operator|=
name|purgeableLogs
expr_stmt|;
name|this
operator|.
name|purger
operator|=
name|purger
expr_stmt|;
block|}
DECL|method|NNStorageRetentionManager (Configuration conf, NNStorage storage, LogsPurgeable purgeableLogs)
specifier|public
name|NNStorageRetentionManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NNStorage
name|storage
parameter_list|,
name|LogsPurgeable
name|purgeableLogs
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|storage
argument_list|,
name|purgeableLogs
argument_list|,
operator|new
name|DeletionStoragePurger
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|purgeCheckpoints (NameNodeFile nnf)
name|void
name|purgeCheckpoints
parameter_list|(
name|NameNodeFile
name|nnf
parameter_list|)
throws|throws
name|IOException
block|{
name|purgeCheckpoinsAfter
argument_list|(
name|nnf
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|purgeCheckpoinsAfter (NameNodeFile nnf, long fromTxId)
name|void
name|purgeCheckpoinsAfter
parameter_list|(
name|NameNodeFile
name|nnf
parameter_list|,
name|long
name|fromTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|FSImageTransactionalStorageInspector
name|inspector
init|=
operator|new
name|FSImageTransactionalStorageInspector
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|nnf
argument_list|)
argument_list|)
decl_stmt|;
name|storage
operator|.
name|inspectStorageDirs
argument_list|(
name|inspector
argument_list|)
expr_stmt|;
for|for
control|(
name|FSImageFile
name|image
range|:
name|inspector
operator|.
name|getFoundImages
argument_list|()
control|)
block|{
if|if
condition|(
name|image
operator|.
name|getCheckpointTxId
argument_list|()
operator|>
name|fromTxId
condition|)
block|{
name|purger
operator|.
name|purgeImage
argument_list|(
name|image
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|purgeOldStorage (NameNodeFile nnf)
name|void
name|purgeOldStorage
parameter_list|(
name|NameNodeFile
name|nnf
parameter_list|)
throws|throws
name|IOException
block|{
name|FSImageTransactionalStorageInspector
name|inspector
init|=
operator|new
name|FSImageTransactionalStorageInspector
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|nnf
argument_list|)
argument_list|)
decl_stmt|;
name|storage
operator|.
name|inspectStorageDirs
argument_list|(
name|inspector
argument_list|)
expr_stmt|;
name|long
name|minImageTxId
init|=
name|getImageTxIdToRetain
argument_list|(
name|inspector
argument_list|)
decl_stmt|;
name|purgeCheckpointsOlderThan
argument_list|(
name|inspector
argument_list|,
name|minImageTxId
argument_list|)
expr_stmt|;
if|if
condition|(
name|nnf
operator|==
name|NameNodeFile
operator|.
name|IMAGE_ROLLBACK
condition|)
block|{
comment|// do not purge edits for IMAGE_ROLLBACK.
return|return;
block|}
comment|// If fsimage_N is the image we want to keep, then we need to keep
comment|// all txns> N. We can remove anything< N+1, since fsimage_N
comment|// reflects the state up to and including N. However, we also
comment|// provide a "cushion" of older txns that we keep, which is
comment|// handy for HA, where a remote node may not have as many
comment|// new images.
comment|//
comment|// First, determine the target number of extra transactions to retain based
comment|// on the configured amount.
name|long
name|minimumRequiredTxId
init|=
name|minImageTxId
operator|+
literal|1
decl_stmt|;
name|long
name|purgeLogsFrom
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|minimumRequiredTxId
operator|-
name|numExtraEditsToRetain
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|EditLogInputStream
argument_list|>
name|editLogs
init|=
operator|new
name|ArrayList
argument_list|<
name|EditLogInputStream
argument_list|>
argument_list|()
decl_stmt|;
name|purgeableLogs
operator|.
name|selectInputStreams
argument_list|(
name|editLogs
argument_list|,
name|purgeLogsFrom
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|editLogs
argument_list|,
operator|new
name|Comparator
argument_list|<
name|EditLogInputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|EditLogInputStream
name|a
parameter_list|,
name|EditLogInputStream
name|b
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compare
argument_list|(
name|a
operator|.
name|getFirstTxId
argument_list|()
argument_list|,
name|b
operator|.
name|getFirstTxId
argument_list|()
argument_list|)
operator|.
name|compare
argument_list|(
name|a
operator|.
name|getLastTxId
argument_list|()
argument_list|,
name|b
operator|.
name|getLastTxId
argument_list|()
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Remove from consideration any edit logs that are in fact required.
while|while
condition|(
name|editLogs
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|editLogs
operator|.
name|get
argument_list|(
name|editLogs
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getFirstTxId
argument_list|()
operator|>=
name|minimumRequiredTxId
condition|)
block|{
name|editLogs
operator|.
name|remove
argument_list|(
name|editLogs
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Next, adjust the number of transactions to retain if doing so would mean
comment|// keeping too many segments around.
while|while
condition|(
name|editLogs
operator|.
name|size
argument_list|()
operator|>
name|maxExtraEditsSegmentsToRetain
condition|)
block|{
name|purgeLogsFrom
operator|=
name|editLogs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastTxId
argument_list|()
operator|+
literal|1
expr_stmt|;
name|editLogs
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// Finally, ensure that we're not trying to purge any transactions that we
comment|// actually need.
if|if
condition|(
name|purgeLogsFrom
operator|>
name|minimumRequiredTxId
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Should not purge more edits than required to "
operator|+
literal|"restore: "
operator|+
name|purgeLogsFrom
operator|+
literal|" should be<= "
operator|+
name|minimumRequiredTxId
argument_list|)
throw|;
block|}
name|purgeableLogs
operator|.
name|purgeLogsOlderThan
argument_list|(
name|purgeLogsFrom
argument_list|)
expr_stmt|;
block|}
DECL|method|purgeCheckpointsOlderThan ( FSImageTransactionalStorageInspector inspector, long minTxId)
specifier|private
name|void
name|purgeCheckpointsOlderThan
parameter_list|(
name|FSImageTransactionalStorageInspector
name|inspector
parameter_list|,
name|long
name|minTxId
parameter_list|)
block|{
for|for
control|(
name|FSImageFile
name|image
range|:
name|inspector
operator|.
name|getFoundImages
argument_list|()
control|)
block|{
if|if
condition|(
name|image
operator|.
name|getCheckpointTxId
argument_list|()
operator|<
name|minTxId
condition|)
block|{
name|purger
operator|.
name|purgeImage
argument_list|(
name|image
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @param inspector inspector that has already inspected all storage dirs    * @return the transaction ID corresponding to the oldest checkpoint    * that should be retained.     */
DECL|method|getImageTxIdToRetain (FSImageTransactionalStorageInspector inspector)
specifier|private
name|long
name|getImageTxIdToRetain
parameter_list|(
name|FSImageTransactionalStorageInspector
name|inspector
parameter_list|)
block|{
name|List
argument_list|<
name|FSImageFile
argument_list|>
name|images
init|=
name|inspector
operator|.
name|getFoundImages
argument_list|()
decl_stmt|;
name|TreeSet
argument_list|<
name|Long
argument_list|>
name|imageTxIds
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|FSImageFile
name|image
range|:
name|images
control|)
block|{
name|imageTxIds
operator|.
name|add
argument_list|(
name|image
operator|.
name|getCheckpointTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|imageTxIdsList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|imageTxIds
argument_list|)
decl_stmt|;
if|if
condition|(
name|imageTxIdsList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
name|Collections
operator|.
name|reverse
argument_list|(
name|imageTxIdsList
argument_list|)
expr_stmt|;
name|int
name|toRetain
init|=
name|Math
operator|.
name|min
argument_list|(
name|numCheckpointsToRetain
argument_list|,
name|imageTxIdsList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|minTxId
init|=
name|imageTxIdsList
operator|.
name|get
argument_list|(
name|toRetain
operator|-
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Going to retain "
operator|+
name|toRetain
operator|+
literal|" images with txid>= "
operator|+
name|minTxId
argument_list|)
expr_stmt|;
return|return
name|minTxId
return|;
block|}
comment|/**    * Interface responsible for disposing of old checkpoints and edit logs.    */
DECL|interface|StoragePurger
specifier|static
interface|interface
name|StoragePurger
block|{
DECL|method|purgeLog (EditLogFile log)
name|void
name|purgeLog
parameter_list|(
name|EditLogFile
name|log
parameter_list|)
function_decl|;
DECL|method|purgeImage (FSImageFile image)
name|void
name|purgeImage
parameter_list|(
name|FSImageFile
name|image
parameter_list|)
function_decl|;
block|}
DECL|class|DeletionStoragePurger
specifier|static
class|class
name|DeletionStoragePurger
implements|implements
name|StoragePurger
block|{
annotation|@
name|Override
DECL|method|purgeLog (EditLogFile log)
specifier|public
name|void
name|purgeLog
parameter_list|(
name|EditLogFile
name|log
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Purging old edit log "
operator|+
name|log
argument_list|)
expr_stmt|;
name|deleteOrWarn
argument_list|(
name|log
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|purgeImage (FSImageFile image)
specifier|public
name|void
name|purgeImage
parameter_list|(
name|FSImageFile
name|image
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Purging old image "
operator|+
name|image
argument_list|)
expr_stmt|;
name|deleteOrWarn
argument_list|(
name|image
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
name|deleteOrWarn
argument_list|(
name|MD5FileUtils
operator|.
name|getDigestFileForFile
argument_list|(
name|image
operator|.
name|getFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteOrWarn (File file)
specifier|private
specifier|static
name|void
name|deleteOrWarn
parameter_list|(
name|File
name|file
parameter_list|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
block|{
comment|// It's OK if we fail to delete something -- we'll catch it
comment|// next time we swing through this directory.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not delete "
operator|+
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Delete old OIV fsimages. Since the target dir is not a full blown    * storage directory, we simply list and keep the latest ones. For the    * same reason, no storage inspector is used.    */
DECL|method|purgeOldLegacyOIVImages (String dir, long txid)
name|void
name|purgeOldLegacyOIVImages
parameter_list|(
name|String
name|dir
parameter_list|,
name|long
name|txid
parameter_list|)
block|{
name|File
name|oivImageDir
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|String
name|oivImagePrefix
init|=
name|NameNodeFile
operator|.
name|IMAGE_LEGACY_OIV
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|filesInStorage
index|[]
decl_stmt|;
comment|// Get the listing
name|filesInStorage
operator|=
name|oivImageDir
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|matches
argument_list|(
name|oivImagePrefix
operator|+
literal|"_(\\d+)"
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Check whether there is any work to do.
if|if
condition|(
name|filesInStorage
operator|!=
literal|null
operator|&&
name|filesInStorage
operator|.
name|length
operator|<=
name|numCheckpointsToRetain
condition|)
block|{
return|return;
block|}
comment|// Create a sorted list of txids from the file names.
name|TreeSet
argument_list|<
name|Long
argument_list|>
name|sortedTxIds
init|=
operator|new
name|TreeSet
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|filesInStorage
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|fName
range|:
name|filesInStorage
control|)
block|{
comment|// Extract the transaction id from the file name.
name|long
name|fTxId
decl_stmt|;
try|try
block|{
name|fTxId
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|fName
operator|.
name|substring
argument_list|(
name|oivImagePrefix
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
comment|// This should not happen since we have already filtered it.
comment|// Log and continue.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid file name. Skipping "
operator|+
name|fName
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|sortedTxIds
operator|.
name|add
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|fTxId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|numFilesToDelete
init|=
name|sortedTxIds
operator|.
name|size
argument_list|()
operator|-
name|numCheckpointsToRetain
decl_stmt|;
name|Iterator
argument_list|<
name|Long
argument_list|>
name|iter
init|=
name|sortedTxIds
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|numFilesToDelete
operator|>
literal|0
operator|&&
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|long
name|txIdVal
init|=
name|iter
operator|.
name|next
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|NNStorage
operator|.
name|getLegacyOIVImageFileName
argument_list|(
name|txIdVal
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting "
operator|+
name|fileName
argument_list|)
expr_stmt|;
name|File
name|fileToDelete
init|=
operator|new
name|File
argument_list|(
name|oivImageDir
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fileToDelete
operator|.
name|delete
argument_list|()
condition|)
block|{
comment|// deletion failed.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete image file: "
operator|+
name|fileToDelete
argument_list|)
expr_stmt|;
block|}
name|numFilesToDelete
operator|--
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

