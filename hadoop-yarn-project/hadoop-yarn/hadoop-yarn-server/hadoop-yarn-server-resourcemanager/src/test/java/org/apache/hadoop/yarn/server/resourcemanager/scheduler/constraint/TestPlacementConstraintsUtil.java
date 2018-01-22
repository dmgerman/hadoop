begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|constraint
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
name|yarn
operator|.
name|api
operator|.
name|resource
operator|.
name|PlacementConstraints
operator|.
name|NODE
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
name|yarn
operator|.
name|api
operator|.
name|resource
operator|.
name|PlacementConstraints
operator|.
name|RACK
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
name|yarn
operator|.
name|api
operator|.
name|resource
operator|.
name|PlacementConstraints
operator|.
name|targetIn
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
name|yarn
operator|.
name|api
operator|.
name|resource
operator|.
name|PlacementConstraints
operator|.
name|targetNotIn
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
name|yarn
operator|.
name|api
operator|.
name|resource
operator|.
name|PlacementConstraints
operator|.
name|PlacementTargets
operator|.
name|allocationTag
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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
name|atomic
operator|.
name|AtomicLong
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
name|yarn
operator|.
name|api
operator|.
name|resource
operator|.
name|PlacementConstraint
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
name|yarn
operator|.
name|api
operator|.
name|resource
operator|.
name|PlacementConstraints
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|SchedulingRequest
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Priority
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ExecutionTypeRequest
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ResourceSizing
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|MockNodes
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|MockRM
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|TestUtils
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|common
operator|.
name|fica
operator|.
name|FiCaSchedulerNode
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
name|yarn
operator|.
name|server
operator|.
name|utils
operator|.
name|BuilderUtils
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_comment
comment|/**  * Test the PlacementConstraint Utility class functionality.  */
end_comment

