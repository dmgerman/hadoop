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
name|*
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/****************************************************************  * Implement the FileSystem API for the checksumed local filesystem.  *  *****************************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|LocalFileSystem
specifier|public
class|class
name|LocalFileSystem
extends|extends
name|ChecksumFileSystem
block|{
DECL|field|NAME
specifier|static
specifier|final
name|URI
name|NAME
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"file:///"
argument_list|)
decl_stmt|;
DECL|field|rand
specifier|static
specifier|private
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|LocalFileSystem ()
specifier|public
name|LocalFileSystem
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|RawLocalFileSystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize (URI name, Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|URI
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|.
name|getConf
argument_list|()
operator|==
literal|null
condition|)
block|{
name|fs
operator|.
name|initialize
argument_list|(
name|name
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|String
name|scheme
init|=
name|name
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|scheme
operator|.
name|equals
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
name|swapScheme
operator|=
name|scheme
expr_stmt|;
block|}
block|}
comment|/**    * Return the protocol scheme for the FileSystem.    *<p/>    *    * @return<code>file</code>    */
annotation|@
name|Override
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
literal|"file"
return|;
block|}
DECL|method|getRaw ()
specifier|public
name|FileSystem
name|getRaw
parameter_list|()
block|{
return|return
name|getRawFileSystem
argument_list|()
return|;
block|}
DECL|method|LocalFileSystem (FileSystem rawLocalFileSystem)
specifier|public
name|LocalFileSystem
parameter_list|(
name|FileSystem
name|rawLocalFileSystem
parameter_list|)
block|{
name|super
argument_list|(
name|rawLocalFileSystem
argument_list|)
expr_stmt|;
block|}
comment|/** Convert a path to a File. */
DECL|method|pathToFile (Path path)
specifier|public
name|File
name|pathToFile
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
operator|(
operator|(
name|RawLocalFileSystem
operator|)
name|fs
operator|)
operator|.
name|pathToFile
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyFromLocalFile (boolean delSrc, Path src, Path dst)
specifier|public
name|void
name|copyFromLocalFile
parameter_list|(
name|boolean
name|delSrc
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
name|FileUtil
operator|.
name|copy
argument_list|(
name|this
argument_list|,
name|src
argument_list|,
name|this
argument_list|,
name|dst
argument_list|,
name|delSrc
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyToLocalFile (boolean delSrc, Path src, Path dst)
specifier|public
name|void
name|copyToLocalFile
parameter_list|(
name|boolean
name|delSrc
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
name|FileUtil
operator|.
name|copy
argument_list|(
name|this
argument_list|,
name|src
argument_list|,
name|this
argument_list|,
name|dst
argument_list|,
name|delSrc
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Moves files to a bad file directory on the same device, so that their    * storage will not be reused.    */
annotation|@
name|Override
DECL|method|reportChecksumFailure (Path p, FSDataInputStream in, long inPos, FSDataInputStream sums, long sumsPos)
specifier|public
name|boolean
name|reportChecksumFailure
parameter_list|(
name|Path
name|p
parameter_list|,
name|FSDataInputStream
name|in
parameter_list|,
name|long
name|inPos
parameter_list|,
name|FSDataInputStream
name|sums
parameter_list|,
name|long
name|sumsPos
parameter_list|)
block|{
try|try
block|{
comment|// canonicalize f
name|File
name|f
init|=
operator|(
operator|(
name|RawLocalFileSystem
operator|)
name|fs
operator|)
operator|.
name|pathToFile
argument_list|(
name|p
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
decl_stmt|;
comment|// find highest writable parent dir of f on the same device
name|String
name|device
init|=
operator|new
name|DF
argument_list|(
name|f
argument_list|,
name|getConf
argument_list|()
argument_list|)
operator|.
name|getMount
argument_list|()
decl_stmt|;
name|File
name|parent
init|=
name|f
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|File
name|dir
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|canWrite
argument_list|()
operator|&&
name|parent
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|device
argument_list|)
condition|)
block|{
name|dir
operator|=
name|parent
expr_stmt|;
name|parent
operator|=
name|parent
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"not able to find the highest writable parent dir"
argument_list|)
throw|;
block|}
comment|// move the file there
name|File
name|badDir
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"bad_files"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|badDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|badDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|badDir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|String
name|suffix
init|=
literal|"."
operator|+
name|rand
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|File
name|badFile
init|=
operator|new
name|File
argument_list|(
name|badDir
argument_list|,
name|f
operator|.
name|getName
argument_list|()
operator|+
name|suffix
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Moving bad file "
operator|+
name|f
operator|+
literal|" to "
operator|+
name|badFile
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close it first
name|boolean
name|b
init|=
name|f
operator|.
name|renameTo
argument_list|(
name|badFile
argument_list|)
decl_stmt|;
comment|// rename it
if|if
condition|(
operator|!
name|b
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring failure of renameTo"
argument_list|)
expr_stmt|;
block|}
comment|// move checksum file too
name|File
name|checkFile
init|=
operator|(
operator|(
name|RawLocalFileSystem
operator|)
name|fs
operator|)
operator|.
name|pathToFile
argument_list|(
name|getChecksumFile
argument_list|(
name|p
argument_list|)
argument_list|)
decl_stmt|;
name|b
operator|=
name|checkFile
operator|.
name|renameTo
argument_list|(
operator|new
name|File
argument_list|(
name|badDir
argument_list|,
name|checkFile
operator|.
name|getName
argument_list|()
operator|+
name|suffix
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|b
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring failure of renameTo"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error moving bad file "
operator|+
name|p
operator|+
literal|": "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

