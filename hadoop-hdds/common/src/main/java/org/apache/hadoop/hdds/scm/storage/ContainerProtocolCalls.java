begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.storage
package|package
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
name|storage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
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
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|XceiverClientSpi
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
name|DatanodeBlockID
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|GetKeyResponseProto
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
name|GetSmallFileRequestProto
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
name|GetSmallFileResponseProto
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|PutSmallFileRequestProto
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ReadChunkResponseProto
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
name|ReadContainerRequestProto
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
name|ReadContainerResponseProto
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
name|Type
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|KeyValue
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Implementation of all container protocol calls performed by Container  * clients.  */
end_comment

begin_class
DECL|class|ContainerProtocolCalls
specifier|public
specifier|final
class|class
name|ContainerProtocolCalls
block|{
comment|/**    * There is no need to instantiate this class.    */
DECL|method|ContainerProtocolCalls ()
specifier|private
name|ContainerProtocolCalls
parameter_list|()
block|{   }
comment|/**    * Calls the container protocol to get a container key.    *    * @param xceiverClient client to perform call    * @param datanodeBlockID blockID to identify container    * @param traceID container protocol call args    * @return container protocol get key response    * @throws IOException if there is an I/O error while performing the call    */
DECL|method|getKey (XceiverClientSpi xceiverClient, DatanodeBlockID datanodeBlockID, String traceID)
specifier|public
specifier|static
name|GetKeyResponseProto
name|getKey
parameter_list|(
name|XceiverClientSpi
name|xceiverClient
parameter_list|,
name|DatanodeBlockID
name|datanodeBlockID
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
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
name|setBlockID
argument_list|(
name|datanodeBlockID
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
decl_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|Type
operator|.
name|GetKey
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
operator|.
name|setGetKey
argument_list|(
name|readKeyRequest
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|xceiverClient
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getGetKey
argument_list|()
return|;
block|}
comment|/**    * Calls the container protocol to put a container key.    *    * @param xceiverClient client to perform call    * @param containerKeyData key data to identify container    * @param traceID container protocol call args    * @throws IOException if there is an I/O error while performing the call    */
DECL|method|putKey (XceiverClientSpi xceiverClient, KeyData containerKeyData, String traceID)
specifier|public
specifier|static
name|void
name|putKey
parameter_list|(
name|XceiverClientSpi
name|xceiverClient
parameter_list|,
name|KeyData
name|containerKeyData
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
block|{
name|PutKeyRequestProto
operator|.
name|Builder
name|createKeyRequest
init|=
name|PutKeyRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyData
argument_list|(
name|containerKeyData
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
decl_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|Type
operator|.
name|PutKey
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
operator|.
name|setPutKey
argument_list|(
name|createKeyRequest
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|xceiverClient
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls the container protocol to read a chunk.    *    * @param xceiverClient client to perform call    * @param chunk information about chunk to read    * @param blockID ID of the block    * @param traceID container protocol call args    * @return container protocol read chunk response    * @throws IOException if there is an I/O error while performing the call    */
DECL|method|readChunk (XceiverClientSpi xceiverClient, ChunkInfo chunk, BlockID blockID, String traceID)
specifier|public
specifier|static
name|ReadChunkResponseProto
name|readChunk
parameter_list|(
name|XceiverClientSpi
name|xceiverClient
parameter_list|,
name|ChunkInfo
name|chunk
parameter_list|,
name|BlockID
name|blockID
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
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
name|chunk
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
decl_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|Type
operator|.
name|ReadChunk
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
operator|.
name|setReadChunk
argument_list|(
name|readChunkRequest
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|xceiverClient
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getReadChunk
argument_list|()
return|;
block|}
comment|/**    * Calls the container protocol to write a chunk.    *    * @param xceiverClient client to perform call    * @param chunk information about chunk to write    * @param blockID ID of the block    * @param data the data of the chunk to write    * @param traceID container protocol call args    * @throws IOException if there is an I/O error while performing the call    */
DECL|method|writeChunk (XceiverClientSpi xceiverClient, ChunkInfo chunk, BlockID blockID, ByteString data, String traceID)
specifier|public
specifier|static
name|void
name|writeChunk
parameter_list|(
name|XceiverClientSpi
name|xceiverClient
parameter_list|,
name|ChunkInfo
name|chunk
parameter_list|,
name|BlockID
name|blockID
parameter_list|,
name|ByteString
name|data
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
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
name|chunk
argument_list|)
operator|.
name|setData
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
decl_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|Type
operator|.
name|WriteChunk
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
operator|.
name|setWriteChunk
argument_list|(
name|writeChunkRequest
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|xceiverClient
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allows writing a small file using single RPC. This takes the container    * name, key name and data to write sends all that data to the container using    * a single RPC. This API is designed to be used for files which are smaller    * than 1 MB.    *    * @param client - client that communicates with the container.    * @param blockID - ID of the block    * @param data - Data to be written into the container.    * @param traceID - Trace ID for logging purpose.    * @throws IOException    */
DECL|method|writeSmallFile (XceiverClientSpi client, BlockID blockID, byte[] data, String traceID)
specifier|public
specifier|static
name|void
name|writeSmallFile
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|,
name|BlockID
name|blockID
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
block|{
name|KeyData
name|containerKeyData
init|=
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
name|build
argument_list|()
decl_stmt|;
name|PutKeyRequestProto
operator|.
name|Builder
name|createKeyRequest
init|=
name|PutKeyRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyData
argument_list|(
name|containerKeyData
argument_list|)
decl_stmt|;
name|KeyValue
name|keyValue
init|=
name|KeyValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
literal|"OverWriteRequested"
argument_list|)
operator|.
name|setValue
argument_list|(
literal|"true"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ChunkInfo
name|chunk
init|=
name|ChunkInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChunkName
argument_list|(
name|blockID
operator|.
name|getLocalID
argument_list|()
operator|+
literal|"_chunk"
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
name|length
argument_list|)
operator|.
name|addMetadata
argument_list|(
name|keyValue
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|PutSmallFileRequestProto
name|putSmallFileRequest
init|=
name|PutSmallFileRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChunkInfo
argument_list|(
name|chunk
argument_list|)
operator|.
name|setKey
argument_list|(
name|createKeyRequest
argument_list|)
operator|.
name|setData
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|data
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|client
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
decl_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|Type
operator|.
name|PutSmallFile
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
operator|.
name|setPutSmallFile
argument_list|(
name|putSmallFileRequest
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * createContainer call that creates a container on the datanode.    * @param client  - client    * @param containerID - ID of container    * @param traceID - traceID    * @throws IOException    */
DECL|method|createContainer (XceiverClientSpi client, long containerID, String traceID)
specifier|public
specifier|static
name|void
name|createContainer
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|,
name|long
name|containerID
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerProtos
operator|.
name|CreateContainerRequestProto
operator|.
name|Builder
name|createRequest
init|=
name|ContainerProtos
operator|.
name|CreateContainerRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|createRequest
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|createRequest
operator|.
name|setContainerType
argument_list|(
name|ContainerProtos
operator|.
name|ContainerType
operator|.
name|KeyValueContainer
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|client
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
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
name|CreateContainer
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCreateContainer
argument_list|(
name|createRequest
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes a container from a pipeline.    *    * @param client    * @param force whether or not to forcibly delete the container.    * @param traceID    * @throws IOException    */
DECL|method|deleteContainer (XceiverClientSpi client, long containerID, boolean force, String traceID)
specifier|public
specifier|static
name|void
name|deleteContainer
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|,
name|long
name|containerID
parameter_list|,
name|boolean
name|force
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerProtos
operator|.
name|DeleteContainerRequestProto
operator|.
name|Builder
name|deleteRequest
init|=
name|ContainerProtos
operator|.
name|DeleteContainerRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|deleteRequest
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|deleteRequest
operator|.
name|setForceDelete
argument_list|(
name|force
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|client
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
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
name|DeleteContainer
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDeleteContainer
argument_list|(
name|deleteRequest
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * Close a container.    *    * @param client    * @param containerID    * @param traceID    * @throws IOException    */
DECL|method|closeContainer (XceiverClientSpi client, long containerID, String traceID)
specifier|public
specifier|static
name|void
name|closeContainer
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|,
name|long
name|containerID
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerProtos
operator|.
name|CloseContainerRequestProto
operator|.
name|Builder
name|closeRequest
init|=
name|ContainerProtos
operator|.
name|CloseContainerRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|closeRequest
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|client
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
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
name|Type
operator|.
name|CloseContainer
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCloseContainer
argument_list|(
name|closeRequest
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * readContainer call that gets meta data from an existing container.    *    * @param client - client    * @param traceID - trace ID    * @throws IOException    */
DECL|method|readContainer ( XceiverClientSpi client, long containerID, String traceID)
specifier|public
specifier|static
name|ReadContainerResponseProto
name|readContainer
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|,
name|long
name|containerID
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
block|{
name|ReadContainerRequestProto
operator|.
name|Builder
name|readRequest
init|=
name|ReadContainerRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|readRequest
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|client
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
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
name|Type
operator|.
name|ReadContainer
argument_list|)
expr_stmt|;
name|request
operator|.
name|setReadContainer
argument_list|(
name|readRequest
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getReadContainer
argument_list|()
return|;
block|}
comment|/**    * Reads the data given the blockID    *    * @param client    * @param blockID - ID of the block    * @param traceID - trace ID    * @return GetSmallFileResponseProto    * @throws IOException    */
DECL|method|readSmallFile (XceiverClientSpi client, BlockID blockID, String traceID)
specifier|public
specifier|static
name|GetSmallFileResponseProto
name|readSmallFile
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|,
name|BlockID
name|blockID
parameter_list|,
name|String
name|traceID
parameter_list|)
throws|throws
name|IOException
block|{
name|GetKeyRequestProto
operator|.
name|Builder
name|getKey
init|=
name|GetKeyRequestProto
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
decl_stmt|;
name|ContainerProtos
operator|.
name|GetSmallFileRequestProto
name|getSmallFileRequest
init|=
name|GetSmallFileRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|getKey
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|client
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuidString
argument_list|()
decl_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|Type
operator|.
name|GetSmallFile
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|traceID
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|id
argument_list|)
operator|.
name|setGetSmallFile
argument_list|(
name|getSmallFileRequest
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|validateContainerResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getGetSmallFile
argument_list|()
return|;
block|}
comment|/**    * Validates a response from a container protocol call.  Any non-successful    * return code is mapped to a corresponding exception and thrown.    *    * @param response container protocol call response    * @throws IOException if the container protocol call failed    */
DECL|method|validateContainerResponse ( ContainerCommandResponseProto response )
specifier|private
specifier|static
name|void
name|validateContainerResponse
parameter_list|(
name|ContainerCommandResponseProto
name|response
parameter_list|)
throws|throws
name|StorageContainerException
block|{
if|if
condition|(
name|response
operator|.
name|getResult
argument_list|()
operator|==
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|StorageContainerException
argument_list|(
name|response
operator|.
name|getMessage
argument_list|()
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

