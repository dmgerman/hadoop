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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineReport
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
name|ClosePipelineInfo
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
name|PipelineAction
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
name|HddsServerUtil
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
name|PipelineID
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
name|OzoneConfigKeys
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
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServerSpi
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
name|RaftConfigKeys
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
name|RaftClientConfigKeys
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
name|grpc
operator|.
name|GrpcConfigKeys
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
name|netty
operator|.
name|NettyConfigKeys
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
name|ClientId
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
name|NotLeaderException
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
name|StateMachineException
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
name|RaftServerConfigKeys
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
name|proto
operator|.
name|RaftProtos
operator|.
name|RoleInfoProto
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
operator|.
name|ReplicationLevel
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
name|SizeInBytes
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
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
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
name|ArrayBlockingQueue
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Creates a ratis server endpoint that acts as the communication layer for  * Ozone containers.  */
end_comment

begin_class
DECL|class|XceiverServerRatis
specifier|public
specifier|final
class|class
name|XceiverServerRatis
implements|implements
name|XceiverServerSpi
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
name|XceiverServerRatis
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CALL_ID_COUNTER
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|CALL_ID_COUNTER
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|method|nextCallId ()
specifier|private
specifier|static
name|long
name|nextCallId
parameter_list|()
block|{
return|return
name|CALL_ID_COUNTER
operator|.
name|getAndIncrement
argument_list|()
operator|&
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
DECL|field|port
specifier|private
specifier|final
name|int
name|port
decl_stmt|;
DECL|field|server
specifier|private
specifier|final
name|RaftServer
name|server
decl_stmt|;
DECL|field|chunkExecutor
specifier|private
name|ThreadPoolExecutor
name|chunkExecutor
decl_stmt|;
DECL|field|clientId
specifier|private
name|ClientId
name|clientId
init|=
name|ClientId
operator|.
name|randomId
argument_list|()
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|StateContext
name|context
decl_stmt|;
DECL|field|replicationLevel
specifier|private
specifier|final
name|ReplicationLevel
name|replicationLevel
decl_stmt|;
DECL|field|nodeFailureTimeoutMs
specifier|private
name|long
name|nodeFailureTimeoutMs
decl_stmt|;
DECL|field|stateMachine
specifier|private
name|ContainerStateMachine
name|stateMachine
decl_stmt|;
DECL|method|XceiverServerRatis (DatanodeDetails dd, int port, ContainerDispatcher dispatcher, Configuration conf, StateContext context)
specifier|private
name|XceiverServerRatis
parameter_list|(
name|DatanodeDetails
name|dd
parameter_list|,
name|int
name|port
parameter_list|,
name|ContainerDispatcher
name|dispatcher
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|StateContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|dd
argument_list|,
literal|"id == null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|RaftProperties
name|serverProperties
init|=
name|newRaftProperties
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numWriteChunkThreads
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_DEFAULT
argument_list|)
decl_stmt|;
name|chunkExecutor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|numWriteChunkThreads
argument_list|,
name|numWriteChunkThreads
argument_list|,
literal|100
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|ArrayBlockingQueue
argument_list|<>
argument_list|(
literal|1024
argument_list|)
argument_list|,
operator|new
name|ThreadPoolExecutor
operator|.
name|CallerRunsPolicy
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numContainerOpExecutors
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|replicationLevel
operator|=
name|conf
operator|.
name|getEnum
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_DEFAULT
argument_list|)
expr_stmt|;
name|stateMachine
operator|=
operator|new
name|ContainerStateMachine
argument_list|(
name|dispatcher
argument_list|,
name|chunkExecutor
argument_list|,
name|this
argument_list|,
name|numContainerOpExecutors
argument_list|)
expr_stmt|;
name|this
operator|.
name|server
operator|=
name|RaftServer
operator|.
name|newBuilder
argument_list|()
operator|.
name|setServerId
argument_list|(
name|RatisHelper
operator|.
name|toRaftPeerId
argument_list|(
name|dd
argument_list|)
argument_list|)
operator|.
name|setProperties
argument_list|(
name|serverProperties
argument_list|)
operator|.
name|setStateMachine
argument_list|(
name|stateMachine
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|newRaftProperties (Configuration conf)
specifier|private
name|RaftProperties
name|newRaftProperties
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|RaftProperties
name|properties
init|=
operator|new
name|RaftProperties
argument_list|()
decl_stmt|;
comment|// Set rpc type
specifier|final
name|String
name|rpcType
init|=
name|conf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|RpcType
name|rpc
init|=
name|SupportedRpcType
operator|.
name|valueOfIgnoreCase
argument_list|(
name|rpcType
argument_list|)
decl_stmt|;
name|RaftConfigKeys
operator|.
name|Rpc
operator|.
name|setType
argument_list|(
name|properties
argument_list|,
name|rpc
argument_list|)
expr_stmt|;
comment|// set raft segment size
specifier|final
name|int
name|raftSegmentSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_SEGMENT_SIZE_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_SEGMENT_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|RaftServerConfigKeys
operator|.
name|Log
operator|.
name|setSegmentSizeMax
argument_list|(
name|properties
argument_list|,
name|SizeInBytes
operator|.
name|valueOf
argument_list|(
name|raftSegmentSize
argument_list|)
argument_list|)
expr_stmt|;
comment|// set raft segment pre-allocated size
specifier|final
name|int
name|raftSegmentPreallocatedSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|RaftServerConfigKeys
operator|.
name|Log
operator|.
name|Appender
operator|.
name|setBufferCapacity
argument_list|(
name|properties
argument_list|,
name|SizeInBytes
operator|.
name|valueOf
argument_list|(
name|raftSegmentPreallocatedSize
argument_list|)
argument_list|)
expr_stmt|;
name|RaftServerConfigKeys
operator|.
name|Log
operator|.
name|setPreallocatedSize
argument_list|(
name|properties
argument_list|,
name|SizeInBytes
operator|.
name|valueOf
argument_list|(
name|raftSegmentPreallocatedSize
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set max write buffer size, which is the scm chunk size
specifier|final
name|int
name|maxChunkSize
init|=
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_CHUNK_MAX_SIZE
decl_stmt|;
name|RaftServerConfigKeys
operator|.
name|Log
operator|.
name|setWriteBufferSize
argument_list|(
name|properties
argument_list|,
name|SizeInBytes
operator|.
name|valueOf
argument_list|(
name|maxChunkSize
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set the client requestTimeout
name|TimeUnit
name|timeUnit
init|=
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
operator|.
name|getUnit
argument_list|()
decl_stmt|;
name|long
name|duration
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
operator|.
name|getDuration
argument_list|()
argument_list|,
name|timeUnit
argument_list|)
decl_stmt|;
specifier|final
name|TimeDuration
name|clientRequestTimeout
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
name|duration
argument_list|,
name|timeUnit
argument_list|)
decl_stmt|;
name|RaftClientConfigKeys
operator|.
name|Rpc
operator|.
name|setRequestTimeout
argument_list|(
name|properties
argument_list|,
name|clientRequestTimeout
argument_list|)
expr_stmt|;
comment|// Set the server Request timeout
name|timeUnit
operator|=
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_DEFAULT
operator|.
name|getUnit
argument_list|()
expr_stmt|;
name|duration
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_DEFAULT
operator|.
name|getDuration
argument_list|()
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
specifier|final
name|TimeDuration
name|serverRequestTimeout
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
name|duration
argument_list|,
name|timeUnit
argument_list|)
decl_stmt|;
name|RaftServerConfigKeys
operator|.
name|Rpc
operator|.
name|setRequestTimeout
argument_list|(
name|properties
argument_list|,
name|serverRequestTimeout
argument_list|)
expr_stmt|;
comment|// set timeout for a retry cache entry
name|timeUnit
operator|=
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_DEFAULT
operator|.
name|getUnit
argument_list|()
expr_stmt|;
name|duration
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_DEFAULT
operator|.
name|getDuration
argument_list|()
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
specifier|final
name|TimeDuration
name|retryCacheTimeout
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
name|duration
argument_list|,
name|timeUnit
argument_list|)
decl_stmt|;
name|RaftServerConfigKeys
operator|.
name|RetryCache
operator|.
name|setExpiryTime
argument_list|(
name|properties
argument_list|,
name|retryCacheTimeout
argument_list|)
expr_stmt|;
comment|// Set the ratis leader election timeout
name|TimeUnit
name|leaderElectionMinTimeoutUnit
init|=
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
operator|.
name|getUnit
argument_list|()
decl_stmt|;
name|duration
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
operator|.
name|getDuration
argument_list|()
argument_list|,
name|leaderElectionMinTimeoutUnit
argument_list|)
expr_stmt|;
specifier|final
name|TimeDuration
name|leaderElectionMinTimeout
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
name|duration
argument_list|,
name|leaderElectionMinTimeoutUnit
argument_list|)
decl_stmt|;
name|RaftServerConfigKeys
operator|.
name|Rpc
operator|.
name|setTimeoutMin
argument_list|(
name|properties
argument_list|,
name|leaderElectionMinTimeout
argument_list|)
expr_stmt|;
name|long
name|leaderElectionMaxTimeout
init|=
name|leaderElectionMinTimeout
operator|.
name|toLong
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|+
literal|200
decl_stmt|;
name|RaftServerConfigKeys
operator|.
name|Rpc
operator|.
name|setTimeoutMax
argument_list|(
name|properties
argument_list|,
name|TimeDuration
operator|.
name|valueOf
argument_list|(
name|leaderElectionMaxTimeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Enable batch append on raft server
name|RaftServerConfigKeys
operator|.
name|Log
operator|.
name|Appender
operator|.
name|setBatchEnabled
argument_list|(
name|properties
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Set the maximum cache segments
name|RaftServerConfigKeys
operator|.
name|Log
operator|.
name|setMaxCachedSegmentNum
argument_list|(
name|properties
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// set the node failure timeout
name|timeUnit
operator|=
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_FAILURE_DURATION_DEFAULT
operator|.
name|getUnit
argument_list|()
expr_stmt|;
name|duration
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_FAILURE_DURATION_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_FAILURE_DURATION_DEFAULT
operator|.
name|getDuration
argument_list|()
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
specifier|final
name|TimeDuration
name|nodeFailureTimeout
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
name|duration
argument_list|,
name|timeUnit
argument_list|)
decl_stmt|;
name|RaftServerConfigKeys
operator|.
name|setLeaderElectionTimeout
argument_list|(
name|properties
argument_list|,
name|nodeFailureTimeout
argument_list|)
expr_stmt|;
name|RaftServerConfigKeys
operator|.
name|Rpc
operator|.
name|setSlownessTimeout
argument_list|(
name|properties
argument_list|,
name|nodeFailureTimeout
argument_list|)
expr_stmt|;
name|nodeFailureTimeoutMs
operator|=
name|nodeFailureTimeout
operator|.
name|toLong
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// Set the ratis storage directory
name|String
name|storageDir
init|=
name|HddsServerUtil
operator|.
name|getOzoneDatanodeRatisDirectory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RaftServerConfigKeys
operator|.
name|setStorageDirs
argument_list|(
name|properties
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|File
argument_list|(
name|storageDir
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// For grpc set the maximum message size
name|GrpcConfigKeys
operator|.
name|setMessageSizeMax
argument_list|(
name|properties
argument_list|,
name|SizeInBytes
operator|.
name|valueOf
argument_list|(
name|maxChunkSize
operator|+
name|raftSegmentPreallocatedSize
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set the ratis port number
if|if
condition|(
name|rpc
operator|==
name|SupportedRpcType
operator|.
name|GRPC
condition|)
block|{
name|GrpcConfigKeys
operator|.
name|Server
operator|.
name|setPort
argument_list|(
name|properties
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rpc
operator|==
name|SupportedRpcType
operator|.
name|NETTY
condition|)
block|{
name|NettyConfigKeys
operator|.
name|Server
operator|.
name|setPort
argument_list|(
name|properties
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
DECL|method|newXceiverServerRatis ( DatanodeDetails datanodeDetails, Configuration ozoneConf, ContainerDispatcher dispatcher, StateContext context)
specifier|public
specifier|static
name|XceiverServerRatis
name|newXceiverServerRatis
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|Configuration
name|ozoneConf
parameter_list|,
name|ContainerDispatcher
name|dispatcher
parameter_list|,
name|StateContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|localPort
init|=
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_PORT
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_PORT_DEFAULT
argument_list|)
decl_stmt|;
comment|// Get an available port on current node and
comment|// use that as the container port
if|if
condition|(
name|ozoneConf
operator|.
name|getBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT_DEFAULT
argument_list|)
condition|)
block|{
try|try
init|(
name|ServerSocket
name|socket
init|=
operator|new
name|ServerSocket
argument_list|()
init|)
block|{
name|socket
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|socket
operator|.
name|bind
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|localPort
operator|=
name|socket
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found a free port for the server : {}"
argument_list|,
name|localPort
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
literal|"Unable find a random free port for the server, "
operator|+
literal|"fallback to use default port {}"
argument_list|,
name|localPort
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|datanodeDetails
operator|.
name|setPort
argument_list|(
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|RATIS
argument_list|,
name|localPort
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|XceiverServerRatis
argument_list|(
name|datanodeDetails
argument_list|,
name|localPort
argument_list|,
name|dispatcher
argument_list|,
name|ozoneConf
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting {} {} at port {}"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|server
operator|.
name|getId
argument_list|()
argument_list|,
name|getIPCPort
argument_list|()
argument_list|)
expr_stmt|;
name|chunkExecutor
operator|.
name|prestartAllCoreThreads
argument_list|()
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
try|try
block|{
name|chunkExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|stateMachine
operator|.
name|close
argument_list|()
expr_stmt|;
name|server
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getIPCPort ()
specifier|public
name|int
name|getIPCPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
comment|/**    * Returns the Replication type supported by this end-point.    *    * @return enum -- {Stand_Alone, Ratis, Chained}    */
annotation|@
name|Override
DECL|method|getServerType ()
specifier|public
name|HddsProtos
operator|.
name|ReplicationType
name|getServerType
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
annotation|@
name|VisibleForTesting
DECL|method|getServer ()
specifier|public
name|RaftServer
name|getServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
DECL|method|processReply (RaftClientReply reply)
specifier|private
name|void
name|processReply
parameter_list|(
name|RaftClientReply
name|reply
parameter_list|)
throws|throws
name|IOException
block|{
comment|// NotLeader exception is thrown only when the raft server to which the
comment|// request is submitted is not the leader. The request will be rejected
comment|// and will eventually be executed once the request comes via the leader
comment|// node.
name|NotLeaderException
name|notLeaderException
init|=
name|reply
operator|.
name|getNotLeaderException
argument_list|()
decl_stmt|;
if|if
condition|(
name|notLeaderException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|notLeaderException
throw|;
block|}
name|StateMachineException
name|stateMachineException
init|=
name|reply
operator|.
name|getStateMachineException
argument_list|()
decl_stmt|;
if|if
condition|(
name|stateMachineException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|stateMachineException
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|submitRequest (ContainerCommandRequestProto request, HddsProtos.PipelineID pipelineID)
specifier|public
name|void
name|submitRequest
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|HddsProtos
operator|.
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|RaftClientReply
name|reply
decl_stmt|;
name|RaftClientRequest
name|raftClientRequest
init|=
name|createRaftClientRequest
argument_list|(
name|request
argument_list|,
name|pipelineID
argument_list|,
name|RaftClientRequest
operator|.
name|writeRequestType
argument_list|(
name|replicationLevel
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|reply
operator|=
name|server
operator|.
name|submitClientRequestAsync
argument_list|(
name|raftClientRequest
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|processReply
argument_list|(
name|reply
argument_list|)
expr_stmt|;
block|}
DECL|method|createRaftClientRequest ( ContainerCommandRequestProto request, HddsProtos.PipelineID pipelineID, RaftClientRequest.Type type)
specifier|private
name|RaftClientRequest
name|createRaftClientRequest
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|HddsProtos
operator|.
name|PipelineID
name|pipelineID
parameter_list|,
name|RaftClientRequest
operator|.
name|Type
name|type
parameter_list|)
block|{
return|return
operator|new
name|RaftClientRequest
argument_list|(
name|clientId
argument_list|,
name|server
operator|.
name|getId
argument_list|()
argument_list|,
name|PipelineID
operator|.
name|getFromProtobuf
argument_list|(
name|pipelineID
argument_list|)
operator|.
name|getRaftGroupID
argument_list|()
argument_list|,
name|nextCallId
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Message
operator|.
name|valueOf
argument_list|(
name|request
operator|.
name|toByteString
argument_list|()
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
block|}
DECL|method|handlePipelineFailure (RaftGroupId groupId, RoleInfoProto roleInfoProto)
specifier|private
name|void
name|handlePipelineFailure
parameter_list|(
name|RaftGroupId
name|groupId
parameter_list|,
name|RoleInfoProto
name|roleInfoProto
parameter_list|)
block|{
name|String
name|msg
decl_stmt|;
name|UUID
name|datanode
init|=
name|RatisHelper
operator|.
name|toDatanodeId
argument_list|(
name|roleInfoProto
operator|.
name|getSelf
argument_list|()
argument_list|)
decl_stmt|;
name|RaftPeerId
name|id
init|=
name|RaftPeerId
operator|.
name|valueOf
argument_list|(
name|roleInfoProto
operator|.
name|getSelf
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|roleInfoProto
operator|.
name|getRole
argument_list|()
condition|)
block|{
case|case
name|CANDIDATE
case|:
name|msg
operator|=
name|datanode
operator|+
literal|" is in candidate state for "
operator|+
name|roleInfoProto
operator|.
name|getCandidateInfo
argument_list|()
operator|.
name|getLastLeaderElapsedTimeMs
argument_list|()
operator|+
literal|"ms"
expr_stmt|;
break|break;
case|case
name|LEADER
case|:
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|datanode
argument_list|)
operator|.
name|append
argument_list|(
literal|" has not seen follower/s"
argument_list|)
expr_stmt|;
for|for
control|(
name|RaftProtos
operator|.
name|ServerRpcProto
name|follower
range|:
name|roleInfoProto
operator|.
name|getLeaderInfo
argument_list|()
operator|.
name|getFollowerInfoList
argument_list|()
control|)
block|{
if|if
condition|(
name|follower
operator|.
name|getLastRpcElapsedTimeMs
argument_list|()
operator|>
name|nodeFailureTimeoutMs
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|RatisHelper
operator|.
name|toDatanodeId
argument_list|(
name|follower
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" for "
argument_list|)
operator|.
name|append
argument_list|(
name|follower
operator|.
name|getLastRpcElapsedTimeMs
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"ms"
argument_list|)
expr_stmt|;
block|}
block|}
name|msg
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"unknown state:"
operator|+
name|roleInfoProto
operator|.
name|getRole
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"node"
operator|+
name|id
operator|+
literal|" is in illegal role "
operator|+
name|roleInfoProto
operator|.
name|getRole
argument_list|()
argument_list|)
throw|;
block|}
name|PipelineID
name|pipelineID
init|=
name|PipelineID
operator|.
name|valueOf
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
name|ClosePipelineInfo
operator|.
name|Builder
name|closePipelineInfo
init|=
name|ClosePipelineInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPipelineID
argument_list|(
name|pipelineID
operator|.
name|getProtobuf
argument_list|()
argument_list|)
operator|.
name|setReason
argument_list|(
name|ClosePipelineInfo
operator|.
name|Reason
operator|.
name|PIPELINE_FAILED
argument_list|)
operator|.
name|setDetailedReason
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|PipelineAction
name|action
init|=
name|PipelineAction
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClosePipeline
argument_list|(
name|closePipelineInfo
argument_list|)
operator|.
name|setAction
argument_list|(
name|PipelineAction
operator|.
name|Action
operator|.
name|CLOSE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|context
operator|.
name|addPipelineActionIfAbsent
argument_list|(
name|action
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"pipeline Action "
operator|+
name|action
operator|.
name|getAction
argument_list|()
operator|+
literal|"  on pipeline "
operator|+
name|pipelineID
operator|+
literal|".Reason : "
operator|+
name|action
operator|.
name|getClosePipeline
argument_list|()
operator|.
name|getDetailedReason
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPipelineReport ()
specifier|public
name|List
argument_list|<
name|PipelineReport
argument_list|>
name|getPipelineReport
parameter_list|()
block|{
try|try
block|{
name|Iterable
argument_list|<
name|RaftGroupId
argument_list|>
name|gids
init|=
name|server
operator|.
name|getGroupIds
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PipelineReport
argument_list|>
name|reports
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RaftGroupId
name|groupId
range|:
name|gids
control|)
block|{
name|reports
operator|.
name|add
argument_list|(
name|PipelineReport
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPipelineID
argument_list|(
name|PipelineID
operator|.
name|valueOf
argument_list|(
name|groupId
argument_list|)
operator|.
name|getProtobuf
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|reports
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|handleNodeSlowness (RaftGroup group, RoleInfoProto roleInfoProto)
name|void
name|handleNodeSlowness
parameter_list|(
name|RaftGroup
name|group
parameter_list|,
name|RoleInfoProto
name|roleInfoProto
parameter_list|)
block|{
name|handlePipelineFailure
argument_list|(
name|group
operator|.
name|getGroupId
argument_list|()
argument_list|,
name|roleInfoProto
argument_list|)
expr_stmt|;
block|}
DECL|method|handleNoLeader (RaftGroup group, RoleInfoProto roleInfoProto)
name|void
name|handleNoLeader
parameter_list|(
name|RaftGroup
name|group
parameter_list|,
name|RoleInfoProto
name|roleInfoProto
parameter_list|)
block|{
name|handlePipelineFailure
argument_list|(
name|group
operator|.
name|getGroupId
argument_list|()
argument_list|,
name|roleInfoProto
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

