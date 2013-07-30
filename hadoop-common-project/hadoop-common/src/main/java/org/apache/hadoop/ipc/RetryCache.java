begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|util
operator|.
name|LightWeightCache
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
name|LightWeightGSet
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
name|LightWeightGSet
operator|.
name|LinkedElement
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

begin_comment
comment|/**  * Maintains a cache of non-idempotent requests that have been successfully  * processed by the RPC server implementation, to handle the retries. A request  * is uniquely identified by the unique client ID + call ID of the RPC request.  * On receiving retried request, an entry will be found in the  * {@link RetryCache} and the previous response is sent back to the request.  *<p>  * To look an implementation using this cache, see HDFS FSNamesystem class.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RetryCache
specifier|public
class|class
name|RetryCache
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RetryCache
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * CacheEntry is tracked using unique client ID and callId of the RPC request    */
DECL|class|CacheEntry
specifier|public
specifier|static
class|class
name|CacheEntry
implements|implements
name|LightWeightCache
operator|.
name|Entry
block|{
comment|/**      * Processing state of the requests      */
DECL|field|INPROGRESS
specifier|private
specifier|static
name|byte
name|INPROGRESS
init|=
literal|0
decl_stmt|;
DECL|field|SUCCESS
specifier|private
specifier|static
name|byte
name|SUCCESS
init|=
literal|1
decl_stmt|;
DECL|field|FAILED
specifier|private
specifier|static
name|byte
name|FAILED
init|=
literal|2
decl_stmt|;
DECL|field|state
specifier|private
name|byte
name|state
init|=
name|INPROGRESS
decl_stmt|;
comment|// Store uuid as two long for better memory utilization
DECL|field|clientIdMsb
specifier|private
specifier|final
name|long
name|clientIdMsb
decl_stmt|;
comment|// Most signficant bytes
DECL|field|clientIdLsb
specifier|private
specifier|final
name|long
name|clientIdLsb
decl_stmt|;
comment|// Least significant bytes
DECL|field|callId
specifier|private
specifier|final
name|int
name|callId
decl_stmt|;
DECL|field|expirationTime
specifier|private
specifier|final
name|long
name|expirationTime
decl_stmt|;
DECL|field|next
specifier|private
name|LightWeightGSet
operator|.
name|LinkedElement
name|next
decl_stmt|;
DECL|method|CacheEntry (byte[] clientId, int callId, long expirationTime)
name|CacheEntry
parameter_list|(
name|byte
index|[]
name|clientId
parameter_list|,
name|int
name|callId
parameter_list|,
name|long
name|expirationTime
parameter_list|)
block|{
comment|// ClientId must be a UUID - that is 16 octets.
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|clientId
operator|.
name|length
operator|==
name|ClientId
operator|.
name|BYTE_LENGTH
argument_list|,
literal|"Invalid clientId - length is "
operator|+
name|clientId
operator|.
name|length
operator|+
literal|" expected length "
operator|+
name|ClientId
operator|.
name|BYTE_LENGTH
argument_list|)
expr_stmt|;
comment|// Convert UUID bytes to two longs
name|long
name|tmp
init|=
literal|0
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
literal|8
condition|;
name|i
operator|++
control|)
block|{
name|tmp
operator|=
operator|(
name|tmp
operator|<<
literal|8
operator|)
operator||
operator|(
name|clientId
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
block|}
name|clientIdMsb
operator|=
name|tmp
expr_stmt|;
name|tmp
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|8
init|;
name|i
operator|<
literal|16
condition|;
name|i
operator|++
control|)
block|{
name|tmp
operator|=
operator|(
name|tmp
operator|<<
literal|8
operator|)
operator||
operator|(
name|clientId
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
block|}
name|clientIdLsb
operator|=
name|tmp
expr_stmt|;
name|this
operator|.
name|callId
operator|=
name|callId
expr_stmt|;
name|this
operator|.
name|expirationTime
operator|=
name|expirationTime
expr_stmt|;
block|}
DECL|method|hashCode (long value)
specifier|private
specifier|static
name|int
name|hashCode
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|value
operator|^
operator|(
name|value
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|hashCode
argument_list|(
name|clientIdMsb
argument_list|)
operator|*
literal|31
operator|+
name|hashCode
argument_list|(
name|clientIdLsb
argument_list|)
operator|)
operator|*
literal|31
operator|+
name|callId
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|CacheEntry
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CacheEntry
name|other
init|=
operator|(
name|CacheEntry
operator|)
name|obj
decl_stmt|;
return|return
name|callId
operator|==
name|other
operator|.
name|callId
operator|&&
name|clientIdMsb
operator|==
name|other
operator|.
name|clientIdMsb
operator|&&
name|clientIdLsb
operator|==
name|other
operator|.
name|clientIdLsb
return|;
block|}
annotation|@
name|Override
DECL|method|setNext (LinkedElement next)
specifier|public
name|void
name|setNext
parameter_list|(
name|LinkedElement
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNext ()
specifier|public
name|LinkedElement
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
DECL|method|completed (boolean success)
specifier|synchronized
name|void
name|completed
parameter_list|(
name|boolean
name|success
parameter_list|)
block|{
name|state
operator|=
name|success
condition|?
name|SUCCESS
else|:
name|FAILED
expr_stmt|;
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
DECL|method|isSuccess ()
specifier|public
specifier|synchronized
name|boolean
name|isSuccess
parameter_list|()
block|{
return|return
name|state
operator|==
name|SUCCESS
return|;
block|}
annotation|@
name|Override
DECL|method|setExpirationTime (long timeNano)
specifier|public
name|void
name|setExpirationTime
parameter_list|(
name|long
name|timeNano
parameter_list|)
block|{
comment|// expiration time does not change
block|}
annotation|@
name|Override
DECL|method|getExpirationTime ()
specifier|public
name|long
name|getExpirationTime
parameter_list|()
block|{
return|return
name|expirationTime
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
operator|(
operator|new
name|UUID
argument_list|(
name|this
operator|.
name|clientIdMsb
argument_list|,
name|this
operator|.
name|clientIdLsb
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|this
operator|.
name|callId
operator|+
literal|":"
operator|+
name|this
operator|.
name|state
return|;
block|}
block|}
comment|/**    * CacheEntry with payload that tracks the previous response or parts of    * previous response to be used for generating response for retried requests.    */
DECL|class|CacheEntryWithPayload
specifier|public
specifier|static
class|class
name|CacheEntryWithPayload
extends|extends
name|CacheEntry
block|{
DECL|field|payload
specifier|private
name|Object
name|payload
decl_stmt|;
DECL|method|CacheEntryWithPayload (byte[] clientId, int callId, Object payload, long expirationTime)
name|CacheEntryWithPayload
parameter_list|(
name|byte
index|[]
name|clientId
parameter_list|,
name|int
name|callId
parameter_list|,
name|Object
name|payload
parameter_list|,
name|long
name|expirationTime
parameter_list|)
block|{
name|super
argument_list|(
name|clientId
argument_list|,
name|callId
argument_list|,
name|expirationTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
comment|/** Override equals to avoid findbugs warnings */
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
comment|/** Override hashcode to avoid findbugs warnings */
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|getPayload ()
specifier|public
name|Object
name|getPayload
parameter_list|()
block|{
return|return
name|payload
return|;
block|}
block|}
DECL|field|set
specifier|private
specifier|final
name|LightWeightGSet
argument_list|<
name|CacheEntry
argument_list|,
name|CacheEntry
argument_list|>
name|set
decl_stmt|;
DECL|field|expirationTime
specifier|private
specifier|final
name|long
name|expirationTime
decl_stmt|;
comment|/**    * Constructor    * @param cacheName name to identify the cache by    * @param percentage percentage of total java heap space used by this cache    * @param expirationTime time for an entry to expire in nanoseconds    */
DECL|method|RetryCache (String cacheName, double percentage, long expirationTime)
specifier|public
name|RetryCache
parameter_list|(
name|String
name|cacheName
parameter_list|,
name|double
name|percentage
parameter_list|,
name|long
name|expirationTime
parameter_list|)
block|{
name|int
name|capacity
init|=
name|LightWeightGSet
operator|.
name|computeCapacity
argument_list|(
name|percentage
argument_list|,
name|cacheName
argument_list|)
decl_stmt|;
name|capacity
operator|=
name|capacity
operator|>
literal|16
condition|?
name|capacity
else|:
literal|16
expr_stmt|;
name|this
operator|.
name|set
operator|=
operator|new
name|LightWeightCache
argument_list|<
name|CacheEntry
argument_list|,
name|CacheEntry
argument_list|>
argument_list|(
name|capacity
argument_list|,
name|capacity
argument_list|,
name|expirationTime
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|expirationTime
operator|=
name|expirationTime
expr_stmt|;
block|}
DECL|method|skipRetryCache ()
specifier|private
specifier|static
name|boolean
name|skipRetryCache
parameter_list|()
block|{
comment|// Do not track non RPC invocation or RPC requests with
comment|// invalid callId or clientId in retry cache
return|return
operator|!
name|Server
operator|.
name|isRpcInvocation
argument_list|()
operator|||
name|Server
operator|.
name|getCallId
argument_list|()
operator|<
literal|0
operator|||
name|Arrays
operator|.
name|equals
argument_list|(
name|Server
operator|.
name|getClientId
argument_list|()
argument_list|,
name|RpcConstants
operator|.
name|DUMMY_CLIENT_ID
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCacheSet ()
specifier|public
name|LightWeightGSet
argument_list|<
name|CacheEntry
argument_list|,
name|CacheEntry
argument_list|>
name|getCacheSet
parameter_list|()
block|{
return|return
name|set
return|;
block|}
comment|/**    * This method handles the following conditions:    *<ul>    *<li>If retry is not to be processed, return null</li>    *<li>If there is no cache entry, add a new entry {@code newEntry} and return    * it.</li>    *<li>If there is an existing entry, wait for its completion. If the    * completion state is {@link CacheEntry#FAILED}, the expectation is that the    * thread that waited for completion, retries the request. the    * {@link CacheEntry} state is set to {@link CacheEntry#INPROGRESS} again.    *<li>If the completion state is {@link CacheEntry#SUCCESS}, the entry is    * returned so that the thread that waits for it can can return previous    * response.</li>    *<ul>    *     * @return {@link CacheEntry}.    */
DECL|method|waitForCompletion (CacheEntry newEntry)
specifier|private
name|CacheEntry
name|waitForCompletion
parameter_list|(
name|CacheEntry
name|newEntry
parameter_list|)
block|{
name|CacheEntry
name|mapEntry
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|mapEntry
operator|=
name|set
operator|.
name|get
argument_list|(
name|newEntry
argument_list|)
expr_stmt|;
comment|// If an entry in the cache does not exist, add a new one
if|if
condition|(
name|mapEntry
operator|==
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
literal|"Adding Rpc request clientId "
operator|+
name|newEntry
operator|.
name|clientIdMsb
operator|+
name|newEntry
operator|.
name|clientIdLsb
operator|+
literal|" callId "
operator|+
name|newEntry
operator|.
name|callId
operator|+
literal|" to retryCache"
argument_list|)
expr_stmt|;
block|}
name|set
operator|.
name|put
argument_list|(
name|newEntry
argument_list|)
expr_stmt|;
return|return
name|newEntry
return|;
block|}
block|}
comment|// Entry already exists in cache. Wait for completion and return its state
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|mapEntry
argument_list|,
literal|"Entry from the cache should not be null"
argument_list|)
expr_stmt|;
comment|// Wait for in progress request to complete
synchronized|synchronized
init|(
name|mapEntry
init|)
block|{
while|while
condition|(
name|mapEntry
operator|.
name|state
operator|==
name|CacheEntry
operator|.
name|INPROGRESS
condition|)
block|{
try|try
block|{
name|mapEntry
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// Restore the interrupted status
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
comment|// Previous request has failed, the expectation is is that it will be
comment|// retried again.
if|if
condition|(
name|mapEntry
operator|.
name|state
operator|!=
name|CacheEntry
operator|.
name|SUCCESS
condition|)
block|{
name|mapEntry
operator|.
name|state
operator|=
name|CacheEntry
operator|.
name|INPROGRESS
expr_stmt|;
block|}
block|}
return|return
name|mapEntry
return|;
block|}
comment|/**     * Add a new cache entry into the retry cache. The cache entry consists of     * clientId and callId extracted from editlog.    */
DECL|method|addCacheEntry (byte[] clientId, int callId)
specifier|public
name|void
name|addCacheEntry
parameter_list|(
name|byte
index|[]
name|clientId
parameter_list|,
name|int
name|callId
parameter_list|)
block|{
name|CacheEntry
name|newEntry
init|=
operator|new
name|CacheEntry
argument_list|(
name|clientId
argument_list|,
name|callId
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|expirationTime
argument_list|)
decl_stmt|;
name|newEntry
operator|.
name|completed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|set
operator|.
name|put
argument_list|(
name|newEntry
argument_list|)
expr_stmt|;
block|}
DECL|method|addCacheEntryWithPayload (byte[] clientId, int callId, Object payload)
specifier|public
name|void
name|addCacheEntryWithPayload
parameter_list|(
name|byte
index|[]
name|clientId
parameter_list|,
name|int
name|callId
parameter_list|,
name|Object
name|payload
parameter_list|)
block|{
name|CacheEntry
name|newEntry
init|=
operator|new
name|CacheEntryWithPayload
argument_list|(
name|clientId
argument_list|,
name|callId
argument_list|,
name|payload
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|expirationTime
argument_list|)
decl_stmt|;
comment|// since the entry is loaded from editlog, we can assume it succeeded.
name|newEntry
operator|.
name|completed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|set
operator|.
name|put
argument_list|(
name|newEntry
argument_list|)
expr_stmt|;
block|}
DECL|method|newEntry (long expirationTime)
specifier|private
specifier|static
name|CacheEntry
name|newEntry
parameter_list|(
name|long
name|expirationTime
parameter_list|)
block|{
return|return
operator|new
name|CacheEntry
argument_list|(
name|Server
operator|.
name|getClientId
argument_list|()
argument_list|,
name|Server
operator|.
name|getCallId
argument_list|()
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|expirationTime
argument_list|)
return|;
block|}
DECL|method|newEntry (Object payload, long expirationTime)
specifier|private
specifier|static
name|CacheEntryWithPayload
name|newEntry
parameter_list|(
name|Object
name|payload
parameter_list|,
name|long
name|expirationTime
parameter_list|)
block|{
return|return
operator|new
name|CacheEntryWithPayload
argument_list|(
name|Server
operator|.
name|getClientId
argument_list|()
argument_list|,
name|Server
operator|.
name|getCallId
argument_list|()
argument_list|,
name|payload
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|expirationTime
argument_list|)
return|;
block|}
comment|/** Static method that provides null check for retryCache */
DECL|method|waitForCompletion (RetryCache cache)
specifier|public
specifier|static
name|CacheEntry
name|waitForCompletion
parameter_list|(
name|RetryCache
name|cache
parameter_list|)
block|{
if|if
condition|(
name|skipRetryCache
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|cache
operator|!=
literal|null
condition|?
name|cache
operator|.
name|waitForCompletion
argument_list|(
name|newEntry
argument_list|(
name|cache
operator|.
name|expirationTime
argument_list|)
argument_list|)
else|:
literal|null
return|;
block|}
comment|/** Static method that provides null check for retryCache */
DECL|method|waitForCompletion (RetryCache cache, Object payload)
specifier|public
specifier|static
name|CacheEntryWithPayload
name|waitForCompletion
parameter_list|(
name|RetryCache
name|cache
parameter_list|,
name|Object
name|payload
parameter_list|)
block|{
if|if
condition|(
name|skipRetryCache
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
call|(
name|CacheEntryWithPayload
call|)
argument_list|(
name|cache
operator|!=
literal|null
condition|?
name|cache
operator|.
name|waitForCompletion
argument_list|(
name|newEntry
argument_list|(
name|payload
argument_list|,
name|cache
operator|.
name|expirationTime
argument_list|)
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
DECL|method|setState (CacheEntry e, boolean success)
specifier|public
specifier|static
name|void
name|setState
parameter_list|(
name|CacheEntry
name|e
parameter_list|,
name|boolean
name|success
parameter_list|)
block|{
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|e
operator|.
name|completed
argument_list|(
name|success
argument_list|)
expr_stmt|;
block|}
DECL|method|setState (CacheEntryWithPayload e, boolean success, Object payload)
specifier|public
specifier|static
name|void
name|setState
parameter_list|(
name|CacheEntryWithPayload
name|e
parameter_list|,
name|boolean
name|success
parameter_list|,
name|Object
name|payload
parameter_list|)
block|{
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|e
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
name|e
operator|.
name|completed
argument_list|(
name|success
argument_list|)
expr_stmt|;
block|}
DECL|method|clear (RetryCache cache)
specifier|public
specifier|static
name|void
name|clear
parameter_list|(
name|RetryCache
name|cache
parameter_list|)
block|{
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

