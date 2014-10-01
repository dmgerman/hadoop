begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Map
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
name|hdfs
operator|.
name|web
operator|.
name|JsonUtil
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
name|ipc
operator|.
name|RemoteException
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
name|ChannelFuture
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
name|ChannelFutureListener
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

begin_import
import|import
name|org
operator|.
name|jboss
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
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
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
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpMethod
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
name|org
operator|.
name|jboss
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
name|org
operator|.
name|jboss
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
name|org
operator|.
name|jboss
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
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|QueryStringDecoder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Query
import|;
end_import

begin_comment
comment|/**  * Implement the read-only WebHDFS API for fsimage.  */
end_comment

begin_class
DECL|class|FSImageHandler
class|class
name|FSImageHandler
extends|extends
name|SimpleChannelUpstreamHandler
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSImageHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|image
specifier|private
specifier|final
name|FSImageLoader
name|image
decl_stmt|;
DECL|method|FSImageHandler (FSImageLoader image)
name|FSImageHandler
parameter_list|(
name|FSImageLoader
name|image
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|image
operator|=
name|image
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|messageReceived ( ChannelHandlerContext ctx, MessageEvent e)
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
name|ChannelFuture
name|future
init|=
name|e
operator|.
name|getFuture
argument_list|()
decl_stmt|;
try|try
block|{
name|future
operator|=
name|handleOperation
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|future
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
DECL|method|handleOperation (MessageEvent e)
specifier|private
name|ChannelFuture
name|handleOperation
parameter_list|(
name|MessageEvent
name|e
parameter_list|)
throws|throws
name|IOException
block|{
name|HttpRequest
name|request
init|=
operator|(
name|HttpRequest
operator|)
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|HttpResponse
name|response
init|=
operator|new
name|DefaultHttpResponse
argument_list|(
name|HttpVersion
operator|.
name|HTTP_1_1
argument_list|,
name|HttpResponseStatus
operator|.
name|OK
argument_list|)
decl_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/json"
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getMethod
argument_list|()
operator|!=
name|HttpMethod
operator|.
name|GET
condition|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpResponseStatus
operator|.
name|METHOD_NOT_ALLOWED
argument_list|)
expr_stmt|;
return|return
name|e
operator|.
name|getChannel
argument_list|()
operator|.
name|write
argument_list|(
name|response
argument_list|)
return|;
block|}
name|QueryStringDecoder
name|decoder
init|=
operator|new
name|QueryStringDecoder
argument_list|(
name|request
operator|.
name|getUri
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|op
init|=
name|getOp
argument_list|(
name|decoder
argument_list|)
decl_stmt|;
name|String
name|content
decl_stmt|;
name|String
name|path
init|=
literal|null
decl_stmt|;
try|try
block|{
name|path
operator|=
name|getPath
argument_list|(
name|decoder
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"GETFILESTATUS"
operator|.
name|equals
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|content
operator|=
name|image
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"LISTSTATUS"
operator|.
name|equals
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|content
operator|=
name|image
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"GETACLSTATUS"
operator|.
name|equals
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|content
operator|=
name|image
operator|.
name|getAclStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid value for webhdfs parameter"
operator|+
literal|" \"op\""
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpResponseStatus
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
name|content
operator|=
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ex
parameter_list|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpResponseStatus
operator|.
name|NOT_FOUND
argument_list|)
expr_stmt|;
name|content
operator|=
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|content
operator|=
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
name|HttpHeaders
operator|.
name|setContentLength
argument_list|(
name|response
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|getChannel
argument_list|()
operator|.
name|write
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|ChannelFuture
name|future
init|=
name|e
operator|.
name|getChannel
argument_list|()
operator|.
name|write
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|response
operator|.
name|getStatus
argument_list|()
operator|.
name|getCode
argument_list|()
operator|+
literal|" method="
operator|+
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" op="
operator|+
name|op
operator|+
literal|" target="
operator|+
name|path
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
DECL|method|getOp (QueryStringDecoder decoder)
specifier|private
specifier|static
name|String
name|getOp
parameter_list|(
name|QueryStringDecoder
name|decoder
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|parameters
init|=
name|decoder
operator|.
name|getParameters
argument_list|()
decl_stmt|;
return|return
name|parameters
operator|.
name|containsKey
argument_list|(
literal|"op"
argument_list|)
condition|?
name|parameters
operator|.
name|get
argument_list|(
literal|"op"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toUpperCase
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|getPath (QueryStringDecoder decoder)
specifier|private
specifier|static
name|String
name|getPath
parameter_list|(
name|QueryStringDecoder
name|decoder
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|String
name|path
init|=
name|decoder
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"/webhdfs/v1/"
argument_list|)
condition|)
block|{
return|return
name|path
operator|.
name|substring
argument_list|(
literal|11
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Path: "
operator|+
name|path
operator|+
literal|" should "
operator|+
literal|"start with \"/webhdfs/v1/\""
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

