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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|dao
operator|.
name|WeightedPolicyInfo
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
name|store
operator|.
name|records
operator|.
name|SubClusterState
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
name|Before
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
comment|/**  * Simple test class for the {@link UniformRandomRouterPolicy}. Tests that one  * of the active subcluster is chosen.  */
end_comment

begin_class
DECL|class|TestUniformRandomRouterPolicy
specifier|public
class|class
name|TestUniformRandomRouterPolicy
extends|extends
name|BaseFederationPoliciesTest
block|{
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setPolicy
argument_list|(
operator|new
name|UniformRandomRouterPolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// needed for base test to work
name|setPolicyInfo
argument_list|(
name|mock
argument_list|(
name|WeightedPolicyInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|SubClusterIdInfo
name|sc
init|=
operator|new
name|SubClusterIdInfo
argument_list|(
literal|"sc"
operator|+
name|i
argument_list|)
decl_stmt|;
name|SubClusterInfo
name|sci
init|=
name|mock
argument_list|(
name|SubClusterInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|sci
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SubClusterState
operator|.
name|SC_RUNNING
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|sci
operator|.
name|getSubClusterId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|sc
operator|.
name|toId
argument_list|()
argument_list|)
expr_stmt|;
name|getActiveSubclusters
argument_list|()
operator|.
name|put
argument_list|(
name|sc
operator|.
name|toId
argument_list|()
argument_list|,
name|sci
argument_list|)
expr_stmt|;
block|}
name|FederationPoliciesTestUtil
operator|.
name|initializePolicyContext
argument_list|(
name|getPolicy
argument_list|()
argument_list|,
name|mock
argument_list|(
name|WeightedPolicyInfo
operator|.
name|class
argument_list|)
argument_list|,
name|getActiveSubclusters
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneSubclusterIsChosen ()
specifier|public
name|void
name|testOneSubclusterIsChosen
parameter_list|()
throws|throws
name|YarnException
block|{
name|SubClusterId
name|chosen
init|=
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
name|getApplicationSubmissionContext
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getActiveSubclusters
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
name|chosen
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

