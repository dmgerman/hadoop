begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.server
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
name|server
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|HDFSPolicyProvider
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
name|protocolPB
operator|.
name|PBHelper
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
name|QJournalProtocolService
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
name|qjournal
operator|.
name|protocolPB
operator|.
name|QJournalProtocolPB
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
name|protocolPB
operator|.
name|QJournalProtocolServerSideTranslatorPB
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|RPC
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
name|RPC
operator|.
name|Server
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
name|net
operator|.
name|NetUtils
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
name|BlockingService
import|;
end_import

begin_class
DECL|class|JournalNodeRpcServer
class|class
name|JournalNodeRpcServer
implements|implements
name|QJournalProtocol
block|{
DECL|field|HANDLER_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|HANDLER_COUNT
init|=
literal|5
decl_stmt|;
DECL|field|jn
specifier|private
name|JournalNode
name|jn
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|method|JournalNodeRpcServer (Configuration conf, JournalNode jn)
name|JournalNodeRpcServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|JournalNode
name|jn
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|jn
operator|=
name|jn
expr_stmt|;
name|Configuration
name|confCopy
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Ensure that nagling doesn't kick in, which could cause latency issues.
name|confCopy
operator|.
name|setBoolean
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_SERVER_TCPNODELAY_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|getAddress
argument_list|(
name|confCopy
argument_list|)
decl_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|confCopy
argument_list|,
name|QJournalProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|QJournalProtocolServerSideTranslatorPB
name|translator
init|=
operator|new
name|QJournalProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|BlockingService
name|service
init|=
name|QJournalProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
name|translator
argument_list|)
decl_stmt|;
name|this
operator|.
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|confCopy
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|QJournalProtocolPB
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|service
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|setPort
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
name|HANDLER_COUNT
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// set service-level authorization security policy
if|if
condition|(
name|confCopy
operator|.
name|getBoolean
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHORIZATION
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|server
operator|.
name|refreshServiceAcl
argument_list|(
name|confCopy
argument_list|,
operator|new
name|HDFSPolicyProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|start ()
name|void
name|start
parameter_list|()
block|{
name|this
operator|.
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|getAddress ()
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|server
operator|.
name|getListenerAddress
argument_list|()
return|;
block|}
DECL|method|join ()
name|void
name|join
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|this
operator|.
name|server
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
name|void
name|stop
parameter_list|()
block|{
name|this
operator|.
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getAddress (Configuration conf)
specifier|static
name|InetSocketAddress
name|getAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|addr
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_RPC_ADDRESS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_RPC_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|,
literal|0
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_RPC_ADDRESS_KEY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isFormatted (String journalId)
specifier|public
name|boolean
name|isFormatted
parameter_list|(
name|String
name|journalId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|)
operator|.
name|isFormatted
argument_list|()
return|;
block|}
annotation|@
name|Override
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
block|{
name|long
name|epoch
init|=
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|)
operator|.
name|getLastPromisedEpoch
argument_list|()
decl_stmt|;
return|return
name|GetJournalStateResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setLastPromisedEpoch
argument_list|(
name|epoch
argument_list|)
operator|.
name|setHttpPort
argument_list|(
name|jn
operator|.
name|getBoundHttpAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|)
operator|.
name|newEpoch
argument_list|(
name|nsInfo
argument_list|,
name|epoch
argument_list|)
return|;
block|}
annotation|@
name|Override
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
block|{
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|)
operator|.
name|format
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|reqInfo
operator|.
name|getJournalId
argument_list|()
argument_list|)
operator|.
name|journal
argument_list|(
name|reqInfo
argument_list|,
name|segmentTxId
argument_list|,
name|firstTxnId
argument_list|,
name|numTxns
argument_list|,
name|records
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|reqInfo
operator|.
name|getJournalId
argument_list|()
argument_list|)
operator|.
name|heartbeat
argument_list|(
name|reqInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|reqInfo
operator|.
name|getJournalId
argument_list|()
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|reqInfo
argument_list|,
name|txid
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|reqInfo
operator|.
name|getJournalId
argument_list|()
argument_list|)
operator|.
name|finalizeLogSegment
argument_list|(
name|reqInfo
argument_list|,
name|startTxId
argument_list|,
name|endTxId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|purgeLogsOlderThan (RequestInfo reqInfo, long minTxIdToKeep)
specifier|public
name|void
name|purgeLogsOlderThan
parameter_list|(
name|RequestInfo
name|reqInfo
parameter_list|,
name|long
name|minTxIdToKeep
parameter_list|)
throws|throws
name|IOException
block|{
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|reqInfo
operator|.
name|getJournalId
argument_list|()
argument_list|)
operator|.
name|purgeLogsOlderThan
argument_list|(
name|reqInfo
argument_list|,
name|minTxIdToKeep
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEditLogManifest (String jid, long sinceTxId, boolean forReading, boolean inProgressOk)
specifier|public
name|GetEditLogManifestResponseProto
name|getEditLogManifest
parameter_list|(
name|String
name|jid
parameter_list|,
name|long
name|sinceTxId
parameter_list|,
name|boolean
name|forReading
parameter_list|,
name|boolean
name|inProgressOk
parameter_list|)
throws|throws
name|IOException
block|{
name|RemoteEditLogManifest
name|manifest
init|=
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|jid
argument_list|)
operator|.
name|getEditLogManifest
argument_list|(
name|sinceTxId
argument_list|,
name|forReading
argument_list|,
name|inProgressOk
argument_list|)
decl_stmt|;
return|return
name|GetEditLogManifestResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setManifest
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|manifest
argument_list|)
argument_list|)
operator|.
name|setHttpPort
argument_list|(
name|jn
operator|.
name|getBoundHttpAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|reqInfo
operator|.
name|getJournalId
argument_list|()
argument_list|)
operator|.
name|prepareRecovery
argument_list|(
name|reqInfo
argument_list|,
name|segmentTxId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|acceptRecovery (RequestInfo reqInfo, SegmentStateProto log, URL fromUrl)
specifier|public
name|void
name|acceptRecovery
parameter_list|(
name|RequestInfo
name|reqInfo
parameter_list|,
name|SegmentStateProto
name|log
parameter_list|,
name|URL
name|fromUrl
parameter_list|)
throws|throws
name|IOException
block|{
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|reqInfo
operator|.
name|getJournalId
argument_list|()
argument_list|)
operator|.
name|acceptRecovery
argument_list|(
name|reqInfo
argument_list|,
name|log
argument_list|,
name|fromUrl
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

