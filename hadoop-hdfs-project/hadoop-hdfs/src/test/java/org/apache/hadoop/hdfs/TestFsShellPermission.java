begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
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
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|lang3
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
name|FileSystemTestHelper
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
name|FsShell
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
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
comment|/**  * This test covers privilege related aspects of FsShell  *  */
end_comment

begin_class
DECL|class|TestFsShellPermission
specifier|public
class|class
name|TestFsShellPermission
block|{
DECL|field|TEST_ROOT
specifier|static
specifier|private
specifier|final
name|String
name|TEST_ROOT
init|=
literal|"/testroot"
decl_stmt|;
DECL|method|createUGI (String ownername, String groupName)
specifier|static
name|UserGroupInformation
name|createUGI
parameter_list|(
name|String
name|ownername
parameter_list|,
name|String
name|groupName
parameter_list|)
block|{
return|return
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|ownername
argument_list|,
operator|new
name|String
index|[]
block|{
name|groupName
block|}
argument_list|)
return|;
block|}
DECL|class|FileEntry
specifier|private
class|class
name|FileEntry
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|isDir
specifier|private
name|boolean
name|isDir
decl_stmt|;
DECL|field|owner
specifier|private
name|String
name|owner
decl_stmt|;
DECL|field|group
specifier|private
name|String
name|group
decl_stmt|;
DECL|field|permission
specifier|private
name|String
name|permission
decl_stmt|;
DECL|method|FileEntry (String path, boolean isDir, String owner, String group, String permission)
specifier|public
name|FileEntry
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|isDir
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|String
name|permission
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|isDir
operator|=
name|isDir
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
block|}
DECL|method|getPath ()
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|isDirectory ()
name|boolean
name|isDirectory
parameter_list|()
block|{
return|return
name|isDir
return|;
block|}
DECL|method|getOwner ()
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
DECL|method|getGroup ()
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
DECL|method|getPermission ()
name|String
name|getPermission
parameter_list|()
block|{
return|return
name|permission
return|;
block|}
block|}
DECL|method|createFiles (FileSystem fs, String topdir, FileEntry[] entries)
specifier|private
name|void
name|createFiles
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|topdir
parameter_list|,
name|FileEntry
index|[]
name|entries
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|FileEntry
name|entry
range|:
name|entries
control|)
block|{
name|String
name|newPathStr
init|=
name|topdir
operator|+
literal|"/"
operator|+
name|entry
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|Path
name|newPath
init|=
operator|new
name|Path
argument_list|(
name|newPathStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|newPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileSystemTestHelper
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|newPath
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|setPermission
argument_list|(
name|newPath
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|entry
operator|.
name|getPermission
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|newPath
argument_list|,
name|entry
operator|.
name|getOwner
argument_list|()
argument_list|,
name|entry
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** delete directory and everything underneath it.*/
DECL|method|deldir (FileSystem fs, String topdir)
specifier|private
specifier|static
name|void
name|deldir
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|topdir
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|topdir
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|execCmd (FsShell shell, final String[] args)
specifier|static
name|String
name|execCmd
parameter_list|(
name|FsShell
name|shell
parameter_list|,
specifier|final
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|baout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|baout
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintStream
name|old
init|=
name|System
operator|.
name|out
decl_stmt|;
name|int
name|ret
decl_stmt|;
try|try
block|{
name|System
operator|.
name|setOut
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|ret
operator|=
name|shell
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|ret
argument_list|)
return|;
block|}
comment|/*    * Each instance of TestDeleteHelper captures one testing scenario.    *    * To create all files listed in fileEntries, and then delete as user    * doAsuser the deleteEntry with command+options specified in cmdAndOptions.    *    * When expectedToDelete is true, the deleteEntry is expected to be deleted;    * otherwise, it's not expected to be deleted. At the end of test,    * the existence of deleteEntry is checked against expectedToDelete    * to ensure the command is finished with expected result    */
DECL|class|TestDeleteHelper
specifier|private
class|class
name|TestDeleteHelper
block|{
DECL|field|fileEntries
specifier|private
name|FileEntry
index|[]
name|fileEntries
decl_stmt|;
DECL|field|deleteEntry
specifier|private
name|FileEntry
name|deleteEntry
decl_stmt|;
DECL|field|cmdAndOptions
specifier|private
name|String
name|cmdAndOptions
decl_stmt|;
DECL|field|expectedToDelete
specifier|private
name|boolean
name|expectedToDelete
decl_stmt|;
DECL|field|doAsGroup
specifier|final
name|String
name|doAsGroup
decl_stmt|;
DECL|field|userUgi
specifier|final
name|UserGroupInformation
name|userUgi
decl_stmt|;
DECL|method|TestDeleteHelper ( FileEntry[] fileEntries, FileEntry deleteEntry, String cmdAndOptions, String doAsUser, boolean expectedToDelete)
specifier|public
name|TestDeleteHelper
parameter_list|(
name|FileEntry
index|[]
name|fileEntries
parameter_list|,
name|FileEntry
name|deleteEntry
parameter_list|,
name|String
name|cmdAndOptions
parameter_list|,
name|String
name|doAsUser
parameter_list|,
name|boolean
name|expectedToDelete
parameter_list|)
block|{
name|this
operator|.
name|fileEntries
operator|=
name|fileEntries
expr_stmt|;
name|this
operator|.
name|deleteEntry
operator|=
name|deleteEntry
expr_stmt|;
name|this
operator|.
name|cmdAndOptions
operator|=
name|cmdAndOptions
expr_stmt|;
name|this
operator|.
name|expectedToDelete
operator|=
name|expectedToDelete
expr_stmt|;
name|doAsGroup
operator|=
name|doAsUser
operator|.
name|equals
argument_list|(
literal|"hdfs"
argument_list|)
condition|?
literal|"supergroup"
else|:
literal|"users"
expr_stmt|;
name|userUgi
operator|=
name|createUGI
argument_list|(
name|doAsUser
argument_list|,
name|doAsGroup
argument_list|)
expr_stmt|;
block|}
DECL|method|execute (Configuration conf, FileSystem fs)
specifier|public
name|void
name|execute
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|Exception
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|createFiles
argument_list|(
name|fs
argument_list|,
name|TEST_ROOT
argument_list|,
name|fileEntries
argument_list|)
expr_stmt|;
specifier|final
name|FsShell
name|fsShell
init|=
operator|new
name|FsShell
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|String
name|deletePath
init|=
name|TEST_ROOT
operator|+
literal|"/"
operator|+
name|deleteEntry
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
index|[]
name|tmpCmdOpts
init|=
name|StringUtils
operator|.
name|split
argument_list|(
name|cmdAndOptions
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|tmpArray
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|tmpCmdOpts
argument_list|)
argument_list|)
decl_stmt|;
name|tmpArray
operator|.
name|add
argument_list|(
name|deletePath
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|cmdOpts
init|=
name|tmpArray
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|tmpArray
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|userUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|execCmd
argument_list|(
name|fsShell
argument_list|,
name|cmdOpts
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|boolean
name|deleted
init|=
operator|!
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|deletePath
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedToDelete
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
name|deldir
argument_list|(
name|fs
argument_list|,
name|TEST_ROOT
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|genDeleteEmptyDirHelper (final String cmdOpts, final String targetPerm, final String asUser, boolean expectedToDelete)
specifier|private
name|TestDeleteHelper
name|genDeleteEmptyDirHelper
parameter_list|(
specifier|final
name|String
name|cmdOpts
parameter_list|,
specifier|final
name|String
name|targetPerm
parameter_list|,
specifier|final
name|String
name|asUser
parameter_list|,
name|boolean
name|expectedToDelete
parameter_list|)
block|{
name|FileEntry
index|[]
name|files
init|=
block|{
operator|new
name|FileEntry
argument_list|(
literal|"userA"
argument_list|,
literal|true
argument_list|,
literal|"userA"
argument_list|,
literal|"users"
argument_list|,
literal|"755"
argument_list|)
block|,
operator|new
name|FileEntry
argument_list|(
literal|"userA/userB"
argument_list|,
literal|true
argument_list|,
literal|"userB"
argument_list|,
literal|"users"
argument_list|,
name|targetPerm
argument_list|)
block|}
decl_stmt|;
name|FileEntry
name|deleteEntry
init|=
name|files
index|[
literal|1
index|]
decl_stmt|;
return|return
operator|new
name|TestDeleteHelper
argument_list|(
name|files
argument_list|,
name|deleteEntry
argument_list|,
name|cmdOpts
argument_list|,
name|asUser
argument_list|,
name|expectedToDelete
argument_list|)
return|;
block|}
comment|// Expect target to be deleted
DECL|method|genRmrEmptyDirWithReadPerm ()
specifier|private
name|TestDeleteHelper
name|genRmrEmptyDirWithReadPerm
parameter_list|()
block|{
return|return
name|genDeleteEmptyDirHelper
argument_list|(
literal|"-rm -r"
argument_list|,
literal|"744"
argument_list|,
literal|"userA"
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|// Expect target to be deleted
DECL|method|genRmrEmptyDirWithNoPerm ()
specifier|private
name|TestDeleteHelper
name|genRmrEmptyDirWithNoPerm
parameter_list|()
block|{
return|return
name|genDeleteEmptyDirHelper
argument_list|(
literal|"-rm -r"
argument_list|,
literal|"700"
argument_list|,
literal|"userA"
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|// Expect target to be deleted
DECL|method|genRmrfEmptyDirWithNoPerm ()
specifier|private
name|TestDeleteHelper
name|genRmrfEmptyDirWithNoPerm
parameter_list|()
block|{
return|return
name|genDeleteEmptyDirHelper
argument_list|(
literal|"-rm -r -f"
argument_list|,
literal|"700"
argument_list|,
literal|"userA"
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|genDeleteNonEmptyDirHelper (final String cmd, final String targetPerm, final String asUser, boolean expectedToDelete)
specifier|private
name|TestDeleteHelper
name|genDeleteNonEmptyDirHelper
parameter_list|(
specifier|final
name|String
name|cmd
parameter_list|,
specifier|final
name|String
name|targetPerm
parameter_list|,
specifier|final
name|String
name|asUser
parameter_list|,
name|boolean
name|expectedToDelete
parameter_list|)
block|{
name|FileEntry
index|[]
name|files
init|=
block|{
operator|new
name|FileEntry
argument_list|(
literal|"userA"
argument_list|,
literal|true
argument_list|,
literal|"userA"
argument_list|,
literal|"users"
argument_list|,
literal|"755"
argument_list|)
block|,
operator|new
name|FileEntry
argument_list|(
literal|"userA/userB"
argument_list|,
literal|true
argument_list|,
literal|"userB"
argument_list|,
literal|"users"
argument_list|,
name|targetPerm
argument_list|)
block|,
operator|new
name|FileEntry
argument_list|(
literal|"userA/userB/xyzfile"
argument_list|,
literal|false
argument_list|,
literal|"userB"
argument_list|,
literal|"users"
argument_list|,
name|targetPerm
argument_list|)
block|}
decl_stmt|;
name|FileEntry
name|deleteEntry
init|=
name|files
index|[
literal|1
index|]
decl_stmt|;
return|return
operator|new
name|TestDeleteHelper
argument_list|(
name|files
argument_list|,
name|deleteEntry
argument_list|,
name|cmd
argument_list|,
name|asUser
argument_list|,
name|expectedToDelete
argument_list|)
return|;
block|}
comment|// Expect target not to be deleted
DECL|method|genRmrNonEmptyDirWithReadPerm ()
specifier|private
name|TestDeleteHelper
name|genRmrNonEmptyDirWithReadPerm
parameter_list|()
block|{
return|return
name|genDeleteNonEmptyDirHelper
argument_list|(
literal|"-rm -r"
argument_list|,
literal|"744"
argument_list|,
literal|"userA"
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|// Expect target not to be deleted
DECL|method|genRmrNonEmptyDirWithNoPerm ()
specifier|private
name|TestDeleteHelper
name|genRmrNonEmptyDirWithNoPerm
parameter_list|()
block|{
return|return
name|genDeleteNonEmptyDirHelper
argument_list|(
literal|"-rm -r"
argument_list|,
literal|"700"
argument_list|,
literal|"userA"
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|// Expect target to be deleted
DECL|method|genRmrNonEmptyDirWithAllPerm ()
specifier|private
name|TestDeleteHelper
name|genRmrNonEmptyDirWithAllPerm
parameter_list|()
block|{
return|return
name|genDeleteNonEmptyDirHelper
argument_list|(
literal|"-rm -r"
argument_list|,
literal|"777"
argument_list|,
literal|"userA"
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|// Expect target not to be deleted
DECL|method|genRmrfNonEmptyDirWithNoPerm ()
specifier|private
name|TestDeleteHelper
name|genRmrfNonEmptyDirWithNoPerm
parameter_list|()
block|{
return|return
name|genDeleteNonEmptyDirHelper
argument_list|(
literal|"-rm -r -f"
argument_list|,
literal|"700"
argument_list|,
literal|"userA"
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|// Expect target to be deleted
DECL|method|genDeleteSingleFileNotAsOwner ()
specifier|public
name|TestDeleteHelper
name|genDeleteSingleFileNotAsOwner
parameter_list|()
throws|throws
name|Exception
block|{
name|FileEntry
index|[]
name|files
init|=
block|{
operator|new
name|FileEntry
argument_list|(
literal|"userA"
argument_list|,
literal|true
argument_list|,
literal|"userA"
argument_list|,
literal|"users"
argument_list|,
literal|"755"
argument_list|)
block|,
operator|new
name|FileEntry
argument_list|(
literal|"userA/userB"
argument_list|,
literal|false
argument_list|,
literal|"userB"
argument_list|,
literal|"users"
argument_list|,
literal|"700"
argument_list|)
block|}
decl_stmt|;
name|FileEntry
name|deleteEntry
init|=
name|files
index|[
literal|1
index|]
decl_stmt|;
return|return
operator|new
name|TestDeleteHelper
argument_list|(
name|files
argument_list|,
name|deleteEntry
argument_list|,
literal|"-rm -r"
argument_list|,
literal|"userA"
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testDelete ()
specifier|public
name|void
name|testDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|String
name|nnUri
init|=
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|conf
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|nnUri
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|TestDeleteHelper
argument_list|>
name|ta
init|=
operator|new
name|ArrayList
argument_list|<
name|TestDeleteHelper
argument_list|>
argument_list|()
decl_stmt|;
comment|// Add empty dir tests
name|ta
operator|.
name|add
argument_list|(
name|genRmrEmptyDirWithReadPerm
argument_list|()
argument_list|)
expr_stmt|;
name|ta
operator|.
name|add
argument_list|(
name|genRmrEmptyDirWithNoPerm
argument_list|()
argument_list|)
expr_stmt|;
name|ta
operator|.
name|add
argument_list|(
name|genRmrfEmptyDirWithNoPerm
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add non-empty dir tests
name|ta
operator|.
name|add
argument_list|(
name|genRmrNonEmptyDirWithReadPerm
argument_list|()
argument_list|)
expr_stmt|;
name|ta
operator|.
name|add
argument_list|(
name|genRmrNonEmptyDirWithNoPerm
argument_list|()
argument_list|)
expr_stmt|;
name|ta
operator|.
name|add
argument_list|(
name|genRmrNonEmptyDirWithAllPerm
argument_list|()
argument_list|)
expr_stmt|;
name|ta
operator|.
name|add
argument_list|(
name|genRmrfNonEmptyDirWithNoPerm
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add single tile test
name|ta
operator|.
name|add
argument_list|(
name|genDeleteSingleFileNotAsOwner
argument_list|()
argument_list|)
expr_stmt|;
comment|// Run all tests
for|for
control|(
name|TestDeleteHelper
name|t
range|:
name|ta
control|)
block|{
name|t
operator|.
name|execute
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

