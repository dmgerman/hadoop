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
name|server
operator|.
name|AuthenticationHandler
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
name|server
operator|.
name|AuthenticationToken
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
name|server
operator|.
name|KerberosAuthenticationHandler
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
name|AbstractDelegationTokenSecretManager
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
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
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
name|Writer
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * An {@link AuthenticationHandler} that implements Kerberos SPNEGO mechanism  * for HTTP and supports Delegation Token functionality.  *<p/>  * In addition to the wrapped {@link AuthenticationHandler} configuration  * properties, this handler supports the following properties prefixed  * with the type of the wrapped<code>AuthenticationHandler</code>:  *<ul>  *<li>delegation-token.token-kind: the token kind for generated tokens  * (no default, required property).</li>  *<li>delegation-token.update-interval.sec: secret manager master key  * update interval in seconds (default 1 day).</li>  *<li>delegation-token.max-lifetime.sec: maximum life of a delegation  * token in seconds (default 7 days).</li>  *<li>delegation-token.renewal-interval.sec: renewal interval for  * delegation tokens in seconds (default 1 day).</li>  *<li>delegation-token.removal-scan-interval.sec: delegation tokens  * removal scan interval in seconds (default 1 hour).</li>  *</ul>  *  */
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
DECL|class|DelegationTokenAuthenticationHandler
specifier|public
specifier|abstract
class|class
name|DelegationTokenAuthenticationHandler
implements|implements
name|AuthenticationHandler
block|{
DECL|field|TYPE_POSTFIX
specifier|protected
specifier|static
specifier|final
name|String
name|TYPE_POSTFIX
init|=
literal|"-dt"
decl_stmt|;
DECL|field|PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"delegation-token."
decl_stmt|;
DECL|field|TOKEN_KIND
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_KIND
init|=
name|PREFIX
operator|+
literal|"token-kind"
decl_stmt|;
DECL|field|DELEGATION_TOKEN_OPS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|DELEGATION_TOKEN_OPS
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|DELEGATION_TOKEN_UGI_ATTRIBUTE
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_UGI_ATTRIBUTE
init|=
literal|"hadoop.security.delegation-token.ugi"
decl_stmt|;
static|static
block|{
name|DELEGATION_TOKEN_OPS
operator|.
name|add
argument_list|(
name|KerberosDelegationTokenAuthenticator
operator|.
name|DelegationTokenOperation
operator|.
name|GETDELEGATIONTOKEN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DELEGATION_TOKEN_OPS
operator|.
name|add
argument_list|(
name|KerberosDelegationTokenAuthenticator
operator|.
name|DelegationTokenOperation
operator|.
name|RENEWDELEGATIONTOKEN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DELEGATION_TOKEN_OPS
operator|.
name|add
argument_list|(
name|KerberosDelegationTokenAuthenticator
operator|.
name|DelegationTokenOperation
operator|.
name|CANCELDELEGATIONTOKEN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|authHandler
specifier|private
name|AuthenticationHandler
name|authHandler
decl_stmt|;
DECL|field|tokenManager
specifier|private
name|DelegationTokenManager
name|tokenManager
decl_stmt|;
DECL|field|authType
specifier|private
name|String
name|authType
decl_stmt|;
DECL|method|DelegationTokenAuthenticationHandler (AuthenticationHandler handler)
specifier|public
name|DelegationTokenAuthenticationHandler
parameter_list|(
name|AuthenticationHandler
name|handler
parameter_list|)
block|{
name|authHandler
operator|=
name|handler
expr_stmt|;
name|authType
operator|=
name|handler
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getTokenManager ()
name|DelegationTokenManager
name|getTokenManager
parameter_list|()
block|{
return|return
name|tokenManager
return|;
block|}
annotation|@
name|Override
DECL|method|init (Properties config)
specifier|public
name|void
name|init
parameter_list|(
name|Properties
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|authHandler
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|initTokenManager
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets an external<code>DelegationTokenSecretManager</code> instance to    * manage creation and verification of Delegation Tokens.    *<p/>    * This is useful for use cases where secrets must be shared across multiple    * services.    *    * @param secretManager a<code>DelegationTokenSecretManager</code> instance    */
DECL|method|setExternalDelegationTokenSecretManager ( AbstractDelegationTokenSecretManager secretManager)
specifier|public
name|void
name|setExternalDelegationTokenSecretManager
parameter_list|(
name|AbstractDelegationTokenSecretManager
name|secretManager
parameter_list|)
block|{
name|tokenManager
operator|.
name|setExternalDelegationTokenSecretManager
argument_list|(
name|secretManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|initTokenManager (Properties config)
specifier|public
name|void
name|initTokenManager
parameter_list|(
name|Properties
name|config
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|config
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|tokenKind
init|=
name|conf
operator|.
name|get
argument_list|(
name|TOKEN_KIND
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenKind
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The configuration does not define the token kind"
argument_list|)
throw|;
block|}
name|tokenKind
operator|=
name|tokenKind
operator|.
name|trim
argument_list|()
expr_stmt|;
name|tokenManager
operator|=
operator|new
name|DelegationTokenManager
argument_list|(
name|conf
argument_list|,
operator|new
name|Text
argument_list|(
name|tokenKind
argument_list|)
argument_list|)
expr_stmt|;
name|tokenManager
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|tokenManager
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|authHandler
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|authType
return|;
block|}
DECL|field|ENTER
specifier|private
specifier|static
specifier|final
name|String
name|ENTER
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|managementOperation (AuthenticationToken token, HttpServletRequest request, HttpServletResponse response)
specifier|public
name|boolean
name|managementOperation
parameter_list|(
name|AuthenticationToken
name|token
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|boolean
name|requestContinues
init|=
literal|true
decl_stmt|;
name|String
name|op
init|=
name|ServletUtils
operator|.
name|getParameter
argument_list|(
name|request
argument_list|,
name|KerberosDelegationTokenAuthenticator
operator|.
name|OP_PARAM
argument_list|)
decl_stmt|;
name|op
operator|=
operator|(
name|op
operator|!=
literal|null
operator|)
condition|?
name|op
operator|.
name|toUpperCase
argument_list|()
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|DELEGATION_TOKEN_OPS
operator|.
name|contains
argument_list|(
name|op
argument_list|)
operator|&&
operator|!
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
literal|"OPTIONS"
argument_list|)
condition|)
block|{
name|KerberosDelegationTokenAuthenticator
operator|.
name|DelegationTokenOperation
name|dtOp
init|=
name|KerberosDelegationTokenAuthenticator
operator|.
name|DelegationTokenOperation
operator|.
name|valueOf
argument_list|(
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|dtOp
operator|.
name|getHttpMethod
argument_list|()
operator|.
name|equals
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
condition|)
block|{
name|boolean
name|doManagement
decl_stmt|;
if|if
condition|(
name|dtOp
operator|.
name|requiresKerberosCredentials
argument_list|()
operator|&&
name|token
operator|==
literal|null
condition|)
block|{
name|token
operator|=
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|requestContinues
operator|=
literal|false
expr_stmt|;
name|doManagement
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|doManagement
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|doManagement
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|doManagement
condition|)
block|{
name|UserGroupInformation
name|requestUgi
init|=
operator|(
name|token
operator|!=
literal|null
operator|)
condition|?
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|token
operator|.
name|getUserName
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
name|Map
name|map
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|dtOp
condition|)
block|{
case|case
name|GETDELEGATIONTOKEN
case|:
if|if
condition|(
name|requestUgi
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"request UGI cannot be NULL"
argument_list|)
throw|;
block|}
name|String
name|renewer
init|=
name|ServletUtils
operator|.
name|getParameter
argument_list|(
name|request
argument_list|,
name|KerberosDelegationTokenAuthenticator
operator|.
name|RENEWER_PARAM
argument_list|)
decl_stmt|;
try|try
block|{
name|Token
argument_list|<
name|?
argument_list|>
name|dToken
init|=
name|tokenManager
operator|.
name|createToken
argument_list|(
name|requestUgi
argument_list|,
name|renewer
argument_list|)
decl_stmt|;
name|map
operator|=
name|delegationTokenToJSON
argument_list|(
name|dToken
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
operator|new
name|AuthenticationException
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
break|break;
case|case
name|RENEWDELEGATIONTOKEN
case|:
if|if
condition|(
name|requestUgi
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"request UGI cannot be NULL"
argument_list|)
throw|;
block|}
name|String
name|tokenToRenew
init|=
name|ServletUtils
operator|.
name|getParameter
argument_list|(
name|request
argument_list|,
name|KerberosDelegationTokenAuthenticator
operator|.
name|TOKEN_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenToRenew
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Operation [{0}] requires the parameter [{1}]"
argument_list|,
name|dtOp
argument_list|,
name|KerberosDelegationTokenAuthenticator
operator|.
name|TOKEN_PARAM
argument_list|)
argument_list|)
expr_stmt|;
name|requestContinues
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|dt
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|dt
operator|.
name|decodeFromUrlString
argument_list|(
name|tokenToRenew
argument_list|)
expr_stmt|;
name|long
name|expirationTime
init|=
name|tokenManager
operator|.
name|renewToken
argument_list|(
name|dt
argument_list|,
name|requestUgi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
name|map
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"long"
argument_list|,
name|expirationTime
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
operator|new
name|AuthenticationException
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
break|break;
case|case
name|CANCELDELEGATIONTOKEN
case|:
name|String
name|tokenToCancel
init|=
name|ServletUtils
operator|.
name|getParameter
argument_list|(
name|request
argument_list|,
name|KerberosDelegationTokenAuthenticator
operator|.
name|TOKEN_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenToCancel
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Operation [{0}] requires the parameter [{1}]"
argument_list|,
name|dtOp
argument_list|,
name|KerberosDelegationTokenAuthenticator
operator|.
name|TOKEN_PARAM
argument_list|)
argument_list|)
expr_stmt|;
name|requestContinues
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|dt
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|dt
operator|.
name|decodeFromUrlString
argument_list|(
name|tokenToCancel
argument_list|)
expr_stmt|;
name|tokenManager
operator|.
name|cancelToken
argument_list|(
name|dt
argument_list|,
operator|(
name|requestUgi
operator|!=
literal|null
operator|)
condition|?
name|requestUgi
operator|.
name|getShortUserName
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
literal|"Invalid delegation token, cannot cancel"
argument_list|)
expr_stmt|;
name|requestContinues
operator|=
literal|false
expr_stmt|;
block|}
block|}
break|break;
block|}
if|if
condition|(
name|requestContinues
condition|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|ObjectMapper
name|jsonMapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|jsonMapper
operator|.
name|writeValue
argument_list|(
name|writer
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|ENTER
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|requestContinues
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Wrong HTTP method [{0}] for operation [{1}], it should be "
operator|+
literal|"[{2}]"
argument_list|,
name|request
operator|.
name|getMethod
argument_list|()
argument_list|,
name|dtOp
argument_list|,
name|dtOp
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|requestContinues
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|requestContinues
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|delegationTokenToJSON (Token token)
specifier|private
specifier|static
name|Map
name|delegationTokenToJSON
parameter_list|(
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
name|json
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KerberosDelegationTokenAuthenticator
operator|.
name|DELEGATION_TOKEN_URL_STRING_JSON
argument_list|,
name|token
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
name|Map
name|response
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
name|KerberosDelegationTokenAuthenticator
operator|.
name|DELEGATION_TOKEN_JSON
argument_list|,
name|json
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Authenticates a request looking for the<code>delegation</code>    * query-string parameter and verifying it is a valid token. If there is not    *<code>delegation</code> query-string parameter, it delegates the    * authentication to the {@link KerberosAuthenticationHandler} unless it is    * disabled.    *    * @param request the HTTP client request.    * @param response the HTTP client response.    * @return the authentication token for the authenticated request.    * @throws IOException thrown if an IO error occurred.    * @throws AuthenticationException thrown if the authentication failed.    */
annotation|@
name|Override
DECL|method|authenticate (HttpServletRequest request, HttpServletResponse response)
specifier|public
name|AuthenticationToken
name|authenticate
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|AuthenticationToken
name|token
decl_stmt|;
name|String
name|delegationParam
init|=
name|getDelegationToken
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|delegationParam
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|dt
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|dt
operator|.
name|decodeFromUrlString
argument_list|(
name|delegationParam
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|tokenManager
operator|.
name|verifyToken
argument_list|(
name|dt
argument_list|)
decl_stmt|;
specifier|final
name|String
name|shortName
init|=
name|ugi
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
comment|// creating a ephemeral token
name|token
operator|=
operator|new
name|AuthenticationToken
argument_list|(
name|shortName
argument_list|,
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|,
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setExpires
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAttribute
argument_list|(
name|DELEGATION_TOKEN_UGI_ATTRIBUTE
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|token
operator|=
literal|null
expr_stmt|;
name|HttpExceptionUtils
operator|.
name|createServletExceptionResponse
argument_list|(
name|response
argument_list|,
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
operator|new
name|AuthenticationException
argument_list|(
name|ex
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|token
operator|=
name|authHandler
operator|.
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
DECL|method|getDelegationToken (HttpServletRequest request)
specifier|private
name|String
name|getDelegationToken
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dToken
init|=
name|request
operator|.
name|getHeader
argument_list|(
name|DelegationTokenAuthenticator
operator|.
name|DELEGATION_TOKEN_HEADER
argument_list|)
decl_stmt|;
if|if
condition|(
name|dToken
operator|==
literal|null
condition|)
block|{
name|dToken
operator|=
name|ServletUtils
operator|.
name|getParameter
argument_list|(
name|request
argument_list|,
name|KerberosDelegationTokenAuthenticator
operator|.
name|DELEGATION_PARAM
argument_list|)
expr_stmt|;
block|}
return|return
name|dToken
return|;
block|}
block|}
end_class

end_unit

