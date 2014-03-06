begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net.unix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
operator|.
name|unix
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|io
operator|.
name|IOUtils
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
name|channels
operator|.
name|ClosedChannelException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|locks
operator|.
name|Condition
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
name|locks
operator|.
name|ReentrantLock
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
name|SystemUtils
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
name|util
operator|.
name|NativeCodeLoader
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
name|Uninterruptibles
import|;
end_import

begin_comment
comment|/**  * The DomainSocketWatcher watches a set of domain sockets to see when they  * become readable, or closed.  When one of those events happens, it makes a  * callback.  *  * See {@link DomainSocket} for more information about UNIX domain sockets.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"HDFS"
argument_list|)
DECL|class|DomainSocketWatcher
specifier|public
specifier|final
class|class
name|DomainSocketWatcher
implements|implements
name|Closeable
block|{
static|static
block|{
if|if
condition|(
name|SystemUtils
operator|.
name|IS_OS_WINDOWS
condition|)
block|{
name|loadingFailureReason
operator|=
literal|"UNIX Domain sockets are not available on Windows."
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
name|loadingFailureReason
operator|=
literal|"libhadoop cannot be loaded."
expr_stmt|;
block|}
else|else
block|{
name|String
name|problem
decl_stmt|;
try|try
block|{
name|anchorNative
argument_list|()
expr_stmt|;
name|problem
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|problem
operator|=
literal|"DomainSocketWatcher#anchorNative got error: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
name|loadingFailureReason
operator|=
name|problem
expr_stmt|;
block|}
block|}
DECL|field|LOG
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DomainSocketWatcher
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The reason why DomainSocketWatcher is not available, or null if it is    * available.    */
DECL|field|loadingFailureReason
specifier|private
specifier|final
specifier|static
name|String
name|loadingFailureReason
decl_stmt|;
comment|/**    * Initializes the native library code.    */
DECL|method|anchorNative ()
specifier|private
specifier|static
specifier|native
name|void
name|anchorNative
parameter_list|()
function_decl|;
DECL|method|getLoadingFailureReason ()
specifier|public
specifier|static
name|String
name|getLoadingFailureReason
parameter_list|()
block|{
return|return
name|loadingFailureReason
return|;
block|}
DECL|interface|Handler
specifier|public
interface|interface
name|Handler
block|{
comment|/**      * Handles an event on a socket.  An event may be the socket becoming      * readable, or the remote end being closed.      *      * @param sock    The socket that the event occurred on.      * @return        Whether we should close the socket.      */
DECL|method|handle (DomainSocket sock)
name|boolean
name|handle
parameter_list|(
name|DomainSocket
name|sock
parameter_list|)
function_decl|;
block|}
comment|/**    * Handler for {DomainSocketWatcher#notificationSockets[1]}    */
DECL|class|NotificationHandler
specifier|private
class|class
name|NotificationHandler
implements|implements
name|Handler
block|{
DECL|method|handle (DomainSocket sock)
specifier|public
name|boolean
name|handle
parameter_list|(
name|DomainSocket
name|sock
parameter_list|)
block|{
try|try
block|{
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
name|this
operator|+
literal|": NotificationHandler: doing a read on "
operator|+
name|sock
operator|.
name|fd
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sock
operator|.
name|getInputStream
argument_list|()
operator|.
name|read
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
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
name|this
operator|+
literal|": NotificationHandler: got EOF on "
operator|+
name|sock
operator|.
name|fd
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
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
name|this
operator|+
literal|": NotificationHandler: read succeeded on "
operator|+
name|sock
operator|.
name|fd
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
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
name|this
operator|+
literal|": NotificationHandler: setting closed to "
operator|+
literal|"true for "
operator|+
name|sock
operator|.
name|fd
argument_list|)
expr_stmt|;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
DECL|class|Entry
specifier|private
specifier|static
class|class
name|Entry
block|{
DECL|field|socket
specifier|final
name|DomainSocket
name|socket
decl_stmt|;
DECL|field|handler
specifier|final
name|Handler
name|handler
decl_stmt|;
DECL|method|Entry (DomainSocket socket, Handler handler)
name|Entry
parameter_list|(
name|DomainSocket
name|socket
parameter_list|,
name|Handler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|socket
operator|=
name|socket
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
DECL|method|getDomainSocket ()
name|DomainSocket
name|getDomainSocket
parameter_list|()
block|{
return|return
name|socket
return|;
block|}
DECL|method|getHandler ()
name|Handler
name|getHandler
parameter_list|()
block|{
return|return
name|handler
return|;
block|}
block|}
comment|/**    * The FdSet is a set of file descriptors that gets passed to poll(2).    * It contains a native memory segment, so that we don't have to copy    * in the poll0 function.    */
DECL|class|FdSet
specifier|private
specifier|static
class|class
name|FdSet
block|{
DECL|field|data
specifier|private
name|long
name|data
decl_stmt|;
DECL|method|alloc0 ()
specifier|private
specifier|native
specifier|static
name|long
name|alloc0
parameter_list|()
function_decl|;
DECL|method|FdSet ()
name|FdSet
parameter_list|()
block|{
name|data
operator|=
name|alloc0
argument_list|()
expr_stmt|;
block|}
comment|/**      * Add a file descriptor to the set.      *      * @param fd   The file descriptor to add.      */
DECL|method|add (int fd)
specifier|native
name|void
name|add
parameter_list|(
name|int
name|fd
parameter_list|)
function_decl|;
comment|/**      * Remove a file descriptor from the set.      *      * @param fd   The file descriptor to remove.      */
DECL|method|remove (int fd)
specifier|native
name|void
name|remove
parameter_list|(
name|int
name|fd
parameter_list|)
function_decl|;
comment|/**      * Get an array containing all the FDs marked as readable.      * Also clear the state of all FDs.      *      * @return     An array containing all of the currently readable file      *             descriptors.      */
DECL|method|getAndClearReadableFds ()
specifier|native
name|int
index|[]
name|getAndClearReadableFds
parameter_list|()
function_decl|;
comment|/**      * Close the object and de-allocate the memory used.      */
DECL|method|close ()
specifier|native
name|void
name|close
parameter_list|()
function_decl|;
block|}
comment|/**    * Lock which protects toAdd, toRemove, and closed.    */
DECL|field|lock
specifier|private
specifier|final
name|ReentrantLock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
comment|/**    * Condition variable which indicates that toAdd and toRemove have been    * processed.    */
DECL|field|processedCond
specifier|private
specifier|final
name|Condition
name|processedCond
init|=
name|lock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
comment|/**    * Entries to add.    */
DECL|field|toAdd
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Entry
argument_list|>
name|toAdd
init|=
operator|new
name|LinkedList
argument_list|<
name|Entry
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Entries to remove.    */
DECL|field|toRemove
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|DomainSocket
argument_list|>
name|toRemove
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|DomainSocket
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Maximum length of time to go between checking whether the interrupted    * bit has been set for this thread.    */
DECL|field|interruptCheckPeriodMs
specifier|private
specifier|final
name|int
name|interruptCheckPeriodMs
decl_stmt|;
comment|/**    * A pair of sockets used to wake up the thread after it has called poll(2).    */
DECL|field|notificationSockets
specifier|private
specifier|final
name|DomainSocket
name|notificationSockets
index|[]
decl_stmt|;
comment|/**    * Whether or not this DomainSocketWatcher is closed.    */
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|DomainSocketWatcher (int interruptCheckPeriodMs)
specifier|public
name|DomainSocketWatcher
parameter_list|(
name|int
name|interruptCheckPeriodMs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|loadingFailureReason
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|loadingFailureReason
argument_list|)
throw|;
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|interruptCheckPeriodMs
operator|>
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|interruptCheckPeriodMs
operator|=
name|interruptCheckPeriodMs
expr_stmt|;
name|notificationSockets
operator|=
name|DomainSocket
operator|.
name|socketpair
argument_list|()
expr_stmt|;
name|watcherThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|watcherThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Close the DomainSocketWatcher and wait for its thread to terminate.    *    * If there is more than one close, all but the first will be ignored.    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|closed
condition|)
return|return;
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
name|this
operator|+
literal|": closing"
argument_list|)
expr_stmt|;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|// Close notificationSockets[0], so that notificationSockets[1] gets an EOF
comment|// event.  This will wake up the thread immediately if it is blocked inside
comment|// the select() system call.
name|notificationSockets
index|[
literal|0
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Wait for the select thread to terminate.
name|Uninterruptibles
operator|.
name|joinUninterruptibly
argument_list|(
name|watcherThread
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|isClosed ()
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|closed
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Add a socket.    *    * @param sock     The socket to add.  It is an error to re-add a socket that    *                   we are already watching.    * @param handler  The handler to associate with this socket.  This may be    *                   called any time after this function is called.    */
DECL|method|add (DomainSocket sock, Handler handler)
specifier|public
name|void
name|add
parameter_list|(
name|DomainSocket
name|sock
parameter_list|,
name|Handler
name|handler
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|closed
condition|)
block|{
name|handler
operator|.
name|handle
argument_list|(
name|sock
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|sock
argument_list|)
expr_stmt|;
return|return;
block|}
name|Entry
name|entry
init|=
operator|new
name|Entry
argument_list|(
name|sock
argument_list|,
name|handler
argument_list|)
decl_stmt|;
try|try
block|{
name|sock
operator|.
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e1
parameter_list|)
block|{
comment|// If the socket is already closed before we add it, invoke the
comment|// handler immediately.  Then we're done.
name|handler
operator|.
name|handle
argument_list|(
name|sock
argument_list|)
expr_stmt|;
return|return;
block|}
name|toAdd
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|kick
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|processedCond
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|toAdd
operator|.
name|contains
argument_list|(
name|entry
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Remove a socket.  Its handler will be called.    *    * @param sock     The socket to remove.    */
DECL|method|remove (DomainSocket sock)
specifier|public
name|void
name|remove
parameter_list|(
name|DomainSocket
name|sock
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|closed
condition|)
return|return;
name|toRemove
operator|.
name|put
argument_list|(
name|sock
operator|.
name|fd
argument_list|,
name|sock
argument_list|)
expr_stmt|;
name|kick
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|processedCond
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|toRemove
operator|.
name|containsKey
argument_list|(
name|sock
operator|.
name|fd
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Wake up the DomainSocketWatcher thread.    */
DECL|method|kick ()
specifier|private
name|void
name|kick
parameter_list|()
block|{
try|try
block|{
name|notificationSockets
index|[
literal|0
index|]
operator|.
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|this
operator|+
literal|": error writing to notificationSockets[0]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|sendCallback (String caller, TreeMap<Integer, Entry> entries, FdSet fdSet, int fd)
specifier|private
name|void
name|sendCallback
parameter_list|(
name|String
name|caller
parameter_list|,
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Entry
argument_list|>
name|entries
parameter_list|,
name|FdSet
name|fdSet
parameter_list|,
name|int
name|fd
parameter_list|)
block|{
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
name|this
operator|+
literal|": "
operator|+
name|caller
operator|+
literal|" starting sendCallback for fd "
operator|+
name|fd
argument_list|)
expr_stmt|;
block|}
name|Entry
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|fd
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|entry
argument_list|,
name|this
operator|+
literal|": fdSet contained "
operator|+
name|fd
operator|+
literal|", which we were "
operator|+
literal|"not tracking."
argument_list|)
expr_stmt|;
name|DomainSocket
name|sock
init|=
name|entry
operator|.
name|getDomainSocket
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|sock
argument_list|)
condition|)
block|{
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
name|this
operator|+
literal|": "
operator|+
name|caller
operator|+
literal|": closing fd "
operator|+
name|fd
operator|+
literal|" at the request of the handler."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|toRemove
operator|.
name|remove
argument_list|(
name|fd
argument_list|)
operator|!=
literal|null
condition|)
block|{
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
name|this
operator|+
literal|": "
operator|+
name|caller
operator|+
literal|" : sendCallback processed fd "
operator|+
name|fd
operator|+
literal|" in toRemove."
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|sock
operator|.
name|refCount
operator|.
name|unreferenceCheckClosed
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
literal|false
argument_list|,
name|this
operator|+
literal|": file descriptor "
operator|+
name|sock
operator|.
name|fd
operator|+
literal|" was closed while "
operator|+
literal|"still in the poll(2) loop."
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|sock
argument_list|)
expr_stmt|;
name|entries
operator|.
name|remove
argument_list|(
name|fd
argument_list|)
expr_stmt|;
name|fdSet
operator|.
name|remove
argument_list|(
name|fd
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|this
operator|+
literal|": "
operator|+
name|caller
operator|+
literal|": sendCallback not "
operator|+
literal|"closing fd "
operator|+
name|fd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|field|watcherThread
specifier|final
name|Thread
name|watcherThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
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
name|this
operator|+
literal|": starting with interruptCheckPeriodMs = "
operator|+
name|interruptCheckPeriodMs
argument_list|)
expr_stmt|;
block|}
specifier|final
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Entry
argument_list|>
argument_list|()
decl_stmt|;
name|FdSet
name|fdSet
init|=
operator|new
name|FdSet
argument_list|()
decl_stmt|;
name|addNotificationSocket
argument_list|(
name|entries
argument_list|,
name|fdSet
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|int
name|fd
range|:
name|fdSet
operator|.
name|getAndClearReadableFds
argument_list|()
control|)
block|{
name|sendCallback
argument_list|(
literal|"getAndClearReadableFds"
argument_list|,
name|entries
argument_list|,
name|fdSet
argument_list|,
name|fd
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|toAdd
operator|.
name|isEmpty
argument_list|()
operator|&&
name|toRemove
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
comment|// Handle pending additions (before pending removes).
for|for
control|(
name|Iterator
argument_list|<
name|Entry
argument_list|>
name|iter
init|=
name|toAdd
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Entry
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|DomainSocket
name|sock
init|=
name|entry
operator|.
name|getDomainSocket
argument_list|()
decl_stmt|;
name|Entry
name|prevEntry
init|=
name|entries
operator|.
name|put
argument_list|(
name|sock
operator|.
name|fd
argument_list|,
name|entry
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|prevEntry
operator|==
literal|null
argument_list|,
name|this
operator|+
literal|": tried to watch a file descriptor that we "
operator|+
literal|"were already watching: "
operator|+
name|sock
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
name|this
operator|+
literal|": adding fd "
operator|+
name|sock
operator|.
name|fd
argument_list|)
expr_stmt|;
block|}
name|fdSet
operator|.
name|add
argument_list|(
name|sock
operator|.
name|fd
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// Handle pending removals
while|while
condition|(
literal|true
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|DomainSocket
argument_list|>
name|entry
init|=
name|toRemove
operator|.
name|firstEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
break|break;
name|sendCallback
argument_list|(
literal|"handlePendingRemovals"
argument_list|,
name|entries
argument_list|,
name|fdSet
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|fd
argument_list|)
expr_stmt|;
block|}
name|processedCond
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
comment|// Check if the thread should terminate.  Doing this check now is
comment|// easier than at the beginning of the loop, since we know toAdd and
comment|// toRemove are now empty and processedCond has been notified if it
comment|// needed to be.
if|if
condition|(
name|closed
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
name|toString
argument_list|()
operator|+
literal|" thread terminating."
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
comment|// Check if someone sent our thread an InterruptedException while we
comment|// were waiting in poll().
if|if
condition|(
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InterruptedException
argument_list|()
throw|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|doPoll0
argument_list|(
name|interruptCheckPeriodMs
argument_list|,
name|fdSet
argument_list|)
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
name|info
argument_list|(
name|toString
argument_list|()
operator|+
literal|" terminating on InterruptedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|toString
argument_list|()
operator|+
literal|" terminating on IOException"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|kick
argument_list|()
expr_stmt|;
comment|// allow the handler for notificationSockets[0] to read a byte
for|for
control|(
name|Entry
name|entry
range|:
name|entries
operator|.
name|values
argument_list|()
control|)
block|{
name|sendCallback
argument_list|(
literal|"close"
argument_list|,
name|entries
argument_list|,
name|fdSet
argument_list|,
name|entry
operator|.
name|getDomainSocket
argument_list|()
operator|.
name|fd
argument_list|)
expr_stmt|;
block|}
name|entries
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fdSet
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
DECL|method|addNotificationSocket (final TreeMap<Integer, Entry> entries, FdSet fdSet)
specifier|private
name|void
name|addNotificationSocket
parameter_list|(
specifier|final
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Entry
argument_list|>
name|entries
parameter_list|,
name|FdSet
name|fdSet
parameter_list|)
block|{
name|entries
operator|.
name|put
argument_list|(
name|notificationSockets
index|[
literal|1
index|]
operator|.
name|fd
argument_list|,
operator|new
name|Entry
argument_list|(
name|notificationSockets
index|[
literal|1
index|]
argument_list|,
operator|new
name|NotificationHandler
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|notificationSockets
index|[
literal|1
index|]
operator|.
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|fdSet
operator|.
name|add
argument_list|(
name|notificationSockets
index|[
literal|1
index|]
operator|.
name|fd
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
name|this
operator|+
literal|": adding notificationSocket "
operator|+
name|notificationSockets
index|[
literal|1
index|]
operator|.
name|fd
operator|+
literal|", connected to "
operator|+
name|notificationSockets
index|[
literal|0
index|]
operator|.
name|fd
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DomainSocketWatcher("
operator|+
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
operator|+
literal|")"
return|;
block|}
DECL|method|doPoll0 (int maxWaitMs, FdSet readFds)
specifier|private
specifier|static
specifier|native
name|int
name|doPoll0
parameter_list|(
name|int
name|maxWaitMs
parameter_list|,
name|FdSet
name|readFds
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

