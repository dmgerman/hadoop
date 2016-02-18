begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|web
package|;
end_package

begin_import
import|import static
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
name|HttpHeaderNames
operator|.
name|CONNECTION
import|;
end_import

begin_import
import|import static
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
name|HttpHeaderValues
operator|.
name|CLOSE
import|;
end_import

begin_import
import|import static
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
name|HttpResponseStatus
operator|.
name|INTERNAL_SERVER_ERROR
import|;
end_import

begin_import
import|import static
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
name|HttpVersion
operator|.
name|HTTP_1_1
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
name|channel
operator|.
name|SimpleChannelInboundHandler
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
name|DefaultHttpResponse
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
name|HttpResponseStatus
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|ReferenceCountUtil
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
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|http
operator|.
name|RestCsrfPreventionFilter
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
name|http
operator|.
name|RestCsrfPreventionFilter
operator|.
name|HttpInteraction
import|;
end_import

begin_comment
comment|/**  * Netty handler that integrates with the {@link RestCsrfPreventionFilter}.  If  * the filter determines that the request is allowed, then this handler forwards  * the request to the next handler in the Netty pipeline.  Otherwise, this  * handler drops the request and immediately sends an HTTP 400 response.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RestCsrfPreventionFilterHandler
specifier|final
class|class
name|RestCsrfPreventionFilterHandler
extends|extends
name|SimpleChannelInboundHandler
argument_list|<
name|HttpRequest
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|DatanodeHttpServer
operator|.
name|LOG
decl_stmt|;
DECL|field|restCsrfPreventionFilter
specifier|private
specifier|final
name|RestCsrfPreventionFilter
name|restCsrfPreventionFilter
decl_stmt|;
comment|/**    * Creates a new RestCsrfPreventionFilterHandler.  There will be a new    * instance created for each new Netty channel/pipeline serving a new request.    * To prevent the cost of repeated initialization of the filter, this    * constructor requires the caller to pass in a pre-built, fully initialized    * filter instance.  The filter is stateless after initialization, so it can    * be shared across multiple Netty channels/pipelines.    *    * @param restCsrfPreventionFilter initialized filter    */
DECL|method|RestCsrfPreventionFilterHandler ( RestCsrfPreventionFilter restCsrfPreventionFilter)
specifier|public
name|RestCsrfPreventionFilterHandler
parameter_list|(
name|RestCsrfPreventionFilter
name|restCsrfPreventionFilter
parameter_list|)
block|{
name|this
operator|.
name|restCsrfPreventionFilter
operator|=
name|restCsrfPreventionFilter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|channelRead0 (final ChannelHandlerContext ctx, final HttpRequest req)
specifier|protected
name|void
name|channelRead0
parameter_list|(
specifier|final
name|ChannelHandlerContext
name|ctx
parameter_list|,
specifier|final
name|HttpRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|restCsrfPreventionFilter
operator|.
name|handleHttpInteraction
argument_list|(
operator|new
name|NettyHttpInteraction
argument_list|(
name|ctx
argument_list|,
name|req
argument_list|)
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|sendResponseAndClose
argument_list|(
name|ctx
argument_list|,
operator|new
name|DefaultHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|INTERNAL_SERVER_ERROR
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Finish handling this pipeline by writing a response with the    * "Connection: close" header, flushing, and scheduling a close of the    * connection.    *    * @param ctx context to receive the response    * @param resp response to send    */
DECL|method|sendResponseAndClose (ChannelHandlerContext ctx, DefaultHttpResponse resp)
specifier|private
specifier|static
name|void
name|sendResponseAndClose
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|DefaultHttpResponse
name|resp
parameter_list|)
block|{
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|CONNECTION
argument_list|,
name|CLOSE
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|writeAndFlush
argument_list|(
name|resp
argument_list|)
operator|.
name|addListener
argument_list|(
name|ChannelFutureListener
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@link HttpInteraction} implementation for use in a Netty pipeline.    */
DECL|class|NettyHttpInteraction
specifier|private
specifier|static
specifier|final
class|class
name|NettyHttpInteraction
implements|implements
name|HttpInteraction
block|{
DECL|field|ctx
specifier|private
specifier|final
name|ChannelHandlerContext
name|ctx
decl_stmt|;
DECL|field|req
specifier|private
specifier|final
name|HttpRequest
name|req
decl_stmt|;
comment|/**      * Creates a new NettyHttpInteraction.      *      * @param ctx context to receive the response      * @param req request to process      */
DECL|method|NettyHttpInteraction (ChannelHandlerContext ctx, HttpRequest req)
specifier|public
name|NettyHttpInteraction
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|HttpRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHeader (String header)
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|header
parameter_list|)
block|{
return|return
name|req
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|header
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMethod ()
specifier|public
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|req
operator|.
name|method
argument_list|()
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|proceed ()
specifier|public
name|void
name|proceed
parameter_list|()
block|{
name|ReferenceCountUtil
operator|.
name|retain
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|fireChannelRead
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendError (int code, String message)
specifier|public
name|void
name|sendError
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|HttpResponseStatus
name|status
init|=
operator|new
name|HttpResponseStatus
argument_list|(
name|code
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|sendResponseAndClose
argument_list|(
name|ctx
argument_list|,
operator|new
name|DefaultHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

