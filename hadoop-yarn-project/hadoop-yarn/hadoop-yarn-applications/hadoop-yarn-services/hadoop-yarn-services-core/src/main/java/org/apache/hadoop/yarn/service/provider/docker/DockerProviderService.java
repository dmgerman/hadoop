begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.provider.docker
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
name|provider
operator|.
name|docker
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
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
name|AbstractProviderService
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
name|ProviderUtils
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|containerlaunch
operator|.
name|AbstractLauncher
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
name|containerlaunch
operator|.
name|CommandLineBuilder
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
name|containerlaunch
operator|.
name|ContainerLaunchService
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
name|exceptions
operator|.
name|SliderException
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
name|Map
import|;
end_import

begin_class
DECL|class|DockerProviderService
specifier|public
class|class
name|DockerProviderService
extends|extends
name|AbstractProviderService
implements|implements
name|DockerKeys
block|{
annotation|@
name|Override
DECL|method|processArtifact (AbstractLauncher launcher, ComponentInstance compInstance, SliderFileSystem fileSystem, Service service, ContainerLaunchService.ComponentLaunchContext compLaunchCtx)
specifier|public
name|void
name|processArtifact
parameter_list|(
name|AbstractLauncher
name|launcher
parameter_list|,
name|ComponentInstance
name|compInstance
parameter_list|,
name|SliderFileSystem
name|fileSystem
parameter_list|,
name|Service
name|service
parameter_list|,
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
name|compLaunchCtx
parameter_list|)
throws|throws
name|IOException
block|{
name|launcher
operator|.
name|setYarnDockerMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|setDockerImage
argument_list|(
name|compLaunchCtx
operator|.
name|getArtifact
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|setDockerNetwork
argument_list|(
name|compLaunchCtx
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|DOCKER_NETWORK
argument_list|)
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|setDockerHostname
argument_list|(
name|compInstance
operator|.
name|getHostname
argument_list|()
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|setRunPrivilegedContainer
argument_list|(
name|compLaunchCtx
operator|.
name|isRunPrivilegedContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check if system is default to disable docker override or    * user requested a Docker container with ENTRY_POINT support.    *    * @param compLaunchContext - launch context for the component.    * @return true if Docker launch command override is disabled    */
DECL|method|checkUseEntryPoint ( ContainerLaunchService.ComponentLaunchContext compLaunchContext)
specifier|private
name|boolean
name|checkUseEntryPoint
parameter_list|(
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
name|compLaunchContext
parameter_list|)
block|{
name|boolean
name|overrideDisable
init|=
literal|false
decl_stmt|;
name|String
name|overrideDisableKey
init|=
name|Environment
operator|.
name|YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE
operator|.
name|name
argument_list|()
decl_stmt|;
name|String
name|overrideDisableValue
init|=
operator|(
name|compLaunchContext
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getEnv
argument_list|(
name|overrideDisableKey
argument_list|)
operator|!=
literal|null
operator|)
condition|?
name|compLaunchContext
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getEnv
argument_list|(
name|overrideDisableKey
argument_list|)
else|:
name|System
operator|.
name|getenv
argument_list|(
name|overrideDisableKey
argument_list|)
decl_stmt|;
name|overrideDisable
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|overrideDisableValue
argument_list|)
expr_stmt|;
return|return
name|overrideDisable
return|;
block|}
annotation|@
name|Override
DECL|method|buildContainerLaunchCommand (AbstractLauncher launcher, Service service, ComponentInstance instance, SliderFileSystem fileSystem, Configuration yarnConf, Container container, ContainerLaunchService.ComponentLaunchContext compLaunchContext, Map<String, String> tokensForSubstitution)
specifier|public
name|void
name|buildContainerLaunchCommand
parameter_list|(
name|AbstractLauncher
name|launcher
parameter_list|,
name|Service
name|service
parameter_list|,
name|ComponentInstance
name|instance
parameter_list|,
name|SliderFileSystem
name|fileSystem
parameter_list|,
name|Configuration
name|yarnConf
parameter_list|,
name|Container
name|container
parameter_list|,
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
name|compLaunchContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokensForSubstitution
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|boolean
name|useEntryPoint
init|=
name|checkUseEntryPoint
argument_list|(
name|compLaunchContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|useEntryPoint
condition|)
block|{
name|String
name|launchCommand
init|=
name|compLaunchContext
operator|.
name|getLaunchCommand
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|launchCommand
argument_list|)
condition|)
block|{
if|if
condition|(
name|launchCommand
operator|.
name|contains
argument_list|(
literal|" "
argument_list|)
condition|)
block|{
comment|// convert space delimiter command to exec format
name|launchCommand
operator|=
name|ProviderUtils
operator|.
name|replaceSpacesWithDelimiter
argument_list|(
name|launchCommand
argument_list|,
literal|","
argument_list|)
expr_stmt|;
block|}
name|launcher
operator|.
name|addCommand
argument_list|(
name|launchCommand
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// substitute launch command
name|String
name|launchCommand
init|=
name|compLaunchContext
operator|.
name|getLaunchCommand
argument_list|()
decl_stmt|;
comment|// docker container may have empty commands
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|launchCommand
argument_list|)
condition|)
block|{
name|launchCommand
operator|=
name|ProviderUtils
operator|.
name|substituteStrWithTokens
argument_list|(
name|launchCommand
argument_list|,
name|tokensForSubstitution
argument_list|)
expr_stmt|;
name|CommandLineBuilder
name|operation
init|=
operator|new
name|CommandLineBuilder
argument_list|()
decl_stmt|;
name|operation
operator|.
name|add
argument_list|(
name|launchCommand
argument_list|)
expr_stmt|;
name|operation
operator|.
name|addOutAndErrFiles
argument_list|(
name|OUT_FILE
argument_list|,
name|ERR_FILE
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|addCommand
argument_list|(
name|operation
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

