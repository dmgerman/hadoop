begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|ipc
operator|.
name|Server
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
name|net
operator|.
name|NetUtils
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
name|SecurityUtil
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
name|ApplicationClientProtocol
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
name|ContainerManagementProtocol
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
name|ContainerManagementProtocolPB
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
name|IncreaseContainersResourceRequest
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
name|IncreaseContainersResourceResponse
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
name|GetContainerStatusesRequest
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
name|GetContainerStatusesResponse
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
name|GetNewApplicationRequest
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
name|SignalContainerRequest
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
name|SignalContainerResponse
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
name|StartContainerRequest
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
name|StartContainersRequest
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
name|StartContainersResponse
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
name|StopContainersRequest
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
name|StopContainersResponse
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
name|ContainerLaunchContext
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
name|factories
operator|.
name|RecordFactory
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
name|ipc
operator|.
name|HadoopYarnProtoRPC
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
name|ipc
operator|.
name|YarnRPC
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
name|CollectorNodemanagerProtocol
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
name|GetTimelineCollectorContextRequest
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
name|GetTimelineCollectorContextResponse
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
name|ReportNewCollectorInfoRequest
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
name|ReportNewCollectorInfoResponse
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
name|AppCollectorsMap
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
DECL|class|TestRPC
specifier|public
class|class
name|TestRPC
block|{
DECL|field|EXCEPTION_MSG
specifier|private
specifier|static
specifier|final
name|String
name|EXCEPTION_MSG
init|=
literal|"test error"
decl_stmt|;
DECL|field|EXCEPTION_CAUSE
specifier|private
specifier|static
specifier|final
name|String
name|EXCEPTION_CAUSE
init|=
literal|"exception cause"
decl_stmt|;
DECL|field|RECORD_FACTORY
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|RECORD_FACTORY
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|ILLEGAL_NUMBER_MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|ILLEGAL_NUMBER_MESSAGE
init|=
literal|"collectors' number in ReportNewCollectorInfoRequest is not ONE."
decl_stmt|;
DECL|field|DEFAULT_COLLECTOR_ADDR
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_COLLECTOR_ADDR
init|=
literal|"localhost:0"
decl_stmt|;
DECL|field|DEFAULT_APP_ID
specifier|public
specifier|static
specifier|final
name|ApplicationId
name|DEFAULT_APP_ID
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testUnknownCall ()
specifier|public
name|void
name|testUnknownCall
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_RPC_IMPL
argument_list|,
name|HadoopYarnProtoRPC
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|bindAddr
init|=
literal|"localhost:0"
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|bindAddr
argument_list|)
decl_stmt|;
name|Server
name|server
init|=
name|rpc
operator|.
name|getServer
argument_list|(
name|ContainerManagementProtocol
operator|.
name|class
argument_list|,
operator|new
name|DummyContainerManager
argument_list|()
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Any unrelated protocol would do
name|ApplicationClientProtocol
name|proxy
init|=
operator|(
name|ApplicationClientProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ApplicationClientProtocol
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|proxy
operator|.
name|getNewApplication
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|GetNewApplicationRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Excepted RPC call to fail with unknown method."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|matches
argument_list|(
literal|"Unknown method getNewApplication called on.*"
operator|+
literal|"org.apache.hadoop.yarn.proto.ApplicationClientProtocol"
operator|+
literal|"\\$ApplicationClientProtocolService\\$BlockingInterface "
operator|+
literal|"protocol."
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRPCOnCollectorNodeManagerProtocol ()
specifier|public
name|void
name|testRPCOnCollectorNodeManagerProtocol
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_RPC_IMPL
argument_list|,
name|HadoopYarnProtoRPC
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|bindAddr
init|=
literal|"localhost:0"
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|bindAddr
argument_list|)
decl_stmt|;
name|Server
name|server
init|=
name|rpc
operator|.
name|getServer
argument_list|(
name|CollectorNodemanagerProtocol
operator|.
name|class
argument_list|,
operator|new
name|DummyNMCollectorService
argument_list|()
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Test unrelated protocol wouldn't get response
name|ApplicationClientProtocol
name|unknownProxy
init|=
operator|(
name|ApplicationClientProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ApplicationClientProtocol
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|unknownProxy
operator|.
name|getNewApplication
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|GetNewApplicationRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Excepted RPC call to fail with unknown method."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|matches
argument_list|(
literal|"Unknown method getNewApplication called on.*"
operator|+
literal|"org.apache.hadoop.yarn.proto.ApplicationClientProtocol"
operator|+
literal|"\\$ApplicationClientProtocolService\\$BlockingInterface "
operator|+
literal|"protocol."
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Test CollectorNodemanagerProtocol get proper response
name|CollectorNodemanagerProtocol
name|proxy
init|=
operator|(
name|CollectorNodemanagerProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|CollectorNodemanagerProtocol
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Verify request with DEFAULT_APP_ID and DEFAULT_COLLECTOR_ADDR get
comment|// normally response.
try|try
block|{
name|ReportNewCollectorInfoRequest
name|request
init|=
name|ReportNewCollectorInfoRequest
operator|.
name|newInstance
argument_list|(
name|DEFAULT_APP_ID
argument_list|,
name|DEFAULT_COLLECTOR_ADDR
argument_list|)
decl_stmt|;
name|proxy
operator|.
name|reportNewCollectorInfo
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"RPC call failured is not expected here."
argument_list|)
expr_stmt|;
block|}
comment|// Verify empty request get YarnException back (by design in
comment|// DummyNMCollectorService)
try|try
block|{
name|proxy
operator|.
name|reportNewCollectorInfo
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|ReportNewCollectorInfoRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Excepted RPC call to fail with YarnException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
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
name|ILLEGAL_NUMBER_MESSAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Verify request with a valid app ID
try|try
block|{
name|GetTimelineCollectorContextRequest
name|request
init|=
name|GetTimelineCollectorContextRequest
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|GetTimelineCollectorContextResponse
name|response
init|=
name|proxy
operator|.
name|getTimelineCollectorContext
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test_user_id"
argument_list|,
name|response
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test_flow_name"
argument_list|,
name|response
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test_flow_version"
argument_list|,
name|response
operator|.
name|getFlowVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|12345678L
argument_list|,
name|response
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"RPC call failured is not expected here."
argument_list|)
expr_stmt|;
block|}
comment|// Verify request with an invalid app ID
try|try
block|{
name|GetTimelineCollectorContextRequest
name|request
init|=
name|GetTimelineCollectorContextRequest
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|proxy
operator|.
name|getTimelineCollectorContext
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"RPC call failured is expected here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|YarnException
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
literal|"The application is not found."
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHadoopProtoRPC ()
specifier|public
name|void
name|testHadoopProtoRPC
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
name|HadoopYarnProtoRPC
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test (String rpcClass)
specifier|private
name|void
name|test
parameter_list|(
name|String
name|rpcClass
parameter_list|)
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_RPC_IMPL
argument_list|,
name|rpcClass
argument_list|)
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|bindAddr
init|=
literal|"localhost:0"
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|bindAddr
argument_list|)
decl_stmt|;
name|Server
name|server
init|=
name|rpc
operator|.
name|getServer
argument_list|(
name|ContainerManagementProtocol
operator|.
name|class
argument_list|,
operator|new
name|DummyContainerManager
argument_list|()
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|ContainerManagementProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|ContainerManagementProtocol
name|proxy
init|=
operator|(
name|ContainerManagementProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ContainerManagementProtocol
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|RECORD_FACTORY
operator|.
name|newRecordInstance
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
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
literal|0
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|applicationAttemptId
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1234
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ContainerTokenIdentifier
name|containerTokenIdentifier
init|=
operator|new
name|ContainerTokenIdentifier
argument_list|(
name|containerId
argument_list|,
literal|"localhost"
argument_list|,
literal|"user"
argument_list|,
name|resource
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000
argument_list|,
literal|42
argument_list|,
literal|42
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Token
name|containerToken
init|=
name|newContainerToken
argument_list|(
name|nodeId
argument_list|,
literal|"password"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|containerTokenIdentifier
argument_list|)
decl_stmt|;
name|StartContainerRequest
name|scRequest
init|=
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|containerLaunchContext
argument_list|,
name|containerToken
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StartContainerRequest
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|StartContainerRequest
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|scRequest
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|StartContainersRequest
operator|.
name|newInstance
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|proxy
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|containerIds
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|GetContainerStatusesRequest
name|gcsRequest
init|=
name|GetContainerStatusesRequest
operator|.
name|newInstance
argument_list|(
name|containerIds
argument_list|)
decl_stmt|;
name|GetContainerStatusesResponse
name|response
init|=
name|proxy
operator|.
name|getContainerStatuses
argument_list|(
name|gcsRequest
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|statuses
init|=
name|response
operator|.
name|getContainerStatuses
argument_list|()
decl_stmt|;
comment|//test remote exception
name|boolean
name|exception
init|=
literal|false
decl_stmt|;
try|try
block|{
name|StopContainersRequest
name|stopRequest
init|=
name|RECORD_FACTORY
operator|.
name|newRecordInstance
argument_list|(
name|StopContainersRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|stopRequest
operator|.
name|setContainerIds
argument_list|(
name|containerIds
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|stopContainers
argument_list|(
name|stopRequest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|exception
operator|=
literal|true
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
name|EXCEPTION_MSG
argument_list|)
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
name|EXCEPTION_CAUSE
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test Exception is "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|exception
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|statuses
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
name|ContainerState
operator|.
name|RUNNING
argument_list|,
name|statuses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|DummyContainerManager
specifier|public
class|class
name|DummyContainerManager
implements|implements
name|ContainerManagementProtocol
block|{
DECL|field|statuses
specifier|private
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|statuses
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getContainerStatuses ( GetContainerStatusesRequest request)
specifier|public
name|GetContainerStatusesResponse
name|getContainerStatuses
parameter_list|(
name|GetContainerStatusesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
block|{
name|GetContainerStatusesResponse
name|response
init|=
name|RECORD_FACTORY
operator|.
name|newRecordInstance
argument_list|(
name|GetContainerStatusesResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContainerStatuses
argument_list|(
name|statuses
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|startContainers ( StartContainersRequest requests)
specifier|public
name|StartContainersResponse
name|startContainers
parameter_list|(
name|StartContainersRequest
name|requests
parameter_list|)
throws|throws
name|YarnException
block|{
name|StartContainersResponse
name|response
init|=
name|RECORD_FACTORY
operator|.
name|newRecordInstance
argument_list|(
name|StartContainersResponse
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|StartContainerRequest
name|request
range|:
name|requests
operator|.
name|getStartContainerRequests
argument_list|()
control|)
block|{
name|Token
name|containerToken
init|=
name|request
operator|.
name|getContainerToken
argument_list|()
decl_stmt|;
name|ContainerTokenIdentifier
name|tokenId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|tokenId
operator|=
name|newContainerTokenIdentifier
argument_list|(
name|containerToken
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|ContainerStatus
name|status
init|=
name|RECORD_FACTORY
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
name|setState
argument_list|(
name|ContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|status
operator|.
name|setContainerId
argument_list|(
name|tokenId
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|.
name|setExitStatus
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|statuses
operator|.
name|add
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|stopContainers (StopContainersRequest request)
specifier|public
name|StopContainersResponse
name|stopContainers
parameter_list|(
name|StopContainersRequest
name|request
parameter_list|)
throws|throws
name|YarnException
block|{
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|(
name|EXCEPTION_MSG
argument_list|,
operator|new
name|Exception
argument_list|(
name|EXCEPTION_CAUSE
argument_list|)
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|increaseContainersResource ( IncreaseContainersResourceRequest request)
specifier|public
name|IncreaseContainersResourceResponse
name|increaseContainersResource
parameter_list|(
name|IncreaseContainersResourceRequest
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
DECL|method|signalToContainer ( SignalContainerRequest request)
specifier|public
name|SignalContainerResponse
name|signalToContainer
parameter_list|(
name|SignalContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnException
block|{
specifier|final
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|(
name|EXCEPTION_MSG
argument_list|,
operator|new
name|Exception
argument_list|(
name|EXCEPTION_CAUSE
argument_list|)
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|newContainerTokenIdentifier ( Token containerToken)
specifier|public
specifier|static
name|ContainerTokenIdentifier
name|newContainerTokenIdentifier
parameter_list|(
name|Token
name|containerToken
parameter_list|)
throws|throws
name|IOException
block|{
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
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
name|token
init|=
operator|new
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
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
argument_list|(
name|containerToken
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|containerToken
operator|.
name|getPassword
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|containerToken
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|containerToken
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|token
operator|.
name|decodeIdentifier
argument_list|()
return|;
block|}
DECL|method|newContainerToken (NodeId nodeId, byte[] password, ContainerTokenIdentifier tokenIdentifier)
specifier|public
specifier|static
name|Token
name|newContainerToken
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|byte
index|[]
name|password
parameter_list|,
name|ContainerTokenIdentifier
name|tokenIdentifier
parameter_list|)
block|{
comment|// RPC layer client expects ip:port as service for tokens
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|,
name|nodeId
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
comment|// NOTE: use SecurityUtil.setTokenService if this becomes a "real" token
name|Token
name|containerToken
init|=
name|Token
operator|.
name|newInstance
argument_list|(
name|tokenIdentifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|ContainerTokenIdentifier
operator|.
name|KIND
operator|.
name|toString
argument_list|()
argument_list|,
name|password
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|addr
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|containerToken
return|;
block|}
comment|// A dummy implementation for CollectorNodemanagerProtocol for test purpose,
comment|// it only can accept one appID, collectorAddr pair or throw exceptions
DECL|class|DummyNMCollectorService
specifier|public
class|class
name|DummyNMCollectorService
implements|implements
name|CollectorNodemanagerProtocol
block|{
annotation|@
name|Override
DECL|method|reportNewCollectorInfo ( ReportNewCollectorInfoRequest request)
specifier|public
name|ReportNewCollectorInfoResponse
name|reportNewCollectorInfo
parameter_list|(
name|ReportNewCollectorInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|AppCollectorsMap
argument_list|>
name|appCollectors
init|=
name|request
operator|.
name|getAppCollectorsList
argument_list|()
decl_stmt|;
if|if
condition|(
name|appCollectors
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// check default appID and collectorAddr
name|AppCollectorsMap
name|appCollector
init|=
name|appCollectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appCollector
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|DEFAULT_APP_ID
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appCollector
operator|.
name|getCollectorAddr
argument_list|()
argument_list|,
name|DEFAULT_COLLECTOR_ADDR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|ILLEGAL_NUMBER_MESSAGE
argument_list|)
throw|;
block|}
name|ReportNewCollectorInfoResponse
name|response
init|=
name|RECORD_FACTORY
operator|.
name|newRecordInstance
argument_list|(
name|ReportNewCollectorInfoResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getTimelineCollectorContext ( GetTimelineCollectorContextRequest request)
specifier|public
name|GetTimelineCollectorContextResponse
name|getTimelineCollectorContext
parameter_list|(
name|GetTimelineCollectorContextRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|request
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|GetTimelineCollectorContextResponse
operator|.
name|newInstance
argument_list|(
literal|"test_user_id"
argument_list|,
literal|"test_flow_name"
argument_list|,
literal|"test_flow_version"
argument_list|,
literal|12345678L
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"The application is not found."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

