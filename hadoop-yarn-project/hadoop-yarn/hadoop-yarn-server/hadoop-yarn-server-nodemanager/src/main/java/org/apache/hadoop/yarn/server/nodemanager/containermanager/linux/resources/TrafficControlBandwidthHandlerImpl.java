begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements. See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership. The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License. You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
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
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TrafficControlBandwidthHandlerImpl
specifier|public
class|class
name|TrafficControlBandwidthHandlerImpl
implements|implements
name|OutboundBandwidthResourceHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TrafficControlBandwidthHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//In the absence of 'scheduling' support, we'll 'infer' the guaranteed
comment|//outbound bandwidth for each container based on this number. This will
comment|//likely go away once we add support on the RM for this resource type.
DECL|field|MAX_CONTAINER_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|MAX_CONTAINER_COUNT
init|=
literal|50
decl_stmt|;
DECL|field|privilegedOperationExecutor
specifier|private
specifier|final
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
decl_stmt|;
DECL|field|cGroupsHandler
specifier|private
specifier|final
name|CGroupsHandler
name|cGroupsHandler
decl_stmt|;
DECL|field|trafficController
specifier|private
specifier|final
name|TrafficController
name|trafficController
decl_stmt|;
DECL|field|containerIdClassIdMap
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|ContainerId
argument_list|,
name|Integer
argument_list|>
name|containerIdClassIdMap
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|device
specifier|private
name|String
name|device
decl_stmt|;
DECL|field|strictMode
specifier|private
name|boolean
name|strictMode
decl_stmt|;
DECL|field|containerBandwidthMbit
specifier|private
name|int
name|containerBandwidthMbit
decl_stmt|;
DECL|field|rootBandwidthMbit
specifier|private
name|int
name|rootBandwidthMbit
decl_stmt|;
DECL|field|yarnBandwidthMbit
specifier|private
name|int
name|yarnBandwidthMbit
decl_stmt|;
DECL|method|TrafficControlBandwidthHandlerImpl (PrivilegedOperationExecutor privilegedOperationExecutor, CGroupsHandler cGroupsHandler, TrafficController trafficController)
specifier|public
name|TrafficControlBandwidthHandlerImpl
parameter_list|(
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|,
name|CGroupsHandler
name|cGroupsHandler
parameter_list|,
name|TrafficController
name|trafficController
parameter_list|)
block|{
name|this
operator|.
name|privilegedOperationExecutor
operator|=
name|privilegedOperationExecutor
expr_stmt|;
name|this
operator|.
name|cGroupsHandler
operator|=
name|cGroupsHandler
expr_stmt|;
name|this
operator|.
name|trafficController
operator|=
name|trafficController
expr_stmt|;
name|this
operator|.
name|containerIdClassIdMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Bootstrapping 'outbound-bandwidth' resource handler - mounts net_cls    * controller and bootstraps a traffic control bandwidth shaping hierarchy    * @param configuration yarn configuration in use    * @return (potentially empty) list of privileged operations to execute.    * @throws ResourceHandlerException    */
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
name|conf
operator|=
name|configuration
expr_stmt|;
comment|//We'll do this inline for the time being - since this is a one time
comment|//operation. At some point, LCE code can be refactored to batch mount
comment|//operations across multiple controllers - cpu, net_cls, blkio etc
name|cGroupsHandler
operator|.
name|initializeCGroupController
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|)
expr_stmt|;
name|device
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_INTERFACE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_NETWORK_RESOURCE_INTERFACE
argument_list|)
expr_stmt|;
name|strictMode
operator|=
name|configuration
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE
argument_list|)
expr_stmt|;
name|rootBandwidthMbit
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_OUTBOUND_BANDWIDTH_MBIT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_NETWORK_RESOURCE_OUTBOUND_BANDWIDTH_MBIT
argument_list|)
expr_stmt|;
name|yarnBandwidthMbit
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_OUTBOUND_BANDWIDTH_YARN_MBIT
argument_list|,
name|rootBandwidthMbit
argument_list|)
expr_stmt|;
name|containerBandwidthMbit
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|yarnBandwidthMbit
operator|/
name|MAX_CONTAINER_COUNT
argument_list|)
expr_stmt|;
name|StringBuffer
name|logLine
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"strict mode is set to :"
argument_list|)
operator|.
name|append
argument_list|(
name|strictMode
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|strictMode
condition|)
block|{
name|logLine
operator|.
name|append
argument_list|(
literal|"container bandwidth will be capped to soft limit."
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logLine
operator|.
name|append
argument_list|(
literal|"containers will be allowed to use spare YARN bandwidth."
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logLine
operator|.
name|append
argument_list|(
literal|"containerBandwidthMbit soft limit (in mbit/sec) is set to : "
argument_list|)
operator|.
name|append
argument_list|(
name|containerBandwidthMbit
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|logLine
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|trafficController
operator|.
name|bootstrap
argument_list|(
name|device
argument_list|,
name|rootBandwidthMbit
argument_list|,
name|yarnBandwidthMbit
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/**    * Pre-start hook for 'outbound-bandwidth' resource. A cgroup is created    * and a net_cls classid is generated and written to a cgroup file. A    * traffic control shaping rule is created in order to limit outbound    * bandwidth utilization.    * @param container Container being launched    * @return privileged operations for some cgroups/tc operations.    * @throws ResourceHandlerException    */
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
name|int
name|classId
init|=
name|trafficController
operator|.
name|getNextClassId
argument_list|()
decl_stmt|;
name|String
name|classIdStr
init|=
name|trafficController
operator|.
name|getStringForNetClsClassId
argument_list|(
name|classId
argument_list|)
decl_stmt|;
name|cGroupsHandler
operator|.
name|createCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|,
name|containerIdStr
argument_list|)
expr_stmt|;
name|cGroupsHandler
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|,
name|containerIdStr
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_CLASSID
argument_list|,
name|classIdStr
argument_list|)
expr_stmt|;
name|containerIdClassIdMap
operator|.
name|put
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|classId
argument_list|)
expr_stmt|;
comment|//Now create a privileged operation in order to update the tasks file with
comment|//the pid of the running container process (root of process tree). This can
comment|//only be done at the time of launching the container, in a privileged
comment|//executable.
name|String
name|tasksFile
init|=
name|cGroupsHandler
operator|.
name|getPathForCGroupTasks
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|,
name|containerIdStr
argument_list|)
decl_stmt|;
name|String
name|opArg
init|=
operator|new
name|StringBuffer
argument_list|(
name|PrivilegedOperation
operator|.
name|CGROUP_ARG_PREFIX
argument_list|)
operator|.
name|append
argument_list|(
name|tasksFile
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|ops
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ops
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
name|opArg
argument_list|)
argument_list|)
expr_stmt|;
comment|//Create a privileged operation to create a tc rule for this container
comment|//We'll return this to the calling (Linux) Container Executor
comment|//implementation for batching optimizations so that we don't fork/exec
comment|//additional times during container launch.
name|TrafficController
operator|.
name|BatchBuilder
name|builder
init|=
name|trafficController
operator|.
expr|new
name|BatchBuilder
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addContainerClass
argument_list|(
name|classId
argument_list|,
name|containerBandwidthMbit
argument_list|,
name|strictMode
argument_list|)
expr_stmt|;
name|ops
operator|.
name|add
argument_list|(
name|builder
operator|.
name|commitBatchToTempFile
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ops
return|;
block|}
comment|/**    * Reacquires state for a container - reads the classid from the cgroup    * being used for the container being reacquired    * @param containerId if of the container being reacquired.    * @return (potentially empty) list of privileged operations    * @throws ResourceHandlerException    */
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
name|String
name|containerIdStr
init|=
name|containerId
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Attempting to reacquire classId for container: "
operator|+
name|containerIdStr
argument_list|)
expr_stmt|;
block|}
name|String
name|classIdStrFromFile
init|=
name|cGroupsHandler
operator|.
name|getCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|,
name|containerIdStr
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_CLASSID
argument_list|)
decl_stmt|;
name|int
name|classId
init|=
name|trafficController
operator|.
name|getClassIdFromFileContents
argument_list|(
name|classIdStrFromFile
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reacquired containerId -> classId mapping: "
operator|+
name|containerIdStr
operator|+
literal|" -> "
operator|+
name|classId
argument_list|)
expr_stmt|;
name|containerIdClassIdMap
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|classId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/**    * Returns total bytes sent per container to be used for metrics tracking    * purposes.    * @return a map of containerId to bytes sent    * @throws ResourceHandlerException    */
DECL|method|getBytesSentPerContainer ()
specifier|public
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Integer
argument_list|>
name|getBytesSentPerContainer
parameter_list|()
throws|throws
name|ResourceHandlerException
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|classIdStats
init|=
name|trafficController
operator|.
name|readStats
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Integer
argument_list|>
name|containerIdStats
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|containerIdClassIdMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ContainerId
name|containerId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Integer
name|classId
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Integer
name|bytesSent
init|=
name|classIdStats
operator|.
name|get
argument_list|(
name|classId
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesSent
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No bytes sent metric found for container: "
operator|+
name|containerId
operator|+
literal|" with classId: "
operator|+
name|classId
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|containerIdStats
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|bytesSent
argument_list|)
expr_stmt|;
block|}
return|return
name|containerIdStats
return|;
block|}
comment|/**    * Cleanup operations once container is completed - deletes cgroup and    * removes traffic shaping rule(s).    * @param containerId of the container that was completed.    * @return    * @throws ResourceHandlerException    */
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
name|LOG
operator|.
name|info
argument_list|(
literal|"postComplete for container: "
operator|+
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
name|NET_CLS
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Integer
name|classId
init|=
name|containerIdClassIdMap
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|classId
operator|!=
literal|null
condition|)
block|{
name|PrivilegedOperation
name|op
init|=
name|trafficController
operator|.
expr|new
name|BatchBuilder
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|)
operator|.
name|deleteContainerClass
argument_list|(
name|classId
argument_list|)
operator|.
name|commitBatchToTempFile
argument_list|()
decl_stmt|;
try|try
block|{
name|privilegedOperationExecutor
operator|.
name|executePrivilegedOperation
argument_list|(
name|op
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|trafficController
operator|.
name|releaseClassId
argument_list|(
name|classId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PrivilegedOperationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete tc rule for classId: "
operator|+
name|classId
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Failed to delete tc rule for classId:"
operator|+
name|classId
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Not cleaning up tc rules. classId unknown for container: "
operator|+
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"teardown(): Nothing to do"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

