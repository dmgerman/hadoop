begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|AccessDeniedException
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
name|OffsetDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|ClientConfiguration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|securitytoken
operator|.
name|AWSSecurityTokenService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|securitytoken
operator|.
name|AWSSecurityTokenServiceClientBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|securitytoken
operator|.
name|model
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|MarshalledCredentialBinding
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
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|MarshalledCredentials
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
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|STSClientFactory
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
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|delegation
operator|.
name|SessionTokenIdentifier
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
name|test
operator|.
name|LambdaTestUtils
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
name|util
operator|.
name|DurationInfo
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
name|contract
operator|.
name|ContractTestUtils
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
name|S3ATestUtils
operator|.
name|assumeSessionTestsEnabled
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
name|S3ATestUtils
operator|.
name|requestSessionCredentials
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
name|S3ATestUtils
operator|.
name|unsetHadoopCredentialProviders
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
name|auth
operator|.
name|MarshalledCredentialBinding
operator|.
name|fromSTSCredentials
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
name|auth
operator|.
name|MarshalledCredentialBinding
operator|.
name|toAWSCredentials
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
name|auth
operator|.
name|RoleTestUtils
operator|.
name|assertCredentialsEqual
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
name|auth
operator|.
name|delegation
operator|.
name|DelegationConstants
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
name|auth
operator|.
name|delegation
operator|.
name|SessionTokenBinding
operator|.
name|CREDENTIALS_CONVERTED_TO_DELEGATION_TOKEN
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
name|intercept
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_comment
comment|/**  * Tests use of temporary credentials (for example, AWS STS& S3).  *  * The property {@link Constants#ASSUMED_ROLE_STS_ENDPOINT} can be set to  * point this at different STS endpoints.  * This test will use the AWS credentials (if provided) for  * S3A tests to request temporary credentials, then attempt to use those  * credentials instead.  */
end_comment

