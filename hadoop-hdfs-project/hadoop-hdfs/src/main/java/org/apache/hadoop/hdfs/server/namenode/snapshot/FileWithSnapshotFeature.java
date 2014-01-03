begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
name|snapshot
package|;
end_package

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
name|hdfs
operator|.
name|protocol
operator|.
name|QuotaExceededException
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
name|blockmanagement
operator|.
name|BlockInfo
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
name|INode
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
name|INode
operator|.
name|BlocksMapUpdateInfo
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
name|INodeFile
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
name|Quota
import|;
end_import

begin_comment
comment|/**  * Feature for file with snapshot-related information.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileWithSnapshotFeature
specifier|public
class|class
name|FileWithSnapshotFeature
implements|implements
name|INode
operator|.
name|Feature
block|{
DECL|field|diffs
specifier|private
specifier|final
name|FileDiffList
name|diffs
decl_stmt|;
DECL|field|isCurrentFileDeleted
specifier|private
name|boolean
name|isCurrentFileDeleted
init|=
literal|false
decl_stmt|;
DECL|method|FileWithSnapshotFeature (FileDiffList diffs)
specifier|public
name|FileWithSnapshotFeature
parameter_list|(
name|FileDiffList
name|diffs
parameter_list|)
block|{
name|this
operator|.
name|diffs
operator|=
name|diffs
operator|!=
literal|null
condition|?
name|diffs
else|:
operator|new
name|FileDiffList
argument_list|()
expr_stmt|;
block|}
DECL|method|isCurrentFileDeleted ()
specifier|public
name|boolean
name|isCurrentFileDeleted
parameter_list|()
block|{
return|return
name|isCurrentFileDeleted
return|;
block|}
comment|/**     * We need to distinguish two scenarios:    * 1) the file is still in the current file directory, it has been modified     *    before while it is included in some snapshot    * 2) the file is not in the current file directory (deleted), but it is in    *    some snapshot, thus we still keep this inode    * For both scenarios the file has snapshot feature. We set     * {@link #isCurrentFileDeleted} to true for 2).    */
DECL|method|deleteCurrentFile ()
specifier|public
name|void
name|deleteCurrentFile
parameter_list|()
block|{
name|isCurrentFileDeleted
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getDiffs ()
specifier|public
name|FileDiffList
name|getDiffs
parameter_list|()
block|{
return|return
name|diffs
return|;
block|}
comment|/** @return the max replication factor in diffs */
DECL|method|getMaxBlockRepInDiffs ()
specifier|public
name|short
name|getMaxBlockRepInDiffs
parameter_list|()
block|{
name|short
name|max
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FileDiff
name|d
range|:
name|getDiffs
argument_list|()
control|)
block|{
if|if
condition|(
name|d
operator|.
name|snapshotINode
operator|!=
literal|null
condition|)
block|{
specifier|final
name|short
name|replication
init|=
name|d
operator|.
name|snapshotINode
operator|.
name|getFileReplication
argument_list|()
decl_stmt|;
if|if
condition|(
name|replication
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|replication
expr_stmt|;
block|}
block|}
block|}
return|return
name|max
return|;
block|}
DECL|method|getDetailedString ()
specifier|public
name|String
name|getDetailedString
parameter_list|()
block|{
return|return
operator|(
name|isCurrentFileDeleted
argument_list|()
condition|?
literal|"(DELETED), "
else|:
literal|", "
operator|)
operator|+
name|diffs
return|;
block|}
DECL|method|cleanFile (final INodeFile file, final Snapshot snapshot, Snapshot prior, final BlocksMapUpdateInfo collectedBlocks, final List<INode> removedINodes, final boolean countDiffChange)
specifier|public
name|Quota
operator|.
name|Counts
name|cleanFile
parameter_list|(
specifier|final
name|INodeFile
name|file
parameter_list|,
specifier|final
name|Snapshot
name|snapshot
parameter_list|,
name|Snapshot
name|prior
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|,
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|removedINodes
parameter_list|,
specifier|final
name|boolean
name|countDiffChange
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
if|if
condition|(
name|snapshot
operator|==
literal|null
condition|)
block|{
comment|// delete the current file while the file has snapshot feature
if|if
condition|(
operator|!
name|isCurrentFileDeleted
argument_list|()
condition|)
block|{
name|file
operator|.
name|recordModification
argument_list|(
name|prior
argument_list|)
expr_stmt|;
name|deleteCurrentFile
argument_list|()
expr_stmt|;
block|}
name|collectBlocksAndClear
argument_list|(
name|file
argument_list|,
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
expr_stmt|;
return|return
name|Quota
operator|.
name|Counts
operator|.
name|newInstance
argument_list|()
return|;
block|}
else|else
block|{
comment|// delete the snapshot
name|prior
operator|=
name|getDiffs
argument_list|()
operator|.
name|updatePrior
argument_list|(
name|snapshot
argument_list|,
name|prior
argument_list|)
expr_stmt|;
return|return
name|diffs
operator|.
name|deleteSnapshotDiff
argument_list|(
name|snapshot
argument_list|,
name|prior
argument_list|,
name|file
argument_list|,
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|,
name|countDiffChange
argument_list|)
return|;
block|}
block|}
DECL|method|clearDiffs ()
specifier|public
name|void
name|clearDiffs
parameter_list|()
block|{
name|this
operator|.
name|diffs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|updateQuotaAndCollectBlocks (INodeFile file, FileDiff removed, BlocksMapUpdateInfo collectedBlocks, final List<INode> removedINodes)
specifier|public
name|Quota
operator|.
name|Counts
name|updateQuotaAndCollectBlocks
parameter_list|(
name|INodeFile
name|file
parameter_list|,
name|FileDiff
name|removed
parameter_list|,
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|,
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|removedINodes
parameter_list|)
block|{
name|long
name|oldDiskspace
init|=
name|file
operator|.
name|diskspaceConsumed
argument_list|()
decl_stmt|;
if|if
condition|(
name|removed
operator|.
name|snapshotINode
operator|!=
literal|null
condition|)
block|{
name|short
name|replication
init|=
name|removed
operator|.
name|snapshotINode
operator|.
name|getFileReplication
argument_list|()
decl_stmt|;
name|short
name|currentRepl
init|=
name|file
operator|.
name|getBlockReplication
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentRepl
operator|==
literal|0
condition|)
block|{
name|oldDiskspace
operator|=
name|file
operator|.
name|computeFileSize
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
operator|*
name|replication
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|replication
operator|>
name|currentRepl
condition|)
block|{
name|oldDiskspace
operator|=
name|oldDiskspace
operator|/
name|file
operator|.
name|getBlockReplication
argument_list|()
operator|*
name|replication
expr_stmt|;
block|}
block|}
name|collectBlocksAndClear
argument_list|(
name|file
argument_list|,
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
expr_stmt|;
name|long
name|dsDelta
init|=
name|oldDiskspace
operator|-
name|file
operator|.
name|diskspaceConsumed
argument_list|()
decl_stmt|;
return|return
name|Quota
operator|.
name|Counts
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
name|dsDelta
argument_list|)
return|;
block|}
comment|/**    * If some blocks at the end of the block list no longer belongs to    * any inode, collect them and update the block list.    */
DECL|method|collectBlocksAndClear (final INodeFile file, final BlocksMapUpdateInfo info, final List<INode> removedINodes)
specifier|private
name|void
name|collectBlocksAndClear
parameter_list|(
specifier|final
name|INodeFile
name|file
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|info
parameter_list|,
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|removedINodes
parameter_list|)
block|{
comment|// check if everything is deleted.
if|if
condition|(
name|isCurrentFileDeleted
argument_list|()
operator|&&
name|getDiffs
argument_list|()
operator|.
name|asList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|file
operator|.
name|destroyAndCollectBlocks
argument_list|(
name|info
argument_list|,
name|removedINodes
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// find max file size.
specifier|final
name|long
name|max
decl_stmt|;
if|if
condition|(
name|isCurrentFileDeleted
argument_list|()
condition|)
block|{
specifier|final
name|FileDiff
name|last
init|=
name|getDiffs
argument_list|()
operator|.
name|getLast
argument_list|()
decl_stmt|;
name|max
operator|=
name|last
operator|==
literal|null
condition|?
literal|0
else|:
name|last
operator|.
name|getFileSize
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|max
operator|=
name|file
operator|.
name|computeFileSize
argument_list|()
expr_stmt|;
block|}
name|collectBlocksBeyondMax
argument_list|(
name|file
argument_list|,
name|max
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
DECL|method|collectBlocksBeyondMax (final INodeFile file, final long max, final BlocksMapUpdateInfo collectedBlocks)
specifier|private
name|void
name|collectBlocksBeyondMax
parameter_list|(
specifier|final
name|INodeFile
name|file
parameter_list|,
specifier|final
name|long
name|max
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
block|{
specifier|final
name|BlockInfo
index|[]
name|oldBlocks
init|=
name|file
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldBlocks
operator|!=
literal|null
condition|)
block|{
comment|//find the minimum n such that the size of the first n blocks> max
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
name|size
init|=
literal|0
init|;
name|n
argument_list|<
name|oldBlocks
operator|.
name|length
operator|&&
name|max
argument_list|>
name|size
condition|;
name|n
operator|++
control|)
block|{
name|size
operator|+=
name|oldBlocks
index|[
name|n
index|]
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
block|}
comment|// starting from block n, the data is beyond max.
if|if
condition|(
name|n
operator|<
name|oldBlocks
operator|.
name|length
condition|)
block|{
comment|// resize the array.
specifier|final
name|BlockInfo
index|[]
name|newBlocks
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
name|newBlocks
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|newBlocks
operator|=
operator|new
name|BlockInfo
index|[
name|n
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|oldBlocks
argument_list|,
literal|0
argument_list|,
name|newBlocks
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
comment|// set new blocks
name|file
operator|.
name|setBlocks
argument_list|(
name|newBlocks
argument_list|)
expr_stmt|;
comment|// collect the blocks beyond max.
if|if
condition|(
name|collectedBlocks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
init|;
name|n
operator|<
name|oldBlocks
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
name|collectedBlocks
operator|.
name|addDeleteBlock
argument_list|(
name|oldBlocks
index|[
name|n
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

