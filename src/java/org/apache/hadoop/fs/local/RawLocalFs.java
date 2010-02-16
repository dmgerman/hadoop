begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.local
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|local
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
name|io
operator|.
name|FileNotFoundException
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
name|net
operator|.
name|URISyntaxException
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
name|DelegateToFileSystem
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
name|FsConstants
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
name|FsServerDefaults
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
name|RawLocalFileSystem
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
name|util
operator|.
name|Shell
import|;
end_import

begin_comment
comment|/**  * The RawLocalFs implementation of AbstractFileSystem.  *  This impl delegates to the old FileSystem  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
comment|/*Evolving for a release,to be changed to Stable */
DECL|class|RawLocalFs
specifier|public
class|class
name|RawLocalFs
extends|extends
name|DelegateToFileSystem
block|{
DECL|method|RawLocalFs (final Configuration conf)
name|RawLocalFs
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|this
argument_list|(
name|FsConstants
operator|.
name|LOCAL_FS_URI
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * This constructor has the signature needed by    * {@link AbstractFileSystem#createFileSystem(URI, Configuration)}.    *     * @param theUri which must be that of localFs    * @param conf    * @throws IOException    * @throws URISyntaxException     */
DECL|method|RawLocalFs (final URI theUri, final Configuration conf)
name|RawLocalFs
parameter_list|(
specifier|final
name|URI
name|theUri
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|super
argument_list|(
name|theUri
argument_list|,
operator|new
name|RawLocalFileSystem
argument_list|()
argument_list|,
name|conf
argument_list|,
name|FsConstants
operator|.
name|LOCAL_FS_URI
operator|.
name|getScheme
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUriDefaultPort ()
specifier|protected
name|int
name|getUriDefaultPort
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
comment|// No default port for file:///
block|}
annotation|@
name|Override
DECL|method|getServerDefaults ()
specifier|protected
name|FsServerDefaults
name|getServerDefaults
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|LocalConfigKeys
operator|.
name|getServerDefaults
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|supportsSymlinks ()
specifier|protected
name|boolean
name|supportsSymlinks
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|createSymlink (Path target, Path link, boolean createParent)
specifier|protected
name|void
name|createSymlink
parameter_list|(
name|Path
name|target
parameter_list|,
name|Path
name|link
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|targetScheme
init|=
name|target
operator|.
name|toUri
argument_list|()
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetScheme
operator|!=
literal|null
operator|&&
operator|!
literal|"file"
operator|.
name|equals
argument_list|(
name|targetScheme
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create symlink to non-local file "
operator|+
literal|"system: "
operator|+
name|target
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|createParent
condition|)
block|{
name|mkdir
argument_list|(
name|link
operator|.
name|getParent
argument_list|()
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// NB: Use createSymbolicLink in java.nio.file.Path once available
try|try
block|{
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|LINK_COMMAND
argument_list|,
literal|"-s"
argument_list|,
operator|new
name|URI
argument_list|(
name|target
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|,
operator|new
name|URI
argument_list|(
name|link
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|x
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid symlink path: "
operator|+
name|x
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|x
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create symlink: "
operator|+
name|x
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**     * Returns the target of the given symlink. Returns the empty string if      * the given path does not refer to a symlink or there is an error     * acessing the symlink.    */
DECL|method|readLink (Path p)
specifier|private
name|String
name|readLink
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
comment|/* NB: Use readSymbolicLink in java.nio.file.Path once available. Could      * use getCanonicalPath in File to get the target of the symlink but that       * does not indicate if the given path refers to a symlink.      */
try|try
block|{
specifier|final
name|String
name|path
init|=
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|READ_LINK_COMMAND
argument_list|,
name|path
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|x
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
block|}
comment|/**    * Return a FileStatus representing the given path. If the path refers     * to a symlink return a FileStatus representing the link rather than    * the object the link refers to.    */
annotation|@
name|Override
DECL|method|getFileLinkStatus (final Path f)
specifier|protected
name|FileStatus
name|getFileLinkStatus
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|target
init|=
name|readLink
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|FileStatus
name|fs
init|=
name|getFileStatus
argument_list|(
name|f
argument_list|)
decl_stmt|;
comment|// If f refers to a regular file or directory
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
name|fs
return|;
block|}
comment|// Otherwise f refers to a symlink
return|return
operator|new
name|FileStatus
argument_list|(
name|fs
operator|.
name|getLen
argument_list|()
argument_list|,
literal|false
argument_list|,
name|fs
operator|.
name|getReplication
argument_list|()
argument_list|,
name|fs
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|fs
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|fs
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|fs
operator|.
name|getPermission
argument_list|()
argument_list|,
name|fs
operator|.
name|getOwner
argument_list|()
argument_list|,
name|fs
operator|.
name|getGroup
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
name|target
argument_list|)
argument_list|,
name|f
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|/* The exists method in the File class returns false for dangling         * links so we can get a FileNotFoundException for links that exist.        * It's also possible that we raced with a delete of the link. Use        * the readBasicFileAttributes method in java.nio.file.attributes         * when available.        */
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
operator|new
name|FileStatus
argument_list|(
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
operator|new
name|Path
argument_list|(
name|target
argument_list|)
argument_list|,
name|f
argument_list|)
return|;
block|}
comment|// f refers to a file or directory that does not exist
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLinkTarget (Path f)
specifier|protected
name|Path
name|getLinkTarget
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* We should never get here. Valid local links are resolved transparently      * by the underlying local file system and accessing a dangling link will       * result in an IOException, not an UnresolvedLinkException, so FileContext      * should never call this function.      */
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

