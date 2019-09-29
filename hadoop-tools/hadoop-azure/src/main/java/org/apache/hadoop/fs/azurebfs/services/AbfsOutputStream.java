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
name|io
operator|.
name|InterruptedIOException
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
name|net
operator|.
name|HttpURLConnection
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
name|util
operator|.
name|Locale
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
name|ConcurrentLinkedDeque
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
name|LinkedBlockingQueue
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
name|ExecutorCompletionService
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
name|ThreadPoolExecutor
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
name|Callable
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
name|Future
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
name|TimeUnit
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
name|ElasticByteBufferPool
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
name|StreamCapabilities
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
name|Syncable
import|;
end_import

begin_comment
comment|/**  * The BlobFsOutputStream for Rest AbfsClient.  */
end_comment

begin_class
DECL|class|AbfsOutputStream
specifier|public
class|class
name|AbfsOutputStream
extends|extends
name|OutputStream
implements|implements
name|Syncable
implements|,
name|StreamCapabilities
block|{
DECL|field|client
specifier|private
specifier|final
name|AbfsClient
name|client
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|position
specifier|private
name|long
name|position
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|supportFlush
specifier|private
name|boolean
name|supportFlush
decl_stmt|;
DECL|field|disableOutputStreamFlush
specifier|private
name|boolean
name|disableOutputStreamFlush
decl_stmt|;
DECL|field|lastError
specifier|private
specifier|volatile
name|IOException
name|lastError
decl_stmt|;
DECL|field|lastFlushOffset
specifier|private
name|long
name|lastFlushOffset
decl_stmt|;
DECL|field|lastTotalAppendOffset
specifier|private
name|long
name|lastTotalAppendOffset
init|=
literal|0
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|bufferIndex
specifier|private
name|int
name|bufferIndex
decl_stmt|;
DECL|field|maxConcurrentRequestCount
specifier|private
specifier|final
name|int
name|maxConcurrentRequestCount
decl_stmt|;
DECL|field|writeOperations
specifier|private
name|ConcurrentLinkedDeque
argument_list|<
name|WriteOperation
argument_list|>
name|writeOperations
decl_stmt|;
DECL|field|threadExecutor
specifier|private
specifier|final
name|ThreadPoolExecutor
name|threadExecutor
decl_stmt|;
DECL|field|completionService
specifier|private
specifier|final
name|ExecutorCompletionService
argument_list|<
name|Void
argument_list|>
name|completionService
decl_stmt|;
comment|/**    * Queue storing buffers with the size of the Azure block ready for    * reuse. The pool allows reusing the blocks instead of allocating new    * blocks. After the data is sent to the service, the buffer is returned    * back to the queue    */
DECL|field|byteBufferPool
specifier|private
specifier|final
name|ElasticByteBufferPool
name|byteBufferPool
init|=
operator|new
name|ElasticByteBufferPool
argument_list|()
decl_stmt|;
DECL|method|AbfsOutputStream ( final AbfsClient client, final String path, final long position, final int bufferSize, final boolean supportFlush, final boolean disableOutputStreamFlush)
specifier|public
name|AbfsOutputStream
parameter_list|(
specifier|final
name|AbfsClient
name|client
parameter_list|,
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|long
name|position
parameter_list|,
specifier|final
name|int
name|bufferSize
parameter_list|,
specifier|final
name|boolean
name|supportFlush
parameter_list|,
specifier|final
name|boolean
name|disableOutputStreamFlush
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
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|supportFlush
operator|=
name|supportFlush
expr_stmt|;
name|this
operator|.
name|disableOutputStreamFlush
operator|=
name|disableOutputStreamFlush
expr_stmt|;
name|this
operator|.
name|lastError
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|lastFlushOffset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|byteBufferPool
operator|.
name|getBuffer
argument_list|(
literal|false
argument_list|,
name|bufferSize
argument_list|)
operator|.
name|array
argument_list|()
expr_stmt|;
name|this
operator|.
name|bufferIndex
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|writeOperations
operator|=
operator|new
name|ConcurrentLinkedDeque
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxConcurrentRequestCount
operator|=
literal|4
operator|*
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
name|threadExecutor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|maxConcurrentRequestCount
argument_list|,
name|maxConcurrentRequestCount
argument_list|,
literal|10L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|completionService
operator|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|this
operator|.
name|threadExecutor
argument_list|)
expr_stmt|;
block|}
comment|/**    * Query the stream for a specific capability.    *    * @param capability string to query the stream support for.    * @return true for hsync and hflush.    */
annotation|@
name|Override
DECL|method|hasCapability (String capability)
specifier|public
name|boolean
name|hasCapability
parameter_list|(
name|String
name|capability
parameter_list|)
block|{
switch|switch
condition|(
name|capability
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
condition|)
block|{
case|case
name|StreamCapabilities
operator|.
name|HSYNC
case|:
case|case
name|StreamCapabilities
operator|.
name|HFLUSH
case|:
return|return
name|supportFlush
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Writes the specified byte to this output stream. The general contract for    * write is that one byte is written to the output stream. The byte to be    * written is the eight low-order bits of the argument b. The 24 high-order    * bits of b are ignored.    *    * @param byteVal the byteValue to write.    * @throws IOException if an I/O error occurs. In particular, an IOException may be    *                     thrown if the output stream has been closed.    */
annotation|@
name|Override
DECL|method|write (final int byteVal)
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|int
name|byteVal
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
name|byteVal
operator|&
literal|0xFF
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes length bytes from the specified byte array starting at off to    * this output stream.    *    * @param data   the byte array to write.    * @param off the start off in the data.    * @param length the number of bytes to write.    * @throws IOException if an I/O error occurs. In particular, an IOException may be    *                     thrown if the output stream has been closed.    */
annotation|@
name|Override
DECL|method|write (final byte[] data, final int off, final int length)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|maybeThrowLastError
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|data
operator|!=
literal|null
argument_list|,
literal|"null data"
argument_list|)
expr_stmt|;
if|if
condition|(
name|off
operator|<
literal|0
operator|||
name|length
argument_list|<
literal|0
operator|||
name|length
argument_list|>
name|data
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
name|int
name|currentOffset
init|=
name|off
decl_stmt|;
name|int
name|writableBytes
init|=
name|bufferSize
operator|-
name|bufferIndex
decl_stmt|;
name|int
name|numberOfBytesToWrite
init|=
name|length
decl_stmt|;
while|while
condition|(
name|numberOfBytesToWrite
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|writableBytes
operator|<=
name|numberOfBytesToWrite
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|currentOffset
argument_list|,
name|buffer
argument_list|,
name|bufferIndex
argument_list|,
name|writableBytes
argument_list|)
expr_stmt|;
name|bufferIndex
operator|+=
name|writableBytes
expr_stmt|;
name|writeCurrentBufferToService
argument_list|()
expr_stmt|;
name|currentOffset
operator|+=
name|writableBytes
expr_stmt|;
name|numberOfBytesToWrite
operator|=
name|numberOfBytesToWrite
operator|-
name|writableBytes
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|currentOffset
argument_list|,
name|buffer
argument_list|,
name|bufferIndex
argument_list|,
name|numberOfBytesToWrite
argument_list|)
expr_stmt|;
name|bufferIndex
operator|+=
name|numberOfBytesToWrite
expr_stmt|;
name|numberOfBytesToWrite
operator|=
literal|0
expr_stmt|;
block|}
name|writableBytes
operator|=
name|bufferSize
operator|-
name|bufferIndex
expr_stmt|;
block|}
block|}
comment|/**    * Throw the last error recorded if not null.    * After the stream is closed, this is always set to    * an exception, so acts as a guard against method invocation once    * closed.    * @throws IOException if lastError is set    */
DECL|method|maybeThrowLastError ()
specifier|private
name|void
name|maybeThrowLastError
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lastError
operator|!=
literal|null
condition|)
block|{
throw|throw
name|lastError
throw|;
block|}
block|}
comment|/**    * Flushes this output stream and forces any buffered output bytes to be    * written out. If any data remains in the payload it is committed to the    * service. Data is queued for writing and forced out to the service    * before the call returns.    */
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|disableOutputStreamFlush
condition|)
block|{
name|flushInternalAsync
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Similar to posix fsync, flush out the data in client's user buffer    * all the way to the disk device (but the disk may have it in its cache).    * @throws IOException if error occurs    */
annotation|@
name|Override
DECL|method|hsync ()
specifier|public
name|void
name|hsync
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|supportFlush
condition|)
block|{
name|flushInternal
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Flush out the data in client's user buffer. After the return of    * this call, new readers will see the data.    * @throws IOException if any error occurs    */
annotation|@
name|Override
DECL|method|hflush ()
specifier|public
name|void
name|hflush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|supportFlush
condition|)
block|{
name|flushInternal
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Force all data in the output stream to be written to Azure storage.    * Wait to return until this is complete. Close the access to the stream and    * shutdown the upload thread pool.    * If the blob was created, its lease will be released.    * Any error encountered caught in threads and stored will be rethrown here    * after cleanup.    */
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
try|try
block|{
name|flushInternal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|threadExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lastError
operator|=
operator|new
name|IOException
argument_list|(
name|FSExceptionMessages
operator|.
name|STREAM_IS_CLOSED
argument_list|)
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
name|bufferIndex
operator|=
literal|0
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
name|writeOperations
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|threadExecutor
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|threadExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|flushInternal (boolean isClose)
specifier|private
specifier|synchronized
name|void
name|flushInternal
parameter_list|(
name|boolean
name|isClose
parameter_list|)
throws|throws
name|IOException
block|{
name|maybeThrowLastError
argument_list|()
expr_stmt|;
name|writeCurrentBufferToService
argument_list|()
expr_stmt|;
name|flushWrittenBytesToService
argument_list|(
name|isClose
argument_list|)
expr_stmt|;
block|}
DECL|method|flushInternalAsync ()
specifier|private
specifier|synchronized
name|void
name|flushInternalAsync
parameter_list|()
throws|throws
name|IOException
block|{
name|maybeThrowLastError
argument_list|()
expr_stmt|;
name|writeCurrentBufferToService
argument_list|()
expr_stmt|;
name|flushWrittenBytesToServiceAsync
argument_list|()
expr_stmt|;
block|}
DECL|method|writeCurrentBufferToService ()
specifier|private
specifier|synchronized
name|void
name|writeCurrentBufferToService
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bufferIndex
operator|==
literal|0
condition|)
block|{
return|return;
block|}
specifier|final
name|byte
index|[]
name|bytes
init|=
name|buffer
decl_stmt|;
specifier|final
name|int
name|bytesLength
init|=
name|bufferIndex
decl_stmt|;
name|buffer
operator|=
name|byteBufferPool
operator|.
name|getBuffer
argument_list|(
literal|false
argument_list|,
name|bufferSize
argument_list|)
operator|.
name|array
argument_list|()
expr_stmt|;
name|bufferIndex
operator|=
literal|0
expr_stmt|;
specifier|final
name|long
name|offset
init|=
name|position
decl_stmt|;
name|position
operator|+=
name|bytesLength
expr_stmt|;
if|if
condition|(
name|threadExecutor
operator|.
name|getQueue
argument_list|()
operator|.
name|size
argument_list|()
operator|>=
name|maxConcurrentRequestCount
operator|*
literal|2
condition|)
block|{
name|waitForTaskToComplete
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Future
argument_list|<
name|Void
argument_list|>
name|job
init|=
name|completionService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|client
operator|.
name|append
argument_list|(
name|path
argument_list|,
name|offset
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytesLength
argument_list|)
expr_stmt|;
name|byteBufferPool
operator|.
name|putBuffer
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|writeOperations
operator|.
name|add
argument_list|(
operator|new
name|WriteOperation
argument_list|(
name|job
argument_list|,
name|offset
argument_list|,
name|bytesLength
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try to shrink the queue
name|shrinkWriteOperationQueue
argument_list|()
expr_stmt|;
block|}
DECL|method|flushWrittenBytesToService (boolean isClose)
specifier|private
specifier|synchronized
name|void
name|flushWrittenBytesToService
parameter_list|(
name|boolean
name|isClose
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|WriteOperation
name|writeOperation
range|:
name|writeOperations
control|)
block|{
try|try
block|{
name|writeOperation
operator|.
name|task
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AbfsRestOperationException
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|AbfsRestOperationException
operator|)
name|ex
operator|.
name|getCause
argument_list|()
operator|)
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
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|ex
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AzureBlobFileSystemException
condition|)
block|{
name|ex
operator|=
operator|(
name|AzureBlobFileSystemException
operator|)
name|ex
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
name|lastError
operator|=
operator|new
name|IOException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|lastError
throw|;
block|}
block|}
name|flushWrittenBytesToServiceInternal
argument_list|(
name|position
argument_list|,
literal|false
argument_list|,
name|isClose
argument_list|)
expr_stmt|;
block|}
DECL|method|flushWrittenBytesToServiceAsync ()
specifier|private
specifier|synchronized
name|void
name|flushWrittenBytesToServiceAsync
parameter_list|()
throws|throws
name|IOException
block|{
name|shrinkWriteOperationQueue
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|lastTotalAppendOffset
operator|>
name|this
operator|.
name|lastFlushOffset
condition|)
block|{
name|this
operator|.
name|flushWrittenBytesToServiceInternal
argument_list|(
name|this
operator|.
name|lastTotalAppendOffset
argument_list|,
literal|true
argument_list|,
literal|false
comment|/*Async flush on close not permitted*/
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|flushWrittenBytesToServiceInternal (final long offset, final boolean retainUncommitedData, final boolean isClose)
specifier|private
specifier|synchronized
name|void
name|flushWrittenBytesToServiceInternal
parameter_list|(
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|boolean
name|retainUncommitedData
parameter_list|,
specifier|final
name|boolean
name|isClose
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|client
operator|.
name|flush
argument_list|(
name|path
argument_list|,
name|offset
argument_list|,
name|retainUncommitedData
argument_list|,
name|isClose
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
if|if
condition|(
operator|(
operator|(
name|AbfsRestOperationException
operator|)
name|ex
operator|)
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
name|ex
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
name|this
operator|.
name|lastFlushOffset
operator|=
name|offset
expr_stmt|;
block|}
comment|/**    * Try to remove the completed write operations from the beginning of write    * operation FIFO queue.    */
DECL|method|shrinkWriteOperationQueue ()
specifier|private
specifier|synchronized
name|void
name|shrinkWriteOperationQueue
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
while|while
condition|(
name|writeOperations
operator|.
name|peek
argument_list|()
operator|!=
literal|null
operator|&&
name|writeOperations
operator|.
name|peek
argument_list|()
operator|.
name|task
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|writeOperations
operator|.
name|peek
argument_list|()
operator|.
name|task
operator|.
name|get
argument_list|()
expr_stmt|;
name|lastTotalAppendOffset
operator|+=
name|writeOperations
operator|.
name|peek
argument_list|()
operator|.
name|length
expr_stmt|;
name|writeOperations
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AzureBlobFileSystemException
condition|)
block|{
name|lastError
operator|=
operator|(
name|AzureBlobFileSystemException
operator|)
name|e
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|lastError
operator|=
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
throw|throw
name|lastError
throw|;
block|}
block|}
DECL|method|waitForTaskToComplete ()
specifier|private
name|void
name|waitForTaskToComplete
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|completed
decl_stmt|;
for|for
control|(
name|completed
operator|=
literal|false
init|;
name|completionService
operator|.
name|poll
argument_list|()
operator|!=
literal|null
condition|;
name|completed
operator|=
literal|true
control|)
block|{
comment|// keep polling until there is no data
block|}
if|if
condition|(
operator|!
name|completed
condition|)
block|{
try|try
block|{
name|completionService
operator|.
name|take
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|lastError
operator|=
operator|(
name|IOException
operator|)
operator|new
name|InterruptedIOException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|lastError
throw|;
block|}
block|}
block|}
DECL|class|WriteOperation
specifier|private
specifier|static
class|class
name|WriteOperation
block|{
DECL|field|task
specifier|private
specifier|final
name|Future
argument_list|<
name|Void
argument_list|>
name|task
decl_stmt|;
DECL|field|startOffset
specifier|private
specifier|final
name|long
name|startOffset
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|method|WriteOperation (final Future<Void> task, final long startOffset, final long length)
name|WriteOperation
parameter_list|(
specifier|final
name|Future
argument_list|<
name|Void
argument_list|>
name|task
parameter_list|,
specifier|final
name|long
name|startOffset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|task
argument_list|,
literal|"task"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|startOffset
operator|>=
literal|0
argument_list|,
literal|"startOffset"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|length
operator|>=
literal|0
argument_list|,
literal|"length"
argument_list|)
expr_stmt|;
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|waitForPendingUploads ()
specifier|public
specifier|synchronized
name|void
name|waitForPendingUploads
parameter_list|()
throws|throws
name|IOException
block|{
name|waitForTaskToComplete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

