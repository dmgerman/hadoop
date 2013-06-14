begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|ApplicationAttemptIdPBImpl
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
name|ApplicationIdPBImpl
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
name|ContainerIdPBImpl
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|RegisterNodeManagerResponsePBImpl
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
name|api
operator|.
name|records
operator|.
name|NodeAction
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
name|NodeHealthStatus
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
name|impl
operator|.
name|pb
operator|.
name|MasterKeyPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|NodeStatusPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|SerializedExceptionPBImpl
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
comment|/**  * Simple test classes from org.apache.hadoop.yarn.server.api  */
end_comment

begin_class
DECL|class|TestYarnServerApiClasses
specifier|public
class|class
name|TestYarnServerApiClasses
block|{
DECL|field|recordFactory
specifier|private
specifier|final
specifier|static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|factories
operator|.
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|/**    * Test RegisterNodeManagerResponsePBImpl. Test getters and setters. The    * RegisterNodeManagerResponsePBImpl should generate a prototype and data    * restore from prototype    */
annotation|@
name|Test
DECL|method|testRegisterNodeManagerResponsePBImpl ()
specifier|public
name|void
name|testRegisterNodeManagerResponsePBImpl
parameter_list|()
block|{
name|RegisterNodeManagerResponsePBImpl
name|original
init|=
operator|new
name|RegisterNodeManagerResponsePBImpl
argument_list|()
decl_stmt|;
name|original
operator|.
name|setContainerTokenMasterKey
argument_list|(
name|getMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|original
operator|.
name|setNMTokenMasterKey
argument_list|(
name|getMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|original
operator|.
name|setNodeAction
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
name|original
operator|.
name|setDiagnosticsMessage
argument_list|(
literal|"testDiagnosticMessage"
argument_list|)
expr_stmt|;
name|RegisterNodeManagerResponsePBImpl
name|copy
init|=
operator|new
name|RegisterNodeManagerResponsePBImpl
argument_list|(
name|original
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|copy
operator|.
name|getContainerTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|copy
operator|.
name|getNMTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|,
name|copy
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testDiagnosticMessage"
argument_list|,
name|copy
operator|.
name|getDiagnosticsMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test NodeHeartbeatRequestPBImpl.    */
annotation|@
name|Test
DECL|method|testNodeHeartbeatRequestPBImpl ()
specifier|public
name|void
name|testNodeHeartbeatRequestPBImpl
parameter_list|()
block|{
name|NodeHeartbeatRequestPBImpl
name|original
init|=
operator|new
name|NodeHeartbeatRequestPBImpl
argument_list|()
decl_stmt|;
name|original
operator|.
name|setLastKnownContainerTokenMasterKey
argument_list|(
name|getMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|original
operator|.
name|setLastKnownNMTokenMasterKey
argument_list|(
name|getMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|original
operator|.
name|setNodeStatus
argument_list|(
name|getNodeStatus
argument_list|()
argument_list|)
expr_stmt|;
name|NodeHeartbeatRequestPBImpl
name|copy
init|=
operator|new
name|NodeHeartbeatRequestPBImpl
argument_list|(
name|original
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|copy
operator|.
name|getLastKnownContainerTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|copy
operator|.
name|getLastKnownNMTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost"
argument_list|,
name|copy
operator|.
name|getNodeStatus
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test NodeHeartbeatResponsePBImpl.    */
annotation|@
name|Test
DECL|method|testNodeHeartbeatResponsePBImpl ()
specifier|public
name|void
name|testNodeHeartbeatResponsePBImpl
parameter_list|()
block|{
name|NodeHeartbeatResponsePBImpl
name|original
init|=
operator|new
name|NodeHeartbeatResponsePBImpl
argument_list|()
decl_stmt|;
name|original
operator|.
name|setDiagnosticsMessage
argument_list|(
literal|"testDiagnosticMessage"
argument_list|)
expr_stmt|;
name|original
operator|.
name|setContainerTokenMasterKey
argument_list|(
name|getMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|original
operator|.
name|setNMTokenMasterKey
argument_list|(
name|getMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|original
operator|.
name|setNextHeartBeatInterval
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|original
operator|.
name|setNodeAction
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
name|original
operator|.
name|setResponseId
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|NodeHeartbeatResponsePBImpl
name|copy
init|=
operator|new
name|NodeHeartbeatResponsePBImpl
argument_list|(
name|original
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|copy
operator|.
name|getResponseId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|,
name|copy
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|copy
operator|.
name|getNextHeartBeatInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|copy
operator|.
name|getContainerTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|copy
operator|.
name|getNMTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testDiagnosticMessage"
argument_list|,
name|copy
operator|.
name|getDiagnosticsMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test RegisterNodeManagerRequestPBImpl.    */
annotation|@
name|Test
DECL|method|testRegisterNodeManagerRequestPBImpl ()
specifier|public
name|void
name|testRegisterNodeManagerRequestPBImpl
parameter_list|()
block|{
name|RegisterNodeManagerRequestPBImpl
name|original
init|=
operator|new
name|RegisterNodeManagerRequestPBImpl
argument_list|()
decl_stmt|;
name|original
operator|.
name|setHttpPort
argument_list|(
literal|8080
argument_list|)
expr_stmt|;
name|original
operator|.
name|setNodeId
argument_list|(
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setMemory
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setVirtualCores
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|original
operator|.
name|setResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|RegisterNodeManagerRequestPBImpl
name|copy
init|=
operator|new
name|RegisterNodeManagerRequestPBImpl
argument_list|(
name|original
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|8080
argument_list|,
name|copy
operator|.
name|getHttpPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9090
argument_list|,
name|copy
operator|.
name|getNodeId
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10000
argument_list|,
name|copy
operator|.
name|getResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|copy
operator|.
name|getResource
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test MasterKeyPBImpl.    */
annotation|@
name|Test
DECL|method|testMasterKeyPBImpl ()
specifier|public
name|void
name|testMasterKeyPBImpl
parameter_list|()
block|{
name|MasterKeyPBImpl
name|original
init|=
operator|new
name|MasterKeyPBImpl
argument_list|()
decl_stmt|;
name|original
operator|.
name|setBytes
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|original
operator|.
name|setKeyId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|MasterKeyPBImpl
name|copy
init|=
operator|new
name|MasterKeyPBImpl
argument_list|(
name|original
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|copy
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|original
operator|.
name|equals
argument_list|(
name|copy
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|original
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test SerializedExceptionPBImpl.    */
annotation|@
name|Test
DECL|method|testSerializedExceptionPBImpl ()
specifier|public
name|void
name|testSerializedExceptionPBImpl
parameter_list|()
block|{
name|SerializedExceptionPBImpl
name|original
init|=
operator|new
name|SerializedExceptionPBImpl
argument_list|()
decl_stmt|;
name|original
operator|.
name|init
argument_list|(
literal|"testMessage"
argument_list|)
expr_stmt|;
name|SerializedExceptionPBImpl
name|copy
init|=
operator|new
name|SerializedExceptionPBImpl
argument_list|(
name|original
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"testMessage"
argument_list|,
name|copy
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|original
operator|=
operator|new
name|SerializedExceptionPBImpl
argument_list|()
expr_stmt|;
name|original
operator|.
name|init
argument_list|(
literal|"testMessage"
argument_list|,
operator|new
name|Throwable
argument_list|(
operator|new
name|Throwable
argument_list|(
literal|"parent"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|copy
operator|=
operator|new
name|SerializedExceptionPBImpl
argument_list|(
name|original
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testMessage"
argument_list|,
name|copy
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"parent"
argument_list|,
name|copy
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|copy
operator|.
name|getRemoteTrace
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"java.lang.Throwable: java.lang.Throwable: parent"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test NodeStatusPBImpl.    */
annotation|@
name|Test
DECL|method|testNodeStatusPBImpl ()
specifier|public
name|void
name|testNodeStatusPBImpl
parameter_list|()
block|{
name|NodeStatusPBImpl
name|original
init|=
operator|new
name|NodeStatusPBImpl
argument_list|()
decl_stmt|;
name|original
operator|.
name|setContainersStatuses
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getContainerStatus
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
name|getContainerStatus
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|original
operator|.
name|setKeepAliveApplications
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getApplicationId
argument_list|(
literal|3
argument_list|)
argument_list|,
name|getApplicationId
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|original
operator|.
name|setNodeHealthStatus
argument_list|(
name|getNodeHealthStatus
argument_list|()
argument_list|)
expr_stmt|;
name|original
operator|.
name|setNodeId
argument_list|(
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|original
operator|.
name|setResponseId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|NodeStatusPBImpl
name|copy
init|=
operator|new
name|NodeStatusPBImpl
argument_list|(
name|original
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|copy
operator|.
name|getContainersStatuses
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getContainerId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|copy
operator|.
name|getKeepAliveApplications
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|copy
operator|.
name|getNodeHealthStatus
argument_list|()
operator|.
name|getLastHealthReportTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9090
argument_list|,
name|copy
operator|.
name|getNodeId
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|copy
operator|.
name|getResponseId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getContainerStatus (int applicationId, int containerID, int appAttemptId)
specifier|private
name|ContainerStatus
name|getContainerStatus
parameter_list|(
name|int
name|applicationId
parameter_list|,
name|int
name|containerID
parameter_list|,
name|int
name|appAttemptId
parameter_list|)
block|{
name|ContainerStatus
name|status
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|status
operator|.
name|setContainerId
argument_list|(
name|getContainerId
argument_list|(
name|containerID
argument_list|,
name|appAttemptId
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|status
return|;
block|}
DECL|method|getApplicationAttemptId (int appAttemptId)
specifier|private
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|(
name|int
name|appAttemptId
parameter_list|)
block|{
name|ApplicationAttemptId
name|result
init|=
name|ApplicationAttemptIdPBImpl
operator|.
name|newInstance
argument_list|(
name|getApplicationId
argument_list|(
name|appAttemptId
argument_list|)
argument_list|,
name|appAttemptId
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
DECL|method|getContainerId (int containerID, int appAttemptId)
specifier|private
name|ContainerId
name|getContainerId
parameter_list|(
name|int
name|containerID
parameter_list|,
name|int
name|appAttemptId
parameter_list|)
block|{
name|ContainerId
name|containerId
init|=
name|ContainerIdPBImpl
operator|.
name|newInstance
argument_list|(
name|getApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
argument_list|,
name|containerID
argument_list|)
decl_stmt|;
return|return
name|containerId
return|;
block|}
DECL|method|getApplicationId (int applicationId)
specifier|private
name|ApplicationId
name|getApplicationId
parameter_list|(
name|int
name|applicationId
parameter_list|)
block|{
name|ApplicationIdPBImpl
name|appId
init|=
operator|new
name|ApplicationIdPBImpl
argument_list|()
block|{
specifier|public
name|ApplicationIdPBImpl
name|setParameters
parameter_list|(
name|int
name|id
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|setClusterTimestamp
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
operator|.
name|setParameters
argument_list|(
name|applicationId
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
return|return
operator|new
name|ApplicationIdPBImpl
argument_list|(
name|appId
operator|.
name|getProto
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getNodeStatus ()
specifier|private
name|NodeStatus
name|getNodeStatus
parameter_list|()
block|{
name|NodeStatus
name|status
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|status
operator|.
name|setContainersStatuses
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|.
name|setKeepAliveApplications
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|.
name|setNodeHealthStatus
argument_list|(
name|getNodeHealthStatus
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|.
name|setNodeId
argument_list|(
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|.
name|setResponseId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|status
return|;
block|}
DECL|method|getNodeId ()
specifier|private
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"localhost"
argument_list|,
literal|9090
argument_list|)
return|;
block|}
DECL|method|getNodeHealthStatus ()
specifier|private
name|NodeHealthStatus
name|getNodeHealthStatus
parameter_list|()
block|{
name|NodeHealthStatus
name|healStatus
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeHealthStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|healStatus
operator|.
name|setHealthReport
argument_list|(
literal|"healthReport"
argument_list|)
expr_stmt|;
name|healStatus
operator|.
name|setIsNodeHealthy
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|healStatus
operator|.
name|setLastHealthReportTime
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
return|return
name|healStatus
return|;
block|}
DECL|method|getMasterKey ()
specifier|private
name|MasterKey
name|getMasterKey
parameter_list|()
block|{
name|MasterKey
name|key
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|MasterKey
operator|.
name|class
argument_list|)
decl_stmt|;
name|key
operator|.
name|setBytes
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|key
operator|.
name|setKeyId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|key
return|;
block|}
block|}
end_class

end_unit

