begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
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
name|util
operator|.
name|EnumSet
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
name|fs
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/** Unit tests for permission */
end_comment

begin_class
DECL|class|TestPermission
specifier|public
class|class
name|TestPermission
extends|extends
name|TestCase
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestPermission
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ROOT_PATH
specifier|final
specifier|private
specifier|static
name|Path
name|ROOT_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/data"
argument_list|)
decl_stmt|;
DECL|field|CHILD_DIR1
specifier|final
specifier|private
specifier|static
name|Path
name|CHILD_DIR1
init|=
operator|new
name|Path
argument_list|(
name|ROOT_PATH
argument_list|,
literal|"child1"
argument_list|)
decl_stmt|;
DECL|field|CHILD_DIR2
specifier|final
specifier|private
specifier|static
name|Path
name|CHILD_DIR2
init|=
operator|new
name|Path
argument_list|(
name|ROOT_PATH
argument_list|,
literal|"child2"
argument_list|)
decl_stmt|;
DECL|field|CHILD_FILE1
specifier|final
specifier|private
specifier|static
name|Path
name|CHILD_FILE1
init|=
operator|new
name|Path
argument_list|(
name|ROOT_PATH
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
DECL|field|CHILD_FILE2
specifier|final
specifier|private
specifier|static
name|Path
name|CHILD_FILE2
init|=
operator|new
name|Path
argument_list|(
name|ROOT_PATH
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
DECL|field|FILE_LEN
specifier|final
specifier|private
specifier|static
name|int
name|FILE_LEN
init|=
literal|100
decl_stmt|;
DECL|field|RAN
specifier|final
specifier|private
specifier|static
name|Random
name|RAN
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|USER_NAME
specifier|final
specifier|private
specifier|static
name|String
name|USER_NAME
init|=
literal|"user"
operator|+
name|RAN
operator|.
name|nextInt
argument_list|()
decl_stmt|;
DECL|field|GROUP_NAMES
specifier|final
specifier|private
specifier|static
name|String
index|[]
name|GROUP_NAMES
init|=
block|{
literal|"group1"
block|,
literal|"group2"
block|}
decl_stmt|;
DECL|method|checkPermission (FileSystem fs, String path, FsPermission expected)
specifier|static
name|FsPermission
name|checkPermission
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|path
parameter_list|,
name|FsPermission
name|expected
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
name|LOG
operator|.
name|info
argument_list|(
name|s
operator|.
name|getPath
argument_list|()
operator|+
literal|": "
operator|+
name|s
operator|.
name|isDirectory
argument_list|()
operator|+
literal|" "
operator|+
name|s
operator|.
name|getPermission
argument_list|()
operator|+
literal|":"
operator|+
name|s
operator|.
name|getOwner
argument_list|()
operator|+
literal|":"
operator|+
name|s
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|expected
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|s
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|toShort
argument_list|()
argument_list|,
name|s
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|s
operator|.
name|getPermission
argument_list|()
return|;
block|}
comment|/**    * Tests backward compatibility. Configuration can be    * either set with old param dfs.umask that takes decimal umasks    * or dfs.umaskmode that takes symbolic or octal umask.    */
DECL|method|testBackwardCompatibility ()
specifier|public
name|void
name|testBackwardCompatibility
parameter_list|()
block|{
comment|// Test 1 - old configuration key with decimal
comment|// umask value should be handled when set using
comment|// FSPermission.setUMask() API
name|FsPermission
name|perm
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|18
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FsPermission
operator|.
name|setUMask
argument_list|(
name|conf
argument_list|,
name|perm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|18
argument_list|,
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test 2 - old configuration key set with decimal
comment|// umask value should be handled
name|perm
operator|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|18
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FsPermission
operator|.
name|DEPRECATED_UMASK_LABEL
argument_list|,
literal|"18"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|18
argument_list|,
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test 3 - old configuration key overrides the new one
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FsPermission
operator|.
name|DEPRECATED_UMASK_LABEL
argument_list|,
literal|"18"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FsPermission
operator|.
name|UMASK_LABEL
argument_list|,
literal|"000"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|18
argument_list|,
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test 4 - new configuration key is handled
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FsPermission
operator|.
name|UMASK_LABEL
argument_list|,
literal|"022"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|18
argument_list|,
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreate ()
specifier|public
name|void
name|testCreate
parameter_list|()
throws|throws
name|Exception
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
name|conf
operator|.
name|set
argument_list|(
name|FsPermission
operator|.
name|UMASK_LABEL
argument_list|,
literal|"000"
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|FsPermission
name|rootPerm
init|=
name|checkPermission
argument_list|(
name|fs
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FsPermission
name|inheritPerm
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
call|(
name|short
call|)
argument_list|(
name|rootPerm
operator|.
name|toShort
argument_list|()
operator||
literal|0300
argument_list|)
argument_list|)
decl_stmt|;
name|FsPermission
name|dirPerm
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
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/a1/a2/a3"
argument_list|)
argument_list|,
name|dirPerm
argument_list|)
expr_stmt|;
name|checkPermission
argument_list|(
name|fs
argument_list|,
literal|"/a1"
argument_list|,
name|inheritPerm
argument_list|)
expr_stmt|;
name|checkPermission
argument_list|(
name|fs
argument_list|,
literal|"/a1/a2"
argument_list|,
name|inheritPerm
argument_list|)
expr_stmt|;
name|checkPermission
argument_list|(
name|fs
argument_list|,
literal|"/a1/a2/a3"
argument_list|,
name|dirPerm
argument_list|)
expr_stmt|;
name|FsPermission
name|filePerm
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0444
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/b1/b2/b3.txt"
argument_list|)
argument_list|,
name|filePerm
argument_list|,
literal|true
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
literal|4096
argument_list|)
argument_list|,
name|fs
operator|.
name|getDefaultReplication
argument_list|()
argument_list|,
name|fs
operator|.
name|getDefaultBlockSize
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkPermission
argument_list|(
name|fs
argument_list|,
literal|"/b1"
argument_list|,
name|inheritPerm
argument_list|)
expr_stmt|;
name|checkPermission
argument_list|(
name|fs
argument_list|,
literal|"/b1/b2"
argument_list|,
name|inheritPerm
argument_list|)
expr_stmt|;
name|checkPermission
argument_list|(
name|fs
argument_list|,
literal|"/b1/b2/b3.txt"
argument_list|,
name|filePerm
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FsPermission
operator|.
name|UMASK_LABEL
argument_list|,
literal|"022"
argument_list|)
expr_stmt|;
name|FsPermission
name|permission
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0666
argument_list|)
decl_stmt|;
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/c1"
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|permission
argument_list|)
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|create
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/c1/c2.txt"
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|permission
argument_list|)
argument_list|)
expr_stmt|;
name|checkPermission
argument_list|(
name|fs
argument_list|,
literal|"/c1"
argument_list|,
name|permission
argument_list|)
expr_stmt|;
name|checkPermission
argument_list|(
name|fs
argument_list|,
literal|"/c1/c2.txt"
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFilePermision ()
specifier|public
name|void
name|testFilePermision
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
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
name|MiniDFSCluster
name|cluster
init|=
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
try|try
block|{
name|FileSystem
name|nnfs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// test permissions on files that do not exist
name|assertFalse
argument_list|(
name|nnfs
operator|.
name|exists
argument_list|(
name|CHILD_FILE1
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|nnfs
operator|.
name|setOwner
argument_list|(
name|CHILD_FILE1
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"GOOD: got "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|nnfs
operator|.
name|setPermission
argument_list|(
name|CHILD_FILE1
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
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"GOOD: got "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
comment|// following dir/file creations are legal
name|nnfs
operator|.
name|mkdirs
argument_list|(
name|CHILD_DIR1
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|nnfs
operator|.
name|create
argument_list|(
name|CHILD_FILE1
argument_list|)
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|FILE_LEN
index|]
decl_stmt|;
name|RAN
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|nnfs
operator|.
name|setPermission
argument_list|(
name|CHILD_FILE1
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"700"
argument_list|)
argument_list|)
expr_stmt|;
comment|// following read is legal
name|byte
name|dataIn
index|[]
init|=
operator|new
name|byte
index|[
name|FILE_LEN
index|]
decl_stmt|;
name|FSDataInputStream
name|fin
init|=
name|nnfs
operator|.
name|open
argument_list|(
name|CHILD_FILE1
argument_list|)
decl_stmt|;
name|int
name|bytesRead
init|=
name|fin
operator|.
name|read
argument_list|(
name|dataIn
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bytesRead
operator|==
name|FILE_LEN
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
name|FILE_LEN
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|data
index|[
name|i
index|]
argument_list|,
name|dataIn
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|////////////////////////////////////////////////////////////////
comment|// test illegal file/dir creation
name|UserGroupInformation
name|userGroupInfo
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|USER_NAME
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
name|FileSystem
name|userfs
init|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|userGroupInfo
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// make sure mkdir of a existing directory that is not owned by
comment|// this user does not throw an exception.
name|userfs
operator|.
name|mkdirs
argument_list|(
name|CHILD_DIR1
argument_list|)
expr_stmt|;
comment|// illegal mkdir
name|assertTrue
argument_list|(
operator|!
name|canMkdirs
argument_list|(
name|userfs
argument_list|,
name|CHILD_DIR2
argument_list|)
argument_list|)
expr_stmt|;
comment|// illegal file creation
name|assertTrue
argument_list|(
operator|!
name|canCreate
argument_list|(
name|userfs
argument_list|,
name|CHILD_FILE2
argument_list|)
argument_list|)
expr_stmt|;
comment|// illegal file open
name|assertTrue
argument_list|(
operator|!
name|canOpen
argument_list|(
name|userfs
argument_list|,
name|CHILD_FILE1
argument_list|)
argument_list|)
expr_stmt|;
name|nnfs
operator|.
name|setPermission
argument_list|(
name|ROOT_PATH
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
argument_list|)
expr_stmt|;
name|nnfs
operator|.
name|setPermission
argument_list|(
name|CHILD_DIR1
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"777"
argument_list|)
argument_list|)
expr_stmt|;
name|nnfs
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
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
specifier|final
name|Path
name|RENAME_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/foo/bar"
argument_list|)
decl_stmt|;
name|userfs
operator|.
name|mkdirs
argument_list|(
name|RENAME_PATH
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|canRename
argument_list|(
name|userfs
argument_list|,
name|RENAME_PATH
argument_list|,
name|CHILD_DIR1
argument_list|)
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
DECL|method|canMkdirs (FileSystem fs, Path p)
specifier|static
name|boolean
name|canMkdirs
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|canCreate (FileSystem fs, Path p)
specifier|static
name|boolean
name|canCreate
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|canOpen (FileSystem fs, Path p)
specifier|static
name|boolean
name|canOpen
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|canRename (FileSystem fs, Path src, Path dst )
specifier|static
name|boolean
name|canRename
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

