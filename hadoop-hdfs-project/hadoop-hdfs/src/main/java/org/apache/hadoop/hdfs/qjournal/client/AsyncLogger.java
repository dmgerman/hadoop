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
name|net
operator|.
name|InetSocketAddress
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|QJournalProtocol
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
name|qjournal
operator|.
name|protocol
operator|.
name|RequestInfo
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
import|;
end_import

begin_comment
comment|/**  * Interface for a remote log which is only communicated with asynchronously.  * This is essentially a wrapper around {@link QJournalProtocol} with the key  * differences being:  *   *<ul>  *<li>All methods return {@link ListenableFuture}s instead of synchronous  * objects.</li>  *<li>The {@link RequestInfo} objects are created by the underlying  * implementation.</li>  *</ul>  */
end_comment

begin_interface
DECL|interface|AsyncLogger
interface|interface
name|AsyncLogger
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|createLogger (Configuration conf, NamespaceInfo nsInfo, String journalId, String nameServiceId, InetSocketAddress addr)
name|AsyncLogger
name|createLogger
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|,
name|String
name|journalId
parameter_list|,
name|String
name|nameServiceId
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|)
function_decl|;
block|}
comment|/**    * Send a batch of edits to the logger.    * @param segmentTxId the first txid in the current segment    * @param firstTxnId the first txid of the edits.    * @param numTxns the number of transactions in the batch    * @param data the actual data to be sent    */
DECL|method|sendEdits ( final long segmentTxId, final long firstTxnId, final int numTxns, final byte[] data)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|sendEdits
parameter_list|(
specifier|final
name|long
name|segmentTxId
parameter_list|,
specifier|final
name|long
name|firstTxnId
parameter_list|,
specifier|final
name|int
name|numTxns
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|)
function_decl|;
comment|/**    * Begin writing a new log segment.    *     * @param txid the first txid to be written to the new log    * @param layoutVersion the LayoutVersion of the log    */
DECL|method|startLogSegment (long txid, int layoutVersion)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|startLogSegment
parameter_list|(
name|long
name|txid
parameter_list|,
name|int
name|layoutVersion
parameter_list|)
function_decl|;
comment|/**    * Finalize a log segment.    *     * @param startTxId the first txid that was written to the segment    * @param endTxId the last txid that was written to the segment    */
DECL|method|finalizeLogSegment ( long startTxId, long endTxId)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|finalizeLogSegment
parameter_list|(
name|long
name|startTxId
parameter_list|,
name|long
name|endTxId
parameter_list|)
function_decl|;
comment|/**    * Allow the remote node to purge edit logs earlier than this.    * @param minTxIdToKeep the min txid which must be retained    */
DECL|method|purgeLogsOlderThan (long minTxIdToKeep)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|purgeLogsOlderThan
parameter_list|(
name|long
name|minTxIdToKeep
parameter_list|)
function_decl|;
comment|/**    * Format the log directory.    * @param nsInfo the namespace info to format with    */
DECL|method|format (NamespaceInfo nsInfo)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|format
parameter_list|(
name|NamespaceInfo
name|nsInfo
parameter_list|)
function_decl|;
comment|/**    * @return whether or not the remote node has any valid data.    */
DECL|method|isFormatted ()
specifier|public
name|ListenableFuture
argument_list|<
name|Boolean
argument_list|>
name|isFormatted
parameter_list|()
function_decl|;
comment|/**    * @return the state of the last epoch on the target node.    */
DECL|method|getJournalState ()
specifier|public
name|ListenableFuture
argument_list|<
name|GetJournalStateResponseProto
argument_list|>
name|getJournalState
parameter_list|()
function_decl|;
comment|/**    * Begin a new epoch on the target node.    */
DECL|method|newEpoch (long epoch)
specifier|public
name|ListenableFuture
argument_list|<
name|NewEpochResponseProto
argument_list|>
name|newEpoch
parameter_list|(
name|long
name|epoch
parameter_list|)
function_decl|;
comment|/**    * Fetch the list of edit logs available on the remote node.    */
DECL|method|getEditLogManifest ( long fromTxnId, boolean inProgressOk)
specifier|public
name|ListenableFuture
argument_list|<
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
function_decl|;
comment|/**    * Prepare recovery. See the HDFS-3077 design document for details.    */
DECL|method|prepareRecovery ( long segmentTxId)
specifier|public
name|ListenableFuture
argument_list|<
name|PrepareRecoveryResponseProto
argument_list|>
name|prepareRecovery
parameter_list|(
name|long
name|segmentTxId
parameter_list|)
function_decl|;
comment|/**    * Accept a recovery proposal. See the HDFS-3077 design document for details.    */
DECL|method|acceptRecovery (SegmentStateProto log, URL fromUrl)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|acceptRecovery
parameter_list|(
name|SegmentStateProto
name|log
parameter_list|,
name|URL
name|fromUrl
parameter_list|)
function_decl|;
comment|/**    * Set the epoch number used for all future calls.    */
DECL|method|setEpoch (long e)
specifier|public
name|void
name|setEpoch
parameter_list|(
name|long
name|e
parameter_list|)
function_decl|;
comment|/**    * Let the logger know the highest committed txid across all loggers in the    * set. This txid may be higher than the last committed txid for<em>this</em>    * logger. See HDFS-3863 for details.    */
DECL|method|setCommittedTxId (long txid)
specifier|public
name|void
name|setCommittedTxId
parameter_list|(
name|long
name|txid
parameter_list|)
function_decl|;
comment|/**    * Build an HTTP URL to fetch the log segment with the given startTxId.    */
DECL|method|buildURLToFetchLogs (long segmentTxId)
specifier|public
name|URL
name|buildURLToFetchLogs
parameter_list|(
name|long
name|segmentTxId
parameter_list|)
function_decl|;
comment|/**    * Tear down any resources, connections, etc. The proxy may not be used    * after this point, and any in-flight RPCs may throw an exception.    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Append an HTML-formatted report for this logger's status to the provided    * StringBuilder. This is displayed on the NN web UI.    */
DECL|method|appendReport (StringBuilder sb)
specifier|public
name|void
name|appendReport
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
function_decl|;
DECL|method|doPreUpgrade ()
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|doPreUpgrade
parameter_list|()
function_decl|;
DECL|method|doUpgrade (StorageInfo sInfo)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|doUpgrade
parameter_list|(
name|StorageInfo
name|sInfo
parameter_list|)
function_decl|;
DECL|method|doFinalize ()
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|doFinalize
parameter_list|()
function_decl|;
DECL|method|canRollBack (StorageInfo storage, StorageInfo prevStorage, int targetLayoutVersion)
specifier|public
name|ListenableFuture
argument_list|<
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
function_decl|;
DECL|method|doRollback ()
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|doRollback
parameter_list|()
function_decl|;
DECL|method|discardSegments (long startTxId)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|discardSegments
parameter_list|(
name|long
name|startTxId
parameter_list|)
function_decl|;
DECL|method|getJournalCTime ()
specifier|public
name|ListenableFuture
argument_list|<
name|Long
argument_list|>
name|getJournalCTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

