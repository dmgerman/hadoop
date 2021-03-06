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
name|conf
operator|.
name|YarnConfiguration
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
name|manager
operator|.
name|PriorityBroadcastPolicyManager
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
name|manager
operator|.
name|UniformBroadcastPolicyManager
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
name|PriorityRouterPolicy
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
name|resolver
operator|.
name|SubClusterResolver
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
name|FederationStateStore
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
name|impl
operator|.
name|MemoryFederationStateStore
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
name|SetSubClusterPolicyConfigurationRequest
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
name|server
operator|.
name|federation
operator|.
name|utils
operator|.
name|FederationStateStoreFacade
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
name|FederationStateStoreTestUtil
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
comment|/**  * Simple test of {@link RouterPolicyFacade}.  */
end_comment

begin_class
DECL|class|TestRouterPolicyFacade
specifier|public
class|class
name|TestRouterPolicyFacade
block|{
DECL|field|routerFacade
specifier|private
name|RouterPolicyFacade
name|routerFacade
decl_stmt|;
DECL|field|subClusterIds
specifier|private
name|List
argument_list|<
name|SubClusterId
argument_list|>
name|subClusterIds
decl_stmt|;
DECL|field|store
specifier|private
name|FederationStateStore
name|store
decl_stmt|;
DECL|field|queue1
specifier|private
name|String
name|queue1
init|=
literal|"queue1"
decl_stmt|;
DECL|field|defQueueKey
specifier|private
name|String
name|defQueueKey
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|YarnException
block|{
comment|// setting up a store and its facade (with caching off)
name|FederationStateStoreFacade
name|fedFacade
init|=
name|FederationStateStoreFacade
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_CACHE_TIME_TO_LIVE_SECS
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|MemoryFederationStateStore
argument_list|()
expr_stmt|;
name|store
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fedFacade
operator|.
name|reinitialize
argument_list|(
name|store
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|FederationStateStoreTestUtil
name|storeTestUtil
init|=
operator|new
name|FederationStateStoreTestUtil
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|storeTestUtil
operator|.
name|registerSubClusters
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|subClusterIds
operator|=
name|storeTestUtil
operator|.
name|getAllSubClusterIds
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|store
operator|.
name|setPolicyConfiguration
argument_list|(
name|SetSubClusterPolicyConfigurationRequest
operator|.
name|newInstance
argument_list|(
name|getUniformPolicy
argument_list|(
name|queue1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|SubClusterResolver
name|resolver
init|=
name|FederationPoliciesTestUtil
operator|.
name|initResolver
argument_list|()
decl_stmt|;
name|routerFacade
operator|=
operator|new
name|RouterPolicyFacade
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|,
name|fedFacade
argument_list|,
name|resolver
argument_list|,
name|subClusterIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConfigurationUpdate ()
specifier|public
name|void
name|testConfigurationUpdate
parameter_list|()
throws|throws
name|YarnException
block|{
comment|// in this test we see what happens when the configuration is changed
comment|// between calls. We achieve this by changing what is in the store.
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
name|queue1
argument_list|)
expr_stmt|;
comment|// first call runs using standard UniformRandomRouterPolicy
name|SubClusterId
name|chosen
init|=
name|routerFacade
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
name|assertTrue
argument_list|(
name|subClusterIds
operator|.
name|contains
argument_list|(
name|chosen
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|get
argument_list|(
name|queue1
argument_list|)
operator|instanceof
name|UniformRandomRouterPolicy
argument_list|)
expr_stmt|;
comment|// then the operator changes how queue1 is routed setting it to
comment|// PriorityRouterPolicy with weights favoring the first subcluster in
comment|// subClusterIds.
name|store
operator|.
name|setPolicyConfiguration
argument_list|(
name|SetSubClusterPolicyConfigurationRequest
operator|.
name|newInstance
argument_list|(
name|getPriorityPolicy
argument_list|(
name|queue1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// second call is routed by new policy PriorityRouterPolicy
name|chosen
operator|=
name|routerFacade
operator|.
name|getHomeSubcluster
argument_list|(
name|applicationSubmissionContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|chosen
operator|.
name|equals
argument_list|(
name|subClusterIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|get
argument_list|(
name|queue1
argument_list|)
operator|instanceof
name|PriorityRouterPolicy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetHomeSubcluster ()
specifier|public
name|void
name|testGetHomeSubcluster
parameter_list|()
throws|throws
name|YarnException
block|{
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
name|queue1
argument_list|)
expr_stmt|;
comment|// the facade only contains the fallback behavior
name|Assert
operator|.
name|assertTrue
argument_list|(
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|containsKey
argument_list|(
name|defQueueKey
argument_list|)
operator|&&
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// when invoked it returns the expected SubClusterId.
name|SubClusterId
name|chosen
init|=
name|routerFacade
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
name|assertTrue
argument_list|(
name|subClusterIds
operator|.
name|contains
argument_list|(
name|chosen
argument_list|)
argument_list|)
expr_stmt|;
comment|// now the caching of policies must have added an entry for this queue
name|Assert
operator|.
name|assertTrue
argument_list|(
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|// after the facade is used the policyMap contains the expected policy type.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|get
argument_list|(
name|queue1
argument_list|)
operator|instanceof
name|UniformRandomRouterPolicy
argument_list|)
expr_stmt|;
comment|// the facade is again empty after reset
name|routerFacade
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// the facade only contains the fallback behavior
name|Assert
operator|.
name|assertTrue
argument_list|(
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|containsKey
argument_list|(
name|defQueueKey
argument_list|)
operator|&&
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFallbacks ()
specifier|public
name|void
name|testFallbacks
parameter_list|()
throws|throws
name|YarnException
block|{
comment|// this tests the behavior of the system when the queue requested is
comment|// not configured (or null) and there is no default policy configured
comment|// for DEFAULT_FEDERATION_POLICY_KEY (*). This is our second line of
comment|// defense.
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
comment|// The facade answers also for non-initialized policies (using the
comment|// defaultPolicy)
name|String
name|uninitQueue
init|=
literal|"non-initialized-queue"
decl_stmt|;
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
name|uninitQueue
argument_list|)
expr_stmt|;
name|SubClusterId
name|chosen
init|=
name|routerFacade
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
name|assertTrue
argument_list|(
name|subClusterIds
operator|.
name|contains
argument_list|(
name|chosen
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|containsKey
argument_list|(
name|uninitQueue
argument_list|)
argument_list|)
expr_stmt|;
comment|// empty string
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
literal|""
argument_list|)
expr_stmt|;
name|chosen
operator|=
name|routerFacade
operator|.
name|getHomeSubcluster
argument_list|(
name|applicationSubmissionContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|subClusterIds
operator|.
name|contains
argument_list|(
name|chosen
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|containsKey
argument_list|(
name|uninitQueue
argument_list|)
argument_list|)
expr_stmt|;
comment|// null queue also falls back to default
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
literal|null
argument_list|)
expr_stmt|;
name|chosen
operator|=
name|routerFacade
operator|.
name|getHomeSubcluster
argument_list|(
name|applicationSubmissionContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|subClusterIds
operator|.
name|contains
argument_list|(
name|chosen
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|routerFacade
operator|.
name|globalPolicyMap
operator|.
name|containsKey
argument_list|(
name|uninitQueue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getUniformPolicy (String queue)
specifier|public
specifier|static
name|SubClusterPolicyConfiguration
name|getUniformPolicy
parameter_list|(
name|String
name|queue
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
block|{
comment|// we go through standard lifecycle instantiating a policyManager and
comment|// configuring it and serializing it to a conf.
name|UniformBroadcastPolicyManager
name|wfp
init|=
operator|new
name|UniformBroadcastPolicyManager
argument_list|()
decl_stmt|;
name|wfp
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|SubClusterPolicyConfiguration
name|fpc
init|=
name|wfp
operator|.
name|serializeConf
argument_list|()
decl_stmt|;
return|return
name|fpc
return|;
block|}
DECL|method|getPriorityPolicy (String queue)
specifier|public
name|SubClusterPolicyConfiguration
name|getPriorityPolicy
parameter_list|(
name|String
name|queue
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
block|{
comment|// we go through standard lifecycle instantiating a policyManager and
comment|// configuring it and serializing it to a conf.
name|PriorityBroadcastPolicyManager
name|wfp
init|=
operator|new
name|PriorityBroadcastPolicyManager
argument_list|()
decl_stmt|;
comment|// equal weight to all subcluster
name|Map
argument_list|<
name|SubClusterIdInfo
argument_list|,
name|Float
argument_list|>
name|routerWeights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SubClusterId
name|s
range|:
name|subClusterIds
control|)
block|{
name|routerWeights
operator|.
name|put
argument_list|(
operator|new
name|SubClusterIdInfo
argument_list|(
name|s
argument_list|)
argument_list|,
literal|0.9f
operator|/
name|subClusterIds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// beside the first one who gets more weight
name|SubClusterIdInfo
name|favorite
init|=
operator|new
name|SubClusterIdInfo
argument_list|(
operator|(
name|subClusterIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
argument_list|)
decl_stmt|;
name|routerWeights
operator|.
name|put
argument_list|(
name|favorite
argument_list|,
operator|(
literal|0.1f
operator|+
literal|0.9f
operator|/
name|subClusterIds
operator|.
name|size
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|WeightedPolicyInfo
name|policyInfo
init|=
operator|new
name|WeightedPolicyInfo
argument_list|()
decl_stmt|;
name|policyInfo
operator|.
name|setRouterPolicyWeights
argument_list|(
name|routerWeights
argument_list|)
expr_stmt|;
name|wfp
operator|.
name|setWeightedPolicyInfo
argument_list|(
name|policyInfo
argument_list|)
expr_stmt|;
name|wfp
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
comment|// serializeConf it in a context
name|SubClusterPolicyConfiguration
name|fpc
init|=
name|wfp
operator|.
name|serializeConf
argument_list|()
decl_stmt|;
return|return
name|fpc
return|;
block|}
block|}
end_class

end_unit

