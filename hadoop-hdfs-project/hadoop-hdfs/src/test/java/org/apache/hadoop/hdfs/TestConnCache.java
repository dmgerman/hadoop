begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|Assert
operator|.
name|assertSame
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
name|assertTrue
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
name|spy
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
name|java
operator|.
name|net
operator|.
name|Socket
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeInfo
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
name|protocol
operator|.
name|ExtendedBlock
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
name|protocol
operator|.
name|LocatedBlock
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|server
operator|.
name|datanode
operator|.
name|DataNode
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Matchers
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_comment
comment|/**  * This class tests the client connection caching in a single node  * mini-cluster.  */
end_comment

begin_class
DECL|class|TestConnCache
specifier|public
class|class
name|TestConnCache
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestConnCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|FILE_SIZE
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
literal|3
operator|*
name|BLOCK_SIZE
decl_stmt|;
DECL|field|conf
specifier|static
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|cluster
specifier|static
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|fs
specifier|static
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|field|testFile
specifier|static
specifier|final
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/testConnCache.dat"
argument_list|)
decl_stmt|;
DECL|field|authenticData
specifier|static
name|byte
name|authenticData
index|[]
init|=
literal|null
decl_stmt|;
DECL|field|util
specifier|static
name|BlockReaderTestUtil
name|util
init|=
literal|null
decl_stmt|;
comment|/**    * A mock Answer to remember the BlockReader used.    *    * It verifies that all invocation to DFSInputStream.getBlockReader()    * use the same socket.    */
DECL|class|MockGetBlockReader
specifier|private
class|class
name|MockGetBlockReader
implements|implements
name|Answer
argument_list|<
name|RemoteBlockReader2
argument_list|>
block|{
DECL|field|reader
specifier|public
name|RemoteBlockReader2
name|reader
init|=
literal|null
decl_stmt|;
DECL|field|sock
specifier|private
name|Socket
name|sock
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|RemoteBlockReader2
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|RemoteBlockReader2
name|prevReader
init|=
name|reader
decl_stmt|;
name|reader
operator|=
operator|(
name|RemoteBlockReader2
operator|)
name|invocation
operator|.
name|callRealMethod
argument_list|()
expr_stmt|;
if|if
condition|(
name|sock
operator|==
literal|null
condition|)
block|{
name|sock
operator|=
name|reader
operator|.
name|dnSock
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|prevReader
operator|!=
literal|null
condition|)
block|{
name|assertSame
argument_list|(
literal|"DFSInputStream should use the same socket"
argument_list|,
name|sock
argument_list|,
name|reader
operator|.
name|dnSock
argument_list|)
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
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
name|Exception
block|{
specifier|final
name|int
name|REPLICATION_FACTOR
init|=
literal|1
decl_stmt|;
name|util
operator|=
operator|new
name|BlockReaderTestUtil
argument_list|(
name|REPLICATION_FACTOR
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|util
operator|.
name|getCluster
argument_list|()
expr_stmt|;
name|conf
operator|=
name|util
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|authenticData
operator|=
name|util
operator|.
name|writeFile
argument_list|(
name|testFile
argument_list|,
name|FILE_SIZE
operator|/
literal|1024
argument_list|)
expr_stmt|;
block|}
comment|/**    * (Optionally) seek to position, read and verify data.    *    * Seek to specified position if pos is non-negative.    */
DECL|method|pread (DFSInputStream in, long pos, byte[] buffer, int offset, int length)
specifier|private
name|void
name|pread
parameter_list|(
name|DFSInputStream
name|in
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
literal|"Test buffer too small"
argument_list|,
name|buffer
operator|.
name|length
operator|>=
name|offset
operator|+
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
literal|0
condition|)
name|in
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reading from file of size "
operator|+
name|in
operator|.
name|getFileLength
argument_list|()
operator|+
literal|" at offset "
operator|+
name|in
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|cnt
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Error in read"
argument_list|,
name|cnt
operator|>
literal|0
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|cnt
expr_stmt|;
name|length
operator|-=
name|cnt
expr_stmt|;
block|}
comment|// Verify
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
operator|++
name|i
control|)
block|{
name|byte
name|actual
init|=
name|buffer
index|[
name|i
index|]
decl_stmt|;
name|byte
name|expect
init|=
name|authenticData
index|[
operator|(
name|int
operator|)
name|pos
operator|+
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Read data mismatch at file offset "
operator|+
operator|(
name|pos
operator|+
name|i
operator|)
operator|+
literal|". Expects "
operator|+
name|expect
operator|+
literal|"; got "
operator|+
name|actual
argument_list|,
name|actual
argument_list|,
name|expect
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test the SocketCache itself.    */
annotation|@
name|Test
DECL|method|testSocketCache ()
specifier|public
name|void
name|testSocketCache
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|CACHE_SIZE
init|=
literal|4
decl_stmt|;
name|SocketCache
name|cache
init|=
operator|new
name|SocketCache
argument_list|(
name|CACHE_SIZE
argument_list|)
decl_stmt|;
comment|// Make a client
name|InetSocketAddress
name|nnAddr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|nnAddr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Find out the DN addr
name|LocatedBlock
name|block
init|=
name|client
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|testFile
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|FILE_SIZE
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DataNode
name|dn
init|=
name|util
operator|.
name|getDataNode
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|dnAddr
init|=
name|dn
operator|.
name|getXferAddress
argument_list|()
decl_stmt|;
comment|// Make some sockets to the DN
name|Socket
index|[]
name|dnSockets
init|=
operator|new
name|Socket
index|[
name|CACHE_SIZE
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
name|dnSockets
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|dnSockets
index|[
name|i
index|]
operator|=
name|client
operator|.
name|socketFactory
operator|.
name|createSocket
argument_list|(
name|dnAddr
operator|.
name|getAddress
argument_list|()
argument_list|,
name|dnAddr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Insert a socket to the NN
name|Socket
name|nnSock
init|=
operator|new
name|Socket
argument_list|(
name|nnAddr
operator|.
name|getAddress
argument_list|()
argument_list|,
name|nnAddr
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|nnSock
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Read the write"
argument_list|,
name|nnSock
argument_list|,
name|cache
operator|.
name|get
argument_list|(
name|nnAddr
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|nnSock
argument_list|)
expr_stmt|;
comment|// Insert DN socks
for|for
control|(
name|Socket
name|dnSock
range|:
name|dnSockets
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|dnSock
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"NN socket evicted"
argument_list|,
literal|null
argument_list|,
name|cache
operator|.
name|get
argument_list|(
name|nnAddr
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Evicted socket closed"
argument_list|,
name|nnSock
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
comment|// Lookup the DN socks
for|for
control|(
name|Socket
name|dnSock
range|:
name|dnSockets
control|)
block|{
name|assertEquals
argument_list|(
literal|"Retrieve cached sockets"
argument_list|,
name|dnSock
argument_list|,
name|cache
operator|.
name|get
argument_list|(
name|dnAddr
argument_list|)
argument_list|)
expr_stmt|;
name|dnSock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Cache is empty"
argument_list|,
literal|0
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read a file served entirely from one DN. Seek around and read from    * different offsets. And verify that they all use the same socket.    *    * @throws java.io.IOException    */
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testReadFromOneDN ()
specifier|public
name|void
name|testReadFromOneDN
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting testReadFromOneDN()"
argument_list|)
expr_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DFSInputStream
name|in
init|=
name|spy
argument_list|(
name|client
operator|.
name|open
argument_list|(
name|testFile
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"opened "
operator|+
name|testFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|dataBuf
init|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
index|]
decl_stmt|;
name|MockGetBlockReader
name|answer
init|=
operator|new
name|MockGetBlockReader
argument_list|()
decl_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
name|answer
argument_list|)
operator|.
name|when
argument_list|(
name|in
argument_list|)
operator|.
name|getBlockReader
argument_list|(
operator|(
name|InetSocketAddress
operator|)
name|Matchers
operator|.
name|anyObject
argument_list|()
argument_list|,
operator|(
name|DatanodeInfo
operator|)
name|Matchers
operator|.
name|anyObject
argument_list|()
argument_list|,
name|Matchers
operator|.
name|anyString
argument_list|()
argument_list|,
operator|(
name|ExtendedBlock
operator|)
name|Matchers
operator|.
name|anyObject
argument_list|()
argument_list|,
operator|(
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
operator|)
name|Matchers
operator|.
name|anyObject
argument_list|()
argument_list|,
name|Matchers
operator|.
name|anyLong
argument_list|()
argument_list|,
name|Matchers
operator|.
name|anyLong
argument_list|()
argument_list|,
name|Matchers
operator|.
name|anyInt
argument_list|()
argument_list|,
name|Matchers
operator|.
name|anyBoolean
argument_list|()
argument_list|,
name|Matchers
operator|.
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Initial read
name|pread
argument_list|(
name|in
argument_list|,
literal|0
argument_list|,
name|dataBuf
argument_list|,
literal|0
argument_list|,
name|dataBuf
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Read again and verify that the socket is the same
name|pread
argument_list|(
name|in
argument_list|,
name|FILE_SIZE
operator|-
name|dataBuf
operator|.
name|length
argument_list|,
name|dataBuf
argument_list|,
literal|0
argument_list|,
name|dataBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|pread
argument_list|(
name|in
argument_list|,
literal|1024
argument_list|,
name|dataBuf
argument_list|,
literal|0
argument_list|,
name|dataBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|pread
argument_list|(
name|in
argument_list|,
operator|-
literal|1
argument_list|,
name|dataBuf
argument_list|,
literal|0
argument_list|,
name|dataBuf
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// No seek; just read
name|pread
argument_list|(
name|in
argument_list|,
literal|64
argument_list|,
name|dataBuf
argument_list|,
literal|0
argument_list|,
name|dataBuf
operator|.
name|length
operator|/
literal|2
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that the socket cache can be disabled by setting the capacity to    * 0. Regression test for HDFS-3365.    */
annotation|@
name|Test
DECL|method|testDisableCache ()
specifier|public
name|void
name|testDisableCache
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting testDisableCache()"
argument_list|)
expr_stmt|;
comment|// Reading with the normally configured filesystem should
comment|// cache a socket.
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|DistributedFileSystem
operator|)
name|fs
operator|)
operator|.
name|dfs
operator|.
name|socketCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Configure a new instance with no caching, ensure that it doesn't
comment|// cache anything
name|Configuration
name|confWithoutCache
init|=
operator|new
name|Configuration
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|confWithoutCache
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_CACHE_CAPACITY_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|FileSystem
name|fsWithoutCache
init|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
name|confWithoutCache
argument_list|)
decl_stmt|;
try|try
block|{
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fsWithoutCache
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|DistributedFileSystem
operator|)
name|fsWithoutCache
operator|)
operator|.
name|dfs
operator|.
name|socketCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsWithoutCache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|teardownCluster ()
specifier|public
specifier|static
name|void
name|teardownCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|util
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

