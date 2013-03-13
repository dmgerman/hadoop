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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|MiniDFSCluster
operator|.
name|DataNodeProperties
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
name|protocol
operator|.
name|DatanodeInfo
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
name|LocatedBlocks
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
name|blockmanagement
operator|.
name|BlockManager
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
name|blockmanagement
operator|.
name|BlockManagerTestUtil
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
name|log4j
operator|.
name|Level
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

begin_comment
comment|/**  * The hotornot.com of unit tests: makes sure that the standby not only  * has namespace information, but also has the correct block reports, etc.  */
end_comment

begin_class
DECL|class|TestStandbyIsHot
specifier|public
class|class
name|TestStandbyIsHot
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestStandbyIsHot
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_FILE_DATA
specifier|private
specifier|static
specifier|final
name|String
name|TEST_FILE_DATA
init|=
literal|"hello highly available world"
decl_stmt|;
DECL|field|TEST_FILE
specifier|private
specifier|static
specifier|final
name|String
name|TEST_FILE
init|=
literal|"/testStandbyIsHot"
decl_stmt|;
DECL|field|TEST_FILE_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_FILE_PATH
init|=
operator|new
name|Path
argument_list|(
name|TEST_FILE
argument_list|)
decl_stmt|;
static|static
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BlockManager
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|NameNode
operator|.
name|stateChangeLog
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testStandbyIsHot ()
specifier|public
name|void
name|testStandbyIsHot
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
comment|// We read from the standby to watch block locations
name|HAUtil
operator|.
name|setAllowStandbyReads
argument_list|(
name|conf
argument_list|,
literal|true
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"=================================="
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|TEST_FILE_PATH
argument_list|,
name|TEST_FILE_DATA
argument_list|)
expr_stmt|;
comment|// Have to force an edit log roll so that the standby catches up
name|nn1
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"=================================="
argument_list|)
expr_stmt|;
comment|// Block locations should show up on standby.
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for block locations to appear on standby node"
argument_list|)
expr_stmt|;
name|waitForBlockLocations
argument_list|(
name|cluster
argument_list|,
name|nn2
argument_list|,
name|TEST_FILE
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// Trigger immediate heartbeats and block reports so
comment|// that the active "trusts" all of the DNs
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// Change replication
name|LOG
operator|.
name|info
argument_list|(
literal|"Changing replication to 1"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setReplication
argument_list|(
name|TEST_FILE_PATH
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|computeAllPendingWork
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
name|waitForBlockLocations
argument_list|(
name|cluster
argument_list|,
name|nn1
argument_list|,
name|TEST_FILE
argument_list|,
literal|1
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for lowered replication to show up on standby"
argument_list|)
expr_stmt|;
name|waitForBlockLocations
argument_list|(
name|cluster
argument_list|,
name|nn2
argument_list|,
name|TEST_FILE
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Change back to 3
name|LOG
operator|.
name|info
argument_list|(
literal|"Changing replication to 3"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setReplication
argument_list|(
name|TEST_FILE_PATH
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|computeAllPendingWork
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for higher replication to show up on standby"
argument_list|)
expr_stmt|;
name|waitForBlockLocations
argument_list|(
name|cluster
argument_list|,
name|nn2
argument_list|,
name|TEST_FILE
argument_list|,
literal|3
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
comment|/**    * Regression test for HDFS-2795:    *  - Start an HA cluster with a DN.    *  - Write several blocks to the FS with replication 1.    *  - Shutdown the DN    *  - Wait for the NNs to declare the DN dead. All blocks will be under-replicated.    *  - Restart the DN.    * In the bug, the standby node would only very slowly notice the blocks returning    * to the cluster.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testDatanodeRestarts ()
specifier|public
name|void
name|testDatanodeRestarts
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
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// We read from the standby to watch block locations
name|HAUtil
operator|.
name|setAllowStandbyReads
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACCESSTIME_PRECISION_KEY
argument_list|,
literal|0
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|NameNode
name|nn0
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NameNode
name|nn1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Create 5 blocks.
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
argument_list|,
name|TEST_FILE_PATH
argument_list|,
literal|5
operator|*
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
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
comment|// Stop the DN.
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
name|String
name|dnName
init|=
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getXferAddr
argument_list|()
decl_stmt|;
name|DataNodeProperties
name|dnProps
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Make sure both NNs register it as dead.
name|BlockManagerTestUtil
operator|.
name|noticeDeadDatanode
argument_list|(
name|nn0
argument_list|,
name|dnName
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|noticeDeadDatanode
argument_list|(
name|nn1
argument_list|,
name|dnName
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|nn0
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|nn0
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getUnderReplicatedBlocks
argument_list|()
argument_list|)
expr_stmt|;
comment|// The SBN will not have any blocks in its neededReplication queue
comment|// since the SBN doesn't process replication.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getUnderReplicatedBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|locs
init|=
name|nn1
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|TEST_FILE
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Standby should have registered that the block has no replicas"
argument_list|,
literal|0
argument_list|,
name|locs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnProps
argument_list|)
expr_stmt|;
comment|// Wait for both NNs to re-register the DN.
name|cluster
operator|.
name|waitActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|nn0
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nn0
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getUnderReplicatedBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getUnderReplicatedBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|locs
operator|=
name|nn1
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|TEST_FILE
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Standby should have registered that the block has replicas again"
argument_list|,
literal|1
argument_list|,
name|locs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
operator|.
name|length
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
DECL|method|waitForBlockLocations (final MiniDFSCluster cluster, final NameNode nn, final String path, final int expectedReplicas)
specifier|static
name|void
name|waitForBlockLocations
parameter_list|(
specifier|final
name|MiniDFSCluster
name|cluster
parameter_list|,
specifier|final
name|NameNode
name|nn
parameter_list|,
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|int
name|expectedReplicas
parameter_list|)
throws|throws
name|Exception
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
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
try|try
block|{
name|LocatedBlocks
name|locs
init|=
name|NameNodeAdapter
operator|.
name|getBlockLocations
argument_list|(
name|nn
argument_list|,
name|path
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|dnis
init|=
name|locs
operator|.
name|getLastLocatedBlock
argument_list|()
operator|.
name|getLocations
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|dni
range|:
name|dnis
control|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|dni
argument_list|)
expr_stmt|;
block|}
name|int
name|numReplicas
init|=
name|dnis
operator|.
name|length
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got "
operator|+
name|numReplicas
operator|+
literal|" locs: "
operator|+
name|locs
argument_list|)
expr_stmt|;
if|if
condition|(
name|numReplicas
operator|>
name|expectedReplicas
condition|)
block|{
name|cluster
operator|.
name|triggerDeletionReports
argument_list|()
expr_stmt|;
block|}
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
return|return
name|numReplicas
operator|==
name|expectedReplicas
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No block locations yet: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|500
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

