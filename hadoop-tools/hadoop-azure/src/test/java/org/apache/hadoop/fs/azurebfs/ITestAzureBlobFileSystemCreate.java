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
name|EnumSet
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
name|CreateFlag
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
name|FileAlreadyExistsException
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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

begin_comment
comment|/**  * Test create operation.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemCreate
specifier|public
class|class
name|ITestAzureBlobFileSystemCreate
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
DECL|field|TEST_CHILD_FILE
specifier|private
specifier|static
specifier|final
name|String
name|TEST_CHILD_FILE
init|=
literal|"childFile"
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemCreate ()
specifier|public
name|ITestAzureBlobFileSystemCreate
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
name|FileAlreadyExistsException
operator|.
name|class
argument_list|)
DECL|method|testCreateFileWithExistingDir ()
specifier|public
name|void
name|testCreateFileWithExistingDir
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
name|TEST_FOLDER_PATH
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_FOLDER_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnsureFileCreated ()
specifier|public
name|void
name|testEnsureFileCreated
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
name|create
argument_list|(
name|TEST_FILE_PATH
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|TEST_FILE_PATH
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testCreateNonRecursive ()
specifier|public
name|void
name|testCreateNonRecursive
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
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
name|TEST_FOLDER_PATH
argument_list|,
name|TEST_CHILD_FILE
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|createNonRecursive
argument_list|(
name|testFile
argument_list|,
literal|true
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should've thrown"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{     }
name|fs
operator|.
name|mkdirs
argument_list|(
name|TEST_FOLDER_PATH
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createNonRecursive
argument_list|(
name|testFile
argument_list|,
literal|true
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testCreateNonRecursive1 ()
specifier|public
name|void
name|testCreateNonRecursive1
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
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
name|TEST_FOLDER_PATH
argument_list|,
name|TEST_CHILD_FILE
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|createNonRecursive
argument_list|(
name|testFile
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|,
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should've thrown"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{     }
name|fs
operator|.
name|mkdirs
argument_list|(
name|TEST_FOLDER_PATH
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createNonRecursive
argument_list|(
name|testFile
argument_list|,
literal|true
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testCreateNonRecursive2 ()
specifier|public
name|void
name|testCreateNonRecursive2
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
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
name|TEST_FOLDER_PATH
argument_list|,
name|TEST_CHILD_FILE
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|createNonRecursive
argument_list|(
name|testFile
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should've thrown"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{     }
name|fs
operator|.
name|mkdirs
argument_list|(
name|TEST_FOLDER_PATH
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createNonRecursive
argument_list|(
name|testFile
argument_list|,
literal|true
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

