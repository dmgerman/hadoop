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
name|Collections
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
name|FileSystem
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
name|TestBlockStoragePolicy
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
name|NameNode
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
name|NetworkTopology
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
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|BeforeClass
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

begin_class
DECL|class|TestAvailableSpaceBlockPlacementPolicy
specifier|public
class|class
name|TestAvailableSpaceBlockPlacementPolicy
block|{
DECL|field|numRacks
specifier|private
specifier|final
specifier|static
name|int
name|numRacks
init|=
literal|4
decl_stmt|;
DECL|field|nodesPerRack
specifier|private
specifier|final
specifier|static
name|int
name|nodesPerRack
init|=
literal|5
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
specifier|static
name|int
name|blockSize
init|=
literal|1024
decl_stmt|;
DECL|field|chooseTimes
specifier|private
specifier|final
specifier|static
name|int
name|chooseTimes
init|=
literal|10000
decl_stmt|;
DECL|field|file
specifier|private
specifier|final
specifier|static
name|String
name|file
init|=
literal|"/tobers/test"
decl_stmt|;
DECL|field|replica
specifier|private
specifier|final
specifier|static
name|int
name|replica
init|=
literal|3
decl_stmt|;
DECL|field|storages
specifier|private
specifier|static
name|DatanodeStorageInfo
index|[]
name|storages
decl_stmt|;
DECL|field|dataNodes
specifier|private
specifier|static
name|DatanodeDescriptor
index|[]
name|dataNodes
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|namenode
specifier|private
specifier|static
name|NameNode
name|namenode
decl_stmt|;
DECL|field|placementPolicy
specifier|private
specifier|static
name|BlockPlacementPolicy
name|placementPolicy
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|NetworkTopology
name|cluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupCluster ()
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AVAILABLE_SPACE_BLOCK_PLACEMENT_POLICY_BALANCED_SPACE_PREFERENCE_FRACTION_KEY
argument_list|,
literal|0.6f
argument_list|)
expr_stmt|;
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[
name|numRacks
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
name|numRacks
condition|;
name|i
operator|++
control|)
block|{
name|racks
index|[
name|i
index|]
operator|=
literal|"/rack"
operator|+
name|i
expr_stmt|;
block|}
name|String
index|[]
name|owerRackOfNodes
init|=
operator|new
name|String
index|[
name|numRacks
operator|*
name|nodesPerRack
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
name|nodesPerRack
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
name|numRacks
condition|;
name|j
operator|++
control|)
block|{
name|owerRackOfNodes
index|[
name|i
operator|*
name|numRacks
operator|+
name|j
index|]
operator|=
name|racks
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
name|storages
operator|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
name|owerRackOfNodes
argument_list|)
expr_stmt|;
name|dataNodes
operator|=
name|DFSTestUtil
operator|.
name|toDatanodeDescriptor
argument_list|(
name|storages
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
literal|"hdfs://localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|File
name|baseDir
init|=
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|AvailableSpaceBlockPlacementPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"name"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|AvailableSpaceBlockPlacementPolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|formatNameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|namenode
operator|=
operator|new
name|NameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|BlockManager
name|bm
init|=
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|placementPolicy
operator|=
name|bm
operator|.
name|getBlockPlacementPolicy
argument_list|()
expr_stmt|;
name|cluster
operator|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getNetworkTopology
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodesPerRack
operator|*
name|numRacks
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|add
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|setupDataNodeCapacity
argument_list|()
expr_stmt|;
block|}
DECL|method|updateHeartbeatWithUsage (DatanodeDescriptor dn, long capacity, long dfsUsed, long remaining, long blockPoolUsed, long dnCacheCapacity, long dnCacheUsed, int xceiverCount, int volFailures)
specifier|private
specifier|static
name|void
name|updateHeartbeatWithUsage
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|,
name|long
name|capacity
parameter_list|,
name|long
name|dfsUsed
parameter_list|,
name|long
name|remaining
parameter_list|,
name|long
name|blockPoolUsed
parameter_list|,
name|long
name|dnCacheCapacity
parameter_list|,
name|long
name|dnCacheUsed
parameter_list|,
name|int
name|xceiverCount
parameter_list|,
name|int
name|volFailures
parameter_list|)
block|{
name|dn
operator|.
name|getStorageInfos
argument_list|()
index|[
literal|0
index|]
operator|.
name|setUtilizationForTesting
argument_list|(
name|capacity
argument_list|,
name|dfsUsed
argument_list|,
name|remaining
argument_list|,
name|blockPoolUsed
argument_list|)
expr_stmt|;
name|dn
operator|.
name|updateHeartbeat
argument_list|(
name|BlockManagerTestUtil
operator|.
name|getStorageReportsForDatanode
argument_list|(
name|dn
argument_list|)
argument_list|,
name|dnCacheCapacity
argument_list|,
name|dnCacheUsed
argument_list|,
name|xceiverCount
argument_list|,
name|volFailures
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|setupDataNodeCapacity ()
specifier|private
specifier|static
name|void
name|setupDataNodeCapacity
parameter_list|()
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
name|nodesPerRack
operator|*
name|numRacks
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|i
operator|%
literal|2
operator|)
operator|==
literal|0
condition|)
block|{
comment|// remaining 100%
name|updateHeartbeatWithUsage
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|,
literal|2
operator|*
name|HdfsServerConstants
operator|.
name|MIN_BLOCKS_FOR_WRITE
operator|*
name|blockSize
argument_list|,
literal|0L
argument_list|,
literal|2
operator|*
name|HdfsServerConstants
operator|.
name|MIN_BLOCKS_FOR_WRITE
operator|*
name|blockSize
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// remaining 50%
name|updateHeartbeatWithUsage
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|,
literal|2
operator|*
name|HdfsServerConstants
operator|.
name|MIN_BLOCKS_FOR_WRITE
operator|*
name|blockSize
argument_list|,
name|HdfsServerConstants
operator|.
name|MIN_BLOCKS_FOR_WRITE
operator|*
name|blockSize
argument_list|,
name|HdfsServerConstants
operator|.
name|MIN_BLOCKS_FOR_WRITE
operator|*
name|blockSize
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*    * To verify that the BlockPlacementPolicy can be replaced by AvailableSpaceBlockPlacementPolicy via    * changing the configuration.    */
annotation|@
name|Test
DECL|method|testPolicyReplacement ()
specifier|public
name|void
name|testPolicyReplacement
parameter_list|()
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|placementPolicy
operator|instanceof
name|AvailableSpaceBlockPlacementPolicy
operator|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * Call choose target many times and verify that nodes with more remaining percent will be chosen    * with high possibility.    */
annotation|@
name|Test
DECL|method|testChooseTarget ()
specifier|public
name|void
name|testChooseTarget
parameter_list|()
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
name|int
name|moreRemainingNode
init|=
literal|0
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
name|chooseTimes
condition|;
name|i
operator|++
control|)
block|{
name|DatanodeStorageInfo
index|[]
name|targets
init|=
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getBlockPlacementPolicy
argument_list|()
operator|.
name|chooseTarget
argument_list|(
name|file
argument_list|,
name|replica
argument_list|,
literal|null
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|blockSize
argument_list|,
name|TestBlockStoragePolicy
operator|.
name|DEFAULT_STORAGE_POLICY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|targets
operator|.
name|length
operator|==
name|replica
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|replica
condition|;
name|j
operator|++
control|)
block|{
name|total
operator|++
expr_stmt|;
if|if
condition|(
name|targets
index|[
name|j
index|]
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|getRemainingPercent
argument_list|()
operator|>
literal|60
condition|)
block|{
name|moreRemainingNode
operator|++
expr_stmt|;
block|}
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|total
operator|==
name|replica
operator|*
name|chooseTimes
argument_list|)
expr_stmt|;
name|double
name|possibility
init|=
literal|1.0
operator|*
name|moreRemainingNode
operator|/
name|total
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|possibility
operator|>
literal|0.52
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|possibility
operator|<
literal|0.55
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChooseDataNode ()
specifier|public
name|void
name|testChooseDataNode
parameter_list|()
block|{
try|try
block|{
name|Collection
argument_list|<
name|Node
argument_list|>
name|allNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|dataNodes
operator|.
name|length
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|allNodes
argument_list|,
name|dataNodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|placementPolicy
operator|instanceof
name|AvailableSpaceBlockPlacementPolicy
condition|)
block|{
comment|// exclude all datanodes when chooseDataNode, no NPE should be thrown
operator|(
operator|(
name|AvailableSpaceBlockPlacementPolicy
operator|)
name|placementPolicy
operator|)
operator|.
name|chooseDataNode
argument_list|(
literal|"~"
argument_list|,
name|allNodes
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"NPE should not be thrown"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|teardownCluster ()
specifier|public
specifier|static
name|void
name|teardownCluster
parameter_list|()
block|{
if|if
condition|(
name|namenode
operator|!=
literal|null
condition|)
block|{
name|namenode
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

