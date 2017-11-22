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
name|URI
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|AWSCredentialsProvider
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
name|AWSSecurityTokenServiceClient
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
name|GetSessionTokenRequest
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
name|GetSessionTokenResult
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3native
operator|.
name|S3xLoginHelper
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

begin_comment
comment|/**  * Tests use of temporary credentials (for example, AWS STS& S3).  * This test extends a class that "does things to the root directory", and  * should only be used against transient filesystems where you don't care about  * the data.  */
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
DECL|field|PROVIDER_CLASS
specifier|private
specifier|static
specifier|final
name|String
name|PROVIDER_CLASS
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
comment|/**    * Test use of STS for requesting temporary credentials.    *    * The property test.sts.endpoint can be set to point this at different    * STS endpoints. This test will use the AWS credentials (if provided) for    * S3A tests to request temporary credentials, then attempt to use those    * credentials instead.    *    * @throws IOException    */
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
if|if
condition|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|TEST_STS_ENABLED
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|skip
argument_list|(
literal|"STS functional tests disabled"
argument_list|)
expr_stmt|;
block|}
name|S3xLoginHelper
operator|.
name|Login
name|login
init|=
name|S3AUtils
operator|.
name|getAWSAccessKeys
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"s3a://foobar"
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|login
operator|.
name|hasLogin
argument_list|()
condition|)
block|{
name|skip
argument_list|(
literal|"testSTS disabled because AWS credentials not configured"
argument_list|)
expr_stmt|;
block|}
name|AWSCredentialsProvider
name|parentCredentials
init|=
operator|new
name|BasicAWSCredentialsProvider
argument_list|(
name|login
operator|.
name|getUser
argument_list|()
argument_list|,
name|login
operator|.
name|getPassword
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|stsEndpoint
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|TEST_STS_ENDPOINT
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|AWSSecurityTokenServiceClient
name|stsClient
decl_stmt|;
name|stsClient
operator|=
operator|new
name|AWSSecurityTokenServiceClient
argument_list|(
name|parentCredentials
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|stsEndpoint
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"STS Endpoint ={}"
argument_list|,
name|stsEndpoint
argument_list|)
expr_stmt|;
name|stsClient
operator|.
name|setEndpoint
argument_list|(
name|stsEndpoint
argument_list|)
expr_stmt|;
block|}
name|GetSessionTokenRequest
name|sessionTokenRequest
init|=
operator|new
name|GetSessionTokenRequest
argument_list|()
decl_stmt|;
name|sessionTokenRequest
operator|.
name|setDurationSeconds
argument_list|(
literal|900
argument_list|)
expr_stmt|;
name|GetSessionTokenResult
name|sessionTokenResult
decl_stmt|;
name|sessionTokenResult
operator|=
name|stsClient
operator|.
name|getSessionToken
argument_list|(
name|sessionTokenRequest
argument_list|)
expr_stmt|;
name|Credentials
name|sessionCreds
init|=
name|sessionTokenResult
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|String
name|childAccessKey
init|=
name|sessionCreds
operator|.
name|getAccessKeyId
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ACCESS_KEY
argument_list|,
name|childAccessKey
argument_list|)
expr_stmt|;
name|String
name|childSecretKey
init|=
name|sessionCreds
operator|.
name|getSecretAccessKey
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SECRET_KEY
argument_list|,
name|childSecretKey
argument_list|)
expr_stmt|;
name|String
name|sessionToken
init|=
name|sessionCreds
operator|.
name|getSessionToken
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SESSION_TOKEN
argument_list|,
name|sessionToken
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AWS_CREDENTIALS_PROVIDER
argument_list|,
name|PROVIDER_CLASS
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
block|}
comment|// now create an invalid set of credentials by changing the session
comment|// token
name|conf
operator|.
name|set
argument_list|(
name|SESSION_TOKEN
argument_list|,
literal|"invalid-"
operator|+
name|sessionToken
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
block|}
end_class

end_unit

