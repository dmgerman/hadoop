begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
package|;
end_package

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
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|FSExceptionMessages
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
name|FSInputStream
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
name|FileSystem
operator|.
name|Statistics
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
name|fs
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|Constants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The input stream for OSS blob system.  * The class uses multi-part downloading to read data from the object content  * stream.  */
end_comment

begin_class
DECL|class|AliyunOSSInputStream
specifier|public
class|class
name|AliyunOSSInputStream
extends|extends
name|FSInputStream
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AliyunOSSInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|downloadPartSize
specifier|private
specifier|final
name|long
name|downloadPartSize
decl_stmt|;
DECL|field|store
specifier|private
name|AliyunOSSFileSystemStore
name|store
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|statistics
specifier|private
name|Statistics
name|statistics
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|contentLength
specifier|private
name|long
name|contentLength
decl_stmt|;
DECL|field|position
specifier|private
name|long
name|position
decl_stmt|;
DECL|field|partRemaining
specifier|private
name|long
name|partRemaining
decl_stmt|;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|maxReadAheadPartNumber
specifier|private
name|int
name|maxReadAheadPartNumber
decl_stmt|;
DECL|field|expectNextPos
specifier|private
name|long
name|expectNextPos
decl_stmt|;
DECL|field|lastByteStart
specifier|private
name|long
name|lastByteStart
decl_stmt|;
DECL|field|readAheadExecutorService
specifier|private
name|ExecutorService
name|readAheadExecutorService
decl_stmt|;
DECL|field|readBufferQueue
specifier|private
name|Queue
argument_list|<
name|ReadBuffer
argument_list|>
name|readBufferQueue
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|AliyunOSSInputStream (Configuration conf, ExecutorService readAheadExecutorService, int maxReadAheadPartNumber, AliyunOSSFileSystemStore store, String key, Long contentLength, Statistics statistics)
specifier|public
name|AliyunOSSInputStream
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ExecutorService
name|readAheadExecutorService
parameter_list|,
name|int
name|maxReadAheadPartNumber
parameter_list|,
name|AliyunOSSFileSystemStore
name|store
parameter_list|,
name|String
name|key
parameter_list|,
name|Long
name|contentLength
parameter_list|,
name|Statistics
name|statistics
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|readAheadExecutorService
operator|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|readAheadExecutorService
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
name|this
operator|.
name|contentLength
operator|=
name|contentLength
expr_stmt|;
name|downloadPartSize
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|MULTIPART_DOWNLOAD_SIZE_KEY
argument_list|,
name|MULTIPART_DOWNLOAD_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxReadAheadPartNumber
operator|=
name|maxReadAheadPartNumber
expr_stmt|;
name|this
operator|.
name|expectNextPos
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastByteStart
operator|=
operator|-
literal|1
expr_stmt|;
name|reopen
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Reopen the wrapped stream at give position, by seeking for    * data of a part length from object content stream.    *    * @param pos position from start of a file    * @throws IOException if failed to reopen    */
DECL|method|reopen (long pos)
specifier|private
specifier|synchronized
name|void
name|reopen
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|partSize
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Cannot seek at negative position:"
operator|+
name|pos
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|>
name|contentLength
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Cannot seek after EOF, contentLength:"
operator|+
name|contentLength
operator|+
literal|" position:"
operator|+
name|pos
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|+
name|downloadPartSize
operator|>
name|contentLength
condition|)
block|{
name|partSize
operator|=
name|contentLength
operator|-
name|pos
expr_stmt|;
block|}
else|else
block|{
name|partSize
operator|=
name|downloadPartSize
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|buffer
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Aborting old stream to open at pos "
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
name|boolean
name|isRandomIO
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|pos
operator|==
name|this
operator|.
name|expectNextPos
condition|)
block|{
name|isRandomIO
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|//new seek, remove cache buffers if its byteStart is not equal to pos
while|while
condition|(
name|readBufferQueue
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|readBufferQueue
operator|.
name|element
argument_list|()
operator|.
name|getByteStart
argument_list|()
operator|!=
name|pos
condition|)
block|{
name|readBufferQueue
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
name|this
operator|.
name|expectNextPos
operator|=
name|pos
operator|+
name|partSize
expr_stmt|;
name|int
name|currentSize
init|=
name|readBufferQueue
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentSize
operator|==
literal|0
condition|)
block|{
comment|//init lastByteStart to pos - partSize, used by for loop below
name|lastByteStart
operator|=
name|pos
operator|-
name|partSize
expr_stmt|;
block|}
else|else
block|{
name|ReadBuffer
index|[]
name|readBuffers
init|=
name|readBufferQueue
operator|.
name|toArray
argument_list|(
operator|new
name|ReadBuffer
index|[
name|currentSize
index|]
argument_list|)
decl_stmt|;
name|lastByteStart
operator|=
name|readBuffers
index|[
name|currentSize
operator|-
literal|1
index|]
operator|.
name|getByteStart
argument_list|()
expr_stmt|;
block|}
name|int
name|maxLen
init|=
name|this
operator|.
name|maxReadAheadPartNumber
operator|-
name|currentSize
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
name|maxLen
operator|&&
name|i
operator|<
operator|(
name|currentSize
operator|+
literal|1
operator|)
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|lastByteStart
operator|+
name|partSize
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
operator|>
name|contentLength
condition|)
block|{
break|break;
block|}
name|long
name|byteStart
init|=
name|lastByteStart
operator|+
name|partSize
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
decl_stmt|;
name|long
name|byteEnd
init|=
name|byteStart
operator|+
name|partSize
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|byteEnd
operator|>=
name|contentLength
condition|)
block|{
name|byteEnd
operator|=
name|contentLength
operator|-
literal|1
expr_stmt|;
block|}
name|ReadBuffer
name|readBuffer
init|=
operator|new
name|ReadBuffer
argument_list|(
name|byteStart
argument_list|,
name|byteEnd
argument_list|)
decl_stmt|;
if|if
condition|(
name|readBuffer
operator|.
name|getBuffer
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|//EOF
name|readBuffer
operator|.
name|setStatus
argument_list|(
name|ReadBuffer
operator|.
name|STATUS
operator|.
name|SUCCESS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|readAheadExecutorService
operator|.
name|execute
argument_list|(
operator|new
name|AliyunOSSFileReaderTask
argument_list|(
name|key
argument_list|,
name|store
argument_list|,
name|readBuffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|readBufferQueue
operator|.
name|add
argument_list|(
name|readBuffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|isRandomIO
condition|)
block|{
break|break;
block|}
block|}
name|ReadBuffer
name|readBuffer
init|=
name|readBufferQueue
operator|.
name|poll
argument_list|()
decl_stmt|;
name|readBuffer
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|readBuffer
operator|.
name|await
argument_list|(
name|ReadBuffer
operator|.
name|STATUS
operator|.
name|INIT
argument_list|)
expr_stmt|;
if|if
condition|(
name|readBuffer
operator|.
name|getStatus
argument_list|()
operator|==
name|ReadBuffer
operator|.
name|STATUS
operator|.
name|ERROR
condition|)
block|{
name|this
operator|.
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|buffer
operator|=
name|readBuffer
operator|.
name|getBuffer
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"interrupted when wait a read buffer"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|readBuffer
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|buffer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Null IO stream"
argument_list|)
throw|;
block|}
name|position
operator|=
name|pos
expr_stmt|;
name|partRemaining
operator|=
name|partSize
expr_stmt|;
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
name|checkNotClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|partRemaining
operator|<=
literal|0
operator|&&
name|position
operator|<
name|contentLength
condition|)
block|{
name|reopen
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
name|int
name|byteRead
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|partRemaining
operator|!=
literal|0
condition|)
block|{
name|byteRead
operator|=
name|this
operator|.
name|buffer
index|[
name|this
operator|.
name|buffer
operator|.
name|length
operator|-
operator|(
name|int
operator|)
name|partRemaining
index|]
operator|&
literal|0xFF
expr_stmt|;
block|}
if|if
condition|(
name|byteRead
operator|>=
literal|0
condition|)
block|{
name|position
operator|++
expr_stmt|;
name|partRemaining
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|statistics
operator|!=
literal|null
operator|&&
name|byteRead
operator|>=
literal|0
condition|)
block|{
name|statistics
operator|.
name|incrementBytesRead
argument_list|(
name|byteRead
argument_list|)
expr_stmt|;
block|}
return|return
name|byteRead
return|;
block|}
comment|/**    * Verify that the input stream is open. Non blocking; this gives    * the last state of the volatile {@link #closed} field.    *    * @throws IOException if the connection is closed.    */
DECL|method|checkNotClosed ()
specifier|private
name|void
name|checkNotClosed
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|FSExceptionMessages
operator|.
name|STREAM_IS_CLOSED
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|read (byte[] buf, int off, int len)
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|(
name|byte
index|[]
name|buf
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
name|checkNotClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|buf
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
elseif|else
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
name|buf
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
elseif|else
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
name|int
name|bytesRead
init|=
literal|0
decl_stmt|;
comment|// Not EOF, and read not done
while|while
condition|(
name|position
operator|<
name|contentLength
operator|&&
name|bytesRead
operator|<
name|len
condition|)
block|{
if|if
condition|(
name|partRemaining
operator|==
literal|0
condition|)
block|{
name|reopen
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
name|int
name|bytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|this
operator|.
name|buffer
operator|.
name|length
operator|-
operator|(
name|int
operator|)
name|partRemaining
init|;
name|i
operator|<
name|this
operator|.
name|buffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buf
index|[
name|off
operator|+
name|bytesRead
index|]
operator|=
name|this
operator|.
name|buffer
index|[
name|i
index|]
expr_stmt|;
name|bytes
operator|++
expr_stmt|;
name|bytesRead
operator|++
expr_stmt|;
if|if
condition|(
name|off
operator|+
name|bytesRead
operator|>=
name|len
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|bytes
operator|>
literal|0
condition|)
block|{
name|position
operator|+=
name|bytes
expr_stmt|;
name|partRemaining
operator|-=
name|bytes
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|partRemaining
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to read from stream. Remaining:"
operator|+
name|partRemaining
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|statistics
operator|!=
literal|null
operator|&&
name|bytesRead
operator|>
literal|0
condition|)
block|{
name|statistics
operator|.
name|incrementBytesRead
argument_list|(
name|bytesRead
argument_list|)
expr_stmt|;
block|}
comment|// Read nothing, but attempt to read something
if|if
condition|(
name|bytesRead
operator|==
literal|0
operator|&&
name|len
operator|>
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|bytesRead
return|;
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
name|closed
condition|)
block|{
return|return;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|available ()
specifier|public
specifier|synchronized
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
name|long
name|remaining
init|=
name|contentLength
operator|-
name|position
decl_stmt|;
if|if
condition|(
name|remaining
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
return|return
operator|(
name|int
operator|)
name|remaining
return|;
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
name|checkNotClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|position
operator|==
name|pos
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|pos
operator|>
name|position
operator|&&
name|pos
operator|<
name|position
operator|+
name|partRemaining
condition|)
block|{
name|long
name|len
init|=
name|pos
operator|-
name|position
decl_stmt|;
name|position
operator|=
name|pos
expr_stmt|;
name|partRemaining
operator|-=
name|len
expr_stmt|;
block|}
else|else
block|{
name|reopen
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
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
name|checkNotClosed
argument_list|()
expr_stmt|;
return|return
name|position
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
name|checkNotClosed
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|getExpectNextPos ()
specifier|public
name|long
name|getExpectNextPos
parameter_list|()
block|{
return|return
name|this
operator|.
name|expectNextPos
return|;
block|}
block|}
end_class

end_unit

