begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.certificates.utils
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
operator|.
name|utils
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
name|Preconditions
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
name|conf
operator|.
name|Configuration
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
name|exceptions
operator|.
name|CertificateException
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
name|SecurityUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Strings
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
name|DEROctetString
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
name|pkcs
operator|.
name|PKCSObjectIdentifiers
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
name|Extensions
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
name|GeneralName
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
name|GeneralNames
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
name|KeyUsage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|openssl
operator|.
name|jcajce
operator|.
name|JcaPEMWriter
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
name|jcajce
operator|.
name|JcaContentSignerBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|pkcs
operator|.
name|PKCS10CertificationRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|pkcs
operator|.
name|PKCS10CertificationRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|pkcs
operator|.
name|jcajce
operator|.
name|JcaPKCS10CertificationRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|util
operator|.
name|io
operator|.
name|pem
operator|.
name|PemObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|util
operator|.
name|io
operator|.
name|pem
operator|.
name|PemReader
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|ArrayList
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
name|Optional
import|;
end_import

begin_comment
comment|/**  * A certificate sign request object that wraps operations to build a  * PKCS10CertificationRequest to CertificateServer.  */
end_comment

begin_class
DECL|class|CertificateSignRequest
specifier|public
specifier|final
class|class
name|CertificateSignRequest
block|{
DECL|field|keyPair
specifier|private
specifier|final
name|KeyPair
name|keyPair
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|SecurityConfig
name|config
decl_stmt|;
DECL|field|extensions
specifier|private
specifier|final
name|Extensions
name|extensions
decl_stmt|;
DECL|field|subject
specifier|private
name|String
name|subject
decl_stmt|;
DECL|field|clusterID
specifier|private
name|String
name|clusterID
decl_stmt|;
DECL|field|scmID
specifier|private
name|String
name|scmID
decl_stmt|;
comment|/**    * Private Ctor for CSR.    *    * @param subject - Subject    * @param scmID - SCM ID    * @param clusterID - Cluster ID    * @param keyPair - KeyPair    * @param config - SCM Config    * @param extensions - CSR extensions    */
DECL|method|CertificateSignRequest (String subject, String scmID, String clusterID, KeyPair keyPair, SecurityConfig config, Extensions extensions)
specifier|private
name|CertificateSignRequest
parameter_list|(
name|String
name|subject
parameter_list|,
name|String
name|scmID
parameter_list|,
name|String
name|clusterID
parameter_list|,
name|KeyPair
name|keyPair
parameter_list|,
name|SecurityConfig
name|config
parameter_list|,
name|Extensions
name|extensions
parameter_list|)
block|{
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
name|this
operator|.
name|clusterID
operator|=
name|clusterID
expr_stmt|;
name|this
operator|.
name|scmID
operator|=
name|scmID
expr_stmt|;
name|this
operator|.
name|keyPair
operator|=
name|keyPair
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|extensions
operator|=
name|extensions
expr_stmt|;
block|}
DECL|method|generateCSR ()
specifier|private
name|PKCS10CertificationRequest
name|generateCSR
parameter_list|()
throws|throws
name|OperatorCreationException
block|{
name|X500Name
name|dnName
init|=
name|SecurityUtil
operator|.
name|getDistinguishedName
argument_list|(
name|subject
argument_list|,
name|scmID
argument_list|,
name|clusterID
argument_list|)
decl_stmt|;
name|PKCS10CertificationRequestBuilder
name|p10Builder
init|=
operator|new
name|JcaPKCS10CertificationRequestBuilder
argument_list|(
name|dnName
argument_list|,
name|keyPair
operator|.
name|getPublic
argument_list|()
argument_list|)
decl_stmt|;
name|ContentSigner
name|contentSigner
init|=
operator|new
name|JcaContentSignerBuilder
argument_list|(
name|config
operator|.
name|getSignatureAlgo
argument_list|()
argument_list|)
operator|.
name|setProvider
argument_list|(
name|config
operator|.
name|getProvider
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|keyPair
operator|.
name|getPrivate
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|extensions
operator|!=
literal|null
condition|)
block|{
name|p10Builder
operator|.
name|addAttribute
argument_list|(
name|PKCSObjectIdentifiers
operator|.
name|pkcs_9_at_extensionRequest
argument_list|,
name|extensions
argument_list|)
expr_stmt|;
block|}
return|return
name|p10Builder
operator|.
name|build
argument_list|(
name|contentSigner
argument_list|)
return|;
block|}
DECL|method|getEncodedString (PKCS10CertificationRequest request)
specifier|public
specifier|static
name|String
name|getEncodedString
parameter_list|(
name|PKCS10CertificationRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|PemObject
name|pemObject
init|=
operator|new
name|PemObject
argument_list|(
literal|"CERTIFICATE REQUEST"
argument_list|,
name|request
operator|.
name|getEncoded
argument_list|()
argument_list|)
decl_stmt|;
name|StringWriter
name|str
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
try|try
init|(
name|JcaPEMWriter
name|pemWriter
init|=
operator|new
name|JcaPEMWriter
argument_list|(
name|str
argument_list|)
init|)
block|{
name|pemWriter
operator|.
name|writeObject
argument_list|(
name|pemObject
argument_list|)
expr_stmt|;
block|}
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Gets a CertificateRequest Object from PEM encoded CSR.    *    * @param csr - PEM Encoded Certificate Request String.    * @return PKCS10CertificationRequest    * @throws IOException - On Error.    */
DECL|method|getCertificationRequest (String csr)
specifier|public
specifier|static
name|PKCS10CertificationRequest
name|getCertificationRequest
parameter_list|(
name|String
name|csr
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|PemReader
name|reader
init|=
operator|new
name|PemReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|csr
argument_list|)
argument_list|)
init|)
block|{
name|PemObject
name|pemObject
init|=
name|reader
operator|.
name|readPemObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|pemObject
operator|.
name|getContent
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SCMSecurityException
argument_list|(
literal|"Invalid Certificate signing request"
argument_list|)
throw|;
block|}
return|return
operator|new
name|PKCS10CertificationRequest
argument_list|(
name|pemObject
operator|.
name|getContent
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Builder class for Certificate Sign Request.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|subject
specifier|private
name|String
name|subject
decl_stmt|;
DECL|field|clusterID
specifier|private
name|String
name|clusterID
decl_stmt|;
DECL|field|scmID
specifier|private
name|String
name|scmID
decl_stmt|;
DECL|field|key
specifier|private
name|KeyPair
name|key
decl_stmt|;
DECL|field|config
specifier|private
name|SecurityConfig
name|config
decl_stmt|;
DECL|field|altNames
specifier|private
name|List
argument_list|<
name|GeneralName
argument_list|>
name|altNames
decl_stmt|;
DECL|field|ca
specifier|private
name|Boolean
name|ca
init|=
literal|false
decl_stmt|;
DECL|field|digitalSignature
specifier|private
name|boolean
name|digitalSignature
decl_stmt|;
DECL|field|digitalEncryption
specifier|private
name|boolean
name|digitalEncryption
decl_stmt|;
DECL|method|setConfiguration ( Configuration configuration)
specifier|public
name|CertificateSignRequest
operator|.
name|Builder
name|setConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
operator|new
name|SecurityConfig
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setKey (KeyPair keyPair)
specifier|public
name|CertificateSignRequest
operator|.
name|Builder
name|setKey
parameter_list|(
name|KeyPair
name|keyPair
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|keyPair
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSubject (String subjectString)
specifier|public
name|CertificateSignRequest
operator|.
name|Builder
name|setSubject
parameter_list|(
name|String
name|subjectString
parameter_list|)
block|{
name|this
operator|.
name|subject
operator|=
name|subjectString
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setClusterID (String s)
specifier|public
name|CertificateSignRequest
operator|.
name|Builder
name|setClusterID
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|clusterID
operator|=
name|s
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setScmID (String s)
specifier|public
name|CertificateSignRequest
operator|.
name|Builder
name|setScmID
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|scmID
operator|=
name|s
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDigitalSignature (boolean dSign)
specifier|public
name|Builder
name|setDigitalSignature
parameter_list|(
name|boolean
name|dSign
parameter_list|)
block|{
name|this
operator|.
name|digitalSignature
operator|=
name|dSign
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDigitalEncryption (boolean dEncryption)
specifier|public
name|Builder
name|setDigitalEncryption
parameter_list|(
name|boolean
name|dEncryption
parameter_list|)
block|{
name|this
operator|.
name|digitalEncryption
operator|=
name|dEncryption
expr_stmt|;
return|return
name|this
return|;
block|}
comment|// Support SAN extenion with DNS and RFC822 Name
comment|// other name type will be added as needed.
DECL|method|addDnsName (String dnsName)
specifier|public
name|CertificateSignRequest
operator|.
name|Builder
name|addDnsName
parameter_list|(
name|String
name|dnsName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dnsName
argument_list|,
literal|"dnsName cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|addAltName
argument_list|(
name|GeneralName
operator|.
name|dNSName
argument_list|,
name|dnsName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|// IP address is subject to change which is optional for now.
DECL|method|addIpAddress (String ip)
specifier|public
name|CertificateSignRequest
operator|.
name|Builder
name|addIpAddress
parameter_list|(
name|String
name|ip
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ip
argument_list|,
literal|"Ip address cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|addAltName
argument_list|(
name|GeneralName
operator|.
name|iPAddress
argument_list|,
name|ip
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addAltName (int tag, String name)
specifier|private
name|CertificateSignRequest
operator|.
name|Builder
name|addAltName
parameter_list|(
name|int
name|tag
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|altNames
operator|==
literal|null
condition|)
block|{
name|altNames
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|altNames
operator|.
name|add
argument_list|(
operator|new
name|GeneralName
argument_list|(
name|tag
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCA (Boolean isCA)
specifier|public
name|CertificateSignRequest
operator|.
name|Builder
name|setCA
parameter_list|(
name|Boolean
name|isCA
parameter_list|)
block|{
name|this
operator|.
name|ca
operator|=
name|isCA
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getKeyUsageExtension ()
specifier|private
name|Extension
name|getKeyUsageExtension
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|keyUsageFlag
init|=
name|KeyUsage
operator|.
name|keyAgreement
decl_stmt|;
if|if
condition|(
name|digitalEncryption
condition|)
block|{
name|keyUsageFlag
operator||=
name|KeyUsage
operator|.
name|keyEncipherment
operator||
name|KeyUsage
operator|.
name|dataEncipherment
expr_stmt|;
block|}
if|if
condition|(
name|digitalSignature
condition|)
block|{
name|keyUsageFlag
operator||=
name|KeyUsage
operator|.
name|digitalSignature
expr_stmt|;
block|}
if|if
condition|(
name|ca
condition|)
block|{
name|keyUsageFlag
operator||=
name|KeyUsage
operator|.
name|keyCertSign
operator||
name|KeyUsage
operator|.
name|cRLSign
expr_stmt|;
block|}
name|KeyUsage
name|keyUsage
init|=
operator|new
name|KeyUsage
argument_list|(
name|keyUsageFlag
argument_list|)
decl_stmt|;
return|return
operator|new
name|Extension
argument_list|(
name|Extension
operator|.
name|keyUsage
argument_list|,
literal|true
argument_list|,
operator|new
name|DEROctetString
argument_list|(
name|keyUsage
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getSubjectAltNameExtension ()
specifier|private
name|Optional
argument_list|<
name|Extension
argument_list|>
name|getSubjectAltNameExtension
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|altNames
operator|!=
literal|null
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|Extension
argument_list|(
name|Extension
operator|.
name|subjectAlternativeName
argument_list|,
literal|false
argument_list|,
operator|new
name|DEROctetString
argument_list|(
operator|new
name|GeneralNames
argument_list|(
name|altNames
operator|.
name|toArray
argument_list|(
operator|new
name|GeneralName
index|[
name|altNames
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
DECL|method|getBasicExtension ()
specifier|private
name|Extension
name|getBasicExtension
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We don't set pathLenConstraint means no limit is imposed.
return|return
operator|new
name|Extension
argument_list|(
name|Extension
operator|.
name|basicConstraints
argument_list|,
literal|true
argument_list|,
operator|new
name|DEROctetString
argument_list|(
operator|new
name|BasicConstraints
argument_list|(
name|ca
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createExtensions ()
specifier|private
name|Extensions
name|createExtensions
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Extension
argument_list|>
name|extensions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Add basic extension
if|if
condition|(
name|ca
condition|)
block|{
name|extensions
operator|.
name|add
argument_list|(
name|getBasicExtension
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Add key usage extension
name|extensions
operator|.
name|add
argument_list|(
name|getKeyUsageExtension
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add subject alternate name extension
name|Optional
argument_list|<
name|Extension
argument_list|>
name|san
init|=
name|getSubjectAltNameExtension
argument_list|()
decl_stmt|;
if|if
condition|(
name|san
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|extensions
operator|.
name|add
argument_list|(
name|san
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Extensions
argument_list|(
name|extensions
operator|.
name|toArray
argument_list|(
operator|new
name|Extension
index|[
name|extensions
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|build ()
specifier|public
name|PKCS10CertificationRequest
name|build
parameter_list|()
throws|throws
name|SCMSecurityException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|key
argument_list|,
literal|"KeyPair cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|Strings
operator|.
name|isNotBlank
argument_list|(
name|subject
argument_list|)
argument_list|,
literal|"Subject "
operator|+
literal|"cannot be blank"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|Strings
operator|.
name|isNotBlank
argument_list|(
name|clusterID
argument_list|)
argument_list|,
literal|"Cluster ID "
operator|+
literal|"cannot be blank"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|Strings
operator|.
name|isNotBlank
argument_list|(
name|scmID
argument_list|)
argument_list|,
literal|"SCM ID cannot "
operator|+
literal|"be blank"
argument_list|)
expr_stmt|;
try|try
block|{
name|CertificateSignRequest
name|csr
init|=
operator|new
name|CertificateSignRequest
argument_list|(
name|subject
argument_list|,
name|scmID
argument_list|,
name|clusterID
argument_list|,
name|key
argument_list|,
name|config
argument_list|,
name|createExtensions
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|csr
operator|.
name|generateCSR
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|CertificateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unable to create "
operator|+
literal|"extension for certificate sign request for %s."
argument_list|,
name|SecurityUtil
operator|.
name|getDistinguishedName
argument_list|(
name|subject
argument_list|,
name|scmID
argument_list|,
name|clusterID
argument_list|)
argument_list|)
argument_list|,
name|ioe
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|OperatorCreationException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|CertificateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unable to create "
operator|+
literal|"certificate sign request for %s."
argument_list|,
name|SecurityUtil
operator|.
name|getDistinguishedName
argument_list|(
name|subject
argument_list|,
name|scmID
argument_list|,
name|clusterID
argument_list|)
argument_list|)
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

