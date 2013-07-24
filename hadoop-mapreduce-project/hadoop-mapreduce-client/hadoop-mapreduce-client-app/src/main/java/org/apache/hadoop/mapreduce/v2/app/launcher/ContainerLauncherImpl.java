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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|service
operator|.
name|AbstractService
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
name|StartContainersRequest
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
name|StartContainersResponse
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
name|StopContainersRequest
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
name|StopContainersResponse
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
name|client
operator|.
name|api
operator|.
name|impl
operator|.
name|ContainerManagementProtocolProxy
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
name|api
operator|.
name|impl
operator|.
name|ContainerManagementProtocolProxy
operator|.
name|ContainerManagementProtocolProxyData
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
name|YarnRuntimeException
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
DECL|field|containers
specifier|private
name|ConcurrentHashMap
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|containers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|AppContext
name|context
decl_stmt|;
DECL|field|launcherPool
specifier|protected
name|ThreadPoolExecutor
name|launcherPool
decl_stmt|;
DECL|field|INITIAL_POOL_SIZE
specifier|protected
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
specifier|protected
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
DECL|field|stopped
specifier|private
specifier|final
name|AtomicBoolean
name|stopped
decl_stmt|;
DECL|field|cmProxy
specifier|private
name|ContainerManagementProtocolProxy
name|cmProxy
decl_stmt|;
DECL|method|getContainer (ContainerLauncherEvent event)
specifier|private
name|Container
name|getContainer
parameter_list|(
name|ContainerLauncherEvent
name|event
parameter_list|)
block|{
name|ContainerId
name|id
init|=
name|event
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|Container
name|c
init|=
name|containers
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|c
operator|=
operator|new
name|Container
argument_list|(
name|event
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|,
name|event
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|event
operator|.
name|getContainerMgrAddress
argument_list|()
argument_list|)
expr_stmt|;
name|Container
name|old
init|=
name|containers
operator|.
name|putIfAbsent
argument_list|(
name|id
argument_list|,
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|c
operator|=
name|old
expr_stmt|;
block|}
block|}
return|return
name|c
return|;
block|}
DECL|method|removeContainerIfDone (ContainerId id)
specifier|private
name|void
name|removeContainerIfDone
parameter_list|(
name|ContainerId
name|id
parameter_list|)
block|{
name|Container
name|c
init|=
name|containers
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
operator|&&
name|c
operator|.
name|isCompletelyDone
argument_list|()
condition|)
block|{
name|containers
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
DECL|enum|ContainerState
specifier|private
specifier|static
enum|enum
name|ContainerState
block|{
DECL|enumConstant|PREP
DECL|enumConstant|FAILED
DECL|enumConstant|RUNNING
DECL|enumConstant|DONE
DECL|enumConstant|KILLED_BEFORE_LAUNCH
name|PREP
block|,
name|FAILED
block|,
name|RUNNING
block|,
name|DONE
block|,
name|KILLED_BEFORE_LAUNCH
block|}
DECL|class|Container
specifier|private
class|class
name|Container
block|{
DECL|field|state
specifier|private
name|ContainerState
name|state
decl_stmt|;
comment|// store enough information to be able to cleanup the container
DECL|field|taskAttemptID
specifier|private
name|TaskAttemptId
name|taskAttemptID
decl_stmt|;
DECL|field|containerID
specifier|private
name|ContainerId
name|containerID
decl_stmt|;
DECL|field|containerMgrAddress
specifier|final
specifier|private
name|String
name|containerMgrAddress
decl_stmt|;
DECL|method|Container (TaskAttemptId taId, ContainerId containerID, String containerMgrAddress)
specifier|public
name|Container
parameter_list|(
name|TaskAttemptId
name|taId
parameter_list|,
name|ContainerId
name|containerID
parameter_list|,
name|String
name|containerMgrAddress
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|PREP
expr_stmt|;
name|this
operator|.
name|taskAttemptID
operator|=
name|taId
expr_stmt|;
name|this
operator|.
name|containerMgrAddress
operator|=
name|containerMgrAddress
expr_stmt|;
name|this
operator|.
name|containerID
operator|=
name|containerID
expr_stmt|;
block|}
DECL|method|isCompletelyDone ()
specifier|public
specifier|synchronized
name|boolean
name|isCompletelyDone
parameter_list|()
block|{
return|return
name|state
operator|==
name|ContainerState
operator|.
name|DONE
operator|||
name|state
operator|==
name|ContainerState
operator|.
name|FAILED
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|launch (ContainerRemoteLaunchEvent event)
specifier|public
specifier|synchronized
name|void
name|launch
parameter_list|(
name|ContainerRemoteLaunchEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Launching "
operator|+
name|taskAttemptID
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|state
operator|==
name|ContainerState
operator|.
name|KILLED_BEFORE_LAUNCH
condition|)
block|{
name|state
operator|=
name|ContainerState
operator|.
name|DONE
expr_stmt|;
name|sendContainerLaunchFailedMsg
argument_list|(
name|taskAttemptID
argument_list|,
literal|"Container was killed before it was launched"
argument_list|)
expr_stmt|;
return|return;
block|}
name|ContainerManagementProtocolProxyData
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|proxy
operator|=
name|getCMProxy
argument_list|(
name|containerMgrAddress
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
comment|// Construct the actual Container
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|event
operator|.
name|getContainerLaunchContext
argument_list|()
decl_stmt|;
comment|// Now launch the actual container
name|StartContainerRequest
name|startRequest
init|=
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|containerLaunchContext
argument_list|,
name|event
operator|.
name|getContainerToken
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StartContainerRequest
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|StartContainerRequest
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|startRequest
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|requestList
init|=
name|StartContainersRequest
operator|.
name|newInstance
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|StartContainersResponse
name|response
init|=
name|proxy
operator|.
name|getContainerManagementProtocol
argument_list|()
operator|.
name|startContainers
argument_list|(
name|requestList
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|containsKey
argument_list|(
name|containerID
argument_list|)
condition|)
block|{
throw|throw
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|get
argument_list|(
name|containerID
argument_list|)
operator|.
name|deSerialize
argument_list|()
throw|;
block|}
name|ByteBuffer
name|portInfo
init|=
name|response
operator|.
name|getAllServicesMetaData
argument_list|()
operator|.
name|get
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
name|this
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|FAILED
expr_stmt|;
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
name|this
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|RUNNING
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
name|this
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|FAILED
expr_stmt|;
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
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|cmProxy
operator|.
name|mayBeCloseProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|kill ()
specifier|public
specifier|synchronized
name|void
name|kill
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|state
operator|==
name|ContainerState
operator|.
name|PREP
condition|)
block|{
name|this
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|KILLED_BEFORE_LAUNCH
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|isCompletelyDone
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"KILLING "
operator|+
name|taskAttemptID
argument_list|)
expr_stmt|;
name|ContainerManagementProtocolProxyData
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|proxy
operator|=
name|getCMProxy
argument_list|(
name|this
operator|.
name|containerMgrAddress
argument_list|,
name|this
operator|.
name|containerID
argument_list|)
expr_stmt|;
comment|// kill the remote container if already launched
name|List
argument_list|<
name|ContainerId
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|this
operator|.
name|containerID
argument_list|)
expr_stmt|;
name|StopContainersRequest
name|request
init|=
name|StopContainersRequest
operator|.
name|newInstance
argument_list|(
name|ids
argument_list|)
decl_stmt|;
name|StopContainersResponse
name|response
init|=
name|proxy
operator|.
name|getContainerManagementProtocol
argument_list|()
operator|.
name|stopContainers
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|containsKey
argument_list|(
name|this
operator|.
name|containerID
argument_list|)
condition|)
block|{
throw|throw
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|get
argument_list|(
name|this
operator|.
name|containerID
argument_list|)
operator|.
name|deSerialize
argument_list|()
throw|;
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
name|this
operator|.
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
name|this
operator|.
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
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|cmProxy
operator|.
name|mayBeCloseProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|DONE
expr_stmt|;
block|}
comment|// after killing, send killed event to task attempt
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
name|this
operator|.
name|taskAttemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_CONTAINER_CLEANED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|this
operator|.
name|stopped
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Upper limit on the thread pool size is "
operator|+
name|this
operator|.
name|limitOnPoolSize
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cmProxy
operator|=
operator|new
name|ContainerManagementProtocolProxy
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
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
name|Set
argument_list|<
name|String
argument_list|>
name|allNodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|stopped
operator|.
name|get
argument_list|()
operator|&&
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
if|if
condition|(
operator|!
name|stopped
operator|.
name|get
argument_list|()
condition|)
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
block|}
return|return;
block|}
name|allNodes
operator|.
name|add
argument_list|(
name|event
operator|.
name|getContainerMgrAddress
argument_list|()
argument_list|)
expr_stmt|;
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
name|allNodes
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
operator|<
name|idealPoolSize
condition|)
block|{
comment|// Bump up the pool size to idealPoolSize+INITIAL_POOL_SIZE, the
comment|// later is just a buffer so we are not always increasing the
comment|// pool-size
name|int
name|newPoolSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|limitOnPoolSize
argument_list|,
name|idealPoolSize
operator|+
name|INITIAL_POOL_SIZE
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting ContainerLauncher pool size to "
operator|+
name|newPoolSize
operator|+
literal|" as number-of-nodes to talk to is "
operator|+
name|numNodes
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
name|createEventProcessor
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
name|serviceStart
argument_list|()
expr_stmt|;
block|}
DECL|method|shutdownAllContainers ()
specifier|private
name|void
name|shutdownAllContainers
parameter_list|()
block|{
for|for
control|(
name|Container
name|ct
range|:
name|this
operator|.
name|containers
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|ct
operator|!=
literal|null
condition|)
block|{
name|ct
operator|.
name|kill
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|stopped
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
comment|// return if already stopped
return|return;
block|}
comment|// shutdown any containers that might be left running
name|shutdownAllContainers
argument_list|()
expr_stmt|;
if|if
condition|(
name|eventHandlingThread
operator|!=
literal|null
condition|)
block|{
name|eventHandlingThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|launcherPool
operator|!=
literal|null
condition|)
block|{
name|launcherPool
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|createEventProcessor (ContainerLauncherEvent event)
specifier|protected
name|EventProcessor
name|createEventProcessor
parameter_list|(
name|ContainerLauncherEvent
name|event
parameter_list|)
block|{
return|return
operator|new
name|EventProcessor
argument_list|(
name|event
argument_list|)
return|;
block|}
comment|/**    * Setup and start the container on remote nodemanager.    */
DECL|class|EventProcessor
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
name|ContainerId
name|containerID
init|=
name|event
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|Container
name|c
init|=
name|getContainer
argument_list|(
name|event
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
name|c
operator|.
name|launch
argument_list|(
name|launchEvent
argument_list|)
expr_stmt|;
break|break;
case|case
name|CONTAINER_REMOTE_CLEANUP
case|:
name|c
operator|.
name|kill
argument_list|()
expr_stmt|;
break|break;
block|}
name|removeContainerIfDone
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
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
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ContainerManagementProtocolProxy
operator|.
name|ContainerManagementProtocolProxyData
DECL|method|getCMProxy (String containerMgrBindAddr, ContainerId containerId)
name|getCMProxy
parameter_list|(
name|String
name|containerMgrBindAddr
parameter_list|,
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|cmProxy
operator|.
name|getProxy
argument_list|(
name|containerMgrBindAddr
argument_list|,
name|containerId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

