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
DECL|field|next
specifier|private
name|FileWithSnapshot
name|next
decl_stmt|;
DECL|method|INodeFileUnderConstructionWithSnapshot (final FileWithSnapshot f, final String clientName, final String clientMachine, final DatanodeDescriptor clientNode)
name|INodeFileUnderConstructionWithSnapshot
parameter_list|(
specifier|final
name|FileWithSnapshot
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
parameter_list|)
block|{
name|super
argument_list|(
name|f
operator|.
name|asINodeFile
argument_list|()
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
name|clientNode
argument_list|)
expr_stmt|;
block|}
comment|/**    * The constructor that creates an    * {@link INodeFileUnderConstructionWithSnapshot} based on an    * {@link INodeFileUnderConstruction}    *     * @param child The given {@link INodeFileUnderConstruction} instance    */
DECL|method|INodeFileUnderConstructionWithSnapshot ( INodeFileUnderConstruction child)
specifier|public
name|INodeFileUnderConstructionWithSnapshot
parameter_list|(
name|INodeFileUnderConstruction
name|child
parameter_list|)
block|{
name|super
argument_list|(
name|child
argument_list|,
name|child
operator|.
name|getClientName
argument_list|()
argument_list|,
name|child
operator|.
name|getClientMachine
argument_list|()
argument_list|,
name|child
operator|.
name|getClientNode
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|=
name|this
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
comment|// link f with this
name|this
operator|.
name|insertBefore
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
annotation|@
name|Override
specifier|public
name|Pair
argument_list|<
name|?
extends|extends
name|INodeFileUnderConstruction
argument_list|,
DECL|method|createSnapshotCopy ()
name|INodeFileUnderConstructionSnapshot
argument_list|>
name|createSnapshotCopy
parameter_list|()
block|{
return|return
operator|new
name|Pair
argument_list|<
name|INodeFileUnderConstructionWithSnapshot
argument_list|,
name|INodeFileUnderConstructionSnapshot
argument_list|>
argument_list|(
name|this
argument_list|,
operator|new
name|INodeFileUnderConstructionSnapshot
argument_list|(
name|this
argument_list|)
argument_list|)
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
DECL|method|getNext ()
specifier|public
name|FileWithSnapshot
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
annotation|@
name|Override
DECL|method|setNext (FileWithSnapshot next)
specifier|public
name|void
name|setNext
parameter_list|(
name|FileWithSnapshot
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|insertAfter (FileWithSnapshot inode)
specifier|public
name|void
name|insertAfter
parameter_list|(
name|FileWithSnapshot
name|inode
parameter_list|)
block|{
name|inode
operator|.
name|setNext
argument_list|(
name|this
operator|.
name|getNext
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNext
argument_list|(
name|inode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|insertBefore (FileWithSnapshot inode)
specifier|public
name|void
name|insertBefore
parameter_list|(
name|FileWithSnapshot
name|inode
parameter_list|)
block|{
name|inode
operator|.
name|setNext
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|next
operator|==
literal|null
operator|||
name|this
operator|.
name|next
operator|==
name|this
condition|)
block|{
name|this
operator|.
name|next
operator|=
name|inode
expr_stmt|;
return|return;
block|}
name|FileWithSnapshot
name|previous
init|=
name|Util
operator|.
name|getPrevious
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|previous
operator|.
name|setNext
argument_list|(
name|inode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeSelf ()
specifier|public
name|void
name|removeSelf
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|next
operator|!=
literal|null
operator|&&
name|this
operator|.
name|next
operator|!=
name|this
condition|)
block|{
name|FileWithSnapshot
name|previous
init|=
name|Util
operator|.
name|getPrevious
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|previous
operator|.
name|setNext
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|next
operator|=
literal|null
expr_stmt|;
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
DECL|method|collectSubtreeBlocksAndClear (BlocksMapUpdateInfo info)
specifier|public
name|int
name|collectSubtreeBlocksAndClear
parameter_list|(
name|BlocksMapUpdateInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|next
operator|==
literal|null
operator|||
name|next
operator|==
name|this
condition|)
block|{
comment|// this is the only remaining inode.
return|return
name|super
operator|.
name|collectSubtreeBlocksAndClear
argument_list|(
name|info
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Util
operator|.
name|collectSubtreeBlocksAndClear
argument_list|(
name|this
argument_list|,
name|info
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

