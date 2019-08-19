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
name|Queue
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
name|ReplicaState
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
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|Maps
import|;
end_import

begin_comment
comment|/**  * In the Standby Node, we can receive messages about blocks  * before they are actually available in the namespace, or while  * they have an outdated state in the namespace. In those cases,  * we queue those block-related messages in this structure.  * */
end_comment

begin_class
DECL|class|PendingDataNodeMessages
class|class
name|PendingDataNodeMessages
block|{
DECL|field|queueByBlockId
specifier|final
name|Map
argument_list|<
name|Block
argument_list|,
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
argument_list|>
name|queueByBlockId
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|class|ReportedBlockInfo
specifier|static
class|class
name|ReportedBlockInfo
block|{
DECL|field|block
specifier|private
specifier|final
name|Block
name|block
decl_stmt|;
DECL|field|storageInfo
specifier|private
specifier|final
name|DatanodeStorageInfo
name|storageInfo
decl_stmt|;
DECL|field|reportedState
specifier|private
specifier|final
name|ReplicaState
name|reportedState
decl_stmt|;
DECL|method|ReportedBlockInfo (DatanodeStorageInfo storageInfo, Block block, ReplicaState reportedState)
name|ReportedBlockInfo
parameter_list|(
name|DatanodeStorageInfo
name|storageInfo
parameter_list|,
name|Block
name|block
parameter_list|,
name|ReplicaState
name|reportedState
parameter_list|)
block|{
name|this
operator|.
name|storageInfo
operator|=
name|storageInfo
expr_stmt|;
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|reportedState
operator|=
name|reportedState
expr_stmt|;
block|}
DECL|method|getBlock ()
name|Block
name|getBlock
parameter_list|()
block|{
return|return
name|block
return|;
block|}
DECL|method|getReportedState ()
name|ReplicaState
name|getReportedState
parameter_list|()
block|{
return|return
name|reportedState
return|;
block|}
DECL|method|getStorageInfo ()
name|DatanodeStorageInfo
name|getStorageInfo
parameter_list|()
block|{
return|return
name|storageInfo
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
literal|"ReportedBlockInfo [block="
operator|+
name|block
operator|+
literal|", dn="
operator|+
name|storageInfo
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|+
literal|", reportedState="
operator|+
name|reportedState
operator|+
literal|"]"
return|;
block|}
block|}
comment|/**    * Remove all pending DN messages which reference the given DN.    * @param dn the datanode whose messages we should remove.    */
DECL|method|removeAllMessagesForDatanode (DatanodeDescriptor dn)
name|void
name|removeAllMessagesForDatanode
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Block
argument_list|,
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
argument_list|>
name|entry
range|:
name|queueByBlockId
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
name|newQueue
init|=
name|Lists
operator|.
name|newLinkedList
argument_list|()
decl_stmt|;
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
name|oldQueue
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|oldQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ReportedBlockInfo
name|rbi
init|=
name|oldQueue
operator|.
name|remove
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|rbi
operator|.
name|getStorageInfo
argument_list|()
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|equals
argument_list|(
name|dn
argument_list|)
condition|)
block|{
name|newQueue
operator|.
name|add
argument_list|(
name|rbi
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|count
operator|--
expr_stmt|;
block|}
block|}
name|queueByBlockId
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|newQueue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|enqueueReportedBlock (DatanodeStorageInfo storageInfo, Block block, ReplicaState reportedState)
name|void
name|enqueueReportedBlock
parameter_list|(
name|DatanodeStorageInfo
name|storageInfo
parameter_list|,
name|Block
name|block
parameter_list|,
name|ReplicaState
name|reportedState
parameter_list|)
block|{
if|if
condition|(
name|BlockIdManager
operator|.
name|isStripedBlockID
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
condition|)
block|{
name|Block
name|blkId
init|=
operator|new
name|Block
argument_list|(
name|BlockIdManager
operator|.
name|convertToStripedID
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|getBlockQueue
argument_list|(
name|blkId
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|ReportedBlockInfo
argument_list|(
name|storageInfo
argument_list|,
operator|new
name|Block
argument_list|(
name|block
argument_list|)
argument_list|,
name|reportedState
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|block
operator|=
operator|new
name|Block
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|getBlockQueue
argument_list|(
name|block
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|ReportedBlockInfo
argument_list|(
name|storageInfo
argument_list|,
name|block
argument_list|,
name|reportedState
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
comment|/**    * @return any messages that were previously queued for the given block,    * or null if no messages were queued.    */
DECL|method|takeBlockQueue (Block block)
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
name|takeBlockQueue
parameter_list|(
name|Block
name|block
parameter_list|)
block|{
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
name|queue
init|=
name|queueByBlockId
operator|.
name|remove
argument_list|(
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|!=
literal|null
condition|)
block|{
name|count
operator|-=
name|queue
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|queue
return|;
block|}
DECL|method|getBlockQueue (Block block)
specifier|private
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
name|getBlockQueue
parameter_list|(
name|Block
name|block
parameter_list|)
block|{
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
name|queue
init|=
name|queueByBlockId
operator|.
name|get
argument_list|(
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
name|queue
operator|=
name|Lists
operator|.
name|newLinkedList
argument_list|()
expr_stmt|;
name|queueByBlockId
operator|.
name|put
argument_list|(
name|block
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
return|return
name|queue
return|;
block|}
DECL|method|count ()
name|int
name|count
parameter_list|()
block|{
return|return
name|count
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Block
argument_list|,
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
argument_list|>
name|entry
range|:
name|queueByBlockId
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"Block "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|":\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|ReportedBlockInfo
name|rbi
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|rbi
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|takeAll ()
name|Iterable
argument_list|<
name|ReportedBlockInfo
argument_list|>
name|takeAll
parameter_list|()
block|{
name|List
argument_list|<
name|ReportedBlockInfo
argument_list|>
name|rbis
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|count
argument_list|)
decl_stmt|;
for|for
control|(
name|Queue
argument_list|<
name|ReportedBlockInfo
argument_list|>
name|q
range|:
name|queueByBlockId
operator|.
name|values
argument_list|()
control|)
block|{
name|rbis
operator|.
name|addAll
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
name|queueByBlockId
operator|.
name|clear
argument_list|()
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
return|return
name|rbis
return|;
block|}
block|}
end_class

end_unit

