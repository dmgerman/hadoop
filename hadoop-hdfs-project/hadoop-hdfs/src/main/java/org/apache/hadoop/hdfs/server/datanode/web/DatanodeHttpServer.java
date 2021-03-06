begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import
name|io
operator|.
name|netty
operator|.
name|bootstrap
operator|.
name|ChannelFactory
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|bootstrap
operator|.
name|ServerBootstrap
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
name|ChannelHandler
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
name|NioServerSocketChannel
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
name|HttpRequestDecoder
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
name|HttpResponseEncoder
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
name|ssl
operator|.
name|SslHandler
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
name|ChunkedWriteHandler
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
name|DFSConfigKeys
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
name|DFSUtil
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
name|common
operator|.
name|JspHelper
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
name|BlockScanner
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
name|DataNodeUGIProvider
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
name|http
operator|.
name|HttpConfig
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
name|http
operator|.
name|HttpServer2
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
name|net
operator|.
name|NetUtils
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
name|authorize
operator|.
name|AccessControlList
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
name|ssl
operator|.
name|SSLFactory
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
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|BindException
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
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
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
name|nio
operator|.
name|channels
operator|.
name|ServerSocketChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|DFSConfigKeys
operator|.
name|DFS_ADMIN
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
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_ADDRESS_DEFAULT
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
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTP_ADDRESS_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTP_INTERNAL_PROXY_PORT
import|;
end_import

begin_comment
comment|/**  * Data node HTTP Server Class.  */
end_comment

