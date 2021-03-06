begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler
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
name|scheduler
package|;
end_package

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
name|ExecutionType
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
name|security
operator|.
name|ContainerTokenIdentifier
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
operator|.
name|NMContext
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
name|ContainerManager
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
name|ContainersMonitor
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
name|metrics
operator|.
name|NodeManagerMetrics
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
operator|.
name|RecoveredContainerState
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
operator|.
name|RecoveredContainerStatus
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
name|MockitoAnnotations
import|;
end_import

begin_comment
comment|/**  * Tests to verify that the {@link ContainerScheduler} is able to  * recover active containers based on RecoveredContainerStatus and  * ExecutionType.  */
end_comment

begin_class
DECL|class|TestContainerSchedulerRecovery
specifier|public
class|class
name|TestContainerSchedulerRecovery
block|{
DECL|field|CONTAINER_SIZE
specifier|private
specifier|static
specifier|final
name|Resource
name|CONTAINER_SIZE
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|4
argument_list|)
decl_stmt|;
DECL|field|ZERO
specifier|private
specifier|static
specifier|final
name|ResourceUtilization
name|ZERO
init|=
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
decl_stmt|;
DECL|field|context
annotation|@
name|Mock
specifier|private
name|NMContext
name|context
decl_stmt|;
DECL|field|metrics
annotation|@
name|Mock
specifier|private
name|NodeManagerMetrics
name|metrics
decl_stmt|;
DECL|field|dispatcher
annotation|@
name|Mock
specifier|private
name|AsyncDispatcher
name|dispatcher
decl_stmt|;
DECL|field|token
annotation|@
name|Mock
specifier|private
name|ContainerTokenIdentifier
name|token
decl_stmt|;
DECL|field|container
annotation|@
name|Mock
specifier|private
name|ContainerImpl
name|container
decl_stmt|;
DECL|field|appId
annotation|@
name|Mock
specifier|private
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|appAttemptId
annotation|@
name|Mock
specifier|private
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
DECL|field|containerId
annotation|@
name|Mock
specifier|private
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|spy
specifier|private
name|ContainerScheduler
name|spy
decl_stmt|;
DECL|method|createRecoveredContainerState ( RecoveredContainerStatus status)
specifier|private
name|RecoveredContainerState
name|createRecoveredContainerState
parameter_list|(
name|RecoveredContainerStatus
name|status
parameter_list|)
block|{
name|RecoveredContainerState
name|mockState
init|=
name|mock
argument_list|(
name|RecoveredContainerState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockState
operator|.
name|getStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|status
argument_list|)
expr_stmt|;
return|return
name|mockState
return|;
block|}
comment|/**    * Set up the {@link ContainersMonitor} dependency of    * {@link ResourceUtilizationTracker} so that we can    * verify the resource utilization.    */
DECL|method|setupContainerMonitor ()
specifier|private
name|void
name|setupContainerMonitor
parameter_list|()
block|{
name|ContainersMonitor
name|containersMonitor
init|=
name|mock
argument_list|(
name|ContainersMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|containersMonitor
operator|.
name|getVCoresAllocatedForContainers
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|10L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|containersMonitor
operator|.
name|getPmemAllocatedForContainers
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|10240L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|containersMonitor
operator|.
name|getVmemRatio
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|containersMonitor
operator|.
name|getVmemAllocatedForContainers
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|10240L
argument_list|)
expr_stmt|;
name|ContainerManager
name|cm
init|=
name|mock
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|cm
operator|.
name|getContainersMonitor
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containersMonitor
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getContainerManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|cm
argument_list|)
expr_stmt|;
name|spy
operator|=
operator|new
name|ContainerScheduler
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|metrics
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp ()
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|MockitoAnnotations
operator|.
name|initMocks
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|setupContainerMonitor
argument_list|()
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
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|CONTAINER_SIZE
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
name|containerId
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|123L
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{   }
comment|/*Test if a container is recovered as QUEUED, GUARANTEED,   * it should be added to queuedGuaranteedContainers map.   * */
DECL|method|testRecoverContainerQueuedGuaranteed ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerQueuedGuaranteed
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|QUEUED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as QUEUED, OPPORTUNISTIC,   * it should be added to queuedOpportunisticContainers map.   * */
DECL|method|testRecoverContainerQueuedOpportunistic ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerQueuedOpportunistic
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|QUEUED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as PAUSED, GUARANTEED,   * it should be added to queuedGuaranteedContainers map.   * */
DECL|method|testRecoverContainerPausedGuaranteed ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerPausedGuaranteed
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|PAUSED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as PAUSED, OPPORTUNISTIC,   * it should be added to queuedOpportunisticContainers map.   * */
DECL|method|testRecoverContainerPausedOpportunistic ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerPausedOpportunistic
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|PAUSED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as LAUNCHED, GUARANTEED,   * it should be added to runningContainers map.   * */
DECL|method|testRecoverContainerLaunchedGuaranteed ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerLaunchedGuaranteed
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|LAUNCHED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResourceUtilization
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1024
argument_list|,
literal|4.0f
argument_list|)
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as LAUNCHED, OPPORTUNISTIC,   * it should be added to runningContainers map.   * */
DECL|method|testRecoverContainerLaunchedOpportunistic ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerLaunchedOpportunistic
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|LAUNCHED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResourceUtilization
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1024
argument_list|,
literal|4.0f
argument_list|)
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as REQUESTED, GUARANTEED,   * it should not be added to any map mentioned below.   * */
DECL|method|testRecoverContainerRequestedGuaranteed ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerRequestedGuaranteed
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|REQUESTED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as REQUESTED, OPPORTUNISTIC,   * it should not be added to any map mentioned below.   * */
DECL|method|testRecoverContainerRequestedOpportunistic ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerRequestedOpportunistic
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|REQUESTED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as COMPLETED, GUARANTEED,   * it should not be added to any map mentioned below.   * */
DECL|method|testRecoverContainerCompletedGuaranteed ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerCompletedGuaranteed
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|COMPLETED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as COMPLETED, OPPORTUNISTIC,   * it should not be added to any map mentioned below.   * */
DECL|method|testRecoverContainerCompletedOpportunistic ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverContainerCompletedOpportunistic
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|COMPLETED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|token
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as GUARANTEED but no executionType set,   * it should not be added to any map mentioned below.   * */
DECL|method|testContainerQueuedNoExecType ()
annotation|@
name|Test
specifier|public
name|void
name|testContainerQueuedNoExecType
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|QUEUED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*Test if a container is recovered as PAUSED but no executionType set,   * it should not be added to any map mentioned below.   * */
DECL|method|testContainerPausedNoExecType ()
annotation|@
name|Test
specifier|public
name|void
name|testContainerPausedNoExecType
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|RecoveredContainerState
name|rcs
init|=
name|createRecoveredContainerState
argument_list|(
name|RecoveredContainerStatus
operator|.
name|PAUSED
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|spy
operator|.
name|recoverActiveContainer
argument_list|(
name|container
argument_list|,
name|rcs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedGuaranteedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumQueuedOpportunisticContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spy
operator|.
name|getNumRunningContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZERO
argument_list|,
name|spy
operator|.
name|getCurrentUtilization
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

