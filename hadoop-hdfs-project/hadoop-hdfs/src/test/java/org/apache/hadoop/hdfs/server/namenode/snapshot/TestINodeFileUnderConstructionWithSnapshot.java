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
name|EnumSet
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
name|FileStatus
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
name|DFSClientAdapter
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
name|client
operator|.
name|HdfsDataOutputStream
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
name|HdfsDataOutputStream
operator|.
name|SyncFlag
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
name|protocol
operator|.
name|LocatedBlocks
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
name|INode
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
name|INodeDirectory
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
name|namenode
operator|.
name|snapshot
operator|.
name|DirectoryWithSnapshotFeature
operator|.
name|DirectoryDiff
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
name|slf4j
operator|.
name|event
operator|.
name|Level
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
comment|/**  * Test snapshot functionalities while file appending.  */
end_comment

begin_class
DECL|class|TestINodeFileUnderConstructionWithSnapshot
specifier|public
class|class
name|TestINodeFileUnderConstructionWithSnapshot
block|{
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|INode
operator|.
name|LOG
parameter_list|,
name|Level
operator|.
name|TRACE
parameter_list|)
constructor_decl|;
name|SnapshotTestHelper
operator|.
name|disableLogs
parameter_list|()
constructor_decl|;
block|}
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0
decl_stmt|;
DECL|field|REPLICATION
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|BLOCKSIZE
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
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fsn
name|FSNamesystem
name|fsn
decl_stmt|;
DECL|field|hdfs
name|DistributedFileSystem
name|hdfs
decl_stmt|;
DECL|field|fsdir
name|FSDirectory
name|fsdir
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
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|dir
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Test snapshot after file appending    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSnapshotAfterAppending ()
specifier|public
name|void
name|testSnapshotAfterAppending
parameter_list|()
throws|throws
name|Exception
block|{
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
comment|// 1. create snapshot --> create file --> append
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|dir
argument_list|,
literal|"s0"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file
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
name|appendFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
name|INodeFile
name|fileNode
init|=
operator|(
name|INodeFile
operator|)
name|fsdir
operator|.
name|getINode
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// 2. create snapshot --> modify the file --> append
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setReplication
argument_list|(
name|file
argument_list|,
call|(
name|short
call|)
argument_list|(
name|REPLICATION
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|appendFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
comment|// check corresponding inodes
name|fileNode
operator|=
operator|(
name|INodeFile
operator|)
name|fsdir
operator|.
name|getINode
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REPLICATION
operator|-
literal|1
argument_list|,
name|fileNode
operator|.
name|getFileReplication
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|3
argument_list|,
name|fileNode
operator|.
name|computeFileSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// 3. create snapshot --> append
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|appendFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
comment|// check corresponding inodes
name|fileNode
operator|=
operator|(
name|INodeFile
operator|)
name|fsdir
operator|.
name|getINode
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REPLICATION
operator|-
literal|1
argument_list|,
name|fileNode
operator|.
name|getFileReplication
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|4
argument_list|,
name|fileNode
operator|.
name|computeFileSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|appendFileWithoutClosing (Path file, int length)
specifier|private
name|HdfsDataOutputStream
name|appendFileWithoutClosing
parameter_list|(
name|Path
name|file
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|toAppend
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|toAppend
argument_list|)
expr_stmt|;
name|HdfsDataOutputStream
name|out
init|=
operator|(
name|HdfsDataOutputStream
operator|)
name|hdfs
operator|.
name|append
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|toAppend
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
comment|/**    * Test snapshot during file appending, before the corresponding    * {@link FSDataOutputStream} instance closes.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSnapshotWhileAppending ()
specifier|public
name|void
name|testSnapshotWhileAppending
parameter_list|()
throws|throws
name|Exception
block|{
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
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// 1. append without closing stream --> create snapshot
name|HdfsDataOutputStream
name|out
init|=
name|appendFileWithoutClosing
argument_list|(
name|file
argument_list|,
name|BLOCKSIZE
argument_list|)
decl_stmt|;
name|out
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|dir
argument_list|,
literal|"s0"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// check: an INodeFileUnderConstructionWithSnapshot should be stored into s0's
comment|// deleted list, with size BLOCKSIZE*2
name|INodeFile
name|fileNode
init|=
operator|(
name|INodeFile
operator|)
name|fsdir
operator|.
name|getINode
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|2
argument_list|,
name|fileNode
operator|.
name|computeFileSize
argument_list|()
argument_list|)
expr_stmt|;
name|INodeDirectory
name|dirNode
init|=
name|fsdir
operator|.
name|getINode
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
name|DirectoryDiff
name|last
init|=
name|dirNode
operator|.
name|getDiffs
argument_list|()
operator|.
name|getLast
argument_list|()
decl_stmt|;
comment|// 2. append without closing stream
name|out
operator|=
name|appendFileWithoutClosing
argument_list|(
name|file
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
name|out
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
comment|// re-check nodeInDeleted_S0
name|dirNode
operator|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asDirectory
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|2
argument_list|,
name|fileNode
operator|.
name|computeFileSize
argument_list|(
name|last
operator|.
name|getSnapshotId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// 3. take snapshot --> close stream
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// check: an INodeFileUnderConstructionWithSnapshot with size BLOCKSIZE*3 should
comment|// have been stored in s1's deleted list
name|fileNode
operator|=
operator|(
name|INodeFile
operator|)
name|fsdir
operator|.
name|getINode
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|dirNode
operator|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asDirectory
argument_list|()
expr_stmt|;
name|last
operator|=
name|dirNode
operator|.
name|getDiffs
argument_list|()
operator|.
name|getLast
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fileNode
operator|.
name|isWithSnapshot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|3
argument_list|,
name|fileNode
operator|.
name|computeFileSize
argument_list|(
name|last
operator|.
name|getSnapshotId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// 4. modify file --> append without closing stream --> take snapshot -->
comment|// close stream
name|hdfs
operator|.
name|setReplication
argument_list|(
name|file
argument_list|,
call|(
name|short
call|)
argument_list|(
name|REPLICATION
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|=
name|appendFileWithoutClosing
argument_list|(
name|file
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// re-check the size of nodeInDeleted_S1
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|3
argument_list|,
name|fileNode
operator|.
name|computeFileSize
argument_list|(
name|last
operator|.
name|getSnapshotId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * call DFSClient#callGetBlockLocations(...) for snapshot file. Make sure only    * blocks within the size range are returned.    */
annotation|@
name|Test
DECL|method|testGetBlockLocations ()
specifier|public
name|void
name|testGetBlockLocations
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/file"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// take a snapshot on root
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|root
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|fileInSnapshot
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|root
argument_list|,
literal|"s1"
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|FileStatus
name|status
init|=
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|fileInSnapshot
argument_list|)
decl_stmt|;
comment|// make sure we record the size for the file
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
comment|// append data to file
name|DFSTestUtil
operator|.
name|appendFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
operator|-
literal|1
argument_list|)
expr_stmt|;
name|status
operator|=
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|fileInSnapshot
argument_list|)
expr_stmt|;
comment|// the size of snapshot file should still be BLOCKSIZE
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
comment|// the size of the file should be (2 * BLOCKSIZE - 1)
name|status
operator|=
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|2
operator|-
literal|1
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
comment|// call DFSClient#callGetBlockLocations for the file in snapshot
name|LocatedBlocks
name|blocks
init|=
name|DFSClientAdapter
operator|.
name|callGetBlockLocations
argument_list|(
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
argument_list|,
name|fileInSnapshot
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blockList
init|=
name|blocks
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
comment|// should be only one block
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|blocks
operator|.
name|getFileLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blockList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the last block
name|LocatedBlock
name|lastBlock
init|=
name|blocks
operator|.
name|getLastLocatedBlock
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|lastBlock
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|lastBlock
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// take another snapshot
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|root
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|fileInSnapshot2
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|root
argument_list|,
literal|"s2"
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// append data to file without closing
name|HdfsDataOutputStream
name|out
init|=
name|appendFileWithoutClosing
argument_list|(
name|file
argument_list|,
name|BLOCKSIZE
argument_list|)
decl_stmt|;
name|out
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|status
operator|=
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|fileInSnapshot2
argument_list|)
expr_stmt|;
comment|// the size of snapshot file should be BLOCKSIZE*2-1
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|2
operator|-
literal|1
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
comment|// the size of the file should be (3 * BLOCKSIZE - 1)
name|status
operator|=
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|3
operator|-
literal|1
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|DFSClientAdapter
operator|.
name|callGetBlockLocations
argument_list|(
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
argument_list|,
name|fileInSnapshot2
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|blocks
operator|.
name|isUnderConstruction
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blocks
operator|.
name|isLastBlockComplete
argument_list|()
argument_list|)
expr_stmt|;
name|blockList
operator|=
name|blocks
operator|.
name|getLocatedBlocks
argument_list|()
expr_stmt|;
comment|// should be 2 blocks
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|2
operator|-
literal|1
argument_list|,
name|blocks
operator|.
name|getFileLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|blockList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the last block
name|lastBlock
operator|=
name|blocks
operator|.
name|getLastLocatedBlock
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|lastBlock
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
argument_list|,
name|lastBlock
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|DFSClientAdapter
operator|.
name|callGetBlockLocations
argument_list|(
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
argument_list|,
name|fileInSnapshot2
operator|.
name|toString
argument_list|()
argument_list|,
name|BLOCKSIZE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|blockList
operator|=
name|blocks
operator|.
name|getLocatedBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blockList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// check blocks for file being written
name|blocks
operator|=
name|DFSClientAdapter
operator|.
name|callGetBlockLocations
argument_list|(
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|blockList
operator|=
name|blocks
operator|.
name|getLocatedBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|blockList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blocks
operator|.
name|isUnderConstruction
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|blocks
operator|.
name|isLastBlockComplete
argument_list|()
argument_list|)
expr_stmt|;
name|lastBlock
operator|=
name|blocks
operator|.
name|getLastLocatedBlock
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|*
literal|2
argument_list|,
name|lastBlock
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKSIZE
operator|-
literal|1
argument_list|,
name|lastBlock
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLease ()
specifier|public
name|void
name|testLease
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|NameNodeAdapter
operator|.
name|setLeasePeriod
argument_list|(
name|fsn
argument_list|,
literal|100
argument_list|,
literal|200
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"foo"
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
literal|0
argument_list|)
expr_stmt|;
name|HdfsDataOutputStream
name|out
init|=
name|appendFileWithoutClosing
argument_list|(
name|bar
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|out
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|dir
argument_list|,
literal|"s0"
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|delete
argument_list|(
name|foo
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
try|try
block|{
name|fsn
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|NameNodeAdapter
operator|.
name|getLeaseManager
argument_list|(
name|fsn
argument_list|)
operator|.
name|runLeaseChecks
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fsn
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|NameNodeAdapter
operator|.
name|setLeasePeriod
argument_list|(
name|fsn
argument_list|,
name|HdfsConstants
operator|.
name|LEASE_SOFTLIMIT_PERIOD
argument_list|,
name|HdfsConstants
operator|.
name|LEASE_HARDLIMIT_PERIOD
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

