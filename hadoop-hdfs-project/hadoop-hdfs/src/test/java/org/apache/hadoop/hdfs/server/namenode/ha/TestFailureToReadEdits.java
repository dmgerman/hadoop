begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
name|ha
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyInt
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doAnswer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|ha
operator|.
name|ServiceFailedException
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
name|HAUtil
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
name|MiniDFSNNTopology
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
name|EditLogInputException
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
name|EditLogInputStream
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
name|FSEditLog
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
name|FSEditLogOp
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
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
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
name|ImmutableList
import|;
end_import

begin_class
DECL|class|TestFailureToReadEdits
specifier|public
class|class
name|TestFailureToReadEdits
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
name|TestFailureToReadEdits
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_DIR1
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DIR1
init|=
literal|"/test1"
decl_stmt|;
DECL|field|TEST_DIR2
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DIR2
init|=
literal|"/test2"
decl_stmt|;
DECL|field|TEST_DIR3
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DIR3
init|=
literal|"/test3"
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|mockRuntime
specifier|private
name|Runtime
name|mockRuntime
init|=
name|mock
argument_list|(
name|Runtime
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|nn0
specifier|private
name|NameNode
name|nn0
decl_stmt|;
DECL|field|nn1
specifier|private
name|NameNode
name|nn1
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
annotation|@
name|Before
DECL|method|setUpCluster ()
specifier|public
name|void
name|setUpCluster
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
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_CHECK_PERIOD_KEY
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
name|DFS_NAMENODE_CHECKPOINT_TXNS_KEY
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
name|DFS_NAMENODE_NUM_CHECKPOINTS_RETAINED_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|HAUtil
operator|.
name|setAllowStandbyReads
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"ns1"
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn1"
argument_list|)
operator|.
name|setHttpPort
argument_list|(
literal|10001
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn2"
argument_list|)
operator|.
name|setHttpPort
argument_list|(
literal|10002
argument_list|)
argument_list|)
argument_list|)
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
name|nnTopology
argument_list|(
name|topology
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
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
name|nn0
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|nn1
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getEditLogTailer
argument_list|()
operator|.
name|setRuntime
argument_list|(
name|mockRuntime
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fs
operator|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDownCluster ()
specifier|public
name|void
name|tearDownCluster
parameter_list|()
throws|throws
name|Exception
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
comment|/**    * Test that the standby NN won't double-replay earlier edits if it encounters    * a failure to read a later edit.    */
annotation|@
name|Test
DECL|method|testFailuretoReadEdits ()
specifier|public
name|void
name|testFailuretoReadEdits
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn0
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
comment|// If these two ops are applied twice, the first op will throw an
comment|// exception the second time its replayed.
name|fs
operator|.
name|setOwner
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR1
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR1
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// This op should get applied just fine.
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// This is the op the mocking will cause to fail to be read.
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LimitedEditLogAnswer
name|answer
init|=
name|causeFailureOnEditLogRead
argument_list|()
decl_stmt|;
try|try
block|{
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn0
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Standby fully caught up, but should not have been able to"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HATestUtil
operator|.
name|CouldNotCatchUpException
name|e
parameter_list|)
block|{
name|verify
argument_list|(
name|mockRuntime
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|exit
argument_list|(
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Null because it was deleted.
name|assertNull
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|nn1
argument_list|,
name|TEST_DIR1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should have been successfully created.
name|assertTrue
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|nn1
argument_list|,
name|TEST_DIR2
argument_list|,
literal|false
argument_list|)
operator|.
name|isDir
argument_list|()
argument_list|)
expr_stmt|;
comment|// Null because it hasn't been created yet.
name|assertNull
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|nn1
argument_list|,
name|TEST_DIR3
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now let the standby read ALL the edits.
name|answer
operator|.
name|setThrowExceptionOnRead
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn0
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
comment|// Null because it was deleted.
name|assertNull
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|nn1
argument_list|,
name|TEST_DIR1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should have been successfully created.
name|assertTrue
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|nn1
argument_list|,
name|TEST_DIR2
argument_list|,
literal|false
argument_list|)
operator|.
name|isDir
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should now have been successfully created.
name|assertTrue
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|nn1
argument_list|,
name|TEST_DIR3
argument_list|,
literal|false
argument_list|)
operator|.
name|isDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the following case:    * 1. SBN is reading a finalized edits file when NFS disappears halfway    *    through (or some intermittent error happens)    * 2. SBN performs a checkpoint and uploads it to the NN    * 3. NN receives a checkpoint that doesn't correspond to the end of any log    *    segment    * 4. Both NN and SBN should be able to restart at this point.    *     * This is a regression test for HDFS-2766.    */
annotation|@
name|Test
DECL|method|testCheckpointStartingMidEditsFile ()
specifier|public
name|void
name|testCheckpointStartingMidEditsFile
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn0
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
comment|// Once the standby catches up, it should notice that it needs to
comment|// do a checkpoint and save one to its local directories.
name|HATestUtil
operator|.
name|waitForCheckpoint
argument_list|(
name|cluster
argument_list|,
literal|1
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// It should also upload it back to the active.
name|HATestUtil
operator|.
name|waitForCheckpoint
argument_list|(
name|cluster
argument_list|,
literal|0
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|causeFailureOnEditLogRead
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn0
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Standby fully caught up, but should not have been able to"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HATestUtil
operator|.
name|CouldNotCatchUpException
name|e
parameter_list|)
block|{
name|verify
argument_list|(
name|mockRuntime
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|exit
argument_list|(
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// 5 because we should get OP_START_LOG_SEGMENT and one successful OP_MKDIR
name|HATestUtil
operator|.
name|waitForCheckpoint
argument_list|(
name|cluster
argument_list|,
literal|1
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// It should also upload it back to the active.
name|HATestUtil
operator|.
name|waitForCheckpoint
argument_list|(
name|cluster
argument_list|,
literal|0
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// Restart the active NN
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|waitForCheckpoint
argument_list|(
name|cluster
argument_list|,
literal|0
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|FileSystem
name|fs0
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Make sure that when the active restarts, it loads all the edits.
name|fs0
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|NameNode
operator|.
name|getUri
argument_list|(
name|nn0
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs0
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs0
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs0
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fs0
operator|!=
literal|null
condition|)
name|fs0
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Ensure that the standby fails to become active if it cannot read all    * available edits in the shared edits dir when it is transitioning to active    * state.    */
annotation|@
name|Test
DECL|method|testFailureToReadEditsOnTransitionToActive ()
specifier|public
name|void
name|testFailureToReadEditsOnTransitionToActive
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn0
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
comment|// It should also upload it back to the active.
name|HATestUtil
operator|.
name|waitForCheckpoint
argument_list|(
name|cluster
argument_list|,
literal|0
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|causeFailureOnEditLogRead
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DIR3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn0
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Standby fully caught up, but should not have been able to"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HATestUtil
operator|.
name|CouldNotCatchUpException
name|e
parameter_list|)
block|{
name|verify
argument_list|(
name|mockRuntime
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|exit
argument_list|(
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Shutdown the active NN.
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Runtime
name|mockRuntime
init|=
name|mock
argument_list|(
name|Runtime
operator|.
name|class
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
operator|.
name|setRuntimeForTesting
argument_list|(
name|mockRuntime
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockRuntime
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|exit
argument_list|(
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Transition the standby to active.
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Standby transitioned to active, but should not have been able to"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceFailedException
name|sfe
parameter_list|)
block|{
name|Throwable
name|sfeCause
init|=
name|sfe
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got expected exception: "
operator|+
name|sfeCause
operator|.
name|toString
argument_list|()
argument_list|,
name|sfeCause
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Standby failed to catch up for some reason other than "
operator|+
literal|"failure to read logs"
argument_list|,
name|sfeCause
operator|.
name|getCause
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|EditLogInputException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|verify
argument_list|(
name|mockRuntime
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|exit
argument_list|(
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|causeFailureOnEditLogRead ()
specifier|private
name|LimitedEditLogAnswer
name|causeFailureOnEditLogRead
parameter_list|()
throws|throws
name|IOException
block|{
name|FSEditLog
name|spyEditLog
init|=
name|spy
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getEditLogTailer
argument_list|()
operator|.
name|getEditLog
argument_list|()
argument_list|)
decl_stmt|;
name|LimitedEditLogAnswer
name|answer
init|=
operator|new
name|LimitedEditLogAnswer
argument_list|()
decl_stmt|;
name|doAnswer
argument_list|(
name|answer
argument_list|)
operator|.
name|when
argument_list|(
name|spyEditLog
argument_list|)
operator|.
name|selectInputStreams
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getEditLogTailer
argument_list|()
operator|.
name|setEditLog
argument_list|(
name|spyEditLog
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
DECL|class|LimitedEditLogAnswer
specifier|private
specifier|static
class|class
name|LimitedEditLogAnswer
implements|implements
name|Answer
argument_list|<
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
argument_list|>
block|{
DECL|field|throwExceptionOnRead
specifier|private
name|boolean
name|throwExceptionOnRead
init|=
literal|true
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
name|streams
init|=
operator|(
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
operator|)
name|invocation
operator|.
name|callRealMethod
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|throwExceptionOnRead
condition|)
block|{
return|return
name|streams
return|;
block|}
else|else
block|{
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
name|ret
init|=
operator|new
name|LinkedList
argument_list|<
name|EditLogInputStream
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|EditLogInputStream
name|stream
range|:
name|streams
control|)
block|{
name|EditLogInputStream
name|spyStream
init|=
name|spy
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|FSEditLogOp
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FSEditLogOp
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|FSEditLogOp
name|op
init|=
operator|(
name|FSEditLogOp
operator|)
name|invocation
operator|.
name|callRealMethod
argument_list|()
decl_stmt|;
if|if
condition|(
name|throwExceptionOnRead
operator|&&
name|TEST_DIR3
operator|.
name|equals
argument_list|(
name|NameNodeAdapter
operator|.
name|getMkdirOpPath
argument_list|(
name|op
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to read op creating "
operator|+
name|TEST_DIR3
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|op
return|;
block|}
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|spyStream
argument_list|)
operator|.
name|readOp
argument_list|()
expr_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|spyStream
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
DECL|method|setThrowExceptionOnRead (boolean throwExceptionOnRead)
specifier|public
name|void
name|setThrowExceptionOnRead
parameter_list|(
name|boolean
name|throwExceptionOnRead
parameter_list|)
block|{
name|this
operator|.
name|throwExceptionOnRead
operator|=
name|throwExceptionOnRead
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

