begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
package|;
end_package

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
name|namenode
operator|.
name|NameNodeHttpServer
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
name|service
operator|.
name|AbstractService
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

begin_comment
comment|/**  * Web interface for the {@link Router}. It exposes the Web UI and the WebHDFS  * methods from {@link RouterWebHdfsMethods}.  */
end_comment

begin_class
DECL|class|RouterHttpServer
specifier|public
class|class
name|RouterHttpServer
extends|extends
name|AbstractService
block|{
DECL|field|NAMENODE_ATTRIBUTE_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|NAMENODE_ATTRIBUTE_KEY
init|=
literal|"name.node"
decl_stmt|;
comment|/** Configuration for the Router HTTP server. */
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/** Router using this HTTP server. */
DECL|field|router
specifier|private
specifier|final
name|Router
name|router
decl_stmt|;
comment|/** HTTP server. */
DECL|field|httpServer
specifier|private
name|HttpServer2
name|httpServer
decl_stmt|;
comment|/** HTTP addresses. */
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
DECL|method|RouterHttpServer (Router router)
specifier|public
name|RouterHttpServer
parameter_list|(
name|Router
name|router
parameter_list|)
block|{
name|super
argument_list|(
name|RouterHttpServer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration configuration)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|conf
operator|=
name|configuration
expr_stmt|;
comment|// Get HTTP address
name|this
operator|.
name|httpAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTP_BIND_HOST_KEY
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTP_ADDRESS_KEY
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTP_ADDRESS_DEFAULT
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTP_PORT_DEFAULT
argument_list|)
expr_stmt|;
comment|// Get HTTPs address
name|this
operator|.
name|httpsAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTPS_BIND_HOST_KEY
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTPS_ADDRESS_KEY
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTPS_ADDRESS_DEFAULT
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTPS_PORT_DEFAULT
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Build and start server
name|String
name|webApp
init|=
literal|"router"
decl_stmt|;
name|HttpServer2
operator|.
name|Builder
name|builder
init|=
name|DFSUtil
operator|.
name|httpServerTemplateForNNAndJN
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|this
operator|.
name|httpAddress
argument_list|,
name|this
operator|.
name|httpsAddress
argument_list|,
name|webApp
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KERBEROS_INTERNAL_SPNEGO_PRINCIPAL_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|)
decl_stmt|;
name|this
operator|.
name|httpServer
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|NameNodeHttpServer
operator|.
name|initWebHdfs
argument_list|(
name|conf
argument_list|,
name|httpAddress
operator|.
name|getHostName
argument_list|()
argument_list|,
name|httpServer
argument_list|,
name|RouterWebHdfsMethods
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|NAMENODE_ATTRIBUTE_KEY
argument_list|,
name|this
operator|.
name|router
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|JspHelper
operator|.
name|CURRENT_CONF
argument_list|,
name|this
operator|.
name|conf
argument_list|)
expr_stmt|;
name|setupServlets
argument_list|(
name|this
operator|.
name|httpServer
argument_list|,
name|this
operator|.
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// The server port can be ephemeral... ensure we have the correct info
name|InetSocketAddress
name|listenAddress
init|=
name|this
operator|.
name|httpServer
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|listenAddress
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|httpAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|this
operator|.
name|httpAddress
operator|.
name|getHostName
argument_list|()
argument_list|,
name|listenAddress
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|httpServer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|httpServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|setupServlets ( HttpServer2 httpServer, Configuration conf)
specifier|private
specifier|static
name|void
name|setupServlets
parameter_list|(
name|HttpServer2
name|httpServer
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
comment|// TODO Add servlets for FSCK, etc
name|httpServer
operator|.
name|addInternalServlet
argument_list|(
name|IsRouterActiveServlet
operator|.
name|SERVLET_NAME
argument_list|,
name|IsRouterActiveServlet
operator|.
name|PATH_SPEC
argument_list|,
name|IsRouterActiveServlet
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|getHttpAddress ()
specifier|public
name|InetSocketAddress
name|getHttpAddress
parameter_list|()
block|{
return|return
name|this
operator|.
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
name|this
operator|.
name|httpsAddress
return|;
block|}
DECL|method|getRouterFromContext (ServletContext context)
specifier|public
specifier|static
name|Router
name|getRouterFromContext
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
return|return
operator|(
name|Router
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|NAMENODE_ATTRIBUTE_KEY
argument_list|)
return|;
block|}
block|}
end_class

end_unit

