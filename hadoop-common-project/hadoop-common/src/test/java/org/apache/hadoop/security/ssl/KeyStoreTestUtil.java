begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.ssl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ssl
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|AlgorithmId
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|CertificateAlgorithmId
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|CertificateIssuerName
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|CertificateSerialNumber
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|CertificateSubjectName
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|CertificateValidity
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|CertificateVersion
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|CertificateX509Key
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|X500Name
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|X509CertImpl
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|x509
operator|.
name|X509CertInfo
import|;
end_import

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
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|Writer
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
name|net
operator|.
name|URL
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
name|Key
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
name|KeyPairGenerator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStore
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
name|PrivateKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
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
name|X509Certificate
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
name|HashMap
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

begin_class
DECL|class|KeyStoreTestUtil
specifier|public
class|class
name|KeyStoreTestUtil
block|{
DECL|method|getClasspathDir (Class klass)
specifier|public
specifier|static
name|String
name|getClasspathDir
parameter_list|(
name|Class
name|klass
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|file
init|=
name|klass
operator|.
name|getName
argument_list|()
decl_stmt|;
name|file
operator|=
name|file
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
literal|".class"
expr_stmt|;
name|URL
name|url
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|String
name|baseDir
init|=
name|url
operator|.
name|toURI
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|baseDir
operator|=
name|baseDir
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|baseDir
operator|.
name|length
argument_list|()
operator|-
name|file
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|baseDir
return|;
block|}
comment|/**    * Create a self-signed X.509 Certificate.    * From http://bfo.com/blog/2011/03/08/odds_and_ends_creating_a_new_x_509_certificate.html.    *    * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"    * @param pair the KeyPair    * @param days how many days from now the Certificate is valid for    * @param algorithm the signing algorithm, eg "SHA1withRSA"    * @return the self-signed certificate    * @throws IOException thrown if an IO error ocurred.    * @throws GeneralSecurityException thrown if an Security error ocurred.    */
DECL|method|generateCertificate (String dn, KeyPair pair, int days, String algorithm)
specifier|public
specifier|static
name|X509Certificate
name|generateCertificate
parameter_list|(
name|String
name|dn
parameter_list|,
name|KeyPair
name|pair
parameter_list|,
name|int
name|days
parameter_list|,
name|String
name|algorithm
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|PrivateKey
name|privkey
init|=
name|pair
operator|.
name|getPrivate
argument_list|()
decl_stmt|;
name|X509CertInfo
name|info
init|=
operator|new
name|X509CertInfo
argument_list|()
decl_stmt|;
name|Date
name|from
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Date
name|to
init|=
operator|new
name|Date
argument_list|(
name|from
operator|.
name|getTime
argument_list|()
operator|+
name|days
operator|*
literal|86400000l
argument_list|)
decl_stmt|;
name|CertificateValidity
name|interval
init|=
operator|new
name|CertificateValidity
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|BigInteger
name|sn
init|=
operator|new
name|BigInteger
argument_list|(
literal|64
argument_list|,
operator|new
name|SecureRandom
argument_list|()
argument_list|)
decl_stmt|;
name|X500Name
name|owner
init|=
operator|new
name|X500Name
argument_list|(
name|dn
argument_list|)
decl_stmt|;
name|info
operator|.
name|set
argument_list|(
name|X509CertInfo
operator|.
name|VALIDITY
argument_list|,
name|interval
argument_list|)
expr_stmt|;
name|info
operator|.
name|set
argument_list|(
name|X509CertInfo
operator|.
name|SERIAL_NUMBER
argument_list|,
operator|new
name|CertificateSerialNumber
argument_list|(
name|sn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|set
argument_list|(
name|X509CertInfo
operator|.
name|SUBJECT
argument_list|,
operator|new
name|CertificateSubjectName
argument_list|(
name|owner
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|set
argument_list|(
name|X509CertInfo
operator|.
name|ISSUER
argument_list|,
operator|new
name|CertificateIssuerName
argument_list|(
name|owner
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|set
argument_list|(
name|X509CertInfo
operator|.
name|KEY
argument_list|,
operator|new
name|CertificateX509Key
argument_list|(
name|pair
operator|.
name|getPublic
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|set
argument_list|(
name|X509CertInfo
operator|.
name|VERSION
argument_list|,
operator|new
name|CertificateVersion
argument_list|(
name|CertificateVersion
operator|.
name|V3
argument_list|)
argument_list|)
expr_stmt|;
name|AlgorithmId
name|algo
init|=
operator|new
name|AlgorithmId
argument_list|(
name|AlgorithmId
operator|.
name|md5WithRSAEncryption_oid
argument_list|)
decl_stmt|;
name|info
operator|.
name|set
argument_list|(
name|X509CertInfo
operator|.
name|ALGORITHM_ID
argument_list|,
operator|new
name|CertificateAlgorithmId
argument_list|(
name|algo
argument_list|)
argument_list|)
expr_stmt|;
comment|// Sign the cert to identify the algorithm that's used.
name|X509CertImpl
name|cert
init|=
operator|new
name|X509CertImpl
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|cert
operator|.
name|sign
argument_list|(
name|privkey
argument_list|,
name|algorithm
argument_list|)
expr_stmt|;
comment|// Update the algorith, and resign.
name|algo
operator|=
operator|(
name|AlgorithmId
operator|)
name|cert
operator|.
name|get
argument_list|(
name|X509CertImpl
operator|.
name|SIG_ALG
argument_list|)
expr_stmt|;
name|info
operator|.
name|set
argument_list|(
name|CertificateAlgorithmId
operator|.
name|NAME
operator|+
literal|"."
operator|+
name|CertificateAlgorithmId
operator|.
name|ALGORITHM
argument_list|,
name|algo
argument_list|)
expr_stmt|;
name|cert
operator|=
operator|new
name|X509CertImpl
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|cert
operator|.
name|sign
argument_list|(
name|privkey
argument_list|,
name|algorithm
argument_list|)
expr_stmt|;
return|return
name|cert
return|;
block|}
DECL|method|generateKeyPair (String algorithm)
specifier|public
specifier|static
name|KeyPair
name|generateKeyPair
parameter_list|(
name|String
name|algorithm
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
block|{
name|KeyPairGenerator
name|keyGen
init|=
name|KeyPairGenerator
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
decl_stmt|;
name|keyGen
operator|.
name|initialize
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
return|return
name|keyGen
operator|.
name|genKeyPair
argument_list|()
return|;
block|}
DECL|method|createEmptyKeyStore ()
specifier|private
specifier|static
name|KeyStore
name|createEmptyKeyStore
parameter_list|()
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|KeyStore
name|ks
init|=
name|KeyStore
operator|.
name|getInstance
argument_list|(
literal|"JKS"
argument_list|)
decl_stmt|;
name|ks
operator|.
name|load
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// initialize
return|return
name|ks
return|;
block|}
DECL|method|saveKeyStore (KeyStore ks, String filename, String password)
specifier|private
specifier|static
name|void
name|saveKeyStore
parameter_list|(
name|KeyStore
name|ks
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
name|ks
operator|.
name|store
argument_list|(
name|out
argument_list|,
name|password
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createKeyStore (String filename, String password, String alias, Key privateKey, Certificate cert)
specifier|public
specifier|static
name|void
name|createKeyStore
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|alias
parameter_list|,
name|Key
name|privateKey
parameter_list|,
name|Certificate
name|cert
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|KeyStore
name|ks
init|=
name|createEmptyKeyStore
argument_list|()
decl_stmt|;
name|ks
operator|.
name|setKeyEntry
argument_list|(
name|alias
argument_list|,
name|privateKey
argument_list|,
name|password
operator|.
name|toCharArray
argument_list|()
argument_list|,
operator|new
name|Certificate
index|[]
block|{
name|cert
block|}
argument_list|)
expr_stmt|;
name|saveKeyStore
argument_list|(
name|ks
argument_list|,
name|filename
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
DECL|method|createTrustStore (String filename, String password, String alias, Certificate cert)
specifier|public
specifier|static
name|void
name|createTrustStore
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|alias
parameter_list|,
name|Certificate
name|cert
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|KeyStore
name|ks
init|=
name|createEmptyKeyStore
argument_list|()
decl_stmt|;
name|ks
operator|.
name|setCertificateEntry
argument_list|(
name|alias
argument_list|,
name|cert
argument_list|)
expr_stmt|;
name|saveKeyStore
argument_list|(
name|ks
argument_list|,
name|filename
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
DECL|method|createTrustStore ( String filename, String password, Map<String, T> certs)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Certificate
parameter_list|>
name|void
name|createTrustStore
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|password
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|certs
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|KeyStore
name|ks
init|=
name|createEmptyKeyStore
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|cert
range|:
name|certs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ks
operator|.
name|setCertificateEntry
argument_list|(
name|cert
operator|.
name|getKey
argument_list|()
argument_list|,
name|cert
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|saveKeyStore
argument_list|(
name|ks
argument_list|,
name|filename
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
DECL|method|cleanupSSLConfig (String keystoresDir, String sslConfDir)
specifier|public
specifier|static
name|void
name|cleanupSSLConfig
parameter_list|(
name|String
name|keystoresDir
parameter_list|,
name|String
name|sslConfDir
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|keystoresDir
operator|+
literal|"/clientKS.jks"
argument_list|)
decl_stmt|;
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|keystoresDir
operator|+
literal|"/serverKS.jks"
argument_list|)
expr_stmt|;
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|keystoresDir
operator|+
literal|"/trustKS.jks"
argument_list|)
expr_stmt|;
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|sslConfDir
operator|+
literal|"/ssl-client.xml"
argument_list|)
expr_stmt|;
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|sslConfDir
operator|+
literal|"/ssl-server.xml"
argument_list|)
expr_stmt|;
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
DECL|method|setupSSLConfig (String keystoresDir, String sslConfDir, Configuration conf, boolean useClientCert)
specifier|public
specifier|static
name|void
name|setupSSLConfig
parameter_list|(
name|String
name|keystoresDir
parameter_list|,
name|String
name|sslConfDir
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|boolean
name|useClientCert
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|clientKS
init|=
name|keystoresDir
operator|+
literal|"/clientKS.jks"
decl_stmt|;
name|String
name|clientPassword
init|=
literal|"clientP"
decl_stmt|;
name|String
name|serverKS
init|=
name|keystoresDir
operator|+
literal|"/serverKS.jks"
decl_stmt|;
name|String
name|serverPassword
init|=
literal|"serverP"
decl_stmt|;
name|String
name|trustKS
init|=
name|keystoresDir
operator|+
literal|"/trustKS.jks"
decl_stmt|;
name|String
name|trustPassword
init|=
literal|"trustP"
decl_stmt|;
name|File
name|sslClientConfFile
init|=
operator|new
name|File
argument_list|(
name|sslConfDir
operator|+
literal|"/ssl-client.xml"
argument_list|)
decl_stmt|;
name|File
name|sslServerConfFile
init|=
operator|new
name|File
argument_list|(
name|sslConfDir
operator|+
literal|"/ssl-server.xml"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|X509Certificate
argument_list|>
name|certs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|X509Certificate
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|useClientCert
condition|)
block|{
name|KeyPair
name|cKP
init|=
name|KeyStoreTestUtil
operator|.
name|generateKeyPair
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|X509Certificate
name|cCert
init|=
name|KeyStoreTestUtil
operator|.
name|generateCertificate
argument_list|(
literal|"CN=localhost, O=client"
argument_list|,
name|cKP
argument_list|,
literal|30
argument_list|,
literal|"SHA1withRSA"
argument_list|)
decl_stmt|;
name|KeyStoreTestUtil
operator|.
name|createKeyStore
argument_list|(
name|clientKS
argument_list|,
name|clientPassword
argument_list|,
literal|"client"
argument_list|,
name|cKP
operator|.
name|getPrivate
argument_list|()
argument_list|,
name|cCert
argument_list|)
expr_stmt|;
name|certs
operator|.
name|put
argument_list|(
literal|"client"
argument_list|,
name|cCert
argument_list|)
expr_stmt|;
block|}
name|KeyPair
name|sKP
init|=
name|KeyStoreTestUtil
operator|.
name|generateKeyPair
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|X509Certificate
name|sCert
init|=
name|KeyStoreTestUtil
operator|.
name|generateCertificate
argument_list|(
literal|"CN=localhost, O=server"
argument_list|,
name|sKP
argument_list|,
literal|30
argument_list|,
literal|"SHA1withRSA"
argument_list|)
decl_stmt|;
name|KeyStoreTestUtil
operator|.
name|createKeyStore
argument_list|(
name|serverKS
argument_list|,
name|serverPassword
argument_list|,
literal|"server"
argument_list|,
name|sKP
operator|.
name|getPrivate
argument_list|()
argument_list|,
name|sCert
argument_list|)
expr_stmt|;
name|certs
operator|.
name|put
argument_list|(
literal|"server"
argument_list|,
name|sCert
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|createTrustStore
argument_list|(
name|trustKS
argument_list|,
name|trustPassword
argument_list|,
name|certs
argument_list|)
expr_stmt|;
name|Configuration
name|clientSSLConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|clientSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|CLIENT
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_KEYSTORE_LOCATION_TPL_KEY
argument_list|)
argument_list|,
name|clientKS
argument_list|)
expr_stmt|;
name|clientSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|CLIENT
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_KEYSTORE_PASSWORD_TPL_KEY
argument_list|)
argument_list|,
name|clientPassword
argument_list|)
expr_stmt|;
name|clientSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|CLIENT
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_TRUSTSTORE_LOCATION_TPL_KEY
argument_list|)
argument_list|,
name|trustKS
argument_list|)
expr_stmt|;
name|clientSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|CLIENT
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_TRUSTSTORE_PASSWORD_TPL_KEY
argument_list|)
argument_list|,
name|trustPassword
argument_list|)
expr_stmt|;
name|clientSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|CLIENT
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_TRUSTSTORE_RELOAD_INTERVAL_TPL_KEY
argument_list|)
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|Configuration
name|serverSSLConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|serverSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|SERVER
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_KEYSTORE_LOCATION_TPL_KEY
argument_list|)
argument_list|,
name|serverKS
argument_list|)
expr_stmt|;
name|serverSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|SERVER
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_KEYSTORE_PASSWORD_TPL_KEY
argument_list|)
argument_list|,
name|serverPassword
argument_list|)
expr_stmt|;
name|serverSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|SERVER
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_TRUSTSTORE_LOCATION_TPL_KEY
argument_list|)
argument_list|,
name|trustKS
argument_list|)
expr_stmt|;
name|serverSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|SERVER
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_TRUSTSTORE_PASSWORD_TPL_KEY
argument_list|)
argument_list|,
name|trustPassword
argument_list|)
expr_stmt|;
name|serverSSLConf
operator|.
name|set
argument_list|(
name|FileBasedKeyStoresFactory
operator|.
name|resolvePropertyName
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|SERVER
argument_list|,
name|FileBasedKeyStoresFactory
operator|.
name|SSL_TRUSTSTORE_RELOAD_INTERVAL_TPL_KEY
argument_list|)
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|sslClientConfFile
argument_list|)
decl_stmt|;
try|try
block|{
name|clientSSLConf
operator|.
name|writeXml
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|writer
operator|=
operator|new
name|FileWriter
argument_list|(
name|sslServerConfFile
argument_list|)
expr_stmt|;
try|try
block|{
name|serverSSLConf
operator|.
name|writeXml
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|SSLFactory
operator|.
name|SSL_HOSTNAME_VERIFIER_KEY
argument_list|,
literal|"ALLOW_ALL"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SSLFactory
operator|.
name|SSL_CLIENT_CONF_KEY
argument_list|,
name|sslClientConfFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SSLFactory
operator|.
name|SSL_SERVER_CONF_KEY
argument_list|,
name|sslServerConfFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|SSLFactory
operator|.
name|SSL_REQUIRE_CLIENT_CERT_KEY
argument_list|,
name|useClientCert
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

