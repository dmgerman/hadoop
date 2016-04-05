begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container
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
name|binary
operator|.
name|Hex
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
name|ozone
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
name|hdfs
operator|.
name|ozone
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
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|OzoneConsts
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
name|Pipeline
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
name|junit
operator|.
name|Assert
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
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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

begin_comment
comment|/**  * Helpers for container tests.  */
end_comment

begin_class
DECL|class|ContainerTestHelper
specifier|public
class|class
name|ContainerTestHelper
block|{
DECL|field|r
specifier|private
specifier|static
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|/**    * Create a pipeline with single node replica.    *    * @return Pipeline with single node in it.    * @throws IOException    */
DECL|method|createSingleNodePipeline (String containerName)
specifier|public
specifier|static
name|Pipeline
name|createSingleNodePipeline
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
block|{
name|ServerSocket
name|socket
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|socket
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
name|DatanodeID
name|datanodeID
init|=
operator|new
name|DatanodeID
argument_list|(
name|socket
operator|.
name|getInetAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|socket
operator|.
name|getInetAddress
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|port
argument_list|,
name|port
argument_list|,
name|port
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|datanodeID
operator|.
name|setContainerPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
name|datanodeID
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
decl_stmt|;
name|pipeline
operator|.
name|addMember
argument_list|(
name|datanodeID
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|setContainerName
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|pipeline
return|;
block|}
comment|/**    * Creates a ChunkInfo for testing.    *    * @param keyName - Name of the key    * @param seqNo   - Chunk number.    * @return ChunkInfo    * @throws IOException    */
DECL|method|getChunk (String keyName, int seqNo, long offset, long len)
specifier|public
specifier|static
name|ChunkInfo
name|getChunk
parameter_list|(
name|String
name|keyName
parameter_list|,
name|int
name|seqNo
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"%s.data.%d"
argument_list|,
name|keyName
argument_list|,
name|seqNo
argument_list|)
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
comment|/**    * Generates some data of the requested len.    *    * @param len - Number of bytes.    * @return byte array with valid data.    */
DECL|method|getData (int len)
specifier|public
specifier|static
name|byte
index|[]
name|getData
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/**    * Computes the hash and sets the value correctly.    *    * @param info - chunk info.    * @param data - data array    * @throws NoSuchAlgorithmException    */
DECL|method|setDataChecksum (ChunkInfo info, byte[] data)
specifier|public
specifier|static
name|void
name|setDataChecksum
parameter_list|(
name|ChunkInfo
name|info
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
block|{
name|MessageDigest
name|sha
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|OzoneConsts
operator|.
name|FILE_HASH
argument_list|)
decl_stmt|;
name|sha
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|info
operator|.
name|setChecksum
argument_list|(
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|sha
operator|.
name|digest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a writeChunk Request.    *    * @param containerName - Name    * @param keyName       - Name    * @param datalen       - data len.    * @return Request.    * @throws IOException    * @throws NoSuchAlgorithmException    */
DECL|method|getWriteChunkRequest ( Pipeline pipeline, String containerName, String keyName, int datalen)
specifier|public
specifier|static
name|ContainerCommandRequestProto
name|getWriteChunkRequest
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|containerName
parameter_list|,
name|String
name|keyName
parameter_list|,
name|int
name|datalen
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchAlgorithmException
block|{
name|ContainerProtos
operator|.
name|WriteChunkRequestProto
operator|.
name|Builder
name|writeRequest
init|=
name|ContainerProtos
operator|.
name|WriteChunkRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|pipeline
operator|.
name|setContainerName
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|writeRequest
operator|.
name|setPipeline
argument_list|(
name|pipeline
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|writeRequest
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|getData
argument_list|(
name|datalen
argument_list|)
decl_stmt|;
name|ChunkInfo
name|info
init|=
name|getChunk
argument_list|(
name|keyName
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|datalen
argument_list|)
decl_stmt|;
name|setDataChecksum
argument_list|(
name|info
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|writeRequest
operator|.
name|setChunkData
argument_list|(
name|info
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|writeRequest
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
name|WriteChunk
argument_list|)
expr_stmt|;
name|request
operator|.
name|setWriteChunk
argument_list|(
name|writeRequest
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a read Request.    *    * @param request writeChunkRequest.    * @return Request.    * @throws IOException    * @throws NoSuchAlgorithmException    */
DECL|method|getReadChunkRequest ( ContainerProtos.WriteChunkRequestProto request)
specifier|public
specifier|static
name|ContainerCommandRequestProto
name|getReadChunkRequest
parameter_list|(
name|ContainerProtos
operator|.
name|WriteChunkRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchAlgorithmException
block|{
name|ContainerProtos
operator|.
name|ReadChunkRequestProto
operator|.
name|Builder
name|readRequest
init|=
name|ContainerProtos
operator|.
name|ReadChunkRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|readRequest
operator|.
name|setPipeline
argument_list|(
name|request
operator|.
name|getPipeline
argument_list|()
argument_list|)
expr_stmt|;
name|readRequest
operator|.
name|setKeyName
argument_list|(
name|request
operator|.
name|getKeyName
argument_list|()
argument_list|)
expr_stmt|;
name|readRequest
operator|.
name|setChunkData
argument_list|(
name|request
operator|.
name|getChunkData
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandRequestProto
operator|.
name|Builder
name|newRequest
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|newRequest
operator|.
name|setCmdType
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|ReadChunk
argument_list|)
expr_stmt|;
name|newRequest
operator|.
name|setReadChunk
argument_list|(
name|readRequest
argument_list|)
expr_stmt|;
return|return
name|newRequest
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a delete Request.    *    * @param writeRequest - write request    * @return request    * @throws IOException    * @throws NoSuchAlgorithmException    */
DECL|method|getDeleteChunkRequest ( ContainerProtos.WriteChunkRequestProto writeRequest)
specifier|public
specifier|static
name|ContainerCommandRequestProto
name|getDeleteChunkRequest
parameter_list|(
name|ContainerProtos
operator|.
name|WriteChunkRequestProto
name|writeRequest
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchAlgorithmException
block|{
name|ContainerProtos
operator|.
name|DeleteChunkRequestProto
operator|.
name|Builder
name|deleteRequest
init|=
name|ContainerProtos
operator|.
name|DeleteChunkRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|deleteRequest
operator|.
name|setPipeline
argument_list|(
name|writeRequest
operator|.
name|getPipeline
argument_list|()
argument_list|)
expr_stmt|;
name|deleteRequest
operator|.
name|setChunkData
argument_list|(
name|writeRequest
operator|.
name|getChunkData
argument_list|()
argument_list|)
expr_stmt|;
name|deleteRequest
operator|.
name|setKeyName
argument_list|(
name|writeRequest
operator|.
name|getKeyName
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
name|DeleteChunk
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDeleteChunk
argument_list|(
name|deleteRequest
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a create container command for test purposes. There are a bunch of    * tests where we need to just send a request and get a reply.    *    * @return ContainerCommandRequestProto.    */
DECL|method|getCreateContainerRequest ( String containerName)
specifier|public
specifier|static
name|ContainerCommandRequestProto
name|getCreateContainerRequest
parameter_list|(
name|String
name|containerName
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
name|ContainerProtos
operator|.
name|ContainerData
operator|.
name|Builder
name|containerData
init|=
name|ContainerProtos
operator|.
name|ContainerData
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|containerData
operator|.
name|setName
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|createRequest
operator|.
name|setPipeline
argument_list|(
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|createRequest
operator|.
name|setContainerData
argument_list|(
name|containerData
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
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a create container response for test purposes. There are a bunch of    * tests where we need to just send a request and get a reply.    *    * @return ContainerCommandRequestProto.    */
specifier|public
specifier|static
name|ContainerCommandResponseProto
DECL|method|getCreateContainerResponse (ContainerCommandRequestProto request)
name|getCreateContainerResponse
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerProtos
operator|.
name|CreateContainerResponseProto
operator|.
name|Builder
name|createResponse
init|=
name|ContainerProtos
operator|.
name|CreateContainerResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|ContainerCommandResponseProto
operator|.
name|Builder
name|response
init|=
name|ContainerCommandResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|response
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
name|response
operator|.
name|setTraceID
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setCreateContainer
argument_list|(
name|createResponse
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setResult
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns the PutKeyRequest for test purpose.    *    * @param writeRequest - Write Chunk Request.    * @return - Request    */
DECL|method|getPutKeyRequest ( ContainerProtos.WriteChunkRequestProto writeRequest)
specifier|public
specifier|static
name|ContainerCommandRequestProto
name|getPutKeyRequest
parameter_list|(
name|ContainerProtos
operator|.
name|WriteChunkRequestProto
name|writeRequest
parameter_list|)
block|{
name|ContainerProtos
operator|.
name|PutKeyRequestProto
operator|.
name|Builder
name|putRequest
init|=
name|ContainerProtos
operator|.
name|PutKeyRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|putRequest
operator|.
name|setPipeline
argument_list|(
name|writeRequest
operator|.
name|getPipeline
argument_list|()
argument_list|)
expr_stmt|;
name|KeyData
name|keyData
init|=
operator|new
name|KeyData
argument_list|(
name|writeRequest
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|writeRequest
operator|.
name|getKeyName
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|newList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|newList
operator|.
name|add
argument_list|(
name|writeRequest
operator|.
name|getChunkData
argument_list|()
argument_list|)
expr_stmt|;
name|keyData
operator|.
name|setChunks
argument_list|(
name|newList
argument_list|)
expr_stmt|;
name|putRequest
operator|.
name|setKeyData
argument_list|(
name|keyData
operator|.
name|getProtoBufMessage
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
name|PutKey
argument_list|)
expr_stmt|;
name|request
operator|.
name|setPutKey
argument_list|(
name|putRequest
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Gets a GetKeyRequest for test purpose.    *    * @param putKeyRequest - putKeyRequest.    * @return - Request    */
DECL|method|getKeyRequest ( ContainerProtos.PutKeyRequestProto putKeyRequest)
specifier|public
specifier|static
name|ContainerCommandRequestProto
name|getKeyRequest
parameter_list|(
name|ContainerProtos
operator|.
name|PutKeyRequestProto
name|putKeyRequest
parameter_list|)
block|{
name|ContainerProtos
operator|.
name|GetKeyRequestProto
operator|.
name|Builder
name|getRequest
init|=
name|ContainerProtos
operator|.
name|GetKeyRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|ContainerProtos
operator|.
name|KeyData
operator|.
name|Builder
name|keyData
init|=
name|ContainerProtos
operator|.
name|KeyData
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|keyData
operator|.
name|setContainerName
argument_list|(
name|putKeyRequest
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|keyData
operator|.
name|setName
argument_list|(
name|putKeyRequest
operator|.
name|getKeyData
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|getRequest
operator|.
name|setKeyData
argument_list|(
name|keyData
argument_list|)
expr_stmt|;
name|getRequest
operator|.
name|setPipeline
argument_list|(
name|putKeyRequest
operator|.
name|getPipeline
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
name|GetKey
argument_list|)
expr_stmt|;
name|request
operator|.
name|setGetKey
argument_list|(
name|getRequest
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    *  Verify the response against the request.    * @param request  - Request    * @param response  - Response    */
DECL|method|verifyGetKey (ContainerCommandRequestProto request, ContainerCommandResponseProto response)
specifier|public
specifier|static
name|void
name|verifyGetKey
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|ContainerCommandResponseProto
name|response
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|,
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
expr_stmt|;
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
name|SUCCESS
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|PutKeyRequestProto
name|putKey
init|=
name|request
operator|.
name|getPutKey
argument_list|()
decl_stmt|;
name|ContainerProtos
operator|.
name|GetKeyRequestProto
name|getKey
init|=
name|request
operator|.
name|getGetKey
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|putKey
operator|.
name|getKeyData
argument_list|()
operator|.
name|getChunksCount
argument_list|()
argument_list|,
name|getKey
operator|.
name|getKeyData
argument_list|()
operator|.
name|getChunksCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *    * @param putKeyRequest - putKeyRequest.    * @return - Request    */
DECL|method|getDeleteKeyRequest ( ContainerProtos.PutKeyRequestProto putKeyRequest)
specifier|public
specifier|static
name|ContainerCommandRequestProto
name|getDeleteKeyRequest
parameter_list|(
name|ContainerProtos
operator|.
name|PutKeyRequestProto
name|putKeyRequest
parameter_list|)
block|{
name|ContainerProtos
operator|.
name|DeleteKeyRequestProto
operator|.
name|Builder
name|delRequest
init|=
name|ContainerProtos
operator|.
name|DeleteKeyRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|delRequest
operator|.
name|setPipeline
argument_list|(
name|putKeyRequest
operator|.
name|getPipeline
argument_list|()
argument_list|)
expr_stmt|;
name|delRequest
operator|.
name|setName
argument_list|(
name|putKeyRequest
operator|.
name|getKeyData
argument_list|()
operator|.
name|getName
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
name|DeleteKey
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDeleteKey
argument_list|(
name|delRequest
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

