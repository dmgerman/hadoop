begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
operator|.
name|monotonicNow
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
name|Arrays
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
name|datanode
operator|.
name|metrics
operator|.
name|DataNodeMetrics
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
name|DatanodeProtocol
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
name|ReceivedDeletedBlockInfo
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
operator|.
name|BlockStatus
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
name|StorageReceivedDeletedBlocks
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
name|collect
operator|.
name|Maps
import|;
end_import

begin_comment
comment|/**  * Manage Incremental Block Reports (IBRs).  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|IncrementalBlockReportManager
class|class
name|IncrementalBlockReportManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IncrementalBlockReportManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|PerStorageIBR
specifier|private
specifier|static
class|class
name|PerStorageIBR
block|{
comment|/** The blocks in this IBR. */
DECL|field|blocks
specifier|final
name|Map
argument_list|<
name|Block
argument_list|,
name|ReceivedDeletedBlockInfo
argument_list|>
name|blocks
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|dnMetrics
specifier|private
name|DataNodeMetrics
name|dnMetrics
decl_stmt|;
DECL|method|PerStorageIBR (final DataNodeMetrics dnMetrics)
name|PerStorageIBR
parameter_list|(
specifier|final
name|DataNodeMetrics
name|dnMetrics
parameter_list|)
block|{
name|this
operator|.
name|dnMetrics
operator|=
name|dnMetrics
expr_stmt|;
block|}
comment|/**      * Remove the given block from this IBR      * @return true if the block was removed; otherwise, return false.      */
DECL|method|remove (Block block)
name|ReceivedDeletedBlockInfo
name|remove
parameter_list|(
name|Block
name|block
parameter_list|)
block|{
return|return
name|blocks
operator|.
name|remove
argument_list|(
name|block
argument_list|)
return|;
block|}
comment|/** @return all the blocks removed from this IBR. */
DECL|method|removeAll ()
name|ReceivedDeletedBlockInfo
index|[]
name|removeAll
parameter_list|()
block|{
specifier|final
name|int
name|size
init|=
name|blocks
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|ReceivedDeletedBlockInfo
index|[]
name|rdbis
init|=
name|blocks
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ReceivedDeletedBlockInfo
index|[
name|size
index|]
argument_list|)
decl_stmt|;
name|blocks
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|rdbis
return|;
block|}
comment|/** Put the block to this IBR. */
DECL|method|put (ReceivedDeletedBlockInfo rdbi)
name|void
name|put
parameter_list|(
name|ReceivedDeletedBlockInfo
name|rdbi
parameter_list|)
block|{
name|blocks
operator|.
name|put
argument_list|(
name|rdbi
operator|.
name|getBlock
argument_list|()
argument_list|,
name|rdbi
argument_list|)
expr_stmt|;
name|increaseBlocksCounter
argument_list|(
name|rdbi
argument_list|)
expr_stmt|;
block|}
DECL|method|increaseBlocksCounter ( final ReceivedDeletedBlockInfo receivedDeletedBlockInfo)
specifier|private
name|void
name|increaseBlocksCounter
parameter_list|(
specifier|final
name|ReceivedDeletedBlockInfo
name|receivedDeletedBlockInfo
parameter_list|)
block|{
switch|switch
condition|(
name|receivedDeletedBlockInfo
operator|.
name|getStatus
argument_list|()
condition|)
block|{
case|case
name|RECEIVING_BLOCK
case|:
name|dnMetrics
operator|.
name|incrBlocksReceivingInPendingIBR
argument_list|()
expr_stmt|;
break|break;
case|case
name|RECEIVED_BLOCK
case|:
name|dnMetrics
operator|.
name|incrBlocksReceivedInPendingIBR
argument_list|()
expr_stmt|;
break|break;
case|case
name|DELETED_BLOCK
case|:
name|dnMetrics
operator|.
name|incrBlocksDeletedInPendingIBR
argument_list|()
expr_stmt|;
break|break;
default|default:
break|break;
block|}
name|dnMetrics
operator|.
name|incrBlocksInPendingIBR
argument_list|()
expr_stmt|;
block|}
comment|/**      * Put the all blocks to this IBR unless the block already exists.      * @param rdbis list of blocks to add.      * @return the number of missing blocks added.      */
DECL|method|putMissing (ReceivedDeletedBlockInfo[] rdbis)
name|int
name|putMissing
parameter_list|(
name|ReceivedDeletedBlockInfo
index|[]
name|rdbis
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ReceivedDeletedBlockInfo
name|rdbi
range|:
name|rdbis
control|)
block|{
if|if
condition|(
operator|!
name|blocks
operator|.
name|containsKey
argument_list|(
name|rdbi
operator|.
name|getBlock
argument_list|()
argument_list|)
condition|)
block|{
name|put
argument_list|(
name|rdbi
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
block|}
comment|/**    * Between block reports (which happen on the order of once an hour) the    * DN reports smaller incremental changes to its block list for each storage.    * This map contains the pending changes not yet to be reported to the NN.    */
DECL|field|pendingIBRs
specifier|private
specifier|final
name|Map
argument_list|<
name|DatanodeStorage
argument_list|,
name|PerStorageIBR
argument_list|>
name|pendingIBRs
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|/**    * If this flag is set then an IBR will be sent immediately by the actor    * thread without waiting for the IBR timer to elapse.    */
DECL|field|readyToSend
specifier|private
specifier|volatile
name|boolean
name|readyToSend
init|=
literal|false
decl_stmt|;
comment|/** The time interval between two IBRs. */
DECL|field|ibrInterval
specifier|private
specifier|final
name|long
name|ibrInterval
decl_stmt|;
comment|/** The timestamp of the last IBR. */
DECL|field|lastIBR
specifier|private
specifier|volatile
name|long
name|lastIBR
decl_stmt|;
DECL|field|dnMetrics
specifier|private
name|DataNodeMetrics
name|dnMetrics
decl_stmt|;
DECL|method|IncrementalBlockReportManager ( final long ibrInterval, final DataNodeMetrics dnMetrics)
name|IncrementalBlockReportManager
parameter_list|(
specifier|final
name|long
name|ibrInterval
parameter_list|,
specifier|final
name|DataNodeMetrics
name|dnMetrics
parameter_list|)
block|{
name|this
operator|.
name|ibrInterval
operator|=
name|ibrInterval
expr_stmt|;
name|this
operator|.
name|lastIBR
operator|=
name|monotonicNow
argument_list|()
operator|-
name|ibrInterval
expr_stmt|;
name|this
operator|.
name|dnMetrics
operator|=
name|dnMetrics
expr_stmt|;
block|}
DECL|method|sendImmediately ()
name|boolean
name|sendImmediately
parameter_list|()
block|{
return|return
name|readyToSend
operator|&&
name|monotonicNow
argument_list|()
operator|-
name|ibrInterval
operator|>=
name|lastIBR
return|;
block|}
DECL|method|waitTillNextIBR (long waitTime)
specifier|synchronized
name|void
name|waitTillNextIBR
parameter_list|(
name|long
name|waitTime
parameter_list|)
block|{
if|if
condition|(
name|waitTime
operator|>
literal|0
operator|&&
operator|!
name|sendImmediately
argument_list|()
condition|)
block|{
try|try
block|{
name|wait
argument_list|(
name|ibrInterval
operator|>
literal|0
operator|&&
name|ibrInterval
operator|<
name|waitTime
condition|?
name|ibrInterval
else|:
name|waitTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" interrupted"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|generateIBRs ()
specifier|private
specifier|synchronized
name|StorageReceivedDeletedBlocks
index|[]
name|generateIBRs
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|StorageReceivedDeletedBlocks
argument_list|>
name|reports
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|pendingIBRs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|DatanodeStorage
argument_list|,
name|PerStorageIBR
argument_list|>
name|entry
range|:
name|pendingIBRs
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|PerStorageIBR
name|perStorage
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Send newly-received and deleted blockids to namenode
specifier|final
name|ReceivedDeletedBlockInfo
index|[]
name|rdbi
init|=
name|perStorage
operator|.
name|removeAll
argument_list|()
decl_stmt|;
if|if
condition|(
name|rdbi
operator|!=
literal|null
condition|)
block|{
name|reports
operator|.
name|add
argument_list|(
operator|new
name|StorageReceivedDeletedBlocks
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|rdbi
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* set blocks to zero */
name|this
operator|.
name|dnMetrics
operator|.
name|resetBlocksInPendingIBR
argument_list|()
expr_stmt|;
name|readyToSend
operator|=
literal|false
expr_stmt|;
return|return
name|reports
operator|.
name|toArray
argument_list|(
operator|new
name|StorageReceivedDeletedBlocks
index|[
name|reports
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|putMissing (StorageReceivedDeletedBlocks[] reports)
specifier|private
specifier|synchronized
name|void
name|putMissing
parameter_list|(
name|StorageReceivedDeletedBlocks
index|[]
name|reports
parameter_list|)
block|{
for|for
control|(
name|StorageReceivedDeletedBlocks
name|r
range|:
name|reports
control|)
block|{
name|pendingIBRs
operator|.
name|get
argument_list|(
name|r
operator|.
name|getStorage
argument_list|()
argument_list|)
operator|.
name|putMissing
argument_list|(
name|r
operator|.
name|getBlocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reports
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|readyToSend
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/** Send IBRs to namenode. */
DECL|method|sendIBRs (DatanodeProtocol namenode, DatanodeRegistration registration, String bpid, String nnRpcLatencySuffix)
name|void
name|sendIBRs
parameter_list|(
name|DatanodeProtocol
name|namenode
parameter_list|,
name|DatanodeRegistration
name|registration
parameter_list|,
name|String
name|bpid
parameter_list|,
name|String
name|nnRpcLatencySuffix
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Generate a list of the pending reports for each storage under the lock
specifier|final
name|StorageReceivedDeletedBlocks
index|[]
name|reports
init|=
name|generateIBRs
argument_list|()
decl_stmt|;
if|if
condition|(
name|reports
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// Nothing new to report.
return|return;
block|}
comment|// Send incremental block reports to the Namenode outside the lock
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"call blockReceivedAndDeleted: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|reports
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|final
name|long
name|startTime
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
try|try
block|{
name|namenode
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|registration
argument_list|,
name|bpid
argument_list|,
name|reports
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|dnMetrics
operator|.
name|addIncrementalBlockReport
argument_list|(
name|monotonicNow
argument_list|()
operator|-
name|startTime
argument_list|,
name|nnRpcLatencySuffix
argument_list|)
expr_stmt|;
name|lastIBR
operator|=
name|startTime
expr_stmt|;
block|}
else|else
block|{
comment|// If we didn't succeed in sending the report, put all of the
comment|// blocks back onto our queue, but only in the case where we
comment|// didn't put something newer in the meantime.
name|putMissing
argument_list|(
name|reports
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** @return the pending IBR for the given {@code storage} */
DECL|method|getPerStorageIBR (DatanodeStorage storage)
specifier|private
name|PerStorageIBR
name|getPerStorageIBR
parameter_list|(
name|DatanodeStorage
name|storage
parameter_list|)
block|{
name|PerStorageIBR
name|perStorage
init|=
name|pendingIBRs
operator|.
name|get
argument_list|(
name|storage
argument_list|)
decl_stmt|;
if|if
condition|(
name|perStorage
operator|==
literal|null
condition|)
block|{
comment|// This is the first time we are adding incremental BR state for
comment|// this storage so create a new map. This is required once per
comment|// storage, per service actor.
name|perStorage
operator|=
operator|new
name|PerStorageIBR
argument_list|(
name|dnMetrics
argument_list|)
expr_stmt|;
name|pendingIBRs
operator|.
name|put
argument_list|(
name|storage
argument_list|,
name|perStorage
argument_list|)
expr_stmt|;
block|}
return|return
name|perStorage
return|;
block|}
comment|/**    * Add a block for notification to NameNode.    * If another entry exists for the same block it is removed.    */
annotation|@
name|VisibleForTesting
DECL|method|addRDBI (ReceivedDeletedBlockInfo rdbi, DatanodeStorage storage)
specifier|synchronized
name|void
name|addRDBI
parameter_list|(
name|ReceivedDeletedBlockInfo
name|rdbi
parameter_list|,
name|DatanodeStorage
name|storage
parameter_list|)
block|{
comment|// Make sure another entry for the same block is first removed.
comment|// There may only be one such entry.
for|for
control|(
name|PerStorageIBR
name|perStorage
range|:
name|pendingIBRs
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|perStorage
operator|.
name|remove
argument_list|(
name|rdbi
operator|.
name|getBlock
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
name|getPerStorageIBR
argument_list|(
name|storage
argument_list|)
operator|.
name|put
argument_list|(
name|rdbi
argument_list|)
expr_stmt|;
block|}
DECL|method|notifyNamenodeBlock (ReceivedDeletedBlockInfo rdbi, DatanodeStorage storage, boolean isOnTransientStorage)
specifier|synchronized
name|void
name|notifyNamenodeBlock
parameter_list|(
name|ReceivedDeletedBlockInfo
name|rdbi
parameter_list|,
name|DatanodeStorage
name|storage
parameter_list|,
name|boolean
name|isOnTransientStorage
parameter_list|)
block|{
name|addRDBI
argument_list|(
name|rdbi
argument_list|,
name|storage
argument_list|)
expr_stmt|;
specifier|final
name|BlockStatus
name|status
init|=
name|rdbi
operator|.
name|getStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|BlockStatus
operator|.
name|RECEIVING_BLOCK
condition|)
block|{
comment|// the report will be sent out in the next heartbeat.
name|readyToSend
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|status
operator|==
name|BlockStatus
operator|.
name|RECEIVED_BLOCK
condition|)
block|{
comment|// the report is sent right away.
name|triggerIBR
argument_list|(
name|isOnTransientStorage
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|triggerIBR (boolean force)
specifier|synchronized
name|void
name|triggerIBR
parameter_list|(
name|boolean
name|force
parameter_list|)
block|{
name|readyToSend
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|force
condition|)
block|{
name|lastIBR
operator|=
name|monotonicNow
argument_list|()
operator|-
name|ibrInterval
expr_stmt|;
block|}
if|if
condition|(
name|sendImmediately
argument_list|()
condition|)
block|{
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|triggerDeletionReportForTests ()
specifier|synchronized
name|void
name|triggerDeletionReportForTests
parameter_list|()
block|{
name|triggerIBR
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|sendImmediately
argument_list|()
condition|)
block|{
try|try
block|{
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
block|}
block|}
DECL|method|clearIBRs ()
name|void
name|clearIBRs
parameter_list|()
block|{
name|pendingIBRs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPendingIBRSize ()
name|int
name|getPendingIBRSize
parameter_list|()
block|{
return|return
name|pendingIBRs
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

