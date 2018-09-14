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
name|fs
operator|.
name|Seekable
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
name|EOFException
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

begin_comment
comment|/**  * An {@link InputStream} used by the REST service in combination with the  * SCMClient to read the value of a key from a sequence  * of container chunks.  All bytes of the key value are stored in container  * chunks.  Each chunk may contain multiple underlying {@link ByteBuffer}  * instances.  This class encapsulates all state management for iterating  * through the sequence of chunks and the sequence of buffers within each chunk.  */
end_comment

begin_class
DECL|class|ChunkInputStream
specifier|public
class|class
name|ChunkInputStream
extends|extends
name|InputStream
implements|implements
name|Seekable
block|{
DECL|field|EOF
specifier|private
specifier|static
specifier|final
name|int
name|EOF
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|blockID
specifier|private
specifier|final
name|BlockID
name|blockID
decl_stmt|;
DECL|field|traceID
specifier|private
specifier|final
name|String
name|traceID
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
DECL|field|chunks
specifier|private
name|List
argument_list|<
name|ChunkInfo
argument_list|>
name|chunks
decl_stmt|;
DECL|field|chunkIndex
specifier|private
name|int
name|chunkIndex
decl_stmt|;
DECL|field|chunkOffset
specifier|private
name|long
index|[]
name|chunkOffset
decl_stmt|;
DECL|field|buffers
specifier|private
name|List
argument_list|<
name|ByteBuffer
argument_list|>
name|buffers
decl_stmt|;
DECL|field|bufferIndex
specifier|private
name|int
name|bufferIndex
decl_stmt|;
comment|/**    * Creates a new ChunkInputStream.    *    * @param blockID block ID of the chunk    * @param xceiverClientManager client manager that controls client    * @param xceiverClient client to perform container calls    * @param chunks list of chunks to read    * @param traceID container protocol call traceID    */
DECL|method|ChunkInputStream ( BlockID blockID, XceiverClientManager xceiverClientManager, XceiverClientSpi xceiverClient, List<ChunkInfo> chunks, String traceID)
specifier|public
name|ChunkInputStream
parameter_list|(
name|BlockID
name|blockID
parameter_list|,
name|XceiverClientManager
name|xceiverClientManager
parameter_list|,
name|XceiverClientSpi
name|xceiverClient
parameter_list|,
name|List
argument_list|<
name|ChunkInfo
argument_list|>
name|chunks
parameter_list|,
name|String
name|traceID
parameter_list|)
block|{
name|this
operator|.
name|blockID
operator|=
name|blockID
expr_stmt|;
name|this
operator|.
name|traceID
operator|=
name|traceID
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
name|chunks
operator|=
name|chunks
expr_stmt|;
name|this
operator|.
name|chunkIndex
operator|=
operator|-
literal|1
expr_stmt|;
comment|// chunkOffset[i] stores offset at which chunk i stores data in
comment|// ChunkInputStream
name|this
operator|.
name|chunkOffset
operator|=
operator|new
name|long
index|[
name|this
operator|.
name|chunks
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|initializeChunkOffset
argument_list|()
expr_stmt|;
name|this
operator|.
name|buffers
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|bufferIndex
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|initializeChunkOffset ()
specifier|private
name|void
name|initializeChunkOffset
parameter_list|()
block|{
name|int
name|tempOffset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chunks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|chunkOffset
index|[
name|i
index|]
operator|=
name|tempOffset
expr_stmt|;
name|tempOffset
operator|+=
name|chunks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|int
name|available
init|=
name|prepareRead
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
name|available
operator|==
name|EOF
condition|?
name|EOF
else|:
name|Byte
operator|.
name|toUnsignedInt
argument_list|(
name|buffers
operator|.
name|get
argument_list|(
name|bufferIndex
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte[] b, int off, int len)
specifier|public
specifier|synchronized
name|int
name|read
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
comment|// According to the JavaDocs for InputStream, it is recommended that
comment|// subclasses provide an override of bulk read if possible for performance
comment|// reasons.  In addition to performance, we need to do it for correctness
comment|// reasons.  The Ozone REST service uses PipedInputStream and
comment|// PipedOutputStream to relay HTTP response data between a Jersey thread and
comment|// a Netty thread.  It turns out that PipedInputStream/PipedOutputStream
comment|// have a subtle dependency (bug?) on the wrapped stream providing separate
comment|// implementations of single-byte read and bulk read.  Without this, get key
comment|// responses might close the connection before writing all of the bytes
comment|// advertised in the Content-Length.
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
name|off
operator|<
literal|0
operator|||
name|len
argument_list|<
literal|0
operator|||
name|len
argument_list|>
name|b
operator|.
name|length
operator|-
name|off
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
return|return
literal|0
return|;
block|}
name|checkOpen
argument_list|()
expr_stmt|;
name|int
name|total
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|available
init|=
name|prepareRead
argument_list|(
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|available
operator|==
name|EOF
condition|)
block|{
return|return
name|total
operator|!=
literal|0
condition|?
name|total
else|:
name|EOF
return|;
block|}
name|buffers
operator|.
name|get
argument_list|(
name|bufferIndex
argument_list|)
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|off
operator|+
name|total
argument_list|,
name|available
argument_list|)
expr_stmt|;
name|len
operator|-=
name|available
expr_stmt|;
name|total
operator|+=
name|available
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
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
condition|)
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
literal|"ChunkInputStream has been closed."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Prepares to read by advancing through chunks and buffers as needed until it    * finds data to return or encounters EOF.    *    * @param len desired length of data to read    * @return length of data available to read, possibly less than desired length    */
DECL|method|prepareRead (int len)
specifier|private
specifier|synchronized
name|int
name|prepareRead
parameter_list|(
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|chunks
operator|==
literal|null
operator|||
name|chunks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// This must be an empty key.
return|return
name|EOF
return|;
block|}
elseif|else
if|if
condition|(
name|buffers
operator|==
literal|null
condition|)
block|{
comment|// The first read triggers fetching the first chunk.
name|readChunkFromContainer
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|buffers
operator|.
name|isEmpty
argument_list|()
operator|&&
name|buffers
operator|.
name|get
argument_list|(
name|bufferIndex
argument_list|)
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
comment|// Data is available from the current buffer.
name|ByteBuffer
name|bb
init|=
name|buffers
operator|.
name|get
argument_list|(
name|bufferIndex
argument_list|)
decl_stmt|;
return|return
name|len
operator|>
name|bb
operator|.
name|remaining
argument_list|()
condition|?
name|bb
operator|.
name|remaining
argument_list|()
else|:
name|len
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|buffers
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|buffers
operator|.
name|get
argument_list|(
name|bufferIndex
argument_list|)
operator|.
name|hasRemaining
argument_list|()
operator|&&
name|bufferIndex
operator|<
name|buffers
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
comment|// There are additional buffers available.
operator|++
name|bufferIndex
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|chunkIndex
operator|<
name|chunks
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
comment|// There are additional chunks available.
name|readChunkFromContainer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// All available input has been consumed.
return|return
name|EOF
return|;
block|}
block|}
block|}
comment|/**    * Attempts to read the chunk at the specified offset in the chunk list.  If    * successful, then the data of the read chunk is saved so that its bytes can    * be returned from subsequent read calls.    *    * @throws IOException if there is an I/O error while performing the call    */
DECL|method|readChunkFromContainer ()
specifier|private
specifier|synchronized
name|void
name|readChunkFromContainer
parameter_list|()
throws|throws
name|IOException
block|{
comment|// On every chunk read chunkIndex should be increased so as to read the
comment|// next chunk
name|chunkIndex
operator|+=
literal|1
expr_stmt|;
specifier|final
name|ReadChunkResponseProto
name|readChunkResponse
decl_stmt|;
specifier|final
name|ChunkInfo
name|chunkInfo
init|=
name|chunks
operator|.
name|get
argument_list|(
name|chunkIndex
argument_list|)
decl_stmt|;
try|try
block|{
name|readChunkResponse
operator|=
name|ContainerProtocolCalls
operator|.
name|readChunk
argument_list|(
name|xceiverClient
argument_list|,
name|chunkInfo
argument_list|,
name|blockID
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
literal|"Unexpected OzoneException: "
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
name|ByteString
name|byteString
init|=
name|readChunkResponse
operator|.
name|getData
argument_list|()
decl_stmt|;
if|if
condition|(
name|byteString
operator|.
name|size
argument_list|()
operator|!=
name|chunkInfo
operator|.
name|getLen
argument_list|()
condition|)
block|{
comment|// Bytes read from chunk should be equal to chunk size.
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Inconsistent read for chunk=%s len=%d bytesRead=%d"
argument_list|,
name|chunkInfo
operator|.
name|getChunkName
argument_list|()
argument_list|,
name|chunkInfo
operator|.
name|getLen
argument_list|()
argument_list|,
name|byteString
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|buffers
operator|=
name|byteString
operator|.
name|asReadOnlyByteBufferList
argument_list|()
expr_stmt|;
name|bufferIndex
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seek (long pos)
specifier|public
specifier|synchronized
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
operator|(
name|chunks
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|pos
operator|>
literal|0
operator|)
operator|||
name|pos
operator|>=
name|chunkOffset
index|[
name|chunks
operator|.
name|size
argument_list|()
operator|-
literal|1
index|]
operator|+
name|chunks
operator|.
name|get
argument_list|(
name|chunks
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getLen
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"EOF encountered pos: "
operator|+
name|pos
operator|+
literal|" container key: "
operator|+
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|chunkIndex
operator|==
operator|-
literal|1
condition|)
block|{
name|chunkIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|chunkOffset
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|<
name|chunkOffset
index|[
name|chunkIndex
index|]
condition|)
block|{
name|chunkIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|chunkOffset
argument_list|,
literal|0
argument_list|,
name|chunkIndex
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|>=
name|chunkOffset
index|[
name|chunkIndex
index|]
operator|+
name|chunks
operator|.
name|get
argument_list|(
name|chunkIndex
argument_list|)
operator|.
name|getLen
argument_list|()
condition|)
block|{
name|chunkIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|chunkOffset
argument_list|,
name|chunkIndex
operator|+
literal|1
argument_list|,
name|chunks
operator|.
name|size
argument_list|()
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|chunkIndex
operator|<
literal|0
condition|)
block|{
comment|// Binary search returns -insertionPoint - 1  if element is not present
comment|// in the array. insertionPoint is the point at which element would be
comment|// inserted in the sorted array. We need to adjust the chunkIndex
comment|// accordingly so that chunkIndex = insertionPoint - 1
name|chunkIndex
operator|=
operator|-
name|chunkIndex
operator|-
literal|2
expr_stmt|;
block|}
comment|// adjust chunkIndex so that readChunkFromContainer reads the correct chunk
name|chunkIndex
operator|-=
literal|1
expr_stmt|;
name|readChunkFromContainer
argument_list|()
expr_stmt|;
name|adjustBufferIndex
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
DECL|method|adjustBufferIndex (long pos)
specifier|private
name|void
name|adjustBufferIndex
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
name|long
name|tempOffest
init|=
name|chunkOffset
index|[
name|chunkIndex
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|buffers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|pos
operator|-
name|tempOffest
operator|>=
name|buffers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|capacity
argument_list|()
condition|)
block|{
name|tempOffest
operator|+=
name|buffers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|capacity
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|bufferIndex
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
name|buffers
operator|.
name|get
argument_list|(
name|bufferIndex
argument_list|)
operator|.
name|position
argument_list|(
call|(
name|int
call|)
argument_list|(
name|pos
operator|-
name|tempOffest
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPos ()
specifier|public
specifier|synchronized
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|chunkIndex
operator|==
operator|-
literal|1
condition|?
literal|0
else|:
name|chunkOffset
index|[
name|chunkIndex
index|]
operator|+
name|buffers
operator|.
name|get
argument_list|(
name|bufferIndex
argument_list|)
operator|.
name|position
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekToNewSource (long targetPos)
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
DECL|method|getBlockID ()
specifier|public
name|BlockID
name|getBlockID
parameter_list|()
block|{
return|return
name|blockID
return|;
block|}
block|}
end_class

end_unit

