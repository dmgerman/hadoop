begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
package|;
end_package

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
name|FileAlreadyExistsException
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
name|FileSystemContractBaseTest
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
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Assume
operator|.
name|assumeFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeNotNull
import|;
end_import

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

begin_comment
comment|/**  * Tests a live Aliyun OSS system.  */
end_comment

begin_class
DECL|class|TestAliyunOSSFileSystemContract
specifier|public
class|class
name|TestAliyunOSSFileSystemContract
extends|extends
name|FileSystemContractBaseTest
block|{
DECL|field|TEST_FS_OSS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TEST_FS_OSS_NAME
init|=
literal|"test.fs.oss.name"
decl_stmt|;
DECL|field|testRootPath
specifier|private
specifier|static
name|Path
name|testRootPath
init|=
operator|new
name|Path
argument_list|(
name|AliyunOSSTestUtils
operator|.
name|generateUniqueTestPath
argument_list|()
argument_list|)
decl_stmt|;
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|fs
operator|=
name|AliyunOSSTestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assumeNotNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTestBaseDir ()
specifier|public
name|Path
name|getTestBaseDir
parameter_list|()
block|{
return|return
name|testRootPath
return|;
block|}
annotation|@
name|Test
DECL|method|testMkdirsWithUmask ()
specifier|public
name|void
name|testMkdirsWithUmask
parameter_list|()
throws|throws
name|Exception
block|{
comment|// not supported
block|}
annotation|@
name|Test
DECL|method|testRootDirAlwaysExists ()
specifier|public
name|void
name|testRootDirAlwaysExists
parameter_list|()
throws|throws
name|Exception
block|{
comment|//this will throw an exception if the path is not found
name|fs
operator|.
name|getFileStatus
argument_list|(
name|super
operator|.
name|path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
comment|//this catches overrides of the base exists() method that don't
comment|//use getFileStatus() as an existence probe
name|assertTrue
argument_list|(
literal|"FileSystem.exists() fails for root"
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|super
operator|.
name|path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameRootDirForbidden ()
specifier|public
name|void
name|testRenameRootDirForbidden
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|super
operator|.
name|path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|super
operator|.
name|path
argument_list|(
literal|"/test/newRootDir"
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListStatus ()
specifier|public
name|void
name|testListStatus
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File exists"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|exists
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|fs
init|=
name|this
operator|.
name|fs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getOwner
argument_list|()
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
name|assertEquals
argument_list|(
name|fs
operator|.
name|getGroup
argument_list|()
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
block|}
annotation|@
name|Test
DECL|method|testDeleteSubdir ()
specifier|public
name|void
name|testDeleteSubdir
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|parentDir
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop"
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file"
argument_list|)
decl_stmt|;
name|Path
name|subdir
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/subdir"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Created subdir"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|mkdirs
argument_list|(
name|subdir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File exists"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|exists
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Parent dir exists"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|exists
argument_list|(
name|parentDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Subdir exists"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|exists
argument_list|(
name|subdir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Deleted subdir"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|delete
argument_list|(
name|subdir
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Parent should exist"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|exists
argument_list|(
name|parentDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Deleted file"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Parent should exist"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|exists
argument_list|(
name|parentDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|renameSupported ()
specifier|protected
name|boolean
name|renameSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Test
DECL|method|testRenameNonExistentPath ()
specifier|public
name|void
name|testRenameNonExistentPath
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|src
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/path"
argument_list|)
decl_stmt|;
name|Path
name|dst
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/new/newpath"
argument_list|)
decl_stmt|;
try|try
block|{
name|super
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw FileNotFoundException!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testRenameFileMoveToNonExistentDirectory ()
specifier|public
name|void
name|testRenameFileMoveToNonExistentDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|src
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Path
name|dst
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/new/newfile"
argument_list|)
decl_stmt|;
try|try
block|{
name|super
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw FileNotFoundException!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testRenameDirectoryConcurrent ()
specifier|public
name|void
name|testRenameDirectoryConcurrent
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|src
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file/"
argument_list|)
decl_stmt|;
name|Path
name|child1
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file/1"
argument_list|)
decl_stmt|;
name|Path
name|child2
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file/2"
argument_list|)
decl_stmt|;
name|Path
name|child3
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file/3"
argument_list|)
decl_stmt|;
name|Path
name|child4
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file/4"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|child1
argument_list|)
expr_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|child2
argument_list|)
expr_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|child3
argument_list|)
expr_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|child4
argument_list|)
expr_stmt|;
name|Path
name|dst
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/new"
argument_list|)
decl_stmt|;
name|super
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|this
operator|.
name|fs
operator|.
name|listStatus
argument_list|(
name|dst
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameDirectoryCopyTaskAllSucceed ()
specifier|public
name|void
name|testRenameDirectoryCopyTaskAllSucceed
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|srcOne
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file/1"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|srcOne
argument_list|)
expr_stmt|;
name|Path
name|dstOne
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/new/file/1"
argument_list|)
decl_stmt|;
name|Path
name|dstTwo
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/new/file/2"
argument_list|)
decl_stmt|;
name|AliyunOSSCopyFileContext
name|copyFileContext
init|=
operator|new
name|AliyunOSSCopyFileContext
argument_list|()
decl_stmt|;
name|AliyunOSSFileSystemStore
name|store
init|=
operator|(
operator|(
name|AliyunOSSFileSystem
operator|)
name|this
operator|.
name|fs
operator|)
operator|.
name|getStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|storeEmptyFile
argument_list|(
literal|"test/new/file/"
argument_list|)
expr_stmt|;
name|AliyunOSSCopyFileTask
name|oneCopyFileTask
init|=
operator|new
name|AliyunOSSCopyFileTask
argument_list|(
name|store
argument_list|,
name|srcOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|dstOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|copyFileContext
argument_list|)
decl_stmt|;
name|oneCopyFileTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|assumeFalse
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
name|AliyunOSSCopyFileTask
name|twoCopyFileTask
init|=
operator|new
name|AliyunOSSCopyFileTask
argument_list|(
name|store
argument_list|,
name|srcOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|dstTwo
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|copyFileContext
argument_list|)
decl_stmt|;
name|twoCopyFileTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|assumeFalse
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
name|copyFileContext
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|copyFileContext
operator|.
name|awaitAllFinish
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|copyFileContext
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|assumeFalse
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameDirectoryCopyTaskAllFailed ()
specifier|public
name|void
name|testRenameDirectoryCopyTaskAllFailed
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|srcOne
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file/1"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|srcOne
argument_list|)
expr_stmt|;
name|Path
name|dstOne
init|=
operator|new
name|Path
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|Path
name|dstTwo
init|=
operator|new
name|Path
argument_list|(
literal|"2"
argument_list|)
decl_stmt|;
name|AliyunOSSCopyFileContext
name|copyFileContext
init|=
operator|new
name|AliyunOSSCopyFileContext
argument_list|()
decl_stmt|;
name|AliyunOSSFileSystemStore
name|store
init|=
operator|(
operator|(
name|AliyunOSSFileSystem
operator|)
name|this
operator|.
name|fs
operator|)
operator|.
name|getStore
argument_list|()
decl_stmt|;
comment|//store.storeEmptyFile("test/new/file/");
name|AliyunOSSCopyFileTask
name|oneCopyFileTask
init|=
operator|new
name|AliyunOSSCopyFileTask
argument_list|(
name|store
argument_list|,
name|srcOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|dstOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|copyFileContext
argument_list|)
decl_stmt|;
name|oneCopyFileTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|assumeTrue
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
name|AliyunOSSCopyFileTask
name|twoCopyFileTask
init|=
operator|new
name|AliyunOSSCopyFileTask
argument_list|(
name|store
argument_list|,
name|srcOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|dstTwo
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|copyFileContext
argument_list|)
decl_stmt|;
name|twoCopyFileTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|assumeTrue
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
name|copyFileContext
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|copyFileContext
operator|.
name|awaitAllFinish
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|copyFileContext
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|assumeTrue
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameDirectoryCopyTaskPartialFailed ()
specifier|public
name|void
name|testRenameDirectoryCopyTaskPartialFailed
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|srcOne
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file/1"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|srcOne
argument_list|)
expr_stmt|;
name|Path
name|dstOne
init|=
operator|new
name|Path
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|Path
name|dstTwo
init|=
operator|new
name|Path
argument_list|(
literal|"/test/new/file/2"
argument_list|)
decl_stmt|;
name|Path
name|dstThree
init|=
operator|new
name|Path
argument_list|(
literal|"3"
argument_list|)
decl_stmt|;
name|AliyunOSSCopyFileContext
name|copyFileContext
init|=
operator|new
name|AliyunOSSCopyFileContext
argument_list|()
decl_stmt|;
name|AliyunOSSFileSystemStore
name|store
init|=
operator|(
operator|(
name|AliyunOSSFileSystem
operator|)
name|this
operator|.
name|fs
operator|)
operator|.
name|getStore
argument_list|()
decl_stmt|;
comment|//store.storeEmptyFile("test/new/file/");
name|AliyunOSSCopyFileTask
name|oneCopyFileTask
init|=
operator|new
name|AliyunOSSCopyFileTask
argument_list|(
name|store
argument_list|,
name|srcOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|dstOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|copyFileContext
argument_list|)
decl_stmt|;
name|oneCopyFileTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|assumeTrue
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
name|AliyunOSSCopyFileTask
name|twoCopyFileTask
init|=
operator|new
name|AliyunOSSCopyFileTask
argument_list|(
name|store
argument_list|,
name|srcOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|dstTwo
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|copyFileContext
argument_list|)
decl_stmt|;
name|twoCopyFileTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|assumeTrue
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
name|AliyunOSSCopyFileTask
name|threeCopyFileTask
init|=
operator|new
name|AliyunOSSCopyFileTask
argument_list|(
name|store
argument_list|,
name|srcOne
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|dstThree
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|copyFileContext
argument_list|)
decl_stmt|;
name|threeCopyFileTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|assumeTrue
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
name|copyFileContext
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|copyFileContext
operator|.
name|awaitAllFinish
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|copyFileContext
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|assumeTrue
argument_list|(
name|copyFileContext
operator|.
name|isCopyFailure
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameDirectoryMoveToNonExistentDirectory ()
specifier|public
name|void
name|testRenameDirectoryMoveToNonExistentDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|src
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/dir"
argument_list|)
decl_stmt|;
name|this
operator|.
name|fs
operator|.
name|mkdirs
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Path
name|dst
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/new/newdir"
argument_list|)
decl_stmt|;
try|try
block|{
name|super
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw FileNotFoundException!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testRenameFileMoveToExistingDirectory ()
specifier|public
name|void
name|testRenameFileMoveToExistingDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameFileMoveToExistingDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameFileAsExistingFile ()
specifier|public
name|void
name|testRenameFileAsExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|src
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Path
name|dst
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/new/newfile"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|dst
argument_list|)
expr_stmt|;
try|try
block|{
name|super
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw FileAlreadyExistsException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testRenameDirectoryAsExistingFile ()
specifier|public
name|void
name|testRenameDirectoryAsExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|renameSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|src
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/dir"
argument_list|)
decl_stmt|;
name|this
operator|.
name|fs
operator|.
name|mkdirs
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Path
name|dst
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/new/newfile"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|dst
argument_list|)
expr_stmt|;
try|try
block|{
name|super
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw FileAlreadyExistsException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testGetFileStatusFileAndDirectory ()
specifier|public
name|void
name|testGetFileStatusFileAndDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|filePath
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/oss/file1"
argument_list|)
decl_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be file"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|getFileStatus
argument_list|(
name|filePath
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Should not be directory"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|getFileStatus
argument_list|(
name|filePath
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|dirPath
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/oss/dir"
argument_list|)
decl_stmt|;
name|this
operator|.
name|fs
operator|.
name|mkdirs
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be directory"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dirPath
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Should not be file"
argument_list|,
name|this
operator|.
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dirPath
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|parentPath
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/oss"
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|fileStatus
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|parentPath
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
literal|"file and directory should be new"
argument_list|,
name|fileStatus
operator|.
name|getModificationTime
argument_list|()
operator|>
literal|0L
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMkdirsForExistingFile ()
specifier|public
name|void
name|testMkdirsForExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|testFile
init|=
name|this
operator|.
name|path
argument_list|(
literal|"/test/hadoop/file"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|this
operator|.
name|fs
operator|.
name|exists
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|createFile
argument_list|(
name|testFile
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|this
operator|.
name|fs
operator|.
name|exists
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|fs
operator|.
name|mkdirs
argument_list|(
name|testFile
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw FileAlreadyExistsException!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testRenameChangingDirShouldFail ()
specifier|public
name|void
name|testRenameChangingDirShouldFail
parameter_list|()
throws|throws
name|Exception
block|{
name|testRenameDir
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testRenameDir
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameDir ()
specifier|public
name|void
name|testRenameDir
parameter_list|()
throws|throws
name|Exception
block|{
name|testRenameDir
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testRenameDir
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testRenameDir (boolean changing, boolean result, boolean empty)
specifier|private
name|void
name|testRenameDir
parameter_list|(
name|boolean
name|changing
parameter_list|,
name|boolean
name|result
parameter_list|,
name|boolean
name|empty
parameter_list|)
throws|throws
name|Exception
block|{
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|setLong
argument_list|(
name|Constants
operator|.
name|FS_OSS_BLOCK_SIZE_KEY
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|String
name|key
init|=
literal|"a/b/test.file"
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|empty
condition|)
block|{
name|fs
operator|.
name|createNewFile
argument_list|(
name|this
operator|.
name|path
argument_list|(
name|key
operator|+
literal|"."
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|createFile
argument_list|(
name|this
operator|.
name|path
argument_list|(
name|key
operator|+
literal|"."
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Path
name|srcPath
init|=
name|this
operator|.
name|path
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|Path
name|dstPath
init|=
name|this
operator|.
name|path
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|TestRenameTask
name|task
init|=
operator|new
name|TestRenameTask
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|dstPath
argument_list|)
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|task
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|task
operator|.
name|isRunning
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|changing
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|this
operator|.
name|path
argument_list|(
literal|"a/b"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|result
argument_list|,
name|task
operator|.
name|isSucceed
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|TestRenameTask
class|class
name|TestRenameTask
implements|implements
name|Runnable
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|srcPath
specifier|private
name|Path
name|srcPath
decl_stmt|;
DECL|field|dstPath
specifier|private
name|Path
name|dstPath
decl_stmt|;
DECL|field|result
specifier|private
name|boolean
name|result
decl_stmt|;
DECL|field|running
specifier|private
name|boolean
name|running
decl_stmt|;
DECL|method|TestRenameTask (FileSystem fs, Path srcPath, Path dstPath)
name|TestRenameTask
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcPath
parameter_list|,
name|Path
name|dstPath
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|srcPath
operator|=
name|srcPath
expr_stmt|;
name|this
operator|.
name|dstPath
operator|=
name|dstPath
expr_stmt|;
name|this
operator|.
name|result
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|running
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|isSucceed ()
name|boolean
name|isSucceed
parameter_list|()
block|{
return|return
name|this
operator|.
name|result
return|;
block|}
DECL|method|isRunning ()
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|this
operator|.
name|running
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|running
operator|=
literal|true
expr_stmt|;
name|result
operator|=
name|fs
operator|.
name|rename
argument_list|(
name|srcPath
argument_list|,
name|dstPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{       }
block|}
block|}
DECL|method|getGlobalTimeout ()
specifier|protected
name|int
name|getGlobalTimeout
parameter_list|()
block|{
return|return
literal|120
operator|*
literal|1000
return|;
block|}
block|}
end_class

end_unit

