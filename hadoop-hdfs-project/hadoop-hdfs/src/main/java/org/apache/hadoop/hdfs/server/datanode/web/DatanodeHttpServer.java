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
name|ChannelFactory
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

begin_class
DECL|class|DatanodeHttpServer
specifier|public
class|class
name|DatanodeHttpServer
implements|implements
name|Closeable
block|{
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
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DatanodeHttpServer
operator|.
name|class
argument_list|)
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
name|HTTP_MAX_THREADS
argument_list|,
literal|10
argument_list|)
expr_stmt|;
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
literal|"http://localhost:0"
argument_list|)
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
decl_stmt|;
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
name|ch
operator|.
name|pipeline
argument_list|()
operator|.
name|addLast
argument_list|(
operator|new
name|PortUnificationServerHandler
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
block|{}
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
argument_list|,
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
block|}
end_class

end_unit

