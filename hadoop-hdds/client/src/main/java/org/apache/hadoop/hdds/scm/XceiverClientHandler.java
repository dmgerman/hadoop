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
name|ratis
operator|.
name|shaded
operator|.
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|Channel
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
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
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
name|netty
operator|.
name|channel
operator|.
name|SimpleChannelInboundHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|util
operator|.
name|Time
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
name|util
operator|.
name|Iterator
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
name|ConcurrentMap
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
name|Future
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

begin_comment
comment|/**  * Netty client handler.  */
end_comment

begin_class
DECL|class|XceiverClientHandler
specifier|public
class|class
name|XceiverClientHandler
extends|extends
name|SimpleChannelInboundHandler
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
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
name|XceiverClientHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|responses
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|ResponseFuture
argument_list|>
name|responses
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|pipeline
specifier|private
specifier|final
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|channel
specifier|private
specifier|volatile
name|Channel
name|channel
decl_stmt|;
DECL|field|metrics
specifier|private
name|XceiverClientMetrics
name|metrics
decl_stmt|;
DECL|field|semaphore
specifier|private
specifier|final
name|Semaphore
name|semaphore
decl_stmt|;
comment|/**    * Constructs a client that can communicate to a container server.    */
DECL|method|XceiverClientHandler (Pipeline pipeline, Semaphore semaphore)
specifier|public
name|XceiverClientHandler
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|Semaphore
name|semaphore
parameter_list|)
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
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
name|metrics
operator|=
name|XceiverClientManager
operator|.
name|getXceiverClientMetrics
argument_list|()
expr_stmt|;
name|this
operator|.
name|semaphore
operator|=
name|semaphore
expr_stmt|;
block|}
comment|/**    *<strong>Please keep in mind that this method will be renamed to {@code    * messageReceived(ChannelHandlerContext, I)} in 5.0.</strong>    *<p>    * Is called for each message of type {@link ContainerProtos    * .ContainerCommandResponseProto}.    *    * @param ctx the {@link ChannelHandlerContext} which this {@link    * SimpleChannelInboundHandler} belongs to    * @param msg the message to handle    * @throws Exception is thrown if an error occurred    */
annotation|@
name|Override
DECL|method|channelRead0 (ChannelHandlerContext ctx, ContainerProtos.ContainerCommandResponseProto msg)
specifier|public
name|void
name|channelRead0
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|decrPendingContainerOpsMetrics
argument_list|(
name|msg
operator|.
name|getCmdType
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|msg
operator|.
name|getTraceID
argument_list|()
decl_stmt|;
name|ResponseFuture
name|response
init|=
name|responses
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|getFuture
argument_list|()
operator|.
name|complete
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|long
name|requestTime
init|=
name|response
operator|.
name|getRequestTime
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|addContainerOpsLatency
argument_list|(
name|msg
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
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"A reply received for message that was not queued. trace "
operator|+
literal|"ID: {}"
argument_list|,
name|msg
operator|.
name|getTraceID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|channelRegistered (ChannelHandlerContext ctx)
specifier|public
name|void
name|channelRegistered
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"channelRegistered: Connected to ctx"
argument_list|)
expr_stmt|;
name|channel
operator|=
name|ctx
operator|.
name|channel
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|exceptionCaught (ChannelHandlerContext ctx, Throwable cause)
specifier|public
name|void
name|exceptionCaught
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception in client "
operator|+
name|cause
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|keyIterator
init|=
name|responses
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|keyIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ResponseFuture
name|response
init|=
name|responses
operator|.
name|remove
argument_list|(
name|keyIterator
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|.
name|getFuture
argument_list|()
operator|.
name|completeExceptionally
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Since netty is async, we send a work request and then wait until a response    * appears in the reply queue. This is simple sync interface for clients. we    * should consider building async interfaces for client if this turns out to    * be a performance bottleneck.    *    * @param request - request.    * @return -- response    */
DECL|method|sendCommand ( ContainerProtos.ContainerCommandRequestProto request)
specifier|public
name|ContainerCommandResponseProto
name|sendCommand
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|Future
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
name|future
init|=
name|sendCommandAsync
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|future
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * SendCommandAsyc queues a command to the Netty Subsystem and returns a    * CompletableFuture. This Future is marked compeleted in the channelRead0    * when the call comes back.    * @param request - Request to execute    * @return CompletableFuture    */
DECL|method|sendCommandAsync ( ContainerProtos.ContainerCommandRequestProto request)
specifier|public
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
name|sendCommandAsync
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|InterruptedException
block|{
comment|// Throw an exception of request doesn't have traceId
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid trace ID"
argument_list|)
throw|;
block|}
comment|// Setting the datanode ID in the commands, so that we can distinguish
comment|// commands when the cluster simulator is running.
if|if
condition|(
operator|!
name|request
operator|.
name|hasDatanodeUuid
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid Datanode ID"
argument_list|)
throw|;
block|}
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
name|future
init|=
operator|new
name|CompletableFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|ResponseFuture
name|response
init|=
operator|new
name|ResponseFuture
argument_list|(
name|future
argument_list|,
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
argument_list|)
decl_stmt|;
name|semaphore
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|ResponseFuture
name|previous
init|=
name|responses
operator|.
name|putIfAbsent
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Command with Trace already exists. Ignoring this command. "
operator|+
literal|"{}. Previous Command: {}"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|,
name|previous
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Duplicate trace ID. Command with this "
operator|+
literal|"trace ID is already executing. Please ensure that "
operator|+
literal|"trace IDs are not reused. ID: "
operator|+
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
throw|;
block|}
name|channel
operator|.
name|writeAndFlush
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getFuture
argument_list|()
return|;
block|}
comment|/**    * Class wraps response future info.    */
DECL|class|ResponseFuture
specifier|static
class|class
name|ResponseFuture
block|{
DECL|field|requestTime
specifier|private
specifier|final
name|long
name|requestTime
decl_stmt|;
DECL|field|future
specifier|private
specifier|final
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
name|future
decl_stmt|;
DECL|method|ResponseFuture (CompletableFuture<ContainerCommandResponseProto> future, long requestTime)
name|ResponseFuture
parameter_list|(
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
name|future
parameter_list|,
name|long
name|requestTime
parameter_list|)
block|{
name|this
operator|.
name|future
operator|=
name|future
expr_stmt|;
name|this
operator|.
name|requestTime
operator|=
name|requestTime
expr_stmt|;
block|}
DECL|method|getRequestTime ()
specifier|public
name|long
name|getRequestTime
parameter_list|()
block|{
return|return
name|requestTime
return|;
block|}
DECL|method|getFuture ()
specifier|public
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
name|getFuture
parameter_list|()
block|{
return|return
name|future
return|;
block|}
block|}
block|}
end_class

end_unit

