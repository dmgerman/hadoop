begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.certificates
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
name|certificates
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
name|exceptions
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
name|keys
operator|.
name|HDDSKeyGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|Extension
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
name|bouncycastle
operator|.
name|cert
operator|.
name|jcajce
operator|.
name|JcaX509CertificateConverter
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
name|Rule
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
name|rules
operator|.
name|TemporaryFolder
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
name|math
operator|.
name|BigInteger
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
name|CertificateException
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
name|time
operator|.
name|Duration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|OZONE_METADATA_DIRS
import|;
end_import

begin_comment
comment|/**  * Test Class for Root Certificate generation.  */
end_comment

begin_class
DECL|class|TestRootCertificate
specifier|public
class|class
name|TestRootCertificate
block|{
DECL|field|securityConfig
specifier|private
name|SecurityConfig
name|securityConfig
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
annotation|@
name|Rule
DECL|field|temporaryFolder
specifier|public
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|OZONE_METADATA_DIRS
argument_list|,
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
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
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllFieldsAreExpected ()
specifier|public
name|void
name|testAllFieldsAreExpected
parameter_list|()
throws|throws
name|SCMSecurityException
throws|,
name|NoSuchProviderException
throws|,
name|NoSuchAlgorithmException
throws|,
name|CertificateException
throws|,
name|SignatureException
throws|,
name|InvalidKeyException
block|{
name|Instant
name|now
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|Date
name|notBefore
init|=
name|Date
operator|.
name|from
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|Date
name|notAfter
init|=
name|Date
operator|.
name|from
argument_list|(
name|now
operator|.
name|plus
argument_list|(
name|Duration
operator|.
name|ofDays
argument_list|(
literal|365
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|clusterID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|scmID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|subject
init|=
literal|"testRootCert"
decl_stmt|;
name|HDDSKeyGenerator
name|keyGen
init|=
operator|new
name|HDDSKeyGenerator
argument_list|(
name|securityConfig
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|KeyPair
name|keyPair
init|=
name|keyGen
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|SelfSignedCertificate
operator|.
name|Builder
name|builder
init|=
name|SelfSignedCertificate
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBeginDate
argument_list|(
name|notBefore
argument_list|)
operator|.
name|setEndDate
argument_list|(
name|notAfter
argument_list|)
operator|.
name|setClusterID
argument_list|(
name|clusterID
argument_list|)
operator|.
name|setScmID
argument_list|(
name|scmID
argument_list|)
operator|.
name|setSubject
argument_list|(
name|subject
argument_list|)
operator|.
name|setKey
argument_list|(
name|keyPair
argument_list|)
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|X509CertificateHolder
name|certificateHolder
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|//Assert that we indeed have a self signed certificate.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|certificateHolder
operator|.
name|getIssuer
argument_list|()
argument_list|,
name|certificateHolder
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make sure that NotBefore is before the current Date
name|Date
name|invalidDate
init|=
name|Date
operator|.
name|from
argument_list|(
name|now
operator|.
name|minus
argument_list|(
name|Duration
operator|.
name|ofDays
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|certificateHolder
operator|.
name|getNotBefore
argument_list|()
operator|.
name|before
argument_list|(
name|invalidDate
argument_list|)
argument_list|)
expr_stmt|;
comment|//Make sure the end date is honored.
name|invalidDate
operator|=
name|Date
operator|.
name|from
argument_list|(
name|now
operator|.
name|plus
argument_list|(
name|Duration
operator|.
name|ofDays
argument_list|(
literal|366
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|certificateHolder
operator|.
name|getNotAfter
argument_list|()
operator|.
name|after
argument_list|(
name|invalidDate
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check the Subject Name and Issuer Name is in the expected format.
name|String
name|dnName
init|=
name|String
operator|.
name|format
argument_list|(
name|SelfSignedCertificate
operator|.
name|getNameFormat
argument_list|()
argument_list|,
name|subject
argument_list|,
name|scmID
argument_list|,
name|clusterID
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|certificateHolder
operator|.
name|getIssuer
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|dnName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|certificateHolder
operator|.
name|getSubject
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|dnName
argument_list|)
expr_stmt|;
comment|// We did not ask for this Certificate to be a CA certificate, hence that
comment|// extension should be null.
name|Assert
operator|.
name|assertNull
argument_list|(
name|certificateHolder
operator|.
name|getExtension
argument_list|(
name|Extension
operator|.
name|basicConstraints
argument_list|)
argument_list|)
expr_stmt|;
comment|// Extract the Certificate and verify that certificate matches the public
comment|// key.
name|X509Certificate
name|cert
init|=
operator|new
name|JcaX509CertificateConverter
argument_list|()
operator|.
name|getCertificate
argument_list|(
name|certificateHolder
argument_list|)
decl_stmt|;
name|cert
operator|.
name|verify
argument_list|(
name|keyPair
operator|.
name|getPublic
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCACert ()
specifier|public
name|void
name|testCACert
parameter_list|()
throws|throws
name|SCMSecurityException
throws|,
name|NoSuchProviderException
throws|,
name|NoSuchAlgorithmException
block|{
name|Instant
name|now
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|Date
name|notBefore
init|=
name|Date
operator|.
name|from
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|Date
name|notAfter
init|=
name|Date
operator|.
name|from
argument_list|(
name|now
operator|.
name|plus
argument_list|(
name|Duration
operator|.
name|ofDays
argument_list|(
literal|365
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|clusterID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|scmID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|subject
init|=
literal|"testRootCert"
decl_stmt|;
name|HDDSKeyGenerator
name|keyGen
init|=
operator|new
name|HDDSKeyGenerator
argument_list|(
name|securityConfig
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|KeyPair
name|keyPair
init|=
name|keyGen
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|SelfSignedCertificate
operator|.
name|Builder
name|builder
init|=
name|SelfSignedCertificate
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBeginDate
argument_list|(
name|notBefore
argument_list|)
operator|.
name|setEndDate
argument_list|(
name|notAfter
argument_list|)
operator|.
name|setClusterID
argument_list|(
name|clusterID
argument_list|)
operator|.
name|setScmID
argument_list|(
name|scmID
argument_list|)
operator|.
name|setSubject
argument_list|(
name|subject
argument_list|)
operator|.
name|setKey
argument_list|(
name|keyPair
argument_list|)
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
operator|.
name|makeCA
argument_list|()
decl_stmt|;
name|X509CertificateHolder
name|certificateHolder
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// This time we asked for a CA Certificate, make sure that extension is
comment|// present and valid.
name|Extension
name|basicExt
init|=
name|certificateHolder
operator|.
name|getExtension
argument_list|(
name|Extension
operator|.
name|basicConstraints
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|basicExt
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|basicExt
operator|.
name|isCritical
argument_list|()
argument_list|)
expr_stmt|;
comment|// Since this code assigns ONE for the root certificate, we check if the
comment|// serial number is the expected number.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|certificateHolder
operator|.
name|getSerialNumber
argument_list|()
argument_list|,
name|BigInteger
operator|.
name|ONE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidParamFails ()
specifier|public
name|void
name|testInvalidParamFails
parameter_list|()
throws|throws
name|SCMSecurityException
throws|,
name|NoSuchProviderException
throws|,
name|NoSuchAlgorithmException
block|{
name|Instant
name|now
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|Date
name|notBefore
init|=
name|Date
operator|.
name|from
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|Date
name|notAfter
init|=
name|Date
operator|.
name|from
argument_list|(
name|now
operator|.
name|plus
argument_list|(
name|Duration
operator|.
name|ofDays
argument_list|(
literal|365
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|clusterID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|scmID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|subject
init|=
literal|"testRootCert"
decl_stmt|;
name|HDDSKeyGenerator
name|keyGen
init|=
operator|new
name|HDDSKeyGenerator
argument_list|(
name|securityConfig
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|KeyPair
name|keyPair
init|=
name|keyGen
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|SelfSignedCertificate
operator|.
name|Builder
name|builder
init|=
name|SelfSignedCertificate
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBeginDate
argument_list|(
name|notBefore
argument_list|)
operator|.
name|setEndDate
argument_list|(
name|notAfter
argument_list|)
operator|.
name|setClusterID
argument_list|(
name|clusterID
argument_list|)
operator|.
name|setScmID
argument_list|(
name|scmID
argument_list|)
operator|.
name|setSubject
argument_list|(
name|subject
argument_list|)
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
operator|.
name|setKey
argument_list|(
name|keyPair
argument_list|)
operator|.
name|makeCA
argument_list|()
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|setKey
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Null Key should have failed."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
decl||
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|builder
operator|.
name|setKey
argument_list|(
name|keyPair
argument_list|)
expr_stmt|;
block|}
comment|// Now try with Blank Subject.
try|try
block|{
name|builder
operator|.
name|setSubject
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Null/Blank Subject should have thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|builder
operator|.
name|setSubject
argument_list|(
name|subject
argument_list|)
expr_stmt|;
block|}
comment|// Now try with blank/null SCM ID
try|try
block|{
name|builder
operator|.
name|setScmID
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Null/Blank SCM ID should have thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|builder
operator|.
name|setScmID
argument_list|(
name|scmID
argument_list|)
expr_stmt|;
block|}
comment|// Now try with blank/null SCM ID
try|try
block|{
name|builder
operator|.
name|setClusterID
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Null/Blank Cluster ID should have thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|builder
operator|.
name|setClusterID
argument_list|(
name|clusterID
argument_list|)
expr_stmt|;
block|}
comment|// Swap the Begin and End Date and verify that we cannot create a
comment|// certificate like that.
try|try
block|{
name|builder
operator|.
name|setBeginDate
argument_list|(
name|notAfter
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setEndDate
argument_list|(
name|notBefore
argument_list|)
expr_stmt|;
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Illegal dates should have thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|builder
operator|.
name|setBeginDate
argument_list|(
name|notBefore
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setEndDate
argument_list|(
name|notAfter
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|KeyPair
name|newKey
init|=
name|keyGen
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|KeyPair
name|wrongKey
init|=
operator|new
name|KeyPair
argument_list|(
name|newKey
operator|.
name|getPublic
argument_list|()
argument_list|,
name|keyPair
operator|.
name|getPrivate
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setKey
argument_list|(
name|wrongKey
argument_list|)
expr_stmt|;
name|X509CertificateHolder
name|certificateHolder
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|X509Certificate
name|cert
init|=
operator|new
name|JcaX509CertificateConverter
argument_list|()
operator|.
name|getCertificate
argument_list|(
name|certificateHolder
argument_list|)
decl_stmt|;
name|cert
operator|.
name|verify
argument_list|(
name|wrongKey
operator|.
name|getPublic
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Invalid Key, should have thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SCMSecurityException
decl||
name|CertificateException
decl||
name|SignatureException
decl||
name|InvalidKeyException
name|e
parameter_list|)
block|{
name|builder
operator|.
name|setKey
argument_list|(
name|keyPair
argument_list|)
expr_stmt|;
block|}
comment|// Assert that we can create a certificate with all sane params.
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

