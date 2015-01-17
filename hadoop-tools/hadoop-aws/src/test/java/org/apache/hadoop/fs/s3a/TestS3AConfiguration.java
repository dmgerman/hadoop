begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3Client
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
name|lang
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonClientException
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
name|Timeout
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
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|fail
import|;
end_import

begin_class
DECL|class|TestS3AConfiguration
specifier|public
class|class
name|TestS3AConfiguration
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|fs
specifier|private
name|S3AFileSystem
name|fs
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
name|TestS3AConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_ENDPOINT
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ENDPOINT
init|=
literal|"test.fs.s3a.endpoint"
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
literal|30
operator|*
literal|60
operator|*
literal|1000
argument_list|)
decl_stmt|;
comment|/**    * Test if custom endpoint is picked up.    *<p/>    * The test expects TEST_ENDPOINT to be defined in the Configuration    * describing the endpoint of the bucket to which TEST_FS_S3A_NAME points    * (f.i. "s3-eu-west-1.amazonaws.com" if the bucket is located in Ireland).    * Evidently, the bucket has to be hosted in the region denoted by the    * endpoint for the test to succeed.    *<p/>    * More info and the list of endpoint identifiers:    * http://docs.aws.amazon.com/general/latest/gr/rande.html#s3_region    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|TestEndpoint ()
specifier|public
name|void
name|TestEndpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|String
name|endpoint
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|TEST_ENDPOINT
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|endpoint
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Custom endpoint test skipped as "
operator|+
name|TEST_ENDPOINT
operator|+
literal|"config "
operator|+
literal|"setting was not detected"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|ENDPOINT
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
name|fs
operator|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|AmazonS3Client
name|s3
init|=
name|fs
operator|.
name|getAmazonS3Client
argument_list|()
decl_stmt|;
name|String
name|endPointRegion
init|=
literal|""
decl_stmt|;
comment|// Differentiate handling of "s3-" and "s3." based endpoint identifiers
name|String
index|[]
name|endpointParts
init|=
name|StringUtils
operator|.
name|split
argument_list|(
name|endpoint
argument_list|,
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|endpointParts
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|endPointRegion
operator|=
name|endpointParts
index|[
literal|0
index|]
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|endpointParts
operator|.
name|length
operator|==
literal|4
condition|)
block|{
name|endPointRegion
operator|=
name|endpointParts
index|[
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected endpoint"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Endpoint config setting and bucket location differ: "
argument_list|,
name|endPointRegion
argument_list|,
name|s3
operator|.
name|getBucketLocation
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getHost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|TestProxyConnection ()
specifier|public
name|void
name|TestProxyConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|Constants
operator|.
name|MAX_ERROR_RETRIES
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|PROXY_HOST
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|Constants
operator|.
name|PROXY_PORT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|String
name|proxy
init|=
name|conf
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|PROXY_HOST
argument_list|)
operator|+
literal|":"
operator|+
name|conf
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|PROXY_PORT
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected a connection error for proxy server at "
operator|+
name|proxy
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AmazonClientException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|proxy
operator|+
literal|" refused"
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|TestProxyPortWithoutHost ()
specifier|public
name|void
name|TestProxyPortWithoutHost
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|Constants
operator|.
name|MAX_ERROR_RETRIES
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|Constants
operator|.
name|PROXY_PORT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected a proxy configuration error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|msg
operator|.
name|contains
argument_list|(
name|Constants
operator|.
name|PROXY_HOST
argument_list|)
operator|&&
operator|!
name|msg
operator|.
name|contains
argument_list|(
name|Constants
operator|.
name|PROXY_PORT
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|TestAutomaticProxyPortSelection ()
specifier|public
name|void
name|TestAutomaticProxyPortSelection
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|Constants
operator|.
name|MAX_ERROR_RETRIES
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|PROXY_HOST
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|SECURE_CONNECTIONS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected a connection error for proxy server"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AmazonClientException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"443"
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|SECURE_CONNECTIONS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected a connection error for proxy server"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AmazonClientException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"80"
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|TestUsernameInconsistentWithPassword ()
specifier|public
name|void
name|TestUsernameInconsistentWithPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|Constants
operator|.
name|MAX_ERROR_RETRIES
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|PROXY_HOST
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|Constants
operator|.
name|PROXY_PORT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|PROXY_USERNAME
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected a connection error for proxy server"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|msg
operator|.
name|contains
argument_list|(
name|Constants
operator|.
name|PROXY_USERNAME
argument_list|)
operator|&&
operator|!
name|msg
operator|.
name|contains
argument_list|(
name|Constants
operator|.
name|PROXY_PASSWORD
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|Constants
operator|.
name|MAX_ERROR_RETRIES
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|PROXY_HOST
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|Constants
operator|.
name|PROXY_PORT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|PROXY_PASSWORD
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected a connection error for proxy server"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|msg
operator|.
name|contains
argument_list|(
name|Constants
operator|.
name|PROXY_USERNAME
argument_list|)
operator|&&
operator|!
name|msg
operator|.
name|contains
argument_list|(
name|Constants
operator|.
name|PROXY_PASSWORD
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

