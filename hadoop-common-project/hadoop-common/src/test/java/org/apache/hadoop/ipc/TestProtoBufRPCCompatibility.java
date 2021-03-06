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
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|CommonConfigurationKeys
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
name|TestProtos
operator|.
name|OptRequestProto
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
name|OptResponseProto
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
name|OldProtobufRpcProto
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
name|NewProtobufRpcProto
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
name|NewerProtobufRpcProto
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

begin_class
DECL|class|TestProtoBufRPCCompatibility
specifier|public
class|class
name|TestProtoBufRPCCompatibility
block|{
DECL|field|ADDRESS
specifier|private
specifier|static
specifier|final
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
DECL|field|server
specifier|private
specifier|static
name|RPC
operator|.
name|Server
name|server
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
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
DECL|interface|OldRpcService
specifier|public
interface|interface
name|OldRpcService
extends|extends
name|OldProtobufRpcProto
operator|.
name|BlockingInterface
block|{   }
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"testProto"
argument_list|,
name|protocolVersion
operator|=
literal|2
argument_list|)
DECL|interface|NewRpcService
specifier|public
interface|interface
name|NewRpcService
extends|extends
name|NewProtobufRpcProto
operator|.
name|BlockingInterface
block|{   }
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"testProto"
argument_list|,
name|protocolVersion
operator|=
literal|2
argument_list|)
DECL|interface|NewerRpcService
specifier|public
interface|interface
name|NewerRpcService
extends|extends
name|NewerProtobufRpcProto
operator|.
name|BlockingInterface
block|{   }
DECL|class|OldServerImpl
specifier|public
specifier|static
class|class
name|OldServerImpl
implements|implements
name|OldRpcService
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
comment|// Ensure clientId is received
name|byte
index|[]
name|clientId
init|=
name|Server
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|Server
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|clientId
operator|.
name|length
argument_list|)
expr_stmt|;
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
DECL|method|echo (RpcController unused, EmptyRequestProto request)
specifier|public
name|EmptyResponseProto
name|echo
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
comment|// Ensure clientId is received
name|byte
index|[]
name|clientId
init|=
name|Server
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|Server
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|clientId
operator|.
name|length
argument_list|)
expr_stmt|;
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
block|}
DECL|class|NewServerImpl
specifier|public
specifier|static
class|class
name|NewServerImpl
implements|implements
name|NewRpcService
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
comment|// Ensure clientId is received
name|byte
index|[]
name|clientId
init|=
name|Server
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|Server
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|clientId
operator|.
name|length
argument_list|)
expr_stmt|;
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
DECL|method|echo (RpcController unused, OptRequestProto request)
specifier|public
name|OptResponseProto
name|echo
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|OptRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|OptResponseProto
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
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"testProto"
argument_list|,
name|protocolVersion
operator|=
literal|2
argument_list|)
DECL|class|NewerServerImpl
specifier|public
specifier|static
class|class
name|NewerServerImpl
implements|implements
name|NewerRpcService
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
comment|// Ensure clientId is received
name|byte
index|[]
name|clientId
init|=
name|Server
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|Server
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|clientId
operator|.
name|length
argument_list|)
expr_stmt|;
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
DECL|method|echo (RpcController unused, EmptyRequestProto request)
specifier|public
name|EmptyResponseProto
name|echo
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
comment|// Ensure clientId is received
name|byte
index|[]
name|clientId
init|=
name|Server
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|Server
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|clientId
operator|.
name|length
argument_list|)
expr_stmt|;
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
block|}
annotation|@
name|Test
DECL|method|testProtocolVersionMismatch ()
specifier|public
name|void
name|testProtocolVersionMismatch
parameter_list|()
throws|throws
name|IOException
throws|,
name|ServiceException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IPC_MAXIMUM_DATA_LENGTH
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// Set RPC engine to protobuf RPC engine
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|NewRpcService
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Create server side implementation
name|NewServerImpl
name|serverImpl
init|=
operator|new
name|NewServerImpl
argument_list|()
decl_stmt|;
name|BlockingService
name|service
init|=
name|NewProtobufRpcProto
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
name|NewRpcService
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
name|OldRpcService
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|OldRpcService
name|proxy
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|OldRpcService
operator|.
name|class
argument_list|,
literal|0
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Verify that exception is thrown if protocolVersion is mismatch between
comment|// client and server.
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
name|proxy
operator|.
name|ping
argument_list|(
literal|null
argument_list|,
name|emptyRequest
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception to occur as version mismatch."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"version mismatch"
argument_list|)
operator|)
condition|)
block|{
comment|// Exception type is not what we expected, re-throw it.
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|// Verify that missing of optional field is still compatible in RPC call.
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|NewerRpcService
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|NewerRpcService
name|newProxy
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|NewerRpcService
operator|.
name|class
argument_list|,
literal|0
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|newProxy
operator|.
name|echo
argument_list|(
literal|null
argument_list|,
name|emptyRequest
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

