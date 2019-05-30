begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.ratis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
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
name|Collection
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
name|CompletableFuture
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
name|transport
operator|.
name|server
operator|.
name|ratis
operator|.
name|ContainerStateMachine
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
name|om
operator|.
name|OzoneManager
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
name|om
operator|.
name|exceptions
operator|.
name|OMException
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
name|om
operator|.
name|helpers
operator|.
name|OMRatisHelper
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
name|proto
operator|.
name|OzoneManagerProtocolProtos
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
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|MultipartInfoApplyInitiateRequest
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
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMRequest
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
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
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
name|protocolPB
operator|.
name|OzoneManagerHARequestHandler
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
name|protocolPB
operator|.
name|OzoneManagerHARequestHandlerImpl
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
name|ratis
operator|.
name|proto
operator|.
name|RaftProtos
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
name|RaftGroupId
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
name|RaftServer
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
name|thirdparty
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|exceptions
operator|.
name|OMException
operator|.
name|STATUS_CODE
import|;
end_import

begin_comment
comment|/**  * The OM StateMachine is the state machine for OM Ratis server. It is  * responsible for applying ratis committed transactions to  * {@link OzoneManager}.  */
end_comment

begin_class
DECL|class|OzoneManagerStateMachine
specifier|public
class|class
name|OzoneManagerStateMachine
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
DECL|field|omRatisServer
specifier|private
specifier|final
name|OzoneManagerRatisServer
name|omRatisServer
decl_stmt|;
DECL|field|ozoneManager
specifier|private
specifier|final
name|OzoneManager
name|ozoneManager
decl_stmt|;
DECL|field|handler
specifier|private
name|OzoneManagerHARequestHandler
name|handler
decl_stmt|;
DECL|field|raftGroupId
specifier|private
name|RaftGroupId
name|raftGroupId
decl_stmt|;
DECL|field|lastAppliedIndex
specifier|private
name|long
name|lastAppliedIndex
init|=
literal|0
decl_stmt|;
DECL|field|ozoneManagerDoubleBuffer
specifier|private
specifier|final
name|OzoneManagerDoubleBuffer
name|ozoneManagerDoubleBuffer
decl_stmt|;
DECL|method|OzoneManagerStateMachine (OzoneManagerRatisServer ratisServer)
specifier|public
name|OzoneManagerStateMachine
parameter_list|(
name|OzoneManagerRatisServer
name|ratisServer
parameter_list|)
block|{
name|this
operator|.
name|omRatisServer
operator|=
name|ratisServer
expr_stmt|;
name|this
operator|.
name|ozoneManager
operator|=
name|omRatisServer
operator|.
name|getOzoneManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|ozoneManagerDoubleBuffer
operator|=
operator|new
name|OzoneManagerDoubleBuffer
argument_list|(
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|handler
operator|=
operator|new
name|OzoneManagerHARequestHandlerImpl
argument_list|(
name|ozoneManager
argument_list|,
name|ozoneManagerDoubleBuffer
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the State Machine with the given server, group and storage.    * TODO: Load the latest snapshot from the file system.    */
annotation|@
name|Override
DECL|method|initialize ( RaftServer server, RaftGroupId id, RaftStorage raftStorage)
specifier|public
name|void
name|initialize
parameter_list|(
name|RaftServer
name|server
parameter_list|,
name|RaftGroupId
name|id
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
name|server
argument_list|,
name|id
argument_list|,
name|raftStorage
argument_list|)
expr_stmt|;
name|this
operator|.
name|raftGroupId
operator|=
name|id
expr_stmt|;
name|storage
operator|.
name|init
argument_list|(
name|raftStorage
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate/pre-process the incoming update request in the state machine.    * @return the content to be written to the log entry. Null means the request    * should be rejected.    * @throws IOException thrown by the state machine while validating    */
annotation|@
name|Override
DECL|method|startTransaction ( RaftClientRequest raftClientRequest)
specifier|public
name|TransactionContext
name|startTransaction
parameter_list|(
name|RaftClientRequest
name|raftClientRequest
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteString
name|messageContent
init|=
name|raftClientRequest
operator|.
name|getMessage
argument_list|()
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|OMRequest
name|omRequest
init|=
name|OMRatisHelper
operator|.
name|convertByteStringToOMRequest
argument_list|(
name|messageContent
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|raftClientRequest
operator|.
name|getRaftGroupId
argument_list|()
operator|.
name|equals
argument_list|(
name|raftGroupId
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|validateRequest
argument_list|(
name|omRequest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|TransactionContext
name|ctxt
init|=
name|TransactionContext
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientRequest
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|this
argument_list|)
operator|.
name|setServerRole
argument_list|(
name|RaftProtos
operator|.
name|RaftPeerRole
operator|.
name|LEADER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ctxt
operator|.
name|setException
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
return|return
name|ctxt
return|;
block|}
return|return
name|handleStartTransactionRequests
argument_list|(
name|raftClientRequest
argument_list|,
name|omRequest
argument_list|)
return|;
block|}
comment|/*    * Apply a committed log entry to the state machine.    */
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
name|OMRequest
name|request
init|=
name|OMRatisHelper
operator|.
name|convertByteStringToOMRequest
argument_list|(
name|trx
operator|.
name|getStateMachineLogEntry
argument_list|()
operator|.
name|getLogData
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|trxLogIndex
init|=
name|trx
operator|.
name|getLogEntry
argument_list|()
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|CompletableFuture
argument_list|<
name|Message
argument_list|>
name|future
init|=
name|CompletableFuture
operator|.
name|supplyAsync
argument_list|(
parameter_list|()
lambda|->
name|runCommand
argument_list|(
name|request
argument_list|,
name|trxLogIndex
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|future
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
comment|/**    * Query the state machine. The request must be read-only.    */
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
name|OMRequest
name|omRequest
init|=
name|OMRatisHelper
operator|.
name|convertByteStringToOMRequest
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
name|queryCommand
argument_list|(
name|omRequest
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
comment|/**    * Take OM Ratis snapshot. Write the snapshot index to file. Snapshot index    * is the log index corresponding to the last applied transaction on the OM    * State Machine.    *    * @return the last applied index on the state machine which has been    * stored in the snapshot file.    */
annotation|@
name|Override
DECL|method|takeSnapshot ()
specifier|public
name|long
name|takeSnapshot
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Saving Ratis snapshot on the OM."
argument_list|)
expr_stmt|;
if|if
condition|(
name|ozoneManager
operator|!=
literal|null
condition|)
block|{
return|return
name|ozoneManager
operator|.
name|saveRatisSnapshot
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
comment|/**    * Notifies the state machine that the raft peer is no longer leader.    */
annotation|@
name|Override
DECL|method|notifyNotLeader (Collection<TransactionContext> pendingEntries)
specifier|public
name|void
name|notifyNotLeader
parameter_list|(
name|Collection
argument_list|<
name|TransactionContext
argument_list|>
name|pendingEntries
parameter_list|)
throws|throws
name|IOException
block|{
name|omRatisServer
operator|.
name|updateServerRole
argument_list|()
expr_stmt|;
block|}
comment|/**    * Handle the RaftClientRequest and return TransactionContext object.    * @param raftClientRequest    * @param omRequest    * @return TransactionContext    */
DECL|method|handleStartTransactionRequests ( RaftClientRequest raftClientRequest, OMRequest omRequest)
specifier|private
name|TransactionContext
name|handleStartTransactionRequests
parameter_list|(
name|RaftClientRequest
name|raftClientRequest
parameter_list|,
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|OMRequest
name|newOmRequest
init|=
literal|null
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|omRequest
operator|.
name|getCmdType
argument_list|()
condition|)
block|{
case|case
name|CreateVolume
case|:
case|case
name|SetVolumeProperty
case|:
case|case
name|DeleteVolume
case|:
name|newOmRequest
operator|=
name|handler
operator|.
name|handleStartTransaction
argument_list|(
name|omRequest
argument_list|)
expr_stmt|;
break|break;
case|case
name|AllocateBlock
case|:
return|return
name|handleAllocateBlock
argument_list|(
name|raftClientRequest
argument_list|,
name|omRequest
argument_list|)
return|;
case|case
name|CreateKey
case|:
return|return
name|handleCreateKeyRequest
argument_list|(
name|raftClientRequest
argument_list|,
name|omRequest
argument_list|)
return|;
case|case
name|InitiateMultiPartUpload
case|:
return|return
name|handleInitiateMultipartUpload
argument_list|(
name|raftClientRequest
argument_list|,
name|omRequest
argument_list|)
return|;
default|default:
return|return
name|TransactionContext
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientRequest
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|this
argument_list|)
operator|.
name|setServerRole
argument_list|(
name|RaftProtos
operator|.
name|RaftPeerRole
operator|.
name|LEADER
argument_list|)
operator|.
name|setLogData
argument_list|(
name|raftClientRequest
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
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|TransactionContext
name|transactionContext
init|=
name|TransactionContext
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientRequest
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|this
argument_list|)
operator|.
name|setServerRole
argument_list|(
name|RaftProtos
operator|.
name|RaftPeerRole
operator|.
name|LEADER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|ex
operator|instanceof
name|OMException
condition|)
block|{
name|IOException
name|ioException
init|=
operator|new
name|IOException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|+
name|STATUS_CODE
operator|+
operator|(
operator|(
name|OMException
operator|)
name|ex
operator|)
operator|.
name|getResult
argument_list|()
argument_list|)
decl_stmt|;
name|transactionContext
operator|.
name|setException
argument_list|(
name|ioException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transactionContext
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in startTransaction for cmdType "
operator|+
name|omRequest
operator|.
name|getCmdType
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
name|transactionContext
return|;
block|}
name|TransactionContext
name|transactionContext
init|=
name|TransactionContext
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientRequest
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|this
argument_list|)
operator|.
name|setServerRole
argument_list|(
name|RaftProtos
operator|.
name|RaftPeerRole
operator|.
name|LEADER
argument_list|)
operator|.
name|setLogData
argument_list|(
name|OMRatisHelper
operator|.
name|convertRequestToByteString
argument_list|(
name|newOmRequest
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|transactionContext
return|;
block|}
DECL|method|handleInitiateMultipartUpload ( RaftClientRequest raftClientRequest, OMRequest omRequest)
specifier|private
name|TransactionContext
name|handleInitiateMultipartUpload
parameter_list|(
name|RaftClientRequest
name|raftClientRequest
parameter_list|,
name|OMRequest
name|omRequest
parameter_list|)
block|{
comment|// Generate a multipart uploadID, and create a new request.
comment|// When applyTransaction happen's all OM's use the same multipartUploadID
comment|// for the key.
name|long
name|time
init|=
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
decl_stmt|;
name|String
name|multipartUploadID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"-"
operator|+
name|time
decl_stmt|;
name|MultipartInfoApplyInitiateRequest
name|multipartInfoApplyInitiateRequest
init|=
name|MultipartInfoApplyInitiateRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyArgs
argument_list|(
name|omRequest
operator|.
name|getInitiateMultiPartUploadRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
argument_list|)
operator|.
name|setMultipartUploadID
argument_list|(
name|multipartUploadID
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OMRequest
operator|.
name|Builder
name|newOmRequest
init|=
name|OMRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|ApplyInitiateMultiPartUpload
argument_list|)
operator|.
name|setInitiateMultiPartUploadApplyRequest
argument_list|(
name|multipartInfoApplyInitiateRequest
argument_list|)
operator|.
name|setClientId
argument_list|(
name|omRequest
operator|.
name|getClientId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|omRequest
operator|.
name|hasTraceID
argument_list|()
condition|)
block|{
name|newOmRequest
operator|.
name|setTraceID
argument_list|(
name|omRequest
operator|.
name|getTraceID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ByteString
name|messageContent
init|=
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|newOmRequest
operator|.
name|build
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|TransactionContext
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientRequest
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|this
argument_list|)
operator|.
name|setServerRole
argument_list|(
name|RaftProtos
operator|.
name|RaftPeerRole
operator|.
name|LEADER
argument_list|)
operator|.
name|setLogData
argument_list|(
name|messageContent
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Handle createKey Request, which needs a special handling. This request    * needs to be executed on the leader, and the response received from this    * request we need to create a ApplyKeyRequest and create a    * TransactionContext object.    */
DECL|method|handleCreateKeyRequest ( RaftClientRequest raftClientRequest, OMRequest omRequest)
specifier|private
name|TransactionContext
name|handleCreateKeyRequest
parameter_list|(
name|RaftClientRequest
name|raftClientRequest
parameter_list|,
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|OMResponse
name|omResponse
init|=
name|handler
operator|.
name|handle
argument_list|(
name|omRequest
argument_list|)
decl_stmt|;
comment|// TODO: if not success should we retry depending on the error if it is
comment|//  retriable?
if|if
condition|(
operator|!
name|omResponse
operator|.
name|getSuccess
argument_list|()
condition|)
block|{
name|TransactionContext
name|transactionContext
init|=
name|TransactionContext
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientRequest
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|this
argument_list|)
operator|.
name|setServerRole
argument_list|(
name|RaftProtos
operator|.
name|RaftPeerRole
operator|.
name|LEADER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|transactionContext
operator|.
name|setException
argument_list|(
name|constructExceptionForFailedRequest
argument_list|(
name|omResponse
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|transactionContext
return|;
block|}
comment|// Get original request
name|OzoneManagerProtocolProtos
operator|.
name|CreateKeyRequest
name|createKeyRequest
init|=
name|omRequest
operator|.
name|getCreateKeyRequest
argument_list|()
decl_stmt|;
comment|// Create Applykey Request.
name|OzoneManagerProtocolProtos
operator|.
name|ApplyCreateKeyRequest
name|applyCreateKeyRequest
init|=
name|OzoneManagerProtocolProtos
operator|.
name|ApplyCreateKeyRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCreateKeyRequest
argument_list|(
name|createKeyRequest
argument_list|)
operator|.
name|setCreateKeyResponse
argument_list|(
name|omResponse
operator|.
name|getCreateKeyResponse
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OMRequest
operator|.
name|Builder
name|newOmRequest
init|=
name|OMRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|ApplyCreateKey
argument_list|)
operator|.
name|setApplyCreateKeyRequest
argument_list|(
name|applyCreateKeyRequest
argument_list|)
operator|.
name|setClientId
argument_list|(
name|omRequest
operator|.
name|getClientId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|omRequest
operator|.
name|hasTraceID
argument_list|()
condition|)
block|{
name|newOmRequest
operator|.
name|setTraceID
argument_list|(
name|omRequest
operator|.
name|getTraceID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ByteString
name|messageContent
init|=
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|newOmRequest
operator|.
name|build
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|TransactionContext
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientRequest
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|this
argument_list|)
operator|.
name|setServerRole
argument_list|(
name|RaftProtos
operator|.
name|RaftPeerRole
operator|.
name|LEADER
argument_list|)
operator|.
name|setLogData
argument_list|(
name|messageContent
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Handle AllocateBlock Request, which needs a special handling. This    * request needs to be executed on the leader, where it connects to SCM and    * get Block information.    * @param raftClientRequest    * @param omRequest    * @return TransactionContext    */
DECL|method|handleAllocateBlock ( RaftClientRequest raftClientRequest, OMRequest omRequest)
specifier|private
name|TransactionContext
name|handleAllocateBlock
parameter_list|(
name|RaftClientRequest
name|raftClientRequest
parameter_list|,
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|OMResponse
name|omResponse
init|=
name|handler
operator|.
name|handle
argument_list|(
name|omRequest
argument_list|)
decl_stmt|;
comment|// If request is failed, no need to proceed further.
comment|// Setting the exception with omResponse message and code.
comment|// TODO: the allocate block fails when scm is in safe mode or when scm is
comment|//  down, but that error is not correctly received in OM end, once that
comment|//  is fixed, we need to see how to handle this failure case or how we
comment|//  need to retry or how to handle this scenario. For other errors like
comment|//  KEY_NOT_FOUND, we don't need a retry/
if|if
condition|(
operator|!
name|omResponse
operator|.
name|getSuccess
argument_list|()
condition|)
block|{
name|TransactionContext
name|transactionContext
init|=
name|TransactionContext
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientRequest
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|this
argument_list|)
operator|.
name|setServerRole
argument_list|(
name|RaftProtos
operator|.
name|RaftPeerRole
operator|.
name|LEADER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|transactionContext
operator|.
name|setException
argument_list|(
name|constructExceptionForFailedRequest
argument_list|(
name|omResponse
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|transactionContext
return|;
block|}
comment|// Get original request
name|OzoneManagerProtocolProtos
operator|.
name|AllocateBlockRequest
name|allocateBlockRequest
init|=
name|omRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
decl_stmt|;
comment|// Create new AllocateBlockRequest with keyLocation set.
name|OzoneManagerProtocolProtos
operator|.
name|AllocateBlockRequest
name|newAllocateBlockRequest
init|=
name|OzoneManagerProtocolProtos
operator|.
name|AllocateBlockRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|mergeFrom
argument_list|(
name|allocateBlockRequest
argument_list|)
operator|.
name|setKeyLocation
argument_list|(
name|omResponse
operator|.
name|getAllocateBlockResponse
argument_list|()
operator|.
name|getKeyLocation
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OMRequest
name|newOmRequest
init|=
name|omRequest
operator|.
name|toBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|AllocateBlock
argument_list|)
operator|.
name|setAllocateBlockRequest
argument_list|(
name|newAllocateBlockRequest
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ByteString
name|messageContent
init|=
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|newOmRequest
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|TransactionContext
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientRequest
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|this
argument_list|)
operator|.
name|setServerRole
argument_list|(
name|RaftProtos
operator|.
name|RaftPeerRole
operator|.
name|LEADER
argument_list|)
operator|.
name|setLogData
argument_list|(
name|messageContent
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Construct IOException message for failed requests in StartTransaction.    * @param omResponse    * @return    */
DECL|method|constructExceptionForFailedRequest ( OMResponse omResponse)
specifier|private
name|IOException
name|constructExceptionForFailedRequest
parameter_list|(
name|OMResponse
name|omResponse
parameter_list|)
block|{
return|return
operator|new
name|IOException
argument_list|(
name|omResponse
operator|.
name|getMessage
argument_list|()
operator|+
literal|" "
operator|+
name|STATUS_CODE
operator|+
name|omResponse
operator|.
name|getStatus
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Submits write request to OM and returns the response Message.    * @param request OMRequest    * @return response from OM    * @throws ServiceException    */
DECL|method|runCommand (OMRequest request, long trxLogIndex)
specifier|private
name|Message
name|runCommand
parameter_list|(
name|OMRequest
name|request
parameter_list|,
name|long
name|trxLogIndex
parameter_list|)
block|{
name|OMResponse
name|response
init|=
name|handler
operator|.
name|handleApplyTransaction
argument_list|(
name|request
argument_list|,
name|trxLogIndex
argument_list|)
decl_stmt|;
name|lastAppliedIndex
operator|=
name|trxLogIndex
expr_stmt|;
return|return
name|OMRatisHelper
operator|.
name|convertResponseToMessage
argument_list|(
name|response
argument_list|)
return|;
block|}
comment|/**    * Submits read request to OM and returns the response Message.    * @param request OMRequest    * @return response from OM    * @throws ServiceException    */
DECL|method|queryCommand (OMRequest request)
specifier|private
name|Message
name|queryCommand
parameter_list|(
name|OMRequest
name|request
parameter_list|)
block|{
name|OMResponse
name|response
init|=
name|handler
operator|.
name|handle
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|OMRatisHelper
operator|.
name|convertResponseToMessage
argument_list|(
name|response
argument_list|)
return|;
block|}
DECL|method|getLastAppliedIndex ()
specifier|public
name|long
name|getLastAppliedIndex
parameter_list|()
block|{
return|return
name|lastAppliedIndex
return|;
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
name|VisibleForTesting
DECL|method|setHandler (OzoneManagerHARequestHandler handler)
specifier|public
name|void
name|setHandler
parameter_list|(
name|OzoneManagerHARequestHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setRaftGroupId (RaftGroupId raftGroupId)
specifier|public
name|void
name|setRaftGroupId
parameter_list|(
name|RaftGroupId
name|raftGroupId
parameter_list|)
block|{
name|this
operator|.
name|raftGroupId
operator|=
name|raftGroupId
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|ozoneManagerDoubleBuffer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

