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
name|CallbackHandler
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
name|SaslClient
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
name|security
operator|.
name|SaslInputStream
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
name|SaslOutputStream
import|;
end_import

begin_comment
comment|/**  * Strongly inspired by Thrift's TSaslTransport class.  *  * Used to abstract over the<code>SaslServer</code> and  *<code>SaslClient</code> classes, which share a lot of their interface, but  * unfortunately don't share a common superclass.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SaslParticipant
class|class
name|SaslParticipant
block|{
comment|// This has to be set as part of the SASL spec, but it don't matter for
comment|// our purposes, but may not be empty. It's sent over the wire, so use
comment|// a short string.
DECL|field|SERVER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SERVER_NAME
init|=
literal|"0"
decl_stmt|;
DECL|field|PROTOCOL
specifier|private
specifier|static
specifier|final
name|String
name|PROTOCOL
init|=
literal|"hdfs"
decl_stmt|;
DECL|field|MECHANISM
specifier|private
specifier|static
specifier|final
name|String
name|MECHANISM
init|=
literal|"DIGEST-MD5"
decl_stmt|;
comment|// One of these will always be null.
DECL|field|saslServer
specifier|private
specifier|final
name|SaslServer
name|saslServer
decl_stmt|;
DECL|field|saslClient
specifier|private
specifier|final
name|SaslClient
name|saslClient
decl_stmt|;
comment|/**    * Creates a SaslParticipant wrapping a SaslServer.    *    * @param saslProps properties of SASL negotiation    * @param callbackHandler for handling all SASL callbacks    * @return SaslParticipant wrapping SaslServer    * @throws SaslException for any error    */
DECL|method|createServerSaslParticipant ( Map<String, String> saslProps, CallbackHandler callbackHandler)
specifier|public
specifier|static
name|SaslParticipant
name|createServerSaslParticipant
parameter_list|(
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
name|SaslException
block|{
return|return
operator|new
name|SaslParticipant
argument_list|(
name|Sasl
operator|.
name|createSaslServer
argument_list|(
name|MECHANISM
argument_list|,
name|PROTOCOL
argument_list|,
name|SERVER_NAME
argument_list|,
name|saslProps
argument_list|,
name|callbackHandler
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a SaslParticipant wrapping a SaslClient.    *    * @param userName SASL user name    * @param saslProps properties of SASL negotiation    * @param callbackHandler for handling all SASL callbacks    * @return SaslParticipant wrapping SaslClient    * @throws SaslException for any error    */
DECL|method|createClientSaslParticipant (String userName, Map<String, String> saslProps, CallbackHandler callbackHandler)
specifier|public
specifier|static
name|SaslParticipant
name|createClientSaslParticipant
parameter_list|(
name|String
name|userName
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
name|SaslException
block|{
return|return
operator|new
name|SaslParticipant
argument_list|(
name|Sasl
operator|.
name|createSaslClient
argument_list|(
operator|new
name|String
index|[]
block|{
name|MECHANISM
block|}
argument_list|,
name|userName
argument_list|,
name|PROTOCOL
argument_list|,
name|SERVER_NAME
argument_list|,
name|saslProps
argument_list|,
name|callbackHandler
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Private constructor wrapping a SaslServer.    *    * @param saslServer to wrap    */
DECL|method|SaslParticipant (SaslServer saslServer)
specifier|private
name|SaslParticipant
parameter_list|(
name|SaslServer
name|saslServer
parameter_list|)
block|{
name|this
operator|.
name|saslServer
operator|=
name|saslServer
expr_stmt|;
name|this
operator|.
name|saslClient
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Private constructor wrapping a SaslClient.    *    * @param saslClient to wrap    */
DECL|method|SaslParticipant (SaslClient saslClient)
specifier|private
name|SaslParticipant
parameter_list|(
name|SaslClient
name|saslClient
parameter_list|)
block|{
name|this
operator|.
name|saslServer
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|saslClient
operator|=
name|saslClient
expr_stmt|;
block|}
comment|/**    * @see {@link SaslServer#evaluateResponse}    * @see {@link SaslClient#evaluateChallenge}    */
DECL|method|evaluateChallengeOrResponse (byte[] challengeOrResponse)
specifier|public
name|byte
index|[]
name|evaluateChallengeOrResponse
parameter_list|(
name|byte
index|[]
name|challengeOrResponse
parameter_list|)
throws|throws
name|SaslException
block|{
if|if
condition|(
name|saslClient
operator|!=
literal|null
condition|)
block|{
return|return
name|saslClient
operator|.
name|evaluateChallenge
argument_list|(
name|challengeOrResponse
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|saslServer
operator|.
name|evaluateResponse
argument_list|(
name|challengeOrResponse
argument_list|)
return|;
block|}
block|}
comment|/**    * After successful SASL negotation, returns the negotiated quality of    * protection.    *    * @return negotiated quality of protection    */
DECL|method|getNegotiatedQop ()
specifier|public
name|String
name|getNegotiatedQop
parameter_list|()
block|{
if|if
condition|(
name|saslClient
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|saslClient
operator|.
name|getNegotiatedProperty
argument_list|(
name|Sasl
operator|.
name|QOP
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|String
operator|)
name|saslServer
operator|.
name|getNegotiatedProperty
argument_list|(
name|Sasl
operator|.
name|QOP
argument_list|)
return|;
block|}
block|}
comment|/**    * After successful SASL negotiation, returns whether it's QOP privacy    *     * @return boolean whether it's QOP privacy    */
DECL|method|isNegotiatedQopPrivacy ()
specifier|public
name|boolean
name|isNegotiatedQopPrivacy
parameter_list|()
block|{
name|String
name|qop
init|=
name|getNegotiatedQop
argument_list|()
decl_stmt|;
return|return
name|qop
operator|!=
literal|null
operator|&&
literal|"auth-conf"
operator|.
name|equalsIgnoreCase
argument_list|(
name|qop
argument_list|)
return|;
block|}
comment|/**    * Wraps a byte array.    *     * @param bytes The array containing the bytes to wrap.    * @param off The starting position at the array    * @param len The number of bytes to wrap    * @return byte[] wrapped bytes    * @throws SaslException if the bytes cannot be successfully wrapped    */
DECL|method|wrap (byte[] bytes, int off, int len)
specifier|public
name|byte
index|[]
name|wrap
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SaslException
block|{
if|if
condition|(
name|saslClient
operator|!=
literal|null
condition|)
block|{
return|return
name|saslClient
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|saslServer
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
comment|/**    * Unwraps a byte array.    *     * @param bytes The array containing the bytes to unwrap.    * @param off The starting position at the array    * @param len The number of bytes to unwrap    * @return byte[] unwrapped bytes    * @throws SaslException if the bytes cannot be successfully unwrapped    */
DECL|method|unwrap (byte[] bytes, int off, int len)
specifier|public
name|byte
index|[]
name|unwrap
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SaslException
block|{
if|if
condition|(
name|saslClient
operator|!=
literal|null
condition|)
block|{
return|return
name|saslClient
operator|.
name|unwrap
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|saslServer
operator|.
name|unwrap
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns true if SASL negotiation is complete.    *    * @return true if SASL negotiation is complete    */
DECL|method|isComplete ()
specifier|public
name|boolean
name|isComplete
parameter_list|()
block|{
if|if
condition|(
name|saslClient
operator|!=
literal|null
condition|)
block|{
return|return
name|saslClient
operator|.
name|isComplete
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|saslServer
operator|.
name|isComplete
argument_list|()
return|;
block|}
block|}
comment|/**    * Return some input/output streams that may henceforth have their    * communication encrypted, depending on the negotiated quality of protection.    *    * @param out output stream to wrap    * @param in input stream to wrap    * @return IOStreamPair wrapping the streams    */
DECL|method|createStreamPair (DataOutputStream out, DataInputStream in)
specifier|public
name|IOStreamPair
name|createStreamPair
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|DataInputStream
name|in
parameter_list|)
block|{
if|if
condition|(
name|saslClient
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|IOStreamPair
argument_list|(
operator|new
name|SaslInputStream
argument_list|(
name|in
argument_list|,
name|saslClient
argument_list|)
argument_list|,
operator|new
name|SaslOutputStream
argument_list|(
name|out
argument_list|,
name|saslClient
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|IOStreamPair
argument_list|(
operator|new
name|SaslInputStream
argument_list|(
name|in
argument_list|,
name|saslServer
argument_list|)
argument_list|,
operator|new
name|SaslOutputStream
argument_list|(
name|out
argument_list|,
name|saslServer
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

