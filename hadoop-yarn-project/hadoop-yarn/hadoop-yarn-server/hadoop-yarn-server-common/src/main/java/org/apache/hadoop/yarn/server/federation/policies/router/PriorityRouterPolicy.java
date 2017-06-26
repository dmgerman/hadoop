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
name|FederationPolicyUtils
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
name|SubClusterIdInfo
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
name|SubClusterInfo
import|;
end_import

begin_comment
comment|/**  * This implements a policy that interprets "weights" as a ordered list of  * preferences among sub-clusters. Highest weight among active subclusters is  * chosen.  */
end_comment

begin_class
DECL|class|PriorityRouterPolicy
specifier|public
class|class
name|PriorityRouterPolicy
extends|extends
name|AbstractRouterPolicy
block|{
annotation|@
name|Override
DECL|method|getHomeSubcluster ( ApplicationSubmissionContext appSubmissionContext, List<SubClusterId> blacklist)
specifier|public
name|SubClusterId
name|getHomeSubcluster
parameter_list|(
name|ApplicationSubmissionContext
name|appSubmissionContext
parameter_list|,
name|List
argument_list|<
name|SubClusterId
argument_list|>
name|blacklist
parameter_list|)
throws|throws
name|YarnException
block|{
comment|// null checks and default-queue behavior
name|validate
argument_list|(
name|appSubmissionContext
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|SubClusterId
argument_list|,
name|SubClusterInfo
argument_list|>
name|activeSubclusters
init|=
name|getActiveSubclusters
argument_list|()
decl_stmt|;
name|FederationPolicyUtils
operator|.
name|validateSubClusterAvailability
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|SubClusterId
argument_list|>
argument_list|(
name|activeSubclusters
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
name|blacklist
argument_list|)
expr_stmt|;
comment|// This finds the sub-cluster with the highest weight among the
comment|// currently active ones.
name|Map
argument_list|<
name|SubClusterIdInfo
argument_list|,
name|Float
argument_list|>
name|weights
init|=
name|getPolicyInfo
argument_list|()
operator|.
name|getRouterPolicyWeights
argument_list|()
decl_stmt|;
name|SubClusterId
name|chosen
init|=
literal|null
decl_stmt|;
name|Float
name|currentBest
init|=
name|Float
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|SubClusterId
name|id
range|:
name|activeSubclusters
operator|.
name|keySet
argument_list|()
control|)
block|{
name|SubClusterIdInfo
name|idInfo
init|=
operator|new
name|SubClusterIdInfo
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|blacklist
operator|!=
literal|null
operator|&&
name|blacklist
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|weights
operator|.
name|containsKey
argument_list|(
name|idInfo
argument_list|)
operator|&&
name|weights
operator|.
name|get
argument_list|(
name|idInfo
argument_list|)
operator|>
name|currentBest
condition|)
block|{
name|currentBest
operator|=
name|weights
operator|.
name|get
argument_list|(
name|idInfo
argument_list|)
expr_stmt|;
name|chosen
operator|=
name|id
expr_stmt|;
block|}
block|}
return|return
name|chosen
return|;
block|}
block|}
end_class

end_unit

