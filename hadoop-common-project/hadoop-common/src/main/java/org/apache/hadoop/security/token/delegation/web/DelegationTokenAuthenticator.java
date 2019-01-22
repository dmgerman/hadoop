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
name|Authenticator
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
name|AbstractDelegationTokenIdentifier
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
name|HttpExceptionUtils
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
name|JsonSerialization
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
comment|/**  * {@link Authenticator} wrapper that enhances an {@link Authenticator} with  * Delegation Token support.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DelegationTokenAuthenticator
specifier|public
specifier|abstract
class|class
name|DelegationTokenAuthenticator
implements|implements
name|Authenticator
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DelegationTokenAuthenticator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"Content-Type"
decl_stmt|;
DECL|field|APPLICATION_JSON_MIME
specifier|private
specifier|static
specifier|final
name|String
name|APPLICATION_JSON_MIME
init|=
literal|"application/json"
decl_stmt|;
DECL|field|HTTP_GET
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_GET
init|=
literal|"GET"
decl_stmt|;
DECL|field|HTTP_PUT
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_PUT
init|=
literal|"PUT"
decl_stmt|;
DECL|field|OP_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|OP_PARAM
init|=
literal|"op"
decl_stmt|;
DECL|field|OP_PARAM_EQUALS
specifier|private
specifier|static
specifier|final
name|String
name|OP_PARAM_EQUALS
init|=
name|OP_PARAM
operator|+
literal|"="
decl_stmt|;
DECL|field|DELEGATION_TOKEN_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_HEADER
init|=
literal|"X-Hadoop-Delegation-Token"
decl_stmt|;
DECL|field|DELEGATION_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_PARAM
init|=
literal|"delegation"
decl_stmt|;
DECL|field|TOKEN_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_PARAM
init|=
literal|"token"
decl_stmt|;
DECL|field|RENEWER_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|RENEWER_PARAM
init|=
literal|"renewer"
decl_stmt|;
DECL|field|SERVICE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_PARAM
init|=
literal|"service"
decl_stmt|;
DECL|field|DELEGATION_TOKEN_JSON
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_JSON
init|=
literal|"Token"
decl_stmt|;
DECL|field|DELEGATION_TOKEN_URL_STRING_JSON
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_URL_STRING_JSON
init|=
literal|"urlString"
decl_stmt|;
DECL|field|RENEW_DELEGATION_TOKEN_JSON
specifier|public
specifier|static
specifier|final
name|String
name|RENEW_DELEGATION_TOKEN_JSON
init|=
literal|"long"
decl_stmt|;
comment|/**    * DelegationToken operations.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|DelegationTokenOperation
specifier|public
enum|enum
name|DelegationTokenOperation
block|{
DECL|enumConstant|GETDELEGATIONTOKEN
name|GETDELEGATIONTOKEN
argument_list|(
name|HTTP_GET
argument_list|,
literal|true
argument_list|)
block|,
DECL|enumConstant|RENEWDELEGATIONTOKEN
name|RENEWDELEGATIONTOKEN
argument_list|(
name|HTTP_PUT
argument_list|,
literal|true
argument_list|)
block|,
DECL|enumConstant|CANCELDELEGATIONTOKEN
name|CANCELDELEGATIONTOKEN
argument_list|(
name|HTTP_PUT
argument_list|,
literal|false
argument_list|)
block|;
DECL|field|httpMethod
specifier|private
name|String
name|httpMethod
decl_stmt|;
DECL|field|requiresKerberosCredentials
specifier|private
name|boolean
name|requiresKerberosCredentials
decl_stmt|;
DECL|method|DelegationTokenOperation (String httpMethod, boolean requiresKerberosCredentials)
specifier|private
name|DelegationTokenOperation
parameter_list|(
name|String
name|httpMethod
parameter_list|,
name|boolean
name|requiresKerberosCredentials
parameter_list|)
block|{
name|this
operator|.
name|httpMethod
operator|=
name|httpMethod
expr_stmt|;
name|this
operator|.
name|requiresKerberosCredentials
operator|=
name|requiresKerberosCredentials
expr_stmt|;
block|}
DECL|method|getHttpMethod ()
specifier|public
name|String
name|getHttpMethod
parameter_list|()
block|{
return|return
name|httpMethod
return|;
block|}
DECL|method|requiresKerberosCredentials ()
specifier|public
name|boolean
name|requiresKerberosCredentials
parameter_list|()
block|{
return|return
name|requiresKerberosCredentials
return|;
block|}
block|}
DECL|field|authenticator
specifier|private
name|Authenticator
name|authenticator
decl_stmt|;
DECL|field|connConfigurator
specifier|private
name|ConnectionConfigurator
name|connConfigurator
decl_stmt|;
DECL|method|DelegationTokenAuthenticator (Authenticator authenticator)
specifier|public
name|DelegationTokenAuthenticator
parameter_list|(
name|Authenticator
name|authenticator
parameter_list|)
block|{
name|this
operator|.
name|authenticator
operator|=
name|authenticator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setConnectionConfigurator (ConnectionConfigurator configurator)
specifier|public
name|void
name|setConnectionConfigurator
parameter_list|(
name|ConnectionConfigurator
name|configurator
parameter_list|)
block|{
name|authenticator
operator|.
name|setConnectionConfigurator
argument_list|(
name|configurator
argument_list|)
expr_stmt|;
name|connConfigurator
operator|=
name|configurator
expr_stmt|;
block|}
DECL|method|hasDelegationToken (URL url, AuthenticatedURL.Token token)
specifier|private
name|boolean
name|hasDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
name|Token
name|token
parameter_list|)
block|{
name|boolean
name|hasDt
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|token
operator|instanceof
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
condition|)
block|{
name|hasDt
operator|=
operator|(
operator|(
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
operator|)
name|token
operator|)
operator|.
name|getDelegationToken
argument_list|()
operator|!=
literal|null
expr_stmt|;
if|if
condition|(
name|hasDt
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Delegation token found: {}"
argument_list|,
operator|(
operator|(
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
operator|)
name|token
operator|)
operator|.
name|getDelegationToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|hasDt
condition|)
block|{
name|String
name|queryStr
init|=
name|url
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|hasDt
operator|=
operator|(
name|queryStr
operator|!=
literal|null
operator|)
operator|&&
name|queryStr
operator|.
name|contains
argument_list|(
name|DELEGATION_PARAM
operator|+
literal|"="
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"hasDt={}, queryStr={}"
argument_list|,
name|hasDt
argument_list|,
name|queryStr
argument_list|)
expr_stmt|;
block|}
return|return
name|hasDt
return|;
block|}
annotation|@
name|Override
DECL|method|authenticate (URL url, AuthenticatedURL.Token token)
specifier|public
name|void
name|authenticate
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
if|if
condition|(
operator|!
name|hasDelegationToken
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
condition|)
block|{
try|try
block|{
comment|// check and renew TGT to handle potential expiration
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|checkTGTAndReloginFromKeytab
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"No delegation token found for url={}, token={}, "
operator|+
literal|"authenticating with {}"
argument_list|,
name|url
argument_list|,
name|token
argument_list|,
name|authenticator
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|authenticator
operator|.
name|authenticate
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
name|NetUtils
operator|.
name|wrapException
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
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Authenticated from delegation token. url={}, token={}"
argument_list|,
name|url
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Requests a delegation token using the configured<code>Authenticator</code>    * for authentication.    *    * @param url the URL to get the delegation token from. Only HTTP/S URLs are    * supported.    * @param token the authentication token being used for the user where the    * Delegation token will be stored.    * @param renewer the renewer user.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
DECL|method|getDelegationToken (URL url, AuthenticatedURL.Token token, String renewer)
specifier|public
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
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
comment|/**    * Requests a delegation token using the configured<code>Authenticator</code>    * for authentication.    *    * @param url the URL to get the delegation token from. Only HTTP/S URLs are    * supported.    * @param token the authentication token being used for the user where the    * Delegation token will be stored.    * @param renewer the renewer user.    * @param doAsUser the user to do as, which will be the token owner.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
DECL|method|getDelegationToken (URL url, AuthenticatedURL.Token token, String renewer, String doAsUser)
specifier|public
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
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
name|Map
name|json
init|=
name|doDelegationTokenOperation
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
name|DelegationTokenOperation
operator|.
name|GETDELEGATIONTOKEN
argument_list|,
name|renewer
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|doAsUser
argument_list|)
decl_stmt|;
name|json
operator|=
operator|(
name|Map
operator|)
name|json
operator|.
name|get
argument_list|(
name|DELEGATION_TOKEN_JSON
argument_list|)
expr_stmt|;
name|String
name|tokenStr
init|=
operator|(
name|String
operator|)
name|json
operator|.
name|get
argument_list|(
name|DELEGATION_TOKEN_URL_STRING_JSON
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|dToken
init|=
operator|new
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|dToken
operator|.
name|decodeFromUrlString
argument_list|(
name|tokenStr
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|service
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
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|dToken
argument_list|,
name|service
argument_list|)
expr_stmt|;
return|return
name|dToken
return|;
block|}
comment|/**    * Renews a delegation token from the server end-point using the    * configured<code>Authenticator</code> for authentication.    *    * @param url the URL to renew the delegation token from. Only HTTP/S URLs are    * supported.    * @param token the authentication token with the Delegation Token to renew.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
DECL|method|renewDelegationToken (URL url, AuthenticatedURL.Token token, Token<AbstractDelegationTokenIdentifier> dToken)
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
name|Token
name|token
parameter_list|,
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|dToken
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
name|dToken
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Renews a delegation token from the server end-point using the    * configured<code>Authenticator</code> for authentication.    *    * @param url the URL to renew the delegation token from. Only HTTP/S URLs are    * supported.    * @param token the authentication token with the Delegation Token to renew.    * @param doAsUser the user to do as, which will be the token owner.    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
DECL|method|renewDelegationToken (URL url, AuthenticatedURL.Token token, Token<AbstractDelegationTokenIdentifier> dToken, String doAsUser)
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
name|Token
name|token
parameter_list|,
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|dToken
parameter_list|,
name|String
name|doAsUser
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|Map
name|json
init|=
name|doDelegationTokenOperation
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
name|DelegationTokenOperation
operator|.
name|RENEWDELEGATIONTOKEN
argument_list|,
literal|null
argument_list|,
name|dToken
argument_list|,
literal|true
argument_list|,
name|doAsUser
argument_list|)
decl_stmt|;
return|return
operator|(
name|Long
operator|)
name|json
operator|.
name|get
argument_list|(
name|RENEW_DELEGATION_TOKEN_JSON
argument_list|)
return|;
block|}
comment|/**    * Cancels a delegation token from the server end-point. It does not require    * being authenticated by the configured<code>Authenticator</code>.    *    * @param url the URL to cancel the delegation token from. Only HTTP/S URLs    * are supported.    * @param token the authentication token with the Delegation Token to cancel.    * @throws IOException if an IO error occurred.    */
DECL|method|cancelDelegationToken (URL url, AuthenticatedURL.Token token, Token<AbstractDelegationTokenIdentifier> dToken)
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
name|Token
name|token
parameter_list|,
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|dToken
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
name|dToken
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Cancels a delegation token from the server end-point. It does not require    * being authenticated by the configured<code>Authenticator</code>.    *    * @param url the URL to cancel the delegation token from. Only HTTP/S URLs    * are supported.    * @param token the authentication token with the Delegation Token to cancel.    * @param doAsUser the user to do as, which will be the token owner.    * @throws IOException if an IO error occurred.    */
DECL|method|cancelDelegationToken (URL url, AuthenticatedURL.Token token, Token<AbstractDelegationTokenIdentifier> dToken, String doAsUser)
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
name|Token
name|token
parameter_list|,
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|dToken
parameter_list|,
name|String
name|doAsUser
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|doDelegationTokenOperation
argument_list|(
name|url
argument_list|,
name|token
argument_list|,
name|DelegationTokenOperation
operator|.
name|CANCELDELEGATIONTOKEN
argument_list|,
literal|null
argument_list|,
name|dToken
argument_list|,
literal|false
argument_list|,
name|doAsUser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This should not happen: "
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
DECL|method|doDelegationTokenOperation (URL url, AuthenticatedURL.Token token, DelegationTokenOperation operation, String renewer, Token<?> dToken, boolean hasResponse, String doAsUser)
specifier|private
name|Map
name|doDelegationTokenOperation
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
name|Token
name|token
parameter_list|,
name|DelegationTokenOperation
name|operation
parameter_list|,
name|String
name|renewer
parameter_list|,
name|Token
argument_list|<
name|?
argument_list|>
name|dToken
parameter_list|,
name|boolean
name|hasResponse
parameter_list|,
name|String
name|doAsUser
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|Map
name|ret
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
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
name|params
operator|.
name|put
argument_list|(
name|OP_PARAM
argument_list|,
name|operation
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|renewer
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|RENEWER_PARAM
argument_list|,
name|renewer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dToken
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|TOKEN_PARAM
argument_list|,
name|dToken
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// proxyuser
if|if
condition|(
name|doAsUser
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|DelegationTokenAuthenticatedURL
operator|.
name|DO_AS
argument_list|,
name|doAsUser
argument_list|)
expr_stmt|;
block|}
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
name|entry
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
name|entry
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
name|URLEncoder
operator|.
name|encode
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"UTF8"
argument_list|)
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
name|AuthenticatedURL
name|aUrl
init|=
operator|new
name|AuthenticatedURL
argument_list|(
name|this
argument_list|,
name|connConfigurator
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
name|AbstractDelegationTokenIdentifier
argument_list|>
name|dt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|token
operator|instanceof
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
operator|&&
name|operation
operator|.
name|requiresKerberosCredentials
argument_list|()
condition|)
block|{
comment|// Unset delegation token to trigger fall-back authentication.
name|dt
operator|=
operator|(
operator|(
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
operator|)
name|token
operator|)
operator|.
name|getDelegationToken
argument_list|()
expr_stmt|;
operator|(
operator|(
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
operator|)
name|token
operator|)
operator|.
name|setDelegationToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|HttpURLConnection
name|conn
init|=
name|aUrl
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|operation
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
expr_stmt|;
name|HttpExceptionUtils
operator|.
name|validateResponse
argument_list|(
name|conn
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasResponse
condition|)
block|{
name|String
name|contentType
init|=
name|conn
operator|.
name|getHeaderField
argument_list|(
name|CONTENT_TYPE
argument_list|)
decl_stmt|;
name|contentType
operator|=
operator|(
name|contentType
operator|!=
literal|null
operator|)
condition|?
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|contentType
argument_list|)
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
operator|&&
name|contentType
operator|.
name|contains
argument_list|(
name|APPLICATION_JSON_MIME
argument_list|)
condition|)
block|{
try|try
block|{
name|ret
operator|=
name|JsonSerialization
operator|.
name|mapReader
argument_list|()
operator|.
name|readValue
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"'%s' did not handle the '%s' delegation token operation: %s"
argument_list|,
name|url
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|operation
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"'%s' did not "
operator|+
literal|"respond with JSON to the '%s' delegation token operation"
argument_list|,
name|url
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|operation
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|dt
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
operator|)
name|token
operator|)
operator|.
name|setDelegationToken
argument_list|(
name|dt
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

