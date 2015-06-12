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
name|DFSConfigKeys
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
name|After
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
annotation|@
name|Test
DECL|method|testTracing ()
specifier|public
name|void
name|testTracing
parameter_list|()
throws|throws
name|Exception
block|{
comment|// write and read without tracing started
name|String
name|fileName
init|=
literal|"testTracingDisabled.dat"
decl_stmt|;
name|writeTestFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|SetSpanReceiver
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|readTestFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|SetSpanReceiver
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|writeWithTracing
argument_list|()
expr_stmt|;
name|readWithTracing
argument_list|()
expr_stmt|;
block|}
DECL|method|writeWithTracing ()
specifier|public
name|void
name|writeWithTracing
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
name|writeTestFile
argument_list|(
literal|"testWriteTraceHooks.dat"
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
literal|"newStreamForCreate"
block|,
literal|"DFSOutputStream#write"
block|,
literal|"DFSOutputStream#close"
block|,
literal|"dataStreamer"
block|,
literal|"OpWriteBlockProto"
block|,
literal|"org.apache.hadoop.hdfs.protocol.ClientProtocol.addBlock"
block|,
literal|"ClientNamenodeProtocol#addBlock"
block|}
decl_stmt|;
name|SetSpanReceiver
operator|.
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
comment|// Spans homed in the top trace shoud have same trace id.
comment|// Spans having multiple parents (e.g. "dataStreamer" added by HDFS-7054)
comment|// and children of them are exception.
name|String
index|[]
name|spansInTopTrace
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
literal|"newStreamForCreate"
block|,
literal|"DFSOutputStream#write"
block|,
literal|"DFSOutputStream#close"
block|,     }
decl_stmt|;
for|for
control|(
name|String
name|desc
range|:
name|spansInTopTrace
control|)
block|{
for|for
control|(
name|Span
name|span
range|:
name|map
operator|.
name|get
argument_list|(
name|desc
argument_list|)
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
comment|// test for timeline annotation added by HADOOP-11242
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"called"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"org.apache.hadoop.hdfs.protocol.ClientProtocol.create"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTimelineAnnotations
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|SetSpanReceiver
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|readWithTracing ()
specifier|public
name|void
name|readWithTracing
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fileName
init|=
literal|"testReadTraceHooks.dat"
decl_stmt|;
name|writeTestFile
argument_list|(
name|fileName
argument_list|)
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
name|readTestFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|ts
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
name|SetSpanReceiver
operator|.
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
name|getSpans
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
name|SetSpanReceiver
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTestFile (String testFileName)
specifier|private
name|void
name|writeTestFile
parameter_list|(
name|String
name|testFileName
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|testFileName
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stream
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
name|hsync
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|readTestFile (String testFileName)
specifier|private
name|void
name|readTestFile
parameter_list|(
name|String
name|testFileName
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|testFileName
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
block|}
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
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
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_HTRACE_PREFIX
operator|+
name|SpanReceiverHost
operator|.
name|SPAN_RECEIVERS_CONF_SUFFIX
argument_list|,
name|SetSpanReceiver
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|startCluster ()
specifier|public
name|void
name|startCluster
parameter_list|()
throws|throws
name|IOException
block|{
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
name|SetSpanReceiver
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutDown ()
specifier|public
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
block|}
end_class

end_unit

