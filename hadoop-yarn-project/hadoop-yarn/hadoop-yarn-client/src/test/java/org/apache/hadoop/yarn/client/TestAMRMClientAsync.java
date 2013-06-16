begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
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
name|anyFloat
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
name|anyInt
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
name|anyString
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
name|when
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|atomic
operator|.
name|AtomicInteger
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
name|AMCommand
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
name|AMRMClient
operator|.
name|ContainerRequest
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
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_class
DECL|class|TestAMRMClientAsync
specifier|public
class|class
name|TestAMRMClientAsync
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
name|TestAMRMClientAsync
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testAMRMClientAsync ()
specifier|public
name|void
name|testAMRMClientAsync
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
specifier|final
name|AtomicBoolean
name|heartbeatBlock
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completed1
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|ContainerStatus
operator|.
name|newInstance
argument_list|(
name|newContainerId
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|allocated1
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|Container
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AllocateResponse
name|response1
init|=
name|createAllocateResponse
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
argument_list|,
name|allocated1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|AllocateResponse
name|response2
init|=
name|createAllocateResponse
argument_list|(
name|completed1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|AllocateResponse
name|emptyResponse
init|=
name|createAllocateResponse
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TestCallbackHandler
name|callbackHandler
init|=
operator|new
name|TestCallbackHandler
argument_list|()
decl_stmt|;
specifier|final
name|AMRMClient
argument_list|<
name|ContainerRequest
argument_list|>
name|client
init|=
name|mock
argument_list|(
name|AMRMClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|secondHeartbeatSync
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|client
operator|.
name|allocate
argument_list|(
name|anyFloat
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response1
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|AllocateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AllocateResponse
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|secondHeartbeatSync
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
while|while
condition|(
name|heartbeatBlock
operator|.
name|get
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|heartbeatBlock
init|)
block|{
name|heartbeatBlock
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
block|}
name|secondHeartbeatSync
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|response2
return|;
block|}
block|}
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|emptyResponse
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|registerApplicationMaster
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getClusterAvailableResources
argument_list|()
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Resource
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resource
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// take client lock to simulate behavior of real impl
synchronized|synchronized
init|(
name|client
init|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|AMRMClientAsync
argument_list|<
name|ContainerRequest
argument_list|>
name|asyncClient
init|=
operator|new
name|AMRMClientAsync
argument_list|<
name|ContainerRequest
argument_list|>
argument_list|(
name|client
argument_list|,
literal|20
argument_list|,
name|callbackHandler
argument_list|)
decl_stmt|;
name|asyncClient
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|asyncClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|asyncClient
operator|.
name|registerApplicationMaster
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// while the CallbackHandler will still only be processing the first response,
comment|// heartbeater thread should still be sending heartbeats.
comment|// To test this, wait for the second heartbeat to be received.
while|while
condition|(
name|secondHeartbeatSync
operator|.
name|get
argument_list|()
operator|<
literal|1
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
comment|// heartbeat will be blocked. make sure we can call client methods at this
comment|// time. Checks that heartbeat is not holding onto client lock
assert|assert
operator|(
name|secondHeartbeatSync
operator|.
name|get
argument_list|()
operator|<
literal|2
operator|)
assert|;
name|asyncClient
operator|.
name|getClusterAvailableResources
argument_list|()
expr_stmt|;
comment|// method returned. now unblock heartbeat
assert|assert
operator|(
name|secondHeartbeatSync
operator|.
name|get
argument_list|()
operator|<
literal|2
operator|)
assert|;
synchronized|synchronized
init|(
name|heartbeatBlock
init|)
block|{
name|heartbeatBlock
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|heartbeatBlock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|// allocated containers should come before completed containers
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|callbackHandler
operator|.
name|takeCompletedContainers
argument_list|()
argument_list|)
expr_stmt|;
comment|// wait for the allocated containers from the first heartbeat's response
while|while
condition|(
name|callbackHandler
operator|.
name|takeAllocatedContainers
argument_list|()
operator|==
literal|null
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|callbackHandler
operator|.
name|takeCompletedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
comment|// wait for the completed containers from the second heartbeat's response
while|while
condition|(
name|callbackHandler
operator|.
name|takeCompletedContainers
argument_list|()
operator|==
literal|null
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|asyncClient
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|callbackHandler
operator|.
name|takeAllocatedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|callbackHandler
operator|.
name|takeCompletedContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testAMRMClientAsyncException ()
specifier|public
name|void
name|testAMRMClientAsyncException
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
name|TestCallbackHandler
name|callbackHandler
init|=
operator|new
name|TestCallbackHandler
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|AMRMClient
argument_list|<
name|ContainerRequest
argument_list|>
name|client
init|=
name|mock
argument_list|(
name|AMRMClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|exStr
init|=
literal|"TestException"
decl_stmt|;
name|YarnException
name|mockException
init|=
name|mock
argument_list|(
name|YarnException
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockException
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|exStr
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|allocate
argument_list|(
name|anyFloat
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|mockException
argument_list|)
expr_stmt|;
name|AMRMClientAsync
argument_list|<
name|ContainerRequest
argument_list|>
name|asyncClient
init|=
operator|new
name|AMRMClientAsync
argument_list|<
name|ContainerRequest
argument_list|>
argument_list|(
name|client
argument_list|,
literal|20
argument_list|,
name|callbackHandler
argument_list|)
decl_stmt|;
name|asyncClient
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|asyncClient
operator|.
name|start
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|callbackHandler
operator|.
name|notifier
init|)
block|{
name|asyncClient
operator|.
name|registerApplicationMaster
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|,
literal|null
argument_list|)
expr_stmt|;
while|while
condition|(
name|callbackHandler
operator|.
name|savedException
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|callbackHandler
operator|.
name|notifier
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|callbackHandler
operator|.
name|savedException
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|exStr
argument_list|)
argument_list|)
expr_stmt|;
name|asyncClient
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// stopping should have joined all threads and completed all callbacks
name|Assert
operator|.
name|assertTrue
argument_list|(
name|callbackHandler
operator|.
name|callbackCount
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|//(timeout=10000)
DECL|method|testAMRMClientAsyncReboot ()
specifier|public
name|void
name|testAMRMClientAsyncReboot
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
name|TestCallbackHandler
name|callbackHandler
init|=
operator|new
name|TestCallbackHandler
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|AMRMClient
argument_list|<
name|ContainerRequest
argument_list|>
name|client
init|=
name|mock
argument_list|(
name|AMRMClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|AllocateResponse
name|rebootResponse
init|=
name|createAllocateResponse
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rebootResponse
operator|.
name|setAMCommand
argument_list|(
name|AMCommand
operator|.
name|AM_RESYNC
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|allocate
argument_list|(
name|anyFloat
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rebootResponse
argument_list|)
expr_stmt|;
name|AMRMClientAsync
argument_list|<
name|ContainerRequest
argument_list|>
name|asyncClient
init|=
operator|new
name|AMRMClientAsync
argument_list|<
name|ContainerRequest
argument_list|>
argument_list|(
name|client
argument_list|,
literal|20
argument_list|,
name|callbackHandler
argument_list|)
decl_stmt|;
name|asyncClient
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|asyncClient
operator|.
name|start
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|callbackHandler
operator|.
name|notifier
init|)
block|{
name|asyncClient
operator|.
name|registerApplicationMaster
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|,
literal|null
argument_list|)
expr_stmt|;
while|while
condition|(
name|callbackHandler
operator|.
name|reboot
operator|==
literal|false
condition|)
block|{
try|try
block|{
name|callbackHandler
operator|.
name|notifier
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|asyncClient
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// stopping should have joined all threads and completed all callbacks
name|Assert
operator|.
name|assertTrue
argument_list|(
name|callbackHandler
operator|.
name|callbackCount
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|createAllocateResponse ( List<ContainerStatus> completed, List<Container> allocated, List<NMToken> nmTokens)
specifier|private
name|AllocateResponse
name|createAllocateResponse
parameter_list|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completed
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|allocated
parameter_list|,
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
parameter_list|)
block|{
name|AllocateResponse
name|response
init|=
name|AllocateResponse
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
name|completed
argument_list|,
name|allocated
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|nmTokens
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
DECL|method|newContainerId (int appId, int appAttemptId, long timestamp, int containerId)
specifier|public
specifier|static
name|ContainerId
name|newContainerId
parameter_list|(
name|int
name|appId
parameter_list|,
name|int
name|appAttemptId
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|int
name|containerId
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|timestamp
argument_list|,
name|appId
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
name|appAttemptId
argument_list|)
decl_stmt|;
return|return
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|applicationAttemptId
argument_list|,
name|containerId
argument_list|)
return|;
block|}
DECL|class|TestCallbackHandler
specifier|private
class|class
name|TestCallbackHandler
implements|implements
name|AMRMClientAsync
operator|.
name|CallbackHandler
block|{
DECL|field|completedContainers
specifier|private
specifier|volatile
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainers
decl_stmt|;
DECL|field|allocatedContainers
specifier|private
specifier|volatile
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
decl_stmt|;
DECL|field|savedException
name|Exception
name|savedException
init|=
literal|null
decl_stmt|;
DECL|field|reboot
name|boolean
name|reboot
init|=
literal|false
decl_stmt|;
DECL|field|notifier
name|Object
name|notifier
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|callbackCount
name|int
name|callbackCount
init|=
literal|0
decl_stmt|;
DECL|method|takeCompletedContainers ()
specifier|public
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|takeCompletedContainers
parameter_list|()
block|{
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|ret
init|=
name|completedContainers
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|completedContainers
operator|=
literal|null
expr_stmt|;
synchronized|synchronized
init|(
name|ret
init|)
block|{
name|ret
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|takeAllocatedContainers ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|takeAllocatedContainers
parameter_list|()
block|{
name|List
argument_list|<
name|Container
argument_list|>
name|ret
init|=
name|allocatedContainers
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|allocatedContainers
operator|=
literal|null
expr_stmt|;
synchronized|synchronized
init|(
name|ret
init|)
block|{
name|ret
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|onContainersCompleted (List<ContainerStatus> statuses)
specifier|public
name|void
name|onContainersCompleted
parameter_list|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|statuses
parameter_list|)
block|{
name|completedContainers
operator|=
name|statuses
expr_stmt|;
comment|// wait for containers to be taken before returning
synchronized|synchronized
init|(
name|completedContainers
init|)
block|{
while|while
condition|(
name|completedContainers
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|completedContainers
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupted during wait"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onContainersAllocated (List<Container> containers)
specifier|public
name|void
name|onContainersAllocated
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
block|{
name|allocatedContainers
operator|=
name|containers
expr_stmt|;
comment|// wait for containers to be taken before returning
synchronized|synchronized
init|(
name|allocatedContainers
init|)
block|{
while|while
condition|(
name|allocatedContainers
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|allocatedContainers
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupted during wait"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onShutdownRequest ()
specifier|public
name|void
name|onShutdownRequest
parameter_list|()
block|{
name|reboot
operator|=
literal|true
expr_stmt|;
synchronized|synchronized
init|(
name|notifier
init|)
block|{
name|notifier
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onNodesUpdated (List<NodeReport> updatedNodes)
specifier|public
name|void
name|onNodesUpdated
parameter_list|(
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
name|callbackCount
operator|++
expr_stmt|;
return|return
literal|0.5f
return|;
block|}
annotation|@
name|Override
DECL|method|onError (Exception e)
specifier|public
name|void
name|onError
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|savedException
operator|=
name|e
expr_stmt|;
synchronized|synchronized
init|(
name|notifier
init|)
block|{
name|notifier
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

