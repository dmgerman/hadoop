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
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * Run {@link NativeAzureFileSystemBaseTest} tests against a mocked store,  * skipping tests of unsupported features  */
end_comment

begin_class
DECL|class|TestNativeAzureFileSystemMocked
specifier|public
class|class
name|TestNativeAzureFileSystemMocked
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
return|return
name|AzureBlobStorageTestAccount
operator|.
name|createMock
argument_list|()
return|;
block|}
comment|// Ignore the following tests because taking a lease requires a real
comment|// (not mock) file system store. These tests don't work on the mock.
annotation|@
name|Override
annotation|@
name|Ignore
DECL|method|testLeaseAsDistributedLock ()
specifier|public
name|void
name|testLeaseAsDistributedLock
parameter_list|()
block|{   }
annotation|@
name|Override
annotation|@
name|Ignore
DECL|method|testSelfRenewingLease ()
specifier|public
name|void
name|testSelfRenewingLease
parameter_list|()
block|{   }
annotation|@
name|Override
annotation|@
name|Ignore
DECL|method|testRedoFolderRenameAll ()
specifier|public
name|void
name|testRedoFolderRenameAll
parameter_list|()
block|{   }
annotation|@
name|Override
annotation|@
name|Ignore
DECL|method|testCreateNonRecursive ()
specifier|public
name|void
name|testCreateNonRecursive
parameter_list|()
block|{   }
annotation|@
name|Override
annotation|@
name|Ignore
DECL|method|testSelfRenewingLeaseFileDelete ()
specifier|public
name|void
name|testSelfRenewingLeaseFileDelete
parameter_list|()
block|{   }
annotation|@
name|Override
annotation|@
name|Ignore
DECL|method|testRenameRedoFolderAlreadyDone ()
specifier|public
name|void
name|testRenameRedoFolderAlreadyDone
parameter_list|()
throws|throws
name|IOException
block|{   }
block|}
end_class

end_unit

