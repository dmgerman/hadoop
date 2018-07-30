begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.transport.server.ratis
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
name|transport
operator|.
name|server
operator|.
name|ratis
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
name|ratis
operator|.
name|shaded
operator|.
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
operator|.
name|ContainerCommandResponseProto
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
operator|.
name|WriteChunkRequestProto
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
name|ContainerDispatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|conf
operator|.
name|RaftProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftClientRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftPeerId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|server
operator|.
name|storage
operator|.
name|RaftStorage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
operator|.
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
operator|.
name|proto
operator|.
name|RaftProtos
operator|.
name|LogEntryProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
operator|.
name|proto
operator|.
name|RaftProtos
operator|.
name|SMLogEntryProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|statemachine
operator|.
name|StateMachineStorage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|statemachine
operator|.
name|TransactionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|statemachine
operator|.
name|impl
operator|.
name|BaseStateMachine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|statemachine
operator|.
name|impl
operator|.
name|SimpleStateMachineStorage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|statemachine
operator|.
name|impl
operator|.
name|TransactionContextImpl
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
name|concurrent
operator|.
name|CompletableFuture
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
name|ConcurrentHashMap
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
name|ThreadPoolExecutor
import|;
end_import

begin_comment
comment|/** A {@link org.apache.ratis.statemachine.StateMachine} for containers.  *  * The stateMachine is responsible for handling different types of container  * requests. The container requests can be divided into readonly and write  * requests.  *  * Read only requests are classified in  * {@link org.apache.hadoop.hdds.HddsUtils#isReadOnly}  * and these readonly requests are replied from the {@link #query(Message)}.  *  * The write requests can be divided into requests with user data  * (WriteChunkRequest) and other request without user data.  *  * Inorder to optimize the write throughput, the writeChunk request is  * processed in 2 phases. The 2 phases are divided in  * {@link #startTransaction(RaftClientRequest)}, in the first phase the user  * data is written directly into the state machine via  * {@link #writeStateMachineData} and in the second phase the  * transaction is committed via {@link #applyTransaction(TransactionContext)}  *  * For the requests with no stateMachine data, the transaction is directly  * committed through  * {@link #applyTransaction(TransactionContext)}  *  * There are 2 ordering operation which are enforced right now in the code,  * 1) Write chunk operation are executed after the create container operation,  * the write chunk operation will fail otherwise as the container still hasn't  * been created. Hence the create container operation has been split in the  * {@link #startTransaction(RaftClientRequest)}, this will help in synchronizing  * the calls in {@link #writeStateMachineData}  *  * 2) Write chunk commit operation is executed after write chunk state machine  * operation. This will ensure that commit operation is sync'd with the state  * machine operation.  *  * Synchronization between {@link #writeStateMachineData} and  * {@link #applyTransaction} need to be enforced in the StateMachine  * implementation. For example, synchronization between writeChunk and  * createContainer in {@link ContainerStateMachine}.  * */
end_comment

