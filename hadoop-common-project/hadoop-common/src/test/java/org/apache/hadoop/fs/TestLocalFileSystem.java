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
operator|.
name|Statistics
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
name|util
operator|.
name|Shell
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
name|FileSystemTestHelper
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|*
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

begin_comment
comment|/**  * This class tests the local file system via the FileSystem abstraction.  */
end_comment

begin_class
DECL|class|TestLocalFileSystem
specifier|public
class|class
name|TestLocalFileSystem
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ROOT_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
operator|+
literal|"/work-dir/localfs"
decl_stmt|;
DECL|field|base
specifier|private
specifier|final
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|fileSys
specifier|private
name|LocalFileSystem
name|fileSys
decl_stmt|;
DECL|method|cleanupFile (FileSystem fs, Path name)
specifier|private
name|void
name|cleanupFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|fileSys
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after ()
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtil
operator|.
name|setWritable
argument_list|(
name|base
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|base
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the capability of setting the working directory.    */
annotation|@
name|Test
DECL|method|testWorkingDirectory ()
specifier|public
name|void
name|testWorkingDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|origDir
init|=
name|fileSys
operator|.
name|getWorkingDirectory
argument_list|()
decl_stmt|;
name|Path
name|subdir
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"new"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// make sure it doesn't already exist
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|subdir
argument_list|)
argument_list|)
expr_stmt|;
comment|// make it and check for it
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|subdir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|isDirectory
argument_list|(
name|subdir
argument_list|)
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|setWorkingDirectory
argument_list|(
name|subdir
argument_list|)
expr_stmt|;
comment|// create a directory and check for it
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
literal|"dir1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|dir1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|isDirectory
argument_list|(
name|dir1
argument_list|)
argument_list|)
expr_stmt|;
comment|// delete the directory and make sure it went away
name|fileSys
operator|.
name|delete
argument_list|(
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|dir1
argument_list|)
argument_list|)
expr_stmt|;
comment|// create files and manipulate them.
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"file1"
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
literal|"sub/file2"
argument_list|)
decl_stmt|;
name|String
name|contents
init|=
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|fileSys
operator|.
name|copyFromLocalFile
argument_list|(
name|file1
argument_list|,
name|file2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|isFile
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file2
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|copyToLocalFile
argument_list|(
name|file1
argument_list|,
name|file2
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file2
argument_list|)
expr_stmt|;
comment|// try a rename
name|fileSys
operator|.
name|rename
argument_list|(
name|file1
argument_list|,
name|file2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|file2
argument_list|)
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|rename
argument_list|(
name|file2
argument_list|,
name|file1
argument_list|)
expr_stmt|;
comment|// try reading a file
name|InputStream
name|stm
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|file1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|3
index|]
decl_stmt|;
name|int
name|bytesRead
init|=
name|stm
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|contents
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytesRead
argument_list|)
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fileSys
operator|.
name|setWorkingDirectory
argument_list|(
name|origDir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * test Syncable interface on raw local file system    * @throws IOException    */
annotation|@
name|Test
DECL|method|testSyncable ()
specifier|public
name|void
name|testSyncable
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getRawFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"syncable"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|)
decl_stmt|;
empty_stmt|;
specifier|final
name|int
name|bytesWritten
init|=
literal|1
decl_stmt|;
name|byte
index|[]
name|expectedBuf
init|=
operator|new
name|byte
index|[]
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|}
decl_stmt|;
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|expectedBuf
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|verifyFile
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|bytesWritten
argument_list|,
name|expectedBuf
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|expectedBuf
argument_list|,
name|bytesWritten
argument_list|,
name|expectedBuf
operator|.
name|length
operator|-
name|bytesWritten
argument_list|)
expr_stmt|;
name|out
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|verifyFile
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|expectedBuf
operator|.
name|length
argument_list|,
name|expectedBuf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|verifyFile (FileSystem fs, Path file, int bytesToVerify, byte[] expectedBytes)
specifier|private
name|void
name|verifyFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|file
parameter_list|,
name|int
name|bytesToVerify
parameter_list|,
name|byte
index|[]
name|expectedBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|readBuf
init|=
operator|new
name|byte
index|[
name|bytesToVerify
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|readBuf
argument_list|,
literal|0
argument_list|,
name|bytesToVerify
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bytesToVerify
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expectedBytes
index|[
name|i
index|]
argument_list|,
name|readBuf
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCopy ()
specifier|public
name|void
name|testCopy
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LocalFileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|src
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"dingo"
argument_list|)
decl_stmt|;
name|Path
name|dst
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"yak"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fs
argument_list|,
name|src
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|FileUtil
operator|.
name|copy
argument_list|(
name|fs
argument_list|,
name|src
argument_list|,
name|fs
argument_list|,
name|dst
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|src
argument_list|)
operator|&&
name|fs
operator|.
name|exists
argument_list|(
name|dst
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|FileUtil
operator|.
name|copy
argument_list|(
name|fs
argument_list|,
name|dst
argument_list|,
name|fs
argument_list|,
name|src
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|src
argument_list|)
operator|&&
name|fs
operator|.
name|exists
argument_list|(
name|dst
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|FileUtil
operator|.
name|copy
argument_list|(
name|fs
argument_list|,
name|src
argument_list|,
name|fs
argument_list|,
name|dst
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|src
argument_list|)
operator|&&
name|fs
operator|.
name|exists
argument_list|(
name|dst
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|FileUtil
operator|.
name|copy
argument_list|(
name|fs
argument_list|,
name|dst
argument_list|,
name|fs
argument_list|,
name|src
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|tmp
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|,
name|dst
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|tmp
argument_list|)
operator|&&
name|fs
operator|.
name|exists
argument_list|(
name|dst
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|FileUtil
operator|.
name|copy
argument_list|(
name|fs
argument_list|,
name|dst
argument_list|,
name|fs
argument_list|,
name|src
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
name|tmp
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
try|try
block|{
name|FileUtil
operator|.
name|copy
argument_list|(
name|fs
argument_list|,
name|dst
argument_list|,
name|fs
argument_list|,
name|src
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to detect existing dir"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
annotation|@
name|Test
DECL|method|testHomeDirectory ()
specifier|public
name|void
name|testHomeDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|fileSys
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|home
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|fileSys
argument_list|)
decl_stmt|;
name|Path
name|fsHome
init|=
name|fileSys
operator|.
name|getHomeDirectory
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|home
argument_list|,
name|fsHome
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPathEscapes ()
specifier|public
name|void
name|testPathEscapes
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"foo%bar"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|path
operator|.
name|makeQualified
argument_list|(
name|fs
argument_list|)
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirs ()
specifier|public
name|void
name|testMkdirs
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LocalFileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|test_dir
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"test_dir"
argument_list|)
decl_stmt|;
name|Path
name|test_file
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|test_dir
argument_list|)
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fs
argument_list|,
name|test_file
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// creating dir over a file
name|Path
name|bad_dir
init|=
operator|new
name|Path
argument_list|(
name|test_file
argument_list|,
literal|"another_dir"
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|bad_dir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to detect existing file in path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to detect null in mkdir arg"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
comment|/** Test deleting a file, directory, and non-existent path */
annotation|@
name|Test
DECL|method|testBasicDelete ()
specifier|public
name|void
name|testBasicDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LocalFileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"dir1"
argument_list|)
decl_stmt|;
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
operator|+
literal|"/dir1"
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
name|Path
name|file3
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"does-not-exist"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir1
argument_list|)
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fs
argument_list|,
name|file2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Returned true deleting non-existant path"
argument_list|,
name|fs
operator|.
name|delete
argument_list|(
name|file3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Did not delete file"
argument_list|,
name|fs
operator|.
name|delete
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Did not delete non-empty dir"
argument_list|,
name|fs
operator|.
name|delete
argument_list|(
name|dir1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatistics ()
specifier|public
name|void
name|testStatistics
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|fileSchemeCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Statistics
name|stats
range|:
name|FileSystem
operator|.
name|getAllStatistics
argument_list|()
control|)
block|{
if|if
condition|(
name|stats
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
name|fileSchemeCount
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fileSchemeCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHasFileDescriptor ()
specifier|public
name|void
name|testHasFileDescriptor
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LocalFileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"test-file"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|BufferedFSInputStream
name|bis
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bis
operator|=
operator|new
name|BufferedFSInputStream
argument_list|(
operator|new
name|RawLocalFileSystem
argument_list|()
operator|.
operator|new
name|LocalFSFileInputStream
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|bis
operator|.
name|getFileDescriptor
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|bis
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testListStatusWithColons ()
specifier|public
name|void
name|testListStatusWithColons
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
operator|!
name|Shell
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LocalFileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|File
name|colonFile
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"foo:bar"
argument_list|)
decl_stmt|;
name|colonFile
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileStatus
index|[]
name|stats
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of stats"
argument_list|,
literal|1
argument_list|,
name|stats
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bad path from stat"
argument_list|,
name|colonFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|stats
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReportChecksumFailure ()
specifier|public
name|void
name|testReportChecksumFailure
parameter_list|()
throws|throws
name|IOException
block|{
name|base
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|base
operator|.
name|exists
argument_list|()
operator|&&
name|base
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|File
name|dir1
init|=
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"dir1"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|dir2
init|=
operator|new
name|File
argument_list|(
name|dir1
argument_list|,
literal|"dir2"
argument_list|)
decl_stmt|;
name|dir2
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|dir2
operator|.
name|exists
argument_list|()
operator|&&
name|FileUtil
operator|.
name|canWrite
argument_list|(
name|dir2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|dataFileName
init|=
literal|"corruptedData"
decl_stmt|;
specifier|final
name|Path
name|dataPath
init|=
operator|new
name|Path
argument_list|(
operator|new
name|File
argument_list|(
name|dir2
argument_list|,
name|dataFileName
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|checksumPath
init|=
name|fileSys
operator|.
name|getChecksumFile
argument_list|(
name|dataPath
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|fsdos
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|dataPath
argument_list|)
decl_stmt|;
try|try
block|{
name|fsdos
operator|.
name|writeUTF
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsdos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|pathToFile
argument_list|(
name|dataPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|dataFileLength
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|dataPath
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|dataFileLength
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// check the the checksum file is created and not empty:
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|pathToFile
argument_list|(
name|checksumPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|checksumFileLength
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|checksumPath
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|checksumFileLength
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// this is a hack to force the #reportChecksumFailure() method to stop
comment|// climbing up at the 'base' directory and use 'dir1/bad_files' as the
comment|// corrupted files storage:
name|FileUtil
operator|.
name|setWritable
argument_list|(
name|base
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|dataFsdis
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|dataPath
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|checksumFsdis
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|checksumPath
argument_list|)
decl_stmt|;
name|boolean
name|retryIsNecessary
init|=
name|fileSys
operator|.
name|reportChecksumFailure
argument_list|(
name|dataPath
argument_list|,
name|dataFsdis
argument_list|,
literal|0
argument_list|,
name|checksumFsdis
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|retryIsNecessary
argument_list|)
expr_stmt|;
comment|// the data file should be moved:
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|pathToFile
argument_list|(
name|dataPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// the checksum file should be moved:
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|pathToFile
argument_list|(
name|checksumPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// check that the files exist in the new location where they were moved:
name|File
index|[]
name|dir1files
init|=
name|dir1
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
return|return
name|pathname
operator|!=
literal|null
operator|&&
operator|!
name|pathname
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"dir2"
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dir1files
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dir1files
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|File
name|badFilesDir
init|=
name|dir1files
index|[
literal|0
index|]
decl_stmt|;
name|File
index|[]
name|badFiles
init|=
name|badFilesDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|badFiles
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|badFiles
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|boolean
name|dataFileFound
init|=
literal|false
decl_stmt|;
name|boolean
name|checksumFileFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|File
name|badFile
range|:
name|badFiles
control|)
block|{
if|if
condition|(
name|badFile
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|dataFileName
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|dataFileLength
operator|==
name|badFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|dataFileFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|badFile
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
name|dataFileName
operator|+
literal|".crc"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|checksumFileLength
operator|==
name|badFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|checksumFileFound
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|dataFileFound
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|checksumFileFound
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