begin_class
DECL|class|DatanodeHttpServer
specifier|public
class|class
name|DatanodeHttpServer
implements|implements
name|Closeable
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
name|DatanodeHttpServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HANDLER_STATE
specifier|private
specifier|static
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Object
argument_list|>
name|HANDLER_STATE
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{}
empty_stmt|;
comment|// HttpServer threads are only used for the web UI and basic servlets, so
comment|// set them to the minimum possible
DECL|field|HTTP_SELECTOR_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|HTTP_SELECTOR_THREADS
init|=
literal|1
decl_stmt|;
DECL|field|HTTP_ACCEPTOR_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|HTTP_ACCEPTOR_THREADS
init|=
literal|1
decl_stmt|;
comment|// Jetty 9.4.x: Adding one more thread to HTTP_MAX_THREADS.
DECL|field|HTTP_MAX_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|HTTP_MAX_THREADS
init|=
name|HTTP_SELECTOR_THREADS
operator|+
name|HTTP_ACCEPTOR_THREADS
operator|+
literal|2
decl_stmt|;
DECL|field|infoServer
specifier|private
specifier|final
name|HttpServer2
name|infoServer
decl_stmt|;
DECL|field|bossGroup
specifier|private
specifier|final
name|EventLoopGroup
name|bossGroup
decl_stmt|;
DECL|field|workerGroup
specifier|private
specifier|final
name|EventLoopGroup
name|workerGroup
decl_stmt|;
DECL|field|externalHttpChannel
specifier|private
specifier|final
name|ServerSocketChannel
name|externalHttpChannel
decl_stmt|;
DECL|field|httpServer
specifier|private
specifier|final
name|ServerBootstrap
name|httpServer
decl_stmt|;
DECL|field|sslFactory
specifier|private
specifier|final
name|SSLFactory
name|sslFactory
decl_stmt|;
DECL|field|httpsServer
specifier|private
specifier|final
name|ServerBootstrap
name|httpsServer
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
DECL|field|httpAddress
specifier|private
name|InetSocketAddress
name|httpAddress
decl_stmt|;
DECL|field|httpsAddress
specifier|private
name|InetSocketAddress
name|httpsAddress
decl_stmt|;
DECL|method|DatanodeHttpServer (final Configuration conf, final DataNode datanode, final ServerSocketChannel externalHttpChannel)
specifier|public
name|DatanodeHttpServer
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|DataNode
name|datanode
parameter_list|,
specifier|final
name|ServerSocketChannel
name|externalHttpChannel
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
name|Configuration
name|confForInfoServer
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|confForInfoServer
operator|.
name|setInt
argument_list|(
name|HttpServer2
operator|.
name|HTTP_MAX_THREADS_KEY
argument_list|,
name|HTTP_MAX_THREADS
argument_list|)
expr_stmt|;
name|confForInfoServer
operator|.
name|setInt
argument_list|(
name|HttpServer2
operator|.
name|HTTP_SELECTOR_COUNT_KEY
argument_list|,
name|HTTP_SELECTOR_THREADS
argument_list|)
expr_stmt|;
name|confForInfoServer
operator|.
name|setInt
argument_list|(
name|HttpServer2
operator|.
name|HTTP_ACCEPTOR_COUNT_KEY
argument_list|,
name|HTTP_ACCEPTOR_THREADS
argument_list|)
expr_stmt|;
name|int
name|proxyPort
init|=
name|confForInfoServer
operator|.
name|getInt
argument_list|(
name|DFS_DATANODE_HTTP_INTERNAL_PROXY_PORT
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|HttpServer2
operator|.
name|Builder
name|builder
init|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"datanode"
argument_list|)
operator|.
name|setConf
argument_list|(
name|confForInfoServer
argument_list|)
operator|.
name|setACL
argument_list|(
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFS_ADMIN
argument_list|,
literal|" "
argument_list|)
argument_list|)
argument_list|)
operator|.
name|hostName
argument_list|(
name|getHostnameForSpnegoPrincipal
argument_list|(
name|confForInfoServer
argument_list|)
argument_list|)
operator|.
name|addEndpoint
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"http://localhost:"
operator|+
name|proxyPort
argument_list|)
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|xFrameEnabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_XFRAME_OPTION_ENABLED
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_XFRAME_OPTION_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|String
name|xFrameOptionValue
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_XFRAME_OPTION_VALUE
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_XFRAME_OPTION_VALUE_DEFAULT
argument_list|)
decl_stmt|;
name|builder
operator|.
name|configureXFrame
argument_list|(
name|xFrameEnabled
argument_list|)
operator|.
name|setXFrameOption
argument_list|(
name|xFrameOptionValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|infoServer
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|infoServer
operator|.
name|setAttribute
argument_list|(
name|HttpServer2
operator|.
name|CONF_CONTEXT_ATTRIBUTE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|infoServer
operator|.
name|setAttribute
argument_list|(
literal|"datanode"
argument_list|,
name|datanode
argument_list|)
expr_stmt|;
name|this
operator|.
name|infoServer
operator|.
name|setAttribute
argument_list|(
name|JspHelper
operator|.
name|CURRENT_CONF
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|infoServer
operator|.
name|addServlet
argument_list|(
literal|null
argument_list|,
literal|"/blockScannerReport"
argument_list|,
name|BlockScanner
operator|.
name|Servlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|DataNodeUGIProvider
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|infoServer
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|InetSocketAddress
name|jettyAddr
init|=
name|infoServer
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|this
operator|.
name|confForCreate
operator|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|confForCreate
operator|.
name|set
argument_list|(
name|FsPermission
operator|.
name|UMASK_LABEL
argument_list|,
literal|"000"
argument_list|)
expr_stmt|;
name|this
operator|.
name|bossGroup
operator|=
operator|new
name|NioEventLoopGroup
argument_list|()
expr_stmt|;
name|this
operator|.
name|workerGroup
operator|=
operator|new
name|NioEventLoopGroup
argument_list|()
expr_stmt|;
name|this
operator|.
name|externalHttpChannel
operator|=
name|externalHttpChannel
expr_stmt|;
name|HttpConfig
operator|.
name|Policy
name|policy
init|=
name|DFSUtil
operator|.
name|getHttpPolicy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|ChannelHandler
index|[]
name|handlers
init|=
name|getFilterHandlers
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|policy
operator|.
name|isHttpEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|httpServer
operator|=
operator|new
name|ServerBootstrap
argument_list|()
operator|.
name|group
argument_list|(
name|bossGroup
argument_list|,
name|workerGroup
argument_list|)
operator|.
name|childHandler
argument_list|(
operator|new
name|ChannelInitializer
argument_list|<
name|SocketChannel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|initChannel
parameter_list|(
name|SocketChannel
name|ch
parameter_list|)
throws|throws
name|Exception
block|{
name|ChannelPipeline
name|p
init|=
name|ch
operator|.
name|pipeline
argument_list|()
decl_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|HttpRequestDecoder
argument_list|()
argument_list|,
operator|new
name|HttpResponseEncoder
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|handlers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ChannelHandler
name|c
range|:
name|handlers
control|)
block|{
name|p
operator|.
name|addLast
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|ChunkedWriteHandler
argument_list|()
argument_list|,
operator|new
name|URLDispatcher
argument_list|(
name|jettyAddr
argument_list|,
name|conf
argument_list|,
name|confForCreate
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpServer
operator|.
name|childOption
argument_list|(
name|ChannelOption
operator|.
name|WRITE_BUFFER_HIGH_WATER_MARK
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_NETTY_HIGH_WATERMARK
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_NETTY_HIGH_WATERMARK_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpServer
operator|.
name|childOption
argument_list|(
name|ChannelOption
operator|.
name|WRITE_BUFFER_LOW_WATER_MARK
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_NETTY_LOW_WATERMARK
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_NETTY_LOW_WATERMARK_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|externalHttpChannel
operator|==
literal|null
condition|)
block|{
name|httpServer
operator|.
name|channel
argument_list|(
name|NioServerSocketChannel
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|httpServer
operator|.
name|channelFactory
argument_list|(
operator|new
name|ChannelFactory
argument_list|<
name|NioServerSocketChannel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NioServerSocketChannel
name|newChannel
parameter_list|()
block|{
return|return
operator|new
name|NioServerSocketChannel
argument_list|(
name|externalHttpChannel
argument_list|)
block|{
comment|// The channel has been bounded externally via JSVC,
comment|// thus bind() becomes a no-op.
annotation|@
name|Override
specifier|protected
name|void
name|doBind
parameter_list|(
name|SocketAddress
name|localAddress
parameter_list|)
throws|throws
name|Exception
block|{               }
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|httpServer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|policy
operator|.
name|isHttpsEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|sslFactory
operator|=
operator|new
name|SSLFactory
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|SERVER
argument_list|,
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|sslFactory
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|httpsServer
operator|=
operator|new
name|ServerBootstrap
argument_list|()
operator|.
name|group
argument_list|(
name|bossGroup
argument_list|,
name|workerGroup
argument_list|)
operator|.
name|channel
argument_list|(
name|NioServerSocketChannel
operator|.
name|class
argument_list|)
operator|.
name|childHandler
argument_list|(
operator|new
name|ChannelInitializer
argument_list|<
name|SocketChannel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|initChannel
parameter_list|(
name|SocketChannel
name|ch
parameter_list|)
throws|throws
name|Exception
block|{
name|ChannelPipeline
name|p
init|=
name|ch
operator|.
name|pipeline
argument_list|()
decl_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|SslHandler
argument_list|(
name|sslFactory
operator|.
name|createSSLEngine
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HttpRequestDecoder
argument_list|()
argument_list|,
operator|new
name|HttpResponseEncoder
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|handlers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ChannelHandler
name|c
range|:
name|handlers
control|)
block|{
name|p
operator|.
name|addLast
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|ChunkedWriteHandler
argument_list|()
argument_list|,
operator|new
name|URLDispatcher
argument_list|(
name|jettyAddr
argument_list|,
name|conf
argument_list|,
name|confForCreate
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|httpsServer
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|sslFactory
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getHostnameForSpnegoPrincipal (Configuration conf)
specifier|private
specifier|static
name|String
name|getHostnameForSpnegoPrincipal
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|addr
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFS_DATANODE_HTTP_ADDRESS_KEY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|==
literal|null
condition|)
block|{
name|addr
operator|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
argument_list|,
name|DFS_DATANODE_HTTPS_ADDRESS_DEFAULT
argument_list|)
expr_stmt|;
block|}
name|InetSocketAddress
name|inetSocker
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|)
decl_stmt|;
return|return
name|inetSocker
operator|.
name|getHostString
argument_list|()
return|;
block|}
comment|/* Get an array of ChannelHandlers specified in the conf    * @param conf configuration to read and pass    * @return array of ChannelHandlers ready to be used    * @throws NoSuchMethodException if the handler does not implement a method    *  initializeState(conf)    * @throws InvocationTargetException if the handler's initalizeState method    *  raises an exception    */
DECL|method|getFilterHandlers (Configuration configuration)
specifier|private
name|ChannelHandler
index|[]
name|getFilterHandlers
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// If the hdfs-site.xml has the proper configs for filter classes, use them.
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|classes
init|=
name|configuration
operator|.
name|getClasses
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPSERVER_FILTER_HANDLERS
argument_list|)
decl_stmt|;
comment|// else use the hard coded class from the default configuration.
if|if
condition|(
name|classes
operator|==
literal|null
condition|)
block|{
name|classes
operator|=
name|configuration
operator|.
name|getClasses
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPSERVER_FILTER_HANDLERS_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|// if we are not able to find any handlers, let us fail since running
comment|// with Csrf will is a security hole. Let us abort the startup.
if|if
condition|(
name|classes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ChannelHandler
index|[]
name|handlers
init|=
operator|new
name|ChannelHandler
index|[
name|classes
operator|.
name|length
index|]
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
name|classes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading filter handler {}"
argument_list|,
name|classes
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Method
name|initializeState
init|=
name|classes
index|[
name|i
index|]
operator|.
name|getDeclaredMethod
argument_list|(
literal|"initializeState"
argument_list|,
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|Constructor
name|constructor
init|=
name|classes
index|[
name|i
index|]
operator|.
name|getDeclaredConstructor
argument_list|(
name|initializeState
operator|.
name|getReturnType
argument_list|()
argument_list|)
decl_stmt|;
name|handlers
index|[
name|i
index|]
operator|=
operator|(
name|ChannelHandler
operator|)
name|constructor
operator|.
name|newInstance
argument_list|(
name|HANDLER_STATE
operator|.
name|getOrDefault
argument_list|(
name|classes
index|[
name|i
index|]
argument_list|,
name|initializeState
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|configuration
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
decl||
name|InvocationTargetException
decl||
name|IllegalAccessException
decl||
name|InstantiationException
decl||
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to initialize handler {}"
argument_list|,
name|classes
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|(
name|handlers
operator|)
return|;
block|}
DECL|method|getHttpAddress ()
specifier|public
name|InetSocketAddress
name|getHttpAddress
parameter_list|()
block|{
return|return
name|httpAddress
return|;
block|}
DECL|method|getHttpsAddress ()
specifier|public
name|InetSocketAddress
name|getHttpsAddress
parameter_list|()
block|{
return|return
name|httpsAddress
return|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|httpServer
operator|!=
literal|null
condition|)
block|{
name|InetSocketAddress
name|infoAddr
init|=
name|DataNode
operator|.
name|getInfoAddr
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ChannelFuture
name|f
init|=
name|httpServer
operator|.
name|bind
argument_list|(
name|infoAddr
argument_list|)
decl_stmt|;
try|try
block|{
name|f
operator|.
name|syncUninterruptibly
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|BindException
condition|)
block|{
throw|throw
name|NetUtils
operator|.
name|wrapException
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
name|infoAddr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|infoAddr
operator|.
name|getPort
argument_list|()
argument_list|,
operator|(
name|SocketException
operator|)
name|e
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|httpAddress
operator|=
operator|(
name|InetSocketAddress
operator|)
name|f
operator|.
name|channel
argument_list|()
operator|.
name|localAddress
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Listening HTTP traffic on "
operator|+
name|httpAddress
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|httpsServer
operator|!=
literal|null
condition|)
block|{
name|InetSocketAddress
name|secInfoSocAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
argument_list|,
name|DFS_DATANODE_HTTPS_ADDRESS_DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
name|ChannelFuture
name|f
init|=
name|httpsServer
operator|.
name|bind
argument_list|(
name|secInfoSocAddr
argument_list|)
decl_stmt|;
try|try
block|{
name|f
operator|.
name|syncUninterruptibly
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|BindException
condition|)
block|{
throw|throw
name|NetUtils
operator|.
name|wrapException
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
name|secInfoSocAddr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|secInfoSocAddr
operator|.
name|getPort
argument_list|()
argument_list|,
operator|(
name|SocketException
operator|)
name|e
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|httpsAddress
operator|=
operator|(
name|InetSocketAddress
operator|)
name|f
operator|.
name|channel
argument_list|()
operator|.
name|localAddress
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Listening HTTPS traffic on "
operator|+
name|httpsAddress
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|bossGroup
operator|.
name|shutdownGracefully
argument_list|()
expr_stmt|;
name|workerGroup
operator|.
name|shutdownGracefully
argument_list|()
expr_stmt|;
if|if
condition|(
name|sslFactory
operator|!=
literal|null
condition|)
block|{
name|sslFactory
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|externalHttpChannel
operator|!=
literal|null
condition|)
block|{
name|externalHttpChannel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|infoServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Since the DataNode HTTP server is not implemented in terms of the    * servlet API, it    * takes some extra effort to obtain an instance of the filter.  This    * method provides    * a minimal {@link FilterConfig} implementation backed by a {@link Map}.    * Call this from    * your filter handler to initialize a servlet filter.    */
DECL|class|MapBasedFilterConfig
specifier|public
specifier|static
specifier|final
class|class
name|MapBasedFilterConfig
implements|implements
name|FilterConfig
block|{
DECL|field|filterName
specifier|private
specifier|final
name|String
name|filterName
decl_stmt|;
DECL|field|parameters
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
decl_stmt|;
comment|/*      * Creates a new MapBasedFilterConfig.      *      * @param filterName filter name      * @param parameters mapping of filter initialization parameters      */
DECL|method|MapBasedFilterConfig (String filterName, Map<String, String> parameters)
specifier|public
name|MapBasedFilterConfig
parameter_list|(
name|String
name|filterName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|filterName
operator|=
name|filterName
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilterName ()
specifier|public
name|String
name|getFilterName
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterName
return|;
block|}
annotation|@
name|Override
DECL|method|getInitParameter (String name)
specifier|public
name|String
name|getInitParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|this
operator|.
name|parameters
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInitParameterNames ()
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getInitParameterNames
parameter_list|()
block|{
throw|throw
name|this
operator|.
name|notImplemented
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getServletContext ()
specifier|public
name|ServletContext
name|getServletContext
parameter_list|()
block|{
throw|throw
name|this
operator|.
name|notImplemented
argument_list|()
throw|;
block|}
comment|/*      * Creates an exception indicating that an interface method is not      * implemented. If you are building a handler it is possible you will      * need to make this interface more extensive.      *      * @return exception indicating method not implemented      */
DECL|method|notImplemented ()
specifier|private
name|UnsupportedOperationException
name|notImplemented
parameter_list|()
block|{
return|return
operator|new
name|UnsupportedOperationException
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" does not implement this method."
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

