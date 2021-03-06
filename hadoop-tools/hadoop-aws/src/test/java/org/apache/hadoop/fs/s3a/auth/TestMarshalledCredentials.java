begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth
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
operator|.
name|auth
package|;
end_package

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
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|AWSCredentials
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
name|Test
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
name|S3AEncryptionMethods
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
name|S3ATestUtils
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
name|EncryptionSecrets
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
name|HadoopTestBase
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

begin_comment
comment|/**  * Unit test of marshalled credential support.  */
end_comment

begin_class
DECL|class|TestMarshalledCredentials
specifier|public
class|class
name|TestMarshalledCredentials
extends|extends
name|HadoopTestBase
block|{
DECL|field|credentials
specifier|private
name|MarshalledCredentials
name|credentials
decl_stmt|;
DECL|field|expiration
specifier|private
name|int
name|expiration
decl_stmt|;
DECL|field|bucketURI
specifier|private
name|URI
name|bucketURI
decl_stmt|;
annotation|@
name|Before
DECL|method|createSessionToken ()
specifier|public
name|void
name|createSessionToken
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|bucketURI
operator|=
operator|new
name|URI
argument_list|(
literal|"s3a://bucket1"
argument_list|)
expr_stmt|;
name|credentials
operator|=
operator|new
name|MarshalledCredentials
argument_list|(
literal|"accessKey"
argument_list|,
literal|"secretKey"
argument_list|,
literal|"sessionToken"
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|setRoleARN
argument_list|(
literal|"roleARN"
argument_list|)
expr_stmt|;
name|expiration
operator|=
literal|1970
expr_stmt|;
name|credentials
operator|.
name|setExpiration
argument_list|(
name|expiration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRoundTrip ()
specifier|public
name|void
name|testRoundTrip
parameter_list|()
throws|throws
name|Throwable
block|{
name|MarshalledCredentials
name|c2
init|=
name|S3ATestUtils
operator|.
name|roundTrip
argument_list|(
name|this
operator|.
name|credentials
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|credentials
argument_list|,
name|c2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"accessKey"
argument_list|,
name|c2
operator|.
name|getAccessKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"secretKey"
argument_list|,
name|c2
operator|.
name|getSecretKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sessionToken"
argument_list|,
name|c2
operator|.
name|getSessionToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expiration
argument_list|,
name|c2
operator|.
name|getExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|credentials
argument_list|,
name|c2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRoundTripNoSessionData ()
specifier|public
name|void
name|testRoundTripNoSessionData
parameter_list|()
throws|throws
name|Throwable
block|{
name|MarshalledCredentials
name|c
init|=
operator|new
name|MarshalledCredentials
argument_list|()
decl_stmt|;
name|c
operator|.
name|setAccessKey
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|c
operator|.
name|setSecretKey
argument_list|(
literal|"K"
argument_list|)
expr_stmt|;
name|MarshalledCredentials
name|c2
init|=
name|S3ATestUtils
operator|.
name|roundTrip
argument_list|(
name|c
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|c
argument_list|,
name|c2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRoundTripEncryptionData ()
specifier|public
name|void
name|testRoundTripEncryptionData
parameter_list|()
throws|throws
name|Throwable
block|{
name|EncryptionSecrets
name|secrets
init|=
operator|new
name|EncryptionSecrets
argument_list|(
name|S3AEncryptionMethods
operator|.
name|SSE_KMS
argument_list|,
literal|"key"
argument_list|)
decl_stmt|;
name|EncryptionSecrets
name|result
init|=
name|S3ATestUtils
operator|.
name|roundTrip
argument_list|(
name|secrets
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"round trip"
argument_list|,
name|secrets
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMarshalledCredentialProviderSession ()
specifier|public
name|void
name|testMarshalledCredentialProviderSession
parameter_list|()
throws|throws
name|Throwable
block|{
name|MarshalledCredentialProvider
name|provider
init|=
operator|new
name|MarshalledCredentialProvider
argument_list|(
literal|"test"
argument_list|,
name|bucketURI
argument_list|,
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
argument_list|,
name|credentials
argument_list|,
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
operator|.
name|SessionOnly
argument_list|)
decl_stmt|;
name|AWSCredentials
name|aws
init|=
name|provider
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|credentials
operator|.
name|toString
argument_list|()
argument_list|,
name|credentials
operator|.
name|getAccessKey
argument_list|()
argument_list|,
name|aws
operator|.
name|getAWSAccessKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|credentials
operator|.
name|toString
argument_list|()
argument_list|,
name|credentials
operator|.
name|getSecretKey
argument_list|()
argument_list|,
name|aws
operator|.
name|getAWSSecretKey
argument_list|()
argument_list|)
expr_stmt|;
comment|// because the credentials are set to full only, creation will fail
block|}
comment|/**    * Create with a mismatch of type and supplied credentials.    * Verify that the operation fails, but only when credentials    * are actually requested.    */
annotation|@
name|Test
DECL|method|testCredentialTypeMismatch ()
specifier|public
name|void
name|testCredentialTypeMismatch
parameter_list|()
throws|throws
name|Throwable
block|{
name|MarshalledCredentialProvider
name|provider
init|=
operator|new
name|MarshalledCredentialProvider
argument_list|(
literal|"test"
argument_list|,
name|bucketURI
argument_list|,
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
argument_list|,
name|credentials
argument_list|,
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
operator|.
name|FullOnly
argument_list|)
decl_stmt|;
comment|// because the credentials are set to full only, creation will fail
name|intercept
argument_list|(
name|NoAuthWithAWSException
operator|.
name|class
argument_list|,
literal|"test"
argument_list|,
parameter_list|()
lambda|->
name|provider
operator|.
name|getCredentials
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This provider fails fast if there's no URL.    */
annotation|@
name|Test
DECL|method|testCredentialProviderNullURI ()
specifier|public
name|void
name|testCredentialProviderNullURI
parameter_list|()
throws|throws
name|Throwable
block|{
name|intercept
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|,
literal|""
argument_list|,
parameter_list|()
lambda|->
operator|new
name|MarshalledCredentialProvider
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|,
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
argument_list|,
name|credentials
argument_list|,
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
operator|.
name|FullOnly
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

