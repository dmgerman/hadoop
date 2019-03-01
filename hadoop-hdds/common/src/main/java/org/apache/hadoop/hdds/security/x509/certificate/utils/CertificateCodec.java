begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.certificate.utils
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|PosixFilePermission
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
name|CertificateFactory
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|PosixFilePermission
operator|.
name|OWNER_EXECUTE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|PosixFilePermission
operator|.
name|OWNER_READ
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|PosixFilePermission
operator|.
name|OWNER_WRITE
import|;
end_import

begin_comment
comment|/**  * A class used to read and write X.509 certificates  PEM encoded Streams.  */
end_comment

begin_class
DECL|class|CertificateCodec
specifier|public
class|class
name|CertificateCodec
block|{
DECL|field|BEGIN_CERT
specifier|public
specifier|static
specifier|final
name|String
name|BEGIN_CERT
init|=
literal|"-----BEGIN CERTIFICATE-----"
decl_stmt|;
DECL|field|END_CERT
specifier|public
specifier|static
specifier|final
name|String
name|END_CERT
init|=
literal|"-----END CERTIFICATE-----"
decl_stmt|;
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
name|CertificateCodec
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CERTIFICATE_CONVERTER
specifier|private
specifier|static
specifier|final
name|JcaX509CertificateConverter
name|CERTIFICATE_CONVERTER
init|=
operator|new
name|JcaX509CertificateConverter
argument_list|()
decl_stmt|;
DECL|field|securityConfig
specifier|private
specifier|final
name|SecurityConfig
name|securityConfig
decl_stmt|;
DECL|field|location
specifier|private
specifier|final
name|Path
name|location
decl_stmt|;
DECL|field|permissionSet
specifier|private
name|Set
argument_list|<
name|PosixFilePermission
argument_list|>
name|permissionSet
init|=
name|Stream
operator|.
name|of
argument_list|(
name|OWNER_READ
argument_list|,
name|OWNER_WRITE
argument_list|,
name|OWNER_EXECUTE
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Creates an CertificateCodec.    *    * @param config - Security Config.    * @param component - Component String.    */
DECL|method|CertificateCodec (SecurityConfig config, String component)
specifier|public
name|CertificateCodec
parameter_list|(
name|SecurityConfig
name|config
parameter_list|,
name|String
name|component
parameter_list|)
block|{
name|this
operator|.
name|securityConfig
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|securityConfig
operator|.
name|getCertificateLocation
argument_list|(
name|component
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an CertificateCodec.    *    * @param config - Security Config.    */
DECL|method|CertificateCodec (SecurityConfig config)
specifier|public
name|CertificateCodec
parameter_list|(
name|SecurityConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|securityConfig
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|securityConfig
operator|.
name|getCertificateLocation
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates an CertificateCodec.    *    * @param configuration - Configuration    */
DECL|method|CertificateCodec (Configuration configuration)
specifier|public
name|CertificateCodec
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|configuration
argument_list|,
literal|"Config cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|securityConfig
operator|=
operator|new
name|SecurityConfig
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|securityConfig
operator|.
name|getCertificateLocation
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a X509 Certificate from the Certificate Holder.    *    * @param holder - Holder    * @return X509Certificate.    * @throws CertificateException - on Error.    */
DECL|method|getX509Certificate (X509CertificateHolder holder)
specifier|public
specifier|static
name|X509Certificate
name|getX509Certificate
parameter_list|(
name|X509CertificateHolder
name|holder
parameter_list|)
throws|throws
name|CertificateException
block|{
return|return
name|CERTIFICATE_CONVERTER
operator|.
name|getCertificate
argument_list|(
name|holder
argument_list|)
return|;
block|}
comment|/**    * Returns the Certificate as a PEM encoded String.    *    * @param x509CertHolder - X.509 Certificate Holder.    * @return PEM Encoded Certificate String.    * @throws SCMSecurityException - On failure to create a PEM String.    */
DECL|method|getPEMEncodedString (X509CertificateHolder x509CertHolder)
specifier|public
specifier|static
name|String
name|getPEMEncodedString
parameter_list|(
name|X509CertificateHolder
name|x509CertHolder
parameter_list|)
throws|throws
name|SCMSecurityException
block|{
try|try
block|{
return|return
name|getPEMEncodedString
argument_list|(
name|getX509Certificate
argument_list|(
name|x509CertHolder
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CertificateException
name|exp
parameter_list|)
block|{
throw|throw
operator|new
name|SCMSecurityException
argument_list|(
name|exp
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the Certificate as a PEM encoded String.    *    * @param certificate - X.509 Certificate.    * @return PEM Encoded Certificate String.    * @throws SCMSecurityException - On failure to create a PEM String.    */
DECL|method|getPEMEncodedString (X509Certificate certificate)
specifier|public
specifier|static
name|String
name|getPEMEncodedString
parameter_list|(
name|X509Certificate
name|certificate
parameter_list|)
throws|throws
name|SCMSecurityException
block|{
try|try
block|{
name|StringWriter
name|stringWriter
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
name|stringWriter
argument_list|)
init|)
block|{
name|pemWriter
operator|.
name|writeObject
argument_list|(
name|certificate
argument_list|)
expr_stmt|;
block|}
return|return
name|stringWriter
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in encoding certificate."
operator|+
name|certificate
operator|.
name|getSubjectDN
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMSecurityException
argument_list|(
literal|"PEM Encoding failed for certificate."
operator|+
name|certificate
operator|.
name|getSubjectDN
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Gets the X.509 Certificate from PEM encoded String.    *    * @param pemEncodedString - PEM encoded String.    * @return X509Certificate  - Certificate.    * @throws CertificateException - Thrown on Failure.    * @throws IOException          - Thrown on Failure.    */
DECL|method|getX509Certificate (String pemEncodedString)
specifier|public
specifier|static
name|X509Certificate
name|getX509Certificate
parameter_list|(
name|String
name|pemEncodedString
parameter_list|)
throws|throws
name|CertificateException
throws|,
name|IOException
block|{
name|CertificateFactory
name|fact
init|=
name|CertificateFactory
operator|.
name|getInstance
argument_list|(
literal|"X.509"
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|input
init|=
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|pemEncodedString
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
return|return
operator|(
name|X509Certificate
operator|)
name|fact
operator|.
name|generateCertificate
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
comment|/**    * Get Certificate location.    *    * @return Path    */
DECL|method|getLocation ()
specifier|public
name|Path
name|getLocation
parameter_list|()
block|{
return|return
name|location
return|;
block|}
comment|/**    * Gets the X.509 Certificate from PEM encoded String.    *    * @param pemEncodedString - PEM encoded String.    * @return X509Certificate  - Certificate.    * @throws CertificateException - Thrown on Failure.    * @throws IOException          - Thrown on Failure.    */
DECL|method|getX509Cert (String pemEncodedString)
specifier|public
specifier|static
name|X509Certificate
name|getX509Cert
parameter_list|(
name|String
name|pemEncodedString
parameter_list|)
throws|throws
name|CertificateException
throws|,
name|IOException
block|{
name|CertificateFactory
name|fact
init|=
name|CertificateFactory
operator|.
name|getInstance
argument_list|(
literal|"X.509"
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|input
init|=
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|pemEncodedString
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
return|return
operator|(
name|X509Certificate
operator|)
name|fact
operator|.
name|generateCertificate
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
comment|/**    * Write the Certificate pointed to the location by the configs.    *    * @param xCertificate - Certificate to write.    * @throws SCMSecurityException - on Error.    * @throws IOException - on Error.    */
DECL|method|writeCertificate (X509CertificateHolder xCertificate)
specifier|public
name|void
name|writeCertificate
parameter_list|(
name|X509CertificateHolder
name|xCertificate
parameter_list|)
throws|throws
name|SCMSecurityException
throws|,
name|IOException
block|{
name|String
name|pem
init|=
name|getPEMEncodedString
argument_list|(
name|xCertificate
argument_list|)
decl_stmt|;
name|writeCertificate
argument_list|(
name|location
operator|.
name|toAbsolutePath
argument_list|()
argument_list|,
name|this
operator|.
name|securityConfig
operator|.
name|getCertificateFileName
argument_list|()
argument_list|,
name|pem
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write the Certificate to the specific file.    *    * @param xCertificate - Certificate to write.    * @param fileName - file name to write to.    * @param overwrite - boolean value, true means overwrite an existing    * certificate.    * @throws SCMSecurityException - On Error.    * @throws IOException          - On Error.    */
DECL|method|writeCertificate (X509CertificateHolder xCertificate, String fileName, boolean overwrite)
specifier|public
name|void
name|writeCertificate
parameter_list|(
name|X509CertificateHolder
name|xCertificate
parameter_list|,
name|String
name|fileName
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|SCMSecurityException
throws|,
name|IOException
block|{
name|String
name|pem
init|=
name|getPEMEncodedString
argument_list|(
name|xCertificate
argument_list|)
decl_stmt|;
name|writeCertificate
argument_list|(
name|location
operator|.
name|toAbsolutePath
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|pem
argument_list|,
name|overwrite
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper function that writes data to the file.    *    * @param basePath - Base Path where the file needs to written to.    * @param fileName - Certificate file name.    * @param pemEncodedCertificate - pemEncoded Certificate file.    * @param force - Overwrite if the file exists.    * @throws IOException - on Error.    */
DECL|method|writeCertificate (Path basePath, String fileName, String pemEncodedCertificate, boolean force)
specifier|public
specifier|synchronized
name|void
name|writeCertificate
parameter_list|(
name|Path
name|basePath
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|pemEncodedCertificate
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|certificateFile
init|=
name|Paths
operator|.
name|get
argument_list|(
name|basePath
operator|.
name|toString
argument_list|()
argument_list|,
name|fileName
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|certificateFile
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|force
condition|)
block|{
throw|throw
operator|new
name|SCMSecurityException
argument_list|(
literal|"Specified certificate file already "
operator|+
literal|"exists.Please use force option if you want to overwrite it."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|basePath
operator|.
name|toFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|basePath
operator|.
name|toFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create file path. Path: {}"
argument_list|,
name|basePath
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Creation of the directories failed."
operator|+
name|basePath
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
try|try
init|(
name|FileOutputStream
name|file
init|=
operator|new
name|FileOutputStream
argument_list|(
name|certificateFile
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|write
argument_list|(
name|pemEncodedCertificate
argument_list|,
name|file
argument_list|,
name|UTF_8
argument_list|)
expr_stmt|;
block|}
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|certificateFile
operator|.
name|toPath
argument_list|()
argument_list|,
name|permissionSet
argument_list|)
expr_stmt|;
block|}
comment|/**    * Rertuns a default certificate using the default paths for this component.    *    * @return X509CertificateHolder.    * @throws SCMSecurityException - on Error.    * @throws CertificateException - on Error.    * @throws IOException          - on Error.    */
DECL|method|readCertificate ()
specifier|public
name|X509CertificateHolder
name|readCertificate
parameter_list|()
throws|throws
name|CertificateException
throws|,
name|IOException
block|{
return|return
name|readCertificate
argument_list|(
name|this
operator|.
name|location
operator|.
name|toAbsolutePath
argument_list|()
argument_list|,
name|this
operator|.
name|securityConfig
operator|.
name|getCertificateFileName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the certificate from the specific PEM encoded file.    *    * @param basePath - base path    * @param fileName - fileName    * @return X%09 Certificate    * @throws IOException          - on Error.    * @throws SCMSecurityException - on Error.    * @throws CertificateException - on Error.    */
DECL|method|readCertificate (Path basePath, String fileName)
specifier|public
specifier|synchronized
name|X509CertificateHolder
name|readCertificate
parameter_list|(
name|Path
name|basePath
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
throws|,
name|CertificateException
block|{
name|File
name|certificateFile
init|=
name|Paths
operator|.
name|get
argument_list|(
name|basePath
operator|.
name|toString
argument_list|()
argument_list|,
name|fileName
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
return|return
name|getX509CertificateHolder
argument_list|(
name|certificateFile
argument_list|)
return|;
block|}
comment|/**    * Helper function to read certificate.    *    * @param certificateFile - Full path to certificate file.    * @return X509CertificateHolder    * @throws IOException          - On Error.    * @throws CertificateException - On Error.    */
DECL|method|getX509CertificateHolder (File certificateFile)
specifier|private
name|X509CertificateHolder
name|getX509CertificateHolder
parameter_list|(
name|File
name|certificateFile
parameter_list|)
throws|throws
name|IOException
throws|,
name|CertificateException
block|{
if|if
condition|(
operator|!
name|certificateFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to find the requested certificate. Path: "
operator|+
name|certificateFile
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|CertificateFactory
name|fact
init|=
name|CertificateFactory
operator|.
name|getInstance
argument_list|(
literal|"X.509"
argument_list|)
decl_stmt|;
try|try
init|(
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|certificateFile
argument_list|)
init|)
block|{
return|return
name|getCertificateHolder
argument_list|(
operator|(
name|X509Certificate
operator|)
name|fact
operator|.
name|generateCertificate
argument_list|(
name|is
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns the Certificate holder from X509Ceritificate class.    *    * @param x509cert - Certificate class.    * @return X509CertificateHolder    * @throws CertificateEncodingException - on Error.    * @throws IOException                  - on Error.    */
DECL|method|getCertificateHolder (X509Certificate x509cert)
specifier|public
name|X509CertificateHolder
name|getCertificateHolder
parameter_list|(
name|X509Certificate
name|x509cert
parameter_list|)
throws|throws
name|CertificateEncodingException
throws|,
name|IOException
block|{
return|return
operator|new
name|X509CertificateHolder
argument_list|(
name|x509cert
operator|.
name|getEncoded
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

