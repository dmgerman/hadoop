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
name|MockNodes
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
name|RMAppEvent
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
name|RMAppEventType
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
name|RMAppImpl
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
name|RMAppAttemptImpl
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
name|RMAppAttemptMetrics
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
name|ResourceScheduler
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
name|AppAddedSchedulerEvent
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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

begin_class
DECL|class|FairSchedulerTestBase
specifier|public
class|class
name|FairSchedulerTestBase
block|{
DECL|field|TEST_DIR
specifier|public
specifier|final
specifier|static
name|String
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|RecordFactory
DECL|field|recordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|APP_ID
specifier|protected
name|int
name|APP_ID
init|=
literal|1
decl_stmt|;
comment|// Incrementing counter for scheduling apps
DECL|field|ATTEMPT_ID
specifier|protected
name|int
name|ATTEMPT_ID
init|=
literal|1
decl_stmt|;
comment|// Incrementing counter for scheduling attempts
DECL|field|conf
specifier|protected
name|Configuration
name|conf
decl_stmt|;
DECL|field|scheduler
specifier|protected
name|FairScheduler
name|scheduler
decl_stmt|;
DECL|field|resourceManager
specifier|protected
name|ResourceManager
name|resourceManager
decl_stmt|;
DECL|field|TEST_RESERVATION_THRESHOLD
specifier|public
specifier|static
specifier|final
name|float
name|TEST_RESERVATION_THRESHOLD
init|=
literal|0.09f
decl_stmt|;
DECL|field|SLEEP_DURATION
specifier|private
specifier|static
specifier|final
name|int
name|SLEEP_DURATION
init|=
literal|10
decl_stmt|;
DECL|field|SLEEP_RETRIES
specifier|private
specifier|static
specifier|final
name|int
name|SLEEP_RETRIES
init|=
literal|1000
decl_stmt|;
DECL|field|NULL_UPDATE_REQUESTS
specifier|final
specifier|static
name|ContainerUpdates
name|NULL_UPDATE_REQUESTS
init|=
operator|new
name|ContainerUpdates
argument_list|()
decl_stmt|;
comment|/**    * The list of nodes added to the cluster using the {@link #addNode} method.    */
DECL|field|rmNodes
specifier|protected
specifier|final
name|List
argument_list|<
name|RMNode
argument_list|>
name|rmNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Helper methods
DECL|method|createConfiguration ()
specifier|public
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FairScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|RM_SCHEDULER_INCREMENT_ALLOCATION_MB
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
literal|10240
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|ASSIGN_MULTIPLE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|UPDATE_INTERVAL_MS
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|PREEMPTION_THRESHOLD
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|RM_SCHEDULER_RESERVATION_THRESHOLD_INCREMENT_MULTIPLE
argument_list|,
name|TEST_RESERVATION_THRESHOLD
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|createAppAttemptId (int appId, int attemptId)
specifier|protected
name|ApplicationAttemptId
name|createAppAttemptId
parameter_list|(
name|int
name|appId
parameter_list|,
name|int
name|attemptId
parameter_list|)
block|{
name|ApplicationId
name|appIdImpl
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
name|appId
argument_list|)
decl_stmt|;
return|return
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appIdImpl
argument_list|,
name|attemptId
argument_list|)
return|;
block|}
DECL|method|createResourceRequest ( int memory, String host, int priority, int numContainers, boolean relaxLocality)
specifier|protected
name|ResourceRequest
name|createResourceRequest
parameter_list|(
name|int
name|memory
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|boolean
name|relaxLocality
parameter_list|)
block|{
return|return
name|createResourceRequest
argument_list|(
name|memory
argument_list|,
literal|1
argument_list|,
name|host
argument_list|,
name|priority
argument_list|,
name|numContainers
argument_list|,
name|relaxLocality
argument_list|)
return|;
block|}
DECL|method|createResourceRequest ( int memory, int vcores, String host, int priority, int numContainers, boolean relaxLocality)
specifier|protected
name|ResourceRequest
name|createResourceRequest
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|vcores
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|boolean
name|relaxLocality
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
name|BuilderUtils
operator|.
name|newResource
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|)
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
name|request
operator|.
name|setRelaxLocality
argument_list|(
name|relaxLocality
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNodeLabelExpression
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Creates a single container priority-1 request and submits to    * scheduler.    */
DECL|method|createSchedulingRequest ( int memory, String queueId, String userId)
specifier|protected
name|ApplicationAttemptId
name|createSchedulingRequest
parameter_list|(
name|int
name|memory
parameter_list|,
name|String
name|queueId
parameter_list|,
name|String
name|userId
parameter_list|)
block|{
return|return
name|createSchedulingRequest
argument_list|(
name|memory
argument_list|,
name|queueId
argument_list|,
name|userId
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|createSchedulingRequest ( int memory, int vcores, String queueId, String userId)
specifier|protected
name|ApplicationAttemptId
name|createSchedulingRequest
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|vcores
parameter_list|,
name|String
name|queueId
parameter_list|,
name|String
name|userId
parameter_list|)
block|{
return|return
name|createSchedulingRequest
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|,
name|queueId
argument_list|,
name|userId
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|createSchedulingRequest ( int memory, String queueId, String userId, int numContainers)
specifier|protected
name|ApplicationAttemptId
name|createSchedulingRequest
parameter_list|(
name|int
name|memory
parameter_list|,
name|String
name|queueId
parameter_list|,
name|String
name|userId
parameter_list|,
name|int
name|numContainers
parameter_list|)
block|{
return|return
name|createSchedulingRequest
argument_list|(
name|memory
argument_list|,
name|queueId
argument_list|,
name|userId
argument_list|,
name|numContainers
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|createSchedulingRequest ( int memory, int vcores, String queueId, String userId, int numContainers)
specifier|protected
name|ApplicationAttemptId
name|createSchedulingRequest
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|vcores
parameter_list|,
name|String
name|queueId
parameter_list|,
name|String
name|userId
parameter_list|,
name|int
name|numContainers
parameter_list|)
block|{
return|return
name|createSchedulingRequest
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|,
name|queueId
argument_list|,
name|userId
argument_list|,
name|numContainers
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|createSchedulingRequest ( int memory, String queueId, String userId, int numContainers, int priority)
specifier|protected
name|ApplicationAttemptId
name|createSchedulingRequest
parameter_list|(
name|int
name|memory
parameter_list|,
name|String
name|queueId
parameter_list|,
name|String
name|userId
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
return|return
name|createSchedulingRequest
argument_list|(
name|memory
argument_list|,
literal|1
argument_list|,
name|queueId
argument_list|,
name|userId
argument_list|,
name|numContainers
argument_list|,
name|priority
argument_list|)
return|;
block|}
DECL|method|createSchedulingRequest ( int memory, int vcores, String queueId, String userId, int numContainers, int priority)
specifier|protected
name|ApplicationAttemptId
name|createSchedulingRequest
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|vcores
parameter_list|,
name|String
name|queueId
parameter_list|,
name|String
name|userId
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
name|ApplicationAttemptId
name|id
init|=
name|createAppAttemptId
argument_list|(
name|this
operator|.
name|APP_ID
operator|++
argument_list|,
name|this
operator|.
name|ATTEMPT_ID
operator|++
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|addApplication
argument_list|(
name|id
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|queueId
argument_list|,
name|userId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// This conditional is for testAclSubmitApplication where app is rejected
comment|// and no app is added.
if|if
condition|(
name|scheduler
operator|.
name|getSchedulerApplications
argument_list|()
operator|.
name|containsKey
argument_list|(
name|id
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
name|scheduler
operator|.
name|addApplicationAttempt
argument_list|(
name|id
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
name|ResourceRequest
name|request
init|=
name|createResourceRequest
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|priority
argument_list|,
name|numContainers
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ask
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|RMApp
name|rmApp
init|=
name|mock
argument_list|(
name|RMApp
operator|.
name|class
argument_list|)
decl_stmt|;
name|RMAppAttempt
name|rmAppAttempt
init|=
name|mock
argument_list|(
name|RMAppAttempt
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rmApp
operator|.
name|getCurrentAppAttempt
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rmAppAttempt
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmAppAttempt
operator|.
name|getRMAppAttemptMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|RMAppAttemptMetrics
argument_list|(
name|id
argument_list|,
name|resourceManager
operator|.
name|getRMContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ApplicationSubmissionContext
name|submissionContext
init|=
name|mock
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|submissionContext
operator|.
name|getUnmanagedAM
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmAppAttempt
operator|.
name|getSubmissionContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|submissionContext
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmApp
operator|.
name|getApplicationSubmissionContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|submissionContext
argument_list|)
expr_stmt|;
name|Container
name|container
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rmAppAttempt
operator|.
name|getMasterContainer
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|put
argument_list|(
name|id
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|rmApp
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|allocate
argument_list|(
name|id
argument_list|,
name|ask
argument_list|,
literal|null
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|NULL_UPDATE_REQUESTS
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|createSchedulingRequest (String queueId, String userId, List<ResourceRequest> ask)
specifier|protected
name|ApplicationAttemptId
name|createSchedulingRequest
parameter_list|(
name|String
name|queueId
parameter_list|,
name|String
name|userId
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
parameter_list|)
block|{
name|ApplicationAttemptId
name|id
init|=
name|createAppAttemptId
argument_list|(
name|this
operator|.
name|APP_ID
operator|++
argument_list|,
name|this
operator|.
name|ATTEMPT_ID
operator|++
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|addApplication
argument_list|(
name|id
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|queueId
argument_list|,
name|userId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// This conditional is for testAclSubmitApplication where app is rejected
comment|// and no app is added.
if|if
condition|(
name|scheduler
operator|.
name|getSchedulerApplications
argument_list|()
operator|.
name|containsKey
argument_list|(
name|id
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
name|scheduler
operator|.
name|addApplicationAttempt
argument_list|(
name|id
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|RMApp
name|rmApp
init|=
name|mock
argument_list|(
name|RMApp
operator|.
name|class
argument_list|)
decl_stmt|;
name|RMAppAttempt
name|rmAppAttempt
init|=
name|mock
argument_list|(
name|RMAppAttempt
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rmApp
operator|.
name|getCurrentAppAttempt
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rmAppAttempt
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmAppAttempt
operator|.
name|getRMAppAttemptMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|RMAppAttemptMetrics
argument_list|(
name|id
argument_list|,
name|resourceManager
operator|.
name|getRMContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ApplicationSubmissionContext
name|submissionContext
init|=
name|mock
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|submissionContext
operator|.
name|getUnmanagedAM
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmAppAttempt
operator|.
name|getSubmissionContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|submissionContext
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmApp
operator|.
name|getApplicationSubmissionContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|submissionContext
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|put
argument_list|(
name|id
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|rmApp
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|allocate
argument_list|(
name|id
argument_list|,
name|ask
argument_list|,
literal|null
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|NULL_UPDATE_REQUESTS
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|createSchedulingRequestExistingApplication ( int memory, int priority, ApplicationAttemptId attId)
specifier|protected
name|void
name|createSchedulingRequestExistingApplication
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|priority
parameter_list|,
name|ApplicationAttemptId
name|attId
parameter_list|)
block|{
name|ResourceRequest
name|request
init|=
name|createResourceRequest
argument_list|(
name|memory
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|priority
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|createSchedulingRequestExistingApplication
argument_list|(
name|request
argument_list|,
name|attId
argument_list|)
expr_stmt|;
block|}
DECL|method|createSchedulingRequestExistingApplication ( int memory, int vcores, int priority, ApplicationAttemptId attId)
specifier|protected
name|void
name|createSchedulingRequestExistingApplication
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|vcores
parameter_list|,
name|int
name|priority
parameter_list|,
name|ApplicationAttemptId
name|attId
parameter_list|)
block|{
name|ResourceRequest
name|request
init|=
name|createResourceRequest
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|priority
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|createSchedulingRequestExistingApplication
argument_list|(
name|request
argument_list|,
name|attId
argument_list|)
expr_stmt|;
block|}
DECL|method|createSchedulingRequestExistingApplication ( ResourceRequest request, ApplicationAttemptId attId)
specifier|protected
name|void
name|createSchedulingRequestExistingApplication
parameter_list|(
name|ResourceRequest
name|request
parameter_list|,
name|ApplicationAttemptId
name|attId
parameter_list|)
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
name|ask
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|allocate
argument_list|(
name|attId
argument_list|,
name|ask
argument_list|,
literal|null
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|NULL_UPDATE_REQUESTS
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
block|}
DECL|method|createApplicationWithAMResource (ApplicationAttemptId attId, String queue, String user, Resource amResource)
specifier|protected
name|void
name|createApplicationWithAMResource
parameter_list|(
name|ApplicationAttemptId
name|attId
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|user
parameter_list|,
name|Resource
name|amResource
parameter_list|)
block|{
name|RMContext
name|rmContext
init|=
name|resourceManager
operator|.
name|getRMContext
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|attId
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|RMApp
name|rmApp
init|=
operator|new
name|RMAppImpl
argument_list|(
name|appId
argument_list|,
name|rmContext
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
name|user
argument_list|,
literal|null
argument_list|,
name|ApplicationSubmissionContext
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|null
argument_list|,
name|queue
argument_list|,
literal|null
argument_list|,
name|mock
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|amResource
argument_list|,
literal|null
argument_list|)
argument_list|,
name|scheduler
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|rmApp
argument_list|)
expr_stmt|;
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|START
argument_list|)
decl_stmt|;
name|resourceManager
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
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|APP_NEW_SAVED
argument_list|)
expr_stmt|;
name|resourceManager
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
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|APP_ACCEPTED
argument_list|)
expr_stmt|;
name|resourceManager
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
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|AppAddedSchedulerEvent
name|appAddedEvent
init|=
operator|new
name|AppAddedSchedulerEvent
argument_list|(
name|appId
argument_list|,
name|queue
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|handle
argument_list|(
name|appAddedEvent
argument_list|)
expr_stmt|;
name|AppAttemptAddedSchedulerEvent
name|attempAddedEvent
init|=
operator|new
name|AppAttemptAddedSchedulerEvent
argument_list|(
name|attId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|handle
argument_list|(
name|attempAddedEvent
argument_list|)
expr_stmt|;
block|}
DECL|method|createMockRMApp (ApplicationAttemptId attemptId)
specifier|protected
name|RMApp
name|createMockRMApp
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|)
block|{
name|RMApp
name|app
init|=
name|mock
argument_list|(
name|RMAppImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|RMAppAttemptImpl
name|attempt
init|=
name|mock
argument_list|(
name|RMAppAttemptImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|RMAppAttemptMetrics
name|attemptMetric
init|=
name|mock
argument_list|(
name|RMAppAttemptMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|attempt
operator|.
name|getRMAppAttemptMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptMetric
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attempt
argument_list|)
expr_stmt|;
name|ApplicationSubmissionContext
name|submissionContext
init|=
name|mock
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|submissionContext
operator|.
name|getUnmanagedAM
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|attempt
operator|.
name|getSubmissionContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|submissionContext
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getApplicationSubmissionContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|submissionContext
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|put
argument_list|(
name|attemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|app
argument_list|)
expr_stmt|;
return|return
name|app
return|;
block|}
DECL|method|checkAppConsumption (FSAppAttempt app, Resource resource)
specifier|protected
name|void
name|checkAppConsumption
parameter_list|(
name|FSAppAttempt
name|app
parameter_list|,
name|Resource
name|resource
parameter_list|)
throws|throws
name|InterruptedException
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
name|SLEEP_RETRIES
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Resources
operator|.
name|equals
argument_list|(
name|resource
argument_list|,
name|app
operator|.
name|getCurrentConsumption
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|SLEEP_DURATION
argument_list|)
expr_stmt|;
block|}
block|}
comment|// available resource
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resource
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|app
operator|.
name|getCurrentConsumption
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resource
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|app
operator|.
name|getCurrentConsumption
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a node to the cluster and track the nodes in {@link #rmNodes}.    * @param memory memory capacity of the node    * @param cores cpu capacity of the node    */
DECL|method|addNode (int memory, int cores)
specifier|protected
name|void
name|addNode
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|cores
parameter_list|)
block|{
name|int
name|id
init|=
name|rmNodes
operator|.
name|size
argument_list|()
operator|+
literal|1
decl_stmt|;
name|RMNode
name|node
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|1
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
name|memory
argument_list|,
name|cores
argument_list|)
argument_list|,
name|id
argument_list|,
literal|"127.0.0."
operator|+
name|id
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|handle
argument_list|(
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|rmNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

