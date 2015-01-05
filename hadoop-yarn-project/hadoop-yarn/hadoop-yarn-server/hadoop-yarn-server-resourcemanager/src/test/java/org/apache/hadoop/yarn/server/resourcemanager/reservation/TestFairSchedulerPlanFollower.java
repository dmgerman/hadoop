begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|spy
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|rmapp
operator|.
name|RMApp
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
name|capacity
operator|.
name|TestUtils
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
name|AllocationConfiguration
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
name|FSLeafQueue
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
name|FSQueue
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
name|FairScheduler
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
name|FairSchedulerConfiguration
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
name|FairSchedulerTestBase
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
name|security
operator|.
name|ApplicationACLsManager
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
name|junit
operator|.
name|After
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
name|Rule
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
name|rules
operator|.
name|TestName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Matchers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestFairSchedulerPlanFollower
specifier|public
class|class
name|TestFairSchedulerPlanFollower
extends|extends
name|TestSchedulerPlanFollowerBase
block|{
DECL|field|ALLOC_FILE
specifier|private
specifier|final
specifier|static
name|String
name|ALLOC_FILE
init|=
operator|new
name|File
argument_list|(
name|FairSchedulerTestBase
operator|.
name|TEST_DIR
argument_list|,
name|TestFairReservationSystem
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".xml"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|spyRMContext
specifier|private
name|RMContext
name|spyRMContext
decl_stmt|;
DECL|field|fs
specifier|private
name|FairScheduler
name|fs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|testHelper
specifier|private
name|FairSchedulerTestBase
name|testHelper
init|=
operator|new
name|FairSchedulerTestBase
argument_list|()
decl_stmt|;
annotation|@
name|Rule
DECL|field|name
specifier|public
name|TestName
name|name
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|testHelper
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FairScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|ALLOCATION_FILE
argument_list|,
name|ALLOC_FILE
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
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
name|conf
operator|=
name|createConfiguration
argument_list|()
expr_stmt|;
name|ReservationSystemTestUtil
operator|.
name|setupFSAllocationFile
argument_list|(
name|ALLOC_FILE
argument_list|)
expr_stmt|;
name|ReservationSystemTestUtil
name|testUtil
init|=
operator|new
name|ReservationSystemTestUtil
argument_list|()
decl_stmt|;
comment|// Setup
name|rmContext
operator|=
name|TestUtils
operator|.
name|getMockRMContext
argument_list|()
expr_stmt|;
name|spyRMContext
operator|=
name|spy
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
name|fs
operator|=
name|ReservationSystemTestUtil
operator|.
name|setupFairScheduler
argument_list|(
name|testUtil
argument_list|,
name|spyRMContext
argument_list|,
name|conf
argument_list|,
literal|125
argument_list|)
expr_stmt|;
name|scheduler
operator|=
name|fs
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|spyApps
init|=
name|spy
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|RMApp
name|rmApp
init|=
name|mock
argument_list|(
name|RMApp
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rmApp
operator|.
name|getRMAppAttempt
argument_list|(
operator|(
name|ApplicationAttemptId
operator|)
name|Matchers
operator|.
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|rmApp
argument_list|)
operator|.
name|when
argument_list|(
name|spyApps
argument_list|)
operator|.
name|get
argument_list|(
operator|(
name|ApplicationId
operator|)
name|Matchers
operator|.
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|spyRMContext
operator|.
name|getRMApps
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|spyApps
argument_list|)
expr_stmt|;
name|ReservationSystemTestUtil
operator|.
name|setupFSAllocationFile
argument_list|(
name|ALLOC_FILE
argument_list|)
expr_stmt|;
name|setupPlanFollower
argument_list|()
expr_stmt|;
block|}
DECL|method|setupPlanFollower ()
specifier|private
name|void
name|setupPlanFollower
parameter_list|()
throws|throws
name|Exception
block|{
name|ReservationSystemTestUtil
name|testUtil
init|=
operator|new
name|ReservationSystemTestUtil
argument_list|()
decl_stmt|;
name|mClock
operator|=
name|mock
argument_list|(
name|Clock
operator|.
name|class
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
name|String
name|reservationQ
init|=
name|testUtil
operator|.
name|getFullReservationQueueName
argument_list|()
decl_stmt|;
name|AllocationConfiguration
name|allocConf
init|=
name|fs
operator|.
name|getAllocationConfiguration
argument_list|()
decl_stmt|;
name|allocConf
operator|.
name|setReservationWindow
argument_list|(
literal|20L
argument_list|)
expr_stmt|;
name|allocConf
operator|.
name|setAverageCapacity
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|policy
operator|.
name|init
argument_list|(
name|reservationQ
argument_list|,
name|allocConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithMoveOnExpiry ()
specifier|public
name|void
name|testWithMoveOnExpiry
parameter_list|()
throws|throws
name|PlanningException
throws|,
name|InterruptedException
throws|,
name|AccessControlException
block|{
comment|// invoke plan follower test with move
name|testPlanFollower
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithKillOnExpiry ()
specifier|public
name|void
name|testWithKillOnExpiry
parameter_list|()
throws|throws
name|PlanningException
throws|,
name|InterruptedException
throws|,
name|AccessControlException
block|{
comment|// invoke plan follower test with kill
name|testPlanFollower
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|verifyCapacity (Queue defQ)
specifier|protected
name|void
name|verifyCapacity
parameter_list|(
name|Queue
name|defQ
parameter_list|)
block|{
name|assertTrue
argument_list|(
operator|(
operator|(
name|FSQueue
operator|)
name|defQ
operator|)
operator|.
name|getWeights
argument_list|()
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|MEMORY
argument_list|)
operator|>
literal|0.9
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDefaultQueue ()
specifier|protected
name|Queue
name|getDefaultQueue
parameter_list|()
block|{
return|return
name|getReservationQueue
argument_list|(
literal|"dedicated"
operator|+
name|ReservationConstants
operator|.
name|DEFAULT_QUEUE_SUFFIX
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfApplications (Queue queue)
specifier|protected
name|int
name|getNumberOfApplications
parameter_list|(
name|Queue
name|queue
parameter_list|)
block|{
name|int
name|numberOfApplications
init|=
name|fs
operator|.
name|getAppsInQueue
argument_list|(
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
name|numberOfApplications
return|;
block|}
annotation|@
name|Override
DECL|method|createPlanFollower ()
specifier|protected
name|AbstractSchedulerPlanFollower
name|createPlanFollower
parameter_list|()
block|{
name|FairSchedulerPlanFollower
name|planFollower
init|=
operator|new
name|FairSchedulerPlanFollower
argument_list|()
decl_stmt|;
name|planFollower
operator|.
name|init
argument_list|(
name|mClock
argument_list|,
name|scheduler
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|plan
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|planFollower
return|;
block|}
annotation|@
name|Override
DECL|method|assertReservationQueueExists (ReservationId r)
specifier|protected
name|void
name|assertReservationQueueExists
parameter_list|(
name|ReservationId
name|r
parameter_list|)
block|{
name|Queue
name|q
init|=
name|getReservationQueue
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|assertReservationQueueExists (ReservationId r, double expectedCapacity, double expectedMaxCapacity)
specifier|protected
name|void
name|assertReservationQueueExists
parameter_list|(
name|ReservationId
name|r
parameter_list|,
name|double
name|expectedCapacity
parameter_list|,
name|double
name|expectedMaxCapacity
parameter_list|)
block|{
name|FSLeafQueue
name|q
init|=
name|fs
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
name|plan
operator|.
name|getQueueName
argument_list|()
operator|+
literal|""
operator|+
literal|"."
operator|+
name|r
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
comment|// For now we are setting both to same weight
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedCapacity
argument_list|,
name|q
operator|.
name|getWeights
argument_list|()
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|MEMORY
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|assertReservationQueueDoesNotExist (ReservationId r)
specifier|protected
name|void
name|assertReservationQueueDoesNotExist
parameter_list|(
name|ReservationId
name|r
parameter_list|)
block|{
name|Queue
name|q
init|=
name|getReservationQueue
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReservationQueue (String r)
specifier|protected
name|Queue
name|getReservationQueue
parameter_list|(
name|String
name|r
parameter_list|)
block|{
return|return
name|fs
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
name|plan
operator|.
name|getQueueName
argument_list|()
operator|+
literal|""
operator|+
literal|"."
operator|+
name|r
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|mockAppACLsManager ()
specifier|public
specifier|static
name|ApplicationACLsManager
name|mockAppACLsManager
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
return|return
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
return|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|scheduler
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

