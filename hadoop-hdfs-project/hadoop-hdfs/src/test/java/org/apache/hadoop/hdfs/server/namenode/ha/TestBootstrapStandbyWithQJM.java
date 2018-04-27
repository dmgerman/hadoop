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
name|CommonConfigurationKeysPublic
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
name|qjournal
operator|.
name|MiniJournalCluster
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
name|qjournal
operator|.
name|MiniQJMHACluster
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
name|test
operator|.
name|Whitebox
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

begin_comment
comment|/**  * Test BootstrapStandby when QJM is used for shared edits.   */
end_comment

begin_class
DECL|class|TestBootstrapStandbyWithQJM
specifier|public
class|class
name|TestBootstrapStandbyWithQJM
block|{
DECL|enum|UpgradeState
enum|enum
name|UpgradeState
block|{
DECL|enumConstant|NORMAL
name|NORMAL
block|,
DECL|enumConstant|RECOVER
name|RECOVER
block|,
DECL|enumConstant|FORMAT
name|FORMAT
block|}
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|jCluster
specifier|private
name|MiniJournalCluster
name|jCluster
decl_stmt|;
DECL|field|nnCount
specifier|private
name|int
name|nnCount
init|=
literal|3
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
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
comment|// Turn off IPC client caching, so that the suite can handle
comment|// the restart of the daemons between test cases.
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|MiniQJMHACluster
name|miniQjmHaCluster
init|=
operator|new
name|MiniQJMHACluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumNameNodes
argument_list|(
name|nnCount
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|=
name|miniQjmHaCluster
operator|.
name|getDfsCluster
argument_list|()
expr_stmt|;
name|jCluster
operator|=
name|miniQjmHaCluster
operator|.
name|getJournalCluster
argument_list|()
expr_stmt|;
comment|// make nn0 active
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// do sth to generate in-progress edit log data
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test2"
argument_list|)
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|jCluster
operator|!=
literal|null
condition|)
block|{
name|jCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|jCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** BootstrapStandby when the existing NN is standby */
annotation|@
name|Test
DECL|method|testBootstrapStandbyWithStandbyNN ()
specifier|public
name|void
name|testBootstrapStandbyWithStandbyNN
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make the first NN in standby state
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bootstrapStandbys
argument_list|()
expr_stmt|;
block|}
comment|/** BootstrapStandby when the existing NN is active */
annotation|@
name|Test
DECL|method|testBootstrapStandbyWithActiveNN ()
specifier|public
name|void
name|testBootstrapStandbyWithActiveNN
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make the first NN in active state
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bootstrapStandbys
argument_list|()
expr_stmt|;
block|}
DECL|method|bootstrapStandbys ()
specifier|private
name|void
name|bootstrapStandbys
parameter_list|()
throws|throws
name|Exception
block|{
comment|// shutdown and bootstrap all the other nns, except the first (start 1, not 0)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|nnCount
condition|;
name|i
operator|++
control|)
block|{
name|Configuration
name|otherNNConf
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// shut down other nn
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
name|i
argument_list|)
expr_stmt|;
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
literal|"-force"
block|}
argument_list|,
name|otherNNConf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
comment|// Should have copied over the namespace from the standby
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
literal|0
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
block|}
comment|/**    * Test the bootstrapstandby while the other namenode is in upgrade state.    * Make sure a previous directory can be created.    */
annotation|@
name|Test
DECL|method|testUpgrade ()
specifier|public
name|void
name|testUpgrade
parameter_list|()
throws|throws
name|Exception
block|{
name|testUpgrade
argument_list|(
name|UpgradeState
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
block|}
comment|/**    * Similar with testUpgrade, but rename nn1's current directory to    * previous.tmp before bootstrapStandby, and make sure the nn1 is recovered    * first then converted into upgrade state.    */
annotation|@
name|Test
DECL|method|testUpgradeWithRecover ()
specifier|public
name|void
name|testUpgradeWithRecover
parameter_list|()
throws|throws
name|Exception
block|{
name|testUpgrade
argument_list|(
name|UpgradeState
operator|.
name|RECOVER
argument_list|)
expr_stmt|;
block|}
comment|/**    * Similar with testUpgrade, but rename nn1's current directory to a random    * name so that it's not formatted. Make sure the nn1 is formatted and then    * converted into upgrade state.    */
annotation|@
name|Test
DECL|method|testUpgradeWithFormat ()
specifier|public
name|void
name|testUpgradeWithFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|testUpgrade
argument_list|(
name|UpgradeState
operator|.
name|FORMAT
argument_list|)
expr_stmt|;
block|}
DECL|method|testUpgrade (UpgradeState state)
specifier|private
name|void
name|testUpgrade
parameter_list|(
name|UpgradeState
name|state
parameter_list|)
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|confNN1
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|File
name|current
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|)
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
specifier|final
name|File
name|tmp
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|)
operator|.
name|getPreviousTmp
argument_list|()
decl_stmt|;
comment|// shut down nn1
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// make NN0 in upgrade state
name|FSImage
name|fsImage0
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
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|fsImage0
argument_list|,
literal|"isUpgradeFinalized"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|RECOVER
case|:
comment|// rename the current directory to previous.tmp in nn1
name|NNStorage
operator|.
name|rename
argument_list|(
name|current
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
break|break;
case|case
name|FORMAT
case|:
comment|// rename the current directory to a random name so it's not formatted
specifier|final
name|File
name|wrongPath
init|=
operator|new
name|File
argument_list|(
name|current
operator|.
name|getParentFile
argument_list|()
argument_list|,
literal|"wrong"
argument_list|)
decl_stmt|;
name|NNStorage
operator|.
name|rename
argument_list|(
name|current
argument_list|,
name|wrongPath
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
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
literal|"-force"
block|}
argument_list|,
name|confNN1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
comment|// Should have copied over the namespace from the standby
name|FSImageTestUtil
operator|.
name|assertNNHasCheckpoints
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
argument_list|)
argument_list|)
expr_stmt|;
name|FSImageTestUtil
operator|.
name|assertNNFilesMatch
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
comment|// make sure the NN1 is in upgrade state, i.e., the previous directory has
comment|// been successfully created
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
operator|.
name|getNamesystem
argument_list|()
operator|.
name|isUpgradeFinalized
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

