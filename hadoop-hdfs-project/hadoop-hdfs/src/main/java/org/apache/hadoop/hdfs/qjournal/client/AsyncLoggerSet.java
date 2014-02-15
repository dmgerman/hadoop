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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|TimeoutException
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
name|hdfs
operator|.
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocolProtos
operator|.
name|GetJournalStateResponseProto
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
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocolProtos
operator|.
name|NewEpochResponseProto
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
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocolProtos
operator|.
name|PrepareRecoveryResponseProto
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
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocolProtos
operator|.
name|SegmentStateProto
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
name|server
operator|.
name|common
operator|.
name|StorageInfo
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
name|server
operator|.
name|protocol
operator|.
name|NamespaceInfo
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
name|server
operator|.
name|protocol
operator|.
name|RemoteEditLogManifest
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
name|Joiner
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
name|ImmutableList
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
name|ListenableFuture
import|;
end_import

begin_comment
comment|/**  * Wrapper around a set of Loggers, taking care of fanning out  * calls to the underlying loggers and constructing corresponding  * {@link QuorumCall} instances.  */
end_comment

begin_class
DECL|class|AsyncLoggerSet
class|class
name|AsyncLoggerSet
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
name|AsyncLoggerSet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|loggers
specifier|private
specifier|final
name|List
argument_list|<
name|AsyncLogger
argument_list|>
name|loggers
decl_stmt|;
DECL|field|INVALID_EPOCH
specifier|private
specifier|static
specifier|final
name|long
name|INVALID_EPOCH
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|myEpoch
specifier|private
name|long
name|myEpoch
init|=
name|INVALID_EPOCH
decl_stmt|;
DECL|method|AsyncLoggerSet (List<AsyncLogger> loggers)
specifier|public
name|AsyncLoggerSet
parameter_list|(
name|List
argument_list|<
name|AsyncLogger
argument_list|>
name|loggers
parameter_list|)
block|{
name|this
operator|.
name|loggers
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|loggers
argument_list|)
expr_stmt|;
block|}
DECL|method|setEpoch (long e)
name|void
name|setEpoch
parameter_list|(
name|long
name|e
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|isEpochEstablished
argument_list|()
argument_list|,
literal|"Epoch already established: epoch=%s"
argument_list|,
name|myEpoch
argument_list|)
expr_stmt|;
name|myEpoch
operator|=
name|e
expr_stmt|;
for|for
control|(
name|AsyncLogger
name|l
range|:
name|loggers
control|)
block|{
name|l
operator|.
name|setEpoch
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the highest successfully committed txid seen by the writer.    * This should be called after a successful write to a quorum, and is used    * for extra sanity checks against the protocol. See HDFS-3863.    */
DECL|method|setCommittedTxId (long txid)
specifier|public
name|void
name|setCommittedTxId
parameter_list|(
name|long
name|txid
parameter_list|)
block|{
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|logger
operator|.
name|setCommittedTxId
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return true if an epoch has been established.    */
DECL|method|isEpochEstablished ()
name|boolean
name|isEpochEstablished
parameter_list|()
block|{
return|return
name|myEpoch
operator|!=
name|INVALID_EPOCH
return|;
block|}
comment|/**    * @return the epoch number for this writer. This may only be called after    * a successful call to {@link #createNewUniqueEpoch(NamespaceInfo)}.    */
DECL|method|getEpoch ()
name|long
name|getEpoch
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|myEpoch
operator|!=
name|INVALID_EPOCH
argument_list|,
literal|"No epoch created yet"
argument_list|)
expr_stmt|;
return|return
name|myEpoch
return|;
block|}
comment|/**    * Close all of the underlying loggers.    */
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|logger
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|purgeLogsOlderThan (long minTxIdToKeep)
name|void
name|purgeLogsOlderThan
parameter_list|(
name|long
name|minTxIdToKeep
parameter_list|)
block|{
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|logger
operator|.
name|purgeLogsOlderThan
argument_list|(
name|minTxIdToKeep
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Wait for a quorum of loggers to respond to the given call. If a quorum    * can't be achieved, throws a QuorumException.    * @param q the quorum call    * @param timeoutMs the number of millis to wait    * @param operationName textual description of the operation, for logging    * @return a map of successful results    * @throws QuorumException if a quorum doesn't respond with success    * @throws IOException if the thread is interrupted or times out    */
DECL|method|waitForWriteQuorum (QuorumCall<AsyncLogger, V> q, int timeoutMs, String operationName)
parameter_list|<
name|V
parameter_list|>
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|V
argument_list|>
name|waitForWriteQuorum
parameter_list|(
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|V
argument_list|>
name|q
parameter_list|,
name|int
name|timeoutMs
parameter_list|,
name|String
name|operationName
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|majority
init|=
name|getMajoritySize
argument_list|()
decl_stmt|;
try|try
block|{
name|q
operator|.
name|waitFor
argument_list|(
name|loggers
operator|.
name|size
argument_list|()
argument_list|,
comment|// either all respond
name|majority
argument_list|,
comment|// or we get a majority successes
name|majority
argument_list|,
comment|// or we get a majority failures,
name|timeoutMs
argument_list|,
name|operationName
argument_list|)
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Interrupted waiting "
operator|+
name|timeoutMs
operator|+
literal|"ms for a "
operator|+
literal|"quorum of nodes to respond."
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Timed out waiting "
operator|+
name|timeoutMs
operator|+
literal|"ms for a "
operator|+
literal|"quorum of nodes to respond."
argument_list|)
throw|;
block|}
if|if
condition|(
name|q
operator|.
name|countSuccesses
argument_list|()
operator|<
name|majority
condition|)
block|{
name|q
operator|.
name|rethrowException
argument_list|(
literal|"Got too many exceptions to achieve quorum size "
operator|+
name|getMajorityString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|q
operator|.
name|getResults
argument_list|()
return|;
block|}
comment|/**    * @return the number of nodes which are required to obtain a quorum.    */
DECL|method|getMajoritySize ()
name|int
name|getMajoritySize
parameter_list|()
block|{
return|return
name|loggers
operator|.
name|size
argument_list|()
operator|/
literal|2
operator|+
literal|1
return|;
block|}
comment|/**    * @return a textual description of the majority size (eg "2/3" or "3/5")    */
DECL|method|getMajorityString ()
name|String
name|getMajorityString
parameter_list|()
block|{
return|return
name|getMajoritySize
argument_list|()
operator|+
literal|"/"
operator|+
name|loggers
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * @return the number of loggers behind this set    */
DECL|method|size ()
name|int
name|size
parameter_list|()
block|{
return|return
name|loggers
operator|.
name|size
argument_list|()
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
literal|"["
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|loggers
argument_list|)
operator|+
literal|"]"
return|;
block|}
comment|/**    * Append an HTML-formatted status readout on the current    * state of the underlying loggers.    * @param sb the StringBuilder to append to    */
DECL|method|appendReport (StringBuilder sb)
name|void
name|appendReport
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|len
init|=
name|loggers
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|len
condition|;
operator|++
name|i
control|)
block|{
name|AsyncLogger
name|l
init|=
name|loggers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|l
argument_list|)
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
expr_stmt|;
name|l
operator|.
name|appendReport
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return the (mutable) list of loggers, for use in tests to    * set up spies    */
annotation|@
name|VisibleForTesting
DECL|method|getLoggersForTests ()
name|List
argument_list|<
name|AsyncLogger
argument_list|>
name|getLoggersForTests
parameter_list|()
block|{
return|return
name|loggers
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////////
comment|// The rest of this file is simply boilerplate wrappers which fan-out the
comment|// various IPC calls to the underlying AsyncLoggers and wrap the result
comment|// in a QuorumCall.
comment|///////////////////////////////////////////////////////////////////////////
DECL|method|getJournalState ()
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|GetJournalStateResponseProto
argument_list|>
name|getJournalState
parameter_list|()
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|GetJournalStateResponseProto
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|logger
operator|.
name|getJournalState
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|isFormatted ()
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Boolean
argument_list|>
name|isFormatted
parameter_list|()
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|logger
operator|.
name|isFormatted
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|newEpoch ( NamespaceInfo nsInfo, long epoch)
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|NewEpochResponseProto
argument_list|>
name|newEpoch
parameter_list|(
name|NamespaceInfo
name|nsInfo
parameter_list|,
name|long
name|epoch
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|NewEpochResponseProto
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|logger
operator|.
name|newEpoch
argument_list|(
name|epoch
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|startLogSegment ( long txid)
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|startLogSegment
parameter_list|(
name|long
name|txid
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|logger
operator|.
name|startLogSegment
argument_list|(
name|txid
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|finalizeLogSegment (long firstTxId, long lastTxId)
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|finalizeLogSegment
parameter_list|(
name|long
name|firstTxId
parameter_list|,
name|long
name|lastTxId
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|logger
operator|.
name|finalizeLogSegment
argument_list|(
name|firstTxId
argument_list|,
name|lastTxId
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|sendEdits ( long segmentTxId, long firstTxnId, int numTxns, byte[] data)
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|sendEdits
parameter_list|(
name|long
name|segmentTxId
parameter_list|,
name|long
name|firstTxnId
parameter_list|,
name|int
name|numTxns
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|logger
operator|.
name|sendEdits
argument_list|(
name|segmentTxId
argument_list|,
name|firstTxnId
argument_list|,
name|numTxns
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|getEditLogManifest ( long fromTxnId, boolean inProgressOk)
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|RemoteEditLogManifest
argument_list|>
name|getEditLogManifest
parameter_list|(
name|long
name|fromTxnId
parameter_list|,
name|boolean
name|inProgressOk
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|RemoteEditLogManifest
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|RemoteEditLogManifest
argument_list|>
name|future
init|=
name|logger
operator|.
name|getEditLogManifest
argument_list|(
name|fromTxnId
argument_list|,
name|inProgressOk
argument_list|)
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|PrepareRecoveryResponseProto
argument_list|>
DECL|method|prepareRecovery (long segmentTxId)
name|prepareRecovery
parameter_list|(
name|long
name|segmentTxId
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|PrepareRecoveryResponseProto
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|PrepareRecoveryResponseProto
argument_list|>
name|future
init|=
name|logger
operator|.
name|prepareRecovery
argument_list|(
name|segmentTxId
argument_list|)
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
DECL|method|acceptRecovery (SegmentStateProto log, URL fromURL)
name|acceptRecovery
parameter_list|(
name|SegmentStateProto
name|log
parameter_list|,
name|URL
name|fromURL
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|logger
operator|.
name|acceptRecovery
argument_list|(
name|log
argument_list|,
name|fromURL
argument_list|)
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|format (NamespaceInfo nsInfo)
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|format
parameter_list|(
name|NamespaceInfo
name|nsInfo
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|logger
operator|.
name|format
argument_list|(
name|nsInfo
argument_list|)
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|doPreUpgrade ()
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|doPreUpgrade
parameter_list|()
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|logger
operator|.
name|doPreUpgrade
argument_list|()
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|doUpgrade (StorageInfo sInfo)
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|doUpgrade
parameter_list|(
name|StorageInfo
name|sInfo
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|logger
operator|.
name|doUpgrade
argument_list|(
name|sInfo
argument_list|)
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|doFinalize ()
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|doFinalize
parameter_list|()
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|logger
operator|.
name|doFinalize
argument_list|()
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|canRollBack (StorageInfo storage, StorageInfo prevStorage, int targetLayoutVersion)
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Boolean
argument_list|>
name|canRollBack
parameter_list|(
name|StorageInfo
name|storage
parameter_list|,
name|StorageInfo
name|prevStorage
parameter_list|,
name|int
name|targetLayoutVersion
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Boolean
argument_list|>
name|future
init|=
name|logger
operator|.
name|canRollBack
argument_list|(
name|storage
argument_list|,
name|prevStorage
argument_list|,
name|targetLayoutVersion
argument_list|)
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|doRollback ()
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|doRollback
parameter_list|()
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|logger
operator|.
name|doRollback
argument_list|()
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|discardSegments (long startTxId)
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|discardSegments
parameter_list|(
name|long
name|startTxId
parameter_list|)
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|logger
operator|.
name|discardSegments
argument_list|(
name|startTxId
argument_list|)
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
DECL|method|getJournalCTime ()
specifier|public
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Long
argument_list|>
name|getJournalCTime
parameter_list|()
block|{
name|Map
argument_list|<
name|AsyncLogger
argument_list|,
name|ListenableFuture
argument_list|<
name|Long
argument_list|>
argument_list|>
name|calls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|loggers
control|)
block|{
name|ListenableFuture
argument_list|<
name|Long
argument_list|>
name|future
init|=
name|logger
operator|.
name|getJournalCTime
argument_list|()
decl_stmt|;
name|calls
operator|.
name|put
argument_list|(
name|logger
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|QuorumCall
operator|.
name|create
argument_list|(
name|calls
argument_list|)
return|;
block|}
block|}
end_class

end_unit

