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
block|}
end_class

end_unit

