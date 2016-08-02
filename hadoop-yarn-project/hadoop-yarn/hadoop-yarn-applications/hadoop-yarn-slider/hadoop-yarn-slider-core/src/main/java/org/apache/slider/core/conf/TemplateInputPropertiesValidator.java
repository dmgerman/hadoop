begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.conf
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|BadConfigException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TemplateInputPropertiesValidator
specifier|public
class|class
name|TemplateInputPropertiesValidator
extends|extends
name|AbstractInputPropertiesValidator
block|{
DECL|method|validatePropertyNamePrefix (String key)
name|void
name|validatePropertyNamePrefix
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|BadConfigException
block|{
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"yarn."
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadConfigException
argument_list|(
literal|"argument %s has 'yarn.' prefix - this is not allowed in templates"
argument_list|,
name|key
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|validateGlobalProperties (ConfTreeOperations props)
name|void
name|validateGlobalProperties
parameter_list|(
name|ConfTreeOperations
name|props
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
end_class

end_unit

