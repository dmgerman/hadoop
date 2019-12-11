begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
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
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|util
operator|.
name|Time
operator|.
name|monotonicNow
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|ExecutionException
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|namenode
operator|.
name|NameNode
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
name|namenode
operator|.
name|Namesystem
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
name|ReflectionUtils
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_comment
comment|/**  * Manages decommissioning and maintenance state for DataNodes. A background  * monitor thread periodically checks the status of DataNodes that are  * decommissioning or entering maintenance state.  *<p>  * A DataNode can be decommissioned in a few situations:  *<ul>  *<li>If a DN is dead, it is decommissioned immediately.</li>  *<li>If a DN is alive, it is decommissioned after all of its blocks  * are sufficiently replicated. Merely under-replicated blocks do not  * block decommissioning as long as they are above a replication  * threshold.</li>  *</ul>  * In the second case, the DataNode transitions to a DECOMMISSION_INPROGRESS  * state and is tracked by the monitor thread. The monitor periodically scans  * through the list of insufficiently replicated blocks on these DataNodes to  * determine if they can be DECOMMISSIONED. The monitor also prunes this list  * as blocks become replicated, so monitor scans will become more efficient  * over time.  *<p>  * DECOMMISSION_INPROGRESS nodes that become dead do not progress to  * DECOMMISSIONED until they become live again. This prevents potential  * durability loss for singly-replicated blocks (see HDFS-6791).  *<p>  * DataNodes can also be put under maintenance state for any short duration  * maintenance operations. Unlike decommissioning, blocks are not always  * re-replicated for the DataNodes to enter maintenance state. When the  * blocks are replicated at least dfs.namenode.maintenance.replication.min,  * DataNodes transition to IN_MAINTENANCE state. Otherwise, just like  * decommissioning, DataNodes transition to ENTERING_MAINTENANCE state and  * wait for the blocks to be sufficiently replicated and then transition to  * IN_MAINTENANCE state. The block replication factor is relaxed for a maximum  * of maintenance expiry time. When DataNodes don't transition or join the  * cluster back by expiry time, blocks are re-replicated just as in  * decommissioning case as to avoid read or write performance degradation.  *<p>  * This class depends on the FSNamesystem lock for synchronization.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DatanodeAdminManager
specifier|public
class|class
name|DatanodeAdminManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DatanodeAdminManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|namesystem
specifier|private
specifier|final
name|Namesystem
name|namesystem
decl_stmt|;
DECL|field|blockManager
specifier|private
specifier|final
name|BlockManager
name|blockManager
decl_stmt|;
DECL|field|hbManager
specifier|private
specifier|final
name|HeartbeatManager
name|hbManager
decl_stmt|;
DECL|field|executor
specifier|private
specifier|final
name|ScheduledExecutorService
name|executor
decl_stmt|;
DECL|field|monitor
specifier|private
name|DatanodeAdminMonitorInterface
name|monitor
init|=
literal|null
decl_stmt|;
DECL|method|DatanodeAdminManager (final Namesystem namesystem, final BlockManager blockManager, final HeartbeatManager hbManager)
name|DatanodeAdminManager
parameter_list|(
specifier|final
name|Namesystem
name|namesystem
parameter_list|,
specifier|final
name|BlockManager
name|blockManager
parameter_list|,
specifier|final
name|HeartbeatManager
name|hbManager
parameter_list|)
block|{
name|this
operator|.
name|namesystem
operator|=
name|namesystem
expr_stmt|;
name|this
operator|.
name|blockManager
operator|=
name|blockManager
expr_stmt|;
name|this
operator|.
name|hbManager
operator|=
name|hbManager
expr_stmt|;
name|executor
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"DatanodeAdminMonitor-%d"
argument_list|)
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start the DataNode admin monitor thread.    * @param conf    */
DECL|method|activate (Configuration conf)
name|void
name|activate
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|int
name|intervalSecs
init|=
operator|(
name|int
operator|)
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_INTERVAL_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|intervalSecs
operator|>=
literal|0
argument_list|,
literal|"Cannot set a negative "
operator|+
literal|"value for "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_INTERVAL_KEY
argument_list|)
expr_stmt|;
name|int
name|blocksPerInterval
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_BLOCKS_PER_INTERVAL_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_BLOCKS_PER_INTERVAL_DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|String
name|deprecatedKey
init|=
literal|"dfs.namenode.decommission.nodes.per.interval"
decl_stmt|;
specifier|final
name|String
name|strNodes
init|=
name|conf
operator|.
name|get
argument_list|(
name|deprecatedKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|strNodes
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Deprecated configuration key {} will be ignored."
argument_list|,
name|deprecatedKey
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Please update your configuration to use {} instead."
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_BLOCKS_PER_INTERVAL_KEY
argument_list|)
expr_stmt|;
block|}
name|checkArgument
argument_list|(
name|blocksPerInterval
operator|>
literal|0
argument_list|,
literal|"Must set a positive value for "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_BLOCKS_PER_INTERVAL_KEY
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxConcurrentTrackedNodes
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_MAX_CONCURRENT_TRACKED_NODES
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_MAX_CONCURRENT_TRACKED_NODES_DEFAULT
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|maxConcurrentTrackedNodes
operator|>=
literal|0
argument_list|,
literal|"Cannot set a negative "
operator|+
literal|"value for "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_MAX_CONCURRENT_TRACKED_NODES
argument_list|)
expr_stmt|;
name|Class
name|cls
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cls
operator|=
name|conf
operator|.
name|getClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_MONITOR_CLASS
argument_list|,
name|DatanodeAdminDefaultMonitor
operator|.
name|class
argument_list|)
expr_stmt|;
name|monitor
operator|=
operator|(
name|DatanodeAdminMonitorInterface
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|cls
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|setBlockManager
argument_list|(
name|blockManager
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|setNameSystem
argument_list|(
name|namesystem
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|setDatanodeAdminManager
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to create the Decommission monitor "
operator|+
literal|"from "
operator|+
name|cls
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|executor
operator|.
name|scheduleAtFixedRate
argument_list|(
name|monitor
argument_list|,
name|intervalSecs
argument_list|,
name|intervalSecs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Activating DatanodeAdminManager with interval {} seconds, "
operator|+
literal|"{} max blocks per interval, "
operator|+
literal|"{} max concurrently tracked nodes."
argument_list|,
name|intervalSecs
argument_list|,
name|blocksPerInterval
argument_list|,
name|maxConcurrentTrackedNodes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Stop the admin monitor thread, waiting briefly for it to terminate.    */
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
try|try
block|{
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
comment|/**    * Start decommissioning the specified datanode.    * @param node    */
annotation|@
name|VisibleForTesting
DECL|method|startDecommission (DatanodeDescriptor node)
specifier|public
name|void
name|startDecommission
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|isDecommissionInProgress
argument_list|()
operator|&&
operator|!
name|node
operator|.
name|isDecommissioned
argument_list|()
condition|)
block|{
comment|// Update DN stats maintained by HeartbeatManager
name|hbManager
operator|.
name|startDecommission
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|// hbManager.startDecommission will set dead node to decommissioned.
if|if
condition|(
name|node
operator|.
name|isDecommissionInProgress
argument_list|()
condition|)
block|{
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|node
operator|.
name|getStorageInfos
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting decommission of {} {} with {} blocks"
argument_list|,
name|node
argument_list|,
name|storage
argument_list|,
name|storage
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|node
operator|.
name|getLeavingServiceStatus
argument_list|()
operator|.
name|setStartTime
argument_list|(
name|monotonicNow
argument_list|()
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|startTrackingNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"startDecommission: Node {} in {}, nothing to do."
argument_list|,
name|node
argument_list|,
name|node
operator|.
name|getAdminState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Stop decommissioning the specified datanode.    * @param node    */
annotation|@
name|VisibleForTesting
DECL|method|stopDecommission (DatanodeDescriptor node)
specifier|public
name|void
name|stopDecommission
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isDecommissionInProgress
argument_list|()
operator|||
name|node
operator|.
name|isDecommissioned
argument_list|()
condition|)
block|{
comment|// Update DN stats maintained by HeartbeatManager
name|hbManager
operator|.
name|stopDecommission
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|// extra redundancy blocks will be detected and processed when
comment|// the dead node comes back and send in its full block report.
if|if
condition|(
name|node
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|blockManager
operator|.
name|processExtraRedundancyBlocksOnInService
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|// Remove from tracking in DatanodeAdminManager
name|monitor
operator|.
name|stopTrackingNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"stopDecommission: Node {} in {}, nothing to do."
argument_list|,
name|node
argument_list|,
name|node
operator|.
name|getAdminState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Start maintenance of the specified datanode.    * @param node    */
annotation|@
name|VisibleForTesting
DECL|method|startMaintenance (DatanodeDescriptor node, long maintenanceExpireTimeInMS)
specifier|public
name|void
name|startMaintenance
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|,
name|long
name|maintenanceExpireTimeInMS
parameter_list|)
block|{
comment|// Even if the node is already in maintenance, we still need to adjust
comment|// the expiration time.
name|node
operator|.
name|setMaintenanceExpireTimeInMS
argument_list|(
name|maintenanceExpireTimeInMS
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|node
operator|.
name|isMaintenance
argument_list|()
condition|)
block|{
comment|// Update DN stats maintained by HeartbeatManager
name|hbManager
operator|.
name|startMaintenance
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|// hbManager.startMaintenance will set dead node to IN_MAINTENANCE.
if|if
condition|(
name|node
operator|.
name|isEnteringMaintenance
argument_list|()
condition|)
block|{
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|node
operator|.
name|getStorageInfos
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting maintenance of {} {} with {} blocks"
argument_list|,
name|node
argument_list|,
name|storage
argument_list|,
name|storage
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|node
operator|.
name|getLeavingServiceStatus
argument_list|()
operator|.
name|setStartTime
argument_list|(
name|monotonicNow
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Track the node regardless whether it is ENTERING_MAINTENANCE or
comment|// IN_MAINTENANCE to support maintenance expiration.
name|monitor
operator|.
name|startTrackingNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"startMaintenance: Node {} in {}, nothing to do."
argument_list|,
name|node
argument_list|,
name|node
operator|.
name|getAdminState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Stop maintenance of the specified datanode.    * @param node    */
annotation|@
name|VisibleForTesting
DECL|method|stopMaintenance (DatanodeDescriptor node)
specifier|public
name|void
name|stopMaintenance
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isMaintenance
argument_list|()
condition|)
block|{
comment|// Update DN stats maintained by HeartbeatManager
name|hbManager
operator|.
name|stopMaintenance
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|// extra redundancy blocks will be detected and processed when
comment|// the dead node comes back and send in its full block report.
if|if
condition|(
operator|!
name|node
operator|.
name|isAlive
argument_list|()
condition|)
block|{
comment|// The node became dead when it was in maintenance, at which point
comment|// the replicas weren't removed from block maps.
comment|// When the node leaves maintenance, the replicas should be removed
comment|// from the block maps to trigger the necessary replication to
comment|// maintain the safety property of "# of live replicas + maintenance
comment|// replicas">= the expected redundancy.
name|blockManager
operator|.
name|removeBlocksAssociatedTo
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Even though putting nodes in maintenance node doesn't cause live
comment|// replicas to match expected replication factor, it is still possible
comment|// to have over replicated when the node leaves maintenance node.
comment|// First scenario:
comment|// a. Node became dead when it is at AdminStates.NORMAL, thus
comment|//    block is replicated so that 3 replicas exist on other nodes.
comment|// b. Admins put the dead node into maintenance mode and then
comment|//    have the node rejoin the cluster.
comment|// c. Take the node out of maintenance mode.
comment|// Second scenario:
comment|// a. With replication factor 3, set one replica to maintenance node,
comment|//    thus block has 1 maintenance replica and 2 live replicas.
comment|// b. Change the replication factor to 2. The block will still have
comment|//    1 maintenance replica and 2 live replicas.
comment|// c. Take the node out of maintenance mode.
name|blockManager
operator|.
name|processExtraRedundancyBlocksOnInService
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|// Remove from tracking in DatanodeAdminManager
name|monitor
operator|.
name|stopTrackingNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"stopMaintenance: Node {} in {}, nothing to do."
argument_list|,
name|node
argument_list|,
name|node
operator|.
name|getAdminState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setDecommissioned (DatanodeDescriptor dn)
specifier|protected
name|void
name|setDecommissioned
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
name|dn
operator|.
name|setDecommissioned
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Decommissioning complete for node {}"
argument_list|,
name|dn
argument_list|)
expr_stmt|;
block|}
DECL|method|setInMaintenance (DatanodeDescriptor dn)
specifier|protected
name|void
name|setInMaintenance
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
name|dn
operator|.
name|setInMaintenance
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Node {} has entered maintenance mode."
argument_list|,
name|dn
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks whether a block is sufficiently replicated/stored for    * DECOMMISSION_INPROGRESS or ENTERING_MAINTENANCE datanodes. For replicated    * blocks or striped blocks, full-strength replication or storage is not    * always necessary, hence "sufficient".    * @return true if sufficient, else false.    */
DECL|method|isSufficient (BlockInfo block, BlockCollection bc, NumberReplicas numberReplicas, boolean isDecommission, boolean isMaintenance)
specifier|protected
name|boolean
name|isSufficient
parameter_list|(
name|BlockInfo
name|block
parameter_list|,
name|BlockCollection
name|bc
parameter_list|,
name|NumberReplicas
name|numberReplicas
parameter_list|,
name|boolean
name|isDecommission
parameter_list|,
name|boolean
name|isMaintenance
parameter_list|)
block|{
if|if
condition|(
name|blockManager
operator|.
name|hasEnoughEffectiveReplicas
argument_list|(
name|block
argument_list|,
name|numberReplicas
argument_list|,
literal|0
argument_list|)
condition|)
block|{
comment|// Block has enough replica, skip
name|LOG
operator|.
name|trace
argument_list|(
literal|"Block {} does not need replication."
argument_list|,
name|block
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|final
name|int
name|numExpected
init|=
name|blockManager
operator|.
name|getExpectedLiveRedundancyNum
argument_list|(
name|block
argument_list|,
name|numberReplicas
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numLive
init|=
name|numberReplicas
operator|.
name|liveReplicas
argument_list|()
decl_stmt|;
comment|// Block is under-replicated
name|LOG
operator|.
name|trace
argument_list|(
literal|"Block {} numExpected={}, numLive={}"
argument_list|,
name|block
argument_list|,
name|numExpected
argument_list|,
name|numLive
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDecommission
operator|&&
name|numExpected
operator|>
name|numLive
condition|)
block|{
if|if
condition|(
name|bc
operator|.
name|isUnderConstruction
argument_list|()
operator|&&
name|block
operator|.
name|equals
argument_list|(
name|bc
operator|.
name|getLastBlock
argument_list|()
argument_list|)
condition|)
block|{
comment|// Can decom a UC block as long as there will still be minReplicas
if|if
condition|(
name|blockManager
operator|.
name|hasMinStorage
argument_list|(
name|block
argument_list|,
name|numLive
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"UC block {} sufficiently-replicated since numLive ({}) "
operator|+
literal|">= minR ({})"
argument_list|,
name|block
argument_list|,
name|numLive
argument_list|,
name|blockManager
operator|.
name|getMinStorageNum
argument_list|(
name|block
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"UC block {} insufficiently-replicated since numLive "
operator|+
literal|"({})< minR ({})"
argument_list|,
name|block
argument_list|,
name|numLive
argument_list|,
name|blockManager
operator|.
name|getMinStorageNum
argument_list|(
name|block
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Can decom a non-UC as long as the default replication is met
if|if
condition|(
name|numLive
operator|>=
name|blockManager
operator|.
name|getDefaultStorageNum
argument_list|(
name|block
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
if|if
condition|(
name|isMaintenance
operator|&&
name|numLive
operator|>=
name|blockManager
operator|.
name|getMinReplicationToBeInMaintenance
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|logBlockReplicationInfo (BlockInfo block, BlockCollection bc, DatanodeDescriptor srcNode, NumberReplicas num, Iterable<DatanodeStorageInfo> storages)
specifier|protected
name|void
name|logBlockReplicationInfo
parameter_list|(
name|BlockInfo
name|block
parameter_list|,
name|BlockCollection
name|bc
parameter_list|,
name|DatanodeDescriptor
name|srcNode
parameter_list|,
name|NumberReplicas
name|num
parameter_list|,
name|Iterable
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|storages
parameter_list|)
block|{
if|if
condition|(
operator|!
name|NameNode
operator|.
name|blockStateChangeLog
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|int
name|curReplicas
init|=
name|num
operator|.
name|liveReplicas
argument_list|()
decl_stmt|;
name|int
name|curExpectedRedundancy
init|=
name|blockManager
operator|.
name|getExpectedRedundancyNum
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|StringBuilder
name|nodeList
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|storages
control|)
block|{
specifier|final
name|DatanodeDescriptor
name|node
init|=
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|nodeList
operator|.
name|append
argument_list|(
name|node
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|NameNode
operator|.
name|blockStateChangeLog
operator|.
name|info
argument_list|(
literal|"Block: "
operator|+
name|block
operator|+
literal|", Expected Replicas: "
operator|+
name|curExpectedRedundancy
operator|+
literal|", live replicas: "
operator|+
name|curReplicas
operator|+
literal|", corrupt replicas: "
operator|+
name|num
operator|.
name|corruptReplicas
argument_list|()
operator|+
literal|", decommissioned replicas: "
operator|+
name|num
operator|.
name|decommissioned
argument_list|()
operator|+
literal|", decommissioning replicas: "
operator|+
name|num
operator|.
name|decommissioning
argument_list|()
operator|+
literal|", maintenance replicas: "
operator|+
name|num
operator|.
name|maintenanceReplicas
argument_list|()
operator|+
literal|", live entering maintenance replicas: "
operator|+
name|num
operator|.
name|liveEnteringMaintenanceReplicas
argument_list|()
operator|+
literal|", excess replicas: "
operator|+
name|num
operator|.
name|excessReplicas
argument_list|()
operator|+
literal|", Is Open File: "
operator|+
name|bc
operator|.
name|isUnderConstruction
argument_list|()
operator|+
literal|", Datanodes having this block: "
operator|+
name|nodeList
operator|+
literal|", Current Datanode: "
operator|+
name|srcNode
operator|+
literal|", Is current datanode decommissioning: "
operator|+
name|srcNode
operator|.
name|isDecommissionInProgress
argument_list|()
operator|+
literal|", Is current datanode entering maintenance: "
operator|+
name|srcNode
operator|.
name|isEnteringMaintenance
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumPendingNodes ()
specifier|public
name|int
name|getNumPendingNodes
parameter_list|()
block|{
return|return
name|monitor
operator|.
name|getPendingNodeCount
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumTrackedNodes ()
specifier|public
name|int
name|getNumTrackedNodes
parameter_list|()
block|{
return|return
name|monitor
operator|.
name|getTrackedNodeCount
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumNodesChecked ()
specifier|public
name|int
name|getNumNodesChecked
parameter_list|()
block|{
return|return
name|monitor
operator|.
name|getNumNodesChecked
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPendingNodes ()
specifier|public
name|Queue
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|getPendingNodes
parameter_list|()
block|{
return|return
name|monitor
operator|.
name|getPendingNodes
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|runMonitorForTest ()
name|void
name|runMonitorForTest
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|executor
operator|.
name|submit
argument_list|(
name|monitor
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

