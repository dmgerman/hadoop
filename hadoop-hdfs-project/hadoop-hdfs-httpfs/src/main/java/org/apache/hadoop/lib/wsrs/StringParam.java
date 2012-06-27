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
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|StringParam
specifier|public
specifier|abstract
class|class
name|StringParam
extends|extends
name|Param
argument_list|<
name|String
argument_list|>
block|{
DECL|field|pattern
specifier|private
name|Pattern
name|pattern
decl_stmt|;
DECL|method|StringParam (String name, String defaultValue)
specifier|public
name|StringParam
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|defaultValue
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|StringParam (String name, String defaultValue, Pattern pattern)
specifier|public
name|StringParam
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defaultValue
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|defaultValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
name|parseParam
argument_list|(
name|defaultValue
argument_list|)
expr_stmt|;
block|}
DECL|method|parseParam (String str)
specifier|public
name|String
name|parseParam
parameter_list|(
name|String
name|str
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|str
operator|!=
literal|null
condition|)
block|{
name|str
operator|=
name|str
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|value
operator|=
name|parse
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
block|}
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
name|getName
argument_list|()
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
DECL|method|parse (String str)
specifier|protected
name|String
name|parse
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|pattern
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|pattern
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid value"
argument_list|)
throw|;
block|}
block|}
return|return
name|str
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
operator|(
name|pattern
operator|==
literal|null
operator|)
condition|?
literal|"a string"
else|:
name|pattern
operator|.
name|pattern
argument_list|()
return|;
block|}
block|}
end_class

end_unit

