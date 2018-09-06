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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|MiniDFSClusterWithNodeGroup
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
name|NameNodeProxies
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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockPlacementPolicyWithNodeGroup
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
name|net
operator|.
name|NetworkTopology
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
name|net
operator|.
name|NetworkTopologyWithNodeGroup
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
comment|/**  * This class tests if a balancer schedules tasks correctly.  */
end_comment

begin_class
DECL|class|TestBalancerWithNodeGroup
specifier|public
class|class
name|TestBalancerWithNodeGroup
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
literal|"org.apache.hadoop.hdfs.TestBalancerWithNodeGroup"
argument_list|)
decl_stmt|;
DECL|field|CAPACITY
specifier|final
specifier|private
specifier|static
name|long
name|CAPACITY
init|=
literal|5000L
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
DECL|field|NODEGROUP0
specifier|final
specifier|private
specifier|static
name|String
name|NODEGROUP0
init|=
literal|"/nodegroup0"
decl_stmt|;
DECL|field|NODEGROUP1
specifier|final
specifier|private
specifier|static
name|String
name|NODEGROUP1
init|=
literal|"/nodegroup1"
decl_stmt|;
DECL|field|NODEGROUP2
specifier|final
specifier|private
specifier|static
name|String
name|NODEGROUP2
init|=
literal|"/nodegroup2"
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
name|MiniDFSClusterWithNodeGroup
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
literal|40000L
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
literal|100
decl_stmt|;
static|static
block|{
name|TestBalancer
operator|.
name|initTestSetup
argument_list|()
expr_stmt|;
block|}
DECL|method|createConf ()
specifier|static
name|Configuration
name|createConf
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|TestBalancer
operator|.
name|initConf
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
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|NET_TOPOLOGY_IMPL_KEY
argument_list|,
name|NetworkTopologyWithNodeGroup
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|BlockPlacementPolicyWithNodeGroup
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
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
name|Collection
argument_list|<
name|URI
argument_list|>
name|namenodes
init|=
name|DFSUtil
operator|.
name|getInternalNsRpcUris
argument_list|(
name|conf
argument_list|)
decl_stmt|;
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
name|BalancerParameters
operator|.
name|DEFAULT
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ExitStatus
operator|.
name|SUCCESS
operator|.
name|getExitCode
argument_list|()
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
literal|"Rebalancing with default factor."
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
DECL|method|runBalancerCanFinish (Configuration conf, long totalUsedSpace, long totalCapacity)
specifier|private
name|void
name|runBalancerCanFinish
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
name|Collection
argument_list|<
name|URI
argument_list|>
name|namenodes
init|=
name|DFSUtil
operator|.
name|getInternalNsRpcUris
argument_list|(
name|conf
argument_list|)
decl_stmt|;
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
name|BalancerParameters
operator|.
name|DEFAULT
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|r
operator|==
name|ExitStatus
operator|.
name|SUCCESS
operator|.
name|getExitCode
argument_list|()
operator|||
operator|(
name|r
operator|==
name|ExitStatus
operator|.
name|NO_MOVE_PROGRESS
operator|.
name|getExitCode
argument_list|()
operator|)
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
literal|"Rebalancing with default factor."
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlocksOnRack (List<LocatedBlock> blks, String rack)
specifier|private
name|Set
argument_list|<
name|ExtendedBlock
argument_list|>
name|getBlocksOnRack
parameter_list|(
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blks
parameter_list|,
name|String
name|rack
parameter_list|)
block|{
name|Set
argument_list|<
name|ExtendedBlock
argument_list|>
name|ret
init|=
operator|new
name|HashSet
argument_list|<
name|ExtendedBlock
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|blk
range|:
name|blks
control|)
block|{
for|for
control|(
name|DatanodeInfo
name|di
range|:
name|blk
operator|.
name|getLocations
argument_list|()
control|)
block|{
if|if
condition|(
name|rack
operator|.
name|equals
argument_list|(
name|NetworkTopology
operator|.
name|getFirstHalf
argument_list|(
name|di
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|blk
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Create a cluster with even distribution, and a new empty node is added to    * the cluster, then test rack locality for balancer policy.     */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testBalancerWithRackLocality ()
specifier|public
name|void
name|testBalancerWithRackLocality
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|createConf
argument_list|()
decl_stmt|;
name|long
index|[]
name|capacities
init|=
operator|new
name|long
index|[]
block|{
name|CAPACITY
block|,
name|CAPACITY
block|}
decl_stmt|;
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[]
block|{
name|RACK0
block|,
name|RACK1
block|}
decl_stmt|;
name|String
index|[]
name|nodeGroups
init|=
operator|new
name|String
index|[]
block|{
name|NODEGROUP0
block|,
name|NODEGROUP1
block|}
decl_stmt|;
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
name|MiniDFSCluster
operator|.
name|Builder
name|builder
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
decl_stmt|;
name|MiniDFSClusterWithNodeGroup
operator|.
name|setNodeGroups
argument_list|(
name|nodeGroups
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSClusterWithNodeGroup
argument_list|(
name|builder
argument_list|)
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
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|conf
argument_list|,
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
operator|.
name|getProxy
argument_list|()
expr_stmt|;
name|long
name|totalCapacity
init|=
name|TestBalancer
operator|.
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
name|long
name|length
init|=
name|totalUsedSpace
operator|/
name|numOfDatanodes
decl_stmt|;
name|TestBalancer
operator|.
name|createFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
name|length
argument_list|,
operator|(
name|short
operator|)
name|numOfDatanodes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|client
operator|.
name|getBlockLocations
argument_list|(
name|filePath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ExtendedBlock
argument_list|>
name|before
init|=
name|getBlocksOnRack
argument_list|(
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
argument_list|,
name|RACK0
argument_list|)
decl_stmt|;
name|long
name|newCapacity
init|=
name|CAPACITY
decl_stmt|;
name|String
name|newRack
init|=
name|RACK1
decl_stmt|;
name|String
name|newNodeGroup
init|=
name|NODEGROUP2
decl_stmt|;
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
argument_list|,
operator|new
name|String
index|[]
block|{
name|newNodeGroup
block|}
argument_list|)
expr_stmt|;
name|totalCapacity
operator|+=
name|newCapacity
expr_stmt|;
comment|// run balancer and validate results
name|runBalancerCanFinish
argument_list|(
name|conf
argument_list|,
name|totalUsedSpace
argument_list|,
name|totalCapacity
argument_list|)
expr_stmt|;
name|lbs
operator|=
name|client
operator|.
name|getBlockLocations
argument_list|(
name|filePath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ExtendedBlock
argument_list|>
name|after
init|=
name|getBlocksOnRack
argument_list|(
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
argument_list|,
name|RACK0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
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
comment|/**    * Create a cluster with even distribution, and a new empty node is added to    * the cluster, then test node-group locality for balancer policy.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testBalancerWithNodeGroup ()
specifier|public
name|void
name|testBalancerWithNodeGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|createConf
argument_list|()
decl_stmt|;
name|long
index|[]
name|capacities
init|=
operator|new
name|long
index|[]
block|{
name|CAPACITY
block|,
name|CAPACITY
block|,
name|CAPACITY
block|,
name|CAPACITY
block|}
decl_stmt|;
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[]
block|{
name|RACK0
block|,
name|RACK0
block|,
name|RACK1
block|,
name|RACK1
block|}
decl_stmt|;
name|String
index|[]
name|nodeGroups
init|=
operator|new
name|String
index|[]
block|{
name|NODEGROUP0
block|,
name|NODEGROUP0
block|,
name|NODEGROUP1
block|,
name|NODEGROUP2
block|}
decl_stmt|;
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
name|assertEquals
argument_list|(
name|numOfDatanodes
argument_list|,
name|nodeGroups
operator|.
name|length
argument_list|)
expr_stmt|;
name|MiniDFSCluster
operator|.
name|Builder
name|builder
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
decl_stmt|;
name|MiniDFSClusterWithNodeGroup
operator|.
name|setNodeGroups
argument_list|(
name|nodeGroups
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSClusterWithNodeGroup
argument_list|(
name|builder
argument_list|)
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
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|conf
argument_list|,
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
operator|.
name|getProxy
argument_list|()
expr_stmt|;
name|long
name|totalCapacity
init|=
name|TestBalancer
operator|.
name|sum
argument_list|(
name|capacities
argument_list|)
decl_stmt|;
comment|// fill up the cluster to be 20% full
name|long
name|totalUsedSpace
init|=
name|totalCapacity
operator|*
literal|2
operator|/
literal|10
decl_stmt|;
name|TestBalancer
operator|.
name|createFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
name|totalUsedSpace
operator|/
operator|(
name|numOfDatanodes
operator|/
literal|2
operator|)
argument_list|,
call|(
name|short
call|)
argument_list|(
name|numOfDatanodes
operator|/
literal|2
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|long
name|newCapacity
init|=
name|CAPACITY
decl_stmt|;
name|String
name|newRack
init|=
name|RACK1
decl_stmt|;
name|String
name|newNodeGroup
init|=
name|NODEGROUP2
decl_stmt|;
comment|// start up an empty node with the same capacity and on NODEGROUP2
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
argument_list|,
operator|new
name|String
index|[]
block|{
name|newNodeGroup
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
comment|/**    * Create a 4 nodes cluster: 2 nodes (n0, n1) in RACK0/NODEGROUP0, 1 node (n2)    * in RACK1/NODEGROUP1 and 1 node (n3) in RACK1/NODEGROUP2. Fill the cluster     * to 60% and 3 replicas, so n2 and n3 will have replica for all blocks according    * to replica placement policy with NodeGroup. As a result, n2 and n3 will be    * filled with 80% (60% x 4 / 3), and no blocks can be migrated from n2 and n3    * to n0 or n1 as balancer policy with node group. Thus, we expect the balancer    * to end in 5 iterations without move block process.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testBalancerEndInNoMoveProgress ()
specifier|public
name|void
name|testBalancerEndInNoMoveProgress
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|createConf
argument_list|()
decl_stmt|;
name|long
index|[]
name|capacities
init|=
operator|new
name|long
index|[]
block|{
name|CAPACITY
block|,
name|CAPACITY
block|,
name|CAPACITY
block|,
name|CAPACITY
block|}
decl_stmt|;
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[]
block|{
name|RACK0
block|,
name|RACK0
block|,
name|RACK1
block|,
name|RACK1
block|}
decl_stmt|;
name|String
index|[]
name|nodeGroups
init|=
operator|new
name|String
index|[]
block|{
name|NODEGROUP0
block|,
name|NODEGROUP0
block|,
name|NODEGROUP1
block|,
name|NODEGROUP2
block|}
decl_stmt|;
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
name|assertEquals
argument_list|(
name|numOfDatanodes
argument_list|,
name|nodeGroups
operator|.
name|length
argument_list|)
expr_stmt|;
name|MiniDFSCluster
operator|.
name|Builder
name|builder
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
decl_stmt|;
name|MiniDFSClusterWithNodeGroup
operator|.
name|setNodeGroups
argument_list|(
name|nodeGroups
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSClusterWithNodeGroup
argument_list|(
name|builder
argument_list|)
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
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|conf
argument_list|,
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
operator|.
name|getProxy
argument_list|()
expr_stmt|;
name|long
name|totalCapacity
init|=
name|TestBalancer
operator|.
name|sum
argument_list|(
name|capacities
argument_list|)
decl_stmt|;
comment|// fill up the cluster to be 60% full
name|long
name|totalUsedSpace
init|=
name|totalCapacity
operator|*
literal|6
operator|/
literal|10
decl_stmt|;
name|TestBalancer
operator|.
name|createFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
name|totalUsedSpace
operator|/
literal|3
argument_list|,
call|(
name|short
call|)
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// run balancer which can finish in 5 iterations with no block movement.
name|runBalancerCanFinish
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
block|}
end_class

end_unit

