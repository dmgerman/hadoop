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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|commons
operator|.
name|logging
operator|.
name|*
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
name|UTF8
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
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
operator|.
name|AuthorizationException
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
name|authorize
operator|.
name|PolicyProvider
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
name|authorize
operator|.
name|Service
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
name|authorize
operator|.
name|ServiceAuthorizationManager
import|;
end_import

begin_comment
comment|/** Unit tests for RPC. */
end_comment

begin_class
DECL|class|TestRPC
specifier|public
class|class
name|TestRPC
extends|extends
name|TestCase
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
name|TestRPC
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
DECL|field|datasize
name|int
name|datasize
init|=
literal|1024
operator|*
literal|100
decl_stmt|;
DECL|field|numThreads
name|int
name|numThreads
init|=
literal|50
decl_stmt|;
DECL|method|TestRPC (String name)
specifier|public
name|TestRPC
parameter_list|(
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
DECL|interface|TestProtocol
specifier|public
interface|interface
name|TestProtocol
extends|extends
name|VersionedProtocol
block|{
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
DECL|method|ping ()
name|void
name|ping
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|slowPing (boolean shouldSlow)
name|void
name|slowPing
parameter_list|(
name|boolean
name|shouldSlow
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|echo (String value)
name|String
name|echo
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|echo (String[] value)
name|String
index|[]
name|echo
parameter_list|(
name|String
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|echo (Writable value)
name|Writable
name|echo
parameter_list|(
name|Writable
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|add (int v1, int v2)
name|int
name|add
parameter_list|(
name|int
name|v1
parameter_list|,
name|int
name|v2
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|add (int[] values)
name|int
name|add
parameter_list|(
name|int
index|[]
name|values
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|error ()
name|int
name|error
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|testServerGet ()
name|void
name|testServerGet
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|exchange (int[] values)
name|int
index|[]
name|exchange
parameter_list|(
name|int
index|[]
name|values
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|TestImpl
specifier|public
specifier|static
class|class
name|TestImpl
implements|implements
name|TestProtocol
block|{
DECL|field|fastPingCounter
name|int
name|fastPingCounter
init|=
literal|0
decl_stmt|;
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
block|{
return|return
name|TestProtocol
operator|.
name|versionID
return|;
block|}
DECL|method|ping ()
specifier|public
name|void
name|ping
parameter_list|()
block|{}
DECL|method|slowPing (boolean shouldSlow)
specifier|public
specifier|synchronized
name|void
name|slowPing
parameter_list|(
name|boolean
name|shouldSlow
parameter_list|)
block|{
if|if
condition|(
name|shouldSlow
condition|)
block|{
while|while
condition|(
name|fastPingCounter
operator|<
literal|2
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
comment|// slow response until two fast pings happened
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{}
block|}
name|fastPingCounter
operator|-=
literal|2
expr_stmt|;
block|}
else|else
block|{
name|fastPingCounter
operator|++
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|echo (String value)
specifier|public
name|String
name|echo
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
return|;
block|}
DECL|method|echo (String[] values)
specifier|public
name|String
index|[]
name|echo
parameter_list|(
name|String
index|[]
name|values
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|values
return|;
block|}
DECL|method|echo (Writable writable)
specifier|public
name|Writable
name|echo
parameter_list|(
name|Writable
name|writable
parameter_list|)
block|{
return|return
name|writable
return|;
block|}
DECL|method|add (int v1, int v2)
specifier|public
name|int
name|add
parameter_list|(
name|int
name|v1
parameter_list|,
name|int
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|+
name|v2
return|;
block|}
DECL|method|add (int[] values)
specifier|public
name|int
name|add
parameter_list|(
name|int
index|[]
name|values
parameter_list|)
block|{
name|int
name|sum
init|=
literal|0
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
name|sum
operator|+=
name|values
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
DECL|method|error ()
specifier|public
name|int
name|error
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"bobo"
argument_list|)
throw|;
block|}
DECL|method|testServerGet ()
specifier|public
name|void
name|testServerGet
parameter_list|()
throws|throws
name|IOException
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
name|IOException
argument_list|(
literal|"Server.get() failed"
argument_list|)
throw|;
block|}
block|}
DECL|method|exchange (int[] values)
specifier|public
name|int
index|[]
name|exchange
parameter_list|(
name|int
index|[]
name|values
parameter_list|)
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
name|values
return|;
block|}
block|}
comment|//
comment|// an object that does a bunch of transactions
comment|//
DECL|class|Transactions
specifier|static
class|class
name|Transactions
implements|implements
name|Runnable
block|{
DECL|field|datasize
name|int
name|datasize
decl_stmt|;
DECL|field|proxy
name|TestProtocol
name|proxy
decl_stmt|;
DECL|method|Transactions (TestProtocol proxy, int datasize)
name|Transactions
parameter_list|(
name|TestProtocol
name|proxy
parameter_list|,
name|int
name|datasize
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
name|this
operator|.
name|datasize
operator|=
name|datasize
expr_stmt|;
block|}
comment|// do two RPC that transfers data.
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
index|[]
name|indata
init|=
operator|new
name|int
index|[
name|datasize
index|]
decl_stmt|;
name|int
index|[]
name|outdata
init|=
literal|null
decl_stmt|;
name|int
name|val
init|=
literal|0
decl_stmt|;
try|try
block|{
name|outdata
operator|=
name|proxy
operator|.
name|exchange
argument_list|(
name|indata
argument_list|)
expr_stmt|;
name|val
operator|=
name|proxy
operator|.
name|add
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Exception from RPC exchange() "
operator|+
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|indata
operator|.
name|length
argument_list|,
name|outdata
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|val
argument_list|,
literal|3
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outdata
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|outdata
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//
comment|// A class that does an RPC but does not read its response.
comment|//
DECL|class|SlowRPC
specifier|static
class|class
name|SlowRPC
implements|implements
name|Runnable
block|{
DECL|field|proxy
specifier|private
name|TestProtocol
name|proxy
decl_stmt|;
DECL|field|done
specifier|private
specifier|volatile
name|boolean
name|done
decl_stmt|;
DECL|method|SlowRPC (TestProtocol proxy)
name|SlowRPC
parameter_list|(
name|TestProtocol
name|proxy
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
name|done
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|isDone ()
name|boolean
name|isDone
parameter_list|()
block|{
return|return
name|done
return|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|proxy
operator|.
name|slowPing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// this would hang until two fast pings happened
name|done
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"SlowRPC ping exception "
operator|+
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSlowRpc ()
specifier|public
name|void
name|testSlowRpc
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing Slow RPC"
argument_list|)
expr_stmt|;
comment|// create a server with two handlers
name|Server
name|server
init|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|,
operator|new
name|TestImpl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TestProtocol
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
comment|// create a client
name|proxy
operator|=
operator|(
name|TestProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|,
name|TestProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|SlowRPC
name|slowrpc
init|=
operator|new
name|SlowRPC
argument_list|(
name|proxy
argument_list|)
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|slowrpc
argument_list|,
literal|"SlowRPC"
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// send a slow RPC, which won't return until two fast pings
name|assertTrue
argument_list|(
literal|"Slow RPC should not have finished1."
argument_list|,
operator|!
name|slowrpc
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|slowPing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// first fast ping
comment|// verify that the first RPC is still stuck
name|assertTrue
argument_list|(
literal|"Slow RPC should not have finished2."
argument_list|,
operator|!
name|slowrpc
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|slowPing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// second fast ping
comment|// Now the slow ping should be able to be executed
while|while
condition|(
operator|!
name|slowrpc
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for slow RPC to get done."
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Down slow rpc testing"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCalls (Configuration conf)
specifier|public
name|void
name|testCalls
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|Server
name|server
init|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|,
operator|new
name|TestImpl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|TestProtocol
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|proxy
operator|=
operator|(
name|TestProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|,
name|TestProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|ping
argument_list|()
expr_stmt|;
name|String
name|stringResult
init|=
name|proxy
operator|.
name|echo
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|stringResult
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|stringResult
operator|=
name|proxy
operator|.
name|echo
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stringResult
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
index|[]
name|stringResults
init|=
name|proxy
operator|.
name|echo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|stringResults
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|stringResults
operator|=
name|proxy
operator|.
name|echo
argument_list|(
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|stringResults
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|UTF8
name|utf8Result
init|=
operator|(
name|UTF8
operator|)
name|proxy
operator|.
name|echo
argument_list|(
operator|new
name|UTF8
argument_list|(
literal|"hello world"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|utf8Result
argument_list|,
operator|new
name|UTF8
argument_list|(
literal|"hello world"
argument_list|)
argument_list|)
expr_stmt|;
name|utf8Result
operator|=
operator|(
name|UTF8
operator|)
name|proxy
operator|.
name|echo
argument_list|(
operator|(
name|UTF8
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|utf8Result
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|int
name|intResult
init|=
name|proxy
operator|.
name|add
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|intResult
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|intResult
operator|=
name|proxy
operator|.
name|add
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|intResult
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|boolean
name|caught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|proxy
operator|.
name|error
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Caught "
operator|+
name|e
argument_list|)
expr_stmt|;
name|caught
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|caught
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|testServerGet
argument_list|()
expr_stmt|;
comment|// create multiple threads and make them do large data transfers
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting multi-threaded RPC test..."
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSocketSendBufSize
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|Thread
name|threadId
index|[]
init|=
operator|new
name|Thread
index|[
name|numThreads
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|Transactions
name|trans
init|=
operator|new
name|Transactions
argument_list|(
name|proxy
argument_list|,
name|datasize
argument_list|)
decl_stmt|;
name|threadId
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
name|trans
argument_list|,
literal|"TransactionThread-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|threadId
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// wait for all transactions to get over
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for all threads to finish RPCs..."
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|threadId
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|i
operator|--
expr_stmt|;
comment|// retry
block|}
block|}
comment|// try some multi-calls
name|Method
name|echo
init|=
name|TestProtocol
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"echo"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|}
argument_list|)
decl_stmt|;
name|String
index|[]
name|strings
init|=
operator|(
name|String
index|[]
operator|)
name|RPC
operator|.
name|call
argument_list|(
name|echo
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
block|{
literal|"a"
block|}
block|,
block|{
literal|"b"
block|}
block|}
argument_list|,
operator|new
name|InetSocketAddress
index|[]
block|{
name|addr
block|,
name|addr
block|}
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|strings
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|Method
name|ping
init|=
name|TestProtocol
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"ping"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|Object
index|[]
name|voids
init|=
name|RPC
operator|.
name|call
argument_list|(
name|ping
argument_list|,
operator|new
name|Object
index|[]
index|[]
block|{
block|{}
block|,
block|{}
block|}
argument_list|,
operator|new
name|InetSocketAddress
index|[]
block|{
name|addr
block|,
name|addr
block|}
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|voids
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testStandaloneClient ()
specifier|public
name|void
name|testStandaloneClient
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|RPC
operator|.
name|waitForProxy
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|,
name|TestProtocol
operator|.
name|versionID
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
name|ADDRESS
argument_list|,
literal|20
argument_list|)
argument_list|,
name|conf
argument_list|,
literal|15000L
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We should not have reached here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConnectException
name|ioe
parameter_list|)
block|{
comment|//this is what we expected
block|}
block|}
DECL|field|ACL_CONFIG
specifier|private
specifier|static
specifier|final
name|String
name|ACL_CONFIG
init|=
literal|"test.protocol.acl"
decl_stmt|;
DECL|class|TestPolicyProvider
specifier|private
specifier|static
class|class
name|TestPolicyProvider
extends|extends
name|PolicyProvider
block|{
annotation|@
name|Override
DECL|method|getServices ()
specifier|public
name|Service
index|[]
name|getServices
parameter_list|()
block|{
return|return
operator|new
name|Service
index|[]
block|{
operator|new
name|Service
argument_list|(
name|ACL_CONFIG
argument_list|,
name|TestProtocol
operator|.
name|class
argument_list|)
block|}
return|;
block|}
block|}
DECL|method|doRPCs (Configuration conf, boolean expectFailure)
specifier|private
name|void
name|doRPCs
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|boolean
name|expectFailure
parameter_list|)
throws|throws
name|Exception
block|{
name|ServiceAuthorizationManager
operator|.
name|refresh
argument_list|(
name|conf
argument_list|,
operator|new
name|TestPolicyProvider
argument_list|()
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|,
operator|new
name|TestImpl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TestProtocol
name|proxy
init|=
literal|null
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
try|try
block|{
name|proxy
operator|=
operator|(
name|TestProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|,
name|TestProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|ping
argument_list|()
expr_stmt|;
if|if
condition|(
name|expectFailure
condition|)
block|{
name|fail
argument_list|(
literal|"Expect RPC.getProxy to fail with AuthorizationException!"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
if|if
condition|(
name|expectFailure
condition|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|unwrapRemoteException
argument_list|()
operator|instanceof
name|AuthorizationException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testAuthorization ()
specifier|public
name|void
name|testAuthorization
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
name|setBoolean
argument_list|(
name|ServiceAuthorizationManager
operator|.
name|SERVICE_AUTHORIZATION_CONFIG
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Expect to succeed
name|conf
operator|.
name|set
argument_list|(
name|ACL_CONFIG
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|doRPCs
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Reset authorization to expect failure
name|conf
operator|.
name|set
argument_list|(
name|ACL_CONFIG
argument_list|,
literal|"invalid invalid"
argument_list|)
expr_stmt|;
name|doRPCs
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Switch off setting socketTimeout values on RPC sockets.    * Verify that RPC calls still work ok.    */
DECL|method|testNoPings ()
specifier|public
name|void
name|testNoPings
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
name|setBoolean
argument_list|(
literal|"ipc.client.ping"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
operator|new
name|TestRPC
argument_list|(
literal|"testnoPings"
argument_list|)
operator|.
name|testCalls
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|TestRPC
argument_list|(
literal|"test"
argument_list|)
operator|.
name|testCalls
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

