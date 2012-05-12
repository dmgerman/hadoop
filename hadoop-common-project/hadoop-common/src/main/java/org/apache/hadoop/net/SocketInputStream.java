begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketTimeoutException
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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
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
name|channels
operator|.
name|SelectableChannel
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
name|SelectionKey
import|;
end_import

begin_comment
comment|/**  * This implements an input stream that can have a timeout while reading.  * This sets non-blocking flag on the socket channel.  * So after create this object, read() on   * {@link Socket#getInputStream()} and write() on   * {@link Socket#getOutputStream()} for the associated socket will throw   * IllegalBlockingModeException.   * Please use {@link SocketOutputStream} for writing.  */
end_comment

begin_class
DECL|class|SocketInputStream
class|class
name|SocketInputStream
extends|extends
name|InputStream
implements|implements
name|ReadableByteChannel
block|{
DECL|field|reader
specifier|private
name|Reader
name|reader
decl_stmt|;
DECL|class|Reader
specifier|private
specifier|static
class|class
name|Reader
extends|extends
name|SocketIOWithTimeout
block|{
DECL|field|channel
name|ReadableByteChannel
name|channel
decl_stmt|;
DECL|method|Reader (ReadableByteChannel channel, long timeout)
name|Reader
parameter_list|(
name|ReadableByteChannel
name|channel
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|(
name|SelectableChannel
operator|)
name|channel
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
block|}
DECL|method|performIO (ByteBuffer buf)
name|int
name|performIO
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|channel
operator|.
name|read
argument_list|(
name|buf
argument_list|)
return|;
block|}
block|}
comment|/**    * Create a new input stream with the given timeout. If the timeout    * is zero, it will be treated as infinite timeout. The socket's    * channel will be configured to be non-blocking.    *     * @param channel     *        Channel for reading, should also be a {@link SelectableChannel}.    *        The channel will be configured to be non-blocking.    * @param timeout timeout in milliseconds. must not be negative.    * @throws IOException    */
DECL|method|SocketInputStream (ReadableByteChannel channel, long timeout)
specifier|public
name|SocketInputStream
parameter_list|(
name|ReadableByteChannel
name|channel
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|SocketIOWithTimeout
operator|.
name|checkChannelValidity
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|Reader
argument_list|(
name|channel
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * Same as SocketInputStream(socket.getChannel(), timeout):<br><br>    *     * Create a new input stream with the given timeout. If the timeout    * is zero, it will be treated as infinite timeout. The socket's    * channel will be configured to be non-blocking.    *     * @see SocketInputStream#SocketInputStream(ReadableByteChannel, long)    *      * @param socket should have a channel associated with it.    * @param timeout timeout timeout in milliseconds. must not be negative.    * @throws IOException    */
DECL|method|SocketInputStream (Socket socket, long timeout)
specifier|public
name|SocketInputStream
parameter_list|(
name|Socket
name|socket
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|socket
operator|.
name|getChannel
argument_list|()
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * Same as SocketInputStream(socket.getChannel(), socket.getSoTimeout())    * :<br><br>    *     * Create a new input stream with the given timeout. If the timeout    * is zero, it will be treated as infinite timeout. The socket's    * channel will be configured to be non-blocking.    * @see SocketInputStream#SocketInputStream(ReadableByteChannel, long)    *      * @param socket should have a channel associated with it.    * @throws IOException    */
DECL|method|SocketInputStream (Socket socket)
specifier|public
name|SocketInputStream
parameter_list|(
name|Socket
name|socket
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|socket
operator|.
name|getChannel
argument_list|()
argument_list|,
name|socket
operator|.
name|getSoTimeout
argument_list|()
argument_list|)
expr_stmt|;
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
comment|/* Allocation can be removed if required.      * probably no need to optimize or encourage single byte read.      */
name|byte
index|[]
name|buf
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
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|>
literal|0
condition|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|buf
index|[
literal|0
index|]
operator|&
literal|0xff
argument_list|)
return|;
block|}
if|if
condition|(
name|ret
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// unexpected
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not read from stream"
argument_list|)
throw|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|read (byte[] b, int off, int len)
specifier|public
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
return|return
name|read
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
argument_list|)
return|;
block|}
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* close the channel since Socket.getInputStream().close()      * closes the socket.      */
name|reader
operator|.
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns underlying channel used by inputstream.    * This is useful in certain cases like channel for     * {@link FileChannel#transferFrom(ReadableByteChannel, long, long)}.    */
DECL|method|getChannel ()
specifier|public
name|ReadableByteChannel
name|getChannel
parameter_list|()
block|{
return|return
name|reader
operator|.
name|channel
return|;
block|}
comment|//ReadableByteChannel interface
DECL|method|isOpen ()
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|reader
operator|.
name|isOpen
argument_list|()
return|;
block|}
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
return|return
name|reader
operator|.
name|doIO
argument_list|(
name|dst
argument_list|,
name|SelectionKey
operator|.
name|OP_READ
argument_list|)
return|;
block|}
comment|/**    * waits for the underlying channel to be ready for reading.    * The timeout specified for this stream applies to this wait.    *     * @throws SocketTimeoutException     *         if select on the channel times out.    * @throws IOException    *         if any other I/O error occurs.     */
DECL|method|waitForReadable ()
specifier|public
name|void
name|waitForReadable
parameter_list|()
throws|throws
name|IOException
block|{
name|reader
operator|.
name|waitForIO
argument_list|(
name|SelectionKey
operator|.
name|OP_READ
argument_list|)
expr_stmt|;
block|}
DECL|method|setTimeout (long timeoutMs)
specifier|public
name|void
name|setTimeout
parameter_list|(
name|long
name|timeoutMs
parameter_list|)
block|{
name|reader
operator|.
name|setTimeout
argument_list|(
name|timeoutMs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

