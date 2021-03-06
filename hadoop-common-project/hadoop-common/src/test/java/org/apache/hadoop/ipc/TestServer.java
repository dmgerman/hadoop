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
name|*
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
name|*
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
name|BindException
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
name|ServerSocket
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
name|LongWritable
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
name|Writable
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
operator|.
name|Call
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * This is intended to be a set of unit tests for the   * org.apache.hadoop.ipc.Server class.  */
end_comment

begin_class
DECL|class|TestServer
specifier|public
class|class
name|TestServer
block|{
annotation|@
name|Test
DECL|method|testBind ()
specifier|public
name|void
name|testBind
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
name|ServerSocket
name|socket
init|=
operator|new
name|ServerSocket
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|socket
operator|.
name|bind
argument_list|(
name|address
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|min
init|=
name|socket
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
name|int
name|max
init|=
name|min
operator|+
literal|100
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"TestRange"
argument_list|,
name|min
operator|+
literal|"-"
operator|+
name|max
argument_list|)
expr_stmt|;
name|ServerSocket
name|socket2
init|=
operator|new
name|ServerSocket
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|address2
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Server
operator|.
name|bind
argument_list|(
name|socket2
argument_list|,
name|address2
argument_list|,
literal|10
argument_list|,
name|conf
argument_list|,
literal|"TestRange"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|socket2
operator|.
name|isBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|socket2
operator|.
name|getLocalPort
argument_list|()
operator|>
name|min
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|socket2
operator|.
name|getLocalPort
argument_list|()
operator|<=
name|max
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|socket2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBindSimple ()
specifier|public
name|void
name|testBindSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|ServerSocket
name|socket
init|=
operator|new
name|ServerSocket
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Server
operator|.
name|bind
argument_list|(
name|socket
argument_list|,
name|address
argument_list|,
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|socket
operator|.
name|isBound
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEmptyConfig ()
specifier|public
name|void
name|testEmptyConfig
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
name|conf
operator|.
name|set
argument_list|(
literal|"TestRange"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ServerSocket
name|socket
init|=
operator|new
name|ServerSocket
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|Server
operator|.
name|bind
argument_list|(
name|socket
argument_list|,
name|address
argument_list|,
literal|10
argument_list|,
name|conf
argument_list|,
literal|"TestRange"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|socket
operator|.
name|isBound
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBindError ()
specifier|public
name|void
name|testBindError
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
name|ServerSocket
name|socket
init|=
operator|new
name|ServerSocket
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|socket
operator|.
name|bind
argument_list|(
name|address
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|min
init|=
name|socket
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"TestRange"
argument_list|,
name|min
operator|+
literal|"-"
operator|+
name|min
argument_list|)
expr_stmt|;
name|ServerSocket
name|socket2
init|=
operator|new
name|ServerSocket
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|address2
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|boolean
name|caught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Server
operator|.
name|bind
argument_list|(
name|socket2
argument_list|,
name|address2
argument_list|,
literal|10
argument_list|,
name|conf
argument_list|,
literal|"TestRange"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BindException
name|e
parameter_list|)
block|{
name|caught
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|socket2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to catch the expected bind exception"
argument_list|,
name|caught
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|TestException1
specifier|static
class|class
name|TestException1
extends|extends
name|Exception
block|{   }
DECL|class|TestException2
specifier|static
class|class
name|TestException2
extends|extends
name|Exception
block|{   }
DECL|class|TestException3
specifier|static
class|class
name|TestException3
extends|extends
name|Exception
block|{   }
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testLogExceptions ()
specifier|public
name|void
name|testLogExceptions
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|Call
name|dummyCall
init|=
operator|new
name|Call
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Logger
name|logger
init|=
name|mock
argument_list|(
name|Logger
operator|.
name|class
argument_list|)
decl_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
literal|1
argument_list|,
name|conf
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Writable
name|call
parameter_list|(
name|RPC
operator|.
name|RpcKind
name|rpcKind
parameter_list|,
name|String
name|protocol
parameter_list|,
name|Writable
name|param
parameter_list|,
name|long
name|receiveTime
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|server
operator|.
name|addSuppressedLoggingExceptions
argument_list|(
name|TestException1
operator|.
name|class
argument_list|)
expr_stmt|;
name|server
operator|.
name|addTerseExceptions
argument_list|(
name|TestException2
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Nothing should be logged for a suppressed exception.
name|server
operator|.
name|logException
argument_list|(
name|logger
argument_list|,
operator|new
name|TestException1
argument_list|()
argument_list|,
name|dummyCall
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|logger
argument_list|)
expr_stmt|;
comment|// No stack trace should be logged for a terse exception.
name|server
operator|.
name|logException
argument_list|(
name|logger
argument_list|,
operator|new
name|TestException2
argument_list|()
argument_list|,
name|dummyCall
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|logger
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|info
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
comment|// Full stack trace should be logged for other exceptions.
specifier|final
name|Throwable
name|te3
init|=
operator|new
name|TestException3
argument_list|()
decl_stmt|;
name|server
operator|.
name|logException
argument_list|(
name|logger
argument_list|,
name|te3
argument_list|,
name|dummyCall
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|logger
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|info
argument_list|(
name|any
argument_list|()
argument_list|,
name|eq
argument_list|(
name|te3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExceptionsHandlerTerse ()
specifier|public
name|void
name|testExceptionsHandlerTerse
parameter_list|()
block|{
name|Server
operator|.
name|ExceptionsHandler
name|handler
init|=
operator|new
name|Server
operator|.
name|ExceptionsHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|addTerseLoggingExceptions
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|handler
operator|.
name|addTerseLoggingExceptions
argument_list|(
name|RpcServerException
operator|.
name|class
argument_list|,
name|IpcException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|isTerseLog
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|isTerseLog
argument_list|(
name|RpcServerException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|isTerseLog
argument_list|(
name|IpcException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|handler
operator|.
name|isTerseLog
argument_list|(
name|RpcClientException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExceptionsHandlerSuppressed ()
specifier|public
name|void
name|testExceptionsHandlerSuppressed
parameter_list|()
block|{
name|Server
operator|.
name|ExceptionsHandler
name|handler
init|=
operator|new
name|Server
operator|.
name|ExceptionsHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|addSuppressedLoggingExceptions
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|handler
operator|.
name|addSuppressedLoggingExceptions
argument_list|(
name|RpcServerException
operator|.
name|class
argument_list|,
name|IpcException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|isSuppressedLog
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|isSuppressedLog
argument_list|(
name|RpcServerException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|isSuppressedLog
argument_list|(
name|IpcException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|handler
operator|.
name|isSuppressedLog
argument_list|(
name|RpcClientException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

