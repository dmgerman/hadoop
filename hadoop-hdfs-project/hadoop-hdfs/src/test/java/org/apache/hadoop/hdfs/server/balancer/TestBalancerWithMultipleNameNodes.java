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
name|LeaseManager
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

begin_comment
comment|/**  * Test balancer with multiple NameNodes  */
end_comment

begin_class
DECL|class|TestBalancerWithMultipleNameNodes
specifier|public
class|class
name|TestBalancerWithMultipleNameNodes
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|Balancer
operator|.
name|LOG
decl_stmt|;
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|LOG
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
name|OFF
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|LeaseManager
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|OFF
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
name|OFF
argument_list|)
expr_stmt|;
comment|//    ((Log4JLogger)DataNode.LOG).getLogger().setLevel(Level.OFF);
block|}
DECL|field|CAPACITY
specifier|private
specifier|static
specifier|final
name|long
name|CAPACITY
init|=
literal|500L
decl_stmt|;
DECL|field|RACK0
specifier|private
specifier|static
specifier|final
name|String
name|RACK0
init|=
literal|"/rack0"
decl_stmt|;
DECL|field|RACK1
specifier|private
specifier|static
specifier|final
name|String
name|RACK1
init|=
literal|"/rack1"
decl_stmt|;
DECL|field|RACK2
specifier|private
specifier|static
specifier|final
name|String
name|RACK2
init|=
literal|"/rack2"
decl_stmt|;
DECL|field|FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"/tmp.txt"
decl_stmt|;
DECL|field|FILE_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|FILE_PATH
init|=
operator|new
name|Path
argument_list|(
name|FILE_NAME
argument_list|)
decl_stmt|;
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
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
comment|/** Common objects used in various methods. */
DECL|class|Suite
specifier|private
specifier|static
class|class
name|Suite
block|{
DECL|field|conf
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|final
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|clients
specifier|final
name|ClientProtocol
index|[]
name|clients
decl_stmt|;
DECL|field|replication
specifier|final
name|short
name|replication
decl_stmt|;
DECL|method|Suite (MiniDFSCluster cluster, final int nNameNodes, final int nDataNodes, Configuration conf)
name|Suite
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
specifier|final
name|int
name|nNameNodes
parameter_list|,
specifier|final
name|int
name|nDataNodes
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
name|clients
operator|=
operator|new
name|ClientProtocol
index|[
name|nNameNodes
index|]
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
name|nNameNodes
condition|;
name|i
operator|++
control|)
block|{
name|clients
index|[
name|i
index|]
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|(
name|i
argument_list|)
operator|.
name|getRpcServer
argument_list|()
expr_stmt|;
block|}
name|replication
operator|=
operator|(
name|short
operator|)
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|nDataNodes
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* create a file with a length of<code>fileLen</code> */
DECL|method|createFile (Suite s, int index, long len )
specifier|private
specifier|static
name|void
name|createFile
parameter_list|(
name|Suite
name|s
parameter_list|,
name|int
name|index
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileSystem
name|fs
init|=
name|s
operator|.
name|cluster
operator|.
name|getFileSystem
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|,
name|len
argument_list|,
name|s
operator|.
name|replication
argument_list|,
name|RANDOM
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
name|FILE_PATH
argument_list|,
name|s
operator|.
name|replication
argument_list|)
expr_stmt|;
block|}
comment|/* fill up a cluster with<code>numNodes</code> datanodes     * whose used space to be<code>size</code>    */
DECL|method|generateBlocks (Suite s, long size )
specifier|private
specifier|static
name|ExtendedBlock
index|[]
index|[]
name|generateBlocks
parameter_list|(
name|Suite
name|s
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ExtendedBlock
index|[]
index|[]
name|blocks
init|=
operator|new
name|ExtendedBlock
index|[
name|s
operator|.
name|clients
operator|.
name|length
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|s
operator|.
name|clients
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
specifier|final
name|long
name|fileLen
init|=
name|size
operator|/
name|s
operator|.
name|replication
decl_stmt|;
name|createFile
argument_list|(
name|s
argument_list|,
name|n
argument_list|,
name|fileLen
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|locatedBlocks
init|=
name|s
operator|.
name|clients
index|[
name|n
index|]
operator|.
name|getBlockLocations
argument_list|(
name|FILE_NAME
argument_list|,
literal|0
argument_list|,
name|fileLen
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numOfBlocks
init|=
name|locatedBlocks
operator|.
name|size
argument_list|()
decl_stmt|;
name|blocks
index|[
name|n
index|]
operator|=
operator|new
name|ExtendedBlock
index|[
name|numOfBlocks
index|]
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
name|numOfBlocks
condition|;
name|i
operator|++
control|)
block|{
specifier|final
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
name|n
index|]
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
block|}
return|return
name|blocks
return|;
block|}
comment|/* wait for one heartbeat */
DECL|method|wait (final ClientProtocol[] clients, long expectedUsedSpace, long expectedTotalSpace)
specifier|static
name|void
name|wait
parameter_list|(
specifier|final
name|ClientProtocol
index|[]
name|clients
parameter_list|,
name|long
name|expectedUsedSpace
parameter_list|,
name|long
name|expectedTotalSpace
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"WAIT expectedUsedSpace="
operator|+
name|expectedUsedSpace
operator|+
literal|", expectedTotalSpace="
operator|+
name|expectedTotalSpace
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|clients
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|boolean
name|done
init|=
literal|false
init|;
operator|!
name|done
condition|;
control|)
block|{
specifier|final
name|long
index|[]
name|s
init|=
name|clients
index|[
name|n
index|]
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|done
operator|=
name|s
index|[
literal|0
index|]
operator|==
name|expectedTotalSpace
operator|&&
name|s
index|[
literal|1
index|]
operator|==
name|expectedUsedSpace
expr_stmt|;
if|if
condition|(
operator|!
name|done
condition|)
block|{
name|sleep
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|i
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"WAIT i="
operator|+
name|i
operator|+
literal|", s=["
operator|+
name|s
index|[
literal|0
index|]
operator|+
literal|", "
operator|+
name|s
index|[
literal|1
index|]
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|runBalancer (Suite s, final long totalUsed, final long totalCapacity)
specifier|static
name|void
name|runBalancer
parameter_list|(
name|Suite
name|s
parameter_list|,
specifier|final
name|long
name|totalUsed
parameter_list|,
specifier|final
name|long
name|totalCapacity
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|double
name|avg
init|=
name|totalUsed
operator|*
literal|100.0
operator|/
name|totalCapacity
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"BALANCER 0: totalUsed="
operator|+
name|totalUsed
operator|+
literal|", totalCapacity="
operator|+
name|totalCapacity
operator|+
literal|", avg="
operator|+
name|avg
argument_list|)
expr_stmt|;
name|wait
argument_list|(
name|s
operator|.
name|clients
argument_list|,
name|totalUsed
argument_list|,
name|totalCapacity
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"BALANCER 1"
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
name|DFSUtil
operator|.
name|getNNServiceRpcAddresses
argument_list|(
name|s
operator|.
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
name|Balancer
operator|.
name|Parameters
operator|.
name|DEFALUT
argument_list|,
name|s
operator|.
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
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
name|LOG
operator|.
name|info
argument_list|(
literal|"BALANCER 2"
argument_list|)
expr_stmt|;
name|wait
argument_list|(
name|s
operator|.
name|clients
argument_list|,
name|totalUsed
argument_list|,
name|totalCapacity
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"BALANCER 3"
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|boolean
name|balanced
init|=
literal|false
init|;
operator|!
name|balanced
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
index|[]
name|used
init|=
operator|new
name|long
index|[
name|s
operator|.
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|long
index|[]
name|cap
init|=
operator|new
name|long
index|[
name|used
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|s
operator|.
name|clients
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
specifier|final
name|DatanodeInfo
index|[]
name|datanodes
init|=
name|s
operator|.
name|clients
index|[
name|n
index|]
operator|.
name|getDatanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|length
argument_list|,
name|used
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|datanodes
operator|.
name|length
condition|;
name|d
operator|++
control|)
block|{
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
name|used
index|[
name|d
index|]
operator|=
name|datanodes
index|[
name|d
index|]
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
name|cap
index|[
name|d
index|]
operator|=
name|datanodes
index|[
name|d
index|]
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"datanodes["
operator|+
name|d
operator|+
literal|"]: getDfsUsed()="
operator|+
name|datanodes
index|[
name|d
index|]
operator|.
name|getDfsUsed
argument_list|()
operator|+
literal|", getCapacity()="
operator|+
name|datanodes
index|[
name|d
index|]
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|used
index|[
name|d
index|]
argument_list|,
name|datanodes
index|[
name|d
index|]
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cap
index|[
name|d
index|]
argument_list|,
name|datanodes
index|[
name|d
index|]
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|balanced
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|used
operator|.
name|length
condition|;
name|d
operator|++
control|)
block|{
specifier|final
name|double
name|p
init|=
name|used
index|[
name|d
index|]
operator|*
literal|100.0
operator|/
name|cap
index|[
name|d
index|]
decl_stmt|;
name|balanced
operator|=
name|p
operator|<=
name|avg
operator|+
name|Balancer
operator|.
name|Parameters
operator|.
name|DEFALUT
operator|.
name|threshold
expr_stmt|;
if|if
condition|(
operator|!
name|balanced
condition|)
block|{
if|if
condition|(
name|i
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"datanodes "
operator|+
name|d
operator|+
literal|" is not yet balanced: "
operator|+
literal|"used="
operator|+
name|used
index|[
name|d
index|]
operator|+
literal|", cap="
operator|+
name|cap
index|[
name|d
index|]
operator|+
literal|", avg="
operator|+
name|avg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"TestBalancer.sum(used)="
operator|+
name|TestBalancer
operator|.
name|sum
argument_list|(
name|used
argument_list|)
operator|+
literal|", TestBalancer.sum(cap)="
operator|+
name|TestBalancer
operator|.
name|sum
argument_list|(
name|cap
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"BALANCER 6"
argument_list|)
expr_stmt|;
block|}
DECL|method|sleep (long ms)
specifier|private
specifier|static
name|void
name|sleep
parameter_list|(
name|long
name|ms
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|ms
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createConf ()
specifier|private
specifier|static
name|Configuration
name|createConf
parameter_list|()
block|{
specifier|final
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
return|return
name|conf
return|;
block|}
comment|/**    * First start a cluster and fill the cluster up to a certain size.    * Then redistribute blocks according the required distribution.    * Finally, balance the cluster.    *     * @param nNameNodes Number of NameNodes    * @param distributionPerNN The distribution for each NameNode.     * @param capacities Capacities of the datanodes    * @param racks Rack names    * @param conf Configuration    */
DECL|method|unevenDistribution (final int nNameNodes, long distributionPerNN[], long capacities[], String[] racks, Configuration conf)
specifier|private
name|void
name|unevenDistribution
parameter_list|(
specifier|final
name|int
name|nNameNodes
parameter_list|,
name|long
name|distributionPerNN
index|[]
parameter_list|,
name|long
name|capacities
index|[]
parameter_list|,
name|String
index|[]
name|racks
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 0"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|nDataNodes
init|=
name|distributionPerNN
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|capacities
operator|.
name|length
operator|!=
name|nDataNodes
operator|||
name|racks
operator|.
name|length
operator|!=
name|nDataNodes
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
name|usedSpacePerNN
init|=
name|TestBalancer
operator|.
name|sum
argument_list|(
name|distributionPerNN
argument_list|)
decl_stmt|;
comment|// fill the cluster
specifier|final
name|ExtendedBlock
index|[]
index|[]
name|blocks
decl_stmt|;
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 1"
argument_list|)
expr_stmt|;
specifier|final
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
name|numNameNodes
argument_list|(
name|nNameNodes
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|nDataNodes
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
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 2"
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 3"
argument_list|)
expr_stmt|;
specifier|final
name|Suite
name|s
init|=
operator|new
name|Suite
argument_list|(
name|cluster
argument_list|,
name|nNameNodes
argument_list|,
name|nDataNodes
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|blocks
operator|=
name|generateBlocks
argument_list|(
name|s
argument_list|,
name|usedSpacePerNN
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 4"
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
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 10"
argument_list|)
expr_stmt|;
specifier|final
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
name|numNameNodes
argument_list|(
name|nNameNodes
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|nDataNodes
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
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 11"
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 12"
argument_list|)
expr_stmt|;
specifier|final
name|Suite
name|s
init|=
operator|new
name|Suite
argument_list|(
name|cluster
argument_list|,
name|nNameNodes
argument_list|,
name|nDataNodes
argument_list|,
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|nNameNodes
condition|;
name|n
operator|++
control|)
block|{
comment|// redistribute blocks
specifier|final
name|Block
index|[]
index|[]
name|blocksDN
init|=
name|TestBalancer
operator|.
name|distributeBlocks
argument_list|(
name|blocks
index|[
name|n
index|]
argument_list|,
name|s
operator|.
name|replication
argument_list|,
name|distributionPerNN
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|blocksDN
operator|.
name|length
condition|;
name|d
operator|++
control|)
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|n
argument_list|,
name|d
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blocksDN
index|[
name|d
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 13: n="
operator|+
name|n
argument_list|)
expr_stmt|;
block|}
specifier|final
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
specifier|final
name|long
name|totalUsed
init|=
name|nNameNodes
operator|*
name|usedSpacePerNN
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 14"
argument_list|)
expr_stmt|;
name|runBalancer
argument_list|(
name|s
argument_list|,
name|totalUsed
argument_list|,
name|totalCapacity
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 15"
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
name|LOG
operator|.
name|info
argument_list|(
literal|"UNEVEN 16"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This test start a cluster, fill the DataNodes to be 30% full;    * It then adds an empty node and start balancing.    *    * @param nNameNodes Number of NameNodes    * @param capacities Capacities of the datanodes    * @param racks Rack names    * @param newCapacity the capacity of the new DataNode    * @param newRack the rack for the new DataNode    * @param conf Configuration    */
DECL|method|runTest (final int nNameNodes, long[] capacities, String[] racks, long newCapacity, String newRack, Configuration conf)
specifier|private
name|void
name|runTest
parameter_list|(
specifier|final
name|int
name|nNameNodes
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
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|nDataNodes
init|=
name|capacities
operator|.
name|length
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"nNameNodes="
operator|+
name|nNameNodes
operator|+
literal|", nDataNodes="
operator|+
name|nDataNodes
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nDataNodes
argument_list|,
name|racks
operator|.
name|length
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"RUN_TEST -1"
argument_list|)
expr_stmt|;
specifier|final
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
name|numNameNodes
argument_list|(
name|nNameNodes
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|nDataNodes
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
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"RUN_TEST 0"
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"RUN_TEST 1"
argument_list|)
expr_stmt|;
specifier|final
name|Suite
name|s
init|=
operator|new
name|Suite
argument_list|(
name|cluster
argument_list|,
name|nNameNodes
argument_list|,
name|nDataNodes
argument_list|,
name|conf
argument_list|)
decl_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"RUN_TEST 2"
argument_list|)
expr_stmt|;
comment|// fill up the cluster to be 30% full
specifier|final
name|long
name|totalUsed
init|=
name|totalCapacity
operator|*
literal|3
operator|/
literal|10
decl_stmt|;
specifier|final
name|long
name|size
init|=
operator|(
name|totalUsed
operator|/
name|nNameNodes
operator|)
operator|/
name|s
operator|.
name|replication
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|nNameNodes
condition|;
name|n
operator|++
control|)
block|{
name|createFile
argument_list|(
name|s
argument_list|,
name|n
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"RUN_TEST 3"
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
name|LOG
operator|.
name|info
argument_list|(
literal|"RUN_TEST 4"
argument_list|)
expr_stmt|;
comment|// run RUN_TEST and validate results
name|runBalancer
argument_list|(
name|s
argument_list|,
name|totalUsed
argument_list|,
name|totalCapacity
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"RUN_TEST 5"
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
name|LOG
operator|.
name|info
argument_list|(
literal|"RUN_TEST 6"
argument_list|)
expr_stmt|;
block|}
comment|/** Test a cluster with even distribution,     * then a new empty node is added to the cluster    */
annotation|@
name|Test
DECL|method|testBalancer ()
specifier|public
name|void
name|testBalancer
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
name|createConf
argument_list|()
decl_stmt|;
name|runTest
argument_list|(
literal|2
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
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/** Test unevenly distributed cluster */
annotation|@
name|Test
DECL|method|testUnevenDistribution ()
specifier|public
name|void
name|testUnevenDistribution
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
name|createConf
argument_list|()
decl_stmt|;
name|unevenDistribution
argument_list|(
literal|2
argument_list|,
operator|new
name|long
index|[]
block|{
literal|30
operator|*
name|CAPACITY
operator|/
literal|100
block|,
literal|5
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
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

