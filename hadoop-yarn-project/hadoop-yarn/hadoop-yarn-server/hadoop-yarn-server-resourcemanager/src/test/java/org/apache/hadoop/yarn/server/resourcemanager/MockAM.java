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

begin_class
DECL|class|MockAM
specifier|public
class|class
name|MockAM
block|{
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
specifier|final
name|RMContext
name|context
decl_stmt|;
DECL|field|amRMProtocol
specifier|private
name|ApplicationMasterProtocol
name|amRMProtocol
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
DECL|method|setAMRMProtocol (ApplicationMasterProtocol amRMProtocol)
name|void
name|setAMRMProtocol
parameter_list|(
name|ApplicationMasterProtocol
name|amRMProtocol
parameter_list|)
block|{
name|this
operator|.
name|amRMProtocol
operator|=
name|amRMProtocol
expr_stmt|;
block|}
DECL|method|waitForState (RMAppAttemptState finalState)
specifier|public
name|void
name|waitForState
parameter_list|(
name|RMAppAttemptState
name|finalState
parameter_list|)
throws|throws
name|Exception
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
name|int
name|timeoutSecs
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|finalState
operator|.
name|equals
argument_list|(
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
argument_list|)
operator|&&
name|timeoutSecs
operator|++
operator|<
literal|40
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"AppAttempt : "
operator|+
name|attemptId
operator|+
literal|" State is : "
operator|+
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
operator|+
literal|" Waiting for state : "
operator|+
name|finalState
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"AppAttempt State is : "
operator|+
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"AppAttempt state is not correct (timedout)"
argument_list|,
name|finalState
argument_list|,
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
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
name|setApplicationAttemptId
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
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
literal|1
argument_list|,
name|numContainers
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
DECL|method|createReq (String[] hosts, int memory, int priority, int containers)
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
for|for
control|(
name|String
name|host
range|:
name|hosts
control|)
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
argument_list|)
decl_stmt|;
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
argument_list|)
decl_stmt|;
name|reqs
operator|.
name|add
argument_list|(
name|rackReq
argument_list|)
expr_stmt|;
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
argument_list|)
decl_stmt|;
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
name|setMemory
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
name|attemptId
argument_list|,
operator|++
name|responseId
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
specifier|final
name|FinishApplicationMasterRequest
name|req
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|req
operator|.
name|setAppAttemptId
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|req
operator|.
name|setDiagnostics
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|req
operator|.
name|setFinalApplicationStatus
argument_list|(
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|req
operator|.
name|setTrackingUrl
argument_list|(
literal|""
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

