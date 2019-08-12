begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.web.webhdfs
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
operator|.
name|webhdfs
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
name|HttpHeaders
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
name|QueryStringDecoder
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
name|fs
operator|.
name|CreateFlag
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
name|fs
operator|.
name|MD5MD5CRC32FileChecksum
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
name|fs
operator|.
name|permission
operator|.
name|FsCreateModes
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|DFSClient
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|client
operator|.
name|HdfsDataInputStream
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|hdfs
operator|.
name|web
operator|.
name|WebHdfsFileSystem
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
name|resources
operator|.
name|AclPermissionParam
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
name|resources
operator|.
name|GetOpParam
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
name|resources
operator|.
name|PostOpParam
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
name|resources
operator|.
name|PutOpParam
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
name|resources
operator|.
name|UserParam
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
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
name|token
operator|.
name|Token
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
name|LimitInputStream
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|ACCEPT
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
name|ACCESS_CONTROL_ALLOW_HEADERS
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
name|ACCESS_CONTROL_ALLOW_METHODS
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
name|ACCESS_CONTROL_ALLOW_ORIGIN
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
name|ACCESS_CONTROL_MAX_AGE
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
name|Names
operator|.
name|LOCATION
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
name|HttpHeaders
operator|.
name|Values
operator|.
name|KEEP_ALIVE
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
name|HttpMethod
operator|.
name|GET
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
name|HttpMethod
operator|.
name|OPTIONS
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
name|HttpMethod
operator|.
name|POST
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
name|HttpMethod
operator|.
name|PUT
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
name|CONTINUE
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
name|CREATED
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
name|OK
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
operator|.
name|HDFS_DELEGATION_KIND
import|;
end_import

