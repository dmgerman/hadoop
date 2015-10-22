begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*******************************************************************************  *   Licensed to the Apache Software Foundation (ASF) under one  *   or more contributor license agreements.  See the NOTICE file  *   distributed with this work for additional information  *   regarding copyright ownership.  The ASF licenses this file  *   to you under the Apache License, Version 2.0 (the  *   "License"); you may not use this file except in compliance  *   with the License.  You may obtain a copy of the License at  *    *       http://www.apache.org/licenses/LICENSE-2.0  *    *   Unless required by applicable law or agreed to in writing, software  *   distributed under the License is distributed on an "AS IS" BASIS,  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *   See the License for the specific language governing permissions and  *   limitations under the License.  *******************************************************************************/
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation.planning
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
name|reservation
operator|.
name|planning
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
name|assertNotNull
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
name|assertNull
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
name|TreeMap
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
name|ReservationDefinition
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
name|ReservationId
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
name|ReservationRequest
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
name|RMContext
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
name|reservation
operator|.
name|InMemoryPlan
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
name|reservation
operator|.
name|InMemoryReservationAllocation
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
name|reservation
operator|.
name|NoOverCommitPolicy
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
name|reservation
operator|.
name|ReservationAllocation
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
name|reservation
operator|.
name|ReservationInterval
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
name|reservation
operator|.
name|ReservationSchedulerConfiguration
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
name|reservation
operator|.
name|ReservationSystemTestUtil
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
name|reservation
operator|.
name|ReservationSystemUtil
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
name|reservation
operator|.
name|SharingPolicy
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
name|reservation
operator|.
name|exceptions
operator|.
name|PlanningException
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
name|QueueMetrics
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
name|Clock
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
name|DefaultResourceCalculator
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
name|ResourceCalculator
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

