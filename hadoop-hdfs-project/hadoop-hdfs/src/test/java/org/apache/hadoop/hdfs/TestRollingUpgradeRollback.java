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
comment|/**  * This class tests rollback for rolling upgrade.  */
end_comment

begin_class
DECL|class|TestRollingUpgradeRollback
specifier|public
class|class
name|TestRollingUpgradeRollback
block|{
DECL|field|NUM_JOURNAL_NODES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_JOURNAL_NODES
init|=
literal|3
decl_stmt|;
DECL|field|JOURNAL_ID
specifier|private
specifier|static
specifier|final
name|String
name|JOURNAL_ID
init|=
literal|"myjournal"
decl_stmt|;
DECL|method|fileExists (List<File> files)
specifier|private
specifier|static
name|boolean
name|fileExists
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|files
parameter_list|)
block|{
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|checkNNStorage (NNStorage storage, long imageTxId, long trashEndTxId)
specifier|private
name|void
name|checkNNStorage
parameter_list|(
name|NNStorage
name|storage
parameter_list|,
name|long
name|imageTxId
parameter_list|,
name|long
name|trashEndTxId
parameter_list|)
block|{
name|List
argument_list|<
name|File
argument_list|>
name|finalizedEdits
init|=
name|storage
operator|.
name|getFiles
argument_list|(
name|NNStorage
operator|.
name|NameNodeDirType
operator|.
name|EDITS
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
name|imageTxId
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fileExists
argument_list|(
name|finalizedEdits
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|inprogressEdits
init|=
name|storage
operator|.
name|getFiles
argument_list|(
name|NNStorage
operator|.
name|NameNodeDirType
operator|.
name|EDITS
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
name|imageTxId
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// For rollback case we will have an inprogress file for future transactions
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fileExists
argument_list|(
name|inprogressEdits
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|trashEndTxId
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|File
argument_list|>
name|trashedEdits
init|=
name|storage
operator|.
name|getFiles
argument_list|(
name|NNStorage
operator|.
name|NameNodeDirType
operator|.
name|EDITS
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
name|imageTxId
operator|+
literal|1
argument_list|,
name|trashEndTxId
argument_list|)
operator|+
literal|".trash"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fileExists
argument_list|(
name|trashedEdits
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|imageFileName
init|=
name|trashEndTxId
operator|>
literal|0
condition|?
name|NNStorage
operator|.
name|getImageFileName
argument_list|(
name|imageTxId
argument_list|)
else|:
name|NNStorage
operator|.
name|getRollbackImageFileName
argument_list|(
name|imageTxId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|imageFiles
init|=
name|storage
operator|.
name|getFiles
argument_list|(
name|NNStorage
operator|.
name|NameNodeDirType
operator|.
name|IMAGE
argument_list|,
name|imageFileName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fileExists
argument_list|(
name|imageFiles
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkJNStorage (File dir, long discardStartTxId, long discardEndTxId)
specifier|private
name|void
name|checkJNStorage
parameter_list|(
name|File
name|dir
parameter_list|,
name|long
name|discardStartTxId
parameter_list|,
name|long
name|discardEndTxId
parameter_list|)
block|{
name|File
name|finalizedEdits
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
name|discardStartTxId
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|finalizedEdits
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|trashEdits
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
name|discardStartTxId
argument_list|,
name|discardEndTxId
argument_list|)
operator|+
literal|".trash"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|trashEdits
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRollbackCommand ()
specifier|public
name|void
name|testRollbackCommand
parameter_list|()
throws|throws
name|Exception
block|{
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
comment|// start rolling upgrade
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
operator|new
name|String
index|[]
block|{
literal|"-rollingUpgrade"
block|,
literal|"prepare"
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// create new directory
name|dfs
operator|.
name|mkdirs
argument_list|(
name|bar
argument_list|)
expr_stmt|;
comment|// check NNStorage
name|NNStorage
name|storage
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
decl_stmt|;
name|checkNNStorage
argument_list|(
name|storage
argument_list|,
literal|3
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// (startSegment, mkdir, endSegment)
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
name|NameNode
name|nn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|nn
operator|=
name|NameNode
operator|.
name|createNameNode
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-rollingUpgrade"
block|,
literal|"rollback"
block|}
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// make sure /foo is still there, but /bar is not
name|INode
name|fooNode
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode4Write
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|fooNode
argument_list|)
expr_stmt|;
name|INode
name|barNode
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode4Write
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|barNode
argument_list|)
expr_stmt|;
comment|// check the details of NNStorage
name|NNStorage
name|storage
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
decl_stmt|;
comment|// (startSegment, upgrade marker, mkdir, endSegment)
name|checkNNStorage
argument_list|(
name|storage
argument_list|,
literal|3
argument_list|,
literal|7
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|nn
operator|!=
literal|null
condition|)
block|{
name|nn
operator|.
name|stop
argument_list|()
expr_stmt|;
name|nn
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testRollbackWithQJM ()
specifier|public
name|void
name|testRollbackWithQJM
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniJournalCluster
name|mjc
init|=
literal|null
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
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
try|try
block|{
name|mjc
operator|=
operator|new
name|MiniJournalCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numJournalNodes
argument_list|(
name|NUM_JOURNAL_NODES
argument_list|)
operator|.
name|build
argument_list|()
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
name|JOURNAL_ID
argument_list|)
operator|.
name|toString
argument_list|()
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
comment|// start rolling upgrade
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
operator|new
name|String
index|[]
block|{
literal|"-rollingUpgrade"
block|,
literal|"prepare"
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// create new directory
name|dfs
operator|.
name|mkdirs
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// rollback
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|"-rollingUpgrade"
argument_list|,
literal|"rollback"
argument_list|)
expr_stmt|;
comment|// make sure /foo is still there, but /bar is not
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
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
comment|// check storage in JNs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_JOURNAL_NODES
condition|;
name|i
operator|++
control|)
block|{
name|File
name|dir
init|=
name|mjc
operator|.
name|getCurrentDir
argument_list|(
literal|0
argument_list|,
name|JOURNAL_ID
argument_list|)
decl_stmt|;
comment|// segments:(startSegment, mkdir, endSegment), (startSegment, upgrade
comment|// marker, mkdir, endSegment)
name|checkJNStorage
argument_list|(
name|dir
argument_list|,
literal|4
argument_list|,
literal|7
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
if|if
condition|(
name|mjc
operator|!=
literal|null
condition|)
block|{
name|mjc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test rollback scenarios where StandbyNameNode does checkpoints during    * rolling upgrade.    */
annotation|@
name|Test
DECL|method|testRollbackWithHAQJM ()
specifier|public
name|void
name|testRollbackWithHAQJM
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniQJMHACluster
name|cluster
init|=
literal|null
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
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniQJMHACluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|MiniDFSCluster
name|dfsCluster
init|=
name|cluster
operator|.
name|getDfsCluster
argument_list|()
decl_stmt|;
name|dfsCluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// let NN1 do checkpoints as fast as possible
name|dfsCluster
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
name|dfsCluster
operator|.
name|restartNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|DistributedFileSystem
name|dfs
init|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|foo
argument_list|)
expr_stmt|;
comment|// start rolling upgrade
name|RollingUpgradeInfo
name|info
init|=
name|dfs
operator|.
name|rollingUpgrade
argument_list|(
name|RollingUpgradeAction
operator|.
name|PREPARE
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|info
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
comment|// create new directory
name|dfs
operator|.
name|mkdirs
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// rollback NN0
name|dfsCluster
operator|.
name|restartNameNode
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|"-rollingUpgrade"
argument_list|,
literal|"rollback"
argument_list|)
expr_stmt|;
comment|// shutdown NN1
name|dfsCluster
operator|.
name|shutdownNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// make sure /foo is still there, but /bar is not
name|dfs
operator|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|(
literal|0
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
comment|// check the details of NNStorage
name|NNStorage
name|storage
init|=
name|dfsCluster
operator|.
name|getNamesystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
decl_stmt|;
comment|// (startSegment, upgrade marker, mkdir, endSegment)
name|checkNNStorage
argument_list|(
name|storage
argument_list|,
literal|3
argument_list|,
literal|7
argument_list|)
expr_stmt|;
comment|// check storage in JNs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_JOURNAL_NODES
condition|;
name|i
operator|++
control|)
block|{
name|File
name|dir
init|=
name|cluster
operator|.
name|getJournalCluster
argument_list|()
operator|.
name|getCurrentDir
argument_list|(
literal|0
argument_list|,
name|MiniQJMHACluster
operator|.
name|NAMESERVICE
argument_list|)
decl_stmt|;
comment|// segments:(startSegment, mkdir, endSegment), (startSegment, upgrade
comment|// marker, mkdir, endSegment)
name|checkJNStorage
argument_list|(
name|dir
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
expr_stmt|;
block|}
comment|// restart NN0 again to make sure we can start using the new fsimage and
comment|// the corresponding md5 checksum
name|dfsCluster
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
comment|// TODO: rollback could not succeed in all JN
block|}
end_class

end_unit

