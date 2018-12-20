begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|security
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
name|hdds
operator|.
name|security
operator|.
name|token
operator|.
name|OzoneBlockTokenIdentifier
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|BlockTokenSecretProto
operator|.
name|AccessModeProto
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
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|SecurityConfig
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
name|Text
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
name|util
operator|.
name|Time
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
name|KeyPair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/**  * SecretManager for Ozone Master block tokens.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OzoneBlockTokenSecretManager
specifier|public
class|class
name|OzoneBlockTokenSecretManager
extends|extends
name|OzoneSecretManager
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
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
name|OzoneBlockTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
empty_stmt|;
comment|// Will be set by grpc clients for individual datanodes.
DECL|field|SERVICE
specifier|static
specifier|final
name|Text
name|SERVICE
init|=
operator|new
name|Text
argument_list|(
literal|"HDDS_SERVICE"
argument_list|)
decl_stmt|;
DECL|field|omCertSerialId
specifier|private
specifier|final
name|String
name|omCertSerialId
decl_stmt|;
comment|/**    * Create a secret manager.    *    * @param conf    * @param blockTokenExpirytime token expiry time for expired tokens in    * milliseconds    */
DECL|method|OzoneBlockTokenSecretManager (SecurityConfig conf, long blockTokenExpirytime, String omCertSerialId)
specifier|public
name|OzoneBlockTokenSecretManager
parameter_list|(
name|SecurityConfig
name|conf
parameter_list|,
name|long
name|blockTokenExpirytime
parameter_list|,
name|String
name|omCertSerialId
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|blockTokenExpirytime
argument_list|,
name|blockTokenExpirytime
argument_list|,
name|SERVICE
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|this
operator|.
name|omCertSerialId
operator|=
name|omCertSerialId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|OzoneBlockTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"Ozone block token can't be created "
operator|+
literal|"without owner and access mode information."
argument_list|)
throw|;
block|}
DECL|method|createIdentifier (String owner, String blockId, EnumSet<AccessModeProto> modes, long maxLength)
specifier|public
name|OzoneBlockTokenIdentifier
name|createIdentifier
parameter_list|(
name|String
name|owner
parameter_list|,
name|String
name|blockId
parameter_list|,
name|EnumSet
argument_list|<
name|AccessModeProto
argument_list|>
name|modes
parameter_list|,
name|long
name|maxLength
parameter_list|)
block|{
return|return
operator|new
name|OzoneBlockTokenIdentifier
argument_list|(
name|owner
argument_list|,
name|blockId
argument_list|,
name|modes
argument_list|,
name|getTokenExpiryTime
argument_list|()
argument_list|,
name|omCertSerialId
argument_list|,
name|maxLength
argument_list|)
return|;
block|}
comment|/**    * Generate an block token for specified user, blockId. Service field for    * token is set to blockId.    *    * @param user    * @param blockId    * @param modes    * @param maxLength    * @return token    */
DECL|method|generateToken (String user, String blockId, EnumSet<AccessModeProto> modes, long maxLength)
specifier|public
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|generateToken
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|blockId
parameter_list|,
name|EnumSet
argument_list|<
name|AccessModeProto
argument_list|>
name|modes
parameter_list|,
name|long
name|maxLength
parameter_list|)
block|{
name|OzoneBlockTokenIdentifier
name|tokenIdentifier
init|=
name|createIdentifier
argument_list|(
name|user
argument_list|,
name|blockId
argument_list|,
name|modes
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|long
name|expiryTime
init|=
name|tokenIdentifier
operator|.
name|getExpiryDate
argument_list|()
decl_stmt|;
name|String
name|tokenId
init|=
name|tokenIdentifier
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Issued delegation token -> expiryTime:{},tokenId:{}"
argument_list|,
name|expiryTime
argument_list|,
name|tokenId
argument_list|)
expr_stmt|;
block|}
comment|// Pass blockId as service.
return|return
operator|new
name|Token
argument_list|<>
argument_list|(
name|tokenIdentifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|createPassword
argument_list|(
name|tokenIdentifier
argument_list|)
argument_list|,
name|tokenIdentifier
operator|.
name|getKind
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|blockId
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Generate an block token for current user.    *    * @param blockId    * @param modes    * @return token    */
DECL|method|generateToken (String blockId, EnumSet<AccessModeProto> modes, long maxLength)
specifier|public
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|generateToken
parameter_list|(
name|String
name|blockId
parameter_list|,
name|EnumSet
argument_list|<
name|AccessModeProto
argument_list|>
name|modes
parameter_list|,
name|long
name|maxLength
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|String
name|userID
init|=
operator|(
name|ugi
operator|==
literal|null
condition|?
literal|null
else|:
name|ugi
operator|.
name|getShortUserName
argument_list|()
operator|)
decl_stmt|;
return|return
name|generateToken
argument_list|(
name|userID
argument_list|,
name|blockId
argument_list|,
name|modes
argument_list|,
name|maxLength
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|retrievePassword (OzoneBlockTokenIdentifier identifier)
specifier|public
name|byte
index|[]
name|retrievePassword
parameter_list|(
name|OzoneBlockTokenIdentifier
name|identifier
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|validateToken
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
return|return
name|createPassword
argument_list|(
name|identifier
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|renewToken (Token<OzoneBlockTokenIdentifier> token, String renewer)
specifier|public
name|long
name|renewToken
parameter_list|(
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Renew token operation is not "
operator|+
literal|"supported for ozone block tokens."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|cancelToken (Token<OzoneBlockTokenIdentifier> token, String canceller)
specifier|public
name|OzoneBlockTokenIdentifier
name|cancelToken
parameter_list|(
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|canceller
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cancel token operation is not "
operator|+
literal|"supported for ozone block tokens."
argument_list|)
throw|;
block|}
comment|/**    * Find the OzoneBlockTokenInfo for the given token id, and verify that if the    * token is not expired.    */
DECL|method|validateToken (OzoneBlockTokenIdentifier identifier)
specifier|public
name|boolean
name|validateToken
parameter_list|(
name|OzoneBlockTokenIdentifier
name|identifier
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
if|if
condition|(
name|identifier
operator|.
name|getExpiryDate
argument_list|()
operator|<
name|now
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"token "
operator|+
name|formatTokenId
argument_list|(
name|identifier
argument_list|)
operator|+
literal|" is "
operator|+
literal|"expired, current time: "
operator|+
name|Time
operator|.
name|formatTime
argument_list|(
name|now
argument_list|)
operator|+
literal|" expiry time: "
operator|+
name|identifier
operator|.
name|getExpiryDate
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|verifySignature
argument_list|(
name|identifier
argument_list|,
name|createPassword
argument_list|(
name|identifier
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Tampared/Inavalid token."
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Should be called before this object is used.    */
annotation|@
name|Override
DECL|method|start (KeyPair keyPair)
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|(
name|KeyPair
name|keyPair
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|start
argument_list|(
name|keyPair
argument_list|)
expr_stmt|;
name|removeExpiredKeys
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns expiry time by adding configured expiry time with current time.    *    * @return Expiry time.    */
DECL|method|getTokenExpiryTime ()
specifier|private
name|long
name|getTokenExpiryTime
parameter_list|()
block|{
return|return
name|Time
operator|.
name|now
argument_list|()
operator|+
name|getTokenRenewInterval
argument_list|()
return|;
block|}
comment|/**    * Should be called before this object is used.    */
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|removeExpiredKeys ()
specifier|private
specifier|synchronized
name|void
name|removeExpiredKeys
parameter_list|()
block|{
comment|// TODO: handle roll private key/certificate
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|OzoneSecretKey
argument_list|>
argument_list|>
name|it
init|=
name|allKeys
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|OzoneSecretKey
argument_list|>
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|OzoneSecretKey
name|key
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|getExpiryDate
argument_list|()
operator|<
name|now
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

