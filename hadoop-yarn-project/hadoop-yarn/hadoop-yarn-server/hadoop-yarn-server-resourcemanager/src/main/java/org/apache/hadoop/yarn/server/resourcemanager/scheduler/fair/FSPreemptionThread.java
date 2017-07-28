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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
DECL|field|delayBeforeNextStarvationCheck
specifier|private
specifier|final
name|long
name|delayBeforeNextStarvationCheck
decl_stmt|;
DECL|field|preemptionTimer
specifier|private
specifier|final
name|Timer
name|preemptionTimer
decl_stmt|;
DECL|field|schedulerReadLock
specifier|private
specifier|final
name|Lock
name|schedulerReadLock
decl_stmt|;
DECL|method|FSPreemptionThread (FairScheduler scheduler)
name|FSPreemptionThread
parameter_list|(
name|FairScheduler
name|scheduler
parameter_list|)
block|{
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
name|warnTimeBeforeKill
operator|=
name|fsConf
operator|.
name|getWaitTimeBeforeKill
argument_list|()
expr_stmt|;
name|long
name|allocDelay
init|=
operator|(
name|fsConf
operator|.
name|isContinuousSchedulingEnabled
argument_list|()
condition|?
literal|10
operator|*
name|fsConf
operator|.
name|getContinuousSchedulingSleepMs
argument_list|()
comment|// 10 runs
else|:
literal|4
operator|*
name|scheduler
operator|.
name|getNMHeartbeatInterval
argument_list|()
operator|)
decl_stmt|;
comment|// 4 heartbeats
name|delayBeforeNextStarvationCheck
operator|=
name|warnTimeBeforeKill
operator|+
name|allocDelay
operator|+
name|fsConf
operator|.
name|getWaitTimeBeforeNextStarvationCheck
argument_list|()
expr_stmt|;
name|schedulerReadLock
operator|=
name|scheduler
operator|.
name|getSchedulerReadLock
argument_list|()
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
while|while
condition|(
operator|!
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
block|{
try|try
block|{
name|FSAppAttempt
name|starvedApp
init|=
name|context
operator|.
name|getStarvedApps
argument_list|()
operator|.
name|take
argument_list|()
decl_stmt|;
comment|// Hold the scheduler readlock so this is not concurrent with the
comment|// update thread.
name|schedulerReadLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|preemptContainers
argument_list|(
name|identifyContainersToPreempt
argument_list|(
name|starvedApp
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|schedulerReadLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|starvedApp
operator|.
name|preemptionTriggered
argument_list|(
name|delayBeforeNextStarvationCheck
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
literal|"Preemption thread interrupted! Exiting."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Given an app, identify containers to preempt to satisfy the app's    * starvation.    *    * Mechanics:    * 1. Fetch all {@link ResourceRequest}s corresponding to the amount of    * starvation.    * 2. For each {@link ResourceRequest}, iterate through matching    * nodes and identify containers to preempt all on one node, also    * optimizing for least number of AM container preemptions.    *    * @param starvedApp starved application for which we are identifying    *                   preemption targets    * @return list of containers to preempt to satisfy starvedApp    */
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
name|containersToPreempt
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Iterate through enough RRs to address app's starvation
for|for
control|(
name|ResourceRequest
name|rr
range|:
name|starvedApp
operator|.
name|getStarvedResourceRequests
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rr
operator|.
name|getNumContainers
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PreemptableContainers
name|bestContainers
init|=
literal|null
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
name|rr
operator|.
name|getResourceName
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|maxAMContainers
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|FSSchedulerNode
name|node
range|:
name|potentialNodes
control|)
block|{
name|PreemptableContainers
name|preemptableContainers
init|=
name|identifyContainersToPreemptOnNode
argument_list|(
name|rr
operator|.
name|getCapability
argument_list|()
argument_list|,
name|node
argument_list|,
name|maxAMContainers
argument_list|)
decl_stmt|;
if|if
condition|(
name|preemptableContainers
operator|!=
literal|null
condition|)
block|{
comment|// This set is better than any previously identified set.
name|bestContainers
operator|=
name|preemptableContainers
expr_stmt|;
name|maxAMContainers
operator|=
name|bestContainers
operator|.
name|numAMContainers
expr_stmt|;
if|if
condition|(
name|maxAMContainers
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
block|}
comment|// End of iteration through nodes for one RR
if|if
condition|(
name|bestContainers
operator|!=
literal|null
operator|&&
name|bestContainers
operator|.
name|containers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|containersToPreempt
operator|.
name|addAll
argument_list|(
name|bestContainers
operator|.
name|containers
argument_list|)
expr_stmt|;
comment|// Reserve the containers for the starved app
name|trackPreemptionsAgainstNode
argument_list|(
name|bestContainers
operator|.
name|containers
argument_list|,
name|starvedApp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// End of iteration over RRs
return|return
name|containersToPreempt
return|;
block|}
comment|/**    * Identify containers to preempt on a given node. Try to find a list with    * least AM containers to avoid preempting AM containers. This method returns    * a non-null set of containers only if the number of AM containers is less    * than maxAMContainers.    *    * @param request resource requested    * @param node the node to check    * @param maxAMContainers max allowed AM containers in the set    * @return list of preemptable containers with fewer AM containers than    *         maxAMContainers if such a list exists; null otherwise.    */
DECL|method|identifyContainersToPreemptOnNode ( Resource request, FSSchedulerNode node, int maxAMContainers)
specifier|private
name|PreemptableContainers
name|identifyContainersToPreemptOnNode
parameter_list|(
name|Resource
name|request
parameter_list|,
name|FSSchedulerNode
name|node
parameter_list|,
name|int
name|maxAMContainers
parameter_list|)
block|{
name|PreemptableContainers
name|preemptableContainers
init|=
operator|new
name|PreemptableContainers
argument_list|(
name|maxAMContainers
argument_list|)
decl_stmt|;
comment|// Figure out list of containers to consider
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containersToCheck
init|=
name|node
operator|.
name|getRunningContainersWithAMsAtTheEnd
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
comment|// Initialize potential with unallocated but not reserved resources
name|Resource
name|potential
init|=
name|Resources
operator|.
name|subtractFromNonNegative
argument_list|(
name|Resources
operator|.
name|clone
argument_list|(
name|node
operator|.
name|getUnallocatedResource
argument_list|()
argument_list|)
argument_list|,
name|node
operator|.
name|getTotalReserved
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
if|if
condition|(
operator|!
name|preemptableContainers
operator|.
name|addContainer
argument_list|(
name|container
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
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
name|request
argument_list|,
name|potential
argument_list|)
condition|)
block|{
return|return
name|preemptableContainers
return|;
block|}
block|}
comment|// Return null if the sum of all preemptable containers' resources
comment|// isn't enough to satisfy the starved request.
return|return
literal|null
return|;
block|}
DECL|method|trackPreemptionsAgainstNode (List<RMContainer> containers, FSAppAttempt app)
specifier|private
name|void
name|trackPreemptionsAgainstNode
parameter_list|(
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containers
parameter_list|,
name|FSAppAttempt
name|app
parameter_list|)
block|{
name|FSSchedulerNode
name|node
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
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|.
name|addContainersForPreemption
argument_list|(
name|containers
argument_list|,
name|app
argument_list|)
expr_stmt|;
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
name|app
operator|.
name|getQueueName
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
specifier|final
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
block|}
block|}
block|}
comment|/**    * A class to track preemptable containers.    */
DECL|class|PreemptableContainers
specifier|private
specifier|static
class|class
name|PreemptableContainers
block|{
DECL|field|containers
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containers
decl_stmt|;
DECL|field|numAMContainers
name|int
name|numAMContainers
decl_stmt|;
DECL|field|maxAMContainers
name|int
name|maxAMContainers
decl_stmt|;
DECL|method|PreemptableContainers (int maxAMContainers)
name|PreemptableContainers
parameter_list|(
name|int
name|maxAMContainers
parameter_list|)
block|{
name|containers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|numAMContainers
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|maxAMContainers
operator|=
name|maxAMContainers
expr_stmt|;
block|}
comment|/**      * Add a container if the number of AM containers is less than      * maxAMContainers.      *      * @param container the container to add      * @return true if success; false otherwise      */
DECL|method|addContainer (RMContainer container)
specifier|private
name|boolean
name|addContainer
parameter_list|(
name|RMContainer
name|container
parameter_list|)
block|{
if|if
condition|(
name|container
operator|.
name|isAMContainer
argument_list|()
condition|)
block|{
name|numAMContainers
operator|++
expr_stmt|;
if|if
condition|(
name|numAMContainers
operator|>=
name|maxAMContainers
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|containers
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

