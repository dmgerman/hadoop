begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
package|;
end_package

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
name|HashMap
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
name|Map
operator|.
name|Entry
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
name|NodeId
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
name|NodeLabel
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Sets
import|;
end_import

begin_class
DECL|class|NodeLabelTestBase
specifier|public
class|class
name|NodeLabelTestBase
block|{
DECL|method|assertMapEquals (Map<NodeId, Set<String>> m1, ImmutableMap<NodeId, Set<String>> m2)
specifier|public
specifier|static
name|void
name|assertMapEquals
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|m1
parameter_list|,
name|ImmutableMap
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|m2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|m1
operator|.
name|size
argument_list|()
argument_list|,
name|m2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeId
name|k
range|:
name|m1
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|m2
operator|.
name|containsKey
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|assertCollectionEquals
argument_list|(
name|m1
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|,
name|m2
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertLabelInfoMapEquals (Map<NodeId, Set<NodeLabel>> m1, ImmutableMap<NodeId, Set<NodeLabel>> m2)
specifier|public
specifier|static
name|void
name|assertLabelInfoMapEquals
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|NodeLabel
argument_list|>
argument_list|>
name|m1
parameter_list|,
name|ImmutableMap
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|NodeLabel
argument_list|>
argument_list|>
name|m2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|m1
operator|.
name|size
argument_list|()
argument_list|,
name|m2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeId
name|k
range|:
name|m1
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|m2
operator|.
name|containsKey
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|assertNLCollectionEquals
argument_list|(
name|m1
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|,
name|m2
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertLabelsToNodesEquals (Map<String, Set<NodeId>> m1, ImmutableMap<String, Set<NodeId>> m2)
specifier|public
specifier|static
name|void
name|assertLabelsToNodesEquals
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|m1
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|m2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|m1
operator|.
name|size
argument_list|()
argument_list|,
name|m2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|k
range|:
name|m1
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|m2
operator|.
name|containsKey
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|NodeId
argument_list|>
name|s1
init|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|(
name|m1
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|NodeId
argument_list|>
name|s2
init|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|(
name|m2
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s1
operator|.
name|containsAll
argument_list|(
name|s2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|transposeNodeToLabels ( Map<NodeId, Set<String>> mapNodeToLabels)
specifier|public
specifier|static
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|transposeNodeToLabels
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|mapNodeToLabels
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|mapLabelsToNodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|mapNodeToLabels
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NodeId
name|node
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|setLabels
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|label
range|:
name|setLabels
control|)
block|{
name|Set
argument_list|<
name|NodeId
argument_list|>
name|setNode
init|=
name|mapLabelsToNodes
operator|.
name|get
argument_list|(
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|setNode
operator|==
literal|null
condition|)
block|{
name|setNode
operator|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|setNode
operator|.
name|add
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
name|node
operator|.
name|getHost
argument_list|()
argument_list|,
name|node
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mapLabelsToNodes
operator|.
name|put
argument_list|(
name|label
argument_list|,
name|setNode
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|mapLabelsToNodes
argument_list|)
return|;
block|}
DECL|method|assertMapContains (Map<NodeId, Set<String>> m1, ImmutableMap<NodeId, Set<String>> m2)
specifier|public
specifier|static
name|void
name|assertMapContains
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|m1
parameter_list|,
name|ImmutableMap
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|m2
parameter_list|)
block|{
for|for
control|(
name|NodeId
name|k
range|:
name|m2
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|m1
operator|.
name|containsKey
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|assertCollectionEquals
argument_list|(
name|m1
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|,
name|m2
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertCollectionEquals (Collection<String> c1, Collection<String> c2)
specifier|public
specifier|static
name|void
name|assertCollectionEquals
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|c1
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|c2
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|s1
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|c1
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|s2
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|c2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s1
operator|.
name|containsAll
argument_list|(
name|s2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNLCollectionEquals (Collection<NodeLabel> c1, Collection<NodeLabel> c2)
specifier|public
specifier|static
name|void
name|assertNLCollectionEquals
parameter_list|(
name|Collection
argument_list|<
name|NodeLabel
argument_list|>
name|c1
parameter_list|,
name|Collection
argument_list|<
name|NodeLabel
argument_list|>
name|c2
parameter_list|)
block|{
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|s1
init|=
operator|new
name|HashSet
argument_list|<
name|NodeLabel
argument_list|>
argument_list|(
name|c1
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|s2
init|=
operator|new
name|HashSet
argument_list|<
name|NodeLabel
argument_list|>
argument_list|(
name|c2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s1
operator|.
name|containsAll
argument_list|(
name|s2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toSet (E... elements)
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|toSet
parameter_list|(
name|E
modifier|...
name|elements
parameter_list|)
block|{
name|Set
argument_list|<
name|E
argument_list|>
name|set
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|elements
argument_list|)
decl_stmt|;
return|return
name|set
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toNodeLabelSet (String... nodeLabelsStr)
specifier|public
specifier|static
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|toNodeLabelSet
parameter_list|(
name|String
modifier|...
name|nodeLabelsStr
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|nodeLabelsStr
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|labels
init|=
operator|new
name|HashSet
argument_list|<
name|NodeLabel
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|label
range|:
name|nodeLabelsStr
control|)
block|{
name|labels
operator|.
name|add
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
name|label
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|labels
return|;
block|}
DECL|method|toNodeId (String str)
specifier|public
name|NodeId
name|toNodeId
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|.
name|contains
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
name|int
name|idx
init|=
name|str
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|NodeId
name|id
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|id
return|;
block|}
else|else
block|{
return|return
name|NodeId
operator|.
name|newInstance
argument_list|(
name|str
argument_list|,
name|CommonNodeLabelsManager
operator|.
name|WILDCARD_PORT
argument_list|)
return|;
block|}
block|}
DECL|method|assertLabelsInfoToNodesEquals ( Map<NodeLabel, Set<NodeId>> m1, ImmutableMap<NodeLabel, Set<NodeId>> m2)
specifier|public
specifier|static
name|void
name|assertLabelsInfoToNodesEquals
parameter_list|(
name|Map
argument_list|<
name|NodeLabel
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|m1
parameter_list|,
name|ImmutableMap
argument_list|<
name|NodeLabel
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|m2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|m1
operator|.
name|size
argument_list|()
argument_list|,
name|m2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeLabel
name|k
range|:
name|m1
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|m2
operator|.
name|containsKey
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|NodeId
argument_list|>
name|s1
init|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|(
name|m1
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|NodeId
argument_list|>
name|s2
init|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|(
name|m2
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s1
operator|.
name|containsAll
argument_list|(
name|s2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

