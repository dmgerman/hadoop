begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|webproxy
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
name|annotations
operator|.
name|VisibleForTesting
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRuntimeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|conn
operator|.
name|ssl
operator|.
name|DefaultHostnameVerifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|conn
operator|.
name|util
operator|.
name|PublicSuffixMatcherLoader
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
name|x500
operator|.
name|X500Name
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
name|AlgorithmIdentifier
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
name|BasicConstraints
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
name|asn1
operator|.
name|x509
operator|.
name|SubjectPublicKeyInfo
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
name|X509v3CertificateBuilder
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
name|bouncycastle
operator|.
name|cert
operator|.
name|jcajce
operator|.
name|JcaX509ExtensionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|crypto
operator|.
name|util
operator|.
name|PrivateKeyFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|jce
operator|.
name|provider
operator|.
name|BouncyCastleProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|operator
operator|.
name|ContentSigner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|operator
operator|.
name|DefaultDigestAlgorithmIdentifierFinder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|operator
operator|.
name|DefaultSignatureAlgorithmIdentifierFinder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|operator
operator|.
name|OperatorCreationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|operator
operator|.
name|bc
operator|.
name|BcRSAContentSignerBuilder
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|HostnameVerifier
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|KeyManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLPeerUnverifiedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManagerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|X509KeyManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|X509TrustManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|net
operator|.
name|Socket
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
name|NoSuchProviderException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Security
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
name|util
operator|.
name|Calendar
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
name|GregorianCalendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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

