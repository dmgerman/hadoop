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
name|util
operator|.
name|StringUtils
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
name|io
operator|.
name|DataInput
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
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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

begin_comment
comment|/** Unit tests for IPC. */
end_comment

begin_class
DECL|class|TestIPC
specifier|public
class|class
name|TestIPC
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
name|TestIPC
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|final
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|PING_INTERVAL
specifier|final
specifier|static
specifier|private
name|int
name|PING_INTERVAL
init|=
literal|1000
decl_stmt|;
static|static
block|{
name|Client
operator|.
name|setPingInterval
argument_list|(
name|conf
argument_list|,
name|PING_INTERVAL
argument_list|)
expr_stmt|;
block|}
DECL|method|TestIPC (String name)
specifier|public
name|TestIPC
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
DECL|method|TestServer (int handlerCount, boolean sleep)
specifier|public
name|TestServer
parameter_list|(
name|int
name|handlerCount
parameter_list|,
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
name|LongWritable
operator|.
name|class
argument_list|,
name|handlerCount
argument_list|,
name|conf
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
literal|2
operator|*
name|PING_INTERVAL
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
comment|// echo param as result
block|}
block|}
DECL|class|SerialCaller
specifier|private
specifier|static
class|class
name|SerialCaller
extends|extends
name|Thread
block|{
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
DECL|field|server
specifier|private
name|InetSocketAddress
name|server
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|failed
specifier|private
name|boolean
name|failed
decl_stmt|;
DECL|method|SerialCaller (Client client, InetSocketAddress server, int count)
specifier|public
name|SerialCaller
parameter_list|(
name|Client
name|client
parameter_list|,
name|InetSocketAddress
name|server
parameter_list|,
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
name|server
operator|=
name|server
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
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
name|LongWritable
name|param
init|=
operator|new
name|LongWritable
argument_list|(
name|RANDOM
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|LongWritable
name|value
init|=
operator|(
name|LongWritable
operator|)
name|client
operator|.
name|call
argument_list|(
name|param
argument_list|,
name|server
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|param
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Call failed!"
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
break|break;
block|}
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
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
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
DECL|class|ParallelCaller
specifier|private
specifier|static
class|class
name|ParallelCaller
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
DECL|field|addresses
specifier|private
name|InetSocketAddress
index|[]
name|addresses
decl_stmt|;
DECL|field|failed
specifier|private
name|boolean
name|failed
decl_stmt|;
DECL|method|ParallelCaller (Client client, InetSocketAddress[] addresses, int count)
specifier|public
name|ParallelCaller
parameter_list|(
name|Client
name|client
parameter_list|,
name|InetSocketAddress
index|[]
name|addresses
parameter_list|,
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
name|addresses
operator|=
name|addresses
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
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
name|Writable
index|[]
name|params
init|=
operator|new
name|Writable
index|[
name|addresses
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|addresses
operator|.
name|length
condition|;
name|j
operator|++
control|)
name|params
index|[
name|j
index|]
operator|=
operator|new
name|LongWritable
argument_list|(
name|RANDOM
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|Writable
index|[]
name|values
init|=
name|client
operator|.
name|call
argument_list|(
name|params
argument_list|,
name|addresses
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|addresses
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|params
index|[
name|j
index|]
operator|.
name|equals
argument_list|(
name|values
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Call failed!"
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
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
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
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
DECL|method|testSerial ()
specifier|public
name|void
name|testSerial
parameter_list|()
throws|throws
name|Exception
block|{
name|testSerial
argument_list|(
literal|3
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|testSerial (int handlerCount, boolean handlerSleep, int clientCount, int callerCount, int callCount)
specifier|public
name|void
name|testSerial
parameter_list|(
name|int
name|handlerCount
parameter_list|,
name|boolean
name|handlerSleep
parameter_list|,
name|int
name|clientCount
parameter_list|,
name|int
name|callerCount
parameter_list|,
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
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|LongWritable
operator|.
name|class
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|SerialCaller
index|[]
name|callers
init|=
operator|new
name|SerialCaller
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
name|SerialCaller
argument_list|(
name|clients
index|[
name|i
operator|%
name|clientCount
index|]
argument_list|,
name|addr
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
DECL|method|testParallel ()
specifier|public
name|void
name|testParallel
parameter_list|()
throws|throws
name|Exception
block|{
name|testParallel
argument_list|(
literal|10
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|testParallel (int handlerCount, boolean handlerSleep, int serverCount, int addressCount, int clientCount, int callerCount, int callCount)
specifier|public
name|void
name|testParallel
parameter_list|(
name|int
name|handlerCount
parameter_list|,
name|boolean
name|handlerSleep
parameter_list|,
name|int
name|serverCount
parameter_list|,
name|int
name|addressCount
parameter_list|,
name|int
name|clientCount
parameter_list|,
name|int
name|callerCount
parameter_list|,
name|int
name|callCount
parameter_list|)
throws|throws
name|Exception
block|{
name|Server
index|[]
name|servers
init|=
operator|new
name|Server
index|[
name|serverCount
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
name|serverCount
condition|;
name|i
operator|++
control|)
block|{
name|servers
index|[
name|i
index|]
operator|=
operator|new
name|TestServer
argument_list|(
name|handlerCount
argument_list|,
name|handlerSleep
argument_list|)
expr_stmt|;
name|servers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|InetSocketAddress
index|[]
name|addresses
init|=
operator|new
name|InetSocketAddress
index|[
name|addressCount
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
name|addressCount
condition|;
name|i
operator|++
control|)
block|{
name|addresses
index|[
name|i
index|]
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|servers
index|[
name|i
operator|%
name|serverCount
index|]
argument_list|)
expr_stmt|;
block|}
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
name|LongWritable
operator|.
name|class
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|ParallelCaller
index|[]
name|callers
init|=
operator|new
name|ParallelCaller
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
name|ParallelCaller
argument_list|(
name|clients
index|[
name|i
operator|%
name|clientCount
index|]
argument_list|,
name|addresses
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|serverCount
condition|;
name|i
operator|++
control|)
block|{
name|servers
index|[
name|i
index|]
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testStandAloneClient ()
specifier|public
name|void
name|testStandAloneClient
parameter_list|()
throws|throws
name|Exception
block|{
name|testParallel
argument_list|(
literal|10
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
try|try
block|{
name|client
operator|.
name|call
argument_list|(
operator|new
name|LongWritable
argument_list|(
name|RANDOM
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|address
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception to have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|String
name|addressText
init|=
name|address
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not find "
operator|+
name|addressText
operator|+
literal|" in "
operator|+
name|message
argument_list|,
name|message
operator|.
name|contains
argument_list|(
name|addressText
argument_list|)
argument_list|)
expr_stmt|;
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No nested exception in "
operator|+
name|e
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|String
name|causeText
init|=
name|cause
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not find "
operator|+
name|causeText
operator|+
literal|" in "
operator|+
name|message
argument_list|,
name|message
operator|.
name|contains
argument_list|(
name|causeText
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LongErrorWritable
specifier|private
specifier|static
class|class
name|LongErrorWritable
extends|extends
name|LongWritable
block|{
DECL|field|ERR_MSG
specifier|private
specifier|final
specifier|static
name|String
name|ERR_MSG
init|=
literal|"Come across an exception while reading"
decl_stmt|;
DECL|method|LongErrorWritable ()
name|LongErrorWritable
parameter_list|()
block|{}
DECL|method|LongErrorWritable (long longValue)
name|LongErrorWritable
parameter_list|(
name|long
name|longValue
parameter_list|)
block|{
name|super
argument_list|(
name|longValue
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|ERR_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|LongRTEWritable
specifier|private
specifier|static
class|class
name|LongRTEWritable
extends|extends
name|LongWritable
block|{
DECL|field|ERR_MSG
specifier|private
specifier|final
specifier|static
name|String
name|ERR_MSG
init|=
literal|"Come across an runtime exception while reading"
decl_stmt|;
DECL|method|LongRTEWritable ()
name|LongRTEWritable
parameter_list|()
block|{}
DECL|method|LongRTEWritable (long longValue)
name|LongRTEWritable
parameter_list|(
name|long
name|longValue
parameter_list|)
block|{
name|super
argument_list|(
name|longValue
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ERR_MSG
argument_list|)
throw|;
block|}
block|}
DECL|method|testErrorClient ()
specifier|public
name|void
name|testErrorClient
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start server
name|Server
name|server
init|=
operator|new
name|TestServer
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// start client
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
name|LongErrorWritable
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|client
operator|.
name|call
argument_list|(
operator|new
name|LongErrorWritable
argument_list|(
name|RANDOM
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|addr
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception to have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// check error
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|cause
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LongErrorWritable
operator|.
name|ERR_MSG
argument_list|,
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRuntimeExceptionWritable ()
specifier|public
name|void
name|testRuntimeExceptionWritable
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start server
name|Server
name|server
init|=
operator|new
name|TestServer
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// start client
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
name|LongRTEWritable
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|client
operator|.
name|call
argument_list|(
operator|new
name|LongRTEWritable
argument_list|(
name|RANDOM
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|addr
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception to have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// check error
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|cause
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
comment|// it's double-wrapped
name|Throwable
name|cause2
init|=
name|cause
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|cause2
operator|instanceof
name|RuntimeException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LongRTEWritable
operator|.
name|ERR_MSG
argument_list|,
name|cause2
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that, if the socket factory throws an IOE, it properly propagates    * to the client.    */
DECL|method|testSocketFactoryException ()
specifier|public
name|void
name|testSocketFactoryException
parameter_list|()
throws|throws
name|Exception
block|{
name|SocketFactory
name|mockFactory
init|=
name|mock
argument_list|(
name|SocketFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Injected fault"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockFactory
argument_list|)
operator|.
name|createSocket
argument_list|()
expr_stmt|;
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|,
name|conf
argument_list|,
name|mockFactory
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
try|try
block|{
name|client
operator|.
name|call
argument_list|(
operator|new
name|LongWritable
argument_list|(
name|RANDOM
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|address
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception to have been thrown"
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
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Injected fault"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
comment|//new TestIPC("test").testSerial(5, false, 2, 10, 1000);
operator|new
name|TestIPC
argument_list|(
literal|"test"
argument_list|)
operator|.
name|testParallel
argument_list|(
literal|10
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

