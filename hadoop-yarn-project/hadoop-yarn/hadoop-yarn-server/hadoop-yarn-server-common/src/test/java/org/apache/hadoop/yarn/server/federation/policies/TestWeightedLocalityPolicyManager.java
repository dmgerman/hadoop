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
name|LocalityMulticastAMRMProxyPolicy
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
name|router
operator|.
name|WeightedRandomRouterPolicy
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

begin_comment
comment|/**  * Simple test of {@link WeightedLocalityPolicyManager}.  */
end_comment

begin_class
DECL|class|TestWeightedLocalityPolicyManager
specifier|public
class|class
name|TestWeightedLocalityPolicyManager
extends|extends
name|BasePolicyManagerTest
block|{
DECL|field|policyInfo
specifier|private
name|WeightedPolicyInfo
name|policyInfo
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
comment|// configure a policy
name|wfp
operator|=
operator|new
name|WeightedLocalityPolicyManager
argument_list|()
expr_stmt|;
name|wfp
operator|.
name|setQueue
argument_list|(
literal|"queue1"
argument_list|)
expr_stmt|;
name|SubClusterId
name|sc1
init|=
name|SubClusterId
operator|.
name|newInstance
argument_list|(
literal|"sc1"
argument_list|)
decl_stmt|;
name|SubClusterId
name|sc2
init|=
name|SubClusterId
operator|.
name|newInstance
argument_list|(
literal|"sc2"
argument_list|)
decl_stmt|;
name|policyInfo
operator|=
operator|new
name|WeightedPolicyInfo
argument_list|()
expr_stmt|;
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
name|routerWeights
operator|.
name|put
argument_list|(
operator|new
name|SubClusterIdInfo
argument_list|(
name|sc1
argument_list|)
argument_list|,
literal|0.2f
argument_list|)
expr_stmt|;
name|routerWeights
operator|.
name|put
argument_list|(
operator|new
name|SubClusterIdInfo
argument_list|(
name|sc2
argument_list|)
argument_list|,
literal|0.8f
argument_list|)
expr_stmt|;
name|policyInfo
operator|.
name|setRouterPolicyWeights
argument_list|(
name|routerWeights
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|SubClusterIdInfo
argument_list|,
name|Float
argument_list|>
name|amrmWeights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|amrmWeights
operator|.
name|put
argument_list|(
operator|new
name|SubClusterIdInfo
argument_list|(
name|sc1
argument_list|)
argument_list|,
literal|0.2f
argument_list|)
expr_stmt|;
name|amrmWeights
operator|.
name|put
argument_list|(
operator|new
name|SubClusterIdInfo
argument_list|(
name|sc2
argument_list|)
argument_list|,
literal|0.8f
argument_list|)
expr_stmt|;
name|policyInfo
operator|.
name|setAMRMPolicyWeights
argument_list|(
name|amrmWeights
argument_list|)
expr_stmt|;
operator|(
operator|(
name|WeightedLocalityPolicyManager
operator|)
name|wfp
operator|)
operator|.
name|setWeightedPolicyInfo
argument_list|(
name|policyInfo
argument_list|)
expr_stmt|;
comment|//set expected params that the base test class will use for tests
name|expectedPolicyManager
operator|=
name|WeightedLocalityPolicyManager
operator|.
name|class
expr_stmt|;
name|expectedAMRMProxyPolicy
operator|=
name|LocalityMulticastAMRMProxyPolicy
operator|.
name|class
expr_stmt|;
name|expectedRouterPolicy
operator|=
name|WeightedRandomRouterPolicy
operator|.
name|class
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPolicyInfoSetCorrectly ()
specifier|public
name|void
name|testPolicyInfoSetCorrectly
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
comment|//check the policyInfo propagates through ser/der correctly
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
operator|(
name|WeightedLocalityPolicyManager
operator|)
name|wfp
operator|)
operator|.
name|getWeightedPolicyInfo
argument_list|()
argument_list|,
name|policyInfo
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

