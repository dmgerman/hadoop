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
name|Constructor
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
name|URLEncoder
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
name|Map
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|KerberosAuthenticator
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineDelegationTokenResponse
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
name|security
operator|.
name|client
operator|.
name|TimelineDelegationTokenIdentifier
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
name|security
operator|.
name|client
operator|.
name|TimelineDelegationTokenOperation
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
name|security
operator|.
name|client
operator|.
name|TimelineAuthenticationConsts
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
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonNode
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

begin_comment
comment|/**  * A<code>KerberosAuthenticator</code> subclass that fallback to  * {@link TimelineAuthenticationConsts}.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineAuthenticator
specifier|public
class|class
name|TimelineAuthenticator
extends|extends
name|KerberosAuthenticator
block|{
DECL|field|mapper
specifier|private
specifier|static
name|ObjectMapper
name|mapper
decl_stmt|;
static|static
block|{
name|mapper
operator|=
operator|new
name|ObjectMapper
argument_list|()
expr_stmt|;
name|YarnJacksonJaxbJsonProvider
operator|.
name|configObjectMapper
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the fallback authenticator if the server does not use Kerberos    * SPNEGO HTTP authentication.    *     * @return a {@link TimelineAuthenticationConsts} instance.    */
annotation|@
name|Override
DECL|method|getFallBackAuthenticator ()
specifier|protected
name|Authenticator
name|getFallBackAuthenticator
parameter_list|()
block|{
return|return
operator|new
name|TimelineAuthenticator
argument_list|()
return|;
block|}
DECL|method|injectDelegationToken (Map<String, String> params, Token<?> dtToken)
specifier|public
specifier|static
name|void
name|injectDelegationToken
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|Token
argument_list|<
name|?
argument_list|>
name|dtToken
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dtToken
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|DELEGATION_PARAM
argument_list|,
name|dtToken
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|hasDelegationToken (URL url)
specifier|private
name|boolean
name|hasDelegationToken
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
return|return
name|url
operator|.
name|getQuery
argument_list|()
operator|.
name|contains
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|DELEGATION_PARAM
operator|+
literal|"="
argument_list|)
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
argument_list|)
condition|)
block|{
name|super
operator|.
name|authenticate
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDelegationToken ( URL url, AuthenticatedURL.Token token, String renewer)
specifier|public
specifier|static
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
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
block|{
name|TimelineDelegationTokenOperation
name|op
init|=
name|TimelineDelegationTokenOperation
operator|.
name|GETDELEGATIONTOKEN
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
name|TimelineAuthenticationConsts
operator|.
name|OP_PARAM
argument_list|,
name|op
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|RENEWER_PARAM
argument_list|,
name|renewer
argument_list|)
expr_stmt|;
name|url
operator|=
name|appendParams
argument_list|(
name|url
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|AuthenticatedURL
name|aUrl
init|=
operator|new
name|AuthenticatedURL
argument_list|(
operator|new
name|TimelineAuthenticator
argument_list|()
argument_list|)
decl_stmt|;
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
name|op
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineDelegationTokenResponse
name|dtRes
init|=
name|validateAndParseResponse
argument_list|(
name|conn
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dtRes
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|DELEGATION_TOKEN_URL
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The response content is not expected: "
operator|+
name|dtRes
operator|.
name|getContent
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|tokenStr
init|=
name|dtRes
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|dToken
init|=
operator|new
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
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
return|return
name|dToken
return|;
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
DECL|method|renewDelegationToken (URL url, AuthenticatedURL.Token token, Token<TimelineDelegationTokenIdentifier> dToken)
specifier|public
specifier|static
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
name|TimelineDelegationTokenIdentifier
argument_list|>
name|dToken
parameter_list|)
throws|throws
name|IOException
block|{
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
name|TimelineAuthenticationConsts
operator|.
name|OP_PARAM
argument_list|,
name|TimelineDelegationTokenOperation
operator|.
name|RENEWDELEGATIONTOKEN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|TOKEN_PARAM
argument_list|,
name|dToken
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
name|url
operator|=
name|appendParams
argument_list|(
name|url
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|AuthenticatedURL
name|aUrl
init|=
operator|new
name|AuthenticatedURL
argument_list|(
operator|new
name|TimelineAuthenticator
argument_list|()
argument_list|)
decl_stmt|;
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
name|TimelineDelegationTokenOperation
operator|.
name|RENEWDELEGATIONTOKEN
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineDelegationTokenResponse
name|dtRes
init|=
name|validateAndParseResponse
argument_list|(
name|conn
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dtRes
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|DELEGATION_TOKEN_EXPIRATION_TIME
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The response content is not expected: "
operator|+
name|dtRes
operator|.
name|getContent
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|dtRes
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
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
DECL|method|cancelDelegationToken (URL url, AuthenticatedURL.Token token, Token<TimelineDelegationTokenIdentifier> dToken)
specifier|public
specifier|static
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
name|TimelineDelegationTokenIdentifier
argument_list|>
name|dToken
parameter_list|)
throws|throws
name|IOException
block|{
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
name|TimelineAuthenticationConsts
operator|.
name|OP_PARAM
argument_list|,
name|TimelineDelegationTokenOperation
operator|.
name|CANCELDELEGATIONTOKEN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|TOKEN_PARAM
argument_list|,
name|dToken
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
name|url
operator|=
name|appendParams
argument_list|(
name|url
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|AuthenticatedURL
name|aUrl
init|=
operator|new
name|AuthenticatedURL
argument_list|(
operator|new
name|TimelineAuthenticator
argument_list|()
argument_list|)
decl_stmt|;
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
name|TimelineDelegationTokenOperation
operator|.
name|CANCELDELEGATIONTOKEN
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
expr_stmt|;
name|validateAndParseResponse
argument_list|(
name|conn
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
comment|/**    * Convenience method that appends parameters an HTTP<code>URL</code>.    *     * @param url    *          the url.    * @param params    *          the query string parameters.    *     * @return a<code>URL</code>    *     * @throws IOException    *           thrown if an IO error occurs.    */
DECL|method|appendParams (URL url, Map<String, String> params)
specifier|public
specifier|static
name|URL
name|appendParams
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|String
name|separator
init|=
name|url
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"?"
argument_list|)
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
return|return
operator|new
name|URL
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Validates the response of an<code>HttpURLConnection</code>. If the current    * status code is not 200, it will throw an exception with a detail message    * using Server side error messages if available. Otherwise,    * {@link TimelineDelegationTokenResponse} will be parsed and returned.    *     * @param conn    *          the<code>HttpURLConnection</code>.    * @return    * @throws IOException    *           thrown if the current status code is not 200 or the JSON response    *           cannot be parsed correctly    */
DECL|method|validateAndParseResponse ( HttpURLConnection conn)
specifier|private
specifier|static
name|TimelineDelegationTokenResponse
name|validateAndParseResponse
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|status
init|=
name|conn
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
name|JsonNode
name|json
init|=
name|mapper
operator|.
name|readTree
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
return|return
name|mapper
operator|.
name|readValue
argument_list|(
name|json
argument_list|,
name|TimelineDelegationTokenResponse
operator|.
name|class
argument_list|)
return|;
block|}
else|else
block|{
comment|// If the status code is not 200, some thing wrong should happen at the
comment|// server side, the JSON content is going to contain exception details.
comment|// We can use the JSON content to reconstruct the exception object.
try|try
block|{
name|String
name|message
init|=
name|json
operator|.
name|get
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|ERROR_MESSAGE_JSON
argument_list|)
operator|.
name|getTextValue
argument_list|()
decl_stmt|;
name|String
name|exception
init|=
name|json
operator|.
name|get
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|ERROR_EXCEPTION_JSON
argument_list|)
operator|.
name|getTextValue
argument_list|()
decl_stmt|;
name|String
name|className
init|=
name|json
operator|.
name|get
argument_list|(
name|TimelineAuthenticationConsts
operator|.
name|ERROR_CLASSNAME_JSON
argument_list|)
operator|.
name|getTextValue
argument_list|()
decl_stmt|;
try|try
block|{
name|ClassLoader
name|cl
init|=
name|TimelineAuthenticator
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|klass
init|=
name|cl
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
argument_list|>
name|constr
init|=
name|klass
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
throw|throw
operator|(
name|IOException
operator|)
name|constr
operator|.
name|newInstance
argument_list|(
name|message
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"{0} - {1}"
argument_list|,
name|exception
argument_list|,
name|message
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|ex
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"HTTP status [{0}], {1}"
argument_list|,
name|status
argument_list|,
name|conn
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