begin_class
DECL|class|TestSimpleCapacityReplanner
specifier|public
class|class
name|TestSimpleCapacityReplanner
block|{
annotation|@
name|Test
DECL|method|testReplanningPlanCapacityLoss ()
specifier|public
name|void
name|testReplanningPlanCapacityLoss
parameter_list|()
throws|throws
name|PlanningException
block|{
name|Resource
name|clusterCapacity
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|100
operator|*
literal|1024
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Resource
name|minAlloc
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Resource
name|maxAlloc
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
operator|*
literal|8
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|ResourceCalculator
name|res
init|=
operator|new
name|DefaultResourceCalculator
argument_list|()
decl_stmt|;
name|long
name|step
init|=
literal|1L
decl_stmt|;
name|Clock
name|clock
init|=
name|mock
argument_list|(
name|Clock
operator|.
name|class
argument_list|)
decl_stmt|;
name|ReservationAgent
name|agent
init|=
name|mock
argument_list|(
name|ReservationAgent
operator|.
name|class
argument_list|)
decl_stmt|;
name|SharingPolicy
name|policy
init|=
operator|new
name|NoOverCommitPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|init
argument_list|(
literal|"root.dedicated"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|QueueMetrics
name|queueMetrics
init|=
name|mock
argument_list|(
name|QueueMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|SimpleCapacityReplanner
name|enf
init|=
operator|new
name|SimpleCapacityReplanner
argument_list|(
name|clock
argument_list|)
decl_stmt|;
name|RMContext
name|context
init|=
name|ReservationSystemTestUtil
operator|.
name|createMockRMContext
argument_list|()
decl_stmt|;
name|ReservationSchedulerConfiguration
name|conf
init|=
name|mock
argument_list|(
name|ReservationSchedulerConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|conf
operator|.
name|getEnforcementWindow
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|6L
argument_list|)
expr_stmt|;
name|enf
operator|.
name|init
argument_list|(
literal|"blah"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Initialize the plan with more resources
name|InMemoryPlan
name|plan
init|=
operator|new
name|InMemoryPlan
argument_list|(
name|queueMetrics
argument_list|,
name|policy
argument_list|,
name|agent
argument_list|,
name|clusterCapacity
argument_list|,
name|step
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|,
name|maxAlloc
argument_list|,
literal|"dedicated"
argument_list|,
name|enf
argument_list|,
literal|true
argument_list|,
name|context
argument_list|,
name|clock
argument_list|)
decl_stmt|;
comment|// add reservation filling the plan (separating them 1ms, so we are sure
comment|// s2 follows s1 on acceptance
name|long
name|ts
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ReservationId
name|r1
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|ts
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|int
index|[]
name|f5
init|=
block|{
literal|20
block|,
literal|20
block|,
literal|20
block|,
literal|20
block|,
literal|20
block|}
decl_stmt|;
name|ReservationDefinition
name|rDef
init|=
name|ReservationSystemTestUtil
operator|.
name|createSimpleReservationDefinition
argument_list|(
literal|0
argument_list|,
literal|0
operator|+
name|f5
operator|.
name|length
argument_list|,
name|f5
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|toString
argument_list|()
argument_list|,
name|plan
operator|.
name|addReservation
argument_list|(
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|r1
argument_list|,
name|rDef
argument_list|,
literal|"u3"
argument_list|,
literal|"dedicated"
argument_list|,
literal|0
argument_list|,
literal|0
operator|+
name|f5
operator|.
name|length
argument_list|,
name|generateAllocation
argument_list|(
literal|0
argument_list|,
name|f5
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|ReservationId
name|r2
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|ts
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|toString
argument_list|()
argument_list|,
name|plan
operator|.
name|addReservation
argument_list|(
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|r2
argument_list|,
name|rDef
argument_list|,
literal|"u4"
argument_list|,
literal|"dedicated"
argument_list|,
literal|0
argument_list|,
literal|0
operator|+
name|f5
operator|.
name|length
argument_list|,
name|generateAllocation
argument_list|(
literal|0
argument_list|,
name|f5
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|2L
argument_list|)
expr_stmt|;
name|ReservationId
name|r3
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|ts
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|toString
argument_list|()
argument_list|,
name|plan
operator|.
name|addReservation
argument_list|(
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|r3
argument_list|,
name|rDef
argument_list|,
literal|"u5"
argument_list|,
literal|"dedicated"
argument_list|,
literal|0
argument_list|,
literal|0
operator|+
name|f5
operator|.
name|length
argument_list|,
name|generateAllocation
argument_list|(
literal|0
argument_list|,
name|f5
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|3L
argument_list|)
expr_stmt|;
name|ReservationId
name|r4
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|ts
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|toString
argument_list|()
argument_list|,
name|plan
operator|.
name|addReservation
argument_list|(
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|r4
argument_list|,
name|rDef
argument_list|,
literal|"u6"
argument_list|,
literal|"dedicated"
argument_list|,
literal|0
argument_list|,
literal|0
operator|+
name|f5
operator|.
name|length
argument_list|,
name|generateAllocation
argument_list|(
literal|0
argument_list|,
name|f5
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|4L
argument_list|)
expr_stmt|;
name|ReservationId
name|r5
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|ts
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|toString
argument_list|()
argument_list|,
name|plan
operator|.
name|addReservation
argument_list|(
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|r5
argument_list|,
name|rDef
argument_list|,
literal|"u7"
argument_list|,
literal|"dedicated"
argument_list|,
literal|0
argument_list|,
literal|0
operator|+
name|f5
operator|.
name|length
argument_list|,
name|generateAllocation
argument_list|(
literal|0
argument_list|,
name|f5
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|int
index|[]
name|f6
init|=
block|{
literal|50
block|,
literal|50
block|,
literal|50
block|,
literal|50
block|,
literal|50
block|}
decl_stmt|;
name|ReservationId
name|r6
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|ts
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|toString
argument_list|()
argument_list|,
name|plan
operator|.
name|addReservation
argument_list|(
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|r6
argument_list|,
name|rDef
argument_list|,
literal|"u3"
argument_list|,
literal|"dedicated"
argument_list|,
literal|10
argument_list|,
literal|10
operator|+
name|f6
operator|.
name|length
argument_list|,
name|generateAllocation
argument_list|(
literal|10
argument_list|,
name|f6
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|6L
argument_list|)
expr_stmt|;
name|ReservationId
name|r7
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|ts
argument_list|,
literal|7
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|toString
argument_list|()
argument_list|,
name|plan
operator|.
name|addReservation
argument_list|(
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|r7
argument_list|,
name|rDef
argument_list|,
literal|"u4"
argument_list|,
literal|"dedicated"
argument_list|,
literal|10
argument_list|,
literal|10
operator|+
name|f6
operator|.
name|length
argument_list|,
name|generateAllocation
argument_list|(
literal|10
argument_list|,
name|f6
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove some of the resources (requires replanning)
name|plan
operator|.
name|setTotalCapacity
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|70
operator|*
literal|1024
argument_list|,
literal|70
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
comment|// run the replanner
name|enf
operator|.
name|plan
argument_list|(
name|plan
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// check which reservation are still present
name|assertNotNull
argument_list|(
name|plan
operator|.
name|getReservationById
argument_list|(
name|r1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|plan
operator|.
name|getReservationById
argument_list|(
name|r2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|plan
operator|.
name|getReservationById
argument_list|(
name|r3
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|plan
operator|.
name|getReservationById
argument_list|(
name|r6
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|plan
operator|.
name|getReservationById
argument_list|(
name|r7
argument_list|)
argument_list|)
expr_stmt|;
comment|// and which ones are removed
name|assertNull
argument_list|(
name|plan
operator|.
name|getReservationById
argument_list|(
name|r4
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|plan
operator|.
name|getReservationById
argument_list|(
name|r5
argument_list|)
argument_list|)
expr_stmt|;
comment|// check resources at each moment in time no more exceed capacity
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
name|int
name|tot
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ReservationAllocation
name|r
range|:
name|plan
operator|.
name|getReservationsAtTime
argument_list|(
name|i
argument_list|)
control|)
block|{
name|tot
operator|=
name|r
operator|.
name|getResourcesAtTime
argument_list|(
name|i
argument_list|)
operator|.
name|getMemory
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|tot
operator|<=
literal|70
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|generateAllocation ( int startTime, int[] alloc)
specifier|private
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|generateAllocation
parameter_list|(
name|int
name|startTime
parameter_list|,
name|int
index|[]
name|alloc
parameter_list|)
block|{
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|req
init|=
operator|new
name|TreeMap
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
argument_list|()
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
name|alloc
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|req
operator|.
name|put
argument_list|(
operator|new
name|ReservationInterval
argument_list|(
name|startTime
operator|+
name|i
argument_list|,
name|startTime
operator|+
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|ReservationSystemUtil
operator|.
name|toResource
argument_list|(
name|ReservationRequest
operator|.
name|newInstance
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|alloc
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|req
return|;
block|}
block|}
end_class

end_unit

