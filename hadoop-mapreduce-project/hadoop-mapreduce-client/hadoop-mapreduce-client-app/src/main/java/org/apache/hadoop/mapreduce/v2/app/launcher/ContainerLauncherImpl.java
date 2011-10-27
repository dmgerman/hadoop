begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.launcher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|launcher
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|BlockingQueue
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
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|LinkedBlockingQueue
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
name|TimeUnit
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
name|io
operator|.
name|Text
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
name|mapred
operator|.
name|ShuffleHandler
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptContainerLaunchedEvent
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
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptDiagnosticsUpdateEvent
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
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEvent
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
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEventType
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
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|ContainerAllocator
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
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|ContainerAllocatorEvent
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
name|YarnException
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
name|ContainerManager
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
name|StartContainerRequest
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
name|StartContainerResponse
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
name|StopContainerRequest
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
name|ContainerToken
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|ipc
operator|.
name|YarnRPC
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_comment
comment|/**  * This class is responsible for launching of containers.  */
end_comment

begin_class
DECL|class|ContainerLauncherImpl
specifier|public
class|class
name|ContainerLauncherImpl
extends|extends
name|AbstractService
implements|implements
name|ContainerLauncher
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
name|ContainerLauncherImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nmTimeOut
name|int
name|nmTimeOut
decl_stmt|;
DECL|field|context
specifier|private
name|AppContext
name|context
decl_stmt|;
DECL|field|launcherPool
specifier|private
name|ThreadPoolExecutor
name|launcherPool
decl_stmt|;
DECL|field|INITIAL_POOL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_POOL_SIZE
init|=
literal|10
decl_stmt|;
DECL|field|limitOnPoolSize
specifier|private
name|int
name|limitOnPoolSize
decl_stmt|;
DECL|field|eventHandlingThread
specifier|private
name|Thread
name|eventHandlingThread
decl_stmt|;
DECL|field|eventQueue
specifier|private
name|BlockingQueue
argument_list|<
name|ContainerLauncherEvent
argument_list|>
name|eventQueue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|ContainerLauncherEvent
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|recordFactory
specifier|private
name|RecordFactory
name|recordFactory
decl_stmt|;
comment|//have a cache/map of UGIs so as to avoid creating too many RPC
comment|//client connection objects to the same NodeManager
DECL|field|ugiMap
specifier|private
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|UserGroupInformation
argument_list|>
name|ugiMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|UserGroupInformation
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ContainerLauncherImpl (AppContext context)
specifier|public
name|ContainerLauncherImpl
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|ContainerLauncherImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|recordFactory
operator|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|limitOnPoolSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_CONTAINERLAUNCHER_THREAD_COUNT_LIMIT
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_AM_CONTAINERLAUNCHER_THREAD_COUNT_LIMIT
argument_list|)
expr_stmt|;
name|this
operator|.
name|nmTimeOut
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|ContainerLauncher
operator|.
name|MR_AM_NM_COMMAND_TIMEOUT
argument_list|,
name|ContainerLauncher
operator|.
name|DEFAULT_NM__COMMAND_TIMEOUT
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"ContainerLauncher #%d"
argument_list|)
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Start with a default core-pool size of 10 and change it dynamically.
name|launcherPool
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|INITIAL_POOL_SIZE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|tf
argument_list|)
expr_stmt|;
name|eventHandlingThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ContainerLauncherEvent
name|event
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
try|try
block|{
name|event
operator|=
name|eventQueue
operator|.
name|take
argument_list|()
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
name|error
argument_list|(
literal|"Returning, interrupted : "
operator|+
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|poolSize
init|=
name|launcherPool
operator|.
name|getCorePoolSize
argument_list|()
decl_stmt|;
comment|// See if we need up the pool size only if haven't reached the
comment|// maximum limit yet.
if|if
condition|(
name|poolSize
operator|!=
name|limitOnPoolSize
condition|)
block|{
comment|// nodes where containers will run at *this* point of time. This is
comment|// *not* the cluster size and doesn't need to be.
name|int
name|numNodes
init|=
name|ugiMap
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|idealPoolSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|limitOnPoolSize
argument_list|,
name|numNodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|poolSize
operator|<=
name|idealPoolSize
condition|)
block|{
comment|// Bump up the pool size to idealPoolSize+INITIAL_POOL_SIZE, the
comment|// later is just a buffer so we are not always increasing the
comment|// pool-size
name|int
name|newPoolSize
init|=
name|idealPoolSize
operator|+
name|INITIAL_POOL_SIZE
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Setting pool size to "
operator|+
name|newPoolSize
argument_list|)
expr_stmt|;
name|launcherPool
operator|.
name|setCorePoolSize
argument_list|(
name|newPoolSize
argument_list|)
expr_stmt|;
block|}
block|}
comment|// the events from the queue are handled in parallel
comment|// using a thread pool
name|launcherPool
operator|.
name|execute
argument_list|(
operator|new
name|EventProcessor
argument_list|(
name|event
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: Group launching of multiple containers to a single
comment|// NodeManager into a single connection
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|eventHandlingThread
operator|.
name|setName
argument_list|(
literal|"ContainerLauncher Event Handler"
argument_list|)
expr_stmt|;
name|eventHandlingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|eventHandlingThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|launcherPool
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getCMProxy ( final String containerManagerBindAddr, ContainerToken containerToken)
specifier|protected
name|ContainerManager
name|getCMProxy
parameter_list|(
specifier|final
name|String
name|containerManagerBindAddr
parameter_list|,
name|ContainerToken
name|containerToken
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|Token
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
argument_list|(
name|containerToken
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|containerToken
operator|.
name|getPassword
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|containerToken
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|containerToken
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// the user in createRemoteUser in this context is not important
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|containerManagerBindAddr
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|ugiMap
operator|.
name|putIfAbsent
argument_list|(
name|containerManagerBindAddr
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
name|user
operator|=
name|ugiMap
operator|.
name|get
argument_list|(
name|containerManagerBindAddr
argument_list|)
expr_stmt|;
block|}
name|ContainerManager
name|proxy
init|=
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|ContainerManager
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContainerManager
name|run
parameter_list|()
block|{
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|ContainerManager
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|containerManagerBindAddr
argument_list|)
argument_list|,
name|getConfig
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
DECL|class|CommandTimer
specifier|private
specifier|static
class|class
name|CommandTimer
extends|extends
name|TimerTask
block|{
DECL|field|commandThread
specifier|private
specifier|final
name|Thread
name|commandThread
decl_stmt|;
DECL|field|event
specifier|protected
specifier|final
name|ContainerLauncherEvent
name|event
decl_stmt|;
DECL|field|message
specifier|protected
specifier|final
name|String
name|message
decl_stmt|;
DECL|method|CommandTimer (Thread thread, ContainerLauncherEvent event)
specifier|public
name|CommandTimer
parameter_list|(
name|Thread
name|thread
parameter_list|,
name|ContainerLauncherEvent
name|event
parameter_list|)
block|{
name|this
operator|.
name|commandThread
operator|=
name|thread
expr_stmt|;
name|this
operator|.
name|event
operator|=
name|event
expr_stmt|;
name|this
operator|.
name|message
operator|=
literal|"Couldn't complete "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" on "
operator|+
name|event
operator|.
name|getContainerID
argument_list|()
operator|+
literal|"/"
operator|+
name|event
operator|.
name|getTaskAttemptID
argument_list|()
operator|+
literal|". Interrupting and returning"
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
name|LOG
operator|.
name|warn
argument_list|(
name|this
operator|.
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|commandThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Setup and start the container on remote nodemanager.    */
DECL|class|EventProcessor
specifier|private
class|class
name|EventProcessor
implements|implements
name|Runnable
block|{
DECL|field|event
specifier|private
name|ContainerLauncherEvent
name|event
decl_stmt|;
DECL|method|EventProcessor (ContainerLauncherEvent event)
name|EventProcessor
parameter_list|(
name|ContainerLauncherEvent
name|event
parameter_list|)
block|{
name|this
operator|.
name|event
operator|=
name|event
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Processing the event "
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Load ContainerManager tokens before creating a connection.
comment|// TODO: Do it only once per NodeManager.
specifier|final
name|String
name|containerManagerBindAddr
init|=
name|event
operator|.
name|getContainerMgrAddress
argument_list|()
decl_stmt|;
name|ContainerId
name|containerID
init|=
name|event
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|ContainerToken
name|containerToken
init|=
name|event
operator|.
name|getContainerToken
argument_list|()
decl_stmt|;
name|TaskAttemptId
name|taskAttemptID
init|=
name|event
operator|.
name|getTaskAttemptID
argument_list|()
decl_stmt|;
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|(
literal|true
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|CONTAINER_REMOTE_LAUNCH
case|:
name|ContainerRemoteLaunchEvent
name|launchEvent
init|=
operator|(
name|ContainerRemoteLaunchEvent
operator|)
name|event
decl_stmt|;
try|try
block|{
name|timer
operator|.
name|schedule
argument_list|(
operator|new
name|CommandTimer
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|event
argument_list|)
argument_list|,
name|nmTimeOut
argument_list|)
expr_stmt|;
name|ContainerManager
name|proxy
init|=
name|getCMProxy
argument_list|(
name|containerManagerBindAddr
argument_list|,
name|containerToken
argument_list|)
decl_stmt|;
comment|// Interruped during getProxy, but that didn't throw exception
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
comment|// The timer cancelled the command in the mean while.
name|String
name|message
init|=
literal|"Start-container for "
operator|+
name|event
operator|.
name|getContainerID
argument_list|()
operator|+
literal|" got interrupted. Returning."
decl_stmt|;
name|sendContainerLaunchFailedMsg
argument_list|(
name|taskAttemptID
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Construct the actual Container
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|launchEvent
operator|.
name|getContainer
argument_list|()
decl_stmt|;
comment|// Now launch the actual container
name|StartContainerRequest
name|startRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|startRequest
operator|.
name|setContainerLaunchContext
argument_list|(
name|containerLaunchContext
argument_list|)
expr_stmt|;
name|StartContainerResponse
name|response
init|=
name|proxy
operator|.
name|startContainer
argument_list|(
name|startRequest
argument_list|)
decl_stmt|;
comment|// container started properly. Stop the timer
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
comment|// The timer cancelled the command in the mean while, but
comment|// startContainer didn't throw exception
name|String
name|message
init|=
literal|"Start-container for "
operator|+
name|event
operator|.
name|getContainerID
argument_list|()
operator|+
literal|" got interrupted. Returning."
decl_stmt|;
name|sendContainerLaunchFailedMsg
argument_list|(
name|taskAttemptID
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return;
block|}
name|ByteBuffer
name|portInfo
init|=
name|response
operator|.
name|getServiceResponse
argument_list|(
name|ShuffleHandler
operator|.
name|MAPREDUCE_SHUFFLE_SERVICEID
argument_list|)
decl_stmt|;
name|int
name|port
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|portInfo
operator|!=
literal|null
condition|)
block|{
name|port
operator|=
name|ShuffleHandler
operator|.
name|deserializeMetaData
argument_list|(
name|portInfo
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Shuffle port returned by ContainerManager for "
operator|+
name|taskAttemptID
operator|+
literal|" : "
operator|+
name|port
argument_list|)
expr_stmt|;
if|if
condition|(
name|port
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Invalid shuffle port number "
operator|+
name|port
operator|+
literal|" returned for "
operator|+
name|taskAttemptID
argument_list|)
throw|;
block|}
comment|// after launching, send launched event to task attempt to move
comment|// it from ASSIGNED to RUNNING state
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptContainerLaunchedEvent
argument_list|(
name|taskAttemptID
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Container launch failed for "
operator|+
name|containerID
operator|+
literal|" : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|sendContainerLaunchFailedMsg
argument_list|(
name|taskAttemptID
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|CONTAINER_REMOTE_CLEANUP
case|:
comment|// We will have to remove the launch (meant "cleanup"? FIXME) event if it is still in eventQueue
comment|// and not yet processed
if|if
condition|(
name|eventQueue
operator|.
name|contains
argument_list|(
name|event
argument_list|)
condition|)
block|{
name|eventQueue
operator|.
name|remove
argument_list|(
name|event
argument_list|)
expr_stmt|;
comment|// TODO: Any synchro needed?
comment|//deallocate the container
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerAllocatorEvent
argument_list|(
name|taskAttemptID
argument_list|,
name|ContainerAllocator
operator|.
name|EventType
operator|.
name|CONTAINER_DEALLOCATE
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|timer
operator|.
name|schedule
argument_list|(
operator|new
name|CommandTimer
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|event
argument_list|)
argument_list|,
name|nmTimeOut
argument_list|)
expr_stmt|;
name|ContainerManager
name|proxy
init|=
name|getCMProxy
argument_list|(
name|containerManagerBindAddr
argument_list|,
name|containerToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
comment|// The timer cancelled the command in the mean while. No need to
comment|// return, send cleanedup event anyways.
name|LOG
operator|.
name|info
argument_list|(
literal|"Stop-container for "
operator|+
name|event
operator|.
name|getContainerID
argument_list|()
operator|+
literal|" got interrupted."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO:check whether container is launched
comment|// kill the remote container if already launched
name|StopContainerRequest
name|stopRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StopContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|stopRequest
operator|.
name|setContainerId
argument_list|(
name|event
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|stopContainer
argument_list|(
name|stopRequest
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// ignore the cleanup failure
name|String
name|message
init|=
literal|"cleanup failed for container "
operator|+
name|event
operator|.
name|getContainerID
argument_list|()
operator|+
literal|" : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptDiagnosticsUpdateEvent
argument_list|(
name|taskAttemptID
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
comment|// after killing, send killed event to taskattempt
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|event
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_CONTAINER_CLEANED
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|sendContainerLaunchFailedMsg (TaskAttemptId taskAttemptID, String message)
name|void
name|sendContainerLaunchFailedMsg
parameter_list|(
name|TaskAttemptId
name|taskAttemptID
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptDiagnosticsUpdateEvent
argument_list|(
name|taskAttemptID
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|taskAttemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_CONTAINER_LAUNCH_FAILED
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (ContainerLauncherEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainerLauncherEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|eventQueue
operator|.
name|put
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

