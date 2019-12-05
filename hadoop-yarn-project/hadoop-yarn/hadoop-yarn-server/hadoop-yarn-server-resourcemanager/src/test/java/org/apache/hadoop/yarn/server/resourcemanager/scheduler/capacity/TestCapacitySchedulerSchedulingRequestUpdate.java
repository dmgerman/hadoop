begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|Priority
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
name|ResourceRequest
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
name|ResourceSizing
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
name|MockAM
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
name|MockNM
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
name|MockRM
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
name|MockRMAppSubmissionData
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
name|MockRMAppSubmitter
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
name|nodelabels
operator|.
name|NullRMNodeLabelsManager
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
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|Test
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

begin_comment
comment|/**  * Test class for verifying Scheduling requests in CS.  */
end_comment

begin_class
DECL|class|TestCapacitySchedulerSchedulingRequestUpdate
specifier|public
class|class
name|TestCapacitySchedulerSchedulingRequestUpdate
extends|extends
name|CapacitySchedulerTestBase
block|{
annotation|@
name|Test
DECL|method|testBasicPendingResourceUpdate ()
specifier|public
name|void
name|testBasicPendingResourceUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|TestUtils
operator|.
name|getConfigurationWithQueueLabels
argument_list|(
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NODE_LABELS_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_PLACEMENT_CONSTRAINTS_HANDLER
argument_list|,
name|YarnConfiguration
operator|.
name|SCHEDULER_RM_PLACEMENT_CONSTRAINTS_HANDLER
argument_list|)
expr_stmt|;
specifier|final
name|RMNodeLabelsManager
name|mgr
init|=
operator|new
name|NullRMNodeLabelsManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addLabelsToNode
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h1"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"x"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
block|{
specifier|protected
name|RMNodeLabelsManager
name|createNodeLabelManager
parameter_list|()
block|{
return|return
name|mgr
return|;
block|}
block|}
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
comment|// label = x
operator|new
name|MockNM
argument_list|(
literal|"h1:1234"
argument_list|,
literal|200
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|registerNode
argument_list|()
expr_stmt|;
name|MockNM
name|nm2
init|=
comment|// label = ""
operator|new
name|MockNM
argument_list|(
literal|"h2:1234"
argument_list|,
literal|200
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|nm2
operator|.
name|registerNode
argument_list|()
expr_stmt|;
comment|// Launch app1 in queue=a1
name|MockRMAppSubmissionData
name|data1
init|=
name|MockRMAppSubmissionData
operator|.
name|Builder
operator|.
name|createWithMemory
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|rm
argument_list|)
operator|.
name|withAppName
argument_list|(
literal|"app"
argument_list|)
operator|.
name|withUser
argument_list|(
literal|"user"
argument_list|)
operator|.
name|withAcls
argument_list|(
literal|null
argument_list|)
operator|.
name|withQueue
argument_list|(
literal|"a1"
argument_list|)
operator|.
name|withUnmanagedAM
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RMApp
name|app1
init|=
name|MockRMAppSubmitter
operator|.
name|submit
argument_list|(
name|rm
argument_list|,
name|data1
argument_list|)
decl_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm
argument_list|,
name|nm2
argument_list|)
decl_stmt|;
comment|// Launch app2 in queue=b1
name|MockRMAppSubmissionData
name|data
init|=
name|MockRMAppSubmissionData
operator|.
name|Builder
operator|.
name|createWithMemory
argument_list|(
literal|8
operator|*
name|GB
argument_list|,
name|rm
argument_list|)
operator|.
name|withAppName
argument_list|(
literal|"app"
argument_list|)
operator|.
name|withUser
argument_list|(
literal|"user"
argument_list|)
operator|.
name|withAcls
argument_list|(
literal|null
argument_list|)
operator|.
name|withQueue
argument_list|(
literal|"b1"
argument_list|)
operator|.
name|withUnmanagedAM
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RMApp
name|app2
init|=
name|MockRMAppSubmitter
operator|.
name|submit
argument_list|(
name|rm
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|MockAM
name|am2
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app2
argument_list|,
name|rm
argument_list|,
name|nm2
argument_list|)
decl_stmt|;
comment|// am1 asks for 8 * 1GB container for no label
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|8
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"mapper"
argument_list|,
literal|"reducer"
argument_list|)
argument_list|,
literal|"mapper"
argument_list|,
literal|"reducer"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// am2 asks for 8 * 1GB container for no label
name|am2
operator|.
name|allocate
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ResourceRequest
operator|.
name|newInstance
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"*"
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|1
operator|*
name|GB
argument_list|)
argument_list|,
literal|8
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// root = a + b
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|16
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// am2 asks for 8 * 1GB container in another priority for no label
name|am2
operator|.
name|allocate
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ResourceRequest
operator|.
name|newInstance
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|"*"
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|1
operator|*
name|GB
argument_list|)
argument_list|,
literal|8
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b1"
argument_list|,
literal|16
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b"
argument_list|,
literal|16
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// root = a + b
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|24
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// am1 asks 4 GB resource instead of 8 * GB for priority=1
comment|// am1 asks for 8 * 1GB container for no label
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|4
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"mapper"
argument_list|,
literal|"reducer"
argument_list|)
argument_list|,
literal|"mapper"
argument_list|,
literal|"reducer"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|4
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|4
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b1"
argument_list|,
literal|16
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b"
argument_list|,
literal|16
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// root = a + b
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|20
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// am1 asks 8 * GB resource which label=x
name|am1
operator|.
name|allocate
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ResourceRequest
operator|.
name|newInstance
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|"*"
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|8
operator|*
name|GB
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|"x"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|4
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|4
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b1"
argument_list|,
literal|16
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b"
argument_list|,
literal|16
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// root = a + b
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|20
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// complete am1/am2, pending resource should be 0 now
name|AppAttemptRemovedSchedulerEvent
name|appRemovedEvent
init|=
operator|new
name|AppAttemptRemovedSchedulerEvent
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|handle
argument_list|(
name|appRemovedEvent
argument_list|)
expr_stmt|;
name|appRemovedEvent
operator|=
operator|new
name|AppAttemptRemovedSchedulerEvent
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|handle
argument_list|(
name|appRemovedEvent
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b1"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodePartitionPendingResourceUpdate ()
specifier|public
name|void
name|testNodePartitionPendingResourceUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|TestUtils
operator|.
name|getConfigurationWithQueueLabels
argument_list|(
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NODE_LABELS_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_PLACEMENT_CONSTRAINTS_HANDLER
argument_list|,
name|YarnConfiguration
operator|.
name|SCHEDULER_RM_PLACEMENT_CONSTRAINTS_HANDLER
argument_list|)
expr_stmt|;
specifier|final
name|RMNodeLabelsManager
name|mgr
init|=
operator|new
name|NullRMNodeLabelsManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addLabelsToNode
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h1"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"x"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
block|{
specifier|protected
name|RMNodeLabelsManager
name|createNodeLabelManager
parameter_list|()
block|{
return|return
name|mgr
return|;
block|}
block|}
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
comment|// label = x
operator|new
name|MockNM
argument_list|(
literal|"h1:1234"
argument_list|,
literal|200
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|registerNode
argument_list|()
expr_stmt|;
name|MockNM
name|nm2
init|=
comment|// label = ""
operator|new
name|MockNM
argument_list|(
literal|"h2:1234"
argument_list|,
literal|200
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|nm2
operator|.
name|registerNode
argument_list|()
expr_stmt|;
comment|// Launch app1 in queue=a1
name|MockRMAppSubmissionData
name|data1
init|=
name|MockRMAppSubmissionData
operator|.
name|Builder
operator|.
name|createWithMemory
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|rm
argument_list|)
operator|.
name|withAppName
argument_list|(
literal|"app"
argument_list|)
operator|.
name|withUser
argument_list|(
literal|"user"
argument_list|)
operator|.
name|withAcls
argument_list|(
literal|null
argument_list|)
operator|.
name|withQueue
argument_list|(
literal|"a1"
argument_list|)
operator|.
name|withUnmanagedAM
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RMApp
name|app1
init|=
name|MockRMAppSubmitter
operator|.
name|submit
argument_list|(
name|rm
argument_list|,
name|data1
argument_list|)
decl_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm
argument_list|,
name|nm2
argument_list|)
decl_stmt|;
comment|// Launch app2 in queue=b1
name|MockRMAppSubmissionData
name|data
init|=
name|MockRMAppSubmissionData
operator|.
name|Builder
operator|.
name|createWithMemory
argument_list|(
literal|8
operator|*
name|GB
argument_list|,
name|rm
argument_list|)
operator|.
name|withAppName
argument_list|(
literal|"app"
argument_list|)
operator|.
name|withUser
argument_list|(
literal|"user"
argument_list|)
operator|.
name|withAcls
argument_list|(
literal|null
argument_list|)
operator|.
name|withQueue
argument_list|(
literal|"b1"
argument_list|)
operator|.
name|withUnmanagedAM
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RMApp
name|app2
init|=
name|MockRMAppSubmitter
operator|.
name|submit
argument_list|(
name|rm
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|MockAM
name|am2
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app2
argument_list|,
name|rm
argument_list|,
name|nm2
argument_list|)
decl_stmt|;
comment|// am1 asks for 8 * 1GB container for "x"
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
literal|"x"
argument_list|,
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|8
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|"mapper"
argument_list|,
literal|"reducer"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// am2 asks for 8 * 1GB container for "x"
name|am2
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
literal|"x"
argument_list|,
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|8
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|"mapper"
argument_list|,
literal|"reducer"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// root = a + b
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|16
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// am1 asks for 6 * 1GB container for "x" in another priority
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
literal|"x"
argument_list|,
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|6
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|"mapper"
argument_list|,
literal|"reducer"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|14
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|14
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// root = a + b
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|22
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// am1 asks for 4 * 1GB container for "x" in priority=1, which should
comment|// override 8 * 1GB
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
literal|"x"
argument_list|,
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|4
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|"mapper"
argument_list|,
literal|"reducer"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|10
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|10
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b1"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b"
argument_list|,
literal|8
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// root = a + b
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|18
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// complete am1/am2, pending resource should be 0 now
name|AppAttemptRemovedSchedulerEvent
name|appRemovedEvent
init|=
operator|new
name|AppAttemptRemovedSchedulerEvent
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|handle
argument_list|(
name|appRemovedEvent
argument_list|)
expr_stmt|;
name|appRemovedEvent
operator|=
operator|new
name|AppAttemptRemovedSchedulerEvent
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|handle
argument_list|(
name|appRemovedEvent
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a1"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b1"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"b"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|rm
argument_list|,
literal|"root"
argument_list|,
literal|0
operator|*
name|GB
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

