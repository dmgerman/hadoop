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
name|INodeMap
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
comment|/**  * Represent an {@link INodeFile} that is snapshotted.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeFileWithSnapshot
specifier|public
class|class
name|INodeFileWithSnapshot
extends|extends
name|INodeFile
implements|implements
name|FileWithSnapshot
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
DECL|method|INodeFileWithSnapshot (INodeFile f)
specifier|public
name|INodeFileWithSnapshot
parameter_list|(
name|INodeFile
name|f
parameter_list|)
block|{
name|this
argument_list|(
name|f
argument_list|,
name|f
operator|instanceof
name|FileWithSnapshot
condition|?
operator|(
operator|(
name|FileWithSnapshot
operator|)
name|f
operator|)
operator|.
name|getDiffs
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|INodeFileWithSnapshot (INodeFile f, FileDiffList diffs)
specifier|public
name|INodeFileWithSnapshot
parameter_list|(
name|INodeFile
name|f
parameter_list|,
name|FileDiffList
name|diffs
parameter_list|)
block|{
name|super
argument_list|(
name|f
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
block|}
annotation|@
name|Override
DECL|method|toUnderConstruction ( final String clientName, final String clientMachine, final DatanodeDescriptor clientNode)
specifier|public
name|INodeFileUnderConstructionWithSnapshot
name|toUnderConstruction
parameter_list|(
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
parameter_list|)
block|{
return|return
operator|new
name|INodeFileUnderConstructionWithSnapshot
argument_list|(
name|this
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
name|clientNode
argument_list|,
name|getDiffs
argument_list|()
argument_list|)
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
annotation|@
name|Override
DECL|method|getSnapshotINode (Snapshot snapshot)
specifier|public
name|INodeFileAttributes
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
DECL|method|recordModification (final Snapshot latest, final INodeMap inodeMap)
specifier|public
name|INodeFileWithSnapshot
name|recordModification
parameter_list|(
specifier|final
name|Snapshot
name|latest
parameter_list|,
specifier|final
name|INodeMap
name|inodeMap
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
if|if
condition|(
name|isInLatestSnapshot
argument_list|(
name|latest
argument_list|)
operator|&&
operator|!
name|shouldRecordInSrcSnapshot
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
DECL|method|cleanSubtree (final Snapshot snapshot, Snapshot prior, final BlocksMapUpdateInfo collectedBlocks, final List<INode> removedINodes, final boolean countDiffChange)
specifier|public
name|Quota
operator|.
name|Counts
name|cleanSubtree
parameter_list|(
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
comment|// delete the current file
if|if
condition|(
operator|!
name|isCurrentFileDeleted
argument_list|()
condition|)
block|{
name|recordModification
argument_list|(
name|prior
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|deleteCurrentFile
argument_list|()
expr_stmt|;
block|}
name|Util
operator|.
name|collectBlocksAndClear
argument_list|(
name|this
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
comment|// delete a snapshot
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
name|this
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
literal|"(DELETED), "
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