begin_class
DECL|class|ITestS3ATemporaryCredentials
specifier|public
class|class
name|ITestS3ATemporaryCredentials
extends|extends
name|AbstractS3ATestBase
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
name|ITestS3ATemporaryCredentials
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEMPORARY_AWS_CREDENTIALS
specifier|private
specifier|static
specifier|final
name|String
name|TEMPORARY_AWS_CREDENTIALS
init|=
name|TemporaryAWSCredentialsProvider
operator|.
name|NAME
decl_stmt|;
DECL|field|TEST_FILE_SIZE
specifier|private
specifier|static
specifier|final
name|long
name|TEST_FILE_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|STS_LONDON
specifier|public
specifier|static
specifier|final
name|String
name|STS_LONDON
init|=
literal|"sts.eu-west-2.amazonaws.com"
decl_stmt|;
DECL|field|EU_IRELAND
specifier|public
specifier|static
specifier|final
name|String
name|EU_IRELAND
init|=
literal|"eu-west-1"
decl_stmt|;
DECL|field|credentials
specifier|private
name|AWSCredentialProviderList
name|credentials
decl_stmt|;
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|assumeSessionTestsEnabled
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|S3AUtils
operator|.
name|closeAutocloseables
argument_list|(
name|LOG
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DELEGATION_TOKEN_BINDING
argument_list|,
name|DELEGATION_TOKEN_SESSION_BINDING
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Test use of STS for requesting temporary credentials.    *    * The property test.sts.endpoint can be set to point this at different    * STS endpoints. This test will use the AWS credentials (if provided) for    * S3A tests to request temporary credentials, then attempt to use those    * credentials instead.    *    * @throws IOException failure    */
annotation|@
name|Test
DECL|method|testSTS ()
specifier|public
name|void
name|testSTS
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|getContract
argument_list|()
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|S3AFileSystem
name|testFS
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|credentials
operator|=
name|testFS
operator|.
name|shareCredentials
argument_list|(
literal|"testSTS"
argument_list|)
expr_stmt|;
name|String
name|bucket
init|=
name|testFS
operator|.
name|getBucket
argument_list|()
decl_stmt|;
name|AWSSecurityTokenServiceClientBuilder
name|builder
init|=
name|STSClientFactory
operator|.
name|builder
argument_list|(
name|conf
argument_list|,
name|bucket
argument_list|,
name|credentials
argument_list|,
name|getStsEndpoint
argument_list|(
name|conf
argument_list|)
argument_list|,
name|getStsRegion
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|STSClientFactory
operator|.
name|STSClient
name|clientConnection
init|=
name|STSClientFactory
operator|.
name|createClientConnection
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|Invoker
argument_list|(
operator|new
name|S3ARetryPolicy
argument_list|(
name|conf
argument_list|)
argument_list|,
name|Invoker
operator|.
name|LOG_EVENT
argument_list|)
argument_list|)
decl_stmt|;
name|Credentials
name|sessionCreds
init|=
name|clientConnection
operator|.
name|requestSessionCredentials
argument_list|(
name|TEST_SESSION_TOKEN_DURATION_SECONDS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
comment|// clone configuration so changes here do not affect the base FS.
name|Configuration
name|conf2
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|S3AUtils
operator|.
name|clearBucketOption
argument_list|(
name|conf2
argument_list|,
name|bucket
argument_list|,
name|AWS_CREDENTIALS_PROVIDER
argument_list|)
expr_stmt|;
name|S3AUtils
operator|.
name|clearBucketOption
argument_list|(
name|conf2
argument_list|,
name|bucket
argument_list|,
name|ACCESS_KEY
argument_list|)
expr_stmt|;
name|S3AUtils
operator|.
name|clearBucketOption
argument_list|(
name|conf2
argument_list|,
name|bucket
argument_list|,
name|SECRET_KEY
argument_list|)
expr_stmt|;
name|S3AUtils
operator|.
name|clearBucketOption
argument_list|(
name|conf2
argument_list|,
name|bucket
argument_list|,
name|SESSION_TOKEN
argument_list|)
expr_stmt|;
name|MarshalledCredentials
name|mc
init|=
name|fromSTSCredentials
argument_list|(
name|sessionCreds
argument_list|)
decl_stmt|;
name|updateConfigWithSessionCreds
argument_list|(
name|conf2
argument_list|,
name|mc
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|AWS_CREDENTIALS_PROVIDER
argument_list|,
name|TEMPORARY_AWS_CREDENTIALS
argument_list|)
expr_stmt|;
comment|// with valid credentials, we can set properties.
try|try
init|(
name|S3AFileSystem
name|fs
init|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf2
argument_list|)
init|)
block|{
name|createAndVerifyFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|(
literal|"testSTS"
argument_list|)
argument_list|,
name|TEST_FILE_SIZE
argument_list|)
expr_stmt|;
block|}
comment|// now create an invalid set of credentials by changing the session
comment|// token
name|conf2
operator|.
name|set
argument_list|(
name|SESSION_TOKEN
argument_list|,
literal|"invalid-"
operator|+
name|sessionCreds
operator|.
name|getSessionToken
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|S3AFileSystem
name|fs
init|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf2
argument_list|)
init|)
block|{
name|createAndVerifyFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|(
literal|"testSTSInvalidToken"
argument_list|)
argument_list|,
name|TEST_FILE_SIZE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an access exception, but file access to "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|" was allowed: "
operator|+
name|fs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AWSS3IOException
decl||
name|AWSBadRequestException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected Exception: {}"
argument_list|,
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Expected Exception: {}"
argument_list|,
name|ex
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getStsEndpoint (final Configuration conf)
specifier|protected
name|String
name|getStsEndpoint
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getTrimmed
argument_list|(
name|ASSUMED_ROLE_STS_ENDPOINT
argument_list|,
name|DEFAULT_ASSUMED_ROLE_STS_ENDPOINT
argument_list|)
return|;
block|}
DECL|method|getStsRegion (final Configuration conf)
specifier|protected
name|String
name|getStsRegion
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getTrimmed
argument_list|(
name|ASSUMED_ROLE_STS_ENDPOINT_REGION
argument_list|,
name|ASSUMED_ROLE_STS_ENDPOINT_REGION_DEFAULT
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testTemporaryCredentialValidation ()
specifier|public
name|void
name|testTemporaryCredentialValidation
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ACCESS_KEY
argument_list|,
literal|"accesskey"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SECRET_KEY
argument_list|,
literal|"secretkey"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SESSION_TOKEN
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|CredentialInitializationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|TemporaryAWSCredentialsProvider
argument_list|(
name|conf
argument_list|)
operator|.
name|getCredentials
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that session tokens are propagated, with the origin string    * declaring this.    */
annotation|@
name|Test
DECL|method|testSessionTokenPropagation ()
specifier|public
name|void
name|testSessionTokenPropagation
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|getContract
argument_list|()
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|MarshalledCredentials
name|sc
init|=
name|requestSessionCredentials
argument_list|(
name|conf
argument_list|,
name|getFileSystem
argument_list|()
operator|.
name|getBucket
argument_list|()
argument_list|)
decl_stmt|;
name|updateConfigWithSessionCreds
argument_list|(
name|conf
argument_list|,
name|sc
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AWS_CREDENTIALS_PROVIDER
argument_list|,
name|TEMPORARY_AWS_CREDENTIALS
argument_list|)
expr_stmt|;
try|try
init|(
name|S3AFileSystem
name|fs
init|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
init|)
block|{
name|createAndVerifyFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|(
literal|"testSTS"
argument_list|)
argument_list|,
name|TEST_FILE_SIZE
argument_list|)
expr_stmt|;
name|SessionTokenIdentifier
name|identifier
init|=
operator|(
name|SessionTokenIdentifier
operator|)
name|fs
operator|.
name|getDelegationToken
argument_list|(
literal|""
argument_list|)
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|String
name|ids
init|=
name|identifier
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"origin in "
operator|+
name|ids
argument_list|,
name|identifier
operator|.
name|getOrigin
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|CREDENTIALS_CONVERTED_TO_DELEGATION_TOKEN
argument_list|)
argument_list|)
expr_stmt|;
comment|// and validate the AWS bits to make sure everything has come across.
name|assertCredentialsEqual
argument_list|(
literal|"Reissued credentials in "
operator|+
name|ids
argument_list|,
name|sc
argument_list|,
name|identifier
operator|.
name|getMarshalledCredentials
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Examine the returned expiry time and validate it against expectations.    * Allows for some flexibility in local clock, but not much.    */
annotation|@
name|Test
DECL|method|testSessionTokenExpiry ()
specifier|public
name|void
name|testSessionTokenExpiry
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|getContract
argument_list|()
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|MarshalledCredentials
name|sc
init|=
name|requestSessionCredentials
argument_list|(
name|conf
argument_list|,
name|getFileSystem
argument_list|()
operator|.
name|getBucket
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|permittedExpiryOffset
init|=
literal|60
decl_stmt|;
name|OffsetDateTime
name|expirationTimestamp
init|=
name|sc
operator|.
name|getExpirationDateTime
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|OffsetDateTime
name|localTimestamp
init|=
name|OffsetDateTime
operator|.
name|now
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"local time of "
operator|+
name|localTimestamp
operator|+
literal|" is after expiry time of "
operator|+
name|expirationTimestamp
argument_list|,
name|localTimestamp
operator|.
name|isBefore
argument_list|(
name|expirationTimestamp
argument_list|)
argument_list|)
expr_stmt|;
comment|// what is the interval
name|Duration
name|actualDuration
init|=
name|Duration
operator|.
name|between
argument_list|(
name|localTimestamp
argument_list|,
name|expirationTimestamp
argument_list|)
decl_stmt|;
name|Duration
name|offset
init|=
name|actualDuration
operator|.
name|minus
argument_list|(
name|TEST_SESSION_TOKEN_DURATION
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"Duration of session "
operator|+
name|actualDuration
operator|+
literal|" out of expected range of with "
operator|+
name|offset
operator|+
literal|" this host's clock may be wrong."
argument_list|,
name|offset
operator|.
name|getSeconds
argument_list|()
argument_list|,
name|Matchers
operator|.
name|lessThanOrEqualTo
argument_list|(
name|permittedExpiryOffset
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|updateConfigWithSessionCreds (final Configuration conf, final MarshalledCredentials sc)
specifier|protected
name|void
name|updateConfigWithSessionCreds
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|MarshalledCredentials
name|sc
parameter_list|)
block|{
name|unsetHadoopCredentialProviders
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setSecretsInConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an invalid session token and verify that it is rejected.    */
annotation|@
name|Test
DECL|method|testInvalidSTSBinding ()
specifier|public
name|void
name|testInvalidSTSBinding
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|getContract
argument_list|()
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|MarshalledCredentials
name|sc
init|=
name|requestSessionCredentials
argument_list|(
name|conf
argument_list|,
name|getFileSystem
argument_list|()
operator|.
name|getBucket
argument_list|()
argument_list|)
decl_stmt|;
name|toAWSCredentials
argument_list|(
name|sc
argument_list|,
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
operator|.
name|AnyNonEmpty
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|updateConfigWithSessionCreds
argument_list|(
name|conf
argument_list|,
name|sc
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AWS_CREDENTIALS_PROVIDER
argument_list|,
name|TEMPORARY_AWS_CREDENTIALS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SESSION_TOKEN
argument_list|,
literal|"invalid-"
operator|+
name|sc
operator|.
name|getSessionToken
argument_list|()
argument_list|)
expr_stmt|;
name|S3AFileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// this may throw an exception, which is an acceptable outcome.
comment|// it must be in the try/catch clause.
name|fs
operator|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"testSTSInvalidToken"
argument_list|)
decl_stmt|;
name|createAndVerifyFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|TEST_FILE_SIZE
argument_list|)
expr_stmt|;
comment|// this is a failure path, so fail with a meaningful error
name|fail
argument_list|(
literal|"request to create a file should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AWSBadRequestException
name|expected
parameter_list|)
block|{
comment|// likely at two points in the operation, depending on
comment|// S3Guard state
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSessionCredentialsBadRegion ()
specifier|public
name|void
name|testSessionCredentialsBadRegion
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Create a session with a bad region and expect failure"
argument_list|)
expr_stmt|;
name|expectedSessionRequestFailure
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
name|DEFAULT_DELEGATION_TOKEN_ENDPOINT
argument_list|,
literal|"us-west-12"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSessionCredentialsWrongRegion ()
specifier|public
name|void
name|testSessionCredentialsWrongRegion
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Create a session with the wrong region and expect failure"
argument_list|)
expr_stmt|;
name|expectedSessionRequestFailure
argument_list|(
name|AccessDeniedException
operator|.
name|class
argument_list|,
name|STS_LONDON
argument_list|,
name|EU_IRELAND
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSessionCredentialsWrongCentralRegion ()
specifier|public
name|void
name|testSessionCredentialsWrongCentralRegion
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Create a session sts.amazonaws.com; region='us-west-1'"
argument_list|)
expr_stmt|;
name|expectedSessionRequestFailure
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|"sts.amazonaws.com"
argument_list|,
literal|"us-west-1"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSessionCredentialsRegionNoEndpoint ()
specifier|public
name|void
name|testSessionCredentialsRegionNoEndpoint
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Create a session with a bad region and expect fast failure"
argument_list|)
expr_stmt|;
name|expectedSessionRequestFailure
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|""
argument_list|,
name|EU_IRELAND
argument_list|,
name|EU_IRELAND
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSessionCredentialsRegionBadEndpoint ()
specifier|public
name|void
name|testSessionCredentialsRegionBadEndpoint
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Create a session with a bad region and expect fast failure"
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|ex
init|=
name|expectedSessionRequestFailure
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|" "
argument_list|,
name|EU_IRELAND
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Outcome: "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|ex
operator|.
name|getCause
argument_list|()
operator|instanceof
name|URISyntaxException
operator|)
condition|)
block|{
throw|throw
name|ex
throw|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSessionCredentialsEndpointNoRegion ()
specifier|public
name|void
name|testSessionCredentialsEndpointNoRegion
parameter_list|()
throws|throws
name|Throwable
block|{
name|expectedSessionRequestFailure
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
name|STS_LONDON
argument_list|,
literal|""
argument_list|,
name|STS_LONDON
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expect an attempt to create a session or request credentials to fail    * with a specific exception class, optionally text.    * @param clazz exact class of exception.    * @param endpoint value for the sts endpoint option.    * @param region signing region.    * @param exceptionText text or "" in the exception.    * @param<E> type of exception.    * @return the caught exception.    * @throws Exception any unexpected exception.    */
DECL|method|expectedSessionRequestFailure ( final Class<E> clazz, final String endpoint, final String region, final String exceptionText)
specifier|public
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|E
name|expectedSessionRequestFailure
parameter_list|(
specifier|final
name|Class
argument_list|<
name|E
argument_list|>
name|clazz
parameter_list|,
specifier|final
name|String
name|endpoint
parameter_list|,
specifier|final
name|String
name|region
parameter_list|,
specifier|final
name|String
name|exceptionText
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|AWSCredentialProviderList
name|parentCreds
init|=
name|getFileSystem
argument_list|()
operator|.
name|shareCredentials
argument_list|(
literal|"test"
argument_list|)
init|;
name|DurationInfo
name|ignored
operator|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|"requesting credentials"
argument_list|)
init|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|getContract
argument_list|()
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|ClientConfiguration
name|awsConf
init|=
name|S3AUtils
operator|.
name|createAwsConf
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
name|AWS_SERVICE_IDENTIFIER_STS
argument_list|)
decl_stmt|;
return|return
name|intercept
argument_list|(
name|clazz
argument_list|,
name|exceptionText
argument_list|,
parameter_list|()
lambda|->
block|{
name|AWSSecurityTokenService
name|tokenService
init|=
name|STSClientFactory
operator|.
name|builder
argument_list|(
name|parentCreds
argument_list|,
name|awsConf
argument_list|,
name|endpoint
argument_list|,
name|region
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Invoker
name|invoker
init|=
operator|new
name|Invoker
argument_list|(
operator|new
name|S3ARetryPolicy
argument_list|(
name|conf
argument_list|)
argument_list|,
name|LOG_AT_ERROR
argument_list|)
decl_stmt|;
name|STSClientFactory
operator|.
name|STSClient
name|stsClient
init|=
name|STSClientFactory
operator|.
name|createClientConnection
argument_list|(
name|tokenService
argument_list|,
name|invoker
argument_list|)
decl_stmt|;
return|return
name|stsClient
operator|.
name|requestSessionCredentials
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
return|;
block|}
argument_list|)
return|;
block|}
block|}
comment|/**    * Log retries at debug.    */
DECL|field|LOG_AT_ERROR
specifier|public
specifier|static
specifier|final
name|Invoker
operator|.
name|Retried
name|LOG_AT_ERROR
init|=
parameter_list|(
name|text
parameter_list|,
name|exception
parameter_list|,
name|retries
parameter_list|,
name|idempotent
parameter_list|)
lambda|->
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"{}"
argument_list|,
name|text
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testTemporaryCredentialValidationOnLoad ()
specifier|public
name|void
name|testTemporaryCredentialValidationOnLoad
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|unsetHadoopCredentialProviders
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ACCESS_KEY
argument_list|,
literal|"aaa"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SECRET_KEY
argument_list|,
literal|"bbb"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SESSION_TOKEN
argument_list|,
literal|""
argument_list|)
expr_stmt|;
specifier|final
name|MarshalledCredentials
name|sc
init|=
name|MarshalledCredentialBinding
operator|.
name|fromFileSystem
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|MarshalledCredentials
operator|.
name|INVALID_CREDENTIALS
argument_list|,
parameter_list|()
lambda|->
block|{
name|sc
operator|.
name|validate
argument_list|(
literal|""
argument_list|,
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
operator|.
name|SessionOnly
argument_list|)
expr_stmt|;
return|return
name|sc
operator|.
name|toString
argument_list|()
return|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyTemporaryCredentialValidation ()
specifier|public
name|void
name|testEmptyTemporaryCredentialValidation
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|unsetHadoopCredentialProviders
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ACCESS_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SECRET_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SESSION_TOKEN
argument_list|,
literal|""
argument_list|)
expr_stmt|;
specifier|final
name|MarshalledCredentials
name|sc
init|=
name|MarshalledCredentialBinding
operator|.
name|fromFileSystem
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|MarshalledCredentialBinding
operator|.
name|NO_AWS_CREDENTIALS
argument_list|,
parameter_list|()
lambda|->
block|{
name|sc
operator|.
name|validate
argument_list|(
literal|""
argument_list|,
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
operator|.
name|SessionOnly
argument_list|)
expr_stmt|;
return|return
name|sc
operator|.
name|toString
argument_list|()
return|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the request mechanism is translating exceptions.    * @throws Exception on a failure    */
annotation|@
name|Test
DECL|method|testSessionRequestExceptionTranslation ()
specifier|public
name|void
name|testSessionRequestExceptionTranslation
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|requestSessionCredentials
argument_list|(
name|getConfiguration
argument_list|()
argument_list|,
name|getFileSystem
argument_list|()
operator|.
name|getBucket
argument_list|()
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

