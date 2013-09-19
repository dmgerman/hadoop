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
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Security
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
name|TreeMap
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
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslException
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
name|SaslServer
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
name|ipc
operator|.
name|Server
operator|.
name|Connection
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
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
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
DECL|enum|QualityOfProtection
specifier|public
specifier|static
enum|enum
name|QualityOfProtection
block|{
DECL|enumConstant|AUTHENTICATION
name|AUTHENTICATION
argument_list|(
literal|"auth"
argument_list|)
block|,
DECL|enumConstant|INTEGRITY
name|INTEGRITY
argument_list|(
literal|"auth-int"
argument_list|)
block|,
DECL|enumConstant|PRIVACY
name|PRIVACY
argument_list|(
literal|"auth-conf"
argument_list|)
block|;
DECL|field|saslQop
specifier|public
specifier|final
name|String
name|saslQop
decl_stmt|;
DECL|method|QualityOfProtection (String saslQop)
specifier|private
name|QualityOfProtection
parameter_list|(
name|String
name|saslQop
parameter_list|)
block|{
name|this
operator|.
name|saslQop
operator|=
name|saslQop
expr_stmt|;
block|}
DECL|method|getSaslQop ()
specifier|public
name|String
name|getSaslQop
parameter_list|()
block|{
return|return
name|saslQop
return|;
block|}
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|authMethod
specifier|public
name|AuthMethod
name|authMethod
decl_stmt|;
DECL|field|mechanism
specifier|public
name|String
name|mechanism
decl_stmt|;
DECL|field|protocol
specifier|public
name|String
name|protocol
decl_stmt|;
DECL|field|serverId
specifier|public
name|String
name|serverId
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|SaslRpcServer (AuthMethod authMethod)
specifier|public
name|SaslRpcServer
parameter_list|(
name|AuthMethod
name|authMethod
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|authMethod
operator|=
name|authMethod
expr_stmt|;
name|mechanism
operator|=
name|authMethod
operator|.
name|getMechanismName
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|authMethod
condition|)
block|{
case|case
name|SIMPLE
case|:
block|{
return|return;
comment|// no sasl for simple
block|}
case|case
name|TOKEN
case|:
block|{
name|protocol
operator|=
literal|""
expr_stmt|;
name|serverId
operator|=
name|SaslRpcServer
operator|.
name|SASL_DEFAULT_REALM
expr_stmt|;
break|break;
block|}
case|case
name|KERBEROS
case|:
block|{
name|String
name|fullName
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
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
literal|"Kerberos principal name is "
operator|+
name|fullName
argument_list|)
expr_stmt|;
comment|// don't use KerberosName because we don't want auth_to_local
name|String
index|[]
name|parts
init|=
name|fullName
operator|.
name|split
argument_list|(
literal|"[/@]"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|protocol
operator|=
name|parts
index|[
literal|0
index|]
expr_stmt|;
comment|// should verify service host is present here rather than in create()
comment|// but lazy tests are using a UGI that isn't a SPN...
name|serverId
operator|=
operator|(
name|parts
operator|.
name|length
operator|<
literal|2
operator|)
condition|?
literal|""
else|:
name|parts
index|[
literal|1
index|]
expr_stmt|;
break|break;
block|}
default|default:
comment|// we should never be able to get here
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Server does not support SASL "
operator|+
name|authMethod
argument_list|)
throw|;
block|}
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|create (Connection connection, SecretManager<TokenIdentifier> secretManager )
specifier|public
name|SaslServer
name|create
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|SecretManager
argument_list|<
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
specifier|final
name|CallbackHandler
name|callback
decl_stmt|;
switch|switch
condition|(
name|authMethod
condition|)
block|{
case|case
name|TOKEN
case|:
block|{
name|callback
operator|=
operator|new
name|SaslDigestCallbackHandler
argument_list|(
name|secretManager
argument_list|,
name|connection
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|KERBEROS
case|:
block|{
if|if
condition|(
name|serverId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Kerberos principal name does NOT have the expected "
operator|+
literal|"hostname part: "
operator|+
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|)
throw|;
block|}
name|callback
operator|=
operator|new
name|SaslGssCallbackHandler
argument_list|()
expr_stmt|;
break|break;
block|}
default|default:
comment|// we should never be able to get here
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Server does not support SASL "
operator|+
name|authMethod
argument_list|)
throw|;
block|}
name|SaslServer
name|saslServer
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|SaslServer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SaslServer
name|run
parameter_list|()
throws|throws
name|SaslException
block|{
return|return
name|Sasl
operator|.
name|createSaslServer
argument_list|(
name|mechanism
argument_list|,
name|protocol
argument_list|,
name|serverId
argument_list|,
name|SaslRpcServer
operator|.
name|SASL_PROPS
argument_list|,
name|callback
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|saslServer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unable to find SASL server implementation for "
operator|+
name|mechanism
argument_list|)
throw|;
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
literal|"Created SASL server with mechanism = "
operator|+
name|mechanism
argument_list|)
expr_stmt|;
block|}
return|return
name|saslServer
return|;
block|}
DECL|method|init (Configuration conf)
specifier|public
specifier|static
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|QualityOfProtection
name|saslQOP
init|=
name|QualityOfProtection
operator|.
name|AUTHENTICATION
decl_stmt|;
name|String
name|rpcProtection
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"hadoop.rpc.protection"
argument_list|,
name|QualityOfProtection
operator|.
name|AUTHENTICATION
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|QualityOfProtection
operator|.
name|INTEGRITY
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
name|rpcProtection
argument_list|)
condition|)
block|{
name|saslQOP
operator|=
name|QualityOfProtection
operator|.
name|INTEGRITY
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|QualityOfProtection
operator|.
name|PRIVACY
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
name|rpcProtection
argument_list|)
condition|)
block|{
name|saslQOP
operator|=
name|QualityOfProtection
operator|.
name|PRIVACY
expr_stmt|;
block|}
name|SASL_PROPS
operator|.
name|put
argument_list|(
name|Sasl
operator|.
name|QOP
argument_list|,
name|saslQOP
operator|.
name|getSaslQop
argument_list|()
argument_list|)
expr_stmt|;
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
name|Security
operator|.
name|addProvider
argument_list|(
operator|new
name|SaslPlainServer
operator|.
name|SecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
DECL|method|getIdentifier (String id, SecretManager<T> secretManager)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
name|T
name|getIdentifier
parameter_list|(
name|String
name|id
parameter_list|,
name|SecretManager
argument_list|<
name|T
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
name|T
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
comment|/** Authentication method */
annotation|@
name|InterfaceStability
operator|.
name|Evolving
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
DECL|enumConstant|Deprecated
annotation|@
name|Deprecated
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
block|,
DECL|enumConstant|TOKEN
name|TOKEN
argument_list|(
operator|(
name|byte
operator|)
literal|82
argument_list|,
literal|"DIGEST-MD5"
argument_list|)
block|,
DECL|enumConstant|PLAIN
name|PLAIN
argument_list|(
operator|(
name|byte
operator|)
literal|83
argument_list|,
literal|"PLAIN"
argument_list|)
block|;
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
annotation|@
name|InterfaceStability
operator|.
name|Evolving
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
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
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
decl_stmt|;
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
block|}
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
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|SaslGssCallbackHandler
specifier|public
specifier|static
class|class
name|SaslGssCallbackHandler
implements|implements
name|CallbackHandler
block|{
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

