begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.security
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
name|resourcemanager
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|security
operator|.
name|client
operator|.
name|BaseClientToAMTokenSecretManager
import|;
end_import

begin_class
DECL|class|ClientToAMTokenSecretManagerInRM
specifier|public
class|class
name|ClientToAMTokenSecretManagerInRM
extends|extends
name|BaseClientToAMTokenSecretManager
block|{
comment|// Per application master-keys for managing client-tokens
DECL|field|masterKeys
specifier|private
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|SecretKey
argument_list|>
name|masterKeys
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|SecretKey
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|registerApplication (ApplicationId applicationID)
specifier|public
specifier|synchronized
name|void
name|registerApplication
parameter_list|(
name|ApplicationId
name|applicationID
parameter_list|)
block|{
name|this
operator|.
name|masterKeys
operator|.
name|put
argument_list|(
name|applicationID
argument_list|,
name|generateSecret
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|unRegisterApplication (ApplicationId applicationID)
specifier|public
specifier|synchronized
name|void
name|unRegisterApplication
parameter_list|(
name|ApplicationId
name|applicationID
parameter_list|)
block|{
name|this
operator|.
name|masterKeys
operator|.
name|remove
argument_list|(
name|applicationID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMasterKey (ApplicationId applicationID)
specifier|public
specifier|synchronized
name|SecretKey
name|getMasterKey
parameter_list|(
name|ApplicationId
name|applicationID
parameter_list|)
block|{
return|return
name|this
operator|.
name|masterKeys
operator|.
name|get
argument_list|(
name|applicationID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

