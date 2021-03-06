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
name|Before
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ConnectException
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
name|nio
operator|.
name|channels
operator|.
name|ClosedByInterruptException
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY
import|;
end_import

begin_comment
comment|/**  * tests that the proxy can be interrupted  */
end_comment

begin_class
DECL|class|TestRPCWaitForProxy
specifier|public
class|class
name|TestRPCWaitForProxy
extends|extends
name|TestRpcBase
block|{
specifier|private
specifier|static
specifier|final
name|Logger
DECL|field|LOG
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestRPCWaitForProxy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setupProtocolEngine ()
specifier|public
name|void
name|setupProtocolEngine
parameter_list|()
block|{
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
block|}
comment|/**    * This tests that the time-bounded wait for a proxy operation works, and    * times out.    *    * @throws Throwable any exception other than that which was expected    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|50000
argument_list|)
DECL|method|testWaitForProxy ()
specifier|public
name|void
name|testWaitForProxy
parameter_list|()
throws|throws
name|Throwable
block|{
name|RpcThread
name|worker
init|=
operator|new
name|RpcThread
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|worker
operator|.
name|start
argument_list|()
expr_stmt|;
name|worker
operator|.
name|join
argument_list|()
expr_stmt|;
name|Throwable
name|caught
init|=
name|worker
operator|.
name|getCaught
argument_list|()
decl_stmt|;
name|Throwable
name|cause
init|=
name|caught
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"No exception was raised"
argument_list|,
name|cause
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|cause
operator|instanceof
name|ConnectException
operator|)
condition|)
block|{
throw|throw
name|caught
throw|;
block|}
block|}
comment|/**    * This test sets off a blocking thread and then interrupts it, before    * checking that the thread was interrupted    *    * @throws Throwable any exception other than that which was expected    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testInterruptedWaitForProxy ()
specifier|public
name|void
name|testInterruptedWaitForProxy
parameter_list|()
throws|throws
name|Throwable
block|{
name|RpcThread
name|worker
init|=
operator|new
name|RpcThread
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|worker
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"worker hasn't started"
argument_list|,
name|worker
operator|.
name|waitStarted
argument_list|)
expr_stmt|;
name|worker
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|worker
operator|.
name|join
argument_list|()
expr_stmt|;
name|Throwable
name|caught
init|=
name|worker
operator|.
name|getCaught
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"No exception was raised"
argument_list|,
name|caught
argument_list|)
expr_stmt|;
comment|// looking for the root cause here, which can be wrapped
comment|// as part of the NetUtils work. Having this test look
comment|// a the type of exception there would be brittle to improvements
comment|// in exception diagnostics.
name|Throwable
name|cause
init|=
name|caught
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|==
literal|null
condition|)
block|{
comment|// no inner cause, use outer exception as root cause.
name|cause
operator|=
name|caught
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cause
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cause
operator|=
name|cause
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|cause
operator|instanceof
name|InterruptedIOException
operator|)
operator|&&
operator|!
operator|(
name|cause
operator|instanceof
name|ClosedByInterruptException
operator|)
condition|)
block|{
throw|throw
name|caught
throw|;
block|}
block|}
comment|/**    * This thread waits for a proxy for the specified timeout, and retains any    * throwable that was raised in the process    */
DECL|class|RpcThread
specifier|private
class|class
name|RpcThread
extends|extends
name|Thread
block|{
DECL|field|caught
specifier|private
name|Throwable
name|caught
decl_stmt|;
DECL|field|connectRetries
specifier|private
name|int
name|connectRetries
decl_stmt|;
DECL|field|waitStarted
specifier|private
specifier|volatile
name|boolean
name|waitStarted
init|=
literal|false
decl_stmt|;
DECL|method|RpcThread (int connectRetries)
specifier|private
name|RpcThread
parameter_list|(
name|int
name|connectRetries
parameter_list|)
block|{
name|this
operator|.
name|connectRetries
operator|=
name|connectRetries
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
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|config
operator|.
name|setInt
argument_list|(
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
name|connectRetries
argument_list|)
expr_stmt|;
name|config
operator|.
name|setInt
argument_list|(
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY
argument_list|,
name|connectRetries
argument_list|)
expr_stmt|;
name|waitStarted
operator|=
literal|true
expr_stmt|;
name|short
name|invalidPort
init|=
literal|20
decl_stmt|;
name|InetSocketAddress
name|invalidAddress
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|ADDRESS
argument_list|,
name|invalidPort
argument_list|)
decl_stmt|;
name|TestRpcBase
operator|.
name|TestRpcService
name|proxy
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestRpcBase
operator|.
name|TestRpcService
operator|.
name|class
argument_list|,
literal|1L
argument_list|,
name|invalidAddress
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Test echo method
name|proxy
operator|.
name|echo
argument_list|(
literal|null
argument_list|,
name|newEchoRequest
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|caught
operator|=
name|throwable
expr_stmt|;
block|}
block|}
DECL|method|getCaught ()
specifier|public
name|Throwable
name|getCaught
parameter_list|()
block|{
return|return
name|caught
return|;
block|}
block|}
block|}
end_class

end_unit

