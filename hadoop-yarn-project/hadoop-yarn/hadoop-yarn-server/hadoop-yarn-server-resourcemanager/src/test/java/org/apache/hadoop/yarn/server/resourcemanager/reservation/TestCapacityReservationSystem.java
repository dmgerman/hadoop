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

begin_class
DECL|class|TestCapacityReservationSystem
specifier|public
class|class
name|TestCapacityReservationSystem
block|{
annotation|@
name|Test
DECL|method|testInitialize ()
specifier|public
name|void
name|testInitialize
parameter_list|()
block|{
name|ReservationSystemTestUtil
name|testUtil
init|=
operator|new
name|ReservationSystemTestUtil
argument_list|()
decl_stmt|;
name|CapacityScheduler
name|capScheduler
init|=
literal|null
decl_stmt|;
try|try
block|{
name|capScheduler
operator|=
name|testUtil
operator|.
name|mockCapacityScheduler
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CapacityReservationSystem
name|reservationSystem
init|=
operator|new
name|CapacityReservationSystem
argument_list|()
decl_stmt|;
name|reservationSystem
operator|.
name|setRMContext
argument_list|(
name|capScheduler
operator|.
name|getRMContext
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|reservationSystem
operator|.
name|reinitialize
argument_list|(
name|capScheduler
operator|.
name|getConf
argument_list|()
argument_list|,
name|capScheduler
operator|.
name|getRMContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|planQName
init|=
name|testUtil
operator|.
name|getreservationQueueName
argument_list|()
decl_stmt|;
name|Plan
name|plan
init|=
name|reservationSystem
operator|.
name|getPlan
argument_list|(
name|planQName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|plan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|plan
operator|instanceof
name|InMemoryPlan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|planQName
argument_list|,
name|plan
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8192
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|plan
operator|.
name|getReservationAgent
argument_list|()
operator|instanceof
name|GreedyReservationAgent
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|plan
operator|.
name|getSharingPolicy
argument_list|()
operator|instanceof
name|CapacityOverTimePolicy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReinitialize ()
specifier|public
name|void
name|testReinitialize
parameter_list|()
block|{
name|ReservationSystemTestUtil
name|testUtil
init|=
operator|new
name|ReservationSystemTestUtil
argument_list|()
decl_stmt|;
name|CapacityScheduler
name|capScheduler
init|=
literal|null
decl_stmt|;
try|try
block|{
name|capScheduler
operator|=
name|testUtil
operator|.
name|mockCapacityScheduler
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CapacityReservationSystem
name|reservationSystem
init|=
operator|new
name|CapacityReservationSystem
argument_list|()
decl_stmt|;
name|CapacitySchedulerConfiguration
name|conf
init|=
name|capScheduler
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|RMContext
name|mockContext
init|=
name|capScheduler
operator|.
name|getRMContext
argument_list|()
decl_stmt|;
name|reservationSystem
operator|.
name|setRMContext
argument_list|(
name|mockContext
argument_list|)
expr_stmt|;
try|try
block|{
name|reservationSystem
operator|.
name|reinitialize
argument_list|(
name|capScheduler
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|mockContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Assert queue in original config
name|String
name|planQName
init|=
name|testUtil
operator|.
name|getreservationQueueName
argument_list|()
decl_stmt|;
name|Plan
name|plan
init|=
name|reservationSystem
operator|.
name|getPlan
argument_list|(
name|planQName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|plan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|plan
operator|instanceof
name|InMemoryPlan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|planQName
argument_list|,
name|plan
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8192
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|plan
operator|.
name|getReservationAgent
argument_list|()
operator|instanceof
name|GreedyReservationAgent
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|plan
operator|.
name|getSharingPolicy
argument_list|()
operator|instanceof
name|CapacityOverTimePolicy
argument_list|)
expr_stmt|;
comment|// Dynamically add a plan
name|String
name|newQ
init|=
literal|"reservation"
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|reservationSystem
operator|.
name|getPlan
argument_list|(
name|newQ
argument_list|)
argument_list|)
expr_stmt|;
name|testUtil
operator|.
name|updateQueueConfiguration
argument_list|(
name|conf
argument_list|,
name|newQ
argument_list|)
expr_stmt|;
try|try
block|{
name|capScheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|mockContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|reservationSystem
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|mockContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Plan
name|newPlan
init|=
name|reservationSystem
operator|.
name|getPlan
argument_list|(
name|newQ
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|newPlan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newPlan
operator|instanceof
name|InMemoryPlan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|newQ
argument_list|,
name|newPlan
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|newPlan
operator|.
name|getTotalCapacity
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newPlan
operator|.
name|getReservationAgent
argument_list|()
operator|instanceof
name|GreedyReservationAgent
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newPlan
operator|.
name|getSharingPolicy
argument_list|()
operator|instanceof
name|CapacityOverTimePolicy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

