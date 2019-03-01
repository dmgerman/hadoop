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
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|utils
operator|.
name|CertificateCodec
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
name|keys
operator|.
name|HDDSKeyGenerator
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
name|keys
operator|.
name|KeyCodec
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
name|ozone
operator|.
name|OzoneSecurityUtil
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
name|bouncycastle
operator|.
name|cert
operator|.
name|X509CertificateHolder
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|HddsConfigKeys
operator|.
name|HDDS_METADATA_DIR_NAME
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test class for {@link DefaultCertificateClient}.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
annotation|@
name|SuppressWarnings
argument_list|(
literal|"visibilitymodifier"
argument_list|)
DECL|class|TestCertificateClientInit
specifier|public
class|class
name|TestCertificateClientInit
block|{
DECL|field|dnCertificateClient
specifier|private
name|CertificateClient
name|dnCertificateClient
decl_stmt|;
DECL|field|omCertificateClient
specifier|private
name|CertificateClient
name|omCertificateClient
decl_stmt|;
DECL|field|keyGenerator
specifier|private
name|HDDSKeyGenerator
name|keyGenerator
decl_stmt|;
DECL|field|metaDirPath
specifier|private
name|Path
name|metaDirPath
decl_stmt|;
DECL|field|securityConfig
specifier|private
name|SecurityConfig
name|securityConfig
decl_stmt|;
DECL|field|keyCodec
specifier|private
name|KeyCodec
name|keyCodec
decl_stmt|;
annotation|@
name|Parameter
DECL|field|pvtKeyPresent
specifier|public
name|boolean
name|pvtKeyPresent
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
literal|1
argument_list|)
DECL|field|pubKeyPresent
specifier|public
name|boolean
name|pubKeyPresent
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
literal|2
argument_list|)
DECL|field|certPresent
specifier|public
name|boolean
name|certPresent
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
literal|3
argument_list|)
DECL|field|expectedResult
specifier|public
name|InitResponse
name|expectedResult
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|initData ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|initData
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|false
block|,
literal|false
block|,
literal|false
block|,
name|GETCERT
block|}
block|,
block|{
literal|false
block|,
literal|false
block|,
literal|true
block|,
name|FAILURE
block|}
block|,
block|{
literal|false
block|,
literal|true
block|,
literal|false
block|,
name|FAILURE
block|}
block|,
block|{
literal|true
block|,
literal|false
block|,
literal|false
block|,
name|FAILURE
block|}
block|,
block|{
literal|false
block|,
literal|true
block|,
literal|true
block|,
name|FAILURE
block|}
block|,
block|{
literal|true
block|,
literal|true
block|,
literal|false
block|,
name|GETCERT
block|}
block|,
block|{
literal|true
block|,
literal|false
block|,
literal|true
block|,
name|SUCCESS
block|}
block|,
block|{
literal|true
block|,
literal|true
block|,
literal|true
block|,
name|SUCCESS
block|}
block|}
argument_list|)
return|;
block|}
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
name|config
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|metaDirPath
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|HDDS_METADATA_DIR_NAME
argument_list|,
name|metaDirPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|securityConfig
operator|=
operator|new
name|SecurityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|dnCertificateClient
operator|=
operator|new
name|DNCertificateClient
argument_list|(
name|securityConfig
argument_list|)
expr_stmt|;
name|omCertificateClient
operator|=
operator|new
name|OMCertificateClient
argument_list|(
name|securityConfig
argument_list|)
expr_stmt|;
name|keyGenerator
operator|=
operator|new
name|HDDSKeyGenerator
argument_list|(
name|securityConfig
argument_list|)
expr_stmt|;
name|keyCodec
operator|=
operator|new
name|KeyCodec
argument_list|(
name|securityConfig
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|dnCertificateClient
operator|=
literal|null
expr_stmt|;
name|omCertificateClient
operator|=
literal|null
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|metaDirPath
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitDatanode ()
specifier|public
name|void
name|testInitDatanode
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyPair
name|keyPair
init|=
name|keyGenerator
operator|.
name|generateKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|pvtKeyPresent
condition|)
block|{
name|keyCodec
operator|.
name|writePrivateKey
argument_list|(
name|keyPair
operator|.
name|getPrivate
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pubKeyPresent
condition|)
block|{
if|if
condition|(
name|dnCertificateClient
operator|.
name|getPublicKey
argument_list|()
operator|==
literal|null
condition|)
block|{
name|keyCodec
operator|.
name|writePublicKey
argument_list|(
name|keyPair
operator|.
name|getPublic
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|certPresent
condition|)
block|{
name|X509Certificate
name|x509Certificate
init|=
name|KeyStoreTestUtil
operator|.
name|generateCertificate
argument_list|(
literal|"CN=Test"
argument_list|,
name|keyPair
argument_list|,
literal|10
argument_list|,
name|securityConfig
operator|.
name|getSignatureAlgo
argument_list|()
argument_list|)
decl_stmt|;
name|CertificateCodec
name|codec
init|=
operator|new
name|CertificateCodec
argument_list|(
name|securityConfig
argument_list|)
decl_stmt|;
name|codec
operator|.
name|writeCertificate
argument_list|(
operator|new
name|X509CertificateHolder
argument_list|(
name|x509Certificate
operator|.
name|getEncoded
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getCertificateFileName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|InitResponse
name|response
init|=
name|dnCertificateClient
operator|.
name|init
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|equals
argument_list|(
name|expectedResult
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|response
operator|.
name|equals
argument_list|(
name|FAILURE
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|OzoneSecurityUtil
operator|.
name|checkIfFileExist
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|OzoneSecurityUtil
operator|.
name|checkIfFileExist
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInitOzoneManager ()
specifier|public
name|void
name|testInitOzoneManager
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyPair
name|keyPair
init|=
name|keyGenerator
operator|.
name|generateKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|pvtKeyPresent
condition|)
block|{
name|keyCodec
operator|.
name|writePrivateKey
argument_list|(
name|keyPair
operator|.
name|getPrivate
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pubKeyPresent
condition|)
block|{
if|if
condition|(
name|omCertificateClient
operator|.
name|getPublicKey
argument_list|()
operator|==
literal|null
condition|)
block|{
name|keyCodec
operator|.
name|writePublicKey
argument_list|(
name|keyPair
operator|.
name|getPublic
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|certPresent
condition|)
block|{
name|X509Certificate
name|x509Certificate
init|=
name|KeyStoreTestUtil
operator|.
name|generateCertificate
argument_list|(
literal|"CN=Test"
argument_list|,
name|keyPair
argument_list|,
literal|10
argument_list|,
name|securityConfig
operator|.
name|getSignatureAlgo
argument_list|()
argument_list|)
decl_stmt|;
name|CertificateCodec
name|codec
init|=
operator|new
name|CertificateCodec
argument_list|(
name|securityConfig
argument_list|)
decl_stmt|;
name|codec
operator|.
name|writeCertificate
argument_list|(
operator|new
name|X509CertificateHolder
argument_list|(
name|x509Certificate
operator|.
name|getEncoded
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getCertificateFileName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|InitResponse
name|response
init|=
name|omCertificateClient
operator|.
name|init
argument_list|()
decl_stmt|;
if|if
condition|(
name|pvtKeyPresent
operator|&&
name|pubKeyPresent
operator|&
operator|!
name|certPresent
condition|)
block|{
name|assertTrue
argument_list|(
name|response
operator|.
name|equals
argument_list|(
name|RECOVER
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|response
operator|.
name|equals
argument_list|(
name|expectedResult
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|response
operator|.
name|equals
argument_list|(
name|FAILURE
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|OzoneSecurityUtil
operator|.
name|checkIfFileExist
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|OzoneSecurityUtil
operator|.
name|checkIfFileExist
argument_list|(
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

