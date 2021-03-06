begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|ContainerExecutor
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
name|executor
operator|.
name|ContainerSignalContext
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
name|Collections
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
operator|.
name|CGROUP_PROCS_FILE
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
operator|.
name|CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES
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
operator|.
name|CGROUP_PARAM_MEMORY_OOM_CONTROL
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
operator|.
name|CGROUP_PARAM_MEMORY_USAGE_BYTES
import|;
end_import

begin_comment
comment|/**  * A very basic OOM handler implementation.  * See the javadoc on the run() method for details.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DefaultOOMHandler
specifier|public
class|class
name|DefaultOOMHandler
implements|implements
name|Runnable
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DefaultOOMHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|memoryStatFile
specifier|private
specifier|final
name|String
name|memoryStatFile
decl_stmt|;
DECL|field|cgroups
specifier|private
specifier|final
name|CGroupsHandler
name|cgroups
decl_stmt|;
comment|/**    * Create an OOM handler.    * This has to be public to be able to construct through reflection.    * @param context node manager context to work with    * @param enforceVirtualMemory true if virtual memory needs to be checked,    *                   false if physical memory needs to be checked instead    */
DECL|method|DefaultOOMHandler (Context context, boolean enforceVirtualMemory)
specifier|public
name|DefaultOOMHandler
parameter_list|(
name|Context
name|context
parameter_list|,
name|boolean
name|enforceVirtualMemory
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|memoryStatFile
operator|=
name|enforceVirtualMemory
condition|?
name|CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES
else|:
name|CGROUP_PARAM_MEMORY_USAGE_BYTES
expr_stmt|;
name|this
operator|.
name|cgroups
operator|=
name|getCGroupsHandler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCGroupsHandler ()
specifier|protected
name|CGroupsHandler
name|getCGroupsHandler
parameter_list|()
block|{
return|return
name|ResourceHandlerModule
operator|.
name|getCGroupsHandler
argument_list|()
return|;
block|}
comment|/**    * Check if a given container exceeds its limits.    */
DECL|method|isContainerOutOfLimit (Container container)
specifier|private
name|boolean
name|isContainerOutOfLimit
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|boolean
name|outOfLimit
init|=
literal|false
decl_stmt|;
name|String
name|value
init|=
literal|null
decl_stmt|;
try|try
block|{
name|value
operator|=
name|cgroups
operator|.
name|getCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|memoryStatFile
argument_list|)
expr_stmt|;
name|long
name|usage
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|long
name|request
init|=
name|container
operator|.
name|getResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// Check if the container has exceeded its limits.
if|if
condition|(
name|usage
operator|>
name|request
condition|)
block|{
name|outOfLimit
operator|=
literal|true
expr_stmt|;
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Container %s is out of its limits, using %d "
operator|+
literal|"when requested only %d"
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|usage
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Could not access memory resource for %s"
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Could not parse %s in %s"
argument_list|,
name|value
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|outOfLimit
return|;
block|}
comment|/**    * SIGKILL the specified container. We do this not using the standard    * container logic. The reason is that the processes are frozen by    * the cgroups OOM handler, so they cannot respond to SIGTERM.    * On the other hand we have to be as fast as possible.    * We walk through the list of active processes in the container.    * This is needed because frozen parents cannot signal their children.    * We kill each process and then try again until the whole cgroup    * is cleaned up. This logic avoids leaking processes in a cgroup.    * Currently the killing only succeeds for PGIDS.    *    * @param container Container to clean up    * @return true if the container is killed successfully, false otherwise    */
DECL|method|sigKill (Container container)
specifier|private
name|boolean
name|sigKill
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|boolean
name|containerKilled
init|=
literal|false
decl_stmt|;
name|boolean
name|finished
init|=
literal|false
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|!
name|finished
condition|)
block|{
name|String
index|[]
name|pids
init|=
name|cgroups
operator|.
name|getCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|CGROUP_PROCS_FILE
argument_list|)
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
decl_stmt|;
name|finished
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|String
name|pid
range|:
name|pids
control|)
block|{
comment|// Note: this kills only PGIDs currently
if|if
condition|(
name|pid
operator|!=
literal|null
operator|&&
operator|!
name|pid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Terminating container %s Sending SIGKILL to -%s"
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|pid
argument_list|)
argument_list|)
expr_stmt|;
name|finished
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|context
operator|.
name|getContainerExecutor
argument_list|()
operator|.
name|signalContainer
argument_list|(
operator|new
name|ContainerSignalContext
operator|.
name|Builder
argument_list|()
operator|.
name|setContainer
argument_list|(
name|container
argument_list|)
operator|.
name|setUser
argument_list|(
name|container
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|setPid
argument_list|(
name|pid
argument_list|)
operator|.
name|setSignal
argument_list|(
name|ContainerExecutor
operator|.
name|Signal
operator|.
name|KILL
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot kill container %s pid -%s."
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|pid
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Interrupted while waiting for processes to disappear"
argument_list|)
expr_stmt|;
block|}
block|}
name|containerKilled
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|ex
parameter_list|)
block|{
comment|// the tasks file of the container may not be available because the
comment|// container may not have been launched at this point when the root
comment|// cgroup is under oom
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot list more tasks in container %s to kill."
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|containerKilled
return|;
block|}
comment|/**    * It is called when the node is under an OOM condition. All processes in    * all sub-cgroups are suspended. We need to act fast, so that we do not    * affect the overall system utilization. In general we try to find a    * newly launched container that exceeded its limits. The justification is    * cost, since probably this is the one that has accumulated the least    * amount of uncommitted data so far. OPPORTUNISTIC containers are always    * killed before any GUARANTEED containers are considered.  We continue the    * process until the OOM is resolved.    */
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|// We kill containers until the kernel reports the OOM situation resolved
comment|// Note: If the kernel has a delay this may kill more than necessary
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|status
init|=
name|cgroups
operator|.
name|getCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
literal|""
argument_list|,
name|CGROUP_PARAM_MEMORY_OOM_CONTROL
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|status
operator|.
name|contains
argument_list|(
name|CGroupsHandler
operator|.
name|UNDER_OOM
argument_list|)
condition|)
block|{
break|break;
block|}
name|boolean
name|containerKilled
init|=
name|killContainer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|containerKilled
condition|)
block|{
comment|// This can happen, if SIGKILL did not clean up
comment|// non-PGID or containers or containers launched by other users
comment|// or if a process was put to the root YARN cgroup.
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Could not find any containers but CGroups "
operator|+
literal|"reserved for containers ran out of memory. "
operator|+
literal|"I am giving up"
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not fetch OOM status. "
operator|+
literal|"This is expected at shutdown. Exiting."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Choose and kill a container in case of OOM. We try to find the most    * recently launched OPPORTUNISTIC container that exceeds its limit    * and fall back to the most recently launched OPPORTUNISTIC container    * If there is no such container found, we choose to kill a GUARANTEED    * container in the same way.    * @return true if a container is killed, false otherwise    */
DECL|method|killContainer ()
specifier|protected
name|boolean
name|killContainer
parameter_list|()
block|{
name|boolean
name|containerKilled
init|=
literal|false
decl_stmt|;
name|ArrayList
argument_list|<
name|ContainerCandidate
argument_list|>
name|candidates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Container
name|container
range|:
name|context
operator|.
name|getContainers
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|container
operator|.
name|isRunning
argument_list|()
condition|)
block|{
comment|// skip containers that are not running yet because killing them
comment|// won't release any memory to get us out of OOM.
continue|continue;
comment|// note even if it is indicated that the container is running from
comment|// container.isRunning(), the container process might not have been
comment|// running yet. From NM's perspective, a container is running as
comment|// soon as the container launch is handed over the container executor
block|}
name|candidates
operator|.
name|add
argument_list|(
operator|new
name|ContainerCandidate
argument_list|(
name|container
argument_list|,
name|isContainerOutOfLimit
argument_list|(
name|container
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|candidates
argument_list|)
expr_stmt|;
if|if
condition|(
name|candidates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Found no running containers to kill in order to release memory"
argument_list|)
expr_stmt|;
block|}
comment|// make sure one container is killed successfully to release memory
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|!
name|containerKilled
operator|&&
name|i
operator|<
name|candidates
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ContainerCandidate
name|candidate
init|=
name|candidates
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|sigKill
argument_list|(
name|candidate
operator|.
name|container
argument_list|)
condition|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"container %s killed by elastic cgroups OOM handler."
argument_list|,
name|candidate
operator|.
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|containerKilled
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|containerKilled
return|;
block|}
comment|/**    * Note: this class has a natural ordering that is inconsistent with equals.    */
DECL|class|ContainerCandidate
specifier|private
specifier|static
class|class
name|ContainerCandidate
implements|implements
name|Comparable
argument_list|<
name|ContainerCandidate
argument_list|>
block|{
DECL|field|outOfLimit
specifier|private
specifier|final
name|boolean
name|outOfLimit
decl_stmt|;
DECL|field|container
specifier|final
name|Container
name|container
decl_stmt|;
DECL|method|ContainerCandidate (Container container, boolean outOfLimit)
name|ContainerCandidate
parameter_list|(
name|Container
name|container
parameter_list|,
name|boolean
name|outOfLimit
parameter_list|)
block|{
name|this
operator|.
name|outOfLimit
operator|=
name|outOfLimit
expr_stmt|;
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
block|}
comment|/**      * Order two containers by their execution type, followed by      * their out-of-limit status and then launch time. Opportunistic      * containers are ordered before Guaranteed containers. If two      * containers are of the same execution type, the one that is      * out of its limits is ordered before the one that isn't. If      * two containers have the same execution type and out-of-limit      * status, the one that's launched later is ordered before the      * other one.      */
annotation|@
name|Override
DECL|method|compareTo (ContainerCandidate o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ContainerCandidate
name|o
parameter_list|)
block|{
name|boolean
name|isThisOpportunistic
init|=
name|isOpportunistic
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|boolean
name|isOtherOpportunistic
init|=
name|isOpportunistic
argument_list|(
name|o
operator|.
name|container
argument_list|)
decl_stmt|;
name|int
name|ret
init|=
name|Boolean
operator|.
name|compare
argument_list|(
name|isOtherOpportunistic
argument_list|,
name|isThisOpportunistic
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
comment|// the two containers are of the same execution type, order them
comment|// by their out-of-limit status.
name|int
name|outOfLimitRet
init|=
name|Boolean
operator|.
name|compare
argument_list|(
name|o
operator|.
name|outOfLimit
argument_list|,
name|outOfLimit
argument_list|)
decl_stmt|;
if|if
condition|(
name|outOfLimitRet
operator|==
literal|0
condition|)
block|{
comment|// the two containers are also of the same out-of-limit status,
comment|// order them by their launch time
name|ret
operator|=
name|Long
operator|.
name|compare
argument_list|(
name|o
operator|.
name|container
operator|.
name|getContainerLaunchTime
argument_list|()
argument_list|,
name|this
operator|.
name|container
operator|.
name|getContainerLaunchTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
name|outOfLimitRet
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ContainerCandidate
name|other
init|=
operator|(
name|ContainerCandidate
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|outOfLimit
operator|!=
name|other
operator|.
name|outOfLimit
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|container
operator|==
literal|null
condition|)
block|{
return|return
name|other
operator|.
name|container
operator|==
literal|null
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|container
operator|.
name|equals
argument_list|(
name|other
operator|.
name|container
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|container
argument_list|)
operator|.
name|append
argument_list|(
name|outOfLimit
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
comment|/**      * Check if a container is OPPORTUNISTIC or not. A container is      * considered OPPORTUNISTIC only if its execution type is not      * null and is OPPORTUNISTIC.      */
DECL|method|isOpportunistic (Container container)
specifier|private
specifier|static
name|boolean
name|isOpportunistic
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
return|return
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
operator|!=
literal|null
operator|&&
name|ExecutionType
operator|.
name|OPPORTUNISTIC
operator|.
name|equals
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
operator|.
name|getExecutionType
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

