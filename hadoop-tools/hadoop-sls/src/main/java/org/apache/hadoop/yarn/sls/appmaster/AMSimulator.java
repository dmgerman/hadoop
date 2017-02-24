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
name|nio
operator|.
name|ByteBuffer
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
name|concurrent
operator|.
name|BlockingQueue
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
name|LinkedBlockingQueue
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
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
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
name|SubmitApplicationRequest
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
name|GetNewApplicationRequest
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
name|GetNewApplicationResponse
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
name|RegisterApplicationMasterRequest
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
name|RegisterApplicationMasterResponse
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
name|ApplicationAccessType
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
name|ApplicationSubmissionContext
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
name|ContainerLaunchContext
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
name|LocalResource
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|RMAppState
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
name|RMAppAttempt
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
name|util
operator|.
name|Records
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|scheduler
operator|.
name|SchedulerWrapper
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
name|scheduler
operator|.
name|TaskRunner
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
name|utils
operator|.
name|SLSUtils
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AMSimulator
specifier|public
specifier|abstract
class|class
name|AMSimulator
extends|extends
name|TaskRunner
operator|.
name|Task
block|{
comment|// resource manager
DECL|field|rm
specifier|protected
name|ResourceManager
name|rm
decl_stmt|;
comment|// main
DECL|field|se
specifier|protected
name|SLSRunner
name|se
decl_stmt|;
comment|// application
DECL|field|appId
specifier|protected
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|appAttemptId
specifier|protected
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
DECL|field|oldAppId
specifier|protected
name|String
name|oldAppId
decl_stmt|;
comment|// jobId from the jobhistory file
comment|// record factory
DECL|field|recordFactory
specifier|protected
specifier|final
specifier|static
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|// response queue
DECL|field|responseQueue
specifier|protected
specifier|final
name|BlockingQueue
argument_list|<
name|AllocateResponse
argument_list|>
name|responseQueue
decl_stmt|;
DECL|field|RESPONSE_ID
specifier|protected
name|int
name|RESPONSE_ID
init|=
literal|1
decl_stmt|;
comment|// user name
DECL|field|user
specifier|protected
name|String
name|user
decl_stmt|;
comment|// queue name
DECL|field|queue
specifier|protected
name|String
name|queue
decl_stmt|;
comment|// am type
DECL|field|amtype
specifier|protected
name|String
name|amtype
decl_stmt|;
comment|// job start/end time
DECL|field|traceStartTimeMS
specifier|protected
name|long
name|traceStartTimeMS
decl_stmt|;
DECL|field|traceFinishTimeMS
specifier|protected
name|long
name|traceFinishTimeMS
decl_stmt|;
DECL|field|simulateStartTimeMS
specifier|protected
name|long
name|simulateStartTimeMS
decl_stmt|;
DECL|field|simulateFinishTimeMS
specifier|protected
name|long
name|simulateFinishTimeMS
decl_stmt|;
comment|// whether tracked in Metrics
DECL|field|isTracked
specifier|protected
name|boolean
name|isTracked
decl_stmt|;
comment|// progress
DECL|field|totalContainers
specifier|protected
name|int
name|totalContainers
decl_stmt|;
DECL|field|finishedContainers
specifier|protected
name|int
name|finishedContainers
decl_stmt|;
comment|// waiting for AM container
DECL|field|isAMContainerRunning
specifier|volatile
name|boolean
name|isAMContainerRunning
init|=
literal|false
decl_stmt|;
DECL|field|amContainer
specifier|volatile
name|Container
name|amContainer
decl_stmt|;
DECL|field|LOG
specifier|protected
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AMSimulator
operator|.
name|class
argument_list|)
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
DECL|method|AMSimulator ()
specifier|public
name|AMSimulator
parameter_list|()
block|{
name|this
operator|.
name|responseQueue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|()
expr_stmt|;
block|}
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
name|traceStartTime
argument_list|,
name|traceStartTime
operator|+
literal|1000000L
operator|*
name|heartbeatInterval
argument_list|,
name|heartbeatInterval
argument_list|)
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|rm
operator|=
name|rm
expr_stmt|;
name|this
operator|.
name|se
operator|=
name|se
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|oldAppId
operator|=
name|oldAppId
expr_stmt|;
name|this
operator|.
name|isTracked
operator|=
name|isTracked
expr_stmt|;
name|this
operator|.
name|traceStartTimeMS
operator|=
name|traceStartTime
expr_stmt|;
name|this
operator|.
name|traceFinishTimeMS
operator|=
name|traceFinishTime
expr_stmt|;
block|}
comment|/**    * register with RM    */
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
name|simulateStartTimeMS
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|SLSRunner
operator|.
name|getRunner
argument_list|()
operator|.
name|getStartTimeMS
argument_list|()
expr_stmt|;
comment|// submit application, waiting until ACCEPTED
name|submitApp
argument_list|()
expr_stmt|;
comment|// track app metrics
name|trackApp
argument_list|()
expr_stmt|;
block|}
DECL|method|notifyAMContainerLaunched (Container masterContainer)
specifier|public
specifier|synchronized
name|void
name|notifyAMContainerLaunched
parameter_list|(
name|Container
name|masterContainer
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|amContainer
operator|=
name|masterContainer
expr_stmt|;
name|this
operator|.
name|appAttemptId
operator|=
name|masterContainer
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
expr_stmt|;
name|registerAM
argument_list|()
expr_stmt|;
name|isAMContainerRunning
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|middleStep ()
specifier|public
name|void
name|middleStep
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|isAMContainerRunning
condition|)
block|{
comment|// process responses in the queue
name|processResponseQueue
argument_list|()
expr_stmt|;
comment|// send out request
name|sendContainerRequest
argument_list|()
expr_stmt|;
comment|// check whether finish
name|checkStop
argument_list|()
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
name|LOG
operator|.
name|info
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Application {0} is shutting down."
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
comment|// unregister tracking
if|if
condition|(
name|isTracked
condition|)
block|{
name|untrackApp
argument_list|()
expr_stmt|;
block|}
comment|// Finish AM container
if|if
condition|(
name|amContainer
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"AM container = "
operator|+
name|amContainer
operator|.
name|getId
argument_list|()
operator|+
literal|" reported to finish"
argument_list|)
expr_stmt|;
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
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"AM container is null"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|appAttemptId
condition|)
block|{
comment|// If appAttemptId == null, AM is not launched from RM's perspective, so
comment|// it's unnecessary to finish am as well
return|return;
block|}
comment|// unregister application master
specifier|final
name|FinishApplicationMasterRequest
name|finishAMRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|finishAMRequest
operator|.
name|setFinalApplicationStatus
argument_list|(
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
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
name|appId
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
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|rm
operator|.
name|getApplicationMasterService
argument_list|()
operator|.
name|finishApplicationMaster
argument_list|(
name|finishAMRequest
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|simulateFinishTimeMS
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|SLSRunner
operator|.
name|getRunner
argument_list|()
operator|.
name|getStartTimeMS
argument_list|()
expr_stmt|;
comment|// record job running information
operator|(
operator|(
name|SchedulerWrapper
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|)
operator|.
name|addAMRuntime
argument_list|(
name|appId
argument_list|,
name|traceStartTimeMS
argument_list|,
name|traceFinishTimeMS
argument_list|,
name|simulateStartTimeMS
argument_list|,
name|simulateFinishTimeMS
argument_list|)
expr_stmt|;
block|}
DECL|method|createResourceRequest ( Resource resource, String host, int priority, int numContainers)
specifier|protected
name|ResourceRequest
name|createResourceRequest
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|numContainers
parameter_list|)
block|{
name|ResourceRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setCapability
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|request
operator|.
name|setResourceName
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNumContainers
argument_list|(
name|numContainers
argument_list|)
expr_stmt|;
name|Priority
name|prio
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|prio
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|request
operator|.
name|setPriority
argument_list|(
name|prio
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|createAllocateRequest (List<ResourceRequest> ask, List<ContainerId> toRelease)
specifier|protected
name|AllocateRequest
name|createAllocateRequest
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|toRelease
parameter_list|)
block|{
name|AllocateRequest
name|allocateRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|allocateRequest
operator|.
name|setResponseId
argument_list|(
name|RESPONSE_ID
operator|++
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setAskList
argument_list|(
name|ask
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setReleaseList
argument_list|(
name|toRelease
argument_list|)
expr_stmt|;
return|return
name|allocateRequest
return|;
block|}
DECL|method|createAllocateRequest (List<ResourceRequest> ask)
specifier|protected
name|AllocateRequest
name|createAllocateRequest
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
parameter_list|)
block|{
return|return
name|createAllocateRequest
argument_list|(
name|ask
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
DECL|method|processResponseQueue ()
specifier|protected
specifier|abstract
name|void
name|processResponseQueue
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|sendContainerRequest ()
specifier|protected
specifier|abstract
name|void
name|sendContainerRequest
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|checkStop ()
specifier|protected
specifier|abstract
name|void
name|checkStop
parameter_list|()
function_decl|;
DECL|method|submitApp ()
specifier|private
name|void
name|submitApp
parameter_list|()
throws|throws
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
comment|// ask for new application
name|GetNewApplicationRequest
name|newAppRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetNewApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|GetNewApplicationResponse
name|newAppResponse
init|=
name|rm
operator|.
name|getClientRMService
argument_list|()
operator|.
name|getNewApplication
argument_list|(
name|newAppRequest
argument_list|)
decl_stmt|;
name|appId
operator|=
name|newAppResponse
operator|.
name|getApplicationId
argument_list|()
expr_stmt|;
comment|// submit the application
specifier|final
name|SubmitApplicationRequest
name|subAppRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|SubmitApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContext
name|appSubContext
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|appSubContext
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|appSubContext
operator|.
name|setMaxAppAttempts
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|appSubContext
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|appSubContext
operator|.
name|setPriority
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerLaunchContext
name|conLauContext
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|conLauContext
operator|.
name|setApplicationACLs
argument_list|(
operator|new
name|HashMap
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|conLauContext
operator|.
name|setCommands
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|conLauContext
operator|.
name|setEnvironment
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|conLauContext
operator|.
name|setLocalResources
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|conLauContext
operator|.
name|setServiceData
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|appSubContext
operator|.
name|setAMContainerSpec
argument_list|(
name|conLauContext
argument_list|)
expr_stmt|;
name|appSubContext
operator|.
name|setResource
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
name|MR_AM_CONTAINER_RESOURCE_MEMORY_MB
argument_list|,
name|MR_AM_CONTAINER_RESOURCE_VCORES
argument_list|)
argument_list|)
expr_stmt|;
name|subAppRequest
operator|.
name|setApplicationSubmissionContext
argument_list|(
name|appSubContext
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|rm
operator|.
name|getClientRMService
argument_list|()
operator|.
name|submitApplication
argument_list|(
name|subAppRequest
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Submit a new application {0}"
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|registerAM ()
specifier|private
name|void
name|registerAM
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
comment|// register application master
specifier|final
name|RegisterApplicationMasterRequest
name|amRegisterRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|amRegisterRequest
operator|.
name|setHost
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|amRegisterRequest
operator|.
name|setRpcPort
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|amRegisterRequest
operator|.
name|setTrackingUrl
argument_list|(
literal|"localhost:1000"
argument_list|)
expr_stmt|;
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
name|appId
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
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|RegisterApplicationMasterResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RegisterApplicationMasterResponse
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
name|registerApplicationMaster
argument_list|(
name|amRegisterRequest
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Register the application master for application {0}"
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|trackApp ()
specifier|private
name|void
name|trackApp
parameter_list|()
block|{
if|if
condition|(
name|isTracked
condition|)
block|{
operator|(
operator|(
name|SchedulerWrapper
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|)
operator|.
name|addTrackedApp
argument_list|(
name|appAttemptId
argument_list|,
name|oldAppId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|untrackApp ()
specifier|public
name|void
name|untrackApp
parameter_list|()
block|{
if|if
condition|(
name|isTracked
condition|)
block|{
operator|(
operator|(
name|SchedulerWrapper
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|)
operator|.
name|removeTrackedApp
argument_list|(
name|appAttemptId
argument_list|,
name|oldAppId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|packageRequests ( List<ContainerSimulator> csList, int priority)
specifier|protected
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|packageRequests
parameter_list|(
name|List
argument_list|<
name|ContainerSimulator
argument_list|>
name|csList
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
comment|// create requests
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|rackLocalRequestMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|nodeLocalRequestMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
name|ResourceRequest
name|anyRequest
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ContainerSimulator
name|cs
range|:
name|csList
control|)
block|{
name|String
name|rackHostNames
index|[]
init|=
name|SLSUtils
operator|.
name|getRackHostName
argument_list|(
name|cs
operator|.
name|getHostname
argument_list|()
argument_list|)
decl_stmt|;
comment|// check rack local
name|String
name|rackname
init|=
literal|"/"
operator|+
name|rackHostNames
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|rackLocalRequestMap
operator|.
name|containsKey
argument_list|(
name|rackname
argument_list|)
condition|)
block|{
name|rackLocalRequestMap
operator|.
name|get
argument_list|(
name|rackname
argument_list|)
operator|.
name|setNumContainers
argument_list|(
name|rackLocalRequestMap
operator|.
name|get
argument_list|(
name|rackname
argument_list|)
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ResourceRequest
name|request
init|=
name|createResourceRequest
argument_list|(
name|cs
operator|.
name|getResource
argument_list|()
argument_list|,
name|rackname
argument_list|,
name|priority
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|rackLocalRequestMap
operator|.
name|put
argument_list|(
name|rackname
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
comment|// check node local
name|String
name|hostname
init|=
name|rackHostNames
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|nodeLocalRequestMap
operator|.
name|containsKey
argument_list|(
name|hostname
argument_list|)
condition|)
block|{
name|nodeLocalRequestMap
operator|.
name|get
argument_list|(
name|hostname
argument_list|)
operator|.
name|setNumContainers
argument_list|(
name|nodeLocalRequestMap
operator|.
name|get
argument_list|(
name|hostname
argument_list|)
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ResourceRequest
name|request
init|=
name|createResourceRequest
argument_list|(
name|cs
operator|.
name|getResource
argument_list|()
argument_list|,
name|hostname
argument_list|,
name|priority
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeLocalRequestMap
operator|.
name|put
argument_list|(
name|hostname
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
comment|// any
if|if
condition|(
name|anyRequest
operator|==
literal|null
condition|)
block|{
name|anyRequest
operator|=
name|createResourceRequest
argument_list|(
name|cs
operator|.
name|getResource
argument_list|()
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|priority
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|anyRequest
operator|.
name|setNumContainers
argument_list|(
name|anyRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
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
name|ask
operator|.
name|addAll
argument_list|(
name|nodeLocalRequestMap
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|ask
operator|.
name|addAll
argument_list|(
name|rackLocalRequestMap
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|anyRequest
operator|!=
literal|null
condition|)
block|{
name|ask
operator|.
name|add
argument_list|(
name|anyRequest
argument_list|)
expr_stmt|;
block|}
return|return
name|ask
return|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|getAMType ()
specifier|public
name|String
name|getAMType
parameter_list|()
block|{
return|return
name|amtype
return|;
block|}
DECL|method|getDuration ()
specifier|public
name|long
name|getDuration
parameter_list|()
block|{
return|return
name|simulateFinishTimeMS
operator|-
name|simulateStartTimeMS
return|;
block|}
DECL|method|getNumTasks ()
specifier|public
name|int
name|getNumTasks
parameter_list|()
block|{
return|return
name|totalContainers
return|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|appAttemptId
return|;
block|}
block|}
end_class

end_unit

