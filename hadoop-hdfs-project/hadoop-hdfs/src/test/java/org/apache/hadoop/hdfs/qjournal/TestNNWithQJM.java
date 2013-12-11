begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
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
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|ipc
operator|.
name|RemoteException
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
name|util
operator|.
name|ExitUtil
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

begin_class
DECL|class|TestNNWithQJM
specifier|public
class|class
name|TestNNWithQJM
block|{
DECL|field|conf
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|mjc
specifier|private
name|MiniJournalCluster
name|mjc
init|=
literal|null
decl_stmt|;
DECL|field|TEST_PATH
specifier|private
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/test-dir"
argument_list|)
decl_stmt|;
DECL|field|TEST_PATH_2
specifier|private
name|Path
name|TEST_PATH_2
init|=
operator|new
name|Path
argument_list|(
literal|"/test-dir"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|resetSystemExit ()
specifier|public
name|void
name|resetSystemExit
parameter_list|()
block|{
name|ExitUtil
operator|.
name|resetFirstExitException
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|startJNs ()
specifier|public
name|void
name|startJNs
parameter_list|()
throws|throws
name|Exception
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
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|stopJNs ()
specifier|public
name|void
name|stopJNs
parameter_list|()
throws|throws
name|Exception
block|{
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
name|mjc
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testLogAndRestart ()
specifier|public
name|void
name|testLogAndRestart
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"/TestNNWithQJM/image"
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
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|mkdirs
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
comment|// Restart the NN and make sure the edit was persisted
comment|// and loaded again
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|exists
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|mkdirs
argument_list|(
name|TEST_PATH_2
argument_list|)
expr_stmt|;
comment|// Restart the NN again and make sure both edits are persisted.
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|exists
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|exists
argument_list|(
name|TEST_PATH_2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
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
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testNewNamenodeTakesOverWriter ()
specifier|public
name|void
name|testNewNamenodeTakesOverWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|nn1Dir
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"/TestNNWithQJM/image-nn1"
argument_list|)
decl_stmt|;
name|File
name|nn2Dir
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"/TestNNWithQJM/image-nn2"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|nn1Dir
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
comment|// Start the cluster once to generate the dfs dirs
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
comment|// Shutdown the cluster before making a copy of the namenode dir
comment|// to release all file locks, otherwise, the copy will fail on
comment|// some platforms.
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
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
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|mkdirs
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
name|Configuration
name|conf2
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|nn2Dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf2
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
name|MiniDFSCluster
name|cluster2
init|=
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
decl_stmt|;
comment|// Check that the new cluster sees the edits made on the old cluster
try|try
block|{
name|assertTrue
argument_list|(
name|cluster2
operator|.
name|getFileSystem
argument_list|()
operator|.
name|exists
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster2
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// Check that, if we try to write to the old NN
comment|// that it aborts.
try|try
block|{
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/x"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not abort trying to write to a fenced NN"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Could not sync enough journals to persistent storage"
argument_list|,
name|re
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|//cluster.shutdown();
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testMismatchedNNIsRejected ()
specifier|public
name|void
name|testMismatchedNNIsRejected
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"/TestNNWithQJM/image"
argument_list|)
expr_stmt|;
name|String
name|defaultEditsDir
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|)
decl_stmt|;
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
comment|// Start a NN, so the storage is formatted -- both on-disk
comment|// and QJM.
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
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// Reformat just the on-disk portion
name|Configuration
name|onDiskOnly
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|onDiskOnly
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
name|defaultEditsDir
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|format
argument_list|(
name|onDiskOnly
argument_list|)
expr_stmt|;
comment|// Start the NN - should fail because the JNs are still formatted
comment|// with the old namespace ID.
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
name|manageNameDfsDirs
argument_list|(
literal|false
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
name|fail
argument_list|(
literal|"New NN with different namespace should have been rejected"
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
literal|"Unable to start log segment 1: too few journals"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testWebPageHasQjmInfo ()
specifier|public
name|void
name|testWebPageHasQjmInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"/TestNNWithQJM/image"
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
comment|// Speed up the test
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
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
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|NameNode
operator|.
name|getHttpAddress
argument_list|(
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|getPort
argument_list|()
operator|+
literal|"/dfshealth.jsp"
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|mkdirs
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
name|String
name|contents
init|=
name|DFSTestUtil
operator|.
name|urlGet
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|contents
operator|.
name|contains
argument_list|(
literal|"QJM to ["
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|contents
operator|.
name|contains
argument_list|(
literal|"Written txid 2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Stop one JN, do another txn, and make sure it shows as behind
comment|// stuck behind the others.
name|mjc
operator|.
name|getJournalNode
argument_list|(
literal|0
argument_list|)
operator|.
name|stopAndJoin
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|TEST_PATH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|contents
operator|=
name|DFSTestUtil
operator|.
name|urlGet
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"1 txns/\\d+ms behind"
argument_list|)
operator|.
name|matcher
argument_list|(
name|contents
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
comment|// Restart NN while JN0 is still down.
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|contents
operator|=
name|DFSTestUtil
operator|.
name|urlGet
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"never written"
argument_list|)
operator|.
name|matcher
argument_list|(
name|contents
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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

