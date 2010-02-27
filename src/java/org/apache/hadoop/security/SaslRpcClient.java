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
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|DataOutputStream
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|RealmChoiceCallback
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
name|SaslClient
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
name|io
operator|.
name|WritableUtils
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
name|RemoteException
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
name|SaslRpcServer
operator|.
name|AuthMethod
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
name|SaslRpcServer
operator|.
name|SaslStatus
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
name|TokenIdentifier
import|;
end_import

begin_comment
comment|/**  * A utility class that encapsulates SASL logic for RPC client  */
end_comment

begin_class
DECL|class|SaslRpcClient
specifier|public
class|class
name|SaslRpcClient
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
name|SaslRpcClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|saslClient
specifier|private
specifier|final
name|SaslClient
name|saslClient
decl_stmt|;
comment|/**    * Create a SaslRpcClient for an authentication method    *     * @param method    *          the requested authentication method    * @param token    *          token to use if needed by the authentication method    */
DECL|method|SaslRpcClient (AuthMethod method, Token<? extends TokenIdentifier> token, String serverPrincipal)
specifier|public
name|SaslRpcClient
parameter_list|(
name|AuthMethod
name|method
parameter_list|,
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|serverPrincipal
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|method
condition|)
block|{
case|case
name|DIGEST
case|:
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
literal|"Creating SASL "
operator|+
name|AuthMethod
operator|.
name|DIGEST
operator|.
name|getMechanismName
argument_list|()
operator|+
literal|" client to authenticate to service at "
operator|+
name|token
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
name|saslClient
operator|=
name|Sasl
operator|.
name|createSaslClient
argument_list|(
operator|new
name|String
index|[]
block|{
name|AuthMethod
operator|.
name|DIGEST
operator|.
name|getMechanismName
argument_list|()
block|}
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SaslRpcServer
operator|.
name|SASL_DEFAULT_REALM
argument_list|,
name|SaslRpcServer
operator|.
name|SASL_PROPS
argument_list|,
operator|new
name|SaslClientCallbackHandler
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|KERBEROS
case|:
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
literal|"Creating SASL "
operator|+
name|AuthMethod
operator|.
name|KERBEROS
operator|.
name|getMechanismName
argument_list|()
operator|+
literal|" client. Server's Kerberos principal name is "
operator|+
name|serverPrincipal
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|serverPrincipal
operator|==
literal|null
operator|||
name|serverPrincipal
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to specify server's Kerberos principal name"
argument_list|)
throw|;
block|}
name|String
name|names
index|[]
init|=
name|SaslRpcServer
operator|.
name|splitKerberosName
argument_list|(
name|serverPrincipal
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|length
operator|!=
literal|3
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Kerberos principal name does NOT have the expected hostname part: "
operator|+
name|serverPrincipal
argument_list|)
throw|;
block|}
name|saslClient
operator|=
name|Sasl
operator|.
name|createSaslClient
argument_list|(
operator|new
name|String
index|[]
block|{
name|AuthMethod
operator|.
name|KERBEROS
operator|.
name|getMechanismName
argument_list|()
block|}
argument_list|,
literal|null
argument_list|,
name|names
index|[
literal|0
index|]
argument_list|,
name|names
index|[
literal|1
index|]
argument_list|,
name|SaslRpcServer
operator|.
name|SASL_PROPS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown authentication method "
operator|+
name|method
argument_list|)
throw|;
block|}
if|if
condition|(
name|saslClient
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to find SASL client implementation"
argument_list|)
throw|;
block|}
DECL|method|readStatus (DataInputStream inStream)
specifier|private
specifier|static
name|void
name|readStatus
parameter_list|(
name|DataInputStream
name|inStream
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|status
init|=
name|inStream
operator|.
name|readInt
argument_list|()
decl_stmt|;
comment|// read status
if|if
condition|(
name|status
operator|!=
name|SaslStatus
operator|.
name|SUCCESS
operator|.
name|state
condition|)
block|{
throw|throw
operator|new
name|RemoteException
argument_list|(
name|WritableUtils
operator|.
name|readString
argument_list|(
name|inStream
argument_list|)
argument_list|,
name|WritableUtils
operator|.
name|readString
argument_list|(
name|inStream
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Do client side SASL authentication with server via the given InputStream    * and OutputStream    *     * @param inS    *          InputStream to use    * @param outS    *          OutputStream to use    * @return true if connection is set up, or false if needs to switch     *             to simple Auth.    * @throws IOException    */
DECL|method|saslConnect (InputStream inS, OutputStream outS)
specifier|public
name|boolean
name|saslConnect
parameter_list|(
name|InputStream
name|inS
parameter_list|,
name|OutputStream
name|outS
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|inStream
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|inS
argument_list|)
argument_list|)
decl_stmt|;
name|DataOutputStream
name|outStream
init|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|outS
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|saslToken
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|saslClient
operator|.
name|hasInitialResponse
argument_list|()
condition|)
name|saslToken
operator|=
name|saslClient
operator|.
name|evaluateChallenge
argument_list|(
name|saslToken
argument_list|)
expr_stmt|;
if|if
condition|(
name|saslToken
operator|!=
literal|null
condition|)
block|{
name|outStream
operator|.
name|writeInt
argument_list|(
name|saslToken
operator|.
name|length
argument_list|)
expr_stmt|;
name|outStream
operator|.
name|write
argument_list|(
name|saslToken
argument_list|,
literal|0
argument_list|,
name|saslToken
operator|.
name|length
argument_list|)
expr_stmt|;
name|outStream
operator|.
name|flush
argument_list|()
expr_stmt|;
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
literal|"Have sent token of size "
operator|+
name|saslToken
operator|.
name|length
operator|+
literal|" from initSASLContext."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|saslClient
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|readStatus
argument_list|(
name|inStream
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|inStream
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|==
name|SaslRpcServer
operator|.
name|SWITCH_TO_SIMPLE_AUTH
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
literal|"Server asks us to fall back to simple auth."
argument_list|)
expr_stmt|;
name|saslClient
operator|.
name|dispose
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
name|saslToken
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
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
literal|"Will read input token of size "
operator|+
name|saslToken
operator|.
name|length
operator|+
literal|" for processing by initSASLContext"
argument_list|)
expr_stmt|;
name|inStream
operator|.
name|readFully
argument_list|(
name|saslToken
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|saslClient
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|saslToken
operator|=
name|saslClient
operator|.
name|evaluateChallenge
argument_list|(
name|saslToken
argument_list|)
expr_stmt|;
if|if
condition|(
name|saslToken
operator|!=
literal|null
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
literal|"Will send token of size "
operator|+
name|saslToken
operator|.
name|length
operator|+
literal|" from initSASLContext."
argument_list|)
expr_stmt|;
name|outStream
operator|.
name|writeInt
argument_list|(
name|saslToken
operator|.
name|length
argument_list|)
expr_stmt|;
name|outStream
operator|.
name|write
argument_list|(
name|saslToken
argument_list|,
literal|0
argument_list|,
name|saslToken
operator|.
name|length
argument_list|)
expr_stmt|;
name|outStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|saslClient
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|readStatus
argument_list|(
name|inStream
argument_list|)
expr_stmt|;
name|saslToken
operator|=
operator|new
name|byte
index|[
name|inStream
operator|.
name|readInt
argument_list|()
index|]
expr_stmt|;
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
literal|"Will read input token of size "
operator|+
name|saslToken
operator|.
name|length
operator|+
literal|" for processing by initSASLContext"
argument_list|)
expr_stmt|;
name|inStream
operator|.
name|readFully
argument_list|(
name|saslToken
argument_list|)
expr_stmt|;
block|}
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
literal|"SASL client context established. Negotiated QoP: "
operator|+
name|saslClient
operator|.
name|getNegotiatedProperty
argument_list|(
name|Sasl
operator|.
name|QOP
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
try|try
block|{
name|saslClient
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SaslException
name|ignored
parameter_list|)
block|{
comment|// ignore further exceptions during cleanup
block|}
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Get a SASL wrapped InputStream. Can be called only after saslConnect() has    * been called.    *     * @param in    *          the InputStream to wrap    * @return a SASL wrapped InputStream    * @throws IOException    */
DECL|method|getInputStream (InputStream in)
specifier|public
name|InputStream
name|getInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|saslClient
operator|.
name|isComplete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Sasl authentication exchange hasn't completed yet"
argument_list|)
throw|;
block|}
return|return
operator|new
name|SaslInputStream
argument_list|(
name|in
argument_list|,
name|saslClient
argument_list|)
return|;
block|}
comment|/**    * Get a SASL wrapped OutputStream. Can be called only after saslConnect() has    * been called.    *     * @param out    *          the OutputStream to wrap    * @return a SASL wrapped OutputStream    * @throws IOException    */
DECL|method|getOutputStream (OutputStream out)
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|saslClient
operator|.
name|isComplete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Sasl authentication exchange hasn't completed yet"
argument_list|)
throw|;
block|}
return|return
operator|new
name|SaslOutputStream
argument_list|(
name|out
argument_list|,
name|saslClient
argument_list|)
return|;
block|}
comment|/** Release resources used by wrapped saslClient */
DECL|method|dispose ()
specifier|public
name|void
name|dispose
parameter_list|()
throws|throws
name|SaslException
block|{
name|saslClient
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
DECL|class|SaslClientCallbackHandler
specifier|private
specifier|static
class|class
name|SaslClientCallbackHandler
implements|implements
name|CallbackHandler
block|{
DECL|field|userName
specifier|private
specifier|final
name|String
name|userName
decl_stmt|;
DECL|field|userPassword
specifier|private
specifier|final
name|char
index|[]
name|userPassword
decl_stmt|;
DECL|method|SaslClientCallbackHandler (Token<? extends TokenIdentifier> token)
specifier|public
name|SaslClientCallbackHandler
parameter_list|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|SaslRpcServer
operator|.
name|encodeIdentifier
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|userPassword
operator|=
name|SaslRpcServer
operator|.
name|encodePassword
argument_list|(
name|token
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|RealmCallback
name|rc
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
name|RealmChoiceCallback
condition|)
block|{
continue|continue;
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
name|rc
operator|=
operator|(
name|RealmCallback
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
literal|"Unrecognized SASL client callback"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|nc
operator|!=
literal|null
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
literal|"SASL client callback: setting username: "
operator|+
name|userName
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pc
operator|!=
literal|null
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
literal|"SASL client callback: setting userPassword"
argument_list|)
expr_stmt|;
name|pc
operator|.
name|setPassword
argument_list|(
name|userPassword
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rc
operator|!=
literal|null
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
literal|"SASL client callback: setting realm: "
operator|+
name|rc
operator|.
name|getDefaultText
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setText
argument_list|(
name|rc
operator|.
name|getDefaultText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

