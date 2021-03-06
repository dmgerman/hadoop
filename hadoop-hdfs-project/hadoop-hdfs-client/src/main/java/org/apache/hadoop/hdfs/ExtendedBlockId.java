begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|EqualsBuilder
import|;
end_import

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
name|hdfs
operator|.
name|protocol
operator|.
name|ExtendedBlock
import|;
end_import

begin_comment
comment|/**  * An immutable key which identifies a block.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ExtendedBlockId
specifier|final
specifier|public
class|class
name|ExtendedBlockId
block|{
comment|/**    * The block ID for this block.    */
DECL|field|blockId
specifier|private
specifier|final
name|long
name|blockId
decl_stmt|;
comment|/**    * The block pool ID for this block.    */
DECL|field|bpId
specifier|private
specifier|final
name|String
name|bpId
decl_stmt|;
DECL|method|fromExtendedBlock (ExtendedBlock block)
specifier|public
specifier|static
name|ExtendedBlockId
name|fromExtendedBlock
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
block|{
return|return
operator|new
name|ExtendedBlockId
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|ExtendedBlockId (long blockId, String bpId)
specifier|public
name|ExtendedBlockId
parameter_list|(
name|long
name|blockId
parameter_list|,
name|String
name|bpId
parameter_list|)
block|{
name|this
operator|.
name|blockId
operator|=
name|blockId
expr_stmt|;
name|this
operator|.
name|bpId
operator|=
name|bpId
expr_stmt|;
block|}
DECL|method|getBlockId ()
specifier|public
name|long
name|getBlockId
parameter_list|()
block|{
return|return
name|this
operator|.
name|blockId
return|;
block|}
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
block|{
return|return
name|this
operator|.
name|bpId
return|;
block|}
annotation|@
name|Override
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
operator|(
name|o
operator|==
literal|null
operator|)
operator|||
operator|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ExtendedBlockId
name|other
init|=
operator|(
name|ExtendedBlockId
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|blockId
argument_list|,
name|other
operator|.
name|blockId
argument_list|)
operator|.
name|append
argument_list|(
name|bpId
argument_list|,
name|other
operator|.
name|bpId
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|this
operator|.
name|blockId
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|bpId
argument_list|)
operator|.
name|toHashCode
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
name|String
operator|.
name|valueOf
argument_list|(
name|blockId
argument_list|)
operator|+
literal|"_"
operator|+
name|bpId
return|;
block|}
block|}
end_class

end_unit

