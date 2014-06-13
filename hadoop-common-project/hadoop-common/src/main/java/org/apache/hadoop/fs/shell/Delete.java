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
name|FileNotFoundException
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
name|PathIsDirectoryException
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
name|PathIsNotEmptyDirectoryException
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
name|PathNotFoundException
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
name|Trash
import|;
end_import

begin_comment
comment|/**  * Classes that delete paths  */
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
DECL|class|Delete
class|class
name|Delete
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
name|Rm
operator|.
name|class
argument_list|,
literal|"-rm"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Rmdir
operator|.
name|class
argument_list|,
literal|"-rmdir"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Rmr
operator|.
name|class
argument_list|,
literal|"-rmr"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Expunge
operator|.
name|class
argument_list|,
literal|"-expunge"
argument_list|)
expr_stmt|;
block|}
comment|/** remove non-directory paths */
DECL|class|Rm
specifier|public
specifier|static
class|class
name|Rm
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
literal|"rm"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[-f] [-r|-R] [-skipTrash]<src> ..."
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Delete all files that match the specified file pattern. "
operator|+
literal|"Equivalent to the Unix command \"rm<src>\"\n"
operator|+
literal|"-skipTrash: option bypasses trash, if enabled, and immediately "
operator|+
literal|"deletes<src>\n"
operator|+
literal|"-f: If the file does not exist, do not display a diagnostic "
operator|+
literal|"message or modify the exit status to reflect an error.\n"
operator|+
literal|"-[rR]:  Recursively deletes directories"
decl_stmt|;
DECL|field|skipTrash
specifier|private
name|boolean
name|skipTrash
init|=
literal|false
decl_stmt|;
DECL|field|deleteDirs
specifier|private
name|boolean
name|deleteDirs
init|=
literal|false
decl_stmt|;
DECL|field|ignoreFNF
specifier|private
name|boolean
name|ignoreFNF
init|=
literal|false
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
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"f"
argument_list|,
literal|"r"
argument_list|,
literal|"R"
argument_list|,
literal|"skipTrash"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|ignoreFNF
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"f"
argument_list|)
expr_stmt|;
name|deleteDirs
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"r"
argument_list|)
operator|||
name|cf
operator|.
name|getOpt
argument_list|(
literal|"R"
argument_list|)
expr_stmt|;
name|skipTrash
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"skipTrash"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expandArgument (String arg)
specifier|protected
name|List
argument_list|<
name|PathData
argument_list|>
name|expandArgument
parameter_list|(
name|String
name|arg
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|super
operator|.
name|expandArgument
argument_list|(
name|arg
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ignoreFNF
condition|)
block|{
throw|throw
name|e
throw|;
block|}
comment|// prevent -f on a non-existent glob from failing
return|return
operator|new
name|LinkedList
argument_list|<
name|PathData
argument_list|>
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|processNonexistentPath (PathData item)
specifier|protected
name|void
name|processNonexistentPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|ignoreFNF
condition|)
name|super
operator|.
name|processNonexistentPath
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processPath (PathData item)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|item
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|deleteDirs
condition|)
block|{
throw|throw
operator|new
name|PathIsDirectoryException
argument_list|(
name|item
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
comment|// TODO: if the user wants the trash to be used but there is any
comment|// problem (ie. creating the trash dir, moving the item to be deleted,
comment|// etc), then the path will just be deleted because moveToTrash returns
comment|// false and it falls thru to fs.delete.  this doesn't seem right
if|if
condition|(
name|moveToTrash
argument_list|(
name|item
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|item
operator|.
name|fs
operator|.
name|delete
argument_list|(
name|item
operator|.
name|path
argument_list|,
name|deleteDirs
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PathIOException
argument_list|(
name|item
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"Deleted "
operator|+
name|item
argument_list|)
expr_stmt|;
block|}
DECL|method|moveToTrash (PathData item)
specifier|private
name|boolean
name|moveToTrash
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|skipTrash
condition|)
block|{
try|try
block|{
name|success
operator|=
name|Trash
operator|.
name|moveToAppropriateTrash
argument_list|(
name|item
operator|.
name|fs
argument_list|,
name|item
operator|.
name|path
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
throw|throw
name|fnfe
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|+
literal|". Consider using -skipTrash option"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
return|return
name|success
return|;
block|}
block|}
comment|/** remove any path */
DECL|class|Rmr
specifier|static
class|class
name|Rmr
extends|extends
name|Rm
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"rmr"
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
name|args
operator|.
name|addFirst
argument_list|(
literal|"-r"
argument_list|)
expr_stmt|;
name|super
operator|.
name|processOptions
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReplacementCommand ()
specifier|public
name|String
name|getReplacementCommand
parameter_list|()
block|{
return|return
literal|"rm -r"
return|;
block|}
block|}
comment|/** remove only empty directories */
DECL|class|Rmdir
specifier|static
class|class
name|Rmdir
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
literal|"rmdir"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[--ignore-fail-on-non-empty]<dir> ..."
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Removes the directory entry specified by each directory argument, "
operator|+
literal|"provided it is empty.\n"
decl_stmt|;
DECL|field|ignoreNonEmpty
specifier|private
name|boolean
name|ignoreNonEmpty
init|=
literal|false
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
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"-ignore-fail-on-non-empty"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|ignoreNonEmpty
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"-ignore-fail-on-non-empty"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processPath (PathData item)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|item
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
name|item
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|item
operator|.
name|fs
operator|.
name|listStatus
argument_list|(
name|item
operator|.
name|path
argument_list|)
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|item
operator|.
name|fs
operator|.
name|delete
argument_list|(
name|item
operator|.
name|path
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PathIOException
argument_list|(
name|item
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|ignoreNonEmpty
condition|)
block|{
throw|throw
operator|new
name|PathIsNotEmptyDirectoryException
argument_list|(
name|item
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** empty the trash */
DECL|class|Expunge
specifier|static
class|class
name|Expunge
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
literal|"expunge"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|""
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Empty the Trash"
decl_stmt|;
comment|// TODO: should probably allow path arguments for the filesystems
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
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
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
name|Trash
name|trash
init|=
operator|new
name|Trash
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|trash
operator|.
name|expunge
argument_list|()
expr_stmt|;
name|trash
operator|.
name|checkpoint
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

