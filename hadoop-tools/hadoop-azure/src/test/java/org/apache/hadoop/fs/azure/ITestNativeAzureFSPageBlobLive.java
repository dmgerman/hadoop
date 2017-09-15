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

begin_comment
comment|/**  * Run the base Azure file system tests strictly on page blobs to make sure fundamental  * operations on page blob files and folders work as expected.  * These operations include create, delete, rename, list, and so on.  */
end_comment

begin_class
DECL|class|ITestNativeAzureFSPageBlobLive
specifier|public
class|class
name|ITestNativeAzureFSPageBlobLive
extends|extends
name|NativeAzureFileSystemBaseTest
block|{
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Configure the page blob directories key so every file created is a page blob.
name|conf
operator|.
name|set
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|KEY_PAGE_BLOB_DIRECTORIES
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
comment|// Configure the atomic rename directories key so every folder will have
comment|// atomic rename applied.
name|conf
operator|.
name|set
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|KEY_ATOMIC_RENAME_DIRECTORIES
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
return|return
name|AzureBlobStorageTestAccount
operator|.
name|create
argument_list|(
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

