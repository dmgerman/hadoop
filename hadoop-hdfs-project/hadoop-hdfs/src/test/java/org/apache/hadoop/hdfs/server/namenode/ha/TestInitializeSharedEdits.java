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
name|URISyntaxException
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|RequestSource
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
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
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
name|DFSUtil
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

begin_class
DECL|class|TestInitializeSharedEdits
specifier|public
class|class
name|TestInitializeSharedEdits
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
name|TestInitializeSharedEdits
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
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
name|DFS_HA_LOGROLL_PERIOD_KEY
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
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
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
name|shutdownClusterAndRemoveSharedEditsDir
argument_list|()
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
DECL|method|shutdownClusterAndRemoveSharedEditsDir ()
specifier|private
name|void
name|shutdownClusterAndRemoveSharedEditsDir
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|File
name|sharedEditsDir
init|=
operator|new
name|File
argument_list|(
name|cluster
operator|.
name|getSharedEditsDir
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|sharedEditsDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCannotStartNameNodes ()
specifier|private
name|void
name|assertCannotStartNameNodes
parameter_list|()
block|{
comment|// Make sure we can't currently start either NN.
try|try
block|{
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have been able to start NN1 without shared dir"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot start an HA namenode with name dirs that need recovery"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have been able to start NN2 without shared dir"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot start an HA namenode with name dirs that need recovery"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertCanStartHaNameNodes (String pathSuffix)
specifier|private
name|void
name|assertCanStartHaNameNodes
parameter_list|(
name|String
name|pathSuffix
parameter_list|)
throws|throws
name|ServiceFailedException
throws|,
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
comment|// Now should be able to start both NNs. Pass "false" here so that we don't
comment|// try to waitActive on all NNs, since the second NN doesn't exist yet.
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Make sure HA is working.
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getRpcServer
argument_list|()
operator|.
name|transitionToActive
argument_list|(
operator|new
name|StateChangeRequestInfo
argument_list|(
name|RequestSource
operator|.
name|REQUEST_BY_USER
argument_list|)
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Path
name|newPath
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|,
name|pathSuffix
argument_list|)
decl_stmt|;
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
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|newPath
argument_list|)
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
argument_list|,
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
argument_list|,
name|newPath
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|isDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
block|}
annotation|@
name|Test
DECL|method|testInitializeSharedEdits ()
specifier|public
name|void
name|testInitializeSharedEdits
parameter_list|()
throws|throws
name|Exception
block|{
name|assertCannotStartNameNodes
argument_list|()
expr_stmt|;
comment|// Initialize the shared edits dir.
name|assertFalse
argument_list|(
name|NameNode
operator|.
name|initializeSharedEdits
argument_list|(
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertCanStartHaNameNodes
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
comment|// Now that we've done a metadata operation, make sure that deleting and
comment|// re-initializing the shared edits dir will let the standby still start.
name|shutdownClusterAndRemoveSharedEditsDir
argument_list|()
expr_stmt|;
name|assertCannotStartNameNodes
argument_list|()
expr_stmt|;
comment|// Re-initialize the shared edits dir.
name|assertFalse
argument_list|(
name|NameNode
operator|.
name|initializeSharedEdits
argument_list|(
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should *still* be able to start both NNs
name|assertCanStartHaNameNodes
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDontOverWriteExistingDir ()
specifier|public
name|void
name|testDontOverWriteExistingDir
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|NameNode
operator|.
name|initializeSharedEdits
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|NameNode
operator|.
name|initializeSharedEdits
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitializeSharedEditsConfiguresGenericConfKeys ()
specifier|public
name|void
name|testInitializeSharedEditsConfiguresGenericConfKeys
parameter_list|()
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
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|,
literal|"ns1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODES_KEY_PREFIX
argument_list|,
literal|"ns1"
argument_list|)
argument_list|,
literal|"nn1,nn2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
literal|"ns1"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|,
literal|"localhost:1234"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|initializeSharedEdits
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

