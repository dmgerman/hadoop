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
name|assertEquals
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
name|assertNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|net
operator|.
name|URI
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
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
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
comment|/**  * Tests that we put the correct metadata on blobs created through WASB.  */
end_comment

begin_class
DECL|class|TestBlobMetadata
specifier|public
class|class
name|TestBlobMetadata
block|{
DECL|field|testAccount
specifier|private
name|AzureBlobStorageTestAccount
name|testAccount
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|backingStore
specifier|private
name|InMemoryBlockBlobStore
name|backingStore
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
name|backingStore
operator|=
name|testAccount
operator|.
name|getMockStorage
argument_list|()
operator|.
name|getBackingStore
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
name|fs
operator|=
literal|null
expr_stmt|;
name|backingStore
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getExpectedOwner ()
specifier|private
specifier|static
name|String
name|getExpectedOwner
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
return|;
block|}
DECL|method|getExpectedPermissionString (String permissionString)
specifier|private
specifier|static
name|String
name|getExpectedPermissionString
parameter_list|(
name|String
name|permissionString
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"{\"owner\":\"%s\",\"group\":\"%s\",\"permissions\":\"%s\"}"
argument_list|,
name|getExpectedOwner
argument_list|()
argument_list|,
name|NativeAzureFileSystem
operator|.
name|AZURE_DEFAULT_GROUP_DEFAULT
argument_list|,
name|permissionString
argument_list|)
return|;
block|}
comment|/**    * Tests that WASB stamped the version in the container metadata.    */
annotation|@
name|Test
DECL|method|testContainerVersionMetadata ()
specifier|public
name|void
name|testContainerVersionMetadata
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Do a write operation to trigger version stamp
name|fs
operator|.
name|createNewFile
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|containerMetadata
init|=
name|backingStore
operator|.
name|getContainerMetadata
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|containerMetadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|CURRENT_WASB_VERSION
argument_list|,
name|containerMetadata
operator|.
name|get
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|VERSION_METADATA_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|FsWithPreExistingContainer
specifier|private
specifier|static
specifier|final
class|class
name|FsWithPreExistingContainer
implements|implements
name|Closeable
block|{
DECL|field|mockStorage
specifier|private
specifier|final
name|MockStorageInterface
name|mockStorage
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|NativeAzureFileSystem
name|fs
decl_stmt|;
DECL|method|FsWithPreExistingContainer (MockStorageInterface mockStorage, NativeAzureFileSystem fs)
specifier|private
name|FsWithPreExistingContainer
parameter_list|(
name|MockStorageInterface
name|mockStorage
parameter_list|,
name|NativeAzureFileSystem
name|fs
parameter_list|)
block|{
name|this
operator|.
name|mockStorage
operator|=
name|mockStorage
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
block|}
DECL|method|getFs ()
specifier|public
name|NativeAzureFileSystem
name|getFs
parameter_list|()
block|{
return|return
name|fs
return|;
block|}
DECL|method|getContainerMetadata ()
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getContainerMetadata
parameter_list|()
block|{
return|return
name|mockStorage
operator|.
name|getBackingStore
argument_list|()
operator|.
name|getContainerMetadata
argument_list|()
return|;
block|}
DECL|method|create ()
specifier|public
specifier|static
name|FsWithPreExistingContainer
name|create
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|create
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|create ( HashMap<String, String> containerMetadata)
specifier|public
specifier|static
name|FsWithPreExistingContainer
name|create
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|containerMetadata
parameter_list|)
throws|throws
name|Exception
block|{
name|AzureNativeFileSystemStore
name|store
init|=
operator|new
name|AzureNativeFileSystemStore
argument_list|()
decl_stmt|;
name|MockStorageInterface
name|mockStorage
init|=
operator|new
name|MockStorageInterface
argument_list|()
decl_stmt|;
name|store
operator|.
name|setAzureStorageInteractionLayer
argument_list|(
name|mockStorage
argument_list|)
expr_stmt|;
name|NativeAzureFileSystem
name|fs
init|=
operator|new
name|NativeAzureFileSystem
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|AzureBlobStorageTestAccount
operator|.
name|setMockAccountKey
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mockStorage
operator|.
name|addPreExistingContainer
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|getMockContainerUri
argument_list|()
argument_list|,
name|containerMetadata
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
operator|new
name|URI
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|MOCK_WASB_URI
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
operator|new
name|FsWithPreExistingContainer
argument_list|(
name|mockStorage
argument_list|,
name|fs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests that WASB stamped the version in the container metadata if it does a    * write operation to a pre-existing container.    */
annotation|@
name|Test
DECL|method|testPreExistingContainerVersionMetadata ()
specifier|public
name|void
name|testPreExistingContainerVersionMetadata
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a mock storage with a pre-existing container that has no
comment|// WASB version metadata on it.
name|FsWithPreExistingContainer
name|fsWithContainer
init|=
name|FsWithPreExistingContainer
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// Now, do some read operations (should touch the metadata)
name|assertFalse
argument_list|(
name|fsWithContainer
operator|.
name|getFs
argument_list|()
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/IDontExist"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fsWithContainer
operator|.
name|getFs
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Check that no container metadata exists yet
name|assertNull
argument_list|(
name|fsWithContainer
operator|.
name|getContainerMetadata
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now do a write operation - should stamp the version
name|fsWithContainer
operator|.
name|getFs
argument_list|()
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dir"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check that now we have the version stamp
name|assertNotNull
argument_list|(
name|fsWithContainer
operator|.
name|getContainerMetadata
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|CURRENT_WASB_VERSION
argument_list|,
name|fsWithContainer
operator|.
name|getContainerMetadata
argument_list|()
operator|.
name|get
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|VERSION_METADATA_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|fsWithContainer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests that WASB works well with an older version container with ASV-era    * version and metadata.    */
annotation|@
name|Test
DECL|method|testFirstContainerVersionMetadata ()
specifier|public
name|void
name|testFirstContainerVersionMetadata
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a mock storage with a pre-existing container that has
comment|// ASV version metadata on it.
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|containerMetadata
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|containerMetadata
operator|.
name|put
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|OLD_VERSION_METADATA_KEY
argument_list|,
name|AzureNativeFileSystemStore
operator|.
name|FIRST_WASB_VERSION
argument_list|)
expr_stmt|;
name|FsWithPreExistingContainer
name|fsWithContainer
init|=
name|FsWithPreExistingContainer
operator|.
name|create
argument_list|(
name|containerMetadata
argument_list|)
decl_stmt|;
comment|// Now, do some read operations (should touch the metadata)
name|assertFalse
argument_list|(
name|fsWithContainer
operator|.
name|getFs
argument_list|()
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/IDontExist"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fsWithContainer
operator|.
name|getFs
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Check that no container metadata exists yet
name|assertEquals
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|FIRST_WASB_VERSION
argument_list|,
name|fsWithContainer
operator|.
name|getContainerMetadata
argument_list|()
operator|.
name|get
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|OLD_VERSION_METADATA_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fsWithContainer
operator|.
name|getContainerMetadata
argument_list|()
operator|.
name|get
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|VERSION_METADATA_KEY
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now do a write operation - should stamp the version
name|fsWithContainer
operator|.
name|getFs
argument_list|()
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dir"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check that now we have the version stamp
name|assertEquals
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|CURRENT_WASB_VERSION
argument_list|,
name|fsWithContainer
operator|.
name|getContainerMetadata
argument_list|()
operator|.
name|get
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|VERSION_METADATA_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fsWithContainer
operator|.
name|getContainerMetadata
argument_list|()
operator|.
name|get
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|OLD_VERSION_METADATA_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|fsWithContainer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
DECL|method|testPermissionMetadata ()
specifier|public
name|void
name|testPermissionMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|FsPermission
name|justMe
init|=
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|READ_WRITE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|Path
name|selfishFile
init|=
operator|new
name|Path
argument_list|(
literal|"/noOneElse"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|selfishFile
argument_list|,
name|justMe
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|,
name|fs
operator|.
name|getDefaultReplication
argument_list|()
argument_list|,
name|fs
operator|.
name|getDefaultBlockSize
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
name|backingStore
operator|.
name|getMetadata
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|toMockUri
argument_list|(
name|selfishFile
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|String
name|storedPermission
init|=
name|metadata
operator|.
name|get
argument_list|(
literal|"hdi_permission"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getExpectedPermissionString
argument_list|(
literal|"rw-------"
argument_list|)
argument_list|,
name|storedPermission
argument_list|)
expr_stmt|;
name|FileStatus
name|retrievedStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|selfishFile
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|retrievedStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|justMe
argument_list|,
name|retrievedStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getExpectedOwner
argument_list|()
argument_list|,
name|retrievedStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NativeAzureFileSystem
operator|.
name|AZURE_DEFAULT_GROUP_DEFAULT
argument_list|,
name|retrievedStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that WASB understands the old-style ASV metadata and changes it when    * it gets the chance.    */
annotation|@
name|Test
DECL|method|testOldPermissionMetadata ()
specifier|public
name|void
name|testOldPermissionMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|selfishFile
init|=
operator|new
name|Path
argument_list|(
literal|"/noOneElse"
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
literal|"asv_permission"
argument_list|,
name|getExpectedPermissionString
argument_list|(
literal|"rw-------"
argument_list|)
argument_list|)
expr_stmt|;
name|backingStore
operator|.
name|setContent
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|toMockUri
argument_list|(
name|selfishFile
argument_list|)
argument_list|,
operator|new
name|byte
index|[]
block|{}
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|FsPermission
name|justMe
init|=
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|READ_WRITE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|FileStatus
name|retrievedStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|selfishFile
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|retrievedStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|justMe
argument_list|,
name|retrievedStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getExpectedOwner
argument_list|()
argument_list|,
name|retrievedStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NativeAzureFileSystem
operator|.
name|AZURE_DEFAULT_GROUP_DEFAULT
argument_list|,
name|retrievedStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|FsPermission
name|meAndYou
init|=
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|READ_WRITE
argument_list|,
name|FsAction
operator|.
name|READ_WRITE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|selfishFile
argument_list|,
name|meAndYou
argument_list|)
expr_stmt|;
name|metadata
operator|=
name|backingStore
operator|.
name|getMetadata
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|toMockUri
argument_list|(
name|selfishFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|String
name|storedPermission
init|=
name|metadata
operator|.
name|get
argument_list|(
literal|"hdi_permission"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getExpectedPermissionString
argument_list|(
literal|"rw-rw----"
argument_list|)
argument_list|,
name|storedPermission
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"asv_permission"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFolderMetadata ()
specifier|public
name|void
name|testFolderMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|folder
init|=
operator|new
name|Path
argument_list|(
literal|"/folder"
argument_list|)
decl_stmt|;
name|FsPermission
name|justRead
init|=
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|READ
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|folder
argument_list|,
name|justRead
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
name|backingStore
operator|.
name|getMetadata
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|toMockUri
argument_list|(
name|folder
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"hdi_isfolder"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getExpectedPermissionString
argument_list|(
literal|"r--r--r--"
argument_list|)
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"hdi_permission"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

