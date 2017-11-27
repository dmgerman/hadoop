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
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|scm
operator|.
name|ScmConfigKeys
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
name|util
operator|.
name|SizeInBytes
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
DECL|method|XceiverServerRatis (DatanodeID id, int port, String storageDir, ContainerDispatcher dispatcher, RpcType rpcType, int maxChunkSize, int raftSegmentSize)
specifier|private
name|XceiverServerRatis
parameter_list|(
name|DatanodeID
name|id
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
name|RpcType
name|rpcType
parameter_list|,
name|int
name|maxChunkSize
parameter_list|,
name|int
name|raftSegmentSize
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|id
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
name|rpcType
argument_list|,
name|port
argument_list|,
name|storageDir
argument_list|,
name|maxChunkSize
argument_list|,
name|raftSegmentSize
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
name|id
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
operator|new
name|ContainerStateMachine
argument_list|(
name|dispatcher
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|newRaftProperties ( RpcType rpc, int port, String storageDir, int scmChunkSize, int raftSegmentSize)
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
name|raftSegmentSize
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
name|raftSegmentSize
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
comment|//TODO: change these configs to setter after RATIS-154
name|properties
operator|.
name|setInt
argument_list|(
literal|"raft.server.log.segment.cache.num.max"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setInt
argument_list|(
literal|"raft.grpc.message.size.max"
argument_list|,
name|scmChunkSize
operator|+
name|raftSegmentSize
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setInt
argument_list|(
literal|"raft.server.rpc.timeout.min"
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setInt
argument_list|(
literal|"raft.server.rpc.timeout.max"
argument_list|,
literal|600
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
else|else
block|{
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
block|}
return|return
name|properties
return|;
block|}
DECL|method|newXceiverServerRatis (DatanodeID datanodeID, Configuration ozoneConf, ContainerDispatcher dispatcher)
specifier|public
specifier|static
name|XceiverServerRatis
name|newXceiverServerRatis
parameter_list|(
name|DatanodeID
name|datanodeID
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
specifier|final
name|String
name|rpcType
init|=
name|ozoneConf
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
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_RATIS_SEGMENT_SIZE_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_RATIS_SEGMENT_SIZE_DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxChunkSize
init|=
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CHUNK_MAX_SIZE
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
name|datanodeID
operator|.
name|getDatanodeUuid
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
name|datanodeID
operator|.
name|setRatisPort
argument_list|(
name|localPort
argument_list|)
expr_stmt|;
return|return
operator|new
name|XceiverServerRatis
argument_list|(
name|datanodeID
argument_list|,
name|localPort
argument_list|,
name|storageDir
argument_list|,
name|dispatcher
argument_list|,
name|rpc
argument_list|,
name|maxChunkSize
argument_list|,
name|raftSegmentSize
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
name|OzoneProtos
operator|.
name|ReplicationType
name|getServerType
parameter_list|()
block|{
return|return
name|OzoneProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
return|;
block|}
block|}
end_class

end_unit

