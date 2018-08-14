begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.provider.defaultImpl
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
name|defaultImpl
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|DefaultProviderService
specifier|public
class|class
name|DefaultProviderService
extends|extends
name|AbstractProviderService
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
block|{   }
block|}
end_class

end_unit

