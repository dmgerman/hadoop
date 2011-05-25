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
name|LinkedList
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
name|shell
operator|.
name|PathExceptions
operator|.
name|PathExistsException
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
name|shell
operator|.
name|PathExceptions
operator|.
name|PathIOException
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
name|shell
operator|.
name|PathExceptions
operator|.
name|PathIsNotDirectoryException
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
name|shell
operator|.
name|PathExceptions
operator|.
name|PathNotFoundException
import|;
end_import

begin_comment
comment|/**  * Provides: argument processing to ensure the destination is valid  * for the number of source arguments.  A processPaths that accepts both  * a source and resolved target.  Sources are resolved as children of  * a destination directory.  */
end_comment

begin_class
DECL|class|CommandWithDestination
specifier|abstract
class|class
name|CommandWithDestination
extends|extends
name|FsCommand
block|{
DECL|field|dst
specifier|protected
name|PathData
name|dst
decl_stmt|;
DECL|field|overwrite
specifier|protected
name|boolean
name|overwrite
init|=
literal|false
decl_stmt|;
comment|// TODO: commands should implement a -f to enable this
DECL|method|setOverwrite (boolean flag)
specifier|protected
name|void
name|setOverwrite
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|overwrite
operator|=
name|flag
expr_stmt|;
block|}
comment|/**    *  The last arg is expected to be a local path, if only one argument is    *  given then the destination will be the current directory     *  @param args is the list of arguments    */
DECL|method|getLocalDestination (LinkedList<String> args)
specifier|protected
name|void
name|getLocalDestination
parameter_list|(
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|pathString
init|=
operator|(
name|args
operator|.
name|size
argument_list|()
operator|<
literal|2
operator|)
condition|?
name|Path
operator|.
name|CUR_DIR
else|:
name|args
operator|.
name|removeLast
argument_list|()
decl_stmt|;
name|dst
operator|=
operator|new
name|PathData
argument_list|(
operator|new
name|File
argument_list|(
name|pathString
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *  The last arg is expected to be a remote path, if only one argument is    *  given then the destination will be the remote user's directory     *  @param args is the list of arguments    *  @throws PathIOException if path doesn't exist or matches too many times     */
DECL|method|getRemoteDestination (LinkedList<String> args)
specifier|protected
name|void
name|getRemoteDestination
parameter_list|(
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
name|dst
operator|=
operator|new
name|PathData
argument_list|(
name|Path
operator|.
name|CUR_DIR
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|pathString
init|=
name|args
operator|.
name|removeLast
argument_list|()
decl_stmt|;
comment|// if the path is a glob, then it must match one and only one path
name|PathData
index|[]
name|items
init|=
name|PathData
operator|.
name|expandAsGlob
argument_list|(
name|pathString
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|items
operator|.
name|length
condition|)
block|{
case|case
literal|0
case|:
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|pathString
argument_list|)
throw|;
case|case
literal|1
case|:
name|dst
operator|=
name|items
index|[
literal|0
index|]
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|PathIOException
argument_list|(
name|pathString
argument_list|,
literal|"Too many matches"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|processArguments (LinkedList<PathData> args)
specifier|protected
name|void
name|processArguments
parameter_list|(
name|LinkedList
argument_list|<
name|PathData
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if more than one arg, the destination must be a directory
comment|// if one arg, the dst must not exist or must be a directory
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
if|if
condition|(
operator|!
name|dst
operator|.
name|exists
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|dst
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|dst
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PathIsNotDirectoryException
argument_list|(
name|dst
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|dst
operator|.
name|exists
operator|&&
operator|!
name|dst
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|overwrite
condition|)
block|{
throw|throw
operator|new
name|PathExistsException
argument_list|(
name|dst
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|super
operator|.
name|processArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processPaths (PathData parent, PathData ... items)
specifier|protected
name|void
name|processPaths
parameter_list|(
name|PathData
name|parent
parameter_list|,
name|PathData
modifier|...
name|items
parameter_list|)
throws|throws
name|IOException
block|{
name|PathData
name|savedDst
init|=
name|dst
decl_stmt|;
try|try
block|{
comment|// modify dst as we descend to append the basename of the
comment|// current directory being processed
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
name|dst
operator|=
name|dst
operator|.
name|getPathDataForChild
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|super
operator|.
name|processPaths
argument_list|(
name|parent
argument_list|,
name|items
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dst
operator|=
name|savedDst
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|processPath (PathData src)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|PathData
name|target
decl_stmt|;
comment|// if the destination is a directory, make target a child path,
comment|// else use the destination as-is
if|if
condition|(
name|dst
operator|.
name|exists
operator|&&
name|dst
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|target
operator|=
name|dst
operator|.
name|getPathDataForChild
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|=
name|dst
expr_stmt|;
block|}
if|if
condition|(
name|target
operator|.
name|exists
operator|&&
operator|!
name|overwrite
condition|)
block|{
throw|throw
operator|new
name|PathExistsException
argument_list|(
name|target
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
try|try
block|{
comment|// invoke processPath with both a source and resolved target
name|processPath
argument_list|(
name|src
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathIOException
name|e
parameter_list|)
block|{
comment|// add the target unless it already has one
if|if
condition|(
name|e
operator|.
name|getTargetPath
argument_list|()
operator|==
literal|null
condition|)
block|{
name|e
operator|.
name|setTargetPath
argument_list|(
name|target
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Called with a source and target destination pair    * @param src for the operation    * @param target for the operation    * @throws IOException if anything goes wrong    */
DECL|method|processPath (PathData src, PathData target)
specifier|protected
specifier|abstract
name|void
name|processPath
parameter_list|(
name|PathData
name|src
parameter_list|,
name|PathData
name|target
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

