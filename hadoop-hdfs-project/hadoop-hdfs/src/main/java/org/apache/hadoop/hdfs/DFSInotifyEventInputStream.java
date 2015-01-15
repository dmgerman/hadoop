begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|Iterators
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
name|hdfs
operator|.
name|inotify
operator|.
name|EventBatch
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
name|hdfs
operator|.
name|inotify
operator|.
name|EventBatchList
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
name|hdfs
operator|.
name|inotify
operator|.
name|MissingEventsException
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
name|hdfs
operator|.
name|protocol
operator|.
name|ClientProtocol
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
name|org
operator|.
name|htrace
operator|.
name|Sampler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|htrace
operator|.
name|Trace
import|;
end_import

begin_import
import|import
name|org
operator|.
name|htrace
operator|.
name|TraceScope
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
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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

begin_comment
comment|/**  * Stream for reading inotify events. DFSInotifyEventInputStreams should not  * be shared among multiple threads.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DFSInotifyEventInputStream
specifier|public
class|class
name|DFSInotifyEventInputStream
block|{
DECL|field|LOG
specifier|public
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DFSInotifyEventInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The trace sampler to use when making RPCs to the NameNode.    */
DECL|field|traceSampler
specifier|private
specifier|final
name|Sampler
argument_list|<
name|?
argument_list|>
name|traceSampler
decl_stmt|;
DECL|field|namenode
specifier|private
specifier|final
name|ClientProtocol
name|namenode
decl_stmt|;
DECL|field|it
specifier|private
name|Iterator
argument_list|<
name|EventBatch
argument_list|>
name|it
decl_stmt|;
DECL|field|lastReadTxid
specifier|private
name|long
name|lastReadTxid
decl_stmt|;
comment|/**    * The most recent txid the NameNode told us it has sync'ed -- helps us    * determine how far behind we are in the edit stream.    */
DECL|field|syncTxid
specifier|private
name|long
name|syncTxid
decl_stmt|;
comment|/**    * Used to generate wait times in {@link DFSInotifyEventInputStream#take()}.    */
DECL|field|rng
specifier|private
name|Random
name|rng
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|INITIAL_WAIT_MS
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_WAIT_MS
init|=
literal|10
decl_stmt|;
DECL|method|DFSInotifyEventInputStream (Sampler<?> traceSampler, ClientProtocol namenode)
name|DFSInotifyEventInputStream
parameter_list|(
name|Sampler
argument_list|<
name|?
argument_list|>
name|traceSampler
parameter_list|,
name|ClientProtocol
name|namenode
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Only consider new transaction IDs.
name|this
argument_list|(
name|traceSampler
argument_list|,
name|namenode
argument_list|,
name|namenode
operator|.
name|getCurrentEditLogTxid
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DFSInotifyEventInputStream (Sampler traceSampler, ClientProtocol namenode, long lastReadTxid)
name|DFSInotifyEventInputStream
parameter_list|(
name|Sampler
name|traceSampler
parameter_list|,
name|ClientProtocol
name|namenode
parameter_list|,
name|long
name|lastReadTxid
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|traceSampler
operator|=
name|traceSampler
expr_stmt|;
name|this
operator|.
name|namenode
operator|=
name|namenode
expr_stmt|;
name|this
operator|.
name|it
operator|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastReadTxid
operator|=
name|lastReadTxid
expr_stmt|;
block|}
comment|/**    * Returns the next batch of events in the stream or null if no new    * batches are currently available.    *    * @throws IOException because of network error or edit log    * corruption. Also possible if JournalNodes are unresponsive in the    * QJM setting (even one unresponsive JournalNode is enough in rare cases),    * so catching this exception and retrying at least a few times is    * recommended.    * @throws MissingEventsException if we cannot return the next batch in the    * stream because the data for the events (and possibly some subsequent    * events) has been deleted (generally because this stream is a very large    * number of transactions behind the current state of the NameNode). It is    * safe to continue reading from the stream after this exception is thrown    * The next available batch of events will be returned.    */
DECL|method|poll ()
specifier|public
name|EventBatch
name|poll
parameter_list|()
throws|throws
name|IOException
throws|,
name|MissingEventsException
block|{
name|TraceScope
name|scope
init|=
name|Trace
operator|.
name|startSpan
argument_list|(
literal|"inotifyPoll"
argument_list|,
name|traceSampler
argument_list|)
decl_stmt|;
try|try
block|{
comment|// need to keep retrying until the NN sends us the latest committed txid
if|if
condition|(
name|lastReadTxid
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"poll(): lastReadTxid is -1, reading current txid from NN"
argument_list|)
expr_stmt|;
name|lastReadTxid
operator|=
name|namenode
operator|.
name|getCurrentEditLogTxid
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|EventBatchList
name|el
init|=
name|namenode
operator|.
name|getEditsFromTxid
argument_list|(
name|lastReadTxid
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|el
operator|.
name|getLastTxid
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// we only want to set syncTxid when we were actually able to read some
comment|// edits on the NN -- otherwise it will seem like edits are being
comment|// generated faster than we can read them when the problem is really
comment|// that we are temporarily unable to read edits
name|syncTxid
operator|=
name|el
operator|.
name|getSyncTxid
argument_list|()
expr_stmt|;
name|it
operator|=
name|el
operator|.
name|getBatches
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|long
name|formerLastReadTxid
init|=
name|lastReadTxid
decl_stmt|;
name|lastReadTxid
operator|=
name|el
operator|.
name|getLastTxid
argument_list|()
expr_stmt|;
if|if
condition|(
name|el
operator|.
name|getFirstTxid
argument_list|()
operator|!=
name|formerLastReadTxid
operator|+
literal|1
condition|)
block|{
throw|throw
operator|new
name|MissingEventsException
argument_list|(
name|formerLastReadTxid
operator|+
literal|1
argument_list|,
name|el
operator|.
name|getFirstTxid
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"poll(): read no edits from the NN when requesting edits "
operator|+
literal|"after txid {}"
argument_list|,
name|lastReadTxid
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// can be empty if el.getLastTxid != -1 but none of the
comment|// newly seen edit log ops actually got converted to events
return|return
name|it
operator|.
name|next
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
finally|finally
block|{
name|scope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Return a estimate of how many transaction IDs behind the NameNode's    * current state this stream is. Clients should periodically call this method    * and check if its result is steadily increasing, which indicates that they    * are falling behind (i.e. transaction are being generated faster than the    * client is reading them). If a client falls too far behind events may be    * deleted before the client can read them.    *<p/>    * A return value of -1 indicates that an estimate could not be produced, and    * should be ignored. The value returned by this method is really only useful    * when compared to previous or subsequent returned values.    */
DECL|method|getTxidsBehindEstimate ()
specifier|public
name|long
name|getTxidsBehindEstimate
parameter_list|()
block|{
if|if
condition|(
name|syncTxid
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
assert|assert
name|syncTxid
operator|>=
name|lastReadTxid
assert|;
comment|// this gives the difference between the last txid we have fetched to the
comment|// client and syncTxid at the time we last fetched events from the
comment|// NameNode
return|return
name|syncTxid
operator|-
name|lastReadTxid
return|;
block|}
block|}
comment|/**    * Returns the next event batch in the stream, waiting up to the specified    * amount of time for a new batch. Returns null if one is not available at the    * end of the specified amount of time. The time before the method returns may    * exceed the specified amount of time by up to the time required for an RPC    * to the NameNode.    *    * @param time number of units of the given TimeUnit to wait    * @param tu the desired TimeUnit    * @throws IOException see {@link DFSInotifyEventInputStream#poll()}    * @throws MissingEventsException    * see {@link DFSInotifyEventInputStream#poll()}    * @throws InterruptedException if the calling thread is interrupted    */
DECL|method|poll (long time, TimeUnit tu)
specifier|public
name|EventBatch
name|poll
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|tu
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|MissingEventsException
block|{
name|TraceScope
name|scope
init|=
name|Trace
operator|.
name|startSpan
argument_list|(
literal|"inotifyPollWithTimeout"
argument_list|,
name|traceSampler
argument_list|)
decl_stmt|;
name|EventBatch
name|next
init|=
literal|null
decl_stmt|;
try|try
block|{
name|long
name|initialTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|long
name|totalWait
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|time
argument_list|,
name|tu
argument_list|)
decl_stmt|;
name|long
name|nextWait
init|=
name|INITIAL_WAIT_MS
decl_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|poll
argument_list|()
operator|)
operator|==
literal|null
condition|)
block|{
name|long
name|timeLeft
init|=
name|totalWait
operator|-
operator|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|initialTime
operator|)
decl_stmt|;
if|if
condition|(
name|timeLeft
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"timed poll(): timed out"
argument_list|)
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|timeLeft
operator|<
name|nextWait
operator|*
literal|2
condition|)
block|{
name|nextWait
operator|=
name|timeLeft
expr_stmt|;
block|}
else|else
block|{
name|nextWait
operator|*=
literal|2
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"timed poll(): poll() returned null, sleeping for {} ms"
argument_list|,
name|nextWait
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|nextWait
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|scope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|next
return|;
block|}
comment|/**    * Returns the next batch of events in the stream, waiting indefinitely if    * a new batch  is not immediately available.    *    * @throws IOException see {@link DFSInotifyEventInputStream#poll()}    * @throws MissingEventsException see    * {@link DFSInotifyEventInputStream#poll()}    * @throws InterruptedException if the calling thread is interrupted    */
DECL|method|take ()
specifier|public
name|EventBatch
name|take
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|MissingEventsException
block|{
name|TraceScope
name|scope
init|=
name|Trace
operator|.
name|startSpan
argument_list|(
literal|"inotifyTake"
argument_list|,
name|traceSampler
argument_list|)
decl_stmt|;
name|EventBatch
name|next
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|nextWaitMin
init|=
name|INITIAL_WAIT_MS
decl_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|poll
argument_list|()
operator|)
operator|==
literal|null
condition|)
block|{
comment|// sleep for a random period between nextWaitMin and nextWaitMin * 2
comment|// to avoid stampedes at the NN if there are multiple clients
name|int
name|sleepTime
init|=
name|nextWaitMin
operator|+
name|rng
operator|.
name|nextInt
argument_list|(
name|nextWaitMin
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"take(): poll() returned null, sleeping for {} ms"
argument_list|,
name|sleepTime
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
comment|// the maximum sleep is 2 minutes
name|nextWaitMin
operator|=
name|Math
operator|.
name|min
argument_list|(
literal|60000
argument_list|,
name|nextWaitMin
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|scope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|next
return|;
block|}
block|}
end_class

end_unit

