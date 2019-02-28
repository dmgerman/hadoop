begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|conf
operator|.
name|OzoneConfiguration
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
name|OMCertificateClient
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
name|ssl
operator|.
name|KeyStoreTestUtil
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
name|test
operator|.
name|GenericTestUtils
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
name|test
operator|.
name|LambdaTestUtils
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|security
operator|.
name|KeyPair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivateKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PublicKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Signature
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_comment
comment|/**  * Test class for {@link OzoneBlockTokenSecretManager}.  */
end_comment

begin_class
DECL|class|TestOzoneBlockTokenSecretManager
specifier|public
class|class
name|TestOzoneBlockTokenSecretManager
block|{
DECL|field|secretManager
specifier|private
name|OzoneBlockTokenSecretManager
name|secretManager
decl_stmt|;
DECL|field|keyPair
specifier|private
name|KeyPair
name|keyPair
decl_stmt|;
DECL|field|x509Certificate
specifier|private
name|X509Certificate
name|x509Certificate
decl_stmt|;
DECL|field|expiryTime
specifier|private
name|long
name|expiryTime
decl_stmt|;
DECL|field|omCertSerialId
specifier|private
name|String
name|omCertSerialId
decl_stmt|;
DECL|field|client
specifier|private
name|CertificateClient
name|client
decl_stmt|;
DECL|field|BASEDIR
specifier|private
specifier|static
specifier|final
name|String
name|BASEDIR
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestOzoneBlockTokenSecretManager
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|BASEDIR
argument_list|)
expr_stmt|;
comment|// Create Ozone Master key pair.
name|keyPair
operator|=
name|KeyStoreTestUtil
operator|.
name|generateKeyPair
argument_list|(
literal|"RSA"
argument_list|)
expr_stmt|;
name|expiryTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|+
literal|60
operator|*
literal|60
operator|*
literal|24
expr_stmt|;
comment|// Create Ozone Master certificate (SCM CA issued cert) and key store.
name|SecurityConfig
name|securityConfig
init|=
operator|new
name|SecurityConfig
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|x509Certificate
operator|=
name|KeyStoreTestUtil
operator|.
name|generateCertificate
argument_list|(
literal|"CN=OzoneMaster"
argument_list|,
name|keyPair
argument_list|,
literal|30
argument_list|,
literal|"SHA256withRSA"
argument_list|)
expr_stmt|;
name|omCertSerialId
operator|=
name|x509Certificate
operator|.
name|getSerialNumber
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|secretManager
operator|=
operator|new
name|OzoneBlockTokenSecretManager
argument_list|(
name|securityConfig
argument_list|,
name|expiryTime
argument_list|,
name|omCertSerialId
argument_list|)
expr_stmt|;
name|client
operator|=
name|getCertificateClient
argument_list|(
name|securityConfig
argument_list|)
expr_stmt|;
name|client
operator|.
name|init
argument_list|()
expr_stmt|;
name|secretManager
operator|.
name|start
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|getCertificateClient (SecurityConfig secConf)
specifier|private
name|CertificateClient
name|getCertificateClient
parameter_list|(
name|SecurityConfig
name|secConf
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|OMCertificateClient
argument_list|(
name|secConf
argument_list|,
literal|"om"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|X509Certificate
name|getCertificate
parameter_list|()
block|{
return|return
name|x509Certificate
return|;
block|}
annotation|@
name|Override
specifier|public
name|PrivateKey
name|getPrivateKey
parameter_list|()
block|{
return|return
name|keyPair
operator|.
name|getPrivate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|PublicKey
name|getPublicKey
parameter_list|()
block|{
return|return
name|keyPair
operator|.
name|getPublic
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|secretManager
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGenerateToken ()
specifier|public
name|void
name|testGenerateToken
parameter_list|()
throws|throws
name|Exception
block|{
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
init|=
name|secretManager
operator|.
name|generateToken
argument_list|(
literal|"101"
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|AccessModeProto
operator|.
name|class
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|OzoneBlockTokenIdentifier
name|identifier
init|=
name|OzoneBlockTokenIdentifier
operator|.
name|readFieldsProtobuf
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// Check basic details.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|identifier
operator|.
name|getBlockId
argument_list|()
operator|.
name|equals
argument_list|(
literal|"101"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|identifier
operator|.
name|getAccessModes
argument_list|()
operator|.
name|equals
argument_list|(
name|EnumSet
operator|.
name|allOf
argument_list|(
name|AccessModeProto
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|identifier
operator|.
name|getOmCertSerialId
argument_list|()
operator|.
name|equals
argument_list|(
name|omCertSerialId
argument_list|)
argument_list|)
expr_stmt|;
name|validateHash
argument_list|(
name|token
operator|.
name|getPassword
argument_list|()
argument_list|,
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateIdentifierSuccess ()
specifier|public
name|void
name|testCreateIdentifierSuccess
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneBlockTokenIdentifier
name|btIdentifier
init|=
name|secretManager
operator|.
name|createIdentifier
argument_list|(
literal|"testUser"
argument_list|,
literal|"101"
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|AccessModeProto
operator|.
name|class
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
comment|// Check basic details.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|btIdentifier
operator|.
name|getOwnerId
argument_list|()
operator|.
name|equals
argument_list|(
literal|"testUser"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|btIdentifier
operator|.
name|getBlockId
argument_list|()
operator|.
name|equals
argument_list|(
literal|"101"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|btIdentifier
operator|.
name|getAccessModes
argument_list|()
operator|.
name|equals
argument_list|(
name|EnumSet
operator|.
name|allOf
argument_list|(
name|AccessModeProto
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|btIdentifier
operator|.
name|getOmCertSerialId
argument_list|()
operator|.
name|equals
argument_list|(
name|omCertSerialId
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|hash
init|=
name|secretManager
operator|.
name|createPassword
argument_list|(
name|btIdentifier
argument_list|)
decl_stmt|;
name|validateHash
argument_list|(
name|hash
argument_list|,
name|btIdentifier
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate hash using public key of KeyPair.    * */
DECL|method|validateHash (byte[] hash, byte[] identifier)
specifier|private
name|void
name|validateHash
parameter_list|(
name|byte
index|[]
name|hash
parameter_list|,
name|byte
index|[]
name|identifier
parameter_list|)
throws|throws
name|Exception
block|{
name|Signature
name|rsaSignature
init|=
name|Signature
operator|.
name|getInstance
argument_list|(
name|secretManager
operator|.
name|getDefaultSignatureAlgorithm
argument_list|()
argument_list|)
decl_stmt|;
name|rsaSignature
operator|.
name|initVerify
argument_list|(
name|client
operator|.
name|getPublicKey
argument_list|()
argument_list|)
expr_stmt|;
name|rsaSignature
operator|.
name|update
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rsaSignature
operator|.
name|verify
argument_list|(
name|hash
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateIdentifierFailure ()
specifier|public
name|void
name|testCreateIdentifierFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|SecurityException
operator|.
name|class
argument_list|,
literal|"Ozone block token can't be created without owner and access mode "
operator|+
literal|"information."
argument_list|,
parameter_list|()
lambda|->
block|{
name|secretManager
operator|.
name|createIdentifier
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenewToken ()
specifier|public
name|void
name|testRenewToken
parameter_list|()
throws|throws
name|Exception
block|{
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
literal|"Renew token operation is not supported for ozone block"
operator|+
literal|" tokens."
argument_list|,
parameter_list|()
lambda|->
block|{
name|secretManager
operator|.
name|renewToken
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCancelToken ()
specifier|public
name|void
name|testCancelToken
parameter_list|()
throws|throws
name|Exception
block|{
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
literal|"Cancel token operation is not supported for ozone block"
operator|+
literal|" tokens."
argument_list|,
parameter_list|()
lambda|->
block|{
name|secretManager
operator|.
name|cancelToken
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVerifySignatureFailure ()
specifier|public
name|void
name|testVerifySignatureFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneBlockTokenIdentifier
name|id
init|=
operator|new
name|OzoneBlockTokenIdentifier
argument_list|(
literal|"testUser"
argument_list|,
literal|"4234"
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|AccessModeProto
operator|.
name|class
argument_list|)
argument_list|,
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|60
operator|*
literal|60
operator|*
literal|24
argument_list|,
literal|"123444"
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
literal|"operation"
operator|+
literal|" is not supported for block tokens"
argument_list|,
parameter_list|()
lambda|->
name|secretManager
operator|.
name|verifySignature
argument_list|(
name|id
argument_list|,
name|client
operator|.
name|signData
argument_list|(
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

