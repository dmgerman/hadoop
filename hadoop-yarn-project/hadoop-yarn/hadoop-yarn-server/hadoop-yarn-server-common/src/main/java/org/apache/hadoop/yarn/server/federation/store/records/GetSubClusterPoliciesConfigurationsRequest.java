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
comment|/**  * GetSubClusterPoliciesConfigurationsRequest is a request to the  * {@code FederationPolicyStore} to obtain all policy configurations.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|GetSubClusterPoliciesConfigurationsRequest
specifier|public
specifier|abstract
class|class
name|GetSubClusterPoliciesConfigurationsRequest
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|GetSubClusterPoliciesConfigurationsRequest
name|newInstance
parameter_list|()
block|{
return|return
name|Records
operator|.
name|newRecord
argument_list|(
name|GetSubClusterPoliciesConfigurationsRequest
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

