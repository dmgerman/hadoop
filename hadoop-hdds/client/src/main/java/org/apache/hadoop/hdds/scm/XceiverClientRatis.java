begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm
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
name|hadoop
operator|.
name|hdds
operator|.
name|HddsUtils
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
name|MultipleIOException
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
name|retry
operator|.
name|RetryPolicy
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
name|client
operator|.
name|HddsClientUtils
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
name|Pipeline
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
name|proto
operator|.
name|HddsProtos
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
name|RatisHelper
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
name|client
operator|.
name|RaftClient
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
name|RaftClientReply
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
name|RaftGroup
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
name|RaftPeer
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
name|rpc
operator|.
name|RpcType
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
name|rpc
operator|.
name|SupportedRpcType
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
name|util
operator|.
name|CheckedBiConsumer
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Objects
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
name|CompletionException
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
name|ExecutionException
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_comment
comment|/**  * An abstract implementation of {@link XceiverClientSpi} using Ratis.  * The underlying RPC mechanism can be chosen via the constructor.  */
end_comment

begin_class
DECL|class|XceiverClientRatis
specifier|public
specifier|final
class|class
name|XceiverClientRatis
extends|extends
name|XceiverClientSpi
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
name|XceiverClientRatis
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|newXceiverClientRatis ( Pipeline pipeline, Configuration ozoneConf)
specifier|public
specifier|static
name|XceiverClientRatis
name|newXceiverClientRatis
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|Configuration
name|ozoneConf
parameter_list|)
block|{
specifier|final
name|String
name|rpcType
init|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxOutstandingRequests
init|=
name|HddsClientUtils
operator|.
name|getMaxOutstandingRequests
argument_list|(
name|ozoneConf
argument_list|)
decl_stmt|;
specifier|final
name|RetryPolicy
name|retryPolicy
init|=
name|RatisHelper
operator|.
name|createRetryPolicy
argument_list|(
name|ozoneConf
argument_list|)
decl_stmt|;
return|return
operator|new
name|XceiverClientRatis
argument_list|(
name|pipeline
argument_list|,
name|SupportedRpcType
operator|.
name|valueOfIgnoreCase
argument_list|(
name|rpcType
argument_list|)
argument_list|,
name|maxOutstandingRequests
argument_list|,
name|retryPolicy
argument_list|)
return|;
block|}
DECL|field|pipeline
specifier|private
specifier|final
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|rpcType
specifier|private
specifier|final
name|RpcType
name|rpcType
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|RaftClient
argument_list|>
name|client
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|maxOutstandingRequests
specifier|private
specifier|final
name|int
name|maxOutstandingRequests
decl_stmt|;
DECL|field|retryPolicy
specifier|private
specifier|final
name|RetryPolicy
name|retryPolicy
decl_stmt|;
comment|/**    * Constructs a client.    */
DECL|method|XceiverClientRatis (Pipeline pipeline, RpcType rpcType, int maxOutStandingChunks, RetryPolicy retryPolicy)
specifier|private
name|XceiverClientRatis
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|RpcType
name|rpcType
parameter_list|,
name|int
name|maxOutStandingChunks
parameter_list|,
name|RetryPolicy
name|retryPolicy
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|pipeline
operator|=
name|pipeline
expr_stmt|;
name|this
operator|.
name|rpcType
operator|=
name|rpcType
expr_stmt|;
name|this
operator|.
name|maxOutstandingRequests
operator|=
name|maxOutStandingChunks
expr_stmt|;
name|this
operator|.
name|retryPolicy
operator|=
name|retryPolicy
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|createPipeline ()
specifier|public
name|void
name|createPipeline
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|RaftGroup
name|group
init|=
name|RatisHelper
operator|.
name|newRaftGroup
argument_list|(
name|pipeline
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"creating pipeline:{} with {}"
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|callRatisRpc
argument_list|(
name|pipeline
operator|.
name|getMachines
argument_list|()
argument_list|,
parameter_list|(
name|raftClient
parameter_list|,
name|peer
parameter_list|)
lambda|->
name|raftClient
operator|.
name|groupAdd
argument_list|(
name|group
argument_list|,
name|peer
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|destroyPipeline ()
specifier|public
name|void
name|destroyPipeline
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|RaftGroup
name|group
init|=
name|RatisHelper
operator|.
name|newRaftGroup
argument_list|(
name|pipeline
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"destroying pipeline:{} with {}"
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|callRatisRpc
argument_list|(
name|pipeline
operator|.
name|getMachines
argument_list|()
argument_list|,
parameter_list|(
name|raftClient
parameter_list|,
name|peer
parameter_list|)
lambda|->
name|raftClient
operator|.
name|groupRemove
argument_list|(
name|group
operator|.
name|getGroupId
argument_list|()
argument_list|,
name|peer
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns Ratis as pipeline Type.    *    * @return - Ratis    */
annotation|@
name|Override
DECL|method|getPipelineType ()
specifier|public
name|HddsProtos
operator|.
name|ReplicationType
name|getPipelineType
parameter_list|()
block|{
return|return
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
return|;
block|}
DECL|method|callRatisRpc (List<DatanodeDetails> datanodes, CheckedBiConsumer<RaftClient, RaftPeer, IOException> rpc)
specifier|private
name|void
name|callRatisRpc
parameter_list|(
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
parameter_list|,
name|CheckedBiConsumer
argument_list|<
name|RaftClient
argument_list|,
name|RaftPeer
argument_list|,
name|IOException
argument_list|>
name|rpc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|datanodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|List
argument_list|<
name|IOException
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|datanodes
operator|.
name|parallelStream
argument_list|()
operator|.
name|forEach
argument_list|(
name|d
lambda|->
block|{
specifier|final
name|RaftPeer
name|p
init|=
name|RatisHelper
operator|.
name|toRaftPeer
argument_list|(
name|d
argument_list|)
decl_stmt|;
try|try
init|(
name|RaftClient
name|client
init|=
name|RatisHelper
operator|.
name|newRaftClient
argument_list|(
name|rpcType
argument_list|,
name|p
argument_list|,
name|retryPolicy
argument_list|)
init|)
block|{
name|rpc
operator|.
name|accept
argument_list|(
name|client
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Failed invoke Ratis rpc "
operator|+
name|rpc
operator|+
literal|" for "
operator|+
name|d
argument_list|,
name|ioe
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|MultipleIOException
operator|.
name|createIOException
argument_list|(
name|exceptions
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPipeline ()
specifier|public
name|Pipeline
name|getPipeline
parameter_list|()
block|{
return|return
name|pipeline
return|;
block|}
annotation|@
name|Override
DECL|method|connect ()
specifier|public
name|void
name|connect
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to pipeline:{} leader:{}"
argument_list|,
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|RatisHelper
operator|.
name|toRaftPeerId
argument_list|(
name|pipeline
operator|.
name|getLeader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO : XceiverClient ratis should pass the config value of
comment|// maxOutstandingRequests so as to set the upper bound on max no of async
comment|// requests to be handled by raft client
if|if
condition|(
operator|!
name|client
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|RatisHelper
operator|.
name|newRaftClient
argument_list|(
name|rpcType
argument_list|,
name|getPipeline
argument_list|()
argument_list|,
name|retryPolicy
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Client is already connected."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
specifier|final
name|RaftClient
name|c
init|=
name|client
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getClient ()
specifier|private
name|RaftClient
name|getClient
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|client
operator|.
name|get
argument_list|()
argument_list|,
literal|"client is null"
argument_list|)
return|;
block|}
DECL|method|sendRequest (ContainerCommandRequestProto request)
specifier|private
name|RaftClientReply
name|sendRequest
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|isReadOnlyRequest
init|=
name|HddsUtils
operator|.
name|isReadOnly
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ByteString
name|byteString
init|=
name|request
operator|.
name|toByteString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"sendCommand {} {}"
argument_list|,
name|isReadOnlyRequest
argument_list|,
name|request
argument_list|)
expr_stmt|;
specifier|final
name|RaftClientReply
name|reply
init|=
name|isReadOnlyRequest
condition|?
name|getClient
argument_list|()
operator|.
name|sendReadOnly
argument_list|(
parameter_list|()
lambda|->
name|byteString
argument_list|)
else|:
name|getClient
argument_list|()
operator|.
name|send
argument_list|(
parameter_list|()
lambda|->
name|byteString
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"reply {} {}"
argument_list|,
name|isReadOnlyRequest
argument_list|,
name|reply
argument_list|)
expr_stmt|;
return|return
name|reply
return|;
block|}
DECL|method|sendRequestAsync ( ContainerCommandRequestProto request)
specifier|private
name|CompletableFuture
argument_list|<
name|RaftClientReply
argument_list|>
name|sendRequestAsync
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|isReadOnlyRequest
init|=
name|HddsUtils
operator|.
name|isReadOnly
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ByteString
name|byteString
init|=
name|request
operator|.
name|toByteString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"sendCommandAsync {} {}"
argument_list|,
name|isReadOnlyRequest
argument_list|,
name|request
argument_list|)
expr_stmt|;
return|return
name|isReadOnlyRequest
condition|?
name|getClient
argument_list|()
operator|.
name|sendReadOnlyAsync
argument_list|(
parameter_list|()
lambda|->
name|byteString
argument_list|)
else|:
name|getClient
argument_list|()
operator|.
name|sendAsync
argument_list|(
parameter_list|()
lambda|->
name|byteString
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sendCommand ( ContainerCommandRequestProto request)
specifier|public
name|ContainerCommandResponseProto
name|sendCommand
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|RaftClientReply
name|reply
init|=
name|sendRequest
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|reply
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Could not execute the request %s"
argument_list|,
name|request
argument_list|)
argument_list|)
throw|;
block|}
name|Preconditions
operator|.
name|checkState
argument_list|(
name|reply
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ContainerCommandResponseProto
operator|.
name|parseFrom
argument_list|(
name|reply
operator|.
name|getMessage
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Sends a given command to server gets a waitable future back.    *    * @param request Request    * @return Response to the command    * @throws IOException    */
annotation|@
name|Override
DECL|method|sendCommandAsync ( ContainerCommandRequestProto request)
specifier|public
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
name|sendCommandAsync
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
return|return
name|sendRequestAsync
argument_list|(
name|request
argument_list|)
operator|.
name|whenComplete
argument_list|(
parameter_list|(
name|reply
parameter_list|,
name|e
parameter_list|)
lambda|->
name|LOG
operator|.
name|debug
argument_list|(
literal|"received reply {} for request: {} exception: {}"
argument_list|,
name|request
argument_list|,
name|reply
argument_list|,
name|e
argument_list|)
argument_list|)
operator|.
name|thenApply
argument_list|(
name|reply
lambda|->
block|{
lambda|try
block|{
return|return
name|ContainerCommandResponseProto
operator|.
name|parseFrom
argument_list|(
name|reply
operator|.
name|getMessage
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidProtocolBufferException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CompletionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|)
class|;
end_class

unit|} }
end_unit

