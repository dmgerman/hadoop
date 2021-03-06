begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|FixMethodOrder
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
name|runners
operator|.
name|MethodSorters
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeNotNull
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
name|azure
operator|.
name|integration
operator|.
name|AzureTestUtils
operator|.
name|cleanupTestAccount
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
name|azure
operator|.
name|integration
operator|.
name|AzureTestUtils
operator|.
name|readStringFromFile
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
name|azure
operator|.
name|integration
operator|.
name|AzureTestUtils
operator|.
name|writeStringToFile
import|;
end_import

begin_class
annotation|@
name|FixMethodOrder
argument_list|(
name|MethodSorters
operator|.
name|NAME_ASCENDING
argument_list|)
comment|/**  * Because FileSystem.Statistics is per FileSystem, so statistics can not be ran in  * parallel, hence in this test file, force them to run in sequential.  */
DECL|class|ITestNativeFileSystemStatistics
specifier|public
class|class
name|ITestNativeFileSystemStatistics
extends|extends
name|AbstractWasbTestWithTimeout
block|{
annotation|@
name|Test
DECL|method|test_001_NativeAzureFileSystemMocked ()
specifier|public
name|void
name|test_001_NativeAzureFileSystemMocked
parameter_list|()
throws|throws
name|Exception
block|{
name|AzureBlobStorageTestAccount
name|testAccount
init|=
name|AzureBlobStorageTestAccount
operator|.
name|createMock
argument_list|()
decl_stmt|;
name|assumeNotNull
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
name|testStatisticsWithAccount
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test_002_NativeAzureFileSystemPageBlobLive ()
specifier|public
name|void
name|test_002_NativeAzureFileSystemPageBlobLive
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
name|AzureBlobStorageTestAccount
name|testAccount
init|=
name|AzureBlobStorageTestAccount
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assumeNotNull
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
name|testStatisticsWithAccount
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test_003_NativeAzureFileSystem ()
specifier|public
name|void
name|test_003_NativeAzureFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|AzureBlobStorageTestAccount
name|testAccount
init|=
name|AzureBlobStorageTestAccount
operator|.
name|create
argument_list|()
decl_stmt|;
name|assumeNotNull
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
name|testStatisticsWithAccount
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
block|}
DECL|method|testStatisticsWithAccount (AzureBlobStorageTestAccount testAccount)
specifier|private
name|void
name|testStatisticsWithAccount
parameter_list|(
name|AzureBlobStorageTestAccount
name|testAccount
parameter_list|)
throws|throws
name|Exception
block|{
name|assumeNotNull
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
name|NativeAzureFileSystem
name|fs
init|=
name|testAccount
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|testStatistics
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|cleanupTestAccount
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
block|}
comment|/**    * When tests are ran in parallel, this tests will fail because    * FileSystem.Statistics is per FileSystem class.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testStatistics (NativeAzureFileSystem fs)
specifier|private
name|void
name|testStatistics
parameter_list|(
name|NativeAzureFileSystem
name|fs
parameter_list|)
throws|throws
name|Exception
block|{
name|FileSystem
operator|.
name|clearStatistics
argument_list|()
expr_stmt|;
name|FileSystem
operator|.
name|Statistics
name|stats
init|=
name|FileSystem
operator|.
name|getStatistics
argument_list|(
literal|"wasb"
argument_list|,
name|NativeAzureFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|newFile
init|=
operator|new
name|Path
argument_list|(
literal|"testStats"
argument_list|)
decl_stmt|;
name|writeStringToFile
argument_list|(
name|fs
argument_list|,
name|newFile
argument_list|,
literal|"12345678"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|stats
operator|.
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|readBack
init|=
name|readStringFromFile
argument_list|(
name|fs
argument_list|,
name|newFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"12345678"
argument_list|,
name|readBack
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|stats
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|stats
operator|.
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
name|newFile
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|stats
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|stats
operator|.
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

