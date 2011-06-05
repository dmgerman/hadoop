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
name|shell
operator|.
name|CopyCommands
operator|.
name|CopyFromLocal
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

begin_comment
comment|/** Various commands for moving files */
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
DECL|class|MoveCommands
class|class
name|MoveCommands
block|{
DECL|method|registerCommands (CommandFactory factory)
specifier|public
specifier|static
name|void
name|registerCommands
parameter_list|(
name|CommandFactory
name|factory
parameter_list|)
block|{
name|factory
operator|.
name|addClass
argument_list|(
name|MoveFromLocal
operator|.
name|class
argument_list|,
literal|"-moveFromLocal"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|MoveToLocal
operator|.
name|class
argument_list|,
literal|"-moveToLocal"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Rename
operator|.
name|class
argument_list|,
literal|"-mv"
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Move local files to a remote filesystem    */
DECL|class|MoveFromLocal
specifier|public
specifier|static
class|class
name|MoveFromLocal
extends|extends
name|CopyFromLocal
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"moveFromLocal"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"<localsrc> ...<dst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Same as -put, except that the source is\n"
operator|+
literal|"deleted after it's copied."
decl_stmt|;
annotation|@
name|Override
DECL|method|processPath (PathData src, PathData target)
specifier|protected
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
block|{
name|target
operator|.
name|fs
operator|.
name|moveFromLocalFile
argument_list|(
name|src
operator|.
name|path
argument_list|,
name|target
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *  Move remote files to a local filesystem    */
DECL|class|MoveToLocal
specifier|public
specifier|static
class|class
name|MoveToLocal
extends|extends
name|FsCommand
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"moveToLocal"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"<src><localdst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Not implemented yet"
decl_stmt|;
annotation|@
name|Override
DECL|method|processOptions (LinkedList<String> args)
specifier|protected
name|void
name|processOptions
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Option '-moveToLocal' is not implemented yet."
argument_list|)
throw|;
block|}
block|}
comment|/** move/rename paths on the same fileystem */
DECL|class|Rename
specifier|public
specifier|static
class|class
name|Rename
extends|extends
name|CommandWithDestination
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"mv"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"<src> ...<dst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Move files that match the specified file pattern<src>\n"
operator|+
literal|"to a destination<dst>.  When moving multiple files, the\n"
operator|+
literal|"destination must be a directory."
decl_stmt|;
annotation|@
name|Override
DECL|method|processOptions (LinkedList<String> args)
specifier|protected
name|void
name|processOptions
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
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|2
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|getRemoteDestination
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processPath (PathData src, PathData target)
specifier|protected
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
block|{
if|if
condition|(
operator|!
name|src
operator|.
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|equals
argument_list|(
name|target
operator|.
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PathIOException
argument_list|(
name|src
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Does not match target filesystem"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|target
operator|.
name|fs
operator|.
name|rename
argument_list|(
name|src
operator|.
name|path
argument_list|,
name|target
operator|.
name|path
argument_list|)
condition|)
block|{
comment|// we have no way to know the actual error...
throw|throw
operator|new
name|PathIOException
argument_list|(
name|src
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

