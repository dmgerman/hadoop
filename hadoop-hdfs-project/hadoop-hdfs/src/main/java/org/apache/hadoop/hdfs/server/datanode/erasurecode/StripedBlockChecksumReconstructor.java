begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.erasurecode
package|package
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
name|erasurecode
package|;
end_package

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
name|nio
operator|.
name|ByteBuffer
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
name|util
operator|.
name|Arrays
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
name|io
operator|.
name|DataOutputBuffer
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
name|io
operator|.
name|MD5Hash
import|;
end_import

begin_comment
comment|/**  * StripedBlockChecksumReconstructor reconstruct one or more missed striped  * block in the striped block group, the minimum number of live striped blocks  * should be no less than data block number. Then checksum will be recalculated  * using the newly reconstructed block.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StripedBlockChecksumReconstructor
specifier|public
class|class
name|StripedBlockChecksumReconstructor
extends|extends
name|StripedReconstructor
block|{
DECL|field|targetBuffer
specifier|private
name|ByteBuffer
name|targetBuffer
decl_stmt|;
DECL|field|targetIndices
specifier|private
specifier|final
name|byte
index|[]
name|targetIndices
decl_stmt|;
DECL|field|checksumBuf
specifier|private
name|byte
index|[]
name|checksumBuf
decl_stmt|;
DECL|field|checksumWriter
specifier|private
name|DataOutputBuffer
name|checksumWriter
decl_stmt|;
DECL|field|md5
specifier|private
name|MD5Hash
name|md5
decl_stmt|;
DECL|field|checksumDataLen
specifier|private
name|long
name|checksumDataLen
decl_stmt|;
DECL|field|requestedLen
specifier|private
name|long
name|requestedLen
decl_stmt|;
DECL|method|StripedBlockChecksumReconstructor (ErasureCodingWorker worker, StripedReconstructionInfo stripedReconInfo, DataOutputBuffer checksumWriter, long requestedBlockLength)
specifier|public
name|StripedBlockChecksumReconstructor
parameter_list|(
name|ErasureCodingWorker
name|worker
parameter_list|,
name|StripedReconstructionInfo
name|stripedReconInfo
parameter_list|,
name|DataOutputBuffer
name|checksumWriter
parameter_list|,
name|long
name|requestedBlockLength
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|worker
argument_list|,
name|stripedReconInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|targetIndices
operator|=
name|stripedReconInfo
operator|.
name|getTargetIndices
argument_list|()
expr_stmt|;
assert|assert
name|targetIndices
operator|!=
literal|null
assert|;
name|this
operator|.
name|checksumWriter
operator|=
name|checksumWriter
expr_stmt|;
name|this
operator|.
name|requestedLen
operator|=
name|requestedBlockLength
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|init ()
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|initDecoderIfNecessary
argument_list|()
expr_stmt|;
name|getStripedReader
argument_list|()
operator|.
name|init
argument_list|()
expr_stmt|;
comment|// allocate buffer to keep the reconstructed block data
name|targetBuffer
operator|=
name|allocateBuffer
argument_list|(
name|getBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|maxTargetLen
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|int
name|targetIndex
range|:
name|targetIndices
control|)
block|{
name|maxTargetLen
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxTargetLen
argument_list|,
name|getBlockLen
argument_list|(
name|targetIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setMaxTargetLength
argument_list|(
name|maxTargetLen
argument_list|)
expr_stmt|;
name|int
name|checksumSize
init|=
name|getChecksum
argument_list|()
operator|.
name|getChecksumSize
argument_list|()
decl_stmt|;
name|int
name|bytesPerChecksum
init|=
name|getChecksum
argument_list|()
operator|.
name|getBytesPerChecksum
argument_list|()
decl_stmt|;
name|int
name|tmpLen
init|=
name|checksumSize
operator|*
operator|(
name|getBufferSize
argument_list|()
operator|/
name|bytesPerChecksum
operator|)
decl_stmt|;
name|checksumBuf
operator|=
operator|new
name|byte
index|[
name|tmpLen
index|]
expr_stmt|;
block|}
DECL|method|reconstruct ()
specifier|public
name|void
name|reconstruct
parameter_list|()
throws|throws
name|IOException
block|{
name|MessageDigest
name|digester
init|=
name|MD5Hash
operator|.
name|getDigester
argument_list|()
decl_stmt|;
name|long
name|maxTargetLength
init|=
name|getMaxTargetLength
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|requestedLen
operator|>
literal|0
operator|&&
name|getPositionInBlock
argument_list|()
operator|<
name|maxTargetLength
condition|)
block|{
name|long
name|remaining
init|=
name|maxTargetLength
operator|-
name|getPositionInBlock
argument_list|()
decl_stmt|;
specifier|final
name|int
name|toReconstructLen
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|getStripedReader
argument_list|()
operator|.
name|getBufferSize
argument_list|()
argument_list|,
name|remaining
argument_list|)
decl_stmt|;
comment|// step1: read from minimum source DNs required for reconstruction.
comment|// The returned success list is the source DNs we do real read from
name|getStripedReader
argument_list|()
operator|.
name|readMinimumSources
argument_list|(
name|toReconstructLen
argument_list|)
expr_stmt|;
comment|// step2: decode to reconstruct targets
name|reconstructTargets
argument_list|(
name|toReconstructLen
argument_list|)
expr_stmt|;
comment|// step3: calculate checksum
name|checksumDataLen
operator|+=
name|checksumWithTargetOutput
argument_list|(
name|targetBuffer
operator|.
name|array
argument_list|()
argument_list|,
name|toReconstructLen
argument_list|,
name|digester
argument_list|)
expr_stmt|;
name|updatePositionInBlock
argument_list|(
name|toReconstructLen
argument_list|)
expr_stmt|;
name|requestedLen
operator|-=
name|toReconstructLen
expr_stmt|;
name|clearBuffers
argument_list|()
expr_stmt|;
block|}
name|byte
index|[]
name|digest
init|=
name|digester
operator|.
name|digest
argument_list|()
decl_stmt|;
name|md5
operator|=
operator|new
name|MD5Hash
argument_list|(
name|digest
argument_list|)
expr_stmt|;
name|md5
operator|.
name|write
argument_list|(
name|checksumWriter
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checksumWithTargetOutput (byte[] outputData, int toReconstructLen, MessageDigest digester)
specifier|private
name|long
name|checksumWithTargetOutput
parameter_list|(
name|byte
index|[]
name|outputData
parameter_list|,
name|int
name|toReconstructLen
parameter_list|,
name|MessageDigest
name|digester
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|checksumDataLength
init|=
literal|0
decl_stmt|;
comment|// Calculate partial block checksum. There are two cases.
comment|// case-1) length of data bytes which is fraction of bytesPerCRC
comment|// case-2) length of data bytes which is less than bytesPerCRC
if|if
condition|(
name|requestedLen
operator|<=
name|toReconstructLen
condition|)
block|{
name|int
name|remainingLen
init|=
name|Math
operator|.
name|toIntExact
argument_list|(
name|requestedLen
argument_list|)
decl_stmt|;
name|outputData
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|targetBuffer
operator|.
name|array
argument_list|()
argument_list|,
name|remainingLen
argument_list|)
expr_stmt|;
name|int
name|partialLength
init|=
name|remainingLen
operator|%
name|getChecksum
argument_list|()
operator|.
name|getBytesPerChecksum
argument_list|()
decl_stmt|;
name|int
name|checksumRemaining
init|=
operator|(
name|remainingLen
operator|/
name|getChecksum
argument_list|()
operator|.
name|getBytesPerChecksum
argument_list|()
operator|)
operator|*
name|getChecksum
argument_list|()
operator|.
name|getChecksumSize
argument_list|()
decl_stmt|;
name|int
name|dataOffset
init|=
literal|0
decl_stmt|;
comment|// case-1) length of data bytes which is fraction of bytesPerCRC
if|if
condition|(
name|checksumRemaining
operator|>
literal|0
condition|)
block|{
name|remainingLen
operator|=
name|remainingLen
operator|-
name|partialLength
expr_stmt|;
name|checksumBuf
operator|=
operator|new
name|byte
index|[
name|checksumRemaining
index|]
expr_stmt|;
name|getChecksum
argument_list|()
operator|.
name|calculateChunkedSums
argument_list|(
name|outputData
argument_list|,
name|dataOffset
argument_list|,
name|remainingLen
argument_list|,
name|checksumBuf
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|digester
operator|.
name|update
argument_list|(
name|checksumBuf
argument_list|,
literal|0
argument_list|,
name|checksumBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|checksumDataLength
operator|=
name|checksumBuf
operator|.
name|length
expr_stmt|;
name|dataOffset
operator|=
name|remainingLen
expr_stmt|;
block|}
comment|// case-2) length of data bytes which is less than bytesPerCRC
if|if
condition|(
name|partialLength
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|partialCrc
init|=
operator|new
name|byte
index|[
name|getChecksum
argument_list|()
operator|.
name|getChecksumSize
argument_list|()
index|]
decl_stmt|;
name|getChecksum
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
name|getChecksum
argument_list|()
operator|.
name|update
argument_list|(
name|outputData
argument_list|,
name|dataOffset
argument_list|,
name|partialLength
argument_list|)
expr_stmt|;
name|getChecksum
argument_list|()
operator|.
name|writeValue
argument_list|(
name|partialCrc
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|digester
operator|.
name|update
argument_list|(
name|partialCrc
argument_list|)
expr_stmt|;
name|checksumDataLength
operator|+=
name|partialCrc
operator|.
name|length
expr_stmt|;
block|}
name|clearBuffers
argument_list|()
expr_stmt|;
comment|// calculated checksum for the requested length, return checksum length.
return|return
name|checksumDataLength
return|;
block|}
name|getChecksum
argument_list|()
operator|.
name|calculateChunkedSums
argument_list|(
name|outputData
argument_list|,
literal|0
argument_list|,
name|outputData
operator|.
name|length
argument_list|,
name|checksumBuf
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// updates digest using the checksum array of bytes
name|digester
operator|.
name|update
argument_list|(
name|checksumBuf
argument_list|,
literal|0
argument_list|,
name|checksumBuf
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|checksumBuf
operator|.
name|length
return|;
block|}
DECL|method|reconstructTargets (int toReconstructLen)
specifier|private
name|void
name|reconstructTargets
parameter_list|(
name|int
name|toReconstructLen
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
index|[]
name|inputs
init|=
name|getStripedReader
argument_list|()
operator|.
name|getInputBuffers
argument_list|(
name|toReconstructLen
argument_list|)
decl_stmt|;
name|ByteBuffer
index|[]
name|outputs
init|=
operator|new
name|ByteBuffer
index|[
literal|1
index|]
decl_stmt|;
name|targetBuffer
operator|.
name|limit
argument_list|(
name|toReconstructLen
argument_list|)
expr_stmt|;
name|outputs
index|[
literal|0
index|]
operator|=
name|targetBuffer
expr_stmt|;
name|int
index|[]
name|tarIndices
init|=
operator|new
name|int
index|[
name|targetIndices
operator|.
name|length
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
name|targetIndices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tarIndices
index|[
name|i
index|]
operator|=
name|targetIndices
index|[
name|i
index|]
expr_stmt|;
block|}
name|getDecoder
argument_list|()
operator|.
name|decode
argument_list|(
name|inputs
argument_list|,
name|tarIndices
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear all associated buffers.    */
DECL|method|clearBuffers ()
specifier|private
name|void
name|clearBuffers
parameter_list|()
block|{
name|getStripedReader
argument_list|()
operator|.
name|clearBuffers
argument_list|()
expr_stmt|;
name|targetBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|getMD5 ()
specifier|public
name|MD5Hash
name|getMD5
parameter_list|()
block|{
return|return
name|md5
return|;
block|}
DECL|method|getChecksumDataLen ()
specifier|public
name|long
name|getChecksumDataLen
parameter_list|()
block|{
return|return
name|checksumDataLen
return|;
block|}
block|}
end_class

end_unit

