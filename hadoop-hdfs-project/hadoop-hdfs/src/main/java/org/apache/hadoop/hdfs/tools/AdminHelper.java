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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|hdfs
operator|.
name|DFSUtil
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
name|CachePoolInfo
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
name|tools
operator|.
name|TableListing
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
name|List
import|;
end_import

begin_comment
comment|/**  * Helper methods for CacheAdmin/CryptoAdmin/StoragePolicyAdmin  */
end_comment

begin_class
DECL|class|AdminHelper
specifier|public
class|class
name|AdminHelper
block|{
comment|/**    * Maximum length for printed lines    */
DECL|field|MAX_LINE_WIDTH
specifier|static
specifier|final
name|int
name|MAX_LINE_WIDTH
init|=
literal|80
decl_stmt|;
DECL|field|HELP_COMMAND_NAME
specifier|static
specifier|final
name|String
name|HELP_COMMAND_NAME
init|=
literal|"-help"
decl_stmt|;
DECL|method|getDFS (Configuration conf)
specifier|static
name|DistributedFileSystem
name|getDFS
parameter_list|(
name|Configuration
name|conf
parameter_list|)
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
DECL|method|getDFS (URI uri, Configuration conf)
specifier|static
name|DistributedFileSystem
name|getDFS
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
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
name|uri
argument_list|,
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
comment|/**    * NN exceptions contain the stack trace as part of the exception message.    * When it's a known error, pretty-print the error and squish the stack trace.    */
DECL|method|prettifyException (Exception e)
specifier|static
name|String
name|prettifyException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
index|[
literal|0
index|]
return|;
block|}
DECL|method|getOptionDescriptionListing ()
specifier|static
name|TableListing
name|getOptionDescriptionListing
parameter_list|()
block|{
return|return
operator|new
name|TableListing
operator|.
name|Builder
argument_list|()
operator|.
name|addField
argument_list|(
literal|""
argument_list|)
operator|.
name|addField
argument_list|(
literal|""
argument_list|,
literal|true
argument_list|)
operator|.
name|wrapWidth
argument_list|(
name|MAX_LINE_WIDTH
argument_list|)
operator|.
name|hideHeaders
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Parses a time-to-live value from a string    * @return The ttl in milliseconds    * @throws IOException if it could not be parsed    */
DECL|method|parseTtlString (String maxTtlString)
specifier|static
name|Long
name|parseTtlString
parameter_list|(
name|String
name|maxTtlString
parameter_list|)
throws|throws
name|IOException
block|{
name|Long
name|maxTtl
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|maxTtlString
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|maxTtlString
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"never"
argument_list|)
condition|)
block|{
name|maxTtl
operator|=
name|CachePoolInfo
operator|.
name|RELATIVE_EXPIRY_NEVER
expr_stmt|;
block|}
else|else
block|{
name|maxTtl
operator|=
name|DFSUtil
operator|.
name|parseRelativeTime
argument_list|(
name|maxTtlString
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|maxTtl
return|;
block|}
DECL|method|parseLimitString (String limitString)
specifier|static
name|Long
name|parseLimitString
parameter_list|(
name|String
name|limitString
parameter_list|)
block|{
name|Long
name|limit
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|limitString
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|limitString
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"unlimited"
argument_list|)
condition|)
block|{
name|limit
operator|=
name|CachePoolInfo
operator|.
name|LIMIT_UNLIMITED
expr_stmt|;
block|}
else|else
block|{
name|limit
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|limitString
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|limit
return|;
block|}
DECL|method|determineCommand (String commandName, Command[] commands)
specifier|static
name|Command
name|determineCommand
parameter_list|(
name|String
name|commandName
parameter_list|,
name|Command
index|[]
name|commands
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|commands
argument_list|)
expr_stmt|;
if|if
condition|(
name|HELP_COMMAND_NAME
operator|.
name|equals
argument_list|(
name|commandName
argument_list|)
condition|)
block|{
return|return
operator|new
name|HelpCommand
argument_list|(
name|commands
argument_list|)
return|;
block|}
for|for
control|(
name|Command
name|command
range|:
name|commands
control|)
block|{
if|if
condition|(
name|command
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
name|command
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|printUsage (boolean longUsage, String toolName, Command[] commands)
specifier|static
name|void
name|printUsage
parameter_list|(
name|boolean
name|longUsage
parameter_list|,
name|String
name|toolName
parameter_list|,
name|Command
index|[]
name|commands
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|commands
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: bin/hdfs "
operator|+
name|toolName
operator|+
literal|" [COMMAND]"
argument_list|)
expr_stmt|;
specifier|final
name|HelpCommand
name|helpCommand
init|=
operator|new
name|HelpCommand
argument_list|(
name|commands
argument_list|)
decl_stmt|;
for|for
control|(
name|AdminHelper
operator|.
name|Command
name|command
range|:
name|commands
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
name|print
argument_list|(
name|longUsage
condition|?
name|helpCommand
operator|.
name|getLongUsage
argument_list|()
else|:
operator|(
literal|"          "
operator|+
name|helpCommand
operator|.
name|getShortUsage
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
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
DECL|method|run (Configuration conf, List<String> args)
name|int
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
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
DECL|class|HelpCommand
specifier|static
class|class
name|HelpCommand
implements|implements
name|Command
block|{
DECL|field|commands
specifier|private
specifier|final
name|Command
index|[]
name|commands
decl_stmt|;
DECL|method|HelpCommand (Command[] commands)
specifier|public
name|HelpCommand
parameter_list|(
name|Command
index|[]
name|commands
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|commands
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|commands
operator|=
name|commands
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|HELP_COMMAND_NAME
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
specifier|final
name|TableListing
name|listing
init|=
name|AdminHelper
operator|.
name|getOptionDescriptionListing
argument_list|()
decl_stmt|;
name|listing
operator|.
name|addRow
argument_list|(
literal|"<command-name>"
argument_list|,
literal|"The command for which to get "
operator|+
literal|"detailed help. If no command is specified, print detailed help for "
operator|+
literal|"all commands"
argument_list|)
expr_stmt|;
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Get detailed help about a command.\n\n"
operator|+
name|listing
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|run (Configuration conf, List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
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
name|AdminHelper
operator|.
name|Command
name|command
range|:
name|commands
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
literal|1
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
name|err
operator|.
name|println
argument_list|(
literal|"You must give exactly one argument to -help."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
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
comment|// prepend a dash to match against the command names
specifier|final
name|AdminHelper
operator|.
name|Command
name|command
init|=
name|AdminHelper
operator|.
name|determineCommand
argument_list|(
literal|"-"
operator|+
name|commandName
argument_list|,
name|commands
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
literal|"Unknown command '"
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
literal|"Valid help command names are:\n"
argument_list|)
expr_stmt|;
name|String
name|separator
init|=
literal|""
decl_stmt|;
for|for
control|(
name|AdminHelper
operator|.
name|Command
name|c
range|:
name|commands
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
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|", "
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

