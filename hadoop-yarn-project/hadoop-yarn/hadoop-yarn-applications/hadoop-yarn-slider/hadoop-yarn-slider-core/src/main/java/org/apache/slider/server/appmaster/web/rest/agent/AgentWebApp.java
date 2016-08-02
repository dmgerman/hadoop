begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest.agent
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|agent
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
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|core
operator|.
name|ResourceConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|container
operator|.
name|WebApplication
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|container
operator|.
name|servlet
operator|.
name|ServletContainer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|container
operator|.
name|servlet
operator|.
name|WebConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|inject
operator|.
name|SingletonTypeInjectableProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|MapOperations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|agent
operator|.
name|AgentKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|WebAppApi
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|security
operator|.
name|SecurityUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|security
operator|.
name|SslSelectChannelConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|thread
operator|.
name|QueuedThreadPool
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
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|Provider
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
name|File
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
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AgentWebApp
specifier|public
class|class
name|AgentWebApp
implements|implements
name|Closeable
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AgentWebApp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|field|securedPort
specifier|private
name|int
name|securedPort
decl_stmt|;
DECL|field|agentServer
specifier|private
specifier|static
name|Server
name|agentServer
decl_stmt|;
DECL|field|BASE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|BASE_PATH
init|=
literal|"slideragent"
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|wsName
specifier|final
name|String
name|wsName
decl_stmt|;
DECL|field|application
specifier|final
name|WebAppApi
name|application
decl_stmt|;
DECL|field|port
name|int
name|port
decl_stmt|;
DECL|field|securedPort
name|int
name|securedPort
decl_stmt|;
DECL|field|configsMap
name|MapOperations
name|configsMap
decl_stmt|;
DECL|method|Builder (String name, String wsName, WebAppApi application)
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|wsName
parameter_list|,
name|WebAppApi
name|application
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|wsName
operator|=
name|wsName
expr_stmt|;
name|this
operator|.
name|application
operator|=
name|application
expr_stmt|;
block|}
DECL|method|withComponentConfig (MapOperations appMasterConfig)
specifier|public
name|Builder
name|withComponentConfig
parameter_list|(
name|MapOperations
name|appMasterConfig
parameter_list|)
block|{
name|this
operator|.
name|configsMap
operator|=
name|appMasterConfig
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withPort (int port)
specifier|public
name|Builder
name|withPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withSecuredPort (int securedPort)
specifier|public
name|Builder
name|withSecuredPort
parameter_list|(
name|int
name|securedPort
parameter_list|)
block|{
name|this
operator|.
name|securedPort
operator|=
name|securedPort
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|start ()
specifier|public
name|AgentWebApp
name|start
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|configsMap
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No SSL Configuration Available"
argument_list|)
throw|;
block|}
name|agentServer
operator|=
operator|new
name|Server
argument_list|()
expr_stmt|;
name|agentServer
operator|.
name|setThreadPool
argument_list|(
operator|new
name|QueuedThreadPool
argument_list|(
name|configsMap
operator|.
name|getOptionInt
argument_list|(
literal|"agent.threadpool.size.max"
argument_list|,
literal|25
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|agentServer
operator|.
name|setStopAtShutdown
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|agentServer
operator|.
name|setGracefulShutdown
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|SslSelectChannelConnector
name|ssl1WayConnector
init|=
name|createSSLConnector
argument_list|(
literal|false
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|SslSelectChannelConnector
name|ssl2WayConnector
init|=
name|createSSLConnector
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|configsMap
operator|.
name|getOption
argument_list|(
name|AgentKeys
operator|.
name|KEY_AGENT_TWO_WAY_SSL_ENABLED
argument_list|,
literal|"false"
argument_list|)
argument_list|)
argument_list|,
name|securedPort
argument_list|)
decl_stmt|;
name|agentServer
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|ssl1WayConnector
block|,
name|ssl2WayConnector
block|}
argument_list|)
expr_stmt|;
name|ServletHolder
name|agent
init|=
operator|new
name|ServletHolder
argument_list|(
operator|new
name|AgentServletContainer
argument_list|()
argument_list|)
decl_stmt|;
name|Context
name|agentRoot
init|=
operator|new
name|Context
argument_list|(
name|agentServer
argument_list|,
literal|"/"
argument_list|,
name|Context
operator|.
name|SESSIONS
argument_list|)
decl_stmt|;
name|agent
operator|.
name|setInitParameter
argument_list|(
literal|"com.sun.jersey.config.property.resourceConfigClass"
argument_list|,
literal|"com.sun.jersey.api.core.PackagesResourceConfig"
argument_list|)
expr_stmt|;
name|agent
operator|.
name|setInitParameter
argument_list|(
literal|"com.sun.jersey.config.property.packages"
argument_list|,
literal|"org.apache.slider.server.appmaster.web.rest.agent"
argument_list|)
expr_stmt|;
name|agent
operator|.
name|setInitParameter
argument_list|(
literal|"com.sun.jersey.api.json.POJOMappingFeature"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|//      agent.setInitParameter("com.sun.jersey.spi.container.ContainerRequestFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
comment|//      agent.setInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
comment|//      agent.setInitParameter("com.sun.jersey.config.feature.Trace", "true");
name|agentRoot
operator|.
name|addServlet
argument_list|(
name|agent
argument_list|,
literal|"/*"
argument_list|)
expr_stmt|;
try|try
block|{
name|openListeners
argument_list|()
expr_stmt|;
name|agentServer
operator|.
name|start
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
literal|"Unable to start agent server"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to start agent server"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to start agent server: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|AgentWebApp
name|webApp
init|=
operator|new
name|AgentWebApp
argument_list|()
decl_stmt|;
name|webApp
operator|.
name|setPort
argument_list|(
name|getConnectorPort
argument_list|(
name|agentServer
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|webApp
operator|.
name|setSecuredPort
argument_list|(
name|getConnectorPort
argument_list|(
name|agentServer
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|webApp
return|;
block|}
DECL|method|openListeners ()
specifier|private
name|void
name|openListeners
parameter_list|()
throws|throws
name|Exception
block|{
comment|// from HttpServer2.openListeners()
for|for
control|(
name|Connector
name|listener
range|:
name|agentServer
operator|.
name|getConnectors
argument_list|()
control|)
block|{
if|if
condition|(
name|listener
operator|.
name|getLocalPort
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// This listener is either started externally or has been bound
continue|continue;
block|}
name|int
name|port
init|=
name|listener
operator|.
name|getPort
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// jetty has a bug where you can't reopen a listener that previously
comment|// failed to open w/o issuing a close first, even if the port is changed
try|try
block|{
name|listener
operator|.
name|close
argument_list|()
expr_stmt|;
name|listener
operator|.
name|open
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Jetty bound to port "
operator|+
name|listener
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|BindException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|port
operator|==
literal|0
condition|)
block|{
name|BindException
name|be
init|=
operator|new
name|BindException
argument_list|(
literal|"Port in use: "
operator|+
name|listener
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|listener
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|be
operator|.
name|initCause
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|be
throw|;
block|}
block|}
comment|// try the next port number
name|listener
operator|.
name|setPort
argument_list|(
operator|++
name|port
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createSSLConnector (boolean needClientAuth, int port)
specifier|private
name|SslSelectChannelConnector
name|createSSLConnector
parameter_list|(
name|boolean
name|needClientAuth
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|SslSelectChannelConnector
name|sslConnector
init|=
operator|new
name|SslSelectChannelConnector
argument_list|()
decl_stmt|;
name|String
name|keystore
init|=
name|SecurityUtils
operator|.
name|getSecurityDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"keystore.p12"
decl_stmt|;
name|String
name|srvrCrtPass
init|=
name|SecurityUtils
operator|.
name|getKeystorePass
argument_list|()
decl_stmt|;
name|sslConnector
operator|.
name|setKeystore
argument_list|(
name|keystore
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setTruststore
argument_list|(
name|keystore
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setPassword
argument_list|(
name|srvrCrtPass
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setKeyPassword
argument_list|(
name|srvrCrtPass
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setTrustPassword
argument_list|(
name|srvrCrtPass
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setKeystoreType
argument_list|(
literal|"PKCS12"
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setTruststoreType
argument_list|(
literal|"PKCS12"
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setNeedClientAuth
argument_list|(
name|needClientAuth
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setAcceptors
argument_list|(
literal|2
argument_list|)
expr_stmt|;
return|return
name|sslConnector
return|;
block|}
annotation|@
name|Provider
DECL|class|WebAppApiProvider
specifier|public
class|class
name|WebAppApiProvider
extends|extends
name|SingletonTypeInjectableProvider
argument_list|<
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
argument_list|,
name|WebAppApi
argument_list|>
block|{
DECL|method|WebAppApiProvider ()
specifier|public
name|WebAppApiProvider
parameter_list|()
block|{
name|super
argument_list|(
name|WebAppApi
operator|.
name|class
argument_list|,
name|application
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|AgentServletContainer
specifier|public
class|class
name|AgentServletContainer
extends|extends
name|ServletContainer
block|{
DECL|method|AgentServletContainer ()
specifier|public
name|AgentServletContainer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure (WebConfig wc, ResourceConfig rc, WebApplication wa)
specifier|protected
name|void
name|configure
parameter_list|(
name|WebConfig
name|wc
parameter_list|,
name|ResourceConfig
name|rc
parameter_list|,
name|WebApplication
name|wa
parameter_list|)
block|{
name|super
operator|.
name|configure
argument_list|(
name|wc
argument_list|,
name|rc
argument_list|,
name|wa
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|singletons
init|=
name|rc
operator|.
name|getSingletons
argument_list|()
decl_stmt|;
name|singletons
operator|.
name|add
argument_list|(
operator|new
name|WebAppApiProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getConnectorPort (Server webServer, int index)
specifier|private
name|int
name|getConnectorPort
parameter_list|(
name|Server
name|webServer
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|index
operator|>=
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|>
name|webServer
operator|.
name|getConnectors
argument_list|()
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal connect index requested"
argument_list|)
throw|;
name|Connector
name|c
init|=
name|webServer
operator|.
name|getConnectors
argument_list|()
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|getLocalPort
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
comment|// The connector is not bounded
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The connector is not bound to a port"
argument_list|)
throw|;
block|}
return|return
name|c
operator|.
name|getLocalPort
argument_list|()
return|;
block|}
block|}
DECL|method|$for (String name, WebAppApi app, String wsPrefix)
specifier|public
specifier|static
name|Builder
name|$for
parameter_list|(
name|String
name|name
parameter_list|,
name|WebAppApi
name|app
parameter_list|,
name|String
name|wsPrefix
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|name
argument_list|,
name|wsPrefix
argument_list|,
name|app
argument_list|)
return|;
block|}
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
DECL|method|setPort (int port)
specifier|public
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
DECL|method|setSecuredPort (int securedPort)
specifier|public
name|void
name|setSecuredPort
parameter_list|(
name|int
name|securedPort
parameter_list|)
block|{
name|this
operator|.
name|securedPort
operator|=
name|securedPort
expr_stmt|;
block|}
DECL|method|getSecuredPort ()
specifier|public
name|int
name|getSecuredPort
parameter_list|()
block|{
return|return
name|securedPort
return|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|//need to stop server and reset injector
try|try
block|{
name|agentServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
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
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

