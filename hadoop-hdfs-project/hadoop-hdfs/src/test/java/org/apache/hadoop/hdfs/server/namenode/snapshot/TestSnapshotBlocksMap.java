begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
operator|.
name|snapshot
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
name|assertNull
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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockCollection
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
name|namenode
operator|.
name|FSDirectory
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
name|INodeFile
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

begin_comment
comment|/**  * Test cases for snapshot-related information in blocksMap.  */
end_comment

begin_class
DECL|class|TestSnapshotBlocksMap
specifier|public
class|class
name|TestSnapshotBlocksMap
block|{
DECL|field|seed
specifier|private
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|BLOCKSIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCKSIZE
init|=
literal|1024
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/TestSnapshot"
argument_list|)
decl_stmt|;
DECL|field|sub1
specifier|private
specifier|final
name|Path
name|sub1
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|protected
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|protected
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fsn
specifier|protected
name|FSNamesystem
name|fsn
decl_stmt|;
DECL|field|hdfs
specifier|protected
name|DistributedFileSystem
name|hdfs
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCKSIZE
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
name|REPLICATION
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
comment|/**    * Test deleting a file with snapshots. Need to check the blocksMap to make    * sure the corresponding record is updated correctly.    */
annotation|@
name|Test
DECL|method|testDeletionWithSnapshots ()
specifier|public
name|void
name|testDeletionWithSnapshots
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file0
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file0"
argument_list|)
decl_stmt|;
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
name|Path
name|subsub1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
name|Path
name|subfile0
init|=
operator|new
name|Path
argument_list|(
name|subsub1
argument_list|,
literal|"file0"
argument_list|)
decl_stmt|;
comment|// Create file under sub1
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file0
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|subfile0
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|BlockManager
name|bm
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|FSDirectory
name|dir
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
name|INodeFile
name|inodeForDeletedFile
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|dir
operator|.
name|getINode
argument_list|(
name|subfile0
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|subfile0
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|BlockInfo
index|[]
name|blocksForDeletedFile
init|=
name|inodeForDeletedFile
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|blocksForDeletedFile
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|BlockCollection
name|bcForDeletedFile
init|=
name|bm
operator|.
name|getBlockCollection
argument_list|(
name|blocksForDeletedFile
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|bcForDeletedFile
argument_list|)
expr_stmt|;
comment|// Normal deletion
name|hdfs
operator|.
name|delete
argument_list|(
name|subsub1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|bcForDeletedFile
operator|=
name|bm
operator|.
name|getBlockCollection
argument_list|(
name|blocksForDeletedFile
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// The INode should have been removed from the blocksMap
name|assertNull
argument_list|(
name|bcForDeletedFile
argument_list|)
expr_stmt|;
comment|// Create snapshots for sub1
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
comment|// Check the block information for file0
comment|// Get the INode for file0
name|INodeFile
name|inode
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|dir
operator|.
name|getINode
argument_list|(
name|file0
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|file0
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|BlockInfo
index|[]
name|blocks
init|=
name|inode
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
comment|// Get the INode for the first block from blocksMap
name|BlockCollection
name|bc
init|=
name|bm
operator|.
name|getBlockCollection
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// The two INode should be the same one
name|assertTrue
argument_list|(
name|bc
operator|==
name|inode
argument_list|)
expr_stmt|;
comment|// Also check the block information for snapshot of file0
name|Path
name|snapshotFile0
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|sub1
argument_list|,
literal|"s0"
argument_list|,
name|file0
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|INodeFile
name|ssINode0
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|dir
operator|.
name|getINode
argument_list|(
name|snapshotFile0
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|snapshotFile0
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|BlockInfo
index|[]
name|ssBlocks
init|=
name|ssINode0
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
comment|// The snapshot of file1 should contain 1 block
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ssBlocks
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Delete file0
name|hdfs
operator|.
name|delete
argument_list|(
name|file0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Make sure the first block of file0 is still in blocksMap
name|BlockInfo
name|blockInfoAfterDeletion
init|=
name|bm
operator|.
name|getStoredBlock
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|blockInfoAfterDeletion
argument_list|)
expr_stmt|;
comment|// Check the INode information
name|BlockCollection
name|bcAfterDeletion
init|=
name|blockInfoAfterDeletion
operator|.
name|getBlockCollection
argument_list|()
decl_stmt|;
comment|// Compare the INode in the blocksMap with INodes for snapshots
name|Path
name|snapshot1File0
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|,
name|file0
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|INodeFile
name|ssINode1
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|dir
operator|.
name|getINode
argument_list|(
name|snapshot1File0
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|snapshot1File0
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bcAfterDeletion
operator|==
name|ssINode0
operator|||
name|bcAfterDeletion
operator|==
name|ssINode1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bcAfterDeletion
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Delete snapshot s1
name|hdfs
operator|.
name|deleteSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
comment|// Make sure the first block of file0 is still in blocksMap
name|BlockInfo
name|blockInfoAfterSnapshotDeletion
init|=
name|bm
operator|.
name|getStoredBlock
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|blockInfoAfterSnapshotDeletion
argument_list|)
expr_stmt|;
name|BlockCollection
name|bcAfterSnapshotDeletion
init|=
name|blockInfoAfterSnapshotDeletion
operator|.
name|getBlockCollection
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|bcAfterSnapshotDeletion
operator|==
name|ssINode0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bcAfterSnapshotDeletion
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
try|try
block|{
name|ssINode1
operator|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|dir
operator|.
name|getINode
argument_list|(
name|snapshot1File0
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|snapshot1File0
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expect FileNotFoundException when identifying the INode in a deleted Snapshot"
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
literal|"File does not exist: "
operator|+
name|snapshot1File0
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: test for deletion file which was appended after taking snapshots
block|}
end_class

end_unit

