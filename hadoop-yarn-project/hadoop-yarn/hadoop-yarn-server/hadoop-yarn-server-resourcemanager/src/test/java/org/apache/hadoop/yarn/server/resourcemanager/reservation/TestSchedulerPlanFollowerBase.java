begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Mockito
operator|.
name|when
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
name|security
operator|.
name|AccessControlException
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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptState
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
name|Queue
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
name|ResourceScheduler
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
name|event
operator|.
name|AppAddedSchedulerEvent
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
name|event
operator|.
name|AppAttemptAddedSchedulerEvent
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
name|event
operator|.
name|AppAttemptRemovedSchedulerEvent
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
name|Assert
import|;
end_import

begin_class
DECL|class|TestSchedulerPlanFollowerBase
specifier|public
specifier|abstract
class|class
name|TestSchedulerPlanFollowerBase
block|{
DECL|field|GB
specifier|final
specifier|static
name|int
name|GB
init|=
literal|1024
decl_stmt|;
DECL|field|mClock
specifier|protected
name|Clock
name|mClock
init|=
literal|null
decl_stmt|;
DECL|field|scheduler
specifier|protected
name|ResourceScheduler
name|scheduler
init|=
literal|null
decl_stmt|;
DECL|field|mAgent
specifier|protected
name|ReservationAgent
name|mAgent
decl_stmt|;
DECL|field|minAlloc
specifier|protected
name|Resource
name|minAlloc
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|GB
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|maxAlloc
specifier|protected
name|Resource
name|maxAlloc
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|GB
operator|*
literal|8
argument_list|,
literal|8
argument_list|)
decl_stmt|;
DECL|field|policy
specifier|protected
name|CapacityOverTimePolicy
name|policy
init|=
operator|new
name|CapacityOverTimePolicy
argument_list|()
decl_stmt|;
DECL|field|plan
specifier|protected
name|Plan
name|plan
decl_stmt|;
DECL|field|res
specifier|private
name|ResourceCalculator
name|res
init|=
operator|new
name|DefaultResourceCalculator
argument_list|()
decl_stmt|;
DECL|field|context
specifier|private
name|RMContext
name|context
init|=
name|ReservationSystemTestUtil
operator|.
name|createMockRMContext
argument_list|()
decl_stmt|;
DECL|method|testPlanFollower (boolean isMove)
specifier|protected
name|void
name|testPlanFollower
parameter_list|(
name|boolean
name|isMove
parameter_list|)
throws|throws
name|PlanningException
throws|,
name|InterruptedException
throws|,
name|AccessControlException
block|{
comment|// Initialize plan based on move flag
name|plan
operator|=
operator|new
name|InMemoryPlan
argument_list|(
name|scheduler
operator|.
name|getRootQueueMetrics
argument_list|()
argument_list|,
name|policy
argument_list|,
name|mAgent
argument_list|,
name|scheduler
operator|.
name|getClusterResource
argument_list|()
argument_list|,
literal|1L
argument_list|,
name|res
argument_list|,
name|scheduler
operator|.
name|getMinimumResourceCapability
argument_list|()
argument_list|,
name|maxAlloc
argument_list|,
literal|"dedicated"
argument_list|,
literal|null
argument_list|,
name|isMove
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|// add a few reservations to the plan
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
name|f1
init|=
block|{
literal|10
block|,
literal|10
block|,
literal|10
block|,
literal|10
block|,
literal|10
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
name|f1
operator|.
name|length
operator|+
literal|1
argument_list|,
name|f1
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
name|f1
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
literal|0L
argument_list|,
literal|1L
argument_list|,
name|f1
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
literal|"u3"
argument_list|,
literal|"dedicated"
argument_list|,
literal|3
argument_list|,
literal|3
operator|+
name|f1
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
literal|3L
argument_list|,
literal|1L
argument_list|,
name|f1
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
name|int
index|[]
name|f2
init|=
block|{
literal|0
block|,
literal|10
block|,
literal|20
block|,
literal|10
block|,
literal|0
block|}
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
literal|"u4"
argument_list|,
literal|"dedicated"
argument_list|,
literal|10
argument_list|,
literal|10
operator|+
name|f2
operator|.
name|length
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
literal|10L
argument_list|,
literal|1L
argument_list|,
name|f2
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
comment|// default reseration queue should exist before run of PlanFollower AND have
comment|// no apps
name|checkDefaultQueueBeforePlanFollowerRun
argument_list|()
expr_stmt|;
name|AbstractSchedulerPlanFollower
name|planFollower
init|=
name|createPlanFollower
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|mClock
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
name|planFollower
operator|.
name|run
argument_list|()
expr_stmt|;
name|Queue
name|q
init|=
name|getReservationQueue
argument_list|(
name|r1
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertReservationQueueExists
argument_list|(
name|r1
argument_list|)
expr_stmt|;
comment|// submit an app to r1
name|String
name|user_0
init|=
literal|"test-user"
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId_0
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|AppAddedSchedulerEvent
name|addAppEvent
init|=
operator|new
name|AppAddedSchedulerEvent
argument_list|(
name|appId
argument_list|,
name|q
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|user_0
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|handle
argument_list|(
name|addAppEvent
argument_list|)
expr_stmt|;
name|AppAttemptAddedSchedulerEvent
name|appAttemptAddedEvent
init|=
operator|new
name|AppAttemptAddedSchedulerEvent
argument_list|(
name|appAttemptId_0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|handle
argument_list|(
name|appAttemptAddedEvent
argument_list|)
expr_stmt|;
comment|// initial default reservation queue should have no apps after first run
name|Queue
name|defQ
init|=
name|getDefaultQueue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getNumberOfApplications
argument_list|(
name|defQ
argument_list|)
argument_list|)
expr_stmt|;
name|assertReservationQueueExists
argument_list|(
name|r1
argument_list|,
literal|0.1
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNumberOfApplications
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
name|assertReservationQueueDoesNotExist
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|assertReservationQueueDoesNotExist
argument_list|(
name|r3
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mClock
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
name|planFollower
operator|.
name|run
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getNumberOfApplications
argument_list|(
name|defQ
argument_list|)
argument_list|)
expr_stmt|;
name|assertReservationQueueExists
argument_list|(
name|r1
argument_list|,
literal|0.1
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNumberOfApplications
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
name|assertReservationQueueExists
argument_list|(
name|r2
argument_list|,
literal|0.1
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|assertReservationQueueDoesNotExist
argument_list|(
name|r3
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mClock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|10L
argument_list|)
expr_stmt|;
name|planFollower
operator|.
name|run
argument_list|()
expr_stmt|;
name|q
operator|=
name|getReservationQueue
argument_list|(
name|r1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isMove
condition|)
block|{
comment|// app should have been moved to default reservation queue
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNumberOfApplications
argument_list|(
name|defQ
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// app should be killed
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getNumberOfApplications
argument_list|(
name|defQ
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|AppAttemptRemovedSchedulerEvent
name|appAttemptRemovedEvent
init|=
operator|new
name|AppAttemptRemovedSchedulerEvent
argument_list|(
name|appAttemptId_0
argument_list|,
name|RMAppAttemptState
operator|.
name|KILLED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|handle
argument_list|(
name|appAttemptRemovedEvent
argument_list|)
expr_stmt|;
block|}
name|assertReservationQueueDoesNotExist
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|assertReservationQueueExists
argument_list|(
name|r3
argument_list|,
literal|0
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mClock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|11L
argument_list|)
expr_stmt|;
name|planFollower
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
name|isMove
condition|)
block|{
comment|// app should have been moved to default reservation queue
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNumberOfApplications
argument_list|(
name|defQ
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// app should be killed
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getNumberOfApplications
argument_list|(
name|defQ
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertReservationQueueDoesNotExist
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|assertReservationQueueDoesNotExist
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|assertReservationQueueExists
argument_list|(
name|r3
argument_list|,
literal|0.1
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mClock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|12L
argument_list|)
expr_stmt|;
name|planFollower
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertReservationQueueDoesNotExist
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|assertReservationQueueDoesNotExist
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|assertReservationQueueExists
argument_list|(
name|r3
argument_list|,
literal|0.2
argument_list|,
literal|0.2
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mClock
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|16L
argument_list|)
expr_stmt|;
name|planFollower
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertReservationQueueDoesNotExist
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|assertReservationQueueDoesNotExist
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|assertReservationQueueDoesNotExist
argument_list|(
name|r3
argument_list|)
expr_stmt|;
name|verifyCapacity
argument_list|(
name|defQ
argument_list|)
expr_stmt|;
block|}
DECL|method|checkDefaultQueueBeforePlanFollowerRun ()
specifier|protected
specifier|abstract
name|void
name|checkDefaultQueueBeforePlanFollowerRun
parameter_list|()
function_decl|;
DECL|method|getReservationQueue (String reservationId)
specifier|protected
specifier|abstract
name|Queue
name|getReservationQueue
parameter_list|(
name|String
name|reservationId
parameter_list|)
function_decl|;
DECL|method|verifyCapacity (Queue defQ)
specifier|protected
specifier|abstract
name|void
name|verifyCapacity
parameter_list|(
name|Queue
name|defQ
parameter_list|)
function_decl|;
DECL|method|getDefaultQueue ()
specifier|protected
specifier|abstract
name|Queue
name|getDefaultQueue
parameter_list|()
function_decl|;
DECL|method|getNumberOfApplications (Queue queue)
specifier|protected
specifier|abstract
name|int
name|getNumberOfApplications
parameter_list|(
name|Queue
name|queue
parameter_list|)
function_decl|;
DECL|method|createPlanFollower ()
specifier|protected
specifier|abstract
name|AbstractSchedulerPlanFollower
name|createPlanFollower
parameter_list|()
function_decl|;
DECL|method|assertReservationQueueExists (ReservationId r)
specifier|protected
specifier|abstract
name|void
name|assertReservationQueueExists
parameter_list|(
name|ReservationId
name|r
parameter_list|)
function_decl|;
DECL|method|assertReservationQueueExists (ReservationId r2, double expectedCapacity, double expectedMaxCapacity)
specifier|protected
specifier|abstract
name|void
name|assertReservationQueueExists
parameter_list|(
name|ReservationId
name|r2
parameter_list|,
name|double
name|expectedCapacity
parameter_list|,
name|double
name|expectedMaxCapacity
parameter_list|)
function_decl|;
DECL|method|assertReservationQueueDoesNotExist (ReservationId r2)
specifier|protected
specifier|abstract
name|void
name|assertReservationQueueDoesNotExist
parameter_list|(
name|ReservationId
name|r2
parameter_list|)
function_decl|;
block|}
end_class

end_unit

