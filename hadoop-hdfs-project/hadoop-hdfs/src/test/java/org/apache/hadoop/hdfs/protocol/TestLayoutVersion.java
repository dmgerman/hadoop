begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|assertNotNull
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
name|assertEquals
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
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
name|LayoutVersion
operator|.
name|Feature
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
name|LayoutVersion
operator|.
name|FeatureInfo
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
name|LayoutVersion
operator|.
name|LayoutFeature
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
name|datanode
operator|.
name|DataNodeLayoutVersion
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
name|NameNodeLayoutVersion
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
comment|/**  * Test for {@link LayoutVersion}  */
end_comment

begin_class
DECL|class|TestLayoutVersion
specifier|public
class|class
name|TestLayoutVersion
block|{
DECL|field|LAST_NON_RESERVED_COMMON_FEATURE
specifier|public
specifier|static
specifier|final
name|LayoutFeature
name|LAST_NON_RESERVED_COMMON_FEATURE
decl_stmt|;
DECL|field|LAST_COMMON_FEATURE
specifier|public
specifier|static
specifier|final
name|LayoutFeature
name|LAST_COMMON_FEATURE
decl_stmt|;
static|static
block|{
specifier|final
name|Feature
index|[]
name|features
init|=
name|Feature
operator|.
name|values
argument_list|()
decl_stmt|;
name|LAST_COMMON_FEATURE
operator|=
name|features
index|[
name|features
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
name|LAST_NON_RESERVED_COMMON_FEATURE
operator|=
name|LayoutVersion
operator|.
name|getLastNonReservedFeature
argument_list|(
name|features
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests to make sure a given layout version supports all the    * features from the ancestor    */
annotation|@
name|Test
DECL|method|testFeaturesFromAncestorSupported ()
specifier|public
name|void
name|testFeaturesFromAncestorSupported
parameter_list|()
block|{
for|for
control|(
name|LayoutFeature
name|f
range|:
name|Feature
operator|.
name|values
argument_list|()
control|)
block|{
name|validateFeatureList
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test to make sure 0.20.203 supports delegation token    */
annotation|@
name|Test
DECL|method|testRelease203 ()
specifier|public
name|void
name|testRelease203
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|LayoutVersion
operator|.
name|Feature
operator|.
name|DELEGATION_TOKEN
argument_list|,
name|Feature
operator|.
name|RESERVED_REL20_203
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to make sure 0.20.204 supports delegation token    */
annotation|@
name|Test
DECL|method|testRelease204 ()
specifier|public
name|void
name|testRelease204
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|LayoutVersion
operator|.
name|Feature
operator|.
name|DELEGATION_TOKEN
argument_list|,
name|Feature
operator|.
name|RESERVED_REL20_204
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to make sure release 1.2.0 support CONCAT    */
annotation|@
name|Test
DECL|method|testRelease1_2_0 ()
specifier|public
name|void
name|testRelease1_2_0
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|LayoutVersion
operator|.
name|Feature
operator|.
name|CONCAT
argument_list|,
name|Feature
operator|.
name|RESERVED_REL1_2_0
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to make sure NameNode.Feature support previous features    */
annotation|@
name|Test
DECL|method|testNameNodeFeature ()
specifier|public
name|void
name|testNameNodeFeature
parameter_list|()
block|{
specifier|final
name|LayoutFeature
name|first
init|=
name|NameNodeLayoutVersion
operator|.
name|Feature
operator|.
name|ROLLING_UPGRADE
decl_stmt|;
name|assertTrue
argument_list|(
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|LAST_NON_RESERVED_COMMON_FEATURE
argument_list|,
name|first
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LAST_COMMON_FEATURE
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
operator|-
literal|1
argument_list|,
name|first
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to make sure DataNode.Feature support previous features    */
annotation|@
name|Test
DECL|method|testDataNodeFeature ()
specifier|public
name|void
name|testDataNodeFeature
parameter_list|()
block|{
specifier|final
name|LayoutFeature
name|first
init|=
name|DataNodeLayoutVersion
operator|.
name|Feature
operator|.
name|FIRST_LAYOUT
decl_stmt|;
name|assertTrue
argument_list|(
name|DataNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|LAST_NON_RESERVED_COMMON_FEATURE
argument_list|,
name|first
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LAST_COMMON_FEATURE
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
operator|-
literal|1
argument_list|,
name|first
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Given feature {@code f}, ensures the layout version of that feature    * supports all the features supported by it's ancestor.    */
DECL|method|validateFeatureList (LayoutFeature f)
specifier|private
name|void
name|validateFeatureList
parameter_list|(
name|LayoutFeature
name|f
parameter_list|)
block|{
specifier|final
name|FeatureInfo
name|info
init|=
name|f
operator|.
name|getInfo
argument_list|()
decl_stmt|;
name|int
name|lv
init|=
name|info
operator|.
name|getLayoutVersion
argument_list|()
decl_stmt|;
name|int
name|ancestorLV
init|=
name|info
operator|.
name|getAncestorLayoutVersion
argument_list|()
decl_stmt|;
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
name|ancestorSet
init|=
name|NameNodeLayoutVersion
operator|.
name|getFeatures
argument_list|(
name|ancestorLV
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ancestorSet
argument_list|)
expr_stmt|;
for|for
control|(
name|LayoutFeature
name|feature
range|:
name|ancestorSet
control|)
block|{
name|assertTrue
argument_list|(
literal|"LV "
operator|+
name|lv
operator|+
literal|" does nto support "
operator|+
name|feature
operator|+
literal|" supported by the ancestor LV "
operator|+
name|info
operator|.
name|getAncestorLayoutVersion
argument_list|()
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|feature
argument_list|,
name|lv
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * When a LayoutVersion support SNAPSHOT, it must support    * FSIMAGE_NAME_OPTIMIZATION.    */
annotation|@
name|Test
DECL|method|testSNAPSHOT ()
specifier|public
name|void
name|testSNAPSHOT
parameter_list|()
block|{
for|for
control|(
name|Feature
name|f
range|:
name|Feature
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|int
name|version
init|=
name|f
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|SNAPSHOT
argument_list|,
name|version
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|FSIMAGE_NAME_OPTIMIZATION
argument_list|,
name|version
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

