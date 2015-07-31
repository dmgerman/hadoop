begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileDescriptor
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
name|concurrent
operator|.
name|ArrayBlockingQueue
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
name|TimeUnit
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
operator|.
name|POSIX
operator|.
name|POSIX_FADV_WILLNEED
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
name|ThreadFactoryBuilder
import|;
end_import

begin_comment
comment|/**  * Manages a pool of threads which can issue readahead requests on file descriptors.  */
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
DECL|class|ReadaheadPool
specifier|public
class|class
name|ReadaheadPool
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ReadaheadPool
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|POOL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|POOL_SIZE
init|=
literal|4
decl_stmt|;
DECL|field|MAX_POOL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_POOL_SIZE
init|=
literal|16
decl_stmt|;
DECL|field|CAPACITY
specifier|private
specifier|static
specifier|final
name|int
name|CAPACITY
init|=
literal|1024
decl_stmt|;
DECL|field|pool
specifier|private
specifier|final
name|ThreadPoolExecutor
name|pool
decl_stmt|;
DECL|field|instance
specifier|private
specifier|static
name|ReadaheadPool
name|instance
decl_stmt|;
comment|/**    * Return the singleton instance for the current process.    */
DECL|method|getInstance ()
specifier|public
specifier|static
name|ReadaheadPool
name|getInstance
parameter_list|()
block|{
synchronized|synchronized
init|(
name|ReadaheadPool
operator|.
name|class
init|)
block|{
if|if
condition|(
name|instance
operator|==
literal|null
operator|&&
name|NativeIO
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|instance
operator|=
operator|new
name|ReadaheadPool
argument_list|()
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
block|}
DECL|method|ReadaheadPool ()
specifier|private
name|ReadaheadPool
parameter_list|()
block|{
name|pool
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|POOL_SIZE
argument_list|,
name|MAX_POOL_SIZE
argument_list|,
literal|3L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|CAPACITY
argument_list|)
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setRejectedExecutionHandler
argument_list|(
operator|new
name|ThreadPoolExecutor
operator|.
name|DiscardOldestPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setThreadFactory
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Readahead Thread #%d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Issue a request to readahead on the given file descriptor.    *     * @param identifier a textual identifier that will be used in error    * messages (e.g. the file name)    * @param fd the file descriptor to read ahead    * @param curPos the current offset at which reads are being issued    * @param readaheadLength the configured length to read ahead    * @param maxOffsetToRead the maximum offset that will be readahead    *        (useful if, for example, only some segment of the file is    *        requested by the user). Pass {@link Long.MAX_VALUE} to allow    *        readahead to the end of the file.    * @param lastReadahead the result returned by the previous invocation    *        of this function on this file descriptor, or null if this is    *        the first call    * @return an object representing this outstanding request, or null    *        if no readahead was performed    */
DECL|method|readaheadStream ( String identifier, FileDescriptor fd, long curPos, long readaheadLength, long maxOffsetToRead, ReadaheadRequest lastReadahead)
specifier|public
name|ReadaheadRequest
name|readaheadStream
parameter_list|(
name|String
name|identifier
parameter_list|,
name|FileDescriptor
name|fd
parameter_list|,
name|long
name|curPos
parameter_list|,
name|long
name|readaheadLength
parameter_list|,
name|long
name|maxOffsetToRead
parameter_list|,
name|ReadaheadRequest
name|lastReadahead
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|curPos
operator|<=
name|maxOffsetToRead
argument_list|,
literal|"Readahead position %s higher than maxOffsetToRead %s"
argument_list|,
name|curPos
argument_list|,
name|maxOffsetToRead
argument_list|)
expr_stmt|;
if|if
condition|(
name|readaheadLength
operator|<=
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|lastOffset
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
if|if
condition|(
name|lastReadahead
operator|!=
literal|null
condition|)
block|{
name|lastOffset
operator|=
name|lastReadahead
operator|.
name|getOffset
argument_list|()
expr_stmt|;
block|}
comment|// trigger each readahead when we have reached the halfway mark
comment|// in the previous readahead. This gives the system time
comment|// to satisfy the readahead before we start reading the data.
name|long
name|nextOffset
init|=
name|lastOffset
operator|+
name|readaheadLength
operator|/
literal|2
decl_stmt|;
if|if
condition|(
name|curPos
operator|>=
name|nextOffset
condition|)
block|{
comment|// cancel any currently pending readahead, to avoid
comment|// piling things up in the queue. Each reader should have at most
comment|// one outstanding request in the queue.
if|if
condition|(
name|lastReadahead
operator|!=
literal|null
condition|)
block|{
name|lastReadahead
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|lastReadahead
operator|=
literal|null
expr_stmt|;
block|}
name|long
name|length
init|=
name|Math
operator|.
name|min
argument_list|(
name|readaheadLength
argument_list|,
name|maxOffsetToRead
operator|-
name|curPos
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|<=
literal|0
condition|)
block|{
comment|// we've reached the end of the stream
return|return
literal|null
return|;
block|}
return|return
name|submitReadahead
argument_list|(
name|identifier
argument_list|,
name|fd
argument_list|,
name|curPos
argument_list|,
name|length
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|lastReadahead
return|;
block|}
block|}
comment|/**    * Submit a request to readahead on the given file descriptor.    * @param identifier a textual identifier used in error messages, etc.    * @param fd the file descriptor to readahead    * @param off the offset at which to start the readahead    * @param len the number of bytes to read    * @return an object representing this pending request    */
DECL|method|submitReadahead ( String identifier, FileDescriptor fd, long off, long len)
specifier|public
name|ReadaheadRequest
name|submitReadahead
parameter_list|(
name|String
name|identifier
parameter_list|,
name|FileDescriptor
name|fd
parameter_list|,
name|long
name|off
parameter_list|,
name|long
name|len
parameter_list|)
block|{
name|ReadaheadRequestImpl
name|req
init|=
operator|new
name|ReadaheadRequestImpl
argument_list|(
name|identifier
argument_list|,
name|fd
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|pool
operator|.
name|execute
argument_list|(
name|req
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"submit readahead: "
operator|+
name|req
argument_list|)
expr_stmt|;
block|}
return|return
name|req
return|;
block|}
comment|/**    * An outstanding readahead request that has been submitted to    * the pool. This request may be pending or may have been    * completed.    */
DECL|interface|ReadaheadRequest
specifier|public
interface|interface
name|ReadaheadRequest
block|{
comment|/**      * Cancels the request for readahead. This should be used      * if the reader no longer needs the requested data,<em>before</em>      * closing the related file descriptor.      *       * It is safe to use even if the readahead request has already      * been fulfilled.      */
DECL|method|cancel ()
specifier|public
name|void
name|cancel
parameter_list|()
function_decl|;
comment|/**      * @return the requested offset      */
DECL|method|getOffset ()
specifier|public
name|long
name|getOffset
parameter_list|()
function_decl|;
comment|/**      * @return the requested length      */
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
function_decl|;
block|}
DECL|class|ReadaheadRequestImpl
specifier|private
specifier|static
class|class
name|ReadaheadRequestImpl
implements|implements
name|Runnable
implements|,
name|ReadaheadRequest
block|{
DECL|field|identifier
specifier|private
specifier|final
name|String
name|identifier
decl_stmt|;
DECL|field|fd
specifier|private
specifier|final
name|FileDescriptor
name|fd
decl_stmt|;
DECL|field|off
DECL|field|len
specifier|private
specifier|final
name|long
name|off
decl_stmt|,
name|len
decl_stmt|;
DECL|field|canceled
specifier|private
specifier|volatile
name|boolean
name|canceled
init|=
literal|false
decl_stmt|;
DECL|method|ReadaheadRequestImpl (String identifier, FileDescriptor fd, long off, long len)
specifier|private
name|ReadaheadRequestImpl
parameter_list|(
name|String
name|identifier
parameter_list|,
name|FileDescriptor
name|fd
parameter_list|,
name|long
name|off
parameter_list|,
name|long
name|len
parameter_list|)
block|{
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
name|this
operator|.
name|fd
operator|=
name|fd
expr_stmt|;
name|this
operator|.
name|off
operator|=
name|off
expr_stmt|;
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|canceled
condition|)
return|return;
comment|// There's a very narrow race here that the file will close right at
comment|// this instant. But if that happens, we'll likely receive an EBADF
comment|// error below, and see that it's canceled, ignoring the error.
comment|// It's also possible that we'll end up requesting readahead on some
comment|// other FD, which may be wasted work, but won't cause a problem.
try|try
block|{
name|NativeIO
operator|.
name|POSIX
operator|.
name|getCacheManipulator
argument_list|()
operator|.
name|posixFadviseIfPossible
argument_list|(
name|identifier
argument_list|,
name|fd
argument_list|,
name|off
argument_list|,
name|len
argument_list|,
name|POSIX_FADV_WILLNEED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|canceled
condition|)
block|{
comment|// no big deal - the reader canceled the request and closed
comment|// the file.
return|return;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed readahead on "
operator|+
name|identifier
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|cancel ()
specifier|public
name|void
name|cancel
parameter_list|()
block|{
name|canceled
operator|=
literal|true
expr_stmt|;
comment|// We could attempt to remove it from the work queue, but that would
comment|// add complexity. In practice, the work queues remain very short,
comment|// so removing canceled requests has no gain.
block|}
annotation|@
name|Override
DECL|method|getOffset ()
specifier|public
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|off
return|;
block|}
annotation|@
name|Override
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|len
return|;
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
literal|"ReadaheadRequestImpl [identifier='"
operator|+
name|identifier
operator|+
literal|"', fd="
operator|+
name|fd
operator|+
literal|", off="
operator|+
name|off
operator|+
literal|", len="
operator|+
name|len
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class

end_unit

