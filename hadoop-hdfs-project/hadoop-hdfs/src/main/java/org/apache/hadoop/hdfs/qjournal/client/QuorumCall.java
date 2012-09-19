begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
operator|.
name|client
package|;
end_package

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
name|Map
operator|.
name|Entry
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
name|TimeoutException
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
name|ipc
operator|.
name|RemoteException
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
name|collect
operator|.
name|Maps
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
name|FutureCallback
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
name|Futures
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
name|ListenableFuture
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
import|;
end_import

begin_comment
comment|/**  * Represents a set of calls for which a quorum of results is needed.  * @param<KEY> a key used to identify each of the outgoing calls  * @param<RESULT> the type of the call result  */
end_comment

begin_class
DECL|class|QuorumCall
class|class
name|QuorumCall
parameter_list|<
name|KEY
parameter_list|,
name|RESULT
parameter_list|>
block|{
DECL|field|successes
specifier|private
specifier|final
name|Map
argument_list|<
name|KEY
argument_list|,
name|RESULT
argument_list|>
name|successes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|exceptions
specifier|private
specifier|final
name|Map
argument_list|<
name|KEY
argument_list|,
name|Throwable
argument_list|>
name|exceptions
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|/**    * Interval, in milliseconds, at which a log message will be made    * while waiting for a quorum call.    */
DECL|field|WAIT_PROGRESS_INTERVAL_MILLIS
specifier|private
specifier|static
specifier|final
name|int
name|WAIT_PROGRESS_INTERVAL_MILLIS
init|=
literal|1000
decl_stmt|;
comment|/**    * Start logging messages at INFO level periodically after waiting for    * this fraction of the configured timeout for any call.    */
DECL|field|WAIT_PROGRESS_INFO_THRESHOLD
specifier|private
specifier|static
specifier|final
name|float
name|WAIT_PROGRESS_INFO_THRESHOLD
init|=
literal|0.3f
decl_stmt|;
comment|/**    * Start logging messages at WARN level after waiting for this    * fraction of the configured timeout for any call.    */
DECL|field|WAIT_PROGRESS_WARN_THRESHOLD
specifier|private
specifier|static
specifier|final
name|float
name|WAIT_PROGRESS_WARN_THRESHOLD
init|=
literal|0.7f
decl_stmt|;
DECL|method|create ( Map<KEY, ? extends ListenableFuture<RESULT>> calls)
specifier|static
parameter_list|<
name|KEY
parameter_list|,
name|RESULT
parameter_list|>
name|QuorumCall
argument_list|<
name|KEY
argument_list|,
name|RESULT
argument_list|>
name|create
parameter_list|(
name|Map
argument_list|<
name|KEY
argument_list|,
name|?
extends|extends
name|ListenableFuture
argument_list|<
name|RESULT
argument_list|>
argument_list|>
name|calls
parameter_list|)
block|{
specifier|final
name|QuorumCall
argument_list|<
name|KEY
argument_list|,
name|RESULT
argument_list|>
name|qr
init|=
operator|new
name|QuorumCall
argument_list|<
name|KEY
argument_list|,
name|RESULT
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Entry
argument_list|<
name|KEY
argument_list|,
name|?
extends|extends
name|ListenableFuture
argument_list|<
name|RESULT
argument_list|>
argument_list|>
name|e
range|:
name|calls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
argument_list|,
literal|"null future for key: "
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|Futures
operator|.
name|addCallback
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
operator|new
name|FutureCallback
argument_list|<
name|RESULT
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|qr
operator|.
name|addException
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|RESULT
name|res
parameter_list|)
block|{
name|qr
operator|.
name|addResult
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|qr
return|;
block|}
DECL|method|QuorumCall ()
specifier|private
name|QuorumCall
parameter_list|()
block|{
comment|// Only instantiated from factory method above
block|}
comment|/**    * Wait for the quorum to achieve a certain number of responses.    *     * Note that, even after this returns, more responses may arrive,    * causing the return value of other methods in this class to change.    *    * @param minResponses return as soon as this many responses have been    * received, regardless of whether they are successes or exceptions    * @param minSuccesses return as soon as this many successful (non-exception)    * responses have been received    * @param maxExceptions return as soon as this many exception responses    * have been received. Pass 0 to return immediately if any exception is    * received.    * @param millis the number of milliseconds to wait for    * @throws InterruptedException if the thread is interrupted while waiting    * @throws TimeoutException if the specified timeout elapses before    * achieving the desired conditions    */
DECL|method|waitFor ( int minResponses, int minSuccesses, int maxExceptions, int millis, String operationName)
specifier|public
specifier|synchronized
name|void
name|waitFor
parameter_list|(
name|int
name|minResponses
parameter_list|,
name|int
name|minSuccesses
parameter_list|,
name|int
name|maxExceptions
parameter_list|,
name|int
name|millis
parameter_list|,
name|String
name|operationName
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|long
name|st
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|long
name|nextLogTime
init|=
name|st
operator|+
call|(
name|long
call|)
argument_list|(
name|millis
operator|*
name|WAIT_PROGRESS_INFO_THRESHOLD
argument_list|)
decl_stmt|;
name|long
name|et
init|=
name|st
operator|+
name|millis
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|checkAssertionErrors
argument_list|()
expr_stmt|;
if|if
condition|(
name|minResponses
operator|>
literal|0
operator|&&
name|countResponses
argument_list|()
operator|>=
name|minResponses
condition|)
return|return;
if|if
condition|(
name|minSuccesses
operator|>
literal|0
operator|&&
name|countSuccesses
argument_list|()
operator|>=
name|minSuccesses
condition|)
return|return;
if|if
condition|(
name|maxExceptions
operator|>=
literal|0
operator|&&
name|countExceptions
argument_list|()
operator|>
name|maxExceptions
condition|)
return|return;
name|long
name|now
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>
name|nextLogTime
condition|)
block|{
name|long
name|waited
init|=
name|now
operator|-
name|st
decl_stmt|;
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Waited %s ms (timeout=%s ms) for a response for %s"
argument_list|,
name|waited
argument_list|,
name|millis
argument_list|,
name|operationName
argument_list|)
decl_stmt|;
if|if
condition|(
name|waited
operator|>
name|millis
operator|*
name|WAIT_PROGRESS_WARN_THRESHOLD
condition|)
block|{
name|QuorumJournalManager
operator|.
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|QuorumJournalManager
operator|.
name|LOG
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|nextLogTime
operator|=
name|now
operator|+
name|WAIT_PROGRESS_INTERVAL_MILLIS
expr_stmt|;
block|}
name|long
name|rem
init|=
name|et
operator|-
name|now
decl_stmt|;
if|if
condition|(
name|rem
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|()
throw|;
block|}
name|rem
operator|=
name|Math
operator|.
name|min
argument_list|(
name|rem
argument_list|,
name|nextLogTime
operator|-
name|now
argument_list|)
expr_stmt|;
name|rem
operator|=
name|Math
operator|.
name|max
argument_list|(
name|rem
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|wait
argument_list|(
name|rem
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Check if any of the responses came back with an AssertionError.    * If so, it re-throws it, even if there was a quorum of responses.    * This code only runs if assertions are enabled for this class,    * otherwise it should JIT itself away.    *     * This is done since AssertionError indicates programmer confusion    * rather than some kind of expected issue, and thus in the context    * of test cases we'd like to actually fail the test case instead of    * continuing through.    */
DECL|method|checkAssertionErrors ()
specifier|private
specifier|synchronized
name|void
name|checkAssertionErrors
parameter_list|()
block|{
name|boolean
name|assertsEnabled
init|=
literal|false
decl_stmt|;
assert|assert
name|assertsEnabled
operator|=
literal|true
assert|;
comment|// sets to true if enabled
if|if
condition|(
name|assertsEnabled
condition|)
block|{
for|for
control|(
name|Throwable
name|t
range|:
name|exceptions
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|AssertionError
condition|)
block|{
throw|throw
operator|(
name|AssertionError
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|RemoteException
operator|&&
operator|(
operator|(
name|RemoteException
operator|)
name|t
operator|)
operator|.
name|getClassName
argument_list|()
operator|.
name|equals
argument_list|(
name|AssertionError
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|addResult (KEY k, RESULT res)
specifier|private
specifier|synchronized
name|void
name|addResult
parameter_list|(
name|KEY
name|k
parameter_list|,
name|RESULT
name|res
parameter_list|)
block|{
name|successes
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
DECL|method|addException (KEY k, Throwable t)
specifier|private
specifier|synchronized
name|void
name|addException
parameter_list|(
name|KEY
name|k
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|exceptions
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return the total number of calls for which a response has been received,    * regardless of whether it threw an exception or returned a successful    * result.    */
DECL|method|countResponses ()
specifier|public
specifier|synchronized
name|int
name|countResponses
parameter_list|()
block|{
return|return
name|successes
operator|.
name|size
argument_list|()
operator|+
name|exceptions
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * @return the number of calls for which a non-exception response has been    * received.    */
DECL|method|countSuccesses ()
specifier|public
specifier|synchronized
name|int
name|countSuccesses
parameter_list|()
block|{
return|return
name|successes
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * @return the number of calls for which an exception response has been    * received.    */
DECL|method|countExceptions ()
specifier|public
specifier|synchronized
name|int
name|countExceptions
parameter_list|()
block|{
return|return
name|exceptions
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * @return the map of successful responses. A copy is made such that this    * map will not be further mutated, even if further results arrive for the    * quorum.    */
DECL|method|getResults ()
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|KEY
argument_list|,
name|RESULT
argument_list|>
name|getResults
parameter_list|()
block|{
return|return
name|Maps
operator|.
name|newHashMap
argument_list|(
name|successes
argument_list|)
return|;
block|}
DECL|method|rethrowException (String msg)
specifier|public
specifier|synchronized
name|void
name|rethrowException
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|QuorumException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|QuorumException
operator|.
name|create
argument_list|(
name|msg
argument_list|,
name|successes
argument_list|,
name|exceptions
argument_list|)
throw|;
block|}
DECL|method|mapToString ( Map<K, ? extends Message> map)
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|>
name|String
name|mapToString
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|?
extends|extends
name|Message
argument_list|>
name|map
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|?
extends|extends
name|Message
argument_list|>
name|e
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

