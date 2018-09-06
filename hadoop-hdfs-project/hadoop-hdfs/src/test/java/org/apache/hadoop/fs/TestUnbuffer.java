begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

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
name|PeerCache
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestUnbuffer
specifier|public
class|class
name|TestUnbuffer
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestUnbuffer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
comment|/**    * Test that calling Unbuffer closes sockets.    */
annotation|@
name|Test
DECL|method|testUnbufferClosesSockets ()
specifier|public
name|void
name|testUnbufferClosesSockets
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
comment|// Set a new ClientContext.  This way, we will have our own PeerCache,
comment|// rather than sharing one with other unit tests.
name|conf
operator|.
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_CONTEXT
argument_list|,
literal|"testUnbufferClosesSocketsContext"
argument_list|)
expr_stmt|;
comment|// Disable short-circuit reads.  With short-circuit, we wouldn't hold open a
comment|// TCP socket.
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
literal|false
argument_list|)
expr_stmt|;
comment|// Set a really long socket timeout to avoid test timing issues.
name|conf
operator|.
name|setLong
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
literal|100000000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_CACHE_EXPIRY_MSEC_KEY
argument_list|,
literal|100000000L
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|FSDataInputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
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
name|build
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/test1"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|TEST_PATH
argument_list|,
literal|128
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|stream
operator|=
name|dfs
operator|.
name|open
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
comment|// Read a byte.  This will trigger the creation of a block reader.
name|stream
operator|.
name|seek
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|int
name|b
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|-
literal|1
operator|!=
name|b
argument_list|)
expr_stmt|;
comment|// The Peer cache should start off empty.
name|PeerCache
name|cache
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getClientContext
argument_list|()
operator|.
name|getPeerCache
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Unbuffer should clear the block reader and return the socket to the
comment|// cache.
name|stream
operator|.
name|unbuffer
argument_list|()
expr_stmt|;
name|stream
operator|.
name|seek
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|b2
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|b
argument_list|,
name|b2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test opening many files via TCP (not short-circuit).    *    * This is practical when using unbuffer, because it reduces the number of    * sockets and amount of memory that we use.    */
annotation|@
name|Test
DECL|method|testOpenManyFilesViaTcp ()
specifier|public
name|void
name|testOpenManyFilesViaTcp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_OPENS
init|=
literal|500
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
literal|false
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|FSDataInputStream
index|[]
name|streams
init|=
operator|new
name|FSDataInputStream
index|[
name|NUM_OPENS
index|]
decl_stmt|;
try|try
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
name|build
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/testFile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|TEST_PATH
argument_list|,
literal|131072
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1
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
name|NUM_OPENS
condition|;
name|i
operator|++
control|)
block|{
name|streams
index|[
name|i
index|]
operator|=
name|dfs
operator|.
name|open
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"opening file "
operator|+
name|i
operator|+
literal|"..."
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|-
literal|1
operator|!=
name|streams
index|[
name|i
index|]
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|streams
index|[
name|i
index|]
operator|.
name|unbuffer
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
for|for
control|(
name|FSDataInputStream
name|stream
range|:
name|streams
control|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test that a InputStream should throw an exception when not implementing    * CanUnbuffer    *    * This should throw an exception when the stream claims to have the    * unbuffer capability, but actually does not implement CanUnbuffer.    */
annotation|@
name|Test
DECL|method|testUnbufferException ()
specifier|public
name|void
name|testUnbufferException
parameter_list|()
block|{
specifier|abstract
class|class
name|BuggyStream
extends|extends
name|FSInputStream
implements|implements
name|StreamCapabilities
block|{     }
name|BuggyStream
name|bs
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|BuggyStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|bs
operator|.
name|hasCapability
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|StreamCapabilitiesPolicy
operator|.
name|CAN_UNBUFFER_NOT_IMPLEMENTED_MESSAGE
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|fs
init|=
operator|new
name|FSDataInputStream
argument_list|(
name|bs
argument_list|)
decl_stmt|;
name|fs
operator|.
name|unbuffer
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

