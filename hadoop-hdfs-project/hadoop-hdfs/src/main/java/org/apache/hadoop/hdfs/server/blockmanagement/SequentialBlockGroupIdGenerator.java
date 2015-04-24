begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|util
operator|.
name|SequentialNumber
import|;
end_import

begin_import
import|import static
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
name|HdfsConstants
operator|.
name|BLOCK_GROUP_INDEX_MASK
import|;
end_import

begin_import
import|import static
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
name|HdfsConstants
operator|.
name|MAX_BLOCKS_IN_GROUP
import|;
end_import

begin_comment
comment|/**  * Generate the next valid block group ID by incrementing the maximum block  * group ID allocated so far, with the first 2^10 block group IDs reserved.  * HDFS-EC introduces a hierarchical protocol to name blocks and groups:  * Contiguous: {reserved block IDs | flag | block ID}  * Striped: {reserved block IDs | flag | block group ID | index in group}  *  * Following n bits of reserved block IDs, The (n+1)th bit in an ID  * distinguishes contiguous (0) and striped (1) blocks. For a striped block,  * bits (n+2) to (64-m) represent the ID of its block group, while the last m  * bits represent its index of the group. The value m is determined by the  * maximum number of blocks in a group (MAX_BLOCKS_IN_GROUP).  *  * Note that the {@link #nextValue()} methods requires external lock to  * guarantee IDs have no conflicts.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SequentialBlockGroupIdGenerator
specifier|public
class|class
name|SequentialBlockGroupIdGenerator
extends|extends
name|SequentialNumber
block|{
DECL|field|blockManager
specifier|private
specifier|final
name|BlockManager
name|blockManager
decl_stmt|;
DECL|method|SequentialBlockGroupIdGenerator (BlockManager blockManagerRef)
name|SequentialBlockGroupIdGenerator
parameter_list|(
name|BlockManager
name|blockManagerRef
parameter_list|)
block|{
name|super
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockManager
operator|=
name|blockManagerRef
expr_stmt|;
block|}
annotation|@
name|Override
comment|// NumberGenerator
DECL|method|nextValue ()
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
name|skipTo
argument_list|(
operator|(
name|getCurrentValue
argument_list|()
operator|&
operator|~
name|BLOCK_GROUP_INDEX_MASK
operator|)
operator|+
name|MAX_BLOCKS_IN_GROUP
argument_list|)
expr_stmt|;
comment|// Make sure there's no conflict with existing random block IDs
specifier|final
name|Block
name|b
init|=
operator|new
name|Block
argument_list|(
name|getCurrentValue
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|hasValidBlockInRange
argument_list|(
name|b
argument_list|)
condition|)
block|{
name|skipTo
argument_list|(
name|getCurrentValue
argument_list|()
operator|+
name|MAX_BLOCKS_IN_GROUP
argument_list|)
expr_stmt|;
name|b
operator|.
name|setBlockId
argument_list|(
name|getCurrentValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|b
operator|.
name|getBlockId
argument_list|()
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"All negative block group IDs are used, "
operator|+
literal|"growing into positive IDs, "
operator|+
literal|"which might conflict with non-erasure coded blocks."
argument_list|)
throw|;
block|}
return|return
name|getCurrentValue
argument_list|()
return|;
block|}
comment|/**    * @param b A block object whose id is set to the starting point for check    * @return true if any ID in the range    *      {id, id+HdfsConstants.MAX_BLOCKS_IN_GROUP} is pointed-to by a file    */
DECL|method|hasValidBlockInRange (Block b)
specifier|private
name|boolean
name|hasValidBlockInRange
parameter_list|(
name|Block
name|b
parameter_list|)
block|{
specifier|final
name|long
name|id
init|=
name|b
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MAX_BLOCKS_IN_GROUP
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|setBlockId
argument_list|(
name|id
operator|+
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|blockManager
operator|.
name|getBlockCollection
argument_list|(
name|b
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

