begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.netty
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|netty
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelFuture
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
name|ChannelFutureListener
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
name|ChannelHandlerContext
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
name|codec
operator|.
name|http
operator|.
name|HttpContent
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
name|codec
operator|.
name|http
operator|.
name|HttpHeaderUtil
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
name|codec
operator|.
name|http
operator|.
name|HttpRequest
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
name|codec
operator|.
name|http
operator|.
name|HttpResponse
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
name|codec
operator|.
name|http
operator|.
name|LastHttpContent
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
name|stream
operator|.
name|ChunkedStream
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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

begin_comment
comment|/**  * Object Store Netty channel pipeline handler that handles inbound  * {@link HttpContent} fragments for the request body by sending the bytes into  * the pipe so that the application dispatch thread can read it.  * After receiving the {@link LastHttpContent}, this handler also flushes the  * response.  */
end_comment

begin_class
DECL|class|RequestContentObjectStoreChannelHandler
specifier|public
specifier|final
class|class
name|RequestContentObjectStoreChannelHandler
extends|extends
name|ObjectStoreChannelHandler
argument_list|<
name|HttpContent
argument_list|>
block|{
DECL|field|nettyReq
specifier|private
specifier|final
name|HttpRequest
name|nettyReq
decl_stmt|;
DECL|field|nettyResp
specifier|private
specifier|final
name|Future
argument_list|<
name|HttpResponse
argument_list|>
name|nettyResp
decl_stmt|;
DECL|field|reqOut
specifier|private
specifier|final
name|OutputStream
name|reqOut
decl_stmt|;
DECL|field|respIn
specifier|private
specifier|final
name|InputStream
name|respIn
decl_stmt|;
comment|/**    * Creates a new RequestContentObjectStoreChannelHandler.    *    * @param nettyReq HTTP request    * @param nettyResp asynchronous HTTP response    * @param reqOut output stream for writing request body    * @param respIn input stream for reading response body    */
DECL|method|RequestContentObjectStoreChannelHandler (HttpRequest nettyReq, Future<HttpResponse> nettyResp, OutputStream reqOut, InputStream respIn)
specifier|public
name|RequestContentObjectStoreChannelHandler
parameter_list|(
name|HttpRequest
name|nettyReq
parameter_list|,
name|Future
argument_list|<
name|HttpResponse
argument_list|>
name|nettyResp
parameter_list|,
name|OutputStream
name|reqOut
parameter_list|,
name|InputStream
name|respIn
parameter_list|)
block|{
name|this
operator|.
name|nettyReq
operator|=
name|nettyReq
expr_stmt|;
name|this
operator|.
name|nettyResp
operator|=
name|nettyResp
expr_stmt|;
name|this
operator|.
name|reqOut
operator|=
name|reqOut
expr_stmt|;
name|this
operator|.
name|respIn
operator|=
name|respIn
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|channelReadComplete (ChannelHandlerContext ctx)
specifier|public
name|void
name|channelReadComplete
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
name|ctx
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|channelRead0 (ChannelHandlerContext ctx, HttpContent content)
specifier|public
name|void
name|channelRead0
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|HttpContent
name|content
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"begin RequestContentObjectStoreChannelHandler channelRead0, "
operator|+
literal|"ctx = {}, content = {}"
argument_list|,
name|ctx
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|content
operator|.
name|content
argument_list|()
operator|.
name|readBytes
argument_list|(
name|this
operator|.
name|reqOut
argument_list|,
name|content
operator|.
name|content
argument_list|()
operator|.
name|readableBytes
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|content
operator|instanceof
name|LastHttpContent
condition|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|reqOut
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|write
argument_list|(
name|this
operator|.
name|nettyResp
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ChannelFuture
name|respFuture
init|=
name|ctx
operator|.
name|writeAndFlush
argument_list|(
operator|new
name|ChunkedStream
argument_list|(
name|this
operator|.
name|respIn
argument_list|)
argument_list|)
decl_stmt|;
name|respFuture
operator|.
name|addListener
argument_list|(
operator|new
name|CloseableCleanupListener
argument_list|(
name|this
operator|.
name|respIn
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|HttpHeaderUtil
operator|.
name|isKeepAlive
argument_list|(
name|this
operator|.
name|nettyReq
argument_list|)
condition|)
block|{
name|respFuture
operator|.
name|addListener
argument_list|(
name|ChannelFutureListener
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"end RequestContentObjectStoreChannelHandler channelRead0, "
operator|+
literal|"ctx = {}, content = {}"
argument_list|,
name|ctx
argument_list|,
name|content
argument_list|)
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
name|super
operator|.
name|exceptionCaught
argument_list|(
name|ctx
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|reqOut
argument_list|,
name|this
operator|.
name|respIn
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

