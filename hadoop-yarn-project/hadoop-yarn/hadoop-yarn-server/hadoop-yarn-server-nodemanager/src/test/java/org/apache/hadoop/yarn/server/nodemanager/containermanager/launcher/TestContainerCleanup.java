begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|fs
operator|.
name|Path
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
name|InlineDispatcher
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
name|recovery
operator|.
name|NMStateStoreService
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import static
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
operator|.
name|NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
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
name|verify
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

begin_comment
comment|/**  * Tests for {@link ContainerCleanup}.  */
end_comment

begin_class
DECL|class|TestContainerCleanup
specifier|public
class|class
name|TestContainerCleanup
block|{
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|field|containerId
specifier|private
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|executor
specifier|private
name|ContainerExecutor
name|executor
decl_stmt|;
DECL|field|launch
specifier|private
name|ContainerLaunch
name|launch
decl_stmt|;
DECL|field|cleanup
specifier|private
name|ContainerCleanup
name|cleanup
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
decl_stmt|;
name|NMStateStoreService
name|storeService
init|=
name|mock
argument_list|(
name|NMStateStoreService
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getNMStateStore
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|storeService
argument_list|)
expr_stmt|;
name|Dispatcher
name|dispatcher
init|=
operator|new
name|InlineDispatcher
argument_list|()
decl_stmt|;
name|executor
operator|=
name|mock
argument_list|(
name|ContainerExecutor
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|executor
operator|.
name|signalContainer
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|ContainerSignalContext
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|containerId
operator|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|attemptId
argument_list|,
literal|1
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
name|launch
operator|=
name|mock
argument_list|(
name|ContainerLaunch
operator|.
name|class
argument_list|)
expr_stmt|;
name|launch
operator|.
name|containerAlreadyLaunched
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|launch
operator|.
name|completed
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|launch
operator|.
name|pidFilePath
operator|=
operator|new
name|Path
argument_list|(
literal|"target/"
operator|+
name|containerId
operator|.
name|toString
argument_list|()
operator|+
literal|".pid"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|launch
operator|.
name|getContainerPid
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|cleanup
operator|=
operator|new
name|ContainerCleanup
argument_list|(
name|context
argument_list|,
name|conf
argument_list|,
name|dispatcher
argument_list|,
name|executor
argument_list|,
name|container
argument_list|,
name|launch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoCleanupWhenContainerNotLaunched ()
specifier|public
name|void
name|testNoCleanupWhenContainerNotLaunched
parameter_list|()
throws|throws
name|IOException
block|{
name|cleanup
operator|.
name|run
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|launch
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|signalContainer
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|SignalContainerCommand
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCleanup ()
specifier|public
name|void
name|testCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|launch
operator|.
name|containerAlreadyLaunched
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cleanup
operator|.
name|run
argument_list|()
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|ContainerSignalContext
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|ContainerSignalContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|executor
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
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"signal"
argument_list|,
name|ContainerExecutor
operator|.
name|Signal
operator|.
name|TERM
argument_list|,
name|captor
operator|.
name|getValue
argument_list|()
operator|.
name|getSignal
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailedExitCleanup ()
specifier|public
name|void
name|testFailedExitCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|launch
operator|.
name|completed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cleanup
operator|.
name|run
argument_list|()
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|ContainerSignalContext
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|ContainerSignalContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|executor
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
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"signal"
argument_list|,
name|ContainerExecutor
operator|.
name|Signal
operator|.
name|TERM
argument_list|,
name|captor
operator|.
name|getValue
argument_list|()
operator|.
name|getSignal
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

