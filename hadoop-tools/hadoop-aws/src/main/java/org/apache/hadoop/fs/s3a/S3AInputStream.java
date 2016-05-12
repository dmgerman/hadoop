begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3Client
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|GetObjectRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|S3ObjectInputStream
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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|fs
operator|.
name|CanSetReadahead
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

begin_comment
comment|/**  * The input stream for an S3A object.  *  * As this stream seeks withing an object, it may close then re-open the stream.  * When this happens, any updated stream data may be retrieved, and, given  * the consistency model of Amazon S3, outdated data may in fact be picked up.  *  * As a result, the outcome of reading from a stream of an object which is  * actively manipulated during the read process is "undefined".  *  * The class is marked as private as code should not be creating instances  * themselves. Any extra feature (e.g instrumentation) should be considered  * unstable.  *  * Because it prints some of the state of the instrumentation,  * the output of {@link #toString()} must also be considered unstable.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|S3AInputStream
specifier|public
class|class
name|S3AInputStream
extends|extends
name|FSInputStream
implements|implements
name|CanSetReadahead
block|{
comment|/**    * This is the public position; the one set in {@link #seek(long)}    * and returned in {@link #getPos()}.    */
DECL|field|pos
specifier|private
name|long
name|pos
decl_stmt|;
comment|/**    * Closed bit. Volatile so reads are non-blocking.    * Updates must be in a synchronized block to guarantee an atomic check and    * set    */
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
decl_stmt|;
DECL|field|wrappedStream
specifier|private
name|S3ObjectInputStream
name|wrappedStream
decl_stmt|;
DECL|field|stats
specifier|private
specifier|final
name|FileSystem
operator|.
name|Statistics
name|stats
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|AmazonS3Client
name|client
decl_stmt|;
DECL|field|bucket
specifier|private
specifier|final
name|String
name|bucket
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|contentLength
specifier|private
specifier|final
name|long
name|contentLength
decl_stmt|;
DECL|field|uri
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|S3AFileSystem
operator|.
name|LOG
decl_stmt|;
DECL|field|CLOSE_THRESHOLD
specifier|public
specifier|static
specifier|final
name|long
name|CLOSE_THRESHOLD
init|=
literal|4096
decl_stmt|;
DECL|field|streamStatistics
specifier|private
specifier|final
name|S3AInstrumentation
operator|.
name|InputStreamStatistics
name|streamStatistics
decl_stmt|;
DECL|field|readahead
specifier|private
name|long
name|readahead
decl_stmt|;
comment|/**    * This is the actual position within the object, used by    * lazy seek to decide whether to seek on the next read or not.    */
DECL|field|nextReadPos
specifier|private
name|long
name|nextReadPos
decl_stmt|;
comment|/* Amount of data desired from the request */
DECL|field|requestedStreamLen
specifier|private
name|long
name|requestedStreamLen
decl_stmt|;
DECL|method|S3AInputStream (String bucket, String key, long contentLength, AmazonS3Client client, FileSystem.Statistics stats, S3AInstrumentation instrumentation, long readahead)
specifier|public
name|S3AInputStream
parameter_list|(
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|,
name|long
name|contentLength
parameter_list|,
name|AmazonS3Client
name|client
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|stats
parameter_list|,
name|S3AInstrumentation
name|instrumentation
parameter_list|,
name|long
name|readahead
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|bucket
argument_list|)
argument_list|,
literal|"No Bucket"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|key
argument_list|)
argument_list|,
literal|"No Key"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|contentLength
operator|>=
literal|0
argument_list|,
literal|"Negative content length"
argument_list|)
expr_stmt|;
name|this
operator|.
name|bucket
operator|=
name|bucket
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|contentLength
operator|=
name|contentLength
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|uri
operator|=
literal|"s3a://"
operator|+
name|this
operator|.
name|bucket
operator|+
literal|"/"
operator|+
name|this
operator|.
name|key
expr_stmt|;
name|this
operator|.
name|streamStatistics
operator|=
name|instrumentation
operator|.
name|newInputStreamStatistics
argument_list|()
expr_stmt|;
name|setReadahead
argument_list|(
name|readahead
argument_list|)
expr_stmt|;
block|}
comment|/**    * Opens up the stream at specified target position and for given length.    *    * @param reason reason for reopen    * @param targetPos target position    * @param length length requested    * @throws IOException    */
DECL|method|reopen (String reason, long targetPos, long length)
specifier|private
specifier|synchronized
name|void
name|reopen
parameter_list|(
name|String
name|reason
parameter_list|,
name|long
name|targetPos
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|requestedStreamLen
operator|=
name|this
operator|.
name|contentLength
expr_stmt|;
if|if
condition|(
name|wrappedStream
operator|!=
literal|null
condition|)
block|{
name|closeStream
argument_list|(
literal|"reopen("
operator|+
name|reason
operator|+
literal|")"
argument_list|,
name|requestedStreamLen
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"reopen({}) for {} at targetPos={}, length={},"
operator|+
literal|" requestedStreamLen={}, streamPosition={}, nextReadPosition={}"
argument_list|,
name|uri
argument_list|,
name|reason
argument_list|,
name|targetPos
argument_list|,
name|length
argument_list|,
name|requestedStreamLen
argument_list|,
name|pos
argument_list|,
name|nextReadPos
argument_list|)
expr_stmt|;
name|streamStatistics
operator|.
name|streamOpened
argument_list|()
expr_stmt|;
name|GetObjectRequest
name|request
init|=
operator|new
name|GetObjectRequest
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|)
operator|.
name|withRange
argument_list|(
name|targetPos
argument_list|,
name|requestedStreamLen
argument_list|)
decl_stmt|;
name|wrappedStream
operator|=
name|client
operator|.
name|getObject
argument_list|(
name|request
argument_list|)
operator|.
name|getObjectContent
argument_list|()
expr_stmt|;
if|if
condition|(
name|wrappedStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Null IO stream from reopen of ("
operator|+
name|reason
operator|+
literal|") "
operator|+
name|uri
argument_list|)
throw|;
block|}
name|this
operator|.
name|pos
operator|=
name|targetPos
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
operator|(
name|nextReadPos
operator|<
literal|0
operator|)
condition|?
literal|0
else|:
name|nextReadPos
return|;
block|}
annotation|@
name|Override
DECL|method|seek (long targetPos)
specifier|public
specifier|synchronized
name|void
name|seek
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
comment|// Do not allow negative seek
if|if
condition|(
name|targetPos
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
operator|+
literal|" "
operator|+
name|targetPos
argument_list|)
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|contentLength
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
comment|// Lazy seek
name|nextReadPos
operator|=
name|targetPos
expr_stmt|;
block|}
comment|/**    * Seek without raising any exception. This is for use in    * {@code finally} clauses    * @param positiveTargetPos a target position which must be positive.    */
DECL|method|seekQuietly (long positiveTargetPos)
specifier|private
name|void
name|seekQuietly
parameter_list|(
name|long
name|positiveTargetPos
parameter_list|)
block|{
try|try
block|{
name|seek
argument_list|(
name|positiveTargetPos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ignoring IOE on seek of {} to {}"
argument_list|,
name|uri
argument_list|,
name|positiveTargetPos
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Adjust the stream to a specific position.    *    * @param targetPos target seek position    * @param length length of content that needs to be read from targetPos    * @throws IOException    */
DECL|method|seekInStream (long targetPos, long length)
specifier|private
name|void
name|seekInStream
parameter_list|(
name|long
name|targetPos
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|wrappedStream
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// compute how much more to skip
name|long
name|diff
init|=
name|targetPos
operator|-
name|pos
decl_stmt|;
if|if
condition|(
name|diff
operator|>
literal|0
condition|)
block|{
comment|// forward seek -this is where data can be skipped
name|int
name|available
init|=
name|wrappedStream
operator|.
name|available
argument_list|()
decl_stmt|;
comment|// always seek at least as far as what is available
name|long
name|forwardSeekRange
init|=
name|Math
operator|.
name|max
argument_list|(
name|readahead
argument_list|,
name|available
argument_list|)
decl_stmt|;
comment|// work out how much is actually left in the stream
comment|// then choose whichever comes first: the range or the EOF
name|long
name|forwardSeekLimit
init|=
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|()
argument_list|,
name|forwardSeekRange
argument_list|)
decl_stmt|;
if|if
condition|(
name|diff
operator|<=
name|forwardSeekLimit
condition|)
block|{
comment|// the forward seek range is within the limits
name|LOG
operator|.
name|debug
argument_list|(
literal|"Forward seek on {}, of {} bytes"
argument_list|,
name|uri
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|streamStatistics
operator|.
name|seekForwards
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|long
name|skipped
init|=
name|wrappedStream
operator|.
name|skip
argument_list|(
name|diff
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipped
operator|>
literal|0
condition|)
block|{
name|pos
operator|+=
name|skipped
expr_stmt|;
comment|// as these bytes have been read, they are included in the counter
name|incrementBytesRead
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|==
name|targetPos
condition|)
block|{
comment|// all is well
return|return;
block|}
else|else
block|{
comment|// log a warning; continue to attempt to re-open
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to seek on {} to {}. Current position {}"
argument_list|,
name|uri
argument_list|,
name|targetPos
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|diff
operator|<
literal|0
condition|)
block|{
comment|// backwards seek
name|streamStatistics
operator|.
name|seekBackwards
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// targetPos == pos
comment|// this should never happen as the caller filters it out.
comment|// Retained just in case
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ignoring seek {} to {} as target position == current"
argument_list|,
name|uri
argument_list|,
name|targetPos
argument_list|)
expr_stmt|;
block|}
comment|// close the stream; if read the object will be opened at the new pos
name|closeStream
argument_list|(
literal|"seekInStream()"
argument_list|,
name|this
operator|.
name|requestedStreamLen
argument_list|)
expr_stmt|;
name|pos
operator|=
name|targetPos
expr_stmt|;
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
comment|/**    * Perform lazy seek and adjust stream to correct position for reading.    *    * @param targetPos position from where data should be read    * @param len length of the content that needs to be read    */
DECL|method|lazySeek (long targetPos, long len)
specifier|private
name|void
name|lazySeek
parameter_list|(
name|long
name|targetPos
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|//For lazy seek
if|if
condition|(
name|targetPos
operator|!=
name|this
operator|.
name|pos
condition|)
block|{
name|seekInStream
argument_list|(
name|targetPos
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|//re-open at specific location if needed
if|if
condition|(
name|wrappedStream
operator|==
literal|null
condition|)
block|{
name|reopen
argument_list|(
literal|"read from new offset"
argument_list|,
name|targetPos
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Increment the bytes read counter if there is a stats instance    * and the number of bytes read is more than zero.    * @param bytesRead number of bytes read    */
DECL|method|incrementBytesRead (long bytesRead)
specifier|private
name|void
name|incrementBytesRead
parameter_list|(
name|long
name|bytesRead
parameter_list|)
block|{
name|streamStatistics
operator|.
name|bytesRead
argument_list|(
name|bytesRead
argument_list|)
expr_stmt|;
if|if
condition|(
name|stats
operator|!=
literal|null
operator|&&
name|bytesRead
operator|>
literal|0
condition|)
block|{
name|stats
operator|.
name|incrementBytesRead
argument_list|(
name|bytesRead
argument_list|)
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
name|checkNotClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|contentLength
operator|==
literal|0
operator|||
operator|(
name|nextReadPos
operator|>=
name|contentLength
operator|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|lazySeek
argument_list|(
name|nextReadPos
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|int
name|byteRead
decl_stmt|;
try|try
block|{
name|byteRead
operator|=
name|wrappedStream
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onReadFailure
argument_list|(
name|e
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|byteRead
operator|=
name|wrappedStream
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|byteRead
operator|>=
literal|0
condition|)
block|{
name|pos
operator|++
expr_stmt|;
name|nextReadPos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|byteRead
operator|>=
literal|0
condition|)
block|{
name|incrementBytesRead
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|byteRead
return|;
block|}
comment|/**    * Handle an IOE on a read by attempting to re-open the stream.    * The filesystem's readException count will be incremented.    * @param ioe exception caught.    * @param length length of data being attempted to read    * @throws IOException any exception thrown on the re-open attempt.    */
DECL|method|onReadFailure (IOException ioe, int length)
specifier|private
name|void
name|onReadFailure
parameter_list|(
name|IOException
name|ioe
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got exception while trying to read from stream {}"
operator|+
literal|" trying to recover: "
operator|+
name|ioe
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"While trying to read from stream {}"
argument_list|,
name|uri
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|streamStatistics
operator|.
name|readException
argument_list|()
expr_stmt|;
name|reopen
argument_list|(
literal|"failure recovery"
argument_list|,
name|pos
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *    * This updates the statistics on read operations started and whether    * or not the read operation "completed", that is: returned the exact    * number of bytes requested.    * @throws EOFException if there is no more data    * @throws IOException if there are other problems    */
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
name|validatePositionedReadArgs
argument_list|(
name|nextReadPos
argument_list|,
name|buf
argument_list|,
name|off
argument_list|,
name|len
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
name|contentLength
operator|==
literal|0
operator|||
operator|(
name|nextReadPos
operator|>=
name|contentLength
operator|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|lazySeek
argument_list|(
name|nextReadPos
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|streamStatistics
operator|.
name|readOperationStarted
argument_list|(
name|nextReadPos
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|int
name|bytesRead
decl_stmt|;
try|try
block|{
name|bytesRead
operator|=
name|wrappedStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onReadFailure
argument_list|(
name|e
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bytesRead
operator|=
name|wrappedStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bytesRead
operator|>
literal|0
condition|)
block|{
name|pos
operator|+=
name|bytesRead
expr_stmt|;
name|nextReadPos
operator|+=
name|bytesRead
expr_stmt|;
block|}
name|incrementBytesRead
argument_list|(
name|bytesRead
argument_list|)
expr_stmt|;
name|streamStatistics
operator|.
name|readOperationCompleted
argument_list|(
name|len
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
return|return
name|bytesRead
return|;
block|}
comment|/**    * Verify that the input stream is open. Non blocking; this gives    * the last state of the volatile {@link #closed} field.    * @throws IOException if the connection is closed.    */
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
name|uri
operator|+
literal|": "
operator|+
name|FSExceptionMessages
operator|.
name|STREAM_IS_CLOSED
argument_list|)
throw|;
block|}
block|}
comment|/**    * Close the stream.    * This triggers publishing of the stream statistics back to the filesystem    * statistics.    * This operation is synchronized, so that only one thread can attempt to    * close the connection; all later/blocked calls are no-ops.    * @throws IOException on any problem    */
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
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
try|try
block|{
comment|// close or abort the stream
name|closeStream
argument_list|(
literal|"close() operation"
argument_list|,
name|this
operator|.
name|contentLength
argument_list|)
expr_stmt|;
comment|// this is actually a no-op
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// merge the statistics back into the FS statistics.
name|streamStatistics
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Close a stream: decide whether to abort or close, based on    * the length of the stream and the current position.    * If a close() is attempted and fails, the operation escalates to    * an abort.    *    * This does not set the {@link #closed} flag.    *    * @param reason reason for stream being closed; used in messages    * @param length length of the stream.    */
DECL|method|closeStream (String reason, long length)
specifier|private
name|void
name|closeStream
parameter_list|(
name|String
name|reason
parameter_list|,
name|long
name|length
parameter_list|)
block|{
if|if
condition|(
name|wrappedStream
operator|!=
literal|null
condition|)
block|{
name|boolean
name|shouldAbort
init|=
name|length
operator|-
name|pos
operator|>
name|CLOSE_THRESHOLD
decl_stmt|;
if|if
condition|(
operator|!
name|shouldAbort
condition|)
block|{
try|try
block|{
comment|// clean close. This will read to the end of the stream,
comment|// so, while cleaner, can be pathological on a multi-GB object
name|wrappedStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|streamStatistics
operator|.
name|streamClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// exception escalates to an abort
name|LOG
operator|.
name|debug
argument_list|(
literal|"When closing {} stream for {}"
argument_list|,
name|uri
argument_list|,
name|reason
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|shouldAbort
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|shouldAbort
condition|)
block|{
comment|// Abort, rather than just close, the underlying stream.  Otherwise, the
comment|// remaining object payload is read from S3 while closing the stream.
name|wrappedStream
operator|.
name|abort
argument_list|()
expr_stmt|;
name|streamStatistics
operator|.
name|streamClose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stream {} {}: {}; streamPos={}, nextReadPos={},"
operator|+
literal|" length={}"
argument_list|,
name|uri
argument_list|,
operator|(
name|shouldAbort
condition|?
literal|"aborted"
else|:
literal|"closed"
operator|)
argument_list|,
name|reason
argument_list|,
name|pos
argument_list|,
name|nextReadPos
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|wrappedStream
operator|=
literal|null
expr_stmt|;
block|}
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
name|remaining
argument_list|()
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
comment|/**    * Bytes left in stream.    * @return how many bytes are left to read    */
DECL|method|remaining ()
specifier|protected
name|long
name|remaining
parameter_list|()
block|{
return|return
name|this
operator|.
name|contentLength
operator|-
name|this
operator|.
name|pos
return|;
block|}
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
comment|/**    * String value includes statistics as well as stream state.    *<b>Important: there are no guarantees as to the stability    * of this value.</b>    * @return a string value for printing in logs/diagnostics    */
annotation|@
name|Override
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"S3AInputStream{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" pos="
argument_list|)
operator|.
name|append
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" nextReadPos="
argument_list|)
operator|.
name|append
argument_list|(
name|nextReadPos
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" contentLength="
argument_list|)
operator|.
name|append
argument_list|(
name|contentLength
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|streamStatistics
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Subclass {@code readFully()} operation which only seeks at the start    * of the series of operations; seeking back at the end.    *    * This is significantly higher performance if multiple read attempts are    * needed to fetch the data, as it does not break the HTTP connection.    *    * To maintain thread safety requirements, this operation is synchronized    * for the duration of the sequence.    * {@inheritDoc}    *    */
annotation|@
name|Override
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
name|checkNotClosed
argument_list|()
expr_stmt|;
name|validatePositionedReadArgs
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
name|streamStatistics
operator|.
name|readFullyOperationStarted
argument_list|(
name|position
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|int
name|nread
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|long
name|oldPos
init|=
name|getPos
argument_list|()
decl_stmt|;
try|try
block|{
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
while|while
condition|(
name|nread
operator|<
name|length
condition|)
block|{
name|int
name|nbytes
init|=
name|read
argument_list|(
name|buffer
argument_list|,
name|offset
operator|+
name|nread
argument_list|,
name|length
operator|-
name|nread
argument_list|)
decl_stmt|;
if|if
condition|(
name|nbytes
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
name|EOF_IN_READ_FULLY
argument_list|)
throw|;
block|}
name|nread
operator|+=
name|nbytes
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|seekQuietly
argument_list|(
name|oldPos
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Access the input stream statistics.    * This is for internal testing and may be removed without warning.    * @return the statistics for this input stream    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getS3AStreamStatistics ()
specifier|public
name|S3AInstrumentation
operator|.
name|InputStreamStatistics
name|getS3AStreamStatistics
parameter_list|()
block|{
return|return
name|streamStatistics
return|;
block|}
annotation|@
name|Override
DECL|method|setReadahead (Long readahead)
specifier|public
name|void
name|setReadahead
parameter_list|(
name|Long
name|readahead
parameter_list|)
block|{
if|if
condition|(
name|readahead
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|readahead
operator|=
name|Constants
operator|.
name|DEFAULT_READAHEAD_RANGE
expr_stmt|;
block|}
else|else
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|readahead
operator|>=
literal|0
argument_list|,
literal|"Negative readahead value"
argument_list|)
expr_stmt|;
name|this
operator|.
name|readahead
operator|=
name|readahead
expr_stmt|;
block|}
block|}
comment|/**    * Get the current readahead value.    * @return a non-negative readahead value    */
DECL|method|getReadahead ()
specifier|public
name|long
name|getReadahead
parameter_list|()
block|{
return|return
name|readahead
return|;
block|}
block|}
end_class

end_unit

