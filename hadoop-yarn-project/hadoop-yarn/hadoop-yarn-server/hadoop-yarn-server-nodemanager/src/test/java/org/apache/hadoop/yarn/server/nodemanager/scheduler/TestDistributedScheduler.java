begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.scheduler
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
name|nodemanager
operator|.
name|scheduler
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
name|ExecutionType
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
name|NodeId
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
name|api
operator|.
name|protocolrecords
operator|.
name|DistributedSchedulingAllocateRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|DistributedSchedulingAllocateResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterDistributedSchedulingAMResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RemoteNode
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
name|api
operator|.
name|records
operator|.
name|MasterKey
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
name|nodemanager
operator|.
name|NodeStatusUpdater
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
name|nodemanager
operator|.
name|amrmproxy
operator|.
name|RequestInterceptor
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
name|nodemanager
operator|.
name|security
operator|.
name|NMContainerTokenSecretManager
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
name|nodemanager
operator|.
name|security
operator|.
name|NMTokenSecretManagerInNM
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
name|scheduler
operator|.
name|OpportunisticContainerAllocator
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
name|util
operator|.
name|Records
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
name|Mockito
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|HashMap
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
name|Map
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

begin_comment
comment|/**  * Test cases for {@link DistributedScheduler}.  */
end_comment

