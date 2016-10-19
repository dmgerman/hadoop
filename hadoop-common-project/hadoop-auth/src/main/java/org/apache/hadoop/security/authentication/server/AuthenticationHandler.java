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
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Interface for server authentication mechanisms.  * The {@link AuthenticationFilter} manages the lifecycle of the authentication handler.  * Implementations must be thread-safe as one instance is initialized and used for all requests.  */
end_comment

begin_interface
DECL|interface|AuthenticationHandler
specifier|public
interface|interface
name|AuthenticationHandler
block|{
DECL|field|WWW_AUTHENTICATE
name|String
name|WWW_AUTHENTICATE
init|=
name|HttpConstants
operator|.
name|WWW_AUTHENTICATE_HEADER
decl_stmt|;
comment|/**    * Returns the authentication type of the authentication handler.    * This should be a name that uniquely identifies the authentication type.    * For example 'simple' or 'kerberos'.    *    * @return the authentication type of the authentication handler.    */
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
function_decl|;
comment|/**    * Initializes the authentication handler instance.    *<p>    * This method is invoked by the {@link AuthenticationFilter#init} method.    *    * @param config configuration properties to initialize the handler.    *    * @throws ServletException thrown if the handler could not be initialized.    */
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
function_decl|;
comment|/**    * Destroys the authentication handler instance.    *<p>    * This method is invoked by the {@link AuthenticationFilter#destroy} method.    */
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
function_decl|;
comment|/**    * Performs an authentication management operation.    *<p>    * This is useful for handling operations like get/renew/cancel    * delegation tokens which are being handled as operations of the    * service end-point.    *<p>    * If the method returns<code>TRUE</code> the request will continue normal    * processing, this means the method has not produced any HTTP response.    *<p>    * If the method returns<code>FALSE</code> the request will end, this means     * the method has produced the corresponding HTTP response.    *    * @param token the authentication token if any, otherwise<code>NULL</code>.    * @param request the HTTP client request.    * @param response the HTTP client response.    * @return<code>TRUE</code> if the request should be processed as a regular    * request,    *<code>FALSE</code> otherwise.    *    * @throws IOException thrown if an IO error occurred.    * @throws AuthenticationException thrown if an Authentication error occurred.    */
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
function_decl|;
comment|/**    * Performs an authentication step for the given HTTP client request.    *<p>    * This method is invoked by the {@link AuthenticationFilter} only if the HTTP client request is    * not yet authenticated.    *<p>    * Depending upon the authentication mechanism being implemented, a particular HTTP client may    * end up making a sequence of invocations before authentication is successfully established (this is    * the case of Kerberos SPNEGO).    *<p>    * This method must return an {@link AuthenticationToken} only if the the HTTP client request has    * been successfully and fully authenticated.    *<p>    * If the HTTP client request has not been completely authenticated, this method must take over    * the corresponding HTTP response and it must return<code>null</code>.    *    * @param request the HTTP client request.    * @param response the HTTP client response.    *    * @return an {@link AuthenticationToken} if the HTTP client request has been authenticated,    *<code>null</code> otherwise (in this case it must take care of the response).    *    * @throws IOException thrown if an IO error occurred.    * @throws AuthenticationException thrown if an Authentication error occurred.    */
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
function_decl|;
block|}
end_interface

end_unit