begin_class
DECL|class|ContainerStateMachine
specifier|public
class|class
name|ContainerStateMachine
extends|extends
name|BaseStateMachine
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerStateMachine
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|storage
specifier|private
specifier|final
name|SimpleStateMachineStorage
name|storage
init|=
operator|new
name|SimpleStateMachineStorage
argument_list|()
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|ContainerDispatcher
name|dispatcher
decl_stmt|;
DECL|field|writeChunkExecutor
specifier|private
name|ThreadPoolExecutor
name|writeChunkExecutor
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
argument_list|>
DECL|field|writeChunkFutureMap
name|writeChunkFutureMap
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
argument_list|>
DECL|field|createContainerFutureMap
name|createContainerFutureMap
decl_stmt|;
DECL|method|ContainerStateMachine (ContainerDispatcher dispatcher, ThreadPoolExecutor writeChunkExecutor)
name|ContainerStateMachine
parameter_list|(
name|ContainerDispatcher
name|dispatcher
parameter_list|,
name|ThreadPoolExecutor
name|writeChunkExecutor
parameter_list|)
block|{
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|writeChunkExecutor
operator|=
name|writeChunkExecutor
expr_stmt|;
name|this
operator|.
name|writeChunkFutureMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|createContainerFutureMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStateMachineStorage ()
specifier|public
name|StateMachineStorage
name|getStateMachineStorage
parameter_list|()
block|{
return|return
name|storage
return|;
block|}
annotation|@
name|Override
DECL|method|initialize ( RaftPeerId id, RaftProperties properties, RaftStorage raftStorage)
specifier|public
name|void
name|initialize
parameter_list|(
name|RaftPeerId
name|id
parameter_list|,
name|RaftProperties
name|properties
parameter_list|,
name|RaftStorage
name|raftStorage
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|initialize
argument_list|(
name|id
argument_list|,
name|properties
argument_list|,
name|raftStorage
argument_list|)
expr_stmt|;
name|storage
operator|.
name|init
argument_list|(
name|raftStorage
argument_list|)
expr_stmt|;
comment|//  TODO handle snapshots
comment|// TODO: Add a flag that tells you that initialize has been called.
comment|// Check with Ratis if this feature is done in Ratis.
block|}
annotation|@
name|Override
DECL|method|startTransaction (RaftClientRequest request)
specifier|public
name|TransactionContext
name|startTransaction
parameter_list|(
name|RaftClientRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ContainerCommandRequestProto
name|proto
init|=
name|getRequestProto
argument_list|(
name|request
operator|.
name|getMessage
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SMLogEntryProto
name|log
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|getCmdType
argument_list|()
operator|==
name|ContainerProtos
operator|.
name|Type
operator|.
name|WriteChunk
condition|)
block|{
specifier|final
name|WriteChunkRequestProto
name|write
init|=
name|proto
operator|.
name|getWriteChunk
argument_list|()
decl_stmt|;
comment|// create the state machine data proto
specifier|final
name|WriteChunkRequestProto
name|dataWriteChunkProto
init|=
name|WriteChunkRequestProto
operator|.
name|newBuilder
argument_list|(
name|write
argument_list|)
operator|.
name|setStage
argument_list|(
name|ContainerProtos
operator|.
name|Stage
operator|.
name|WRITE_DATA
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerCommandRequestProto
name|dataContainerCommandProto
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
operator|.
name|setWriteChunk
argument_list|(
name|dataWriteChunkProto
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// create the log entry proto
specifier|final
name|WriteChunkRequestProto
name|commitWriteChunkProto
init|=
name|WriteChunkRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlockID
argument_list|(
name|write
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|setChunkData
argument_list|(
name|write
operator|.
name|getChunkData
argument_list|()
argument_list|)
comment|// skipping the data field as it is
comment|// already set in statemachine data proto
operator|.
name|setStage
argument_list|(
name|ContainerProtos
operator|.
name|Stage
operator|.
name|COMMIT_DATA
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerCommandRequestProto
name|commitContainerCommandProto
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
operator|.
name|setWriteChunk
argument_list|(
name|commitWriteChunkProto
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|log
operator|=
name|SMLogEntryProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setData
argument_list|(
name|commitContainerCommandProto
operator|.
name|toByteString
argument_list|()
argument_list|)
operator|.
name|setStateMachineData
argument_list|(
name|dataContainerCommandProto
operator|.
name|toByteString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|proto
operator|.
name|getCmdType
argument_list|()
operator|==
name|ContainerProtos
operator|.
name|Type
operator|.
name|CreateContainer
condition|)
block|{
name|log
operator|=
name|SMLogEntryProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setData
argument_list|(
name|request
operator|.
name|getMessage
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
operator|.
name|setStateMachineData
argument_list|(
name|request
operator|.
name|getMessage
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|=
name|SMLogEntryProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setData
argument_list|(
name|request
operator|.
name|getMessage
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|TransactionContextImpl
argument_list|(
name|this
argument_list|,
name|request
argument_list|,
name|log
argument_list|)
return|;
block|}
DECL|method|getRequestProto (ByteString request)
specifier|private
name|ContainerCommandRequestProto
name|getRequestProto
parameter_list|(
name|ByteString
name|request
parameter_list|)
throws|throws
name|InvalidProtocolBufferException
block|{
return|return
name|ContainerCommandRequestProto
operator|.
name|parseFrom
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|runCommand (ContainerCommandRequestProto requestProto)
specifier|private
name|Message
name|runCommand
parameter_list|(
name|ContainerCommandRequestProto
name|requestProto
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"dispatch {}"
argument_list|,
name|requestProto
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|dispatcher
operator|.
name|dispatch
argument_list|(
name|requestProto
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"response {}"
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
parameter_list|()
lambda|->
name|response
operator|.
name|toByteString
argument_list|()
return|;
block|}
DECL|method|handleWriteChunk ( ContainerCommandRequestProto requestProto, long entryIndex)
specifier|private
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
name|handleWriteChunk
parameter_list|(
name|ContainerCommandRequestProto
name|requestProto
parameter_list|,
name|long
name|entryIndex
parameter_list|)
block|{
specifier|final
name|WriteChunkRequestProto
name|write
init|=
name|requestProto
operator|.
name|getWriteChunk
argument_list|()
decl_stmt|;
name|long
name|containerID
init|=
name|write
operator|.
name|getBlockID
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
name|future
init|=
name|createContainerFutureMap
operator|.
name|get
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
name|writeChunkFuture
decl_stmt|;
if|if
condition|(
name|future
operator|!=
literal|null
condition|)
block|{
name|writeChunkFuture
operator|=
name|future
operator|.
name|thenApplyAsync
argument_list|(
name|v
lambda|->
name|runCommand
argument_list|(
name|requestProto
argument_list|)
argument_list|,
name|writeChunkExecutor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeChunkFuture
operator|=
name|CompletableFuture
operator|.
name|supplyAsync
argument_list|(
parameter_list|()
lambda|->
name|runCommand
argument_list|(
name|requestProto
argument_list|)
argument_list|,
name|writeChunkExecutor
argument_list|)
expr_stmt|;
block|}
name|writeChunkFutureMap
operator|.
name|put
argument_list|(
name|entryIndex
argument_list|,
name|writeChunkFuture
argument_list|)
expr_stmt|;
return|return
name|writeChunkFuture
return|;
block|}
DECL|method|handleCreateContainer ( ContainerCommandRequestProto requestProto)
specifier|private
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
name|handleCreateContainer
parameter_list|(
name|ContainerCommandRequestProto
name|requestProto
parameter_list|)
block|{
name|long
name|containerID
init|=
name|requestProto
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|createContainerFutureMap
operator|.
name|computeIfAbsent
argument_list|(
name|containerID
argument_list|,
name|k
lambda|->
operator|new
name|CompletableFuture
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|CompletableFuture
operator|.
name|completedFuture
argument_list|(
parameter_list|()
lambda|->
name|ByteString
operator|.
name|EMPTY
argument_list|)
return|;
block|}
comment|/*    * writeStateMachineData calls are not synchronized with each other    * and also with applyTransaction.    */
annotation|@
name|Override
DECL|method|writeStateMachineData (LogEntryProto entry)
specifier|public
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
name|writeStateMachineData
parameter_list|(
name|LogEntryProto
name|entry
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ContainerCommandRequestProto
name|requestProto
init|=
name|getRequestProto
argument_list|(
name|entry
operator|.
name|getSmLogEntry
argument_list|()
operator|.
name|getStateMachineData
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|Type
name|cmdType
init|=
name|requestProto
operator|.
name|getCmdType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|cmdType
condition|)
block|{
case|case
name|CreateContainer
case|:
return|return
name|handleCreateContainer
argument_list|(
name|requestProto
argument_list|)
return|;
case|case
name|WriteChunk
case|:
return|return
name|handleWriteChunk
argument_list|(
name|requestProto
argument_list|,
name|entry
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cmd Type:"
operator|+
name|cmdType
operator|+
literal|" should not have state machine data"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|completeExceptionally
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|query (Message request)
specifier|public
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
name|query
parameter_list|(
name|Message
name|request
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ContainerCommandRequestProto
name|requestProto
init|=
name|getRequestProto
argument_list|(
name|request
operator|.
name|getContent
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|CompletableFuture
operator|.
name|completedFuture
argument_list|(
name|runCommand
argument_list|(
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|completeExceptionally
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
comment|/*    * ApplyTransaction calls in Ratis are sequential.    */
annotation|@
name|Override
DECL|method|applyTransaction (TransactionContext trx)
specifier|public
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
name|applyTransaction
parameter_list|(
name|TransactionContext
name|trx
parameter_list|)
block|{
try|try
block|{
name|ContainerCommandRequestProto
name|requestProto
init|=
name|getRequestProto
argument_list|(
name|trx
operator|.
name|getSMLogEntry
argument_list|()
operator|.
name|getData
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|Type
name|cmdType
init|=
name|requestProto
operator|.
name|getCmdType
argument_list|()
decl_stmt|;
if|if
condition|(
name|cmdType
operator|==
name|ContainerProtos
operator|.
name|Type
operator|.
name|WriteChunk
condition|)
block|{
name|WriteChunkRequestProto
name|write
init|=
name|requestProto
operator|.
name|getWriteChunk
argument_list|()
decl_stmt|;
comment|// the data field has already been removed in start Transaction
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|write
operator|.
name|hasData
argument_list|()
argument_list|)
expr_stmt|;
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
name|stateMachineFuture
init|=
name|writeChunkFutureMap
operator|.
name|remove
argument_list|(
name|trx
operator|.
name|getLogEntry
argument_list|()
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|stateMachineFuture
operator|.
name|thenComposeAsync
argument_list|(
name|v
lambda|->
name|CompletableFuture
operator|.
name|completedFuture
argument_list|(
name|runCommand
argument_list|(
name|requestProto
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|Message
name|message
init|=
name|runCommand
argument_list|(
name|requestProto
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmdType
operator|==
name|ContainerProtos
operator|.
name|Type
operator|.
name|CreateContainer
condition|)
block|{
name|long
name|containerID
init|=
name|requestProto
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|createContainerFutureMap
operator|.
name|remove
argument_list|(
name|containerID
argument_list|)
operator|.
name|complete
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
return|return
name|CompletableFuture
operator|.
name|completedFuture
argument_list|(
name|message
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|completeExceptionally
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
DECL|method|completeExceptionally (Exception e)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|CompletableFuture
argument_list|<
name|T
argument_list|>
name|completeExceptionally
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|CompletableFuture
argument_list|<
name|T
argument_list|>
name|future
init|=
operator|new
name|CompletableFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|future
operator|.
name|completeExceptionally
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
block|}
end_class

end_unit

