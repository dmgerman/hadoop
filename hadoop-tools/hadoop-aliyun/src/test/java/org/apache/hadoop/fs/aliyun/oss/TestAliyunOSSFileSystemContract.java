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
name|fs
operator|.
name|FileStatus
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

begin_comment
comment|/**  * Tests a live Aliyun OSS system.  *  * This uses BlockJUnit4ClassRunner because FileSystemContractBaseTest from  * TestCase which uses the old Junit3 runner that doesn't ignore assumptions  * properly making it impossible to skip the tests if we don't have a valid  * bucket.  */
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
name|Override
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
name|super
operator|.
name|setUp
argument_list|()
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
name|Override
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
name|Override
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
name|Override
DECL|method|testRenameRootDirForbidden ()
specifier|public
name|void
name|testRenameRootDirForbidden
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|renameSupported
argument_list|()
condition|)
block|{
return|return;
block|}
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
name|Override
DECL|method|testRenameNonExistentPath ()
specifier|public
name|void
name|testRenameNonExistentPath
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|renameSupported
argument_list|()
condition|)
block|{
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
block|}
annotation|@
name|Override
DECL|method|testRenameFileMoveToNonExistentDirectory ()
specifier|public
name|void
name|testRenameFileMoveToNonExistentDirectory
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|renameSupported
argument_list|()
condition|)
block|{
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
block|}
annotation|@
name|Override
DECL|method|testRenameDirectoryMoveToNonExistentDirectory ()
specifier|public
name|void
name|testRenameDirectoryMoveToNonExistentDirectory
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|renameSupported
argument_list|()
condition|)
block|{
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
block|}
annotation|@
name|Override
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
name|Override
DECL|method|testRenameFileAsExistingFile ()
specifier|public
name|void
name|testRenameFileAsExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|renameSupported
argument_list|()
condition|)
block|{
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
block|}
annotation|@
name|Override
DECL|method|testRenameDirectoryAsExistingFile ()
specifier|public
name|void
name|testRenameDirectoryAsExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|renameSupported
argument_list|()
condition|)
block|{
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
block|}
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
block|}
end_class

end_unit

