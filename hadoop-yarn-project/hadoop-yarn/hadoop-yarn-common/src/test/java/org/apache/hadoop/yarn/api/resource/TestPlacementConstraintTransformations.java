begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.resource
package|package
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
name|maxCardinality
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
name|or
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
name|targetCardinality
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
name|List
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
operator|.
name|AbstractConstraint
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
operator|.
name|CardinalityConstraint
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
operator|.
name|Or
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
operator|.
name|SingleConstraint
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
operator|.
name|TargetConstraint
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
operator|.
name|TargetConstraint
operator|.
name|TargetOperator
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
name|PlacementConstraintTransformations
operator|.
name|SingleConstraintTransformer
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
name|PlacementConstraintTransformations
operator|.
name|SpecializedConstraintTransformer
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
operator|.
name|PlacementTargets
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
comment|/**  * Test class for {@link PlacementConstraintTransformations}.  */
end_comment

begin_class
DECL|class|TestPlacementConstraintTransformations
specifier|public
class|class
name|TestPlacementConstraintTransformations
block|{
annotation|@
name|Test
DECL|method|testTargetConstraint ()
specifier|public
name|void
name|testTargetConstraint
parameter_list|()
block|{
name|AbstractConstraint
name|sConstraintExpr
init|=
name|targetIn
argument_list|(
name|NODE
argument_list|,
name|allocationTag
argument_list|(
literal|"hbase-m"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sConstraintExpr
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|PlacementConstraint
name|sConstraint
init|=
name|PlacementConstraints
operator|.
name|build
argument_list|(
name|sConstraintExpr
argument_list|)
decl_stmt|;
comment|// Transform from SimpleConstraint to specialized TargetConstraint
name|SpecializedConstraintTransformer
name|specTransformer
init|=
operator|new
name|SpecializedConstraintTransformer
argument_list|(
name|sConstraint
argument_list|)
decl_stmt|;
name|PlacementConstraint
name|tConstraint
init|=
name|specTransformer
operator|.
name|transform
argument_list|()
decl_stmt|;
name|AbstractConstraint
name|tConstraintExpr
init|=
name|tConstraint
operator|.
name|getConstraintExpr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tConstraintExpr
operator|instanceof
name|TargetConstraint
argument_list|)
expr_stmt|;
name|SingleConstraint
name|single
init|=
operator|(
name|SingleConstraint
operator|)
name|sConstraintExpr
decl_stmt|;
name|TargetConstraint
name|target
init|=
operator|(
name|TargetConstraint
operator|)
name|tConstraintExpr
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|single
operator|.
name|getScope
argument_list|()
argument_list|,
name|target
operator|.
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TargetOperator
operator|.
name|IN
argument_list|,
name|target
operator|.
name|getOp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|single
operator|.
name|getTargetExpressions
argument_list|()
argument_list|,
name|target
operator|.
name|getTargetExpressions
argument_list|()
argument_list|)
expr_stmt|;
comment|// Transform from specialized TargetConstraint to SimpleConstraint
name|SingleConstraintTransformer
name|singleTransformer
init|=
operator|new
name|SingleConstraintTransformer
argument_list|(
name|tConstraint
argument_list|)
decl_stmt|;
name|sConstraint
operator|=
name|singleTransformer
operator|.
name|transform
argument_list|()
expr_stmt|;
name|sConstraintExpr
operator|=
name|sConstraint
operator|.
name|getConstraintExpr
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sConstraintExpr
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|single
operator|=
operator|(
name|SingleConstraint
operator|)
name|sConstraintExpr
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|target
operator|.
name|getScope
argument_list|()
argument_list|,
name|single
operator|.
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|single
operator|.
name|getMinCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|single
operator|.
name|getMaxCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|single
operator|.
name|getTargetExpressions
argument_list|()
argument_list|,
name|target
operator|.
name|getTargetExpressions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCardinalityConstraint ()
specifier|public
name|void
name|testCardinalityConstraint
parameter_list|()
block|{
name|CardinalityConstraint
name|cardinality
init|=
operator|new
name|CardinalityConstraint
argument_list|(
name|RACK
argument_list|,
literal|3
argument_list|,
literal|10
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"hb"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|PlacementConstraint
name|cConstraint
init|=
name|PlacementConstraints
operator|.
name|build
argument_list|(
name|cardinality
argument_list|)
decl_stmt|;
comment|// Transform from specialized CardinalityConstraint to SimpleConstraint
name|SingleConstraintTransformer
name|singleTransformer
init|=
operator|new
name|SingleConstraintTransformer
argument_list|(
name|cConstraint
argument_list|)
decl_stmt|;
name|PlacementConstraint
name|sConstraint
init|=
name|singleTransformer
operator|.
name|transform
argument_list|()
decl_stmt|;
name|AbstractConstraint
name|sConstraintExpr
init|=
name|sConstraint
operator|.
name|getConstraintExpr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sConstraintExpr
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|SingleConstraint
name|single
init|=
operator|(
name|SingleConstraint
operator|)
name|sConstraintExpr
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cardinality
operator|.
name|getScope
argument_list|()
argument_list|,
name|single
operator|.
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cardinality
operator|.
name|getMinCardinality
argument_list|()
argument_list|,
name|single
operator|.
name|getMinCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cardinality
operator|.
name|getMaxCardinality
argument_list|()
argument_list|,
name|single
operator|.
name|getMaxCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|PlacementTargets
operator|.
name|allocationTag
argument_list|(
literal|"hb"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|single
operator|.
name|getTargetExpressions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTargetCardinalityConstraint ()
specifier|public
name|void
name|testTargetCardinalityConstraint
parameter_list|()
block|{
name|AbstractConstraint
name|constraintExpr
init|=
name|targetCardinality
argument_list|(
name|RACK
argument_list|,
literal|3
argument_list|,
literal|10
argument_list|,
name|allocationTag
argument_list|(
literal|"zk"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraintExpr
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|PlacementConstraint
name|constraint
init|=
name|PlacementConstraints
operator|.
name|build
argument_list|(
name|constraintExpr
argument_list|)
decl_stmt|;
comment|// Apply transformation. Should be a no-op.
name|SpecializedConstraintTransformer
name|specTransformer
init|=
operator|new
name|SpecializedConstraintTransformer
argument_list|(
name|constraint
argument_list|)
decl_stmt|;
name|PlacementConstraint
name|newConstraint
init|=
name|specTransformer
operator|.
name|transform
argument_list|()
decl_stmt|;
comment|// The constraint expression should be the same.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|constraintExpr
argument_list|,
name|newConstraint
operator|.
name|getConstraintExpr
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCompositeConstraint ()
specifier|public
name|void
name|testCompositeConstraint
parameter_list|()
block|{
name|AbstractConstraint
name|constraintExpr
init|=
name|or
argument_list|(
name|targetIn
argument_list|(
name|RACK
argument_list|,
name|allocationTag
argument_list|(
literal|"spark"
argument_list|)
argument_list|)
argument_list|,
name|maxCardinality
argument_list|(
name|NODE
argument_list|,
literal|3
argument_list|)
argument_list|,
name|targetCardinality
argument_list|(
name|RACK
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
name|allocationTag
argument_list|(
literal|"zk"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraintExpr
operator|instanceof
name|Or
argument_list|)
expr_stmt|;
name|PlacementConstraint
name|constraint
init|=
name|PlacementConstraints
operator|.
name|build
argument_list|(
name|constraintExpr
argument_list|)
decl_stmt|;
name|Or
name|orExpr
init|=
operator|(
name|Or
operator|)
name|constraintExpr
decl_stmt|;
for|for
control|(
name|AbstractConstraint
name|child
range|:
name|orExpr
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
block|}
comment|// Apply transformation. Should transform target and cardinality constraints
comment|// included in the composite constraint to specialized ones.
name|SpecializedConstraintTransformer
name|specTransformer
init|=
operator|new
name|SpecializedConstraintTransformer
argument_list|(
name|constraint
argument_list|)
decl_stmt|;
name|PlacementConstraint
name|specConstraint
init|=
name|specTransformer
operator|.
name|transform
argument_list|()
decl_stmt|;
name|Or
name|specOrExpr
init|=
operator|(
name|Or
operator|)
name|specConstraint
operator|.
name|getConstraintExpr
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AbstractConstraint
argument_list|>
name|specChildren
init|=
name|specOrExpr
operator|.
name|getChildren
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|specChildren
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|specChildren
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|TargetConstraint
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|specChildren
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|specChildren
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
comment|// Transform from specialized TargetConstraint to SimpleConstraint
name|SingleConstraintTransformer
name|singleTransformer
init|=
operator|new
name|SingleConstraintTransformer
argument_list|(
name|specConstraint
argument_list|)
decl_stmt|;
name|PlacementConstraint
name|simConstraint
init|=
name|singleTransformer
operator|.
name|transform
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraintExpr
operator|instanceof
name|Or
argument_list|)
expr_stmt|;
name|Or
name|simOrExpr
init|=
operator|(
name|Or
operator|)
name|specConstraint
operator|.
name|getConstraintExpr
argument_list|()
decl_stmt|;
for|for
control|(
name|AbstractConstraint
name|child
range|:
name|simOrExpr
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

