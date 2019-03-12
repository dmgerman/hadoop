begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.fpga
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
name|fpga
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|resources
operator|.
name|fpga
operator|.
name|FpgaResourceAllocator
operator|.
name|FpgaDevice
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
name|fpga
operator|.
name|AbstractFpgaVendorPlugin
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
name|fpga
operator|.
name|FpgaDiscoverer
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
import|import static
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
name|ResourceInformation
operator|.
name|FPGA_URI
import|;
end_import

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FpgaResourceHandlerImpl
specifier|public
class|class
name|FpgaResourceHandlerImpl
implements|implements
name|ResourceHandler
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FpgaResourceHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REQUEST_FPGA_IP_ID_KEY
specifier|private
specifier|final
name|String
name|REQUEST_FPGA_IP_ID_KEY
init|=
literal|"REQUESTED_FPGA_IP_ID"
decl_stmt|;
DECL|field|vendorPlugin
specifier|private
specifier|final
name|AbstractFpgaVendorPlugin
name|vendorPlugin
decl_stmt|;
DECL|field|allocator
specifier|private
specifier|final
name|FpgaResourceAllocator
name|allocator
decl_stmt|;
DECL|field|cGroupsHandler
specifier|private
specifier|final
name|CGroupsHandler
name|cGroupsHandler
decl_stmt|;
DECL|field|EXCLUDED_FPGAS_CLI_OPTION
specifier|public
specifier|static
specifier|final
name|String
name|EXCLUDED_FPGAS_CLI_OPTION
init|=
literal|"--excluded_fpgas"
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
DECL|field|privilegedOperationExecutor
specifier|private
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|FpgaResourceHandlerImpl (Context nmContext, CGroupsHandler cGroupsHandler, PrivilegedOperationExecutor privilegedOperationExecutor, AbstractFpgaVendorPlugin plugin)
specifier|public
name|FpgaResourceHandlerImpl
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
name|AbstractFpgaVendorPlugin
name|plugin
parameter_list|)
block|{
name|this
operator|.
name|allocator
operator|=
operator|new
name|FpgaResourceAllocator
argument_list|(
name|nmContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|vendorPlugin
operator|=
name|plugin
expr_stmt|;
name|FpgaDiscoverer
operator|.
name|getInstance
argument_list|()
operator|.
name|setResourceHanderPlugin
argument_list|(
name|vendorPlugin
argument_list|)
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
block|}
annotation|@
name|VisibleForTesting
DECL|method|getFpgaAllocator ()
specifier|public
name|FpgaResourceAllocator
name|getFpgaAllocator
parameter_list|()
block|{
return|return
name|allocator
return|;
block|}
DECL|method|getRequestedIPID (Container container)
specifier|public
name|String
name|getRequestedIPID
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|String
name|r
init|=
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
operator|.
name|get
argument_list|(
name|REQUEST_FPGA_IP_ID_KEY
argument_list|)
decl_stmt|;
return|return
name|r
operator|==
literal|null
condition|?
literal|""
else|:
name|r
return|;
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
comment|// The plugin should be initilized by FpgaDiscoverer already
if|if
condition|(
operator|!
name|vendorPlugin
operator|.
name|initPlugin
argument_list|(
name|configuration
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"FPGA plugin initialization failed"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"FPGA Plugin bootstrap success."
argument_list|)
expr_stmt|;
comment|// Get avialable devices minor numbers from toolchain or static configuration
name|List
argument_list|<
name|FpgaResourceAllocator
operator|.
name|FpgaDevice
argument_list|>
name|fpgaDeviceList
init|=
name|FpgaDiscoverer
operator|.
name|getInstance
argument_list|()
operator|.
name|discover
argument_list|()
decl_stmt|;
name|allocator
operator|.
name|addFpga
argument_list|(
name|vendorPlugin
operator|.
name|getFpgaType
argument_list|()
argument_list|,
name|fpgaDeviceList
argument_list|)
expr_stmt|;
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
comment|// 1. Get requested FPGA type and count, choose corresponding FPGA plugin(s)
comment|// 2. Use allocator.assignFpga(type, count) to get FPGAAllocation
comment|// 3. If required, download to ensure IP file exists and configure IP file for all devices
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
name|Resource
name|requestedResource
init|=
name|container
operator|.
name|getResource
argument_list|()
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
name|long
name|deviceCount
init|=
name|requestedResource
operator|.
name|getResourceValue
argument_list|(
name|FPGA_URI
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|containerIdStr
operator|+
literal|" requested "
operator|+
name|deviceCount
operator|+
literal|" Intel FPGA(s)"
argument_list|)
expr_stmt|;
name|String
name|ipFilePath
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// allocate even request 0 FPGA because we need to deny all device numbers for this container
name|FpgaResourceAllocator
operator|.
name|FpgaAllocation
name|allocation
init|=
name|allocator
operator|.
name|assignFpga
argument_list|(
name|vendorPlugin
operator|.
name|getFpgaType
argument_list|()
argument_list|,
name|deviceCount
argument_list|,
name|container
argument_list|,
name|getRequestedIPID
argument_list|(
name|container
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"FpgaAllocation:"
operator|+
name|allocation
argument_list|)
expr_stmt|;
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
name|FPGA
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
name|getDenied
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
name|denied
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|allocation
operator|.
name|getDenied
argument_list|()
operator|.
name|forEach
argument_list|(
name|device
lambda|->
name|denied
operator|.
name|add
argument_list|(
name|device
operator|.
name|getMinor
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|privilegedOperation
operator|.
name|appendArgs
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|EXCLUDED_FPGAS_CLI_OPTION
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|denied
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
if|if
condition|(
name|deviceCount
operator|>
literal|0
condition|)
block|{
comment|/**          * We only support flashing one IP for all devices now. If user don't set this          * environment variable, we assume that user's application can find the IP file by          * itself.          * Note that the IP downloading and reprogramming in advance in YARN is not necessary because          * the OpenCL application may find the IP file and reprogram device on the fly. But YARN do this          * for the containers will achieve the quickest reprogram path          *          * For instance, REQUESTED_FPGA_IP_ID = "matrix_mul" will make all devices          * programmed with matrix multiplication IP          *          * In the future, we may support "matrix_mul:1,gzip:2" format to support different IP          * for different devices          *          * */
name|ipFilePath
operator|=
name|vendorPlugin
operator|.
name|retrieveIPfilePath
argument_list|(
name|getRequestedIPID
argument_list|(
name|container
argument_list|)
argument_list|,
name|container
operator|.
name|getWorkDir
argument_list|()
argument_list|,
name|container
operator|.
name|getResourceSet
argument_list|()
operator|.
name|getLocalizedResources
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ipFilePath
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"FPGA plugin failed to downloaded IP, please check the"
operator|+
literal|" value of environment viable: "
operator|+
name|REQUEST_FPGA_IP_ID_KEY
operator|+
literal|" if you want YARN to program the device"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"IP file path:"
operator|+
name|ipFilePath
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FpgaResourceAllocator
operator|.
name|FpgaDevice
argument_list|>
name|allowed
init|=
name|allocation
operator|.
name|getAllowed
argument_list|()
decl_stmt|;
name|String
name|majorMinorNumber
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|allowed
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FpgaDevice
name|device
init|=
name|allowed
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|majorMinorNumber
operator|=
name|device
operator|.
name|getMajor
argument_list|()
operator|+
literal|":"
operator|+
name|device
operator|.
name|getMinor
argument_list|()
expr_stmt|;
name|String
name|currentIPID
init|=
name|device
operator|.
name|getIPID
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|currentIPID
operator|&&
name|currentIPID
operator|.
name|equalsIgnoreCase
argument_list|(
name|getRequestedIPID
argument_list|(
name|container
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"IP already in device \""
operator|+
name|allowed
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getAliasDevName
argument_list|()
operator|+
literal|","
operator|+
name|majorMinorNumber
operator|+
literal|"\", skip reprogramming"
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|vendorPlugin
operator|.
name|configureIP
argument_list|(
name|ipFilePath
argument_list|,
name|device
argument_list|)
condition|)
block|{
comment|// update the allocator that we update an IP of a device
name|allocator
operator|.
name|updateFpga
argument_list|(
name|containerIdStr
argument_list|,
name|allowed
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|getRequestedIPID
argument_list|(
name|container
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO: update the node constraint label
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|re
parameter_list|)
block|{
name|allocator
operator|.
name|cleanupAssignFpgas
argument_list|(
name|containerIdStr
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
name|containerIdStr
argument_list|)
expr_stmt|;
throw|throw
name|re
throw|;
block|}
catch|catch
parameter_list|(
name|PrivilegedOperationException
name|e
parameter_list|)
block|{
name|allocator
operator|.
name|cleanupAssignFpgas
argument_list|(
name|containerIdStr
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
comment|//isolation operation
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
name|allocator
operator|.
name|recoverAssignedFpgas
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
DECL|method|postComplete (ContainerId containerId)
specifier|public
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
name|allocator
operator|.
name|cleanupAssignFpgas
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
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
name|FpgaResourceHandlerImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"{"
operator|+
literal|"vendorPlugin="
operator|+
name|vendorPlugin
operator|+
literal|", allocator="
operator|+
name|allocator
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