begin_class
DECL|class|WebHdfsHandler
specifier|public
class|class
name|WebHdfsHandler
extends|extends
name|SimpleChannelInboundHandler
argument_list|<
name|HttpRequest
argument_list|>
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|WebHdfsHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REQLOG
specifier|static
specifier|final
name|Logger
name|REQLOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"datanode.webhdfs"
argument_list|)
decl_stmt|;
DECL|field|WEBHDFS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|WEBHDFS_PREFIX
init|=
name|WebHdfsFileSystem
operator|.
name|PATH_PREFIX
decl_stmt|;
DECL|field|WEBHDFS_PREFIX_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|WEBHDFS_PREFIX_LENGTH
init|=
name|WEBHDFS_PREFIX
operator|.
name|length
argument_list|()
decl_stmt|;
DECL|field|APPLICATION_OCTET_STREAM
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_OCTET_STREAM
init|=
literal|"application/octet-stream"
decl_stmt|;
DECL|field|APPLICATION_JSON_UTF8
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_JSON_UTF8
init|=
literal|"application/json; charset=utf-8"
decl_stmt|;
DECL|field|EMPTY_CREATE_FLAG
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|EMPTY_CREATE_FLAG
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|CreateFlag
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|confForCreate
specifier|private
specifier|final
name|Configuration
name|confForCreate
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|params
specifier|private
name|ParameterParser
name|params
decl_stmt|;
DECL|field|ugi
specifier|private
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|resp
specifier|private
name|DefaultHttpResponse
name|resp
init|=
literal|null
decl_stmt|;
DECL|method|WebHdfsHandler (Configuration conf, Configuration confForCreate)
specifier|public
name|WebHdfsHandler
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Configuration
name|confForCreate
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|confForCreate
operator|=
name|confForCreate
expr_stmt|;
comment|/** set user pattern based on configuration file */
name|UserParam
operator|.
name|setUserPattern
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_USER_PATTERN_KEY
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_USER_PATTERN_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|AclPermissionParam
operator|.
name|setAclPermissionPattern
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_ACL_PERMISSION_PATTERN_KEY
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_ACL_PERMISSION_PATTERN_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|channelRead0 (final ChannelHandlerContext ctx, final HttpRequest req)
specifier|public
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
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|req
operator|.
name|getUri
argument_list|()
operator|.
name|startsWith
argument_list|(
name|WEBHDFS_PREFIX
argument_list|)
argument_list|)
expr_stmt|;
name|QueryStringDecoder
name|queryString
init|=
operator|new
name|QueryStringDecoder
argument_list|(
name|req
operator|.
name|getUri
argument_list|()
argument_list|)
decl_stmt|;
name|params
operator|=
operator|new
name|ParameterParser
argument_list|(
name|queryString
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DataNodeUGIProvider
name|ugiProvider
init|=
operator|new
name|DataNodeUGIProvider
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|ugi
operator|=
name|ugiProvider
operator|.
name|ugi
argument_list|()
expr_stmt|;
name|path
operator|=
name|params
operator|.
name|path
argument_list|()
expr_stmt|;
name|injectToken
argument_list|()
expr_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|handle
argument_list|(
name|ctx
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|String
name|host
init|=
literal|null
decl_stmt|;
try|try
block|{
name|host
operator|=
operator|(
operator|(
name|InetSocketAddress
operator|)
name|ctx
operator|.
name|channel
argument_list|()
operator|.
name|remoteAddress
argument_list|()
operator|)
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error retrieving hostname: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|host
operator|=
literal|"unknown"
expr_stmt|;
block|}
name|REQLOG
operator|.
name|info
argument_list|(
name|host
operator|+
literal|" "
operator|+
name|req
operator|.
name|getMethod
argument_list|()
operator|+
literal|" "
operator|+
name|req
operator|.
name|getUri
argument_list|()
operator|+
literal|" "
operator|+
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|getResponseCode ()
name|int
name|getResponseCode
parameter_list|()
block|{
return|return
operator|(
name|resp
operator|==
literal|null
operator|)
condition|?
name|INTERNAL_SERVER_ERROR
operator|.
name|code
argument_list|()
else|:
name|resp
operator|.
name|getStatus
argument_list|()
operator|.
name|code
argument_list|()
return|;
block|}
DECL|method|handle (ChannelHandlerContext ctx, HttpRequest req)
specifier|public
name|void
name|handle
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|HttpRequest
name|req
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|String
name|op
init|=
name|params
operator|.
name|op
argument_list|()
decl_stmt|;
name|HttpMethod
name|method
init|=
name|req
operator|.
name|getMethod
argument_list|()
decl_stmt|;
if|if
condition|(
name|PutOpParam
operator|.
name|Op
operator|.
name|CREATE
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
operator|&&
name|method
operator|==
name|PUT
condition|)
block|{
name|onCreate
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|PostOpParam
operator|.
name|Op
operator|.
name|APPEND
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
operator|&&
name|method
operator|==
name|POST
condition|)
block|{
name|onAppend
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GetOpParam
operator|.
name|Op
operator|.
name|OPEN
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
operator|&&
name|method
operator|==
name|GET
condition|)
block|{
name|onOpen
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILECHECKSUM
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
operator|&&
name|method
operator|==
name|GET
condition|)
block|{
name|onGetFileChecksum
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|PutOpParam
operator|.
name|Op
operator|.
name|CREATE
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
operator|&&
name|method
operator|==
name|OPTIONS
condition|)
block|{
name|allowCORSOnCreate
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid operation "
operator|+
name|op
argument_list|)
throw|;
block|}
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
name|debug
argument_list|(
literal|"Error "
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|resp
operator|=
name|ExceptionHandler
operator|.
name|exceptionCaught
argument_list|(
name|cause
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
DECL|method|onCreate (ChannelHandlerContext ctx)
specifier|private
name|void
name|onCreate
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|writeContinueHeader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
specifier|final
name|String
name|nnId
init|=
name|params
operator|.
name|namenodeId
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bufferSize
init|=
name|params
operator|.
name|bufferSize
argument_list|()
decl_stmt|;
specifier|final
name|short
name|replication
init|=
name|params
operator|.
name|replication
argument_list|()
decl_stmt|;
specifier|final
name|long
name|blockSize
init|=
name|params
operator|.
name|blockSize
argument_list|()
decl_stmt|;
specifier|final
name|FsPermission
name|unmaskedPermission
init|=
name|params
operator|.
name|unmaskedPermission
argument_list|()
decl_stmt|;
specifier|final
name|FsPermission
name|permission
init|=
name|unmaskedPermission
operator|==
literal|null
condition|?
name|params
operator|.
name|permission
argument_list|()
else|:
name|FsCreateModes
operator|.
name|create
argument_list|(
name|params
operator|.
name|permission
argument_list|()
argument_list|,
name|unmaskedPermission
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|createParent
init|=
name|params
operator|.
name|createParent
argument_list|()
decl_stmt|;
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
init|=
name|params
operator|.
name|createFlag
argument_list|()
decl_stmt|;
if|if
condition|(
name|flags
operator|.
name|equals
argument_list|(
name|EMPTY_CREATE_FLAG
argument_list|)
condition|)
block|{
name|flags
operator|=
name|params
operator|.
name|overwrite
argument_list|()
condition|?
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|,
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
else|:
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|params
operator|.
name|overwrite
argument_list|()
condition|)
block|{
name|flags
operator|.
name|add
argument_list|(
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|DFSClient
name|dfsClient
init|=
name|newDfsClient
argument_list|(
name|nnId
argument_list|,
name|confForCreate
argument_list|)
decl_stmt|;
name|OutputStream
name|out
init|=
name|dfsClient
operator|.
name|createWrappedOutputStream
argument_list|(
name|dfsClient
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|permission
argument_list|,
name|flags
argument_list|,
name|createParent
argument_list|,
name|replication
argument_list|,
name|blockSize
argument_list|,
literal|null
argument_list|,
name|bufferSize
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|resp
operator|=
operator|new
name|DefaultHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|CREATED
argument_list|)
expr_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|HDFS_URI_SCHEME
argument_list|,
name|nnId
argument_list|,
name|path
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|LOCATION
argument_list|,
name|uri
operator|.
name|toString
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
name|CONTENT_LENGTH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|pipeline
argument_list|()
operator|.
name|replace
argument_list|(
name|this
argument_list|,
name|HdfsWriter
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
operator|new
name|HdfsWriter
argument_list|(
name|dfsClient
argument_list|,
name|out
argument_list|,
name|resp
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|onAppend (ChannelHandlerContext ctx)
specifier|private
name|void
name|onAppend
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|writeContinueHeader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
specifier|final
name|String
name|nnId
init|=
name|params
operator|.
name|namenodeId
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bufferSize
init|=
name|params
operator|.
name|bufferSize
argument_list|()
decl_stmt|;
name|DFSClient
name|dfsClient
init|=
name|newDfsClient
argument_list|(
name|nnId
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|OutputStream
name|out
init|=
name|dfsClient
operator|.
name|append
argument_list|(
name|path
argument_list|,
name|bufferSize
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|APPEND
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|resp
operator|=
operator|new
name|DefaultHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|OK
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
literal|0
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|pipeline
argument_list|()
operator|.
name|replace
argument_list|(
name|this
argument_list|,
name|HdfsWriter
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
operator|new
name|HdfsWriter
argument_list|(
name|dfsClient
argument_list|,
name|out
argument_list|,
name|resp
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|onOpen (ChannelHandlerContext ctx)
specifier|private
name|void
name|onOpen
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|nnId
init|=
name|params
operator|.
name|namenodeId
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bufferSize
init|=
name|params
operator|.
name|bufferSize
argument_list|()
decl_stmt|;
specifier|final
name|long
name|offset
init|=
name|params
operator|.
name|offset
argument_list|()
decl_stmt|;
specifier|final
name|long
name|length
init|=
name|params
operator|.
name|length
argument_list|()
decl_stmt|;
name|resp
operator|=
operator|new
name|DefaultHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|HttpHeaders
name|headers
init|=
name|resp
operator|.
name|headers
argument_list|()
decl_stmt|;
comment|// Allow the UI to access the file
name|headers
operator|.
name|set
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|,
name|GET
argument_list|)
expr_stmt|;
name|headers
operator|.
name|set
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|headers
operator|.
name|set
argument_list|(
name|CONTENT_TYPE
argument_list|,
name|APPLICATION_OCTET_STREAM
argument_list|)
expr_stmt|;
name|headers
operator|.
name|set
argument_list|(
name|CONNECTION
argument_list|,
name|CLOSE
argument_list|)
expr_stmt|;
specifier|final
name|DFSClient
name|dfsclient
init|=
name|newDfsClient
argument_list|(
name|nnId
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|HdfsDataInputStream
name|in
init|=
name|dfsclient
operator|.
name|createWrappedInputStream
argument_list|(
name|dfsclient
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|bufferSize
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|long
name|contentLength
init|=
name|in
operator|.
name|getVisibleLength
argument_list|()
operator|-
name|offset
decl_stmt|;
if|if
condition|(
name|length
operator|>=
literal|0
condition|)
block|{
name|contentLength
operator|=
name|Math
operator|.
name|min
argument_list|(
name|contentLength
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|final
name|InputStream
name|data
decl_stmt|;
if|if
condition|(
name|contentLength
operator|>=
literal|0
condition|)
block|{
name|headers
operator|.
name|set
argument_list|(
name|CONTENT_LENGTH
argument_list|,
name|contentLength
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|LimitInputStream
argument_list|(
name|in
argument_list|,
name|contentLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|data
operator|=
name|in
expr_stmt|;
block|}
name|ctx
operator|.
name|write
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|writeAndFlush
argument_list|(
operator|new
name|ChunkedStream
argument_list|(
name|data
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|dfsclient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
DECL|method|onGetFileChecksum (ChannelHandlerContext ctx)
specifier|private
name|void
name|onGetFileChecksum
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|MD5MD5CRC32FileChecksum
name|checksum
init|=
literal|null
decl_stmt|;
specifier|final
name|String
name|nnId
init|=
name|params
operator|.
name|namenodeId
argument_list|()
decl_stmt|;
name|DFSClient
name|dfsclient
init|=
name|newDfsClient
argument_list|(
name|nnId
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|checksum
operator|=
name|dfsclient
operator|.
name|getFileChecksum
argument_list|(
name|path
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|dfsclient
operator|.
name|close
argument_list|()
expr_stmt|;
name|dfsclient
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|dfsclient
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|js
init|=
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|checksum
argument_list|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|resp
operator|=
operator|new
name|DefaultFullHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|OK
argument_list|,
name|Unpooled
operator|.
name|wrappedBuffer
argument_list|(
name|js
argument_list|)
argument_list|)
expr_stmt|;
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
name|js
operator|.
name|length
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
comment|//Accept preflighted CORS requests
DECL|method|allowCORSOnCreate (ChannelHandlerContext ctx)
specifier|private
name|void
name|allowCORSOnCreate
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|resp
operator|=
operator|new
name|DefaultHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|HttpHeaders
name|headers
init|=
name|resp
operator|.
name|headers
argument_list|()
decl_stmt|;
name|headers
operator|.
name|set
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|headers
operator|.
name|set
argument_list|(
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|,
name|ACCEPT
argument_list|)
expr_stmt|;
name|headers
operator|.
name|set
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|,
name|PUT
argument_list|)
expr_stmt|;
name|headers
operator|.
name|set
argument_list|(
name|ACCESS_CONTROL_MAX_AGE
argument_list|,
literal|1728000
argument_list|)
expr_stmt|;
name|headers
operator|.
name|set
argument_list|(
name|CONTENT_LENGTH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|headers
operator|.
name|set
argument_list|(
name|CONNECTION
argument_list|,
name|KEEP_ALIVE
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
DECL|method|writeContinueHeader (ChannelHandlerContext ctx)
specifier|private
specifier|static
name|void
name|writeContinueHeader
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
block|{
name|DefaultHttpResponse
name|r
init|=
operator|new
name|DefaultFullHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|CONTINUE
argument_list|,
name|Unpooled
operator|.
name|EMPTY_BUFFER
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|writeAndFlush
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
DECL|method|newDfsClient (String nnId, Configuration conf)
specifier|private
specifier|static
name|DFSClient
name|newDfsClient
parameter_list|(
name|String
name|nnId
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|HDFS_URI_SCHEME
operator|+
literal|"://"
operator|+
name|nnId
argument_list|)
decl_stmt|;
return|return
operator|new
name|DFSClient
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
return|;
block|}
DECL|method|injectToken ()
specifier|private
name|void
name|injectToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|params
operator|.
name|delegationToken
argument_list|()
decl_stmt|;
name|token
operator|.
name|setKind
argument_list|(
name|HDFS_DELEGATION_KIND
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

