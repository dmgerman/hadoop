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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
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
name|nodelabels
operator|.
name|NullRMNodeLabelsManager
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
name|capacity
operator|.
name|CapacityScheduler
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
name|capacity
operator|.
name|CapacitySchedulerConfiguration
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
name|capacity
operator|.
name|CapacitySchedulerMetrics
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
name|Test
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
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_comment
comment|/**  * Test class for CS metrics.  */
end_comment

begin_class
DECL|class|TestCapacitySchedulerMetrics
specifier|public
class|class
name|TestCapacitySchedulerMetrics
block|{
DECL|field|rm
specifier|private
name|MockRM
name|rm
decl_stmt|;
annotation|@
name|Test
DECL|method|testCSMetrics ()
specifier|public
name|void
name|testCSMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
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
name|setBoolean
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|SCHEDULE_ASYNCHRONOUSLY_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|RMNodeLabelsManager
name|mgr
init|=
operator|new
name|NullRMNodeLabelsManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RMNodeLabelsManager
name|createNodeLabelManager
parameter_list|()
block|{
return|return
name|mgr
return|;
block|}
block|}
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|setNodeLabelManager
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
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
literal|"host1:1234"
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"host2:1234"
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|CapacitySchedulerMetrics
name|csMetrics
init|=
name|CapacitySchedulerMetrics
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|csMetrics
argument_list|)
expr_stmt|;
try|try
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|csMetrics
operator|.
name|getNumOfNodeUpdate
argument_list|()
operator|==
literal|2
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"CS metrics not updated on node-update events."
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|csMetrics
operator|.
name|getNumOfAllocates
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|csMetrics
operator|.
name|getNumOfCommitSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|RMApp
name|rmApp
init|=
name|MockRMAppSubmitter
operator|.
name|submit
argument_list|(
name|rm
argument_list|,
name|MockRMAppSubmissionData
operator|.
name|Builder
operator|.
name|createWithMemory
argument_list|(
literal|1024
argument_list|,
name|rm
argument_list|)
operator|.
name|withAppName
argument_list|(
literal|"app"
argument_list|)
operator|.
name|withUser
argument_list|(
literal|"user"
argument_list|)
operator|.
name|withAcls
argument_list|(
literal|null
argument_list|)
operator|.
name|withUnmanagedAM
argument_list|(
literal|false
argument_list|)
operator|.
name|withQueue
argument_list|(
literal|"default"
argument_list|)
operator|.
name|withMaxAppAttempts
argument_list|(
literal|1
argument_list|)
operator|.
name|withCredentials
argument_list|(
literal|null
argument_list|)
operator|.
name|withAppType
argument_list|(
literal|null
argument_list|)
operator|.
name|withWaitForAppAcceptedState
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|MockAM
name|am
init|=
name|MockRM
operator|.
name|launchAMWhenAsyncSchedulingEnabled
argument_list|(
name|rmApp
argument_list|,
name|rm
argument_list|)
decl_stmt|;
name|am
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|am
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
literal|1024
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Verify HB metrics updated
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|csMetrics
operator|.
name|getNumOfNodeUpdate
argument_list|()
operator|==
literal|4
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
comment|// For async mode, the number of alloc might be bigger than 1
name|Assert
operator|.
name|assertTrue
argument_list|(
name|csMetrics
operator|.
name|getNumOfAllocates
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// But there will be only 2 successful commit (1 AM + 1 task)
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|csMetrics
operator|.
name|getNumOfCommitSuccess
argument_list|()
operator|==
literal|2
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"CS metrics not updated on node-update events."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|rm
operator|!=
literal|null
condition|)
block|{
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

