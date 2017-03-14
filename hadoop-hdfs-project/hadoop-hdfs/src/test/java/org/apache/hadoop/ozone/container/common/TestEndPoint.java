begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
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
name|fs
operator|.
name|FileUtil
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|ipc
operator|.
name|RPC
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|EndpointStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|endpoint
operator|.
name|HeartbeatEndpointTask
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|endpoint
operator|.
name|RegisterEndpointTask
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|endpoint
operator|.
name|VersionEndpointTask
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerNodeIDProto
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMHeartbeatResponseProto
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMNodeReport
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMRegisteredCmdResponseProto
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMStorageReport
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionResponseProto
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|Type
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
name|ozone
operator|.
name|scm
operator|.
name|VersionInfo
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
name|test
operator|.
name|PathUtils
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
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|matchers
operator|.
name|LessOrEqual
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ReportState
operator|.
name|states
operator|.
name|noContainerReports
import|;
end_import

begin_comment
comment|/**  * Tests the endpoints.  */
end_comment

begin_class
DECL|class|TestEndPoint
specifier|public
class|class
name|TestEndPoint
block|{
DECL|field|serverAddress
specifier|private
specifier|static
name|InetSocketAddress
name|serverAddress
decl_stmt|;
DECL|field|scmServer
specifier|private
specifier|static
name|RPC
operator|.
name|Server
name|scmServer
decl_stmt|;
DECL|field|scmServerImpl
specifier|private
specifier|static
name|ScmTestMock
name|scmServerImpl
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ReportState
DECL|field|defaultReportState
name|defaultReportState
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|scmServer
operator|!=
literal|null
condition|)
block|{
name|scmServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|serverAddress
operator|=
name|SCMTestUtils
operator|.
name|getReuseableAddress
argument_list|()
expr_stmt|;
name|scmServerImpl
operator|=
operator|new
name|ScmTestMock
argument_list|()
expr_stmt|;
name|scmServer
operator|=
name|SCMTestUtils
operator|.
name|startScmRpcServer
argument_list|(
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
argument_list|,
name|scmServerImpl
argument_list|,
name|serverAddress
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|testDir
operator|=
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|TestEndPoint
operator|.
name|class
argument_list|)
expr_stmt|;
name|defaultReportState
operator|=
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ReportState
operator|.
name|newBuilder
argument_list|()
operator|.
name|setState
argument_list|(
name|noContainerReports
argument_list|)
operator|.
name|setCount
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * This test asserts that we are able to make a version call to SCM server    * and gets back the expected values.    */
DECL|method|testGetVersion ()
specifier|public
name|void
name|testGetVersion
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|EndpointStateMachine
name|rpcEndPoint
init|=
name|SCMTestUtils
operator|.
name|createEndpoint
argument_list|(
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
argument_list|,
name|serverAddress
argument_list|,
literal|1000
argument_list|)
init|)
block|{
name|SCMVersionResponseProto
name|responseProto
init|=
name|rpcEndPoint
operator|.
name|getEndPoint
argument_list|()
operator|.
name|getVersion
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseProto
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|responseProto
operator|.
name|getKeys
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|,
name|VersionInfo
operator|.
name|DESCRIPTION_KEY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|responseProto
operator|.
name|getKeys
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|VersionInfo
operator|.
name|getLatestVersion
argument_list|()
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
comment|/**    * We make getVersion RPC call, but via the VersionEndpointTask which is    * how the state machine would make the call.    */
DECL|method|testGetVersionTask ()
specifier|public
name|void
name|testGetVersionTask
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
decl_stmt|;
try|try
init|(
name|EndpointStateMachine
name|rpcEndPoint
init|=
name|SCMTestUtils
operator|.
name|createEndpoint
argument_list|(
name|conf
argument_list|,
name|serverAddress
argument_list|,
literal|1000
argument_list|)
init|)
block|{
name|rpcEndPoint
operator|.
name|setState
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|GETVERSION
argument_list|)
expr_stmt|;
name|VersionEndpointTask
name|versionTask
init|=
operator|new
name|VersionEndpointTask
argument_list|(
name|rpcEndPoint
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|EndpointStateMachine
operator|.
name|EndPointStates
name|newState
init|=
name|versionTask
operator|.
name|call
argument_list|()
decl_stmt|;
comment|// if version call worked the endpoint should automatically move to the
comment|// next state.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|REGISTER
argument_list|,
name|newState
argument_list|)
expr_stmt|;
comment|// Now rpcEndpoint should remember the version it got from SCM
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|rpcEndPoint
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
comment|/**    * This test makes a call to end point where there is no SCM server. We    * expect that versionTask should be able to handle it.    */
DECL|method|testGetVersionToInvalidEndpoint ()
specifier|public
name|void
name|testGetVersionToInvalidEndpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|nonExistentServerAddress
init|=
name|SCMTestUtils
operator|.
name|getReuseableAddress
argument_list|()
decl_stmt|;
try|try
init|(
name|EndpointStateMachine
name|rpcEndPoint
init|=
name|SCMTestUtils
operator|.
name|createEndpoint
argument_list|(
name|conf
argument_list|,
name|nonExistentServerAddress
argument_list|,
literal|1000
argument_list|)
init|)
block|{
name|rpcEndPoint
operator|.
name|setState
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|GETVERSION
argument_list|)
expr_stmt|;
name|VersionEndpointTask
name|versionTask
init|=
operator|new
name|VersionEndpointTask
argument_list|(
name|rpcEndPoint
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|EndpointStateMachine
operator|.
name|EndPointStates
name|newState
init|=
name|versionTask
operator|.
name|call
argument_list|()
decl_stmt|;
comment|// This version call did NOT work, so endpoint should remain in the same
comment|// state.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|GETVERSION
argument_list|,
name|newState
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
comment|/**    * This test makes a getVersionRPC call, but the DummyStorageServer is    * going to respond little slowly. We will assert that we are still in the    * GETVERSION state after the timeout.    */
DECL|method|testGetVersionAssertRpcTimeOut ()
specifier|public
name|void
name|testGetVersionAssertRpcTimeOut
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|rpcTimeout
init|=
literal|1000
decl_stmt|;
specifier|final
name|long
name|tolerance
init|=
literal|100
decl_stmt|;
name|Configuration
name|conf
init|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
decl_stmt|;
try|try
init|(
name|EndpointStateMachine
name|rpcEndPoint
init|=
name|SCMTestUtils
operator|.
name|createEndpoint
argument_list|(
name|conf
argument_list|,
name|serverAddress
argument_list|,
operator|(
name|int
operator|)
name|rpcTimeout
argument_list|)
init|)
block|{
name|rpcEndPoint
operator|.
name|setState
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|GETVERSION
argument_list|)
expr_stmt|;
name|VersionEndpointTask
name|versionTask
init|=
operator|new
name|VersionEndpointTask
argument_list|(
name|rpcEndPoint
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|scmServerImpl
operator|.
name|setRpcResponseDelay
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|EndpointStateMachine
operator|.
name|EndPointStates
name|newState
init|=
name|versionTask
operator|.
name|call
argument_list|()
decl_stmt|;
name|long
name|end
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|scmServerImpl
operator|.
name|setRpcResponseDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertThat
argument_list|(
name|end
operator|-
name|start
argument_list|,
operator|new
name|LessOrEqual
argument_list|<>
argument_list|(
name|rpcTimeout
operator|+
name|tolerance
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|GETVERSION
argument_list|,
name|newState
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRegister ()
specifier|public
name|void
name|testRegister
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|scmAddressArray
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
name|scmAddressArray
index|[
literal|0
index|]
operator|=
name|serverAddress
operator|.
name|toString
argument_list|()
expr_stmt|;
name|DatanodeID
name|nodeToRegister
init|=
name|SCMTestUtils
operator|.
name|getDatanodeID
argument_list|()
decl_stmt|;
try|try
init|(
name|EndpointStateMachine
name|rpcEndPoint
init|=
name|SCMTestUtils
operator|.
name|createEndpoint
argument_list|(
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
argument_list|,
name|serverAddress
argument_list|,
literal|1000
argument_list|)
init|)
block|{
name|SCMRegisteredCmdResponseProto
name|responseProto
init|=
name|rpcEndPoint
operator|.
name|getEndPoint
argument_list|()
operator|.
name|register
argument_list|(
name|nodeToRegister
argument_list|,
name|scmAddressArray
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseProto
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|responseProto
operator|.
name|getDatanodeUUID
argument_list|()
argument_list|,
name|nodeToRegister
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseProto
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|registerTaskHelper (InetSocketAddress scmAddress, int rpcTimeout, boolean clearContainerID)
specifier|private
name|EndpointStateMachine
name|registerTaskHelper
parameter_list|(
name|InetSocketAddress
name|scmAddress
parameter_list|,
name|int
name|rpcTimeout
parameter_list|,
name|boolean
name|clearContainerID
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|EndpointStateMachine
name|rpcEndPoint
init|=
name|SCMTestUtils
operator|.
name|createEndpoint
argument_list|(
name|conf
argument_list|,
name|scmAddress
argument_list|,
name|rpcTimeout
argument_list|)
decl_stmt|;
name|rpcEndPoint
operator|.
name|setState
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|REGISTER
argument_list|)
expr_stmt|;
name|RegisterEndpointTask
name|endpointTask
init|=
operator|new
name|RegisterEndpointTask
argument_list|(
name|rpcEndPoint
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clearContainerID
condition|)
block|{
name|ContainerNodeIDProto
name|containerNodeID
init|=
name|ContainerNodeIDProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterID
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setDatanodeID
argument_list|(
name|SCMTestUtils
operator|.
name|getDatanodeID
argument_list|()
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|endpointTask
operator|.
name|setContainerNodeIDProto
argument_list|(
name|containerNodeID
argument_list|)
expr_stmt|;
block|}
name|endpointTask
operator|.
name|call
argument_list|()
expr_stmt|;
return|return
name|rpcEndPoint
return|;
block|}
annotation|@
name|Test
DECL|method|testRegisterTask ()
specifier|public
name|void
name|testRegisterTask
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|EndpointStateMachine
name|rpcEndpoint
init|=
name|registerTaskHelper
argument_list|(
name|serverAddress
argument_list|,
literal|1000
argument_list|,
literal|false
argument_list|)
init|)
block|{
comment|// Successful register should move us to Heartbeat state.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|HEARTBEAT
argument_list|,
name|rpcEndpoint
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRegisterToInvalidEndpoint ()
specifier|public
name|void
name|testRegisterToInvalidEndpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|InetSocketAddress
name|address
init|=
name|SCMTestUtils
operator|.
name|getReuseableAddress
argument_list|()
decl_stmt|;
try|try
init|(
name|EndpointStateMachine
name|rpcEndpoint
init|=
name|registerTaskHelper
argument_list|(
name|address
argument_list|,
literal|1000
argument_list|,
literal|false
argument_list|)
init|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|REGISTER
argument_list|,
name|rpcEndpoint
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRegisterNoContainerID ()
specifier|public
name|void
name|testRegisterNoContainerID
parameter_list|()
throws|throws
name|Exception
block|{
name|InetSocketAddress
name|address
init|=
name|SCMTestUtils
operator|.
name|getReuseableAddress
argument_list|()
decl_stmt|;
try|try
init|(
name|EndpointStateMachine
name|rpcEndpoint
init|=
name|registerTaskHelper
argument_list|(
name|address
argument_list|,
literal|1000
argument_list|,
literal|true
argument_list|)
init|)
block|{
comment|// No Container ID, therefore we tell the datanode that we would like to
comment|// shutdown.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|SHUTDOWN
argument_list|,
name|rpcEndpoint
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRegisterRpcTimeout ()
specifier|public
name|void
name|testRegisterRpcTimeout
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|rpcTimeout
init|=
literal|1000
decl_stmt|;
specifier|final
name|long
name|tolerance
init|=
literal|200
decl_stmt|;
name|scmServerImpl
operator|.
name|setRpcResponseDelay
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|registerTaskHelper
argument_list|(
name|serverAddress
argument_list|,
literal|1000
argument_list|,
literal|false
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|end
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|scmServerImpl
operator|.
name|setRpcResponseDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertThat
argument_list|(
name|end
operator|-
name|start
argument_list|,
operator|new
name|LessOrEqual
argument_list|<>
argument_list|(
name|rpcTimeout
operator|+
name|tolerance
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHeartbeat ()
specifier|public
name|void
name|testHeartbeat
parameter_list|()
throws|throws
name|Exception
block|{
name|DatanodeID
name|dataNode
init|=
name|SCMTestUtils
operator|.
name|getDatanodeID
argument_list|()
decl_stmt|;
try|try
init|(
name|EndpointStateMachine
name|rpcEndPoint
init|=
name|SCMTestUtils
operator|.
name|createEndpoint
argument_list|(
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
argument_list|,
name|serverAddress
argument_list|,
literal|1000
argument_list|)
init|)
block|{
name|SCMNodeReport
operator|.
name|Builder
name|nrb
init|=
name|SCMNodeReport
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|SCMStorageReport
operator|.
name|Builder
name|srb
init|=
name|SCMStorageReport
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|srb
operator|.
name|setStorageUuid
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|srb
operator|.
name|setCapacity
argument_list|(
literal|2000
argument_list|)
operator|.
name|setScmUsed
argument_list|(
literal|500
argument_list|)
operator|.
name|setRemaining
argument_list|(
literal|1500
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|nrb
operator|.
name|addStorageReport
argument_list|(
name|srb
argument_list|)
expr_stmt|;
name|SCMHeartbeatResponseProto
name|responseProto
init|=
name|rpcEndPoint
operator|.
name|getEndPoint
argument_list|()
operator|.
name|sendHeartbeat
argument_list|(
name|dataNode
argument_list|,
name|nrb
operator|.
name|build
argument_list|()
argument_list|,
name|defaultReportState
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseProto
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|responseProto
operator|.
name|getCommandsCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseProto
operator|.
name|getCommandsList
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
name|responseProto
operator|.
name|getCommandsList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCmdType
argument_list|()
argument_list|,
name|Type
operator|.
name|nullCmd
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|heartbeatTaskHelper (InetSocketAddress scmAddress, int rpcTimeout)
specifier|private
name|void
name|heartbeatTaskHelper
parameter_list|(
name|InetSocketAddress
name|scmAddress
parameter_list|,
name|int
name|rpcTimeout
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create a datanode state machine for stateConext used by endpoint task
try|try
init|(
name|DatanodeStateMachine
name|stateMachine
init|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|conf
argument_list|)
init|;
name|EndpointStateMachine
name|rpcEndPoint
operator|=
name|SCMTestUtils
operator|.
name|createEndpoint
argument_list|(
name|conf
argument_list|,
name|scmAddress
argument_list|,
name|rpcTimeout
argument_list|)
init|)
block|{
name|ContainerNodeIDProto
name|containerNodeID
init|=
name|ContainerNodeIDProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterID
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setDatanodeID
argument_list|(
name|SCMTestUtils
operator|.
name|getDatanodeID
argument_list|()
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|rpcEndPoint
operator|.
name|setState
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|HEARTBEAT
argument_list|)
expr_stmt|;
specifier|final
name|StateContext
name|stateContext
init|=
operator|new
name|StateContext
argument_list|(
name|conf
argument_list|,
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|stateMachine
argument_list|)
decl_stmt|;
name|HeartbeatEndpointTask
name|endpointTask
init|=
operator|new
name|HeartbeatEndpointTask
argument_list|(
name|rpcEndPoint
argument_list|,
name|conf
argument_list|,
name|stateContext
argument_list|)
decl_stmt|;
name|endpointTask
operator|.
name|setContainerNodeIDProto
argument_list|(
name|containerNodeID
argument_list|)
expr_stmt|;
name|endpointTask
operator|.
name|call
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|endpointTask
operator|.
name|getContainerNodeIDProto
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|HEARTBEAT
argument_list|,
name|rpcEndPoint
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHeartbeatTask ()
specifier|public
name|void
name|testHeartbeatTask
parameter_list|()
throws|throws
name|Exception
block|{
name|heartbeatTaskHelper
argument_list|(
name|serverAddress
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHeartbeatTaskToInvalidNode ()
specifier|public
name|void
name|testHeartbeatTaskToInvalidNode
parameter_list|()
throws|throws
name|Exception
block|{
name|InetSocketAddress
name|invalidAddress
init|=
name|SCMTestUtils
operator|.
name|getReuseableAddress
argument_list|()
decl_stmt|;
name|heartbeatTaskHelper
argument_list|(
name|invalidAddress
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHeartbeatTaskRpcTimeOut ()
specifier|public
name|void
name|testHeartbeatTaskRpcTimeOut
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|rpcTimeout
init|=
literal|1000
decl_stmt|;
specifier|final
name|long
name|tolerance
init|=
literal|200
decl_stmt|;
name|scmServerImpl
operator|.
name|setRpcResponseDelay
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|invalidAddress
init|=
name|SCMTestUtils
operator|.
name|getReuseableAddress
argument_list|()
decl_stmt|;
name|heartbeatTaskHelper
argument_list|(
name|invalidAddress
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|scmServerImpl
operator|.
name|setRpcResponseDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertThat
argument_list|(
name|end
operator|-
name|start
argument_list|,
operator|new
name|LessOrEqual
argument_list|<>
argument_list|(
name|rpcTimeout
operator|+
name|tolerance
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

