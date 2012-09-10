begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.protocol
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
name|protocol
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|client
operator|.
name|QuorumJournalManager
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
name|GetEditLogManifestResponseProto
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
name|server
operator|.
name|JournalNode
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
name|namenode
operator|.
name|JournalManager
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
name|security
operator|.
name|KerberosInfo
import|;
end_import

begin_comment
comment|/**  * Protocol used to communicate between {@link QuorumJournalManager}  * and each {@link JournalNode}.  *   * This is responsible for sending edits as well as coordinating  * recovery of the nodes.  */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_USER_NAME_KEY
argument_list|,
name|clientPrincipal
operator|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_USER_NAME_KEY
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|QJournalProtocol
specifier|public
interface|interface
name|QJournalProtocol
block|{
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * Get the current state of the journal, including the most recent    * epoch number and the HTTP port.    */
DECL|method|getJournalState (String journalId)
specifier|public
name|GetJournalStateResponseProto
name|getJournalState
parameter_list|(
name|String
name|journalId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Format the underlying storage for the given namespace.    */
DECL|method|format (String journalId, NamespaceInfo nsInfo)
specifier|public
name|void
name|format
parameter_list|(
name|String
name|journalId
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Begin a new epoch. See the HDFS-3077 design doc for details.    */
DECL|method|newEpoch (String journalId, NamespaceInfo nsInfo, long epoch)
specifier|public
name|NewEpochResponseProto
name|newEpoch
parameter_list|(
name|String
name|journalId
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|,
name|long
name|epoch
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Journal edit records.    * This message is sent by the active name-node to the JournalNodes    * to write edits to their local logs.    */
DECL|method|journal (RequestInfo reqInfo, long segmentTxId, long firstTxnId, int numTxns, byte[] records)
specifier|public
name|void
name|journal
parameter_list|(
name|RequestInfo
name|reqInfo
parameter_list|,
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
name|records
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Heartbeat.    * This is a no-op on the server, except that it verifies that the    * caller is in fact still the active writer, and provides up-to-date    * information on the most recently committed txid.    */
DECL|method|heartbeat (RequestInfo reqInfo)
specifier|public
name|void
name|heartbeat
parameter_list|(
name|RequestInfo
name|reqInfo
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Start writing to a new log segment on the JournalNode.    * Before calling this, one should finalize the previous segment    * using {@link #finalizeLogSegment(RequestInfo, long, long)}.    *     * @param txid the first txid in the new log    */
DECL|method|startLogSegment (RequestInfo reqInfo, long txid)
specifier|public
name|void
name|startLogSegment
parameter_list|(
name|RequestInfo
name|reqInfo
parameter_list|,
name|long
name|txid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Finalize the given log segment on the JournalNode. The segment    * is expected to be in-progress and starting at the given startTxId.    *    * @param startTxId the starting transaction ID of the log    * @param endTxId the expected last transaction in the given log    * @throws IOException if no such segment exists    */
DECL|method|finalizeLogSegment (RequestInfo reqInfo, long startTxId, long endTxId)
specifier|public
name|void
name|finalizeLogSegment
parameter_list|(
name|RequestInfo
name|reqInfo
parameter_list|,
name|long
name|startTxId
parameter_list|,
name|long
name|endTxId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @throws IOException     * @see JournalManager#purgeLogsOlderThan(long)    */
DECL|method|purgeLogsOlderThan (RequestInfo requestInfo, long minTxIdToKeep)
specifier|public
name|void
name|purgeLogsOlderThan
parameter_list|(
name|RequestInfo
name|requestInfo
parameter_list|,
name|long
name|minTxIdToKeep
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @param jid the journal from which to enumerate edits    * @param sinceTxId the first transaction which the client cares about    * @return a list of edit log segments since the given transaction ID.    */
DECL|method|getEditLogManifest ( String jid, long sinceTxId)
specifier|public
name|GetEditLogManifestResponseProto
name|getEditLogManifest
parameter_list|(
name|String
name|jid
parameter_list|,
name|long
name|sinceTxId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Begin the recovery process for a given segment. See the HDFS-3077    * design document for details.    */
DECL|method|prepareRecovery (RequestInfo reqInfo, long segmentTxId)
specifier|public
name|PrepareRecoveryResponseProto
name|prepareRecovery
parameter_list|(
name|RequestInfo
name|reqInfo
parameter_list|,
name|long
name|segmentTxId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Accept a proposed recovery for the given transaction ID.    */
DECL|method|acceptRecovery (RequestInfo reqInfo, SegmentStateProto stateToAccept, URL fromUrl)
specifier|public
name|void
name|acceptRecovery
parameter_list|(
name|RequestInfo
name|reqInfo
parameter_list|,
name|SegmentStateProto
name|stateToAccept
parameter_list|,
name|URL
name|fromUrl
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

