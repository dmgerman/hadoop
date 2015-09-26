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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|commons
operator|.
name|lang
operator|.
name|WordUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|conf
operator|.
name|Configured
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
name|Command
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
name|CommandFactory
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
name|FsCommand
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
name|tracing
operator|.
name|SpanReceiverHost
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tracing
operator|.
name|TraceUtils
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
name|Tool
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
name|ToolRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|Sampler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|SamplerBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|Trace
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|TraceScope
import|;
end_import

begin_comment
comment|/** Provide command line access to a FileSystem. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FsShell
specifier|public
class|class
name|FsShell
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FsShell
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MAX_LINE_WIDTH
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LINE_WIDTH
init|=
literal|80
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|trash
specifier|private
name|Trash
name|trash
decl_stmt|;
DECL|field|commandFactory
specifier|protected
name|CommandFactory
name|commandFactory
decl_stmt|;
DECL|field|traceSampler
specifier|private
name|Sampler
name|traceSampler
decl_stmt|;
DECL|field|usagePrefix
specifier|private
specifier|final
name|String
name|usagePrefix
init|=
literal|"Usage: hadoop fs [generic options]"
decl_stmt|;
DECL|field|spanReceiverHost
specifier|private
name|SpanReceiverHost
name|spanReceiverHost
decl_stmt|;
DECL|field|SEHLL_HTRACE_PREFIX
specifier|static
specifier|final
name|String
name|SEHLL_HTRACE_PREFIX
init|=
literal|"dfs.shell.htrace."
decl_stmt|;
comment|/**    * Default ctor with no configuration.  Be sure to invoke    * {@link #setConf(Configuration)} with a valid configuration prior    * to running commands.    */
DECL|method|FsShell ()
specifier|public
name|FsShell
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a FsShell with the given configuration.  Commands can be    * executed via {@link #run(String[])}    * @param conf the hadoop configuration    */
DECL|method|FsShell (Configuration conf)
specifier|public
name|FsShell
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getFS ()
specifier|protected
name|FileSystem
name|getFS
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|==
literal|null
condition|)
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fs
return|;
block|}
DECL|method|getTrash ()
specifier|protected
name|Trash
name|getTrash
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|trash
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|trash
operator|=
operator|new
name|Trash
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|trash
return|;
block|}
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|getConf
argument_list|()
operator|.
name|setQuietMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|commandFactory
operator|==
literal|null
condition|)
block|{
name|commandFactory
operator|=
operator|new
name|CommandFactory
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|commandFactory
operator|.
name|addObject
argument_list|(
operator|new
name|Help
argument_list|()
argument_list|,
literal|"-help"
argument_list|)
expr_stmt|;
name|commandFactory
operator|.
name|addObject
argument_list|(
operator|new
name|Usage
argument_list|()
argument_list|,
literal|"-usage"
argument_list|)
expr_stmt|;
name|registerCommands
argument_list|(
name|commandFactory
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|spanReceiverHost
operator|=
name|SpanReceiverHost
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|SEHLL_HTRACE_PREFIX
argument_list|)
expr_stmt|;
block|}
DECL|method|registerCommands (CommandFactory factory)
specifier|protected
name|void
name|registerCommands
parameter_list|(
name|CommandFactory
name|factory
parameter_list|)
block|{
comment|// TODO: DFSAdmin subclasses FsShell so need to protect the command
comment|// registration.  This class should morph into a base class for
comment|// commands, and then this method can be abstract
if|if
condition|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|FsShell
operator|.
name|class
argument_list|)
condition|)
block|{
name|factory
operator|.
name|registerCommands
argument_list|(
name|FsCommand
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the Trash object associated with this shell.    * @return Path to the trash    * @throws IOException upon error    */
DECL|method|getCurrentTrashDir ()
specifier|public
name|Path
name|getCurrentTrashDir
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getTrash
argument_list|()
operator|.
name|getCurrentTrashDir
argument_list|()
return|;
block|}
comment|// NOTE: Usage/Help are inner classes to allow access to outer methods
comment|// that access commandFactory
comment|/**    *  Display help for commands with their short usage and long description    */
DECL|class|Usage
specifier|protected
class|class
name|Usage
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
literal|"usage"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[cmd ...]"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Displays the usage for given command or all commands if none "
operator|+
literal|"is specified."
decl_stmt|;
annotation|@
name|Override
DECL|method|processRawArguments (LinkedList<String> args)
specifier|protected
name|void
name|processRawArguments
parameter_list|(
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|printUsage
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
name|printUsage
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Displays short usage of commands sans the long description    */
DECL|class|Help
specifier|protected
class|class
name|Help
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
literal|"help"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[cmd ...]"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Displays help for given command or all commands if none "
operator|+
literal|"is specified."
decl_stmt|;
annotation|@
name|Override
DECL|method|processRawArguments (LinkedList<String> args)
specifier|protected
name|void
name|processRawArguments
parameter_list|(
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|printHelp
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
name|printHelp
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*    * The following are helper methods for getInfo().  They are defined    * outside of the scope of the Help/Usage class because the run() method    * needs to invoke them too.     */
comment|// print all usages
DECL|method|printUsage (PrintStream out)
specifier|private
name|void
name|printUsage
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|printInfo
argument_list|(
name|out
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// print one usage
DECL|method|printUsage (PrintStream out, String cmd)
specifier|private
name|void
name|printUsage
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|String
name|cmd
parameter_list|)
block|{
name|printInfo
argument_list|(
name|out
argument_list|,
name|cmd
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// print all helps
DECL|method|printHelp (PrintStream out)
specifier|private
name|void
name|printHelp
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|printInfo
argument_list|(
name|out
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// print one help
DECL|method|printHelp (PrintStream out, String cmd)
specifier|private
name|void
name|printHelp
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|String
name|cmd
parameter_list|)
block|{
name|printInfo
argument_list|(
name|out
argument_list|,
name|cmd
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|printInfo (PrintStream out, String cmd, boolean showHelp)
specifier|private
name|void
name|printInfo
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|String
name|cmd
parameter_list|,
name|boolean
name|showHelp
parameter_list|)
block|{
if|if
condition|(
name|cmd
operator|!=
literal|null
condition|)
block|{
comment|// display help or usage for one command
name|Command
name|instance
init|=
name|commandFactory
operator|.
name|getInstance
argument_list|(
literal|"-"
operator|+
name|cmd
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnknownCommandException
argument_list|(
name|cmd
argument_list|)
throw|;
block|}
if|if
condition|(
name|showHelp
condition|)
block|{
name|printInstanceHelp
argument_list|(
name|out
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|printInstanceUsage
argument_list|(
name|out
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// display help or usage for all commands
name|out
operator|.
name|println
argument_list|(
name|usagePrefix
argument_list|)
expr_stmt|;
comment|// display list of short usages
name|ArrayList
argument_list|<
name|Command
argument_list|>
name|instances
init|=
operator|new
name|ArrayList
argument_list|<
name|Command
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|commandFactory
operator|.
name|getNames
argument_list|()
control|)
block|{
name|Command
name|instance
init|=
name|commandFactory
operator|.
name|getInstance
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|instance
operator|.
name|isDeprecated
argument_list|()
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"\t["
operator|+
name|instance
operator|.
name|getUsage
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|instances
operator|.
name|add
argument_list|(
name|instance
argument_list|)
expr_stmt|;
block|}
block|}
comment|// display long descriptions for each command
if|if
condition|(
name|showHelp
condition|)
block|{
for|for
control|(
name|Command
name|instance
range|:
name|instances
control|)
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|printInstanceHelp
argument_list|(
name|out
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|printInstanceUsage (PrintStream out, Command instance)
specifier|private
name|void
name|printInstanceUsage
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|Command
name|instance
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|usagePrefix
operator|+
literal|" "
operator|+
name|instance
operator|.
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|printInstanceHelp (PrintStream out, Command instance)
specifier|private
name|void
name|printInstanceHelp
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|Command
name|instance
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|instance
operator|.
name|getUsage
argument_list|()
operator|+
literal|" :"
argument_list|)
expr_stmt|;
name|TableListing
name|listing
init|=
literal|null
decl_stmt|;
specifier|final
name|String
name|prefix
init|=
literal|"  "
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|instance
operator|.
name|getDescription
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
if|if
condition|(
name|line
operator|.
name|matches
argument_list|(
literal|"^[ \t]*[-<].*$"
argument_list|)
condition|)
block|{
name|String
index|[]
name|segments
init|=
name|line
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|segments
operator|.
name|length
operator|==
literal|2
condition|)
block|{
if|if
condition|(
name|listing
operator|==
literal|null
condition|)
block|{
name|listing
operator|=
name|createOptionTableListing
argument_list|()
expr_stmt|;
block|}
name|listing
operator|.
name|addRow
argument_list|(
name|segments
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
argument_list|,
name|segments
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
comment|// Normal literal description.
if|if
condition|(
name|listing
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|listingLine
range|:
name|listing
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|listingLine
argument_list|)
expr_stmt|;
block|}
name|listing
operator|=
literal|null
expr_stmt|;
block|}
for|for
control|(
name|String
name|descLine
range|:
name|WordUtils
operator|.
name|wrap
argument_list|(
name|line
argument_list|,
name|MAX_LINE_WIDTH
argument_list|,
literal|"\n"
argument_list|,
literal|true
argument_list|)
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|descLine
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|listing
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|listingLine
range|:
name|listing
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|listingLine
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Creates a two-row table, the first row is for the command line option,
comment|// the second row is for the option description.
DECL|method|createOptionTableListing ()
specifier|private
name|TableListing
name|createOptionTableListing
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
name|build
argument_list|()
return|;
block|}
comment|/**    * run    */
annotation|@
name|Override
DECL|method|run (String argv[])
specifier|public
name|int
name|run
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
comment|// initialize FsShell
name|init
argument_list|()
expr_stmt|;
name|traceSampler
operator|=
operator|new
name|SamplerBuilder
argument_list|(
name|TraceUtils
operator|.
name|wrapHadoopConf
argument_list|(
name|SEHLL_HTRACE_PREFIX
argument_list|,
name|getConf
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|int
name|exitCode
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|argv
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|printUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|cmd
init|=
name|argv
index|[
literal|0
index|]
decl_stmt|;
name|Command
name|instance
init|=
literal|null
decl_stmt|;
try|try
block|{
name|instance
operator|=
name|commandFactory
operator|.
name|getInstance
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnknownCommandException
argument_list|()
throw|;
block|}
name|TraceScope
name|scope
init|=
name|Trace
operator|.
name|startSpan
argument_list|(
name|instance
operator|.
name|getCommandName
argument_list|()
argument_list|,
name|traceSampler
argument_list|)
decl_stmt|;
if|if
condition|(
name|scope
operator|.
name|getSpan
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|args
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|" "
argument_list|,
name|argv
argument_list|)
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
argument_list|()
operator|>
literal|2048
condition|)
block|{
name|args
operator|=
name|args
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2048
argument_list|)
expr_stmt|;
block|}
name|scope
operator|.
name|getSpan
argument_list|()
operator|.
name|addKVAnnotation
argument_list|(
literal|"args"
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|exitCode
operator|=
name|instance
operator|.
name|run
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|argv
argument_list|,
literal|1
argument_list|,
name|argv
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|scope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|displayError
argument_list|(
name|cmd
argument_list|,
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
if|if
condition|(
name|instance
operator|!=
literal|null
condition|)
block|{
name|printInstanceUsage
argument_list|(
name|System
operator|.
name|err
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// instance.run catches IOE, so something is REALLY wrong if here
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|displayError
argument_list|(
name|cmd
argument_list|,
literal|"Fatal internal error"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|exitCode
return|;
block|}
DECL|method|displayError (String cmd, String message)
specifier|private
name|void
name|displayError
parameter_list|(
name|String
name|cmd
parameter_list|,
name|String
name|message
parameter_list|)
block|{
for|for
control|(
name|String
name|line
range|:
name|message
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|cmd
operator|+
literal|": "
operator|+
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'-'
condition|)
block|{
name|Command
name|instance
init|=
literal|null
decl_stmt|;
name|instance
operator|=
name|commandFactory
operator|.
name|getInstance
argument_list|(
literal|"-"
operator|+
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
name|instance
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Did you mean -"
operator|+
name|cmd
operator|+
literal|"?  This command "
operator|+
literal|"begins with a dash."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    *  Performs any necessary cleanup    * @throws IOException upon error    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|spanReceiverHost
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|spanReceiverHost
operator|.
name|closeReceivers
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * main() has some simple utility methods    * @param argv the command and its arguments    * @throws Exception upon error    */
DECL|method|main (String argv[])
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|FsShell
name|shell
init|=
name|newShellInstance
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setQuietMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|shell
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|res
decl_stmt|;
try|try
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
name|argv
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|shell
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
comment|// TODO: this should be abstract in a base class
DECL|method|newShellInstance ()
specifier|protected
specifier|static
name|FsShell
name|newShellInstance
parameter_list|()
block|{
return|return
operator|new
name|FsShell
argument_list|()
return|;
block|}
comment|/**    * The default ctor signals that the command being executed does not exist,    * while other ctor signals that a specific command does not exist.  The    * latter is used by commands that process other commands, ex. -usage/-help    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|UnknownCommandException
specifier|static
class|class
name|UnknownCommandException
extends|extends
name|IllegalArgumentException
block|{
DECL|field|cmd
specifier|private
specifier|final
name|String
name|cmd
decl_stmt|;
DECL|method|UnknownCommandException ()
name|UnknownCommandException
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|UnknownCommandException (String cmd)
name|UnknownCommandException
parameter_list|(
name|String
name|cmd
parameter_list|)
block|{
name|this
operator|.
name|cmd
operator|=
name|cmd
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
return|return
operator|(
operator|(
name|cmd
operator|!=
literal|null
operator|)
condition|?
literal|"`"
operator|+
name|cmd
operator|+
literal|"': "
else|:
literal|""
operator|)
operator|+
literal|"Unknown command"
return|;
block|}
block|}
block|}
end_class

end_unit

