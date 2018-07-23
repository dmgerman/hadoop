begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.metrics
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
name|metrics
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|metrics2
operator|.
name|source
operator|.
name|JvmMetrics
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|*
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
name|util
operator|.
name|Records
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
DECL|class|TestNodeManagerMetrics
specifier|public
class|class
name|TestNodeManagerMetrics
block|{
DECL|field|GiB
specifier|static
specifier|final
name|int
name|GiB
init|=
literal|1024
decl_stmt|;
comment|// MiB
DECL|field|metrics
specifier|private
name|NodeManagerMetrics
name|metrics
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"NodeManager"
argument_list|)
expr_stmt|;
name|metrics
operator|=
name|NodeManagerMetrics
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReferenceOfSingletonJvmMetrics ()
specifier|public
name|void
name|testReferenceOfSingletonJvmMetrics
parameter_list|()
block|{
name|JvmMetrics
name|jvmMetrics
init|=
name|JvmMetrics
operator|.
name|initSingleton
argument_list|(
literal|"NodeManagerModule"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"NodeManagerMetrics should reference the singleton"
operator|+
literal|" JvmMetrics instance"
argument_list|,
name|jvmMetrics
argument_list|,
name|metrics
operator|.
name|getJvmMetrics
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNames ()
annotation|@
name|Test
specifier|public
name|void
name|testNames
parameter_list|()
block|{
name|Resource
name|total
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|total
operator|.
name|setMemorySize
argument_list|(
literal|8
operator|*
name|GiB
argument_list|)
expr_stmt|;
name|total
operator|.
name|setVirtualCores
argument_list|(
literal|16
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setMemorySize
argument_list|(
literal|512
argument_list|)
expr_stmt|;
comment|//512MiB
name|resource
operator|.
name|setVirtualCores
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Resource
name|largerResource
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|largerResource
operator|.
name|setMemorySize
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|largerResource
operator|.
name|setVirtualCores
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Resource
name|smallerResource
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|smallerResource
operator|.
name|setMemorySize
argument_list|(
literal|256
argument_list|)
expr_stmt|;
name|smallerResource
operator|.
name|setVirtualCores
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|addResource
argument_list|(
name|total
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|--
operator|>
literal|0
condition|;
control|)
block|{
comment|// allocate 10 containers(allocatedGB: 5GiB, availableGB: 3GiB)
name|metrics
operator|.
name|launchedContainer
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|allocateContainer
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
name|metrics
operator|.
name|initingContainer
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|endInitingContainer
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|runningContainer
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|endRunningContainer
argument_list|()
expr_stmt|;
comment|// Releasing 3 containers(allocatedGB: 3.5GiB, availableGB: 4.5GiB)
name|metrics
operator|.
name|completedContainer
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|releaseContainer
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|failedContainer
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|releaseContainer
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|killedContainer
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|releaseContainer
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|initingContainer
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|runningContainer
argument_list|()
expr_stmt|;
comment|// Increase resource for a container
name|metrics
operator|.
name|changeContainer
argument_list|(
name|resource
argument_list|,
name|largerResource
argument_list|)
expr_stmt|;
comment|// Decrease resource for a container
name|metrics
operator|.
name|changeContainer
argument_list|(
name|resource
argument_list|,
name|smallerResource
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|metrics
operator|.
name|containerLaunchDuration
operator|.
name|changed
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|addContainerLaunchDuration
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|metrics
operator|.
name|containerLaunchDuration
operator|.
name|changed
argument_list|()
argument_list|)
expr_stmt|;
comment|// availableGB is expected to be floored,
comment|// while allocatedGB is expected to be ceiled.
comment|// allocatedGB: 3.75GB allocated memory is shown as 4GB
comment|// availableGB: 4.25GB available memory is shown as 4GB
name|checkMetrics
argument_list|(
literal|10
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|,
literal|4
argument_list|,
literal|13
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// Update resource and check available resource again
name|metrics
operator|.
name|addResource
argument_list|(
name|total
argument_list|)
expr_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
literal|"NodeManagerMetrics"
argument_list|)
decl_stmt|;
name|assertGauge
argument_list|(
literal|"AvailableGB"
argument_list|,
literal|12
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AvailableVCores"
argument_list|,
literal|19
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
DECL|method|checkMetrics (int launched, int completed, int failed, int killed, int initing, int running, int allocatedGB, int allocatedContainers, int availableGB, int allocatedVCores, int availableVCores)
specifier|public
specifier|static
name|void
name|checkMetrics
parameter_list|(
name|int
name|launched
parameter_list|,
name|int
name|completed
parameter_list|,
name|int
name|failed
parameter_list|,
name|int
name|killed
parameter_list|,
name|int
name|initing
parameter_list|,
name|int
name|running
parameter_list|,
name|int
name|allocatedGB
parameter_list|,
name|int
name|allocatedContainers
parameter_list|,
name|int
name|availableGB
parameter_list|,
name|int
name|allocatedVCores
parameter_list|,
name|int
name|availableVCores
parameter_list|)
block|{
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
literal|"NodeManagerMetrics"
argument_list|)
decl_stmt|;
name|assertCounter
argument_list|(
literal|"ContainersLaunched"
argument_list|,
name|launched
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"ContainersCompleted"
argument_list|,
name|completed
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"ContainersFailed"
argument_list|,
name|failed
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"ContainersKilled"
argument_list|,
name|killed
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"ContainersIniting"
argument_list|,
name|initing
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"ContainersRunning"
argument_list|,
name|running
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AllocatedGB"
argument_list|,
name|allocatedGB
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AllocatedVCores"
argument_list|,
name|allocatedVCores
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AllocatedContainers"
argument_list|,
name|allocatedContainers
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AvailableGB"
argument_list|,
name|availableGB
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AvailableVCores"
argument_list|,
name|availableVCores
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

