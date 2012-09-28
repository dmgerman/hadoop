begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ListIterator
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
name|FileUtil
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
name|permission
operator|.
name|FsPermission
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
DECL|class|TestDirectoryCollection
specifier|public
class|class
name|TestDirectoryCollection
block|{
DECL|field|testDir
specifier|private
specifier|static
specifier|final
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestDirectoryCollection
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|testFile
specifier|private
specifier|static
specifier|final
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"testfile"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|testDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|testFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardown ()
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConcurrentAccess ()
specifier|public
name|void
name|testConcurrentAccess
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Initialize DirectoryCollection with a file instead of a directory
name|String
index|[]
name|dirs
init|=
block|{
name|testFile
operator|.
name|getPath
argument_list|()
block|}
decl_stmt|;
name|DirectoryCollection
name|dc
init|=
operator|new
name|DirectoryCollection
argument_list|(
name|dirs
argument_list|)
decl_stmt|;
comment|// Create an iterator before checkDirs is called to reliable test case
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|dc
operator|.
name|getGoodDirs
argument_list|()
decl_stmt|;
name|ListIterator
argument_list|<
name|String
argument_list|>
name|li
init|=
name|list
operator|.
name|listIterator
argument_list|()
decl_stmt|;
comment|// DiskErrorException will invalidate iterator of non-concurrent
comment|// collections. ConcurrentModificationException will be thrown upon next
comment|// use of the iterator.
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"checkDirs did not remove test file from directory list"
argument_list|,
name|dc
operator|.
name|checkDirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify no ConcurrentModification is thrown
name|li
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateDirectories ()
specifier|public
name|void
name|testCreateDirectories
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
name|FileContext
name|localFs
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|dirA
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"dirA"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|dirB
init|=
operator|new
name|File
argument_list|(
name|dirA
argument_list|,
literal|"dirB"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|dirC
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"dirC"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|Path
name|pathC
init|=
operator|new
name|Path
argument_list|(
name|dirC
argument_list|)
decl_stmt|;
name|FsPermission
name|permDirC
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0710
argument_list|)
decl_stmt|;
name|localFs
operator|.
name|mkdir
argument_list|(
name|pathC
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|setPermission
argument_list|(
name|pathC
argument_list|,
name|permDirC
argument_list|)
expr_stmt|;
name|String
index|[]
name|dirs
init|=
block|{
name|dirA
block|,
name|dirB
block|,
name|dirC
block|}
decl_stmt|;
name|DirectoryCollection
name|dc
init|=
operator|new
name|DirectoryCollection
argument_list|(
name|dirs
argument_list|)
decl_stmt|;
name|FsPermission
name|defaultPerm
init|=
name|FsPermission
operator|.
name|getDefault
argument_list|()
operator|.
name|applyUMask
argument_list|(
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
name|FsPermission
operator|.
name|DEFAULT_UMASK
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|createResult
init|=
name|dc
operator|.
name|createNonExistentDirs
argument_list|(
name|localFs
argument_list|,
name|defaultPerm
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|createResult
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|localFs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|dirA
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"local dir parent not created with proper permissions"
argument_list|,
name|defaultPerm
argument_list|,
name|status
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|=
name|localFs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|dirB
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"local dir not created with proper permissions"
argument_list|,
name|defaultPerm
argument_list|,
name|status
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|=
name|localFs
operator|.
name|getFileStatus
argument_list|(
name|pathC
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"existing local directory permissions modified"
argument_list|,
name|permDirC
argument_list|,
name|status
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

