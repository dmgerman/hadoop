begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|HttpURLConnection
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
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
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
name|web
operator|.
name|oauth2
operator|.
name|OAuth2ConnectionConfigurator
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
name|AuthenticatedURL
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

begin_comment
comment|/**  * Utilities for handling URLs  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|URLConnectionFactory
specifier|public
class|class
name|URLConnectionFactory
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
name|URLConnectionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Timeout for socket connects and reads    */
DECL|field|DEFAULT_SOCKET_TIMEOUT
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_SOCKET_TIMEOUT
init|=
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 minute
DECL|field|connConfigurator
specifier|private
specifier|final
name|ConnectionConfigurator
name|connConfigurator
decl_stmt|;
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
name|URLConnectionFactory
operator|.
name|setTimeouts
argument_list|(
name|conn
argument_list|,
name|DEFAULT_SOCKET_TIMEOUT
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
comment|/**    * The URLConnectionFactory that sets the default timeout and it only trusts    * Java's SSL certificates.    */
DECL|field|DEFAULT_SYSTEM_CONNECTION_FACTORY
specifier|public
specifier|static
specifier|final
name|URLConnectionFactory
name|DEFAULT_SYSTEM_CONNECTION_FACTORY
init|=
operator|new
name|URLConnectionFactory
argument_list|(
name|DEFAULT_TIMEOUT_CONN_CONFIGURATOR
argument_list|)
decl_stmt|;
comment|/**    * Construct a new URLConnectionFactory based on the configuration. It will    * try to load SSL certificates when it is specified.    */
DECL|method|newDefaultURLConnectionFactory ( Configuration conf)
specifier|public
specifier|static
name|URLConnectionFactory
name|newDefaultURLConnectionFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ConnectionConfigurator
name|conn
init|=
name|getSSLConnectionConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
operator|new
name|URLConnectionFactory
argument_list|(
name|conn
argument_list|)
return|;
block|}
DECL|method|getSSLConnectionConfiguration ( Configuration conf)
specifier|private
specifier|static
name|ConnectionConfigurator
name|getSSLConnectionConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ConnectionConfigurator
name|conn
decl_stmt|;
try|try
block|{
name|conn
operator|=
name|newSslConnConfigurator
argument_list|(
name|DEFAULT_SOCKET_TIMEOUT
argument_list|,
name|conf
argument_list|)
expr_stmt|;
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
literal|"Cannot load customized ssl related configuration. Fallback to"
operator|+
literal|" system-generic settings."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|conn
operator|=
name|DEFAULT_TIMEOUT_CONN_CONFIGURATOR
expr_stmt|;
block|}
return|return
name|conn
return|;
block|}
comment|/**    * Construct a new URLConnectionFactory that supports OAut-based connections.    * It will also try to load the SSL configuration when they are specified.    */
DECL|method|newOAuth2URLConnectionFactory ( Configuration conf)
specifier|public
specifier|static
name|URLConnectionFactory
name|newOAuth2URLConnectionFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|ConnectionConfigurator
name|conn
decl_stmt|;
try|try
block|{
name|ConnectionConfigurator
name|sslConnConfigurator
init|=
name|newSslConnConfigurator
argument_list|(
name|DEFAULT_SOCKET_TIMEOUT
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|conn
operator|=
operator|new
name|OAuth2ConnectionConfigurator
argument_list|(
name|conf
argument_list|,
name|sslConnConfigurator
argument_list|)
expr_stmt|;
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
literal|"Unable to load OAuth2 connection factory."
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|URLConnectionFactory
argument_list|(
name|conn
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|URLConnectionFactory (ConnectionConfigurator connConfigurator)
name|URLConnectionFactory
parameter_list|(
name|ConnectionConfigurator
name|connConfigurator
parameter_list|)
block|{
name|this
operator|.
name|connConfigurator
operator|=
name|connConfigurator
expr_stmt|;
block|}
comment|/**    * Create a new ConnectionConfigurator for SSL connections    */
DECL|method|newSslConnConfigurator ( final int defaultTimeout, Configuration conf)
specifier|private
specifier|static
name|ConnectionConfigurator
name|newSslConnConfigurator
parameter_list|(
specifier|final
name|int
name|defaultTimeout
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
specifier|final
name|SSLFactory
name|factory
decl_stmt|;
specifier|final
name|SSLSocketFactory
name|sf
decl_stmt|;
specifier|final
name|HostnameVerifier
name|hv
decl_stmt|;
specifier|final
name|int
name|connectTimeout
decl_stmt|;
specifier|final
name|int
name|readTimeout
decl_stmt|;
name|factory
operator|=
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
expr_stmt|;
name|factory
operator|.
name|init
argument_list|()
expr_stmt|;
name|sf
operator|=
name|factory
operator|.
name|createSSLSocketFactory
argument_list|()
expr_stmt|;
name|hv
operator|=
name|factory
operator|.
name|getHostnameVerifier
argument_list|()
expr_stmt|;
name|connectTimeout
operator|=
operator|(
name|int
operator|)
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_SOCKET_CONNECT_TIMEOUT_KEY
argument_list|,
name|defaultTimeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|readTimeout
operator|=
operator|(
name|int
operator|)
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_SOCKET_READ_TIMEOUT_KEY
argument_list|,
name|defaultTimeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
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
name|URLConnectionFactory
operator|.
name|setTimeouts
argument_list|(
name|conn
argument_list|,
name|connectTimeout
argument_list|,
name|readTimeout
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
block|}
return|;
block|}
comment|/**    * Opens a url with read and connect timeouts    *    * @param url    *          to open    * @return URLConnection    * @throws IOException    */
DECL|method|openConnection (URL url)
specifier|public
name|URLConnection
name|openConnection
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|openConnection
argument_list|(
name|url
argument_list|,
literal|false
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|e
parameter_list|)
block|{
comment|// Unreachable
name|LOG
operator|.
name|error
argument_list|(
literal|"Open connection {} failed"
argument_list|,
name|url
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Opens a url with read and connect timeouts    *    * @param url    *          URL to open    * @param isSpnego    *          whether the url should be authenticated via SPNEGO    * @return URLConnection    * @throws IOException    * @throws AuthenticationException    */
DECL|method|openConnection (URL url, boolean isSpnego)
specifier|public
name|URLConnection
name|openConnection
parameter_list|(
name|URL
name|url
parameter_list|,
name|boolean
name|isSpnego
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
if|if
condition|(
name|isSpnego
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"open AuthenticatedURL connection {}"
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|checkTGTAndReloginFromKeytab
argument_list|()
expr_stmt|;
specifier|final
name|AuthenticatedURL
operator|.
name|Token
name|authToken
init|=
operator|new
name|AuthenticatedURL
operator|.
name|Token
argument_list|()
decl_stmt|;
return|return
operator|new
name|AuthenticatedURL
argument_list|(
operator|new
name|KerberosUgiAuthenticator
argument_list|()
argument_list|,
name|connConfigurator
argument_list|)
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|authToken
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"open URL connection"
argument_list|)
expr_stmt|;
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|instanceof
name|HttpURLConnection
condition|)
block|{
name|connConfigurator
operator|.
name|configure
argument_list|(
operator|(
name|HttpURLConnection
operator|)
name|connection
argument_list|)
expr_stmt|;
block|}
return|return
name|connection
return|;
block|}
block|}
comment|/**    * Sets timeout parameters on the given URLConnection.    *    * @param connection    *          URLConnection to set    * @param socketTimeout    *          the connection and read timeout of the connection.    */
DECL|method|setTimeouts (URLConnection connection, int connectTimeout, int readTimeout)
specifier|private
specifier|static
name|void
name|setTimeouts
parameter_list|(
name|URLConnection
name|connection
parameter_list|,
name|int
name|connectTimeout
parameter_list|,
name|int
name|readTimeout
parameter_list|)
block|{
name|connection
operator|.
name|setConnectTimeout
argument_list|(
name|connectTimeout
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setReadTimeout
argument_list|(
name|readTimeout
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

