begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
operator|.
name|nfs3
package|;
end_package

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
comment|/**  * Nfs server. Supports NFS v3 using {@link RpcProgram}.  * Only TCP server is supported and UDP is not supported.  */
end_comment

begin_class
DECL|class|Nfs3Base
specifier|public
specifier|abstract
class|class
name|Nfs3Base
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
name|Nfs3Base
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
DECL|field|nfsBoundPort
specifier|private
name|int
name|nfsBoundPort
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
DECL|method|Nfs3Base (RpcProgram rpcProgram, Configuration conf)
specifier|protected
name|Nfs3Base
parameter_list|(
name|RpcProgram
name|rpcProgram
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|rpcProgram
operator|=
name|rpcProgram
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"NFS server port set to: "
operator|+
name|rpcProgram
operator|.
name|getPort
argument_list|()
argument_list|)
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
name|startTCPServer
argument_list|()
expr_stmt|;
comment|// Start TCP server
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
name|NfsShutdownHook
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
name|TRANSPORT_TCP
argument_list|,
name|nfsBoundPort
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
literal|"Failed to register the NFSv3 service."
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
literal|0
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
name|nfsBoundPort
operator|=
name|tcpServer
operator|.
name|getBoundPort
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|nfsBoundPort
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
name|nfsBoundPort
argument_list|)
expr_stmt|;
name|nfsBoundPort
operator|=
literal|0
expr_stmt|;
block|}
name|rpcProgram
operator|.
name|stopDaemons
argument_list|()
expr_stmt|;
block|}
comment|/**    * Priority of the nfsd shutdown hook.    */
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|public
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|10
decl_stmt|;
DECL|class|NfsShutdownHook
specifier|private
class|class
name|NfsShutdownHook
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

