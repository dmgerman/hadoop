begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.client
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
name|client
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|bootstrap
operator|.
name|Bootstrap
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
name|ChannelInitializer
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
name|ChannelOption
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
name|ChannelPipeline
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
name|SocketChannel
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
name|NioSocketChannel
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
name|DefaultFullHttpRequest
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
name|FullHttpRequest
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
name|HttpClientCodec
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
name|HttpContentDecompressor
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
name|HttpObject
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
name|HttpVersion
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|MiniOzoneClassicCluster
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|OzoneConsts
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
name|client
operator|.
name|rest
operator|.
name|headers
operator|.
name|Header
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
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|CloseableHttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|utils
operator|.
name|URIBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|HttpClients
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|PoolingHttpClientConnectionManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|HttpHeaders
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
name|net
operator|.
name|HttpURLConnection
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
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|CharsetUtil
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Unit tests for Ozone client connection reuse with Apache HttpClient and Netty  * based HttpClient.  */
end_comment

begin_class
DECL|class|TestOzoneClient
specifier|public
class|class
name|TestOzoneClient
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TestOzoneClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|testVolumeCount
specifier|private
specifier|static
name|int
name|testVolumeCount
init|=
literal|5
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneClassicCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|endpoint
specifier|private
specifier|static
name|String
name|endpoint
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"log4j.logger.org.apache.http"
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_HANDLER_TYPE_KEY
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|endpoint
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"http://localhost:%d"
argument_list|,
name|MiniOzoneClassicCluster
operator|.
name|getOzoneRestPort
argument_list|(
name|dataNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testNewConnectionPerRequest ()
specifier|public
name|void
name|testNewConnectionPerRequest
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testVolumeCount
condition|;
name|i
operator|++
control|)
block|{
try|try
init|(
name|CloseableHttpClient
name|httpClient
init|=
name|HttpClients
operator|.
name|createDefault
argument_list|()
init|)
block|{
name|createVolume
argument_list|(
name|getRandomVolumeName
argument_list|(
name|i
argument_list|)
argument_list|,
name|httpClient
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Object handler should be able to serve multiple requests from    * a single http client. This allows the client side to reuse    * http connections in a connection pool instead of creating a new    * connection per request which consumes resource heavily.    *    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testReuseWithApacheHttpClient ()
specifier|public
name|void
name|testReuseWithApacheHttpClient
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|PoolingHttpClientConnectionManager
name|cm
init|=
operator|new
name|PoolingHttpClientConnectionManager
argument_list|()
decl_stmt|;
name|cm
operator|.
name|setMaxTotal
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setDefaultMaxPerRoute
argument_list|(
literal|20
argument_list|)
expr_stmt|;
try|try
init|(
name|CloseableHttpClient
name|httpClient
init|=
name|HttpClients
operator|.
name|custom
argument_list|()
operator|.
name|setConnectionManager
argument_list|(
name|cm
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testVolumeCount
condition|;
name|i
operator|++
control|)
block|{
name|createVolume
argument_list|(
name|getRandomVolumeName
argument_list|(
name|i
argument_list|)
argument_list|,
name|httpClient
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testReuseWithNettyHttpClient ()
specifier|public
name|void
name|testReuseWithNettyHttpClient
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|URISyntaxException
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|endpoint
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|uri
operator|.
name|getHost
argument_list|()
operator|==
literal|null
condition|?
literal|"127.0.0.1"
else|:
name|uri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|uri
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|EventLoopGroup
name|workerGroup
init|=
operator|new
name|NioEventLoopGroup
argument_list|()
decl_stmt|;
try|try
block|{
name|Bootstrap
name|b
init|=
operator|new
name|Bootstrap
argument_list|()
decl_stmt|;
name|b
operator|.
name|group
argument_list|(
name|workerGroup
argument_list|)
operator|.
name|channel
argument_list|(
name|NioSocketChannel
operator|.
name|class
argument_list|)
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|SO_KEEPALIVE
argument_list|,
literal|true
argument_list|)
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|SO_REUSEADDR
argument_list|,
literal|true
argument_list|)
operator|.
name|handler
argument_list|(
operator|new
name|ChannelInitializer
argument_list|<
name|SocketChannel
argument_list|>
argument_list|()
block|{
comment|/**              * This method will be called once the {@link Channel} was              * registered. After the method returns this instance              * will be removed from the {@link ChannelPipeline}              * of the {@link Channel}.              *              * @param ch the {@link Channel} which was registered.              * @throws Exception is thrown if an error occurs.              * In that case the {@link Channel} will be closed.              */
annotation|@
name|Override
specifier|public
name|void
name|initChannel
parameter_list|(
name|SocketChannel
name|ch
parameter_list|)
block|{
name|ChannelPipeline
name|p
init|=
name|ch
operator|.
name|pipeline
argument_list|()
decl_stmt|;
comment|// Comment the following line if you don't want client http trace
name|p
operator|.
name|addLast
argument_list|(
literal|"log"
argument_list|,
operator|new
name|LoggingHandler
argument_list|(
name|LogLevel
operator|.
name|INFO
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|HttpClientCodec
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|HttpContentDecompressor
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|NettyHttpClientHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Channel
name|ch
init|=
name|b
operator|.
name|connect
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
operator|.
name|sync
argument_list|()
operator|.
name|channel
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testVolumeCount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|volumeName
init|=
name|getRandomVolumeName
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|sendNettyCreateVolumeRequest
argument_list|(
name|ch
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|ch
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Wait for the server to close the connection.
name|ch
operator|.
name|closeFuture
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error received in client setup"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|workerGroup
operator|.
name|shutdownGracefully
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|NettyHttpClientHandler
class|class
name|NettyHttpClientHandler
extends|extends
name|SimpleChannelInboundHandler
argument_list|<
name|HttpObject
argument_list|>
block|{
annotation|@
name|Override
DECL|method|channelRead0 (ChannelHandlerContext ctx, HttpObject msg)
specifier|public
name|void
name|channelRead0
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|HttpObject
name|msg
parameter_list|)
block|{
if|if
condition|(
name|msg
operator|instanceof
name|HttpResponse
condition|)
block|{
name|HttpResponse
name|response
init|=
operator|(
name|HttpResponse
operator|)
name|msg
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"STATUS: "
operator|+
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"VERSION: "
operator|+
name|response
operator|.
name|getProtocolVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpResponseStatus
operator|.
name|CREATED
operator|.
name|code
argument_list|()
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|instanceof
name|HttpContent
condition|)
block|{
name|HttpContent
name|content
init|=
operator|(
name|HttpContent
operator|)
name|msg
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|content
operator|.
name|content
argument_list|()
operator|.
name|toString
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|content
operator|instanceof
name|LastHttpContent
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"END OF CONTENT"
argument_list|)
expr_stmt|;
block|}
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
name|log
operator|.
name|error
argument_list|(
literal|"Exception upon channel read"
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getRandomVolumeName (int index)
specifier|private
name|String
name|getRandomVolumeName
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|UUID
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
return|return
literal|"test-volume-"
operator|+
name|index
operator|+
literal|"-"
operator|+
name|id
return|;
block|}
comment|// Prepare the HTTP request and send it over the netty channel.
DECL|method|sendNettyCreateVolumeRequest (Channel channel, String volumeName)
specifier|private
name|void
name|sendNettyCreateVolumeRequest
parameter_list|(
name|Channel
name|channel
parameter_list|,
name|String
name|volumeName
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|URIBuilder
name|builder
init|=
operator|new
name|URIBuilder
argument_list|(
name|endpoint
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|volumeName
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|uri
operator|.
name|getHost
argument_list|()
operator|==
literal|null
condition|?
literal|"127.0.0.1"
else|:
name|uri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|FullHttpRequest
name|request
init|=
operator|new
name|DefaultFullHttpRequest
argument_list|(
name|HttpVersion
operator|.
name|HTTP_1_1
argument_list|,
name|HttpMethod
operator|.
name|POST
argument_list|,
name|uri
operator|.
name|getRawPath
argument_list|()
argument_list|)
decl_stmt|;
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"EEE, dd MMM yyyy HH:mm:ss ZZZ"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|request
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|HttpHeaders
operator|.
name|HOST
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|request
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/json"
argument_list|)
expr_stmt|;
name|request
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|Header
operator|.
name|OZONE_VERSION_HEADER
argument_list|,
name|Header
operator|.
name|OZONE_V1_VERSION_HEADER
argument_list|)
expr_stmt|;
name|request
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|HttpHeaders
operator|.
name|DATE
argument_list|,
name|format
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|Header
operator|.
name|OZONE_USER
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|HttpHeaders
operator|.
name|AUTHORIZATION
argument_list|,
name|Header
operator|.
name|OZONE_SIMPLE_AUTHENTICATION_SCHEME
operator|+
literal|" "
operator|+
name|OzoneConsts
operator|.
name|OZONE_SIMPLE_HDFS_USER
argument_list|)
expr_stmt|;
comment|// Send the HTTP request via netty channel.
name|channel
operator|.
name|writeAndFlush
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
comment|// It is caller's responsibility to close the client.
DECL|method|createVolume (String volumeName, CloseableHttpClient httpClient)
specifier|private
name|void
name|createVolume
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|CloseableHttpClient
name|httpClient
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|HttpPost
name|create1
init|=
name|getCreateVolumeRequest
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|HttpEntity
name|entity
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CloseableHttpResponse
name|response1
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|create1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_CREATED
argument_list|,
name|response1
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|=
name|response1
operator|.
name|getEntity
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCreateVolumeRequest (String volumeName)
specifier|private
name|HttpPost
name|getCreateVolumeRequest
parameter_list|(
name|String
name|volumeName
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|URIBuilder
name|builder
init|=
operator|new
name|URIBuilder
argument_list|(
name|endpoint
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|volumeName
argument_list|)
expr_stmt|;
name|HttpPost
name|httpPost
init|=
operator|new
name|HttpPost
argument_list|(
name|builder
operator|.
name|build
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"EEE, dd MMM yyyy HH:mm:ss ZZZ"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
name|Header
operator|.
name|OZONE_VERSION_HEADER
argument_list|,
name|Header
operator|.
name|OZONE_V1_VERSION_HEADER
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
name|HttpHeaders
operator|.
name|DATE
argument_list|,
name|format
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
name|Header
operator|.
name|OZONE_USER
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
name|HttpHeaders
operator|.
name|AUTHORIZATION
argument_list|,
name|Header
operator|.
name|OZONE_SIMPLE_AUTHENTICATION_SCHEME
operator|+
literal|" "
operator|+
name|OzoneConsts
operator|.
name|OZONE_SIMPLE_HDFS_USER
argument_list|)
expr_stmt|;
return|return
name|httpPost
return|;
block|}
block|}
end_class

end_unit

