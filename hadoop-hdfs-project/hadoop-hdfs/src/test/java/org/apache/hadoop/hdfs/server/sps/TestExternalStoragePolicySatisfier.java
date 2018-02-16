begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.sps
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
name|sps
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SASL_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_KERBEROS_PRINCIPAL_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_KEYTAB_FILE_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_HTTP_POLICY_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KEYTAB_FILE_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_SPS_ADDRESS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_SPS_KERBEROS_PRINCIPAL_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_SPS_KEYTAB_FILE_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_SPS_MAX_OUTSTANDING_PATHS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_DATA_TRANSFER_PROTECTION_KEY
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Properties
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
name|fs
operator|.
name|Path
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
name|StorageType
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
name|DFSTestUtil
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
name|DistributedFileSystem
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
name|MiniDFSCluster
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
name|Block
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
name|StoragePolicySatisfierMode
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
name|balancer
operator|.
name|NameNodeConnector
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
name|sps
operator|.
name|BlockMovementListener
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
name|sps
operator|.
name|BlockStorageMovementAttemptedItems
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
name|sps
operator|.
name|StoragePolicySatisfier
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
name|sps
operator|.
name|TestStoragePolicySatisfier
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
name|http
operator|.
name|HttpConfig
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
name|minikdc
operator|.
name|MiniKdc
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
name|SecurityUtil
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authentication
operator|.
name|util
operator|.
name|KerberosName
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
name|ssl
operator|.
name|KeyStoreTestUtil
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Tests the external sps service plugins.  */
end_comment

