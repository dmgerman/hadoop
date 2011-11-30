begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
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
name|BlockListAsLongs
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
name|ExtendedBlock
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
name|DatanodeRegistration
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
name|ReceivedDeletedBlockInfo
import|;
end_import

begin_class
DECL|class|PendingDataNodeMessages
specifier|public
class|class
name|PendingDataNodeMessages
block|{
DECL|field|queue
name|PriorityQueue
argument_list|<
name|DataNodeMessage
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|DataNodeMessage
argument_list|>
argument_list|()
decl_stmt|;
DECL|enum|MessageType
enum|enum
name|MessageType
block|{
DECL|enumConstant|BLOCK_RECEIVED_DELETE
name|BLOCK_RECEIVED_DELETE
block|,
DECL|enumConstant|BLOCK_REPORT
name|BLOCK_REPORT
block|,
DECL|enumConstant|COMMIT_BLOCK_SYNCHRONIZATION
name|COMMIT_BLOCK_SYNCHRONIZATION
block|}
DECL|class|DataNodeMessage
specifier|static
specifier|abstract
class|class
name|DataNodeMessage
implements|implements
name|Comparable
argument_list|<
name|DataNodeMessage
argument_list|>
block|{
DECL|field|type
specifier|final
name|MessageType
name|type
decl_stmt|;
DECL|field|targetGs
specifier|private
specifier|final
name|long
name|targetGs
decl_stmt|;
DECL|method|DataNodeMessage (MessageType type, long targetGenStamp)
name|DataNodeMessage
parameter_list|(
name|MessageType
name|type
parameter_list|,
name|long
name|targetGenStamp
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|targetGs
operator|=
name|targetGenStamp
expr_stmt|;
block|}
DECL|method|getType ()
specifier|protected
name|MessageType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getTargetGs ()
specifier|protected
name|long
name|getTargetGs
parameter_list|()
block|{
return|return
name|targetGs
return|;
block|}
DECL|method|compareTo (DataNodeMessage other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|DataNodeMessage
name|other
parameter_list|)
block|{
if|if
condition|(
name|targetGs
operator|==
name|other
operator|.
name|targetGs
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|targetGs
operator|<
name|other
operator|.
name|targetGs
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|1
return|;
block|}
block|}
DECL|class|BlockReceivedDeleteMessage
specifier|static
class|class
name|BlockReceivedDeleteMessage
extends|extends
name|DataNodeMessage
block|{
DECL|field|nodeReg
specifier|final
name|DatanodeRegistration
name|nodeReg
decl_stmt|;
DECL|field|poolId
specifier|final
name|String
name|poolId
decl_stmt|;
DECL|field|receivedAndDeletedBlocks
specifier|final
name|ReceivedDeletedBlockInfo
index|[]
name|receivedAndDeletedBlocks
decl_stmt|;
DECL|method|BlockReceivedDeleteMessage (DatanodeRegistration nodeReg, String poolId, ReceivedDeletedBlockInfo[] receivedAndDeletedBlocks, long targetGs)
name|BlockReceivedDeleteMessage
parameter_list|(
name|DatanodeRegistration
name|nodeReg
parameter_list|,
name|String
name|poolId
parameter_list|,
name|ReceivedDeletedBlockInfo
index|[]
name|receivedAndDeletedBlocks
parameter_list|,
name|long
name|targetGs
parameter_list|)
block|{
name|super
argument_list|(
name|MessageType
operator|.
name|BLOCK_RECEIVED_DELETE
argument_list|,
name|targetGs
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeReg
operator|=
name|nodeReg
expr_stmt|;
name|this
operator|.
name|poolId
operator|=
name|poolId
expr_stmt|;
name|this
operator|.
name|receivedAndDeletedBlocks
operator|=
name|receivedAndDeletedBlocks
expr_stmt|;
block|}
DECL|method|getNodeReg ()
name|DatanodeRegistration
name|getNodeReg
parameter_list|()
block|{
return|return
name|nodeReg
return|;
block|}
DECL|method|getPoolId ()
name|String
name|getPoolId
parameter_list|()
block|{
return|return
name|poolId
return|;
block|}
DECL|method|getReceivedAndDeletedBlocks ()
name|ReceivedDeletedBlockInfo
index|[]
name|getReceivedAndDeletedBlocks
parameter_list|()
block|{
return|return
name|receivedAndDeletedBlocks
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"BlockReceivedDeletedMessage with "
operator|+
name|receivedAndDeletedBlocks
operator|.
name|length
operator|+
literal|" blocks"
return|;
block|}
block|}
DECL|class|CommitBlockSynchronizationMessage
specifier|static
class|class
name|CommitBlockSynchronizationMessage
extends|extends
name|DataNodeMessage
block|{
DECL|field|block
specifier|private
specifier|final
name|ExtendedBlock
name|block
decl_stmt|;
DECL|field|newgenerationstamp
specifier|private
specifier|final
name|long
name|newgenerationstamp
decl_stmt|;
DECL|field|newlength
specifier|private
specifier|final
name|long
name|newlength
decl_stmt|;
DECL|field|closeFile
specifier|private
specifier|final
name|boolean
name|closeFile
decl_stmt|;
DECL|field|deleteblock
specifier|private
specifier|final
name|boolean
name|deleteblock
decl_stmt|;
DECL|field|newtargets
specifier|private
specifier|final
name|DatanodeID
index|[]
name|newtargets
decl_stmt|;
DECL|method|CommitBlockSynchronizationMessage (ExtendedBlock block, long newgenerationstamp, long newlength, boolean closeFile, boolean deleteblock, DatanodeID[] newtargets, long targetGenStamp)
name|CommitBlockSynchronizationMessage
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|long
name|newgenerationstamp
parameter_list|,
name|long
name|newlength
parameter_list|,
name|boolean
name|closeFile
parameter_list|,
name|boolean
name|deleteblock
parameter_list|,
name|DatanodeID
index|[]
name|newtargets
parameter_list|,
name|long
name|targetGenStamp
parameter_list|)
block|{
name|super
argument_list|(
name|MessageType
operator|.
name|COMMIT_BLOCK_SYNCHRONIZATION
argument_list|,
name|targetGenStamp
argument_list|)
expr_stmt|;
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|newgenerationstamp
operator|=
name|newgenerationstamp
expr_stmt|;
name|this
operator|.
name|newlength
operator|=
name|newlength
expr_stmt|;
name|this
operator|.
name|closeFile
operator|=
name|closeFile
expr_stmt|;
name|this
operator|.
name|deleteblock
operator|=
name|deleteblock
expr_stmt|;
name|this
operator|.
name|newtargets
operator|=
name|newtargets
expr_stmt|;
block|}
DECL|method|getBlock ()
name|ExtendedBlock
name|getBlock
parameter_list|()
block|{
return|return
name|block
return|;
block|}
DECL|method|getNewgenerationstamp ()
name|long
name|getNewgenerationstamp
parameter_list|()
block|{
return|return
name|newgenerationstamp
return|;
block|}
DECL|method|getNewlength ()
name|long
name|getNewlength
parameter_list|()
block|{
return|return
name|newlength
return|;
block|}
DECL|method|isCloseFile ()
name|boolean
name|isCloseFile
parameter_list|()
block|{
return|return
name|closeFile
return|;
block|}
DECL|method|isDeleteblock ()
name|boolean
name|isDeleteblock
parameter_list|()
block|{
return|return
name|deleteblock
return|;
block|}
DECL|method|getNewtargets ()
name|DatanodeID
index|[]
name|getNewtargets
parameter_list|()
block|{
return|return
name|newtargets
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CommitBlockSynchronizationMessage for "
operator|+
name|block
return|;
block|}
block|}
DECL|class|BlockReportMessage
specifier|static
class|class
name|BlockReportMessage
extends|extends
name|DataNodeMessage
block|{
DECL|field|nodeReg
specifier|private
specifier|final
name|DatanodeRegistration
name|nodeReg
decl_stmt|;
DECL|field|poolId
specifier|private
specifier|final
name|String
name|poolId
decl_stmt|;
DECL|field|blockList
specifier|private
specifier|final
name|BlockListAsLongs
name|blockList
decl_stmt|;
DECL|method|BlockReportMessage (DatanodeRegistration nodeReg, String poolId, BlockListAsLongs blist, long targetGenStamp)
name|BlockReportMessage
parameter_list|(
name|DatanodeRegistration
name|nodeReg
parameter_list|,
name|String
name|poolId
parameter_list|,
name|BlockListAsLongs
name|blist
parameter_list|,
name|long
name|targetGenStamp
parameter_list|)
block|{
name|super
argument_list|(
name|MessageType
operator|.
name|BLOCK_REPORT
argument_list|,
name|targetGenStamp
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeReg
operator|=
name|nodeReg
expr_stmt|;
name|this
operator|.
name|poolId
operator|=
name|poolId
expr_stmt|;
name|this
operator|.
name|blockList
operator|=
name|blist
expr_stmt|;
block|}
DECL|method|getNodeReg ()
name|DatanodeRegistration
name|getNodeReg
parameter_list|()
block|{
return|return
name|nodeReg
return|;
block|}
DECL|method|getPoolId ()
name|String
name|getPoolId
parameter_list|()
block|{
return|return
name|poolId
return|;
block|}
DECL|method|getBlockList ()
name|BlockListAsLongs
name|getBlockList
parameter_list|()
block|{
return|return
name|blockList
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"BlockReport from "
operator|+
name|nodeReg
operator|+
literal|" with "
operator|+
name|blockList
operator|.
name|getNumberOfBlocks
argument_list|()
operator|+
literal|" blocks"
return|;
block|}
block|}
DECL|method|queueMessage (DataNodeMessage msg)
specifier|synchronized
name|void
name|queueMessage
parameter_list|(
name|DataNodeMessage
name|msg
parameter_list|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a message if contains a message less or equal to the given gs,    * otherwise returns null.    *     * @param gs    */
DECL|method|take (long gs)
specifier|synchronized
name|DataNodeMessage
name|take
parameter_list|(
name|long
name|gs
parameter_list|)
block|{
name|DataNodeMessage
name|m
init|=
name|queue
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
operator|&&
name|m
operator|.
name|getTargetGs
argument_list|()
operator|<
name|gs
condition|)
block|{
return|return
name|queue
operator|.
name|remove
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|isEmpty ()
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|queue
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
end_class

end_unit

