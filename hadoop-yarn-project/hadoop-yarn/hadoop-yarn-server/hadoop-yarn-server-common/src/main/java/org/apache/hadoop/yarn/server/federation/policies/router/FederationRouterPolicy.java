begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.policies.router
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
name|router
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
name|api
operator|.
name|records
operator|.
name|ApplicationSubmissionContext
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
name|exceptions
operator|.
name|YarnException
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
name|ConfigurableFederationPolicy
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
name|store
operator|.
name|records
operator|.
name|SubClusterId
import|;
end_import

begin_comment
comment|/**  * Implements the logic for determining the routing of an application submission  * based on a policy.  */
end_comment

begin_interface
DECL|interface|FederationRouterPolicy
specifier|public
interface|interface
name|FederationRouterPolicy
extends|extends
name|ConfigurableFederationPolicy
block|{
comment|/**    * Determines the sub-cluster that the user application submision should be    * routed to.    *    * @param appSubmissionContext the context for the app being submitted.    *    * @return the sub-cluster as identified by {@link SubClusterId} to route the    * request to.    *    * @throws YarnException if the policy cannot determine a viable subcluster.    */
DECL|method|getHomeSubcluster ( ApplicationSubmissionContext appSubmissionContext)
name|SubClusterId
name|getHomeSubcluster
parameter_list|(
name|ApplicationSubmissionContext
name|appSubmissionContext
parameter_list|)
throws|throws
name|YarnException
function_decl|;
block|}
end_interface

end_unit

