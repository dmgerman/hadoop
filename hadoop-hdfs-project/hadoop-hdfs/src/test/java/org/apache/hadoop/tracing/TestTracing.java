begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tracing
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tracing
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
name|lang
operator|.
name|RandomStringUtils
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
name|FSDataInputStream
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
name|hdfs
operator|.
name|DistributedFileSystem
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|HTraceConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|Sampler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|Span
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|SpanReceiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|Trace
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|TraceScope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|BeforeClass
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Map
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
name|ConcurrentHashMap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
import|;
end_import

begin_class
DECL|class|TestTracing
specifier|public
class|class
name|TestTracing
block|{
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfs
specifier|private
specifier|static
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|spanReceiverHost
specifier|private
specifier|static
name|SpanReceiverHost
name|spanReceiverHost
decl_stmt|;
annotation|@
name|Test
DECL|method|testGetSpanReceiverHost ()
specifier|public
name|void
name|testGetSpanReceiverHost
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|c
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// getting instance already loaded.
name|c
operator|.
name|set
argument_list|(
name|SpanReceiverHost
operator|.
name|SPAN_RECEIVERS_CONF_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|SpanReceiverHost
name|s
init|=
name|SpanReceiverHost
operator|.
name|getInstance
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|spanReceiverHost
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteTraceHooks ()
specifier|public
name|void
name|testWriteTraceHooks
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|TraceScope
name|ts
init|=
name|Trace
operator|.
name|startSpan
argument_list|(
literal|"testWriteTraceHooks"
argument_list|,
name|Sampler
operator|.
name|ALWAYS
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"traceWriteTest.dat"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stream
init|=
name|dfs
operator|.
name|create
argument_list|(
name|file
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|data
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|102400
argument_list|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
index|[]
name|expectedSpanNames
init|=
block|{
literal|"testWriteTraceHooks"
block|,
literal|"org.apache.hadoop.hdfs.protocol.ClientProtocol.create"
block|,
literal|"ClientNamenodeProtocol#create"
block|,
literal|"org.apache.hadoop.hdfs.protocol.ClientProtocol.fsync"
block|,
literal|"ClientNamenodeProtocol#fsync"
block|,
literal|"org.apache.hadoop.hdfs.protocol.ClientProtocol.complete"
block|,
literal|"ClientNamenodeProtocol#complete"
block|,
literal|"DFSOutputStream"
block|,
literal|"OpWriteBlockProto"
block|,
literal|"org.apache.hadoop.hdfs.protocol.ClientProtocol.addBlock"
block|,
literal|"ClientNamenodeProtocol#addBlock"
block|}
decl_stmt|;
name|assertSpanNamesFound
argument_list|(
name|expectedSpanNames
argument_list|)
expr_stmt|;
comment|// The trace should last about the same amount of time as the test
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Span
argument_list|>
argument_list|>
name|map
init|=
name|SetSpanReceiver
operator|.
name|SetHolder
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|Span
name|s
init|=
name|map
operator|.
name|get
argument_list|(
literal|"testWriteTraceHooks"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|long
name|spanStart
init|=
name|s
operator|.
name|getStartTimeMillis
argument_list|()
decl_stmt|;
name|long
name|spanEnd
init|=
name|s
operator|.
name|getStopTimeMillis
argument_list|()
decl_stmt|;
comment|// There should only be one trace id as it should all be homed in the
comment|// top trace.
for|for
control|(
name|Span
name|span
range|:
name|SetSpanReceiver
operator|.
name|SetHolder
operator|.
name|spans
operator|.
name|values
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ts
operator|.
name|getSpan
argument_list|()
operator|.
name|getTraceId
argument_list|()
argument_list|,
name|span
operator|.
name|getTraceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteWithoutTraceHooks ()
specifier|public
name|void
name|testWriteWithoutTraceHooks
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"withoutTraceWriteTest.dat"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stream
init|=
name|dfs
operator|.
name|create
argument_list|(
name|file
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|data
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|102400
argument_list|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|SetSpanReceiver
operator|.
name|SetHolder
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
DECL|method|testReadTraceHooks ()
specifier|public
name|void
name|testReadTraceHooks
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fileName
init|=
literal|"traceReadTest.dat"
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
comment|// Create the file.
name|FSDataOutputStream
name|ostream
init|=
name|dfs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
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
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|data
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|10240
argument_list|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|ostream
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|ostream
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|TraceScope
name|ts
init|=
name|Trace
operator|.
name|startSpan
argument_list|(
literal|"testReadTraceHooks"
argument_list|,
name|Sampler
operator|.
name|ALWAYS
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|istream
init|=
name|dfs
operator|.
name|open
argument_list|(
name|filePath
argument_list|,
literal|10240
argument_list|)
decl_stmt|;
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|10240
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
name|istream
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|>
literal|0
condition|)
block|{
name|count
operator|+=
literal|1
expr_stmt|;
name|buf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|istream
operator|.
name|seek
argument_list|(
name|istream
operator|.
name|getPos
argument_list|()
operator|+
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// Ignore this it's probably a seek after eof.
block|}
finally|finally
block|{
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|ts
operator|.
name|getSpan
argument_list|()
operator|.
name|addTimelineAnnotation
argument_list|(
literal|"count: "
operator|+
name|count
argument_list|)
expr_stmt|;
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
index|[]
name|expectedSpanNames
init|=
block|{
literal|"testReadTraceHooks"
block|,
literal|"org.apache.hadoop.hdfs.protocol.ClientProtocol.getBlockLocations"
block|,
literal|"ClientNamenodeProtocol#getBlockLocations"
block|,
literal|"OpReadBlockProto"
block|}
decl_stmt|;
name|assertSpanNamesFound
argument_list|(
name|expectedSpanNames
argument_list|)
expr_stmt|;
comment|// The trace should last about the same amount of time as the test
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Span
argument_list|>
argument_list|>
name|map
init|=
name|SetSpanReceiver
operator|.
name|SetHolder
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|Span
name|s
init|=
name|map
operator|.
name|get
argument_list|(
literal|"testReadTraceHooks"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|long
name|spanStart
init|=
name|s
operator|.
name|getStartTimeMillis
argument_list|()
decl_stmt|;
name|long
name|spanEnd
init|=
name|s
operator|.
name|getStopTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|spanStart
operator|-
name|startTime
operator|<
literal|100
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|spanEnd
operator|-
name|endTime
operator|<
literal|100
argument_list|)
expr_stmt|;
comment|// There should only be one trace id as it should all be homed in the
comment|// top trace.
for|for
control|(
name|Span
name|span
range|:
name|SetSpanReceiver
operator|.
name|SetHolder
operator|.
name|spans
operator|.
name|values
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ts
operator|.
name|getSpan
argument_list|()
operator|.
name|getTraceId
argument_list|()
argument_list|,
name|span
operator|.
name|getTraceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadWithoutTraceHooks ()
specifier|public
name|void
name|testReadWithoutTraceHooks
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fileName
init|=
literal|"withoutTraceReadTest.dat"
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
comment|// Create the file.
name|FSDataOutputStream
name|ostream
init|=
name|dfs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
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
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|data
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|10240
argument_list|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|ostream
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|ostream
operator|.
name|close
argument_list|()
expr_stmt|;
name|FSDataInputStream
name|istream
init|=
name|dfs
operator|.
name|open
argument_list|(
name|filePath
argument_list|,
literal|10240
argument_list|)
decl_stmt|;
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|10240
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
name|istream
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|>
literal|0
condition|)
block|{
name|count
operator|+=
literal|1
expr_stmt|;
name|buf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|istream
operator|.
name|seek
argument_list|(
name|istream
operator|.
name|getPos
argument_list|()
operator|+
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// Ignore this it's probably a seek after eof.
block|}
finally|finally
block|{
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|SetSpanReceiver
operator|.
name|SetHolder
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|cleanSet ()
specifier|public
name|void
name|cleanSet
parameter_list|()
block|{
name|SetSpanReceiver
operator|.
name|SetHolder
operator|.
name|spans
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setupCluster ()
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
literal|"dfs.blocksize"
argument_list|,
literal|100
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SpanReceiverHost
operator|.
name|SPAN_RECEIVERS_CONF_KEY
argument_list|,
name|SetSpanReceiver
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|spanReceiverHost
operator|=
name|SpanReceiverHost
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutDown ()
specifier|public
specifier|static
name|void
name|shutDown
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|assertSpanNamesFound (final String[] expectedSpanNames)
specifier|static
name|void
name|assertSpanNamesFound
parameter_list|(
specifier|final
name|String
index|[]
name|expectedSpanNames
parameter_list|)
block|{
try|try
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Span
argument_list|>
argument_list|>
name|map
init|=
name|SetSpanReceiver
operator|.
name|SetHolder
operator|.
name|getMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|spanName
range|:
name|expectedSpanNames
control|)
block|{
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|spanName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"timed out to get expected spans: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"interrupted while waiting spans: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Span receiver that puts all spans into a single set.    * This is useful for testing.    *<p/>    * We're not using HTrace's POJOReceiver here so as that doesn't    * push all the metrics to a static place, and would make testing    * SpanReceiverHost harder.    */
DECL|class|SetSpanReceiver
specifier|public
specifier|static
class|class
name|SetSpanReceiver
implements|implements
name|SpanReceiver
block|{
DECL|method|SetSpanReceiver (HTraceConfiguration conf)
specifier|public
name|SetSpanReceiver
parameter_list|(
name|HTraceConfiguration
name|conf
parameter_list|)
block|{     }
DECL|method|receiveSpan (Span span)
specifier|public
name|void
name|receiveSpan
parameter_list|(
name|Span
name|span
parameter_list|)
block|{
name|SetHolder
operator|.
name|spans
operator|.
name|put
argument_list|(
name|span
operator|.
name|getSpanId
argument_list|()
argument_list|,
name|span
argument_list|)
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{     }
DECL|class|SetHolder
specifier|public
specifier|static
class|class
name|SetHolder
block|{
DECL|field|spans
specifier|public
specifier|static
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|Span
argument_list|>
name|spans
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|Span
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|size ()
specifier|public
specifier|static
name|int
name|size
parameter_list|()
block|{
return|return
name|spans
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getMap ()
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Span
argument_list|>
argument_list|>
name|getMap
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Span
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Span
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Span
name|s
range|:
name|spans
operator|.
name|values
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Span
argument_list|>
name|l
init|=
name|map
operator|.
name|get
argument_list|(
name|s
operator|.
name|getDescription
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|LinkedList
argument_list|<
name|Span
argument_list|>
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|s
operator|.
name|getDescription
argument_list|()
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

