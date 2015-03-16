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
name|Collections
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
name|blockmanagement
operator|.
name|BlockInfoContiguous
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
name|BlockInfoContiguousUnderConstruction
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
name|INodeFileAttributes
import|;
end_import

begin_comment
comment|/** A list of FileDiffs for storing snapshot data. */
end_comment

begin_class
DECL|class|FileDiffList
specifier|public
class|class
name|FileDiffList
extends|extends
name|AbstractINodeDiffList
argument_list|<
name|INodeFile
argument_list|,
name|INodeFileAttributes
argument_list|,
name|FileDiff
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createDiff (int snapshotId, INodeFile file)
name|FileDiff
name|createDiff
parameter_list|(
name|int
name|snapshotId
parameter_list|,
name|INodeFile
name|file
parameter_list|)
block|{
return|return
operator|new
name|FileDiff
argument_list|(
name|snapshotId
argument_list|,
name|file
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createSnapshotCopy (INodeFile currentINode)
name|INodeFileAttributes
name|createSnapshotCopy
parameter_list|(
name|INodeFile
name|currentINode
parameter_list|)
block|{
return|return
operator|new
name|INodeFileAttributes
operator|.
name|SnapshotCopy
argument_list|(
name|currentINode
argument_list|)
return|;
block|}
DECL|method|destroyAndCollectSnapshotBlocks ( BlocksMapUpdateInfo collectedBlocks)
specifier|public
name|void
name|destroyAndCollectSnapshotBlocks
parameter_list|(
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
block|{
for|for
control|(
name|FileDiff
name|d
range|:
name|asList
argument_list|()
control|)
block|{
name|d
operator|.
name|destroyAndCollectSnapshotBlocks
argument_list|(
name|collectedBlocks
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|saveSelf2Snapshot (int latestSnapshotId, INodeFile iNodeFile, INodeFileAttributes snapshotCopy, boolean withBlocks)
specifier|public
name|void
name|saveSelf2Snapshot
parameter_list|(
name|int
name|latestSnapshotId
parameter_list|,
name|INodeFile
name|iNodeFile
parameter_list|,
name|INodeFileAttributes
name|snapshotCopy
parameter_list|,
name|boolean
name|withBlocks
parameter_list|)
block|{
specifier|final
name|FileDiff
name|diff
init|=
name|super
operator|.
name|saveSelf2Snapshot
argument_list|(
name|latestSnapshotId
argument_list|,
name|iNodeFile
argument_list|,
name|snapshotCopy
argument_list|)
decl_stmt|;
if|if
condition|(
name|withBlocks
condition|)
block|{
comment|// Store blocks if this is the first update
name|BlockInfoContiguous
index|[]
name|blks
init|=
name|iNodeFile
operator|.
name|getContiguousBlocks
argument_list|()
decl_stmt|;
assert|assert
name|blks
operator|!=
literal|null
assert|;
name|diff
operator|.
name|setBlocks
argument_list|(
name|blks
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|findEarlierSnapshotBlocks (int snapshotId)
specifier|public
name|BlockInfoContiguous
index|[]
name|findEarlierSnapshotBlocks
parameter_list|(
name|int
name|snapshotId
parameter_list|)
block|{
assert|assert
name|snapshotId
operator|!=
name|Snapshot
operator|.
name|NO_SNAPSHOT_ID
operator|:
literal|"Wrong snapshot id"
assert|;
if|if
condition|(
name|snapshotId
operator|==
name|Snapshot
operator|.
name|CURRENT_STATE_ID
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|FileDiff
argument_list|>
name|diffs
init|=
name|this
operator|.
name|asList
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|diffs
argument_list|,
name|snapshotId
argument_list|)
decl_stmt|;
name|BlockInfoContiguous
index|[]
name|blocks
init|=
literal|null
decl_stmt|;
for|for
control|(
name|i
operator|=
name|i
operator|>=
literal|0
condition|?
name|i
else|:
operator|-
name|i
operator|-
literal|2
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|blocks
operator|=
name|diffs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
if|if
condition|(
name|blocks
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
return|return
name|blocks
return|;
block|}
DECL|method|findLaterSnapshotBlocks (int snapshotId)
specifier|public
name|BlockInfoContiguous
index|[]
name|findLaterSnapshotBlocks
parameter_list|(
name|int
name|snapshotId
parameter_list|)
block|{
assert|assert
name|snapshotId
operator|!=
name|Snapshot
operator|.
name|NO_SNAPSHOT_ID
operator|:
literal|"Wrong snapshot id"
assert|;
if|if
condition|(
name|snapshotId
operator|==
name|Snapshot
operator|.
name|CURRENT_STATE_ID
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|FileDiff
argument_list|>
name|diffs
init|=
name|this
operator|.
name|asList
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|diffs
argument_list|,
name|snapshotId
argument_list|)
decl_stmt|;
name|BlockInfoContiguous
index|[]
name|blocks
init|=
literal|null
decl_stmt|;
for|for
control|(
name|i
operator|=
name|i
operator|>=
literal|0
condition|?
name|i
operator|+
literal|1
else|:
operator|-
name|i
operator|-
literal|1
init|;
name|i
operator|<
name|diffs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|blocks
operator|=
name|diffs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
if|if
condition|(
name|blocks
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
return|return
name|blocks
return|;
block|}
comment|/**    * Copy blocks from the removed snapshot into the previous snapshot    * up to the file length of the latter.    * Collect unused blocks of the removed snapshot.    */
DECL|method|combineAndCollectSnapshotBlocks ( INode.ReclaimContext reclaimContext, INodeFile file, FileDiff removed)
name|void
name|combineAndCollectSnapshotBlocks
parameter_list|(
name|INode
operator|.
name|ReclaimContext
name|reclaimContext
parameter_list|,
name|INodeFile
name|file
parameter_list|,
name|FileDiff
name|removed
parameter_list|)
block|{
name|BlockInfoContiguous
index|[]
name|removedBlocks
init|=
name|removed
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
if|if
condition|(
name|removedBlocks
operator|==
literal|null
condition|)
block|{
name|FileWithSnapshotFeature
name|sf
init|=
name|file
operator|.
name|getFileWithSnapshotFeature
argument_list|()
decl_stmt|;
assert|assert
name|sf
operator|!=
literal|null
operator|:
literal|"FileWithSnapshotFeature is null"
assert|;
if|if
condition|(
name|sf
operator|.
name|isCurrentFileDeleted
argument_list|()
condition|)
name|sf
operator|.
name|collectBlocksAndClear
argument_list|(
name|reclaimContext
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|p
init|=
name|getPrior
argument_list|(
name|removed
operator|.
name|getSnapshotId
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FileDiff
name|earlierDiff
init|=
name|p
operator|==
name|Snapshot
operator|.
name|NO_SNAPSHOT_ID
condition|?
literal|null
else|:
name|getDiffById
argument_list|(
name|p
argument_list|)
decl_stmt|;
comment|// Copy blocks to the previous snapshot if not set already
if|if
condition|(
name|earlierDiff
operator|!=
literal|null
condition|)
block|{
name|earlierDiff
operator|.
name|setBlocks
argument_list|(
name|removedBlocks
argument_list|)
expr_stmt|;
block|}
name|BlockInfoContiguous
index|[]
name|earlierBlocks
init|=
operator|(
name|earlierDiff
operator|==
literal|null
condition|?
operator|new
name|BlockInfoContiguous
index|[]
block|{}
else|:
name|earlierDiff
operator|.
name|getBlocks
argument_list|()
operator|)
decl_stmt|;
comment|// Find later snapshot (or file itself) with blocks
name|BlockInfoContiguous
index|[]
name|laterBlocks
init|=
name|findLaterSnapshotBlocks
argument_list|(
name|removed
operator|.
name|getSnapshotId
argument_list|()
argument_list|)
decl_stmt|;
name|laterBlocks
operator|=
operator|(
name|laterBlocks
operator|==
literal|null
operator|)
condition|?
name|file
operator|.
name|getContiguousBlocks
argument_list|()
else|:
name|laterBlocks
expr_stmt|;
comment|// Skip blocks, which belong to either the earlier or the later lists
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|removedBlocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|<
name|earlierBlocks
operator|.
name|length
operator|&&
name|removedBlocks
index|[
name|i
index|]
operator|==
name|earlierBlocks
index|[
name|i
index|]
condition|)
continue|continue;
if|if
condition|(
name|i
operator|<
name|laterBlocks
operator|.
name|length
operator|&&
name|removedBlocks
index|[
name|i
index|]
operator|==
name|laterBlocks
index|[
name|i
index|]
condition|)
continue|continue;
break|break;
block|}
comment|// Check if last block is part of truncate recovery
name|BlockInfo
name|lastBlock
init|=
name|file
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
name|Block
name|dontRemoveBlock
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|lastBlock
operator|!=
literal|null
operator|&&
name|lastBlock
operator|.
name|getBlockUCState
argument_list|()
operator|.
name|equals
argument_list|(
name|HdfsServerConstants
operator|.
name|BlockUCState
operator|.
name|UNDER_RECOVERY
argument_list|)
condition|)
block|{
name|dontRemoveBlock
operator|=
operator|(
operator|(
name|BlockInfoContiguousUnderConstruction
operator|)
name|lastBlock
operator|)
operator|.
name|getTruncateBlock
argument_list|()
expr_stmt|;
block|}
comment|// Collect the remaining blocks of the file, ignoring truncate block
for|for
control|(
init|;
name|i
operator|<
name|removedBlocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|dontRemoveBlock
operator|==
literal|null
operator|||
operator|!
name|removedBlocks
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|dontRemoveBlock
argument_list|)
condition|)
block|{
name|reclaimContext
operator|.
name|collectedBlocks
argument_list|()
operator|.
name|addDeleteBlock
argument_list|(
name|removedBlocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

