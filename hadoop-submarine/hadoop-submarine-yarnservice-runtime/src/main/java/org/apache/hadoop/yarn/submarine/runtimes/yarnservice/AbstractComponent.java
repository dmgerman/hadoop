begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice
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
name|common
operator|.
name|api
operator|.
name|PyTorchRole
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
name|Role
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
name|command
operator|.
name|AbstractLaunchCommand
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
name|LaunchCommandFactory
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
name|getScriptFileName
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
comment|/**  * Abstract base class for Component classes.  * The implementations of this class are act like factories for  * {@link Component} instances.  * All dependencies are passed to the constructor so that child classes  * are obliged to provide matching constructors.  */
end_comment

begin_class
DECL|class|AbstractComponent
specifier|public
specifier|abstract
class|class
name|AbstractComponent
block|{
DECL|field|fsOperations
specifier|private
specifier|final
name|FileSystemOperations
name|fsOperations
decl_stmt|;
DECL|field|parameters
specifier|protected
specifier|final
name|RunJobParameters
name|parameters
decl_stmt|;
DECL|field|role
specifier|protected
specifier|final
name|Role
name|role
decl_stmt|;
DECL|field|remoteDirectoryManager
specifier|private
specifier|final
name|RemoteDirectoryManager
name|remoteDirectoryManager
decl_stmt|;
DECL|field|yarnConfig
specifier|protected
specifier|final
name|Configuration
name|yarnConfig
decl_stmt|;
DECL|field|launchCommandFactory
specifier|private
specifier|final
name|LaunchCommandFactory
name|launchCommandFactory
decl_stmt|;
comment|/**    * This is only required for testing.    */
DECL|field|localScriptFile
specifier|private
name|String
name|localScriptFile
decl_stmt|;
DECL|method|AbstractComponent (FileSystemOperations fsOperations, RemoteDirectoryManager remoteDirectoryManager, RunJobParameters parameters, Role role, Configuration yarnConfig, LaunchCommandFactory launchCommandFactory)
specifier|public
name|AbstractComponent
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
name|Role
name|role
parameter_list|,
name|Configuration
name|yarnConfig
parameter_list|,
name|LaunchCommandFactory
name|launchCommandFactory
parameter_list|)
block|{
name|this
operator|.
name|fsOperations
operator|=
name|fsOperations
expr_stmt|;
name|this
operator|.
name|remoteDirectoryManager
operator|=
name|remoteDirectoryManager
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
name|this
operator|.
name|role
operator|=
name|role
expr_stmt|;
name|this
operator|.
name|launchCommandFactory
operator|=
name|launchCommandFactory
expr_stmt|;
name|this
operator|.
name|yarnConfig
operator|=
name|yarnConfig
expr_stmt|;
block|}
DECL|method|createComponent ()
specifier|protected
specifier|abstract
name|Component
name|createComponent
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|createComponentInternal ()
specifier|protected
name|Component
name|createComponentInternal
parameter_list|()
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|this
operator|.
name|parameters
operator|.
name|getWorkerResource
argument_list|()
argument_list|,
literal|"Worker resource must not be null!"
argument_list|)
expr_stmt|;
if|if
condition|(
name|parameters
operator|.
name|getNumWorkers
argument_list|()
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Number of workers should be at least 1!"
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
if|if
condition|(
name|role
operator|.
name|equals
argument_list|(
name|TensorFlowRole
operator|.
name|PRIMARY_WORKER
argument_list|)
operator|||
name|role
operator|.
name|equals
argument_list|(
name|PyTorchRole
operator|.
name|PRIMARY_WORKER
argument_list|)
condition|)
block|{
name|component
operator|.
name|setNumberOfContainers
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
comment|// If the dependencies are upgraded to hadoop 3.3.0.
comment|// yarn.service.container-state-report-as-service-state can be replaced
comment|// with CONTAINER_STATE_REPORT_AS_SERVICE_STATE
name|component
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"yarn.service.container-state-report-as-service-state"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|component
operator|.
name|setNumberOfContainers
argument_list|(
operator|(
name|long
operator|)
name|parameters
operator|.
name|getNumWorkers
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getWorkerDockerImage
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
name|parameters
operator|.
name|getWorkerDockerImage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|component
operator|.
name|setResource
argument_list|(
name|convertYarnResourceToServiceResource
argument_list|(
name|parameters
operator|.
name|getWorkerResource
argument_list|()
argument_list|)
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
comment|/**    * Generates a command launch script on local disk,    * returns path to the script.    */
DECL|method|generateLaunchCommand (Component component)
specifier|protected
name|void
name|generateLaunchCommand
parameter_list|(
name|Component
name|component
parameter_list|)
throws|throws
name|IOException
block|{
name|AbstractLaunchCommand
name|launchCommand
init|=
name|launchCommandFactory
operator|.
name|createLaunchCommand
argument_list|(
name|role
argument_list|,
name|component
argument_list|)
decl_stmt|;
name|this
operator|.
name|localScriptFile
operator|=
name|launchCommand
operator|.
name|generateLaunchScript
argument_list|()
expr_stmt|;
name|String
name|remoteLaunchCommand
init|=
name|uploadLaunchCommand
argument_list|(
name|component
argument_list|)
decl_stmt|;
name|component
operator|.
name|setLaunchCommand
argument_list|(
name|remoteLaunchCommand
argument_list|)
expr_stmt|;
block|}
DECL|method|uploadLaunchCommand (Component component)
specifier|private
name|String
name|uploadLaunchCommand
parameter_list|(
name|Component
name|component
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|localScriptFile
argument_list|,
literal|"localScriptFile should be "
operator|+
literal|"set before calling this method!"
argument_list|)
expr_stmt|;
name|Path
name|stagingDir
init|=
name|remoteDirectoryManager
operator|.
name|getJobStagingArea
argument_list|(
name|parameters
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|destScriptFileName
init|=
name|getScriptFileName
argument_list|(
name|role
argument_list|)
decl_stmt|;
name|fsOperations
operator|.
name|uploadToRemoteFileAndLocalizeToContainerWorkDir
argument_list|(
name|stagingDir
argument_list|,
name|localScriptFile
argument_list|,
name|destScriptFileName
argument_list|,
name|component
argument_list|)
expr_stmt|;
return|return
literal|"./"
operator|+
name|destScriptFileName
return|;
block|}
DECL|method|getLocalScriptFile ()
name|String
name|getLocalScriptFile
parameter_list|()
block|{
return|return
name|localScriptFile
return|;
block|}
block|}
end_class

end_unit

