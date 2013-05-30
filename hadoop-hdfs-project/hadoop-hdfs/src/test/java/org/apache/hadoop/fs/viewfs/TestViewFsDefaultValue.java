begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_DEFAULT
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
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
name|DFS_BLOCK_SIZE_DEFAULT
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
name|DFS_BLOCK_SIZE_KEY
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
name|DFS_BYTES_PER_CHECKSUM_DEFAULT
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
name|DFS_BYTES_PER_CHECKSUM_KEY
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
name|DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
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
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
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
name|DFS_REPLICATION_DEFAULT
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
name|DFS_REPLICATION_KEY
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
name|fail
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|ContentSummary
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
name|FileSystemTestHelper
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
name|FsConstants
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
name|FsServerDefaults
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

begin_comment
comment|/**  * Tests for viewfs implementation of default fs level values.  * This tests for both passing in a path (based on mount point)  * to obtain the default value of the fs that the path is mounted on  * or just passing in no arguments.  */
end_comment

begin_class
DECL|class|TestViewFsDefaultValue
specifier|public
class|class
name|TestViewFsDefaultValue
block|{
DECL|field|testFileDir
specifier|static
specifier|final
name|String
name|testFileDir
init|=
literal|"/tmp/test/"
decl_stmt|;
DECL|field|testFileName
specifier|static
specifier|final
name|String
name|testFileName
init|=
name|testFileDir
operator|+
literal|"testFileStatusSerialziation"
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fileSystemTestHelper
specifier|private
specifier|static
specifier|final
name|FileSystemTestHelper
name|fileSystemTestHelper
init|=
operator|new
name|FileSystemTestHelper
argument_list|()
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
name|Configuration
name|CONF
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|fHdfs
specifier|private
specifier|static
name|FileSystem
name|fHdfs
decl_stmt|;
DECL|field|vfs
specifier|private
specifier|static
name|FileSystem
name|vfs
decl_stmt|;
DECL|field|testFilePath
specifier|private
specifier|static
name|Path
name|testFilePath
decl_stmt|;
DECL|field|testFileDirPath
specifier|private
specifier|static
name|Path
name|testFileDirPath
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|clusterSetupAtBegining ()
specifier|public
specifier|static
name|void
name|clusterSetupAtBegining
parameter_list|()
throws|throws
name|IOException
throws|,
name|LoginException
throws|,
name|URISyntaxException
block|{
name|CONF
operator|.
name|setLong
argument_list|(
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DFS_BLOCK_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setInt
argument_list|(
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|DFS_BYTES_PER_CHECKSUM_DEFAULT
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setInt
argument_list|(
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|,
name|DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setInt
argument_list|(
name|DFS_REPLICATION_KEY
argument_list|,
name|DFS_REPLICATION_DEFAULT
operator|+
literal|1
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setInt
argument_list|(
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|DFS_REPLICATION_DEFAULT
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|fHdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|fileSystemTestHelper
operator|.
name|createFile
argument_list|(
name|fHdfs
argument_list|,
name|testFileName
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|ViewFileSystemTestSetup
operator|.
name|createConfig
argument_list|()
decl_stmt|;
name|ConfigUtil
operator|.
name|addLink
argument_list|(
name|conf
argument_list|,
literal|"/tmp"
argument_list|,
operator|new
name|URI
argument_list|(
name|fHdfs
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|vfs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|FsConstants
operator|.
name|VIEWFS_URI
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|testFileDirPath
operator|=
operator|new
name|Path
argument_list|(
name|testFileDir
argument_list|)
expr_stmt|;
name|testFilePath
operator|=
operator|new
name|Path
argument_list|(
name|testFileName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that default blocksize values can be retrieved on the client side.    */
annotation|@
name|Test
DECL|method|testGetDefaultBlockSize ()
specifier|public
name|void
name|testGetDefaultBlockSize
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
comment|// createFile does not use defaultBlockSize to create the file,
comment|// but we are only looking at the defaultBlockSize, so this
comment|// test should still pass
try|try
block|{
name|vfs
operator|.
name|getDefaultBlockSize
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"getServerDefaults on viewFs did not throw excetion!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotInMountpointException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|vfs
operator|.
name|getDefaultBlockSize
argument_list|(
name|testFilePath
argument_list|)
argument_list|,
name|DFS_BLOCK_SIZE_DEFAULT
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that default replication values can be retrieved on the client side.    */
annotation|@
name|Test
DECL|method|testGetDefaultReplication ()
specifier|public
name|void
name|testGetDefaultReplication
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
try|try
block|{
name|vfs
operator|.
name|getDefaultReplication
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"getDefaultReplication on viewFs did not throw excetion!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotInMountpointException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|vfs
operator|.
name|getDefaultReplication
argument_list|(
name|testFilePath
argument_list|)
argument_list|,
name|DFS_REPLICATION_DEFAULT
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that server default values can be retrieved on the client side.    */
annotation|@
name|Test
DECL|method|testServerDefaults ()
specifier|public
name|void
name|testServerDefaults
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|FsServerDefaults
name|serverDefaults
init|=
name|vfs
operator|.
name|getServerDefaults
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"getServerDefaults on viewFs did not throw excetion!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotInMountpointException
name|e
parameter_list|)
block|{
name|FsServerDefaults
name|serverDefaults
init|=
name|vfs
operator|.
name|getServerDefaults
argument_list|(
name|testFilePath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DFS_BLOCK_SIZE_DEFAULT
argument_list|,
name|serverDefaults
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_BYTES_PER_CHECKSUM_DEFAULT
argument_list|,
name|serverDefaults
operator|.
name|getBytesPerChecksum
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
argument_list|,
name|serverDefaults
operator|.
name|getWritePacketSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|,
name|serverDefaults
operator|.
name|getFileBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_REPLICATION_DEFAULT
operator|+
literal|1
argument_list|,
name|serverDefaults
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that getContentSummary can be retrieved on the client side.    */
annotation|@
name|Test
DECL|method|testGetContentSummary ()
specifier|public
name|void
name|testGetContentSummary
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|hFs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|hFs
decl_stmt|;
name|dfs
operator|.
name|setQuota
argument_list|(
name|testFileDirPath
argument_list|,
literal|100
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|ContentSummary
name|cs
init|=
name|vfs
operator|.
name|getContentSummary
argument_list|(
name|testFileDirPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|cs
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|500
argument_list|,
name|cs
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|fHdfs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|testFileName
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

