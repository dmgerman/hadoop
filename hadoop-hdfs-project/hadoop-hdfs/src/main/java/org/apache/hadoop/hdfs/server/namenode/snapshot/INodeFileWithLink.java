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
name|namenode
operator|.
name|INodeFile
import|;
end_import

begin_comment
comment|/**  * INodeFile with a link to the next element.  * This class is used to represent the original file that is snapshotted.  * The snapshot files are represented by {@link INodeFileSnapshot}.  * The link of all the snapshot files and the original file form a circular  * linked list so that all elements are accessible by any of the elements.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeFileWithLink
specifier|public
class|class
name|INodeFileWithLink
extends|extends
name|INodeFile
block|{
DECL|field|next
specifier|private
name|INodeFileWithLink
name|next
decl_stmt|;
DECL|method|INodeFileWithLink (INodeFile f)
specifier|public
name|INodeFileWithLink
parameter_list|(
name|INodeFile
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|next
operator|=
name|this
expr_stmt|;
block|}
DECL|method|setNext (INodeFileWithLink next)
name|void
name|setNext
parameter_list|(
name|INodeFileWithLink
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
DECL|method|getNext ()
name|INodeFileWithLink
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
comment|/** Insert inode to the circular linked list. */
DECL|method|insert (INodeFileWithLink inode)
specifier|public
name|void
name|insert
parameter_list|(
name|INodeFileWithLink
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
comment|/**    * @return the max file replication of the elements    *         in the circular linked list.    */
annotation|@
name|Override
DECL|method|getBlockReplication ()
specifier|public
name|short
name|getBlockReplication
parameter_list|()
block|{
name|short
name|max
init|=
name|getFileReplication
argument_list|()
decl_stmt|;
for|for
control|(
name|INodeFileWithLink
name|i
init|=
name|next
init|;
name|i
operator|!=
name|this
condition|;
name|i
operator|=
name|i
operator|.
name|getNext
argument_list|()
control|)
block|{
specifier|final
name|short
name|replication
init|=
name|i
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
return|return
name|max
return|;
block|}
comment|/**    * {@inheritDoc}    *     * Remove the current inode from the circular linked list.    * If some blocks at the end of the block list no longer belongs to    * any other inode, collect them and update the block list.    */
annotation|@
name|Override
DECL|method|collectSubtreeBlocksAndClear (List<Block> v)
specifier|protected
name|int
name|collectSubtreeBlocksAndClear
parameter_list|(
name|List
argument_list|<
name|Block
argument_list|>
name|v
parameter_list|)
block|{
if|if
condition|(
name|next
operator|==
name|this
condition|)
block|{
comment|//this is the only remaining inode.
name|super
operator|.
name|collectSubtreeBlocksAndClear
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//There are other inode(s) using the blocks.
comment|//Compute max file size excluding this and find the last inode.
name|long
name|max
init|=
name|next
operator|.
name|computeFileSize
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|INodeFileWithLink
name|last
init|=
name|next
decl_stmt|;
for|for
control|(
name|INodeFileWithLink
name|i
init|=
name|next
operator|.
name|getNext
argument_list|()
init|;
name|i
operator|!=
name|this
condition|;
name|i
operator|=
name|i
operator|.
name|getNext
argument_list|()
control|)
block|{
specifier|final
name|long
name|size
init|=
name|i
operator|.
name|computeFileSize
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|size
expr_stmt|;
block|}
name|last
operator|=
name|i
expr_stmt|;
block|}
name|collectBlocksBeyondMaxAndClear
argument_list|(
name|max
argument_list|,
name|v
argument_list|)
expr_stmt|;
comment|//remove this from the circular linked list.
name|last
operator|.
name|next
operator|=
name|this
operator|.
name|next
expr_stmt|;
name|this
operator|.
name|next
operator|=
literal|null
expr_stmt|;
comment|//clear parent
name|parent
operator|=
literal|null
expr_stmt|;
block|}
return|return
literal|1
return|;
block|}
DECL|method|collectBlocksBeyondMaxAndClear (final long max, final List<Block> v)
specifier|private
name|void
name|collectBlocksBeyondMaxAndClear
parameter_list|(
specifier|final
name|long
name|max
parameter_list|,
specifier|final
name|List
argument_list|<
name|Block
argument_list|>
name|v
parameter_list|)
block|{
specifier|final
name|BlockInfo
index|[]
name|oldBlocks
init|=
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
comment|//starting from block n, the data is beyond max.
if|if
condition|(
name|n
operator|<
name|oldBlocks
operator|.
name|length
condition|)
block|{
comment|//resize the array.
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
for|for
control|(
name|INodeFileWithLink
name|i
init|=
name|next
init|;
name|i
operator|!=
name|this
condition|;
name|i
operator|=
name|i
operator|.
name|getNext
argument_list|()
control|)
block|{
name|i
operator|.
name|setBlocks
argument_list|(
name|newBlocks
argument_list|)
expr_stmt|;
block|}
comment|//collect the blocks beyond max.
if|if
condition|(
name|v
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
name|v
operator|.
name|add
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
name|setBlocks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

