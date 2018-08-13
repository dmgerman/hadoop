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
name|shaded
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
name|shaded
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
name|shaded
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
DECL|field|asyncStub
specifier|private
name|XceiverClientProtocolServiceStub
name|asyncStub
decl_stmt|;
DECL|field|metrics
specifier|private
name|XceiverClientMetrics
name|metrics
decl_stmt|;
DECL|field|channel
specifier|private
name|ManagedChannel
name|channel
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
comment|/**    * Constructs a client that can communicate with the Container framework on    * data nodes.    *    * @param pipeline - Pipeline that defines the machines.    * @param config -- Ozone Config    */
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
name|DatanodeDetails
name|leader
init|=
name|this
operator|.
name|pipeline
operator|.
name|getLeader
argument_list|()
decl_stmt|;
comment|// read port from the data node, on failure use default configured
comment|// port.
name|int
name|port
init|=
name|leader
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to server Port : "
operator|+
name|leader
operator|.
name|getIpAddress
argument_list|()
argument_list|)
expr_stmt|;
name|channel
operator|=
name|NettyChannelBuilder
operator|.
name|forAddress
argument_list|(
name|leader
operator|.
name|getIpAddress
argument_list|()
argument_list|,
name|port
argument_list|)
operator|.
name|usePlaintext
argument_list|(
literal|true
argument_list|)
operator|.
name|maxInboundMessageSize
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_CHUNK_MAX_SIZE
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|asyncStub
operator|=
name|XceiverClientProtocolServiceGrpc
operator|.
name|newStub
argument_list|(
name|channel
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns if the xceiver client connects to a server.    *    * @return True if the connection is alive, false otherwise.    */
annotation|@
name|VisibleForTesting
DECL|method|isConnected ()
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
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
return|return
name|sendCommandAsync
argument_list|(
name|request
argument_list|)
operator|.
name|get
argument_list|()
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
comment|/**        * In case the grpc channel handler throws an exception,        * the exception thrown will be wrapped within {@link ExecutionException}.        * Unwarpping here so that original exception gets passed        * to to the client.        */
if|if
condition|(
name|e
operator|instanceof
name|ExecutionException
condition|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|cause
throw|;
block|}
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected exception during execution:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Sends a given command to server gets a waitable future back.    *    * @param request Request    * @return Response to the command    * @throws IOException    */
annotation|@
name|Override
specifier|public
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
DECL|method|sendCommandAsync (ContainerCommandRequestProto request)
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
if|if
condition|(
name|channel
operator|==
literal|null
operator|||
operator|!
name|isConnected
argument_list|()
condition|)
block|{
name|reconnect
argument_list|()
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
specifier|final
name|StreamObserver
argument_list|<
name|ContainerCommandRequestProto
argument_list|>
name|requestObserver
init|=
name|asyncStub
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
name|replyFuture
return|;
block|}
DECL|method|reconnect ()
specifier|private
name|void
name|reconnect
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|connect
argument_list|()
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
argument_list|()
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
comment|/**    * Create a pipeline.    *    * @param pipeline -  pipeline to be created.    */
annotation|@
name|Override
DECL|method|createPipeline (Pipeline pipeline)
specifier|public
name|void
name|createPipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
comment|// For stand alone pipeline, there is no notion called setup pipeline.
return|return;
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

