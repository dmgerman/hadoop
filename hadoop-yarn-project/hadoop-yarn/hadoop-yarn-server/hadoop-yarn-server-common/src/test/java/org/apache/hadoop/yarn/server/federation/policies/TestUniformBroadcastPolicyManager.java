begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.policies
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
name|UniformRandomRouterPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_comment
comment|/**  * Simple test of {@link UniformBroadcastPolicyManager}.  */
end_comment

begin_class
DECL|class|TestUniformBroadcastPolicyManager
specifier|public
class|class
name|TestUniformBroadcastPolicyManager
extends|extends
name|BasePolicyManagerTest
block|{
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
comment|//config policy
name|wfp
operator|=
operator|new
name|UniformBroadcastPolicyManager
argument_list|()
expr_stmt|;
name|wfp
operator|.
name|setQueue
argument_list|(
literal|"queue1"
argument_list|)
expr_stmt|;
comment|//set expected params that the base test class will use for tests
name|expectedPolicyManager
operator|=
name|UniformBroadcastPolicyManager
operator|.
name|class
expr_stmt|;
name|expectedAMRMProxyPolicy
operator|=
name|BroadcastAMRMProxyPolicy
operator|.
name|class
expr_stmt|;
name|expectedRouterPolicy
operator|=
name|UniformRandomRouterPolicy
operator|.
name|class
expr_stmt|;
block|}
block|}
end_class

end_unit

