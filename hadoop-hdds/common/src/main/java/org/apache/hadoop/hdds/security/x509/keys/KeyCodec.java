begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.keys
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
name|keys
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
name|FileUtils
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
name|output
operator|.
name|FileWriterWithEncoding
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
name|PemWriter
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|FileSystems
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
name|KeyFactory
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
name|spec
operator|.
name|InvalidKeySpecException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|spec
operator|.
name|PKCS8EncodedKeySpec
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|spec
operator|.
name|X509EncodedKeySpec
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
name|function
operator|.
name|Supplier
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
comment|/**  * We store all Key material in good old PEM files. This helps in avoiding  * dealing will persistent Java KeyStore issues. Also when debugging, general  * tools like OpenSSL can be used to read and decode these files.  */
end_comment

begin_class
DECL|class|KeyCodec
specifier|public
class|class
name|KeyCodec
block|{
DECL|field|PRIVATE_KEY
specifier|public
specifier|final
specifier|static
name|String
name|PRIVATE_KEY
init|=
literal|"PRIVATE KEY"
decl_stmt|;
DECL|field|PUBLIC_KEY
specifier|public
specifier|final
specifier|static
name|String
name|PUBLIC_KEY
init|=
literal|"PUBLIC KEY"
decl_stmt|;
DECL|field|DEFAULT_CHARSET
specifier|public
specifier|final
specifier|static
name|Charset
name|DEFAULT_CHARSET
init|=
name|StandardCharsets
operator|.
name|UTF_8
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KeyCodec
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|location
specifier|private
specifier|final
name|Path
name|location
decl_stmt|;
DECL|field|securityConfig
specifier|private
specifier|final
name|SecurityConfig
name|securityConfig
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
DECL|field|isPosixFileSystem
specifier|private
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|isPosixFileSystem
decl_stmt|;
comment|/**    * Creates an KeyCodec.    *    * @param config - Security Config.    * @param component - Component String.    */
DECL|method|KeyCodec (SecurityConfig config, String component)
specifier|public
name|KeyCodec
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
name|isPosixFileSystem
operator|=
name|KeyCodec
operator|::
name|isPosix
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|securityConfig
operator|.
name|getKeyLocation
argument_list|(
name|component
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an HDDS Key Writer.    *    * @param configuration - Configuration    */
DECL|method|KeyCodec (Configuration configuration)
specifier|public
name|KeyCodec
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
name|isPosixFileSystem
operator|=
name|KeyCodec
operator|::
name|isPosix
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|securityConfig
operator|.
name|getKeyLocation
argument_list|()
expr_stmt|;
block|}
comment|/**    * Checks if File System supports posix style security permissions.    *    * @return True if it supports posix.    */
DECL|method|isPosix ()
specifier|private
specifier|static
name|Boolean
name|isPosix
parameter_list|()
block|{
return|return
name|FileSystems
operator|.
name|getDefault
argument_list|()
operator|.
name|supportedFileAttributeViews
argument_list|()
operator|.
name|contains
argument_list|(
literal|"posix"
argument_list|)
return|;
block|}
comment|/**    * Returns the Permission set.    *    * @return Set    */
annotation|@
name|VisibleForTesting
DECL|method|getPermissionSet ()
specifier|public
name|Set
argument_list|<
name|PosixFilePermission
argument_list|>
name|getPermissionSet
parameter_list|()
block|{
return|return
name|permissionSet
return|;
block|}
comment|/**    * Returns the Security config used for this object.    *    * @return SecurityConfig    */
DECL|method|getSecurityConfig ()
specifier|public
name|SecurityConfig
name|getSecurityConfig
parameter_list|()
block|{
return|return
name|securityConfig
return|;
block|}
comment|/**    * This function is used only for testing.    *    * @param isPosixFileSystem - Sets a boolean function for mimicking files    * systems that are not posix.    */
annotation|@
name|VisibleForTesting
DECL|method|setIsPosixFileSystem (Supplier<Boolean> isPosixFileSystem)
specifier|public
name|void
name|setIsPosixFileSystem
parameter_list|(
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|isPosixFileSystem
parameter_list|)
block|{
name|this
operator|.
name|isPosixFileSystem
operator|=
name|isPosixFileSystem
expr_stmt|;
block|}
comment|/**    * Writes a given key using the default config options.    *    * @param keyPair - Key Pair to write to file.    * @throws IOException - On I/O failure.    */
DECL|method|writeKey (KeyPair keyPair)
specifier|public
name|void
name|writeKey
parameter_list|(
name|KeyPair
name|keyPair
parameter_list|)
throws|throws
name|IOException
block|{
name|writeKey
argument_list|(
name|location
argument_list|,
name|keyPair
argument_list|,
name|securityConfig
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes a given key using default config options.    *    * @param keyPair - Key pair to write    * @param overwrite - Overwrites the keys if they already exist.    * @throws IOException - On I/O failure.    */
DECL|method|writeKey (KeyPair keyPair, boolean overwrite)
specifier|public
name|void
name|writeKey
parameter_list|(
name|KeyPair
name|keyPair
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
block|{
name|writeKey
argument_list|(
name|location
argument_list|,
name|keyPair
argument_list|,
name|securityConfig
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|,
name|overwrite
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes a given key using default config options.    *    * @param basePath - The location to write to, override the config values.    * @param keyPair - Key pair to write    * @param overwrite - Overwrites the keys if they already exist.    * @throws IOException - On I/O failure.    */
DECL|method|writeKey (Path basePath, KeyPair keyPair, boolean overwrite)
specifier|public
name|void
name|writeKey
parameter_list|(
name|Path
name|basePath
parameter_list|,
name|KeyPair
name|keyPair
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
block|{
name|writeKey
argument_list|(
name|basePath
argument_list|,
name|keyPair
argument_list|,
name|securityConfig
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|,
name|overwrite
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads a Private Key from the PEM Encoded Store.    *    * @param basePath - Base Path, Directory where the Key is stored.    * @param keyFileName - File Name of the private key    * @return PrivateKey Object.    * @throws IOException - on Error.    */
DECL|method|readKey (Path basePath, String keyFileName)
specifier|private
name|PKCS8EncodedKeySpec
name|readKey
parameter_list|(
name|Path
name|basePath
parameter_list|,
name|String
name|keyFileName
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|fileName
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
name|keyFileName
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|String
name|keyData
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|fileName
argument_list|,
name|DEFAULT_CHARSET
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|pemContent
decl_stmt|;
try|try
init|(
name|PemReader
name|pemReader
init|=
operator|new
name|PemReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|keyData
argument_list|)
argument_list|)
init|)
block|{
name|PemObject
name|keyObject
init|=
name|pemReader
operator|.
name|readPemObject
argument_list|()
decl_stmt|;
name|pemContent
operator|=
name|keyObject
operator|.
name|getContent
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|PKCS8EncodedKeySpec
argument_list|(
name|pemContent
argument_list|)
return|;
block|}
comment|/**    * Returns a Private Key from a PEM encoded file.    *    * @param basePath - base path    * @param privateKeyFileName - private key file name.    * @return PrivateKey    * @throws InvalidKeySpecException  - on Error.    * @throws NoSuchAlgorithmException - on Error.    * @throws IOException              - on Error.    */
DECL|method|readPrivateKey (Path basePath, String privateKeyFileName)
specifier|public
name|PrivateKey
name|readPrivateKey
parameter_list|(
name|Path
name|basePath
parameter_list|,
name|String
name|privateKeyFileName
parameter_list|)
throws|throws
name|InvalidKeySpecException
throws|,
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
name|PKCS8EncodedKeySpec
name|encodedKeySpec
init|=
name|readKey
argument_list|(
name|basePath
argument_list|,
name|privateKeyFileName
argument_list|)
decl_stmt|;
specifier|final
name|KeyFactory
name|keyFactory
init|=
name|KeyFactory
operator|.
name|getInstance
argument_list|(
name|securityConfig
operator|.
name|getKeyAlgo
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|keyFactory
operator|.
name|generatePrivate
argument_list|(
name|encodedKeySpec
argument_list|)
return|;
block|}
comment|/**    * Read the Public Key using defaults.    * @return PublicKey.    * @throws InvalidKeySpecException - On Error.    * @throws NoSuchAlgorithmException - On Error.    * @throws IOException - On Error.    */
DECL|method|readPublicKey ()
specifier|public
name|PublicKey
name|readPublicKey
parameter_list|()
throws|throws
name|InvalidKeySpecException
throws|,
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
return|return
name|readPublicKey
argument_list|(
name|this
operator|.
name|location
operator|.
name|toAbsolutePath
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns a public key from a PEM encoded file.    *    * @param basePath - base path.    * @param publicKeyFileName - public key file name.    * @return PublicKey    * @throws NoSuchAlgorithmException - on Error.    * @throws InvalidKeySpecException  - on Error.    * @throws IOException              - on Error.    */
DECL|method|readPublicKey (Path basePath, String publicKeyFileName)
specifier|public
name|PublicKey
name|readPublicKey
parameter_list|(
name|Path
name|basePath
parameter_list|,
name|String
name|publicKeyFileName
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|InvalidKeySpecException
throws|,
name|IOException
block|{
name|PKCS8EncodedKeySpec
name|encodedKeySpec
init|=
name|readKey
argument_list|(
name|basePath
argument_list|,
name|publicKeyFileName
argument_list|)
decl_stmt|;
specifier|final
name|KeyFactory
name|keyFactory
init|=
name|KeyFactory
operator|.
name|getInstance
argument_list|(
name|securityConfig
operator|.
name|getKeyAlgo
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|keyFactory
operator|.
name|generatePublic
argument_list|(
operator|new
name|X509EncodedKeySpec
argument_list|(
name|encodedKeySpec
operator|.
name|getEncoded
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the private key  using defaults.    * @return PrivateKey.    * @throws InvalidKeySpecException - On Error.    * @throws NoSuchAlgorithmException - On Error.    * @throws IOException - On Error.    */
DECL|method|readPrivateKey ()
specifier|public
name|PrivateKey
name|readPrivateKey
parameter_list|()
throws|throws
name|InvalidKeySpecException
throws|,
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
return|return
name|readPrivateKey
argument_list|(
name|this
operator|.
name|location
operator|.
name|toAbsolutePath
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Helper function that actually writes data to the files.    *    * @param basePath - base path to write key    * @param keyPair - Key pair to write to file.    * @param privateKeyFileName - private key file name.    * @param publicKeyFileName - public key file name.    * @param force - forces overwriting the keys.    * @throws IOException - On I/O failure.    */
DECL|method|writeKey (Path basePath, KeyPair keyPair, String privateKeyFileName, String publicKeyFileName, boolean force)
specifier|private
specifier|synchronized
name|void
name|writeKey
parameter_list|(
name|Path
name|basePath
parameter_list|,
name|KeyPair
name|keyPair
parameter_list|,
name|String
name|privateKeyFileName
parameter_list|,
name|String
name|publicKeyFileName
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPreconditions
argument_list|(
name|basePath
argument_list|)
expr_stmt|;
name|File
name|privateKeyFile
init|=
name|Paths
operator|.
name|get
argument_list|(
name|location
operator|.
name|toString
argument_list|()
argument_list|,
name|privateKeyFileName
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|publicKeyFile
init|=
name|Paths
operator|.
name|get
argument_list|(
name|location
operator|.
name|toString
argument_list|()
argument_list|,
name|publicKeyFileName
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|checkKeyFile
argument_list|(
name|privateKeyFile
argument_list|,
name|force
argument_list|,
name|publicKeyFile
argument_list|)
expr_stmt|;
try|try
init|(
name|PemWriter
name|privateKeyWriter
init|=
operator|new
name|PemWriter
argument_list|(
operator|new
name|FileWriterWithEncoding
argument_list|(
name|privateKeyFile
argument_list|,
name|DEFAULT_CHARSET
argument_list|)
argument_list|)
init|)
block|{
name|privateKeyWriter
operator|.
name|writeObject
argument_list|(
operator|new
name|PemObject
argument_list|(
name|PRIVATE_KEY
argument_list|,
name|keyPair
operator|.
name|getPrivate
argument_list|()
operator|.
name|getEncoded
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|PemWriter
name|publicKeyWriter
init|=
operator|new
name|PemWriter
argument_list|(
operator|new
name|FileWriterWithEncoding
argument_list|(
name|publicKeyFile
argument_list|,
name|DEFAULT_CHARSET
argument_list|)
argument_list|)
init|)
block|{
name|publicKeyWriter
operator|.
name|writeObject
argument_list|(
operator|new
name|PemObject
argument_list|(
name|PUBLIC_KEY
argument_list|,
name|keyPair
operator|.
name|getPublic
argument_list|()
operator|.
name|getEncoded
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|privateKeyFile
operator|.
name|toPath
argument_list|()
argument_list|,
name|permissionSet
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|publicKeyFile
operator|.
name|toPath
argument_list|()
argument_list|,
name|permissionSet
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks if private and public key file already exists. Throws IOException if    * file exists and force flag is set to false, else will delete the existing    * file.    *    * @param privateKeyFile - Private key file.    * @param force - forces overwriting the keys.    * @param publicKeyFile - public key file.    * @throws IOException - On I/O failure.    */
DECL|method|checkKeyFile (File privateKeyFile, boolean force, File publicKeyFile)
specifier|private
name|void
name|checkKeyFile
parameter_list|(
name|File
name|privateKeyFile
parameter_list|,
name|boolean
name|force
parameter_list|,
name|File
name|publicKeyFile
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|privateKeyFile
operator|.
name|exists
argument_list|()
operator|&&
name|force
condition|)
block|{
if|if
condition|(
operator|!
name|privateKeyFile
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to delete private key file."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|publicKeyFile
operator|.
name|exists
argument_list|()
operator|&&
name|force
condition|)
block|{
if|if
condition|(
operator|!
name|publicKeyFile
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to delete public key file."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|privateKeyFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Private Key file already exists."
argument_list|)
throw|;
block|}
if|if
condition|(
name|publicKeyFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Public Key file already exists."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Checks if base path exists and sets file permissions.    *    * @param basePath - base path to write key    * @throws IOException - On I/O failure.    */
DECL|method|checkPreconditions (Path basePath)
specifier|private
name|void
name|checkPreconditions
parameter_list|(
name|Path
name|basePath
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|basePath
argument_list|,
literal|"Base path cannot be null"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isPosixFileSystem
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Keys cannot be stored securely without POSIX file system "
operator|+
literal|"support for now."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported File System for pem file."
argument_list|)
throw|;
block|}
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|basePath
argument_list|)
condition|)
block|{
comment|// Not the end of the world if we reset the permissions on an existing
comment|// directory.
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|basePath
argument_list|,
name|permissionSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|success
init|=
name|basePath
operator|.
name|toFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create the directory for the "
operator|+
literal|"location. Location: {}"
argument_list|,
name|basePath
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create the directory for the "
operator|+
literal|"location. Location:"
operator|+
name|basePath
argument_list|)
throw|;
block|}
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|basePath
argument_list|,
name|permissionSet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

