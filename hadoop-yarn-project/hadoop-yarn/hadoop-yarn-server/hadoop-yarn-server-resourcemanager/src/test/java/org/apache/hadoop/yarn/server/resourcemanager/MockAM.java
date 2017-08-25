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
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
import|;
end_import

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
name|ArrayList
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
name|ApplicationMasterProtocol
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
name|RegisterApplicationMasterRequest
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
name|FinalApplicationStatus
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
name|Priority
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
name|api
operator|.
name|records
operator|.
name|ExecutionTypeRequest
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
name|UpdateContainerRequest
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptState
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_class
DECL|class|MockAM
specifier|public
class|class
name|MockAM
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|MockAM
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|responseId
specifier|private
specifier|volatile
name|int
name|responseId
init|=
literal|0
decl_stmt|;
DECL|field|attemptId
specifier|private
specifier|final
name|ApplicationAttemptId
name|attemptId
decl_stmt|;
DECL|field|context
specifier|private
name|RMContext
name|context
decl_stmt|;
DECL|field|amRMProtocol
specifier|private
name|ApplicationMasterProtocol
name|amRMProtocol
decl_stmt|;
DECL|field|ugi
specifier|private
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|lastResponse
specifier|private
specifier|volatile
name|AllocateResponse
name|lastResponse
decl_stmt|;
DECL|field|requests
specifier|private
specifier|final
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|releases
specifier|private
specifier|final
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releases
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MockAM (RMContext context, ApplicationMasterProtocol amRMProtocol, ApplicationAttemptId attemptId)
specifier|public
name|MockAM
parameter_list|(
name|RMContext
name|context
parameter_list|,
name|ApplicationMasterProtocol
name|amRMProtocol
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|amRMProtocol
operator|=
name|amRMProtocol
expr_stmt|;
name|this
operator|.
name|attemptId
operator|=
name|attemptId
expr_stmt|;
block|}
DECL|method|setAMRMProtocol (ApplicationMasterProtocol amRMProtocol, RMContext context)
specifier|public
name|void
name|setAMRMProtocol
parameter_list|(
name|ApplicationMasterProtocol
name|amRMProtocol
parameter_list|,
name|RMContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|amRMProtocol
operator|=
name|amRMProtocol
expr_stmt|;
block|}
comment|/**    * Wait until an attempt has reached a specified state.    * The timeout is 40 seconds.    * @param finalState the attempt state waited    * @throws InterruptedException    *         if interrupted while waiting for the state transition    */
DECL|method|waitForState (RMAppAttemptState finalState)
specifier|private
name|void
name|waitForState
parameter_list|(
name|RMAppAttemptState
name|finalState
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|RMApp
name|app
init|=
name|context
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
decl_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|app
operator|.
name|getRMAppAttempt
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
name|MockRM
operator|.
name|waitForState
argument_list|(
name|attempt
argument_list|,
name|finalState
argument_list|)
expr_stmt|;
block|}
DECL|method|registerAppAttempt ()
specifier|public
name|RegisterApplicationMasterResponse
name|registerAppAttempt
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|registerAppAttempt
argument_list|(
literal|true
argument_list|)
return|;
block|}
DECL|method|registerAppAttempt (boolean wait)
specifier|public
name|RegisterApplicationMasterResponse
name|registerAppAttempt
parameter_list|(
name|boolean
name|wait
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|wait
condition|)
block|{
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|LAUNCHED
argument_list|)
expr_stmt|;
block|}
name|responseId
operator|=
literal|0
expr_stmt|;
specifier|final
name|RegisterApplicationMasterRequest
name|req
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|req
operator|.
name|setHost
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|req
operator|.
name|setRpcPort
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|req
operator|.
name|setTrackingUrl
argument_list|(
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|ugi
operator|==
literal|null
condition|)
block|{
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|attemptId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|token
init|=
name|context
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
block|}
try|try
block|{
return|return
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|RegisterApplicationMasterResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RegisterApplicationMasterResponse
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|amRMProtocol
operator|.
name|registerApplicationMaster
argument_list|(
name|req
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|Exception
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
DECL|method|setApplicationLastResponseId (int newLastResponseId)
specifier|public
name|boolean
name|setApplicationLastResponseId
parameter_list|(
name|int
name|newLastResponseId
parameter_list|)
block|{
name|ApplicationMasterService
name|applicationMasterService
init|=
operator|(
name|ApplicationMasterService
operator|)
name|amRMProtocol
decl_stmt|;
name|responseId
operator|=
name|newLastResponseId
expr_stmt|;
return|return
name|applicationMasterService
operator|.
name|setAttemptLastResponseId
argument_list|(
name|attemptId
argument_list|,
name|newLastResponseId
argument_list|)
return|;
block|}
DECL|method|getResponseId ()
specifier|public
name|int
name|getResponseId
parameter_list|()
block|{
return|return
name|responseId
return|;
block|}
DECL|method|addRequests (String[] hosts, int memory, int priority, int containers)
specifier|public
name|void
name|addRequests
parameter_list|(
name|String
index|[]
name|hosts
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|containers
parameter_list|)
throws|throws
name|Exception
block|{
name|addRequests
argument_list|(
name|hosts
argument_list|,
name|memory
argument_list|,
name|priority
argument_list|,
name|containers
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|addRequests (String[] hosts, int memory, int priority, int containers, long allocationRequestId)
specifier|public
name|void
name|addRequests
parameter_list|(
name|String
index|[]
name|hosts
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|containers
parameter_list|,
name|long
name|allocationRequestId
parameter_list|)
throws|throws
name|Exception
block|{
name|requests
operator|.
name|addAll
argument_list|(
name|createReq
argument_list|(
name|hosts
argument_list|,
name|memory
argument_list|,
name|priority
argument_list|,
name|containers
argument_list|,
name|allocationRequestId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|schedule ()
specifier|public
name|AllocateResponse
name|schedule
parameter_list|()
throws|throws
name|Exception
block|{
name|AllocateResponse
name|response
init|=
name|allocate
argument_list|(
name|requests
argument_list|,
name|releases
argument_list|)
decl_stmt|;
name|requests
operator|.
name|clear
argument_list|()
expr_stmt|;
name|releases
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|addContainerToBeReleased (ContainerId containerId)
specifier|public
name|void
name|addContainerToBeReleased
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|releases
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
DECL|method|allocate ( String host, int memory, int numContainers, List<ContainerId> releases)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releases
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|allocate
argument_list|(
name|host
argument_list|,
name|memory
argument_list|,
name|numContainers
argument_list|,
name|releases
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|allocate ( String host, int memory, int numContainers, List<ContainerId> releases, String labelExpression)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releases
parameter_list|,
name|String
name|labelExpression
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|allocate
argument_list|(
name|host
argument_list|,
name|memory
argument_list|,
name|numContainers
argument_list|,
literal|1
argument_list|,
name|releases
argument_list|,
name|labelExpression
argument_list|)
return|;
block|}
DECL|method|allocate ( String host, int memory, int numContainers, int priority, List<ContainerId> releases, String labelExpression)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|int
name|priority
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releases
parameter_list|,
name|String
name|labelExpression
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|reqs
init|=
name|createReq
argument_list|(
operator|new
name|String
index|[]
block|{
name|host
block|}
argument_list|,
name|memory
argument_list|,
name|priority
argument_list|,
name|numContainers
argument_list|,
name|labelExpression
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
return|return
name|allocate
argument_list|(
name|reqs
argument_list|,
name|releases
argument_list|)
return|;
block|}
DECL|method|createReq (String[] hosts, int memory, int priority, int containers, long allocationRequestId)
specifier|public
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|createReq
parameter_list|(
name|String
index|[]
name|hosts
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|containers
parameter_list|,
name|long
name|allocationRequestId
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createReq
argument_list|(
name|hosts
argument_list|,
name|memory
argument_list|,
name|priority
argument_list|,
name|containers
argument_list|,
literal|null
argument_list|,
name|allocationRequestId
argument_list|)
return|;
block|}
DECL|method|createReq (String[] hosts, int memory, int priority, int containers, String labelExpression, long allocationRequestId)
specifier|public
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|createReq
parameter_list|(
name|String
index|[]
name|hosts
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|containers
parameter_list|,
name|String
name|labelExpression
parameter_list|,
name|long
name|allocationRequestId
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|reqs
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|hosts
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|host
range|:
name|hosts
control|)
block|{
comment|// only add host/rack request when asked host isn't ANY
if|if
condition|(
operator|!
name|host
operator|.
name|equals
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
condition|)
block|{
name|ResourceRequest
name|hostReq
init|=
name|createResourceReq
argument_list|(
name|host
argument_list|,
name|memory
argument_list|,
name|priority
argument_list|,
name|containers
argument_list|,
name|labelExpression
argument_list|)
decl_stmt|;
name|hostReq
operator|.
name|setAllocationRequestId
argument_list|(
name|allocationRequestId
argument_list|)
expr_stmt|;
name|reqs
operator|.
name|add
argument_list|(
name|hostReq
argument_list|)
expr_stmt|;
name|ResourceRequest
name|rackReq
init|=
name|createResourceReq
argument_list|(
literal|"/default-rack"
argument_list|,
name|memory
argument_list|,
name|priority
argument_list|,
name|containers
argument_list|,
name|labelExpression
argument_list|)
decl_stmt|;
name|rackReq
operator|.
name|setAllocationRequestId
argument_list|(
name|allocationRequestId
argument_list|)
expr_stmt|;
name|reqs
operator|.
name|add
argument_list|(
name|rackReq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ResourceRequest
name|offRackReq
init|=
name|createResourceReq
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|memory
argument_list|,
name|priority
argument_list|,
name|containers
argument_list|,
name|labelExpression
argument_list|)
decl_stmt|;
name|offRackReq
operator|.
name|setAllocationRequestId
argument_list|(
name|allocationRequestId
argument_list|)
expr_stmt|;
name|reqs
operator|.
name|add
argument_list|(
name|offRackReq
argument_list|)
expr_stmt|;
return|return
name|reqs
return|;
block|}
DECL|method|createResourceReq (String resource, int memory, int priority, int containers)
specifier|public
name|ResourceRequest
name|createResourceReq
parameter_list|(
name|String
name|resource
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|containers
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createResourceReq
argument_list|(
name|resource
argument_list|,
name|memory
argument_list|,
name|priority
argument_list|,
name|containers
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|createResourceReq (String resource, int memory, int priority, int containers, String labelExpression)
specifier|public
name|ResourceRequest
name|createResourceReq
parameter_list|(
name|String
name|resource
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|containers
parameter_list|,
name|String
name|labelExpression
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createResourceReq
argument_list|(
name|resource
argument_list|,
name|memory
argument_list|,
name|priority
argument_list|,
name|containers
argument_list|,
name|labelExpression
argument_list|,
name|ExecutionTypeRequest
operator|.
name|newInstance
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createResourceReq (String resource, int memory, int priority, int containers, String labelExpression, ExecutionTypeRequest executionTypeRequest)
specifier|public
name|ResourceRequest
name|createResourceReq
parameter_list|(
name|String
name|resource
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|priority
parameter_list|,
name|int
name|containers
parameter_list|,
name|String
name|labelExpression
parameter_list|,
name|ExecutionTypeRequest
name|executionTypeRequest
parameter_list|)
throws|throws
name|Exception
block|{
name|ResourceRequest
name|req
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|req
operator|.
name|setResourceName
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|req
operator|.
name|setNumContainers
argument_list|(
name|containers
argument_list|)
expr_stmt|;
name|Priority
name|pri
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|pri
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|req
operator|.
name|setPriority
argument_list|(
name|pri
argument_list|)
expr_stmt|;
name|Resource
name|capability
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
name|capability
operator|.
name|setMemorySize
argument_list|(
name|memory
argument_list|)
expr_stmt|;
name|req
operator|.
name|setCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
if|if
condition|(
name|labelExpression
operator|!=
literal|null
condition|)
block|{
name|req
operator|.
name|setNodeLabelExpression
argument_list|(
name|labelExpression
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|setExecutionTypeRequest
argument_list|(
name|executionTypeRequest
argument_list|)
expr_stmt|;
return|return
name|req
return|;
block|}
DECL|method|allocate ( List<ResourceRequest> resourceRequest, List<ContainerId> releases)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequest
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releases
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AllocateRequest
name|req
init|=
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0F
argument_list|,
name|resourceRequest
argument_list|,
name|releases
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|allocate
argument_list|(
name|req
argument_list|)
return|;
block|}
DECL|method|sendContainerResizingRequest ( List<UpdateContainerRequest> updateRequests)
specifier|public
name|AllocateResponse
name|sendContainerResizingRequest
parameter_list|(
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|updateRequests
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AllocateRequest
name|req
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
name|updateRequests
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|allocate
argument_list|(
name|req
argument_list|)
return|;
block|}
DECL|method|sendContainerUpdateRequest ( List<UpdateContainerRequest> updateRequests)
specifier|public
name|AllocateResponse
name|sendContainerUpdateRequest
parameter_list|(
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|updateRequests
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AllocateRequest
name|req
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
name|updateRequests
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|allocate
argument_list|(
name|req
argument_list|)
return|;
block|}
DECL|method|allocate (AllocateRequest allocateRequest)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|AllocateRequest
name|allocateRequest
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
name|context
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
name|lastResponse
operator|=
name|doAllocateAs
argument_list|(
name|ugi
argument_list|,
name|allocateRequest
argument_list|)
expr_stmt|;
return|return
name|lastResponse
return|;
block|}
DECL|method|doAllocateAs (UserGroupInformation ugi, final AllocateRequest req)
specifier|public
name|AllocateResponse
name|doAllocateAs
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
specifier|final
name|AllocateRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|req
operator|.
name|setResponseId
argument_list|(
name|responseId
argument_list|)
expr_stmt|;
try|try
block|{
name|AllocateResponse
name|response
init|=
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
name|amRMProtocol
operator|.
name|allocate
argument_list|(
name|req
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|responseId
operator|=
name|response
operator|.
name|getResponseId
argument_list|()
expr_stmt|;
return|return
name|response
return|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|Exception
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
DECL|method|doHeartbeat ()
specifier|public
name|AllocateResponse
name|doHeartbeat
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|allocate
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|unregisterAppAttempt ()
specifier|public
name|void
name|unregisterAppAttempt
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|unregisterAppAttempt
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|unregisterAppAttempt (boolean waitForStateRunning)
specifier|public
name|void
name|unregisterAppAttempt
parameter_list|(
name|boolean
name|waitForStateRunning
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|FinishApplicationMasterRequest
name|req
init|=
name|FinishApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|unregisterAppAttempt
argument_list|(
name|req
argument_list|,
name|waitForStateRunning
argument_list|)
expr_stmt|;
block|}
DECL|method|unregisterAppAttempt (final FinishApplicationMasterRequest req, boolean waitForStateRunning)
specifier|public
name|void
name|unregisterAppAttempt
parameter_list|(
specifier|final
name|FinishApplicationMasterRequest
name|req
parameter_list|,
name|boolean
name|waitForStateRunning
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|waitForStateRunning
condition|)
block|{
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ugi
operator|==
literal|null
condition|)
block|{
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|attemptId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|token
init|=
name|context
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
block|}
try|try
block|{
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|amRMProtocol
operator|.
name|finishApplicationMaster
argument_list|(
name|req
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|Exception
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|this
operator|.
name|attemptId
return|;
block|}
DECL|method|allocateAndWaitForContainers (int nContainer, int memory, MockNM nm)
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|allocateAndWaitForContainers
parameter_list|(
name|int
name|nContainer
parameter_list|,
name|int
name|memory
parameter_list|,
name|MockNM
name|nm
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|allocateAndWaitForContainers
argument_list|(
literal|"ANY"
argument_list|,
name|nContainer
argument_list|,
name|memory
argument_list|,
name|nm
argument_list|)
return|;
block|}
DECL|method|allocateAndWaitForContainers (String host, int nContainer, int memory, MockNM nm)
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|allocateAndWaitForContainers
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|nContainer
parameter_list|,
name|int
name|memory
parameter_list|,
name|MockNM
name|nm
parameter_list|)
throws|throws
name|Exception
block|{
comment|// AM request for containers
name|allocate
argument_list|(
name|host
argument_list|,
name|memory
argument_list|,
name|nContainer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// kick the scheduler
name|nm
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
name|conts
init|=
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
literal|null
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
name|nContainer
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
literal|500
argument_list|)
expr_stmt|;
block|}
return|return
name|conts
return|;
block|}
block|}
end_class

end_unit

