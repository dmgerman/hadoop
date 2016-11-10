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
name|FSDataOutputStream
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
name|protocol
operator|.
name|LocatedBlock
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
name|Before
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
comment|/**  * Tests that StoragePolicySatisfier daemon is able to check the blocks to be  * moved and finding its suggested target locations to move.  */
end_comment

begin_class
DECL|class|TestStoragePolicySatisfier
specifier|public
class|class
name|TestStoragePolicySatisfier
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestStoragePolicySatisfier
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Configuration
name|config
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
DECL|field|hdfsCluster
specifier|private
name|MiniDFSCluster
name|hdfsCluster
init|=
literal|null
decl_stmt|;
DECL|field|numOfDatanodes
specifier|final
specifier|private
name|int
name|numOfDatanodes
init|=
literal|3
decl_stmt|;
DECL|field|storagesPerDatanode
specifier|final
specifier|private
name|int
name|storagesPerDatanode
init|=
literal|2
decl_stmt|;
DECL|field|capacity
specifier|final
specifier|private
name|long
name|capacity
init|=
literal|2
operator|*
literal|256
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|file
specifier|final
specifier|private
name|String
name|file
init|=
literal|"/testMoveWhenStoragePolicyNotSatisfying"
decl_stmt|;
DECL|field|distributedFS
specifier|private
name|DistributedFileSystem
name|distributedFS
init|=
literal|null
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
name|config
operator|.
name|setLong
argument_list|(
literal|"dfs.block.size"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|hdfsCluster
operator|=
name|startCluster
argument_list|(
name|config
argument_list|,
name|allDiskTypes
argument_list|,
name|numOfDatanodes
argument_list|,
name|storagesPerDatanode
argument_list|,
name|capacity
argument_list|)
expr_stmt|;
name|distributedFS
operator|=
name|hdfsCluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|writeContent
argument_list|(
name|distributedFS
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testWhenStoragePolicySetToCOLD ()
specifier|public
name|void
name|testWhenStoragePolicySetToCOLD
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// Change policy to ALL_SSD
name|distributedFS
operator|.
name|setStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"COLD"
argument_list|)
expr_stmt|;
name|FSNamesystem
name|namesystem
init|=
name|hdfsCluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|INode
name|inode
init|=
name|namesystem
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|StorageType
index|[]
index|[]
name|newtypes
init|=
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|,
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|,
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|}
decl_stmt|;
name|startAdditionalDNs
argument_list|(
name|config
argument_list|,
literal|3
argument_list|,
name|numOfDatanodes
argument_list|,
name|newtypes
argument_list|,
name|storagesPerDatanode
argument_list|,
name|capacity
argument_list|,
name|hdfsCluster
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|satisfyStoragePolicy
argument_list|(
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|hdfsCluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// Wait till namenode notified about the block location details
name|waitExpectedStorageType
argument_list|(
name|file
argument_list|,
name|StorageType
operator|.
name|ARCHIVE
argument_list|,
name|distributedFS
argument_list|,
literal|3
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|hdfsCluster
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
literal|300000
argument_list|)
DECL|method|testWhenStoragePolicySetToALLSSD ()
specifier|public
name|void
name|testWhenStoragePolicySetToALLSSD
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// Change policy to ALL_SSD
name|distributedFS
operator|.
name|setStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"ALL_SSD"
argument_list|)
expr_stmt|;
name|FSNamesystem
name|namesystem
init|=
name|hdfsCluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|INode
name|inode
init|=
name|namesystem
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|StorageType
index|[]
index|[]
name|newtypes
init|=
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
decl_stmt|;
comment|// Making sure SDD based nodes added to cluster. Adding SSD based
comment|// datanodes.
name|startAdditionalDNs
argument_list|(
name|config
argument_list|,
literal|3
argument_list|,
name|numOfDatanodes
argument_list|,
name|newtypes
argument_list|,
name|storagesPerDatanode
argument_list|,
name|capacity
argument_list|,
name|hdfsCluster
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|satisfyStoragePolicy
argument_list|(
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|hdfsCluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// Wait till StorgePolicySatisfier Identified that block to move to SSD
comment|// areas
name|waitExpectedStorageType
argument_list|(
name|file
argument_list|,
name|StorageType
operator|.
name|SSD
argument_list|,
name|distributedFS
argument_list|,
literal|3
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|hdfsCluster
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
literal|300000
argument_list|)
DECL|method|testWhenStoragePolicySetToONESSD ()
specifier|public
name|void
name|testWhenStoragePolicySetToONESSD
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// Change policy to ONE_SSD
name|distributedFS
operator|.
name|setStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"ONE_SSD"
argument_list|)
expr_stmt|;
name|FSNamesystem
name|namesystem
init|=
name|hdfsCluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|INode
name|inode
init|=
name|namesystem
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|StorageType
index|[]
index|[]
name|newtypes
init|=
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
decl_stmt|;
comment|// Making sure SDD based nodes added to cluster. Adding SSD based
comment|// datanodes.
name|startAdditionalDNs
argument_list|(
name|config
argument_list|,
literal|1
argument_list|,
name|numOfDatanodes
argument_list|,
name|newtypes
argument_list|,
name|storagesPerDatanode
argument_list|,
name|capacity
argument_list|,
name|hdfsCluster
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|satisfyStoragePolicy
argument_list|(
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|hdfsCluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// Wait till StorgePolicySatisfier Identified that block to move to SSD
comment|// areas
name|waitExpectedStorageType
argument_list|(
name|file
argument_list|,
name|StorageType
operator|.
name|SSD
argument_list|,
name|distributedFS
argument_list|,
literal|1
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
name|waitExpectedStorageType
argument_list|(
name|file
argument_list|,
name|StorageType
operator|.
name|DISK
argument_list|,
name|distributedFS
argument_list|,
literal|2
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|hdfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests to verify that the block storage movement results will be propagated    * to Namenode via datanode heartbeat.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testPerTrackIdBlocksStorageMovementResults ()
specifier|public
name|void
name|testPerTrackIdBlocksStorageMovementResults
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// Change policy to ONE_SSD
name|distributedFS
operator|.
name|setStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"ONE_SSD"
argument_list|)
expr_stmt|;
name|FSNamesystem
name|namesystem
init|=
name|hdfsCluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|INode
name|inode
init|=
name|namesystem
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|StorageType
index|[]
index|[]
name|newtypes
init|=
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
decl_stmt|;
comment|// Making sure SDD based nodes added to cluster. Adding SSD based
comment|// datanodes.
name|startAdditionalDNs
argument_list|(
name|config
argument_list|,
literal|1
argument_list|,
name|numOfDatanodes
argument_list|,
name|newtypes
argument_list|,
name|storagesPerDatanode
argument_list|,
name|capacity
argument_list|,
name|hdfsCluster
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|satisfyStoragePolicy
argument_list|(
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|hdfsCluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// Wait till the block is moved to SSD areas
name|waitExpectedStorageType
argument_list|(
name|file
argument_list|,
name|StorageType
operator|.
name|SSD
argument_list|,
name|distributedFS
argument_list|,
literal|1
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
name|waitExpectedStorageType
argument_list|(
name|file
argument_list|,
name|StorageType
operator|.
name|DISK
argument_list|,
name|distributedFS
argument_list|,
literal|2
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
name|waitForBlocksMovementResult
argument_list|(
literal|1
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|hdfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|waitForBlocksMovementResult (int expectedResultsCount, int timeout)
specifier|private
name|void
name|waitForBlocksMovementResult
parameter_list|(
name|int
name|expectedResultsCount
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|BlockManager
name|blockManager
init|=
name|hdfsCluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
specifier|final
name|StoragePolicySatisfier
name|sps
init|=
name|blockManager
operator|.
name|getStoragePolicySatisfier
argument_list|()
decl_stmt|;
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
literal|"expectedResultsCount={} actualResultsCount={}"
argument_list|,
name|expectedResultsCount
argument_list|,
name|sps
operator|.
name|getAttemptedItemsMonitor
argument_list|()
operator|.
name|resultsCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|expectedResultsCount
operator|==
name|sps
operator|.
name|getAttemptedItemsMonitor
argument_list|()
operator|.
name|resultsCount
argument_list|()
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
DECL|method|writeContent (final DistributedFileSystem dfs, final String fileName)
specifier|private
name|void
name|writeContent
parameter_list|(
specifier|final
name|DistributedFileSystem
name|dfs
parameter_list|,
specifier|final
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write to DISK
specifier|final
name|FSDataOutputStream
name|out
init|=
name|dfs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeChars
argument_list|(
literal|"t"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|startAdditionalDNs (final Configuration conf, int newNodesRequired, int existingNodesNum, StorageType[][] newTypes, int storagesPerDatanode, long capacity, final MiniDFSCluster cluster)
specifier|private
name|void
name|startAdditionalDNs
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
name|int
name|newNodesRequired
parameter_list|,
name|int
name|existingNodesNum
parameter_list|,
name|StorageType
index|[]
index|[]
name|newTypes
parameter_list|,
name|int
name|storagesPerDatanode
parameter_list|,
name|long
name|capacity
parameter_list|,
specifier|final
name|MiniDFSCluster
name|cluster
parameter_list|)
throws|throws
name|IOException
block|{
name|long
index|[]
index|[]
name|capacities
decl_stmt|;
name|existingNodesNum
operator|+=
name|newNodesRequired
expr_stmt|;
name|capacities
operator|=
operator|new
name|long
index|[
name|newNodesRequired
index|]
index|[
name|storagesPerDatanode
index|]
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
name|newNodesRequired
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
name|storagesPerDatanode
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
name|capacity
expr_stmt|;
block|}
block|}
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
name|newNodesRequired
argument_list|,
name|newTypes
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|capacities
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
block|}
DECL|method|startCluster (final Configuration conf, StorageType[][] storageTypes, int numberOfDatanodes, int storagesPerDn, long nodeCapacity)
specifier|private
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
return|return
name|cluster
return|;
block|}
comment|// Check whether the Block movement has been successfully completed to satisfy
comment|// the storage policy for the given file.
DECL|method|waitExpectedStorageType (final String fileName, final StorageType expectedStorageType, final DistributedFileSystem dfs, int expectedStorageCount, int timeout)
specifier|private
name|void
name|waitExpectedStorageType
parameter_list|(
specifier|final
name|String
name|fileName
parameter_list|,
specifier|final
name|StorageType
name|expectedStorageType
parameter_list|,
specifier|final
name|DistributedFileSystem
name|dfs
parameter_list|,
name|int
name|expectedStorageCount
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|Exception
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
name|LocatedBlock
name|lb
init|=
literal|null
decl_stmt|;
try|try
block|{
name|lb
operator|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|fileName
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|0
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
literal|"Exception while getting located blocks"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|int
name|actualStorageCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StorageType
name|storageType
range|:
name|lb
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|expectedStorageType
operator|==
name|storageType
condition|)
block|{
name|actualStorageCount
operator|++
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
name|expectedStorageType
operator|+
literal|" replica count, expected={} and actual={}"
argument_list|,
name|expectedStorageType
argument_list|,
name|actualStorageCount
argument_list|)
expr_stmt|;
return|return
name|expectedStorageCount
operator|==
name|actualStorageCount
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
block|}
end_class

end_unit

