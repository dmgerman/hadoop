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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|pipeline
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
name|hdds
operator|.
name|security
operator|.
name|token
operator|.
name|OzoneBlockTokenIdentifier
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|GetBlockResponseProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_comment
comment|/**  * An {@link InputStream} called from KeyInputStream to read a block from the  * container.  * This class encapsulates all state management for iterating  * through the sequence of chunks through {@link ChunkInputStream}.  */
end_comment

begin_class
DECL|class|BlockInputStream
specifier|public
class|class
name|BlockInputStream
extends|extends
name|InputStream
implements|implements
name|Seekable
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BlockInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|pipeline
specifier|private
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|token
specifier|private
specifier|final
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
decl_stmt|;
DECL|field|verifyChecksum
specifier|private
specifier|final
name|boolean
name|verifyChecksum
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
DECL|field|initialized
specifier|private
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
comment|// List of ChunkInputStreams, one for each chunk in the block
DECL|field|chunkStreams
specifier|private
name|List
argument_list|<
name|ChunkInputStream
argument_list|>
name|chunkStreams
decl_stmt|;
comment|// chunkOffsets[i] stores the index of the first data byte in
comment|// chunkStream i w.r.t the block data.
comment|// Letâs say we have chunk size as 40 bytes. And let's say the parent
comment|// block stores data from index 200 and has length 400.
comment|// The first 40 bytes of this block will be stored in chunk[0], next 40 in
comment|// chunk[1] and so on. But since the chunkOffsets are w.r.t the block only
comment|// and not the key, the values in chunkOffsets will be [0, 40, 80,....].
DECL|field|chunkOffsets
specifier|private
name|long
index|[]
name|chunkOffsets
init|=
literal|null
decl_stmt|;
comment|// Index of the chunkStream corresponding to the current position of the
comment|// BlockInputStream i.e offset of the data to be read next from this block
DECL|field|chunkIndex
specifier|private
name|int
name|chunkIndex
decl_stmt|;
comment|// Position of the BlockInputStream is maintainted by this variable till
comment|// the stream is initialized. This position is w.r.t to the block only and
comment|// not the key.
comment|// For the above example, if we seek to position 240 before the stream is
comment|// initialized, then value of blockPosition will be set to 40.
comment|// Once, the stream is initialized, the position of the stream
comment|// will be determined by the current chunkStream and its position.
DECL|field|blockPosition
specifier|private
name|long
name|blockPosition
init|=
literal|0
decl_stmt|;
comment|// Tracks the chunkIndex corresponding to the last blockPosition so that it
comment|// can be reset if a new position is seeked.
DECL|field|chunkIndexOfPrevPosition
specifier|private
name|int
name|chunkIndexOfPrevPosition
decl_stmt|;
DECL|method|BlockInputStream (BlockID blockId, long blockLen, Pipeline pipeline, Token<OzoneBlockTokenIdentifier> token, boolean verifyChecksum, XceiverClientManager xceiverClientManager)
specifier|public
name|BlockInputStream
parameter_list|(
name|BlockID
name|blockId
parameter_list|,
name|long
name|blockLen
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|,
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|,
name|XceiverClientManager
name|xceiverClientManager
parameter_list|)
block|{
name|this
operator|.
name|blockID
operator|=
name|blockId
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|blockLen
expr_stmt|;
name|this
operator|.
name|pipeline
operator|=
name|pipeline
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|verifyChecksum
operator|=
name|verifyChecksum
expr_stmt|;
name|this
operator|.
name|xceiverClientManager
operator|=
name|xceiverClientManager
expr_stmt|;
block|}
comment|/**    * Initialize the BlockInputStream. Get the BlockData (list of chunks) from    * the Container and create the ChunkInputStreams for each Chunk in the Block.    */
DECL|method|initialize ()
specifier|public
specifier|synchronized
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Pre-check that the stream has not been intialized already
if|if
condition|(
name|initialized
condition|)
block|{
return|return;
block|}
name|List
argument_list|<
name|ChunkInfo
argument_list|>
name|chunks
init|=
name|getChunkInfos
argument_list|()
decl_stmt|;
if|if
condition|(
name|chunks
operator|!=
literal|null
operator|&&
operator|!
name|chunks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// For each chunk in the block, create a ChunkInputStream and compute
comment|// its chunkOffset
name|this
operator|.
name|chunkOffsets
operator|=
operator|new
name|long
index|[
name|chunks
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|long
name|tempOffset
init|=
literal|0
decl_stmt|;
name|this
operator|.
name|chunkStreams
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|chunks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|addStream
argument_list|(
name|chunks
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|chunkOffsets
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
name|initialized
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|chunkIndex
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|blockPosition
operator|>
literal|0
condition|)
block|{
comment|// Stream was seeked to blockPosition before initialization. Seek to the
comment|// blockPosition now.
name|seek
argument_list|(
name|blockPosition
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Send RPC call to get the block info from the container.    * @return List of chunks in this block.    */
DECL|method|getChunkInfos ()
specifier|protected
name|List
argument_list|<
name|ChunkInfo
argument_list|>
name|getChunkInfos
parameter_list|()
throws|throws
name|IOException
block|{
comment|// irrespective of the container state, we will always read via Standalone
comment|// protocol.
if|if
condition|(
name|pipeline
operator|.
name|getType
argument_list|()
operator|!=
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
condition|)
block|{
name|pipeline
operator|=
name|Pipeline
operator|.
name|newBuilder
argument_list|(
name|pipeline
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|xceiverClient
operator|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|ChunkInfo
argument_list|>
name|chunks
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initializing BlockInputStream for get key to access {}"
argument_list|,
name|blockID
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
name|DatanodeBlockID
name|datanodeBlockID
init|=
name|blockID
operator|.
name|getDatanodeBlockIDProtobuf
argument_list|()
decl_stmt|;
name|GetBlockResponseProto
name|response
init|=
name|ContainerProtocolCalls
operator|.
name|getBlock
argument_list|(
name|xceiverClient
argument_list|,
name|datanodeBlockID
argument_list|)
decl_stmt|;
name|chunks
operator|=
name|response
operator|.
name|getBlockData
argument_list|()
operator|.
name|getChunksList
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
operator|!
name|success
condition|)
block|{
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|xceiverClient
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|chunks
return|;
block|}
comment|/**    * Append another ChunkInputStream to the end of the list. Note that the    * ChunkInputStream is only created here. The chunk will be read from the    * Datanode only when a read operation is performed on for that chunk.    */
DECL|method|addStream (ChunkInfo chunkInfo)
specifier|protected
specifier|synchronized
name|void
name|addStream
parameter_list|(
name|ChunkInfo
name|chunkInfo
parameter_list|)
block|{
name|chunkStreams
operator|.
name|add
argument_list|(
operator|new
name|ChunkInputStream
argument_list|(
name|chunkInfo
argument_list|,
name|blockID
argument_list|,
name|xceiverClient
argument_list|,
name|verifyChecksum
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getRemaining ()
specifier|public
specifier|synchronized
name|long
name|getRemaining
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|length
operator|-
name|getPos
argument_list|()
return|;
block|}
comment|/**    * {@inheritDoc}    */
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
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|==
name|EOF
condition|)
block|{
return|return
name|EOF
return|;
block|}
return|return
name|Byte
operator|.
name|toUnsignedInt
argument_list|(
name|buf
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
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
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
name|initialize
argument_list|()
expr_stmt|;
block|}
name|checkOpen
argument_list|()
expr_stmt|;
name|int
name|totalReadLen
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
comment|// if we are at the last chunk and have read the entire chunk, return
if|if
condition|(
name|chunkStreams
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
operator|(
name|chunkStreams
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|<=
name|chunkIndex
operator|&&
name|chunkStreams
operator|.
name|get
argument_list|(
name|chunkIndex
argument_list|)
operator|.
name|getRemaining
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
return|return
name|totalReadLen
operator|==
literal|0
condition|?
name|EOF
else|:
name|totalReadLen
return|;
block|}
comment|// Get the current chunkStream and read data from it
name|ChunkInputStream
name|current
init|=
name|chunkStreams
operator|.
name|get
argument_list|(
name|chunkIndex
argument_list|)
decl_stmt|;
name|int
name|numBytesToRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
operator|(
name|int
operator|)
name|current
operator|.
name|getRemaining
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numBytesRead
init|=
name|current
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|numBytesToRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|numBytesRead
operator|!=
name|numBytesToRead
condition|)
block|{
comment|// This implies that there is either data loss or corruption in the
comment|// chunk entries. Even EOF in the current stream would be covered in
comment|// this case.
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Inconsistent read for chunkName=%s length=%d numBytesRead=%d"
argument_list|,
name|current
operator|.
name|getChunkName
argument_list|()
argument_list|,
name|current
operator|.
name|getLength
argument_list|()
argument_list|,
name|numBytesRead
argument_list|)
argument_list|)
throw|;
block|}
name|totalReadLen
operator|+=
name|numBytesRead
expr_stmt|;
name|off
operator|+=
name|numBytesRead
expr_stmt|;
name|len
operator|-=
name|numBytesRead
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|getRemaining
argument_list|()
operator|<=
literal|0
operator|&&
operator|(
operator|(
name|chunkIndex
operator|+
literal|1
operator|)
operator|<
name|chunkStreams
operator|.
name|size
argument_list|()
operator|)
condition|)
block|{
name|chunkIndex
operator|+=
literal|1
expr_stmt|;
block|}
block|}
return|return
name|totalReadLen
return|;
block|}
comment|/**    * Seeks the BlockInputStream to the specified position. If the stream is    * not initialized, save the seeked position via blockPosition. Otherwise,    * update the position in 2 steps:    *    1. Updating the chunkIndex to the chunkStream corresponding to the    *    seeked position.    *    2. Seek the corresponding chunkStream to the adjusted position.    *    * Letâs say we have chunk size as 40 bytes. And let's say the parent block    * stores data from index 200 and has length 400. If the key was seeked to    * position 90, then this block will be seeked to position 90.    * When seek(90) is called on this blockStream, then    *    1. chunkIndex will be set to 2 (as indices 80 - 120 reside in chunk[2]).    *    2. chunkStream[2] will be seeked to position 10    *       (= 90 - chunkOffset[2] (= 80)).    */
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
operator|!
name|initialized
condition|)
block|{
comment|// Stream has not been initialized yet. Save the position so that it
comment|// can be seeked when the stream is initialized.
name|blockPosition
operator|=
name|pos
expr_stmt|;
return|return;
block|}
name|checkOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|pos
operator|>=
name|length
condition|)
block|{
if|if
condition|(
name|pos
operator|==
literal|0
condition|)
block|{
comment|// It is possible for length and pos to be zero in which case
comment|// seek should return instead of throwing exception
return|return;
block|}
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"EOF encountered at pos: "
operator|+
name|pos
operator|+
literal|" for block: "
operator|+
name|blockID
argument_list|)
throw|;
block|}
if|if
condition|(
name|chunkIndex
operator|>=
name|chunkStreams
operator|.
name|size
argument_list|()
condition|)
block|{
name|chunkIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|chunkOffsets
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
name|chunkOffsets
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
name|chunkOffsets
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
name|chunkOffsets
index|[
name|chunkIndex
index|]
operator|+
name|chunkStreams
operator|.
name|get
argument_list|(
name|chunkIndex
argument_list|)
operator|.
name|getLength
argument_list|()
condition|)
block|{
name|chunkIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|chunkOffsets
argument_list|,
name|chunkIndex
operator|+
literal|1
argument_list|,
name|chunkStreams
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
comment|// Reset the previous chunkStream's position
name|chunkStreams
operator|.
name|get
argument_list|(
name|chunkIndexOfPrevPosition
argument_list|)
operator|.
name|resetPosition
argument_list|()
expr_stmt|;
comment|// seek to the proper offset in the ChunkInputStream
name|chunkStreams
operator|.
name|get
argument_list|(
name|chunkIndex
argument_list|)
operator|.
name|seek
argument_list|(
name|pos
operator|-
name|chunkOffsets
index|[
name|chunkIndex
index|]
argument_list|)
expr_stmt|;
name|chunkIndexOfPrevPosition
operator|=
name|chunkIndex
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
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
comment|// The stream is not initialized yet. Return the blockPosition
return|return
name|blockPosition
return|;
block|}
else|else
block|{
return|return
name|chunkOffsets
index|[
name|chunkIndex
index|]
operator|+
name|chunkStreams
operator|.
name|get
argument_list|(
name|chunkIndex
argument_list|)
operator|.
name|getPos
argument_list|()
return|;
block|}
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
argument_list|,
literal|false
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
DECL|method|resetPosition ()
specifier|public
specifier|synchronized
name|void
name|resetPosition
parameter_list|()
block|{
name|this
operator|.
name|blockPosition
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Checks if the stream is open.  If not, throw an exception.    *    * @throws IOException if stream is closed    */
DECL|method|checkOpen ()
specifier|protected
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
literal|"BlockInputStream has been closed."
argument_list|)
throw|;
block|}
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
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getChunkIndex ()
specifier|synchronized
name|int
name|getChunkIndex
parameter_list|()
block|{
return|return
name|chunkIndex
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getBlockPosition ()
specifier|synchronized
name|long
name|getBlockPosition
parameter_list|()
block|{
return|return
name|blockPosition
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getChunkStreams ()
specifier|synchronized
name|List
argument_list|<
name|ChunkInputStream
argument_list|>
name|getChunkStreams
parameter_list|()
block|{
return|return
name|chunkStreams
return|;
block|}
block|}
end_class

end_unit

