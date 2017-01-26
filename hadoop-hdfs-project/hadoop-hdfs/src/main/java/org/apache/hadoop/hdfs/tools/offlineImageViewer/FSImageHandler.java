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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ByteBuf
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|Unpooled
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
name|channel
operator|.
name|group
operator|.
name|ChannelGroup
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
name|DefaultFullHttpResponse
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
name|HttpMethod
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
name|util
operator|.
name|StringUtils
import|;
end_import

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
name|HttpHeaders
operator|.
name|Names
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
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_LENGTH
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
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_TYPE
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
name|HttpHeaders
operator|.
name|Values
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
name|BAD_REQUEST
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
name|FORBIDDEN
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
name|HttpResponseStatus
operator|.
name|METHOD_NOT_ALLOWED
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
name|NOT_FOUND
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
import|import static
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
operator|.
name|webhdfs
operator|.
name|WebHdfsHandler
operator|.
name|APPLICATION_JSON_UTF8
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|web
operator|.
name|webhdfs
operator|.
name|WebHdfsHandler
operator|.
name|WEBHDFS_PREFIX
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|web
operator|.
name|webhdfs
operator|.
name|WebHdfsHandler
operator|.
name|WEBHDFS_PREFIX_LENGTH
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
name|SimpleChannelInboundHandler
argument_list|<
name|HttpRequest
argument_list|>
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
DECL|field|activeChannels
specifier|private
specifier|final
name|ChannelGroup
name|activeChannels
decl_stmt|;
annotation|@
name|Override
DECL|method|channelActive (ChannelHandlerContext ctx)
specifier|public
name|void
name|channelActive
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
name|activeChannels
operator|.
name|add
argument_list|(
name|ctx
operator|.
name|channel
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|FSImageHandler (FSImageLoader image, ChannelGroup activeChannels)
name|FSImageHandler
parameter_list|(
name|FSImageLoader
name|image
parameter_list|,
name|ChannelGroup
name|activeChannels
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
name|this
operator|.
name|activeChannels
operator|=
name|activeChannels
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|channelRead0 (ChannelHandlerContext ctx, HttpRequest request)
specifier|public
name|void
name|channelRead0
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|HttpRequest
name|request
parameter_list|)
throws|throws
name|Exception
block|{
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
name|DefaultHttpResponse
name|resp
init|=
operator|new
name|DefaultHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|METHOD_NOT_ALLOWED
argument_list|)
decl_stmt|;
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
name|write
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
return|return;
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
specifier|final
name|String
name|content
decl_stmt|;
name|String
name|path
init|=
name|getPath
argument_list|(
name|decoder
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|op
condition|)
block|{
case|case
literal|"GETFILESTATUS"
case|:
name|content
operator|=
name|image
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"LISTSTATUS"
case|:
name|content
operator|=
name|image
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"GETACLSTATUS"
case|:
name|content
operator|=
name|image
operator|.
name|getAclStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"GETXATTRS"
case|:
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|getXattrNames
argument_list|(
name|decoder
argument_list|)
decl_stmt|;
name|String
name|encoder
init|=
name|getEncoder
argument_list|(
name|decoder
argument_list|)
decl_stmt|;
name|content
operator|=
name|image
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|,
name|names
argument_list|,
name|encoder
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"LISTXATTRS"
case|:
name|content
operator|=
name|image
operator|.
name|listXAttrs
argument_list|(
name|path
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"GETCONTENTSUMMARY"
case|:
name|content
operator|=
name|image
operator|.
name|getContentSummary
argument_list|(
name|path
argument_list|)
expr_stmt|;
break|break;
default|default:
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
name|LOG
operator|.
name|info
argument_list|(
literal|"op="
operator|+
name|op
operator|+
literal|" target="
operator|+
name|path
argument_list|)
expr_stmt|;
name|DefaultFullHttpResponse
name|resp
init|=
operator|new
name|DefaultFullHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|HttpResponseStatus
operator|.
name|OK
argument_list|,
name|Unpooled
operator|.
name|wrappedBuffer
argument_list|(
name|content
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|CONTENT_TYPE
argument_list|,
name|APPLICATION_JSON_UTF8
argument_list|)
expr_stmt|;
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|CONTENT_LENGTH
argument_list|,
name|resp
operator|.
name|content
argument_list|()
operator|.
name|readableBytes
argument_list|()
argument_list|)
expr_stmt|;
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
name|write
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
throws|throws
name|Exception
block|{
name|Exception
name|e
init|=
name|cause
operator|instanceof
name|Exception
condition|?
operator|(
name|Exception
operator|)
name|cause
else|:
operator|new
name|Exception
argument_list|(
name|cause
argument_list|)
decl_stmt|;
specifier|final
name|String
name|output
init|=
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|ByteBuf
name|content
init|=
name|Unpooled
operator|.
name|wrappedBuffer
argument_list|(
name|output
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DefaultFullHttpResponse
name|resp
init|=
operator|new
name|DefaultFullHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|INTERNAL_SERVER_ERROR
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|CONTENT_TYPE
argument_list|,
name|APPLICATION_JSON_UTF8
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|IllegalArgumentException
condition|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|FileNotFoundException
condition|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|NOT_FOUND
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|IOException
condition|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|FORBIDDEN
argument_list|)
expr_stmt|;
block|}
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|CONTENT_LENGTH
argument_list|,
name|resp
operator|.
name|content
argument_list|()
operator|.
name|readableBytes
argument_list|()
argument_list|)
expr_stmt|;
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
name|write
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
name|parameters
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
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
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
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|getXattrNames (QueryStringDecoder decoder)
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getXattrNames
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
name|parameters
argument_list|()
decl_stmt|;
return|return
name|parameters
operator|.
name|get
argument_list|(
literal|"xattr.name"
argument_list|)
return|;
block|}
DECL|method|getEncoder (QueryStringDecoder decoder)
specifier|private
specifier|static
name|String
name|getEncoder
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
name|parameters
argument_list|()
decl_stmt|;
return|return
name|parameters
operator|.
name|containsKey
argument_list|(
literal|"encoding"
argument_list|)
condition|?
name|parameters
operator|.
name|get
argument_list|(
literal|"encoding"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|path
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
name|WEBHDFS_PREFIX
argument_list|)
condition|)
block|{
return|return
name|path
operator|.
name|substring
argument_list|(
name|WEBHDFS_PREFIX_LENGTH
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
literal|"start with "
operator|+
name|WEBHDFS_PREFIX
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

