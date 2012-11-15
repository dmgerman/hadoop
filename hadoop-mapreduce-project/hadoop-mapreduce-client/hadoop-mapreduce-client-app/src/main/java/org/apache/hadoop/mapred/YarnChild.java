begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|OutputStream
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
name|net
operator|.
name|InetSocketAddress
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|FSError
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
name|LocalDirAllocator
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
name|permission
operator|.
name|FsPermission
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
name|io
operator|.
name|IOUtils
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
name|ipc
operator|.
name|RPC
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
name|mapreduce
operator|.
name|MRConfig
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|TaskType
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
name|mapreduce
operator|.
name|filecache
operator|.
name|DistributedCache
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
name|mapreduce
operator|.
name|security
operator|.
name|TokenCache
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenIdentifier
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenSecretManager
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|metrics2
operator|.
name|source
operator|.
name|JvmMetrics
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
name|net
operator|.
name|NetUtils
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
name|security
operator|.
name|Credentials
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
name|security
operator|.
name|SecurityUtil
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|DiskChecker
operator|.
name|DiskErrorException
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
name|yarn
operator|.
name|YarnUncaughtExceptionHandler
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
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_comment
comment|/**  * The main() for MapReduce task processes.  */
end_comment

begin_class
DECL|class|YarnChild
class|class
name|YarnChild
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
name|YarnChild
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|taskid
specifier|static
specifier|volatile
name|TaskAttemptID
name|taskid
init|=
literal|null
decl_stmt|;
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
throws|throws
name|Throwable
block|{
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
operator|new
name|YarnUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Child starting"
argument_list|)
expr_stmt|;
specifier|final
name|JobConf
name|defaultConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|defaultConf
operator|.
name|addResource
argument_list|(
name|MRJobConfig
operator|.
name|JOB_CONF_FILE
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|defaultConf
argument_list|)
expr_stmt|;
name|String
name|host
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|address
init|=
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
decl_stmt|;
specifier|final
name|TaskAttemptID
name|firstTaskid
init|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|int
name|jvmIdInt
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
decl_stmt|;
name|JVMId
name|jvmId
init|=
operator|new
name|JVMId
argument_list|(
name|firstTaskid
operator|.
name|getJobID
argument_list|()
argument_list|,
name|firstTaskid
operator|.
name|getTaskType
argument_list|()
operator|==
name|TaskType
operator|.
name|MAP
argument_list|,
name|jvmIdInt
argument_list|)
decl_stmt|;
comment|// initialize metrics
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
name|StringUtils
operator|.
name|camelize
argument_list|(
name|firstTaskid
operator|.
name|getTaskType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|+
literal|"Task"
argument_list|)
expr_stmt|;
comment|// Security framework already loaded the tokens into current ugi
name|Credentials
name|credentials
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing with tokens:"
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|credentials
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
comment|// Create TaskUmbilicalProtocol as actual task owner.
name|UserGroupInformation
name|taskOwner
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|firstTaskid
operator|.
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|jt
init|=
name|TokenCache
operator|.
name|getJobToken
argument_list|(
name|credentials
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|jt
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|taskOwner
operator|.
name|addToken
argument_list|(
name|jt
argument_list|)
expr_stmt|;
specifier|final
name|TaskUmbilicalProtocol
name|umbilical
init|=
name|taskOwner
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|TaskUmbilicalProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TaskUmbilicalProtocol
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|TaskUmbilicalProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|TaskUmbilicalProtocol
operator|.
name|class
argument_list|,
name|TaskUmbilicalProtocol
operator|.
name|versionID
argument_list|,
name|address
argument_list|,
name|defaultConf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// report non-pid to application master
name|JvmContext
name|context
init|=
operator|new
name|JvmContext
argument_list|(
name|jvmId
argument_list|,
literal|"-1000"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"PID: "
operator|+
name|System
operator|.
name|getenv
argument_list|()
operator|.
name|get
argument_list|(
literal|"JVM_PID"
argument_list|)
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
literal|null
decl_stmt|;
name|UserGroupInformation
name|childUGI
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|idleLoopCount
init|=
literal|0
decl_stmt|;
name|JvmTask
name|myTask
init|=
literal|null
decl_stmt|;
empty_stmt|;
comment|// poll for new task
for|for
control|(
name|int
name|idle
init|=
literal|0
init|;
literal|null
operator|==
name|myTask
condition|;
operator|++
name|idle
control|)
block|{
name|long
name|sleepTimeMilliSecs
init|=
name|Math
operator|.
name|min
argument_list|(
name|idle
operator|*
literal|500
argument_list|,
literal|1500
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleeping for "
operator|+
name|sleepTimeMilliSecs
operator|+
literal|"ms before retrying again. Got null now."
argument_list|)
expr_stmt|;
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|sleepTimeMilliSecs
argument_list|)
expr_stmt|;
name|myTask
operator|=
name|umbilical
operator|.
name|getTask
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|myTask
operator|.
name|shouldDie
argument_list|()
condition|)
block|{
return|return;
block|}
name|task
operator|=
name|myTask
operator|.
name|getTask
argument_list|()
expr_stmt|;
name|YarnChild
operator|.
name|taskid
operator|=
name|task
operator|.
name|getTaskID
argument_list|()
expr_stmt|;
comment|// Create the job-conf and set credentials
specifier|final
name|JobConf
name|job
init|=
name|configureTask
argument_list|(
name|task
argument_list|,
name|credentials
argument_list|,
name|jt
argument_list|)
decl_stmt|;
comment|// Initiate Java VM metrics
name|JvmMetrics
operator|.
name|initSingleton
argument_list|(
name|jvmId
operator|.
name|toString
argument_list|()
argument_list|,
name|job
operator|.
name|getSessionId
argument_list|()
argument_list|)
expr_stmt|;
name|childUGI
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|System
operator|.
name|getenv
argument_list|(
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|USER
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add tokens to new user so that it may execute its task correctly.
name|childUGI
operator|.
name|addCredentials
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
comment|// Create a final reference to the task for the doAs block
specifier|final
name|Task
name|taskFinal
init|=
name|task
decl_stmt|;
name|childUGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
comment|// use job-specified working directory
name|FileSystem
operator|.
name|get
argument_list|(
name|job
argument_list|)
operator|.
name|setWorkingDirectory
argument_list|(
name|job
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|taskFinal
operator|.
name|run
argument_list|(
name|job
argument_list|,
name|umbilical
argument_list|)
expr_stmt|;
comment|// run the task
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FSError
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"FSError from child"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|umbilical
operator|.
name|fsError
argument_list|(
name|taskid
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception running child : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|exception
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
comment|// do cleanup for the task
if|if
condition|(
name|childUGI
operator|==
literal|null
condition|)
block|{
comment|// no need to job into doAs block
name|task
operator|.
name|taskCleanup
argument_list|(
name|umbilical
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Task
name|taskFinal
init|=
name|task
decl_stmt|;
name|childUGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|taskFinal
operator|.
name|taskCleanup
argument_list|(
name|umbilical
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception cleaning up: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Report back any failures, for diagnostic purposes
if|if
condition|(
name|taskid
operator|!=
literal|null
condition|)
block|{
name|umbilical
operator|.
name|fatalError
argument_list|(
name|taskid
argument_list|,
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|exception
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Error running child : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|throwable
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|taskid
operator|!=
literal|null
condition|)
block|{
name|Throwable
name|tCause
init|=
name|throwable
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|String
name|cause
init|=
name|tCause
operator|==
literal|null
condition|?
name|throwable
operator|.
name|getMessage
argument_list|()
else|:
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|tCause
argument_list|)
decl_stmt|;
name|umbilical
operator|.
name|fatalError
argument_list|(
name|taskid
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|umbilical
argument_list|)
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// Shutting down log4j of the child-vm...
comment|// This assumes that on return from Task.run()
comment|// there is no more logging done.
name|LogManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Configure mapred-local dirs. This config is used by the task for finding    * out an output directory.    * @throws IOException     */
DECL|method|configureLocalDirs (Task task, JobConf job)
specifier|private
specifier|static
name|void
name|configureLocalDirs
parameter_list|(
name|Task
name|task
parameter_list|,
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|localSysDirs
init|=
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|System
operator|.
name|getenv
argument_list|(
name|ApplicationConstants
operator|.
name|LOCAL_DIR_ENV
argument_list|)
argument_list|)
decl_stmt|;
name|job
operator|.
name|setStrings
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|,
name|localSysDirs
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
operator|+
literal|" for child: "
operator|+
name|job
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|)
argument_list|)
expr_stmt|;
name|LocalDirAllocator
name|lDirAlloc
init|=
operator|new
name|LocalDirAllocator
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|)
decl_stmt|;
name|Path
name|workDir
init|=
literal|null
decl_stmt|;
comment|// First, try to find the JOB_LOCAL_DIR on this host.
try|try
block|{
name|workDir
operator|=
name|lDirAlloc
operator|.
name|getLocalPathToRead
argument_list|(
literal|"work"
argument_list|,
name|job
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskErrorException
name|e
parameter_list|)
block|{
comment|// DiskErrorException means dir not found. If not found, it will
comment|// be created below.
block|}
if|if
condition|(
name|workDir
operator|==
literal|null
condition|)
block|{
comment|// JOB_LOCAL_DIR doesn't exist on this host -- Create it.
name|workDir
operator|=
name|lDirAlloc
operator|.
name|getLocalPathForWrite
argument_list|(
literal|"work"
argument_list|,
name|job
argument_list|)
expr_stmt|;
name|FileSystem
name|lfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|job
argument_list|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|boolean
name|madeDir
init|=
literal|false
decl_stmt|;
try|try
block|{
name|madeDir
operator|=
name|lfs
operator|.
name|mkdirs
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|e
parameter_list|)
block|{
comment|// Since all tasks will be running in their own JVM, the race condition
comment|// exists where multiple tasks could be trying to create this directory
comment|// at the same time. If this task loses the race, it's okay because
comment|// the directory already exists.
name|madeDir
operator|=
literal|true
expr_stmt|;
name|workDir
operator|=
name|lDirAlloc
operator|.
name|getLocalPathToRead
argument_list|(
literal|"work"
argument_list|,
name|job
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|madeDir
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|workDir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|job
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|JOB_LOCAL_DIR
argument_list|,
name|workDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|configureTask (Task task, Credentials credentials, Token<JobTokenIdentifier> jt)
specifier|private
specifier|static
name|JobConf
name|configureTask
parameter_list|(
name|Task
name|task
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|jt
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|MRJobConfig
operator|.
name|JOB_CONF_FILE
argument_list|)
decl_stmt|;
name|job
operator|.
name|setCredentials
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
name|String
name|appAttemptIdEnv
init|=
name|System
operator|.
name|getenv
argument_list|(
name|MRJobConfig
operator|.
name|APPLICATION_ATTEMPT_ID_ENV
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"APPLICATION_ATTEMPT_ID: "
operator|+
name|appAttemptIdEnv
argument_list|)
expr_stmt|;
comment|// Set it in conf, so as to be able to be used the the OutputCommitter.
name|job
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|APPLICATION_ATTEMPT_ID
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|appAttemptIdEnv
argument_list|)
argument_list|)
expr_stmt|;
comment|// set tcp nodelay
name|job
operator|.
name|setBoolean
argument_list|(
literal|"ipc.client.tcpnodelay"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|job
operator|.
name|setClass
argument_list|(
name|MRConfig
operator|.
name|TASK_LOCAL_OUTPUT_CLASS
argument_list|,
name|YarnOutputFiles
operator|.
name|class
argument_list|,
name|MapOutputFile
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// set the jobTokenFile into task
name|task
operator|.
name|setJobTokenSecret
argument_list|(
name|JobTokenSecretManager
operator|.
name|createSecretKey
argument_list|(
name|jt
operator|.
name|getPassword
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// setup the child's MRConfig.LOCAL_DIR.
name|configureLocalDirs
argument_list|(
name|task
argument_list|,
name|job
argument_list|)
expr_stmt|;
comment|// setup the child's attempt directories
comment|// Do the task-type specific localization
name|task
operator|.
name|localizeConfiguration
argument_list|(
name|job
argument_list|)
expr_stmt|;
comment|// Set up the DistributedCache related configs
name|setupDistributedCacheConfig
argument_list|(
name|job
argument_list|)
expr_stmt|;
comment|// Overwrite the localized task jobconf which is linked to in the current
comment|// work-dir.
name|Path
name|localTaskFile
init|=
operator|new
name|Path
argument_list|(
name|MRJobConfig
operator|.
name|JOB_CONF_FILE
argument_list|)
decl_stmt|;
name|writeLocalJobFile
argument_list|(
name|localTaskFile
argument_list|,
name|job
argument_list|)
expr_stmt|;
name|task
operator|.
name|setJobFile
argument_list|(
name|localTaskFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|task
operator|.
name|setConf
argument_list|(
name|job
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
comment|/**    * Set up the DistributedCache related configs to make    * {@link DistributedCache#getLocalCacheFiles(Configuration)}    * and    * {@link DistributedCache#getLocalCacheArchives(Configuration)}    * working.    * @param job    * @throws IOException    */
DECL|method|setupDistributedCacheConfig (final JobConf job)
specifier|private
specifier|static
name|void
name|setupDistributedCacheConfig
parameter_list|(
specifier|final
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|localWorkDir
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"PWD"
argument_list|)
decl_stmt|;
comment|//        ^ ^ all symlinks are created in the current work-dir
comment|// Update the configuration object with localized archives.
name|URI
index|[]
name|cacheArchives
init|=
name|DistributedCache
operator|.
name|getCacheArchives
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheArchives
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|localArchives
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cacheArchives
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|URI
name|u
init|=
name|cacheArchives
index|[
name|i
index|]
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|u
argument_list|)
decl_stmt|;
name|Path
name|name
init|=
operator|new
name|Path
argument_list|(
operator|(
literal|null
operator|==
name|u
operator|.
name|getFragment
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getName
argument_list|()
else|:
name|u
operator|.
name|getFragment
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|linkName
init|=
name|name
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|localArchives
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
name|localWorkDir
argument_list|,
name|linkName
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|localArchives
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|job
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_LOCALARCHIVES
argument_list|,
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|localArchives
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|localArchives
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Update the configuration object with localized files.
name|URI
index|[]
name|cacheFiles
init|=
name|DistributedCache
operator|.
name|getCacheFiles
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheFiles
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|localFiles
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cacheFiles
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|URI
name|u
init|=
name|cacheFiles
index|[
name|i
index|]
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|u
argument_list|)
decl_stmt|;
name|Path
name|name
init|=
operator|new
name|Path
argument_list|(
operator|(
literal|null
operator|==
name|u
operator|.
name|getFragment
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getName
argument_list|()
else|:
name|u
operator|.
name|getFragment
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|linkName
init|=
name|name
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|localFiles
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
name|localWorkDir
argument_list|,
name|linkName
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|localFiles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|job
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_LOCALFILES
argument_list|,
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|localFiles
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|localFiles
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|urw_gr
specifier|private
specifier|static
specifier|final
name|FsPermission
name|urw_gr
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0640
argument_list|)
decl_stmt|;
comment|/**    * Write the task specific job-configuration file.    * @throws IOException    */
DECL|method|writeLocalJobFile (Path jobFile, JobConf conf)
specifier|private
specifier|static
name|void
name|writeLocalJobFile
parameter_list|(
name|Path
name|jobFile
parameter_list|,
name|JobConf
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|localFs
operator|.
name|delete
argument_list|(
name|jobFile
argument_list|)
expr_stmt|;
name|OutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
name|FileSystem
operator|.
name|create
argument_list|(
name|localFs
argument_list|,
name|jobFile
argument_list|,
name|urw_gr
argument_list|)
expr_stmt|;
name|conf
operator|.
name|writeXml
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

