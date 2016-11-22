begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|nio
operator|.
name|ByteBuffer
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
name|api
operator|.
name|records
operator|.
name|ResourceRequest
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
name|amrmproxy
operator|.
name|FederationAMRMProxyPolicy
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
name|policies
operator|.
name|exceptions
operator|.
name|FederationPolicyInitializationException
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
name|FederationRouterPolicy
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
name|SubClusterPolicyConfiguration
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Base class for policies tests, tests for common reinitialization cases.  */
end_comment

begin_class
DECL|class|BaseFederationPoliciesTest
specifier|public
specifier|abstract
class|class
name|BaseFederationPoliciesTest
block|{
DECL|field|policy
specifier|private
name|ConfigurableFederationPolicy
name|policy
decl_stmt|;
DECL|field|policyInfo
specifier|private
name|WeightedPolicyInfo
name|policyInfo
init|=
name|mock
argument_list|(
name|WeightedPolicyInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|activeSubclusters
specifier|private
name|Map
argument_list|<
name|SubClusterId
argument_list|,
name|SubClusterInfo
argument_list|>
name|activeSubclusters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|federationPolicyContext
specifier|private
name|FederationPolicyInitializationContext
name|federationPolicyContext
decl_stmt|;
DECL|field|applicationSubmissionContext
specifier|private
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
DECL|field|rand
specifier|private
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|homeSubCluster
specifier|private
name|SubClusterId
name|homeSubCluster
decl_stmt|;
annotation|@
name|Test
DECL|method|testReinitilialize ()
specifier|public
name|void
name|testReinitilialize
parameter_list|()
throws|throws
name|YarnException
block|{
name|FederationPolicyInitializationContext
name|fpc
init|=
operator|new
name|FederationPolicyInitializationContext
argument_list|()
decl_stmt|;
name|ByteBuffer
name|buf
init|=
name|getPolicyInfo
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
decl_stmt|;
name|fpc
operator|.
name|setSubClusterPolicyConfiguration
argument_list|(
name|SubClusterPolicyConfiguration
operator|.
name|newInstance
argument_list|(
literal|"queue1"
argument_list|,
name|getPolicy
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|buf
argument_list|)
argument_list|)
expr_stmt|;
name|fpc
operator|.
name|setFederationSubclusterResolver
argument_list|(
name|FederationPoliciesTestUtil
operator|.
name|initResolver
argument_list|()
argument_list|)
expr_stmt|;
name|fpc
operator|.
name|setFederationStateStoreFacade
argument_list|(
name|FederationPoliciesTestUtil
operator|.
name|initFacade
argument_list|()
argument_list|)
expr_stmt|;
name|getPolicy
argument_list|()
operator|.
name|reinitialize
argument_list|(
name|fpc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyInitializationException
operator|.
name|class
argument_list|)
DECL|method|testReinitilializeBad1 ()
specifier|public
name|void
name|testReinitilializeBad1
parameter_list|()
throws|throws
name|YarnException
block|{
name|getPolicy
argument_list|()
operator|.
name|reinitialize
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyInitializationException
operator|.
name|class
argument_list|)
DECL|method|testReinitilializeBad2 ()
specifier|public
name|void
name|testReinitilializeBad2
parameter_list|()
throws|throws
name|YarnException
block|{
name|FederationPolicyInitializationContext
name|fpc
init|=
operator|new
name|FederationPolicyInitializationContext
argument_list|()
decl_stmt|;
name|getPolicy
argument_list|()
operator|.
name|reinitialize
argument_list|(
name|fpc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyInitializationException
operator|.
name|class
argument_list|)
DECL|method|testReinitilializeBad3 ()
specifier|public
name|void
name|testReinitilializeBad3
parameter_list|()
throws|throws
name|YarnException
block|{
name|FederationPolicyInitializationContext
name|fpc
init|=
operator|new
name|FederationPolicyInitializationContext
argument_list|()
decl_stmt|;
name|ByteBuffer
name|buf
init|=
name|mock
argument_list|(
name|ByteBuffer
operator|.
name|class
argument_list|)
decl_stmt|;
name|fpc
operator|.
name|setSubClusterPolicyConfiguration
argument_list|(
name|SubClusterPolicyConfiguration
operator|.
name|newInstance
argument_list|(
literal|"queue1"
argument_list|,
literal|"WrongPolicyName"
argument_list|,
name|buf
argument_list|)
argument_list|)
expr_stmt|;
name|fpc
operator|.
name|setFederationSubclusterResolver
argument_list|(
name|FederationPoliciesTestUtil
operator|.
name|initResolver
argument_list|()
argument_list|)
expr_stmt|;
name|fpc
operator|.
name|setFederationStateStoreFacade
argument_list|(
name|FederationPoliciesTestUtil
operator|.
name|initFacade
argument_list|()
argument_list|)
expr_stmt|;
name|getPolicy
argument_list|()
operator|.
name|reinitialize
argument_list|(
name|fpc
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
DECL|method|testNoSubclusters ()
specifier|public
name|void
name|testNoSubclusters
parameter_list|()
throws|throws
name|YarnException
block|{
comment|// empty the activeSubclusters map
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
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigurableFederationPolicy
name|localPolicy
init|=
name|getPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|localPolicy
operator|instanceof
name|FederationRouterPolicy
condition|)
block|{
operator|(
operator|(
name|FederationRouterPolicy
operator|)
name|localPolicy
operator|)
operator|.
name|getHomeSubcluster
argument_list|(
name|getApplicationSubmissionContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|hosts
init|=
operator|new
name|String
index|[]
block|{
literal|"host1"
block|,
literal|"host2"
block|}
decl_stmt|;
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
init|=
name|FederationPoliciesTestUtil
operator|.
name|createResourceRequests
argument_list|(
name|hosts
argument_list|,
literal|2
operator|*
literal|1024
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
operator|(
operator|(
name|FederationAMRMProxyPolicy
operator|)
name|localPolicy
operator|)
operator|.
name|splitResourceRequests
argument_list|(
name|resourceRequests
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getPolicy ()
specifier|public
name|ConfigurableFederationPolicy
name|getPolicy
parameter_list|()
block|{
return|return
name|policy
return|;
block|}
DECL|method|setPolicy (ConfigurableFederationPolicy policy)
specifier|public
name|void
name|setPolicy
parameter_list|(
name|ConfigurableFederationPolicy
name|policy
parameter_list|)
block|{
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
block|}
DECL|method|getPolicyInfo ()
specifier|public
name|WeightedPolicyInfo
name|getPolicyInfo
parameter_list|()
block|{
return|return
name|policyInfo
return|;
block|}
DECL|method|setPolicyInfo (WeightedPolicyInfo policyInfo)
specifier|public
name|void
name|setPolicyInfo
parameter_list|(
name|WeightedPolicyInfo
name|policyInfo
parameter_list|)
block|{
name|this
operator|.
name|policyInfo
operator|=
name|policyInfo
expr_stmt|;
block|}
DECL|method|getActiveSubclusters ()
specifier|public
name|Map
argument_list|<
name|SubClusterId
argument_list|,
name|SubClusterInfo
argument_list|>
name|getActiveSubclusters
parameter_list|()
block|{
return|return
name|activeSubclusters
return|;
block|}
DECL|method|setActiveSubclusters ( Map<SubClusterId, SubClusterInfo> activeSubclusters)
specifier|public
name|void
name|setActiveSubclusters
parameter_list|(
name|Map
argument_list|<
name|SubClusterId
argument_list|,
name|SubClusterInfo
argument_list|>
name|activeSubclusters
parameter_list|)
block|{
name|this
operator|.
name|activeSubclusters
operator|=
name|activeSubclusters
expr_stmt|;
block|}
DECL|method|getFederationPolicyContext ()
specifier|public
name|FederationPolicyInitializationContext
name|getFederationPolicyContext
parameter_list|()
block|{
return|return
name|federationPolicyContext
return|;
block|}
DECL|method|setFederationPolicyContext ( FederationPolicyInitializationContext federationPolicyContext)
specifier|public
name|void
name|setFederationPolicyContext
parameter_list|(
name|FederationPolicyInitializationContext
name|federationPolicyContext
parameter_list|)
block|{
name|this
operator|.
name|federationPolicyContext
operator|=
name|federationPolicyContext
expr_stmt|;
block|}
DECL|method|getApplicationSubmissionContext ()
specifier|public
name|ApplicationSubmissionContext
name|getApplicationSubmissionContext
parameter_list|()
block|{
return|return
name|applicationSubmissionContext
return|;
block|}
DECL|method|setApplicationSubmissionContext ( ApplicationSubmissionContext applicationSubmissionContext)
specifier|public
name|void
name|setApplicationSubmissionContext
parameter_list|(
name|ApplicationSubmissionContext
name|applicationSubmissionContext
parameter_list|)
block|{
name|this
operator|.
name|applicationSubmissionContext
operator|=
name|applicationSubmissionContext
expr_stmt|;
block|}
DECL|method|getRand ()
specifier|public
name|Random
name|getRand
parameter_list|()
block|{
return|return
name|rand
return|;
block|}
DECL|method|setRand (Random rand)
specifier|public
name|void
name|setRand
parameter_list|(
name|Random
name|rand
parameter_list|)
block|{
name|this
operator|.
name|rand
operator|=
name|rand
expr_stmt|;
block|}
DECL|method|getHomeSubCluster ()
specifier|public
name|SubClusterId
name|getHomeSubCluster
parameter_list|()
block|{
return|return
name|homeSubCluster
return|;
block|}
DECL|method|setHomeSubCluster (SubClusterId homeSubCluster)
specifier|public
name|void
name|setHomeSubCluster
parameter_list|(
name|SubClusterId
name|homeSubCluster
parameter_list|)
block|{
name|this
operator|.
name|homeSubCluster
operator|=
name|homeSubCluster
expr_stmt|;
block|}
DECL|method|setMockActiveSubclusters (int numSubclusters)
specifier|public
name|void
name|setMockActiveSubclusters
parameter_list|(
name|int
name|numSubclusters
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numSubclusters
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
block|}
block|}
end_class

end_unit

