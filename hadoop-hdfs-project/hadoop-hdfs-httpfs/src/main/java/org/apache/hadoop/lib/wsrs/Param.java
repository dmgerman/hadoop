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
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Param
specifier|public
specifier|abstract
class|class
name|Param
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|value
specifier|protected
name|T
name|value
decl_stmt|;
DECL|method|Param (String name, T defaultValue)
specifier|public
name|Param
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|defaultValue
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
name|value
operator|=
name|defaultValue
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
DECL|method|parseParam (String str)
specifier|public
name|T
name|parseParam
parameter_list|(
name|String
name|str
parameter_list|)
block|{
try|try
block|{
name|value
operator|=
operator|(
name|str
operator|!=
literal|null
operator|&&
name|str
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|parse
argument_list|(
name|str
argument_list|)
else|:
name|value
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Parameter [{0}], invalid value [{1}], value must be [{2}]"
argument_list|,
name|name
argument_list|,
name|str
argument_list|,
name|getDomain
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
DECL|method|value ()
specifier|public
name|T
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|getDomain ()
specifier|protected
specifier|abstract
name|String
name|getDomain
parameter_list|()
function_decl|;
DECL|method|parse (String str)
specifier|protected
specifier|abstract
name|T
name|parse
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|value
operator|!=
literal|null
operator|)
condition|?
name|value
operator|.
name|toString
argument_list|()
else|:
literal|"NULL"
return|;
block|}
block|}
end_class

end_unit

