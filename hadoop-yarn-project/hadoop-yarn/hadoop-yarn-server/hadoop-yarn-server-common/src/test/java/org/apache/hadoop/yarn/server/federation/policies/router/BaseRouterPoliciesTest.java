begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|BaseFederationPoliciesTest
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
name|exceptions
operator|.
name|FederationPolicyException
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
name|utils
operator|.
name|FederationPoliciesTestUtil
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
name|resource
operator|.
name|Resources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Base class for router policies tests, tests for null input cases.  */
end_comment

begin_class
DECL|class|BaseRouterPoliciesTest
specifier|public
specifier|abstract
class|class
name|BaseRouterPoliciesTest
extends|extends
name|BaseFederationPoliciesTest
block|{
annotation|@
name|Test
DECL|method|testNullQueueRouting ()
specifier|public
name|void
name|testNullQueueRouting
parameter_list|()
throws|throws
name|YarnException
block|{
name|FederationRouterPolicy
name|localPolicy
init|=
operator|(
name|FederationRouterPolicy
operator|)
name|getPolicy
argument_list|()
decl_stmt|;
name|ApplicationSubmissionContext
name|applicationSubmissionContext
init|=
name|ApplicationSubmissionContext
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SubClusterId
name|chosen
init|=
name|localPolicy
operator|.
name|getHomeSubcluster
argument_list|(
name|applicationSubmissionContext
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|chosen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyException
operator|.
name|class
argument_list|)
DECL|method|testNullAppContext ()
specifier|public
name|void
name|testNullAppContext
parameter_list|()
throws|throws
name|YarnException
block|{
operator|(
operator|(
name|FederationRouterPolicy
operator|)
name|getPolicy
argument_list|()
operator|)
operator|.
name|getHomeSubcluster
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlacklistSubcluster ()
specifier|public
name|void
name|testBlacklistSubcluster
parameter_list|()
throws|throws
name|YarnException
block|{
name|FederationRouterPolicy
name|localPolicy
init|=
operator|(
name|FederationRouterPolicy
operator|)
name|getPolicy
argument_list|()
decl_stmt|;
name|ApplicationSubmissionContext
name|applicationSubmissionContext
init|=
name|ApplicationSubmissionContext
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|SubClusterId
argument_list|,
name|SubClusterInfo
argument_list|>
name|activeSubClusters
init|=
name|getActiveSubclusters
argument_list|()
decl_stmt|;
if|if
condition|(
name|activeSubClusters
operator|!=
literal|null
operator|&&
name|activeSubClusters
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|&&
operator|!
operator|(
name|localPolicy
operator|instanceof
name|RejectRouterPolicy
operator|)
condition|)
block|{
comment|// blacklist all the active subcluster but one.
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SubClusterId
argument_list|>
name|blacklistSubclusters
init|=
operator|new
name|ArrayList
argument_list|<
name|SubClusterId
argument_list|>
argument_list|(
name|activeSubClusters
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|SubClusterId
name|removed
init|=
name|blacklistSubclusters
operator|.
name|remove
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|blacklistSubclusters
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// bias LoadBasedRouterPolicy
name|getPolicyInfo
argument_list|()
operator|.
name|getRouterPolicyWeights
argument_list|()
operator|.
name|put
argument_list|(
operator|new
name|SubClusterIdInfo
argument_list|(
name|removed
argument_list|)
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|FederationPoliciesTestUtil
operator|.
name|initializePolicyContext
argument_list|(
name|getPolicy
argument_list|()
argument_list|,
name|getPolicyInfo
argument_list|()
argument_list|,
name|getActiveSubclusters
argument_list|()
argument_list|)
expr_stmt|;
name|SubClusterId
name|chosen
init|=
name|localPolicy
operator|.
name|getHomeSubcluster
argument_list|(
name|applicationSubmissionContext
argument_list|,
name|blacklistSubclusters
argument_list|)
decl_stmt|;
comment|// check that the selected sub-cluster is only one not blacklisted
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|chosen
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|removed
argument_list|,
name|chosen
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

