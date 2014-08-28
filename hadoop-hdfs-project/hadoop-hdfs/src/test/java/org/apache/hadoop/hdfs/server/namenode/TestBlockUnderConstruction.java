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
name|List
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
name|BlockLocation
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
name|TestFileCreation
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
name|BlockInfoUnderConstruction
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
name|protocol
operator|.
name|NamenodeProtocols
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
DECL|class|TestBlockUnderConstruction
specifier|public
class|class
name|TestBlockUnderConstruction
block|{
DECL|field|BASE_DIR
specifier|static
specifier|final
name|String
name|BASE_DIR
init|=
literal|"/test/TestBlockUnderConstruction"
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|8192
decl_stmt|;
comment|// same as TestFileCreation.blocksize
DECL|field|NUM_BLOCKS
specifier|static
specifier|final
name|int
name|NUM_BLOCKS
init|=
literal|5
decl_stmt|;
comment|// number of blocks to write
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|hdfs
specifier|private
specifier|static
name|DistributedFileSystem
name|hdfs
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
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
literal|3
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
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|hdfs
operator|!=
literal|null
condition|)
name|hdfs
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|writeFile (Path file, FSDataOutputStream stm, int size)
name|void
name|writeFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|FSDataOutputStream
name|stm
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|blocksBefore
init|=
name|stm
operator|.
name|getPos
argument_list|()
operator|/
name|BLOCK_SIZE
decl_stmt|;
name|TestFileCreation
operator|.
name|writeFile
argument_list|(
name|stm
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
comment|// need to make sure the full block is completely flushed to the DataNodes
comment|// (see FSOutputSummer#flush)
name|stm
operator|.
name|flush
argument_list|()
expr_stmt|;
name|int
name|blocksAfter
init|=
literal|0
decl_stmt|;
comment|// wait until the block is allocated by DataStreamer
name|BlockLocation
index|[]
name|locatedBlocks
decl_stmt|;
while|while
condition|(
name|blocksAfter
operator|<=
name|blocksBefore
condition|)
block|{
name|locatedBlocks
operator|=
name|DFSClientAdapter
operator|.
name|getDFSClient
argument_list|(
name|hdfs
argument_list|)
operator|.
name|getBlockLocations
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|,
literal|0L
argument_list|,
name|BLOCK_SIZE
operator|*
name|NUM_BLOCKS
argument_list|)
expr_stmt|;
name|blocksAfter
operator|=
name|locatedBlocks
operator|==
literal|null
condition|?
literal|0
else|:
name|locatedBlocks
operator|.
name|length
expr_stmt|;
block|}
block|}
DECL|method|verifyFileBlocks (String file, boolean isFileOpen)
specifier|private
name|void
name|verifyFileBlocks
parameter_list|(
name|String
name|file
parameter_list|,
name|boolean
name|isFileOpen
parameter_list|)
throws|throws
name|IOException
block|{
name|FSNamesystem
name|ns
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|INodeFile
name|inode
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|ns
operator|.
name|dir
operator|.
name|getINode
argument_list|(
name|file
argument_list|)
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"File "
operator|+
name|inode
operator|.
name|toString
argument_list|()
operator|+
literal|" isUnderConstruction = "
operator|+
name|inode
operator|.
name|isUnderConstruction
argument_list|()
operator|+
literal|" expected to be "
operator|+
name|isFileOpen
argument_list|,
name|inode
operator|.
name|isUnderConstruction
argument_list|()
operator|==
name|isFileOpen
argument_list|)
expr_stmt|;
name|BlockInfo
index|[]
name|blocks
init|=
name|inode
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"File does not have blocks: "
operator|+
name|inode
operator|.
name|toString
argument_list|()
argument_list|,
name|blocks
operator|!=
literal|null
operator|&&
name|blocks
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
name|BlockInfo
name|curBlock
decl_stmt|;
comment|// all blocks but the last two should be regular blocks
for|for
control|(
init|;
name|idx
operator|<
name|blocks
operator|.
name|length
operator|-
literal|2
condition|;
name|idx
operator|++
control|)
block|{
name|curBlock
operator|=
name|blocks
index|[
name|idx
index|]
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Block is not complete: "
operator|+
name|curBlock
argument_list|,
name|curBlock
operator|.
name|isComplete
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Block is not in BlocksMap: "
operator|+
name|curBlock
argument_list|,
name|ns
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getStoredBlock
argument_list|(
name|curBlock
argument_list|)
operator|==
name|curBlock
argument_list|)
expr_stmt|;
block|}
comment|// the penultimate block is either complete or
comment|// committed if the file is not closed
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|curBlock
operator|=
name|blocks
index|[
name|idx
operator|-
literal|1
index|]
expr_stmt|;
comment|// penultimate block
name|assertTrue
argument_list|(
literal|"Block "
operator|+
name|curBlock
operator|+
literal|" isUnderConstruction = "
operator|+
name|inode
operator|.
name|isUnderConstruction
argument_list|()
operator|+
literal|" expected to be "
operator|+
name|isFileOpen
argument_list|,
operator|(
name|isFileOpen
operator|&&
name|curBlock
operator|.
name|isComplete
argument_list|()
operator|)
operator|||
operator|(
operator|!
name|isFileOpen
operator|&&
operator|!
name|curBlock
operator|.
name|isComplete
argument_list|()
operator|==
operator|(
name|curBlock
operator|.
name|getBlockUCState
argument_list|()
operator|==
name|BlockUCState
operator|.
name|COMMITTED
operator|)
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Block is not in BlocksMap: "
operator|+
name|curBlock
argument_list|,
name|ns
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getStoredBlock
argument_list|(
name|curBlock
argument_list|)
operator|==
name|curBlock
argument_list|)
expr_stmt|;
block|}
comment|// The last block is complete if the file is closed.
comment|// If the file is open, the last block may be complete or not.
name|curBlock
operator|=
name|blocks
index|[
name|idx
index|]
expr_stmt|;
comment|// last block
if|if
condition|(
operator|!
name|isFileOpen
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Block "
operator|+
name|curBlock
operator|+
literal|", isFileOpen = "
operator|+
name|isFileOpen
argument_list|,
name|curBlock
operator|.
name|isComplete
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Block is not in BlocksMap: "
operator|+
name|curBlock
argument_list|,
name|ns
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getStoredBlock
argument_list|(
name|curBlock
argument_list|)
operator|==
name|curBlock
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockCreation ()
specifier|public
name|void
name|testBlockCreation
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|BASE_DIR
argument_list|,
literal|"file1.dat"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|NUM_BLOCKS
condition|;
name|idx
operator|++
control|)
block|{
comment|// write one block
name|writeFile
argument_list|(
name|file1
argument_list|,
name|out
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
comment|// verify consistency
name|verifyFileBlocks
argument_list|(
name|file1
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// close file
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify consistency
name|verifyFileBlocks
argument_list|(
name|file1
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test NameNode.getBlockLocations(..) on reading un-closed files.    */
annotation|@
name|Test
DECL|method|testGetBlockLocations ()
specifier|public
name|void
name|testGetBlockLocations
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|NamenodeProtocols
name|namenode
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|BASE_DIR
argument_list|,
literal|"file2.dat"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|src
init|=
name|p
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|p
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// write a half block
name|int
name|len
init|=
name|BLOCK_SIZE
operator|>>>
literal|1
decl_stmt|;
name|writeFile
argument_list|(
name|p
argument_list|,
name|out
argument_list|,
name|len
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|NUM_BLOCKS
condition|;
control|)
block|{
comment|// verify consistency
specifier|final
name|LocatedBlocks
name|lb
init|=
name|namenode
operator|.
name|getBlockLocations
argument_list|(
name|src
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks
init|=
name|lb
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Block
name|b
init|=
name|blocks
operator|.
name|get
argument_list|(
name|blocks
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|b
operator|instanceof
name|BlockInfoUnderConstruction
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|i
operator|<
name|NUM_BLOCKS
condition|)
block|{
comment|// write one more block
name|writeFile
argument_list|(
name|p
argument_list|,
name|out
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|len
operator|+=
name|BLOCK_SIZE
expr_stmt|;
block|}
block|}
comment|// close file
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

