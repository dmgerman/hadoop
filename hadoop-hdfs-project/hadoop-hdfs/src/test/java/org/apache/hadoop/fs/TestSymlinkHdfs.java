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
name|permission
operator|.
name|FsPermission
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
name|HdfsConfiguration
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
name|protocol
operator|.
name|HdfsConstants
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
name|QuotaExceededException
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
name|namenode
operator|.
name|NameNode
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
name|web
operator|.
name|WebHdfsConstants
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
name|web
operator|.
name|WebHdfsFileSystem
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
name|web
operator|.
name|WebHdfsTestUtil
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
name|ipc
operator|.
name|RemoteException
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
name|log4j
operator|.
name|Level
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
comment|/**  * Test symbolic links in Hdfs.  */
end_comment

begin_class
DECL|class|TestSymlinkHdfs
specifier|abstract
specifier|public
class|class
name|TestSymlinkHdfs
extends|extends
name|SymlinkBaseTest
block|{
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|NameNode
operator|.
name|stateChangeLog
parameter_list|,
name|Level
operator|.
name|ALL
parameter_list|)
constructor_decl|;
block|}
DECL|field|cluster
specifier|protected
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|webhdfs
specifier|protected
specifier|static
name|WebHdfsFileSystem
name|webhdfs
decl_stmt|;
DECL|field|dfs
specifier|protected
specifier|static
name|DistributedFileSystem
name|dfs
decl_stmt|;
annotation|@
name|Override
DECL|method|getScheme ()
specifier|protected
name|String
name|getScheme
parameter_list|()
block|{
return|return
literal|"hdfs"
return|;
block|}
annotation|@
name|Override
DECL|method|testBaseDir1 ()
specifier|protected
name|String
name|testBaseDir1
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|"/test1"
return|;
block|}
annotation|@
name|Override
DECL|method|testBaseDir2 ()
specifier|protected
name|String
name|testBaseDir2
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|"/test2"
return|;
block|}
annotation|@
name|Override
DECL|method|testURI ()
specifier|protected
name|URI
name|testURI
parameter_list|()
block|{
return|return
name|cluster
operator|.
name|getURI
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|unwrapException (IOException e)
specifier|protected
name|IOException
name|unwrapException
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|RemoteException
condition|)
block|{
return|return
operator|(
operator|(
name|RemoteException
operator|)
name|e
operator|)
operator|.
name|unwrapRemoteException
argument_list|()
return|;
block|}
return|return
name|e
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClassSetup ()
specifier|public
specifier|static
name|void
name|beforeClassSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FsPermission
operator|.
name|UMASK_LABEL
argument_list|,
literal|"000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY
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
name|build
argument_list|()
expr_stmt|;
name|webhdfs
operator|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystem
argument_list|(
name|conf
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
argument_list|)
expr_stmt|;
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClassTeardown ()
specifier|public
specifier|static
name|void
name|afterClassTeardown
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|10000
argument_list|)
comment|/** Access a file using a link that spans Hdfs to LocalFs */
DECL|method|testLinkAcrossFileSystems ()
specifier|public
name|void
name|testLinkAcrossFileSystems
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|localDir
init|=
operator|new
name|Path
argument_list|(
literal|"file://"
operator|+
name|wrapper
operator|.
name|getAbsoluteTestRootDir
argument_list|()
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
name|Path
name|localFile
init|=
operator|new
name|Path
argument_list|(
literal|"file://"
operator|+
name|wrapper
operator|.
name|getAbsoluteTestRootDir
argument_list|()
operator|+
literal|"/test/file"
argument_list|)
decl_stmt|;
name|Path
name|link
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"linkToFile"
argument_list|)
decl_stmt|;
name|FSTestWrapper
name|localWrapper
init|=
name|wrapper
operator|.
name|getLocalFSWrapper
argument_list|()
decl_stmt|;
name|localWrapper
operator|.
name|delete
argument_list|(
name|localDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localWrapper
operator|.
name|mkdir
argument_list|(
name|localDir
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localWrapper
operator|.
name|setWorkingDirectory
argument_list|(
name|localDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|localDir
argument_list|,
name|localWrapper
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|createAndWriteFile
argument_list|(
name|localWrapper
argument_list|,
name|localFile
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|localFile
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|readFile
argument_list|(
name|link
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|link
argument_list|)
operator|.
name|getLen
argument_list|()
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
comment|/** Test renaming a file across two file systems using a link */
DECL|method|testRenameAcrossFileSystemsViaLink ()
specifier|public
name|void
name|testRenameAcrossFileSystemsViaLink
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|localDir
init|=
operator|new
name|Path
argument_list|(
literal|"file://"
operator|+
name|wrapper
operator|.
name|getAbsoluteTestRootDir
argument_list|()
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
name|Path
name|hdfsFile
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|Path
name|link
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"link"
argument_list|)
decl_stmt|;
name|Path
name|hdfsFileNew
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"fileNew"
argument_list|)
decl_stmt|;
name|Path
name|hdfsFileNewViaLink
init|=
operator|new
name|Path
argument_list|(
name|link
argument_list|,
literal|"fileNew"
argument_list|)
decl_stmt|;
name|FSTestWrapper
name|localWrapper
init|=
name|wrapper
operator|.
name|getLocalFSWrapper
argument_list|()
decl_stmt|;
name|localWrapper
operator|.
name|delete
argument_list|(
name|localDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localWrapper
operator|.
name|mkdir
argument_list|(
name|localDir
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localWrapper
operator|.
name|setWorkingDirectory
argument_list|(
name|localDir
argument_list|)
expr_stmt|;
name|createAndWriteFile
argument_list|(
name|hdfsFile
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|localDir
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Rename hdfs://test1/file to hdfs://test1/link/fileNew
comment|// which renames to file://TEST_ROOT/test/fileNew which
comment|// spans AbstractFileSystems and therefore fails.
try|try
block|{
name|wrapper
operator|.
name|rename
argument_list|(
name|hdfsFile
argument_list|,
name|hdfsFileNewViaLink
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Renamed across file systems"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidPathException
name|ipe
parameter_list|)
block|{
comment|// Expected from FileContext
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// Expected from Filesystem
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Wrong FS: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Now rename hdfs://test1/link/fileNew to hdfs://test1/fileNew
comment|// which renames file://TEST_ROOT/test/fileNew to hdfs://test1/fileNew
comment|// which spans AbstractFileSystems and therefore fails.
name|createAndWriteFile
argument_list|(
name|hdfsFileNewViaLink
argument_list|)
expr_stmt|;
try|try
block|{
name|wrapper
operator|.
name|rename
argument_list|(
name|hdfsFileNewViaLink
argument_list|,
name|hdfsFileNew
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Renamed across file systems"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidPathException
name|ipe
parameter_list|)
block|{
comment|// Expected from FileContext
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// Expected from Filesystem
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Wrong FS: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
comment|/** Test create symlink to / */
DECL|method|testCreateLinkToSlash ()
specifier|public
name|void
name|testCreateLinkToSlash
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|Path
name|link
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"linkToSlash"
argument_list|)
decl_stmt|;
name|Path
name|fileViaLink
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
operator|+
literal|"/linkToSlash"
operator|+
name|testBaseDir1
argument_list|()
operator|+
literal|"/file"
argument_list|)
decl_stmt|;
name|createAndWriteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setWorkingDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|readFile
argument_list|(
name|fileViaLink
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|fileViaLink
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
comment|// Ditto when using another file context since the file system
comment|// for the slash is resolved according to the link's parent.
if|if
condition|(
name|wrapper
operator|instanceof
name|FileContextTestWrapper
condition|)
block|{
name|FSTestWrapper
name|localWrapper
init|=
name|wrapper
operator|.
name|getLocalFSWrapper
argument_list|()
decl_stmt|;
name|Path
name|linkQual
init|=
operator|new
name|Path
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|fileViaLink
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|localWrapper
operator|.
name|getFileStatus
argument_list|(
name|linkQual
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
comment|/** setPermission affects the target not the link */
DECL|method|testSetPermissionAffectsTarget ()
specifier|public
name|void
name|testSetPermissionAffectsTarget
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir2
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|linkToFile
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"linkToFile"
argument_list|)
decl_stmt|;
name|Path
name|linkToDir
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"linkToDir"
argument_list|)
decl_stmt|;
name|createAndWriteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|file
argument_list|,
name|linkToFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|dir
argument_list|,
name|linkToDir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Changing the permissions using the link does not modify
comment|// the permissions of the link..
name|FsPermission
name|perms
init|=
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|linkToFile
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|wrapper
operator|.
name|setPermission
argument_list|(
name|linkToFile
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0664
argument_list|)
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setOwner
argument_list|(
name|linkToFile
argument_list|,
literal|"user"
argument_list|,
literal|"group"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|perms
argument_list|,
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|linkToFile
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
comment|// but the file's permissions were adjusted appropriately
name|FileStatus
name|stat
init|=
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0664
argument_list|,
name|stat
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"user"
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"group"
argument_list|,
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
comment|// Getting the file's permissions via the link is the same
comment|// as getting the permissions directly.
name|assertEquals
argument_list|(
name|stat
operator|.
name|getPermission
argument_list|()
argument_list|,
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|linkToFile
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
comment|// Ditto for a link to a directory
name|perms
operator|=
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|linkToDir
argument_list|)
operator|.
name|getPermission
argument_list|()
expr_stmt|;
name|wrapper
operator|.
name|setPermission
argument_list|(
name|linkToDir
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0664
argument_list|)
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setOwner
argument_list|(
name|linkToDir
argument_list|,
literal|"user"
argument_list|,
literal|"group"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|perms
argument_list|,
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|linkToDir
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|=
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0664
argument_list|,
name|stat
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"user"
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"group"
argument_list|,
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stat
operator|.
name|getPermission
argument_list|()
argument_list|,
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|linkToDir
argument_list|)
operator|.
name|getPermission
argument_list|()
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
comment|/** Create a symlink using a path with scheme but no authority */
DECL|method|testCreateWithPartQualPathFails ()
specifier|public
name|void
name|testCreateWithPartQualPathFails
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|fileWoAuth
init|=
operator|new
name|Path
argument_list|(
literal|"hdfs:///test/file"
argument_list|)
decl_stmt|;
name|Path
name|linkWoAuth
init|=
operator|new
name|Path
argument_list|(
literal|"hdfs:///test/link"
argument_list|)
decl_stmt|;
try|try
block|{
name|createAndWriteFile
argument_list|(
name|fileWoAuth
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"HDFS requires URIs with schemes have an authority"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|wrapper
operator|.
name|createSymlink
argument_list|(
operator|new
name|Path
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|linkWoAuth
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"HDFS requires URIs with schemes have an authority"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
comment|/** setReplication affects the target not the link */
DECL|method|testSetReplication ()
specifier|public
name|void
name|testSetReplication
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|Path
name|link
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"linkToFile"
argument_list|)
decl_stmt|;
name|createAndWriteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|file
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setReplication
argument_list|(
name|link
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|link
argument_list|)
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|link
argument_list|)
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
operator|.
name|getReplication
argument_list|()
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
comment|/** Test create symlink with a max len name */
DECL|method|testCreateLinkMaxPathLink ()
specifier|public
name|void
name|testCreateLinkMaxPathLink
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxPathLen
init|=
name|HdfsConstants
operator|.
name|MAX_PATH_LENGTH
decl_stmt|;
specifier|final
name|int
name|dirLen
init|=
name|dir
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|1
decl_stmt|;
name|int
name|len
init|=
name|maxPathLen
operator|-
name|dirLen
decl_stmt|;
comment|// Build a MAX_PATH_LENGTH path
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|""
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
operator|(
name|len
operator|/
literal|10
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"0123456789"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|len
operator|%
literal|10
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
block|}
name|Path
name|link
init|=
operator|new
name|Path
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|maxPathLen
argument_list|,
name|dirLen
operator|+
name|link
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that it works
name|createAndWriteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setWorkingDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|file
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|readFile
argument_list|(
name|link
argument_list|)
expr_stmt|;
comment|// Now modify the path so it's too large
name|link
operator|=
operator|new
name|Path
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|"x"
argument_list|)
expr_stmt|;
try|try
block|{
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|file
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Path name should be too long"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|x
parameter_list|)
block|{
comment|// Expected
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
comment|/** Test symlink owner */
DECL|method|testLinkOwner ()
specifier|public
name|void
name|testLinkOwner
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|Path
name|link
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"symlinkToFile"
argument_list|)
decl_stmt|;
name|createAndWriteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|file
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|FileStatus
name|statFile
init|=
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|FileStatus
name|statLink
init|=
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|link
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|statLink
operator|.
name|getOwner
argument_list|()
argument_list|,
name|statFile
operator|.
name|getOwner
argument_list|()
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
comment|/** Test WebHdfsFileSystem.createSymlink(..). */
DECL|method|testWebHDFS ()
specifier|public
name|void
name|testWebHDFS
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|Path
name|link
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|,
literal|"linkToFile"
argument_list|)
decl_stmt|;
name|createAndWriteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|webhdfs
operator|.
name|createSymlink
argument_list|(
name|file
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setReplication
argument_list|(
name|link
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|link
argument_list|)
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|link
argument_list|)
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
operator|.
name|getReplication
argument_list|()
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
comment|/** Test craeteSymlink(..) with quota. */
DECL|method|testQuota ()
specifier|public
name|void
name|testQuota
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|setQuota
argument_list|(
name|dir
argument_list|,
literal|3
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|createAndWriteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|//creating the first link should succeed
specifier|final
name|Path
name|link1
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"link1"
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|file
argument_list|,
name|link1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
comment|//creating the second link should fail with QuotaExceededException.
specifier|final
name|Path
name|link2
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"link2"
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|file
argument_list|,
name|link2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Created symlink despite quota violation"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QuotaExceededException
name|qee
parameter_list|)
block|{
comment|//expected
block|}
block|}
block|}
end_class

end_unit

