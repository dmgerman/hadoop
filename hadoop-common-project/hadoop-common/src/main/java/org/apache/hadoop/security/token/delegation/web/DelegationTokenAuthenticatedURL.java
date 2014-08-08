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
comment|/**  * The<code>DelegationTokenAuthenticatedURL</code> is a  * {@link AuthenticatedURL} sub-class with built-in Hadoop Delegation Token  * functionality.  *<p/>  * The authentication mechanisms supported by default are Hadoop Simple  * authentication (also known as pseudo authentication) and Kerberos SPNEGO  * authentication.  *<p/>  * Additional authentication mechanisms can be supported via {@link  * DelegationTokenAuthenticator} implementations.  *<p/>  * The default {@link DelegationTokenAuthenticator} is the {@link  * KerberosDelegationTokenAuthenticator} class which supports  * automatic fallback from Kerberos SPNEGO to Hadoop Simple authentication via  * the {@link PseudoDelegationTokenAuthenticator} class.  *<p/>  *<code>AuthenticatedURL</code> instances are not thread-safe.  */
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
comment|/**    * Returns the default {@link DelegationTokenAuthenticator} class to use when    * an {@link DelegationTokenAuthenticatedURL} instance is created without    * specifying one.    *<p/>    * The default class is {@link KerberosDelegationTokenAuthenticator}    *    * @return the delegation token authenticator class to use as default.    */
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
DECL|method|obtainDelegationTokenAuthenticator (DelegationTokenAuthenticator dta)
name|obtainDelegationTokenAuthenticator
parameter_list|(
name|DelegationTokenAuthenticator
name|dta
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|dta
operator|!=
literal|null
operator|)
condition|?
name|dta
else|:
name|DEFAULT_AUTHENTICATOR
operator|.
name|newInstance
argument_list|()
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
comment|/**    * Creates an<code>DelegationTokenAuthenticatedURL</code>.    *<p/>    * An instance of the default {@link DelegationTokenAuthenticator} will be    * used.    */
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
argument_list|)
argument_list|,
name|connConfigurator
argument_list|)
expr_stmt|;
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
name|dt
init|=
name|creds
operator|.
name|getToken
argument_list|(
name|service
argument_list|)
decl_stmt|;
if|if
condition|(
name|dt
operator|!=
literal|null
condition|)
block|{
name|extraParams
operator|.
name|put
argument_list|(
name|KerberosDelegationTokenAuthenticator
operator|.
name|DELEGATION_PARAM
argument_list|,
name|dt
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
return|return
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
comment|/**    * Requests a delegation token using the configured<code>Authenticator</code>    * for authentication.    *    * @param url the URL to get the delegation token from. Only HTTP/S URLs are    * supported.    * @param token the authentication token being used for the user where the    * Delegation token will be stored.    * @return a delegation token.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
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

