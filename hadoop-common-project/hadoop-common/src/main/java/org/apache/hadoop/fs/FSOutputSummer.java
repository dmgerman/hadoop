begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|zip
operator|.
name|Checksum
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

begin_comment
comment|/**  * This is a generic output stream for generating checksums for  * data before it is written to the underlying stream  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FSOutputSummer
specifier|abstract
specifier|public
class|class
name|FSOutputSummer
extends|extends
name|OutputStream
block|{
comment|// data checksum
DECL|field|sum
specifier|private
name|Checksum
name|sum
decl_stmt|;
comment|// internal buffer for storing data before it is checksumed
DECL|field|buf
specifier|private
name|byte
name|buf
index|[]
decl_stmt|;
comment|// internal buffer for storing checksum
DECL|field|checksum
specifier|private
name|byte
name|checksum
index|[]
decl_stmt|;
comment|// The number of valid bytes in the buffer.
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|method|FSOutputSummer (Checksum sum, int maxChunkSize, int checksumSize)
specifier|protected
name|FSOutputSummer
parameter_list|(
name|Checksum
name|sum
parameter_list|,
name|int
name|maxChunkSize
parameter_list|,
name|int
name|checksumSize
parameter_list|)
block|{
name|this
operator|.
name|sum
operator|=
name|sum
expr_stmt|;
name|this
operator|.
name|buf
operator|=
operator|new
name|byte
index|[
name|maxChunkSize
index|]
expr_stmt|;
name|this
operator|.
name|checksum
operator|=
operator|new
name|byte
index|[
name|checksumSize
index|]
expr_stmt|;
name|this
operator|.
name|count
operator|=
literal|0
expr_stmt|;
block|}
comment|/* write the data chunk in<code>b</code> staring at<code>offset</code> with    * a length of<code>len</code>, and its checksum    */
DECL|method|writeChunk (byte[] b, int offset, int len, byte[] checksum)
specifier|protected
specifier|abstract
name|void
name|writeChunk
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|byte
index|[]
name|checksum
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Write one byte */
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
name|sum
operator|.
name|update
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|buf
index|[
name|count
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|b
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|buf
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Writes<code>len</code> bytes from the specified byte array     * starting at offset<code>off</code> and generate a checksum for    * each data chunk.    *    *<p> This method stores bytes from the given array into this    * stream's buffer before it gets checksumed. The buffer gets checksumed     * and flushed to the underlying output stream when all data     * in a checksum chunk are in the buffer.  If the buffer is empty and    * requested length is at least as large as the size of next checksum chunk    * size, this method will checksum and write the chunk directly     * to the underlying output stream.  Thus it avoids uneccessary data copy.    *    * @param      b     the data.    * @param      off   the start offset in the data.    * @param      len   the number of bytes to write.    * @exception  IOException  if an I/O error occurs.    */
annotation|@
name|Override
DECL|method|write (byte b[], int off, int len)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|byte
name|b
index|[]
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
name|off
operator|<
literal|0
operator|||
name|len
argument_list|<
literal|0
operator|||
name|off
argument_list|>
name|b
operator|.
name|length
operator|-
name|len
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|()
throw|;
block|}
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|len
condition|;
name|n
operator|+=
name|write1
argument_list|(
name|b
argument_list|,
name|off
operator|+
name|n
argument_list|,
name|len
operator|-
name|n
argument_list|)
control|)
block|{     }
block|}
comment|/**    * Write a portion of an array, flushing to the underlying    * stream at most once if necessary.    */
DECL|method|write1 (byte b[], int off, int len)
specifier|private
name|int
name|write1
parameter_list|(
name|byte
name|b
index|[]
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
name|count
operator|==
literal|0
operator|&&
name|len
operator|>=
name|buf
operator|.
name|length
condition|)
block|{
comment|// local buffer is empty and user data has one chunk
comment|// checksum and output data
specifier|final
name|int
name|length
init|=
name|buf
operator|.
name|length
decl_stmt|;
name|sum
operator|.
name|update
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|writeChecksumChunk
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|length
return|;
block|}
comment|// copy user data to local buffer
name|int
name|bytesToCopy
init|=
name|buf
operator|.
name|length
operator|-
name|count
decl_stmt|;
name|bytesToCopy
operator|=
operator|(
name|len
operator|<
name|bytesToCopy
operator|)
condition|?
name|len
else|:
name|bytesToCopy
expr_stmt|;
name|sum
operator|.
name|update
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|buf
argument_list|,
name|count
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
name|count
operator|+=
name|bytesToCopy
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|buf
operator|.
name|length
condition|)
block|{
comment|// local buffer is full
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
return|return
name|bytesToCopy
return|;
block|}
comment|/* Forces any buffered output bytes to be checksumed and written out to    * the underlying output stream.     */
DECL|method|flushBuffer ()
specifier|protected
specifier|synchronized
name|void
name|flushBuffer
parameter_list|()
throws|throws
name|IOException
block|{
name|flushBuffer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/* Forces any buffered output bytes to be checksumed and written out to    * the underlying output stream.  If keep is true, then the state of     * this object remains intact.    */
DECL|method|flushBuffer (boolean keep)
specifier|protected
specifier|synchronized
name|void
name|flushBuffer
parameter_list|(
name|boolean
name|keep
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
name|int
name|chunkLen
init|=
name|count
decl_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|writeChecksumChunk
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|chunkLen
argument_list|,
name|keep
argument_list|)
expr_stmt|;
if|if
condition|(
name|keep
condition|)
block|{
name|count
operator|=
name|chunkLen
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Return the number of valid bytes currently in the buffer.    */
DECL|method|getBufferedDataSize ()
specifier|protected
specifier|synchronized
name|int
name|getBufferedDataSize
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/** Generate checksum for the data chunk and output data chunk& checksum    * to the underlying output stream. If keep is true then keep the    * current checksum intact, do not reset it.    */
DECL|method|writeChecksumChunk (byte b[], int off, int len, boolean keep)
specifier|private
name|void
name|writeChecksumChunk
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|boolean
name|keep
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|tempChecksum
init|=
operator|(
name|int
operator|)
name|sum
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|keep
condition|)
block|{
name|sum
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|int2byte
argument_list|(
name|tempChecksum
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
name|writeChunk
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
block|}
comment|/**    * Converts a checksum integer value to a byte stream    */
DECL|method|convertToByteStream (Checksum sum, int checksumSize)
specifier|static
specifier|public
name|byte
index|[]
name|convertToByteStream
parameter_list|(
name|Checksum
name|sum
parameter_list|,
name|int
name|checksumSize
parameter_list|)
block|{
return|return
name|int2byte
argument_list|(
operator|(
name|int
operator|)
name|sum
operator|.
name|getValue
argument_list|()
argument_list|,
operator|new
name|byte
index|[
name|checksumSize
index|]
argument_list|)
return|;
block|}
DECL|method|int2byte (int integer, byte[] bytes)
specifier|static
name|byte
index|[]
name|int2byte
parameter_list|(
name|int
name|integer
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|integer
operator|>>>
literal|24
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|integer
operator|>>>
literal|16
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|integer
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|integer
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
comment|/**    * Resets existing buffer with a new one of the specified size.    */
DECL|method|resetChecksumChunk (int size)
specifier|protected
specifier|synchronized
name|void
name|resetChecksumChunk
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|sum
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|buf
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
name|this
operator|.
name|count
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

