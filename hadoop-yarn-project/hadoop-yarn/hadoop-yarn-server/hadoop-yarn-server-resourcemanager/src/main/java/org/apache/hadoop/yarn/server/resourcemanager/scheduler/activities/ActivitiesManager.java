begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.activities
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
name|activities
package|;
end_package

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
name|service
operator|.
name|AbstractService
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
name|FinalApplicationStatus
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
name|SchedulerApplicationAttempt
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
name|webapp
operator|.
name|dao
operator|.
name|ActivitiesInfo
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
name|webapp
operator|.
name|dao
operator|.
name|AppActivitiesInfo
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
name|SystemClock
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
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
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * A class to store node or application allocations.  * It mainly contains operations for allocation start, add, update and finish.  */
end_comment

begin_class
DECL|class|ActivitiesManager
specifier|public
class|class
name|ActivitiesManager
extends|extends
name|AbstractService
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
name|ActivitiesManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recordingNodesAllocation
specifier|private
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|NodeAllocation
argument_list|>
argument_list|>
name|recordingNodesAllocation
decl_stmt|;
DECL|field|completedNodeAllocations
specifier|private
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|NodeAllocation
argument_list|>
argument_list|>
name|completedNodeAllocations
decl_stmt|;
DECL|field|activeRecordedNodes
specifier|private
name|Set
argument_list|<
name|NodeId
argument_list|>
name|activeRecordedNodes
decl_stmt|;
specifier|private
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|Long
argument_list|>
DECL|field|recordingAppActivitiesUntilSpecifiedTime
name|recordingAppActivitiesUntilSpecifiedTime
decl_stmt|;
DECL|field|appsAllocation
specifier|private
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|AppAllocation
argument_list|>
name|appsAllocation
decl_stmt|;
specifier|private
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|AppAllocation
argument_list|>
argument_list|>
DECL|field|completedAppAllocations
name|completedAppAllocations
decl_stmt|;
DECL|field|recordNextAvailableNode
specifier|private
name|boolean
name|recordNextAvailableNode
init|=
literal|false
decl_stmt|;
DECL|field|lastAvailableNodeActivities
specifier|private
name|List
argument_list|<
name|NodeAllocation
argument_list|>
name|lastAvailableNodeActivities
init|=
literal|null
decl_stmt|;
DECL|field|cleanUpThread
specifier|private
name|Thread
name|cleanUpThread
decl_stmt|;
DECL|field|timeThreshold
specifier|private
name|int
name|timeThreshold
init|=
literal|600
operator|*
literal|1000
decl_stmt|;
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|stopped
specifier|private
specifier|volatile
name|boolean
name|stopped
decl_stmt|;
DECL|method|ActivitiesManager (RMContext rmContext)
specifier|public
name|ActivitiesManager
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|super
argument_list|(
name|ActivitiesManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|recordingNodesAllocation
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|completedNodeAllocations
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|appsAllocation
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|completedAppAllocations
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|activeRecordedNodes
operator|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|recordingAppActivitiesUntilSpecifiedTime
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
DECL|method|getAppActivitiesInfo (ApplicationId applicationId)
specifier|public
name|AppActivitiesInfo
name|getAppActivitiesInfo
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
if|if
condition|(
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
operator|.
name|getFinalApplicationStatus
argument_list|()
operator|==
name|FinalApplicationStatus
operator|.
name|UNDEFINED
condition|)
block|{
name|List
argument_list|<
name|AppAllocation
argument_list|>
name|allocations
init|=
name|completedAppAllocations
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
return|return
operator|new
name|AppActivitiesInfo
argument_list|(
name|allocations
argument_list|,
name|applicationId
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|AppActivitiesInfo
argument_list|(
literal|"fail to get application activities after finished"
argument_list|,
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|getActivitiesInfo (String nodeId)
specifier|public
name|ActivitiesInfo
name|getActivitiesInfo
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
name|List
argument_list|<
name|NodeAllocation
argument_list|>
name|allocations
decl_stmt|;
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
block|{
name|allocations
operator|=
name|lastAvailableNodeActivities
expr_stmt|;
block|}
else|else
block|{
name|allocations
operator|=
name|completedNodeAllocations
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|fromString
argument_list|(
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ActivitiesInfo
argument_list|(
name|allocations
argument_list|,
name|nodeId
argument_list|)
return|;
block|}
DECL|method|recordNextNodeUpdateActivities (String nodeId)
specifier|public
name|void
name|recordNextNodeUpdateActivities
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
block|{
name|recordNextAvailableNode
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|activeRecordedNodes
operator|.
name|add
argument_list|(
name|NodeId
operator|.
name|fromString
argument_list|(
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|turnOnAppActivitiesRecording (ApplicationId applicationId, double maxTime)
specifier|public
name|void
name|turnOnAppActivitiesRecording
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|double
name|maxTime
parameter_list|)
block|{
name|long
name|startTS
init|=
name|SystemClock
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|long
name|endTS
init|=
name|startTS
operator|+
call|(
name|long
call|)
argument_list|(
name|maxTime
operator|*
literal|1000
argument_list|)
decl_stmt|;
name|recordingAppActivitiesUntilSpecifiedTime
operator|.
name|put
argument_list|(
name|applicationId
argument_list|,
name|endTS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|cleanUpThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stopped
operator|&&
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|NodeAllocation
argument_list|>
argument_list|>
argument_list|>
name|ite
init|=
name|completedNodeAllocations
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|ite
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|NodeAllocation
argument_list|>
argument_list|>
name|nodeAllocation
init|=
name|ite
operator|.
name|next
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NodeAllocation
argument_list|>
name|allocations
init|=
name|nodeAllocation
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|long
name|currTS
init|=
name|SystemClock
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|allocations
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|allocations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTimeStamp
argument_list|()
operator|-
name|currTS
operator|>
name|timeThreshold
condition|)
block|{
name|ite
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|AppAllocation
argument_list|>
argument_list|>
argument_list|>
name|iteApp
init|=
name|completedAppAllocations
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iteApp
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|AppAllocation
argument_list|>
argument_list|>
name|appAllocation
init|=
name|iteApp
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appAllocation
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getFinalApplicationStatus
argument_list|()
operator|!=
name|FinalApplicationStatus
operator|.
name|UNDEFINED
condition|)
block|{
name|iteApp
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" thread interrupted"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|cleanUpThread
operator|.
name|setName
argument_list|(
literal|"ActivitiesManager thread."
argument_list|)
expr_stmt|;
name|cleanUpThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|stopped
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|cleanUpThread
operator|!=
literal|null
condition|)
block|{
name|cleanUpThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|cleanUpThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted Exception while stopping"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|startNodeUpdateRecording (NodeId nodeID)
name|void
name|startNodeUpdateRecording
parameter_list|(
name|NodeId
name|nodeID
parameter_list|)
block|{
if|if
condition|(
name|recordNextAvailableNode
condition|)
block|{
name|recordNextNodeUpdateActivities
argument_list|(
name|nodeID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|activeRecordedNodes
operator|.
name|contains
argument_list|(
name|nodeID
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|NodeAllocation
argument_list|>
name|nodeAllocation
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|recordingNodesAllocation
operator|.
name|put
argument_list|(
name|nodeID
argument_list|,
name|nodeAllocation
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|startAppAllocationRecording (NodeId nodeID, long currTS, SchedulerApplicationAttempt application)
name|void
name|startAppAllocationRecording
parameter_list|(
name|NodeId
name|nodeID
parameter_list|,
name|long
name|currTS
parameter_list|,
name|SchedulerApplicationAttempt
name|application
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|application
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
if|if
condition|(
name|recordingAppActivitiesUntilSpecifiedTime
operator|.
name|containsKey
argument_list|(
name|applicationId
argument_list|)
operator|&&
name|recordingAppActivitiesUntilSpecifiedTime
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
operator|>
name|currTS
condition|)
block|{
name|appsAllocation
operator|.
name|put
argument_list|(
name|applicationId
argument_list|,
operator|new
name|AppAllocation
argument_list|(
name|application
operator|.
name|getPriority
argument_list|()
argument_list|,
name|nodeID
argument_list|,
name|application
operator|.
name|getQueueName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|recordingAppActivitiesUntilSpecifiedTime
operator|.
name|containsKey
argument_list|(
name|applicationId
argument_list|)
operator|&&
name|recordingAppActivitiesUntilSpecifiedTime
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
operator|<=
name|currTS
condition|)
block|{
name|turnOffActivityMonitoringForApp
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add queue, application or container activity into specific node allocation.
DECL|method|addSchedulingActivityForNode (SchedulerNode node, String parentName, String childName, String priority, ActivityState state, String diagnostic, String type)
name|void
name|addSchedulingActivityForNode
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|String
name|parentName
parameter_list|,
name|String
name|childName
parameter_list|,
name|String
name|priority
parameter_list|,
name|ActivityState
name|state
parameter_list|,
name|String
name|diagnostic
parameter_list|,
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|shouldRecordThisNode
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|NodeAllocation
name|nodeAllocation
init|=
name|getCurrentNodeAllocation
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
name|nodeAllocation
operator|.
name|addAllocationActivity
argument_list|(
name|parentName
argument_list|,
name|childName
argument_list|,
name|priority
argument_list|,
name|state
argument_list|,
name|diagnostic
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add queue, application or container activity into specific application
comment|// allocation.
DECL|method|addSchedulingActivityForApp (ApplicationId applicationId, ContainerId containerId, String priority, ActivityState state, String diagnostic, String type)
name|void
name|addSchedulingActivityForApp
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|String
name|priority
parameter_list|,
name|ActivityState
name|state
parameter_list|,
name|String
name|diagnostic
parameter_list|,
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|shouldRecordThisApp
argument_list|(
name|applicationId
argument_list|)
condition|)
block|{
name|AppAllocation
name|appAllocation
init|=
name|appsAllocation
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
name|appAllocation
operator|.
name|addAppAllocationActivity
argument_list|(
name|containerId
operator|==
literal|null
condition|?
literal|"Container-Id-Not-Assigned"
else|:
name|containerId
operator|.
name|toString
argument_list|()
argument_list|,
name|priority
argument_list|,
name|state
argument_list|,
name|diagnostic
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Update container allocation meta status for this node allocation.
comment|// It updates general container status but not the detailed activity state
comment|// in updateActivityState.
DECL|method|updateAllocationFinalState (NodeId nodeID, ContainerId containerId, AllocationState containerState)
name|void
name|updateAllocationFinalState
parameter_list|(
name|NodeId
name|nodeID
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|AllocationState
name|containerState
parameter_list|)
block|{
if|if
condition|(
name|shouldRecordThisNode
argument_list|(
name|nodeID
argument_list|)
condition|)
block|{
name|NodeAllocation
name|nodeAllocation
init|=
name|getCurrentNodeAllocation
argument_list|(
name|nodeID
argument_list|)
decl_stmt|;
name|nodeAllocation
operator|.
name|updateContainerState
argument_list|(
name|containerId
argument_list|,
name|containerState
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|finishAppAllocationRecording (ApplicationId applicationId, ContainerId containerId, ActivityState appState, String diagnostic)
name|void
name|finishAppAllocationRecording
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|ActivityState
name|appState
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
if|if
condition|(
name|shouldRecordThisApp
argument_list|(
name|applicationId
argument_list|)
condition|)
block|{
name|long
name|currTS
init|=
name|SystemClock
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|AppAllocation
name|appAllocation
init|=
name|appsAllocation
operator|.
name|remove
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
name|appAllocation
operator|.
name|updateAppContainerStateAndTime
argument_list|(
name|containerId
argument_list|,
name|appState
argument_list|,
name|currTS
argument_list|,
name|diagnostic
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AppAllocation
argument_list|>
name|appAllocations
decl_stmt|;
if|if
condition|(
name|completedAppAllocations
operator|.
name|containsKey
argument_list|(
name|applicationId
argument_list|)
condition|)
block|{
name|appAllocations
operator|=
name|completedAppAllocations
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|appAllocations
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|completedAppAllocations
operator|.
name|put
argument_list|(
name|applicationId
argument_list|,
name|appAllocations
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|appAllocations
operator|.
name|size
argument_list|()
operator|==
literal|1000
condition|)
block|{
name|appAllocations
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|appAllocations
operator|.
name|add
argument_list|(
name|appAllocation
argument_list|)
expr_stmt|;
if|if
condition|(
name|recordingAppActivitiesUntilSpecifiedTime
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
operator|<=
name|currTS
condition|)
block|{
name|turnOffActivityMonitoringForApp
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|finishNodeUpdateRecording (NodeId nodeID)
name|void
name|finishNodeUpdateRecording
parameter_list|(
name|NodeId
name|nodeID
parameter_list|)
block|{
name|List
argument_list|<
name|NodeAllocation
argument_list|>
name|value
init|=
name|recordingNodesAllocation
operator|.
name|get
argument_list|(
name|nodeID
argument_list|)
decl_stmt|;
name|long
name|timeStamp
init|=
name|SystemClock
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|lastAvailableNodeActivities
operator|=
name|value
expr_stmt|;
for|for
control|(
name|NodeAllocation
name|allocation
range|:
name|lastAvailableNodeActivities
control|)
block|{
name|allocation
operator|.
name|transformToTree
argument_list|()
expr_stmt|;
name|allocation
operator|.
name|setTimeStamp
argument_list|(
name|timeStamp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|recordNextAvailableNode
condition|)
block|{
name|recordNextAvailableNode
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|shouldRecordThisNode
argument_list|(
name|nodeID
argument_list|)
condition|)
block|{
name|recordingNodesAllocation
operator|.
name|remove
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
name|completedNodeAllocations
operator|.
name|put
argument_list|(
name|nodeID
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|stopRecordNodeUpdateActivities
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|shouldRecordThisApp (ApplicationId applicationId)
name|boolean
name|shouldRecordThisApp
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
return|return
name|recordingAppActivitiesUntilSpecifiedTime
operator|.
name|containsKey
argument_list|(
name|applicationId
argument_list|)
operator|&&
name|appsAllocation
operator|.
name|containsKey
argument_list|(
name|applicationId
argument_list|)
return|;
block|}
DECL|method|shouldRecordThisNode (NodeId nodeID)
name|boolean
name|shouldRecordThisNode
parameter_list|(
name|NodeId
name|nodeID
parameter_list|)
block|{
return|return
name|activeRecordedNodes
operator|.
name|contains
argument_list|(
name|nodeID
argument_list|)
operator|&&
name|recordingNodesAllocation
operator|.
name|containsKey
argument_list|(
name|nodeID
argument_list|)
return|;
block|}
DECL|method|getCurrentNodeAllocation (NodeId nodeID)
specifier|private
name|NodeAllocation
name|getCurrentNodeAllocation
parameter_list|(
name|NodeId
name|nodeID
parameter_list|)
block|{
name|List
argument_list|<
name|NodeAllocation
argument_list|>
name|nodeAllocations
init|=
name|recordingNodesAllocation
operator|.
name|get
argument_list|(
name|nodeID
argument_list|)
decl_stmt|;
name|NodeAllocation
name|nodeAllocation
decl_stmt|;
comment|// When this node has already stored allocation activities, get the
comment|// last allocation for this node.
if|if
condition|(
name|nodeAllocations
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|nodeAllocation
operator|=
name|nodeAllocations
operator|.
name|get
argument_list|(
name|nodeAllocations
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// When final state in last allocation is not DEFAULT, it means
comment|// last allocation has finished. Create a new allocation for this node,
comment|// and add it to the allocation list. Return this new allocation.
comment|//
comment|// When final state in last allocation is DEFAULT,
comment|// it means last allocation has not finished. Just get last allocation.
if|if
condition|(
name|nodeAllocation
operator|.
name|getFinalAllocationState
argument_list|()
operator|!=
name|AllocationState
operator|.
name|DEFAULT
condition|)
block|{
name|nodeAllocation
operator|=
operator|new
name|NodeAllocation
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
name|nodeAllocations
operator|.
name|add
argument_list|(
name|nodeAllocation
argument_list|)
expr_stmt|;
block|}
block|}
comment|// When this node has not stored allocation activities,
comment|// create a new allocation for this node, and add it to the allocation list.
comment|// Return this new allocation.
else|else
block|{
name|nodeAllocation
operator|=
operator|new
name|NodeAllocation
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
name|nodeAllocations
operator|.
name|add
argument_list|(
name|nodeAllocation
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeAllocation
return|;
block|}
DECL|method|stopRecordNodeUpdateActivities (NodeId nodeId)
specifier|private
name|void
name|stopRecordNodeUpdateActivities
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|activeRecordedNodes
operator|.
name|remove
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
DECL|method|turnOffActivityMonitoringForApp (ApplicationId applicationId)
specifier|private
name|void
name|turnOffActivityMonitoringForApp
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|recordingAppActivitiesUntilSpecifiedTime
operator|.
name|remove
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

