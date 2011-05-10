begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
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
name|Path
import|;
end_import

begin_comment
comment|/**  * Standardized posix/linux style exceptions for path related errors.  * Returns an IOException with the format "path: standard error string".  */
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
DECL|class|PathExceptions
specifier|public
class|class
name|PathExceptions
block|{
comment|/** EIO */
DECL|class|PathIOException
specifier|public
specifier|static
class|class
name|PathIOException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0L
decl_stmt|;
DECL|field|EIO
specifier|private
specifier|static
specifier|final
name|String
name|EIO
init|=
literal|"Input/output error"
decl_stmt|;
comment|// NOTE: this really should be a Path, but a Path is buggy and won't
comment|// return the exact string used to construct the path, and it mangles
comment|// uris with no authority
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
comment|/**      * Constructor a generic I/O error exception      *  @param path for the exception      */
DECL|method|PathIOException (String path)
specifier|public
name|PathIOException
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
name|EIO
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Appends the text of a Throwable to the default error message      * @param path for the exception      * @param cause a throwable to extract the error message      */
DECL|method|PathIOException (String path, Throwable cause)
specifier|public
name|PathIOException
parameter_list|(
name|String
name|path
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
name|EIO
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**      * Avoid using this method.  Use a subclass of PathIOException if      * possible.      * @param path for the exception      * @param error custom string to use an the error text      */
DECL|method|PathIOException (String path, String error)
specifier|public
name|PathIOException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|error
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
name|error
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|PathIOException (String path, String error, Throwable cause)
specifier|protected
name|PathIOException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|error
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|error
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
name|String
name|message
init|=
literal|"`"
operator|+
name|path
operator|+
literal|"': "
operator|+
name|super
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|message
operator|+=
literal|": "
operator|+
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
return|return
name|message
return|;
block|}
comment|/** @return Path that generated the exception */
DECL|method|getPath ()
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
comment|/** ENOENT */
DECL|class|PathNotFoundException
specifier|public
specifier|static
class|class
name|PathNotFoundException
extends|extends
name|PathIOException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0L
decl_stmt|;
comment|/** @param path for the exception */
DECL|method|PathNotFoundException (String path)
specifier|public
name|PathNotFoundException
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|"No such file or directory"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** EEXISTS */
DECL|class|PathExistsException
specifier|public
specifier|static
class|class
name|PathExistsException
extends|extends
name|PathIOException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0L
decl_stmt|;
comment|/** @param path for the exception */
DECL|method|PathExistsException (String path)
specifier|public
name|PathExistsException
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|"File exists"
argument_list|)
expr_stmt|;
block|}
DECL|method|PathExistsException (String path, String error)
specifier|protected
name|PathExistsException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|error
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** EISDIR */
DECL|class|PathIsDirectoryException
specifier|public
specifier|static
class|class
name|PathIsDirectoryException
extends|extends
name|PathExistsException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0L
decl_stmt|;
comment|/** @param path for the exception */
DECL|method|PathIsDirectoryException (String path)
specifier|public
name|PathIsDirectoryException
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|"Is a directory"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** ENOTDIR */
DECL|class|PathIsNotDirectoryException
specifier|public
specifier|static
class|class
name|PathIsNotDirectoryException
extends|extends
name|PathExistsException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0L
decl_stmt|;
comment|/** @param path for the exception */
DECL|method|PathIsNotDirectoryException (String path)
specifier|public
name|PathIsNotDirectoryException
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|"Is not a directory"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** EACCES */
DECL|class|PathAccessDeniedException
specifier|public
specifier|static
class|class
name|PathAccessDeniedException
extends|extends
name|PathIOException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0L
decl_stmt|;
comment|/** @param path for the exception */
DECL|method|PathAccessDeniedException (String path)
specifier|public
name|PathAccessDeniedException
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|"Permission denied"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** EPERM */
DECL|class|PathPermissionException
specifier|public
specifier|static
class|class
name|PathPermissionException
extends|extends
name|PathIOException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0L
decl_stmt|;
comment|/** @param path for the exception */
DECL|method|PathPermissionException (String path)
specifier|public
name|PathPermissionException
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|"Operation not permitted"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

