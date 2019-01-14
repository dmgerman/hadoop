begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
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
name|net
operator|.
name|URI
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|security
operator|.
name|ProviderUtils
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
name|alias
operator|.
name|CredentialProvider
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
name|alias
operator|.
name|CredentialProviderFactory
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|*
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
name|fs
operator|.
name|s3a
operator|.
name|S3AEncryptionMethods
operator|.
name|*
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
name|fs
operator|.
name|s3a
operator|.
name|S3AUtils
operator|.
name|*
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test SSE setup operations and errors raised.  * Tests related to secret providers and AWS credentials are also  * included, as they share some common setup operations.  */
end_comment

begin_class
DECL|class|TestSSEConfiguration
specifier|public
class|class
name|TestSSEConfiguration
extends|extends
name|Assert
block|{
comment|/** Bucket to use for per-bucket options. */
DECL|field|BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|BUCKET
init|=
literal|"dataset-1"
decl_stmt|;
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
name|S3ATestConstants
operator|.
name|S3A_TEST_TIMEOUT
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|tempDir
specifier|public
specifier|final
name|TemporaryFolder
name|tempDir
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testSSECNoKey ()
specifier|public
name|void
name|testSSECNoKey
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertGetAlgorithmFails
argument_list|(
name|SSE_C_NO_KEY_ERROR
argument_list|,
name|SSE_C
operator|.
name|getMethod
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSSECBlankKey ()
specifier|public
name|void
name|testSSECBlankKey
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertGetAlgorithmFails
argument_list|(
name|SSE_C_NO_KEY_ERROR
argument_list|,
name|SSE_C
operator|.
name|getMethod
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSSECGoodKey ()
specifier|public
name|void
name|testSSECGoodKey
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertEquals
argument_list|(
name|SSE_C
argument_list|,
name|getAlgorithm
argument_list|(
name|SSE_C
argument_list|,
literal|"sseckey"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKMSGoodKey ()
specifier|public
name|void
name|testKMSGoodKey
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertEquals
argument_list|(
name|SSE_KMS
argument_list|,
name|getAlgorithm
argument_list|(
name|SSE_KMS
argument_list|,
literal|"kmskey"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAESKeySet ()
specifier|public
name|void
name|testAESKeySet
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertGetAlgorithmFails
argument_list|(
name|SSE_S3_WITH_KEY_ERROR
argument_list|,
name|SSE_S3
operator|.
name|getMethod
argument_list|()
argument_list|,
literal|"setkey"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSSEEmptyKey ()
specifier|public
name|void
name|testSSEEmptyKey
parameter_list|()
block|{
comment|// test the internal logic of the test setup code
name|Configuration
name|c
init|=
name|buildConf
argument_list|(
name|SSE_C
operator|.
name|getMethod
argument_list|()
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|getServerSideEncryptionKey
argument_list|(
name|BUCKET
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSSEKeyNull ()
specifier|public
name|void
name|testSSEKeyNull
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// test the internal logic of the test setup code
specifier|final
name|Configuration
name|c
init|=
name|buildConf
argument_list|(
name|SSE_C
operator|.
name|getMethod
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|getServerSideEncryptionKey
argument_list|(
name|BUCKET
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|SSE_C_NO_KEY_ERROR
argument_list|,
parameter_list|()
lambda|->
name|getEncryptionAlgorithm
argument_list|(
name|BUCKET
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSSEKeyFromCredentialProvider ()
specifier|public
name|void
name|testSSEKeyFromCredentialProvider
parameter_list|()
throws|throws
name|Exception
block|{
comment|// set up conf to have a cred provider
specifier|final
name|Configuration
name|conf
init|=
name|confWithProvider
argument_list|()
decl_stmt|;
name|String
name|key
init|=
literal|"provisioned"
decl_stmt|;
name|setProviderOption
argument_list|(
name|conf
argument_list|,
name|SERVER_SIDE_ENCRYPTION_KEY
argument_list|,
name|key
argument_list|)
expr_stmt|;
comment|// let's set the password in config and ensure that it uses the credential
comment|// provider provisioned value instead.
name|conf
operator|.
name|set
argument_list|(
name|SERVER_SIDE_ENCRYPTION_KEY
argument_list|,
literal|"keyInConfObject"
argument_list|)
expr_stmt|;
name|String
name|sseKey
init|=
name|getServerSideEncryptionKey
argument_list|(
name|BUCKET
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Proxy password should not retrun null."
argument_list|,
name|sseKey
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Proxy password override did NOT work."
argument_list|,
name|key
argument_list|,
name|sseKey
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a temp file provider to the config.    * @param conf config    * @throws Exception failure    */
DECL|method|addFileProvider (Configuration conf)
specifier|private
name|void
name|addFileProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|File
name|file
init|=
name|tempDir
operator|.
name|newFile
argument_list|(
literal|"test.jks"
argument_list|)
decl_stmt|;
specifier|final
name|URI
name|jks
init|=
name|ProviderUtils
operator|.
name|nestURIForLocalJavaKeyStoreProvider
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CredentialProviderFactory
operator|.
name|CREDENTIAL_PROVIDER_PATH
argument_list|,
name|jks
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the an option under the configuration via the    * {@link CredentialProviderFactory} APIs.    * @param conf config    * @param option option name    * @param value value to set option to.    * @throws Exception failure    */
DECL|method|setProviderOption (final Configuration conf, String option, String value)
name|void
name|setProviderOption
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
name|String
name|option
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|Exception
block|{
comment|// add our password to the provider
specifier|final
name|CredentialProvider
name|provider
init|=
name|CredentialProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|option
argument_list|,
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Assert that the exception text from {@link #getAlgorithm(String, String)}    * is as expected.    * @param expected expected substring in error    * @param alg algorithm to ask for    * @param key optional key value    * @throws Exception anything else which gets raised    */
DECL|method|assertGetAlgorithmFails (String expected, final String alg, final String key)
specifier|public
name|void
name|assertGetAlgorithmFails
parameter_list|(
name|String
name|expected
parameter_list|,
specifier|final
name|String
name|alg
parameter_list|,
specifier|final
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|expected
argument_list|,
parameter_list|()
lambda|->
name|getAlgorithm
argument_list|(
name|alg
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getAlgorithm (S3AEncryptionMethods algorithm, String key)
specifier|private
name|S3AEncryptionMethods
name|getAlgorithm
parameter_list|(
name|S3AEncryptionMethods
name|algorithm
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getAlgorithm
argument_list|(
name|algorithm
operator|.
name|getMethod
argument_list|()
argument_list|,
name|key
argument_list|)
return|;
block|}
DECL|method|getAlgorithm (String algorithm, String key)
specifier|private
name|S3AEncryptionMethods
name|getAlgorithm
parameter_list|(
name|String
name|algorithm
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getEncryptionAlgorithm
argument_list|(
name|BUCKET
argument_list|,
name|buildConf
argument_list|(
name|algorithm
argument_list|,
name|key
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Build a new configuration with the given S3-SSE algorithm    * and key.    * @param algorithm  algorithm to use, may be null    * @param key key, may be null    * @return the new config.    */
DECL|method|buildConf (String algorithm, String key)
specifier|private
name|Configuration
name|buildConf
parameter_list|(
name|String
name|algorithm
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|Configuration
name|conf
init|=
name|emptyConf
argument_list|()
decl_stmt|;
if|if
condition|(
name|algorithm
operator|!=
literal|null
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|SERVER_SIDE_ENCRYPTION_ALGORITHM
argument_list|,
name|algorithm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|unset
argument_list|(
name|SERVER_SIDE_ENCRYPTION_ALGORITHM
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|SERVER_SIDE_ENCRYPTION_KEY
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|unset
argument_list|(
name|SERVER_SIDE_ENCRYPTION_KEY
argument_list|)
expr_stmt|;
block|}
return|return
name|conf
return|;
block|}
comment|/**    * Create an empty conf: no -default or -site values.    * @return an empty configuration    */
DECL|method|emptyConf ()
specifier|private
name|Configuration
name|emptyConf
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**    * Create a configuration with no defaults and bonded to a file    * provider, so that    * {@link #setProviderOption(Configuration, String, String)}    * can be used to set a secret.    * @return the configuration    * @throws Exception any failure    */
DECL|method|confWithProvider ()
specifier|private
name|Configuration
name|confWithProvider
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
name|emptyConf
argument_list|()
decl_stmt|;
name|addFileProvider
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|field|SECRET
specifier|private
specifier|static
specifier|final
name|String
name|SECRET
init|=
literal|"*secret*"
decl_stmt|;
DECL|field|BUCKET_PATTERN
specifier|private
specifier|static
specifier|final
name|String
name|BUCKET_PATTERN
init|=
name|FS_S3A_BUCKET_PREFIX
operator|+
literal|"%s.%s"
decl_stmt|;
annotation|@
name|Test
DECL|method|testGetPasswordFromConf ()
specifier|public
name|void
name|testGetPasswordFromConf
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Configuration
name|conf
init|=
name|emptyConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SECRET_KEY
argument_list|,
name|SECRET
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SECRET
argument_list|,
name|lookupPassword
argument_list|(
name|conf
argument_list|,
name|SECRET_KEY
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SECRET
argument_list|,
name|lookupPassword
argument_list|(
name|conf
argument_list|,
name|SECRET_KEY
argument_list|,
literal|"defVal"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetPasswordFromProvider ()
specifier|public
name|void
name|testGetPasswordFromProvider
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Configuration
name|conf
init|=
name|confWithProvider
argument_list|()
decl_stmt|;
name|setProviderOption
argument_list|(
name|conf
argument_list|,
name|SECRET_KEY
argument_list|,
name|SECRET
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SECRET
argument_list|,
name|lookupPassword
argument_list|(
name|conf
argument_list|,
name|SECRET_KEY
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertSecretKeyEquals
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
name|SECRET
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertSecretKeyEquals
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|"overidden"
argument_list|,
literal|"overidden"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetBucketPasswordFromProvider ()
specifier|public
name|void
name|testGetBucketPasswordFromProvider
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Configuration
name|conf
init|=
name|confWithProvider
argument_list|()
decl_stmt|;
name|URI
name|bucketURI
init|=
operator|new
name|URI
argument_list|(
literal|"s3a://"
operator|+
name|BUCKET
operator|+
literal|"/"
argument_list|)
decl_stmt|;
name|setProviderOption
argument_list|(
name|conf
argument_list|,
name|SECRET_KEY
argument_list|,
literal|"unbucketed"
argument_list|)
expr_stmt|;
name|String
name|bucketedKey
init|=
name|String
operator|.
name|format
argument_list|(
name|BUCKET_PATTERN
argument_list|,
name|BUCKET
argument_list|,
name|SECRET_KEY
argument_list|)
decl_stmt|;
name|setProviderOption
argument_list|(
name|conf
argument_list|,
name|bucketedKey
argument_list|,
name|SECRET
argument_list|)
expr_stmt|;
name|String
name|overrideVal
decl_stmt|;
name|overrideVal
operator|=
literal|""
expr_stmt|;
name|assertSecretKeyEquals
argument_list|(
name|conf
argument_list|,
name|BUCKET
argument_list|,
name|SECRET
argument_list|,
name|overrideVal
argument_list|)
expr_stmt|;
name|assertSecretKeyEquals
argument_list|(
name|conf
argument_list|,
name|bucketURI
operator|.
name|getHost
argument_list|()
argument_list|,
name|SECRET
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertSecretKeyEquals
argument_list|(
name|conf
argument_list|,
name|bucketURI
operator|.
name|getHost
argument_list|()
argument_list|,
literal|"overidden"
argument_list|,
literal|"overidden"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a secret key is as expected.    * @param conf configuration to examine    * @param bucket bucket name    * @param expected expected value    * @param overrideVal override value in {@code S3AUtils.lookupPassword()}    * @throws IOException IO problem    */
DECL|method|assertSecretKeyEquals (Configuration conf, String bucket, String expected, String overrideVal)
specifier|private
name|void
name|assertSecretKeyEquals
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
name|overrideVal
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|S3AUtils
operator|.
name|lookupPassword
argument_list|(
name|bucket
argument_list|,
name|conf
argument_list|,
name|SECRET_KEY
argument_list|,
name|overrideVal
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetBucketPasswordFromProviderShort ()
specifier|public
name|void
name|testGetBucketPasswordFromProviderShort
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Configuration
name|conf
init|=
name|confWithProvider
argument_list|()
decl_stmt|;
name|URI
name|bucketURI
init|=
operator|new
name|URI
argument_list|(
literal|"s3a://"
operator|+
name|BUCKET
operator|+
literal|"/"
argument_list|)
decl_stmt|;
name|setProviderOption
argument_list|(
name|conf
argument_list|,
name|SECRET_KEY
argument_list|,
literal|"unbucketed"
argument_list|)
expr_stmt|;
name|String
name|bucketedKey
init|=
name|String
operator|.
name|format
argument_list|(
name|BUCKET_PATTERN
argument_list|,
name|BUCKET
argument_list|,
literal|"secret.key"
argument_list|)
decl_stmt|;
name|setProviderOption
argument_list|(
name|conf
argument_list|,
name|bucketedKey
argument_list|,
name|SECRET
argument_list|)
expr_stmt|;
name|assertSecretKeyEquals
argument_list|(
name|conf
argument_list|,
name|BUCKET
argument_list|,
name|SECRET
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertSecretKeyEquals
argument_list|(
name|conf
argument_list|,
name|bucketURI
operator|.
name|getHost
argument_list|()
argument_list|,
name|SECRET
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertSecretKeyEquals
argument_list|(
name|conf
argument_list|,
name|bucketURI
operator|.
name|getHost
argument_list|()
argument_list|,
literal|"overidden"
argument_list|,
literal|"overidden"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnknownEncryptionMethod ()
specifier|public
name|void
name|testUnknownEncryptionMethod
parameter_list|()
throws|throws
name|Throwable
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|UNKNOWN_ALGORITHM
argument_list|,
parameter_list|()
lambda|->
name|S3AEncryptionMethods
operator|.
name|getMethod
argument_list|(
literal|"SSE-ROT13"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClientEncryptionMethod ()
specifier|public
name|void
name|testClientEncryptionMethod
parameter_list|()
throws|throws
name|Throwable
block|{
name|S3AEncryptionMethods
name|method
init|=
name|getMethod
argument_list|(
literal|"CSE-KMS"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CSE_KMS
argument_list|,
name|method
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"shouldn't be server side "
operator|+
name|method
argument_list|,
name|method
operator|.
name|isServerSide
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCSEKMSEncryptionMethod ()
specifier|public
name|void
name|testCSEKMSEncryptionMethod
parameter_list|()
throws|throws
name|Throwable
block|{
name|S3AEncryptionMethods
name|method
init|=
name|getMethod
argument_list|(
literal|"CSE-CUSTOM"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CSE_CUSTOM
argument_list|,
name|method
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"shouldn't be server side "
operator|+
name|method
argument_list|,
name|method
operator|.
name|isServerSide
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoEncryptionMethod ()
specifier|public
name|void
name|testNoEncryptionMethod
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertEquals
argument_list|(
name|NONE
argument_list|,
name|getMethod
argument_list|(
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

