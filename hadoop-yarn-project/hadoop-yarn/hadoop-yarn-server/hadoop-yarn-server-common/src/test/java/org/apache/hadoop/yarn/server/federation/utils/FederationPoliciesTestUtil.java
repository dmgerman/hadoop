begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.utils
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
name|utils
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
name|conf
operator|.
name|Configuration
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
name|policies
operator|.
name|FederationPolicyInitializationContext
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
name|resolver
operator|.
name|DefaultSubClusterResolverImpl
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
name|records
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|ArrayList
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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

begin_comment
comment|/**  * Support class providing common initialization methods to test federation  * policies.  */
end_comment

begin_class
DECL|class|FederationPoliciesTestUtil
specifier|public
specifier|final
class|class
name|FederationPoliciesTestUtil
block|{
DECL|method|FederationPoliciesTestUtil ()
specifier|private
name|FederationPoliciesTestUtil
parameter_list|()
block|{
comment|// disabled.
block|}
DECL|method|initializePolicyContext ( FederationPolicyInitializationContext fpc, ConfigurableFederationPolicy policy, WeightedPolicyInfo policyInfo, Map<SubClusterId, SubClusterInfo> activeSubclusters)
specifier|public
specifier|static
name|void
name|initializePolicyContext
parameter_list|(
name|FederationPolicyInitializationContext
name|fpc
parameter_list|,
name|ConfigurableFederationPolicy
name|policy
parameter_list|,
name|WeightedPolicyInfo
name|policyInfo
parameter_list|,
name|Map
argument_list|<
name|SubClusterId
argument_list|,
name|SubClusterInfo
argument_list|>
name|activeSubclusters
parameter_list|)
throws|throws
name|YarnException
block|{
name|ByteBuffer
name|buf
init|=
name|policyInfo
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
name|policy
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
name|FederationStateStoreFacade
name|facade
init|=
name|FederationStateStoreFacade
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|FederationStateStore
name|fss
init|=
name|mock
argument_list|(
name|FederationStateStore
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|activeSubclusters
operator|==
literal|null
condition|)
block|{
name|activeSubclusters
operator|=
operator|new
name|HashMap
argument_list|<
name|SubClusterId
argument_list|,
name|SubClusterInfo
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|GetSubClustersInfoResponse
name|response
init|=
name|GetSubClustersInfoResponse
operator|.
name|newInstance
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|SubClusterInfo
argument_list|>
argument_list|(
name|activeSubclusters
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fss
operator|.
name|getSubClusters
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|facade
operator|.
name|reinitialize
argument_list|(
name|fss
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|fpc
operator|.
name|setFederationStateStoreFacade
argument_list|(
name|facade
argument_list|)
expr_stmt|;
name|policy
operator|.
name|reinitialize
argument_list|(
name|fpc
argument_list|)
expr_stmt|;
block|}
DECL|method|initializePolicyContext ( ConfigurableFederationPolicy policy, WeightedPolicyInfo policyInfo, Map<SubClusterId, SubClusterInfo> activeSubclusters)
specifier|public
specifier|static
name|void
name|initializePolicyContext
parameter_list|(
name|ConfigurableFederationPolicy
name|policy
parameter_list|,
name|WeightedPolicyInfo
name|policyInfo
parameter_list|,
name|Map
argument_list|<
name|SubClusterId
argument_list|,
name|SubClusterInfo
argument_list|>
name|activeSubclusters
parameter_list|)
throws|throws
name|YarnException
block|{
name|FederationPolicyInitializationContext
name|context
init|=
operator|new
name|FederationPolicyInitializationContext
argument_list|(
literal|null
argument_list|,
name|initResolver
argument_list|()
argument_list|,
name|initFacade
argument_list|()
argument_list|)
decl_stmt|;
name|initializePolicyContext
argument_list|(
name|context
argument_list|,
name|policy
argument_list|,
name|policyInfo
argument_list|,
name|activeSubclusters
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize a {@link SubClusterResolver}.    *    * @return a subcluster resolver for tests.    */
DECL|method|initResolver ()
specifier|public
specifier|static
name|SubClusterResolver
name|initResolver
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|SubClusterResolver
name|resolver
init|=
operator|new
name|DefaultSubClusterResolverImpl
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"nodes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not find 'nodes' dummy file in classpath"
argument_list|)
throw|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_MACHINE_LIST
argument_list|,
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|resolver
return|;
block|}
comment|/**    * Initialiaze a main-memory {@link FederationStateStoreFacade} used for    * testing, wiht a mock resolver.    *    * @param subClusterInfos the list of subclusters to be served on    *                        getSubClusters invocations.    *    * @return the facade.    *    * @throws YarnException in case the initialization is not successful.    */
DECL|method|initFacade ( List<SubClusterInfo> subClusterInfos, SubClusterPolicyConfiguration policyConfiguration)
specifier|public
specifier|static
name|FederationStateStoreFacade
name|initFacade
parameter_list|(
name|List
argument_list|<
name|SubClusterInfo
argument_list|>
name|subClusterInfos
parameter_list|,
name|SubClusterPolicyConfiguration
name|policyConfiguration
parameter_list|)
throws|throws
name|YarnException
block|{
name|FederationStateStoreFacade
name|goodFacade
init|=
name|FederationStateStoreFacade
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|FederationStateStore
name|fss
init|=
name|mock
argument_list|(
name|FederationStateStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|GetSubClustersInfoResponse
name|response
init|=
name|GetSubClustersInfoResponse
operator|.
name|newInstance
argument_list|(
name|subClusterInfos
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fss
operator|.
name|getSubClusters
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SubClusterPolicyConfiguration
argument_list|>
name|configurations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|configurations
operator|.
name|add
argument_list|(
name|policyConfiguration
argument_list|)
expr_stmt|;
name|GetSubClusterPoliciesConfigurationsResponse
name|policiesResponse
init|=
name|GetSubClusterPoliciesConfigurationsResponse
operator|.
name|newInstance
argument_list|(
name|configurations
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fss
operator|.
name|getPoliciesConfigurations
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|policiesResponse
argument_list|)
expr_stmt|;
name|GetSubClusterPolicyConfigurationResponse
name|policyResponse
init|=
name|GetSubClusterPolicyConfigurationResponse
operator|.
name|newInstance
argument_list|(
name|policyConfiguration
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fss
operator|.
name|getPolicyConfiguration
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|policyResponse
argument_list|)
expr_stmt|;
name|goodFacade
operator|.
name|reinitialize
argument_list|(
name|fss
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|goodFacade
return|;
block|}
comment|/**    * Initialiaze a main-memory {@link FederationStateStoreFacade} used for    * testing, wiht a mock resolver.    *    * @return the facade.    *    * @throws YarnException in case the initialization is not successful.    */
DECL|method|initFacade ()
specifier|public
specifier|static
name|FederationStateStoreFacade
name|initFacade
parameter_list|()
throws|throws
name|YarnException
block|{
return|return
name|initFacade
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
name|mock
argument_list|(
name|SubClusterPolicyConfiguration
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

