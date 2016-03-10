begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.client
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
name|client
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
name|server
operator|.
name|AuthenticationFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|URL
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
name|Map
import|;
end_import

begin_comment
comment|/**  * The {@link AuthenticatedURL} class enables the use of the JDK {@link URL} class  * against HTTP endpoints protected with the {@link AuthenticationFilter}.  *<p>  * The authentication mechanisms supported by default are Hadoop Simple  authentication  * (also known as pseudo authentication) and Kerberos SPNEGO authentication.  *<p>  * Additional authentication mechanisms can be supported via {@link Authenticator} implementations.  *<p>  * The default {@link Authenticator} is the {@link KerberosAuthenticator} class which supports  * automatic fallback from Kerberos SPNEGO to Hadoop Simple authentication.  *<p>  *<code>AuthenticatedURL</code> instances are not thread-safe.  *<p>  * The usage pattern of the {@link AuthenticatedURL} is:  *<pre>  *  * // establishing an initial connection  *  * URL url = new URL("http://foo:8080/bar");  * AuthenticatedURL.Token token = new AuthenticatedURL.Token();  * AuthenticatedURL aUrl = new AuthenticatedURL();  * HttpURLConnection conn = new AuthenticatedURL(url, token).openConnection();  * ....  * // use the 'conn' instance  * ....  *  * // establishing a follow up connection using a token from the previous connection  *  * HttpURLConnection conn = new AuthenticatedURL(url, token).openConnection();  * ....  * // use the 'conn' instance  * ....  *  *</pre>  */
end_comment

