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
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|Shell
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Test symbolic links using LocalFs.  */
end_comment

begin_class
DECL|class|TestSymlinkLocalFS
specifier|abstract
specifier|public
class|class
name|TestSymlinkLocalFS
extends|extends
name|SymlinkBaseTest
block|{
comment|// Workaround for HADOOP-9652
static|static
block|{
name|RawLocalFileSystem
operator|.
name|useStatIfAvailable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getScheme ()
specifier|protected
name|String
name|getScheme
parameter_list|()
block|{
return|return
literal|"file"
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
name|wrapper
operator|.
name|getAbsoluteTestRootDir
argument_list|()
operator|+
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
name|wrapper
operator|.
name|getAbsoluteTestRootDir
argument_list|()
operator|+
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
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
literal|"file:///"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|emulatingSymlinksOnWindows ()
specifier|protected
name|boolean
name|emulatingSymlinksOnWindows
parameter_list|()
block|{
comment|// Java 6 on Windows has very poor symlink support. Specifically
comment|// Specifically File#length and File#renameTo do not work as expected.
comment|// (see HADOOP-9061 for additional details)
comment|// Hence some symlink tests will be skipped.
comment|//
return|return
operator|(
name|Shell
operator|.
name|WINDOWS
operator|&&
operator|!
name|Shell
operator|.
name|isJava7OrAbove
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|testCreateDanglingLink ()
specifier|public
name|void
name|testCreateDanglingLink
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Dangling symlinks are not supported on Windows local file system.
name|assumeTrue
argument_list|(
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|super
operator|.
name|testCreateDanglingLink
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testCreateFileViaDanglingLinkParent ()
specifier|public
name|void
name|testCreateFileViaDanglingLinkParent
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|super
operator|.
name|testCreateFileViaDanglingLinkParent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testOpenResolvesLinks ()
specifier|public
name|void
name|testOpenResolvesLinks
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|super
operator|.
name|testOpenResolvesLinks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testRecursiveLinks ()
specifier|public
name|void
name|testRecursiveLinks
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|super
operator|.
name|testRecursiveLinks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testRenameDirToDanglingSymlink ()
specifier|public
name|void
name|testRenameDirToDanglingSymlink
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|super
operator|.
name|testRenameDirToDanglingSymlink
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testStatDanglingLink ()
specifier|public
name|void
name|testStatDanglingLink
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|super
operator|.
name|testStatDanglingLink
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
comment|/** lstat a non-existant file using a partially qualified path */
DECL|method|testDanglingLinkFilePartQual ()
specifier|public
name|void
name|testDanglingLinkFilePartQual
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|filePartQual
init|=
operator|new
name|Path
argument_list|(
name|getScheme
argument_list|()
operator|+
literal|":///doesNotExist"
argument_list|)
decl_stmt|;
try|try
block|{
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|filePartQual
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Got FileStatus for non-existant file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|f
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|wrapper
operator|.
name|getLinkTarget
argument_list|(
name|filePartQual
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Got link target for non-existant file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|f
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
literal|1000
argument_list|)
comment|/** Stat and lstat a dangling link */
DECL|method|testDanglingLink ()
specifier|public
name|void
name|testDanglingLink
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|Path
name|fileAbs
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
operator|+
literal|"/file"
argument_list|)
decl_stmt|;
name|Path
name|fileQual
init|=
operator|new
name|Path
argument_list|(
name|testURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|fileAbs
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
operator|+
literal|"/linkToFile"
argument_list|)
decl_stmt|;
name|Path
name|linkQual
init|=
operator|new
name|Path
argument_list|(
name|testURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|link
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|fileAbs
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Deleting the link using FileContext currently fails because
comment|// resolve looks up LocalFs rather than RawLocalFs for the path
comment|// so we call ChecksumFs delete (which doesn't delete dangling
comment|// links) instead of delegating to delete in RawLocalFileSystem
comment|// which deletes via fullyDelete. testDeleteLink above works
comment|// because the link is not dangling.
comment|//assertTrue(fc.delete(link, false));
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|link
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|fileAbs
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|link
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Got FileStatus for dangling link"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|f
parameter_list|)
block|{
comment|// Expected. File's exists method returns false for dangling links
block|}
comment|// We can stat a dangling link
name|UserGroupInformation
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|FileStatus
name|fsd
init|=
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|link
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fileQual
argument_list|,
name|fsd
operator|.
name|getSymlink
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fsd
operator|.
name|isSymlink
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fsd
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getUserName
argument_list|()
argument_list|,
name|fsd
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
comment|// Compare against user's primary group
name|assertEquals
argument_list|(
name|user
operator|.
name|getGroupNames
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|fsd
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|linkQual
argument_list|,
name|fsd
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Accessing the link
try|try
block|{
name|readFile
argument_list|(
name|link
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Got FileStatus for dangling link"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|f
parameter_list|)
block|{
comment|// Ditto.
block|}
comment|// Creating the file makes the link work
name|createAndWriteFile
argument_list|(
name|fileAbs
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|getFileStatus
argument_list|(
name|link
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
comment|/**     * Test getLinkTarget with a partially qualified target.     * NB: Hadoop does not support fully qualified URIs for the     * file scheme (eg file://host/tmp/test).    */
DECL|method|testGetLinkStatusPartQualTarget ()
specifier|public
name|void
name|testGetLinkStatusPartQualTarget
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
operator|!
name|emulatingSymlinksOnWindows
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|fileAbs
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
operator|+
literal|"/file"
argument_list|)
decl_stmt|;
name|Path
name|fileQual
init|=
operator|new
name|Path
argument_list|(
name|testURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|fileAbs
argument_list|)
decl_stmt|;
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
name|link
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir1
argument_list|()
operator|+
literal|"/linkToFile"
argument_list|)
decl_stmt|;
name|Path
name|dirNew
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir2
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|linkNew
init|=
operator|new
name|Path
argument_list|(
name|testBaseDir2
argument_list|()
operator|+
literal|"/linkToFile"
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|delete
argument_list|(
name|dirNew
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createAndWriteFile
argument_list|(
name|fileQual
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setWorkingDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// Link target is partially qualified, we get the same back.
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|fileQual
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileQual
argument_list|,
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|link
argument_list|)
operator|.
name|getSymlink
argument_list|()
argument_list|)
expr_stmt|;
comment|// Because the target was specified with an absolute path the
comment|// link fails to resolve after moving the parent directory.
name|wrapper
operator|.
name|rename
argument_list|(
name|dir
argument_list|,
name|dirNew
argument_list|)
expr_stmt|;
comment|// The target is still the old path
name|assertEquals
argument_list|(
name|fileQual
argument_list|,
name|wrapper
operator|.
name|getFileLinkStatus
argument_list|(
name|linkNew
argument_list|)
operator|.
name|getSymlink
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|readFile
argument_list|(
name|linkNew
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The link should be dangling now."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|x
parameter_list|)
block|{
comment|// Expected.
block|}
comment|// RawLocalFs only maintains the path part, not the URI, and
comment|// therefore does not support links to other file systems.
name|Path
name|anotherFs
init|=
operator|new
name|Path
argument_list|(
literal|"hdfs://host:1000/dir/file"
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|linkNew
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|wrapper
operator|.
name|createSymlink
argument_list|(
name|anotherFs
argument_list|,
name|linkNew
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Created a local fs link to a non-local fs"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|x
parameter_list|)
block|{
comment|// Excpected.
block|}
block|}
comment|/** Test create symlink to . */
annotation|@
name|Override
DECL|method|testCreateLinkToDot ()
specifier|public
name|void
name|testCreateLinkToDot
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|super
operator|.
name|testCreateLinkToDot
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// Expected.
block|}
block|}
block|}
end_class

end_unit

