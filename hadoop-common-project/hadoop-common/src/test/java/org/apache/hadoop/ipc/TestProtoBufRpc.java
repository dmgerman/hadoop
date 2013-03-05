begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertCounterGt
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|ipc
operator|.
name|protobuf
operator|.
name|TestProtos
operator|.
name|EchoRequestProto
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
name|protobuf
operator|.
name|TestProtos
operator|.
name|EchoResponseProto
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
name|protobuf
operator|.
name|TestProtos
operator|.
name|EmptyRequestProto
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
name|protobuf
operator|.
name|TestProtos
operator|.
name|EmptyResponseProto
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
name|protobuf
operator|.
name|TestRpcServiceProtos
operator|.
name|TestProtobufRpcProto
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
name|protobuf
operator|.
name|TestRpcServiceProtos
operator|.
name|TestProtobufRpc2Proto
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|After
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|BlockingService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|RpcController
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
import|;
end_import

begin_comment
comment|/**  * Test for testing protocol buffer based RPC mechanism.  * This test depends on test.proto definition of types in src/test/proto  * and protobuf service definition from src/test/test_rpc_service.proto  */
end_comment

begin_class
DECL|class|TestProtoBufRpc
specifier|public
class|class
name|TestProtoBufRpc
block|{
DECL|field|ADDRESS
specifier|public
specifier|final
specifier|static
name|String
name|ADDRESS
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|PORT
specifier|public
specifier|final
specifier|static
name|int
name|PORT
init|=
literal|0
decl_stmt|;
DECL|field|addr
specifier|private
specifier|static
name|InetSocketAddress
name|addr
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|server
specifier|private
specifier|static
name|RPC
operator|.
name|Server
name|server
decl_stmt|;
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"testProto"
argument_list|,
name|protocolVersion
operator|=
literal|1
argument_list|)
DECL|interface|TestRpcService
specifier|public
interface|interface
name|TestRpcService
extends|extends
name|TestProtobufRpcProto
operator|.
name|BlockingInterface
block|{   }
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"testProto2"
argument_list|,
name|protocolVersion
operator|=
literal|1
argument_list|)
DECL|interface|TestRpcService2
specifier|public
interface|interface
name|TestRpcService2
extends|extends
name|TestProtobufRpc2Proto
operator|.
name|BlockingInterface
block|{   }
DECL|class|PBServerImpl
specifier|public
specifier|static
class|class
name|PBServerImpl
implements|implements
name|TestRpcService
block|{
annotation|@
name|Override
DECL|method|ping (RpcController unused, EmptyRequestProto request)
specifier|public
name|EmptyResponseProto
name|ping
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|EmptyRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|EmptyResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|echo (RpcController unused, EchoRequestProto request)
specifier|public
name|EchoResponseProto
name|echo
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|EchoRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|EchoResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMessage
argument_list|(
name|request
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|error (RpcController unused, EmptyRequestProto request)
specifier|public
name|EmptyResponseProto
name|error
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|EmptyRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"error"
argument_list|,
operator|new
name|RpcServerException
argument_list|(
literal|"error"
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|error2 (RpcController unused, EmptyRequestProto request)
specifier|public
name|EmptyResponseProto
name|error2
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|EmptyRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"error"
argument_list|,
operator|new
name|URISyntaxException
argument_list|(
literal|""
argument_list|,
literal|"testException"
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|class|PBServer2Impl
specifier|public
specifier|static
class|class
name|PBServer2Impl
implements|implements
name|TestRpcService2
block|{
annotation|@
name|Override
DECL|method|ping2 (RpcController unused, EmptyRequestProto request)
specifier|public
name|EmptyResponseProto
name|ping2
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|EmptyRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|EmptyResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|echo2 (RpcController unused, EchoRequestProto request)
specifier|public
name|EchoResponseProto
name|echo2
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|EchoRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|EchoResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMessage
argument_list|(
name|request
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Setup server for both protocols
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
comment|// Set RPC engine to protobuf RPC engine
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|TestRpcService
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Create server side implementation
name|PBServerImpl
name|serverImpl
init|=
operator|new
name|PBServerImpl
argument_list|()
decl_stmt|;
name|BlockingService
name|service
init|=
name|TestProtobufRpcProto
operator|.
name|newReflectiveBlockingService
argument_list|(
name|serverImpl
argument_list|)
decl_stmt|;
comment|// Get RPC server for server side implementation
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|TestRpcService
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|service
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|ADDRESS
argument_list|)
operator|.
name|setPort
argument_list|(
name|PORT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|addr
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
expr_stmt|;
comment|// now the second protocol
name|PBServer2Impl
name|server2Impl
init|=
operator|new
name|PBServer2Impl
argument_list|()
decl_stmt|;
name|BlockingService
name|service2
init|=
name|TestProtobufRpc2Proto
operator|.
name|newReflectiveBlockingService
argument_list|(
name|server2Impl
argument_list|)
decl_stmt|;
name|server
operator|.
name|addProtocol
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|TestRpcService2
operator|.
name|class
argument_list|,
name|service2
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
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
throws|throws
name|Exception
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getClient ()
specifier|private
specifier|static
name|TestRpcService
name|getClient
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Set RPC engine to protobuf RPC engine
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|TestRpcService
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestRpcService
operator|.
name|class
argument_list|,
literal|0
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
return|;
block|}
DECL|method|getClient2 ()
specifier|private
specifier|static
name|TestRpcService2
name|getClient2
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Set RPC engine to protobuf RPC engine
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|TestRpcService2
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestRpcService2
operator|.
name|class
argument_list|,
literal|0
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testProtoBufRpc ()
specifier|public
name|void
name|testProtoBufRpc
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRpcService
name|client
init|=
name|getClient
argument_list|()
decl_stmt|;
name|testProtoBufRpc
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
comment|// separated test out so that other tests can call it.
DECL|method|testProtoBufRpc (TestRpcService client)
specifier|public
specifier|static
name|void
name|testProtoBufRpc
parameter_list|(
name|TestRpcService
name|client
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Test ping method
name|EmptyRequestProto
name|emptyRequest
init|=
name|EmptyRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|ping
argument_list|(
literal|null
argument_list|,
name|emptyRequest
argument_list|)
expr_stmt|;
comment|// Test echo method
name|EchoRequestProto
name|echoRequest
init|=
name|EchoRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|EchoResponseProto
name|echoResponse
init|=
name|client
operator|.
name|echo
argument_list|(
literal|null
argument_list|,
name|echoRequest
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|echoResponse
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
comment|// Test error method - error should be thrown as RemoteException
try|try
block|{
name|client
operator|.
name|error
argument_list|(
literal|null
argument_list|,
name|emptyRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected exception is not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|RemoteException
name|re
init|=
operator|(
name|RemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|RpcServerException
name|rse
init|=
operator|(
name|RpcServerException
operator|)
name|re
operator|.
name|unwrapRemoteException
argument_list|(
name|RpcServerException
operator|.
name|class
argument_list|)
decl_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testProtoBufRpc2 ()
specifier|public
name|void
name|testProtoBufRpc2
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRpcService2
name|client
init|=
name|getClient2
argument_list|()
decl_stmt|;
comment|// Test ping method
name|EmptyRequestProto
name|emptyRequest
init|=
name|EmptyRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|ping2
argument_list|(
literal|null
argument_list|,
name|emptyRequest
argument_list|)
expr_stmt|;
comment|// Test echo method
name|EchoRequestProto
name|echoRequest
init|=
name|EchoRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|EchoResponseProto
name|echoResponse
init|=
name|client
operator|.
name|echo2
argument_list|(
literal|null
argument_list|,
name|echoRequest
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|echoResponse
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
comment|// Ensure RPC metrics are updated
name|MetricsRecordBuilder
name|rpcMetrics
init|=
name|getMetrics
argument_list|(
name|server
operator|.
name|getRpcMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertCounterGt
argument_list|(
literal|"RpcQueueTimeNumOps"
argument_list|,
literal|0L
argument_list|,
name|rpcMetrics
argument_list|)
expr_stmt|;
name|assertCounterGt
argument_list|(
literal|"RpcProcessingTimeNumOps"
argument_list|,
literal|0L
argument_list|,
name|rpcMetrics
argument_list|)
expr_stmt|;
name|MetricsRecordBuilder
name|rpcDetailedMetrics
init|=
name|getMetrics
argument_list|(
name|server
operator|.
name|getRpcDetailedMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertCounterGt
argument_list|(
literal|"Echo2NumOps"
argument_list|,
literal|0L
argument_list|,
name|rpcDetailedMetrics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testProtoBufRandomException ()
specifier|public
name|void
name|testProtoBufRandomException
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRpcService
name|client
init|=
name|getClient
argument_list|()
decl_stmt|;
name|EmptyRequestProto
name|emptyRequest
init|=
name|EmptyRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|client
operator|.
name|error2
argument_list|(
literal|null
argument_list|,
name|emptyRequest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|se
operator|.
name|getCause
argument_list|()
operator|instanceof
name|RemoteException
argument_list|)
expr_stmt|;
name|RemoteException
name|re
init|=
operator|(
name|RemoteException
operator|)
name|se
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|re
operator|.
name|getClassName
argument_list|()
operator|.
name|equals
argument_list|(
name|URISyntaxException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|re
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"testException"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

