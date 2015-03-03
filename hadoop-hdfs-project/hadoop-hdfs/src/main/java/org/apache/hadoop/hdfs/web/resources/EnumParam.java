begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.resources
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|resources
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
DECL|class|EnumParam
specifier|abstract
class|class
name|EnumParam
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
name|E
argument_list|,
name|EnumParam
operator|.
name|Domain
argument_list|<
name|E
argument_list|>
argument_list|>
block|{
DECL|method|EnumParam (final Domain<E> domain, final E value)
name|EnumParam
parameter_list|(
specifier|final
name|Domain
argument_list|<
name|E
argument_list|>
name|domain
parameter_list|,
specifier|final
name|E
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|domain
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** The domain of the parameter. */
DECL|class|Domain
specifier|static
specifier|final
class|class
name|Domain
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
operator|.
name|Domain
argument_list|<
name|E
argument_list|>
block|{
DECL|field|enumClass
specifier|private
specifier|final
name|Class
argument_list|<
name|E
argument_list|>
name|enumClass
decl_stmt|;
DECL|method|Domain (String name, final Class<E> enumClass)
name|Domain
parameter_list|(
name|String
name|name
parameter_list|,
specifier|final
name|Class
argument_list|<
name|E
argument_list|>
name|enumClass
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|enumClass
operator|=
name|enumClass
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDomain ()
specifier|public
specifier|final
name|String
name|getDomain
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|enumClass
operator|.
name|getEnumConstants
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse (final String str)
specifier|final
name|E
name|parse
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
return|return
name|Enum
operator|.
name|valueOf
argument_list|(
name|enumClass
argument_list|,
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|str
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

