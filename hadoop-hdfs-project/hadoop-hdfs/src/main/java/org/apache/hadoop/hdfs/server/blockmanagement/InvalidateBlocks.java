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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
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
name|HashSet
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
name|Map
operator|.
name|Entry
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
name|atomic
operator|.
name|LongAdder
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
name|DatanodeInfo
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
name|util
operator|.
name|LightWeightHashSet
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
name|StringUtils
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

begin_comment
comment|/**  * Keeps a Collection for every named machine containing blocks  * that have recently been invalidated and are thought to live  * on the machine in question.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|InvalidateBlocks
class|class
name|InvalidateBlocks
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|DatanodeInfo
argument_list|,
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
argument_list|>
DECL|field|nodeToBlocks
name|nodeToBlocks
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|DatanodeInfo
argument_list|,
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
argument_list|>
DECL|field|nodeToECBlocks
name|nodeToECBlocks
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|numBlocks
specifier|private
specifier|final
name|LongAdder
name|numBlocks
init|=
operator|new
name|LongAdder
argument_list|()
decl_stmt|;
DECL|field|numECBlocks
specifier|private
specifier|final
name|LongAdder
name|numECBlocks
init|=
operator|new
name|LongAdder
argument_list|()
decl_stmt|;
DECL|field|blockInvalidateLimit
specifier|private
specifier|final
name|int
name|blockInvalidateLimit
decl_stmt|;
DECL|field|blockIdManager
specifier|private
specifier|final
name|BlockIdManager
name|blockIdManager
decl_stmt|;
comment|/**    * The period of pending time for block invalidation since the NameNode    * startup    */
DECL|field|pendingPeriodInMs
specifier|private
specifier|final
name|long
name|pendingPeriodInMs
decl_stmt|;
comment|/** the startup time */
DECL|field|startupTime
specifier|private
specifier|final
name|long
name|startupTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
DECL|method|InvalidateBlocks (final int blockInvalidateLimit, long pendingPeriodInMs, final BlockIdManager blockIdManager)
name|InvalidateBlocks
parameter_list|(
specifier|final
name|int
name|blockInvalidateLimit
parameter_list|,
name|long
name|pendingPeriodInMs
parameter_list|,
specifier|final
name|BlockIdManager
name|blockIdManager
parameter_list|)
block|{
name|this
operator|.
name|blockInvalidateLimit
operator|=
name|blockInvalidateLimit
expr_stmt|;
name|this
operator|.
name|pendingPeriodInMs
operator|=
name|pendingPeriodInMs
expr_stmt|;
name|this
operator|.
name|blockIdManager
operator|=
name|blockIdManager
expr_stmt|;
name|printBlockDeletionTime
argument_list|()
expr_stmt|;
block|}
DECL|method|printBlockDeletionTime ()
specifier|private
name|void
name|printBlockDeletionTime
parameter_list|()
block|{
name|BlockManager
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"{} is set to {}"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STARTUP_DELAY_BLOCK_DELETION_SEC_KEY
argument_list|,
name|DFSUtil
operator|.
name|durationToString
argument_list|(
name|pendingPeriodInMs
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy MMM dd HH:mm:ss"
argument_list|)
decl_stmt|;
name|Calendar
name|calendar
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|pendingPeriodInMs
operator|/
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|BlockManager
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"The block deletion will start around {}"
argument_list|,
name|sdf
operator|.
name|format
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return The total number of blocks to be invalidated.    */
DECL|method|numBlocks ()
name|long
name|numBlocks
parameter_list|()
block|{
return|return
name|getECBlocks
argument_list|()
operator|+
name|getBlocks
argument_list|()
return|;
block|}
comment|/**    * @return The total number of blocks of type    * {@link org.apache.hadoop.hdfs.protocol.BlockType#CONTIGUOUS}    * to be invalidated.    */
DECL|method|getBlocks ()
name|long
name|getBlocks
parameter_list|()
block|{
return|return
name|numBlocks
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|/**    * @return The total number of blocks of type    * {@link org.apache.hadoop.hdfs.protocol.BlockType#STRIPED}    * to be invalidated.    */
DECL|method|getECBlocks ()
name|long
name|getECBlocks
parameter_list|()
block|{
return|return
name|numECBlocks
operator|.
name|longValue
argument_list|()
return|;
block|}
DECL|method|getBlocksSet (final DatanodeInfo dn)
specifier|private
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|getBlocksSet
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|)
block|{
return|return
name|nodeToBlocks
operator|.
name|get
argument_list|(
name|dn
argument_list|)
return|;
block|}
DECL|method|getECBlocksSet (final DatanodeInfo dn)
specifier|private
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|getECBlocksSet
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|)
block|{
return|return
name|nodeToECBlocks
operator|.
name|get
argument_list|(
name|dn
argument_list|)
return|;
block|}
DECL|method|getBlocksSet (final DatanodeInfo dn, final Block block)
specifier|private
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|getBlocksSet
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|,
specifier|final
name|Block
name|block
parameter_list|)
block|{
if|if
condition|(
name|blockIdManager
operator|.
name|isStripedBlock
argument_list|(
name|block
argument_list|)
condition|)
block|{
return|return
name|getECBlocksSet
argument_list|(
name|dn
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getBlocksSet
argument_list|(
name|dn
argument_list|)
return|;
block|}
block|}
DECL|method|putBlocksSet (final DatanodeInfo dn, final Block block, final LightWeightHashSet set)
specifier|private
name|void
name|putBlocksSet
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|,
specifier|final
name|Block
name|block
parameter_list|,
specifier|final
name|LightWeightHashSet
name|set
parameter_list|)
block|{
if|if
condition|(
name|blockIdManager
operator|.
name|isStripedBlock
argument_list|(
name|block
argument_list|)
condition|)
block|{
assert|assert
name|getECBlocksSet
argument_list|(
name|dn
argument_list|)
operator|==
literal|null
assert|;
name|nodeToECBlocks
operator|.
name|put
argument_list|(
name|dn
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|getBlocksSet
argument_list|(
name|dn
argument_list|)
operator|==
literal|null
assert|;
name|nodeToBlocks
operator|.
name|put
argument_list|(
name|dn
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getBlockSetsSize (final DatanodeInfo dn)
specifier|private
name|long
name|getBlockSetsSize
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|)
block|{
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|replicaBlocks
init|=
name|getBlocksSet
argument_list|(
name|dn
argument_list|)
decl_stmt|;
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|stripedBlocks
init|=
name|getECBlocksSet
argument_list|(
name|dn
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|replicaBlocks
operator|==
literal|null
condition|?
literal|0
else|:
name|replicaBlocks
operator|.
name|size
argument_list|()
operator|)
operator|+
operator|(
name|stripedBlocks
operator|==
literal|null
condition|?
literal|0
else|:
name|stripedBlocks
operator|.
name|size
argument_list|()
operator|)
operator|)
return|;
block|}
comment|/**    * @return true if the given storage has the given block listed for    * invalidation. Blocks are compared including their generation stamps:    * if a block is pending invalidation but with a different generation stamp,    * returns false.    */
DECL|method|contains (final DatanodeInfo dn, final Block block)
specifier|synchronized
name|boolean
name|contains
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|,
specifier|final
name|Block
name|block
parameter_list|)
block|{
specifier|final
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|s
init|=
name|getBlocksSet
argument_list|(
name|dn
argument_list|,
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
comment|// no invalidate blocks for this storage ID
block|}
name|Block
name|blockInSet
init|=
name|s
operator|.
name|getElement
argument_list|(
name|block
argument_list|)
decl_stmt|;
return|return
name|blockInSet
operator|!=
literal|null
operator|&&
name|block
operator|.
name|getGenerationStamp
argument_list|()
operator|==
name|blockInSet
operator|.
name|getGenerationStamp
argument_list|()
return|;
block|}
comment|/**    * Add a block to the block collection which will be    * invalidated on the specified datanode.    */
DECL|method|add (final Block block, final DatanodeInfo datanode, final boolean log)
specifier|synchronized
name|void
name|add
parameter_list|(
specifier|final
name|Block
name|block
parameter_list|,
specifier|final
name|DatanodeInfo
name|datanode
parameter_list|,
specifier|final
name|boolean
name|log
parameter_list|)
block|{
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|set
init|=
name|getBlocksSet
argument_list|(
name|datanode
argument_list|,
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|set
operator|=
operator|new
name|LightWeightHashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|putBlocksSet
argument_list|(
name|datanode
argument_list|,
name|block
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|set
operator|.
name|add
argument_list|(
name|block
argument_list|)
condition|)
block|{
if|if
condition|(
name|blockIdManager
operator|.
name|isStripedBlock
argument_list|(
name|block
argument_list|)
condition|)
block|{
name|numECBlocks
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numBlocks
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|log
condition|)
block|{
name|NameNode
operator|.
name|blockStateChangeLog
operator|.
name|debug
argument_list|(
literal|"BLOCK* {}: add {} to {}"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|block
argument_list|,
name|datanode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Remove a storage from the invalidatesSet */
DECL|method|remove (final DatanodeInfo dn)
specifier|synchronized
name|void
name|remove
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|)
block|{
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|replicaBlockSets
init|=
name|nodeToBlocks
operator|.
name|remove
argument_list|(
name|dn
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicaBlockSets
operator|!=
literal|null
condition|)
block|{
name|numBlocks
operator|.
name|add
argument_list|(
name|replicaBlockSets
operator|.
name|size
argument_list|()
operator|*
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|ecBlocksSet
init|=
name|nodeToECBlocks
operator|.
name|remove
argument_list|(
name|dn
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecBlocksSet
operator|!=
literal|null
condition|)
block|{
name|numECBlocks
operator|.
name|add
argument_list|(
name|ecBlocksSet
operator|.
name|size
argument_list|()
operator|*
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Remove the block from the specified storage. */
DECL|method|remove (final DatanodeInfo dn, final Block block)
specifier|synchronized
name|void
name|remove
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|,
specifier|final
name|Block
name|block
parameter_list|)
block|{
specifier|final
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|v
init|=
name|getBlocksSet
argument_list|(
name|dn
argument_list|,
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|v
operator|.
name|remove
argument_list|(
name|block
argument_list|)
condition|)
block|{
if|if
condition|(
name|blockIdManager
operator|.
name|isStripedBlock
argument_list|(
name|block
argument_list|)
condition|)
block|{
name|numECBlocks
operator|.
name|decrement
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numBlocks
operator|.
name|decrement
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|v
operator|.
name|isEmpty
argument_list|()
operator|&&
name|getBlockSetsSize
argument_list|(
name|dn
argument_list|)
operator|==
literal|0
condition|)
block|{
name|remove
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|dumpBlockSet (final Map<DatanodeInfo, LightWeightHashSet<Block>> nodeToBlocksMap, final PrintWriter out)
specifier|private
name|void
name|dumpBlockSet
parameter_list|(
specifier|final
name|Map
argument_list|<
name|DatanodeInfo
argument_list|,
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
argument_list|>
name|nodeToBlocksMap
parameter_list|,
specifier|final
name|PrintWriter
name|out
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|DatanodeInfo
argument_list|,
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
argument_list|>
name|entry
range|:
name|nodeToBlocksMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|blocks
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|blocks
operator|!=
literal|null
operator|&&
name|blocks
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|join
argument_list|(
literal|','
argument_list|,
name|blocks
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Print the contents to out. */
DECL|method|dump (final PrintWriter out)
specifier|synchronized
name|void
name|dump
parameter_list|(
specifier|final
name|PrintWriter
name|out
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|nodeToBlocks
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|nodeToECBlocks
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Metasave: Blocks "
operator|+
name|numBlocks
argument_list|()
operator|+
literal|" waiting deletion from "
operator|+
name|size
operator|+
literal|" datanodes."
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|dumpBlockSet
argument_list|(
name|nodeToBlocks
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|dumpBlockSet
argument_list|(
name|nodeToECBlocks
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
comment|/** @return a list of the storage IDs. */
DECL|method|getDatanodes ()
specifier|synchronized
name|List
argument_list|<
name|DatanodeInfo
argument_list|>
name|getDatanodes
parameter_list|()
block|{
name|HashSet
argument_list|<
name|DatanodeInfo
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|nodeToBlocks
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|nodeToECBlocks
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|set
argument_list|)
return|;
block|}
comment|/**    * @return the remianing pending time    */
annotation|@
name|VisibleForTesting
DECL|method|getInvalidationDelay ()
name|long
name|getInvalidationDelay
parameter_list|()
block|{
return|return
name|pendingPeriodInMs
operator|-
operator|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startupTime
operator|)
return|;
block|}
comment|/**    * Get blocks to invalidate by limit as blocks that can be sent in one    * message is limited.    * @return the remaining limit    */
DECL|method|getBlocksToInvalidateByLimit (LightWeightHashSet<Block> blockSet, List<Block> toInvalidate, LongAdder statsAdder, int limit)
specifier|private
name|int
name|getBlocksToInvalidateByLimit
parameter_list|(
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|blockSet
parameter_list|,
name|List
argument_list|<
name|Block
argument_list|>
name|toInvalidate
parameter_list|,
name|LongAdder
name|statsAdder
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
assert|assert
name|blockSet
operator|!=
literal|null
assert|;
name|int
name|remainingLimit
init|=
name|limit
decl_stmt|;
name|List
argument_list|<
name|Block
argument_list|>
name|polledBlocks
init|=
name|blockSet
operator|.
name|pollN
argument_list|(
name|limit
argument_list|)
decl_stmt|;
name|remainingLimit
operator|-=
name|polledBlocks
operator|.
name|size
argument_list|()
expr_stmt|;
name|toInvalidate
operator|.
name|addAll
argument_list|(
name|polledBlocks
argument_list|)
expr_stmt|;
name|statsAdder
operator|.
name|add
argument_list|(
name|polledBlocks
operator|.
name|size
argument_list|()
operator|*
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|remainingLimit
return|;
block|}
DECL|method|invalidateWork (final DatanodeDescriptor dn)
specifier|synchronized
name|List
argument_list|<
name|Block
argument_list|>
name|invalidateWork
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
specifier|final
name|long
name|delay
init|=
name|getInvalidationDelay
argument_list|()
decl_stmt|;
if|if
condition|(
name|delay
operator|>
literal|0
condition|)
block|{
name|BlockManager
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Block deletion is delayed during NameNode startup. "
operator|+
literal|"The deletion will start after {} ms."
argument_list|,
name|delay
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|int
name|remainingLimit
init|=
name|blockInvalidateLimit
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Block
argument_list|>
name|toInvalidate
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeToBlocks
operator|.
name|get
argument_list|(
name|dn
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|remainingLimit
operator|=
name|getBlocksToInvalidateByLimit
argument_list|(
name|nodeToBlocks
operator|.
name|get
argument_list|(
name|dn
argument_list|)
argument_list|,
name|toInvalidate
argument_list|,
name|numBlocks
argument_list|,
name|remainingLimit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|remainingLimit
operator|>
literal|0
operator|)
operator|&&
operator|(
name|nodeToECBlocks
operator|.
name|get
argument_list|(
name|dn
argument_list|)
operator|!=
literal|null
operator|)
condition|)
block|{
name|getBlocksToInvalidateByLimit
argument_list|(
name|nodeToECBlocks
operator|.
name|get
argument_list|(
name|dn
argument_list|)
argument_list|,
name|toInvalidate
argument_list|,
name|numECBlocks
argument_list|,
name|remainingLimit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|toInvalidate
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|getBlockSetsSize
argument_list|(
name|dn
argument_list|)
operator|==
literal|0
condition|)
block|{
name|remove
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
name|dn
operator|.
name|addBlocksToBeInvalidated
argument_list|(
name|toInvalidate
argument_list|)
expr_stmt|;
block|}
return|return
name|toInvalidate
return|;
block|}
DECL|method|clear ()
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|nodeToBlocks
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nodeToECBlocks
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numBlocks
operator|.
name|reset
argument_list|()
expr_stmt|;
name|numECBlocks
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

