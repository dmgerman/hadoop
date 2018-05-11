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
name|ChannelInitializer
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
name|ChannelPipeline
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
name|socket
operator|.
name|SocketChannel
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
name|handler
operator|.
name|codec
operator|.
name|protobuf
operator|.
name|ProtobufDecoder
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
name|handler
operator|.
name|codec
operator|.
name|protobuf
operator|.
name|ProtobufEncoder
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
name|handler
operator|.
name|codec
operator|.
name|protobuf
operator|.
name|ProtobufVarint32FrameDecoder
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
name|handler
operator|.
name|codec
operator|.
name|protobuf
operator|.
name|ProtobufVarint32LengthFieldPrepender
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

begin_comment
comment|/**  * Creates a channel for the XceiverServer.  */
end_comment

begin_class
DECL|class|XceiverServerInitializer
specifier|public
class|class
name|XceiverServerInitializer
extends|extends
name|ChannelInitializer
argument_list|<
name|SocketChannel
argument_list|>
block|{
DECL|field|dispatcher
specifier|private
specifier|final
name|ContainerDispatcher
name|dispatcher
decl_stmt|;
DECL|method|XceiverServerInitializer (ContainerDispatcher dispatcher)
specifier|public
name|XceiverServerInitializer
parameter_list|(
name|ContainerDispatcher
name|dispatcher
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
block|}
comment|/**    * This method will be called once the Channel is registered. After    * the method returns this instance will be removed from the {@link    * ChannelPipeline}    *    * @param ch the  which was registered.    * @throws Exception is thrown if an error occurs. In that case the channel    * will be closed.    */
annotation|@
name|Override
DECL|method|initChannel (SocketChannel ch)
specifier|protected
name|void
name|initChannel
parameter_list|(
name|SocketChannel
name|ch
parameter_list|)
throws|throws
name|Exception
block|{
name|ChannelPipeline
name|pipeline
init|=
name|ch
operator|.
name|pipeline
argument_list|()
decl_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
operator|new
name|ProtobufVarint32FrameDecoder
argument_list|()
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
operator|new
name|ProtobufDecoder
argument_list|(
name|ContainerCommandRequestProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
operator|new
name|ProtobufVarint32LengthFieldPrepender
argument_list|()
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
operator|new
name|ProtobufEncoder
argument_list|()
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
operator|new
name|XceiverServerHandler
argument_list|(
name|dispatcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

