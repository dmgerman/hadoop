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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertFalse
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
name|fail
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
name|HashSet
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
name|BlockListAsLongs
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
name|BlockManager
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|DatanodeStorageInfo
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
name|datanode
operator|.
name|DataNode
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
name|datanode
operator|.
name|InternalDataNodeTestUtils
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
name|BlockReportContext
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
name|DatanodeCommand
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
name|DatanodeProtocol
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
name|DatanodeRegistration
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
name|DatanodeStorage
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
name|ReceivedDeletedBlockInfo
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
name|RegisterCommand
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
name|StorageBlockReport
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
name|StorageReceivedDeletedBlocks
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
name|StorageReport
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
name|Node
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Test to ensure requests from dead datnodes are rejected by namenode with  * appropriate exceptions/failure response  */
end_comment

begin_class
DECL|class|TestDeadDatanode
specifier|public
class|class
name|TestDeadDatanode
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDeadDatanode
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Test to ensure namenode rejects request from dead datanode    * - Start a cluster    * - Shutdown the datanode and wait for it to be marked dead at the namenode    * - Send datanode requests to Namenode and make sure it is rejected     *   appropriately.    */
annotation|@
name|Test
DECL|method|testDeadDatanode ()
specifier|public
name|void
name|testDeadDatanode
parameter_list|()
throws|throws
name|Exception
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|500
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
name|String
name|poolId
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
comment|// wait for datanode to be marked live
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DatanodeRegistration
name|reg
init|=
name|InternalDataNodeTestUtils
operator|.
name|getDNRegistrationForBP
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|poolId
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|waitForDatanodeState
argument_list|(
name|cluster
argument_list|,
name|reg
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
comment|// Shutdown and wait for datanode to be marked dead
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForDatanodeState
argument_list|(
name|cluster
argument_list|,
name|reg
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
name|DatanodeProtocol
name|dnp
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|ReceivedDeletedBlockInfo
index|[]
name|blocks
init|=
block|{
operator|new
name|ReceivedDeletedBlockInfo
argument_list|(
operator|new
name|Block
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ReceivedDeletedBlockInfo
operator|.
name|BlockStatus
operator|.
name|RECEIVED_BLOCK
argument_list|,
literal|null
argument_list|)
block|}
decl_stmt|;
name|StorageReceivedDeletedBlocks
index|[]
name|storageBlocks
init|=
block|{
operator|new
name|StorageReceivedDeletedBlocks
argument_list|(
name|reg
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|blocks
argument_list|)
block|}
decl_stmt|;
comment|// Ensure blockReceived call from dead datanode is not rejected with
comment|// IOException, since it's async, but the node remains unregistered.
name|dnp
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|reg
argument_list|,
name|poolId
argument_list|,
name|storageBlocks
argument_list|)
expr_stmt|;
name|BlockManager
name|bm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
comment|// IBRs are async, make sure the NN processes all of them.
name|bm
operator|.
name|flushBlockOps
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|reg
argument_list|)
operator|.
name|isRegistered
argument_list|()
argument_list|)
expr_stmt|;
comment|// Ensure blockReport from dead datanode is rejected with IOException
name|StorageBlockReport
index|[]
name|report
init|=
block|{
operator|new
name|StorageBlockReport
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|reg
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
argument_list|,
name|BlockListAsLongs
operator|.
name|EMPTY
argument_list|)
block|}
decl_stmt|;
try|try
block|{
name|dnp
operator|.
name|blockReport
argument_list|(
name|reg
argument_list|,
name|poolId
argument_list|,
name|report
argument_list|,
operator|new
name|BlockReportContext
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
literal|0L
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException is not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
comment|// Ensure heartbeat from dead datanode is rejected with a command
comment|// that asks datanode to register again
name|StorageReport
index|[]
name|rep
init|=
block|{
operator|new
name|StorageReport
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|reg
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
block|}
decl_stmt|;
name|DatanodeCommand
index|[]
name|cmd
init|=
name|dnp
operator|.
name|sendHeartbeat
argument_list|(
name|reg
argument_list|,
name|rep
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|getCommands
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cmd
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cmd
index|[
literal|0
index|]
operator|.
name|getAction
argument_list|()
argument_list|,
name|RegisterCommand
operator|.
name|REGISTER
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeadNodeAsBlockTarget ()
specifier|public
name|void
name|testDeadNodeAsBlockTarget
parameter_list|()
throws|throws
name|Exception
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|500
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
name|numDataNodes
argument_list|(
literal|3
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
name|String
name|poolId
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
comment|// wait for datanode to be marked live
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DatanodeRegistration
name|reg
init|=
name|InternalDataNodeTestUtils
operator|.
name|getDNRegistrationForBP
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|poolId
argument_list|)
decl_stmt|;
comment|// Get the updated datanode descriptor
name|BlockManager
name|bm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|DatanodeManager
name|dm
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
name|Node
name|clientNode
init|=
name|dm
operator|.
name|getDatanode
argument_list|(
name|reg
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|waitForDatanodeState
argument_list|(
name|cluster
argument_list|,
name|reg
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
comment|// Shutdown and wait for datanode to be marked dead
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForDatanodeState
argument_list|(
name|cluster
argument_list|,
name|reg
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
comment|// Get the updated datanode descriptor available in DNM
comment|// choose the targets, but local node should not get selected as this is not
comment|// part of the cluster anymore
name|DatanodeStorageInfo
index|[]
name|results
init|=
name|bm
operator|.
name|chooseTarget4NewBlock
argument_list|(
literal|"/hello"
argument_list|,
literal|3
argument_list|,
name|clientNode
argument_list|,
operator|new
name|HashSet
argument_list|<
name|Node
argument_list|>
argument_list|()
argument_list|,
literal|256
operator|*
literal|1024
operator|*
literal|1024L
argument_list|,
literal|null
argument_list|,
operator|(
name|byte
operator|)
literal|7
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|datanodeStorageInfo
range|:
name|results
control|)
block|{
name|assertFalse
argument_list|(
literal|"Dead node should not be choosen"
argument_list|,
name|datanodeStorageInfo
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|equals
argument_list|(
name|clientNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

