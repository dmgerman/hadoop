begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
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
name|common
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|DatanodeDetails
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerType
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
operator|.
name|ContainerCommandResponseProto
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
operator|.
name|ContainerCommandRequestProto
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
operator|.
name|WriteChunkRequestProto
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
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerAction
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
name|ContainerMetrics
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
name|interfaces
operator|.
name|Container
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
name|interfaces
operator|.
name|Handler
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
name|statemachine
operator|.
name|StateContext
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
name|KeyValueContainer
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
name|KeyValueContainerData
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
name|ratis
operator|.
name|thirdparty
operator|.
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|HDDS_DATANODE_DIR_KEY
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
name|times
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
name|verify
import|;
end_import

begin_comment
comment|/**  * Test-cases to verify the functionality of HddsDispatcher.  */
end_comment

begin_class
DECL|class|TestHddsDispatcher
specifier|public
class|class
name|TestHddsDispatcher
block|{
annotation|@
name|Test
DECL|method|testContainerCloseActionWhenFull ()
specifier|public
name|void
name|testContainerCloseActionWhenFull
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|testDir
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestHddsDispatcher
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|UUID
name|scmId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HDDS_DATANODE_DIR_KEY
argument_list|,
name|testDir
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|dd
init|=
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|ContainerSet
name|containerSet
init|=
operator|new
name|ContainerSet
argument_list|()
decl_stmt|;
name|VolumeSet
name|volumeSet
init|=
operator|new
name|VolumeSet
argument_list|(
name|dd
operator|.
name|getUuidString
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|StateContext
name|context
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|KeyValueContainerData
name|containerData
init|=
operator|new
name|KeyValueContainerData
argument_list|(
literal|1L
argument_list|,
operator|(
name|long
operator|)
name|StorageUnit
operator|.
name|GB
operator|.
name|toBytes
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Container
name|container
init|=
operator|new
name|KeyValueContainer
argument_list|(
name|containerData
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|container
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
operator|new
name|RoundRobinVolumeChoosingPolicy
argument_list|()
argument_list|,
name|scmId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|containerSet
operator|.
name|addContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|ContainerMetrics
name|metrics
init|=
name|ContainerMetrics
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ContainerType
argument_list|,
name|Handler
argument_list|>
name|handlers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerType
name|containerType
range|:
name|ContainerType
operator|.
name|values
argument_list|()
control|)
block|{
name|handlers
operator|.
name|put
argument_list|(
name|containerType
argument_list|,
name|Handler
operator|.
name|getHandlerForContainerType
argument_list|(
name|containerType
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
name|containerSet
argument_list|,
name|volumeSet
argument_list|,
name|metrics
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|HddsDispatcher
name|hddsDispatcher
init|=
operator|new
name|HddsDispatcher
argument_list|(
name|conf
argument_list|,
name|containerSet
argument_list|,
name|volumeSet
argument_list|,
name|handlers
argument_list|,
name|context
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|hddsDispatcher
operator|.
name|setScmId
argument_list|(
name|scmId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|responseOne
init|=
name|hddsDispatcher
operator|.
name|dispatch
argument_list|(
name|getWriteChunkRequest
argument_list|(
name|dd
operator|.
name|getUuidString
argument_list|()
argument_list|,
literal|1L
argument_list|,
literal|1L
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|responseOne
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|context
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|addContainerActionIfAbsent
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|ContainerAction
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|containerData
operator|.
name|setBytesUsed
argument_list|(
name|Double
operator|.
name|valueOf
argument_list|(
name|StorageUnit
operator|.
name|MB
operator|.
name|toBytes
argument_list|(
literal|950
argument_list|)
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|responseTwo
init|=
name|hddsDispatcher
operator|.
name|dispatch
argument_list|(
name|getWriteChunkRequest
argument_list|(
name|dd
operator|.
name|getUuidString
argument_list|()
argument_list|,
literal|1L
argument_list|,
literal|2L
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|responseTwo
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|context
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|addContainerActionIfAbsent
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|ContainerAction
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCreateContainerWithWriteChunk ()
specifier|public
name|void
name|testCreateContainerWithWriteChunk
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|testDir
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestHddsDispatcher
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|UUID
name|scmId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HDDS_DATANODE_DIR_KEY
argument_list|,
name|testDir
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|dd
init|=
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|ContainerSet
name|containerSet
init|=
operator|new
name|ContainerSet
argument_list|()
decl_stmt|;
name|VolumeSet
name|volumeSet
init|=
operator|new
name|VolumeSet
argument_list|(
name|dd
operator|.
name|getUuidString
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|StateContext
name|context
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerMetrics
name|metrics
init|=
name|ContainerMetrics
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ContainerType
argument_list|,
name|Handler
argument_list|>
name|handlers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerType
name|containerType
range|:
name|ContainerType
operator|.
name|values
argument_list|()
control|)
block|{
name|handlers
operator|.
name|put
argument_list|(
name|containerType
argument_list|,
name|Handler
operator|.
name|getHandlerForContainerType
argument_list|(
name|containerType
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
name|containerSet
argument_list|,
name|volumeSet
argument_list|,
name|metrics
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|HddsDispatcher
name|hddsDispatcher
init|=
operator|new
name|HddsDispatcher
argument_list|(
name|conf
argument_list|,
name|containerSet
argument_list|,
name|volumeSet
argument_list|,
name|handlers
argument_list|,
name|context
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|hddsDispatcher
operator|.
name|setScmId
argument_list|(
name|scmId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandRequestProto
name|writeChunkRequest
init|=
name|getWriteChunkRequest
argument_list|(
name|dd
operator|.
name|getUuidString
argument_list|()
argument_list|,
literal|1L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
comment|// send read chunk request and make sure container does not exist
name|ContainerCommandResponseProto
name|response
init|=
name|hddsDispatcher
operator|.
name|dispatch
argument_list|(
name|getReadChunkRequest
argument_list|(
name|writeChunkRequest
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|response
operator|.
name|getResult
argument_list|()
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|CONTAINER_NOT_FOUND
argument_list|)
expr_stmt|;
comment|// send write chunk request without sending create container
name|response
operator|=
name|hddsDispatcher
operator|.
name|dispatch
argument_list|(
name|writeChunkRequest
argument_list|)
expr_stmt|;
comment|// container should be created as part of write chunk request
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
comment|// send read chunk request to read the chunk written above
name|response
operator|=
name|hddsDispatcher
operator|.
name|dispatch
argument_list|(
name|getReadChunkRequest
argument_list|(
name|writeChunkRequest
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|response
operator|.
name|getReadChunk
argument_list|()
operator|.
name|getData
argument_list|()
argument_list|,
name|writeChunkRequest
operator|.
name|getWriteChunk
argument_list|()
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// This method has to be removed once we move scm/TestUtils.java
comment|// from server-scm project to container-service or to common project.
DECL|method|randomDatanodeDetails ()
specifier|private
specifier|static
name|DatanodeDetails
name|randomDatanodeDetails
parameter_list|()
block|{
name|DatanodeDetails
operator|.
name|Port
name|containerPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|ratisPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|RATIS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|restPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|REST
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Builder
name|builder
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setUuid
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
name|setHostName
argument_list|(
literal|"localhost"
argument_list|)
operator|.
name|setIpAddress
argument_list|(
literal|"127.0.0.1"
argument_list|)
operator|.
name|addPort
argument_list|(
name|containerPort
argument_list|)
operator|.
name|addPort
argument_list|(
name|ratisPort
argument_list|)
operator|.
name|addPort
argument_list|(
name|restPort
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getWriteChunkRequest ( String datanodeId, Long containerId, Long localId)
specifier|private
name|ContainerCommandRequestProto
name|getWriteChunkRequest
parameter_list|(
name|String
name|datanodeId
parameter_list|,
name|Long
name|containerId
parameter_list|,
name|Long
name|localId
parameter_list|)
block|{
name|ByteString
name|data
init|=
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|ChunkInfo
name|chunk
init|=
name|ContainerProtos
operator|.
name|ChunkInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChunkName
argument_list|(
name|DigestUtils
operator|.
name|md5Hex
argument_list|(
literal|"dummy-key"
argument_list|)
operator|+
literal|"_stream_"
operator|+
name|containerId
operator|+
literal|"_chunk_"
operator|+
name|localId
argument_list|)
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|setLen
argument_list|(
name|data
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|WriteChunkRequestProto
operator|.
name|Builder
name|writeChunkRequest
init|=
name|WriteChunkRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlockID
argument_list|(
operator|new
name|BlockID
argument_list|(
name|containerId
argument_list|,
name|localId
argument_list|)
operator|.
name|getDatanodeBlockIDProtobuf
argument_list|()
argument_list|)
operator|.
name|setChunkData
argument_list|(
name|chunk
argument_list|)
operator|.
name|setData
argument_list|(
name|data
argument_list|)
decl_stmt|;
return|return
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|containerId
argument_list|)
operator|.
name|setCmdType
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|WriteChunk
argument_list|)
operator|.
name|setTraceID
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
name|setDatanodeUuid
argument_list|(
name|datanodeId
argument_list|)
operator|.
name|setWriteChunk
argument_list|(
name|writeChunkRequest
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Creates container read chunk request using input container write chunk    * request.    *    * @param writeChunkRequest - Input container write chunk request    * @return container read chunk request    */
DECL|method|getReadChunkRequest ( ContainerCommandRequestProto writeChunkRequest)
specifier|private
name|ContainerCommandRequestProto
name|getReadChunkRequest
parameter_list|(
name|ContainerCommandRequestProto
name|writeChunkRequest
parameter_list|)
block|{
name|WriteChunkRequestProto
name|writeChunk
init|=
name|writeChunkRequest
operator|.
name|getWriteChunk
argument_list|()
decl_stmt|;
name|ContainerProtos
operator|.
name|ReadChunkRequestProto
operator|.
name|Builder
name|readChunkRequest
init|=
name|ContainerProtos
operator|.
name|ReadChunkRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlockID
argument_list|(
name|writeChunk
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|setChunkData
argument_list|(
name|writeChunk
operator|.
name|getChunkData
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|ReadChunk
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|writeChunk
operator|.
name|getBlockID
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|writeChunkRequest
operator|.
name|getTraceID
argument_list|()
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|writeChunkRequest
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
operator|.
name|setReadChunk
argument_list|(
name|readChunkRequest
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

