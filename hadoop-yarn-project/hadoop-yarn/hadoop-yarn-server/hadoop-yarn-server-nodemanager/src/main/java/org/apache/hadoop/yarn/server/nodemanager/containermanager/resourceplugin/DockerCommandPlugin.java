begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin
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
operator|.
name|containermanager
operator|.
name|resourceplugin
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
name|linux
operator|.
name|runtime
operator|.
name|docker
operator|.
name|DockerRunCommand
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
name|linux
operator|.
name|runtime
operator|.
name|docker
operator|.
name|DockerVolumeCommand
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
name|runtime
operator|.
name|ContainerExecutionException
import|;
end_import

begin_comment
comment|/**  * Interface to make different resource plugins (e.g. GPU) can update docker run  * command without adding logic to Docker runtime.  */
end_comment

begin_interface
DECL|interface|DockerCommandPlugin
specifier|public
interface|interface
name|DockerCommandPlugin
block|{
comment|/**    * Update docker run command    * @param dockerRunCommand docker run command    * @param container NM container    * @throws ContainerExecutionException if any issue occurs    */
DECL|method|updateDockerRunCommand (DockerRunCommand dockerRunCommand, Container container)
name|void
name|updateDockerRunCommand
parameter_list|(
name|DockerRunCommand
name|dockerRunCommand
parameter_list|,
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
comment|/**    * Create volume when needed.    * @param container container    * @return {@link DockerVolumeCommand} to create volume    * @throws ContainerExecutionException when any issue happens    */
DECL|method|getCreateDockerVolumeCommand (Container container)
name|DockerVolumeCommand
name|getCreateDockerVolumeCommand
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
comment|/**    * Cleanup volumes created for one docker container    * @param container container    * @return {@link DockerVolumeCommand} to remove volume    * @throws ContainerExecutionException when any issue happens    */
DECL|method|getCleanupDockerVolumesCommand (Container container)
name|DockerVolumeCommand
name|getCleanupDockerVolumesCommand
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
comment|// Add support to other docker command when required.
block|}
end_interface

end_unit

