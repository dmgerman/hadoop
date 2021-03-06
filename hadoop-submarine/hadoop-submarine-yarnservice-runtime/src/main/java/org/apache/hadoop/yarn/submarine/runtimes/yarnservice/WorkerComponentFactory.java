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
name|PyTorchRunJobParameters
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
name|client
operator|.
name|cli
operator|.
name|runjob
operator|.
name|Framework
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
name|LaunchCommandFactory
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
name|PyTorchLaunchCommandFactory
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
name|pytorch
operator|.
name|component
operator|.
name|PyTorchWorkerComponent
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
name|tensorflow
operator|.
name|component
operator|.
name|TensorFlowWorkerComponent
import|;
end_import

begin_comment
comment|/**  * Factory class that helps creating Native Service components.  */
end_comment

begin_class
DECL|class|WorkerComponentFactory
specifier|public
class|class
name|WorkerComponentFactory
block|{
DECL|field|fsOperations
specifier|private
specifier|final
name|FileSystemOperations
name|fsOperations
decl_stmt|;
DECL|field|remoteDirectoryManager
specifier|private
specifier|final
name|RemoteDirectoryManager
name|remoteDirectoryManager
decl_stmt|;
DECL|field|parameters
specifier|private
specifier|final
name|RunJobParameters
name|parameters
decl_stmt|;
DECL|field|launchCommandFactory
specifier|private
specifier|final
name|LaunchCommandFactory
name|launchCommandFactory
decl_stmt|;
DECL|field|yarnConfig
specifier|private
specifier|final
name|Configuration
name|yarnConfig
decl_stmt|;
DECL|method|WorkerComponentFactory (FileSystemOperations fsOperations, RemoteDirectoryManager remoteDirectoryManager, RunJobParameters parameters, LaunchCommandFactory launchCommandFactory, Configuration yarnConfig)
name|WorkerComponentFactory
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
name|LaunchCommandFactory
name|launchCommandFactory
parameter_list|,
name|Configuration
name|yarnConfig
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
comment|/**    * Creates either a TensorFlow or a PyTorch Native Service component.    */
DECL|method|create (Framework framework, Role role)
specifier|public
name|AbstractComponent
name|create
parameter_list|(
name|Framework
name|framework
parameter_list|,
name|Role
name|role
parameter_list|)
block|{
if|if
condition|(
name|framework
operator|==
name|Framework
operator|.
name|TENSORFLOW
condition|)
block|{
return|return
operator|new
name|TensorFlowWorkerComponent
argument_list|(
name|fsOperations
argument_list|,
name|remoteDirectoryManager
argument_list|,
operator|(
name|TensorFlowRunJobParameters
operator|)
name|parameters
argument_list|,
name|role
argument_list|,
operator|(
name|TensorFlowLaunchCommandFactory
operator|)
name|launchCommandFactory
argument_list|,
name|yarnConfig
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|framework
operator|==
name|Framework
operator|.
name|PYTORCH
condition|)
block|{
return|return
operator|new
name|PyTorchWorkerComponent
argument_list|(
name|fsOperations
argument_list|,
name|remoteDirectoryManager
argument_list|,
operator|(
name|PyTorchRunJobParameters
operator|)
name|parameters
argument_list|,
name|role
argument_list|,
operator|(
name|PyTorchLaunchCommandFactory
operator|)
name|launchCommandFactory
argument_list|,
name|yarnConfig
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Only supported frameworks are: "
operator|+
name|Framework
operator|.
name|getValues
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

