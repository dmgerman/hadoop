begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
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
name|assertFalse
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|Map
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

begin_class
DECL|class|TestNetworkTopologyWithNodeGroup
specifier|public
class|class
name|TestNetworkTopologyWithNodeGroup
block|{
DECL|field|cluster
specifier|private
specifier|final
specifier|static
name|NetworkTopologyWithNodeGroup
name|cluster
init|=
operator|new
name|NetworkTopologyWithNodeGroup
argument_list|()
decl_stmt|;
DECL|field|dataNodes
specifier|private
specifier|final
specifier|static
name|NodeBase
name|dataNodes
index|[]
init|=
operator|new
name|NodeBase
index|[]
block|{
operator|new
name|NodeBase
argument_list|(
literal|"h1"
argument_list|,
literal|"/d1/r1/s1"
argument_list|)
block|,
operator|new
name|NodeBase
argument_list|(
literal|"h2"
argument_list|,
literal|"/d1/r1/s1"
argument_list|)
block|,
operator|new
name|NodeBase
argument_list|(
literal|"h3"
argument_list|,
literal|"/d1/r1/s2"
argument_list|)
block|,
operator|new
name|NodeBase
argument_list|(
literal|"h4"
argument_list|,
literal|"/d1/r2/s3"
argument_list|)
block|,
operator|new
name|NodeBase
argument_list|(
literal|"h5"
argument_list|,
literal|"/d1/r2/s3"
argument_list|)
block|,
operator|new
name|NodeBase
argument_list|(
literal|"h6"
argument_list|,
literal|"/d1/r2/s4"
argument_list|)
block|,
operator|new
name|NodeBase
argument_list|(
literal|"h7"
argument_list|,
literal|"/d2/r3/s5"
argument_list|)
block|,
operator|new
name|NodeBase
argument_list|(
literal|"h8"
argument_list|,
literal|"/d2/r3/s6"
argument_list|)
block|}
decl_stmt|;
DECL|field|computeNode
specifier|private
specifier|final
specifier|static
name|NodeBase
name|computeNode
init|=
operator|new
name|NodeBase
argument_list|(
literal|"/d1/r1/s1/h9"
argument_list|)
decl_stmt|;
DECL|field|rackOnlyNode
specifier|private
specifier|final
specifier|static
name|NodeBase
name|rackOnlyNode
init|=
operator|new
name|NodeBase
argument_list|(
literal|"h10"
argument_list|,
literal|"/r2"
argument_list|)
decl_stmt|;
static|static
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
name|dataNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|add
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNumOfChildren ()
specifier|public
name|void
name|testNumOfChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|dataNodes
operator|.
name|length
argument_list|,
name|cluster
operator|.
name|getNumOfLeaves
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNumOfRacks ()
specifier|public
name|void
name|testNumOfRacks
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cluster
operator|.
name|getNumOfRacks
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRacks ()
specifier|public
name|void
name|testRacks
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cluster
operator|.
name|getNumOfRacks
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|1
index|]
argument_list|,
name|dataNodes
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|2
index|]
argument_list|,
name|dataNodes
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|3
index|]
argument_list|,
name|dataNodes
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|4
index|]
argument_list|,
name|dataNodes
index|[
literal|5
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|5
index|]
argument_list|,
name|dataNodes
index|[
literal|6
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|6
index|]
argument_list|,
name|dataNodes
index|[
literal|7
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeGroups ()
specifier|public
name|void
name|testNodeGroups
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cluster
operator|.
name|getNumOfRacks
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameNodeGroup
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isOnSameNodeGroup
argument_list|(
name|dataNodes
index|[
literal|1
index|]
argument_list|,
name|dataNodes
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isOnSameNodeGroup
argument_list|(
name|dataNodes
index|[
literal|2
index|]
argument_list|,
name|dataNodes
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameNodeGroup
argument_list|(
name|dataNodes
index|[
literal|3
index|]
argument_list|,
name|dataNodes
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isOnSameNodeGroup
argument_list|(
name|dataNodes
index|[
literal|4
index|]
argument_list|,
name|dataNodes
index|[
literal|5
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isOnSameNodeGroup
argument_list|(
name|dataNodes
index|[
literal|5
index|]
argument_list|,
name|dataNodes
index|[
literal|6
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isOnSameNodeGroup
argument_list|(
name|dataNodes
index|[
literal|6
index|]
argument_list|,
name|dataNodes
index|[
literal|7
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetDistance ()
specifier|public
name|void
name|testGetDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cluster
operator|.
name|getDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cluster
operator|.
name|getDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|cluster
operator|.
name|getDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|cluster
operator|.
name|getDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|cluster
operator|.
name|getDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|6
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortByDistance ()
specifier|public
name|void
name|testSortByDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBase
index|[]
name|testNodes
init|=
operator|new
name|NodeBase
index|[
literal|4
index|]
decl_stmt|;
comment|// array contains both local node, local node group& local rack node
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|1
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|2
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|3
index|]
expr_stmt|;
name|testNodes
index|[
literal|3
index|]
operator|=
name|dataNodes
index|[
literal|0
index|]
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|2
index|]
operator|==
name|dataNodes
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|3
index|]
operator|==
name|dataNodes
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
comment|// array contains local node& local node group
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|3
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|4
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|1
index|]
expr_stmt|;
name|testNodes
index|[
literal|3
index|]
operator|=
name|dataNodes
index|[
literal|0
index|]
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// array contains local node& rack node
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|5
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|3
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|2
index|]
expr_stmt|;
name|testNodes
index|[
literal|3
index|]
operator|=
name|dataNodes
index|[
literal|0
index|]
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// array contains local-nodegroup node (not a data node also)& rack node
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|6
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|7
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|2
index|]
expr_stmt|;
name|testNodes
index|[
literal|3
index|]
operator|=
name|dataNodes
index|[
literal|0
index|]
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|computeNode
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**    * This picks a large number of nodes at random in order to ensure coverage    *     * @param numNodes the number of nodes    * @param excludedScope the excluded scope    * @return the frequency that nodes were chosen    */
DECL|method|pickNodesAtRandom (int numNodes, String excludedScope)
specifier|private
name|Map
argument_list|<
name|Node
argument_list|,
name|Integer
argument_list|>
name|pickNodesAtRandom
parameter_list|(
name|int
name|numNodes
parameter_list|,
name|String
name|excludedScope
parameter_list|)
block|{
name|Map
argument_list|<
name|Node
argument_list|,
name|Integer
argument_list|>
name|frequency
init|=
operator|new
name|HashMap
argument_list|<
name|Node
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeBase
name|dnd
range|:
name|dataNodes
control|)
block|{
name|frequency
operator|.
name|put
argument_list|(
name|dnd
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numNodes
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|random
init|=
name|cluster
operator|.
name|chooseRandom
argument_list|(
name|excludedScope
argument_list|)
decl_stmt|;
name|frequency
operator|.
name|put
argument_list|(
name|random
argument_list|,
name|frequency
operator|.
name|get
argument_list|(
name|random
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|frequency
return|;
block|}
comment|/**    * This test checks that chooseRandom works for an excluded node.    */
comment|/**    * Test replica placement policy in case last node is invalid.    * We create 6 nodes but the last node is in fault topology (with rack info),    * so cannot be added to cluster. We should test proper exception is thrown in     * adding node but shouldn't affect the cluster.    */
annotation|@
name|Test
DECL|method|testChooseRandomExcludedNode ()
specifier|public
name|void
name|testChooseRandomExcludedNode
parameter_list|()
block|{
name|String
name|scope
init|=
literal|"~"
operator|+
name|NodeBase
operator|.
name|getPath
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Node
argument_list|,
name|Integer
argument_list|>
name|frequency
init|=
name|pickNodesAtRandom
argument_list|(
literal|100
argument_list|,
name|scope
argument_list|)
decl_stmt|;
for|for
control|(
name|Node
name|key
range|:
name|dataNodes
control|)
block|{
comment|// all nodes except the first should be more than zero
name|assertTrue
argument_list|(
name|frequency
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|>
literal|0
operator|||
name|key
operator|==
name|dataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNodeGroup ()
specifier|public
name|void
name|testNodeGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|res
init|=
name|cluster
operator|.
name|getNodeGroup
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"NodeGroup should be NodeBase.ROOT for empty location"
argument_list|,
name|res
operator|.
name|equals
argument_list|(
name|NodeBase
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|getNodeGroup
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Null Network Location should throw exception!"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Network Location is null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This test checks that adding a node with invalid topology will be failed     * with an exception to show topology is invalid.    */
annotation|@
name|Test
DECL|method|testAddNodeWithInvalidTopology ()
specifier|public
name|void
name|testAddNodeWithInvalidTopology
parameter_list|()
block|{
comment|// The last node is a node with invalid topology
try|try
block|{
name|cluster
operator|.
name|add
argument_list|(
name|rackOnlyNode
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception should be thrown, so we should not have reached here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|e
operator|instanceof
name|IllegalArgumentException
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"Expecting IllegalArgumentException, but caught:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal network location"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

