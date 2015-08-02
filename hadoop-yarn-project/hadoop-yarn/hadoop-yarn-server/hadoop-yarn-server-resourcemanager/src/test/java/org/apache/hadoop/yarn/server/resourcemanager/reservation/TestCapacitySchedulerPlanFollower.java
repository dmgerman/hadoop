begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|capacity
operator|.
name|CSQueue
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
name|CapacityScheduler
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
name|capacity
operator|.
name|CapacitySchedulerContext
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
name|security
operator|.
name|RMContainerTokenSecretManager
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
name|Resources
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
DECL|class|TestCapacitySchedulerPlanFollower
specifier|public
class|class
name|TestCapacitySchedulerPlanFollower
extends|extends
name|TestSchedulerPlanFollowerBase
block|{
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
DECL|field|csContext
specifier|private
name|CapacitySchedulerContext
name|csContext
decl_stmt|;
DECL|field|cs
specifier|private
name|CapacityScheduler
name|cs
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
name|CapacityScheduler
name|spyCs
init|=
operator|new
name|CapacityScheduler
argument_list|()
decl_stmt|;
name|cs
operator|=
name|spy
argument_list|(
name|spyCs
argument_list|)
expr_stmt|;
name|scheduler
operator|=
name|cs
expr_stmt|;
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
name|when
argument_list|(
name|spyRMContext
operator|.
name|getScheduler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|ReservationSystemTestUtil
operator|.
name|setupQueueConfiguration
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|csContext
operator|=
name|mock
argument_list|(
name|CapacitySchedulerContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getMinimumResourceCapability
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|minAlloc
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getMaximumResourceCapability
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxAlloc
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getClusterResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|100
operator|*
literal|16
operator|*
name|GB
argument_list|,
literal|100
operator|*
literal|32
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getClusterResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|125
operator|*
name|GB
argument_list|,
literal|125
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|DefaultResourceCalculator
argument_list|()
argument_list|)
expr_stmt|;
name|RMContainerTokenSecretManager
name|containerTokenSecretManager
init|=
operator|new
name|RMContainerTokenSecretManager
argument_list|(
name|csConf
argument_list|)
decl_stmt|;
name|containerTokenSecretManager
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getContainerTokenSecretManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerTokenSecretManager
argument_list|)
expr_stmt|;
name|cs
operator|.
name|setRMContext
argument_list|(
name|spyRMContext
argument_list|)
expr_stmt|;
name|cs
operator|.
name|init
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|cs
operator|.
name|start
argument_list|()
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
name|ReservationSystemTestUtil
operator|.
name|getFullReservationQueueName
argument_list|()
decl_stmt|;
name|CapacitySchedulerConfiguration
name|csConf
init|=
name|cs
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|csConf
operator|.
name|setReservationWindow
argument_list|(
name|reservationQ
argument_list|,
literal|20L
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setMaximumCapacity
argument_list|(
name|reservationQ
argument_list|,
literal|40
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setAverageCapacity
argument_list|(
name|reservationQ
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|policy
operator|.
name|init
argument_list|(
name|reservationQ
argument_list|,
name|csConf
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
name|CSQueue
name|csQueue
init|=
operator|(
name|CSQueue
operator|)
name|defQ
decl_stmt|;
name|assertTrue
argument_list|(
name|csQueue
operator|.
name|getCapacity
argument_list|()
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
name|cs
operator|.
name|getQueue
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
name|CSQueue
name|csQueue
init|=
operator|(
name|CSQueue
operator|)
name|queue
decl_stmt|;
name|int
name|numberOfApplications
init|=
name|csQueue
operator|.
name|getNumApplications
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
name|CapacitySchedulerPlanFollower
name|createPlanFollower
parameter_list|()
block|{
name|CapacitySchedulerPlanFollower
name|planFollower
init|=
operator|new
name|CapacitySchedulerPlanFollower
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
name|CSQueue
name|q
init|=
name|cs
operator|.
name|getQueue
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
DECL|method|assertReservationQueueExists (ReservationId r2, double expectedCapacity, double expectedMaxCapacity)
specifier|protected
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
block|{
name|CSQueue
name|q
init|=
name|cs
operator|.
name|getQueue
argument_list|(
name|r2
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
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedCapacity
argument_list|,
name|q
operator|.
name|getCapacity
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedMaxCapacity
argument_list|,
name|q
operator|.
name|getMaximumCapacity
argument_list|()
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|assertReservationQueueDoesNotExist (ReservationId r2)
specifier|protected
name|void
name|assertReservationQueueDoesNotExist
parameter_list|(
name|ReservationId
name|r2
parameter_list|)
block|{
name|CSQueue
name|q2
init|=
name|cs
operator|.
name|getQueue
argument_list|(
name|r2
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|q2
argument_list|)
expr_stmt|;
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
name|cs
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getReservationQueue (String reservationId)
specifier|protected
name|Queue
name|getReservationQueue
parameter_list|(
name|String
name|reservationId
parameter_list|)
block|{
return|return
name|cs
operator|.
name|getQueue
argument_list|(
name|reservationId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

