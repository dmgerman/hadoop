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
name|java
operator|.
name|util
operator|.
name|NavigableSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|NameNode
import|;
end_import

begin_comment
comment|/**  * Keep prioritized queues of under replicated blocks.  * Blocks have replication priority, with priority {@link #QUEUE_HIGHEST_PRIORITY}  * indicating the highest priority.  *</p>  * Having a prioritised queue allows the {@link BlockManager} to select  * which blocks to replicate first -it tries to give priority to data  * that is most at risk or considered most valuable.  *  *<p/>  * The policy for choosing which priority to give added blocks  * is implemented in {@link #getPriority(Block, int, int, int)}.  *</p>  *<p>The queue order is as follows:</p>  *<ol>  *<li>{@link #QUEUE_HIGHEST_PRIORITY}: the blocks that must be replicated  *   first. That is blocks with only one copy, or blocks with zero live  *   copies but a copy in a node being decommissioned. These blocks  *   are at risk of loss if the disk or server on which they  *   remain fails.</li>  *<li>{@link #QUEUE_VERY_UNDER_REPLICATED}: blocks that are very  *   under-replicated compared to their expected values. Currently  *   that means the ratio of the ratio of actual:expected means that  *   there is<i>less than</i> 1:3.</li>. These blocks may not be at risk,  *   but they are clearly considered "important".  *<li>{@link #QUEUE_UNDER_REPLICATED}: blocks that are also under  *   replicated, and the ratio of actual:expected is good enough that  *   they do not need to go into the {@link #QUEUE_VERY_UNDER_REPLICATED}  *   queue.</li>  *<li>{@link #QUEUE_REPLICAS_BADLY_DISTRIBUTED}: there are as least as  *   many copies of a block as required, but the blocks are not adequately  *   distributed. Loss of a rack/switch could take all copies off-line.</li>  *<li>{@link #QUEUE_WITH_CORRUPT_BLOCKS} This is for blocks that are corrupt  *   and for which there are no-non-corrupt copies (currently) available.  *   The policy here is to keep those corrupt blocks replicated, but give  *   blocks that are not corrupt higher priority.</li>  *</ol>  */
end_comment

