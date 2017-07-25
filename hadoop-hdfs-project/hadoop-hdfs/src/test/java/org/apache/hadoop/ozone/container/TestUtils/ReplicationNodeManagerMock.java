begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.TestUtils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|TestUtils
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
name|base
operator|.
name|Preconditions
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
name|ozone
operator|.
name|protocol
operator|.
name|VersionResponse
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMNodeReport
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionRequestProto
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
name|ozone
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|proto
operator|.
name|OzoneProtos
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * A Node Manager to test replication.  */
end_comment

begin_class
DECL|class|ReplicationNodeManagerMock
specifier|public
class|class
name|ReplicationNodeManagerMock
implements|implements
name|NodeManager
block|{
DECL|field|nodeStateMap
specifier|private
specifier|final
name|Map
argument_list|<
name|DatanodeID
argument_list|,
name|NodeState
argument_list|>
name|nodeStateMap
decl_stmt|;
comment|/**    * A list of Datanodes and current states.    * @param nodeState A node state map.    */
DECL|method|ReplicationNodeManagerMock (Map<DatanodeID, NodeState> nodeState)
specifier|public
name|ReplicationNodeManagerMock
parameter_list|(
name|Map
argument_list|<
name|DatanodeID
argument_list|,
name|NodeState
argument_list|>
name|nodeState
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodeState
argument_list|)
expr_stmt|;
name|nodeStateMap
operator|=
name|nodeState
expr_stmt|;
block|}
comment|/**    * Get the minimum number of nodes to get out of chill mode.    *    * @return int    */
annotation|@
name|Override
DECL|method|getMinimumChillModeNodes ()
specifier|public
name|int
name|getMinimumChillModeNodes
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Reports if we have exited out of chill mode by discovering enough nodes.    *    * @return True if we are out of Node layer chill mode, false otherwise.    */
annotation|@
name|Override
DECL|method|isOutOfNodeChillMode ()
specifier|public
name|boolean
name|isOutOfNodeChillMode
parameter_list|()
block|{
return|return
operator|!
name|nodeStateMap
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * Returns a chill mode status string.    *    * @return String    */
annotation|@
name|Override
DECL|method|getChillModeStatus ()
specifier|public
name|String
name|getChillModeStatus
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Returns the status of manual chill mode flag.    *    * @return true if forceEnterChillMode has been called, false if    * forceExitChillMode or status is not set. eg. clearChillModeFlag.    */
annotation|@
name|Override
DECL|method|isInManualChillMode ()
specifier|public
name|boolean
name|isInManualChillMode
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Get the number of data nodes that in all states.    *    * @return A state to number of nodes that in this state mapping    */
annotation|@
name|Override
DECL|method|getNodeCount ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getNodeCount
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Removes a data node from the management of this Node Manager.    *    * @param node - DataNode.    * @throws UnregisteredNodeException    */
annotation|@
name|Override
DECL|method|removeNode (DatanodeID node)
specifier|public
name|void
name|removeNode
parameter_list|(
name|DatanodeID
name|node
parameter_list|)
throws|throws
name|UnregisteredNodeException
block|{
name|nodeStateMap
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets all Live Datanodes that is currently communicating with SCM.    *    * @param nodestate - State of the node    * @return List of Datanodes that are Heartbeating SCM.    */
annotation|@
name|Override
DECL|method|getNodes (NodeState nodestate)
specifier|public
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getNodes
parameter_list|(
name|NodeState
name|nodestate
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Returns the Number of Datanodes that are communicating with SCM.    *    * @param nodestate - State of the node    * @return int -- count    */
annotation|@
name|Override
DECL|method|getNodeCount (NodeState nodestate)
specifier|public
name|int
name|getNodeCount
parameter_list|(
name|NodeState
name|nodestate
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Get all datanodes known to SCM.    *    * @return List of DatanodeIDs known to SCM.    */
annotation|@
name|Override
DECL|method|getAllNodes ()
specifier|public
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getAllNodes
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Chill mode is the period when node manager waits for a minimum    * configured number of datanodes to report in. This is called chill mode    * to indicate the period before node manager gets into action.    *<p>    * Forcefully exits the chill mode, even if we have not met the minimum    * criteria of the nodes reporting in.    */
annotation|@
name|Override
DECL|method|forceExitChillMode ()
specifier|public
name|void
name|forceExitChillMode
parameter_list|()
block|{    }
comment|/**    * Forcefully enters chill mode, even if all minimum node conditions are met.    */
annotation|@
name|Override
DECL|method|forceEnterChillMode ()
specifier|public
name|void
name|forceEnterChillMode
parameter_list|()
block|{    }
comment|/**    * Clears the manual chill mode flag.    */
annotation|@
name|Override
DECL|method|clearChillModeFlag ()
specifier|public
name|void
name|clearChillModeFlag
parameter_list|()
block|{    }
comment|/**    * Returns the aggregated node stats.    *    * @return the aggregated node stats.    */
annotation|@
name|Override
DECL|method|getStats ()
specifier|public
name|SCMNodeStat
name|getStats
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Return a map of node stats.    *    * @return a map of individual node stats (live/stale but not dead).    */
annotation|@
name|Override
DECL|method|getNodeStats ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SCMNodeStat
argument_list|>
name|getNodeStats
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Return the node stat of the specified datanode.    *    * @param datanodeID - datanode ID.    * @return node stat if it is live/stale, null if it is dead or does't exist.    */
annotation|@
name|Override
DECL|method|getNodeStat (DatanodeID datanodeID)
specifier|public
name|SCMNodeMetric
name|getNodeStat
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Wait for the heartbeat is processed by NodeManager.    *    * @return true if heartbeat has been processed.    */
annotation|@
name|Override
DECL|method|waitForHeartbeatProcessed ()
specifier|public
name|boolean
name|waitForHeartbeatProcessed
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Returns the node state of a specific node.    *    * @param id - DatanodeID    * @return Healthy/Stale/Dead.    */
annotation|@
name|Override
DECL|method|getNodeState (DatanodeID id)
specifier|public
name|NodeState
name|getNodeState
parameter_list|(
name|DatanodeID
name|id
parameter_list|)
block|{
return|return
name|nodeStateMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Closes this stream and releases any system resources associated    * with it. If the stream is already closed then invoking this    * method has no effect.    *<p>    *<p> As noted in {@link AutoCloseable#close()}, cases where the    * close may fail require careful attention. It is strongly advised    * to relinquish the underlying resources and to internally    *<em>mark</em> the {@code Closeable} as closed, prior to throwing    * the {@code IOException}.    *    * @throws IOException if an I/O error occurs    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
comment|/**    * When an object implementing interface<code>Runnable</code> is used    * to create a thread, starting the thread causes the object's    *<code>run</code> method to be called in that separately executing    * thread.    *<p>    * The general contract of the method<code>run</code> is that it may    * take any action whatsoever.    *    * @see Thread#run()    */
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{    }
comment|/**    * Gets the version info from SCM.    *    * @param versionRequest - version Request.    * @return - returns SCM version info and other required information needed by    * datanode.    */
annotation|@
name|Override
DECL|method|getVersion (SCMVersionRequestProto versionRequest)
specifier|public
name|VersionResponse
name|getVersion
parameter_list|(
name|SCMVersionRequestProto
name|versionRequest
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Register the node if the node finds that it is not registered with any SCM.    *    * @param datanodeID - Send datanodeID with Node info, but datanode UUID is    * empty. Server returns a datanodeID for the given node.    * @return SCMHeartbeatResponseProto    */
annotation|@
name|Override
DECL|method|register (DatanodeID datanodeID)
specifier|public
name|SCMCommand
name|register
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Send heartbeat to indicate the datanode is alive and doing well.    *    * @param datanodeID - Datanode ID.    * @param nodeReport - node report.    * @return SCMheartbeat response list    */
annotation|@
name|Override
DECL|method|sendHeartbeat (DatanodeID datanodeID, SCMNodeReport nodeReport)
specifier|public
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|sendHeartbeat
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|,
name|SCMNodeReport
name|nodeReport
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Clears all nodes from the node Manager.    */
DECL|method|clearMap ()
specifier|public
name|void
name|clearMap
parameter_list|()
block|{
name|this
operator|.
name|nodeStateMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds a node to the existing Node manager. This is used only for test    * purposes.    * @param id - DatanodeID    * @param state State you want to put that node to.    */
DECL|method|addNode (DatanodeID id, NodeState state)
specifier|public
name|void
name|addNode
parameter_list|(
name|DatanodeID
name|id
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|nodeStateMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

