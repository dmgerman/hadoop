begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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

begin_comment
comment|/**   * A base class for running a Unix command.  *   *<code>Shell</code> can be used to run unix commands like<code>du</code> or  *<code>df</code>. It also offers facilities to gate commands by   * time-intervals.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Shell
specifier|abstract
specifier|public
class|class
name|Shell
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Shell
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** a Unix command to get the current user's name */
DECL|field|USER_NAME_COMMAND
specifier|public
specifier|final
specifier|static
name|String
name|USER_NAME_COMMAND
init|=
literal|"whoami"
decl_stmt|;
comment|/** a Unix command to get the current user's groups list */
DECL|method|getGroupsCommand ()
specifier|public
specifier|static
name|String
index|[]
name|getGroupsCommand
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"bash"
block|,
literal|"-c"
block|,
literal|"groups"
block|}
return|;
block|}
comment|/** a Unix command to get a given user's groups list */
DECL|method|getGroupsForUserCommand (final String user)
specifier|public
specifier|static
name|String
index|[]
name|getGroupsForUserCommand
parameter_list|(
specifier|final
name|String
name|user
parameter_list|)
block|{
comment|//'groups username' command return is non-consistent across different unixes
return|return
operator|new
name|String
index|[]
block|{
literal|"bash"
block|,
literal|"-c"
block|,
literal|"id -Gn "
operator|+
name|user
block|}
return|;
block|}
comment|/** a Unix command to get a given netgroup's user list */
DECL|method|getUsersForNetgroupCommand (final String netgroup)
specifier|public
specifier|static
name|String
index|[]
name|getUsersForNetgroupCommand
parameter_list|(
specifier|final
name|String
name|netgroup
parameter_list|)
block|{
comment|//'groups username' command return is non-consistent across different unixes
return|return
operator|new
name|String
index|[]
block|{
literal|"bash"
block|,
literal|"-c"
block|,
literal|"getent netgroup "
operator|+
name|netgroup
block|}
return|;
block|}
comment|/** a Unix command to set permission */
DECL|field|SET_PERMISSION_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|SET_PERMISSION_COMMAND
init|=
literal|"chmod"
decl_stmt|;
comment|/** a Unix command to set owner */
DECL|field|SET_OWNER_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|SET_OWNER_COMMAND
init|=
literal|"chown"
decl_stmt|;
DECL|field|SET_GROUP_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|SET_GROUP_COMMAND
init|=
literal|"chgrp"
decl_stmt|;
comment|/** a Unix command to create a link */
DECL|field|LINK_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|LINK_COMMAND
init|=
literal|"ln"
decl_stmt|;
comment|/** a Unix command to get a link target */
DECL|field|READ_LINK_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|READ_LINK_COMMAND
init|=
literal|"readlink"
decl_stmt|;
comment|/** Return a Unix command to get permission information. */
DECL|method|getGET_PERMISSION_COMMAND ()
specifier|public
specifier|static
name|String
index|[]
name|getGET_PERMISSION_COMMAND
parameter_list|()
block|{
comment|//force /bin/ls, except on windows.
return|return
operator|new
name|String
index|[]
block|{
operator|(
name|WINDOWS
condition|?
literal|"ls"
else|:
literal|"/bin/ls"
operator|)
block|,
literal|"-ld"
block|}
return|;
block|}
comment|/**Time after which the executing script would be timedout*/
DECL|field|timeOutInterval
specifier|protected
name|long
name|timeOutInterval
init|=
literal|0L
decl_stmt|;
comment|/** If or not script timed out*/
DECL|field|timedOut
specifier|private
name|AtomicBoolean
name|timedOut
decl_stmt|;
comment|/** a Unix command to get ulimit of a process. */
DECL|field|ULIMIT_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|ULIMIT_COMMAND
init|=
literal|"ulimit"
decl_stmt|;
comment|/**     * Get the Unix command for setting the maximum virtual memory available    * to a given child process. This is only relevant when we are forking a    * process from within the Mapper or the Reducer implementations.    * Also see Hadoop Pipes and Hadoop Streaming.    *     * It also checks to ensure that we are running on a *nix platform else     * (e.g. in Cygwin/Windows) it returns<code>null</code>.    * @param memoryLimit virtual memory limit    * @return a<code>String[]</code> with the ulimit command arguments or     *<code>null</code> if we are running on a non *nix platform or    *         if the limit is unspecified.    */
DECL|method|getUlimitMemoryCommand (int memoryLimit)
specifier|public
specifier|static
name|String
index|[]
name|getUlimitMemoryCommand
parameter_list|(
name|int
name|memoryLimit
parameter_list|)
block|{
comment|// ulimit isn't supported on Windows
if|if
condition|(
name|WINDOWS
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|String
index|[]
block|{
name|ULIMIT_COMMAND
block|,
literal|"-v"
block|,
name|String
operator|.
name|valueOf
argument_list|(
name|memoryLimit
argument_list|)
block|}
return|;
block|}
comment|/**     * Get the Unix command for setting the maximum virtual memory available    * to a given child process. This is only relevant when we are forking a    * process from within the Mapper or the Reducer implementations.    * see also Hadoop Pipes and Streaming.    *     * It also checks to ensure that we are running on a *nix platform else     * (e.g. in Cygwin/Windows) it returns<code>null</code>.    * @param conf configuration    * @return a<code>String[]</code> with the ulimit command arguments or     *<code>null</code> if we are running on a non *nix platform or    *         if the limit is unspecified.    * @deprecated Use {@link #getUlimitMemoryCommand(int)}    */
annotation|@
name|Deprecated
DECL|method|getUlimitMemoryCommand (Configuration conf)
specifier|public
specifier|static
name|String
index|[]
name|getUlimitMemoryCommand
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// ulimit isn't supported on Windows
if|if
condition|(
name|WINDOWS
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// get the memory limit from the configuration
name|String
name|ulimit
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"mapred.child.ulimit"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ulimit
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Parse it to ensure it is legal/sane
name|int
name|memoryLimit
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|ulimit
argument_list|)
decl_stmt|;
return|return
name|getUlimitMemoryCommand
argument_list|(
name|memoryLimit
argument_list|)
return|;
block|}
comment|/** Set to true on Windows platforms */
DECL|field|WINDOWS
specifier|public
specifier|static
specifier|final
name|boolean
name|WINDOWS
comment|/* borrowed from Path.WINDOWS */
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
decl_stmt|;
DECL|field|interval
specifier|private
name|long
name|interval
decl_stmt|;
comment|// refresh interval in msec
DECL|field|lastTime
specifier|private
name|long
name|lastTime
decl_stmt|;
comment|// last time the command was performed
DECL|field|environment
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
decl_stmt|;
comment|// env for the command execution
DECL|field|dir
specifier|private
name|File
name|dir
decl_stmt|;
DECL|field|process
specifier|private
name|Process
name|process
decl_stmt|;
comment|// sub process used to execute the command
DECL|field|exitCode
specifier|private
name|int
name|exitCode
decl_stmt|;
comment|/**If or not script finished executing*/
DECL|field|completed
specifier|private
specifier|volatile
name|AtomicBoolean
name|completed
decl_stmt|;
DECL|method|Shell ()
specifier|public
name|Shell
parameter_list|()
block|{
name|this
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param interval the minimum duration to wait before re-executing the     *        command.    */
DECL|method|Shell ( long interval )
specifier|public
name|Shell
parameter_list|(
name|long
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|lastTime
operator|=
operator|(
name|interval
operator|<
literal|0
operator|)
condition|?
literal|0
else|:
operator|-
name|interval
expr_stmt|;
block|}
comment|/** set the environment for the command     * @param env Mapping of environment variables    */
DECL|method|setEnvironment (Map<String, String> env)
specifier|protected
name|void
name|setEnvironment
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
name|this
operator|.
name|environment
operator|=
name|env
expr_stmt|;
block|}
comment|/** set the working directory     * @param dir The directory where the command would be executed    */
DECL|method|setWorkingDirectory (File dir)
specifier|protected
name|void
name|setWorkingDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/** check to see if a command needs to be executed and execute if needed */
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lastTime
operator|+
name|interval
operator|>
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
return|return;
name|exitCode
operator|=
literal|0
expr_stmt|;
comment|// reset for next run
name|runCommand
argument_list|()
expr_stmt|;
block|}
comment|/** Run a command */
DECL|method|runCommand ()
specifier|private
name|void
name|runCommand
parameter_list|()
throws|throws
name|IOException
block|{
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|getExecString
argument_list|()
argument_list|)
decl_stmt|;
name|Timer
name|timeOutTimer
init|=
literal|null
decl_stmt|;
name|ShellTimeoutTimerTask
name|timeoutTimerTask
init|=
literal|null
decl_stmt|;
name|timedOut
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|completed
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|environment
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|environment
argument_list|()
operator|.
name|putAll
argument_list|(
name|this
operator|.
name|environment
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|directory
argument_list|(
name|this
operator|.
name|dir
argument_list|)
expr_stmt|;
block|}
name|process
operator|=
name|builder
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|timeOutInterval
operator|>
literal|0
condition|)
block|{
name|timeOutTimer
operator|=
operator|new
name|Timer
argument_list|(
literal|"Shell command timeout"
argument_list|)
expr_stmt|;
name|timeoutTimerTask
operator|=
operator|new
name|ShellTimeoutTimerTask
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|//One time scheduling.
name|timeOutTimer
operator|.
name|schedule
argument_list|(
name|timeoutTimerTask
argument_list|,
name|timeOutInterval
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BufferedReader
name|errReader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|process
operator|.
name|getErrorStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedReader
name|inReader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|StringBuffer
name|errMsg
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
comment|// read error and input streams as this would free up the buffers
comment|// free the error stream buffer
name|Thread
name|errThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|String
name|line
init|=
name|errReader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|isInterrupted
argument_list|()
condition|)
block|{
name|errMsg
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|errMsg
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|errReader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error reading the error stream"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
try|try
block|{
name|errThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{ }
try|try
block|{
name|parseExecResult
argument_list|(
name|inReader
argument_list|)
expr_stmt|;
comment|// parse the output
comment|// clear the input stream buffer
name|String
name|line
init|=
name|inReader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|inReader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
comment|// wait for the process to finish and check the exit code
name|exitCode
operator|=
name|process
operator|.
name|waitFor
argument_list|()
expr_stmt|;
try|try
block|{
comment|// make sure that the error thread exits
name|errThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted while reading the error stream"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
name|completed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//the timeout thread handling
comment|//taken care in finally block
if|if
condition|(
name|exitCode
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|ExitCodeException
argument_list|(
name|exitCode
argument_list|,
name|errMsg
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ie
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|timeOutTimer
operator|!=
literal|null
condition|)
block|{
name|timeOutTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
comment|// close the input stream
try|try
block|{
name|inReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while closing the input stream"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|completed
operator|.
name|get
argument_list|()
condition|)
block|{
name|errThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|errReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while closing the error stream"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|lastTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** return an array containing the command name& its parameters */
DECL|method|getExecString ()
specifier|protected
specifier|abstract
name|String
index|[]
name|getExecString
parameter_list|()
function_decl|;
comment|/** Parse the execution result */
DECL|method|parseExecResult (BufferedReader lines)
specifier|protected
specifier|abstract
name|void
name|parseExecResult
parameter_list|(
name|BufferedReader
name|lines
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** get the current sub-process executing the given command     * @return process executing the command    */
DECL|method|getProcess ()
specifier|public
name|Process
name|getProcess
parameter_list|()
block|{
return|return
name|process
return|;
block|}
comment|/** get the exit code     * @return the exit code of the process    */
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|exitCode
return|;
block|}
comment|/**    * This is an IOException with exit code added.    */
DECL|class|ExitCodeException
specifier|public
specifier|static
class|class
name|ExitCodeException
extends|extends
name|IOException
block|{
DECL|field|exitCode
name|int
name|exitCode
decl_stmt|;
DECL|method|ExitCodeException (int exitCode, String message)
specifier|public
name|ExitCodeException
parameter_list|(
name|int
name|exitCode
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
block|}
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|exitCode
return|;
block|}
block|}
comment|/**    * A simple shell command executor.    *     *<code>ShellCommandExecutor</code>should be used in cases where the output     * of the command needs no explicit parsing and where the command, working     * directory and the environment remains unchanged. The output of the command     * is stored as-is and is expected to be small.    */
DECL|class|ShellCommandExecutor
specifier|public
specifier|static
class|class
name|ShellCommandExecutor
extends|extends
name|Shell
block|{
DECL|field|command
specifier|private
name|String
index|[]
name|command
decl_stmt|;
DECL|field|output
specifier|private
name|StringBuffer
name|output
decl_stmt|;
DECL|method|ShellCommandExecutor (String[] execString)
specifier|public
name|ShellCommandExecutor
parameter_list|(
name|String
index|[]
name|execString
parameter_list|)
block|{
name|this
argument_list|(
name|execString
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ShellCommandExecutor (String[] execString, File dir)
specifier|public
name|ShellCommandExecutor
parameter_list|(
name|String
index|[]
name|execString
parameter_list|,
name|File
name|dir
parameter_list|)
block|{
name|this
argument_list|(
name|execString
argument_list|,
name|dir
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ShellCommandExecutor (String[] execString, File dir, Map<String, String> env)
specifier|public
name|ShellCommandExecutor
parameter_list|(
name|String
index|[]
name|execString
parameter_list|,
name|File
name|dir
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
name|this
argument_list|(
name|execString
argument_list|,
name|dir
argument_list|,
name|env
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new instance of the ShellCommandExecutor to execute a command.      *       * @param execString The command to execute with arguments      * @param dir If not-null, specifies the directory which should be set      *            as the current working directory for the command.      *            If null, the current working directory is not modified.      * @param env If not-null, environment of the command will include the      *            key-value pairs specified in the map. If null, the current      *            environment is not modified.      * @param timeout Specifies the time in milliseconds, after which the      *                command will be killed and the status marked as timedout.      *                If 0, the command will not be timed out.       */
DECL|method|ShellCommandExecutor (String[] execString, File dir, Map<String, String> env, long timeout)
specifier|public
name|ShellCommandExecutor
parameter_list|(
name|String
index|[]
name|execString
parameter_list|,
name|File
name|dir
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|,
name|long
name|timeout
parameter_list|)
block|{
name|command
operator|=
name|execString
operator|.
name|clone
argument_list|()
expr_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|setWorkingDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|env
operator|!=
literal|null
condition|)
block|{
name|setEnvironment
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
name|timeOutInterval
operator|=
name|timeout
expr_stmt|;
block|}
comment|/** Execute the shell command. */
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
DECL|method|getExecString ()
specifier|public
name|String
index|[]
name|getExecString
parameter_list|()
block|{
return|return
name|command
return|;
block|}
DECL|method|parseExecResult (BufferedReader lines)
specifier|protected
name|void
name|parseExecResult
parameter_list|(
name|BufferedReader
name|lines
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|512
index|]
decl_stmt|;
name|int
name|nRead
decl_stmt|;
while|while
condition|(
operator|(
name|nRead
operator|=
name|lines
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|output
operator|.
name|append
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|nRead
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Get the output of the shell command.*/
DECL|method|getOutput ()
specifier|public
name|String
name|getOutput
parameter_list|()
block|{
return|return
operator|(
name|output
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|output
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns the commands of this instance.      * Arguments with spaces in are presented with quotes round; other      * arguments are presented raw      *      * @return a string representation of the object.      */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
name|getExecString
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|args
control|)
block|{
if|if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * To check if the passed script to shell command executor timed out or    * not.    *     * @return if the script timed out.    */
DECL|method|isTimedOut ()
specifier|public
name|boolean
name|isTimedOut
parameter_list|()
block|{
return|return
name|timedOut
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Set if the command has timed out.    *     */
DECL|method|setTimedOut ()
specifier|private
name|void
name|setTimedOut
parameter_list|()
block|{
name|this
operator|.
name|timedOut
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**     * Static method to execute a shell command.     * Covers most of the simple cases without requiring the user to implement      * the<code>Shell</code> interface.    * @param cmd shell command to execute.    * @return the output of the executed command.    */
DECL|method|execCommand (String .... cmd)
specifier|public
specifier|static
name|String
name|execCommand
parameter_list|(
name|String
modifier|...
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|execCommand
argument_list|(
literal|null
argument_list|,
name|cmd
argument_list|,
literal|0L
argument_list|)
return|;
block|}
comment|/**     * Static method to execute a shell command.     * Covers most of the simple cases without requiring the user to implement      * the<code>Shell</code> interface.    * @param env the map of environment key=value    * @param cmd shell command to execute.    * @param timeout time in milliseconds after which script should be marked timeout    * @return the output of the executed command.o    */
DECL|method|execCommand (Map<String, String> env, String[] cmd, long timeout)
specifier|public
specifier|static
name|String
name|execCommand
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|,
name|String
index|[]
name|cmd
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|ShellCommandExecutor
name|exec
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|cmd
argument_list|,
literal|null
argument_list|,
name|env
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|exec
operator|.
name|execute
argument_list|()
expr_stmt|;
return|return
name|exec
operator|.
name|getOutput
argument_list|()
return|;
block|}
comment|/**     * Static method to execute a shell command.     * Covers most of the simple cases without requiring the user to implement      * the<code>Shell</code> interface.    * @param env the map of environment key=value    * @param cmd shell command to execute.    * @return the output of the executed command.    */
DECL|method|execCommand (Map<String,String> env, String ... cmd)
specifier|public
specifier|static
name|String
name|execCommand
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|,
name|String
modifier|...
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|execCommand
argument_list|(
name|env
argument_list|,
name|cmd
argument_list|,
literal|0L
argument_list|)
return|;
block|}
comment|/**    * Timer which is used to timeout scripts spawned off by shell.    */
DECL|class|ShellTimeoutTimerTask
specifier|private
specifier|static
class|class
name|ShellTimeoutTimerTask
extends|extends
name|TimerTask
block|{
DECL|field|shell
specifier|private
name|Shell
name|shell
decl_stmt|;
DECL|method|ShellTimeoutTimerTask (Shell shell)
specifier|public
name|ShellTimeoutTimerTask
parameter_list|(
name|Shell
name|shell
parameter_list|)
block|{
name|this
operator|.
name|shell
operator|=
name|shell
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Process
name|p
init|=
name|shell
operator|.
name|getProcess
argument_list|()
decl_stmt|;
try|try
block|{
name|p
operator|.
name|exitValue
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//Process has not terminated.
comment|//So check if it has completed
comment|//if not just destroy it.
if|if
condition|(
name|p
operator|!=
literal|null
operator|&&
operator|!
name|shell
operator|.
name|completed
operator|.
name|get
argument_list|()
condition|)
block|{
name|shell
operator|.
name|setTimedOut
argument_list|()
expr_stmt|;
name|p
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

