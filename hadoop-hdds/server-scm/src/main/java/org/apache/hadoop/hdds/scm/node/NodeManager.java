begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|node
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|ContainerID
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|Pipeline
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|PipelineID
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
operator|.
name|SCMNodeMetric
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
operator|.
name|SCMNodeStat
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
name|hdds
operator|.
name|scm
operator|.
name|node
operator|.
name|states
operator|.
name|NodeNotFoundException
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventHandler
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
name|ozone
operator|.
name|protocol
operator|.
name|StorageContainerNodeProtocol
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|CommandForDatanode
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * A node manager supports a simple interface for managing a datanode.  *<p>  * 1. A datanode registers with the NodeManager.  *<p>  * 2. If the node is allowed to register, we add that to the nodes that we need  * to keep track of.  *<p>  * 3. A heartbeat is made by the node at a fixed frequency.  *<p>  * 4. A node can be in any of these 4 states: {HEALTHY, STALE, DEAD,  * DECOMMISSIONED}  *<p>  * HEALTHY - It is a datanode that is regularly heartbeating us.  *  * STALE - A datanode for which we have missed few heart beats.  *  * DEAD - A datanode that we have not heard from for a while.  *  * DECOMMISSIONED - Someone told us to remove this node from the tracking  * list, by calling removeNode. We will throw away this nodes info soon.  */
end_comment

begin_interface
DECL|interface|NodeManager
specifier|public
interface|interface
name|NodeManager
extends|extends
name|StorageContainerNodeProtocol
extends|,
name|EventHandler
argument_list|<
name|CommandForDatanode
argument_list|>
extends|,
name|NodeManagerMXBean
extends|,
name|Closeable
block|{
comment|/**    * Gets all Live Datanodes that is currently communicating with SCM.    * @param nodeState - State of the node    * @return List of Datanodes that are Heartbeating SCM.    */
DECL|method|getNodes (NodeState nodeState)
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|getNodes
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
function_decl|;
comment|/**    * Returns the Number of Datanodes that are communicating with SCM.    * @param nodeState - State of the node    * @return int -- count    */
DECL|method|getNodeCount (NodeState nodeState)
name|int
name|getNodeCount
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
function_decl|;
comment|/**    * Get all datanodes known to SCM.    *    * @return List of DatanodeDetails known to SCM.    */
DECL|method|getAllNodes ()
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|getAllNodes
parameter_list|()
function_decl|;
comment|/**    * Returns the aggregated node stats.    * @return the aggregated node stats.    */
DECL|method|getStats ()
name|SCMNodeStat
name|getStats
parameter_list|()
function_decl|;
comment|/**    * Return a map of node stats.    * @return a map of individual node stats (live/stale but not dead).    */
DECL|method|getNodeStats ()
name|Map
argument_list|<
name|DatanodeDetails
argument_list|,
name|SCMNodeStat
argument_list|>
name|getNodeStats
parameter_list|()
function_decl|;
comment|/**    * Return the node stat of the specified datanode.    * @param datanodeDetails DatanodeDetails.    * @return node stat if it is live/stale, null if it is decommissioned or    * doesn't exist.    */
DECL|method|getNodeStat (DatanodeDetails datanodeDetails)
name|SCMNodeMetric
name|getNodeStat
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
function_decl|;
comment|/**    * Returns the node state of a specific node.    * @param datanodeDetails DatanodeDetails    * @return Healthy/Stale/Dead.    */
DECL|method|getNodeState (DatanodeDetails datanodeDetails)
name|NodeState
name|getNodeState
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
function_decl|;
comment|/**    * Get set of pipelines a datanode is part of.    * @param datanodeDetails DatanodeDetails    * @return Set of PipelineID    */
DECL|method|getPipelines (DatanodeDetails datanodeDetails)
name|Set
argument_list|<
name|PipelineID
argument_list|>
name|getPipelines
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
function_decl|;
comment|/**    * Add pipeline information in the NodeManager.    * @param pipeline - Pipeline to be added    */
DECL|method|addPipeline (Pipeline pipeline)
name|void
name|addPipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
function_decl|;
comment|/**    * Remove a pipeline information from the NodeManager.    * @param pipeline - Pipeline to be removed    */
DECL|method|removePipeline (Pipeline pipeline)
name|void
name|removePipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
function_decl|;
comment|/**    * Remaps datanode to containers mapping to the new set of containers.    * @param datanodeDetails - DatanodeDetails    * @param containerIds - Set of containerIDs    * @throws NodeNotFoundException - if datanode is not known. For new datanode    *                        use addDatanodeInContainerMap call.    */
DECL|method|setContainers (DatanodeDetails datanodeDetails, Set<ContainerID> containerIds)
name|void
name|setContainers
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containerIds
parameter_list|)
throws|throws
name|NodeNotFoundException
function_decl|;
comment|/**    * Return set of containerIDs available on a datanode.    * @param datanodeDetails DatanodeDetails    * @return set of containerIDs    */
DECL|method|getContainers (DatanodeDetails datanodeDetails)
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|getContainers
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
throws|throws
name|NodeNotFoundException
function_decl|;
comment|/**    * Add a {@link SCMCommand} to the command queue, which are    * handled by HB thread asynchronously.    * @param dnId datanode uuid    * @param command    */
DECL|method|addDatanodeCommand (UUID dnId, SCMCommand command)
name|void
name|addDatanodeCommand
parameter_list|(
name|UUID
name|dnId
parameter_list|,
name|SCMCommand
name|command
parameter_list|)
function_decl|;
comment|/**    * Process node report.    *    * @param datanodeDetails    * @param nodeReport    */
DECL|method|processNodeReport (DatanodeDetails datanodeDetails, NodeReportProto nodeReport)
name|void
name|processNodeReport
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|NodeReportProto
name|nodeReport
parameter_list|)
function_decl|;
comment|/**    * Get list of SCMCommands in the Command Queue for a particular Datanode.    * @param dnID - Datanode uuid.    * @return list of commands    */
comment|// TODO: We can give better name to this method!
DECL|method|getCommandQueue (UUID dnID)
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|getCommandQueue
parameter_list|(
name|UUID
name|dnID
parameter_list|)
function_decl|;
comment|/**    * Given datanode host address, returns the DatanodeDetails for the    * node.    *    * @param address node host address    * @return the given datanode, or null if not found    */
DECL|method|getNode (String address)
name|DatanodeDetails
name|getNode
parameter_list|(
name|String
name|address
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

