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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|scheduler
operator|.
name|common
operator|.
name|fica
operator|.
name|FiCaSchedulerNode
import|;
end_import

begin_comment
comment|/**  * Utility for logging scheduler activities  */
end_comment

begin_comment
comment|// FIXME: make sure CandidateNodeSet works with this class
end_comment

begin_class
DECL|class|ActivitiesLogger
specifier|public
class|class
name|ActivitiesLogger
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ActivitiesLogger
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Methods for recording activities from an app    */
DECL|class|APP
specifier|public
specifier|static
class|class
name|APP
block|{
comment|/*      * Record skipped application activity when no container allocated /      * reserved / re-reserved. Scheduler will look at following applications      * within the same leaf queue.      */
DECL|method|recordSkippedAppActivityWithoutAllocation ( ActivitiesManager activitiesManager, SchedulerNode node, SchedulerApplicationAttempt application, Priority priority, String diagnostic)
specifier|public
specifier|static
name|void
name|recordSkippedAppActivityWithoutAllocation
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|SchedulerApplicationAttempt
name|application
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
name|recordAppActivityWithoutAllocation
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
name|application
argument_list|,
name|priority
argument_list|,
name|diagnostic
argument_list|,
name|ActivityState
operator|.
name|SKIPPED
argument_list|)
expr_stmt|;
block|}
comment|/*      * Record application activity when rejected because of queue maximum      * capacity or user limit.      */
DECL|method|recordRejectedAppActivityFromLeafQueue ( ActivitiesManager activitiesManager, SchedulerNode node, SchedulerApplicationAttempt application, Priority priority, String diagnostic)
specifier|public
specifier|static
name|void
name|recordRejectedAppActivityFromLeafQueue
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|SchedulerApplicationAttempt
name|application
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
name|String
name|type
init|=
literal|"app"
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
operator|||
name|activitiesManager
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|activitiesManager
operator|.
name|shouldRecordThisNode
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|recordActivity
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
name|application
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|priority
argument_list|,
name|ActivityState
operator|.
name|REJECTED
argument_list|,
name|diagnostic
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
name|finishSkippedAppAllocationRecording
argument_list|(
name|activitiesManager
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|ActivityState
operator|.
name|REJECTED
argument_list|,
name|diagnostic
argument_list|)
expr_stmt|;
block|}
comment|/*      * Record application activity when no container allocated /      * reserved / re-reserved. Scheduler will look at following applications      * within the same leaf queue.      */
DECL|method|recordAppActivityWithoutAllocation ( ActivitiesManager activitiesManager, SchedulerNode node, SchedulerApplicationAttempt application, Priority priority, String diagnostic, ActivityState appState)
specifier|public
specifier|static
name|void
name|recordAppActivityWithoutAllocation
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|SchedulerApplicationAttempt
name|application
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|String
name|diagnostic
parameter_list|,
name|ActivityState
name|appState
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
operator|||
name|activitiesManager
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|activitiesManager
operator|.
name|shouldRecordThisNode
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|type
init|=
literal|"container"
decl_stmt|;
comment|// Add application-container activity into specific node allocation.
name|activitiesManager
operator|.
name|addSchedulingActivityForNode
argument_list|(
name|node
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
name|priority
operator|.
name|toString
argument_list|()
argument_list|,
name|ActivityState
operator|.
name|SKIPPED
argument_list|,
name|diagnostic
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|type
operator|=
literal|"app"
expr_stmt|;
comment|// Add queue-application activity into specific node allocation.
name|activitiesManager
operator|.
name|addSchedulingActivityForNode
argument_list|(
name|node
argument_list|,
name|application
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|application
operator|.
name|getPriority
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ActivityState
operator|.
name|SKIPPED
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|EMPTY
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|// Add application-container activity into specific application allocation
comment|// Under this condition, it fails to allocate a container to this
comment|// application, so containerId is null.
if|if
condition|(
name|activitiesManager
operator|.
name|shouldRecordThisApp
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|type
init|=
literal|"container"
decl_stmt|;
name|activitiesManager
operator|.
name|addSchedulingActivityForApp
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
literal|null
argument_list|,
name|priority
operator|.
name|toString
argument_list|()
argument_list|,
name|appState
argument_list|,
name|diagnostic
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Record application activity when container allocated / reserved /      * re-reserved      */
DECL|method|recordAppActivityWithAllocation ( ActivitiesManager activitiesManager, SchedulerNode node, SchedulerApplicationAttempt application, RMContainer updatedContainer, ActivityState activityState)
specifier|public
specifier|static
name|void
name|recordAppActivityWithAllocation
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|SchedulerApplicationAttempt
name|application
parameter_list|,
name|RMContainer
name|updatedContainer
parameter_list|,
name|ActivityState
name|activityState
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
operator|||
name|activitiesManager
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|activitiesManager
operator|.
name|shouldRecordThisNode
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|type
init|=
literal|"container"
decl_stmt|;
comment|// Add application-container activity into specific node allocation.
name|activitiesManager
operator|.
name|addSchedulingActivityForNode
argument_list|(
name|node
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|updatedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|updatedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getPriority
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|activityState
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|EMPTY
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|type
operator|=
literal|"app"
expr_stmt|;
comment|// Add queue-application activity into specific node allocation.
name|activitiesManager
operator|.
name|addSchedulingActivityForNode
argument_list|(
name|node
argument_list|,
name|application
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|application
operator|.
name|getPriority
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ActivityState
operator|.
name|ACCEPTED
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|EMPTY
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|// Add application-container activity into specific application allocation
if|if
condition|(
name|activitiesManager
operator|.
name|shouldRecordThisApp
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|type
init|=
literal|"container"
decl_stmt|;
name|activitiesManager
operator|.
name|addSchedulingActivityForApp
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|updatedContainer
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|updatedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getPriority
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|activityState
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|EMPTY
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Invoked when scheduler starts to look at this application within one node      * update.      */
DECL|method|startAppAllocationRecording ( ActivitiesManager activitiesManager, FiCaSchedulerNode node, long currentTime, SchedulerApplicationAttempt application)
specifier|public
specifier|static
name|void
name|startAppAllocationRecording
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|,
name|long
name|currentTime
parameter_list|,
name|SchedulerApplicationAttempt
name|application
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
operator|||
name|activitiesManager
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|activitiesManager
operator|.
name|startAppAllocationRecording
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|currentTime
argument_list|,
name|application
argument_list|)
expr_stmt|;
block|}
comment|/*      * Invoked when scheduler finishes looking at this application within one      * node update, and the app has any container allocated/reserved during      * this allocation.      */
DECL|method|finishAllocatedAppAllocationRecording ( ActivitiesManager activitiesManager, ApplicationId applicationId, ContainerId containerId, ActivityState containerState, String diagnostic)
specifier|public
specifier|static
name|void
name|finishAllocatedAppAllocationRecording
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|ActivityState
name|containerState
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
if|if
condition|(
name|activitiesManager
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|activitiesManager
operator|.
name|shouldRecordThisApp
argument_list|(
name|applicationId
argument_list|)
condition|)
block|{
name|activitiesManager
operator|.
name|finishAppAllocationRecording
argument_list|(
name|applicationId
argument_list|,
name|containerId
argument_list|,
name|containerState
argument_list|,
name|diagnostic
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Invoked when scheduler finishes looking at this application within one      * node update, and the app DOESN'T have any container allocated/reserved      * during this allocation.      */
DECL|method|finishSkippedAppAllocationRecording ( ActivitiesManager activitiesManager, ApplicationId applicationId, ActivityState containerState, String diagnostic)
specifier|public
specifier|static
name|void
name|finishSkippedAppAllocationRecording
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|ActivityState
name|containerState
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
name|finishAllocatedAppAllocationRecording
argument_list|(
name|activitiesManager
argument_list|,
name|applicationId
argument_list|,
literal|null
argument_list|,
name|containerState
argument_list|,
name|diagnostic
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Methods for recording activities from a queue    */
DECL|class|QUEUE
specifier|public
specifier|static
class|class
name|QUEUE
block|{
comment|/*      * Record activities of a queue      */
DECL|method|recordQueueActivity (ActivitiesManager activitiesManager, SchedulerNode node, String parentQueueName, String queueName, ActivityState state, String diagnostic)
specifier|public
specifier|static
name|void
name|recordQueueActivity
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|String
name|parentQueueName
parameter_list|,
name|String
name|queueName
parameter_list|,
name|ActivityState
name|state
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
operator|||
name|activitiesManager
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|activitiesManager
operator|.
name|shouldRecordThisNode
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|recordActivity
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
name|parentQueueName
argument_list|,
name|queueName
argument_list|,
literal|null
argument_list|,
name|state
argument_list|,
name|diagnostic
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Methods for recording overall activities from one node update    */
DECL|class|NODE
specifier|public
specifier|static
class|class
name|NODE
block|{
comment|/*      * Invoked when node allocation finishes, and there's NO container      * allocated or reserved during the allocation      */
DECL|method|finishSkippedNodeAllocation ( ActivitiesManager activitiesManager, SchedulerNode node)
specifier|public
specifier|static
name|void
name|finishSkippedNodeAllocation
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|SchedulerNode
name|node
parameter_list|)
block|{
name|finishAllocatedNodeAllocation
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
literal|null
argument_list|,
name|AllocationState
operator|.
name|SKIPPED
argument_list|)
expr_stmt|;
block|}
comment|/*      * Invoked when node allocation finishes, and there's any container      * allocated or reserved during the allocation      */
DECL|method|finishAllocatedNodeAllocation ( ActivitiesManager activitiesManager, SchedulerNode node, ContainerId containerId, AllocationState containerState)
specifier|public
specifier|static
name|void
name|finishAllocatedNodeAllocation
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|SchedulerNode
name|node
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
name|node
operator|==
literal|null
operator|||
name|activitiesManager
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|activitiesManager
operator|.
name|shouldRecordThisNode
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|activitiesManager
operator|.
name|updateAllocationFinalState
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|containerId
argument_list|,
name|containerState
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Invoked when node heartbeat finishes      */
DECL|method|finishNodeUpdateRecording ( ActivitiesManager activitiesManager, NodeId nodeID)
specifier|public
specifier|static
name|void
name|finishNodeUpdateRecording
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|NodeId
name|nodeID
parameter_list|)
block|{
if|if
condition|(
name|activitiesManager
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|activitiesManager
operator|.
name|finishNodeUpdateRecording
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
block|}
comment|/*      * Invoked when node heartbeat starts      */
DECL|method|startNodeUpdateRecording ( ActivitiesManager activitiesManager, NodeId nodeID)
specifier|public
specifier|static
name|void
name|startNodeUpdateRecording
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|NodeId
name|nodeID
parameter_list|)
block|{
if|if
condition|(
name|activitiesManager
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|activitiesManager
operator|.
name|startNodeUpdateRecording
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add queue, application or container activity into specific node allocation.
DECL|method|recordActivity (ActivitiesManager activitiesManager, SchedulerNode node, String parentName, String childName, Priority priority, ActivityState state, String diagnostic, String type)
specifier|private
specifier|static
name|void
name|recordActivity
parameter_list|(
name|ActivitiesManager
name|activitiesManager
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|String
name|parentName
parameter_list|,
name|String
name|childName
parameter_list|,
name|Priority
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
name|activitiesManager
operator|.
name|addSchedulingActivityForNode
argument_list|(
name|node
argument_list|,
name|parentName
argument_list|,
name|childName
argument_list|,
name|priority
operator|!=
literal|null
condition|?
name|priority
operator|.
name|toString
argument_list|()
else|:
literal|null
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
end_class

end_unit

