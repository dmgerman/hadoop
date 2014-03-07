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
name|io
operator|.
name|OutputStream
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
name|net
operator|.
name|URL
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
name|JournalSet
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
name|NNStorage
operator|.
name|NameNodeFile
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
name|hdfs
operator|.
name|util
operator|.
name|Canceler
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
name|compress
operator|.
name|CompressionCodecFactory
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
name|compress
operator|.
name|CompressionOutputStream
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
name|compress
operator|.
name|GzipCodec
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
name|ipc
operator|.
name|StandbyException
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ThreadUtil
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
specifier|protected
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|nn0
DECL|field|nn1
specifier|protected
name|NameNode
name|nn0
decl_stmt|,
name|nn1
decl_stmt|;
DECL|field|fs
specifier|protected
name|FileSystem
name|fs
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
name|TestStandbyCheckpoints
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
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
comment|// Dial down the retention of extra edits and checkpoints. This is to
comment|// help catch regressions of HDFS-4238 (SBN should not purge shared edits)
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NUM_CHECKPOINTS_RETAINED_KEY
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
name|DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_COMPRESS_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_COMPRESSION_CODEC_KEY
argument_list|,
name|SlowCodec
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|CompressionCodecFactory
operator|.
name|setCodecClasses
argument_list|(
name|conf
argument_list|,
name|ImmutableList
operator|.
expr|<
name|Class
operator|>
name|of
argument_list|(
name|SlowCodec
operator|.
name|class
argument_list|)
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
literal|10061
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
literal|10062
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
name|JournalSet
name|standbyJournalSet
init|=
name|NameNodeAdapter
operator|.
name|spyOnJournalSet
argument_list|(
name|nn1
argument_list|)
decl_stmt|;
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
literal|12
argument_list|)
argument_list|)
expr_stmt|;
comment|// The standby should never try to purge edit logs on shared storage.
name|Mockito
operator|.
name|verify
argument_list|(
name|standbyJournalSet
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|purgeLogsOlderThan
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
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
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
name|NameNodeFile
operator|.
name|IMAGE
argument_list|)
argument_list|,
operator|(
name|Canceler
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
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
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
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|0
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
argument_list|,
name|fsn
operator|.
name|getLastInodeId
argument_list|()
operator|+
literal|1
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
name|boolean
name|canceledOne
init|=
literal|false
decl_stmt|;
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
operator|&&
operator|!
name|canceledOne
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
name|canceledOne
operator|=
name|StandbyCheckpointer
operator|.
name|getCanceledCount
argument_list|()
operator|>
literal|0
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|canceledOne
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test cancellation of ongoing checkpoints when failover happens    * mid-checkpoint during image upload from standby to active NN.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCheckpointCancellationDuringUpload ()
specifier|public
name|void
name|testCheckpointCancellationDuringUpload
parameter_list|()
throws|throws
name|Exception
block|{
comment|// don't compress, we want a big image
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_COMPRESS_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|1
argument_list|)
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_COMPRESS_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Throttle SBN upload to make it hang during upload to ANN
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|1
argument_list|)
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_TRANSFER_RATE_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
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
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|doEdits
argument_list|(
literal|0
argument_list|,
literal|100
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
literal|104
argument_list|)
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
block|}
comment|/**    * Make sure that clients will receive StandbyExceptions even when a    * checkpoint is in progress on the SBN, and therefore the StandbyCheckpointer    * thread will have FSNS lock. Regression test for HDFS-4591.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testStandbyExceptionThrownDuringCheckpoint ()
specifier|public
name|void
name|testStandbyExceptionThrownDuringCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Set it up so that we know when the SBN checkpoint starts and ends.
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
name|DelayAnswer
name|answerer
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
name|answerer
argument_list|)
operator|.
name|when
argument_list|(
name|spyImage1
argument_list|)
operator|.
name|saveNamespace
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
name|NameNodeFile
operator|.
name|IMAGE
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|Canceler
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Perform some edits and wait for a checkpoint to start on the SBN.
name|doEdits
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|nn0
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|answerer
operator|.
name|waitForCall
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"SBN is not performing checkpoint but it should be."
argument_list|,
name|answerer
operator|.
name|getFireCount
argument_list|()
operator|==
literal|1
operator|&&
name|answerer
operator|.
name|getResultCount
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// Make sure that the lock has actually been taken by the checkpointing
comment|// thread.
name|ThreadUtil
operator|.
name|sleepAtLeastIgnoreInterrupts
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Perform an RPC to the SBN and make sure it throws a StandbyException.
name|nn1
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getFileInfo
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown StandbyException, but instead succeeded."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StandbyException
name|se
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"is not supported"
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
comment|// Make sure that the checkpoint is still going on, implying that the client
comment|// RPC to the SBN happened during the checkpoint.
name|assertTrue
argument_list|(
literal|"SBN should have still been checkpointing."
argument_list|,
name|answerer
operator|.
name|getFireCount
argument_list|()
operator|==
literal|1
operator|&&
name|answerer
operator|.
name|getResultCount
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|answerer
operator|.
name|proceed
argument_list|()
expr_stmt|;
name|answerer
operator|.
name|waitForResult
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"SBN should have finished checkpointing."
argument_list|,
name|answerer
operator|.
name|getFireCount
argument_list|()
operator|==
literal|1
operator|&&
name|answerer
operator|.
name|getResultCount
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testReadsAllowedDuringCheckpoint ()
specifier|public
name|void
name|testReadsAllowedDuringCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Set it up so that we know when the SBN checkpoint starts and ends.
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
name|DelayAnswer
name|answerer
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
name|answerer
argument_list|)
operator|.
name|when
argument_list|(
name|spyImage1
argument_list|)
operator|.
name|saveNamespace
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|NameNodeFile
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|Canceler
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Perform some edits and wait for a checkpoint to start on the SBN.
name|doEdits
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|nn0
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|answerer
operator|.
name|waitForCall
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"SBN is not performing checkpoint but it should be."
argument_list|,
name|answerer
operator|.
name|getFireCount
argument_list|()
operator|==
literal|1
operator|&&
name|answerer
operator|.
name|getResultCount
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// Make sure that the lock has actually been taken by the checkpointing
comment|// thread.
name|ThreadUtil
operator|.
name|sleepAtLeastIgnoreInterrupts
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Perform an RPC that needs to take the write lock.
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|nn1
operator|.
name|getRpcServer
argument_list|()
operator|.
name|restoreFailedStorage
argument_list|(
literal|"false"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Make sure that our thread is waiting for the lock.
name|ThreadUtil
operator|.
name|sleepAtLeastIgnoreInterrupts
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFsLockForTests
argument_list|()
operator|.
name|hasQueuedThreads
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFsLockForTests
argument_list|()
operator|.
name|isWriteLocked
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getLongReadLockForTests
argument_list|()
operator|.
name|hasQueuedThreads
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get /jmx of the standby NN web UI, which will cause the FSNS read lock to
comment|// be taken.
name|String
name|pageContents
init|=
name|DFSTestUtil
operator|.
name|urlGet
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://"
operator|+
name|nn1
operator|.
name|getHttpAddress
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|nn1
operator|.
name|getHttpAddress
argument_list|()
operator|.
name|getPort
argument_list|()
operator|+
literal|"/jmx"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pageContents
operator|.
name|contains
argument_list|(
literal|"NumLiveDataNodes"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure that the checkpoint is still going on, implying that the client
comment|// RPC to the SBN happened during the checkpoint.
name|assertTrue
argument_list|(
literal|"SBN should have still been checkpointing."
argument_list|,
name|answerer
operator|.
name|getFireCount
argument_list|()
operator|==
literal|1
operator|&&
name|answerer
operator|.
name|getResultCount
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|answerer
operator|.
name|proceed
argument_list|()
expr_stmt|;
name|answerer
operator|.
name|waitForResult
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"SBN should have finished checkpointing."
argument_list|,
name|answerer
operator|.
name|getFireCount
argument_list|()
operator|==
literal|1
operator|&&
name|answerer
operator|.
name|getResultCount
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
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
comment|/**    * A codec which just slows down the saving of the image significantly    * by sleeping a few milliseconds on every write. This makes it easy to    * catch the standby in the middle of saving a checkpoint.    */
DECL|class|SlowCodec
specifier|public
specifier|static
class|class
name|SlowCodec
extends|extends
name|GzipCodec
block|{
annotation|@
name|Override
DECL|method|createOutputStream (OutputStream out)
specifier|public
name|CompressionOutputStream
name|createOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|CompressionOutputStream
name|ret
init|=
name|super
operator|.
name|createOutputStream
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|CompressionOutputStream
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|ret
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|GenericTestUtils
operator|.
name|SleepAnswer
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spy
argument_list|)
operator|.
name|write
argument_list|(
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spy
return|;
block|}
block|}
block|}
end_class

end_unit

