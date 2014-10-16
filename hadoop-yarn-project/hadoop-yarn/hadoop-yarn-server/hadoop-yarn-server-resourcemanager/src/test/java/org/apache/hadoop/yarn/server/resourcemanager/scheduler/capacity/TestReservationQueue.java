begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|capacity
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
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|io
operator|.
name|IOException
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
name|scheduler
operator|.
name|SchedulerDynamicEditException
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
name|QueueEntitlement
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

begin_class
DECL|class|TestReservationQueue
specifier|public
class|class
name|TestReservationQueue
block|{
DECL|field|csConf
name|CapacitySchedulerConfiguration
name|csConf
decl_stmt|;
DECL|field|csContext
name|CapacitySchedulerContext
name|csContext
decl_stmt|;
DECL|field|GB
specifier|final
specifier|static
name|int
name|GB
init|=
literal|1024
decl_stmt|;
DECL|field|resourceCalculator
specifier|private
specifier|final
name|ResourceCalculator
name|resourceCalculator
init|=
operator|new
name|DefaultResourceCalculator
argument_list|()
decl_stmt|;
DECL|field|reservationQueue
name|ReservationQueue
name|reservationQueue
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
comment|// setup a context / conf
name|csConf
operator|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
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
name|conf
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
name|Resources
operator|.
name|createResource
argument_list|(
name|GB
argument_list|,
literal|1
argument_list|)
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
name|Resources
operator|.
name|createResource
argument_list|(
literal|16
operator|*
name|GB
argument_list|,
literal|32
argument_list|)
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
name|csContext
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|resourceCalculator
argument_list|)
expr_stmt|;
name|RMContext
name|mockRMContext
init|=
name|TestUtils
operator|.
name|getMockRMContext
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getRMContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockRMContext
argument_list|)
expr_stmt|;
comment|// create a queue
name|PlanQueue
name|pq
init|=
operator|new
name|PlanQueue
argument_list|(
name|csContext
argument_list|,
literal|"root"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|reservationQueue
operator|=
operator|new
name|ReservationQueue
argument_list|(
name|csContext
argument_list|,
literal|"a"
argument_list|,
name|pq
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddSubtractCapacity ()
specifier|public
name|void
name|testAddSubtractCapacity
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verify that setting, adding, subtracting capacity works
name|reservationQueue
operator|.
name|setCapacity
argument_list|(
literal|1.0F
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|" actual capacity: "
operator|+
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
operator|-
literal|1
operator|<
name|CSQueueUtils
operator|.
name|EPSILON
argument_list|)
expr_stmt|;
name|reservationQueue
operator|.
name|setEntitlement
argument_list|(
operator|new
name|QueueEntitlement
argument_list|(
literal|0.9f
argument_list|,
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|" actual capacity: "
operator|+
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
operator|-
literal|0.9
operator|<
name|CSQueueUtils
operator|.
name|EPSILON
argument_list|)
expr_stmt|;
name|reservationQueue
operator|.
name|setEntitlement
argument_list|(
operator|new
name|QueueEntitlement
argument_list|(
literal|1f
argument_list|,
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|" actual capacity: "
operator|+
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
operator|-
literal|1
operator|<
name|CSQueueUtils
operator|.
name|EPSILON
argument_list|)
expr_stmt|;
name|reservationQueue
operator|.
name|setEntitlement
argument_list|(
operator|new
name|QueueEntitlement
argument_list|(
literal|0f
argument_list|,
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|" actual capacity: "
operator|+
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
operator|<
name|CSQueueUtils
operator|.
name|EPSILON
argument_list|)
expr_stmt|;
try|try
block|{
name|reservationQueue
operator|.
name|setEntitlement
argument_list|(
operator|new
name|QueueEntitlement
argument_list|(
literal|1.1f
argument_list|,
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerDynamicEditException
name|iae
parameter_list|)
block|{
comment|// expected
name|assertTrue
argument_list|(
literal|" actual capacity: "
operator|+
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
operator|-
literal|1
operator|<
name|CSQueueUtils
operator|.
name|EPSILON
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|reservationQueue
operator|.
name|setEntitlement
argument_list|(
operator|new
name|QueueEntitlement
argument_list|(
operator|-
literal|0.1f
argument_list|,
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerDynamicEditException
name|iae
parameter_list|)
block|{
comment|// expected
name|assertTrue
argument_list|(
literal|" actual capacity: "
operator|+
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|reservationQueue
operator|.
name|getCapacity
argument_list|()
operator|-
literal|1
operator|<
name|CSQueueUtils
operator|.
name|EPSILON
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

