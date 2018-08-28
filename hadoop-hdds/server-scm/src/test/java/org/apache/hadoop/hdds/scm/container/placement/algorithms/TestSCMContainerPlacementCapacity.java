begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.placement.algorithms
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|algorithms
package|;
end_package

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
name|hdds
operator|.
name|conf
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|NodeState
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
name|hdds
operator|.
name|scm
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
operator|.
name|SCMNodeMetric
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
name|hdds
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|hdds
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|TestSCMContainerPlacementCapacity
specifier|public
class|class
name|TestSCMContainerPlacementCapacity
block|{
annotation|@
name|Test
DECL|method|chooseDatanodes ()
specifier|public
name|void
name|chooseDatanodes
parameter_list|()
throws|throws
name|SCMException
block|{
comment|//given
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
operator|new
name|ArrayList
argument_list|<>
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
literal|7
condition|;
name|i
operator|++
control|)
block|{
name|datanodes
operator|.
name|add
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|NodeManager
name|mockNodeManager
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NodeManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockNodeManager
operator|.
name|getNodes
argument_list|(
name|NodeState
operator|.
name|HEALTHY
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|datanodes
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockNodeManager
operator|.
name|getNodeStat
argument_list|(
name|anyObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|SCMNodeMetric
argument_list|(
literal|100L
argument_list|,
literal|0L
argument_list|,
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockNodeManager
operator|.
name|getNodeStat
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|SCMNodeMetric
argument_list|(
literal|100L
argument_list|,
literal|90L
argument_list|,
literal|10L
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockNodeManager
operator|.
name|getNodeStat
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|SCMNodeMetric
argument_list|(
literal|100L
argument_list|,
literal|80L
argument_list|,
literal|20L
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockNodeManager
operator|.
name|getNodeStat
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|SCMNodeMetric
argument_list|(
literal|100L
argument_list|,
literal|70L
argument_list|,
literal|30L
argument_list|)
argument_list|)
expr_stmt|;
name|SCMContainerPlacementCapacity
name|scmContainerPlacementRandom
init|=
operator|new
name|SCMContainerPlacementCapacity
argument_list|(
name|mockNodeManager
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|existingNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|existingNodes
operator|.
name|add
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|existingNodes
operator|.
name|add
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|DatanodeDetails
argument_list|,
name|Integer
argument_list|>
name|selectedCount
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeDetails
name|datanode
range|:
name|datanodes
control|)
block|{
name|selectedCount
operator|.
name|put
argument_list|(
name|datanode
argument_list|,
literal|0
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
comment|//when
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodeDetails
init|=
name|scmContainerPlacementRandom
operator|.
name|chooseDatanodes
argument_list|(
name|existingNodes
argument_list|,
literal|1
argument_list|,
literal|15
argument_list|)
decl_stmt|;
comment|//then
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|datanodeDetails
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|datanode0Details
init|=
name|datanodeDetails
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"Datanode 0 should not been selected: excluded by parameter"
argument_list|,
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|datanode0Details
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"Datanode 1 should not been selected: excluded by parameter"
argument_list|,
name|datanodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|datanode0Details
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"Datanode 2 should not been selected: not enough space there"
argument_list|,
name|datanodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|datanode0Details
argument_list|)
expr_stmt|;
name|selectedCount
operator|.
name|put
argument_list|(
name|datanode0Details
argument_list|,
name|selectedCount
operator|.
name|get
argument_list|(
name|datanode0Details
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|//datanode 4 has less space. Should be selected less times.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|selectedCount
operator|.
name|get
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|>
name|selectedCount
operator|.
name|get
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|selectedCount
operator|.
name|get
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|>
name|selectedCount
operator|.
name|get
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

