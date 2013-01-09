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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_MAX_BLOCK_ACQUIRE_FAILURES_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SOCKET_REUSE_KEEPALIVE_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SOCKET_WRITE_TIMEOUT_KEY
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
name|Assert
operator|.
name|assertNotNull
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
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|MiniDFSCluster
operator|.
name|DataNodeProperties
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
name|net
operator|.
name|Peer
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DataNodeTestUtils
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
name|protocol
operator|.
name|DatanodeRegistration
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
name|util
operator|.
name|ReflectionUtils
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|NullOutputStream
import|;
end_import

begin_class
DECL|class|TestDataTransferKeepalive
specifier|public
class|class
name|TestDataTransferKeepalive
block|{
DECL|field|conf
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|dnAddr
specifier|private
name|InetSocketAddress
name|dnAddr
decl_stmt|;
DECL|field|dn
specifier|private
name|DataNode
name|dn
decl_stmt|;
DECL|field|dfsClient
specifier|private
name|DFSClient
name|dfsClient
decl_stmt|;
DECL|field|TEST_FILE
specifier|private
specifier|static
name|Path
name|TEST_FILE
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
DECL|field|KEEPALIVE_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|KEEPALIVE_TIMEOUT
init|=
literal|1000
decl_stmt|;
DECL|field|WRITE_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|WRITE_TIMEOUT
init|=
literal|3000
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_DATANODE_SOCKET_REUSE_KEEPALIVE_KEY
argument_list|,
name|KEEPALIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_CLIENT_MAX_BLOCK_ACQUIRE_FAILURES_KEY
argument_list|,
literal|0
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
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfsClient
operator|=
operator|(
operator|(
name|DistributedFileSystem
operator|)
name|fs
operator|)
operator|.
name|dfs
expr_stmt|;
name|String
name|poolId
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|dn
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|DatanodeRegistration
name|dnReg
init|=
name|DataNodeTestUtils
operator|.
name|getDNRegistrationForBP
argument_list|(
name|dn
argument_list|,
name|poolId
argument_list|)
decl_stmt|;
name|dnAddr
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|dnReg
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Regression test for HDFS-3357. Check that the datanode is respecting    * its configured keepalive timeout.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testKeepaliveTimeouts ()
specifier|public
name|void
name|testKeepaliveTimeouts
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|TEST_FILE
argument_list|,
literal|1L
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|// Clients that write aren't currently re-used.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient
operator|.
name|peerCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertXceiverCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Reads the file, so we should get a
comment|// cached socket, and should have an xceiver on the other side.
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|TEST_FILE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dfsClient
operator|.
name|peerCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertXceiverCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Sleep for a bit longer than the keepalive timeout
comment|// and make sure the xceiver died.
name|Thread
operator|.
name|sleep
argument_list|(
name|KEEPALIVE_TIMEOUT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|assertXceiverCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// The socket is still in the cache, because we don't
comment|// notice that it's closed until we try to read
comment|// from it again.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dfsClient
operator|.
name|peerCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Take it out of the cache - reading should
comment|// give an EOF.
name|Peer
name|peer
init|=
name|dfsClient
operator|.
name|peerCache
operator|.
name|get
argument_list|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|peer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|peer
operator|.
name|getInputStream
argument_list|()
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for the case where the client beings to read a long block, but doesn't    * read bytes off the stream quickly. The datanode should time out sending the    * chunks and the transceiver should die, even if it has a long keepalive.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testSlowReader ()
specifier|public
name|void
name|testSlowReader
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Restart the DN with a shorter write timeout.
name|DataNodeProperties
name|props
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|props
operator|.
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_DATANODE_SOCKET_WRITE_TIMEOUT_KEY
argument_list|,
name|WRITE_TIMEOUT
argument_list|)
expr_stmt|;
name|props
operator|.
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_DATANODE_SOCKET_REUSE_KEEPALIVE_KEY
argument_list|,
literal|120000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|props
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Wait for heartbeats to avoid a startup race where we
comment|// try to write the block while the DN is still starting.
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
name|dn
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|TEST_FILE
argument_list|,
literal|1024
operator|*
literal|1024
operator|*
literal|8L
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|stm
init|=
name|fs
operator|.
name|open
argument_list|(
name|TEST_FILE
argument_list|)
decl_stmt|;
try|try
block|{
name|stm
operator|.
name|read
argument_list|()
expr_stmt|;
name|assertXceiverCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|WRITE_TIMEOUT
operator|+
literal|1000
argument_list|)
expr_stmt|;
comment|// DN should time out in sendChunks, and this should force
comment|// the xceiver to exit.
name|assertXceiverCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|stm
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testManyClosedSocketsInCache ()
specifier|public
name|void
name|testManyClosedSocketsInCache
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Make a small file
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|TEST_FILE
argument_list|,
literal|1L
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|// Insert a bunch of dead sockets in the cache, by opening
comment|// many streams concurrently, reading all of the data,
comment|// and then closing them.
name|InputStream
index|[]
name|stms
init|=
operator|new
name|InputStream
index|[
literal|5
index|]
decl_stmt|;
try|try
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
name|stms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stms
index|[
name|i
index|]
operator|=
name|fs
operator|.
name|open
argument_list|(
name|TEST_FILE
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|InputStream
name|stm
range|:
name|stms
control|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|stm
argument_list|,
operator|new
name|NullOutputStream
argument_list|()
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|stms
argument_list|)
expr_stmt|;
block|}
name|DFSClient
name|client
init|=
operator|(
operator|(
name|DistributedFileSystem
operator|)
name|fs
operator|)
operator|.
name|dfs
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|client
operator|.
name|peerCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Let all the xceivers timeout
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|assertXceiverCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Client side still has the sockets cached
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|client
operator|.
name|peerCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Reading should not throw an exception.
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|TEST_FILE
argument_list|)
expr_stmt|;
block|}
DECL|method|assertXceiverCount (int expected)
specifier|private
name|void
name|assertXceiverCount
parameter_list|(
name|int
name|expected
parameter_list|)
block|{
comment|// Subtract 1, since the DataXceiverServer
comment|// counts as one
name|int
name|count
init|=
name|dn
operator|.
name|getXceiverCount
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|count
operator|!=
name|expected
condition|)
block|{
name|ReflectionUtils
operator|.
name|printThreadInfo
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|err
argument_list|)
argument_list|,
literal|"Thread dumps"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected "
operator|+
name|expected
operator|+
literal|" xceivers, found "
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

