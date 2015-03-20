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
name|namenode
operator|.
name|snapshot
operator|.
name|SnapshotTestHelper
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
comment|/**  * Make sure we correctly update the quota usage for truncate.  * We need to cover the following cases:  * 1. No snapshot, truncate to 0  * 2. No snapshot, truncate at block boundary  * 3. No snapshot, not on block boundary  * 4~6. With snapshot, all the current blocks are included in latest  *      snapshots, repeat 1~3  * 7~9. With snapshot, blocks in the latest snapshot and blocks in the current  *      file diverged, repeat 1~3  */
end_comment

begin_class
DECL|class|TestTruncateQuotaUpdate
specifier|public
class|class
name|TestTruncateQuotaUpdate
block|{
DECL|field|BLOCKSIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCKSIZE
init|=
literal|1024
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|4
decl_stmt|;
DECL|field|DISKQUOTA
specifier|private
specifier|static
specifier|final
name|long
name|DISKQUOTA
init|=
name|BLOCKSIZE
operator|*
literal|20
decl_stmt|;
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0L
decl_stmt|;
DECL|field|dir
specifier|private
specifier|static
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/TestTruncateQuotaUpdate"
argument_list|)
decl_stmt|;
DECL|field|file
specifier|private
specifier|static
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fsdir
specifier|private
name|FSDirectory
name|fsdir
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
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
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|fsdir
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
expr_stmt|;
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
name|dir
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setQuota
argument_list|(
name|dir
argument_list|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|,
name|DISKQUOTA
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setQuotaByStorageType
argument_list|(
name|dir
argument_list|,
name|StorageType
operator|.
name|DISK
argument_list|,
name|DISKQUOTA
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setStoragePolicy
argument_list|(
name|dir
argument_list|,
name|HdfsConstants
operator|.
name|HOT_STORAGE_POLICY_NAME
argument_list|)
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
annotation|@
name|Test
DECL|method|testTruncateQuotaUpdate ()
specifier|public
name|void
name|testTruncateQuotaUpdate
parameter_list|()
throws|throws
name|Exception
block|{    }
DECL|interface|TruncateCase
specifier|public
interface|interface
name|TruncateCase
block|{
DECL|method|prepare ()
specifier|public
name|void
name|prepare
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
DECL|method|testTruncate (long newLength, long expectedDiff, long expectedUsage)
specifier|private
name|void
name|testTruncate
parameter_list|(
name|long
name|newLength
parameter_list|,
name|long
name|expectedDiff
parameter_list|,
name|long
name|expectedUsage
parameter_list|)
throws|throws
name|Exception
block|{
comment|// before doing the real truncation, make sure the computation is correct
specifier|final
name|INodesInPath
name|iip
init|=
name|fsdir
operator|.
name|getINodesInPath4Write
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|INodeFile
name|fileNode
init|=
name|iip
operator|.
name|getLastINode
argument_list|()
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|fileNode
operator|.
name|recordModification
argument_list|(
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|long
name|diff
init|=
name|fileNode
operator|.
name|computeQuotaDeltaForTruncate
argument_list|(
name|newLength
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedDiff
argument_list|,
name|diff
argument_list|)
expr_stmt|;
comment|// do the real truncation
name|dfs
operator|.
name|truncate
argument_list|(
name|file
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
comment|// wait for truncate to finish
name|TestFileTruncate
operator|.
name|checkBlockRecovery
argument_list|(
name|file
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
specifier|final
name|INodeDirectory
name|dirNode
init|=
name|fsdir
operator|.
name|getINode4Write
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asDirectory
argument_list|()
decl_stmt|;
specifier|final
name|long
name|spaceUsed
init|=
name|dirNode
operator|.
name|getDirectoryWithQuotaFeature
argument_list|()
operator|.
name|getSpaceConsumed
argument_list|()
operator|.
name|getStorageSpace
argument_list|()
decl_stmt|;
specifier|final
name|long
name|diskUsed
init|=
name|dirNode
operator|.
name|getDirectoryWithQuotaFeature
argument_list|()
operator|.
name|getSpaceConsumed
argument_list|()
operator|.
name|getTypeSpaces
argument_list|()
operator|.
name|get
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedUsage
argument_list|,
name|spaceUsed
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedUsage
argument_list|,
name|diskUsed
argument_list|)
expr_stmt|;
block|}
comment|/**    * case 1~3    */
DECL|class|TruncateWithoutSnapshot
specifier|private
class|class
name|TruncateWithoutSnapshot
implements|implements
name|TruncateCase
block|{
annotation|@
name|Override
DECL|method|prepare ()
specifier|public
name|void
name|prepare
parameter_list|()
throws|throws
name|Exception
block|{
comment|// original file size: 2.5 block
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
operator|*
literal|2
operator|+
name|BLOCKSIZE
operator|/
literal|2
argument_list|,
name|REPLICATION
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
comment|// case 1: first truncate to 1.5 blocks
name|long
name|newLength
init|=
name|BLOCKSIZE
operator|+
name|BLOCKSIZE
operator|/
literal|2
decl_stmt|;
comment|// we truncate 1 blocks, but not on the boundary, thus the diff should
comment|// be -block + (block - 0.5 block) = -0.5 block
name|long
name|diff
init|=
operator|-
name|BLOCKSIZE
operator|/
literal|2
decl_stmt|;
comment|// the new quota usage should be BLOCKSIZE * 1.5 * replication
name|long
name|usage
init|=
operator|(
name|BLOCKSIZE
operator|+
name|BLOCKSIZE
operator|/
literal|2
operator|)
operator|*
name|REPLICATION
decl_stmt|;
name|testTruncate
argument_list|(
name|newLength
argument_list|,
name|diff
argument_list|,
name|usage
argument_list|)
expr_stmt|;
comment|// case 2: truncate to 1 block
name|newLength
operator|=
name|BLOCKSIZE
expr_stmt|;
comment|// the diff should be -0.5 block since this is not on boundary
name|diff
operator|=
operator|-
name|BLOCKSIZE
operator|/
literal|2
expr_stmt|;
comment|// after truncation the quota usage should be BLOCKSIZE * replication
name|usage
operator|=
name|BLOCKSIZE
operator|*
name|REPLICATION
expr_stmt|;
name|testTruncate
argument_list|(
name|newLength
argument_list|,
name|diff
argument_list|,
name|usage
argument_list|)
expr_stmt|;
comment|// case 3: truncate to 0
name|testTruncate
argument_list|(
literal|0
argument_list|,
operator|-
name|BLOCKSIZE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * case 4~6    */
DECL|class|TruncateWithSnapshot
specifier|private
class|class
name|TruncateWithSnapshot
implements|implements
name|TruncateCase
block|{
annotation|@
name|Override
DECL|method|prepare ()
specifier|public
name|void
name|prepare
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
operator|*
literal|2
operator|+
name|BLOCKSIZE
operator|/
literal|2
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
name|dfs
argument_list|,
name|dir
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
comment|// case 4: truncate to 1.5 blocks
name|long
name|newLength
init|=
name|BLOCKSIZE
operator|+
name|BLOCKSIZE
operator|/
literal|2
decl_stmt|;
comment|// all the blocks are in snapshot. truncate need to allocate a new block
comment|// diff should be +BLOCKSIZE
name|long
name|diff
init|=
name|BLOCKSIZE
decl_stmt|;
comment|// the new quota usage should be BLOCKSIZE * 3 * replication
name|long
name|usage
init|=
name|BLOCKSIZE
operator|*
literal|3
operator|*
name|REPLICATION
decl_stmt|;
name|testTruncate
argument_list|(
name|newLength
argument_list|,
name|diff
argument_list|,
name|usage
argument_list|)
expr_stmt|;
comment|// case 5: truncate to 1 block
name|newLength
operator|=
name|BLOCKSIZE
expr_stmt|;
comment|// the block for truncation is not in snapshot, diff should be -0.5 block
name|diff
operator|=
operator|-
name|BLOCKSIZE
operator|/
literal|2
expr_stmt|;
comment|// after truncation the quota usage should be 2.5 block * repl
name|usage
operator|=
operator|(
name|BLOCKSIZE
operator|*
literal|2
operator|+
name|BLOCKSIZE
operator|/
literal|2
operator|)
operator|*
name|REPLICATION
expr_stmt|;
name|testTruncate
argument_list|(
name|newLength
argument_list|,
name|diff
argument_list|,
name|usage
argument_list|)
expr_stmt|;
comment|// case 6: truncate to 0
name|testTruncate
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|usage
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * case 7~9    */
DECL|class|TruncateWithSnapshot2
specifier|private
class|class
name|TruncateWithSnapshot2
implements|implements
name|TruncateCase
block|{
annotation|@
name|Override
DECL|method|prepare ()
specifier|public
name|void
name|prepare
parameter_list|()
throws|throws
name|Exception
block|{
comment|// original size: 2.5 blocks
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
operator|*
literal|2
operator|+
name|BLOCKSIZE
operator|/
literal|2
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
name|dfs
argument_list|,
name|dir
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
comment|// truncate to 1.5 block
name|dfs
operator|.
name|truncate
argument_list|(
name|file
argument_list|,
name|BLOCKSIZE
operator|+
name|BLOCKSIZE
operator|/
literal|2
argument_list|)
expr_stmt|;
name|TestFileTruncate
operator|.
name|checkBlockRecovery
argument_list|(
name|file
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
comment|// append another 1 BLOCK
name|DFSTestUtil
operator|.
name|appendFile
argument_list|(
name|dfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
comment|// case 8: truncate to 2 blocks
name|long
name|newLength
init|=
name|BLOCKSIZE
operator|*
literal|2
decl_stmt|;
comment|// the original 2.5 blocks are in snapshot. the block truncated is not
comment|// in snapshot. diff should be -0.5 block
name|long
name|diff
init|=
operator|-
name|BLOCKSIZE
operator|/
literal|2
decl_stmt|;
comment|// the new quota usage should be BLOCKSIZE * 3.5 * replication
name|long
name|usage
init|=
operator|(
name|BLOCKSIZE
operator|*
literal|3
operator|+
name|BLOCKSIZE
operator|/
literal|2
operator|)
operator|*
name|REPLICATION
decl_stmt|;
name|testTruncate
argument_list|(
name|newLength
argument_list|,
name|diff
argument_list|,
name|usage
argument_list|)
expr_stmt|;
comment|// case 7: truncate to 1.5 block
name|newLength
operator|=
name|BLOCKSIZE
operator|+
name|BLOCKSIZE
operator|/
literal|2
expr_stmt|;
comment|// the block for truncation is not in snapshot, diff should be
comment|// -0.5 block + (block - 0.5block) = 0
name|diff
operator|=
literal|0
expr_stmt|;
comment|// after truncation the quota usage should be 3 block * repl
name|usage
operator|=
operator|(
name|BLOCKSIZE
operator|*
literal|3
operator|)
operator|*
name|REPLICATION
expr_stmt|;
name|testTruncate
argument_list|(
name|newLength
argument_list|,
name|diff
argument_list|,
name|usage
argument_list|)
expr_stmt|;
comment|// case 9: truncate to 0
name|testTruncate
argument_list|(
literal|0
argument_list|,
operator|-
name|BLOCKSIZE
operator|/
literal|2
argument_list|,
operator|(
name|BLOCKSIZE
operator|*
literal|2
operator|+
name|BLOCKSIZE
operator|/
literal|2
operator|)
operator|*
name|REPLICATION
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTruncateQuotaUpdate (TruncateCase t)
specifier|private
name|void
name|testTruncateQuotaUpdate
parameter_list|(
name|TruncateCase
name|t
parameter_list|)
throws|throws
name|Exception
block|{
name|t
operator|.
name|prepare
argument_list|()
expr_stmt|;
name|t
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQuotaNoSnapshot ()
specifier|public
name|void
name|testQuotaNoSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|testTruncateQuotaUpdate
argument_list|(
operator|new
name|TruncateWithoutSnapshot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQuotaWithSnapshot ()
specifier|public
name|void
name|testQuotaWithSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|testTruncateQuotaUpdate
argument_list|(
operator|new
name|TruncateWithSnapshot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQuotaWithSnapshot2 ()
specifier|public
name|void
name|testQuotaWithSnapshot2
parameter_list|()
throws|throws
name|Exception
block|{
name|testTruncateQuotaUpdate
argument_list|(
operator|new
name|TruncateWithSnapshot2
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

