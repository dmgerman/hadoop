begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm.storage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|storage
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|storage
operator|.
name|ContainerProtocolCalls
operator|.
name|putKey
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
name|scm
operator|.
name|storage
operator|.
name|ContainerProtocolCalls
operator|.
name|writeChunk
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|scm
operator|.
name|XceiverClientManager
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
name|scm
operator|.
name|XceiverClientSpi
import|;
end_import

begin_comment
comment|/**  * An {@link OutputStream} used by the REST service in combination with the  * SCMClient to write the value of a key to a sequence  * of container chunks.  Writes are buffered locally and periodically written to  * the container as a new chunk.  In order to preserve the semantics that  * replacement of a pre-existing key is atomic, each instance of the stream has  * an internal unique identifier.  This unique identifier and a monotonically  * increasing chunk index form a composite key that is used as the chunk name.  * After all data is written, a putKey call creates or updates the corresponding  * container key, and this call includes the full list of chunks that make up  * the key data.  The list of chunks is updated all at once.  Therefore, a  * concurrent reader never can see an intermediate state in which different  * chunks of data from different versions of the key data are interleaved.  * This class encapsulates all state management for buffering and writing  * through to the container.  */
end_comment

begin_class
DECL|class|ChunkOutputStream
specifier|public
class|class
name|ChunkOutputStream
extends|extends
name|OutputStream
block|{
DECL|field|containerKey
specifier|private
specifier|final
name|String
name|containerKey
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|traceID
specifier|private
specifier|final
name|String
name|traceID
decl_stmt|;
DECL|field|containerKeyData
specifier|private
specifier|final
name|KeyData
operator|.
name|Builder
name|containerKeyData
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|field|xceiverClient
specifier|private
name|XceiverClientSpi
name|xceiverClient
decl_stmt|;
DECL|field|buffer
specifier|private
name|ByteBuffer
name|buffer
decl_stmt|;
DECL|field|streamId
specifier|private
specifier|final
name|String
name|streamId
decl_stmt|;
DECL|field|chunkIndex
specifier|private
name|int
name|chunkIndex
decl_stmt|;
DECL|field|chunkSize
specifier|private
name|int
name|chunkSize
decl_stmt|;
comment|/**    * Creates a new ChunkOutputStream.    *    * @param containerKey container key    * @param key chunk key    * @param xceiverClientManager client manager that controls client    * @param xceiverClient client to perform container calls    * @param traceID container protocol call args    * @param chunkSize chunk size    */
DECL|method|ChunkOutputStream (String containerKey, String key, XceiverClientManager xceiverClientManager, XceiverClientSpi xceiverClient, String traceID, int chunkSize)
specifier|public
name|ChunkOutputStream
parameter_list|(
name|String
name|containerKey
parameter_list|,
name|String
name|key
parameter_list|,
name|XceiverClientManager
name|xceiverClientManager
parameter_list|,
name|XceiverClientSpi
name|xceiverClient
parameter_list|,
name|String
name|traceID
parameter_list|,
name|int
name|chunkSize
parameter_list|)
block|{
name|this
operator|.
name|containerKey
operator|=
name|containerKey
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|traceID
operator|=
name|traceID
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|chunkSize
expr_stmt|;
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
literal|"TYPE"
argument_list|)
operator|.
name|setValue
argument_list|(
literal|"KEY"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|this
operator|.
name|containerKeyData
operator|=
name|KeyData
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerName
argument_list|(
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
operator|.
name|setName
argument_list|(
name|containerKey
argument_list|)
operator|.
name|addMetadata
argument_list|(
name|keyValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|xceiverClientManager
operator|=
name|xceiverClientManager
expr_stmt|;
name|this
operator|.
name|xceiverClient
operator|=
name|xceiverClient
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|chunkSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|streamId
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|chunkIndex
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (int b)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|int
name|rollbackPosition
init|=
name|buffer
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|rollbackLimit
init|=
name|buffer
operator|.
name|limit
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|position
argument_list|()
operator|==
name|chunkSize
condition|)
block|{
name|flushBufferToChunk
argument_list|(
name|rollbackPosition
argument_list|,
name|rollbackLimit
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|if
condition|(
operator|(
name|off
operator|<
literal|0
operator|)
operator|||
operator|(
name|off
operator|>
name|b
operator|.
name|length
operator|)
operator|||
operator|(
name|len
operator|<
literal|0
operator|)
operator|||
operator|(
operator|(
name|off
operator|+
name|len
operator|)
operator|>
name|b
operator|.
name|length
operator|)
operator|||
operator|(
operator|(
name|off
operator|+
name|len
operator|)
operator|<
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|checkOpen
argument_list|()
expr_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|writeLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|chunkSize
operator|-
name|buffer
operator|.
name|position
argument_list|()
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|int
name|rollbackPosition
init|=
name|buffer
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|rollbackLimit
init|=
name|buffer
operator|.
name|limit
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|writeLen
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|position
argument_list|()
operator|==
name|chunkSize
condition|)
block|{
name|flushBufferToChunk
argument_list|(
name|rollbackPosition
argument_list|,
name|rollbackLimit
argument_list|)
expr_stmt|;
block|}
name|off
operator|+=
name|writeLen
expr_stmt|;
name|len
operator|-=
name|writeLen
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
specifier|synchronized
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|position
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|rollbackPosition
init|=
name|buffer
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|rollbackLimit
init|=
name|buffer
operator|.
name|limit
argument_list|()
decl_stmt|;
name|flushBufferToChunk
argument_list|(
name|rollbackPosition
argument_list|,
name|rollbackLimit
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|xceiverClientManager
operator|!=
literal|null
operator|&&
name|xceiverClient
operator|!=
literal|null
operator|&&
name|buffer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
name|buffer
operator|.
name|position
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writeChunkToContainer
argument_list|()
expr_stmt|;
block|}
name|putKey
argument_list|(
name|xceiverClient
argument_list|,
name|containerKeyData
operator|.
name|build
argument_list|()
argument_list|,
name|traceID
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected Storage Container Exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|xceiverClient
argument_list|)
expr_stmt|;
name|xceiverClientManager
operator|=
literal|null
expr_stmt|;
name|xceiverClient
operator|=
literal|null
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Checks if the stream is open.  If not, throws an exception.    *    * @throws IOException if stream is closed    */
DECL|method|checkOpen ()
specifier|private
specifier|synchronized
name|void
name|checkOpen
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|xceiverClient
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"ChunkOutputStream has been closed."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Attempts to flush buffered writes by writing a new chunk to the container.    * If successful, then clears the buffer to prepare to receive writes for a    * new chunk.    *    * @param rollbackPosition position to restore in buffer if write fails    * @param rollbackLimit limit to restore in buffer if write fails    * @throws IOException if there is an I/O error while performing the call    */
DECL|method|flushBufferToChunk (int rollbackPosition, int rollbackLimit)
specifier|private
specifier|synchronized
name|void
name|flushBufferToChunk
parameter_list|(
name|int
name|rollbackPosition
parameter_list|,
name|int
name|rollbackLimit
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|writeChunkToContainer
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|position
argument_list|(
name|rollbackPosition
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|limit
argument_list|(
name|rollbackLimit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Writes buffered data as a new chunk to the container and saves chunk    * information to be used later in putKey call.    *    * @throws IOException if there is an I/O error while performing the call    */
DECL|method|writeChunkToContainer ()
specifier|private
specifier|synchronized
name|void
name|writeChunkToContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|ByteString
name|data
init|=
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|buffer
argument_list|)
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
name|DigestUtils
operator|.
name|md5Hex
argument_list|(
name|key
argument_list|)
operator|+
literal|"_stream_"
operator|+
name|streamId
operator|+
literal|"_chunk_"
operator|+
operator|++
name|chunkIndex
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
try|try
block|{
name|writeChunk
argument_list|(
name|xceiverClient
argument_list|,
name|chunk
argument_list|,
name|key
argument_list|,
name|data
argument_list|,
name|traceID
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected Storage Container Exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|containerKeyData
operator|.
name|addChunks
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

