begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.applications.unmanagedamlauncher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|applications
operator|.
name|unmanagedamlauncher
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
name|net
operator|.
name|InetAddress
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
name|EnumSet
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
name|Set
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
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|GnuParser
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
name|cli
operator|.
name|HelpFormatter
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
name|cli
operator|.
name|Options
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
name|cli
operator|.
name|ParseException
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
operator|.
name|Environment
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetNewApplicationResponse
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationReport
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationSubmissionContext
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerLaunchContext
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|FinalApplicationStatus
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Priority
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|YarnApplicationState
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
name|yarn
operator|.
name|client
operator|.
name|YarnClientImpl
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRemoteException
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * The UnmanagedLauncher is a simple client that launches and unmanaged AM. An  * unmanagedAM is an AM that is not launched and managed by the RM. The client  * creates a new application on the RM and negotiates a new attempt id. Then it  * waits for the RM app state to reach be YarnApplicationState.ACCEPTED after  * which it spawns the AM in another process and passes it the container id via  * env variable Environment.CONTAINER_ID. The AM can be in any  * language. The AM can register with the RM using the attempt id obtained  * from the container id and proceed as normal.  * The client redirects app stdout and stderr to its own stdout and  * stderr and waits for the AM process to exit. Then it waits for the RM to  * report app completion.  */
end_comment

