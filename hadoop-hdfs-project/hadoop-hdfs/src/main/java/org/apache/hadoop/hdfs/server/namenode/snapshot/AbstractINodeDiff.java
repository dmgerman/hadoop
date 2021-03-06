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
name|io
operator|.
name|DataOutput
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
name|snapshot
operator|.
name|SnapshotFSImageFormat
operator|.
name|ReferenceMap
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

begin_comment
comment|/**  * The difference of an inode between in two snapshots.  * {@link AbstractINodeDiffList} maintains a list of snapshot diffs,  *<pre>  *   d_1 -> d_2 -> ... -> d_n -> null,  *</pre>  * where -> denotes the {@link AbstractINodeDiff#posteriorDiff} reference. The  * current directory state is stored in the field of {@link INode}.  * The snapshot state can be obtained by applying the diffs one-by-one in  * reversed chronological order.  Let s_1, s_2, ..., s_n be the corresponding  * snapshots.  Then,  *<pre>  *   s_n                     = (current state) - d_n;  *   s_{n-1} = s_n - d_{n-1} = (current state) - d_n - d_{n-1};  *   ...  *   s_k     = s_{k+1} - d_k = (current state) - d_n - d_{n-1} - ... - d_k.  *</pre>  */
end_comment

begin_class
DECL|class|AbstractINodeDiff
specifier|abstract
class|class
name|AbstractINodeDiff
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
name|Comparable
argument_list|<
name|Integer
argument_list|>
block|{
comment|/** The id of the corresponding snapshot. */
DECL|field|snapshotId
specifier|private
name|int
name|snapshotId
decl_stmt|;
comment|/** The snapshot inode data.  It is null when there is no change. */
DECL|field|snapshotINode
name|A
name|snapshotINode
decl_stmt|;
comment|/**    * Posterior diff is the diff happened after this diff.    * The posterior diff should be first applied to obtain the posterior    * snapshot and then apply this diff in order to obtain this snapshot.    * If the posterior diff is null, the posterior state is the current state.     */
DECL|field|posteriorDiff
specifier|private
name|D
name|posteriorDiff
decl_stmt|;
DECL|method|AbstractINodeDiff (int snapshotId, A snapshotINode, D posteriorDiff)
name|AbstractINodeDiff
parameter_list|(
name|int
name|snapshotId
parameter_list|,
name|A
name|snapshotINode
parameter_list|,
name|D
name|posteriorDiff
parameter_list|)
block|{
name|this
operator|.
name|snapshotId
operator|=
name|snapshotId
expr_stmt|;
name|this
operator|.
name|snapshotINode
operator|=
name|snapshotINode
expr_stmt|;
name|this
operator|.
name|posteriorDiff
operator|=
name|posteriorDiff
expr_stmt|;
block|}
comment|/** Compare diffs with snapshot ID. */
annotation|@
name|Override
DECL|method|compareTo (final Integer that)
specifier|public
specifier|final
name|int
name|compareTo
parameter_list|(
specifier|final
name|Integer
name|that
parameter_list|)
block|{
return|return
name|Snapshot
operator|.
name|ID_INTEGER_COMPARATOR
operator|.
name|compare
argument_list|(
name|this
operator|.
name|snapshotId
argument_list|,
name|that
argument_list|)
return|;
block|}
comment|/** @return the snapshot object of this diff. */
DECL|method|getSnapshotId ()
specifier|public
specifier|final
name|int
name|getSnapshotId
parameter_list|()
block|{
return|return
name|snapshotId
return|;
block|}
DECL|method|setSnapshotId (int snapshot)
specifier|final
name|void
name|setSnapshotId
parameter_list|(
name|int
name|snapshot
parameter_list|)
block|{
name|this
operator|.
name|snapshotId
operator|=
name|snapshot
expr_stmt|;
block|}
comment|/** @return the posterior diff. */
DECL|method|getPosterior ()
specifier|final
name|D
name|getPosterior
parameter_list|()
block|{
return|return
name|posteriorDiff
return|;
block|}
DECL|method|setPosterior (D posterior)
specifier|final
name|void
name|setPosterior
parameter_list|(
name|D
name|posterior
parameter_list|)
block|{
name|posteriorDiff
operator|=
name|posterior
expr_stmt|;
block|}
comment|/** Save the INode state to the snapshot if it is not done already. */
DECL|method|saveSnapshotCopy (A snapshotCopy)
name|void
name|saveSnapshotCopy
parameter_list|(
name|A
name|snapshotCopy
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|snapshotINode
operator|==
literal|null
argument_list|,
literal|"Expected snapshotINode to be null"
argument_list|)
expr_stmt|;
name|snapshotINode
operator|=
name|snapshotCopy
expr_stmt|;
block|}
comment|/** @return the inode corresponding to the snapshot. */
DECL|method|getSnapshotINode ()
name|A
name|getSnapshotINode
parameter_list|()
block|{
comment|// get from this diff, then the posterior diff
comment|// and then null for the current inode
for|for
control|(
name|AbstractINodeDiff
argument_list|<
name|N
argument_list|,
name|A
argument_list|,
name|D
argument_list|>
name|d
init|=
name|this
init|;
condition|;
name|d
operator|=
name|d
operator|.
name|posteriorDiff
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
return|return
name|d
operator|.
name|snapshotINode
return|;
block|}
elseif|else
if|if
condition|(
name|d
operator|.
name|posteriorDiff
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
comment|/** Combine the posterior diff and collect blocks for deletion. */
DECL|method|combinePosteriorAndCollectBlocks ( INode.ReclaimContext reclaimContext, final N currentINode, final D posterior)
specifier|abstract
name|void
name|combinePosteriorAndCollectBlocks
parameter_list|(
name|INode
operator|.
name|ReclaimContext
name|reclaimContext
parameter_list|,
specifier|final
name|N
name|currentINode
parameter_list|,
specifier|final
name|D
name|posterior
parameter_list|)
function_decl|;
comment|/**    * Delete and clear self.    * @param reclaimContext blocks and inodes that need to be reclaimed    * @param currentINode The inode where the deletion happens.    */
DECL|method|destroyDiffAndCollectBlocks (INode.ReclaimContext reclaimContext, final N currentINode)
specifier|abstract
name|void
name|destroyDiffAndCollectBlocks
parameter_list|(
name|INode
operator|.
name|ReclaimContext
name|reclaimContext
parameter_list|,
specifier|final
name|N
name|currentINode
parameter_list|)
function_decl|;
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
name|this
operator|.
name|getSnapshotId
argument_list|()
operator|+
literal|" (post="
operator|+
operator|(
name|posteriorDiff
operator|==
literal|null
condition|?
literal|null
else|:
name|posteriorDiff
operator|.
name|getSnapshotId
argument_list|()
operator|)
operator|+
literal|")"
return|;
block|}
DECL|method|writeSnapshot (DataOutput out)
name|void
name|writeSnapshot
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
block|}
DECL|method|write (DataOutput out, ReferenceMap referenceMap )
specifier|abstract
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|ReferenceMap
name|referenceMap
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

