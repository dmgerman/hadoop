begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.permission
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|DFSTestUtil
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
name|DistributedFileSystem
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
name|HdfsConfiguration
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

begin_class
DECL|class|TestStickyBit
specifier|public
class|class
name|TestStickyBit
block|{
DECL|field|user1
specifier|static
name|UserGroupInformation
name|user1
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"theDoctor"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tardis"
block|}
argument_list|)
decl_stmt|;
DECL|field|user2
specifier|static
name|UserGroupInformation
name|user2
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"rose"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"powellestates"
block|}
argument_list|)
decl_stmt|;
comment|/**    * Ensure that even if a file is in a directory with the sticky bit on,    * another user can write to that file (assuming correct permissions).    */
DECL|method|confirmCanAppend (Configuration conf, FileSystem hdfs, Path baseDir)
specifier|private
name|void
name|confirmCanAppend
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|hdfs
parameter_list|,
name|Path
name|baseDir
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Create a tmp directory with wide-open permissions and sticky bit
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|baseDir
argument_list|,
literal|"tmp"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|p
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|01777
argument_list|)
argument_list|)
expr_stmt|;
comment|// Write a file to the new tmp directory as a regular user
name|hdfs
operator|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|user1
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|file
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
comment|// Log onto cluster as another user and attempt to append to file
name|hdfs
operator|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|user2
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|h
init|=
name|hdfs
operator|.
name|append
argument_list|(
name|file2
argument_list|)
decl_stmt|;
name|h
operator|.
name|write
argument_list|(
literal|"Some more data"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|h
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that one user can't delete another user's file when the sticky bit is    * set.    */
DECL|method|confirmDeletingFiles (Configuration conf, FileSystem hdfs, Path baseDir)
specifier|private
name|void
name|confirmDeletingFiles
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|hdfs
parameter_list|,
name|Path
name|baseDir
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|baseDir
argument_list|,
literal|"contemporary"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|p
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|01777
argument_list|)
argument_list|)
expr_stmt|;
comment|// Write a file to the new temp directory as a regular user
name|hdfs
operator|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|user1
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|)
expr_stmt|;
comment|// Make sure the correct user is the owner
name|assertEquals
argument_list|(
name|user1
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
comment|// Log onto cluster as another user and attempt to delete the file
name|FileSystem
name|hdfs2
init|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|user2
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|hdfs2
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to delete someone else's file with SB on"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"sticky bit"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that if a directory is created in a directory that has the sticky bit    * on, the new directory does not automatically get a sticky bit, as is    * standard Unix behavior    */
DECL|method|confirmStickyBitDoesntPropagate (FileSystem hdfs, Path baseDir)
specifier|private
name|void
name|confirmStickyBitDoesntPropagate
parameter_list|(
name|FileSystem
name|hdfs
parameter_list|,
name|Path
name|baseDir
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|baseDir
argument_list|,
literal|"scissorsisters"
argument_list|)
decl_stmt|;
comment|// Turn on its sticky bit
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|01666
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create a subdirectory within it
name|Path
name|p2
init|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|p2
argument_list|)
expr_stmt|;
comment|// Ensure new directory doesn't have its sticky bit on
name|assertFalse
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|p2
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test basic ability to get and set sticky bits on files and directories.    */
DECL|method|confirmSettingAndGetting (FileSystem hdfs, Path baseDir)
specifier|private
name|void
name|confirmSettingAndGetting
parameter_list|(
name|FileSystem
name|hdfs
parameter_list|,
name|Path
name|baseDir
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|p1
init|=
operator|new
name|Path
argument_list|(
name|baseDir
argument_list|,
literal|"roguetraders"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|p1
argument_list|)
expr_stmt|;
comment|// Initially sticky bit should not be set
name|assertFalse
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|p1
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Same permission, but with sticky bit on
name|short
name|withSB
decl_stmt|;
name|withSB
operator|=
call|(
name|short
call|)
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|p1
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
operator||
literal|01000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|new
name|FsPermission
argument_list|(
name|withSB
argument_list|)
operator|)
operator|.
name|getStickyBit
argument_list|()
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|p1
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|withSB
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|p1
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
argument_list|)
expr_stmt|;
comment|// However, while you can set the sticky bit on files, it has no effect,
comment|// following the linux/unix model:
comment|//
comment|// [user@host test]$ ls -alh
comment|// -rw-r--r-- 1 user users 0 Dec 31 01:46 aFile
comment|// [user@host test]$ chmod +t aFile
comment|// [user@host test]$ ls -alh
comment|// -rw-r--r-- 1 user users 0 Dec 31 01:46 aFile
comment|// Write a file to the fs, try to set its sticky bit, expect to be ignored
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
name|baseDir
argument_list|,
literal|"somefile"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|hdfs
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
argument_list|)
expr_stmt|;
name|withSB
operator|=
call|(
name|short
call|)
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
operator||
literal|01000
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|f
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|withSB
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeneralSBBehavior ()
specifier|public
name|void
name|testGeneralSBBehavior
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PERMISSIONS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
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
literal|4
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|FileSystem
name|hdfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|instanceof
name|DistributedFileSystem
argument_list|)
expr_stmt|;
name|Path
name|baseDir
init|=
operator|new
name|Path
argument_list|(
literal|"/mcgann"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
name|confirmCanAppend
argument_list|(
name|conf
argument_list|,
name|hdfs
argument_list|,
name|baseDir
argument_list|)
expr_stmt|;
name|baseDir
operator|=
operator|new
name|Path
argument_list|(
literal|"/eccleston"
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
name|confirmSettingAndGetting
argument_list|(
name|hdfs
argument_list|,
name|baseDir
argument_list|)
expr_stmt|;
name|baseDir
operator|=
operator|new
name|Path
argument_list|(
literal|"/tennant"
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
name|confirmDeletingFiles
argument_list|(
name|conf
argument_list|,
name|hdfs
argument_list|,
name|baseDir
argument_list|)
expr_stmt|;
name|baseDir
operator|=
operator|new
name|Path
argument_list|(
literal|"/smith"
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
name|confirmStickyBitDoesntPropagate
argument_list|(
name|hdfs
argument_list|,
name|baseDir
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that one user can't rename/move another user's file when the sticky    * bit is set.    */
annotation|@
name|Test
DECL|method|testMovingFiles ()
specifier|public
name|void
name|testMovingFiles
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Set up cluster for testing
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PERMISSIONS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
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
literal|4
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|FileSystem
name|hdfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|instanceof
name|DistributedFileSystem
argument_list|)
expr_stmt|;
comment|// Create a tmp directory with wide-open permissions and sticky bit
name|Path
name|tmpPath
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
decl_stmt|;
name|Path
name|tmpPath2
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp2"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|tmpPath
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|tmpPath2
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|tmpPath
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|01777
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|tmpPath2
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|01777
argument_list|)
argument_list|)
expr_stmt|;
comment|// Write a file to the new tmp directory as a regular user
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|tmpPath
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|FileSystem
name|hdfs2
init|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|user1
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|hdfs2
argument_list|,
name|file
argument_list|)
expr_stmt|;
comment|// Log onto cluster as another user and attempt to move the file
name|FileSystem
name|hdfs3
init|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|user2
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|hdfs3
operator|.
name|rename
argument_list|(
name|file
argument_list|,
operator|new
name|Path
argument_list|(
name|tmpPath2
argument_list|,
literal|"renamed"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to rename someone else's file with SB on"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"sticky bit"
argument_list|)
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
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Ensure that when we set a sticky bit and shut down the file system, we get    * the sticky bit back on re-start, and that no extra sticky bits appear after    * re-start.    */
annotation|@
name|Test
DECL|method|testStickyBitPersistence ()
specifier|public
name|void
name|testStickyBitPersistence
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PERMISSIONS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
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
literal|4
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|FileSystem
name|hdfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|instanceof
name|DistributedFileSystem
argument_list|)
expr_stmt|;
comment|// A tale of three directories...
name|Path
name|sbSet
init|=
operator|new
name|Path
argument_list|(
literal|"/Housemartins"
argument_list|)
decl_stmt|;
name|Path
name|sbNotSpecified
init|=
operator|new
name|Path
argument_list|(
literal|"/INXS"
argument_list|)
decl_stmt|;
name|Path
name|sbSetOff
init|=
operator|new
name|Path
argument_list|(
literal|"/Easyworld"
argument_list|)
decl_stmt|;
for|for
control|(
name|Path
name|p
range|:
operator|new
name|Path
index|[]
block|{
name|sbSet
block|,
name|sbNotSpecified
block|,
name|sbSetOff
block|}
control|)
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// Two directories had there sticky bits set explicitly...
name|hdfs
operator|.
name|setPermission
argument_list|(
name|sbSet
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|01777
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|sbSetOff
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|00777
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// Start file system up again
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
literal|4
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|exists
argument_list|(
name|sbSet
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|sbSet
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|exists
argument_list|(
name|sbNotSpecified
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|sbNotSpecified
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|exists
argument_list|(
name|sbSetOff
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|sbSetOff
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/***    * Write a quick file to the specified file system at specified path    */
DECL|method|writeFile (FileSystem hdfs, Path p)
specifier|static
specifier|private
name|void
name|writeFile
parameter_list|(
name|FileSystem
name|hdfs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|o
init|=
name|hdfs
operator|.
name|create
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|o
operator|.
name|write
argument_list|(
literal|"some file contents"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

