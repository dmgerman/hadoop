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
comment|/**  * Simple test class for the {@link LoadBasedRouterPolicy}. Test that the load  * is properly considered for allocation.  */
end_comment

begin_class
DECL|class|TestLoadBasedRouterPolicy
specifier|public
class|class
name|TestLoadBasedRouterPolicy
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
name|LoadBasedRouterPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|setPolicyInfo
argument_list|(
operator|new
name|WeightedPolicyInfo
argument_list|()
argument_list|)
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
comment|// simulate 20 active subclusters
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
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
name|String
operator|.
name|format
argument_list|(
literal|"sc%02d"
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|SubClusterInfo
name|federationSubClusterInfo
init|=
name|SubClusterInfo
operator|.
name|newInstance
argument_list|(
name|sc
operator|.
name|toId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|SubClusterState
operator|.
name|SC_RUNNING
argument_list|,
operator|-
literal|1
argument_list|,
name|generateClusterMetricsInfo
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
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
name|federationSubClusterInfo
argument_list|)
expr_stmt|;
name|float
name|weight
init|=
name|getRand
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|5
condition|)
block|{
name|weight
operator|=
literal|1.0f
expr_stmt|;
block|}
comment|// 5% chance we omit one of the weights
if|if
condition|(
name|i
operator|<=
literal|5
operator|||
name|getRand
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|>
literal|0.05f
condition|)
block|{
name|routerWeights
operator|.
name|put
argument_list|(
name|sc
argument_list|,
name|weight
argument_list|)
expr_stmt|;
name|amrmWeights
operator|.
name|put
argument_list|(
name|sc
argument_list|,
name|weight
argument_list|)
expr_stmt|;
block|}
block|}
name|getPolicyInfo
argument_list|()
operator|.
name|setRouterPolicyWeights
argument_list|(
name|routerWeights
argument_list|)
expr_stmt|;
name|getPolicyInfo
argument_list|()
operator|.
name|setAMRMPolicyWeights
argument_list|(
name|amrmWeights
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
block|}
DECL|method|generateClusterMetricsInfo (int id)
specifier|private
name|String
name|generateClusterMetricsInfo
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|long
name|mem
init|=
literal|1024
operator|*
name|getRand
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|277
operator|*
literal|100
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// plant a best cluster
if|if
condition|(
name|id
operator|==
literal|5
condition|)
block|{
name|mem
operator|=
literal|1024
operator|*
literal|277
operator|*
literal|100
expr_stmt|;
block|}
name|String
name|clusterMetrics
init|=
literal|"{\"clusterMetrics\":{\"appsSubmitted\":65,"
operator|+
literal|"\"appsCompleted\":64,"
operator|+
literal|"\"appsPending\":0,\"appsRunning\":0,\"appsFailed\":0,"
operator|+
literal|"\"appsKilled\":1,\"reservedMB\":0,\"availableMB\":"
operator|+
name|mem
operator|+
literal|","
operator|+
literal|"\"allocatedMB\":0,\"reservedVirtualCores\":0,"
operator|+
literal|"\"availableVirtualCores\":2216,\"allocatedVirtualCores\":0,"
operator|+
literal|"\"containersAllocated\":0,\"containersReserved\":0,"
operator|+
literal|"\"containersPending\":0,\"totalMB\":28364800,"
operator|+
literal|"\"totalVirtualCores\":2216,\"totalNodes\":278,\"lostNodes\":1,"
operator|+
literal|"\"unhealthyNodes\":0,\"decommissionedNodes\":0,"
operator|+
literal|"\"rebootedNodes\":0,\"activeNodes\":277}}\n"
decl_stmt|;
return|return
name|clusterMetrics
return|;
block|}
annotation|@
name|Test
DECL|method|testLoadIsRespected ()
specifier|public
name|void
name|testLoadIsRespected
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
comment|// check the "planted" best cluster is chosen
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"sc05"
argument_list|,
name|chosen
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

