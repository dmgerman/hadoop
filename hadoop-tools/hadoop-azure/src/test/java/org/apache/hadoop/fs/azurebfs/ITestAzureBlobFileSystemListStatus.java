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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
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
name|LocatedFileStatus
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
name|contract
operator|.
name|ContractTestUtils
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
comment|/**  * Test listStatus operation.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemListStatus
specifier|public
class|class
name|ITestAzureBlobFileSystemListStatus
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|field|TEST_FILES_NUMBER
specifier|private
specifier|static
specifier|final
name|int
name|TEST_FILES_NUMBER
init|=
literal|6000
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemListStatus ()
specifier|public
name|ITestAzureBlobFileSystemListStatus
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
DECL|method|testListPath ()
specifier|public
name|void
name|testListPath
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ExecutorService
name|es
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|TEST_FILES_NUMBER
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable
init|=
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|touch
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|tasks
operator|.
name|add
argument_list|(
name|es
operator|.
name|submit
argument_list|(
name|callable
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Future
argument_list|<
name|Void
argument_list|>
name|task
range|:
name|tasks
control|)
block|{
name|task
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|es
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|FileStatus
index|[]
name|files
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_FILES_NUMBER
argument_list|,
name|files
operator|.
name|length
comment|/* user directory */
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a file, verifies that listStatus returns it,    * even while the file is still open for writing.    */
annotation|@
name|Test
DECL|method|testListFileVsListDir ()
specifier|public
name|void
name|testListFileVsListDir
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/testFile"
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|ignored
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
init|)
block|{
name|FileStatus
index|[]
name|testFiles
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"length of test files"
argument_list|,
literal|1
argument_list|,
name|testFiles
operator|.
name|length
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|testFiles
index|[
literal|0
index|]
decl_stmt|;
name|assertIsFileReference
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testListFileVsListDir2 ()
specifier|public
name|void
name|testListFileVsListDir2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testFolder"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testFolder/testFolder2"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testFolder/testFolder2/testFolder3"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|testFile0Path
init|=
operator|new
name|Path
argument_list|(
literal|"/testFolder/testFolder2/testFolder3/testFile"
argument_list|)
decl_stmt|;
name|ContractTestUtils
operator|.
name|touch
argument_list|(
name|fs
argument_list|,
name|testFile0Path
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|testFiles
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|testFile0Path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong listing size of file "
operator|+
name|testFile0Path
argument_list|,
literal|1
argument_list|,
name|testFiles
operator|.
name|length
argument_list|)
expr_stmt|;
name|FileStatus
name|file0
init|=
name|testFiles
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong path for "
operator|+
name|file0
argument_list|,
operator|new
name|Path
argument_list|(
name|getTestUrl
argument_list|()
argument_list|,
literal|"/testFolder/testFolder2/testFolder3/testFile"
argument_list|)
argument_list|,
name|file0
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertIsFileReference
argument_list|(
name|file0
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
DECL|method|testListNonExistentDir ()
specifier|public
name|void
name|testListNonExistentDir
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testFile/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListFiles ()
specifier|public
name|void
name|testListFiles
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|testDir
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|fileStatuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fileStatuses
operator|.
name|length
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/sub"
argument_list|)
argument_list|)
expr_stmt|;
name|fileStatuses
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fileStatuses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sub"
argument_list|,
name|fileStatuses
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertIsDirectoryReference
argument_list|(
name|fileStatuses
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Path
name|childF
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/f"
argument_list|)
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|childF
argument_list|)
expr_stmt|;
name|fileStatuses
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fileStatuses
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|FileStatus
name|childStatus
init|=
name|fileStatuses
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|childF
argument_list|,
name|childStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f"
argument_list|,
name|childStatus
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertIsFileReference
argument_list|(
name|childStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|childStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FileStatus
name|status1
init|=
name|fileStatuses
index|[
literal|1
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sub"
argument_list|,
name|status1
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertIsDirectoryReference
argument_list|(
name|status1
argument_list|)
expr_stmt|;
comment|// look at the child through getFileStatus
name|LocatedFileStatus
name|locatedChildStatus
init|=
name|fs
operator|.
name|listFiles
argument_list|(
name|childF
argument_list|,
literal|false
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertIsFileReference
argument_list|(
name|locatedChildStatus
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|fs
operator|.
name|listFiles
argument_list|(
name|childF
argument_list|,
literal|false
argument_list|)
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// do some final checks on the status (failing due to version checks)
name|assertEquals
argument_list|(
literal|"Path mismatch of "
operator|+
name|locatedChildStatus
argument_list|,
name|childF
argument_list|,
name|locatedChildStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"locatedstatus.equals(status)"
argument_list|,
name|locatedChildStatus
argument_list|,
name|childStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"status.equals(locatedstatus)"
argument_list|,
name|childStatus
argument_list|,
name|locatedChildStatus
argument_list|)
expr_stmt|;
block|}
DECL|method|assertIsDirectoryReference (FileStatus status)
specifier|private
name|void
name|assertIsDirectoryReference
parameter_list|(
name|FileStatus
name|status
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Not a directory: "
operator|+
name|status
argument_list|,
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Not a directory: "
operator|+
name|status
argument_list|,
name|status
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertIsFileReference (FileStatus status)
specifier|private
name|void
name|assertIsFileReference
parameter_list|(
name|FileStatus
name|status
parameter_list|)
block|{
name|assertFalse
argument_list|(
literal|"Not a file: "
operator|+
name|status
argument_list|,
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not a file: "
operator|+
name|status
argument_list|,
name|status
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

