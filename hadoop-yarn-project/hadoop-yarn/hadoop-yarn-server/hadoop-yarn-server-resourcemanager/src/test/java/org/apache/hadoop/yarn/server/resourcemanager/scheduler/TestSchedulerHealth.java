begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|net
operator|.
name|NetworkTopology
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
name|util
operator|.
name|Time
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
name|ContainerId
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
name|event
operator|.
name|AsyncDispatcher
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
name|NodeManager
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
name|Application
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
name|ResourceManager
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
name|Task
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
name|rmnode
operator|.
name|RMNode
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
name|event
operator|.
name|NodeAddedSchedulerEvent
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
name|NodeUpdateSchedulerEvent
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_class
DECL|class|TestSchedulerHealth
specifier|public
class|class
name|TestSchedulerHealth
block|{
DECL|field|resourceManager
specifier|private
name|ResourceManager
name|resourceManager
decl_stmt|;
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|resourceManager
operator|=
operator|new
name|ResourceManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|RMNodeLabelsManager
name|createNodeLabelManager
parameter_list|()
block|{
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
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|mgr
return|;
block|}
block|}
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
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
name|CapacityScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getContainerTokenSecretManager
argument_list|()
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getNMTokenSecretManager
argument_list|()
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
operator|(
operator|(
name|AsyncDispatcher
operator|)
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getDispatcher
argument_list|()
operator|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCounts ()
specifier|public
name|void
name|testCounts
parameter_list|()
block|{
name|SchedulerHealth
name|sh
init|=
operator|new
name|SchedulerHealth
argument_list|()
decl_stmt|;
name|int
name|value
init|=
literal|1
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
literal|2
condition|;
operator|++
name|i
control|)
block|{
name|sh
operator|.
name|updateSchedulerPreemptionCounts
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|sh
operator|.
name|updateSchedulerAllocationCounts
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|sh
operator|.
name|updateSchedulerReservationCounts
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|sh
operator|.
name|updateSchedulerReleaseCounts
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
argument_list|,
name|sh
operator|.
name|getAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
argument_list|,
name|sh
operator|.
name|getReleaseCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
argument_list|,
name|sh
operator|.
name|getReservationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
argument_list|,
name|sh
operator|.
name|getPreemptionCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|sh
operator|.
name|getAggregateAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|sh
operator|.
name|getAggregateReleaseCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|sh
operator|.
name|getAggregateReservationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|sh
operator|.
name|getAggregatePreemptionCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOperationDetails ()
specifier|public
name|void
name|testOperationDetails
parameter_list|()
block|{
name|SchedulerHealth
name|sh
init|=
operator|new
name|SchedulerHealth
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|sh
operator|.
name|updateRelease
argument_list|(
name|now
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"testhost"
argument_list|,
literal|1234
argument_list|)
argument_list|,
name|ContainerId
operator|.
name|fromString
argument_list|(
literal|"container_1427562107907_0002_01_000001"
argument_list|)
argument_list|,
literal|"testqueue"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container_1427562107907_0002_01_000001"
argument_list|,
name|sh
operator|.
name|getLastReleaseDetails
argument_list|()
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testhost:1234"
argument_list|,
name|sh
operator|.
name|getLastReleaseDetails
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testqueue"
argument_list|,
name|sh
operator|.
name|getLastReleaseDetails
argument_list|()
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|now
argument_list|,
name|sh
operator|.
name|getLastReleaseDetails
argument_list|()
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sh
operator|.
name|getLastSchedulerRunTime
argument_list|()
argument_list|)
expr_stmt|;
name|now
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
name|sh
operator|.
name|updateReservation
argument_list|(
name|now
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"testhost1"
argument_list|,
literal|1234
argument_list|)
argument_list|,
name|ContainerId
operator|.
name|fromString
argument_list|(
literal|"container_1427562107907_0003_01_000001"
argument_list|)
argument_list|,
literal|"testqueue1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container_1427562107907_0003_01_000001"
argument_list|,
name|sh
operator|.
name|getLastReservationDetails
argument_list|()
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testhost1:1234"
argument_list|,
name|sh
operator|.
name|getLastReservationDetails
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testqueue1"
argument_list|,
name|sh
operator|.
name|getLastReservationDetails
argument_list|()
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|now
argument_list|,
name|sh
operator|.
name|getLastReservationDetails
argument_list|()
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sh
operator|.
name|getLastSchedulerRunTime
argument_list|()
argument_list|)
expr_stmt|;
name|now
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
name|sh
operator|.
name|updateAllocation
argument_list|(
name|now
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"testhost2"
argument_list|,
literal|1234
argument_list|)
argument_list|,
name|ContainerId
operator|.
name|fromString
argument_list|(
literal|"container_1427562107907_0004_01_000001"
argument_list|)
argument_list|,
literal|"testqueue2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container_1427562107907_0004_01_000001"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testhost2:1234"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testqueue2"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|now
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sh
operator|.
name|getLastSchedulerRunTime
argument_list|()
argument_list|)
expr_stmt|;
name|now
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
name|sh
operator|.
name|updatePreemption
argument_list|(
name|now
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"testhost3"
argument_list|,
literal|1234
argument_list|)
argument_list|,
name|ContainerId
operator|.
name|fromString
argument_list|(
literal|"container_1427562107907_0005_01_000001"
argument_list|)
argument_list|,
literal|"testqueue3"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container_1427562107907_0005_01_000001"
argument_list|,
name|sh
operator|.
name|getLastPreemptionDetails
argument_list|()
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testhost3:1234"
argument_list|,
name|sh
operator|.
name|getLastPreemptionDetails
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testqueue3"
argument_list|,
name|sh
operator|.
name|getLastPreemptionDetails
argument_list|()
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|now
argument_list|,
name|sh
operator|.
name|getLastPreemptionDetails
argument_list|()
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sh
operator|.
name|getLastSchedulerRunTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResourceUpdate ()
specifier|public
name|void
name|testResourceUpdate
parameter_list|()
block|{
name|SchedulerHealth
name|sh
init|=
operator|new
name|SchedulerHealth
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|sh
operator|.
name|updateSchedulerRunDetails
argument_list|(
name|now
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2048
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|now
argument_list|,
name|sh
operator|.
name|getLastSchedulerRunTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
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
name|sh
operator|.
name|getResourcesAllocated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2048
argument_list|,
literal|1
argument_list|)
argument_list|,
name|sh
operator|.
name|getResourcesReserved
argument_list|()
argument_list|)
expr_stmt|;
name|now
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
name|sh
operator|.
name|updateSchedulerReleaseDetails
argument_list|(
name|now
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|3072
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|now
argument_list|,
name|sh
operator|.
name|getLastSchedulerRunTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|3072
argument_list|,
literal|1
argument_list|)
argument_list|,
name|sh
operator|.
name|getResourcesReleased
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|registerNode (String hostName, int containerManagerPort, int httpPort, String rackName, Resource capability)
specifier|private
name|NodeManager
name|registerNode
parameter_list|(
name|String
name|hostName
parameter_list|,
name|int
name|containerManagerPort
parameter_list|,
name|int
name|httpPort
parameter_list|,
name|String
name|rackName
parameter_list|,
name|Resource
name|capability
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|NodeManager
name|nm
init|=
operator|new
name|NodeManager
argument_list|(
name|hostName
argument_list|,
name|containerManagerPort
argument_list|,
name|httpPort
argument_list|,
name|rackName
argument_list|,
name|capability
argument_list|,
name|resourceManager
argument_list|)
decl_stmt|;
name|NodeAddedSchedulerEvent
name|nodeAddEvent1
init|=
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm
operator|.
name|getNodeId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|handle
argument_list|(
name|nodeAddEvent1
argument_list|)
expr_stmt|;
return|return
name|nm
return|;
block|}
DECL|method|nodeUpdate (NodeManager nm)
specifier|private
name|void
name|nodeUpdate
parameter_list|(
name|NodeManager
name|nm
parameter_list|)
block|{
name|RMNode
name|node
init|=
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
comment|// Send a heartbeat to kick the tires on the Scheduler
name|NodeUpdateSchedulerEvent
name|nodeUpdate
init|=
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|handle
argument_list|(
name|nodeUpdate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCapacitySchedulerAllocation ()
specifier|public
name|void
name|testCapacitySchedulerAllocation
parameter_list|()
throws|throws
name|Exception
block|{
name|setup
argument_list|()
expr_stmt|;
name|boolean
name|isCapacityScheduler
init|=
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|instanceof
name|CapacityScheduler
decl_stmt|;
name|assumeTrue
argument_list|(
literal|"This test is only supported on Capacity Scheduler"
argument_list|,
name|isCapacityScheduler
argument_list|)
expr_stmt|;
comment|// Register node1
name|String
name|host_0
init|=
literal|"host_0"
decl_stmt|;
name|NodeManager
name|nm_0
init|=
name|registerNode
argument_list|(
name|host_0
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|5
operator|*
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// ResourceRequest priorities
name|Priority
name|priority_0
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Priority
name|priority_1
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Submit an application
name|Application
name|application_0
init|=
operator|new
name|Application
argument_list|(
literal|"user_0"
argument_list|,
literal|"default"
argument_list|,
name|resourceManager
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|submit
argument_list|()
expr_stmt|;
name|application_0
operator|.
name|addNodeManager
argument_list|(
name|host_0
argument_list|,
literal|1234
argument_list|,
name|nm_0
argument_list|)
expr_stmt|;
name|Resource
name|capability_0_0
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority_1
argument_list|,
name|capability_0_0
argument_list|)
expr_stmt|;
name|Resource
name|capability_0_1
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority_0
argument_list|,
name|capability_0_1
argument_list|)
expr_stmt|;
name|Task
name|task_0_0
init|=
operator|new
name|Task
argument_list|(
name|application_0
argument_list|,
name|priority_1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host_0
block|}
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addTask
argument_list|(
name|task_0_0
argument_list|)
expr_stmt|;
name|Task
name|task_0_1
init|=
operator|new
name|Task
argument_list|(
name|application_0
argument_list|,
name|priority_0
argument_list|,
operator|new
name|String
index|[]
block|{
name|host_0
block|}
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addTask
argument_list|(
name|task_0_1
argument_list|)
expr_stmt|;
comment|// Send resource requests to the scheduler
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// Send a heartbeat to kick the tires on the Scheduler
name|nodeUpdate
argument_list|(
name|nm_0
argument_list|)
expr_stmt|;
name|SchedulerHealth
name|sh
init|=
operator|(
operator|(
name|CapacityScheduler
operator|)
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|)
operator|.
name|getSchedulerHealth
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|sh
operator|.
name|getAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|3
operator|*
literal|1024
argument_list|,
literal|2
argument_list|)
argument_list|,
name|sh
operator|.
name|getResourcesAllocated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|sh
operator|.
name|getAggregateAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"host_0:1234"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"root.default"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|Task
name|task_0_2
init|=
operator|new
name|Task
argument_list|(
name|application_0
argument_list|,
name|priority_0
argument_list|,
operator|new
name|String
index|[]
block|{
name|host_0
block|}
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addTask
argument_list|(
name|task_0_2
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nodeUpdate
argument_list|(
name|nm_0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sh
operator|.
name|getAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|sh
operator|.
name|getResourcesAllocated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|sh
operator|.
name|getAggregateAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"host_0:1234"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"root.default"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCapacitySchedulerReservation ()
specifier|public
name|void
name|testCapacitySchedulerReservation
parameter_list|()
throws|throws
name|Exception
block|{
name|setup
argument_list|()
expr_stmt|;
name|boolean
name|isCapacityScheduler
init|=
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|instanceof
name|CapacityScheduler
decl_stmt|;
name|assumeTrue
argument_list|(
literal|"This test is only supported on Capacity Scheduler"
argument_list|,
name|isCapacityScheduler
argument_list|)
expr_stmt|;
comment|// Register nodes
name|String
name|host_0
init|=
literal|"host_0"
decl_stmt|;
name|NodeManager
name|nm_0
init|=
name|registerNode
argument_list|(
name|host_0
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|host_1
init|=
literal|"host_1"
decl_stmt|;
name|NodeManager
name|nm_1
init|=
name|registerNode
argument_list|(
name|host_1
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|5
operator|*
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|nodeUpdate
argument_list|(
name|nm_0
argument_list|)
expr_stmt|;
name|nodeUpdate
argument_list|(
name|nm_1
argument_list|)
expr_stmt|;
comment|// ResourceRequest priorities
name|Priority
name|priority_0
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Priority
name|priority_1
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Submit an application
name|Application
name|application_0
init|=
operator|new
name|Application
argument_list|(
literal|"user_0"
argument_list|,
literal|"default"
argument_list|,
name|resourceManager
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|submit
argument_list|()
expr_stmt|;
name|application_0
operator|.
name|addNodeManager
argument_list|(
name|host_0
argument_list|,
literal|1234
argument_list|,
name|nm_0
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|addNodeManager
argument_list|(
name|host_1
argument_list|,
literal|1234
argument_list|,
name|nm_1
argument_list|)
expr_stmt|;
name|Resource
name|capability_0_0
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority_1
argument_list|,
name|capability_0_0
argument_list|)
expr_stmt|;
name|Resource
name|capability_0_1
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority_0
argument_list|,
name|capability_0_1
argument_list|)
expr_stmt|;
name|Task
name|task_0_0
init|=
operator|new
name|Task
argument_list|(
name|application_0
argument_list|,
name|priority_1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host_0
block|}
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addTask
argument_list|(
name|task_0_0
argument_list|)
expr_stmt|;
comment|// Send resource requests to the scheduler
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// Send a heartbeat to kick the tires on the Scheduler
name|nodeUpdate
argument_list|(
name|nm_0
argument_list|)
expr_stmt|;
name|SchedulerHealth
name|sh
init|=
operator|(
operator|(
name|CapacityScheduler
operator|)
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|)
operator|.
name|getSchedulerHealth
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sh
operator|.
name|getAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
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
name|sh
operator|.
name|getResourcesAllocated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sh
operator|.
name|getAggregateAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"host_0:1234"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"root.default"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|Task
name|task_0_1
init|=
operator|new
name|Task
argument_list|(
name|application_0
argument_list|,
name|priority_0
argument_list|,
operator|new
name|String
index|[]
block|{
name|host_0
block|}
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addTask
argument_list|(
name|task_0_1
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nodeUpdate
argument_list|(
name|nm_0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sh
operator|.
name|getAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sh
operator|.
name|getReservationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|sh
operator|.
name|getResourcesReserved
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sh
operator|.
name|getAggregateAllocationCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"host_0:1234"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"root.default"
argument_list|,
name|sh
operator|.
name|getLastAllocationDetails
argument_list|()
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

