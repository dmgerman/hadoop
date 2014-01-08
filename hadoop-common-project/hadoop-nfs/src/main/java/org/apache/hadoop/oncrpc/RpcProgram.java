begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.oncrpc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|oncrpc
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|RpcAcceptedReply
operator|.
name|AcceptState
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
name|security
operator|.
name|Verifier
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
name|portmap
operator|.
name|PortmapRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
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
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|MessageEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|SimpleChannelUpstreamHandler
import|;
end_import

begin_comment
comment|/**  * Class for writing RPC server programs based on RFC 1050. Extend this class  * and implement {@link #handleInternal} to handle the requests received.  */
end_comment

begin_class
DECL|class|RpcProgram
specifier|public
specifier|abstract
class|class
name|RpcProgram
extends|extends
name|SimpleChannelUpstreamHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RpcProgram
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RPCB_PORT
specifier|public
specifier|static
specifier|final
name|int
name|RPCB_PORT
init|=
literal|111
decl_stmt|;
DECL|field|program
specifier|private
specifier|final
name|String
name|program
decl_stmt|;
DECL|field|host
specifier|private
specifier|final
name|String
name|host
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
comment|// Ephemeral port is chosen later
DECL|field|progNumber
specifier|private
specifier|final
name|int
name|progNumber
decl_stmt|;
DECL|field|lowProgVersion
specifier|private
specifier|final
name|int
name|lowProgVersion
decl_stmt|;
DECL|field|highProgVersion
specifier|private
specifier|final
name|int
name|highProgVersion
decl_stmt|;
comment|/**    * Constructor    *     * @param program program name    * @param host host where the Rpc server program is started    * @param port port where the Rpc server program is listening to    * @param progNumber program number as defined in RFC 1050    * @param lowProgVersion lowest version of the specification supported    * @param highProgVersion highest version of the specification supported    */
DECL|method|RpcProgram (String program, String host, int port, int progNumber, int lowProgVersion, int highProgVersion)
specifier|protected
name|RpcProgram
parameter_list|(
name|String
name|program
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|int
name|progNumber
parameter_list|,
name|int
name|lowProgVersion
parameter_list|,
name|int
name|highProgVersion
parameter_list|)
block|{
name|this
operator|.
name|program
operator|=
name|program
expr_stmt|;
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|progNumber
operator|=
name|progNumber
expr_stmt|;
name|this
operator|.
name|lowProgVersion
operator|=
name|lowProgVersion
expr_stmt|;
name|this
operator|.
name|highProgVersion
operator|=
name|highProgVersion
expr_stmt|;
block|}
comment|/**    * Register this program with the local portmapper.    */
DECL|method|register (int transport, int boundPort)
specifier|public
name|void
name|register
parameter_list|(
name|int
name|transport
parameter_list|,
name|int
name|boundPort
parameter_list|)
block|{
if|if
condition|(
name|boundPort
operator|!=
name|port
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The bound port is "
operator|+
name|boundPort
operator|+
literal|", different with configured port "
operator|+
name|port
argument_list|)
expr_stmt|;
name|port
operator|=
name|boundPort
expr_stmt|;
block|}
comment|// Register all the program versions with portmapper for a given transport
for|for
control|(
name|int
name|vers
init|=
name|lowProgVersion
init|;
name|vers
operator|<=
name|highProgVersion
condition|;
name|vers
operator|++
control|)
block|{
name|PortmapMapping
name|mapEntry
init|=
operator|new
name|PortmapMapping
argument_list|(
name|progNumber
argument_list|,
name|vers
argument_list|,
name|transport
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|register
argument_list|(
name|mapEntry
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Unregister this program with the local portmapper.    */
DECL|method|unregister (int transport, int boundPort)
specifier|public
name|void
name|unregister
parameter_list|(
name|int
name|transport
parameter_list|,
name|int
name|boundPort
parameter_list|)
block|{
if|if
condition|(
name|boundPort
operator|!=
name|port
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The bound port is "
operator|+
name|boundPort
operator|+
literal|", different with configured port "
operator|+
name|port
argument_list|)
expr_stmt|;
name|port
operator|=
name|boundPort
expr_stmt|;
block|}
comment|// Unregister all the program versions with portmapper for a given transport
for|for
control|(
name|int
name|vers
init|=
name|lowProgVersion
init|;
name|vers
operator|<=
name|highProgVersion
condition|;
name|vers
operator|++
control|)
block|{
name|PortmapMapping
name|mapEntry
init|=
operator|new
name|PortmapMapping
argument_list|(
name|progNumber
argument_list|,
name|vers
argument_list|,
name|transport
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|register
argument_list|(
name|mapEntry
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Register the program with Portmap or Rpcbind    */
DECL|method|register (PortmapMapping mapEntry, boolean set)
specifier|protected
name|void
name|register
parameter_list|(
name|PortmapMapping
name|mapEntry
parameter_list|,
name|boolean
name|set
parameter_list|)
block|{
name|XDR
name|mappingRequest
init|=
name|PortmapRequest
operator|.
name|create
argument_list|(
name|mapEntry
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|SimpleUdpClient
name|registrationClient
init|=
operator|new
name|SimpleUdpClient
argument_list|(
name|host
argument_list|,
name|RPCB_PORT
argument_list|,
name|mappingRequest
argument_list|)
decl_stmt|;
try|try
block|{
name|registrationClient
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|request
init|=
name|set
condition|?
literal|"Registration"
else|:
literal|"Unregistration"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|request
operator|+
literal|" failure with "
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
operator|+
literal|", portmap entry: "
operator|+
name|mapEntry
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|request
operator|+
literal|" failure"
argument_list|)
throw|;
block|}
block|}
comment|// Start extra daemons
DECL|method|startDaemons ()
specifier|public
name|void
name|startDaemons
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|messageReceived (ChannelHandlerContext ctx, MessageEvent e)
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|MessageEvent
name|e
parameter_list|)
throws|throws
name|Exception
block|{
name|RpcInfo
name|info
init|=
operator|(
name|RpcInfo
operator|)
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|RpcCall
name|call
init|=
operator|(
name|RpcCall
operator|)
name|info
operator|.
name|header
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|program
operator|+
literal|" procedure #"
operator|+
name|call
operator|.
name|getProcedure
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|progNumber
operator|!=
name|call
operator|.
name|getProgram
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid RPC call program "
operator|+
name|call
operator|.
name|getProgram
argument_list|()
argument_list|)
expr_stmt|;
name|RpcAcceptedReply
name|reply
init|=
name|RpcAcceptedReply
operator|.
name|getInstance
argument_list|(
name|call
operator|.
name|getXid
argument_list|()
argument_list|,
name|AcceptState
operator|.
name|PROG_UNAVAIL
argument_list|,
name|Verifier
operator|.
name|VERIFIER_NONE
argument_list|)
decl_stmt|;
name|XDR
name|out
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|reply
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|ChannelBuffer
name|b
init|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|out
operator|.
name|asReadOnlyWrap
argument_list|()
operator|.
name|buffer
argument_list|()
argument_list|)
decl_stmt|;
name|RpcResponse
name|rsp
init|=
operator|new
name|RpcResponse
argument_list|(
name|b
argument_list|,
name|info
operator|.
name|remoteAddress
argument_list|()
argument_list|)
decl_stmt|;
name|RpcUtil
operator|.
name|sendRpcResponse
argument_list|(
name|ctx
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|ver
init|=
name|call
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|ver
argument_list|<
name|lowProgVersion
operator|||
name|ver
argument_list|>
name|highProgVersion
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid RPC call version "
operator|+
name|ver
argument_list|)
expr_stmt|;
name|RpcAcceptedReply
name|reply
init|=
name|RpcAcceptedReply
operator|.
name|getInstance
argument_list|(
name|call
operator|.
name|getXid
argument_list|()
argument_list|,
name|AcceptState
operator|.
name|PROG_MISMATCH
argument_list|,
name|Verifier
operator|.
name|VERIFIER_NONE
argument_list|)
decl_stmt|;
name|XDR
name|out
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|reply
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|lowProgVersion
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|highProgVersion
argument_list|)
expr_stmt|;
name|ChannelBuffer
name|b
init|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|out
operator|.
name|asReadOnlyWrap
argument_list|()
operator|.
name|buffer
argument_list|()
argument_list|)
decl_stmt|;
name|RpcResponse
name|rsp
init|=
operator|new
name|RpcResponse
argument_list|(
name|b
argument_list|,
name|info
operator|.
name|remoteAddress
argument_list|()
argument_list|)
decl_stmt|;
name|RpcUtil
operator|.
name|sendRpcResponse
argument_list|(
name|ctx
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
name|handleInternal
argument_list|(
name|ctx
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
DECL|method|handleInternal (ChannelHandlerContext ctx, RpcInfo info)
specifier|protected
specifier|abstract
name|void
name|handleInternal
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Rpc program: "
operator|+
name|program
operator|+
literal|" at "
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
return|;
block|}
DECL|method|isIdempotent (RpcCall call)
specifier|protected
specifier|abstract
name|boolean
name|isIdempotent
parameter_list|(
name|RpcCall
name|call
parameter_list|)
function_decl|;
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
block|}
end_class

end_unit

