begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|File
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
name|UUID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|StorageUnit
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
name|HddsConfigKeys
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
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationFactor
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
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationType
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
name|TestUtils
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
name|MockNodeManager
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
name|hdds
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
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
name|node
operator|.
name|NodeManager
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
name|protocol
operator|.
name|ScmBlockLocationProtocol
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
name|server
operator|.
name|SCMConfigurator
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
name|server
operator|.
name|StorageContainerManager
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
name|exceptions
operator|.
name|OMException
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
name|*
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
name|test
operator|.
name|GenericTestUtils
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
name|test
operator|.
name|LambdaTestUtils
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
name|BeforeClass
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test class for @{@link KeyManagerImpl}.  */
end_comment

begin_class
DECL|class|TestKeyManagerImpl
specifier|public
class|class
name|TestKeyManagerImpl
block|{
DECL|field|keyManager
specifier|private
specifier|static
name|KeyManagerImpl
name|keyManager
decl_stmt|;
DECL|field|volumeManager
specifier|private
specifier|static
name|VolumeManagerImpl
name|volumeManager
decl_stmt|;
DECL|field|bucketManager
specifier|private
specifier|static
name|BucketManagerImpl
name|bucketManager
decl_stmt|;
DECL|field|scm
specifier|private
specifier|static
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|mockScmBlockLocationProtocol
specifier|private
specifier|static
name|ScmBlockLocationProtocol
name|mockScmBlockLocationProtocol
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|metadataManager
specifier|private
specifier|static
name|OMMetadataManager
name|metadataManager
decl_stmt|;
DECL|field|dir
specifier|private
specifier|static
name|File
name|dir
decl_stmt|;
DECL|field|scmBlockSize
specifier|private
specifier|static
name|long
name|scmBlockSize
decl_stmt|;
DECL|field|KEY_NAME
specifier|private
specifier|static
specifier|final
name|String
name|KEY_NAME
init|=
literal|"key1"
decl_stmt|;
DECL|field|BUCKET_NAME
specifier|private
specifier|static
specifier|final
name|String
name|BUCKET_NAME
init|=
literal|"bucket1"
decl_stmt|;
DECL|field|VOLUME_NAME
specifier|private
specifier|static
specifier|final
name|String
name|VOLUME_NAME
init|=
literal|"vol1"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
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
name|dir
operator|=
name|GenericTestUtils
operator|.
name|getRandomizedTestDir
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|mockScmBlockLocationProtocol
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ScmBlockLocationProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|metadataManager
operator|=
operator|new
name|OmMetadataManagerImpl
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|volumeManager
operator|=
operator|new
name|VolumeManagerImpl
argument_list|(
name|metadataManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|bucketManager
operator|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metadataManager
argument_list|)
expr_stmt|;
name|NodeManager
name|nodeManager
init|=
operator|new
name|MockNodeManager
argument_list|(
literal|true
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|SCMConfigurator
name|configurator
init|=
operator|new
name|SCMConfigurator
argument_list|()
decl_stmt|;
name|configurator
operator|.
name|setScmNodeManager
argument_list|(
name|nodeManager
argument_list|)
expr_stmt|;
name|scm
operator|=
name|TestUtils
operator|.
name|getScm
argument_list|(
name|conf
argument_list|,
name|configurator
argument_list|)
expr_stmt|;
name|scm
operator|.
name|start
argument_list|()
expr_stmt|;
name|scm
operator|.
name|exitChillMode
argument_list|()
expr_stmt|;
name|scmBlockSize
operator|=
operator|(
name|long
operator|)
name|conf
operator|.
name|getStorageSize
argument_list|(
name|OZONE_SCM_BLOCK_SIZE
argument_list|,
name|OZONE_SCM_BLOCK_SIZE_DEFAULT
argument_list|,
name|StorageUnit
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|OZONE_KEY_PREALLOCATION_BLOCKS_MAX
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|keyManager
operator|=
operator|new
name|KeyManagerImpl
argument_list|(
name|scm
operator|.
name|getBlockProtocolServer
argument_list|()
argument_list|,
name|metadataManager
argument_list|,
name|conf
argument_list|,
literal|"om1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockScmBlockLocationProtocol
operator|.
name|allocateBlock
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|ReplicationType
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|ReplicationFactor
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|ExcludeList
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|SCMException
argument_list|(
literal|"ChillModePrecheck failed for allocateBlock"
argument_list|,
name|ResultCodes
operator|.
name|CHILL_MODE_EXCEPTION
argument_list|)
argument_list|)
expr_stmt|;
name|createVolume
argument_list|(
name|VOLUME_NAME
argument_list|)
expr_stmt|;
name|createBucket
argument_list|(
name|VOLUME_NAME
argument_list|,
name|BUCKET_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|scm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|scm
operator|.
name|join
argument_list|()
expr_stmt|;
name|metadataManager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|keyManager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|createBucket (String volumeName, String bucketName)
specifier|private
specifier|static
name|void
name|createBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
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
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|createVolume (String volumeName)
specifier|private
specifier|static
name|void
name|createVolume
parameter_list|(
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
block|{
name|OmVolumeArgs
name|volumeArgs
init|=
name|OmVolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setAdminName
argument_list|(
literal|"bilbo"
argument_list|)
operator|.
name|setOwnerName
argument_list|(
literal|"bilbo"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|volumeManager
operator|.
name|createVolume
argument_list|(
name|volumeArgs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|allocateBlockFailureInChillMode ()
specifier|public
name|void
name|allocateBlockFailureInChillMode
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyManager
name|keyManager1
init|=
operator|new
name|KeyManagerImpl
argument_list|(
name|mockScmBlockLocationProtocol
argument_list|,
name|metadataManager
argument_list|,
name|conf
argument_list|,
literal|"om1"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|OmKeyArgs
name|keyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setKeyName
argument_list|(
name|KEY_NAME
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|BUCKET_NAME
argument_list|)
operator|.
name|setFactor
argument_list|(
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
operator|.
name|setType
argument_list|(
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|VOLUME_NAME
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OpenKeySession
name|keySession
init|=
name|keyManager1
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
decl_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|OMException
operator|.
name|class
argument_list|,
literal|"ChillModePrecheck failed for allocateBlock"
argument_list|,
parameter_list|()
lambda|->
block|{
name|keyManager1
operator|.
name|allocateBlock
argument_list|(
name|keyArgs
argument_list|,
name|keySession
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|openKeyFailureInChillMode ()
specifier|public
name|void
name|openKeyFailureInChillMode
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyManager
name|keyManager1
init|=
operator|new
name|KeyManagerImpl
argument_list|(
name|mockScmBlockLocationProtocol
argument_list|,
name|metadataManager
argument_list|,
name|conf
argument_list|,
literal|"om1"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|OmKeyArgs
name|keyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setKeyName
argument_list|(
name|KEY_NAME
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|BUCKET_NAME
argument_list|)
operator|.
name|setFactor
argument_list|(
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|1000
argument_list|)
operator|.
name|setType
argument_list|(
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|VOLUME_NAME
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|OMException
operator|.
name|class
argument_list|,
literal|"ChillModePrecheck failed for allocateBlock"
argument_list|,
parameter_list|()
lambda|->
block|{
name|keyManager1
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|openKeyWithMultipleBlocks ()
specifier|public
name|void
name|openKeyWithMultipleBlocks
parameter_list|()
throws|throws
name|IOException
block|{
name|OmKeyArgs
name|keyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setKeyName
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|BUCKET_NAME
argument_list|)
operator|.
name|setFactor
argument_list|(
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
operator|.
name|setDataSize
argument_list|(
name|scmBlockSize
operator|*
literal|10
argument_list|)
operator|.
name|setType
argument_list|(
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|VOLUME_NAME
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OpenKeySession
name|keySession
init|=
name|keyManager
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
decl_stmt|;
name|OmKeyInfo
name|keyInfo
init|=
name|keySession
operator|.
name|getKeyInfo
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
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

