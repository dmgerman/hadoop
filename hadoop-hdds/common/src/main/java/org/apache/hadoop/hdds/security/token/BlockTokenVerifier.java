begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.token
package|package
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
package|;
end_package

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
name|Strings
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
name|exception
operator|.
name|SCMSecurityException
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
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|certificate
operator|.
name|client
operator|.
name|CertificateClient
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_comment
comment|/**  * Verify token and return a UGI with token if authenticated.  */
end_comment

begin_class
DECL|class|BlockTokenVerifier
specifier|public
class|class
name|BlockTokenVerifier
implements|implements
name|TokenVerifier
block|{
DECL|field|caClient
specifier|private
specifier|final
name|CertificateClient
name|caClient
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|SecurityConfig
name|conf
decl_stmt|;
DECL|method|BlockTokenVerifier (SecurityConfig conf, CertificateClient caClient)
specifier|public
name|BlockTokenVerifier
parameter_list|(
name|SecurityConfig
name|conf
parameter_list|,
name|CertificateClient
name|caClient
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|caClient
operator|=
name|caClient
expr_stmt|;
block|}
DECL|method|isExpired (long expiryDate)
specifier|private
name|boolean
name|isExpired
parameter_list|(
name|long
name|expiryDate
parameter_list|)
block|{
return|return
name|Time
operator|.
name|now
argument_list|()
operator|>
name|expiryDate
return|;
block|}
annotation|@
name|Override
DECL|method|verify (String user, String tokenStr)
specifier|public
name|UserGroupInformation
name|verify
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|tokenStr
parameter_list|)
throws|throws
name|SCMSecurityException
block|{
if|if
condition|(
name|conf
operator|.
name|isGrpcBlockTokenEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|tokenStr
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BlockTokenException
argument_list|(
literal|"Fail to find any token (empty or "
operator|+
literal|"null."
argument_list|)
throw|;
block|}
specifier|final
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|OzoneBlockTokenIdentifier
name|tokenId
init|=
operator|new
name|OzoneBlockTokenIdentifier
argument_list|()
decl_stmt|;
try|try
block|{
name|token
operator|.
name|decodeFromUrlString
argument_list|(
name|tokenStr
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|buf
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|tokenId
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|BlockTokenException
argument_list|(
literal|"Failed to decode token : "
operator|+
name|tokenStr
argument_list|)
throw|;
block|}
comment|// TODO: revisit this when caClient is ready, skip signature check now.
comment|/**        * the final code should like        * if (caClient == null) {        *   throw new SCMSecurityException("Certificate client not available to        *       validate token");        * }        */
if|if
condition|(
name|caClient
operator|!=
literal|null
condition|)
block|{
name|X509Certificate
name|singerCert
init|=
name|caClient
operator|.
name|queryCertificate
argument_list|(
literal|"certId="
operator|+
name|tokenId
operator|.
name|getOmCertSerialId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|singerCert
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BlockTokenException
argument_list|(
literal|"Can't find signer certificate "
operator|+
literal|"(OmCertSerialId: "
operator|+
name|tokenId
operator|.
name|getOmCertSerialId
argument_list|()
operator|+
literal|") of the block token for user: "
operator|+
name|tokenId
operator|.
name|getUser
argument_list|()
argument_list|)
throw|;
block|}
name|Boolean
name|validToken
init|=
name|caClient
operator|.
name|verifySignature
argument_list|(
name|tokenId
operator|.
name|getBytes
argument_list|()
argument_list|,
name|token
operator|.
name|getPassword
argument_list|()
argument_list|,
name|singerCert
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|validToken
condition|)
block|{
throw|throw
operator|new
name|BlockTokenException
argument_list|(
literal|"Invalid block token for user: "
operator|+
name|tokenId
operator|.
name|getUser
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// check expiration
if|if
condition|(
name|isExpired
argument_list|(
name|tokenId
operator|.
name|getExpiryDate
argument_list|()
argument_list|)
condition|)
block|{
name|UserGroupInformation
name|tokenUser
init|=
name|tokenId
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|tokenUser
operator|.
name|setAuthenticationMethod
argument_list|(
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|TOKEN
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BlockTokenException
argument_list|(
literal|"Expired block token for user: "
operator|+
name|tokenUser
argument_list|)
throw|;
block|}
comment|// defer access mode, bcsid and maxLength check to container dispatcher
name|UserGroupInformation
name|ugi
init|=
name|tokenId
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|setAuthenticationMethod
argument_list|(
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|TOKEN
argument_list|)
expr_stmt|;
return|return
name|ugi
return|;
block|}
else|else
block|{
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

