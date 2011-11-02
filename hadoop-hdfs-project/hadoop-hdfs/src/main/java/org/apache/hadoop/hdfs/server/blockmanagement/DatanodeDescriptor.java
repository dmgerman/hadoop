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
name|DataInput
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
name|Iterator
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
name|Queue
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
name|classification
operator|.
name|InterfaceStability
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
name|DeprecatedUTF8
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|WritableUtils
import|;
end_import

begin_comment
comment|/**************************************************  * DatanodeDescriptor tracks stats on a given DataNode, such as  * available storage capacity, last update time, etc., and maintains a  * set of blocks stored on the datanode.  *  * This data structure is internal to the namenode. It is *not* sent  * over-the-wire to the Client or the Datanodes. Neither is it stored  * persistently in the fsImage.  **************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DatanodeDescriptor
specifier|public
class|class
name|DatanodeDescriptor
extends|extends
name|DatanodeInfo
block|{
comment|// Stores status of decommissioning.
comment|// If node is not decommissioning, do not use this object for anything.
DECL|field|decommissioningStatus
specifier|public
name|DecommissioningStatus
name|decommissioningStatus
init|=
operator|new
name|DecommissioningStatus
argument_list|()
decl_stmt|;
comment|/** Block and targets pair */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|BlockTargetPair
specifier|public
specifier|static
class|class
name|BlockTargetPair
block|{
DECL|field|block
specifier|public
specifier|final
name|Block
name|block
decl_stmt|;
DECL|field|targets
specifier|public
specifier|final
name|DatanodeDescriptor
index|[]
name|targets
decl_stmt|;
DECL|method|BlockTargetPair (Block block, DatanodeDescriptor[] targets)
name|BlockTargetPair
parameter_list|(
name|Block
name|block
parameter_list|,
name|DatanodeDescriptor
index|[]
name|targets
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|targets
operator|=
name|targets
expr_stmt|;
block|}
block|}
comment|/** A BlockTargetPair queue. */
DECL|class|BlockQueue
specifier|private
specifier|static
class|class
name|BlockQueue
parameter_list|<
name|E
parameter_list|>
block|{
DECL|field|blockq
specifier|private
specifier|final
name|Queue
argument_list|<
name|E
argument_list|>
name|blockq
init|=
operator|new
name|LinkedList
argument_list|<
name|E
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Size of the queue */
DECL|method|size ()
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|blockq
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** Enqueue */
DECL|method|offer (E e)
specifier|synchronized
name|boolean
name|offer
parameter_list|(
name|E
name|e
parameter_list|)
block|{
return|return
name|blockq
operator|.
name|offer
argument_list|(
name|e
argument_list|)
return|;
block|}
comment|/** Dequeue */
DECL|method|poll (int numBlocks)
specifier|synchronized
name|List
argument_list|<
name|E
argument_list|>
name|poll
parameter_list|(
name|int
name|numBlocks
parameter_list|)
block|{
if|if
condition|(
name|numBlocks
operator|<=
literal|0
operator|||
name|blockq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|E
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|E
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
init|;
operator|!
name|blockq
operator|.
name|isEmpty
argument_list|()
operator|&&
name|numBlocks
operator|>
literal|0
condition|;
name|numBlocks
operator|--
control|)
block|{
name|results
operator|.
name|add
argument_list|(
name|blockq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
comment|/**      * Returns<tt>true</tt> if the queue contains the specified element.      */
DECL|method|contains (E e)
name|boolean
name|contains
parameter_list|(
name|E
name|e
parameter_list|)
block|{
return|return
name|blockq
operator|.
name|contains
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
DECL|field|blockList
specifier|private
specifier|volatile
name|BlockInfo
name|blockList
init|=
literal|null
decl_stmt|;
DECL|field|numBlocks
specifier|private
name|int
name|numBlocks
init|=
literal|0
decl_stmt|;
comment|// isAlive == heartbeats.contains(this)
comment|// This is an optimization, because contains takes O(n) time on Arraylist
DECL|field|isAlive
specifier|public
name|boolean
name|isAlive
init|=
literal|false
decl_stmt|;
DECL|field|needKeyUpdate
specifier|public
name|boolean
name|needKeyUpdate
init|=
literal|false
decl_stmt|;
comment|// A system administrator can tune the balancer bandwidth parameter
comment|// (dfs.balance.bandwidthPerSec) dynamically by calling
comment|// "dfsadmin -setBalanacerBandwidth<newbandwidth>", at which point the
comment|// following 'bandwidth' variable gets updated with the new value for each
comment|// node. Once the heartbeat command is issued to update the value on the
comment|// specified datanode, this value will be set back to 0.
DECL|field|bandwidth
specifier|private
name|long
name|bandwidth
decl_stmt|;
comment|/** A queue of blocks to be replicated by this datanode */
DECL|field|replicateBlocks
specifier|private
name|BlockQueue
argument_list|<
name|BlockTargetPair
argument_list|>
name|replicateBlocks
init|=
operator|new
name|BlockQueue
argument_list|<
name|BlockTargetPair
argument_list|>
argument_list|()
decl_stmt|;
comment|/** A queue of blocks to be recovered by this datanode */
DECL|field|recoverBlocks
specifier|private
name|BlockQueue
argument_list|<
name|BlockInfoUnderConstruction
argument_list|>
name|recoverBlocks
init|=
operator|new
name|BlockQueue
argument_list|<
name|BlockInfoUnderConstruction
argument_list|>
argument_list|()
decl_stmt|;
comment|/** A set of blocks to be invalidated by this datanode */
DECL|field|invalidateBlocks
specifier|private
name|Set
argument_list|<
name|Block
argument_list|>
name|invalidateBlocks
init|=
operator|new
name|TreeSet
argument_list|<
name|Block
argument_list|>
argument_list|()
decl_stmt|;
comment|/* Variables for maintaining number of blocks scheduled to be written to    * this datanode. This count is approximate and might be slightly bigger    * in case of errors (e.g. datanode does not report if an error occurs     * while writing the block).    */
DECL|field|currApproxBlocksScheduled
specifier|private
name|int
name|currApproxBlocksScheduled
init|=
literal|0
decl_stmt|;
DECL|field|prevApproxBlocksScheduled
specifier|private
name|int
name|prevApproxBlocksScheduled
init|=
literal|0
decl_stmt|;
DECL|field|lastBlocksScheduledRollTime
specifier|private
name|long
name|lastBlocksScheduledRollTime
init|=
literal|0
decl_stmt|;
DECL|field|BLOCKS_SCHEDULED_ROLL_INTERVAL
specifier|private
specifier|static
specifier|final
name|int
name|BLOCKS_SCHEDULED_ROLL_INTERVAL
init|=
literal|600
operator|*
literal|1000
decl_stmt|;
comment|//10min
DECL|field|volumeFailures
specifier|private
name|int
name|volumeFailures
init|=
literal|0
decl_stmt|;
comment|/**     * When set to true, the node is not in include list and is not allowed    * to communicate with the namenode    */
DECL|field|disallowed
specifier|private
name|boolean
name|disallowed
init|=
literal|false
decl_stmt|;
comment|/** Default constructor */
DECL|method|DatanodeDescriptor ()
specifier|public
name|DatanodeDescriptor
parameter_list|()
block|{}
comment|/** DatanodeDescriptor constructor    * @param nodeID id of the data node    */
DECL|method|DatanodeDescriptor (DatanodeID nodeID)
specifier|public
name|DatanodeDescriptor
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|)
block|{
name|this
argument_list|(
name|nodeID
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** DatanodeDescriptor constructor    *     * @param nodeID id of the data node    * @param networkLocation location of the data node in network    */
DECL|method|DatanodeDescriptor (DatanodeID nodeID, String networkLocation)
specifier|public
name|DatanodeDescriptor
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|String
name|networkLocation
parameter_list|)
block|{
name|this
argument_list|(
name|nodeID
argument_list|,
name|networkLocation
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** DatanodeDescriptor constructor    *     * @param nodeID id of the data node    * @param networkLocation location of the data node in network    * @param hostName it could be different from host specified for DatanodeID    */
DECL|method|DatanodeDescriptor (DatanodeID nodeID, String networkLocation, String hostName)
specifier|public
name|DatanodeDescriptor
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|String
name|networkLocation
parameter_list|,
name|String
name|hostName
parameter_list|)
block|{
name|this
argument_list|(
name|nodeID
argument_list|,
name|networkLocation
argument_list|,
name|hostName
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** DatanodeDescriptor constructor    *     * @param nodeID id of the data node    * @param capacity capacity of the data node    * @param dfsUsed space used by the data node    * @param remaining remaining capacity of the data node    * @param bpused space used by the block pool corresponding to this namenode    * @param xceiverCount # of data transfers at the data node    */
DECL|method|DatanodeDescriptor (DatanodeID nodeID, long capacity, long dfsUsed, long remaining, long bpused, int xceiverCount, int failedVolumes)
specifier|public
name|DatanodeDescriptor
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|long
name|capacity
parameter_list|,
name|long
name|dfsUsed
parameter_list|,
name|long
name|remaining
parameter_list|,
name|long
name|bpused
parameter_list|,
name|int
name|xceiverCount
parameter_list|,
name|int
name|failedVolumes
parameter_list|)
block|{
name|super
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
name|updateHeartbeat
argument_list|(
name|capacity
argument_list|,
name|dfsUsed
argument_list|,
name|remaining
argument_list|,
name|bpused
argument_list|,
name|xceiverCount
argument_list|,
name|failedVolumes
argument_list|)
expr_stmt|;
block|}
comment|/** DatanodeDescriptor constructor    *     * @param nodeID id of the data node    * @param networkLocation location of the data node in network    * @param capacity capacity of the data node, including space used by non-dfs    * @param dfsUsed the used space by dfs datanode    * @param remaining remaining capacity of the data node    * @param bpused space used by the block pool corresponding to this namenode    * @param xceiverCount # of data transfers at the data node    */
DECL|method|DatanodeDescriptor (DatanodeID nodeID, String networkLocation, String hostName, long capacity, long dfsUsed, long remaining, long bpused, int xceiverCount, int failedVolumes)
specifier|public
name|DatanodeDescriptor
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|String
name|networkLocation
parameter_list|,
name|String
name|hostName
parameter_list|,
name|long
name|capacity
parameter_list|,
name|long
name|dfsUsed
parameter_list|,
name|long
name|remaining
parameter_list|,
name|long
name|bpused
parameter_list|,
name|int
name|xceiverCount
parameter_list|,
name|int
name|failedVolumes
parameter_list|)
block|{
name|super
argument_list|(
name|nodeID
argument_list|,
name|networkLocation
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
name|updateHeartbeat
argument_list|(
name|capacity
argument_list|,
name|dfsUsed
argument_list|,
name|remaining
argument_list|,
name|bpused
argument_list|,
name|xceiverCount
argument_list|,
name|failedVolumes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add datanode to the block.    * Add block to the head of the list of blocks belonging to the data-node.    */
DECL|method|addBlock (BlockInfo b)
specifier|public
name|boolean
name|addBlock
parameter_list|(
name|BlockInfo
name|b
parameter_list|)
block|{
if|if
condition|(
operator|!
name|b
operator|.
name|addNode
argument_list|(
name|this
argument_list|)
condition|)
return|return
literal|false
return|;
comment|// add to the head of the data-node list
name|blockList
operator|=
name|b
operator|.
name|listInsert
argument_list|(
name|blockList
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|numBlocks
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Remove block from the list of blocks belonging to the data-node.    * Remove datanode from the block.    */
DECL|method|removeBlock (BlockInfo b)
specifier|public
name|boolean
name|removeBlock
parameter_list|(
name|BlockInfo
name|b
parameter_list|)
block|{
name|blockList
operator|=
name|b
operator|.
name|listRemove
argument_list|(
name|blockList
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|.
name|removeNode
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|numBlocks
operator|--
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Move block to the head of the list of blocks belonging to the data-node.    * @return the index of the head of the blockList    */
DECL|method|moveBlockToHead (BlockInfo b, int curIndex, int headIndex)
name|int
name|moveBlockToHead
parameter_list|(
name|BlockInfo
name|b
parameter_list|,
name|int
name|curIndex
parameter_list|,
name|int
name|headIndex
parameter_list|)
block|{
name|blockList
operator|=
name|b
operator|.
name|moveBlockToHead
argument_list|(
name|blockList
argument_list|,
name|this
argument_list|,
name|curIndex
argument_list|,
name|headIndex
argument_list|)
expr_stmt|;
return|return
name|curIndex
return|;
block|}
comment|/**    * Used for testing only    * @return the head of the blockList    */
DECL|method|getHead ()
specifier|protected
name|BlockInfo
name|getHead
parameter_list|()
block|{
return|return
name|blockList
return|;
block|}
comment|/**    * Replace specified old block with a new one in the DataNodeDescriptor.    *    * @param oldBlock - block to be replaced    * @param newBlock - a replacement block    * @return the new block    */
DECL|method|replaceBlock (BlockInfo oldBlock, BlockInfo newBlock)
specifier|public
name|BlockInfo
name|replaceBlock
parameter_list|(
name|BlockInfo
name|oldBlock
parameter_list|,
name|BlockInfo
name|newBlock
parameter_list|)
block|{
name|boolean
name|done
init|=
name|removeBlock
argument_list|(
name|oldBlock
argument_list|)
decl_stmt|;
assert|assert
name|done
operator|:
literal|"Old block should belong to the data-node when replacing"
assert|;
name|done
operator|=
name|addBlock
argument_list|(
name|newBlock
argument_list|)
expr_stmt|;
assert|assert
name|done
operator|:
literal|"New block should not belong to the data-node when replacing"
assert|;
return|return
name|newBlock
return|;
block|}
DECL|method|resetBlocks ()
specifier|public
name|void
name|resetBlocks
parameter_list|()
block|{
name|this
operator|.
name|capacity
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|blockPoolUsed
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|dfsUsed
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|xceiverCount
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|blockList
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|invalidateBlocks
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|volumeFailures
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|numBlocks ()
specifier|public
name|int
name|numBlocks
parameter_list|()
block|{
return|return
name|numBlocks
return|;
block|}
comment|/**    * Updates stats from datanode heartbeat.    */
DECL|method|updateHeartbeat (long capacity, long dfsUsed, long remaining, long blockPoolUsed, int xceiverCount, int volFailures)
specifier|public
name|void
name|updateHeartbeat
parameter_list|(
name|long
name|capacity
parameter_list|,
name|long
name|dfsUsed
parameter_list|,
name|long
name|remaining
parameter_list|,
name|long
name|blockPoolUsed
parameter_list|,
name|int
name|xceiverCount
parameter_list|,
name|int
name|volFailures
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
name|dfsUsed
operator|=
name|dfsUsed
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
name|remaining
expr_stmt|;
name|this
operator|.
name|blockPoolUsed
operator|=
name|blockPoolUsed
expr_stmt|;
name|this
operator|.
name|lastUpdate
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|xceiverCount
operator|=
name|xceiverCount
expr_stmt|;
name|this
operator|.
name|volumeFailures
operator|=
name|volFailures
expr_stmt|;
name|rollBlocksScheduled
argument_list|(
name|lastUpdate
argument_list|)
expr_stmt|;
block|}
comment|/**    * Iterates over the list of blocks belonging to the datanode.    */
DECL|class|BlockIterator
specifier|public
specifier|static
class|class
name|BlockIterator
implements|implements
name|Iterator
argument_list|<
name|BlockInfo
argument_list|>
block|{
DECL|field|current
specifier|private
name|BlockInfo
name|current
decl_stmt|;
DECL|field|node
specifier|private
name|DatanodeDescriptor
name|node
decl_stmt|;
DECL|method|BlockIterator (BlockInfo head, DatanodeDescriptor dn)
name|BlockIterator
parameter_list|(
name|BlockInfo
name|head
parameter_list|,
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
name|this
operator|.
name|current
operator|=
name|head
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|dn
expr_stmt|;
block|}
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|current
operator|!=
literal|null
return|;
block|}
DECL|method|next ()
specifier|public
name|BlockInfo
name|next
parameter_list|()
block|{
name|BlockInfo
name|res
init|=
name|current
decl_stmt|;
name|current
operator|=
name|current
operator|.
name|getNext
argument_list|(
name|current
operator|.
name|findDatanode
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Sorry. can't remove."
argument_list|)
throw|;
block|}
block|}
DECL|method|getBlockIterator ()
specifier|public
name|Iterator
argument_list|<
name|BlockInfo
argument_list|>
name|getBlockIterator
parameter_list|()
block|{
return|return
operator|new
name|BlockIterator
argument_list|(
name|this
operator|.
name|blockList
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/**    * Store block replication work.    */
DECL|method|addBlockToBeReplicated (Block block, DatanodeDescriptor[] targets)
name|void
name|addBlockToBeReplicated
parameter_list|(
name|Block
name|block
parameter_list|,
name|DatanodeDescriptor
index|[]
name|targets
parameter_list|)
block|{
assert|assert
operator|(
name|block
operator|!=
literal|null
operator|&&
name|targets
operator|!=
literal|null
operator|&&
name|targets
operator|.
name|length
operator|>
literal|0
operator|)
assert|;
name|replicateBlocks
operator|.
name|offer
argument_list|(
operator|new
name|BlockTargetPair
argument_list|(
name|block
argument_list|,
name|targets
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Store block recovery work.    */
DECL|method|addBlockToBeRecovered (BlockInfoUnderConstruction block)
name|void
name|addBlockToBeRecovered
parameter_list|(
name|BlockInfoUnderConstruction
name|block
parameter_list|)
block|{
if|if
condition|(
name|recoverBlocks
operator|.
name|contains
argument_list|(
name|block
argument_list|)
condition|)
block|{
comment|// this prevents adding the same block twice to the recovery queue
name|BlockManager
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Block "
operator|+
name|block
operator|+
literal|" is already in the recovery queue."
argument_list|)
expr_stmt|;
return|return;
block|}
name|recoverBlocks
operator|.
name|offer
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
comment|/**    * Store block invalidation work.    */
DECL|method|addBlocksToBeInvalidated (List<Block> blocklist)
name|void
name|addBlocksToBeInvalidated
parameter_list|(
name|List
argument_list|<
name|Block
argument_list|>
name|blocklist
parameter_list|)
block|{
assert|assert
operator|(
name|blocklist
operator|!=
literal|null
operator|&&
name|blocklist
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
assert|;
synchronized|synchronized
init|(
name|invalidateBlocks
init|)
block|{
for|for
control|(
name|Block
name|blk
range|:
name|blocklist
control|)
block|{
name|invalidateBlocks
operator|.
name|add
argument_list|(
name|blk
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * The number of work items that are pending to be replicated    */
DECL|method|getNumberOfBlocksToBeReplicated ()
name|int
name|getNumberOfBlocksToBeReplicated
parameter_list|()
block|{
return|return
name|replicateBlocks
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * The number of block invalidation items that are pending to     * be sent to the datanode    */
DECL|method|getNumberOfBlocksToBeInvalidated ()
name|int
name|getNumberOfBlocksToBeInvalidated
parameter_list|()
block|{
synchronized|synchronized
init|(
name|invalidateBlocks
init|)
block|{
return|return
name|invalidateBlocks
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|method|getReplicationCommand (int maxTransfers)
specifier|public
name|List
argument_list|<
name|BlockTargetPair
argument_list|>
name|getReplicationCommand
parameter_list|(
name|int
name|maxTransfers
parameter_list|)
block|{
return|return
name|replicateBlocks
operator|.
name|poll
argument_list|(
name|maxTransfers
argument_list|)
return|;
block|}
DECL|method|getLeaseRecoveryCommand (int maxTransfers)
specifier|public
name|BlockInfoUnderConstruction
index|[]
name|getLeaseRecoveryCommand
parameter_list|(
name|int
name|maxTransfers
parameter_list|)
block|{
name|List
argument_list|<
name|BlockInfoUnderConstruction
argument_list|>
name|blocks
init|=
name|recoverBlocks
operator|.
name|poll
argument_list|(
name|maxTransfers
argument_list|)
decl_stmt|;
if|if
condition|(
name|blocks
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|blocks
operator|.
name|toArray
argument_list|(
operator|new
name|BlockInfoUnderConstruction
index|[
name|blocks
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * Remove the specified number of blocks to be invalidated    */
DECL|method|getInvalidateBlocks (int maxblocks)
specifier|public
name|Block
index|[]
name|getInvalidateBlocks
parameter_list|(
name|int
name|maxblocks
parameter_list|)
block|{
return|return
name|getBlockArray
argument_list|(
name|invalidateBlocks
argument_list|,
name|maxblocks
argument_list|)
return|;
block|}
DECL|method|getBlockArray (Collection<Block> blocks, int max)
specifier|static
specifier|private
name|Block
index|[]
name|getBlockArray
parameter_list|(
name|Collection
argument_list|<
name|Block
argument_list|>
name|blocks
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|Block
index|[]
name|blockarray
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|blocks
init|)
block|{
name|int
name|available
init|=
name|blocks
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|available
decl_stmt|;
if|if
condition|(
name|max
operator|>
literal|0
operator|&&
name|n
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|max
operator|<
name|n
condition|)
block|{
name|n
operator|=
name|max
expr_stmt|;
block|}
comment|// allocate the properly sized block array ...
name|blockarray
operator|=
operator|new
name|Block
index|[
name|n
index|]
expr_stmt|;
comment|// iterate tree collecting n blocks...
name|Iterator
argument_list|<
name|Block
argument_list|>
name|e
init|=
name|blocks
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|blockCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|blockCount
operator|<
name|n
operator|&&
name|e
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// insert into array ...
name|blockarray
index|[
name|blockCount
operator|++
index|]
operator|=
name|e
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// remove from tree via iterator, if we are removing
comment|// less than total available blocks
if|if
condition|(
name|n
operator|<
name|available
condition|)
block|{
name|e
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
assert|assert
operator|(
name|blockarray
operator|.
name|length
operator|==
name|n
operator|)
assert|;
comment|// now if the number of blocks removed equals available blocks,
comment|// them remove all blocks in one fell swoop via clear
if|if
condition|(
name|n
operator|==
name|available
condition|)
block|{
name|blocks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|blockarray
return|;
block|}
comment|/** Serialization for FSEditLog */
DECL|method|readFieldsFromFSEditLog (DataInput in)
specifier|public
name|void
name|readFieldsFromFSEditLog
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|name
operator|=
name|DeprecatedUTF8
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageID
operator|=
name|DeprecatedUTF8
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|infoPort
operator|=
name|in
operator|.
name|readShort
argument_list|()
operator|&
literal|0x0000ffff
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|dfsUsed
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockPoolUsed
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastUpdate
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|xceiverCount
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|setAdminState
argument_list|(
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|AdminStates
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return Approximate number of blocks currently scheduled to be written     * to this datanode.    */
DECL|method|getBlocksScheduled ()
specifier|public
name|int
name|getBlocksScheduled
parameter_list|()
block|{
return|return
name|currApproxBlocksScheduled
operator|+
name|prevApproxBlocksScheduled
return|;
block|}
comment|/**    * Increments counter for number of blocks scheduled.     */
DECL|method|incBlocksScheduled ()
specifier|public
name|void
name|incBlocksScheduled
parameter_list|()
block|{
name|currApproxBlocksScheduled
operator|++
expr_stmt|;
block|}
comment|/**    * Decrements counter for number of blocks scheduled.    */
DECL|method|decBlocksScheduled ()
name|void
name|decBlocksScheduled
parameter_list|()
block|{
if|if
condition|(
name|prevApproxBlocksScheduled
operator|>
literal|0
condition|)
block|{
name|prevApproxBlocksScheduled
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currApproxBlocksScheduled
operator|>
literal|0
condition|)
block|{
name|currApproxBlocksScheduled
operator|--
expr_stmt|;
block|}
comment|// its ok if both counters are zero.
block|}
comment|/**    * Adjusts curr and prev number of blocks scheduled every few minutes.    */
DECL|method|rollBlocksScheduled (long now)
specifier|private
name|void
name|rollBlocksScheduled
parameter_list|(
name|long
name|now
parameter_list|)
block|{
if|if
condition|(
operator|(
name|now
operator|-
name|lastBlocksScheduledRollTime
operator|)
operator|>
name|BLOCKS_SCHEDULED_ROLL_INTERVAL
condition|)
block|{
name|prevApproxBlocksScheduled
operator|=
name|currApproxBlocksScheduled
expr_stmt|;
name|currApproxBlocksScheduled
operator|=
literal|0
expr_stmt|;
name|lastBlocksScheduledRollTime
operator|=
name|now
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Super implementation is sufficient
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// Sufficient to use super equality as datanodes are uniquely identified
comment|// by DatanodeID
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
comment|/** Decommissioning status */
DECL|class|DecommissioningStatus
specifier|public
class|class
name|DecommissioningStatus
block|{
DECL|field|underReplicatedBlocks
specifier|private
name|int
name|underReplicatedBlocks
decl_stmt|;
DECL|field|decommissionOnlyReplicas
specifier|private
name|int
name|decommissionOnlyReplicas
decl_stmt|;
DECL|field|underReplicatedInOpenFiles
specifier|private
name|int
name|underReplicatedInOpenFiles
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|method|set (int underRep, int onlyRep, int underConstruction)
specifier|synchronized
name|void
name|set
parameter_list|(
name|int
name|underRep
parameter_list|,
name|int
name|onlyRep
parameter_list|,
name|int
name|underConstruction
parameter_list|)
block|{
if|if
condition|(
name|isDecommissionInProgress
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return;
block|}
name|underReplicatedBlocks
operator|=
name|underRep
expr_stmt|;
name|decommissionOnlyReplicas
operator|=
name|onlyRep
expr_stmt|;
name|underReplicatedInOpenFiles
operator|=
name|underConstruction
expr_stmt|;
block|}
comment|/** @return the number of under-replicated blocks */
DECL|method|getUnderReplicatedBlocks ()
specifier|public
specifier|synchronized
name|int
name|getUnderReplicatedBlocks
parameter_list|()
block|{
if|if
condition|(
name|isDecommissionInProgress
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|underReplicatedBlocks
return|;
block|}
comment|/** @return the number of decommission-only replicas */
DECL|method|getDecommissionOnlyReplicas ()
specifier|public
specifier|synchronized
name|int
name|getDecommissionOnlyReplicas
parameter_list|()
block|{
if|if
condition|(
name|isDecommissionInProgress
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|decommissionOnlyReplicas
return|;
block|}
comment|/** @return the number of under-replicated blocks in open files */
DECL|method|getUnderReplicatedInOpenFiles ()
specifier|public
specifier|synchronized
name|int
name|getUnderReplicatedInOpenFiles
parameter_list|()
block|{
if|if
condition|(
name|isDecommissionInProgress
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|underReplicatedInOpenFiles
return|;
block|}
comment|/** Set start time */
DECL|method|setStartTime (long time)
specifier|public
specifier|synchronized
name|void
name|setStartTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|startTime
operator|=
name|time
expr_stmt|;
block|}
comment|/** @return start time */
DECL|method|getStartTime ()
specifier|public
specifier|synchronized
name|long
name|getStartTime
parameter_list|()
block|{
if|if
condition|(
name|isDecommissionInProgress
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|startTime
return|;
block|}
block|}
comment|// End of class DecommissioningStatus
comment|/**    * Set the flag to indicate if this datanode is disallowed from communicating    * with the namenode.    */
DECL|method|setDisallowed (boolean flag)
specifier|public
name|void
name|setDisallowed
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|disallowed
operator|=
name|flag
expr_stmt|;
block|}
comment|/** Is the datanode disallowed from communicating with the namenode? */
DECL|method|isDisallowed ()
specifier|public
name|boolean
name|isDisallowed
parameter_list|()
block|{
return|return
name|disallowed
return|;
block|}
comment|/**    * @return number of failed volumes in the datanode.    */
DECL|method|getVolumeFailures ()
specifier|public
name|int
name|getVolumeFailures
parameter_list|()
block|{
return|return
name|volumeFailures
return|;
block|}
comment|/**    * @param nodeReg DatanodeID to update registration for.    */
DECL|method|updateRegInfo (DatanodeID nodeReg)
specifier|public
name|void
name|updateRegInfo
parameter_list|(
name|DatanodeID
name|nodeReg
parameter_list|)
block|{
name|super
operator|.
name|updateRegInfo
argument_list|(
name|nodeReg
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return Blanacer bandwidth in bytes per second for this datanode.    */
DECL|method|getBalancerBandwidth ()
specifier|public
name|long
name|getBalancerBandwidth
parameter_list|()
block|{
return|return
name|this
operator|.
name|bandwidth
return|;
block|}
comment|/**    * @param bandwidth Blanacer bandwidth in bytes per second for this datanode.    */
DECL|method|setBalancerBandwidth (long bandwidth)
specifier|public
name|void
name|setBalancerBandwidth
parameter_list|(
name|long
name|bandwidth
parameter_list|)
block|{
name|this
operator|.
name|bandwidth
operator|=
name|bandwidth
expr_stmt|;
block|}
block|}
end_class

end_unit

