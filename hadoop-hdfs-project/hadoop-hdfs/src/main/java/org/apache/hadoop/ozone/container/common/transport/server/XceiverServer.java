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
name|io
operator|.
name|netty
operator|.
name|bootstrap
operator|.
name|ServerBootstrap
import|;
end_import

begin_import
import|import
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
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|EventLoopGroup
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|nio
operator|.
name|NioEventLoopGroup
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|socket
operator|.
name|nio
operator|.
name|NioServerSocketChannel
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|logging
operator|.
name|LogLevel
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|logging
operator|.
name|LoggingHandler
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

begin_comment
comment|/**  * Creates a netty server endpoint that acts as the communication layer for  * Ozone containers.  */
end_comment

begin_class
DECL|class|XceiverServer
specifier|public
specifier|final
class|class
name|XceiverServer
block|{
DECL|field|port
specifier|private
specifier|final
name|int
name|port
decl_stmt|;
DECL|field|storageContainer
specifier|private
specifier|final
name|ContainerDispatcher
name|storageContainer
decl_stmt|;
DECL|field|bossGroup
specifier|private
name|EventLoopGroup
name|bossGroup
decl_stmt|;
DECL|field|workerGroup
specifier|private
name|EventLoopGroup
name|workerGroup
decl_stmt|;
DECL|field|channel
specifier|private
name|Channel
name|channel
decl_stmt|;
comment|/**    * Constructs a netty server class.    *    * @param conf - Configuration    */
DECL|method|XceiverServer (Configuration conf, ContainerDispatcher dispatcher)
specifier|public
name|XceiverServer
parameter_list|(
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
name|DFS_OZONE_CONTAINER_IPC_PORT
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_OZONE_CONTAINER_IPC_PORT_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageContainer
operator|=
name|dispatcher
expr_stmt|;
block|}
comment|/**    * Starts running the server.    *    * @throws Exception    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|bossGroup
operator|=
operator|new
name|NioEventLoopGroup
argument_list|()
expr_stmt|;
name|workerGroup
operator|=
operator|new
name|NioEventLoopGroup
argument_list|()
expr_stmt|;
name|channel
operator|=
operator|new
name|ServerBootstrap
argument_list|()
operator|.
name|group
argument_list|(
name|bossGroup
argument_list|,
name|workerGroup
argument_list|)
operator|.
name|channel
argument_list|(
name|NioServerSocketChannel
operator|.
name|class
argument_list|)
operator|.
name|handler
argument_list|(
operator|new
name|LoggingHandler
argument_list|(
name|LogLevel
operator|.
name|INFO
argument_list|)
argument_list|)
operator|.
name|childHandler
argument_list|(
operator|new
name|XceiverServerInitializer
argument_list|(
name|storageContainer
argument_list|)
argument_list|)
operator|.
name|bind
argument_list|(
name|port
argument_list|)
operator|.
name|syncUninterruptibly
argument_list|()
operator|.
name|channel
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stops a running server.    *    * @throws Exception    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|bossGroup
operator|!=
literal|null
condition|)
block|{
name|bossGroup
operator|.
name|shutdownGracefully
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|workerGroup
operator|!=
literal|null
condition|)
block|{
name|workerGroup
operator|.
name|shutdownGracefully
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|channel
operator|!=
literal|null
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
operator|.
name|awaitUninterruptibly
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

