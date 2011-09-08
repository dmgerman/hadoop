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
name|io
operator|.
name|FileNotFoundException
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
name|util
operator|.
name|Random
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
name|junit
operator|.
name|Assert
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

begin_comment
comment|/**  * Helper class for unit tests.  */
end_comment

begin_class
DECL|class|FileSystemTestHelper
specifier|public
specifier|final
class|class
name|FileSystemTestHelper
block|{
comment|// The test root is relative to the<wd>/build/test/data by default
DECL|field|TEST_ROOT_DIR
specifier|public
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
literal|"target/test/data"
argument_list|)
operator|+
literal|"/test"
decl_stmt|;
DECL|field|DEFAULT_BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|DEFAULT_NUM_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_NUM_BLOCKS
init|=
literal|2
decl_stmt|;
DECL|field|absTestRootDir
specifier|private
specifier|static
name|String
name|absTestRootDir
init|=
literal|null
decl_stmt|;
comment|/** Hidden constructor */
DECL|method|FileSystemTestHelper ()
specifier|private
name|FileSystemTestHelper
parameter_list|()
block|{}
DECL|method|addFileSystemForTesting (URI uri, Configuration conf, FileSystem fs)
specifier|public
specifier|static
name|void
name|addFileSystemForTesting
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
operator|.
name|addFileSystemForTesting
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
DECL|method|getDefaultBlockSize ()
specifier|public
specifier|static
name|int
name|getDefaultBlockSize
parameter_list|()
block|{
return|return
name|DEFAULT_BLOCK_SIZE
return|;
block|}
DECL|method|getFileData (int numOfBlocks, long blockSize)
specifier|public
specifier|static
name|byte
index|[]
name|getFileData
parameter_list|(
name|int
name|numOfBlocks
parameter_list|,
name|long
name|blockSize
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
call|(
name|int
call|)
argument_list|(
name|numOfBlocks
operator|*
name|blockSize
argument_list|)
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
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
return|return
name|data
return|;
block|}
DECL|method|getTestRootPath (FileSystem fSys)
specifier|public
specifier|static
name|Path
name|getTestRootPath
parameter_list|(
name|FileSystem
name|fSys
parameter_list|)
block|{
return|return
name|fSys
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTestRootPath (FileSystem fSys, String pathString)
specifier|public
specifier|static
name|Path
name|getTestRootPath
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|String
name|pathString
parameter_list|)
block|{
return|return
name|fSys
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|pathString
argument_list|)
argument_list|)
return|;
block|}
comment|// the getAbsolutexxx method is needed because the root test dir
comment|// can be messed up by changing the working dir.
DECL|method|getAbsoluteTestRootDir (FileSystem fSys)
specifier|public
specifier|static
name|String
name|getAbsoluteTestRootDir
parameter_list|(
name|FileSystem
name|fSys
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|absTestRootDir
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|TEST_ROOT_DIR
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|absTestRootDir
operator|=
name|TEST_ROOT_DIR
expr_stmt|;
block|}
else|else
block|{
name|absTestRootDir
operator|=
name|fSys
operator|.
name|getWorkingDirectory
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|TEST_ROOT_DIR
expr_stmt|;
block|}
block|}
return|return
name|absTestRootDir
return|;
block|}
DECL|method|getAbsoluteTestRootPath (FileSystem fSys)
specifier|public
specifier|static
name|Path
name|getAbsoluteTestRootPath
parameter_list|(
name|FileSystem
name|fSys
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fSys
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|getAbsoluteTestRootDir
argument_list|(
name|fSys
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getDefaultWorkingDirectory (FileSystem fSys)
specifier|public
specifier|static
name|Path
name|getDefaultWorkingDirectory
parameter_list|(
name|FileSystem
name|fSys
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
literal|"/user/"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|fSys
operator|.
name|getUri
argument_list|()
argument_list|,
name|fSys
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * Create files with numBlocks blocks each with block size blockSize.    */
DECL|method|createFile (FileSystem fSys, Path path, int numBlocks, int blockSize, boolean createParent)
specifier|public
specifier|static
name|long
name|createFile
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|numBlocks
parameter_list|,
name|int
name|blockSize
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
name|fSys
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|4096
argument_list|,
name|fSys
operator|.
name|getDefaultReplication
argument_list|()
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|getFileData
argument_list|(
name|numBlocks
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|data
operator|.
name|length
return|;
block|}
DECL|method|createFile (FileSystem fSys, Path path, int numBlocks, int blockSize)
specifier|public
specifier|static
name|long
name|createFile
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|numBlocks
parameter_list|,
name|int
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createFile
argument_list|(
name|fSys
argument_list|,
name|path
argument_list|,
name|numBlocks
argument_list|,
name|blockSize
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|createFile (FileSystem fSys, Path path)
specifier|public
specifier|static
name|long
name|createFile
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createFile
argument_list|(
name|fSys
argument_list|,
name|path
argument_list|,
name|DEFAULT_NUM_BLOCKS
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|createFile (FileSystem fSys, String name)
specifier|public
specifier|static
name|long
name|createFile
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|createFile
argument_list|(
name|fSys
argument_list|,
name|path
argument_list|)
return|;
block|}
DECL|method|exists (FileSystem fSys, Path p)
specifier|public
specifier|static
name|boolean
name|exists
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fSys
operator|.
name|exists
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|isFile (FileSystem fSys, Path p)
specifier|public
specifier|static
name|boolean
name|isFile
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|fSys
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
operator|.
name|isFile
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|isDir (FileSystem fSys, Path p)
specifier|public
specifier|static
name|boolean
name|isDir
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|fSys
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
operator|.
name|isDirectory
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|writeFile (FileSystem fileSys, Path name, int fileSize)
specifier|static
name|String
name|writeFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|fileSize
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
comment|// Create and write a file that contains three blocks of data
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
return|;
block|}
DECL|method|readFile (FileSystem fs, Path name, int buflen)
specifier|static
name|String
name|readFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|buflen
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|buflen
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|remaining
init|,
name|n
init|;
operator|(
name|remaining
operator|=
name|b
operator|.
name|length
operator|-
name|offset
operator|)
operator|>
literal|0
operator|&&
operator|(
name|n
operator|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|remaining
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|;
name|offset
operator|+=
name|n
control|)
empty_stmt|;
name|assertEquals
argument_list|(
name|offset
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|b
operator|.
name|length
argument_list|,
name|in
operator|.
name|getPos
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|)
decl_stmt|;
return|return
name|s
return|;
block|}
DECL|method|containsPath (FileSystem fSys, Path path, FileStatus[] dirList)
specifier|public
specifier|static
name|FileStatus
name|containsPath
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|Path
name|path
parameter_list|,
name|FileStatus
index|[]
name|dirList
parameter_list|)
throws|throws
name|IOException
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
name|dirList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|dirList
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
return|return
name|dirList
index|[
name|i
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|containsPath (Path path, FileStatus[] dirList)
specifier|public
specifier|static
name|FileStatus
name|containsPath
parameter_list|(
name|Path
name|path
parameter_list|,
name|FileStatus
index|[]
name|dirList
parameter_list|)
throws|throws
name|IOException
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
name|dirList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|dirList
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
return|return
name|dirList
index|[
name|i
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|containsPath (FileSystem fSys, String path, FileStatus[] dirList)
specifier|public
specifier|static
name|FileStatus
name|containsPath
parameter_list|(
name|FileSystem
name|fSys
parameter_list|,
name|String
name|path
parameter_list|,
name|FileStatus
index|[]
name|dirList
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|containsPath
argument_list|(
name|fSys
argument_list|,
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
name|dirList
argument_list|)
return|;
block|}
DECL|enum|fileType
DECL|enumConstant|isDir
DECL|enumConstant|isFile
DECL|enumConstant|isSymlink
specifier|public
specifier|static
enum|enum
name|fileType
block|{
name|isDir
block|,
name|isFile
block|,
name|isSymlink
block|}
empty_stmt|;
DECL|method|checkFileStatus (FileSystem aFs, String path, fileType expectedType)
specifier|public
specifier|static
name|void
name|checkFileStatus
parameter_list|(
name|FileSystem
name|aFs
parameter_list|,
name|String
name|path
parameter_list|,
name|fileType
name|expectedType
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|s
init|=
name|aFs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedType
operator|==
name|fileType
operator|.
name|isDir
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedType
operator|==
name|fileType
operator|.
name|isFile
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedType
operator|==
name|fileType
operator|.
name|isSymlink
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|isSymlink
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|aFs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|,
name|s
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

