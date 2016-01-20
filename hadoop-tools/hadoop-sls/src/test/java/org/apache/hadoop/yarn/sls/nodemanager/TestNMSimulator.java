begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.nodemanager
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
name|nodemanager
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
name|conf
operator|.
name|SLSConfiguration
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
DECL|class|TestNMSimulator
specifier|public
class|class
name|TestNMSimulator
block|{
DECL|field|GB
specifier|private
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
DECL|field|rm
specifier|private
name|ResourceManager
name|rm
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
block|{
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
literal|"org.apache.hadoop.yarn.sls.scheduler.ResourceSchedulerWrapper"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SLSConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
literal|"org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|SLSConfiguration
operator|.
name|METRICS_SWITCH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|ResourceManager
argument_list|()
expr_stmt|;
name|rm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNMSimulator ()
specifier|public
name|void
name|testNMSimulator
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Register one node
name|NMSimulator
name|node1
init|=
operator|new
name|NMSimulator
argument_list|()
decl_stmt|;
name|node1
operator|.
name|init
argument_list|(
literal|"/rack1/node1"
argument_list|,
name|GB
operator|*
literal|10
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|,
name|rm
argument_list|)
expr_stmt|;
name|node1
operator|.
name|middleStep
argument_list|()
expr_stmt|;
name|int
name|numClusterNodes
init|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNumClusterNodes
argument_list|()
decl_stmt|;
name|int
name|cumulativeSleepTime
init|=
literal|0
decl_stmt|;
name|int
name|sleepInterval
init|=
literal|100
decl_stmt|;
while|while
condition|(
name|numClusterNodes
operator|!=
literal|1
operator|&&
name|cumulativeSleepTime
operator|<
literal|5000
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepInterval
argument_list|)
expr_stmt|;
name|cumulativeSleepTime
operator|=
name|cumulativeSleepTime
operator|+
name|sleepInterval
expr_stmt|;
name|numClusterNodes
operator|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNumClusterNodes
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNumClusterNodes
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|GB
operator|*
literal|10
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getRootQueueMetrics
argument_list|()
operator|.
name|getAvailableMB
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getRootQueueMetrics
argument_list|()
operator|.
name|getAvailableVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
comment|// Allocate one container on node1
name|ContainerId
name|cId1
init|=
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Container
name|container1
init|=
name|Container
operator|.
name|newInstance
argument_list|(
name|cId1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
name|GB
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|node1
operator|.
name|addNewContainer
argument_list|(
name|container1
argument_list|,
literal|100000l
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Node1 should have one running container."
argument_list|,
name|node1
operator|.
name|getRunningContainers
argument_list|()
operator|.
name|containsKey
argument_list|(
name|cId1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Allocate one AM container on node1
name|ContainerId
name|cId2
init|=
name|newContainerId
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Container
name|container2
init|=
name|Container
operator|.
name|newInstance
argument_list|(
name|cId2
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
name|GB
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|node1
operator|.
name|addNewContainer
argument_list|(
name|container2
argument_list|,
operator|-
literal|1l
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Node1 should have one running AM container"
argument_list|,
name|node1
operator|.
name|getAMContainers
argument_list|()
operator|.
name|contains
argument_list|(
name|cId2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove containers
name|node1
operator|.
name|cleanupContainer
argument_list|(
name|cId1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Container1 should be removed from Node1."
argument_list|,
name|node1
operator|.
name|getCompletedContainers
argument_list|()
operator|.
name|contains
argument_list|(
name|cId1
argument_list|)
argument_list|)
expr_stmt|;
name|node1
operator|.
name|cleanupContainer
argument_list|(
name|cId2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Container2 should be removed from Node1."
argument_list|,
name|node1
operator|.
name|getAMContainers
argument_list|()
operator|.
name|contains
argument_list|(
name|cId2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|newContainerId (int appId, int appAttemptId, int cId)
specifier|private
name|ContainerId
name|newContainerId
parameter_list|(
name|int
name|appId
parameter_list|,
name|int
name|appAttemptId
parameter_list|,
name|int
name|cId
parameter_list|)
block|{
return|return
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|appId
argument_list|)
argument_list|,
name|appAttemptId
argument_list|)
argument_list|,
name|cId
argument_list|)
return|;
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
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

