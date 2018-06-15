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
name|Random
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
name|Path
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test append operations.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemAppend
specifier|public
class|class
name|ITestAzureBlobFileSystemAppend
extends|extends
name|DependencyInjectedTest
block|{
DECL|field|TEST_FILE_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_FILE_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"testfile"
argument_list|)
decl_stmt|;
DECL|field|TEST_FOLDER_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_FOLDER_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"testFolder"
argument_list|)
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemAppend ()
specifier|public
name|ITestAzureBlobFileSystemAppend
parameter_list|()
block|{
name|super
argument_list|()
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
DECL|method|testAppendDirShouldFail ()
specifier|public
name|void
name|testAppendDirShouldFail
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
specifier|final
name|Path
name|filePath
init|=
name|TEST_FILE_PATH
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|append
argument_list|(
name|filePath
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppendWithLength0 ()
specifier|public
name|void
name|testAppendWithLength0
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
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|TEST_FILE_PATH
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|1000
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stream
operator|.
name|getPos
argument_list|()
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
DECL|method|testAppendFileAfterDelete ()
specifier|public
name|void
name|testAppendFileAfterDelete
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
specifier|final
name|Path
name|filePath
init|=
name|TEST_FILE_PATH
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fs
operator|.
name|append
argument_list|(
name|filePath
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
DECL|method|testAppendDirectory ()
specifier|public
name|void
name|testAppendDirectory
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
specifier|final
name|Path
name|folderPath
init|=
name|TEST_FOLDER_PATH
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|folderPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|append
argument_list|(
name|folderPath
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

