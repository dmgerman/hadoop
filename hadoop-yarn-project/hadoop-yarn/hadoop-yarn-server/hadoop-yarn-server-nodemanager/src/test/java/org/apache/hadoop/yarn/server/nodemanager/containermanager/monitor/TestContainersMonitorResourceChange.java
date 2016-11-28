begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|monitor
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
name|ConcurrentMap
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
name|ConcurrentSkipListMap
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
name|ResourceUtilization
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
name|nodemanager
operator|.
name|ContainerExecutor
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
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerEventType
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerImpl
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|monitor
operator|.
name|ContainersMonitorImpl
operator|.
name|ProcessTreeInfo
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
name|nodemanager
operator|.
name|executor
operator|.
name|ContainerLivenessContext
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
name|nodemanager
operator|.
name|executor
operator|.
name|ContainerSignalContext
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
name|nodemanager
operator|.
name|executor
operator|.
name|ContainerStartContext
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
name|nodemanager
operator|.
name|executor
operator|.
name|DeletionAsUserContext
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
name|nodemanager
operator|.
name|executor
operator|.
name|LocalizerStartContext
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
name|After
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_class
DECL|class|TestContainersMonitorResourceChange
specifier|public
class|class
name|TestContainersMonitorResourceChange
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TestContainersMonitorResourceChange
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containersMonitor
specifier|private
name|ContainersMonitorImpl
name|containersMonitor
decl_stmt|;
DECL|field|executor
specifier|private
name|MockExecutor
name|executor
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|dispatcher
specifier|private
name|AsyncDispatcher
name|dispatcher
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
decl_stmt|;
DECL|field|containerEventHandler
specifier|private
name|MockContainerEventHandler
name|containerEventHandler
decl_stmt|;
DECL|field|containerMap
specifier|private
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|containerMap
decl_stmt|;
DECL|field|WAIT_MS_PER_LOOP
specifier|static
specifier|final
name|int
name|WAIT_MS_PER_LOOP
init|=
literal|20
decl_stmt|;
comment|// 20 milli seconds
DECL|class|MockExecutor
specifier|private
specifier|static
class|class
name|MockExecutor
extends|extends
name|ContainerExecutor
block|{
annotation|@
name|Override
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|startLocalizer (LocalizerStartContext ctx)
specifier|public
name|void
name|startLocalizer
parameter_list|(
name|LocalizerStartContext
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{     }
annotation|@
name|Override
DECL|method|launchContainer (ContainerStartContext ctx)
specifier|public
name|int
name|launchContainer
parameter_list|(
name|ContainerStartContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|signalContainer (ContainerSignalContext ctx)
specifier|public
name|boolean
name|signalContainer
parameter_list|(
name|ContainerSignalContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|deleteAsUser (DeletionAsUserContext ctx)
specifier|public
name|void
name|deleteAsUser
parameter_list|(
name|DeletionAsUserContext
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{     }
annotation|@
name|Override
DECL|method|symLink (String target, String symlink)
specifier|public
name|void
name|symLink
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|symlink
parameter_list|)
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
DECL|method|getProcessId (ContainerId containerId)
specifier|public
name|String
name|getProcessId
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|containerId
operator|.
name|getContainerId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isContainerAlive (ContainerLivenessContext ctx)
specifier|public
name|boolean
name|isContainerAlive
parameter_list|(
name|ContainerLivenessContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|class|MockContainerEventHandler
specifier|private
specifier|static
class|class
name|MockContainerEventHandler
implements|implements
name|EventHandler
argument_list|<
name|ContainerEvent
argument_list|>
block|{
DECL|field|killedContainer
specifier|final
specifier|private
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|killedContainer
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|handle (ContainerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainerEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|ContainerEventType
operator|.
name|KILL_CONTAINER
condition|)
block|{
synchronized|synchronized
init|(
name|killedContainer
init|)
block|{
name|killedContainer
operator|.
name|add
argument_list|(
name|event
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isContainerKilled (ContainerId containerId)
specifier|public
name|boolean
name|isContainerKilled
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
synchronized|synchronized
init|(
name|killedContainer
init|)
block|{
return|return
name|killedContainer
operator|.
name|contains
argument_list|(
name|containerId
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|executor
operator|=
operator|new
name|MockExecutor
argument_list|()
expr_stmt|;
name|dispatcher
operator|=
operator|new
name|AsyncDispatcher
argument_list|()
expr_stmt|;
name|context
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
expr_stmt|;
name|containerMap
operator|=
operator|new
name|ConcurrentSkipListMap
argument_list|<>
argument_list|()
expr_stmt|;
name|Container
name|container
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ContainerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerMap
operator|.
name|put
argument_list|(
name|getContainerId
argument_list|(
literal|1
argument_list|)
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|containerMap
argument_list|)
operator|.
name|when
argument_list|(
name|context
argument_list|)
operator|.
name|getContainers
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_RESOURCE_CALCULATOR
argument_list|,
name|MockResourceCalculatorPlugin
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_PROCESS_TREE
argument_list|,
name|MockResourceCalculatorProcessTree
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|containerEventHandler
operator|=
operator|new
name|MockContainerEventHandler
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ContainerEventType
operator|.
name|class
argument_list|,
name|containerEventHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|containersMonitor
operator|!=
literal|null
condition|)
block|{
name|containersMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dispatcher
operator|!=
literal|null
condition|)
block|{
name|dispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testContainersResourceChange ()
specifier|public
name|void
name|testContainersResourceChange
parameter_list|()
throws|throws
name|Exception
block|{
comment|// set container monitor interval to be 20ms
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_INTERVAL_MS
argument_list|,
literal|20L
argument_list|)
expr_stmt|;
name|containersMonitor
operator|=
name|createContainersMonitor
argument_list|(
name|executor
argument_list|,
name|dispatcher
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|containersMonitor
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|containersMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// create container 1
name|containersMonitor
operator|.
name|handle
argument_list|(
operator|new
name|ContainerStartMonitoringEvent
argument_list|(
name|getContainerId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|2100L
argument_list|,
literal|1000L
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify that this container is properly tracked
name|assertNotNull
argument_list|(
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000L
argument_list|,
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getPmemLimit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2100L
argument_list|,
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getVmemLimit
argument_list|()
argument_list|)
expr_stmt|;
comment|// sleep longer than the monitor interval to make sure resource
comment|// enforcement has started
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
comment|// increase pmem usage, the container should be killed
name|MockResourceCalculatorProcessTree
name|mockTree
init|=
operator|(
name|MockResourceCalculatorProcessTree
operator|)
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getProcessTree
argument_list|()
decl_stmt|;
name|mockTree
operator|.
name|setRssMemorySize
argument_list|(
literal|2500L
argument_list|)
expr_stmt|;
comment|// verify that this container is killed
for|for
control|(
name|int
name|waitMs
init|=
literal|0
init|;
name|waitMs
operator|<
literal|5000
condition|;
name|waitMs
operator|+=
literal|50
control|)
block|{
if|if
condition|(
name|containerEventHandler
operator|.
name|isContainerKilled
argument_list|(
name|getContainerId
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|containerEventHandler
operator|.
name|isContainerKilled
argument_list|(
name|getContainerId
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// create container 2
name|containersMonitor
operator|.
name|handle
argument_list|(
operator|new
name|ContainerStartMonitoringEvent
argument_list|(
name|getContainerId
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|2202009L
argument_list|,
literal|1048576L
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify that this container is properly tracked
name|assertNotNull
argument_list|(
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1048576L
argument_list|,
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getPmemLimit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2202009L
argument_list|,
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getVmemLimit
argument_list|()
argument_list|)
expr_stmt|;
comment|// trigger a change resource event, check limit after change
name|containersMonitor
operator|.
name|handle
argument_list|(
operator|new
name|ChangeMonitoringContainerResourceEvent
argument_list|(
name|getContainerId
argument_list|(
literal|2
argument_list|)
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2097152L
argument_list|,
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getPmemLimit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4404019L
argument_list|,
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getVmemLimit
argument_list|()
argument_list|)
expr_stmt|;
comment|// sleep longer than the monitor interval to make sure resource
comment|// enforcement has started
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
comment|// increase pmem usage, the container should NOT be killed
name|mockTree
operator|=
operator|(
name|MockResourceCalculatorProcessTree
operator|)
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getProcessTree
argument_list|()
expr_stmt|;
name|mockTree
operator|.
name|setRssMemorySize
argument_list|(
literal|2000000L
argument_list|)
expr_stmt|;
comment|// verify that this container is not killed
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|containerEventHandler
operator|.
name|isContainerKilled
argument_list|(
name|getContainerId
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|containersMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainersResourceChangeIsTriggeredImmediately ()
specifier|public
name|void
name|testContainersResourceChangeIsTriggeredImmediately
parameter_list|()
throws|throws
name|Exception
block|{
comment|// set container monitor interval to be 20s
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_INTERVAL_MS
argument_list|,
literal|20000L
argument_list|)
expr_stmt|;
name|containersMonitor
operator|=
name|createContainersMonitor
argument_list|(
name|executor
argument_list|,
name|dispatcher
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|containersMonitor
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|containersMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// sleep 1 second to make sure the container monitor thread is
comment|// now waiting for the next monitor cycle
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// create a container with id 3
name|containersMonitor
operator|.
name|handle
argument_list|(
operator|new
name|ContainerStartMonitoringEvent
argument_list|(
name|getContainerId
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|2202009L
argument_list|,
literal|1048576L
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify that this container has been tracked
name|assertNotNull
argument_list|(
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// trigger a change resource event, check limit after change
name|containersMonitor
operator|.
name|handle
argument_list|(
operator|new
name|ChangeMonitoringContainerResourceEvent
argument_list|(
name|getContainerId
argument_list|(
literal|3
argument_list|)
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify that this container has been properly tracked with the
comment|// correct size
name|assertEquals
argument_list|(
literal|2097152L
argument_list|,
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|getPmemLimit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4404019L
argument_list|,
name|getProcessTreeInfo
argument_list|(
name|getContainerId
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|getVmemLimit
argument_list|()
argument_list|)
expr_stmt|;
name|containersMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainersCPUResourceForDefaultValue ()
specifier|public
name|void
name|testContainersCPUResourceForDefaultValue
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|newConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// set container monitor interval to be 20s
name|newConf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_INTERVAL_MS
argument_list|,
literal|20L
argument_list|)
expr_stmt|;
name|containersMonitor
operator|=
name|createContainersMonitor
argument_list|(
name|executor
argument_list|,
name|dispatcher
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|newConf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_PROCESS_TREE
argument_list|,
name|MockCPUResourceCalculatorProcessTree
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
comment|// set container monitor interval to be 20ms
name|containersMonitor
operator|.
name|init
argument_list|(
name|newConf
argument_list|)
expr_stmt|;
name|containersMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// create container 1
name|containersMonitor
operator|.
name|handle
argument_list|(
operator|new
name|ContainerStartMonitoringEvent
argument_list|(
name|getContainerId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|2100L
argument_list|,
literal|1000L
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify the container utilization value.
comment|// Since MockCPUResourceCalculatorProcessTree will return a -1 as CPU
comment|// utilization, containersUtilization will not be calculated and hence it
comment|// will be 0.
name|assertEquals
argument_list|(
literal|"Resource utilization must be default with MonitorThread's first run"
argument_list|,
literal|0
argument_list|,
name|containersMonitor
operator|.
name|getContainersUtilization
argument_list|()
operator|.
name|compareTo
argument_list|(
name|ResourceUtilization
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0.0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify the container utilization value. Since atleast one round is done,
comment|// we can expect a non-zero value for container utilization as
comment|// MockCPUResourceCalculatorProcessTree#getCpuUsagePercent will return 50.
name|waitForContainerResourceUtilizationChange
argument_list|(
name|containersMonitor
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|containersMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|waitForContainerResourceUtilizationChange ( ContainersMonitorImpl containersMonitor, int timeoutMsecs)
specifier|public
specifier|static
name|void
name|waitForContainerResourceUtilizationChange
parameter_list|(
name|ContainersMonitorImpl
name|containersMonitor
parameter_list|,
name|int
name|timeoutMsecs
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|timeWaiting
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|0
operator|==
name|containersMonitor
operator|.
name|getContainersUtilization
argument_list|()
operator|.
name|compareTo
argument_list|(
name|ResourceUtilization
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0.0f
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|timeWaiting
operator|>=
name|timeoutMsecs
condition|)
block|{
break|break;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Monitor thread is waiting for resource utlization change."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|WAIT_MS_PER_LOOP
argument_list|)
expr_stmt|;
name|timeWaiting
operator|+=
name|WAIT_MS_PER_LOOP
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Resource utilization is not changed from second run onwards"
argument_list|,
literal|0
operator|!=
name|containersMonitor
operator|.
name|getContainersUtilization
argument_list|()
operator|.
name|compareTo
argument_list|(
name|ResourceUtilization
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0.0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createContainersMonitor ( ContainerExecutor containerExecutor, AsyncDispatcher dispatcher, Context context)
specifier|private
name|ContainersMonitorImpl
name|createContainersMonitor
parameter_list|(
name|ContainerExecutor
name|containerExecutor
parameter_list|,
name|AsyncDispatcher
name|dispatcher
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
return|return
operator|new
name|ContainersMonitorImpl
argument_list|(
name|containerExecutor
argument_list|,
name|dispatcher
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|getContainerId (int id)
specifier|private
name|ContainerId
name|getContainerId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|123456L
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|method|getProcessTreeInfo (ContainerId id)
specifier|private
name|ProcessTreeInfo
name|getProcessTreeInfo
parameter_list|(
name|ContainerId
name|id
parameter_list|)
block|{
return|return
name|containersMonitor
operator|.
name|trackingContainers
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
end_class

end_unit

