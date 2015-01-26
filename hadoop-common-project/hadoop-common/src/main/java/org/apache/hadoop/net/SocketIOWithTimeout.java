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
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|Selector
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
name|SocketChannel
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
name|spi
operator|.
name|SelectorProvider
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
name|Time
import|;
end_import

begin_comment
comment|/**  * This supports input and output streams for a socket channels.   * These streams can have a timeout.  */
end_comment

begin_class
DECL|class|SocketIOWithTimeout
specifier|abstract
class|class
name|SocketIOWithTimeout
block|{
comment|// This is intentionally package private.
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
name|SocketIOWithTimeout
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|channel
specifier|private
name|SelectableChannel
name|channel
decl_stmt|;
DECL|field|timeout
specifier|private
name|long
name|timeout
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|selector
specifier|private
specifier|static
name|SelectorPool
name|selector
init|=
operator|new
name|SelectorPool
argument_list|()
decl_stmt|;
comment|/* A timeout value of 0 implies wait for ever.     * We should have a value of timeout that implies zero wait.. i.e.     * read or write returns immediately.    *     * This will set channel to non-blocking.    */
DECL|method|SocketIOWithTimeout (SelectableChannel channel, long timeout)
name|SocketIOWithTimeout
parameter_list|(
name|SelectableChannel
name|channel
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|checkChannelValidity
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
comment|// Set non-blocking
name|channel
operator|.
name|configureBlocking
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|isOpen ()
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
operator|!
name|closed
operator|&&
name|channel
operator|.
name|isOpen
argument_list|()
return|;
block|}
DECL|method|getChannel ()
name|SelectableChannel
name|getChannel
parameter_list|()
block|{
return|return
name|channel
return|;
block|}
comment|/**     * Utility function to check if channel is ok.    * Mainly to throw IOException instead of runtime exception    * in case of mismatch. This mismatch can occur for many runtime    * reasons.    */
DECL|method|checkChannelValidity (Object channel)
specifier|static
name|void
name|checkChannelValidity
parameter_list|(
name|Object
name|channel
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|channel
operator|==
literal|null
condition|)
block|{
comment|/* Most common reason is that original socket does not have a channel.        * So making this an IOException rather than a RuntimeException.        */
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Channel is null. Check "
operator|+
literal|"how the channel or socket is created."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|channel
operator|instanceof
name|SelectableChannel
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Channel should be a SelectableChannel"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Performs actual IO operations. This is not expected to block.    *      * @param buf    * @return number of bytes (or some equivalent). 0 implies underlying    *         channel is drained completely. We will wait if more IO is     *         required.    * @throws IOException    */
DECL|method|performIO (ByteBuffer buf)
specifier|abstract
name|int
name|performIO
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Performs one IO and returns number of bytes read or written.    * It waits up to the specified timeout. If the channel is     * not read before the timeout, SocketTimeoutException is thrown.    *     * @param buf buffer for IO    * @param ops Selection Ops used for waiting. Suggested values:     *        SelectionKey.OP_READ while reading and SelectionKey.OP_WRITE while    *        writing.     *            * @return number of bytes read or written. negative implies end of stream.    * @throws IOException    */
DECL|method|doIO (ByteBuffer buf, int ops)
name|int
name|doIO
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|,
name|int
name|ops
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* For now only one thread is allowed. If user want to read or write      * from multiple threads, multiple streams could be created. In that      * case multiple threads work as well as underlying channel supports it.      */
if|if
condition|(
operator|!
name|buf
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Buffer has no data left."
argument_list|)
throw|;
comment|//or should we just return 0?
block|}
while|while
condition|(
name|buf
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
name|int
name|n
init|=
name|performIO
argument_list|(
name|buf
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|0
condition|)
block|{
comment|// successful io or an error.
return|return
name|n
return|;
block|}
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
name|channel
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
comment|//now wait for socket to be ready.
name|int
name|count
init|=
literal|0
decl_stmt|;
try|try
block|{
name|count
operator|=
name|selector
operator|.
name|select
argument_list|(
name|channel
argument_list|,
name|ops
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//unexpected IOException.
name|closed
operator|=
literal|true
expr_stmt|;
throw|throw
name|e
throw|;
block|}
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|SocketTimeoutException
argument_list|(
name|timeoutExceptionString
argument_list|(
name|channel
argument_list|,
name|timeout
argument_list|,
name|ops
argument_list|)
argument_list|)
throw|;
block|}
comment|// otherwise the socket should be ready for io.
block|}
return|return
literal|0
return|;
comment|// does not reach here.
block|}
comment|/**    * The contract is similar to {@link SocketChannel#connect(SocketAddress)}     * with a timeout.    *     * @see SocketChannel#connect(SocketAddress)    *     * @param channel - this should be a {@link SelectableChannel}    * @param endpoint    * @throws IOException    */
DECL|method|connect (SocketChannel channel, SocketAddress endpoint, int timeout)
specifier|static
name|void
name|connect
parameter_list|(
name|SocketChannel
name|channel
parameter_list|,
name|SocketAddress
name|endpoint
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|blockingOn
init|=
name|channel
operator|.
name|isBlocking
argument_list|()
decl_stmt|;
if|if
condition|(
name|blockingOn
condition|)
block|{
name|channel
operator|.
name|configureBlocking
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|channel
operator|.
name|connect
argument_list|(
name|endpoint
argument_list|)
condition|)
block|{
return|return;
block|}
name|long
name|timeoutLeft
init|=
name|timeout
decl_stmt|;
name|long
name|endTime
init|=
operator|(
name|timeout
operator|>
literal|0
operator|)
condition|?
operator|(
name|Time
operator|.
name|now
argument_list|()
operator|+
name|timeout
operator|)
else|:
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// we might have to call finishConnect() more than once
comment|// for some channels (with user level protocols)
name|int
name|ret
init|=
name|selector
operator|.
name|select
argument_list|(
operator|(
name|SelectableChannel
operator|)
name|channel
argument_list|,
name|SelectionKey
operator|.
name|OP_CONNECT
argument_list|,
name|timeoutLeft
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|>
literal|0
operator|&&
name|channel
operator|.
name|finishConnect
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|ret
operator|==
literal|0
operator|||
operator|(
name|timeout
operator|>
literal|0
operator|&&
operator|(
name|timeoutLeft
operator|=
operator|(
name|endTime
operator|-
name|Time
operator|.
name|now
argument_list|()
operator|)
operator|)
operator|<=
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|SocketTimeoutException
argument_list|(
name|timeoutExceptionString
argument_list|(
name|channel
argument_list|,
name|timeout
argument_list|,
name|SelectionKey
operator|.
name|OP_CONNECT
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// javadoc for SocketChannel.connect() says channel should be closed.
try|try
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|blockingOn
operator|&&
name|channel
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|channel
operator|.
name|configureBlocking
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * This is similar to {@link #doIO(ByteBuffer, int)} except that it    * does not perform any I/O. It just waits for the channel to be ready    * for I/O as specified in ops.    *     * @param ops Selection Ops used for waiting    *     * @throws SocketTimeoutException     *         if select on the channel times out.    * @throws IOException    *         if any other I/O error occurs.     */
DECL|method|waitForIO (int ops)
name|void
name|waitForIO
parameter_list|(
name|int
name|ops
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|selector
operator|.
name|select
argument_list|(
name|channel
argument_list|,
name|ops
argument_list|,
name|timeout
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|SocketTimeoutException
argument_list|(
name|timeoutExceptionString
argument_list|(
name|channel
argument_list|,
name|timeout
argument_list|,
name|ops
argument_list|)
argument_list|)
throw|;
block|}
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
name|this
operator|.
name|timeout
operator|=
name|timeoutMs
expr_stmt|;
block|}
DECL|method|timeoutExceptionString (SelectableChannel channel, long timeout, int ops)
specifier|private
specifier|static
name|String
name|timeoutExceptionString
parameter_list|(
name|SelectableChannel
name|channel
parameter_list|,
name|long
name|timeout
parameter_list|,
name|int
name|ops
parameter_list|)
block|{
name|String
name|waitingFor
decl_stmt|;
switch|switch
condition|(
name|ops
condition|)
block|{
case|case
name|SelectionKey
operator|.
name|OP_READ
case|:
name|waitingFor
operator|=
literal|"read"
expr_stmt|;
break|break;
case|case
name|SelectionKey
operator|.
name|OP_WRITE
case|:
name|waitingFor
operator|=
literal|"write"
expr_stmt|;
break|break;
case|case
name|SelectionKey
operator|.
name|OP_CONNECT
case|:
name|waitingFor
operator|=
literal|"connect"
expr_stmt|;
break|break;
default|default :
name|waitingFor
operator|=
literal|""
operator|+
name|ops
expr_stmt|;
block|}
return|return
name|timeout
operator|+
literal|" millis timeout while "
operator|+
literal|"waiting for channel to be ready for "
operator|+
name|waitingFor
operator|+
literal|". ch : "
operator|+
name|channel
return|;
block|}
comment|/**    * This maintains a pool of selectors. These selectors are closed    * once they are idle (unused) for a few seconds.    */
DECL|class|SelectorPool
specifier|private
specifier|static
class|class
name|SelectorPool
block|{
DECL|class|SelectorInfo
specifier|private
specifier|static
class|class
name|SelectorInfo
block|{
DECL|field|selector
name|Selector
name|selector
decl_stmt|;
DECL|field|lastActivityTime
name|long
name|lastActivityTime
decl_stmt|;
DECL|field|queue
name|LinkedList
argument_list|<
name|SelectorInfo
argument_list|>
name|queue
decl_stmt|;
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|selector
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|selector
operator|.
name|close
argument_list|()
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
name|warn
argument_list|(
literal|"Unexpected exception while closing selector : "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|ProviderInfo
specifier|private
specifier|static
class|class
name|ProviderInfo
block|{
DECL|field|provider
name|SelectorProvider
name|provider
decl_stmt|;
DECL|field|queue
name|LinkedList
argument_list|<
name|SelectorInfo
argument_list|>
name|queue
decl_stmt|;
comment|// lifo
DECL|field|next
name|ProviderInfo
name|next
decl_stmt|;
block|}
DECL|field|IDLE_TIMEOUT
specifier|private
specifier|static
specifier|final
name|long
name|IDLE_TIMEOUT
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
comment|// 10 seconds.
DECL|field|providerList
specifier|private
name|ProviderInfo
name|providerList
init|=
literal|null
decl_stmt|;
comment|/**      * Waits on the channel with the given timeout using one of the       * cached selectors. It also removes any cached selectors that are      * idle for a few seconds.      *       * @param channel      * @param ops      * @param timeout      * @return      * @throws IOException      */
DECL|method|select (SelectableChannel channel, int ops, long timeout)
name|int
name|select
parameter_list|(
name|SelectableChannel
name|channel
parameter_list|,
name|int
name|ops
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|SelectorInfo
name|info
init|=
name|get
argument_list|(
name|channel
argument_list|)
decl_stmt|;
name|SelectionKey
name|key
init|=
literal|null
decl_stmt|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|long
name|start
init|=
operator|(
name|timeout
operator|==
literal|0
operator|)
condition|?
literal|0
else|:
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|key
operator|=
name|channel
operator|.
name|register
argument_list|(
name|info
operator|.
name|selector
argument_list|,
name|ops
argument_list|)
expr_stmt|;
name|ret
operator|=
name|info
operator|.
name|selector
operator|.
name|select
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|0
condition|)
block|{
return|return
name|ret
return|;
block|}
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|(
literal|"Interrupted while waiting for "
operator|+
literal|"IO on channel "
operator|+
name|channel
operator|+
literal|". "
operator|+
name|timeout
operator|+
literal|" millis timeout left."
argument_list|)
throw|;
block|}
comment|/* Sometimes select() returns 0 much before timeout for             * unknown reasons. So select again if required.            */
if|if
condition|(
name|timeout
operator|>
literal|0
condition|)
block|{
name|timeout
operator|-=
name|Time
operator|.
name|now
argument_list|()
operator|-
name|start
expr_stmt|;
if|if
condition|(
name|timeout
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|key
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
comment|//clear the canceled key.
try|try
block|{
name|info
operator|.
name|selector
operator|.
name|selectNow
argument_list|()
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
name|info
argument_list|(
literal|"Unexpected Exception while clearing selector : "
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// don't put the selector back.
name|info
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
name|release
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Takes one selector from end of LRU list of free selectors.      * If there are no selectors awailable, it creates a new selector.      * Also invokes trimIdleSelectors().       *       * @param channel      * @return       * @throws IOException      */
DECL|method|get (SelectableChannel channel)
specifier|private
specifier|synchronized
name|SelectorInfo
name|get
parameter_list|(
name|SelectableChannel
name|channel
parameter_list|)
throws|throws
name|IOException
block|{
name|SelectorInfo
name|selInfo
init|=
literal|null
decl_stmt|;
name|SelectorProvider
name|provider
init|=
name|channel
operator|.
name|provider
argument_list|()
decl_stmt|;
comment|// pick the list : rarely there is more than one provider in use.
name|ProviderInfo
name|pList
init|=
name|providerList
decl_stmt|;
while|while
condition|(
name|pList
operator|!=
literal|null
operator|&&
name|pList
operator|.
name|provider
operator|!=
name|provider
condition|)
block|{
name|pList
operator|=
name|pList
operator|.
name|next
expr_stmt|;
block|}
if|if
condition|(
name|pList
operator|==
literal|null
condition|)
block|{
comment|//LOG.info("Creating new ProviderInfo : " + provider.toString());
name|pList
operator|=
operator|new
name|ProviderInfo
argument_list|()
expr_stmt|;
name|pList
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
name|pList
operator|.
name|queue
operator|=
operator|new
name|LinkedList
argument_list|<
name|SelectorInfo
argument_list|>
argument_list|()
expr_stmt|;
name|pList
operator|.
name|next
operator|=
name|providerList
expr_stmt|;
name|providerList
operator|=
name|pList
expr_stmt|;
block|}
name|LinkedList
argument_list|<
name|SelectorInfo
argument_list|>
name|queue
init|=
name|pList
operator|.
name|queue
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Selector
name|selector
init|=
name|provider
operator|.
name|openSelector
argument_list|()
decl_stmt|;
name|selInfo
operator|=
operator|new
name|SelectorInfo
argument_list|()
expr_stmt|;
name|selInfo
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|selInfo
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
else|else
block|{
name|selInfo
operator|=
name|queue
operator|.
name|removeLast
argument_list|()
expr_stmt|;
block|}
name|trimIdleSelectors
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|selInfo
return|;
block|}
comment|/**      * puts selector back at the end of LRU list of free selectos.      * Also invokes trimIdleSelectors().      *       * @param info      */
DECL|method|release (SelectorInfo info)
specifier|private
specifier|synchronized
name|void
name|release
parameter_list|(
name|SelectorInfo
name|info
parameter_list|)
block|{
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|trimIdleSelectors
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|info
operator|.
name|lastActivityTime
operator|=
name|now
expr_stmt|;
name|info
operator|.
name|queue
operator|.
name|addLast
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Closes selectors that are idle for IDLE_TIMEOUT (10 sec). It does not      * traverse the whole list, just over the one that have crossed       * the timeout.      */
DECL|method|trimIdleSelectors (long now)
specifier|private
name|void
name|trimIdleSelectors
parameter_list|(
name|long
name|now
parameter_list|)
block|{
name|long
name|cutoff
init|=
name|now
operator|-
name|IDLE_TIMEOUT
decl_stmt|;
for|for
control|(
name|ProviderInfo
name|pList
init|=
name|providerList
init|;
name|pList
operator|!=
literal|null
condition|;
name|pList
operator|=
name|pList
operator|.
name|next
control|)
block|{
if|if
condition|(
name|pList
operator|.
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|SelectorInfo
argument_list|>
name|it
init|=
name|pList
operator|.
name|queue
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SelectorInfo
name|info
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|lastActivityTime
operator|>
name|cutoff
condition|)
block|{
break|break;
block|}
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
name|info
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

