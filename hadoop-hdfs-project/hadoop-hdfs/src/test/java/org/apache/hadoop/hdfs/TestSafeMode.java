begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|hdfs
operator|.
name|MiniDFSCluster
operator|.
name|DataNodeProperties
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
operator|.
name|SafeModeAction
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|After
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * Tests to verify safe mode correctness.  */
end_comment

begin_class
DECL|class|TestSafeMode
specifier|public
class|class
name|TestSafeMode
block|{
DECL|field|TEST_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
name|FileSystem
name|fs
decl_stmt|;
DECL|field|dfs
name|DistributedFileSystem
name|dfs
decl_stmt|;
annotation|@
name|Before
DECL|method|startUp ()
specifier|public
name|void
name|startUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
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
literal|1
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
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|fs
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
name|IOException
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
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
comment|/**    * This test verifies that if SafeMode is manually entered, name-node does not    * come out of safe mode even after the startup safe mode conditions are met.    *<ol>    *<li>Start cluster with 1 data-node.</li>    *<li>Create 2 files with replication 1.</li>    *<li>Re-start cluster with 0 data-nodes.     * Name-node should stay in automatic safe-mode.</li>    *<li>Enter safe mode manually.</li>    *<li>Start the data-node.</li>    *<li>Wait longer than<tt>dfs.namenode.safemode.extension</tt> and     * verify that the name-node is still in safe mode.</li>    *</ol>    *      * @throws IOException    */
annotation|@
name|Test
DECL|method|testManualSafeMode ()
specifier|public
name|void
name|testManualSafeMode
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/testManualSafeMode/file1"
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/testManualSafeMode/file2"
argument_list|)
decl_stmt|;
comment|// create two files with one block each.
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
literal|1000
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
name|createFile
argument_list|(
name|fs
argument_list|,
name|file2
argument_list|,
literal|1000
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// now bring up just the NameNode.
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
literal|0
argument_list|)
operator|.
name|format
argument_list|(
literal|false
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
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No datanode is started. Should be in SafeMode"
argument_list|,
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_GET
argument_list|)
argument_list|)
expr_stmt|;
comment|// manually set safemode.
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
comment|// now bring up the datanode and wait for it to be active.
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// wait longer than dfs.namenode.safemode.extension
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{}
name|assertTrue
argument_list|(
literal|"should still be in SafeMode"
argument_list|,
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_GET
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"should not be in SafeMode"
argument_list|,
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that, if there are no blocks in the filesystem,    * the NameNode doesn't enter the "safemode extension" period.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|45000
argument_list|)
DECL|method|testNoExtensionIfNoBlocks ()
specifier|public
name|void
name|testNoExtensionIfNoBlocks
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_EXTENSION_KEY
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
comment|// Even though we have safemode extension set high, we should immediately
comment|// exit safemode on startup because there are no blocks in the namespace.
name|String
name|status
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSafemode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the NN initializes its under-replicated blocks queue    * before it is ready to exit safemode (HDFS-1476)    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|45000
argument_list|)
DECL|method|testInitializeReplQueuesEarly ()
specifier|public
name|void
name|testInitializeReplQueuesEarly
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Spray the blocks around the cluster when we add DNs instead of
comment|// concentrating all blocks on the first node.
name|BlockManagerTestUtil
operator|.
name|setWritingPrefersLocalNode
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
name|StartupOption
operator|.
name|REGULAR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|,
literal|15
operator|*
name|BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DataNodeProperties
argument_list|>
name|dnprops
init|=
name|Lists
operator|.
name|newLinkedList
argument_list|()
decl_stmt|;
name|dnprops
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dnprops
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dnprops
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
operator|.
name|setFloat
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPL_QUEUE_THRESHOLD_PCT_KEY
argument_list|,
literal|1f
operator|/
literal|15f
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
specifier|final
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|String
name|status
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSafemode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Safe mode is ON.The reported blocks 0 needs additional "
operator|+
literal|"15 blocks to reach the threshold 0.9990 of total blocks 15. "
operator|+
literal|"Safe mode will be turned off automatically."
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Mis-replicated block queues should not be initialized "
operator|+
literal|"until threshold is crossed"
argument_list|,
name|NameNodeAdapter
operator|.
name|safeModeInitializedReplQueues
argument_list|(
name|nn
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnprops
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Wait for the block report from the restarted DN to come in.
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
return|return
name|NameNodeAdapter
operator|.
name|getSafeModeSafeBlocks
argument_list|(
name|nn
argument_list|)
operator|>
literal|0
return|;
block|}
block|}
argument_list|,
literal|10
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
comment|// SafeMode is fine-grain synchronized, so the processMisReplicatedBlocks
comment|// call is still going on at this point - wait until it's done by grabbing
comment|// the lock.
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
name|int
name|safe
init|=
name|NameNodeAdapter
operator|.
name|getSafeModeSafeBlocks
argument_list|(
name|nn
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected first block report to make some but not all blocks "
operator|+
literal|"safe. Got: "
operator|+
name|safe
argument_list|,
name|safe
operator|>=
literal|1
operator|&&
name|safe
operator|<
literal|15
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|NameNodeAdapter
operator|.
name|safeModeInitializedReplQueues
argument_list|(
name|nn
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
operator|-
name|safe
argument_list|,
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getUnderReplicatedBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that, when under-replicated blocks are processed at the end of    * safe-mode, blocks currently under construction are not considered    * under-construction or missing. Regression test for HDFS-2822.    */
annotation|@
name|Test
DECL|method|testRbwBlocksNotConsideredUnderReplicated ()
specifier|public
name|void
name|testRbwBlocksNotConsideredUnderReplicated
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FSDataOutputStream
argument_list|>
name|stms
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Create some junk blocks so that the NN doesn't just immediately
comment|// exit safemode on restart.
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/junk-blocks"
argument_list|)
argument_list|,
name|BLOCK_SIZE
operator|*
literal|4
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
comment|// Create several files which are left open. It's important to
comment|// create several here, because otherwise the first iteration of the
comment|// replication monitor will pull them off the replication queue and
comment|// hide this bug from the test!
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|FSDataOutputStream
name|stm
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/append-"
operator|+
name|i
argument_list|)
argument_list|,
literal|true
argument_list|,
name|BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|stms
operator|.
name|add
argument_list|(
name|stm
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|stm
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|FSNamesystem
name|ns
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|ns
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ns
operator|.
name|getPendingReplicationBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ns
operator|.
name|getCorruptReplicaBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ns
operator|.
name|getMissingBlocksCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
for|for
control|(
name|FSDataOutputStream
name|stm
range|:
name|stms
control|)
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|stm
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|interface|FSRun
specifier|public
interface|interface
name|FSRun
block|{
DECL|method|run (FileSystem fs)
specifier|public
specifier|abstract
name|void
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Assert that the given function fails to run due to a safe     * mode exception.    */
DECL|method|runFsFun (String msg, FSRun f)
specifier|public
name|void
name|runFsFun
parameter_list|(
name|String
name|msg
parameter_list|,
name|FSRun
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|run
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"safe mode"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Run various fs operations while the NN is in safe mode,    * assert that they are either allowed or fail as expected.    */
annotation|@
name|Test
DECL|method|testOperationsWhileInSafeMode ()
specifier|public
name|void
name|testOperationsWhileInSafeMode
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/file1"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_GET
argument_list|)
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Could not enter SM"
argument_list|,
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
argument_list|)
expr_stmt|;
name|runFsFun
argument_list|(
literal|"Set quota while in SM"
argument_list|,
operator|new
name|FSRun
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|DistributedFileSystem
operator|)
name|fs
operator|)
operator|.
name|setQuota
argument_list|(
name|file1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|runFsFun
argument_list|(
literal|"Set perm while in SM"
argument_list|,
operator|new
name|FSRun
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|setPermission
argument_list|(
name|file1
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|runFsFun
argument_list|(
literal|"Set owner while in SM"
argument_list|,
operator|new
name|FSRun
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|setOwner
argument_list|(
name|file1
argument_list|,
literal|"user"
argument_list|,
literal|"group"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|runFsFun
argument_list|(
literal|"Set repl while in SM"
argument_list|,
operator|new
name|FSRun
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|setReplication
argument_list|(
name|file1
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|runFsFun
argument_list|(
literal|"Append file while in SM"
argument_list|,
operator|new
name|FSRun
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|DFSTestUtil
operator|.
name|appendFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
literal|"new bytes"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|runFsFun
argument_list|(
literal|"Delete file while in SM"
argument_list|,
operator|new
name|FSRun
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|delete
argument_list|(
name|file1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|runFsFun
argument_list|(
literal|"Rename file while in SM"
argument_list|,
operator|new
name|FSRun
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|rename
argument_list|(
name|file1
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|setTimes
argument_list|(
name|file1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Set times failed while in SM"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Set times failed while in SM"
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"Could not leave SM"
argument_list|,
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the NameNode stays in safemode when dfs.safemode.datanode.min    * is set to a number greater than the number of live datanodes.    */
annotation|@
name|Test
DECL|method|testDatanodeThreshold ()
specifier|public
name|void
name|testDatanodeThreshold
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_EXTENSION_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_MIN_DATANODES_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|fs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|String
name|tipMsg
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSafemode
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Safemode tip message looks right: "
operator|+
name|tipMsg
argument_list|,
name|tipMsg
operator|.
name|contains
argument_list|(
literal|"The number of live datanodes 0 needs an additional "
operator|+
literal|"2 live datanodes to reach the minimum number 1. "
operator|+
literal|"Safe mode will be turned off automatically."
argument_list|)
argument_list|)
expr_stmt|;
comment|// Start a datanode
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Wait long enough for safemode check to refire
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{}
comment|// We now should be out of safe mode.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSafemode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*    * Tests some utility methods that surround the SafeMode's state.    * @throws IOException when there's an issue connecting to the test DFS.    */
DECL|method|testSafeModeUtils ()
specifier|public
name|void
name|testSafeModeUtils
parameter_list|()
throws|throws
name|IOException
block|{
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
comment|// Enter safemode.
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"State was expected to be in safemode."
argument_list|,
name|dfs
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Exit safemode.
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"State was expected to be out of safemode."
argument_list|,
name|dfs
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

