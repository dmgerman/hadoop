begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager
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
operator|.
name|applicationsmanager
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|AllocateRequest
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
name|NodeReport
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
name|NodeState
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
name|DrainDispatcher
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
name|EventHandler
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
name|AMRMTokenIdentifier
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
name|ApplicationMasterService
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
name|MockAM
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
name|MockNM
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
name|MockRM
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
name|event
operator|.
name|SchedulerEvent
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
DECL|class|TestAMRMRPCNodeUpdates
specifier|public
class|class
name|TestAMRMRPCNodeUpdates
block|{
DECL|field|rm
specifier|private
name|MockRM
name|rm
decl_stmt|;
DECL|field|amService
name|ApplicationMasterService
name|amService
init|=
literal|null
decl_stmt|;
DECL|field|dispatcher
name|DrainDispatcher
name|dispatcher
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
block|{
name|dispatcher
operator|=
operator|new
name|DrainDispatcher
argument_list|()
expr_stmt|;
name|this
operator|.
name|rm
operator|=
operator|new
name|MockRM
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|EventHandler
argument_list|<
name|SchedulerEvent
argument_list|>
name|createSchedulerEventDispatcher
parameter_list|()
block|{
return|return
operator|new
name|SchedulerEventDispatcher
argument_list|(
name|this
operator|.
name|scheduler
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|SchedulerEvent
name|event
parameter_list|)
block|{
name|scheduler
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Dispatcher
name|createDispatcher
parameter_list|()
block|{
return|return
name|dispatcher
return|;
block|}
block|}
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|amService
operator|=
name|rm
operator|.
name|getApplicationMasterService
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
if|if
condition|(
name|rm
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|syncNodeHeartbeat (MockNM nm, boolean health)
specifier|private
name|void
name|syncNodeHeartbeat
parameter_list|(
name|MockNM
name|nm
parameter_list|,
name|boolean
name|health
parameter_list|)
throws|throws
name|Exception
block|{
name|nm
operator|.
name|nodeHeartbeat
argument_list|(
name|health
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
DECL|method|syncNodeLost (MockNM nm)
specifier|private
name|void
name|syncNodeLost
parameter_list|(
name|MockNM
name|nm
parameter_list|)
throws|throws
name|Exception
block|{
name|rm
operator|.
name|sendNodeStarted
argument_list|(
name|nm
argument_list|)
expr_stmt|;
name|rm
operator|.
name|NMwaitForState
argument_list|(
name|nm
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|NodeState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|rm
operator|.
name|sendNodeLost
argument_list|(
name|nm
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
DECL|method|allocate (final ApplicationAttemptId attemptId, final AllocateRequest req)
specifier|private
name|AllocateResponse
name|allocate
parameter_list|(
specifier|final
name|ApplicationAttemptId
name|attemptId
parameter_list|,
specifier|final
name|AllocateRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|attemptId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|token
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|attemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|getRMAppAttempt
argument_list|(
name|attemptId
argument_list|)
operator|.
name|getAMRMToken
argument_list|()
decl_stmt|;
name|ugi
operator|.
name|addTokenIdentifier
argument_list|(
name|token
operator|.
name|decodeIdentifier
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|AllocateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AllocateResponse
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|amService
operator|.
name|allocate
argument_list|(
name|req
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testAMRMUnusableNodes ()
specifier|public
name|void
name|testAMRMUnusableNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.2:1234"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|MockNM
name|nm3
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.3:1234"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|MockNM
name|nm4
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.4:1234"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
comment|// Trigger the scheduling so the AM gets 'launched' on nm1
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
comment|// register AM returns no unusable node
name|am1
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|// allocate request returns no updated node
name|AllocateRequest
name|allocateRequest1
init|=
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0F
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|AllocateResponse
name|response1
init|=
name|allocate
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|allocateRequest1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
init|=
name|response1
operator|.
name|getUpdatedNodes
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|syncNodeHeartbeat
argument_list|(
name|nm4
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// allocate request returns updated node
name|allocateRequest1
operator|=
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
name|response1
operator|.
name|getResponseId
argument_list|()
argument_list|,
literal|0F
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|response1
operator|=
name|allocate
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|allocateRequest1
argument_list|)
expr_stmt|;
name|updatedNodes
operator|=
name|response1
operator|.
name|getUpdatedNodes
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|NodeReport
name|nr
init|=
name|updatedNodes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nm4
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|nr
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|UNHEALTHY
argument_list|,
name|nr
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
comment|// resending the allocate request returns the same result
name|response1
operator|=
name|allocate
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|allocateRequest1
argument_list|)
expr_stmt|;
name|updatedNodes
operator|=
name|response1
operator|.
name|getUpdatedNodes
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|nr
operator|=
name|updatedNodes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nm4
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|nr
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|UNHEALTHY
argument_list|,
name|nr
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|syncNodeLost
argument_list|(
name|nm3
argument_list|)
expr_stmt|;
comment|// subsequent allocate request returns delta
name|allocateRequest1
operator|=
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
name|response1
operator|.
name|getResponseId
argument_list|()
argument_list|,
literal|0F
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|response1
operator|=
name|allocate
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|allocateRequest1
argument_list|)
expr_stmt|;
name|updatedNodes
operator|=
name|response1
operator|.
name|getUpdatedNodes
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|nr
operator|=
name|updatedNodes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nm3
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|nr
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|LOST
argument_list|,
name|nr
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
comment|// registering another AM gives it the complete failed list
name|RMApp
name|app2
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
comment|// Trigger nm2 heartbeat so that AM gets launched on it
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
comment|// register AM returns all unusable nodes
name|am2
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|// allocate request returns no updated node
name|AllocateRequest
name|allocateRequest2
init|=
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0F
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|AllocateResponse
name|response2
init|=
name|allocate
argument_list|(
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|allocateRequest2
argument_list|)
decl_stmt|;
name|updatedNodes
operator|=
name|response2
operator|.
name|getUpdatedNodes
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|syncNodeHeartbeat
argument_list|(
name|nm4
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// both AM's should get delta updated nodes
name|allocateRequest1
operator|=
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
name|response1
operator|.
name|getResponseId
argument_list|()
argument_list|,
literal|0F
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|response1
operator|=
name|allocate
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|allocateRequest1
argument_list|)
expr_stmt|;
name|updatedNodes
operator|=
name|response1
operator|.
name|getUpdatedNodes
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|nr
operator|=
name|updatedNodes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nm4
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|nr
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|RUNNING
argument_list|,
name|nr
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|allocateRequest2
operator|=
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
name|response2
operator|.
name|getResponseId
argument_list|()
argument_list|,
literal|0F
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|response2
operator|=
name|allocate
argument_list|(
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|allocateRequest2
argument_list|)
expr_stmt|;
name|updatedNodes
operator|=
name|response2
operator|.
name|getUpdatedNodes
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|nr
operator|=
name|updatedNodes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nm4
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|nr
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|RUNNING
argument_list|,
name|nr
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
comment|// subsequent allocate calls should return no updated nodes
name|allocateRequest2
operator|=
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
name|response2
operator|.
name|getResponseId
argument_list|()
argument_list|,
literal|0F
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|response2
operator|=
name|allocate
argument_list|(
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|allocateRequest2
argument_list|)
expr_stmt|;
name|updatedNodes
operator|=
name|response2
operator|.
name|getUpdatedNodes
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// how to do the above for LOST node
block|}
block|}
end_class

end_unit

