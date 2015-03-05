begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|nio
operator|.
name|BufferOverflowException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ClosedChannelException
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
name|HdfsConstants
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
name|datatransfer
operator|.
name|PacketHeader
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
name|util
operator|.
name|ByteArrayManager
import|;
end_import

begin_comment
comment|/****************************************************************  * DFSPacket is used by DataStreamer and DFSOutputStream.  * DFSOutputStream generates packets and then ask DatStreamer  * to send them to datanodes.  ****************************************************************/
end_comment

begin_class
DECL|class|DFSPacket
class|class
name|DFSPacket
block|{
DECL|field|HEART_BEAT_SEQNO
specifier|public
specifier|static
specifier|final
name|long
name|HEART_BEAT_SEQNO
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|seqno
specifier|private
specifier|final
name|long
name|seqno
decl_stmt|;
comment|// sequence number of buffer in block
DECL|field|offsetInBlock
specifier|private
specifier|final
name|long
name|offsetInBlock
decl_stmt|;
comment|// offset in block
DECL|field|syncBlock
specifier|private
name|boolean
name|syncBlock
decl_stmt|;
comment|// this packet forces the current block to disk
DECL|field|numChunks
specifier|private
name|int
name|numChunks
decl_stmt|;
comment|// number of chunks currently in packet
DECL|field|maxChunks
specifier|private
specifier|final
name|int
name|maxChunks
decl_stmt|;
comment|// max chunks in packet
DECL|field|buf
specifier|private
name|byte
index|[]
name|buf
decl_stmt|;
DECL|field|lastPacketInBlock
specifier|private
specifier|final
name|boolean
name|lastPacketInBlock
decl_stmt|;
comment|// is this the last packet in block?
comment|/**    * buf is pointed into like follows:    *  (C is checksum data, D is payload data)    *    * [_________CCCCCCCCC________________DDDDDDDDDDDDDDDD___]    *           ^        ^               ^               ^    *           |        checksumPos     dataStart       dataPos    *           checksumStart    *    * Right before sending, we move the checksum data to immediately precede    * the actual data, and then insert the header into the buffer immediately    * preceding the checksum data, so we make sure to keep enough space in    * front of the checksum data to support the largest conceivable header.    */
DECL|field|checksumStart
specifier|private
name|int
name|checksumStart
decl_stmt|;
DECL|field|checksumPos
specifier|private
name|int
name|checksumPos
decl_stmt|;
DECL|field|dataStart
specifier|private
specifier|final
name|int
name|dataStart
decl_stmt|;
DECL|field|dataPos
specifier|private
name|int
name|dataPos
decl_stmt|;
comment|/**    * Create a new packet.    *    * @param buf the buffer storing data and checksums    * @param chunksPerPkt maximum number of chunks per packet.    * @param offsetInBlock offset in bytes into the HDFS block.    * @param seqno the sequence number of this packet    * @param checksumSize the size of checksum    * @param lastPacketInBlock if this is the last packet    */
DECL|method|DFSPacket (byte[] buf, int chunksPerPkt, long offsetInBlock, long seqno, int checksumSize, boolean lastPacketInBlock)
name|DFSPacket
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|chunksPerPkt
parameter_list|,
name|long
name|offsetInBlock
parameter_list|,
name|long
name|seqno
parameter_list|,
name|int
name|checksumSize
parameter_list|,
name|boolean
name|lastPacketInBlock
parameter_list|)
block|{
name|this
operator|.
name|lastPacketInBlock
operator|=
name|lastPacketInBlock
expr_stmt|;
name|this
operator|.
name|numChunks
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|offsetInBlock
operator|=
name|offsetInBlock
expr_stmt|;
name|this
operator|.
name|seqno
operator|=
name|seqno
expr_stmt|;
name|this
operator|.
name|buf
operator|=
name|buf
expr_stmt|;
name|checksumStart
operator|=
name|PacketHeader
operator|.
name|PKT_MAX_HEADER_LEN
expr_stmt|;
name|checksumPos
operator|=
name|checksumStart
expr_stmt|;
name|dataStart
operator|=
name|checksumStart
operator|+
operator|(
name|chunksPerPkt
operator|*
name|checksumSize
operator|)
expr_stmt|;
name|dataPos
operator|=
name|dataStart
expr_stmt|;
name|maxChunks
operator|=
name|chunksPerPkt
expr_stmt|;
block|}
comment|/**    * Write data to this packet.    *    * @param inarray input array of data    * @param off the offset of data to write    * @param len the length of data to write    * @throws ClosedChannelException    */
DECL|method|writeData (byte[] inarray, int off, int len)
specifier|synchronized
name|void
name|writeData
parameter_list|(
name|byte
index|[]
name|inarray
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|ClosedChannelException
block|{
name|checkBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|dataPos
operator|+
name|len
operator|>
name|buf
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|BufferOverflowException
argument_list|()
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|inarray
argument_list|,
name|off
argument_list|,
name|buf
argument_list|,
name|dataPos
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|dataPos
operator|+=
name|len
expr_stmt|;
block|}
comment|/**    * Write checksums to this packet    *    * @param inarray input array of checksums    * @param off the offset of checksums to write    * @param len the length of checksums to write    * @throws ClosedChannelException    */
DECL|method|writeChecksum (byte[] inarray, int off, int len)
specifier|synchronized
name|void
name|writeChecksum
parameter_list|(
name|byte
index|[]
name|inarray
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|ClosedChannelException
block|{
name|checkBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|checksumPos
operator|+
name|len
operator|>
name|dataStart
condition|)
block|{
throw|throw
operator|new
name|BufferOverflowException
argument_list|()
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|inarray
argument_list|,
name|off
argument_list|,
name|buf
argument_list|,
name|checksumPos
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|checksumPos
operator|+=
name|len
expr_stmt|;
block|}
comment|/**    * Write the full packet, including the header, to the given output stream.    *    * @param stm    * @throws IOException    */
DECL|method|writeTo (DataOutputStream stm)
specifier|synchronized
name|void
name|writeTo
parameter_list|(
name|DataOutputStream
name|stm
parameter_list|)
throws|throws
name|IOException
block|{
name|checkBuffer
argument_list|()
expr_stmt|;
specifier|final
name|int
name|dataLen
init|=
name|dataPos
operator|-
name|dataStart
decl_stmt|;
specifier|final
name|int
name|checksumLen
init|=
name|checksumPos
operator|-
name|checksumStart
decl_stmt|;
specifier|final
name|int
name|pktLen
init|=
name|HdfsConstants
operator|.
name|BYTES_IN_INTEGER
operator|+
name|dataLen
operator|+
name|checksumLen
decl_stmt|;
name|PacketHeader
name|header
init|=
operator|new
name|PacketHeader
argument_list|(
name|pktLen
argument_list|,
name|offsetInBlock
argument_list|,
name|seqno
argument_list|,
name|lastPacketInBlock
argument_list|,
name|dataLen
argument_list|,
name|syncBlock
argument_list|)
decl_stmt|;
if|if
condition|(
name|checksumPos
operator|!=
name|dataStart
condition|)
block|{
comment|// Move the checksum to cover the gap. This can happen for the last
comment|// packet or during an hflush/hsync call.
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|checksumStart
argument_list|,
name|buf
argument_list|,
name|dataStart
operator|-
name|checksumLen
argument_list|,
name|checksumLen
argument_list|)
expr_stmt|;
name|checksumPos
operator|=
name|dataStart
expr_stmt|;
name|checksumStart
operator|=
name|checksumPos
operator|-
name|checksumLen
expr_stmt|;
block|}
specifier|final
name|int
name|headerStart
init|=
name|checksumStart
operator|-
name|header
operator|.
name|getSerializedSize
argument_list|()
decl_stmt|;
assert|assert
name|checksumStart
operator|+
literal|1
operator|>=
name|header
operator|.
name|getSerializedSize
argument_list|()
assert|;
assert|assert
name|headerStart
operator|>=
literal|0
assert|;
assert|assert
name|headerStart
operator|+
name|header
operator|.
name|getSerializedSize
argument_list|()
operator|==
name|checksumStart
assert|;
comment|// Copy the header data into the buffer immediately preceding the checksum
comment|// data.
name|System
operator|.
name|arraycopy
argument_list|(
name|header
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
name|headerStart
argument_list|,
name|header
operator|.
name|getSerializedSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// corrupt the data for testing.
if|if
condition|(
name|DFSClientFaultInjector
operator|.
name|get
argument_list|()
operator|.
name|corruptPacket
argument_list|()
condition|)
block|{
name|buf
index|[
name|headerStart
operator|+
name|header
operator|.
name|getSerializedSize
argument_list|()
operator|+
name|checksumLen
operator|+
name|dataLen
operator|-
literal|1
index|]
operator|^=
literal|0xff
expr_stmt|;
block|}
comment|// Write the now contiguous full packet to the output stream.
name|stm
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|headerStart
argument_list|,
name|header
operator|.
name|getSerializedSize
argument_list|()
operator|+
name|checksumLen
operator|+
name|dataLen
argument_list|)
expr_stmt|;
comment|// undo corruption.
if|if
condition|(
name|DFSClientFaultInjector
operator|.
name|get
argument_list|()
operator|.
name|uncorruptPacket
argument_list|()
condition|)
block|{
name|buf
index|[
name|headerStart
operator|+
name|header
operator|.
name|getSerializedSize
argument_list|()
operator|+
name|checksumLen
operator|+
name|dataLen
operator|-
literal|1
index|]
operator|^=
literal|0xff
expr_stmt|;
block|}
block|}
DECL|method|checkBuffer ()
specifier|private
specifier|synchronized
name|void
name|checkBuffer
parameter_list|()
throws|throws
name|ClosedChannelException
block|{
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ClosedChannelException
argument_list|()
throw|;
block|}
block|}
comment|/**    * Release the buffer in this packet to ByteArrayManager.    *    * @param bam    */
DECL|method|releaseBuffer (ByteArrayManager bam)
specifier|synchronized
name|void
name|releaseBuffer
parameter_list|(
name|ByteArrayManager
name|bam
parameter_list|)
block|{
name|bam
operator|.
name|release
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * get the packet's last byte's offset in the block    *    * @return the packet's last byte's offset in the block    */
DECL|method|getLastByteOffsetBlock ()
specifier|synchronized
name|long
name|getLastByteOffsetBlock
parameter_list|()
block|{
return|return
name|offsetInBlock
operator|+
name|dataPos
operator|-
name|dataStart
return|;
block|}
comment|/**    * Check if this packet is a heart beat packet    *    * @return true if the sequence number is HEART_BEAT_SEQNO    */
DECL|method|isHeartbeatPacket ()
name|boolean
name|isHeartbeatPacket
parameter_list|()
block|{
return|return
name|seqno
operator|==
name|HEART_BEAT_SEQNO
return|;
block|}
comment|/**    * check if this packet is the last packet in block    *    * @return true if the packet is the last packet    */
DECL|method|isLastPacketInBlock ()
name|boolean
name|isLastPacketInBlock
parameter_list|()
block|{
return|return
name|lastPacketInBlock
return|;
block|}
comment|/**    * get sequence number of this packet    *    * @return the sequence number of this packet    */
DECL|method|getSeqno ()
name|long
name|getSeqno
parameter_list|()
block|{
return|return
name|seqno
return|;
block|}
comment|/**    * get the number of chunks this packet contains    *    * @return the number of chunks in this packet    */
DECL|method|getNumChunks ()
specifier|synchronized
name|int
name|getNumChunks
parameter_list|()
block|{
return|return
name|numChunks
return|;
block|}
comment|/**    * increase the number of chunks by one    */
DECL|method|incNumChunks ()
specifier|synchronized
name|void
name|incNumChunks
parameter_list|()
block|{
name|numChunks
operator|++
expr_stmt|;
block|}
comment|/**    * get the maximum number of packets    *    * @return the maximum number of packets    */
DECL|method|getMaxChunks ()
name|int
name|getMaxChunks
parameter_list|()
block|{
return|return
name|maxChunks
return|;
block|}
comment|/**    * set if to sync block    *    * @param syncBlock if to sync block    */
DECL|method|setSyncBlock (boolean syncBlock)
specifier|synchronized
name|void
name|setSyncBlock
parameter_list|(
name|boolean
name|syncBlock
parameter_list|)
block|{
name|this
operator|.
name|syncBlock
operator|=
name|syncBlock
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"packet seqno: "
operator|+
name|this
operator|.
name|seqno
operator|+
literal|" offsetInBlock: "
operator|+
name|this
operator|.
name|offsetInBlock
operator|+
literal|" lastPacketInBlock: "
operator|+
name|this
operator|.
name|lastPacketInBlock
operator|+
literal|" lastByteOffsetInBlock: "
operator|+
name|this
operator|.
name|getLastByteOffsetBlock
argument_list|()
return|;
block|}
block|}
end_class

end_unit

