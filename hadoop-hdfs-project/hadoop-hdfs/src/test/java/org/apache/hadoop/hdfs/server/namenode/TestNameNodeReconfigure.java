begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|conf
operator|.
name|ReconfigurationException
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
name|HdfsConfiguration
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
name|blockmanagement
operator|.
name|DatanodeManager
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
name|test
operator|.
name|GenericTestUtils
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_CALLER_CONTEXT_ENABLED_DEFAULT
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
name|DFS_HEARTBEAT_INTERVAL_KEY
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
name|DFS_HEARTBEAT_INTERVAL_DEFAULT
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
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
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
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_DEFAULT
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
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
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
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_DEFAULT
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
name|DFS_BLOCK_INVALIDATE_LIMIT_KEY
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
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|IPC_BACKOFF_ENABLE_DEFAULT
import|;
end_import

begin_class
DECL|class|TestNameNodeReconfigure
specifier|public
class|class
name|TestNameNodeReconfigure
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestNameNodeReconfigure
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|customizedBlockInvalidateLimit
specifier|private
specifier|final
name|int
name|customizedBlockInvalidateLimit
init|=
literal|500
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_BLOCK_INVALIDATE_LIMIT_KEY
argument_list|,
name|customizedBlockInvalidateLimit
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReconfigureCallerContextEnabled ()
specifier|public
name|void
name|testReconfigureCallerContextEnabled
parameter_list|()
throws|throws
name|ReconfigurationException
block|{
specifier|final
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
specifier|final
name|FSNamesystem
name|nameSystem
init|=
name|nameNode
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
comment|// try invalid values
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|verifyReconfigureCallerContextEnabled
argument_list|(
name|nameNode
argument_list|,
name|nameSystem
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// enable CallerContext
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|verifyReconfigureCallerContextEnabled
argument_list|(
name|nameNode
argument_list|,
name|nameSystem
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// disable CallerContext
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|verifyReconfigureCallerContextEnabled
argument_list|(
name|nameNode
argument_list|,
name|nameSystem
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// revert to default
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// verify default
name|assertEquals
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|false
argument_list|,
name|nameSystem
operator|.
name|getCallerContextEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|null
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyReconfigureCallerContextEnabled (final NameNode nameNode, final FSNamesystem nameSystem, boolean expected)
name|void
name|verifyReconfigureCallerContextEnabled
parameter_list|(
specifier|final
name|NameNode
name|nameNode
parameter_list|,
specifier|final
name|FSNamesystem
name|nameSystem
parameter_list|,
name|boolean
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
operator|+
literal|" has wrong value"
argument_list|,
name|expected
argument_list|,
name|nameNode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getCallerContextEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
operator|+
literal|" has wrong value"
argument_list|,
name|expected
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
argument_list|,
name|HADOOP_CALLER_CONTEXT_ENABLED_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to reconfigure enable/disable IPC backoff    */
annotation|@
name|Test
DECL|method|testReconfigureIPCBackoff ()
specifier|public
name|void
name|testReconfigureIPCBackoff
parameter_list|()
throws|throws
name|ReconfigurationException
block|{
specifier|final
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|NameNodeRpcServer
name|nnrs
init|=
operator|(
name|NameNodeRpcServer
operator|)
name|nameNode
operator|.
name|getRpcServer
argument_list|()
decl_stmt|;
name|String
name|ipcClientRPCBackoffEnable
init|=
name|NameNode
operator|.
name|buildBackoffEnableKey
argument_list|(
name|nnrs
operator|.
name|getClientRpcServer
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
comment|// try invalid values
name|verifyReconfigureIPCBackoff
argument_list|(
name|nameNode
argument_list|,
name|nnrs
argument_list|,
name|ipcClientRPCBackoffEnable
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// enable IPC_CLIENT_RPC_BACKOFF
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|ipcClientRPCBackoffEnable
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|verifyReconfigureIPCBackoff
argument_list|(
name|nameNode
argument_list|,
name|nnrs
argument_list|,
name|ipcClientRPCBackoffEnable
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// disable IPC_CLIENT_RPC_BACKOFF
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|ipcClientRPCBackoffEnable
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|verifyReconfigureIPCBackoff
argument_list|(
name|nameNode
argument_list|,
name|nnrs
argument_list|,
name|ipcClientRPCBackoffEnable
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// revert to default
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|ipcClientRPCBackoffEnable
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ipcClientRPCBackoffEnable
operator|+
literal|" has wrong value"
argument_list|,
literal|false
argument_list|,
name|nnrs
operator|.
name|getClientRpcServer
argument_list|()
operator|.
name|isClientBackoffEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ipcClientRPCBackoffEnable
operator|+
literal|" has wrong value"
argument_list|,
literal|null
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|ipcClientRPCBackoffEnable
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyReconfigureIPCBackoff (final NameNode nameNode, final NameNodeRpcServer nnrs, String property, boolean expected)
name|void
name|verifyReconfigureIPCBackoff
parameter_list|(
specifier|final
name|NameNode
name|nameNode
parameter_list|,
specifier|final
name|NameNodeRpcServer
name|nnrs
parameter_list|,
name|String
name|property
parameter_list|,
name|boolean
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|property
operator|+
literal|" has wrong value"
argument_list|,
name|expected
argument_list|,
name|nnrs
operator|.
name|getClientRpcServer
argument_list|()
operator|.
name|isClientBackoffEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|property
operator|+
literal|" has wrong value"
argument_list|,
name|expected
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|property
argument_list|,
name|IPC_BACKOFF_ENABLE_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to reconfigure interval of heart beat check and re-check.    */
annotation|@
name|Test
DECL|method|testReconfigureHearbeatCheck ()
specifier|public
name|void
name|testReconfigureHearbeatCheck
parameter_list|()
throws|throws
name|ReconfigurationException
block|{
specifier|final
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeManager
name|datanodeManager
init|=
name|nameNode
operator|.
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
comment|// change properties
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|""
operator|+
literal|6
argument_list|)
expr_stmt|;
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|""
operator|+
operator|(
literal|10
operator|*
literal|60
operator|*
literal|1000
operator|)
argument_list|)
expr_stmt|;
comment|// try invalid values
try|try
block|{
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ReconfigurationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NumberFormatException
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ReconfigurationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NumberFormatException
argument_list|)
expr_stmt|;
block|}
comment|// verify change
name|assertEquals
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|6
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|DFS_HEARTBEAT_INTERVAL_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|6
argument_list|,
name|datanodeManager
operator|.
name|getHeartbeatInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|10
operator|*
literal|60
operator|*
literal|1000
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|10
operator|*
literal|60
operator|*
literal|1000
argument_list|,
name|datanodeManager
operator|.
name|getHeartbeatRecheckInterval
argument_list|()
argument_list|)
expr_stmt|;
comment|// change to a value with time unit
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|"1m"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|60
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|DFS_HEARTBEAT_INTERVAL_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|60
argument_list|,
name|datanodeManager
operator|.
name|getHeartbeatInterval
argument_list|()
argument_list|)
expr_stmt|;
comment|// revert to defaults
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// verify defaults
name|assertEquals
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|null
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
name|DFS_HEARTBEAT_INTERVAL_DEFAULT
argument_list|,
name|datanodeManager
operator|.
name|getHeartbeatInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|null
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
operator|+
literal|" has wrong value"
argument_list|,
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_DEFAULT
argument_list|,
name|datanodeManager
operator|.
name|getHeartbeatRecheckInterval
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests activate/deactivate Storage Policy Satisfier dynamically.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testReconfigureStoragePolicySatisfierActivated ()
specifier|public
name|void
name|testReconfigureStoragePolicySatisfierActivated
parameter_list|()
throws|throws
name|ReconfigurationException
block|{
specifier|final
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|verifySPSActivated
argument_list|(
name|nameNode
argument_list|,
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// try invalid values
try|try
block|{
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ReconfigurationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"For activating or deactivating storage policy satisfier, "
operator|+
literal|"we must pass true/false only"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// enable SPS
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|verifySPSActivated
argument_list|(
name|nameNode
argument_list|,
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// disable SPS
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|verifySPSActivated
argument_list|(
name|nameNode
argument_list|,
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// revert to default
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|true
argument_list|,
name|nameNode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|isStoragePolicySatisfierRunning
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|true
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to satisfy storage policy after deactivating storage policy satisfier.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testSatisfyStoragePolicyAfterSatisfierDeactivated ()
specifier|public
name|void
name|testSatisfyStoragePolicyAfterSatisfierDeactivated
parameter_list|()
throws|throws
name|ReconfigurationException
throws|,
name|IOException
block|{
specifier|final
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
comment|// deactivate SPS
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|verifySPSActivated
argument_list|(
name|nameNode
argument_list|,
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testSPS"
argument_list|)
decl_stmt|;
name|DistributedFileSystem
name|fileSystem
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|setStoragePolicy
argument_list|(
name|filePath
argument_list|,
literal|"COLD"
argument_list|)
expr_stmt|;
try|try
block|{
name|fileSystem
operator|.
name|satisfyStoragePolicy
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected to fail, as storage policy feature has deactivated."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot request to satisfy storage policy "
operator|+
literal|"when storage policy satisfier feature has been deactivated"
operator|+
literal|" by admin. Seek for an admin help to activate it "
operator|+
literal|"or use Mover tool."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// revert to default
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|true
argument_list|,
name|nameNode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|isStoragePolicySatisfierRunning
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
operator|+
literal|" has wrong value"
argument_list|,
literal|true
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_KEY
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySPSActivated (final NameNode nameNode, String property, boolean expected)
name|void
name|verifySPSActivated
parameter_list|(
specifier|final
name|NameNode
name|nameNode
parameter_list|,
name|String
name|property
parameter_list|,
name|boolean
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|property
operator|+
literal|" has wrong value"
argument_list|,
name|expected
argument_list|,
name|nameNode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|isStoragePolicySatisfierRunning
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|property
operator|+
literal|" has wrong value"
argument_list|,
name|expected
argument_list|,
name|nameNode
operator|.
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|property
argument_list|,
name|DFS_STORAGE_POLICY_SATISFIER_ACTIVATE_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockInvalidateLimitAfterReconfigured ()
specifier|public
name|void
name|testBlockInvalidateLimitAfterReconfigured
parameter_list|()
throws|throws
name|ReconfigurationException
block|{
specifier|final
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeManager
name|datanodeManager
init|=
name|nameNode
operator|.
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DFS_BLOCK_INVALIDATE_LIMIT_KEY
operator|+
literal|" is not correctly set"
argument_list|,
name|customizedBlockInvalidateLimit
argument_list|,
name|datanodeManager
operator|.
name|getBlockInvalidateLimit
argument_list|()
argument_list|)
expr_stmt|;
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// 20 * 6 = 120< 500
comment|// Invalid block limit should stay same as before after reconfiguration.
name|assertEquals
argument_list|(
name|DFS_BLOCK_INVALIDATE_LIMIT_KEY
operator|+
literal|" is not honored after reconfiguration"
argument_list|,
name|customizedBlockInvalidateLimit
argument_list|,
name|datanodeManager
operator|.
name|getBlockInvalidateLimit
argument_list|()
argument_list|)
expr_stmt|;
name|nameNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
comment|// 20 * 50 = 1000> 500
comment|// Invalid block limit should be reset to 1000
name|assertEquals
argument_list|(
name|DFS_BLOCK_INVALIDATE_LIMIT_KEY
operator|+
literal|" is not reconfigured correctly"
argument_list|,
literal|1000
argument_list|,
name|datanodeManager
operator|.
name|getBlockInvalidateLimit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutDown ()
specifier|public
name|void
name|shutDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

