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
name|concurrent
operator|.
name|TimeoutException
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
name|FileUtil
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
name|CheckpointSignature
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
name|LogCapturer
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
DECL|class|TestBootstrapStandby
specifier|public
class|class
name|TestBootstrapStandby
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
name|TestBootstrapStandby
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxNNCount
specifier|private
specifier|static
specifier|final
name|int
name|maxNNCount
init|=
literal|3
decl_stmt|;
DECL|field|STARTING_PORT
specifier|private
specifier|static
specifier|final
name|int
name|STARTING_PORT
init|=
literal|20000
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
annotation|@
name|Before
DECL|method|setupCluster ()
specifier|public
name|void
name|setupCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// duplicate code with MiniQJMHACluster#createDefaultTopology, but don't want to cross
comment|// dependencies or munge too much code to support it all correctly
name|MiniDFSNNTopology
operator|.
name|NSConf
name|nameservice
init|=
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"ns1"
argument_list|)
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
name|maxNNCount
condition|;
name|i
operator|++
control|)
block|{
name|nameservice
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn"
operator|+
name|i
argument_list|)
operator|.
name|setHttpPort
argument_list|(
name|STARTING_PORT
operator|+
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
name|nameservice
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
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// shutdown the other NNs
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|maxNNCount
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|shutdownCluster ()
specifier|public
name|void
name|shutdownCluster
parameter_list|()
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
comment|/**    * Test for the base success case. The primary NN    * hasn't made any checkpoints, and we copy the fsimage_0    * file over and start up.    */
annotation|@
name|Test
DECL|method|testSuccessfulBaseCase ()
specifier|public
name|void
name|testSuccessfulBaseCase
parameter_list|()
throws|throws
name|Exception
block|{
name|removeStandbyNameDirs
argument_list|()
expr_stmt|;
comment|// skip the first NN, its up
for|for
control|(
name|int
name|index
init|=
literal|1
init|;
name|index
operator|<
name|maxNNCount
condition|;
name|index
operator|++
control|)
block|{
try|try
block|{
name|cluster
operator|.
name|restartNameNode
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"storage directory does not exist or is not accessible"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
name|int
name|rc
init|=
name|BootstrapStandby
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-nonInteractive"
block|}
argument_list|,
name|cluster
operator|.
name|getConfiguration
argument_list|(
name|index
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
comment|// Should have copied over the namespace from the active
name|FSImageTestUtil
operator|.
name|assertNNHasCheckpoints
argument_list|(
name|cluster
argument_list|,
name|index
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// We should now be able to start the standbys successfully.
name|restartNameNodesFromIndex
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for downloading a checkpoint made at a later checkpoint    * from the active.    */
annotation|@
name|Test
DECL|method|testDownloadingLaterCheckpoint ()
specifier|public
name|void
name|testDownloadingLaterCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Roll edit logs a few times to inflate txid
name|nn0
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|nn0
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
comment|// Make checkpoint
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nn0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|nn0
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|nn0
argument_list|)
expr_stmt|;
name|long
name|expectedCheckpointTxId
init|=
name|NameNodeAdapter
operator|.
name|getNamesystem
argument_list|(
name|nn0
argument_list|)
operator|.
name|getFSImage
argument_list|()
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|expectedCheckpointTxId
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
name|maxNNCount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|forceBootstrap
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should have copied over the namespace from the active
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking namenode: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|FSImageTestUtil
operator|.
name|assertNNHasCheckpoints
argument_list|(
name|cluster
argument_list|,
name|i
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
operator|(
name|int
operator|)
name|expectedCheckpointTxId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FSImageTestUtil
operator|.
name|assertNNFilesMatch
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
comment|// We should now be able to start the standby successfully.
name|restartNameNodesFromIndex
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for the case where the shared edits dir doesn't have    * all of the recent edit logs.    */
annotation|@
name|Test
DECL|method|testSharedEditsMissingLogs ()
specifier|public
name|void
name|testSharedEditsMissingLogs
parameter_list|()
throws|throws
name|Exception
block|{
name|removeStandbyNameDirs
argument_list|()
expr_stmt|;
name|CheckpointSignature
name|sig
init|=
name|nn0
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|sig
operator|.
name|getCurSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should have created edits_1-2 in shared edits dir
name|URI
name|editsUri
init|=
name|cluster
operator|.
name|getSharedEditsDir
argument_list|(
literal|0
argument_list|,
name|maxNNCount
operator|-
literal|1
argument_list|)
decl_stmt|;
name|File
name|editsDir
init|=
operator|new
name|File
argument_list|(
name|editsUri
argument_list|)
decl_stmt|;
name|File
name|currentDir
init|=
operator|new
name|File
argument_list|(
name|editsDir
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
name|File
name|editsSegment
init|=
operator|new
name|File
argument_list|(
name|currentDir
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|editsSegment
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|currentDir
argument_list|)
expr_stmt|;
comment|// Delete the segment.
name|assertTrue
argument_list|(
name|editsSegment
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
comment|// Trying to bootstrap standby should now fail since the edit
comment|// logs aren't available in the shared dir.
name|LogCapturer
name|logs
init|=
name|GenericTestUtils
operator|.
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BootstrapStandby
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|BootstrapStandby
operator|.
name|ERR_CODE_LOGS_UNAVAILABLE
argument_list|,
name|forceBootstrap
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|logs
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
block|}
name|GenericTestUtils
operator|.
name|assertMatches
argument_list|(
name|logs
operator|.
name|getOutput
argument_list|()
argument_list|,
literal|"FATAL.*Unable to read transaction ids 1-3 from the configured shared"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Show that bootstrapping will fail on a given NameNode if its directories already exist. Its not    * run across all the NN because its testing the state local on each node.    * @throws Exception on unexpected failure    */
annotation|@
name|Test
DECL|method|testStandbyDirsAlreadyExist ()
specifier|public
name|void
name|testStandbyDirsAlreadyExist
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Should not pass since standby dirs exist, force not given
name|int
name|rc
init|=
name|BootstrapStandby
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-nonInteractive"
block|}
argument_list|,
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|BootstrapStandby
operator|.
name|ERR_CODE_ALREADY_FORMATTED
argument_list|,
name|rc
argument_list|)
expr_stmt|;
comment|// Should pass with -force
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|forceBootstrap
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that, even if the other node is not active, we are able    * to bootstrap standby from it.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testOtherNodeNotActive ()
specifier|public
name|void
name|testOtherNodeNotActive
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
name|assertSuccessfulBootstrapFromIndex
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that bootstrapping standby NN is not limited by    * {@link DFSConfigKeys#DFS_IMAGE_TRANSFER_RATE_KEY}, but is limited by    * {@link DFSConfigKeys#DFS_IMAGE_TRANSFER_BOOTSTRAP_STANDBY_RATE_KEY}    * created by HDFS-8808.    */
annotation|@
name|Test
DECL|method|testRateThrottling ()
specifier|public
name|void
name|testRateThrottling
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_TRANSFER_RATE_KEY
argument_list|,
literal|1
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
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Each edit has at least 1 byte. So the lowRate definitely should cause
comment|// a timeout, if enforced. If lowRate is not enforced, any reasonable test
comment|// machine should at least download an image with 5 edits in 5 seconds.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|nn0
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
block|}
comment|// A very low DFS_IMAGE_TRANSFER_RATE_KEY value won't affect bootstrapping
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
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
try|try
block|{
name|testSuccessfulBaseCase
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|500
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|shutdownCluster
argument_list|()
expr_stmt|;
name|setupCluster
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_TRANSFER_BOOTSTRAP_STANDBY_RATE_KEY
argument_list|,
literal|1
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
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// A very low DFS_IMAGE_TRANSFER_BOOTSTRAP_STANDBY_RATE_KEY value should
comment|// cause timeout
try|try
block|{
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
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
try|try
block|{
name|testSuccessfulBaseCase
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|500
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not timeout"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Encountered expected timeout."
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeStandbyNameDirs ()
specifier|private
name|void
name|removeStandbyNameDirs
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|maxNNCount
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|URI
name|u
range|:
name|cluster
operator|.
name|getNameDirs
argument_list|(
name|i
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|u
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|u
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing standby dir "
operator|+
name|dir
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|restartNameNodesFromIndex (int start)
specifier|private
name|void
name|restartNameNodesFromIndex
parameter_list|(
name|int
name|start
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
name|maxNNCount
condition|;
name|i
operator|++
control|)
block|{
comment|// We should now be able to start the standby successfully.
name|cluster
operator|.
name|restartNameNode
argument_list|(
name|i
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
block|}
comment|/**    * Force boot strapping on a namenode    * @param i index of the namenode to attempt    * @return exit code    * @throws Exception on unexpected failure    */
DECL|method|forceBootstrap (int i)
specifier|private
name|int
name|forceBootstrap
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|BootstrapStandby
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-force"
block|}
argument_list|,
name|cluster
operator|.
name|getConfiguration
argument_list|(
name|i
argument_list|)
argument_list|)
return|;
block|}
DECL|method|assertSuccessfulBootstrapFromIndex (int start)
specifier|private
name|void
name|assertSuccessfulBootstrapFromIndex
parameter_list|(
name|int
name|start
parameter_list|)
throws|throws
name|Exception
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
name|maxNNCount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|forceBootstrap
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

