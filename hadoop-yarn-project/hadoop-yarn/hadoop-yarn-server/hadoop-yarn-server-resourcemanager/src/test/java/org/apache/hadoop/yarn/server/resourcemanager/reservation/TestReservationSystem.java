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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|ParameterizedSchedulerTestBase
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
name|AbstractYarnScheduler
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
name|Test
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
name|io
operator|.
name|IOException
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|}
argument_list|)
DECL|class|TestReservationSystem
specifier|public
class|class
name|TestReservationSystem
extends|extends
name|ParameterizedSchedulerTestBase
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
name|TestReservationSystem
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
DECL|field|scheduler
specifier|private
name|AbstractYarnScheduler
name|scheduler
decl_stmt|;
DECL|field|reservationSystem
specifier|private
name|AbstractReservationSystem
name|reservationSystem
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|mockRMContext
specifier|private
name|RMContext
name|mockRMContext
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|scheduler
operator|=
name|initializeScheduler
argument_list|()
expr_stmt|;
name|rmContext
operator|=
name|getRMContext
argument_list|()
expr_stmt|;
name|reservationSystem
operator|=
name|configureReservationSystem
argument_list|()
expr_stmt|;
name|reservationSystem
operator|.
name|setRMContext
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|setMiniClusterMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|conf
operator|=
literal|null
expr_stmt|;
name|reservationSystem
operator|=
literal|null
expr_stmt|;
name|rmContext
operator|=
literal|null
expr_stmt|;
name|scheduler
operator|=
literal|null
expr_stmt|;
name|clearRMContext
argument_list|()
expr_stmt|;
name|QueueMetrics
operator|.
name|clearQueueMetrics
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitialize ()
specifier|public
name|void
name|testInitialize
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|reservationSystem
operator|.
name|reinitialize
argument_list|(
name|scheduler
operator|.
name|getConfig
argument_list|()
argument_list|,
name|rmContext
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
if|if
condition|(
name|getSchedulerType
argument_list|()
operator|.
name|equals
argument_list|(
name|SchedulerType
operator|.
name|CAPACITY
argument_list|)
condition|)
block|{
name|ReservationSystemTestUtil
operator|.
name|validateReservationQueue
argument_list|(
name|reservationSystem
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|getReservationQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ReservationSystemTestUtil
operator|.
name|validateReservationQueue
argument_list|(
name|reservationSystem
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|getFullReservationQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReinitialize ()
specifier|public
name|void
name|testReinitialize
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
name|scheduler
operator|.
name|getConfig
argument_list|()
expr_stmt|;
try|try
block|{
name|reservationSystem
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|rmContext
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
if|if
condition|(
name|getSchedulerType
argument_list|()
operator|.
name|equals
argument_list|(
name|SchedulerType
operator|.
name|CAPACITY
argument_list|)
condition|)
block|{
name|ReservationSystemTestUtil
operator|.
name|validateReservationQueue
argument_list|(
name|reservationSystem
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|getReservationQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ReservationSystemTestUtil
operator|.
name|validateReservationQueue
argument_list|(
name|reservationSystem
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|getFullReservationQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|updateSchedulerConf
argument_list|(
name|conf
argument_list|,
name|newQ
argument_list|)
expr_stmt|;
try|try
block|{
name|scheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|rmContext
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
name|rmContext
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
if|if
condition|(
name|getSchedulerType
argument_list|()
operator|.
name|equals
argument_list|(
name|SchedulerType
operator|.
name|CAPACITY
argument_list|)
condition|)
block|{
name|ReservationSystemTestUtil
operator|.
name|validateReservationQueue
argument_list|(
name|reservationSystem
argument_list|,
name|newQ
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ReservationSystemTestUtil
operator|.
name|validateReservationQueue
argument_list|(
name|reservationSystem
argument_list|,
literal|"root."
operator|+
name|newQ
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|initializeScheduler ()
specifier|public
name|AbstractYarnScheduler
name|initializeScheduler
parameter_list|()
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|getSchedulerType
argument_list|()
condition|)
block|{
case|case
name|CAPACITY
case|:
return|return
name|initializeCapacityScheduler
argument_list|()
return|;
case|case
name|FAIR
case|:
return|return
name|initializeFairScheduler
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|configureReservationSystem ()
specifier|public
name|AbstractReservationSystem
name|configureReservationSystem
parameter_list|()
block|{
switch|switch
condition|(
name|getSchedulerType
argument_list|()
condition|)
block|{
case|case
name|CAPACITY
case|:
return|return
operator|new
name|CapacityReservationSystem
argument_list|()
return|;
case|case
name|FAIR
case|:
return|return
operator|new
name|FairReservationSystem
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|updateSchedulerConf (Configuration conf, String newQ)
specifier|public
name|void
name|updateSchedulerConf
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|newQ
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|getSchedulerType
argument_list|()
condition|)
block|{
case|case
name|CAPACITY
case|:
name|ReservationSystemTestUtil
operator|.
name|updateQueueConfiguration
argument_list|(
operator|(
name|CapacitySchedulerConfiguration
operator|)
name|conf
argument_list|,
name|newQ
argument_list|)
expr_stmt|;
case|case
name|FAIR
case|:
name|ReservationSystemTestUtil
operator|.
name|updateFSAllocationFile
argument_list|(
name|ALLOC_FILE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRMContext ()
specifier|public
name|RMContext
name|getRMContext
parameter_list|()
block|{
return|return
name|mockRMContext
return|;
block|}
DECL|method|clearRMContext ()
specifier|public
name|void
name|clearRMContext
parameter_list|()
block|{
name|mockRMContext
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|initializeCapacityScheduler ()
specifier|private
name|CapacityScheduler
name|initializeCapacityScheduler
parameter_list|()
block|{
comment|// stolen from TestCapacityScheduler
name|CapacitySchedulerConfiguration
name|conf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|ReservationSystemTestUtil
operator|.
name|setupQueueConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|CapacityScheduler
name|cs
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|CapacityScheduler
argument_list|()
argument_list|)
decl_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mockRMContext
operator|=
name|ReservationSystemTestUtil
operator|.
name|createRMContext
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cs
operator|.
name|setRMContext
argument_list|(
name|mockRMContext
argument_list|)
expr_stmt|;
try|try
block|{
name|cs
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|ReservationSystemTestUtil
operator|.
name|initializeRMContext
argument_list|(
literal|10
argument_list|,
name|cs
argument_list|,
name|mockRMContext
argument_list|)
expr_stmt|;
return|return
name|cs
return|;
block|}
DECL|method|createFSConfiguration ()
specifier|private
name|Configuration
name|createFSConfiguration
parameter_list|()
block|{
name|FairSchedulerTestBase
name|testHelper
init|=
operator|new
name|FairSchedulerTestBase
argument_list|()
decl_stmt|;
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
DECL|method|initializeFairScheduler ()
specifier|private
name|FairScheduler
name|initializeFairScheduler
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|createFSConfiguration
argument_list|()
decl_stmt|;
name|ReservationSystemTestUtil
operator|.
name|setupFSAllocationFile
argument_list|(
name|ALLOC_FILE
argument_list|)
expr_stmt|;
comment|// Setup
name|mockRMContext
operator|=
name|ReservationSystemTestUtil
operator|.
name|createRMContext
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|ReservationSystemTestUtil
operator|.
name|setupFairScheduler
argument_list|(
name|mockRMContext
argument_list|,
name|conf
argument_list|,
literal|10
argument_list|)
return|;
block|}
block|}
end_class

end_unit

