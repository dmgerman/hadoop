begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.snative
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|snative
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
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
name|swift
operator|.
name|exceptions
operator|.
name|SwiftConnectionClosedException
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
name|swift
operator|.
name|exceptions
operator|.
name|SwiftException
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
name|swift
operator|.
name|http
operator|.
name|HttpBodyContent
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
name|swift
operator|.
name|http
operator|.
name|HttpInputStreamWithRelease
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
name|swift
operator|.
name|util
operator|.
name|SwiftUtils
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
comment|/**  * The input stream from remote Swift blobs.  * The class attempts to be buffer aware, and react to a forward seek operation  * by trying to scan ahead through the current block of data to find it.  * This accelerates some operations that do a lot of seek()/read() actions,  * including work (such as in the MR engine) that do a seek() immediately after  * an open().  */
end_comment

begin_class
DECL|class|SwiftNativeInputStream
class|class
name|SwiftNativeInputStream
extends|extends
name|FSInputStream
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SwiftNativeInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    *  range requested off the server: {@value}    */
DECL|field|bufferSize
specifier|private
specifier|final
name|long
name|bufferSize
decl_stmt|;
comment|/**    * File nativeStore instance    */
DECL|field|nativeStore
specifier|private
specifier|final
name|SwiftNativeFileSystemStore
name|nativeStore
decl_stmt|;
comment|/**    * Hadoop statistics. Used to get info about number of reads, writes, etc.    */
DECL|field|statistics
specifier|private
specifier|final
name|FileSystem
operator|.
name|Statistics
name|statistics
decl_stmt|;
comment|/**    * Data input stream    */
DECL|field|httpStream
specifier|private
name|HttpInputStreamWithRelease
name|httpStream
decl_stmt|;
comment|/**    * File path    */
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
comment|/**    * Current position    */
DECL|field|pos
specifier|private
name|long
name|pos
init|=
literal|0
decl_stmt|;
comment|/**    * Length of the file picked up at start time    */
DECL|field|contentLength
specifier|private
name|long
name|contentLength
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Why the stream is closed    */
DECL|field|reasonClosed
specifier|private
name|String
name|reasonClosed
init|=
literal|"unopened"
decl_stmt|;
comment|/**    * Offset in the range requested last    */
DECL|field|rangeOffset
specifier|private
name|long
name|rangeOffset
init|=
literal|0
decl_stmt|;
DECL|method|SwiftNativeInputStream (SwiftNativeFileSystemStore storeNative, FileSystem.Statistics statistics, Path path, long bufferSize)
specifier|public
name|SwiftNativeInputStream
parameter_list|(
name|SwiftNativeFileSystemStore
name|storeNative
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|statistics
parameter_list|,
name|Path
name|path
parameter_list|,
name|long
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|nativeStore
operator|=
name|storeNative
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
if|if
condition|(
name|bufferSize
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid buffer size"
argument_list|)
throw|;
block|}
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
comment|//initial buffer fill
name|this
operator|.
name|httpStream
operator|=
name|storeNative
operator|.
name|getObject
argument_list|(
name|path
argument_list|)
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
comment|//fillBuffer(0);
block|}
comment|/**    * Move to a new position within the file relative to where the pointer is now.    * Always call from a synchronized clause    * @param offset offset    */
DECL|method|incPos (int offset)
specifier|private
specifier|synchronized
name|void
name|incPos
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|pos
operator|+=
name|offset
expr_stmt|;
name|rangeOffset
operator|+=
name|offset
expr_stmt|;
name|SwiftUtils
operator|.
name|trace
argument_list|(
name|LOG
argument_list|,
literal|"Inc: pos=%d bufferOffset=%d"
argument_list|,
name|pos
argument_list|,
name|rangeOffset
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update the start of the buffer; always call from a sync'd clause    * @param seekPos position sought.    * @param contentLength content length provided by response (may be -1)    */
DECL|method|updateStartOfBufferPosition (long seekPos, long contentLength)
specifier|private
specifier|synchronized
name|void
name|updateStartOfBufferPosition
parameter_list|(
name|long
name|seekPos
parameter_list|,
name|long
name|contentLength
parameter_list|)
block|{
comment|//reset the seek pointer
name|pos
operator|=
name|seekPos
expr_stmt|;
comment|//and put the buffer offset to 0
name|rangeOffset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|contentLength
operator|=
name|contentLength
expr_stmt|;
name|SwiftUtils
operator|.
name|trace
argument_list|(
name|LOG
argument_list|,
literal|"Move: pos=%d; bufferOffset=%d; contentLength=%d"
argument_list|,
name|pos
argument_list|,
name|rangeOffset
argument_list|,
name|contentLength
argument_list|)
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
name|verifyOpen
argument_list|()
expr_stmt|;
name|int
name|result
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|result
operator|=
name|httpStream
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"IOException while reading "
operator|+
name|path
operator|+
literal|": "
operator|+
name|e
operator|+
literal|", attempting to reopen."
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|reopenBuffer
argument_list|()
condition|)
block|{
name|result
operator|=
name|httpStream
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|!=
operator|-
literal|1
condition|)
block|{
name|incPos
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|statistics
operator|!=
literal|null
operator|&&
name|result
operator|!=
operator|-
literal|1
condition|)
block|{
name|statistics
operator|.
name|incrementBytesRead
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
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
name|SwiftUtils
operator|.
name|debug
argument_list|(
name|LOG
argument_list|,
literal|"read(buffer, %d, %d)"
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|SwiftUtils
operator|.
name|validateReadArgs
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|int
name|result
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|verifyOpen
argument_list|()
expr_stmt|;
name|result
operator|=
name|httpStream
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//other IO problems are viewed as transient and re-attempted
name|LOG
operator|.
name|info
argument_list|(
literal|"Received IOException while reading '"
operator|+
name|path
operator|+
literal|"', attempting to reopen: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"IOE on read()"
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|reopenBuffer
argument_list|()
condition|)
block|{
name|result
operator|=
name|httpStream
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|>
literal|0
condition|)
block|{
name|incPos
argument_list|(
name|result
argument_list|)
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
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Re-open the buffer    * @return true iff more data could be added to the buffer    * @throws IOException if not    */
DECL|method|reopenBuffer ()
specifier|private
name|boolean
name|reopenBuffer
parameter_list|()
throws|throws
name|IOException
block|{
name|innerClose
argument_list|(
literal|"reopening buffer to trigger refresh"
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fillBuffer
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|eof
parameter_list|)
block|{
comment|//the EOF has been reached
name|this
operator|.
name|reasonClosed
operator|=
literal|"End of file"
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
comment|/**    * close the stream. After this the stream is not usable -unless and until    * it is re-opened (which can happen on some of the buffer ops)    * This method is thread-safe and idempotent.    *    * @throws IOException on IO problems.    */
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
name|innerClose
argument_list|(
literal|"closed"
argument_list|)
expr_stmt|;
block|}
DECL|method|innerClose (String reason)
specifier|private
name|void
name|innerClose
parameter_list|(
name|String
name|reason
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|httpStream
operator|!=
literal|null
condition|)
block|{
name|reasonClosed
operator|=
name|reason
expr_stmt|;
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
literal|"Closing HTTP input stream : "
operator|+
name|reason
argument_list|)
expr_stmt|;
block|}
name|httpStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|httpStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Assume that the connection is not closed: throws an exception if it is    * @throws SwiftConnectionClosedException    */
DECL|method|verifyOpen ()
specifier|private
name|void
name|verifyOpen
parameter_list|()
throws|throws
name|SwiftConnectionClosedException
block|{
if|if
condition|(
name|httpStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SwiftConnectionClosedException
argument_list|(
name|reasonClosed
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SwiftNativeInputStream"
operator|+
literal|" position="
operator|+
name|pos
operator|+
literal|" buffer size = "
operator|+
name|bufferSize
operator|+
literal|" "
operator|+
operator|(
name|httpStream
operator|!=
literal|null
condition|?
name|httpStream
operator|.
name|toString
argument_list|()
else|:
operator|(
literal|" no input stream: "
operator|+
name|reasonClosed
operator|)
operator|)
return|;
block|}
comment|/**    * Treats any finalize() call without the input stream being closed    * as a serious problem, logging at error level    * @throws Throwable n/a    */
annotation|@
name|Override
DECL|method|finalize ()
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|httpStream
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Input stream is leaking handles by not being closed() properly: "
operator|+
name|httpStream
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Read through the specified number of bytes.    * The implementation iterates a byte a time, which may seem inefficient    * compared to the read(bytes[]) method offered by input streams.    * However, if you look at the code that implements that method, it comes    * down to read() one char at a time -only here the return value is discarded.    *    *<p/>    * This is a no-op if the stream is closed    * @param bytes number of bytes to read.    * @throws IOException IO problems    * @throws SwiftException if a read returned -1.    */
DECL|method|chompBytes (long bytes)
specifier|private
name|int
name|chompBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|httpStream
operator|!=
literal|null
condition|)
block|{
name|int
name|result
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bytes
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|httpStream
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|SwiftException
argument_list|(
literal|"Received error code while chomping input"
argument_list|)
throw|;
block|}
name|count
operator|++
expr_stmt|;
name|incPos
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**    * Seek to an offset. If the data is already in the buffer, move to it    * @param targetPos target position    * @throws IOException on any problem    */
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
argument_list|)
throw|;
block|}
comment|//there's some special handling of near-local data
comment|//as the seek can be omitted if it is in/adjacent
name|long
name|offset
init|=
name|targetPos
operator|-
name|pos
decl_stmt|;
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
literal|"Seek to "
operator|+
name|targetPos
operator|+
literal|"; current pos ="
operator|+
name|pos
operator|+
literal|"; offset="
operator|+
name|offset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"seek is no-op"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"seek is backwards"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|rangeOffset
operator|+
name|offset
operator|<
name|bufferSize
operator|)
condition|)
block|{
comment|//if the seek is in  range of that requested, scan forwards
comment|//instead of closing and re-opening a new HTTP connection
name|SwiftUtils
operator|.
name|debug
argument_list|(
name|LOG
argument_list|,
literal|"seek is within current stream"
operator|+
literal|"; pos= %d ; targetPos=%d; "
operator|+
literal|"offset= %d ; bufferOffset=%d"
argument_list|,
name|pos
argument_list|,
name|targetPos
argument_list|,
name|offset
argument_list|,
name|rangeOffset
argument_list|)
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"chomping "
argument_list|)
expr_stmt|;
name|chompBytes
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//this is assumed to be recoverable with a seek -or more likely to fail
name|LOG
operator|.
name|debug
argument_list|(
literal|"while chomping "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|targetPos
operator|-
name|pos
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"chomping successful"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"chomping failed"
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"Seek is beyond buffer size of "
operator|+
name|bufferSize
argument_list|)
expr_stmt|;
block|}
block|}
name|innerClose
argument_list|(
literal|"seeking to "
operator|+
name|targetPos
argument_list|)
expr_stmt|;
name|fillBuffer
argument_list|(
name|targetPos
argument_list|)
expr_stmt|;
block|}
comment|/**    * Fill the buffer from the target position    * If the target position == current position, the    * read still goes ahead; this is a way of handling partial read failures    * @param targetPos target position    * @throws IOException IO problems on the read    */
DECL|method|fillBuffer (long targetPos)
specifier|private
name|void
name|fillBuffer
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|length
init|=
name|targetPos
operator|+
name|bufferSize
decl_stmt|;
name|SwiftUtils
operator|.
name|debug
argument_list|(
name|LOG
argument_list|,
literal|"Fetching %d bytes starting at %d"
argument_list|,
name|length
argument_list|,
name|targetPos
argument_list|)
expr_stmt|;
name|HttpBodyContent
name|blob
init|=
name|nativeStore
operator|.
name|getObject
argument_list|(
name|path
argument_list|,
name|targetPos
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|httpStream
operator|=
name|blob
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|updateStartOfBufferPosition
argument_list|(
name|targetPos
argument_list|,
name|blob
operator|.
name|getContentLength
argument_list|()
argument_list|)
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
name|pos
return|;
block|}
comment|/**    * This FS doesn't explicitly support multiple data sources, so    * return false here.    * @param targetPos the desired target position    * @return true if a new source of the data has been set up    * as the source of future reads    * @throws IOException IO problems    */
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
block|}
end_class

end_unit

