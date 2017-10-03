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
name|Collections
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
name|RegisterApplicationMasterResponse
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
name|exceptions
operator|.
name|InvalidApplicationMasterRequestException
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

begin_comment
comment|/**  * Test UAM handling in RM.  */
end_comment

begin_class
DECL|class|TestWorkPreservingUnmanagedAM
specifier|public
class|class
name|TestWorkPreservingUnmanagedAM
extends|extends
name|ParameterizedSchedulerTestBase
block|{
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|method|TestWorkPreservingUnmanagedAM (SchedulerType type)
specifier|public
name|TestWorkPreservingUnmanagedAM
parameter_list|(
name|SchedulerType
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
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
name|getConf
argument_list|()
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|setMiniClusterMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test UAM work preserving restart. When the keepContainersAcrossAttempt flag    * is on, we allow UAM to directly register again and move on without getting    * the applicationAlreadyRegistered exception.    */
DECL|method|testUAMRestart (boolean keepContainers)
specifier|protected
name|void
name|testUAMRestart
parameter_list|(
name|boolean
name|keepContainers
parameter_list|)
throws|throws
name|Exception
block|{
comment|// start RM
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
name|nm
init|=
operator|new
name|MockNM
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|15120
argument_list|,
name|rm
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|nm
operator|.
name|registerNode
argument_list|()
expr_stmt|;
comment|// create app and launch the UAM
name|boolean
name|unamanged
init|=
literal|true
decl_stmt|;
name|int
name|maxAttempts
init|=
literal|1
decl_stmt|;
name|boolean
name|waitForAccepted
init|=
literal|true
decl_stmt|;
name|RMApp
name|app
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|""
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
name|unamanged
argument_list|,
literal|null
argument_list|,
name|maxAttempts
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|waitForAccepted
argument_list|,
name|keepContainers
argument_list|)
decl_stmt|;
name|MockAM
name|am
init|=
name|MockRM
operator|.
name|launchUAM
argument_list|(
name|app
argument_list|,
name|rm
argument_list|,
name|nm
argument_list|)
decl_stmt|;
comment|// Register for the first time
name|am
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|// Allocate two containers to UAM
name|int
name|numContainers
init|=
literal|3
decl_stmt|;
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
literal|"127.0.0.1"
argument_list|,
literal|1000
argument_list|,
name|numContainers
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
while|while
condition|(
name|conts
operator|.
name|size
argument_list|()
operator|<
name|numContainers
condition|)
block|{
name|nm
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conts
operator|.
name|addAll
argument_list|(
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
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// Release one container
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releaseList
init|=
name|Collections
operator|.
name|singletonList
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
decl_stmt|;
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|finishedConts
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
name|releaseList
argument_list|)
operator|.
name|getCompletedContainersStatuses
argument_list|()
decl_stmt|;
while|while
condition|(
name|finishedConts
operator|.
name|size
argument_list|()
operator|<
name|releaseList
operator|.
name|size
argument_list|()
condition|)
block|{
name|nm
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|finishedConts
operator|.
name|addAll
argument_list|(
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
name|getCompletedContainersStatuses
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// Register for the second time
name|RegisterApplicationMasterResponse
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|am
operator|.
name|registerAppAttempt
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidApplicationMasterRequestException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|keepContainers
argument_list|)
expr_stmt|;
return|return;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"RM should not allow second register"
operator|+
literal|" for UAM without keep container flag "
argument_list|,
literal|true
argument_list|,
name|keepContainers
argument_list|)
expr_stmt|;
comment|// Expecting the two running containers previously
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|response
operator|.
name|getContainersFromPreviousAttempts
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
name|response
operator|.
name|getNMTokensFromPreviousAttempts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Allocate one more containers to UAM, just to be safe
name|numContainers
operator|=
literal|1
expr_stmt|;
name|am
operator|.
name|allocate
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|1000
argument_list|,
name|numContainers
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|nm
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
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
while|while
condition|(
name|conts
operator|.
name|size
argument_list|()
operator|<
name|numContainers
condition|)
block|{
name|nm
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conts
operator|.
name|addAll
argument_list|(
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
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|600000
argument_list|)
DECL|method|testUAMRestartKeepContainers ()
specifier|public
name|void
name|testUAMRestartKeepContainers
parameter_list|()
throws|throws
name|Exception
block|{
name|testUAMRestart
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|600000
argument_list|)
DECL|method|testUAMRestartNoKeepContainers ()
specifier|public
name|void
name|testUAMRestartNoKeepContainers
parameter_list|()
throws|throws
name|Exception
block|{
name|testUAMRestart
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

