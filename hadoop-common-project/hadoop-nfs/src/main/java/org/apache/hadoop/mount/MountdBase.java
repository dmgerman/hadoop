begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mount
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mount
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|oncrpc
operator|.
name|RpcProgram
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
name|oncrpc
operator|.
name|SimpleTcpServer
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
name|oncrpc
operator|.
name|SimpleUdpServer
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
name|portmap
operator|.
name|PortmapMapping
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
name|ShutdownHookManager
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ExitUtil
operator|.
name|terminate
import|;
end_import

begin_comment
comment|/**  * Main class for starting mountd daemon. This daemon implements the NFS  * mount protocol. When receiving a MOUNT request from an NFS client, it checks  * the request against the list of currently exported file systems. If the  * client is permitted to mount the file system, rpc.mountd obtains a file  * handle for requested directory and returns it to the client.  */
end_comment

begin_class
DECL|class|MountdBase
specifier|abstract
specifier|public
class|class
name|MountdBase
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
name|MountdBase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rpcProgram
specifier|private
specifier|final
name|RpcProgram
name|rpcProgram
decl_stmt|;
DECL|field|udpBoundPort
specifier|private
name|int
name|udpBoundPort
decl_stmt|;
comment|// Will set after server starts
DECL|field|tcpBoundPort
specifier|private
name|int
name|tcpBoundPort
decl_stmt|;
comment|// Will set after server starts
DECL|method|getRpcProgram ()
specifier|public
name|RpcProgram
name|getRpcProgram
parameter_list|()
block|{
return|return
name|rpcProgram
return|;
block|}
comment|/**    * Constructor    * @param program  rpc server which handles mount request    * @throws IOException fail to construct MountdBase    */
DECL|method|MountdBase (RpcProgram program)
specifier|public
name|MountdBase
parameter_list|(
name|RpcProgram
name|program
parameter_list|)
throws|throws
name|IOException
block|{
name|rpcProgram
operator|=
name|program
expr_stmt|;
block|}
comment|/* Start UDP server */
DECL|method|startUDPServer ()
specifier|private
name|void
name|startUDPServer
parameter_list|()
block|{
name|SimpleUdpServer
name|udpServer
init|=
operator|new
name|SimpleUdpServer
argument_list|(
name|rpcProgram
operator|.
name|getPort
argument_list|()
argument_list|,
name|rpcProgram
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|rpcProgram
operator|.
name|startDaemons
argument_list|()
expr_stmt|;
try|try
block|{
name|udpServer
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to start the UDP server."
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|udpServer
operator|.
name|getBoundPort
argument_list|()
operator|>
literal|0
condition|)
block|{
name|rpcProgram
operator|.
name|unregister
argument_list|(
name|PortmapMapping
operator|.
name|TRANSPORT_UDP
argument_list|,
name|udpServer
operator|.
name|getBoundPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|udpServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|udpBoundPort
operator|=
name|udpServer
operator|.
name|getBoundPort
argument_list|()
expr_stmt|;
block|}
comment|/* Start TCP server */
DECL|method|startTCPServer ()
specifier|private
name|void
name|startTCPServer
parameter_list|()
block|{
name|SimpleTcpServer
name|tcpServer
init|=
operator|new
name|SimpleTcpServer
argument_list|(
name|rpcProgram
operator|.
name|getPort
argument_list|()
argument_list|,
name|rpcProgram
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|rpcProgram
operator|.
name|startDaemons
argument_list|()
expr_stmt|;
try|try
block|{
name|tcpServer
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to start the TCP server."
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|tcpServer
operator|.
name|getBoundPort
argument_list|()
operator|>
literal|0
condition|)
block|{
name|rpcProgram
operator|.
name|unregister
argument_list|(
name|PortmapMapping
operator|.
name|TRANSPORT_TCP
argument_list|,
name|tcpServer
operator|.
name|getBoundPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tcpServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|tcpBoundPort
operator|=
name|tcpServer
operator|.
name|getBoundPort
argument_list|()
expr_stmt|;
block|}
DECL|method|start (boolean register)
specifier|public
name|void
name|start
parameter_list|(
name|boolean
name|register
parameter_list|)
block|{
name|startUDPServer
argument_list|()
expr_stmt|;
name|startTCPServer
argument_list|()
expr_stmt|;
if|if
condition|(
name|register
condition|)
block|{
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Unregister
argument_list|()
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
try|try
block|{
name|rpcProgram
operator|.
name|register
argument_list|(
name|PortmapMapping
operator|.
name|TRANSPORT_UDP
argument_list|,
name|udpBoundPort
argument_list|)
expr_stmt|;
name|rpcProgram
operator|.
name|register
argument_list|(
name|PortmapMapping
operator|.
name|TRANSPORT_TCP
argument_list|,
name|tcpBoundPort
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to register the MOUNT service."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|udpBoundPort
operator|>
literal|0
condition|)
block|{
name|rpcProgram
operator|.
name|unregister
argument_list|(
name|PortmapMapping
operator|.
name|TRANSPORT_UDP
argument_list|,
name|udpBoundPort
argument_list|)
expr_stmt|;
name|udpBoundPort
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|tcpBoundPort
operator|>
literal|0
condition|)
block|{
name|rpcProgram
operator|.
name|unregister
argument_list|(
name|PortmapMapping
operator|.
name|TRANSPORT_TCP
argument_list|,
name|tcpBoundPort
argument_list|)
expr_stmt|;
name|tcpBoundPort
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**    * Priority of the mountd shutdown hook.    */
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|public
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|10
decl_stmt|;
DECL|class|Unregister
specifier|private
class|class
name|Unregister
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

