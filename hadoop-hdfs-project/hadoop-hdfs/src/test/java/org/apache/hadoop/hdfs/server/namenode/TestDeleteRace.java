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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|AppendTestUtil
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
name|protocol
operator|.
name|DatanodeID
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
name|protocolPB
operator|.
name|DatanodeProtocolClientSideTranslatorPB
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
name|BlockPlacementPolicy
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
name|BlockPlacementPolicyDefault
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
name|DatanodeStorageInfo
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
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|DelayAnswer
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|util
operator|.
name|reflection
operator|.
name|Whitebox
import|;
end_import

begin_comment
comment|/**  * Test race between delete and other operations.  For now only addBlock()  * is tested since all others are acquiring FSNamesystem lock for the   * whole duration.  */
end_comment

begin_class
DECL|class|TestDeleteRace
specifier|public
class|class
name|TestDeleteRace
block|{
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|4096
decl_stmt|;
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
name|TestDeleteRace
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|Test
DECL|method|testDeleteAddBlockRace ()
specifier|public
name|void
name|testDeleteAddBlockRace
parameter_list|()
throws|throws
name|Exception
block|{
name|testDeleteAddBlockRace
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteAddBlockRaceWithSnapshot ()
specifier|public
name|void
name|testDeleteAddBlockRaceWithSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|testDeleteAddBlockRace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeleteAddBlockRace (boolean hasSnapshot)
specifier|private
name|void
name|testDeleteAddBlockRace
parameter_list|(
name|boolean
name|hasSnapshot
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|SlowBlockPlacementPolicy
operator|.
name|class
argument_list|,
name|BlockPlacementPolicy
operator|.
name|class
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
name|build
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|String
name|fileName
init|=
literal|"/testDeleteAddBlockRace"
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
literal|null
decl_stmt|;
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasSnapshot
condition|)
block|{
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
operator|(
name|DistributedFileSystem
operator|)
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
block|}
name|Thread
name|deleteThread
init|=
operator|new
name|DeleteThread
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
decl_stmt|;
name|deleteThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
comment|// write data and syn to make sure a block is allocated.
name|out
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[
literal|32
index|]
argument_list|,
literal|0
argument_list|,
literal|32
argument_list|)
expr_stmt|;
name|out
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should have failed."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
name|filePath
operator|.
name|getName
argument_list|()
argument_list|,
name|e
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
DECL|class|SlowBlockPlacementPolicy
specifier|private
specifier|static
class|class
name|SlowBlockPlacementPolicy
extends|extends
name|BlockPlacementPolicyDefault
block|{
annotation|@
name|Override
DECL|method|chooseTarget (String srcPath, int numOfReplicas, Node writer, List<DatanodeStorageInfo> chosenNodes, boolean returnChosenNodes, Set<Node> excludedNodes, long blocksize, final BlockStoragePolicy storagePolicy)
specifier|public
name|DatanodeStorageInfo
index|[]
name|chooseTarget
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|int
name|numOfReplicas
parameter_list|,
name|Node
name|writer
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|chosenNodes
parameter_list|,
name|boolean
name|returnChosenNodes
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|long
name|blocksize
parameter_list|,
specifier|final
name|BlockStoragePolicy
name|storagePolicy
parameter_list|)
block|{
name|DatanodeStorageInfo
index|[]
name|results
init|=
name|super
operator|.
name|chooseTarget
argument_list|(
name|srcPath
argument_list|,
name|numOfReplicas
argument_list|,
name|writer
argument_list|,
name|chosenNodes
argument_list|,
name|returnChosenNodes
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|storagePolicy
argument_list|)
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
return|return
name|results
return|;
block|}
block|}
DECL|class|DeleteThread
specifier|private
class|class
name|DeleteThread
extends|extends
name|Thread
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|method|DeleteThread (FileSystem fs, Path path)
name|DeleteThread
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting"
operator|+
name|path
argument_list|)
expr_stmt|;
specifier|final
name|FSDirectory
name|fsdir
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|dir
decl_stmt|;
name|INode
name|fileINode
init|=
name|fsdir
operator|.
name|getINode4Write
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|INodeMap
name|inodeMap
init|=
operator|(
name|INodeMap
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|fsdir
argument_list|,
literal|"inodeMap"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// after deletion, add the inode back to the inodeMap
name|inodeMap
operator|.
name|put
argument_list|(
name|fileINode
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleted"
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|RenameThread
specifier|private
class|class
name|RenameThread
extends|extends
name|Thread
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|from
specifier|private
name|Path
name|from
decl_stmt|;
DECL|field|to
specifier|private
name|Path
name|to
decl_stmt|;
DECL|method|RenameThread (FileSystem fs, Path from, Path to)
name|RenameThread
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|from
parameter_list|,
name|Path
name|to
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Renaming "
operator|+
name|from
operator|+
literal|" to "
operator|+
name|to
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Renamed "
operator|+
name|from
operator|+
literal|" to "
operator|+
name|to
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testRenameRace ()
specifier|public
name|void
name|testRenameRace
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|SlowBlockPlacementPolicy
operator|.
name|class
argument_list|,
name|BlockPlacementPolicy
operator|.
name|class
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
name|build
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|dirPath1
init|=
operator|new
name|Path
argument_list|(
literal|"/testRenameRace1"
argument_list|)
decl_stmt|;
name|Path
name|dirPath2
init|=
operator|new
name|Path
argument_list|(
literal|"/testRenameRace2"
argument_list|)
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testRenameRace1/file1"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dirPath1
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|Thread
name|renameThread
init|=
operator|new
name|RenameThread
argument_list|(
name|fs
argument_list|,
name|dirPath1
argument_list|,
name|dirPath2
argument_list|)
decl_stmt|;
name|renameThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// write data and close to make sure a block is allocated.
name|out
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[
literal|32
index|]
argument_list|,
literal|0
argument_list|,
literal|32
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Restart name node so that it replays edit. If old path was
comment|// logged in edit, it will fail to come up.
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|0
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
comment|/**    * Test race between delete operation and commitBlockSynchronization method.    * See HDFS-6825.    * @param hasSnapshot    * @throws Exception    */
DECL|method|testDeleteAndCommitBlockSynchronizationRace (boolean hasSnapshot)
specifier|private
name|void
name|testDeleteAndCommitBlockSynchronizationRace
parameter_list|(
name|boolean
name|hasSnapshot
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Start testing, hasSnapshot: "
operator|+
name|hasSnapshot
argument_list|)
expr_stmt|;
specifier|final
name|String
name|testPaths
index|[]
init|=
block|{
literal|"/test-file"
block|,
literal|"/testdir/testdir1/test-file"
block|}
decl_stmt|;
specifier|final
name|Path
name|rootPath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Disable permissions so that another user can recover the lease.
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PERMISSIONS_ENABLED_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|stm
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|DataNode
argument_list|,
name|DatanodeProtocolClientSideTranslatorPB
argument_list|>
name|dnMap
init|=
operator|new
name|HashMap
argument_list|<
name|DataNode
argument_list|,
name|DatanodeProtocolClientSideTranslatorPB
argument_list|>
argument_list|()
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
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|int
name|stId
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|testPath
range|:
name|testPaths
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"test on "
operator|+
name|testPath
operator|+
literal|" snapshot: "
operator|+
name|hasSnapshot
argument_list|)
expr_stmt|;
name|Path
name|fPath
init|=
operator|new
name|Path
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
comment|//find grandest non-root parent
name|Path
name|grandestNonRootParent
init|=
name|fPath
decl_stmt|;
while|while
condition|(
operator|!
name|grandestNonRootParent
operator|.
name|getParent
argument_list|()
operator|.
name|equals
argument_list|(
name|rootPath
argument_list|)
condition|)
block|{
name|grandestNonRootParent
operator|=
name|grandestNonRootParent
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|stm
operator|=
name|fs
operator|.
name|create
argument_list|(
name|fPath
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"test on "
operator|+
name|testPath
operator|+
literal|" created "
operator|+
name|fPath
argument_list|)
expr_stmt|;
comment|// write a half block
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|stm
argument_list|,
literal|0
argument_list|,
name|BLOCK_SIZE
operator|/
literal|2
argument_list|)
expr_stmt|;
name|stm
operator|.
name|hflush
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasSnapshot
condition|)
block|{
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|fs
argument_list|,
name|rootPath
argument_list|,
literal|"st"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|stId
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|stId
expr_stmt|;
block|}
comment|// Look into the block manager on the active node for the block
comment|// under construction.
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|ExtendedBlock
name|blk
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|fPath
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|expectedPrimary
init|=
name|DFSTestUtil
operator|.
name|getExpectedPrimaryNode
argument_list|(
name|nn
argument_list|,
name|blk
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Expecting block recovery to be triggered on DN "
operator|+
name|expectedPrimary
argument_list|)
expr_stmt|;
comment|// Find the corresponding DN daemon, and spy on its connection to the
comment|// active.
name|DataNode
name|primaryDN
init|=
name|cluster
operator|.
name|getDataNode
argument_list|(
name|expectedPrimary
operator|.
name|getIpcPort
argument_list|()
argument_list|)
decl_stmt|;
name|DatanodeProtocolClientSideTranslatorPB
name|nnSpy
init|=
name|dnMap
operator|.
name|get
argument_list|(
name|primaryDN
argument_list|)
decl_stmt|;
if|if
condition|(
name|nnSpy
operator|==
literal|null
condition|)
block|{
name|nnSpy
operator|=
name|DataNodeTestUtils
operator|.
name|spyOnBposToNN
argument_list|(
name|primaryDN
argument_list|,
name|nn
argument_list|)
expr_stmt|;
name|dnMap
operator|.
name|put
argument_list|(
name|primaryDN
argument_list|,
name|nnSpy
argument_list|)
expr_stmt|;
block|}
comment|// Delay the commitBlockSynchronization call
name|DelayAnswer
name|delayer
init|=
operator|new
name|DelayAnswer
argument_list|(
name|LOG
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
name|delayer
argument_list|)
operator|.
name|when
argument_list|(
name|nnSpy
argument_list|)
operator|.
name|commitBlockSynchronization
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|blk
argument_list|)
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
comment|// new genstamp
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|,
comment|// new length
name|Mockito
operator|.
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|,
comment|// close file
name|Mockito
operator|.
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|,
comment|// delete block
operator|(
name|DatanodeID
index|[]
operator|)
name|Mockito
operator|.
name|anyObject
argument_list|()
argument_list|,
comment|// new targets
operator|(
name|String
index|[]
operator|)
name|Mockito
operator|.
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
comment|// new target storages
name|fs
operator|.
name|recoverLease
argument_list|(
name|fPath
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for commitBlockSynchronization call from primary"
argument_list|)
expr_stmt|;
name|delayer
operator|.
name|waitForCall
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting recursively "
operator|+
name|grandestNonRootParent
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|grandestNonRootParent
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|delayer
operator|.
name|proceed
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Now wait for result"
argument_list|)
expr_stmt|;
name|delayer
operator|.
name|waitForResult
argument_list|()
expr_stmt|;
name|Throwable
name|t
init|=
name|delayer
operator|.
name|getThrown
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Result exception (snapshot: "
operator|+
name|hasSnapshot
operator|+
literal|"): "
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
block|}
comment|// end of loop each fPath
name|LOG
operator|.
name|info
argument_list|(
literal|"Now check we can restart"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNodes
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Restart finished"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|stm
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|stm
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|600000
argument_list|)
DECL|method|testDeleteAndCommitBlockSynchonizationRaceNoSnapshot ()
specifier|public
name|void
name|testDeleteAndCommitBlockSynchonizationRaceNoSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|testDeleteAndCommitBlockSynchronizationRace
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|600000
argument_list|)
DECL|method|testDeleteAndCommitBlockSynchronizationRaceHasSnapshot ()
specifier|public
name|void
name|testDeleteAndCommitBlockSynchronizationRaceHasSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|testDeleteAndCommitBlockSynchronizationRace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

