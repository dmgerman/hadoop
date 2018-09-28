begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509
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
name|Provider
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
name|time
operator|.
name|Duration
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
name|HDDS_DEFAULT_KEY_ALGORITHM
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
name|HDDS_DEFAULT_KEY_LEN
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
name|HDDS_DEFAULT_SECURITY_PROVIDER
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
name|HDDS_KEY_ALGORITHM
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
name|HDDS_KEY_DIR_NAME
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
name|HDDS_KEY_DIR_NAME_DEFAULT
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
name|HDDS_KEY_LEN
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
name|HddsConfigKeys
operator|.
name|HDDS_PRIVATE_KEY_FILE_NAME
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
name|HDDS_PRIVATE_KEY_FILE_NAME_DEFAULT
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
name|HDDS_PUBLIC_KEY_FILE_NAME
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
name|HDDS_PUBLIC_KEY_FILE_NAME_DEFAULT
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
name|HDDS_SECURITY_PROVIDER
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
name|HDDS_X509_MAX_DURATION
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
name|HDDS_X509_MAX_DURATION_DEFAULT
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
name|HDDS_X509_SIGNATURE_ALGO
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
name|HDDS_X509_SIGNATURE_ALGO_DEFAULT
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
import|;
end_import

begin_comment
comment|/**  * A class that deals with all Security related configs in HDDS.  *  * This class allows security configs to be read and used consistently across  * all of security related code base.  */
end_comment

