begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|Arrays
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
name|ContainerState
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
name|event
operator|.
name|Dispatcher
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
name|DrainDispatcher
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
name|EventHandler
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
name|api
operator|.
name|protocolrecords
operator|.
name|NMContainerStatus
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatResponse
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
name|recovery
operator|.
name|MemoryRMStateStore
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
name|log4j
operator|.
name|Level
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
name|LogManager
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
name|Before
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

begin_class
DECL|class|TestApplicationCleanup
specifier|public
class|class
name|TestApplicationCleanup
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
name|TestApplicationCleanup
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|UnknownHostException
block|{
name|Logger
name|rootLogger
init|=
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RECOVERY_ENABLED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_STORE
argument_list|,
name|MemoryRMStateStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
annotation|@
name|Test
DECL|method|testAppCleanup ()
specifier|public
name|void
name|testAppCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|Logger
name|rootLogger
init|=
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|()
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|RMApp
name|app
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
comment|//kick the scheduling
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|MockAM
name|am
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|//request for containers
name|int
name|request
init|=
literal|2
decl_stmt|;
name|am
operator|.
name|allocate
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|1000
argument_list|,
name|request
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
comment|//kick the scheduler
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|conts
init|=
name|am
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|int
name|contReceived
init|=
name|conts
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|contReceived
operator|<
name|request
operator|&&
name|waitCount
operator|++
operator|<
literal|200
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got "
operator|+
name|contReceived
operator|+
literal|" containers. Waiting to get "
operator|+
name|request
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|conts
operator|=
name|am
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
expr_stmt|;
name|contReceived
operator|+=
name|conts
operator|.
name|size
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|request
argument_list|,
name|contReceived
argument_list|)
expr_stmt|;
name|am
operator|.
name|unregisterAppAttempt
argument_list|()
expr_stmt|;
name|NodeHeartbeatResponse
name|resp
init|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
decl_stmt|;
name|am
operator|.
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
comment|//currently only containers are cleaned via this
comment|//AM container is cleaned via container launcher
name|resp
operator|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containersToCleanup
init|=
name|resp
operator|.
name|getContainersToCleanup
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|appsToCleanup
init|=
name|resp
operator|.
name|getApplicationsToCleanup
argument_list|()
decl_stmt|;
name|int
name|numCleanedContainers
init|=
name|containersToCleanup
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|numCleanedApps
init|=
name|appsToCleanup
operator|.
name|size
argument_list|()
decl_stmt|;
name|waitCount
operator|=
literal|0
expr_stmt|;
while|while
condition|(
operator|(
name|numCleanedContainers
operator|<
literal|2
operator|||
name|numCleanedApps
operator|<
literal|1
operator|)
operator|&&
name|waitCount
operator|++
operator|<
literal|200
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting to get cleanup events.. cleanedConts: "
operator|+
name|numCleanedContainers
operator|+
literal|" cleanedApps: "
operator|+
name|numCleanedApps
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|resp
operator|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|deltaContainersToCleanup
init|=
name|resp
operator|.
name|getContainersToCleanup
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|deltaAppsToCleanup
init|=
name|resp
operator|.
name|getApplicationsToCleanup
argument_list|()
decl_stmt|;
comment|// Add the deltas to the global list
name|containersToCleanup
operator|.
name|addAll
argument_list|(
name|deltaContainersToCleanup
argument_list|)
expr_stmt|;
name|appsToCleanup
operator|.
name|addAll
argument_list|(
name|deltaAppsToCleanup
argument_list|)
expr_stmt|;
comment|// Update counts now
name|numCleanedContainers
operator|=
name|containersToCleanup
operator|.
name|size
argument_list|()
expr_stmt|;
name|numCleanedApps
operator|=
name|appsToCleanup
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appsToCleanup
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|appsToCleanup
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|numCleanedApps
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|numCleanedContainers
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
annotation|@
name|Test
DECL|method|testContainerCleanup ()
specifier|public
name|void
name|testContainerCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|Logger
name|rootLogger
init|=
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
specifier|final
name|DrainDispatcher
name|dispatcher
init|=
operator|new
name|DrainDispatcher
argument_list|()
decl_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|EventHandler
argument_list|<
name|SchedulerEvent
argument_list|>
name|createSchedulerEventDispatcher
parameter_list|()
block|{
return|return
operator|new
name|SchedulerEventDispatcher
argument_list|(
name|this
operator|.
name|scheduler
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|SchedulerEvent
name|event
parameter_list|)
block|{
name|scheduler
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Dispatcher
name|createDispatcher
parameter_list|()
block|{
return|return
name|dispatcher
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
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|RMApp
name|app
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
comment|//kick the scheduling
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|MockAM
name|am
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|//request for containers
name|int
name|request
init|=
literal|2
decl_stmt|;
name|am
operator|.
name|allocate
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|1000
argument_list|,
name|request
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
comment|//kick the scheduler
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|conts
init|=
name|am
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|int
name|contReceived
init|=
name|conts
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|contReceived
operator|<
name|request
operator|&&
name|waitCount
operator|++
operator|<
literal|200
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got "
operator|+
name|contReceived
operator|+
literal|" containers. Waiting to get "
operator|+
name|request
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|conts
operator|=
name|am
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|contReceived
operator|+=
name|conts
operator|.
name|size
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|request
argument_list|,
name|contReceived
argument_list|)
expr_stmt|;
comment|// Release a container.
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
name|release
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|release
operator|.
name|add
argument_list|(
name|conts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|am
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
name|release
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
comment|// Send one more heartbeat with a fake running container. This is to
comment|// simulate the situation that can happen if the NM reports that container
comment|// is running in the same heartbeat when the RM asks it to clean it up.
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
name|containerStatuses
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
name|containerStatusList
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
decl_stmt|;
name|containerStatusList
operator|.
name|add
argument_list|(
name|BuilderUtils
operator|.
name|newContainerStatus
argument_list|(
name|conts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|"nothing"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|containerStatuses
operator|.
name|put
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|containerStatusList
argument_list|)
expr_stmt|;
name|NodeHeartbeatResponse
name|resp
init|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|containerStatuses
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|contsToClean
init|=
name|resp
operator|.
name|getContainersToCleanup
argument_list|()
decl_stmt|;
name|int
name|cleanedConts
init|=
name|contsToClean
operator|.
name|size
argument_list|()
decl_stmt|;
name|waitCount
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|cleanedConts
operator|<
literal|1
operator|&&
name|waitCount
operator|++
operator|<
literal|200
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting to get cleanup events.. cleanedConts: "
operator|+
name|cleanedConts
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|resp
operator|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|contsToClean
operator|=
name|resp
operator|.
name|getContainersToCleanup
argument_list|()
expr_stmt|;
name|cleanedConts
operator|+=
name|contsToClean
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Got cleanup for "
operator|+
name|contsToClean
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cleanedConts
argument_list|)
expr_stmt|;
comment|// Now to test the case when RM already gave cleanup, and NM suddenly
comment|// realizes that the container is running.
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing container launch much after release and "
operator|+
literal|"NM getting cleanup"
argument_list|)
expr_stmt|;
name|containerStatuses
operator|.
name|clear
argument_list|()
expr_stmt|;
name|containerStatusList
operator|.
name|clear
argument_list|()
expr_stmt|;
name|containerStatusList
operator|.
name|add
argument_list|(
name|BuilderUtils
operator|.
name|newContainerStatus
argument_list|(
name|conts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|"nothing"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|containerStatuses
operator|.
name|put
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|containerStatusList
argument_list|)
expr_stmt|;
name|resp
operator|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|containerStatuses
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|contsToClean
operator|=
name|resp
operator|.
name|getContainersToCleanup
argument_list|()
expr_stmt|;
name|cleanedConts
operator|=
name|contsToClean
operator|.
name|size
argument_list|()
expr_stmt|;
comment|// The cleanup list won't be instantaneous as it is given out by scheduler
comment|// and not RMNodeImpl.
name|waitCount
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|cleanedConts
operator|<
literal|1
operator|&&
name|waitCount
operator|++
operator|<
literal|200
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting to get cleanup events.. cleanedConts: "
operator|+
name|cleanedConts
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|resp
operator|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|contsToClean
operator|=
name|resp
operator|.
name|getContainersToCleanup
argument_list|()
expr_stmt|;
name|cleanedConts
operator|+=
name|contsToClean
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Got cleanup for "
operator|+
name|contsToClean
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cleanedConts
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|waitForAppCleanupMessageRecved (MockNM nm, ApplicationId appId)
specifier|private
name|void
name|waitForAppCleanupMessageRecved
parameter_list|(
name|MockNM
name|nm
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|Exception
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|NodeHeartbeatResponse
name|response
init|=
name|nm
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getApplicationsToCleanup
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getApplicationsToCleanup
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|appId
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getApplicationsToCleanup
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Haven't got application="
operator|+
name|appId
operator|.
name|toString
argument_list|()
operator|+
literal|" in cleanup list from node heartbeat response, "
operator|+
literal|"sleep for a while before next heartbeat"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|launchAM (RMApp app, MockRM rm, MockNM nm)
specifier|private
name|MockAM
name|launchAM
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|MockRM
name|rm
parameter_list|,
name|MockNM
name|nm
parameter_list|)
throws|throws
name|Exception
block|{
name|RMAppAttempt
name|attempt
init|=
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|nm
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MockAM
name|am
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|rm
operator|.
name|waitForState
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
return|return
name|am
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAppCleanupWhenRMRestartedAfterAppFinished ()
specifier|public
name|void
name|testAppCleanupWhenRMRestartedAfterAppFinished
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|MemoryRMStateStore
name|memStore
init|=
operator|new
name|MemoryRMStateStore
argument_list|()
decl_stmt|;
name|memStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// start RM
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
operator|new
name|MockNM
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|15120
argument_list|,
name|rm1
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
comment|// create app and launch the AM
name|RMApp
name|app0
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|MockAM
name|am0
init|=
name|launchAM
argument_list|(
name|app0
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|am0
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|)
expr_stmt|;
comment|// start new RM
name|MockRM
name|rm2
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
decl_stmt|;
name|rm2
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// nm1 register to rm2, and do a heartbeat
name|nm1
operator|.
name|setResourceTrackerService
argument_list|(
name|rm2
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
expr_stmt|;
name|nm1
operator|.
name|registerNode
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rm2
operator|.
name|waitForState
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|)
expr_stmt|;
comment|// wait for application cleanup message received
name|waitForAppCleanupMessageRecved
argument_list|(
name|nm1
argument_list|,
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|rm2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAppCleanupWhenRMRestartedBeforeAppFinished ()
specifier|public
name|void
name|testAppCleanupWhenRMRestartedBeforeAppFinished
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|MemoryRMStateStore
name|memStore
init|=
operator|new
name|MemoryRMStateStore
argument_list|()
decl_stmt|;
name|memStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// start RM
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
operator|new
name|MockNM
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|1024
argument_list|,
name|rm1
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
operator|new
name|MockNM
argument_list|(
literal|"127.0.0.1:5678"
argument_list|,
literal|1024
argument_list|,
name|rm1
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
comment|// create app and launch the AM
name|RMApp
name|app0
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|MockAM
name|am0
init|=
name|launchAM
argument_list|(
name|app0
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
comment|// alloc another container on nm2
name|AllocateResponse
name|allocResponse
init|=
name|am0
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
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
literal|null
operator|==
name|allocResponse
operator|.
name|getAllocatedContainers
argument_list|()
operator|||
name|allocResponse
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|allocResponse
operator|=
name|am0
operator|.
name|allocate
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|// start new RM
name|MockRM
name|rm2
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
decl_stmt|;
name|rm2
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// nm1/nm2 register to rm2, and do a heartbeat
name|nm1
operator|.
name|setResourceTrackerService
argument_list|(
name|rm2
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
expr_stmt|;
name|nm1
operator|.
name|registerNode
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|NMContainerStatus
operator|.
name|newInstance
argument_list|(
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|am0
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
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
literal|""
argument_list|,
literal|0
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|1234
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|setResourceTrackerService
argument_list|(
name|rm2
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|registerNode
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// assert app state has been saved.
name|rm2
operator|.
name|waitForState
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|)
expr_stmt|;
comment|// wait for application cleanup message received on NM1
name|waitForAppCleanupMessageRecved
argument_list|(
name|nm1
argument_list|,
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|// wait for application cleanup message received on NM2
name|waitForAppCleanupMessageRecved
argument_list|(
name|nm2
argument_list|,
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|rm2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|TestApplicationCleanup
name|t
init|=
operator|new
name|TestApplicationCleanup
argument_list|()
decl_stmt|;
name|t
operator|.
name|testAppCleanup
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

