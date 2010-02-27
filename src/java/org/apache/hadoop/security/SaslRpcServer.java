begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|TreeMap
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|Callback
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
name|callback
operator|.
name|CallbackHandler
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
name|callback
operator|.
name|NameCallback
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
name|callback
operator|.
name|PasswordCallback
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
name|callback
operator|.
name|UnsupportedCallbackException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|AuthorizeCallback
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|RealmCallback
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|Sasl
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|ipc
operator|.
name|Server
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
name|SecretManager
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
name|SecretManager
operator|.
name|InvalidToken
import|;
end_import

begin_comment
comment|/**  * A utility class for dealing with SASL on RPC server  */
end_comment

begin_class
DECL|class|SaslRpcServer
specifier|public
class|class
name|SaslRpcServer
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SaslRpcServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SASL_DEFAULT_REALM
specifier|public
specifier|static
specifier|final
name|String
name|SASL_DEFAULT_REALM
init|=
literal|"default"
decl_stmt|;
DECL|field|SASL_PROPS
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|SASL_PROPS
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
comment|// Request authentication plus integrity protection
name|SASL_PROPS
operator|.
name|put
argument_list|(
name|Sasl
operator|.
name|QOP
argument_list|,
literal|"auth-int"
argument_list|)
expr_stmt|;
comment|// Request mutual authentication
name|SASL_PROPS
operator|.
name|put
argument_list|(
name|Sasl
operator|.
name|SERVER_AUTH
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|field|SWITCH_TO_SIMPLE_AUTH
specifier|public
specifier|static
specifier|final
name|int
name|SWITCH_TO_SIMPLE_AUTH
init|=
operator|-
literal|88
decl_stmt|;
DECL|method|encodeIdentifier (byte[] identifier)
specifier|static
name|String
name|encodeIdentifier
parameter_list|(
name|byte
index|[]
name|identifier
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|Base64
operator|.
name|encodeBase64
argument_list|(
name|identifier
argument_list|)
argument_list|)
return|;
block|}
DECL|method|decodeIdentifier (String identifier)
specifier|static
name|byte
index|[]
name|decodeIdentifier
parameter_list|(
name|String
name|identifier
parameter_list|)
block|{
return|return
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getIdentifier (String id, SecretManager<TokenIdentifier> secretManager)
specifier|public
specifier|static
name|TokenIdentifier
name|getIdentifier
parameter_list|(
name|String
name|id
parameter_list|,
name|SecretManager
argument_list|<
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|byte
index|[]
name|tokenId
init|=
name|decodeIdentifier
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|TokenIdentifier
name|tokenIdentifier
init|=
name|secretManager
operator|.
name|createIdentifier
argument_list|()
decl_stmt|;
try|try
block|{
name|tokenIdentifier
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|tokenId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|InvalidToken
operator|)
operator|new
name|InvalidToken
argument_list|(
literal|"Can't de-serialize tokenIdentifier"
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|tokenIdentifier
return|;
block|}
DECL|method|encodePassword (byte[] password)
specifier|static
name|char
index|[]
name|encodePassword
parameter_list|(
name|byte
index|[]
name|password
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|Base64
operator|.
name|encodeBase64
argument_list|(
name|password
argument_list|)
argument_list|)
operator|.
name|toCharArray
argument_list|()
return|;
block|}
comment|/** Splitting fully qualified Kerberos name into parts */
DECL|method|splitKerberosName (String fullName)
specifier|public
specifier|static
name|String
index|[]
name|splitKerberosName
parameter_list|(
name|String
name|fullName
parameter_list|)
block|{
return|return
name|fullName
operator|.
name|split
argument_list|(
literal|"[/@]"
argument_list|)
return|;
block|}
DECL|enum|SaslStatus
specifier|public
enum|enum
name|SaslStatus
block|{
DECL|enumConstant|SUCCESS
name|SUCCESS
argument_list|(
literal|0
argument_list|)
block|,
DECL|enumConstant|ERROR
name|ERROR
argument_list|(
literal|1
argument_list|)
block|;
DECL|field|state
specifier|public
specifier|final
name|int
name|state
decl_stmt|;
DECL|method|SaslStatus (int state)
specifier|private
name|SaslStatus
parameter_list|(
name|int
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
block|}
comment|/** Authentication method */
DECL|enum|AuthMethod
specifier|public
specifier|static
enum|enum
name|AuthMethod
block|{
DECL|enumConstant|SIMPLE
name|SIMPLE
argument_list|(
operator|(
name|byte
operator|)
literal|80
argument_list|,
literal|""
argument_list|)
block|,
comment|// no authentication
DECL|enumConstant|KERBEROS
name|KERBEROS
argument_list|(
operator|(
name|byte
operator|)
literal|81
argument_list|,
literal|"GSSAPI"
argument_list|)
block|,
comment|// SASL Kerberos authentication
DECL|enumConstant|DIGEST
name|DIGEST
argument_list|(
operator|(
name|byte
operator|)
literal|82
argument_list|,
literal|"DIGEST-MD5"
argument_list|)
block|;
comment|// SASL DIGEST-MD5 authentication
comment|/** The code for this method. */
DECL|field|code
specifier|public
specifier|final
name|byte
name|code
decl_stmt|;
DECL|field|mechanismName
specifier|public
specifier|final
name|String
name|mechanismName
decl_stmt|;
DECL|method|AuthMethod (byte code, String mechanismName)
specifier|private
name|AuthMethod
parameter_list|(
name|byte
name|code
parameter_list|,
name|String
name|mechanismName
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
name|this
operator|.
name|mechanismName
operator|=
name|mechanismName
expr_stmt|;
block|}
DECL|field|FIRST_CODE
specifier|private
specifier|static
specifier|final
name|int
name|FIRST_CODE
init|=
name|values
argument_list|()
index|[
literal|0
index|]
operator|.
name|code
decl_stmt|;
comment|/** Return the object represented by the code. */
DECL|method|valueOf (byte code)
specifier|private
specifier|static
name|AuthMethod
name|valueOf
parameter_list|(
name|byte
name|code
parameter_list|)
block|{
specifier|final
name|int
name|i
init|=
operator|(
name|code
operator|&
literal|0xff
operator|)
operator|-
name|FIRST_CODE
decl_stmt|;
return|return
name|i
operator|<
literal|0
operator|||
name|i
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|?
literal|null
else|:
name|values
argument_list|()
index|[
name|i
index|]
return|;
block|}
comment|/** Return the SASL mechanism name */
DECL|method|getMechanismName ()
specifier|public
name|String
name|getMechanismName
parameter_list|()
block|{
return|return
name|mechanismName
return|;
block|}
comment|/** Read from in */
DECL|method|read (DataInput in)
specifier|public
specifier|static
name|AuthMethod
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|valueOf
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
return|;
block|}
comment|/** Write to out */
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|code
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
comment|/** CallbackHandler for SASL DIGEST-MD5 mechanism */
DECL|class|SaslDigestCallbackHandler
specifier|public
specifier|static
class|class
name|SaslDigestCallbackHandler
implements|implements
name|CallbackHandler
block|{
DECL|field|secretManager
specifier|private
name|SecretManager
argument_list|<
name|TokenIdentifier
argument_list|>
name|secretManager
decl_stmt|;
DECL|field|connection
specifier|private
name|Server
operator|.
name|Connection
name|connection
decl_stmt|;
DECL|method|SaslDigestCallbackHandler ( SecretManager<TokenIdentifier> secretManager, Server.Connection connection)
specifier|public
name|SaslDigestCallbackHandler
parameter_list|(
name|SecretManager
argument_list|<
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|,
name|Server
operator|.
name|Connection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|secretManager
operator|=
name|secretManager
expr_stmt|;
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
DECL|method|getPassword (TokenIdentifier tokenid)
specifier|private
name|char
index|[]
name|getPassword
parameter_list|(
name|TokenIdentifier
name|tokenid
parameter_list|)
throws|throws
name|InvalidToken
block|{
return|return
name|encodePassword
argument_list|(
name|secretManager
operator|.
name|retrievePassword
argument_list|(
name|tokenid
argument_list|)
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|handle (Callback[] callbacks)
specifier|public
name|void
name|handle
parameter_list|(
name|Callback
index|[]
name|callbacks
parameter_list|)
throws|throws
name|InvalidToken
throws|,
name|UnsupportedCallbackException
block|{
name|NameCallback
name|nc
init|=
literal|null
decl_stmt|;
name|PasswordCallback
name|pc
init|=
literal|null
decl_stmt|;
name|AuthorizeCallback
name|ac
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Callback
name|callback
range|:
name|callbacks
control|)
block|{
if|if
condition|(
name|callback
operator|instanceof
name|AuthorizeCallback
condition|)
block|{
name|ac
operator|=
operator|(
name|AuthorizeCallback
operator|)
name|callback
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|callback
operator|instanceof
name|NameCallback
condition|)
block|{
name|nc
operator|=
operator|(
name|NameCallback
operator|)
name|callback
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|callback
operator|instanceof
name|PasswordCallback
condition|)
block|{
name|pc
operator|=
operator|(
name|PasswordCallback
operator|)
name|callback
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|callback
operator|instanceof
name|RealmCallback
condition|)
block|{
continue|continue;
comment|// realm is ignored
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedCallbackException
argument_list|(
name|callback
argument_list|,
literal|"Unrecognized SASL DIGEST-MD5 Callback"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|pc
operator|!=
literal|null
condition|)
block|{
name|TokenIdentifier
name|tokenIdentifier
init|=
name|getIdentifier
argument_list|(
name|nc
operator|.
name|getDefaultName
argument_list|()
argument_list|,
name|secretManager
argument_list|)
decl_stmt|;
name|char
index|[]
name|password
init|=
name|getPassword
argument_list|(
name|tokenIdentifier
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|user
init|=
literal|null
decl_stmt|;
name|user
operator|=
name|tokenIdentifier
operator|.
name|getUser
argument_list|()
expr_stmt|;
comment|// may throw exception
name|connection
operator|.
name|attemptingUser
operator|=
name|user
expr_stmt|;
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
literal|"SASL server DIGEST-MD5 callback: setting password "
operator|+
literal|"for client: "
operator|+
name|tokenIdentifier
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pc
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ac
operator|!=
literal|null
condition|)
block|{
name|String
name|authid
init|=
name|ac
operator|.
name|getAuthenticationID
argument_list|()
decl_stmt|;
name|String
name|authzid
init|=
name|ac
operator|.
name|getAuthorizationID
argument_list|()
decl_stmt|;
if|if
condition|(
name|authid
operator|.
name|equals
argument_list|(
name|authzid
argument_list|)
condition|)
block|{
name|ac
operator|.
name|setAuthorized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ac
operator|.
name|setAuthorized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ac
operator|.
name|isAuthorized
argument_list|()
condition|)
block|{
name|String
name|username
init|=
name|getIdentifier
argument_list|(
name|authzid
argument_list|,
name|secretManager
argument_list|)
operator|.
name|getUser
argument_list|()
operator|.
name|getUserName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"SASL server DIGEST-MD5 callback: setting "
operator|+
literal|"canonicalized client ID: "
operator|+
name|username
argument_list|)
expr_stmt|;
name|ac
operator|.
name|setAuthorizedID
argument_list|(
name|authzid
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** CallbackHandler for SASL GSSAPI Kerberos mechanism */
DECL|class|SaslGssCallbackHandler
specifier|public
specifier|static
class|class
name|SaslGssCallbackHandler
implements|implements
name|CallbackHandler
block|{
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|handle (Callback[] callbacks)
specifier|public
name|void
name|handle
parameter_list|(
name|Callback
index|[]
name|callbacks
parameter_list|)
throws|throws
name|UnsupportedCallbackException
block|{
name|AuthorizeCallback
name|ac
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Callback
name|callback
range|:
name|callbacks
control|)
block|{
if|if
condition|(
name|callback
operator|instanceof
name|AuthorizeCallback
condition|)
block|{
name|ac
operator|=
operator|(
name|AuthorizeCallback
operator|)
name|callback
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedCallbackException
argument_list|(
name|callback
argument_list|,
literal|"Unrecognized SASL GSSAPI Callback"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|ac
operator|!=
literal|null
condition|)
block|{
name|String
name|authid
init|=
name|ac
operator|.
name|getAuthenticationID
argument_list|()
decl_stmt|;
name|String
name|authzid
init|=
name|ac
operator|.
name|getAuthorizationID
argument_list|()
decl_stmt|;
if|if
condition|(
name|authid
operator|.
name|equals
argument_list|(
name|authzid
argument_list|)
condition|)
block|{
name|ac
operator|.
name|setAuthorized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ac
operator|.
name|setAuthorized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ac
operator|.
name|isAuthorized
argument_list|()
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"SASL server GSSAPI callback: setting "
operator|+
literal|"canonicalized client ID: "
operator|+
name|authzid
argument_list|)
expr_stmt|;
name|ac
operator|.
name|setAuthorizedID
argument_list|(
name|authzid
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

