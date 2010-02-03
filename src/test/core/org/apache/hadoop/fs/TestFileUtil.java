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
name|Test
import|;
end_import

begin_class
DECL|class|TestFileUtil
specifier|public
class|class
name|TestFileUtil
block|{
DECL|field|TEST_DIR
specifier|final
specifier|static
specifier|private
name|File
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"fu"
argument_list|)
decl_stmt|;
DECL|field|FILE
specifier|private
specifier|static
name|String
name|FILE
init|=
literal|"x"
decl_stmt|;
DECL|field|LINK
specifier|private
specifier|static
name|String
name|LINK
init|=
literal|"y"
decl_stmt|;
DECL|field|DIR
specifier|private
specifier|static
name|String
name|DIR
init|=
literal|"dir"
decl_stmt|;
DECL|field|del
specifier|private
name|File
name|del
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"del"
argument_list|)
decl_stmt|;
DECL|field|tmp
specifier|private
name|File
name|tmp
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"tmp"
argument_list|)
decl_stmt|;
DECL|field|dir1
specifier|private
name|File
name|dir1
init|=
operator|new
name|File
argument_list|(
name|del
argument_list|,
name|DIR
operator|+
literal|"1"
argument_list|)
decl_stmt|;
DECL|field|dir2
specifier|private
name|File
name|dir2
init|=
operator|new
name|File
argument_list|(
name|del
argument_list|,
name|DIR
operator|+
literal|"2"
argument_list|)
decl_stmt|;
comment|/**    * Creates directories del and tmp for testing.    *     * Contents of them are    * dir:tmp:     *   file: x    * dir:del:    *   file: x    *   dir: dir1 : file:x    *   dir: dir2 : file:x    *   link: y to tmp/x    *   link: tmpDir to tmp    */
DECL|method|setupDirs ()
specifier|private
name|void
name|setupDirs
parameter_list|()
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|del
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|tmp
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|del
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|tmp
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|del
argument_list|,
name|FILE
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|File
name|tmpFile
init|=
operator|new
name|File
argument_list|(
name|tmp
argument_list|,
name|FILE
argument_list|)
decl_stmt|;
name|tmpFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
comment|// create directories
name|dir1
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|dir1
argument_list|,
name|FILE
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|dir2
argument_list|,
name|FILE
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
comment|// create a symlink to file
name|File
name|link
init|=
operator|new
name|File
argument_list|(
name|del
argument_list|,
name|LINK
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|symLink
argument_list|(
name|tmpFile
operator|.
name|toString
argument_list|()
argument_list|,
name|link
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// create a symlink to dir
name|File
name|linkDir
init|=
operator|new
name|File
argument_list|(
name|del
argument_list|,
literal|"tmpDir"
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|symLink
argument_list|(
name|tmp
operator|.
name|toString
argument_list|()
argument_list|,
name|linkDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|del
operator|.
name|listFiles
argument_list|()
operator|.
name|length
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
name|IOException
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|del
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFullyDelete ()
specifier|public
name|void
name|testFullyDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|setupDirs
argument_list|()
expr_stmt|;
name|boolean
name|ret
init|=
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|del
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ret
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|del
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|validateTmpDir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFullyDeleteContents ()
specifier|public
name|void
name|testFullyDeleteContents
parameter_list|()
throws|throws
name|IOException
block|{
name|setupDirs
argument_list|()
expr_stmt|;
name|boolean
name|ret
init|=
name|FileUtil
operator|.
name|fullyDeleteContents
argument_list|(
name|del
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ret
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|del
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|del
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|validateTmpDir
argument_list|()
expr_stmt|;
block|}
DECL|method|validateTmpDir ()
specifier|private
name|void
name|validateTmpDir
parameter_list|()
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tmp
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tmp
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|tmp
argument_list|,
name|FILE
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a directory which can not be deleted.    *     * Contents of the directory are :    * dir : del    *   dir : dir1 : x. this directory is not writable    * @throws IOException    */
DECL|method|setupDirsAndNonWritablePermissions ()
specifier|private
name|void
name|setupDirsAndNonWritablePermissions
parameter_list|()
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|del
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|del
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|del
argument_list|,
name|FILE
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
comment|// create directory
name|dir1
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|dir1
argument_list|,
name|FILE
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|changePermissions
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// sets writable permissions for dir1
DECL|method|changePermissions (boolean perm)
specifier|private
name|void
name|changePermissions
parameter_list|(
name|boolean
name|perm
parameter_list|)
block|{
name|dir1
operator|.
name|setWritable
argument_list|(
name|perm
argument_list|)
expr_stmt|;
block|}
comment|// validates the return value
comment|// validates the directory:dir1 exists
comment|// sets writable permissions for the directory so that it can be deleted in
comment|// tearDown()
DECL|method|validateAndSetWritablePermissions (boolean ret)
specifier|private
name|void
name|validateAndSetWritablePermissions
parameter_list|(
name|boolean
name|ret
parameter_list|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|ret
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dir1
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|changePermissions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailFullyDelete ()
specifier|public
name|void
name|testFailFullyDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|setupDirsAndNonWritablePermissions
argument_list|()
expr_stmt|;
name|boolean
name|ret
init|=
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|del
argument_list|)
decl_stmt|;
name|validateAndSetWritablePermissions
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailFullyDeleteContents ()
specifier|public
name|void
name|testFailFullyDeleteContents
parameter_list|()
throws|throws
name|IOException
block|{
name|setupDirsAndNonWritablePermissions
argument_list|()
expr_stmt|;
name|boolean
name|ret
init|=
name|FileUtil
operator|.
name|fullyDeleteContents
argument_list|(
name|del
argument_list|)
decl_stmt|;
name|validateAndSetWritablePermissions
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

