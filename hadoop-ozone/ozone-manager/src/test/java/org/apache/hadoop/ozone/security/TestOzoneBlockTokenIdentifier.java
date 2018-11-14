begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|InvalidKeyException
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
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchProviderException
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
name|Signature
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SignatureException
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
name|Certificate
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
name|CertificateEncodingException
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|crypto
operator|.
name|KeyGenerator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|Mac
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|lang3
operator|.
name|RandomStringUtils
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
name|lang3
operator|.
name|RandomUtils
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
name|fs
operator|.
name|FileUtil
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
name|BeforeClass
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

begin_comment
comment|/**  * Test class for OzoneManagerDelegationToken.  */
end_comment

begin_class
DECL|class|TestOzoneBlockTokenIdentifier
specifier|public
class|class
name|TestOzoneBlockTokenIdentifier
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
name|TestOzoneBlockTokenIdentifier
operator|.
name|class
argument_list|)
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
name|TestOzoneBlockTokenIdentifier
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|KEYSTORES_DIR
specifier|private
specifier|static
specifier|final
name|String
name|KEYSTORES_DIR
init|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|expiryTime
specifier|private
specifier|static
name|long
name|expiryTime
decl_stmt|;
DECL|field|keyPair
specifier|private
specifier|static
name|KeyPair
name|keyPair
decl_stmt|;
DECL|field|cert
specifier|private
specifier|static
name|X509Certificate
name|cert
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|base
operator|.
name|mkdirs
argument_list|()
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
comment|// Create Ozone Master certificate (SCM CA issued cert) and key store.
name|cert
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
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// KeyStoreTestUtil.cleanupSSLConfig(KEYSTORES_DIR, sslConfsDir);
block|}
annotation|@
name|Test
DECL|method|testSignToken ()
specifier|public
name|void
name|testSignToken
parameter_list|()
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|String
name|keystore
init|=
operator|new
name|File
argument_list|(
name|KEYSTORES_DIR
argument_list|,
literal|"keystore.jks"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|truststore
init|=
operator|new
name|File
argument_list|(
name|KEYSTORES_DIR
argument_list|,
literal|"truststore.jks"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|trustPassword
init|=
literal|"trustPass"
decl_stmt|;
name|String
name|keyStorePassword
init|=
literal|"keyStorePass"
decl_stmt|;
name|String
name|keyPassword
init|=
literal|"keyPass"
decl_stmt|;
name|KeyStoreTestUtil
operator|.
name|createKeyStore
argument_list|(
name|keystore
argument_list|,
name|keyStorePassword
argument_list|,
name|keyPassword
argument_list|,
literal|"OzoneMaster"
argument_list|,
name|keyPair
operator|.
name|getPrivate
argument_list|()
argument_list|,
name|cert
argument_list|)
expr_stmt|;
comment|// Create trust store and put the certificate in the trust store
name|Map
argument_list|<
name|String
argument_list|,
name|X509Certificate
argument_list|>
name|certs
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"server"
argument_list|,
name|cert
argument_list|)
decl_stmt|;
name|KeyStoreTestUtil
operator|.
name|createTrustStore
argument_list|(
name|truststore
argument_list|,
name|trustPassword
argument_list|,
name|certs
argument_list|)
expr_stmt|;
comment|// Sign the OzoneMaster Token with Ozone Master private key
name|PrivateKey
name|privateKey
init|=
name|keyPair
operator|.
name|getPrivate
argument_list|()
decl_stmt|;
name|OzoneBlockTokenIdentifier
name|tokenId
init|=
operator|new
name|OzoneBlockTokenIdentifier
argument_list|(
literal|"testUser"
argument_list|,
literal|"84940"
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|HddsProtos
operator|.
name|BlockTokenSecretProto
operator|.
name|AccessModeProto
operator|.
name|class
argument_list|)
argument_list|,
name|expiryTime
argument_list|,
name|cert
operator|.
name|getSerialNumber
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|signedToken
init|=
name|signTokenAsymmetric
argument_list|(
name|tokenId
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
comment|// Verify a valid signed OzoneMaster Token with Ozone Master
comment|// public key(certificate)
name|boolean
name|isValidToken
init|=
name|verifyTokenAsymmetric
argument_list|(
name|tokenId
argument_list|,
name|signedToken
argument_list|,
name|cert
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{} is {}"
argument_list|,
name|tokenId
argument_list|,
name|isValidToken
condition|?
literal|"valid."
else|:
literal|"invalid."
argument_list|)
expr_stmt|;
comment|// Verify an invalid signed OzoneMaster Token with Ozone Master
comment|// public key(certificate)
name|tokenId
operator|=
operator|new
name|OzoneBlockTokenIdentifier
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|HddsProtos
operator|.
name|BlockTokenSecretProto
operator|.
name|AccessModeProto
operator|.
name|class
argument_list|)
argument_list|,
name|expiryTime
argument_list|,
name|cert
operator|.
name|getSerialNumber
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Unsigned token {} is {}"
argument_list|,
name|tokenId
argument_list|,
name|verifyTokenAsymmetric
argument_list|(
name|tokenId
argument_list|,
name|RandomUtils
operator|.
name|nextBytes
argument_list|(
literal|128
argument_list|)
argument_list|,
name|cert
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|signTokenAsymmetric (OzoneBlockTokenIdentifier tokenId, PrivateKey privateKey)
specifier|public
name|byte
index|[]
name|signTokenAsymmetric
parameter_list|(
name|OzoneBlockTokenIdentifier
name|tokenId
parameter_list|,
name|PrivateKey
name|privateKey
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|InvalidKeyException
throws|,
name|SignatureException
block|{
name|Signature
name|rsaSignature
init|=
name|Signature
operator|.
name|getInstance
argument_list|(
literal|"SHA256withRSA"
argument_list|)
decl_stmt|;
name|rsaSignature
operator|.
name|initSign
argument_list|(
name|privateKey
argument_list|)
expr_stmt|;
name|rsaSignature
operator|.
name|update
argument_list|(
name|tokenId
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|signature
init|=
name|rsaSignature
operator|.
name|sign
argument_list|()
decl_stmt|;
return|return
name|signature
return|;
block|}
DECL|method|verifyTokenAsymmetric (OzoneBlockTokenIdentifier tokenId, byte[] signature, Certificate certificate)
specifier|public
name|boolean
name|verifyTokenAsymmetric
parameter_list|(
name|OzoneBlockTokenIdentifier
name|tokenId
parameter_list|,
name|byte
index|[]
name|signature
parameter_list|,
name|Certificate
name|certificate
parameter_list|)
throws|throws
name|InvalidKeyException
throws|,
name|NoSuchAlgorithmException
throws|,
name|SignatureException
block|{
name|Signature
name|rsaSignature
init|=
name|Signature
operator|.
name|getInstance
argument_list|(
literal|"SHA256withRSA"
argument_list|)
decl_stmt|;
name|rsaSignature
operator|.
name|initVerify
argument_list|(
name|certificate
argument_list|)
expr_stmt|;
name|rsaSignature
operator|.
name|update
argument_list|(
name|tokenId
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|isValid
init|=
name|rsaSignature
operator|.
name|verify
argument_list|(
name|signature
argument_list|)
decl_stmt|;
return|return
name|isValid
return|;
block|}
DECL|method|signTokenSymmetric (OzoneBlockTokenIdentifier identifier, Mac mac, SecretKey key)
specifier|private
name|byte
index|[]
name|signTokenSymmetric
parameter_list|(
name|OzoneBlockTokenIdentifier
name|identifier
parameter_list|,
name|Mac
name|mac
parameter_list|,
name|SecretKey
name|key
parameter_list|)
block|{
try|try
block|{
name|mac
operator|.
name|init
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidKeyException
name|ike
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid key to HMAC computation"
argument_list|,
name|ike
argument_list|)
throw|;
block|}
return|return
name|mac
operator|.
name|doFinal
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|generateTestToken ()
name|OzoneBlockTokenIdentifier
name|generateTestToken
parameter_list|()
block|{
return|return
operator|new
name|OzoneBlockTokenIdentifier
argument_list|(
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|6
argument_list|)
argument_list|,
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|5
argument_list|)
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|HddsProtos
operator|.
name|BlockTokenSecretProto
operator|.
name|AccessModeProto
operator|.
name|class
argument_list|)
argument_list|,
name|expiryTime
argument_list|,
name|cert
operator|.
name|getSerialNumber
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testAsymmetricTokenPerf ()
specifier|public
name|void
name|testAsymmetricTokenPerf
parameter_list|()
throws|throws
name|NoSuchAlgorithmException
throws|,
name|CertificateEncodingException
throws|,
name|NoSuchProviderException
throws|,
name|InvalidKeyException
throws|,
name|SignatureException
block|{
specifier|final
name|int
name|testTokenCount
init|=
literal|1000
decl_stmt|;
name|List
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|tokenIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|tokenPasswordAsym
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testTokenCount
condition|;
name|i
operator|++
control|)
block|{
name|tokenIds
operator|.
name|add
argument_list|(
name|generateTestToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|KeyPair
name|kp
init|=
name|KeyStoreTestUtil
operator|.
name|generateKeyPair
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
comment|// Create Ozone Master certificate (SCM CA issued cert) and key store
name|X509Certificate
name|certificate
decl_stmt|;
name|certificate
operator|=
name|KeyStoreTestUtil
operator|.
name|generateCertificate
argument_list|(
literal|"CN=OzoneMaster"
argument_list|,
name|kp
argument_list|,
literal|30
argument_list|,
literal|"SHA256withRSA"
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testTokenCount
condition|;
name|i
operator|++
control|)
block|{
name|tokenPasswordAsym
operator|.
name|add
argument_list|(
name|signTokenAsymmetric
argument_list|(
name|tokenIds
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|kp
operator|.
name|getPrivate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|duration
init|=
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Average token sign time with HmacSha256(RSA/1024 key) is {} ns"
argument_list|,
name|duration
operator|/
name|testTokenCount
argument_list|)
expr_stmt|;
name|startTime
operator|=
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testTokenCount
condition|;
name|i
operator|++
control|)
block|{
name|verifyTokenAsymmetric
argument_list|(
name|tokenIds
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|tokenPasswordAsym
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|certificate
argument_list|)
expr_stmt|;
block|}
name|duration
operator|=
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Average token verify time with HmacSha256(RSA/1024 key) "
operator|+
literal|"is {} ns"
argument_list|,
name|duration
operator|/
name|testTokenCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSymmetricTokenPerf ()
specifier|public
name|void
name|testSymmetricTokenPerf
parameter_list|()
block|{
name|String
name|hmacSHA1
init|=
literal|"HmacSHA1"
decl_stmt|;
name|String
name|hmacSHA256
init|=
literal|"HmacSHA256"
decl_stmt|;
name|testSymmetricTokenPerfHelper
argument_list|(
name|hmacSHA1
argument_list|,
literal|64
argument_list|)
expr_stmt|;
name|testSymmetricTokenPerfHelper
argument_list|(
name|hmacSHA256
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
block|}
DECL|method|testSymmetricTokenPerfHelper (String hmacAlgorithm, int keyLen)
specifier|public
name|void
name|testSymmetricTokenPerfHelper
parameter_list|(
name|String
name|hmacAlgorithm
parameter_list|,
name|int
name|keyLen
parameter_list|)
block|{
specifier|final
name|int
name|testTokenCount
init|=
literal|1000
decl_stmt|;
name|List
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|tokenIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|tokenPasswordSym
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testTokenCount
condition|;
name|i
operator|++
control|)
block|{
name|tokenIds
operator|.
name|add
argument_list|(
name|generateTestToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|KeyGenerator
name|keyGen
decl_stmt|;
try|try
block|{
name|keyGen
operator|=
name|KeyGenerator
operator|.
name|getInstance
argument_list|(
name|hmacAlgorithm
argument_list|)
expr_stmt|;
name|keyGen
operator|.
name|init
argument_list|(
name|keyLen
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|nsa
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't find "
operator|+
name|hmacAlgorithm
operator|+
literal|" algorithm."
argument_list|)
throw|;
block|}
name|Mac
name|mac
decl_stmt|;
try|try
block|{
name|mac
operator|=
name|Mac
operator|.
name|getInstance
argument_list|(
name|hmacAlgorithm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|nsa
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't find "
operator|+
name|hmacAlgorithm
operator|+
literal|" algorithm."
argument_list|)
throw|;
block|}
name|SecretKey
name|secretKey
init|=
name|keyGen
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testTokenCount
condition|;
name|i
operator|++
control|)
block|{
name|tokenPasswordSym
operator|.
name|add
argument_list|(
name|signTokenSymmetric
argument_list|(
name|tokenIds
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|mac
argument_list|,
name|secretKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|duration
init|=
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Average token sign time with {}({} symmetric key) is {} ns"
argument_list|,
name|hmacAlgorithm
argument_list|,
name|keyLen
argument_list|,
name|duration
operator|/
name|testTokenCount
argument_list|)
expr_stmt|;
block|}
comment|// TODO: verify certificate with a trust store
DECL|method|verifyCert (Certificate certificate)
specifier|public
name|boolean
name|verifyCert
parameter_list|(
name|Certificate
name|certificate
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

