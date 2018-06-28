begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|keyvalue
package|;
end_package

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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|StorageContainerException
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerDataYaml
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
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|HddsVolume
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
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|RoundRobinVolumeChoosingPolicy
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
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|VolumeSet
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
name|container
operator|.
name|keyvalue
operator|.
name|helpers
operator|.
name|KeyValueContainerLocationUtil
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
name|util
operator|.
name|DiskChecker
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
name|rules
operator|.
name|TemporaryFolder
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
import|import static
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|Preconditions
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
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_comment
comment|/**  * Class to test KeyValue Container operations.  */
end_comment

begin_class
DECL|class|TestKeyValueContainer
specifier|public
class|class
name|TestKeyValueContainer
block|{
annotation|@
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|scmId
specifier|private
name|String
name|scmId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|volumeSet
specifier|private
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|field|volumeChoosingPolicy
specifier|private
name|RoundRobinVolumeChoosingPolicy
name|volumeChoosingPolicy
decl_stmt|;
DECL|field|containerId
specifier|private
name|long
name|containerId
init|=
literal|1L
decl_stmt|;
DECL|field|containerName
specifier|private
name|String
name|containerName
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
DECL|field|keyValueContainerData
specifier|private
name|KeyValueContainerData
name|keyValueContainerData
decl_stmt|;
DECL|field|keyValueContainer
specifier|private
name|KeyValueContainer
name|keyValueContainer
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
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|HddsVolume
name|hddsVolume
init|=
operator|new
name|HddsVolume
operator|.
name|Builder
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|conf
argument_list|(
name|conf
argument_list|)
operator|.
name|datanodeUuid
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
name|build
argument_list|()
decl_stmt|;
name|volumeSet
operator|=
name|mock
argument_list|(
name|VolumeSet
operator|.
name|class
argument_list|)
expr_stmt|;
name|volumeChoosingPolicy
operator|=
name|mock
argument_list|(
name|RoundRobinVolumeChoosingPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|volumeChoosingPolicy
operator|.
name|chooseVolume
argument_list|(
name|anyList
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|hddsVolume
argument_list|)
expr_stmt|;
name|keyValueContainerData
operator|=
operator|new
name|KeyValueContainerData
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|=
operator|new
name|KeyValueContainer
argument_list|(
name|keyValueContainerData
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateContainer ()
specifier|public
name|void
name|testCreateContainer
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create Container.
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|keyValueContainerData
operator|=
operator|(
name|KeyValueContainerData
operator|)
name|keyValueContainer
operator|.
name|getContainerData
argument_list|()
expr_stmt|;
name|String
name|containerMetaDataPath
init|=
name|keyValueContainerData
operator|.
name|getMetadataPath
argument_list|()
decl_stmt|;
name|String
name|chunksPath
init|=
name|keyValueContainerData
operator|.
name|getChunksPath
argument_list|()
decl_stmt|;
comment|// Check whether containerMetaDataPath and chunksPath exists or not.
name|assertTrue
argument_list|(
name|containerMetaDataPath
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chunksPath
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|File
name|containerMetaDataLoc
init|=
operator|new
name|File
argument_list|(
name|containerMetaDataPath
argument_list|)
decl_stmt|;
comment|//Check whether container file, check sum file and container db file exists
comment|// or not.
name|assertTrue
argument_list|(
name|KeyValueContainerLocationUtil
operator|.
name|getContainerFile
argument_list|(
name|containerMetaDataLoc
argument_list|,
name|containerName
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|,
literal|".Container File does"
operator|+
literal|" not exist"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|KeyValueContainerLocationUtil
operator|.
name|getContainerCheckSumFile
argument_list|(
name|containerMetaDataLoc
argument_list|,
name|containerName
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Container check sum "
operator|+
literal|"File does"
operator|+
literal|" not exist"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|KeyValueContainerLocationUtil
operator|.
name|getContainerDBFile
argument_list|(
name|containerMetaDataLoc
argument_list|,
name|containerName
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Container DB does "
operator|+
literal|"not exist"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDuplicateContainer ()
specifier|public
name|void
name|testDuplicateContainer
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// Create Container.
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testDuplicateContainer failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"ContainerFile already "
operator|+
literal|"exists"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|CONTAINER_ALREADY_EXISTS
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDiskFullExceptionCreateContainer ()
specifier|public
name|void
name|testDiskFullExceptionCreateContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|when
argument_list|(
name|volumeChoosingPolicy
operator|.
name|chooseVolume
argument_list|(
name|anyList
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|DiskChecker
operator|.
name|DiskOutOfSpaceException
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testDiskFullExceptionCreateContainer failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"disk out of space"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|DISK_OUT_OF_SPACE
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDeleteContainer ()
specifier|public
name|void
name|testDeleteContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|keyValueContainerData
operator|.
name|setState
argument_list|(
name|ContainerProtos
operator|.
name|ContainerLifeCycleState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|=
operator|new
name|KeyValueContainer
argument_list|(
name|keyValueContainerData
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|delete
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|containerMetaDataPath
init|=
name|keyValueContainerData
operator|.
name|getMetadataPath
argument_list|()
decl_stmt|;
name|File
name|containerMetaDataLoc
init|=
operator|new
name|File
argument_list|(
name|containerMetaDataPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Container directory still exists"
argument_list|,
name|containerMetaDataLoc
operator|.
name|getParentFile
argument_list|()
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Container File still exists"
argument_list|,
name|KeyValueContainerLocationUtil
operator|.
name|getContainerFile
argument_list|(
name|containerMetaDataLoc
argument_list|,
name|containerName
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Container DB file still exists"
argument_list|,
name|KeyValueContainerLocationUtil
operator|.
name|getContainerDBFile
argument_list|(
name|containerMetaDataLoc
argument_list|,
name|containerName
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseContainer ()
specifier|public
name|void
name|testCloseContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|close
argument_list|()
expr_stmt|;
name|keyValueContainerData
operator|=
operator|(
name|KeyValueContainerData
operator|)
name|keyValueContainer
operator|.
name|getContainerData
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerLifeCycleState
operator|.
name|CLOSED
argument_list|,
name|keyValueContainerData
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|//Check state in the .container file
name|String
name|containerMetaDataPath
init|=
name|keyValueContainerData
operator|.
name|getMetadataPath
argument_list|()
decl_stmt|;
name|File
name|containerMetaDataLoc
init|=
operator|new
name|File
argument_list|(
name|containerMetaDataPath
argument_list|)
decl_stmt|;
name|File
name|containerFile
init|=
name|KeyValueContainerLocationUtil
operator|.
name|getContainerFile
argument_list|(
name|containerMetaDataLoc
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|keyValueContainerData
operator|=
operator|(
name|KeyValueContainerData
operator|)
name|ContainerDataYaml
operator|.
name|readContainerFile
argument_list|(
name|containerFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerLifeCycleState
operator|.
name|CLOSED
argument_list|,
name|keyValueContainerData
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseInvalidContainer ()
specifier|public
name|void
name|testCloseInvalidContainer
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|keyValueContainerData
operator|.
name|setState
argument_list|(
name|ContainerProtos
operator|.
name|ContainerLifeCycleState
operator|.
name|INVALID
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"testCloseInvalidContainer failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|INVALID_CONTAINER_STATE
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Invalid container data"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUpdateContainer ()
specifier|public
name|void
name|testUpdateContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
literal|"VOLUME"
argument_list|,
literal|"ozone"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|put
argument_list|(
literal|"OWNER"
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|update
argument_list|(
name|metadata
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|keyValueContainerData
operator|=
operator|(
name|KeyValueContainerData
operator|)
name|keyValueContainer
operator|.
name|getContainerData
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|keyValueContainerData
operator|.
name|getMetadata
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//Check metadata in the .container file
name|String
name|containerMetaDataPath
init|=
name|keyValueContainerData
operator|.
name|getMetadataPath
argument_list|()
decl_stmt|;
name|File
name|containerMetaDataLoc
init|=
operator|new
name|File
argument_list|(
name|containerMetaDataPath
argument_list|)
decl_stmt|;
name|File
name|containerFile
init|=
name|KeyValueContainerLocationUtil
operator|.
name|getContainerFile
argument_list|(
name|containerMetaDataLoc
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|keyValueContainerData
operator|=
operator|(
name|KeyValueContainerData
operator|)
name|ContainerDataYaml
operator|.
name|readContainerFile
argument_list|(
name|containerFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|keyValueContainerData
operator|.
name|getMetadata
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateContainerInvalidMetadata ()
specifier|public
name|void
name|testUpdateContainerInvalidMetadata
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
literal|"VOLUME"
argument_list|,
literal|"ozone"
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|update
argument_list|(
name|metadata
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//Trying to update again with same metadata
name|keyValueContainer
operator|.
name|update
argument_list|(
name|metadata
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testUpdateContainerInvalidMetadata failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Container Metadata update "
operator|+
literal|"error"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|CONTAINER_METADATA_ERROR
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUpdateContainerUnsupportedRequest ()
specifier|public
name|void
name|testUpdateContainerUnsupportedRequest
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|keyValueContainerData
operator|.
name|setState
argument_list|(
name|ContainerProtos
operator|.
name|ContainerLifeCycleState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|=
operator|new
name|KeyValueContainer
argument_list|(
name|keyValueContainerData
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
literal|"VOLUME"
argument_list|,
literal|"ozone"
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|update
argument_list|(
name|metadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testUpdateContainerUnsupportedRequest failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Updating a closed container "
operator|+
literal|"is not allowed"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|UNSUPPORTED_REQUEST
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

