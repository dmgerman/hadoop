begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.nodelabels
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
name|nodelabels
package|;
end_package

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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeAttribute
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
name|NodeAttributeType
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
name|conf
operator|.
name|YarnConfiguration
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
name|nodelabels
operator|.
name|AttributeValue
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
name|nodelabels
operator|.
name|NodeAttributeStore
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
name|nodelabels
operator|.
name|NodeAttributesManager
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
name|nodelabels
operator|.
name|NodeLabelUtil
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
name|java
operator|.
name|io
operator|.
name|File
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

begin_comment
comment|/**  * Unit tests for node attribute manager.  */
end_comment

begin_class
DECL|class|TestNodeAttributesManager
specifier|public
class|class
name|TestNodeAttributesManager
block|{
DECL|field|attributesManager
specifier|private
name|NodeAttributesManager
name|attributesManager
decl_stmt|;
DECL|field|PREFIXES
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|PREFIXES
init|=
operator|new
name|String
index|[]
block|{
literal|"yarn.test1.io"
block|,
literal|"yarn.test2.io"
block|,
literal|"yarn.test3.io"
block|}
decl_stmt|;
DECL|field|HOSTNAMES
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|HOSTNAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"host1"
block|,
literal|"host2"
block|,
literal|"host3"
block|}
decl_stmt|;
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|attributesManager
operator|=
operator|new
name|NodeAttributesManagerImpl
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|FS_NODE_ATTRIBUTE_STORE_IMPL_CLASS
argument_list|,
name|FileSystemNodeAttributeStore
operator|.
name|class
argument_list|,
name|NodeAttributeStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|File
name|tempDir
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"nattr"
argument_list|,
literal|".tmp"
argument_list|)
decl_stmt|;
name|tempDir
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tempDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|tempDir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FS_NODE_ATTRIBUTE_STORE_ROOT_DIR
argument_list|,
name|tempDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
block|{
if|if
condition|(
name|attributesManager
operator|!=
literal|null
condition|)
block|{
name|attributesManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createAttributesForTest (String attributePrefix, int numOfAttributes, String attributeNamePrefix, String attributeValuePrefix)
specifier|private
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|createAttributesForTest
parameter_list|(
name|String
name|attributePrefix
parameter_list|,
name|int
name|numOfAttributes
parameter_list|,
name|String
name|attributeNamePrefix
parameter_list|,
name|String
name|attributeValuePrefix
parameter_list|)
block|{
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|attributes
init|=
operator|new
name|HashSet
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
name|numOfAttributes
condition|;
name|i
operator|++
control|)
block|{
name|NodeAttribute
name|attribute
init|=
name|NodeAttribute
operator|.
name|newInstance
argument_list|(
name|attributePrefix
argument_list|,
name|attributeNamePrefix
operator|+
literal|"_"
operator|+
name|i
argument_list|,
name|NodeAttributeType
operator|.
name|STRING
argument_list|,
name|attributeValuePrefix
operator|+
literal|"_"
operator|+
name|i
argument_list|)
decl_stmt|;
name|attributes
operator|.
name|add
argument_list|(
name|attribute
argument_list|)
expr_stmt|;
block|}
return|return
name|attributes
return|;
block|}
DECL|method|sameAttributeSet (Set<NodeAttribute> set1, Set<NodeAttribute> set2)
specifier|private
name|boolean
name|sameAttributeSet
parameter_list|(
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|set1
parameter_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|set2
parameter_list|)
block|{
return|return
name|Sets
operator|.
name|difference
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testAddNodeAttributes ()
specifier|public
name|void
name|testAddNodeAttributes
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|toAddAttributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|NodeAttribute
argument_list|,
name|AttributeValue
argument_list|>
name|nodeAttributes
decl_stmt|;
comment|// Add 3 attributes to host1
comment|//  yarn.test1.io/A1=host1_v1_1
comment|//  yarn.test1.io/A2=host1_v1_2
comment|//  yarn.test1.io/A3=host1_v1_3
name|toAddAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|,
literal|3
argument_list|,
literal|"A"
argument_list|,
literal|"host1_v1"
argument_list|)
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|addNodeAttributes
argument_list|(
name|toAddAttributes
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sameAttributeSet
argument_list|(
name|toAddAttributes
operator|.
name|get
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|nodeAttributes
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add 2 attributes to host2
comment|//  yarn.test1.io/A1=host2_v1_1
comment|//  yarn.test1.io/A2=host2_v1_2
name|toAddAttributes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|toAddAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|1
index|]
argument_list|,
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|,
literal|2
argument_list|,
literal|"A"
argument_list|,
literal|"host2_v1"
argument_list|)
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|addNodeAttributes
argument_list|(
name|toAddAttributes
argument_list|)
expr_stmt|;
comment|// Verify host1 attributes are still valid.
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify new added host2 attributes are correctly updated.
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sameAttributeSet
argument_list|(
name|toAddAttributes
operator|.
name|get
argument_list|(
name|HOSTNAMES
index|[
literal|1
index|]
argument_list|)
argument_list|,
name|nodeAttributes
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Cluster wide, it only has 3 attributes.
comment|//  yarn.test1.io/A1
comment|//  yarn.test1.io/A2
comment|//  yarn.test1.io/A3
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|clusterAttributes
init|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|clusterAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Query for attributes under a non-exist prefix,
comment|// ensure it returns an empty set.
name|clusterAttributes
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"non_exist_prefix"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|clusterAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Not provide any prefix, ensure it returns all attributes.
name|clusterAttributes
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|clusterAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add some other attributes with different prefixes on host1 and host2.
name|toAddAttributes
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Host1
comment|//  yarn.test2.io/A_1=host1_v2_1
comment|//  ...
comment|//  yarn.test2.io/A_10=host1_v2_10
name|toAddAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|1
index|]
argument_list|,
literal|10
argument_list|,
literal|"C"
argument_list|,
literal|"host1_v2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Host2
comment|//  yarn.test2.io/C_1=host1_v2_1
comment|//  ...
comment|//  yarn.test2.io/C_20=host1_v2_20
name|toAddAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|1
index|]
argument_list|,
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|1
index|]
argument_list|,
literal|20
argument_list|,
literal|"C"
argument_list|,
literal|"host1_v2"
argument_list|)
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|addNodeAttributes
argument_list|(
name|toAddAttributes
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveNodeAttributes ()
specifier|public
name|void
name|testRemoveNodeAttributes
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|toAddAttributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|toRemoveAttributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|allAttributesPerPrefix
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|NodeAttribute
argument_list|,
name|AttributeValue
argument_list|>
name|nodeAttributes
decl_stmt|;
comment|// Host1 -----------------------
comment|//  yarn.test1.io
comment|//    A1=host1_v1_1
comment|//    A2=host1_v1_2
comment|//    A3=host1_v1_3
comment|//  yarn.test2.io
comment|//    B1=host1_v2_1
comment|//    ...
comment|//    B5=host5_v2_5
comment|// Host2 -----------------------
comment|//  yarn.test1.io
comment|//    A1=host2_v1_1
comment|//    A2=host2_v1_2
comment|//  yarn.test3.io
comment|//    C1=host2_v3_1
comment|//    c2=host2_v3_2
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|host1set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|host1set1
init|=
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|,
literal|3
argument_list|,
literal|"A"
argument_list|,
literal|"host1_v1"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|host1set2
init|=
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|1
index|]
argument_list|,
literal|5
argument_list|,
literal|"B"
argument_list|,
literal|"host1_v1"
argument_list|)
decl_stmt|;
name|host1set
operator|.
name|addAll
argument_list|(
name|host1set1
argument_list|)
expr_stmt|;
name|host1set
operator|.
name|addAll
argument_list|(
name|host1set2
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|host2set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|host2set1
init|=
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|,
literal|2
argument_list|,
literal|"A"
argument_list|,
literal|"host2_v1"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|host2set2
init|=
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|2
index|]
argument_list|,
literal|2
argument_list|,
literal|"C"
argument_list|,
literal|"host2_v3"
argument_list|)
decl_stmt|;
name|host2set
operator|.
name|addAll
argument_list|(
name|host2set1
argument_list|)
expr_stmt|;
name|host2set
operator|.
name|addAll
argument_list|(
name|host2set2
argument_list|)
expr_stmt|;
name|toAddAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|host1set
argument_list|)
expr_stmt|;
name|toAddAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|1
index|]
argument_list|,
name|host2set
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|addNodeAttributes
argument_list|(
name|toAddAttributes
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|allAttributesPerPrefix
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|allAttributesPerPrefix
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|allAttributesPerPrefix
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|PREFIXES
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|allAttributesPerPrefix
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|allAttributesPerPrefix
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|PREFIXES
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allAttributesPerPrefix
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove "yarn.test1.io/A_2" from host1
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|attributes2rm1
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|attributes2rm1
operator|.
name|add
argument_list|(
name|NodeAttribute
operator|.
name|newInstance
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|,
literal|"A_2"
argument_list|,
name|NodeAttributeType
operator|.
name|STRING
argument_list|,
literal|"anyValue"
argument_list|)
argument_list|)
expr_stmt|;
name|toRemoveAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|attributes2rm1
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|removeNodeAttributes
argument_list|(
name|toRemoveAttributes
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove again, but give a non-exist attribute name
name|attributes2rm1
operator|.
name|clear
argument_list|()
expr_stmt|;
name|toRemoveAttributes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attributes2rm1
operator|.
name|add
argument_list|(
name|NodeAttribute
operator|.
name|newInstance
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|,
literal|"non_exist_name"
argument_list|,
name|NodeAttributeType
operator|.
name|STRING
argument_list|,
literal|"anyValue"
argument_list|)
argument_list|)
expr_stmt|;
name|toRemoveAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|attributes2rm1
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|removeNodeAttributes
argument_list|(
name|toRemoveAttributes
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove "yarn.test1.io/A_2" from host2 too,
comment|// by then there will be no such attribute exist in the cluster.
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|attributes2rm2
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|attributes2rm2
operator|.
name|add
argument_list|(
name|NodeAttribute
operator|.
name|newInstance
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|,
literal|"A_2"
argument_list|,
name|NodeAttributeType
operator|.
name|STRING
argument_list|,
literal|"anyValue"
argument_list|)
argument_list|)
expr_stmt|;
name|toRemoveAttributes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|toRemoveAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|1
index|]
argument_list|,
name|attributes2rm2
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|removeNodeAttributes
argument_list|(
name|toRemoveAttributes
argument_list|)
expr_stmt|;
comment|// Make sure cluster wide attributes are still consistent.
comment|// Since both host1 and host2 doesn't have "yarn.test1.io/A_2",
comment|// get all attributes under prefix "yarn.test1.io" should only return
comment|// us A_1 and A_3.
name|allAttributesPerPrefix
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allAttributesPerPrefix
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReplaceNodeAttributes ()
specifier|public
name|void
name|testReplaceNodeAttributes
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|toAddAttributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|toReplaceMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|NodeAttribute
argument_list|,
name|AttributeValue
argument_list|>
name|nodeAttributes
decl_stmt|;
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|filteredAttributes
decl_stmt|;
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|clusterAttributes
decl_stmt|;
comment|// Add 3 attributes to host1
comment|//  yarn.test1.io/A1=host1_v1_1
comment|//  yarn.test1.io/A2=host1_v1_2
comment|//  yarn.test1.io/A3=host1_v1_3
name|toAddAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|0
index|]
argument_list|,
literal|3
argument_list|,
literal|"A"
argument_list|,
literal|"host1_v1"
argument_list|)
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|addNodeAttributes
argument_list|(
name|toAddAttributes
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add 10 distributed node attributes to host1
comment|//  nn.yarn.io/dist-node-attribute1=dist_v1_1
comment|//  nn.yarn.io/dist-node-attribute2=dist_v1_2
comment|//  ...
comment|//  nn.yarn.io/dist-node-attribute10=dist_v1_10
name|toAddAttributes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|toAddAttributes
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|createAttributesForTest
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|,
literal|10
argument_list|,
literal|"dist-node-attribute"
argument_list|,
literal|"dist_v1"
argument_list|)
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|addNodeAttributes
argument_list|(
name|toAddAttributes
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|clusterAttributes
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|,
name|PREFIXES
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|clusterAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Replace by prefix
comment|// Same distributed attributes names, but different values.
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|toReplaceAttributes
init|=
name|createAttributesForTest
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|,
literal|5
argument_list|,
literal|"dist-node-attribute"
argument_list|,
literal|"dist_v2"
argument_list|)
decl_stmt|;
name|attributesManager
operator|.
name|replaceNodeAttributes
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|toReplaceAttributes
argument_list|)
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|clusterAttributes
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|,
name|PREFIXES
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|clusterAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now we have 5 distributed attributes
name|filteredAttributes
operator|=
name|NodeLabelUtil
operator|.
name|filterAttributesByPrefix
argument_list|(
name|nodeAttributes
operator|.
name|keySet
argument_list|()
argument_list|,
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|filteredAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Values are updated to have prefix dist_v2
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filteredAttributes
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|nodeAttribute
lambda|->
name|nodeAttribute
operator|.
name|getAttributeValue
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"dist_v2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// We still have 3 yarn.test1.io attributes
name|filteredAttributes
operator|=
name|NodeLabelUtil
operator|.
name|filterAttributesByPrefix
argument_list|(
name|nodeAttributes
operator|.
name|keySet
argument_list|()
argument_list|,
name|PREFIXES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|filteredAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Replace with prefix
comment|// Different attribute names
name|toReplaceAttributes
operator|=
name|createAttributesForTest
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|,
literal|1
argument_list|,
literal|"dist-node-attribute-v2"
argument_list|,
literal|"dist_v3"
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|replaceNodeAttributes
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|toReplaceAttributes
argument_list|)
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|clusterAttributes
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|clusterAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|NodeAttribute
name|attr
init|=
name|clusterAttributes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"dist-node-attribute-v2_0"
argument_list|,
name|attr
operator|.
name|getAttributeKey
argument_list|()
operator|.
name|getAttributeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|,
name|attr
operator|.
name|getAttributeKey
argument_list|()
operator|.
name|getAttributePrefix
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"dist_v3_0"
argument_list|,
name|attr
operator|.
name|getAttributeValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// Replace all attributes
name|toReplaceMap
operator|.
name|put
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|,
name|createAttributesForTest
argument_list|(
name|PREFIXES
index|[
literal|1
index|]
argument_list|,
literal|2
argument_list|,
literal|"B"
argument_list|,
literal|"B_v1"
argument_list|)
argument_list|)
expr_stmt|;
name|attributesManager
operator|.
name|replaceNodeAttributes
argument_list|(
literal|null
argument_list|,
name|toReplaceMap
argument_list|)
expr_stmt|;
name|nodeAttributes
operator|=
name|attributesManager
operator|.
name|getAttributesForNode
argument_list|(
name|HOSTNAMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nodeAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|clusterAttributes
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|PREFIXES
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|clusterAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|clusterAttributes
operator|=
name|attributesManager
operator|.
name|getClusterNodeAttributes
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_DISTRIBUTED
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|clusterAttributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

