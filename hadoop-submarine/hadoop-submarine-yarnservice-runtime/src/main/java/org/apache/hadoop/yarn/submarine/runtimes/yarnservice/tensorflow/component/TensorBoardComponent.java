begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice.tensorflow.component
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|tensorflow
operator|.
name|component
package|;
end_package

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
name|api
operator|.
name|records
operator|.
name|Component
operator|.
name|RestartPolicyEnum
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|runjob
operator|.
name|RunJobParameters
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|runjob
operator|.
name|TensorFlowRunJobParameters
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
name|submarine
operator|.
name|common
operator|.
name|api
operator|.
name|TensorFlowRole
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
name|submarine
operator|.
name|common
operator|.
name|fs
operator|.
name|RemoteDirectoryManager
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
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|AbstractComponent
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
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|FileSystemOperations
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
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|YarnServiceUtils
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
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|command
operator|.
name|TensorFlowLaunchCommandFactory
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
name|Objects
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
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|tensorflow
operator|.
name|TensorFlowCommons
operator|.
name|addCommonEnvironments
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
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|tensorflow
operator|.
name|TensorFlowCommons
operator|.
name|getDNSDomain
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
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|tensorflow
operator|.
name|TensorFlowCommons
operator|.
name|getUserName
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
name|submarine
operator|.
name|utils
operator|.
name|DockerUtilities
operator|.
name|getDockerArtifact
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
name|submarine
operator|.
name|utils
operator|.
name|SubmarineResourceUtils
operator|.
name|convertYarnResourceToServiceResource
import|;
end_import

begin_comment
comment|/**  * Component implementation for Tensorboard's Tensorboard.  */
end_comment

begin_class
DECL|class|TensorBoardComponent
specifier|public
class|class
name|TensorBoardComponent
extends|extends
name|AbstractComponent
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TensorBoardComponent
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TENSORBOARD_QUICKLINK_LABEL
specifier|public
specifier|static
specifier|final
name|String
name|TENSORBOARD_QUICKLINK_LABEL
init|=
literal|"Tensorboard"
decl_stmt|;
DECL|field|DEFAULT_PORT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_PORT
init|=
literal|6006
decl_stmt|;
comment|//computed fields
DECL|field|tensorboardLink
specifier|private
name|String
name|tensorboardLink
decl_stmt|;
DECL|method|TensorBoardComponent (FileSystemOperations fsOperations, RemoteDirectoryManager remoteDirectoryManager, RunJobParameters parameters, TensorFlowLaunchCommandFactory launchCommandFactory, Configuration yarnConfig)
specifier|public
name|TensorBoardComponent
parameter_list|(
name|FileSystemOperations
name|fsOperations
parameter_list|,
name|RemoteDirectoryManager
name|remoteDirectoryManager
parameter_list|,
name|RunJobParameters
name|parameters
parameter_list|,
name|TensorFlowLaunchCommandFactory
name|launchCommandFactory
parameter_list|,
name|Configuration
name|yarnConfig
parameter_list|)
block|{
name|super
argument_list|(
name|fsOperations
argument_list|,
name|remoteDirectoryManager
argument_list|,
name|parameters
argument_list|,
name|TensorFlowRole
operator|.
name|TENSORBOARD
argument_list|,
name|yarnConfig
argument_list|,
name|launchCommandFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponent ()
specifier|public
name|Component
name|createComponent
parameter_list|()
throws|throws
name|IOException
block|{
name|TensorFlowRunJobParameters
name|tensorFlowParams
init|=
operator|(
name|TensorFlowRunJobParameters
operator|)
name|this
operator|.
name|parameters
decl_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|tensorFlowParams
operator|.
name|getTensorboardResource
argument_list|()
argument_list|,
literal|"TensorBoard resource must not be null!"
argument_list|)
expr_stmt|;
name|Component
name|component
init|=
operator|new
name|Component
argument_list|()
decl_stmt|;
name|component
operator|.
name|setName
argument_list|(
name|role
operator|.
name|getComponentName
argument_list|()
argument_list|)
expr_stmt|;
name|component
operator|.
name|setNumberOfContainers
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|component
operator|.
name|setRestartPolicy
argument_list|(
name|RestartPolicyEnum
operator|.
name|NEVER
argument_list|)
expr_stmt|;
name|component
operator|.
name|setResource
argument_list|(
name|convertYarnResourceToServiceResource
argument_list|(
name|tensorFlowParams
operator|.
name|getTensorboardResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|tensorFlowParams
operator|.
name|getTensorboardDockerImage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|component
operator|.
name|setArtifact
argument_list|(
name|getDockerArtifact
argument_list|(
name|tensorFlowParams
operator|.
name|getTensorboardDockerImage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|addCommonEnvironments
argument_list|(
name|component
argument_list|,
name|role
argument_list|)
expr_stmt|;
name|generateLaunchCommand
argument_list|(
name|component
argument_list|)
expr_stmt|;
name|tensorboardLink
operator|=
literal|"http://"
operator|+
name|YarnServiceUtils
operator|.
name|getDNSName
argument_list|(
name|parameters
operator|.
name|getName
argument_list|()
argument_list|,
name|role
operator|.
name|getComponentName
argument_list|()
operator|+
literal|"-"
operator|+
literal|0
argument_list|,
name|getUserName
argument_list|()
argument_list|,
name|getDNSDomain
argument_list|(
name|yarnConfig
argument_list|)
argument_list|,
name|DEFAULT_PORT
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Link to tensorboard:"
operator|+
name|tensorboardLink
argument_list|)
expr_stmt|;
return|return
name|component
return|;
block|}
DECL|method|getTensorboardLink ()
specifier|public
name|String
name|getTensorboardLink
parameter_list|()
block|{
return|return
name|tensorboardLink
return|;
block|}
block|}
end_class

end_unit

