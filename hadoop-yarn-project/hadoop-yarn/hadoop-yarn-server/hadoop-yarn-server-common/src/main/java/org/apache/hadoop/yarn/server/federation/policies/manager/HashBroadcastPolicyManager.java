begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.policies.manager
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
name|federation
operator|.
name|policies
operator|.
name|manager
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
name|server
operator|.
name|federation
operator|.
name|policies
operator|.
name|amrmproxy
operator|.
name|BroadcastAMRMProxyPolicy
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
name|federation
operator|.
name|policies
operator|.
name|router
operator|.
name|HashBasedRouterPolicy
import|;
end_import

begin_comment
comment|/**  * Policy that routes applications via hashing of their queuename, and broadcast  * resource requests. This picks a {@link HashBasedRouterPolicy} for the router  * and a {@link BroadcastAMRMProxyPolicy} for the amrmproxy as they are designed  * to work together.  */
end_comment

begin_class
DECL|class|HashBroadcastPolicyManager
specifier|public
class|class
name|HashBroadcastPolicyManager
extends|extends
name|AbstractPolicyManager
block|{
DECL|method|HashBroadcastPolicyManager ()
specifier|public
name|HashBroadcastPolicyManager
parameter_list|()
block|{
comment|// this structurally hard-codes two compatible policies for Router and
comment|// AMRMProxy.
name|routerFederationPolicy
operator|=
name|HashBasedRouterPolicy
operator|.
name|class
expr_stmt|;
name|amrmProxyFederationPolicy
operator|=
name|BroadcastAMRMProxyPolicy
operator|.
name|class
expr_stmt|;
block|}
block|}
end_class

end_unit

