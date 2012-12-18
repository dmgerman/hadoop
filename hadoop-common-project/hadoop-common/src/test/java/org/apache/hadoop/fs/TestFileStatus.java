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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|DataInput
import|;
end_import

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
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|junit
operator|.
name|Test
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

begin_class
DECL|class|TestFileStatus
specifier|public
class|class
name|TestFileStatus
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
name|TestFileStatus
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Values for creating {@link FileStatus} in some tests */
DECL|field|LENGTH
specifier|static
specifier|final
name|int
name|LENGTH
init|=
literal|1
decl_stmt|;
DECL|field|REPLICATION
specifier|static
specifier|final
name|int
name|REPLICATION
init|=
literal|2
decl_stmt|;
DECL|field|BLKSIZE
specifier|static
specifier|final
name|long
name|BLKSIZE
init|=
literal|3
decl_stmt|;
DECL|field|MTIME
specifier|static
specifier|final
name|long
name|MTIME
init|=
literal|4
decl_stmt|;
DECL|field|ATIME
specifier|static
specifier|final
name|long
name|ATIME
init|=
literal|5
decl_stmt|;
DECL|field|OWNER
specifier|static
specifier|final
name|String
name|OWNER
init|=
literal|"owner"
decl_stmt|;
DECL|field|GROUP
specifier|static
specifier|final
name|String
name|GROUP
init|=
literal|"group"
decl_stmt|;
DECL|field|PERMISSION
specifier|static
specifier|final
name|FsPermission
name|PERMISSION
init|=
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-rw-rw-rw-"
argument_list|)
decl_stmt|;
DECL|field|PATH
specifier|static
specifier|final
name|Path
name|PATH
init|=
operator|new
name|Path
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
comment|/**    * Check that the write and readField methods work correctly.    */
annotation|@
name|Test
DECL|method|testFileStatusWritable ()
specifier|public
name|void
name|testFileStatusWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStatus
index|[]
name|tests
init|=
block|{
operator|new
name|FileStatus
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
block|,
operator|new
name|FileStatus
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
block|,
operator|new
name|FileStatus
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Writing FileStatuses to a ByteArrayOutputStream"
argument_list|)
expr_stmt|;
comment|// Writing input list to ByteArrayOutputStream
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutput
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|fs
range|:
name|tests
control|)
block|{
name|fs
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating ByteArrayInputStream object"
argument_list|)
expr_stmt|;
name|DataInput
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing if read objects are equal to written ones"
argument_list|)
expr_stmt|;
name|FileStatus
name|dest
init|=
operator|new
name|FileStatus
argument_list|()
decl_stmt|;
name|int
name|iterator
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FileStatus
name|fs
range|:
name|tests
control|)
block|{
name|dest
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Different FileStatuses in iteration "
operator|+
name|iterator
argument_list|,
name|dest
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|iterator
operator|++
expr_stmt|;
block|}
block|}
comment|/**    * Check that the full parameter constructor works correctly.    */
annotation|@
name|Test
DECL|method|constructorFull ()
specifier|public
name|void
name|constructorFull
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|isdir
init|=
literal|false
decl_stmt|;
name|Path
name|symlink
init|=
operator|new
name|Path
argument_list|(
literal|"symlink"
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus
init|=
operator|new
name|FileStatus
argument_list|(
name|LENGTH
argument_list|,
name|isdir
argument_list|,
name|REPLICATION
argument_list|,
name|BLKSIZE
argument_list|,
name|MTIME
argument_list|,
name|ATIME
argument_list|,
name|PERMISSION
argument_list|,
name|OWNER
argument_list|,
name|GROUP
argument_list|,
name|symlink
argument_list|,
name|PATH
argument_list|)
decl_stmt|;
name|validateAccessors
argument_list|(
name|fileStatus
argument_list|,
name|LENGTH
argument_list|,
name|isdir
argument_list|,
name|REPLICATION
argument_list|,
name|BLKSIZE
argument_list|,
name|MTIME
argument_list|,
name|ATIME
argument_list|,
name|PERMISSION
argument_list|,
name|OWNER
argument_list|,
name|GROUP
argument_list|,
name|symlink
argument_list|,
name|PATH
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that the non-symlink constructor works correctly.    */
annotation|@
name|Test
DECL|method|constructorNoSymlink ()
specifier|public
name|void
name|constructorNoSymlink
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|isdir
init|=
literal|true
decl_stmt|;
name|FileStatus
name|fileStatus
init|=
operator|new
name|FileStatus
argument_list|(
name|LENGTH
argument_list|,
name|isdir
argument_list|,
name|REPLICATION
argument_list|,
name|BLKSIZE
argument_list|,
name|MTIME
argument_list|,
name|ATIME
argument_list|,
name|PERMISSION
argument_list|,
name|OWNER
argument_list|,
name|GROUP
argument_list|,
name|PATH
argument_list|)
decl_stmt|;
name|validateAccessors
argument_list|(
name|fileStatus
argument_list|,
name|LENGTH
argument_list|,
name|isdir
argument_list|,
name|REPLICATION
argument_list|,
name|BLKSIZE
argument_list|,
name|MTIME
argument_list|,
name|ATIME
argument_list|,
name|PERMISSION
argument_list|,
name|OWNER
argument_list|,
name|GROUP
argument_list|,
literal|null
argument_list|,
name|PATH
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that the constructor without owner, group and permissions works    * correctly.    */
annotation|@
name|Test
DECL|method|constructorNoOwner ()
specifier|public
name|void
name|constructorNoOwner
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|isdir
init|=
literal|true
decl_stmt|;
name|FileStatus
name|fileStatus
init|=
operator|new
name|FileStatus
argument_list|(
name|LENGTH
argument_list|,
name|isdir
argument_list|,
name|REPLICATION
argument_list|,
name|BLKSIZE
argument_list|,
name|MTIME
argument_list|,
name|PATH
argument_list|)
decl_stmt|;
name|validateAccessors
argument_list|(
name|fileStatus
argument_list|,
name|LENGTH
argument_list|,
name|isdir
argument_list|,
name|REPLICATION
argument_list|,
name|BLKSIZE
argument_list|,
name|MTIME
argument_list|,
literal|0
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
name|PATH
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that the no parameter constructor works correctly.    */
annotation|@
name|Test
DECL|method|constructorBlank ()
specifier|public
name|void
name|constructorBlank
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
name|fileStatus
init|=
operator|new
name|FileStatus
argument_list|()
decl_stmt|;
name|validateAccessors
argument_list|(
name|fileStatus
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that FileStatus are equal if their paths are equal.    */
annotation|@
name|Test
DECL|method|testEquals ()
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus1
init|=
operator|new
name|FileStatus
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-rw-rw-rw-"
argument_list|)
argument_list|,
literal|"one"
argument_list|,
literal|"one"
argument_list|,
literal|null
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus2
init|=
operator|new
name|FileStatus
argument_list|(
literal|2
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"---x--x--x"
argument_list|)
argument_list|,
literal|"two"
argument_list|,
literal|"two"
argument_list|,
literal|null
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fileStatus1
argument_list|,
name|fileStatus2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that FileStatus are not equal if their paths are not equal.    */
annotation|@
name|Test
DECL|method|testNotEquals ()
specifier|public
name|void
name|testNotEquals
parameter_list|()
block|{
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
literal|"path1"
argument_list|)
decl_stmt|;
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
literal|"path2"
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus1
init|=
operator|new
name|FileStatus
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-rw-rw-rw-"
argument_list|)
argument_list|,
literal|"one"
argument_list|,
literal|"one"
argument_list|,
literal|null
argument_list|,
name|path1
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus2
init|=
operator|new
name|FileStatus
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-rw-rw-rw-"
argument_list|)
argument_list|,
literal|"one"
argument_list|,
literal|"one"
argument_list|,
literal|null
argument_list|,
name|path2
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fileStatus1
operator|.
name|equals
argument_list|(
name|fileStatus2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fileStatus2
operator|.
name|equals
argument_list|(
name|fileStatus1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that toString produces the expected output for a file.    */
annotation|@
name|Test
DECL|method|toStringFile ()
specifier|public
name|void
name|toStringFile
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|isdir
init|=
literal|false
decl_stmt|;
name|FileStatus
name|fileStatus
init|=
operator|new
name|FileStatus
argument_list|(
name|LENGTH
argument_list|,
name|isdir
argument_list|,
name|REPLICATION
argument_list|,
name|BLKSIZE
argument_list|,
name|MTIME
argument_list|,
name|ATIME
argument_list|,
name|PERMISSION
argument_list|,
name|OWNER
argument_list|,
name|GROUP
argument_list|,
literal|null
argument_list|,
name|PATH
argument_list|)
decl_stmt|;
name|validateToString
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that toString produces the expected output for a directory.    */
annotation|@
name|Test
DECL|method|toStringDir ()
specifier|public
name|void
name|toStringDir
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
name|fileStatus
init|=
operator|new
name|FileStatus
argument_list|(
name|LENGTH
argument_list|,
literal|true
argument_list|,
name|REPLICATION
argument_list|,
name|BLKSIZE
argument_list|,
name|MTIME
argument_list|,
name|ATIME
argument_list|,
name|PERMISSION
argument_list|,
name|OWNER
argument_list|,
name|GROUP
argument_list|,
literal|null
argument_list|,
name|PATH
argument_list|)
decl_stmt|;
name|validateToString
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that toString produces the expected output for a symlink.    */
annotation|@
name|Test
DECL|method|toStringSymlink ()
specifier|public
name|void
name|toStringSymlink
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|isdir
init|=
literal|false
decl_stmt|;
name|Path
name|symlink
init|=
operator|new
name|Path
argument_list|(
literal|"symlink"
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus
init|=
operator|new
name|FileStatus
argument_list|(
name|LENGTH
argument_list|,
name|isdir
argument_list|,
name|REPLICATION
argument_list|,
name|BLKSIZE
argument_list|,
name|MTIME
argument_list|,
name|ATIME
argument_list|,
name|PERMISSION
argument_list|,
name|OWNER
argument_list|,
name|GROUP
argument_list|,
name|symlink
argument_list|,
name|PATH
argument_list|)
decl_stmt|;
name|validateToString
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate the accessors for FileStatus.    * @param fileStatus FileStatus to checked    * @param length expected length    * @param isdir expected isDirectory    * @param replication expected replication    * @param blocksize expected blocksize    * @param mtime expected modification time    * @param atime expected access time    * @param permission expected permission    * @param owner expected owner    * @param group expected group    * @param symlink expected symlink    * @param path expected path    */
DECL|method|validateAccessors (FileStatus fileStatus, long length, boolean isdir, int replication, long blocksize, long mtime, long atime, FsPermission permission, String owner, String group, Path symlink, Path path)
specifier|private
name|void
name|validateAccessors
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|isdir
parameter_list|,
name|int
name|replication
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|long
name|mtime
parameter_list|,
name|long
name|atime
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|Path
name|symlink
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|length
argument_list|,
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|isdir
argument_list|,
name|fileStatus
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|replication
argument_list|,
name|fileStatus
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blocksize
argument_list|,
name|fileStatus
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mtime
argument_list|,
name|fileStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|atime
argument_list|,
name|fileStatus
operator|.
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|permission
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|owner
argument_list|,
name|fileStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group
argument_list|,
name|fileStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|symlink
operator|==
literal|null
condition|)
block|{
name|assertFalse
argument_list|(
name|fileStatus
operator|.
name|isSymlink
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|fileStatus
operator|.
name|isSymlink
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|symlink
argument_list|,
name|fileStatus
operator|.
name|getSymlink
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|path
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates the toString method for FileStatus.    * @param fileStatus FileStatus to be validated    */
DECL|method|validateToString (FileStatus fileStatus)
specifier|private
name|void
name|validateToString
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|expected
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"FileStatus{"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"path="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"isDirectory="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|isDirectory
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fileStatus
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|expected
operator|.
name|append
argument_list|(
literal|"length="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"replication="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getReplication
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"blocksize="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getBlockSize
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
name|expected
operator|.
name|append
argument_list|(
literal|"modification_time="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"access_time="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getAccessTime
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"owner="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"group="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"permission="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileStatus
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
name|expected
operator|.
name|append
argument_list|(
literal|"isSymlink="
argument_list|)
operator|.
name|append
argument_list|(
literal|true
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"symlink="
argument_list|)
operator|.
name|append
argument_list|(
name|fileStatus
operator|.
name|getSymlink
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expected
operator|.
name|append
argument_list|(
literal|"isSymlink="
argument_list|)
operator|.
name|append
argument_list|(
literal|false
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
operator|.
name|toString
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

