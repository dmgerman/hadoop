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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|ParentNotDirectoryException
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
name|snative
operator|.
name|SwiftNativeFileSystem
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

begin_comment
comment|/**  * This is the full filesystem contract test -which requires the  * Default config set up to point to a filesystem.  *  * Some of the tests override the base class tests -these  * are where SwiftFS does not implement those features, or  * when the behavior of SwiftFS does not match the normal  * contract -which normally means that directories and equal files  * are being treated as equal.  */
end_comment

begin_class
DECL|class|TestSwiftFileSystemContract
specifier|public
class|class
name|TestSwiftFileSystemContract
extends|extends
name|FileSystemContractBaseTest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestSwiftFileSystemContract
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Override this if the filesystem is not case sensitive    * @return true if the case detection/preservation tests should run    */
DECL|method|filesystemIsCaseSensitive ()
specifier|protected
name|boolean
name|filesystemIsCaseSensitive
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|URI
name|uri
init|=
name|getFilesystemURI
argument_list|()
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|fs
operator|=
name|createSwiftFS
argument_list|()
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//FS init failed, set it to null so that teardown doesn't
comment|//attempt to use it
name|fs
operator|=
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|getFilesystemURI ()
specifier|protected
name|URI
name|getFilesystemURI
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
return|return
name|SwiftTestUtils
operator|.
name|getServiceURI
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createSwiftFS ()
specifier|protected
name|SwiftNativeFileSystem
name|createSwiftFS
parameter_list|()
throws|throws
name|IOException
block|{
name|SwiftNativeFileSystem
name|swiftNativeFileSystem
init|=
operator|new
name|SwiftNativeFileSystem
argument_list|()
decl_stmt|;
return|return
name|swiftNativeFileSystem
return|;
block|}
annotation|@
name|Override
DECL|method|testMkdirsFailsForSubdirectoryOfExistingFile ()
specifier|public
name|void
name|testMkdirsFailsForSubdirectoryOfExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|testDir
init|=
name|path
argument_list|(
literal|"/test/hadoop"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|filepath
init|=
name|path
argument_list|(
literal|"/test/hadoop/file"
argument_list|)
decl_stmt|;
name|SwiftTestUtils
operator|.
name|writeTextFile
argument_list|(
name|fs
argument_list|,
name|filepath
argument_list|,
literal|"hello, world"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Path
name|testSubDir
init|=
operator|new
name|Path
argument_list|(
name|filepath
argument_list|,
literal|"subdir"
argument_list|)
decl_stmt|;
name|SwiftTestUtils
operator|.
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"subdir before mkdir"
argument_list|,
name|testSubDir
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|testSubDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw IOException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParentNotDirectoryException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|//now verify that the subdir path does not exist
name|SwiftTestUtils
operator|.
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"subdir after mkdir"
argument_list|,
name|testSubDir
argument_list|)
expr_stmt|;
name|Path
name|testDeepSubDir
init|=
name|path
argument_list|(
literal|"/test/hadoop/file/deep/sub/dir"
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|testDeepSubDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw IOException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParentNotDirectoryException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|SwiftTestUtils
operator|.
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"testDeepSubDir  after mkdir"
argument_list|,
name|testDeepSubDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testWriteReadAndDeleteEmptyFile ()
specifier|public
name|void
name|testWriteReadAndDeleteEmptyFile
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|super
operator|.
name|testWriteReadAndDeleteEmptyFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
name|SwiftTestUtils
operator|.
name|downgrade
argument_list|(
literal|"empty files get mistaken for directories"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
comment|//unsupported
block|}
DECL|method|testZeroByteFilesAreFiles ()
specifier|public
name|void
name|testZeroByteFilesAreFiles
parameter_list|()
throws|throws
name|Exception
block|{
comment|//    SwiftTestUtils.unsupported("testZeroByteFilesAreFiles");
block|}
block|}
end_class

end_unit

