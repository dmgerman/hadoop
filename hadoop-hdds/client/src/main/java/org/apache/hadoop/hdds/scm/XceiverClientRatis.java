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
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
name|OptionalLong
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
name|TimeUnit
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
name|TimeoutException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|pipeline
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
name|security
operator|.
name|x509
operator|.
name|SecurityConfig
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
name|tracing
operator|.
name|TracingUtil
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
name|hdds
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
name|grpc
operator|.
name|GrpcTlsConfig
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
name|GroupMismatchException
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
name|RaftException
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
name|InvalidProtocolBufferException
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
name|TimeDuration
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|io
operator|.
name|opentracing
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|io
operator|.
name|opentracing
operator|.
name|util
operator|.
name|GlobalTracer
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
name|XceiverClientRatis
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|newXceiverClientRatis ( org.apache.hadoop.hdds.scm.pipeline.Pipeline pipeline, Configuration ozoneConf)
specifier|public
specifier|static
name|XceiverClientRatis
name|newXceiverClientRatis
parameter_list|(
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
name|pipeline
operator|.
name|Pipeline
name|pipeline
parameter_list|,
name|Configuration
name|ozoneConf
parameter_list|)
block|{
return|return
name|newXceiverClientRatis
argument_list|(
name|pipeline
argument_list|,
name|ozoneConf
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newXceiverClientRatis ( org.apache.hadoop.hdds.scm.pipeline.Pipeline pipeline, Configuration ozoneConf, X509Certificate caCert)
specifier|public
specifier|static
name|XceiverClientRatis
name|newXceiverClientRatis
parameter_list|(
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
name|pipeline
operator|.
name|Pipeline
name|pipeline
parameter_list|,
name|Configuration
name|ozoneConf
parameter_list|,
name|X509Certificate
name|caCert
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
name|TimeDuration
name|clientRequestTimeout
init|=
name|RatisHelper
operator|.
name|getClientRequestTimeout
argument_list|(
name|ozoneConf
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
specifier|final
name|GrpcTlsConfig
name|tlsConfig
init|=
name|RatisHelper
operator|.
name|createTlsClientConfig
argument_list|(
operator|new
name|SecurityConfig
argument_list|(
name|ozoneConf
argument_list|)
argument_list|,
name|caCert
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
argument_list|,
name|tlsConfig
argument_list|,
name|clientRequestTimeout
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
DECL|field|tlsConfig
specifier|private
specifier|final
name|GrpcTlsConfig
name|tlsConfig
decl_stmt|;
DECL|field|clientRequestTimeout
specifier|private
specifier|final
name|TimeDuration
name|clientRequestTimeout
decl_stmt|;
comment|// Map to track commit index at every server
DECL|field|commitInfoMap
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|UUID
argument_list|,
name|Long
argument_list|>
name|commitInfoMap
decl_stmt|;
DECL|field|metrics
specifier|private
name|XceiverClientMetrics
name|metrics
decl_stmt|;
comment|/**    * Constructs a client.    */
DECL|method|XceiverClientRatis (Pipeline pipeline, RpcType rpcType, int maxOutStandingChunks, RetryPolicy retryPolicy, GrpcTlsConfig tlsConfig, TimeDuration timeout)
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
parameter_list|,
name|GrpcTlsConfig
name|tlsConfig
parameter_list|,
name|TimeDuration
name|timeout
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
name|commitInfoMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|tlsConfig
operator|=
name|tlsConfig
expr_stmt|;
name|this
operator|.
name|clientRequestTimeout
operator|=
name|timeout
expr_stmt|;
name|metrics
operator|=
name|XceiverClientManager
operator|.
name|getXceiverClientMetrics
argument_list|()
expr_stmt|;
block|}
DECL|method|updateCommitInfosMap ( Collection<RaftProtos.CommitInfoProto> commitInfoProtos)
specifier|private
name|void
name|updateCommitInfosMap
parameter_list|(
name|Collection
argument_list|<
name|RaftProtos
operator|.
name|CommitInfoProto
argument_list|>
name|commitInfoProtos
parameter_list|)
block|{
comment|// if the commitInfo map is empty, just update the commit indexes for each
comment|// of the servers
if|if
condition|(
name|commitInfoMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|commitInfoProtos
operator|.
name|forEach
argument_list|(
name|proto
lambda|->
name|commitInfoMap
operator|.
name|put
argument_list|(
name|RatisHelper
operator|.
name|toDatanodeId
argument_list|(
name|proto
operator|.
name|getServer
argument_list|()
argument_list|)
argument_list|,
name|proto
operator|.
name|getCommitIndex
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// In case the commit is happening 2 way, just update the commitIndex
comment|// for the servers which have been successfully updating the commit
comment|// indexes. This is important because getReplicatedMinCommitIndex()
comment|// should always return the min commit index out of the nodes which have
comment|// been replicating data successfully.
block|}
else|else
block|{
name|commitInfoProtos
operator|.
name|forEach
argument_list|(
name|proto
lambda|->
name|commitInfoMap
operator|.
name|computeIfPresent
argument_list|(
name|RatisHelper
operator|.
name|toDatanodeId
argument_list|(
name|proto
operator|.
name|getServer
argument_list|()
argument_list|)
argument_list|,
parameter_list|(
name|address
parameter_list|,
name|index
parameter_list|)
lambda|->
block|{
name|index
operator|=
name|proto
operator|.
name|getCommitIndex
argument_list|()
argument_list|;
return|return
name|index
return|;
block|}
block|)
block|)
class|;
end_class

begin_comment
unit|}   }
comment|/**    * Returns Ratis as pipeline Type.    *    * @return - Ratis    */
end_comment

begin_function
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
end_function

begin_function
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
end_function

begin_function
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
literal|"Connecting to pipeline:{} datanode:{}"
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
name|getFirstNode
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
argument_list|,
name|maxOutstandingRequests
argument_list|,
name|tlsConfig
argument_list|,
name|clientRequestTimeout
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
end_function

begin_function
annotation|@
name|Override
DECL|method|connect (String encodedToken)
specifier|public
name|void
name|connect
parameter_list|(
name|String
name|encodedToken
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Block tokens are not "
operator|+
literal|"implemented for Ratis clients."
argument_list|)
throw|;
block|}
end_function

begin_function
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
name|closeRaftClient
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|closeRaftClient (RaftClient raftClient)
specifier|private
name|void
name|closeRaftClient
parameter_list|(
name|RaftClient
name|raftClient
parameter_list|)
block|{
try|try
block|{
name|raftClient
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
end_function

begin_function
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
end_function

begin_function
annotation|@
name|VisibleForTesting
DECL|method|getCommitInfoMap ()
specifier|public
name|ConcurrentHashMap
argument_list|<
name|UUID
argument_list|,
name|Long
argument_list|>
name|getCommitInfoMap
parameter_list|()
block|{
return|return
name|commitInfoMap
return|;
block|}
end_function

begin_function
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
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|GlobalTracer
operator|.
name|get
argument_list|()
operator|.
name|buildSpan
argument_list|(
literal|"XceiverClientRatis."
operator|+
name|request
operator|.
name|getCmdType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|startActive
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|ContainerCommandRequestProto
name|finalPayload
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|(
name|request
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|boolean
name|isReadOnlyRequest
init|=
name|HddsUtils
operator|.
name|isReadOnly
argument_list|(
name|finalPayload
argument_list|)
decl_stmt|;
name|ByteString
name|byteString
init|=
name|finalPayload
operator|.
name|toByteString
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
literal|"sendCommandAsync {} {}"
argument_list|,
name|isReadOnlyRequest
argument_list|,
name|sanitizeForDebug
argument_list|(
name|finalPayload
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
block|}
end_function

begin_function
DECL|method|sanitizeForDebug ( ContainerCommandRequestProto request)
specifier|private
name|ContainerCommandRequestProto
name|sanitizeForDebug
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
block|{
switch|switch
condition|(
name|request
operator|.
name|getCmdType
argument_list|()
condition|)
block|{
case|case
name|PutSmallFile
case|:
return|return
name|request
operator|.
name|toBuilder
argument_list|()
operator|.
name|setPutSmallFile
argument_list|(
name|request
operator|.
name|getPutSmallFile
argument_list|()
operator|.
name|toBuilder
argument_list|()
operator|.
name|clearData
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|WriteChunk
case|:
return|return
name|request
operator|.
name|toBuilder
argument_list|()
operator|.
name|setWriteChunk
argument_list|(
name|request
operator|.
name|getWriteChunk
argument_list|()
operator|.
name|toBuilder
argument_list|()
operator|.
name|clearData
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
default|default:
return|return
name|request
return|;
block|}
block|}
end_function

begin_comment
comment|// gets the minimum log index replicated to all servers
end_comment

begin_function
annotation|@
name|Override
DECL|method|getReplicatedMinCommitIndex ()
specifier|public
name|long
name|getReplicatedMinCommitIndex
parameter_list|()
block|{
name|OptionalLong
name|minIndex
init|=
name|commitInfoMap
operator|.
name|values
argument_list|()
operator|.
name|parallelStream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|v
lambda|->
name|v
argument_list|)
operator|.
name|min
argument_list|()
decl_stmt|;
return|return
name|minIndex
operator|.
name|isPresent
argument_list|()
condition|?
name|minIndex
operator|.
name|getAsLong
argument_list|()
else|:
literal|0
return|;
block|}
end_function

begin_function
DECL|method|addDatanodetoReply (UUID address, XceiverClientReply reply)
specifier|private
name|void
name|addDatanodetoReply
parameter_list|(
name|UUID
name|address
parameter_list|,
name|XceiverClientReply
name|reply
parameter_list|)
block|{
name|DatanodeDetails
operator|.
name|Builder
name|builder
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setUuid
argument_list|(
name|address
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|reply
operator|.
name|addDatanode
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|watchForCommit (long index, long timeout)
specifier|public
name|XceiverClientReply
name|watchForCommit
parameter_list|(
name|long
name|index
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
throws|,
name|IOException
block|{
name|long
name|commitIndex
init|=
name|getReplicatedMinCommitIndex
argument_list|()
decl_stmt|;
name|XceiverClientReply
name|clientReply
init|=
operator|new
name|XceiverClientReply
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitIndex
operator|>=
name|index
condition|)
block|{
comment|// return the min commit index till which the log has been replicated to
comment|// all servers
name|clientReply
operator|.
name|setLogIndex
argument_list|(
name|commitIndex
argument_list|)
expr_stmt|;
return|return
name|clientReply
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"commit index : {} watch timeout : {}"
argument_list|,
name|index
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|RaftClientReply
name|reply
decl_stmt|;
try|try
block|{
name|CompletableFuture
argument_list|<
name|RaftClientReply
argument_list|>
name|replyFuture
init|=
name|getClient
argument_list|()
operator|.
name|sendWatchAsync
argument_list|(
name|index
argument_list|,
name|RaftProtos
operator|.
name|ReplicationLevel
operator|.
name|ALL_COMMITTED
argument_list|)
decl_stmt|;
name|replyFuture
operator|.
name|get
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Throwable
name|t
init|=
name|HddsClientUtils
operator|.
name|checkForException
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"3 way commit failed on pipeline {}"
argument_list|,
name|pipeline
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|GroupMismatchException
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|reply
operator|=
name|getClient
argument_list|()
operator|.
name|sendWatchAsync
argument_list|(
name|index
argument_list|,
name|RaftProtos
operator|.
name|ReplicationLevel
operator|.
name|MAJORITY_COMMITTED
argument_list|)
operator|.
name|get
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RaftProtos
operator|.
name|CommitInfoProto
argument_list|>
name|commitInfoProtoList
init|=
name|reply
operator|.
name|getCommitInfos
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|i
lambda|->
name|i
operator|.
name|getCommitIndex
argument_list|()
operator|<
name|index
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|commitInfoProtoList
operator|.
name|parallelStream
argument_list|()
operator|.
name|forEach
argument_list|(
name|proto
lambda|->
block|{
name|UUID
name|address
init|=
name|RatisHelper
operator|.
name|toDatanodeId
argument_list|(
name|proto
operator|.
name|getServer
argument_list|()
argument_list|)
decl_stmt|;
name|addDatanodetoReply
argument_list|(
name|address
argument_list|,
name|clientReply
argument_list|)
expr_stmt|;
comment|// since 3 way commit has failed, the updated map from now on  will
comment|// only store entries for those datanodes which have had successful
comment|// replication.
name|commitInfoMap
operator|.
name|remove
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not commit index {} on pipeline {} to all the nodes. "
operator|+
literal|"Server {} has failed. Committed by majority."
argument_list|,
name|index
argument_list|,
name|pipeline
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
name|clientReply
operator|.
name|setLogIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|clientReply
return|;
block|}
end_function

begin_comment
comment|/**    * Sends a given command to server gets a waitable future back.    *    * @param request Request    * @return Response to the command    */
end_comment

begin_function
annotation|@
name|Override
DECL|method|sendCommandAsync ( ContainerCommandRequestProto request)
specifier|public
name|XceiverClientReply
name|sendCommandAsync
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
block|{
name|XceiverClientReply
name|asyncReply
init|=
operator|new
name|XceiverClientReply
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|long
name|requestTime
init|=
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
decl_stmt|;
name|CompletableFuture
argument_list|<
name|RaftClientReply
argument_list|>
name|raftClientReply
init|=
name|sendRequestAsync
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|incrPendingContainerOpsMetrics
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
expr_stmt|;
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
name|containerCommandResponse
init|=
name|raftClientReply
operator|.
name|whenComplete
argument_list|(
parameter_list|(
name|reply
parameter_list|,
name|e
parameter_list|)
lambda|->
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"received reply {} for request: cmdType={} containerID={}"
operator|+
literal|" pipelineID={} traceID={} exception: {}"
argument_list|,
name|reply
argument_list|,
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|,
name|request
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|request
operator|.
name|getPipelineID
argument_list|()
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|decrPendingContainerOpsMetrics
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|addContainerOpsLatency
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|,
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
operator|-
name|requestTime
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|thenApply
argument_list|(
name|reply
lambda|->
block|{
try|try
block|{
if|if
condition|(
operator|!
name|reply
operator|.
name|isSuccess
argument_list|()
condition|)
block|{
comment|// in case of raft retry failure, the raft client is
comment|// not able to connect to the leader hence the pipeline
comment|// can not be used but this instance of RaftClient will close
comment|// and refreshed again. In case the client cannot connect to
comment|// leader, getClient call will fail.
comment|// No need to set the failed Server ID here. Ozone client
comment|// will directly exclude this pipeline in next allocate block
comment|// to SCM as in this case, it is the raft client which is not
comment|// able to connect to leader in the pipeline, though the
comment|// pipeline can still be functional.
name|RaftException
name|exception
init|=
name|reply
operator|.
name|getException
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|exception
argument_list|,
literal|"Raft reply failure but "
operator|+
literal|"no exception propagated."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|CompletionException
argument_list|(
name|exception
argument_list|)
throw|;
block|}
name|ContainerCommandResponseProto
name|response
init|=
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
decl_stmt|;
name|UUID
name|serverId
init|=
name|RatisHelper
operator|.
name|toDatanodeId
argument_list|(
name|reply
operator|.
name|getReplierId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getResult
argument_list|()
operator|==
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
condition|)
block|{
name|updateCommitInfosMap
argument_list|(
name|reply
operator|.
name|getCommitInfos
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|asyncReply
operator|.
name|setLogIndex
argument_list|(
name|reply
operator|.
name|getLogIndex
argument_list|()
argument_list|)
expr_stmt|;
name|addDatanodetoReply
argument_list|(
name|serverId
argument_list|,
name|asyncReply
argument_list|)
expr_stmt|;
return|return
name|response
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
argument_list|)
decl_stmt|;
name|asyncReply
operator|.
name|setResponse
argument_list|(
name|containerCommandResponse
argument_list|)
expr_stmt|;
return|return
name|asyncReply
return|;
block|}
end_function

unit|}
end_unit

