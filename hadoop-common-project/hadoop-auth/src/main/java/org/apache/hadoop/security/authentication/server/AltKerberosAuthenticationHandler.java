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
name|Locale
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

begin_comment
comment|/**  * The {@link AltKerberosAuthenticationHandler} behaves exactly the same way as  * the {@link KerberosAuthenticationHandler}, except that it allows for an  * alternative form of authentication for browsers while still using Kerberos  * for Java access.  This is an abstract class that should be subclassed  * to allow a developer to implement their own custom authentication for browser  * access.  The alternateAuthenticate method will be called whenever a request  * comes from a browser.  */
end_comment

begin_class
DECL|class|AltKerberosAuthenticationHandler
specifier|public
specifier|abstract
class|class
name|AltKerberosAuthenticationHandler
extends|extends
name|KerberosAuthenticationHandler
block|{
comment|/**    * Constant that identifies the authentication mechanism.    */
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"alt-kerberos"
decl_stmt|;
comment|/**    * Constant for the configuration property that indicates which user agents    * are not considered browsers (comma separated)    */
DECL|field|NON_BROWSER_USER_AGENTS
specifier|public
specifier|static
specifier|final
name|String
name|NON_BROWSER_USER_AGENTS
init|=
name|TYPE
operator|+
literal|".non-browser.user-agents"
decl_stmt|;
DECL|field|NON_BROWSER_USER_AGENTS_DEFAULT
specifier|private
specifier|static
specifier|final
name|String
name|NON_BROWSER_USER_AGENTS_DEFAULT
init|=
literal|"java,curl,wget,perl"
decl_stmt|;
DECL|field|nonBrowserUserAgents
specifier|private
name|String
index|[]
name|nonBrowserUserAgents
decl_stmt|;
comment|/**    * Returns the authentication type of the authentication handler,    * 'alt-kerberos'.    *    * @return the authentication type of the authentication handler,    * 'alt-kerberos'.    */
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|TYPE
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
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|nonBrowserUserAgents
operator|=
name|config
operator|.
name|getProperty
argument_list|(
name|NON_BROWSER_USER_AGENTS
argument_list|,
name|NON_BROWSER_USER_AGENTS_DEFAULT
argument_list|)
operator|.
name|split
argument_list|(
literal|"\\W*,\\W*"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nonBrowserUserAgents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nonBrowserUserAgents
index|[
name|i
index|]
operator|=
name|nonBrowserUserAgents
index|[
name|i
index|]
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * It enforces the the Kerberos SPNEGO authentication sequence returning an    * {@link AuthenticationToken} only after the Kerberos SPNEGO sequence has    * completed successfully (in the case of Java access) and only after the    * custom authentication implemented by the subclass in alternateAuthenticate    * has completed successfully (in the case of browser access).    *    * @param request the HTTP client request.    * @param response the HTTP client response.    *    * @return an authentication token if the request is authorized or null    *    * @throws IOException thrown if an IO error occurred    * @throws AuthenticationException thrown if an authentication error occurred    */
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
if|if
condition|(
name|isBrowser
argument_list|(
name|request
operator|.
name|getHeader
argument_list|(
literal|"User-Agent"
argument_list|)
argument_list|)
condition|)
block|{
name|token
operator|=
name|alternateAuthenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|token
operator|=
name|super
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
comment|/**    * This method parses the User-Agent String and returns whether or not it    * refers to a browser.  If its not a browser, then Kerberos authentication    * will be used; if it is a browser, alternateAuthenticate from the subclass    * will be used.    *<p>    * A User-Agent String is considered to be a browser if it does not contain    * any of the values from alt-kerberos.non-browser.user-agents; the default    * behavior is to consider everything a browser unless it contains one of:    * "java", "curl", "wget", or "perl".  Subclasses can optionally override    * this method to use different behavior.    *    * @param userAgent The User-Agent String, or null if there isn't one    * @return true if the User-Agent String refers to a browser, false if not    */
DECL|method|isBrowser (String userAgent)
specifier|protected
name|boolean
name|isBrowser
parameter_list|(
name|String
name|userAgent
parameter_list|)
block|{
if|if
condition|(
name|userAgent
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|userAgent
operator|=
name|userAgent
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
name|boolean
name|isBrowser
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|nonBrowserUserAgent
range|:
name|nonBrowserUserAgents
control|)
block|{
if|if
condition|(
name|userAgent
operator|.
name|contains
argument_list|(
name|nonBrowserUserAgent
argument_list|)
condition|)
block|{
name|isBrowser
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
return|return
name|isBrowser
return|;
block|}
comment|/**    * Subclasses should implement this method to provide the custom    * authentication to be used for browsers.    *    * @param request the HTTP client request.    * @param response the HTTP client response.    * @return an authentication token if the request is authorized, or null    * @throws IOException thrown if an IO error occurs    * @throws AuthenticationException thrown if an authentication error occurs    */
DECL|method|alternateAuthenticate ( HttpServletRequest request, HttpServletResponse response)
specifier|public
specifier|abstract
name|AuthenticationToken
name|alternateAuthenticate
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
end_class

end_unit

