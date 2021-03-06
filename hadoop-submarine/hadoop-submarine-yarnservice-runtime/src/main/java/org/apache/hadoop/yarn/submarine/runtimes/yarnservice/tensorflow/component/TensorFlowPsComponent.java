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
name|command
operator|.
name|TensorFlowLaunchCommandFactory
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
comment|/**  * Component implementation for TensorFlow's PS process.  */
end_comment

begin_class
DECL|class|TensorFlowPsComponent
specifier|public
class|class
name|TensorFlowPsComponent
extends|extends
name|AbstractComponent
block|{
DECL|method|TensorFlowPsComponent (FileSystemOperations fsOperations, RemoteDirectoryManager remoteDirectoryManager, TensorFlowLaunchCommandFactory launchCommandFactory, RunJobParameters parameters, Configuration yarnConfig)
specifier|public
name|TensorFlowPsComponent
parameter_list|(
name|FileSystemOperations
name|fsOperations
parameter_list|,
name|RemoteDirectoryManager
name|remoteDirectoryManager
parameter_list|,
name|TensorFlowLaunchCommandFactory
name|launchCommandFactory
parameter_list|,
name|RunJobParameters
name|parameters
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
name|PS
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
name|getPsResource
argument_list|()
argument_list|,
literal|"PS resource must not be null!"
argument_list|)
expr_stmt|;
if|if
condition|(
name|tensorFlowParams
operator|.
name|getNumPS
argument_list|()
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Number of PS should be at least 1!"
argument_list|)
throw|;
block|}
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
operator|(
name|long
operator|)
name|tensorFlowParams
operator|.
name|getNumPS
argument_list|()
argument_list|)
expr_stmt|;
name|component
operator|.
name|setRestartPolicy
argument_list|(
name|Component
operator|.
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
name|getPsResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Override global docker image if needed.
if|if
condition|(
name|tensorFlowParams
operator|.
name|getPsDockerImage
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
name|getPsDockerImage
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
return|return
name|component
return|;
block|}
block|}
end_class

end_unit

