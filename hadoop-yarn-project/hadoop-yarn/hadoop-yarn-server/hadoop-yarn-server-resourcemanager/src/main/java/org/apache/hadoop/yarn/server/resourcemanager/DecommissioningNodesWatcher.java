begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
package|;
end_package

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
name|HashSet
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
name|Map
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|ContainerState
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
name|ContainerStatus
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
name|NodeState
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
name|api
operator|.
name|records
operator|.
name|NodeStatus
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
name|rmapp
operator|.
name|RMApp
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
name|rmapp
operator|.
name|RMAppState
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
name|RMNodeEvent
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
name|RMNodeEventType
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
name|MonotonicClock
import|;
end_import

begin_comment
comment|/**  * DecommissioningNodesWatcher is used by ResourceTrackerService to track  * DECOMMISSIONING nodes to decide when, after all running containers on  * the node have completed, will be transitioned into DECOMMISSIONED state  * (NodeManager will be told to shutdown).  * Under MR application, a node, after completes all its containers,  * may still serve it map output data during the duration of the application  * for reducers. A fully graceful mechanism would keep such DECOMMISSIONING  * nodes until all involved applications complete. It could be however  * undesirable under long-running applications scenario where a bunch  * of "idle" nodes would stay around for long period of time.  *  * DecommissioningNodesWatcher balance such concern with a timeout policy ---  * a DECOMMISSIONING node will be DECOMMISSIONED no later than  * DECOMMISSIONING_TIMEOUT regardless of running containers or applications.  *  * To be efficient, DecommissioningNodesWatcher skip tracking application  * containers on a particular node before the node is in DECOMMISSIONING state.  * It only tracks containers once the node is in DECOMMISSIONING state.  * DecommissioningNodesWatcher basically is no cost when no node is  * DECOMMISSIONING. This sacrifices the possibility that the node once  * host containers of an application that is still running  * (the affected map tasks will be rescheduled).  */
end_comment

