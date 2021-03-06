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
name|INodeAttributes
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
name|INodeDirectory
import|;
end_import

begin_comment
comment|/**  * A list of snapshot diffs for storing snapshot data.  *  * @param<N> The {@link INode} type.  * @param<D> The diff type, which must extend {@link AbstractINodeDiff}.  */
end_comment

begin_class
DECL|class|AbstractINodeDiffList
specifier|abstract
class|class
name|AbstractINodeDiffList
parameter_list|<
name|N
extends|extends
name|INode
parameter_list|,
name|A
extends|extends
name|INodeAttributes
parameter_list|,
name|D
extends|extends
name|AbstractINodeDiff
parameter_list|<
name|N
parameter_list|,
name|A
parameter_list|,
name|D
parameter_list|>
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|D
argument_list|>
block|{
comment|/** Diff list sorted by snapshot IDs, i.e. in chronological order.     * Created lazily to avoid wasting memory by empty lists. */
DECL|field|diffs
specifier|private
name|DiffList
argument_list|<
name|D
argument_list|>
name|diffs
decl_stmt|;
comment|/** @return this list as a unmodifiable {@link List}. */
DECL|method|asList ()
specifier|public
specifier|final
name|DiffList
argument_list|<
name|D
argument_list|>
name|asList
parameter_list|()
block|{
return|return
name|diffs
operator|!=
literal|null
condition|?
name|DiffList
operator|.
name|unmodifiableList
argument_list|(
name|diffs
argument_list|)
else|:
name|DiffList
operator|.
name|emptyList
argument_list|()
return|;
block|}
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|diffs
operator|==
literal|null
operator|||
name|diffs
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/** Clear the list. */
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|diffs
operator|=
literal|null
expr_stmt|;
block|}
comment|/** @return an {@link AbstractINodeDiff}. */
DECL|method|createDiff (int snapshotId, N currentINode)
specifier|abstract
name|D
name|createDiff
parameter_list|(
name|int
name|snapshotId
parameter_list|,
name|N
name|currentINode
parameter_list|)
function_decl|;
comment|/** @return a snapshot copy of the current inode. */
DECL|method|createSnapshotCopy (N currentINode)
specifier|abstract
name|A
name|createSnapshotCopy
parameter_list|(
name|N
name|currentINode
parameter_list|)
function_decl|;
comment|/**    * Delete a snapshot. The synchronization of the diff list will be done     * outside. If the diff to remove is not the first one in the diff list, we     * need to combine the diff with its previous one.    *     * @param reclaimContext blocks and inodes that need to be reclaimed    * @param snapshot The id of the snapshot to be deleted    * @param prior The id of the snapshot taken before the to-be-deleted snapshot    * @param currentINode the inode where the snapshot diff is deleted    */
DECL|method|deleteSnapshotDiff (INode.ReclaimContext reclaimContext, final int snapshot, final int prior, final N currentINode)
specifier|public
specifier|final
name|void
name|deleteSnapshotDiff
parameter_list|(
name|INode
operator|.
name|ReclaimContext
name|reclaimContext
parameter_list|,
specifier|final
name|int
name|snapshot
parameter_list|,
specifier|final
name|int
name|prior
parameter_list|,
specifier|final
name|N
name|currentINode
parameter_list|)
block|{
if|if
condition|(
name|diffs
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|snapshotIndex
init|=
name|diffs
operator|.
name|binarySearch
argument_list|(
name|snapshot
argument_list|)
decl_stmt|;
name|D
name|removed
decl_stmt|;
if|if
condition|(
name|snapshotIndex
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|prior
operator|!=
name|Snapshot
operator|.
name|NO_SNAPSHOT_ID
condition|)
block|{
comment|// there is still snapshot before
comment|// set the snapshot to latestBefore
name|diffs
operator|.
name|get
argument_list|(
name|snapshotIndex
argument_list|)
operator|.
name|setSnapshotId
argument_list|(
name|prior
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// there is no snapshot before
name|removed
operator|=
name|diffs
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|diffs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|diffs
operator|=
literal|null
expr_stmt|;
block|}
name|removed
operator|.
name|destroyDiffAndCollectBlocks
argument_list|(
name|reclaimContext
argument_list|,
name|currentINode
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|snapshotIndex
operator|>
literal|0
condition|)
block|{
specifier|final
name|AbstractINodeDiff
argument_list|<
name|N
argument_list|,
name|A
argument_list|,
name|D
argument_list|>
name|previous
init|=
name|diffs
operator|.
name|get
argument_list|(
name|snapshotIndex
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|.
name|getSnapshotId
argument_list|()
operator|!=
name|prior
condition|)
block|{
name|diffs
operator|.
name|get
argument_list|(
name|snapshotIndex
argument_list|)
operator|.
name|setSnapshotId
argument_list|(
name|prior
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// combine the to-be-removed diff with its previous diff
name|removed
operator|=
name|diffs
operator|.
name|remove
argument_list|(
name|snapshotIndex
argument_list|)
expr_stmt|;
if|if
condition|(
name|previous
operator|.
name|snapshotINode
operator|==
literal|null
condition|)
block|{
name|previous
operator|.
name|snapshotINode
operator|=
name|removed
operator|.
name|snapshotINode
expr_stmt|;
block|}
name|previous
operator|.
name|combinePosteriorAndCollectBlocks
argument_list|(
name|reclaimContext
argument_list|,
name|currentINode
argument_list|,
name|removed
argument_list|)
expr_stmt|;
name|previous
operator|.
name|setPosterior
argument_list|(
name|removed
operator|.
name|getPosterior
argument_list|()
argument_list|)
expr_stmt|;
name|removed
operator|.
name|setPosterior
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Add an {@link AbstractINodeDiff} for the given snapshot. */
DECL|method|addDiff (int latestSnapshotId, N currentINode)
specifier|final
name|D
name|addDiff
parameter_list|(
name|int
name|latestSnapshotId
parameter_list|,
name|N
name|currentINode
parameter_list|)
block|{
return|return
name|addLast
argument_list|(
name|createDiff
argument_list|(
name|latestSnapshotId
argument_list|,
name|currentINode
argument_list|)
argument_list|)
return|;
block|}
comment|/** Append the diff at the end of the list. */
DECL|method|addLast (D diff)
specifier|private
name|D
name|addLast
parameter_list|(
name|D
name|diff
parameter_list|)
block|{
name|createDiffsIfNeeded
argument_list|()
expr_stmt|;
specifier|final
name|D
name|last
init|=
name|getLast
argument_list|()
decl_stmt|;
name|diffs
operator|.
name|addLast
argument_list|(
name|diff
argument_list|)
expr_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
name|last
operator|.
name|setPosterior
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
return|return
name|diff
return|;
block|}
comment|/** Add the diff to the beginning of the list. */
DECL|method|addFirst (D diff)
specifier|final
name|void
name|addFirst
parameter_list|(
name|D
name|diff
parameter_list|)
block|{
name|createDiffsIfNeeded
argument_list|()
expr_stmt|;
specifier|final
name|D
name|first
init|=
name|diffs
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|diffs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|diffs
operator|.
name|addFirst
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|diff
operator|.
name|setPosterior
argument_list|(
name|first
argument_list|)
expr_stmt|;
block|}
comment|/** @return the last diff. */
DECL|method|getLast ()
specifier|public
specifier|final
name|D
name|getLast
parameter_list|()
block|{
if|if
condition|(
name|diffs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|n
init|=
name|diffs
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
name|n
operator|==
literal|0
condition|?
literal|null
else|:
name|diffs
operator|.
name|get
argument_list|(
name|n
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|newDiffs ()
name|DiffList
argument_list|<
name|D
argument_list|>
name|newDiffs
parameter_list|()
block|{
return|return
operator|new
name|DiffListByArrayList
argument_list|<>
argument_list|(
name|INodeDirectory
operator|.
name|DEFAULT_FILES_PER_DIRECTORY
argument_list|)
return|;
block|}
DECL|method|createDiffsIfNeeded ()
specifier|private
name|void
name|createDiffsIfNeeded
parameter_list|()
block|{
if|if
condition|(
name|diffs
operator|==
literal|null
condition|)
block|{
name|diffs
operator|=
name|newDiffs
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** @return the id of the last snapshot. */
DECL|method|getLastSnapshotId ()
specifier|public
specifier|final
name|int
name|getLastSnapshotId
parameter_list|()
block|{
specifier|final
name|AbstractINodeDiff
argument_list|<
name|N
argument_list|,
name|A
argument_list|,
name|D
argument_list|>
name|last
init|=
name|getLast
argument_list|()
decl_stmt|;
return|return
name|last
operator|==
literal|null
condition|?
name|Snapshot
operator|.
name|CURRENT_STATE_ID
else|:
name|last
operator|.
name|getSnapshotId
argument_list|()
return|;
block|}
comment|/**    * Find the latest snapshot before a given snapshot.    * @param anchorId The returned snapshot's id must be&lt;= or&lt; this    *                 given snapshot id.    * @param exclusive True means the returned snapshot's id must be&lt; the    *                  given id, otherwise&lt;=.    * @return The id of the latest snapshot before the given snapshot.    */
DECL|method|getPrior (int anchorId, boolean exclusive)
specifier|public
specifier|final
name|int
name|getPrior
parameter_list|(
name|int
name|anchorId
parameter_list|,
name|boolean
name|exclusive
parameter_list|)
block|{
if|if
condition|(
name|diffs
operator|==
literal|null
condition|)
block|{
return|return
name|Snapshot
operator|.
name|NO_SNAPSHOT_ID
return|;
block|}
if|if
condition|(
name|anchorId
operator|==
name|Snapshot
operator|.
name|CURRENT_STATE_ID
condition|)
block|{
name|int
name|last
init|=
name|getLastSnapshotId
argument_list|()
decl_stmt|;
if|if
condition|(
name|exclusive
operator|&&
name|last
operator|==
name|anchorId
condition|)
block|{
return|return
name|Snapshot
operator|.
name|NO_SNAPSHOT_ID
return|;
block|}
return|return
name|last
return|;
block|}
specifier|final
name|int
name|i
init|=
name|diffs
operator|.
name|binarySearch
argument_list|(
name|anchorId
argument_list|)
decl_stmt|;
if|if
condition|(
name|exclusive
condition|)
block|{
comment|// must be the one before
if|if
condition|(
name|i
operator|==
operator|-
literal|1
operator|||
name|i
operator|==
literal|0
condition|)
block|{
return|return
name|Snapshot
operator|.
name|NO_SNAPSHOT_ID
return|;
block|}
else|else
block|{
name|int
name|priorIndex
init|=
name|i
operator|>
literal|0
condition|?
name|i
operator|-
literal|1
else|:
operator|-
name|i
operator|-
literal|2
decl_stmt|;
return|return
name|diffs
operator|.
name|get
argument_list|(
name|priorIndex
argument_list|)
operator|.
name|getSnapshotId
argument_list|()
return|;
block|}
block|}
else|else
block|{
comment|// the one, or the one before if not existing
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|diffs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getSnapshotId
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|i
operator|<
operator|-
literal|1
condition|)
block|{
return|return
name|diffs
operator|.
name|get
argument_list|(
operator|-
name|i
operator|-
literal|2
argument_list|)
operator|.
name|getSnapshotId
argument_list|()
return|;
block|}
else|else
block|{
comment|// i == -1
return|return
name|Snapshot
operator|.
name|NO_SNAPSHOT_ID
return|;
block|}
block|}
block|}
DECL|method|getPrior (int snapshotId)
specifier|public
specifier|final
name|int
name|getPrior
parameter_list|(
name|int
name|snapshotId
parameter_list|)
block|{
return|return
name|getPrior
argument_list|(
name|snapshotId
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Update the prior snapshot.    */
DECL|method|updatePrior (int snapshot, int prior)
specifier|final
name|int
name|updatePrior
parameter_list|(
name|int
name|snapshot
parameter_list|,
name|int
name|prior
parameter_list|)
block|{
name|int
name|p
init|=
name|getPrior
argument_list|(
name|snapshot
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
name|Snapshot
operator|.
name|CURRENT_STATE_ID
operator|&&
name|Snapshot
operator|.
name|ID_INTEGER_COMPARATOR
operator|.
name|compare
argument_list|(
name|p
argument_list|,
name|prior
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|p
return|;
block|}
return|return
name|prior
return|;
block|}
DECL|method|getDiffById (final int snapshotId)
specifier|public
specifier|final
name|D
name|getDiffById
parameter_list|(
specifier|final
name|int
name|snapshotId
parameter_list|)
block|{
if|if
condition|(
name|snapshotId
operator|==
name|Snapshot
operator|.
name|CURRENT_STATE_ID
operator|||
name|diffs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|i
init|=
name|diffs
operator|.
name|binarySearch
argument_list|(
name|snapshotId
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
comment|// exact match
return|return
name|diffs
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
else|else
block|{
comment|// Exact match not found means that there were no changes between
comment|// given snapshot and the next state so that the diff for the given
comment|// snapshot was not recorded. Thus, return the next state.
specifier|final
name|int
name|j
init|=
operator|-
name|i
operator|-
literal|1
decl_stmt|;
return|return
name|j
operator|<
name|diffs
operator|.
name|size
argument_list|()
condition|?
name|diffs
operator|.
name|get
argument_list|(
name|j
argument_list|)
else|:
literal|null
return|;
block|}
block|}
comment|/**    * Search for the snapshot whose id is 1) no less than the given id,     * and 2) most close to the given id.    */
DECL|method|getSnapshotById (final int snapshotId)
specifier|public
specifier|final
name|int
name|getSnapshotById
parameter_list|(
specifier|final
name|int
name|snapshotId
parameter_list|)
block|{
name|D
name|diff
init|=
name|getDiffById
argument_list|(
name|snapshotId
argument_list|)
decl_stmt|;
return|return
name|diff
operator|==
literal|null
condition|?
name|Snapshot
operator|.
name|CURRENT_STATE_ID
else|:
name|diff
operator|.
name|getSnapshotId
argument_list|()
return|;
block|}
DECL|method|getDiffIndexById (final int snapshotId)
specifier|public
specifier|final
name|int
name|getDiffIndexById
parameter_list|(
specifier|final
name|int
name|snapshotId
parameter_list|)
block|{
name|int
name|diffIndex
init|=
name|diffs
operator|.
name|binarySearch
argument_list|(
name|snapshotId
argument_list|)
decl_stmt|;
name|diffIndex
operator|=
name|diffIndex
operator|<
literal|0
condition|?
operator|(
operator|-
name|diffIndex
operator|-
literal|1
operator|)
else|:
name|diffIndex
expr_stmt|;
return|return
name|diffIndex
return|;
block|}
DECL|method|changedBetweenSnapshots (Snapshot from, Snapshot to)
specifier|final
name|int
index|[]
name|changedBetweenSnapshots
parameter_list|(
name|Snapshot
name|from
parameter_list|,
name|Snapshot
name|to
parameter_list|)
block|{
if|if
condition|(
name|diffs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Snapshot
name|earlier
init|=
name|from
decl_stmt|;
name|Snapshot
name|later
init|=
name|to
decl_stmt|;
if|if
condition|(
name|Snapshot
operator|.
name|ID_COMPARATOR
operator|.
name|compare
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
operator|>
literal|0
condition|)
block|{
name|earlier
operator|=
name|to
expr_stmt|;
name|later
operator|=
name|from
expr_stmt|;
block|}
specifier|final
name|int
name|size
init|=
name|diffs
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|earlierDiffIndex
init|=
name|getDiffIndexById
argument_list|(
name|earlier
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|laterDiffIndex
init|=
name|later
operator|==
literal|null
condition|?
name|size
else|:
name|getDiffIndexById
argument_list|(
name|later
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|earlierDiffIndex
operator|==
name|size
condition|)
block|{
comment|// if the earlierSnapshot is after the latest SnapshotDiff stored in
comment|// diffs, no modification happened after the earlierSnapshot
return|return
literal|null
return|;
block|}
if|if
condition|(
name|laterDiffIndex
operator|==
operator|-
literal|1
operator|||
name|laterDiffIndex
operator|==
literal|0
condition|)
block|{
comment|// if the laterSnapshot is the earliest SnapshotDiff stored in diffs, or
comment|// before it, no modification happened before the laterSnapshot
return|return
literal|null
return|;
block|}
return|return
operator|new
name|int
index|[]
block|{
name|earlierDiffIndex
block|,
name|laterDiffIndex
block|}
return|;
block|}
comment|/**    * @return the inode corresponding to the given snapshot.    *         Note that the current inode is returned if there is no change    *         between the given snapshot and the current state.     */
DECL|method|getSnapshotINode (final int snapshotId, final A currentINode)
specifier|public
name|A
name|getSnapshotINode
parameter_list|(
specifier|final
name|int
name|snapshotId
parameter_list|,
specifier|final
name|A
name|currentINode
parameter_list|)
block|{
specifier|final
name|D
name|diff
init|=
name|getDiffById
argument_list|(
name|snapshotId
argument_list|)
decl_stmt|;
specifier|final
name|A
name|inode
init|=
name|diff
operator|==
literal|null
condition|?
literal|null
else|:
name|diff
operator|.
name|getSnapshotINode
argument_list|()
decl_stmt|;
return|return
name|inode
operator|==
literal|null
condition|?
name|currentINode
else|:
name|inode
return|;
block|}
comment|/**    * Check if the latest snapshot diff exists.  If not, add it.    * @return the latest snapshot diff, which is never null.    */
DECL|method|checkAndAddLatestSnapshotDiff (int latestSnapshotId, N currentINode)
specifier|final
name|D
name|checkAndAddLatestSnapshotDiff
parameter_list|(
name|int
name|latestSnapshotId
parameter_list|,
name|N
name|currentINode
parameter_list|)
block|{
specifier|final
name|D
name|last
init|=
name|getLast
argument_list|()
decl_stmt|;
return|return
operator|(
name|last
operator|!=
literal|null
operator|&&
name|Snapshot
operator|.
name|ID_INTEGER_COMPARATOR
operator|.
name|compare
argument_list|(
name|last
operator|.
name|getSnapshotId
argument_list|()
argument_list|,
name|latestSnapshotId
argument_list|)
operator|>=
literal|0
operator|)
condition|?
name|last
else|:
name|addDiff
argument_list|(
name|latestSnapshotId
argument_list|,
name|currentINode
argument_list|)
return|;
block|}
comment|/** Save the snapshot copy to the latest snapshot. */
DECL|method|saveSelf2Snapshot (int latestSnapshotId, N currentINode, A snapshotCopy)
specifier|public
name|D
name|saveSelf2Snapshot
parameter_list|(
name|int
name|latestSnapshotId
parameter_list|,
name|N
name|currentINode
parameter_list|,
name|A
name|snapshotCopy
parameter_list|)
block|{
name|D
name|diff
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|latestSnapshotId
operator|!=
name|Snapshot
operator|.
name|CURRENT_STATE_ID
condition|)
block|{
name|diff
operator|=
name|checkAndAddLatestSnapshotDiff
argument_list|(
name|latestSnapshotId
argument_list|,
name|currentINode
argument_list|)
expr_stmt|;
if|if
condition|(
name|diff
operator|.
name|snapshotINode
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|snapshotCopy
operator|==
literal|null
condition|)
block|{
name|snapshotCopy
operator|=
name|createSnapshotCopy
argument_list|(
name|currentINode
argument_list|)
expr_stmt|;
block|}
name|diff
operator|.
name|saveSnapshotCopy
argument_list|(
name|snapshotCopy
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|diff
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|D
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|diffs
operator|!=
literal|null
condition|?
name|diffs
operator|.
name|iterator
argument_list|()
else|:
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|diffs
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|hashCode
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
for|for
control|(
name|D
name|d
range|:
name|diffs
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|d
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|setLength
argument_list|(
name|b
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
block|}
end_class

end_unit