begin_class
DECL|class|UnderReplicatedBlocks
class|class
name|UnderReplicatedBlocks
implements|implements
name|Iterable
argument_list|<
name|Block
argument_list|>
block|{
comment|/** The total number of queues : {@value} */
DECL|field|LEVEL
specifier|static
specifier|final
name|int
name|LEVEL
init|=
literal|5
decl_stmt|;
comment|/** The queue with the highest priority: {@value} */
DECL|field|QUEUE_HIGHEST_PRIORITY
specifier|static
specifier|final
name|int
name|QUEUE_HIGHEST_PRIORITY
init|=
literal|0
decl_stmt|;
comment|/** The queue for blocks that are way below their expected value : {@value} */
DECL|field|QUEUE_VERY_UNDER_REPLICATED
specifier|static
specifier|final
name|int
name|QUEUE_VERY_UNDER_REPLICATED
init|=
literal|1
decl_stmt|;
comment|/** The queue for "normally" under-replicated blocks: {@value} */
DECL|field|QUEUE_UNDER_REPLICATED
specifier|static
specifier|final
name|int
name|QUEUE_UNDER_REPLICATED
init|=
literal|2
decl_stmt|;
comment|/** The queue for blocks that have the right number of replicas,    * but which the block manager felt were badly distributed: {@value}    */
DECL|field|QUEUE_REPLICAS_BADLY_DISTRIBUTED
specifier|static
specifier|final
name|int
name|QUEUE_REPLICAS_BADLY_DISTRIBUTED
init|=
literal|3
decl_stmt|;
comment|/** The queue for corrupt blocks: {@value} */
DECL|field|QUEUE_WITH_CORRUPT_BLOCKS
specifier|static
specifier|final
name|int
name|QUEUE_WITH_CORRUPT_BLOCKS
init|=
literal|4
decl_stmt|;
comment|/** the queues themselves */
DECL|field|priorityQueues
specifier|private
specifier|final
name|List
argument_list|<
name|NavigableSet
argument_list|<
name|Block
argument_list|>
argument_list|>
name|priorityQueues
init|=
operator|new
name|ArrayList
argument_list|<
name|NavigableSet
argument_list|<
name|Block
argument_list|>
argument_list|>
argument_list|(
name|LEVEL
argument_list|)
decl_stmt|;
comment|/** Create an object. */
DECL|method|UnderReplicatedBlocks ()
name|UnderReplicatedBlocks
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|LEVEL
condition|;
name|i
operator|++
control|)
block|{
name|priorityQueues
operator|.
name|add
argument_list|(
operator|new
name|TreeSet
argument_list|<
name|Block
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Empty the queues.    */
DECL|method|clear ()
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|LEVEL
condition|;
name|i
operator|++
control|)
block|{
name|priorityQueues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Return the total number of under replication blocks */
DECL|method|size ()
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
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
name|LEVEL
condition|;
name|i
operator|++
control|)
block|{
name|size
operator|+=
name|priorityQueues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
comment|/** Return the number of under replication blocks excluding corrupt blocks */
DECL|method|getUnderReplicatedBlockCount ()
specifier|synchronized
name|int
name|getUnderReplicatedBlockCount
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
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
name|LEVEL
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
name|QUEUE_WITH_CORRUPT_BLOCKS
condition|)
block|{
name|size
operator|+=
name|priorityQueues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|size
return|;
block|}
comment|/** Return the number of corrupt blocks */
DECL|method|getCorruptBlockSize ()
specifier|synchronized
name|int
name|getCorruptBlockSize
parameter_list|()
block|{
return|return
name|priorityQueues
operator|.
name|get
argument_list|(
name|QUEUE_WITH_CORRUPT_BLOCKS
argument_list|)
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** Check if a block is in the neededReplication queue */
DECL|method|contains (Block block)
specifier|synchronized
name|boolean
name|contains
parameter_list|(
name|Block
name|block
parameter_list|)
block|{
for|for
control|(
name|NavigableSet
argument_list|<
name|Block
argument_list|>
name|set
range|:
name|priorityQueues
control|)
block|{
if|if
condition|(
name|set
operator|.
name|contains
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
return|return
literal|false
return|;
block|}
comment|/** Return the priority of a block    * @param block a under replicated block    * @param curReplicas current number of replicas of the block    * @param expectedReplicas expected number of replicas of the block    * @return the priority for the blocks, between 0 and ({@link #LEVEL}-1)    */
DECL|method|getPriority (Block block, int curReplicas, int decommissionedReplicas, int expectedReplicas)
specifier|private
name|int
name|getPriority
parameter_list|(
name|Block
name|block
parameter_list|,
name|int
name|curReplicas
parameter_list|,
name|int
name|decommissionedReplicas
parameter_list|,
name|int
name|expectedReplicas
parameter_list|)
block|{
assert|assert
name|curReplicas
operator|>=
literal|0
operator|:
literal|"Negative replicas!"
assert|;
if|if
condition|(
name|curReplicas
operator|>=
name|expectedReplicas
condition|)
block|{
comment|// Block has enough copies, but not enough racks
return|return
name|QUEUE_REPLICAS_BADLY_DISTRIBUTED
return|;
block|}
elseif|else
if|if
condition|(
name|curReplicas
operator|==
literal|0
condition|)
block|{
comment|// If there are zero non-decommissioned replicas but there are
comment|// some decommissioned replicas, then assign them highest priority
if|if
condition|(
name|decommissionedReplicas
operator|>
literal|0
condition|)
block|{
return|return
name|QUEUE_HIGHEST_PRIORITY
return|;
block|}
comment|//all we have are corrupt blocks
return|return
name|QUEUE_WITH_CORRUPT_BLOCKS
return|;
block|}
elseif|else
if|if
condition|(
name|curReplicas
operator|==
literal|1
condition|)
block|{
comment|//only on replica -risk of loss
comment|// highest priority
return|return
name|QUEUE_HIGHEST_PRIORITY
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|curReplicas
operator|*
literal|3
operator|)
operator|<
name|expectedReplicas
condition|)
block|{
comment|//there is less than a third as many blocks as requested;
comment|//this is considered very under-replicated
return|return
name|QUEUE_VERY_UNDER_REPLICATED
return|;
block|}
else|else
block|{
comment|//add to the normal queue for under replicated blocks
return|return
name|QUEUE_UNDER_REPLICATED
return|;
block|}
block|}
comment|/** add a block to a under replication queue according to its priority    * @param block a under replication block    * @param curReplicas current number of replicas of the block    * @param decomissionedReplicas the number of decommissioned replicas    * @param expectedReplicas expected number of replicas of the block    * @return true if the block was added to a queue.    */
DECL|method|add (Block block, int curReplicas, int decomissionedReplicas, int expectedReplicas)
specifier|synchronized
name|boolean
name|add
parameter_list|(
name|Block
name|block
parameter_list|,
name|int
name|curReplicas
parameter_list|,
name|int
name|decomissionedReplicas
parameter_list|,
name|int
name|expectedReplicas
parameter_list|)
block|{
assert|assert
name|curReplicas
operator|>=
literal|0
operator|:
literal|"Negative replicas!"
assert|;
name|int
name|priLevel
init|=
name|getPriority
argument_list|(
name|block
argument_list|,
name|curReplicas
argument_list|,
name|decomissionedReplicas
argument_list|,
name|expectedReplicas
argument_list|)
decl_stmt|;
if|if
condition|(
name|priLevel
operator|!=
name|LEVEL
operator|&&
name|priorityQueues
operator|.
name|get
argument_list|(
name|priLevel
argument_list|)
operator|.
name|add
argument_list|(
name|block
argument_list|)
condition|)
block|{
if|if
condition|(
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"BLOCK* NameSystem.UnderReplicationBlock.add:"
operator|+
name|block
operator|+
literal|" has only "
operator|+
name|curReplicas
operator|+
literal|" replicas and need "
operator|+
name|expectedReplicas
operator|+
literal|" replicas so is added to neededReplications"
operator|+
literal|" at priority level "
operator|+
name|priLevel
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** remove a block from a under replication queue */
DECL|method|remove (Block block, int oldReplicas, int decommissionedReplicas, int oldExpectedReplicas)
specifier|synchronized
name|boolean
name|remove
parameter_list|(
name|Block
name|block
parameter_list|,
name|int
name|oldReplicas
parameter_list|,
name|int
name|decommissionedReplicas
parameter_list|,
name|int
name|oldExpectedReplicas
parameter_list|)
block|{
name|int
name|priLevel
init|=
name|getPriority
argument_list|(
name|block
argument_list|,
name|oldReplicas
argument_list|,
name|decommissionedReplicas
argument_list|,
name|oldExpectedReplicas
argument_list|)
decl_stmt|;
return|return
name|remove
argument_list|(
name|block
argument_list|,
name|priLevel
argument_list|)
return|;
block|}
comment|/**    * Remove a block from the under replication queues.    *    * The priLevel parameter is a hint of which queue to query    * first: if negative or&gt;= {@link #LEVEL} this shortcutting    * is not attmpted.    *    * If the block is not found in the nominated queue, an attempt is made to    * remove it from all queues.    *    *<i>Warning:</i> This is not a synchronized method.    * @param block block to remove    * @param priLevel expected privilege level    * @return true if the block was found and removed from one of the priority queues    */
DECL|method|remove (Block block, int priLevel)
name|boolean
name|remove
parameter_list|(
name|Block
name|block
parameter_list|,
name|int
name|priLevel
parameter_list|)
block|{
if|if
condition|(
name|priLevel
operator|>=
literal|0
operator|&&
name|priLevel
operator|<
name|LEVEL
operator|&&
name|priorityQueues
operator|.
name|get
argument_list|(
name|priLevel
argument_list|)
operator|.
name|remove
argument_list|(
name|block
argument_list|)
condition|)
block|{
if|if
condition|(
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"BLOCK* NameSystem.UnderReplicationBlock.remove: "
operator|+
literal|"Removing block "
operator|+
name|block
operator|+
literal|" from priority queue "
operator|+
name|priLevel
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// Try to remove the block from all queues if the block was
comment|// not found in the queue for the given priority level.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|LEVEL
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|priorityQueues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|remove
argument_list|(
name|block
argument_list|)
condition|)
block|{
if|if
condition|(
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"BLOCK* NameSystem.UnderReplicationBlock.remove: "
operator|+
literal|"Removing block "
operator|+
name|block
operator|+
literal|" from priority queue "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Recalculate and potentially update the priority level of a block.    *    * If the block priority has changed from before an attempt is made to    * remove it from the block queue. Regardless of whether or not the block    * is in the block queue of (recalculate) priority, an attempt is made    * to add it to that queue. This ensures that the block will be    * in its expected priority queue (and only that queue) by the end of the    * method call.    * @param block a under replicated block    * @param curReplicas current number of replicas of the block    * @param decommissionedReplicas  the number of decommissioned replicas    * @param curExpectedReplicas expected number of replicas of the block    * @param curReplicasDelta the change in the replicate count from before    * @param expectedReplicasDelta the change in the expected replica count from before    */
DECL|method|update (Block block, int curReplicas, int decommissionedReplicas, int curExpectedReplicas, int curReplicasDelta, int expectedReplicasDelta)
specifier|synchronized
name|void
name|update
parameter_list|(
name|Block
name|block
parameter_list|,
name|int
name|curReplicas
parameter_list|,
name|int
name|decommissionedReplicas
parameter_list|,
name|int
name|curExpectedReplicas
parameter_list|,
name|int
name|curReplicasDelta
parameter_list|,
name|int
name|expectedReplicasDelta
parameter_list|)
block|{
name|int
name|oldReplicas
init|=
name|curReplicas
operator|-
name|curReplicasDelta
decl_stmt|;
name|int
name|oldExpectedReplicas
init|=
name|curExpectedReplicas
operator|-
name|expectedReplicasDelta
decl_stmt|;
name|int
name|curPri
init|=
name|getPriority
argument_list|(
name|block
argument_list|,
name|curReplicas
argument_list|,
name|decommissionedReplicas
argument_list|,
name|curExpectedReplicas
argument_list|)
decl_stmt|;
name|int
name|oldPri
init|=
name|getPriority
argument_list|(
name|block
argument_list|,
name|oldReplicas
argument_list|,
name|decommissionedReplicas
argument_list|,
name|oldExpectedReplicas
argument_list|)
decl_stmt|;
if|if
condition|(
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"UnderReplicationBlocks.update "
operator|+
name|block
operator|+
literal|" curReplicas "
operator|+
name|curReplicas
operator|+
literal|" curExpectedReplicas "
operator|+
name|curExpectedReplicas
operator|+
literal|" oldReplicas "
operator|+
name|oldReplicas
operator|+
literal|" oldExpectedReplicas  "
operator|+
name|oldExpectedReplicas
operator|+
literal|" curPri  "
operator|+
name|curPri
operator|+
literal|" oldPri  "
operator|+
name|oldPri
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|oldPri
operator|!=
name|LEVEL
operator|&&
name|oldPri
operator|!=
name|curPri
condition|)
block|{
name|remove
argument_list|(
name|block
argument_list|,
name|oldPri
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|curPri
operator|!=
name|LEVEL
operator|&&
name|priorityQueues
operator|.
name|get
argument_list|(
name|curPri
argument_list|)
operator|.
name|add
argument_list|(
name|block
argument_list|)
condition|)
block|{
if|if
condition|(
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"BLOCK* NameSystem.UnderReplicationBlock.update:"
operator|+
name|block
operator|+
literal|" has only "
operator|+
name|curReplicas
operator|+
literal|" replicas and needs "
operator|+
name|curExpectedReplicas
operator|+
literal|" replicas so is added to neededReplications"
operator|+
literal|" at priority level "
operator|+
name|curPri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** returns an iterator of all blocks in a given priority queue */
DECL|method|iterator (int level)
specifier|synchronized
name|BlockIterator
name|iterator
parameter_list|(
name|int
name|level
parameter_list|)
block|{
return|return
operator|new
name|BlockIterator
argument_list|(
name|level
argument_list|)
return|;
block|}
comment|/** return an iterator of all the under replication blocks */
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
specifier|synchronized
name|BlockIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|BlockIterator
argument_list|()
return|;
block|}
comment|/**    * An iterator over blocks.    */
DECL|class|BlockIterator
class|class
name|BlockIterator
implements|implements
name|Iterator
argument_list|<
name|Block
argument_list|>
block|{
DECL|field|level
specifier|private
name|int
name|level
decl_stmt|;
DECL|field|isIteratorForLevel
specifier|private
name|boolean
name|isIteratorForLevel
init|=
literal|false
decl_stmt|;
DECL|field|iterators
specifier|private
name|List
argument_list|<
name|Iterator
argument_list|<
name|Block
argument_list|>
argument_list|>
name|iterators
init|=
operator|new
name|ArrayList
argument_list|<
name|Iterator
argument_list|<
name|Block
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Construct an iterator over all queues.      */
DECL|method|BlockIterator ()
specifier|private
name|BlockIterator
parameter_list|()
block|{
name|level
operator|=
literal|0
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
name|LEVEL
condition|;
name|i
operator|++
control|)
block|{
name|iterators
operator|.
name|add
argument_list|(
name|priorityQueues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Constrict an iterator for a single queue level      * @param l the priority level to iterate over      */
DECL|method|BlockIterator (int l)
specifier|private
name|BlockIterator
parameter_list|(
name|int
name|l
parameter_list|)
block|{
name|level
operator|=
name|l
expr_stmt|;
name|isIteratorForLevel
operator|=
literal|true
expr_stmt|;
name|iterators
operator|.
name|add
argument_list|(
name|priorityQueues
operator|.
name|get
argument_list|(
name|level
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|update ()
specifier|private
name|void
name|update
parameter_list|()
block|{
if|if
condition|(
name|isIteratorForLevel
condition|)
block|{
return|return;
block|}
while|while
condition|(
name|level
operator|<
name|LEVEL
operator|-
literal|1
operator|&&
operator|!
name|iterators
operator|.
name|get
argument_list|(
name|level
argument_list|)
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|level
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|Block
name|next
parameter_list|()
block|{
if|if
condition|(
name|isIteratorForLevel
condition|)
block|{
return|return
name|iterators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|next
argument_list|()
return|;
block|}
name|update
argument_list|()
expr_stmt|;
return|return
name|iterators
operator|.
name|get
argument_list|(
name|level
argument_list|)
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|isIteratorForLevel
condition|)
block|{
return|return
name|iterators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|hasNext
argument_list|()
return|;
block|}
name|update
argument_list|()
expr_stmt|;
return|return
name|iterators
operator|.
name|get
argument_list|(
name|level
argument_list|)
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|isIteratorForLevel
condition|)
block|{
name|iterators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|iterators
operator|.
name|get
argument_list|(
name|level
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getPriority ()
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|level
return|;
block|}
block|}
block|}
end_class

end_unit

