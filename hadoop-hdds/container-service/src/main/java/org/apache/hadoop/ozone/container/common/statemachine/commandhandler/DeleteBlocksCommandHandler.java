begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.statemachine.commandhandler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|commandhandler
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
name|primitives
operator|.
name|Longs
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|SCMCommandProto
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
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|ContainerBlocksDeletionACKProto
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
name|ContainerBlocksDeletionACKProto
operator|.
name|DeleteBlockTransactionResult
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
name|ozone
operator|.
name|OzoneConsts
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|DeletedContainerBlocksSummary
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
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|Container
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
name|container
operator|.
name|keyvalue
operator|.
name|KeyValueContainerData
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
name|container
operator|.
name|keyvalue
operator|.
name|helpers
operator|.
name|BlockUtils
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerSet
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
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|EndpointStateMachine
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
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|SCMConnectionManager
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
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|container
operator|.
name|ozoneimpl
operator|.
name|OzoneContainer
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
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
name|BatchOperation
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
name|MetadataStore
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
import|import static
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|Result
operator|.
name|CONTAINER_NOT_FOUND
import|;
end_import

begin_comment
comment|/**  * Handle block deletion commands.  */
end_comment

begin_class
DECL|class|DeleteBlocksCommandHandler
specifier|public
class|class
name|DeleteBlocksCommandHandler
implements|implements
name|CommandHandler
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
name|DeleteBlocksCommandHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerSet
specifier|private
specifier|final
name|ContainerSet
name|containerSet
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|invocationCount
specifier|private
name|int
name|invocationCount
decl_stmt|;
DECL|field|totalTime
specifier|private
name|long
name|totalTime
decl_stmt|;
DECL|field|cmdExecuted
specifier|private
name|boolean
name|cmdExecuted
decl_stmt|;
DECL|method|DeleteBlocksCommandHandler (ContainerSet cset, Configuration conf)
specifier|public
name|DeleteBlocksCommandHandler
parameter_list|(
name|ContainerSet
name|cset
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|containerSet
operator|=
name|cset
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (SCMCommand command, OzoneContainer container, StateContext context, SCMConnectionManager connectionManager)
specifier|public
name|void
name|handle
parameter_list|(
name|SCMCommand
name|command
parameter_list|,
name|OzoneContainer
name|container
parameter_list|,
name|StateContext
name|context
parameter_list|,
name|SCMConnectionManager
name|connectionManager
parameter_list|)
block|{
name|cmdExecuted
operator|=
literal|false
expr_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|command
operator|.
name|getType
argument_list|()
operator|!=
name|SCMCommandProto
operator|.
name|Type
operator|.
name|deleteBlocksCommand
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Skipping handling command, expected command "
operator|+
literal|"type {} but found {}"
argument_list|,
name|SCMCommandProto
operator|.
name|Type
operator|.
name|deleteBlocksCommand
argument_list|,
name|command
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Processing block deletion command."
argument_list|)
expr_stmt|;
name|invocationCount
operator|++
expr_stmt|;
comment|// move blocks to deleting state.
comment|// this is a metadata update, the actual deletion happens in another
comment|// recycling thread.
name|DeleteBlocksCommand
name|cmd
init|=
operator|(
name|DeleteBlocksCommand
operator|)
name|command
decl_stmt|;
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|containerBlocks
init|=
name|cmd
operator|.
name|blocksTobeDeleted
argument_list|()
decl_stmt|;
name|DeletedContainerBlocksSummary
name|summary
init|=
name|DeletedContainerBlocksSummary
operator|.
name|getFrom
argument_list|(
name|containerBlocks
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Start to delete container blocks, TXIDs={}, "
operator|+
literal|"numOfContainers={}, numOfBlocks={}"
argument_list|,
name|summary
operator|.
name|getTxIDSummary
argument_list|()
argument_list|,
name|summary
operator|.
name|getNumOfContainers
argument_list|()
argument_list|,
name|summary
operator|.
name|getNumOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerBlocksDeletionACKProto
operator|.
name|Builder
name|resultBuilder
init|=
name|ContainerBlocksDeletionACKProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|containerBlocks
operator|.
name|forEach
argument_list|(
name|entry
lambda|->
block|{
name|DeleteBlockTransactionResult
operator|.
name|Builder
name|txResultBuilder
init|=
name|DeleteBlockTransactionResult
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|txResultBuilder
operator|.
name|setTxID
argument_list|(
name|entry
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|containerId
init|=
name|entry
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
try|try
block|{
name|Container
name|cont
init|=
name|containerSet
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cont
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to find the container "
operator|+
name|containerId
argument_list|,
name|CONTAINER_NOT_FOUND
argument_list|)
throw|;
block|}
name|ContainerProtos
operator|.
name|ContainerType
name|containerType
init|=
name|cont
operator|.
name|getContainerType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|containerType
condition|)
block|{
case|case
name|KeyValueContainer
case|:
name|KeyValueContainerData
name|containerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|cont
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
name|deleteKeyValueContainerBlocks
argument_list|(
name|containerData
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|txResultBuilder
operator|.
name|setContainerID
argument_list|(
name|containerId
argument_list|)
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Delete Blocks Command Handler is not implemented for "
operator|+
literal|"containerType {}"
argument_list|,
name|containerType
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete blocks for container={}, TXID={}"
argument_list|,
name|entry
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|entry
operator|.
name|getTxID
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|txResultBuilder
operator|.
name|setContainerID
argument_list|(
name|containerId
argument_list|)
operator|.
name|setSuccess
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|resultBuilder
operator|.
name|addResults
argument_list|(
name|txResultBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|setDnId
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|ContainerBlocksDeletionACKProto
name|blockDeletionACK
init|=
name|resultBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Send ACK back to SCM as long as meta updated
comment|// TODO Or we should wait until the blocks are actually deleted?
if|if
condition|(
operator|!
name|containerBlocks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|EndpointStateMachine
name|endPoint
range|:
name|connectionManager
operator|.
name|getValues
argument_list|()
control|)
block|{
try|try
block|{
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
literal|"Sending following block deletion ACK to SCM"
argument_list|)
expr_stmt|;
for|for
control|(
name|DeleteBlockTransactionResult
name|result
range|:
name|blockDeletionACK
operator|.
name|getResultsList
argument_list|()
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|result
operator|.
name|getTxID
argument_list|()
operator|+
literal|" : "
operator|+
name|result
operator|.
name|getSuccess
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|endPoint
operator|.
name|getEndPoint
argument_list|()
operator|.
name|sendContainerBlocksDeletionACK
argument_list|(
name|blockDeletionACK
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to send block deletion ACK to SCM {}"
argument_list|,
name|endPoint
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|cmdExecuted
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|updateCommandStatus
argument_list|(
name|context
argument_list|,
name|command
argument_list|,
name|cmdExecuted
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|long
name|endTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|totalTime
operator|+=
name|endTime
operator|-
name|startTime
expr_stmt|;
block|}
block|}
comment|/**    * Move a bunch of blocks from a container to deleting state. This is a meta    * update, the actual deletes happen in async mode.    *    * @param containerData - KeyValueContainerData    * @param delTX a block deletion transaction.    * @throws IOException if I/O error occurs.    */
DECL|method|deleteKeyValueContainerBlocks ( KeyValueContainerData containerData, DeletedBlocksTransaction delTX)
specifier|private
name|void
name|deleteKeyValueContainerBlocks
parameter_list|(
name|KeyValueContainerData
name|containerData
parameter_list|,
name|DeletedBlocksTransaction
name|delTX
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|containerId
init|=
name|delTX
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
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
literal|"Processing Container : {}, DB path : {}"
argument_list|,
name|containerId
argument_list|,
name|containerData
operator|.
name|getMetadataPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|delTX
operator|.
name|getTxID
argument_list|()
operator|<
name|containerData
operator|.
name|getDeleteTransactionId
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Ignoring delete blocks for containerId: %d."
operator|+
literal|" Outdated delete transactionId %d< %d"
argument_list|,
name|containerId
argument_list|,
name|delTX
operator|.
name|getTxID
argument_list|()
argument_list|,
name|containerData
operator|.
name|getDeleteTransactionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|newDeletionBlocks
init|=
literal|0
decl_stmt|;
name|MetadataStore
name|containerDB
init|=
name|BlockUtils
operator|.
name|getDB
argument_list|(
name|containerData
argument_list|,
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|Long
name|blk
range|:
name|delTX
operator|.
name|getLocalIDList
argument_list|()
control|)
block|{
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
argument_list|()
decl_stmt|;
name|byte
index|[]
name|blkBytes
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|blk
argument_list|)
decl_stmt|;
name|byte
index|[]
name|blkInfo
init|=
name|containerDB
operator|.
name|get
argument_list|(
name|blkBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|blkInfo
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|deletingKeyBytes
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
operator|+
name|blk
argument_list|)
decl_stmt|;
name|byte
index|[]
name|deletedKeyBytes
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|OzoneConsts
operator|.
name|DELETED_KEY_PREFIX
operator|+
name|blk
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerDB
operator|.
name|get
argument_list|(
name|deletingKeyBytes
argument_list|)
operator|!=
literal|null
operator|||
name|containerDB
operator|.
name|get
argument_list|(
name|deletedKeyBytes
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Ignoring delete for block %d in container %d."
operator|+
literal|" Entry already added."
argument_list|,
name|blk
argument_list|,
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// Found the block in container db,
comment|// use an atomic update to change its state to deleting.
name|batch
operator|.
name|put
argument_list|(
name|deletingKeyBytes
argument_list|,
name|blkInfo
argument_list|)
expr_stmt|;
name|batch
operator|.
name|delete
argument_list|(
name|blkBytes
argument_list|)
expr_stmt|;
try|try
block|{
name|containerDB
operator|.
name|writeBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
name|newDeletionBlocks
operator|++
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Transited Block {} to DELETING state in container {}"
argument_list|,
name|blk
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// if some blocks failed to delete, we fail this TX,
comment|// without sending this ACK to SCM, SCM will resend the TX
comment|// with a certain number of retries.
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete blocks for TXID = "
operator|+
name|delTX
operator|.
name|getTxID
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Block {} not found or already under deletion in"
operator|+
literal|" container {}, skip deleting it."
argument_list|,
name|blk
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
name|containerDB
operator|.
name|put
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|OzoneConsts
operator|.
name|DELETE_TRANSACTION_KEY_PREFIX
operator|+
name|delTX
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|,
name|Longs
operator|.
name|toByteArray
argument_list|(
name|delTX
operator|.
name|getTxID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|containerData
operator|.
name|updateDeleteTransactionId
argument_list|(
name|delTX
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
comment|// update pending deletion blocks count in in-memory container status
name|containerData
operator|.
name|incrPendingDeletionBlocks
argument_list|(
name|newDeletionBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCommandType ()
specifier|public
name|SCMCommandProto
operator|.
name|Type
name|getCommandType
parameter_list|()
block|{
return|return
name|SCMCommandProto
operator|.
name|Type
operator|.
name|deleteBlocksCommand
return|;
block|}
annotation|@
name|Override
DECL|method|getInvocationCount ()
specifier|public
name|int
name|getInvocationCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|invocationCount
return|;
block|}
annotation|@
name|Override
DECL|method|getAverageRunTime ()
specifier|public
name|long
name|getAverageRunTime
parameter_list|()
block|{
if|if
condition|(
name|invocationCount
operator|>
literal|0
condition|)
block|{
return|return
name|totalTime
operator|/
name|invocationCount
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

