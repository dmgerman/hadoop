begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
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
name|util
operator|.
name|UUID
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

begin_class
DECL|class|TestFileSystemOperationExceptionMessage
specifier|public
class|class
name|TestFileSystemOperationExceptionMessage
extends|extends
name|NativeAzureFileSystemBaseTest
block|{
annotation|@
name|Test
DECL|method|testAnonymouseCredentialExceptionMessage ()
specifier|public
name|void
name|testAnonymouseCredentialExceptionMessage
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
name|AzureBlobStorageTestAccount
operator|.
name|createTestConfiguration
argument_list|()
decl_stmt|;
name|String
name|testStorageAccount
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"fs.azure.test.account.name"
argument_list|)
decl_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.AbstractFileSystem.wasb.impl"
argument_list|,
literal|"org.apache.hadoop.fs.azure.Wasb"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.azure.skip.metrics"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|testContainer
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|wasbUri
init|=
name|String
operator|.
name|format
argument_list|(
literal|"wasb://%s@%s"
argument_list|,
name|testContainer
argument_list|,
name|testStorageAccount
argument_list|)
decl_stmt|;
name|String
name|expectedErrorMessage
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Container %s in account %s not found, and we can't create it "
operator|+
literal|"using anoynomous credentials, and no credentials found for "
operator|+
literal|"them in the configuration."
argument_list|,
name|testContainer
argument_list|,
name|testStorageAccount
argument_list|)
decl_stmt|;
name|fs
operator|=
operator|new
name|NativeAzureFileSystem
argument_list|()
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|initialize
argument_list|(
operator|new
name|URI
argument_list|(
name|wasbUri
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Throwable
name|innerException
init|=
name|ex
operator|.
name|getCause
argument_list|()
decl_stmt|;
while|while
condition|(
name|innerException
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|innerException
operator|instanceof
name|AzureException
operator|)
condition|)
block|{
name|innerException
operator|=
name|innerException
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|innerException
operator|!=
literal|null
condition|)
block|{
name|String
name|exceptionMessage
init|=
name|innerException
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|exceptionMessage
operator|==
literal|null
operator|||
name|exceptionMessage
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|equals
argument_list|(
name|expectedErrorMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createTestAccount ()
specifier|protected
name|AzureBlobStorageTestAccount
name|createTestAccount
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|AzureBlobStorageTestAccount
operator|.
name|create
argument_list|()
return|;
block|}
block|}
end_class

end_unit