begin_class
DECL|class|TestDistributedScheduler
specifier|public
class|class
name|TestDistributedScheduler
block|{
annotation|@
name|Test
DECL|method|testDistributedScheduler ()
specifier|public
name|void
name|testDistributedScheduler
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
name|DistributedScheduler
name|distributedScheduler
init|=
operator|new
name|DistributedScheduler
argument_list|()
decl_stmt|;
name|RequestInterceptor
name|finalReqIntcptr
init|=
name|setup
argument_list|(
name|conf
argument_list|,
name|distributedScheduler
argument_list|)
decl_stmt|;
name|registerAM
argument_list|(
name|distributedScheduler
argument_list|,
name|finalReqIntcptr
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|RemoteNode
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"http://a:1"
argument_list|)
argument_list|,
name|RemoteNode
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"http://b:2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|flipFlag
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|finalReqIntcptr
operator|.
name|allocateForDistributedScheduling
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|DistributedSchedulingAllocateRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|DistributedSchedulingAllocateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DistributedSchedulingAllocateResponse
name|answer
parameter_list|(
name|InvocationOnMock
name|invocationOnMock
parameter_list|)
throws|throws
name|Throwable
block|{
name|flipFlag
operator|.
name|set
argument_list|(
operator|!
name|flipFlag
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|flipFlag
operator|.
name|get
argument_list|()
condition|)
block|{
return|return
name|createAllocateResponse
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|RemoteNode
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|"http://c:3"
argument_list|)
argument_list|,
name|RemoteNode
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"d"
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|"http://d:4"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createAllocateResponse
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|RemoteNode
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"d"
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|"http://d:4"
argument_list|)
argument_list|,
name|RemoteNode
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|"http://c:3"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|AllocateRequest
name|allocateRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResourceRequest
name|guaranteedReq
init|=
name|createResourceRequest
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|,
literal|5
argument_list|,
literal|"*"
argument_list|)
decl_stmt|;
name|ResourceRequest
name|opportunisticReq
init|=
name|createResourceRequest
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|,
literal|4
argument_list|,
literal|"*"
argument_list|)
decl_stmt|;
name|allocateRequest
operator|.
name|setAskList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|guaranteedReq
argument_list|,
name|opportunisticReq
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify 4 containers were allocated
name|AllocateResponse
name|allocateResponse
init|=
name|distributedScheduler
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|allocateResponse
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify equal distribution on hosts a and b, and none on c or d
name|Map
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
name|allocs
init|=
name|mapAllocs
argument_list|(
name|allocateResponse
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"d"
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// New Allocate request
name|allocateRequest
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|opportunisticReq
operator|=
name|createResourceRequest
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|,
literal|6
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setAskList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|guaranteedReq
argument_list|,
name|opportunisticReq
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify 6 containers were allocated
name|allocateResponse
operator|=
name|distributedScheduler
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|allocateResponse
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify new containers are equally distribution on hosts c and d,
comment|// and none on a or b
name|allocs
operator|=
name|mapAllocs
argument_list|(
name|allocateResponse
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
argument_list|)
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
argument_list|,
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"d"
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure the DistributedScheduler respects the list order..
comment|// The first request should be allocated to "d" since it is ranked higher
comment|// The second request should be allocated to "c" since the ranking is
comment|// flipped on every allocate response.
name|allocateRequest
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|opportunisticReq
operator|=
name|createResourceRequest
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|,
literal|1
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setAskList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|guaranteedReq
argument_list|,
name|opportunisticReq
argument_list|)
argument_list|)
expr_stmt|;
name|allocateResponse
operator|=
name|distributedScheduler
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
name|allocs
operator|=
name|mapAllocs
argument_list|(
name|allocateResponse
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"d"
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|allocateRequest
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|opportunisticReq
operator|=
name|createResourceRequest
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|,
literal|1
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setAskList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|guaranteedReq
argument_list|,
name|opportunisticReq
argument_list|)
argument_list|)
expr_stmt|;
name|allocateResponse
operator|=
name|distributedScheduler
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
name|allocs
operator|=
name|mapAllocs
argument_list|(
name|allocateResponse
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|allocateRequest
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|opportunisticReq
operator|=
name|createResourceRequest
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|,
literal|1
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setAskList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|guaranteedReq
argument_list|,
name|opportunisticReq
argument_list|)
argument_list|)
expr_stmt|;
name|allocateResponse
operator|=
name|distributedScheduler
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
name|allocs
operator|=
name|mapAllocs
argument_list|(
name|allocateResponse
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|allocs
operator|.
name|get
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"d"
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|registerAM (DistributedScheduler distributedScheduler, RequestInterceptor finalReqIntcptr, List<RemoteNode> nodeList)
specifier|private
name|void
name|registerAM
parameter_list|(
name|DistributedScheduler
name|distributedScheduler
parameter_list|,
name|RequestInterceptor
name|finalReqIntcptr
parameter_list|,
name|List
argument_list|<
name|RemoteNode
argument_list|>
name|nodeList
parameter_list|)
throws|throws
name|Exception
block|{
name|RegisterDistributedSchedulingAMResponse
name|distSchedRegisterResponse
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterDistributedSchedulingAMResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|distSchedRegisterResponse
operator|.
name|setRegisterResponse
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterResponse
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|distSchedRegisterResponse
operator|.
name|setContainerTokenExpiryInterval
argument_list|(
literal|12345
argument_list|)
expr_stmt|;
name|distSchedRegisterResponse
operator|.
name|setContainerIdStart
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|distSchedRegisterResponse
operator|.
name|setMaxContainerResource
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|distSchedRegisterResponse
operator|.
name|setMinContainerResource
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|512
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|distSchedRegisterResponse
operator|.
name|setNodesForScheduling
argument_list|(
name|nodeList
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|finalReqIntcptr
operator|.
name|registerApplicationMasterForDistributedScheduling
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|distSchedRegisterResponse
argument_list|)
expr_stmt|;
name|distributedScheduler
operator|.
name|registerApplicationMaster
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setup (Configuration conf, DistributedScheduler distributedScheduler)
specifier|private
name|RequestInterceptor
name|setup
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DistributedScheduler
name|distributedScheduler
parameter_list|)
block|{
name|NodeStatusUpdater
name|nodeStatusUpdater
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NodeStatusUpdater
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|nodeStatusUpdater
operator|.
name|getRMIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|12345l
argument_list|)
expr_stmt|;
name|NMContainerTokenSecretManager
name|nmContainerTokenSecretManager
init|=
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|MasterKey
name|mKey
init|=
operator|new
name|MasterKey
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getKeyId
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setKeyId
parameter_list|(
name|int
name|keyId
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|ByteBuffer
name|getBytes
parameter_list|()
block|{
return|return
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|8
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBytes
parameter_list|(
name|ByteBuffer
name|bytes
parameter_list|)
block|{}
block|}
decl_stmt|;
name|nmContainerTokenSecretManager
operator|.
name|setMasterKey
argument_list|(
name|mKey
argument_list|)
expr_stmt|;
name|OpportunisticContainerAllocator
name|containerAllocator
init|=
operator|new
name|OpportunisticContainerAllocator
argument_list|(
name|nmContainerTokenSecretManager
argument_list|)
decl_stmt|;
name|NMTokenSecretManagerInNM
name|nmTokenSecretManagerInNM
init|=
operator|new
name|NMTokenSecretManagerInNM
argument_list|()
decl_stmt|;
name|nmTokenSecretManagerInNM
operator|.
name|setMasterKey
argument_list|(
name|mKey
argument_list|)
expr_stmt|;
name|distributedScheduler
operator|.
name|initLocal
argument_list|(
literal|1234
argument_list|,
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
argument_list|,
name|containerAllocator
argument_list|,
name|nmTokenSecretManagerInNM
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|RequestInterceptor
name|finalReqIntcptr
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|RequestInterceptor
operator|.
name|class
argument_list|)
decl_stmt|;
name|distributedScheduler
operator|.
name|setNextInterceptor
argument_list|(
name|finalReqIntcptr
argument_list|)
expr_stmt|;
return|return
name|finalReqIntcptr
return|;
block|}
DECL|method|createResourceRequest (ExecutionType execType, int numContainers, String resourceName)
specifier|private
name|ResourceRequest
name|createResourceRequest
parameter_list|(
name|ExecutionType
name|execType
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|String
name|resourceName
parameter_list|)
block|{
name|ResourceRequest
name|opportunisticReq
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
name|opportunisticReq
operator|.
name|setExecutionTypeRequest
argument_list|(
name|ExecutionTypeRequest
operator|.
name|newInstance
argument_list|(
name|execType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|opportunisticReq
operator|.
name|setNumContainers
argument_list|(
name|numContainers
argument_list|)
expr_stmt|;
name|opportunisticReq
operator|.
name|setCapability
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|opportunisticReq
operator|.
name|setPriority
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|opportunisticReq
operator|.
name|setRelaxLocality
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|opportunisticReq
operator|.
name|setResourceName
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
return|return
name|opportunisticReq
return|;
block|}
DECL|method|createAllocateResponse ( List<RemoteNode> nodes)
specifier|private
name|DistributedSchedulingAllocateResponse
name|createAllocateResponse
parameter_list|(
name|List
argument_list|<
name|RemoteNode
argument_list|>
name|nodes
parameter_list|)
block|{
name|DistributedSchedulingAllocateResponse
name|distSchedAllocateResponse
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|DistributedSchedulingAllocateResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|distSchedAllocateResponse
operator|.
name|setAllocateResponse
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateResponse
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|distSchedAllocateResponse
operator|.
name|setNodesForScheduling
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
return|return
name|distSchedAllocateResponse
return|;
block|}
DECL|method|mapAllocs ( AllocateResponse allocateResponse, int expectedSize)
specifier|private
name|Map
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
name|mapAllocs
parameter_list|(
name|AllocateResponse
name|allocateResponse
parameter_list|,
name|int
name|expectedSize
parameter_list|)
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|allocateResponse
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
name|allocs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Container
name|c
range|:
name|allocateResponse
operator|.
name|getAllocatedContainers
argument_list|()
control|)
block|{
name|ContainerTokenIdentifier
name|cTokId
init|=
name|BuilderUtils
operator|.
name|newContainerTokenIdentifier
argument_list|(
name|c
operator|.
name|getContainerToken
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|c
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|c
operator|.
name|getNodeId
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|,
name|cTokId
operator|.
name|getNmHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|cIds
init|=
name|allocs
operator|.
name|get
argument_list|(
name|c
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cIds
operator|==
literal|null
condition|)
block|{
name|cIds
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|allocs
operator|.
name|put
argument_list|(
name|c
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|cIds
argument_list|)
expr_stmt|;
block|}
name|cIds
operator|.
name|add
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|allocs
return|;
block|}
block|}
end_class

end_unit

