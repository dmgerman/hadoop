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
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|test
operator|.
name|LambdaTestUtils
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

begin_comment
comment|/**  * Test class for HDDS pem writer.  */
end_comment

begin_class
DECL|class|TestKeyCodec
specifier|public
class|class
name|TestKeyCodec
block|{
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
DECL|field|configuration
specifier|private
name|OzoneConfiguration
name|configuration
decl_stmt|;
DECL|field|securityConfig
specifier|private
name|SecurityConfig
name|securityConfig
decl_stmt|;
DECL|field|component
specifier|private
name|String
name|component
decl_stmt|;
DECL|field|keyGenerator
specifier|private
name|HDDSKeyGenerator
name|keyGenerator
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
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
name|configuration
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|prefix
operator|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|HDDS_METADATA_DIR_NAME
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
name|keyGenerator
operator|=
operator|new
name|HDDSKeyGenerator
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|securityConfig
operator|=
operator|new
name|SecurityConfig
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|component
operator|=
literal|"test_component"
expr_stmt|;
block|}
comment|/**    * Assert basic things like we are able to create a file, and the names are    * in expected format etc.    *    * @throws NoSuchProviderException - On Error, due to missing Java    * dependencies.    * @throws NoSuchAlgorithmException - On Error,  due to missing Java    * dependencies.    * @throws IOException - On I/O failure.    */
annotation|@
name|Test
DECL|method|testWriteKey ()
specifier|public
name|void
name|testWriteKey
parameter_list|()
throws|throws
name|NoSuchProviderException
throws|,
name|NoSuchAlgorithmException
throws|,
name|IOException
throws|,
name|InvalidKeySpecException
block|{
name|KeyPair
name|keys
init|=
name|keyGenerator
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|KeyCodec
name|pemWriter
init|=
operator|new
name|KeyCodec
argument_list|(
name|securityConfig
argument_list|,
name|component
argument_list|)
decl_stmt|;
name|pemWriter
operator|.
name|writeKey
argument_list|(
name|keys
argument_list|)
expr_stmt|;
comment|// Assert that locations have been created.
name|Path
name|keyLocation
init|=
name|pemWriter
operator|.
name|getSecurityConfig
argument_list|()
operator|.
name|getKeyLocation
argument_list|(
name|component
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|keyLocation
operator|.
name|toFile
argument_list|()
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Assert that locations are created in the locations that we specified
comment|// using the Config.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|keyLocation
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|privateKeyPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|keyLocation
operator|.
name|toString
argument_list|()
argument_list|,
name|pemWriter
operator|.
name|getSecurityConfig
argument_list|()
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|privateKeyPath
operator|.
name|toFile
argument_list|()
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|publicKeyPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|keyLocation
operator|.
name|toString
argument_list|()
argument_list|,
name|pemWriter
operator|.
name|getSecurityConfig
argument_list|()
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|publicKeyPath
operator|.
name|toFile
argument_list|()
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Read the private key and test if the expected String in the PEM file
comment|// format exists.
name|byte
index|[]
name|privateKey
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|privateKeyPath
argument_list|)
decl_stmt|;
name|String
name|privateKeydata
init|=
operator|new
name|String
argument_list|(
name|privateKey
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|privateKeydata
operator|.
name|contains
argument_list|(
literal|"PRIVATE KEY"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Read the public key and test if the expected String in the PEM file
comment|// format exists.
name|byte
index|[]
name|publicKey
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|publicKeyPath
argument_list|)
decl_stmt|;
name|String
name|publicKeydata
init|=
operator|new
name|String
argument_list|(
name|publicKey
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|publicKeydata
operator|.
name|contains
argument_list|(
literal|"PUBLIC KEY"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Let us decode the PEM file and parse it back into binary.
name|KeyFactory
name|kf
init|=
name|KeyFactory
operator|.
name|getInstance
argument_list|(
name|pemWriter
operator|.
name|getSecurityConfig
argument_list|()
operator|.
name|getKeyAlgo
argument_list|()
argument_list|)
decl_stmt|;
comment|// Replace the PEM Human readable guards.
name|privateKeydata
operator|=
name|privateKeydata
operator|.
name|replace
argument_list|(
literal|"-----BEGIN PRIVATE KEY-----\n"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|privateKeydata
operator|=
name|privateKeydata
operator|.
name|replace
argument_list|(
literal|"-----END PRIVATE KEY-----"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Decode the bas64 to binary format and then use an ASN.1 parser to
comment|// parse the binary format.
name|byte
index|[]
name|keyBytes
init|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|privateKeydata
argument_list|)
decl_stmt|;
name|PKCS8EncodedKeySpec
name|spec
init|=
operator|new
name|PKCS8EncodedKeySpec
argument_list|(
name|keyBytes
argument_list|)
decl_stmt|;
name|PrivateKey
name|privateKeyDecoded
init|=
name|kf
operator|.
name|generatePrivate
argument_list|(
name|spec
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Private Key should not be null"
argument_list|,
name|privateKeyDecoded
argument_list|)
expr_stmt|;
comment|// Let us decode the public key and veriy that we can parse it back into
comment|// binary.
name|publicKeydata
operator|=
name|publicKeydata
operator|.
name|replace
argument_list|(
literal|"-----BEGIN PUBLIC KEY-----\n"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|publicKeydata
operator|=
name|publicKeydata
operator|.
name|replace
argument_list|(
literal|"-----END PUBLIC KEY-----"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|keyBytes
operator|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|publicKeydata
argument_list|)
expr_stmt|;
name|X509EncodedKeySpec
name|pubKeyspec
init|=
operator|new
name|X509EncodedKeySpec
argument_list|(
name|keyBytes
argument_list|)
decl_stmt|;
name|PublicKey
name|publicKeyDecoded
init|=
name|kf
operator|.
name|generatePublic
argument_list|(
name|pubKeyspec
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Public Key should not be null"
argument_list|,
name|publicKeyDecoded
argument_list|)
expr_stmt|;
comment|// Now let us assert the permissions on the Directories and files are as
comment|// expected.
name|Set
argument_list|<
name|PosixFilePermission
argument_list|>
name|expectedSet
init|=
name|pemWriter
operator|.
name|getPermissionSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|PosixFilePermission
argument_list|>
name|currentSet
init|=
name|Files
operator|.
name|getPosixFilePermissions
argument_list|(
name|privateKeyPath
argument_list|)
decl_stmt|;
name|currentSet
operator|.
name|removeAll
argument_list|(
name|expectedSet
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|currentSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|currentSet
operator|=
name|Files
operator|.
name|getPosixFilePermissions
argument_list|(
name|publicKeyPath
argument_list|)
expr_stmt|;
name|currentSet
operator|.
name|removeAll
argument_list|(
name|expectedSet
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|currentSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|currentSet
operator|=
name|Files
operator|.
name|getPosixFilePermissions
argument_list|(
name|keyLocation
argument_list|)
expr_stmt|;
name|currentSet
operator|.
name|removeAll
argument_list|(
name|expectedSet
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|currentSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert key rewrite fails without force option.    *    * @throws IOException - on I/O failure.    */
annotation|@
name|Test
DECL|method|testReWriteKey ()
specifier|public
name|void
name|testReWriteKey
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyPair
name|kp
init|=
name|keyGenerator
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|KeyCodec
name|pemWriter
init|=
operator|new
name|KeyCodec
argument_list|(
name|securityConfig
argument_list|,
name|component
argument_list|)
decl_stmt|;
name|SecurityConfig
name|secConfig
init|=
name|pemWriter
operator|.
name|getSecurityConfig
argument_list|()
decl_stmt|;
name|pemWriter
operator|.
name|writeKey
argument_list|(
name|kp
argument_list|)
expr_stmt|;
comment|// Assert that rewriting of keys throws exception with valid messages.
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
literal|"Private Key file already exists."
argument_list|,
parameter_list|()
lambda|->
name|pemWriter
operator|.
name|writeKey
argument_list|(
name|kp
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|secConfig
operator|.
name|getKeyLocation
argument_list|(
name|component
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|secConfig
operator|.
name|getPrivateKeyFileName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
literal|"Public Key file already exists."
argument_list|,
parameter_list|()
lambda|->
name|pemWriter
operator|.
name|writeKey
argument_list|(
name|kp
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|secConfig
operator|.
name|getKeyLocation
argument_list|(
name|component
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|secConfig
operator|.
name|getPublicKeyFileName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should succeed now as both public and private key are deleted.
name|pemWriter
operator|.
name|writeKey
argument_list|(
name|kp
argument_list|)
expr_stmt|;
comment|// Should succeed with overwrite flag as true.
name|pemWriter
operator|.
name|writeKey
argument_list|(
name|kp
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert key rewrite fails in non Posix file system.    *    * @throws IOException - on I/O failure.    */
annotation|@
name|Test
DECL|method|testWriteKeyInNonPosixFS ()
specifier|public
name|void
name|testWriteKeyInNonPosixFS
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyPair
name|kp
init|=
name|keyGenerator
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|KeyCodec
name|pemWriter
init|=
operator|new
name|KeyCodec
argument_list|(
name|securityConfig
argument_list|,
name|component
argument_list|)
decl_stmt|;
name|pemWriter
operator|.
name|setIsPosixFileSystem
argument_list|(
parameter_list|()
lambda|->
literal|false
argument_list|)
expr_stmt|;
comment|// Assert key rewrite fails in non Posix file system.
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
literal|"Unsupported File System for pem file."
argument_list|,
parameter_list|()
lambda|->
name|pemWriter
operator|.
name|writeKey
argument_list|(
name|kp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadWritePublicKeywithoutArgs ()
specifier|public
name|void
name|testReadWritePublicKeywithoutArgs
parameter_list|()
throws|throws
name|NoSuchProviderException
throws|,
name|NoSuchAlgorithmException
throws|,
name|IOException
throws|,
name|InvalidKeySpecException
block|{
name|KeyPair
name|kp
init|=
name|keyGenerator
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|KeyCodec
name|keycodec
init|=
operator|new
name|KeyCodec
argument_list|(
name|securityConfig
argument_list|,
name|component
argument_list|)
decl_stmt|;
name|keycodec
operator|.
name|writeKey
argument_list|(
name|kp
argument_list|)
expr_stmt|;
name|PublicKey
name|pubKey
init|=
name|keycodec
operator|.
name|readPublicKey
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|pubKey
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

