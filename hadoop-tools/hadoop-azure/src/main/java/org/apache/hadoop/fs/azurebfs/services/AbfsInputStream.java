begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|services
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
name|FileNotFoundException
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
name|net
operator|.
name|HttpURLConnection
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|AbfsRestOperationException
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
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|AzureBlobFileSystemException
import|;
end_import

begin_comment
comment|/**  * The AbfsInputStream for AbfsClient.  */
end_comment

begin_class
DECL|class|AbfsInputStream
specifier|public
class|class
name|AbfsInputStream
extends|extends
name|FSInputStream
block|{
DECL|field|client
specifier|private
specifier|final
name|AbfsClient
name|client
decl_stmt|;
DECL|field|statistics
specifier|private
specifier|final
name|Statistics
name|statistics
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|contentLength
specifier|private
specifier|final
name|long
name|contentLength
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
comment|// default buffer size
DECL|field|readAheadQueueDepth
specifier|private
specifier|final
name|int
name|readAheadQueueDepth
decl_stmt|;
comment|// initialized in constructor
DECL|field|eTag
specifier|private
specifier|final
name|String
name|eTag
decl_stmt|;
comment|// eTag of the path when InputStream are created
DECL|field|tolerateOobAppends
specifier|private
specifier|final
name|boolean
name|tolerateOobAppends
decl_stmt|;
comment|// whether tolerate Oob Appends
DECL|field|readAheadEnabled
specifier|private
specifier|final
name|boolean
name|readAheadEnabled
decl_stmt|;
comment|// whether enable readAhead;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
init|=
literal|null
decl_stmt|;
comment|// will be initialized on first use
DECL|field|fCursor
specifier|private
name|long
name|fCursor
init|=
literal|0
decl_stmt|;
comment|// cursor of buffer within file - offset of next byte to read from remote server
DECL|field|fCursorAfterLastRead
specifier|private
name|long
name|fCursorAfterLastRead
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|bCursor
specifier|private
name|int
name|bCursor
init|=
literal|0
decl_stmt|;
comment|// cursor of read within buffer - offset of next byte to be returned from buffer
DECL|field|limit
specifier|private
name|int
name|limit
init|=
literal|0
decl_stmt|;
comment|// offset of next byte to be read into buffer from service (i.e., upper marker+1
comment|//                                                      of valid bytes in buffer)
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|AbfsInputStream ( final AbfsClient client, final Statistics statistics, final String path, final long contentLength, final int bufferSize, final int readAheadQueueDepth, final boolean tolerateOobAppends, final String eTag)
specifier|public
name|AbfsInputStream
parameter_list|(
specifier|final
name|AbfsClient
name|client
parameter_list|,
specifier|final
name|Statistics
name|statistics
parameter_list|,
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|long
name|contentLength
parameter_list|,
specifier|final
name|int
name|bufferSize
parameter_list|,
specifier|final
name|int
name|readAheadQueueDepth
parameter_list|,
specifier|final
name|boolean
name|tolerateOobAppends
parameter_list|,
specifier|final
name|String
name|eTag
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|contentLength
operator|=
name|contentLength
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|this
operator|.
name|readAheadQueueDepth
operator|=
operator|(
name|readAheadQueueDepth
operator|>=
literal|0
operator|)
condition|?
name|readAheadQueueDepth
else|:
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
expr_stmt|;
name|this
operator|.
name|tolerateOobAppends
operator|=
name|tolerateOobAppends
expr_stmt|;
name|this
operator|.
name|eTag
operator|=
name|eTag
expr_stmt|;
name|this
operator|.
name|readAheadEnabled
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|int
name|numberOfBytesRead
init|=
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|numberOfBytesRead
operator|<
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
operator|(
name|b
index|[
literal|0
index|]
operator|&
literal|0xFF
operator|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|read (final byte[] b, final int off, final int len)
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|currentOff
init|=
name|off
decl_stmt|;
name|int
name|currentLen
init|=
name|len
decl_stmt|;
name|int
name|lastReadBytes
decl_stmt|;
name|int
name|totalReadBytes
init|=
literal|0
decl_stmt|;
do|do
block|{
name|lastReadBytes
operator|=
name|readOneBlock
argument_list|(
name|b
argument_list|,
name|currentOff
argument_list|,
name|currentLen
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastReadBytes
operator|>
literal|0
condition|)
block|{
name|currentOff
operator|+=
name|lastReadBytes
expr_stmt|;
name|currentLen
operator|-=
name|lastReadBytes
expr_stmt|;
name|totalReadBytes
operator|+=
name|lastReadBytes
expr_stmt|;
block|}
if|if
condition|(
name|currentLen
operator|<=
literal|0
operator|||
name|currentLen
operator|>
name|b
operator|.
name|length
operator|-
name|currentOff
condition|)
block|{
break|break;
block|}
block|}
do|while
condition|(
name|lastReadBytes
operator|>
literal|0
condition|)
do|;
return|return
name|totalReadBytes
operator|>
literal|0
condition|?
name|totalReadBytes
else|:
name|lastReadBytes
return|;
block|}
DECL|method|readOneBlock (final byte[] b, final int off, final int len)
specifier|private
name|int
name|readOneBlock
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|available
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
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
comment|//If buffer is empty, then fill the buffer.
if|if
condition|(
name|bCursor
operator|==
name|limit
condition|)
block|{
comment|//If EOF, then return -1
if|if
condition|(
name|fCursor
operator|>=
name|contentLength
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|long
name|bytesRead
init|=
literal|0
decl_stmt|;
comment|//reset buffer to initial state - i.e., throw away existing data
name|bCursor
operator|=
literal|0
expr_stmt|;
name|limit
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
name|bufferSize
index|]
expr_stmt|;
block|}
comment|// Enable readAhead when reading sequentially
if|if
condition|(
operator|-
literal|1
operator|==
name|fCursorAfterLastRead
operator|||
name|fCursorAfterLastRead
operator|==
name|fCursor
operator|||
name|b
operator|.
name|length
operator|>=
name|bufferSize
condition|)
block|{
name|bytesRead
operator|=
name|readInternal
argument_list|(
name|fCursor
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferSize
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bytesRead
operator|=
name|readInternal
argument_list|(
name|fCursor
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bytesRead
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|limit
operator|+=
name|bytesRead
expr_stmt|;
name|fCursor
operator|+=
name|bytesRead
expr_stmt|;
name|fCursorAfterLastRead
operator|=
name|fCursor
expr_stmt|;
block|}
comment|//If there is anything in the buffer, then return lesser of (requested bytes) and (bytes in buffer)
comment|//(bytes returned may be less than requested)
name|int
name|bytesRemaining
init|=
name|limit
operator|-
name|bCursor
decl_stmt|;
name|int
name|bytesToRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|bytesRemaining
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bCursor
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|bytesToRead
argument_list|)
expr_stmt|;
name|bCursor
operator|+=
name|bytesToRead
expr_stmt|;
if|if
condition|(
name|statistics
operator|!=
literal|null
condition|)
block|{
name|statistics
operator|.
name|incrementBytesRead
argument_list|(
name|bytesToRead
argument_list|)
expr_stmt|;
block|}
return|return
name|bytesToRead
return|;
block|}
DECL|method|readInternal (final long position, final byte[] b, final int offset, final int length, final boolean bypassReadAhead)
specifier|private
name|int
name|readInternal
parameter_list|(
specifier|final
name|long
name|position
parameter_list|,
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|,
specifier|final
name|boolean
name|bypassReadAhead
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|readAheadEnabled
operator|&&
operator|!
name|bypassReadAhead
condition|)
block|{
comment|// try reading from read-ahead
if|if
condition|(
name|offset
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"readahead buffers cannot have non-zero buffer offsets"
argument_list|)
throw|;
block|}
name|int
name|receivedBytes
decl_stmt|;
comment|// queue read-aheads
name|int
name|numReadAheads
init|=
name|this
operator|.
name|readAheadQueueDepth
decl_stmt|;
name|long
name|nextSize
decl_stmt|;
name|long
name|nextOffset
init|=
name|position
decl_stmt|;
while|while
condition|(
name|numReadAheads
operator|>
literal|0
operator|&&
name|nextOffset
operator|<
name|contentLength
condition|)
block|{
name|nextSize
operator|=
name|Math
operator|.
name|min
argument_list|(
operator|(
name|long
operator|)
name|bufferSize
argument_list|,
name|contentLength
operator|-
name|nextOffset
argument_list|)
expr_stmt|;
name|ReadBufferManager
operator|.
name|getBufferManager
argument_list|()
operator|.
name|queueReadAhead
argument_list|(
name|this
argument_list|,
name|nextOffset
argument_list|,
operator|(
name|int
operator|)
name|nextSize
argument_list|)
expr_stmt|;
name|nextOffset
operator|=
name|nextOffset
operator|+
name|nextSize
expr_stmt|;
name|numReadAheads
operator|--
expr_stmt|;
block|}
comment|// try reading from buffers first
name|receivedBytes
operator|=
name|ReadBufferManager
operator|.
name|getBufferManager
argument_list|()
operator|.
name|getBlock
argument_list|(
name|this
argument_list|,
name|position
argument_list|,
name|length
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|receivedBytes
operator|>
literal|0
condition|)
block|{
return|return
name|receivedBytes
return|;
block|}
comment|// got nothing from read-ahead, do our own read now
name|receivedBytes
operator|=
name|readRemote
argument_list|(
name|position
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|receivedBytes
return|;
block|}
else|else
block|{
return|return
name|readRemote
argument_list|(
name|position
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
DECL|method|readRemote (long position, byte[] b, int offset, int length)
name|int
name|readRemote
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|b
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
if|if
condition|(
name|position
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"attempting to read from negative offset"
argument_list|)
throw|;
block|}
if|if
condition|(
name|position
operator|>=
name|contentLength
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// Hadoop prefers -1 to EOFException
block|}
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null byte array passed in to read() method"
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|>=
name|b
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"offset greater than length of array"
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"requested read length is less than zero"
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|>
operator|(
name|b
operator|.
name|length
operator|-
name|offset
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"requested read length is more than will fit after requested offset in buffer"
argument_list|)
throw|;
block|}
specifier|final
name|AbfsRestOperation
name|op
decl_stmt|;
name|AbfsPerfTracker
name|tracker
init|=
name|client
operator|.
name|getAbfsPerfTracker
argument_list|()
decl_stmt|;
try|try
init|(
name|AbfsPerfInfo
name|perfInfo
init|=
operator|new
name|AbfsPerfInfo
argument_list|(
name|tracker
argument_list|,
literal|"readRemote"
argument_list|,
literal|"read"
argument_list|)
init|)
block|{
name|op
operator|=
name|client
operator|.
name|read
argument_list|(
name|path
argument_list|,
name|position
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|tolerateOobAppends
condition|?
literal|"*"
else|:
name|eTag
argument_list|)
expr_stmt|;
name|perfInfo
operator|.
name|registerResult
argument_list|(
name|op
operator|.
name|getResult
argument_list|()
argument_list|)
operator|.
name|registerSuccess
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AzureBlobFileSystemException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|instanceof
name|AbfsRestOperationException
condition|)
block|{
name|AbfsRestOperationException
name|ere
init|=
operator|(
name|AbfsRestOperationException
operator|)
name|ex
decl_stmt|;
if|if
condition|(
name|ere
operator|.
name|getStatusCode
argument_list|()
operator|==
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|ere
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|long
name|bytesRead
init|=
name|op
operator|.
name|getResult
argument_list|()
operator|.
name|getBytesReceived
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytesRead
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected Content-Length"
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|bytesRead
return|;
block|}
comment|/**    * Seek to given position in stream.    * @param n position to seek to    * @throws IOException if there is an error    * @throws EOFException if attempting to seek past end of file    */
annotation|@
name|Override
DECL|method|seek (long n)
specifier|public
specifier|synchronized
name|void
name|seek
parameter_list|(
name|long
name|n
parameter_list|)
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
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
name|FSExceptionMessages
operator|.
name|NEGATIVE_SEEK
argument_list|)
throw|;
block|}
if|if
condition|(
name|n
operator|>
name|contentLength
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
name|FSExceptionMessages
operator|.
name|CANNOT_SEEK_PAST_EOF
argument_list|)
throw|;
block|}
if|if
condition|(
name|n
operator|>=
name|fCursor
operator|-
name|limit
operator|&&
name|n
operator|<=
name|fCursor
condition|)
block|{
comment|// within buffer
name|bCursor
operator|=
call|(
name|int
call|)
argument_list|(
name|n
operator|-
operator|(
name|fCursor
operator|-
name|limit
operator|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// next read will read from here
name|fCursor
operator|=
name|n
expr_stmt|;
comment|//invalidate buffer
name|limit
operator|=
literal|0
expr_stmt|;
name|bCursor
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|skip (long n)
specifier|public
specifier|synchronized
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
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
name|long
name|currentPos
init|=
name|getPos
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentPos
operator|==
name|contentLength
condition|)
block|{
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
name|FSExceptionMessages
operator|.
name|CANNOT_SEEK_PAST_EOF
argument_list|)
throw|;
block|}
block|}
name|long
name|newPos
init|=
name|currentPos
operator|+
name|n
decl_stmt|;
if|if
condition|(
name|newPos
operator|<
literal|0
condition|)
block|{
name|newPos
operator|=
literal|0
expr_stmt|;
name|n
operator|=
name|newPos
operator|-
name|currentPos
expr_stmt|;
block|}
if|if
condition|(
name|newPos
operator|>
name|contentLength
condition|)
block|{
name|newPos
operator|=
name|contentLength
expr_stmt|;
name|n
operator|=
name|newPos
operator|-
name|currentPos
expr_stmt|;
block|}
name|seek
argument_list|(
name|newPos
argument_list|)
expr_stmt|;
return|return
name|n
return|;
block|}
comment|/**    * Return the size of the remaining available bytes    * if the size is less than or equal to {@link Integer#MAX_VALUE},    * otherwise, return {@link Integer#MAX_VALUE}.    *    * This is to match the behavior of DFSInputStream.available(),    * which some clients may rely on (HBase write-ahead log reading in    * particular).    */
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
specifier|final
name|long
name|remaining
init|=
name|this
operator|.
name|contentLength
operator|-
name|this
operator|.
name|getPos
argument_list|()
decl_stmt|;
return|return
name|remaining
operator|<=
name|Integer
operator|.
name|MAX_VALUE
condition|?
operator|(
name|int
operator|)
name|remaining
else|:
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/**    * Returns the length of the file that this stream refers to. Note that the length returned is the length    * as of the time the Stream was opened. Specifically, if there have been subsequent appends to the file,    * they wont be reflected in the returned length.    *    * @return length of the file.    * @throws IOException if the stream is closed    */
DECL|method|length ()
specifier|public
name|long
name|length
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
return|return
name|contentLength
return|;
block|}
comment|/**    * Return the current offset from the start of the file    * @throws IOException throws {@link IOException} if there is an error    */
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
return|return
name|fCursor
operator|-
name|limit
operator|+
name|bCursor
return|;
block|}
comment|/**    * Seeks a different copy of the data.  Returns true if    * found a new source, false otherwise.    * @throws IOException throws {@link IOException} if there is an error    */
annotation|@
name|Override
DECL|method|seekToNewSource (long l)
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|l
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
throws|throws
name|IOException
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
comment|// de-reference the buffer so it can be GC'ed sooner
block|}
comment|/**    * Not supported by this stream. Throws {@link UnsupportedOperationException}    * @param readlimit ignored    */
annotation|@
name|Override
DECL|method|mark (int readlimit)
specifier|public
specifier|synchronized
name|void
name|mark
parameter_list|(
name|int
name|readlimit
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"mark()/reset() not supported on this stream"
argument_list|)
throw|;
block|}
comment|/**    * Not supported by this stream. Throws {@link UnsupportedOperationException}    */
annotation|@
name|Override
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"mark()/reset() not supported on this stream"
argument_list|)
throw|;
block|}
comment|/**    * gets whether mark and reset are supported by {@code ADLFileInputStream}. Always returns false.    *    * @return always {@code false}    */
annotation|@
name|Override
DECL|method|markSupported ()
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

