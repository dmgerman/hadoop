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
name|*
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
comment|/** Utility that wraps a {@link FSInputStream} in a {@link DataInputStream}  * and buffers input through a {@link BufferedInputStream}. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|FSDataInputStream
specifier|public
class|class
name|FSDataInputStream
extends|extends
name|DataInputStream
implements|implements
name|Seekable
implements|,
name|PositionedReadable
implements|,
name|Closeable
implements|,
name|ByteBufferReadable
block|{
DECL|method|FSDataInputStream (InputStream in)
specifier|public
name|FSDataInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|in
operator|instanceof
name|Seekable
operator|)
operator|||
operator|!
operator|(
name|in
operator|instanceof
name|PositionedReadable
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"In is not an instance of Seekable or PositionedReadable"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Seek to the given offset.    *    * @param desired offset to seek to    */
DECL|method|seek (long desired)
specifier|public
specifier|synchronized
name|void
name|seek
parameter_list|(
name|long
name|desired
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|Seekable
operator|)
name|in
operator|)
operator|.
name|seek
argument_list|(
name|desired
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the current position in the input stream.    *    * @return current position in the input stream    */
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|Seekable
operator|)
name|in
operator|)
operator|.
name|getPos
argument_list|()
return|;
block|}
comment|/**    * Read bytes from the given position in the stream to the given buffer.    *    * @param position  position in the input stream to seek    * @param buffer    buffer into which data is read    * @param offset    offset into the buffer in which data is written    * @param length    maximum number of bytes to read    * @return total number of bytes read into the buffer, or<code>-1</code>    *         if there is no more data because the end of the stream has been    *         reached    */
DECL|method|read (long position, byte[] buffer, int offset, int length)
specifier|public
name|int
name|read
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|PositionedReadable
operator|)
name|in
operator|)
operator|.
name|read
argument_list|(
name|position
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * Read bytes from the given position in the stream to the given buffer.    * Continues to read until<code>length</code> bytes have been read.    *    * @param position  position in the input stream to seek    * @param buffer    buffer into which data is read    * @param offset    offset into the buffer in which data is written    * @param length    the number of bytes to read    * @throws EOFException If the end of stream is reached while reading.    *                      If an exception is thrown an undetermined number    *                      of bytes in the buffer may have been written.     */
DECL|method|readFully (long position, byte[] buffer, int offset, int length)
specifier|public
name|void
name|readFully
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|PositionedReadable
operator|)
name|in
operator|)
operator|.
name|readFully
argument_list|(
name|position
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * See {@link #readFully(long, byte[], int, int)}.    */
DECL|method|readFully (long position, byte[] buffer)
specifier|public
name|void
name|readFully
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|PositionedReadable
operator|)
name|in
operator|)
operator|.
name|readFully
argument_list|(
name|position
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Seek to the given position on an alternate copy of the data.    *    * @param  targetPos  position to seek to    * @return true if a new source is found, false otherwise    */
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
operator|(
operator|(
name|Seekable
operator|)
name|in
operator|)
operator|.
name|seekToNewSource
argument_list|(
name|targetPos
argument_list|)
return|;
block|}
comment|/**    * Get a reference to the wrapped input stream. Used by unit tests.    *    * @return the underlying input stream    */
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
DECL|method|getWrappedStream ()
specifier|public
name|InputStream
name|getWrappedStream
parameter_list|()
block|{
return|return
name|in
return|;
block|}
DECL|method|read (ByteBuffer buf)
specifier|public
name|int
name|read
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|instanceof
name|ByteBufferReadable
condition|)
block|{
return|return
operator|(
operator|(
name|ByteBufferReadable
operator|)
name|in
operator|)
operator|.
name|read
argument_list|(
name|buf
argument_list|)
return|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Byte-buffer read unsupported by input stream"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