begin_class
DECL|class|UnmanagedAMLauncher
specifier|public
class|class
name|UnmanagedAMLauncher
block|{
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
name|UnmanagedAMLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|// Handle to talk to the Resource Manager/Applications Manager
DECL|field|rmClient
specifier|private
name|YarnClientImpl
name|rmClient
decl_stmt|;
comment|// Application master specific info to register a new Application with RM/ASM
DECL|field|appName
specifier|private
name|String
name|appName
init|=
literal|""
decl_stmt|;
comment|// App master priority
DECL|field|amPriority
specifier|private
name|int
name|amPriority
init|=
literal|0
decl_stmt|;
comment|// Queue for App master
DECL|field|amQueue
specifier|private
name|String
name|amQueue
init|=
literal|""
decl_stmt|;
comment|// cmd to start AM
DECL|field|amCmd
specifier|private
name|String
name|amCmd
init|=
literal|null
decl_stmt|;
comment|// set the classpath explicitly
DECL|field|classpath
specifier|private
name|String
name|classpath
init|=
literal|null
decl_stmt|;
DECL|field|amCompleted
specifier|private
specifier|volatile
name|boolean
name|amCompleted
init|=
literal|false
decl_stmt|;
comment|/**    * @param args    *          Command line arguments    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|UnmanagedAMLauncher
name|client
init|=
operator|new
name|UnmanagedAMLauncher
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing Client"
argument_list|)
expr_stmt|;
name|boolean
name|doRun
init|=
name|client
operator|.
name|init
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|doRun
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Error running Client"
argument_list|,
name|t
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
block|}
comment|/**    */
DECL|method|UnmanagedAMLauncher (Configuration conf)
specifier|public
name|UnmanagedAMLauncher
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Set up RPC
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|UnmanagedAMLauncher ()
specifier|public
name|UnmanagedAMLauncher
parameter_list|()
throws|throws
name|Exception
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|printUsage (Options opts)
specifier|private
name|void
name|printUsage
parameter_list|(
name|Options
name|opts
parameter_list|)
block|{
operator|new
name|HelpFormatter
argument_list|()
operator|.
name|printHelp
argument_list|(
literal|"Client"
argument_list|,
name|opts
argument_list|)
expr_stmt|;
block|}
DECL|method|init (String[] args)
specifier|public
name|boolean
name|init
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ParseException
block|{
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"appname"
argument_list|,
literal|true
argument_list|,
literal|"Application Name. Default value - UnmanagedAM"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"priority"
argument_list|,
literal|true
argument_list|,
literal|"Application Priority. Default 0"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"queue"
argument_list|,
literal|true
argument_list|,
literal|"RM Queue in which this application is to be submitted"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"master_memory"
argument_list|,
literal|true
argument_list|,
literal|"Amount of memory in MB to be requested to run the application master"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"cmd"
argument_list|,
literal|true
argument_list|,
literal|"command to start unmanaged AM (required)"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"classpath"
argument_list|,
literal|true
argument_list|,
literal|"additional classpath"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"help"
argument_list|,
literal|false
argument_list|,
literal|"Print usage"
argument_list|)
expr_stmt|;
name|CommandLine
name|cliParser
init|=
operator|new
name|GnuParser
argument_list|()
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No args specified for client to initialize"
argument_list|)
throw|;
block|}
if|if
condition|(
name|cliParser
operator|.
name|hasOption
argument_list|(
literal|"help"
argument_list|)
condition|)
block|{
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|appName
operator|=
name|cliParser
operator|.
name|getOptionValue
argument_list|(
literal|"appname"
argument_list|,
literal|"UnmanagedAM"
argument_list|)
expr_stmt|;
name|amPriority
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|cliParser
operator|.
name|getOptionValue
argument_list|(
literal|"priority"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|amQueue
operator|=
name|cliParser
operator|.
name|getOptionValue
argument_list|(
literal|"queue"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|classpath
operator|=
name|cliParser
operator|.
name|getOptionValue
argument_list|(
literal|"classpath"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|amCmd
operator|=
name|cliParser
operator|.
name|getOptionValue
argument_list|(
literal|"cmd"
argument_list|)
expr_stmt|;
if|if
condition|(
name|amCmd
operator|==
literal|null
condition|)
block|{
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No cmd specified for application master"
argument_list|)
throw|;
block|}
name|YarnConfiguration
name|yarnConf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rmClient
operator|=
operator|new
name|YarnClientImpl
argument_list|()
expr_stmt|;
name|rmClient
operator|.
name|init
argument_list|(
name|yarnConf
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|launchAM (ApplicationAttemptId attemptId)
specifier|public
name|void
name|launchAM
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
name|System
operator|.
name|getenv
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|envAMList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|setClasspath
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|env
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"CLASSPATH"
argument_list|)
condition|)
block|{
name|setClasspath
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|classpath
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|value
operator|+
name|File
operator|.
name|pathSeparator
operator|+
name|classpath
expr_stmt|;
block|}
block|}
name|envAMList
operator|.
name|add
argument_list|(
name|key
operator|+
literal|"="
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|setClasspath
operator|&&
name|classpath
operator|!=
literal|null
condition|)
block|{
name|envAMList
operator|.
name|add
argument_list|(
literal|"CLASSPATH="
operator|+
name|classpath
argument_list|)
expr_stmt|;
block|}
name|ContainerId
name|containerId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerId
operator|.
name|setApplicationAttemptId
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|containerId
operator|.
name|setId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|hostname
init|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
decl_stmt|;
name|envAMList
operator|.
name|add
argument_list|(
name|Environment
operator|.
name|CONTAINER_ID
operator|.
name|name
argument_list|()
operator|+
literal|"="
operator|+
name|containerId
argument_list|)
expr_stmt|;
name|envAMList
operator|.
name|add
argument_list|(
name|Environment
operator|.
name|NM_HOST
operator|.
name|name
argument_list|()
operator|+
literal|"="
operator|+
name|hostname
argument_list|)
expr_stmt|;
name|envAMList
operator|.
name|add
argument_list|(
name|Environment
operator|.
name|NM_HTTP_PORT
operator|.
name|name
argument_list|()
operator|+
literal|"=0"
argument_list|)
expr_stmt|;
name|envAMList
operator|.
name|add
argument_list|(
name|Environment
operator|.
name|NM_PORT
operator|.
name|name
argument_list|()
operator|+
literal|"=0"
argument_list|)
expr_stmt|;
name|envAMList
operator|.
name|add
argument_list|(
name|Environment
operator|.
name|LOCAL_DIRS
operator|.
name|name
argument_list|()
operator|+
literal|"= /tmp"
argument_list|)
expr_stmt|;
name|envAMList
operator|.
name|add
argument_list|(
name|ApplicationConstants
operator|.
name|APP_SUBMIT_TIME_ENV
operator|+
literal|"="
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|envAM
init|=
operator|new
name|String
index|[
name|envAMList
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|Process
name|amProc
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|amCmd
argument_list|,
name|envAMList
operator|.
name|toArray
argument_list|(
name|envAM
argument_list|)
argument_list|)
decl_stmt|;
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
name|amProc
operator|.
name|getErrorStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|inReader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|amProc
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|line
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
name|Thread
name|outThread
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
name|inReader
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|inReader
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
literal|"Error reading the out stream"
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
name|outThread
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
comment|// wait for the process to finish and check the exit code
try|try
block|{
name|int
name|exitCode
init|=
name|amProc
operator|.
name|waitFor
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"AM process exited with value: "
operator|+
name|exitCode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|amCompleted
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
comment|// make sure that the error thread exits
comment|// on Windows these threads sometimes get stuck and hang the execution
comment|// timeout and join later after destroying the process.
name|errThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|outThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|errReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|inReader
operator|.
name|close
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
name|info
argument_list|(
literal|"ShellExecutor: Interrupted while reading the error/out stream"
argument_list|,
name|ie
argument_list|)
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
literal|"Error while closing the error/out stream"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
name|amProc
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|boolean
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting Client"
argument_list|)
expr_stmt|;
comment|// Connect to ResourceManager
name|rmClient
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Get a new application id
name|GetNewApplicationResponse
name|newApp
init|=
name|rmClient
operator|.
name|getNewApplication
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|newApp
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
comment|// Create launch context for app master
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting up application submission context for ASM"
argument_list|)
expr_stmt|;
name|ApplicationSubmissionContext
name|appContext
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// set the application id
name|appContext
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
comment|// set the application name
name|appContext
operator|.
name|setApplicationName
argument_list|(
name|appName
argument_list|)
expr_stmt|;
comment|// Set the priority for the application master
name|Priority
name|pri
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|pri
operator|.
name|setPriority
argument_list|(
name|amPriority
argument_list|)
expr_stmt|;
name|appContext
operator|.
name|setPriority
argument_list|(
name|pri
argument_list|)
expr_stmt|;
comment|// Set the queue to which this application is to be submitted in the RM
name|appContext
operator|.
name|setQueue
argument_list|(
name|amQueue
argument_list|)
expr_stmt|;
comment|// Set up the container launch context for the application master
name|ContainerLaunchContext
name|amContainer
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|appContext
operator|.
name|setAMContainerSpec
argument_list|(
name|amContainer
argument_list|)
expr_stmt|;
comment|// unmanaged AM
name|appContext
operator|.
name|setUnmanagedAM
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting unmanaged AM"
argument_list|)
expr_stmt|;
comment|// Submit the application to the applications manager
name|LOG
operator|.
name|info
argument_list|(
literal|"Submitting application to ASM"
argument_list|)
expr_stmt|;
name|rmClient
operator|.
name|submitApplication
argument_list|(
name|appContext
argument_list|)
expr_stmt|;
comment|// Monitor the application to wait for launch state
name|ApplicationReport
name|appReport
init|=
name|monitorApplication
argument_list|(
name|appId
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|YarnApplicationState
operator|.
name|ACCEPTED
argument_list|)
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|appReport
operator|.
name|getCurrentApplicationAttemptId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Launching application with id: "
operator|+
name|attemptId
argument_list|)
expr_stmt|;
comment|// launch AM
name|launchAM
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
comment|// Monitor the application for end state
name|appReport
operator|=
name|monitorApplication
argument_list|(
name|appId
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|YarnApplicationState
operator|.
name|KILLED
argument_list|,
name|YarnApplicationState
operator|.
name|FAILED
argument_list|,
name|YarnApplicationState
operator|.
name|FINISHED
argument_list|)
argument_list|)
expr_stmt|;
name|YarnApplicationState
name|appState
init|=
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
decl_stmt|;
name|FinalApplicationStatus
name|appStatus
init|=
name|appReport
operator|.
name|getFinalApplicationStatus
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"App ended with state: "
operator|+
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
operator|+
literal|" and status: "
operator|+
name|appStatus
argument_list|)
expr_stmt|;
name|boolean
name|success
decl_stmt|;
if|if
condition|(
name|YarnApplicationState
operator|.
name|FINISHED
operator|==
name|appState
operator|&&
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
operator|==
name|appStatus
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Application has completed successfully."
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Application did finished unsuccessfully."
operator|+
literal|" YarnState="
operator|+
name|appState
operator|.
name|toString
argument_list|()
operator|+
literal|", FinalStatus="
operator|+
name|appStatus
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|success
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
finally|finally
block|{
name|rmClient
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Monitor the submitted application for completion. Kill application if time    * expires.    *     * @param appId    *          Application Id of application to be monitored    * @return true if application completed successfully    * @throws YarnRemoteException    */
DECL|method|monitorApplication (ApplicationId appId, Set<YarnApplicationState> finalState)
specifier|private
name|ApplicationReport
name|monitorApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|Set
argument_list|<
name|YarnApplicationState
argument_list|>
name|finalState
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|long
name|foundAMCompletedTime
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|timeToWaitMS
init|=
literal|10000
decl_stmt|;
name|StringBuilder
name|expectedFinalState
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|YarnApplicationState
name|state
range|:
name|finalState
control|)
block|{
if|if
condition|(
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
name|expectedFinalState
operator|.
name|append
argument_list|(
name|state
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expectedFinalState
operator|.
name|append
argument_list|(
literal|","
operator|+
name|state
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
literal|true
condition|)
block|{
comment|// Check app status every 1 second.
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Thread sleep in monitoring loop interrupted"
argument_list|)
expr_stmt|;
block|}
comment|// Get application report for the appId we are interested in
name|ApplicationReport
name|report
init|=
name|rmClient
operator|.
name|getApplicationReport
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got application report from ASM for"
operator|+
literal|", appId="
operator|+
name|appId
operator|.
name|getId
argument_list|()
operator|+
literal|", appAttemptId="
operator|+
name|report
operator|.
name|getCurrentApplicationAttemptId
argument_list|()
operator|+
literal|", clientToken="
operator|+
name|report
operator|.
name|getClientToken
argument_list|()
operator|+
literal|", appDiagnostics="
operator|+
name|report
operator|.
name|getDiagnostics
argument_list|()
operator|+
literal|", appMasterHost="
operator|+
name|report
operator|.
name|getHost
argument_list|()
operator|+
literal|", appQueue="
operator|+
name|report
operator|.
name|getQueue
argument_list|()
operator|+
literal|", appMasterRpcPort="
operator|+
name|report
operator|.
name|getRpcPort
argument_list|()
operator|+
literal|", appStartTime="
operator|+
name|report
operator|.
name|getStartTime
argument_list|()
operator|+
literal|", yarnAppState="
operator|+
name|report
operator|.
name|getYarnApplicationState
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|", distributedFinalState="
operator|+
name|report
operator|.
name|getFinalApplicationStatus
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|", appTrackingUrl="
operator|+
name|report
operator|.
name|getTrackingUrl
argument_list|()
operator|+
literal|", appUser="
operator|+
name|report
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|YarnApplicationState
name|state
init|=
name|report
operator|.
name|getYarnApplicationState
argument_list|()
decl_stmt|;
if|if
condition|(
name|finalState
operator|.
name|contains
argument_list|(
name|state
argument_list|)
condition|)
block|{
return|return
name|report
return|;
block|}
comment|// wait for 10 seconds after process has completed for app report to
comment|// come back
if|if
condition|(
name|amCompleted
condition|)
block|{
if|if
condition|(
name|foundAMCompletedTime
operator|==
literal|0
condition|)
block|{
name|foundAMCompletedTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|foundAMCompletedTime
operator|)
operator|>
name|timeToWaitMS
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Waited "
operator|+
name|timeToWaitMS
operator|/
literal|1000
operator|+
literal|" seconds after process completed for AppReport"
operator|+
literal|" to reach desired final state. Not waiting anymore."
operator|+
literal|"CurrentState = "
operator|+
name|state
operator|+
literal|", ExpectedStates = "
operator|+
name|expectedFinalState
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to receive final expected state"
operator|+
literal|" in ApplicationReport"
operator|+
literal|", CurrentState="
operator|+
name|state
operator|+
literal|", ExpectedStates="
operator|+
name|expectedFinalState
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

