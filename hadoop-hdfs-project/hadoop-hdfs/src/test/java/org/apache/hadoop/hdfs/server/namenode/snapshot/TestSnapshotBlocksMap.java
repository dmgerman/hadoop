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
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
operator|.
name|assertExceptionContains
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
name|protocol
operator|.
name|ExtendedBlock
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
DECL|field|fsdir
name|FSDirectory
name|fsdir
decl_stmt|;
DECL|field|blockmanager
name|BlockManager
name|blockmanager
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
name|fsdir
operator|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
expr_stmt|;
name|blockmanager
operator|=
name|fsn
operator|.
name|getBlockManager
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
DECL|method|assertAllNull (INodeFile inode, Path path, String[] snapshots)
name|void
name|assertAllNull
parameter_list|(
name|INodeFile
name|inode
parameter_list|,
name|Path
name|path
parameter_list|,
name|String
index|[]
name|snapshots
parameter_list|)
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertNull
argument_list|(
name|inode
operator|.
name|getBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|assertINodeNull
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertINodeNullInSnapshots
argument_list|(
name|path
argument_list|,
name|snapshots
argument_list|)
expr_stmt|;
block|}
DECL|method|assertINodeNull (String path)
name|void
name|assertINodeNull
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertNull
argument_list|(
name|fsdir
operator|.
name|getINode
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertINodeNullInSnapshots (Path path, String... snapshots)
name|void
name|assertINodeNullInSnapshots
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
modifier|...
name|snapshots
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|s
range|:
name|snapshots
control|)
block|{
name|assertINodeNull
argument_list|(
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|,
name|s
argument_list|,
name|path
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertBlockCollection (String path, int numBlocks, final FSDirectory dir, final BlockManager blkManager)
specifier|static
name|INodeFile
name|assertBlockCollection
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|numBlocks
parameter_list|,
specifier|final
name|FSDirectory
name|dir
parameter_list|,
specifier|final
name|BlockManager
name|blkManager
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|INodeFile
name|file
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|dir
operator|.
name|getINode
argument_list|(
name|path
argument_list|)
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numBlocks
argument_list|,
name|file
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|BlockInfo
name|b
range|:
name|file
operator|.
name|getBlocks
argument_list|()
control|)
block|{
name|assertBlockCollection
argument_list|(
name|blkManager
argument_list|,
name|file
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
return|return
name|file
return|;
block|}
DECL|method|assertBlockCollection (final BlockManager blkManager, final INodeFile file, final BlockInfo b)
specifier|static
name|void
name|assertBlockCollection
parameter_list|(
specifier|final
name|BlockManager
name|blkManager
parameter_list|,
specifier|final
name|INodeFile
name|file
parameter_list|,
specifier|final
name|BlockInfo
name|b
parameter_list|)
block|{
name|Assert
operator|.
name|assertSame
argument_list|(
name|b
argument_list|,
name|blkManager
operator|.
name|getStoredBlock
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertSame
argument_list|(
name|file
argument_list|,
name|blkManager
operator|.
name|getBlockCollection
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertSame
argument_list|(
name|file
argument_list|,
name|b
operator|.
name|getBlockCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test deleting a file with snapshots. Need to check the blocksMap to make    * sure the corresponding record is updated correctly.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
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
name|sub2
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub2"
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|sub2
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
name|Path
name|file3
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file3"
argument_list|)
decl_stmt|;
name|Path
name|file4
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file4"
argument_list|)
decl_stmt|;
name|Path
name|file5
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file5"
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
literal|4
operator|*
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
literal|2
operator|*
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
name|file2
argument_list|,
literal|3
operator|*
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// Normal deletion
block|{
specifier|final
name|INodeFile
name|f2
init|=
name|assertBlockCollection
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
literal|3
argument_list|,
name|fsdir
argument_list|,
name|blockmanager
argument_list|)
decl_stmt|;
name|BlockInfo
index|[]
name|blocks
init|=
name|f2
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|hdfs
operator|.
name|delete
argument_list|(
name|sub2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// The INode should have been removed from the blocksMap
for|for
control|(
name|BlockInfo
name|b
range|:
name|blocks
control|)
block|{
name|assertNull
argument_list|(
name|blockmanager
operator|.
name|getBlockCollection
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Create snapshots for sub1
specifier|final
name|String
index|[]
name|snapshots
init|=
block|{
literal|"s0"
block|,
literal|"s1"
block|,
literal|"s2"
block|}
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file3
argument_list|,
literal|5
operator|*
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
name|snapshots
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file4
argument_list|,
literal|1
operator|*
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
name|snapshots
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file5
argument_list|,
literal|7
operator|*
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
name|snapshots
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// set replication so that the inode should be replaced for snapshots
block|{
name|INodeFile
name|f1
init|=
name|assertBlockCollection
argument_list|(
name|file1
operator|.
name|toString
argument_list|()
argument_list|,
literal|2
argument_list|,
name|fsdir
argument_list|,
name|blockmanager
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertSame
argument_list|(
name|INodeFile
operator|.
name|class
argument_list|,
name|f1
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setReplication
argument_list|(
name|file1
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|f1
operator|=
name|assertBlockCollection
argument_list|(
name|file1
operator|.
name|toString
argument_list|()
argument_list|,
literal|2
argument_list|,
name|fsdir
argument_list|,
name|blockmanager
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertSame
argument_list|(
name|INodeFileWithSnapshot
operator|.
name|class
argument_list|,
name|f1
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check the block information for file0
specifier|final
name|INodeFile
name|f0
init|=
name|assertBlockCollection
argument_list|(
name|file0
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|,
name|fsdir
argument_list|,
name|blockmanager
argument_list|)
decl_stmt|;
name|BlockInfo
index|[]
name|blocks0
init|=
name|f0
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
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
name|assertBlockCollection
argument_list|(
name|snapshotFile0
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|,
name|fsdir
argument_list|,
name|blockmanager
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
comment|// Make sure the blocks of file0 is still in blocksMap
for|for
control|(
name|BlockInfo
name|b
range|:
name|blocks0
control|)
block|{
name|assertNotNull
argument_list|(
name|blockmanager
operator|.
name|getBlockCollection
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertBlockCollection
argument_list|(
name|snapshotFile0
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|,
name|fsdir
argument_list|,
name|blockmanager
argument_list|)
expr_stmt|;
comment|// Compare the INode in the blocksMap with INodes for snapshots
name|String
name|s1f0
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
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertBlockCollection
argument_list|(
name|s1f0
argument_list|,
literal|4
argument_list|,
name|fsdir
argument_list|,
name|blockmanager
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
for|for
control|(
name|BlockInfo
name|b
range|:
name|blocks0
control|)
block|{
name|assertNotNull
argument_list|(
name|blockmanager
operator|.
name|getBlockCollection
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertBlockCollection
argument_list|(
name|snapshotFile0
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|,
name|fsdir
argument_list|,
name|blockmanager
argument_list|)
expr_stmt|;
try|try
block|{
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|fsdir
operator|.
name|getINode
argument_list|(
name|s1f0
argument_list|)
argument_list|,
name|s1f0
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
name|assertExceptionContains
argument_list|(
literal|"File does not exist: "
operator|+
name|s1f0
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * Try to read the files inside snapshot but deleted in original place after    * restarting post checkpoint. refer HDFS-5427    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testReadSnapshotFileWithCheckpoint ()
specifier|public
name|void
name|testReadSnapshotFileWithCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
literal|"/foo/bar"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|bar
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|100024L
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|foo
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|delete
argument_list|(
name|bar
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// checkpoint
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nameNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
comment|// restart namenode to load snapshot files from fsimage
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|snapshotPath
init|=
name|Snapshot
operator|.
name|getSnapshotPath
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|,
literal|"s1/bar"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|hdfs
argument_list|,
operator|new
name|Path
argument_list|(
name|snapshotPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * Try to read the files inside snapshot but renamed to different file and    * deleted after restarting post checkpoint. refer HDFS-5427    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testReadRenamedSnapshotFileWithCheckpoint ()
specifier|public
name|void
name|testReadRenamedSnapshotFileWithCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|foo2
init|=
operator|new
name|Path
argument_list|(
literal|"/foo2"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|foo2
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|foo2
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar2
init|=
operator|new
name|Path
argument_list|(
name|foo2
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|bar
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|100024L
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|foo
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
comment|// rename to another snapshottable directory and take snapshot
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|rename
argument_list|(
name|bar
argument_list|,
name|bar2
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|foo2
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
comment|// delete the original renamed file to make sure blocks are not updated by
comment|// the original file
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|delete
argument_list|(
name|bar2
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// checkpoint
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nameNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
comment|// restart namenode to load snapshot files from fsimage
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// file in first snapshot
name|String
name|barSnapshotPath
init|=
name|Snapshot
operator|.
name|getSnapshotPath
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|,
literal|"s1/bar"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|hdfs
argument_list|,
operator|new
name|Path
argument_list|(
name|barSnapshotPath
argument_list|)
argument_list|)
expr_stmt|;
comment|// file in second snapshot after rename+delete
name|String
name|bar2SnapshotPath
init|=
name|Snapshot
operator|.
name|getSnapshotPath
argument_list|(
name|foo2
operator|.
name|toString
argument_list|()
argument_list|,
literal|"s2/bar"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|hdfs
argument_list|,
operator|new
name|Path
argument_list|(
name|bar2SnapshotPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make sure we delete 0-sized block when deleting an INodeFileUCWithSnapshot    */
annotation|@
name|Test
DECL|method|testDeletionWithZeroSizeBlock ()
specifier|public
name|void
name|testDeletionWithZeroSizeBlock
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|bar
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|foo
argument_list|,
literal|"s0"
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|append
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|INodeFile
name|barNode
init|=
name|fsdir
operator|.
name|getINode4Write
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|BlockInfo
index|[]
name|blks
init|=
name|barNode
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|blks
index|[
literal|0
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|previous
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|fsn
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|blks
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|addBlock
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|,
name|hdfs
operator|.
name|getClient
argument_list|()
operator|.
name|getClientName
argument_list|()
argument_list|,
name|previous
argument_list|,
literal|null
argument_list|,
name|barNode
operator|.
name|getId
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|foo
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|barNode
operator|=
name|fsdir
operator|.
name|getINode4Write
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
expr_stmt|;
name|blks
operator|=
name|barNode
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|blks
index|[
literal|0
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blks
index|[
literal|1
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|delete
argument_list|(
name|bar
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|sbar
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|foo
argument_list|,
literal|"s1"
argument_list|,
name|bar
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|barNode
operator|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sbar
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
expr_stmt|;
name|blks
operator|=
name|barNode
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|blks
index|[
literal|0
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Make sure we delete 0-sized block when deleting an INodeFileUC */
annotation|@
name|Test
DECL|method|testDeletionWithZeroSizeBlock2 ()
specifier|public
name|void
name|testDeletionWithZeroSizeBlock2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|subDir
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"sub"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|subDir
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|bar
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|append
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|INodeFile
name|barNode
init|=
name|fsdir
operator|.
name|getINode4Write
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|BlockInfo
index|[]
name|blks
init|=
name|barNode
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|previous
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|fsn
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|blks
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|addBlock
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|,
name|hdfs
operator|.
name|getClient
argument_list|()
operator|.
name|getClientName
argument_list|()
argument_list|,
name|previous
argument_list|,
literal|null
argument_list|,
name|barNode
operator|.
name|getId
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|foo
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|barNode
operator|=
name|fsdir
operator|.
name|getINode4Write
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
expr_stmt|;
name|blks
operator|=
name|barNode
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|blks
index|[
literal|0
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blks
index|[
literal|1
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|delete
argument_list|(
name|subDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|sbar
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|foo
argument_list|,
literal|"s1"
argument_list|,
literal|"sub/bar"
argument_list|)
decl_stmt|;
name|barNode
operator|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sbar
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
expr_stmt|;
name|blks
operator|=
name|barNode
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|blks
index|[
literal|0
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * 1. rename under-construction file with 0-sized blocks after snapshot.    * 2. delete the renamed directory.    * make sure we delete the 0-sized block.    * see HDFS-5476.    */
annotation|@
name|Test
DECL|method|testDeletionWithZeroSizeBlock3 ()
specifier|public
name|void
name|testDeletionWithZeroSizeBlock3
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|subDir
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"sub"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|subDir
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|bar
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|append
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|INodeFile
name|barNode
init|=
name|fsdir
operator|.
name|getINode4Write
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|BlockInfo
index|[]
name|blks
init|=
name|barNode
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|previous
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|fsn
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|blks
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|addBlock
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|,
name|hdfs
operator|.
name|getClient
argument_list|()
operator|.
name|getClientName
argument_list|()
argument_list|,
name|previous
argument_list|,
literal|null
argument_list|,
name|barNode
operator|.
name|getId
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|foo
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
comment|// rename bar
specifier|final
name|Path
name|bar2
init|=
operator|new
name|Path
argument_list|(
name|subDir
argument_list|,
literal|"bar2"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|rename
argument_list|(
name|bar
argument_list|,
name|bar2
argument_list|)
expr_stmt|;
name|INodeFile
name|bar2Node
init|=
name|fsdir
operator|.
name|getINode4Write
argument_list|(
name|bar2
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|blks
operator|=
name|bar2Node
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|blks
index|[
literal|0
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blks
index|[
literal|1
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// delete subDir
name|hdfs
operator|.
name|delete
argument_list|(
name|subDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|sbar
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|foo
argument_list|,
literal|"s1"
argument_list|,
literal|"sub/bar"
argument_list|)
decl_stmt|;
name|barNode
operator|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sbar
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
expr_stmt|;
name|blks
operator|=
name|barNode
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|blks
index|[
literal|0
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

