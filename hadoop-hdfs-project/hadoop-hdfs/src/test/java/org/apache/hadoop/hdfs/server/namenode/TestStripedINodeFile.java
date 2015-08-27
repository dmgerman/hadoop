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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|permission
operator|.
name|PermissionStatus
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
name|BlockStoragePolicy
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
name|ErasureCodingPolicy
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
name|blockmanagement
operator|.
name|BlockInfo
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
name|BlockInfoStriped
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
name|BlockStoragePolicySuite
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * This class tests INodeFile with striped feature.  */
end_comment

begin_class
DECL|class|TestStripedINodeFile
specifier|public
class|class
name|TestStripedINodeFile
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
name|TestINodeFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|perm
specifier|private
specifier|static
specifier|final
name|PermissionStatus
name|perm
init|=
operator|new
name|PermissionStatus
argument_list|(
literal|"userName"
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|defaultSuite
specifier|private
specifier|final
name|BlockStoragePolicySuite
name|defaultSuite
init|=
name|BlockStoragePolicySuite
operator|.
name|createDefaultSuite
argument_list|()
decl_stmt|;
DECL|field|defaultPolicy
specifier|private
specifier|final
name|BlockStoragePolicy
name|defaultPolicy
init|=
name|defaultSuite
operator|.
name|getDefaultPolicy
argument_list|()
decl_stmt|;
DECL|field|testECPolicy
specifier|private
specifier|static
specifier|final
name|ErasureCodingPolicy
name|testECPolicy
init|=
name|ErasureCodingPolicyManager
operator|.
name|getSystemDefaultPolicy
argument_list|()
decl_stmt|;
DECL|method|createStripedINodeFile ()
specifier|private
specifier|static
name|INodeFile
name|createStripedINodeFile
parameter_list|()
block|{
return|return
operator|new
name|INodeFile
argument_list|(
name|HdfsConstants
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
literal|null
argument_list|,
name|perm
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|null
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|,
literal|1024L
argument_list|,
name|HdfsServerConstants
operator|.
name|COLD_STORAGE_POLICY_ID
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testBlockStripedFeature ()
specifier|public
name|void
name|testBlockStripedFeature
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|INodeFile
name|inf
init|=
name|createStripedINodeFile
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|inf
operator|.
name|isStriped
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockStripedTotalBlockCount ()
specifier|public
name|void
name|testBlockStripedTotalBlockCount
parameter_list|()
block|{
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|blockInfoStriped
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk
argument_list|,
name|testECPolicy
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|blockInfoStriped
operator|.
name|getTotalBlockNum
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockStripedLength ()
specifier|public
name|void
name|testBlockStripedLength
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|INodeFile
name|inf
init|=
name|createStripedINodeFile
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|blockInfoStriped
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk
argument_list|,
name|testECPolicy
argument_list|)
decl_stmt|;
name|inf
operator|.
name|addBlock
argument_list|(
name|blockInfoStriped
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|inf
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockStripedConsumedSpace ()
specifier|public
name|void
name|testBlockStripedConsumedSpace
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|INodeFile
name|inf
init|=
name|createStripedINodeFile
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|blockInfoStriped
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk
argument_list|,
name|testECPolicy
argument_list|)
decl_stmt|;
name|blockInfoStriped
operator|.
name|setNumBytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|inf
operator|.
name|addBlock
argument_list|(
name|blockInfoStriped
argument_list|)
expr_stmt|;
comment|//   0. Calculate the total bytes per stripes<Num Bytes per Stripes>
comment|//   1. Calculate the number of stripes in this block group.<Num Stripes>
comment|//   2. Calculate the last remaining length which does not make a stripe.<Last Stripe Length>
comment|//   3. Total consumed space is the total of
comment|//     a. The total of the full cells of data blocks and parity blocks.
comment|//     b. The remaining of data block which does not make a stripe.
comment|//     c. The last parity block cells. These size should be same
comment|//        to the first cell in this stripe.
comment|// So the total consumed space is the sum of
comment|//  a.<Cell Size> * (<Num Stripes> - 1) *<Total Block Num> = 0
comment|//  b.<Num Bytes> %<Num Bytes per Stripes> = 1
comment|//  c.<Last Stripe Length> *<Parity Block Num> = 1 * 3
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|inf
operator|.
name|storagespaceConsumedStriped
argument_list|()
operator|.
name|getStorageSpace
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|inf
operator|.
name|storagespaceConsumed
argument_list|(
name|defaultPolicy
argument_list|)
operator|.
name|getStorageSpace
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleBlockStripedConsumedSpace ()
specifier|public
name|void
name|testMultipleBlockStripedConsumedSpace
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|INodeFile
name|inf
init|=
name|createStripedINodeFile
argument_list|()
decl_stmt|;
name|Block
name|blk1
init|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|blockInfoStriped1
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk1
argument_list|,
name|testECPolicy
argument_list|)
decl_stmt|;
name|blockInfoStriped1
operator|.
name|setNumBytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Block
name|blk2
init|=
operator|new
name|Block
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|blockInfoStriped2
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk2
argument_list|,
name|testECPolicy
argument_list|)
decl_stmt|;
name|blockInfoStriped2
operator|.
name|setNumBytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|inf
operator|.
name|addBlock
argument_list|(
name|blockInfoStriped1
argument_list|)
expr_stmt|;
name|inf
operator|.
name|addBlock
argument_list|(
name|blockInfoStriped2
argument_list|)
expr_stmt|;
comment|// This is the double size of one block in above case.
name|assertEquals
argument_list|(
literal|4
operator|*
literal|2
argument_list|,
name|inf
operator|.
name|storagespaceConsumedStriped
argument_list|()
operator|.
name|getStorageSpace
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
operator|*
literal|2
argument_list|,
name|inf
operator|.
name|storagespaceConsumed
argument_list|(
name|defaultPolicy
argument_list|)
operator|.
name|getStorageSpace
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockStripedFileSize ()
specifier|public
name|void
name|testBlockStripedFileSize
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|INodeFile
name|inf
init|=
name|createStripedINodeFile
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|blockInfoStriped
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk
argument_list|,
name|testECPolicy
argument_list|)
decl_stmt|;
name|blockInfoStriped
operator|.
name|setNumBytes
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|inf
operator|.
name|addBlock
argument_list|(
name|blockInfoStriped
argument_list|)
expr_stmt|;
comment|// Compute file size should return actual data
comment|// size which is retained by this file.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|inf
operator|.
name|computeFileSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|inf
operator|.
name|computeFileSize
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockStripedUCFileSize ()
specifier|public
name|void
name|testBlockStripedUCFileSize
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|INodeFile
name|inf
init|=
name|createStripedINodeFile
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|bInfoUCStriped
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk
argument_list|,
name|testECPolicy
argument_list|)
decl_stmt|;
name|bInfoUCStriped
operator|.
name|convertToBlockUnderConstruction
argument_list|(
name|HdfsServerConstants
operator|.
name|BlockUCState
operator|.
name|UNDER_CONSTRUCTION
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|bInfoUCStriped
operator|.
name|setNumBytes
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|inf
operator|.
name|addBlock
argument_list|(
name|bInfoUCStriped
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|inf
operator|.
name|computeFileSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|inf
operator|.
name|computeFileSize
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockStripedComputeQuotaUsage ()
specifier|public
name|void
name|testBlockStripedComputeQuotaUsage
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|INodeFile
name|inf
init|=
name|createStripedINodeFile
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|blockInfoStriped
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk
argument_list|,
name|testECPolicy
argument_list|)
decl_stmt|;
name|blockInfoStriped
operator|.
name|setNumBytes
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|inf
operator|.
name|addBlock
argument_list|(
name|blockInfoStriped
argument_list|)
expr_stmt|;
name|QuotaCounts
name|counts
init|=
name|inf
operator|.
name|computeQuotaUsageWithStriped
argument_list|(
name|defaultPolicy
argument_list|,
operator|new
name|QuotaCounts
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counts
operator|.
name|getNameSpace
argument_list|()
argument_list|)
expr_stmt|;
comment|// The total consumed space is the sum of
comment|//  a.<Cell Size> * (<Num Stripes> - 1) *<Total Block Num> = 0
comment|//  b.<Num Bytes> %<Num Bytes per Stripes> = 100
comment|//  c.<Last Stripe Length> *<Parity Block Num> = 100 * 3
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|counts
operator|.
name|getStorageSpace
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockStripedUCComputeQuotaUsage ()
specifier|public
name|void
name|testBlockStripedUCComputeQuotaUsage
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|INodeFile
name|inf
init|=
name|createStripedINodeFile
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|bInfoUCStriped
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk
argument_list|,
name|testECPolicy
argument_list|)
decl_stmt|;
name|bInfoUCStriped
operator|.
name|convertToBlockUnderConstruction
argument_list|(
name|HdfsServerConstants
operator|.
name|BlockUCState
operator|.
name|UNDER_CONSTRUCTION
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|bInfoUCStriped
operator|.
name|setNumBytes
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|inf
operator|.
name|addBlock
argument_list|(
name|bInfoUCStriped
argument_list|)
expr_stmt|;
name|QuotaCounts
name|counts
init|=
name|inf
operator|.
name|computeQuotaUsageWithStriped
argument_list|(
name|defaultPolicy
argument_list|,
operator|new
name|QuotaCounts
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|inf
operator|.
name|getPreferredBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counts
operator|.
name|getNameSpace
argument_list|()
argument_list|)
expr_stmt|;
comment|// Consumed space in the case of BlockInfoStripedUC can be calculated
comment|// by using preferred block size. This is 1024 and total block num
comment|// is 9(= 3 + 6). Consumed storage space should be 1024 * 9 = 9216.
name|assertEquals
argument_list|(
literal|9216
argument_list|,
name|counts
operator|.
name|getStorageSpace
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the behavior of striped and contiguous block deletions.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testDeleteOp ()
specifier|public
name|void
name|testDeleteOp
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|len
init|=
literal|1024
decl_stmt|;
specifier|final
name|Path
name|parentDir
init|=
operator|new
name|Path
argument_list|(
literal|"/parentDir"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|zone
init|=
operator|new
name|Path
argument_list|(
name|parentDir
argument_list|,
literal|"zone"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|zoneFile
init|=
operator|new
name|Path
argument_list|(
name|zone
argument_list|,
literal|"zoneFile"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|contiguousFile
init|=
operator|new
name|Path
argument_list|(
name|parentDir
argument_list|,
literal|"someFile"
argument_list|)
decl_stmt|;
specifier|final
name|DistributedFileSystem
name|dfs
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|short
name|GROUP_SIZE
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
operator|+
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_XATTRS_PER_INODE_KEY
argument_list|,
literal|2
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
name|GROUP_SIZE
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
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|zone
argument_list|)
expr_stmt|;
comment|// create erasure zone
name|dfs
operator|.
name|createErasureCodingZone
argument_list|(
name|zone
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|zoneFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|contiguousFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
specifier|final
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
comment|// Case-1: Verify the behavior of striped blocks
comment|// Get blocks of striped file
name|INode
name|inodeStriped
init|=
name|fsd
operator|.
name|getINode
argument_list|(
literal|"/parentDir/zone/zoneFile"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to get INodeFile for /parentDir/zone/zoneFile"
argument_list|,
name|inodeStriped
operator|instanceof
name|INodeFile
argument_list|)
expr_stmt|;
name|INodeFile
name|inodeStripedFile
init|=
operator|(
name|INodeFile
operator|)
name|inodeStriped
decl_stmt|;
name|BlockInfo
index|[]
name|stripedBlks
init|=
name|inodeStripedFile
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
for|for
control|(
name|BlockInfo
name|blockInfo
range|:
name|stripedBlks
control|)
block|{
name|assertFalse
argument_list|(
literal|"Mistakenly marked the block as deleted!"
argument_list|,
name|blockInfo
operator|.
name|isDeleted
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// delete erasure zone directory
name|dfs
operator|.
name|delete
argument_list|(
name|zone
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|BlockInfo
name|blockInfo
range|:
name|stripedBlks
control|)
block|{
name|assertTrue
argument_list|(
literal|"Didn't mark the block as deleted!"
argument_list|,
name|blockInfo
operator|.
name|isDeleted
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Case-2: Verify the behavior of contiguous blocks
comment|// Get blocks of contiguous file
name|INode
name|inode
init|=
name|fsd
operator|.
name|getINode
argument_list|(
literal|"/parentDir/someFile"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to get INodeFile for /parentDir/someFile"
argument_list|,
name|inode
operator|instanceof
name|INodeFile
argument_list|)
expr_stmt|;
name|INodeFile
name|inodeFile
init|=
operator|(
name|INodeFile
operator|)
name|inode
decl_stmt|;
name|BlockInfo
index|[]
name|contiguousBlks
init|=
name|inodeFile
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
for|for
control|(
name|BlockInfo
name|blockInfo
range|:
name|contiguousBlks
control|)
block|{
name|assertFalse
argument_list|(
literal|"Mistakenly marked the block as deleted!"
argument_list|,
name|blockInfo
operator|.
name|isDeleted
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// delete parent directory
name|dfs
operator|.
name|delete
argument_list|(
name|parentDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|BlockInfo
name|blockInfo
range|:
name|contiguousBlks
control|)
block|{
name|assertTrue
argument_list|(
literal|"Didn't mark the block as deleted!"
argument_list|,
name|blockInfo
operator|.
name|isDeleted
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
block|}
end_class

end_unit

