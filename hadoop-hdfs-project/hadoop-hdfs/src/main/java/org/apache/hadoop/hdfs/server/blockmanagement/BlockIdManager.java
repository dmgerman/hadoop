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
name|protocol
operator|.
name|BlockType
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
name|HdfsConstants
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
name|GenerationStamp
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
name|FSEditLog
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
import|import static
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
name|BlockType
operator|.
name|STRIPED
import|;
end_import

begin_comment
comment|/**  * BlockIdManager allocates the generation stamps and the block ID. The  * {@link FSNamesystem} is responsible for persisting the allocations in the  * {@link FSEditLog}.  */
end_comment

begin_class
DECL|class|BlockIdManager
specifier|public
class|class
name|BlockIdManager
block|{
comment|/**    * The global generation stamp for legacy blocks with randomly    * generated block IDs.    */
DECL|field|legacyGenerationStamp
specifier|private
specifier|final
name|GenerationStamp
name|legacyGenerationStamp
init|=
operator|new
name|GenerationStamp
argument_list|()
decl_stmt|;
comment|/**    * The global generation stamp for this file system.    */
DECL|field|generationStamp
specifier|private
specifier|final
name|GenerationStamp
name|generationStamp
init|=
operator|new
name|GenerationStamp
argument_list|()
decl_stmt|;
comment|/**    * The value of the generation stamp when the first switch to sequential    * block IDs was made. Blocks with generation stamps below this value    * have randomly allocated block IDs. Blocks with generation stamps above    * this value had sequentially allocated block IDs. Read from the fsImage    * (or initialized as an offset from the V1 (legacy) generation stamp on    * upgrade).    */
DECL|field|legacyGenerationStampLimit
specifier|private
name|long
name|legacyGenerationStampLimit
decl_stmt|;
comment|/**    * The global block ID space for this file system.    */
DECL|field|blockIdGenerator
specifier|private
specifier|final
name|SequentialBlockIdGenerator
name|blockIdGenerator
decl_stmt|;
DECL|field|blockGroupIdGenerator
specifier|private
specifier|final
name|SequentialBlockGroupIdGenerator
name|blockGroupIdGenerator
decl_stmt|;
DECL|method|BlockIdManager (BlockManager blockManager)
specifier|public
name|BlockIdManager
parameter_list|(
name|BlockManager
name|blockManager
parameter_list|)
block|{
name|this
operator|.
name|legacyGenerationStampLimit
operator|=
name|HdfsConstants
operator|.
name|GRANDFATHER_GENERATION_STAMP
expr_stmt|;
name|this
operator|.
name|blockIdGenerator
operator|=
operator|new
name|SequentialBlockIdGenerator
argument_list|(
name|blockManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockGroupIdGenerator
operator|=
operator|new
name|SequentialBlockGroupIdGenerator
argument_list|(
name|blockManager
argument_list|)
expr_stmt|;
block|}
comment|/**    * Upgrades the generation stamp for the filesystem    * by reserving a sufficient range for all existing blocks.    * Should be invoked only during the first upgrade to    * sequential block IDs.    */
DECL|method|upgradeLegacyGenerationStamp ()
specifier|public
name|long
name|upgradeLegacyGenerationStamp
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|generationStamp
operator|.
name|getCurrentValue
argument_list|()
operator|==
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
expr_stmt|;
name|generationStamp
operator|.
name|skipTo
argument_list|(
name|legacyGenerationStamp
operator|.
name|getCurrentValue
argument_list|()
operator|+
name|HdfsServerConstants
operator|.
name|RESERVED_LEGACY_GENERATION_STAMPS
argument_list|)
expr_stmt|;
name|legacyGenerationStampLimit
operator|=
name|generationStamp
operator|.
name|getCurrentValue
argument_list|()
expr_stmt|;
return|return
name|generationStamp
operator|.
name|getCurrentValue
argument_list|()
return|;
block|}
comment|/**    * Sets the generation stamp that delineates random and sequentially    * allocated block IDs.    *    * @param stamp set generation stamp limit to this value    */
DECL|method|setLegacyGenerationStampLimit (long stamp)
specifier|public
name|void
name|setLegacyGenerationStampLimit
parameter_list|(
name|long
name|stamp
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|legacyGenerationStampLimit
operator|==
name|HdfsConstants
operator|.
name|GRANDFATHER_GENERATION_STAMP
argument_list|)
expr_stmt|;
name|legacyGenerationStampLimit
operator|=
name|stamp
expr_stmt|;
block|}
comment|/**    * Gets the value of the generation stamp that delineates sequential    * and random block IDs.    */
DECL|method|getGenerationStampAtblockIdSwitch ()
specifier|public
name|long
name|getGenerationStampAtblockIdSwitch
parameter_list|()
block|{
return|return
name|legacyGenerationStampLimit
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getBlockIdGenerator ()
name|SequentialBlockIdGenerator
name|getBlockIdGenerator
parameter_list|()
block|{
return|return
name|blockIdGenerator
return|;
block|}
comment|/**    * Sets the maximum allocated contiguous block ID for this filesystem. This is    * the basis for allocating new block IDs.    */
DECL|method|setLastAllocatedContiguousBlockId (long blockId)
specifier|public
name|void
name|setLastAllocatedContiguousBlockId
parameter_list|(
name|long
name|blockId
parameter_list|)
block|{
name|blockIdGenerator
operator|.
name|skipTo
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the maximum sequentially allocated contiguous block ID for this    * filesystem    */
DECL|method|getLastAllocatedContiguousBlockId ()
specifier|public
name|long
name|getLastAllocatedContiguousBlockId
parameter_list|()
block|{
return|return
name|blockIdGenerator
operator|.
name|getCurrentValue
argument_list|()
return|;
block|}
comment|/**    * Sets the maximum allocated striped block ID for this filesystem. This is    * the basis for allocating new block IDs.    */
DECL|method|setLastAllocatedStripedBlockId (long blockId)
specifier|public
name|void
name|setLastAllocatedStripedBlockId
parameter_list|(
name|long
name|blockId
parameter_list|)
block|{
name|blockGroupIdGenerator
operator|.
name|skipTo
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the maximum sequentially allocated striped block ID for this    * filesystem    */
DECL|method|getLastAllocatedStripedBlockId ()
specifier|public
name|long
name|getLastAllocatedStripedBlockId
parameter_list|()
block|{
return|return
name|blockGroupIdGenerator
operator|.
name|getCurrentValue
argument_list|()
return|;
block|}
comment|/**    * Sets the current generation stamp for legacy blocks    */
DECL|method|setLegacyGenerationStamp (long stamp)
specifier|public
name|void
name|setLegacyGenerationStamp
parameter_list|(
name|long
name|stamp
parameter_list|)
block|{
name|legacyGenerationStamp
operator|.
name|setCurrentValue
argument_list|(
name|stamp
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the current generation stamp for legacy blocks    */
DECL|method|getLegacyGenerationStamp ()
specifier|public
name|long
name|getLegacyGenerationStamp
parameter_list|()
block|{
return|return
name|legacyGenerationStamp
operator|.
name|getCurrentValue
argument_list|()
return|;
block|}
comment|/**    * Gets the current generation stamp for this filesystem    */
DECL|method|setGenerationStamp (long stamp)
specifier|public
name|void
name|setGenerationStamp
parameter_list|(
name|long
name|stamp
parameter_list|)
block|{
name|generationStamp
operator|.
name|setCurrentValue
argument_list|(
name|stamp
argument_list|)
expr_stmt|;
block|}
DECL|method|getGenerationStamp ()
specifier|public
name|long
name|getGenerationStamp
parameter_list|()
block|{
return|return
name|generationStamp
operator|.
name|getCurrentValue
argument_list|()
return|;
block|}
comment|/**    * Increments, logs and then returns the stamp    */
DECL|method|nextGenerationStamp (boolean legacyBlock)
name|long
name|nextGenerationStamp
parameter_list|(
name|boolean
name|legacyBlock
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|legacyBlock
condition|?
name|getNextLegacyGenerationStamp
argument_list|()
else|:
name|getNextGenerationStamp
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNextLegacyGenerationStamp ()
name|long
name|getNextLegacyGenerationStamp
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|legacyGenStamp
init|=
name|legacyGenerationStamp
operator|.
name|nextValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|legacyGenStamp
operator|>=
name|legacyGenerationStampLimit
condition|)
block|{
comment|// We ran out of generation stamps for legacy blocks. In practice, it
comment|// is extremely unlikely as we reserved 1T legacy generation stamps. The
comment|// result is that we can no longer append to the legacy blocks that
comment|// were created before the upgrade to sequential block IDs.
throw|throw
operator|new
name|OutOfLegacyGenerationStampsException
argument_list|()
throw|;
block|}
return|return
name|legacyGenStamp
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNextGenerationStamp ()
name|long
name|getNextGenerationStamp
parameter_list|()
block|{
return|return
name|generationStamp
operator|.
name|nextValue
argument_list|()
return|;
block|}
DECL|method|getLegacyGenerationStampLimit ()
specifier|public
name|long
name|getLegacyGenerationStampLimit
parameter_list|()
block|{
return|return
name|legacyGenerationStampLimit
return|;
block|}
comment|/**    * Determine whether the block ID was randomly generated (legacy) or    * sequentially generated. The generation stamp value is used to    * make the distinction.    *    * @return true if the block ID was randomly generated, false otherwise.    */
DECL|method|isLegacyBlock (Block block)
name|boolean
name|isLegacyBlock
parameter_list|(
name|Block
name|block
parameter_list|)
block|{
return|return
name|block
operator|.
name|getGenerationStamp
argument_list|()
operator|<
name|getLegacyGenerationStampLimit
argument_list|()
return|;
block|}
comment|/**    * Increments, logs and then returns the block ID    */
DECL|method|nextBlockId (BlockType blockType)
name|long
name|nextBlockId
parameter_list|(
name|BlockType
name|blockType
parameter_list|)
block|{
switch|switch
condition|(
name|blockType
condition|)
block|{
case|case
name|CONTIGUOUS
case|:
return|return
name|blockIdGenerator
operator|.
name|nextValue
argument_list|()
return|;
case|case
name|STRIPED
case|:
return|return
name|blockGroupIdGenerator
operator|.
name|nextValue
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"nextBlockId called with an unsupported BlockType"
argument_list|)
throw|;
block|}
block|}
DECL|method|isGenStampInFuture (Block block)
name|boolean
name|isGenStampInFuture
parameter_list|(
name|Block
name|block
parameter_list|)
block|{
if|if
condition|(
name|isLegacyBlock
argument_list|(
name|block
argument_list|)
condition|)
block|{
return|return
name|block
operator|.
name|getGenerationStamp
argument_list|()
operator|>
name|getLegacyGenerationStamp
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|block
operator|.
name|getGenerationStamp
argument_list|()
operator|>
name|getGenerationStamp
argument_list|()
return|;
block|}
block|}
DECL|method|clear ()
name|void
name|clear
parameter_list|()
block|{
name|legacyGenerationStamp
operator|.
name|setCurrentValue
argument_list|(
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
expr_stmt|;
name|generationStamp
operator|.
name|setCurrentValue
argument_list|(
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
expr_stmt|;
name|getBlockIdGenerator
argument_list|()
operator|.
name|setCurrentValue
argument_list|(
name|SequentialBlockIdGenerator
operator|.
name|LAST_RESERVED_BLOCK_ID
argument_list|)
expr_stmt|;
name|getBlockGroupIdGenerator
argument_list|()
operator|.
name|setCurrentValue
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|legacyGenerationStampLimit
operator|=
name|HdfsConstants
operator|.
name|GRANDFATHER_GENERATION_STAMP
expr_stmt|;
block|}
comment|/**    * Return true if the block is a striped block.    *    * Before HDFS-4645, block ID was randomly generated (legacy), so it is    * possible that legacy block ID to be negative, which should not be    * considered as striped block ID.    *    * @see #isLegacyBlock(Block) detecting legacy block IDs.    */
DECL|method|isStripedBlock (Block block)
specifier|public
name|boolean
name|isStripedBlock
parameter_list|(
name|Block
name|block
parameter_list|)
block|{
return|return
name|isStripedBlockID
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
operator|&&
operator|!
name|isLegacyBlock
argument_list|(
name|block
argument_list|)
return|;
block|}
comment|/**    * See {@link #isStripedBlock(Block)}, we should not use this function alone    * to determine a block is striped block.    */
DECL|method|isStripedBlockID (long id)
specifier|public
specifier|static
name|boolean
name|isStripedBlockID
parameter_list|(
name|long
name|id
parameter_list|)
block|{
return|return
name|BlockType
operator|.
name|fromBlockId
argument_list|(
name|id
argument_list|)
operator|==
name|STRIPED
return|;
block|}
comment|/**    * The last 4 bits of HdfsConstants.BLOCK_GROUP_INDEX_MASK(15) is 1111,    * so the last 4 bits of (~HdfsConstants.BLOCK_GROUP_INDEX_MASK) is 0000    * and the other 60 bits are 1. Group ID is the first 60 bits of any    * data/parity block id in the same striped block group.    */
DECL|method|convertToStripedID (long id)
specifier|static
name|long
name|convertToStripedID
parameter_list|(
name|long
name|id
parameter_list|)
block|{
return|return
name|id
operator|&
operator|(
operator|~
name|HdfsServerConstants
operator|.
name|BLOCK_GROUP_INDEX_MASK
operator|)
return|;
block|}
DECL|method|getBlockIndex (Block reportedBlock)
specifier|public
specifier|static
name|byte
name|getBlockIndex
parameter_list|(
name|Block
name|reportedBlock
parameter_list|)
block|{
return|return
call|(
name|byte
call|)
argument_list|(
name|reportedBlock
operator|.
name|getBlockId
argument_list|()
operator|&
name|HdfsServerConstants
operator|.
name|BLOCK_GROUP_INDEX_MASK
argument_list|)
return|;
block|}
DECL|method|getBlockGroupIdGenerator ()
name|SequentialBlockGroupIdGenerator
name|getBlockGroupIdGenerator
parameter_list|()
block|{
return|return
name|blockGroupIdGenerator
return|;
block|}
block|}
end_class

end_unit

