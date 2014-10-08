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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
comment|/**  * Tests the scenario where a colon is included in the file/directory name.  *   * NativeAzureFileSystem#create(), #mkdir(), and #rename() disallow the  * creation/rename of files/directories through WASB that have colons in the  * names.  */
end_comment

begin_class
DECL|class|TestNativeAzureFileSystemFileNameCheck
specifier|public
class|class
name|TestNativeAzureFileSystemFileNameCheck
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|field|testAccount
specifier|private
name|AzureBlobStorageTestAccount
name|testAccount
init|=
literal|null
decl_stmt|;
DECL|field|root
specifier|private
name|String
name|root
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|testAccount
operator|=
name|AzureBlobStorageTestAccount
operator|.
name|createMock
argument_list|()
expr_stmt|;
name|fs
operator|=
name|testAccount
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|root
operator|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
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
name|testAccount
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|root
operator|=
literal|null
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
name|testAccount
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreate ()
specifier|public
name|void
name|testCreate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// positive test
name|Path
name|testFile1
init|=
operator|new
name|Path
argument_list|(
name|root
operator|+
literal|"/testFile1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|createNewFile
argument_list|(
name|testFile1
argument_list|)
argument_list|)
expr_stmt|;
comment|// negative test
name|Path
name|testFile2
init|=
operator|new
name|Path
argument_list|(
name|root
operator|+
literal|"/testFile2:2"
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|createNewFile
argument_list|(
name|testFile2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should've thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
annotation|@
name|Test
DECL|method|testRename ()
specifier|public
name|void
name|testRename
parameter_list|()
throws|throws
name|Exception
block|{
comment|// positive test
name|Path
name|testFile1
init|=
operator|new
name|Path
argument_list|(
name|root
operator|+
literal|"/testFile1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|createNewFile
argument_list|(
name|testFile1
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|testFile2
init|=
operator|new
name|Path
argument_list|(
name|root
operator|+
literal|"/testFile2"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|testFile1
argument_list|,
name|testFile2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|testFile1
argument_list|)
operator|&&
name|fs
operator|.
name|exists
argument_list|(
name|testFile2
argument_list|)
argument_list|)
expr_stmt|;
comment|// negative test
name|Path
name|testFile3
init|=
operator|new
name|Path
argument_list|(
name|root
operator|+
literal|"/testFile3:3"
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|testFile2
argument_list|,
name|testFile3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should've thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|testFile2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirs ()
specifier|public
name|void
name|testMkdirs
parameter_list|()
throws|throws
name|Exception
block|{
comment|// positive test
name|Path
name|testFolder1
init|=
operator|new
name|Path
argument_list|(
name|root
operator|+
literal|"/testFolder1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|testFolder1
argument_list|)
argument_list|)
expr_stmt|;
comment|// negative test
name|Path
name|testFolder2
init|=
operator|new
name|Path
argument_list|(
name|root
operator|+
literal|"/testFolder2:2"
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|testFolder2
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should've thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
annotation|@
name|Test
DECL|method|testWasbFsck ()
specifier|public
name|void
name|testWasbFsck
parameter_list|()
throws|throws
name|Exception
block|{
comment|// positive test
name|Path
name|testFolder1
init|=
operator|new
name|Path
argument_list|(
name|root
operator|+
literal|"/testFolder1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|testFolder1
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|testFolder2
init|=
operator|new
name|Path
argument_list|(
name|testFolder1
argument_list|,
literal|"testFolder2"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|testFolder2
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|testFolder3
init|=
operator|new
name|Path
argument_list|(
name|testFolder1
argument_list|,
literal|"testFolder3"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|testFolder3
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|testFile1
init|=
operator|new
name|Path
argument_list|(
name|testFolder2
argument_list|,
literal|"testFile1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|createNewFile
argument_list|(
name|testFile1
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|testFile2
init|=
operator|new
name|Path
argument_list|(
name|testFolder1
argument_list|,
literal|"testFile2"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|createNewFile
argument_list|(
name|testFile2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|runWasbFsck
argument_list|(
name|testFolder1
argument_list|)
argument_list|)
expr_stmt|;
comment|// negative test
name|InMemoryBlockBlobStore
name|backingStore
init|=
name|testAccount
operator|.
name|getMockStorage
argument_list|()
operator|.
name|getBackingStore
argument_list|()
decl_stmt|;
name|backingStore
operator|.
name|setContent
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|toMockUri
argument_list|(
literal|"testFolder1/testFolder2/test2:2"
argument_list|)
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runWasbFsck
argument_list|(
name|testFolder1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|runWasbFsck (Path p)
specifier|private
name|boolean
name|runWasbFsck
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|Exception
block|{
name|WasbFsck
name|fsck
init|=
operator|new
name|WasbFsck
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|fsck
operator|.
name|setMockFileSystemForTesting
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|fsck
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
name|p
operator|.
name|toString
argument_list|()
block|}
argument_list|)
expr_stmt|;
return|return
name|fsck
operator|.
name|getPathNameWarning
argument_list|()
return|;
block|}
block|}
end_class

end_unit

