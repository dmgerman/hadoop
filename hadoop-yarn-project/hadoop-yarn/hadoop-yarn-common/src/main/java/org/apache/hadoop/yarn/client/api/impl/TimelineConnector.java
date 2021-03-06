begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|impl
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
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ConnectException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|SocketTimeoutException
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
name|net
operator|.
name|URLConnection
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
name|PrivilegedExceptionAction
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
name|HostnameVerifier
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
name|HttpsURLConnection
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
name|SSLSocketFactory
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|AuthenticationException
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
name|authentication
operator|.
name|client
operator|.
name|ConnectionConfigurator
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenAuthenticatedURL
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
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
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
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenAuthenticator
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
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|KerberosDelegationTokenAuthenticator
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
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|PseudoDelegationTokenAuthenticator
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
name|YarnException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

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
name|client
operator|.
name|Client
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
name|client
operator|.
name|ClientHandlerException
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
name|client
operator|.
name|ClientRequest
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
name|client
operator|.
name|ClientResponse
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
name|client
operator|.
name|config
operator|.
name|ClientConfig
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
name|client
operator|.
name|config
operator|.
name|DefaultClientConfig
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
name|client
operator|.
name|filter
operator|.
name|ClientFilter
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
name|client
operator|.
name|urlconnection
operator|.
name|HttpURLConnectionFactory
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
name|client
operator|.
name|urlconnection
operator|.
name|URLConnectionClientHandler
import|;
end_import

begin_comment
comment|/**  * Utility Connector class which is used by timeline clients to securely get  * connected to the timeline server.  *  */
end_comment

