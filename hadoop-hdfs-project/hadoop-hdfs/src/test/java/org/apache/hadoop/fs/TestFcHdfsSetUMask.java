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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
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
name|FileContextTestHelper
operator|.
name|*
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
name|After
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

begin_class
DECL|class|TestFcHdfsSetUMask
specifier|public
class|class
name|TestFcHdfsSetUMask
block|{
DECL|field|fileContextTestHelper
specifier|private
specifier|static
specifier|final
name|FileContextTestHelper
name|fileContextTestHelper
init|=
operator|new
name|FileContextTestHelper
argument_list|(
literal|"/tmp/TestFcHdfsSetUMask"
argument_list|)
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
DECL|field|fc
specifier|private
specifier|static
name|FileContext
name|fc
decl_stmt|;
comment|// rwxrwx---
DECL|field|USER_GROUP_OPEN_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|FsPermission
name|USER_GROUP_OPEN_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0770
argument_list|)
decl_stmt|;
DECL|field|USER_GROUP_OPEN_FILE_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|FsPermission
name|USER_GROUP_OPEN_FILE_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0660
argument_list|)
decl_stmt|;
DECL|field|USER_GROUP_OPEN_TEST_UMASK
specifier|private
specifier|static
specifier|final
name|FsPermission
name|USER_GROUP_OPEN_TEST_UMASK
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|0770
operator|^
literal|0777
argument_list|)
argument_list|)
decl_stmt|;
comment|// ---------
DECL|field|BLANK_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|FsPermission
name|BLANK_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0000
argument_list|)
decl_stmt|;
comment|// parent directory permissions when creating a directory with blank (000)
comment|// permissions - it always add the -wx------ bits to the parent so that
comment|// it can create the child
DECL|field|PARENT_PERMS_FOR_BLANK_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|FsPermission
name|PARENT_PERMS_FOR_BLANK_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0300
argument_list|)
decl_stmt|;
DECL|field|BLANK_TEST_UMASK
specifier|private
specifier|static
specifier|final
name|FsPermission
name|BLANK_TEST_UMASK
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|0000
operator|^
literal|0777
argument_list|)
argument_list|)
decl_stmt|;
comment|// rwxrwxrwx
DECL|field|WIDE_OPEN_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|FsPermission
name|WIDE_OPEN_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
decl_stmt|;
DECL|field|WIDE_OPEN_FILE_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|FsPermission
name|WIDE_OPEN_FILE_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0666
argument_list|)
decl_stmt|;
DECL|field|WIDE_OPEN_TEST_UMASK
specifier|private
specifier|static
specifier|final
name|FsPermission
name|WIDE_OPEN_TEST_UMASK
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|0777
operator|^
literal|0777
argument_list|)
argument_list|)
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
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// set permissions very restrictive
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_KEY
argument_list|,
literal|"077"
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
literal|2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fc
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|(
literal|0
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|defaultWorkingDirectory
operator|=
name|fc
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
name|fc
operator|.
name|mkdir
argument_list|(
name|defaultWorkingDirectory
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|ClusterShutdownAtEnd ()
specifier|public
specifier|static
name|void
name|ClusterShutdownAtEnd
parameter_list|()
throws|throws
name|Exception
block|{
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
block|{
try|try
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FileSystem
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot change log level\n"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|fc
operator|.
name|setUMask
argument_list|(
name|WIDE_OPEN_TEST_UMASK
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|fc
operator|.
name|delete
argument_list|(
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirWithExistingDirClear ()
specifier|public
name|void
name|testMkdirWithExistingDirClear
parameter_list|()
throws|throws
name|IOException
block|{
name|testMkdirWithExistingDir
argument_list|(
name|BLANK_TEST_UMASK
argument_list|,
name|BLANK_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirWithExistingDirOpen ()
specifier|public
name|void
name|testMkdirWithExistingDirOpen
parameter_list|()
throws|throws
name|IOException
block|{
name|testMkdirWithExistingDir
argument_list|(
name|WIDE_OPEN_TEST_UMASK
argument_list|,
name|WIDE_OPEN_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirWithExistingDirMiddle ()
specifier|public
name|void
name|testMkdirWithExistingDirMiddle
parameter_list|()
throws|throws
name|IOException
block|{
name|testMkdirWithExistingDir
argument_list|(
name|USER_GROUP_OPEN_TEST_UMASK
argument_list|,
name|USER_GROUP_OPEN_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirRecursiveWithNonExistingDirClear ()
specifier|public
name|void
name|testMkdirRecursiveWithNonExistingDirClear
parameter_list|()
throws|throws
name|IOException
block|{
comment|// by default parent directories have -wx------ bits set
name|testMkdirRecursiveWithNonExistingDir
argument_list|(
name|BLANK_TEST_UMASK
argument_list|,
name|BLANK_PERMISSIONS
argument_list|,
name|PARENT_PERMS_FOR_BLANK_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirRecursiveWithNonExistingDirOpen ()
specifier|public
name|void
name|testMkdirRecursiveWithNonExistingDirOpen
parameter_list|()
throws|throws
name|IOException
block|{
name|testMkdirRecursiveWithNonExistingDir
argument_list|(
name|WIDE_OPEN_TEST_UMASK
argument_list|,
name|WIDE_OPEN_PERMISSIONS
argument_list|,
name|WIDE_OPEN_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirRecursiveWithNonExistingDirMiddle ()
specifier|public
name|void
name|testMkdirRecursiveWithNonExistingDirMiddle
parameter_list|()
throws|throws
name|IOException
block|{
name|testMkdirRecursiveWithNonExistingDir
argument_list|(
name|USER_GROUP_OPEN_TEST_UMASK
argument_list|,
name|USER_GROUP_OPEN_PERMISSIONS
argument_list|,
name|USER_GROUP_OPEN_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateRecursiveWithExistingDirClear ()
specifier|public
name|void
name|testCreateRecursiveWithExistingDirClear
parameter_list|()
throws|throws
name|IOException
block|{
name|testCreateRecursiveWithExistingDir
argument_list|(
name|BLANK_TEST_UMASK
argument_list|,
name|BLANK_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateRecursiveWithExistingDirOpen ()
specifier|public
name|void
name|testCreateRecursiveWithExistingDirOpen
parameter_list|()
throws|throws
name|IOException
block|{
name|testCreateRecursiveWithExistingDir
argument_list|(
name|WIDE_OPEN_TEST_UMASK
argument_list|,
name|WIDE_OPEN_FILE_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateRecursiveWithExistingDirMiddle ()
specifier|public
name|void
name|testCreateRecursiveWithExistingDirMiddle
parameter_list|()
throws|throws
name|IOException
block|{
name|testCreateRecursiveWithExistingDir
argument_list|(
name|USER_GROUP_OPEN_TEST_UMASK
argument_list|,
name|USER_GROUP_OPEN_FILE_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateRecursiveWithNonExistingDirClear ()
specifier|public
name|void
name|testCreateRecursiveWithNonExistingDirClear
parameter_list|()
throws|throws
name|IOException
block|{
comment|// directory permission inherited from parent so this must match the @Before
comment|// set of umask
name|testCreateRecursiveWithNonExistingDir
argument_list|(
name|BLANK_TEST_UMASK
argument_list|,
name|WIDE_OPEN_PERMISSIONS
argument_list|,
name|BLANK_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateRecursiveWithNonExistingDirOpen ()
specifier|public
name|void
name|testCreateRecursiveWithNonExistingDirOpen
parameter_list|()
throws|throws
name|IOException
block|{
comment|// directory permission inherited from parent so this must match the @Before
comment|// set of umask
name|testCreateRecursiveWithNonExistingDir
argument_list|(
name|WIDE_OPEN_TEST_UMASK
argument_list|,
name|WIDE_OPEN_PERMISSIONS
argument_list|,
name|WIDE_OPEN_FILE_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateRecursiveWithNonExistingDirMiddle ()
specifier|public
name|void
name|testCreateRecursiveWithNonExistingDirMiddle
parameter_list|()
throws|throws
name|IOException
block|{
comment|// directory permission inherited from parent so this must match the @Before
comment|// set of umask
name|testCreateRecursiveWithNonExistingDir
argument_list|(
name|USER_GROUP_OPEN_TEST_UMASK
argument_list|,
name|WIDE_OPEN_PERMISSIONS
argument_list|,
name|USER_GROUP_OPEN_FILE_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
DECL|method|testMkdirWithExistingDir (FsPermission umask, FsPermission expectedPerms)
specifier|public
name|void
name|testMkdirWithExistingDir
parameter_list|(
name|FsPermission
name|umask
parameter_list|,
name|FsPermission
name|expectedPerms
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|f
init|=
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"aDir"
argument_list|)
decl_stmt|;
name|fc
operator|.
name|setUMask
argument_list|(
name|umask
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|f
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isDir
argument_list|(
name|fc
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"permissions on directory are wrong"
argument_list|,
name|expectedPerms
argument_list|,
name|fc
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMkdirRecursiveWithNonExistingDir (FsPermission umask, FsPermission expectedPerms, FsPermission expectedParentPerms)
specifier|public
name|void
name|testMkdirRecursiveWithNonExistingDir
parameter_list|(
name|FsPermission
name|umask
parameter_list|,
name|FsPermission
name|expectedPerms
parameter_list|,
name|FsPermission
name|expectedParentPerms
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|f
init|=
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"NonExistant2/aDir"
argument_list|)
decl_stmt|;
name|fc
operator|.
name|setUMask
argument_list|(
name|umask
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|f
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isDir
argument_list|(
name|fc
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"permissions on directory are wrong"
argument_list|,
name|expectedPerms
argument_list|,
name|fc
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|fParent
init|=
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"NonExistant2"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"permissions on parent directory are wrong"
argument_list|,
name|expectedParentPerms
argument_list|,
name|fc
operator|.
name|getFileStatus
argument_list|(
name|fParent
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateRecursiveWithExistingDir (FsPermission umask, FsPermission expectedPerms)
specifier|public
name|void
name|testCreateRecursiveWithExistingDir
parameter_list|(
name|FsPermission
name|umask
parameter_list|,
name|FsPermission
name|expectedPerms
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|f
init|=
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|fc
operator|.
name|setUMask
argument_list|(
name|umask
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|fc
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isFile
argument_list|(
name|fc
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"permissions on file are wrong"
argument_list|,
name|expectedPerms
argument_list|,
name|fc
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateRecursiveWithNonExistingDir (FsPermission umask, FsPermission expectedDirPerms, FsPermission expectedFilePerms)
specifier|public
name|void
name|testCreateRecursiveWithNonExistingDir
parameter_list|(
name|FsPermission
name|umask
parameter_list|,
name|FsPermission
name|expectedDirPerms
parameter_list|,
name|FsPermission
name|expectedFilePerms
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|f
init|=
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"NonExisting/foo"
argument_list|)
decl_stmt|;
name|Path
name|fParent
init|=
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"NonExisting"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fc
argument_list|,
name|fParent
argument_list|)
argument_list|)
expr_stmt|;
name|fc
operator|.
name|setUMask
argument_list|(
name|umask
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|fc
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isFile
argument_list|(
name|fc
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"permissions on file are wrong"
argument_list|,
name|expectedFilePerms
argument_list|,
name|fc
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"permissions on parent directory are wrong"
argument_list|,
name|expectedDirPerms
argument_list|,
name|fc
operator|.
name|getFileStatus
argument_list|(
name|fParent
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

