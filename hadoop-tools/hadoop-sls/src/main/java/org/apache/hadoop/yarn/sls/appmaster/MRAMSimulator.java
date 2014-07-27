begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.appmaster
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
name|appmaster
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
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|security
operator|.
name|AMRMTokenIdentifier
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
name|utils
operator|.
name|BuilderUtils
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
name|scheduler
operator|.
name|ContainerSimulator
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
name|log4j
operator|.
name|Logger
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|MRAMSimulator
specifier|public
class|class
name|MRAMSimulator
extends|extends
name|AMSimulator
block|{
comment|/*   Vocabulary Used:    pending -> requests which are NOT yet sent to RM   scheduled -> requests which are sent to RM but not yet assigned   assigned -> requests which are assigned to a container   completed -> request corresponding to which container has completed      Maps are scheduled as soon as their requests are received. Reduces are   scheduled when all maps have finished (not support slow-start currently).   */
DECL|field|PRIORITY_REDUCE
specifier|private
specifier|static
specifier|final
name|int
name|PRIORITY_REDUCE
init|=
literal|10
decl_stmt|;
DECL|field|PRIORITY_MAP
specifier|private
specifier|static
specifier|final
name|int
name|PRIORITY_MAP
init|=
literal|20
decl_stmt|;
comment|// pending maps
DECL|field|pendingMaps
specifier|private
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
name|pendingMaps
init|=
operator|new
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
comment|// pending failed maps
DECL|field|pendingFailedMaps
specifier|private
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
name|pendingFailedMaps
init|=
operator|new
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
comment|// scheduled maps
DECL|field|scheduledMaps
specifier|private
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
name|scheduledMaps
init|=
operator|new
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
comment|// assigned maps
DECL|field|assignedMaps
specifier|private
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerSimulator
argument_list|>
name|assignedMaps
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
comment|// reduces which are not yet scheduled
DECL|field|pendingReduces
specifier|private
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
name|pendingReduces
init|=
operator|new
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
comment|// pending failed reduces
DECL|field|pendingFailedReduces
specifier|private
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
name|pendingFailedReduces
init|=
operator|new
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
comment|// scheduled reduces
DECL|field|scheduledReduces
specifier|private
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
name|scheduledReduces
init|=
operator|new
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
comment|// assigned reduces
DECL|field|assignedReduces
specifier|private
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerSimulator
argument_list|>
name|assignedReduces
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
comment|// all maps& reduces
DECL|field|allMaps
specifier|private
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
name|allMaps
init|=
operator|new
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allReduces
specifier|private
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
name|allReduces
init|=
operator|new
name|LinkedList
argument_list|<
name|ContainerSimulator
argument_list|>
argument_list|()
decl_stmt|;
comment|// counters
DECL|field|mapFinished
specifier|private
name|int
name|mapFinished
init|=
literal|0
decl_stmt|;
DECL|field|mapTotal
specifier|private
name|int
name|mapTotal
init|=
literal|0
decl_stmt|;
DECL|field|reduceFinished
specifier|private
name|int
name|reduceFinished
init|=
literal|0
decl_stmt|;
DECL|field|reduceTotal
specifier|private
name|int
name|reduceTotal
init|=
literal|0
decl_stmt|;
comment|// waiting for AM container
DECL|field|isAMContainerRunning
specifier|private
name|boolean
name|isAMContainerRunning
init|=
literal|false
decl_stmt|;
DECL|field|amContainer
specifier|private
name|Container
name|amContainer
decl_stmt|;
comment|// finished
DECL|field|isFinished
specifier|private
name|boolean
name|isFinished
init|=
literal|false
decl_stmt|;
comment|// resource for AM container
DECL|field|MR_AM_CONTAINER_RESOURCE_MEMORY_MB
specifier|private
specifier|final
specifier|static
name|int
name|MR_AM_CONTAINER_RESOURCE_MEMORY_MB
init|=
literal|1024
decl_stmt|;
DECL|field|MR_AM_CONTAINER_RESOURCE_VCORES
specifier|private
specifier|final
specifier|static
name|int
name|MR_AM_CONTAINER_RESOURCE_VCORES
init|=
literal|1
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|MRAMSimulator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|init (int id, int heartbeatInterval, List<ContainerSimulator> containerList, ResourceManager rm, SLSRunner se, long traceStartTime, long traceFinishTime, String user, String queue, boolean isTracked, String oldAppId)
specifier|public
name|void
name|init
parameter_list|(
name|int
name|id
parameter_list|,
name|int
name|heartbeatInterval
parameter_list|,
name|List
argument_list|<
name|ContainerSimulator
argument_list|>
name|containerList
parameter_list|,
name|ResourceManager
name|rm
parameter_list|,
name|SLSRunner
name|se
parameter_list|,
name|long
name|traceStartTime
parameter_list|,
name|long
name|traceFinishTime
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|boolean
name|isTracked
parameter_list|,
name|String
name|oldAppId
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|id
argument_list|,
name|heartbeatInterval
argument_list|,
name|containerList
argument_list|,
name|rm
argument_list|,
name|se
argument_list|,
name|traceStartTime
argument_list|,
name|traceFinishTime
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
name|isTracked
argument_list|,
name|oldAppId
argument_list|)
expr_stmt|;
name|amtype
operator|=
literal|"mapreduce"
expr_stmt|;
comment|// get map/reduce tasks
for|for
control|(
name|ContainerSimulator
name|cs
range|:
name|containerList
control|)
block|{
if|if
condition|(
name|cs
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"map"
argument_list|)
condition|)
block|{
name|cs
operator|.
name|setPriority
argument_list|(
name|PRIORITY_MAP
argument_list|)
expr_stmt|;
name|pendingMaps
operator|.
name|add
argument_list|(
name|cs
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cs
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"reduce"
argument_list|)
condition|)
block|{
name|cs
operator|.
name|setPriority
argument_list|(
name|PRIORITY_REDUCE
argument_list|)
expr_stmt|;
name|pendingReduces
operator|.
name|add
argument_list|(
name|cs
argument_list|)
expr_stmt|;
block|}
block|}
name|allMaps
operator|.
name|addAll
argument_list|(
name|pendingMaps
argument_list|)
expr_stmt|;
name|allReduces
operator|.
name|addAll
argument_list|(
name|pendingReduces
argument_list|)
expr_stmt|;
name|mapTotal
operator|=
name|pendingMaps
operator|.
name|size
argument_list|()
expr_stmt|;
name|reduceTotal
operator|=
name|pendingReduces
operator|.
name|size
argument_list|()
expr_stmt|;
name|totalContainers
operator|=
name|mapTotal
operator|+
name|reduceTotal
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|firstStep ()
specifier|public
name|void
name|firstStep
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|firstStep
argument_list|()
expr_stmt|;
name|requestAMContainer
argument_list|()
expr_stmt|;
block|}
comment|/**    * send out request for AM container    */
DECL|method|requestAMContainer ()
specifier|protected
name|void
name|requestAMContainer
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
name|ResourceRequest
name|amRequest
init|=
name|createResourceRequest
argument_list|(
name|BuilderUtils
operator|.
name|newResource
argument_list|(
name|MR_AM_CONTAINER_RESOURCE_MEMORY_MB
argument_list|,
name|MR_AM_CONTAINER_RESOURCE_VCORES
argument_list|)
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ask
operator|.
name|add
argument_list|(
name|amRequest
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} sends out allocate "
operator|+
literal|"request for its AM"
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AllocateRequest
name|request
init|=
name|this
operator|.
name|createAllocateRequest
argument_list|(
name|ask
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|token
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|getRMAppAttempt
argument_list|(
name|appAttemptId
argument_list|)
operator|.
name|getAMRMToken
argument_list|()
decl_stmt|;
name|ugi
operator|.
name|addTokenIdentifier
argument_list|(
name|token
operator|.
name|decodeIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|AllocateResponse
name|response
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|AllocateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AllocateResponse
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|rm
operator|.
name|getApplicationMasterService
argument_list|()
operator|.
name|allocate
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// waiting until the AM container is allocated
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|response
operator|!=
literal|null
operator|&&
operator|!
name|response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// get AM container
name|Container
name|container
init|=
name|response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|se
operator|.
name|getNmMap
argument_list|()
operator|.
name|get
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|addNewContainer
argument_list|(
name|container
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
comment|// start AM container
name|amContainer
operator|=
name|container
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} starts its "
operator|+
literal|"AM container ({1})."
argument_list|,
name|appId
argument_list|,
name|amContainer
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|isAMContainerRunning
operator|=
literal|true
expr_stmt|;
break|break;
block|}
comment|// this sleep time is different from HeartBeat
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// send out empty request
name|sendContainerRequest
argument_list|()
expr_stmt|;
name|response
operator|=
name|responseQueue
operator|.
name|take
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|processResponseQueue ()
specifier|protected
name|void
name|processResponseQueue
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|YarnException
throws|,
name|IOException
block|{
while|while
condition|(
operator|!
name|responseQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|AllocateResponse
name|response
init|=
name|responseQueue
operator|.
name|take
argument_list|()
decl_stmt|;
comment|// check completed containers
if|if
condition|(
operator|!
name|response
operator|.
name|getCompletedContainersStatuses
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|ContainerStatus
name|cs
range|:
name|response
operator|.
name|getCompletedContainersStatuses
argument_list|()
control|)
block|{
name|ContainerId
name|containerId
init|=
name|cs
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|cs
operator|.
name|getExitStatus
argument_list|()
operator|==
name|ContainerExitStatus
operator|.
name|SUCCESS
condition|)
block|{
if|if
condition|(
name|assignedMaps
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} has one"
operator|+
literal|"mapper finished ({1})."
argument_list|,
name|appId
argument_list|,
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
name|assignedMaps
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|mapFinished
operator|++
expr_stmt|;
name|finishedContainers
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|assignedReduces
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} has one"
operator|+
literal|"reducer finished ({1})."
argument_list|,
name|appId
argument_list|,
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
name|assignedReduces
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|reduceFinished
operator|++
expr_stmt|;
name|finishedContainers
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// am container released event
name|isFinished
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} goes to "
operator|+
literal|"finish."
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// container to be killed
if|if
condition|(
name|assignedMaps
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} has one "
operator|+
literal|"mapper killed ({1})."
argument_list|,
name|appId
argument_list|,
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
name|pendingFailedMaps
operator|.
name|add
argument_list|(
name|assignedMaps
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|assignedReduces
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} has one "
operator|+
literal|"reducer killed ({1})."
argument_list|,
name|appId
argument_list|,
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
name|pendingFailedReduces
operator|.
name|add
argument_list|(
name|assignedReduces
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0}'s AM is "
operator|+
literal|"going to be killed. Restarting..."
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|restart
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// check finished
if|if
condition|(
name|isAMContainerRunning
operator|&&
operator|(
name|mapFinished
operator|==
name|mapTotal
operator|)
operator|&&
operator|(
name|reduceFinished
operator|==
name|reduceTotal
operator|)
condition|)
block|{
comment|// to release the AM container
name|se
operator|.
name|getNmMap
argument_list|()
operator|.
name|get
argument_list|(
name|amContainer
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|cleanupContainer
argument_list|(
name|amContainer
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|isAMContainerRunning
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} sends out event "
operator|+
literal|"to clean up its AM container."
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|isFinished
operator|=
literal|true
expr_stmt|;
block|}
comment|// check allocated containers
for|for
control|(
name|Container
name|container
range|:
name|response
operator|.
name|getAllocatedContainers
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|scheduledMaps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ContainerSimulator
name|cs
init|=
name|scheduledMaps
operator|.
name|remove
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} starts a "
operator|+
literal|"launch a mapper ({1})."
argument_list|,
name|appId
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assignedMaps
operator|.
name|put
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|cs
argument_list|)
expr_stmt|;
name|se
operator|.
name|getNmMap
argument_list|()
operator|.
name|get
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|addNewContainer
argument_list|(
name|container
argument_list|,
name|cs
operator|.
name|getLifeTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|this
operator|.
name|scheduledReduces
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ContainerSimulator
name|cs
init|=
name|scheduledReduces
operator|.
name|remove
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} starts a "
operator|+
literal|"launch a reducer ({1})."
argument_list|,
name|appId
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assignedReduces
operator|.
name|put
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|cs
argument_list|)
expr_stmt|;
name|se
operator|.
name|getNmMap
argument_list|()
operator|.
name|get
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|addNewContainer
argument_list|(
name|container
argument_list|,
name|cs
operator|.
name|getLifeTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * restart running because of the am container killed    */
DECL|method|restart ()
specifier|private
name|void
name|restart
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
comment|// clear
name|finishedContainers
operator|=
literal|0
expr_stmt|;
name|isFinished
operator|=
literal|false
expr_stmt|;
name|mapFinished
operator|=
literal|0
expr_stmt|;
name|reduceFinished
operator|=
literal|0
expr_stmt|;
name|pendingFailedMaps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pendingMaps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pendingReduces
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pendingFailedReduces
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pendingMaps
operator|.
name|addAll
argument_list|(
name|allMaps
argument_list|)
expr_stmt|;
name|pendingReduces
operator|.
name|addAll
argument_list|(
name|pendingReduces
argument_list|)
expr_stmt|;
name|isAMContainerRunning
operator|=
literal|false
expr_stmt|;
name|amContainer
operator|=
literal|null
expr_stmt|;
comment|// resent am container request
name|requestAMContainer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendContainerRequest ()
specifier|protected
name|void
name|sendContainerRequest
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|isFinished
condition|)
block|{
return|return;
block|}
comment|// send out request
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isAMContainerRunning
condition|)
block|{
if|if
condition|(
name|mapFinished
operator|!=
name|mapTotal
condition|)
block|{
comment|// map phase
if|if
condition|(
operator|!
name|pendingMaps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ask
operator|=
name|packageRequests
argument_list|(
name|pendingMaps
argument_list|,
name|PRIORITY_MAP
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} sends out "
operator|+
literal|"request for {1} mappers."
argument_list|,
name|appId
argument_list|,
name|pendingMaps
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|scheduledMaps
operator|.
name|addAll
argument_list|(
name|pendingMaps
argument_list|)
expr_stmt|;
name|pendingMaps
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|pendingFailedMaps
operator|.
name|isEmpty
argument_list|()
operator|&&
name|scheduledMaps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ask
operator|=
name|packageRequests
argument_list|(
name|pendingFailedMaps
argument_list|,
name|PRIORITY_MAP
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} sends out "
operator|+
literal|"requests for {1} failed mappers."
argument_list|,
name|appId
argument_list|,
name|pendingFailedMaps
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|scheduledMaps
operator|.
name|addAll
argument_list|(
name|pendingFailedMaps
argument_list|)
expr_stmt|;
name|pendingFailedMaps
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|reduceFinished
operator|!=
name|reduceTotal
condition|)
block|{
comment|// reduce phase
if|if
condition|(
operator|!
name|pendingReduces
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ask
operator|=
name|packageRequests
argument_list|(
name|pendingReduces
argument_list|,
name|PRIORITY_REDUCE
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} sends out "
operator|+
literal|"requests for {1} reducers."
argument_list|,
name|appId
argument_list|,
name|pendingReduces
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|scheduledReduces
operator|.
name|addAll
argument_list|(
name|pendingReduces
argument_list|)
expr_stmt|;
name|pendingReduces
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|pendingFailedReduces
operator|.
name|isEmpty
argument_list|()
operator|&&
name|scheduledReduces
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ask
operator|=
name|packageRequests
argument_list|(
name|pendingFailedReduces
argument_list|,
name|PRIORITY_REDUCE
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} sends out "
operator|+
literal|"request for {1} failed reducers."
argument_list|,
name|appId
argument_list|,
name|pendingFailedReduces
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|scheduledReduces
operator|.
name|addAll
argument_list|(
name|pendingFailedReduces
argument_list|)
expr_stmt|;
name|pendingFailedReduces
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|ask
operator|==
literal|null
condition|)
block|{
name|ask
operator|=
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|final
name|AllocateRequest
name|request
init|=
name|createAllocateRequest
argument_list|(
name|ask
argument_list|)
decl_stmt|;
if|if
condition|(
name|totalContainers
operator|==
literal|0
condition|)
block|{
name|request
operator|.
name|setProgress
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|request
operator|.
name|setProgress
argument_list|(
operator|(
name|float
operator|)
name|finishedContainers
operator|/
name|totalContainers
argument_list|)
expr_stmt|;
block|}
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|token
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|getRMAppAttempt
argument_list|(
name|appAttemptId
argument_list|)
operator|.
name|getAMRMToken
argument_list|()
decl_stmt|;
name|ugi
operator|.
name|addTokenIdentifier
argument_list|(
name|token
operator|.
name|decodeIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|AllocateResponse
name|response
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|AllocateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AllocateResponse
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|rm
operator|.
name|getApplicationMasterService
argument_list|()
operator|.
name|allocate
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
name|responseQueue
operator|.
name|put
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkStop ()
specifier|protected
name|void
name|checkStop
parameter_list|()
block|{
if|if
condition|(
name|isFinished
condition|)
block|{
name|super
operator|.
name|setEndTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|lastStep ()
specifier|public
name|void
name|lastStep
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|lastStep
argument_list|()
expr_stmt|;
comment|// clear data structures
name|allMaps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|allReduces
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assignedMaps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assignedReduces
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pendingFailedMaps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pendingFailedReduces
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pendingMaps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pendingReduces
operator|.
name|clear
argument_list|()
expr_stmt|;
name|scheduledMaps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|scheduledReduces
operator|.
name|clear
argument_list|()
expr_stmt|;
name|responseQueue
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

