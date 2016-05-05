begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.local
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|local
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|isA
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|Collections
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
name|io
operator|.
name|Text
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskId
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|ClusterInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|client
operator|.
name|ClientService
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptContainerAssignedEvent
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|ContainerAllocator
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|ContainerAllocatorEvent
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|FinishApplicationMasterResponse
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
name|ApplicationId
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
name|NMToken
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
name|client
operator|.
name|ClientRMProxy
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
name|Event
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
name|exceptions
operator|.
name|YarnException
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
name|YarnRuntimeException
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
name|ipc
operator|.
name|RPCUtil
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
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_class
DECL|class|TestLocalContainerAllocator
specifier|public
class|class
name|TestLocalContainerAllocator
block|{
annotation|@
name|Test
DECL|method|testRMConnectionRetry ()
specifier|public
name|void
name|testRMConnectionRetry
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verify the connection exception is thrown
comment|// if we haven't exhausted the retry interval
name|ApplicationMasterProtocol
name|mockScheduler
init|=
name|mock
argument_list|(
name|ApplicationMasterProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockScheduler
operator|.
name|allocate
argument_list|(
name|isA
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"forcefail"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LocalContainerAllocator
name|lca
init|=
operator|new
name|StubbedLocalContainerAllocator
argument_list|(
name|mockScheduler
argument_list|)
decl_stmt|;
name|lca
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|lca
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|lca
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"heartbeat was supposed to throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
comment|// YarnException is expected
block|}
finally|finally
block|{
name|lca
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// verify YarnRuntimeException is thrown when the retry interval has expired
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_TO_RM_WAIT_INTERVAL_MS
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|lca
operator|=
operator|new
name|StubbedLocalContainerAllocator
argument_list|(
name|mockScheduler
argument_list|)
expr_stmt|;
name|lca
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|lca
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|lca
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"heartbeat was supposed to throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
comment|// YarnRuntimeException is expected
block|}
finally|finally
block|{
name|lca
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllocResponseId ()
specifier|public
name|void
name|testAllocResponseId
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationMasterProtocol
name|scheduler
init|=
operator|new
name|MockScheduler
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LocalContainerAllocator
name|lca
init|=
operator|new
name|StubbedLocalContainerAllocator
argument_list|(
name|scheduler
argument_list|)
decl_stmt|;
name|lca
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|lca
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// do two heartbeats to verify the response ID is being tracked
name|lca
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|lca
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|lca
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAMRMTokenUpdate ()
specifier|public
name|void
name|testAMRMTokenUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|AMRMTokenIdentifier
name|oldTokenId
init|=
operator|new
name|AMRMTokenIdentifier
argument_list|(
name|attemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|AMRMTokenIdentifier
name|newTokenId
init|=
operator|new
name|AMRMTokenIdentifier
argument_list|(
name|attemptId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|oldToken
init|=
operator|new
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
argument_list|(
name|oldTokenId
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"oldpassword"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|oldTokenId
operator|.
name|getKind
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|newToken
init|=
operator|new
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
argument_list|(
name|newTokenId
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"newpassword"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|newTokenId
operator|.
name|getKind
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|)
decl_stmt|;
name|MockScheduler
name|scheduler
init|=
operator|new
name|MockScheduler
argument_list|()
decl_stmt|;
name|scheduler
operator|.
name|amToken
operator|=
name|newToken
expr_stmt|;
specifier|final
name|LocalContainerAllocator
name|lca
init|=
operator|new
name|StubbedLocalContainerAllocator
argument_list|(
name|scheduler
argument_list|)
decl_stmt|;
name|lca
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|lca
operator|.
name|start
argument_list|()
expr_stmt|;
name|UserGroupInformation
name|testUgi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"someuser"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|testUgi
operator|.
name|addToken
argument_list|(
name|oldToken
argument_list|)
expr_stmt|;
name|testUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|lca
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|lca
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify there is only one AMRM token in the UGI and it matches the
comment|// updated token from the RM
name|int
name|tokenCount
init|=
literal|0
decl_stmt|;
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|ugiToken
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
range|:
name|testUgi
operator|.
name|getTokens
argument_list|()
control|)
block|{
if|if
condition|(
name|AMRMTokenIdentifier
operator|.
name|KIND_NAME
operator|.
name|equals
argument_list|(
name|token
operator|.
name|getKind
argument_list|()
argument_list|)
condition|)
block|{
name|ugiToken
operator|=
name|token
expr_stmt|;
operator|++
name|tokenCount
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"too many AMRM tokens"
argument_list|,
literal|1
argument_list|,
name|tokenCount
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
literal|"token identifier not updated"
argument_list|,
name|newToken
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|ugiToken
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
literal|"token password not updated"
argument_list|,
name|newToken
operator|.
name|getPassword
argument_list|()
argument_list|,
name|ugiToken
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"AMRM token service not updated"
argument_list|,
operator|new
name|Text
argument_list|(
name|ClientRMProxy
operator|.
name|getAMRMTokenService
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|,
name|ugiToken
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllocatedContainerResourceIsNotNull ()
specifier|public
name|void
name|testAllocatedContainerResourceIsNotNull
parameter_list|()
block|{
name|ArgumentCaptor
argument_list|<
name|TaskAttemptContainerAssignedEvent
argument_list|>
name|containerAssignedCaptor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|TaskAttemptContainerAssignedEvent
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|EventHandler
argument_list|<
name|Event
argument_list|>
name|eventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|AppContext
name|context
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|eventHandler
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|fromString
argument_list|(
literal|"container_1427562107907_0002_01_000001"
argument_list|)
decl_stmt|;
name|LocalContainerAllocator
name|containerAllocator
init|=
operator|new
name|LocalContainerAllocator
argument_list|(
name|mock
argument_list|(
name|ClientService
operator|.
name|class
argument_list|)
argument_list|,
name|context
argument_list|,
literal|"localhost"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|containerId
argument_list|)
decl_stmt|;
name|ContainerAllocatorEvent
name|containerAllocatorEvent
init|=
name|createContainerRequestEvent
argument_list|()
decl_stmt|;
name|containerAllocator
operator|.
name|handle
argument_list|(
name|containerAllocatorEvent
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|eventHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|containerAssignedCaptor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|Container
name|container
init|=
name|containerAssignedCaptor
operator|.
name|getValue
argument_list|()
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|Resource
name|containerResource
init|=
name|container
operator|.
name|getResource
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|containerResource
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerResource
operator|.
name|getMemory
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerResource
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|createContainerRequestEvent ()
specifier|private
specifier|static
name|ContainerAllocatorEvent
name|createContainerRequestEvent
parameter_list|()
block|{
name|TaskAttemptId
name|taskAttemptId
init|=
name|mock
argument_list|(
name|TaskAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|TaskId
name|taskId
init|=
name|mock
argument_list|(
name|TaskId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|taskAttemptId
operator|.
name|getTaskId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
return|return
operator|new
name|ContainerAllocatorEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|ContainerAllocator
operator|.
name|EventType
operator|.
name|CONTAINER_REQ
argument_list|)
return|;
block|}
DECL|class|StubbedLocalContainerAllocator
specifier|private
specifier|static
class|class
name|StubbedLocalContainerAllocator
extends|extends
name|LocalContainerAllocator
block|{
DECL|field|scheduler
specifier|private
name|ApplicationMasterProtocol
name|scheduler
decl_stmt|;
DECL|method|StubbedLocalContainerAllocator (ApplicationMasterProtocol scheduler)
specifier|public
name|StubbedLocalContainerAllocator
parameter_list|(
name|ApplicationMasterProtocol
name|scheduler
parameter_list|)
block|{
name|super
argument_list|(
name|mock
argument_list|(
name|ClientService
operator|.
name|class
argument_list|)
argument_list|,
name|createAppContext
argument_list|()
argument_list|,
literal|"nmhost"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|register ()
specifier|protected
name|void
name|register
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|unregister ()
specifier|protected
name|void
name|unregister
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|startAllocatorThread ()
specifier|protected
name|void
name|startAllocatorThread
parameter_list|()
block|{
name|allocatorThread
operator|=
operator|new
name|Thread
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSchedulerProxy ()
specifier|protected
name|ApplicationMasterProtocol
name|createSchedulerProxy
parameter_list|()
block|{
return|return
name|scheduler
return|;
block|}
DECL|method|createAppContext ()
specifier|private
specifier|static
name|AppContext
name|createAppContext
parameter_list|()
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|mock
argument_list|(
name|Job
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|EventHandler
name|eventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|AppContext
name|ctx
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getApplicationID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getJob
argument_list|(
name|isA
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getClusterInfo
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ClusterInfo
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|10240
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|eventHandler
argument_list|)
expr_stmt|;
return|return
name|ctx
return|;
block|}
block|}
DECL|class|MockScheduler
specifier|private
specifier|static
class|class
name|MockScheduler
implements|implements
name|ApplicationMasterProtocol
block|{
DECL|field|responseId
name|int
name|responseId
init|=
literal|0
decl_stmt|;
DECL|field|amToken
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|amToken
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|registerApplicationMaster ( RegisterApplicationMasterRequest request)
specifier|public
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|finishApplicationMaster ( FinishApplicationMasterRequest request)
specifier|public
name|FinishApplicationMasterResponse
name|finishApplicationMaster
parameter_list|(
name|FinishApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|allocate (AllocateRequest request)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"response ID mismatch"
argument_list|,
name|responseId
argument_list|,
name|request
operator|.
name|getResponseId
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|responseId
expr_stmt|;
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
name|Token
name|yarnToken
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|amToken
operator|!=
literal|null
condition|)
block|{
name|yarnToken
operator|=
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
name|Token
operator|.
name|newInstance
argument_list|(
name|amToken
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|amToken
operator|.
name|getKind
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|amToken
operator|.
name|getPassword
argument_list|()
argument_list|,
name|amToken
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|AllocateResponse
name|response
init|=
name|AllocateResponse
operator|.
name|newInstance
argument_list|(
name|responseId
argument_list|,
name|Collections
operator|.
expr|<
name|ContainerStatus
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Container
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|NodeReport
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
expr|<
name|NMToken
operator|>
name|emptyList
argument_list|()
argument_list|,
name|yarnToken
argument_list|,
name|Collections
operator|.
expr|<
name|Container
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Container
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|.
name|setApplicationPriority
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
block|}
block|}
end_class

end_unit

