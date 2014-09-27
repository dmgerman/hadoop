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
name|proto
operator|.
name|YarnServiceProtos
operator|.
name|SchedulerResourceTypes
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
name|fair
operator|.
name|FairScheduler
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
name|DefaultResourceCalculator
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
name|DominantResourceCalculator
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateResponse
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
name|FinishApplicationMasterRequest
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
name|impl
operator|.
name|pb
operator|.
name|AllocateRequestPBImpl
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
name|ApplicationMasterNotRegisteredException
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
name|InvalidContainerReleaseException
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
name|fifo
operator|.
name|FifoScheduler
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
name|BeforeClass
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
name|*
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|sleep
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_class
DECL|class|TestApplicationMasterService
specifier|public
class|class
name|TestApplicationMasterService
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
DECL|field|GB
specifier|private
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|YarnConfiguration
name|conf
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
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
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FifoScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|3000000
argument_list|)
DECL|method|testRMIdentifierOnContainerAllocation ()
specifier|public
name|void
name|testRMIdentifierOnContainerAllocation
parameter_list|()
throws|throws
name|Exception
block|{
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Register node1
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|6
operator|*
name|GB
argument_list|)
decl_stmt|;
comment|// Submit an application
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
comment|// kick the scheduling
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
name|am1
operator|.
name|addRequests
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.1"
block|}
argument_list|,
name|GB
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|AllocateResponse
name|alloc1Response
init|=
name|am1
operator|.
name|schedule
argument_list|()
decl_stmt|;
comment|// send the request
comment|// kick the scheduler
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|alloc1Response
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
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|alloc1Response
operator|=
name|am1
operator|.
name|schedule
argument_list|()
expr_stmt|;
block|}
comment|// assert RMIdentifer is set properly in allocated containers
name|Container
name|allocatedContainer
init|=
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ContainerTokenIdentifier
name|tokenId
init|=
name|BuilderUtils
operator|.
name|newContainerTokenIdentifier
argument_list|(
name|allocatedContainer
operator|.
name|getContainerToken
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MockRM
operator|.
name|getClusterTimeStamp
argument_list|()
argument_list|,
name|tokenId
operator|.
name|getRMIdentifier
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|testInvalidContainerReleaseRequest ()
specifier|public
name|void
name|testInvalidContainerReleaseRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Register node1
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|6
operator|*
name|GB
argument_list|)
decl_stmt|;
comment|// Submit an application
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
comment|// kick the scheduling
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
name|am1
operator|.
name|addRequests
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.1"
block|}
argument_list|,
name|GB
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|AllocateResponse
name|alloc1Response
init|=
name|am1
operator|.
name|schedule
argument_list|()
decl_stmt|;
comment|// send the request
comment|// kick the scheduler
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|alloc1Response
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
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|alloc1Response
operator|=
name|am1
operator|.
name|schedule
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|RMApp
name|app2
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|nm1
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
comment|// Now trying to release container allocated for app1 -> appAttempt1.
name|ContainerId
name|cId
init|=
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
decl_stmt|;
name|am2
operator|.
name|addContainerToBeReleased
argument_list|(
name|cId
argument_list|)
expr_stmt|;
try|try
block|{
name|am2
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception was expected!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidContainerReleaseException
name|e
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Cannot release container : "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|cId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" not belonging to this application attempt : "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1200000
argument_list|)
DECL|method|testProgressFilter ()
specifier|public
name|void
name|testProgressFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Register node1
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|6
operator|*
name|GB
argument_list|)
decl_stmt|;
comment|// Submit an application
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
name|AllocateRequestPBImpl
name|allocateRequest
init|=
operator|new
name|AllocateRequestPBImpl
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|release
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
name|allocateRequest
operator|.
name|setReleaseList
argument_list|(
name|release
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setAskList
argument_list|(
name|ask
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setProgress
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
while|while
condition|(
name|attempt1
operator|.
name|getProgress
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for allocate event to be handled ..."
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|allocateRequest
operator|.
name|setProgress
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
while|while
condition|(
name|attempt1
operator|.
name|getProgress
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for allocate event to be handled ..."
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|allocateRequest
operator|.
name|setProgress
argument_list|(
operator|(
name|float
operator|)
literal|9
argument_list|)
expr_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
while|while
condition|(
name|attempt1
operator|.
name|getProgress
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for allocate event to be handled ..."
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|allocateRequest
operator|.
name|setProgress
argument_list|(
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
while|while
condition|(
name|attempt1
operator|.
name|getProgress
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for allocate event to be handled ..."
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|allocateRequest
operator|.
name|setProgress
argument_list|(
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
while|while
condition|(
name|attempt1
operator|.
name|getProgress
argument_list|()
operator|!=
literal|0.5
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for allocate event to be handled ..."
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|allocateRequest
operator|.
name|setProgress
argument_list|(
operator|(
name|float
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
while|while
condition|(
name|attempt1
operator|.
name|getProgress
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for allocate event to be handled ..."
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1200000
argument_list|)
DECL|method|testFinishApplicationMasterBeforeRegistering ()
specifier|public
name|void
name|testFinishApplicationMasterBeforeRegistering
parameter_list|()
throws|throws
name|Exception
block|{
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Register node1
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|6
operator|*
name|GB
argument_list|)
decl_stmt|;
comment|// Submit an application
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
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAM
argument_list|(
name|app1
argument_list|,
name|rm
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|FinishApplicationMasterRequest
name|req
init|=
name|FinishApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
name|FinalApplicationStatus
operator|.
name|FAILED
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Throwable
name|cause
init|=
literal|null
decl_stmt|;
try|try
block|{
name|am1
operator|.
name|unregisterAppAttempt
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|cause
operator|=
name|e
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cause
operator|instanceof
name|ApplicationMasterNotRegisteredException
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cause
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Application Master is trying to unregister before registering for:"
argument_list|)
argument_list|)
expr_stmt|;
name|am1
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|am1
operator|.
name|unregisterAppAttempt
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|3000000
argument_list|)
DECL|method|testResourceTypes ()
specifier|public
name|void
name|testResourceTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|YarnConfiguration
argument_list|,
name|EnumSet
argument_list|<
name|SchedulerResourceTypes
argument_list|>
argument_list|>
name|driver
init|=
operator|new
name|HashMap
argument_list|<
name|YarnConfiguration
argument_list|,
name|EnumSet
argument_list|<
name|SchedulerResourceTypes
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|CapacitySchedulerConfiguration
name|csconf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|csconf
operator|.
name|setResourceComparator
argument_list|(
name|DominantResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|testCapacityDRConf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|csconf
argument_list|)
decl_stmt|;
name|testCapacityDRConf
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
name|YarnConfiguration
name|testCapacityDefConf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|testCapacityDefConf
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
name|YarnConfiguration
name|testFairDefConf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|testFairDefConf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FairScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|driver
operator|.
name|put
argument_list|(
name|conf
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|SchedulerResourceTypes
operator|.
name|MEMORY
argument_list|)
argument_list|)
expr_stmt|;
name|driver
operator|.
name|put
argument_list|(
name|testCapacityDRConf
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|SchedulerResourceTypes
operator|.
name|CPU
argument_list|,
name|SchedulerResourceTypes
operator|.
name|MEMORY
argument_list|)
argument_list|)
expr_stmt|;
name|driver
operator|.
name|put
argument_list|(
name|testCapacityDefConf
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|SchedulerResourceTypes
operator|.
name|MEMORY
argument_list|)
argument_list|)
expr_stmt|;
name|driver
operator|.
name|put
argument_list|(
name|testFairDefConf
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|SchedulerResourceTypes
operator|.
name|MEMORY
argument_list|,
name|SchedulerResourceTypes
operator|.
name|CPU
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|YarnConfiguration
argument_list|,
name|EnumSet
argument_list|<
name|SchedulerResourceTypes
argument_list|>
argument_list|>
name|entry
range|:
name|driver
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|EnumSet
argument_list|<
name|SchedulerResourceTypes
argument_list|>
name|expectedValue
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"127.0.0.1:1234"
argument_list|,
literal|6
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
name|RegisterApplicationMasterResponse
name|resp
init|=
name|am1
operator|.
name|registerAppAttempt
argument_list|()
decl_stmt|;
name|EnumSet
argument_list|<
name|SchedulerResourceTypes
argument_list|>
name|types
init|=
name|resp
operator|.
name|getSchedulerResourceTypes
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"types = "
operator|+
name|types
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedValue
argument_list|,
name|types
argument_list|)
expr_stmt|;
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

