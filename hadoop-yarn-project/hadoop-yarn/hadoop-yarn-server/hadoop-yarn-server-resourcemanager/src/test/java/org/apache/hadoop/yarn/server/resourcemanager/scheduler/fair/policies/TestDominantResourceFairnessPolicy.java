begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|policies
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Resource
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
name|resourcemanager
operator|.
name|resource
operator|.
name|ResourceType
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
name|resourcemanager
operator|.
name|resource
operator|.
name|ResourceWeights
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|FakeSchedulable
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|Schedulable
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
name|utils
operator|.
name|BuilderUtils
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
name|Test
import|;
end_import

begin_comment
comment|/**  * comparator.compare(sched1, sched2)< 0 means that sched1 should get a  * container before sched2  */
end_comment

begin_class
DECL|class|TestDominantResourceFairnessPolicy
specifier|public
class|class
name|TestDominantResourceFairnessPolicy
block|{
DECL|method|createComparator (int clusterMem, int clusterCpu)
specifier|private
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
name|createComparator
parameter_list|(
name|int
name|clusterMem
parameter_list|,
name|int
name|clusterCpu
parameter_list|)
block|{
name|DominantResourceFairnessPolicy
name|policy
init|=
operator|new
name|DominantResourceFairnessPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|initialize
argument_list|(
name|BuilderUtils
operator|.
name|newResource
argument_list|(
name|clusterMem
argument_list|,
name|clusterCpu
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|policy
operator|.
name|getComparator
argument_list|()
return|;
block|}
DECL|method|createSchedulable (int memUsage, int cpuUsage)
specifier|private
name|Schedulable
name|createSchedulable
parameter_list|(
name|int
name|memUsage
parameter_list|,
name|int
name|cpuUsage
parameter_list|)
block|{
return|return
name|createSchedulable
argument_list|(
name|memUsage
argument_list|,
name|cpuUsage
argument_list|,
name|ResourceWeights
operator|.
name|NEUTRAL
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|createSchedulable (int memUsage, int cpuUsage, int minMemShare, int minCpuShare)
specifier|private
name|Schedulable
name|createSchedulable
parameter_list|(
name|int
name|memUsage
parameter_list|,
name|int
name|cpuUsage
parameter_list|,
name|int
name|minMemShare
parameter_list|,
name|int
name|minCpuShare
parameter_list|)
block|{
return|return
name|createSchedulable
argument_list|(
name|memUsage
argument_list|,
name|cpuUsage
argument_list|,
name|ResourceWeights
operator|.
name|NEUTRAL
argument_list|,
name|minMemShare
argument_list|,
name|minCpuShare
argument_list|)
return|;
block|}
DECL|method|createSchedulable (int memUsage, int cpuUsage, ResourceWeights weights)
specifier|private
name|Schedulable
name|createSchedulable
parameter_list|(
name|int
name|memUsage
parameter_list|,
name|int
name|cpuUsage
parameter_list|,
name|ResourceWeights
name|weights
parameter_list|)
block|{
return|return
name|createSchedulable
argument_list|(
name|memUsage
argument_list|,
name|cpuUsage
argument_list|,
name|weights
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|createSchedulable (int memUsage, int cpuUsage, ResourceWeights weights, int minMemShare, int minCpuShare)
specifier|private
name|Schedulable
name|createSchedulable
parameter_list|(
name|int
name|memUsage
parameter_list|,
name|int
name|cpuUsage
parameter_list|,
name|ResourceWeights
name|weights
parameter_list|,
name|int
name|minMemShare
parameter_list|,
name|int
name|minCpuShare
parameter_list|)
block|{
name|Resource
name|usage
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
name|memUsage
argument_list|,
name|cpuUsage
argument_list|)
decl_stmt|;
name|Resource
name|minShare
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
name|minMemShare
argument_list|,
name|minCpuShare
argument_list|)
decl_stmt|;
return|return
operator|new
name|FakeSchedulable
argument_list|(
name|minShare
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|weights
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|,
name|usage
argument_list|,
literal|0l
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testSameDominantResource ()
specifier|public
name|void
name|testSameDominantResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|4
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|1000
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|2000
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDifferentDominantResource ()
specifier|public
name|void
name|testDifferentDominantResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|4000
argument_list|,
literal|3
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|2000
argument_list|,
literal|5
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneIsNeedy ()
specifier|public
name|void
name|testOneIsNeedy
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|2000
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|4000
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBothAreNeedy ()
specifier|public
name|void
name|testBothAreNeedy
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|100
argument_list|)
operator|.
name|compare
argument_list|(
comment|// dominant share is 2000/8000
name|createSchedulable
argument_list|(
literal|2000
argument_list|,
literal|5
argument_list|)
argument_list|,
comment|// dominant share is 4000/8000
name|createSchedulable
argument_list|(
literal|4000
argument_list|,
literal|3
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|100
argument_list|)
operator|.
name|compare
argument_list|(
comment|// dominant min share is 2/3
name|createSchedulable
argument_list|(
literal|2000
argument_list|,
literal|5
argument_list|,
literal|3000
argument_list|,
literal|6
argument_list|)
argument_list|,
comment|// dominant min share is 4/5
name|createSchedulable
argument_list|(
literal|4000
argument_list|,
literal|3
argument_list|,
literal|5000
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEvenWeightsSameDominantResource ()
specifier|public
name|void
name|testEvenWeightsSameDominantResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|3000
argument_list|,
literal|1
argument_list|,
operator|new
name|ResourceWeights
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|2000
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|1000
argument_list|,
literal|3
argument_list|,
operator|new
name|ResourceWeights
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|1000
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEvenWeightsDifferentDominantResource ()
specifier|public
name|void
name|testEvenWeightsDifferentDominantResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|1000
argument_list|,
literal|3
argument_list|,
operator|new
name|ResourceWeights
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|2000
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|3000
argument_list|,
literal|1
argument_list|,
operator|new
name|ResourceWeights
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|1000
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnevenWeightsSameDominantResource ()
specifier|public
name|void
name|testUnevenWeightsSameDominantResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|3000
argument_list|,
literal|1
argument_list|,
operator|new
name|ResourceWeights
argument_list|(
literal|2.0f
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|2000
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|1000
argument_list|,
literal|3
argument_list|,
operator|new
name|ResourceWeights
argument_list|(
literal|1.0f
argument_list|,
literal|2.0f
argument_list|)
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|1000
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnevenWeightsDifferentDominantResource ()
specifier|public
name|void
name|testUnevenWeightsDifferentDominantResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|1000
argument_list|,
literal|3
argument_list|,
operator|new
name|ResourceWeights
argument_list|(
literal|1.0f
argument_list|,
literal|2.0f
argument_list|)
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|2000
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|createComparator
argument_list|(
literal|8000
argument_list|,
literal|8
argument_list|)
operator|.
name|compare
argument_list|(
name|createSchedulable
argument_list|(
literal|3000
argument_list|,
literal|1
argument_list|,
operator|new
name|ResourceWeights
argument_list|(
literal|2.0f
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
argument_list|,
name|createSchedulable
argument_list|(
literal|1000
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCalculateShares ()
specifier|public
name|void
name|testCalculateShares
parameter_list|()
block|{
name|Resource
name|used
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|10
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|Resource
name|capacity
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|100
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|ResourceType
index|[]
name|resourceOrder
init|=
operator|new
name|ResourceType
index|[
literal|2
index|]
decl_stmt|;
name|ResourceWeights
name|shares
init|=
operator|new
name|ResourceWeights
argument_list|()
decl_stmt|;
name|DominantResourceFairnessPolicy
operator|.
name|DominantResourceFairnessComparator
name|comparator
init|=
operator|new
name|DominantResourceFairnessPolicy
operator|.
name|DominantResourceFairnessComparator
argument_list|()
decl_stmt|;
name|comparator
operator|.
name|calculateShares
argument_list|(
name|used
argument_list|,
name|capacity
argument_list|,
name|shares
argument_list|,
name|resourceOrder
argument_list|,
name|ResourceWeights
operator|.
name|NEUTRAL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|.1
argument_list|,
name|shares
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|MEMORY
argument_list|)
argument_list|,
literal|.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|.5
argument_list|,
name|shares
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|CPU
argument_list|)
argument_list|,
literal|.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResourceType
operator|.
name|CPU
argument_list|,
name|resourceOrder
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResourceType
operator|.
name|MEMORY
argument_list|,
name|resourceOrder
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

