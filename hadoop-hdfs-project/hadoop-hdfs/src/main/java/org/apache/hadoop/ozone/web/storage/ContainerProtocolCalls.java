begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.storage
package|package
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
name|storage
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_BAD_REQUEST
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_INTERNAL_ERROR
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
name|ozone
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|client
operator|.
name|XceiverClient
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
name|exceptions
operator|.
name|ErrorTable
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
name|exceptions
operator|.
name|OzoneException
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

begin_comment
comment|/**  * Implementation of all container protocol calls performed by  * {@link DistributedStorageHandler}.  */
end_comment

begin_class
DECL|class|ContainerProtocolCalls
specifier|final
class|class
name|ContainerProtocolCalls
block|{
comment|/**    * Calls the container protocol to get a container key.    *    * @param xceiverClient client to perform call    * @param containerKeyData key data to identify container    * @param args container protocol call args    * @returns container protocol get key response    * @throws IOException if there is an I/O error while performing the call    * @throws OzoneException if the container protocol call failed    */
DECL|method|getKey (XceiverClient xceiverClient, KeyData containerKeyData, UserArgs args)
specifier|public
specifier|static
name|GetKeyResponseProto
name|getKey
parameter_list|(
name|XceiverClient
name|xceiverClient
parameter_list|,
name|KeyData
name|containerKeyData
parameter_list|,
name|UserArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|setPipeline
argument_list|(
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
operator|.
name|setKeyData
argument_list|(
name|containerKeyData
argument_list|)
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
name|args
operator|.
name|getRequestID
argument_list|()
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
argument_list|,
name|args
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getGetKey
argument_list|()
return|;
block|}
comment|/**    * Calls the container protocol to put a container key.    *    * @param xceiverClient client to perform call    * @param containerKeyData key data to identify container    * @param args container protocol call args    * @throws IOException if there is an I/O error while performing the call    * @throws OzoneException if the container protocol call failed    */
DECL|method|putKey (XceiverClient xceiverClient, KeyData containerKeyData, UserArgs args)
specifier|public
specifier|static
name|void
name|putKey
parameter_list|(
name|XceiverClient
name|xceiverClient
parameter_list|,
name|KeyData
name|containerKeyData
parameter_list|,
name|UserArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|setPipeline
argument_list|(
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
operator|.
name|setKeyData
argument_list|(
name|containerKeyData
argument_list|)
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
name|args
operator|.
name|getRequestID
argument_list|()
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
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls the container protocol to read a chunk.    *    * @param xceiverClient client to perform call    * @param chunk information about chunk to read    * @param key the key name    * @param args container protocol call args    * @returns container protocol read chunk response    * @throws IOException if there is an I/O error while performing the call    * @throws OzoneException if the container protocol call failed    */
DECL|method|readChunk (XceiverClient xceiverClient, ChunkInfo chunk, String key, UserArgs args)
specifier|public
specifier|static
name|ReadChunkResponseProto
name|readChunk
parameter_list|(
name|XceiverClient
name|xceiverClient
parameter_list|,
name|ChunkInfo
name|chunk
parameter_list|,
name|String
name|key
parameter_list|,
name|UserArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|setPipeline
argument_list|(
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|key
argument_list|)
operator|.
name|setChunkData
argument_list|(
name|chunk
argument_list|)
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
name|args
operator|.
name|getRequestID
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
argument_list|,
name|args
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getReadChunk
argument_list|()
return|;
block|}
comment|/**    * Calls the container protocol to write a chunk.    *    * @param xceiverClient client to perform call    * @param chunk information about chunk to write    * @param key the key name    * @param data the data of the chunk to write    * @param args container protocol call args    * @throws IOException if there is an I/O error while performing the call    * @throws OzoneException if the container protocol call failed    */
DECL|method|writeChunk (XceiverClient xceiverClient, ChunkInfo chunk, String key, ByteString data, UserArgs args)
specifier|public
specifier|static
name|void
name|writeChunk
parameter_list|(
name|XceiverClient
name|xceiverClient
parameter_list|,
name|ChunkInfo
name|chunk
parameter_list|,
name|String
name|key
parameter_list|,
name|ByteString
name|data
parameter_list|,
name|UserArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|setPipeline
argument_list|(
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|key
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
name|args
operator|.
name|getRequestID
argument_list|()
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
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates a response from a container protocol call.  Any non-successful    * return code is mapped to a corresponding exception and thrown.    *    * @param response container protocol call response    * @param args container protocol call args    * @throws OzoneException if the container protocol call failed    */
DECL|method|validateContainerResponse ( ContainerCommandResponseProto response, UserArgs args)
specifier|private
specifier|static
name|void
name|validateContainerResponse
parameter_list|(
name|ContainerCommandResponseProto
name|response
parameter_list|,
name|UserArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
switch|switch
condition|(
name|response
operator|.
name|getResult
argument_list|()
condition|)
block|{
case|case
name|SUCCESS
case|:
break|break;
case|case
name|MALFORMED_REQUEST
case|:
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
operator|new
name|OzoneException
argument_list|(
name|HTTP_BAD_REQUEST
argument_list|,
literal|"badRequest"
argument_list|,
literal|"Bad container request."
argument_list|)
argument_list|,
name|args
argument_list|)
throw|;
case|case
name|UNSUPPORTED_REQUEST
case|:
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
operator|new
name|OzoneException
argument_list|(
name|HTTP_INTERNAL_ERROR
argument_list|,
literal|"internalServerError"
argument_list|,
literal|"Unsupported container request."
argument_list|)
argument_list|,
name|args
argument_list|)
throw|;
case|case
name|CONTAINER_INTERNAL_ERROR
case|:
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
operator|new
name|OzoneException
argument_list|(
name|HTTP_INTERNAL_ERROR
argument_list|,
literal|"internalServerError"
argument_list|,
literal|"Container internal error."
argument_list|)
argument_list|,
name|args
argument_list|)
throw|;
default|default:
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
operator|new
name|OzoneException
argument_list|(
name|HTTP_INTERNAL_ERROR
argument_list|,
literal|"internalServerError"
argument_list|,
literal|"Unrecognized container response."
argument_list|)
argument_list|,
name|args
argument_list|)
throw|;
block|}
block|}
comment|/**    * There is no need to instantiate this class.    */
DECL|method|ContainerProtocolCalls ()
specifier|private
name|ContainerProtocolCalls
parameter_list|()
block|{   }
block|}
end_class

end_unit