begin_class
DECL|class|TestPlacementConstraintsUtil
specifier|public
class|class
name|TestPlacementConstraintsUtil
block|{
DECL|field|rmNodes
specifier|private
name|List
argument_list|<
name|RMNode
argument_list|>
name|rmNodes
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|GB
specifier|private
specifier|static
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
DECL|field|appId1
specifier|private
name|ApplicationId
name|appId1
decl_stmt|;
DECL|field|c1
DECL|field|c2
DECL|field|c3
DECL|field|c4
specifier|private
name|PlacementConstraint
name|c1
decl_stmt|,
name|c2
decl_stmt|,
name|c3
decl_stmt|,
name|c4
decl_stmt|;
DECL|field|sourceTag1
DECL|field|sourceTag2
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|sourceTag1
decl_stmt|,
name|sourceTag2
decl_stmt|;
DECL|field|constraintMap1
DECL|field|constraintMap2
specifier|private
name|Map
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|,
name|PlacementConstraint
argument_list|>
name|constraintMap1
decl_stmt|,
name|constraintMap2
decl_stmt|;
DECL|field|requestID
specifier|private
name|AtomicLong
name|requestID
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|()
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNodes
operator|.
name|resetHostIds
argument_list|()
expr_stmt|;
name|rmNodes
operator|=
name|MockNodes
operator|.
name|newNodes
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|4096
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|RMNode
name|rmNode
range|:
name|rmNodes
control|)
block|{
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|putIfAbsent
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|rmNode
argument_list|)
expr_stmt|;
block|}
name|rmContext
operator|=
name|rm
operator|.
name|getRMContext
argument_list|()
expr_stmt|;
comment|// Build appIDs, constraints, source tags, and constraint map.
name|long
name|ts
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|appId1
operator|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
name|ts
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|c1
operator|=
name|PlacementConstraints
operator|.
name|build
argument_list|(
name|targetIn
argument_list|(
name|NODE
argument_list|,
name|allocationTag
argument_list|(
literal|"hbase-m"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|c2
operator|=
name|PlacementConstraints
operator|.
name|build
argument_list|(
name|targetIn
argument_list|(
name|RACK
argument_list|,
name|allocationTag
argument_list|(
literal|"hbase-rs"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|c3
operator|=
name|PlacementConstraints
operator|.
name|build
argument_list|(
name|targetNotIn
argument_list|(
name|NODE
argument_list|,
name|allocationTag
argument_list|(
literal|"hbase-m"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|c4
operator|=
name|PlacementConstraints
operator|.
name|build
argument_list|(
name|targetNotIn
argument_list|(
name|RACK
argument_list|,
name|allocationTag
argument_list|(
literal|"hbase-rs"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sourceTag1
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"spark"
argument_list|)
argument_list|)
expr_stmt|;
name|sourceTag2
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"zk"
argument_list|)
argument_list|)
expr_stmt|;
name|constraintMap1
operator|=
name|Stream
operator|.
name|of
argument_list|(
operator|new
name|AbstractMap
operator|.
name|SimpleEntry
argument_list|<>
argument_list|(
name|sourceTag1
argument_list|,
name|c1
argument_list|)
argument_list|,
operator|new
name|AbstractMap
operator|.
name|SimpleEntry
argument_list|<>
argument_list|(
name|sourceTag2
argument_list|,
name|c2
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|AbstractMap
operator|.
name|SimpleEntry
operator|::
name|getKey
argument_list|,
name|AbstractMap
operator|.
name|SimpleEntry
operator|::
name|getValue
argument_list|)
argument_list|)
expr_stmt|;
name|constraintMap2
operator|=
name|Stream
operator|.
name|of
argument_list|(
operator|new
name|AbstractMap
operator|.
name|SimpleEntry
argument_list|<>
argument_list|(
name|sourceTag1
argument_list|,
name|c3
argument_list|)
argument_list|,
operator|new
name|AbstractMap
operator|.
name|SimpleEntry
argument_list|<>
argument_list|(
name|sourceTag2
argument_list|,
name|c4
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|AbstractMap
operator|.
name|SimpleEntry
operator|::
name|getKey
argument_list|,
name|AbstractMap
operator|.
name|SimpleEntry
operator|::
name|getValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createSchedulingRequest (Set<String> allocationTags, PlacementConstraint constraint)
specifier|private
name|SchedulingRequest
name|createSchedulingRequest
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
parameter_list|,
name|PlacementConstraint
name|constraint
parameter_list|)
block|{
return|return
name|SchedulingRequest
operator|.
name|newInstance
argument_list|(
name|requestID
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ExecutionTypeRequest
operator|.
name|newInstance
argument_list|()
argument_list|,
name|allocationTags
argument_list|,
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|,
name|constraint
argument_list|)
return|;
block|}
DECL|method|createSchedulingRequest (Set<String> allocationTags)
specifier|private
name|SchedulingRequest
name|createSchedulingRequest
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
parameter_list|)
block|{
return|return
name|createSchedulingRequest
argument_list|(
name|allocationTags
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testNodeAffinityAssignment ()
specifier|public
name|void
name|testNodeAffinityAssignment
parameter_list|()
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|PlacementConstraintManagerService
name|pcm
init|=
operator|new
name|MemoryPlacementConstraintManager
argument_list|()
decl_stmt|;
name|AllocationTagsManager
name|tm
init|=
operator|new
name|AllocationTagsManager
argument_list|(
name|rmContext
argument_list|)
decl_stmt|;
comment|// Register App1 with affinity constraint map
name|pcm
operator|.
name|registerApplication
argument_list|(
name|appId1
argument_list|,
name|constraintMap1
argument_list|)
expr_stmt|;
comment|// No containers are running so all 'zk' and 'spark' allocations should fail
comment|// on every cluster NODE
name|Iterator
argument_list|<
name|RMNode
argument_list|>
name|nodeIterator
init|=
name|rmNodes
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|nodeIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RMNode
name|currentNode
init|=
name|nodeIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|currentNode
operator|.
name|getHostName
argument_list|()
argument_list|,
name|currentNode
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag1
argument_list|)
argument_list|,
name|schedulerNode
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag2
argument_list|)
argument_list|,
name|schedulerNode
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Now place container:      * Node0:123 (Rack1):      *    container_app1_1 (hbase-m)      */
name|RMNode
name|n0_r1
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RMNode
name|n1_r1
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RMNode
name|n2_r2
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|RMNode
name|n3_r2
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode0
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n0_r1
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n0_r1
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode1
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n1_r1
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n1_r1
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode2
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n2_r2
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n2_r2
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode3
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n3_r2
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n3_r2
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
comment|// 1 Containers on node 0 with allocationTag 'hbase-m'
name|ContainerId
name|hbase_m
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId1
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|tm
operator|.
name|addContainer
argument_list|(
name|n0_r1
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|hbase_m
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"hbase-m"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'spark' placement on Node0 should now SUCCEED
name|Assert
operator|.
name|assertTrue
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag1
argument_list|)
argument_list|,
name|schedulerNode0
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
comment|// FAIL on the rest of the nodes
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag1
argument_list|)
argument_list|,
name|schedulerNode1
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag1
argument_list|)
argument_list|,
name|schedulerNode2
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag1
argument_list|)
argument_list|,
name|schedulerNode3
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRackAffinityAssignment ()
specifier|public
name|void
name|testRackAffinityAssignment
parameter_list|()
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|PlacementConstraintManagerService
name|pcm
init|=
operator|new
name|MemoryPlacementConstraintManager
argument_list|()
decl_stmt|;
name|AllocationTagsManager
name|tm
init|=
operator|new
name|AllocationTagsManager
argument_list|(
name|rmContext
argument_list|)
decl_stmt|;
comment|// Register App1 with affinity constraint map
name|pcm
operator|.
name|registerApplication
argument_list|(
name|appId1
argument_list|,
name|constraintMap1
argument_list|)
expr_stmt|;
comment|/**      * Now place container:      * Node0:123 (Rack1):      *    container_app1_1 (hbase-rs)      */
name|RMNode
name|n0_r1
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RMNode
name|n1_r1
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RMNode
name|n2_r2
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|RMNode
name|n3_r2
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
comment|// 1 Containers on Node0-Rack1 with allocationTag 'hbase-rs'
name|ContainerId
name|hbase_m
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId1
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|tm
operator|.
name|addContainer
argument_list|(
name|n0_r1
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|hbase_m
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"hbase-rs"
argument_list|)
argument_list|)
expr_stmt|;
name|FiCaSchedulerNode
name|schedulerNode0
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n0_r1
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n0_r1
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode1
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n1_r1
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n1_r1
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode2
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n2_r2
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n2_r2
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode3
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n3_r2
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n3_r2
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
comment|// 'zk' placement on Rack1 should now SUCCEED
name|Assert
operator|.
name|assertTrue
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag2
argument_list|)
argument_list|,
name|schedulerNode0
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag2
argument_list|)
argument_list|,
name|schedulerNode1
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
comment|// FAIL on the rest of the RACKs
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag2
argument_list|)
argument_list|,
name|schedulerNode2
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag2
argument_list|)
argument_list|,
name|schedulerNode3
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeAntiAffinityAssignment ()
specifier|public
name|void
name|testNodeAntiAffinityAssignment
parameter_list|()
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|PlacementConstraintManagerService
name|pcm
init|=
operator|new
name|MemoryPlacementConstraintManager
argument_list|()
decl_stmt|;
name|AllocationTagsManager
name|tm
init|=
operator|new
name|AllocationTagsManager
argument_list|(
name|rmContext
argument_list|)
decl_stmt|;
comment|// Register App1 with anti-affinity constraint map
name|pcm
operator|.
name|registerApplication
argument_list|(
name|appId1
argument_list|,
name|constraintMap2
argument_list|)
expr_stmt|;
comment|/**      * place container:      * Node0:123 (Rack1):      *    container_app1_1 (hbase-m)      */
name|RMNode
name|n0_r1
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RMNode
name|n1_r1
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RMNode
name|n2_r2
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|RMNode
name|n3_r2
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode0
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n0_r1
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n0_r1
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode1
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n1_r1
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n1_r1
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode2
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n2_r2
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n2_r2
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode3
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n3_r2
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n3_r2
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
comment|// 1 Containers on node 0 with allocationTag 'hbase-m'
name|ContainerId
name|hbase_m
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId1
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|tm
operator|.
name|addContainer
argument_list|(
name|n0_r1
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|hbase_m
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"hbase-m"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'spark' placement on Node0 should now FAIL
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag1
argument_list|)
argument_list|,
name|schedulerNode0
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
comment|// SUCCEED on the rest of the nodes
name|Assert
operator|.
name|assertTrue
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag1
argument_list|)
argument_list|,
name|schedulerNode1
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag1
argument_list|)
argument_list|,
name|schedulerNode2
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag1
argument_list|)
argument_list|,
name|schedulerNode3
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRackAntiAffinityAssignment ()
specifier|public
name|void
name|testRackAntiAffinityAssignment
parameter_list|()
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|AllocationTagsManager
name|tm
init|=
operator|new
name|AllocationTagsManager
argument_list|(
name|rmContext
argument_list|)
decl_stmt|;
name|PlacementConstraintManagerService
name|pcm
init|=
operator|new
name|MemoryPlacementConstraintManager
argument_list|()
decl_stmt|;
comment|// Register App1 with anti-affinity constraint map
name|pcm
operator|.
name|registerApplication
argument_list|(
name|appId1
argument_list|,
name|constraintMap2
argument_list|)
expr_stmt|;
comment|/**      * Place container:      * Node0:123 (Rack1):      *    container_app1_1 (hbase-rs)      */
name|RMNode
name|n0_r1
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RMNode
name|n1_r1
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RMNode
name|n2_r2
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|RMNode
name|n3_r2
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
comment|// 1 Containers on Node0-Rack1 with allocationTag 'hbase-rs'
name|ContainerId
name|hbase_m
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId1
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|tm
operator|.
name|addContainer
argument_list|(
name|n0_r1
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|hbase_m
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"hbase-rs"
argument_list|)
argument_list|)
expr_stmt|;
name|FiCaSchedulerNode
name|schedulerNode0
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n0_r1
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n0_r1
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode1
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n1_r1
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n1_r1
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode2
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n2_r2
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n2_r2
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|FiCaSchedulerNode
name|schedulerNode3
init|=
name|TestUtils
operator|.
name|getMockNode
argument_list|(
name|n3_r2
operator|.
name|getHostName
argument_list|()
argument_list|,
name|n3_r2
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|123
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
comment|// 'zk' placement on Rack1 should FAIL
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag2
argument_list|)
argument_list|,
name|schedulerNode0
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag2
argument_list|)
argument_list|,
name|schedulerNode1
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
comment|// SUCCEED on the rest of the RACKs
name|Assert
operator|.
name|assertTrue
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag2
argument_list|)
argument_list|,
name|schedulerNode2
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId1
argument_list|,
name|createSchedulingRequest
argument_list|(
name|sourceTag2
argument_list|)
argument_list|,
name|schedulerNode3
argument_list|,
name|pcm
argument_list|,
name|tm
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

