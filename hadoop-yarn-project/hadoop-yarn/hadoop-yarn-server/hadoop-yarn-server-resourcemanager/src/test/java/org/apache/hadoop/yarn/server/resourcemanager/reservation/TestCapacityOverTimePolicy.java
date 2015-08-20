begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*******************************************************************************  *   Licensed to the Apache Software Foundation (ASF) under one  *   or more contributor license agreements.  See the NOTICE file  *   distributed with this work for additional information  *   regarding copyright ownership.  The ASF licenses this file  *   to you under the Apache License, Version 2.0 (the  *   "License"); you may not use this file except in compliance  *   with the License.  You may obtain a copy of the License at  *    *       http://www.apache.org/licenses/LICENSE-2.0  *    *   Unless required by applicable law or agreed to in writing, software  *   distributed under the License is distributed on an "AS IS" BASIS,  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *   See the License for the specific language governing permissions and  *   limitations under the License.  *******************************************************************************/
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation
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
name|assertTrue
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
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|reservation
operator|.
name|exceptions
operator|.
name|PlanningQuotaException
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
name|ResourceOverCommitException
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
name|planning
operator|.
name|ReservationAgent
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

begin_class
DECL|class|TestCapacityOverTimePolicy
specifier|public
class|class
name|TestCapacityOverTimePolicy
block|{
DECL|field|timeWindow
name|long
name|timeWindow
decl_stmt|;
DECL|field|step
name|long
name|step
decl_stmt|;
DECL|field|avgConstraint
name|float
name|avgConstraint
decl_stmt|;
DECL|field|instConstraint
name|float
name|instConstraint
decl_stmt|;
DECL|field|initTime
name|long
name|initTime
decl_stmt|;
DECL|field|plan
name|InMemoryPlan
name|plan
decl_stmt|;
DECL|field|mAgent
name|ReservationAgent
name|mAgent
decl_stmt|;
DECL|field|minAlloc
name|Resource
name|minAlloc
decl_stmt|;
DECL|field|res
name|ResourceCalculator
name|res
decl_stmt|;
DECL|field|maxAlloc
name|Resource
name|maxAlloc
decl_stmt|;
DECL|field|totCont
name|int
name|totCont
init|=
literal|1000000
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 24h window
name|timeWindow
operator|=
literal|86400000L
expr_stmt|;
comment|// 1 sec step
name|step
operator|=
literal|1000L
expr_stmt|;
comment|// 25% avg cap on capacity
name|avgConstraint
operator|=
literal|25
expr_stmt|;
comment|// 70% instantaneous cap on capacity
name|instConstraint
operator|=
literal|70
expr_stmt|;
name|initTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|minAlloc
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|res
operator|=
operator|new
name|DefaultResourceCalculator
argument_list|()
expr_stmt|;
name|maxAlloc
operator|=
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
expr_stmt|;
name|mAgent
operator|=
name|mock
argument_list|(
name|ReservationAgent
operator|.
name|class
argument_list|)
expr_stmt|;
name|ReservationSystemTestUtil
name|testUtil
init|=
operator|new
name|ReservationSystemTestUtil
argument_list|()
decl_stmt|;
name|QueueMetrics
name|rootQueueMetrics
init|=
name|mock
argument_list|(
name|QueueMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|reservationQ
init|=
name|testUtil
operator|.
name|getFullReservationQueueName
argument_list|()
decl_stmt|;
name|Resource
name|clusterResource
init|=
name|testUtil
operator|.
name|calculateClusterResource
argument_list|(
name|totCont
argument_list|)
decl_stmt|;
name|ReservationSchedulerConfiguration
name|conf
init|=
name|ReservationSystemTestUtil
operator|.
name|createConf
argument_list|(
name|reservationQ
argument_list|,
name|timeWindow
argument_list|,
name|instConstraint
argument_list|,
name|avgConstraint
argument_list|)
decl_stmt|;
name|CapacityOverTimePolicy
name|policy
init|=
operator|new
name|CapacityOverTimePolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|init
argument_list|(
name|reservationQ
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|plan
operator|=
operator|new
name|InMemoryPlan
argument_list|(
name|rootQueueMetrics
argument_list|,
name|policy
argument_list|,
name|mAgent
argument_list|,
name|clusterResource
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
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|generateData (int length, int val)
specifier|public
name|int
index|[]
name|generateData
parameter_list|(
name|int
name|length
parameter_list|,
name|int
name|val
parameter_list|)
block|{
name|int
index|[]
name|data
init|=
operator|new
name|int
index|[
name|length
index|]
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
name|val
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
annotation|@
name|Test
DECL|method|testSimplePass ()
specifier|public
name|void
name|testSimplePass
parameter_list|()
throws|throws
name|IOException
throws|,
name|PlanningException
block|{
comment|// generate allocation that simply fit within all constraints
name|int
index|[]
name|f
init|=
name|generateData
argument_list|(
literal|3600
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
literal|0.2
operator|*
name|totCont
argument_list|)
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u1"
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|f
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
name|initTime
argument_list|,
name|step
argument_list|,
name|f
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimplePass2 ()
specifier|public
name|void
name|testSimplePass2
parameter_list|()
throws|throws
name|IOException
throws|,
name|PlanningException
block|{
comment|// generate allocation from single tenant that exceed avg momentarily but
comment|// fit within
comment|// max instantanesou
name|int
index|[]
name|f
init|=
name|generateData
argument_list|(
literal|3600
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
literal|0.69
operator|*
name|totCont
argument_list|)
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u1"
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|f
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
name|initTime
argument_list|,
name|step
argument_list|,
name|f
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiTenantPass ()
specifier|public
name|void
name|testMultiTenantPass
parameter_list|()
throws|throws
name|IOException
throws|,
name|PlanningException
block|{
comment|// generate allocation from multiple tenants that barely fit in tot capacity
name|int
index|[]
name|f
init|=
name|generateData
argument_list|(
literal|3600
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
literal|0.25
operator|*
name|totCont
argument_list|)
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u"
operator|+
name|i
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|f
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
name|initTime
argument_list|,
name|step
argument_list|,
name|f
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ResourceOverCommitException
operator|.
name|class
argument_list|)
DECL|method|testMultiTenantFail ()
specifier|public
name|void
name|testMultiTenantFail
parameter_list|()
throws|throws
name|IOException
throws|,
name|PlanningException
block|{
comment|// generate allocation from multiple tenants that exceed tot capacity
name|int
index|[]
name|f
init|=
name|generateData
argument_list|(
literal|3600
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
literal|0.25
operator|*
name|totCont
argument_list|)
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u"
operator|+
name|i
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|f
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
name|initTime
argument_list|,
name|step
argument_list|,
name|f
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PlanningQuotaException
operator|.
name|class
argument_list|)
DECL|method|testInstFail ()
specifier|public
name|void
name|testInstFail
parameter_list|()
throws|throws
name|IOException
throws|,
name|PlanningException
block|{
comment|// generate allocation that exceed the instantaneous cap single-show
name|int
index|[]
name|f
init|=
name|generateData
argument_list|(
literal|3600
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
literal|0.71
operator|*
name|totCont
argument_list|)
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u1"
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|f
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
name|initTime
argument_list|,
name|step
argument_list|,
name|f
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"should not have accepted this"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInstFailBySum ()
specifier|public
name|void
name|testInstFailBySum
parameter_list|()
throws|throws
name|IOException
throws|,
name|PlanningException
block|{
comment|// generate allocation that exceed the instantaneous cap by sum
name|int
index|[]
name|f
init|=
name|generateData
argument_list|(
literal|3600
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
literal|0.3
operator|*
name|totCont
argument_list|)
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u1"
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|f
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
name|initTime
argument_list|,
name|step
argument_list|,
name|f
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u1"
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|f
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
name|initTime
argument_list|,
name|step
argument_list|,
name|f
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u1"
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|f
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
name|initTime
argument_list|,
name|step
argument_list|,
name|f
argument_list|)
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlanningQuotaException
name|p
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PlanningQuotaException
operator|.
name|class
argument_list|)
DECL|method|testFailAvg ()
specifier|public
name|void
name|testFailAvg
parameter_list|()
throws|throws
name|IOException
throws|,
name|PlanningException
block|{
comment|// generate an allocation which violates the 25% average single-shot
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
name|long
name|win
init|=
name|timeWindow
operator|/
literal|2
operator|+
literal|100
decl_stmt|;
name|int
name|cont
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
literal|0.5
operator|*
name|totCont
argument_list|)
decl_stmt|;
name|req
operator|.
name|put
argument_list|(
operator|new
name|ReservationInterval
argument_list|(
name|initTime
argument_list|,
name|initTime
operator|+
name|win
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
name|cont
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u1"
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|win
argument_list|,
name|req
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailAvgBySum ()
specifier|public
name|void
name|testFailAvgBySum
parameter_list|()
throws|throws
name|IOException
throws|,
name|PlanningException
block|{
comment|// generate an allocation which violates the 25% average by sum
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
name|long
name|win
init|=
literal|86400000
operator|/
literal|4
operator|+
literal|1
decl_stmt|;
name|int
name|cont
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
literal|0.5
operator|*
name|totCont
argument_list|)
decl_stmt|;
name|req
operator|.
name|put
argument_list|(
operator|new
name|ReservationInterval
argument_list|(
name|initTime
argument_list|,
name|initTime
operator|+
name|win
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
name|cont
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u1"
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|win
argument_list|,
name|req
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
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
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"u1"
argument_list|,
literal|"dedicated"
argument_list|,
name|initTime
argument_list|,
name|initTime
operator|+
name|win
argument_list|,
name|req
argument_list|,
name|res
argument_list|,
name|minAlloc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"should not have accepted this"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlanningQuotaException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

