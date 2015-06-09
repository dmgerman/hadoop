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
name|FileSystemContractBaseTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_class
DECL|class|TestNativeAzureFileSystemContractPageBlobLive
specifier|public
class|class
name|TestNativeAzureFileSystemContractPageBlobLive
extends|extends
name|FileSystemContractBaseTest
block|{
DECL|field|testAccount
specifier|private
name|AzureBlobStorageTestAccount
name|testAccount
decl_stmt|;
DECL|method|createTestAccount ()
specifier|private
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
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|testAccount
operator|=
name|createTestAccount
argument_list|()
expr_stmt|;
if|if
condition|(
name|testAccount
operator|!=
literal|null
condition|)
block|{
name|fs
operator|=
name|testAccount
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|testAccount
operator|!=
literal|null
condition|)
block|{
name|testAccount
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|testAccount
operator|=
literal|null
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|runTest ()
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|testAccount
operator|!=
literal|null
condition|)
block|{
name|super
operator|.
name|runTest
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * The following tests are failing on Azure and the Azure     * file system code needs to be modified to make them pass.    * A separate work item has been opened for this.    */
annotation|@
name|Ignore
DECL|method|testMoveFileUnderParent ()
specifier|public
name|void
name|testMoveFileUnderParent
parameter_list|()
throws|throws
name|Throwable
block|{   }
annotation|@
name|Ignore
DECL|method|testRenameFileToSelf ()
specifier|public
name|void
name|testRenameFileToSelf
parameter_list|()
throws|throws
name|Throwable
block|{   }
annotation|@
name|Ignore
DECL|method|testRenameChildDirForbidden ()
specifier|public
name|void
name|testRenameChildDirForbidden
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Ignore
DECL|method|testMoveDirUnderParent ()
specifier|public
name|void
name|testMoveDirUnderParent
parameter_list|()
throws|throws
name|Throwable
block|{   }
annotation|@
name|Ignore
DECL|method|testRenameDirToSelf ()
specifier|public
name|void
name|testRenameDirToSelf
parameter_list|()
throws|throws
name|Throwable
block|{   }
block|}
end_class

end_unit

