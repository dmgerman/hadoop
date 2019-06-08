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
name|XceiverClientProtocolServiceGrpc
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
name|XceiverClientProtocolServiceGrpc
operator|.
name|XceiverClientProtocolServiceStub
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
name|scm
operator|.
name|storage
operator|.
name|CheckedBiFunction
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
name|exception
operator|.
name|SCMSecurityException
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
name|GrpcClientInterceptor
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
name|security
operator|.
name|UserGroupInformation
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
name|io
operator|.
name|grpc
operator|.
name|ManagedChannel
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
name|io
operator|.
name|grpc
operator|.
name|Status
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
name|io
operator|.
name|grpc
operator|.
name|netty
operator|.
name|GrpcSslContexts
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
name|io
operator|.
name|grpc
operator|.
name|netty
operator|.
name|NettyChannelBuilder
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
name|io
operator|.
name|grpc
operator|.
name|stub
operator|.
name|StreamObserver
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
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|ssl
operator|.
name|SslContextBuilder
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
name|util
operator|.
name|HashMap
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
name|Semaphore
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

begin_comment
comment|/**  * A Client for the storageContainer protocol.  */
end_comment

begin_class
DECL|class|XceiverClientGrpc
specifier|public
class|class
name|XceiverClientGrpc
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
name|XceiverClientGrpc
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pipeline
specifier|private
specifier|final
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Configuration
name|config
decl_stmt|;
DECL|field|asyncStubs
specifier|private
name|Map
argument_list|<
name|UUID
argument_list|,
name|XceiverClientProtocolServiceStub
argument_list|>
name|asyncStubs
decl_stmt|;
DECL|field|metrics
specifier|private
name|XceiverClientMetrics
name|metrics
decl_stmt|;
DECL|field|channels
specifier|private
name|Map
argument_list|<
name|UUID
argument_list|,
name|ManagedChannel
argument_list|>
name|channels
decl_stmt|;
DECL|field|semaphore
specifier|private
specifier|final
name|Semaphore
name|semaphore
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|secConfig
specifier|private
name|SecurityConfig
name|secConfig
decl_stmt|;
comment|/**    * Constructs a client that can communicate with the Container framework on    * data nodes.    *    * @param pipeline - Pipeline that defines the machines.    * @param config   -- Ozone Config    */
DECL|method|XceiverClientGrpc (Pipeline pipeline, Configuration config)
specifier|public
name|XceiverClientGrpc
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|pipeline
operator|=
name|pipeline
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|secConfig
operator|=
operator|new
name|SecurityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|semaphore
operator|=
operator|new
name|Semaphore
argument_list|(
name|HddsClientUtils
operator|.
name|getMaxOutstandingRequests
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|XceiverClientManager
operator|.
name|getXceiverClientMetrics
argument_list|()
expr_stmt|;
name|this
operator|.
name|channels
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|asyncStubs
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * To be used when grpc token is not enabled.    */
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
comment|// leader by default is the 1st datanode in the datanode list of pipleline
name|DatanodeDetails
name|dn
init|=
name|this
operator|.
name|pipeline
operator|.
name|getFirstNode
argument_list|()
decl_stmt|;
comment|// just make a connection to the 1st datanode at the beginning
name|connectToDatanode
argument_list|(
name|dn
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Passed encoded token to GRPC header when security is enabled.    */
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
comment|// leader by default is the 1st datanode in the datanode list of pipleline
name|DatanodeDetails
name|dn
init|=
name|this
operator|.
name|pipeline
operator|.
name|getFirstNode
argument_list|()
decl_stmt|;
comment|// just make a connection to the 1st datanode at the beginning
name|connectToDatanode
argument_list|(
name|dn
argument_list|,
name|encodedToken
argument_list|)
expr_stmt|;
block|}
DECL|method|connectToDatanode (DatanodeDetails dn, String encodedToken)
specifier|private
name|void
name|connectToDatanode
parameter_list|(
name|DatanodeDetails
name|dn
parameter_list|,
name|String
name|encodedToken
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read port from the data node, on failure use default configured
comment|// port.
name|int
name|port
init|=
name|dn
operator|.
name|getPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|==
literal|0
condition|)
block|{
name|port
operator|=
name|config
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
block|}
comment|// Add credential context to the client call
name|String
name|userName
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to server Port : "
operator|+
name|dn
operator|.
name|getIpAddress
argument_list|()
argument_list|)
expr_stmt|;
name|NettyChannelBuilder
name|channelBuilder
init|=
name|NettyChannelBuilder
operator|.
name|forAddress
argument_list|(
name|dn
operator|.
name|getIpAddress
argument_list|()
argument_list|,
name|port
argument_list|)
operator|.
name|usePlaintext
argument_list|()
operator|.
name|maxInboundMessageSize
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_SCM_CHUNK_MAX_SIZE
argument_list|)
operator|.
name|intercept
argument_list|(
operator|new
name|ClientCredentialInterceptor
argument_list|(
name|userName
argument_list|,
name|encodedToken
argument_list|)
argument_list|,
operator|new
name|GrpcClientInterceptor
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|secConfig
operator|.
name|isGrpcTlsEnabled
argument_list|()
condition|)
block|{
name|File
name|trustCertCollectionFile
init|=
name|secConfig
operator|.
name|getTrustStoreFile
argument_list|()
decl_stmt|;
name|File
name|privateKeyFile
init|=
name|secConfig
operator|.
name|getClientPrivateKeyFile
argument_list|()
decl_stmt|;
name|File
name|clientCertChainFile
init|=
name|secConfig
operator|.
name|getClientCertChainFile
argument_list|()
decl_stmt|;
name|SslContextBuilder
name|sslContextBuilder
init|=
name|GrpcSslContexts
operator|.
name|forClient
argument_list|()
decl_stmt|;
if|if
condition|(
name|trustCertCollectionFile
operator|!=
literal|null
condition|)
block|{
name|sslContextBuilder
operator|.
name|trustManager
argument_list|(
name|trustCertCollectionFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|secConfig
operator|.
name|isGrpcMutualTlsRequired
argument_list|()
operator|&&
name|clientCertChainFile
operator|!=
literal|null
operator|&&
name|privateKeyFile
operator|!=
literal|null
condition|)
block|{
name|sslContextBuilder
operator|.
name|keyManager
argument_list|(
name|clientCertChainFile
argument_list|,
name|privateKeyFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|secConfig
operator|.
name|useTestCert
argument_list|()
condition|)
block|{
name|channelBuilder
operator|.
name|overrideAuthority
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
block|}
name|channelBuilder
operator|.
name|useTransportSecurity
argument_list|()
operator|.
name|sslContext
argument_list|(
name|sslContextBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|channelBuilder
operator|.
name|usePlaintext
argument_list|()
expr_stmt|;
block|}
name|ManagedChannel
name|channel
init|=
name|channelBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|XceiverClientProtocolServiceStub
name|asyncStub
init|=
name|XceiverClientProtocolServiceGrpc
operator|.
name|newStub
argument_list|(
name|channel
argument_list|)
decl_stmt|;
name|asyncStubs
operator|.
name|put
argument_list|(
name|dn
operator|.
name|getUuid
argument_list|()
argument_list|,
name|asyncStub
argument_list|)
expr_stmt|;
name|channels
operator|.
name|put
argument_list|(
name|dn
operator|.
name|getUuid
argument_list|()
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns if the xceiver client connects to all servers in the pipeline.    *    * @return True if the connection is alive, false otherwise.    */
annotation|@
name|VisibleForTesting
DECL|method|isConnected (DatanodeDetails details)
specifier|public
name|boolean
name|isConnected
parameter_list|(
name|DatanodeDetails
name|details
parameter_list|)
block|{
return|return
name|isConnected
argument_list|(
name|channels
operator|.
name|get
argument_list|(
name|details
operator|.
name|getUuid
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|isConnected (ManagedChannel channel)
specifier|private
name|boolean
name|isConnected
parameter_list|(
name|ManagedChannel
name|channel
parameter_list|)
block|{
return|return
name|channel
operator|!=
literal|null
operator|&&
operator|!
name|channel
operator|.
name|isTerminated
argument_list|()
operator|&&
operator|!
name|channel
operator|.
name|isShutdown
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closed
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|ManagedChannel
name|channel
range|:
name|channels
operator|.
name|values
argument_list|()
control|)
block|{
name|channel
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
try|try
block|{
name|channel
operator|.
name|awaitTermination
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception while waiting for channel termination"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
try|try
block|{
name|XceiverClientReply
name|reply
decl_stmt|;
name|reply
operator|=
name|sendCommandWithTraceIDAndRetry
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|responseProto
init|=
name|reply
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|responseProto
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to execute command "
operator|+
name|request
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|sendCommand ( ContainerCommandRequestProto request, List<CheckedBiFunction> validators)
specifier|public
name|ContainerCommandResponseProto
name|sendCommand
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|List
argument_list|<
name|CheckedBiFunction
argument_list|>
name|validators
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|XceiverClientReply
name|reply
decl_stmt|;
name|reply
operator|=
name|sendCommandWithTraceIDAndRetry
argument_list|(
name|request
argument_list|,
name|validators
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|responseProto
init|=
name|reply
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|responseProto
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to execute command "
operator|+
name|request
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|sendCommandWithTraceIDAndRetry ( ContainerCommandRequestProto request, List<CheckedBiFunction> validators)
specifier|private
name|XceiverClientReply
name|sendCommandWithTraceIDAndRetry
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|List
argument_list|<
name|CheckedBiFunction
argument_list|>
name|validators
parameter_list|)
throws|throws
name|IOException
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
literal|"XceiverClientGrpc."
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
return|return
name|sendCommandWithRetry
argument_list|(
name|finalPayload
argument_list|,
name|validators
argument_list|)
return|;
block|}
block|}
DECL|method|sendCommandWithRetry ( ContainerCommandRequestProto request, List<CheckedBiFunction> validators)
specifier|private
name|XceiverClientReply
name|sendCommandWithRetry
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|List
argument_list|<
name|CheckedBiFunction
argument_list|>
name|validators
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerCommandResponseProto
name|responseProto
init|=
literal|null
decl_stmt|;
name|IOException
name|ioException
init|=
literal|null
decl_stmt|;
comment|// In case of an exception or an error, we will try to read from the
comment|// datanodes in the pipeline in a round robin fashion.
comment|// TODO: cache the correct leader info in here, so that any subsequent calls
comment|// should first go to leader
name|XceiverClientReply
name|reply
init|=
operator|new
name|XceiverClientReply
argument_list|(
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeDetails
name|dn
range|:
name|pipeline
operator|.
name|getNodes
argument_list|()
control|)
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Executing command "
operator|+
name|request
operator|+
literal|" on datanode "
operator|+
name|dn
argument_list|)
expr_stmt|;
comment|// In case the command gets retried on a 2nd datanode,
comment|// sendCommandAsyncCall will create a new channel and async stub
comment|// in case these don't exist for the specific datanode.
name|reply
operator|.
name|addDatanode
argument_list|(
name|dn
argument_list|)
expr_stmt|;
name|responseProto
operator|=
name|sendCommandAsync
argument_list|(
name|request
argument_list|,
name|dn
argument_list|)
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|validators
operator|!=
literal|null
operator|&&
operator|!
name|validators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|CheckedBiFunction
name|validator
range|:
name|validators
control|)
block|{
name|validator
operator|.
name|apply
argument_list|(
name|request
argument_list|,
name|responseProto
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|InterruptedException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to execute command "
operator|+
name|request
operator|+
literal|" on datanode "
operator|+
name|dn
operator|.
name|getUuidString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|e
operator|instanceof
name|IOException
operator|)
condition|)
block|{
if|if
condition|(
name|Status
operator|.
name|fromThrowable
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
operator|.
name|getCode
argument_list|()
operator|==
name|Status
operator|.
name|UNAUTHENTICATED
operator|.
name|getCode
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SCMSecurityException
argument_list|(
literal|"Failed to authenticate with "
operator|+
literal|"GRPC XceiverServer with Ozone block token."
argument_list|)
throw|;
block|}
name|ioException
operator|=
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ioException
operator|=
operator|(
name|IOException
operator|)
name|e
expr_stmt|;
block|}
name|responseProto
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|responseProto
operator|!=
literal|null
condition|)
block|{
name|reply
operator|.
name|setResponse
argument_list|(
name|CompletableFuture
operator|.
name|completedFuture
argument_list|(
name|responseProto
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|reply
return|;
block|}
else|else
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ioException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to execute command "
operator|+
name|request
operator|+
literal|" on the pipeline "
operator|+
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|ioException
throw|;
block|}
block|}
comment|// TODO: for a true async API, once the waitable future while executing
comment|// the command on one channel fails, it should be retried asynchronously
comment|// on the future Task for all the remaining datanodes.
comment|// Note: this Async api is not used currently used in any active I/O path.
comment|// In case it gets used, the asynchronous retry logic needs to be plugged
comment|// in here.
comment|/**    * Sends a given command to server gets a waitable future back.    *    * @param request Request    * @return Response to the command    * @throws IOException    */
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
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
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
literal|"XceiverClientGrpc."
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
name|XceiverClientReply
name|asyncReply
init|=
name|sendCommandAsync
argument_list|(
name|finalPayload
argument_list|,
name|pipeline
operator|.
name|getFirstNode
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO : for now make this API sync in nature as async requests are
comment|// served out of order over XceiverClientGrpc. This needs to be fixed
comment|// if this API is to be used for I/O path. Currently, this is not
comment|// used for Read/Write Operation but for tests.
if|if
condition|(
operator|!
name|HddsUtils
operator|.
name|isReadOnly
argument_list|(
name|request
argument_list|)
condition|)
block|{
name|asyncReply
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
return|return
name|asyncReply
return|;
block|}
block|}
DECL|method|sendCommandAsync ( ContainerCommandRequestProto request, DatanodeDetails dn)
specifier|private
name|XceiverClientReply
name|sendCommandAsync
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|DatanodeDetails
name|dn
parameter_list|)
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This channel is not connected."
argument_list|)
throw|;
block|}
name|UUID
name|dnId
init|=
name|dn
operator|.
name|getUuid
argument_list|()
decl_stmt|;
name|ManagedChannel
name|channel
init|=
name|channels
operator|.
name|get
argument_list|(
name|dnId
argument_list|)
decl_stmt|;
comment|// If the channel doesn't exist for this specific datanode or the channel
comment|// is closed, just reconnect
name|String
name|token
init|=
name|request
operator|.
name|getEncodedToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isConnected
argument_list|(
name|channel
argument_list|)
condition|)
block|{
name|reconnect
argument_list|(
name|dn
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
name|replyFuture
init|=
operator|new
name|CompletableFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|semaphore
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|long
name|requestTime
init|=
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
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
comment|// create a new grpc stream for each non-async call.
comment|// TODO: for async calls, we should reuse StreamObserver resources.
specifier|final
name|StreamObserver
argument_list|<
name|ContainerCommandRequestProto
argument_list|>
name|requestObserver
init|=
name|asyncStubs
operator|.
name|get
argument_list|(
name|dnId
argument_list|)
operator|.
name|send
argument_list|(
operator|new
name|StreamObserver
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onNext
parameter_list|(
name|ContainerCommandResponseProto
name|value
parameter_list|)
block|{
name|replyFuture
operator|.
name|complete
argument_list|(
name|value
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
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onError
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|replyFuture
operator|.
name|completeExceptionally
argument_list|(
name|t
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
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onCompleted
parameter_list|()
block|{
if|if
condition|(
operator|!
name|replyFuture
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|replyFuture
operator|.
name|completeExceptionally
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Stream completed but no reply for request "
operator|+
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|requestObserver
operator|.
name|onNext
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|requestObserver
operator|.
name|onCompleted
argument_list|()
expr_stmt|;
return|return
operator|new
name|XceiverClientReply
argument_list|(
name|replyFuture
argument_list|)
return|;
block|}
DECL|method|reconnect (DatanodeDetails dn, String encodedToken)
specifier|private
name|void
name|reconnect
parameter_list|(
name|DatanodeDetails
name|dn
parameter_list|,
name|String
name|encodedToken
parameter_list|)
throws|throws
name|IOException
block|{
name|ManagedChannel
name|channel
decl_stmt|;
try|try
block|{
name|connectToDatanode
argument_list|(
name|dn
argument_list|,
name|encodedToken
argument_list|)
expr_stmt|;
name|channel
operator|=
name|channels
operator|.
name|get
argument_list|(
name|dn
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while connecting: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|channel
operator|==
literal|null
operator|||
operator|!
name|isConnected
argument_list|(
name|channel
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This channel is not connected."
argument_list|)
throw|;
block|}
block|}
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
comment|// there is no notion of watch for commit index in standalone pipeline
return|return
literal|null
return|;
block|}
empty_stmt|;
DECL|method|getReplicatedMinCommitIndex ()
specifier|public
name|long
name|getReplicatedMinCommitIndex
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Returns pipeline Type.    *    * @return - Stand Alone as the type.    */
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
name|STAND_ALONE
return|;
block|}
block|}
end_class

end_unit

