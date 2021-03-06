begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.records
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
name|store
operator|.
name|records
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
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * GetSubClusterPolicyConfigurationResponse contains the answer from the {@code  * FederationPolicyStore} to a request to get the information about how a policy  * should be configured via a {@link SubClusterPolicyConfiguration}.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|GetSubClusterPolicyConfigurationResponse
specifier|public
specifier|abstract
class|class
name|GetSubClusterPolicyConfigurationResponse
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance ( SubClusterPolicyConfiguration policy)
specifier|public
specifier|static
name|GetSubClusterPolicyConfigurationResponse
name|newInstance
parameter_list|(
name|SubClusterPolicyConfiguration
name|policy
parameter_list|)
block|{
name|GetSubClusterPolicyConfigurationResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetSubClusterPolicyConfigurationResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setPolicyConfiguration
argument_list|(
name|policy
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Get the policy configuration.    *    * @return the policy configuration for the specified queue    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getPolicyConfiguration ()
specifier|public
specifier|abstract
name|SubClusterPolicyConfiguration
name|getPolicyConfiguration
parameter_list|()
function_decl|;
comment|/**    * Sets the policyConfiguration configuration.    *    * @param policyConfiguration the policyConfiguration configuration for the    *          specified queue    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setPolicyConfiguration ( SubClusterPolicyConfiguration policyConfiguration)
specifier|public
specifier|abstract
name|void
name|setPolicyConfiguration
parameter_list|(
name|SubClusterPolicyConfiguration
name|policyConfiguration
parameter_list|)
function_decl|;
block|}
end_class

end_unit

