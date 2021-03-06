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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
comment|/**  * Simple test class for the {@link HashBasedRouterPolicy}. Tests that one of  * the active sub-cluster is chosen.  */
end_comment

begin_class
DECL|class|TestHashBasedRouterPolicy
specifier|public
class|class
name|TestHashBasedRouterPolicy
extends|extends
name|BaseRouterPoliciesTest
block|{
DECL|field|numSubclusters
specifier|private
name|int
name|numSubclusters
init|=
literal|10
decl_stmt|;
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
comment|// set policy in base class
name|setPolicy
argument_list|(
operator|new
name|HashBasedRouterPolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// setting up the active sub-clusters for this test
name|setMockActiveSubclusters
argument_list|(
name|numSubclusters
argument_list|)
expr_stmt|;
comment|// initialize policy with context
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
block|}
annotation|@
name|Test
DECL|method|testHashSpreadUniformlyAmongSubclusters ()
specifier|public
name|void
name|testHashSpreadUniformlyAmongSubclusters
parameter_list|()
throws|throws
name|YarnException
block|{
name|SubClusterId
name|chosen
decl_stmt|;
name|Map
argument_list|<
name|SubClusterId
argument_list|,
name|AtomicLong
argument_list|>
name|counter
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SubClusterId
name|id
range|:
name|getActiveSubclusters
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|counter
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|jobPerSub
init|=
literal|100
decl_stmt|;
name|ApplicationSubmissionContext
name|applicationSubmissionContext
init|=
name|mock
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|jobPerSub
operator|*
name|numSubclusters
condition|;
name|i
operator|++
control|)
block|{
name|when
argument_list|(
name|applicationSubmissionContext
operator|.
name|getQueue
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"queue"
operator|+
name|i
argument_list|)
expr_stmt|;
name|chosen
operator|=
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
name|applicationSubmissionContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|counter
operator|.
name|get
argument_list|(
name|chosen
argument_list|)
operator|.
name|addAndGet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// hash spread the jobs equally among the subclusters
for|for
control|(
name|AtomicLong
name|a
range|:
name|counter
operator|.
name|values
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|a
operator|.
name|get
argument_list|()
argument_list|,
name|jobPerSub
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

