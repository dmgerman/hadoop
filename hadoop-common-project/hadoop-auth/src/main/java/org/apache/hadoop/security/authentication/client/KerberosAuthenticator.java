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
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|HttpConstants
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
name|util
operator|.
name|AuthToken
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
name|util
operator|.
name|KerberosUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|Oid
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|AppConfigurationEntry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|security
operator|.
name|AccessControlContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedActionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|PlatformName
operator|.
name|IBM_JAVA
import|;
end_import

begin_comment
comment|/**  * The {@link KerberosAuthenticator} implements the Kerberos SPNEGO authentication sequence.  *<p>  * It uses the default principal for the Kerberos cache (normally set via kinit).  *<p>  * It falls back to the {@link PseudoAuthenticator} if the HTTP endpoint does not trigger an SPNEGO authentication  * sequence.  */
end_comment

begin_class
DECL|class|KerberosAuthenticator
specifier|public
class|class
name|KerberosAuthenticator
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
name|KerberosAuthenticator
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * HTTP header used by the SPNEGO server endpoint during an authentication sequence.    */
DECL|field|WWW_AUTHENTICATE
specifier|public
specifier|static
specifier|final
name|String
name|WWW_AUTHENTICATE
init|=
name|HttpConstants
operator|.
name|WWW_AUTHENTICATE_HEADER
decl_stmt|;
comment|/**    * HTTP header used by the SPNEGO client endpoint during an authentication sequence.    */
DECL|field|AUTHORIZATION
specifier|public
specifier|static
specifier|final
name|String
name|AUTHORIZATION
init|=
name|HttpConstants
operator|.
name|AUTHORIZATION_HEADER
decl_stmt|;
comment|/**    * HTTP header prefix used by the SPNEGO client/server endpoints during an authentication sequence.    */
DECL|field|NEGOTIATE
specifier|public
specifier|static
specifier|final
name|String
name|NEGOTIATE
init|=
name|HttpConstants
operator|.
name|NEGOTIATE
decl_stmt|;
DECL|field|AUTH_HTTP_METHOD
specifier|private
specifier|static
specifier|final
name|String
name|AUTH_HTTP_METHOD
init|=
literal|"OPTIONS"
decl_stmt|;
comment|/*   * Defines the Kerberos configuration that will be used to obtain the Kerberos principal from the   * Kerberos cache.   */
DECL|class|KerberosConfiguration
specifier|private
specifier|static
class|class
name|KerberosConfiguration
extends|extends
name|Configuration
block|{
DECL|field|OS_LOGIN_MODULE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|OS_LOGIN_MODULE_NAME
decl_stmt|;
DECL|field|windows
specifier|private
specifier|static
specifier|final
name|boolean
name|windows
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
decl_stmt|;
DECL|field|is64Bit
specifier|private
specifier|static
specifier|final
name|boolean
name|is64Bit
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.arch"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"64"
argument_list|)
decl_stmt|;
DECL|field|aix
specifier|private
specifier|static
specifier|final
name|boolean
name|aix
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"AIX"
argument_list|)
decl_stmt|;
comment|/* Return the OS login module class name */
DECL|method|getOSLoginModuleName ()
specifier|private
specifier|static
name|String
name|getOSLoginModuleName
parameter_list|()
block|{
if|if
condition|(
name|IBM_JAVA
condition|)
block|{
if|if
condition|(
name|windows
condition|)
block|{
return|return
name|is64Bit
condition|?
literal|"com.ibm.security.auth.module.Win64LoginModule"
else|:
literal|"com.ibm.security.auth.module.NTLoginModule"
return|;
block|}
elseif|else
if|if
condition|(
name|aix
condition|)
block|{
return|return
name|is64Bit
condition|?
literal|"com.ibm.security.auth.module.AIX64LoginModule"
else|:
literal|"com.ibm.security.auth.module.AIXLoginModule"
return|;
block|}
else|else
block|{
return|return
literal|"com.ibm.security.auth.module.LinuxLoginModule"
return|;
block|}
block|}
else|else
block|{
return|return
name|windows
condition|?
literal|"com.sun.security.auth.module.NTLoginModule"
else|:
literal|"com.sun.security.auth.module.UnixLoginModule"
return|;
block|}
block|}
static|static
block|{
name|OS_LOGIN_MODULE_NAME
operator|=
name|getOSLoginModuleName
argument_list|()
expr_stmt|;
block|}
DECL|field|OS_SPECIFIC_LOGIN
specifier|private
specifier|static
specifier|final
name|AppConfigurationEntry
name|OS_SPECIFIC_LOGIN
init|=
operator|new
name|AppConfigurationEntry
argument_list|(
name|OS_LOGIN_MODULE_NAME
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|USER_KERBEROS_OPTIONS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|USER_KERBEROS_OPTIONS
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
static|static
block|{
name|String
name|ticketCache
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"KRB5CCNAME"
argument_list|)
decl_stmt|;
if|if
condition|(
name|IBM_JAVA
condition|)
block|{
name|USER_KERBEROS_OPTIONS
operator|.
name|put
argument_list|(
literal|"useDefaultCcache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|USER_KERBEROS_OPTIONS
operator|.
name|put
argument_list|(
literal|"doNotPrompt"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|USER_KERBEROS_OPTIONS
operator|.
name|put
argument_list|(
literal|"useTicketCache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ticketCache
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|IBM_JAVA
condition|)
block|{
comment|// The first value searched when "useDefaultCcache" is used.
name|System
operator|.
name|setProperty
argument_list|(
literal|"KRB5CCNAME"
argument_list|,
name|ticketCache
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|USER_KERBEROS_OPTIONS
operator|.
name|put
argument_list|(
literal|"ticketCache"
argument_list|,
name|ticketCache
argument_list|)
expr_stmt|;
block|}
block|}
name|USER_KERBEROS_OPTIONS
operator|.
name|put
argument_list|(
literal|"renewTGT"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|field|USER_KERBEROS_LOGIN
specifier|private
specifier|static
specifier|final
name|AppConfigurationEntry
name|USER_KERBEROS_LOGIN
init|=
operator|new
name|AppConfigurationEntry
argument_list|(
name|KerberosUtil
operator|.
name|getKrb5LoginModuleName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|OPTIONAL
argument_list|,
name|USER_KERBEROS_OPTIONS
argument_list|)
decl_stmt|;
DECL|field|USER_KERBEROS_CONF
specifier|private
specifier|static
specifier|final
name|AppConfigurationEntry
index|[]
name|USER_KERBEROS_CONF
init|=
operator|new
name|AppConfigurationEntry
index|[]
block|{
name|OS_SPECIFIC_LOGIN
block|,
name|USER_KERBEROS_LOGIN
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|getAppConfigurationEntry (String appName)
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|appName
parameter_list|)
block|{
return|return
name|USER_KERBEROS_CONF
return|;
block|}
block|}
DECL|field|url
specifier|private
name|URL
name|url
decl_stmt|;
DECL|field|base64
specifier|private
name|Base64
name|base64
decl_stmt|;
DECL|field|connConfigurator
specifier|private
name|ConnectionConfigurator
name|connConfigurator
decl_stmt|;
comment|/**    * Sets a {@link ConnectionConfigurator} instance to use for    * configuring connections.    *    * @param configurator the {@link ConnectionConfigurator} instance.    */
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
name|connConfigurator
operator|=
name|configurator
expr_stmt|;
block|}
comment|/**    * Performs SPNEGO authentication against the specified URL.    *<p>    * If a token is given it does a NOP and returns the given token.    *<p>    * If no token is given, it will perform the SPNEGO authentication sequence using an    * HTTP<code>OPTIONS</code> request.    *    * @param url the URl to authenticate against.    * @param token the authentication token being used for the user.    *    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication error occurred.    */
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
name|token
operator|.
name|isSet
argument_list|()
condition|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|base64
operator|=
operator|new
name|Base64
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|HttpURLConnection
name|conn
init|=
name|token
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|connConfigurator
argument_list|)
decl_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|AUTH_HTTP_METHOD
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|boolean
name|needFallback
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|conn
operator|.
name|getResponseCode
argument_list|()
operator|==
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"JDK performed authentication on our behalf."
argument_list|)
expr_stmt|;
comment|// If the JDK already did the SPNEGO back-and-forth for
comment|// us, just pull out the token.
name|AuthenticatedURL
operator|.
name|extractToken
argument_list|(
name|conn
argument_list|,
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|isTokenKerberos
argument_list|(
name|token
argument_list|)
condition|)
block|{
return|return;
block|}
name|needFallback
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|needFallback
operator|&&
name|isNegotiate
argument_list|(
name|conn
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Performing our own SPNEGO sequence."
argument_list|)
expr_stmt|;
name|doSpnegoSequence
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using fallback authenticator sequence."
argument_list|)
expr_stmt|;
name|Authenticator
name|auth
init|=
name|getFallBackAuthenticator
argument_list|()
decl_stmt|;
comment|// Make sure that the fall back authenticator have the same
comment|// ConnectionConfigurator, since the method might be overridden.
comment|// Otherwise the fall back authenticator might not have the information
comment|// to make the connection (e.g., SSL certificates)
name|auth
operator|.
name|setConnectionConfigurator
argument_list|(
name|connConfigurator
argument_list|)
expr_stmt|;
name|auth
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
block|}
comment|/**    * If the specified URL does not support SPNEGO authentication, a fallback {@link Authenticator} will be used.    *<p>    * This implementation returns a {@link PseudoAuthenticator}.    *    * @return the fallback {@link Authenticator}.    */
DECL|method|getFallBackAuthenticator ()
specifier|protected
name|Authenticator
name|getFallBackAuthenticator
parameter_list|()
block|{
name|Authenticator
name|auth
init|=
operator|new
name|PseudoAuthenticator
argument_list|()
decl_stmt|;
if|if
condition|(
name|connConfigurator
operator|!=
literal|null
condition|)
block|{
name|auth
operator|.
name|setConnectionConfigurator
argument_list|(
name|connConfigurator
argument_list|)
expr_stmt|;
block|}
return|return
name|auth
return|;
block|}
comment|/*    * Check if the passed token is of type "kerberos" or "kerberos-dt"    */
DECL|method|isTokenKerberos (AuthenticatedURL.Token token)
specifier|private
name|boolean
name|isTokenKerberos
parameter_list|(
name|AuthenticatedURL
operator|.
name|Token
name|token
parameter_list|)
throws|throws
name|AuthenticationException
block|{
if|if
condition|(
name|token
operator|.
name|isSet
argument_list|()
condition|)
block|{
name|AuthToken
name|aToken
init|=
name|AuthToken
operator|.
name|parse
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aToken
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"kerberos"
argument_list|)
operator|||
name|aToken
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"kerberos-dt"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/*   * Indicates if the response is starting a SPNEGO negotiation.   */
DECL|method|isNegotiate (HttpURLConnection conn)
specifier|private
name|boolean
name|isNegotiate
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|negotiate
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|conn
operator|.
name|getResponseCode
argument_list|()
operator|==
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
condition|)
block|{
name|String
name|authHeader
init|=
name|conn
operator|.
name|getHeaderField
argument_list|(
name|WWW_AUTHENTICATE
argument_list|)
decl_stmt|;
name|negotiate
operator|=
name|authHeader
operator|!=
literal|null
operator|&&
name|authHeader
operator|.
name|trim
argument_list|()
operator|.
name|startsWith
argument_list|(
name|NEGOTIATE
argument_list|)
expr_stmt|;
block|}
return|return
name|negotiate
return|;
block|}
comment|/**    * Implements the SPNEGO authentication sequence interaction using the current default principal    * in the Kerberos cache (normally set via kinit).    *    * @param token the authentication token being used for the user.    *    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication error occurred.    */
DECL|method|doSpnegoSequence (final AuthenticatedURL.Token token)
specifier|private
name|void
name|doSpnegoSequence
parameter_list|(
specifier|final
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
try|try
block|{
name|AccessControlContext
name|context
init|=
name|AccessController
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|Subject
name|subject
init|=
name|Subject
operator|.
name|getSubject
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|subject
operator|==
literal|null
operator|||
operator|(
operator|!
name|KerberosUtil
operator|.
name|hasKerberosKeyTab
argument_list|(
name|subject
argument_list|)
operator|&&
operator|!
name|KerberosUtil
operator|.
name|hasKerberosTicket
argument_list|(
name|subject
argument_list|)
operator|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No subject in context, logging in"
argument_list|)
expr_stmt|;
name|subject
operator|=
operator|new
name|Subject
argument_list|()
expr_stmt|;
name|LoginContext
name|login
init|=
operator|new
name|LoginContext
argument_list|(
literal|""
argument_list|,
name|subject
argument_list|,
literal|null
argument_list|,
operator|new
name|KerberosConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|login
operator|.
name|login
argument_list|()
expr_stmt|;
block|}
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
literal|"Using subject: "
operator|+
name|subject
argument_list|)
expr_stmt|;
block|}
name|Subject
operator|.
name|doAs
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|GSSContext
name|gssContext
init|=
literal|null
decl_stmt|;
try|try
block|{
name|GSSManager
name|gssManager
init|=
name|GSSManager
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|String
name|servicePrincipal
init|=
name|KerberosUtil
operator|.
name|getServicePrincipal
argument_list|(
literal|"HTTP"
argument_list|,
name|KerberosAuthenticator
operator|.
name|this
operator|.
name|url
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
name|Oid
name|oid
init|=
name|KerberosUtil
operator|.
name|NT_GSS_KRB5_PRINCIPAL_OID
decl_stmt|;
name|GSSName
name|serviceName
init|=
name|gssManager
operator|.
name|createName
argument_list|(
name|servicePrincipal
argument_list|,
name|oid
argument_list|)
decl_stmt|;
name|oid
operator|=
name|KerberosUtil
operator|.
name|GSS_KRB5_MECH_OID
expr_stmt|;
name|gssContext
operator|=
name|gssManager
operator|.
name|createContext
argument_list|(
name|serviceName
argument_list|,
name|oid
argument_list|,
literal|null
argument_list|,
name|GSSContext
operator|.
name|DEFAULT_LIFETIME
argument_list|)
expr_stmt|;
name|gssContext
operator|.
name|requestCredDeleg
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|gssContext
operator|.
name|requestMutualAuth
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|byte
index|[]
name|inToken
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|byte
index|[]
name|outToken
decl_stmt|;
name|boolean
name|established
init|=
literal|false
decl_stmt|;
comment|// Loop while the context is still not established
while|while
condition|(
operator|!
name|established
condition|)
block|{
name|HttpURLConnection
name|conn
init|=
name|token
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|connConfigurator
argument_list|)
decl_stmt|;
name|outToken
operator|=
name|gssContext
operator|.
name|initSecContext
argument_list|(
name|inToken
argument_list|,
literal|0
argument_list|,
name|inToken
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|outToken
operator|!=
literal|null
condition|)
block|{
name|sendToken
argument_list|(
name|conn
argument_list|,
name|outToken
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|gssContext
operator|.
name|isEstablished
argument_list|()
condition|)
block|{
name|inToken
operator|=
name|readToken
argument_list|(
name|conn
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|established
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|gssContext
operator|!=
literal|null
condition|)
block|{
name|gssContext
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|gssContext
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PrivilegedActionException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getException
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
name|getException
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|ex
operator|.
name|getException
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|LoginException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/*   * Sends the Kerberos token to the server.   */
DECL|method|sendToken (HttpURLConnection conn, byte[] outToken)
specifier|private
name|void
name|sendToken
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|,
name|byte
index|[]
name|outToken
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|token
init|=
name|base64
operator|.
name|encodeToString
argument_list|(
name|outToken
argument_list|)
decl_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|AUTH_HTTP_METHOD
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
name|AUTHORIZATION
argument_list|,
name|NEGOTIATE
operator|+
literal|" "
operator|+
name|token
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
comment|/*   * Retrieves the Kerberos token returned by the server.   */
DECL|method|readToken (HttpURLConnection conn)
specifier|private
name|byte
index|[]
name|readToken
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|int
name|status
init|=
name|conn
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|HttpURLConnection
operator|.
name|HTTP_OK
operator|||
name|status
operator|==
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
condition|)
block|{
name|String
name|authHeader
init|=
name|conn
operator|.
name|getHeaderField
argument_list|(
name|WWW_AUTHENTICATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|authHeader
operator|==
literal|null
operator|||
operator|!
name|authHeader
operator|.
name|trim
argument_list|()
operator|.
name|startsWith
argument_list|(
name|NEGOTIATE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
literal|"Invalid SPNEGO sequence, '"
operator|+
name|WWW_AUTHENTICATE
operator|+
literal|"' header incorrect: "
operator|+
name|authHeader
argument_list|)
throw|;
block|}
name|String
name|negotiation
init|=
name|authHeader
operator|.
name|trim
argument_list|()
operator|.
name|substring
argument_list|(
operator|(
name|NEGOTIATE
operator|+
literal|" "
operator|)
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
return|return
name|base64
operator|.
name|decode
argument_list|(
name|negotiation
argument_list|)
return|;
block|}
throw|throw
operator|new
name|AuthenticationException
argument_list|(
literal|"Invalid SPNEGO sequence, status code: "
operator|+
name|status
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

