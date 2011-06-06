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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * An abstract class for the execution of a file system command  */
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
DECL|class|Command
specifier|abstract
specifier|public
class|class
name|Command
extends|extends
name|Configured
block|{
comment|/** default name of the command */
DECL|field|NAME
specifier|public
specifier|static
name|String
name|NAME
decl_stmt|;
comment|/** the command's usage switches and arguments format */
DECL|field|USAGE
specifier|public
specifier|static
name|String
name|USAGE
decl_stmt|;
comment|/** the command's long description */
DECL|field|DESCRIPTION
specifier|public
specifier|static
name|String
name|DESCRIPTION
decl_stmt|;
DECL|field|args
specifier|protected
name|String
index|[]
name|args
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|exitCode
specifier|protected
name|int
name|exitCode
init|=
literal|0
decl_stmt|;
DECL|field|numErrors
specifier|protected
name|int
name|numErrors
init|=
literal|0
decl_stmt|;
DECL|field|recursive
specifier|protected
name|boolean
name|recursive
init|=
literal|false
decl_stmt|;
DECL|field|exceptions
specifier|protected
name|ArrayList
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Command
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** allows stdout to be captured if necessary */
DECL|field|out
specifier|public
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
comment|/** allows stderr to be captured if necessary */
DECL|field|err
specifier|public
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
comment|/** Constructor */
DECL|method|Command ()
specifier|protected
name|Command
parameter_list|()
block|{
name|out
operator|=
name|System
operator|.
name|out
expr_stmt|;
name|err
operator|=
name|System
operator|.
name|err
expr_stmt|;
block|}
comment|/** Constructor */
DECL|method|Command (Configuration conf)
specifier|protected
name|Command
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
comment|/** @return the command's name excluding the leading character - */
DECL|method|getCommandName ()
specifier|abstract
specifier|public
name|String
name|getCommandName
parameter_list|()
function_decl|;
DECL|method|setRecursive (boolean flag)
specifier|protected
name|void
name|setRecursive
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|recursive
operator|=
name|flag
expr_stmt|;
block|}
DECL|method|isRecursive ()
specifier|protected
name|boolean
name|isRecursive
parameter_list|()
block|{
return|return
name|recursive
return|;
block|}
comment|/**     * Execute the command on the input path    *     * @param path the input path    * @throws IOException if any error occurs    */
DECL|method|run (Path path)
specifier|abstract
specifier|protected
name|void
name|run
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * For each source path, execute the command    *     * @return 0 if it runs successfully; -1 if it fails    */
DECL|method|runAll ()
specifier|public
name|int
name|runAll
parameter_list|()
block|{
name|int
name|exitCode
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|src
range|:
name|args
control|)
block|{
try|try
block|{
name|PathData
index|[]
name|srcs
init|=
name|PathData
operator|.
name|expandAsGlob
argument_list|(
name|src
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PathData
name|s
range|:
name|srcs
control|)
block|{
name|run
argument_list|(
name|s
operator|.
name|path
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
name|exitCode
operator|=
operator|-
literal|1
expr_stmt|;
name|displayError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|exitCode
return|;
block|}
comment|/**    * Invokes the command handler.  The default behavior is to process options,    * expand arguments, and then process each argument.    *<pre>    * run    * |-> {@link #processOptions(LinkedList)}    * \-> {@link #processRawArguments(LinkedList)}    *      |-> {@link #expandArguments(LinkedList)}    *      |   \-> {@link #expandArgument(String)}*    *      \-> {@link #processArguments(LinkedList)}    *          |-> {@link #processArgument(PathData)}*    *          |   |-> {@link #processPathArgument(PathData)}    *          |   \-> {@link #processPaths(PathData, PathData...)}    *          |        \-> {@link #processPath(PathData)}*    *          \-> {@link #processNonexistentPath(PathData)}    *</pre>    * Most commands will chose to implement just    * {@link #processOptions(LinkedList)} and {@link #processPath(PathData)}    *     * @param argv the list of command line arguments    * @return the exit code for the command    * @throws IllegalArgumentException if called with invalid arguments    */
DECL|method|run (String...argv)
specifier|public
name|int
name|run
parameter_list|(
name|String
modifier|...
name|argv
parameter_list|)
block|{
name|LinkedList
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
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|argv
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|isDeprecated
argument_list|()
condition|)
block|{
name|displayWarning
argument_list|(
literal|"DEPRECATED: Please use '"
operator|+
name|getReplacementCommand
argument_list|()
operator|+
literal|"' instead."
argument_list|)
expr_stmt|;
block|}
name|processOptions
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|processRawArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|displayError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|numErrors
operator|==
literal|0
operator|)
condition|?
name|exitCode
else|:
name|exitCodeForError
argument_list|()
return|;
block|}
comment|/**    * The exit code to be returned if any errors occur during execution.    * This method is needed to account for the inconsistency in the exit    * codes returned by various commands.    * @return a non-zero exit code    */
DECL|method|exitCodeForError ()
specifier|protected
name|int
name|exitCodeForError
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/**    * Must be implemented by commands to process the command line flags and    * check the bounds of the remaining arguments.  If an    * IllegalArgumentException is thrown, the FsShell object will print the    * short usage of the command.    * @param args the command line arguments    * @throws IOException    */
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
block|{}
comment|/**    * Allows commands that don't use paths to handle the raw arguments.    * Default behavior is to expand the arguments via    * {@link #expandArguments(LinkedList)} and pass the resulting list to    * {@link #processArguments(LinkedList)}     * @param args the list of argument strings    * @throws IOException    */
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
throws|throws
name|IOException
block|{
name|processArguments
argument_list|(
name|expandArguments
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Expands a list of arguments into {@link PathData} objects.  The default    *  behavior is to call {@link #expandArgument(String)} on each element    *  which by default globs the argument.  The loop catches IOExceptions,    *  increments the error count, and displays the exception.    * @param args strings to expand into {@link PathData} objects    * @return list of all {@link PathData} objects the arguments    * @throws IOException if anything goes wrong...    */
DECL|method|expandArguments (LinkedList<String> args)
specifier|protected
name|LinkedList
argument_list|<
name|PathData
argument_list|>
name|expandArguments
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
name|LinkedList
argument_list|<
name|PathData
argument_list|>
name|expandedArgs
init|=
operator|new
name|LinkedList
argument_list|<
name|PathData
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
try|try
block|{
name|expandedArgs
operator|.
name|addAll
argument_list|(
name|expandArgument
argument_list|(
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// other exceptions are probably nasty
name|displayError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|expandedArgs
return|;
block|}
comment|/**    * Expand the given argument into a list of {@link PathData} objects.    * The default behavior is to expand globs.  Commands may override to    * perform other expansions on an argument.    * @param arg string pattern to expand    * @return list of {@link PathData} objects    * @throws IOException if anything goes wrong...    */
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
name|PathData
index|[]
name|items
init|=
name|PathData
operator|.
name|expandAsGlob
argument_list|(
name|arg
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|items
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// it's a glob that failed to match
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|arg
argument_list|)
throw|;
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|items
argument_list|)
return|;
block|}
comment|/**    *  Processes the command's list of expanded arguments.    *  {@link #processArgument(PathData)} will be invoked with each item    *  in the list.  The loop catches IOExceptions, increments the error    *  count, and displays the exception.    *  @param args a list of {@link PathData} to process    *  @throws IOException if anything goes wrong...     */
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
for|for
control|(
name|PathData
name|arg
range|:
name|args
control|)
block|{
try|try
block|{
name|processArgument
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|displayError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Processes a {@link PathData} item, calling    * {@link #processPathArgument(PathData)} or    * {@link #processNonexistentPath(PathData)} on each item.    * @param item {@link PathData} item to process    * @throws IOException if anything goes wrong...    */
DECL|method|processArgument (PathData item)
specifier|protected
name|void
name|processArgument
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
name|exists
condition|)
block|{
name|processPathArgument
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|processNonexistentPath
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *  This is the last chance to modify an argument before going into the    *  (possibly) recursive {@link #processPaths(PathData, PathData...)}    *  -> {@link #processPath(PathData)} loop.  Ex.  ls and du use this to    *  expand out directories.    *  @param item a {@link PathData} representing a path which exists    *  @throws IOException if anything goes wrong...     */
DECL|method|processPathArgument (PathData item)
specifier|protected
name|void
name|processPathArgument
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
comment|// null indicates that the call is not via recursion, ie. there is
comment|// no parent directory that was expanded
name|processPaths
argument_list|(
literal|null
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Provides a hook for handling paths that don't exist.  By default it    *  will throw an exception.  Primarily overriden by commands that create    *  paths such as mkdir or touch.    *  @param item the {@link PathData} that doesn't exist    *  @throws FileNotFoundException if arg is a path and it doesn't exist    *  @throws IOException if anything else goes wrong...     */
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
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|item
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
comment|/**    *  Iterates over the given expanded paths and invokes    *  {@link #processPath(PathData)} on each element.  If "recursive" is true,    *  will do a post-visit DFS on directories.    *  @param parent if called via a recurse, will be the parent dir, else null    *  @param items a list of {@link PathData} objects to process    *  @throws IOException if anything goes wrong...    */
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
comment|// TODO: this really should be iterative
for|for
control|(
name|PathData
name|item
range|:
name|items
control|)
block|{
try|try
block|{
name|processPath
argument_list|(
name|item
argument_list|)
expr_stmt|;
if|if
condition|(
name|recursive
operator|&&
name|item
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|recursePath
argument_list|(
name|item
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
name|displayError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Hook for commands to implement an operation to be applied on each    * path for the command.  Note implementation of this method is optional    * if earlier methods in the chain handle the operation.    * @param item a {@link PathData} object    * @throws RuntimeException if invoked but not implemented    * @throws IOException if anything else goes wrong in the user-implementation    */
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"processPath() is not implemented"
argument_list|)
throw|;
block|}
comment|/**    *  Gets the directory listing for a path and invokes    *  {@link #processPaths(PathData, PathData...)}    *  @param item {@link PathData} for directory to recurse into    *  @throws IOException if anything goes wrong...    */
DECL|method|recursePath (PathData item)
specifier|protected
name|void
name|recursePath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|processPaths
argument_list|(
name|item
argument_list|,
name|item
operator|.
name|getDirectoryContents
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Display an exception prefaced with the command name.  Also increments    * the error count for the command which will result in a non-zero exit    * code.    * @param e exception to display    */
DECL|method|displayError (Exception e)
specifier|public
name|void
name|displayError
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// build up a list of exceptions that occurred
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|String
name|errorMessage
init|=
name|e
operator|.
name|getLocalizedMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|errorMessage
operator|==
literal|null
condition|)
block|{
comment|// this is an unexpected condition, so dump the whole exception since
comment|// it's probably a nasty internal error where the backtrace would be
comment|// useful
name|errorMessage
operator|=
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|errorMessage
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|errorMessage
operator|=
name|errorMessage
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|,
literal|2
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
block|}
name|displayError
argument_list|(
name|errorMessage
argument_list|)
expr_stmt|;
block|}
comment|/**    * Display an error string prefaced with the command name.  Also increments    * the error count for the command which will result in a non-zero exit    * code.    * @param message error message to display    */
DECL|method|displayError (String message)
specifier|public
name|void
name|displayError
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|numErrors
operator|++
expr_stmt|;
name|displayWarning
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * Display an warning string prefaced with the command name.    * @param message warning message to display    */
DECL|method|displayWarning (String message)
specifier|public
name|void
name|displayWarning
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|err
operator|.
name|println
argument_list|(
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * The name of the command.  Will first try to use the assigned name    * else fallback to the command's preferred name    * @return name of the command    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
operator|(
name|name
operator|==
literal|null
operator|)
condition|?
name|getCommandField
argument_list|(
literal|"NAME"
argument_list|)
else|:
name|name
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|?
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
else|:
name|name
return|;
comment|// this is a historical method
block|}
comment|/**    * Define the name of the command.    * @param name as invoked    */
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * The short usage suitable for the synopsis    * @return "name options"    */
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
name|String
name|cmd
init|=
literal|"-"
operator|+
name|getName
argument_list|()
decl_stmt|;
name|String
name|usage
init|=
name|isDeprecated
argument_list|()
condition|?
literal|""
else|:
name|getCommandField
argument_list|(
literal|"USAGE"
argument_list|)
decl_stmt|;
return|return
name|usage
operator|.
name|isEmpty
argument_list|()
condition|?
name|cmd
else|:
name|cmd
operator|+
literal|" "
operator|+
name|usage
return|;
block|}
comment|/**    * The long usage suitable for help output    * @return text of the usage    */
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|isDeprecated
argument_list|()
condition|?
literal|"(DEPRECATED) Same as '"
operator|+
name|getReplacementCommand
argument_list|()
operator|+
literal|"'"
else|:
name|getCommandField
argument_list|(
literal|"DESCRIPTION"
argument_list|)
return|;
block|}
comment|/**    * Is the command deprecated?    * @return boolean    */
DECL|method|isDeprecated ()
specifier|public
specifier|final
name|boolean
name|isDeprecated
parameter_list|()
block|{
return|return
operator|(
name|getReplacementCommand
argument_list|()
operator|!=
literal|null
operator|)
return|;
block|}
comment|/**    * The replacement for a deprecated command    * @return null if not deprecated, else alternative command    */
DECL|method|getReplacementCommand ()
specifier|public
name|String
name|getReplacementCommand
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Get a public static class field    * @param field the field to retrieve    * @return String of the field    */
DECL|method|getCommandField (String field)
specifier|private
name|String
name|getCommandField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|String
name|value
decl_stmt|;
try|try
block|{
name|value
operator|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
operator|.
name|get
argument_list|(
name|this
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failed to get "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"."
operator|+
name|field
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