begin_class
DECL|class|DecommissioningNodesWatcher
specifier|public
class|class
name|DecommissioningNodesWatcher
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DecommissioningNodesWatcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
comment|// Default timeout value in mills.
comment|// Negative value indicates no timeout. 0 means immediate.
DECL|field|defaultTimeoutMs
specifier|private
name|long
name|defaultTimeoutMs
init|=
literal|1000L
operator|*
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT
decl_stmt|;
comment|// Once a RMNode is observed in DECOMMISSIONING state,
comment|// All its ContainerStatus update are tracked inside DecomNodeContext.
DECL|class|DecommissioningNodeContext
class|class
name|DecommissioningNodeContext
block|{
DECL|field|nodeId
specifier|private
specifier|final
name|NodeId
name|nodeId
decl_stmt|;
comment|// Last known NodeState.
DECL|field|nodeState
specifier|private
name|NodeState
name|nodeState
decl_stmt|;
comment|// The moment node is observed in DECOMMISSIONING state.
DECL|field|decommissioningStartTime
specifier|private
specifier|final
name|long
name|decommissioningStartTime
decl_stmt|;
DECL|field|lastContainerFinishTime
specifier|private
name|long
name|lastContainerFinishTime
decl_stmt|;
comment|// number of running containers at the moment.
DECL|field|numActiveContainers
specifier|private
name|int
name|numActiveContainers
decl_stmt|;
comment|// All applications run on the node at or after decommissioningStartTime.
DECL|field|appIds
specifier|private
name|Set
argument_list|<
name|ApplicationId
argument_list|>
name|appIds
decl_stmt|;
comment|// First moment the node is observed in DECOMMISSIONED state.
DECL|field|decommissionedTime
specifier|private
name|long
name|decommissionedTime
decl_stmt|;
comment|// Timeout in millis for this decommissioning node.
comment|// This value could be dynamically updated with new value from RMNode.
DECL|field|timeoutMs
specifier|private
name|long
name|timeoutMs
decl_stmt|;
DECL|field|lastUpdateTime
specifier|private
name|long
name|lastUpdateTime
decl_stmt|;
DECL|method|DecommissioningNodeContext (NodeId nodeId)
specifier|public
name|DecommissioningNodeContext
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
name|this
operator|.
name|appIds
operator|=
operator|new
name|HashSet
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|decommissioningStartTime
operator|=
name|mclock
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|timeoutMs
operator|=
name|defaultTimeoutMs
expr_stmt|;
block|}
DECL|method|updateTimeout (Integer timeoutSec)
name|void
name|updateTimeout
parameter_list|(
name|Integer
name|timeoutSec
parameter_list|)
block|{
name|this
operator|.
name|timeoutMs
operator|=
operator|(
name|timeoutSec
operator|==
literal|null
operator|)
condition|?
name|defaultTimeoutMs
else|:
operator|(
literal|1000L
operator|*
name|timeoutSec
operator|)
expr_stmt|;
block|}
block|}
comment|// All DECOMMISSIONING nodes to track.
DECL|field|decomNodes
specifier|private
name|HashMap
argument_list|<
name|NodeId
argument_list|,
name|DecommissioningNodeContext
argument_list|>
name|decomNodes
init|=
operator|new
name|HashMap
argument_list|<
name|NodeId
argument_list|,
name|DecommissioningNodeContext
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|pollTimer
specifier|private
name|Timer
name|pollTimer
decl_stmt|;
DECL|field|mclock
specifier|private
name|MonotonicClock
name|mclock
decl_stmt|;
DECL|method|DecommissioningNodesWatcher (RMContext rmContext)
specifier|public
name|DecommissioningNodesWatcher
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
name|pollTimer
operator|=
operator|new
name|Timer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|mclock
operator|=
operator|new
name|MonotonicClock
argument_list|()
expr_stmt|;
block|}
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|readDecommissioningTimeout
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|v
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_DECOMMISSIONING_NODES_WATCHER_POLL_INTERVAL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_DECOMMISSIONING_NODES_WATCHER_POLL_INTERVAL
argument_list|)
decl_stmt|;
name|pollTimer
operator|.
name|schedule
argument_list|(
operator|new
name|PollTimerTask
argument_list|(
name|rmContext
argument_list|)
argument_list|,
literal|0
argument_list|,
operator|(
literal|1000L
operator|*
name|v
operator|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update rmNode decommissioning status based on NodeStatus.    * @param rmNode The node    * @param remoteNodeStatus latest NodeStatus    */
DECL|method|update (RMNode rmNode, NodeStatus remoteNodeStatus)
specifier|public
specifier|synchronized
name|void
name|update
parameter_list|(
name|RMNode
name|rmNode
parameter_list|,
name|NodeStatus
name|remoteNodeStatus
parameter_list|)
block|{
name|DecommissioningNodeContext
name|context
init|=
name|decomNodes
operator|.
name|get
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|mclock
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|rmNode
operator|.
name|getState
argument_list|()
operator|==
name|NodeState
operator|.
name|DECOMMISSIONED
condition|)
block|{
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|context
operator|.
name|nodeState
operator|=
name|rmNode
operator|.
name|getState
argument_list|()
expr_stmt|;
comment|// keep DECOMMISSIONED node for a while for status log, so that such
comment|// host will appear as DECOMMISSIONED instead of quietly disappears.
if|if
condition|(
name|context
operator|.
name|decommissionedTime
operator|==
literal|0
condition|)
block|{
name|context
operator|.
name|decommissionedTime
operator|=
name|now
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|now
operator|-
name|context
operator|.
name|decommissionedTime
operator|>
literal|60000L
condition|)
block|{
name|decomNodes
operator|.
name|remove
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|rmNode
operator|.
name|getState
argument_list|()
operator|==
name|NodeState
operator|.
name|DECOMMISSIONING
condition|)
block|{
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|context
operator|=
operator|new
name|DecommissioningNodeContext
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|decomNodes
operator|.
name|put
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|nodeState
operator|=
name|rmNode
operator|.
name|getState
argument_list|()
expr_stmt|;
name|context
operator|.
name|decommissionedTime
operator|=
literal|0
expr_stmt|;
block|}
name|context
operator|.
name|updateTimeout
argument_list|(
name|rmNode
operator|.
name|getDecommissioningTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|lastUpdateTime
operator|=
name|now
expr_stmt|;
if|if
condition|(
name|remoteNodeStatus
operator|.
name|getKeepAliveApplications
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|appIds
operator|.
name|addAll
argument_list|(
name|remoteNodeStatus
operator|.
name|getKeepAliveApplications
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Count number of active containers.
name|int
name|numActiveContainers
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ContainerStatus
name|cs
range|:
name|remoteNodeStatus
operator|.
name|getContainersStatuses
argument_list|()
control|)
block|{
name|ContainerState
name|newState
init|=
name|cs
operator|.
name|getState
argument_list|()
decl_stmt|;
if|if
condition|(
name|newState
operator|==
name|ContainerState
operator|.
name|RUNNING
operator|||
name|newState
operator|==
name|ContainerState
operator|.
name|NEW
condition|)
block|{
name|numActiveContainers
operator|++
expr_stmt|;
block|}
name|context
operator|.
name|numActiveContainers
operator|=
name|numActiveContainers
expr_stmt|;
name|ApplicationId
name|aid
init|=
name|cs
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|context
operator|.
name|appIds
operator|.
name|contains
argument_list|(
name|aid
argument_list|)
condition|)
block|{
name|context
operator|.
name|appIds
operator|.
name|add
argument_list|(
name|aid
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|numActiveContainers
operator|=
name|numActiveContainers
expr_stmt|;
comment|// maintain lastContainerFinishTime.
if|if
condition|(
name|context
operator|.
name|numActiveContainers
operator|==
literal|0
operator|&&
name|context
operator|.
name|lastContainerFinishTime
operator|==
literal|0
condition|)
block|{
name|context
operator|.
name|lastContainerFinishTime
operator|=
name|now
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// remove node in other states
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|decomNodes
operator|.
name|remove
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|remove (NodeId nodeId)
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|DecommissioningNodeContext
name|context
init|=
name|decomNodes
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"remove "
operator|+
name|nodeId
operator|+
literal|" in "
operator|+
name|context
operator|.
name|nodeState
argument_list|)
expr_stmt|;
name|decomNodes
operator|.
name|remove
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Status about a specific decommissioning node.    *    */
DECL|enum|DecommissioningNodeStatus
specifier|public
enum|enum
name|DecommissioningNodeStatus
block|{
comment|// Node is not in DECOMMISSIONING state.
DECL|enumConstant|NONE
name|NONE
block|,
comment|// wait for running containers to complete
DECL|enumConstant|WAIT_CONTAINER
name|WAIT_CONTAINER
block|,
comment|// wait for running application to complete (after all containers complete);
DECL|enumConstant|WAIT_APP
name|WAIT_APP
block|,
comment|// Timeout waiting for either containers or applications to complete.
DECL|enumConstant|TIMEOUT
name|TIMEOUT
block|,
comment|// nothing to wait, ready to be decommissioned
DECL|enumConstant|READY
name|READY
block|,
comment|// The node has already been decommissioned
DECL|enumConstant|DECOMMISSIONED
name|DECOMMISSIONED
block|,   }
DECL|method|checkReadyToBeDecommissioned (NodeId nodeId)
specifier|public
name|boolean
name|checkReadyToBeDecommissioned
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|DecommissioningNodeStatus
name|s
init|=
name|checkDecommissioningStatus
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
return|return
operator|(
name|s
operator|==
name|DecommissioningNodeStatus
operator|.
name|READY
operator|||
name|s
operator|==
name|DecommissioningNodeStatus
operator|.
name|TIMEOUT
operator|)
return|;
block|}
DECL|method|checkDecommissioningStatus (NodeId nodeId)
specifier|public
name|DecommissioningNodeStatus
name|checkDecommissioningStatus
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|DecommissioningNodeContext
name|context
init|=
name|decomNodes
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
return|return
name|DecommissioningNodeStatus
operator|.
name|NONE
return|;
block|}
if|if
condition|(
name|context
operator|.
name|nodeState
operator|==
name|NodeState
operator|.
name|DECOMMISSIONED
condition|)
block|{
return|return
name|DecommissioningNodeStatus
operator|.
name|DECOMMISSIONED
return|;
block|}
name|long
name|waitTime
init|=
name|mclock
operator|.
name|getTime
argument_list|()
operator|-
name|context
operator|.
name|decommissioningStartTime
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|numActiveContainers
operator|>
literal|0
condition|)
block|{
return|return
operator|(
name|context
operator|.
name|timeoutMs
operator|<
literal|0
operator|||
name|waitTime
operator|<
name|context
operator|.
name|timeoutMs
operator|)
condition|?
name|DecommissioningNodeStatus
operator|.
name|WAIT_CONTAINER
else|:
name|DecommissioningNodeStatus
operator|.
name|TIMEOUT
return|;
block|}
name|removeCompletedApps
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|appIds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|DecommissioningNodeStatus
operator|.
name|READY
return|;
block|}
else|else
block|{
return|return
operator|(
name|context
operator|.
name|timeoutMs
operator|<
literal|0
operator|||
name|waitTime
operator|<
name|context
operator|.
name|timeoutMs
operator|)
condition|?
name|DecommissioningNodeStatus
operator|.
name|WAIT_APP
else|:
name|DecommissioningNodeStatus
operator|.
name|TIMEOUT
return|;
block|}
block|}
comment|/**    * PollTimerTask periodically:    *   1. log status of all DECOMMISSIONING nodes;    *   2. identify and taken care of stale DECOMMISSIONING nodes    *      (for example, node already terminated).    */
DECL|class|PollTimerTask
class|class
name|PollTimerTask
extends|extends
name|TimerTask
block|{
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|PollTimerTask (RMContext rmContext)
specifier|public
name|PollTimerTask
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|logDecommissioningNodesStatus
argument_list|()
expr_stmt|;
name|long
name|now
init|=
name|mclock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|NodeId
argument_list|>
name|staleNodes
init|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|DecommissioningNodeContext
argument_list|>
argument_list|>
name|it
init|=
name|decomNodes
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
name|NodeId
argument_list|,
name|DecommissioningNodeContext
argument_list|>
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|DecommissioningNodeContext
name|d
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Skip node recently updated (NM usually updates every second).
if|if
condition|(
name|now
operator|-
name|d
operator|.
name|lastUpdateTime
operator|<
literal|5000L
condition|)
block|{
continue|continue;
block|}
comment|// Remove stale non-DECOMMISSIONING node
if|if
condition|(
name|d
operator|.
name|nodeState
operator|!=
name|NodeState
operator|.
name|DECOMMISSIONING
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"remove "
operator|+
name|d
operator|.
name|nodeState
operator|+
literal|" "
operator|+
name|d
operator|.
name|nodeId
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
continue|continue;
block|}
elseif|else
if|if
condition|(
name|now
operator|-
name|d
operator|.
name|lastUpdateTime
operator|>
literal|60000L
condition|)
block|{
comment|// Node DECOMMISSIONED could become stale, remove as necessary.
name|RMNode
name|rmNode
init|=
name|getRmNode
argument_list|(
name|d
operator|.
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmNode
operator|!=
literal|null
operator|&&
name|rmNode
operator|.
name|getState
argument_list|()
operator|==
name|NodeState
operator|.
name|DECOMMISSIONED
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"remove "
operator|+
name|rmNode
operator|.
name|getState
argument_list|()
operator|+
literal|" "
operator|+
name|d
operator|.
name|nodeId
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
if|if
condition|(
name|d
operator|.
name|timeoutMs
operator|>=
literal|0
operator|&&
name|d
operator|.
name|decommissioningStartTime
operator|+
name|d
operator|.
name|timeoutMs
operator|<
name|now
condition|)
block|{
name|staleNodes
operator|.
name|add
argument_list|(
name|d
operator|.
name|nodeId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Identified stale and timeout node "
operator|+
name|d
operator|.
name|nodeId
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|NodeId
name|nodeId
range|:
name|staleNodes
control|)
block|{
name|RMNode
name|rmNode
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmNode
operator|==
literal|null
operator|||
name|rmNode
operator|.
name|getState
argument_list|()
operator|!=
name|NodeState
operator|.
name|DECOMMISSIONING
condition|)
block|{
name|remove
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|rmNode
operator|.
name|getState
argument_list|()
operator|==
name|NodeState
operator|.
name|DECOMMISSIONING
operator|&&
name|checkReadyToBeDecommissioned
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"DECOMMISSIONING "
operator|+
name|nodeId
operator|+
literal|" timeout"
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|nodeId
argument_list|,
name|RMNodeEventType
operator|.
name|DECOMMISSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getRmNode (NodeId nodeId)
specifier|private
name|RMNode
name|getRmNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|RMNode
name|rmNode
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmNode
operator|==
literal|null
condition|)
block|{
name|rmNode
operator|=
name|this
operator|.
name|rmContext
operator|.
name|getInactiveRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
return|return
name|rmNode
return|;
block|}
DECL|method|removeCompletedApps (DecommissioningNodeContext context)
specifier|private
name|void
name|removeCompletedApps
parameter_list|(
name|DecommissioningNodeContext
name|context
parameter_list|)
block|{
name|Iterator
argument_list|<
name|ApplicationId
argument_list|>
name|it
init|=
name|context
operator|.
name|appIds
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ApplicationId
name|appId
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|RMApp
name|rmApp
init|=
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmApp
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Consider non-existing app "
operator|+
name|appId
operator|+
literal|" as completed"
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|rmApp
operator|.
name|getState
argument_list|()
operator|==
name|RMAppState
operator|.
name|FINISHED
operator|||
name|rmApp
operator|.
name|getState
argument_list|()
operator|==
name|RMAppState
operator|.
name|FAILED
operator|||
name|rmApp
operator|.
name|getState
argument_list|()
operator|==
name|RMAppState
operator|.
name|KILLED
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Remove "
operator|+
name|rmApp
operator|.
name|getState
argument_list|()
operator|+
literal|" app "
operator|+
name|appId
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Time in second to be decommissioned.
DECL|method|getTimeoutInSec (DecommissioningNodeContext context)
specifier|private
name|int
name|getTimeoutInSec
parameter_list|(
name|DecommissioningNodeContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|context
operator|.
name|nodeState
operator|==
name|NodeState
operator|.
name|DECOMMISSIONED
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|nodeState
operator|!=
name|NodeState
operator|.
name|DECOMMISSIONING
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|context
operator|.
name|appIds
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|context
operator|.
name|numActiveContainers
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// negative timeout value means no timeout (infinite timeout).
if|if
condition|(
name|context
operator|.
name|timeoutMs
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|long
name|now
init|=
name|mclock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|long
name|timeout
init|=
name|context
operator|.
name|decommissioningStartTime
operator|+
name|context
operator|.
name|timeoutMs
operator|-
name|now
decl_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
call|(
name|int
call|)
argument_list|(
name|timeout
operator|/
literal|1000
argument_list|)
argument_list|)
return|;
block|}
DECL|method|logDecommissioningNodesStatus ()
specifier|private
name|void
name|logDecommissioningNodesStatus
parameter_list|()
block|{
if|if
condition|(
operator|!
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
operator|||
name|decomNodes
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|mclock
operator|.
name|getTime
argument_list|()
decl_stmt|;
for|for
control|(
name|DecommissioningNodeContext
name|d
range|:
name|decomNodes
operator|.
name|values
argument_list|()
control|)
block|{
name|DecommissioningNodeStatus
name|s
init|=
name|checkDecommissioningStatus
argument_list|(
name|d
operator|.
name|nodeId
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%n  %-34s %4ds fresh:%3ds containers:%2d %14s"
argument_list|,
name|d
operator|.
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|,
operator|(
name|now
operator|-
name|d
operator|.
name|decommissioningStartTime
operator|)
operator|/
literal|1000
argument_list|,
operator|(
name|now
operator|-
name|d
operator|.
name|lastUpdateTime
operator|)
operator|/
literal|1000
argument_list|,
name|d
operator|.
name|numActiveContainers
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|==
name|DecommissioningNodeStatus
operator|.
name|WAIT_APP
operator|||
name|s
operator|==
name|DecommissioningNodeStatus
operator|.
name|WAIT_CONTAINER
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|" timeout:%4ds"
argument_list|,
name|getTimeoutInSec
argument_list|(
name|d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ApplicationId
name|aid
range|:
name|d
operator|.
name|appIds
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n    "
operator|+
name|aid
argument_list|)
expr_stmt|;
name|RMApp
name|rmApp
init|=
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|aid
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmApp
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|" %s %9s %5.2f%% %5ds"
argument_list|,
name|rmApp
operator|.
name|getState
argument_list|()
argument_list|,
operator|(
name|rmApp
operator|.
name|getApplicationType
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|rmApp
operator|.
name|getApplicationType
argument_list|()
argument_list|,
literal|100.0
operator|*
name|rmApp
operator|.
name|getProgress
argument_list|()
argument_list|,
operator|(
name|mclock
operator|.
name|getTime
argument_list|()
operator|-
name|rmApp
operator|.
name|getStartTime
argument_list|()
operator|)
operator|/
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Decommissioning Nodes: "
operator|+
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Read possible new DECOMMISSIONING_TIMEOUT_KEY from yarn-site.xml.
comment|// This enables DecommissioningNodesWatcher to pick up new value
comment|// without ResourceManager restart.
DECL|method|readDecommissioningTimeout (Configuration conf)
specifier|private
name|void
name|readDecommissioningTimeout
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
block|}
name|int
name|v
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultTimeoutMs
operator|!=
literal|1000L
operator|*
name|v
condition|)
block|{
name|defaultTimeoutMs
operator|=
literal|1000L
operator|*
name|v
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Use new decommissioningTimeoutMs: "
operator|+
name|defaultTimeoutMs
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error readDecommissioningTimeout "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