begin_class
DECL|class|TestExternalStoragePolicySatisfier
specifier|public
class|class
name|TestExternalStoragePolicySatisfier
extends|extends
name|TestStoragePolicySatisfier
block|{
DECL|field|allDiskTypes
specifier|private
name|StorageType
index|[]
index|[]
name|allDiskTypes
init|=
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
decl_stmt|;
DECL|field|nnc
specifier|private
name|NameNodeConnector
name|nnc
decl_stmt|;
DECL|field|keytabFile
specifier|private
name|File
name|keytabFile
decl_stmt|;
DECL|field|principal
specifier|private
name|String
name|principal
decl_stmt|;
DECL|field|kdc
specifier|private
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|baseDir
specifier|private
name|File
name|baseDir
decl_stmt|;
DECL|field|externalSps
specifier|private
name|StoragePolicySatisfier
argument_list|<
name|String
argument_list|>
name|externalSps
decl_stmt|;
DECL|field|externalCtxt
specifier|private
name|ExternalSPSContext
name|externalCtxt
decl_stmt|;
annotation|@
name|After
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|kdc
operator|!=
literal|null
condition|)
block|{
name|kdc
operator|.
name|stop
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|shutdownCluster ()
specifier|public
name|void
name|shutdownCluster
parameter_list|()
block|{
if|if
condition|(
name|externalSps
operator|!=
literal|null
condition|)
block|{
name|externalSps
operator|.
name|stopGracefully
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|shutdownCluster
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|getConf
argument_list|()
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_MODE_KEY
argument_list|,
name|StoragePolicySatisfierMode
operator|.
name|EXTERNAL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createCluster ()
specifier|public
name|void
name|createCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|getConf
argument_list|()
operator|.
name|setLong
argument_list|(
literal|"dfs.block.size"
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|setCluster
argument_list|(
name|startCluster
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|allDiskTypes
argument_list|,
name|NUM_OF_DATANODES
argument_list|,
name|STORAGES_PER_DATANODE
argument_list|,
name|CAPACITY
argument_list|)
argument_list|)
expr_stmt|;
name|getFS
argument_list|()
expr_stmt|;
name|writeContent
argument_list|(
name|FILE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startCluster (final Configuration conf, StorageType[][] storageTypes, int numberOfDatanodes, int storagesPerDn, long nodeCapacity)
specifier|public
name|MiniDFSCluster
name|startCluster
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
name|StorageType
index|[]
index|[]
name|storageTypes
parameter_list|,
name|int
name|numberOfDatanodes
parameter_list|,
name|int
name|storagesPerDn
parameter_list|,
name|long
name|nodeCapacity
parameter_list|)
throws|throws
name|IOException
block|{
name|long
index|[]
index|[]
name|capacities
init|=
operator|new
name|long
index|[
name|numberOfDatanodes
index|]
index|[
name|storagesPerDn
index|]
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
name|numberOfDatanodes
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|storagesPerDn
condition|;
name|j
operator|++
control|)
block|{
name|capacities
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|nodeCapacity
expr_stmt|;
block|}
block|}
specifier|final
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numberOfDatanodes
argument_list|)
operator|.
name|storagesPerDatanode
argument_list|(
name|storagesPerDn
argument_list|)
operator|.
name|storageTypes
argument_list|(
name|storageTypes
argument_list|)
operator|.
name|storageCapacities
argument_list|(
name|capacities
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|nnc
operator|=
name|getNameNodeConnector
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|externalSps
operator|=
operator|new
name|StoragePolicySatisfier
argument_list|<
name|String
argument_list|>
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|externalCtxt
operator|=
operator|new
name|ExternalSPSContext
argument_list|(
name|externalSps
argument_list|,
name|getNameNodeConnector
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|ExternalBlockMovementListener
name|blkMoveListener
init|=
operator|new
name|ExternalBlockMovementListener
argument_list|()
decl_stmt|;
name|ExternalSPSBlockMoveTaskHandler
name|externalHandler
init|=
operator|new
name|ExternalSPSBlockMoveTaskHandler
argument_list|(
name|conf
argument_list|,
name|nnc
argument_list|,
name|externalSps
argument_list|)
decl_stmt|;
name|externalHandler
operator|.
name|init
argument_list|()
expr_stmt|;
name|externalSps
operator|.
name|init
argument_list|(
name|externalCtxt
argument_list|,
operator|new
name|ExternalSPSFilePathCollector
argument_list|(
name|externalSps
argument_list|)
argument_list|,
name|externalHandler
argument_list|,
name|blkMoveListener
argument_list|)
expr_stmt|;
name|externalSps
operator|.
name|start
argument_list|(
literal|true
argument_list|,
name|StoragePolicySatisfierMode
operator|.
name|EXTERNAL
argument_list|)
expr_stmt|;
return|return
name|cluster
return|;
block|}
DECL|method|restartNamenode ()
specifier|public
name|void
name|restartNamenode
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|externalSps
operator|!=
literal|null
condition|)
block|{
name|externalSps
operator|.
name|stopGracefully
argument_list|()
expr_stmt|;
block|}
name|getCluster
argument_list|()
operator|.
name|restartNameNodes
argument_list|()
expr_stmt|;
name|getCluster
argument_list|()
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|externalSps
operator|=
operator|new
name|StoragePolicySatisfier
argument_list|<>
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|externalCtxt
operator|=
operator|new
name|ExternalSPSContext
argument_list|(
name|externalSps
argument_list|,
name|getNameNodeConnector
argument_list|(
name|getConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ExternalBlockMovementListener
name|blkMoveListener
init|=
operator|new
name|ExternalBlockMovementListener
argument_list|()
decl_stmt|;
name|ExternalSPSBlockMoveTaskHandler
name|externalHandler
init|=
operator|new
name|ExternalSPSBlockMoveTaskHandler
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|nnc
argument_list|,
name|externalSps
argument_list|)
decl_stmt|;
name|externalHandler
operator|.
name|init
argument_list|()
expr_stmt|;
name|externalSps
operator|.
name|init
argument_list|(
name|externalCtxt
argument_list|,
operator|new
name|ExternalSPSFilePathCollector
argument_list|(
name|externalSps
argument_list|)
argument_list|,
name|externalHandler
argument_list|,
name|blkMoveListener
argument_list|)
expr_stmt|;
name|externalSps
operator|.
name|start
argument_list|(
literal|true
argument_list|,
name|StoragePolicySatisfierMode
operator|.
name|EXTERNAL
argument_list|)
expr_stmt|;
block|}
DECL|class|ExternalBlockMovementListener
specifier|private
class|class
name|ExternalBlockMovementListener
implements|implements
name|BlockMovementListener
block|{
DECL|field|actualBlockMovements
specifier|private
name|List
argument_list|<
name|Block
argument_list|>
name|actualBlockMovements
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|notifyMovementTriedBlocks (Block[] moveAttemptFinishedBlks)
specifier|public
name|void
name|notifyMovementTriedBlocks
parameter_list|(
name|Block
index|[]
name|moveAttemptFinishedBlks
parameter_list|)
block|{
for|for
control|(
name|Block
name|block
range|:
name|moveAttemptFinishedBlks
control|)
block|{
name|actualBlockMovements
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Movement attempted blocks"
argument_list|,
name|actualBlockMovements
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getNameNodeConnector (Configuration conf)
specifier|private
name|NameNodeConnector
name|getNameNodeConnector
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Collection
argument_list|<
name|URI
argument_list|>
name|namenodes
init|=
name|DFSUtil
operator|.
name|getInternalNsRpcUris
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|namenodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|externalSPSPathId
init|=
name|HdfsServerConstants
operator|.
name|MOVER_ID_PATH
decl_stmt|;
name|NameNodeConnector
operator|.
name|checkOtherInstanceRunning
argument_list|(
literal|false
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|NameNodeConnector
argument_list|>
name|nncs
init|=
name|NameNodeConnector
operator|.
name|newNameNodeConnectors
argument_list|(
name|namenodes
argument_list|,
name|StoragePolicySatisfier
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|externalSPSPathId
argument_list|,
name|conf
argument_list|,
name|NameNodeConnector
operator|.
name|DEFAULT_MAX_IDLE_ITERATIONS
argument_list|)
decl_stmt|;
return|return
name|nncs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
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
literal|"Failed to connect with namenode"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// Ignore
block|}
block|}
block|}
DECL|method|waitForAttemptedItems (long expectedBlkMovAttemptedCount, int timeout)
specifier|public
name|void
name|waitForAttemptedItems
parameter_list|(
name|long
name|expectedBlkMovAttemptedCount
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"expectedAttemptedItemsCount={} actualAttemptedItemsCount={}"
argument_list|,
name|expectedBlkMovAttemptedCount
argument_list|,
operator|(
call|(
name|BlockStorageMovementAttemptedItems
argument_list|<
name|String
argument_list|>
call|)
argument_list|(
name|externalSps
operator|.
name|getAttemptedItemsMonitor
argument_list|()
argument_list|)
operator|)
operator|.
name|getAttemptedItemsCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
call|(
name|BlockStorageMovementAttemptedItems
argument_list|<
name|String
argument_list|>
call|)
argument_list|(
name|externalSps
operator|.
name|getAttemptedItemsMonitor
argument_list|()
argument_list|)
operator|)
operator|.
name|getAttemptedItemsCount
argument_list|()
operator|==
name|expectedBlkMovAttemptedCount
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForBlocksMovementAttemptReport ( long expectedMovementFinishedBlocksCount, int timeout)
specifier|public
name|void
name|waitForBlocksMovementAttemptReport
parameter_list|(
name|long
name|expectedMovementFinishedBlocksCount
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MovementFinishedBlocks: expectedCount={} actualCount={}"
argument_list|,
name|expectedMovementFinishedBlocksCount
argument_list|,
operator|(
call|(
name|BlockStorageMovementAttemptedItems
argument_list|<
name|String
argument_list|>
call|)
argument_list|(
name|externalSps
operator|.
name|getAttemptedItemsMonitor
argument_list|()
argument_list|)
operator|)
operator|.
name|getMovementFinishedBlocksCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
call|(
name|BlockStorageMovementAttemptedItems
argument_list|<
name|String
argument_list|>
call|)
argument_list|(
name|externalSps
operator|.
name|getAttemptedItemsMonitor
argument_list|()
argument_list|)
operator|)
operator|.
name|getMovementFinishedBlocksCount
argument_list|()
operator|>=
name|expectedMovementFinishedBlocksCount
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
DECL|method|initSecureConf (Configuration conf)
specifier|private
name|void
name|initSecureConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|username
init|=
literal|"externalSPS"
decl_stmt|;
name|baseDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestExternalStoragePolicySatisfier
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|baseDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|Properties
name|kdcConf
init|=
name|MiniKdc
operator|.
name|createConf
argument_list|()
decl_stmt|;
name|kdc
operator|=
operator|new
name|MiniKdc
argument_list|(
name|kdcConf
argument_list|,
name|baseDir
argument_list|)
expr_stmt|;
name|kdc
operator|.
name|start
argument_list|()
expr_stmt|;
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|KERBEROS
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|KerberosName
operator|.
name|resetDefaultRealm
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected configuration to enable security"
argument_list|,
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|keytabFile
operator|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|username
operator|+
literal|".keytab"
argument_list|)
expr_stmt|;
name|String
name|keytab
init|=
name|keytabFile
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
comment|// Windows will not reverse name lookup "127.0.0.1" to "localhost".
name|String
name|krbInstance
init|=
name|Path
operator|.
name|WINDOWS
condition|?
literal|"127.0.0.1"
else|:
literal|"localhost"
decl_stmt|;
name|principal
operator|=
name|username
operator|+
literal|"/"
operator|+
name|krbInstance
operator|+
literal|"@"
operator|+
name|kdc
operator|.
name|getRealm
argument_list|()
expr_stmt|;
name|String
name|spnegoPrincipal
init|=
literal|"HTTP/"
operator|+
name|krbInstance
operator|+
literal|"@"
operator|+
name|kdc
operator|.
name|getRealm
argument_list|()
decl_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|keytabFile
argument_list|,
name|username
argument_list|,
name|username
operator|+
literal|"/"
operator|+
name|krbInstance
argument_list|,
literal|"HTTP/"
operator|+
name|krbInstance
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|principal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|principal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_KEYTAB_FILE_KEY
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|spnegoPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|,
literal|"authentication"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_HTTP_POLICY_KEY
argument_list|,
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTPS_ONLY
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SASL_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_SPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_SPS_KEYTAB_FILE_KEY
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_SPS_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|principal
argument_list|)
expr_stmt|;
name|String
name|keystoresDir
init|=
name|baseDir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|sslConfDir
init|=
name|KeyStoreTestUtil
operator|.
name|getClasspathDir
argument_list|(
name|TestExternalStoragePolicySatisfier
operator|.
name|class
argument_list|)
decl_stmt|;
name|KeyStoreTestUtil
operator|.
name|setupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY
argument_list|,
name|KeyStoreTestUtil
operator|.
name|getClientSSLConfigFileName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY
argument_list|,
name|KeyStoreTestUtil
operator|.
name|getServerSSLConfigFileName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test SPS runs fine when logging in with a keytab in kerberized env. Reusing    * testWhenStoragePolicySetToALLSSD here for basic functionality testing.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testWithKeytabs ()
specifier|public
name|void
name|testWithKeytabs
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|initSecureConf
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|loginUserFromKeytabAndReturnUGI
argument_list|(
name|principal
argument_list|,
name|keytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verify that sps runs Ok.
name|testWhenStoragePolicySetToALLSSD
argument_list|()
expr_stmt|;
comment|// verify that UGI was logged in using keytab.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|UserGroupInformation
operator|.
name|isLoginKeytabBased
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Reset UGI so that other tests are not affected.
name|UserGroupInformation
operator|.
name|reset
argument_list|()
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test verifies that SPS call will throw exception if the call Q exceeds    * OutstandingQueueLimit value.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testOutstandingQueueLimitExceeds ()
specifier|public
name|void
name|testOutstandingQueueLimitExceeds
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|getConf
argument_list|()
operator|.
name|setInt
argument_list|(
name|DFS_SPS_MAX_OUTSTANDING_PATHS_KEY
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|createCluster
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|FILE
argument_list|)
expr_stmt|;
name|DistributedFileSystem
name|fs
init|=
name|getFS
argument_list|()
decl_stmt|;
comment|// Creates 4 more files. Send all of them for satisfying the storage
comment|// policy together.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|String
name|file1
init|=
literal|"/testOutstandingQueueLimitExceeds_"
operator|+
name|i
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|file1
argument_list|)
expr_stmt|;
name|writeContent
argument_list|(
name|file1
argument_list|)
expr_stmt|;
name|fs
operator|.
name|satisfyStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|fileExceeds
init|=
literal|"/testOutstandingQueueLimitExceeds_"
operator|+
literal|4
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|fileExceeds
argument_list|)
expr_stmt|;
name|writeContent
argument_list|(
name|fileExceeds
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|satisfyStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|fileExceeds
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should throw exception as it exceeds "
operator|+
literal|"outstanding SPS call Q limit"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Outstanding satisfier queue limit: 3 exceeded, try later!"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|shutdownCluster
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test verifies status check when Satisfier is not running inside namenode.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStoragePolicySatisfyPathStatus ()
specifier|public
name|void
name|testStoragePolicySatisfyPathStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|createCluster
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|fs
init|=
name|getFS
argument_list|()
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|checkStoragePolicySatisfyPathStatus
argument_list|(
name|FILE
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should throw exception as SPS is not running inside NN!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Satisfier is not running"
operator|+
literal|" inside namenode, so status can't be returned."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests to verify that SPS should be able to start when the Mover ID file    * is not being hold by a Mover. This can be the case when Mover exits    * ungracefully without deleting the ID file from HDFS.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testWhenMoverExitsWithoutDeleteMoverIDFile ()
specifier|public
name|void
name|testWhenMoverExitsWithoutDeleteMoverIDFile
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|createCluster
argument_list|()
expr_stmt|;
comment|// Simulate the case by creating MOVER_ID file
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|getCluster
argument_list|()
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|HdfsServerConstants
operator|.
name|MOVER_ID_PATH
argument_list|,
literal|0
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|restartNamenode
argument_list|()
expr_stmt|;
name|boolean
name|running
init|=
name|externalCtxt
operator|.
name|isRunning
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"SPS should be running as "
operator|+
literal|"no Mover really running"
argument_list|,
name|running
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|shutdownCluster
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * This test need not run as external scan is not a batch based scanning right    * now.    */
annotation|@
name|Ignore
argument_list|(
literal|"ExternalFileIdCollector is not batch based right now."
operator|+
literal|" So, ignoring it."
argument_list|)
DECL|method|testBatchProcessingForSPSDirectory ()
specifier|public
name|void
name|testBatchProcessingForSPSDirectory
parameter_list|()
throws|throws
name|Exception
block|{   }
comment|/**    * This test case is more specific to internal.    */
annotation|@
name|Ignore
argument_list|(
literal|"This test is specific to internal, so skipping here."
argument_list|)
DECL|method|testWhenMoverIsAlreadyRunningBeforeStoragePolicySatisfier ()
specifier|public
name|void
name|testWhenMoverIsAlreadyRunningBeforeStoragePolicySatisfier
parameter_list|()
throws|throws
name|Exception
block|{   }
comment|/**    * Status won't be supported for external SPS, now. So, ignoring it.    */
annotation|@
name|Ignore
argument_list|(
literal|"Status is not supported for external SPS. So, ignoring it."
argument_list|)
DECL|method|testMaxRetryForFailedBlock ()
specifier|public
name|void
name|testMaxRetryForFailedBlock
parameter_list|()
throws|throws
name|Exception
block|{   }
comment|/**    * This test is specific to internal SPS. So, ignoring it.    */
annotation|@
name|Ignore
argument_list|(
literal|"This test is specific to internal SPS. So, ignoring it."
argument_list|)
annotation|@
name|Override
DECL|method|testTraverseWhenParentDeleted ()
specifier|public
name|void
name|testTraverseWhenParentDeleted
parameter_list|()
throws|throws
name|Exception
block|{   }
comment|/**    * This test is specific to internal SPS. So, ignoring it.    */
annotation|@
name|Ignore
argument_list|(
literal|"This test is specific to internal SPS. So, ignoring it."
argument_list|)
annotation|@
name|Override
DECL|method|testTraverseWhenRootParentDeleted ()
specifier|public
name|void
name|testTraverseWhenRootParentDeleted
parameter_list|()
throws|throws
name|Exception
block|{   }
block|}
end_class

end_unit

