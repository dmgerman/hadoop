begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol.datatransfer.sasl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|sasl
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_DATA_TRANSFER_PROTECTION_KEY
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
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|sasl
operator|.
name|DataTransferSaslUtil
operator|.
name|*
import|;
end_import

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
name|SaslException
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
name|crypto
operator|.
name|CipherOption
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
name|hdfs
operator|.
name|net
operator|.
name|Peer
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|IOStreamPair
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
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|InvalidEncryptionKeyException
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
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|DataTransferEncryptorMessageProto
operator|.
name|DataTransferEncryptorStatus
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockPoolTokenSecretManager
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DNConf
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
name|SaslPropertiesResolver
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
name|SecurityUtil
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
name|UserGroupInformation
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * Negotiates SASL for DataTransferProtocol on behalf of a server.  There are  * two possible supported variants of SASL negotiation: either a general-purpose  * negotiation supporting any quality of protection, or a specialized  * negotiation that enforces privacy as the quality of protection using a  * cryptographically strong encryption key.  *  * This class is used in the DataNode for handling inbound connections.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SaslDataTransferServer
specifier|public
class|class
name|SaslDataTransferServer
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SaslDataTransferServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|blockPoolTokenSecretManager
specifier|private
specifier|final
name|BlockPoolTokenSecretManager
name|blockPoolTokenSecretManager
decl_stmt|;
DECL|field|dnConf
specifier|private
specifier|final
name|DNConf
name|dnConf
decl_stmt|;
comment|/**    * Creates a new SaslDataTransferServer.    *    * @param dnConf configuration of DataNode    * @param blockPoolTokenSecretManager used for checking block access tokens    *   and encryption keys    */
DECL|method|SaslDataTransferServer (DNConf dnConf, BlockPoolTokenSecretManager blockPoolTokenSecretManager)
specifier|public
name|SaslDataTransferServer
parameter_list|(
name|DNConf
name|dnConf
parameter_list|,
name|BlockPoolTokenSecretManager
name|blockPoolTokenSecretManager
parameter_list|)
block|{
name|this
operator|.
name|blockPoolTokenSecretManager
operator|=
name|blockPoolTokenSecretManager
expr_stmt|;
name|this
operator|.
name|dnConf
operator|=
name|dnConf
expr_stmt|;
block|}
comment|/**    * Receives SASL negotiation from a peer on behalf of a server.    *    * @param peer connection peer    * @param underlyingOut connection output stream    * @param underlyingIn connection input stream    * @param int xferPort data transfer port of DataNode accepting connection    * @param datanodeId ID of DataNode accepting connection    * @return new pair of streams, wrapped after SASL negotiation    * @throws IOException for any error    */
DECL|method|receive (Peer peer, OutputStream underlyingOut, InputStream underlyingIn, int xferPort, DatanodeID datanodeId)
specifier|public
name|IOStreamPair
name|receive
parameter_list|(
name|Peer
name|peer
parameter_list|,
name|OutputStream
name|underlyingOut
parameter_list|,
name|InputStream
name|underlyingIn
parameter_list|,
name|int
name|xferPort
parameter_list|,
name|DatanodeID
name|datanodeId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dnConf
operator|.
name|getEncryptDataTransfer
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SASL server doing encrypted handshake for peer = {}, datanodeId = {}"
argument_list|,
name|peer
argument_list|,
name|datanodeId
argument_list|)
expr_stmt|;
return|return
name|getEncryptedStreams
argument_list|(
name|peer
argument_list|,
name|underlyingOut
argument_list|,
name|underlyingIn
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SASL server skipping handshake in unsecured configuration for "
operator|+
literal|"peer = {}, datanodeId = {}"
argument_list|,
name|peer
argument_list|,
name|datanodeId
argument_list|)
expr_stmt|;
return|return
operator|new
name|IOStreamPair
argument_list|(
name|underlyingIn
argument_list|,
name|underlyingOut
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|SecurityUtil
operator|.
name|isPrivilegedPort
argument_list|(
name|xferPort
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SASL server skipping handshake in secured configuration for "
operator|+
literal|"peer = {}, datanodeId = {}"
argument_list|,
name|peer
argument_list|,
name|datanodeId
argument_list|)
expr_stmt|;
return|return
operator|new
name|IOStreamPair
argument_list|(
name|underlyingIn
argument_list|,
name|underlyingOut
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dnConf
operator|.
name|getSaslPropsResolver
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SASL server doing general handshake for peer = {}, datanodeId = {}"
argument_list|,
name|peer
argument_list|,
name|datanodeId
argument_list|)
expr_stmt|;
return|return
name|getSaslStreams
argument_list|(
name|peer
argument_list|,
name|underlyingOut
argument_list|,
name|underlyingIn
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dnConf
operator|.
name|getIgnoreSecurePortsForTesting
argument_list|()
condition|)
block|{
comment|// It's a secured cluster using non-privileged ports, but no SASL.  The
comment|// only way this can happen is if the DataNode has
comment|// ignore.secure.ports.for.testing configured, so this is a rare edge case.
name|LOG
operator|.
name|debug
argument_list|(
literal|"SASL server skipping handshake in secured configuration with no SASL "
operator|+
literal|"protection configured for peer = {}, datanodeId = {}"
argument_list|,
name|peer
argument_list|,
name|datanodeId
argument_list|)
expr_stmt|;
return|return
operator|new
name|IOStreamPair
argument_list|(
name|underlyingIn
argument_list|,
name|underlyingOut
argument_list|)
return|;
block|}
else|else
block|{
comment|// The error message here intentionally does not mention
comment|// ignore.secure.ports.for.testing.  That's intended for dev use only.
comment|// This code path is not expected to execute ever, because DataNode startup
comment|// checks for invalid configuration and aborts.
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot create a secured "
operator|+
literal|"connection if DataNode listens on unprivileged port (%d) and no "
operator|+
literal|"protection is defined in configuration property %s."
argument_list|,
name|datanodeId
operator|.
name|getXferPort
argument_list|()
argument_list|,
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Receives SASL negotiation for specialized encrypted handshake.    *    * @param peer connection peer    * @param underlyingOut connection output stream    * @param underlyingIn connection input stream    * @return new pair of streams, wrapped after SASL negotiation    * @throws IOException for any error    */
DECL|method|getEncryptedStreams (Peer peer, OutputStream underlyingOut, InputStream underlyingIn)
specifier|private
name|IOStreamPair
name|getEncryptedStreams
parameter_list|(
name|Peer
name|peer
parameter_list|,
name|OutputStream
name|underlyingOut
parameter_list|,
name|InputStream
name|underlyingIn
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|peer
operator|.
name|hasSecureChannel
argument_list|()
operator|||
name|dnConf
operator|.
name|getTrustedChannelResolver
argument_list|()
operator|.
name|isTrusted
argument_list|(
name|getPeerAddress
argument_list|(
name|peer
argument_list|)
argument_list|)
condition|)
block|{
return|return
operator|new
name|IOStreamPair
argument_list|(
name|underlyingIn
argument_list|,
name|underlyingOut
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|saslProps
init|=
name|createSaslPropertiesForEncryption
argument_list|(
name|dnConf
operator|.
name|getEncryptionAlgorithm
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"Server using encryption algorithm "
operator|+
name|dnConf
operator|.
name|getEncryptionAlgorithm
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CallbackHandler
name|callbackHandler
init|=
operator|new
name|SaslServerCallbackHandler
argument_list|(
operator|new
name|PasswordFunction
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|char
index|[]
name|apply
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|encryptionKeyToPassword
argument_list|(
name|getEncryptionKeyFromUserName
argument_list|(
name|userName
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|doSaslHandshake
argument_list|(
name|underlyingOut
argument_list|,
name|underlyingIn
argument_list|,
name|saslProps
argument_list|,
name|callbackHandler
argument_list|)
return|;
block|}
comment|/**    * The SASL handshake for encrypted vs. general-purpose uses different logic    * for determining the password.  This interface is used to parameterize that    * logic.  It's similar to a Guava Function, but we need to let it throw    * exceptions.    */
DECL|interface|PasswordFunction
specifier|private
interface|interface
name|PasswordFunction
block|{
comment|/**      * Returns the SASL password for the given user name.      *      * @param userName SASL user name      * @return SASL password      * @throws IOException for any error      */
DECL|method|apply (String userName)
name|char
index|[]
name|apply
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Sets user name and password when asked by the server-side SASL object.    */
DECL|class|SaslServerCallbackHandler
specifier|private
specifier|static
specifier|final
class|class
name|SaslServerCallbackHandler
implements|implements
name|CallbackHandler
block|{
DECL|field|passwordFunction
specifier|private
specifier|final
name|PasswordFunction
name|passwordFunction
decl_stmt|;
comment|/**      * Creates a new SaslServerCallbackHandler.      *      * @param passwordFunction for determing the user's password      */
DECL|method|SaslServerCallbackHandler (PasswordFunction passwordFunction)
specifier|public
name|SaslServerCallbackHandler
parameter_list|(
name|PasswordFunction
name|passwordFunction
parameter_list|)
block|{
name|this
operator|.
name|passwordFunction
operator|=
name|passwordFunction
expr_stmt|;
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
name|IOException
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
literal|"Unrecognized SASL DIGEST-MD5 Callback: "
operator|+
name|callback
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
name|pc
operator|.
name|setPassword
argument_list|(
name|passwordFunction
operator|.
name|apply
argument_list|(
name|nc
operator|.
name|getDefaultName
argument_list|()
argument_list|)
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
name|ac
operator|.
name|setAuthorized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ac
operator|.
name|setAuthorizedID
argument_list|(
name|ac
operator|.
name|getAuthorizationID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Given a secret manager and a username encoded for the encrypted handshake,    * determine the encryption key.    *     * @param userName containing the keyId, blockPoolId, and nonce.    * @return secret encryption key.    * @throws IOException    */
DECL|method|getEncryptionKeyFromUserName (String userName)
specifier|private
name|byte
index|[]
name|getEncryptionKeyFromUserName
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|nameComponents
init|=
name|userName
operator|.
name|split
argument_list|(
name|NAME_DELIMITER
argument_list|)
decl_stmt|;
if|if
condition|(
name|nameComponents
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
literal|"Provided name '"
operator|+
name|userName
operator|+
literal|"' has "
operator|+
name|nameComponents
operator|.
name|length
operator|+
literal|" components instead of the expected 3."
argument_list|)
throw|;
block|}
name|int
name|keyId
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|nameComponents
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|String
name|blockPoolId
init|=
name|nameComponents
index|[
literal|1
index|]
decl_stmt|;
name|byte
index|[]
name|nonce
init|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|nameComponents
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
return|return
name|blockPoolTokenSecretManager
operator|.
name|retrieveDataEncryptionKey
argument_list|(
name|keyId
argument_list|,
name|blockPoolId
argument_list|,
name|nonce
argument_list|)
return|;
block|}
comment|/**    * Receives SASL negotiation for general-purpose handshake.    *    * @param peer connection peer    * @param underlyingOut connection output stream    * @param underlyingIn connection input stream    * @return new pair of streams, wrapped after SASL negotiation    * @throws IOException for any error    */
DECL|method|getSaslStreams (Peer peer, OutputStream underlyingOut, InputStream underlyingIn)
specifier|private
name|IOStreamPair
name|getSaslStreams
parameter_list|(
name|Peer
name|peer
parameter_list|,
name|OutputStream
name|underlyingOut
parameter_list|,
name|InputStream
name|underlyingIn
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|peer
operator|.
name|hasSecureChannel
argument_list|()
operator|||
name|dnConf
operator|.
name|getTrustedChannelResolver
argument_list|()
operator|.
name|isTrusted
argument_list|(
name|getPeerAddress
argument_list|(
name|peer
argument_list|)
argument_list|)
condition|)
block|{
return|return
operator|new
name|IOStreamPair
argument_list|(
name|underlyingIn
argument_list|,
name|underlyingOut
argument_list|)
return|;
block|}
name|SaslPropertiesResolver
name|saslPropsResolver
init|=
name|dnConf
operator|.
name|getSaslPropsResolver
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|saslProps
init|=
name|saslPropsResolver
operator|.
name|getServerProperties
argument_list|(
name|getPeerAddress
argument_list|(
name|peer
argument_list|)
argument_list|)
decl_stmt|;
name|CallbackHandler
name|callbackHandler
init|=
operator|new
name|SaslServerCallbackHandler
argument_list|(
operator|new
name|PasswordFunction
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|char
index|[]
name|apply
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|buildServerPassword
argument_list|(
name|userName
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|doSaslHandshake
argument_list|(
name|underlyingOut
argument_list|,
name|underlyingIn
argument_list|,
name|saslProps
argument_list|,
name|callbackHandler
argument_list|)
return|;
block|}
comment|/**    * Calculates the expected correct password on the server side for the    * general-purpose handshake.  The password consists of the block access    * token's password (known to the DataNode via its secret manager).  This    * expects that the client has supplied a user name consisting of its    * serialized block access token identifier.    *    * @param userName SASL user name containing serialized block access token    *   identifier    * @return expected correct SASL password    * @throws IOException for any error    */
DECL|method|buildServerPassword (String userName)
specifier|private
name|char
index|[]
name|buildServerPassword
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockTokenIdentifier
name|identifier
init|=
name|deserializeIdentifier
argument_list|(
name|userName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|tokenPassword
init|=
name|blockPoolTokenSecretManager
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
return|return
operator|(
operator|new
name|String
argument_list|(
name|Base64
operator|.
name|encodeBase64
argument_list|(
name|tokenPassword
argument_list|,
literal|false
argument_list|)
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|)
operator|.
name|toCharArray
argument_list|()
return|;
block|}
comment|/**    * Deserializes a base64-encoded binary representation of a block access    * token.    *    * @param str String to deserialize    * @return BlockTokenIdentifier deserialized from str    * @throws IOException if there is any I/O error    */
DECL|method|deserializeIdentifier (String str)
specifier|private
name|BlockTokenIdentifier
name|deserializeIdentifier
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockTokenIdentifier
name|identifier
init|=
operator|new
name|BlockTokenIdentifier
argument_list|()
decl_stmt|;
name|identifier
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|str
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|identifier
return|;
block|}
comment|/**    * This method actually executes the server-side SASL handshake.    *    * @param underlyingOut connection output stream    * @param underlyingIn connection input stream    * @param saslProps properties of SASL negotiation    * @param callbackHandler for responding to SASL callbacks    * @return new pair of streams, wrapped after SASL negotiation    * @throws IOException for any error    */
DECL|method|doSaslHandshake (OutputStream underlyingOut, InputStream underlyingIn, Map<String, String> saslProps, CallbackHandler callbackHandler)
specifier|private
name|IOStreamPair
name|doSaslHandshake
parameter_list|(
name|OutputStream
name|underlyingOut
parameter_list|,
name|InputStream
name|underlyingIn
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|saslProps
parameter_list|,
name|CallbackHandler
name|callbackHandler
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|underlyingIn
argument_list|)
decl_stmt|;
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|underlyingOut
argument_list|)
decl_stmt|;
name|SaslParticipant
name|sasl
init|=
name|SaslParticipant
operator|.
name|createServerSaslParticipant
argument_list|(
name|saslProps
argument_list|,
name|callbackHandler
argument_list|)
decl_stmt|;
name|int
name|magicNumber
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|magicNumber
operator|!=
name|SASL_TRANSFER_MAGIC_NUMBER
condition|)
block|{
throw|throw
operator|new
name|InvalidMagicNumberException
argument_list|(
name|magicNumber
argument_list|,
name|dnConf
operator|.
name|getEncryptDataTransfer
argument_list|()
argument_list|)
throw|;
block|}
try|try
block|{
comment|// step 1
name|byte
index|[]
name|remoteResponse
init|=
name|readSaslMessage
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|byte
index|[]
name|localResponse
init|=
name|sasl
operator|.
name|evaluateChallengeOrResponse
argument_list|(
name|remoteResponse
argument_list|)
decl_stmt|;
name|sendSaslMessage
argument_list|(
name|out
argument_list|,
name|localResponse
argument_list|)
expr_stmt|;
comment|// step 2 (server-side only)
name|List
argument_list|<
name|CipherOption
argument_list|>
name|cipherOptions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|remoteResponse
operator|=
name|readSaslMessageAndNegotiationCipherOptions
argument_list|(
name|in
argument_list|,
name|cipherOptions
argument_list|)
expr_stmt|;
name|localResponse
operator|=
name|sasl
operator|.
name|evaluateChallengeOrResponse
argument_list|(
name|remoteResponse
argument_list|)
expr_stmt|;
comment|// SASL handshake is complete
name|checkSaslComplete
argument_list|(
name|sasl
argument_list|,
name|saslProps
argument_list|)
expr_stmt|;
name|CipherOption
name|cipherOption
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sasl
operator|.
name|isNegotiatedQopPrivacy
argument_list|()
condition|)
block|{
comment|// Negotiate a cipher option
name|cipherOption
operator|=
name|negotiateCipherOption
argument_list|(
name|dnConf
operator|.
name|getConf
argument_list|()
argument_list|,
name|cipherOptions
argument_list|)
expr_stmt|;
if|if
condition|(
name|cipherOption
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
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Server using cipher suite "
operator|+
name|cipherOption
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// If negotiated cipher option is not null, wrap it before sending.
name|sendSaslMessageAndNegotiatedCipherOption
argument_list|(
name|out
argument_list|,
name|localResponse
argument_list|,
name|wrap
argument_list|(
name|cipherOption
argument_list|,
name|sasl
argument_list|)
argument_list|)
expr_stmt|;
comment|// If negotiated cipher option is not null, we will use it to create
comment|// stream pair.
return|return
name|cipherOption
operator|!=
literal|null
condition|?
name|createStreamPair
argument_list|(
name|dnConf
operator|.
name|getConf
argument_list|()
argument_list|,
name|cipherOption
argument_list|,
name|underlyingOut
argument_list|,
name|underlyingIn
argument_list|,
literal|true
argument_list|)
else|:
name|sasl
operator|.
name|createStreamPair
argument_list|(
name|out
argument_list|,
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|ioe
operator|instanceof
name|SaslException
operator|&&
name|ioe
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
operator|&&
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InvalidEncryptionKeyException
condition|)
block|{
comment|// This could just be because the client is long-lived and hasn't gotten
comment|// a new encryption key from the NN in a while. Upon receiving this
comment|// error, the client will get a new encryption key from the NN and retry
comment|// connecting to this DN.
name|sendInvalidKeySaslErrorMessage
argument_list|(
name|out
argument_list|,
name|ioe
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sendGenericSaslErrorMessage
argument_list|(
name|out
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ioe
throw|;
block|}
block|}
comment|/**    * Sends a SASL negotiation message indicating an invalid key error.    *    * @param out stream to receive message    * @param message to send    * @throws IOException for any error    */
DECL|method|sendInvalidKeySaslErrorMessage (DataOutputStream out, String message)
specifier|private
specifier|static
name|void
name|sendInvalidKeySaslErrorMessage
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|sendSaslMessage
argument_list|(
name|out
argument_list|,
name|DataTransferEncryptorStatus
operator|.
name|ERROR_UNKNOWN_KEY
argument_list|,
literal|null
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

