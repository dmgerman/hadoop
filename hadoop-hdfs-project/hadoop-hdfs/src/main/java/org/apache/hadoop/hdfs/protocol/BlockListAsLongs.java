begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
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
name|io
operator|.
name|OutputStream
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|BlockListAsLongs
operator|.
name|BlockReportReplica
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|ReplicaState
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
name|Replica
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|CodedInputStream
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
name|CodedOutputStream
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
name|WireFormat
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|BlockListAsLongs
specifier|public
specifier|abstract
class|class
name|BlockListAsLongs
implements|implements
name|Iterable
argument_list|<
name|BlockReportReplica
argument_list|>
block|{
DECL|field|CHUNK_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|CHUNK_SIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
comment|// 64K
DECL|field|EMPTY_LONGS
specifier|private
specifier|static
name|long
index|[]
name|EMPTY_LONGS
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|0
block|}
decl_stmt|;
DECL|field|EMPTY
specifier|public
specifier|static
name|BlockListAsLongs
name|EMPTY
init|=
operator|new
name|BlockListAsLongs
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getNumberOfBlocks
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteString
name|getBlocksBuffer
parameter_list|()
block|{
return|return
name|ByteString
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
index|[]
name|getBlockListAsLongs
parameter_list|()
block|{
return|return
name|EMPTY_LONGS
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BlockReportReplica
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Prepare an instance to in-place decode the given ByteString buffer.    * @param numBlocks - blocks in the buffer    * @param blocksBuf - ByteString encoded varints    * @param maxDataLength - maximum allowable data size in protobuf message    * @return BlockListAsLongs    */
DECL|method|decodeBuffer (final int numBlocks, final ByteString blocksBuf, final int maxDataLength)
specifier|public
specifier|static
name|BlockListAsLongs
name|decodeBuffer
parameter_list|(
specifier|final
name|int
name|numBlocks
parameter_list|,
specifier|final
name|ByteString
name|blocksBuf
parameter_list|,
specifier|final
name|int
name|maxDataLength
parameter_list|)
block|{
return|return
operator|new
name|BufferDecoder
argument_list|(
name|numBlocks
argument_list|,
name|blocksBuf
argument_list|,
name|maxDataLength
argument_list|)
return|;
block|}
comment|/**    * Prepare an instance to in-place decode the given ByteString buffers.    * @param numBlocks - blocks in the buffers    * @param blocksBufs - list of ByteString encoded varints    * @return BlockListAsLongs    */
annotation|@
name|VisibleForTesting
DECL|method|decodeBuffers (final int numBlocks, final List<ByteString> blocksBufs)
specifier|public
specifier|static
name|BlockListAsLongs
name|decodeBuffers
parameter_list|(
specifier|final
name|int
name|numBlocks
parameter_list|,
specifier|final
name|List
argument_list|<
name|ByteString
argument_list|>
name|blocksBufs
parameter_list|)
block|{
return|return
name|decodeBuffers
argument_list|(
name|numBlocks
argument_list|,
name|blocksBufs
argument_list|,
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
argument_list|)
return|;
block|}
comment|/**    * Prepare an instance to in-place decode the given ByteString buffers.    * @param numBlocks - blocks in the buffers    * @param blocksBufs - list of ByteString encoded varints    * @param maxDataLength - maximum allowable data size in protobuf message    * @return BlockListAsLongs    */
DECL|method|decodeBuffers (final int numBlocks, final List<ByteString> blocksBufs, final int maxDataLength)
specifier|public
specifier|static
name|BlockListAsLongs
name|decodeBuffers
parameter_list|(
specifier|final
name|int
name|numBlocks
parameter_list|,
specifier|final
name|List
argument_list|<
name|ByteString
argument_list|>
name|blocksBufs
parameter_list|,
specifier|final
name|int
name|maxDataLength
parameter_list|)
block|{
comment|// this doesn't actually copy the data
return|return
name|decodeBuffer
argument_list|(
name|numBlocks
argument_list|,
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|blocksBufs
argument_list|)
argument_list|,
name|maxDataLength
argument_list|)
return|;
block|}
comment|/**    * Prepare an instance to in-place decode the given list of Longs.  Note    * it's much more efficient to decode ByteString buffers and only exists    * for compatibility.    * @param blocksList - list of longs    * @return BlockListAsLongs    */
DECL|method|decodeLongs (List<Long> blocksList)
specifier|public
specifier|static
name|BlockListAsLongs
name|decodeLongs
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|blocksList
parameter_list|)
block|{
return|return
name|decodeLongs
argument_list|(
name|blocksList
argument_list|,
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
argument_list|)
return|;
block|}
comment|/**    * Prepare an instance to in-place decode the given list of Longs.  Note    * it's much more efficient to decode ByteString buffers and only exists    * for compatibility.    * @param blocksList - list of longs    * @param maxDataLength - maximum allowable data size in protobuf message    * @return BlockListAsLongs    */
DECL|method|decodeLongs (List<Long> blocksList, int maxDataLength)
specifier|public
specifier|static
name|BlockListAsLongs
name|decodeLongs
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|blocksList
parameter_list|,
name|int
name|maxDataLength
parameter_list|)
block|{
return|return
name|blocksList
operator|.
name|isEmpty
argument_list|()
condition|?
name|EMPTY
else|:
operator|new
name|LongsDecoder
argument_list|(
name|blocksList
argument_list|,
name|maxDataLength
argument_list|)
return|;
block|}
comment|/**    * Prepare an instance to encode the collection of replicas into an    * efficient ByteString.    * @param replicas - replicas to encode    * @return BlockListAsLongs    */
annotation|@
name|VisibleForTesting
DECL|method|encode ( final Collection<? extends Replica> replicas)
specifier|public
specifier|static
name|BlockListAsLongs
name|encode
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|?
extends|extends
name|Replica
argument_list|>
name|replicas
parameter_list|)
block|{
name|BlockListAsLongs
operator|.
name|Builder
name|builder
init|=
name|builder
argument_list|(
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
argument_list|)
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|readFrom (InputStream is, int maxDataLength)
specifier|public
specifier|static
name|BlockListAsLongs
name|readFrom
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|int
name|maxDataLength
parameter_list|)
throws|throws
name|IOException
block|{
name|CodedInputStream
name|cis
init|=
name|CodedInputStream
operator|.
name|newInstance
argument_list|(
name|is
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxDataLength
operator|!=
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
condition|)
block|{
name|cis
operator|.
name|setSizeLimit
argument_list|(
name|maxDataLength
argument_list|)
expr_stmt|;
block|}
name|int
name|numBlocks
init|=
operator|-
literal|1
decl_stmt|;
name|ByteString
name|blocksBuf
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|cis
operator|.
name|isAtEnd
argument_list|()
condition|)
block|{
name|int
name|tag
init|=
name|cis
operator|.
name|readTag
argument_list|()
decl_stmt|;
name|int
name|field
init|=
name|WireFormat
operator|.
name|getTagFieldNumber
argument_list|(
name|tag
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|field
condition|)
block|{
case|case
literal|0
case|:
break|break;
case|case
literal|1
case|:
name|numBlocks
operator|=
operator|(
name|int
operator|)
name|cis
operator|.
name|readInt32
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|blocksBuf
operator|=
name|cis
operator|.
name|readBytes
argument_list|()
expr_stmt|;
break|break;
default|default:
name|cis
operator|.
name|skipField
argument_list|(
name|tag
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|numBlocks
operator|!=
operator|-
literal|1
operator|&&
name|blocksBuf
operator|!=
literal|null
condition|)
block|{
return|return
name|decodeBuffer
argument_list|(
name|numBlocks
argument_list|,
name|blocksBuf
argument_list|,
name|maxDataLength
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|writeTo (OutputStream os)
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|CodedOutputStream
name|cos
init|=
name|CodedOutputStream
operator|.
name|newInstance
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|cos
operator|.
name|writeInt32
argument_list|(
literal|1
argument_list|,
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|cos
operator|.
name|writeBytes
argument_list|(
literal|2
argument_list|,
name|getBlocksBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|cos
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|builder ()
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
name|builder
argument_list|(
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
argument_list|)
return|;
block|}
DECL|method|builder (int maxDataLength)
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|int
name|maxDataLength
parameter_list|)
block|{
return|return
operator|new
name|BlockListAsLongs
operator|.
name|Builder
argument_list|(
name|maxDataLength
argument_list|)
return|;
block|}
comment|/**    * The number of blocks    * @return - the number of blocks    */
DECL|method|getNumberOfBlocks ()
specifier|abstract
specifier|public
name|int
name|getNumberOfBlocks
parameter_list|()
function_decl|;
comment|/**    * Very efficient encoding of the block report into a ByteString to avoid    * the overhead of protobuf repeating fields.  Primitive repeating fields    * require re-allocs of an ArrayList<Long> and the associated (un)boxing    * overhead which puts pressure on GC.    *     * The structure of the buffer is as follows:    * - each replica is represented by 4 longs:    *   blockId, block length, genstamp, replica state    *    * @return ByteString encoded block report    */
DECL|method|getBlocksBuffer ()
specifier|abstract
specifier|public
name|ByteString
name|getBlocksBuffer
parameter_list|()
function_decl|;
comment|/**    * List of ByteStrings that encode this block report    *    * @return ByteStrings    */
DECL|method|getBlocksBuffers ()
specifier|public
name|List
argument_list|<
name|ByteString
argument_list|>
name|getBlocksBuffers
parameter_list|()
block|{
specifier|final
name|ByteString
name|blocksBuf
init|=
name|getBlocksBuffer
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ByteString
argument_list|>
name|buffers
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|blocksBuf
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|<=
name|CHUNK_SIZE
condition|)
block|{
name|buffers
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|blocksBuf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffers
operator|=
operator|new
name|ArrayList
argument_list|<
name|ByteString
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|size
condition|;
name|pos
operator|+=
name|CHUNK_SIZE
control|)
block|{
comment|// this doesn't actually copy the data
name|buffers
operator|.
name|add
argument_list|(
name|blocksBuf
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|pos
operator|+
name|CHUNK_SIZE
argument_list|,
name|size
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buffers
return|;
block|}
comment|/**    * Convert block report to old-style list of longs.  Only used to    * re-encode the block report when the DN detects an older NN. This is    * inefficient, but in practice a DN is unlikely to be upgraded first    *     * The structure of the array is as follows:    * 0: the length of the finalized replica list;    * 1: the length of the under-construction replica list;    * - followed by finalized replica list where each replica is represented by    *   3 longs: one for the blockId, one for the block length, and one for    *   the generation stamp;    * - followed by the invalid replica represented with three -1s;    * - followed by the under-construction replica list where each replica is    *   represented by 4 longs: three for the block id, length, generation     *   stamp, and the fourth for the replica state.    * @return list of longs    */
DECL|method|getBlockListAsLongs ()
specifier|abstract
specifier|public
name|long
index|[]
name|getBlockListAsLongs
parameter_list|()
function_decl|;
comment|/**    * Returns a singleton iterator over blocks in the block report.  Do not    * add the returned blocks to a collection.    * @return Iterator    */
DECL|method|iterator ()
specifier|abstract
specifier|public
name|Iterator
argument_list|<
name|BlockReportReplica
argument_list|>
name|iterator
parameter_list|()
function_decl|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|out
specifier|private
specifier|final
name|ByteString
operator|.
name|Output
name|out
decl_stmt|;
DECL|field|cos
specifier|private
specifier|final
name|CodedOutputStream
name|cos
decl_stmt|;
DECL|field|numBlocks
specifier|private
name|int
name|numBlocks
init|=
literal|0
decl_stmt|;
DECL|field|numFinalized
specifier|private
name|int
name|numFinalized
init|=
literal|0
decl_stmt|;
DECL|field|maxDataLength
specifier|private
specifier|final
name|int
name|maxDataLength
decl_stmt|;
DECL|method|Builder (int maxDataLength)
name|Builder
parameter_list|(
name|int
name|maxDataLength
parameter_list|)
block|{
name|out
operator|=
name|ByteString
operator|.
name|newOutput
argument_list|(
literal|64
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|cos
operator|=
name|CodedOutputStream
operator|.
name|newInstance
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDataLength
operator|=
name|maxDataLength
expr_stmt|;
block|}
DECL|method|add (Replica replica)
specifier|public
name|void
name|add
parameter_list|(
name|Replica
name|replica
parameter_list|)
block|{
try|try
block|{
comment|// zig-zag to reduce size of legacy blocks
name|cos
operator|.
name|writeSInt64NoTag
argument_list|(
name|replica
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
name|cos
operator|.
name|writeRawVarint64
argument_list|(
name|replica
operator|.
name|getBytesOnDisk
argument_list|()
argument_list|)
expr_stmt|;
name|cos
operator|.
name|writeRawVarint64
argument_list|(
name|replica
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
name|ReplicaState
name|state
init|=
name|replica
operator|.
name|getState
argument_list|()
decl_stmt|;
comment|// although state is not a 64-bit value, using a long varint to
comment|// allow for future use of the upper bits
name|cos
operator|.
name|writeRawVarint64
argument_list|(
name|state
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|ReplicaState
operator|.
name|FINALIZED
condition|)
block|{
name|numFinalized
operator|++
expr_stmt|;
block|}
name|numBlocks
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// shouldn't happen, ByteString.Output doesn't throw IOE
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
DECL|method|getNumberOfBlocks ()
specifier|public
name|int
name|getNumberOfBlocks
parameter_list|()
block|{
return|return
name|numBlocks
return|;
block|}
DECL|method|build ()
specifier|public
name|BlockListAsLongs
name|build
parameter_list|()
block|{
try|try
block|{
name|cos
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// shouldn't happen, ByteString.Output doesn't throw IOE
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
return|return
operator|new
name|BufferDecoder
argument_list|(
name|numBlocks
argument_list|,
name|numFinalized
argument_list|,
name|out
operator|.
name|toByteString
argument_list|()
argument_list|,
name|maxDataLength
argument_list|)
return|;
block|}
block|}
comment|// decode new-style ByteString buffer based block report
DECL|class|BufferDecoder
specifier|private
specifier|static
class|class
name|BufferDecoder
extends|extends
name|BlockListAsLongs
block|{
comment|// reserve upper bits for future use.  decoding masks off these bits to
comment|// allow compatibility for the current through future release that may
comment|// start using the bits
DECL|field|NUM_BYTES_MASK
specifier|private
specifier|static
name|long
name|NUM_BYTES_MASK
init|=
operator|(
operator|-
literal|1L
operator|)
operator|>>>
operator|(
literal|64
operator|-
literal|48
operator|)
decl_stmt|;
DECL|field|REPLICA_STATE_MASK
specifier|private
specifier|static
name|long
name|REPLICA_STATE_MASK
init|=
operator|(
operator|-
literal|1L
operator|)
operator|>>>
operator|(
literal|64
operator|-
literal|4
operator|)
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|ByteString
name|buffer
decl_stmt|;
DECL|field|numBlocks
specifier|private
specifier|final
name|int
name|numBlocks
decl_stmt|;
DECL|field|numFinalized
specifier|private
name|int
name|numFinalized
decl_stmt|;
DECL|field|maxDataLength
specifier|private
specifier|final
name|int
name|maxDataLength
decl_stmt|;
DECL|method|BufferDecoder (final int numBlocks, final ByteString buf, final int maxDataLength)
name|BufferDecoder
parameter_list|(
specifier|final
name|int
name|numBlocks
parameter_list|,
specifier|final
name|ByteString
name|buf
parameter_list|,
specifier|final
name|int
name|maxDataLength
parameter_list|)
block|{
name|this
argument_list|(
name|numBlocks
argument_list|,
operator|-
literal|1
argument_list|,
name|buf
argument_list|,
name|maxDataLength
argument_list|)
expr_stmt|;
block|}
DECL|method|BufferDecoder (final int numBlocks, final int numFinalized, final ByteString buf, final int maxDataLength)
name|BufferDecoder
parameter_list|(
specifier|final
name|int
name|numBlocks
parameter_list|,
specifier|final
name|int
name|numFinalized
parameter_list|,
specifier|final
name|ByteString
name|buf
parameter_list|,
specifier|final
name|int
name|maxDataLength
parameter_list|)
block|{
name|this
operator|.
name|numBlocks
operator|=
name|numBlocks
expr_stmt|;
name|this
operator|.
name|numFinalized
operator|=
name|numFinalized
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|buf
expr_stmt|;
name|this
operator|.
name|maxDataLength
operator|=
name|maxDataLength
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfBlocks ()
specifier|public
name|int
name|getNumberOfBlocks
parameter_list|()
block|{
return|return
name|numBlocks
return|;
block|}
annotation|@
name|Override
DECL|method|getBlocksBuffer ()
specifier|public
name|ByteString
name|getBlocksBuffer
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockListAsLongs ()
specifier|public
name|long
index|[]
name|getBlockListAsLongs
parameter_list|()
block|{
comment|// terribly inefficient but only occurs if server tries to transcode
comment|// an undecoded buffer into longs - ie. it will never happen but let's
comment|// handle it anyway
if|if
condition|(
name|numFinalized
operator|==
operator|-
literal|1
condition|)
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|this
control|)
block|{
if|if
condition|(
name|replica
operator|.
name|getState
argument_list|()
operator|==
name|ReplicaState
operator|.
name|FINALIZED
condition|)
block|{
name|n
operator|++
expr_stmt|;
block|}
block|}
name|numFinalized
operator|=
name|n
expr_stmt|;
block|}
name|int
name|numUc
init|=
name|numBlocks
operator|-
name|numFinalized
decl_stmt|;
name|int
name|size
init|=
literal|2
operator|+
literal|3
operator|*
operator|(
name|numFinalized
operator|+
literal|1
operator|)
operator|+
literal|4
operator|*
operator|(
name|numUc
operator|)
decl_stmt|;
name|long
index|[]
name|longs
init|=
operator|new
name|long
index|[
name|size
index|]
decl_stmt|;
name|longs
index|[
literal|0
index|]
operator|=
name|numFinalized
expr_stmt|;
name|longs
index|[
literal|1
index|]
operator|=
name|numUc
expr_stmt|;
name|int
name|idx
init|=
literal|2
decl_stmt|;
name|int
name|ucIdx
init|=
name|idx
operator|+
literal|3
operator|*
name|numFinalized
decl_stmt|;
comment|// delimiter block
name|longs
index|[
name|ucIdx
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|longs
index|[
name|ucIdx
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|longs
index|[
name|ucIdx
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
for|for
control|(
name|BlockReportReplica
name|block
range|:
name|this
control|)
block|{
switch|switch
condition|(
name|block
operator|.
name|getState
argument_list|()
condition|)
block|{
case|case
name|FINALIZED
case|:
block|{
name|longs
index|[
name|idx
operator|++
index|]
operator|=
name|block
operator|.
name|getBlockId
argument_list|()
expr_stmt|;
name|longs
index|[
name|idx
operator|++
index|]
operator|=
name|block
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
name|longs
index|[
name|idx
operator|++
index|]
operator|=
name|block
operator|.
name|getGenerationStamp
argument_list|()
expr_stmt|;
break|break;
block|}
default|default:
block|{
name|longs
index|[
name|ucIdx
operator|++
index|]
operator|=
name|block
operator|.
name|getBlockId
argument_list|()
expr_stmt|;
name|longs
index|[
name|ucIdx
operator|++
index|]
operator|=
name|block
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
name|longs
index|[
name|ucIdx
operator|++
index|]
operator|=
name|block
operator|.
name|getGenerationStamp
argument_list|()
expr_stmt|;
name|longs
index|[
name|ucIdx
operator|++
index|]
operator|=
name|block
operator|.
name|getState
argument_list|()
operator|.
name|getValue
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|longs
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|BlockReportReplica
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|BlockReportReplica
argument_list|>
argument_list|()
block|{
specifier|final
name|BlockReportReplica
name|block
init|=
operator|new
name|BlockReportReplica
argument_list|()
decl_stmt|;
specifier|final
name|CodedInputStream
name|cis
init|=
name|buffer
operator|.
name|newCodedInput
argument_list|()
decl_stmt|;
specifier|private
name|int
name|currentBlockIndex
init|=
literal|0
decl_stmt|;
block|{
if|if
condition|(
name|maxDataLength
operator|!=
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
condition|)
block|{
name|cis
operator|.
name|setSizeLimit
argument_list|(
name|maxDataLength
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|currentBlockIndex
operator|<
name|numBlocks
return|;
block|}
annotation|@
name|Override
specifier|public
name|BlockReportReplica
name|next
parameter_list|()
block|{
name|currentBlockIndex
operator|++
expr_stmt|;
try|try
block|{
comment|// zig-zag to reduce size of legacy blocks and mask off bits
comment|// we don't (yet) understand
name|block
operator|.
name|setBlockId
argument_list|(
name|cis
operator|.
name|readSInt64
argument_list|()
argument_list|)
expr_stmt|;
name|block
operator|.
name|setNumBytes
argument_list|(
name|cis
operator|.
name|readRawVarint64
argument_list|()
operator|&
name|NUM_BYTES_MASK
argument_list|)
expr_stmt|;
name|block
operator|.
name|setGenerationStamp
argument_list|(
name|cis
operator|.
name|readRawVarint64
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|state
init|=
name|cis
operator|.
name|readRawVarint64
argument_list|()
operator|&
name|REPLICA_STATE_MASK
decl_stmt|;
name|block
operator|.
name|setState
argument_list|(
name|ReplicaState
operator|.
name|getState
argument_list|(
operator|(
name|int
operator|)
name|state
argument_list|)
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
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|block
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
comment|// decode old style block report of longs
DECL|class|LongsDecoder
specifier|private
specifier|static
class|class
name|LongsDecoder
extends|extends
name|BlockListAsLongs
block|{
DECL|field|values
specifier|private
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|values
decl_stmt|;
DECL|field|finalizedBlocks
specifier|private
specifier|final
name|int
name|finalizedBlocks
decl_stmt|;
DECL|field|numBlocks
specifier|private
specifier|final
name|int
name|numBlocks
decl_stmt|;
DECL|field|maxDataLength
specifier|private
specifier|final
name|int
name|maxDataLength
decl_stmt|;
comment|// set the header
DECL|method|LongsDecoder (List<Long> values, int maxDataLength)
name|LongsDecoder
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|values
parameter_list|,
name|int
name|maxDataLength
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
operator|.
name|subList
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|finalizedBlocks
operator|=
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|numBlocks
operator|=
name|finalizedBlocks
operator|+
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxDataLength
operator|=
name|maxDataLength
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfBlocks ()
specifier|public
name|int
name|getNumberOfBlocks
parameter_list|()
block|{
return|return
name|numBlocks
return|;
block|}
annotation|@
name|Override
DECL|method|getBlocksBuffer ()
specifier|public
name|ByteString
name|getBlocksBuffer
parameter_list|()
block|{
name|Builder
name|builder
init|=
name|builder
argument_list|(
name|maxDataLength
argument_list|)
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|this
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
operator|.
name|getBlocksBuffer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockListAsLongs ()
specifier|public
name|long
index|[]
name|getBlockListAsLongs
parameter_list|()
block|{
name|long
index|[]
name|longs
init|=
operator|new
name|long
index|[
literal|2
operator|+
name|values
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|longs
index|[
literal|0
index|]
operator|=
name|finalizedBlocks
expr_stmt|;
name|longs
index|[
literal|1
index|]
operator|=
name|numBlocks
operator|-
name|finalizedBlocks
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
name|longs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|longs
index|[
name|i
index|]
operator|=
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|longs
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|BlockReportReplica
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|BlockReportReplica
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|BlockReportReplica
name|block
init|=
operator|new
name|BlockReportReplica
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|Long
argument_list|>
name|iter
init|=
name|values
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|int
name|currentBlockIndex
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|currentBlockIndex
operator|<
name|numBlocks
return|;
block|}
annotation|@
name|Override
specifier|public
name|BlockReportReplica
name|next
parameter_list|()
block|{
if|if
condition|(
name|currentBlockIndex
operator|==
name|finalizedBlocks
condition|)
block|{
comment|// verify the presence of the delimiter block
name|readBlock
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
operator|==
operator|-
literal|1
operator|&&
name|block
operator|.
name|getNumBytes
argument_list|()
operator|==
operator|-
literal|1
operator|&&
name|block
operator|.
name|getGenerationStamp
argument_list|()
operator|==
operator|-
literal|1
argument_list|,
literal|"Invalid delimiter block"
argument_list|)
expr_stmt|;
block|}
name|readBlock
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentBlockIndex
operator|++
operator|<
name|finalizedBlocks
condition|)
block|{
name|block
operator|.
name|setState
argument_list|(
name|ReplicaState
operator|.
name|FINALIZED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|block
operator|.
name|setState
argument_list|(
name|ReplicaState
operator|.
name|getState
argument_list|(
name|iter
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|block
return|;
block|}
specifier|private
name|void
name|readBlock
parameter_list|()
block|{
name|block
operator|.
name|setBlockId
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|block
operator|.
name|setNumBytes
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|block
operator|.
name|setGenerationStamp
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockReportReplica
specifier|public
specifier|static
class|class
name|BlockReportReplica
extends|extends
name|Block
implements|implements
name|Replica
block|{
DECL|field|state
specifier|private
name|ReplicaState
name|state
decl_stmt|;
DECL|method|BlockReportReplica ()
specifier|private
name|BlockReportReplica
parameter_list|()
block|{     }
DECL|method|BlockReportReplica (Block block)
specifier|public
name|BlockReportReplica
parameter_list|(
name|Block
name|block
parameter_list|)
block|{
name|super
argument_list|(
name|block
argument_list|)
expr_stmt|;
if|if
condition|(
name|block
operator|instanceof
name|BlockReportReplica
condition|)
block|{
name|this
operator|.
name|state
operator|=
operator|(
operator|(
name|BlockReportReplica
operator|)
name|block
operator|)
operator|.
name|getState
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|state
operator|=
name|ReplicaState
operator|.
name|FINALIZED
expr_stmt|;
block|}
block|}
DECL|method|setState (ReplicaState state)
specifier|public
name|void
name|setState
parameter_list|(
name|ReplicaState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|ReplicaState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesOnDisk ()
specifier|public
name|long
name|getBytesOnDisk
parameter_list|()
block|{
return|return
name|getNumBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getVisibleLength ()
specifier|public
name|long
name|getVisibleLength
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getStorageUuid ()
specifier|public
name|String
name|getStorageUuid
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|isOnTransientStorage ()
specifier|public
name|boolean
name|isOnTransientStorage
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

