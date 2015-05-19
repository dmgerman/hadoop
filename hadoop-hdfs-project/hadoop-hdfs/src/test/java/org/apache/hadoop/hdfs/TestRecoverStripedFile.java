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
name|DatanodeID
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
name|HdfsConstants
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
name|protocol
operator|.
name|LocatedStripedBlock
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
name|DatanodeDescriptor
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
name|hdfs
operator|.
name|util
operator|.
name|StripedBlockUtil
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
name|Assert
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|FileInputStream
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
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|TestRecoverStripedFile
specifier|public
class|class
name|TestRecoverStripedFile
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
name|TestRecoverStripedFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dataBlkNum
specifier|private
specifier|static
specifier|final
name|int
name|dataBlkNum
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|parityBlkNum
specifier|private
specifier|static
specifier|final
name|int
name|parityBlkNum
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|static
specifier|final
name|int
name|cellSize
init|=
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|static
specifier|final
name|int
name|blockSize
init|=
name|cellSize
operator|*
literal|3
decl_stmt|;
DECL|field|groupSize
specifier|private
specifier|static
specifier|final
name|int
name|groupSize
init|=
name|dataBlkNum
operator|+
name|parityBlkNum
decl_stmt|;
DECL|field|dnNum
specifier|private
specifier|static
specifier|final
name|int
name|dnNum
init|=
name|groupSize
operator|+
name|parityBlkNum
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
comment|// Map: DatanodeID -> datanode index in cluster
DECL|field|dnMap
specifier|private
name|Map
argument_list|<
name|DatanodeID
argument_list|,
name|Integer
argument_list|>
name|dnMap
init|=
operator|new
name|HashMap
argument_list|<
name|DatanodeID
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
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
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_STRIPED_READ_BUFFER_SIZE_KEY
argument_list|,
name|cellSize
operator|-
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
argument_list|,
literal|1
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
name|dnNum
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
empty_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
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
name|dnNum
condition|;
name|i
operator|++
control|)
block|{
name|dnMap
operator|.
name|put
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testRecoverOneParityBlock ()
specifier|public
name|void
name|testRecoverOneParityBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLen
init|=
literal|10
operator|*
name|blockSize
operator|+
name|blockSize
operator|/
literal|10
decl_stmt|;
name|assertFileBlocksRecovery
argument_list|(
literal|"/testRecoverOneParityBlock"
argument_list|,
name|fileLen
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testRecoverThreeParityBlocks ()
specifier|public
name|void
name|testRecoverThreeParityBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLen
init|=
literal|3
operator|*
name|blockSize
operator|+
name|blockSize
operator|/
literal|10
decl_stmt|;
name|assertFileBlocksRecovery
argument_list|(
literal|"/testRecoverThreeParityBlocks"
argument_list|,
name|fileLen
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testRecoverThreeDataBlocks ()
specifier|public
name|void
name|testRecoverThreeDataBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLen
init|=
literal|3
operator|*
name|blockSize
operator|+
name|blockSize
operator|/
literal|10
decl_stmt|;
name|assertFileBlocksRecovery
argument_list|(
literal|"/testRecoverThreeDataBlocks"
argument_list|,
name|fileLen
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testRecoverOneDataBlock ()
specifier|public
name|void
name|testRecoverOneDataBlock
parameter_list|()
throws|throws
name|Exception
block|{
comment|////TODO: TODO: wait for HADOOP-11847
comment|//int fileLen = 10 * blockSize + blockSize/10;
comment|//assertFileBlocksRecovery("/testRecoverOneDataBlock", fileLen, 1, 1);
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testRecoverAnyBlocks ()
specifier|public
name|void
name|testRecoverAnyBlocks
parameter_list|()
throws|throws
name|Exception
block|{
comment|////TODO: TODO: wait for HADOOP-11847
comment|//int fileLen = 3 * blockSize + blockSize/10;
comment|//assertFileBlocksRecovery("/testRecoverAnyBlocks", fileLen, 2, 2);
block|}
comment|/**    * Test the file blocks recovery.    * 1. Check the replica is recovered in the target datanode,     *    and verify the block replica length, generationStamp and content.    * 2. Read the file and verify content.     */
DECL|method|assertFileBlocksRecovery (String fileName, int fileLen, int recovery, int toRecoverBlockNum)
specifier|private
name|void
name|assertFileBlocksRecovery
parameter_list|(
name|String
name|fileName
parameter_list|,
name|int
name|fileLen
parameter_list|,
name|int
name|recovery
parameter_list|,
name|int
name|toRecoverBlockNum
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|recovery
operator|!=
literal|0
operator|&&
name|recovery
operator|!=
literal|1
operator|&&
name|recovery
operator|!=
literal|2
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Invalid recovery: 0 is to recovery parity blocks,"
operator|+
literal|"1 is to recovery data blocks, 2 is any."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|toRecoverBlockNum
argument_list|<
literal|1
operator|||
name|toRecoverBlockNum
argument_list|>
name|parityBlkNum
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"toRecoverBlockNum should be between 1 ~ "
operator|+
name|parityBlkNum
argument_list|)
expr_stmt|;
block|}
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|testCreateStripedFile
argument_list|(
name|file
argument_list|,
name|fileLen
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|locatedBlocks
init|=
name|getLocatedBlocks
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|locatedBlocks
operator|.
name|getFileLength
argument_list|()
argument_list|,
name|fileLen
argument_list|)
expr_stmt|;
name|LocatedStripedBlock
name|lastBlock
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|locatedBlocks
operator|.
name|getLastLocatedBlock
argument_list|()
decl_stmt|;
name|DatanodeInfo
index|[]
name|storageInfos
init|=
name|lastBlock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|int
index|[]
name|indices
init|=
name|lastBlock
operator|.
name|getBlockIndices
argument_list|()
decl_stmt|;
name|BitSet
name|bitset
init|=
operator|new
name|BitSet
argument_list|(
name|dnNum
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|storageInfo
range|:
name|storageInfos
control|)
block|{
name|bitset
operator|.
name|set
argument_list|(
name|dnMap
operator|.
name|get
argument_list|(
name|storageInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
index|[]
name|toDead
init|=
operator|new
name|int
index|[
name|toRecoverBlockNum
index|]
decl_stmt|;
name|int
name|n
init|=
literal|0
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
name|indices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|n
operator|<
name|toRecoverBlockNum
condition|)
block|{
if|if
condition|(
name|recovery
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|indices
index|[
name|i
index|]
operator|>=
name|dataBlkNum
condition|)
block|{
name|toDead
index|[
name|n
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|recovery
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|indices
index|[
name|i
index|]
operator|<
name|dataBlkNum
condition|)
block|{
name|toDead
index|[
name|n
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
else|else
block|{
name|toDead
index|[
name|n
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
name|DatanodeInfo
index|[]
name|dataDNs
init|=
operator|new
name|DatanodeInfo
index|[
name|toRecoverBlockNum
index|]
decl_stmt|;
name|int
index|[]
name|deadDnIndices
init|=
operator|new
name|int
index|[
name|toRecoverBlockNum
index|]
decl_stmt|;
name|ExtendedBlock
index|[]
name|blocks
init|=
operator|new
name|ExtendedBlock
index|[
name|toRecoverBlockNum
index|]
decl_stmt|;
name|File
index|[]
name|replicas
init|=
operator|new
name|File
index|[
name|toRecoverBlockNum
index|]
decl_stmt|;
name|File
index|[]
name|metadatas
init|=
operator|new
name|File
index|[
name|toRecoverBlockNum
index|]
decl_stmt|;
name|byte
index|[]
index|[]
name|replicaContents
init|=
operator|new
name|byte
index|[
name|toRecoverBlockNum
index|]
index|[]
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
name|toRecoverBlockNum
condition|;
name|i
operator|++
control|)
block|{
name|dataDNs
index|[
name|i
index|]
operator|=
name|storageInfos
index|[
name|toDead
index|[
name|i
index|]
index|]
expr_stmt|;
name|deadDnIndices
index|[
name|i
index|]
operator|=
name|dnMap
operator|.
name|get
argument_list|(
name|dataDNs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// Check the block replica file on deadDn before it dead.
name|blocks
index|[
name|i
index|]
operator|=
name|StripedBlockUtil
operator|.
name|constructInternalBlock
argument_list|(
name|lastBlock
operator|.
name|getBlock
argument_list|()
argument_list|,
name|cellSize
argument_list|,
name|dataBlkNum
argument_list|,
name|indices
index|[
name|toDead
index|[
name|i
index|]
index|]
argument_list|)
expr_stmt|;
name|replicas
index|[
name|i
index|]
operator|=
name|cluster
operator|.
name|getBlockFile
argument_list|(
name|deadDnIndices
index|[
name|i
index|]
argument_list|,
name|blocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|metadatas
index|[
name|i
index|]
operator|=
name|cluster
operator|.
name|getBlockMetadataFile
argument_list|(
name|deadDnIndices
index|[
name|i
index|]
argument_list|,
name|blocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// the block replica on the datanode should be the same as expected
name|assertEquals
argument_list|(
name|replicas
index|[
name|i
index|]
operator|.
name|length
argument_list|()
argument_list|,
name|StripedBlockUtil
operator|.
name|getInternalBlockLength
argument_list|(
name|lastBlock
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|cellSize
argument_list|,
name|dataBlkNum
argument_list|,
name|indices
index|[
name|toDead
index|[
name|i
index|]
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadatas
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|blocks
index|[
name|i
index|]
operator|.
name|getGenerationStamp
argument_list|()
operator|+
literal|".meta"
argument_list|)
argument_list|)
expr_stmt|;
name|replicaContents
index|[
name|i
index|]
operator|=
name|readReplica
argument_list|(
name|replicas
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|DatanodeID
index|[]
name|dnIDs
init|=
operator|new
name|DatanodeID
index|[
name|toRecoverBlockNum
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
name|toRecoverBlockNum
condition|;
name|i
operator|++
control|)
block|{
comment|/*          * Kill the datanode which contains one replica          * We need to make sure it dead in namenode: clear its update time and           * trigger NN to check heartbeat.          */
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
name|deadDnIndices
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dnIDs
index|[
name|i
index|]
operator|=
name|dn
operator|.
name|getDatanodeId
argument_list|()
expr_stmt|;
block|}
name|setDataNodesDead
argument_list|(
name|dnIDs
argument_list|)
expr_stmt|;
comment|// Check the locatedBlocks of the file again
name|locatedBlocks
operator|=
name|getLocatedBlocks
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|lastBlock
operator|=
operator|(
name|LocatedStripedBlock
operator|)
name|locatedBlocks
operator|.
name|getLastLocatedBlock
argument_list|()
expr_stmt|;
name|storageInfos
operator|=
name|lastBlock
operator|.
name|getLocations
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|storageInfos
operator|.
name|length
argument_list|,
name|groupSize
operator|-
name|toRecoverBlockNum
argument_list|)
expr_stmt|;
name|int
index|[]
name|targetDNs
init|=
operator|new
name|int
index|[
name|dnNum
operator|-
name|groupSize
index|]
decl_stmt|;
name|n
operator|=
literal|0
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
name|dnNum
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|bitset
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
comment|// not contain replica of the block.
name|targetDNs
index|[
name|n
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
name|waitForRecoveryFinished
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|targetDNs
operator|=
name|sortTargetsByReplicas
argument_list|(
name|blocks
argument_list|,
name|targetDNs
argument_list|)
expr_stmt|;
comment|// Check the replica on the new target node.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|toRecoverBlockNum
condition|;
name|i
operator|++
control|)
block|{
name|File
name|replicaAfterRecovery
init|=
name|cluster
operator|.
name|getBlockFile
argument_list|(
name|targetDNs
index|[
name|i
index|]
argument_list|,
name|blocks
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|File
name|metadataAfterRecovery
init|=
name|cluster
operator|.
name|getBlockMetadataFile
argument_list|(
name|targetDNs
index|[
name|i
index|]
argument_list|,
name|blocks
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|replicaAfterRecovery
operator|.
name|length
argument_list|()
argument_list|,
name|replicas
index|[
name|i
index|]
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadataAfterRecovery
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|blocks
index|[
name|i
index|]
operator|.
name|getGenerationStamp
argument_list|()
operator|+
literal|".meta"
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|replicaContentAfterRecovery
init|=
name|readReplica
argument_list|(
name|replicaAfterRecovery
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|replicaContents
index|[
name|i
index|]
argument_list|,
name|replicaContentAfterRecovery
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|toRecoverBlockNum
condition|;
name|i
operator|++
control|)
block|{
name|restartDataNode
argument_list|(
name|toDead
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
block|}
name|fs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|setDataNodesDead (DatanodeID[] dnIDs)
specifier|private
name|void
name|setDataNodesDead
parameter_list|(
name|DatanodeID
index|[]
name|dnIDs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|DatanodeID
name|dn
range|:
name|dnIDs
control|)
block|{
name|DatanodeDescriptor
name|dnd
init|=
name|NameNodeAdapter
operator|.
name|getDatanode
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
argument_list|,
name|dn
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|setDatanodeDead
argument_list|(
name|dnd
argument_list|)
expr_stmt|;
block|}
name|BlockManagerTestUtil
operator|.
name|checkHeartbeat
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|restartDataNode (int dn)
specifier|private
name|void
name|restartDataNode
parameter_list|(
name|int
name|dn
parameter_list|)
block|{
try|try
block|{
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dn
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
block|}
DECL|method|sortTargetsByReplicas (ExtendedBlock[] blocks, int[] targetDNs)
specifier|private
name|int
index|[]
name|sortTargetsByReplicas
parameter_list|(
name|ExtendedBlock
index|[]
name|blocks
parameter_list|,
name|int
index|[]
name|targetDNs
parameter_list|)
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|blocks
operator|.
name|length
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
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|targetDNs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|targetDNs
index|[
name|j
index|]
operator|!=
operator|-
literal|1
condition|)
block|{
name|File
name|replica
init|=
name|cluster
operator|.
name|getBlockFile
argument_list|(
name|targetDNs
index|[
name|j
index|]
argument_list|,
name|blocks
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|replica
operator|!=
literal|null
condition|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|targetDNs
index|[
name|j
index|]
expr_stmt|;
name|targetDNs
index|[
name|j
index|]
operator|=
operator|-
literal|1
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|result
index|[
name|i
index|]
operator|==
operator|-
literal|1
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to recover striped block: "
operator|+
name|blocks
index|[
name|i
index|]
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|readReplica (File replica)
specifier|private
name|byte
index|[]
name|readReplica
parameter_list|(
name|File
name|replica
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|length
init|=
operator|(
name|int
operator|)
name|replica
operator|.
name|length
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|content
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|replica
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|total
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|total
operator|<
name|length
condition|)
block|{
name|int
name|n
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
name|content
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|total
operator|+=
name|n
expr_stmt|;
block|}
if|if
condition|(
name|total
operator|<
name|length
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to read all content of replica"
argument_list|)
expr_stmt|;
block|}
return|return
name|content
operator|.
name|toByteArray
argument_list|()
return|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|waitForRecoveryFinished (Path file)
specifier|private
name|LocatedBlocks
name|waitForRecoveryFinished
parameter_list|(
name|Path
name|file
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|ATTEMPTS
init|=
literal|60
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
name|ATTEMPTS
condition|;
name|i
operator|++
control|)
block|{
name|LocatedBlocks
name|locatedBlocks
init|=
name|getLocatedBlocks
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|LocatedStripedBlock
name|lastBlock
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|locatedBlocks
operator|.
name|getLastLocatedBlock
argument_list|()
decl_stmt|;
name|DatanodeInfo
index|[]
name|storageInfos
init|=
name|lastBlock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
if|if
condition|(
name|storageInfos
operator|.
name|length
operator|>=
name|groupSize
condition|)
block|{
return|return
name|locatedBlocks
return|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Time out waiting for EC block recovery."
argument_list|)
throw|;
block|}
DECL|method|getLocatedBlocks (Path file)
specifier|private
name|LocatedBlocks
name|getLocatedBlocks
parameter_list|(
name|Path
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
DECL|method|testCreateStripedFile (Path file, int dataLen)
specifier|private
name|void
name|testCreateStripedFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|int
name|dataLen
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|dataLen
index|]
decl_stmt|;
name|DFSUtil
operator|.
name|getRandom
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|writeContents
argument_list|(
name|file
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
DECL|method|writeContents (Path file, byte[] contents)
name|void
name|writeContents
parameter_list|(
name|Path
name|file
parameter_list|,
name|byte
index|[]
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|contents
argument_list|,
literal|0
argument_list|,
name|contents
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

