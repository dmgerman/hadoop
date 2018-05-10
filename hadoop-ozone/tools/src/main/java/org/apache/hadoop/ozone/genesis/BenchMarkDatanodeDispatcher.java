begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.genesis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
package|;
end_package

begin_import
import|import
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
name|commons
operator|.
name|lang
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
name|commons
operator|.
name|lang3
operator|.
name|RandomUtils
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|StorageLocation
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ChunkManagerImpl
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
name|ContainerManagerImpl
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
name|Dispatcher
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerManager
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
name|PipelineChannel
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
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Benchmark
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Setup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|State
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|TearDown
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
name|ArrayList
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Random
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|OzoneConsts
operator|.
name|CONTAINER_ROOT_PREFIX
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
name|LifeCycleState
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
name|proto
operator|.
name|ContainerProtos
operator|.
name|CreateContainerRequestProto
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
name|ContainerProtos
operator|.
name|ReadChunkRequestProto
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
name|ContainerProtos
operator|.
name|PutKeyRequestProto
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
name|ContainerProtos
operator|.
name|GetKeyRequestProto
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
name|ContainerProtos
operator|.
name|ContainerData
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationFactor
import|;
end_import

begin_class
annotation|@
name|State
argument_list|(
name|Scope
operator|.
name|Benchmark
argument_list|)
DECL|class|BenchMarkDatanodeDispatcher
specifier|public
class|class
name|BenchMarkDatanodeDispatcher
block|{
DECL|field|baseDir
specifier|private
name|String
name|baseDir
decl_stmt|;
DECL|field|datanodeUuid
specifier|private
name|String
name|datanodeUuid
decl_stmt|;
DECL|field|dispatcher
specifier|private
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|pipelineChannel
specifier|private
name|PipelineChannel
name|pipelineChannel
decl_stmt|;
DECL|field|data
specifier|private
name|ByteString
name|data
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
DECL|field|containerCount
specifier|private
name|AtomicInteger
name|containerCount
decl_stmt|;
DECL|field|keyCount
specifier|private
name|AtomicInteger
name|keyCount
decl_stmt|;
DECL|field|chunkCount
specifier|private
name|AtomicInteger
name|chunkCount
decl_stmt|;
DECL|field|initContainers
specifier|final
name|int
name|initContainers
init|=
literal|100
decl_stmt|;
DECL|field|initKeys
specifier|final
name|int
name|initKeys
init|=
literal|50
decl_stmt|;
DECL|field|initChunks
specifier|final
name|int
name|initChunks
init|=
literal|100
decl_stmt|;
DECL|field|containers
name|List
argument_list|<
name|Long
argument_list|>
name|containers
decl_stmt|;
DECL|field|keys
name|List
argument_list|<
name|Long
argument_list|>
name|keys
decl_stmt|;
DECL|field|chunks
name|List
argument_list|<
name|String
argument_list|>
name|chunks
decl_stmt|;
annotation|@
name|Setup
argument_list|(
name|Level
operator|.
name|Trial
argument_list|)
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
name|datanodeUuid
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|pipelineChannel
operator|=
operator|new
name|PipelineChannel
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|LifeCycleState
operator|.
name|OPEN
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
literal|"SA-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1 MB of data
name|data
operator|=
name|ByteString
operator|.
name|copyFromUtf8
argument_list|(
name|RandomStringUtils
operator|.
name|randomAscii
argument_list|(
literal|1048576
argument_list|)
argument_list|)
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ContainerManager
name|manager
init|=
operator|new
name|ContainerManagerImpl
argument_list|()
decl_stmt|;
name|baseDir
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
operator|+
name|File
operator|.
name|separator
operator|+
name|datanodeUuid
expr_stmt|;
comment|// data directory
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.datanode.data.dir"
argument_list|,
name|baseDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"data"
argument_list|)
expr_stmt|;
comment|// metadata directory
name|StorageLocation
name|metadataDir
init|=
name|StorageLocation
operator|.
name|parse
argument_list|(
name|baseDir
operator|+
name|File
operator|.
name|separator
operator|+
name|CONTAINER_ROOT_PREFIX
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|metadataDir
argument_list|)
decl_stmt|;
name|manager
operator|.
name|init
argument_list|(
name|conf
argument_list|,
name|locations
argument_list|,
name|GenesisUtil
operator|.
name|createDatanodeDetails
argument_list|(
name|datanodeUuid
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setChunkManager
argument_list|(
operator|new
name|ChunkManagerImpl
argument_list|(
name|manager
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setKeyManager
argument_list|(
operator|new
name|KeyManagerImpl
argument_list|(
name|manager
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|dispatcher
operator|=
operator|new
name|Dispatcher
argument_list|(
name|manager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|init
argument_list|()
expr_stmt|;
name|containerCount
operator|=
operator|new
name|AtomicInteger
argument_list|()
expr_stmt|;
name|keyCount
operator|=
operator|new
name|AtomicInteger
argument_list|()
expr_stmt|;
name|chunkCount
operator|=
operator|new
name|AtomicInteger
argument_list|()
expr_stmt|;
name|containers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|keys
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|chunks
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
comment|// Create containers
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|initContainers
condition|;
name|x
operator|++
control|)
block|{
name|long
name|containerID
init|=
name|Time
operator|.
name|getUtcTime
argument_list|()
operator|+
name|x
decl_stmt|;
name|ContainerCommandRequestProto
name|req
init|=
name|getCreateContainerCommand
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|dispatch
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|containers
operator|.
name|add
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|containerCount
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|initKeys
condition|;
name|x
operator|++
control|)
block|{
name|keys
operator|.
name|add
argument_list|(
name|Time
operator|.
name|getUtcTime
argument_list|()
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|initChunks
condition|;
name|x
operator|++
control|)
block|{
name|chunks
operator|.
name|add
argument_list|(
literal|"chunk-"
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
comment|// Add chunk and keys to the containers
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|initKeys
condition|;
name|x
operator|++
control|)
block|{
name|String
name|chunkName
init|=
name|chunks
operator|.
name|get
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|chunkCount
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
name|long
name|key
init|=
name|keys
operator|.
name|get
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|keyCount
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|initContainers
condition|;
name|y
operator|++
control|)
block|{
name|long
name|containerID
init|=
name|containers
operator|.
name|get
argument_list|(
name|y
argument_list|)
decl_stmt|;
name|BlockID
name|blockID
init|=
operator|new
name|BlockID
argument_list|(
name|containerID
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|dispatch
argument_list|(
name|getPutKeyCommand
argument_list|(
name|blockID
argument_list|,
name|chunkName
argument_list|)
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|dispatch
argument_list|(
name|getWriteChunkCommand
argument_list|(
name|blockID
argument_list|,
name|chunkName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|TearDown
argument_list|(
name|Level
operator|.
name|Trial
argument_list|)
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|dispatcher
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getCreateContainerCommand (long containerID)
specifier|private
name|ContainerCommandRequestProto
name|getCreateContainerCommand
parameter_list|(
name|long
name|containerID
parameter_list|)
block|{
name|CreateContainerRequestProto
operator|.
name|Builder
name|createRequest
init|=
name|CreateContainerRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|createRequest
operator|.
name|setContainerData
argument_list|(
name|ContainerData
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandRequestProto
operator|.
name|Builder
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|request
operator|.
name|setCmdType
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|CreateContainer
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCreateContainer
argument_list|(
name|createRequest
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDatanodeUuid
argument_list|(
name|datanodeUuid
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTraceID
argument_list|(
name|containerID
operator|+
literal|"-trace"
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getWriteChunkCommand ( BlockID blockID, String chunkName)
specifier|private
name|ContainerCommandRequestProto
name|getWriteChunkCommand
parameter_list|(
name|BlockID
name|blockID
parameter_list|,
name|String
name|chunkName
parameter_list|)
block|{
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
name|blockID
operator|.
name|getDatanodeBlockIDProtobuf
argument_list|()
argument_list|)
operator|.
name|setChunkData
argument_list|(
name|getChunkInfo
argument_list|(
name|blockID
argument_list|,
name|chunkName
argument_list|)
argument_list|)
operator|.
name|setData
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|ContainerCommandRequestProto
operator|.
name|Builder
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|request
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
name|getBlockTraceID
argument_list|(
name|blockID
argument_list|)
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|datanodeUuid
argument_list|)
operator|.
name|setWriteChunk
argument_list|(
name|writeChunkRequest
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getReadChunkCommand ( BlockID blockID, String chunkName)
specifier|private
name|ContainerCommandRequestProto
name|getReadChunkCommand
parameter_list|(
name|BlockID
name|blockID
parameter_list|,
name|String
name|chunkName
parameter_list|)
block|{
name|ReadChunkRequestProto
operator|.
name|Builder
name|readChunkRequest
init|=
name|ReadChunkRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlockID
argument_list|(
name|blockID
operator|.
name|getDatanodeBlockIDProtobuf
argument_list|()
argument_list|)
operator|.
name|setChunkData
argument_list|(
name|getChunkInfo
argument_list|(
name|blockID
argument_list|,
name|chunkName
argument_list|)
argument_list|)
decl_stmt|;
name|ContainerCommandRequestProto
operator|.
name|Builder
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|request
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
name|setTraceID
argument_list|(
name|getBlockTraceID
argument_list|(
name|blockID
argument_list|)
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|datanodeUuid
argument_list|)
operator|.
name|setReadChunk
argument_list|(
name|readChunkRequest
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getChunkInfo ( BlockID blockID, String chunkName)
specifier|private
name|ContainerProtos
operator|.
name|ChunkInfo
name|getChunkInfo
parameter_list|(
name|BlockID
name|blockID
parameter_list|,
name|String
name|chunkName
parameter_list|)
block|{
name|ContainerProtos
operator|.
name|ChunkInfo
operator|.
name|Builder
name|builder
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
name|chunkName
argument_list|)
operator|+
literal|"_stream_"
operator|+
name|blockID
operator|.
name|getContainerID
argument_list|()
operator|+
literal|"_block_"
operator|+
name|blockID
operator|.
name|getLocalID
argument_list|()
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
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getPutKeyCommand ( BlockID blockID, String chunkKey)
specifier|private
name|ContainerCommandRequestProto
name|getPutKeyCommand
parameter_list|(
name|BlockID
name|blockID
parameter_list|,
name|String
name|chunkKey
parameter_list|)
block|{
name|PutKeyRequestProto
operator|.
name|Builder
name|putKeyRequest
init|=
name|PutKeyRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyData
argument_list|(
name|getKeyData
argument_list|(
name|blockID
argument_list|,
name|chunkKey
argument_list|)
argument_list|)
decl_stmt|;
name|ContainerCommandRequestProto
operator|.
name|Builder
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|request
operator|.
name|setCmdType
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|PutKey
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|getBlockTraceID
argument_list|(
name|blockID
argument_list|)
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|datanodeUuid
argument_list|)
operator|.
name|setPutKey
argument_list|(
name|putKeyRequest
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getGetKeyCommand ( BlockID blockID, String chunkKey)
specifier|private
name|ContainerCommandRequestProto
name|getGetKeyCommand
parameter_list|(
name|BlockID
name|blockID
parameter_list|,
name|String
name|chunkKey
parameter_list|)
block|{
name|GetKeyRequestProto
operator|.
name|Builder
name|readKeyRequest
init|=
name|GetKeyRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyData
argument_list|(
name|getKeyData
argument_list|(
name|blockID
argument_list|,
name|chunkKey
argument_list|)
argument_list|)
decl_stmt|;
name|ContainerCommandRequestProto
operator|.
name|Builder
name|request
init|=
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
name|GetKey
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|getBlockTraceID
argument_list|(
name|blockID
argument_list|)
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|datanodeUuid
argument_list|)
operator|.
name|setGetKey
argument_list|(
name|readKeyRequest
argument_list|)
decl_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getKeyData ( BlockID blockID, String chunkKey)
specifier|private
name|ContainerProtos
operator|.
name|KeyData
name|getKeyData
parameter_list|(
name|BlockID
name|blockID
parameter_list|,
name|String
name|chunkKey
parameter_list|)
block|{
name|ContainerProtos
operator|.
name|KeyData
operator|.
name|Builder
name|builder
init|=
name|ContainerProtos
operator|.
name|KeyData
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlockID
argument_list|(
name|blockID
operator|.
name|getDatanodeBlockIDProtobuf
argument_list|()
argument_list|)
operator|.
name|addChunks
argument_list|(
name|getChunkInfo
argument_list|(
name|blockID
argument_list|,
name|chunkKey
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Benchmark
DECL|method|createContainer (BenchMarkDatanodeDispatcher bmdd)
specifier|public
name|void
name|createContainer
parameter_list|(
name|BenchMarkDatanodeDispatcher
name|bmdd
parameter_list|)
block|{
name|long
name|containerID
init|=
name|RandomUtils
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|ContainerCommandRequestProto
name|req
init|=
name|getCreateContainerCommand
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|bmdd
operator|.
name|dispatcher
operator|.
name|dispatch
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|bmdd
operator|.
name|containers
operator|.
name|add
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|bmdd
operator|.
name|containerCount
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Benchmark
DECL|method|writeChunk (BenchMarkDatanodeDispatcher bmdd)
specifier|public
name|void
name|writeChunk
parameter_list|(
name|BenchMarkDatanodeDispatcher
name|bmdd
parameter_list|)
block|{
name|bmdd
operator|.
name|dispatcher
operator|.
name|dispatch
argument_list|(
name|getWriteChunkCommand
argument_list|(
name|getRandomBlockID
argument_list|()
argument_list|,
name|getNewChunkToWrite
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
DECL|method|readChunk (BenchMarkDatanodeDispatcher bmdd)
specifier|public
name|void
name|readChunk
parameter_list|(
name|BenchMarkDatanodeDispatcher
name|bmdd
parameter_list|)
block|{
name|BlockID
name|blockID
init|=
name|getRandomBlockID
argument_list|()
decl_stmt|;
name|String
name|chunkKey
init|=
name|getRandomChunkToRead
argument_list|()
decl_stmt|;
name|bmdd
operator|.
name|dispatcher
operator|.
name|dispatch
argument_list|(
name|getReadChunkCommand
argument_list|(
name|blockID
argument_list|,
name|chunkKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
DECL|method|putKey (BenchMarkDatanodeDispatcher bmdd)
specifier|public
name|void
name|putKey
parameter_list|(
name|BenchMarkDatanodeDispatcher
name|bmdd
parameter_list|)
block|{
name|BlockID
name|blockID
init|=
name|getRandomBlockID
argument_list|()
decl_stmt|;
name|String
name|chunkKey
init|=
name|getNewChunkToWrite
argument_list|()
decl_stmt|;
name|bmdd
operator|.
name|dispatcher
operator|.
name|dispatch
argument_list|(
name|getPutKeyCommand
argument_list|(
name|blockID
argument_list|,
name|chunkKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
DECL|method|getKey (BenchMarkDatanodeDispatcher bmdd)
specifier|public
name|void
name|getKey
parameter_list|(
name|BenchMarkDatanodeDispatcher
name|bmdd
parameter_list|)
block|{
name|BlockID
name|blockID
init|=
name|getRandomBlockID
argument_list|()
decl_stmt|;
name|String
name|chunkKey
init|=
name|getNewChunkToWrite
argument_list|()
decl_stmt|;
name|bmdd
operator|.
name|dispatcher
operator|.
name|dispatch
argument_list|(
name|getGetKeyCommand
argument_list|(
name|blockID
argument_list|,
name|chunkKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Chunks writes from benchmark only reaches certain containers
comment|// Use initChunks instead of updated counters to guarantee
comment|// key/chunks are readable.
DECL|method|getRandomBlockID ()
specifier|private
name|BlockID
name|getRandomBlockID
parameter_list|()
block|{
return|return
operator|new
name|BlockID
argument_list|(
name|getRandomContainerID
argument_list|()
argument_list|,
name|getRandomKeyID
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getRandomContainerID ()
specifier|private
name|long
name|getRandomContainerID
parameter_list|()
block|{
return|return
name|containers
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|initContainers
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getRandomKeyID ()
specifier|private
name|long
name|getRandomKeyID
parameter_list|()
block|{
return|return
name|keys
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|initKeys
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getRandomChunkToRead ()
specifier|private
name|String
name|getRandomChunkToRead
parameter_list|()
block|{
return|return
name|chunks
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|initChunks
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getNewChunkToWrite ()
specifier|private
name|String
name|getNewChunkToWrite
parameter_list|()
block|{
return|return
literal|"chunk-"
operator|+
name|chunkCount
operator|.
name|getAndIncrement
argument_list|()
return|;
block|}
DECL|method|getBlockTraceID (BlockID blockID)
specifier|private
name|String
name|getBlockTraceID
parameter_list|(
name|BlockID
name|blockID
parameter_list|)
block|{
return|return
name|blockID
operator|.
name|getContainerID
argument_list|()
operator|+
literal|"-"
operator|+
name|blockID
operator|.
name|getLocalID
argument_list|()
operator|+
literal|"-trace"
return|;
block|}
block|}
end_class

end_unit

