begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.wsrs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|wsrs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|EnumSetParam
specifier|public
specifier|abstract
class|class
name|EnumSetParam
parameter_list|<
name|E
extends|extends
name|Enum
parameter_list|<
name|E
parameter_list|>
parameter_list|>
extends|extends
name|Param
argument_list|<
name|EnumSet
argument_list|<
name|E
argument_list|>
argument_list|>
block|{
DECL|field|klass
name|Class
argument_list|<
name|E
argument_list|>
name|klass
decl_stmt|;
DECL|method|EnumSetParam (String name, Class<E> e, EnumSet<E> defaultValue)
specifier|public
name|EnumSetParam
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|E
argument_list|>
name|e
parameter_list|,
name|EnumSet
argument_list|<
name|E
argument_list|>
name|defaultValue
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|defaultValue
argument_list|)
expr_stmt|;
name|klass
operator|=
name|e
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse (String str)
specifier|protected
name|EnumSet
argument_list|<
name|E
argument_list|>
name|parse
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|EnumSet
argument_list|<
name|E
argument_list|>
name|set
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|klass
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|str
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|sub
range|:
name|str
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|Enum
operator|.
name|valueOf
argument_list|(
name|klass
argument_list|,
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|sub
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|set
return|;
block|}
annotation|@
name|Override
DECL|method|getDomain ()
specifier|protected
name|String
name|getDomain
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|klass
operator|.
name|getEnumConstants
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Convert an EnumSet to a string of comma separated values. */
DECL|method|toString (EnumSet<E> set)
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|String
name|toString
parameter_list|(
name|EnumSet
argument_list|<
name|E
argument_list|>
name|set
parameter_list|)
block|{
if|if
condition|(
name|set
operator|==
literal|null
operator|||
name|set
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|""
return|;
block|}
else|else
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|i
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
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
name|getName
argument_list|()
operator|+
literal|"="
operator|+
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

