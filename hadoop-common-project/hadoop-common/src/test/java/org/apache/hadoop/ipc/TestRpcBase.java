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
name|SecretManager
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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadMXBean
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
name|CountDownLatch
import|;
end_import

begin_comment
comment|/** Test facilities for unit tests for RPC. */
end_comment

begin_class
DECL|class|TestRpcBase
specifier|public
class|class
name|TestRpcBase
block|{
DECL|field|SERVER_PRINCIPAL_KEY
specifier|protected
specifier|final
specifier|static
name|String
name|SERVER_PRINCIPAL_KEY
init|=
literal|"test.ipc.server.principal"
decl_stmt|;
DECL|field|ADDRESS
specifier|protected
specifier|final
specifier|static
name|String
name|ADDRESS
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|PORT
specifier|protected
specifier|final
specifier|static
name|int
name|PORT
init|=
literal|0
decl_stmt|;
DECL|field|addr
specifier|protected
specifier|static
name|InetSocketAddress
name|addr
decl_stmt|;
DECL|field|conf
specifier|protected
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|method|setupConf ()
specifier|protected
name|void
name|setupConf
parameter_list|()
block|{
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
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|newServerBuilder ( Configuration serverConf)
specifier|protected
specifier|static
name|RPC
operator|.
name|Builder
name|newServerBuilder
parameter_list|(
name|Configuration
name|serverConf
parameter_list|)
throws|throws
name|IOException
block|{
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
name|TestRpcServiceProtos
operator|.
name|TestProtobufRpcProto
operator|.
name|newReflectiveBlockingService
argument_list|(
name|serverImpl
argument_list|)
decl_stmt|;
comment|// Get RPC server for server side implementation
name|RPC
operator|.
name|Builder
name|builder
init|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|serverConf
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
decl_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|setupTestServer (Configuration serverConf, int numHandlers)
specifier|protected
specifier|static
name|RPC
operator|.
name|Server
name|setupTestServer
parameter_list|(
name|Configuration
name|serverConf
parameter_list|,
name|int
name|numHandlers
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|setupTestServer
argument_list|(
name|serverConf
argument_list|,
name|numHandlers
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|setupTestServer (Configuration serverConf, int numHandlers, SecretManager<?> serverSm)
specifier|protected
specifier|static
name|RPC
operator|.
name|Server
name|setupTestServer
parameter_list|(
name|Configuration
name|serverConf
parameter_list|,
name|int
name|numHandlers
parameter_list|,
name|SecretManager
argument_list|<
name|?
argument_list|>
name|serverSm
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|Builder
name|builder
init|=
name|newServerBuilder
argument_list|(
name|serverConf
argument_list|)
decl_stmt|;
if|if
condition|(
name|numHandlers
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|setNumHandlers
argument_list|(
name|numHandlers
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|serverSm
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setSecretManager
argument_list|(
name|serverSm
argument_list|)
expr_stmt|;
block|}
return|return
name|setupTestServer
argument_list|(
name|builder
argument_list|)
return|;
block|}
DECL|method|setupTestServer (RPC.Builder builder)
specifier|protected
specifier|static
name|RPC
operator|.
name|Server
name|setupTestServer
parameter_list|(
name|RPC
operator|.
name|Builder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|Server
name|server
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|server
operator|.
name|start
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
return|return
name|server
return|;
block|}
DECL|method|getClient (InetSocketAddress serverAddr, Configuration clientConf)
specifier|protected
specifier|static
name|TestRpcService
name|getClient
parameter_list|(
name|InetSocketAddress
name|serverAddr
parameter_list|,
name|Configuration
name|clientConf
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
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
name|serverAddr
argument_list|,
name|clientConf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|stop (Server server, TestRpcService proxy)
specifier|protected
specifier|static
name|void
name|stop
parameter_list|(
name|Server
name|server
parameter_list|,
name|TestRpcService
name|proxy
parameter_list|)
block|{
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{}
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{}
block|}
block|}
comment|/**    * Count the number of threads that have a stack frame containing    * the given string    */
DECL|method|countThreads (String search)
specifier|protected
specifier|static
name|int
name|countThreads
parameter_list|(
name|String
name|search
parameter_list|)
block|{
name|ThreadMXBean
name|threadBean
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|ThreadInfo
index|[]
name|infos
init|=
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|threadBean
operator|.
name|getAllThreadIds
argument_list|()
argument_list|,
literal|20
argument_list|)
decl_stmt|;
for|for
control|(
name|ThreadInfo
name|info
range|:
name|infos
control|)
block|{
if|if
condition|(
name|info
operator|==
literal|null
condition|)
continue|continue;
for|for
control|(
name|StackTraceElement
name|elem
range|:
name|info
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
if|if
condition|(
name|elem
operator|.
name|getClassName
argument_list|()
operator|.
name|contains
argument_list|(
name|search
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|count
return|;
block|}
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"org.apache.hadoop.ipc.TestRpcBase$TestRpcService"
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
name|TestRpcServiceProtos
operator|.
name|TestProtobufRpcProto
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
DECL|field|fastPingCounter
name|CountDownLatch
name|fastPingCounter
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|postponedCalls
specifier|private
name|List
argument_list|<
name|Server
operator|.
name|Call
argument_list|>
name|postponedCalls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|ping (RpcController unused, TestProtos.EmptyRequestProto request)
specifier|public
name|TestProtos
operator|.
name|EmptyResponseProto
name|ping
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|TestProtos
operator|.
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
name|clientId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ClientId
operator|.
name|BYTE_LENGTH
argument_list|,
name|clientId
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|TestProtos
operator|.
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
DECL|method|echo ( RpcController unused, TestProtos.EchoRequestProto request)
specifier|public
name|TestProtos
operator|.
name|EchoResponseProto
name|echo
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|TestProtos
operator|.
name|EchoRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|TestProtos
operator|.
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
DECL|method|error ( RpcController unused, TestProtos.EmptyRequestProto request)
specifier|public
name|TestProtos
operator|.
name|EmptyResponseProto
name|error
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|TestProtos
operator|.
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
DECL|method|error2 ( RpcController unused, TestProtos.EmptyRequestProto request)
specifier|public
name|TestProtos
operator|.
name|EmptyResponseProto
name|error2
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|TestProtos
operator|.
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
annotation|@
name|Override
DECL|method|slowPing ( RpcController unused, TestProtos.SlowPingRequestProto request)
specifier|public
name|TestProtos
operator|.
name|EmptyResponseProto
name|slowPing
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|TestProtos
operator|.
name|SlowPingRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|boolean
name|shouldSlow
init|=
name|request
operator|.
name|getShouldSlow
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldSlow
condition|)
block|{
try|try
block|{
name|fastPingCounter
operator|.
name|await
argument_list|()
expr_stmt|;
comment|//slow response until two fast pings happened
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{}
block|}
else|else
block|{
name|fastPingCounter
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
return|return
name|TestProtos
operator|.
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
DECL|method|echo2 ( RpcController controller, TestProtos.EchoRequestProto2 request)
specifier|public
name|TestProtos
operator|.
name|EchoResponseProto2
name|echo2
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|TestProtos
operator|.
name|EchoRequestProto2
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|TestProtos
operator|.
name|EchoResponseProto2
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllMessage
argument_list|(
name|request
operator|.
name|getMessageList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|add ( RpcController controller, TestProtos.AddRequestProto request)
specifier|public
name|TestProtos
operator|.
name|AddResponseProto
name|add
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|TestProtos
operator|.
name|AddRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|TestProtos
operator|.
name|AddResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setResult
argument_list|(
name|request
operator|.
name|getParam1
argument_list|()
operator|+
name|request
operator|.
name|getParam2
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|add2 ( RpcController controller, TestProtos.AddRequestProto2 request)
specifier|public
name|TestProtos
operator|.
name|AddResponseProto
name|add2
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|TestProtos
operator|.
name|AddRequestProto2
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|int
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Integer
name|num
range|:
name|request
operator|.
name|getParamsList
argument_list|()
control|)
block|{
name|sum
operator|+=
name|num
expr_stmt|;
block|}
return|return
name|TestProtos
operator|.
name|AddResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setResult
argument_list|(
name|sum
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|testServerGet ( RpcController controller, TestProtos.EmptyRequestProto request)
specifier|public
name|TestProtos
operator|.
name|EmptyResponseProto
name|testServerGet
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|TestProtos
operator|.
name|EmptyRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
if|if
condition|(
operator|!
operator|(
name|Server
operator|.
name|get
argument_list|()
operator|instanceof
name|RPC
operator|.
name|Server
operator|)
condition|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Server.get() failed"
argument_list|)
throw|;
block|}
return|return
name|TestProtos
operator|.
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
DECL|method|exchange ( RpcController controller, TestProtos.ExchangeRequestProto request)
specifier|public
name|TestProtos
operator|.
name|ExchangeResponseProto
name|exchange
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|TestProtos
operator|.
name|ExchangeRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|Integer
index|[]
name|values
init|=
operator|new
name|Integer
index|[
name|request
operator|.
name|getValuesCount
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
return|return
name|TestProtos
operator|.
name|ExchangeResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllValues
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sleep ( RpcController controller, TestProtos.SleepRequestProto request)
specifier|public
name|TestProtos
operator|.
name|EmptyResponseProto
name|sleep
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|TestProtos
operator|.
name|SleepRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|request
operator|.
name|getMilliSeconds
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{}
return|return
name|TestProtos
operator|.
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
DECL|method|newEmptyRequest ()
specifier|protected
specifier|static
name|TestProtos
operator|.
name|EmptyRequestProto
name|newEmptyRequest
parameter_list|()
block|{
return|return
name|TestProtos
operator|.
name|EmptyRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|newEchoRequest (String msg)
specifier|protected
specifier|static
name|TestProtos
operator|.
name|EchoRequestProto
name|newEchoRequest
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
return|return
name|TestProtos
operator|.
name|EchoRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMessage
argument_list|(
name|msg
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convert (TestProtos.EchoResponseProto response)
specifier|protected
specifier|static
name|String
name|convert
parameter_list|(
name|TestProtos
operator|.
name|EchoResponseProto
name|response
parameter_list|)
block|{
return|return
name|response
operator|.
name|getMessage
argument_list|()
return|;
block|}
DECL|method|newSlowPingRequest ( boolean shouldSlow)
specifier|protected
specifier|static
name|TestProtos
operator|.
name|SlowPingRequestProto
name|newSlowPingRequest
parameter_list|(
name|boolean
name|shouldSlow
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|TestProtos
operator|.
name|SlowPingRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setShouldSlow
argument_list|(
name|shouldSlow
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|newSleepRequest ( int milliSeconds)
specifier|protected
specifier|static
name|TestProtos
operator|.
name|SleepRequestProto
name|newSleepRequest
parameter_list|(
name|int
name|milliSeconds
parameter_list|)
block|{
return|return
name|TestProtos
operator|.
name|SleepRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMilliSeconds
argument_list|(
name|milliSeconds
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