begin_class
DECL|class|AuthenticatedURL
specifier|public
class|class
name|AuthenticatedURL
block|{
comment|/**    * Name of the HTTP cookie used for the authentication token between the client and the server.    */
DECL|field|AUTH_COOKIE
specifier|public
specifier|static
specifier|final
name|String
name|AUTH_COOKIE
init|=
literal|"hadoop.auth"
decl_stmt|;
DECL|field|AUTH_COOKIE_EQ
specifier|private
specifier|static
specifier|final
name|String
name|AUTH_COOKIE_EQ
init|=
name|AUTH_COOKIE
operator|+
literal|"="
decl_stmt|;
comment|/**    * Client side authentication token.    */
DECL|class|Token
specifier|public
specifier|static
class|class
name|Token
block|{
DECL|field|token
specifier|private
name|String
name|token
decl_stmt|;
comment|/**      * Creates a token.      */
DECL|method|Token ()
specifier|public
name|Token
parameter_list|()
block|{     }
comment|/**      * Creates a token using an existing string representation of the token.      *      * @param tokenStr string representation of the tokenStr.      */
DECL|method|Token (String tokenStr)
specifier|public
name|Token
parameter_list|(
name|String
name|tokenStr
parameter_list|)
block|{
if|if
condition|(
name|tokenStr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"tokenStr cannot be null"
argument_list|)
throw|;
block|}
name|set
argument_list|(
name|tokenStr
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns if a token from the server has been set.      *      * @return if a token from the server has been set.      */
DECL|method|isSet ()
specifier|public
name|boolean
name|isSet
parameter_list|()
block|{
return|return
name|token
operator|!=
literal|null
return|;
block|}
comment|/**      * Sets a token.      *      * @param tokenStr string representation of the tokenStr.      */
DECL|method|set (String tokenStr)
name|void
name|set
parameter_list|(
name|String
name|tokenStr
parameter_list|)
block|{
name|token
operator|=
name|tokenStr
expr_stmt|;
block|}
comment|/**      * Returns the string representation of the token.      *      * @return the string representation of the token.      */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|token
return|;
block|}
block|}
DECL|field|DEFAULT_AUTHENTICATOR
specifier|private
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|Authenticator
argument_list|>
name|DEFAULT_AUTHENTICATOR
init|=
name|KerberosAuthenticator
operator|.
name|class
decl_stmt|;
comment|/**    * Sets the default {@link Authenticator} class to use when an {@link AuthenticatedURL} instance    * is created without specifying an authenticator.    *    * @param authenticator the authenticator class to use as default.    */
DECL|method|setDefaultAuthenticator (Class<? extends Authenticator> authenticator)
specifier|public
specifier|static
name|void
name|setDefaultAuthenticator
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Authenticator
argument_list|>
name|authenticator
parameter_list|)
block|{
name|DEFAULT_AUTHENTICATOR
operator|=
name|authenticator
expr_stmt|;
block|}
comment|/**    * Returns the default {@link Authenticator} class to use when an {@link AuthenticatedURL} instance    * is created without specifying an authenticator.    *    * @return the authenticator class to use as default.    */
DECL|method|getDefaultAuthenticator ()
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|Authenticator
argument_list|>
name|getDefaultAuthenticator
parameter_list|()
block|{
return|return
name|DEFAULT_AUTHENTICATOR
return|;
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
comment|/**    * Creates an {@link AuthenticatedURL}.    */
DECL|method|AuthenticatedURL ()
specifier|public
name|AuthenticatedURL
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an<code>AuthenticatedURL</code>.    *    * @param authenticator the {@link Authenticator} instance to use, if<code>null</code> a {@link    * KerberosAuthenticator} is used.    */
DECL|method|AuthenticatedURL (Authenticator authenticator)
specifier|public
name|AuthenticatedURL
parameter_list|(
name|Authenticator
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
comment|/**    * Creates an<code>AuthenticatedURL</code>.    *    * @param authenticator the {@link Authenticator} instance to use, if<code>null</code> a {@link    * KerberosAuthenticator} is used.    * @param connConfigurator a connection configurator.    */
DECL|method|AuthenticatedURL (Authenticator authenticator, ConnectionConfigurator connConfigurator)
specifier|public
name|AuthenticatedURL
parameter_list|(
name|Authenticator
name|authenticator
parameter_list|,
name|ConnectionConfigurator
name|connConfigurator
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|authenticator
operator|=
operator|(
name|authenticator
operator|!=
literal|null
operator|)
condition|?
name|authenticator
else|:
name|DEFAULT_AUTHENTICATOR
operator|.
name|newInstance
argument_list|()
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
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|this
operator|.
name|connConfigurator
operator|=
name|connConfigurator
expr_stmt|;
name|this
operator|.
name|authenticator
operator|.
name|setConnectionConfigurator
argument_list|(
name|connConfigurator
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the {@link Authenticator} instance used by the    *<code>AuthenticatedURL</code>.    *    * @return the {@link Authenticator} instance    */
DECL|method|getAuthenticator ()
specifier|protected
name|Authenticator
name|getAuthenticator
parameter_list|()
block|{
return|return
name|authenticator
return|;
block|}
comment|/**    * Returns an authenticated {@link HttpURLConnection}.    *    * @param url the URL to connect to. Only HTTP/S URLs are supported.    * @param token the authentication token being used for the user.    *    * @return an authenticated {@link HttpURLConnection}.    *    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
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
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"url cannot be NULL"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|url
operator|.
name|getProtocol
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"http"
argument_list|)
operator|&&
operator|!
name|url
operator|.
name|getProtocol
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"https"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"url must be for a HTTP or HTTPS resource"
argument_list|)
throw|;
block|}
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"token cannot be NULL"
argument_list|)
throw|;
block|}
name|authenticator
operator|.
name|authenticate
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|connConfigurator
operator|!=
literal|null
condition|)
block|{
name|conn
operator|=
name|connConfigurator
operator|.
name|configure
argument_list|(
name|conn
argument_list|)
expr_stmt|;
block|}
name|injectToken
argument_list|(
name|conn
argument_list|,
name|token
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
comment|/**    * Helper method that injects an authentication token to send with a connection.    *    * @param conn connection to inject the authentication token into.    * @param token authentication token to inject.    */
DECL|method|injectToken (HttpURLConnection conn, Token token)
specifier|public
specifier|static
name|void
name|injectToken
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|,
name|Token
name|token
parameter_list|)
block|{
name|String
name|t
init|=
name|token
operator|.
name|token
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|t
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
condition|)
block|{
name|t
operator|=
literal|"\""
operator|+
name|t
operator|+
literal|"\""
expr_stmt|;
block|}
name|conn
operator|.
name|addRequestProperty
argument_list|(
literal|"Cookie"
argument_list|,
name|AUTH_COOKIE_EQ
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Helper method that extracts an authentication token received from a connection.    *<p>    * This method is used by {@link Authenticator} implementations.    *    * @param conn connection to extract the authentication token from.    * @param token the authentication token.    *    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication exception occurred.    */
DECL|method|extractToken (HttpURLConnection conn, Token token)
specifier|public
specifier|static
name|void
name|extractToken
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|,
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|int
name|respCode
init|=
name|conn
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|respCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_OK
operator|||
name|respCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_CREATED
operator|||
name|respCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_ACCEPTED
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|headers
init|=
name|conn
operator|.
name|getHeaderFields
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|cookies
init|=
name|headers
operator|.
name|get
argument_list|(
literal|"Set-Cookie"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cookies
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|cookie
range|:
name|cookies
control|)
block|{
if|if
condition|(
name|cookie
operator|.
name|startsWith
argument_list|(
name|AUTH_COOKIE_EQ
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|cookie
operator|.
name|substring
argument_list|(
name|AUTH_COOKIE_EQ
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|separator
init|=
name|value
operator|.
name|indexOf
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
if|if
condition|(
name|separator
operator|>
operator|-
literal|1
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|separator
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|token
operator|.
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|respCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
condition|)
block|{
name|token
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|conn
operator|.
name|getURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
name|token
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AuthenticationException
argument_list|(
literal|"Authentication failed"
operator|+
literal|", URL: "
operator|+
name|conn
operator|.
name|getURL
argument_list|()
operator|+
literal|", status: "
operator|+
name|conn
operator|.
name|getResponseCode
argument_list|()
operator|+
literal|", message: "
operator|+
name|conn
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

