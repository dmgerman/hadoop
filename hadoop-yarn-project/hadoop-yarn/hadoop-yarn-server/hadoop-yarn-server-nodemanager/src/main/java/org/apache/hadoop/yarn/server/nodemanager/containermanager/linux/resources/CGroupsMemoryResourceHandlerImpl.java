begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
name|ExecutionType
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
name|conf
operator|.
name|YarnConfiguration
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|java
operator|.
name|io
operator|.
name|File
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
name|List
import|;
end_import

begin_comment
comment|/**  * Handler class to handle the memory controller. YARN already ships a  * physical memory monitor in Java but it isn't as  * good as CGroups. This handler sets the soft and hard memory limits. The soft  * limit is set to 90% of the hard limit.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|CGroupsMemoryResourceHandlerImpl
specifier|public
class|class
name|CGroupsMemoryResourceHandlerImpl
implements|implements
name|MemoryResourceHandler
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
name|CGroupsMemoryResourceHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MEMORY
specifier|private
specifier|static
specifier|final
name|CGroupsHandler
operator|.
name|CGroupController
name|MEMORY
init|=
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
decl_stmt|;
DECL|field|OPPORTUNISTIC_SWAPPINESS
specifier|private
specifier|static
specifier|final
name|int
name|OPPORTUNISTIC_SWAPPINESS
init|=
literal|100
decl_stmt|;
DECL|field|OPPORTUNISTIC_SOFT_LIMIT
specifier|private
specifier|static
specifier|final
name|int
name|OPPORTUNISTIC_SOFT_LIMIT
init|=
literal|0
decl_stmt|;
DECL|field|cGroupsHandler
specifier|private
name|CGroupsHandler
name|cGroupsHandler
decl_stmt|;
DECL|field|enforce
specifier|private
name|boolean
name|enforce
init|=
literal|true
decl_stmt|;
DECL|field|swappiness
specifier|private
name|int
name|swappiness
init|=
literal|0
decl_stmt|;
comment|// multiplier to set the soft limit - value should be between 0 and 1
DECL|field|softLimit
specifier|private
name|float
name|softLimit
init|=
literal|0.0f
decl_stmt|;
DECL|method|CGroupsMemoryResourceHandlerImpl (CGroupsHandler cGroupsHandler)
name|CGroupsMemoryResourceHandlerImpl
parameter_list|(
name|CGroupsHandler
name|cGroupsHandler
parameter_list|)
block|{
name|this
operator|.
name|cGroupsHandler
operator|=
name|cGroupsHandler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bootstrap (Configuration conf)
specifier|public
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|bootstrap
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|this
operator|.
name|cGroupsHandler
operator|.
name|initializeCGroupController
argument_list|(
name|MEMORY
argument_list|)
expr_stmt|;
name|enforce
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MEMORY_RESOURCE_ENFORCED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_MEMORY_RESOURCE_ENFORCED
argument_list|)
expr_stmt|;
name|swappiness
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS
argument_list|)
expr_stmt|;
if|if
condition|(
name|swappiness
argument_list|<
literal|0
operator|||
name|swappiness
argument_list|>
literal|100
condition|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Illegal value '"
operator|+
name|swappiness
operator|+
literal|"' for "
operator|+
name|YarnConfiguration
operator|.
name|NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS
operator|+
literal|". Value must be between 0 and 100."
argument_list|)
throw|;
block|}
name|float
name|softLimitPerc
init|=
name|conf
operator|.
name|getFloat
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MEMORY_RESOURCE_CGROUPS_SOFT_LIMIT_PERCENTAGE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_MEMORY_RESOURCE_CGROUPS_SOFT_LIMIT_PERCENTAGE
argument_list|)
decl_stmt|;
name|softLimit
operator|=
name|softLimitPerc
operator|/
literal|100.0f
expr_stmt|;
if|if
condition|(
name|softLimitPerc
argument_list|<
literal|0.0f
operator|||
name|softLimitPerc
argument_list|>
literal|100.0f
condition|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Illegal value '"
operator|+
name|softLimitPerc
operator|+
literal|"' "
operator|+
name|YarnConfiguration
operator|.
name|NM_MEMORY_RESOURCE_CGROUPS_SOFT_LIMIT_PERCENTAGE
operator|+
literal|". Value must be between 0 and 100."
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getSwappiness ()
name|int
name|getSwappiness
parameter_list|()
block|{
return|return
name|swappiness
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
name|String
name|cgroupId
init|=
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|File
name|cgroup
init|=
operator|new
name|File
argument_list|(
name|cGroupsHandler
operator|.
name|getPathForCGroup
argument_list|(
name|MEMORY
argument_list|,
name|cgroupId
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|cgroup
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|//memory is in MB
name|long
name|containerSoftLimit
init|=
call|(
name|long
call|)
argument_list|(
name|container
operator|.
name|getResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|*
name|this
operator|.
name|softLimit
argument_list|)
decl_stmt|;
name|long
name|containerHardLimit
init|=
name|container
operator|.
name|getResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
if|if
condition|(
name|enforce
condition|)
block|{
try|try
block|{
name|cGroupsHandler
operator|.
name|updateCGroupParam
argument_list|(
name|MEMORY
argument_list|,
name|cgroupId
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|containerHardLimit
argument_list|)
operator|+
literal|"M"
argument_list|)
expr_stmt|;
name|ContainerTokenIdentifier
name|id
init|=
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|getExecutionType
argument_list|()
operator|==
name|ExecutionType
operator|.
name|OPPORTUNISTIC
condition|)
block|{
name|cGroupsHandler
operator|.
name|updateCGroupParam
argument_list|(
name|MEMORY
argument_list|,
name|cgroupId
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|OPPORTUNISTIC_SOFT_LIMIT
argument_list|)
operator|+
literal|"M"
argument_list|)
expr_stmt|;
name|cGroupsHandler
operator|.
name|updateCGroupParam
argument_list|(
name|MEMORY
argument_list|,
name|cgroupId
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SWAPPINESS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|OPPORTUNISTIC_SWAPPINESS
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cGroupsHandler
operator|.
name|updateCGroupParam
argument_list|(
name|MEMORY
argument_list|,
name|cgroupId
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|containerSoftLimit
argument_list|)
operator|+
literal|"M"
argument_list|)
expr_stmt|;
name|cGroupsHandler
operator|.
name|updateCGroupParam
argument_list|(
name|MEMORY
argument_list|,
name|cgroupId
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SWAPPINESS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|swappiness
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|re
parameter_list|)
block|{
name|cGroupsHandler
operator|.
name|deleteCGroup
argument_list|(
name|MEMORY
argument_list|,
name|cgroupId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not update cgroup for container"
argument_list|,
name|re
argument_list|)
expr_stmt|;
throw|throw
name|re
throw|;
block|}
block|}
block|}
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
name|String
name|cgroupId
init|=
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|cGroupsHandler
operator|.
name|createCGroup
argument_list|(
name|MEMORY
argument_list|,
name|cgroupId
argument_list|)
expr_stmt|;
name|updateContainer
argument_list|(
name|container
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
name|MEMORY
argument_list|,
name|cgroupId
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
name|cGroupsHandler
operator|.
name|deleteCGroup
argument_list|(
name|MEMORY
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
name|CGroupsMemoryResourceHandlerImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

