begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
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
name|ConcurrentSkipListMap
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
name|util
operator|.
name|ReflectionUtils
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
name|ShutdownHookManager
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
name|NodeHealthStatus
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
name|NodeId
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
name|event
operator|.
name|AsyncDispatcher
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
name|event
operator|.
name|Dispatcher
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
name|event
operator|.
name|EventHandler
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|ContainerManagerImpl
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|Application
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|server
operator|.
name|nodemanager
operator|.
name|metrics
operator|.
name|NodeManagerMetrics
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
name|server
operator|.
name|nodemanager
operator|.
name|security
operator|.
name|NMContainerTokenSecretManager
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
name|server
operator|.
name|nodemanager
operator|.
name|webapp
operator|.
name|WebServer
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
name|server
operator|.
name|security
operator|.
name|ApplicationACLsManager
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
name|CompositeService
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_class
DECL|class|NodeManager
specifier|public
class|class
name|NodeManager
extends|extends
name|CompositeService
implements|implements
name|EventHandler
argument_list|<
name|NodeManagerEvent
argument_list|>
block|{
comment|/**    * Priority of the NodeManager shutdown hook.    */
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|public
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|30
decl_stmt|;
comment|/**    * Extra duration to wait for containers to be killed on shutdown.    */
DECL|field|SHUTDOWN_CLEANUP_SLOP_MS
specifier|private
specifier|static
specifier|final
name|int
name|SHUTDOWN_CLEANUP_SLOP_MS
init|=
literal|1000
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
name|NodeManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|metrics
specifier|protected
specifier|final
name|NodeManagerMetrics
name|metrics
init|=
name|NodeManagerMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
DECL|field|aclsManager
specifier|private
name|ApplicationACLsManager
name|aclsManager
decl_stmt|;
DECL|field|nodeHealthChecker
specifier|private
name|NodeHealthCheckerService
name|nodeHealthChecker
decl_stmt|;
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
decl_stmt|;
DECL|field|dispatcher
specifier|private
name|AsyncDispatcher
name|dispatcher
decl_stmt|;
DECL|field|containerManager
specifier|private
name|ContainerManagerImpl
name|containerManager
decl_stmt|;
DECL|field|nodeStatusUpdater
specifier|private
name|NodeStatusUpdater
name|nodeStatusUpdater
decl_stmt|;
DECL|field|nodeManagerShutdownHook
specifier|private
specifier|static
name|CompositeServiceShutdownHook
name|nodeManagerShutdownHook
decl_stmt|;
DECL|field|waitForContainersOnShutdownMillis
specifier|private
name|long
name|waitForContainersOnShutdownMillis
decl_stmt|;
DECL|field|isStopping
specifier|private
name|AtomicBoolean
name|isStopping
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|NodeManager ()
specifier|public
name|NodeManager
parameter_list|()
block|{
name|super
argument_list|(
name|NodeManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createNodeStatusUpdater (Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker)
specifier|protected
name|NodeStatusUpdater
name|createNodeStatusUpdater
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|NodeHealthCheckerService
name|healthChecker
parameter_list|)
block|{
return|return
operator|new
name|NodeStatusUpdaterImpl
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|)
return|;
block|}
DECL|method|createNodeResourceMonitor ()
specifier|protected
name|NodeResourceMonitor
name|createNodeResourceMonitor
parameter_list|()
block|{
return|return
operator|new
name|NodeResourceMonitorImpl
argument_list|()
return|;
block|}
DECL|method|createContainerManager (Context context, ContainerExecutor exec, DeletionService del, NodeStatusUpdater nodeStatusUpdater, ApplicationACLsManager aclsManager, LocalDirsHandlerService dirsHandler)
specifier|protected
name|ContainerManagerImpl
name|createContainerManager
parameter_list|(
name|Context
name|context
parameter_list|,
name|ContainerExecutor
name|exec
parameter_list|,
name|DeletionService
name|del
parameter_list|,
name|NodeStatusUpdater
name|nodeStatusUpdater
parameter_list|,
name|ApplicationACLsManager
name|aclsManager
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
return|return
operator|new
name|ContainerManagerImpl
argument_list|(
name|context
argument_list|,
name|exec
argument_list|,
name|del
argument_list|,
name|nodeStatusUpdater
argument_list|,
name|metrics
argument_list|,
name|aclsManager
argument_list|,
name|dirsHandler
argument_list|)
return|;
block|}
DECL|method|createWebServer (Context nmContext, ResourceView resourceView, ApplicationACLsManager aclsManager, LocalDirsHandlerService dirsHandler)
specifier|protected
name|WebServer
name|createWebServer
parameter_list|(
name|Context
name|nmContext
parameter_list|,
name|ResourceView
name|resourceView
parameter_list|,
name|ApplicationACLsManager
name|aclsManager
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
return|return
operator|new
name|WebServer
argument_list|(
name|nmContext
argument_list|,
name|resourceView
argument_list|,
name|aclsManager
argument_list|,
name|dirsHandler
argument_list|)
return|;
block|}
DECL|method|createDeletionService (ContainerExecutor exec)
specifier|protected
name|DeletionService
name|createDeletionService
parameter_list|(
name|ContainerExecutor
name|exec
parameter_list|)
block|{
return|return
operator|new
name|DeletionService
argument_list|(
name|exec
argument_list|)
return|;
block|}
DECL|method|createNMContext (NMContainerTokenSecretManager containerTokenSecretManager)
specifier|protected
name|NMContext
name|createNMContext
parameter_list|(
name|NMContainerTokenSecretManager
name|containerTokenSecretManager
parameter_list|)
block|{
return|return
operator|new
name|NMContext
argument_list|(
name|containerTokenSecretManager
argument_list|)
return|;
block|}
DECL|method|doSecureLogin ()
specifier|protected
name|void
name|doSecureLogin
parameter_list|()
throws|throws
name|IOException
block|{
name|SecurityUtil
operator|.
name|login
argument_list|(
name|getConfig
argument_list|()
argument_list|,
name|YarnConfiguration
operator|.
name|NM_KEYTAB
argument_list|,
name|YarnConfiguration
operator|.
name|NM_PRINCIPAL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|Dispatcher
operator|.
name|DISPATCHER_EXIT_ON_ERROR_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|NMContainerTokenSecretManager
name|containerTokenSecretManager
init|=
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|context
operator|=
name|createNMContext
argument_list|(
name|containerTokenSecretManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|aclsManager
operator|=
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ContainerExecutor
name|exec
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|conf
operator|.
name|getClass
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_EXECUTOR
argument_list|,
name|DefaultContainerExecutor
operator|.
name|class
argument_list|,
name|ContainerExecutor
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|exec
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed to initialize container executor"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|DeletionService
name|del
init|=
name|createDeletionService
argument_list|(
name|exec
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|del
argument_list|)
expr_stmt|;
comment|// NodeManager level dispatcher
name|this
operator|.
name|dispatcher
operator|=
operator|new
name|AsyncDispatcher
argument_list|()
expr_stmt|;
name|nodeHealthChecker
operator|=
operator|new
name|NodeHealthCheckerService
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|nodeHealthChecker
argument_list|)
expr_stmt|;
name|dirsHandler
operator|=
name|nodeHealthChecker
operator|.
name|getDiskHandler
argument_list|()
expr_stmt|;
name|nodeStatusUpdater
operator|=
name|createNodeStatusUpdater
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|nodeHealthChecker
argument_list|)
expr_stmt|;
name|NodeResourceMonitor
name|nodeResourceMonitor
init|=
name|createNodeResourceMonitor
argument_list|()
decl_stmt|;
name|addService
argument_list|(
name|nodeResourceMonitor
argument_list|)
expr_stmt|;
name|containerManager
operator|=
name|createContainerManager
argument_list|(
name|context
argument_list|,
name|exec
argument_list|,
name|del
argument_list|,
name|nodeStatusUpdater
argument_list|,
name|this
operator|.
name|aclsManager
argument_list|,
name|dirsHandler
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
operator|(
operator|(
name|NMContext
operator|)
name|context
operator|)
operator|.
name|setContainerManager
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
name|WebServer
name|webServer
init|=
name|createWebServer
argument_list|(
name|context
argument_list|,
name|containerManager
operator|.
name|getContainersMonitor
argument_list|()
argument_list|,
name|this
operator|.
name|aclsManager
argument_list|,
name|dirsHandler
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|webServer
argument_list|)
expr_stmt|;
operator|(
operator|(
name|NMContext
operator|)
name|context
operator|)
operator|.
name|setWebServer
argument_list|(
name|webServer
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ContainerManagerEventType
operator|.
name|class
argument_list|,
name|containerManager
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|NodeManagerEventType
operator|.
name|class
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"NodeManager"
argument_list|)
expr_stmt|;
comment|// StatusUpdater should be added last so that it get started last
comment|// so that we make sure everything is up before registering with RM.
name|addService
argument_list|(
name|nodeStatusUpdater
argument_list|)
expr_stmt|;
name|waitForContainersOnShutdownMillis
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
argument_list|)
operator|+
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PROCESS_KILL_WAIT_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PROCESS_KILL_WAIT_MS
argument_list|)
operator|+
name|SHUTDOWN_CLEANUP_SLOP_MS
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// TODO add local dirs to del
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
try|try
block|{
name|doSecureLogin
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed NodeManager login"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|isStopping
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
return|return;
block|}
name|cleanupContainers
argument_list|(
name|NodeManagerEventType
operator|.
name|SHUTDOWN
argument_list|)
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|resyncWithRM ()
specifier|protected
name|void
name|resyncWithRM
parameter_list|()
block|{
comment|//we do not want to block dispatcher thread here
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Notifying ContainerManager to block new container-requests"
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|setBlockNewContainerRequests
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cleanupContainers
argument_list|(
name|NodeManagerEventType
operator|.
name|RESYNC
argument_list|)
expr_stmt|;
operator|(
operator|(
name|NodeStatusUpdaterImpl
operator|)
name|nodeStatusUpdater
operator|)
operator|.
name|rebootNodeStatusUpdater
argument_list|()
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|cleanupContainers (NodeManagerEventType eventType)
specifier|protected
name|void
name|cleanupContainers
parameter_list|(
name|NodeManagerEventType
name|eventType
parameter_list|)
block|{
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|containers
init|=
name|context
operator|.
name|getContainers
argument_list|()
decl_stmt|;
if|if
condition|(
name|containers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Containers still running on "
operator|+
name|eventType
operator|+
literal|" : "
operator|+
name|containers
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|(
name|containers
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|CMgrCompletedContainersEvent
argument_list|(
name|containerIds
argument_list|,
name|CMgrCompletedContainersEvent
operator|.
name|Reason
operator|.
name|ON_SHUTDOWN
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for containers to be killed"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|eventType
condition|)
block|{
case|case
name|SHUTDOWN
case|:
name|long
name|waitStartTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|containers
operator|.
name|isEmpty
argument_list|()
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|waitStartTime
operator|<
name|waitForContainersOnShutdownMillis
condition|)
block|{
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
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted while sleeping on container kill on shutdown"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|RESYNC
case|:
while|while
condition|(
operator|!
name|containers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|//to remove done containers from the map
name|nodeStatusUpdater
operator|.
name|getNodeStatusAndUpdateContainersInContext
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted while sleeping on container kill on resync"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid eventType: "
operator|+
name|eventType
argument_list|)
expr_stmt|;
block|}
comment|// All containers killed
if|if
condition|(
name|containers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"All containers in DONE state"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Done waiting for containers to be killed. Still alive: "
operator|+
name|containers
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NMContext
specifier|public
specifier|static
class|class
name|NMContext
implements|implements
name|Context
block|{
DECL|field|nodeId
specifier|private
specifier|final
name|NodeId
name|nodeId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|applications
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|Application
argument_list|>
name|applications
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationId
argument_list|,
name|Application
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|containers
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|containers
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|containerTokenSecretManager
specifier|private
specifier|final
name|NMContainerTokenSecretManager
name|containerTokenSecretManager
decl_stmt|;
DECL|field|containerManager
specifier|private
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|webServer
specifier|private
name|WebServer
name|webServer
decl_stmt|;
DECL|field|nodeHealthStatus
specifier|private
specifier|final
name|NodeHealthStatus
name|nodeHealthStatus
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
operator|.
name|newRecordInstance
argument_list|(
name|NodeHealthStatus
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|NMContext (NMContainerTokenSecretManager containerTokenSecretManager)
specifier|public
name|NMContext
parameter_list|(
name|NMContainerTokenSecretManager
name|containerTokenSecretManager
parameter_list|)
block|{
name|this
operator|.
name|containerTokenSecretManager
operator|=
name|containerTokenSecretManager
expr_stmt|;
name|this
operator|.
name|nodeHealthStatus
operator|.
name|setIsNodeHealthy
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeHealthStatus
operator|.
name|setHealthReport
argument_list|(
literal|"Healthy"
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeHealthStatus
operator|.
name|setLastHealthReportTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Usable only after ContainerManager is started.      */
annotation|@
name|Override
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeId
return|;
block|}
annotation|@
name|Override
DECL|method|getHttpPort ()
specifier|public
name|int
name|getHttpPort
parameter_list|()
block|{
return|return
name|this
operator|.
name|webServer
operator|.
name|getPort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getApplications ()
specifier|public
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|Application
argument_list|>
name|getApplications
parameter_list|()
block|{
return|return
name|this
operator|.
name|applications
return|;
block|}
annotation|@
name|Override
DECL|method|getContainers ()
specifier|public
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|getContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|containers
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerTokenSecretManager ()
specifier|public
name|NMContainerTokenSecretManager
name|getContainerTokenSecretManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerTokenSecretManager
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeHealthStatus ()
specifier|public
name|NodeHealthStatus
name|getNodeHealthStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeHealthStatus
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerManager ()
specifier|public
name|ContainerManager
name|getContainerManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerManager
return|;
block|}
DECL|method|setContainerManager (ContainerManager containerManager)
specifier|public
name|void
name|setContainerManager
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|)
block|{
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
block|}
DECL|method|setWebServer (WebServer webServer)
specifier|public
name|void
name|setWebServer
parameter_list|(
name|WebServer
name|webServer
parameter_list|)
block|{
name|this
operator|.
name|webServer
operator|=
name|webServer
expr_stmt|;
block|}
block|}
comment|/**    * @return the node health checker    */
DECL|method|getNodeHealthChecker ()
specifier|public
name|NodeHealthCheckerService
name|getNodeHealthChecker
parameter_list|()
block|{
return|return
name|nodeHealthChecker
return|;
block|}
DECL|method|initAndStartNodeManager (Configuration conf, boolean hasToReboot)
specifier|private
name|void
name|initAndStartNodeManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|boolean
name|hasToReboot
parameter_list|)
block|{
try|try
block|{
comment|// Remove the old hook if we are rebooting.
if|if
condition|(
name|hasToReboot
operator|&&
literal|null
operator|!=
name|nodeManagerShutdownHook
condition|)
block|{
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|removeShutdownHook
argument_list|(
name|nodeManagerShutdownHook
argument_list|)
expr_stmt|;
block|}
name|nodeManagerShutdownHook
operator|=
operator|new
name|CompositeServiceShutdownHook
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
name|nodeManagerShutdownHook
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
name|this
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|start
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
literal|"Error starting NodeManager"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|handle (NodeManagerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|NodeManagerEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|SHUTDOWN
case|:
name|stop
argument_list|()
expr_stmt|;
break|break;
case|case
name|RESYNC
case|:
name|resyncWithRM
argument_list|()
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid shutdown event "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|". Ignoring."
argument_list|)
expr_stmt|;
block|}
block|}
comment|// For testing
DECL|method|createNewNodeManager ()
name|NodeManager
name|createNewNodeManager
parameter_list|()
block|{
return|return
operator|new
name|NodeManager
argument_list|()
return|;
block|}
comment|// For testing
DECL|method|getContainerManager ()
name|ContainerManagerImpl
name|getContainerManager
parameter_list|()
block|{
return|return
name|containerManager
return|;
block|}
comment|//For testing
DECL|method|getNMDispatcher ()
name|Dispatcher
name|getNMDispatcher
parameter_list|()
block|{
return|return
name|dispatcher
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNMContext ()
name|Context
name|getNMContext
parameter_list|()
block|{
return|return
name|this
operator|.
name|context
return|;
block|}
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
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
operator|new
name|YarnUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|NodeManager
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|NodeManager
name|nodeManager
init|=
operator|new
name|NodeManager
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|nodeManager
operator|.
name|initAndStartNodeManager
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

