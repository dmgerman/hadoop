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
name|DataInputStream
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
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|Options
operator|.
name|CreateOpts
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
name|Options
operator|.
name|CreateOpts
operator|.
name|BlockSize
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
name|Options
operator|.
name|Rename
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
name|security
operator|.
name|AccessControlException
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
name|Progressable
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

begin_comment
comment|/**  * Helper class for unit tests.  */
end_comment

begin_class
DECL|class|FileSystemTestWrapper
specifier|public
specifier|final
class|class
name|FileSystemTestWrapper
extends|extends
name|FSTestWrapper
block|{
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|method|FileSystemTestWrapper (FileSystem fs)
specifier|public
name|FileSystemTestWrapper
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
name|this
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|FileSystemTestWrapper (FileSystem fs, String rootDir)
specifier|public
name|FileSystemTestWrapper
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|rootDir
parameter_list|)
block|{
name|super
argument_list|(
name|rootDir
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
block|}
DECL|method|getLocalFSWrapper ()
specifier|public
name|FSTestWrapper
name|getLocalFSWrapper
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileSystemTestWrapper
argument_list|(
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getDefaultWorkingDirectory ()
specifier|public
name|Path
name|getDefaultWorkingDirectory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getTestRootPath
argument_list|(
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
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * Create files with numBlocks blocks each with block size blockSize.    */
DECL|method|createFile (Path path, int numBlocks, CreateOpts... options)
specifier|public
name|long
name|createFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|numBlocks
parameter_list|,
name|CreateOpts
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockSize
name|blockSizeOpt
init|=
name|CreateOpts
operator|.
name|getOpt
argument_list|(
name|CreateOpts
operator|.
name|BlockSize
operator|.
name|class
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|long
name|blockSize
init|=
name|blockSizeOpt
operator|!=
literal|null
condition|?
name|blockSizeOpt
operator|.
name|getValue
argument_list|()
else|:
name|DEFAULT_BLOCK_SIZE
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|create
argument_list|(
name|path
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|,
name|options
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
DECL|method|createFile (Path path, int numBlocks, int blockSize)
specifier|public
name|long
name|createFile
parameter_list|(
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
name|path
argument_list|,
name|numBlocks
argument_list|,
name|CreateOpts
operator|.
name|blockSize
argument_list|(
name|blockSize
argument_list|)
argument_list|,
name|CreateOpts
operator|.
name|createParent
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createFile (Path path)
specifier|public
name|long
name|createFile
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createFile
argument_list|(
name|path
argument_list|,
name|DEFAULT_NUM_BLOCKS
argument_list|,
name|CreateOpts
operator|.
name|createParent
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createFile (String name)
specifier|public
name|long
name|createFile
parameter_list|(
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
name|name
argument_list|)
decl_stmt|;
return|return
name|createFile
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|createFileNonRecursive (String name)
specifier|public
name|long
name|createFileNonRecursive
parameter_list|(
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
name|name
argument_list|)
decl_stmt|;
return|return
name|createFileNonRecursive
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|createFileNonRecursive (Path path)
specifier|public
name|long
name|createFileNonRecursive
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createFile
argument_list|(
name|path
argument_list|,
name|DEFAULT_NUM_BLOCKS
argument_list|,
name|CreateOpts
operator|.
name|donotCreateParent
argument_list|()
argument_list|)
return|;
block|}
DECL|method|appendToFile (Path path, int numBlocks, CreateOpts... options)
specifier|public
name|void
name|appendToFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|numBlocks
parameter_list|,
name|CreateOpts
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockSize
name|blockSizeOpt
init|=
name|CreateOpts
operator|.
name|getOpt
argument_list|(
name|CreateOpts
operator|.
name|BlockSize
operator|.
name|class
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|long
name|blockSize
init|=
name|blockSizeOpt
operator|!=
literal|null
condition|?
name|blockSizeOpt
operator|.
name|getValue
argument_list|()
else|:
name|DEFAULT_BLOCK_SIZE
decl_stmt|;
name|FSDataOutputStream
name|out
decl_stmt|;
name|out
operator|=
name|fs
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
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
block|}
DECL|method|exists (Path p)
specifier|public
name|boolean
name|exists
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|exists
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|isFile (Path p)
specifier|public
name|boolean
name|isFile
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|fs
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
DECL|method|isDir (Path p)
specifier|public
name|boolean
name|isDir
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|fs
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
DECL|method|isSymlink (Path p)
specifier|public
name|boolean
name|isSymlink
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|fs
operator|.
name|getFileLinkStatus
argument_list|(
name|p
argument_list|)
operator|.
name|isSymlink
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
DECL|method|writeFile (Path path, byte b[])
specifier|public
name|void
name|writeFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|byte
name|b
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
name|create
argument_list|(
name|path
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|,
name|CreateOpts
operator|.
name|createParent
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|readFile (Path path, int len)
specifier|public
name|byte
index|[]
name|readFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|dis
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|dis
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|dis
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|buffer
return|;
block|}
DECL|method|containsPath (Path path, FileStatus[] dirList)
specifier|public
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
DECL|method|containsPath (String path, FileStatus[] dirList)
specifier|public
name|FileStatus
name|containsPath
parameter_list|(
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
DECL|method|checkFileStatus (String path, fileType expectedType)
specifier|public
name|void
name|checkFileStatus
parameter_list|(
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
name|fs
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
name|fs
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
DECL|method|checkFileLinkStatus (String path, fileType expectedType)
specifier|public
name|void
name|checkFileLinkStatus
parameter_list|(
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
name|fs
operator|.
name|getFileLinkStatus
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
name|fs
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
comment|//
comment|// FileContext wrappers
comment|//
annotation|@
name|Override
DECL|method|makeQualified (Path path)
specifier|public
name|Path
name|makeQualified
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|mkdir (Path dir, FsPermission permission, boolean createParent)
specifier|public
name|void
name|mkdir
parameter_list|(
name|Path
name|dir
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileAlreadyExistsException
throws|,
name|FileNotFoundException
throws|,
name|ParentNotDirectoryException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
name|fs
operator|.
name|primitiveMkdir
argument_list|(
name|dir
argument_list|,
name|permission
argument_list|,
name|createParent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|delete (Path f, boolean recursive)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
return|return
name|fs
operator|.
name|delete
argument_list|(
name|f
argument_list|,
name|recursive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileLinkStatus (Path f)
specifier|public
name|FileStatus
name|getFileLinkStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
return|return
name|fs
operator|.
name|getFileLinkStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createSymlink (Path target, Path link, boolean createParent)
specifier|public
name|void
name|createSymlink
parameter_list|(
name|Path
name|target
parameter_list|,
name|Path
name|link
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileAlreadyExistsException
throws|,
name|FileNotFoundException
throws|,
name|ParentNotDirectoryException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
name|fs
operator|.
name|createSymlink
argument_list|(
name|target
argument_list|,
name|link
argument_list|,
name|createParent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setWorkingDirectory (Path newWDir)
specifier|public
name|void
name|setWorkingDirectory
parameter_list|(
name|Path
name|newWDir
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|setWorkingDirectory
argument_list|(
name|newWDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWorkingDirectory ()
specifier|public
name|Path
name|getWorkingDirectory
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStatus (Path f)
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
return|return
name|fs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create (Path f, EnumSet<CreateFlag> createFlag, CreateOpts... opts)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|createFlag
parameter_list|,
name|CreateOpts
modifier|...
name|opts
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileAlreadyExistsException
throws|,
name|FileNotFoundException
throws|,
name|ParentNotDirectoryException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
comment|// Need to translate the FileContext-style options into FileSystem-style
comment|// Permissions with umask
name|CreateOpts
operator|.
name|Perms
name|permOpt
init|=
name|CreateOpts
operator|.
name|getOpt
argument_list|(
name|CreateOpts
operator|.
name|Perms
operator|.
name|class
argument_list|,
name|opts
argument_list|)
decl_stmt|;
name|FsPermission
name|umask
init|=
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|FsPermission
name|permission
init|=
operator|(
name|permOpt
operator|!=
literal|null
operator|)
condition|?
name|permOpt
operator|.
name|getValue
argument_list|()
else|:
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
operator|.
name|applyUMask
argument_list|(
name|umask
argument_list|)
decl_stmt|;
name|permission
operator|=
name|permission
operator|.
name|applyUMask
argument_list|(
name|umask
argument_list|)
expr_stmt|;
comment|// Overwrite
name|boolean
name|overwrite
init|=
name|createFlag
operator|.
name|contains
argument_list|(
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
decl_stmt|;
comment|// bufferSize
name|int
name|bufferSize
init|=
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|CreateOpts
operator|.
name|BufferSize
name|bufOpt
init|=
name|CreateOpts
operator|.
name|getOpt
argument_list|(
name|CreateOpts
operator|.
name|BufferSize
operator|.
name|class
argument_list|,
name|opts
argument_list|)
decl_stmt|;
name|bufferSize
operator|=
operator|(
name|bufOpt
operator|!=
literal|null
operator|)
condition|?
name|bufOpt
operator|.
name|getValue
argument_list|()
else|:
name|bufferSize
expr_stmt|;
comment|// replication
name|short
name|replication
init|=
name|fs
operator|.
name|getDefaultReplication
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|CreateOpts
operator|.
name|ReplicationFactor
name|repOpt
init|=
name|CreateOpts
operator|.
name|getOpt
argument_list|(
name|CreateOpts
operator|.
name|ReplicationFactor
operator|.
name|class
argument_list|,
name|opts
argument_list|)
decl_stmt|;
name|replication
operator|=
operator|(
name|repOpt
operator|!=
literal|null
operator|)
condition|?
name|repOpt
operator|.
name|getValue
argument_list|()
else|:
name|replication
expr_stmt|;
comment|// blockSize
name|long
name|blockSize
init|=
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|CreateOpts
operator|.
name|BlockSize
name|blockOpt
init|=
name|CreateOpts
operator|.
name|getOpt
argument_list|(
name|CreateOpts
operator|.
name|BlockSize
operator|.
name|class
argument_list|,
name|opts
argument_list|)
decl_stmt|;
name|blockSize
operator|=
operator|(
name|blockOpt
operator|!=
literal|null
operator|)
condition|?
name|blockOpt
operator|.
name|getValue
argument_list|()
else|:
name|blockSize
expr_stmt|;
comment|// Progressable
name|Progressable
name|progress
init|=
literal|null
decl_stmt|;
name|CreateOpts
operator|.
name|Progress
name|progressOpt
init|=
name|CreateOpts
operator|.
name|getOpt
argument_list|(
name|CreateOpts
operator|.
name|Progress
operator|.
name|class
argument_list|,
name|opts
argument_list|)
decl_stmt|;
name|progress
operator|=
operator|(
name|progressOpt
operator|!=
literal|null
operator|)
condition|?
name|progressOpt
operator|.
name|getValue
argument_list|()
else|:
name|progress
expr_stmt|;
return|return
name|fs
operator|.
name|create
argument_list|(
name|f
argument_list|,
name|permission
argument_list|,
name|overwrite
argument_list|,
name|bufferSize
argument_list|,
name|replication
argument_list|,
name|blockSize
argument_list|,
name|progress
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|open (Path f)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
return|return
name|fs
operator|.
name|open
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLinkTarget (Path f)
specifier|public
name|Path
name|getLinkTarget
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
return|return
name|fs
operator|.
name|getLinkTarget
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setReplication (final Path f, final short replication)
specifier|public
name|boolean
name|setReplication
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|short
name|replication
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|IOException
block|{
return|return
name|fs
operator|.
name|setReplication
argument_list|(
name|f
argument_list|,
name|replication
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|rename (Path src, Path dst, Rename... options)
specifier|public
name|void
name|rename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|,
name|Rename
modifier|...
name|options
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileAlreadyExistsException
throws|,
name|FileNotFoundException
throws|,
name|ParentNotDirectoryException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
name|fs
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFileBlockLocations (Path f, long start, long len)
specifier|public
name|BlockLocation
index|[]
name|getFileBlockLocations
parameter_list|(
name|Path
name|f
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
return|return
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|f
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileChecksum (Path f)
specifier|public
name|FileChecksum
name|getFileChecksum
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|IOException
block|{
return|return
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|class|FakeRemoteIterator
specifier|private
class|class
name|FakeRemoteIterator
parameter_list|<
name|E
parameter_list|>
implements|implements
name|RemoteIterator
argument_list|<
name|E
argument_list|>
block|{
DECL|field|elements
specifier|private
name|E
index|[]
name|elements
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|method|FakeRemoteIterator (E[] elements)
name|FakeRemoteIterator
parameter_list|(
name|E
index|[]
name|elements
parameter_list|)
block|{
name|this
operator|.
name|elements
operator|=
name|elements
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|count
operator|<
name|elements
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|E
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|elements
index|[
name|count
operator|++
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|listStatusIterator (Path f)
specifier|public
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|listStatusIterator
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
comment|// Fake the RemoteIterator, because FileSystem has no such thing
name|FileStatus
index|[]
name|statuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|f
argument_list|)
decl_stmt|;
return|return
operator|new
name|FakeRemoteIterator
argument_list|<
name|FileStatus
argument_list|>
argument_list|(
name|statuses
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setPermission (final Path f, final FsPermission permission)
specifier|public
name|void
name|setPermission
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
name|fs
operator|.
name|setPermission
argument_list|(
name|f
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setOwner (final Path f, final String username, final String groupname)
specifier|public
name|void
name|setOwner
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|groupname
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|UnsupportedFileSystemException
throws|,
name|FileNotFoundException
throws|,
name|IOException
block|{
name|fs
operator|.
name|setOwner
argument_list|(
name|f
argument_list|,
name|username
argument_list|,
name|groupname
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTimes (Path f, long mtime, long atime)
specifier|public
name|void
name|setTimes
parameter_list|(
name|Path
name|f
parameter_list|,
name|long
name|mtime
parameter_list|,
name|long
name|atime
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
name|fs
operator|.
name|setTimes
argument_list|(
name|f
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|listStatus (Path f)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
return|return
name|fs
operator|.
name|listStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|globStatus (Path pathPattern, PathFilter filter)
specifier|public
name|FileStatus
index|[]
name|globStatus
parameter_list|(
name|Path
name|pathPattern
parameter_list|,
name|PathFilter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|globStatus
argument_list|(
name|pathPattern
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

