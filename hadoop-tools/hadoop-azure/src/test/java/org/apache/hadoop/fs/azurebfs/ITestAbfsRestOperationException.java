begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
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
name|azurebfs
operator|.
name|oauth2
operator|.
name|RetryTestTokenProvider
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
name|FileStatus
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
name|FileSystem
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
name|Test
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
comment|/**  * Verify the AbfsRestOperationException error message format.  * */
end_comment

begin_class
DECL|class|ITestAbfsRestOperationException
specifier|public
class|class
name|ITestAbfsRestOperationException
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|method|ITestAbfsRestOperationException ()
specifier|public
name|ITestAbfsRestOperationException
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAbfsRestOperationExceptionFormat ()
specifier|public
name|void
name|testAbfsRestOperationExceptionFormat
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|nonExistedFilePath1
init|=
operator|new
name|Path
argument_list|(
literal|"nonExistedPath1"
argument_list|)
decl_stmt|;
name|Path
name|nonExistedFilePath2
init|=
operator|new
name|Path
argument_list|(
literal|"nonExistedPath2"
argument_list|)
decl_stmt|;
try|try
block|{
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|nonExistedFilePath1
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|String
name|errorMessage
init|=
name|ex
operator|.
name|getLocalizedMessage
argument_list|()
decl_stmt|;
name|String
index|[]
name|errorFields
init|=
name|errorMessage
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|errorFields
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Check status message, status code, HTTP Request Type and URL.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Operation failed: \"The specified path does not exist.\""
argument_list|,
name|errorFields
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"404"
argument_list|,
name|errorFields
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"HEAD"
argument_list|,
name|errorFields
index|[
literal|2
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|errorFields
index|[
literal|3
index|]
operator|.
name|trim
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"http"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|fs
operator|.
name|listFiles
argument_list|(
name|nonExistedFilePath2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// verify its format
name|String
name|errorMessage
init|=
name|ex
operator|.
name|getLocalizedMessage
argument_list|()
decl_stmt|;
name|String
index|[]
name|errorFields
init|=
name|errorMessage
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|errorFields
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Check status message, status code, HTTP Request Type and URL.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Operation failed: \"The specified path does not exist.\""
argument_list|,
name|errorFields
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"404"
argument_list|,
name|errorFields
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"GET"
argument_list|,
name|errorFields
index|[
literal|2
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|errorFields
index|[
literal|3
index|]
operator|.
name|trim
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"http"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check storage error code and storage error message.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"PathNotFound"
argument_list|,
name|errorFields
index|[
literal|4
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|errorFields
index|[
literal|5
index|]
operator|.
name|contains
argument_list|(
literal|"RequestId"
argument_list|)
operator|&&
name|errorFields
index|[
literal|5
index|]
operator|.
name|contains
argument_list|(
literal|"Time"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRequestRetryConfig ()
specifier|public
name|void
name|testRequestRetryConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|testRetryLogic
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testRetryLogic
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|testRetryLogic (int numOfRetries)
specifier|public
name|void
name|testRetryLogic
parameter_list|(
name|int
name|numOfRetries
parameter_list|)
throws|throws
name|Exception
block|{
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|this
operator|.
name|getRawConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|accountName
init|=
name|config
operator|.
name|get
argument_list|(
literal|"fs.azure.abfs.account.name"
argument_list|)
decl_stmt|;
comment|// Setup to configure custom token provider
name|config
operator|.
name|set
argument_list|(
literal|"fs.azure.account.auth.type."
operator|+
name|accountName
argument_list|,
literal|"Custom"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
literal|"fs.azure.account.oauth.provider.type."
operator|+
name|accountName
argument_list|,
literal|"org.apache.hadoop.fs"
operator|+
literal|".azurebfs.oauth2.RetryTestTokenProvider"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
literal|"fs.azure.io.retry.max.retries"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numOfRetries
argument_list|)
argument_list|)
expr_stmt|;
comment|// Stop filesystem creation as it will lead to calls to store.
name|config
operator|.
name|set
argument_list|(
literal|"fs.azure.createRemoteFileSystemDuringInitialization"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
specifier|final
name|AzureBlobFileSystem
name|fs1
init|=
operator|(
name|AzureBlobFileSystem
operator|)
name|FileSystem
operator|.
name|newInstance
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|RetryTestTokenProvider
operator|.
name|ResetStatusToFirstTokenFetch
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs1
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
comment|// Number of retries done should be as configured
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Number of token fetch retries ("
operator|+
name|RetryTestTokenProvider
operator|.
name|reTryCount
operator|+
literal|") done, does not match with max "
operator|+
literal|"retry count configured ("
operator|+
name|numOfRetries
operator|+
literal|")"
argument_list|,
name|RetryTestTokenProvider
operator|.
name|reTryCount
operator|==
name|numOfRetries
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

