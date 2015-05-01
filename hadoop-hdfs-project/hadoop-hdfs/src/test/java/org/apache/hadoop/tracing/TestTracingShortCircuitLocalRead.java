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
name|IOException
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
name|DFSTestUtil
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
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
name|IOUtils
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
name|unix
operator|.
name|DomainSocket
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
name|unix
operator|.
name|TemporarySocketDirectory
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
name|NativeCodeLoader
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

begin_class
DECL|class|TestTracingShortCircuitLocalRead
specifier|public
class|class
name|TestTracingShortCircuitLocalRead
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
DECL|field|sockDir
specifier|private
specifier|static
name|TemporarySocketDirectory
name|sockDir
decl_stmt|;
DECL|field|TEST_PATH
specifier|static
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"testShortCircuitTraceHooks"
argument_list|)
decl_stmt|;
DECL|field|TEST_LENGTH
specifier|static
specifier|final
name|int
name|TEST_LENGTH
init|=
literal|1234
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{
name|sockDir
operator|=
operator|new
name|TemporarySocketDirectory
argument_list|()
expr_stmt|;
name|DomainSocket
operator|.
name|disableBindPathValidation
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
name|sockDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShortCircuitTraceHooks ()
specifier|public
name|void
name|testShortCircuitTraceHooks
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
operator|&&
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
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
name|TestTracing
operator|.
name|SetSpanReceiver
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
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
name|setBoolean
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Read
operator|.
name|ShortCircuit
operator|.
name|KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Read
operator|.
name|ShortCircuit
operator|.
name|SKIP_CHECKSUM_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DOMAIN_SOCKET_PATH_KEY
argument_list|,
literal|"testShortCircuitTraceHooks._PORT"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CHECKSUM_TYPE_KEY
argument_list|,
literal|"CRC32C"
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
try|try
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|TEST_PATH
argument_list|,
name|TEST_LENGTH
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|5678L
argument_list|)
expr_stmt|;
name|TraceScope
name|ts
init|=
name|Trace
operator|.
name|startSpan
argument_list|(
literal|"testShortCircuitTraceHooks"
argument_list|,
name|Sampler
operator|.
name|ALWAYS
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|stream
init|=
name|dfs
operator|.
name|open
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|TEST_LENGTH
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|TEST_LENGTH
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"OpRequestShortCircuitAccessProto"
block|,
literal|"ShortCircuitShmRequestProto"
block|}
decl_stmt|;
name|TestTracing
operator|.
name|assertSpanNamesFound
argument_list|(
name|expectedSpanNames
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

