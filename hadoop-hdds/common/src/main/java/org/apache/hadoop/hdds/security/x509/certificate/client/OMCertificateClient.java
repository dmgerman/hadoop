begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.certificate.client
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
name|x509
operator|.
name|certificate
operator|.
name|client
package|;
end_package

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
name|exceptions
operator|.
name|CertificateException
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
operator|.
name|InitResponse
operator|.
name|FAILURE
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
operator|.
name|InitResponse
operator|.
name|GETCERT
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
operator|.
name|InitResponse
operator|.
name|RECOVER
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
operator|.
name|InitResponse
operator|.
name|SUCCESS
import|;
end_import

begin_comment
comment|/**  * Certificate client for OzoneManager.  */
end_comment

begin_class
DECL|class|OMCertificateClient
specifier|public
class|class
name|OMCertificateClient
extends|extends
name|DefaultCertificateClient
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
name|OMCertificateClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|OMCertificateClient (SecurityConfig securityConfig, String component)
name|OMCertificateClient
parameter_list|(
name|SecurityConfig
name|securityConfig
parameter_list|,
name|String
name|component
parameter_list|)
block|{
name|super
argument_list|(
name|securityConfig
argument_list|,
name|component
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
block|}
DECL|method|handleCase (InitCase init)
specifier|protected
name|InitResponse
name|handleCase
parameter_list|(
name|InitCase
name|init
parameter_list|)
throws|throws
name|CertificateException
block|{
switch|switch
condition|(
name|init
condition|)
block|{
case|case
name|NONE
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating keypair for client as keypair and certificate not "
operator|+
literal|"found."
argument_list|)
expr_stmt|;
name|bootstrapClientKeys
argument_list|()
expr_stmt|;
return|return
name|GETCERT
return|;
case|case
name|CERT
case|:
name|LOG
operator|.
name|error
argument_list|(
literal|"Private key not found, while certificate is still present."
operator|+
literal|"Delete keypair and try again."
argument_list|)
expr_stmt|;
return|return
name|FAILURE
return|;
case|case
name|PUBLIC_KEY
case|:
name|LOG
operator|.
name|error
argument_list|(
literal|"Found public key but private key and certificate missing."
argument_list|)
expr_stmt|;
return|return
name|FAILURE
return|;
case|case
name|PRIVATE_KEY
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Found private key but public key and certificate is missing."
argument_list|)
expr_stmt|;
comment|// TODO: Recovering public key from private might be possible in some
comment|//  cases.
return|return
name|FAILURE
return|;
case|case
name|PUBLICKEY_CERT
case|:
name|LOG
operator|.
name|error
argument_list|(
literal|"Found public key and certificate but private key is "
operator|+
literal|"missing."
argument_list|)
expr_stmt|;
return|return
name|FAILURE
return|;
case|case
name|PRIVATEKEY_CERT
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Found private key and certificate but public key missing."
argument_list|)
expr_stmt|;
if|if
condition|(
name|recoverPublicKey
argument_list|()
condition|)
block|{
return|return
name|SUCCESS
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Public key recovery failed."
argument_list|)
expr_stmt|;
return|return
name|FAILURE
return|;
block|}
case|case
name|PUBLICKEY_PRIVATEKEY
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Found private and public key but certificate is missing."
argument_list|)
expr_stmt|;
if|if
condition|(
name|validateKeyPair
argument_list|(
name|getPublicKey
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|RECOVER
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Keypair validation failed."
argument_list|)
expr_stmt|;
return|return
name|FAILURE
return|;
block|}
case|case
name|ALL
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Found certificate file along with KeyPair."
argument_list|)
expr_stmt|;
if|if
condition|(
name|validateKeyPairAndCertificate
argument_list|()
condition|)
block|{
return|return
name|SUCCESS
return|;
block|}
else|else
block|{
return|return
name|FAILURE
return|;
block|}
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected case: {}, Private key:{} , "
operator|+
literal|"public key:{}, certificate:{}"
argument_list|,
name|init
argument_list|,
operator|(
operator|(
name|init
operator|.
name|ordinal
argument_list|()
operator|&
literal|1
operator|<<
literal|2
operator|)
operator|==
literal|1
operator|)
argument_list|,
operator|(
operator|(
name|init
operator|.
name|ordinal
argument_list|()
operator|&
literal|1
operator|<<
literal|1
operator|)
operator|==
literal|1
operator|)
argument_list|,
operator|(
operator|(
name|init
operator|.
name|ordinal
argument_list|()
operator|&
literal|1
operator|<<
literal|0
operator|)
operator|==
literal|1
operator|)
argument_list|)
expr_stmt|;
return|return
name|FAILURE
return|;
block|}
block|}
DECL|method|getLogger ()
specifier|public
name|Logger
name|getLogger
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
block|}
end_class

end_unit

