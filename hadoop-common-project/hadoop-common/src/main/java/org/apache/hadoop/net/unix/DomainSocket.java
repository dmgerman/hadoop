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
name|FileInputStream
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
name|InputStream
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
name|nio
operator|.
name|channels
operator|.
name|ReadableByteChannel
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|CloseableReferenceCount
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

begin_comment
comment|/**  * The implementation of UNIX domain sockets in Java.  *   * See {@link DomainSocket} for more information about UNIX domain sockets.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"HDFS"
argument_list|)
DECL|class|DomainSocket
specifier|public
class|class
name|DomainSocket
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
literal|"DomainSocket#anchorNative got error: "
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
name|DomainSocket
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * True only if we should validate the paths used in {@link DomainSocket#bind()}    */
DECL|field|validateBindPaths
specifier|private
specifier|static
name|boolean
name|validateBindPaths
init|=
literal|true
decl_stmt|;
comment|/**    * The reason why DomainSocket is not available, or null if it is available.    */
DECL|field|loadingFailureReason
specifier|private
specifier|final
specifier|static
name|String
name|loadingFailureReason
decl_stmt|;
comment|/**    * Initialize the native library code.    */
DECL|method|anchorNative ()
specifier|private
specifier|static
specifier|native
name|void
name|anchorNative
parameter_list|()
function_decl|;
comment|/**    * This function is designed to validate that the path chosen for a UNIX    * domain socket is secure.  A socket path is secure if it doesn't allow    * unprivileged users to perform a man-in-the-middle attack against it.    * For example, one way to perform a man-in-the-middle attack would be for    * a malicious user to move the server socket out of the way and create his    * own socket in the same place.  Not good.    *     * Note that we only check the path once.  It's possible that the    * permissions on the path could change, perhaps to something more relaxed,    * immediately after the path passes our validation test-- hence creating a    * security hole.  However, the purpose of this check is to spot common    * misconfigurations.  System administrators do not commonly change    * permissions on these paths while the server is running.    *    * @param path             the path to validate    * @param skipComponents   the number of starting path components to skip     *                         validation for (used only for testing)    */
annotation|@
name|VisibleForTesting
DECL|method|validateSocketPathSecurity0 (String path, int skipComponents)
specifier|native
specifier|static
name|void
name|validateSocketPathSecurity0
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|skipComponents
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return true only if UNIX domain sockets are available.    */
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
comment|/**    * Disable validation of the server bind paths.    */
annotation|@
name|VisibleForTesting
DECL|method|disableBindPathValidation ()
specifier|public
specifier|static
name|void
name|disableBindPathValidation
parameter_list|()
block|{
name|validateBindPaths
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Given a path and a port, compute the effective path by replacing    * occurrences of _PORT with the port.  This is mainly to make it     * possible to run multiple DataNodes locally for testing purposes.    *    * @param path            The source path    * @param port            Port number to use    *    * @return                The effective path    */
DECL|method|getEffectivePath (String path, int port)
specifier|public
specifier|static
name|String
name|getEffectivePath
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|port
parameter_list|)
block|{
return|return
name|path
operator|.
name|replace
argument_list|(
literal|"_PORT"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|port
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * The socket reference count and closed bit.    */
DECL|field|refCount
specifier|final
name|CloseableReferenceCount
name|refCount
decl_stmt|;
comment|/**    * The file descriptor associated with this UNIX domain socket.    */
DECL|field|fd
specifier|final
name|int
name|fd
decl_stmt|;
comment|/**    * The path associated with this UNIX domain socket.    */
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
comment|/**    * The InputStream associated with this socket.    */
DECL|field|inputStream
specifier|private
specifier|final
name|DomainInputStream
name|inputStream
init|=
operator|new
name|DomainInputStream
argument_list|()
decl_stmt|;
comment|/**    * The OutputStream associated with this socket.    */
DECL|field|outputStream
specifier|private
specifier|final
name|DomainOutputStream
name|outputStream
init|=
operator|new
name|DomainOutputStream
argument_list|()
decl_stmt|;
comment|/**    * The Channel associated with this socket.    */
DECL|field|channel
specifier|private
specifier|final
name|DomainChannel
name|channel
init|=
operator|new
name|DomainChannel
argument_list|()
decl_stmt|;
DECL|method|DomainSocket (String path, int fd)
specifier|private
name|DomainSocket
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|fd
parameter_list|)
block|{
name|this
operator|.
name|refCount
operator|=
operator|new
name|CloseableReferenceCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|fd
operator|=
name|fd
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|bind0 (String path)
specifier|private
specifier|static
specifier|native
name|int
name|bind0
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|unreference (boolean checkClosed)
specifier|private
name|void
name|unreference
parameter_list|(
name|boolean
name|checkClosed
parameter_list|)
throws|throws
name|ClosedChannelException
block|{
if|if
condition|(
name|checkClosed
condition|)
block|{
name|refCount
operator|.
name|unreferenceCheckClosed
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|refCount
operator|.
name|unreference
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Create a new DomainSocket listening on the given path.    *    * @param path         The path to bind and listen on.    * @return             The new DomainSocket.    */
DECL|method|bindAndListen (String path)
specifier|public
specifier|static
name|DomainSocket
name|bindAndListen
parameter_list|(
name|String
name|path
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
if|if
condition|(
name|validateBindPaths
condition|)
block|{
name|validateSocketPathSecurity0
argument_list|(
name|path
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|int
name|fd
init|=
name|bind0
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
operator|new
name|DomainSocket
argument_list|(
name|path
argument_list|,
name|fd
argument_list|)
return|;
block|}
comment|/**    * Create a pair of UNIX domain sockets which are connected to each other    * by calling socketpair(2).    *    * @return                An array of two UNIX domain sockets connected to    *                        each other.    * @throws IOException    on error.    */
DECL|method|socketpair ()
specifier|public
specifier|static
name|DomainSocket
index|[]
name|socketpair
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|fds
index|[]
init|=
name|socketpair0
argument_list|()
decl_stmt|;
return|return
operator|new
name|DomainSocket
index|[]
block|{
operator|new
name|DomainSocket
argument_list|(
literal|"(anonymous0)"
argument_list|,
name|fds
index|[
literal|0
index|]
argument_list|)
block|,
operator|new
name|DomainSocket
argument_list|(
literal|"(anonymous1)"
argument_list|,
name|fds
index|[
literal|1
index|]
argument_list|)
block|}
return|;
block|}
DECL|method|socketpair0 ()
specifier|private
specifier|static
specifier|native
name|int
index|[]
name|socketpair0
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|accept0 (int fd)
specifier|private
specifier|static
specifier|native
name|int
name|accept0
parameter_list|(
name|int
name|fd
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Accept a new UNIX domain connection.    *    * This method can only be used on sockets that were bound with bind().    *    * @return                              The new connection.    * @throws IOException                  If there was an I/O error    *                                      performing the accept-- such as the    *                                      socket being closed from under us.    * @throws SocketTimeoutException       If the accept timed out.    */
DECL|method|accept ()
specifier|public
name|DomainSocket
name|accept
parameter_list|()
throws|throws
name|IOException
block|{
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|DomainSocket
name|ret
init|=
operator|new
name|DomainSocket
argument_list|(
name|path
argument_list|,
name|accept0
argument_list|(
name|fd
argument_list|)
argument_list|)
decl_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|connect0 (String path)
specifier|private
specifier|static
specifier|native
name|int
name|connect0
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
comment|/**    * Create a new DomainSocket connected to the given path.    *    * @param path         The path to connect to.    * @return             The new DomainSocket.    */
DECL|method|connect (String path)
specifier|public
specifier|static
name|DomainSocket
name|connect
parameter_list|(
name|String
name|path
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
name|int
name|fd
init|=
name|connect0
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
operator|new
name|DomainSocket
argument_list|(
name|path
argument_list|,
name|fd
argument_list|)
return|;
block|}
comment|/**    * Return true if the file descriptor is currently open.    *    * @return                 True if the file descriptor is currently open.    */
DECL|method|isOpen ()
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|refCount
operator|.
name|isOpen
argument_list|()
return|;
block|}
comment|/**    * @return                 The socket path.    */
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
comment|/**    * @return                 The socket InputStream    */
DECL|method|getInputStream ()
specifier|public
name|DomainInputStream
name|getInputStream
parameter_list|()
block|{
return|return
name|inputStream
return|;
block|}
comment|/**    * @return                 The socket OutputStream    */
DECL|method|getOutputStream ()
specifier|public
name|DomainOutputStream
name|getOutputStream
parameter_list|()
block|{
return|return
name|outputStream
return|;
block|}
comment|/**    * @return                 The socket Channel    */
DECL|method|getChannel ()
specifier|public
name|DomainChannel
name|getChannel
parameter_list|()
block|{
return|return
name|channel
return|;
block|}
DECL|field|SEND_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|SEND_BUFFER_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|RECEIVE_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|RECEIVE_BUFFER_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|SEND_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|SEND_TIMEOUT
init|=
literal|3
decl_stmt|;
DECL|field|RECEIVE_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|RECEIVE_TIMEOUT
init|=
literal|4
decl_stmt|;
DECL|method|setAttribute0 (int fd, int type, int val)
specifier|private
specifier|static
specifier|native
name|void
name|setAttribute0
parameter_list|(
name|int
name|fd
parameter_list|,
name|int
name|type
parameter_list|,
name|int
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|setAttribute (int type, int size)
specifier|public
name|void
name|setAttribute
parameter_list|(
name|int
name|type
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|setAttribute0
argument_list|(
name|fd
argument_list|,
name|type
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAttribute0 (int fd, int type)
specifier|private
specifier|native
name|int
name|getAttribute0
parameter_list|(
name|int
name|fd
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getAttribute (int type)
specifier|public
name|int
name|getAttribute
parameter_list|(
name|int
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|int
name|attribute
decl_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|attribute
operator|=
name|getAttribute0
argument_list|(
name|fd
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
return|return
name|attribute
return|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|close0 (int fd)
specifier|private
specifier|static
specifier|native
name|void
name|close0
parameter_list|(
name|int
name|fd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|closeFileDescriptor0 (FileDescriptor fd)
specifier|private
specifier|static
specifier|native
name|void
name|closeFileDescriptor0
parameter_list|(
name|FileDescriptor
name|fd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|shutdown0 (int fd)
specifier|private
specifier|static
specifier|native
name|void
name|shutdown0
parameter_list|(
name|int
name|fd
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the Socket.    */
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
comment|// Set the closed bit on this DomainSocket
name|int
name|count
decl_stmt|;
try|try
block|{
name|count
operator|=
name|refCount
operator|.
name|setClosed
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
comment|// Someone else already closed the DomainSocket.
return|return;
block|}
comment|// Wait for all references to go away
name|boolean
name|didShutdown
init|=
literal|false
decl_stmt|;
name|boolean
name|interrupted
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|count
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|didShutdown
condition|)
block|{
try|try
block|{
comment|// Calling shutdown on the socket will interrupt blocking system
comment|// calls like accept, write, and read that are going on in a
comment|// different thread.
name|shutdown0
argument_list|(
name|fd
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
literal|"shutdown error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|didShutdown
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|interrupted
operator|=
literal|true
expr_stmt|;
block|}
name|count
operator|=
name|refCount
operator|.
name|getReferenceCount
argument_list|()
expr_stmt|;
block|}
comment|// At this point, nobody has a reference to the file descriptor,
comment|// and nobody will be able to get one in the future either.
comment|// We now call close(2) on the file descriptor.
comment|// After this point, the file descriptor number will be reused by
comment|// something else.  Although this DomainSocket object continues to hold
comment|// the old file descriptor number (it's a final field), we never use it
comment|// again because this DomainSocket is closed.
name|close0
argument_list|(
name|fd
argument_list|)
expr_stmt|;
if|if
condition|(
name|interrupted
condition|)
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
block|}
comment|/**    * Call shutdown(SHUT_RDWR) on the UNIX domain socket.    *    * @throws IOException    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|shutdown0
argument_list|(
name|fd
argument_list|)
expr_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendFileDescriptors0 (int fd, FileDescriptor descriptors[], byte jbuf[], int offset, int length)
specifier|private
specifier|native
specifier|static
name|void
name|sendFileDescriptors0
parameter_list|(
name|int
name|fd
parameter_list|,
name|FileDescriptor
name|descriptors
index|[]
parameter_list|,
name|byte
name|jbuf
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Send some FileDescriptor objects to the process on the other side of this    * socket.    *     * @param descriptors       The file descriptors to send.    * @param jbuf              Some bytes to send.  You must send at least    *                          one byte.    * @param offset            The offset in the jbuf array to start at.    * @param length            Length of the jbuf array to use.    */
DECL|method|sendFileDescriptors (FileDescriptor descriptors[], byte jbuf[], int offset, int length)
specifier|public
name|void
name|sendFileDescriptors
parameter_list|(
name|FileDescriptor
name|descriptors
index|[]
parameter_list|,
name|byte
name|jbuf
index|[]
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
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|sendFileDescriptors0
argument_list|(
name|fd
argument_list|,
name|descriptors
argument_list|,
name|jbuf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|receiveFileDescriptors0 (int fd, FileDescriptor[] descriptors, byte jbuf[], int offset, int length)
specifier|private
specifier|static
specifier|native
name|int
name|receiveFileDescriptors0
parameter_list|(
name|int
name|fd
parameter_list|,
name|FileDescriptor
index|[]
name|descriptors
parameter_list|,
name|byte
name|jbuf
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Receive some FileDescriptor objects from the process on the other side of    * this socket.    *    * @param descriptors       (output parameter) Array of FileDescriptors.    *                          We will fill as many slots as possible with file    *                          descriptors passed from the remote process.  The    *                          other slots will contain NULL.    * @param jbuf              (output parameter) Buffer to read into.    *                          The UNIX domain sockets API requires you to read    *                          at least one byte from the remote process, even    *                          if all you care about is the file descriptors    *                          you will receive.    * @param offset            Offset into the byte buffer to load data    * @param length            Length of the byte buffer to use for data    *    * @return                  The number of bytes read.  This will be -1 if we    *                          reached EOF (similar to SocketInputStream);    *                          otherwise, it will be positive.    * @throws                  IOException if there was an I/O error.    */
DECL|method|receiveFileDescriptors (FileDescriptor[] descriptors, byte jbuf[], int offset, int length)
specifier|public
name|int
name|receiveFileDescriptors
parameter_list|(
name|FileDescriptor
index|[]
name|descriptors
parameter_list|,
name|byte
name|jbuf
index|[]
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
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|int
name|nBytes
init|=
name|receiveFileDescriptors0
argument_list|(
name|fd
argument_list|,
name|descriptors
argument_list|,
name|jbuf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
return|return
name|nBytes
return|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Receive some FileDescriptor objects from the process on the other side of    * this socket, and wrap them in FileInputStream objects.    *    * See {@link DomainSocket#recvFileInputStreams(ByteBuffer)}    */
DECL|method|recvFileInputStreams (FileInputStream[] streams, byte buf[], int offset, int length)
specifier|public
name|int
name|recvFileInputStreams
parameter_list|(
name|FileInputStream
index|[]
name|streams
parameter_list|,
name|byte
name|buf
index|[]
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
name|FileDescriptor
name|descriptors
index|[]
init|=
operator|new
name|FileDescriptor
index|[
name|streams
operator|.
name|length
index|]
decl_stmt|;
name|boolean
name|success
init|=
literal|false
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
name|streams
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|streams
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
try|try
block|{
name|int
name|ret
init|=
name|receiveFileDescriptors0
argument_list|(
name|fd
argument_list|,
name|descriptors
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|descriptors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|descriptors
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|streams
index|[
name|j
operator|++
index|]
operator|=
operator|new
name|FileInputStream
argument_list|(
name|descriptors
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|descriptors
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|descriptors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|descriptors
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|closeFileDescriptor0
argument_list|(
name|descriptors
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|streams
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|streams
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|streams
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
name|unreference
argument_list|(
operator|!
name|success
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readArray0 (int fd, byte b[], int off, int len)
specifier|private
specifier|native
specifier|static
name|int
name|readArray0
parameter_list|(
name|int
name|fd
parameter_list|,
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
function_decl|;
DECL|method|available0 (int fd)
specifier|private
specifier|native
specifier|static
name|int
name|available0
parameter_list|(
name|int
name|fd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|write0 (int fd, int b)
specifier|private
specifier|static
specifier|native
name|void
name|write0
parameter_list|(
name|int
name|fd
parameter_list|,
name|int
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeArray0 (int fd, byte b[], int offset, int length)
specifier|private
specifier|static
specifier|native
name|void
name|writeArray0
parameter_list|(
name|int
name|fd
parameter_list|,
name|byte
name|b
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|readByteBufferDirect0 (int fd, ByteBuffer dst, int position, int remaining)
specifier|private
specifier|native
specifier|static
name|int
name|readByteBufferDirect0
parameter_list|(
name|int
name|fd
parameter_list|,
name|ByteBuffer
name|dst
parameter_list|,
name|int
name|position
parameter_list|,
name|int
name|remaining
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Input stream for UNIX domain sockets.    */
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"HDFS"
argument_list|)
DECL|class|DomainInputStream
specifier|public
class|class
name|DomainInputStream
extends|extends
name|InputStream
block|{
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
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|byte
name|b
index|[]
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|int
name|ret
init|=
name|DomainSocket
operator|.
name|readArray0
argument_list|(
name|DomainSocket
operator|.
name|this
operator|.
name|fd
argument_list|,
name|b
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
return|return
operator|(
name|ret
operator|>=
literal|0
operator|)
condition|?
name|b
index|[
literal|0
index|]
else|:
operator|-
literal|1
return|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|read (byte b[], int off, int len)
specifier|public
name|int
name|read
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
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|int
name|nRead
init|=
name|DomainSocket
operator|.
name|readArray0
argument_list|(
name|DomainSocket
operator|.
name|this
operator|.
name|fd
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
return|return
name|nRead
return|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|available ()
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|int
name|nAvailable
init|=
name|DomainSocket
operator|.
name|available0
argument_list|(
name|DomainSocket
operator|.
name|this
operator|.
name|fd
argument_list|)
decl_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
return|return
name|nAvailable
return|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
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
name|DomainSocket
operator|.
name|this
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Output stream for UNIX domain sockets.    */
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"HDFS"
argument_list|)
DECL|class|DomainOutputStream
specifier|public
class|class
name|DomainOutputStream
extends|extends
name|OutputStream
block|{
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
name|DomainSocket
operator|.
name|this
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (int val)
specifier|public
name|void
name|write
parameter_list|(
name|int
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|byte
name|b
index|[]
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|b
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|val
expr_stmt|;
name|DomainSocket
operator|.
name|writeArray0
argument_list|(
name|DomainSocket
operator|.
name|this
operator|.
name|fd
argument_list|,
name|b
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
name|void
name|write
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
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|DomainSocket
operator|.
name|writeArray0
argument_list|(
name|DomainSocket
operator|.
name|this
operator|.
name|fd
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|exc
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"HDFS"
argument_list|)
DECL|class|DomainChannel
specifier|public
class|class
name|DomainChannel
implements|implements
name|ReadableByteChannel
block|{
annotation|@
name|Override
DECL|method|isOpen ()
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|DomainSocket
operator|.
name|this
operator|.
name|isOpen
argument_list|()
return|;
block|}
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
name|DomainSocket
operator|.
name|this
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read (ByteBuffer dst)
specifier|public
name|int
name|read
parameter_list|(
name|ByteBuffer
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|refCount
operator|.
name|reference
argument_list|()
expr_stmt|;
name|boolean
name|exc
init|=
literal|true
decl_stmt|;
try|try
block|{
name|int
name|nread
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|dst
operator|.
name|isDirect
argument_list|()
condition|)
block|{
name|nread
operator|=
name|DomainSocket
operator|.
name|readByteBufferDirect0
argument_list|(
name|DomainSocket
operator|.
name|this
operator|.
name|fd
argument_list|,
name|dst
argument_list|,
name|dst
operator|.
name|position
argument_list|()
argument_list|,
name|dst
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dst
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|nread
operator|=
name|DomainSocket
operator|.
name|readArray0
argument_list|(
name|DomainSocket
operator|.
name|this
operator|.
name|fd
argument_list|,
name|dst
operator|.
name|array
argument_list|()
argument_list|,
name|dst
operator|.
name|position
argument_list|()
operator|+
name|dst
operator|.
name|arrayOffset
argument_list|()
argument_list|,
name|dst
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"we don't support "
operator|+
literal|"using ByteBuffers that aren't either direct or backed by "
operator|+
literal|"arrays"
argument_list|)
throw|;
block|}
if|if
condition|(
name|nread
operator|>
literal|0
condition|)
block|{
name|dst
operator|.
name|position
argument_list|(
name|dst
operator|.
name|position
argument_list|()
operator|+
name|nread
argument_list|)
expr_stmt|;
block|}
name|exc
operator|=
literal|false
expr_stmt|;
return|return
name|nread
return|;
block|}
finally|finally
block|{
name|unreference
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
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
name|String
operator|.
name|format
argument_list|(
literal|"DomainSocket(fd=%d,path=%s)"
argument_list|,
name|fd
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

