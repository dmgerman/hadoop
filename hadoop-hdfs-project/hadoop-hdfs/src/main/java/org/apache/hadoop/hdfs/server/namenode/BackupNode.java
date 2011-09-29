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
name|protocol
operator|.
name|DatanodeInfo
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
name|BlocksWithLocations
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
name|NamenodeCommand
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
name|String
name|nnHttpAddress
decl_stmt|;
comment|/** Checkpoint manager */
DECL|field|checkpointManager
name|Checkpointer
name|checkpointManager
decl_stmt|;
comment|/** ClusterID to which BackupNode belongs to */
DECL|field|clusterId
name|String
name|clusterId
decl_stmt|;
comment|/** Block pool Id of the peer namenode of this BackupNode */
DECL|field|blockPoolId
name|String
name|blockPoolId
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
throws|throws
name|IOException
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
throws|throws
name|IOException
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
DECL|method|setHttpServerAddress (Configuration conf)
specifier|protected
name|void
name|setHttpServerAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|BN_HTTP_ADDRESS_NAME_KEY
argument_list|,
name|getHostPortString
argument_list|(
name|getHttpAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|clusterId
operator|=
name|nsInfo
operator|.
name|getClusterID
argument_list|()
expr_stmt|;
name|blockPoolId
operator|=
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
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
if|if
condition|(
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
name|RPC
operator|.
name|stopProxy
argument_list|(
name|namenode
argument_list|)
expr_stmt|;
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
comment|// Stop name-node threads
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
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
DECL|field|nnRpcAddress
specifier|private
specifier|final
name|String
name|nnRpcAddress
decl_stmt|;
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
name|this
operator|.
name|server
operator|.
name|addProtocol
argument_list|(
name|JournalProtocol
operator|.
name|class
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|nnRpcAddress
operator|=
name|nn
operator|.
name|nnRpcAddress
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|protocol
operator|.
name|equals
argument_list|(
name|JournalProtocol
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|JournalProtocol
operator|.
name|versionID
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getProtocolVersion
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|)
return|;
block|}
block|}
comment|/////////////////////////////////////////////////////
comment|// BackupNodeProtocol implementation for backup node.
comment|/////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|startLogSegment (NamenodeRegistration registration, long txid)
specifier|public
name|void
name|startLogSegment
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|,
name|long
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|nn
operator|.
name|checkOperation
argument_list|(
name|OperationCategory
operator|.
name|JOURNAL
argument_list|)
expr_stmt|;
name|verifyRequest
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|verifyRequest
argument_list|(
name|registration
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
DECL|method|journal (NamenodeRegistration nnReg, long firstTxId, int numTxns, byte[] records)
specifier|public
name|void
name|journal
parameter_list|(
name|NamenodeRegistration
name|nnReg
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
name|nn
operator|.
name|checkOperation
argument_list|(
name|OperationCategory
operator|.
name|JOURNAL
argument_list|)
expr_stmt|;
name|verifyRequest
argument_list|(
name|nnReg
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|nnRpcAddress
operator|.
name|equals
argument_list|(
name|nnReg
operator|.
name|getAddress
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Journal request from unexpected name-node: "
operator|+
name|nnReg
operator|.
name|getAddress
argument_list|()
operator|+
literal|" expecting "
operator|+
name|nnRpcAddress
argument_list|)
throw|;
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
operator|(
name|NamenodeProtocol
operator|)
name|RPC
operator|.
name|waitForProxy
argument_list|(
name|NamenodeProtocol
operator|.
name|class
argument_list|,
name|NamenodeProtocol
operator|.
name|versionID
argument_list|,
name|nnAddress
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|nnRpcAddress
operator|=
name|getHostPortString
argument_list|(
name|nnAddress
argument_list|)
expr_stmt|;
name|this
operator|.
name|nnHttpAddress
operator|=
name|getHostPortString
argument_list|(
name|super
operator|.
name|getHttpServerAddress
argument_list|(
name|conf
argument_list|)
argument_list|)
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
block|{}
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
comment|/**    * Register this backup node with the active name-node.    * @param nsInfo    * @throws IOException    */
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
name|register
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
block|{}
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
name|LAYOUT_VERSION
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
name|LAYOUT_VERSION
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
DECL|method|getBlockPoolId ()
name|String
name|getBlockPoolId
parameter_list|()
block|{
return|return
name|blockPoolId
return|;
block|}
DECL|method|getClusterId ()
name|String
name|getClusterId
parameter_list|()
block|{
return|return
name|clusterId
return|;
block|}
annotation|@
name|Override
comment|// NameNode
DECL|method|checkOperation (OperationCategory op)
specifier|protected
name|void
name|checkOperation
parameter_list|(
name|OperationCategory
name|op
parameter_list|)
throws|throws
name|UnsupportedActionException
block|{
if|if
condition|(
name|OperationCategory
operator|.
name|JOURNAL
operator|!=
name|op
condition|)
block|{
name|String
name|msg
init|=
literal|"Operation category "
operator|+
name|op
operator|+
literal|" is not supported at the BackupNode"
decl_stmt|;
throw|throw
operator|new
name|UnsupportedActionException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

