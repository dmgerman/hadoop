begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.docker
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
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
name|slider
operator|.
name|api
operator|.
name|resource
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
name|slider
operator|.
name|common
operator|.
name|tools
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
name|slider
operator|.
name|core
operator|.
name|launch
operator|.
name|ContainerLauncher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|AbstractProviderService
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
name|DockerProviderService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|DockerProviderService ()
specifier|protected
name|DockerProviderService
parameter_list|()
block|{
name|super
argument_list|(
name|DockerProviderService
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|processArtifact (ContainerLauncher launcher, Component component, SliderFileSystem fileSystem)
specifier|public
name|void
name|processArtifact
parameter_list|(
name|ContainerLauncher
name|launcher
parameter_list|,
name|Component
name|component
parameter_list|,
name|SliderFileSystem
name|fileSystem
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
name|component
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
name|component
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|DOCKER_NETWORK
argument_list|,
name|DEFAULT_DOCKER_NETWORK
argument_list|)
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|setRunPrivilegedContainer
argument_list|(
name|component
operator|.
name|getRunPrivilegedContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

