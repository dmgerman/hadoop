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
name|Lists
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
name|Sets
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
name|ServiceException
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
name|FileUtil
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
name|QJournalProtocolProtos
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
name|JournalIdProto
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
name|GetEditLogManifestRequestProto
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
name|server
operator|.
name|common
operator|.
name|Util
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
name|RemoteEditLog
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
name|util
operator|.
name|DataTransferThrottler
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
name|util
operator|.
name|Daemon
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
name|File
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
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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

begin_comment
comment|/**  * A Journal Sync thread runs through the lifetime of the JN. It periodically  * gossips with other journal nodes to compare edit log manifests and if it  * detects any missing log segment, it downloads it from the other journal node  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|JournalNodeSyncer
specifier|public
class|class
name|JournalNodeSyncer
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JournalNodeSyncer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|jn
specifier|private
specifier|final
name|JournalNode
name|jn
decl_stmt|;
DECL|field|journal
specifier|private
specifier|final
name|Journal
name|journal
decl_stmt|;
DECL|field|jid
specifier|private
specifier|final
name|String
name|jid
decl_stmt|;
DECL|field|jidProto
specifier|private
specifier|final
name|JournalIdProto
name|jidProto
decl_stmt|;
DECL|field|jnStorage
specifier|private
specifier|final
name|JNStorage
name|jnStorage
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|syncJournalDaemon
specifier|private
specifier|volatile
name|Daemon
name|syncJournalDaemon
decl_stmt|;
DECL|field|shouldSync
specifier|private
specifier|volatile
name|boolean
name|shouldSync
init|=
literal|true
decl_stmt|;
DECL|field|otherJNProxies
specifier|private
name|List
argument_list|<
name|JournalNodeProxy
argument_list|>
name|otherJNProxies
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|numOtherJNs
specifier|private
name|int
name|numOtherJNs
decl_stmt|;
DECL|field|journalNodeIndexForSync
specifier|private
name|int
name|journalNodeIndexForSync
init|=
literal|0
decl_stmt|;
DECL|field|journalSyncInterval
specifier|private
specifier|final
name|long
name|journalSyncInterval
decl_stmt|;
DECL|field|logSegmentTransferTimeout
specifier|private
specifier|final
name|int
name|logSegmentTransferTimeout
decl_stmt|;
DECL|field|throttler
specifier|private
specifier|final
name|DataTransferThrottler
name|throttler
decl_stmt|;
DECL|method|JournalNodeSyncer (JournalNode jouranlNode, Journal journal, String jid, Configuration conf)
name|JournalNodeSyncer
parameter_list|(
name|JournalNode
name|jouranlNode
parameter_list|,
name|Journal
name|journal
parameter_list|,
name|String
name|jid
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|jn
operator|=
name|jouranlNode
expr_stmt|;
name|this
operator|.
name|journal
operator|=
name|journal
expr_stmt|;
name|this
operator|.
name|jid
operator|=
name|jid
expr_stmt|;
name|this
operator|.
name|jidProto
operator|=
name|convertJournalId
argument_list|(
name|this
operator|.
name|jid
argument_list|)
expr_stmt|;
name|this
operator|.
name|jnStorage
operator|=
name|journal
operator|.
name|getStorage
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|journalSyncInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_SYNC_INTERVAL_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_SYNC_INTERVAL_DEFAULT
argument_list|)
expr_stmt|;
name|logSegmentTransferTimeout
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_EDIT_LOG_TRANSFER_TIMEOUT_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_EDIT_LOG_TRANSFER_TIMEOUT_DEFAULT
argument_list|)
expr_stmt|;
name|throttler
operator|=
name|getThrottler
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|stopSync ()
name|void
name|stopSync
parameter_list|()
block|{
name|shouldSync
operator|=
literal|false
expr_stmt|;
comment|// Delete the edits.sync directory
name|File
name|editsSyncDir
init|=
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|getEditsSyncDir
argument_list|()
decl_stmt|;
if|if
condition|(
name|editsSyncDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|editsSyncDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|syncJournalDaemon
operator|!=
literal|null
condition|)
block|{
name|syncJournalDaemon
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting SyncJournal daemon for journal "
operator|+
name|jid
argument_list|)
expr_stmt|;
if|if
condition|(
name|getOtherJournalNodeProxies
argument_list|()
condition|)
block|{
name|startSyncJournalsDaemon
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to start SyncJournal daemon for journal "
operator|+
name|jid
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createEditsSyncDir ()
specifier|private
name|boolean
name|createEditsSyncDir
parameter_list|()
block|{
name|File
name|editsSyncDir
init|=
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|getEditsSyncDir
argument_list|()
decl_stmt|;
if|if
condition|(
name|editsSyncDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|editsSyncDir
operator|+
literal|" directory already exists."
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
name|editsSyncDir
operator|.
name|mkdir
argument_list|()
return|;
block|}
DECL|method|getOtherJournalNodeProxies ()
specifier|private
name|boolean
name|getOtherJournalNodeProxies
parameter_list|()
block|{
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|otherJournalNodes
init|=
name|getOtherJournalNodeAddrs
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherJournalNodes
operator|==
literal|null
operator|||
name|otherJournalNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Other JournalNode addresses not available. Journal Syncing "
operator|+
literal|"cannot be done"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
for|for
control|(
name|InetSocketAddress
name|addr
range|:
name|otherJournalNodes
control|)
block|{
try|try
block|{
name|otherJNProxies
operator|.
name|add
argument_list|(
operator|new
name|JournalNodeProxy
argument_list|(
name|addr
argument_list|)
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
name|warn
argument_list|(
literal|"Could not add proxy for Journal at addresss "
operator|+
name|addr
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|otherJNProxies
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot sync as there is no other JN available for sync."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|numOtherJNs
operator|=
name|otherJNProxies
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|startSyncJournalsDaemon ()
specifier|private
name|void
name|startSyncJournalsDaemon
parameter_list|()
block|{
name|syncJournalDaemon
operator|=
operator|new
name|Daemon
argument_list|(
parameter_list|()
lambda|->
block|{
comment|// Wait for journal to be formatted to create edits.sync directory
while|while
condition|(
operator|!
name|journal
operator|.
name|isFormatted
argument_list|()
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|journalSyncInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"JournalNodeSyncer daemon received Runtime exception."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
operator|!
name|createEditsSyncDir
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create directory for downloading log "
operator|+
literal|"segments: %s. Stopping Journal Node Sync."
argument_list|,
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|getEditsSyncDir
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
while|while
condition|(
name|shouldSync
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|journal
operator|.
name|isFormatted
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Journal cannot sync. Not formatted."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|syncJournals
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|journalSyncInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shouldSync
condition|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|InterruptedException
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping JournalNode Sync."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"JournalNodeSyncer received an exception while "
operator|+
literal|"shutting down."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
if|if
condition|(
name|t
operator|instanceof
name|InterruptedException
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"JournalNodeSyncer interrupted"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"JournalNodeSyncer daemon received Runtime exception. "
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|syncJournalDaemon
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|syncJournals ()
specifier|private
name|void
name|syncJournals
parameter_list|()
block|{
name|syncWithJournalAtIndex
argument_list|(
name|journalNodeIndexForSync
argument_list|)
expr_stmt|;
name|journalNodeIndexForSync
operator|=
operator|(
name|journalNodeIndexForSync
operator|+
literal|1
operator|)
operator|%
name|numOtherJNs
expr_stmt|;
block|}
DECL|method|syncWithJournalAtIndex (int index)
specifier|private
name|void
name|syncWithJournalAtIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Syncing Journal "
operator|+
name|jn
operator|.
name|getBoundIpcAddress
argument_list|()
operator|.
name|getAddress
argument_list|()
operator|+
literal|":"
operator|+
name|jn
operator|.
name|getBoundIpcAddress
argument_list|()
operator|.
name|getPort
argument_list|()
operator|+
literal|" with "
operator|+
name|otherJNProxies
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|+
literal|", journal id: "
operator|+
name|jid
argument_list|)
expr_stmt|;
specifier|final
name|QJournalProtocolPB
name|jnProxy
init|=
name|otherJNProxies
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|jnProxy
decl_stmt|;
if|if
condition|(
name|jnProxy
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"JournalNode Proxy not found."
argument_list|)
expr_stmt|;
return|return;
block|}
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|thisJournalEditLogs
decl_stmt|;
try|try
block|{
name|thisJournalEditLogs
operator|=
name|journal
operator|.
name|getEditLogManifest
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
operator|.
name|getLogs
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
name|error
argument_list|(
literal|"Exception in getting local edit log manifest"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|GetEditLogManifestResponseProto
name|editLogManifest
decl_stmt|;
try|try
block|{
name|editLogManifest
operator|=
name|jnProxy
operator|.
name|getEditLogManifest
argument_list|(
literal|null
argument_list|,
name|GetEditLogManifestRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setJid
argument_list|(
name|jidProto
argument_list|)
operator|.
name|setSinceTxId
argument_list|(
literal|0
argument_list|)
operator|.
name|setInProgressOk
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not sync with Journal at "
operator|+
name|otherJNProxies
operator|.
name|get
argument_list|(
name|journalNodeIndexForSync
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|getMissingLogSegments
argument_list|(
name|thisJournalEditLogs
argument_list|,
name|editLogManifest
argument_list|,
name|otherJNProxies
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getOtherJournalNodeAddrs ()
specifier|private
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|getOtherJournalNodeAddrs
parameter_list|()
block|{
name|URI
name|uri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|uriStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|uriStr
operator|==
literal|null
operator|||
name|uriStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not construct Shared Edits Uri"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|uriStr
argument_list|)
expr_stmt|;
return|return
name|Util
operator|.
name|getLoggerAddresses
argument_list|(
name|uri
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|jn
operator|.
name|getBoundIpcAddress
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"The conf property "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
operator|+
literal|" not set properly."
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
literal|"Could not parse JournalNode addresses: "
operator|+
name|uri
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|convertJournalId (String journalId)
specifier|private
name|JournalIdProto
name|convertJournalId
parameter_list|(
name|String
name|journalId
parameter_list|)
block|{
return|return
name|QJournalProtocolProtos
operator|.
name|JournalIdProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setIdentifier
argument_list|(
name|journalId
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getMissingLogSegments (List<RemoteEditLog> thisJournalEditLogs, GetEditLogManifestResponseProto response, JournalNodeProxy remoteJNproxy)
specifier|private
name|void
name|getMissingLogSegments
parameter_list|(
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|thisJournalEditLogs
parameter_list|,
name|GetEditLogManifestResponseProto
name|response
parameter_list|,
name|JournalNodeProxy
name|remoteJNproxy
parameter_list|)
block|{
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|otherJournalEditLogs
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|response
operator|.
name|getManifest
argument_list|()
argument_list|)
operator|.
name|getLogs
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherJournalEditLogs
operator|==
literal|null
operator|||
name|otherJournalEditLogs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Journal at "
operator|+
name|remoteJNproxy
operator|.
name|jnAddr
operator|+
literal|" has no edit logs"
argument_list|)
expr_stmt|;
return|return;
block|}
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|missingLogs
init|=
name|getMissingLogList
argument_list|(
name|thisJournalEditLogs
argument_list|,
name|otherJournalEditLogs
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|missingLogs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|NamespaceInfo
name|nsInfo
init|=
name|jnStorage
operator|.
name|getNamespaceInfo
argument_list|()
decl_stmt|;
for|for
control|(
name|RemoteEditLog
name|missingLog
range|:
name|missingLogs
control|)
block|{
name|URL
name|url
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|remoteJNproxy
operator|.
name|httpServerUrl
operator|==
literal|null
condition|)
block|{
name|remoteJNproxy
operator|.
name|httpServerUrl
operator|=
name|getHttpServerURI
argument_list|(
literal|"http"
argument_list|,
name|remoteJNproxy
operator|.
name|jnAddr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|response
operator|.
name|getHttpPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|urlPath
init|=
name|GetJournalEditServlet
operator|.
name|buildPath
argument_list|(
name|jid
argument_list|,
name|missingLog
operator|.
name|getStartTxId
argument_list|()
argument_list|,
name|nsInfo
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|url
operator|=
operator|new
name|URL
argument_list|(
name|remoteJNproxy
operator|.
name|httpServerUrl
argument_list|,
name|urlPath
argument_list|)
expr_stmt|;
name|success
operator|=
name|downloadMissingLogSegment
argument_list|(
name|url
argument_list|,
name|missingLog
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"MalformedURL when download missing log segment"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in downloading missing log segment from url "
operator|+
name|url
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Aborting current sync attempt."
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
comment|/**    *  Returns the logs present in otherJournalEditLogs and missing from    *  thisJournalEditLogs.    */
DECL|method|getMissingLogList ( List<RemoteEditLog> thisJournalEditLogs, List<RemoteEditLog> otherJournalEditLogs)
specifier|private
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|getMissingLogList
parameter_list|(
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|thisJournalEditLogs
parameter_list|,
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|otherJournalEditLogs
parameter_list|)
block|{
if|if
condition|(
name|thisJournalEditLogs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|otherJournalEditLogs
return|;
block|}
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|missingEditLogs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|int
name|thisJnIndex
init|=
literal|0
decl_stmt|,
name|otherJnIndex
init|=
literal|0
decl_stmt|;
name|int
name|thisJnNumLogs
init|=
name|thisJournalEditLogs
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|otherJnNumLogs
init|=
name|otherJournalEditLogs
operator|.
name|size
argument_list|()
decl_stmt|;
while|while
condition|(
name|thisJnIndex
operator|<
name|thisJnNumLogs
operator|&&
name|otherJnIndex
operator|<
name|otherJnNumLogs
condition|)
block|{
name|long
name|localJNstartTxId
init|=
name|thisJournalEditLogs
operator|.
name|get
argument_list|(
name|thisJnIndex
argument_list|)
operator|.
name|getStartTxId
argument_list|()
decl_stmt|;
name|long
name|remoteJNstartTxId
init|=
name|otherJournalEditLogs
operator|.
name|get
argument_list|(
name|otherJnIndex
argument_list|)
operator|.
name|getStartTxId
argument_list|()
decl_stmt|;
if|if
condition|(
name|localJNstartTxId
operator|==
name|remoteJNstartTxId
condition|)
block|{
name|thisJnIndex
operator|++
expr_stmt|;
name|otherJnIndex
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localJNstartTxId
operator|>
name|remoteJNstartTxId
condition|)
block|{
name|missingEditLogs
operator|.
name|add
argument_list|(
name|otherJournalEditLogs
operator|.
name|get
argument_list|(
name|otherJnIndex
argument_list|)
argument_list|)
expr_stmt|;
name|otherJnIndex
operator|++
expr_stmt|;
block|}
else|else
block|{
name|thisJnIndex
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|otherJnIndex
operator|<
name|otherJnNumLogs
condition|)
block|{
for|for
control|(
init|;
name|otherJnIndex
operator|<
name|otherJnNumLogs
condition|;
name|otherJnIndex
operator|++
control|)
block|{
name|missingEditLogs
operator|.
name|add
argument_list|(
name|otherJournalEditLogs
operator|.
name|get
argument_list|(
name|otherJnIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|missingEditLogs
return|;
block|}
DECL|method|getHttpServerURI (String scheme, String hostname, int port)
specifier|private
name|URL
name|getHttpServerURI
parameter_list|(
name|String
name|scheme
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|MalformedURLException
block|{
return|return
operator|new
name|URL
argument_list|(
name|scheme
argument_list|,
name|hostname
argument_list|,
name|port
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/**    * Transfer an edit log from one journal node to another for sync-up.    */
DECL|method|downloadMissingLogSegment (URL url, RemoteEditLog log)
specifier|private
name|boolean
name|downloadMissingLogSegment
parameter_list|(
name|URL
name|url
parameter_list|,
name|RemoteEditLog
name|log
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Downloading missing Edit Log from "
operator|+
name|url
operator|+
literal|" to "
operator|+
name|jnStorage
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|log
operator|.
name|getStartTxId
argument_list|()
operator|>
literal|0
operator|&&
name|log
operator|.
name|getEndTxId
argument_list|()
operator|>
literal|0
operator|:
literal|"bad log: "
operator|+
name|log
assert|;
name|File
name|finalEditsFile
init|=
name|jnStorage
operator|.
name|getFinalizedEditsFile
argument_list|(
name|log
operator|.
name|getStartTxId
argument_list|()
argument_list|,
name|log
operator|.
name|getEndTxId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|finalEditsFile
operator|.
name|exists
argument_list|()
operator|&&
name|FileUtil
operator|.
name|canRead
argument_list|(
name|finalEditsFile
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Skipping download of remote edit log "
operator|+
name|log
operator|+
literal|" since it's"
operator|+
literal|" already stored locally at "
operator|+
name|finalEditsFile
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// Download the log segment to current.tmp directory first.
name|File
name|tmpEditsFile
init|=
name|jnStorage
operator|.
name|getTemporaryEditsFile
argument_list|(
name|log
operator|.
name|getStartTxId
argument_list|()
argument_list|,
name|log
operator|.
name|getEndTxId
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Util
operator|.
name|doGetUrl
argument_list|(
name|url
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|tmpEditsFile
argument_list|)
argument_list|,
name|jnStorage
argument_list|,
literal|false
argument_list|,
name|logSegmentTransferTimeout
argument_list|,
name|throttler
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
literal|"Download of Edit Log file for Syncing failed. Deleting temp "
operator|+
literal|"file: "
operator|+
name|tmpEditsFile
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|tmpEditsFile
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Deleting "
operator|+
name|tmpEditsFile
operator|+
literal|" has failed"
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Downloaded file "
operator|+
name|tmpEditsFile
operator|.
name|getName
argument_list|()
operator|+
literal|" of size "
operator|+
name|tmpEditsFile
operator|.
name|length
argument_list|()
operator|+
literal|" bytes."
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|moveSuccess
init|=
name|journal
operator|.
name|moveTmpSegmentToCurrent
argument_list|(
name|tmpEditsFile
argument_list|,
name|finalEditsFile
argument_list|,
name|log
operator|.
name|getEndTxId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|moveSuccess
condition|)
block|{
comment|// If move is not successful, delete the tmpFile
name|LOG
operator|.
name|debug
argument_list|(
literal|"Move to current directory unsuccessful. Deleting temporary "
operator|+
literal|"file: "
operator|+
name|tmpEditsFile
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|tmpEditsFile
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Deleting "
operator|+
name|tmpEditsFile
operator|+
literal|" has failed"
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|getThrottler (Configuration conf)
specifier|private
specifier|static
name|DataTransferThrottler
name|getThrottler
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|long
name|transferBandwidth
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_EDIT_LOG_TRANSFER_RATE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_EDIT_LOG_TRANSFER_RATE_DEFAULT
argument_list|)
decl_stmt|;
name|DataTransferThrottler
name|throttler
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|transferBandwidth
operator|>
literal|0
condition|)
block|{
name|throttler
operator|=
operator|new
name|DataTransferThrottler
argument_list|(
name|transferBandwidth
argument_list|)
expr_stmt|;
block|}
return|return
name|throttler
return|;
block|}
DECL|class|JournalNodeProxy
specifier|private
class|class
name|JournalNodeProxy
block|{
DECL|field|jnAddr
specifier|private
specifier|final
name|InetSocketAddress
name|jnAddr
decl_stmt|;
DECL|field|jnProxy
specifier|private
specifier|final
name|QJournalProtocolPB
name|jnProxy
decl_stmt|;
DECL|field|httpServerUrl
specifier|private
name|URL
name|httpServerUrl
decl_stmt|;
DECL|method|JournalNodeProxy (InetSocketAddress jnAddr)
name|JournalNodeProxy
parameter_list|(
name|InetSocketAddress
name|jnAddr
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|jnAddr
operator|=
name|jnAddr
expr_stmt|;
name|this
operator|.
name|jnProxy
operator|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|QJournalProtocolPB
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|QJournalProtocolPB
operator|.
name|class
argument_list|)
argument_list|,
name|jnAddr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
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
name|jnAddr
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

