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
name|NodeHealthCheckerService
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
name|service
operator|.
name|Service
import|;
end_import

begin_class
DECL|class|NodeManager
specifier|public
class|class
name|NodeManager
extends|extends
name|CompositeService
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
DECL|method|createContainerManager (Context context, ContainerExecutor exec, DeletionService del, NodeStatusUpdater nodeStatusUpdater)
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
argument_list|)
return|;
block|}
DECL|method|createWebServer (Context nmContext, ResourceView resourceView)
specifier|protected
name|WebServer
name|createWebServer
parameter_list|(
name|Context
name|nmContext
parameter_list|,
name|ResourceView
name|resourceView
parameter_list|)
block|{
return|return
operator|new
name|WebServer
argument_list|(
name|nmContext
argument_list|,
name|resourceView
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
name|Context
name|context
init|=
operator|new
name|NMContext
argument_list|()
decl_stmt|;
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
name|DeletionService
name|del
init|=
operator|new
name|DeletionService
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
name|AsyncDispatcher
name|dispatcher
init|=
operator|new
name|AsyncDispatcher
argument_list|()
decl_stmt|;
name|NodeHealthCheckerService
name|healthChecker
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|NodeHealthCheckerService
operator|.
name|shouldRun
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|healthChecker
operator|=
operator|new
name|NodeHealthCheckerService
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|healthChecker
argument_list|)
expr_stmt|;
block|}
comment|// StatusUpdater should be added first so that it can start first. Once it
comment|// contacts RM, does registration and gets tokens, then only
comment|// ContainerManager can start.
name|NodeStatusUpdater
name|nodeStatusUpdater
init|=
name|createNodeStatusUpdater
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|nodeStatusUpdater
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
name|ContainerManagerImpl
name|containerManager
init|=
name|createContainerManager
argument_list|(
name|context
argument_list|,
name|exec
argument_list|,
name|del
argument_list|,
name|nodeStatusUpdater
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
name|Service
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
argument_list|)
decl_stmt|;
name|addService
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
name|addService
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
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
name|NodeManager
operator|.
name|this
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"NodeManager"
argument_list|)
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
DECL|class|NMContext
specifier|public
specifier|static
class|class
name|NMContext
implements|implements
name|Context
block|{
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
DECL|method|NMContext ()
specifier|public
name|NMContext
parameter_list|()
block|{
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
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|nodeManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

