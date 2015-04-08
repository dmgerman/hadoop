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
name|assertTrue
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
name|Arrays
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
name|ChecksumException
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
name|hdfs
operator|.
name|protocol
operator|.
name|BlockLocalPathInfo
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
name|ClientDatanodeProtocol
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
name|Assume
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
DECL|class|TestBlockReaderLocalLegacy
specifier|public
class|class
name|TestBlockReaderLocalLegacy
block|{
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
name|DFSInputStream
operator|.
name|tcpReadsDisabledForTesting
operator|=
literal|true
expr_stmt|;
name|DomainSocket
operator|.
name|disableBindPathValidation
argument_list|()
expr_stmt|;
block|}
DECL|method|getConfiguration ( TemporarySocketDirectory socketDir)
specifier|private
specifier|static
name|HdfsConfiguration
name|getConfiguration
parameter_list|(
name|TemporarySocketDirectory
name|socketDir
parameter_list|)
throws|throws
name|IOException
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|socketDir
operator|==
literal|null
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DOMAIN_SOCKET_PATH_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DOMAIN_SOCKET_PATH_KEY
argument_list|,
operator|new
name|File
argument_list|(
name|socketDir
operator|.
name|getDir
argument_list|()
argument_list|,
literal|"TestBlockReaderLocalLegacy.%d.sock"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_READ_SHORTCIRCUIT_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_USE_LEGACY_BLOCKREADERLOCAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_READ_SHORTCIRCUIT_SKIP_CHECKSUM_KEY
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
name|DFS_BLOCK_LOCAL_PATH_ACCESS_USER_KEY
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_DOMAIN_SOCKET_DATA_TRAFFIC
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Set short retry timeouts so this test runs faster
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Test that, in the case of an error, the position and limit of a ByteBuffer    * are left unchanged. This is not mandated by ByteBufferReadable, but clients    * of this class might immediately issue a retry on failure, so it's polite.    */
annotation|@
name|Test
DECL|method|testStablePositionAfterCorruptRead ()
specifier|public
name|void
name|testStablePositionAfterCorruptRead
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|short
name|REPL_FACTOR
init|=
literal|1
decl_stmt|;
specifier|final
name|long
name|FILE_LENGTH
init|=
literal|512L
decl_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getConfiguration
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/corrupted"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|FILE_LENGTH
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|12345L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|REPL_FACTOR
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|int
name|blockFilesCorrupted
init|=
name|cluster
operator|.
name|corruptBlockOnDataNodes
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"All replicas not corrupted"
argument_list|,
name|REPL_FACTOR
argument_list|,
name|blockFilesCorrupted
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|dis
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
operator|(
name|int
operator|)
name|FILE_LENGTH
argument_list|)
decl_stmt|;
name|boolean
name|sawException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|ex
parameter_list|)
block|{
name|sawException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|sawException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|buf
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buf
operator|.
name|capacity
argument_list|()
argument_list|,
name|buf
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|dis
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|buf
operator|.
name|position
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|buf
operator|.
name|limit
argument_list|(
literal|25
argument_list|)
expr_stmt|;
name|sawException
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|dis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|ex
parameter_list|)
block|{
name|sawException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|sawException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|buf
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|buf
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBothOldAndNewShortCircuitConfigured ()
specifier|public
name|void
name|testBothOldAndNewShortCircuitConfigured
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|short
name|REPL_FACTOR
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|FILE_LENGTH
init|=
literal|512
decl_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|null
operator|==
name|DomainSocket
operator|.
name|getLoadingFailureReason
argument_list|()
argument_list|)
expr_stmt|;
name|TemporarySocketDirectory
name|socketDir
init|=
operator|new
name|TemporarySocketDirectory
argument_list|()
decl_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getConfiguration
argument_list|(
name|socketDir
argument_list|)
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|socketDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|byte
name|orig
index|[]
init|=
operator|new
name|byte
index|[
name|FILE_LENGTH
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
name|orig
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|orig
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|10
argument_list|)
expr_stmt|;
block|}
name|FSDataOutputStream
name|fos
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|orig
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|REPL_FACTOR
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|fis
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|FILE_LENGTH
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|fis
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|FILE_LENGTH
argument_list|)
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|orig
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|equals
argument_list|(
name|orig
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testBlockReaderLocalLegacyWithAppend ()
specifier|public
name|void
name|testBlockReaderLocalLegacyWithAppend
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|short
name|REPL_FACTOR
init|=
literal|1
decl_stmt|;
specifier|final
name|HdfsConfiguration
name|conf
init|=
name|getConfiguration
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_USE_LEGACY_BLOCKREADERLOCAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
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
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/testBlockReaderLocalLegacy"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|path
argument_list|,
literal|10
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|dfs
argument_list|,
name|path
argument_list|,
name|REPL_FACTOR
argument_list|)
expr_stmt|;
specifier|final
name|ClientDatanodeProtocol
name|proxy
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
decl_stmt|;
specifier|final
name|ExtendedBlock
name|originalBlock
decl_stmt|;
specifier|final
name|long
name|originalGS
decl_stmt|;
block|{
specifier|final
name|LocatedBlock
name|lb
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|proxy
operator|=
name|DFSUtil
operator|.
name|createClientDatanodeProtocolProxy
argument_list|(
name|lb
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|conf
argument_list|,
literal|60000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|token
operator|=
name|lb
operator|.
name|getBlockToken
argument_list|()
expr_stmt|;
comment|// get block and generation stamp
specifier|final
name|ExtendedBlock
name|blk
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|lb
operator|.
name|getBlock
argument_list|()
argument_list|)
decl_stmt|;
name|originalBlock
operator|=
operator|new
name|ExtendedBlock
argument_list|(
name|blk
argument_list|)
expr_stmt|;
name|originalGS
operator|=
name|originalBlock
operator|.
name|getGenerationStamp
argument_list|()
expr_stmt|;
comment|// test getBlockLocalPathInfo
specifier|final
name|BlockLocalPathInfo
name|info
init|=
name|proxy
operator|.
name|getBlockLocalPathInfo
argument_list|(
name|blk
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalGS
argument_list|,
name|info
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
comment|// append one byte
name|FSDataOutputStream
name|out
init|=
name|dfs
operator|.
name|append
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|{
comment|// get new generation stamp
specifier|final
name|LocatedBlock
name|lb
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|long
name|newGS
init|=
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newGS
operator|>
name|originalGS
argument_list|)
expr_stmt|;
comment|// getBlockLocalPathInfo using the original block.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalGS
argument_list|,
name|originalBlock
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|BlockLocalPathInfo
name|info
init|=
name|proxy
operator|.
name|getBlockLocalPathInfo
argument_list|(
name|originalBlock
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|newGS
argument_list|,
name|info
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

