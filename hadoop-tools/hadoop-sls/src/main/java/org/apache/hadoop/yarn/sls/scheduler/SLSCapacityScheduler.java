begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.scheduler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|scheduler
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
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|Configurable
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
name|Container
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
name|ContainerExitStatus
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
name|ContainerStatus
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
name|SchedulingRequest
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
name|rmcontainer
operator|.
name|RMContainer
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
name|UpdatedContainerInfo
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
name|Allocation
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
name|ContainerUpdates
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
name|SchedulerAppReport
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
name|SchedulerApplication
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|event
operator|.
name|SchedulerEvent
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
name|SchedulerEventType
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
name|sls
operator|.
name|SLSRunner
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
name|sls
operator|.
name|conf
operator|.
name|SLSConfiguration
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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SLSCapacityScheduler
specifier|public
class|class
name|SLSCapacityScheduler
extends|extends
name|CapacityScheduler
implements|implements
name|SchedulerWrapper
implements|,
name|Configurable
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|appQueueMap
specifier|private
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|String
argument_list|>
name|appQueueMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|preemptionContainerMap
specifier|private
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Resource
argument_list|>
name|preemptionContainerMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ContainerId
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
comment|// metrics
DECL|field|schedulerMetrics
specifier|private
name|SchedulerMetrics
name|schedulerMetrics
decl_stmt|;
DECL|field|metricsON
specifier|private
name|boolean
name|metricsON
decl_stmt|;
DECL|field|tracker
specifier|private
name|Tracker
name|tracker
decl_stmt|;
DECL|method|getTracker ()
specifier|public
name|Tracker
name|getTracker
parameter_list|()
block|{
return|return
name|tracker
return|;
block|}
DECL|method|SLSCapacityScheduler ()
specifier|public
name|SLSCapacityScheduler
parameter_list|()
block|{
name|tracker
operator|=
operator|new
name|Tracker
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|metricsON
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|SLSConfiguration
operator|.
name|METRICS_SWITCH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|metricsON
condition|)
block|{
try|try
block|{
name|schedulerMetrics
operator|=
name|SchedulerMetrics
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|,
name|CapacityScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|schedulerMetrics
operator|.
name|init
argument_list|(
name|this
argument_list|,
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|allocate (ApplicationAttemptId attemptId, List<ResourceRequest> resourceRequests, List<SchedulingRequest> schedulingRequests, List<ContainerId> containerIds, List<String> strings, List<String> strings2, ContainerUpdates updateRequests)
specifier|public
name|Allocation
name|allocate
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
parameter_list|,
name|List
argument_list|<
name|SchedulingRequest
argument_list|>
name|schedulingRequests
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|strings2
parameter_list|,
name|ContainerUpdates
name|updateRequests
parameter_list|)
block|{
if|if
condition|(
name|metricsON
condition|)
block|{
specifier|final
name|Timer
operator|.
name|Context
name|context
init|=
name|schedulerMetrics
operator|.
name|getSchedulerAllocateTimer
argument_list|()
operator|.
name|time
argument_list|()
decl_stmt|;
name|Allocation
name|allocation
init|=
literal|null
decl_stmt|;
try|try
block|{
name|allocation
operator|=
name|super
operator|.
name|allocate
argument_list|(
name|attemptId
argument_list|,
name|resourceRequests
argument_list|,
name|schedulingRequests
argument_list|,
name|containerIds
argument_list|,
name|strings
argument_list|,
name|strings2
argument_list|,
name|updateRequests
argument_list|)
expr_stmt|;
return|return
name|allocation
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|stop
argument_list|()
expr_stmt|;
name|schedulerMetrics
operator|.
name|increaseSchedulerAllocationCounter
argument_list|()
expr_stmt|;
try|try
block|{
name|updateQueueWithAllocateRequest
argument_list|(
name|allocation
argument_list|,
name|attemptId
argument_list|,
name|resourceRequests
argument_list|,
name|containerIds
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
return|return
name|super
operator|.
name|allocate
argument_list|(
name|attemptId
argument_list|,
name|resourceRequests
argument_list|,
name|schedulingRequests
argument_list|,
name|containerIds
argument_list|,
name|strings
argument_list|,
name|strings2
argument_list|,
name|updateRequests
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|handle (SchedulerEvent schedulerEvent)
specifier|public
name|void
name|handle
parameter_list|(
name|SchedulerEvent
name|schedulerEvent
parameter_list|)
block|{
if|if
condition|(
operator|!
name|metricsON
condition|)
block|{
name|super
operator|.
name|handle
argument_list|(
name|schedulerEvent
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|schedulerMetrics
operator|.
name|isRunning
argument_list|()
condition|)
block|{
name|schedulerMetrics
operator|.
name|setRunning
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|Timer
operator|.
name|Context
name|handlerTimer
init|=
literal|null
decl_stmt|;
name|Timer
operator|.
name|Context
name|operationTimer
init|=
literal|null
decl_stmt|;
name|NodeUpdateSchedulerEventWrapper
name|eventWrapper
decl_stmt|;
try|try
block|{
if|if
condition|(
name|schedulerEvent
operator|.
name|getType
argument_list|()
operator|==
name|SchedulerEventType
operator|.
name|NODE_UPDATE
operator|&&
name|schedulerEvent
operator|instanceof
name|NodeUpdateSchedulerEvent
condition|)
block|{
name|eventWrapper
operator|=
operator|new
name|NodeUpdateSchedulerEventWrapper
argument_list|(
operator|(
name|NodeUpdateSchedulerEvent
operator|)
name|schedulerEvent
argument_list|)
expr_stmt|;
name|schedulerEvent
operator|=
name|eventWrapper
expr_stmt|;
name|updateQueueWithNodeUpdate
argument_list|(
name|eventWrapper
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|schedulerEvent
operator|.
name|getType
argument_list|()
operator|==
name|SchedulerEventType
operator|.
name|APP_ATTEMPT_REMOVED
operator|&&
name|schedulerEvent
operator|instanceof
name|AppAttemptRemovedSchedulerEvent
condition|)
block|{
comment|// check if having AM Container, update resource usage information
name|AppAttemptRemovedSchedulerEvent
name|appRemoveEvent
init|=
operator|(
name|AppAttemptRemovedSchedulerEvent
operator|)
name|schedulerEvent
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|appRemoveEvent
operator|.
name|getApplicationAttemptID
argument_list|()
decl_stmt|;
name|String
name|queue
init|=
name|appQueueMap
operator|.
name|get
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|SchedulerAppReport
name|app
init|=
name|super
operator|.
name|getSchedulerAppInfo
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|app
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// have 0 or 1
comment|// should have one container which is AM container
name|RMContainer
name|rmc
init|=
name|app
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|schedulerMetrics
operator|.
name|updateQueueMetricsByRelease
argument_list|(
name|rmc
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
name|handlerTimer
operator|=
name|schedulerMetrics
operator|.
name|getSchedulerHandleTimer
argument_list|()
operator|.
name|time
argument_list|()
expr_stmt|;
name|operationTimer
operator|=
name|schedulerMetrics
operator|.
name|getSchedulerHandleTimer
argument_list|(
name|schedulerEvent
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|time
argument_list|()
expr_stmt|;
name|super
operator|.
name|handle
argument_list|(
name|schedulerEvent
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|handlerTimer
operator|!=
literal|null
condition|)
block|{
name|handlerTimer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|operationTimer
operator|!=
literal|null
condition|)
block|{
name|operationTimer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|schedulerMetrics
operator|.
name|increaseSchedulerHandleCounter
argument_list|(
name|schedulerEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|schedulerEvent
operator|.
name|getType
argument_list|()
operator|==
name|SchedulerEventType
operator|.
name|APP_ATTEMPT_REMOVED
operator|&&
name|schedulerEvent
operator|instanceof
name|AppAttemptRemovedSchedulerEvent
condition|)
block|{
name|SLSRunner
operator|.
name|decreaseRemainingApps
argument_list|()
expr_stmt|;
name|AppAttemptRemovedSchedulerEvent
name|appRemoveEvent
init|=
operator|(
name|AppAttemptRemovedSchedulerEvent
operator|)
name|schedulerEvent
decl_stmt|;
name|appQueueMap
operator|.
name|remove
argument_list|(
name|appRemoveEvent
operator|.
name|getApplicationAttemptID
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|schedulerEvent
operator|.
name|getType
argument_list|()
operator|==
name|SchedulerEventType
operator|.
name|APP_ATTEMPT_ADDED
operator|&&
name|schedulerEvent
operator|instanceof
name|AppAttemptAddedSchedulerEvent
condition|)
block|{
name|AppAttemptAddedSchedulerEvent
name|appAddEvent
init|=
operator|(
name|AppAttemptAddedSchedulerEvent
operator|)
name|schedulerEvent
decl_stmt|;
name|SchedulerApplication
name|app
init|=
name|applications
operator|.
name|get
argument_list|(
name|appAddEvent
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|appQueueMap
operator|.
name|put
argument_list|(
name|appAddEvent
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|app
operator|.
name|getQueue
argument_list|()
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|updateQueueWithNodeUpdate ( NodeUpdateSchedulerEventWrapper eventWrapper)
specifier|private
name|void
name|updateQueueWithNodeUpdate
parameter_list|(
name|NodeUpdateSchedulerEventWrapper
name|eventWrapper
parameter_list|)
block|{
name|RMNodeWrapper
name|node
init|=
operator|(
name|RMNodeWrapper
operator|)
name|eventWrapper
operator|.
name|getRMNode
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|UpdatedContainerInfo
argument_list|>
name|containerList
init|=
name|node
operator|.
name|getContainerUpdates
argument_list|()
decl_stmt|;
for|for
control|(
name|UpdatedContainerInfo
name|info
range|:
name|containerList
control|)
block|{
for|for
control|(
name|ContainerStatus
name|status
range|:
name|info
operator|.
name|getCompletedContainers
argument_list|()
control|)
block|{
name|ContainerId
name|containerId
init|=
name|status
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|SchedulerAppReport
name|app
init|=
name|super
operator|.
name|getSchedulerAppInfo
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|app
operator|==
literal|null
condition|)
block|{
comment|// this happens for the AM container
comment|// The app have already removed when the NM sends the release
comment|// information.
continue|continue;
block|}
name|String
name|queue
init|=
name|appQueueMap
operator|.
name|get
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|releasedMemory
init|=
literal|0
decl_stmt|,
name|releasedVCores
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|status
operator|.
name|getExitStatus
argument_list|()
operator|==
name|ContainerExitStatus
operator|.
name|SUCCESS
condition|)
block|{
for|for
control|(
name|RMContainer
name|rmc
range|:
name|app
operator|.
name|getLiveContainers
argument_list|()
control|)
block|{
if|if
condition|(
name|rmc
operator|.
name|getContainerId
argument_list|()
operator|==
name|containerId
condition|)
block|{
name|releasedMemory
operator|+=
name|rmc
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|releasedVCores
operator|+=
name|rmc
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|status
operator|.
name|getExitStatus
argument_list|()
operator|==
name|ContainerExitStatus
operator|.
name|ABORTED
condition|)
block|{
if|if
condition|(
name|preemptionContainerMap
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|Resource
name|preResource
init|=
name|preemptionContainerMap
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|releasedMemory
operator|+=
name|preResource
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|releasedVCores
operator|+=
name|preResource
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
name|preemptionContainerMap
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
comment|// update queue counters
name|schedulerMetrics
operator|.
name|updateQueueMetricsByRelease
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
name|releasedMemory
argument_list|,
name|releasedVCores
argument_list|)
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|updateQueueWithAllocateRequest (Allocation allocation, ApplicationAttemptId attemptId, List<ResourceRequest> resourceRequests, List<ContainerId> containerIds)
specifier|private
name|void
name|updateQueueWithAllocateRequest
parameter_list|(
name|Allocation
name|allocation
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
parameter_list|)
throws|throws
name|IOException
block|{
comment|// update queue information
name|Resource
name|pendingResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Resource
name|allocatedResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|queueName
init|=
name|appQueueMap
operator|.
name|get
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
comment|// container requested
for|for
control|(
name|ResourceRequest
name|request
range|:
name|resourceRequests
control|)
block|{
if|if
condition|(
name|request
operator|.
name|getResourceName
argument_list|()
operator|.
name|equals
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|pendingResource
argument_list|,
name|Resources
operator|.
name|multiply
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|,
name|request
operator|.
name|getNumContainers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// container allocated
for|for
control|(
name|Container
name|container
range|:
name|allocation
operator|.
name|getContainers
argument_list|()
control|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|allocatedResource
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|pendingResource
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// container released from AM
name|SchedulerAppReport
name|report
init|=
name|super
operator|.
name|getSchedulerAppInfo
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
for|for
control|(
name|ContainerId
name|containerId
range|:
name|containerIds
control|)
block|{
name|Container
name|container
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RMContainer
name|c
range|:
name|report
operator|.
name|getLiveContainers
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getContainerId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|container
operator|=
name|c
operator|.
name|getContainer
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
comment|// released allocated containers
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|allocatedResource
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|RMContainer
name|c
range|:
name|report
operator|.
name|getReservedContainers
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getContainerId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|container
operator|=
name|c
operator|.
name|getContainer
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
comment|// released reserved containers
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|pendingResource
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// containers released/preemption from scheduler
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|preemptionContainers
init|=
operator|new
name|HashSet
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|allocation
operator|.
name|getContainerPreemptions
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|preemptionContainers
operator|.
name|addAll
argument_list|(
name|allocation
operator|.
name|getContainerPreemptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allocation
operator|.
name|getStrictContainerPreemptions
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|preemptionContainers
operator|.
name|addAll
argument_list|(
name|allocation
operator|.
name|getStrictContainerPreemptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|preemptionContainers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|ContainerId
name|containerId
range|:
name|preemptionContainers
control|)
block|{
if|if
condition|(
operator|!
name|preemptionContainerMap
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|Container
name|container
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RMContainer
name|c
range|:
name|report
operator|.
name|getLiveContainers
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getContainerId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|container
operator|=
name|c
operator|.
name|getContainer
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|preemptionContainerMap
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// update metrics
name|schedulerMetrics
operator|.
name|updateQueueMetrics
argument_list|(
name|pendingResource
argument_list|,
name|allocatedResource
argument_list|,
name|queueName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|metricsON
condition|)
block|{
name|schedulerMetrics
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|getSchedulerMetrics ()
specifier|public
name|SchedulerMetrics
name|getSchedulerMetrics
parameter_list|()
block|{
return|return
name|schedulerMetrics
return|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|getRealQueueName (String queue)
specifier|public
name|String
name|getRealQueueName
parameter_list|(
name|String
name|queue
parameter_list|)
throws|throws
name|YarnException
block|{
if|if
condition|(
name|getQueue
argument_list|(
name|queue
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Can't find the queue by the given name: "
operator|+
name|queue
operator|+
literal|"! Please check if queue "
operator|+
name|queue
operator|+
literal|" is in the allocation file."
argument_list|)
throw|;
block|}
return|return
name|getQueue
argument_list|(
name|queue
argument_list|)
operator|.
name|getQueueName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

