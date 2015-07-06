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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Set
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
name|DFSTestUtil
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
name|Block
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
name|FSNamesystem
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
name|protocol
operator|.
name|DatanodeStorage
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
name|util
operator|.
name|Daemon
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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

begin_class
DECL|class|BlockManagerTestUtil
specifier|public
class|class
name|BlockManagerTestUtil
block|{
DECL|method|setNodeReplicationLimit (final BlockManager blockManager, final int limit)
specifier|public
specifier|static
name|void
name|setNodeReplicationLimit
parameter_list|(
specifier|final
name|BlockManager
name|blockManager
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|)
block|{
name|blockManager
operator|.
name|maxReplicationStreams
operator|=
name|limit
expr_stmt|;
block|}
comment|/** @return the datanode descriptor for the given the given storageID. */
DECL|method|getDatanode (final FSNamesystem ns, final String storageID)
specifier|public
specifier|static
name|DatanodeDescriptor
name|getDatanode
parameter_list|(
specifier|final
name|FSNamesystem
name|ns
parameter_list|,
specifier|final
name|String
name|storageID
parameter_list|)
block|{
name|ns
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|ns
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|storageID
argument_list|)
return|;
block|}
finally|finally
block|{
name|ns
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Refresh block queue counts on the name-node.    */
DECL|method|updateState (final BlockManager blockManager)
specifier|public
specifier|static
name|void
name|updateState
parameter_list|(
specifier|final
name|BlockManager
name|blockManager
parameter_list|)
block|{
name|blockManager
operator|.
name|updateState
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return a tuple of the replica state (number racks, number live    * replicas, and number needed replicas) for the given block.    */
DECL|method|getReplicaInfo (final FSNamesystem namesystem, final Block b)
specifier|public
specifier|static
name|int
index|[]
name|getReplicaInfo
parameter_list|(
specifier|final
name|FSNamesystem
name|namesystem
parameter_list|,
specifier|final
name|Block
name|b
parameter_list|)
block|{
specifier|final
name|BlockManager
name|bm
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|namesystem
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|BlockInfo
name|storedBlock
init|=
name|bm
operator|.
name|getStoredBlock
argument_list|(
name|b
argument_list|)
decl_stmt|;
return|return
operator|new
name|int
index|[]
block|{
name|getNumberOfRacks
argument_list|(
name|bm
argument_list|,
name|b
argument_list|)
block|,
name|bm
operator|.
name|countNodes
argument_list|(
name|storedBlock
argument_list|)
operator|.
name|liveReplicas
argument_list|()
block|,
name|bm
operator|.
name|neededReplications
operator|.
name|contains
argument_list|(
name|storedBlock
argument_list|)
condition|?
literal|1
else|:
literal|0
block|}
return|;
block|}
finally|finally
block|{
name|namesystem
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * @return the number of racks over which a given block is replicated    * decommissioning/decommissioned nodes are not counted. corrupt replicas     * are also ignored    */
DECL|method|getNumberOfRacks (final BlockManager blockManager, final Block b)
specifier|private
specifier|static
name|int
name|getNumberOfRacks
parameter_list|(
specifier|final
name|BlockManager
name|blockManager
parameter_list|,
specifier|final
name|Block
name|b
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|rackSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|corruptNodes
init|=
name|getCorruptReplicas
argument_list|(
name|blockManager
argument_list|)
operator|.
name|getNodes
argument_list|(
name|b
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|blockManager
operator|.
name|blocksMap
operator|.
name|getStorages
argument_list|(
name|b
argument_list|)
control|)
block|{
specifier|final
name|DatanodeDescriptor
name|cur
init|=
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|cur
operator|.
name|isDecommissionInProgress
argument_list|()
operator|&&
operator|!
name|cur
operator|.
name|isDecommissioned
argument_list|()
condition|)
block|{
if|if
condition|(
operator|(
name|corruptNodes
operator|==
literal|null
operator|)
operator|||
operator|!
name|corruptNodes
operator|.
name|contains
argument_list|(
name|cur
argument_list|)
condition|)
block|{
name|String
name|rackName
init|=
name|cur
operator|.
name|getNetworkLocation
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|rackSet
operator|.
name|contains
argument_list|(
name|rackName
argument_list|)
condition|)
block|{
name|rackSet
operator|.
name|add
argument_list|(
name|rackName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|rackSet
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * @return replication monitor thread instance from block manager.    */
DECL|method|getReplicationThread (final BlockManager blockManager)
specifier|public
specifier|static
name|Daemon
name|getReplicationThread
parameter_list|(
specifier|final
name|BlockManager
name|blockManager
parameter_list|)
block|{
return|return
name|blockManager
operator|.
name|replicationThread
return|;
block|}
comment|/**    * Stop the replication monitor thread    */
DECL|method|stopReplicationThread (final BlockManager blockManager)
specifier|public
specifier|static
name|void
name|stopReplicationThread
parameter_list|(
specifier|final
name|BlockManager
name|blockManager
parameter_list|)
throws|throws
name|IOException
block|{
name|blockManager
operator|.
name|enableRMTerminationForTesting
argument_list|()
expr_stmt|;
name|blockManager
operator|.
name|replicationThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|blockManager
operator|.
name|replicationThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Interrupted while trying to stop ReplicationMonitor"
argument_list|)
throw|;
block|}
block|}
comment|/**    * @return corruptReplicas from block manager    */
DECL|method|getCorruptReplicas (final BlockManager blockManager)
specifier|public
specifier|static
name|CorruptReplicasMap
name|getCorruptReplicas
parameter_list|(
specifier|final
name|BlockManager
name|blockManager
parameter_list|)
block|{
return|return
name|blockManager
operator|.
name|corruptReplicas
return|;
block|}
comment|/**    * @return computed block replication and block invalidation work that can be    *         scheduled on data-nodes.    * @throws IOException    */
DECL|method|getComputedDatanodeWork (final BlockManager blockManager)
specifier|public
specifier|static
name|int
name|getComputedDatanodeWork
parameter_list|(
specifier|final
name|BlockManager
name|blockManager
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|blockManager
operator|.
name|computeDatanodeWork
argument_list|()
return|;
block|}
DECL|method|computeInvalidationWork (BlockManager bm)
specifier|public
specifier|static
name|int
name|computeInvalidationWork
parameter_list|(
name|BlockManager
name|bm
parameter_list|)
block|{
return|return
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**    * Compute all the replication and invalidation work for the    * given BlockManager.    *     * This differs from the above functions in that it computes    * replication work for all DNs rather than a particular subset,    * regardless of invalidation/replication limit configurations.    *     * NB: you may want to set    * {@link DFSConfigKeys#DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY} to    * a high value to ensure that all work is calculated.    */
DECL|method|computeAllPendingWork (BlockManager bm)
specifier|public
specifier|static
name|int
name|computeAllPendingWork
parameter_list|(
name|BlockManager
name|bm
parameter_list|)
block|{
name|int
name|work
init|=
name|computeInvalidationWork
argument_list|(
name|bm
argument_list|)
decl_stmt|;
name|work
operator|+=
name|bm
operator|.
name|computeBlockRecoveryWork
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
return|return
name|work
return|;
block|}
comment|/**    * Ensure that the given NameNode marks the specified DataNode as    * entirely dead/expired.    * @param nn the NameNode to manipulate    * @param dnName the name of the DataNode    */
DECL|method|noticeDeadDatanode (NameNode nn, String dnName)
specifier|public
specifier|static
name|void
name|noticeDeadDatanode
parameter_list|(
name|NameNode
name|nn
parameter_list|,
name|String
name|dnName
parameter_list|)
block|{
name|FSNamesystem
name|namesystem
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|DatanodeManager
name|dnm
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
name|HeartbeatManager
name|hbm
init|=
name|dnm
operator|.
name|getHeartbeatManager
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
index|[]
name|dnds
init|=
name|hbm
operator|.
name|getDatanodes
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
name|theDND
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DatanodeDescriptor
name|dnd
range|:
name|dnds
control|)
block|{
if|if
condition|(
name|dnd
operator|.
name|getXferAddr
argument_list|()
operator|.
name|equals
argument_list|(
name|dnName
argument_list|)
condition|)
block|{
name|theDND
operator|=
name|dnd
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Could not find DN with name: "
operator|+
name|dnName
argument_list|,
name|theDND
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|hbm
init|)
block|{
name|DFSTestUtil
operator|.
name|setDatanodeDead
argument_list|(
name|theDND
argument_list|)
expr_stmt|;
name|hbm
operator|.
name|heartbeatCheck
argument_list|()
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
comment|/**    * Change whether the block placement policy will prefer the writer's    * local Datanode or not.    * @param prefer if true, prefer local node    */
DECL|method|setWritingPrefersLocalNode ( BlockManager bm, boolean prefer)
specifier|public
specifier|static
name|void
name|setWritingPrefersLocalNode
parameter_list|(
name|BlockManager
name|bm
parameter_list|,
name|boolean
name|prefer
parameter_list|)
block|{
name|BlockPlacementPolicy
name|bpp
init|=
name|bm
operator|.
name|getBlockPlacementPolicy
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|bpp
operator|instanceof
name|BlockPlacementPolicyDefault
argument_list|,
literal|"Must use default policy, got %s"
argument_list|,
name|bpp
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|BlockPlacementPolicyDefault
operator|)
name|bpp
operator|)
operator|.
name|setPreferLocalNode
argument_list|(
name|prefer
argument_list|)
expr_stmt|;
block|}
comment|/**    * Call heartbeat check function of HeartbeatManager    * @param bm the BlockManager to manipulate    */
DECL|method|checkHeartbeat (BlockManager bm)
specifier|public
specifier|static
name|void
name|checkHeartbeat
parameter_list|(
name|BlockManager
name|bm
parameter_list|)
block|{
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getHeartbeatManager
argument_list|()
operator|.
name|heartbeatCheck
argument_list|()
expr_stmt|;
block|}
comment|/**    * Call heartbeat check function of HeartbeatManager and get    * under replicated blocks count within write lock to make sure    * computeDatanodeWork doesn't interfere.    * @param namesystem the FSNamesystem    * @param bm the BlockManager to manipulate    * @return the number of under replicated blocks    */
DECL|method|checkHeartbeatAndGetUnderReplicatedBlocksCount ( FSNamesystem namesystem, BlockManager bm)
specifier|public
specifier|static
name|int
name|checkHeartbeatAndGetUnderReplicatedBlocksCount
parameter_list|(
name|FSNamesystem
name|namesystem
parameter_list|,
name|BlockManager
name|bm
parameter_list|)
block|{
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getHeartbeatManager
argument_list|()
operator|.
name|heartbeatCheck
argument_list|()
expr_stmt|;
return|return
name|bm
operator|.
name|getUnderReplicatedNotMissingBlocks
argument_list|()
return|;
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
DECL|method|updateStorage (DatanodeDescriptor dn, DatanodeStorage s)
specifier|public
specifier|static
name|DatanodeStorageInfo
name|updateStorage
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|,
name|DatanodeStorage
name|s
parameter_list|)
block|{
return|return
name|dn
operator|.
name|updateStorage
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/**    * Call heartbeat check function of HeartbeatManager    * @param bm the BlockManager to manipulate    */
DECL|method|rescanPostponedMisreplicatedBlocks (BlockManager bm)
specifier|public
specifier|static
name|void
name|rescanPostponedMisreplicatedBlocks
parameter_list|(
name|BlockManager
name|bm
parameter_list|)
block|{
name|bm
operator|.
name|rescanPostponedMisreplicatedBlocks
argument_list|()
expr_stmt|;
block|}
DECL|method|getLocalDatanodeDescriptor ( boolean initializeStorage)
specifier|public
specifier|static
name|DatanodeDescriptor
name|getLocalDatanodeDescriptor
parameter_list|(
name|boolean
name|initializeStorage
parameter_list|)
block|{
name|DatanodeDescriptor
name|dn
init|=
operator|new
name|DatanodeDescriptor
argument_list|(
name|DFSTestUtil
operator|.
name|getLocalDatanodeID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|initializeStorage
condition|)
block|{
name|dn
operator|.
name|updateStorage
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|DatanodeStorage
operator|.
name|generateUuid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|dn
return|;
block|}
DECL|method|getDatanodeDescriptor (String ipAddr, String rackLocation, boolean initializeStorage)
specifier|public
specifier|static
name|DatanodeDescriptor
name|getDatanodeDescriptor
parameter_list|(
name|String
name|ipAddr
parameter_list|,
name|String
name|rackLocation
parameter_list|,
name|boolean
name|initializeStorage
parameter_list|)
block|{
return|return
name|getDatanodeDescriptor
argument_list|(
name|ipAddr
argument_list|,
name|rackLocation
argument_list|,
name|initializeStorage
condition|?
operator|new
name|DatanodeStorage
argument_list|(
name|DatanodeStorage
operator|.
name|generateUuid
argument_list|()
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
DECL|method|getDatanodeDescriptor (String ipAddr, String rackLocation, DatanodeStorage storage)
specifier|public
specifier|static
name|DatanodeDescriptor
name|getDatanodeDescriptor
parameter_list|(
name|String
name|ipAddr
parameter_list|,
name|String
name|rackLocation
parameter_list|,
name|DatanodeStorage
name|storage
parameter_list|)
block|{
return|return
name|getDatanodeDescriptor
argument_list|(
name|ipAddr
argument_list|,
name|rackLocation
argument_list|,
name|storage
argument_list|,
literal|"host"
argument_list|)
return|;
block|}
DECL|method|getDatanodeDescriptor (String ipAddr, String rackLocation, DatanodeStorage storage, String hostname)
specifier|public
specifier|static
name|DatanodeDescriptor
name|getDatanodeDescriptor
parameter_list|(
name|String
name|ipAddr
parameter_list|,
name|String
name|rackLocation
parameter_list|,
name|DatanodeStorage
name|storage
parameter_list|,
name|String
name|hostname
parameter_list|)
block|{
name|DatanodeDescriptor
name|dn
init|=
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
name|ipAddr
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DEFAULT_PORT
argument_list|,
name|rackLocation
argument_list|,
name|hostname
argument_list|)
decl_stmt|;
if|if
condition|(
name|storage
operator|!=
literal|null
condition|)
block|{
name|dn
operator|.
name|updateStorage
argument_list|(
name|storage
argument_list|)
expr_stmt|;
block|}
return|return
name|dn
return|;
block|}
DECL|method|newDatanodeStorageInfo ( DatanodeDescriptor dn, DatanodeStorage s)
specifier|public
specifier|static
name|DatanodeStorageInfo
name|newDatanodeStorageInfo
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|,
name|DatanodeStorage
name|s
parameter_list|)
block|{
return|return
operator|new
name|DatanodeStorageInfo
argument_list|(
name|dn
argument_list|,
name|s
argument_list|)
return|;
block|}
DECL|method|getStorageReportsForDatanode ( DatanodeDescriptor dnd)
specifier|public
specifier|static
name|StorageReport
index|[]
name|getStorageReportsForDatanode
parameter_list|(
name|DatanodeDescriptor
name|dnd
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|StorageReport
argument_list|>
name|reports
init|=
operator|new
name|ArrayList
argument_list|<
name|StorageReport
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|dnd
operator|.
name|getStorageInfos
argument_list|()
control|)
block|{
name|DatanodeStorage
name|dns
init|=
operator|new
name|DatanodeStorage
argument_list|(
name|storage
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|storage
operator|.
name|getState
argument_list|()
argument_list|,
name|storage
operator|.
name|getStorageType
argument_list|()
argument_list|)
decl_stmt|;
name|StorageReport
name|report
init|=
operator|new
name|StorageReport
argument_list|(
name|dns
argument_list|,
literal|false
argument_list|,
name|storage
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|storage
operator|.
name|getDfsUsed
argument_list|()
argument_list|,
name|storage
operator|.
name|getRemaining
argument_list|()
argument_list|,
name|storage
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|)
decl_stmt|;
name|reports
operator|.
name|add
argument_list|(
name|report
argument_list|)
expr_stmt|;
block|}
return|return
name|reports
operator|.
name|toArray
argument_list|(
name|StorageReport
operator|.
name|EMPTY_ARRAY
argument_list|)
return|;
block|}
comment|/**    * Have DatanodeManager check decommission state.    * @param dm the DatanodeManager to manipulate    */
DECL|method|recheckDecommissionState (DatanodeManager dm)
specifier|public
specifier|static
name|void
name|recheckDecommissionState
parameter_list|(
name|DatanodeManager
name|dm
parameter_list|)
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|dm
operator|.
name|getDecomManager
argument_list|()
operator|.
name|runMonitor
argument_list|()
expr_stmt|;
block|}
comment|/**    * add block to the replicateBlocks queue of the Datanode    */
DECL|method|addBlockToBeReplicated (DatanodeDescriptor node, Block block, DatanodeStorageInfo[] targets)
specifier|public
specifier|static
name|void
name|addBlockToBeReplicated
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|,
name|Block
name|block
parameter_list|,
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
block|{
name|node
operator|.
name|addBlockToBeReplicated
argument_list|(
name|block
argument_list|,
name|targets
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

