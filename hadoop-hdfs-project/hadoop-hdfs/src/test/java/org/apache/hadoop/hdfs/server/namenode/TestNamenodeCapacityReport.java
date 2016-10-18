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
name|assertTrue
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
name|ArrayList
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
name|DFSOutputStream
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
name|DFSUtilClient
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|blockmanagement
operator|.
name|BlockManagerTestUtil
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
name|DatanodeDescriptor
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
name|datanode
operator|.
name|FsDatasetTestUtils
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
comment|/**  * This tests InterDataNodeProtocol for block handling.   */
end_comment

begin_class
DECL|class|TestNamenodeCapacityReport
specifier|public
class|class
name|TestNamenodeCapacityReport
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
name|TestNamenodeCapacityReport
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The following test first creates a file.    * It verifies the block information from a datanode.    * Then, it updates the block with new information and verifies again.     */
annotation|@
name|Test
DECL|method|testVolumeSize ()
specifier|public
name|void
name|testVolumeSize
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
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
comment|// Set aside fifth of the total capacity as reserved
name|long
name|reserved
init|=
literal|10000
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DU_RESERVED_KEY
argument_list|,
name|reserved
argument_list|)
expr_stmt|;
try|try
block|{
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
name|DatanodeManager
name|dm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|(           )
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
comment|// Ensure the data reported for each data node is right
specifier|final
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|live
init|=
operator|new
name|ArrayList
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|dead
init|=
operator|new
name|ArrayList
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
decl_stmt|;
name|dm
operator|.
name|fetchDatanodes
argument_list|(
name|live
argument_list|,
name|dead
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|live
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|long
name|used
decl_stmt|,
name|remaining
decl_stmt|,
name|configCapacity
decl_stmt|,
name|nonDFSUsed
decl_stmt|,
name|bpUsed
decl_stmt|;
name|float
name|percentUsed
decl_stmt|,
name|percentRemaining
decl_stmt|,
name|percentBpUsed
decl_stmt|;
for|for
control|(
specifier|final
name|DatanodeDescriptor
name|datanode
range|:
name|live
control|)
block|{
name|used
operator|=
name|datanode
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
name|remaining
operator|=
name|datanode
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
name|nonDFSUsed
operator|=
name|datanode
operator|.
name|getNonDfsUsed
argument_list|()
expr_stmt|;
name|configCapacity
operator|=
name|datanode
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|percentUsed
operator|=
name|datanode
operator|.
name|getDfsUsedPercent
argument_list|()
expr_stmt|;
name|percentRemaining
operator|=
name|datanode
operator|.
name|getRemainingPercent
argument_list|()
expr_stmt|;
name|bpUsed
operator|=
name|datanode
operator|.
name|getBlockPoolUsed
argument_list|()
expr_stmt|;
name|percentBpUsed
operator|=
name|datanode
operator|.
name|getBlockPoolUsedPercent
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Datanode configCapacity "
operator|+
name|configCapacity
operator|+
literal|" used "
operator|+
name|used
operator|+
literal|" non DFS used "
operator|+
name|nonDFSUsed
operator|+
literal|" remaining "
operator|+
name|remaining
operator|+
literal|" perentUsed "
operator|+
name|percentUsed
operator|+
literal|" percentRemaining "
operator|+
name|percentRemaining
argument_list|)
expr_stmt|;
comment|// There will be 5% space reserved in ext filesystem which is not
comment|// considered.
name|assertTrue
argument_list|(
name|configCapacity
operator|>=
operator|(
name|used
operator|+
name|remaining
operator|+
name|nonDFSUsed
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|percentUsed
operator|==
name|DFSUtilClient
operator|.
name|getPercentUsed
argument_list|(
name|used
argument_list|,
name|configCapacity
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|percentRemaining
operator|==
name|DFSUtilClient
operator|.
name|getPercentRemaining
argument_list|(
name|remaining
argument_list|,
name|configCapacity
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|percentBpUsed
operator|==
name|DFSUtilClient
operator|.
name|getPercentUsed
argument_list|(
name|bpUsed
argument_list|,
name|configCapacity
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// Currently two data directories are created by the data node
comment|// in the MiniDFSCluster. This results in each data directory having
comment|// capacity equals to the disk capacity of the data directory.
comment|// Hence the capacity reported by the data node is twice the disk space
comment|// the disk capacity
comment|//
comment|// So multiply the disk capacity and reserved space by two
comment|// for accommodating it
comment|//
specifier|final
name|FsDatasetTestUtils
name|utils
init|=
name|cluster
operator|.
name|getFsDatasetTestUtils
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|numOfDataDirs
init|=
name|utils
operator|.
name|getDefaultNumOfDataDirs
argument_list|()
decl_stmt|;
name|long
name|diskCapacity
init|=
name|numOfDataDirs
operator|*
name|utils
operator|.
name|getRawCapacity
argument_list|()
decl_stmt|;
name|reserved
operator|*=
name|numOfDataDirs
expr_stmt|;
name|configCapacity
operator|=
name|namesystem
operator|.
name|getCapacityTotal
argument_list|()
expr_stmt|;
name|used
operator|=
name|namesystem
operator|.
name|getCapacityUsed
argument_list|()
expr_stmt|;
name|nonDFSUsed
operator|=
name|namesystem
operator|.
name|getNonDfsUsedSpace
argument_list|()
expr_stmt|;
name|remaining
operator|=
name|namesystem
operator|.
name|getCapacityRemaining
argument_list|()
expr_stmt|;
name|percentUsed
operator|=
name|namesystem
operator|.
name|getPercentUsed
argument_list|()
expr_stmt|;
name|percentRemaining
operator|=
name|namesystem
operator|.
name|getPercentRemaining
argument_list|()
expr_stmt|;
name|bpUsed
operator|=
name|namesystem
operator|.
name|getBlockPoolUsedSpace
argument_list|()
expr_stmt|;
name|percentBpUsed
operator|=
name|namesystem
operator|.
name|getPercentBlockPoolUsed
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Data node directory "
operator|+
name|cluster
operator|.
name|getDataDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Name node diskCapacity "
operator|+
name|diskCapacity
operator|+
literal|" configCapacity "
operator|+
name|configCapacity
operator|+
literal|" reserved "
operator|+
name|reserved
operator|+
literal|" used "
operator|+
name|used
operator|+
literal|" remaining "
operator|+
name|remaining
operator|+
literal|" nonDFSUsed "
operator|+
name|nonDFSUsed
operator|+
literal|" remaining "
operator|+
name|remaining
operator|+
literal|" percentUsed "
operator|+
name|percentUsed
operator|+
literal|" percentRemaining "
operator|+
name|percentRemaining
operator|+
literal|" bpUsed "
operator|+
name|bpUsed
operator|+
literal|" percentBpUsed "
operator|+
name|percentBpUsed
argument_list|)
expr_stmt|;
comment|// Ensure new total capacity reported excludes the reserved space
name|assertTrue
argument_list|(
name|configCapacity
operator|==
name|diskCapacity
operator|-
name|reserved
argument_list|)
expr_stmt|;
comment|// Ensure new total capacity reported excludes the reserved space
comment|// There will be 5% space reserved in ext filesystem which is not
comment|// considered.
name|assertTrue
argument_list|(
name|configCapacity
operator|>=
operator|(
name|used
operator|+
name|remaining
operator|+
name|nonDFSUsed
operator|)
argument_list|)
expr_stmt|;
comment|// Ensure percent used is calculated based on used and present capacity
name|assertTrue
argument_list|(
name|percentUsed
operator|==
name|DFSUtilClient
operator|.
name|getPercentUsed
argument_list|(
name|used
argument_list|,
name|configCapacity
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure percent used is calculated based on used and present capacity
name|assertTrue
argument_list|(
name|percentBpUsed
operator|==
name|DFSUtilClient
operator|.
name|getPercentUsed
argument_list|(
name|bpUsed
argument_list|,
name|configCapacity
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure percent used is calculated based on used and present capacity
name|assertTrue
argument_list|(
name|percentRemaining
operator|==
operator|(
operator|(
name|float
operator|)
name|remaining
operator|*
literal|100.0f
operator|)
operator|/
operator|(
name|float
operator|)
name|configCapacity
argument_list|)
expr_stmt|;
comment|//Adding testcase for non-dfs used where we need to consider
comment|// reserved replica also.
specifier|final
name|int
name|fileCount
init|=
literal|5
decl_stmt|;
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// create streams and hsync to force datastreamers to start
name|DFSOutputStream
index|[]
name|streams
init|=
operator|new
name|DFSOutputStream
index|[
name|fileCount
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
name|fileCount
condition|;
name|i
operator|++
control|)
block|{
name|streams
index|[
name|i
index|]
operator|=
operator|(
name|DFSOutputStream
operator|)
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/f"
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|getWrappedStream
argument_list|()
expr_stmt|;
name|streams
index|[
name|i
index|]
operator|.
name|write
argument_list|(
literal|"1"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|streams
index|[
name|i
index|]
operator|.
name|hsync
argument_list|()
expr_stmt|;
block|}
name|triggerHeartbeats
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|configCapacity
operator|>
operator|(
name|namesystem
operator|.
name|getCapacityUsed
argument_list|()
operator|+
name|namesystem
operator|.
name|getCapacityRemaining
argument_list|()
operator|+
name|namesystem
operator|.
name|getNonDfsUsedSpace
argument_list|()
operator|)
argument_list|)
expr_stmt|;
comment|// There is a chance that nonDFS usage might have slightly due to
comment|// testlogs, So assume 1MB other files used within this gap
name|assertTrue
argument_list|(
operator|(
name|namesystem
operator|.
name|getCapacityUsed
argument_list|()
operator|+
name|namesystem
operator|.
name|getCapacityRemaining
argument_list|()
operator|+
name|namesystem
operator|.
name|getNonDfsUsedSpace
argument_list|()
operator|+
name|fileCount
operator|*
name|fs
operator|.
name|getDefaultBlockSize
argument_list|()
operator|)
operator|-
name|configCapacity
operator|<
literal|1
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
finally|finally
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
DECL|field|EPSILON
specifier|private
specifier|static
specifier|final
name|float
name|EPSILON
init|=
literal|0.0001f
decl_stmt|;
annotation|@
name|Test
DECL|method|testXceiverCount ()
specifier|public
name|void
name|testXceiverCount
parameter_list|()
throws|throws
name|Exception
block|{
name|testXceiverCountInternal
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testXceiverCountInternal
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testXceiverCountInternal (int minMaintenanceR)
specifier|public
name|void
name|testXceiverCountInternal
parameter_list|(
name|int
name|minMaintenanceR
parameter_list|)
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
comment|// retry one time, if close fails
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|BlockWrite
operator|.
name|LOCATEFOLLOWINGBLOCK_RETRIES_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_KEY
argument_list|,
name|minMaintenanceR
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|nodes
init|=
literal|8
decl_stmt|;
specifier|final
name|int
name|fileCount
init|=
literal|5
decl_stmt|;
specifier|final
name|short
name|fileRepl
init|=
literal|3
decl_stmt|;
try|try
block|{
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
name|nodes
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
name|DatanodeManager
name|dnm
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// trigger heartbeats in case not already sent
name|triggerHeartbeats
argument_list|(
name|datanodes
argument_list|)
expr_stmt|;
comment|// check that all nodes are live and in service
name|int
name|expectedTotalLoad
init|=
name|nodes
decl_stmt|;
comment|// xceiver server adds 1 to load
name|int
name|expectedInServiceNodes
init|=
name|nodes
decl_stmt|;
name|int
name|expectedInServiceLoad
init|=
name|nodes
decl_stmt|;
name|checkClusterHealth
argument_list|(
name|nodes
argument_list|,
name|namesystem
argument_list|,
name|expectedTotalLoad
argument_list|,
name|expectedInServiceNodes
argument_list|,
name|expectedInServiceLoad
argument_list|)
expr_stmt|;
comment|// Shutdown half the nodes followed by admin operations on those nodes.
comment|// Ensure counts are accurate.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|DataNode
name|dn
init|=
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|dnd
init|=
name|dnm
operator|.
name|getDatanode
argument_list|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
decl_stmt|;
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|setDatanodeDead
argument_list|(
name|dnd
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|checkHeartbeat
argument_list|(
name|namesystem
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
comment|//Admin operations on dead nodes won't impact nodesInService metrics.
name|startDecommissionOrMaintenance
argument_list|(
name|dnm
argument_list|,
name|dnd
argument_list|,
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
operator|)
argument_list|)
expr_stmt|;
name|expectedInServiceNodes
operator|--
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedInServiceNodes
argument_list|,
name|namesystem
operator|.
name|getNumLiveDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedInServiceNodes
argument_list|,
name|getNumDNInService
argument_list|(
name|namesystem
argument_list|)
argument_list|)
expr_stmt|;
name|stopDecommissionOrMaintenance
argument_list|(
name|dnm
argument_list|,
name|dnd
argument_list|,
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedInServiceNodes
argument_list|,
name|getNumDNInService
argument_list|(
name|namesystem
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// restart the nodes to verify that counts are correct after
comment|// node re-registration
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
name|datanodes
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
expr_stmt|;
name|expectedInServiceNodes
operator|=
name|nodes
expr_stmt|;
name|assertEquals
argument_list|(
name|nodes
argument_list|,
name|datanodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkClusterHealth
argument_list|(
name|nodes
argument_list|,
name|namesystem
argument_list|,
name|expectedTotalLoad
argument_list|,
name|expectedInServiceNodes
argument_list|,
name|expectedInServiceLoad
argument_list|)
expr_stmt|;
comment|// create streams and hsync to force datastreamers to start
name|DFSOutputStream
index|[]
name|streams
init|=
operator|new
name|DFSOutputStream
index|[
name|fileCount
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
name|fileCount
condition|;
name|i
operator|++
control|)
block|{
name|streams
index|[
name|i
index|]
operator|=
operator|(
name|DFSOutputStream
operator|)
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/f"
operator|+
name|i
argument_list|)
argument_list|,
name|fileRepl
argument_list|)
operator|.
name|getWrappedStream
argument_list|()
expr_stmt|;
name|streams
index|[
name|i
index|]
operator|.
name|write
argument_list|(
literal|"1"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|streams
index|[
name|i
index|]
operator|.
name|hsync
argument_list|()
expr_stmt|;
comment|// the load for writers is 2 because both the write xceiver& packet
comment|// responder threads are counted in the load
name|expectedTotalLoad
operator|+=
literal|2
operator|*
name|fileRepl
expr_stmt|;
name|expectedInServiceLoad
operator|+=
literal|2
operator|*
name|fileRepl
expr_stmt|;
block|}
comment|// force nodes to send load update
name|triggerHeartbeats
argument_list|(
name|datanodes
argument_list|)
expr_stmt|;
name|checkClusterHealth
argument_list|(
name|nodes
argument_list|,
name|namesystem
argument_list|,
name|expectedTotalLoad
argument_list|,
name|expectedInServiceNodes
argument_list|,
name|expectedInServiceLoad
argument_list|)
expr_stmt|;
comment|// admin operations on a few nodes, substract their load from the
comment|// expected load, trigger heartbeat to force load update.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileRepl
condition|;
name|i
operator|++
control|)
block|{
name|expectedInServiceNodes
operator|--
expr_stmt|;
name|DatanodeDescriptor
name|dnd
init|=
name|dnm
operator|.
name|getDatanode
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
decl_stmt|;
name|expectedInServiceLoad
operator|-=
name|dnd
operator|.
name|getXceiverCount
argument_list|()
expr_stmt|;
name|startDecommissionOrMaintenance
argument_list|(
name|dnm
argument_list|,
name|dnd
argument_list|,
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
operator|)
argument_list|)
expr_stmt|;
name|DataNodeTestUtils
operator|.
name|triggerHeartbeat
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|checkClusterHealth
argument_list|(
name|nodes
argument_list|,
name|namesystem
argument_list|,
name|expectedTotalLoad
argument_list|,
name|expectedInServiceNodes
argument_list|,
name|expectedInServiceLoad
argument_list|)
expr_stmt|;
block|}
comment|// check expected load while closing each stream.  recalc expected
comment|// load based on whether the nodes in the pipeline are decomm
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileCount
condition|;
name|i
operator|++
control|)
block|{
name|int
name|adminOps
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|dni
range|:
name|streams
index|[
name|i
index|]
operator|.
name|getPipeline
argument_list|()
control|)
block|{
name|DatanodeDescriptor
name|dnd
init|=
name|dnm
operator|.
name|getDatanode
argument_list|(
name|dni
argument_list|)
decl_stmt|;
name|expectedTotalLoad
operator|-=
literal|2
expr_stmt|;
if|if
condition|(
operator|!
name|dnd
operator|.
name|isInService
argument_list|()
condition|)
block|{
name|adminOps
operator|++
expr_stmt|;
block|}
else|else
block|{
name|expectedInServiceLoad
operator|-=
literal|2
expr_stmt|;
block|}
block|}
try|try
block|{
name|streams
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// nodes will go decommissioned even if there's a UC block whose
comment|// other locations are decommissioned too.  we'll ignore that
comment|// bug for now
if|if
condition|(
name|adminOps
operator|<
name|fileRepl
condition|)
block|{
throw|throw
name|ioe
throw|;
block|}
block|}
name|triggerHeartbeats
argument_list|(
name|datanodes
argument_list|)
expr_stmt|;
comment|// verify node count and loads
name|checkClusterHealth
argument_list|(
name|nodes
argument_list|,
name|namesystem
argument_list|,
name|expectedTotalLoad
argument_list|,
name|expectedInServiceNodes
argument_list|,
name|expectedInServiceLoad
argument_list|)
expr_stmt|;
block|}
comment|// shutdown each node, verify node counts based on admin state
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
condition|;
name|i
operator|++
control|)
block|{
name|DataNode
name|dn
init|=
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// force it to appear dead so live count decreases
name|DatanodeDescriptor
name|dnDesc
init|=
name|dnm
operator|.
name|getDatanode
argument_list|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|setDatanodeDead
argument_list|(
name|dnDesc
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|checkHeartbeat
argument_list|(
name|namesystem
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nodes
operator|-
literal|1
operator|-
name|i
argument_list|,
name|namesystem
operator|.
name|getNumLiveDataNodes
argument_list|()
argument_list|)
expr_stmt|;
comment|// first few nodes are already out of service
if|if
condition|(
name|i
operator|>=
name|fileRepl
condition|)
block|{
name|expectedInServiceNodes
operator|--
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedInServiceNodes
argument_list|,
name|getNumDNInService
argument_list|(
name|namesystem
argument_list|)
argument_list|)
expr_stmt|;
comment|// live nodes always report load of 1.  no nodes is load 0
name|double
name|expectedXceiverAvg
init|=
operator|(
name|i
operator|==
name|nodes
operator|-
literal|1
operator|)
condition|?
literal|0.0
else|:
literal|1.0
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|double
operator|)
name|expectedXceiverAvg
argument_list|,
name|getInServiceXceiverAverage
argument_list|(
name|namesystem
argument_list|)
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
block|}
comment|// final sanity check
name|checkClusterHealth
argument_list|(
literal|0
argument_list|,
name|namesystem
argument_list|,
literal|0.0
argument_list|,
literal|0
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
finally|finally
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
DECL|method|startDecommissionOrMaintenance (DatanodeManager dnm, DatanodeDescriptor dnd, boolean decomm)
specifier|private
name|void
name|startDecommissionOrMaintenance
parameter_list|(
name|DatanodeManager
name|dnm
parameter_list|,
name|DatanodeDescriptor
name|dnd
parameter_list|,
name|boolean
name|decomm
parameter_list|)
block|{
if|if
condition|(
name|decomm
condition|)
block|{
name|dnm
operator|.
name|getDecomManager
argument_list|()
operator|.
name|startDecommission
argument_list|(
name|dnd
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dnm
operator|.
name|getDecomManager
argument_list|()
operator|.
name|startMaintenance
argument_list|(
name|dnd
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stopDecommissionOrMaintenance (DatanodeManager dnm, DatanodeDescriptor dnd, boolean decomm)
specifier|private
name|void
name|stopDecommissionOrMaintenance
parameter_list|(
name|DatanodeManager
name|dnm
parameter_list|,
name|DatanodeDescriptor
name|dnd
parameter_list|,
name|boolean
name|decomm
parameter_list|)
block|{
if|if
condition|(
name|decomm
condition|)
block|{
name|dnm
operator|.
name|getDecomManager
argument_list|()
operator|.
name|stopDecommission
argument_list|(
name|dnd
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dnm
operator|.
name|getDecomManager
argument_list|()
operator|.
name|stopMaintenance
argument_list|(
name|dnd
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkClusterHealth ( int numOfLiveNodes, FSNamesystem namesystem, double expectedTotalLoad, int expectedInServiceNodes, double expectedInServiceLoad)
specifier|private
specifier|static
name|void
name|checkClusterHealth
parameter_list|(
name|int
name|numOfLiveNodes
parameter_list|,
name|FSNamesystem
name|namesystem
parameter_list|,
name|double
name|expectedTotalLoad
parameter_list|,
name|int
name|expectedInServiceNodes
parameter_list|,
name|double
name|expectedInServiceLoad
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|numOfLiveNodes
argument_list|,
name|namesystem
operator|.
name|getNumLiveDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedInServiceNodes
argument_list|,
name|getNumDNInService
argument_list|(
name|namesystem
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedTotalLoad
argument_list|,
name|namesystem
operator|.
name|getTotalLoad
argument_list|()
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedInServiceNodes
operator|!=
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|expectedInServiceLoad
operator|/
name|expectedInServiceNodes
argument_list|,
name|getInServiceXceiverAverage
argument_list|(
name|namesystem
argument_list|)
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|getInServiceXceiverAverage
argument_list|(
name|namesystem
argument_list|)
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getNumDNInService (FSNamesystem fsn)
specifier|private
specifier|static
name|int
name|getNumDNInService
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|)
block|{
return|return
name|fsn
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getFSClusterStats
argument_list|()
operator|.
name|getNumDatanodesInService
argument_list|()
return|;
block|}
DECL|method|getInServiceXceiverAverage (FSNamesystem fsn)
specifier|private
specifier|static
name|double
name|getInServiceXceiverAverage
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|)
block|{
return|return
name|fsn
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getFSClusterStats
argument_list|()
operator|.
name|getInServiceXceiverAverage
argument_list|()
return|;
block|}
DECL|method|triggerHeartbeats (List<DataNode> datanodes)
specifier|private
name|void
name|triggerHeartbeats
parameter_list|(
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
for|for
control|(
name|DataNode
name|dn
range|:
name|datanodes
control|)
block|{
name|DataNodeTestUtils
operator|.
name|triggerHeartbeat
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

