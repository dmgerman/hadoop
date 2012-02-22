begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.balancer
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
name|balancer
package|;
end_package

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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ClientProtocol
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
operator|.
name|DatanodeReportType
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
name|server
operator|.
name|datanode
operator|.
name|SimulatedFSDataset
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

begin_comment
comment|/**  * This class tests if a balancer schedules tasks correctly.  */
end_comment

begin_class
DECL|class|TestBalancer
specifier|public
class|class
name|TestBalancer
extends|extends
name|TestCase
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
literal|"org.apache.hadoop.hdfs.TestReplication"
argument_list|)
decl_stmt|;
DECL|field|CAPACITY
specifier|final
specifier|private
specifier|static
name|long
name|CAPACITY
init|=
literal|500L
decl_stmt|;
DECL|field|RACK0
specifier|final
specifier|private
specifier|static
name|String
name|RACK0
init|=
literal|"/rack0"
decl_stmt|;
DECL|field|RACK1
specifier|final
specifier|private
specifier|static
name|String
name|RACK1
init|=
literal|"/rack1"
decl_stmt|;
DECL|field|RACK2
specifier|final
specifier|private
specifier|static
name|String
name|RACK2
init|=
literal|"/rack2"
decl_stmt|;
DECL|field|fileName
specifier|final
specifier|static
specifier|private
name|String
name|fileName
init|=
literal|"/tmp.txt"
decl_stmt|;
DECL|field|filePath
specifier|final
specifier|static
specifier|private
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|client
name|ClientProtocol
name|client
decl_stmt|;
DECL|field|TIMEOUT
specifier|static
specifier|final
name|long
name|TIMEOUT
init|=
literal|20000L
decl_stmt|;
comment|//msec
DECL|field|CAPACITY_ALLOWED_VARIANCE
specifier|static
specifier|final
name|double
name|CAPACITY_ALLOWED_VARIANCE
init|=
literal|0.005
decl_stmt|;
comment|// 0.5%
DECL|field|BALANCE_ALLOWED_VARIANCE
specifier|static
specifier|final
name|double
name|BALANCE_ALLOWED_VARIANCE
init|=
literal|0.11
decl_stmt|;
comment|// 10%+delta
DECL|field|DEFAULT_BLOCK_SIZE
specifier|static
specifier|final
name|int
name|DEFAULT_BLOCK_SIZE
init|=
literal|10
decl_stmt|;
DECL|field|r
specifier|private
specifier|static
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
static|static
block|{
name|Balancer
operator|.
name|setBlockMoveWaitTime
argument_list|(
literal|1000L
argument_list|)
expr_stmt|;
block|}
DECL|method|initConf (Configuration conf)
specifier|static
name|void
name|initConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
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
literal|1L
argument_list|)
expr_stmt|;
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BALANCER_MOVEDWINWIDTH_KEY
argument_list|,
literal|2000L
argument_list|)
expr_stmt|;
block|}
comment|/* create a file with a length of<code>fileLen</code> */
DECL|method|createFile (long fileLen, short replicationFactor)
specifier|private
name|void
name|createFile
parameter_list|(
name|long
name|fileLen
parameter_list|,
name|short
name|replicationFactor
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|fileLen
argument_list|,
name|replicationFactor
argument_list|,
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
block|}
comment|/* fill up a cluster with<code>numNodes</code> datanodes     * whose used space to be<code>size</code>    */
DECL|method|generateBlocks (Configuration conf, long size, short numNodes)
specifier|private
name|ExtendedBlock
index|[]
name|generateBlocks
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|long
name|size
parameter_list|,
name|short
name|numNodes
parameter_list|)
throws|throws
name|IOException
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
name|numNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|client
operator|=
name|DFSUtil
operator|.
name|createNamenode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|short
name|replicationFactor
init|=
call|(
name|short
call|)
argument_list|(
name|numNodes
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|fileLen
init|=
name|size
operator|/
name|replicationFactor
decl_stmt|;
name|createFile
argument_list|(
name|fileLen
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|locatedBlocks
init|=
name|client
operator|.
name|getBlockLocations
argument_list|(
name|fileName
argument_list|,
literal|0
argument_list|,
name|fileLen
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
name|int
name|numOfBlocks
init|=
name|locatedBlocks
operator|.
name|size
argument_list|()
decl_stmt|;
name|ExtendedBlock
index|[]
name|blocks
init|=
operator|new
name|ExtendedBlock
index|[
name|numOfBlocks
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
name|numOfBlocks
condition|;
name|i
operator|++
control|)
block|{
name|ExtendedBlock
name|b
init|=
name|locatedBlocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|ExtendedBlock
argument_list|(
name|b
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|b
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|blocks
return|;
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
comment|/* Distribute all blocks according to the given distribution */
DECL|method|distributeBlocks (ExtendedBlock[] blocks, short replicationFactor, final long[] distribution)
specifier|static
name|Block
index|[]
index|[]
name|distributeBlocks
parameter_list|(
name|ExtendedBlock
index|[]
name|blocks
parameter_list|,
name|short
name|replicationFactor
parameter_list|,
specifier|final
name|long
index|[]
name|distribution
parameter_list|)
block|{
comment|// make a copy
name|long
index|[]
name|usedSpace
init|=
operator|new
name|long
index|[
name|distribution
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|distribution
argument_list|,
literal|0
argument_list|,
name|usedSpace
argument_list|,
literal|0
argument_list|,
name|distribution
operator|.
name|length
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|Block
argument_list|>
argument_list|>
name|blockReports
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|Block
argument_list|>
argument_list|>
argument_list|(
name|usedSpace
operator|.
name|length
argument_list|)
decl_stmt|;
name|Block
index|[]
index|[]
name|results
init|=
operator|new
name|Block
index|[
name|usedSpace
operator|.
name|length
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
name|usedSpace
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blockReports
operator|.
name|add
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Block
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|replicationFactor
condition|;
name|j
operator|++
control|)
block|{
name|boolean
name|notChosen
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|notChosen
condition|)
block|{
name|int
name|chosenIndex
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|usedSpace
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|usedSpace
index|[
name|chosenIndex
index|]
operator|>
literal|0
condition|)
block|{
name|notChosen
operator|=
literal|false
expr_stmt|;
name|blockReports
operator|.
name|get
argument_list|(
name|chosenIndex
argument_list|)
operator|.
name|add
argument_list|(
name|blocks
index|[
name|i
index|]
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
expr_stmt|;
name|usedSpace
index|[
name|chosenIndex
index|]
operator|-=
name|blocks
index|[
name|i
index|]
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|usedSpace
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|Block
argument_list|>
name|nodeBlockList
init|=
name|blockReports
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|results
index|[
name|i
index|]
operator|=
name|nodeBlockList
operator|.
name|toArray
argument_list|(
operator|new
name|Block
index|[
name|nodeBlockList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
DECL|method|sum (long[] x)
specifier|static
name|long
name|sum
parameter_list|(
name|long
index|[]
name|x
parameter_list|)
block|{
name|long
name|s
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|long
name|a
range|:
name|x
control|)
block|{
name|s
operator|+=
name|a
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/* we first start a cluster and fill the cluster up to a certain size.    * then redistribute blocks according the required distribution.    * Afterwards a balancer is running to balance the cluster.    */
DECL|method|testUnevenDistribution (Configuration conf, long distribution[], long capacities[], String[] racks)
specifier|private
name|void
name|testUnevenDistribution
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|long
name|distribution
index|[]
parameter_list|,
name|long
name|capacities
index|[]
parameter_list|,
name|String
index|[]
name|racks
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|numDatanodes
init|=
name|distribution
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|capacities
operator|.
name|length
operator|!=
name|numDatanodes
operator|||
name|racks
operator|.
name|length
operator|!=
name|numDatanodes
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Array length is not the same"
argument_list|)
throw|;
block|}
comment|// calculate total space that need to be filled
specifier|final
name|long
name|totalUsedSpace
init|=
name|sum
argument_list|(
name|distribution
argument_list|)
decl_stmt|;
comment|// fill the cluster
name|ExtendedBlock
index|[]
name|blocks
init|=
name|generateBlocks
argument_list|(
name|conf
argument_list|,
name|totalUsedSpace
argument_list|,
operator|(
name|short
operator|)
name|numDatanodes
argument_list|)
decl_stmt|;
comment|// redistribute blocks
name|Block
index|[]
index|[]
name|blocksDN
init|=
name|distributeBlocks
argument_list|(
name|blocks
argument_list|,
call|(
name|short
call|)
argument_list|(
name|numDatanodes
operator|-
literal|1
argument_list|)
argument_list|,
name|distribution
argument_list|)
decl_stmt|;
comment|// restart the cluster: do NOT format the cluster
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY
argument_list|,
literal|"0.0f"
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
name|numDatanodes
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|simulatedCapacities
argument_list|(
name|capacities
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
name|client
operator|=
name|DFSUtil
operator|.
name|createNamenode
argument_list|(
name|conf
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
name|blocksDN
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blocksDN
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|long
name|totalCapacity
init|=
name|sum
argument_list|(
name|capacities
argument_list|)
decl_stmt|;
name|runBalancer
argument_list|(
name|conf
argument_list|,
name|totalUsedSpace
argument_list|,
name|totalCapacity
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Wait until heartbeat gives expected results, within CAPACITY_ALLOWED_VARIANCE,     * summed over all nodes.  Times out after TIMEOUT msec.    * @param expectedUsedSpace    * @param expectedTotalSpace    * @throws IOException - if getStats() fails    * @throws TimeoutException    */
DECL|method|waitForHeartBeat (long expectedUsedSpace, long expectedTotalSpace)
specifier|private
name|void
name|waitForHeartBeat
parameter_list|(
name|long
name|expectedUsedSpace
parameter_list|,
name|long
name|expectedTotalSpace
parameter_list|)
throws|throws
name|IOException
throws|,
name|TimeoutException
block|{
name|long
name|timeout
init|=
name|TIMEOUT
decl_stmt|;
name|long
name|failtime
init|=
operator|(
name|timeout
operator|<=
literal|0L
operator|)
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeout
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|long
index|[]
name|status
init|=
name|client
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|double
name|totalSpaceVariance
init|=
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|double
operator|)
name|status
index|[
literal|0
index|]
operator|-
name|expectedTotalSpace
argument_list|)
operator|/
name|expectedTotalSpace
decl_stmt|;
name|double
name|usedSpaceVariance
init|=
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|double
operator|)
name|status
index|[
literal|1
index|]
operator|-
name|expectedUsedSpace
argument_list|)
operator|/
name|expectedUsedSpace
decl_stmt|;
if|if
condition|(
name|totalSpaceVariance
operator|<
name|CAPACITY_ALLOWED_VARIANCE
operator|&&
name|usedSpaceVariance
operator|<
name|CAPACITY_ALLOWED_VARIANCE
condition|)
break|break;
comment|//done
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|failtime
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Cluster failed to reached expected values of "
operator|+
literal|"totalSpace (current: "
operator|+
name|status
index|[
literal|0
index|]
operator|+
literal|", expected: "
operator|+
name|expectedTotalSpace
operator|+
literal|"), or usedSpace (current: "
operator|+
name|status
index|[
literal|1
index|]
operator|+
literal|", expected: "
operator|+
name|expectedUsedSpace
operator|+
literal|"), in more than "
operator|+
name|timeout
operator|+
literal|" msec."
argument_list|)
throw|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{       }
block|}
block|}
comment|/**    * Wait until balanced: each datanode gives utilization within     * BALANCE_ALLOWED_VARIANCE of average    * @throws IOException    * @throws TimeoutException    */
DECL|method|waitForBalancer (long totalUsedSpace, long totalCapacity)
specifier|private
name|void
name|waitForBalancer
parameter_list|(
name|long
name|totalUsedSpace
parameter_list|,
name|long
name|totalCapacity
parameter_list|)
throws|throws
name|IOException
throws|,
name|TimeoutException
block|{
name|long
name|timeout
init|=
name|TIMEOUT
decl_stmt|;
name|long
name|failtime
init|=
operator|(
name|timeout
operator|<=
literal|0L
operator|)
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeout
decl_stmt|;
specifier|final
name|double
name|avgUtilization
init|=
operator|(
operator|(
name|double
operator|)
name|totalUsedSpace
operator|)
operator|/
name|totalCapacity
decl_stmt|;
name|boolean
name|balanced
decl_stmt|;
do|do
block|{
name|DatanodeInfo
index|[]
name|datanodeReport
init|=
name|client
operator|.
name|getDatanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|datanodeReport
operator|.
name|length
argument_list|,
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|balanced
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|DatanodeInfo
name|datanode
range|:
name|datanodeReport
control|)
block|{
name|double
name|nodeUtilization
init|=
operator|(
operator|(
name|double
operator|)
name|datanode
operator|.
name|getDfsUsed
argument_list|()
operator|)
operator|/
name|datanode
operator|.
name|getCapacity
argument_list|()
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|avgUtilization
operator|-
name|nodeUtilization
argument_list|)
operator|>
name|BALANCE_ALLOWED_VARIANCE
condition|)
block|{
name|balanced
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|failtime
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Rebalancing expected avg utilization to become "
operator|+
name|avgUtilization
operator|+
literal|", but on datanode "
operator|+
name|datanode
operator|+
literal|" it remains at "
operator|+
name|nodeUtilization
operator|+
literal|" after more than "
operator|+
name|TIMEOUT
operator|+
literal|" msec."
argument_list|)
throw|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{           }
break|break;
block|}
block|}
block|}
do|while
condition|(
operator|!
name|balanced
condition|)
do|;
block|}
comment|/** This test start a cluster with specified number of nodes,     * and fills it to be 30% full (with a single file replicated identically    * to all datanodes);    * It then adds one new empty node and starts balancing.    *     * @param conf - configuration    * @param capacities - array of capacities of original nodes in cluster    * @param racks - array of racks for original nodes in cluster    * @param newCapacity - new node's capacity    * @param newRack - new node's rack    * @throws Exception    */
DECL|method|doTest (Configuration conf, long[] capacities, String[] racks, long newCapacity, String newRack)
specifier|private
name|void
name|doTest
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|long
index|[]
name|capacities
parameter_list|,
name|String
index|[]
name|racks
parameter_list|,
name|long
name|newCapacity
parameter_list|,
name|String
name|newRack
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|capacities
operator|.
name|length
argument_list|,
name|racks
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|numOfDatanodes
init|=
name|capacities
operator|.
name|length
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
name|numDataNodes
argument_list|(
name|capacities
operator|.
name|length
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|simulatedCapacities
argument_list|(
name|capacities
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|client
operator|=
name|DFSUtil
operator|.
name|createNamenode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|long
name|totalCapacity
init|=
name|sum
argument_list|(
name|capacities
argument_list|)
decl_stmt|;
comment|// fill up the cluster to be 30% full
name|long
name|totalUsedSpace
init|=
name|totalCapacity
operator|*
literal|3
operator|/
literal|10
decl_stmt|;
name|createFile
argument_list|(
name|totalUsedSpace
operator|/
name|numOfDatanodes
argument_list|,
operator|(
name|short
operator|)
name|numOfDatanodes
argument_list|)
expr_stmt|;
comment|// start up an empty node with the same capacity and on the same rack
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
operator|new
name|String
index|[]
block|{
name|newRack
block|}
argument_list|,
operator|new
name|long
index|[]
block|{
name|newCapacity
block|}
argument_list|)
expr_stmt|;
name|totalCapacity
operator|+=
name|newCapacity
expr_stmt|;
comment|// run balancer and validate results
name|runBalancer
argument_list|(
name|conf
argument_list|,
name|totalUsedSpace
argument_list|,
name|totalCapacity
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
DECL|method|runBalancer (Configuration conf, long totalUsedSpace, long totalCapacity)
specifier|private
name|void
name|runBalancer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|long
name|totalUsedSpace
parameter_list|,
name|long
name|totalCapacity
parameter_list|)
throws|throws
name|Exception
block|{
name|waitForHeartBeat
argument_list|(
name|totalUsedSpace
argument_list|,
name|totalCapacity
argument_list|)
expr_stmt|;
comment|// start rebalancing
specifier|final
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|namenodes
init|=
operator|new
name|ArrayList
argument_list|<
name|InetSocketAddress
argument_list|>
argument_list|()
decl_stmt|;
name|namenodes
operator|.
name|add
argument_list|(
name|NameNode
operator|.
name|getServiceAddress
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|r
init|=
name|Balancer
operator|.
name|run
argument_list|(
name|namenodes
argument_list|,
name|Balancer
operator|.
name|Parameters
operator|.
name|DEFALUT
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Balancer
operator|.
name|ReturnStatus
operator|.
name|SUCCESS
operator|.
name|code
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|waitForHeartBeat
argument_list|(
name|totalUsedSpace
argument_list|,
name|totalCapacity
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Rebalancing with default ctor."
argument_list|)
expr_stmt|;
name|waitForBalancer
argument_list|(
name|totalUsedSpace
argument_list|,
name|totalCapacity
argument_list|)
expr_stmt|;
block|}
comment|/** one-node cluster test*/
DECL|method|oneNodeTest (Configuration conf)
specifier|private
name|void
name|oneNodeTest
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
comment|// add an empty node with half of the CAPACITY& the same rack
name|doTest
argument_list|(
name|conf
argument_list|,
operator|new
name|long
index|[]
block|{
name|CAPACITY
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|RACK0
block|}
argument_list|,
name|CAPACITY
operator|/
literal|2
argument_list|,
name|RACK0
argument_list|)
expr_stmt|;
block|}
comment|/** two-node cluster test */
DECL|method|twoNodeTest (Configuration conf)
specifier|private
name|void
name|twoNodeTest
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
name|conf
argument_list|,
operator|new
name|long
index|[]
block|{
name|CAPACITY
block|,
name|CAPACITY
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|RACK0
block|,
name|RACK1
block|}
argument_list|,
name|CAPACITY
argument_list|,
name|RACK2
argument_list|)
expr_stmt|;
block|}
comment|/** test using a user-supplied conf */
DECL|method|integrationTest (Configuration conf)
specifier|public
name|void
name|integrationTest
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|initConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|oneNodeTest
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/** Test a cluster with even distribution,     * then a new empty node is added to the cluster*/
DECL|method|testBalancer0 ()
specifier|public
name|void
name|testBalancer0
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
name|initConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|oneNodeTest
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|twoNodeTest
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/** Test unevenly distributed cluster */
DECL|method|testBalancer1 ()
specifier|public
name|void
name|testBalancer1
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
name|initConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testUnevenDistribution
argument_list|(
name|conf
argument_list|,
operator|new
name|long
index|[]
block|{
literal|50
operator|*
name|CAPACITY
operator|/
literal|100
block|,
literal|10
operator|*
name|CAPACITY
operator|/
literal|100
block|}
argument_list|,
operator|new
name|long
index|[]
block|{
name|CAPACITY
block|,
name|CAPACITY
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|RACK0
block|,
name|RACK1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBalancer2 ()
specifier|public
name|void
name|testBalancer2
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
name|initConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testBalancerDefaultConstructor
argument_list|(
name|conf
argument_list|,
operator|new
name|long
index|[]
block|{
name|CAPACITY
block|,
name|CAPACITY
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|RACK0
block|,
name|RACK1
block|}
argument_list|,
name|CAPACITY
argument_list|,
name|RACK2
argument_list|)
expr_stmt|;
block|}
DECL|method|testBalancerDefaultConstructor (Configuration conf, long[] capacities, String[] racks, long newCapacity, String newRack)
specifier|private
name|void
name|testBalancerDefaultConstructor
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|long
index|[]
name|capacities
parameter_list|,
name|String
index|[]
name|racks
parameter_list|,
name|long
name|newCapacity
parameter_list|,
name|String
name|newRack
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|numOfDatanodes
init|=
name|capacities
operator|.
name|length
decl_stmt|;
name|assertEquals
argument_list|(
name|numOfDatanodes
argument_list|,
name|racks
operator|.
name|length
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
name|capacities
operator|.
name|length
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|simulatedCapacities
argument_list|(
name|capacities
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|client
operator|=
name|DFSUtil
operator|.
name|createNamenode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|long
name|totalCapacity
init|=
name|sum
argument_list|(
name|capacities
argument_list|)
decl_stmt|;
comment|// fill up the cluster to be 30% full
name|long
name|totalUsedSpace
init|=
name|totalCapacity
operator|*
literal|3
operator|/
literal|10
decl_stmt|;
name|createFile
argument_list|(
name|totalUsedSpace
operator|/
name|numOfDatanodes
argument_list|,
operator|(
name|short
operator|)
name|numOfDatanodes
argument_list|)
expr_stmt|;
comment|// start up an empty node with the same capacity and on the same rack
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
operator|new
name|String
index|[]
block|{
name|newRack
block|}
argument_list|,
operator|new
name|long
index|[]
block|{
name|newCapacity
block|}
argument_list|)
expr_stmt|;
name|totalCapacity
operator|+=
name|newCapacity
expr_stmt|;
comment|// run balancer and validate results
name|runBalancer
argument_list|(
name|conf
argument_list|,
name|totalUsedSpace
argument_list|,
name|totalCapacity
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
comment|/**    * @param args    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|TestBalancer
name|balancerTest
init|=
operator|new
name|TestBalancer
argument_list|()
decl_stmt|;
name|balancerTest
operator|.
name|testBalancer0
argument_list|()
expr_stmt|;
name|balancerTest
operator|.
name|testBalancer1
argument_list|()
expr_stmt|;
name|balancerTest
operator|.
name|testBalancer2
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

