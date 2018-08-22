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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|CommonConfigurationKeys
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
name|services
operator|.
name|AuthType
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
name|Assume
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
name|runner
operator|.
name|RunWith
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
name|Parameterized
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
name|Parallelized
import|;
end_import

begin_comment
comment|/**  * Test permission operations.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parallelized
operator|.
name|class
argument_list|)
DECL|class|ITestAzureBlobFileSystemPermission
specifier|public
class|class
name|ITestAzureBlobFileSystemPermission
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|field|testRoot
specifier|private
specifier|static
name|Path
name|testRoot
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_UMASK_VALUE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_UMASK_VALUE
init|=
literal|"027"
decl_stmt|;
DECL|field|DEFAULT_UMASK_PERMISSION
specifier|private
specifier|static
specifier|final
name|FsPermission
name|DEFAULT_UMASK_PERMISSION
init|=
operator|new
name|FsPermission
argument_list|(
name|DEFAULT_UMASK_VALUE
argument_list|)
decl_stmt|;
DECL|field|KILOBYTE
specifier|private
specifier|static
specifier|final
name|int
name|KILOBYTE
init|=
literal|1024
decl_stmt|;
DECL|field|permission
specifier|private
name|FsPermission
name|permission
decl_stmt|;
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemPermission (FsPermission testPermission)
specifier|public
name|ITestAzureBlobFileSystemPermission
parameter_list|(
name|FsPermission
name|testPermission
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
name|permission
operator|=
name|testPermission
expr_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|this
operator|.
name|getAuthType
argument_list|()
operator|==
name|AuthType
operator|.
name|OAuth
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
DECL|method|abfsCreateNonRecursiveTestData ()
specifier|public
specifier|static
name|Collection
name|abfsCreateNonRecursiveTestData
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*       Test Data       File/Folder name, User permission, Group permission, Other Permission,       Parent already exist       shouldCreateSucceed, expectedExceptionIfFileCreateFails     */
specifier|final
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|datas
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FsAction
name|g
range|:
name|FsAction
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|FsAction
name|o
range|:
name|FsAction
operator|.
name|values
argument_list|()
control|)
block|{
name|datas
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|g
argument_list|,
name|o
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|datas
return|;
block|}
annotation|@
name|Test
DECL|method|testFilePermission ()
specifier|public
name|void
name|testFilePermission
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
name|getConf
argument_list|()
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_KEY
argument_list|,
name|DEFAULT_UMASK_VALUE
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
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
name|fs
operator|.
name|removeDefaultAcl
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|permission
argument_list|,
literal|true
argument_list|,
name|KILOBYTE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|KILOBYTE
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|permission
operator|.
name|applyUMask
argument_list|(
name|DEFAULT_UMASK_PERMISSION
argument_list|)
argument_list|,
name|status
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFolderPermission ()
specifier|public
name|void
name|testFolderPermission
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
name|getConf
argument_list|()
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_KEY
argument_list|,
literal|"027"
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
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
name|WRITE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|removeDefaultAcl
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|,
name|permission
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|permission
operator|.
name|applyUMask
argument_list|(
name|DEFAULT_UMASK_PERMISSION
argument_list|)
argument_list|,
name|status
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

