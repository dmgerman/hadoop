begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
package|;
end_package

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
name|util
operator|.
name|EnumSet
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
name|AbstractFileSystem
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
name|CreateFlag
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
name|FileContext
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
name|FileContextTestHelper
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
name|FsConstants
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
name|viewfs
operator|.
name|ChRootedFs
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestChRootedFs
specifier|public
class|class
name|TestChRootedFs
block|{
DECL|field|fileContextTestHelper
name|FileContextTestHelper
name|fileContextTestHelper
init|=
operator|new
name|FileContextTestHelper
argument_list|()
decl_stmt|;
DECL|field|fc
name|FileContext
name|fc
decl_stmt|;
comment|// The ChRoootedFs
DECL|field|fcTarget
name|FileContext
name|fcTarget
decl_stmt|;
comment|//
DECL|field|chrootedTo
name|Path
name|chrootedTo
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
comment|// create the test root on local_fs
name|fcTarget
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
expr_stmt|;
name|chrootedTo
operator|=
name|fileContextTestHelper
operator|.
name|getAbsoluteTestRootPath
argument_list|(
name|fcTarget
argument_list|)
expr_stmt|;
comment|// In case previous test was killed before cleanup
name|fcTarget
operator|.
name|delete
argument_list|(
name|chrootedTo
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fcTarget
operator|.
name|mkdir
argument_list|(
name|chrootedTo
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// ChRoot to the root of the testDirectory
name|fc
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
operator|new
name|ChRootedFs
argument_list|(
name|fcTarget
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|,
name|chrootedTo
argument_list|)
argument_list|,
name|conf
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
name|fcTarget
operator|.
name|delete
argument_list|(
name|chrootedTo
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicPaths ()
specifier|public
name|void
name|testBasicPaths
parameter_list|()
block|{
name|URI
name|uri
init|=
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|chrootedTo
operator|.
name|toUri
argument_list|()
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fc
operator|.
name|makeQualified
argument_list|(
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
argument_list|)
argument_list|,
name|fc
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fc
operator|.
name|makeQualified
argument_list|(
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
argument_list|)
argument_list|,
name|fc
operator|.
name|getHomeDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|/*      * ChRootedFs as its uri like file:///chrootRoot.      * This is questionable since path.makequalified(uri, path) ignores      * the pathPart of a uri. So our notion of chrooted URI is questionable.      * But if we were to fix Path#makeQualified() then  the next test should      *  have been:      Assert.assertEquals(         new Path(chrootedTo + "/foo/bar").makeQualified(             FsConstants.LOCAL_FS_URI, null),         fc.makeQualified(new Path( "/foo/bar")));     */
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/foo/bar"
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|FsConstants
operator|.
name|LOCAL_FS_URI
argument_list|,
literal|null
argument_list|)
argument_list|,
name|fc
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test modify operations (create, mkdir, delete, etc)     *     * Verify the operation via chrootedfs (ie fc) and *also* via the    *  target file system (ie fclocal) that has been chrooted.    */
annotation|@
name|Test
DECL|method|testCreateDelete ()
specifier|public
name|void
name|testCreateDelete
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Create file
name|fileContextTestHelper
operator|.
name|createFileNonRecursive
argument_list|(
name|fc
argument_list|,
literal|"/foo"
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
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isFile
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create file with recursive dir
name|fileContextTestHelper
operator|.
name|createFile
argument_list|(
name|fc
argument_list|,
literal|"/newDir/foo"
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
operator|new
name|Path
argument_list|(
literal|"/newDir/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isFile
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"newDir/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Delete the created file
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fc
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/newDir/foo"
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fc
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/newDir/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"newDir/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create file with a 2 component dirs recursively
name|fileContextTestHelper
operator|.
name|createFile
argument_list|(
name|fc
argument_list|,
literal|"/newDir/newDir2/foo"
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
operator|new
name|Path
argument_list|(
literal|"/newDir/newDir2/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isFile
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"newDir/newDir2/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Delete the created file
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fc
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/newDir/newDir2/foo"
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fc
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/newDir/newDir2/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"newDir/newDir2/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirDelete ()
specifier|public
name|void
name|testMkdirDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|fc
operator|.
name|mkdir
argument_list|(
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"/dirX"
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|false
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
operator|new
name|Path
argument_list|(
literal|"/dirX"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isDir
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"dirX"
argument_list|)
argument_list|)
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
argument_list|,
literal|"/dirX/dirY"
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|false
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
operator|new
name|Path
argument_list|(
literal|"/dirX/dirY"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isDir
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"dirX/dirY"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Delete the created dir
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fc
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dirX/dirY"
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fc
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/dirX/dirY"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"dirX/dirY"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fc
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dirX"
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fc
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/dirX"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"dirX"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRename ()
specifier|public
name|void
name|testRename
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Rename a file
name|fileContextTestHelper
operator|.
name|createFile
argument_list|(
name|fc
argument_list|,
literal|"/newDir/foo"
argument_list|)
expr_stmt|;
name|fc
operator|.
name|rename
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/newDir/foo"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/newDir/fooBar"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fc
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/newDir/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"newDir/foo"
argument_list|)
argument_list|)
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
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"/newDir/fooBar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isFile
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"newDir/fooBar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Rename a dir
name|fc
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/newDir/dirFoo"
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fc
operator|.
name|rename
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/newDir/dirFoo"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/newDir/dirFooBar"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fc
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/newDir/dirFoo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"newDir/dirFoo"
argument_list|)
argument_list|)
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
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"/newDir/dirFooBar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isDir
argument_list|(
name|fcTarget
argument_list|,
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"newDir/dirFooBar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * We would have liked renames across file system to fail but     * Unfortunately there is not way to distinguish the two file systems     * @throws IOException    */
annotation|@
name|Test
DECL|method|testRenameAcrossFs ()
specifier|public
name|void
name|testRenameAcrossFs
parameter_list|()
throws|throws
name|IOException
block|{
name|fc
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/newDir/dirFoo"
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// the root will get interpreted to the root of the chrooted fs.
name|fc
operator|.
name|rename
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/newDir/dirFoo"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///dirFooBar"
argument_list|)
argument_list|)
expr_stmt|;
name|FileContextTestHelper
operator|.
name|isDir
argument_list|(
name|fc
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/dirFooBar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testList ()
specifier|public
name|void
name|testList
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
name|fs
init|=
name|fc
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|//  should return the full path not the chrooted path
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fs
operator|.
name|getPath
argument_list|()
argument_list|,
name|chrootedTo
argument_list|)
expr_stmt|;
comment|// list on Slash
name|FileStatus
index|[]
name|dirPaths
init|=
name|fc
operator|.
name|util
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dirPaths
operator|.
name|length
argument_list|)
expr_stmt|;
name|fileContextTestHelper
operator|.
name|createFileNonRecursive
argument_list|(
name|fc
argument_list|,
literal|"/foo"
argument_list|)
expr_stmt|;
name|fileContextTestHelper
operator|.
name|createFileNonRecursive
argument_list|(
name|fc
argument_list|,
literal|"/bar"
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dirX"
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|false
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
argument_list|,
literal|"/dirY"
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dirX/dirXX"
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dirPaths
operator|=
name|fc
operator|.
name|util
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|dirPaths
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Note the the file status paths are the full paths on target
name|fs
operator|=
name|fileContextTestHelper
operator|.
name|containsPath
argument_list|(
name|fcTarget
argument_list|,
literal|"foo"
argument_list|,
name|dirPaths
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|=
name|fileContextTestHelper
operator|.
name|containsPath
argument_list|(
name|fcTarget
argument_list|,
literal|"bar"
argument_list|,
name|dirPaths
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|=
name|fileContextTestHelper
operator|.
name|containsPath
argument_list|(
name|fcTarget
argument_list|,
literal|"dirX"
argument_list|,
name|dirPaths
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|=
name|fileContextTestHelper
operator|.
name|containsPath
argument_list|(
name|fcTarget
argument_list|,
literal|"dirY"
argument_list|,
name|dirPaths
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWorkingDirectory ()
specifier|public
name|void
name|testWorkingDirectory
parameter_list|()
throws|throws
name|Exception
block|{
comment|// First we cd to our test root
name|fc
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testWd"
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Path
name|workDir
init|=
operator|new
name|Path
argument_list|(
literal|"/testWd"
argument_list|)
decl_stmt|;
name|Path
name|fqWd
init|=
name|fc
operator|.
name|makeQualified
argument_list|(
name|workDir
argument_list|)
decl_stmt|;
name|fc
operator|.
name|setWorkingDirectory
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fqWd
argument_list|,
name|fc
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|fc
operator|.
name|setWorkingDirectory
argument_list|(
operator|new
name|Path
argument_list|(
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fqWd
argument_list|,
name|fc
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|fc
operator|.
name|setWorkingDirectory
argument_list|(
operator|new
name|Path
argument_list|(
literal|".."
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fqWd
operator|.
name|getParent
argument_list|()
argument_list|,
name|fc
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// cd using a relative path
comment|// Go back to our test root
name|workDir
operator|=
operator|new
name|Path
argument_list|(
literal|"/testWd"
argument_list|)
expr_stmt|;
name|fqWd
operator|=
name|fc
operator|.
name|makeQualified
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
name|fc
operator|.
name|setWorkingDirectory
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fqWd
argument_list|,
name|fc
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|relativeDir
init|=
operator|new
name|Path
argument_list|(
literal|"existingDir1"
argument_list|)
decl_stmt|;
name|Path
name|absoluteDir
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"existingDir1"
argument_list|)
decl_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|absoluteDir
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Path
name|fqAbsoluteDir
init|=
name|fc
operator|.
name|makeQualified
argument_list|(
name|absoluteDir
argument_list|)
decl_stmt|;
name|fc
operator|.
name|setWorkingDirectory
argument_list|(
name|relativeDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fqAbsoluteDir
argument_list|,
name|fc
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// cd using a absolute path
name|absoluteDir
operator|=
operator|new
name|Path
argument_list|(
literal|"/test/existingDir2"
argument_list|)
expr_stmt|;
name|fqAbsoluteDir
operator|=
name|fc
operator|.
name|makeQualified
argument_list|(
name|absoluteDir
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|absoluteDir
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fc
operator|.
name|setWorkingDirectory
argument_list|(
name|absoluteDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fqAbsoluteDir
argument_list|,
name|fc
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now open a file relative to the wd we just set above.
name|Path
name|absolutePath
init|=
operator|new
name|Path
argument_list|(
name|absoluteDir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|fc
operator|.
name|create
argument_list|(
name|absolutePath
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fc
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now mkdir relative to the dir we cd'ed to
name|fc
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"newDir"
argument_list|)
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
operator|new
name|Path
argument_list|(
name|absoluteDir
argument_list|,
literal|"newDir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|absoluteDir
operator|=
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"nonexistingPath"
argument_list|)
expr_stmt|;
try|try
block|{
name|fc
operator|.
name|setWorkingDirectory
argument_list|(
name|absoluteDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"cd to non existing dir should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Exception as expected
block|}
comment|// Try a URI
specifier|final
name|String
name|LOCAL_FS_ROOT_URI
init|=
literal|"file:///tmp/test"
decl_stmt|;
name|absoluteDir
operator|=
operator|new
name|Path
argument_list|(
name|LOCAL_FS_ROOT_URI
operator|+
literal|"/existingDir"
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|absoluteDir
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fc
operator|.
name|setWorkingDirectory
argument_list|(
name|absoluteDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|absoluteDir
argument_list|,
name|fc
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test resolvePath(p)     */
annotation|@
name|Test
DECL|method|testResolvePath ()
specifier|public
name|void
name|testResolvePath
parameter_list|()
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|chrootedTo
argument_list|,
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|resolvePath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fileContextTestHelper
operator|.
name|createFile
argument_list|(
name|fc
argument_list|,
literal|"/foo"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
name|chrootedTo
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|resolvePath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testResolvePathNonExisting ()
specifier|public
name|void
name|testResolvePathNonExisting
parameter_list|()
throws|throws
name|IOException
block|{
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|resolvePath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/nonExisting"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsValidNameValidInBaseFs ()
specifier|public
name|void
name|testIsValidNameValidInBaseFs
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractFileSystem
name|baseFs
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|)
decl_stmt|;
name|ChRootedFs
name|chRootedFs
init|=
operator|new
name|ChRootedFs
argument_list|(
name|baseFs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/chroot"
argument_list|)
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|baseFs
argument_list|)
operator|.
name|isValidName
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|chRootedFs
operator|.
name|isValidName
argument_list|(
literal|"/test"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|baseFs
argument_list|)
operator|.
name|isValidName
argument_list|(
literal|"/chroot/test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsValidNameInvalidInBaseFs ()
specifier|public
name|void
name|testIsValidNameInvalidInBaseFs
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractFileSystem
name|baseFs
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|)
decl_stmt|;
name|ChRootedFs
name|chRootedFs
init|=
operator|new
name|ChRootedFs
argument_list|(
name|baseFs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/chroot"
argument_list|)
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|when
argument_list|(
name|baseFs
argument_list|)
operator|.
name|isValidName
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|chRootedFs
operator|.
name|isValidName
argument_list|(
literal|"/test"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|baseFs
argument_list|)
operator|.
name|isValidName
argument_list|(
literal|"/chroot/test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testCreateSnapshot ()
specifier|public
name|void
name|testCreateSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|snapRootPath
init|=
operator|new
name|Path
argument_list|(
literal|"/snapPath"
argument_list|)
decl_stmt|;
name|Path
name|chRootedSnapRootPath
init|=
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|getPathWithoutSchemeAndAuthority
argument_list|(
name|chrootedTo
argument_list|)
argument_list|,
literal|"snapPath"
argument_list|)
decl_stmt|;
name|AbstractFileSystem
name|baseFs
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|)
decl_stmt|;
name|ChRootedFs
name|chRootedFs
init|=
operator|new
name|ChRootedFs
argument_list|(
name|baseFs
argument_list|,
name|chrootedTo
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|snapRootPath
argument_list|)
operator|.
name|when
argument_list|(
name|baseFs
argument_list|)
operator|.
name|createSnapshot
argument_list|(
name|chRootedSnapRootPath
argument_list|,
literal|"snap1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|snapRootPath
argument_list|,
name|chRootedFs
operator|.
name|createSnapshot
argument_list|(
name|snapRootPath
argument_list|,
literal|"snap1"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|baseFs
argument_list|)
operator|.
name|createSnapshot
argument_list|(
name|chRootedSnapRootPath
argument_list|,
literal|"snap1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testDeleteSnapshot ()
specifier|public
name|void
name|testDeleteSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|snapRootPath
init|=
operator|new
name|Path
argument_list|(
literal|"/snapPath"
argument_list|)
decl_stmt|;
name|Path
name|chRootedSnapRootPath
init|=
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|getPathWithoutSchemeAndAuthority
argument_list|(
name|chrootedTo
argument_list|)
argument_list|,
literal|"snapPath"
argument_list|)
decl_stmt|;
name|AbstractFileSystem
name|baseFs
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|)
decl_stmt|;
name|ChRootedFs
name|chRootedFs
init|=
operator|new
name|ChRootedFs
argument_list|(
name|baseFs
argument_list|,
name|chrootedTo
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|baseFs
argument_list|)
operator|.
name|deleteSnapshot
argument_list|(
name|chRootedSnapRootPath
argument_list|,
literal|"snap1"
argument_list|)
expr_stmt|;
name|chRootedFs
operator|.
name|deleteSnapshot
argument_list|(
name|snapRootPath
argument_list|,
literal|"snap1"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|baseFs
argument_list|)
operator|.
name|deleteSnapshot
argument_list|(
name|chRootedSnapRootPath
argument_list|,
literal|"snap1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testRenameSnapshot ()
specifier|public
name|void
name|testRenameSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|snapRootPath
init|=
operator|new
name|Path
argument_list|(
literal|"/snapPath"
argument_list|)
decl_stmt|;
name|Path
name|chRootedSnapRootPath
init|=
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|getPathWithoutSchemeAndAuthority
argument_list|(
name|chrootedTo
argument_list|)
argument_list|,
literal|"snapPath"
argument_list|)
decl_stmt|;
name|AbstractFileSystem
name|baseFs
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|)
decl_stmt|;
name|ChRootedFs
name|chRootedFs
init|=
operator|new
name|ChRootedFs
argument_list|(
name|baseFs
argument_list|,
name|chrootedTo
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|baseFs
argument_list|)
operator|.
name|renameSnapshot
argument_list|(
name|chRootedSnapRootPath
argument_list|,
literal|"snapOldName"
argument_list|,
literal|"snapNewName"
argument_list|)
expr_stmt|;
name|chRootedFs
operator|.
name|renameSnapshot
argument_list|(
name|snapRootPath
argument_list|,
literal|"snapOldName"
argument_list|,
literal|"snapNewName"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|baseFs
argument_list|)
operator|.
name|renameSnapshot
argument_list|(
name|chRootedSnapRootPath
argument_list|,
literal|"snapOldName"
argument_list|,
literal|"snapNewName"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

