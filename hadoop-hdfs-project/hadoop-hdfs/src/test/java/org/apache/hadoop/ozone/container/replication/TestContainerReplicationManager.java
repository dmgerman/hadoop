begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.replication
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|replication
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
name|ozone
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|container
operator|.
name|TestUtils
operator|.
name|ReplicationDatanodeStateManager
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
name|ozone
operator|.
name|container
operator|.
name|TestUtils
operator|.
name|ReplicationNodeManagerMock
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
name|ozone
operator|.
name|container
operator|.
name|TestUtils
operator|.
name|ReplicationNodePoolManagerMock
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|SCMTestUtils
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|replication
operator|.
name|ContainerReplicationManager
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
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|replication
operator|.
name|InProgressPool
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|CommandQueue
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|NodePoolManager
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
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
operator|.
name|LogCapturer
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
name|LinkedList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_REPORTS_WAIT_TIMEOUT_SECONDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
operator|.
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Uninterruptibles
operator|.
name|sleepUninterruptibly
import|;
end_import

begin_comment
comment|/**  * Tests for the container manager.  */
end_comment

begin_class
DECL|class|TestContainerReplicationManager
specifier|public
class|class
name|TestContainerReplicationManager
block|{
DECL|field|POOL_NAME_TEMPLATE
specifier|final
specifier|static
name|String
name|POOL_NAME_TEMPLATE
init|=
literal|"Pool%d"
decl_stmt|;
DECL|field|MAX_DATANODES
specifier|static
specifier|final
name|int
name|MAX_DATANODES
init|=
literal|72
decl_stmt|;
DECL|field|POOL_SIZE
specifier|static
specifier|final
name|int
name|POOL_SIZE
init|=
literal|24
decl_stmt|;
DECL|field|POOL_COUNT
specifier|static
specifier|final
name|int
name|POOL_COUNT
init|=
literal|3
decl_stmt|;
DECL|field|logCapturer
specifier|private
name|LogCapturer
name|logCapturer
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ContainerReplicationManager
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|datanodes
specifier|private
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|datanodes
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|nodeManager
specifier|private
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|poolManager
specifier|private
name|NodePoolManager
name|poolManager
decl_stmt|;
DECL|field|commandQueue
specifier|private
name|CommandQueue
name|commandQueue
decl_stmt|;
DECL|field|replicationManager
specifier|private
name|ContainerReplicationManager
name|replicationManager
decl_stmt|;
DECL|field|datanodeStateManager
specifier|private
name|ReplicationDatanodeStateManager
name|datanodeStateManager
decl_stmt|;
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|logCapturer
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|DatanodeID
argument_list|,
name|NodeManager
operator|.
name|NODESTATE
argument_list|>
name|nodeStateMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// We are setting up 3 pools with 24 nodes each in this cluster.
comment|// First we create 72 Datanodes.
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|MAX_DATANODES
condition|;
name|x
operator|++
control|)
block|{
name|DatanodeID
name|datanode
init|=
name|SCMTestUtils
operator|.
name|getDatanodeID
argument_list|()
decl_stmt|;
name|datanodes
operator|.
name|add
argument_list|(
name|datanode
argument_list|)
expr_stmt|;
name|nodeStateMap
operator|.
name|put
argument_list|(
name|datanode
argument_list|,
name|NodeManager
operator|.
name|NODESTATE
operator|.
name|HEALTHY
argument_list|)
expr_stmt|;
block|}
comment|// All nodes in this cluster are healthy for time being.
name|nodeManager
operator|=
operator|new
name|ReplicationNodeManagerMock
argument_list|(
name|nodeStateMap
argument_list|)
expr_stmt|;
name|poolManager
operator|=
operator|new
name|ReplicationNodePoolManagerMock
argument_list|()
expr_stmt|;
name|commandQueue
operator|=
operator|new
name|CommandQueue
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Max datanodes should be equal to POOL_SIZE * "
operator|+
literal|"POOL_COUNT"
argument_list|,
name|POOL_COUNT
operator|*
name|POOL_SIZE
argument_list|,
name|MAX_DATANODES
argument_list|)
expr_stmt|;
comment|// Start from 1 instead of zero so we can multiply and get the node index.
for|for
control|(
name|int
name|y
init|=
literal|1
init|;
name|y
operator|<=
name|POOL_COUNT
condition|;
name|y
operator|++
control|)
block|{
name|String
name|poolName
init|=
name|String
operator|.
name|format
argument_list|(
name|POOL_NAME_TEMPLATE
argument_list|,
name|y
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|z
init|=
literal|0
init|;
name|z
operator|<
name|POOL_SIZE
condition|;
name|z
operator|++
control|)
block|{
name|DatanodeID
name|id
init|=
name|datanodes
operator|.
name|get
argument_list|(
name|y
operator|*
name|z
argument_list|)
decl_stmt|;
name|poolManager
operator|.
name|addNode
argument_list|(
name|poolName
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|OzoneConfiguration
name|config
init|=
name|SCMTestUtils
operator|.
name|getOzoneConf
argument_list|()
decl_stmt|;
name|config
operator|.
name|setInt
argument_list|(
name|OZONE_SCM_CONTAINER_REPORTS_WAIT_TIMEOUT_SECONDS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|replicationManager
operator|=
operator|new
name|ContainerReplicationManager
argument_list|(
name|config
argument_list|,
name|nodeManager
argument_list|,
name|poolManager
argument_list|,
name|commandQueue
argument_list|)
expr_stmt|;
name|datanodeStateManager
operator|=
operator|new
name|ReplicationDatanodeStateManager
argument_list|(
name|nodeManager
argument_list|,
name|poolManager
argument_list|)
expr_stmt|;
comment|// Sleep for one second to make sure all threads get time to run.
name|sleepUninterruptibly
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * Asserts that at least one pool is picked up for processing.    */
DECL|method|testAssertPoolsAreProcessed ()
specifier|public
name|void
name|testAssertPoolsAreProcessed
parameter_list|()
block|{
comment|// This asserts that replication manager has started processing at least
comment|// one pool.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicationManager
operator|.
name|getInProgressPoolCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Since all datanodes are flagged as healthy in this test, for each
comment|// datanode we must have queued a command.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Commands are in queue :"
argument_list|,
name|commandQueue
operator|.
name|getCommandsInQueue
argument_list|()
argument_list|,
name|POOL_SIZE
operator|*
name|replicationManager
operator|.
name|getInProgressPoolCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * This test sends container reports for 2 containers to a pool in progress.    * Asserts that we are able to find a container with single replica and do    * not find container with 3 replicas.    */
DECL|method|testDetectSingleContainerReplica ()
specifier|public
name|void
name|testDetectSingleContainerReplica
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|String
name|singleNodeContainer
init|=
literal|"SingleNodeContainer"
decl_stmt|;
name|String
name|threeNodeContainer
init|=
literal|"ThreeNodeContainer"
decl_stmt|;
name|InProgressPool
name|ppool
init|=
name|replicationManager
operator|.
name|getInProcessPoolList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Only single datanode reporting that "SingleNodeContainer" exists.
name|List
argument_list|<
name|ContainerReportsProto
argument_list|>
name|clist
init|=
name|datanodeStateManager
operator|.
name|getContainerReport
argument_list|(
name|singleNodeContainer
argument_list|,
name|ppool
operator|.
name|getPool
argument_list|()
operator|.
name|getPoolName
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ppool
operator|.
name|handleContainerReport
argument_list|(
name|clist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Three nodes are going to report that ThreeNodeContainer  exists.
name|clist
operator|=
name|datanodeStateManager
operator|.
name|getContainerReport
argument_list|(
name|threeNodeContainer
argument_list|,
name|ppool
operator|.
name|getPool
argument_list|()
operator|.
name|getPoolName
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerReportsProto
name|reportsProto
range|:
name|clist
control|)
block|{
name|ppool
operator|.
name|handleContainerReport
argument_list|(
name|reportsProto
argument_list|)
expr_stmt|;
block|}
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|ppool
operator|.
name|getContainerProcessedCount
argument_list|()
operator|==
literal|4
argument_list|,
literal|200
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|ppool
operator|.
name|setDoneProcessing
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|containers
init|=
name|ppool
operator|.
name|filterContainer
argument_list|(
name|p
lambda|->
name|p
operator|.
name|getValue
argument_list|()
operator|==
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|singleNodeContainer
argument_list|,
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|count
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * We create three containers, Normal,OveReplicated and WayOverReplicated    * containers. This test asserts that we are able to find the    * over replicated containers.    */
DECL|method|testDetectOverReplica ()
specifier|public
name|void
name|testDetectOverReplica
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|String
name|normalContainer
init|=
literal|"NormalContainer"
decl_stmt|;
name|String
name|overReplicated
init|=
literal|"OverReplicatedContainer"
decl_stmt|;
name|String
name|wayOverReplicated
init|=
literal|"WayOverReplicated"
decl_stmt|;
name|InProgressPool
name|ppool
init|=
name|replicationManager
operator|.
name|getInProcessPoolList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContainerReportsProto
argument_list|>
name|clist
init|=
name|datanodeStateManager
operator|.
name|getContainerReport
argument_list|(
name|normalContainer
argument_list|,
name|ppool
operator|.
name|getPool
argument_list|()
operator|.
name|getPoolName
argument_list|()
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|ppool
operator|.
name|handleContainerReport
argument_list|(
name|clist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|clist
operator|=
name|datanodeStateManager
operator|.
name|getContainerReport
argument_list|(
name|overReplicated
argument_list|,
name|ppool
operator|.
name|getPool
argument_list|()
operator|.
name|getPoolName
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerReportsProto
name|reportsProto
range|:
name|clist
control|)
block|{
name|ppool
operator|.
name|handleContainerReport
argument_list|(
name|reportsProto
argument_list|)
expr_stmt|;
block|}
name|clist
operator|=
name|datanodeStateManager
operator|.
name|getContainerReport
argument_list|(
name|wayOverReplicated
argument_list|,
name|ppool
operator|.
name|getPool
argument_list|()
operator|.
name|getPoolName
argument_list|()
argument_list|,
literal|7
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerReportsProto
name|reportsProto
range|:
name|clist
control|)
block|{
name|ppool
operator|.
name|handleContainerReport
argument_list|(
name|reportsProto
argument_list|)
expr_stmt|;
block|}
comment|// We ignore container reports from the same datanodes.
comment|// it is possible that these each of these containers get placed
comment|// on same datanodes, so allowing for 4 duplicates in the set of 14.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|ppool
operator|.
name|getContainerProcessedCount
argument_list|()
operator|>
literal|10
argument_list|,
literal|200
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|ppool
operator|.
name|setDoneProcessing
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|containers
init|=
name|ppool
operator|.
name|filterContainer
argument_list|(
name|p
lambda|->
name|p
operator|.
name|getValue
argument_list|()
operator|>
literal|3
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|containers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * This test verifies that all pools are picked up for replica processing.    *    */
DECL|method|testAllPoolsAreProcessed ()
specifier|public
name|void
name|testAllPoolsAreProcessed
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
comment|// Verify that we saw all three pools being picked up for processing.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|replicationManager
operator|.
name|getPoolProcessCount
argument_list|()
operator|>=
literal|3
argument_list|,
literal|200
argument_list|,
literal|15
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Pool1"
argument_list|)
operator|&&
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Pool2"
argument_list|)
operator|&&
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Pool3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * Adds a new pool and tests that we are able to pick up that new pool for    * processing as well as handle container reports for datanodes in that pool.    * @throws TimeoutException    * @throws InterruptedException    */
DECL|method|testAddingNewPoolWorks ()
specifier|public
name|void
name|testAddingNewPoolWorks
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|LogCapturer
name|inProgressLog
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|InProgressPool
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|InProgressPool
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
try|try
block|{
name|DatanodeID
name|id
init|=
name|SCMTestUtils
operator|.
name|getDatanodeID
argument_list|()
decl_stmt|;
operator|(
call|(
name|ReplicationNodeManagerMock
call|)
argument_list|(
name|nodeManager
argument_list|)
operator|)
operator|.
name|addNode
argument_list|(
name|id
argument_list|,
name|NodeManager
operator|.
name|NODESTATE
operator|.
name|HEALTHY
argument_list|)
expr_stmt|;
name|poolManager
operator|.
name|addNode
argument_list|(
literal|"PoolNew"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"PoolNew"
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|15
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// Assert that we are able to send a container report to this new
comment|// pool and datanode.
name|List
argument_list|<
name|ContainerReportsProto
argument_list|>
name|clist
init|=
name|datanodeStateManager
operator|.
name|getContainerReport
argument_list|(
literal|"NewContainer1"
argument_list|,
literal|"PoolNew"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|replicationManager
operator|.
name|handleContainerReport
argument_list|(
name|clist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|inProgressLog
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NewContainer1"
argument_list|)
operator|&&
name|inProgressLog
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|id
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|10
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|inProgressLog
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

