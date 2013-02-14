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
name|server
operator|.
name|blockmanagement
operator|.
name|DatanodeDescriptor
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
name|INodeFileUnderConstruction
import|;
end_import

begin_comment
comment|/**  * Represent an {@link INodeFileUnderConstruction} that is snapshotted.  * Note that snapshot files are represented by  * {@link INodeFileUnderConstructionSnapshot}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeFileUnderConstructionWithSnapshot
specifier|public
class|class
name|INodeFileUnderConstructionWithSnapshot
extends|extends
name|INodeFileUnderConstruction
implements|implements
name|FileWithSnapshot
block|{
comment|/**    * Factory for {@link INodeFileUnderConstruction} diff.    */
DECL|class|FileUcDiffFactory
specifier|static
class|class
name|FileUcDiffFactory
extends|extends
name|FileDiffFactory
block|{
DECL|field|INSTANCE
specifier|static
specifier|final
name|FileUcDiffFactory
name|INSTANCE
init|=
operator|new
name|FileUcDiffFactory
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|createSnapshotCopy (INodeFile file)
name|INodeFileUnderConstruction
name|createSnapshotCopy
parameter_list|(
name|INodeFile
name|file
parameter_list|)
block|{
specifier|final
name|INodeFileUnderConstruction
name|uc
init|=
operator|(
name|INodeFileUnderConstruction
operator|)
name|file
decl_stmt|;
specifier|final
name|INodeFileUnderConstruction
name|copy
init|=
operator|new
name|INodeFileUnderConstruction
argument_list|(
name|uc
argument_list|,
name|uc
operator|.
name|getClientName
argument_list|()
argument_list|,
name|uc
operator|.
name|getClientMachine
argument_list|()
argument_list|,
name|uc
operator|.
name|getClientNode
argument_list|()
argument_list|)
decl_stmt|;
name|copy
operator|.
name|setBlocks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
block|}
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
DECL|method|INodeFileUnderConstructionWithSnapshot (final INodeFile f, final String clientName, final String clientMachine, final DatanodeDescriptor clientNode, final FileDiffList diffs)
name|INodeFileUnderConstructionWithSnapshot
parameter_list|(
specifier|final
name|INodeFile
name|f
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|String
name|clientMachine
parameter_list|,
specifier|final
name|DatanodeDescriptor
name|clientNode
parameter_list|,
specifier|final
name|FileDiffList
name|diffs
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
name|clientNode
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|diffs
operator|.
name|setFactory
argument_list|(
name|FileUcDiffFactory
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct an {@link INodeFileUnderConstructionWithSnapshot} based on an    * {@link INodeFileUnderConstruction}.    *     * @param f The given {@link INodeFileUnderConstruction} instance    */
DECL|method|INodeFileUnderConstructionWithSnapshot (INodeFileUnderConstruction f, final FileDiffList diffs)
specifier|public
name|INodeFileUnderConstructionWithSnapshot
parameter_list|(
name|INodeFileUnderConstruction
name|f
parameter_list|,
specifier|final
name|FileDiffList
name|diffs
parameter_list|)
block|{
name|this
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|getClientName
argument_list|()
argument_list|,
name|f
operator|.
name|getClientMachine
argument_list|()
argument_list|,
name|f
operator|.
name|getClientNode
argument_list|()
argument_list|,
name|diffs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toINodeFile (final long mtime)
specifier|protected
name|INodeFileWithSnapshot
name|toINodeFile
parameter_list|(
specifier|final
name|long
name|mtime
parameter_list|)
block|{
name|assertAllBlocksComplete
argument_list|()
expr_stmt|;
specifier|final
name|long
name|atime
init|=
name|getModificationTime
argument_list|()
decl_stmt|;
specifier|final
name|INodeFileWithSnapshot
name|f
init|=
operator|new
name|INodeFileWithSnapshot
argument_list|(
name|this
argument_list|,
name|getDiffs
argument_list|()
argument_list|)
decl_stmt|;
name|f
operator|.
name|setModificationTime
argument_list|(
name|mtime
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|setAccessTime
argument_list|(
name|atime
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|getSnapshotINode (Snapshot snapshot)
specifier|public
name|INodeFile
name|getSnapshotINode
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|diffs
operator|.
name|getSnapshotINode
argument_list|(
name|snapshot
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|recordModification ( final Snapshot latest)
specifier|public
name|INodeFileUnderConstructionWithSnapshot
name|recordModification
parameter_list|(
specifier|final
name|Snapshot
name|latest
parameter_list|)
block|{
if|if
condition|(
name|isInLatestSnapshot
argument_list|(
name|latest
argument_list|)
condition|)
block|{
name|diffs
operator|.
name|saveSelf2Snapshot
argument_list|(
name|latest
argument_list|,
name|this
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|asINodeFile ()
specifier|public
name|INodeFile
name|asINodeFile
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|getBlockReplication ()
specifier|public
name|short
name|getBlockReplication
parameter_list|()
block|{
return|return
name|Util
operator|.
name|getBlockReplication
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeFileSize (boolean includesBlockInfoUnderConstruction, Snapshot snapshot)
specifier|public
name|long
name|computeFileSize
parameter_list|(
name|boolean
name|includesBlockInfoUnderConstruction
parameter_list|,
name|Snapshot
name|snapshot
parameter_list|)
block|{
specifier|final
name|FileDiff
name|diff
init|=
name|diffs
operator|.
name|getDiff
argument_list|(
name|snapshot
argument_list|)
decl_stmt|;
return|return
name|diff
operator|!=
literal|null
condition|?
name|diff
operator|.
name|fileSize
else|:
name|super
operator|.
name|computeFileSize
argument_list|(
name|includesBlockInfoUnderConstruction
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|destroySubtreeAndCollectBlocks (final Snapshot snapshot, final BlocksMapUpdateInfo collectedBlocks)
specifier|public
name|int
name|destroySubtreeAndCollectBlocks
parameter_list|(
specifier|final
name|Snapshot
name|snapshot
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|==
literal|null
condition|)
block|{
name|isCurrentFileDeleted
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|diffs
operator|.
name|deleteSnapshotDiff
argument_list|(
name|snapshot
argument_list|,
name|this
argument_list|,
name|collectedBlocks
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|//snapshot diff not found and nothing is deleted.
return|return
literal|0
return|;
block|}
block|}
name|Util
operator|.
name|collectBlocksAndClear
argument_list|(
name|this
argument_list|,
name|collectedBlocks
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|toDetailString ()
specifier|public
name|String
name|toDetailString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toDetailString
argument_list|()
operator|+
operator|(
name|isCurrentFileDeleted
argument_list|()
condition|?
literal|" (DELETED), "
else|:
literal|", "
operator|)
operator|+
name|diffs
return|;
block|}
block|}
end_class

end_unit

