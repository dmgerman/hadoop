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
name|Iterator
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|BlockUCState
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|ReplicaState
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

begin_comment
comment|/**  * Represents a block that is currently being constructed.<br>  * This is usually the last block of a file opened for write or append.  */
end_comment

begin_class
DECL|class|BlockInfoContiguousUnderConstruction
specifier|public
class|class
name|BlockInfoContiguousUnderConstruction
extends|extends
name|BlockInfoContiguous
block|{
comment|/** Block state. See {@link BlockUCState} */
DECL|field|blockUCState
specifier|private
name|BlockUCState
name|blockUCState
decl_stmt|;
comment|/**    * Block replicas as assigned when the block was allocated.    * This defines the pipeline order.    */
DECL|field|replicas
specifier|private
name|List
argument_list|<
name|ReplicaUnderConstruction
argument_list|>
name|replicas
decl_stmt|;
comment|/**    * Index of the primary data node doing the recovery. Useful for log    * messages.    */
DECL|field|primaryNodeIndex
specifier|private
name|int
name|primaryNodeIndex
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * The new generation stamp, which this block will have    * after the recovery succeeds. Also used as a recovery id to identify    * the right recovery if any of the abandoned recoveries re-appear.    */
DECL|field|blockRecoveryId
specifier|private
name|long
name|blockRecoveryId
init|=
literal|0
decl_stmt|;
comment|/**    * The block source to use in the event of copy-on-write truncate.    */
DECL|field|truncateBlock
specifier|private
name|Block
name|truncateBlock
decl_stmt|;
comment|/**    * ReplicaUnderConstruction contains information about replicas while    * they are under construction.    * The GS, the length and the state of the replica is as reported by     * the data-node.    * It is not guaranteed, but expected, that data-nodes actually have    * corresponding replicas.    */
DECL|class|ReplicaUnderConstruction
specifier|static
class|class
name|ReplicaUnderConstruction
extends|extends
name|Block
block|{
DECL|field|expectedLocation
specifier|private
specifier|final
name|DatanodeStorageInfo
name|expectedLocation
decl_stmt|;
DECL|field|state
specifier|private
name|ReplicaState
name|state
decl_stmt|;
DECL|field|chosenAsPrimary
specifier|private
name|boolean
name|chosenAsPrimary
decl_stmt|;
DECL|method|ReplicaUnderConstruction (Block block, DatanodeStorageInfo target, ReplicaState state)
name|ReplicaUnderConstruction
parameter_list|(
name|Block
name|block
parameter_list|,
name|DatanodeStorageInfo
name|target
parameter_list|,
name|ReplicaState
name|state
parameter_list|)
block|{
name|super
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedLocation
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|chosenAsPrimary
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Expected block replica location as assigned when the block was allocated.      * This defines the pipeline order.      * It is not guaranteed, but expected, that the data-node actually has      * the replica.      */
DECL|method|getExpectedStorageLocation ()
specifier|private
name|DatanodeStorageInfo
name|getExpectedStorageLocation
parameter_list|()
block|{
return|return
name|expectedLocation
return|;
block|}
comment|/**      * Get replica state as reported by the data-node.      */
DECL|method|getState ()
name|ReplicaState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**      * Whether the replica was chosen for recovery.      */
DECL|method|getChosenAsPrimary ()
name|boolean
name|getChosenAsPrimary
parameter_list|()
block|{
return|return
name|chosenAsPrimary
return|;
block|}
comment|/**      * Set replica state.      */
DECL|method|setState (ReplicaState s)
name|void
name|setState
parameter_list|(
name|ReplicaState
name|s
parameter_list|)
block|{
name|state
operator|=
name|s
expr_stmt|;
block|}
comment|/**      * Set whether this replica was chosen for recovery.      */
DECL|method|setChosenAsPrimary (boolean chosenAsPrimary)
name|void
name|setChosenAsPrimary
parameter_list|(
name|boolean
name|chosenAsPrimary
parameter_list|)
block|{
name|this
operator|.
name|chosenAsPrimary
operator|=
name|chosenAsPrimary
expr_stmt|;
block|}
comment|/**      * Is data-node the replica belongs to alive.      */
DECL|method|isAlive ()
name|boolean
name|isAlive
parameter_list|()
block|{
return|return
name|expectedLocation
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|isAlive
return|;
block|}
annotation|@
name|Override
comment|// Block
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// Block
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// Sufficient to rely on super's implementation
return|return
operator|(
name|this
operator|==
name|obj
operator|)
operator|||
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|50
argument_list|)
decl_stmt|;
name|appendStringTo
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|appendStringTo (StringBuilder sb)
specifier|public
name|void
name|appendStringTo
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"ReplicaUC["
argument_list|)
operator|.
name|append
argument_list|(
name|expectedLocation
argument_list|)
operator|.
name|append
argument_list|(
literal|"|"
argument_list|)
operator|.
name|append
argument_list|(
name|state
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create block and set its state to    * {@link BlockUCState#UNDER_CONSTRUCTION}.    */
DECL|method|BlockInfoContiguousUnderConstruction (Block blk, short replication)
specifier|public
name|BlockInfoContiguousUnderConstruction
parameter_list|(
name|Block
name|blk
parameter_list|,
name|short
name|replication
parameter_list|)
block|{
name|this
argument_list|(
name|blk
argument_list|,
name|replication
argument_list|,
name|BlockUCState
operator|.
name|UNDER_CONSTRUCTION
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a block that is currently being constructed.    */
DECL|method|BlockInfoContiguousUnderConstruction (Block blk, short replication, BlockUCState state, DatanodeStorageInfo[] targets)
specifier|public
name|BlockInfoContiguousUnderConstruction
parameter_list|(
name|Block
name|blk
parameter_list|,
name|short
name|replication
parameter_list|,
name|BlockUCState
name|state
parameter_list|,
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
block|{
name|super
argument_list|(
name|blk
argument_list|,
name|replication
argument_list|)
expr_stmt|;
assert|assert
name|getBlockUCState
argument_list|()
operator|!=
name|BlockUCState
operator|.
name|COMPLETE
operator|:
literal|"BlockInfoUnderConstruction cannot be in COMPLETE state"
assert|;
name|this
operator|.
name|blockUCState
operator|=
name|state
expr_stmt|;
name|setExpectedLocations
argument_list|(
name|targets
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convert an under construction block to a complete block.    *     * @return BlockInfo - a complete block.    * @throws IOException if the state of the block     * (the generation stamp and the length) has not been committed by     * the client or it does not have at least a minimal number of replicas     * reported from data-nodes.     */
DECL|method|convertToCompleteBlock ()
name|BlockInfo
name|convertToCompleteBlock
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|getBlockUCState
argument_list|()
operator|!=
name|BlockUCState
operator|.
name|COMPLETE
operator|:
literal|"Trying to convert a COMPLETE block"
assert|;
return|return
operator|new
name|BlockInfoContiguous
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Set expected locations */
DECL|method|setExpectedLocations (DatanodeStorageInfo[] targets)
specifier|public
name|void
name|setExpectedLocations
parameter_list|(
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
block|{
name|int
name|numLocations
init|=
name|targets
operator|==
literal|null
condition|?
literal|0
else|:
name|targets
operator|.
name|length
decl_stmt|;
name|this
operator|.
name|replicas
operator|=
operator|new
name|ArrayList
argument_list|<
name|ReplicaUnderConstruction
argument_list|>
argument_list|(
name|numLocations
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numLocations
condition|;
name|i
operator|++
control|)
name|replicas
operator|.
name|add
argument_list|(
operator|new
name|ReplicaUnderConstruction
argument_list|(
name|this
argument_list|,
name|targets
index|[
name|i
index|]
argument_list|,
name|ReplicaState
operator|.
name|RBW
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create array of expected replica locations    * (as has been assigned by chooseTargets()).    */
DECL|method|getExpectedStorageLocations ()
specifier|public
name|DatanodeStorageInfo
index|[]
name|getExpectedStorageLocations
parameter_list|()
block|{
name|int
name|numLocations
init|=
name|replicas
operator|==
literal|null
condition|?
literal|0
else|:
name|replicas
operator|.
name|size
argument_list|()
decl_stmt|;
name|DatanodeStorageInfo
index|[]
name|storages
init|=
operator|new
name|DatanodeStorageInfo
index|[
name|numLocations
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numLocations
condition|;
name|i
operator|++
control|)
name|storages
index|[
name|i
index|]
operator|=
name|replicas
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getExpectedStorageLocation
argument_list|()
expr_stmt|;
return|return
name|storages
return|;
block|}
comment|/** Get the number of expected locations */
DECL|method|getNumExpectedLocations ()
specifier|public
name|int
name|getNumExpectedLocations
parameter_list|()
block|{
return|return
name|replicas
operator|==
literal|null
condition|?
literal|0
else|:
name|replicas
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Return the state of the block under construction.    * @see BlockUCState    */
annotation|@
name|Override
comment|// BlockInfo
DECL|method|getBlockUCState ()
specifier|public
name|BlockUCState
name|getBlockUCState
parameter_list|()
block|{
return|return
name|blockUCState
return|;
block|}
DECL|method|setBlockUCState (BlockUCState s)
name|void
name|setBlockUCState
parameter_list|(
name|BlockUCState
name|s
parameter_list|)
block|{
name|blockUCState
operator|=
name|s
expr_stmt|;
block|}
comment|/** Get block recovery ID */
DECL|method|getBlockRecoveryId ()
specifier|public
name|long
name|getBlockRecoveryId
parameter_list|()
block|{
return|return
name|blockRecoveryId
return|;
block|}
comment|/** Get recover block */
DECL|method|getTruncateBlock ()
specifier|public
name|Block
name|getTruncateBlock
parameter_list|()
block|{
return|return
name|truncateBlock
return|;
block|}
DECL|method|setTruncateBlock (Block recoveryBlock)
specifier|public
name|void
name|setTruncateBlock
parameter_list|(
name|Block
name|recoveryBlock
parameter_list|)
block|{
name|this
operator|.
name|truncateBlock
operator|=
name|recoveryBlock
expr_stmt|;
block|}
comment|/**    * Process the recorded replicas. When about to commit or finish the    * pipeline recovery sort out bad replicas.    * @param genStamp  The final generation stamp for the block.    */
DECL|method|setGenerationStampAndVerifyReplicas (long genStamp)
specifier|public
name|void
name|setGenerationStampAndVerifyReplicas
parameter_list|(
name|long
name|genStamp
parameter_list|)
block|{
comment|// Set the generation stamp for the block.
name|setGenerationStamp
argument_list|(
name|genStamp
argument_list|)
expr_stmt|;
if|if
condition|(
name|replicas
operator|==
literal|null
condition|)
return|return;
comment|// Remove the replicas with wrong gen stamp.
comment|// The replica list is unchanged.
for|for
control|(
name|ReplicaUnderConstruction
name|r
range|:
name|replicas
control|)
block|{
if|if
condition|(
name|genStamp
operator|!=
name|r
operator|.
name|getGenerationStamp
argument_list|()
condition|)
block|{
name|r
operator|.
name|getExpectedStorageLocation
argument_list|()
operator|.
name|removeBlock
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|blockStateChangeLog
operator|.
name|info
argument_list|(
literal|"BLOCK* Removing stale replica "
operator|+
literal|"from location: {}"
argument_list|,
name|r
operator|.
name|getExpectedStorageLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Commit block's length and generation stamp as reported by the client.    * Set block state to {@link BlockUCState#COMMITTED}.    * @param block - contains client reported block length and generation     * @throws IOException if block ids are inconsistent.    */
DECL|method|commitBlock (Block block)
name|void
name|commitBlock
parameter_list|(
name|Block
name|block
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getBlockId
argument_list|()
operator|!=
name|block
operator|.
name|getBlockId
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Trying to commit inconsistent block: id = "
operator|+
name|block
operator|.
name|getBlockId
argument_list|()
operator|+
literal|", expected id = "
operator|+
name|getBlockId
argument_list|()
argument_list|)
throw|;
name|blockUCState
operator|=
name|BlockUCState
operator|.
name|COMMITTED
expr_stmt|;
name|this
operator|.
name|set
argument_list|(
name|getBlockId
argument_list|()
argument_list|,
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
comment|// Sort out invalid replicas.
name|setGenerationStampAndVerifyReplicas
argument_list|(
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize lease recovery for this block.    * Find the first alive data-node starting from the previous primary and    * make it primary.    */
DECL|method|initializeBlockRecovery (long recoveryId)
specifier|public
name|void
name|initializeBlockRecovery
parameter_list|(
name|long
name|recoveryId
parameter_list|)
block|{
name|setBlockUCState
argument_list|(
name|BlockUCState
operator|.
name|UNDER_RECOVERY
argument_list|)
expr_stmt|;
name|blockRecoveryId
operator|=
name|recoveryId
expr_stmt|;
if|if
condition|(
name|replicas
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|NameNode
operator|.
name|blockStateChangeLog
operator|.
name|warn
argument_list|(
literal|"BLOCK*"
operator|+
literal|" BlockInfoUnderConstruction.initLeaseRecovery:"
operator|+
literal|" No blocks found, lease removed."
argument_list|)
expr_stmt|;
block|}
name|boolean
name|allLiveReplicasTriedAsPrimary
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|replicas
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// Check if all replicas have been tried or not.
if|if
condition|(
name|replicas
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|allLiveReplicasTriedAsPrimary
operator|=
operator|(
name|allLiveReplicasTriedAsPrimary
operator|&&
name|replicas
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getChosenAsPrimary
argument_list|()
operator|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|allLiveReplicasTriedAsPrimary
condition|)
block|{
comment|// Just set all the replicas to be chosen whether they are alive or not.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|replicas
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|replicas
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|setChosenAsPrimary
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|mostRecentLastUpdate
init|=
literal|0
decl_stmt|;
name|ReplicaUnderConstruction
name|primary
init|=
literal|null
decl_stmt|;
name|primaryNodeIndex
operator|=
operator|-
literal|1
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|replicas
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// Skip alive replicas which have been chosen for recovery.
if|if
condition|(
operator|!
operator|(
name|replicas
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|isAlive
argument_list|()
operator|&&
operator|!
name|replicas
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getChosenAsPrimary
argument_list|()
operator|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|ReplicaUnderConstruction
name|ruc
init|=
name|replicas
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|long
name|lastUpdate
init|=
name|ruc
operator|.
name|getExpectedStorageLocation
argument_list|()
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|getLastUpdateMonotonic
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastUpdate
operator|>
name|mostRecentLastUpdate
condition|)
block|{
name|primaryNodeIndex
operator|=
name|i
expr_stmt|;
name|primary
operator|=
name|ruc
expr_stmt|;
name|mostRecentLastUpdate
operator|=
name|lastUpdate
expr_stmt|;
block|}
block|}
if|if
condition|(
name|primary
operator|!=
literal|null
condition|)
block|{
name|primary
operator|.
name|getExpectedStorageLocation
argument_list|()
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|addBlockToBeRecovered
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|primary
operator|.
name|setChosenAsPrimary
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|blockStateChangeLog
operator|.
name|info
argument_list|(
literal|"BLOCK* {} recovery started, primary={}"
argument_list|,
name|this
argument_list|,
name|primary
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addReplicaIfNotPresent (DatanodeStorageInfo storage, Block block, ReplicaState rState)
name|void
name|addReplicaIfNotPresent
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|,
name|Block
name|block
parameter_list|,
name|ReplicaState
name|rState
parameter_list|)
block|{
name|Iterator
argument_list|<
name|ReplicaUnderConstruction
argument_list|>
name|it
init|=
name|replicas
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
name|ReplicaUnderConstruction
name|r
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|DatanodeStorageInfo
name|expectedLocation
init|=
name|r
operator|.
name|getExpectedStorageLocation
argument_list|()
decl_stmt|;
if|if
condition|(
name|expectedLocation
operator|==
name|storage
condition|)
block|{
comment|// Record the gen stamp from the report
name|r
operator|.
name|setGenerationStamp
argument_list|(
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|expectedLocation
operator|!=
literal|null
operator|&&
name|expectedLocation
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|==
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
condition|)
block|{
comment|// The Datanode reported that the block is on a different storage
comment|// than the one chosen by BlockPlacementPolicy. This can occur as
comment|// we allow Datanodes to choose the target storage. Update our
comment|// state by removing the stale entry and adding a new one.
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|replicas
operator|.
name|add
argument_list|(
operator|new
name|ReplicaUnderConstruction
argument_list|(
name|block
argument_list|,
name|storage
argument_list|,
name|rState
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// BlockInfo
comment|// BlockInfoUnderConstruction participates in maps the same way as BlockInfo
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// BlockInfo
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// Sufficient to rely on super's implementation
return|return
operator|(
name|this
operator|==
name|obj
operator|)
operator|||
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|appendStringTo
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|appendStringTo (StringBuilder sb)
specifier|public
name|void
name|appendStringTo
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|super
operator|.
name|appendStringTo
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|appendUCParts
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
DECL|method|appendUCParts (StringBuilder sb)
specifier|private
name|void
name|appendUCParts
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"{UCState="
argument_list|)
operator|.
name|append
argument_list|(
name|blockUCState
argument_list|)
operator|.
name|append
argument_list|(
literal|", truncateBlock="
operator|+
name|truncateBlock
argument_list|)
operator|.
name|append
argument_list|(
literal|", primaryNodeIndex="
argument_list|)
operator|.
name|append
argument_list|(
name|primaryNodeIndex
argument_list|)
operator|.
name|append
argument_list|(
literal|", replicas=["
argument_list|)
expr_stmt|;
if|if
condition|(
name|replicas
operator|!=
literal|null
condition|)
block|{
name|Iterator
argument_list|<
name|ReplicaUnderConstruction
argument_list|>
name|iter
init|=
name|replicas
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iter
operator|.
name|next
argument_list|()
operator|.
name|appendStringTo
argument_list|(
name|sb
argument_list|)
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|iter
operator|.
name|next
argument_list|()
operator|.
name|appendStringTo
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

