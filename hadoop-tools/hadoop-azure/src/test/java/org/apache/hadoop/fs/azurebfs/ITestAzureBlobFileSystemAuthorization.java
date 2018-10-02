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
name|Arrays
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
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
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
name|azurebfs
operator|.
name|extensions
operator|.
name|AbfsAuthorizationException
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
name|AclEntry
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|extensions
operator|.
name|MockAbfsAuthorizer
operator|.
name|*
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
name|azurebfs
operator|.
name|utils
operator|.
name|AclTestHelpers
operator|.
name|aclEntry
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
name|permission
operator|.
name|AclEntryScope
operator|.
name|ACCESS
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
name|permission
operator|.
name|AclEntryType
operator|.
name|GROUP
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * Test Perform Authorization Check operation  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemAuthorization
specifier|public
class|class
name|ITestAzureBlobFileSystemAuthorization
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|field|TEST_READ_ONLY_FILE_PATH_0
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_READ_ONLY_FILE_PATH_0
init|=
operator|new
name|Path
argument_list|(
name|TEST_READ_ONLY_FILE_0
argument_list|)
decl_stmt|;
DECL|field|TEST_READ_ONLY_FOLDER_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_READ_ONLY_FOLDER_PATH
init|=
operator|new
name|Path
argument_list|(
name|TEST_READ_ONLY_FOLDER
argument_list|)
decl_stmt|;
DECL|field|TEST_WRITE_ONLY_FILE_PATH_0
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_WRITE_ONLY_FILE_PATH_0
init|=
operator|new
name|Path
argument_list|(
name|TEST_WRITE_ONLY_FILE_0
argument_list|)
decl_stmt|;
DECL|field|TEST_WRITE_ONLY_FILE_PATH_1
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_WRITE_ONLY_FILE_PATH_1
init|=
operator|new
name|Path
argument_list|(
name|TEST_WRITE_ONLY_FILE_1
argument_list|)
decl_stmt|;
DECL|field|TEST_READ_WRITE_FILE_PATH_0
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_READ_WRITE_FILE_PATH_0
init|=
operator|new
name|Path
argument_list|(
name|TEST_READ_WRITE_FILE_0
argument_list|)
decl_stmt|;
DECL|field|TEST_READ_WRITE_FILE_PATH_1
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_READ_WRITE_FILE_PATH_1
init|=
operator|new
name|Path
argument_list|(
name|TEST_READ_WRITE_FILE_1
argument_list|)
decl_stmt|;
DECL|field|TEST_WRITE_ONLY_FOLDER_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_WRITE_ONLY_FOLDER_PATH
init|=
operator|new
name|Path
argument_list|(
name|TEST_WRITE_ONLY_FOLDER
argument_list|)
decl_stmt|;
DECL|field|TEST_WRITE_THEN_READ_ONLY_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_WRITE_THEN_READ_ONLY_PATH
init|=
operator|new
name|Path
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY
argument_list|)
decl_stmt|;
DECL|field|TEST_AUTHZ_CLASS
specifier|private
specifier|static
specifier|final
name|String
name|TEST_AUTHZ_CLASS
init|=
literal|"org.apache.hadoop.fs.azurebfs.extensions.MockAbfsAuthorizer"
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemAuthorization ()
specifier|public
name|ITestAzureBlobFileSystemAuthorization
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|ABFS_EXTERNAL_AUTHORIZATION_CLASS
argument_list|,
name|TEST_AUTHZ_CLASS
argument_list|)
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenFileWithInvalidPath ()
specifier|public
name|void
name|testOpenFileWithInvalidPath
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
name|intercept
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenFileAuthorized ()
specifier|public
name|void
name|testOpenFileAuthorized
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
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|open
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenFileUnauthorized ()
specifier|public
name|void
name|testOpenFileUnauthorized
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
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|open
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateFileAuthorized ()
specifier|public
name|void
name|testCreateFileAuthorized
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
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateFileUnauthorized ()
specifier|public
name|void
name|testCreateFileUnauthorized
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
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|create
argument_list|(
name|TEST_READ_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppendFileAuthorized ()
specifier|public
name|void
name|testAppendFileAuthorized
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
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|append
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppendFileUnauthorized ()
specifier|public
name|void
name|testAppendFileUnauthorized
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
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|append
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameAuthorized ()
specifier|public
name|void
name|testRenameAuthorized
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
name|rename
argument_list|(
name|TEST_READ_WRITE_FILE_PATH_0
argument_list|,
name|TEST_READ_WRITE_FILE_PATH_1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameUnauthorized ()
specifier|public
name|void
name|testRenameUnauthorized
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
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|rename
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|,
name|TEST_WRITE_ONLY_FILE_PATH_1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteFileAuthorized ()
specifier|public
name|void
name|testDeleteFileAuthorized
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
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteFileUnauthorized ()
specifier|public
name|void
name|testDeleteFileUnauthorized
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
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|delete
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListStatusAuthorized ()
specifier|public
name|void
name|testListStatusAuthorized
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
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|listStatus
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListStatusUnauthorized ()
specifier|public
name|void
name|testListStatusUnauthorized
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
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|listStatus
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkDirsAuthorized ()
specifier|public
name|void
name|testMkDirsAuthorized
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
name|TEST_WRITE_ONLY_FOLDER_PATH
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkDirsUnauthorized ()
specifier|public
name|void
name|testMkDirsUnauthorized
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
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|TEST_READ_ONLY_FOLDER_PATH
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFileStatusAuthorized ()
specifier|public
name|void
name|testGetFileStatusAuthorized
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
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|getFileStatus
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFileStatusUnauthorized ()
specifier|public
name|void
name|testGetFileStatusUnauthorized
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
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|getFileStatus
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetOwnerAuthorized ()
specifier|public
name|void
name|testSetOwnerAuthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|,
literal|"testUser"
argument_list|,
literal|"testGroup"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetOwnerUnauthorized ()
specifier|public
name|void
name|testSetOwnerUnauthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|setOwner
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|,
literal|"testUser"
argument_list|,
literal|"testGroup"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetPermissionAuthorized ()
specifier|public
name|void
name|testSetPermissionAuthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetPermissionUnauthorized ()
specifier|public
name|void
name|testSetPermissionUnauthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|setPermission
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testModifyAclEntriesAuthorized ()
specifier|public
name|void
name|testModifyAclEntriesAuthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
literal|"bar"
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|modifyAclEntries
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testModifyAclEntriesUnauthorized ()
specifier|public
name|void
name|testModifyAclEntriesUnauthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
literal|"bar"
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|modifyAclEntries
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveAclEntriesAuthorized ()
specifier|public
name|void
name|testRemoveAclEntriesAuthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
literal|"bar"
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|removeAclEntries
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveAclEntriesUnauthorized ()
specifier|public
name|void
name|testRemoveAclEntriesUnauthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
literal|"bar"
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|removeAclEntries
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveDefaultAclAuthorized ()
specifier|public
name|void
name|testRemoveDefaultAclAuthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|removeDefaultAcl
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveDefaultAclUnauthorized ()
specifier|public
name|void
name|testRemoveDefaultAclUnauthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|removeDefaultAcl
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveAclAuthorized ()
specifier|public
name|void
name|testRemoveAclAuthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|removeAcl
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveAclUnauthorized ()
specifier|public
name|void
name|testRemoveAclUnauthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|removeAcl
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetAclAuthorized ()
specifier|public
name|void
name|testSetAclAuthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
literal|"bar"
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setAcl
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetAclUnauthorized ()
specifier|public
name|void
name|testSetAclUnauthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
literal|"bar"
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|setAcl
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetAclStatusAuthorized ()
specifier|public
name|void
name|testGetAclStatusAuthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
literal|"bar"
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|getAclStatus
argument_list|(
name|TEST_WRITE_THEN_READ_ONLY_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetAclStatusUnauthorized ()
specifier|public
name|void
name|testGetAclStatusUnauthorized
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
name|assumeTrue
argument_list|(
literal|"This test case only runs when namespace is enabled"
argument_list|,
name|fs
operator|.
name|getIsNamespaceEnabeld
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
literal|"bar"
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|AbfsAuthorizationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|fs
operator|.
name|getAclStatus
argument_list|(
name|TEST_WRITE_ONLY_FILE_PATH_0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

