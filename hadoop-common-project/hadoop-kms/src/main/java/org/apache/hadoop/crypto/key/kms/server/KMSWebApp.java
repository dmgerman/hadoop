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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|JmxReporter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Meter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
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
name|crypto
operator|.
name|key
operator|.
name|CachingKeyProvider
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderCryptoExtension
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderFactory
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
name|util
operator|.
name|VersionInfo
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
name|org
operator|.
name|slf4j
operator|.
name|bridge
operator|.
name|SLF4JBridgeHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContextEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContextListener
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSWebApp
specifier|public
class|class
name|KMSWebApp
implements|implements
name|ServletContextListener
block|{
DECL|field|LOG4J_PROPERTIES
specifier|private
specifier|static
specifier|final
name|String
name|LOG4J_PROPERTIES
init|=
literal|"kms-log4j.properties"
decl_stmt|;
DECL|field|METRICS_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|METRICS_PREFIX
init|=
literal|"hadoop.kms."
decl_stmt|;
DECL|field|ADMIN_CALLS_METER
specifier|private
specifier|static
specifier|final
name|String
name|ADMIN_CALLS_METER
init|=
name|METRICS_PREFIX
operator|+
literal|"admin.calls.meter"
decl_stmt|;
DECL|field|KEY_CALLS_METER
specifier|private
specifier|static
specifier|final
name|String
name|KEY_CALLS_METER
init|=
name|METRICS_PREFIX
operator|+
literal|"key.calls.meter"
decl_stmt|;
DECL|field|INVALID_CALLS_METER
specifier|private
specifier|static
specifier|final
name|String
name|INVALID_CALLS_METER
init|=
name|METRICS_PREFIX
operator|+
literal|"invalid.calls.meter"
decl_stmt|;
DECL|field|UNAUTHORIZED_CALLS_METER
specifier|private
specifier|static
specifier|final
name|String
name|UNAUTHORIZED_CALLS_METER
init|=
name|METRICS_PREFIX
operator|+
literal|"unauthorized.calls.meter"
decl_stmt|;
DECL|field|UNAUTHENTICATED_CALLS_METER
specifier|private
specifier|static
specifier|final
name|String
name|UNAUTHENTICATED_CALLS_METER
init|=
name|METRICS_PREFIX
operator|+
literal|"unauthenticated.calls.meter"
decl_stmt|;
DECL|field|GENERATE_EEK_METER
specifier|private
specifier|static
specifier|final
name|String
name|GENERATE_EEK_METER
init|=
name|METRICS_PREFIX
operator|+
literal|"generate_eek.calls.meter"
decl_stmt|;
DECL|field|DECRYPT_EEK_METER
specifier|private
specifier|static
specifier|final
name|String
name|DECRYPT_EEK_METER
init|=
name|METRICS_PREFIX
operator|+
literal|"decrypt_eek.calls.meter"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
decl_stmt|;
DECL|field|metricRegistry
specifier|private
specifier|static
name|MetricRegistry
name|metricRegistry
decl_stmt|;
DECL|field|jmxReporter
specifier|private
name|JmxReporter
name|jmxReporter
decl_stmt|;
DECL|field|kmsConf
specifier|private
specifier|static
name|Configuration
name|kmsConf
decl_stmt|;
DECL|field|kmsAcls
specifier|private
specifier|static
name|KMSACLs
name|kmsAcls
decl_stmt|;
DECL|field|adminCallsMeter
specifier|private
specifier|static
name|Meter
name|adminCallsMeter
decl_stmt|;
DECL|field|keyCallsMeter
specifier|private
specifier|static
name|Meter
name|keyCallsMeter
decl_stmt|;
DECL|field|unauthorizedCallsMeter
specifier|private
specifier|static
name|Meter
name|unauthorizedCallsMeter
decl_stmt|;
DECL|field|unauthenticatedCallsMeter
specifier|private
specifier|static
name|Meter
name|unauthenticatedCallsMeter
decl_stmt|;
DECL|field|decryptEEKCallsMeter
specifier|private
specifier|static
name|Meter
name|decryptEEKCallsMeter
decl_stmt|;
DECL|field|generateEEKCallsMeter
specifier|private
specifier|static
name|Meter
name|generateEEKCallsMeter
decl_stmt|;
DECL|field|invalidCallsMeter
specifier|private
specifier|static
name|Meter
name|invalidCallsMeter
decl_stmt|;
DECL|field|kmsAudit
specifier|private
specifier|static
name|KMSAudit
name|kmsAudit
decl_stmt|;
DECL|field|keyProviderCryptoExtension
specifier|private
specifier|static
name|KeyProviderCryptoExtension
name|keyProviderCryptoExtension
decl_stmt|;
static|static
block|{
name|SLF4JBridgeHandler
operator|.
name|removeHandlersForRootLogger
argument_list|()
expr_stmt|;
name|SLF4JBridgeHandler
operator|.
name|install
argument_list|()
expr_stmt|;
block|}
DECL|method|initLogging (String confDir)
specifier|private
name|void
name|initLogging
parameter_list|(
name|String
name|confDir
parameter_list|)
block|{
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
operator|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KMSWebApp
operator|.
name|class
argument_list|)
expr_stmt|;
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
else|else
block|{
name|LOG
operator|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KMSWebApp
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|contextInitialized (ServletContextEvent sce)
specifier|public
name|void
name|contextInitialized
parameter_list|(
name|ServletContextEvent
name|sce
parameter_list|)
block|{
try|try
block|{
name|String
name|confDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|KMSConfiguration
operator|.
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
name|kmsConf
operator|=
name|KMSConfiguration
operator|.
name|getKMSConf
argument_list|()
expr_stmt|;
name|initLogging
argument_list|(
name|confDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"-------------------------------------------------------------"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"  Java runtime version : {}"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.runtime.version"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"  KMS Hadoop Version: "
operator|+
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"-------------------------------------------------------------"
argument_list|)
expr_stmt|;
name|kmsAcls
operator|=
operator|new
name|KMSACLs
argument_list|()
expr_stmt|;
name|kmsAcls
operator|.
name|startReloader
argument_list|()
expr_stmt|;
name|metricRegistry
operator|=
operator|new
name|MetricRegistry
argument_list|()
expr_stmt|;
name|jmxReporter
operator|=
name|JmxReporter
operator|.
name|forRegistry
argument_list|(
name|metricRegistry
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|jmxReporter
operator|.
name|start
argument_list|()
expr_stmt|;
name|generateEEKCallsMeter
operator|=
name|metricRegistry
operator|.
name|register
argument_list|(
name|GENERATE_EEK_METER
argument_list|,
operator|new
name|Meter
argument_list|()
argument_list|)
expr_stmt|;
name|decryptEEKCallsMeter
operator|=
name|metricRegistry
operator|.
name|register
argument_list|(
name|DECRYPT_EEK_METER
argument_list|,
operator|new
name|Meter
argument_list|()
argument_list|)
expr_stmt|;
name|adminCallsMeter
operator|=
name|metricRegistry
operator|.
name|register
argument_list|(
name|ADMIN_CALLS_METER
argument_list|,
operator|new
name|Meter
argument_list|()
argument_list|)
expr_stmt|;
name|keyCallsMeter
operator|=
name|metricRegistry
operator|.
name|register
argument_list|(
name|KEY_CALLS_METER
argument_list|,
operator|new
name|Meter
argument_list|()
argument_list|)
expr_stmt|;
name|invalidCallsMeter
operator|=
name|metricRegistry
operator|.
name|register
argument_list|(
name|INVALID_CALLS_METER
argument_list|,
operator|new
name|Meter
argument_list|()
argument_list|)
expr_stmt|;
name|unauthorizedCallsMeter
operator|=
name|metricRegistry
operator|.
name|register
argument_list|(
name|UNAUTHORIZED_CALLS_METER
argument_list|,
operator|new
name|Meter
argument_list|()
argument_list|)
expr_stmt|;
name|unauthenticatedCallsMeter
operator|=
name|metricRegistry
operator|.
name|register
argument_list|(
name|UNAUTHENTICATED_CALLS_METER
argument_list|,
operator|new
name|Meter
argument_list|()
argument_list|)
expr_stmt|;
name|kmsAudit
operator|=
operator|new
name|KMSAudit
argument_list|(
name|kmsConf
operator|.
name|getLong
argument_list|(
name|KMSConfiguration
operator|.
name|KMS_AUDIT_AGGREGATION_WINDOW
argument_list|,
name|KMSConfiguration
operator|.
name|KMS_AUDIT_AGGREGATION_WINDOW_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// this is required for the the JMXJsonServlet to work properly.
comment|// the JMXJsonServlet is behind the authentication filter,
comment|// thus the '*' ACL.
name|sce
operator|.
name|getServletContext
argument_list|()
operator|.
name|setAttribute
argument_list|(
name|HttpServer2
operator|.
name|CONF_CONTEXT_ATTRIBUTE
argument_list|,
name|kmsConf
argument_list|)
expr_stmt|;
name|sce
operator|.
name|getServletContext
argument_list|()
operator|.
name|setAttribute
argument_list|(
name|HttpServer2
operator|.
name|ADMINS_ACL
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|AccessControlList
operator|.
name|WILDCARD_ACL_VALUE
argument_list|)
argument_list|)
expr_stmt|;
comment|// intializing the KeyProvider
name|String
name|providerString
init|=
name|kmsConf
operator|.
name|get
argument_list|(
name|KMSConfiguration
operator|.
name|KEY_PROVIDER_URI
argument_list|)
decl_stmt|;
if|if
condition|(
name|providerString
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No KeyProvider has been defined"
argument_list|)
throw|;
block|}
name|KeyProvider
name|keyProvider
init|=
name|KeyProviderFactory
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
name|providerString
argument_list|)
argument_list|,
name|kmsConf
argument_list|)
decl_stmt|;
if|if
condition|(
name|kmsConf
operator|.
name|getBoolean
argument_list|(
name|KMSConfiguration
operator|.
name|KEY_CACHE_ENABLE
argument_list|,
name|KMSConfiguration
operator|.
name|KEY_CACHE_ENABLE_DEFAULT
argument_list|)
condition|)
block|{
name|long
name|keyTimeOutMillis
init|=
name|kmsConf
operator|.
name|getLong
argument_list|(
name|KMSConfiguration
operator|.
name|KEY_CACHE_TIMEOUT_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|KEY_CACHE_TIMEOUT_DEFAULT
argument_list|)
decl_stmt|;
name|long
name|currKeyTimeOutMillis
init|=
name|kmsConf
operator|.
name|getLong
argument_list|(
name|KMSConfiguration
operator|.
name|CURR_KEY_CACHE_TIMEOUT_KEY
argument_list|,
name|KMSConfiguration
operator|.
name|CURR_KEY_CACHE_TIMEOUT_DEFAULT
argument_list|)
decl_stmt|;
name|keyProvider
operator|=
operator|new
name|CachingKeyProvider
argument_list|(
name|keyProvider
argument_list|,
name|keyTimeOutMillis
argument_list|,
name|currKeyTimeOutMillis
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialized KeyProvider "
operator|+
name|keyProvider
argument_list|)
expr_stmt|;
name|keyProviderCryptoExtension
operator|=
name|KeyProviderCryptoExtension
operator|.
name|createKeyProviderCryptoExtension
argument_list|(
name|keyProvider
argument_list|)
expr_stmt|;
name|keyProviderCryptoExtension
operator|=
operator|new
name|EagerKeyGeneratorKeyProviderCryptoExtension
argument_list|(
name|kmsConf
argument_list|,
name|keyProviderCryptoExtension
argument_list|)
expr_stmt|;
if|if
condition|(
name|kmsConf
operator|.
name|getBoolean
argument_list|(
name|KMSConfiguration
operator|.
name|KEY_AUTHORIZATION_ENABLE
argument_list|,
name|KMSConfiguration
operator|.
name|KEY_AUTHORIZATION_ENABLE_DEFAULT
argument_list|)
condition|)
block|{
name|keyProviderCryptoExtension
operator|=
operator|new
name|KeyAuthorizationKeyProvider
argument_list|(
name|keyProviderCryptoExtension
argument_list|,
name|kmsAcls
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialized KeyProviderCryptoExtension "
operator|+
name|keyProviderCryptoExtension
argument_list|)
expr_stmt|;
specifier|final
name|int
name|defaultBitlength
init|=
name|kmsConf
operator|.
name|getInt
argument_list|(
name|KeyProvider
operator|.
name|DEFAULT_BITLENGTH_NAME
argument_list|,
name|KeyProvider
operator|.
name|DEFAULT_BITLENGTH
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Default key bitlength is {}"
argument_list|,
name|defaultBitlength
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"KMS Started"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: Hadoop KMS could not be started"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"REASON: "
operator|+
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stacktrace:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------------------------------------"
argument_list|)
expr_stmt|;
name|ex
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------------------------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|contextDestroyed (ServletContextEvent sce)
specifier|public
name|void
name|contextDestroyed
parameter_list|(
name|ServletContextEvent
name|sce
parameter_list|)
block|{
name|kmsAudit
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|kmsAcls
operator|.
name|stopReloader
argument_list|()
expr_stmt|;
name|jmxReporter
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jmxReporter
operator|.
name|close
argument_list|()
expr_stmt|;
name|metricRegistry
operator|=
literal|null
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"KMS Stopped"
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfiguration ()
specifier|public
specifier|static
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|(
name|kmsConf
argument_list|)
return|;
block|}
DECL|method|getACLs ()
specifier|public
specifier|static
name|KMSACLs
name|getACLs
parameter_list|()
block|{
return|return
name|kmsAcls
return|;
block|}
DECL|method|getAdminCallsMeter ()
specifier|public
specifier|static
name|Meter
name|getAdminCallsMeter
parameter_list|()
block|{
return|return
name|adminCallsMeter
return|;
block|}
DECL|method|getKeyCallsMeter ()
specifier|public
specifier|static
name|Meter
name|getKeyCallsMeter
parameter_list|()
block|{
return|return
name|keyCallsMeter
return|;
block|}
DECL|method|getInvalidCallsMeter ()
specifier|public
specifier|static
name|Meter
name|getInvalidCallsMeter
parameter_list|()
block|{
return|return
name|invalidCallsMeter
return|;
block|}
DECL|method|getGenerateEEKCallsMeter ()
specifier|public
specifier|static
name|Meter
name|getGenerateEEKCallsMeter
parameter_list|()
block|{
return|return
name|generateEEKCallsMeter
return|;
block|}
DECL|method|getDecryptEEKCallsMeter ()
specifier|public
specifier|static
name|Meter
name|getDecryptEEKCallsMeter
parameter_list|()
block|{
return|return
name|decryptEEKCallsMeter
return|;
block|}
DECL|method|getUnauthorizedCallsMeter ()
specifier|public
specifier|static
name|Meter
name|getUnauthorizedCallsMeter
parameter_list|()
block|{
return|return
name|unauthorizedCallsMeter
return|;
block|}
DECL|method|getUnauthenticatedCallsMeter ()
specifier|public
specifier|static
name|Meter
name|getUnauthenticatedCallsMeter
parameter_list|()
block|{
return|return
name|unauthenticatedCallsMeter
return|;
block|}
DECL|method|getKeyProvider ()
specifier|public
specifier|static
name|KeyProviderCryptoExtension
name|getKeyProvider
parameter_list|()
block|{
return|return
name|keyProviderCryptoExtension
return|;
block|}
DECL|method|getKMSAudit ()
specifier|public
specifier|static
name|KMSAudit
name|getKMSAudit
parameter_list|()
block|{
return|return
name|kmsAudit
return|;
block|}
block|}
end_class

end_unit

