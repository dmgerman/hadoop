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
name|IOException
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

begin_comment
comment|/**  * Test FileStatus.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemFileStatus
specifier|public
class|class
name|ITestAzureBlobFileSystemFileStatus
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|field|DEFAULT_FILE_PERMISSION_VALUE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_FILE_PERMISSION_VALUE
init|=
literal|"640"
decl_stmt|;
DECL|field|DEFAULT_DIR_PERMISSION_VALUE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_DIR_PERMISSION_VALUE
init|=
literal|"750"
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
DECL|field|FULL_PERMISSION
specifier|private
specifier|static
specifier|final
name|String
name|FULL_PERMISSION
init|=
literal|"777"
decl_stmt|;
DECL|field|TEST_FILE
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_FILE
init|=
operator|new
name|Path
argument_list|(
literal|"testFile"
argument_list|)
decl_stmt|;
DECL|field|TEST_FOLDER
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_FOLDER
init|=
operator|new
name|Path
argument_list|(
literal|"testDir"
argument_list|)
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemFileStatus ()
specifier|public
name|ITestAzureBlobFileSystemFileStatus
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
DECL|method|testEnsureStatusWorksForRoot ()
specifier|public
name|void
name|testEnsureStatusWorksForRoot
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
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|rootls
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"root listing"
argument_list|,
literal|0
argument_list|,
name|rootls
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileStatusPermissionsAndOwnerAndGroup ()
specifier|public
name|void
name|testFileStatusPermissionsAndOwnerAndGroup
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
name|touch
argument_list|(
name|TEST_FILE
argument_list|)
expr_stmt|;
name|validateStatus
argument_list|(
name|fs
argument_list|,
name|TEST_FILE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|validateStatus (final AzureBlobFileSystem fs, final Path name, final boolean isDir)
specifier|private
name|FileStatus
name|validateStatus
parameter_list|(
specifier|final
name|AzureBlobFileSystem
name|fs
parameter_list|,
specifier|final
name|Path
name|name
parameter_list|,
specifier|final
name|boolean
name|isDir
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|errorInStatus
init|=
literal|"error in "
operator|+
name|fileStatus
operator|+
literal|" from "
operator|+
name|fs
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|getIsNamespaceEnabled
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|errorInStatus
operator|+
literal|": owner"
argument_list|,
name|fs
operator|.
name|getOwnerUser
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|errorInStatus
operator|+
literal|": group"
argument_list|,
name|fs
operator|.
name|getOwnerUserPrimaryGroup
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|FsPermission
argument_list|(
name|FULL_PERMISSION
argument_list|)
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// When running with namespace enabled account,
comment|// the owner and group info retrieved from server will be digit ids.
comment|// hence skip the owner and group validation
if|if
condition|(
name|isDir
condition|)
block|{
name|assertEquals
argument_list|(
name|errorInStatus
operator|+
literal|": permission"
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|DEFAULT_DIR_PERMISSION_VALUE
argument_list|)
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|errorInStatus
operator|+
literal|": permission"
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|DEFAULT_FILE_PERMISSION_VALUE
argument_list|)
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fileStatus
return|;
block|}
annotation|@
name|Test
DECL|method|testFolderStatusPermissionsAndOwnerAndGroup ()
specifier|public
name|void
name|testFolderStatusPermissionsAndOwnerAndGroup
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
name|fs
operator|.
name|mkdirs
argument_list|(
name|TEST_FOLDER
argument_list|)
expr_stmt|;
name|validateStatus
argument_list|(
name|fs
argument_list|,
name|TEST_FOLDER
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAbfsPathWithHost ()
specifier|public
name|void
name|testAbfsPathWithHost
parameter_list|()
throws|throws
name|IOException
block|{
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|pathWithHost1
init|=
operator|new
name|Path
argument_list|(
literal|"abfs://mycluster/abfs/file1.txt"
argument_list|)
decl_stmt|;
name|Path
name|pathwithouthost1
init|=
operator|new
name|Path
argument_list|(
literal|"/abfs/file1.txt"
argument_list|)
decl_stmt|;
name|Path
name|pathWithHost2
init|=
operator|new
name|Path
argument_list|(
literal|"abfs://mycluster/abfs/file2.txt"
argument_list|)
decl_stmt|;
name|Path
name|pathwithouthost2
init|=
operator|new
name|Path
argument_list|(
literal|"/abfs/file2.txt"
argument_list|)
decl_stmt|;
comment|// verify compatibility of this path format
name|fs
operator|.
name|create
argument_list|(
name|pathWithHost1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|pathwithouthost1
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|pathwithouthost2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|pathWithHost2
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify get
name|FileStatus
name|fileStatus1
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|pathWithHost1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|pathwithouthost1
operator|.
name|getName
argument_list|()
argument_list|,
name|fileStatus1
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus2
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|pathwithouthost2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|pathWithHost2
operator|.
name|getName
argument_list|()
argument_list|,
name|fileStatus2
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
DECL|method|testLastModifiedTime ()
specifier|public
name|void
name|testLastModifiedTime
parameter_list|()
throws|throws
name|IOException
block|{
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|testFilePath
init|=
operator|new
name|Path
argument_list|(
literal|"childfile1.txt"
argument_list|)
decl_stmt|;
name|long
name|createStartTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|minCreateStartTime
init|=
operator|(
name|createStartTime
operator|/
literal|1000
operator|)
operator|*
literal|1000
operator|-
literal|1
decl_stmt|;
comment|//  Dividing and multiplying by 1000 to make last 3 digits 0.
comment|//  It is observed that modification time is returned with last 3
comment|//  digits 0 always.
name|fs
operator|.
name|create
argument_list|(
name|testFilePath
argument_list|)
expr_stmt|;
name|long
name|createEndTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|FileStatus
name|fStat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|testFilePath
argument_list|)
decl_stmt|;
name|long
name|lastModifiedTime
init|=
name|fStat
operator|.
name|getModificationTime
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"lastModifiedTime should be after minCreateStartTime"
argument_list|,
name|minCreateStartTime
operator|<
name|lastModifiedTime
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"lastModifiedTime should be before createEndTime"
argument_list|,
name|createEndTime
operator|>
name|lastModifiedTime
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

