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
name|permission
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
name|test
operator|.
name|GenericTestUtils
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
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
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
name|assertThat
import|;
end_import

begin_comment
comment|/**  * This class tests the local file system via the FileSystem abstraction.  */
end_comment

begin_class
DECL|class|TestLocalFileSystemPermission
specifier|public
class|class
name|TestLocalFileSystemPermission
extends|extends
name|TestCase
block|{
DECL|field|TEST_PATH_PREFIX
specifier|static
specifier|final
name|String
name|TEST_PATH_PREFIX
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestLocalFileSystemPermission
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FileSystem
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
block|}
DECL|method|writeFile (FileSystem fs, String name)
specifier|private
name|Path
name|writeFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH_PREFIX
operator|+
name|name
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stm
init|=
name|fs
operator|.
name|create
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|stm
operator|.
name|writeBytes
argument_list|(
literal|"42\n"
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|f
return|;
block|}
DECL|method|writeFile (FileSystem fs, String name, FsPermission perm)
specifier|private
name|Path
name|writeFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|name
parameter_list|,
name|FsPermission
name|perm
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH_PREFIX
operator|+
name|name
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stm
init|=
name|fs
operator|.
name|create
argument_list|(
name|f
argument_list|,
name|perm
argument_list|,
literal|true
argument_list|,
literal|2048
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|32
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|stm
operator|.
name|writeBytes
argument_list|(
literal|"42\n"
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|f
return|;
block|}
DECL|method|cleanup (FileSystem fs, Path name)
specifier|private
name|void
name|cleanup
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
DECL|method|testLocalFSDirsetPermission ()
specifier|public
name|void
name|testLocalFSDirsetPermission
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|Path
operator|.
name|WINDOWS
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test for Windows"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LocalFileSystem
name|localfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
name|localfs
operator|.
name|getConf
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
literal|"044"
argument_list|)
expr_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH_PREFIX
operator|+
literal|"dir"
argument_list|)
decl_stmt|;
name|localfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
try|try
block|{
name|FsPermission
name|initialPermission
init|=
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
operator|.
name|applyUMask
argument_list|(
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|,
name|initialPermission
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|FsPermission
name|perm
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
decl_stmt|;
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH_PREFIX
operator|+
literal|"dir1"
argument_list|)
decl_stmt|;
name|localfs
operator|.
name|mkdirs
argument_list|(
name|dir1
argument_list|,
name|perm
argument_list|)
expr_stmt|;
try|try
block|{
name|FsPermission
name|initialPermission
init|=
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|dir1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|perm
operator|.
name|applyUMask
argument_list|(
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|,
name|initialPermission
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|Path
name|dir2
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH_PREFIX
operator|+
literal|"dir2"
argument_list|)
decl_stmt|;
name|localfs
operator|.
name|mkdirs
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
try|try
block|{
name|FsPermission
name|initialPermission
init|=
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|dir2
argument_list|)
decl_stmt|;
name|Path
name|copyPath
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH_PREFIX
operator|+
literal|"dir_copy"
argument_list|)
decl_stmt|;
name|localfs
operator|.
name|rename
argument_list|(
name|dir2
argument_list|,
name|copyPath
argument_list|)
expr_stmt|;
name|FsPermission
name|copyPermission
init|=
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|copyPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|copyPermission
argument_list|,
name|initialPermission
argument_list|)
expr_stmt|;
name|dir2
operator|=
name|copyPath
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test"
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
name|cleanup
argument_list|(
name|localfs
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|cleanup
argument_list|(
name|localfs
argument_list|,
name|dir1
argument_list|)
expr_stmt|;
if|if
condition|(
name|localfs
operator|.
name|exists
argument_list|(
name|dir2
argument_list|)
condition|)
block|{
name|localfs
operator|.
name|delete
argument_list|(
name|dir2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Test LocalFileSystem.setPermission */
DECL|method|testLocalFSsetPermission ()
specifier|public
name|void
name|testLocalFSsetPermission
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|Path
operator|.
name|WINDOWS
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test for Windows"
argument_list|)
expr_stmt|;
return|return;
block|}
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
literal|"044"
argument_list|)
expr_stmt|;
name|LocalFileSystem
name|localfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|filename
init|=
literal|"foo"
decl_stmt|;
name|Path
name|f
init|=
name|writeFile
argument_list|(
name|localfs
argument_list|,
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
name|FsPermission
name|initialPermission
init|=
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|f
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
operator|.
name|applyUMask
argument_list|(
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|,
name|initialPermission
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|filename1
init|=
literal|"foo1"
decl_stmt|;
name|FsPermission
name|perm
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
decl_stmt|;
name|Path
name|f1
init|=
name|writeFile
argument_list|(
name|localfs
argument_list|,
name|filename1
argument_list|,
name|perm
argument_list|)
decl_stmt|;
try|try
block|{
name|FsPermission
name|initialPermission
init|=
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|f1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|perm
operator|.
name|applyUMask
argument_list|(
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|,
name|initialPermission
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|filename2
init|=
literal|"foo2"
decl_stmt|;
name|Path
name|f2
init|=
name|writeFile
argument_list|(
name|localfs
argument_list|,
name|filename2
argument_list|)
decl_stmt|;
try|try
block|{
name|FsPermission
name|initialPermission
init|=
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|f2
argument_list|)
decl_stmt|;
name|Path
name|copyPath
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH_PREFIX
operator|+
literal|"/foo_copy"
argument_list|)
decl_stmt|;
name|localfs
operator|.
name|rename
argument_list|(
name|f2
argument_list|,
name|copyPath
argument_list|)
expr_stmt|;
name|FsPermission
name|copyPermission
init|=
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|copyPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|copyPermission
argument_list|,
name|initialPermission
argument_list|)
expr_stmt|;
name|f2
operator|=
name|copyPath
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
comment|// create files and manipulate them.
name|FsPermission
name|all
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
decl_stmt|;
name|FsPermission
name|none
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
decl_stmt|;
name|localfs
operator|.
name|setPermission
argument_list|(
name|f
argument_list|,
name|none
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|none
argument_list|,
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|localfs
operator|.
name|setPermission
argument_list|(
name|f
argument_list|,
name|all
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|all
argument_list|,
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cleanup
argument_list|(
name|localfs
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|cleanup
argument_list|(
name|localfs
argument_list|,
name|f1
argument_list|)
expr_stmt|;
if|if
condition|(
name|localfs
operator|.
name|exists
argument_list|(
name|f2
argument_list|)
condition|)
block|{
name|localfs
operator|.
name|delete
argument_list|(
name|f2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getPermission (LocalFileSystem fs, Path p)
name|FsPermission
name|getPermission
parameter_list|(
name|LocalFileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
operator|.
name|getPermission
argument_list|()
return|;
block|}
comment|/** Test LocalFileSystem.setOwner */
DECL|method|testLocalFSsetOwner ()
specifier|public
name|void
name|testLocalFSsetOwner
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|Path
operator|.
name|WINDOWS
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test for Windows"
argument_list|)
expr_stmt|;
return|return;
block|}
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
literal|"044"
argument_list|)
expr_stmt|;
name|LocalFileSystem
name|localfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|filename
init|=
literal|"bar"
decl_stmt|;
name|Path
name|f
init|=
name|writeFile
argument_list|(
name|localfs
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
literal|null
decl_stmt|;
try|try
block|{
name|groups
operator|=
name|getGroups
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|filename
operator|+
literal|": "
operator|+
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|groups
operator|==
literal|null
operator|||
name|groups
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test: need at least one group.  groups="
operator|+
name|groups
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// create files and manipulate them.
try|try
block|{
name|String
name|g0
init|=
name|groups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|localfs
operator|.
name|setOwner
argument_list|(
name|f
argument_list|,
literal|null
argument_list|,
name|g0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|g0
argument_list|,
name|getGroup
argument_list|(
name|localfs
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|groups
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|String
name|g1
init|=
name|groups
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|localfs
operator|.
name|setOwner
argument_list|(
name|f
argument_list|,
literal|null
argument_list|,
name|g1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|g1
argument_list|,
name|getGroup
argument_list|(
name|localfs
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Not testing changing the group since user "
operator|+
literal|"belongs to only one group."
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cleanup
argument_list|(
name|localfs
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Steps:    * 1. Create a directory with default permissions: 777 with umask 022    * 2. Check the directory has good permissions: 755    * 3. Set the umask to 062.    * 4. Create a new directory with default permissions.    * 5. For this directory we expect 715 as permission not 755    * @throws Exception we can throw away all the exception.    */
DECL|method|testSetUmaskInRealTime ()
specifier|public
name|void
name|testSetUmaskInRealTime
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|Path
operator|.
name|WINDOWS
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cannot run test for Windows"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LocalFileSystem
name|localfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
name|localfs
operator|.
name|getConf
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
literal|"022"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH_PREFIX
operator|+
literal|"dir"
argument_list|)
decl_stmt|;
name|Path
name|dir2
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH_PREFIX
operator|+
literal|"dir2"
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|localfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|FsPermission
name|initialPermission
init|=
name|getPermission
argument_list|(
name|localfs
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"With umask 022 permission should be 755 since the default "
operator|+
literal|"permission is 777"
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"755"
argument_list|)
argument_list|,
name|initialPermission
argument_list|)
expr_stmt|;
comment|// Modify umask and create a new directory
comment|// and check if new umask is applied
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_KEY
argument_list|,
literal|"062"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|localfs
operator|.
name|mkdirs
argument_list|(
name|dir2
argument_list|)
argument_list|)
expr_stmt|;
name|FsPermission
name|finalPermission
init|=
name|localfs
operator|.
name|getFileStatus
argument_list|(
name|dir2
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"With umask 062 permission should not be 755 since the "
operator|+
literal|"default permission is 777"
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"755"
argument_list|)
argument_list|,
name|is
argument_list|(
name|not
argument_list|(
name|finalPermission
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"With umask 062 we expect 715 since the default permission is 777"
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"715"
argument_list|)
argument_list|,
name|finalPermission
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_KEY
argument_list|,
literal|"022"
argument_list|)
expr_stmt|;
name|cleanup
argument_list|(
name|localfs
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|cleanup
argument_list|(
name|localfs
argument_list|,
name|dir2
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getGroups ()
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|a
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|getGroupsCommand
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|StringTokenizer
name|t
init|=
operator|new
name|StringTokenizer
argument_list|(
name|s
argument_list|)
init|;
name|t
operator|.
name|hasMoreTokens
argument_list|()
condition|;
control|)
block|{
name|a
operator|.
name|add
argument_list|(
name|t
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
DECL|method|getGroup (LocalFileSystem fs, Path p)
name|String
name|getGroup
parameter_list|(
name|LocalFileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
operator|.
name|getGroup
argument_list|()
return|;
block|}
block|}
end_class

end_unit

