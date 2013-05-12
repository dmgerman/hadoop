begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|assertCounter
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
name|assertGauge
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
name|MockitoMaker
operator|.
name|make
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
name|MockitoMaker
operator|.
name|stub
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffers
operator|.
name|wrappedBuffer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|zip
operator|.
name|CheckedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Checksum
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
name|CommonConfigurationKeysPublic
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
name|FSDataOutputStream
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
name|FileSystem
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
name|Path
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
name|DataOutputBuffer
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenIdentifier
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
name|mapreduce
operator|.
name|task
operator|.
name|reduce
operator|.
name|ShuffleHeader
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
name|metrics2
operator|.
name|MetricsSource
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
name|MetricsSystem
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
name|impl
operator|.
name|MetricsSystemImpl
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
name|util
operator|.
name|PureJavaCrc32
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|ContainerLocalizer
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
name|BuilderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|Channel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelFuture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponseStatus
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
DECL|class|TestShuffleHandler
specifier|public
class|class
name|TestShuffleHandler
block|{
DECL|field|MiB
specifier|static
specifier|final
name|long
name|MiB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestShuffleHandler
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testSerializeMeta ()
specifier|public
name|void
name|testSerializeMeta
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ShuffleHandler
operator|.
name|deserializeMetaData
argument_list|(
name|ShuffleHandler
operator|.
name|serializeMetaData
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|ShuffleHandler
operator|.
name|deserializeMetaData
argument_list|(
name|ShuffleHandler
operator|.
name|serializeMetaData
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8080
argument_list|,
name|ShuffleHandler
operator|.
name|deserializeMetaData
argument_list|(
name|ShuffleHandler
operator|.
name|serializeMetaData
argument_list|(
literal|8080
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testShuffleMetrics ()
specifier|public
name|void
name|testShuffleMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|MetricsSystem
name|ms
init|=
operator|new
name|MetricsSystemImpl
argument_list|()
decl_stmt|;
name|ShuffleHandler
name|sh
init|=
operator|new
name|ShuffleHandler
argument_list|(
name|ms
argument_list|)
decl_stmt|;
name|ChannelFuture
name|cf
init|=
name|make
argument_list|(
name|stub
argument_list|(
name|ChannelFuture
operator|.
name|class
argument_list|)
operator|.
name|returning
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
operator|.
name|from
operator|.
name|isSuccess
argument_list|()
argument_list|)
decl_stmt|;
name|sh
operator|.
name|metrics
operator|.
name|shuffleConnections
operator|.
name|incr
argument_list|()
expr_stmt|;
name|sh
operator|.
name|metrics
operator|.
name|shuffleOutputBytes
operator|.
name|incr
argument_list|(
literal|1
operator|*
name|MiB
argument_list|)
expr_stmt|;
name|sh
operator|.
name|metrics
operator|.
name|shuffleConnections
operator|.
name|incr
argument_list|()
expr_stmt|;
name|sh
operator|.
name|metrics
operator|.
name|shuffleOutputBytes
operator|.
name|incr
argument_list|(
literal|2
operator|*
name|MiB
argument_list|)
expr_stmt|;
name|checkShuffleMetrics
argument_list|(
name|ms
argument_list|,
literal|3
operator|*
name|MiB
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|sh
operator|.
name|metrics
operator|.
name|operationComplete
argument_list|(
name|cf
argument_list|)
expr_stmt|;
name|sh
operator|.
name|metrics
operator|.
name|operationComplete
argument_list|(
name|cf
argument_list|)
expr_stmt|;
name|checkShuffleMetrics
argument_list|(
name|ms
argument_list|,
literal|3
operator|*
name|MiB
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|checkShuffleMetrics (MetricsSystem ms, long bytes, int failed, int succeeded, int connections)
specifier|static
name|void
name|checkShuffleMetrics
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|,
name|long
name|bytes
parameter_list|,
name|int
name|failed
parameter_list|,
name|int
name|succeeded
parameter_list|,
name|int
name|connections
parameter_list|)
block|{
name|MetricsSource
name|source
init|=
name|ms
operator|.
name|getSource
argument_list|(
literal|"ShuffleMetrics"
argument_list|)
decl_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertCounter
argument_list|(
literal|"ShuffleOutputBytes"
argument_list|,
name|bytes
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"ShuffleOutputsFailed"
argument_list|,
name|failed
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"ShuffleOutputsOK"
argument_list|,
name|succeeded
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"ShuffleConnections"
argument_list|,
name|connections
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testClientClosesConnection ()
specifier|public
name|void
name|testClientClosesConnection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
name|failures
init|=
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ShuffleHandler
operator|.
name|SHUFFLE_PORT_CONFIG_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ShuffleHandler
name|shuffleHandler
init|=
operator|new
name|ShuffleHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Shuffle
name|getShuffle
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// replace the shuffle handler with one stubbed for testing
return|return
operator|new
name|Shuffle
argument_list|(
name|conf
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|verifyRequest
parameter_list|(
name|String
name|appid
parameter_list|,
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|HttpRequest
name|request
parameter_list|,
name|HttpResponse
name|response
parameter_list|,
name|URL
name|requestUri
parameter_list|)
throws|throws
name|IOException
block|{           }
annotation|@
name|Override
specifier|protected
name|ChannelFuture
name|sendMapOutput
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Channel
name|ch
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|jobId
parameter_list|,
name|String
name|mapId
parameter_list|,
name|int
name|reduce
parameter_list|)
throws|throws
name|IOException
block|{
comment|// send a shuffle header and a lot of data down the channel
comment|// to trigger a broken pipe
name|ShuffleHeader
name|header
init|=
operator|new
name|ShuffleHeader
argument_list|(
literal|"attempt_12345_1_m_1_0"
argument_list|,
literal|5678
argument_list|,
literal|5678
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|header
operator|.
name|write
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|ch
operator|.
name|write
argument_list|(
name|wrappedBuffer
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|dob
operator|=
operator|new
name|DataOutputBuffer
argument_list|()
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
literal|100000
condition|;
operator|++
name|i
control|)
block|{
name|header
operator|.
name|write
argument_list|(
name|dob
argument_list|)
expr_stmt|;
block|}
return|return
name|ch
operator|.
name|write
argument_list|(
name|wrappedBuffer
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|sendError
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|HttpResponseStatus
name|status
parameter_list|)
block|{
if|if
condition|(
name|failures
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|failures
operator|.
name|add
argument_list|(
operator|new
name|Error
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|getChannel
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|sendError
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|String
name|message
parameter_list|,
name|HttpResponseStatus
name|status
parameter_list|)
block|{
if|if
condition|(
name|failures
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|failures
operator|.
name|add
argument_list|(
operator|new
name|Error
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|getChannel
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|shuffleHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|shuffleHandler
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// simulate a reducer that closes early by reading a single shuffle header
comment|// then closing the connection
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://127.0.0.1:"
operator|+
name|shuffleHandler
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|ShuffleHandler
operator|.
name|SHUFFLE_PORT_CONFIG_KEY
argument_list|)
operator|+
literal|"/mapOutput?job=job_12345_1&reduce=1&map=attempt_12345_1_m_1_0"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|DataInputStream
name|input
init|=
operator|new
name|DataInputStream
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|ShuffleHeader
name|header
init|=
operator|new
name|ShuffleHeader
argument_list|()
decl_stmt|;
name|header
operator|.
name|readFields
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|shuffleHandler
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"sendError called when client closed connection"
argument_list|,
name|failures
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMaxConnections ()
specifier|public
name|void
name|testMaxConnections
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
name|setInt
argument_list|(
name|ShuffleHandler
operator|.
name|SHUFFLE_PORT_CONFIG_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ShuffleHandler
operator|.
name|MAX_SHUFFLE_CONNECTIONS
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|ShuffleHandler
name|shuffleHandler
init|=
operator|new
name|ShuffleHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Shuffle
name|getShuffle
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// replace the shuffle handler with one stubbed for testing
return|return
operator|new
name|Shuffle
argument_list|(
name|conf
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|verifyRequest
parameter_list|(
name|String
name|appid
parameter_list|,
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|HttpRequest
name|request
parameter_list|,
name|HttpResponse
name|response
parameter_list|,
name|URL
name|requestUri
parameter_list|)
throws|throws
name|IOException
block|{           }
annotation|@
name|Override
specifier|protected
name|ChannelFuture
name|sendMapOutput
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Channel
name|ch
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|jobId
parameter_list|,
name|String
name|mapId
parameter_list|,
name|int
name|reduce
parameter_list|)
throws|throws
name|IOException
block|{
comment|// send a shuffle header and a lot of data down the channel
comment|// to trigger a broken pipe
name|ShuffleHeader
name|header
init|=
operator|new
name|ShuffleHeader
argument_list|(
literal|"dummy_header"
argument_list|,
literal|5678
argument_list|,
literal|5678
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|header
operator|.
name|write
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|ch
operator|.
name|write
argument_list|(
name|wrappedBuffer
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|dob
operator|=
operator|new
name|DataOutputBuffer
argument_list|()
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
literal|100000
condition|;
operator|++
name|i
control|)
block|{
name|header
operator|.
name|write
argument_list|(
name|dob
argument_list|)
expr_stmt|;
block|}
return|return
name|ch
operator|.
name|write
argument_list|(
name|wrappedBuffer
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|shuffleHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|shuffleHandler
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// setup connections
name|int
name|connAttempts
init|=
literal|3
decl_stmt|;
name|HttpURLConnection
name|conns
index|[]
init|=
operator|new
name|HttpURLConnection
index|[
name|connAttempts
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
name|connAttempts
condition|;
name|i
operator|++
control|)
block|{
name|String
name|URLstring
init|=
literal|"http://127.0.0.1:"
operator|+
name|shuffleHandler
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|ShuffleHandler
operator|.
name|SHUFFLE_PORT_CONFIG_KEY
argument_list|)
operator|+
literal|"/mapOutput?job=job_12345_1&reduce=1&map=attempt_12345_1_m_"
operator|+
name|i
operator|+
literal|"_0"
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|URLstring
argument_list|)
decl_stmt|;
name|conns
index|[
name|i
index|]
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
block|}
comment|// Try to open numerous connections
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|connAttempts
condition|;
name|i
operator|++
control|)
block|{
name|conns
index|[
name|i
index|]
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
comment|//Ensure first connections are okay
name|conns
index|[
literal|0
index|]
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|int
name|rc
init|=
name|conns
index|[
literal|0
index|]
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|conns
index|[
literal|1
index|]
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|rc
operator|=
name|conns
index|[
literal|1
index|]
operator|.
name|getResponseCode
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|rc
argument_list|)
expr_stmt|;
comment|// This connection should be closed because it to above the limit
try|try
block|{
name|conns
index|[
literal|2
index|]
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|rc
operator|=
name|conns
index|[
literal|2
index|]
operator|.
name|getResponseCode
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected a SocketException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected - connection should not be open"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected a SocketException"
argument_list|)
expr_stmt|;
block|}
name|shuffleHandler
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|100000
argument_list|)
DECL|method|testMapFileAccess ()
specifier|public
name|void
name|testMapFileAccess
parameter_list|()
throws|throws
name|IOException
block|{
comment|// This will run only in NativeIO is enabled as SecureIOUtils need it
name|assumeTrue
argument_list|(
name|NativeIO
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ShuffleHandler
operator|.
name|SHUFFLE_PORT_CONFIG_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ShuffleHandler
operator|.
name|MAX_SHUFFLE_CONNECTIONS
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|File
name|absLogDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestShuffleHandler
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"LocDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|absLogDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|12345
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|appAttemptId
init|=
literal|"attempt_12345_1_m_1_0"
decl_stmt|;
name|String
name|user
init|=
literal|"randomUser"
decl_stmt|;
name|String
name|reducerId
init|=
literal|"0"
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|fileMap
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
name|createShuffleHandlerFiles
argument_list|(
name|absLogDir
argument_list|,
name|user
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
name|appAttemptId
argument_list|,
name|conf
argument_list|,
name|fileMap
argument_list|)
expr_stmt|;
name|ShuffleHandler
name|shuffleHandler
init|=
operator|new
name|ShuffleHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Shuffle
name|getShuffle
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// replace the shuffle handler with one stubbed for testing
return|return
operator|new
name|Shuffle
argument_list|(
name|conf
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|verifyRequest
parameter_list|(
name|String
name|appid
parameter_list|,
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|HttpRequest
name|request
parameter_list|,
name|HttpResponse
name|response
parameter_list|,
name|URL
name|requestUri
parameter_list|)
throws|throws
name|IOException
block|{           }
block|}
return|;
block|}
block|}
decl_stmt|;
name|shuffleHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|shuffleHandler
operator|.
name|start
argument_list|()
expr_stmt|;
name|DataOutputBuffer
name|outputBuffer
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|outputBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|jt
init|=
operator|new
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
argument_list|(
literal|"identifier"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"password"
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|user
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"shuffleService"
argument_list|)
argument_list|)
decl_stmt|;
name|jt
operator|.
name|write
argument_list|(
name|outputBuffer
argument_list|)
expr_stmt|;
name|shuffleHandler
operator|.
name|initApp
argument_list|(
name|user
argument_list|,
name|appId
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|outputBuffer
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|outputBuffer
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://127.0.0.1:"
operator|+
name|shuffleHandler
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|ShuffleHandler
operator|.
name|SHUFFLE_PORT_CONFIG_KEY
argument_list|)
operator|+
literal|"/mapOutput?job=job_12345_0001&reduce="
operator|+
name|reducerId
operator|+
literal|"&map=attempt_12345_1_m_1_0"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|byte
index|[]
name|byteArr
init|=
operator|new
name|byte
index|[
literal|10000
index|]
decl_stmt|;
try|try
block|{
name|DataInputStream
name|is
init|=
operator|new
name|DataInputStream
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|is
operator|.
name|readFully
argument_list|(
name|byteArr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
comment|// Retrieve file owner name
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|fileMap
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|owner
init|=
name|NativeIO
operator|.
name|POSIX
operator|.
name|getFstat
argument_list|(
name|is
operator|.
name|getFD
argument_list|()
argument_list|)
operator|.
name|getOwner
argument_list|()
decl_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|message
init|=
literal|"Owner '"
operator|+
name|owner
operator|+
literal|"' for path "
operator|+
name|fileMap
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" did not match expected owner '"
operator|+
name|user
operator|+
literal|"'"
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
operator|new
name|String
argument_list|(
name|byteArr
argument_list|)
operator|)
operator|.
name|contains
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|shuffleHandler
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createShuffleHandlerFiles (File logDir, String user, String appId, String appAttemptId, Configuration conf, List<File> fileMap)
specifier|public
specifier|static
name|void
name|createShuffleHandlerFiles
parameter_list|(
name|File
name|logDir
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|appAttemptId
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|File
argument_list|>
name|fileMap
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|attemptDir
init|=
name|StringUtils
operator|.
name|join
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|,
operator|new
name|String
index|[]
block|{
name|logDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
name|ContainerLocalizer
operator|.
name|USERCACHE
block|,
name|user
block|,
name|ContainerLocalizer
operator|.
name|APPCACHE
block|,
name|appId
block|,
literal|"output"
block|,
name|appAttemptId
block|}
argument_list|)
decl_stmt|;
name|File
name|appAttemptDir
init|=
operator|new
name|File
argument_list|(
name|attemptDir
argument_list|)
decl_stmt|;
name|appAttemptDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|appAttemptDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|indexFile
init|=
operator|new
name|File
argument_list|(
name|appAttemptDir
argument_list|,
literal|"file.out.index"
argument_list|)
decl_stmt|;
name|fileMap
operator|.
name|add
argument_list|(
name|indexFile
argument_list|)
expr_stmt|;
name|createIndexFile
argument_list|(
name|indexFile
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|File
name|mapOutputFile
init|=
operator|new
name|File
argument_list|(
name|appAttemptDir
argument_list|,
literal|"file.out"
argument_list|)
decl_stmt|;
name|fileMap
operator|.
name|add
argument_list|(
name|mapOutputFile
argument_list|)
expr_stmt|;
name|createMapOutputFile
argument_list|(
name|mapOutputFile
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
DECL|method|createMapOutputFile (File mapOutputFile, Configuration conf)
name|createMapOutputFile
parameter_list|(
name|File
name|mapOutputFile
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|mapOutputFile
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"Creating new dummy map output file. Used only for testing"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndexFile (File indexFile, Configuration conf)
specifier|public
specifier|static
name|void
name|createIndexFile
parameter_list|(
name|File
name|indexFile
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deleting existing file"
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|indexFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|FSDataOutputStream
name|output
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getRaw
argument_list|()
operator|.
name|append
argument_list|(
operator|new
name|Path
argument_list|(
name|indexFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Checksum
name|crc
init|=
operator|new
name|PureJavaCrc32
argument_list|()
decl_stmt|;
name|crc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CheckedOutputStream
name|chk
init|=
operator|new
name|CheckedOutputStream
argument_list|(
name|output
argument_list|,
name|crc
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
literal|"Writing new index file. This file will be used only "
operator|+
literal|"for the testing."
decl_stmt|;
name|chk
operator|.
name|write
argument_list|(
name|Arrays
operator|.
name|copyOf
argument_list|(
name|msg
operator|.
name|getBytes
argument_list|()
argument_list|,
name|MapTask
operator|.
name|MAP_OUTPUT_INDEX_RECORD_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|chk
operator|.
name|getChecksum
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

