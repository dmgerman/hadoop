begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
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
name|api
operator|.
name|protocolrecords
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|DataOutputBuffer
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
name|security
operator|.
name|Credentials
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
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenIdentifier
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
name|ContainerExitStatus
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
name|impl
operator|.
name|pb
operator|.
name|ContainerStatusPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|NMContainerStatusPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|NodeHeartbeatRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|NodeHeartbeatResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RegisterNodeManagerRequestPBImpl
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
name|NodeStatus
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
name|QueuedContainersStatus
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

begin_class
DECL|class|TestProtocolRecords
specifier|public
class|class
name|TestProtocolRecords
block|{
annotation|@
name|Test
DECL|method|testNMContainerStatus ()
specifier|public
name|void
name|testNMContainerStatus
parameter_list|()
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|123456789
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
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|attemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1000
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|NMContainerStatus
name|report
init|=
name|NMContainerStatus
operator|.
name|newInstance
argument_list|(
name|containerId
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|,
name|resource
argument_list|,
literal|"diagnostics"
argument_list|,
name|ContainerExitStatus
operator|.
name|ABORTED
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|NMContainerStatus
name|reportProto
init|=
operator|new
name|NMContainerStatusPBImpl
argument_list|(
operator|(
operator|(
name|NMContainerStatusPBImpl
operator|)
name|report
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"diagnostics"
argument_list|,
name|reportProto
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resource
argument_list|,
name|reportProto
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerExitStatus
operator|.
name|ABORTED
argument_list|,
name|reportProto
operator|.
name|getContainerExitStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerState
operator|.
name|COMPLETE
argument_list|,
name|reportProto
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId
argument_list|,
name|reportProto
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|10
argument_list|)
argument_list|,
name|reportProto
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1234
argument_list|,
name|reportProto
operator|.
name|getCreationTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegisterNodeManagerRequest ()
specifier|public
name|void
name|testRegisterNodeManagerRequest
parameter_list|()
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|123456789
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
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|attemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|NMContainerStatus
name|containerReport
init|=
name|NMContainerStatus
operator|.
name|newInstance
argument_list|(
name|containerId
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"diagnostics"
argument_list|,
literal|0
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NMContainerStatus
argument_list|>
name|reports
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|containerReport
argument_list|)
decl_stmt|;
name|RegisterNodeManagerRequest
name|request
init|=
name|RegisterNodeManagerRequest
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"1.1.1.1"
argument_list|,
literal|1000
argument_list|)
argument_list|,
literal|8080
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"NM-version-id"
argument_list|,
name|reports
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|appId
argument_list|)
argument_list|)
decl_stmt|;
name|RegisterNodeManagerRequest
name|requestProto
init|=
operator|new
name|RegisterNodeManagerRequestPBImpl
argument_list|(
operator|(
operator|(
name|RegisterNodeManagerRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerReport
argument_list|,
name|requestProto
operator|.
name|getNMContainerStatuses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8080
argument_list|,
name|requestProto
operator|.
name|getHttpPort
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"NM-version-id"
argument_list|,
name|requestProto
operator|.
name|getNMVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"1.1.1.1"
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|requestProto
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|requestProto
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|requestProto
operator|.
name|getRunningApplications
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
name|appId
argument_list|,
name|requestProto
operator|.
name|getRunningApplications
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeHeartBeatResponse ()
specifier|public
name|void
name|testNodeHeartBeatResponse
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeHeartbeatResponse
name|record
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeHeartbeatResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|appCredentials
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
decl_stmt|;
name|Credentials
name|app1Cred
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token1
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|token1
operator|.
name|setKind
argument_list|(
operator|new
name|Text
argument_list|(
literal|"kind1"
argument_list|)
argument_list|)
expr_stmt|;
name|app1Cred
operator|.
name|addToken
argument_list|(
operator|new
name|Text
argument_list|(
literal|"token1"
argument_list|)
argument_list|,
name|token1
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token2
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|token2
operator|.
name|setKind
argument_list|(
operator|new
name|Text
argument_list|(
literal|"kind2"
argument_list|)
argument_list|)
expr_stmt|;
name|app1Cred
operator|.
name|addToken
argument_list|(
operator|new
name|Text
argument_list|(
literal|"token2"
argument_list|)
argument_list|,
name|token2
argument_list|)
expr_stmt|;
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|app1Cred
operator|.
name|writeTokenStorageToStream
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|ByteBuffer
name|byteBuffer1
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|appCredentials
operator|.
name|put
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234
argument_list|,
literal|1
argument_list|)
argument_list|,
name|byteBuffer1
argument_list|)
expr_stmt|;
name|record
operator|.
name|setSystemCredentialsForApps
argument_list|(
name|appCredentials
argument_list|)
expr_stmt|;
name|NodeHeartbeatResponse
name|proto
init|=
operator|new
name|NodeHeartbeatResponsePBImpl
argument_list|(
operator|(
operator|(
name|NodeHeartbeatResponsePBImpl
operator|)
name|record
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appCredentials
argument_list|,
name|proto
operator|.
name|getSystemCredentialsForApps
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeHeartBeatRequest ()
specifier|public
name|void
name|testNodeHeartBeatRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeHeartbeatRequest
name|record
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeHeartbeatRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeStatus
name|nodeStatus
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|QueuedContainersStatus
name|queuedContainersStatus
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|QueuedContainersStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|queuedContainersStatus
operator|.
name|setEstimatedQueueWaitTime
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|queuedContainersStatus
operator|.
name|setWaitQueueLength
argument_list|(
literal|321
argument_list|)
expr_stmt|;
name|nodeStatus
operator|.
name|setQueuedContainersStatus
argument_list|(
name|queuedContainersStatus
argument_list|)
expr_stmt|;
name|record
operator|.
name|setNodeStatus
argument_list|(
name|nodeStatus
argument_list|)
expr_stmt|;
name|NodeHeartbeatRequestPBImpl
name|pb
init|=
operator|new
name|NodeHeartbeatRequestPBImpl
argument_list|(
operator|(
operator|(
name|NodeHeartbeatRequestPBImpl
operator|)
name|record
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|pb
operator|.
name|getNodeStatus
argument_list|()
operator|.
name|getQueuedContainersStatus
argument_list|()
operator|.
name|getEstimatedQueueWaitTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|321
argument_list|,
name|pb
operator|.
name|getNodeStatus
argument_list|()
operator|.
name|getQueuedContainersStatus
argument_list|()
operator|.
name|getWaitQueueLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerStatus ()
specifier|public
name|void
name|testContainerStatus
parameter_list|()
block|{
name|ContainerStatus
name|status
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ips
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"139.5.25.2"
argument_list|)
decl_stmt|;
name|status
operator|.
name|setIPs
argument_list|(
name|ips
argument_list|)
expr_stmt|;
name|status
operator|.
name|setHost
argument_list|(
literal|"locahost123"
argument_list|)
expr_stmt|;
name|ContainerStatusPBImpl
name|pb
init|=
operator|new
name|ContainerStatusPBImpl
argument_list|(
operator|(
operator|(
name|ContainerStatusPBImpl
operator|)
name|status
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ips
argument_list|,
name|pb
operator|.
name|getIPs
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"locahost123"
argument_list|,
name|pb
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|.
name|setIPs
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|status
operator|.
name|getIPs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

