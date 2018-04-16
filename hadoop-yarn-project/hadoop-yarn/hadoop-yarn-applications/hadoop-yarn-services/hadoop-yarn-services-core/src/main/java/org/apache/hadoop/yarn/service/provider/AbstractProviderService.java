begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.provider
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
name|lang
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
name|conf
operator|.
name|YarnServiceConf
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
name|Component
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
name|conf
operator|.
name|YarnServiceConstants
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
name|utils
operator|.
name|ServiceUtils
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
name|ServiceContext
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|conf
operator|.
name|YarnServiceConf
operator|.
name|CONTAINER_FAILURES_VALIDITY_INTERVAL
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
name|conf
operator|.
name|YarnServiceConf
operator|.
name|CONTAINER_RETRY_INTERVAL
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
name|conf
operator|.
name|YarnServiceConf
operator|.
name|CONTAINER_RETRY_MAX
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
name|conf
operator|.
name|YarnServiceConf
operator|.
name|DEFAULT_CONTAINER_FAILURES_VALIDITY_INTERVAL
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
name|conf
operator|.
name|YarnServiceConf
operator|.
name|DEFAULT_CONTAINER_RETRY_INTERVAL
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
name|conf
operator|.
name|YarnServiceConf
operator|.
name|DEFAULT_CONTAINER_RETRY_MAX
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
name|utils
operator|.
name|ServiceApiUtil
operator|.
name|$
import|;
end_import

begin_class
DECL|class|AbstractProviderService
specifier|public
specifier|abstract
class|class
name|AbstractProviderService
implements|implements
name|ProviderService
implements|,
name|YarnServiceConstants
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractProviderService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|processArtifact (AbstractLauncher launcher, ComponentInstance compInstance, SliderFileSystem fileSystem, Service service)
specifier|public
specifier|abstract
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
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|buildContainerLaunchContext (AbstractLauncher launcher, Service service, ComponentInstance instance, SliderFileSystem fileSystem, Configuration yarnConf, Container container)
specifier|public
name|void
name|buildContainerLaunchContext
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|Component
name|component
init|=
name|instance
operator|.
name|getComponent
argument_list|()
operator|.
name|getComponentSpec
argument_list|()
decl_stmt|;
empty_stmt|;
name|processArtifact
argument_list|(
name|launcher
argument_list|,
name|instance
argument_list|,
name|fileSystem
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|ServiceContext
name|context
init|=
name|instance
operator|.
name|getComponent
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|// Generate tokens (key-value pair) for config substitution.
comment|// Get pre-defined tokens
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|globalTokens
init|=
name|instance
operator|.
name|getComponent
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|globalTokens
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokensForSubstitution
init|=
name|ProviderUtils
operator|.
name|initCompTokensForSubstitute
argument_list|(
name|instance
argument_list|,
name|container
argument_list|)
decl_stmt|;
name|tokensForSubstitution
operator|.
name|putAll
argument_list|(
name|globalTokens
argument_list|)
expr_stmt|;
comment|// Set the environment variables in launcher
name|launcher
operator|.
name|putEnv
argument_list|(
name|ServiceUtils
operator|.
name|buildEnvMap
argument_list|(
name|component
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|tokensForSubstitution
argument_list|)
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|setEnv
argument_list|(
literal|"WORK_DIR"
argument_list|,
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|PWD
operator|.
name|$
argument_list|()
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|setEnv
argument_list|(
literal|"LOG_DIR"
argument_list|,
name|ApplicationConstants
operator|.
name|LOG_DIR_EXPANSION_VAR
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getenv
argument_list|(
name|HADOOP_USER_NAME
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|launcher
operator|.
name|setEnv
argument_list|(
name|HADOOP_USER_NAME
argument_list|,
name|System
operator|.
name|getenv
argument_list|(
name|HADOOP_USER_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|launcher
operator|.
name|setEnv
argument_list|(
literal|"LANG"
argument_list|,
literal|"en_US.UTF-8"
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|setEnv
argument_list|(
literal|"LC_ALL"
argument_list|,
literal|"en_US.UTF-8"
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|setEnv
argument_list|(
literal|"LANGUAGE"
argument_list|,
literal|"en_US.UTF-8"
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|launcher
operator|.
name|getEnv
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|tokensForSubstitution
operator|.
name|put
argument_list|(
name|$
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//TODO add component host tokens?
comment|//    ProviderUtils.addComponentHostTokens(tokensForSubstitution, amState);
comment|// create config file on hdfs and add local resource
name|ProviderUtils
operator|.
name|createConfigFileAndAddLocalResource
argument_list|(
name|launcher
argument_list|,
name|fileSystem
argument_list|,
name|component
argument_list|,
name|tokensForSubstitution
argument_list|,
name|instance
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|// substitute launch command
name|String
name|launchCommand
init|=
name|component
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
comment|// By default retry forever every 30 seconds
name|launcher
operator|.
name|setRetryContext
argument_list|(
name|YarnServiceConf
operator|.
name|getInt
argument_list|(
name|CONTAINER_RETRY_MAX
argument_list|,
name|DEFAULT_CONTAINER_RETRY_MAX
argument_list|,
name|component
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|yarnConf
argument_list|)
argument_list|,
name|YarnServiceConf
operator|.
name|getInt
argument_list|(
name|CONTAINER_RETRY_INTERVAL
argument_list|,
name|DEFAULT_CONTAINER_RETRY_INTERVAL
argument_list|,
name|component
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|yarnConf
argument_list|)
argument_list|,
name|YarnServiceConf
operator|.
name|getLong
argument_list|(
name|CONTAINER_FAILURES_VALIDITY_INTERVAL
argument_list|,
name|DEFAULT_CONTAINER_FAILURES_VALIDITY_INTERVAL
argument_list|,
name|component
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|yarnConf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

