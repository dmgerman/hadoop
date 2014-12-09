begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.server
package|package
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
name|PseudoAuthenticator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|utils
operator|.
name|URLEncodedUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|NameValuePair
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * The<code>PseudoAuthenticationHandler</code> provides a pseudo authentication mechanism that accepts  * the user name specified as a query string parameter.  *<p>  * This mimics the model of Hadoop Simple authentication which trust the 'user.name' property provided in  * the configuration object.  *<p>  * This handler can be configured to support anonymous users.  *<p>  * The only supported configuration property is:  *<ul>  *<li>simple.anonymous.allowed:<code>true|false</code>, default value is<code>false</code></li>  *</ul>  */
end_comment

begin_class
DECL|class|PseudoAuthenticationHandler
specifier|public
class|class
name|PseudoAuthenticationHandler
implements|implements
name|AuthenticationHandler
block|{
comment|/**    * Constant that identifies the authentication mechanism.    */
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"simple"
decl_stmt|;
comment|/**    * Constant for the configuration property that indicates if anonymous users are allowed.    */
DECL|field|ANONYMOUS_ALLOWED
specifier|public
specifier|static
specifier|final
name|String
name|ANONYMOUS_ALLOWED
init|=
name|TYPE
operator|+
literal|".anonymous.allowed"
decl_stmt|;
DECL|field|UTF8_CHARSET
specifier|private
specifier|static
specifier|final
name|Charset
name|UTF8_CHARSET
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|field|PSEUDO_AUTH
specifier|private
specifier|static
specifier|final
name|String
name|PSEUDO_AUTH
init|=
literal|"PseudoAuth"
decl_stmt|;
DECL|field|acceptAnonymous
specifier|private
name|boolean
name|acceptAnonymous
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
comment|/**    * Creates a Hadoop pseudo authentication handler with the default auth-token    * type,<code>simple</code>.    */
DECL|method|PseudoAuthenticationHandler ()
specifier|public
name|PseudoAuthenticationHandler
parameter_list|()
block|{
name|this
argument_list|(
name|TYPE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a Hadoop pseudo authentication handler with a custom auth-token    * type.    *    * @param type auth-token type.    */
DECL|method|PseudoAuthenticationHandler (String type)
specifier|public
name|PseudoAuthenticationHandler
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**    * Initializes the authentication handler instance.    *<p>    * This method is invoked by the {@link AuthenticationFilter#init} method.    *    * @param config configuration properties to initialize the handler.    *    * @throws ServletException thrown if the handler could not be initialized.    */
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
name|acceptAnonymous
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|config
operator|.
name|getProperty
argument_list|(
name|ANONYMOUS_ALLOWED
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns if the handler is configured to support anonymous users.    *    * @return if the handler is configured to support anonymous users.    */
DECL|method|getAcceptAnonymous ()
specifier|protected
name|boolean
name|getAcceptAnonymous
parameter_list|()
block|{
return|return
name|acceptAnonymous
return|;
block|}
comment|/**    * Releases any resources initialized by the authentication handler.    *<p>    * This implementation does a NOP.    */
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{   }
comment|/**    * Returns the authentication type of the authentication handler, 'simple'.    *    * @return the authentication type of the authentication handler, 'simple'.    */
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * This is an empty implementation, it always returns<code>TRUE</code>.    *    *    *    * @param token the authentication token if any, otherwise<code>NULL</code>.    * @param request the HTTP client request.    * @param response the HTTP client response.    *    * @return<code>TRUE</code>    * @throws IOException it is never thrown.    * @throws AuthenticationException it is never thrown.    */
annotation|@
name|Override
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
return|return
literal|true
return|;
block|}
DECL|method|getUserName (HttpServletRequest request)
specifier|private
name|String
name|getUserName
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|list
init|=
name|URLEncodedUtils
operator|.
name|parse
argument_list|(
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|UTF8_CHARSET
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|NameValuePair
name|nv
range|:
name|list
control|)
block|{
if|if
condition|(
name|PseudoAuthenticator
operator|.
name|USER_NAME
operator|.
name|equals
argument_list|(
name|nv
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|nv
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Authenticates an HTTP client request.    *<p>    * It extracts the {@link PseudoAuthenticator#USER_NAME} parameter from the query string and creates    * an {@link AuthenticationToken} with it.    *<p>    * If the HTTP client request does not contain the {@link PseudoAuthenticator#USER_NAME} parameter and    * the handler is configured to allow anonymous users it returns the {@link AuthenticationToken#ANONYMOUS}    * token.    *<p>    * If the HTTP client request does not contain the {@link PseudoAuthenticator#USER_NAME} parameter and    * the handler is configured to disallow anonymous users it throws an {@link AuthenticationException}.    *    * @param request the HTTP client request.    * @param response the HTTP client response.    *    * @return an authentication token if the HTTP client request is accepted and credentials are valid.    *    * @throws IOException thrown if an IO error occurred.    * @throws AuthenticationException thrown if HTTP client request was not accepted as an authentication request.    */
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
name|userName
init|=
name|getUserName
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|userName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|getAcceptAnonymous
argument_list|()
condition|)
block|{
name|token
operator|=
name|AuthenticationToken
operator|.
name|ANONYMOUS
expr_stmt|;
block|}
else|else
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
name|WWW_AUTHENTICATE
argument_list|,
name|PSEUDO_AUTH
argument_list|)
expr_stmt|;
name|token
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|token
operator|=
operator|new
name|AuthenticationToken
argument_list|(
name|userName
argument_list|,
name|userName
argument_list|,
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

