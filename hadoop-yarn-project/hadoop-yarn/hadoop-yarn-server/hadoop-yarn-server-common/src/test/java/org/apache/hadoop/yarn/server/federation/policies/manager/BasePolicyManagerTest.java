begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.policies.manager
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
name|manager
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * This class provides common test methods for testing {@code  * FederationPolicyManager}s.  */
end_comment

begin_class
DECL|class|BasePolicyManagerTest
specifier|public
specifier|abstract
class|class
name|BasePolicyManagerTest
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:visibilitymodifier"
argument_list|)
DECL|field|wfp
specifier|protected
name|FederationPolicyManager
name|wfp
init|=
literal|null
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:visibilitymodifier"
argument_list|)
DECL|field|expectedPolicyManager
specifier|protected
name|Class
name|expectedPolicyManager
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:visibilitymodifier"
argument_list|)
DECL|field|expectedAMRMProxyPolicy
specifier|protected
name|Class
name|expectedAMRMProxyPolicy
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:visibilitymodifier"
argument_list|)
DECL|field|expectedRouterPolicy
specifier|protected
name|Class
name|expectedRouterPolicy
decl_stmt|;
annotation|@
name|Test
DECL|method|testSerializeAndInstantiate ()
specifier|public
name|void
name|testSerializeAndInstantiate
parameter_list|()
throws|throws
name|Exception
block|{
name|serializeAndDeserializePolicyManager
argument_list|(
name|wfp
argument_list|,
name|expectedPolicyManager
argument_list|,
name|expectedAMRMProxyPolicy
argument_list|,
name|expectedRouterPolicy
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
DECL|method|testSerializeAndInstantiateBad1 ()
specifier|public
name|void
name|testSerializeAndInstantiateBad1
parameter_list|()
throws|throws
name|Exception
block|{
name|serializeAndDeserializePolicyManager
argument_list|(
name|wfp
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|expectedAMRMProxyPolicy
argument_list|,
name|expectedRouterPolicy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AssertionError
operator|.
name|class
argument_list|)
DECL|method|testSerializeAndInstantiateBad2 ()
specifier|public
name|void
name|testSerializeAndInstantiateBad2
parameter_list|()
throws|throws
name|Exception
block|{
name|serializeAndDeserializePolicyManager
argument_list|(
name|wfp
argument_list|,
name|expectedPolicyManager
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|expectedRouterPolicy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AssertionError
operator|.
name|class
argument_list|)
DECL|method|testSerializeAndInstantiateBad3 ()
specifier|public
name|void
name|testSerializeAndInstantiateBad3
parameter_list|()
throws|throws
name|Exception
block|{
name|serializeAndDeserializePolicyManager
argument_list|(
name|wfp
argument_list|,
name|expectedPolicyManager
argument_list|,
name|expectedAMRMProxyPolicy
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|serializeAndDeserializePolicyManager ( FederationPolicyManager wfp, Class policyManagerType, Class expAMRMProxyPolicy, Class expRouterPolicy)
specifier|protected
specifier|static
name|void
name|serializeAndDeserializePolicyManager
parameter_list|(
name|FederationPolicyManager
name|wfp
parameter_list|,
name|Class
name|policyManagerType
parameter_list|,
name|Class
name|expAMRMProxyPolicy
parameter_list|,
name|Class
name|expRouterPolicy
parameter_list|)
throws|throws
name|Exception
block|{
comment|// serializeConf it in a context
name|SubClusterPolicyConfiguration
name|fpc
init|=
name|wfp
operator|.
name|serializeConf
argument_list|()
decl_stmt|;
name|fpc
operator|.
name|setType
argument_list|(
name|policyManagerType
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|FederationPolicyInitializationContext
name|context
init|=
operator|new
name|FederationPolicyInitializationContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setSubClusterPolicyConfiguration
argument_list|(
name|fpc
argument_list|)
expr_stmt|;
name|context
operator|.
name|setFederationStateStoreFacade
argument_list|(
name|FederationPoliciesTestUtil
operator|.
name|initFacade
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setFederationSubclusterResolver
argument_list|(
name|FederationPoliciesTestUtil
operator|.
name|initResolver
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setHomeSubcluster
argument_list|(
name|SubClusterId
operator|.
name|newInstance
argument_list|(
literal|"homesubcluster"
argument_list|)
argument_list|)
expr_stmt|;
comment|// based on the "context" created instantiate new class and use it
name|Class
name|c
init|=
name|Class
operator|.
name|forName
argument_list|(
name|wfp
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
decl_stmt|;
name|FederationPolicyManager
name|wfp2
init|=
operator|(
name|FederationPolicyManager
operator|)
name|c
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|FederationAMRMProxyPolicy
name|federationAMRMProxyPolicy
init|=
name|wfp2
operator|.
name|getAMRMPolicy
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FederationRouterPolicy
name|federationRouterPolicy
init|=
name|wfp2
operator|.
name|getRouterPolicy
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|federationAMRMProxyPolicy
argument_list|)
operator|.
name|isExactlyInstanceOf
argument_list|(
name|expAMRMProxyPolicy
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|federationRouterPolicy
argument_list|)
operator|.
name|isExactlyInstanceOf
argument_list|(
name|expRouterPolicy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

