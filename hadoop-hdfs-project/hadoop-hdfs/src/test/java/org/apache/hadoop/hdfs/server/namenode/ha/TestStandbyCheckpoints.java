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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|URI
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
name|FSImage
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
name|FSImageTestUtil
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
name|NNStorage
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
name|Mockito
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
name|ImmutableSet
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

begin_class
DECL|class|TestStandbyCheckpoints
specifier|public
class|class
name|TestStandbyCheckpoints
block|{
DECL|field|NUM_DIRS_IN_LOG
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DIRS_IN_LOG
init|=
literal|200000
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|nn0
DECL|field|nn1
specifier|private
name|NameNode
name|nn0
decl_stmt|,
name|nn1
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
annotation|@
name|Before
DECL|method|setupCluster ()
specifier|public
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
literal|5
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
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdownCluster ()
specifier|public
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|IOException
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
DECL|method|testSBNCheckpoints ()
specifier|public
name|void
name|testSBNCheckpoints
parameter_list|()
throws|throws
name|Exception
block|{
name|doEdits
argument_list|(
literal|0
argument_list|,
literal|10
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
literal|12
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
literal|12
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for the case when both of the NNs in the cluster are    * in the standby state, and thus are both creating checkpoints    * and uploading them to each other.    * In this circumstance, they should receive the error from the    * other node indicating that the other node already has a    * checkpoint for the given txid, but this should not cause    * an abort, etc.    */
annotation|@
name|Test
DECL|method|testBothNodesInStandbyState ()
specifier|public
name|void
name|testBothNodesInStandbyState
parameter_list|()
throws|throws
name|Exception
block|{
name|doEdits
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Transitioning to standby closed the edit log on the active,
comment|// so the standby will catch up. Then, both will be in standby mode
comment|// with enough uncheckpointed txns to cause a checkpoint, and they
comment|// will each try to take a checkpoint and upload to each other.
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
literal|12
argument_list|)
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
literal|12
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|nn0
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|dirs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|dirs
operator|.
name|addAll
argument_list|(
name|FSImageTestUtil
operator|.
name|getNameNodeCurrentDirs
argument_list|(
name|cluster
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dirs
operator|.
name|addAll
argument_list|(
name|FSImageTestUtil
operator|.
name|getNameNodeCurrentDirs
argument_list|(
name|cluster
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|FSImageTestUtil
operator|.
name|assertParallelFilesAreIdentical
argument_list|(
name|dirs
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for the case when the SBN is configured to checkpoint based    * on a time period, but no transactions are happening on the    * active. Thus, it would want to save a second checkpoint at the    * same txid, which is a no-op. This test makes sure this doesn't    * cause any problem.    */
annotation|@
name|Test
DECL|method|testCheckpointWhenNoNewTransactionsHappened ()
specifier|public
name|void
name|testCheckpointWhenNoNewTransactionsHappened
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Checkpoint as fast as we can, in a tight loop.
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|1
argument_list|)
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_PERIOD_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|1
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
name|FSImage
name|spyImage1
init|=
name|NameNodeAdapter
operator|.
name|spyOnFsImage
argument_list|(
name|nn1
argument_list|)
decl_stmt|;
comment|// We shouldn't save any checkpoints at txid=0
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyImage1
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|saveNamespace
argument_list|(
operator|(
name|FSNamesystem
operator|)
name|Mockito
operator|.
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
comment|// Roll the primary and wait for the standby to catch up
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn0
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// We should make exactly one checkpoint at this new txid.
name|Mockito
operator|.
name|verify
argument_list|(
name|spyImage1
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|saveNamespace
argument_list|(
operator|(
name|FSNamesystem
operator|)
name|Mockito
operator|.
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test cancellation of ongoing checkpoints when failover happens    * mid-checkpoint.     */
annotation|@
name|Test
DECL|method|testCheckpointCancellation ()
specifier|public
name|void
name|testCheckpointCancellation
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Create an edit log in the shared edits dir with a lot
comment|// of mkdirs operations. This is solely so that the image is
comment|// large enough to take a non-trivial amount of time to load.
comment|// (only ~15MB)
name|URI
name|sharedUri
init|=
name|cluster
operator|.
name|getSharedEditsDir
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|File
name|sharedDir
init|=
operator|new
name|File
argument_list|(
name|sharedUri
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|,
literal|"testCheckpointCancellation-tmp"
argument_list|)
decl_stmt|;
name|FSImageTestUtil
operator|.
name|createAbortedLogWithMkdirs
argument_list|(
name|tmpDir
argument_list|,
name|NUM_DIRS_IN_LOG
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|String
name|fname
init|=
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
literal|3
argument_list|)
decl_stmt|;
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
name|fname
argument_list|)
operator|.
name|renameTo
argument_list|(
operator|new
name|File
argument_list|(
name|sharedDir
argument_list|,
name|fname
argument_list|)
argument_list|)
expr_stmt|;
comment|// Checkpoint as fast as we can, in a tight loop.
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|1
argument_list|)
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_PERIOD_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|1
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
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|doEdits
argument_list|(
name|i
operator|*
literal|10
argument_list|,
name|i
operator|*
literal|10
operator|+
literal|10
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|StandbyCheckpointer
operator|.
name|getCanceledCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|doEdits (int start, int stop)
specifier|private
name|void
name|doEdits
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|stop
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|stop
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