begin_comment
comment|/**  * Allows for the generation and acceptance of specialized HTTPS Certificates to  * be used for HTTPS communication between the AMs and the RM Proxy.  */
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
DECL|class|ProxyCA
specifier|public
class|class
name|ProxyCA
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
name|ProxyCA
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|caCert
specifier|private
name|X509Certificate
name|caCert
decl_stmt|;
DECL|field|caKeyPair
specifier|private
name|KeyPair
name|caKeyPair
decl_stmt|;
DECL|field|childTrustStore
specifier|private
name|KeyStore
name|childTrustStore
decl_stmt|;
DECL|field|srand
specifier|private
specifier|final
name|Random
name|srand
decl_stmt|;
DECL|field|defaultTrustManager
specifier|private
name|X509TrustManager
name|defaultTrustManager
decl_stmt|;
DECL|field|x509KeyManager
specifier|private
name|X509KeyManager
name|x509KeyManager
decl_stmt|;
DECL|field|hostnameVerifier
specifier|private
name|HostnameVerifier
name|hostnameVerifier
decl_stmt|;
DECL|field|SIG_ALG_ID
specifier|private
specifier|static
specifier|final
name|AlgorithmIdentifier
name|SIG_ALG_ID
init|=
operator|new
name|DefaultSignatureAlgorithmIdentifierFinder
argument_list|()
operator|.
name|find
argument_list|(
literal|"SHA512WITHRSA"
argument_list|)
decl_stmt|;
DECL|method|ProxyCA ()
specifier|public
name|ProxyCA
parameter_list|()
block|{
name|srand
operator|=
operator|new
name|SecureRandom
argument_list|()
expr_stmt|;
comment|// This only has to be done once
name|Security
operator|.
name|addProvider
argument_list|(
operator|new
name|BouncyCastleProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|createCACertAndKeyPair
argument_list|()
expr_stmt|;
name|initInternal
argument_list|()
expr_stmt|;
block|}
DECL|method|init (X509Certificate caCert, PrivateKey caPrivateKey)
specifier|public
name|void
name|init
parameter_list|(
name|X509Certificate
name|caCert
parameter_list|,
name|PrivateKey
name|caPrivateKey
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
if|if
condition|(
name|caCert
operator|==
literal|null
operator|||
name|caPrivateKey
operator|==
literal|null
operator|||
operator|!
name|verifyCertAndKeys
argument_list|(
name|caCert
argument_list|,
name|caPrivateKey
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not verify Certificate, Public Key, and Private Key: "
operator|+
literal|"regenerating"
argument_list|)
expr_stmt|;
name|createCACertAndKeyPair
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|caCert
operator|=
name|caCert
expr_stmt|;
name|this
operator|.
name|caKeyPair
operator|=
operator|new
name|KeyPair
argument_list|(
name|caCert
operator|.
name|getPublicKey
argument_list|()
argument_list|,
name|caPrivateKey
argument_list|)
expr_stmt|;
block|}
name|initInternal
argument_list|()
expr_stmt|;
block|}
DECL|method|initInternal ()
specifier|private
name|void
name|initInternal
parameter_list|()
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|defaultTrustManager
operator|=
literal|null
expr_stmt|;
name|TrustManagerFactory
name|factory
init|=
name|TrustManagerFactory
operator|.
name|getInstance
argument_list|(
name|TrustManagerFactory
operator|.
name|getDefaultAlgorithm
argument_list|()
argument_list|)
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
operator|(
name|KeyStore
operator|)
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|TrustManager
name|manager
range|:
name|factory
operator|.
name|getTrustManagers
argument_list|()
control|)
block|{
if|if
condition|(
name|manager
operator|instanceof
name|X509TrustManager
condition|)
block|{
name|defaultTrustManager
operator|=
operator|(
name|X509TrustManager
operator|)
name|manager
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|defaultTrustManager
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Could not find default X509 Trust Manager"
argument_list|)
throw|;
block|}
name|this
operator|.
name|x509KeyManager
operator|=
name|createKeyManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|hostnameVerifier
operator|=
name|createHostnameVerifier
argument_list|()
expr_stmt|;
name|this
operator|.
name|childTrustStore
operator|=
name|createTrustStore
argument_list|(
literal|"client"
argument_list|,
name|caCert
argument_list|)
expr_stmt|;
block|}
DECL|method|createCert (boolean isCa, String issuerStr, String subjectStr, Date from, Date to, PublicKey publicKey, PrivateKey privateKey)
specifier|private
name|X509Certificate
name|createCert
parameter_list|(
name|boolean
name|isCa
parameter_list|,
name|String
name|issuerStr
parameter_list|,
name|String
name|subjectStr
parameter_list|,
name|Date
name|from
parameter_list|,
name|Date
name|to
parameter_list|,
name|PublicKey
name|publicKey
parameter_list|,
name|PrivateKey
name|privateKey
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
name|X500Name
name|issuer
init|=
operator|new
name|X500Name
argument_list|(
name|issuerStr
argument_list|)
decl_stmt|;
name|X500Name
name|subject
init|=
operator|new
name|X500Name
argument_list|(
name|subjectStr
argument_list|)
decl_stmt|;
name|SubjectPublicKeyInfo
name|subPubKeyInfo
init|=
name|SubjectPublicKeyInfo
operator|.
name|getInstance
argument_list|(
name|publicKey
operator|.
name|getEncoded
argument_list|()
argument_list|)
decl_stmt|;
name|X509v3CertificateBuilder
name|certBuilder
init|=
operator|new
name|X509v3CertificateBuilder
argument_list|(
name|issuer
argument_list|,
operator|new
name|BigInteger
argument_list|(
literal|64
argument_list|,
name|srand
argument_list|)
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|subject
argument_list|,
name|subPubKeyInfo
argument_list|)
decl_stmt|;
name|AlgorithmIdentifier
name|digAlgId
init|=
operator|new
name|DefaultDigestAlgorithmIdentifierFinder
argument_list|()
operator|.
name|find
argument_list|(
name|SIG_ALG_ID
argument_list|)
decl_stmt|;
name|ContentSigner
name|contentSigner
decl_stmt|;
try|try
block|{
name|contentSigner
operator|=
operator|new
name|BcRSAContentSignerBuilder
argument_list|(
name|SIG_ALG_ID
argument_list|,
name|digAlgId
argument_list|)
operator|.
name|build
argument_list|(
name|PrivateKeyFactory
operator|.
name|createKey
argument_list|(
name|privateKey
operator|.
name|getEncoded
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OperatorCreationException
name|oce
parameter_list|)
block|{
throw|throw
operator|new
name|GeneralSecurityException
argument_list|(
name|oce
argument_list|)
throw|;
block|}
if|if
condition|(
name|isCa
condition|)
block|{
comment|// BasicConstraints(0) indicates a CA and a path length of 0.  This is
comment|// important to indicate that child certificates can't issue additional
comment|// grandchild certificates
name|certBuilder
operator|.
name|addExtension
argument_list|(
name|Extension
operator|.
name|basicConstraints
argument_list|,
literal|true
argument_list|,
operator|new
name|BasicConstraints
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// BasicConstraints(false) indicates this is not a CA
name|certBuilder
operator|.
name|addExtension
argument_list|(
name|Extension
operator|.
name|basicConstraints
argument_list|,
literal|true
argument_list|,
operator|new
name|BasicConstraints
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|certBuilder
operator|.
name|addExtension
argument_list|(
name|Extension
operator|.
name|authorityKeyIdentifier
argument_list|,
literal|false
argument_list|,
operator|new
name|JcaX509ExtensionUtils
argument_list|()
operator|.
name|createAuthorityKeyIdentifier
argument_list|(
name|caCert
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|X509CertificateHolder
name|certHolder
init|=
name|certBuilder
operator|.
name|build
argument_list|(
name|contentSigner
argument_list|)
decl_stmt|;
name|X509Certificate
name|cert
init|=
operator|new
name|JcaX509CertificateConverter
argument_list|()
operator|.
name|setProvider
argument_list|(
literal|"BC"
argument_list|)
operator|.
name|getCertificate
argument_list|(
name|certHolder
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created Certificate for {}"
argument_list|,
name|subject
argument_list|)
expr_stmt|;
return|return
name|cert
return|;
block|}
DECL|method|createCACertAndKeyPair ()
specifier|private
name|void
name|createCACertAndKeyPair
parameter_list|()
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
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
name|GregorianCalendar
argument_list|(
literal|2037
argument_list|,
name|Calendar
operator|.
name|DECEMBER
argument_list|,
literal|31
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|KeyPairGenerator
name|keyGen
init|=
name|KeyPairGenerator
operator|.
name|getInstance
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|keyGen
operator|.
name|initialize
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|caKeyPair
operator|=
name|keyGen
operator|.
name|genKeyPair
argument_list|()
expr_stmt|;
name|String
name|subject
init|=
literal|"OU=YARN-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|caCert
operator|=
name|createCert
argument_list|(
literal|true
argument_list|,
name|subject
argument_list|,
name|subject
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|caKeyPair
operator|.
name|getPublic
argument_list|()
argument_list|,
name|caKeyPair
operator|.
name|getPrivate
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"CA Certificate: \n{}"
argument_list|,
name|caCert
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createChildKeyStore (ApplicationId appId, String ksPassword)
specifier|public
name|byte
index|[]
name|createChildKeyStore
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|ksPassword
parameter_list|)
throws|throws
name|Exception
block|{
comment|// We don't check the expiration date, and this will provide further reason
comment|// for outside users to not accept these certificates
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
name|from
decl_stmt|;
name|KeyPairGenerator
name|keyGen
init|=
name|KeyPairGenerator
operator|.
name|getInstance
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|keyGen
operator|.
name|initialize
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|KeyPair
name|keyPair
init|=
name|keyGen
operator|.
name|genKeyPair
argument_list|()
decl_stmt|;
name|String
name|issuer
init|=
name|caCert
operator|.
name|getSubjectX500Principal
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|subject
init|=
literal|"CN="
operator|+
name|appId
decl_stmt|;
name|X509Certificate
name|cert
init|=
name|createCert
argument_list|(
literal|false
argument_list|,
name|issuer
argument_list|,
name|subject
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|keyPair
operator|.
name|getPublic
argument_list|()
argument_list|,
name|caKeyPair
operator|.
name|getPrivate
argument_list|()
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
name|LOG
operator|.
name|trace
argument_list|(
literal|"Certificate for {}: \n{}"
argument_list|,
name|appId
argument_list|,
name|cert
argument_list|)
expr_stmt|;
block|}
name|KeyStore
name|keyStore
init|=
name|createChildKeyStore
argument_list|(
name|ksPassword
argument_list|,
literal|"server"
argument_list|,
name|keyPair
operator|.
name|getPrivate
argument_list|()
argument_list|,
name|cert
argument_list|)
decl_stmt|;
return|return
name|keyStoreToBytes
argument_list|(
name|keyStore
argument_list|,
name|ksPassword
argument_list|)
return|;
block|}
DECL|method|getChildTrustStore (String password)
specifier|public
name|byte
index|[]
name|getChildTrustStore
parameter_list|(
name|String
name|password
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
return|return
name|keyStoreToBytes
argument_list|(
name|childTrustStore
argument_list|,
name|password
argument_list|)
return|;
block|}
DECL|method|createEmptyKeyStore ()
specifier|private
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
DECL|method|createChildKeyStore (String password, String alias, Key privateKey, Certificate cert)
specifier|private
name|KeyStore
name|createChildKeyStore
parameter_list|(
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
block|,
name|caCert
block|}
argument_list|)
expr_stmt|;
return|return
name|ks
return|;
block|}
DECL|method|generateKeyStorePassword ()
specifier|public
name|String
name|generateKeyStorePassword
parameter_list|()
block|{
return|return
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|16
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|srand
argument_list|)
return|;
block|}
DECL|method|keyStoreToBytes (KeyStore ks, String password)
specifier|private
name|byte
index|[]
name|keyStoreToBytes
parameter_list|(
name|KeyStore
name|ks
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
try|try
init|(
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
init|)
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
return|return
name|out
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
DECL|method|createTrustStore (String alias, Certificate cert)
specifier|private
name|KeyStore
name|createTrustStore
parameter_list|(
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
return|return
name|ks
return|;
block|}
DECL|method|createSSLContext (ApplicationId appId)
specifier|public
name|SSLContext
name|createSSLContext
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|GeneralSecurityException
block|{
comment|// We need the normal TrustManager, plus our custom one.  While the
comment|// SSLContext accepts an array of TrustManagers, the docs indicate that only
comment|// the first instance of any particular implementation type is used
comment|// (e.g. X509KeyManager) - this means that simply putting both TrustManagers
comment|// in won't work.  We need to have ours do both.
name|TrustManager
index|[]
name|trustManagers
init|=
operator|new
name|TrustManager
index|[]
block|{
name|createTrustManager
argument_list|(
name|appId
argument_list|)
block|}
decl_stmt|;
name|KeyManager
index|[]
name|keyManagers
init|=
operator|new
name|KeyManager
index|[]
block|{
name|x509KeyManager
block|}
decl_stmt|;
name|SSLContext
name|sc
init|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"SSL"
argument_list|)
decl_stmt|;
name|sc
operator|.
name|init
argument_list|(
name|keyManagers
argument_list|,
name|trustManagers
argument_list|,
operator|new
name|SecureRandom
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sc
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|createTrustManager (ApplicationId appId)
name|X509TrustManager
name|createTrustManager
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
return|return
operator|new
name|X509TrustManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
index|[]
name|getAcceptedIssuers
parameter_list|()
block|{
return|return
name|defaultTrustManager
operator|.
name|getAcceptedIssuers
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkClientTrusted
parameter_list|(
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
index|[]
name|certs
parameter_list|,
name|String
name|authType
parameter_list|)
block|{
comment|// not used
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkServerTrusted
parameter_list|(
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
index|[]
name|certs
parameter_list|,
name|String
name|authType
parameter_list|)
throws|throws
name|CertificateException
block|{
comment|// Our certs will always have 2 in the chain, with 0 being the app's
comment|// cert and 1 being the RM's cert
name|boolean
name|issuedByRM
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|certs
operator|.
name|length
operator|==
literal|2
condition|)
block|{
try|try
block|{
comment|// We can verify both certs using the CA cert's public key - the
comment|// child cert's info is not needed
name|certs
index|[
literal|0
index|]
operator|.
name|verify
argument_list|(
name|caKeyPair
operator|.
name|getPublic
argument_list|()
argument_list|)
expr_stmt|;
name|certs
index|[
literal|1
index|]
operator|.
name|verify
argument_list|(
name|caKeyPair
operator|.
name|getPublic
argument_list|()
argument_list|)
expr_stmt|;
name|issuedByRM
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CertificateException
decl||
name|NoSuchAlgorithmException
decl||
name|InvalidKeyException
decl||
name|NoSuchProviderException
decl||
name|SignatureException
name|e
parameter_list|)
block|{
comment|// Fall back to the default trust manager
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not verify certificate with RM CA, falling "
operator|+
literal|"back to default"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|defaultTrustManager
operator|.
name|checkServerTrusted
argument_list|(
name|certs
argument_list|,
name|authType
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Certificate not issued by RM CA, falling back to "
operator|+
literal|"default"
argument_list|)
expr_stmt|;
name|defaultTrustManager
operator|.
name|checkServerTrusted
argument_list|(
name|certs
argument_list|,
name|authType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|issuedByRM
condition|)
block|{
comment|// Check that it has the correct App ID
if|if
condition|(
operator|!
name|certs
index|[
literal|0
index|]
operator|.
name|getSubjectX500Principal
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"CN="
operator|+
name|appId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CertificateException
argument_list|(
literal|"Expected to find Subject X500 Principal with CN="
operator|+
name|appId
operator|+
literal|" but found "
operator|+
name|certs
index|[
literal|0
index|]
operator|.
name|getSubjectX500Principal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Verified certificate signed by RM CA"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getX509KeyManager ()
name|X509KeyManager
name|getX509KeyManager
parameter_list|()
block|{
return|return
name|x509KeyManager
return|;
block|}
DECL|method|createKeyManager ()
specifier|private
name|X509KeyManager
name|createKeyManager
parameter_list|()
block|{
return|return
operator|new
name|X509KeyManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getClientAliases
parameter_list|(
name|String
name|s
parameter_list|,
name|Principal
index|[]
name|principals
parameter_list|)
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"client"
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|chooseClientAlias
parameter_list|(
name|String
index|[]
name|strings
parameter_list|,
name|Principal
index|[]
name|principals
parameter_list|,
name|Socket
name|socket
parameter_list|)
block|{
return|return
literal|"client"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getServerAliases
parameter_list|(
name|String
name|s
parameter_list|,
name|Principal
index|[]
name|principals
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|chooseServerAlias
parameter_list|(
name|String
name|s
parameter_list|,
name|Principal
index|[]
name|principals
parameter_list|,
name|Socket
name|socket
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|X509Certificate
index|[]
name|getCertificateChain
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|X509Certificate
index|[]
block|{
name|caCert
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|PrivateKey
name|getPrivateKey
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|caKeyPair
operator|.
name|getPrivate
argument_list|()
return|;
block|}
block|}
return|;
block|}
DECL|method|getHostnameVerifier ()
specifier|public
name|HostnameVerifier
name|getHostnameVerifier
parameter_list|()
block|{
return|return
name|hostnameVerifier
return|;
block|}
DECL|method|createHostnameVerifier ()
specifier|private
name|HostnameVerifier
name|createHostnameVerifier
parameter_list|()
block|{
name|HostnameVerifier
name|defaultHostnameVerifier
init|=
operator|new
name|DefaultHostnameVerifier
argument_list|(
name|PublicSuffixMatcherLoader
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|HostnameVerifier
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|verify
parameter_list|(
name|String
name|host
parameter_list|,
name|SSLSession
name|sslSession
parameter_list|)
block|{
try|try
block|{
name|Certificate
index|[]
name|certs
init|=
name|sslSession
operator|.
name|getPeerCertificates
argument_list|()
decl_stmt|;
if|if
condition|(
name|certs
operator|.
name|length
operator|==
literal|2
condition|)
block|{
comment|// Make sure this is one of our certs.  More thorough checking would
comment|// have already been done by the SSLContext
name|certs
index|[
literal|0
index|]
operator|.
name|verify
argument_list|(
name|caKeyPair
operator|.
name|getPublic
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Verified certificate signed by RM CA, "
operator|+
literal|"skipping hostname verification"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SSLPeerUnverifiedException
name|e
parameter_list|)
block|{
comment|// No certificate
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|CertificateException
decl||
name|NoSuchAlgorithmException
decl||
name|InvalidKeyException
decl||
name|SignatureException
decl||
name|NoSuchProviderException
name|e
parameter_list|)
block|{
comment|// fall back to normal verifier below
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not verify certificate with RM CA, "
operator|+
literal|"falling back to default hostname verification"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|defaultHostnameVerifier
operator|.
name|verify
argument_list|(
name|host
argument_list|,
name|sslSession
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setDefaultTrustManager (X509TrustManager trustManager)
name|void
name|setDefaultTrustManager
parameter_list|(
name|X509TrustManager
name|trustManager
parameter_list|)
block|{
name|this
operator|.
name|defaultTrustManager
operator|=
name|trustManager
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCaCert ()
specifier|public
name|X509Certificate
name|getCaCert
parameter_list|()
block|{
return|return
name|caCert
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCaKeyPair ()
specifier|public
name|KeyPair
name|getCaKeyPair
parameter_list|()
block|{
return|return
name|caKeyPair
return|;
block|}
DECL|method|verifyCertAndKeys (X509Certificate cert, PrivateKey privateKey)
specifier|private
name|boolean
name|verifyCertAndKeys
parameter_list|(
name|X509Certificate
name|cert
parameter_list|,
name|PrivateKey
name|privateKey
parameter_list|)
throws|throws
name|GeneralSecurityException
block|{
name|PublicKey
name|publicKey
init|=
name|cert
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|2000
index|]
decl_stmt|;
name|srand
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Signature
name|signer
init|=
name|Signature
operator|.
name|getInstance
argument_list|(
literal|"SHA512withRSA"
argument_list|)
decl_stmt|;
name|signer
operator|.
name|initSign
argument_list|(
name|privateKey
argument_list|)
expr_stmt|;
name|signer
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|byte
index|[]
name|sig
init|=
name|signer
operator|.
name|sign
argument_list|()
decl_stmt|;
name|signer
operator|=
name|Signature
operator|.
name|getInstance
argument_list|(
literal|"SHA512withRSA"
argument_list|)
expr_stmt|;
name|signer
operator|.
name|initVerify
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|signer
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|signer
operator|.
name|verify
argument_list|(
name|sig
argument_list|)
return|;
block|}
block|}
end_class

end_unit

