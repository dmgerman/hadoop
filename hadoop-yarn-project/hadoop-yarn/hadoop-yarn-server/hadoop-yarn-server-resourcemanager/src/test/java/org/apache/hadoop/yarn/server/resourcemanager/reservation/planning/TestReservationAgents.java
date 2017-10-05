begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*****************************************************************************  *   Licensed to the Apache Software Foundation (ASF) under one  *   or more contributor license agreements.  See the NOTICE file  *   distributed with this work for additional information  *   regarding copyright ownership.  The ASF licenses this file  *   to you under the Apache License, Version 2.0 (the  *   "License"); you may not use this file except in compliance  *   with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *   Unless required by applicable law or agreed to in writing, software  *   distributed under the License is distributed on an "AS IS" BASIS,  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *   See the License for the specific language governing permissions and  *   limitations under the License.  *****************************************************************************/
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
name|ReservationRequestInterpreter
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
name|ReservationRequests
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
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|ReservationDefinitionPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ReservationRequestsPBImpl
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
name|CapacityOverTimePolicy
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
name|Plan
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
name|RLESparseResourceAllocation
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|NavigableMap
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

begin_comment
comment|/**  * General purpose ReservationAgent tester.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
annotation|@
name|SuppressWarnings
argument_list|(
literal|"VisibilityModifier"
argument_list|)
DECL|class|TestReservationAgents
specifier|public
class|class
name|TestReservationAgents
block|{
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
name|value
operator|=
literal|0
argument_list|)
DECL|field|agentClass
specifier|public
name|Class
name|agentClass
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
name|value
operator|=
literal|1
argument_list|)
DECL|field|allocateLeft
specifier|public
name|boolean
name|allocateLeft
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
name|value
operator|=
literal|2
argument_list|)
DECL|field|recurrenceExpression
specifier|public
name|String
name|recurrenceExpression
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
name|value
operator|=
literal|3
argument_list|)
DECL|field|numOfNodes
specifier|public
name|int
name|numOfNodes
decl_stmt|;
DECL|field|step
specifier|private
name|long
name|step
decl_stmt|;
DECL|field|rand
specifier|private
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|agent
specifier|private
name|ReservationAgent
name|agent
decl_stmt|;
DECL|field|plan
specifier|private
name|Plan
name|plan
decl_stmt|;
DECL|field|resCalc
specifier|private
name|ResourceCalculator
name|resCalc
init|=
operator|new
name|DefaultResourceCalculator
argument_list|()
decl_stmt|;
DECL|field|minAlloc
specifier|private
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
DECL|field|maxAlloc
specifier|private
name|Resource
name|maxAlloc
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|32
operator|*
literal|1023
argument_list|,
literal|32
argument_list|)
decl_stmt|;
DECL|field|timeHorizon
specifier|private
name|long
name|timeHorizon
init|=
literal|2
operator|*
literal|24
operator|*
literal|3600
operator|*
literal|1000
decl_stmt|;
comment|// 2 days
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestReservationAgents
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"Testing: agent {0}, allocateLeft: {1},"
operator|+
literal|" recurrenceExpression: {2}, numNodes: {3})"
argument_list|)
DECL|method|data ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|GreedyReservationAgent
operator|.
name|class
block|,
literal|true
block|,
literal|"0"
block|,
literal|100
block|}
block|,
block|{
name|GreedyReservationAgent
operator|.
name|class
block|,
literal|false
block|,
literal|"0"
block|,
literal|100
block|}
block|,
block|{
name|GreedyReservationAgent
operator|.
name|class
block|,
literal|true
block|,
literal|"7200000"
block|,
literal|100
block|}
block|,
block|{
name|GreedyReservationAgent
operator|.
name|class
block|,
literal|false
block|,
literal|"7200000"
block|,
literal|100
block|}
block|,
block|{
name|GreedyReservationAgent
operator|.
name|class
block|,
literal|true
block|,
literal|"86400000"
block|,
literal|100
block|}
block|,
block|{
name|GreedyReservationAgent
operator|.
name|class
block|,
literal|false
block|,
literal|"86400000"
block|,
literal|100
block|}
block|,
block|{
name|AlignedPlannerWithGreedy
operator|.
name|class
block|,
literal|true
block|,
literal|"0"
block|,
literal|100
block|}
block|,
block|{
name|AlignedPlannerWithGreedy
operator|.
name|class
block|,
literal|false
block|,
literal|"0"
block|,
literal|100
block|}
block|,
block|{
name|AlignedPlannerWithGreedy
operator|.
name|class
block|,
literal|true
block|,
literal|"7200000"
block|,
literal|100
block|}
block|,
block|{
name|AlignedPlannerWithGreedy
operator|.
name|class
block|,
literal|false
block|,
literal|"7200000"
block|,
literal|100
block|}
block|,
block|{
name|AlignedPlannerWithGreedy
operator|.
name|class
block|,
literal|true
block|,
literal|"86400000"
block|,
literal|100
block|}
block|,
block|{
name|AlignedPlannerWithGreedy
operator|.
name|class
block|,
literal|false
block|,
literal|"86400000"
block|,
literal|100
block|}
block|}
argument_list|)
return|;
block|}
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
name|long
name|seed
init|=
name|rand
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|rand
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running with seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
comment|// setting completely loose quotas
name|long
name|timeWindow
init|=
literal|1000000L
decl_stmt|;
name|Resource
name|clusterCapacity
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|numOfNodes
operator|*
literal|1024
argument_list|,
name|numOfNodes
argument_list|)
decl_stmt|;
name|step
operator|=
literal|1000L
expr_stmt|;
name|String
name|reservationQ
init|=
name|ReservationSystemTestUtil
operator|.
name|getFullReservationQueueName
argument_list|()
decl_stmt|;
name|float
name|instConstraint
init|=
literal|100
decl_stmt|;
name|float
name|avgConstraint
init|=
literal|100
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
comment|// setting conf to
name|conf
operator|.
name|setBoolean
argument_list|(
name|GreedyReservationAgent
operator|.
name|FAVOR_EARLY_ALLOCATION
argument_list|,
name|allocateLeft
argument_list|)
expr_stmt|;
name|agent
operator|=
operator|(
name|ReservationAgent
operator|)
name|agentClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|agent
operator|.
name|init
argument_list|(
name|conf
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
name|RMContext
name|context
init|=
name|ReservationSystemTestUtil
operator|.
name|createMockRMContext
argument_list|()
decl_stmt|;
name|plan
operator|=
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
name|resCalc
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
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|period
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|recurrenceExpression
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|ReservationDefinition
name|rr
init|=
name|createRandomRequest
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|rr
operator|!=
literal|null
condition|)
block|{
name|ReservationId
name|reservationID
init|=
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
decl_stmt|;
try|try
block|{
name|agent
operator|.
name|createReservation
argument_list|(
name|reservationID
argument_list|,
literal|"u1"
argument_list|,
name|plan
argument_list|,
name|rr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlanningException
name|p
parameter_list|)
block|{
comment|// happens
block|}
block|}
block|}
block|}
DECL|method|createRandomRequest (int i)
specifier|private
name|ReservationDefinition
name|createRandomRequest
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|PlanningException
block|{
name|long
name|arrival
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|rand
operator|.
name|nextDouble
argument_list|()
operator|*
name|timeHorizon
argument_list|)
decl_stmt|;
name|long
name|period
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|recurrenceExpression
argument_list|)
decl_stmt|;
comment|// min between period and rand around 30min
name|long
name|duration
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|round
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|rand
operator|.
name|nextDouble
argument_list|()
operator|*
literal|3600
operator|*
literal|1000
argument_list|,
name|period
argument_list|)
argument_list|)
decl_stmt|;
comment|// min between period and rand around 5x duration
name|long
name|deadline
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|arrival
operator|+
name|Math
operator|.
name|min
argument_list|(
name|duration
operator|*
name|rand
operator|.
name|nextDouble
argument_list|()
operator|*
literal|10
argument_list|,
name|period
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
operator|(
operator|(
name|deadline
operator|-
name|arrival
operator|)
operator|<=
name|period
operator|)
assert|;
name|RLESparseResourceAllocation
name|available
init|=
name|plan
operator|.
name|getAvailableResourceOverTime
argument_list|(
literal|"u1"
argument_list|,
literal|null
argument_list|,
name|arrival
argument_list|,
name|deadline
argument_list|,
name|period
argument_list|)
decl_stmt|;
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|availableMap
init|=
name|available
operator|.
name|getCumulative
argument_list|()
decl_stmt|;
comment|// look at available space, and for each segment, use half of it with 50%
comment|// probability
name|List
argument_list|<
name|ReservationRequest
argument_list|>
name|reservationRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|e
range|:
name|availableMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
operator|&&
name|rand
operator|.
name|nextDouble
argument_list|()
operator|>
literal|0.001
condition|)
block|{
name|int
name|numContainers
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|Resources
operator|.
name|divide
argument_list|(
name|resCalc
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|minAlloc
argument_list|)
operator|/
literal|2
argument_list|)
decl_stmt|;
name|long
name|tempDur
init|=
name|Math
operator|.
name|min
argument_list|(
name|duration
argument_list|,
name|availableMap
operator|.
name|higherKey
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
operator|-
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|reservationRequests
operator|.
name|add
argument_list|(
name|ReservationRequest
operator|.
name|newInstance
argument_list|(
name|minAlloc
argument_list|,
name|numContainers
argument_list|,
literal|1
argument_list|,
name|tempDur
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|reservationRequests
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ReservationDefinition
name|rr
init|=
operator|new
name|ReservationDefinitionPBImpl
argument_list|()
decl_stmt|;
name|rr
operator|.
name|setArrival
argument_list|(
name|arrival
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setDeadline
argument_list|(
name|deadline
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setRecurrenceExpression
argument_list|(
name|recurrenceExpression
argument_list|)
expr_stmt|;
name|ReservationRequests
name|reqs
init|=
operator|new
name|ReservationRequestsPBImpl
argument_list|()
decl_stmt|;
name|reqs
operator|.
name|setInterpreter
argument_list|(
name|ReservationRequestInterpreter
operator|.
name|R_ORDER
argument_list|)
expr_stmt|;
name|reqs
operator|.
name|setReservationResources
argument_list|(
name|reservationRequests
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setReservationRequests
argument_list|(
name|reqs
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setReservationName
argument_list|(
literal|"res_"
operator|+
name|i
argument_list|)
expr_stmt|;
return|return
name|rr
return|;
block|}
block|}
end_class

end_unit

