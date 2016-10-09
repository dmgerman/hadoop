begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.distributed
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|distributed
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|ResourceOption
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
name|api
operator|.
name|protocolrecords
operator|.
name|NMContainerStatus
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
name|api
operator|.
name|records
operator|.
name|QueuedContainersStatus
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
name|resourcemanager
operator|.
name|ClusterMonitor
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
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
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
name|ScheduledExecutorService
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
name|TimeUnit
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * The NodeQueueLoadMonitor keeps track of load metrics (such as queue length  * and total wait time) associated with Container Queues on the Node Manager.  * It uses this information to periodically sort the Nodes from least to most  * loaded.  */
end_comment

begin_class
DECL|class|NodeQueueLoadMonitor
specifier|public
class|class
name|NodeQueueLoadMonitor
implements|implements
name|ClusterMonitor
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
name|NodeQueueLoadMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The comparator used to specify the metric against which the load    * of two Nodes are compared.    */
DECL|enum|LoadComparator
specifier|public
enum|enum
name|LoadComparator
implements|implements
name|Comparator
argument_list|<
name|ClusterNode
argument_list|>
block|{
DECL|enumConstant|QUEUE_LENGTH
name|QUEUE_LENGTH
block|,
DECL|enumConstant|QUEUE_WAIT_TIME
name|QUEUE_WAIT_TIME
block|;
annotation|@
name|Override
DECL|method|compare (ClusterNode o1, ClusterNode o2)
specifier|public
name|int
name|compare
parameter_list|(
name|ClusterNode
name|o1
parameter_list|,
name|ClusterNode
name|o2
parameter_list|)
block|{
if|if
condition|(
name|getMetric
argument_list|(
name|o1
argument_list|)
operator|==
name|getMetric
argument_list|(
name|o2
argument_list|)
condition|)
block|{
return|return
name|o1
operator|.
name|timestamp
operator|<
name|o2
operator|.
name|timestamp
condition|?
operator|+
literal|1
else|:
operator|-
literal|1
return|;
block|}
return|return
name|getMetric
argument_list|(
name|o1
argument_list|)
operator|>
name|getMetric
argument_list|(
name|o2
argument_list|)
condition|?
operator|+
literal|1
else|:
operator|-
literal|1
return|;
block|}
DECL|method|getMetric (ClusterNode c)
specifier|public
name|int
name|getMetric
parameter_list|(
name|ClusterNode
name|c
parameter_list|)
block|{
return|return
operator|(
name|this
operator|==
name|QUEUE_LENGTH
operator|)
condition|?
name|c
operator|.
name|queueLength
else|:
name|c
operator|.
name|queueWaitTime
return|;
block|}
block|}
DECL|class|ClusterNode
specifier|static
class|class
name|ClusterNode
block|{
DECL|field|queueLength
name|int
name|queueLength
init|=
literal|0
decl_stmt|;
DECL|field|queueWaitTime
name|int
name|queueWaitTime
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|timestamp
name|double
name|timestamp
decl_stmt|;
DECL|field|nodeId
specifier|final
name|NodeId
name|nodeId
decl_stmt|;
DECL|method|ClusterNode (NodeId nodeId)
specifier|public
name|ClusterNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|updateTimestamp
argument_list|()
expr_stmt|;
block|}
DECL|method|setQueueLength (int qLength)
specifier|public
name|ClusterNode
name|setQueueLength
parameter_list|(
name|int
name|qLength
parameter_list|)
block|{
name|this
operator|.
name|queueLength
operator|=
name|qLength
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setQueueWaitTime (int wTime)
specifier|public
name|ClusterNode
name|setQueueWaitTime
parameter_list|(
name|int
name|wTime
parameter_list|)
block|{
name|this
operator|.
name|queueWaitTime
operator|=
name|wTime
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|updateTimestamp ()
specifier|public
name|ClusterNode
name|updateTimestamp
parameter_list|()
block|{
name|this
operator|.
name|timestamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|field|scheduledExecutor
specifier|private
specifier|final
name|ScheduledExecutorService
name|scheduledExecutor
decl_stmt|;
DECL|field|sortedNodes
specifier|private
specifier|final
name|List
argument_list|<
name|NodeId
argument_list|>
name|sortedNodes
decl_stmt|;
DECL|field|clusterNodes
specifier|private
specifier|final
name|Map
argument_list|<
name|NodeId
argument_list|,
name|ClusterNode
argument_list|>
name|clusterNodes
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|LoadComparator
name|comparator
decl_stmt|;
DECL|field|thresholdCalculator
specifier|private
name|QueueLimitCalculator
name|thresholdCalculator
decl_stmt|;
DECL|field|sortedNodesLock
specifier|private
name|ReentrantReadWriteLock
name|sortedNodesLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|clusterNodesLock
specifier|private
name|ReentrantReadWriteLock
name|clusterNodesLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|computeTask
name|Runnable
name|computeTask
init|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ReentrantReadWriteLock
operator|.
name|WriteLock
name|writeLock
init|=
name|sortedNodesLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|sortedNodes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|sortedNodes
operator|.
name|addAll
argument_list|(
name|sortNodes
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|thresholdCalculator
operator|!=
literal|null
condition|)
block|{
name|thresholdCalculator
operator|.
name|update
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|NodeQueueLoadMonitor (LoadComparator comparator)
name|NodeQueueLoadMonitor
parameter_list|(
name|LoadComparator
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|sortedNodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|this
operator|.
name|scheduledExecutor
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|NodeQueueLoadMonitor (long nodeComputationInterval, LoadComparator comparator)
specifier|public
name|NodeQueueLoadMonitor
parameter_list|(
name|long
name|nodeComputationInterval
parameter_list|,
name|LoadComparator
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|sortedNodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|scheduledExecutor
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|this
operator|.
name|scheduledExecutor
operator|.
name|scheduleAtFixedRate
argument_list|(
name|computeTask
argument_list|,
name|nodeComputationInterval
argument_list|,
name|nodeComputationInterval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|getSortedNodes ()
name|List
argument_list|<
name|NodeId
argument_list|>
name|getSortedNodes
parameter_list|()
block|{
return|return
name|sortedNodes
return|;
block|}
DECL|method|getThresholdCalculator ()
specifier|public
name|QueueLimitCalculator
name|getThresholdCalculator
parameter_list|()
block|{
return|return
name|thresholdCalculator
return|;
block|}
DECL|method|getClusterNodes ()
name|Map
argument_list|<
name|NodeId
argument_list|,
name|ClusterNode
argument_list|>
name|getClusterNodes
parameter_list|()
block|{
return|return
name|clusterNodes
return|;
block|}
DECL|method|getComparator ()
name|Comparator
argument_list|<
name|ClusterNode
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|comparator
return|;
block|}
DECL|method|initThresholdCalculator (float sigma, int limitMin, int limitMax)
specifier|public
name|void
name|initThresholdCalculator
parameter_list|(
name|float
name|sigma
parameter_list|,
name|int
name|limitMin
parameter_list|,
name|int
name|limitMax
parameter_list|)
block|{
name|this
operator|.
name|thresholdCalculator
operator|=
operator|new
name|QueueLimitCalculator
argument_list|(
name|this
argument_list|,
name|sigma
argument_list|,
name|limitMin
argument_list|,
name|limitMax
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addNode (List<NMContainerStatus> containerStatuses, RMNode rmNode)
specifier|public
name|void
name|addNode
parameter_list|(
name|List
argument_list|<
name|NMContainerStatus
argument_list|>
name|containerStatuses
parameter_list|,
name|RMNode
name|rmNode
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Node added event from: "
operator|+
name|rmNode
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Ignoring this currently : at least one NODE_UPDATE heartbeat is
comment|// required to ensure node eligibility.
block|}
annotation|@
name|Override
DECL|method|removeNode (RMNode removedRMNode)
specifier|public
name|void
name|removeNode
parameter_list|(
name|RMNode
name|removedRMNode
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Node delete event for: "
operator|+
name|removedRMNode
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ReentrantReadWriteLock
operator|.
name|WriteLock
name|writeLock
init|=
name|clusterNodesLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|ClusterNode
name|node
decl_stmt|;
try|try
block|{
name|node
operator|=
name|this
operator|.
name|clusterNodes
operator|.
name|remove
argument_list|(
name|removedRMNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Delete ClusterNode: "
operator|+
name|removedRMNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Node not in list!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|updateNode (RMNode rmNode)
specifier|public
name|void
name|updateNode
parameter_list|(
name|RMNode
name|rmNode
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Node update event from: "
operator|+
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|QueuedContainersStatus
name|queuedContainersStatus
init|=
name|rmNode
operator|.
name|getQueuedContainersStatus
argument_list|()
decl_stmt|;
name|int
name|estimatedQueueWaitTime
init|=
name|queuedContainersStatus
operator|.
name|getEstimatedQueueWaitTime
argument_list|()
decl_stmt|;
name|int
name|waitQueueLength
init|=
name|queuedContainersStatus
operator|.
name|getWaitQueueLength
argument_list|()
decl_stmt|;
comment|// Add nodes to clusterNodes. If estimatedQueueTime is -1, ignore node
comment|// UNLESS comparator is based on queue length.
name|ReentrantReadWriteLock
operator|.
name|WriteLock
name|writeLock
init|=
name|clusterNodesLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ClusterNode
name|currentNode
init|=
name|this
operator|.
name|clusterNodes
operator|.
name|get
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentNode
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|estimatedQueueWaitTime
operator|!=
operator|-
literal|1
operator|||
name|comparator
operator|==
name|LoadComparator
operator|.
name|QUEUE_LENGTH
condition|)
block|{
name|this
operator|.
name|clusterNodes
operator|.
name|put
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|,
operator|new
name|ClusterNode
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
operator|.
name|setQueueWaitTime
argument_list|(
name|estimatedQueueWaitTime
argument_list|)
operator|.
name|setQueueLength
argument_list|(
name|waitQueueLength
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Inserting ClusterNode ["
operator|+
name|rmNode
operator|.
name|getNodeID
argument_list|()
operator|+
literal|"] "
operator|+
literal|"with queue wait time ["
operator|+
name|estimatedQueueWaitTime
operator|+
literal|"] and "
operator|+
literal|"wait queue length ["
operator|+
name|waitQueueLength
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"IGNORING ClusterNode ["
operator|+
name|rmNode
operator|.
name|getNodeID
argument_list|()
operator|+
literal|"] "
operator|+
literal|"with queue wait time ["
operator|+
name|estimatedQueueWaitTime
operator|+
literal|"] and "
operator|+
literal|"wait queue length ["
operator|+
name|waitQueueLength
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|estimatedQueueWaitTime
operator|!=
operator|-
literal|1
operator|||
name|comparator
operator|==
name|LoadComparator
operator|.
name|QUEUE_LENGTH
condition|)
block|{
name|currentNode
operator|.
name|setQueueWaitTime
argument_list|(
name|estimatedQueueWaitTime
argument_list|)
operator|.
name|setQueueLength
argument_list|(
name|waitQueueLength
argument_list|)
operator|.
name|updateTimestamp
argument_list|()
expr_stmt|;
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
literal|"Updating ClusterNode ["
operator|+
name|rmNode
operator|.
name|getNodeID
argument_list|()
operator|+
literal|"] "
operator|+
literal|"with queue wait time ["
operator|+
name|estimatedQueueWaitTime
operator|+
literal|"] and "
operator|+
literal|"wait queue length ["
operator|+
name|waitQueueLength
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|clusterNodes
operator|.
name|remove
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting ClusterNode ["
operator|+
name|rmNode
operator|.
name|getNodeID
argument_list|()
operator|+
literal|"] "
operator|+
literal|"with queue wait time ["
operator|+
name|currentNode
operator|.
name|queueWaitTime
operator|+
literal|"] and "
operator|+
literal|"wait queue length ["
operator|+
name|currentNode
operator|.
name|queueLength
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateNodeResource (RMNode rmNode, ResourceOption resourceOption)
specifier|public
name|void
name|updateNodeResource
parameter_list|(
name|RMNode
name|rmNode
parameter_list|,
name|ResourceOption
name|resourceOption
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Node resource update event from: "
operator|+
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
comment|// Ignoring this currently.
block|}
comment|/**    * Returns all Node Ids as ordered list from Least to Most Loaded.    * @return ordered list of nodes    */
DECL|method|selectNodes ()
specifier|public
name|List
argument_list|<
name|NodeId
argument_list|>
name|selectNodes
parameter_list|()
block|{
return|return
name|selectLeastLoadedNodes
argument_list|(
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Returns 'K' of the least Loaded Node Ids as ordered list.    * @param k max number of nodes to return    * @return ordered list of nodes    */
DECL|method|selectLeastLoadedNodes (int k)
specifier|public
name|List
argument_list|<
name|NodeId
argument_list|>
name|selectLeastLoadedNodes
parameter_list|(
name|int
name|k
parameter_list|)
block|{
name|ReentrantReadWriteLock
operator|.
name|ReadLock
name|readLock
init|=
name|sortedNodesLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|NodeId
argument_list|>
name|retVal
init|=
operator|(
operator|(
name|k
operator|<
name|this
operator|.
name|sortedNodes
operator|.
name|size
argument_list|()
operator|)
operator|&&
operator|(
name|k
operator|>=
literal|0
operator|)
operator|)
condition|?
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|this
operator|.
name|sortedNodes
argument_list|)
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|k
argument_list|)
else|:
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|this
operator|.
name|sortedNodes
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|retVal
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|sortNodes ()
specifier|private
name|List
argument_list|<
name|NodeId
argument_list|>
name|sortNodes
parameter_list|()
block|{
name|ReentrantReadWriteLock
operator|.
name|ReadLock
name|readLock
init|=
name|clusterNodesLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ArrayList
name|aList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|this
operator|.
name|clusterNodes
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NodeId
argument_list|>
name|retList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Object
index|[]
name|nodes
init|=
name|aList
operator|.
name|toArray
argument_list|()
decl_stmt|;
comment|// Collections.sort would do something similar by calling Arrays.sort
comment|// internally but would finally iterate through the input list (aList)
comment|// to reset the value of each element. Since we don't really care about
comment|// 'aList', we can use the iteration to create the list of nodeIds which
comment|// is what we ultimately care about.
name|Arrays
operator|.
name|sort
argument_list|(
name|nodes
argument_list|,
operator|(
name|Comparator
operator|)
name|comparator
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nodes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|retList
operator|.
name|add
argument_list|(
operator|(
operator|(
name|ClusterNode
operator|)
name|nodes
index|[
name|j
index|]
operator|)
operator|.
name|nodeId
argument_list|)
expr_stmt|;
block|}
return|return
name|retList
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

