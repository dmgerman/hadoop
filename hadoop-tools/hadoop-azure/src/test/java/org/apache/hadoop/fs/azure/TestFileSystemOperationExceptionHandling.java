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
name|FileNotFoundException
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
operator|.
name|FsAction
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
name|permission
operator|.
name|FsPermission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
DECL|class|TestFileSystemOperationExceptionHandling
specifier|public
class|class
name|TestFileSystemOperationExceptionHandling
extends|extends
name|NativeAzureFileSystemBaseTest
block|{
DECL|field|inputStream
specifier|private
name|FSDataInputStream
name|inputStream
init|=
literal|null
decl_stmt|;
DECL|field|testPath
specifier|private
specifier|static
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
literal|"testfile.dat"
argument_list|)
decl_stmt|;
DECL|field|testFolderPath
specifier|private
specifier|static
name|Path
name|testFolderPath
init|=
operator|new
name|Path
argument_list|(
literal|"testfolder"
argument_list|)
decl_stmt|;
comment|/*    * Helper method that creates a InputStream to validate exceptions    * for various scenarios    */
DECL|method|setupInputStreamToTest (AzureBlobStorageTestAccount testAccount)
specifier|private
name|void
name|setupInputStreamToTest
parameter_list|(
name|AzureBlobStorageTestAccount
name|testAccount
parameter_list|)
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|testAccount
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Step 1: Create a file and write dummy data.
name|Path
name|testFilePath1
init|=
operator|new
name|Path
argument_list|(
literal|"test1.dat"
argument_list|)
decl_stmt|;
name|Path
name|testFilePath2
init|=
operator|new
name|Path
argument_list|(
literal|"test2.dat"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|outputStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|testFilePath1
argument_list|)
decl_stmt|;
name|String
name|testString
init|=
literal|"This is a test string"
decl_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
name|testString
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Step 2: Open a read stream on the file.
name|inputStream
operator|=
name|fs
operator|.
name|open
argument_list|(
name|testFilePath1
argument_list|)
expr_stmt|;
comment|// Step 3: Rename the file
name|fs
operator|.
name|rename
argument_list|(
name|testFilePath1
argument_list|,
name|testFilePath2
argument_list|)
expr_stmt|;
block|}
comment|/*    * Tests a basic single threaded read scenario for Page blobs.    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testSingleThreadedPageBlobReadScenario ()
specifier|public
name|void
name|testSingleThreadedPageBlobReadScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|AzureBlobStorageTestAccount
name|testAccount
init|=
name|ExceptionHandlingTestHelper
operator|.
name|getPageBlobTestStorageAccount
argument_list|()
decl_stmt|;
name|setupInputStreamToTest
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
name|byte
index|[]
name|readBuffer
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|inputStream
operator|.
name|read
argument_list|(
name|readBuffer
argument_list|)
expr_stmt|;
block|}
comment|/*    * Tests a basic single threaded seek scenario for Page blobs.    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testSingleThreadedPageBlobSeekScenario ()
specifier|public
name|void
name|testSingleThreadedPageBlobSeekScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|AzureBlobStorageTestAccount
name|testAccount
init|=
name|ExceptionHandlingTestHelper
operator|.
name|getPageBlobTestStorageAccount
argument_list|()
decl_stmt|;
name|setupInputStreamToTest
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
name|inputStream
operator|.
name|seek
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test a basic single thread seek scenario for Block blobs.    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testSingleThreadBlockBlobSeekScenario ()
specifier|public
name|void
name|testSingleThreadBlockBlobSeekScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|AzureBlobStorageTestAccount
name|testAccount
init|=
name|createTestAccount
argument_list|()
decl_stmt|;
name|setupInputStreamToTest
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
name|inputStream
operator|.
name|seek
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
comment|/*    * Tests a basic single threaded read scenario for Block blobs.    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testSingledThreadBlockBlobReadScenario ()
specifier|public
name|void
name|testSingledThreadBlockBlobReadScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|AzureBlobStorageTestAccount
name|testAccount
init|=
name|createTestAccount
argument_list|()
decl_stmt|;
name|setupInputStreamToTest
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
name|byte
index|[]
name|readBuffer
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|inputStream
operator|.
name|read
argument_list|(
name|readBuffer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
comment|/*    * Tests basic single threaded setPermission scenario    */
DECL|method|testSingleThreadedBlockBlobSetPermissionScenario ()
specifier|public
name|void
name|testSingleThreadedBlockBlobSetPermissionScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|createTestAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|testPath
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|EXECUTE
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
comment|/*    * Tests basic single threaded setPermission scenario    */
DECL|method|testSingleThreadedPageBlobSetPermissionScenario ()
specifier|public
name|void
name|testSingleThreadedPageBlobSetPermissionScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|ExceptionHandlingTestHelper
operator|.
name|getPageBlobTestStorageAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|testPath
argument_list|,
literal|"testowner"
argument_list|,
literal|"testgroup"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
comment|/*    * Tests basic single threaded setPermission scenario    */
DECL|method|testSingleThreadedBlockBlobSetOwnerScenario ()
specifier|public
name|void
name|testSingleThreadedBlockBlobSetOwnerScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|createTestAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|testPath
argument_list|,
literal|"testowner"
argument_list|,
literal|"testgroup"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
comment|/*    * Tests basic single threaded setPermission scenario    */
DECL|method|testSingleThreadedPageBlobSetOwnerScenario ()
specifier|public
name|void
name|testSingleThreadedPageBlobSetOwnerScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|ExceptionHandlingTestHelper
operator|.
name|getPageBlobTestStorageAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|testPath
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|EXECUTE
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
comment|/*    * Test basic single threaded listStatus scenario    */
DECL|method|testSingleThreadedBlockBlobListStatusScenario ()
specifier|public
name|void
name|testSingleThreadedBlockBlobListStatusScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createTestFolder
argument_list|(
name|createTestAccount
argument_list|()
argument_list|,
name|testFolderPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testFolderPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|listStatus
argument_list|(
name|testFolderPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
comment|/*    * Test basica single threaded listStatus scenario    */
DECL|method|testSingleThreadedPageBlobListStatusScenario ()
specifier|public
name|void
name|testSingleThreadedPageBlobListStatusScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createTestFolder
argument_list|(
name|ExceptionHandlingTestHelper
operator|.
name|getPageBlobTestStorageAccount
argument_list|()
argument_list|,
name|testFolderPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testFolderPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|listStatus
argument_list|(
name|testFolderPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/*    * Test basic single threaded listStatus scenario    */
DECL|method|testSingleThreadedBlockBlobRenameScenario ()
specifier|public
name|void
name|testSingleThreadedBlockBlobRenameScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|createTestAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|Path
name|dstPath
init|=
operator|new
name|Path
argument_list|(
literal|"dstFile.dat"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|boolean
name|renameResult
init|=
name|fs
operator|.
name|rename
argument_list|(
name|testPath
argument_list|,
name|dstPath
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|renameResult
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/*    * Test basic single threaded listStatus scenario    */
DECL|method|testSingleThreadedPageBlobRenameScenario ()
specifier|public
name|void
name|testSingleThreadedPageBlobRenameScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|ExceptionHandlingTestHelper
operator|.
name|getPageBlobTestStorageAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|Path
name|dstPath
init|=
operator|new
name|Path
argument_list|(
literal|"dstFile.dat"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|boolean
name|renameResult
init|=
name|fs
operator|.
name|rename
argument_list|(
name|testPath
argument_list|,
name|dstPath
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|renameResult
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/*    * Test basic single threaded listStatus scenario    */
DECL|method|testSingleThreadedBlockBlobDeleteScenario ()
specifier|public
name|void
name|testSingleThreadedBlockBlobDeleteScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|createTestAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|boolean
name|deleteResult
init|=
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|deleteResult
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/*    * Test basic single threaded listStatus scenario    */
DECL|method|testSingleThreadedPageBlobDeleteScenario ()
specifier|public
name|void
name|testSingleThreadedPageBlobDeleteScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|ExceptionHandlingTestHelper
operator|.
name|getPageBlobTestStorageAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|boolean
name|deleteResult
init|=
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|deleteResult
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
comment|/*    * Test basic single threaded listStatus scenario    */
DECL|method|testSingleThreadedBlockBlobOpenScenario ()
specifier|public
name|void
name|testSingleThreadedBlockBlobOpenScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|createTestAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|inputStream
operator|=
name|fs
operator|.
name|open
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
comment|/*    * Test basic single threaded listStatus scenario    */
DECL|method|testSingleThreadedPageBlobOpenScenario ()
specifier|public
name|void
name|testSingleThreadedPageBlobOpenScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExceptionHandlingTestHelper
operator|.
name|createEmptyFile
argument_list|(
name|ExceptionHandlingTestHelper
operator|.
name|getPageBlobTestStorageAccount
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|inputStream
operator|=
name|fs
operator|.
name|open
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|inputStream
operator|!=
literal|null
condition|)
block|{
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fs
operator|!=
literal|null
operator|&&
name|fs
operator|.
name|exists
argument_list|(
name|testPath
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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

