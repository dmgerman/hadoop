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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|server
operator|.
name|security
operator|.
name|ContainerTokenSecretManager
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
name|application
operator|.
name|ApplicationEvent
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
name|ApplicationEventType
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
name|ApplicationInitedEvent
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
name|containermanager
operator|.
name|container
operator|.
name|ContainerEvent
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
name|ContainerEventType
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
name|ContainerExitEvent
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
name|ContainerResourceLocalizedEvent
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
name|launcher
operator|.
name|ContainersLauncher
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
name|launcher
operator|.
name|ContainersLauncherEvent
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
name|localizer
operator|.
name|LocalResourceRequest
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
name|localizer
operator|.
name|ResourceLocalizationService
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
name|localizer
operator|.
name|event
operator|.
name|ApplicationLocalizationEvent
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
name|localizer
operator|.
name|event
operator|.
name|ContainerLocalizationEvent
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
name|localizer
operator|.
name|event
operator|.
name|ContainerLocalizationRequestEvent
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
name|localizer
operator|.
name|event
operator|.
name|LocalizationEvent
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
name|loghandler
operator|.
name|LogHandler
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
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerEvent
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

begin_class
DECL|class|DummyContainerManager
specifier|public
class|class
name|DummyContainerManager
extends|extends
name|ContainerManagerImpl
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
name|DummyContainerManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|DummyContainerManager (Context context, ContainerExecutor exec, DeletionService deletionContext, NodeStatusUpdater nodeStatusUpdater, NodeManagerMetrics metrics, ContainerTokenSecretManager containerTokenSecretManager, ApplicationACLsManager applicationACLsManager)
specifier|public
name|DummyContainerManager
parameter_list|(
name|Context
name|context
parameter_list|,
name|ContainerExecutor
name|exec
parameter_list|,
name|DeletionService
name|deletionContext
parameter_list|,
name|NodeStatusUpdater
name|nodeStatusUpdater
parameter_list|,
name|NodeManagerMetrics
name|metrics
parameter_list|,
name|ContainerTokenSecretManager
name|containerTokenSecretManager
parameter_list|,
name|ApplicationACLsManager
name|applicationACLsManager
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|exec
argument_list|,
name|deletionContext
argument_list|,
name|nodeStatusUpdater
argument_list|,
name|metrics
argument_list|,
name|containerTokenSecretManager
argument_list|,
name|applicationACLsManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createResourceLocalizationService (ContainerExecutor exec, DeletionService deletionContext)
specifier|protected
name|ResourceLocalizationService
name|createResourceLocalizationService
parameter_list|(
name|ContainerExecutor
name|exec
parameter_list|,
name|DeletionService
name|deletionContext
parameter_list|)
block|{
return|return
operator|new
name|ResourceLocalizationService
argument_list|(
name|super
operator|.
name|dispatcher
argument_list|,
name|exec
argument_list|,
name|deletionContext
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|LocalizationEvent
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
name|INIT_APPLICATION_RESOURCES
case|:
name|Application
name|app
init|=
operator|(
operator|(
name|ApplicationLocalizationEvent
operator|)
name|event
operator|)
operator|.
name|getApplication
argument_list|()
decl_stmt|;
comment|// Simulate event from ApplicationLocalization.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationInitedEvent
argument_list|(
name|app
operator|.
name|getAppId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|INIT_CONTAINER_RESOURCES
case|:
name|ContainerLocalizationRequestEvent
name|rsrcReqs
init|=
operator|(
name|ContainerLocalizationRequestEvent
operator|)
name|event
decl_stmt|;
comment|// simulate localization of all requested resources
for|for
control|(
name|Collection
argument_list|<
name|LocalResourceRequest
argument_list|>
name|rc
range|:
name|rsrcReqs
operator|.
name|getRequestedResources
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|LocalResourceRequest
name|req
range|:
name|rc
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"DEBUG: "
operator|+
name|req
operator|+
literal|":"
operator|+
name|rsrcReqs
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerResourceLocalizedEvent
argument_list|(
name|rsrcReqs
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|req
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///local"
operator|+
name|req
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|CLEANUP_CONTAINER_RESOURCES
case|:
name|Container
name|container
init|=
operator|(
operator|(
name|ContainerLocalizationEvent
operator|)
name|event
operator|)
operator|.
name|getContainer
argument_list|()
decl_stmt|;
comment|// TODO: delete the container dir
name|this
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerEvent
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|ContainerEventType
operator|.
name|CONTAINER_RESOURCES_CLEANEDUP
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DESTROY_APPLICATION_RESOURCES
case|:
name|Application
name|application
init|=
operator|(
operator|(
name|ApplicationLocalizationEvent
operator|)
name|event
operator|)
operator|.
name|getApplication
argument_list|()
decl_stmt|;
comment|// decrement reference counts of all resources associated with this
comment|// app
name|this
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationEvent
argument_list|(
name|application
operator|.
name|getAppId
argument_list|()
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_RESOURCES_CLEANEDUP
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|"Unexpected event: "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createContainersLauncher (Context context, ContainerExecutor exec)
specifier|protected
name|ContainersLauncher
name|createContainersLauncher
parameter_list|(
name|Context
name|context
parameter_list|,
name|ContainerExecutor
name|exec
parameter_list|)
block|{
return|return
operator|new
name|ContainersLauncher
argument_list|(
name|context
argument_list|,
name|super
operator|.
name|dispatcher
argument_list|,
name|exec
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|ContainersLauncherEvent
name|event
parameter_list|)
block|{
name|Container
name|container
init|=
name|event
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|container
operator|.
name|getContainerID
argument_list|()
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
name|LAUNCH_CONTAINER
case|:
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerEvent
argument_list|(
name|containerId
argument_list|,
name|ContainerEventType
operator|.
name|CONTAINER_LAUNCHED
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|CLEANUP_CONTAINER
case|:
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerExitEvent
argument_list|(
name|containerId
argument_list|,
name|ContainerEventType
operator|.
name|CONTAINER_KILLED_ON_REQUEST
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createLogHandler (Configuration conf, Context context, DeletionService deletionService)
specifier|protected
name|LogHandler
name|createLogHandler
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Context
name|context
parameter_list|,
name|DeletionService
name|deletionService
parameter_list|)
block|{
return|return
operator|new
name|LogHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|LogHandlerEvent
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
name|APPLICATION_STARTED
case|:
break|break;
case|case
name|CONTAINER_FINISHED
case|:
break|break;
case|case
name|APPLICATION_FINISHED
case|:
break|break;
default|default:
comment|// Ignore
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

