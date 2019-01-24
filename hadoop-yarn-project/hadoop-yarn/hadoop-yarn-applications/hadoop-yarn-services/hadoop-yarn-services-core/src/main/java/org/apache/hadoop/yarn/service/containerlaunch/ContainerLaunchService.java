begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.containerlaunch
package|package
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
name|containerlaunch
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|yarn
operator|.
name|api
operator|.
name|records
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
name|service
operator|.
name|ServiceContext
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
name|api
operator|.
name|records
operator|.
name|Artifact
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
name|component
operator|.
name|ComponentEvent
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
name|component
operator|.
name|ComponentEventType
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
name|component
operator|.
name|instance
operator|.
name|ComponentInstance
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
name|provider
operator|.
name|ProviderService
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
name|provider
operator|.
name|ProviderFactory
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
name|api
operator|.
name|records
operator|.
name|Service
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
name|utils
operator|.
name|SliderFileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Callable
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
name|ExecutorService
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
name|Executors
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
name|Future
import|;
end_import

begin_import
import|import static
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
name|provider
operator|.
name|ProviderService
operator|.
name|FAILED_LAUNCH_PARAMS
import|;
end_import

begin_class
DECL|class|ContainerLaunchService
specifier|public
class|class
name|ContainerLaunchService
extends|extends
name|AbstractService
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerLaunchService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|executorService
specifier|private
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|fs
specifier|private
name|SliderFileSystem
name|fs
decl_stmt|;
DECL|field|context
specifier|private
name|ServiceContext
name|context
decl_stmt|;
DECL|method|ContainerLaunchService (ServiceContext context)
specifier|public
name|ContainerLaunchService
parameter_list|(
name|ServiceContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|ContainerLaunchService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|context
operator|.
name|fs
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
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|executorService
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|executorService
operator|!=
literal|null
condition|)
block|{
name|executorService
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
DECL|method|launchCompInstance ( Service service, ComponentInstance instance, Container container, ComponentLaunchContext componentLaunchContext)
specifier|public
name|Future
argument_list|<
name|ProviderService
operator|.
name|ResolvedLaunchParams
argument_list|>
name|launchCompInstance
parameter_list|(
name|Service
name|service
parameter_list|,
name|ComponentInstance
name|instance
parameter_list|,
name|Container
name|container
parameter_list|,
name|ComponentLaunchContext
name|componentLaunchContext
parameter_list|)
block|{
name|ContainerLauncher
name|launcher
init|=
operator|new
name|ContainerLauncher
argument_list|(
name|service
argument_list|,
name|instance
argument_list|,
name|container
argument_list|,
name|componentLaunchContext
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|executorService
operator|.
name|submit
argument_list|(
name|launcher
argument_list|)
return|;
block|}
DECL|method|reInitCompInstance ( Service service, ComponentInstance instance, Container container, ComponentLaunchContext componentLaunchContext)
specifier|public
name|Future
argument_list|<
name|ProviderService
operator|.
name|ResolvedLaunchParams
argument_list|>
name|reInitCompInstance
parameter_list|(
name|Service
name|service
parameter_list|,
name|ComponentInstance
name|instance
parameter_list|,
name|Container
name|container
parameter_list|,
name|ComponentLaunchContext
name|componentLaunchContext
parameter_list|)
block|{
name|ContainerLauncher
name|reInitializer
init|=
operator|new
name|ContainerLauncher
argument_list|(
name|service
argument_list|,
name|instance
argument_list|,
name|container
argument_list|,
name|componentLaunchContext
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|executorService
operator|.
name|submit
argument_list|(
name|reInitializer
argument_list|)
return|;
block|}
DECL|class|ContainerLauncher
specifier|private
class|class
name|ContainerLauncher
implements|implements
name|Callable
argument_list|<
name|ProviderService
operator|.
name|ResolvedLaunchParams
argument_list|>
block|{
DECL|field|container
specifier|public
specifier|final
name|Container
name|container
decl_stmt|;
DECL|field|service
specifier|public
specifier|final
name|Service
name|service
decl_stmt|;
DECL|field|instance
specifier|public
name|ComponentInstance
name|instance
decl_stmt|;
DECL|field|componentLaunchContext
specifier|private
specifier|final
name|ComponentLaunchContext
name|componentLaunchContext
decl_stmt|;
DECL|field|reInit
specifier|private
specifier|final
name|boolean
name|reInit
decl_stmt|;
DECL|method|ContainerLauncher (Service service, ComponentInstance instance, Container container, ComponentLaunchContext componentLaunchContext, boolean reInit)
name|ContainerLauncher
parameter_list|(
name|Service
name|service
parameter_list|,
name|ComponentInstance
name|instance
parameter_list|,
name|Container
name|container
parameter_list|,
name|ComponentLaunchContext
name|componentLaunchContext
parameter_list|,
name|boolean
name|reInit
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|instance
operator|=
name|instance
expr_stmt|;
name|this
operator|.
name|componentLaunchContext
operator|=
name|componentLaunchContext
expr_stmt|;
name|this
operator|.
name|reInit
operator|=
name|reInit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|ProviderService
operator|.
name|ResolvedLaunchParams
name|call
parameter_list|()
block|{
name|ProviderService
name|provider
init|=
name|ProviderFactory
operator|.
name|getProviderService
argument_list|(
name|componentLaunchContext
operator|.
name|getArtifact
argument_list|()
argument_list|)
decl_stmt|;
name|AbstractLauncher
name|launcher
init|=
operator|new
name|AbstractLauncher
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|ProviderService
operator|.
name|ResolvedLaunchParams
name|resolvedParams
init|=
literal|null
decl_stmt|;
try|try
block|{
name|resolvedParams
operator|=
name|provider
operator|.
name|buildContainerLaunchContext
argument_list|(
name|launcher
argument_list|,
name|service
argument_list|,
name|instance
argument_list|,
name|fs
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|container
argument_list|,
name|componentLaunchContext
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|reInit
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"launching container {}"
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|instance
operator|.
name|getComponent
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|getNmClient
argument_list|()
operator|.
name|startContainerAsync
argument_list|(
name|container
argument_list|,
name|launcher
operator|.
name|completeContainerLaunch
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"reInitializing container {} with version {}"
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|componentLaunchContext
operator|.
name|getServiceVersion
argument_list|()
argument_list|)
expr_stmt|;
name|instance
operator|.
name|getComponent
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|getNmClient
argument_list|()
operator|.
name|reInitializeContainerAsync
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|launcher
operator|.
name|completeContainerLaunch
argument_list|()
argument_list|,
literal|true
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
name|LOG
operator|.
name|error
argument_list|(
literal|"{}: Failed to launch container."
argument_list|,
name|instance
operator|.
name|getCompInstanceId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|ComponentEvent
name|event
init|=
operator|new
name|ComponentEvent
argument_list|(
name|instance
operator|.
name|getCompName
argument_list|()
argument_list|,
name|ComponentEventType
operator|.
name|CONTAINER_COMPLETED
argument_list|)
operator|.
name|setInstance
argument_list|(
name|instance
argument_list|)
operator|.
name|setContainerId
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|scheduler
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resolvedParams
operator|!=
literal|null
condition|)
block|{
return|return
name|resolvedParams
return|;
block|}
else|else
block|{
return|return
name|FAILED_LAUNCH_PARAMS
return|;
block|}
block|}
block|}
comment|/**    * Launch context of a component.    */
DECL|class|ComponentLaunchContext
specifier|public
specifier|static
class|class
name|ComponentLaunchContext
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|serviceVersion
specifier|private
specifier|final
name|String
name|serviceVersion
decl_stmt|;
DECL|field|artifact
specifier|private
name|Artifact
name|artifact
decl_stmt|;
specifier|private
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
name|api
operator|.
name|records
operator|.
name|Configuration
DECL|field|configuration
name|configuration
decl_stmt|;
DECL|field|launchCommand
specifier|private
name|String
name|launchCommand
decl_stmt|;
DECL|field|runPrivilegedContainer
specifier|private
name|boolean
name|runPrivilegedContainer
decl_stmt|;
DECL|method|ComponentLaunchContext (String name, String serviceVersion)
specifier|public
name|ComponentLaunchContext
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|serviceVersion
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|serviceVersion
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|serviceVersion
argument_list|)
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getServiceVersion ()
specifier|public
name|String
name|getServiceVersion
parameter_list|()
block|{
return|return
name|serviceVersion
return|;
block|}
DECL|method|getArtifact ()
specifier|public
name|Artifact
name|getArtifact
parameter_list|()
block|{
return|return
name|artifact
return|;
block|}
specifier|public
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
name|api
operator|.
name|records
operator|.
DECL|method|getConfiguration ()
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
DECL|method|getLaunchCommand ()
specifier|public
name|String
name|getLaunchCommand
parameter_list|()
block|{
return|return
name|launchCommand
return|;
block|}
DECL|method|isRunPrivilegedContainer ()
specifier|public
name|boolean
name|isRunPrivilegedContainer
parameter_list|()
block|{
return|return
name|runPrivilegedContainer
return|;
block|}
DECL|method|setArtifact (Artifact artifact)
specifier|public
name|ComponentLaunchContext
name|setArtifact
parameter_list|(
name|Artifact
name|artifact
parameter_list|)
block|{
name|this
operator|.
name|artifact
operator|=
name|artifact
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setConfiguration (org.apache.hadoop.yarn. service.api.records.Configuration configuration)
specifier|public
name|ComponentLaunchContext
name|setConfiguration
parameter_list|(
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
name|api
operator|.
name|records
operator|.
name|Configuration
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLaunchCommand (String launchCommand)
specifier|public
name|ComponentLaunchContext
name|setLaunchCommand
parameter_list|(
name|String
name|launchCommand
parameter_list|)
block|{
name|this
operator|.
name|launchCommand
operator|=
name|launchCommand
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRunPrivilegedContainer ( boolean runPrivilegedContainer)
specifier|public
name|ComponentLaunchContext
name|setRunPrivilegedContainer
parameter_list|(
name|boolean
name|runPrivilegedContainer
parameter_list|)
block|{
name|this
operator|.
name|runPrivilegedContainer
operator|=
name|runPrivilegedContainer
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

