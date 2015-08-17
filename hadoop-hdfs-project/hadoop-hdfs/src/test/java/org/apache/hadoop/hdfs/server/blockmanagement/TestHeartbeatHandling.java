begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|server
operator|.
name|common
operator|.
name|GenerationStamp
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
name|BlockUCState
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
name|DataNodeTestUtils
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
name|FSNamesystem
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
name|NameNodeAdapter
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
name|BlockCommand
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
name|BlockRecoveryCommand
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test if FSNamesystem handles heartbeat right  */
end_comment

begin_class
DECL|class|TestHeartbeatHandling
specifier|public
class|class
name|TestHeartbeatHandling
block|{
comment|/**    * Test if    * {@link FSNamesystem#handleHeartbeat}    * can pick up replication and/or invalidate requests and observes the max    * limit    */
annotation|@
name|Test
DECL|method|testHeartbeat ()
specifier|public
name|void
name|testHeartbeat
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|HeartbeatManager
name|hm
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|(           )
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getHeartbeatManager
argument_list|()
decl_stmt|;
specifier|final
name|String
name|poolId
init|=
name|namesystem
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeRegistration
name|nodeReg
init|=
name|DataNodeTestUtils
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
specifier|final
name|DatanodeDescriptor
name|dd
init|=
name|NameNodeAdapter
operator|.
name|getDatanode
argument_list|(
name|namesystem
argument_list|,
name|nodeReg
argument_list|)
decl_stmt|;
specifier|final
name|String
name|storageID
init|=
name|DatanodeStorage
operator|.
name|generateUuid
argument_list|()
decl_stmt|;
name|dd
operator|.
name|updateStorage
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|storageID
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|REMAINING_BLOCKS
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|MAX_REPLICATE_LIMIT
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|int
name|MAX_INVALIDATE_LIMIT
init|=
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_INVALIDATE_LIMIT_DEFAULT
decl_stmt|;
specifier|final
name|int
name|MAX_INVALIDATE_BLOCKS
init|=
literal|2
operator|*
name|MAX_INVALIDATE_LIMIT
operator|+
name|REMAINING_BLOCKS
decl_stmt|;
specifier|final
name|int
name|MAX_REPLICATE_BLOCKS
init|=
literal|2
operator|*
name|MAX_REPLICATE_LIMIT
operator|+
name|REMAINING_BLOCKS
decl_stmt|;
specifier|final
name|DatanodeStorageInfo
index|[]
name|ONE_TARGET
init|=
block|{
name|dd
operator|.
name|getStorageInfo
argument_list|(
name|storageID
argument_list|)
block|}
decl_stmt|;
try|try
block|{
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|hm
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MAX_REPLICATE_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|dd
operator|.
name|addBlockToBeReplicated
argument_list|(
operator|new
name|Block
argument_list|(
name|i
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
argument_list|,
name|ONE_TARGET
argument_list|)
expr_stmt|;
block|}
name|DatanodeCommand
index|[]
name|cmds
init|=
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg
argument_list|,
name|dd
argument_list|,
name|namesystem
argument_list|)
operator|.
name|getCommands
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cmds
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_TRANSFER
argument_list|,
name|cmds
index|[
literal|0
index|]
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX_REPLICATE_LIMIT
argument_list|,
operator|(
operator|(
name|BlockCommand
operator|)
name|cmds
index|[
literal|0
index|]
operator|)
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Block
argument_list|>
name|blockList
init|=
operator|new
name|ArrayList
argument_list|<
name|Block
argument_list|>
argument_list|(
name|MAX_INVALIDATE_BLOCKS
argument_list|)
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
name|MAX_INVALIDATE_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|blockList
operator|.
name|add
argument_list|(
operator|new
name|Block
argument_list|(
name|i
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dd
operator|.
name|addBlocksToBeInvalidated
argument_list|(
name|blockList
argument_list|)
expr_stmt|;
name|cmds
operator|=
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg
argument_list|,
name|dd
argument_list|,
name|namesystem
argument_list|)
operator|.
name|getCommands
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cmds
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_TRANSFER
argument_list|,
name|cmds
index|[
literal|0
index|]
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX_REPLICATE_LIMIT
argument_list|,
operator|(
operator|(
name|BlockCommand
operator|)
name|cmds
index|[
literal|0
index|]
operator|)
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_INVALIDATE
argument_list|,
name|cmds
index|[
literal|1
index|]
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX_INVALIDATE_LIMIT
argument_list|,
operator|(
operator|(
name|BlockCommand
operator|)
name|cmds
index|[
literal|1
index|]
operator|)
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|cmds
operator|=
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg
argument_list|,
name|dd
argument_list|,
name|namesystem
argument_list|)
operator|.
name|getCommands
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cmds
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_TRANSFER
argument_list|,
name|cmds
index|[
literal|0
index|]
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REMAINING_BLOCKS
argument_list|,
operator|(
operator|(
name|BlockCommand
operator|)
name|cmds
index|[
literal|0
index|]
operator|)
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_INVALIDATE
argument_list|,
name|cmds
index|[
literal|1
index|]
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX_INVALIDATE_LIMIT
argument_list|,
operator|(
operator|(
name|BlockCommand
operator|)
name|cmds
index|[
literal|1
index|]
operator|)
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|cmds
operator|=
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg
argument_list|,
name|dd
argument_list|,
name|namesystem
argument_list|)
operator|.
name|getCommands
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cmds
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_INVALIDATE
argument_list|,
name|cmds
index|[
literal|0
index|]
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REMAINING_BLOCKS
argument_list|,
operator|(
operator|(
name|BlockCommand
operator|)
name|cmds
index|[
literal|0
index|]
operator|)
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|cmds
operator|=
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg
argument_list|,
name|dd
argument_list|,
name|namesystem
argument_list|)
operator|.
name|getCommands
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cmds
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test if    * {@link FSNamesystem#handleHeartbeat}    * correctly selects data node targets for block recovery.    */
annotation|@
name|Test
DECL|method|testHeartbeatBlockRecovery ()
specifier|public
name|void
name|testHeartbeatBlockRecovery
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|HeartbeatManager
name|hm
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|(           )
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getHeartbeatManager
argument_list|()
decl_stmt|;
specifier|final
name|String
name|poolId
init|=
name|namesystem
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeRegistration
name|nodeReg1
init|=
name|DataNodeTestUtils
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
specifier|final
name|DatanodeDescriptor
name|dd1
init|=
name|NameNodeAdapter
operator|.
name|getDatanode
argument_list|(
name|namesystem
argument_list|,
name|nodeReg1
argument_list|)
decl_stmt|;
name|dd1
operator|.
name|updateStorage
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|DatanodeStorage
operator|.
name|generateUuid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeRegistration
name|nodeReg2
init|=
name|DataNodeTestUtils
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
literal|1
argument_list|)
argument_list|,
name|poolId
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeDescriptor
name|dd2
init|=
name|NameNodeAdapter
operator|.
name|getDatanode
argument_list|(
name|namesystem
argument_list|,
name|nodeReg2
argument_list|)
decl_stmt|;
name|dd2
operator|.
name|updateStorage
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|DatanodeStorage
operator|.
name|generateUuid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeRegistration
name|nodeReg3
init|=
name|DataNodeTestUtils
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
literal|2
argument_list|)
argument_list|,
name|poolId
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeDescriptor
name|dd3
init|=
name|NameNodeAdapter
operator|.
name|getDatanode
argument_list|(
name|namesystem
argument_list|,
name|nodeReg3
argument_list|)
decl_stmt|;
name|dd3
operator|.
name|updateStorage
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|DatanodeStorage
operator|.
name|generateUuid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|hm
init|)
block|{
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg1
argument_list|,
name|dd1
argument_list|,
name|namesystem
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg2
argument_list|,
name|dd2
argument_list|,
name|namesystem
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg3
argument_list|,
name|dd3
argument_list|,
name|namesystem
argument_list|)
expr_stmt|;
comment|// Test with all alive nodes.
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeStorageInfo
index|[]
name|storages
init|=
block|{
name|dd1
operator|.
name|getStorageInfos
argument_list|()
index|[
literal|0
index|]
block|,
name|dd2
operator|.
name|getStorageInfos
argument_list|()
index|[
literal|0
index|]
block|,
name|dd3
operator|.
name|getStorageInfos
argument_list|()
index|[
literal|0
index|]
block|}
decl_stmt|;
name|BlockInfo
name|blockInfo
init|=
operator|new
name|BlockInfoContiguous
argument_list|(
operator|new
name|Block
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
decl_stmt|;
name|blockInfo
operator|.
name|convertToBlockUnderConstruction
argument_list|(
name|BlockUCState
operator|.
name|UNDER_RECOVERY
argument_list|,
name|storages
argument_list|)
expr_stmt|;
name|dd1
operator|.
name|addBlockToBeRecovered
argument_list|(
name|blockInfo
argument_list|)
expr_stmt|;
name|DatanodeCommand
index|[]
name|cmds
init|=
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg1
argument_list|,
name|dd1
argument_list|,
name|namesystem
argument_list|)
operator|.
name|getCommands
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cmds
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_RECOVERBLOCK
argument_list|,
name|cmds
index|[
literal|0
index|]
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|BlockRecoveryCommand
name|recoveryCommand
init|=
operator|(
name|BlockRecoveryCommand
operator|)
name|cmds
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|recoveryCommand
operator|.
name|getRecoveringBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DatanodeInfo
index|[]
name|recoveringNodes
init|=
name|recoveryCommand
operator|.
name|getRecoveringBlocks
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|BlockRecoveryCommand
operator|.
name|RecoveringBlock
index|[
literal|0
index|]
argument_list|)
index|[
literal|0
index|]
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|recoveringNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|recoveringNodes
index|[
literal|0
index|]
argument_list|,
name|dd1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|recoveringNodes
index|[
literal|1
index|]
argument_list|,
name|dd2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|recoveringNodes
index|[
literal|2
index|]
argument_list|,
name|dd3
argument_list|)
expr_stmt|;
comment|// Test with one stale node.
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// More than the default stale interval of 30 seconds.
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd2
argument_list|,
operator|-
literal|40
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|blockInfo
operator|=
operator|new
name|BlockInfoContiguous
argument_list|(
operator|new
name|Block
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|blockInfo
operator|.
name|convertToBlockUnderConstruction
argument_list|(
name|BlockUCState
operator|.
name|UNDER_RECOVERY
argument_list|,
name|storages
argument_list|)
expr_stmt|;
name|dd1
operator|.
name|addBlockToBeRecovered
argument_list|(
name|blockInfo
argument_list|)
expr_stmt|;
name|cmds
operator|=
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg1
argument_list|,
name|dd1
argument_list|,
name|namesystem
argument_list|)
operator|.
name|getCommands
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cmds
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_RECOVERBLOCK
argument_list|,
name|cmds
index|[
literal|0
index|]
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryCommand
operator|=
operator|(
name|BlockRecoveryCommand
operator|)
name|cmds
index|[
literal|0
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|recoveryCommand
operator|.
name|getRecoveringBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|recoveringNodes
operator|=
name|recoveryCommand
operator|.
name|getRecoveringBlocks
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|BlockRecoveryCommand
operator|.
name|RecoveringBlock
index|[
literal|0
index|]
argument_list|)
index|[
literal|0
index|]
operator|.
name|getLocations
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|recoveringNodes
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// dd2 is skipped.
name|assertEquals
argument_list|(
name|recoveringNodes
index|[
literal|0
index|]
argument_list|,
name|dd1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|recoveringNodes
index|[
literal|1
index|]
argument_list|,
name|dd3
argument_list|)
expr_stmt|;
comment|// Test with all stale node.
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd1
argument_list|,
operator|-
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// More than the default stale interval of 30 seconds.
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd2
argument_list|,
operator|-
literal|40
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd3
argument_list|,
operator|-
literal|80
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|blockInfo
operator|=
operator|new
name|BlockInfoContiguous
argument_list|(
operator|new
name|Block
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|blockInfo
operator|.
name|convertToBlockUnderConstruction
argument_list|(
name|BlockUCState
operator|.
name|UNDER_RECOVERY
argument_list|,
name|storages
argument_list|)
expr_stmt|;
name|dd1
operator|.
name|addBlockToBeRecovered
argument_list|(
name|blockInfo
argument_list|)
expr_stmt|;
name|cmds
operator|=
name|NameNodeAdapter
operator|.
name|sendHeartBeat
argument_list|(
name|nodeReg1
argument_list|,
name|dd1
argument_list|,
name|namesystem
argument_list|)
operator|.
name|getCommands
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cmds
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_RECOVERBLOCK
argument_list|,
name|cmds
index|[
literal|0
index|]
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryCommand
operator|=
operator|(
name|BlockRecoveryCommand
operator|)
name|cmds
index|[
literal|0
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|recoveryCommand
operator|.
name|getRecoveringBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|recoveringNodes
operator|=
name|recoveryCommand
operator|.
name|getRecoveringBlocks
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|BlockRecoveryCommand
operator|.
name|RecoveringBlock
index|[
literal|0
index|]
argument_list|)
index|[
literal|0
index|]
operator|.
name|getLocations
argument_list|()
expr_stmt|;
comment|// Only dd1 is included since it heart beated and hence its not stale
comment|// when the list of recovery blocks is constructed.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|recoveringNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|recoveringNodes
index|[
literal|0
index|]
argument_list|,
name|dd1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|recoveringNodes
index|[
literal|1
index|]
argument_list|,
name|dd2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|recoveringNodes
index|[
literal|2
index|]
argument_list|,
name|dd3
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
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

