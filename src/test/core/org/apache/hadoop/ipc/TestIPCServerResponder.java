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
name|Random
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|io
operator|.
name|BytesWritable
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
name|net
operator|.
name|NetUtils
import|;
end_import

begin_comment
comment|/**  * This test provokes partial writes in the server, which is   * serving multiple clients.  */
end_comment

begin_class
DECL|class|TestIPCServerResponder
specifier|public
class|class
name|TestIPCServerResponder
extends|extends
name|TestCase
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestIPCServerResponder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|TestIPCServerResponder (final String name)
specifier|public
name|TestIPCServerResponder
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|ADDRESS
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|BYTE_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|BYTE_COUNT
init|=
literal|1024
decl_stmt|;
DECL|field|BYTES
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|BYTES
init|=
operator|new
name|byte
index|[
name|BYTE_COUNT
index|]
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|BYTE_COUNT
condition|;
name|i
operator|++
control|)
name|BYTES
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|'a'
operator|+
operator|(
name|i
operator|%
literal|26
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|class|TestServer
specifier|private
specifier|static
class|class
name|TestServer
extends|extends
name|Server
block|{
DECL|field|sleep
specifier|private
name|boolean
name|sleep
decl_stmt|;
DECL|method|TestServer (final int handlerCount, final boolean sleep)
specifier|public
name|TestServer
parameter_list|(
specifier|final
name|int
name|handlerCount
parameter_list|,
specifier|final
name|boolean
name|sleep
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|ADDRESS
argument_list|,
literal|0
argument_list|,
name|BytesWritable
operator|.
name|class
argument_list|,
name|handlerCount
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Set the buffer size to half of the maximum parameter/result size
comment|// to force the socket to block
name|this
operator|.
name|setSocketSendBufSize
argument_list|(
name|BYTE_COUNT
operator|/
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|sleep
operator|=
name|sleep
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call (Class<?> protocol, Writable param, long receiveTime)
specifier|public
name|Writable
name|call
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Writable
name|param
parameter_list|,
name|long
name|receiveTime
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sleep
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
comment|// sleep a bit
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
return|return
name|param
return|;
block|}
block|}
DECL|class|Caller
specifier|private
specifier|static
class|class
name|Caller
extends|extends
name|Thread
block|{
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|address
specifier|private
name|InetSocketAddress
name|address
decl_stmt|;
DECL|field|failed
specifier|private
name|boolean
name|failed
decl_stmt|;
DECL|method|Caller (final Client client, final InetSocketAddress address, final int count)
specifier|public
name|Caller
parameter_list|(
specifier|final
name|Client
name|client
parameter_list|,
specifier|final
name|InetSocketAddress
name|address
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|int
name|byteSize
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|BYTE_COUNT
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|byteSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|BYTES
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|byteSize
argument_list|)
expr_stmt|;
name|Writable
name|param
init|=
operator|new
name|BytesWritable
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|Writable
name|value
init|=
name|client
operator|.
name|call
argument_list|(
name|param
argument_list|,
name|address
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|20
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
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testResponseBuffer ()
specifier|public
name|void
name|testResponseBuffer
parameter_list|()
throws|throws
name|Exception
block|{
name|Server
operator|.
name|INITIAL_RESP_BUF_SIZE
operator|=
literal|1
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IPC_SERVER_RPC_MAX_RESPONSE_SIZE_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testServerResponder
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
comment|// reset configuration
block|}
DECL|method|testServerResponder ()
specifier|public
name|void
name|testServerResponder
parameter_list|()
throws|throws
name|Exception
block|{
name|testServerResponder
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|200
argument_list|)
expr_stmt|;
block|}
DECL|method|testServerResponder (final int handlerCount, final boolean handlerSleep, final int clientCount, final int callerCount, final int callCount)
specifier|public
name|void
name|testServerResponder
parameter_list|(
specifier|final
name|int
name|handlerCount
parameter_list|,
specifier|final
name|boolean
name|handlerSleep
parameter_list|,
specifier|final
name|int
name|clientCount
parameter_list|,
specifier|final
name|int
name|callerCount
parameter_list|,
specifier|final
name|int
name|callCount
parameter_list|)
throws|throws
name|Exception
block|{
name|Server
name|server
init|=
operator|new
name|TestServer
argument_list|(
name|handlerCount
argument_list|,
name|handlerSleep
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|address
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|Client
index|[]
name|clients
init|=
operator|new
name|Client
index|[
name|clientCount
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
name|clientCount
condition|;
name|i
operator|++
control|)
block|{
name|clients
index|[
name|i
index|]
operator|=
operator|new
name|Client
argument_list|(
name|BytesWritable
operator|.
name|class
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|Caller
index|[]
name|callers
init|=
operator|new
name|Caller
index|[
name|callerCount
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
name|callerCount
condition|;
name|i
operator|++
control|)
block|{
name|callers
index|[
name|i
index|]
operator|=
operator|new
name|Caller
argument_list|(
name|clients
index|[
name|i
operator|%
name|clientCount
index|]
argument_list|,
name|address
argument_list|,
name|callCount
argument_list|)
expr_stmt|;
name|callers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|callerCount
condition|;
name|i
operator|++
control|)
block|{
name|callers
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|callers
index|[
name|i
index|]
operator|.
name|failed
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clientCount
condition|;
name|i
operator|++
control|)
block|{
name|clients
index|[
name|i
index|]
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

