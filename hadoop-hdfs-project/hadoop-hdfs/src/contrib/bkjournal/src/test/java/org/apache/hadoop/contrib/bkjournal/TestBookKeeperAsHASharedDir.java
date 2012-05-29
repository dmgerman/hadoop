begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.bkjournal
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|bkjournal
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|server
operator|.
name|namenode
operator|.
name|ha
operator|.
name|HATestUtil
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
name|FSEditLogTestUtil
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
name|bookkeeper
operator|.
name|proto
operator|.
name|BookieServer
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|atLeastOnce
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

begin_comment
comment|/**  * Integration test to ensure that the BookKeeper JournalManager  * works for HDFS Namenode HA  */
end_comment

begin_class
DECL|class|TestBookKeeperAsHASharedDir
specifier|public
class|class
name|TestBookKeeperAsHASharedDir
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestBookKeeperAsHASharedDir
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|bkutil
specifier|private
specifier|static
name|BKJMUtil
name|bkutil
decl_stmt|;
DECL|field|numBookies
specifier|static
name|int
name|numBookies
init|=
literal|3
decl_stmt|;
DECL|field|TEST_FILE_DATA
specifier|private
specifier|static
specifier|final
name|String
name|TEST_FILE_DATA
init|=
literal|"HA BookKeeperJournalManager"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupBookkeeper ()
specifier|public
specifier|static
name|void
name|setupBookkeeper
parameter_list|()
throws|throws
name|Exception
block|{
name|bkutil
operator|=
operator|new
name|BKJMUtil
argument_list|(
name|numBookies
argument_list|)
expr_stmt|;
name|bkutil
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownBookkeeper ()
specifier|public
specifier|static
name|void
name|teardownBookkeeper
parameter_list|()
throws|throws
name|Exception
block|{
name|bkutil
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test simple HA failover usecase with BK    */
annotation|@
name|Test
DECL|method|testFailoverWithBK ()
specifier|public
name|void
name|testFailoverWithBK
parameter_list|()
throws|throws
name|Exception
block|{
name|Runtime
name|mockRuntime1
init|=
name|mock
argument_list|(
name|Runtime
operator|.
name|class
argument_list|)
decl_stmt|;
name|Runtime
name|mockRuntime2
init|=
name|mock
argument_list|(
name|Runtime
operator|.
name|class
argument_list|)
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
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
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
argument_list|,
name|BKJMUtil
operator|.
name|createJournalURI
argument_list|(
literal|"/hotfailover"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|BKJMUtil
operator|.
name|addJournalManagerDefinition
argument_list|(
name|conf
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
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|manageNameDfsSharedDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|NameNode
name|nn1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NameNode
name|nn2
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|FSEditLogTestUtil
operator|.
name|setRuntimeForEditLog
argument_list|(
name|nn1
argument_list|,
name|mockRuntime1
argument_list|)
expr_stmt|;
name|FSEditLogTestUtil
operator|.
name|setRuntimeForEditLog
argument_list|(
name|nn2
argument_list|,
name|mockRuntime2
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/testBKJMfailover"
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdownNameNode
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
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|verify
argument_list|(
name|mockRuntime1
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
name|verify
argument_list|(
name|mockRuntime2
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
comment|/**    * Test HA failover, where BK, as the shared storage, fails.    * Once it becomes available again, a standby can come up.    * Verify that any write happening after the BK fail is not    * available on the standby.    */
annotation|@
name|Test
DECL|method|testFailoverWithFailingBKCluster ()
specifier|public
name|void
name|testFailoverWithFailingBKCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|ensembleSize
init|=
name|numBookies
operator|+
literal|1
decl_stmt|;
name|BookieServer
name|newBookie
init|=
name|bkutil
operator|.
name|newBookie
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"New bookie didn't start"
argument_list|,
name|ensembleSize
argument_list|,
name|bkutil
operator|.
name|checkBookiesUp
argument_list|(
name|ensembleSize
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|BookieServer
name|replacementBookie
init|=
literal|null
decl_stmt|;
name|Runtime
name|mockRuntime1
init|=
name|mock
argument_list|(
name|Runtime
operator|.
name|class
argument_list|)
decl_stmt|;
name|Runtime
name|mockRuntime2
init|=
name|mock
argument_list|(
name|Runtime
operator|.
name|class
argument_list|)
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
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
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
argument_list|,
name|BKJMUtil
operator|.
name|createJournalURI
argument_list|(
literal|"/hotfailoverWithFail"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|BookKeeperJournalManager
operator|.
name|BKJM_BOOKKEEPER_ENSEMBLE_SIZE
argument_list|,
name|ensembleSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|BookKeeperJournalManager
operator|.
name|BKJM_BOOKKEEPER_QUORUM_SIZE
argument_list|,
name|ensembleSize
argument_list|)
expr_stmt|;
name|BKJMUtil
operator|.
name|addJournalManagerDefinition
argument_list|(
name|conf
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
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|manageNameDfsSharedDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|NameNode
name|nn1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NameNode
name|nn2
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|FSEditLogTestUtil
operator|.
name|setRuntimeForEditLog
argument_list|(
name|nn1
argument_list|,
name|mockRuntime1
argument_list|)
expr_stmt|;
name|FSEditLogTestUtil
operator|.
name|setRuntimeForEditLog
argument_list|(
name|nn2
argument_list|,
name|mockRuntime2
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Path
name|p1
init|=
operator|new
name|Path
argument_list|(
literal|"/testBKJMFailingBKCluster1"
argument_list|)
decl_stmt|;
name|Path
name|p2
init|=
operator|new
name|Path
argument_list|(
literal|"/testBKJMFailingBKCluster2"
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p1
argument_list|)
expr_stmt|;
name|newBookie
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// will take down shared storage
name|assertEquals
argument_list|(
literal|"New bookie didn't stop"
argument_list|,
name|numBookies
argument_list|,
name|bkutil
operator|.
name|checkBookiesUp
argument_list|(
name|numBookies
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// mkdirs will "succeed", but nn have called runtime.exit
name|fs
operator|.
name|mkdirs
argument_list|(
name|p2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockRuntime1
argument_list|,
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|exit
argument_list|(
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockRuntime2
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
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't have been able to transition with bookies down"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Wrong exception"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Failed to start active services"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|verify
argument_list|(
name|mockRuntime2
argument_list|,
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|exit
argument_list|(
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
name|replacementBookie
operator|=
name|bkutil
operator|.
name|newBookie
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Replacement bookie didn't start"
argument_list|,
name|ensembleSize
argument_list|,
name|bkutil
operator|.
name|checkBookiesUp
argument_list|(
name|ensembleSize
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// should work fine now
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|newBookie
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|replacementBookie
operator|!=
literal|null
condition|)
block|{
name|replacementBookie
operator|.
name|shutdown
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
block|}
comment|/**    * Test that two namenodes can't continue as primary    */
annotation|@
name|Test
DECL|method|testMultiplePrimariesStarted ()
specifier|public
name|void
name|testMultiplePrimariesStarted
parameter_list|()
throws|throws
name|Exception
block|{
name|Runtime
name|mockRuntime1
init|=
name|mock
argument_list|(
name|Runtime
operator|.
name|class
argument_list|)
decl_stmt|;
name|Runtime
name|mockRuntime2
init|=
name|mock
argument_list|(
name|Runtime
operator|.
name|class
argument_list|)
decl_stmt|;
name|Path
name|p1
init|=
operator|new
name|Path
argument_list|(
literal|"/testBKJMMultiplePrimary"
argument_list|)
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
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
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
argument_list|,
name|BKJMUtil
operator|.
name|createJournalURI
argument_list|(
literal|"/hotfailoverMultiple"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|BKJMUtil
operator|.
name|addJournalManagerDefinition
argument_list|(
name|conf
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
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|manageNameDfsSharedDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|NameNode
name|nn1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NameNode
name|nn2
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|FSEditLogTestUtil
operator|.
name|setRuntimeForEditLog
argument_list|(
name|nn1
argument_list|,
name|mockRuntime1
argument_list|)
expr_stmt|;
name|FSEditLogTestUtil
operator|.
name|setRuntimeForEditLog
argument_list|(
name|nn2
argument_list|,
name|mockRuntime2
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p1
argument_list|)
expr_stmt|;
name|nn1
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// get the older active server.
comment|// This edit log updation on older active should make older active
comment|// shutdown.
name|fs
operator|.
name|delete
argument_list|(
name|p1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockRuntime1
argument_list|,
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|exit
argument_list|(
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockRuntime2
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
block|}
end_class

end_unit

