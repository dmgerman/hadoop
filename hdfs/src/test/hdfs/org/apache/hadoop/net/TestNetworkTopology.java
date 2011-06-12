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
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|protocol
operator|.
name|DatanodeID
import|;
end_import

begin_class
DECL|class|TestNetworkTopology
specifier|public
class|class
name|TestNetworkTopology
extends|extends
name|TestCase
block|{
DECL|field|cluster
specifier|private
specifier|final
specifier|static
name|NetworkTopology
name|cluster
init|=
operator|new
name|NetworkTopology
argument_list|()
decl_stmt|;
DECL|field|dataNodes
specifier|private
specifier|final
specifier|static
name|DatanodeDescriptor
name|dataNodes
index|[]
init|=
operator|new
name|DatanodeDescriptor
index|[]
block|{
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"h1:5020"
argument_list|)
argument_list|,
literal|"/d1/r1"
argument_list|)
block|,
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"h2:5020"
argument_list|)
argument_list|,
literal|"/d1/r1"
argument_list|)
block|,
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"h3:5020"
argument_list|)
argument_list|,
literal|"/d1/r2"
argument_list|)
block|,
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"h4:5020"
argument_list|)
argument_list|,
literal|"/d1/r2"
argument_list|)
block|,
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"h5:5020"
argument_list|)
argument_list|,
literal|"/d1/r2"
argument_list|)
block|,
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"h6:5020"
argument_list|)
argument_list|,
literal|"/d2/r3"
argument_list|)
block|,
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"h7:5020"
argument_list|)
argument_list|,
literal|"/d2/r3"
argument_list|)
block|}
decl_stmt|;
DECL|field|NODE
specifier|private
specifier|final
specifier|static
name|DatanodeDescriptor
name|NODE
init|=
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"h8:5020"
argument_list|)
argument_list|,
literal|"/d2/r4"
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
DECL|method|testContains ()
specifier|public
name|void
name|testContains
parameter_list|()
throws|throws
name|Exception
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
name|assertTrue
argument_list|(
name|cluster
operator|.
name|contains
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|cluster
operator|.
name|contains
argument_list|(
name|NODE
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|cluster
operator|.
name|getNumOfLeaves
argument_list|()
argument_list|,
name|dataNodes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
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
name|cluster
operator|.
name|getNumOfRacks
argument_list|()
argument_list|,
literal|3
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
name|assertFalse
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
name|assertTrue
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
name|assertFalse
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
name|assertTrue
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
block|}
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
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
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
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
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
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
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
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
DECL|method|testPseudoSortByDistance ()
specifier|public
name|void
name|testPseudoSortByDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|DatanodeDescriptor
index|[]
name|testNodes
init|=
operator|new
name|DatanodeDescriptor
index|[
literal|3
index|]
decl_stmt|;
comment|// array contains both local node& local rack node
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
literal|0
index|]
expr_stmt|;
name|cluster
operator|.
name|pseudoSortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
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
comment|// array contains local node
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
literal|0
index|]
expr_stmt|;
name|cluster
operator|.
name|pseudoSortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
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
literal|3
index|]
argument_list|)
expr_stmt|;
comment|// array contains local rack node
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
literal|1
index|]
expr_stmt|;
name|cluster
operator|.
name|pseudoSortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
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
literal|1
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
literal|3
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
literal|5
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemove ()
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
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
name|remove
argument_list|(
name|dataNodes
index|[
name|i
index|]
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
name|dataNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|cluster
operator|.
name|contains
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cluster
operator|.
name|getNumOfLeaves
argument_list|()
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
name|DatanodeDescriptor
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
comment|/**    * This test checks that chooseRandom works for an excluded rack.    */
DECL|method|testChooseRandomExcludedRack ()
specifier|public
name|void
name|testChooseRandomExcludedRack
parameter_list|()
block|{
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
literal|"~"
operator|+
literal|"/d2"
argument_list|)
decl_stmt|;
comment|// all the nodes on the second rack should be zero
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|dataNodes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|freq
init|=
name|frequency
operator|.
name|get
argument_list|(
name|dataNodes
index|[
name|j
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataNodes
index|[
name|j
index|]
operator|.
name|getNetworkLocation
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"/d2"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|freq
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

