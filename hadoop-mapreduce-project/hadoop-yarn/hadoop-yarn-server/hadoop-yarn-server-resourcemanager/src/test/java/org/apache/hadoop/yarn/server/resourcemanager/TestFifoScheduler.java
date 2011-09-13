begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|AMResponse
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
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|Store
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
name|StoreFactory
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

begin_class
DECL|class|TestFifoScheduler
specifier|public
class|class
name|TestFifoScheduler
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
name|TestFifoScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceManager
specifier|private
name|ResourceManager
name|resourceManager
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Store
name|store
init|=
name|StoreFactory
operator|.
name|getStore
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|resourceManager
operator|=
operator|new
name|ResourceManager
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
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
block|{   }
annotation|@
name|Test
DECL|method|test ()
specifier|public
name|void
name|test
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
name|int
name|GB
init|=
literal|1024
decl_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|6
operator|*
name|GB
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h2:5678"
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2048
argument_list|)
decl_stmt|;
comment|// kick the scheduling, 2 GB given to AM1, remaining 4GB on nm1
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RMAppAttempt
name|attempt1
init|=
name|app1
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|MockAM
name|am1
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am1
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getUsedResource
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|RMApp
name|app2
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2048
argument_list|)
decl_stmt|;
comment|// kick the scheduling, 2GB given to AM, remaining 2 GB on nm2
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RMAppAttempt
name|attempt2
init|=
name|app2
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|MockAM
name|am2
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am2
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getUsedResource
argument_list|(
name|nm2
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
comment|// add request for containers
name|am1
operator|.
name|addRequests
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"h1"
block|,
literal|"h2"
block|}
argument_list|,
name|GB
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|AMResponse
name|am1Response
init|=
name|am1
operator|.
name|schedule
argument_list|()
decl_stmt|;
comment|// send the request
comment|// add request for containers
name|am2
operator|.
name|addRequests
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"h1"
block|,
literal|"h2"
block|}
argument_list|,
literal|3
operator|*
name|GB
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|AMResponse
name|am2Response
init|=
name|am2
operator|.
name|schedule
argument_list|()
decl_stmt|;
comment|// send the request
comment|// kick the scheduler, 1 GB and 3 GB given to AM1 and AM2, remaining 0
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|am1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for containers to be created for app 1..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|am1Response
operator|=
name|am1
operator|.
name|schedule
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|am2Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for containers to be created for app 2..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|am2Response
operator|=
name|am2
operator|.
name|schedule
argument_list|()
expr_stmt|;
block|}
comment|// kick the scheduler, nothing given remaining 2 GB.
name|nm2
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
name|allocated1
init|=
name|am1Response
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|allocated1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|allocated1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|allocated1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|allocated2
init|=
name|am2Response
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|allocated2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
name|allocated2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|allocated2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getAvailableResource
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getAvailableResource
argument_list|(
name|nm2
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getUsedResource
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getUsedResource
argument_list|(
name|nm2
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Container
name|c1
init|=
name|allocated1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|GB
argument_list|,
name|c1
operator|.
name|getResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|c1
operator|.
name|setState
argument_list|(
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
name|nm1
operator|.
name|containerStatus
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|attempt1
operator|.
name|getJustFinishedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|<
literal|1
operator|&&
name|waitCount
operator|++
operator|!=
literal|20
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for containers to be finished for app 1... Tried "
operator|+
name|waitCount
operator|+
literal|" times already.."
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|attempt1
operator|.
name|getJustFinishedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|am1
operator|.
name|schedule
argument_list|()
operator|.
name|getCompletedContainersStatuses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
operator|*
name|GB
argument_list|,
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getUsedResource
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|rm
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
name|TestFifoScheduler
name|t
init|=
operator|new
name|TestFifoScheduler
argument_list|()
decl_stmt|;
name|t
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

