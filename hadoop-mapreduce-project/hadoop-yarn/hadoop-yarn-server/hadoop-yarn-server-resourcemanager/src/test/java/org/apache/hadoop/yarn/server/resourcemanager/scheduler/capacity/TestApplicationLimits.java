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
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|UserGroupInformation
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
name|QueueACL
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
name|SchedulerApp
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
name|SchedulerNode
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
DECL|class|TestApplicationLimits
specifier|public
class|class
name|TestApplicationLimits
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestApplicationLimits
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|GB
specifier|final
specifier|static
name|int
name|GB
init|=
literal|1024
decl_stmt|;
DECL|field|queue
name|LeafQueue
name|queue
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupQueueConfiguration
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|CapacitySchedulerContext
name|csContext
init|=
name|mock
argument_list|(
name|CapacitySchedulerContext
operator|.
name|class
argument_list|)
decl_stmt|;
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
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getClusterResources
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|10
operator|*
literal|16
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|queues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
argument_list|()
decl_stmt|;
name|CSQueue
name|root
init|=
name|CapacityScheduler
operator|.
name|parseQueue
argument_list|(
name|csContext
argument_list|,
name|csConf
argument_list|,
literal|null
argument_list|,
literal|"root"
argument_list|,
name|queues
argument_list|,
name|queues
argument_list|,
name|CapacityScheduler
operator|.
name|queueComparator
argument_list|,
name|CapacityScheduler
operator|.
name|applicationComparator
argument_list|,
name|TestUtils
operator|.
name|spyHook
argument_list|)
decl_stmt|;
name|queue
operator|=
name|spy
argument_list|(
operator|new
name|LeafQueue
argument_list|(
name|csContext
argument_list|,
name|A
argument_list|,
name|root
argument_list|,
name|CapacityScheduler
operator|.
name|applicationComparator
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Stub out ACL checks
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|queue
argument_list|)
operator|.
name|hasAccess
argument_list|(
name|any
argument_list|(
name|QueueACL
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Some default values
name|doReturn
argument_list|(
literal|100
argument_list|)
operator|.
name|when
argument_list|(
name|queue
argument_list|)
operator|.
name|getMaxApplications
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|25
argument_list|)
operator|.
name|when
argument_list|(
name|queue
argument_list|)
operator|.
name|getMaxApplicationsPerUser
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|10
argument_list|)
operator|.
name|when
argument_list|(
name|queue
argument_list|)
operator|.
name|getMaximumActiveApplications
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|2
argument_list|)
operator|.
name|when
argument_list|(
name|queue
argument_list|)
operator|.
name|getMaximumActiveApplicationsPerUser
argument_list|()
expr_stmt|;
block|}
DECL|field|A
specifier|private
specifier|static
specifier|final
name|String
name|A
init|=
literal|"a"
decl_stmt|;
DECL|field|B
specifier|private
specifier|static
specifier|final
name|String
name|B
init|=
literal|"b"
decl_stmt|;
DECL|method|setupQueueConfiguration (CapacitySchedulerConfiguration conf)
specifier|private
name|void
name|setupQueueConfiguration
parameter_list|(
name|CapacitySchedulerConfiguration
name|conf
parameter_list|)
block|{
comment|// Define top-level queues
name|conf
operator|.
name|setQueues
argument_list|(
name|CapacityScheduler
operator|.
name|ROOT
argument_list|,
operator|new
name|String
index|[]
block|{
name|A
block|,
name|B
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|CapacityScheduler
operator|.
name|ROOT
argument_list|,
literal|100
argument_list|)
expr_stmt|;
specifier|final
name|String
name|Q_A
init|=
name|CapacityScheduler
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|A
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|Q_A
argument_list|,
literal|10
argument_list|)
expr_stmt|;
specifier|final
name|String
name|Q_B
init|=
name|CapacityScheduler
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|B
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|Q_B
argument_list|,
literal|90
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setup top-level queues a and b"
argument_list|)
expr_stmt|;
block|}
DECL|method|getMockApplication (int appId, String user)
specifier|private
name|SchedulerApp
name|getMockApplication
parameter_list|(
name|int
name|appId
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|SchedulerApp
name|application
init|=
name|mock
argument_list|(
name|SchedulerApp
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|TestUtils
operator|.
name|getMockApplicationAttemptId
argument_list|(
name|appId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|applicationAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|application
argument_list|)
operator|.
name|getApplicationId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|applicationAttemptId
argument_list|)
operator|.
name|when
argument_list|(
name|application
argument_list|)
operator|.
name|getApplicationAttemptId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|user
argument_list|)
operator|.
name|when
argument_list|(
name|application
argument_list|)
operator|.
name|getUser
argument_list|()
expr_stmt|;
return|return
name|application
return|;
block|}
annotation|@
name|Test
DECL|method|testLimitsComputation ()
specifier|public
name|void
name|testLimitsComputation
parameter_list|()
throws|throws
name|Exception
block|{
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupQueueConfiguration
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|CapacitySchedulerContext
name|csContext
init|=
name|mock
argument_list|(
name|CapacitySchedulerContext
operator|.
name|class
argument_list|)
decl_stmt|;
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
argument_list|)
argument_list|)
expr_stmt|;
comment|// Say cluster has 100 nodes of 16G each
name|Resource
name|clusterResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|100
operator|*
literal|16
operator|*
name|GB
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getClusterResources
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clusterResource
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|queues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
argument_list|()
decl_stmt|;
name|CSQueue
name|root
init|=
name|CapacityScheduler
operator|.
name|parseQueue
argument_list|(
name|csContext
argument_list|,
name|csConf
argument_list|,
literal|null
argument_list|,
literal|"root"
argument_list|,
name|queues
argument_list|,
name|queues
argument_list|,
name|CapacityScheduler
operator|.
name|queueComparator
argument_list|,
name|CapacityScheduler
operator|.
name|applicationComparator
argument_list|,
name|TestUtils
operator|.
name|spyHook
argument_list|)
decl_stmt|;
name|LeafQueue
name|queue
init|=
operator|(
name|LeafQueue
operator|)
name|queues
operator|.
name|get
argument_list|(
name|A
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Queue 'A' -"
operator|+
literal|" maxActiveApplications="
operator|+
name|queue
operator|.
name|getMaximumActiveApplications
argument_list|()
operator|+
literal|" maxActiveApplicationsPerUser="
operator|+
name|queue
operator|.
name|getMaximumActiveApplicationsPerUser
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|expectedMaxActiveApps
init|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
call|(
name|int
call|)
argument_list|(
operator|(
name|clusterResource
operator|.
name|getMemory
argument_list|()
operator|/
name|LeafQueue
operator|.
name|DEFAULT_AM_RESOURCE
operator|)
operator|*
name|csConf
operator|.
name|getMaximumApplicationMasterResourcePercent
argument_list|()
operator|*
name|queue
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedMaxActiveApps
argument_list|,
name|queue
operator|.
name|getMaximumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|int
call|)
argument_list|(
name|expectedMaxActiveApps
operator|*
operator|(
name|queue
operator|.
name|getUserLimit
argument_list|()
operator|/
literal|100.0f
operator|)
operator|*
name|queue
operator|.
name|getUserLimitFactor
argument_list|()
argument_list|)
argument_list|,
name|queue
operator|.
name|getMaximumActiveApplicationsPerUser
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add some nodes to the cluster& test new limits
name|clusterResource
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|120
operator|*
literal|16
operator|*
name|GB
argument_list|)
expr_stmt|;
name|root
operator|.
name|updateClusterResource
argument_list|(
name|clusterResource
argument_list|)
expr_stmt|;
name|expectedMaxActiveApps
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
call|(
name|int
call|)
argument_list|(
operator|(
name|clusterResource
operator|.
name|getMemory
argument_list|()
operator|/
name|LeafQueue
operator|.
name|DEFAULT_AM_RESOURCE
operator|)
operator|*
name|csConf
operator|.
name|getMaximumApplicationMasterResourcePercent
argument_list|()
operator|*
name|queue
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedMaxActiveApps
argument_list|,
name|queue
operator|.
name|getMaximumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|int
call|)
argument_list|(
name|expectedMaxActiveApps
operator|*
operator|(
name|queue
operator|.
name|getUserLimit
argument_list|()
operator|/
literal|100.0f
operator|)
operator|*
name|queue
operator|.
name|getUserLimitFactor
argument_list|()
argument_list|)
argument_list|,
name|queue
operator|.
name|getMaximumActiveApplicationsPerUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testActiveApplicationLimits ()
specifier|public
name|void
name|testActiveApplicationLimits
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|user_0
init|=
literal|"user_0"
decl_stmt|;
specifier|final
name|String
name|user_1
init|=
literal|"user_1"
decl_stmt|;
name|int
name|APPLICATION_ID
init|=
literal|0
decl_stmt|;
comment|// Submit first application
name|SchedulerApp
name|app_0
init|=
name|getMockApplication
argument_list|(
name|APPLICATION_ID
operator|++
argument_list|,
name|user_0
argument_list|)
decl_stmt|;
name|queue
operator|.
name|submitApplication
argument_list|(
name|app_0
argument_list|,
name|user_0
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Submit second application
name|SchedulerApp
name|app_1
init|=
name|getMockApplication
argument_list|(
name|APPLICATION_ID
operator|++
argument_list|,
name|user_0
argument_list|)
decl_stmt|;
name|queue
operator|.
name|submitApplication
argument_list|(
name|app_1
argument_list|,
name|user_0
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Submit third application, should remain pending
name|SchedulerApp
name|app_2
init|=
name|getMockApplication
argument_list|(
name|APPLICATION_ID
operator|++
argument_list|,
name|user_0
argument_list|)
decl_stmt|;
name|queue
operator|.
name|submitApplication
argument_list|(
name|app_2
argument_list|,
name|user_0
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Finish one application, app_2 should be activated
name|queue
operator|.
name|finishApplication
argument_list|(
name|app_0
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Submit another one for user_0
name|SchedulerApp
name|app_3
init|=
name|getMockApplication
argument_list|(
name|APPLICATION_ID
operator|++
argument_list|,
name|user_0
argument_list|)
decl_stmt|;
name|queue
operator|.
name|submitApplication
argument_list|(
name|app_3
argument_list|,
name|user_0
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Change queue limit to be smaller so 2 users can fill it up
name|doReturn
argument_list|(
literal|3
argument_list|)
operator|.
name|when
argument_list|(
name|queue
argument_list|)
operator|.
name|getMaximumActiveApplications
argument_list|()
expr_stmt|;
comment|// Submit first app for user_1
name|SchedulerApp
name|app_4
init|=
name|getMockApplication
argument_list|(
name|APPLICATION_ID
operator|++
argument_list|,
name|user_1
argument_list|)
decl_stmt|;
name|queue
operator|.
name|submitApplication
argument_list|(
name|app_4
argument_list|,
name|user_1
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Submit second app for user_1, should block due to queue-limit
name|SchedulerApp
name|app_5
init|=
name|getMockApplication
argument_list|(
name|APPLICATION_ID
operator|++
argument_list|,
name|user_1
argument_list|)
decl_stmt|;
name|queue
operator|.
name|submitApplication
argument_list|(
name|app_5
argument_list|,
name|user_1
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now finish one app of user_1 so app_5 should be activated
name|queue
operator|.
name|finishApplication
argument_list|(
name|app_4
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getNumActiveApplications
argument_list|(
name|user_1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getNumPendingApplications
argument_list|(
name|user_1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHeadroom ()
specifier|public
name|void
name|testHeadroom
parameter_list|()
throws|throws
name|Exception
block|{
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|csConf
operator|.
name|setUserLimit
argument_list|(
name|CapacityScheduler
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|A
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|setupQueueConfiguration
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|CapacitySchedulerContext
name|csContext
init|=
name|mock
argument_list|(
name|CapacitySchedulerContext
operator|.
name|class
argument_list|)
decl_stmt|;
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
argument_list|)
argument_list|)
expr_stmt|;
comment|// Say cluster has 100 nodes of 16G each
name|Resource
name|clusterResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|100
operator|*
literal|16
operator|*
name|GB
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|csContext
operator|.
name|getClusterResources
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clusterResource
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|queues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
argument_list|()
decl_stmt|;
name|CapacityScheduler
operator|.
name|parseQueue
argument_list|(
name|csContext
argument_list|,
name|csConf
argument_list|,
literal|null
argument_list|,
literal|"root"
argument_list|,
name|queues
argument_list|,
name|queues
argument_list|,
name|CapacityScheduler
operator|.
name|queueComparator
argument_list|,
name|CapacityScheduler
operator|.
name|applicationComparator
argument_list|,
name|TestUtils
operator|.
name|spyHook
argument_list|)
expr_stmt|;
comment|// Manipulate queue 'a'
name|LeafQueue
name|queue
init|=
name|TestLeafQueue
operator|.
name|stubLeafQueue
argument_list|(
operator|(
name|LeafQueue
operator|)
name|queues
operator|.
name|get
argument_list|(
name|A
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|host_0
init|=
literal|"host_0"
decl_stmt|;
name|String
name|rack_0
init|=
literal|"rack_0"
decl_stmt|;
name|SchedulerNode
name|node_0
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|host_0
argument_list|,
name|rack_0
argument_list|,
literal|0
argument_list|,
literal|16
operator|*
name|GB
argument_list|)
decl_stmt|;
specifier|final
name|String
name|user_0
init|=
literal|"user_0"
decl_stmt|;
specifier|final
name|String
name|user_1
init|=
literal|"user_1"
decl_stmt|;
name|int
name|APPLICATION_ID
init|=
literal|0
decl_stmt|;
comment|// Submit first application from user_0, check headroom
name|SchedulerApp
name|app_0_0
init|=
name|getMockApplication
argument_list|(
name|APPLICATION_ID
operator|++
argument_list|,
name|user_0
argument_list|)
decl_stmt|;
name|queue
operator|.
name|submitApplication
argument_list|(
name|app_0_0
argument_list|,
name|user_0
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|queue
operator|.
name|assignContainers
argument_list|(
name|clusterResource
argument_list|,
name|node_0
argument_list|)
expr_stmt|;
comment|// Schedule to compute
name|Resource
name|expectedHeadroom
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|10
operator|*
literal|16
operator|*
name|GB
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|app_0_0
argument_list|)
operator|.
name|setAvailableResourceLimit
argument_list|(
name|eq
argument_list|(
name|expectedHeadroom
argument_list|)
argument_list|)
expr_stmt|;
comment|// Submit second application from user_0, check headroom
name|SchedulerApp
name|app_0_1
init|=
name|getMockApplication
argument_list|(
name|APPLICATION_ID
operator|++
argument_list|,
name|user_0
argument_list|)
decl_stmt|;
name|queue
operator|.
name|submitApplication
argument_list|(
name|app_0_1
argument_list|,
name|user_0
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|queue
operator|.
name|assignContainers
argument_list|(
name|clusterResource
argument_list|,
name|node_0
argument_list|)
expr_stmt|;
comment|// Schedule to compute
name|verify
argument_list|(
name|app_0_0
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|setAvailableResourceLimit
argument_list|(
name|eq
argument_list|(
name|expectedHeadroom
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|app_0_1
argument_list|)
operator|.
name|setAvailableResourceLimit
argument_list|(
name|eq
argument_list|(
name|expectedHeadroom
argument_list|)
argument_list|)
expr_stmt|;
comment|// no change
comment|// Submit first application from user_1, check  for new headroom
name|SchedulerApp
name|app_1_0
init|=
name|getMockApplication
argument_list|(
name|APPLICATION_ID
operator|++
argument_list|,
name|user_1
argument_list|)
decl_stmt|;
name|queue
operator|.
name|submitApplication
argument_list|(
name|app_1_0
argument_list|,
name|user_1
argument_list|,
name|A
argument_list|)
expr_stmt|;
name|queue
operator|.
name|assignContainers
argument_list|(
name|clusterResource
argument_list|,
name|node_0
argument_list|)
expr_stmt|;
comment|// Schedule to compute
name|expectedHeadroom
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|10
operator|*
literal|16
operator|*
name|GB
operator|/
literal|2
argument_list|)
expr_stmt|;
comment|// changes
name|verify
argument_list|(
name|app_0_0
argument_list|)
operator|.
name|setAvailableResourceLimit
argument_list|(
name|eq
argument_list|(
name|expectedHeadroom
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|app_0_1
argument_list|)
operator|.
name|setAvailableResourceLimit
argument_list|(
name|eq
argument_list|(
name|expectedHeadroom
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|app_1_0
argument_list|)
operator|.
name|setAvailableResourceLimit
argument_list|(
name|eq
argument_list|(
name|expectedHeadroom
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now reduce cluster size and check for the smaller headroom
name|clusterResource
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|90
operator|*
literal|16
operator|*
name|GB
argument_list|)
expr_stmt|;
name|queue
operator|.
name|assignContainers
argument_list|(
name|clusterResource
argument_list|,
name|node_0
argument_list|)
expr_stmt|;
comment|// Schedule to compute
name|expectedHeadroom
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|9
operator|*
literal|16
operator|*
name|GB
operator|/
literal|2
argument_list|)
expr_stmt|;
comment|// changes
name|verify
argument_list|(
name|app_0_0
argument_list|)
operator|.
name|setAvailableResourceLimit
argument_list|(
name|eq
argument_list|(
name|expectedHeadroom
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|app_0_1
argument_list|)
operator|.
name|setAvailableResourceLimit
argument_list|(
name|eq
argument_list|(
name|expectedHeadroom
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|app_1_0
argument_list|)
operator|.
name|setAvailableResourceLimit
argument_list|(
name|eq
argument_list|(
name|expectedHeadroom
argument_list|)
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
block|{      }
block|}
end_class

end_unit

