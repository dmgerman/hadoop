begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer
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
name|diskbalancer
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
name|base
operator|.
name|Preconditions
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|server
operator|.
name|balancer
operator|.
name|TestBalancer
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
name|DiskBalancerWorkStatus
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
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|fsdataset
operator|.
name|impl
operator|.
name|FsVolumeImpl
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
name|diskbalancer
operator|.
name|connectors
operator|.
name|ClusterConnector
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
name|diskbalancer
operator|.
name|connectors
operator|.
name|ConnectorFactory
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerCluster
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerDataNode
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerVolume
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
name|diskbalancer
operator|.
name|planner
operator|.
name|NodePlan
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
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
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Random
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
name|assertNotNull
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test Disk Balancer.  */
end_comment

begin_class
DECL|class|TestDiskBalancer
specifier|public
class|class
name|TestDiskBalancer
block|{
DECL|field|PLAN_FILE
specifier|private
specifier|static
specifier|final
name|String
name|PLAN_FILE
init|=
literal|"/system/current.plan.json"
decl_stmt|;
annotation|@
name|Test
DECL|method|testDiskBalancerNameNodeConnectivity ()
specifier|public
name|void
name|testDiskBalancerNameNodeConnectivity
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
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DISK_BALANCER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDatanodes
init|=
literal|2
decl_stmt|;
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
name|numDatanodes
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
name|ClusterConnector
name|nameNodeConnector
init|=
name|ConnectorFactory
operator|.
name|getCluster
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DiskBalancerCluster
name|diskBalancerCluster
init|=
operator|new
name|DiskBalancerCluster
argument_list|(
name|nameNodeConnector
argument_list|)
decl_stmt|;
name|diskBalancerCluster
operator|.
name|readClusterInfo
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|numDatanodes
argument_list|)
expr_stmt|;
name|DataNode
name|dnNode
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
name|DiskBalancerDataNode
name|dbDnNode
init|=
name|diskBalancerCluster
operator|.
name|getNodeByUUID
argument_list|(
name|dnNode
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|dnNode
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|dbDnNode
operator|.
name|getDataNodeUUID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dnNode
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getIpAddr
argument_list|()
argument_list|,
name|dbDnNode
operator|.
name|getDataNodeIP
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dnNode
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|,
name|dbDnNode
operator|.
name|getDataNodeName
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|ref
init|=
name|dnNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
name|assertEquals
argument_list|(
name|ref
operator|.
name|size
argument_list|()
argument_list|,
name|dbDnNode
operator|.
name|getVolumeCount
argument_list|()
argument_list|)
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
comment|/**    * This test simulates a real Data node working with DiskBalancer.    *<p>    * Here is the overview of this test.    *<p>    * 1. Write a bunch of blocks and move them to one disk to create imbalance.    * 2. Rewrite  the capacity of the disks in DiskBalancer Model so that planner    * will produce a move plan. 3. Execute the move plan and wait unitl the plan    * is done. 4. Verify the source disk has blocks now.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testDiskBalancerEndToEnd ()
specifier|public
name|void
name|testDiskBalancerEndToEnd
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
specifier|final
name|int
name|defaultBlockSize
init|=
literal|100
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DISK_BALANCER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|defaultBlockSize
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
name|defaultBlockSize
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
specifier|final
name|int
name|numDatanodes
init|=
literal|1
decl_stmt|;
specifier|final
name|String
name|fileName
init|=
literal|"/tmp.txt"
decl_stmt|;
specifier|final
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
specifier|final
name|int
name|blocks
init|=
literal|100
decl_stmt|;
specifier|final
name|int
name|blocksSize
init|=
literal|1024
decl_stmt|;
specifier|final
name|int
name|fileLen
init|=
name|blocks
operator|*
name|blocksSize
decl_stmt|;
comment|// Write a file and restart the cluster
name|long
index|[]
name|capacities
init|=
operator|new
name|long
index|[]
block|{
name|defaultBlockSize
operator|*
literal|2
operator|*
name|fileLen
block|,
name|defaultBlockSize
operator|*
literal|2
operator|*
name|fileLen
block|}
decl_stmt|;
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
name|numDatanodes
argument_list|)
operator|.
name|storageCapacities
argument_list|(
name|capacities
argument_list|)
operator|.
name|storageTypes
argument_list|(
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
argument_list|)
operator|.
name|storagesPerDatanode
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FsVolumeImpl
name|source
init|=
literal|null
decl_stmt|;
name|FsVolumeImpl
name|dest
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|TestBalancer
operator|.
name|createFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
name|fileLen
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|numDatanodes
operator|-
literal|1
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Get the data node and move all data to one disk.
name|DataNode
name|dnNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|numDatanodes
operator|-
literal|1
argument_list|)
decl_stmt|;
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|refs
init|=
name|dnNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
name|source
operator|=
operator|(
name|FsVolumeImpl
operator|)
name|refs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dest
operator|=
operator|(
name|FsVolumeImpl
operator|)
name|refs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|source
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|DiskBalancerTestUtil
operator|.
name|moveAllDataToDestVolume
argument_list|(
name|dnNode
operator|.
name|getFSDataset
argument_list|()
argument_list|,
name|source
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|source
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Start up a disk balancer and read the cluster info.
specifier|final
name|DataNode
name|newDN
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|numDatanodes
operator|-
literal|1
argument_list|)
decl_stmt|;
name|ClusterConnector
name|nameNodeConnector
init|=
name|ConnectorFactory
operator|.
name|getCluster
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DiskBalancerCluster
name|diskBalancerCluster
init|=
operator|new
name|DiskBalancerCluster
argument_list|(
name|nameNodeConnector
argument_list|)
decl_stmt|;
name|diskBalancerCluster
operator|.
name|readClusterInfo
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|nodesToProcess
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Rewrite the capacity in the model to show that disks need
comment|// re-balancing.
name|setVolumeCapacity
argument_list|(
name|diskBalancerCluster
argument_list|,
name|defaultBlockSize
operator|*
literal|2
operator|*
name|fileLen
argument_list|,
literal|"DISK"
argument_list|)
expr_stmt|;
comment|// Pick a node to process.
name|nodesToProcess
operator|.
name|add
argument_list|(
name|diskBalancerCluster
operator|.
name|getNodeByUUID
argument_list|(
name|dnNode
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|diskBalancerCluster
operator|.
name|setNodesToProcess
argument_list|(
name|nodesToProcess
argument_list|)
expr_stmt|;
comment|// Compute a plan.
name|List
argument_list|<
name|NodePlan
argument_list|>
name|clusterplan
init|=
name|diskBalancerCluster
operator|.
name|computePlan
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
comment|// Now we must have a plan,since the node is imbalanced and we
comment|// asked the disk balancer to create a plan.
name|assertTrue
argument_list|(
name|clusterplan
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|NodePlan
name|plan
init|=
name|clusterplan
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|plan
operator|.
name|setNodeUUID
argument_list|(
name|dnNode
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
name|plan
operator|.
name|setTimeStamp
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|planJson
init|=
name|plan
operator|.
name|toJson
argument_list|()
decl_stmt|;
name|String
name|planID
init|=
name|DigestUtils
operator|.
name|shaHex
argument_list|(
name|planJson
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|plan
operator|.
name|getVolumeSetPlans
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|getVolumeSetPlans
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|plan
operator|.
name|getVolumeSetPlans
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|setTolerancePercent
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|// Submit the plan and wait till the execution is done.
name|newDN
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planID
argument_list|,
literal|1
argument_list|,
name|PLAN_FILE
argument_list|,
name|planJson
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|jmxString
init|=
name|newDN
operator|.
name|getDiskBalancerStatus
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|jmxString
argument_list|)
expr_stmt|;
name|DiskBalancerWorkStatus
name|status
init|=
name|DiskBalancerWorkStatus
operator|.
name|parseJson
argument_list|(
name|jmxString
argument_list|)
decl_stmt|;
name|DiskBalancerWorkStatus
name|realStatus
init|=
name|newDN
operator|.
name|queryDiskBalancerPlan
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|realStatus
operator|.
name|getPlanID
argument_list|()
argument_list|,
name|status
operator|.
name|getPlanID
argument_list|()
argument_list|)
expr_stmt|;
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
try|try
block|{
return|return
name|newDN
operator|.
name|queryDiskBalancerPlan
argument_list|()
operator|.
name|getResult
argument_list|()
operator|==
name|DiskBalancerWorkStatus
operator|.
name|Result
operator|.
name|PLAN_DONE
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
comment|//verify that it worked.
name|dnNode
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|numDatanodes
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dnNode
operator|.
name|queryDiskBalancerPlan
argument_list|()
operator|.
name|getResult
argument_list|()
argument_list|,
name|DiskBalancerWorkStatus
operator|.
name|Result
operator|.
name|PLAN_DONE
argument_list|)
expr_stmt|;
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|refs
init|=
name|dnNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
name|source
operator|=
operator|(
name|FsVolumeImpl
operator|)
name|refs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|source
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// Tolerance
name|long
name|delta
init|=
operator|(
name|plan
operator|.
name|getVolumeSetPlans
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBytesToMove
argument_list|()
operator|*
literal|10
operator|)
operator|/
literal|100
decl_stmt|;
name|assertTrue
argument_list|(
operator|(
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|source
argument_list|)
operator|*
name|defaultBlockSize
operator|+
name|delta
operator|)
operator|>=
name|plan
operator|.
name|getVolumeSetPlans
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBytesToMove
argument_list|()
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testBalanceDataBetweenMultiplePairsOfVolumes ()
specifier|public
name|void
name|testBalanceDataBetweenMultiplePairsOfVolumes
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
specifier|final
name|int
name|DEFAULT_BLOCK_SIZE
init|=
literal|2048
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DISK_BALANCER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
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
specifier|final
name|int
name|NUM_DATANODES
init|=
literal|1
decl_stmt|;
specifier|final
name|long
name|CAP
init|=
literal|512
operator|*
literal|1024
decl_stmt|;
specifier|final
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/testfile"
argument_list|)
decl_stmt|;
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
name|NUM_DATANODES
argument_list|)
operator|.
name|storageCapacities
argument_list|(
operator|new
name|long
index|[]
block|{
name|CAP
block|,
name|CAP
block|,
name|CAP
block|,
name|CAP
block|}
argument_list|)
operator|.
name|storagesPerDatanode
argument_list|(
literal|4
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
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|TestBalancer
operator|.
name|createFile
argument_list|(
name|cluster
argument_list|,
name|testFile
argument_list|,
name|CAP
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|testFile
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|DataNode
name|dnNode
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
comment|// Move data out of two volumes to make them empty.
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|refs
init|=
name|dnNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|refs
operator|.
name|size
argument_list|()
argument_list|)
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
name|refs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|FsVolumeImpl
name|source
init|=
operator|(
name|FsVolumeImpl
operator|)
name|refs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|FsVolumeImpl
name|dest
init|=
operator|(
name|FsVolumeImpl
operator|)
name|refs
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|source
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|DiskBalancerTestUtil
operator|.
name|moveAllDataToDestVolume
argument_list|(
name|dnNode
operator|.
name|getFSDataset
argument_list|()
argument_list|,
name|source
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|source
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Start up a disk balancer and read the cluster info.
specifier|final
name|DataNode
name|dataNode
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
name|ClusterConnector
name|nameNodeConnector
init|=
name|ConnectorFactory
operator|.
name|getCluster
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DiskBalancerCluster
name|diskBalancerCluster
init|=
operator|new
name|DiskBalancerCluster
argument_list|(
name|nameNodeConnector
argument_list|)
decl_stmt|;
name|diskBalancerCluster
operator|.
name|readClusterInfo
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|nodesToProcess
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Rewrite the capacity in the model to show that disks need
comment|// re-balancing.
name|setVolumeCapacity
argument_list|(
name|diskBalancerCluster
argument_list|,
name|CAP
argument_list|,
literal|"DISK"
argument_list|)
expr_stmt|;
name|nodesToProcess
operator|.
name|add
argument_list|(
name|diskBalancerCluster
operator|.
name|getNodeByUUID
argument_list|(
name|dataNode
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|diskBalancerCluster
operator|.
name|setNodesToProcess
argument_list|(
name|nodesToProcess
argument_list|)
expr_stmt|;
comment|// Compute a plan.
name|List
argument_list|<
name|NodePlan
argument_list|>
name|clusterPlan
init|=
name|diskBalancerCluster
operator|.
name|computePlan
argument_list|(
literal|10.0f
argument_list|)
decl_stmt|;
name|NodePlan
name|plan
init|=
name|clusterPlan
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|plan
operator|.
name|getVolumeSetPlans
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|plan
operator|.
name|setNodeUUID
argument_list|(
name|dnNode
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
name|plan
operator|.
name|setTimeStamp
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|planJson
init|=
name|plan
operator|.
name|toJson
argument_list|()
decl_stmt|;
name|String
name|planID
init|=
name|DigestUtils
operator|.
name|shaHex
argument_list|(
name|planJson
argument_list|)
decl_stmt|;
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planID
argument_list|,
literal|1
argument_list|,
name|PLAN_FILE
argument_list|,
name|planJson
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
try|try
block|{
return|return
name|dataNode
operator|.
name|queryDiskBalancerPlan
argument_list|()
operator|.
name|getResult
argument_list|()
operator|==
name|DiskBalancerWorkStatus
operator|.
name|Result
operator|.
name|PLAN_DONE
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dataNode
operator|.
name|queryDiskBalancerPlan
argument_list|()
operator|.
name|getResult
argument_list|()
argument_list|,
name|DiskBalancerWorkStatus
operator|.
name|Result
operator|.
name|PLAN_DONE
argument_list|)
expr_stmt|;
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|refs
init|=
name|dataNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
for|for
control|(
name|FsVolumeSpi
name|vol
range|:
name|refs
control|)
block|{
name|assertTrue
argument_list|(
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|vol
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Sets alll Disks capacity to size specified.    *    * @param cluster - DiskBalancerCluster    * @param size    - new size of the disk    */
DECL|method|setVolumeCapacity (DiskBalancerCluster cluster, long size, String diskType)
specifier|private
name|void
name|setVolumeCapacity
parameter_list|(
name|DiskBalancerCluster
name|cluster
parameter_list|,
name|long
name|size
parameter_list|,
name|String
name|diskType
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
for|for
control|(
name|DiskBalancerDataNode
name|node
range|:
name|cluster
operator|.
name|getNodes
argument_list|()
control|)
block|{
for|for
control|(
name|DiskBalancerVolume
name|vol
range|:
name|node
operator|.
name|getVolumeSets
argument_list|()
operator|.
name|get
argument_list|(
name|diskType
argument_list|)
operator|.
name|getVolumes
argument_list|()
control|)
block|{
name|vol
operator|.
name|setCapacity
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
name|node
operator|.
name|getVolumeSets
argument_list|()
operator|.
name|get
argument_list|(
name|diskType
argument_list|)
operator|.
name|computeVolumeDataDensity
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

