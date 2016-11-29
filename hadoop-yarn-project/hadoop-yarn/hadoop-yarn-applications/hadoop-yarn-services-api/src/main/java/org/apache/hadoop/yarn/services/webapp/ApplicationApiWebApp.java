begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.services.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|services
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
name|services
operator|.
name|utils
operator|.
name|RestApiConstants
operator|.
name|*
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
name|InetAddress
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
name|Arrays
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
name|lang
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|services
operator|.
name|api
operator|.
name|impl
operator|.
name|ApplicationApiService
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

begin_comment
comment|/**  * This class launches the web application using Hadoop HttpServer2 (which uses  * an embedded Jetty container). This is the entry point to your application.  * The Java command used to launch this app should call the main method.  */
end_comment

begin_class
DECL|class|ApplicationApiWebApp
specifier|public
class|class
name|ApplicationApiWebApp
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
name|ApplicationApiWebApp
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
DECL|field|applicationApiServer
specifier|private
name|HttpServer2
name|applicationApiServer
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
name|ApplicationApiWebApp
name|apiWebApp
init|=
operator|new
name|ApplicationApiWebApp
argument_list|()
decl_stmt|;
try|try
block|{
name|apiWebApp
operator|.
name|startWebApp
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|apiWebApp
operator|!=
literal|null
condition|)
block|{
name|apiWebApp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|ApplicationApiWebApp ()
specifier|public
name|ApplicationApiWebApp
parameter_list|()
block|{
name|super
argument_list|(
name|ApplicationApiWebApp
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
name|applicationApiServer
operator|!=
literal|null
condition|)
block|{
name|applicationApiServer
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
DECL|method|startWebApp ()
specifier|protected
name|void
name|startWebApp
parameter_list|()
throws|throws
name|IOException
block|{
comment|// The port that we should run on can be set into an environment variable
comment|// Look for that variable and default to 9191 if it isn't there.
name|String
name|webPort
init|=
name|System
operator|.
name|getenv
argument_list|(
name|PROPERTY_REST_SERVICE_PORT
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|webPort
argument_list|)
condition|)
block|{
name|webPort
operator|=
literal|"9191"
expr_stmt|;
block|}
name|String
name|webHost
init|=
name|System
operator|.
name|getenv
argument_list|(
name|PROPERTY_REST_SERVICE_HOST
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|webHost
argument_list|)
condition|)
block|{
name|webHost
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"YARN native services REST API running on host {} and port {}"
argument_list|,
name|webHost
argument_list|,
name|webPort
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Configuration = {}"
argument_list|,
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|applicationApiServer
operator|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"services-rest-api"
argument_list|)
operator|.
name|addEndpoint
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"http://"
operator|+
name|webHost
operator|+
literal|":"
operator|+
name|webPort
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|String
name|apiPackages
init|=
name|ApplicationApiService
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
name|applicationApiServer
operator|.
name|addJerseyResourcePackage
argument_list|(
name|apiPackages
argument_list|,
name|CONTEXT_ROOT
operator|+
literal|"/*"
argument_list|)
expr_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Application starting up. Logging start..."
argument_list|)
expr_stmt|;
name|applicationApiServer
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
name|applicationApiServer
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
name|applicationApiServer
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
name|Arrays
operator|.
name|asList
argument_list|(
name|applicationApiServer
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
name|Arrays
operator|.
name|asList
argument_list|(
name|applicationApiServer
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
name|Arrays
operator|.
name|asList
argument_list|(
name|applicationApiServer
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

