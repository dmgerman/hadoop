begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
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
name|IOException
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
operator|.
name|compareByteArrays
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
operator|.
name|dataset
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
operator|.
name|readBytesToString
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
operator|.
name|readDataset
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
operator|.
name|writeDataset
import|;
end_import

begin_class
DECL|class|TestSwiftFileSystemRename
specifier|public
class|class
name|TestSwiftFileSystemRename
extends|extends
name|SwiftFileSystemBaseTest
block|{
comment|/**    * Rename a file into a directory    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameFileIntoExistingDirectory ()
specifier|public
name|void
name|testRenameFileIntoExistingDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
name|Path
name|src
init|=
name|path
argument_list|(
literal|"/test/olddir/file"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Path
name|dst
init|=
name|path
argument_list|(
literal|"/test/new/newdir"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dst
argument_list|)
expr_stmt|;
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
name|Path
name|newFile
init|=
name|path
argument_list|(
literal|"/test/new/newdir/file"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|newFile
argument_list|)
condition|)
block|{
name|String
name|ls
init|=
name|ls
argument_list|(
name|dst
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|ls
argument_list|(
name|path
argument_list|(
literal|"/test/new"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|ls
argument_list|(
name|path
argument_list|(
literal|"/test/hadoop"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not find "
operator|+
name|newFile
operator|+
literal|" - directory: "
operator|+
name|ls
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Destination changed"
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|(
literal|"/test/new/newdir/file"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameFile ()
specifier|public
name|void
name|testRenameFile
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
specifier|final
name|Path
name|old
init|=
operator|new
name|Path
argument_list|(
literal|"/test/alice/file"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|newPath
init|=
operator|new
name|Path
argument_list|(
literal|"/test/bob/file"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|newPath
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FSDataOutputStream
name|fsDataOutputStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|old
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|message
init|=
literal|"Some data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|fsDataOutputStream
operator|.
name|write
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|fsDataOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|old
argument_list|)
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|old
argument_list|,
name|newPath
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|FSDataInputStream
name|bobStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|newPath
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
specifier|final
name|int
name|read
init|=
name|bobStream
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|bobStream
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|read
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
name|message
argument_list|)
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameDirectory ()
specifier|public
name|void
name|testRenameDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
specifier|final
name|Path
name|old
init|=
operator|new
name|Path
argument_list|(
literal|"/test/data/logs"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|newPath
init|=
operator|new
name|Path
argument_list|(
literal|"/test/var/logs"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|old
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|newPath
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|old
argument_list|)
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|old
argument_list|,
name|newPath
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameTheSameDirectory ()
specifier|public
name|void
name|testRenameTheSameDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
specifier|final
name|Path
name|old
init|=
operator|new
name|Path
argument_list|(
literal|"/test/usr/data"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|old
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|old
argument_list|,
name|old
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameDirectoryIntoExistingDirectory ()
specifier|public
name|void
name|testRenameDirectoryIntoExistingDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
name|Path
name|src
init|=
name|path
argument_list|(
literal|"/test/olddir/dir"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|path
argument_list|(
literal|"/test/olddir/dir/file1"
argument_list|)
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|path
argument_list|(
literal|"/test/olddir/dir/subdir/file2"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|dst
init|=
name|path
argument_list|(
literal|"/test/new/newdir"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dst
argument_list|)
expr_stmt|;
comment|//this renames into a child
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
name|assertExists
argument_list|(
literal|"new dir"
argument_list|,
name|path
argument_list|(
literal|"/test/new/newdir/dir"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"Renamed nested file1"
argument_list|,
name|path
argument_list|(
literal|"/test/new/newdir/dir/file1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"Nested file1 should have been deleted"
argument_list|,
name|path
argument_list|(
literal|"/test/olddir/dir/file1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"Renamed nested subdir"
argument_list|,
name|path
argument_list|(
literal|"/test/new/newdir/dir/subdir/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"file under subdir"
argument_list|,
name|path
argument_list|(
literal|"/test/new/newdir/dir/subdir/file2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"Nested /test/hadoop/dir/subdir/file2 still exists"
argument_list|,
name|path
argument_list|(
literal|"/test/olddir/dir/subdir/file2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * trying to rename a directory onto itself should fail,    * preserving everything underneath.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameDirToSelf ()
specifier|public
name|void
name|testRenameDirToSelf
parameter_list|()
throws|throws
name|Throwable
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
name|Path
name|parentdir
init|=
name|path
argument_list|(
literal|"/test/parentdir"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|parentdir
argument_list|)
expr_stmt|;
name|Path
name|child
init|=
operator|new
name|Path
argument_list|(
name|parentdir
argument_list|,
literal|"child"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|parentdir
argument_list|,
name|parentdir
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//verify the child is still there
name|assertIsFile
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that root directory renames are not allowed    *    * @throws Exception on failures    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameRootDirForbidden ()
specifier|public
name|void
name|testRenameRootDirForbidden
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
name|rename
argument_list|(
name|path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
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
comment|/**    * Assert that renaming a parent directory to be a child    * of itself is forbidden    *    * @throws Exception on failures    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameChildDirForbidden ()
specifier|public
name|void
name|testRenameChildDirForbidden
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
name|Path
name|parentdir
init|=
name|path
argument_list|(
literal|"/test/parentdir"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|parentdir
argument_list|)
expr_stmt|;
name|Path
name|childFile
init|=
operator|new
name|Path
argument_list|(
name|parentdir
argument_list|,
literal|"childfile"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|childFile
argument_list|)
expr_stmt|;
comment|//verify one level down
name|Path
name|childdir
init|=
operator|new
name|Path
argument_list|(
name|parentdir
argument_list|,
literal|"childdir"
argument_list|)
decl_stmt|;
name|rename
argument_list|(
name|parentdir
argument_list|,
name|childdir
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//now another level
name|fs
operator|.
name|mkdirs
argument_list|(
name|childdir
argument_list|)
expr_stmt|;
name|Path
name|childchilddir
init|=
operator|new
name|Path
argument_list|(
name|childdir
argument_list|,
literal|"childdir"
argument_list|)
decl_stmt|;
name|rename
argument_list|(
name|parentdir
argument_list|,
name|childchilddir
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
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameFileAndVerifyContents ()
specifier|public
name|void
name|testRenameFileAndVerifyContents
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
specifier|final
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/test/home/user/documents/file.txt"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|newFilePath
init|=
operator|new
name|Path
argument_list|(
literal|"/test/home/user/files/file.txt"
argument_list|)
decl_stmt|;
name|mkdirs
argument_list|(
name|newFilePath
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|len
init|=
literal|1024
decl_stmt|;
name|byte
index|[]
name|dataset
init|=
name|dataset
argument_list|(
name|len
argument_list|,
literal|'A'
argument_list|,
literal|26
argument_list|)
decl_stmt|;
name|writeDataset
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|dataset
argument_list|,
name|len
argument_list|,
name|len
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|filePath
argument_list|,
name|newFilePath
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|byte
index|[]
name|dest
init|=
name|readDataset
argument_list|(
name|fs
argument_list|,
name|newFilePath
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|compareByteArrays
argument_list|(
name|dataset
argument_list|,
name|dest
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|String
name|reread
init|=
name|readBytesToString
argument_list|(
name|fs
argument_list|,
name|newFilePath
argument_list|,
literal|20
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testMoveFileUnderParent ()
specifier|public
name|void
name|testMoveFileUnderParent
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
operator|!
name|renameSupported
argument_list|()
condition|)
return|return;
name|Path
name|filepath
init|=
name|path
argument_list|(
literal|"test/file"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|filepath
argument_list|)
expr_stmt|;
comment|//HDFS expects rename src, src -> true
name|rename
argument_list|(
name|filepath
argument_list|,
name|filepath
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//verify the file is still there
name|assertIsFile
argument_list|(
name|filepath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testMoveDirUnderParent ()
specifier|public
name|void
name|testMoveDirUnderParent
parameter_list|()
throws|throws
name|Throwable
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
name|Path
name|testdir
init|=
name|path
argument_list|(
literal|"test/dir"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|testdir
argument_list|)
expr_stmt|;
name|Path
name|parent
init|=
name|testdir
operator|.
name|getParent
argument_list|()
decl_stmt|;
comment|//the outcome here is ambiguous, so is not checked
name|fs
operator|.
name|rename
argument_list|(
name|testdir
argument_list|,
name|parent
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"Source directory has been deleted "
argument_list|,
name|testdir
argument_list|)
expr_stmt|;
block|}
comment|/**    * trying to rename a file onto itself should succeed (it's a no-op)    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameFileToSelf ()
specifier|public
name|void
name|testRenameFileToSelf
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
operator|!
name|renameSupported
argument_list|()
condition|)
return|return;
name|Path
name|filepath
init|=
name|path
argument_list|(
literal|"test/file"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|filepath
argument_list|)
expr_stmt|;
comment|//HDFS expects rename src, src -> true
name|rename
argument_list|(
name|filepath
argument_list|,
name|filepath
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//verify the file is still there
name|assertIsFile
argument_list|(
name|filepath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenamedConsistence ()
specifier|public
name|void
name|testRenamedConsistence
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
name|describe
argument_list|(
literal|"verify that overwriting a file with new data doesn't impact"
operator|+
literal|" the existing content"
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/test/home/user/documents/file.txt"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|newFilePath
init|=
operator|new
name|Path
argument_list|(
literal|"/test/home/user/files/file.txt"
argument_list|)
decl_stmt|;
name|mkdirs
argument_list|(
name|newFilePath
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|len
init|=
literal|1024
decl_stmt|;
name|byte
index|[]
name|dataset
init|=
name|dataset
argument_list|(
name|len
argument_list|,
literal|'A'
argument_list|,
literal|26
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dataset2
init|=
name|dataset
argument_list|(
name|len
argument_list|,
literal|'a'
argument_list|,
literal|26
argument_list|)
decl_stmt|;
name|writeDataset
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|dataset
argument_list|,
name|len
argument_list|,
name|len
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|filePath
argument_list|,
name|newFilePath
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|SwiftTestUtils
operator|.
name|writeAndRead
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|dataset2
argument_list|,
name|len
argument_list|,
name|len
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|byte
index|[]
name|dest
init|=
name|readDataset
argument_list|(
name|fs
argument_list|,
name|newFilePath
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|compareByteArrays
argument_list|(
name|dataset
argument_list|,
name|dest
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|String
name|reread
init|=
name|readBytesToString
argument_list|(
name|fs
argument_list|,
name|newFilePath
argument_list|,
literal|20
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testRenameMissingFile ()
specifier|public
name|void
name|testRenameMissingFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|assumeRenameSupported
argument_list|()
expr_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"/test/RenameMissingFile"
argument_list|)
decl_stmt|;
name|Path
name|path2
init|=
name|path
argument_list|(
literal|"/test/RenameMissingFileDest"
argument_list|)
decl_stmt|;
name|mkdirs
argument_list|(
name|path
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|path
argument_list|,
name|path2
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

