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
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A policy about how to write/read/code an erasure coding file.  */
end_comment

begin_class
DECL|class|ErasureCodingPolicy
specifier|public
specifier|final
class|class
name|ErasureCodingPolicy
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
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
DECL|method|ErasureCodingPolicy (String name, ECSchema schema, int cellSize)
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
parameter_list|)
block|{
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
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ErasureCodingPolicy
name|that
init|=
operator|(
name|ErasureCodingPolicy
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|that
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|that
operator|.
name|getCellSize
argument_list|()
operator|==
name|cellSize
operator|&&
name|that
operator|.
name|getSchema
argument_list|()
operator|.
name|equals
argument_list|(
name|schema
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
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
name|int
name|result
init|=
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|schema
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|cellSize
expr_stmt|;
return|return
name|result
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ErasureCodingPolicy=["
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Name="
operator|+
name|name
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Schema=["
operator|+
name|schema
operator|.
name|toString
argument_list|()
operator|+
literal|"], "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"CellSize="
operator|+
name|cellSize
operator|+
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

