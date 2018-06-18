begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Identifies a Block uniquely across the block pools  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ExtendedBlock
specifier|public
class|class
name|ExtendedBlock
block|{
DECL|field|poolId
specifier|private
name|String
name|poolId
decl_stmt|;
DECL|field|block
specifier|private
name|Block
name|block
decl_stmt|;
DECL|method|ExtendedBlock ()
specifier|public
name|ExtendedBlock
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|ExtendedBlock (final ExtendedBlock b)
specifier|public
name|ExtendedBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|b
parameter_list|)
block|{
name|this
argument_list|(
name|b
operator|.
name|poolId
argument_list|,
operator|new
name|Block
argument_list|(
name|b
operator|.
name|block
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ExtendedBlock (final String poolId, final long blockId)
specifier|public
name|ExtendedBlock
parameter_list|(
specifier|final
name|String
name|poolId
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|)
block|{
name|this
argument_list|(
name|poolId
argument_list|,
name|blockId
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|ExtendedBlock (String poolId, Block b)
specifier|public
name|ExtendedBlock
parameter_list|(
name|String
name|poolId
parameter_list|,
name|Block
name|b
parameter_list|)
block|{
name|this
operator|.
name|poolId
operator|=
name|poolId
operator|!=
literal|null
condition|?
name|poolId
operator|.
name|intern
argument_list|()
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|block
operator|=
name|b
expr_stmt|;
block|}
DECL|method|ExtendedBlock (final String poolId, final long blkid, final long len, final long genstamp)
specifier|public
name|ExtendedBlock
parameter_list|(
specifier|final
name|String
name|poolId
parameter_list|,
specifier|final
name|long
name|blkid
parameter_list|,
specifier|final
name|long
name|len
parameter_list|,
specifier|final
name|long
name|genstamp
parameter_list|)
block|{
name|this
operator|.
name|poolId
operator|=
name|poolId
operator|!=
literal|null
condition|?
name|poolId
operator|.
name|intern
argument_list|()
else|:
literal|null
expr_stmt|;
name|block
operator|=
operator|new
name|Block
argument_list|(
name|blkid
argument_list|,
name|len
argument_list|,
name|genstamp
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
block|{
return|return
name|poolId
return|;
block|}
comment|/** Returns the block file name for the block */
DECL|method|getBlockName ()
specifier|public
name|String
name|getBlockName
parameter_list|()
block|{
return|return
name|block
operator|.
name|getBlockName
argument_list|()
return|;
block|}
DECL|method|getNumBytes ()
specifier|public
name|long
name|getNumBytes
parameter_list|()
block|{
return|return
name|block
operator|.
name|getNumBytes
argument_list|()
return|;
block|}
DECL|method|getBlockId ()
specifier|public
name|long
name|getBlockId
parameter_list|()
block|{
return|return
name|block
operator|.
name|getBlockId
argument_list|()
return|;
block|}
DECL|method|getGenerationStamp ()
specifier|public
name|long
name|getGenerationStamp
parameter_list|()
block|{
return|return
name|block
operator|.
name|getGenerationStamp
argument_list|()
return|;
block|}
DECL|method|setBlockId (final long bid)
specifier|public
name|void
name|setBlockId
parameter_list|(
specifier|final
name|long
name|bid
parameter_list|)
block|{
name|block
operator|.
name|setBlockId
argument_list|(
name|bid
argument_list|)
expr_stmt|;
block|}
DECL|method|setGenerationStamp (final long genStamp)
specifier|public
name|void
name|setGenerationStamp
parameter_list|(
specifier|final
name|long
name|genStamp
parameter_list|)
block|{
name|block
operator|.
name|setGenerationStamp
argument_list|(
name|genStamp
argument_list|)
expr_stmt|;
block|}
DECL|method|setNumBytes (final long len)
specifier|public
name|void
name|setNumBytes
parameter_list|(
specifier|final
name|long
name|len
parameter_list|)
block|{
name|block
operator|.
name|setNumBytes
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|set (String poolId, Block blk)
specifier|public
name|void
name|set
parameter_list|(
name|String
name|poolId
parameter_list|,
name|Block
name|blk
parameter_list|)
block|{
name|this
operator|.
name|poolId
operator|=
name|poolId
operator|!=
literal|null
condition|?
name|poolId
operator|.
name|intern
argument_list|()
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|block
operator|=
name|blk
expr_stmt|;
block|}
DECL|method|getLocalBlock (final ExtendedBlock b)
specifier|public
specifier|static
name|Block
name|getLocalBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|b
parameter_list|)
block|{
return|return
name|b
operator|==
literal|null
condition|?
literal|null
else|:
name|b
operator|.
name|getLocalBlock
argument_list|()
return|;
block|}
DECL|method|getLocalBlock ()
specifier|public
name|Block
name|getLocalBlock
parameter_list|()
block|{
return|return
name|block
return|;
block|}
annotation|@
name|Override
comment|// Object
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|ExtendedBlock
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ExtendedBlock
name|b
init|=
operator|(
name|ExtendedBlock
operator|)
name|o
decl_stmt|;
return|return
name|b
operator|.
name|block
operator|.
name|equals
argument_list|(
name|block
argument_list|)
operator|&&
operator|(
name|b
operator|.
name|poolId
operator|!=
literal|null
condition|?
name|b
operator|.
name|poolId
operator|.
name|equals
argument_list|(
name|poolId
argument_list|)
else|:
name|poolId
operator|==
literal|null
operator|)
return|;
block|}
annotation|@
name|Override
comment|// Object
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|(
literal|31
argument_list|,
literal|17
argument_list|)
operator|.
name|append
argument_list|(
name|poolId
argument_list|)
operator|.
name|append
argument_list|(
name|block
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// Object
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|poolId
operator|+
literal|":"
operator|+
name|block
return|;
block|}
block|}
end_class

end_unit

