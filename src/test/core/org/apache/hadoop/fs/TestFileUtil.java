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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|TestFileUtil
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|field|xSubDir
specifier|private
name|File
name|xSubDir
init|=
operator|new
name|File
argument_list|(
name|del
argument_list|,
literal|"xsubdir"
argument_list|)
decl_stmt|;
DECL|field|ySubDir
specifier|private
name|File
name|ySubDir
init|=
operator|new
name|File
argument_list|(
name|del
argument_list|,
literal|"ysubdir"
argument_list|)
decl_stmt|;
DECL|field|file1Name
specifier|static
name|String
name|file1Name
init|=
literal|"file1"
decl_stmt|;
DECL|field|file2
specifier|private
name|File
name|file2
init|=
operator|new
name|File
argument_list|(
name|xSubDir
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
DECL|field|file3
specifier|private
name|File
name|file3
init|=
operator|new
name|File
argument_list|(
name|ySubDir
argument_list|,
literal|"file3"
argument_list|)
decl_stmt|;
DECL|field|zlink
specifier|private
name|File
name|zlink
init|=
operator|new
name|File
argument_list|(
name|del
argument_list|,
literal|"zlink"
argument_list|)
decl_stmt|;
comment|/**    * Creates a directory which can not be deleted completely.    *     * Directory structure. The naming is important in that {@link MyFile}    * is used to return them in alphabetical order when listed.    *     *                     del(+w)    *                       |    *    .---------------------------------------,    *    |            |              |           |    *  file1(!w)   xsubdir(-w)   ysubdir(+w)   zlink    *                 |              |    *               file2          file3    *    * @throws IOException    */
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
literal|"The directory del should not have existed!"
argument_list|,
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
name|MyFile
argument_list|(
name|del
argument_list|,
name|file1Name
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
comment|// "file1" is non-deletable by default, see MyFile.delete().
name|xSubDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|file2
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|xSubDir
operator|.
name|setWritable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ySubDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|file3
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"The directory tmp should not have existed!"
argument_list|,
name|tmp
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|mkdirs
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
name|FileUtil
operator|.
name|symLink
argument_list|(
name|tmpFile
operator|.
name|toString
argument_list|()
argument_list|,
name|zlink
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Validates the return value.
comment|// Validates the existence of directory "xsubdir" and the file "file1"
comment|// Sets writable permissions for the non-deleted dir "xsubdir" so that it can
comment|// be deleted in tearDown().
DECL|method|validateAndSetWritablePermissions (boolean ret)
specifier|private
name|void
name|validateAndSetWritablePermissions
parameter_list|(
name|boolean
name|ret
parameter_list|)
block|{
name|xSubDir
operator|.
name|setWritable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"The return value should have been false!"
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The file file1 should not have been deleted!"
argument_list|,
operator|new
name|File
argument_list|(
name|del
argument_list|,
name|file1Name
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The directory xsubdir should not have been deleted!"
argument_list|,
name|xSubDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The file file2 should not have been deleted!"
argument_list|,
name|file2
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"The directory ysubdir should have been deleted!"
argument_list|,
name|ySubDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"The link zlink should have been deleted!"
argument_list|,
name|zlink
operator|.
name|exists
argument_list|()
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Running test to verify failure of fullyDelete()"
argument_list|)
expr_stmt|;
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
operator|new
name|MyFile
argument_list|(
name|del
argument_list|)
argument_list|)
decl_stmt|;
name|validateAndSetWritablePermissions
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
comment|/**    * Extend {@link File}. Same as {@link File} except for two things: (1) This    * treats file1Name as a very special file which is not delete-able    * irrespective of it's parent-dir's permissions, a peculiar file instance for    * testing. (2) It returns the files in alphabetically sorted order when    * listed.    *     */
DECL|class|MyFile
specifier|public
specifier|static
class|class
name|MyFile
extends|extends
name|File
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|MyFile (File f)
specifier|public
name|MyFile
parameter_list|(
name|File
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|MyFile (File parent, String child)
specifier|public
name|MyFile
parameter_list|(
name|File
name|parent
parameter_list|,
name|String
name|child
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
comment|/**      * Same as {@link File#delete()} except for file1Name which will never be      * deleted (hard-coded)      */
annotation|@
name|Override
DECL|method|delete ()
specifier|public
name|boolean
name|delete
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to delete myFile "
operator|+
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|bool
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|file1Name
argument_list|)
condition|)
block|{
name|bool
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|bool
operator|=
name|super
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|bool
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleted "
operator|+
name|getAbsolutePath
argument_list|()
operator|+
literal|" successfully"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cannot delete "
operator|+
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|bool
return|;
block|}
comment|/**      * Return the list of files in an alphabetically sorted order      */
annotation|@
name|Override
DECL|method|listFiles ()
specifier|public
name|File
index|[]
name|listFiles
parameter_list|()
block|{
name|File
index|[]
name|files
init|=
name|super
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|filesList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|files
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|filesList
argument_list|)
expr_stmt|;
name|File
index|[]
name|myFiles
init|=
operator|new
name|MyFile
index|[
name|files
operator|.
name|length
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|filesList
control|)
block|{
name|myFiles
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|MyFile
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|myFiles
return|;
block|}
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Running test to verify failure of fullyDeleteContents()"
argument_list|)
expr_stmt|;
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
operator|new
name|MyFile
argument_list|(
name|del
argument_list|)
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

