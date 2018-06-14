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
name|common
operator|.
name|base
operator|.
name|Strings
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
name|*
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
name|shaded
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
DECL|field|callIdCounter
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|callIdCounter
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
name|callIdCounter
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
DECL|field|writeChunkExecutor
specifier|private
name|ThreadPoolExecutor
name|writeChunkExecutor
decl_stmt|;
DECL|method|XceiverServerRatis (DatanodeDetails dd, int port, String storageDir, ContainerDispatcher dispatcher, Configuration conf)
specifier|private
name|XceiverServerRatis
parameter_list|(
name|DatanodeDetails
name|dd
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|storageDir
parameter_list|,
name|ContainerDispatcher
name|dispatcher
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
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
specifier|final
name|int
name|maxChunkSize
init|=
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_CHUNK_MAX_SIZE
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
name|rpc
argument_list|,
name|port
argument_list|,
name|storageDir
argument_list|,
name|maxChunkSize
argument_list|,
name|raftSegmentSize
argument_list|,
name|raftSegmentPreallocatedSize
argument_list|)
decl_stmt|;
name|setRequestTimeout
argument_list|(
name|serverProperties
argument_list|,
name|clientRequestTimeout
argument_list|,
name|serverRequestTimeout
argument_list|)
expr_stmt|;
name|writeChunkExecutor
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
name|ContainerStateMachine
name|stateMachine
init|=
operator|new
name|ContainerStateMachine
argument_list|(
name|dispatcher
argument_list|,
name|writeChunkExecutor
argument_list|)
decl_stmt|;
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
name|setGroup
argument_list|(
name|RatisHelper
operator|.
name|emptyRaftGroup
argument_list|()
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
DECL|method|setRequestTimeout (RaftProperties serverProperties, TimeDuration clientRequestTimeout, TimeDuration serverRequestTimeout)
specifier|private
specifier|static
name|void
name|setRequestTimeout
parameter_list|(
name|RaftProperties
name|serverProperties
parameter_list|,
name|TimeDuration
name|clientRequestTimeout
parameter_list|,
name|TimeDuration
name|serverRequestTimeout
parameter_list|)
block|{
name|RaftClientConfigKeys
operator|.
name|Rpc
operator|.
name|setRequestTimeout
argument_list|(
name|serverProperties
argument_list|,
name|clientRequestTimeout
argument_list|)
expr_stmt|;
name|RaftServerConfigKeys
operator|.
name|Rpc
operator|.
name|setRequestTimeout
argument_list|(
name|serverProperties
argument_list|,
name|serverRequestTimeout
argument_list|)
expr_stmt|;
block|}
DECL|method|newRaftProperties ( RpcType rpc, int port, String storageDir, int scmChunkSize, int raftSegmentSize, int raftSegmentPreallocatedSize)
specifier|private
specifier|static
name|RaftProperties
name|newRaftProperties
parameter_list|(
name|RpcType
name|rpc
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|storageDir
parameter_list|,
name|int
name|scmChunkSize
parameter_list|,
name|int
name|raftSegmentSize
parameter_list|,
name|int
name|raftSegmentPreallocatedSize
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
name|setWriteBufferSize
argument_list|(
name|properties
argument_list|,
name|SizeInBytes
operator|.
name|valueOf
argument_list|(
name|scmChunkSize
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
name|RaftServerConfigKeys
operator|.
name|setStorageDir
argument_list|(
name|properties
argument_list|,
operator|new
name|File
argument_list|(
name|storageDir
argument_list|)
argument_list|)
expr_stmt|;
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
name|scmChunkSize
operator|+
name|raftSegmentPreallocatedSize
argument_list|)
argument_list|)
expr_stmt|;
name|RaftServerConfigKeys
operator|.
name|Rpc
operator|.
name|setTimeoutMin
argument_list|(
name|properties
argument_list|,
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|800
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
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
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|newXceiverServerRatis ( DatanodeDetails datanodeDetails, Configuration ozoneConf, ContainerDispatcher dispatcher)
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
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|ratisDir
init|=
name|File
operator|.
name|separator
operator|+
literal|"ratis"
decl_stmt|;
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
name|String
name|storageDir
init|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_DATANODE_STORAGE_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|storageDir
argument_list|)
condition|)
block|{
name|storageDir
operator|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|storageDir
argument_list|,
literal|"ozone.metadata.dirs "
operator|+
literal|"cannot be null, Please check your configs."
argument_list|)
expr_stmt|;
name|storageDir
operator|=
name|storageDir
operator|.
name|concat
argument_list|(
name|ratisDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Storage directory for Ratis is not configured. Mapping Ratis "
operator|+
literal|"storage under {}. It is a good idea to map this to an SSD disk."
argument_list|,
name|storageDir
argument_list|)
expr_stmt|;
block|}
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
comment|// If we have random local ports configured this means that it
comment|// probably running under MiniOzoneCluster. Ratis locks the storage
comment|// directories, so we need to pass different local directory for each
comment|// local instance. So we map ratis directories under datanode ID.
name|storageDir
operator|=
name|storageDir
operator|.
name|concat
argument_list|(
name|File
operator|.
name|separator
operator|+
name|datanodeDetails
operator|.
name|getUuidString
argument_list|()
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
name|storageDir
argument_list|,
name|dispatcher
argument_list|,
name|ozoneConf
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
name|writeChunkExecutor
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
name|writeChunkExecutor
operator|.
name|shutdown
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
block|{
comment|// NotLeader exception is thrown only when the raft server to which the
comment|// request is submitted is not the leader. The request will be rejected
comment|// and will eventually be executed once the request comnes via the leader
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
name|LOG
operator|.
name|info
argument_list|(
name|reply
operator|.
name|getNotLeaderException
argument_list|()
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
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
comment|// In case the request could not be completed, StateMachine Exception
comment|// will be thrown. For now, Just log the message.
comment|// If the container could not be closed, SCM will come to know
comment|// via containerReports. CloseContainer should be re tried via SCM.
name|LOG
operator|.
name|error
argument_list|(
name|stateMachineException
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|submitRequest ( ContainerProtos.ContainerCommandRequestProto request)
specifier|public
name|void
name|submitRequest
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|ClientId
name|clientId
init|=
name|ClientId
operator|.
name|randomId
argument_list|()
decl_stmt|;
name|RaftClientRequest
name|raftClientRequest
init|=
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
name|RatisHelper
operator|.
name|emptyRaftGroup
argument_list|()
operator|.
name|getGroupId
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
name|RaftClientRequest
comment|// ReplicationLevel.ALL ensures the transactions corresponding to
comment|// the request here are applied on all the raft servers.
operator|.
name|writeRequestType
argument_list|(
name|RaftProtos
operator|.
name|ReplicationLevel
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|CompletableFuture
argument_list|<
name|RaftClientReply
argument_list|>
name|reply
init|=
name|server
operator|.
name|submitClientRequestAsync
argument_list|(
name|raftClientRequest
argument_list|)
decl_stmt|;
name|reply
operator|.
name|thenAccept
argument_list|(
name|this
operator|::
name|processReply
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

