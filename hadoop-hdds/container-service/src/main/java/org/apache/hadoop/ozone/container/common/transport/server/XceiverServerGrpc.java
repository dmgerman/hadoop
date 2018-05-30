begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.transport.server
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
name|ratis
operator|.
name|shaded
operator|.
name|io
operator|.
name|grpc
operator|.
name|Server
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
name|io
operator|.
name|grpc
operator|.
name|ServerBuilder
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
name|io
operator|.
name|grpc
operator|.
name|netty
operator|.
name|NettyServerBuilder
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

begin_comment
comment|/**  * Creates a Grpc server endpoint that acts as the communication layer for  * Ozone containers.  */
end_comment

begin_class
DECL|class|XceiverServerGrpc
specifier|public
specifier|final
class|class
name|XceiverServerGrpc
implements|implements
name|XceiverServerSpi
block|{
specifier|private
specifier|static
specifier|final
name|Logger
DECL|field|LOG
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XceiverServerGrpc
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
comment|/**    * Constructs a Grpc server class.    *    * @param conf - Configuration    */
DECL|method|XceiverServerGrpc (DatanodeDetails datanodeDetails, Configuration conf, ContainerDispatcher dispatcher)
specifier|public
name|XceiverServerGrpc
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|ContainerDispatcher
name|dispatcher
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT_DEFAULT
argument_list|)
expr_stmt|;
comment|// Get an available port on current node and
comment|// use that as the container port
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_RANDOM_PORT
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_RANDOM_PORT_DEFAULT
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
name|this
operator|.
name|port
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
name|this
operator|.
name|port
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
name|this
operator|.
name|port
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
name|STANDALONE
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|=
operator|(
operator|(
name|NettyServerBuilder
operator|)
name|ServerBuilder
operator|.
name|forPort
argument_list|(
name|port
argument_list|)
operator|)
operator|.
name|maxMessageSize
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_CHUNK_MAX_SIZE
argument_list|)
operator|.
name|addService
argument_list|(
operator|new
name|GrpcXceiverService
argument_list|(
name|dispatcher
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
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
name|this
operator|.
name|port
return|;
block|}
comment|/**    * Returns the Replication type supported by this end-point.    *    * @return enum -- {Stand_Alone, Ratis, Grpc, Chained}    */
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
name|STAND_ALONE
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
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

