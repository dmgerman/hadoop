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
name|List
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
name|DFSUtil
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|StorageReport
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
name|protocol
operator|.
name|VolumeFailureSummary
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
name|Daemon
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
name|Time
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

begin_comment
comment|/**  * Manage the heartbeats received from datanodes.  * The datanode list and statistics are synchronized  * by the heartbeat manager lock.  */
end_comment

begin_class
DECL|class|HeartbeatManager
class|class
name|HeartbeatManager
implements|implements
name|DatanodeStatistics
block|{
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
name|HeartbeatManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Stores a subset of the datanodeMap in DatanodeManager,    * containing nodes that are considered alive.    * The HeartbeatMonitor periodically checks for out-dated entries,    * and removes them from the list.    * It is synchronized by the heartbeat manager lock.    */
DECL|field|datanodes
specifier|private
specifier|final
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|datanodes
init|=
operator|new
name|ArrayList
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Statistics, which are synchronized by the heartbeat manager lock. */
DECL|field|stats
specifier|private
specifier|final
name|Stats
name|stats
init|=
operator|new
name|Stats
argument_list|()
decl_stmt|;
comment|/** The time period to check for expired datanodes */
DECL|field|heartbeatRecheckInterval
specifier|private
specifier|final
name|long
name|heartbeatRecheckInterval
decl_stmt|;
comment|/** Heartbeat monitor thread */
DECL|field|heartbeatThread
specifier|private
specifier|final
name|Daemon
name|heartbeatThread
init|=
operator|new
name|Daemon
argument_list|(
operator|new
name|Monitor
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|namesystem
specifier|final
name|Namesystem
name|namesystem
decl_stmt|;
DECL|field|blockManager
specifier|final
name|BlockManager
name|blockManager
decl_stmt|;
DECL|method|HeartbeatManager (final Namesystem namesystem, final BlockManager blockManager, final Configuration conf)
name|HeartbeatManager
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
name|Configuration
name|conf
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
name|boolean
name|avoidStaleDataNodesForWrite
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AVOID_STALE_DATANODE_FOR_WRITE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AVOID_STALE_DATANODE_FOR_WRITE_DEFAULT
argument_list|)
decl_stmt|;
name|long
name|recheckInterval
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_DEFAULT
argument_list|)
decl_stmt|;
comment|// 5 min
name|long
name|staleInterval
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STALE_DATANODE_INTERVAL_DEFAULT
argument_list|)
decl_stmt|;
comment|// 30s
if|if
condition|(
name|avoidStaleDataNodesForWrite
operator|&&
name|staleInterval
operator|<
name|recheckInterval
condition|)
block|{
name|this
operator|.
name|heartbeatRecheckInterval
operator|=
name|staleInterval
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting heartbeat recheck interval to "
operator|+
name|staleInterval
operator|+
literal|" since "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY
operator|+
literal|" is less than "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|heartbeatRecheckInterval
operator|=
name|recheckInterval
expr_stmt|;
block|}
block|}
DECL|method|activate (Configuration conf)
name|void
name|activate
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|heartbeatThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
name|heartbeatThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
comment|// This will no effect if the thread hasn't yet been started.
name|heartbeatThread
operator|.
name|join
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{     }
block|}
DECL|method|getLiveDatanodeCount ()
specifier|synchronized
name|int
name|getLiveDatanodeCount
parameter_list|()
block|{
return|return
name|datanodes
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacityTotal ()
specifier|public
specifier|synchronized
name|long
name|getCapacityTotal
parameter_list|()
block|{
return|return
name|stats
operator|.
name|capacityTotal
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacityUsed ()
specifier|public
specifier|synchronized
name|long
name|getCapacityUsed
parameter_list|()
block|{
return|return
name|stats
operator|.
name|capacityUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacityUsedPercent ()
specifier|public
specifier|synchronized
name|float
name|getCapacityUsedPercent
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|getPercentUsed
argument_list|(
name|stats
operator|.
name|capacityUsed
argument_list|,
name|stats
operator|.
name|capacityTotal
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacityRemaining ()
specifier|public
specifier|synchronized
name|long
name|getCapacityRemaining
parameter_list|()
block|{
return|return
name|stats
operator|.
name|capacityRemaining
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacityRemainingPercent ()
specifier|public
specifier|synchronized
name|float
name|getCapacityRemainingPercent
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|getPercentRemaining
argument_list|(
name|stats
operator|.
name|capacityRemaining
argument_list|,
name|stats
operator|.
name|capacityTotal
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockPoolUsed ()
specifier|public
specifier|synchronized
name|long
name|getBlockPoolUsed
parameter_list|()
block|{
return|return
name|stats
operator|.
name|blockPoolUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getPercentBlockPoolUsed ()
specifier|public
specifier|synchronized
name|float
name|getPercentBlockPoolUsed
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|getPercentUsed
argument_list|(
name|stats
operator|.
name|blockPoolUsed
argument_list|,
name|stats
operator|.
name|capacityTotal
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacityUsedNonDFS ()
specifier|public
specifier|synchronized
name|long
name|getCapacityUsedNonDFS
parameter_list|()
block|{
specifier|final
name|long
name|nonDFSUsed
init|=
name|stats
operator|.
name|capacityTotal
operator|-
name|stats
operator|.
name|capacityRemaining
operator|-
name|stats
operator|.
name|capacityUsed
decl_stmt|;
return|return
name|nonDFSUsed
operator|<
literal|0L
condition|?
literal|0L
else|:
name|nonDFSUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getXceiverCount ()
specifier|public
specifier|synchronized
name|int
name|getXceiverCount
parameter_list|()
block|{
return|return
name|stats
operator|.
name|xceiverCount
return|;
block|}
annotation|@
name|Override
DECL|method|getInServiceXceiverCount ()
specifier|public
specifier|synchronized
name|int
name|getInServiceXceiverCount
parameter_list|()
block|{
return|return
name|stats
operator|.
name|nodesInServiceXceiverCount
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDatanodesInService ()
specifier|public
specifier|synchronized
name|int
name|getNumDatanodesInService
parameter_list|()
block|{
return|return
name|stats
operator|.
name|nodesInService
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheCapacity ()
specifier|public
specifier|synchronized
name|long
name|getCacheCapacity
parameter_list|()
block|{
return|return
name|stats
operator|.
name|cacheCapacity
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheUsed ()
specifier|public
specifier|synchronized
name|long
name|getCacheUsed
parameter_list|()
block|{
return|return
name|stats
operator|.
name|cacheUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getStats ()
specifier|public
specifier|synchronized
name|long
index|[]
name|getStats
parameter_list|()
block|{
return|return
operator|new
name|long
index|[]
block|{
name|getCapacityTotal
argument_list|()
block|,
name|getCapacityUsed
argument_list|()
block|,
name|getCapacityRemaining
argument_list|()
block|,
operator|-
literal|1L
block|,
operator|-
literal|1L
block|,
operator|-
literal|1L
block|,
operator|-
literal|1L
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getExpiredHeartbeats ()
specifier|public
specifier|synchronized
name|int
name|getExpiredHeartbeats
parameter_list|()
block|{
return|return
name|stats
operator|.
name|expiredHeartbeats
return|;
block|}
DECL|method|register (final DatanodeDescriptor d)
specifier|synchronized
name|void
name|register
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|d
parameter_list|)
block|{
if|if
condition|(
operator|!
name|d
operator|.
name|isAlive
condition|)
block|{
name|addDatanode
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|//update its timestamp
name|d
operator|.
name|updateHeartbeatState
argument_list|(
name|StorageReport
operator|.
name|EMPTY_ARRAY
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDatanodes ()
specifier|synchronized
name|DatanodeDescriptor
index|[]
name|getDatanodes
parameter_list|()
block|{
return|return
name|datanodes
operator|.
name|toArray
argument_list|(
operator|new
name|DatanodeDescriptor
index|[
name|datanodes
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|addDatanode (final DatanodeDescriptor d)
specifier|synchronized
name|void
name|addDatanode
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|d
parameter_list|)
block|{
comment|// update in-service node count
name|stats
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|datanodes
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|.
name|isAlive
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|removeDatanode (DatanodeDescriptor node)
specifier|synchronized
name|void
name|removeDatanode
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isAlive
condition|)
block|{
name|stats
operator|.
name|subtract
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|datanodes
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|isAlive
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|updateHeartbeat (final DatanodeDescriptor node, StorageReport[] reports, long cacheCapacity, long cacheUsed, int xceiverCount, int failedVolumes, VolumeFailureSummary volumeFailureSummary)
specifier|synchronized
name|void
name|updateHeartbeat
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|node
parameter_list|,
name|StorageReport
index|[]
name|reports
parameter_list|,
name|long
name|cacheCapacity
parameter_list|,
name|long
name|cacheUsed
parameter_list|,
name|int
name|xceiverCount
parameter_list|,
name|int
name|failedVolumes
parameter_list|,
name|VolumeFailureSummary
name|volumeFailureSummary
parameter_list|)
block|{
name|stats
operator|.
name|subtract
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|updateHeartbeat
argument_list|(
name|reports
argument_list|,
name|cacheCapacity
argument_list|,
name|cacheUsed
argument_list|,
name|xceiverCount
argument_list|,
name|failedVolumes
argument_list|,
name|volumeFailureSummary
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
DECL|method|startDecommission (final DatanodeDescriptor node)
specifier|synchronized
name|void
name|startDecommission
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|isAlive
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Dead node {} is decommissioned immediately."
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|setDecommissioned
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|stats
operator|.
name|subtract
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|startDecommission
argument_list|()
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stopDecommission (final DatanodeDescriptor node)
specifier|synchronized
name|void
name|stopDecommission
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping decommissioning of {} node {}"
argument_list|,
name|node
operator|.
name|isAlive
condition|?
literal|"live"
else|:
literal|"dead"
argument_list|,
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|node
operator|.
name|isAlive
condition|)
block|{
name|node
operator|.
name|stopDecommission
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|stats
operator|.
name|subtract
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|stopDecommission
argument_list|()
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Check if there are any expired heartbeats, and if so,    * whether any blocks have to be re-replicated.    * While removing dead datanodes, make sure that only one datanode is marked    * dead at a time within the synchronized section. Otherwise, a cascading    * effect causes more datanodes to be declared dead.    * Check if there are any failed storage and if so,    * Remove all the blocks on the storage. It also covers the following less    * common scenarios. After DatanodeStorage is marked FAILED, it is still    * possible to receive IBR for this storage.    * 1) DN could deliver IBR for failed storage due to its implementation.    *    a) DN queues a pending IBR request.    *    b) The storage of the block fails.    *    c) DN first sends HB, NN will mark the storage FAILED.    *    d) DN then sends the pending IBR request.    * 2) SBN processes block request from pendingDNMessages.    *    It is possible to have messages in pendingDNMessages that refer    *    to some failed storage.    *    a) SBN receives a IBR and put it in pendingDNMessages.    *    b) The storage of the block fails.    *    c) Edit log replay get the IBR from pendingDNMessages.    * Alternatively, we can resolve these scenarios with the following approaches.    * A. Make sure DN don't deliver IBR for failed storage.    * B. Remove all blocks in PendingDataNodeMessages for the failed storage    *    when we remove all blocks from BlocksMap for that storage.    */
DECL|method|heartbeatCheck ()
name|void
name|heartbeatCheck
parameter_list|()
block|{
specifier|final
name|DatanodeManager
name|dm
init|=
name|blockManager
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
comment|// It's OK to check safe mode w/o taking the lock here, we re-check
comment|// for safe mode after taking the lock before removing a datanode.
if|if
condition|(
name|namesystem
operator|.
name|isInStartupSafeMode
argument_list|()
condition|)
block|{
return|return;
block|}
name|boolean
name|allAlive
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|allAlive
condition|)
block|{
comment|// locate the first dead node.
name|DatanodeID
name|dead
init|=
literal|null
decl_stmt|;
comment|// locate the first failed storage that isn't on a dead node.
name|DatanodeStorageInfo
name|failedStorage
init|=
literal|null
decl_stmt|;
comment|// check the number of stale nodes
name|int
name|numOfStaleNodes
init|=
literal|0
decl_stmt|;
name|int
name|numOfStaleStorages
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|DatanodeDescriptor
name|d
range|:
name|datanodes
control|)
block|{
if|if
condition|(
name|dead
operator|==
literal|null
operator|&&
name|dm
operator|.
name|isDatanodeDead
argument_list|(
name|d
argument_list|)
condition|)
block|{
name|stats
operator|.
name|incrExpiredHeartbeats
argument_list|()
expr_stmt|;
name|dead
operator|=
name|d
expr_stmt|;
block|}
if|if
condition|(
name|d
operator|.
name|isStale
argument_list|(
name|dm
operator|.
name|getStaleInterval
argument_list|()
argument_list|)
condition|)
block|{
name|numOfStaleNodes
operator|++
expr_stmt|;
block|}
name|DatanodeStorageInfo
index|[]
name|storageInfos
init|=
name|d
operator|.
name|getStorageInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storageInfo
range|:
name|storageInfos
control|)
block|{
if|if
condition|(
name|storageInfo
operator|.
name|areBlockContentsStale
argument_list|()
condition|)
block|{
name|numOfStaleStorages
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|failedStorage
operator|==
literal|null
operator|&&
name|storageInfo
operator|.
name|areBlocksOnFailedStorage
argument_list|()
operator|&&
name|d
operator|!=
name|dead
condition|)
block|{
name|failedStorage
operator|=
name|storageInfo
expr_stmt|;
block|}
block|}
block|}
comment|// Set the number of stale nodes in the DatanodeManager
name|dm
operator|.
name|setNumStaleNodes
argument_list|(
name|numOfStaleNodes
argument_list|)
expr_stmt|;
name|dm
operator|.
name|setNumStaleStorages
argument_list|(
name|numOfStaleStorages
argument_list|)
expr_stmt|;
block|}
name|allAlive
operator|=
name|dead
operator|==
literal|null
operator|&&
name|failedStorage
operator|==
literal|null
expr_stmt|;
if|if
condition|(
name|dead
operator|!=
literal|null
condition|)
block|{
comment|// acquire the fsnamesystem lock, and then remove the dead node.
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|namesystem
operator|.
name|isInStartupSafeMode
argument_list|()
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|dm
operator|.
name|removeDeadDatanode
argument_list|(
name|dead
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|failedStorage
operator|!=
literal|null
condition|)
block|{
comment|// acquire the fsnamesystem lock, and remove blocks on the storage.
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|namesystem
operator|.
name|isInStartupSafeMode
argument_list|()
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|blockManager
operator|.
name|removeBlocksAssociatedTo
argument_list|(
name|failedStorage
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Periodically check heartbeat and update block key */
DECL|class|Monitor
specifier|private
class|class
name|Monitor
implements|implements
name|Runnable
block|{
DECL|field|lastHeartbeatCheck
specifier|private
name|long
name|lastHeartbeatCheck
decl_stmt|;
DECL|field|lastBlockKeyUpdate
specifier|private
name|long
name|lastBlockKeyUpdate
decl_stmt|;
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
name|namesystem
operator|.
name|isRunning
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|long
name|now
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastHeartbeatCheck
operator|+
name|heartbeatRecheckInterval
operator|<
name|now
condition|)
block|{
name|heartbeatCheck
argument_list|()
expr_stmt|;
name|lastHeartbeatCheck
operator|=
name|now
expr_stmt|;
block|}
if|if
condition|(
name|blockManager
operator|.
name|shouldUpdateBlockKey
argument_list|(
name|now
operator|-
name|lastBlockKeyUpdate
argument_list|)
condition|)
block|{
synchronized|synchronized
init|(
name|HeartbeatManager
operator|.
name|this
init|)
block|{
for|for
control|(
name|DatanodeDescriptor
name|d
range|:
name|datanodes
control|)
block|{
name|d
operator|.
name|needKeyUpdate
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|lastBlockKeyUpdate
operator|=
name|now
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
name|error
argument_list|(
literal|"Exception while checking heartbeat"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// 5 seconds
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{         }
block|}
block|}
block|}
comment|/** Datanode statistics.    * For decommissioning/decommissioned nodes, only used capacity is counted.    */
DECL|class|Stats
specifier|private
specifier|static
class|class
name|Stats
block|{
DECL|field|capacityTotal
specifier|private
name|long
name|capacityTotal
init|=
literal|0L
decl_stmt|;
DECL|field|capacityUsed
specifier|private
name|long
name|capacityUsed
init|=
literal|0L
decl_stmt|;
DECL|field|capacityRemaining
specifier|private
name|long
name|capacityRemaining
init|=
literal|0L
decl_stmt|;
DECL|field|blockPoolUsed
specifier|private
name|long
name|blockPoolUsed
init|=
literal|0L
decl_stmt|;
DECL|field|xceiverCount
specifier|private
name|int
name|xceiverCount
init|=
literal|0
decl_stmt|;
DECL|field|cacheCapacity
specifier|private
name|long
name|cacheCapacity
init|=
literal|0L
decl_stmt|;
DECL|field|cacheUsed
specifier|private
name|long
name|cacheUsed
init|=
literal|0L
decl_stmt|;
DECL|field|nodesInService
specifier|private
name|int
name|nodesInService
init|=
literal|0
decl_stmt|;
DECL|field|nodesInServiceXceiverCount
specifier|private
name|int
name|nodesInServiceXceiverCount
init|=
literal|0
decl_stmt|;
DECL|field|expiredHeartbeats
specifier|private
name|int
name|expiredHeartbeats
init|=
literal|0
decl_stmt|;
DECL|method|add (final DatanodeDescriptor node)
specifier|private
name|void
name|add
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
name|capacityUsed
operator|+=
name|node
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
name|blockPoolUsed
operator|+=
name|node
operator|.
name|getBlockPoolUsed
argument_list|()
expr_stmt|;
name|xceiverCount
operator|+=
name|node
operator|.
name|getXceiverCount
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|node
operator|.
name|isDecommissionInProgress
argument_list|()
operator|||
name|node
operator|.
name|isDecommissioned
argument_list|()
operator|)
condition|)
block|{
name|nodesInService
operator|++
expr_stmt|;
name|nodesInServiceXceiverCount
operator|+=
name|node
operator|.
name|getXceiverCount
argument_list|()
expr_stmt|;
name|capacityTotal
operator|+=
name|node
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|capacityRemaining
operator|+=
name|node
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|capacityTotal
operator|+=
name|node
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
block|}
name|cacheCapacity
operator|+=
name|node
operator|.
name|getCacheCapacity
argument_list|()
expr_stmt|;
name|cacheUsed
operator|+=
name|node
operator|.
name|getCacheUsed
argument_list|()
expr_stmt|;
block|}
DECL|method|subtract (final DatanodeDescriptor node)
specifier|private
name|void
name|subtract
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
name|capacityUsed
operator|-=
name|node
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
name|blockPoolUsed
operator|-=
name|node
operator|.
name|getBlockPoolUsed
argument_list|()
expr_stmt|;
name|xceiverCount
operator|-=
name|node
operator|.
name|getXceiverCount
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|node
operator|.
name|isDecommissionInProgress
argument_list|()
operator|||
name|node
operator|.
name|isDecommissioned
argument_list|()
operator|)
condition|)
block|{
name|nodesInService
operator|--
expr_stmt|;
name|nodesInServiceXceiverCount
operator|-=
name|node
operator|.
name|getXceiverCount
argument_list|()
expr_stmt|;
name|capacityTotal
operator|-=
name|node
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|capacityRemaining
operator|-=
name|node
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|capacityTotal
operator|-=
name|node
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
block|}
name|cacheCapacity
operator|-=
name|node
operator|.
name|getCacheCapacity
argument_list|()
expr_stmt|;
name|cacheUsed
operator|-=
name|node
operator|.
name|getCacheUsed
argument_list|()
expr_stmt|;
block|}
comment|/** Increment expired heartbeat counter. */
DECL|method|incrExpiredHeartbeats ()
specifier|private
name|void
name|incrExpiredHeartbeats
parameter_list|()
block|{
name|expiredHeartbeats
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

