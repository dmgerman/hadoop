begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.gpu
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
name|linux
operator|.
name|resources
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|util
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
name|privileged
operator|.
name|PrivilegedOperation
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
name|PrivilegedOperationException
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
name|ResourceHandlerException
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
name|DockerLinuxContainerRuntime
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
name|gpu
operator|.
name|GpuDevice
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
name|gpu
operator|.
name|GpuDiscoverer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
DECL|class|GpuResourceHandlerImpl
specifier|public
class|class
name|GpuResourceHandlerImpl
implements|implements
name|ResourceHandler
block|{
DECL|field|LOG
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|GpuResourceHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// This will be used by container-executor to add necessary clis
DECL|field|EXCLUDED_GPUS_CLI_OPTION
specifier|public
specifier|static
specifier|final
name|String
name|EXCLUDED_GPUS_CLI_OPTION
init|=
literal|"--excluded_gpus"
decl_stmt|;
DECL|field|CONTAINER_ID_CLI_OPTION
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_ID_CLI_OPTION
init|=
literal|"--container_id"
decl_stmt|;
DECL|field|nmContext
specifier|private
specifier|final
name|Context
name|nmContext
decl_stmt|;
DECL|field|gpuAllocator
specifier|private
specifier|final
name|GpuResourceAllocator
name|gpuAllocator
decl_stmt|;
DECL|field|cGroupsHandler
specifier|private
specifier|final
name|CGroupsHandler
name|cGroupsHandler
decl_stmt|;
DECL|field|privilegedOperationExecutor
specifier|private
specifier|final
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
decl_stmt|;
DECL|field|gpuDiscoverer
specifier|private
specifier|final
name|GpuDiscoverer
name|gpuDiscoverer
decl_stmt|;
DECL|method|GpuResourceHandlerImpl (Context nmContext, CGroupsHandler cGroupsHandler, PrivilegedOperationExecutor privilegedOperationExecutor, GpuDiscoverer gpuDiscoverer)
specifier|public
name|GpuResourceHandlerImpl
parameter_list|(
name|Context
name|nmContext
parameter_list|,
name|CGroupsHandler
name|cGroupsHandler
parameter_list|,
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|,
name|GpuDiscoverer
name|gpuDiscoverer
parameter_list|)
block|{
name|this
operator|.
name|nmContext
operator|=
name|nmContext
expr_stmt|;
name|this
operator|.
name|cGroupsHandler
operator|=
name|cGroupsHandler
expr_stmt|;
name|this
operator|.
name|privilegedOperationExecutor
operator|=
name|privilegedOperationExecutor
expr_stmt|;
name|this
operator|.
name|gpuAllocator
operator|=
operator|new
name|GpuResourceAllocator
argument_list|(
name|nmContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|gpuDiscoverer
operator|=
name|gpuDiscoverer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bootstrap (Configuration configuration)
specifier|public
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|bootstrap
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|List
argument_list|<
name|GpuDevice
argument_list|>
name|usableGpus
decl_stmt|;
try|try
block|{
name|usableGpus
operator|=
name|gpuDiscoverer
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
if|if
condition|(
name|usableGpus
operator|==
literal|null
operator|||
name|usableGpus
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"GPU is enabled on the NodeManager, but couldn't find "
operator|+
literal|"any usable GPU devices, please double check configuration!"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception when trying to get usable GPU device"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
for|for
control|(
name|GpuDevice
name|gpu
range|:
name|usableGpus
control|)
block|{
name|gpuAllocator
operator|.
name|addGpu
argument_list|(
name|gpu
argument_list|)
expr_stmt|;
block|}
comment|// And initialize cgroups
name|this
operator|.
name|cGroupsHandler
operator|.
name|initializeCGroupController
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|DEVICES
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|preStart (Container container)
specifier|public
specifier|synchronized
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|preStart
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|String
name|containerIdStr
init|=
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Assign Gpus to container if requested some.
name|GpuResourceAllocator
operator|.
name|GpuAllocation
name|allocation
init|=
name|gpuAllocator
operator|.
name|assignGpus
argument_list|(
name|container
argument_list|)
decl_stmt|;
comment|// Create device cgroups for the container
name|cGroupsHandler
operator|.
name|createCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|DEVICES
argument_list|,
name|containerIdStr
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DockerLinuxContainerRuntime
operator|.
name|isDockerContainerRequested
argument_list|(
name|nmContext
operator|.
name|getConf
argument_list|()
argument_list|,
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
argument_list|)
condition|)
block|{
comment|// Write to devices cgroup only for non-docker container. The reason is
comment|// docker engine runtime runc do the devices cgroups initialize in the
comment|// pre-hook, see:
comment|//   https://github.com/opencontainers/runc/blob/master/libcontainer/configs/device_defaults.go
comment|//
comment|// YARN by default runs docker container inside cgroup, if we setup cgroups
comment|// devices.deny for the parent cgroup for launched container, we can see
comment|// errors like: failed to write c *:* m to devices.allow:
comment|// write path-to-parent-cgroup/<container-id>/devices.allow:
comment|// operation not permitted.
comment|//
comment|// To avoid this happen, if docker is requested when container being
comment|// launched, we will not setup devices.deny for the container. Instead YARN
comment|// will pass --device parameter to docker engine. See NvidiaDockerV1CommandPlugin
try|try
block|{
comment|// Execute c-e to setup GPU isolation before launch the container
name|PrivilegedOperation
name|privilegedOperation
init|=
operator|new
name|PrivilegedOperation
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|GPU
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|CONTAINER_ID_CLI_OPTION
argument_list|,
name|containerIdStr
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|allocation
operator|.
name|getDeniedGPUs
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|minorNumbers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|GpuDevice
name|deniedGpu
range|:
name|allocation
operator|.
name|getDeniedGPUs
argument_list|()
control|)
block|{
name|minorNumbers
operator|.
name|add
argument_list|(
name|deniedGpu
operator|.
name|getMinorNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|privilegedOperation
operator|.
name|appendArgs
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|EXCLUDED_GPUS_CLI_OPTION
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|minorNumbers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|privilegedOperationExecutor
operator|.
name|executePrivilegedOperation
argument_list|(
name|privilegedOperation
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PrivilegedOperationException
name|e
parameter_list|)
block|{
name|cGroupsHandler
operator|.
name|deleteCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|DEVICES
argument_list|,
name|containerIdStr
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not update cgroup for container"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
operator|new
name|PrivilegedOperation
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|ADD_PID_TO_CGROUP
argument_list|,
name|PrivilegedOperation
operator|.
name|CGROUP_ARG_PREFIX
operator|+
name|cGroupsHandler
operator|.
name|getPathForCGroupTasks
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|DEVICES
argument_list|,
name|containerIdStr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getGpuAllocator ()
specifier|public
name|GpuResourceAllocator
name|getGpuAllocator
parameter_list|()
block|{
return|return
name|gpuAllocator
return|;
block|}
annotation|@
name|Override
DECL|method|reacquireContainer (ContainerId containerId)
specifier|public
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|reacquireContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|gpuAllocator
operator|.
name|recoverAssignedGpus
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|updateContainer (Container container)
specifier|public
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|updateContainer
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|postComplete ( ContainerId containerId)
specifier|public
specifier|synchronized
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|postComplete
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|gpuAllocator
operator|.
name|cleanupAssignGpus
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|cGroupsHandler
operator|.
name|deleteCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|DEVICES
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|teardown ()
specifier|public
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|teardown
parameter_list|()
throws|throws
name|ResourceHandlerException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|GpuResourceHandlerImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"{"
operator|+
literal|"gpuAllocator="
operator|+
name|gpuAllocator
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

