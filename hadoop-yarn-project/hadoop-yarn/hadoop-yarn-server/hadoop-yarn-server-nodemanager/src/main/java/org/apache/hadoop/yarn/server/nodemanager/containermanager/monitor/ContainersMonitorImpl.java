begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor
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
name|monitor
package|;
end_package

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
name|Iterator
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
name|Map
operator|.
name|Entry
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
operator|.
name|TraditionalBinaryPrefix
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
name|event
operator|.
name|AsyncDispatcher
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
name|event
operator|.
name|Dispatcher
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
name|ContainerKillEvent
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
name|AbstractService
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
name|util
operator|.
name|ResourceCalculatorProcessTree
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
name|util
operator|.
name|ResourceCalculatorPlugin
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_class
DECL|class|ContainersMonitorImpl
specifier|public
class|class
name|ContainersMonitorImpl
extends|extends
name|AbstractService
implements|implements
name|ContainersMonitor
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
name|ContainersMonitorImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|monitoringInterval
specifier|private
name|long
name|monitoringInterval
decl_stmt|;
DECL|field|monitoringThread
specifier|private
name|MonitoringThread
name|monitoringThread
decl_stmt|;
DECL|field|containersToBeRemoved
specifier|final
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containersToBeRemoved
decl_stmt|;
DECL|field|containersToBeAdded
specifier|final
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ProcessTreeInfo
argument_list|>
name|containersToBeAdded
decl_stmt|;
DECL|field|trackingContainers
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ProcessTreeInfo
argument_list|>
name|trackingContainers
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ProcessTreeInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|containerExecutor
specifier|final
name|ContainerExecutor
name|containerExecutor
decl_stmt|;
DECL|field|eventDispatcher
specifier|private
specifier|final
name|Dispatcher
name|eventDispatcher
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|resourceCalculatorPlugin
specifier|private
name|ResourceCalculatorPlugin
name|resourceCalculatorPlugin
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|processTreeClass
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|ResourceCalculatorProcessTree
argument_list|>
name|processTreeClass
decl_stmt|;
DECL|field|maxVmemAllottedForContainers
specifier|private
name|long
name|maxVmemAllottedForContainers
init|=
name|DISABLED_MEMORY_LIMIT
decl_stmt|;
DECL|field|maxPmemAllottedForContainers
specifier|private
name|long
name|maxPmemAllottedForContainers
init|=
name|DISABLED_MEMORY_LIMIT
decl_stmt|;
comment|/**    * A value which if set for memory related configuration options, indicates    * that the options are turned off.    */
DECL|field|DISABLED_MEMORY_LIMIT
specifier|public
specifier|static
specifier|final
name|long
name|DISABLED_MEMORY_LIMIT
init|=
operator|-
literal|1L
decl_stmt|;
DECL|method|ContainersMonitorImpl (ContainerExecutor exec, AsyncDispatcher dispatcher, Context context)
specifier|public
name|ContainersMonitorImpl
parameter_list|(
name|ContainerExecutor
name|exec
parameter_list|,
name|AsyncDispatcher
name|dispatcher
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|super
argument_list|(
literal|"containers-monitor"
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerExecutor
operator|=
name|exec
expr_stmt|;
name|this
operator|.
name|eventDispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|containersToBeAdded
operator|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ProcessTreeInfo
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|containersToBeRemoved
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|monitoringThread
operator|=
operator|new
name|MonitoringThread
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|monitoringInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_MON_INTERVAL_MS
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|ResourceCalculatorPlugin
argument_list|>
name|clazz
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_RESOURCE_CALCULATOR
argument_list|,
literal|null
argument_list|,
name|ResourceCalculatorPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|resourceCalculatorPlugin
operator|=
name|ResourceCalculatorPlugin
operator|.
name|getResourceCalculatorPlugin
argument_list|(
name|clazz
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|" Using ResourceCalculatorPlugin : "
operator|+
name|this
operator|.
name|resourceCalculatorPlugin
argument_list|)
expr_stmt|;
name|processTreeClass
operator|=
name|conf
operator|.
name|getClass
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_PROCESS_TREE
argument_list|,
literal|null
argument_list|,
name|ResourceCalculatorProcessTree
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|" Using ResourceCalculatorProcessTree : "
operator|+
name|this
operator|.
name|processTreeClass
argument_list|)
expr_stmt|;
name|long
name|totalPhysicalMemoryOnNM
init|=
name|DISABLED_MEMORY_LIMIT
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|resourceCalculatorPlugin
operator|!=
literal|null
condition|)
block|{
name|totalPhysicalMemoryOnNM
operator|=
name|this
operator|.
name|resourceCalculatorPlugin
operator|.
name|getPhysicalMemorySize
argument_list|()
expr_stmt|;
if|if
condition|(
name|totalPhysicalMemoryOnNM
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"NodeManager's totalPmem could not be calculated. "
operator|+
literal|"Setting it to "
operator|+
name|DISABLED_MEMORY_LIMIT
argument_list|)
expr_stmt|;
name|totalPhysicalMemoryOnNM
operator|=
name|DISABLED_MEMORY_LIMIT
expr_stmt|;
block|}
block|}
comment|// ///////// Physical memory configuration //////
name|this
operator|.
name|maxPmemAllottedForContainers
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_MB
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PMEM_MB
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxPmemAllottedForContainers
operator|=
name|this
operator|.
name|maxPmemAllottedForContainers
operator|*
literal|1024
operator|*
literal|1024L
expr_stmt|;
comment|//Normalize to bytes
if|if
condition|(
name|totalPhysicalMemoryOnNM
operator|!=
name|DISABLED_MEMORY_LIMIT
operator|&&
name|this
operator|.
name|maxPmemAllottedForContainers
operator|>
name|totalPhysicalMemoryOnNM
operator|*
literal|0.80f
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"NodeManager configured with "
operator|+
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|maxPmemAllottedForContainers
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|)
operator|+
literal|" physical memory allocated to containers, which is more than "
operator|+
literal|"80% of the total physical memory available ("
operator|+
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|totalPhysicalMemoryOnNM
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|)
operator|+
literal|"). Thrashing might happen."
argument_list|)
expr_stmt|;
block|}
comment|// ///////// Virtual memory configuration //////
name|float
name|vmemRatio
init|=
name|conf
operator|.
name|getFloat
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_PMEM_RATIO
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_VMEM_PMEM_RATIO
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|vmemRatio
operator|>
literal|0.99f
argument_list|,
name|YarnConfiguration
operator|.
name|NM_VMEM_PMEM_RATIO
operator|+
literal|" should be at least 1.0"
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxVmemAllottedForContainers
operator|=
call|(
name|long
call|)
argument_list|(
name|vmemRatio
operator|*
name|maxPmemAllottedForContainers
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Is the total physical memory check enabled?    *    * @return true if total physical memory check is enabled.    */
DECL|method|isPhysicalMemoryCheckEnabled ()
name|boolean
name|isPhysicalMemoryCheckEnabled
parameter_list|()
block|{
return|return
operator|!
operator|(
name|this
operator|.
name|maxPmemAllottedForContainers
operator|==
name|DISABLED_MEMORY_LIMIT
operator|)
return|;
block|}
comment|/**    * Is the total virtual memory check enabled?    *    * @return true if total virtual memory check is enabled.    */
DECL|method|isVirtualMemoryCheckEnabled ()
name|boolean
name|isVirtualMemoryCheckEnabled
parameter_list|()
block|{
return|return
operator|!
operator|(
name|this
operator|.
name|maxVmemAllottedForContainers
operator|==
name|DISABLED_MEMORY_LIMIT
operator|)
return|;
block|}
DECL|method|isEnabled ()
specifier|private
name|boolean
name|isEnabled
parameter_list|()
block|{
if|if
condition|(
name|resourceCalculatorPlugin
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"ResourceCalculatorPlugin is unavailable on this system. "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" is disabled."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|ResourceCalculatorProcessTree
operator|.
name|getResourceCalculatorProcessTree
argument_list|(
literal|"0"
argument_list|,
name|processTreeClass
argument_list|,
name|conf
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"ResourceCalculatorProcessTree is unavailable on this system. "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" is disabled."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|isPhysicalMemoryCheckEnabled
argument_list|()
operator|||
name|isVirtualMemoryCheckEnabled
argument_list|()
operator|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Neither virutal-memory nor physical-memory monitoring is "
operator|+
literal|"needed. Not running the monitor-thread"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|monitoringThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|monitoringThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|monitoringThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
empty_stmt|;
block|}
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|class|ProcessTreeInfo
specifier|private
specifier|static
class|class
name|ProcessTreeInfo
block|{
DECL|field|containerId
specifier|private
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|pid
specifier|private
name|String
name|pid
decl_stmt|;
DECL|field|pTree
specifier|private
name|ResourceCalculatorProcessTree
name|pTree
decl_stmt|;
DECL|field|vmemLimit
specifier|private
name|long
name|vmemLimit
decl_stmt|;
DECL|field|pmemLimit
specifier|private
name|long
name|pmemLimit
decl_stmt|;
DECL|method|ProcessTreeInfo (ContainerId containerId, String pid, ResourceCalculatorProcessTree pTree, long vmemLimit, long pmemLimit)
specifier|public
name|ProcessTreeInfo
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|String
name|pid
parameter_list|,
name|ResourceCalculatorProcessTree
name|pTree
parameter_list|,
name|long
name|vmemLimit
parameter_list|,
name|long
name|pmemLimit
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|pid
operator|=
name|pid
expr_stmt|;
name|this
operator|.
name|pTree
operator|=
name|pTree
expr_stmt|;
name|this
operator|.
name|vmemLimit
operator|=
name|vmemLimit
expr_stmt|;
name|this
operator|.
name|pmemLimit
operator|=
name|pmemLimit
expr_stmt|;
block|}
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerId
return|;
block|}
DECL|method|getPID ()
specifier|public
name|String
name|getPID
parameter_list|()
block|{
return|return
name|this
operator|.
name|pid
return|;
block|}
DECL|method|setPid (String pid)
specifier|public
name|void
name|setPid
parameter_list|(
name|String
name|pid
parameter_list|)
block|{
name|this
operator|.
name|pid
operator|=
name|pid
expr_stmt|;
block|}
DECL|method|getProcessTree ()
specifier|public
name|ResourceCalculatorProcessTree
name|getProcessTree
parameter_list|()
block|{
return|return
name|this
operator|.
name|pTree
return|;
block|}
DECL|method|setProcessTree (ResourceCalculatorProcessTree pTree)
specifier|public
name|void
name|setProcessTree
parameter_list|(
name|ResourceCalculatorProcessTree
name|pTree
parameter_list|)
block|{
name|this
operator|.
name|pTree
operator|=
name|pTree
expr_stmt|;
block|}
DECL|method|getVmemLimit ()
specifier|public
name|long
name|getVmemLimit
parameter_list|()
block|{
return|return
name|this
operator|.
name|vmemLimit
return|;
block|}
comment|/**      * @return Physical memory limit for the process tree in bytes      */
DECL|method|getPmemLimit ()
specifier|public
name|long
name|getPmemLimit
parameter_list|()
block|{
return|return
name|this
operator|.
name|pmemLimit
return|;
block|}
block|}
comment|/**    * Check whether a container's process tree's current memory usage is over    * limit.    *    * When a java process exec's a program, it could momentarily account for    * double the size of it's memory, because the JVM does a fork()+exec()    * which at fork time creates a copy of the parent's memory. If the    * monitoring thread detects the memory used by the container tree at the    * same instance, it could assume it is over limit and kill the tree, for no    * fault of the process itself.    *    * We counter this problem by employing a heuristic check: - if a process    * tree exceeds the memory limit by more than twice, it is killed    * immediately - if a process tree has processes older than the monitoring    * interval exceeding the memory limit by even 1 time, it is killed. Else it    * is given the benefit of doubt to lie around for one more iteration.    *    * @param containerId    *          Container Id for the container tree    * @param currentMemUsage    *          Memory usage of a container tree    * @param curMemUsageOfAgedProcesses    *          Memory usage of processes older than an iteration in a container    *          tree    * @param vmemLimit    *          The limit specified for the container    * @return true if the memory usage is more than twice the specified limit,    *         or if processes in the tree, older than this thread's monitoring    *         interval, exceed the memory limit. False, otherwise.    */
DECL|method|isProcessTreeOverLimit (String containerId, long currentMemUsage, long curMemUsageOfAgedProcesses, long vmemLimit)
name|boolean
name|isProcessTreeOverLimit
parameter_list|(
name|String
name|containerId
parameter_list|,
name|long
name|currentMemUsage
parameter_list|,
name|long
name|curMemUsageOfAgedProcesses
parameter_list|,
name|long
name|vmemLimit
parameter_list|)
block|{
name|boolean
name|isOverLimit
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|currentMemUsage
operator|>
operator|(
literal|2
operator|*
name|vmemLimit
operator|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Process tree for container: "
operator|+
name|containerId
operator|+
literal|" running over twice "
operator|+
literal|"the configured limit. Limit="
operator|+
name|vmemLimit
operator|+
literal|", current usage = "
operator|+
name|currentMemUsage
argument_list|)
expr_stmt|;
name|isOverLimit
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|curMemUsageOfAgedProcesses
operator|>
name|vmemLimit
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Process tree for container: "
operator|+
name|containerId
operator|+
literal|" has processes older than 1 "
operator|+
literal|"iteration running over the configured limit. Limit="
operator|+
name|vmemLimit
operator|+
literal|", current usage = "
operator|+
name|curMemUsageOfAgedProcesses
argument_list|)
expr_stmt|;
name|isOverLimit
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|isOverLimit
return|;
block|}
comment|// method provided just for easy testing purposes
DECL|method|isProcessTreeOverLimit (ResourceCalculatorProcessTree pTree, String containerId, long limit)
name|boolean
name|isProcessTreeOverLimit
parameter_list|(
name|ResourceCalculatorProcessTree
name|pTree
parameter_list|,
name|String
name|containerId
parameter_list|,
name|long
name|limit
parameter_list|)
block|{
name|long
name|currentMemUsage
init|=
name|pTree
operator|.
name|getCumulativeVmem
argument_list|()
decl_stmt|;
comment|// as processes begin with an age 1, we want to see if there are processes
comment|// more than 1 iteration old.
name|long
name|curMemUsageOfAgedProcesses
init|=
name|pTree
operator|.
name|getCumulativeVmem
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
name|isProcessTreeOverLimit
argument_list|(
name|containerId
argument_list|,
name|currentMemUsage
argument_list|,
name|curMemUsageOfAgedProcesses
argument_list|,
name|limit
argument_list|)
return|;
block|}
DECL|class|MonitoringThread
specifier|private
class|class
name|MonitoringThread
extends|extends
name|Thread
block|{
DECL|method|MonitoringThread ()
specifier|public
name|MonitoringThread
parameter_list|()
block|{
name|super
argument_list|(
literal|"Container Monitor"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
comment|// Print the processTrees for debugging.
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|tmp
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"[ "
argument_list|)
decl_stmt|;
for|for
control|(
name|ProcessTreeInfo
name|p
range|:
name|trackingContainers
operator|.
name|values
argument_list|()
control|)
block|{
name|tmp
operator|.
name|append
argument_list|(
name|p
operator|.
name|getPID
argument_list|()
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Current ProcessTree list : "
operator|+
name|tmp
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|tmp
operator|.
name|length
argument_list|()
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
comment|// Add new containers
synchronized|synchronized
init|(
name|containersToBeAdded
init|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|ProcessTreeInfo
argument_list|>
name|entry
range|:
name|containersToBeAdded
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
name|ProcessTreeInfo
name|processTreeInfo
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting resource-monitoring for "
operator|+
name|containerId
argument_list|)
expr_stmt|;
name|trackingContainers
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|processTreeInfo
argument_list|)
expr_stmt|;
block|}
name|containersToBeAdded
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Remove finished containers
synchronized|synchronized
init|(
name|containersToBeRemoved
init|)
block|{
for|for
control|(
name|ContainerId
name|containerId
range|:
name|containersToBeRemoved
control|)
block|{
name|trackingContainers
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping resource-monitoring for "
operator|+
name|containerId
argument_list|)
expr_stmt|;
block|}
name|containersToBeRemoved
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Now do the monitoring for the trackingContainers
comment|// Check memory usage and kill any overflowing containers
name|long
name|vmemStillInUsage
init|=
literal|0
decl_stmt|;
name|long
name|pmemStillInUsage
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|ProcessTreeInfo
argument_list|>
argument_list|>
name|it
init|=
name|trackingContainers
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|ProcessTreeInfo
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|ProcessTreeInfo
name|ptInfo
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|pId
init|=
name|ptInfo
operator|.
name|getPID
argument_list|()
decl_stmt|;
comment|// Initialize any uninitialized processTrees
if|if
condition|(
name|pId
operator|==
literal|null
condition|)
block|{
comment|// get pid from ContainerId
name|pId
operator|=
name|containerExecutor
operator|.
name|getProcessId
argument_list|(
name|ptInfo
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|pId
operator|!=
literal|null
condition|)
block|{
comment|// pId will be null, either if the container is not spawned yet
comment|// or if the container's pid is removed from ContainerExecutor
name|LOG
operator|.
name|debug
argument_list|(
literal|"Tracking ProcessTree "
operator|+
name|pId
operator|+
literal|" for the first time"
argument_list|)
expr_stmt|;
name|ResourceCalculatorProcessTree
name|pt
init|=
name|ResourceCalculatorProcessTree
operator|.
name|getResourceCalculatorProcessTree
argument_list|(
name|pId
argument_list|,
name|processTreeClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ptInfo
operator|.
name|setPid
argument_list|(
name|pId
argument_list|)
expr_stmt|;
name|ptInfo
operator|.
name|setProcessTree
argument_list|(
name|pt
argument_list|)
expr_stmt|;
block|}
block|}
comment|// End of initializing any uninitialized processTrees
if|if
condition|(
name|pId
operator|==
literal|null
condition|)
block|{
continue|continue;
comment|// processTree cannot be tracked
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Constructing ProcessTree for : PID = "
operator|+
name|pId
operator|+
literal|" ContainerId = "
operator|+
name|containerId
argument_list|)
expr_stmt|;
name|ResourceCalculatorProcessTree
name|pTree
init|=
name|ptInfo
operator|.
name|getProcessTree
argument_list|()
decl_stmt|;
name|pTree
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
comment|// update process-tree
name|long
name|currentVmemUsage
init|=
name|pTree
operator|.
name|getCumulativeVmem
argument_list|()
decl_stmt|;
name|long
name|currentPmemUsage
init|=
name|pTree
operator|.
name|getCumulativeRssmem
argument_list|()
decl_stmt|;
comment|// as processes begin with an age 1, we want to see if there
comment|// are processes more than 1 iteration old.
name|long
name|curMemUsageOfAgedProcesses
init|=
name|pTree
operator|.
name|getCumulativeVmem
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|curRssMemUsageOfAgedProcesses
init|=
name|pTree
operator|.
name|getCumulativeRssmem
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|vmemLimit
init|=
name|ptInfo
operator|.
name|getVmemLimit
argument_list|()
decl_stmt|;
name|long
name|pmemLimit
init|=
name|ptInfo
operator|.
name|getPmemLimit
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Memory usage of ProcessTree %s for container-id %s: "
argument_list|,
name|pId
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
operator|+
name|formatUsageString
argument_list|(
name|currentVmemUsage
argument_list|,
name|vmemLimit
argument_list|,
name|currentPmemUsage
argument_list|,
name|pmemLimit
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|isMemoryOverLimit
init|=
literal|false
decl_stmt|;
name|String
name|msg
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|isVirtualMemoryCheckEnabled
argument_list|()
operator|&&
name|isProcessTreeOverLimit
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|,
name|currentVmemUsage
argument_list|,
name|curMemUsageOfAgedProcesses
argument_list|,
name|vmemLimit
argument_list|)
condition|)
block|{
comment|// Container (the root process) is still alive and overflowing
comment|// memory.
comment|// Dump the process-tree and then clean it up.
name|msg
operator|=
name|formatErrorMessage
argument_list|(
literal|"virtual"
argument_list|,
name|currentVmemUsage
argument_list|,
name|vmemLimit
argument_list|,
name|currentPmemUsage
argument_list|,
name|pmemLimit
argument_list|,
name|pId
argument_list|,
name|containerId
argument_list|,
name|pTree
argument_list|)
expr_stmt|;
name|isMemoryOverLimit
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isPhysicalMemoryCheckEnabled
argument_list|()
operator|&&
name|isProcessTreeOverLimit
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|,
name|currentPmemUsage
argument_list|,
name|curRssMemUsageOfAgedProcesses
argument_list|,
name|pmemLimit
argument_list|)
condition|)
block|{
comment|// Container (the root process) is still alive and overflowing
comment|// memory.
comment|// Dump the process-tree and then clean it up.
name|msg
operator|=
name|formatErrorMessage
argument_list|(
literal|"physical"
argument_list|,
name|currentVmemUsage
argument_list|,
name|vmemLimit
argument_list|,
name|currentPmemUsage
argument_list|,
name|pmemLimit
argument_list|,
name|pId
argument_list|,
name|containerId
argument_list|,
name|pTree
argument_list|)
expr_stmt|;
name|isMemoryOverLimit
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|isMemoryOverLimit
condition|)
block|{
comment|// Virtual or physical memory over limit. Fail the container and
comment|// remove
comment|// the corresponding process tree
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// warn if not a leader
if|if
condition|(
operator|!
name|pTree
operator|.
name|checkPidPgrpidForMatch
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Killed container process with PID "
operator|+
name|pId
operator|+
literal|" but it is not a process group leader."
argument_list|)
expr_stmt|;
block|}
comment|// kill the container
name|eventDispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerKillEvent
argument_list|(
name|containerId
argument_list|,
name|msg
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed ProcessTree with root "
operator|+
name|pId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Accounting the total memory in usage for all containers that
comment|// are still
comment|// alive and within limits.
name|vmemStillInUsage
operator|+=
name|currentVmemUsage
expr_stmt|;
name|pmemStillInUsage
operator|+=
name|currentPmemUsage
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Log the exception and proceed to the next container.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Uncaught exception in ContainerMemoryManager "
operator|+
literal|"while managing memory of "
operator|+
name|containerId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|monitoringInterval
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
name|warn
argument_list|(
name|ContainersMonitorImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" is interrupted. Exiting."
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|formatErrorMessage (String memTypeExceeded, long currentVmemUsage, long vmemLimit, long currentPmemUsage, long pmemLimit, String pId, ContainerId containerId, ResourceCalculatorProcessTree pTree)
specifier|private
name|String
name|formatErrorMessage
parameter_list|(
name|String
name|memTypeExceeded
parameter_list|,
name|long
name|currentVmemUsage
parameter_list|,
name|long
name|vmemLimit
parameter_list|,
name|long
name|currentPmemUsage
parameter_list|,
name|long
name|pmemLimit
parameter_list|,
name|String
name|pId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|ResourceCalculatorProcessTree
name|pTree
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Container [pid=%s,containerID=%s] is running beyond %s memory limits. "
argument_list|,
name|pId
argument_list|,
name|containerId
argument_list|,
name|memTypeExceeded
argument_list|)
operator|+
literal|"Current usage: "
operator|+
name|formatUsageString
argument_list|(
name|currentVmemUsage
argument_list|,
name|vmemLimit
argument_list|,
name|currentPmemUsage
argument_list|,
name|pmemLimit
argument_list|)
operator|+
literal|". Killing container.\n"
operator|+
literal|"Dump of the process-tree for "
operator|+
name|containerId
operator|+
literal|" :\n"
operator|+
name|pTree
operator|.
name|getProcessTreeDump
argument_list|()
return|;
block|}
DECL|method|formatUsageString (long currentVmemUsage, long vmemLimit, long currentPmemUsage, long pmemLimit)
specifier|private
name|String
name|formatUsageString
parameter_list|(
name|long
name|currentVmemUsage
parameter_list|,
name|long
name|vmemLimit
parameter_list|,
name|long
name|currentPmemUsage
parameter_list|,
name|long
name|pmemLimit
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%sB of %sB physical memory used; "
operator|+
literal|"%sB of %sB virtual memory used"
argument_list|,
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|currentPmemUsage
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|)
argument_list|,
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|pmemLimit
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|)
argument_list|,
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|currentVmemUsage
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|)
argument_list|,
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|vmemLimit
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getVmemAllocatedForContainers ()
specifier|public
name|long
name|getVmemAllocatedForContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxVmemAllottedForContainers
return|;
block|}
annotation|@
name|Override
DECL|method|getPmemAllocatedForContainers ()
specifier|public
name|long
name|getPmemAllocatedForContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxPmemAllottedForContainers
return|;
block|}
annotation|@
name|Override
DECL|method|handle (ContainersMonitorEvent monitoringEvent)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainersMonitorEvent
name|monitoringEvent
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|ContainerId
name|containerId
init|=
name|monitoringEvent
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|monitoringEvent
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|START_MONITORING_CONTAINER
case|:
name|ContainerStartMonitoringEvent
name|startEvent
init|=
operator|(
name|ContainerStartMonitoringEvent
operator|)
name|monitoringEvent
decl_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|containersToBeAdded
init|)
block|{
name|ProcessTreeInfo
name|processTreeInfo
init|=
operator|new
name|ProcessTreeInfo
argument_list|(
name|containerId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|startEvent
operator|.
name|getVmemLimit
argument_list|()
argument_list|,
name|startEvent
operator|.
name|getPmemLimit
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|containersToBeAdded
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|processTreeInfo
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|STOP_MONITORING_CONTAINER
case|:
synchronized|synchronized
init|(
name|this
operator|.
name|containersToBeRemoved
init|)
block|{
name|this
operator|.
name|containersToBeRemoved
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
comment|// TODO: Wrong event.
block|}
block|}
block|}
end_class

end_unit

