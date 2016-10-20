begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|LocalFileSystem
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

begin_comment
comment|/**  * Class that provides utility functions for checking disk problem  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DiskChecker
specifier|public
class|class
name|DiskChecker
block|{
DECL|class|DiskErrorException
specifier|public
specifier|static
class|class
name|DiskErrorException
extends|extends
name|IOException
block|{
DECL|method|DiskErrorException (String msg)
specifier|public
name|DiskErrorException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|DiskErrorException (String msg, Throwable cause)
specifier|public
name|DiskErrorException
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DiskOutOfSpaceException
specifier|public
specifier|static
class|class
name|DiskOutOfSpaceException
extends|extends
name|IOException
block|{
DECL|method|DiskOutOfSpaceException (String msg)
specifier|public
name|DiskOutOfSpaceException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create the directory if it doesn't exist and check that dir is readable,    * writable and executable    *      * @param dir    * @throws DiskErrorException    */
DECL|method|checkDir (File dir)
specifier|public
specifier|static
name|void
name|checkDir
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|DiskErrorException
block|{
if|if
condition|(
operator|!
name|mkdirsWithExistsCheck
argument_list|(
name|dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Cannot create directory: "
operator|+
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|checkAccessByFileMethods
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create the local directory if necessary, check permissions and also ensure    * it can be read from and written into.    *    * @param localFS local filesystem    * @param dir directory    * @param expected permission    * @throws DiskErrorException    * @throws IOException    */
DECL|method|checkDir (LocalFileSystem localFS, Path dir, FsPermission expected)
specifier|public
specifier|static
name|void
name|checkDir
parameter_list|(
name|LocalFileSystem
name|localFS
parameter_list|,
name|Path
name|dir
parameter_list|,
name|FsPermission
name|expected
parameter_list|)
throws|throws
name|DiskErrorException
throws|,
name|IOException
block|{
name|mkdirsWithExistsAndPermissionCheck
argument_list|(
name|localFS
argument_list|,
name|dir
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|checkAccessByFileMethods
argument_list|(
name|localFS
operator|.
name|pathToFile
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks that the current running process can read, write, and execute the    * given directory by using methods of the File object.    *     * @param dir File to check    * @throws DiskErrorException if dir is not readable, not writable, or not    *   executable    */
DECL|method|checkAccessByFileMethods (File dir)
specifier|private
specifier|static
name|void
name|checkAccessByFileMethods
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|DiskErrorException
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Not a directory: "
operator|+
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|FileUtil
operator|.
name|canRead
argument_list|(
name|dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Directory is not readable: "
operator|+
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|FileUtil
operator|.
name|canWrite
argument_list|(
name|dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Directory is not writable: "
operator|+
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|FileUtil
operator|.
name|canExecute
argument_list|(
name|dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Directory is not executable: "
operator|+
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * The semantics of mkdirsWithExistsCheck method is different from the mkdirs    * method provided in the Sun's java.io.File class in the following way:    * While creating the non-existent parent directories, this method checks for    * the existence of those directories if the mkdir fails at any point (since    * that directory might have just been created by some other process).    * If both mkdir() and the exists() check fails for any seemingly    * non-existent directory, then we signal an error; Sun's mkdir would signal    * an error (return false) if a directory it is attempting to create already    * exists or the mkdir fails.    * @param dir    * @return true on success, false on failure    */
DECL|method|mkdirsWithExistsCheck (File dir)
specifier|private
specifier|static
name|boolean
name|mkdirsWithExistsCheck
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|.
name|mkdir
argument_list|()
operator|||
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|File
name|canonDir
decl_stmt|;
try|try
block|{
name|canonDir
operator|=
name|dir
operator|.
name|getCanonicalFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|parent
init|=
name|canonDir
operator|.
name|getParent
argument_list|()
decl_stmt|;
return|return
operator|(
name|parent
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|mkdirsWithExistsCheck
argument_list|(
operator|new
name|File
argument_list|(
name|parent
argument_list|)
argument_list|)
operator|&&
operator|(
name|canonDir
operator|.
name|mkdir
argument_list|()
operator|||
name|canonDir
operator|.
name|exists
argument_list|()
operator|)
operator|)
return|;
block|}
comment|/**    * Create the directory or check permissions if it already exists.    *    * The semantics of mkdirsWithExistsAndPermissionCheck method is different    * from the mkdirs method provided in the Sun's java.io.File class in the    * following way:    * While creating the non-existent parent directories, this method checks for    * the existence of those directories if the mkdir fails at any point (since    * that directory might have just been created by some other process).    * If both mkdir() and the exists() check fails for any seemingly    * non-existent directory, then we signal an error; Sun's mkdir would signal    * an error (return false) if a directory it is attempting to create already    * exists or the mkdir fails.    *    * @param localFS local filesystem    * @param dir directory to be created or checked    * @param expected expected permission    * @throws IOException    */
DECL|method|mkdirsWithExistsAndPermissionCheck ( LocalFileSystem localFS, Path dir, FsPermission expected)
specifier|static
name|void
name|mkdirsWithExistsAndPermissionCheck
parameter_list|(
name|LocalFileSystem
name|localFS
parameter_list|,
name|Path
name|dir
parameter_list|,
name|FsPermission
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|directory
init|=
name|localFS
operator|.
name|pathToFile
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|boolean
name|created
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|directory
operator|.
name|exists
argument_list|()
condition|)
name|created
operator|=
name|mkdirsWithExistsCheck
argument_list|(
name|directory
argument_list|)
expr_stmt|;
if|if
condition|(
name|created
operator|||
operator|!
name|localFS
operator|.
name|getFileStatus
argument_list|(
name|dir
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|equals
argument_list|(
name|expected
argument_list|)
condition|)
name|localFS
operator|.
name|setPermission
argument_list|(
name|dir
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

