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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineReportsProto
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
name|node
operator|.
name|states
operator|.
name|NodeAlreadyExistsException
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
name|scm
operator|.
name|server
operator|.
name|StorageContainerManager
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
name|VersionInfo
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
name|server
operator|.
name|events
operator|.
name|EventPublisher
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
name|conf
operator|.
name|OzoneConfiguration
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMRegisteredResponseProto
operator|.
name|ErrorCode
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
name|StorageReportProto
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
name|ipc
operator|.
name|Server
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
name|RegisteredCommand
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
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|net
operator|.
name|InetAddress
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
comment|/**  * Maintains information about the Datanodes on SCM side.  *<p>  * Heartbeats under SCM is very simple compared to HDFS heartbeatManager.  *<p>  * The getNode(byState) functions make copy of node maps and then creates a list  * based on that. It should be assumed that these get functions always report  * *stale* information. For example, getting the deadNodeCount followed by  * getNodes(DEAD) could very well produce totally different count. Also  * getNodeCount(HEALTHY) + getNodeCount(DEAD) + getNodeCode(STALE), is not  * guaranteed to add up to the total nodes that we know off. Please treat all  * get functions in this file as a snap-shot of information that is inconsistent  * as soon as you read it.  */
end_comment

begin_class
DECL|class|SCMNodeManager
specifier|public
class|class
name|SCMNodeManager
implements|implements
name|NodeManager
implements|,
name|StorageContainerNodeProtocol
block|{
annotation|@
name|VisibleForTesting
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SCMNodeManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nodeStateManager
specifier|private
specifier|final
name|NodeStateManager
name|nodeStateManager
decl_stmt|;
comment|// Should we maintain aggregated stats? If this is not frequently used, we
comment|// can always calculate it from nodeStats whenever required.
comment|// Aggregated node stats
DECL|field|scmStat
specifier|private
name|SCMNodeStat
name|scmStat
decl_stmt|;
DECL|field|clusterID
specifier|private
specifier|final
name|String
name|clusterID
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|VersionInfo
name|version
decl_stmt|;
DECL|field|commandQueue
specifier|private
specifier|final
name|CommandQueue
name|commandQueue
decl_stmt|;
comment|// Node manager MXBean
DECL|field|nmInfoBean
specifier|private
name|ObjectName
name|nmInfoBean
decl_stmt|;
comment|// Node pool manager.
DECL|field|scmManager
specifier|private
specifier|final
name|StorageContainerManager
name|scmManager
decl_stmt|;
comment|/**    * Constructs SCM machine Manager.    */
DECL|method|SCMNodeManager (OzoneConfiguration conf, String clusterID, StorageContainerManager scmManager, EventPublisher eventPublisher)
specifier|public
name|SCMNodeManager
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|String
name|clusterID
parameter_list|,
name|StorageContainerManager
name|scmManager
parameter_list|,
name|EventPublisher
name|eventPublisher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|nodeStateManager
operator|=
operator|new
name|NodeStateManager
argument_list|(
name|conf
argument_list|,
name|eventPublisher
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmStat
operator|=
operator|new
name|SCMNodeStat
argument_list|()
expr_stmt|;
name|this
operator|.
name|clusterID
operator|=
name|clusterID
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|VersionInfo
operator|.
name|getLatestVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|commandQueue
operator|=
operator|new
name|CommandQueue
argument_list|()
expr_stmt|;
name|this
operator|.
name|scmManager
operator|=
name|scmManager
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Entering startup chill mode."
argument_list|)
expr_stmt|;
name|registerMXBean
argument_list|()
expr_stmt|;
block|}
DECL|method|registerMXBean ()
specifier|private
name|void
name|registerMXBean
parameter_list|()
block|{
name|this
operator|.
name|nmInfoBean
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"SCMNodeManager"
argument_list|,
literal|"SCMNodeManagerInfo"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|unregisterMXBean ()
specifier|private
name|void
name|unregisterMXBean
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|nmInfoBean
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|this
operator|.
name|nmInfoBean
argument_list|)
expr_stmt|;
name|this
operator|.
name|nmInfoBean
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Gets all datanodes that are in a certain state. This function works by    * taking a snapshot of the current collection and then returning the list    * from that collection. This means that real map might have changed by the    * time we return this list.    *    * @return List of Datanodes that are known to SCM in the requested state.    */
annotation|@
name|Override
DECL|method|getNodes (NodeState nodestate)
specifier|public
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|getNodes
parameter_list|(
name|NodeState
name|nodestate
parameter_list|)
block|{
return|return
name|nodeStateManager
operator|.
name|getNodes
argument_list|(
name|nodestate
argument_list|)
return|;
block|}
comment|/**    * Returns all datanodes that are known to SCM.    *    * @return List of DatanodeDetails    */
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
name|nodeStateManager
operator|.
name|getAllNodes
argument_list|()
return|;
block|}
comment|/**    * Returns the Number of Datanodes by State they are in.    *    * @return int -- count    */
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
name|nodeStateManager
operator|.
name|getNodeCount
argument_list|(
name|nodestate
argument_list|)
return|;
block|}
comment|/**    * Returns the node state of a specific node.    *    * @param datanodeDetails - Datanode Details    * @return Healthy/Stale/Dead/Unknown.    */
annotation|@
name|Override
DECL|method|getNodeState (DatanodeDetails datanodeDetails)
specifier|public
name|NodeState
name|getNodeState
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
block|{
try|try
block|{
return|return
name|nodeStateManager
operator|.
name|getNodeState
argument_list|(
name|datanodeDetails
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NodeNotFoundException
name|e
parameter_list|)
block|{
comment|// TODO: should we throw NodeNotFoundException?
return|return
literal|null
return|;
block|}
block|}
DECL|method|updateNodeStat (UUID dnId, NodeReportProto nodeReport)
specifier|private
name|void
name|updateNodeStat
parameter_list|(
name|UUID
name|dnId
parameter_list|,
name|NodeReportProto
name|nodeReport
parameter_list|)
block|{
name|SCMNodeStat
name|stat
decl_stmt|;
try|try
block|{
name|stat
operator|=
name|nodeStateManager
operator|.
name|getNodeStat
argument_list|(
name|dnId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SCM updateNodeStat based on heartbeat from previous "
operator|+
literal|"dead datanode {}"
argument_list|,
name|dnId
argument_list|)
expr_stmt|;
name|stat
operator|=
operator|new
name|SCMNodeStat
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|nodeReport
operator|!=
literal|null
operator|&&
name|nodeReport
operator|.
name|getStorageReportCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|long
name|totalCapacity
init|=
literal|0
decl_stmt|;
name|long
name|totalRemaining
init|=
literal|0
decl_stmt|;
name|long
name|totalScmUsed
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|StorageReportProto
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
name|StorageReportProto
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
name|scmStat
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
name|scmStat
operator|.
name|add
argument_list|(
name|stat
argument_list|)
expr_stmt|;
block|}
name|nodeStateManager
operator|.
name|setNodeStat
argument_list|(
name|dnId
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes this stream and releases any system resources associated with it. If    * the stream is already closed then invoking this method has no effect.    *    * @throws IOException if an I/O error occurs    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|unregisterMXBean
argument_list|()
expr_stmt|;
block|}
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
name|VersionResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVersion
argument_list|(
name|this
operator|.
name|version
operator|.
name|getVersion
argument_list|()
argument_list|)
operator|.
name|addValue
argument_list|(
name|OzoneConsts
operator|.
name|SCM_ID
argument_list|,
name|this
operator|.
name|scmManager
operator|.
name|getScmStorage
argument_list|()
operator|.
name|getScmId
argument_list|()
argument_list|)
operator|.
name|addValue
argument_list|(
name|OzoneConsts
operator|.
name|CLUSTER_ID
argument_list|,
name|this
operator|.
name|scmManager
operator|.
name|getScmStorage
argument_list|()
operator|.
name|getClusterID
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Register the node if the node finds that it is not registered with any    * SCM.    *    * @param datanodeDetails - Send datanodeDetails with Node info.    *                   This function generates and assigns new datanode ID    *                   for the datanode. This allows SCM to be run independent    *                   of Namenode if required.    * @param nodeReport NodeReport.    *    * @return SCMHeartbeatResponseProto    */
annotation|@
name|Override
DECL|method|register ( DatanodeDetails datanodeDetails, NodeReportProto nodeReport, PipelineReportsProto pipelineReportsProto)
specifier|public
name|RegisteredCommand
name|register
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|NodeReportProto
name|nodeReport
parameter_list|,
name|PipelineReportsProto
name|pipelineReportsProto
parameter_list|)
block|{
name|InetAddress
name|dnAddress
init|=
name|Server
operator|.
name|getRemoteIp
argument_list|()
decl_stmt|;
if|if
condition|(
name|dnAddress
operator|!=
literal|null
condition|)
block|{
comment|// Mostly called inside an RPC, update ip and peer hostname
name|datanodeDetails
operator|.
name|setHostName
argument_list|(
name|dnAddress
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|datanodeDetails
operator|.
name|setIpAddress
argument_list|(
name|dnAddress
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|nodeStateManager
operator|.
name|addNode
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
comment|// Updating Node Report, as registration is successful
name|updateNodeStat
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
name|nodeReport
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered Data node : {}"
argument_list|,
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeAlreadyExistsException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Datanode is already registered. Datanode: {}"
argument_list|,
name|datanodeDetails
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|RegisteredCommand
operator|.
name|newBuilder
argument_list|()
operator|.
name|setErrorCode
argument_list|(
name|ErrorCode
operator|.
name|success
argument_list|)
operator|.
name|setDatanodeUUID
argument_list|(
name|datanodeDetails
operator|.
name|getUuidString
argument_list|()
argument_list|)
operator|.
name|setClusterID
argument_list|(
name|this
operator|.
name|clusterID
argument_list|)
operator|.
name|setHostname
argument_list|(
name|datanodeDetails
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|setIpAddress
argument_list|(
name|datanodeDetails
operator|.
name|getIpAddress
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Send heartbeat to indicate the datanode is alive and doing well.    *    * @param datanodeDetails - DatanodeDetailsProto.    * @return SCMheartbeat response.    */
annotation|@
name|Override
DECL|method|processHeartbeat (DatanodeDetails datanodeDetails)
specifier|public
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|processHeartbeat
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|datanodeDetails
argument_list|,
literal|"Heartbeat is missing "
operator|+
literal|"DatanodeDetails."
argument_list|)
expr_stmt|;
try|try
block|{
name|nodeStateManager
operator|.
name|updateLastHeartbeatTime
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"SCM trying to process heartbeat from an "
operator|+
literal|"unregistered node {}. Ignoring the heartbeat."
argument_list|,
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
return|return
name|commandQueue
operator|.
name|getCommand
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isNodeRegistered (DatanodeDetails datanodeDetails)
specifier|public
name|Boolean
name|isNodeRegistered
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
block|{
try|try
block|{
name|nodeStateManager
operator|.
name|getNode
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|NodeNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Process node report.    *    * @param dnUuid    * @param nodeReport    */
annotation|@
name|Override
DECL|method|processNodeReport (DatanodeDetails dnUuid, NodeReportProto nodeReport)
specifier|public
name|void
name|processNodeReport
parameter_list|(
name|DatanodeDetails
name|dnUuid
parameter_list|,
name|NodeReportProto
name|nodeReport
parameter_list|)
block|{
name|this
operator|.
name|updateNodeStat
argument_list|(
name|dnUuid
operator|.
name|getUuid
argument_list|()
argument_list|,
name|nodeReport
argument_list|)
expr_stmt|;
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
operator|new
name|SCMNodeStat
argument_list|(
name|this
operator|.
name|scmStat
argument_list|)
return|;
block|}
comment|/**    * Return a map of node stats.    * @return a map of individual node stats (live/stale but not dead).    */
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
name|nodeStateManager
operator|.
name|getNodeStatsMap
argument_list|()
return|;
block|}
comment|/**    * Return the node stat of the specified datanode.    * @param datanodeDetails - datanode ID.    * @return node stat if it is live/stale, null if it is decommissioned or    * doesn't exist.    */
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
try|try
block|{
return|return
operator|new
name|SCMNodeMetric
argument_list|(
name|nodeStateManager
operator|.
name|getNodeStat
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NodeNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"SCM getNodeStat from a decommissioned or removed datanode {}"
argument_list|,
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
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
name|NodeState
name|state
range|:
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
comment|/**    * Get set of pipelines a datanode is part of.    * @param datanodeDetails - datanodeID    * @return Set of PipelineID    */
annotation|@
name|Override
DECL|method|getPipelines (DatanodeDetails datanodeDetails)
specifier|public
name|Set
argument_list|<
name|PipelineID
argument_list|>
name|getPipelines
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
block|{
return|return
name|nodeStateManager
operator|.
name|getPipelineByDnID
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Add pipeline information in the NodeManager.    * @param pipeline - Pipeline to be added    */
annotation|@
name|Override
DECL|method|addPipeline (Pipeline pipeline)
specifier|public
name|void
name|addPipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
name|nodeStateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove a pipeline information from the NodeManager.    * @param pipeline - Pipeline to be removed    */
annotation|@
name|Override
DECL|method|removePipeline (Pipeline pipeline)
specifier|public
name|void
name|removePipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
name|nodeStateManager
operator|.
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update set of containers available on a datanode.    * @param datanodeDetails - DatanodeID    * @param containerIds - Set of containerIDs    * @throws NodeNotFoundException - if datanode is not known. For new datanode    *                        use addDatanodeInContainerMap call.    */
annotation|@
name|Override
DECL|method|setContainers (DatanodeDetails datanodeDetails, Set<ContainerID> containerIds)
specifier|public
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
block|{
name|nodeStateManager
operator|.
name|setContainers
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
name|containerIds
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return set of containerIDs available on a datanode.    * @param datanodeDetails - DatanodeID    * @return - set of containerIDs    */
annotation|@
name|Override
DECL|method|getContainers (DatanodeDetails datanodeDetails)
specifier|public
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
block|{
return|return
name|nodeStateManager
operator|.
name|getContainers
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
return|;
block|}
comment|// TODO:
comment|// Since datanode commands are added through event queue, onMessage method
comment|// should take care of adding commands to command queue.
comment|// Refactor and remove all the usage of this method and delete this method.
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
name|this
operator|.
name|commandQueue
operator|.
name|addCommand
argument_list|(
name|dnId
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method is called by EventQueue whenever someone adds a new    * DATANODE_COMMAND to the Queue.    *    * @param commandForDatanode DatanodeCommand    * @param ignored publisher    */
annotation|@
name|Override
DECL|method|onMessage (CommandForDatanode commandForDatanode, EventPublisher ignored)
specifier|public
name|void
name|onMessage
parameter_list|(
name|CommandForDatanode
name|commandForDatanode
parameter_list|,
name|EventPublisher
name|ignored
parameter_list|)
block|{
name|addDatanodeCommand
argument_list|(
name|commandForDatanode
operator|.
name|getDatanodeId
argument_list|()
argument_list|,
name|commandForDatanode
operator|.
name|getCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update the node stats and cluster storage stats in this SCM Node Manager.    *    * @param dnUuid datanode uuid.    */
annotation|@
name|Override
comment|// TODO: This should be removed.
DECL|method|processDeadNode (UUID dnUuid)
specifier|public
name|void
name|processDeadNode
parameter_list|(
name|UUID
name|dnUuid
parameter_list|)
block|{
try|try
block|{
name|SCMNodeStat
name|stat
init|=
name|nodeStateManager
operator|.
name|getNodeStat
argument_list|(
name|dnUuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Update stat values as Datanode {} is dead."
argument_list|,
name|dnUuid
argument_list|)
expr_stmt|;
name|scmStat
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
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NodeNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't update stats based on message of dead Datanode {}, it"
operator|+
literal|" doesn't exist or decommissioned already."
argument_list|,
name|dnUuid
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCommandQueue (UUID dnID)
specifier|public
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|getCommandQueue
parameter_list|(
name|UUID
name|dnID
parameter_list|)
block|{
return|return
name|commandQueue
operator|.
name|getCommand
argument_list|(
name|dnID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

