begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.deviceframework
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
name|deviceframework
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|api
operator|.
name|deviceplugin
operator|.
name|Device
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
name|api
operator|.
name|deviceplugin
operator|.
name|DevicePlugin
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
name|api
operator|.
name|deviceplugin
operator|.
name|DeviceRuntimeSpec
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
name|api
operator|.
name|deviceplugin
operator|.
name|YarnRuntimeType
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
name|OCIContainerRuntime
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * The Hooks into container lifecycle.  * Get device list from device plugin in {@code bootstrap}  * Assign devices for a container in {@code preStart}  * Restore statue in {@code reacquireContainer}  * Recycle devices from container in {@code postComplete}  * */
end_comment

begin_class
DECL|class|DeviceResourceHandlerImpl
specifier|public
class|class
name|DeviceResourceHandlerImpl
implements|implements
name|ResourceHandler
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DeviceResourceHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceName
specifier|private
specifier|final
name|String
name|resourceName
decl_stmt|;
DECL|field|devicePlugin
specifier|private
specifier|final
name|DevicePlugin
name|devicePlugin
decl_stmt|;
DECL|field|deviceMappingManager
specifier|private
specifier|final
name|DeviceMappingManager
name|deviceMappingManager
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
DECL|field|devicePluginAdapter
specifier|private
specifier|final
name|DevicePluginAdapter
name|devicePluginAdapter
decl_stmt|;
DECL|field|nmContext
specifier|private
specifier|final
name|Context
name|nmContext
decl_stmt|;
DECL|field|shellWrapper
specifier|private
name|ShellWrapper
name|shellWrapper
decl_stmt|;
comment|// This will be used by container-executor to add necessary clis
DECL|field|EXCLUDED_DEVICES_CLI_OPTION
specifier|public
specifier|static
specifier|final
name|String
name|EXCLUDED_DEVICES_CLI_OPTION
init|=
literal|"--excluded_devices"
decl_stmt|;
DECL|field|ALLOWED_DEVICES_CLI_OPTION
specifier|public
specifier|static
specifier|final
name|String
name|ALLOWED_DEVICES_CLI_OPTION
init|=
literal|"--allowed_devices"
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
DECL|method|DeviceResourceHandlerImpl (String resName, DevicePluginAdapter devPluginAdapter, DeviceMappingManager devMappingManager, CGroupsHandler cgHandler, PrivilegedOperationExecutor operation, Context ctx)
specifier|public
name|DeviceResourceHandlerImpl
parameter_list|(
name|String
name|resName
parameter_list|,
name|DevicePluginAdapter
name|devPluginAdapter
parameter_list|,
name|DeviceMappingManager
name|devMappingManager
parameter_list|,
name|CGroupsHandler
name|cgHandler
parameter_list|,
name|PrivilegedOperationExecutor
name|operation
parameter_list|,
name|Context
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|devicePluginAdapter
operator|=
name|devPluginAdapter
expr_stmt|;
name|this
operator|.
name|resourceName
operator|=
name|resName
expr_stmt|;
name|this
operator|.
name|devicePlugin
operator|=
name|devPluginAdapter
operator|.
name|getDevicePlugin
argument_list|()
expr_stmt|;
name|this
operator|.
name|cGroupsHandler
operator|=
name|cgHandler
expr_stmt|;
name|this
operator|.
name|privilegedOperationExecutor
operator|=
name|operation
expr_stmt|;
name|this
operator|.
name|deviceMappingManager
operator|=
name|devMappingManager
expr_stmt|;
name|this
operator|.
name|nmContext
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|shellWrapper
operator|=
operator|new
name|ShellWrapper
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|DeviceResourceHandlerImpl (String resName, DevicePluginAdapter devPluginAdapter, DeviceMappingManager devMappingManager, CGroupsHandler cgHandler, PrivilegedOperationExecutor operation, Context ctx, ShellWrapper shell)
specifier|public
name|DeviceResourceHandlerImpl
parameter_list|(
name|String
name|resName
parameter_list|,
name|DevicePluginAdapter
name|devPluginAdapter
parameter_list|,
name|DeviceMappingManager
name|devMappingManager
parameter_list|,
name|CGroupsHandler
name|cgHandler
parameter_list|,
name|PrivilegedOperationExecutor
name|operation
parameter_list|,
name|Context
name|ctx
parameter_list|,
name|ShellWrapper
name|shell
parameter_list|)
block|{
name|this
operator|.
name|devicePluginAdapter
operator|=
name|devPluginAdapter
expr_stmt|;
name|this
operator|.
name|resourceName
operator|=
name|resName
expr_stmt|;
name|this
operator|.
name|devicePlugin
operator|=
name|devPluginAdapter
operator|.
name|getDevicePlugin
argument_list|()
expr_stmt|;
name|this
operator|.
name|cGroupsHandler
operator|=
name|cgHandler
expr_stmt|;
name|this
operator|.
name|privilegedOperationExecutor
operator|=
name|operation
expr_stmt|;
name|this
operator|.
name|deviceMappingManager
operator|=
name|devMappingManager
expr_stmt|;
name|this
operator|.
name|nmContext
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|shellWrapper
operator|=
name|shell
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
name|Set
argument_list|<
name|Device
argument_list|>
name|availableDevices
init|=
literal|null
decl_stmt|;
try|try
block|{
name|availableDevices
operator|=
name|devicePlugin
operator|.
name|getDevices
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Exception thrown from"
operator|+
literal|" plugin's \"getDevices\""
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * We won't fail the NM if plugin returns invalid value here.      * */
if|if
condition|(
name|availableDevices
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Bootstrap "
operator|+
name|resourceName
operator|+
literal|" failed. Null value got from plugin's getDevices method"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Add device set. Here we trust the plugin's return value
name|deviceMappingManager
operator|.
name|addDeviceSet
argument_list|(
name|resourceName
argument_list|,
name|availableDevices
argument_list|)
expr_stmt|;
comment|// Init cgroups
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
name|DeviceMappingManager
operator|.
name|DeviceAllocation
name|allocation
init|=
name|deviceMappingManager
operator|.
name|assignDevices
argument_list|(
name|resourceName
argument_list|,
name|container
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Allocated to {}: {}"
argument_list|,
name|containerIdStr
argument_list|,
name|allocation
argument_list|)
expr_stmt|;
name|DeviceRuntimeSpec
name|spec
decl_stmt|;
try|try
block|{
name|spec
operator|=
name|devicePlugin
operator|.
name|onDevicesAllocated
argument_list|(
name|allocation
operator|.
name|getAllowed
argument_list|()
argument_list|,
name|YarnRuntimeType
operator|.
name|RUNTIME_DEFAULT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Exception thrown from"
operator|+
literal|" plugin's \"onDeviceAllocated\""
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
comment|// cgroups operation based on allocation
if|if
condition|(
name|spec
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Runtime spec in non-Docker container is not supported yet!"
argument_list|)
expr_stmt|;
block|}
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
comment|// non-Docker, use cgroups to do isolation
if|if
condition|(
operator|!
name|OCIContainerRuntime
operator|.
name|isOCICompliantContainerRequested
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
name|tryIsolateDevices
argument_list|(
name|allocation
argument_list|,
name|containerIdStr
argument_list|)
expr_stmt|;
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
comment|/**    * Try set cgroup devices params for the container using container-executor.    * If it has real device major number, minor number or dev path,    * we'll do the enforcement. Otherwise, won't do it.    *    * */
DECL|method|tryIsolateDevices ( DeviceMappingManager.DeviceAllocation allocation, String containerIdStr)
specifier|private
name|void
name|tryIsolateDevices
parameter_list|(
name|DeviceMappingManager
operator|.
name|DeviceAllocation
name|allocation
parameter_list|,
name|String
name|containerIdStr
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
try|try
block|{
comment|// Execute c-e to setup device isolation before launch the container
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
name|DEVICE
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
name|boolean
name|needNativeDeviceOperation
init|=
literal|false
decl_stmt|;
name|int
name|majorNumber
decl_stmt|;
name|int
name|minorNumber
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|devNumbers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|allocation
operator|.
name|getDenied
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|DeviceType
name|devType
decl_stmt|;
for|for
control|(
name|Device
name|deniedDevice
range|:
name|allocation
operator|.
name|getDenied
argument_list|()
control|)
block|{
name|majorNumber
operator|=
name|deniedDevice
operator|.
name|getMajorNumber
argument_list|()
expr_stmt|;
name|minorNumber
operator|=
name|deniedDevice
operator|.
name|getMinorNumber
argument_list|()
expr_stmt|;
comment|// Add device type
name|devType
operator|=
name|getDeviceType
argument_list|(
name|deniedDevice
argument_list|)
expr_stmt|;
if|if
condition|(
name|devType
operator|!=
literal|null
condition|)
block|{
name|devNumbers
operator|.
name|add
argument_list|(
name|devType
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|majorNumber
operator|+
literal|":"
operator|+
name|minorNumber
operator|+
literal|"-rwm"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|devNumbers
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|privilegedOperation
operator|.
name|appendArgs
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|EXCLUDED_DEVICES_CLI_OPTION
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|devNumbers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|needNativeDeviceOperation
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|allocation
operator|.
name|getAllowed
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|devNumbers
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Device
name|allowedDevice
range|:
name|allocation
operator|.
name|getAllowed
argument_list|()
control|)
block|{
name|majorNumber
operator|=
name|allowedDevice
operator|.
name|getMajorNumber
argument_list|()
expr_stmt|;
name|minorNumber
operator|=
name|allowedDevice
operator|.
name|getMinorNumber
argument_list|()
expr_stmt|;
if|if
condition|(
name|majorNumber
operator|!=
operator|-
literal|1
operator|&&
name|minorNumber
operator|!=
operator|-
literal|1
condition|)
block|{
name|devNumbers
operator|.
name|add
argument_list|(
name|majorNumber
operator|+
literal|":"
operator|+
name|minorNumber
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|devNumbers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|privilegedOperation
operator|.
name|appendArgs
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ALLOWED_DEVICES_CLI_OPTION
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|devNumbers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|needNativeDeviceOperation
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|needNativeDeviceOperation
condition|)
block|{
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
block|}
annotation|@
name|Override
DECL|method|reacquireContainer ( ContainerId containerId)
specifier|public
specifier|synchronized
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
name|deviceMappingManager
operator|.
name|recoverAssignedDevices
argument_list|(
name|resourceName
argument_list|,
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
name|deviceMappingManager
operator|.
name|cleanupAssignedDevices
argument_list|(
name|resourceName
argument_list|,
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
name|DeviceResourceHandlerImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"{"
operator|+
literal|"resourceName='"
operator|+
name|resourceName
operator|+
literal|'\''
operator|+
literal|", devicePlugin="
operator|+
name|devicePlugin
operator|+
literal|", devicePluginAdapter="
operator|+
name|devicePluginAdapter
operator|+
literal|'}'
return|;
block|}
DECL|method|getDeviceType (Device device)
specifier|public
name|DeviceType
name|getDeviceType
parameter_list|(
name|Device
name|device
parameter_list|)
block|{
name|String
name|devName
init|=
name|device
operator|.
name|getDevPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|devName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Empty device path provided, try to get device type from "
operator|+
literal|"major:minor device number"
argument_list|)
expr_stmt|;
name|int
name|major
init|=
name|device
operator|.
name|getMajorNumber
argument_list|()
decl_stmt|;
name|int
name|minor
init|=
name|device
operator|.
name|getMinorNumber
argument_list|()
decl_stmt|;
if|if
condition|(
name|major
operator|==
operator|-
literal|1
operator|&&
name|minor
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Non device number provided, cannot decide the device type"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Get type from the device numbers
return|return
name|getDeviceTypeFromDeviceNumber
argument_list|(
name|device
operator|.
name|getMajorNumber
argument_list|()
argument_list|,
name|device
operator|.
name|getMinorNumber
argument_list|()
argument_list|)
return|;
block|}
name|DeviceType
name|deviceType
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Try to get device type from device path: {}"
argument_list|,
name|devName
argument_list|)
expr_stmt|;
name|String
name|output
init|=
name|shellWrapper
operator|.
name|getDeviceFileType
argument_list|(
name|devName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"stat output:{}"
argument_list|,
name|output
argument_list|)
expr_stmt|;
name|deviceType
operator|=
name|output
operator|.
name|startsWith
argument_list|(
literal|"c"
argument_list|)
condition|?
name|DeviceType
operator|.
name|CHAR
else|:
name|DeviceType
operator|.
name|BLOCK
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Failed to get device type from stat "
operator|+
name|devName
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|deviceType
return|;
block|}
comment|/**    * Get the device type used for cgroups value set.    * If sys file "/sys/dev/block/major:minor" exists, it's block device.    * Otherwise, it's char device. An exception is that Nvidia GPU doesn't    * create this sys file. so assume character device by default.    */
DECL|method|getDeviceTypeFromDeviceNumber (int major, int minor)
specifier|public
name|DeviceType
name|getDeviceTypeFromDeviceNumber
parameter_list|(
name|int
name|major
parameter_list|,
name|int
name|minor
parameter_list|)
block|{
if|if
condition|(
name|shellWrapper
operator|.
name|existFile
argument_list|(
literal|"/sys/dev/block/"
operator|+
name|major
operator|+
literal|":"
operator|+
name|minor
argument_list|)
condition|)
block|{
return|return
name|DeviceType
operator|.
name|BLOCK
return|;
block|}
return|return
name|DeviceType
operator|.
name|CHAR
return|;
block|}
comment|/**    * Enum for Linux device type. Used when updating device cgroups params.    * "b" represents block device    * "c" represents character device    * */
DECL|enum|DeviceType
specifier|private
enum|enum
name|DeviceType
block|{
DECL|enumConstant|BLOCK
name|BLOCK
argument_list|(
literal|"b"
argument_list|)
block|,
DECL|enumConstant|CHAR
name|CHAR
argument_list|(
literal|"c"
argument_list|)
block|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|DeviceType (String n)
name|DeviceType
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|n
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
block|}
end_class

end_unit

