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

begin_comment
comment|/**  * The FileStatus is being serialized in MR as jobs are submitted.  * Since viewfs has overlayed ViewFsFileStatus, we ran into  * serialization problems. THis test is test the fix.  */
end_comment

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
name|assertFalse
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
name|FileChecksum
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
name|FileStatus
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
name|DataInputBuffer
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
name|security
operator|.
name|UserGroupInformation
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
DECL|class|TestViewFsFileStatusHdfs
specifier|public
class|class
name|TestViewFsFileStatusHdfs
block|{
DECL|field|testfilename
specifier|static
specifier|final
name|String
name|testfilename
init|=
literal|"/tmp/testFileStatusSerialziation"
decl_stmt|;
DECL|field|someFile
specifier|static
specifier|final
name|String
name|someFile
init|=
literal|"/hdfstmp/someFileForTestGetFileChecksum"
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
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|defaultWorkingDirectory
specifier|private
specifier|static
name|Path
name|defaultWorkingDirectory
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
specifier|final
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
literal|2
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
name|defaultWorkingDirectory
operator|=
name|fHdfs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/"
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fHdfs
operator|.
name|mkdirs
argument_list|(
name|defaultWorkingDirectory
argument_list|)
expr_stmt|;
comment|// Setup the ViewFS to be used for all tests.
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
literal|"/vfstmp"
argument_list|,
operator|new
name|URI
argument_list|(
name|fHdfs
operator|.
name|getUri
argument_list|()
operator|+
literal|"/hdfstmp"
argument_list|)
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|ViewFileSystem
operator|.
name|class
argument_list|,
name|vfs
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileStatusSerialziation ()
specifier|public
name|void
name|testFileStatusSerialziation
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|long
name|len
init|=
name|fileSystemTestHelper
operator|.
name|createFile
argument_list|(
name|fHdfs
argument_list|,
name|testfilename
argument_list|)
decl_stmt|;
name|FileStatus
name|stat
init|=
name|vfs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|testfilename
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|len
argument_list|,
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
comment|// check serialization/deserialization
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|stat
operator|.
name|write
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
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
expr_stmt|;
name|FileStatus
name|deSer
init|=
operator|new
name|FileStatus
argument_list|()
decl_stmt|;
name|deSer
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|len
argument_list|,
name|deSer
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFileChecksum ()
specifier|public
name|void
name|testGetFileChecksum
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
comment|// Create two different files in HDFS
name|fileSystemTestHelper
operator|.
name|createFile
argument_list|(
name|fHdfs
argument_list|,
name|someFile
argument_list|)
expr_stmt|;
name|fileSystemTestHelper
operator|.
name|createFile
argument_list|(
name|fHdfs
argument_list|,
name|fileSystemTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fHdfs
argument_list|,
name|someFile
operator|+
literal|"other"
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|512
argument_list|)
expr_stmt|;
comment|// Get checksum through ViewFS
name|FileChecksum
name|viewFSCheckSum
init|=
name|vfs
operator|.
name|getFileChecksum
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/vfstmp/someFileForTestGetFileChecksum"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Get checksum through HDFS.
name|FileChecksum
name|hdfsCheckSum
init|=
name|fHdfs
operator|.
name|getFileChecksum
argument_list|(
operator|new
name|Path
argument_list|(
name|someFile
argument_list|)
argument_list|)
decl_stmt|;
comment|// Get checksum of different file in HDFS
name|FileChecksum
name|otherHdfsFileCheckSum
init|=
name|fHdfs
operator|.
name|getFileChecksum
argument_list|(
operator|new
name|Path
argument_list|(
name|someFile
operator|+
literal|"other"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Checksums of the same file (got through HDFS and ViewFS should be same)
name|assertEquals
argument_list|(
literal|"HDFS and ViewFS checksums were not the same"
argument_list|,
name|viewFSCheckSum
argument_list|,
name|hdfsCheckSum
argument_list|)
expr_stmt|;
comment|// Checksum of different files should be different.
name|assertFalse
argument_list|(
literal|"Some other HDFS file which should not have had the same "
operator|+
literal|"checksum as viewFS did!"
argument_list|,
name|viewFSCheckSum
operator|.
name|equals
argument_list|(
name|otherHdfsFileCheckSum
argument_list|)
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
name|testfilename
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fHdfs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|someFile
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fHdfs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|someFile
operator|+
literal|"other"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

