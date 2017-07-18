begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|ExecutionException
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
name|FutureTask
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
name|TimeUnit
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
name|TimeoutException
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
name|atomic
operator|.
name|AtomicBoolean
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
name|locks
operator|.
name|Condition
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
name|locks
operator|.
name|ReentrantLock
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

begin_class
DECL|class|TestRpcServerHandoff
specifier|public
class|class
name|TestRpcServerHandoff
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestRpcServerHandoff
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BIND_ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|BIND_ADDRESS
init|=
literal|"0.0.0.0"
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
DECL|class|ServerForHandoffTest
specifier|public
specifier|static
class|class
name|ServerForHandoffTest
extends|extends
name|Server
block|{
DECL|field|invoked
specifier|private
specifier|final
name|AtomicBoolean
name|invoked
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|ReentrantLock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|invokedCondition
specifier|private
specifier|final
name|Condition
name|invokedCondition
init|=
name|lock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
DECL|field|request
specifier|private
specifier|volatile
name|Writable
name|request
decl_stmt|;
DECL|field|deferredCall
specifier|private
specifier|volatile
name|Call
name|deferredCall
decl_stmt|;
DECL|method|ServerForHandoffTest (int handlerCount)
specifier|protected
name|ServerForHandoffTest
parameter_list|(
name|int
name|handlerCount
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|BIND_ADDRESS
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
block|}
annotation|@
name|Override
DECL|method|call (RPC.RpcKind rpcKind, String protocol, Writable param, long receiveTime)
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
name|request
operator|=
name|param
expr_stmt|;
name|deferredCall
operator|=
name|Server
operator|.
name|getCurCall
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|Server
operator|.
name|getCurCall
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|deferResponse
argument_list|()
expr_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|invoked
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|invokedCondition
operator|.
name|signal
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|awaitInvocation ()
name|void
name|awaitInvocation
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
while|while
condition|(
operator|!
name|invoked
operator|.
name|get
argument_list|()
condition|)
block|{
name|invokedCondition
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|sendResponse ()
name|void
name|sendResponse
parameter_list|()
block|{
name|deferredCall
operator|.
name|setDeferredResponse
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|sendError ()
name|void
name|sendError
parameter_list|()
block|{
name|deferredCall
operator|.
name|setDeferredError
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"DeferredError"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testDeferredResponse ()
specifier|public
name|void
name|testDeferredResponse
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|ServerForHandoffTest
name|server
init|=
operator|new
name|ServerForHandoffTest
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|InetSocketAddress
name|serverAddress
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|byte
index|[]
name|requestBytes
init|=
name|generateRandomBytes
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|ClientCallable
name|clientCallable
init|=
operator|new
name|ClientCallable
argument_list|(
name|serverAddress
argument_list|,
name|conf
argument_list|,
name|requestBytes
argument_list|)
decl_stmt|;
name|FutureTask
argument_list|<
name|Writable
argument_list|>
name|future
init|=
operator|new
name|FutureTask
argument_list|<
name|Writable
argument_list|>
argument_list|(
name|clientCallable
argument_list|)
decl_stmt|;
name|Thread
name|clientThread
init|=
operator|new
name|Thread
argument_list|(
name|future
argument_list|)
decl_stmt|;
name|clientThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|server
operator|.
name|awaitInvocation
argument_list|()
expr_stmt|;
name|awaitResponseTimeout
argument_list|(
name|future
argument_list|)
expr_stmt|;
name|server
operator|.
name|sendResponse
argument_list|()
expr_stmt|;
name|BytesWritable
name|response
init|=
operator|(
name|BytesWritable
operator|)
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|BytesWritable
argument_list|(
name|requestBytes
argument_list|)
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testDeferredException ()
specifier|public
name|void
name|testDeferredException
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|ServerForHandoffTest
name|server
init|=
operator|new
name|ServerForHandoffTest
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|InetSocketAddress
name|serverAddress
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|byte
index|[]
name|requestBytes
init|=
name|generateRandomBytes
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|ClientCallable
name|clientCallable
init|=
operator|new
name|ClientCallable
argument_list|(
name|serverAddress
argument_list|,
name|conf
argument_list|,
name|requestBytes
argument_list|)
decl_stmt|;
name|FutureTask
argument_list|<
name|Writable
argument_list|>
name|future
init|=
operator|new
name|FutureTask
argument_list|<
name|Writable
argument_list|>
argument_list|(
name|clientCallable
argument_list|)
decl_stmt|;
name|Thread
name|clientThread
init|=
operator|new
name|Thread
argument_list|(
name|future
argument_list|)
decl_stmt|;
name|clientThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|server
operator|.
name|awaitInvocation
argument_list|()
expr_stmt|;
name|awaitResponseTimeout
argument_list|(
name|future
argument_list|)
expr_stmt|;
name|server
operator|.
name|sendError
argument_list|()
expr_stmt|;
try|try
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Call succeeded. Was expecting an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cause
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
name|cause
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|re
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"DeferredError"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|awaitResponseTimeout (FutureTask<Writable> future)
specifier|private
name|void
name|awaitResponseTimeout
parameter_list|(
name|FutureTask
argument_list|<
name|Writable
argument_list|>
name|future
parameter_list|)
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|long
name|sleepTime
init|=
literal|3000L
decl_stmt|;
while|while
condition|(
name|sleepTime
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|future
operator|.
name|get
argument_list|(
literal|200L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected to timeout since"
operator|+
literal|" the deferred response hasn't been registered"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
comment|// Ignoring. Expected to time out.
block|}
name|sleepTime
operator|-=
literal|200L
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Done sleeping"
argument_list|)
expr_stmt|;
block|}
DECL|class|ClientCallable
specifier|private
specifier|static
class|class
name|ClientCallable
implements|implements
name|Callable
argument_list|<
name|Writable
argument_list|>
block|{
DECL|field|address
specifier|private
specifier|final
name|InetSocketAddress
name|address
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|requestBytes
specifier|final
name|byte
index|[]
name|requestBytes
decl_stmt|;
DECL|method|ClientCallable (InetSocketAddress address, Configuration conf, byte[] requestBytes)
specifier|private
name|ClientCallable
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|byte
index|[]
name|requestBytes
parameter_list|)
block|{
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|requestBytes
operator|=
name|requestBytes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Writable
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
name|BytesWritable
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Writable
name|param
init|=
operator|new
name|BytesWritable
argument_list|(
name|requestBytes
argument_list|)
decl_stmt|;
specifier|final
name|Client
operator|.
name|ConnectionId
name|remoteId
init|=
name|Client
operator|.
name|ConnectionId
operator|.
name|getConnectionId
argument_list|(
name|address
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Writable
name|result
init|=
name|client
operator|.
name|call
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_BUILTIN
argument_list|,
name|param
argument_list|,
name|remoteId
argument_list|,
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|method|generateRandomBytes (int length)
specifier|private
name|byte
index|[]
name|generateRandomBytes
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
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
name|random
operator|.
name|nextInt
argument_list|(
literal|26
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
block|}
end_class

end_unit

