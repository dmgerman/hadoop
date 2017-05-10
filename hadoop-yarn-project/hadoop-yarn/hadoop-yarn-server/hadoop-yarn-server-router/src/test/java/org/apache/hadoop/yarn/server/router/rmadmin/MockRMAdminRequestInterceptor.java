begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.rmadmin
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|router
operator|.
name|rmadmin
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|server
operator|.
name|MockResourceManagerFacade
import|;
end_import

begin_comment
comment|/**  * This class mocks the RMAmdinRequestInterceptor.  */
end_comment

begin_class
DECL|class|MockRMAdminRequestInterceptor
specifier|public
class|class
name|MockRMAdminRequestInterceptor
extends|extends
name|DefaultRMAdminRequestInterceptor
block|{
DECL|method|init (String user)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|MockResourceManagerFacade
name|mockRM
init|=
operator|new
name|MockResourceManagerFacade
argument_list|(
operator|new
name|YarnConfiguration
argument_list|(
name|super
operator|.
name|getConf
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|super
operator|.
name|setRMAdmin
argument_list|(
name|mockRM
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

