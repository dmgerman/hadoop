begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.agent.application.metadata
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|agent
operator|.
name|application
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Application default config  */
end_comment

begin_class
DECL|class|DefaultConfig
specifier|public
class|class
name|DefaultConfig
block|{
DECL|field|propertyInfos
name|List
argument_list|<
name|PropertyInfo
argument_list|>
name|propertyInfos
decl_stmt|;
DECL|method|DefaultConfig ()
specifier|public
name|DefaultConfig
parameter_list|()
block|{
name|propertyInfos
operator|=
operator|new
name|ArrayList
argument_list|<
name|PropertyInfo
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|addPropertyInfo (PropertyInfo propertyInfo)
specifier|public
name|void
name|addPropertyInfo
parameter_list|(
name|PropertyInfo
name|propertyInfo
parameter_list|)
block|{
name|propertyInfos
operator|.
name|add
argument_list|(
name|propertyInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|getPropertyInfos ()
specifier|public
name|List
argument_list|<
name|PropertyInfo
argument_list|>
name|getPropertyInfos
parameter_list|()
block|{
return|return
name|propertyInfos
return|;
block|}
block|}
end_class

end_unit

