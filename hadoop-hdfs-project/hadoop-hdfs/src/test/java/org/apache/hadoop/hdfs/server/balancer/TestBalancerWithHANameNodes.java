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
name|MiniDFSNNTopology
operator|.
name|NNConf
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
name|ha
operator|.
name|HATestUtil
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
comment|/**  * Test balancer with HA NameNodes  */
end_comment

begin_class
DECL|class|TestBalancerWithHANameNodes
specifier|public
class|class
name|TestBalancerWithHANameNodes
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|client
name|ClientProtocol
name|client
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
comment|/**    * Test a cluster with even distribution, then a new empty node is added to    * the cluster. Test start a cluster with specified number of nodes, and fills    * it to be 30% full (with a single file replicated identically to all    * datanodes); It then adds one new empty node and starts balancing.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testBalancerWithHANameNodes ()
specifier|public
name|void
name|testBalancerWithHANameNodes
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
name|TestBalancer
operator|.
name|initConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|long
name|newNodeCapacity
init|=
name|TestBalancer
operator|.
name|CAPACITY
decl_stmt|;
comment|// new node's capacity
name|String
name|newNodeRack
init|=
name|TestBalancer
operator|.
name|RACK2
decl_stmt|;
comment|// new node's rack
comment|// array of racks for original nodes in cluster
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[]
block|{
name|TestBalancer
operator|.
name|RACK0
block|,
name|TestBalancer
operator|.
name|RACK1
block|}
decl_stmt|;
comment|// array of capacities of original nodes in cluster
name|long
index|[]
name|capacities
init|=
operator|new
name|long
index|[]
block|{
name|TestBalancer
operator|.
name|CAPACITY
block|,
name|TestBalancer
operator|.
name|CAPACITY
block|}
decl_stmt|;
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
name|NNConf
name|nn1Conf
init|=
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn1"
argument_list|)
decl_stmt|;
name|nn1Conf
operator|.
name|setIpcPort
argument_list|(
name|NameNode
operator|.
name|DEFAULT_PORT
argument_list|)
expr_stmt|;
name|Configuration
name|copiedConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|copiedConf
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
name|HATestUtil
operator|.
name|setFailoverConfigurations
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
expr_stmt|;
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
literal|1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|client
operator|=
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|conf
argument_list|,
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|conf
argument_list|)
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
name|TestBalancer
operator|.
name|createFile
argument_list|(
name|cluster
argument_list|,
name|TestBalancer
operator|.
name|filePath
argument_list|,
name|totalUsedSpace
operator|/
name|numOfDatanodes
argument_list|,
operator|(
name|short
operator|)
name|numOfDatanodes
argument_list|,
literal|1
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
name|newNodeRack
block|}
argument_list|,
operator|new
name|long
index|[]
block|{
name|newNodeCapacity
block|}
argument_list|)
expr_stmt|;
name|totalCapacity
operator|+=
name|newNodeCapacity
expr_stmt|;
name|TestBalancer
operator|.
name|waitForHeartBeat
argument_list|(
name|totalUsedSpace
argument_list|,
name|totalCapacity
argument_list|,
name|client
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|URI
argument_list|>
name|namenodes
init|=
name|DFSUtil
operator|.
name|getNsServiceRpcUris
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|namenodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|namenodes
operator|.
name|contains
argument_list|(
name|HATestUtil
operator|.
name|getLogicalUri
argument_list|(
name|cluster
argument_list|)
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
name|TestBalancer
operator|.
name|waitForBalancer
argument_list|(
name|totalUsedSpace
argument_list|,
name|totalCapacity
argument_list|,
name|client
argument_list|,
name|cluster
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

