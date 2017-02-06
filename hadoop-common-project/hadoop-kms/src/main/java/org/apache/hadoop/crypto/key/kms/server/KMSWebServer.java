begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
package|;
end_package

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
name|MalformedURLException
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
name|URL
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
name|classification
operator|.
name|InterfaceAudience
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
name|ConfigurationWithLogging
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
comment|/**  * The KMS web server.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSWebServer
specifier|public
class|class
name|KMSWebServer
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
name|KMSWebServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"kms"
decl_stmt|;
DECL|field|SERVLET_PATH
specifier|private
specifier|static
specifier|final
name|String
name|SERVLET_PATH
init|=
literal|"/kms"
decl_stmt|;
DECL|field|httpServer
specifier|private
specifier|final
name|HttpServer2
name|httpServer
decl_stmt|;
DECL|field|scheme
specifier|private
specifier|final
name|String
name|scheme
decl_stmt|;
DECL|method|KMSWebServer (Configuration conf, Configuration sslConf)
name|KMSWebServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Configuration
name|sslConf
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Override configuration with deprecated environment variables.
name|deprecateEnv
argument_list|(
literal|"KMS_TEMP"
argument_list|,
name|conf
argument_list|,
name|HttpServer2
operator|.
name|HTTP_TEMP_DIR_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|KMS_SITE_XML
argument_list|)
expr_stmt|;
name|deprecateEnv
argument_list|(
literal|"KMS_HTTP_PORT"
argument_list|,
name|conf
argument_list|,
name|KMSConfiguration
operator|.
name|HTTP_PORT_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|KMS_SITE_XML
argument_list|)
expr_stmt|;
name|deprecateEnv
argument_list|(
literal|"KMS_MAX_THREADS"
argument_list|,
name|conf
argument_list|,
name|HttpServer2
operator|.
name|HTTP_MAX_THREADS_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|KMS_SITE_XML
argument_list|)
expr_stmt|;
name|deprecateEnv
argument_list|(
literal|"KMS_MAX_HTTP_HEADER_SIZE"
argument_list|,
name|conf
argument_list|,
name|HttpServer2
operator|.
name|HTTP_MAX_REQUEST_HEADER_SIZE_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|KMS_SITE_XML
argument_list|)
expr_stmt|;
name|deprecateEnv
argument_list|(
literal|"KMS_MAX_HTTP_HEADER_SIZE"
argument_list|,
name|conf
argument_list|,
name|HttpServer2
operator|.
name|HTTP_MAX_RESPONSE_HEADER_SIZE_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|KMS_SITE_XML
argument_list|)
expr_stmt|;
name|deprecateEnv
argument_list|(
literal|"KMS_SSL_ENABLED"
argument_list|,
name|conf
argument_list|,
name|KMSConfiguration
operator|.
name|SSL_ENABLED_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|KMS_SITE_XML
argument_list|)
expr_stmt|;
name|deprecateEnv
argument_list|(
literal|"KMS_SSL_KEYSTORE_FILE"
argument_list|,
name|sslConf
argument_list|,
name|SSLFactory
operator|.
name|SSL_SERVER_KEYSTORE_LOCATION
argument_list|,
name|SSLFactory
operator|.
name|SSL_SERVER_CONF_DEFAULT
argument_list|)
expr_stmt|;
name|deprecateEnv
argument_list|(
literal|"KMS_SSL_KEYSTORE_PASS"
argument_list|,
name|sslConf
argument_list|,
name|SSLFactory
operator|.
name|SSL_SERVER_KEYSTORE_PASSWORD
argument_list|,
name|SSLFactory
operator|.
name|SSL_SERVER_CONF_DEFAULT
argument_list|)
expr_stmt|;
name|boolean
name|sslEnabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|KMSConfiguration
operator|.
name|SSL_ENABLED_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|SSL_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
name|scheme
operator|=
name|sslEnabled
condition|?
name|HttpServer2
operator|.
name|HTTPS_SCHEME
else|:
name|HttpServer2
operator|.
name|HTTP_SCHEME
expr_stmt|;
name|String
name|host
init|=
name|conf
operator|.
name|get
argument_list|(
name|KMSConfiguration
operator|.
name|HTTP_HOST_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|HTTP_HOST_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|KMSConfiguration
operator|.
name|HTTP_PORT_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|HTTP_PORT_DEFAULT
argument_list|)
decl_stmt|;
name|URI
name|endpoint
init|=
operator|new
name|URI
argument_list|(
name|scheme
argument_list|,
literal|null
argument_list|,
name|host
argument_list|,
name|port
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|httpServer
operator|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
name|NAME
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setSSLConf
argument_list|(
name|sslConf
argument_list|)
operator|.
name|authFilterConfigurationPrefix
argument_list|(
name|KMSAuthenticationFilter
operator|.
name|CONFIG_PREFIX
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
name|KMSConfiguration
operator|.
name|HTTP_ADMINS_KEY
argument_list|,
literal|" "
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addEndpoint
argument_list|(
name|endpoint
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * Load the deprecated environment variable into the configuration.    *    * @param varName the environment variable name    * @param conf the configuration    * @param propName the configuration property name    * @param confFile the configuration file name    */
DECL|method|deprecateEnv (String varName, Configuration conf, String propName, String confFile)
specifier|private
specifier|static
name|void
name|deprecateEnv
parameter_list|(
name|String
name|varName
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|propName
parameter_list|,
name|String
name|confFile
parameter_list|)
block|{
name|String
name|value
init|=
name|System
operator|.
name|getenv
argument_list|(
name|varName
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|propValue
init|=
name|conf
operator|.
name|get
argument_list|(
name|propName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Environment variable {} = '{}' is deprecated and overriding"
operator|+
literal|" property {} = '{}', please set the property in {} instead."
argument_list|,
name|varName
argument_list|,
name|value
argument_list|,
name|propName
argument_list|,
name|propValue
argument_list|,
name|confFile
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|propName
argument_list|,
name|value
argument_list|,
literal|"environment variable "
operator|+
name|varName
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|isRunning ()
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|httpServer
operator|.
name|isAlive
argument_list|()
return|;
block|}
DECL|method|join ()
specifier|public
name|void
name|join
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|httpServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|httpServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getKMSUrl ()
specifier|public
name|URL
name|getKMSUrl
parameter_list|()
block|{
name|InetSocketAddress
name|addr
init|=
name|httpServer
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|addr
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
operator|new
name|URL
argument_list|(
name|scheme
argument_list|,
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|SERVLET_PATH
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"It should never happen: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
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
name|Exception
block|{
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|KMSWebServer
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|ConfigurationWithLogging
argument_list|(
name|KMSConfiguration
operator|.
name|getKMSConf
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|sslConf
init|=
operator|new
name|ConfigurationWithLogging
argument_list|(
name|SSLFactory
operator|.
name|readSSLConfiguration
argument_list|(
name|conf
argument_list|,
name|SSLFactory
operator|.
name|Mode
operator|.
name|SERVER
argument_list|)
argument_list|)
decl_stmt|;
name|KMSWebServer
name|kmsWebServer
init|=
operator|new
name|KMSWebServer
argument_list|(
name|conf
argument_list|,
name|sslConf
argument_list|)
decl_stmt|;
name|kmsWebServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|kmsWebServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

