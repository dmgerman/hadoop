begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|UnsupportedFileSystemException
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
name|StartContainerRequest
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
name|StartContainersRequest
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|BaseContainerManagerTest
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
name|List
import|;
end_import

begin_comment
comment|/**  * Make sure ContainerScheduler related changes are compatible  * with old behavior.  */
end_comment

begin_class
DECL|class|TestContainerSchedulerBehaviorCompatibility
specifier|public
class|class
name|TestContainerSchedulerBehaviorCompatibility
extends|extends
name|BaseContainerManagerTest
block|{
DECL|method|TestContainerSchedulerBehaviorCompatibility ()
specifier|public
name|TestContainerSchedulerBehaviorCompatibility
parameter_list|()
throws|throws
name|UnsupportedFileSystemException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VCORES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_OPPORTUNISTIC_CONTAINERS_MAX_QUEUE_LENGTH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testForceStartGuaranteedContainersWhenOppContainerDisabled ()
specifier|public
name|void
name|testForceStartGuaranteedContainersWhenOppContainerDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|containerManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerLaunchContext
operator|.
name|setCommands
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"echo"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StartContainerRequest
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Add a container start request with #vcores> available (1).
comment|// This could happen when DefaultContainerCalculator configured because
comment|// on the RM side it won't check vcores at all.
name|list
operator|.
name|add
argument_list|(
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|containerLaunchContext
argument_list|,
name|createContainerToken
argument_list|(
name|createContainerId
argument_list|(
literal|0
argument_list|)
argument_list|,
name|DUMMY_RM_IDENTIFIER
argument_list|,
name|context
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|user
argument_list|,
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|2048
argument_list|,
literal|4
argument_list|)
argument_list|,
name|context
operator|.
name|getContainerTokenSecretManager
argument_list|()
argument_list|,
literal|null
argument_list|,
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|StartContainersRequest
operator|.
name|newInstance
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
expr_stmt|;
name|ContainerScheduler
name|cs
init|=
name|containerManager
operator|.
name|getContainerScheduler
argument_list|()
decl_stmt|;
name|int
name|nQueuedContainers
init|=
name|cs
operator|.
name|getNumQueuedContainers
argument_list|()
decl_stmt|;
name|int
name|nRunningContainers
init|=
name|cs
operator|.
name|getNumRunningContainers
argument_list|()
decl_stmt|;
comment|// Wait at most 10 secs and we expect all containers finished.
name|int
name|maxTry
init|=
literal|100
decl_stmt|;
name|int
name|nTried
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|nQueuedContainers
operator|!=
literal|0
operator|||
name|nRunningContainers
operator|!=
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|nQueuedContainers
operator|=
name|cs
operator|.
name|getNumQueuedContainers
argument_list|()
expr_stmt|;
name|nRunningContainers
operator|=
name|cs
operator|.
name|getNumRunningContainers
argument_list|()
expr_stmt|;
name|nTried
operator|++
expr_stmt|;
if|if
condition|(
name|nTried
operator|>
name|maxTry
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to get either number of queuing containers to 0 or "
operator|+
literal|"number of running containers to 0, #queued="
operator|+
name|nQueuedContainers
operator|+
literal|", #running="
operator|+
name|nRunningContainers
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

