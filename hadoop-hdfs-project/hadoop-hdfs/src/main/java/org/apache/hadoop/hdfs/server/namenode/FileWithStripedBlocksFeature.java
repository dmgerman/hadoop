begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
package|;
end_package

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
name|BlockInfoStriped
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
name|BlockInfoStripedUnderConstruction
import|;
end_import

begin_comment
comment|/**  * Feature for file with striped blocks  */
end_comment

begin_class
DECL|class|FileWithStripedBlocksFeature
class|class
name|FileWithStripedBlocksFeature
implements|implements
name|INode
operator|.
name|Feature
block|{
DECL|field|blocks
specifier|private
name|BlockInfoStriped
index|[]
name|blocks
decl_stmt|;
DECL|method|FileWithStripedBlocksFeature ()
name|FileWithStripedBlocksFeature
parameter_list|()
block|{
name|blocks
operator|=
operator|new
name|BlockInfoStriped
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|FileWithStripedBlocksFeature (BlockInfoStriped[] blocks)
name|FileWithStripedBlocksFeature
parameter_list|(
name|BlockInfoStriped
index|[]
name|blocks
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|blocks
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
DECL|method|getBlocks ()
name|BlockInfoStriped
index|[]
name|getBlocks
parameter_list|()
block|{
return|return
name|this
operator|.
name|blocks
return|;
block|}
DECL|method|setBlock (int index, BlockInfoStriped blk)
name|void
name|setBlock
parameter_list|(
name|int
name|index
parameter_list|,
name|BlockInfoStriped
name|blk
parameter_list|)
block|{
name|blocks
index|[
name|index
index|]
operator|=
name|blk
expr_stmt|;
block|}
DECL|method|getLastBlock ()
name|BlockInfoStriped
name|getLastBlock
parameter_list|()
block|{
return|return
name|blocks
operator|==
literal|null
operator|||
name|blocks
operator|.
name|length
operator|==
literal|0
condition|?
literal|null
else|:
name|blocks
index|[
name|blocks
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
DECL|method|numBlocks ()
name|int
name|numBlocks
parameter_list|()
block|{
return|return
name|blocks
operator|==
literal|null
condition|?
literal|0
else|:
name|blocks
operator|.
name|length
return|;
block|}
DECL|method|updateBlockCollection (INodeFile file)
name|void
name|updateBlockCollection
parameter_list|(
name|INodeFile
name|file
parameter_list|)
block|{
if|if
condition|(
name|blocks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|BlockInfoStriped
name|blk
range|:
name|blocks
control|)
block|{
name|blk
operator|.
name|setBlockCollection
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setBlocks (BlockInfoStriped[] blocks)
specifier|private
name|void
name|setBlocks
parameter_list|(
name|BlockInfoStriped
index|[]
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
DECL|method|addBlock (BlockInfoStriped newBlock)
name|void
name|addBlock
parameter_list|(
name|BlockInfoStriped
name|newBlock
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|blocks
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|setBlocks
argument_list|(
operator|new
name|BlockInfoStriped
index|[]
block|{
name|newBlock
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|size
init|=
name|this
operator|.
name|blocks
operator|.
name|length
decl_stmt|;
name|BlockInfoStriped
index|[]
name|newlist
init|=
operator|new
name|BlockInfoStriped
index|[
name|size
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|blocks
argument_list|,
literal|0
argument_list|,
name|newlist
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|newlist
index|[
name|size
index|]
operator|=
name|newBlock
expr_stmt|;
name|this
operator|.
name|setBlocks
argument_list|(
name|newlist
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeLastBlock ( Block oldblock)
name|BlockInfoStripedUnderConstruction
name|removeLastBlock
parameter_list|(
name|Block
name|oldblock
parameter_list|)
block|{
if|if
condition|(
name|blocks
operator|==
literal|null
operator|||
name|blocks
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|newSize
init|=
name|blocks
operator|.
name|length
operator|-
literal|1
decl_stmt|;
if|if
condition|(
operator|!
name|blocks
index|[
name|newSize
index|]
operator|.
name|equals
argument_list|(
name|oldblock
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|BlockInfoStripedUnderConstruction
name|uc
init|=
operator|(
name|BlockInfoStripedUnderConstruction
operator|)
name|blocks
index|[
name|newSize
index|]
decl_stmt|;
comment|//copy to a new list
name|BlockInfoStriped
index|[]
name|newlist
init|=
operator|new
name|BlockInfoStriped
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
argument_list|,
literal|0
argument_list|,
name|newlist
argument_list|,
literal|0
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|setBlocks
argument_list|(
name|newlist
argument_list|)
expr_stmt|;
return|return
name|uc
return|;
block|}
DECL|method|truncateStripedBlocks (int n)
name|void
name|truncateStripedBlocks
parameter_list|(
name|int
name|n
parameter_list|)
block|{
specifier|final
name|BlockInfoStriped
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
operator|new
name|BlockInfoStriped
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|newBlocks
operator|=
operator|new
name|BlockInfoStriped
index|[
name|n
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|getBlocks
argument_list|()
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
name|setBlocks
argument_list|(
name|newBlocks
argument_list|)
expr_stmt|;
block|}
DECL|method|clear ()
name|void
name|clear
parameter_list|()
block|{
name|this
operator|.
name|blocks
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

