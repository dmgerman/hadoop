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
import|import static
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
operator|.
name|assertMkdirs
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|assertPathDoesNotExist
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|assertRenameOutcome
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|assertIsFile
import|;
end_import

begin_comment
comment|/**  * Test rename operation.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemRename
specifier|public
class|class
name|ITestAzureBlobFileSystemRename
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|method|ITestAzureBlobFileSystemRename ()
specifier|public
name|ITestAzureBlobFileSystemRename
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
DECL|method|testEnsureFileIsRenamed ()
specifier|public
name|void
name|testEnsureFileIsRenamed
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
name|src
init|=
name|path
argument_list|(
literal|"testEnsureFileIsRenamed-src"
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Path
name|dest
init|=
name|path
argument_list|(
literal|"testEnsureFileIsRenamed-dest"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dest
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertRenameOutcome
argument_list|(
name|fs
argument_list|,
name|src
argument_list|,
name|dest
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertIsFile
argument_list|(
name|fs
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"expected renamed"
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameFileUnderDir ()
specifier|public
name|void
name|testRenameFileUnderDir
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
name|sourceDir
init|=
operator|new
name|Path
argument_list|(
literal|"/testSrc"
argument_list|)
decl_stmt|;
name|assertMkdirs
argument_list|(
name|fs
argument_list|,
name|sourceDir
argument_list|)
expr_stmt|;
name|String
name|filename
init|=
literal|"file1"
decl_stmt|;
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|sourceDir
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|file1
argument_list|)
expr_stmt|;
name|Path
name|destDir
init|=
operator|new
name|Path
argument_list|(
literal|"/testDst"
argument_list|)
decl_stmt|;
name|assertRenameOutcome
argument_list|(
name|fs
argument_list|,
name|sourceDir
argument_list|,
name|destDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|fileStatus
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|destDir
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Null file status"
argument_list|,
name|fileStatus
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fileStatus
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong filename in "
operator|+
name|status
argument_list|,
name|filename
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameDirectory ()
specifier|public
name|void
name|testRenameDirectory
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
literal|"testDir"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|test1
init|=
operator|new
name|Path
argument_list|(
literal|"testDir/test1"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"testDir/test1/test2"
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
literal|"testDir/test1/test2/test3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertRenameOutcome
argument_list|(
name|fs
argument_list|,
name|test1
argument_list|,
operator|new
name|Path
argument_list|(
literal|"testDir/test10"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"rename source dir"
argument_list|,
name|test1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameFirstLevelDirectory ()
specifier|public
name|void
name|testRenameFirstLevelDirectory
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
literal|1000
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
literal|"/test/"
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
name|Path
name|source
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|Path
name|dest
init|=
operator|new
name|Path
argument_list|(
literal|"/renamedDir"
argument_list|)
decl_stmt|;
name|assertRenameOutcome
argument_list|(
name|fs
argument_list|,
name|source
argument_list|,
name|dest
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|files
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of files in listing"
argument_list|,
literal|1000
argument_list|,
name|files
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"rename source dir"
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameRoot ()
specifier|public
name|void
name|testRenameRoot
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
name|assertRenameOutcome
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/testRenameRoot"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertRenameOutcome
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/s"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPosixRenameDirectory ()
specifier|public
name|void
name|testPosixRenameDirectory
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
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
literal|"testDir2/test1/test2/test3"
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
literal|"testDir2/test4"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|rename
argument_list|(
operator|new
name|Path
argument_list|(
literal|"testDir2/test1/test2/test3"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"testDir2/test4"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"testDir2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"testDir2/test1/test2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"testDir2/test4"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"testDir2/test4/test3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"testDir2/test1/test2/test3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

