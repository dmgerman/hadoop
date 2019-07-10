begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|protocol
operator|.
name|StorageType
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ExcludeList
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
name|hdfs
operator|.
name|DFSUtil
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|ObjectStoreHandler
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
name|ozone
operator|.
name|MiniOzoneCluster
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyArgs
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyInfo
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyLocationInfo
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyLocationInfoGroup
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OpenKeySession
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
name|ozone
operator|.
name|web
operator|.
name|handlers
operator|.
name|BucketArgs
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
name|ozone
operator|.
name|web
operator|.
name|handlers
operator|.
name|KeyArgs
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
name|ozone
operator|.
name|web
operator|.
name|handlers
operator|.
name|UserArgs
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
name|ozone
operator|.
name|web
operator|.
name|handlers
operator|.
name|VolumeArgs
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
name|ozone
operator|.
name|web
operator|.
name|interfaces
operator|.
name|StorageHandler
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
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|LinkedList
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * This class tests the versioning of blocks from OM side.  */
end_comment

begin_class
DECL|class|TestOmBlockVersioning
specifier|public
class|class
name|TestOmBlockVersioning
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|userArgs
specifier|private
specifier|static
name|UserArgs
name|userArgs
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|ozoneManager
specifier|private
specifier|static
name|OzoneManager
name|ozoneManager
decl_stmt|;
DECL|field|storageHandler
specifier|private
specifier|static
name|StorageHandler
name|storageHandler
decl_stmt|;
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true    *    * @throws IOException    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|storageHandler
operator|=
operator|new
name|ObjectStoreHandler
argument_list|(
name|conf
argument_list|)
operator|.
name|getStorageHandler
argument_list|()
expr_stmt|;
name|userArgs
operator|=
operator|new
name|UserArgs
argument_list|(
literal|null
argument_list|,
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ozoneManager
operator|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllocateCommit ()
specifier|public
name|void
name|testAllocateCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|String
name|userName
init|=
name|ugi
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
name|adminName
init|=
name|ugi
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|bucketName
init|=
literal|"bucket"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|keyName
init|=
literal|"key"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|VolumeArgs
name|createVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|createVolumeArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|createVolumeArgs
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createVolume
argument_list|(
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|BucketArgs
name|bucketArgs
init|=
operator|new
name|BucketArgs
argument_list|(
name|bucketName
argument_list|,
name|createVolumeArgs
argument_list|)
decl_stmt|;
name|bucketArgs
operator|.
name|setAddAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setRemoveAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createBucket
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|OmKeyArgs
name|keyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|1000
argument_list|)
operator|.
name|setRefreshPipeline
argument_list|(
literal|true
argument_list|)
operator|.
name|setAcls
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// 1st update, version 0
name|OpenKeySession
name|openKey
init|=
name|ozoneManager
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
decl_stmt|;
comment|// explicitly set the keyLocation list before committing the key.
name|keyArgs
operator|.
name|setLocationInfoList
argument_list|(
name|openKey
operator|.
name|getKeyInfo
argument_list|()
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getBlocksLatestVersionOnly
argument_list|()
argument_list|)
expr_stmt|;
name|ozoneManager
operator|.
name|commitKey
argument_list|(
name|keyArgs
argument_list|,
name|openKey
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|OmKeyInfo
name|keyInfo
init|=
name|ozoneManager
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
decl_stmt|;
name|OmKeyLocationInfoGroup
name|highestVersion
init|=
name|checkVersions
argument_list|(
name|keyInfo
operator|.
name|getKeyLocationVersions
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|highestVersion
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|highestVersion
operator|.
name|getLocationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2nd update, version 1
name|openKey
operator|=
name|ozoneManager
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
expr_stmt|;
comment|//OmKeyLocationInfo locationInfo =
comment|//    ozoneManager.allocateBlock(keyArgs, openKey.getId());
comment|// explicitly set the keyLocation list before committing the key.
name|keyArgs
operator|.
name|setLocationInfoList
argument_list|(
name|openKey
operator|.
name|getKeyInfo
argument_list|()
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getBlocksLatestVersionOnly
argument_list|()
argument_list|)
expr_stmt|;
name|ozoneManager
operator|.
name|commitKey
argument_list|(
name|keyArgs
argument_list|,
name|openKey
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|keyInfo
operator|=
name|ozoneManager
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
expr_stmt|;
name|highestVersion
operator|=
name|checkVersions
argument_list|(
name|keyInfo
operator|.
name|getKeyLocationVersions
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|highestVersion
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|highestVersion
operator|.
name|getLocationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 3rd update, version 2
name|openKey
operator|=
name|ozoneManager
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
expr_stmt|;
comment|// this block will be appended to the latest version of version 2.
name|OmKeyLocationInfo
name|locationInfo
init|=
name|ozoneManager
operator|.
name|allocateBlock
argument_list|(
name|keyArgs
argument_list|,
name|openKey
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfoList
init|=
name|openKey
operator|.
name|getKeyInfo
argument_list|()
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getBlocksLatestVersionOnly
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|locationInfoList
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|locationInfoList
operator|.
name|add
argument_list|(
name|locationInfo
argument_list|)
expr_stmt|;
name|keyArgs
operator|.
name|setLocationInfoList
argument_list|(
name|locationInfoList
argument_list|)
expr_stmt|;
name|ozoneManager
operator|.
name|commitKey
argument_list|(
name|keyArgs
argument_list|,
name|openKey
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|keyInfo
operator|=
name|ozoneManager
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
expr_stmt|;
name|highestVersion
operator|=
name|checkVersions
argument_list|(
name|keyInfo
operator|.
name|getKeyLocationVersions
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|highestVersion
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|highestVersion
operator|.
name|getLocationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkVersions ( List<OmKeyLocationInfoGroup> versions)
specifier|private
name|OmKeyLocationInfoGroup
name|checkVersions
parameter_list|(
name|List
argument_list|<
name|OmKeyLocationInfoGroup
argument_list|>
name|versions
parameter_list|)
block|{
name|OmKeyLocationInfoGroup
name|currentVersion
init|=
literal|null
decl_stmt|;
for|for
control|(
name|OmKeyLocationInfoGroup
name|version
range|:
name|versions
control|)
block|{
if|if
condition|(
name|currentVersion
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|currentVersion
operator|.
name|getVersion
argument_list|()
operator|+
literal|1
argument_list|,
name|version
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|OmKeyLocationInfo
name|info
range|:
name|currentVersion
operator|.
name|getLocationList
argument_list|()
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
comment|// all the blocks from the previous version must present in the next
comment|// version
for|for
control|(
name|OmKeyLocationInfo
name|info2
range|:
name|version
operator|.
name|getLocationList
argument_list|()
control|)
block|{
if|if
condition|(
name|info
operator|.
name|getLocalID
argument_list|()
operator|==
name|info2
operator|.
name|getLocalID
argument_list|()
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
block|}
name|currentVersion
operator|=
name|version
expr_stmt|;
block|}
return|return
name|currentVersion
return|;
block|}
annotation|@
name|Test
DECL|method|testReadLatestVersion ()
specifier|public
name|void
name|testReadLatestVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|adminName
init|=
literal|"admin"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|bucketName
init|=
literal|"bucket"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|keyName
init|=
literal|"key"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|VolumeArgs
name|createVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|createVolumeArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|createVolumeArgs
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createVolume
argument_list|(
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|BucketArgs
name|bucketArgs
init|=
operator|new
name|BucketArgs
argument_list|(
name|bucketName
argument_list|,
name|createVolumeArgs
argument_list|)
decl_stmt|;
name|bucketArgs
operator|.
name|setAddAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setRemoveAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createBucket
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|OmKeyArgs
name|omKeyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|1000
argument_list|)
operator|.
name|setRefreshPipeline
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|dataString
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|KeyArgs
name|keyArgs
init|=
operator|new
name|KeyArgs
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|keyArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
comment|// this write will create 1st version with one block
try|try
init|(
name|OutputStream
name|stream
init|=
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|dataString
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|dataString
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
try|try
init|(
name|InputStream
name|in
init|=
name|storageHandler
operator|.
name|newKeyReader
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|in
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|OmKeyInfo
name|keyInfo
init|=
name|ozoneManager
operator|.
name|lookupKey
argument_list|(
name|omKeyArgs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|dataString
argument_list|,
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|keyInfo
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|keyInfo
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getLocationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// this write will create 2nd version, 2nd version will contain block from
comment|// version 1, and add a new block
name|dataString
operator|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|dataString
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
try|try
init|(
name|OutputStream
name|stream
init|=
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|dataString
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|InputStream
name|in
init|=
name|storageHandler
operator|.
name|newKeyReader
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|in
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|keyInfo
operator|=
name|ozoneManager
operator|.
name|lookupKey
argument_list|(
name|omKeyArgs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dataString
argument_list|,
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|keyInfo
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|keyInfo
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getLocationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dataString
operator|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|dataString
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
try|try
init|(
name|OutputStream
name|stream
init|=
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|dataString
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|InputStream
name|in
init|=
name|storageHandler
operator|.
name|newKeyReader
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|in
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|keyInfo
operator|=
name|ozoneManager
operator|.
name|lookupKey
argument_list|(
name|omKeyArgs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dataString
argument_list|,
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|keyInfo
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|keyInfo
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getLocationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

