begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|fair
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
name|rmcontainer
operator|.
name|RMContainerEventType
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
name|SchedulerUtils
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import

begin_comment
comment|/**  * Thread that handles FairScheduler preemption.  */
end_comment

begin_class
DECL|class|FSPreemptionThread
class|class
name|FSPreemptionThread
extends|extends
name|Thread
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
name|FSPreemptionThread
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|context
specifier|protected
specifier|final
name|FSContext
name|context
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|FairScheduler
name|scheduler
decl_stmt|;
DECL|field|warnTimeBeforeKill
specifier|private
specifier|final
name|long
name|warnTimeBeforeKill
decl_stmt|;
DECL|field|preemptionTimer
specifier|private
specifier|final
name|Timer
name|preemptionTimer
decl_stmt|;
DECL|method|FSPreemptionThread (FairScheduler scheduler)
name|FSPreemptionThread
parameter_list|(
name|FairScheduler
name|scheduler
parameter_list|)
block|{
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|scheduler
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|FairSchedulerConfiguration
name|fsConf
init|=
name|scheduler
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|context
operator|.
name|setPreemptionEnabled
argument_list|()
expr_stmt|;
name|context
operator|.
name|setPreemptionUtilizationThreshold
argument_list|(
name|fsConf
operator|.
name|getPreemptionUtilizationThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|warnTimeBeforeKill
operator|=
name|fsConf
operator|.
name|getWaitTimeBeforeKill
argument_list|()
expr_stmt|;
name|preemptionTimer
operator|=
operator|new
name|Timer
argument_list|(
literal|"Preemption Timer"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setName
argument_list|(
literal|"FSPreemptionThread"
argument_list|)
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
block|{
name|FSAppAttempt
name|starvedApp
decl_stmt|;
try|try
block|{
name|starvedApp
operator|=
name|context
operator|.
name|getStarvedApps
argument_list|()
operator|.
name|take
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|Resources
operator|.
name|isNone
argument_list|(
name|starvedApp
operator|.
name|getStarvation
argument_list|()
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containers
init|=
name|identifyContainersToPreempt
argument_list|(
name|starvedApp
argument_list|)
decl_stmt|;
if|if
condition|(
name|containers
operator|!=
literal|null
condition|)
block|{
name|preemptContainers
argument_list|(
name|containers
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Preemption thread interrupted! Exiting."
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
comment|/**    * Given an app, identify containers to preempt to satisfy the app's next    * resource request.    *    * @param starvedApp starved application for which we are identifying    *                   preemption targets    * @return list of containers to preempt to satisfy starvedApp, null if the    * app cannot be satisfied by preempting any running containers    */
DECL|method|identifyContainersToPreempt ( FSAppAttempt starvedApp)
specifier|private
name|List
argument_list|<
name|RMContainer
argument_list|>
name|identifyContainersToPreempt
parameter_list|(
name|FSAppAttempt
name|starvedApp
parameter_list|)
block|{
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// return value
comment|// Find the nodes that match the next resource request
name|ResourceRequest
name|request
init|=
name|starvedApp
operator|.
name|getNextResourceRequest
argument_list|()
decl_stmt|;
comment|// TODO (KK): Should we check other resource requests if we can't match
comment|// the first one?
name|Resource
name|requestCapability
init|=
name|request
operator|.
name|getCapability
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FSSchedulerNode
argument_list|>
name|potentialNodes
init|=
name|scheduler
operator|.
name|getNodeTracker
argument_list|()
operator|.
name|getNodesByResourceName
argument_list|(
name|request
operator|.
name|getResourceName
argument_list|()
argument_list|)
decl_stmt|;
comment|// From the potential nodes, pick a node that has enough containers
comment|// from apps over their fairshare
for|for
control|(
name|FSSchedulerNode
name|node
range|:
name|potentialNodes
control|)
block|{
comment|// Reset containers for the new node being considered.
name|containers
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// TODO (YARN-5829): Attempt to reserve the node for starved app. The
comment|// subsequent if-check needs to be reworked accordingly.
name|FSAppAttempt
name|nodeReservedApp
init|=
name|node
operator|.
name|getReservedAppSchedulable
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeReservedApp
operator|!=
literal|null
operator|&&
operator|!
name|nodeReservedApp
operator|.
name|equals
argument_list|(
name|starvedApp
argument_list|)
condition|)
block|{
comment|// This node is already reserved by another app. Let us not consider
comment|// this for preemption.
continue|continue;
block|}
comment|// Figure out list of containers to consider
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containersToCheck
init|=
name|node
operator|.
name|getCopiedListOfRunningContainers
argument_list|()
decl_stmt|;
name|containersToCheck
operator|.
name|removeAll
argument_list|(
name|node
operator|.
name|getContainersForPreemption
argument_list|()
argument_list|)
expr_stmt|;
comment|// Initialize potential with unallocated resources
name|Resource
name|potential
init|=
name|Resources
operator|.
name|clone
argument_list|(
name|node
operator|.
name|getUnallocatedResource
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RMContainer
name|container
range|:
name|containersToCheck
control|)
block|{
name|FSAppAttempt
name|app
init|=
name|scheduler
operator|.
name|getSchedulerApp
argument_list|(
name|container
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|app
operator|.
name|canContainerBePreempted
argument_list|(
name|container
argument_list|)
condition|)
block|{
comment|// Flag container for preemption
name|containers
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|potential
argument_list|,
name|container
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check if we have already identified enough containers
if|if
condition|(
name|Resources
operator|.
name|fitsIn
argument_list|(
name|requestCapability
argument_list|,
name|potential
argument_list|)
condition|)
block|{
comment|// Mark the containers as being considered for preemption on the node.
comment|// Make sure the containers are subsequently removed by calling
comment|// FSSchedulerNode#removeContainerForPreemption.
name|node
operator|.
name|addContainersForPreemption
argument_list|(
name|containers
argument_list|)
expr_stmt|;
return|return
name|containers
return|;
block|}
else|else
block|{
comment|// TODO (YARN-5829): Unreserve the node for the starved app.
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|preemptContainers (List<RMContainer> containers)
specifier|private
name|void
name|preemptContainers
parameter_list|(
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containers
parameter_list|)
block|{
comment|// Warn application about containers to be killed
for|for
control|(
name|RMContainer
name|container
range|:
name|containers
control|)
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|container
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
name|FSAppAttempt
name|app
init|=
name|scheduler
operator|.
name|getSchedulerApp
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|FSLeafQueue
name|queue
init|=
name|app
operator|.
name|getQueue
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Preempting container "
operator|+
name|container
operator|+
literal|" from queue "
operator|+
name|queue
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|app
operator|.
name|trackContainerForPreemption
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
comment|// Schedule timer task to kill containers
name|preemptionTimer
operator|.
name|schedule
argument_list|(
operator|new
name|PreemptContainersTask
argument_list|(
name|containers
argument_list|)
argument_list|,
name|warnTimeBeforeKill
argument_list|)
expr_stmt|;
block|}
DECL|class|PreemptContainersTask
specifier|private
class|class
name|PreemptContainersTask
extends|extends
name|TimerTask
block|{
DECL|field|containers
specifier|private
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containers
decl_stmt|;
DECL|method|PreemptContainersTask (List<RMContainer> containers)
name|PreemptContainersTask
parameter_list|(
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containers
parameter_list|)
block|{
name|this
operator|.
name|containers
operator|=
name|containers
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|RMContainer
name|container
range|:
name|containers
control|)
block|{
name|ContainerStatus
name|status
init|=
name|SchedulerUtils
operator|.
name|createPreemptedContainerStatus
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|SchedulerUtils
operator|.
name|PREEMPTED_CONTAINER
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Killing container "
operator|+
name|container
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|completedContainer
argument_list|(
name|container
argument_list|,
name|status
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|)
expr_stmt|;
name|FSSchedulerNode
name|containerNode
init|=
operator|(
name|FSSchedulerNode
operator|)
name|scheduler
operator|.
name|getNodeTracker
argument_list|()
operator|.
name|getNode
argument_list|(
name|container
operator|.
name|getAllocatedNode
argument_list|()
argument_list|)
decl_stmt|;
name|containerNode
operator|.
name|removeContainerForPreemption
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

