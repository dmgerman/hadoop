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
name|util
operator|.
name|Shell
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
comment|/**  * Handler class to handle the blkio controller. Currently it splits resources  * evenly across all containers. Once we have scheduling sorted out, we can  * modify the function to represent the disk resources allocated.  */
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
DECL|class|CGroupsBlkioResourceHandlerImpl
specifier|public
class|class
name|CGroupsBlkioResourceHandlerImpl
implements|implements
name|DiskResourceHandler
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
name|CGroupsBlkioResourceHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cGroupsHandler
specifier|private
name|CGroupsHandler
name|cGroupsHandler
decl_stmt|;
comment|// Arbitrarily choose a weight - all that matters is that all containers
comment|// get the same weight assigned to them. Once we have scheduling support
comment|// this number will be determined dynamically for each container.
annotation|@
name|VisibleForTesting
DECL|field|DEFAULT_WEIGHT
specifier|static
specifier|final
name|String
name|DEFAULT_WEIGHT
init|=
literal|"500"
decl_stmt|;
DECL|field|PARTITIONS_FILE
specifier|private
specifier|static
specifier|final
name|String
name|PARTITIONS_FILE
init|=
literal|"/proc/partitions"
decl_stmt|;
DECL|method|CGroupsBlkioResourceHandlerImpl (CGroupsHandler cGroupsHandler)
name|CGroupsBlkioResourceHandlerImpl
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
comment|// check for linux so that we don't print messages for tests running on
comment|// other platforms
if|if
condition|(
name|Shell
operator|.
name|LINUX
condition|)
block|{
name|checkDiskScheduler
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkDiskScheduler ()
specifier|private
name|void
name|checkDiskScheduler
parameter_list|()
block|{
name|String
name|data
decl_stmt|;
comment|// read /proc/partitions and check to make sure that sd* and hd*
comment|// are using the CFQ scheduler. If they aren't print a warning
try|try
block|{
name|byte
index|[]
name|contents
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|PARTITIONS_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|data
operator|=
operator|new
name|String
argument_list|(
name|contents
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|trim
argument_list|()
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
literal|"Couldn't read "
operator|+
name|PARTITIONS_FILE
operator|+
literal|"; can't determine disk scheduler type"
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
index|[]
name|lines
init|=
name|data
operator|.
name|split
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lines
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|line
range|:
name|lines
control|)
block|{
name|String
index|[]
name|columns
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|columns
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|String
name|partition
init|=
name|columns
index|[
literal|4
index|]
decl_stmt|;
comment|// check some known partitions to make sure  the disk scheduler
comment|// is cfq - not meant to be comprehensive, more a sanity check
if|if
condition|(
name|partition
operator|.
name|startsWith
argument_list|(
literal|"sd"
argument_list|)
operator|||
name|partition
operator|.
name|startsWith
argument_list|(
literal|"hd"
argument_list|)
operator|||
name|partition
operator|.
name|startsWith
argument_list|(
literal|"vd"
argument_list|)
operator|||
name|partition
operator|.
name|startsWith
argument_list|(
literal|"xvd"
argument_list|)
condition|)
block|{
name|String
name|schedulerPath
init|=
literal|"/sys/block/"
operator|+
name|partition
operator|+
literal|"/queue/scheduler"
decl_stmt|;
name|File
name|schedulerFile
init|=
operator|new
name|File
argument_list|(
name|schedulerPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|schedulerFile
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
name|byte
index|[]
name|contents
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|schedulerPath
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|schedulerString
init|=
operator|new
name|String
argument_list|(
name|contents
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|schedulerString
operator|.
name|contains
argument_list|(
literal|"[cfq]"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Device "
operator|+
name|partition
operator|+
literal|" does not use the CFQ"
operator|+
literal|" scheduler; disk isolation using "
operator|+
literal|"CGroups will not work on this partition."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to determine disk scheduler type for partition "
operator|+
name|partition
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
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
comment|// if bootstrap is called on this class, disk is already enabled
comment|// so no need to check again
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
name|BLKIO
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
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|BLKIO
argument_list|,
name|cgroupId
argument_list|)
expr_stmt|;
try|try
block|{
name|cGroupsHandler
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|BLKIO
argument_list|,
name|cgroupId
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_BLKIO_WEIGHT
argument_list|,
name|DEFAULT_WEIGHT
argument_list|)
expr_stmt|;
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
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|BLKIO
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
name|BLKIO
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
name|cGroupsHandler
operator|.
name|deleteCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|BLKIO
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
block|}
end_class

end_unit

