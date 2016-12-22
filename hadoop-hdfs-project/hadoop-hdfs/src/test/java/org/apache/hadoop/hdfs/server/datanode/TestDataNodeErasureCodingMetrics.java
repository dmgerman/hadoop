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
name|StripedFileTestUtil
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
name|ErasureCodingPolicy
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
name|namenode
operator|.
name|ErasureCodingPolicyManager
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getLongCounter
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getLongCounterWithoutCheck
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|assertNotNull
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
name|IOException
import|;
end_import

begin_comment
comment|/**  * This file tests the erasure coding metrics in DataNode.  */
end_comment

begin_class
DECL|class|TestDataNodeErasureCodingMetrics
specifier|public
class|class
name|TestDataNodeErasureCodingMetrics
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
name|TestDataNodeErasureCodingMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ecPolicy
specifier|private
specifier|final
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|ErasureCodingPolicyManager
operator|.
name|getSystemDefaultPolicy
argument_list|()
decl_stmt|;
DECL|field|dataBlocks
specifier|private
specifier|final
name|int
name|dataBlocks
init|=
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
decl_stmt|;
DECL|field|parityBlocks
specifier|private
specifier|final
name|int
name|parityBlocks
init|=
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
init|=
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
init|=
name|cellSize
operator|*
literal|2
decl_stmt|;
DECL|field|groupSize
specifier|private
specifier|final
name|int
name|groupSize
init|=
name|dataBlocks
operator|+
name|parityBlocks
decl_stmt|;
DECL|field|blockGroupSize
specifier|private
specifier|final
name|int
name|blockGroupSize
init|=
name|blockSize
operator|*
name|dataBlocks
decl_stmt|;
DECL|field|numDNs
specifier|private
specifier|final
name|int
name|numDNs
init|=
name|groupSize
operator|+
literal|1
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
name|DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY
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
name|numDNs
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
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|setErasureCodingPolicy
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
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
DECL|method|testFullBlock ()
specifier|public
name|void
name|testFullBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"/testEcMetrics"
argument_list|,
name|blockGroupSize
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionTasks should be "
argument_list|,
literal|1
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcReconstructionTasks"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcFailedReconstructionTasks should be "
argument_list|,
literal|0
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcFailedReconstructionTasks"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getLongMetric
argument_list|(
literal|"EcDecodingTimeNanos"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionBytesRead should be "
argument_list|,
name|blockGroupSize
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcReconstructionBytesRead"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionBytesWritten should be "
argument_list|,
name|blockSize
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcReconstructionBytesWritten"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionRemoteBytesRead should be "
argument_list|,
literal|0
argument_list|,
name|getLongMetricWithoutCheck
argument_list|(
literal|"EcReconstructionRemoteBytesRead"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// A partial block, reconstruct the partial block
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testReconstructionBytesPartialGroup1 ()
specifier|public
name|void
name|testReconstructionBytesPartialGroup1
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|fileLen
init|=
name|blockSize
operator|/
literal|10
decl_stmt|;
name|doTest
argument_list|(
literal|"/testEcBytes"
argument_list|,
name|fileLen
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionBytesRead should be "
argument_list|,
name|fileLen
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcReconstructionBytesRead"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionBytesWritten should be "
argument_list|,
name|fileLen
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcReconstructionBytesWritten"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionRemoteBytesRead should be "
argument_list|,
literal|0
argument_list|,
name|getLongMetricWithoutCheck
argument_list|(
literal|"EcReconstructionRemoteBytesRead"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// 1 full block + 5 partial block, reconstruct the full block
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testReconstructionBytesPartialGroup2 ()
specifier|public
name|void
name|testReconstructionBytesPartialGroup2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|fileLen
init|=
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
name|cellSize
operator|/
literal|10
decl_stmt|;
name|doTest
argument_list|(
literal|"/testEcBytes"
argument_list|,
name|fileLen
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ecReconstructionBytesRead should be "
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
name|cellSize
operator|/
literal|10
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcReconstructionBytesRead"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionBytesWritten should be "
argument_list|,
name|blockSize
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcReconstructionBytesWritten"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionRemoteBytesRead should be "
argument_list|,
literal|0
argument_list|,
name|getLongMetricWithoutCheck
argument_list|(
literal|"EcReconstructionRemoteBytesRead"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// 1 full block + 5 partial block, reconstruct the partial block
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testReconstructionBytesPartialGroup3 ()
specifier|public
name|void
name|testReconstructionBytesPartialGroup3
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|fileLen
init|=
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
name|cellSize
operator|/
literal|10
decl_stmt|;
name|doTest
argument_list|(
literal|"/testEcBytes"
argument_list|,
name|fileLen
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ecReconstructionBytesRead should be "
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
operator|(
name|cellSize
operator|/
literal|10
operator|)
operator|*
literal|2
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcReconstructionBytesRead"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ecReconstructionBytesWritten should be "
argument_list|,
name|cellSize
operator|+
name|cellSize
operator|/
literal|10
argument_list|,
name|getLongMetric
argument_list|(
literal|"EcReconstructionBytesWritten"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"EcReconstructionRemoteBytesRead should be "
argument_list|,
literal|0
argument_list|,
name|getLongMetricWithoutCheck
argument_list|(
literal|"EcReconstructionRemoteBytesRead"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getLongMetric (String metricName)
specifier|private
name|long
name|getLongMetric
parameter_list|(
name|String
name|metricName
parameter_list|)
block|{
name|long
name|metricValue
init|=
literal|0
decl_stmt|;
comment|// Add all reconstruction metric value from all data nodes
for|for
control|(
name|DataNode
name|dn
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|dn
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|metricValue
operator|+=
name|getLongCounter
argument_list|(
name|metricName
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
return|return
name|metricValue
return|;
block|}
DECL|method|getLongMetricWithoutCheck (String metricName)
specifier|private
name|long
name|getLongMetricWithoutCheck
parameter_list|(
name|String
name|metricName
parameter_list|)
block|{
name|long
name|metricValue
init|=
literal|0
decl_stmt|;
comment|// Add all reconstruction metric value from all data nodes
for|for
control|(
name|DataNode
name|dn
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|dn
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|metricValue
operator|+=
name|getLongCounterWithoutCheck
argument_list|(
name|metricName
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
return|return
name|metricValue
return|;
block|}
DECL|method|doTest (String fileName, int fileLen, int deadNodeIndex)
specifier|private
name|void
name|doTest
parameter_list|(
name|String
name|fileName
parameter_list|,
name|int
name|fileLen
parameter_list|,
name|int
name|deadNodeIndex
parameter_list|)
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|fileLen
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|deadNodeIndex
operator|>=
literal|0
operator|&&
name|deadNodeIndex
operator|<
name|numDNs
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|fileLen
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|waitBlockGroupsReported
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
specifier|final
name|LocatedBlocks
name|locatedBlocks
init|=
name|StripedFileTestUtil
operator|.
name|getLocatedBlocks
argument_list|(
name|file
argument_list|,
name|fs
argument_list|)
decl_stmt|;
specifier|final
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
name|assertTrue
argument_list|(
name|lastBlock
operator|.
name|getLocations
argument_list|()
operator|.
name|length
operator|>
name|deadNodeIndex
argument_list|)
expr_stmt|;
specifier|final
name|DataNode
name|toCorruptDn
init|=
name|cluster
operator|.
name|getDataNode
argument_list|(
name|lastBlock
operator|.
name|getLocations
argument_list|()
index|[
name|deadNodeIndex
index|]
operator|.
name|getIpcPort
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Datanode to be corrupted: "
operator|+
name|toCorruptDn
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Failed to find a datanode to be corrupted"
argument_list|,
name|toCorruptDn
argument_list|)
expr_stmt|;
name|toCorruptDn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|setDataNodeDead
argument_list|(
name|toCorruptDn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForDatanodeState
argument_list|(
name|cluster
argument_list|,
name|toCorruptDn
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
specifier|final
name|int
name|workCount
init|=
name|getComputedDatanodeWork
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Wrongly computed block reconstruction work"
argument_list|,
name|workCount
operator|>
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
name|int
name|totalBlocks
init|=
operator|(
name|fileLen
operator|/
name|blockGroupSize
operator|)
operator|*
name|groupSize
decl_stmt|;
specifier|final
name|int
name|remainder
init|=
name|fileLen
operator|%
name|blockGroupSize
decl_stmt|;
name|totalBlocks
operator|+=
operator|(
name|remainder
operator|==
literal|0
operator|)
condition|?
literal|0
else|:
operator|(
name|remainder
operator|%
name|blockSize
operator|==
literal|0
operator|)
condition|?
name|remainder
operator|/
name|blockSize
operator|+
name|parityBlocks
else|:
name|remainder
operator|/
name|blockSize
operator|+
literal|1
operator|+
name|parityBlocks
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|waitForAllReconstructionFinished
argument_list|(
name|file
argument_list|,
name|fs
argument_list|,
name|totalBlocks
argument_list|)
expr_stmt|;
block|}
DECL|method|getComputedDatanodeWork ()
specifier|private
name|int
name|getComputedDatanodeWork
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|BlockManager
name|bm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
comment|// Giving a grace period to compute datanode work.
name|int
name|workCount
init|=
literal|0
decl_stmt|;
name|int
name|retries
init|=
literal|20
decl_stmt|;
while|while
condition|(
name|retries
operator|>
literal|0
condition|)
block|{
name|workCount
operator|=
name|BlockManagerTestUtil
operator|.
name|getComputedDatanodeWork
argument_list|(
name|bm
argument_list|)
expr_stmt|;
if|if
condition|(
name|workCount
operator|>
literal|0
condition|)
block|{
break|break;
block|}
name|retries
operator|--
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Computed datanode work: "
operator|+
name|workCount
operator|+
literal|", retries: "
operator|+
name|retries
argument_list|)
expr_stmt|;
return|return
name|workCount
return|;
block|}
DECL|method|setDataNodeDead (DatanodeID dnID)
specifier|private
name|void
name|setDataNodeDead
parameter_list|(
name|DatanodeID
name|dnID
parameter_list|)
throws|throws
name|IOException
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
name|dnID
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|setDatanodeDead
argument_list|(
name|dnd
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

