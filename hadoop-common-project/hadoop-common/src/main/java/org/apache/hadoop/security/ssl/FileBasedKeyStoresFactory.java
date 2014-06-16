begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.ssl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ssl
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
name|annotations
operator|.
name|VisibleForTesting
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
name|classification
operator|.
name|InterfaceStability
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|KeyManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|KeyManagerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|io
operator|.
name|InputStream
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
name|security
operator|.
name|KeyStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_comment
comment|/**  * {@link KeyStoresFactory} implementation that reads the certificates from  * keystore files.  *<p/>  * if the trust certificates keystore file changes, the {@link TrustManager}  * is refreshed with the new trust certificate entries (using a  * {@link ReloadingX509TrustManager} trustmanager).  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|FileBasedKeyStoresFactory
specifier|public
class|class
name|FileBasedKeyStoresFactory
implements|implements
name|KeyStoresFactory
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SSL_KEYSTORE_LOCATION_TPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SSL_KEYSTORE_LOCATION_TPL_KEY
init|=
literal|"ssl.{0}.keystore.location"
decl_stmt|;
DECL|field|SSL_KEYSTORE_PASSWORD_TPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SSL_KEYSTORE_PASSWORD_TPL_KEY
init|=
literal|"ssl.{0}.keystore.password"
decl_stmt|;
DECL|field|SSL_KEYSTORE_KEYPASSWORD_TPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SSL_KEYSTORE_KEYPASSWORD_TPL_KEY
init|=
literal|"ssl.{0}.keystore.keypassword"
decl_stmt|;
DECL|field|SSL_KEYSTORE_TYPE_TPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SSL_KEYSTORE_TYPE_TPL_KEY
init|=
literal|"ssl.{0}.keystore.type"
decl_stmt|;
DECL|field|SSL_TRUSTSTORE_RELOAD_INTERVAL_TPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SSL_TRUSTSTORE_RELOAD_INTERVAL_TPL_KEY
init|=
literal|"ssl.{0}.truststore.reload.interval"
decl_stmt|;
DECL|field|SSL_TRUSTSTORE_LOCATION_TPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SSL_TRUSTSTORE_LOCATION_TPL_KEY
init|=
literal|"ssl.{0}.truststore.location"
decl_stmt|;
DECL|field|SSL_TRUSTSTORE_PASSWORD_TPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SSL_TRUSTSTORE_PASSWORD_TPL_KEY
init|=
literal|"ssl.{0}.truststore.password"
decl_stmt|;
DECL|field|SSL_TRUSTSTORE_TYPE_TPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SSL_TRUSTSTORE_TYPE_TPL_KEY
init|=
literal|"ssl.{0}.truststore.type"
decl_stmt|;
comment|/**    * Default format of the keystore files.    */
DECL|field|DEFAULT_KEYSTORE_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_KEYSTORE_TYPE
init|=
literal|"jks"
decl_stmt|;
comment|/**    * Reload interval in milliseconds.    */
DECL|field|DEFAULT_SSL_TRUSTSTORE_RELOAD_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SSL_TRUSTSTORE_RELOAD_INTERVAL
init|=
literal|10000
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|keyManagers
specifier|private
name|KeyManager
index|[]
name|keyManagers
decl_stmt|;
DECL|field|trustManagers
specifier|private
name|TrustManager
index|[]
name|trustManagers
decl_stmt|;
DECL|field|trustManager
specifier|private
name|ReloadingX509TrustManager
name|trustManager
decl_stmt|;
comment|/**    * Resolves a property name to its client/server version if applicable.    *<p/>    * NOTE: This method is public for testing purposes.    *    * @param mode client/server mode.    * @param template property name template.    * @return the resolved property name.    */
annotation|@
name|VisibleForTesting
DECL|method|resolvePropertyName (SSLFactory.Mode mode, String template)
specifier|public
specifier|static
name|String
name|resolvePropertyName
parameter_list|(
name|SSLFactory
operator|.
name|Mode
name|mode
parameter_list|,
name|String
name|template
parameter_list|)
block|{
return|return
name|MessageFormat
operator|.
name|format
argument_list|(
name|template
argument_list|,
name|mode
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Sets the configuration for the factory.    *    * @param conf the configuration for the factory.    */
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
comment|/**    * Returns the configuration of the factory.    *    * @return the configuration of the factory.    */
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Initializes the keystores of the factory.    *    * @param mode if the keystores are to be used in client or server mode.    * @throws IOException thrown if the keystores could not be initialized due    * to an IO error.    * @throws GeneralSecurityException thrown if the keystores could not be    * initialized due to a security error.    */
annotation|@
name|Override
DECL|method|init (SSLFactory.Mode mode)
specifier|public
name|void
name|init
parameter_list|(
name|SSLFactory
operator|.
name|Mode
name|mode
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
name|boolean
name|requireClientCert
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|SSLFactory
operator|.
name|SSL_REQUIRE_CLIENT_CERT_KEY
argument_list|,
name|SSLFactory
operator|.
name|DEFAULT_SSL_REQUIRE_CLIENT_CERT
argument_list|)
decl_stmt|;
comment|// certificate store
name|String
name|keystoreType
init|=
name|conf
operator|.
name|get
argument_list|(
name|resolvePropertyName
argument_list|(
name|mode
argument_list|,
name|SSL_KEYSTORE_TYPE_TPL_KEY
argument_list|)
argument_list|,
name|DEFAULT_KEYSTORE_TYPE
argument_list|)
decl_stmt|;
name|KeyStore
name|keystore
init|=
name|KeyStore
operator|.
name|getInstance
argument_list|(
name|keystoreType
argument_list|)
decl_stmt|;
name|String
name|keystoreKeyPassword
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|requireClientCert
operator|||
name|mode
operator|==
name|SSLFactory
operator|.
name|Mode
operator|.
name|SERVER
condition|)
block|{
name|String
name|locationProperty
init|=
name|resolvePropertyName
argument_list|(
name|mode
argument_list|,
name|SSL_KEYSTORE_LOCATION_TPL_KEY
argument_list|)
decl_stmt|;
name|String
name|keystoreLocation
init|=
name|conf
operator|.
name|get
argument_list|(
name|locationProperty
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|keystoreLocation
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|GeneralSecurityException
argument_list|(
literal|"The property '"
operator|+
name|locationProperty
operator|+
literal|"' has not been set in the ssl configuration file."
argument_list|)
throw|;
block|}
name|String
name|passwordProperty
init|=
name|resolvePropertyName
argument_list|(
name|mode
argument_list|,
name|SSL_KEYSTORE_PASSWORD_TPL_KEY
argument_list|)
decl_stmt|;
name|String
name|keystorePassword
init|=
name|conf
operator|.
name|get
argument_list|(
name|passwordProperty
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|keystorePassword
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|GeneralSecurityException
argument_list|(
literal|"The property '"
operator|+
name|passwordProperty
operator|+
literal|"' has not been set in the ssl configuration file."
argument_list|)
throw|;
block|}
name|String
name|keyPasswordProperty
init|=
name|resolvePropertyName
argument_list|(
name|mode
argument_list|,
name|SSL_KEYSTORE_KEYPASSWORD_TPL_KEY
argument_list|)
decl_stmt|;
comment|// Key password defaults to the same value as store password for
comment|// compatibility with legacy configurations that did not use a separate
comment|// configuration property for key password.
name|keystoreKeyPassword
operator|=
name|conf
operator|.
name|get
argument_list|(
name|keyPasswordProperty
argument_list|,
name|keystorePassword
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|mode
operator|.
name|toString
argument_list|()
operator|+
literal|" KeyStore: "
operator|+
name|keystoreLocation
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|keystoreLocation
argument_list|)
decl_stmt|;
try|try
block|{
name|keystore
operator|.
name|load
argument_list|(
name|is
argument_list|,
name|keystorePassword
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|mode
operator|.
name|toString
argument_list|()
operator|+
literal|" Loaded KeyStore: "
operator|+
name|keystoreLocation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|keystore
operator|.
name|load
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|KeyManagerFactory
name|keyMgrFactory
init|=
name|KeyManagerFactory
operator|.
name|getInstance
argument_list|(
name|SSLFactory
operator|.
name|SSLCERTIFICATE
argument_list|)
decl_stmt|;
name|keyMgrFactory
operator|.
name|init
argument_list|(
name|keystore
argument_list|,
operator|(
name|keystoreKeyPassword
operator|!=
literal|null
operator|)
condition|?
name|keystoreKeyPassword
operator|.
name|toCharArray
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
name|keyManagers
operator|=
name|keyMgrFactory
operator|.
name|getKeyManagers
argument_list|()
expr_stmt|;
comment|//trust store
name|String
name|truststoreType
init|=
name|conf
operator|.
name|get
argument_list|(
name|resolvePropertyName
argument_list|(
name|mode
argument_list|,
name|SSL_TRUSTSTORE_TYPE_TPL_KEY
argument_list|)
argument_list|,
name|DEFAULT_KEYSTORE_TYPE
argument_list|)
decl_stmt|;
name|String
name|locationProperty
init|=
name|resolvePropertyName
argument_list|(
name|mode
argument_list|,
name|SSL_TRUSTSTORE_LOCATION_TPL_KEY
argument_list|)
decl_stmt|;
name|String
name|truststoreLocation
init|=
name|conf
operator|.
name|get
argument_list|(
name|locationProperty
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|truststoreLocation
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|passwordProperty
init|=
name|resolvePropertyName
argument_list|(
name|mode
argument_list|,
name|SSL_TRUSTSTORE_PASSWORD_TPL_KEY
argument_list|)
decl_stmt|;
name|String
name|truststorePassword
init|=
name|conf
operator|.
name|get
argument_list|(
name|passwordProperty
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|truststorePassword
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|GeneralSecurityException
argument_list|(
literal|"The property '"
operator|+
name|passwordProperty
operator|+
literal|"' has not been set in the ssl configuration file."
argument_list|)
throw|;
block|}
name|long
name|truststoreReloadInterval
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|resolvePropertyName
argument_list|(
name|mode
argument_list|,
name|SSL_TRUSTSTORE_RELOAD_INTERVAL_TPL_KEY
argument_list|)
argument_list|,
name|DEFAULT_SSL_TRUSTSTORE_RELOAD_INTERVAL
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|mode
operator|.
name|toString
argument_list|()
operator|+
literal|" TrustStore: "
operator|+
name|truststoreLocation
argument_list|)
expr_stmt|;
name|trustManager
operator|=
operator|new
name|ReloadingX509TrustManager
argument_list|(
name|truststoreType
argument_list|,
name|truststoreLocation
argument_list|,
name|truststorePassword
argument_list|,
name|truststoreReloadInterval
argument_list|)
expr_stmt|;
name|trustManager
operator|.
name|init
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|mode
operator|.
name|toString
argument_list|()
operator|+
literal|" Loaded TrustStore: "
operator|+
name|truststoreLocation
argument_list|)
expr_stmt|;
name|trustManagers
operator|=
operator|new
name|TrustManager
index|[]
block|{
name|trustManager
block|}
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The property '"
operator|+
name|locationProperty
operator|+
literal|"' has not been set, "
operator|+
literal|"no TrustStore will be loaded"
argument_list|)
expr_stmt|;
name|trustManagers
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Releases any resources being used.    */
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
specifier|synchronized
name|void
name|destroy
parameter_list|()
block|{
if|if
condition|(
name|trustManager
operator|!=
literal|null
condition|)
block|{
name|trustManager
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|trustManager
operator|=
literal|null
expr_stmt|;
name|keyManagers
operator|=
literal|null
expr_stmt|;
name|trustManagers
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Returns the keymanagers for owned certificates.    *    * @return the keymanagers for owned certificates.    */
annotation|@
name|Override
DECL|method|getKeyManagers ()
specifier|public
name|KeyManager
index|[]
name|getKeyManagers
parameter_list|()
block|{
return|return
name|keyManagers
return|;
block|}
comment|/**    * Returns the trustmanagers for trusted certificates.    *    * @return the trustmanagers for trusted certificates.    */
annotation|@
name|Override
DECL|method|getTrustManagers ()
specifier|public
name|TrustManager
index|[]
name|getTrustManagers
parameter_list|()
block|{
return|return
name|trustManagers
return|;
block|}
block|}
end_class

end_unit

