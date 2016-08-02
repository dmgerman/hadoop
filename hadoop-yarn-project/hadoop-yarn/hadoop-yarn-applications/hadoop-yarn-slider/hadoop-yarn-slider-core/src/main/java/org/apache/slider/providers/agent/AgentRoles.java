begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.agent
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
name|providers
operator|.
name|ProviderRole
import|;
end_import

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

begin_class
DECL|class|AgentRoles
specifier|public
class|class
name|AgentRoles
block|{
comment|/**    * List of roles Agent provider does not have any roles by default. All roles are read from the application    * specification.    */
DECL|field|ROLES
specifier|protected
specifier|static
specifier|final
name|List
argument_list|<
name|ProviderRole
argument_list|>
name|ROLES
init|=
operator|new
name|ArrayList
argument_list|<
name|ProviderRole
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|getRoles ()
specifier|public
specifier|static
name|List
argument_list|<
name|ProviderRole
argument_list|>
name|getRoles
parameter_list|()
block|{
return|return
name|ROLES
return|;
block|}
block|}
end_class

end_unit

