begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.node
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|hdfs
operator|.
name|protocol
operator|.
name|UnregisteredNodeException
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|UnresolvedTopologyException
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

begin_comment
comment|/**  * A node manager supports a simple interface for managing a datanode.  *<p/>  * 1. A datanode registers with the NodeManager.  *<p/>  * 2. If the node is allowed to register, we add that to the nodes that we need  * to keep track of.  *<p/>  * 3. A heartbeat is made by the node at a fixed frequency.  *<p/>  * 4. A node can be in any of these 4 states: {HEALTHY, STALE, DEAD,  * DECOMMISSIONED}  *<p/>  * HEALTHY - It is a datanode that is regularly heartbeating us.  *  * STALE - A datanode for which we have missed few heart beats.  *  * DEAD - A datanode that we have not heard from for a while.  *  * DECOMMISSIONED - Someone told us to remove this node from the tracking  * list, by calling removeNode. We will throw away this nodes info soon.  */
end_comment

begin_interface
DECL|interface|NodeManager
specifier|public
interface|interface
name|NodeManager
extends|extends
name|Closeable
extends|,
name|Runnable
block|{
comment|/**    * Update the heartbeat timestamp.    *    * @param datanodeID - Name of the datanode that send us heatbeat.    */
DECL|method|updateHeartbeat (DatanodeID datanodeID)
name|void
name|updateHeartbeat
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|)
function_decl|;
comment|/**    * Add a New Datanode to the NodeManager.    *    * @param nodeReg - Datanode ID.    * @throws UnresolvedTopologyException    */
DECL|method|registerNode (DatanodeID nodeReg)
name|void
name|registerNode
parameter_list|(
name|DatanodeID
name|nodeReg
parameter_list|)
throws|throws
name|UnresolvedTopologyException
function_decl|;
comment|/**    * Removes a data node from the management of this Node Manager.    *    * @param node - DataNode.    * @throws UnregisteredNodeException    */
DECL|method|removeNode (DatanodeID node)
name|void
name|removeNode
parameter_list|(
name|DatanodeID
name|node
parameter_list|)
throws|throws
name|UnregisteredNodeException
function_decl|;
comment|/**    * Gets all Live Datanodes that is currently communicating with SCM.    *    * @return List of Datanodes that are Heartbeating SCM.    */
DECL|method|getNodes (NODESTATE nodestate)
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getNodes
parameter_list|(
name|NODESTATE
name|nodestate
parameter_list|)
function_decl|;
comment|/**    * Returns the Number of Datanodes that are communicating with SCM.    *    * @return int -- count    */
DECL|method|getNodeCount (NODESTATE nodestate)
name|int
name|getNodeCount
parameter_list|(
name|NODESTATE
name|nodestate
parameter_list|)
function_decl|;
comment|/**    * Get all datanodes known to SCM.    *    * @return List of DatanodeIDs known to SCM.    */
DECL|method|getAllNodes ()
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getAllNodes
parameter_list|()
function_decl|;
comment|/**    * Get the minimum number of nodes to get out of safe mode.    *    * @return int    */
DECL|method|getMinimumSafeModeNodes ()
name|int
name|getMinimumSafeModeNodes
parameter_list|()
function_decl|;
comment|/**    * Reports if we have exited out of safe mode by discovering enough nodes.    *    * @return True if we are out of Node layer safe mode, false otherwise.    */
DECL|method|isOutOfNodeSafeMode ()
name|boolean
name|isOutOfNodeSafeMode
parameter_list|()
function_decl|;
comment|/**    * Enum that represents the Node State. This is used in calls to getNodeList    * and getNodeCount. TODO: Add decommission when we support it.    */
DECL|enum|NODESTATE
enum|enum
name|NODESTATE
block|{
DECL|enumConstant|HEALTHY
name|HEALTHY
block|,
DECL|enumConstant|STALE
name|STALE
block|,
DECL|enumConstant|DEAD
name|DEAD
block|}
block|}
end_interface

end_unit

