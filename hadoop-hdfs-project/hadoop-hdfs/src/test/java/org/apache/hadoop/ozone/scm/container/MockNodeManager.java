begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container
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
name|container
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|SCMTestUtils
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
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|SCMNodeStat
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_comment
comment|/**  * Test Helper for testing container Mapping.  */
end_comment

begin_class
DECL|class|MockNodeManager
specifier|public
class|class
name|MockNodeManager
implements|implements
name|NodeManager
block|{
DECL|field|healthyNodes
specifier|private
specifier|final
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|healthyNodes
decl_stmt|;
DECL|field|HEALTHY_NODE_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|HEALTHY_NODE_COUNT
init|=
literal|10
decl_stmt|;
DECL|field|chillmode
specifier|private
name|boolean
name|chillmode
decl_stmt|;
DECL|method|MockNodeManager ()
specifier|public
name|MockNodeManager
parameter_list|()
block|{
name|this
operator|.
name|healthyNodes
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|10
condition|;
name|x
operator|++
control|)
block|{
name|healthyNodes
operator|.
name|add
argument_list|(
name|SCMTestUtils
operator|.
name|getDatanodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|chillmode
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Sets the chill mode value.    * @param chillmode  boolean    */
DECL|method|setChillmode (boolean chillmode)
specifier|public
name|void
name|setChillmode
parameter_list|(
name|boolean
name|chillmode
parameter_list|)
block|{
name|this
operator|.
name|chillmode
operator|=
name|chillmode
expr_stmt|;
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
block|{    }
comment|/**    * Gets all Live Datanodes that is currently communicating with SCM.    *    * @param nodestate - State of the node    * @return List of Datanodes that are Heartbeating SCM.    */
annotation|@
name|Override
DECL|method|getNodes (NODESTATE nodestate)
specifier|public
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getNodes
parameter_list|(
name|NODESTATE
name|nodestate
parameter_list|)
block|{
if|if
condition|(
name|nodestate
operator|==
name|NODESTATE
operator|.
name|HEALTHY
condition|)
block|{
return|return
name|healthyNodes
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Returns the Number of Datanodes that are communicating with SCM.    *    * @param nodestate - State of the node    * @return int -- count    */
annotation|@
name|Override
DECL|method|getNodeCount (NODESTATE nodestate)
specifier|public
name|int
name|getNodeCount
parameter_list|(
name|NODESTATE
name|nodestate
parameter_list|)
block|{
if|if
condition|(
name|nodestate
operator|==
name|NODESTATE
operator|.
name|HEALTHY
condition|)
block|{
return|return
name|HEALTHY_NODE_COUNT
return|;
block|}
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
name|chillmode
return|;
block|}
comment|/**    * Chill mode is the period when node manager waits for a minimum configured    * number of datanodes to report in. This is called chill mode to indicate the    * period before node manager gets into action.    *<p>    * Forcefully exits the chill mode, even if we have not met the minimum    * criteria of the nodes reporting in.    */
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
comment|/**    * Returns the aggregated node stats.    * @return the aggregated node stats.    */
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
comment|/**    * Return a map of nodes to their stats.    * @return a list of individual node stats (live/stale but not dead).    */
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
comment|/**    * Return the node stat of the specified datanode.    * @param datanodeID - datanode ID.    * @return node stat if it is live/stale, null if it is dead or does't exist.    */
annotation|@
name|Override
DECL|method|getNodeStat (DatanodeID datanodeID)
specifier|public
name|SCMNodeStat
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
comment|/**    * Used for testing.    *    * @return true if the HB check is done.    */
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
comment|/**    * Closes this stream and releases any system resources associated with it. If    * the stream is already closed then invoking this method has no effect.    *<p>    *<p> As noted in {@link AutoCloseable#close()}, cases where the close may    * fail require careful attention. It is strongly advised to relinquish the    * underlying resources and to internally<em>mark</em> the {@code Closeable}    * as closed, prior to throwing the {@code IOException}.    *    * @throws IOException if an I/O error occurs    */
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
comment|/**    * When an object implementing interface<code>Runnable</code> is used to    * create a thread, starting the thread causes the object's<code>run</code>    * method to be called in that separately executing thread.    *<p>    * The general contract of the method<code>run</code> is that it may take any    * action whatsoever.    *    * @see Thread#run()    */
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
DECL|method|getVersion (StorageContainerDatanodeProtocolProtos .SCMVersionRequestProto versionRequest)
specifier|public
name|VersionResponse
name|getVersion
parameter_list|(
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionRequestProto
name|versionRequest
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Register the node if the node finds that it is not registered with any    * SCM.    *    * @param datanodeID - Send datanodeID with Node info, but datanode UUID is    * empty. Server returns a datanodeID for the given node.    * @return SCMHeartbeatResponseProto    */
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
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nodeCountMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeManager
operator|.
name|NODESTATE
name|state
range|:
name|NodeManager
operator|.
name|NODESTATE
operator|.
name|values
argument_list|()
control|)
block|{
name|nodeCountMap
operator|.
name|put
argument_list|(
name|state
operator|.
name|toString
argument_list|()
argument_list|,
name|getNodeCount
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeCountMap
return|;
block|}
block|}
end_class

end_unit