begin_class
DECL|class|SecurityConfig
specifier|public
class|class
name|SecurityConfig
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
name|SecurityConfig
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|provider
specifier|private
specifier|static
specifier|volatile
name|Provider
name|provider
decl_stmt|;
DECL|field|configuration
specifier|private
specifier|final
name|Configuration
name|configuration
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|keyAlgo
specifier|private
specifier|final
name|String
name|keyAlgo
decl_stmt|;
DECL|field|providerString
specifier|private
specifier|final
name|String
name|providerString
decl_stmt|;
DECL|field|metadatDir
specifier|private
specifier|final
name|String
name|metadatDir
decl_stmt|;
DECL|field|keyDir
specifier|private
specifier|final
name|String
name|keyDir
decl_stmt|;
DECL|field|privateKeyFileName
specifier|private
specifier|final
name|String
name|privateKeyFileName
decl_stmt|;
DECL|field|publicKeyFileName
specifier|private
specifier|final
name|String
name|publicKeyFileName
decl_stmt|;
DECL|field|certDuration
specifier|private
specifier|final
name|Duration
name|certDuration
decl_stmt|;
DECL|field|x509SignatureAlgo
specifier|private
specifier|final
name|String
name|x509SignatureAlgo
decl_stmt|;
comment|/**    * Constructs a SecurityConfig.    *    * @param configuration - HDDS Configuration    */
DECL|method|SecurityConfig (Configuration configuration)
specifier|public
name|SecurityConfig
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
literal|"Configuration cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|this
operator|.
name|configuration
operator|.
name|getInt
argument_list|(
name|HDDS_KEY_LEN
argument_list|,
name|HDDS_DEFAULT_KEY_LEN
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyAlgo
operator|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|HDDS_KEY_ALGORITHM
argument_list|,
name|HDDS_DEFAULT_KEY_ALGORITHM
argument_list|)
expr_stmt|;
name|this
operator|.
name|providerString
operator|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|HDDS_SECURITY_PROVIDER
argument_list|,
name|HDDS_DEFAULT_SECURITY_PROVIDER
argument_list|)
expr_stmt|;
comment|// Please Note: To make it easy for our customers we will attempt to read
comment|// HDDS metadata dir and if that is not set, we will use Ozone directory.
comment|// TODO: We might want to fix this later.
name|this
operator|.
name|metadatDir
operator|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|HDDS_METADATA_DIR_NAME
argument_list|,
name|configuration
operator|.
name|get
argument_list|(
name|OZONE_METADATA_DIRS
argument_list|)
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|this
operator|.
name|metadatDir
argument_list|,
literal|"Metadata directory can't be"
operator|+
literal|" null. Please check configs."
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyDir
operator|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|HDDS_KEY_DIR_NAME
argument_list|,
name|HDDS_KEY_DIR_NAME_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|privateKeyFileName
operator|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|HDDS_PRIVATE_KEY_FILE_NAME
argument_list|,
name|HDDS_PRIVATE_KEY_FILE_NAME_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|publicKeyFileName
operator|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|HDDS_PUBLIC_KEY_FILE_NAME
argument_list|,
name|HDDS_PUBLIC_KEY_FILE_NAME_DEFAULT
argument_list|)
expr_stmt|;
name|String
name|durationString
init|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|HDDS_X509_MAX_DURATION
argument_list|,
name|HDDS_X509_MAX_DURATION_DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|certDuration
operator|=
name|Duration
operator|.
name|parse
argument_list|(
name|durationString
argument_list|)
expr_stmt|;
name|this
operator|.
name|x509SignatureAlgo
operator|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|HDDS_X509_SIGNATURE_ALGO
argument_list|,
name|HDDS_X509_SIGNATURE_ALGO_DEFAULT
argument_list|)
expr_stmt|;
comment|// First Startup -- if the provider is null, check for the provider.
if|if
condition|(
name|SecurityConfig
operator|.
name|provider
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|SecurityConfig
operator|.
name|class
init|)
block|{
name|provider
operator|=
name|Security
operator|.
name|getProvider
argument_list|(
name|this
operator|.
name|providerString
argument_list|)
expr_stmt|;
if|if
condition|(
name|SecurityConfig
operator|.
name|provider
operator|==
literal|null
condition|)
block|{
comment|// Provider not found, let us try to Dynamically initialize the
comment|// provider.
name|provider
operator|=
name|initSecurityProvider
argument_list|(
name|this
operator|.
name|providerString
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Returns the public key file name, This is used for storing the public    * keys on disk.    *    * @return String, File name used for public keys.    */
DECL|method|getPublicKeyFileName ()
specifier|public
name|String
name|getPublicKeyFileName
parameter_list|()
block|{
return|return
name|publicKeyFileName
return|;
block|}
comment|/**    * Returns the private key file name.This is used for storing the private    * keys on disk.    *    * @return String, File name used for private keys.    */
DECL|method|getPrivateKeyFileName ()
specifier|public
name|String
name|getPrivateKeyFileName
parameter_list|()
block|{
return|return
name|privateKeyFileName
return|;
block|}
comment|/**    * Returns the File path to where keys are stored.    *    * @return String Key location.    */
DECL|method|getKeyLocation ()
specifier|public
name|Path
name|getKeyLocation
parameter_list|()
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
name|metadatDir
argument_list|,
name|keyDir
argument_list|)
return|;
block|}
comment|/**    * Gets the Key Size, The default key size is 2048, since the default    * algorithm used is RSA. User can change this by setting the "hdds.key    * .len" in configuration.    *    * @return key size.    */
DECL|method|getSize ()
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**    * Returns the Provider name. SCM defaults to using Bouncy Castle and will    * return "BC".    *    * @return String Provider name.    */
DECL|method|getProvider ()
specifier|public
name|String
name|getProvider
parameter_list|()
block|{
return|return
name|providerString
return|;
block|}
comment|/**    * Returns the Key generation Algorithm used.  User can change this by    * setting the "hdds.key.algo" in configuration.    *    * @return String Algo.    */
DECL|method|getKeyAlgo ()
specifier|public
name|String
name|getKeyAlgo
parameter_list|()
block|{
return|return
name|keyAlgo
return|;
block|}
comment|/**    * Returns the X.509 Signature Algorithm used. This can be changed by setting    * "hdds.x509.signature.algorithm" to the new name. The default algorithm    * is SHA256withRSA.    *    * @return String    */
DECL|method|getSignatureAlgo ()
specifier|public
name|String
name|getSignatureAlgo
parameter_list|()
block|{
return|return
name|x509SignatureAlgo
return|;
block|}
comment|/**    * Returns the Configuration used for initializing this SecurityConfig.    *    * @return Configuration    */
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
comment|/**    * Returns the maximum length a certificate can be valid in SCM. The    * default value is 5 years. This can be changed by setting    * "hdds.x509.max.duration" in configuration. The formats accepted are    * based on the ISO-8601 duration format PnDTnHnMn.nS    *    * Default value is 5 years and written as P1865D.    *    * @return Duration.    */
DECL|method|getMaxCertificateDuration ()
specifier|public
name|Duration
name|getMaxCertificateDuration
parameter_list|()
block|{
return|return
name|this
operator|.
name|certDuration
return|;
block|}
comment|/**    * Adds a security provider dynamically if it is not loaded already.    *    * @param providerName - name of the provider.    */
DECL|method|initSecurityProvider (String providerName)
specifier|private
name|Provider
name|initSecurityProvider
parameter_list|(
name|String
name|providerName
parameter_list|)
block|{
switch|switch
condition|(
name|providerName
condition|)
block|{
case|case
literal|"BC"
case|:
name|Security
operator|.
name|addProvider
argument_list|(
operator|new
name|BouncyCastleProvider
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Security
operator|.
name|getProvider
argument_list|(
name|providerName
argument_list|)
return|;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Security Provider:{} is unknown"
argument_list|,
name|provider
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"Unknown security provider:"
operator|+
name|provider
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

