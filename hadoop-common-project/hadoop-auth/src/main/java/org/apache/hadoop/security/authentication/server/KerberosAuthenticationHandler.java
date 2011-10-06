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
name|KerberosAuthenticator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|security
operator|.
name|auth
operator|.
name|module
operator|.
name|Krb5LoginModule
import|;
end_import

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
name|util
operator|.
name|KerberosName
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
name|GSSCredential
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
name|kerberos
operator|.
name|KerberosPrincipal
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
name|File
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
name|security
operator|.
name|Principal
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
name|HashSet
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
comment|/**  * The {@link KerberosAuthenticationHandler} implements the Kerberos SPNEGO authentication mechanism for HTTP.  *<p/>  * The supported configuration properties are:  *<ul>  *<li>kerberos.principal: the Kerberos principal to used by the server. As stated by the Kerberos SPNEGO  * specification, it should be<code>HTTP/${HOSTNAME}@{REALM}</code>. The realm can be omitted from the  * principal as the JDK GSS libraries will use the realm name of the configured default realm.  * It does not have a default value.</li>  *<li>kerberos.keytab: the keytab file containing the credentials for the Kerberos principal.  * It does not have a default value.</li>  *</ul>  */
end_comment

begin_class
DECL|class|KerberosAuthenticationHandler
specifier|public
class|class
name|KerberosAuthenticationHandler
implements|implements
name|AuthenticationHandler
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
name|KerberosAuthenticationHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Kerberos context configuration for the JDK GSS library.    */
DECL|class|KerberosConfiguration
specifier|private
specifier|static
class|class
name|KerberosConfiguration
extends|extends
name|Configuration
block|{
DECL|field|keytab
specifier|private
name|String
name|keytab
decl_stmt|;
DECL|field|principal
specifier|private
name|String
name|principal
decl_stmt|;
DECL|method|KerberosConfiguration (String keytab, String principal)
specifier|public
name|KerberosConfiguration
parameter_list|(
name|String
name|keytab
parameter_list|,
name|String
name|principal
parameter_list|)
block|{
name|this
operator|.
name|keytab
operator|=
name|keytab
expr_stmt|;
name|this
operator|.
name|principal
operator|=
name|principal
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAppConfigurationEntry (String name)
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
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
name|options
operator|.
name|put
argument_list|(
literal|"keyTab"
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"principal"
argument_list|,
name|principal
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"useKeyTab"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"storeKey"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"doNotPrompt"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"useTicketCache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"renewTGT"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"refreshKrb5Config"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"isInitiator"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
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
name|ticketCache
operator|!=
literal|null
condition|)
block|{
name|options
operator|.
name|put
argument_list|(
literal|"ticketCache"
argument_list|,
name|ticketCache
argument_list|)
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
name|options
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|Krb5LoginModule
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|options
argument_list|)
block|,}
return|;
block|}
block|}
comment|/**    * Constant that identifies the authentication mechanism.    */
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"kerberos"
decl_stmt|;
comment|/**    * Constant for the configuration property that indicates the kerberos principal.    */
DECL|field|PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|PRINCIPAL
init|=
name|TYPE
operator|+
literal|".principal"
decl_stmt|;
comment|/**    * Constant for the configuration property that indicates the keytab file path.    */
DECL|field|KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|KEYTAB
init|=
name|TYPE
operator|+
literal|".keytab"
decl_stmt|;
comment|/**    * Constant for the configuration property that indicates the Kerberos name    * rules for the Kerberos principals.    */
DECL|field|NAME_RULES
specifier|public
specifier|static
specifier|final
name|String
name|NAME_RULES
init|=
name|TYPE
operator|+
literal|".name.rules"
decl_stmt|;
DECL|field|principal
specifier|private
name|String
name|principal
decl_stmt|;
DECL|field|keytab
specifier|private
name|String
name|keytab
decl_stmt|;
DECL|field|gssManager
specifier|private
name|GSSManager
name|gssManager
decl_stmt|;
DECL|field|loginContext
specifier|private
name|LoginContext
name|loginContext
decl_stmt|;
comment|/**    * Initializes the authentication handler instance.    *<p/>    * It creates a Kerberos context using the principal and keytab specified in the configuration.    *<p/>    * This method is invoked by the {@link AuthenticationFilter#init} method.    *    * @param config configuration properties to initialize the handler.    *    * @throws ServletException thrown if the handler could not be initialized.    */
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
try|try
block|{
name|principal
operator|=
name|config
operator|.
name|getProperty
argument_list|(
name|PRINCIPAL
argument_list|,
name|principal
argument_list|)
expr_stmt|;
if|if
condition|(
name|principal
operator|==
literal|null
operator|||
name|principal
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Principal not defined in configuration"
argument_list|)
throw|;
block|}
name|keytab
operator|=
name|config
operator|.
name|getProperty
argument_list|(
name|KEYTAB
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
if|if
condition|(
name|keytab
operator|==
literal|null
operator|||
name|keytab
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Keytab not defined in configuration"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|new
name|File
argument_list|(
name|keytab
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Keytab does not exist: "
operator|+
name|keytab
argument_list|)
throw|;
block|}
name|String
name|nameRules
init|=
name|config
operator|.
name|getProperty
argument_list|(
name|NAME_RULES
argument_list|,
literal|"DEFAULT"
argument_list|)
decl_stmt|;
name|KerberosName
operator|.
name|setRules
argument_list|(
name|nameRules
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
name|principal
argument_list|)
argument_list|)
expr_stmt|;
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|false
argument_list|,
name|principals
argument_list|,
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|KerberosConfiguration
name|kerberosConfiguration
init|=
operator|new
name|KerberosConfiguration
argument_list|(
name|keytab
argument_list|,
name|principal
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Login using keytab "
operator|+
name|keytab
operator|+
literal|", for principal "
operator|+
name|principal
argument_list|)
expr_stmt|;
name|loginContext
operator|=
operator|new
name|LoginContext
argument_list|(
literal|""
argument_list|,
name|subject
argument_list|,
literal|null
argument_list|,
name|kerberosConfiguration
argument_list|)
expr_stmt|;
name|loginContext
operator|.
name|login
argument_list|()
expr_stmt|;
name|Subject
name|serverSubject
init|=
name|loginContext
operator|.
name|getSubject
argument_list|()
decl_stmt|;
try|try
block|{
name|gssManager
operator|=
name|Subject
operator|.
name|doAs
argument_list|(
name|serverSubject
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|GSSManager
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|GSSManager
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|GSSManager
operator|.
name|getInstance
argument_list|()
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
throw|throw
name|ex
operator|.
name|getException
argument_list|()
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialized, principal [{}] from keytab [{}]"
argument_list|,
name|principal
argument_list|,
name|keytab
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
name|ServletException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Releases any resources initialized by the authentication handler.    *<p/>    * It destroys the Kerberos context.    */
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|loginContext
operator|!=
literal|null
condition|)
block|{
name|loginContext
operator|.
name|logout
argument_list|()
expr_stmt|;
name|loginContext
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|LoginException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the authentication type of the authentication handler, 'kerberos'.    *<p/>    *    * @return the authentication type of the authentication handler, 'kerberos'.    */
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
comment|/**    * Returns the Kerberos principal used by the authentication handler.    *    * @return the Kerberos principal used by the authentication handler.    */
DECL|method|getPrincipal ()
specifier|protected
name|String
name|getPrincipal
parameter_list|()
block|{
return|return
name|principal
return|;
block|}
comment|/**    * Returns the keytab used by the authentication handler.    *    * @return the keytab used by the authentication handler.    */
DECL|method|getKeytab ()
specifier|protected
name|String
name|getKeytab
parameter_list|()
block|{
return|return
name|keytab
return|;
block|}
comment|/**    * It enforces the the Kerberos SPNEGO authentication sequence returning an {@link AuthenticationToken} only    * after the Kerberos SPNEGO sequence has completed successfully.    *<p/>    *    * @param request the HTTP client request.    * @param response the HTTP client response.    *    * @return an authentication token if the Kerberos SPNEGO sequence is complete and valid,    *<code>null</code> if it is in progress (in this case the handler handles the response to the client).    *    * @throws IOException thrown if an IO error occurred.    * @throws AuthenticationException thrown if Kerberos SPNEGO sequence failed.    */
annotation|@
name|Override
DECL|method|authenticate (HttpServletRequest request, final HttpServletResponse response)
specifier|public
name|AuthenticationToken
name|authenticate
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
specifier|final
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
init|=
literal|null
decl_stmt|;
name|String
name|authorization
init|=
name|request
operator|.
name|getHeader
argument_list|(
name|KerberosAuthenticator
operator|.
name|AUTHORIZATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorization
operator|==
literal|null
operator|||
operator|!
name|authorization
operator|.
name|startsWith
argument_list|(
name|KerberosAuthenticator
operator|.
name|NEGOTIATE
argument_list|)
condition|)
block|{
name|response
operator|.
name|setHeader
argument_list|(
name|KerberosAuthenticator
operator|.
name|WWW_AUTHENTICATE
argument_list|,
name|KerberosAuthenticator
operator|.
name|NEGOTIATE
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|)
expr_stmt|;
if|if
condition|(
name|authorization
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"SPNEGO starting"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"'"
operator|+
name|KerberosAuthenticator
operator|.
name|AUTHORIZATION
operator|+
literal|"' does not start with '"
operator|+
name|KerberosAuthenticator
operator|.
name|NEGOTIATE
operator|+
literal|"' :  {}"
argument_list|,
name|authorization
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|authorization
operator|=
name|authorization
operator|.
name|substring
argument_list|(
name|KerberosAuthenticator
operator|.
name|NEGOTIATE
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
specifier|final
name|Base64
name|base64
init|=
operator|new
name|Base64
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|clientToken
init|=
name|base64
operator|.
name|decode
argument_list|(
name|authorization
argument_list|)
decl_stmt|;
name|Subject
name|serverSubject
init|=
name|loginContext
operator|.
name|getSubject
argument_list|()
decl_stmt|;
try|try
block|{
name|token
operator|=
name|Subject
operator|.
name|doAs
argument_list|(
name|serverSubject
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|AuthenticationToken
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AuthenticationToken
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthenticationToken
name|token
init|=
literal|null
decl_stmt|;
name|GSSContext
name|gssContext
init|=
literal|null
decl_stmt|;
try|try
block|{
name|gssContext
operator|=
name|gssManager
operator|.
name|createContext
argument_list|(
operator|(
name|GSSCredential
operator|)
literal|null
argument_list|)
expr_stmt|;
name|byte
index|[]
name|serverToken
init|=
name|gssContext
operator|.
name|acceptSecContext
argument_list|(
name|clientToken
argument_list|,
literal|0
argument_list|,
name|clientToken
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|serverToken
operator|!=
literal|null
operator|&&
name|serverToken
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|String
name|authenticate
init|=
name|base64
operator|.
name|encodeToString
argument_list|(
name|serverToken
argument_list|)
decl_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
name|KerberosAuthenticator
operator|.
name|WWW_AUTHENTICATE
argument_list|,
name|KerberosAuthenticator
operator|.
name|NEGOTIATE
operator|+
literal|" "
operator|+
name|authenticate
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
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"SPNEGO in progress"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|clientPrincipal
init|=
name|gssContext
operator|.
name|getSrcName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|KerberosName
name|kerberosName
init|=
operator|new
name|KerberosName
argument_list|(
name|clientPrincipal
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
name|kerberosName
operator|.
name|getShortName
argument_list|()
decl_stmt|;
name|token
operator|=
operator|new
name|AuthenticationToken
argument_list|(
name|userName
argument_list|,
name|clientPrincipal
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"SPNEGO completed for principal [{}]"
argument_list|,
name|clientPrincipal
argument_list|)
expr_stmt|;
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
block|}
block|}
return|return
name|token
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
block|}
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

