begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|PlatformAssumptions
operator|.
name|assumeNotWindows
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
name|assertEquals
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|FSDataOutputStream
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
name|server
operator|.
name|datanode
operator|.
name|FsDatasetTestUtils
operator|.
name|MaterializedReplica
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
name|ha
operator|.
name|TestDNFencing
operator|.
name|RandomDeleterPolicy
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
name|IOUtils
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

begin_comment
comment|/**  * Test when RBW block is removed. Invalidation of the corrupted block happens  * and then the under replicated block gets replicated to the datanode.  */
end_comment

begin_class
DECL|class|TestRBWBlockInvalidation
specifier|public
class|class
name|TestRBWBlockInvalidation
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestRBWBlockInvalidation
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|countReplicas (final FSNamesystem namesystem, ExtendedBlock block)
specifier|private
specifier|static
name|NumberReplicas
name|countReplicas
parameter_list|(
specifier|final
name|FSNamesystem
name|namesystem
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|)
block|{
specifier|final
name|BlockManager
name|blockManager
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
return|return
name|blockManager
operator|.
name|countNodes
argument_list|(
name|blockManager
operator|.
name|getStoredBlock
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Test when a block's replica is removed from RBW folder in one of the    * datanode, namenode should ask to invalidate that corrupted block and    * schedule replication for one more replica for that under replicated block.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|600000
argument_list|)
DECL|method|testBlockInvalidationWhenRBWReplicaMissedInDN ()
specifier|public
name|void
name|testBlockInvalidationWhenRBWReplicaMissedInDN
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// This test cannot pass on Windows due to file locking enforcement.  It will
comment|// reject the attempt to delete the block file from the RBW folder.
name|assumeNotWindows
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|300
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DIRECTORYSCAN_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
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
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/TestRBWBlockInvalidation"
argument_list|,
literal|"foo1"
argument_list|)
decl_stmt|;
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|testPath
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"HDFS-3157: "
operator|+
name|testPath
argument_list|)
expr_stmt|;
name|out
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|blk
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|)
decl_stmt|;
comment|// Delete partial block and its meta information from the RBW folder
comment|// of first datanode.
name|MaterializedReplica
name|replica
init|=
name|cluster
operator|.
name|getMaterializedReplica
argument_list|(
literal|0
argument_list|,
name|blk
argument_list|)
decl_stmt|;
name|replica
operator|.
name|deleteData
argument_list|()
expr_stmt|;
name|replica
operator|.
name|deleteMeta
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|liveReplicas
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|(
name|liveReplicas
operator|=
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|blk
argument_list|)
operator|.
name|liveReplicas
argument_list|()
operator|)
operator|<
literal|2
condition|)
block|{
comment|// This confirms we have a corrupt replica
name|LOG
operator|.
name|info
argument_list|(
literal|"Live Replicas after corruption: "
operator|+
name|liveReplicas
argument_list|)
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"There should be less than 2 replicas in the "
operator|+
literal|"liveReplicasMap"
argument_list|,
literal|1
argument_list|,
name|liveReplicas
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|(
name|liveReplicas
operator|=
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|blk
argument_list|)
operator|.
name|liveReplicas
argument_list|()
operator|)
operator|>
literal|1
condition|)
block|{
comment|//Wait till the live replica count becomes equal to Replication Factor
name|LOG
operator|.
name|info
argument_list|(
literal|"Live Replicas after Rereplication: "
operator|+
name|liveReplicas
argument_list|)
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"There should be two live replicas"
argument_list|,
literal|2
argument_list|,
name|liveReplicas
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|blk
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Corrupt Replicas becomes 0"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Regression test for HDFS-4799, a case where, upon restart, if there    * were RWR replicas with out-of-date genstamps, the NN could accidentally    * delete good replicas instead of the bad replicas.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testRWRInvalidation ()
specifier|public
name|void
name|testRWRInvalidation
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Set the deletion policy to be randomized rather than the default.
comment|// The default is based on disk space, which isn't controllable
comment|// in the context of the test, whereas a random one is more accurate
comment|// to what is seen in real clusters (nodes have random amounts of free
comment|// space)
name|conf
operator|.
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|RandomDeleterPolicy
operator|.
name|class
argument_list|,
name|BlockPlacementPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Speed up the test a bit with faster heartbeats.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|int
name|numFiles
init|=
literal|10
decl_stmt|;
comment|// Test with a bunch of separate files, since otherwise the test may
comment|// fail just due to "good luck", even if a bug is present.
name|List
argument_list|<
name|Path
argument_list|>
name|testPaths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
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
name|numFiles
condition|;
name|i
operator|++
control|)
block|{
name|testPaths
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|FSDataOutputStream
argument_list|>
name|streams
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Open the test files and write some data to each
for|for
control|(
name|Path
name|path
range|:
name|testPaths
control|)
block|{
name|FSDataOutputStream
name|out
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|create
argument_list|(
name|path
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"old gs data\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Path
name|path
range|:
name|testPaths
control|)
block|{
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
comment|// Shutdown one of the nodes in the pipeline
name|DataNodeProperties
name|oldGenstampNode
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Write some more data and flush again. This data will only
comment|// be in the latter genstamp copy of the blocks.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|streams
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|path
init|=
name|testPaths
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|streams
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"new gs data\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// Set replication so that only one node is necessary for this block,
comment|// and close it.
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|setReplication
argument_list|(
name|path
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Path
name|path
range|:
name|testPaths
control|)
block|{
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Upon restart, there will be two replicas, one with an old genstamp
comment|// and one current copy. This test wants to ensure that the old genstamp
comment|// copy is the one that is deleted.
name|LOG
operator|.
name|info
argument_list|(
literal|"=========================== restarting cluster"
argument_list|)
expr_stmt|;
name|DataNodeProperties
name|otherNode
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
comment|// Restart the datanode with the corrupt replica first.
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|oldGenstampNode
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Then the other node
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|otherNode
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Compute and send invalidations, waiting until they're fully processed.
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
operator|.
name|computeInvalidateWork
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
name|HATestUtil
operator|.
name|waitForDNDeletions
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerDeletionReports
argument_list|()
expr_stmt|;
name|waitForNumTotalBlocks
argument_list|(
name|cluster
argument_list|,
name|numFiles
argument_list|)
expr_stmt|;
comment|// Make sure we can still read the blocks.
for|for
control|(
name|Path
name|path
range|:
name|testPaths
control|)
block|{
name|String
name|ret
init|=
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"old gs data\n"
operator|+
literal|"new gs data\n"
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|streams
operator|.
name|toArray
argument_list|(
operator|new
name|Closeable
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
DECL|method|waitForNumTotalBlocks (final MiniDFSCluster cluster, final int numTotalBlocks)
specifier|private
name|void
name|waitForNumTotalBlocks
parameter_list|(
specifier|final
name|MiniDFSCluster
name|cluster
parameter_list|,
specifier|final
name|int
name|numTotalBlocks
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
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// Wait total blocks
if|if
condition|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlocksTotal
argument_list|()
operator|==
name|numTotalBlocks
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{
comment|// Ignore the exception
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

