begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher
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
name|launcher
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
name|test
operator|.
name|Whitebox
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
name|SignalContainerCommand
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
name|LocalDirsHandlerService
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
name|NodeManager
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
name|ContainerManagerImpl
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
name|application
operator|.
name|Application
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
name|application
operator|.
name|ApplicationImpl
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
name|InjectMocks
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mock
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
import|import
name|org
operator|.
name|mockito
operator|.
name|MockitoAnnotations
import|;
end_import

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
name|Collections
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
name|concurrent
operator|.
name|ExecutorService
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
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tests to verify all the Container's Launcher Events in  * {@link ContainersLauncher} are handled as expected.  */
end_comment

begin_class
DECL|class|TestContainersLauncher
specifier|public
class|class
name|TestContainersLauncher
block|{
annotation|@
name|Mock
DECL|field|app1
specifier|private
name|ApplicationImpl
name|app1
decl_stmt|;
annotation|@
name|Mock
DECL|field|container
specifier|private
name|ContainerImpl
name|container
decl_stmt|;
annotation|@
name|Mock
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
decl_stmt|;
annotation|@
name|Mock
DECL|field|appAttemptId
specifier|private
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
annotation|@
name|Mock
DECL|field|containerId
specifier|private
name|ContainerId
name|containerId
decl_stmt|;
annotation|@
name|Mock
DECL|field|event
specifier|private
name|ContainersLauncherEvent
name|event
decl_stmt|;
annotation|@
name|Mock
DECL|field|context
specifier|private
name|NodeManager
operator|.
name|NMContext
name|context
decl_stmt|;
annotation|@
name|Mock
DECL|field|dispatcher
specifier|private
name|AsyncDispatcher
name|dispatcher
decl_stmt|;
annotation|@
name|Mock
DECL|field|exec
specifier|private
name|ContainerExecutor
name|exec
decl_stmt|;
annotation|@
name|Mock
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
annotation|@
name|Mock
DECL|field|containerManager
specifier|private
name|ContainerManagerImpl
name|containerManager
decl_stmt|;
annotation|@
name|Mock
DECL|field|containerLauncher
specifier|private
name|ExecutorService
name|containerLauncher
decl_stmt|;
annotation|@
name|Mock
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Mock
DECL|field|containerLaunch
specifier|private
name|ContainerLaunch
name|containerLaunch
decl_stmt|;
annotation|@
name|InjectMocks
DECL|field|tempContainersLauncher
specifier|private
name|ContainersLauncher
name|tempContainersLauncher
init|=
operator|new
name|ContainersLauncher
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|exec
argument_list|,
name|dirsHandler
argument_list|,
name|containerManager
argument_list|)
decl_stmt|;
DECL|field|spy
specifier|private
name|ContainersLauncher
name|spy
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|MockitoAnnotations
operator|.
name|initMocks
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|Application
argument_list|>
name|applications
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|applications
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|app1
argument_list|)
expr_stmt|;
name|spy
operator|=
name|spy
argument_list|(
name|tempContainersLauncher
argument_list|)
expr_stmt|;
name|conf
operator|=
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|spy
argument_list|)
operator|.
name|getConfig
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|event
operator|.
name|getContainer
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getApplications
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|applications
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testLaunchContainerEvent ()
specifier|public
name|void
name|testLaunchContainerEvent
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
name|dummyMap
init|=
operator|(
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|spy
argument_list|,
literal|"running"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainersLauncherEventType
operator|.
name|LAUNCH_CONTAINER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|spy
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|containerLauncher
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|submit
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|ContainerLaunch
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testRelaunchContainerEvent ()
specifier|public
name|void
name|testRelaunchContainerEvent
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
name|dummyMap
init|=
operator|(
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|spy
argument_list|,
literal|"running"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainersLauncherEventType
operator|.
name|RELAUNCH_CONTAINER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|spy
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|containerLauncher
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|submit
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|ContainerRelaunch
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerId
name|cid
range|:
name|dummyMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|o
init|=
name|dummyMap
operator|.
name|get
argument_list|(
name|cid
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
operator|(
name|o
operator|instanceof
name|ContainerRelaunch
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testRecoverContainerEvent ()
specifier|public
name|void
name|testRecoverContainerEvent
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
name|dummyMap
init|=
operator|(
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|spy
argument_list|,
literal|"running"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainersLauncherEventType
operator|.
name|RECOVER_CONTAINER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|spy
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|containerLauncher
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|submit
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|RecoveredContainerLaunch
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerId
name|cid
range|:
name|dummyMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|o
init|=
name|dummyMap
operator|.
name|get
argument_list|(
name|cid
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
operator|(
name|o
operator|instanceof
name|RecoveredContainerLaunch
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRecoverPausedContainerEvent ()
specifier|public
name|void
name|testRecoverPausedContainerEvent
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|when
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainersLauncherEventType
operator|.
name|RECOVER_PAUSED_CONTAINER
argument_list|)
expr_stmt|;
name|spy
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|containerLauncher
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|submit
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|RecoverPausedContainerLaunch
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCleanupContainerEvent ()
specifier|public
name|void
name|testCleanupContainerEvent
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
name|dummyMap
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|dummyMap
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|containerLaunch
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|spy
argument_list|,
literal|"running"
argument_list|,
name|dummyMap
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainersLauncherEventType
operator|.
name|CLEANUP_CONTAINER
argument_list|)
expr_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|containerLaunch
argument_list|)
operator|.
name|cleanupContainer
argument_list|()
expr_stmt|;
name|spy
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|containerLaunch
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|cleanupContainer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCleanupContainerForReINITEvent ()
specifier|public
name|void
name|testCleanupContainerForReINITEvent
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
name|dummyMap
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|dummyMap
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|containerLaunch
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|spy
argument_list|,
literal|"running"
argument_list|,
name|dummyMap
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainersLauncherEventType
operator|.
name|CLEANUP_CONTAINER_FOR_REINIT
argument_list|)
expr_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|containerLaunch
argument_list|)
operator|.
name|cleanupContainer
argument_list|()
expr_stmt|;
name|spy
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|containerLaunch
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|cleanupContainer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSignalContainerEvent ()
specifier|public
name|void
name|testSignalContainerEvent
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
name|dummyMap
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|dummyMap
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|containerLaunch
argument_list|)
expr_stmt|;
name|SignalContainersLauncherEvent
name|dummyEvent
init|=
name|mock
argument_list|(
name|SignalContainersLauncherEvent
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|dummyEvent
operator|.
name|getContainer
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|spy
argument_list|,
literal|"running"
argument_list|,
name|dummyMap
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|dummyEvent
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainersLauncherEventType
operator|.
name|SIGNAL_CONTAINER
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|dummyEvent
operator|.
name|getCommand
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SignalContainerCommand
operator|.
name|GRACEFUL_SHUTDOWN
argument_list|)
expr_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|containerLaunch
argument_list|)
operator|.
name|signalContainer
argument_list|(
name|SignalContainerCommand
operator|.
name|GRACEFUL_SHUTDOWN
argument_list|)
expr_stmt|;
name|spy
operator|.
name|handle
argument_list|(
name|dummyEvent
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|containerLaunch
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|signalContainer
argument_list|(
name|SignalContainerCommand
operator|.
name|GRACEFUL_SHUTDOWN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPauseContainerEvent ()
specifier|public
name|void
name|testPauseContainerEvent
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
name|dummyMap
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|dummyMap
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|containerLaunch
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|spy
argument_list|,
literal|"running"
argument_list|,
name|dummyMap
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainersLauncherEventType
operator|.
name|PAUSE_CONTAINER
argument_list|)
expr_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|containerLaunch
argument_list|)
operator|.
name|pauseContainer
argument_list|()
expr_stmt|;
name|spy
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|containerLaunch
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|pauseContainer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResumeContainerEvent ()
specifier|public
name|void
name|testResumeContainerEvent
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
name|dummyMap
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|dummyMap
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|containerLaunch
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|spy
argument_list|,
literal|"running"
argument_list|,
name|dummyMap
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainersLauncherEventType
operator|.
name|RESUME_CONTAINER
argument_list|)
expr_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|containerLaunch
argument_list|)
operator|.
name|resumeContainer
argument_list|()
expr_stmt|;
name|spy
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dummyMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|containerLaunch
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|resumeContainer
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

