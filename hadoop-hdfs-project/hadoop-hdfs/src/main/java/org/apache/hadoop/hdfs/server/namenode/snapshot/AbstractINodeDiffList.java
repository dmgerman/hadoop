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
name|protocol
operator|.
name|NSQuotaExceededException
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
name|D
extends|extends
name|AbstractINodeDiff
parameter_list|<
name|N
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
DECL|field|factory
specifier|private
name|AbstractINodeDiff
operator|.
name|Factory
argument_list|<
name|N
argument_list|,
name|D
argument_list|>
name|factory
decl_stmt|;
comment|/** Diff list sorted by snapshot IDs, i.e. in chronological order. */
DECL|field|diffs
specifier|private
specifier|final
name|List
argument_list|<
name|D
argument_list|>
name|diffs
init|=
operator|new
name|ArrayList
argument_list|<
name|D
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|setFactory (AbstractINodeDiff.Factory<N, D> factory)
name|void
name|setFactory
parameter_list|(
name|AbstractINodeDiff
operator|.
name|Factory
argument_list|<
name|N
argument_list|,
name|D
argument_list|>
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
comment|/** @return this list as a unmodifiable {@link List}. */
DECL|method|asList ()
specifier|public
specifier|final
name|List
argument_list|<
name|D
argument_list|>
name|asList
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|diffs
argument_list|)
return|;
block|}
comment|/** Get the size of the list and then clear it. */
DECL|method|clear ()
specifier|public
name|int
name|clear
parameter_list|()
block|{
specifier|final
name|int
name|n
init|=
name|diffs
operator|.
name|size
argument_list|()
decl_stmt|;
name|diffs
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|n
return|;
block|}
comment|/**    * Delete a snapshot. The synchronization of the diff list will be done     * outside. If the diff to remove is not the first one in the diff list, we     * need to combine the diff with its previous one.    *     * @param snapshot The snapshot to be deleted    * @param prior The snapshot taken before the to-be-deleted snapshot    * @param collectedBlocks Used to collect information for blocksMap update    * @return delta in namespace.     */
DECL|method|deleteSnapshotDiff (final Snapshot snapshot, Snapshot prior, final N currentINode, final BlocksMapUpdateInfo collectedBlocks)
specifier|final
name|int
name|deleteSnapshotDiff
parameter_list|(
specifier|final
name|Snapshot
name|snapshot
parameter_list|,
name|Snapshot
name|prior
parameter_list|,
specifier|final
name|N
name|currentINode
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
block|{
name|int
name|snapshotIndex
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|diffs
argument_list|,
name|snapshot
argument_list|)
decl_stmt|;
name|int
name|removedNum
init|=
literal|0
decl_stmt|;
name|D
name|removed
init|=
literal|null
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
literal|null
condition|)
block|{
comment|// set the snapshot to latestBefore
name|diffs
operator|.
name|get
argument_list|(
name|snapshotIndex
argument_list|)
operator|.
name|setSnapshot
argument_list|(
name|prior
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|removed
operator|=
name|diffs
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|removedNum
operator|++
expr_stmt|;
comment|// removed a diff
name|removedNum
operator|+=
name|removed
operator|.
name|destroyAndCollectBlocks
argument_list|(
name|currentINode
argument_list|,
name|collectedBlocks
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
operator|!
name|previous
operator|.
name|getSnapshot
argument_list|()
operator|.
name|equals
argument_list|(
name|prior
argument_list|)
condition|)
block|{
name|diffs
operator|.
name|get
argument_list|(
name|snapshotIndex
argument_list|)
operator|.
name|setSnapshot
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
name|removedNum
operator|++
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
elseif|else
if|if
condition|(
name|removed
operator|.
name|snapshotINode
operator|!=
literal|null
condition|)
block|{
name|removed
operator|.
name|snapshotINode
operator|.
name|clearReferences
argument_list|()
expr_stmt|;
block|}
name|removedNum
operator|+=
name|previous
operator|.
name|combinePosteriorAndCollectBlocks
argument_list|(
name|currentINode
argument_list|,
name|removed
argument_list|,
name|collectedBlocks
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
return|return
name|removedNum
return|;
block|}
comment|/** Add an {@link AbstractINodeDiff} for the given snapshot. */
DECL|method|addDiff (Snapshot latest, N currentINode)
specifier|final
name|D
name|addDiff
parameter_list|(
name|Snapshot
name|latest
parameter_list|,
name|N
name|currentINode
parameter_list|)
throws|throws
name|NSQuotaExceededException
block|{
name|currentINode
operator|.
name|addNamespaceConsumed
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|addLast
argument_list|(
name|factory
operator|.
name|createDiff
argument_list|(
name|latest
argument_list|,
name|currentINode
argument_list|)
argument_list|)
return|;
block|}
comment|/** Append the diff at the end of the list. */
DECL|method|addLast (D diff)
specifier|private
specifier|final
name|D
name|addLast
parameter_list|(
name|D
name|diff
parameter_list|)
block|{
specifier|final
name|D
name|last
init|=
name|getLast
argument_list|()
decl_stmt|;
name|diffs
operator|.
name|add
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
name|add
argument_list|(
literal|0
argument_list|,
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
specifier|final
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
comment|/** @return the last snapshot. */
DECL|method|getLastSnapshot ()
specifier|final
name|Snapshot
name|getLastSnapshot
parameter_list|()
block|{
specifier|final
name|AbstractINodeDiff
argument_list|<
name|N
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
literal|null
else|:
name|last
operator|.
name|getSnapshot
argument_list|()
return|;
block|}
comment|/**    * Find the latest snapshot before a given snapshot.    * @param anchor The returned snapshot must be taken before this given     *               snapshot.    * @return The latest snapshot before the given snapshot.    */
DECL|method|getPrior (Snapshot anchor)
specifier|final
name|Snapshot
name|getPrior
parameter_list|(
name|Snapshot
name|anchor
parameter_list|)
block|{
if|if
condition|(
name|anchor
operator|==
literal|null
condition|)
block|{
return|return
name|getLastSnapshot
argument_list|()
return|;
block|}
specifier|final
name|int
name|i
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|diffs
argument_list|,
name|anchor
argument_list|)
decl_stmt|;
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
literal|null
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
name|getSnapshot
argument_list|()
return|;
block|}
block|}
comment|/**    * @return the diff corresponding to the given snapshot.    *         When the diff is null, it means that the current state and    *         the corresponding snapshot state are the same.     */
DECL|method|getDiff (Snapshot snapshot)
specifier|public
specifier|final
name|D
name|getDiff
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|==
literal|null
condition|)
block|{
comment|// snapshot == null means the current state, therefore, return null.
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|i
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|diffs
argument_list|,
name|snapshot
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
comment|// snapshot was not recorded.  Thus, return the next state.
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
comment|/**    * Check if changes have happened between two snapshots.    * @param earlierSnapshot The snapshot taken earlier    * @param laterSnapshot The snapshot taken later    * @return Whether or not modifications (including diretory/file metadata    *         change, file creation/deletion under the directory) have happened    *         between snapshots.    */
DECL|method|changedBetweenSnapshots (Snapshot earlierSnapshot, Snapshot laterSnapshot)
specifier|final
name|boolean
name|changedBetweenSnapshots
parameter_list|(
name|Snapshot
name|earlierSnapshot
parameter_list|,
name|Snapshot
name|laterSnapshot
parameter_list|)
block|{
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
name|Collections
operator|.
name|binarySearch
argument_list|(
name|diffs
argument_list|,
name|earlierSnapshot
argument_list|)
decl_stmt|;
if|if
condition|(
operator|-
name|earlierDiffIndex
operator|-
literal|1
operator|==
name|size
condition|)
block|{
comment|// if the earlierSnapshot is after the latest SnapshotDiff stored in
comment|// diffs, no modification happened after the earlierSnapshot
return|return
literal|false
return|;
block|}
if|if
condition|(
name|laterSnapshot
operator|!=
literal|null
condition|)
block|{
name|int
name|laterDiffIndex
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|diffs
argument_list|,
name|laterSnapshot
argument_list|)
decl_stmt|;
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
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * @return the inode corresponding to the given snapshot.    *         Note that the current inode is returned if there is no change    *         between the given snapshot and the current state.     */
DECL|method|getSnapshotINode (final Snapshot snapshot, final N currentINode)
name|N
name|getSnapshotINode
parameter_list|(
specifier|final
name|Snapshot
name|snapshot
parameter_list|,
specifier|final
name|N
name|currentINode
parameter_list|)
block|{
specifier|final
name|D
name|diff
init|=
name|getDiff
argument_list|(
name|snapshot
argument_list|)
decl_stmt|;
specifier|final
name|N
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
DECL|method|checkAndAddLatestSnapshotDiff (Snapshot latest, N currentINode)
specifier|final
name|D
name|checkAndAddLatestSnapshotDiff
parameter_list|(
name|Snapshot
name|latest
parameter_list|,
name|N
name|currentINode
parameter_list|)
throws|throws
name|NSQuotaExceededException
block|{
specifier|final
name|D
name|last
init|=
name|getLast
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
operator|&&
name|Snapshot
operator|.
name|ID_COMPARATOR
operator|.
name|compare
argument_list|(
name|last
operator|.
name|getSnapshot
argument_list|()
argument_list|,
name|latest
argument_list|)
operator|>=
literal|0
condition|)
block|{
return|return
name|last
return|;
block|}
else|else
block|{
try|try
block|{
return|return
name|addDiff
argument_list|(
name|latest
argument_list|,
name|currentINode
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NSQuotaExceededException
name|e
parameter_list|)
block|{
name|e
operator|.
name|setMessagePrefix
argument_list|(
literal|"Failed to record modification for snapshot"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
comment|/** Save the snapshot copy to the latest snapshot. */
DECL|method|saveSelf2Snapshot (Snapshot latest, N currentINode, N snapshotCopy)
specifier|public
name|void
name|saveSelf2Snapshot
parameter_list|(
name|Snapshot
name|latest
parameter_list|,
name|N
name|currentINode
parameter_list|,
name|N
name|snapshotCopy
parameter_list|)
throws|throws
name|NSQuotaExceededException
block|{
if|if
condition|(
name|latest
operator|!=
literal|null
condition|)
block|{
name|checkAndAddLatestSnapshotDiff
argument_list|(
name|latest
argument_list|,
name|currentINode
argument_list|)
operator|.
name|saveSnapshotCopy
argument_list|(
name|snapshotCopy
argument_list|,
name|factory
argument_list|,
name|currentINode
argument_list|)
expr_stmt|;
block|}
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
operator|.
name|iterator
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
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|diffs
return|;
block|}
block|}
end_class

end_unit

