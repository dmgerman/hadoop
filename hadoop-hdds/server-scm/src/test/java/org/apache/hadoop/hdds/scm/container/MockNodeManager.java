begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container
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
name|hdds
operator|.
name|scm
operator|.
name|node
operator|.
name|NodePoolManager
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMStorageReport
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
name|OzoneConsts
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
name|assertj
operator|.
name|core
operator|.
name|util
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|hdds
operator|.
name|scm
operator|.
name|TestUtils
operator|.
name|getDatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|NodeState
operator|.
name|DEAD
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|NodeState
operator|.
name|HEALTHY
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|NodeState
operator|.
name|STALE
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
DECL|field|NODES
specifier|private
specifier|final
specifier|static
name|NodeData
index|[]
name|NODES
init|=
block|{
operator|new
name|NodeData
argument_list|(
literal|10L
operator|*
name|OzoneConsts
operator|.
name|TB
argument_list|,
name|OzoneConsts
operator|.
name|GB
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
literal|64L
operator|*
name|OzoneConsts
operator|.
name|TB
argument_list|,
literal|100
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
literal|128L
operator|*
name|OzoneConsts
operator|.
name|TB
argument_list|,
literal|256
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
literal|40L
operator|*
name|OzoneConsts
operator|.
name|TB
argument_list|,
name|OzoneConsts
operator|.
name|TB
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
literal|256L
operator|*
name|OzoneConsts
operator|.
name|TB
argument_list|,
literal|200
operator|*
name|OzoneConsts
operator|.
name|TB
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
literal|20L
operator|*
name|OzoneConsts
operator|.
name|TB
argument_list|,
literal|10
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
literal|32L
operator|*
name|OzoneConsts
operator|.
name|TB
argument_list|,
literal|16
operator|*
name|OzoneConsts
operator|.
name|TB
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
name|OzoneConsts
operator|.
name|TB
argument_list|,
literal|900
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
name|OzoneConsts
operator|.
name|TB
argument_list|,
literal|900
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|,
name|NodeData
operator|.
name|STALE
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
name|OzoneConsts
operator|.
name|TB
argument_list|,
literal|200L
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|,
name|NodeData
operator|.
name|STALE
argument_list|)
block|,
operator|new
name|NodeData
argument_list|(
name|OzoneConsts
operator|.
name|TB
argument_list|,
literal|200L
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|,
name|NodeData
operator|.
name|DEAD
argument_list|)
block|}
decl_stmt|;
DECL|field|healthyNodes
specifier|private
specifier|final
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|healthyNodes
decl_stmt|;
DECL|field|staleNodes
specifier|private
specifier|final
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|staleNodes
decl_stmt|;
DECL|field|deadNodes
specifier|private
specifier|final
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|deadNodes
decl_stmt|;
DECL|field|nodeMetricMap
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|SCMNodeStat
argument_list|>
name|nodeMetricMap
decl_stmt|;
DECL|field|aggregateStat
specifier|private
specifier|final
name|SCMNodeStat
name|aggregateStat
decl_stmt|;
DECL|field|chillmode
specifier|private
name|boolean
name|chillmode
decl_stmt|;
DECL|field|commandMap
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|SCMCommand
argument_list|>
argument_list|>
name|commandMap
decl_stmt|;
DECL|method|MockNodeManager (boolean initializeFakeNodes, int nodeCount)
specifier|public
name|MockNodeManager
parameter_list|(
name|boolean
name|initializeFakeNodes
parameter_list|,
name|int
name|nodeCount
parameter_list|)
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
name|this
operator|.
name|staleNodes
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|deadNodes
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeMetricMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|aggregateStat
operator|=
operator|new
name|SCMNodeStat
argument_list|()
expr_stmt|;
if|if
condition|(
name|initializeFakeNodes
condition|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|nodeCount
condition|;
name|x
operator|++
control|)
block|{
name|DatanodeDetails
name|dd
init|=
name|getDatanodeDetails
argument_list|()
decl_stmt|;
name|populateNodeMetric
argument_list|(
name|dd
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
block|}
name|chillmode
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|commandMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Invoked from ctor to create some node Metrics.    *    * @param datanodeDetails - Datanode details    */
DECL|method|populateNodeMetric (DatanodeDetails datanodeDetails, int x)
specifier|private
name|void
name|populateNodeMetric
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|int
name|x
parameter_list|)
block|{
name|SCMNodeStat
name|newStat
init|=
operator|new
name|SCMNodeStat
argument_list|()
decl_stmt|;
name|long
name|remaining
init|=
name|NODES
index|[
name|x
operator|%
name|NODES
operator|.
name|length
index|]
operator|.
name|capacity
operator|-
name|NODES
index|[
name|x
operator|%
name|NODES
operator|.
name|length
index|]
operator|.
name|used
decl_stmt|;
name|newStat
operator|.
name|set
argument_list|(
operator|(
name|NODES
index|[
name|x
operator|%
name|NODES
operator|.
name|length
index|]
operator|.
name|capacity
operator|)
argument_list|,
operator|(
name|NODES
index|[
name|x
operator|%
name|NODES
operator|.
name|length
index|]
operator|.
name|used
operator|)
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeMetricMap
operator|.
name|put
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
name|newStat
argument_list|)
expr_stmt|;
name|aggregateStat
operator|.
name|add
argument_list|(
name|newStat
argument_list|)
expr_stmt|;
if|if
condition|(
name|NODES
index|[
name|x
operator|%
name|NODES
operator|.
name|length
index|]
operator|.
name|getCurrentState
argument_list|()
operator|==
name|NodeData
operator|.
name|HEALTHY
condition|)
block|{
name|healthyNodes
operator|.
name|add
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|NODES
index|[
name|x
operator|%
name|NODES
operator|.
name|length
index|]
operator|.
name|getCurrentState
argument_list|()
operator|==
name|NodeData
operator|.
name|STALE
condition|)
block|{
name|staleNodes
operator|.
name|add
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|NODES
index|[
name|x
operator|%
name|NODES
operator|.
name|length
index|]
operator|.
name|getCurrentState
argument_list|()
operator|==
name|NodeData
operator|.
name|DEAD
condition|)
block|{
name|deadNodes
operator|.
name|add
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Sets the chill mode value.    * @param chillmode boolean    */
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
DECL|method|removeNode (DatanodeDetails node)
specifier|public
name|void
name|removeNode
parameter_list|(
name|DatanodeDetails
name|node
parameter_list|)
throws|throws
name|UnregisteredNodeException
block|{    }
comment|/**    * Gets all Live Datanodes that is currently communicating with SCM.    *    * @param nodestate - State of the node    * @return List of Datanodes that are Heartbeating SCM.    */
annotation|@
name|Override
DECL|method|getNodes (HddsProtos.NodeState nodestate)
specifier|public
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|getNodes
parameter_list|(
name|HddsProtos
operator|.
name|NodeState
name|nodestate
parameter_list|)
block|{
if|if
condition|(
name|nodestate
operator|==
name|HEALTHY
condition|)
block|{
return|return
name|healthyNodes
return|;
block|}
if|if
condition|(
name|nodestate
operator|==
name|STALE
condition|)
block|{
return|return
name|staleNodes
return|;
block|}
if|if
condition|(
name|nodestate
operator|==
name|DEAD
condition|)
block|{
return|return
name|deadNodes
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Returns the Number of Datanodes that are communicating with SCM.    *    * @param nodestate - State of the node    * @return int -- count    */
annotation|@
name|Override
DECL|method|getNodeCount (HddsProtos.NodeState nodestate)
specifier|public
name|int
name|getNodeCount
parameter_list|(
name|HddsProtos
operator|.
name|NodeState
name|nodestate
parameter_list|)
block|{
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|nodes
init|=
name|getNodes
argument_list|(
name|nodestate
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
condition|)
block|{
return|return
name|nodes
operator|.
name|size
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
comment|/**    * Get all datanodes known to SCM.    *    * @return List of DatanodeDetails known to SCM.    */
annotation|@
name|Override
DECL|method|getAllNodes ()
specifier|public
name|List
argument_list|<
name|DatanodeDetails
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
comment|/**    * Chill mode is the period when node manager waits for a minimum configured    * number of datanodes to report in. This is called chill mode to indicate the    * period before node manager gets into action.    *<p>    * Forcefully exits the chill mode, even if we have not met the minimum    * criteria of the nodes reporting in.    */
annotation|@
name|Override
DECL|method|forceExitChillMode ()
specifier|public
name|void
name|forceExitChillMode
parameter_list|()
block|{    }
comment|/**    * Puts the node manager into manual chill mode.    */
annotation|@
name|Override
DECL|method|enterChillMode ()
specifier|public
name|void
name|enterChillMode
parameter_list|()
block|{    }
comment|/**    * Brings node manager out of manual chill mode.    */
annotation|@
name|Override
DECL|method|exitChillMode ()
specifier|public
name|void
name|exitChillMode
parameter_list|()
block|{    }
comment|/**    * Returns true if node manager is out of chill mode, else false.    * @return true if out of chill mode, else false    */
annotation|@
name|Override
DECL|method|isOutOfChillMode ()
specifier|public
name|boolean
name|isOutOfChillMode
parameter_list|()
block|{
return|return
operator|!
name|chillmode
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
name|aggregateStat
return|;
block|}
comment|/**    * Return a map of nodes to their stats.    * @return a list of individual node stats (live/stale but not dead).    */
annotation|@
name|Override
DECL|method|getNodeStats ()
specifier|public
name|Map
argument_list|<
name|UUID
argument_list|,
name|SCMNodeStat
argument_list|>
name|getNodeStats
parameter_list|()
block|{
return|return
name|nodeMetricMap
return|;
block|}
comment|/**    * Return the node stat of the specified datanode.    * @param datanodeDetails - datanode details.    * @return node stat if it is live/stale, null if it is dead or does't exist.    */
annotation|@
name|Override
DECL|method|getNodeStat (DatanodeDetails datanodeDetails)
specifier|public
name|SCMNodeMetric
name|getNodeStat
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
block|{
return|return
operator|new
name|SCMNodeMetric
argument_list|(
name|nodeMetricMap
operator|.
name|get
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNodePoolManager ()
specifier|public
name|NodePoolManager
name|getNodePoolManager
parameter_list|()
block|{
return|return
name|Mockito
operator|.
name|mock
argument_list|(
name|NodePoolManager
operator|.
name|class
argument_list|)
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
comment|/**    * Returns the node state of a specific node.    *    * @param dd - DatanodeDetails    * @return Healthy/Stale/Dead.    */
annotation|@
name|Override
DECL|method|getNodeState (DatanodeDetails dd)
specifier|public
name|HddsProtos
operator|.
name|NodeState
name|getNodeState
parameter_list|(
name|DatanodeDetails
name|dd
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|addDatanodeCommand (UUID dnId, SCMCommand command)
specifier|public
name|void
name|addDatanodeCommand
parameter_list|(
name|UUID
name|dnId
parameter_list|,
name|SCMCommand
name|command
parameter_list|)
block|{
if|if
condition|(
name|commandMap
operator|.
name|containsKey
argument_list|(
name|dnId
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|commandList
init|=
name|commandMap
operator|.
name|get
argument_list|(
name|dnId
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|commandList
argument_list|)
expr_stmt|;
name|commandList
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|commandList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|commandList
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|commandMap
operator|.
name|put
argument_list|(
name|dnId
argument_list|,
name|commandList
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Returns the number of commands that is queued to this node manager.
DECL|method|getCommandCount (DatanodeDetails dd)
specifier|public
name|int
name|getCommandCount
parameter_list|(
name|DatanodeDetails
name|dd
parameter_list|)
block|{
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|list
init|=
name|commandMap
operator|.
name|get
argument_list|(
name|dd
operator|.
name|getUuid
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|list
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|list
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|clearCommandQueue (UUID dnId)
specifier|public
name|void
name|clearCommandQueue
parameter_list|(
name|UUID
name|dnId
parameter_list|)
block|{
if|if
condition|(
name|commandMap
operator|.
name|containsKey
argument_list|(
name|dnId
argument_list|)
condition|)
block|{
name|commandMap
operator|.
name|put
argument_list|(
name|dnId
argument_list|,
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Register the node if the node finds that it is not registered with any    * SCM.    *    * @param datanodeDetails DatanodeDetailsProto    * @param nodeReport SCMNodeReport    * @return SCMHeartbeatResponseProto    */
annotation|@
name|Override
DECL|method|register (HddsProtos.DatanodeDetailsProto datanodeDetails, SCMNodeReport nodeReport)
specifier|public
name|SCMCommand
name|register
parameter_list|(
name|HddsProtos
operator|.
name|DatanodeDetailsProto
name|datanodeDetails
parameter_list|,
name|SCMNodeReport
name|nodeReport
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Send heartbeat to indicate the datanode is alive and doing well.    *    * @param datanodeDetails - Datanode ID.    * @param nodeReport - node report.    * @return SCMheartbeat response list    */
annotation|@
name|Override
DECL|method|sendHeartbeat ( HddsProtos.DatanodeDetailsProto datanodeDetails, SCMNodeReport nodeReport)
specifier|public
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|sendHeartbeat
parameter_list|(
name|HddsProtos
operator|.
name|DatanodeDetailsProto
name|datanodeDetails
parameter_list|,
name|SCMNodeReport
name|nodeReport
parameter_list|)
block|{
if|if
condition|(
operator|(
name|datanodeDetails
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|nodeReport
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|nodeReport
operator|.
name|getStorageReportCount
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|SCMNodeStat
name|stat
init|=
name|this
operator|.
name|nodeMetricMap
operator|.
name|get
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|totalCapacity
init|=
literal|0L
decl_stmt|;
name|long
name|totalRemaining
init|=
literal|0L
decl_stmt|;
name|long
name|totalScmUsed
init|=
literal|0L
decl_stmt|;
name|List
argument_list|<
name|SCMStorageReport
argument_list|>
name|storageReports
init|=
name|nodeReport
operator|.
name|getStorageReportList
argument_list|()
decl_stmt|;
for|for
control|(
name|SCMStorageReport
name|report
range|:
name|storageReports
control|)
block|{
name|totalCapacity
operator|+=
name|report
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|totalRemaining
operator|+=
name|report
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
name|totalScmUsed
operator|+=
name|report
operator|.
name|getScmUsed
argument_list|()
expr_stmt|;
block|}
name|aggregateStat
operator|.
name|subtract
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|stat
operator|.
name|set
argument_list|(
name|totalCapacity
argument_list|,
name|totalScmUsed
argument_list|,
name|totalRemaining
argument_list|)
expr_stmt|;
name|aggregateStat
operator|.
name|add
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|nodeMetricMap
operator|.
name|put
argument_list|(
name|DatanodeDetails
operator|.
name|getFromProtoBuf
argument_list|(
name|datanodeDetails
argument_list|)
operator|.
name|getUuid
argument_list|()
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
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
name|HddsProtos
operator|.
name|NodeState
name|state
range|:
name|HddsProtos
operator|.
name|NodeState
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
comment|/**    * Makes it easy to add a container.    *    * @param datanodeDetails datanode details    * @param size number of bytes.    */
DECL|method|addContainer (DatanodeDetails datanodeDetails, long size)
specifier|public
name|void
name|addContainer
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|SCMNodeStat
name|stat
init|=
name|this
operator|.
name|nodeMetricMap
operator|.
name|get
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|!=
literal|null
condition|)
block|{
name|aggregateStat
operator|.
name|subtract
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|stat
operator|.
name|getCapacity
argument_list|()
operator|.
name|add
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|aggregateStat
operator|.
name|add
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|nodeMetricMap
operator|.
name|put
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Makes it easy to simulate a delete of a container.    *    * @param datanodeDetails datanode Details    * @param size number of bytes.    */
DECL|method|delContainer (DatanodeDetails datanodeDetails, long size)
specifier|public
name|void
name|delContainer
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|SCMNodeStat
name|stat
init|=
name|this
operator|.
name|nodeMetricMap
operator|.
name|get
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|!=
literal|null
condition|)
block|{
name|aggregateStat
operator|.
name|subtract
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|stat
operator|.
name|getCapacity
argument_list|()
operator|.
name|subtract
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|aggregateStat
operator|.
name|add
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|nodeMetricMap
operator|.
name|put
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * A class to declare some values for the nodes so that our tests    * won't fail.    */
DECL|class|NodeData
specifier|private
specifier|static
class|class
name|NodeData
block|{
DECL|field|HEALTHY
specifier|public
specifier|static
specifier|final
name|long
name|HEALTHY
init|=
literal|1
decl_stmt|;
DECL|field|STALE
specifier|public
specifier|static
specifier|final
name|long
name|STALE
init|=
literal|2
decl_stmt|;
DECL|field|DEAD
specifier|public
specifier|static
specifier|final
name|long
name|DEAD
init|=
literal|3
decl_stmt|;
DECL|field|capacity
specifier|private
name|long
name|capacity
decl_stmt|;
DECL|field|used
specifier|private
name|long
name|used
decl_stmt|;
DECL|field|currentState
specifier|private
name|long
name|currentState
decl_stmt|;
comment|/**      * By default nodes are healthy.      * @param capacity      * @param used      */
DECL|method|NodeData (long capacity, long used)
name|NodeData
parameter_list|(
name|long
name|capacity
parameter_list|,
name|long
name|used
parameter_list|)
block|{
name|this
argument_list|(
name|capacity
argument_list|,
name|used
argument_list|,
name|HEALTHY
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a nodeDefinition.      *      * @param capacity capacity.      * @param used used.      * @param currentState - Healthy, Stale and DEAD nodes.      */
DECL|method|NodeData (long capacity, long used, long currentState)
name|NodeData
parameter_list|(
name|long
name|capacity
parameter_list|,
name|long
name|used
parameter_list|,
name|long
name|currentState
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|used
operator|=
name|used
expr_stmt|;
name|this
operator|.
name|currentState
operator|=
name|currentState
expr_stmt|;
block|}
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
DECL|method|setCapacity (long capacity)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|long
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
block|}
DECL|method|getUsed ()
specifier|public
name|long
name|getUsed
parameter_list|()
block|{
return|return
name|used
return|;
block|}
DECL|method|setUsed (long used)
specifier|public
name|void
name|setUsed
parameter_list|(
name|long
name|used
parameter_list|)
block|{
name|this
operator|.
name|used
operator|=
name|used
expr_stmt|;
block|}
DECL|method|getCurrentState ()
specifier|public
name|long
name|getCurrentState
parameter_list|()
block|{
return|return
name|currentState
return|;
block|}
DECL|method|setCurrentState (long currentState)
specifier|public
name|void
name|setCurrentState
parameter_list|(
name|long
name|currentState
parameter_list|)
block|{
name|this
operator|.
name|currentState
operator|=
name|currentState
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

