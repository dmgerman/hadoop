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
name|IOException
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

begin_comment
comment|/**  * Class used to perform an operation on and resolve symlinks in a  * path. The operation may potentially span multiple file systems.  */
end_comment

begin_class
DECL|class|FSLinkResolver
specifier|public
specifier|abstract
class|class
name|FSLinkResolver
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|MAX_PATH_LINKS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_PATH_LINKS
init|=
literal|32
decl_stmt|;
comment|/**    * See {@link #qualifySymlinkTarget(URI, Path, Path)}    */
DECL|method|qualifySymlinkTarget (final AbstractFileSystem pathFS, Path pathWithLink, Path target)
specifier|public
specifier|static
name|Path
name|qualifySymlinkTarget
parameter_list|(
specifier|final
name|AbstractFileSystem
name|pathFS
parameter_list|,
name|Path
name|pathWithLink
parameter_list|,
name|Path
name|target
parameter_list|)
block|{
return|return
name|qualifySymlinkTarget
argument_list|(
name|pathFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|pathWithLink
argument_list|,
name|target
argument_list|)
return|;
block|}
comment|/**    * See {@link #qualifySymlinkTarget(URI, Path, Path)}    */
DECL|method|qualifySymlinkTarget (final FileSystem pathFS, Path pathWithLink, Path target)
specifier|public
specifier|static
name|Path
name|qualifySymlinkTarget
parameter_list|(
specifier|final
name|FileSystem
name|pathFS
parameter_list|,
name|Path
name|pathWithLink
parameter_list|,
name|Path
name|target
parameter_list|)
block|{
return|return
name|qualifySymlinkTarget
argument_list|(
name|pathFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|pathWithLink
argument_list|,
name|target
argument_list|)
return|;
block|}
comment|/**    * Return a fully-qualified version of the given symlink target if it    * has no scheme and authority. Partially and fully-qualified paths    * are returned unmodified.    * @param pathURI URI of the filesystem of pathWithLink    * @param pathWithLink Path that contains the symlink    * @param target The symlink's absolute target    * @return Fully qualified version of the target.    */
DECL|method|qualifySymlinkTarget (final URI pathURI, Path pathWithLink, Path target)
specifier|private
specifier|static
name|Path
name|qualifySymlinkTarget
parameter_list|(
specifier|final
name|URI
name|pathURI
parameter_list|,
name|Path
name|pathWithLink
parameter_list|,
name|Path
name|target
parameter_list|)
block|{
comment|// NB: makeQualified uses the target's scheme and authority, if
comment|// specified, and the scheme and authority of pathURI, if not.
specifier|final
name|URI
name|targetUri
init|=
name|target
operator|.
name|toUri
argument_list|()
decl_stmt|;
specifier|final
name|String
name|scheme
init|=
name|targetUri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
specifier|final
name|String
name|auth
init|=
name|targetUri
operator|.
name|getAuthority
argument_list|()
decl_stmt|;
return|return
operator|(
name|scheme
operator|==
literal|null
operator|&&
name|auth
operator|==
literal|null
operator|)
condition|?
name|target
operator|.
name|makeQualified
argument_list|(
name|pathURI
argument_list|,
name|pathWithLink
operator|.
name|getParent
argument_list|()
argument_list|)
else|:
name|target
return|;
block|}
comment|// FileContext / AbstractFileSystem resolution methods
comment|/**    * Generic helper function overridden on instantiation to perform a    * specific operation on the given file system using the given path    * which may result in an UnresolvedLinkException.    * @param fs AbstractFileSystem to perform the operation on.    * @param p Path given the file system.    * @return Generic type determined by the specific implementation.    * @throws UnresolvedLinkException If symbolic link<code>path</code> could    *           not be resolved    * @throws IOException an I/O error occurred    */
DECL|method|next (final AbstractFileSystem fs, final Path p)
specifier|public
name|T
name|next
parameter_list|(
specifier|final
name|AbstractFileSystem
name|fs
parameter_list|,
specifier|final
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Should not be called without first overriding!"
argument_list|)
throw|;
block|}
comment|/**    * Performs the operation specified by the next function, calling it    * repeatedly until all symlinks in the given path are resolved.    * @param fc FileContext used to access file systems.    * @param path The path to resolve symlinks on.    * @return Generic type determined by the implementation of next.    * @throws IOException    */
DECL|method|resolve (final FileContext fc, final Path path)
specifier|public
name|T
name|resolve
parameter_list|(
specifier|final
name|FileContext
name|fc
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|T
name|in
init|=
literal|null
decl_stmt|;
name|Path
name|p
init|=
name|path
decl_stmt|;
comment|// NB: More than one AbstractFileSystem can match a scheme, eg
comment|// "file" resolves to LocalFs but could have come by RawLocalFs.
name|AbstractFileSystem
name|fs
init|=
name|fc
operator|.
name|getFSofPath
argument_list|(
name|p
argument_list|)
decl_stmt|;
comment|// Loop until all symlinks are resolved or the limit is reached
for|for
control|(
name|boolean
name|isLink
init|=
literal|true
init|;
name|isLink
condition|;
control|)
block|{
try|try
block|{
name|in
operator|=
name|next
argument_list|(
name|fs
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|isLink
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnresolvedLinkException
name|e
parameter_list|)
block|{
if|if
condition|(
name|count
operator|++
operator|>
name|MAX_PATH_LINKS
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Possible cyclic loop while "
operator|+
literal|"following symbolic link "
operator|+
name|path
argument_list|)
throw|;
block|}
comment|// Resolve the first unresolved path component
name|p
operator|=
name|FSLinkResolver
operator|.
name|qualifySymlinkTarget
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|fs
operator|.
name|getLinkTarget
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|=
name|fc
operator|.
name|getFSofPath
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|in
return|;
block|}
block|}
end_class

end_unit

