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

begin_comment
comment|/**  * Wraps different possible read implementations so that callers can be  * strategy-agnostic.  */
end_comment

begin_interface
DECL|interface|ReaderStrategy
interface|interface
name|ReaderStrategy
block|{
comment|/**    * Read from a block using the blockReader.    * @param blockReader    * @return number of bytes read    * @throws IOException    */
DECL|method|readFromBlock (BlockReader blockReader)
name|int
name|readFromBlock
parameter_list|(
name|BlockReader
name|blockReader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read from a block using the blockReader with desired length to read.    * @param blockReader    * @param length number of bytes desired to read, not ensured    * @return number of bytes read    * @throws IOException    */
DECL|method|readFromBlock (BlockReader blockReader, int length)
name|int
name|readFromBlock
parameter_list|(
name|BlockReader
name|blockReader
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read or copy from a src buffer.    * @param src    * @return number of bytes copied    * Note: the position of the src buffer is not changed after the call    */
DECL|method|readFromBuffer (ByteBuffer src)
name|int
name|readFromBuffer
parameter_list|(
name|ByteBuffer
name|src
parameter_list|)
function_decl|;
comment|/**    * Read or copy length of data bytes from a src buffer with desired length.    * @param src    * @return number of bytes copied    * Note: the position of the src buffer is not changed after the call    */
DECL|method|readFromBuffer (ByteBuffer src, int length)
name|int
name|readFromBuffer
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/**    * @return the target read buffer that reads data into.    */
DECL|method|getReadBuffer ()
name|ByteBuffer
name|getReadBuffer
parameter_list|()
function_decl|;
comment|/**    * @return the target length to read.    */
DECL|method|getTargetLength ()
name|int
name|getTargetLength
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|/**  * Used to read bytes into a byte array buffer. Note it's not thread-safe  * and the behavior is not defined if concurrently operated.  */
end_comment

begin_class
DECL|class|ByteArrayStrategy
class|class
name|ByteArrayStrategy
implements|implements
name|ReaderStrategy
block|{
DECL|field|dfsClient
specifier|private
specifier|final
name|DFSClient
name|dfsClient
decl_stmt|;
DECL|field|readStatistics
specifier|private
specifier|final
name|ReadStatistics
name|readStatistics
decl_stmt|;
DECL|field|readBuf
specifier|private
specifier|final
name|byte
index|[]
name|readBuf
decl_stmt|;
DECL|field|offset
specifier|private
name|int
name|offset
decl_stmt|;
DECL|field|targetLength
specifier|private
specifier|final
name|int
name|targetLength
decl_stmt|;
comment|/**    * The constructor.    * @param readBuf target buffer to read into    * @param offset offset into the buffer    * @param targetLength target length of data    * @param readStatistics statistics counter    */
DECL|method|ByteArrayStrategy (byte[] readBuf, int offset, int targetLength, ReadStatistics readStatistics, DFSClient dfsClient)
specifier|public
name|ByteArrayStrategy
parameter_list|(
name|byte
index|[]
name|readBuf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|targetLength
parameter_list|,
name|ReadStatistics
name|readStatistics
parameter_list|,
name|DFSClient
name|dfsClient
parameter_list|)
block|{
name|this
operator|.
name|readBuf
operator|=
name|readBuf
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|targetLength
operator|=
name|targetLength
expr_stmt|;
name|this
operator|.
name|readStatistics
operator|=
name|readStatistics
expr_stmt|;
name|this
operator|.
name|dfsClient
operator|=
name|dfsClient
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReadBuffer ()
specifier|public
name|ByteBuffer
name|getReadBuffer
parameter_list|()
block|{
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|readBuf
argument_list|,
name|offset
argument_list|,
name|targetLength
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTargetLength ()
specifier|public
name|int
name|getTargetLength
parameter_list|()
block|{
return|return
name|targetLength
return|;
block|}
annotation|@
name|Override
DECL|method|readFromBlock (BlockReader blockReader)
specifier|public
name|int
name|readFromBlock
parameter_list|(
name|BlockReader
name|blockReader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFromBlock
argument_list|(
name|blockReader
argument_list|,
name|targetLength
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readFromBlock (BlockReader blockReader, int length)
specifier|public
name|int
name|readFromBlock
parameter_list|(
name|BlockReader
name|blockReader
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|nRead
init|=
name|blockReader
operator|.
name|read
argument_list|(
name|readBuf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|nRead
operator|>
literal|0
condition|)
block|{
name|offset
operator|+=
name|nRead
expr_stmt|;
block|}
return|return
name|nRead
return|;
block|}
annotation|@
name|Override
DECL|method|readFromBuffer (ByteBuffer src)
specifier|public
name|int
name|readFromBuffer
parameter_list|(
name|ByteBuffer
name|src
parameter_list|)
block|{
return|return
name|readFromBuffer
argument_list|(
name|src
argument_list|,
name|src
operator|.
name|remaining
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readFromBuffer (ByteBuffer src, int length)
specifier|public
name|int
name|readFromBuffer
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|ByteBuffer
name|dup
init|=
name|src
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|dup
operator|.
name|get
argument_list|(
name|readBuf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|length
expr_stmt|;
return|return
name|length
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * Used to read bytes into a user-supplied ByteBuffer. Note it's not thread-safe  * and the behavior is not defined if concurrently operated. When read operation  * is performed, the position of the underlying byte buffer will move forward as  * stated in ByteBufferReadable#read(ByteBuffer buf) method.  */
end_comment

begin_class
DECL|class|ByteBufferStrategy
class|class
name|ByteBufferStrategy
implements|implements
name|ReaderStrategy
block|{
DECL|field|dfsClient
specifier|private
specifier|final
name|DFSClient
name|dfsClient
decl_stmt|;
DECL|field|readStatistics
specifier|private
specifier|final
name|ReadStatistics
name|readStatistics
decl_stmt|;
DECL|field|readBuf
specifier|private
specifier|final
name|ByteBuffer
name|readBuf
decl_stmt|;
DECL|field|targetLength
specifier|private
specifier|final
name|int
name|targetLength
decl_stmt|;
comment|/**    * The constructor.    * @param readBuf target buffer to read into    * @param readStatistics statistics counter    */
DECL|method|ByteBufferStrategy (ByteBuffer readBuf, ReadStatistics readStatistics, DFSClient dfsClient)
name|ByteBufferStrategy
parameter_list|(
name|ByteBuffer
name|readBuf
parameter_list|,
name|ReadStatistics
name|readStatistics
parameter_list|,
name|DFSClient
name|dfsClient
parameter_list|)
block|{
name|this
operator|.
name|readBuf
operator|=
name|readBuf
expr_stmt|;
name|this
operator|.
name|targetLength
operator|=
name|readBuf
operator|.
name|remaining
argument_list|()
expr_stmt|;
name|this
operator|.
name|readStatistics
operator|=
name|readStatistics
expr_stmt|;
name|this
operator|.
name|dfsClient
operator|=
name|dfsClient
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReadBuffer ()
specifier|public
name|ByteBuffer
name|getReadBuffer
parameter_list|()
block|{
return|return
name|readBuf
return|;
block|}
annotation|@
name|Override
DECL|method|readFromBlock (BlockReader blockReader)
specifier|public
name|int
name|readFromBlock
parameter_list|(
name|BlockReader
name|blockReader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFromBlock
argument_list|(
name|blockReader
argument_list|,
name|readBuf
operator|.
name|remaining
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readFromBlock (BlockReader blockReader, int length)
specifier|public
name|int
name|readFromBlock
parameter_list|(
name|BlockReader
name|blockReader
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|tmpBuf
init|=
name|readBuf
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|tmpBuf
operator|.
name|limit
argument_list|(
name|tmpBuf
operator|.
name|position
argument_list|()
operator|+
name|length
argument_list|)
expr_stmt|;
name|int
name|nRead
init|=
name|blockReader
operator|.
name|read
argument_list|(
name|tmpBuf
argument_list|)
decl_stmt|;
comment|// Only when data are read, update the position
if|if
condition|(
name|nRead
operator|>
literal|0
condition|)
block|{
name|readBuf
operator|.
name|position
argument_list|(
name|readBuf
operator|.
name|position
argument_list|()
operator|+
name|nRead
argument_list|)
expr_stmt|;
block|}
return|return
name|nRead
return|;
block|}
annotation|@
name|Override
DECL|method|getTargetLength ()
specifier|public
name|int
name|getTargetLength
parameter_list|()
block|{
return|return
name|targetLength
return|;
block|}
annotation|@
name|Override
DECL|method|readFromBuffer (ByteBuffer src)
specifier|public
name|int
name|readFromBuffer
parameter_list|(
name|ByteBuffer
name|src
parameter_list|)
block|{
return|return
name|readFromBuffer
argument_list|(
name|src
argument_list|,
name|src
operator|.
name|remaining
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readFromBuffer (ByteBuffer src, int length)
specifier|public
name|int
name|readFromBuffer
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|ByteBuffer
name|dup
init|=
name|src
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|int
name|newLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|readBuf
operator|.
name|remaining
argument_list|()
argument_list|,
name|dup
operator|.
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
name|newLen
operator|=
name|Math
operator|.
name|min
argument_list|(
name|newLen
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|dup
operator|.
name|limit
argument_list|(
name|dup
operator|.
name|position
argument_list|()
operator|+
name|newLen
argument_list|)
expr_stmt|;
name|readBuf
operator|.
name|put
argument_list|(
name|dup
argument_list|)
expr_stmt|;
return|return
name|newLen
return|;
block|}
block|}
end_class

end_unit

