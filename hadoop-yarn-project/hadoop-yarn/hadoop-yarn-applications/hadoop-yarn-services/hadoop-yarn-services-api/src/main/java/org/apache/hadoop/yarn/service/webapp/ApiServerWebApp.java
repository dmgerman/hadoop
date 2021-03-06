begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|webapp
package|;
end_package

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
name|SecurityUtil
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
name|YarnJacksonJaxbJsonProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|webapp
operator|.
name|Configuration
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
name|Collections
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
import|import static
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
operator|.
name|RM_WEBAPP_SPNEGO_KEYTAB_FILE_KEY
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|RM_WEBAPP_SPNEGO_USER_NAME_KEY
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
name|yarn
operator|.
name|service
operator|.
name|conf
operator|.
name|YarnServiceConf
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This class launches the web service using Hadoop HttpServer2 (which uses  * an embedded Jetty container). This is the entry point to your service.  * The Java command used to launch this app should call the main method.  */
end_comment

begin_class
DECL|class|ApiServerWebApp
specifier|public
class|class
name|ApiServerWebApp
extends|extends
name|AbstractService
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ApiServerWebApp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SEP
specifier|private
specifier|static
specifier|final
name|String
name|SEP
init|=
literal|";"
decl_stmt|;
comment|// REST API server for YARN native services
DECL|field|apiServer
specifier|private
name|HttpServer2
name|apiServer
decl_stmt|;
DECL|field|bindAddress
specifier|private
name|InetSocketAddress
name|bindAddress
decl_stmt|;
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|ApiServerWebApp
name|apiWebApp
init|=
operator|new
name|ApiServerWebApp
argument_list|()
decl_stmt|;
try|try
block|{
name|apiWebApp
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|apiWebApp
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Got exception starting"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|apiWebApp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|ApiServerWebApp ()
specifier|public
name|ApiServerWebApp
parameter_list|()
block|{
name|super
argument_list|(
name|ApiServerWebApp
operator|.
name|class
operator|.
name|getName
argument_list|()
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
name|bindAddress
operator|=
name|getConfig
argument_list|()
operator|.
name|getSocketAddr
argument_list|(
name|API_SERVER_ADDRESS
argument_list|,
name|DEFAULT_API_SERVER_ADDRESS
argument_list|,
name|DEFAULT_API_SERVER_PORT
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"YARN API server running on "
operator|+
name|bindAddress
argument_list|)
expr_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|doSecureLogin
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|startWebApp
argument_list|()
expr_stmt|;
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
name|apiServer
operator|!=
literal|null
condition|)
block|{
name|apiServer
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
DECL|method|doSecureLogin (org.apache.hadoop.conf.Configuration conf)
specifier|private
name|void
name|doSecureLogin
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|RM_KEYTAB
argument_list|,
name|YarnConfiguration
operator|.
name|RM_PRINCIPAL
argument_list|,
name|bindAddress
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|addFilters
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|addFilters (org.apache.hadoop.conf.Configuration conf)
specifier|private
name|void
name|addFilters
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
name|conf
parameter_list|)
block|{
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
block|}
DECL|method|startWebApp ()
specifier|private
name|void
name|startWebApp
parameter_list|()
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
literal|"http://"
operator|+
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|bindAddress
argument_list|)
argument_list|)
decl_stmt|;
name|apiServer
operator|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"api-server"
argument_list|)
operator|.
name|setConf
argument_list|(
name|getConfig
argument_list|()
argument_list|)
operator|.
name|setSecurityEnabled
argument_list|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|)
operator|.
name|setUsernameConfKey
argument_list|(
name|RM_WEBAPP_SPNEGO_USER_NAME_KEY
argument_list|)
operator|.
name|setKeytabConfKey
argument_list|(
name|RM_WEBAPP_SPNEGO_KEYTAB_FILE_KEY
argument_list|)
operator|.
name|addEndpoint
argument_list|(
name|uri
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|String
name|apiPackages
init|=
name|ApiServer
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
name|SEP
operator|+
name|GenericExceptionHandler
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
name|SEP
operator|+
name|YarnJacksonJaxbJsonProvider
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|apiServer
operator|.
name|addJerseyResourcePackage
argument_list|(
name|apiPackages
argument_list|,
literal|"/*"
argument_list|)
expr_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Service starting up. Logging start..."
argument_list|)
expr_stmt|;
name|apiServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Server status = {}"
argument_list|,
name|apiServer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Configuration
name|conf
range|:
name|apiServer
operator|.
name|getWebAppContext
argument_list|()
operator|.
name|getConfigurations
argument_list|()
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Configurations = {}"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Context Path = {}"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|apiServer
operator|.
name|getWebAppContext
argument_list|()
operator|.
name|getContextPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"ResourceBase = {}"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|apiServer
operator|.
name|getWebAppContext
argument_list|()
operator|.
name|getResourceBase
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"War = {}"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|apiServer
operator|.
name|getWebAppContext
argument_list|()
operator|.
name|getWar
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Hadoop HttpServer2 App **failed**"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
end_class

end_unit

