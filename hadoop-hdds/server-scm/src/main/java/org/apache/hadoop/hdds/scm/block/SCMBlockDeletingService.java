begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|block
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
name|conf
operator|.
name|Configuration
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|Mapping
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
name|hdds
operator|.
name|scm
operator|.
name|events
operator|.
name|SCMEvents
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
name|hdds
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|NodeState
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|DeletedBlocksTransaction
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventPublisher
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|CommandForDatanode
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|DeleteBlocksCommand
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
name|utils
operator|.
name|BackgroundService
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
name|utils
operator|.
name|BackgroundTask
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
name|utils
operator|.
name|BackgroundTaskQueue
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
name|utils
operator|.
name|BackgroundTaskResult
operator|.
name|EmptyTaskResult
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
name|UUID
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
name|TimeUnit
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL_DEFAULT
import|;
end_import

begin_comment
comment|/**  * A background service running in SCM to delete blocks. This service scans  * block deletion log in certain interval and caches block deletion commands  * in {@link org.apache.hadoop.hdds.scm.node.CommandQueue}, asynchronously  * SCM HB thread polls cached commands and sends them to datanode for physical  * processing.  */
end_comment

begin_class
DECL|class|SCMBlockDeletingService
specifier|public
class|class
name|SCMBlockDeletingService
extends|extends
name|BackgroundService
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SCMBlockDeletingService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// ThreadPoolSize=2, 1 for scheduler and the other for the scanner.
DECL|field|BLOCK_DELETING_SERVICE_CORE_POOL_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|BLOCK_DELETING_SERVICE_CORE_POOL_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|deletedBlockLog
specifier|private
specifier|final
name|DeletedBlockLog
name|deletedBlockLog
decl_stmt|;
DECL|field|mappingService
specifier|private
specifier|final
name|Mapping
name|mappingService
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|eventPublisher
specifier|private
specifier|final
name|EventPublisher
name|eventPublisher
decl_stmt|;
comment|// Block delete limit size is dynamically calculated based on container
comment|// delete limit size (ozone.block.deleting.container.limit.per.interval)
comment|// that configured for datanode. To ensure DN not wait for
comment|// delete commands, we use this value multiply by a factor 2 as the final
comment|// limit TX size for each node.
comment|// Currently we implement a throttle algorithm that throttling delete blocks
comment|// for each datanode. Each node is limited by the calculation size. Firstly
comment|// current node info is fetched from nodemanager, then scan entire delLog
comment|// from the beginning to end. If one node reaches maximum value, its records
comment|// will be skipped. If not, keep scanning until it reaches maximum value.
comment|// Once all node are full, the scan behavior will stop.
DECL|field|blockDeleteLimitSize
specifier|private
name|int
name|blockDeleteLimitSize
decl_stmt|;
DECL|method|SCMBlockDeletingService (DeletedBlockLog deletedBlockLog, Mapping mapper, NodeManager nodeManager, EventPublisher eventPublisher, long interval, long serviceTimeout, Configuration conf)
specifier|public
name|SCMBlockDeletingService
parameter_list|(
name|DeletedBlockLog
name|deletedBlockLog
parameter_list|,
name|Mapping
name|mapper
parameter_list|,
name|NodeManager
name|nodeManager
parameter_list|,
name|EventPublisher
name|eventPublisher
parameter_list|,
name|long
name|interval
parameter_list|,
name|long
name|serviceTimeout
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
literal|"SCMBlockDeletingService"
argument_list|,
name|interval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|BLOCK_DELETING_SERVICE_CORE_POOL_SIZE
argument_list|,
name|serviceTimeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|deletedBlockLog
operator|=
name|deletedBlockLog
expr_stmt|;
name|this
operator|.
name|mappingService
operator|=
name|mapper
expr_stmt|;
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|eventPublisher
operator|=
name|eventPublisher
expr_stmt|;
name|int
name|containerLimit
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
argument_list|,
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL_DEFAULT
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|containerLimit
operator|>
literal|0
argument_list|,
literal|"Container limit size should be "
operator|+
literal|"positive."
argument_list|)
expr_stmt|;
comment|// Use container limit value multiply by a factor 2 to ensure DN
comment|// not wait for orders.
name|this
operator|.
name|blockDeleteLimitSize
operator|=
name|containerLimit
operator|*
literal|2
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTasks ()
specifier|public
name|BackgroundTaskQueue
name|getTasks
parameter_list|()
block|{
name|BackgroundTaskQueue
name|queue
init|=
operator|new
name|BackgroundTaskQueue
argument_list|()
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
operator|new
name|DeletedBlockTransactionScanner
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|queue
return|;
block|}
DECL|method|handlePendingDeletes (PendingDeleteStatusList deletionStatusList)
specifier|public
name|void
name|handlePendingDeletes
parameter_list|(
name|PendingDeleteStatusList
name|deletionStatusList
parameter_list|)
block|{
name|DatanodeDetails
name|dnDetails
init|=
name|deletionStatusList
operator|.
name|getDatanodeDetails
argument_list|()
decl_stmt|;
for|for
control|(
name|PendingDeleteStatusList
operator|.
name|PendingDeleteStatus
name|deletionStatus
range|:
name|deletionStatusList
operator|.
name|getPendingDeleteStatuses
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Block deletion txnID mismatch in datanode {} for containerID {}."
operator|+
literal|" Datanode delete txnID: {}, SCM txnID: {}"
argument_list|,
name|dnDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
name|deletionStatus
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|deletionStatus
operator|.
name|getDnDeleteTransactionId
argument_list|()
argument_list|,
name|deletionStatus
operator|.
name|getScmDeleteTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DeletedBlockTransactionScanner
specifier|private
class|class
name|DeletedBlockTransactionScanner
implements|implements
name|BackgroundTask
argument_list|<
name|EmptyTaskResult
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getPriority ()
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|EmptyTaskResult
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|dnTxCount
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
comment|// Scan SCM DB in HB interval and collect a throttled list of
comment|// to delete blocks.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running DeletedBlockTransactionScanner"
argument_list|)
expr_stmt|;
name|DatanodeDeletedBlockTransactions
name|transactions
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
name|nodeManager
operator|.
name|getNodes
argument_list|(
name|NodeState
operator|.
name|HEALTHY
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|transactionMap
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|datanodes
operator|!=
literal|null
condition|)
block|{
name|transactions
operator|=
operator|new
name|DatanodeDeletedBlockTransactions
argument_list|(
name|mappingService
argument_list|,
name|blockDeleteLimitSize
argument_list|,
name|datanodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|transactionMap
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
name|transactions
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// We may tolerant a number of failures for sometime
comment|// but if it continues to fail, at some point we need to raise
comment|// an exception and probably fail the SCM ? At present, it simply
comment|// continues to retry the scanning.
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get block deletion transactions from delTX log"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scanned deleted blocks log and got {} delTX to process."
argument_list|,
name|transactions
operator|.
name|getTXNum
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|transactions
operator|!=
literal|null
operator|&&
operator|!
name|transactions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|UUID
name|dnId
range|:
name|transactions
operator|.
name|getDatanodeIDs
argument_list|()
control|)
block|{
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|dnTXs
init|=
name|transactions
operator|.
name|getDatanodeTransactions
argument_list|(
name|dnId
argument_list|)
decl_stmt|;
if|if
condition|(
name|dnTXs
operator|!=
literal|null
operator|&&
operator|!
name|dnTXs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|dnTxCount
operator|+=
name|dnTXs
operator|.
name|size
argument_list|()
expr_stmt|;
comment|// TODO commandQueue needs a cap.
comment|// We should stop caching new commands if num of un-processed
comment|// command is bigger than a limit, e.g 50. In case datanode goes
comment|// offline for sometime, the cached commands be flooded.
name|eventPublisher
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|RETRIABLE_DATANODE_COMMAND
argument_list|,
operator|new
name|CommandForDatanode
argument_list|<>
argument_list|(
name|dnId
argument_list|,
operator|new
name|DeleteBlocksCommand
argument_list|(
name|dnTXs
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Added delete block command for datanode {} in the queue,"
operator|+
literal|" number of delete block transactions: {}, TxID list: {}"
argument_list|,
name|dnId
argument_list|,
name|dnTXs
operator|.
name|size
argument_list|()
argument_list|,
name|String
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|transactions
operator|.
name|getTransactionIDList
argument_list|(
name|dnId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|mappingService
operator|.
name|updateDeleteTransactionId
argument_list|(
name|transactionMap
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dnTxCount
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Totally added {} delete blocks command for"
operator|+
literal|" {} datanodes, task elapsed time: {}ms"
argument_list|,
name|dnTxCount
argument_list|,
name|transactions
operator|.
name|getDatanodeIDs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
return|return
name|EmptyTaskResult
operator|.
name|newResult
argument_list|()
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|setBlockDeleteTXNum (int numTXs)
specifier|public
name|void
name|setBlockDeleteTXNum
parameter_list|(
name|int
name|numTXs
parameter_list|)
block|{
name|blockDeleteLimitSize
operator|=
name|numTXs
expr_stmt|;
block|}
block|}
end_class

end_unit

