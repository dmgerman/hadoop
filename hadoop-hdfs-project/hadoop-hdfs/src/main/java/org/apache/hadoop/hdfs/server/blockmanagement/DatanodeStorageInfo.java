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
name|Arrays
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|StorageType
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
name|DatanodeStorage
operator|.
name|State
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

begin_comment
comment|/**  * A Datanode has one or more storages. A storage in the Datanode is represented  * by this class.  */
end_comment

begin_class
DECL|class|DatanodeStorageInfo
specifier|public
class|class
name|DatanodeStorageInfo
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|DatanodeStorageInfo
index|[]
name|EMPTY_ARRAY
init|=
block|{}
decl_stmt|;
DECL|method|toDatanodeInfos (DatanodeStorageInfo[] storages)
specifier|public
specifier|static
name|DatanodeInfo
index|[]
name|toDatanodeInfos
parameter_list|(
name|DatanodeStorageInfo
index|[]
name|storages
parameter_list|)
block|{
return|return
name|toDatanodeInfos
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|storages
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toDatanodeInfos (List<DatanodeStorageInfo> storages)
specifier|static
name|DatanodeInfo
index|[]
name|toDatanodeInfos
parameter_list|(
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|storages
parameter_list|)
block|{
specifier|final
name|DatanodeInfo
index|[]
name|datanodes
init|=
operator|new
name|DatanodeInfo
index|[
name|storages
operator|.
name|size
argument_list|()
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
name|storages
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|datanodes
index|[
name|i
index|]
operator|=
name|storages
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatanodeDescriptor
argument_list|()
expr_stmt|;
block|}
return|return
name|datanodes
return|;
block|}
DECL|method|toDatanodeDescriptors ( DatanodeStorageInfo[] storages)
specifier|static
name|DatanodeDescriptor
index|[]
name|toDatanodeDescriptors
parameter_list|(
name|DatanodeStorageInfo
index|[]
name|storages
parameter_list|)
block|{
name|DatanodeDescriptor
index|[]
name|datanodes
init|=
operator|new
name|DatanodeDescriptor
index|[
name|storages
operator|.
name|length
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
name|storages
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|datanodes
index|[
name|i
index|]
operator|=
name|storages
index|[
name|i
index|]
operator|.
name|getDatanodeDescriptor
argument_list|()
expr_stmt|;
block|}
return|return
name|datanodes
return|;
block|}
DECL|method|toStorageIDs (DatanodeStorageInfo[] storages)
specifier|public
specifier|static
name|String
index|[]
name|toStorageIDs
parameter_list|(
name|DatanodeStorageInfo
index|[]
name|storages
parameter_list|)
block|{
name|String
index|[]
name|storageIDs
init|=
operator|new
name|String
index|[
name|storages
operator|.
name|length
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
name|storageIDs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|storageIDs
index|[
name|i
index|]
operator|=
name|storages
index|[
name|i
index|]
operator|.
name|getStorageID
argument_list|()
expr_stmt|;
block|}
return|return
name|storageIDs
return|;
block|}
DECL|method|toStorageTypes (DatanodeStorageInfo[] storages)
specifier|public
specifier|static
name|StorageType
index|[]
name|toStorageTypes
parameter_list|(
name|DatanodeStorageInfo
index|[]
name|storages
parameter_list|)
block|{
name|StorageType
index|[]
name|storageTypes
init|=
operator|new
name|StorageType
index|[
name|storages
operator|.
name|length
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
name|storageTypes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|storageTypes
index|[
name|i
index|]
operator|=
name|storages
index|[
name|i
index|]
operator|.
name|getStorageType
argument_list|()
expr_stmt|;
block|}
return|return
name|storageTypes
return|;
block|}
DECL|method|updateFromStorage (DatanodeStorage storage)
specifier|public
name|void
name|updateFromStorage
parameter_list|(
name|DatanodeStorage
name|storage
parameter_list|)
block|{
name|state
operator|=
name|storage
operator|.
name|getState
argument_list|()
expr_stmt|;
name|storageType
operator|=
name|storage
operator|.
name|getStorageType
argument_list|()
expr_stmt|;
block|}
comment|/**    * Iterates over the list of blocks belonging to the data-node.    */
DECL|class|BlockIterator
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
DECL|method|BlockIterator (BlockInfo head)
name|BlockIterator
parameter_list|(
name|BlockInfo
name|head
parameter_list|)
block|{
name|this
operator|.
name|current
operator|=
name|head
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
name|findStorageInfo
argument_list|(
name|DatanodeStorageInfo
operator|.
name|this
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
DECL|field|dn
specifier|private
specifier|final
name|DatanodeDescriptor
name|dn
decl_stmt|;
DECL|field|storageID
specifier|private
specifier|final
name|String
name|storageID
decl_stmt|;
DECL|field|storageType
specifier|private
name|StorageType
name|storageType
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
decl_stmt|;
DECL|field|capacity
specifier|private
name|long
name|capacity
decl_stmt|;
DECL|field|dfsUsed
specifier|private
name|long
name|dfsUsed
decl_stmt|;
DECL|field|remaining
specifier|private
name|long
name|remaining
decl_stmt|;
DECL|field|blockPoolUsed
specifier|private
name|long
name|blockPoolUsed
decl_stmt|;
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
comment|/** The number of block reports received */
DECL|field|blockReportCount
specifier|private
name|int
name|blockReportCount
init|=
literal|0
decl_stmt|;
comment|/**    * Set to false on any NN failover, and reset to true    * whenever a block report is received.    */
DECL|field|heartbeatedSinceFailover
specifier|private
name|boolean
name|heartbeatedSinceFailover
init|=
literal|false
decl_stmt|;
comment|/**    * At startup or at failover, the storages in the cluster may have pending    * block deletions from a previous incarnation of the NameNode. The block    * contents are considered as stale until a block report is received. When a    * storage is considered as stale, the replicas on it are also considered as    * stale. If any block has at least one stale replica, then no invalidations    * will be processed for this block. See HDFS-1972.    */
DECL|field|blockContentsStale
specifier|private
name|boolean
name|blockContentsStale
init|=
literal|true
decl_stmt|;
DECL|method|DatanodeStorageInfo (DatanodeDescriptor dn, DatanodeStorage s)
name|DatanodeStorageInfo
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|,
name|DatanodeStorage
name|s
parameter_list|)
block|{
name|this
operator|.
name|dn
operator|=
name|dn
expr_stmt|;
name|this
operator|.
name|storageID
operator|=
name|s
operator|.
name|getStorageID
argument_list|()
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|s
operator|.
name|getStorageType
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|s
operator|.
name|getState
argument_list|()
expr_stmt|;
block|}
DECL|method|getBlockReportCount ()
name|int
name|getBlockReportCount
parameter_list|()
block|{
return|return
name|blockReportCount
return|;
block|}
DECL|method|setBlockReportCount (int blockReportCount)
name|void
name|setBlockReportCount
parameter_list|(
name|int
name|blockReportCount
parameter_list|)
block|{
name|this
operator|.
name|blockReportCount
operator|=
name|blockReportCount
expr_stmt|;
block|}
DECL|method|areBlockContentsStale ()
name|boolean
name|areBlockContentsStale
parameter_list|()
block|{
return|return
name|blockContentsStale
return|;
block|}
DECL|method|markStaleAfterFailover ()
name|void
name|markStaleAfterFailover
parameter_list|()
block|{
name|heartbeatedSinceFailover
operator|=
literal|false
expr_stmt|;
name|blockContentsStale
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|receivedHeartbeat (StorageReport report)
name|void
name|receivedHeartbeat
parameter_list|(
name|StorageReport
name|report
parameter_list|)
block|{
name|updateState
argument_list|(
name|report
argument_list|)
expr_stmt|;
name|heartbeatedSinceFailover
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|receivedBlockReport ()
name|void
name|receivedBlockReport
parameter_list|()
block|{
if|if
condition|(
name|heartbeatedSinceFailover
condition|)
block|{
name|blockContentsStale
operator|=
literal|false
expr_stmt|;
block|}
name|blockReportCount
operator|++
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setUtilizationForTesting (long capacity, long dfsUsed, long remaining, long blockPoolUsed)
specifier|public
name|void
name|setUtilizationForTesting
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
block|}
DECL|method|getState ()
name|State
name|getState
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
return|;
block|}
DECL|method|getStorageID ()
name|String
name|getStorageID
parameter_list|()
block|{
return|return
name|storageID
return|;
block|}
DECL|method|getStorageType ()
name|StorageType
name|getStorageType
parameter_list|()
block|{
return|return
name|storageType
return|;
block|}
DECL|method|getCapacity ()
name|long
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
DECL|method|getDfsUsed ()
name|long
name|getDfsUsed
parameter_list|()
block|{
return|return
name|dfsUsed
return|;
block|}
DECL|method|getRemaining ()
name|long
name|getRemaining
parameter_list|()
block|{
return|return
name|remaining
return|;
block|}
DECL|method|getBlockPoolUsed ()
name|long
name|getBlockPoolUsed
parameter_list|()
block|{
return|return
name|blockPoolUsed
return|;
block|}
DECL|method|addBlock (BlockInfo b)
specifier|public
name|boolean
name|addBlock
parameter_list|(
name|BlockInfo
name|b
parameter_list|)
block|{
comment|// First check whether the block belongs to a different storage
comment|// on the same DN.
name|boolean
name|replaced
init|=
literal|false
decl_stmt|;
name|DatanodeStorageInfo
name|otherStorage
init|=
name|b
operator|.
name|findStorageInfo
argument_list|(
name|getDatanodeDescriptor
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherStorage
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|otherStorage
operator|!=
name|this
condition|)
block|{
comment|// The block belongs to a different storage. Remove it first.
name|otherStorage
operator|.
name|removeBlock
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|replaced
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// The block is already associated with this storage.
return|return
literal|false
return|;
block|}
block|}
comment|// add to the head of the data-node list
name|b
operator|.
name|addStorage
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
operator|!
name|replaced
return|;
block|}
DECL|method|removeBlock (BlockInfo b)
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
name|removeStorage
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
DECL|method|numBlocks ()
name|int
name|numBlocks
parameter_list|()
block|{
return|return
name|numBlocks
return|;
block|}
DECL|method|getBlockIterator ()
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
name|blockList
argument_list|)
return|;
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
annotation|@
name|VisibleForTesting
DECL|method|getBlockListHeadForTesting ()
name|BlockInfo
name|getBlockListHeadForTesting
parameter_list|()
block|{
return|return
name|blockList
return|;
block|}
DECL|method|updateState (StorageReport r)
name|void
name|updateState
parameter_list|(
name|StorageReport
name|r
parameter_list|)
block|{
name|capacity
operator|=
name|r
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|dfsUsed
operator|=
name|r
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
name|remaining
operator|=
name|r
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
name|blockPoolUsed
operator|=
name|r
operator|.
name|getBlockPoolUsed
argument_list|()
expr_stmt|;
block|}
DECL|method|getDatanodeDescriptor ()
specifier|public
name|DatanodeDescriptor
name|getDatanodeDescriptor
parameter_list|()
block|{
return|return
name|dn
return|;
block|}
comment|/** Increment the number of blocks scheduled for each given storage */
DECL|method|incrementBlocksScheduled (DatanodeStorageInfo... storages)
specifier|public
specifier|static
name|void
name|incrementBlocksScheduled
parameter_list|(
name|DatanodeStorageInfo
modifier|...
name|storages
parameter_list|)
block|{
for|for
control|(
name|DatanodeStorageInfo
name|s
range|:
name|storages
control|)
block|{
name|s
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|incrementBlocksScheduled
argument_list|(
name|s
operator|.
name|getStorageType
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
operator|!
operator|(
name|obj
operator|instanceof
name|DatanodeStorageInfo
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|DatanodeStorageInfo
name|that
init|=
operator|(
name|DatanodeStorageInfo
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|storageID
operator|.
name|equals
argument_list|(
name|that
operator|.
name|storageID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|storageID
operator|.
name|hashCode
argument_list|()
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
return|return
literal|"["
operator|+
name|storageType
operator|+
literal|"]"
operator|+
name|storageID
operator|+
literal|":"
operator|+
name|state
return|;
block|}
DECL|method|toStorageReport ()
name|StorageReport
name|toStorageReport
parameter_list|()
block|{
return|return
operator|new
name|StorageReport
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|storageID
argument_list|,
name|state
argument_list|,
name|storageType
argument_list|)
argument_list|,
literal|false
argument_list|,
name|capacity
argument_list|,
name|dfsUsed
argument_list|,
name|remaining
argument_list|,
name|blockPoolUsed
argument_list|)
return|;
block|}
DECL|method|toStorageTypes ( final Iterable<DatanodeStorageInfo> infos)
specifier|static
name|Iterable
argument_list|<
name|StorageType
argument_list|>
name|toStorageTypes
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|infos
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|StorageType
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|StorageType
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|StorageType
argument_list|>
argument_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|i
init|=
name|infos
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|i
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|StorageType
name|next
parameter_list|()
block|{
return|return
name|i
operator|.
name|next
argument_list|()
operator|.
name|getStorageType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/** @return the first {@link DatanodeStorageInfo} corresponding to    *          the given datanode    */
DECL|method|getDatanodeStorageInfo ( final Iterable<DatanodeStorageInfo> infos, final DatanodeDescriptor datanode)
specifier|static
name|DatanodeStorageInfo
name|getDatanodeStorageInfo
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|infos
parameter_list|,
specifier|final
name|DatanodeDescriptor
name|datanode
parameter_list|)
block|{
if|if
condition|(
name|datanode
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|infos
control|)
block|{
if|if
condition|(
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|==
name|datanode
condition|)
block|{
return|return
name|storage
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

