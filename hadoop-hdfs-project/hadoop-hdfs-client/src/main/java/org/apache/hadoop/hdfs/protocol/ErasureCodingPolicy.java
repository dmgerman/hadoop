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
name|commons
operator|.
name|lang
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
name|lang
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
import|;
end_import

begin_comment
comment|/**  * A policy about how to write/read/code an erasure coding file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ErasureCodingPolicy
specifier|public
specifier|final
class|class
name|ErasureCodingPolicy
block|{
DECL|field|schema
specifier|private
specifier|final
name|ECSchema
name|schema
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|id
specifier|private
name|byte
name|id
decl_stmt|;
DECL|method|ErasureCodingPolicy (String name, ECSchema schema, int cellSize, byte id)
specifier|public
name|ErasureCodingPolicy
parameter_list|(
name|String
name|name
parameter_list|,
name|ECSchema
name|schema
parameter_list|,
name|int
name|cellSize
parameter_list|,
name|byte
name|id
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|schema
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|cellSize
operator|>
literal|0
argument_list|,
literal|"cellSize must be positive"
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|cellSize
operator|=
name|cellSize
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|ErasureCodingPolicy (ECSchema schema, int cellSize, byte id)
specifier|public
name|ErasureCodingPolicy
parameter_list|(
name|ECSchema
name|schema
parameter_list|,
name|int
name|cellSize
parameter_list|,
name|byte
name|id
parameter_list|)
block|{
name|this
argument_list|(
name|composePolicyName
argument_list|(
name|schema
argument_list|,
name|cellSize
argument_list|)
argument_list|,
name|schema
argument_list|,
name|cellSize
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
DECL|method|composePolicyName (ECSchema schema, int cellSize)
specifier|public
specifier|static
name|String
name|composePolicyName
parameter_list|(
name|ECSchema
name|schema
parameter_list|,
name|int
name|cellSize
parameter_list|)
block|{
assert|assert
name|cellSize
operator|%
literal|1024
operator|==
literal|0
assert|;
return|return
name|schema
operator|.
name|getCodecName
argument_list|()
operator|.
name|toUpperCase
argument_list|()
operator|+
literal|"-"
operator|+
name|schema
operator|.
name|getNumDataUnits
argument_list|()
operator|+
literal|"-"
operator|+
name|schema
operator|.
name|getNumParityUnits
argument_list|()
operator|+
literal|"-"
operator|+
name|cellSize
operator|/
literal|1024
operator|+
literal|"k"
return|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getSchema ()
specifier|public
name|ECSchema
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
DECL|method|getCellSize ()
specifier|public
name|int
name|getCellSize
parameter_list|()
block|{
return|return
name|cellSize
return|;
block|}
DECL|method|getNumDataUnits ()
specifier|public
name|int
name|getNumDataUnits
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getNumDataUnits
argument_list|()
return|;
block|}
DECL|method|getNumParityUnits ()
specifier|public
name|int
name|getNumParityUnits
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getNumParityUnits
argument_list|()
return|;
block|}
DECL|method|getCodecName ()
specifier|public
name|String
name|getCodecName
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getCodecName
argument_list|()
return|;
block|}
DECL|method|getId ()
specifier|public
name|byte
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|setId (byte id)
specifier|public
name|void
name|setId
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
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
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ErasureCodingPolicy
name|rhs
init|=
operator|(
name|ErasureCodingPolicy
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
name|name
argument_list|,
name|rhs
operator|.
name|name
argument_list|)
operator|.
name|append
argument_list|(
name|schema
argument_list|,
name|rhs
operator|.
name|schema
argument_list|)
operator|.
name|append
argument_list|(
name|cellSize
argument_list|,
name|rhs
operator|.
name|cellSize
argument_list|)
operator|.
name|append
argument_list|(
name|id
argument_list|,
name|rhs
operator|.
name|id
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
argument_list|(
literal|303855623
argument_list|,
literal|582626729
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
name|schema
argument_list|)
operator|.
name|append
argument_list|(
name|cellSize
argument_list|)
operator|.
name|append
argument_list|(
name|id
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
literal|"ErasureCodingPolicy=["
operator|+
literal|"Name="
operator|+
name|name
operator|+
literal|", "
operator|+
literal|"Schema=["
operator|+
name|schema
operator|.
name|toString
argument_list|()
operator|+
literal|"], "
operator|+
literal|"CellSize="
operator|+
name|cellSize
operator|+
literal|", "
operator|+
literal|"Id="
operator|+
name|id
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

