begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu
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
operator|.
name|gpu
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|exceptions
operator|.
name|YarnException
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
name|Context
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
name|privileged
operator|.
name|PrivilegedOperationExecutor
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
name|resources
operator|.
name|CGroupsHandler
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
name|resources
operator|.
name|ResourceHandler
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
name|resources
operator|.
name|gpu
operator|.
name|GpuResourceAllocator
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
name|resources
operator|.
name|gpu
operator|.
name|GpuResourceHandlerImpl
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
name|resourceplugin
operator|.
name|DockerCommandPlugin
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
name|resourceplugin
operator|.
name|NodeResourceUpdaterPlugin
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
name|resourceplugin
operator|.
name|ResourcePlugin
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
name|webapp
operator|.
name|dao
operator|.
name|NMResourceInfo
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
name|webapp
operator|.
name|dao
operator|.
name|gpu
operator|.
name|GpuDeviceInformation
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
name|webapp
operator|.
name|dao
operator|.
name|gpu
operator|.
name|NMGpuResourceInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
DECL|class|GpuResourcePlugin
specifier|public
class|class
name|GpuResourcePlugin
implements|implements
name|ResourcePlugin
block|{
DECL|field|gpuResourceHandler
specifier|private
name|GpuResourceHandlerImpl
name|gpuResourceHandler
init|=
literal|null
decl_stmt|;
DECL|field|resourceDiscoverHandler
specifier|private
name|GpuNodeResourceUpdateHandler
name|resourceDiscoverHandler
init|=
literal|null
decl_stmt|;
DECL|field|dockerCommandPlugin
specifier|private
name|DockerCommandPlugin
name|dockerCommandPlugin
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (Context context)
specifier|public
specifier|synchronized
name|void
name|initialize
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|YarnException
block|{
name|resourceDiscoverHandler
operator|=
operator|new
name|GpuNodeResourceUpdateHandler
argument_list|()
expr_stmt|;
name|GpuDiscoverer
operator|.
name|getInstance
argument_list|()
operator|.
name|initialize
argument_list|(
name|context
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|dockerCommandPlugin
operator|=
name|GpuDockerCommandPluginFactory
operator|.
name|createGpuDockerCommandPlugin
argument_list|(
name|context
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createResourceHandler ( Context context, CGroupsHandler cGroupsHandler, PrivilegedOperationExecutor privilegedOperationExecutor)
specifier|public
specifier|synchronized
name|ResourceHandler
name|createResourceHandler
parameter_list|(
name|Context
name|context
parameter_list|,
name|CGroupsHandler
name|cGroupsHandler
parameter_list|,
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|)
block|{
if|if
condition|(
name|gpuResourceHandler
operator|==
literal|null
condition|)
block|{
name|gpuResourceHandler
operator|=
operator|new
name|GpuResourceHandlerImpl
argument_list|(
name|context
argument_list|,
name|cGroupsHandler
argument_list|,
name|privilegedOperationExecutor
argument_list|)
expr_stmt|;
block|}
return|return
name|gpuResourceHandler
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeResourceHandlerInstance ()
specifier|public
specifier|synchronized
name|NodeResourceUpdaterPlugin
name|getNodeResourceHandlerInstance
parameter_list|()
block|{
return|return
name|resourceDiscoverHandler
return|;
block|}
annotation|@
name|Override
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|YarnException
block|{
comment|// Do nothing.
block|}
DECL|method|getDockerCommandPluginInstance ()
specifier|public
name|DockerCommandPlugin
name|getDockerCommandPluginInstance
parameter_list|()
block|{
return|return
name|dockerCommandPlugin
return|;
block|}
annotation|@
name|Override
DECL|method|getNMResourceInfo ()
specifier|public
name|NMResourceInfo
name|getNMResourceInfo
parameter_list|()
throws|throws
name|YarnException
block|{
name|GpuDeviceInformation
name|gpuDeviceInformation
init|=
name|GpuDiscoverer
operator|.
name|getInstance
argument_list|()
operator|.
name|getGpuDeviceInformation
argument_list|()
decl_stmt|;
name|GpuResourceAllocator
name|gpuResourceAllocator
init|=
name|gpuResourceHandler
operator|.
name|getGpuAllocator
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|GpuDevice
argument_list|>
name|totalGpus
init|=
name|gpuResourceAllocator
operator|.
name|getAllowedGpusCopy
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AssignedGpuDevice
argument_list|>
name|assignedGpuDevices
init|=
name|gpuResourceAllocator
operator|.
name|getAssignedGpusCopy
argument_list|()
decl_stmt|;
return|return
operator|new
name|NMGpuResourceInfo
argument_list|(
name|gpuDeviceInformation
argument_list|,
name|totalGpus
argument_list|,
name|assignedGpuDevices
argument_list|)
return|;
block|}
block|}
end_class

end_unit

