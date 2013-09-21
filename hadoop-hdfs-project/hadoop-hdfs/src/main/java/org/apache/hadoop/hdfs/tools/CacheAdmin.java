begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
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
name|FileSystem
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
name|RemoteIterator
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
name|hdfs
operator|.
name|DistributedFileSystem
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
name|hdfs
operator|.
name|protocol
operator|.
name|PathBasedCacheDescriptor
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
name|hdfs
operator|.
name|protocol
operator|.
name|PathBasedCacheDirective
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
name|hdfs
operator|.
name|tools
operator|.
name|TableListing
operator|.
name|Justification
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
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * This class implements command-line operations on the HDFS Cache.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CacheAdmin
specifier|public
class|class
name|CacheAdmin
block|{
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|getDFS ()
specifier|private
specifier|static
name|DistributedFileSystem
name|getDFS
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|fs
operator|instanceof
name|DistributedFileSystem
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"FileSystem "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|" is not an HDFS file system"
argument_list|)
throw|;
block|}
return|return
operator|(
name|DistributedFileSystem
operator|)
name|fs
return|;
block|}
DECL|interface|Command
interface|interface
name|Command
block|{
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getShortUsage ()
name|String
name|getShortUsage
parameter_list|()
function_decl|;
DECL|method|getLongUsage ()
name|String
name|getLongUsage
parameter_list|()
function_decl|;
DECL|method|run (List<String> args)
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|AddPathBasedCacheDirectiveCommand
specifier|private
specifier|static
class|class
name|AddPathBasedCacheDirectiveCommand
implements|implements
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-addPath"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"[-addPath -path<path> -pool<pool-name>]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"Adds a new PathBasedCache directive.\n"
operator|+
literal|"<path>  The new path to cache.\n"
operator|+
literal|"        Paths may be either directories or files.\n"
operator|+
literal|"<pool-name> The pool which this directive will reside in.\n"
operator|+
literal|"        You must have write permission on the cache pool in order\n"
operator|+
literal|"        to add new entries to it.\n"
return|;
block|}
annotation|@
name|Override
DECL|method|run (List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-path"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify a path with -path."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|String
name|poolName
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-pool"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|poolName
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify a pool name with -pool."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't understand argument: "
operator|+
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|DistributedFileSystem
name|dfs
init|=
name|getDFS
argument_list|()
decl_stmt|;
name|PathBasedCacheDirective
name|directive
init|=
operator|new
name|PathBasedCacheDirective
argument_list|(
name|path
argument_list|,
name|poolName
argument_list|)
decl_stmt|;
name|PathBasedCacheDescriptor
name|descriptor
init|=
name|dfs
operator|.
name|addPathBasedCacheDirective
argument_list|(
name|directive
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Added PathBasedCache entry "
operator|+
name|descriptor
operator|.
name|getEntryId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
DECL|class|RemovePathBasedCacheDirectiveCommand
specifier|private
specifier|static
class|class
name|RemovePathBasedCacheDirectiveCommand
implements|implements
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-removePath"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"[-removePath<id>]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"Remove a cache directive.\n"
operator|+
literal|"<id>    The id of the cache directive to remove.\n"
operator|+
literal|"        You must have write permission on the pool where the\n"
operator|+
literal|"        directive resides in order to remove it.  To see a list\n"
operator|+
literal|"        of PathBasedCache directive IDs, use the -list command.\n"
return|;
block|}
annotation|@
name|Override
DECL|method|run (List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|idString
init|=
name|StringUtils
operator|.
name|popFirstNonOption
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|idString
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify a directive ID to remove."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|long
name|id
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|idString
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|<=
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Invalid directive ID "
operator|+
name|id
operator|+
literal|": ids must "
operator|+
literal|"be greater than 0."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't understand argument: "
operator|+
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|DistributedFileSystem
name|dfs
init|=
name|getDFS
argument_list|()
decl_stmt|;
name|dfs
operator|.
name|removePathBasedCacheDescriptor
argument_list|(
operator|new
name|PathBasedCacheDescriptor
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Removed PathBasedCache directive "
operator|+
name|id
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
DECL|class|ListPathBasedCacheDirectiveCommand
specifier|private
specifier|static
class|class
name|ListPathBasedCacheDirectiveCommand
implements|implements
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-listPaths"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"[-listPaths [-path<path>] [-pool<pool-name>]]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"List PathBasedCache directives.\n"
operator|+
literal|"<path> If a -path argument is given, we will list only\n"
operator|+
literal|"        PathBasedCache entries with this path.\n"
operator|+
literal|"        Note that if there is a PathBasedCache directive for<path>\n"
operator|+
literal|"        in a cache pool that we don't have read access for, it\n"
operator|+
literal|"        not be listed.  If there are unreadable cache pools, a\n"
operator|+
literal|"        message will be printed.\n"
operator|+
literal|"        may be incomplete.\n"
operator|+
literal|"<pool-name> If a -pool argument is given, we will list only path\n"
operator|+
literal|"        cache entries in that pool.\n"
return|;
block|}
annotation|@
name|Override
DECL|method|run (List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|pathFilter
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-path"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|String
name|poolFilter
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-pool"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't understand argument: "
operator|+
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|TableListing
name|tableListing
init|=
operator|new
name|TableListing
operator|.
name|Builder
argument_list|()
operator|.
name|addField
argument_list|(
literal|"ID"
argument_list|,
name|Justification
operator|.
name|RIGHT
argument_list|)
operator|.
name|addField
argument_list|(
literal|"POOL"
argument_list|,
name|Justification
operator|.
name|LEFT
argument_list|)
operator|.
name|addField
argument_list|(
literal|"PATH"
argument_list|,
name|Justification
operator|.
name|LEFT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DistributedFileSystem
name|dfs
init|=
name|getDFS
argument_list|()
decl_stmt|;
name|RemoteIterator
argument_list|<
name|PathBasedCacheDescriptor
argument_list|>
name|iter
init|=
name|dfs
operator|.
name|listPathBasedCacheDescriptors
argument_list|(
name|poolFilter
argument_list|,
name|pathFilter
argument_list|)
decl_stmt|;
name|int
name|numEntries
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PathBasedCacheDescriptor
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|row
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|""
operator|+
name|entry
operator|.
name|getEntryId
argument_list|()
block|,
name|entry
operator|.
name|getPool
argument_list|()
block|,
name|entry
operator|.
name|getPath
argument_list|()
block|,         }
decl_stmt|;
name|tableListing
operator|.
name|addRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|numEntries
operator|++
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Found %d entr%s\n"
argument_list|,
name|numEntries
argument_list|,
name|numEntries
operator|==
literal|1
condition|?
literal|"y"
else|:
literal|"ies"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|numEntries
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|tableListing
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
block|}
DECL|class|HelpCommand
specifier|private
specifier|static
class|class
name|HelpCommand
implements|implements
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-help"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"[-help<command-name>]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"Get detailed help about a command.\n"
operator|+
literal|"<command-name> The command to get detailed help for.  If no "
operator|+
literal|"        command-name is specified, we will print detailed help "
operator|+
literal|"        about all commands"
return|;
block|}
annotation|@
name|Override
DECL|method|run (List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|List
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
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|Command
name|command
range|:
name|COMMANDS
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|command
operator|.
name|getLongUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"You must give exactly one argument to -help."
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
name|String
name|commandName
init|=
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|commandName
operator|.
name|replaceAll
argument_list|(
literal|"^[-]*"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Command
name|command
init|=
name|determineCommand
argument_list|(
name|commandName
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"Sorry, I don't know the command '"
operator|+
name|commandName
operator|+
literal|"'.\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"Valid command names are:\n"
argument_list|)
expr_stmt|;
name|String
name|separator
init|=
literal|""
decl_stmt|;
for|for
control|(
name|Command
name|c
range|:
name|COMMANDS
control|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|separator
operator|+
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|", "
expr_stmt|;
block|}
return|return
literal|1
return|;
block|}
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|command
operator|.
name|getLongUsage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
DECL|field|COMMANDS
specifier|private
specifier|static
name|Command
index|[]
name|COMMANDS
init|=
block|{
operator|new
name|AddPathBasedCacheDirectiveCommand
argument_list|()
block|,
operator|new
name|RemovePathBasedCacheDirectiveCommand
argument_list|()
block|,
operator|new
name|ListPathBasedCacheDirectiveCommand
argument_list|()
block|,
operator|new
name|HelpCommand
argument_list|()
block|,   }
decl_stmt|;
DECL|method|printUsage (boolean longUsage)
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|(
name|boolean
name|longUsage
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: bin/hdfs cacheadmin [COMMAND]"
argument_list|)
expr_stmt|;
for|for
control|(
name|Command
name|command
range|:
name|COMMANDS
control|)
block|{
if|if
condition|(
name|longUsage
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|command
operator|.
name|getLongUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"          "
operator|+
name|command
operator|.
name|getShortUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
DECL|method|determineCommand (String commandName)
specifier|private
specifier|static
name|Command
name|determineCommand
parameter_list|(
name|String
name|commandName
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COMMANDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|COMMANDS
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|commandName
argument_list|)
condition|)
block|{
return|return
name|COMMANDS
index|[
name|i
index|]
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|main (String[] argsArray)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argsArray
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|argsArray
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|printUsage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|Command
name|command
init|=
name|determineCommand
argument_list|(
name|argsArray
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't understand command '"
operator|+
name|argsArray
index|[
literal|0
index|]
operator|+
literal|"'"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|argsArray
index|[
literal|0
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Command names must start with dashes."
argument_list|)
expr_stmt|;
block|}
name|printUsage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|argsArray
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|args
operator|.
name|add
argument_list|(
name|argsArray
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|command
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

