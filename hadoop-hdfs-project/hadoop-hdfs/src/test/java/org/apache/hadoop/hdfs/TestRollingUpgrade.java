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
name|HdfsConstants
operator|.
name|RollingUpgradeAction
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
name|protocol
operator|.
name|RollingUpgradeInfo
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
name|tools
operator|.
name|DFSAdmin
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

begin_comment
comment|/**  * This class tests rolling upgrade.  */
end_comment

begin_class
DECL|class|TestRollingUpgrade
specifier|public
class|class
name|TestRollingUpgrade
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
name|TestRollingUpgrade
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|runCmd (DFSAdmin dfsadmin, String... args)
specifier|private
name|void
name|runCmd
parameter_list|(
name|DFSAdmin
name|dfsadmin
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsadmin
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test DFSAdmin Upgrade Command.    */
annotation|@
name|Test
DECL|method|testDFSAdminRollingUpgradeCommands ()
specifier|public
name|void
name|testDFSAdminRollingUpgradeCommands
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start a cluster
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
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
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
literal|"/bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|baz
init|=
operator|new
name|Path
argument_list|(
literal|"/baz"
argument_list|)
decl_stmt|;
block|{
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|DFSAdmin
name|dfsadmin
init|=
operator|new
name|DFSAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|foo
argument_list|)
expr_stmt|;
block|{
comment|//illegal argument
specifier|final
name|String
index|[]
name|args
init|=
block|{
literal|"-rollingUpgrade"
block|,
literal|"abc"
block|}
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfsadmin
operator|.
name|run
argument_list|(
name|args
argument_list|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
block|}
comment|//query rolling upgrade
name|runCmd
argument_list|(
name|dfsadmin
argument_list|,
literal|"-rollingUpgrade"
argument_list|)
expr_stmt|;
comment|//start rolling upgrade
name|runCmd
argument_list|(
name|dfsadmin
argument_list|,
literal|"-rollingUpgrade"
argument_list|,
literal|"start"
argument_list|)
expr_stmt|;
comment|//query rolling upgrade
name|runCmd
argument_list|(
name|dfsadmin
argument_list|,
literal|"-rollingUpgrade"
argument_list|,
literal|"query"
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|bar
argument_list|)
expr_stmt|;
comment|//finalize rolling upgrade
name|runCmd
argument_list|(
name|dfsadmin
argument_list|,
literal|"-rollingUpgrade"
argument_list|,
literal|"finalize"
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|baz
argument_list|)
expr_stmt|;
name|runCmd
argument_list|(
name|dfsadmin
argument_list|,
literal|"-rollingUpgrade"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|foo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|bar
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|baz
argument_list|)
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
block|{
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|foo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|bar
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|baz
argument_list|)
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
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setConf (Configuration conf, File dir, MiniJournalCluster mjc)
specifier|private
specifier|static
name|Configuration
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|File
name|dir
parameter_list|,
name|MiniJournalCluster
name|mjc
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
name|mjc
operator|.
name|getQuorumJournalURI
argument_list|(
literal|"myjournal"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_TXNS_KEY
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testRollingUpgradeWithQJM ()
specifier|public
name|void
name|testRollingUpgradeWithQJM
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|nnDirPrefix
init|=
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"/nn/"
decl_stmt|;
specifier|final
name|File
name|nn1Dir
init|=
operator|new
name|File
argument_list|(
name|nnDirPrefix
operator|+
literal|"image1"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|nn2Dir
init|=
operator|new
name|File
argument_list|(
name|nnDirPrefix
operator|+
literal|"image2"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"nn1Dir="
operator|+
name|nn1Dir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"nn2Dir="
operator|+
name|nn2Dir
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|MiniJournalCluster
name|mjc
init|=
operator|new
name|MiniJournalCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|,
name|nn1Dir
argument_list|,
name|mjc
argument_list|)
expr_stmt|;
block|{
comment|// Start the cluster once to generate the dfs dirs
specifier|final
name|MiniDFSCluster
name|cluster
init|=
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
name|manageNameDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|checkExitOnShutdown
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Shutdown the cluster before making a copy of the namenode dir to release
comment|// all file locks, otherwise, the copy will fail on some platforms.
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|MiniDFSCluster
name|cluster2
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Start a second NN pointed to the same quorum.
comment|// We need to copy the image dir from the first NN -- or else
comment|// the new NN will just be rejected because of Namespace mismatch.
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|nn2Dir
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|copy
argument_list|(
name|nn1Dir
argument_list|,
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getRaw
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
name|nn2Dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Start the cluster again
specifier|final
name|MiniDFSCluster
name|cluster
init|=
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
name|manageNameDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|checkExitOnShutdown
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
literal|"/bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|baz
init|=
operator|new
name|Path
argument_list|(
literal|"/baz"
argument_list|)
decl_stmt|;
specifier|final
name|RollingUpgradeInfo
name|info1
decl_stmt|;
block|{
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|foo
argument_list|)
expr_stmt|;
comment|//start rolling upgrade
name|info1
operator|=
name|dfs
operator|.
name|rollingUpgrade
argument_list|(
name|RollingUpgradeAction
operator|.
name|START
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"START\n"
operator|+
name|info1
argument_list|)
expr_stmt|;
comment|//query rolling upgrade
name|Assert
operator|.
name|assertEquals
argument_list|(
name|info1
argument_list|,
name|dfs
operator|.
name|rollingUpgrade
argument_list|(
name|RollingUpgradeAction
operator|.
name|QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|bar
argument_list|)
expr_stmt|;
block|}
comment|// cluster2 takes over QJM
specifier|final
name|Configuration
name|conf2
init|=
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|nn2Dir
argument_list|,
name|mjc
argument_list|)
decl_stmt|;
name|cluster2
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf2
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
name|manageNameDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|DistributedFileSystem
name|dfs2
init|=
name|cluster2
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Check that cluster2 sees the edits made on cluster1
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|foo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|bar
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|baz
argument_list|)
argument_list|)
expr_stmt|;
comment|//query rolling upgrade in cluster2
name|Assert
operator|.
name|assertEquals
argument_list|(
name|info1
argument_list|,
name|dfs2
operator|.
name|rollingUpgrade
argument_list|(
name|RollingUpgradeAction
operator|.
name|QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|dfs2
operator|.
name|mkdirs
argument_list|(
name|baz
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"RESTART cluster 2"
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|info1
argument_list|,
name|dfs2
operator|.
name|rollingUpgrade
argument_list|(
name|RollingUpgradeAction
operator|.
name|QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|foo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|bar
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|baz
argument_list|)
argument_list|)
expr_stmt|;
comment|//restart cluster with -upgrade should fail.
try|try
block|{
name|cluster2
operator|.
name|restartNameNode
argument_list|(
literal|"-upgrade"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The exception is expected."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"RESTART cluster 2 again"
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|info1
argument_list|,
name|dfs2
operator|.
name|rollingUpgrade
argument_list|(
name|RollingUpgradeAction
operator|.
name|QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|foo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|bar
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|baz
argument_list|)
argument_list|)
expr_stmt|;
comment|//finalize rolling upgrade
specifier|final
name|RollingUpgradeInfo
name|finalize
init|=
name|dfs2
operator|.
name|rollingUpgrade
argument_list|(
name|RollingUpgradeAction
operator|.
name|FINALIZE
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"FINALIZE: "
operator|+
name|finalize
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|info1
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|finalize
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"RESTART cluster 2 with regular startup option"
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|getNameNodeInfos
argument_list|()
index|[
literal|0
index|]
operator|.
name|setStartOpt
argument_list|(
name|StartupOption
operator|.
name|REGULAR
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|foo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|bar
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs2
operator|.
name|exists
argument_list|(
name|baz
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster2
operator|!=
literal|null
condition|)
name|cluster2
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRollback ()
specifier|public
name|void
name|testRollback
parameter_list|()
throws|throws
name|IOException
block|{
comment|// start a cluster
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
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
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
literal|"/bar"
argument_list|)
decl_stmt|;
block|{
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|foo
argument_list|)
expr_stmt|;
comment|//start rolling upgrade
name|dfs
operator|.
name|rollingUpgrade
argument_list|(
name|RollingUpgradeAction
operator|.
name|START
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|foo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|bar
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Restart should succeed!
comment|//      cluster.restartNameNode();
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|"-rollingUpgrade"
argument_list|,
literal|"rollback"
argument_list|)
expr_stmt|;
block|{
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|foo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|bar
argument_list|)
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
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDFSAdminDatanodeUpgradeControlCommands ()
specifier|public
name|void
name|testDFSAdminDatanodeUpgradeControlCommands
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start a cluster
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
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
specifier|final
name|DFSAdmin
name|dfsadmin
init|=
operator|new
name|DFSAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// check the datanode
specifier|final
name|String
name|dnAddr
init|=
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getIpcAddr
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|args1
init|=
block|{
literal|"-getDatanodeInfo"
block|,
name|dnAddr
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsadmin
operator|.
name|run
argument_list|(
name|args1
argument_list|)
argument_list|)
expr_stmt|;
comment|// issue shutdown to the datanode.
specifier|final
name|String
index|[]
name|args2
init|=
block|{
literal|"-shutdownDatanode"
block|,
name|dnAddr
block|,
literal|"upgrade"
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsadmin
operator|.
name|run
argument_list|(
name|args2
argument_list|)
argument_list|)
expr_stmt|;
comment|// the datanode should be down.
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"DataNode should exit"
argument_list|,
name|dn
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
comment|// ping should fail.
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|dfsadmin
operator|.
name|run
argument_list|(
name|args1
argument_list|)
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
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

