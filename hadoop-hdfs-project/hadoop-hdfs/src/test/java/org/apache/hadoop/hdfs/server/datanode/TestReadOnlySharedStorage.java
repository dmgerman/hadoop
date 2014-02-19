begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|*
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

begin_import
import|import static
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
name|protocol
operator|.
name|DatanodeStorage
operator|.
name|State
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|DFSClient
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
name|protocol
operator|.
name|Block
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
name|ExtendedBlock
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
name|LocatedBlock
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
name|blockmanagement
operator|.
name|DatanodeManager
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
name|NumberReplicas
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
name|protocol
operator|.
name|DatanodeStorage
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
name|protocol
operator|.
name|StorageReport
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
name|Iterables
import|;
end_import

begin_comment
comment|/**  * Test proper {@link BlockManager} replication counting for {@link DatanodeStorage}s  * with {@link DatanodeStorage.State#READ_ONLY_SHARED READ_ONLY} state.  *   * Uses {@link SimulatedFSDataset} to inject read-only replicas into a DataNode.  */
end_comment

begin_class
DECL|class|TestReadOnlySharedStorage
specifier|public
class|class
name|TestReadOnlySharedStorage
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestReadOnlySharedStorage
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_DATANODES
specifier|private
specifier|static
name|short
name|NUM_DATANODES
init|=
literal|3
decl_stmt|;
DECL|field|RO_NODE_INDEX
specifier|private
specifier|static
name|int
name|RO_NODE_INDEX
init|=
literal|0
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|seed
specifier|private
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0x1BADF00DL
decl_stmt|;
DECL|field|PATH
specifier|private
specifier|static
specifier|final
name|Path
name|PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|TestReadOnlySharedStorage
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
DECL|field|RETRIES
specifier|private
specifier|static
specifier|final
name|int
name|RETRIES
init|=
literal|10
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
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|client
specifier|private
name|DFSClient
name|client
decl_stmt|;
DECL|field|blockManager
specifier|private
name|BlockManager
name|blockManager
decl_stmt|;
DECL|field|datanodeManager
specifier|private
name|DatanodeManager
name|datanodeManager
decl_stmt|;
DECL|field|normalDataNode
specifier|private
name|DatanodeInfo
name|normalDataNode
decl_stmt|;
DECL|field|readOnlyDataNode
specifier|private
name|DatanodeInfo
name|readOnlyDataNode
decl_stmt|;
DECL|field|block
specifier|private
name|Block
name|block
decl_stmt|;
DECL|field|extendedBlock
specifier|private
name|ExtendedBlock
name|extendedBlock
decl_stmt|;
comment|/**    * Setup a {@link MiniDFSCluster}.    * Create a block with both {@link State#NORMAL} and {@link State#READ_ONLY_SHARED} replicas.    */
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Configuration
index|[]
name|overlays
init|=
operator|new
name|Configuration
index|[
name|NUM_DATANODES
index|]
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
name|overlays
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|overlays
index|[
name|i
index|]
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|RO_NODE_INDEX
condition|)
block|{
name|overlays
index|[
name|i
index|]
operator|.
name|setEnum
argument_list|(
name|SimulatedFSDataset
operator|.
name|CONFIG_PROPERTY_STATE
argument_list|,
name|i
operator|==
name|RO_NODE_INDEX
condition|?
name|READ_ONLY_SHARED
else|:
name|NORMAL
argument_list|)
expr_stmt|;
block|}
block|}
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
name|NUM_DATANODES
argument_list|)
operator|.
name|dataNodeConfOverlays
argument_list|(
name|overlays
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|blockManager
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
expr_stmt|;
name|datanodeManager
operator|=
name|blockManager
operator|.
name|getDatanodeManager
argument_list|()
expr_stmt|;
name|client
operator|=
operator|new
name|DFSClient
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
argument_list|,
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
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
name|NUM_DATANODES
condition|;
name|i
operator|++
control|)
block|{
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|validateStorageState
argument_list|(
name|BlockManagerTestUtil
operator|.
name|getStorageReportsForDatanode
argument_list|(
name|datanodeManager
operator|.
name|getDatanode
argument_list|(
name|dataNode
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|i
operator|==
name|RO_NODE_INDEX
condition|?
name|READ_ONLY_SHARED
else|:
name|NORMAL
argument_list|)
expr_stmt|;
block|}
comment|// Create a 1 block file
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|PATH
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|LocatedBlock
name|locatedBlock
init|=
name|getLocatedBlock
argument_list|()
decl_stmt|;
name|extendedBlock
operator|=
name|locatedBlock
operator|.
name|getBlock
argument_list|()
expr_stmt|;
name|block
operator|=
name|extendedBlock
operator|.
name|getLocalBlock
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|locatedBlock
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|normalDataNode
operator|=
name|locatedBlock
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
name|readOnlyDataNode
operator|=
name|datanodeManager
operator|.
name|getDatanode
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|RO_NODE_INDEX
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|normalDataNode
argument_list|,
name|is
argument_list|(
name|not
argument_list|(
name|readOnlyDataNode
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|validateNumberReplicas
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Inject the block into the datanode with READ_ONLY_SHARED storage
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|RO_NODE_INDEX
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|block
argument_list|)
argument_list|)
expr_stmt|;
comment|// There should now be 2 *locations* for the block
comment|// Must wait until the NameNode has processed the block report for the injected blocks
name|waitForLocations
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|delete
argument_list|(
name|PATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
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
block|}
DECL|method|waitForLocations (int locations)
specifier|private
name|void
name|waitForLocations
parameter_list|(
name|int
name|locations
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
for|for
control|(
name|int
name|tries
init|=
literal|0
init|;
name|tries
operator|<
name|RETRIES
condition|;
control|)
try|try
block|{
name|LocatedBlock
name|locatedBlock
init|=
name|getLocatedBlock
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|locatedBlock
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
name|locations
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
if|if
condition|(
operator|++
name|tries
operator|<
name|RETRIES
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
DECL|method|getLocatedBlock ()
specifier|private
name|LocatedBlock
name|getLocatedBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|LocatedBlocks
name|locatedBlocks
init|=
name|client
operator|.
name|getLocatedBlocks
argument_list|(
name|PATH
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|locatedBlocks
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|locatedBlocks
operator|.
name|getLocatedBlocks
argument_list|()
argument_list|)
return|;
block|}
DECL|method|validateStorageState (StorageReport[] storageReports, DatanodeStorage.State state)
specifier|private
name|void
name|validateStorageState
parameter_list|(
name|StorageReport
index|[]
name|storageReports
parameter_list|,
name|DatanodeStorage
operator|.
name|State
name|state
parameter_list|)
block|{
for|for
control|(
name|StorageReport
name|storageReport
range|:
name|storageReports
control|)
block|{
name|DatanodeStorage
name|storage
init|=
name|storageReport
operator|.
name|getStorage
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|storage
operator|.
name|getState
argument_list|()
argument_list|,
name|is
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validateNumberReplicas (int expectedReplicas)
specifier|private
name|void
name|validateNumberReplicas
parameter_list|(
name|int
name|expectedReplicas
parameter_list|)
throws|throws
name|IOException
block|{
name|NumberReplicas
name|numberReplicas
init|=
name|blockManager
operator|.
name|countNodes
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|numberReplicas
operator|.
name|liveReplicas
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedReplicas
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|numberReplicas
operator|.
name|excessReplicas
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|numberReplicas
operator|.
name|corruptReplicas
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|numberReplicas
operator|.
name|decommissionedReplicas
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|numberReplicas
operator|.
name|replicasOnStaleNodes
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|blockManager
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blockManager
operator|.
name|getUnderReplicatedBlocksCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blockManager
operator|.
name|getExcessBlocksCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that<tt>READ_ONLY_SHARED</tt> replicas are<i>not</i> counted towards the overall     * replication count, but<i>are</i> included as replica locations returned to clients for reads.    */
annotation|@
name|Test
DECL|method|testReplicaCounting ()
specifier|public
name|void
name|testReplicaCounting
parameter_list|()
throws|throws
name|Exception
block|{
comment|// There should only be 1 *replica* (the READ_ONLY_SHARED doesn't count)
name|validateNumberReplicas
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setReplication
argument_list|(
name|PATH
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
comment|// There should now be 3 *locations* for the block, and 2 *replicas*
name|waitForLocations
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|validateNumberReplicas
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the NameNode is able to still use<tt>READ_ONLY_SHARED</tt> replicas even     * when the single NORMAL replica is offline (and the effective replication count is 0).    */
annotation|@
name|Test
DECL|method|testNormalReplicaOffline ()
specifier|public
name|void
name|testNormalReplicaOffline
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Stop the datanode hosting the NORMAL replica
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|normalDataNode
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// Force NameNode to detect that the datanode is down
name|BlockManagerTestUtil
operator|.
name|noticeDeadDatanode
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
name|normalDataNode
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// The live replica count should now be zero (since the NORMAL replica is offline)
name|NumberReplicas
name|numberReplicas
init|=
name|blockManager
operator|.
name|countNodes
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|numberReplicas
operator|.
name|liveReplicas
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// The block should be reported as under-replicated
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|blockManager
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blockManager
operator|.
name|getUnderReplicatedBlocksCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
comment|// The BlockManager should be able to heal the replication count back to 1
comment|// by triggering an inter-datanode replication from one of the READ_ONLY_SHARED replicas
name|BlockManagerTestUtil
operator|.
name|computeAllPendingWork
argument_list|(
name|blockManager
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForReplication
argument_list|(
name|cluster
argument_list|,
name|extendedBlock
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// There should now be 2 *locations* for the block, and 1 *replica*
name|assertThat
argument_list|(
name|getLocatedBlock
argument_list|()
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|validateNumberReplicas
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that corrupt<tt>READ_ONLY_SHARED</tt> replicas aren't counted     * towards the corrupt replicas total.    */
annotation|@
name|Test
DECL|method|testReadOnlyReplicaCorrupt ()
specifier|public
name|void
name|testReadOnlyReplicaCorrupt
parameter_list|()
throws|throws
name|Exception
block|{
comment|// "Corrupt" a READ_ONLY_SHARED replica by reporting it as a bad replica
name|client
operator|.
name|reportBadBlocks
argument_list|(
operator|new
name|LocatedBlock
index|[]
block|{
operator|new
name|LocatedBlock
argument_list|(
name|extendedBlock
argument_list|,
operator|new
name|DatanodeInfo
index|[]
block|{
name|readOnlyDataNode
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
comment|// There should now be only 1 *location* for the block as the READ_ONLY_SHARED is corrupt
name|waitForLocations
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// However, the corrupt READ_ONLY_SHARED replica should *not* affect the overall corrupt replicas count
name|NumberReplicas
name|numberReplicas
init|=
name|blockManager
operator|.
name|countNodes
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|numberReplicas
operator|.
name|corruptReplicas
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

