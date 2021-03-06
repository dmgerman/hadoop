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
name|Path
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
name|PropertyConfigurator
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
name|File
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
name|URL
import|;
end_import

begin_comment
comment|/**  * Utility class to load KMS configuration files.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSConfiguration
specifier|public
class|class
name|KMSConfiguration
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
name|KMSConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|KMS_CONFIG_DIR
specifier|public
specifier|static
specifier|final
name|String
name|KMS_CONFIG_DIR
init|=
literal|"kms.config.dir"
decl_stmt|;
DECL|field|KMS_DEFAULT_XML
specifier|public
specifier|static
specifier|final
name|String
name|KMS_DEFAULT_XML
init|=
literal|"kms-default.xml"
decl_stmt|;
DECL|field|KMS_SITE_XML
specifier|public
specifier|static
specifier|final
name|String
name|KMS_SITE_XML
init|=
literal|"kms-site.xml"
decl_stmt|;
DECL|field|KMS_ACLS_XML
specifier|public
specifier|static
specifier|final
name|String
name|KMS_ACLS_XML
init|=
literal|"kms-acls.xml"
decl_stmt|;
DECL|field|CONFIG_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_PREFIX
init|=
literal|"hadoop.kms."
decl_stmt|;
DECL|field|KEY_ACL_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ACL_PREFIX
init|=
literal|"key.acl."
decl_stmt|;
DECL|field|KEY_ACL_PREFIX_REGEX
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ACL_PREFIX_REGEX
init|=
literal|"^key\\.acl\\..+"
decl_stmt|;
DECL|field|DEFAULT_KEY_ACL_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_KEY_ACL_PREFIX
init|=
literal|"default.key.acl."
decl_stmt|;
DECL|field|WHITELIST_KEY_ACL_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|WHITELIST_KEY_ACL_PREFIX
init|=
literal|"whitelist.key.acl."
decl_stmt|;
comment|// HTTP properties
DECL|field|HTTP_PORT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_PORT_KEY
init|=
literal|"hadoop.kms.http.port"
decl_stmt|;
DECL|field|HTTP_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HTTP_PORT_DEFAULT
init|=
literal|9600
decl_stmt|;
DECL|field|HTTP_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_HOST_KEY
init|=
literal|"hadoop.kms.http.host"
decl_stmt|;
DECL|field|HTTP_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|HTTP_ADMINS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_ADMINS_KEY
init|=
literal|"hadoop.kms.http.administrators"
decl_stmt|;
comment|// SSL properties
DECL|field|SSL_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SSL_ENABLED_KEY
init|=
literal|"hadoop.kms.ssl.enabled"
decl_stmt|;
DECL|field|SSL_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|SSL_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
comment|// Property to set the backing KeyProvider
DECL|field|KEY_PROVIDER_URI
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PROVIDER_URI
init|=
name|CONFIG_PREFIX
operator|+
literal|"key.provider.uri"
decl_stmt|;
comment|// Property to Enable/Disable Caching
DECL|field|KEY_CACHE_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CACHE_ENABLE
init|=
name|CONFIG_PREFIX
operator|+
literal|"cache.enable"
decl_stmt|;
comment|// Timeout for the Key and Metadata Cache
DECL|field|KEY_CACHE_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CACHE_TIMEOUT_KEY
init|=
name|CONFIG_PREFIX
operator|+
literal|"cache.timeout.ms"
decl_stmt|;
comment|// TImeout for the Current Key cache
DECL|field|CURR_KEY_CACHE_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CURR_KEY_CACHE_TIMEOUT_KEY
init|=
name|CONFIG_PREFIX
operator|+
literal|"current.key.cache.timeout.ms"
decl_stmt|;
comment|// Delay for Audit logs that need aggregation
DECL|field|KMS_AUDIT_AGGREGATION_WINDOW
specifier|public
specifier|static
specifier|final
name|String
name|KMS_AUDIT_AGGREGATION_WINDOW
init|=
name|CONFIG_PREFIX
operator|+
literal|"audit.aggregation.window.ms"
decl_stmt|;
comment|// Process name shown in metrics
DECL|field|METRICS_PROCESS_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_PROCESS_NAME_KEY
init|=
name|CONFIG_PREFIX
operator|+
literal|"metrics.process.name"
decl_stmt|;
DECL|field|METRICS_PROCESS_NAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_PROCESS_NAME_DEFAULT
init|=
literal|"KMS"
decl_stmt|;
comment|// Session id for metrics
DECL|field|METRICS_SESSION_ID_KEY
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_SESSION_ID_KEY
init|=
name|CONFIG_PREFIX
operator|+
literal|"metrics.session.id"
decl_stmt|;
comment|// KMS Audit logger classes to use
DECL|field|KMS_AUDIT_LOGGER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KMS_AUDIT_LOGGER_KEY
init|=
name|CONFIG_PREFIX
operator|+
literal|"audit.logger"
decl_stmt|;
DECL|field|KEY_CACHE_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|KEY_CACHE_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
comment|// 10 mins
DECL|field|KEY_CACHE_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|KEY_CACHE_TIMEOUT_DEFAULT
init|=
literal|10
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 30 secs
DECL|field|CURR_KEY_CACHE_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|CURR_KEY_CACHE_TIMEOUT_DEFAULT
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
comment|// 10 secs
DECL|field|KMS_AUDIT_AGGREGATION_WINDOW_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|KMS_AUDIT_AGGREGATION_WINDOW_DEFAULT
init|=
literal|10000
decl_stmt|;
comment|// Property to Enable/Disable per Key authorization
DECL|field|KEY_AUTHORIZATION_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|KEY_AUTHORIZATION_ENABLE
init|=
name|CONFIG_PREFIX
operator|+
literal|"key.authorization.enable"
decl_stmt|;
DECL|field|KEY_AUTHORIZATION_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|KEY_AUTHORIZATION_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|LOG4J_PROPERTIES
specifier|private
specifier|static
specifier|final
name|String
name|LOG4J_PROPERTIES
init|=
literal|"kms-log4j.properties"
decl_stmt|;
static|static
block|{
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
name|KMS_DEFAULT_XML
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
name|KMS_SITE_XML
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfiguration (boolean loadHadoopDefaults, String ... resources)
specifier|static
name|Configuration
name|getConfiguration
parameter_list|(
name|boolean
name|loadHadoopDefaults
parameter_list|,
name|String
modifier|...
name|resources
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|loadHadoopDefaults
argument_list|)
decl_stmt|;
name|String
name|confDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|KMS_CONFIG_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|confDir
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Path
name|confPath
init|=
operator|new
name|Path
argument_list|(
name|confDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|confPath
operator|.
name|isUriPathAbsolute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"System property '"
operator|+
name|KMS_CONFIG_DIR
operator|+
literal|"' must be an absolute path: "
operator|+
name|confDir
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|resource
range|:
name|resources
control|)
block|{
name|conf
operator|.
name|addResource
argument_list|(
operator|new
name|URL
argument_list|(
literal|"file://"
operator|+
operator|new
name|Path
argument_list|(
name|confDir
argument_list|,
name|resource
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|resource
range|:
name|resources
control|)
block|{
name|conf
operator|.
name|addResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|conf
return|;
block|}
DECL|method|getKMSConf ()
specifier|public
specifier|static
name|Configuration
name|getKMSConf
parameter_list|()
block|{
return|return
name|getConfiguration
argument_list|(
literal|true
argument_list|,
literal|"core-site.xml"
argument_list|,
name|KMS_SITE_XML
argument_list|)
return|;
block|}
DECL|method|getACLsConf ()
specifier|public
specifier|static
name|Configuration
name|getACLsConf
parameter_list|()
block|{
return|return
name|getConfiguration
argument_list|(
literal|false
argument_list|,
name|KMS_ACLS_XML
argument_list|)
return|;
block|}
DECL|method|isACLsFileNewer (long time)
specifier|public
specifier|static
name|boolean
name|isACLsFileNewer
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|boolean
name|newer
init|=
literal|false
decl_stmt|;
name|String
name|confDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|KMS_CONFIG_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|confDir
operator|!=
literal|null
condition|)
block|{
name|Path
name|confPath
init|=
operator|new
name|Path
argument_list|(
name|confDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|confPath
operator|.
name|isUriPathAbsolute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"System property '"
operator|+
name|KMS_CONFIG_DIR
operator|+
literal|"' must be an absolute path: "
operator|+
name|confDir
argument_list|)
throw|;
block|}
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
name|KMS_ACLS_XML
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Checking file {}, modification time is {}, last reload time is"
operator|+
literal|" {}"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|,
name|f
operator|.
name|lastModified
argument_list|()
argument_list|,
name|time
argument_list|)
expr_stmt|;
comment|// at least 100ms newer than time, we do this to ensure the file
comment|// has been properly closed/flushed
name|newer
operator|=
name|f
operator|.
name|lastModified
argument_list|()
operator|-
name|time
operator|>
literal|100
expr_stmt|;
block|}
return|return
name|newer
return|;
block|}
DECL|method|initLogging ()
specifier|public
specifier|static
name|void
name|initLogging
parameter_list|()
block|{
name|String
name|confDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|KMS_CONFIG_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|confDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"System property '"
operator|+
name|KMSConfiguration
operator|.
name|KMS_CONFIG_DIR
operator|+
literal|"' not defined"
argument_list|)
throw|;
block|}
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"log4j.configuration"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"log4j.defaultInitOverride"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|boolean
name|fromClasspath
init|=
literal|true
decl_stmt|;
name|File
name|log4jConf
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
name|LOG4J_PROPERTIES
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|log4jConf
operator|.
name|exists
argument_list|()
condition|)
block|{
name|PropertyConfigurator
operator|.
name|configureAndWatch
argument_list|(
name|log4jConf
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|fromClasspath
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|ClassLoader
name|cl
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|URL
name|log4jUrl
init|=
name|cl
operator|.
name|getResource
argument_list|(
name|LOG4J_PROPERTIES
argument_list|)
decl_stmt|;
if|if
condition|(
name|log4jUrl
operator|!=
literal|null
condition|)
block|{
name|PropertyConfigurator
operator|.
name|configure
argument_list|(
name|log4jUrl
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"KMS log starting"
argument_list|)
expr_stmt|;
if|if
condition|(
name|fromClasspath
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Log4j configuration file '{}' not found"
argument_list|,
name|LOG4J_PROPERTIES
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Logging with INFO level to standard output"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

