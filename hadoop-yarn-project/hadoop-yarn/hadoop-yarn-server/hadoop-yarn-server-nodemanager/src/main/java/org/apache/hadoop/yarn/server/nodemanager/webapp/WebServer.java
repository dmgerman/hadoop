begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|pajoin
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
name|security
operator|.
name|AuthenticationFilterInitializer
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
name|HttpCrossOriginFilterInitializer
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRuntimeException
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|Context
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|LocalDirsHandlerService
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|ResourceView
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
name|yarn
operator|.
name|server
operator|.
name|security
operator|.
name|ApplicationACLsManager
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
name|yarn
operator|.
name|webapp
operator|.
name|GenericExceptionHandler
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
name|yarn
operator|.
name|webapp
operator|.
name|WebApp
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
name|yarn
operator|.
name|webapp
operator|.
name|WebApps
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
name|yarn
operator|.
name|webapp
operator|.
name|YarnWebParams
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
name|yarn
operator|.
name|webapp
operator|.
name|util
operator|.
name|WebAppUtils
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
name|guice
operator|.
name|spi
operator|.
name|container
operator|.
name|servlet
operator|.
name|GuiceContainer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_class
DECL|class|WebServer
specifier|public
class|class
name|WebServer
extends|extends
name|AbstractService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|WebServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nmContext
specifier|private
specifier|final
name|Context
name|nmContext
decl_stmt|;
DECL|field|nmWebApp
specifier|private
specifier|final
name|NMWebApp
name|nmWebApp
decl_stmt|;
DECL|field|webApp
specifier|private
name|WebApp
name|webApp
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|method|WebServer (Context nmContext, ResourceView resourceView, ApplicationACLsManager aclsManager, LocalDirsHandlerService dirsHandler)
specifier|public
name|WebServer
parameter_list|(
name|Context
name|nmContext
parameter_list|,
name|ResourceView
name|resourceView
parameter_list|,
name|ApplicationACLsManager
name|aclsManager
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
name|super
argument_list|(
name|WebServer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|nmContext
operator|=
name|nmContext
expr_stmt|;
name|this
operator|.
name|nmWebApp
operator|=
operator|new
name|NMWebApp
argument_list|(
name|resourceView
argument_list|,
name|aclsManager
argument_list|,
name|dirsHandler
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
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|terminalParams
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|terminalParams
operator|.
name|put
argument_list|(
literal|"resourceBase"
argument_list|,
name|WebServer
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"TERMINAL"
argument_list|)
operator|.
name|toExternalForm
argument_list|()
argument_list|)
expr_stmt|;
name|terminalParams
operator|.
name|put
argument_list|(
literal|"dirAllowed"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|terminalParams
operator|.
name|put
argument_list|(
literal|"pathInfoOnly"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|bindAddress
init|=
name|WebAppUtils
operator|.
name|getWebAppBindURL
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|NM_BIND_HOST
argument_list|,
name|WebAppUtils
operator|.
name|getNMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|enableCors
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_ENABLE_CORS_FILTER
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_WEBAPP_ENABLE_CORS_FILTER
argument_list|)
decl_stmt|;
if|if
condition|(
name|enableCors
condition|)
block|{
name|getConfig
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|HttpCrossOriginFilterInitializer
operator|.
name|PREFIX
operator|+
name|HttpCrossOriginFilterInitializer
operator|.
name|ENABLED_SUFFIX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// Always load pseudo authentication filter to parse "user.name" in an URL
comment|// to identify a HTTP request's user.
name|boolean
name|hasHadoopAuthFilterInitializer
init|=
literal|false
decl_stmt|;
name|String
name|filterInitializerConfKey
init|=
literal|"hadoop.http.filter.initializers"
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|initializersClasses
init|=
name|conf
operator|.
name|getClasses
argument_list|(
name|filterInitializerConfKey
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|targets
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|initializersClasses
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|initializer
range|:
name|initializersClasses
control|)
block|{
if|if
condition|(
name|initializer
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|hasHadoopAuthFilterInitializer
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|targets
operator|.
name|add
argument_list|(
name|initializer
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|hasHadoopAuthFilterInitializer
condition|)
block|{
name|targets
operator|.
name|add
argument_list|(
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|filterInitializerConfKey
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|targets
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ContainerShellWebSocket
operator|.
name|init
argument_list|(
name|nmContext
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiating NMWebApp at "
operator|+
name|bindAddress
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|webApp
operator|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|"node"
argument_list|,
name|Context
operator|.
name|class
argument_list|,
name|this
operator|.
name|nmContext
argument_list|,
literal|"ws"
argument_list|)
operator|.
name|at
argument_list|(
name|bindAddress
argument_list|)
operator|.
name|withServlet
argument_list|(
literal|"ContainerShellWebSocket"
argument_list|,
literal|"/container/*"
argument_list|,
name|ContainerShellWebSocketServlet
operator|.
name|class
argument_list|,
name|params
argument_list|,
literal|false
argument_list|)
operator|.
name|withServlet
argument_list|(
literal|"Terminal"
argument_list|,
literal|"/terminal/*"
argument_list|,
name|TerminalServlet
operator|.
name|class
argument_list|,
name|terminalParams
argument_list|,
literal|false
argument_list|)
operator|.
name|with
argument_list|(
name|conf
argument_list|)
operator|.
name|withHttpSpnegoPrincipalKey
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_SPNEGO_USER_NAME_KEY
argument_list|)
operator|.
name|withHttpSpnegoKeytabKey
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_SPNEGO_KEYTAB_FILE_KEY
argument_list|)
operator|.
name|withCSRFProtection
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSRF_PREFIX
argument_list|)
operator|.
name|withXFSProtection
argument_list|(
name|YarnConfiguration
operator|.
name|NM_XFS_PREFIX
argument_list|)
operator|.
name|start
argument_list|(
name|this
operator|.
name|nmWebApp
argument_list|)
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|this
operator|.
name|webApp
operator|.
name|httpServer
argument_list|()
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"NMWebapps failed to start."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|this
operator|.
name|port
return|;
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
name|webApp
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stopping webapp"
argument_list|)
expr_stmt|;
name|this
operator|.
name|webApp
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
DECL|class|NMWebApp
specifier|public
specifier|static
class|class
name|NMWebApp
extends|extends
name|WebApp
implements|implements
name|YarnWebParams
block|{
DECL|field|resourceView
specifier|private
specifier|final
name|ResourceView
name|resourceView
decl_stmt|;
DECL|field|aclsManager
specifier|private
specifier|final
name|ApplicationACLsManager
name|aclsManager
decl_stmt|;
DECL|field|dirsHandler
specifier|private
specifier|final
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|method|NMWebApp (ResourceView resourceView, ApplicationACLsManager aclsManager, LocalDirsHandlerService dirsHandler)
specifier|public
name|NMWebApp
parameter_list|(
name|ResourceView
name|resourceView
parameter_list|,
name|ApplicationACLsManager
name|aclsManager
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
name|this
operator|.
name|resourceView
operator|=
name|resourceView
expr_stmt|;
name|this
operator|.
name|aclsManager
operator|=
name|aclsManager
expr_stmt|;
name|this
operator|.
name|dirsHandler
operator|=
name|dirsHandler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|bind
argument_list|(
name|NMWebServices
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GenericExceptionHandler
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|JAXBContextResolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ResourceView
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|this
operator|.
name|resourceView
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ApplicationACLsManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|this
operator|.
name|aclsManager
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|LocalDirsHandlerService
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|dirsHandler
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/"
argument_list|,
name|NMController
operator|.
name|class
argument_list|,
literal|"info"
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/node"
argument_list|,
name|NMController
operator|.
name|class
argument_list|,
literal|"node"
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/allApplications"
argument_list|,
name|NMController
operator|.
name|class
argument_list|,
literal|"allApplications"
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/allContainers"
argument_list|,
name|NMController
operator|.
name|class
argument_list|,
literal|"allContainers"
argument_list|)
expr_stmt|;
name|route
argument_list|(
name|pajoin
argument_list|(
literal|"/application"
argument_list|,
name|APPLICATION_ID
argument_list|)
argument_list|,
name|NMController
operator|.
name|class
argument_list|,
literal|"application"
argument_list|)
expr_stmt|;
name|route
argument_list|(
name|pajoin
argument_list|(
literal|"/container"
argument_list|,
name|CONTAINER_ID
argument_list|)
argument_list|,
name|NMController
operator|.
name|class
argument_list|,
literal|"container"
argument_list|)
expr_stmt|;
name|route
argument_list|(
name|pajoin
argument_list|(
literal|"/containerlogs"
argument_list|,
name|CONTAINER_ID
argument_list|,
name|APP_OWNER
argument_list|,
name|CONTAINER_LOG_TYPE
argument_list|)
argument_list|,
name|NMController
operator|.
name|class
argument_list|,
literal|"logs"
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/errors-and-warnings"
argument_list|,
name|NMController
operator|.
name|class
argument_list|,
literal|"errorsAndWarnings"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWebAppFilterClass ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|GuiceContainer
argument_list|>
name|getWebAppFilterClass
parameter_list|()
block|{
return|return
name|NMWebAppFilter
operator|.
name|class
return|;
block|}
block|}
block|}
end_class

end_unit

