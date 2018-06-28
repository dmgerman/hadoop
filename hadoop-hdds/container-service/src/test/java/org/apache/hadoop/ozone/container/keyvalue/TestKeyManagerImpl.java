begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|client
operator|.
name|BlockID
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
name|helpers
operator|.
name|ChunkInfo
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
name|helpers
operator|.
name|KeyData
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
name|keyvalue
operator|.
name|impl
operator|.
name|KeyManagerImpl
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
name|IOException
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
name|junit
operator|.
name|Assert
operator|.
name|*
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
comment|/**  * This class is used to test key related operations on the container.  */
end_comment

begin_class
DECL|class|TestKeyManagerImpl
specifier|public
class|class
name|TestKeyManagerImpl
block|{
DECL|field|config
specifier|private
name|OzoneConfiguration
name|config
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
DECL|field|keyData
specifier|private
name|KeyData
name|keyData
decl_stmt|;
DECL|field|keyValueContainerManager
specifier|private
name|KeyManagerImpl
name|keyValueContainerManager
decl_stmt|;
DECL|field|blockID
specifier|private
name|BlockID
name|blockID
decl_stmt|;
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
name|config
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
name|config
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
name|config
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
comment|// Creating KeyData
name|blockID
operator|=
operator|new
name|BlockID
argument_list|(
literal|1L
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|keyData
operator|=
operator|new
name|KeyData
argument_list|(
name|blockID
argument_list|)
expr_stmt|;
name|keyData
operator|.
name|addMetadata
argument_list|(
literal|"VOLUME"
argument_list|,
literal|"ozone"
argument_list|)
expr_stmt|;
name|keyData
operator|.
name|addMetadata
argument_list|(
literal|"OWNER"
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|chunkList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|ChunkInfo
name|info
init|=
operator|new
name|ChunkInfo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d.data.%d"
argument_list|,
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|chunkList
operator|.
name|add
argument_list|(
name|info
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|keyData
operator|.
name|setChunks
argument_list|(
name|chunkList
argument_list|)
expr_stmt|;
comment|// Create KeyValueContainerManager
name|keyValueContainerManager
operator|=
operator|new
name|KeyManagerImpl
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutAndGetKey ()
specifier|public
name|void
name|testPutAndGetKey
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Put Key
name|keyValueContainerManager
operator|.
name|putKey
argument_list|(
name|keyValueContainer
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
comment|//Get Key
name|KeyData
name|fromGetKeyData
init|=
name|keyValueContainerManager
operator|.
name|getKey
argument_list|(
name|keyValueContainer
argument_list|,
name|keyData
operator|.
name|getBlockID
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|keyData
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|fromGetKeyData
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|keyData
operator|.
name|getLocalID
argument_list|()
argument_list|,
name|fromGetKeyData
operator|.
name|getLocalID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|keyData
operator|.
name|getChunks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|fromGetKeyData
operator|.
name|getChunks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|keyData
operator|.
name|getMetadata
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|fromGetKeyData
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
DECL|method|testDeleteKey ()
specifier|public
name|void
name|testDeleteKey
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|//Put Key
name|keyValueContainerManager
operator|.
name|putKey
argument_list|(
name|keyValueContainer
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
comment|//Delete Key
name|keyValueContainerManager
operator|.
name|deleteKey
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testDeleteKey failed"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testListKey ()
specifier|public
name|void
name|testListKey
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|keyValueContainerManager
operator|.
name|putKey
argument_list|(
name|keyValueContainer
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|KeyData
argument_list|>
name|listKeyData
init|=
name|keyValueContainerManager
operator|.
name|listKey
argument_list|(
name|keyValueContainer
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|listKeyData
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listKeyData
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|2
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|blockID
operator|=
operator|new
name|BlockID
argument_list|(
literal|1L
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|keyData
operator|=
operator|new
name|KeyData
argument_list|(
name|blockID
argument_list|)
expr_stmt|;
name|keyData
operator|.
name|addMetadata
argument_list|(
literal|"VOLUME"
argument_list|,
literal|"ozone"
argument_list|)
expr_stmt|;
name|keyData
operator|.
name|addMetadata
argument_list|(
literal|"OWNER"
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|chunkList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|ChunkInfo
name|info
init|=
operator|new
name|ChunkInfo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d.data.%d"
argument_list|,
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|chunkList
operator|.
name|add
argument_list|(
name|info
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|keyData
operator|.
name|setChunks
argument_list|(
name|chunkList
argument_list|)
expr_stmt|;
name|keyValueContainerManager
operator|.
name|putKey
argument_list|(
name|keyValueContainer
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
block|}
name|listKeyData
operator|=
name|keyValueContainerManager
operator|.
name|listKey
argument_list|(
name|keyValueContainer
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|listKeyData
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listKeyData
operator|.
name|size
argument_list|()
operator|==
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testListKey failed"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetNoSuchKey ()
specifier|public
name|void
name|testGetNoSuchKey
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|keyValueContainerManager
operator|.
name|getKey
argument_list|(
name|keyValueContainer
argument_list|,
operator|new
name|BlockID
argument_list|(
literal|1L
argument_list|,
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testGetNoSuchKey failed"
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
literal|"Unable to find the key."
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
name|NO_SUCH_KEY
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