begin_class
DECL|class|TimelineConnector
specifier|public
class|class
name|TimelineConnector
extends|extends
name|AbstractService
block|{
DECL|field|JOINER
specifier|private
specifier|static
specifier|final
name|Joiner
name|JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|""
argument_list|)
decl_stmt|;
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
name|TimelineConnector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_SOCKET_TIMEOUT
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_SOCKET_TIMEOUT
init|=
literal|1
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 minute
DECL|field|sslFactory
specifier|private
name|SSLFactory
name|sslFactory
decl_stmt|;
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
DECL|field|connConfigurator
specifier|private
name|ConnectionConfigurator
name|connConfigurator
decl_stmt|;
DECL|field|authenticator
specifier|private
name|DelegationTokenAuthenticator
name|authenticator
decl_stmt|;
DECL|field|token
specifier|private
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
name|token
decl_stmt|;
DECL|field|authUgi
specifier|private
name|UserGroupInformation
name|authUgi
decl_stmt|;
DECL|field|doAsUser
specifier|private
name|String
name|doAsUser
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|connectionRetry
name|TimelineClientConnectionRetry
name|connectionRetry
decl_stmt|;
DECL|field|requireConnectionRetry
specifier|private
name|boolean
name|requireConnectionRetry
decl_stmt|;
DECL|method|TimelineConnector (boolean requireConnectionRetry, UserGroupInformation authUgi, String doAsUser, DelegationTokenAuthenticatedURL.Token token)
specifier|public
name|TimelineConnector
parameter_list|(
name|boolean
name|requireConnectionRetry
parameter_list|,
name|UserGroupInformation
name|authUgi
parameter_list|,
name|String
name|doAsUser
parameter_list|,
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
name|token
parameter_list|)
block|{
name|super
argument_list|(
literal|"TimelineConnector"
argument_list|)
expr_stmt|;
name|this
operator|.
name|requireConnectionRetry
operator|=
name|requireConnectionRetry
expr_stmt|;
name|this
operator|.
name|authUgi
operator|=
name|authUgi
expr_stmt|;
name|this
operator|.
name|doAsUser
operator|=
name|doAsUser
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ClientConfig
name|cc
init|=
operator|new
name|DefaultClientConfig
argument_list|()
decl_stmt|;
name|cc
operator|.
name|getClasses
argument_list|()
operator|.
name|add
argument_list|(
name|YarnJacksonJaxbJsonProvider
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|)
block|{
comment|// If https is chosen, configures SSL client.
name|sslFactory
operator|=
name|getSSLFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|connConfigurator
operator|=
name|getConnConfigurator
argument_list|(
name|sslFactory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|connConfigurator
operator|=
name|DEFAULT_TIMEOUT_CONN_CONFIGURATOR
expr_stmt|;
block|}
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|authenticator
operator|=
operator|new
name|KerberosDelegationTokenAuthenticator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|authenticator
operator|=
operator|new
name|PseudoDelegationTokenAuthenticator
argument_list|()
expr_stmt|;
block|}
name|authenticator
operator|.
name|setConnectionConfigurator
argument_list|(
name|connConfigurator
argument_list|)
expr_stmt|;
name|connectionRetry
operator|=
operator|new
name|TimelineClientConnectionRetry
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|Client
argument_list|(
operator|new
name|URLConnectionClientHandler
argument_list|(
operator|new
name|TimelineURLConnectionFactory
argument_list|(
name|authUgi
argument_list|,
name|authenticator
argument_list|,
name|connConfigurator
argument_list|,
name|token
argument_list|,
name|doAsUser
argument_list|)
argument_list|)
argument_list|,
name|cc
argument_list|)
expr_stmt|;
if|if
condition|(
name|requireConnectionRetry
condition|)
block|{
name|TimelineJerseyRetryFilter
name|retryFilter
init|=
operator|new
name|TimelineJerseyRetryFilter
argument_list|(
name|connectionRetry
argument_list|)
decl_stmt|;
name|client
operator|.
name|addFilter
argument_list|(
name|retryFilter
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|DEFAULT_TIMEOUT_CONN_CONFIGURATOR
specifier|private
specifier|static
specifier|final
name|ConnectionConfigurator
name|DEFAULT_TIMEOUT_CONN_CONFIGURATOR
init|=
operator|new
name|ConnectionConfigurator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|HttpURLConnection
name|configure
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|setTimeouts
argument_list|(
name|conn
argument_list|,
name|DEFAULT_SOCKET_TIMEOUT
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
block|}
decl_stmt|;
DECL|method|getConnConfigurator (SSLFactory sslFactoryObj)
specifier|private
name|ConnectionConfigurator
name|getConnConfigurator
parameter_list|(
name|SSLFactory
name|sslFactoryObj
parameter_list|)
block|{
try|try
block|{
return|return
name|initSslConnConfigurator
argument_list|(
name|DEFAULT_SOCKET_TIMEOUT
argument_list|,
name|sslFactoryObj
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cannot load customized ssl related configuration. "
operator|+
literal|"Fallback to system-generic settings."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|DEFAULT_TIMEOUT_CONN_CONFIGURATOR
return|;
block|}
block|}
DECL|method|initSslConnConfigurator ( final int timeout, SSLFactory sslFactory)
specifier|private
specifier|static
name|ConnectionConfigurator
name|initSslConnConfigurator
parameter_list|(
specifier|final
name|int
name|timeout
parameter_list|,
name|SSLFactory
name|sslFactory
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
specifier|final
name|SSLSocketFactory
name|sf
decl_stmt|;
specifier|final
name|HostnameVerifier
name|hv
decl_stmt|;
name|sf
operator|=
name|sslFactory
operator|.
name|createSSLSocketFactory
argument_list|()
expr_stmt|;
name|hv
operator|=
name|sslFactory
operator|.
name|getHostnameVerifier
argument_list|()
expr_stmt|;
return|return
operator|new
name|ConnectionConfigurator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|HttpURLConnection
name|configure
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|conn
operator|instanceof
name|HttpsURLConnection
condition|)
block|{
name|HttpsURLConnection
name|c
init|=
operator|(
name|HttpsURLConnection
operator|)
name|conn
decl_stmt|;
name|c
operator|.
name|setSSLSocketFactory
argument_list|(
name|sf
argument_list|)
expr_stmt|;
name|c
operator|.
name|setHostnameVerifier
argument_list|(
name|hv
argument_list|)
expr_stmt|;
block|}
name|setTimeouts
argument_list|(
name|conn
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
block|}
return|;
block|}
DECL|method|getSSLFactory (Configuration conf)
specifier|protected
name|SSLFactory
name|getSSLFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|SSLFactory
name|newSSLFactory
init|=
operator|new
name|SSLFactory
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|CLIENT
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|newSSLFactory
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|newSSLFactory
return|;
block|}
DECL|method|setTimeouts (URLConnection connection, int socketTimeout)
specifier|private
specifier|static
name|void
name|setTimeouts
parameter_list|(
name|URLConnection
name|connection
parameter_list|,
name|int
name|socketTimeout
parameter_list|)
block|{
name|connection
operator|.
name|setConnectTimeout
argument_list|(
name|socketTimeout
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setReadTimeout
argument_list|(
name|socketTimeout
argument_list|)
expr_stmt|;
block|}
DECL|method|constructResURI (Configuration conf, String address, String uri)
specifier|public
specifier|static
name|URI
name|constructResURI
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|address
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
return|return
name|URI
operator|.
name|create
argument_list|(
name|JOINER
operator|.
name|join
argument_list|(
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|?
literal|"https://"
else|:
literal|"http://"
argument_list|,
name|address
argument_list|,
name|uri
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getDelegationTokenAuthenticatedURL ()
name|DelegationTokenAuthenticatedURL
name|getDelegationTokenAuthenticatedURL
parameter_list|()
block|{
return|return
operator|new
name|DelegationTokenAuthenticatedURL
argument_list|(
name|authenticator
argument_list|,
name|connConfigurator
argument_list|)
return|;
block|}
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|sslFactory
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|sslFactory
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getClient ()
specifier|public
name|Client
name|getClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
DECL|method|operateDelegationToken ( final PrivilegedExceptionAction<?> action)
specifier|public
name|Object
name|operateDelegationToken
parameter_list|(
specifier|final
name|PrivilegedExceptionAction
argument_list|<
name|?
argument_list|>
name|action
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
comment|// Set up the retry operation
name|TimelineClientRetryOp
name|tokenRetryOp
init|=
name|createRetryOpForOperateDelegationToken
argument_list|(
name|action
argument_list|)
decl_stmt|;
return|return
name|connectionRetry
operator|.
name|retryOn
argument_list|(
name|tokenRetryOp
argument_list|)
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|createRetryOpForOperateDelegationToken ( final PrivilegedExceptionAction<?> action)
name|TimelineClientRetryOp
name|createRetryOpForOperateDelegationToken
parameter_list|(
specifier|final
name|PrivilegedExceptionAction
argument_list|<
name|?
argument_list|>
name|action
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TimelineClientRetryOpForOperateDelegationToken
argument_list|(
name|this
operator|.
name|authUgi
argument_list|,
name|action
argument_list|)
return|;
block|}
comment|/**    * Abstract class for an operation that should be retried by timeline client.    */
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|class|TimelineClientRetryOp
specifier|public
specifier|static
specifier|abstract
class|class
name|TimelineClientRetryOp
block|{
comment|// The operation that should be retried
DECL|method|run ()
specifier|public
specifier|abstract
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|// The method to indicate if we should retry given the incoming exception
DECL|method|shouldRetryOn (Exception e)
specifier|public
specifier|abstract
name|boolean
name|shouldRetryOn
parameter_list|(
name|Exception
name|e
parameter_list|)
function_decl|;
block|}
DECL|class|TimelineURLConnectionFactory
specifier|private
specifier|static
class|class
name|TimelineURLConnectionFactory
implements|implements
name|HttpURLConnectionFactory
block|{
DECL|field|authenticator
specifier|private
name|DelegationTokenAuthenticator
name|authenticator
decl_stmt|;
DECL|field|authUgi
specifier|private
name|UserGroupInformation
name|authUgi
decl_stmt|;
DECL|field|connConfigurator
specifier|private
name|ConnectionConfigurator
name|connConfigurator
decl_stmt|;
DECL|field|token
specifier|private
name|Token
name|token
decl_stmt|;
DECL|field|doAsUser
specifier|private
name|String
name|doAsUser
decl_stmt|;
DECL|method|TimelineURLConnectionFactory (UserGroupInformation authUgi, DelegationTokenAuthenticator authenticator, ConnectionConfigurator connConfigurator, DelegationTokenAuthenticatedURL.Token token, String doAsUser)
specifier|public
name|TimelineURLConnectionFactory
parameter_list|(
name|UserGroupInformation
name|authUgi
parameter_list|,
name|DelegationTokenAuthenticator
name|authenticator
parameter_list|,
name|ConnectionConfigurator
name|connConfigurator
parameter_list|,
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
name|token
parameter_list|,
name|String
name|doAsUser
parameter_list|)
block|{
name|this
operator|.
name|authUgi
operator|=
name|authUgi
expr_stmt|;
name|this
operator|.
name|authenticator
operator|=
name|authenticator
expr_stmt|;
name|this
operator|.
name|connConfigurator
operator|=
name|connConfigurator
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|doAsUser
operator|=
name|doAsUser
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHttpURLConnection (final URL url)
specifier|public
name|HttpURLConnection
name|getHttpURLConnection
parameter_list|(
specifier|final
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|authUgi
operator|.
name|checkTGTAndReloginFromKeytab
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|new
name|DelegationTokenAuthenticatedURL
argument_list|(
name|authenticator
argument_list|,
name|connConfigurator
argument_list|)
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
name|doAsUser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ae
argument_list|)
throw|;
block|}
block|}
block|}
comment|// Class to handle retry
comment|// Outside this class, only visible to tests
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|class|TimelineClientConnectionRetry
specifier|static
class|class
name|TimelineClientConnectionRetry
block|{
comment|// maxRetries< 0 means keep trying
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|field|maxRetries
specifier|public
name|int
name|maxRetries
decl_stmt|;
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|field|retryInterval
specifier|public
name|long
name|retryInterval
decl_stmt|;
comment|// Indicates if retries happened last time. Only tests should read it.
comment|// In unit tests, retryOn() calls should _not_ be concurrent.
DECL|field|retried
specifier|private
name|boolean
name|retried
init|=
literal|false
decl_stmt|;
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getRetired ()
name|boolean
name|getRetired
parameter_list|()
block|{
return|return
name|retried
return|;
block|}
comment|// Constructor with default retry settings
DECL|method|TimelineClientConnectionRetry (Configuration conf)
specifier|public
name|TimelineClientConnectionRetry
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|)
operator|>=
operator|-
literal|1
argument_list|,
literal|"%s property value should be greater than or equal to -1"
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|)
operator|>
literal|0
argument_list|,
literal|"%s property value should be greater than zero"
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|)
expr_stmt|;
name|maxRetries
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|)
expr_stmt|;
name|retryInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|)
expr_stmt|;
block|}
DECL|method|retryOn (TimelineClientRetryOp op)
specifier|public
name|Object
name|retryOn
parameter_list|(
name|TimelineClientRetryOp
name|op
parameter_list|)
throws|throws
name|RuntimeException
throws|,
name|IOException
block|{
name|int
name|leftRetries
init|=
name|maxRetries
decl_stmt|;
name|retried
operator|=
literal|false
expr_stmt|;
comment|// keep trying
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
comment|// try perform the op, if fail, keep retrying
return|return
name|op
operator|.
name|run
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// break if there's no retries left
if|if
condition|(
name|leftRetries
operator|==
literal|0
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|op
operator|.
name|shouldRetryOn
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|logException
argument_list|(
name|e
argument_list|,
name|leftRetries
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
if|if
condition|(
name|leftRetries
operator|>
literal|0
condition|)
block|{
name|leftRetries
operator|--
expr_stmt|;
block|}
name|retried
operator|=
literal|true
expr_stmt|;
try|try
block|{
comment|// sleep for the given time interval
name|Thread
operator|.
name|sleep
argument_list|(
name|retryInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Client retry sleep interrupted! "
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to connect to timeline server. "
operator|+
literal|"Connection retries limit exceeded. "
operator|+
literal|"The posted timeline event may be missing"
argument_list|)
throw|;
block|}
empty_stmt|;
DECL|method|logException (Exception e, int leftRetries)
specifier|private
name|void
name|logException
parameter_list|(
name|Exception
name|e
parameter_list|,
name|int
name|leftRetries
parameter_list|)
block|{
if|if
condition|(
name|leftRetries
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception caught by TimelineClientConnectionRetry,"
operator|+
literal|" will try "
operator|+
name|leftRetries
operator|+
literal|" more time(s).\nMessage: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// note that maxRetries may be -1 at the very beginning
name|LOG
operator|.
name|info
argument_list|(
literal|"ConnectionException caught by TimelineClientConnectionRetry,"
operator|+
literal|" will keep retrying.\nMessage: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|TimelineJerseyRetryFilter
specifier|private
specifier|static
class|class
name|TimelineJerseyRetryFilter
extends|extends
name|ClientFilter
block|{
DECL|field|connectionRetry
specifier|private
name|TimelineClientConnectionRetry
name|connectionRetry
decl_stmt|;
DECL|method|TimelineJerseyRetryFilter ( TimelineClientConnectionRetry connectionRetry)
specifier|public
name|TimelineJerseyRetryFilter
parameter_list|(
name|TimelineClientConnectionRetry
name|connectionRetry
parameter_list|)
block|{
name|this
operator|.
name|connectionRetry
operator|=
name|connectionRetry
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (final ClientRequest cr)
specifier|public
name|ClientResponse
name|handle
parameter_list|(
specifier|final
name|ClientRequest
name|cr
parameter_list|)
throws|throws
name|ClientHandlerException
block|{
comment|// Set up the retry operation
name|TimelineClientRetryOp
name|jerseyRetryOp
init|=
operator|new
name|TimelineClientRetryOp
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
comment|// Try pass the request, if fail, keep retrying
return|return
name|getNext
argument_list|()
operator|.
name|handle
argument_list|(
name|cr
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldRetryOn
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Only retry on connection exceptions
return|return
operator|(
name|e
operator|instanceof
name|ClientHandlerException
operator|)
operator|&&
operator|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ConnectException
operator|||
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|SocketTimeoutException
operator|||
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|SocketException
operator|)
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
return|return
operator|(
name|ClientResponse
operator|)
name|connectionRetry
operator|.
name|retryOn
argument_list|(
name|jerseyRetryOp
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ClientHandlerException
argument_list|(
literal|"Jersey retry failed!\nMessage: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|class|TimelineClientRetryOpForOperateDelegationToken
specifier|public
specifier|static
class|class
name|TimelineClientRetryOpForOperateDelegationToken
extends|extends
name|TimelineClientRetryOp
block|{
DECL|field|authUgi
specifier|private
specifier|final
name|UserGroupInformation
name|authUgi
decl_stmt|;
DECL|field|action
specifier|private
specifier|final
name|PrivilegedExceptionAction
argument_list|<
name|?
argument_list|>
name|action
decl_stmt|;
DECL|method|TimelineClientRetryOpForOperateDelegationToken ( UserGroupInformation authUgi, PrivilegedExceptionAction<?> action)
specifier|public
name|TimelineClientRetryOpForOperateDelegationToken
parameter_list|(
name|UserGroupInformation
name|authUgi
parameter_list|,
name|PrivilegedExceptionAction
argument_list|<
name|?
argument_list|>
name|action
parameter_list|)
block|{
name|this
operator|.
name|authUgi
operator|=
name|authUgi
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Try pass the request, if fail, keep retrying
name|authUgi
operator|.
name|checkTGTAndReloginFromKeytab
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|authUgi
operator|.
name|doAs
argument_list|(
name|action
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
annotation|@
name|Override
DECL|method|shouldRetryOn (Exception e)
specifier|public
name|boolean
name|shouldRetryOn
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// retry on connection exceptions
comment|// and SocketTimeoutException
return|return
operator|(
name|e
operator|instanceof
name|ConnectException
operator|||
name|e
operator|instanceof
name|SocketTimeoutException
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

