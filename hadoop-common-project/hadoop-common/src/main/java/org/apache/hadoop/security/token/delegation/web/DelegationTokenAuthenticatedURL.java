begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token.delegation.web
package|package
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
name|io
operator|.
name|Text
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
name|Credentials
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
name|token
operator|.
name|TokenIdentifier
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
name|AbstractDelegationTokenIdentifier
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
name|HttpURLConnection
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
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
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
name|Map
import|;
end_import

begin_comment
comment|/**  * The<code>DelegationTokenAuthenticatedURL</code> is a  * {@link AuthenticatedURL} sub-class with built-in Hadoop Delegation Token  * functionality.  *<p>  * The authentication mechanisms supported by default are Hadoop Simple  * authentication (also known as pseudo authentication) and Kerberos SPNEGO  * authentication.  *<p>  * Additional authentication mechanisms can be supported via {@link  * DelegationTokenAuthenticator} implementations.  *<p>  * The default {@link DelegationTokenAuthenticator} is the {@link  * KerberosDelegationTokenAuthenticator} class which supports  * automatic fallback from Kerberos SPNEGO to Hadoop Simple authentication via  * the {@link PseudoDelegationTokenAuthenticator} class.  *<p>  *<code>AuthenticatedURL</code> instances are not thread-safe.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DelegationTokenAuthenticatedURL
specifier|public
class|class
name|DelegationTokenAuthenticatedURL
extends|extends
name|AuthenticatedURL
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
name|DelegationTokenAuthenticatedURL
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Constant used in URL's query string to perform a proxy user request, the    * value of the<code>DO_AS</code> parameter is the user the request will be    * done on behalf of.    */
DECL|field|DO_AS
specifier|static
specifier|final
name|String
name|DO_AS
init|=
literal|"doAs"
decl_stmt|;
comment|/**    * Client side authentication token that handles Delegation Tokens.    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Token
specifier|public
specifier|static
class|class
name|Token
extends|extends
name|AuthenticatedURL
operator|.
name|Token
block|{
specifier|private
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
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
DECL|field|delegationToken
name|delegationToken
decl_stmt|;
specifier|public
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
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
DECL|method|getDelegationToken ()
name|getDelegationToken
parameter_list|()
block|{
return|return
name|delegationToken
return|;
block|}
DECL|method|setDelegationToken ( org.apache.hadoop.security.token.Token<AbstractDelegationTokenIdentifier> delegationToken)
specifier|public
name|void
name|setDelegationToken
parameter_list|(
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
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|delegationToken
parameter_list|)
block|{
name|this
operator|.
name|delegationToken
operator|=
name|delegationToken
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|DelegationTokenAuthenticator
argument_list|>
DECL|field|DEFAULT_AUTHENTICATOR
name|DEFAULT_AUTHENTICATOR
init|=
name|KerberosDelegationTokenAuthenticator
operator|.
name|class
decl_stmt|;
comment|/**    * Sets the default {@link DelegationTokenAuthenticator} class to use when an    * {@link DelegationTokenAuthenticatedURL} instance is created without    * specifying one.    *    * The default class is {@link KerberosDelegationTokenAuthenticator}    *    * @param authenticator the authenticator class to use as default.    */
DECL|method|setDefaultDelegationTokenAuthenticator ( Class<? extends DelegationTokenAuthenticator> authenticator)
specifier|public
specifier|static
name|void
name|setDefaultDelegationTokenAuthenticator
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|DelegationTokenAuthenticator
argument_list|>
name|authenticator
parameter_list|)
block|{
name|DEFAULT_AUTHENTICATOR
operator|=
name|authenticator
expr_stmt|;
block|}
comment|/**    * Returns the default {@link DelegationTokenAuthenticator} class to use when    * an {@link DelegationTokenAuthenticatedURL} instance is created without    * specifying one.    *<p>    * The default class is {@link KerberosDelegationTokenAuthenticator}    *    * @return the delegation token authenticator class to use as default.    */
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|DelegationTokenAuthenticator
argument_list|>
DECL|method|getDefaultDelegationTokenAuthenticator ()
name|getDefaultDelegationTokenAuthenticator
parameter_list|()
block|{
return|return
name|DEFAULT_AUTHENTICATOR
return|;
block|}
specifier|private
specifier|static
name|DelegationTokenAuthenticator
DECL|method|obtainDelegationTokenAuthenticator (DelegationTokenAuthenticator dta, ConnectionConfigurator connConfigurator)
name|obtainDelegationTokenAuthenticator
parameter_list|(
name|DelegationTokenAuthenticator
name|dta
parameter_list|,
name|ConnectionConfigurator
name|connConfigurator
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|dta
operator|==
literal|null
condition|)
block|{
name|dta
operator|=
name|DEFAULT_AUTHENTICATOR
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|dta
operator|.
name|setConnectionConfigurator
argument_list|(
name|connConfigurator
argument_list|)
expr_stmt|;
block|}
return|return
name|dta
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|field|useQueryStringforDelegationToken
specifier|private
name|boolean
name|useQueryStringforDelegationToken
init|=
literal|false
decl_stmt|;
comment|/**    * Creates an<code>DelegationTokenAuthenticatedURL</code>.    *<p>    * An instance of the default {@link DelegationTokenAuthenticator} will be    * used.    */
DECL|method|DelegationTokenAuthenticatedURL ()
specifier|public
name|DelegationTokenAuthenticatedURL
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an<code>DelegationTokenAuthenticatedURL</code>.    *    * @param authenticator the {@link DelegationTokenAuthenticator} instance to    * use, if<code>null</code> the default one will be used.    */
DECL|method|DelegationTokenAuthenticatedURL ( DelegationTokenAuthenticator authenticator)
specifier|public
name|DelegationTokenAuthenticatedURL
parameter_list|(
name|DelegationTokenAuthenticator
name|authenticator
parameter_list|)
block|{
name|this
argument_list|(
name|authenticator
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an<code>DelegationTokenAuthenticatedURL</code> using the default    * {@link DelegationTokenAuthenticator} class.    *    * @param connConfigurator a connection configurator.    */
DECL|method|DelegationTokenAuthenticatedURL ( ConnectionConfigurator connConfigurator)
specifier|public
name|DelegationTokenAuthenticatedURL
parameter_list|(
name|ConnectionConfigurator
name|connConfigurator
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|connConfigurator
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an<code>DelegationTokenAuthenticatedURL</code>.    *    * @param authenticator the {@link DelegationTokenAuthenticator} instance to    * use, if<code>null</code> the default one will be used.    * @param connConfigurator a connection configurator.    */
DECL|method|DelegationTokenAuthenticatedURL ( DelegationTokenAuthenticator authenticator, ConnectionConfigurator connConfigurator)
specifier|public
name|DelegationTokenAuthenticatedURL
parameter_list|(
name|DelegationTokenAuthenticator
name|authenticator
parameter_list|,
name|ConnectionConfigurator
name|connConfigurator
parameter_list|)
block|{
name|super
argument_list|(
name|obtainDelegationTokenAuthenticator
argument_list|(
name|authenticator
argument_list|,
name|connConfigurator
argument_list|)
argument_list|,
name|connConfigurator
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets if delegation token should be transmitted in the URL query string.    * By default it is transmitted using the    * {@link DelegationTokenAuthenticator#DELEGATION_TOKEN_HEADER} HTTP header.    *<p>    * This method is provided to enable WebHDFS backwards compatibility.    *    * @param useQueryString<code>TRUE</code> if the token is transmitted in the    * URL query string,<code>FALSE</code> if the delegation token is transmitted    * using the {@link DelegationTokenAuthenticator#DELEGATION_TOKEN_HEADER} HTTP    * header.    */
annotation|@
name|Deprecated
DECL|method|setUseQueryStringForDelegationToken (boolean useQueryString)
specifier|protected
name|void
name|setUseQueryStringForDelegationToken
parameter_list|(
name|boolean
name|useQueryString
parameter_list|)
block|{
name|useQueryStringforDelegationToken
operator|=
name|useQueryString
expr_stmt|;
block|}
comment|/**    * Returns if delegation token is transmitted as a HTTP header.    *    * @return<code>TRUE</code> if the token is transmitted in the URL query    * string,<code>FALSE</code> if the delegation token is transmitted using the    * {@link DelegationTokenAuthenticator#DELEGATION_TOKEN_HEADER} HTTP header.    */
DECL|method|useQueryStringForDelegationToken ()
specifier|public
name|boolean
name|useQueryStringForDelegationToken
parameter_list|()
block|{
return|return
name|useQueryStringforDelegationToken
return|;
block|}
comment|/**    * Returns an authenticated {@link HttpURLConnection}, it uses a Delegation    * Token only if the given auth token is an instance of {@link Token} and    * it contains a Delegation Token, otherwise use the configured    * {@link DelegationTokenAuthenticator} to authenticate the connection.    *    * @param url the URL to connect to. Only HTTP/S URLs are supported.    * @param token the authentication token being used for the user.    * @return an authenticated {@link HttpURLConnection}.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
annotation|@
name|Override
DECL|method|openConnection (URL url, AuthenticatedURL.Token token)
specifier|public
name|HttpURLConnection
name|openConnection
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
return|return
operator|(
name|token
operator|instanceof
name|Token
operator|)
condition|?
name|openConnection
argument_list|(
name|url
argument_list|,
operator|(
name|Token
operator|)
name|token
argument_list|)
else|:
name|super
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
return|;
block|}
comment|/**    * Returns an authenticated {@link HttpURLConnection}. If the Delegation    * Token is present, it will be used taking precedence over the configured    *<code>Authenticator</code>.    *    * @param url the URL to connect to. Only HTTP/S URLs are supported.    * @param token the authentication token being used for the user.    * @return an authenticated {@link HttpURLConnection}.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
DECL|method|openConnection (URL url, Token token)
specifier|public
name|HttpURLConnection
name|openConnection
parameter_list|(
name|URL
name|url
parameter_list|,
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
return|return
name|openConnection
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|augmentURL (URL url, Map<String, String> params)
specifier|private
name|URL
name|augmentURL
parameter_list|(
name|URL
name|url
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|params
operator|!=
literal|null
operator|&&
name|params
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|urlStr
init|=
name|url
operator|.
name|toExternalForm
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|urlStr
argument_list|)
decl_stmt|;
name|String
name|separator
init|=
operator|(
name|urlStr
operator|.
name|contains
argument_list|(
literal|"?"
argument_list|)
operator|)
condition|?
literal|"&"
else|:
literal|"?"
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|param
range|:
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|separator
argument_list|)
operator|.
name|append
argument_list|(
name|param
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|param
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|"&"
expr_stmt|;
block|}
name|url
operator|=
operator|new
name|URL
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|url
return|;
block|}
comment|/**    * Returns an authenticated {@link HttpURLConnection}. If the Delegation    * Token is present, it will be used taking precedence over the configured    *<code>Authenticator</code>. If the<code>doAs</code> parameter is not NULL,    * the request will be done on behalf of the specified<code>doAs</code> user.    *    * @param url the URL to connect to. Only HTTP/S URLs are supported.    * @param token the authentication token being used for the user.    * @param doAs user to do the the request on behalf of, if NULL the request is    * as self.    * @return an authenticated {@link HttpURLConnection}.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|openConnection (URL url, Token token, String doAs)
specifier|public
name|HttpURLConnection
name|openConnection
parameter_list|(
name|URL
name|url
parameter_list|,
name|Token
name|token
parameter_list|,
name|String
name|doAs
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|url
argument_list|,
literal|"url"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|token
argument_list|,
literal|"token"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extraParams
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
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|dToken
init|=
literal|null
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to url {} with token {} as {}"
argument_list|,
name|url
argument_list|,
name|token
argument_list|,
name|doAs
argument_list|)
expr_stmt|;
comment|// if we have valid auth token, it takes precedence over a delegation token
comment|// and we don't even look for one.
if|if
condition|(
operator|!
name|token
operator|.
name|isSet
argument_list|()
condition|)
block|{
comment|// delegation token
name|Credentials
name|creds
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Token not set, looking for delegation token. Creds:{},"
operator|+
literal|" size:{}"
argument_list|,
name|creds
operator|.
name|getAllTokens
argument_list|()
argument_list|,
name|creds
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|creds
operator|.
name|getAllTokens
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|dToken
operator|=
name|selectDelegationToken
argument_list|(
name|url
argument_list|,
name|creds
argument_list|)
expr_stmt|;
if|if
condition|(
name|dToken
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|useQueryStringForDelegationToken
argument_list|()
condition|)
block|{
comment|// delegation token will go in the query string, injecting it
name|extraParams
operator|.
name|put
argument_list|(
name|KerberosDelegationTokenAuthenticator
operator|.
name|DELEGATION_PARAM
argument_list|,
name|dToken
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// delegation token will go as request header, setting it in the
comment|// auth-token to ensure no authentication handshake is triggered
comment|// (if we have a delegation token, we are authenticated)
comment|// the delegation token header is injected in the connection request
comment|// at the end of this method.
name|token
operator|.
name|delegationToken
operator|=
operator|(
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
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
operator|)
name|dToken
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// proxyuser
if|if
condition|(
name|doAs
operator|!=
literal|null
condition|)
block|{
name|extraParams
operator|.
name|put
argument_list|(
name|DO_AS
argument_list|,
name|URLEncoder
operator|.
name|encode
argument_list|(
name|doAs
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|url
operator|=
name|augmentURL
argument_list|(
name|url
argument_list|,
name|extraParams
argument_list|)
expr_stmt|;
name|HttpURLConnection
name|conn
init|=
name|super
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|token
operator|.
name|isSet
argument_list|()
operator|&&
operator|!
name|useQueryStringForDelegationToken
argument_list|()
operator|&&
name|dToken
operator|!=
literal|null
condition|)
block|{
comment|// injecting the delegation token header in the connection request
name|conn
operator|.
name|setRequestProperty
argument_list|(
name|DelegationTokenAuthenticator
operator|.
name|DELEGATION_TOKEN_HEADER
argument_list|,
name|dToken
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|conn
return|;
block|}
comment|/**    * Select a delegation token from all tokens in credentials, based on url.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
specifier|public
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
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
DECL|method|selectDelegationToken (URL url, Credentials creds)
name|selectDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|Credentials
name|creds
parameter_list|)
block|{
specifier|final
name|InetSocketAddress
name|serviceAddr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|url
operator|.
name|getHost
argument_list|()
argument_list|,
name|url
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Text
name|service
init|=
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|serviceAddr
argument_list|)
decl_stmt|;
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
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|dToken
init|=
name|creds
operator|.
name|getToken
argument_list|(
name|service
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using delegation token {} from service:{}"
argument_list|,
name|dToken
argument_list|,
name|service
argument_list|)
expr_stmt|;
return|return
name|dToken
return|;
block|}
comment|/**    * Requests a delegation token using the configured<code>Authenticator</code>    * for authentication.    *    * @param url the URL to get the delegation token from. Only HTTP/S URLs are    * supported.    * @param token the authentication token being used for the user where the    * Delegation token will be stored.    * @param renewer the renewer user.    * @return a delegation token.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
specifier|public
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
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
DECL|method|getDelegationToken (URL url, Token token, String renewer)
name|getDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|Token
name|token
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
return|return
name|getDelegationToken
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
name|renewer
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Requests a delegation token using the configured<code>Authenticator</code>    * for authentication.    *    * @param url the URL to get the delegation token from. Only HTTP/S URLs are    * supported.    * @param token the authentication token being used for the user where the    * Delegation token will be stored.    * @param renewer the renewer user.    * @param doAsUser the user to do as, which will be the token owner.    * @return a delegation token.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
specifier|public
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
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
DECL|method|getDelegationToken (URL url, Token token, String renewer, String doAsUser)
name|getDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|Token
name|token
parameter_list|,
name|String
name|renewer
parameter_list|,
name|String
name|doAsUser
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|url
argument_list|,
literal|"url"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|token
argument_list|,
literal|"token"
argument_list|)
expr_stmt|;
try|try
block|{
name|token
operator|.
name|delegationToken
operator|=
operator|(
operator|(
name|KerberosDelegationTokenAuthenticator
operator|)
name|getAuthenticator
argument_list|()
operator|)
operator|.
name|getDelegationToken
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
name|renewer
argument_list|,
name|doAsUser
argument_list|)
expr_stmt|;
return|return
name|token
operator|.
name|delegationToken
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|token
operator|.
name|delegationToken
operator|=
literal|null
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
comment|/**    * Renews a delegation token from the server end-point using the    * configured<code>Authenticator</code> for authentication.    *    * @param url the URL to renew the delegation token from. Only HTTP/S URLs are    * supported.    * @param token the authentication token with the Delegation Token to renew.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
DECL|method|renewDelegationToken (URL url, Token token)
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
return|return
name|renewDelegationToken
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Renews a delegation token from the server end-point using the    * configured<code>Authenticator</code> for authentication.    *    * @param url the URL to renew the delegation token from. Only HTTP/S URLs are    * supported.    * @param token the authentication token with the Delegation Token to renew.    * @param doAsUser the user to do as, which will be the token owner.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
DECL|method|renewDelegationToken (URL url, Token token, String doAsUser)
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|Token
name|token
parameter_list|,
name|String
name|doAsUser
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|url
argument_list|,
literal|"url"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|token
argument_list|,
literal|"token"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|token
operator|.
name|delegationToken
argument_list|,
literal|"No delegation token available"
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
operator|(
name|KerberosDelegationTokenAuthenticator
operator|)
name|getAuthenticator
argument_list|()
operator|)
operator|.
name|renewDelegationToken
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
name|token
operator|.
name|delegationToken
argument_list|,
name|doAsUser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|token
operator|.
name|delegationToken
operator|=
literal|null
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
comment|/**    * Cancels a delegation token from the server end-point. It does not require    * being authenticated by the configured<code>Authenticator</code>.    *    * @param url the URL to cancel the delegation token from. Only HTTP/S URLs    * are supported.    * @param token the authentication token with the Delegation Token to cancel.    * @throws IOException if an IO error occurred.    */
DECL|method|cancelDelegationToken (URL url, Token token)
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|cancelDelegationToken
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Cancels a delegation token from the server end-point. It does not require    * being authenticated by the configured<code>Authenticator</code>.    *    * @param url the URL to cancel the delegation token from. Only HTTP/S URLs    * are supported.    * @param token the authentication token with the Delegation Token to cancel.    * @param doAsUser the user to do as, which will be the token owner.    * @throws IOException if an IO error occurred.    */
DECL|method|cancelDelegationToken (URL url, Token token, String doAsUser)
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|Token
name|token
parameter_list|,
name|String
name|doAsUser
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|url
argument_list|,
literal|"url"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|token
argument_list|,
literal|"token"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|token
operator|.
name|delegationToken
argument_list|,
literal|"No delegation token available"
argument_list|)
expr_stmt|;
try|try
block|{
operator|(
operator|(
name|KerberosDelegationTokenAuthenticator
operator|)
name|getAuthenticator
argument_list|()
operator|)
operator|.
name|cancelDelegationToken
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
name|token
operator|.
name|delegationToken
argument_list|,
name|doAsUser
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|token
operator|.
name|delegationToken
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

