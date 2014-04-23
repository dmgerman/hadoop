begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
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
name|SocketTimeoutException
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
name|ha
operator|.
name|ServiceFailedException
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
name|DFSUtil
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
name|NameNodeProxies
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
name|HdfsConstants
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
name|UnregisteredNodeException
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
name|HdfsConstants
operator|.
name|SafeModeAction
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
name|proto
operator|.
name|JournalProtocolProtos
operator|.
name|JournalProtocolService
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
name|JournalProtocolPB
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
name|JournalProtocolServerSideTranslatorPB
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
name|HdfsServerConstants
operator|.
name|NamenodeRole
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
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|Storage
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
name|ha
operator|.
name|HAState
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
name|FenceResponse
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
name|JournalInfo
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
name|JournalProtocol
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
name|NamenodeProtocol
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
name|NamenodeRegistration
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
name|ipc
operator|.
name|StandbyException
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
name|net
operator|.
name|NetUtils
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
name|UserGroupInformation
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
name|protobuf
operator|.
name|BlockingService
import|;
end_import

begin_comment
comment|/**  * BackupNode.  *<p>  * Backup node can play two roles.  *<ol>  *<li>{@link NamenodeRole#CHECKPOINT} node periodically creates checkpoints,   * that is downloads image and edits from the active node, merges them, and  * uploads the new image back to the active.</li>  *<li>{@link NamenodeRole#BACKUP} node keeps its namespace in sync with the  * active node, and periodically creates checkpoints by simply saving the  * namespace image to local disk(s).</li>  *</ol>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BackupNode
specifier|public
class|class
name|BackupNode
extends|extends
name|NameNode
block|{
DECL|field|BN_ADDRESS_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|BN_ADDRESS_NAME_KEY
init|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_ADDRESS_KEY
decl_stmt|;
DECL|field|BN_ADDRESS_DEFAULT
specifier|private
specifier|static
specifier|final
name|String
name|BN_ADDRESS_DEFAULT
init|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_ADDRESS_DEFAULT
decl_stmt|;
DECL|field|BN_HTTP_ADDRESS_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|BN_HTTP_ADDRESS_NAME_KEY
init|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY
decl_stmt|;
DECL|field|BN_HTTP_ADDRESS_DEFAULT
specifier|private
specifier|static
specifier|final
name|String
name|BN_HTTP_ADDRESS_DEFAULT
init|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_DEFAULT
decl_stmt|;
DECL|field|BN_SERVICE_RPC_ADDRESS_KEY
specifier|private
specifier|static
specifier|final
name|String
name|BN_SERVICE_RPC_ADDRESS_KEY
init|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_SERVICE_RPC_ADDRESS_KEY
decl_stmt|;
DECL|field|BN_SAFEMODE_THRESHOLD_PCT_DEFAULT
specifier|private
specifier|static
specifier|final
name|float
name|BN_SAFEMODE_THRESHOLD_PCT_DEFAULT
init|=
literal|1.5f
decl_stmt|;
DECL|field|BN_SAFEMODE_EXTENSION_DEFAULT
specifier|private
specifier|static
specifier|final
name|int
name|BN_SAFEMODE_EXTENSION_DEFAULT
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/** Name-node proxy */
DECL|field|namenode
name|NamenodeProtocol
name|namenode
decl_stmt|;
comment|/** Name-node RPC address */
DECL|field|nnRpcAddress
name|String
name|nnRpcAddress
decl_stmt|;
comment|/** Name-node HTTP address */
DECL|field|nnHttpAddress
name|URL
name|nnHttpAddress
decl_stmt|;
comment|/** Checkpoint manager */
DECL|field|checkpointManager
name|Checkpointer
name|checkpointManager
decl_stmt|;
DECL|method|BackupNode (Configuration conf, NamenodeRole role)
name|BackupNode
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NamenodeRole
name|role
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|role
argument_list|)
expr_stmt|;
block|}
comment|/////////////////////////////////////////////////////
comment|// Common NameNode methods implementation for backup node.
comment|/////////////////////////////////////////////////////
annotation|@
name|Override
comment|// NameNode
DECL|method|getRpcServerAddress (Configuration conf)
specifier|protected
name|InetSocketAddress
name|getRpcServerAddress
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
name|BN_ADDRESS_NAME_KEY
argument_list|,
name|BN_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getServiceRpcServerAddress (Configuration conf)
specifier|protected
name|InetSocketAddress
name|getServiceRpcServerAddress
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
name|BN_SERVICE_RPC_ADDRESS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|==
literal|null
operator|||
name|addr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// NameNode
DECL|method|setRpcServerAddress (Configuration conf, InetSocketAddress addr)
specifier|protected
name|void
name|setRpcServerAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|BN_ADDRESS_NAME_KEY
argument_list|,
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|addr
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// Namenode
DECL|method|setRpcServiceServerAddress (Configuration conf, InetSocketAddress addr)
specifier|protected
name|void
name|setRpcServiceServerAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|BN_SERVICE_RPC_ADDRESS_KEY
argument_list|,
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|addr
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// NameNode
DECL|method|getHttpServerAddress (Configuration conf)
specifier|protected
name|InetSocketAddress
name|getHttpServerAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
assert|assert
name|getNameNodeAddress
argument_list|()
operator|!=
literal|null
operator|:
literal|"rpcAddress should be calculated first"
assert|;
name|String
name|addr
init|=
name|conf
operator|.
name|get
argument_list|(
name|BN_HTTP_ADDRESS_NAME_KEY
argument_list|,
name|BN_HTTP_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// NameNode
DECL|method|loadNamesystem (Configuration conf)
specifier|protected
name|void
name|loadNamesystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|.
name|setFloat
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY
argument_list|,
name|BN_SAFEMODE_THRESHOLD_PCT_DEFAULT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_EXTENSION_KEY
argument_list|,
name|BN_SAFEMODE_EXTENSION_DEFAULT
argument_list|)
expr_stmt|;
name|BackupImage
name|bnImage
init|=
operator|new
name|BackupImage
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|namesystem
operator|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|bnImage
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|dir
operator|.
name|disableQuotaChecks
argument_list|()
expr_stmt|;
name|bnImage
operator|.
name|setNamesystem
argument_list|(
name|namesystem
argument_list|)
expr_stmt|;
name|bnImage
operator|.
name|recoverCreateRead
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
comment|// NameNode
DECL|method|initialize (Configuration conf)
specifier|protected
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Trash is disabled in BackupNameNode,
comment|// but should be turned back on if it ever becomes active.
name|conf
operator|.
name|setLong
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_TRASH_INTERVAL_KEY
argument_list|,
name|CommonConfigurationKeys
operator|.
name|FS_TRASH_INTERVAL_DEFAULT
argument_list|)
expr_stmt|;
name|NamespaceInfo
name|nsInfo
init|=
name|handshake
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|super
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|setBlockPoolId
argument_list|(
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
operator|==
name|namesystem
operator|.
name|isInSafeMode
argument_list|()
condition|)
block|{
name|namesystem
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
block|}
comment|// Backup node should never do lease recovery,
comment|// therefore lease hard limit should never expire.
name|namesystem
operator|.
name|leaseManager
operator|.
name|setLeasePeriod
argument_list|(
name|HdfsConstants
operator|.
name|LEASE_SOFTLIMIT_PERIOD
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
comment|// register with the active name-node
name|registerWith
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
comment|// Checkpoint daemon should start after the rpc server started
name|runCheckpointDaemon
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|getHttpAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|addr
operator|!=
literal|null
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|BN_HTTP_ADDRESS_NAME_KEY
argument_list|,
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|getHttpAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createRpcServer (Configuration conf)
specifier|protected
name|NameNodeRpcServer
name|createRpcServer
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BackupNodeRpcServer
argument_list|(
name|conf
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// NameNode
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|stop
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|stop (boolean reportError)
name|void
name|stop
parameter_list|(
name|boolean
name|reportError
parameter_list|)
block|{
if|if
condition|(
name|checkpointManager
operator|!=
literal|null
condition|)
block|{
comment|// Prevent from starting a new checkpoint.
comment|// Checkpoints that has already been started may proceed until
comment|// the error reporting to the name-node is complete.
comment|// Checkpoint manager should not be interrupted yet because it will
comment|// close storage file channels and the checkpoint may fail with
comment|// ClosedByInterruptException.
name|checkpointManager
operator|.
name|shouldRun
operator|=
literal|false
expr_stmt|;
block|}
comment|// reportError is a test hook to simulate backupnode crashing and not
comment|// doing a clean exit w.r.t active namenode
if|if
condition|(
name|reportError
operator|&&
name|namenode
operator|!=
literal|null
operator|&&
name|getRegistration
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// Exclude this node from the list of backup streams on the name-node
try|try
block|{
name|namenode
operator|.
name|errorReport
argument_list|(
name|getRegistration
argument_list|()
argument_list|,
name|NamenodeProtocol
operator|.
name|FATAL
argument_list|,
literal|"Shutting down."
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
literal|"Failed to report to name-node."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Stop the RPC client
if|if
condition|(
name|namenode
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|namenode
argument_list|)
expr_stmt|;
block|}
name|namenode
operator|=
literal|null
expr_stmt|;
comment|// Stop the checkpoint manager
if|if
condition|(
name|checkpointManager
operator|!=
literal|null
condition|)
block|{
name|checkpointManager
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|checkpointManager
operator|=
literal|null
expr_stmt|;
block|}
comment|// Abort current log segment - otherwise the NN shutdown code
comment|// will close it gracefully, which is incorrect.
name|getFSImage
argument_list|()
operator|.
name|getEditLog
argument_list|()
operator|.
name|abortCurrentLogSegment
argument_list|()
expr_stmt|;
comment|// Stop name-node threads
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/* @Override */
comment|// NameNode
DECL|method|setSafeMode (SafeModeAction action)
specifier|public
name|boolean
name|setSafeMode
parameter_list|(
name|SafeModeAction
name|action
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedActionException
argument_list|(
literal|"setSafeMode"
argument_list|)
throw|;
block|}
DECL|class|BackupNodeRpcServer
specifier|static
class|class
name|BackupNodeRpcServer
extends|extends
name|NameNodeRpcServer
implements|implements
name|JournalProtocol
block|{
DECL|method|BackupNodeRpcServer (Configuration conf, BackupNode nn)
specifier|private
name|BackupNodeRpcServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|BackupNode
name|nn
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|nn
argument_list|)
expr_stmt|;
name|JournalProtocolServerSideTranslatorPB
name|journalProtocolTranslator
init|=
operator|new
name|JournalProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|BlockingService
name|service
init|=
name|JournalProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
name|journalProtocolTranslator
argument_list|)
decl_stmt|;
name|DFSUtil
operator|.
name|addPBProtocol
argument_list|(
name|conf
argument_list|,
name|JournalProtocolPB
operator|.
name|class
argument_list|,
name|service
argument_list|,
name|this
operator|.
name|clientRpcServer
argument_list|)
expr_stmt|;
block|}
comment|/**       * Verifies a journal request      */
DECL|method|verifyJournalRequest (JournalInfo journalInfo)
specifier|private
name|void
name|verifyJournalRequest
parameter_list|(
name|JournalInfo
name|journalInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyLayoutVersion
argument_list|(
name|journalInfo
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|errorMsg
init|=
literal|null
decl_stmt|;
name|int
name|expectedNamespaceID
init|=
name|namesystem
operator|.
name|getNamespaceInfo
argument_list|()
operator|.
name|getNamespaceID
argument_list|()
decl_stmt|;
if|if
condition|(
name|journalInfo
operator|.
name|getNamespaceId
argument_list|()
operator|!=
name|expectedNamespaceID
condition|)
block|{
name|errorMsg
operator|=
literal|"Invalid namespaceID in journal request - expected "
operator|+
name|expectedNamespaceID
operator|+
literal|" actual "
operator|+
name|journalInfo
operator|.
name|getNamespaceId
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|errorMsg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|UnregisteredNodeException
argument_list|(
name|journalInfo
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|journalInfo
operator|.
name|getClusterId
argument_list|()
operator|.
name|equals
argument_list|(
name|namesystem
operator|.
name|getClusterId
argument_list|()
argument_list|)
condition|)
block|{
name|errorMsg
operator|=
literal|"Invalid clusterId in journal request - expected "
operator|+
name|journalInfo
operator|.
name|getClusterId
argument_list|()
operator|+
literal|" actual "
operator|+
name|namesystem
operator|.
name|getClusterId
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|errorMsg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|UnregisteredNodeException
argument_list|(
name|journalInfo
argument_list|)
throw|;
block|}
block|}
comment|/////////////////////////////////////////////////////
comment|// JournalProtocol implementation for backup node.
comment|/////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|startLogSegment (JournalInfo journalInfo, long epoch, long txid)
specifier|public
name|void
name|startLogSegment
parameter_list|(
name|JournalInfo
name|journalInfo
parameter_list|,
name|long
name|epoch
parameter_list|,
name|long
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|namesystem
operator|.
name|checkOperation
argument_list|(
name|OperationCategory
operator|.
name|JOURNAL
argument_list|)
expr_stmt|;
name|verifyJournalRequest
argument_list|(
name|journalInfo
argument_list|)
expr_stmt|;
name|getBNImage
argument_list|()
operator|.
name|namenodeStartedLogSegment
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|journal (JournalInfo journalInfo, long epoch, long firstTxId, int numTxns, byte[] records)
specifier|public
name|void
name|journal
parameter_list|(
name|JournalInfo
name|journalInfo
parameter_list|,
name|long
name|epoch
parameter_list|,
name|long
name|firstTxId
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
name|namesystem
operator|.
name|checkOperation
argument_list|(
name|OperationCategory
operator|.
name|JOURNAL
argument_list|)
expr_stmt|;
name|verifyJournalRequest
argument_list|(
name|journalInfo
argument_list|)
expr_stmt|;
name|getBNImage
argument_list|()
operator|.
name|journal
argument_list|(
name|firstTxId
argument_list|,
name|numTxns
argument_list|,
name|records
argument_list|)
expr_stmt|;
block|}
DECL|method|getBNImage ()
specifier|private
name|BackupImage
name|getBNImage
parameter_list|()
block|{
return|return
operator|(
name|BackupImage
operator|)
name|nn
operator|.
name|getFSImage
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fence (JournalInfo journalInfo, long epoch, String fencerInfo)
specifier|public
name|FenceResponse
name|fence
parameter_list|(
name|JournalInfo
name|journalInfo
parameter_list|,
name|long
name|epoch
parameter_list|,
name|String
name|fencerInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Fenced by "
operator|+
name|fencerInfo
operator|+
literal|" with epoch "
operator|+
name|epoch
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"BackupNode does not support fence"
argument_list|)
throw|;
block|}
block|}
comment|//////////////////////////////////////////////////////
DECL|method|shouldCheckpointAtStartup ()
name|boolean
name|shouldCheckpointAtStartup
parameter_list|()
block|{
name|FSImage
name|fsImage
init|=
name|getFSImage
argument_list|()
decl_stmt|;
if|if
condition|(
name|isRole
argument_list|(
name|NamenodeRole
operator|.
name|CHECKPOINT
argument_list|)
condition|)
block|{
assert|assert
name|fsImage
operator|.
name|getStorage
argument_list|()
operator|.
name|getNumStorageDirs
argument_list|()
operator|>
literal|0
assert|;
return|return
operator|!
name|fsImage
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|)
operator|.
name|getVersionFile
argument_list|()
operator|.
name|exists
argument_list|()
return|;
block|}
comment|// BN always checkpoints on startup in order to get in sync with namespace
return|return
literal|true
return|;
block|}
DECL|method|handshake (Configuration conf)
specifier|private
name|NamespaceInfo
name|handshake
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// connect to name node
name|InetSocketAddress
name|nnAddress
init|=
name|NameNode
operator|.
name|getServiceAddress
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|this
operator|.
name|namenode
operator|=
name|NameNodeProxies
operator|.
name|createNonHAProxy
argument_list|(
name|conf
argument_list|,
name|nnAddress
argument_list|,
name|NamenodeProtocol
operator|.
name|class
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|getProxy
argument_list|()
expr_stmt|;
name|this
operator|.
name|nnRpcAddress
operator|=
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|nnAddress
argument_list|)
expr_stmt|;
name|this
operator|.
name|nnHttpAddress
operator|=
name|DFSUtil
operator|.
name|getInfoServer
argument_list|(
name|nnAddress
argument_list|,
name|conf
argument_list|,
name|DFSUtil
operator|.
name|getHttpClientScheme
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|toURL
argument_list|()
expr_stmt|;
comment|// get version and id info from the name-node
name|NamespaceInfo
name|nsInfo
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|isStopRequested
argument_list|()
condition|)
block|{
try|try
block|{
name|nsInfo
operator|=
name|handshake
argument_list|(
name|namenode
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|e
parameter_list|)
block|{
comment|// name-node is busy
name|LOG
operator|.
name|info
argument_list|(
literal|"Problem connecting to server: "
operator|+
name|nnAddress
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Encountered exception "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|nsInfo
return|;
block|}
comment|/**    * Start a backup node daemon.    */
DECL|method|runCheckpointDaemon (Configuration conf)
specifier|private
name|void
name|runCheckpointDaemon
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|checkpointManager
operator|=
operator|new
name|Checkpointer
argument_list|(
name|conf
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|checkpointManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Checkpoint.<br>    * Tests may use it to initiate a checkpoint process.    * @throws IOException    */
DECL|method|doCheckpoint ()
name|void
name|doCheckpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|checkpointManager
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
block|}
comment|/**    * Register this backup node with the active name-node.    * @param nsInfo namespace information    * @throws IOException    */
DECL|method|registerWith (NamespaceInfo nsInfo)
specifier|private
name|void
name|registerWith
parameter_list|(
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|BackupImage
name|bnImage
init|=
operator|(
name|BackupImage
operator|)
name|getFSImage
argument_list|()
decl_stmt|;
name|NNStorage
name|storage
init|=
name|bnImage
operator|.
name|getStorage
argument_list|()
decl_stmt|;
comment|// verify namespaceID
if|if
condition|(
name|storage
operator|.
name|getNamespaceID
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// new backup storage
name|storage
operator|.
name|setStorageInfo
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
name|storage
operator|.
name|setBlockPoolID
argument_list|(
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
argument_list|)
expr_stmt|;
name|storage
operator|.
name|setClusterID
argument_list|(
name|nsInfo
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nsInfo
operator|.
name|validateStorage
argument_list|(
name|storage
argument_list|)
expr_stmt|;
block|}
name|bnImage
operator|.
name|initEditLog
argument_list|(
name|StartupOption
operator|.
name|REGULAR
argument_list|)
expr_stmt|;
name|setRegistration
argument_list|()
expr_stmt|;
name|NamenodeRegistration
name|nnReg
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|isStopRequested
argument_list|()
condition|)
block|{
try|try
block|{
name|nnReg
operator|=
name|namenode
operator|.
name|registerSubordinateNamenode
argument_list|(
name|getRegistration
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|e
parameter_list|)
block|{
comment|// name-node is busy
name|LOG
operator|.
name|info
argument_list|(
literal|"Problem connecting to name-node: "
operator|+
name|nnRpcAddress
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Encountered exception "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|msg
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nnReg
operator|==
literal|null
condition|)
comment|// consider as a rejection
name|msg
operator|=
literal|"Registration rejected by "
operator|+
name|nnRpcAddress
expr_stmt|;
elseif|else
if|if
condition|(
operator|!
name|nnReg
operator|.
name|isRole
argument_list|(
name|NamenodeRole
operator|.
name|NAMENODE
argument_list|)
condition|)
block|{
name|msg
operator|=
literal|"Name-node "
operator|+
name|nnRpcAddress
operator|+
literal|" is not active"
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|msg
operator|+=
literal|". Shutting down."
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
comment|// stop the node
block|}
name|nnRpcAddress
operator|=
name|nnReg
operator|.
name|getAddress
argument_list|()
expr_stmt|;
block|}
comment|// TODO: move to a common with DataNode util class
DECL|method|handshake (NamenodeProtocol namenode)
specifier|private
specifier|static
name|NamespaceInfo
name|handshake
parameter_list|(
name|NamenodeProtocol
name|namenode
parameter_list|)
throws|throws
name|IOException
throws|,
name|SocketTimeoutException
block|{
name|NamespaceInfo
name|nsInfo
decl_stmt|;
name|nsInfo
operator|=
name|namenode
operator|.
name|versionRequest
argument_list|()
expr_stmt|;
comment|// throws SocketTimeoutException
name|String
name|errorMsg
init|=
literal|null
decl_stmt|;
comment|// verify build version
if|if
condition|(
operator|!
name|nsInfo
operator|.
name|getBuildVersion
argument_list|()
operator|.
name|equals
argument_list|(
name|Storage
operator|.
name|getBuildVersion
argument_list|()
argument_list|)
condition|)
block|{
name|errorMsg
operator|=
literal|"Incompatible build versions: active name-node BV = "
operator|+
name|nsInfo
operator|.
name|getBuildVersion
argument_list|()
operator|+
literal|"; backup node BV = "
operator|+
name|Storage
operator|.
name|getBuildVersion
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|fatal
argument_list|(
name|errorMsg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|errorMsg
argument_list|)
throw|;
block|}
assert|assert
name|HdfsConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
operator|==
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|:
literal|"Active and backup node layout versions must be the same. Expected: "
operator|+
name|HdfsConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
operator|+
literal|" actual "
operator|+
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
assert|;
return|return
name|nsInfo
return|;
block|}
annotation|@
name|Override
DECL|method|getNameServiceId (Configuration conf)
specifier|protected
name|String
name|getNameServiceId
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|DFSUtil
operator|.
name|getBackupNameServiceId
argument_list|(
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createHAState (StartupOption startOpt)
specifier|protected
name|HAState
name|createHAState
parameter_list|(
name|StartupOption
name|startOpt
parameter_list|)
block|{
return|return
operator|new
name|BackupState
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// NameNode
DECL|method|createHAContext ()
specifier|protected
name|NameNodeHAContext
name|createHAContext
parameter_list|()
block|{
return|return
operator|new
name|BNHAContext
argument_list|()
return|;
block|}
DECL|class|BNHAContext
specifier|private
class|class
name|BNHAContext
extends|extends
name|NameNodeHAContext
block|{
annotation|@
name|Override
comment|// NameNodeHAContext
DECL|method|checkOperation (OperationCategory op)
specifier|public
name|void
name|checkOperation
parameter_list|(
name|OperationCategory
name|op
parameter_list|)
throws|throws
name|StandbyException
block|{
if|if
condition|(
name|op
operator|==
name|OperationCategory
operator|.
name|UNCHECKED
operator|||
name|op
operator|==
name|OperationCategory
operator|.
name|CHECKPOINT
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|OperationCategory
operator|.
name|JOURNAL
operator|!=
name|op
operator|&&
operator|!
operator|(
name|OperationCategory
operator|.
name|READ
operator|==
name|op
operator|&&
operator|!
name|isRole
argument_list|(
name|NamenodeRole
operator|.
name|CHECKPOINT
argument_list|)
operator|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Operation category "
operator|+
name|op
operator|+
literal|" is not supported at "
operator|+
name|getRole
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|StandbyException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
comment|// NameNodeHAContext
DECL|method|prepareToStopStandbyServices ()
specifier|public
name|void
name|prepareToStopStandbyServices
parameter_list|()
throws|throws
name|ServiceFailedException
block|{     }
comment|/**      * Start services for BackupNode.      *<p>      * The following services should be muted      * (not run or not pass any control commands to DataNodes)      * on BackupNode:      * {@link LeaseManager.Monitor} protected by SafeMode.      * {@link BlockManager.ReplicationMonitor} protected by SafeMode.      * {@link HeartbeatManager.Monitor} protected by SafeMode.      * {@link DecommissionManager.Monitor} need to prohibit refreshNodes().      * {@link PendingReplicationBlocks.PendingReplicationMonitor} harmless,      * because ReplicationMonitor is muted.      */
annotation|@
name|Override
DECL|method|startActiveServices ()
specifier|public
name|void
name|startActiveServices
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|namesystem
operator|.
name|startActiveServices
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|doImmediateShutdown
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|stopActiveServices ()
specifier|public
name|void
name|stopActiveServices
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|namesystem
operator|!=
literal|null
condition|)
block|{
name|namesystem
operator|.
name|stopActiveServices
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|doImmediateShutdown
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

