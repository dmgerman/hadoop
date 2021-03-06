begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice.pytorch.component
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
name|pytorch
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
name|PyTorchLaunchCommandFactory
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

begin_comment
comment|/**  * Component implementation for Worker process of PyTorch.  */
end_comment

begin_class
DECL|class|PyTorchWorkerComponent
specifier|public
class|class
name|PyTorchWorkerComponent
extends|extends
name|AbstractComponent
block|{
DECL|method|PyTorchWorkerComponent (FileSystemOperations fsOperations, RemoteDirectoryManager remoteDirectoryManager, PyTorchRunJobParameters parameters, Role role, PyTorchLaunchCommandFactory launchCommandFactory, Configuration yarnConfig)
specifier|public
name|PyTorchWorkerComponent
parameter_list|(
name|FileSystemOperations
name|fsOperations
parameter_list|,
name|RemoteDirectoryManager
name|remoteDirectoryManager
parameter_list|,
name|PyTorchRunJobParameters
name|parameters
parameter_list|,
name|Role
name|role
parameter_list|,
name|PyTorchLaunchCommandFactory
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
name|role
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
return|return
name|createComponentInternal
argument_list|()
return|;
block|}
block|}
end_class

end_unit

