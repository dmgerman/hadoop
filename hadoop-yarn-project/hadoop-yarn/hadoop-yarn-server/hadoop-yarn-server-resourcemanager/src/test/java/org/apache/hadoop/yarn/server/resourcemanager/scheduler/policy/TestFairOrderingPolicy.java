begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.policy
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
name|policy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|Priority
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
name|capacity
operator|.
name|CapacitySchedulerConfiguration
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
name|common
operator|.
name|fica
operator|.
name|FiCaSchedulerApp
import|;
end_import

begin_class
DECL|class|TestFairOrderingPolicy
specifier|public
class|class
name|TestFairOrderingPolicy
block|{
DECL|field|GB
specifier|final
specifier|static
name|int
name|GB
init|=
literal|1024
decl_stmt|;
annotation|@
name|Test
DECL|method|testSimpleComparison ()
specifier|public
name|void
name|testSimpleComparison
parameter_list|()
block|{
name|FairOrderingPolicy
argument_list|<
name|MockSchedulableEntity
argument_list|>
name|policy
init|=
operator|new
name|FairOrderingPolicy
argument_list|<
name|MockSchedulableEntity
argument_list|>
argument_list|()
decl_stmt|;
name|MockSchedulableEntity
name|r1
init|=
operator|new
name|MockSchedulableEntity
argument_list|()
decl_stmt|;
name|MockSchedulableEntity
name|r2
init|=
operator|new
name|MockSchedulableEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|policy
operator|.
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|//consumption
name|r1
operator|.
name|setUsed
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|AbstractComparatorOrderingPolicy
operator|.
name|updateSchedulingResourceUsage
argument_list|(
name|r1
operator|.
name|getSchedulingResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|policy
operator|.
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSizeBasedWeight ()
specifier|public
name|void
name|testSizeBasedWeight
parameter_list|()
block|{
name|FairOrderingPolicy
argument_list|<
name|MockSchedulableEntity
argument_list|>
name|policy
init|=
operator|new
name|FairOrderingPolicy
argument_list|<
name|MockSchedulableEntity
argument_list|>
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setSizeBasedWeight
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MockSchedulableEntity
name|r1
init|=
operator|new
name|MockSchedulableEntity
argument_list|()
decl_stmt|;
name|MockSchedulableEntity
name|r2
init|=
operator|new
name|MockSchedulableEntity
argument_list|()
decl_stmt|;
comment|//No changes, equal
name|Assert
operator|.
name|assertTrue
argument_list|(
name|policy
operator|.
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setUsed
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|4
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|r2
operator|.
name|setUsed
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|4
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setPending
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|4
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|r2
operator|.
name|setPending
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|4
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|AbstractComparatorOrderingPolicy
operator|.
name|updateSchedulingResourceUsage
argument_list|(
name|r1
operator|.
name|getSchedulingResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractComparatorOrderingPolicy
operator|.
name|updateSchedulingResourceUsage
argument_list|(
name|r2
operator|.
name|getSchedulingResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
comment|//Same, equal
name|Assert
operator|.
name|assertTrue
argument_list|(
name|policy
operator|.
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|r2
operator|.
name|setUsed
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|5
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|r2
operator|.
name|setPending
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|5
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|AbstractComparatorOrderingPolicy
operator|.
name|updateSchedulingResourceUsage
argument_list|(
name|r2
operator|.
name|getSchedulingResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
comment|//More demand and consumption, but not enough more demand to overcome
comment|//additional consumption
name|Assert
operator|.
name|assertTrue
argument_list|(
name|policy
operator|.
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
comment|//High demand, enough to reverse sbw
name|r2
operator|.
name|setPending
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|100
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|AbstractComparatorOrderingPolicy
operator|.
name|updateSchedulingResourceUsage
argument_list|(
name|r2
operator|.
name|getSchedulingResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|policy
operator|.
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIterators ()
specifier|public
name|void
name|testIterators
parameter_list|()
block|{
name|OrderingPolicy
argument_list|<
name|MockSchedulableEntity
argument_list|>
name|schedOrder
init|=
operator|new
name|FairOrderingPolicy
argument_list|<
name|MockSchedulableEntity
argument_list|>
argument_list|()
decl_stmt|;
name|MockSchedulableEntity
name|msp1
init|=
operator|new
name|MockSchedulableEntity
argument_list|()
decl_stmt|;
name|MockSchedulableEntity
name|msp2
init|=
operator|new
name|MockSchedulableEntity
argument_list|()
decl_stmt|;
name|MockSchedulableEntity
name|msp3
init|=
operator|new
name|MockSchedulableEntity
argument_list|()
decl_stmt|;
name|msp1
operator|.
name|setId
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|msp2
operator|.
name|setId
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|msp3
operator|.
name|setId
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|msp1
operator|.
name|setUsed
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|msp2
operator|.
name|setUsed
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|msp3
operator|.
name|setUsed
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|AbstractComparatorOrderingPolicy
operator|.
name|updateSchedulingResourceUsage
argument_list|(
name|msp1
operator|.
name|getSchedulingResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractComparatorOrderingPolicy
operator|.
name|updateSchedulingResourceUsage
argument_list|(
name|msp2
operator|.
name|getSchedulingResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractComparatorOrderingPolicy
operator|.
name|updateSchedulingResourceUsage
argument_list|(
name|msp2
operator|.
name|getSchedulingResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
name|schedOrder
operator|.
name|addSchedulableEntity
argument_list|(
name|msp1
argument_list|)
expr_stmt|;
name|schedOrder
operator|.
name|addSchedulableEntity
argument_list|(
name|msp2
argument_list|)
expr_stmt|;
name|schedOrder
operator|.
name|addSchedulableEntity
argument_list|(
name|msp3
argument_list|)
expr_stmt|;
comment|//Assignment, least to greatest consumption
name|checkIds
argument_list|(
name|schedOrder
operator|.
name|getAssignmentIterator
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|,
literal|"2"
block|,
literal|"1"
block|}
argument_list|)
expr_stmt|;
comment|//Preemption, greatest to least
name|checkIds
argument_list|(
name|schedOrder
operator|.
name|getPreemptionIterator
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|}
argument_list|)
expr_stmt|;
comment|//Change value without inform, should see no change
name|msp2
operator|.
name|setUsed
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|checkIds
argument_list|(
name|schedOrder
operator|.
name|getAssignmentIterator
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|,
literal|"2"
block|,
literal|"1"
block|}
argument_list|)
expr_stmt|;
name|checkIds
argument_list|(
name|schedOrder
operator|.
name|getPreemptionIterator
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|}
argument_list|)
expr_stmt|;
comment|//Do inform, will reorder
name|schedOrder
operator|.
name|containerAllocated
argument_list|(
name|msp2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkIds
argument_list|(
name|schedOrder
operator|.
name|getAssignmentIterator
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|,
literal|"1"
block|,
literal|"2"
block|}
argument_list|)
expr_stmt|;
name|checkIds
argument_list|(
name|schedOrder
operator|.
name|getPreemptionIterator
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2"
block|,
literal|"1"
block|,
literal|"3"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|checkIds (Iterator<MockSchedulableEntity> si, String[] ids)
specifier|public
name|void
name|checkIds
parameter_list|(
name|Iterator
argument_list|<
name|MockSchedulableEntity
argument_list|>
name|si
parameter_list|,
name|String
index|[]
name|ids
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ids
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|si
operator|.
name|next
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|ids
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

