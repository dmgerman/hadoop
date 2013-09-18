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
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|RpcCallCache
operator|.
name|CacheEntry
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
name|VerifierNone
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
name|channel
operator|.
name|Channel
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
specifier|final
name|int
name|port
decl_stmt|;
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
DECL|field|rpcCallCache
specifier|private
specifier|final
name|RpcCallCache
name|rpcCallCache
decl_stmt|;
comment|/**    * Constructor    *     * @param program program name    * @param host host where the Rpc server program is started    * @param port port where the Rpc server program is listening to    * @param progNumber program number as defined in RFC 1050    * @param lowProgVersion lowest version of the specification supported    * @param highProgVersion highest version of the specification supported    * @param cacheSize size of cache to handle duplciate requests. Size<= 0    *          indicates no cache.    */
DECL|method|RpcProgram (String program, String host, int port, int progNumber, int lowProgVersion, int highProgVersion, int cacheSize)
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
parameter_list|,
name|int
name|cacheSize
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
name|this
operator|.
name|rpcCallCache
operator|=
name|cacheSize
operator|>
literal|0
condition|?
operator|new
name|RpcCallCache
argument_list|(
name|program
argument_list|,
name|cacheSize
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
comment|/**    * Register this program with the local portmapper.    */
DECL|method|register (int transport)
specifier|public
name|void
name|register
parameter_list|(
name|int
name|transport
parameter_list|)
block|{
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
name|register
argument_list|(
name|vers
argument_list|,
name|transport
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Register this program with the local portmapper.    */
DECL|method|register (int progVersion, int transport)
specifier|private
name|void
name|register
parameter_list|(
name|int
name|progVersion
parameter_list|,
name|int
name|transport
parameter_list|)
block|{
name|PortmapMapping
name|mapEntry
init|=
operator|new
name|PortmapMapping
argument_list|(
name|progNumber
argument_list|,
name|progVersion
argument_list|,
name|transport
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|register
argument_list|(
name|mapEntry
argument_list|)
expr_stmt|;
block|}
comment|/**    * Register the program with Portmap or Rpcbind    */
DECL|method|register (PortmapMapping mapEntry)
specifier|protected
name|void
name|register
parameter_list|(
name|PortmapMapping
name|mapEntry
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Registration failure with "
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
literal|"Registration failure"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Handle an RPC request.    * @param rpcCall RPC call that is received    * @param in xdr with cursor at reading the remaining bytes of a method call    * @param out xdr output corresponding to Rpc reply    * @param client making the Rpc request    * @param channel connection over which Rpc request is received    * @return response xdr response    */
DECL|method|handleInternal (RpcCall rpcCall, XDR in, XDR out, InetAddress client, Channel channel)
specifier|protected
specifier|abstract
name|XDR
name|handleInternal
parameter_list|(
name|RpcCall
name|rpcCall
parameter_list|,
name|XDR
name|in
parameter_list|,
name|XDR
name|out
parameter_list|,
name|InetAddress
name|client
parameter_list|,
name|Channel
name|channel
parameter_list|)
function_decl|;
DECL|method|handle (XDR xdr, InetAddress client, Channel channel)
specifier|public
name|XDR
name|handle
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|InetAddress
name|client
parameter_list|,
name|Channel
name|channel
parameter_list|)
block|{
name|XDR
name|out
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|RpcCall
name|rpcCall
init|=
name|RpcCall
operator|.
name|read
argument_list|(
name|xdr
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|program
operator|+
literal|" procedure #"
operator|+
name|rpcCall
operator|.
name|getProcedure
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|checkProgram
argument_list|(
name|rpcCall
operator|.
name|getProgram
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|programMismatch
argument_list|(
name|out
argument_list|,
name|rpcCall
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|checkProgramVersion
argument_list|(
name|rpcCall
operator|.
name|getVersion
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|programVersionMismatch
argument_list|(
name|out
argument_list|,
name|rpcCall
argument_list|)
return|;
block|}
comment|// Check for duplicate requests in the cache for non-idempotent requests
name|boolean
name|idempotent
init|=
name|rpcCallCache
operator|!=
literal|null
operator|&&
operator|!
name|isIdempotent
argument_list|(
name|rpcCall
argument_list|)
decl_stmt|;
if|if
condition|(
name|idempotent
condition|)
block|{
name|CacheEntry
name|entry
init|=
name|rpcCallCache
operator|.
name|checkOrAddToCache
argument_list|(
name|client
argument_list|,
name|rpcCall
operator|.
name|getXid
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
comment|// in ache
if|if
condition|(
name|entry
operator|.
name|isCompleted
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending the cached reply to retransmitted request "
operator|+
name|rpcCall
operator|.
name|getXid
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entry
operator|.
name|getResponse
argument_list|()
return|;
block|}
else|else
block|{
comment|// else request is in progress
name|LOG
operator|.
name|info
argument_list|(
literal|"Retransmitted request, transaction still in progress "
operator|+
name|rpcCall
operator|.
name|getXid
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: ignore the request?
block|}
block|}
block|}
name|XDR
name|response
init|=
name|handleInternal
argument_list|(
name|rpcCall
argument_list|,
name|xdr
argument_list|,
name|out
argument_list|,
name|client
argument_list|,
name|channel
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No sync response, expect an async response for request XID="
operator|+
name|rpcCall
operator|.
name|getXid
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add the request to the cache
if|if
condition|(
name|idempotent
condition|)
block|{
name|rpcCallCache
operator|.
name|callCompleted
argument_list|(
name|client
argument_list|,
name|rpcCall
operator|.
name|getXid
argument_list|()
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
DECL|method|programMismatch (XDR out, RpcCall call)
specifier|private
name|XDR
name|programMismatch
parameter_list|(
name|XDR
name|out
parameter_list|,
name|RpcCall
name|call
parameter_list|)
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
operator|new
name|VerifierNone
argument_list|()
argument_list|)
decl_stmt|;
name|reply
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
DECL|method|programVersionMismatch (XDR out, RpcCall call)
specifier|private
name|XDR
name|programVersionMismatch
parameter_list|(
name|XDR
name|out
parameter_list|,
name|RpcCall
name|call
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid RPC call version "
operator|+
name|call
operator|.
name|getVersion
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
name|PROG_MISMATCH
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
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
return|return
name|out
return|;
block|}
DECL|method|checkProgram (int progNumber)
specifier|private
name|boolean
name|checkProgram
parameter_list|(
name|int
name|progNumber
parameter_list|)
block|{
return|return
name|this
operator|.
name|progNumber
operator|==
name|progNumber
return|;
block|}
comment|/** Return true if a the program version in rpcCall is supported */
DECL|method|checkProgramVersion (int programVersion)
specifier|private
name|boolean
name|checkProgramVersion
parameter_list|(
name|int
name|programVersion
parameter_list|)
block|{
return|return
name|programVersion
operator|>=
name|lowProgVersion
operator|&&
name|programVersion
operator|<=
name|highProgVersion
return|;
block|}
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

