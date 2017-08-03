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
name|and
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
name|nodeAttribute
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
name|And
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
name|TargetExpression
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
name|TargetExpression
operator|.
name|TargetType
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
comment|/**  * Test class for the various static methods in  * {@link org.apache.hadoop.yarn.api.resource.PlacementConstraints}.  */
end_comment

begin_class
DECL|class|TestPlacementConstraints
specifier|public
class|class
name|TestPlacementConstraints
block|{
annotation|@
name|Test
DECL|method|testNodeAffinityToTag ()
specifier|public
name|void
name|testNodeAffinityToTag
parameter_list|()
block|{
name|AbstractConstraint
name|constraintExpr
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
name|SingleConstraint
name|sConstraint
init|=
operator|(
name|SingleConstraint
operator|)
name|constraintExpr
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NODE
argument_list|,
name|sConstraint
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
name|sConstraint
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
name|sConstraint
operator|.
name|getMaxCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sConstraint
operator|.
name|getTargetExpressions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TargetExpression
name|tExpr
init|=
name|sConstraint
operator|.
name|getTargetExpressions
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|tExpr
operator|.
name|getTargetKey
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TargetType
operator|.
name|ALLOCATION_TAG
argument_list|,
name|tExpr
operator|.
name|getTargetType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tExpr
operator|.
name|getTargetValues
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"hbase-m"
argument_list|,
name|tExpr
operator|.
name|getTargetValues
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
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
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|constraint
operator|.
name|getConstraintExpr
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeAntiAffinityToAttribute ()
specifier|public
name|void
name|testNodeAntiAffinityToAttribute
parameter_list|()
block|{
name|AbstractConstraint
name|constraintExpr
init|=
name|targetNotIn
argument_list|(
name|NODE
argument_list|,
name|nodeAttribute
argument_list|(
literal|"java"
argument_list|,
literal|"1.8"
argument_list|)
argument_list|)
decl_stmt|;
name|SingleConstraint
name|sConstraint
init|=
operator|(
name|SingleConstraint
operator|)
name|constraintExpr
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NODE
argument_list|,
name|sConstraint
operator|.
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sConstraint
operator|.
name|getMinCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sConstraint
operator|.
name|getMaxCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sConstraint
operator|.
name|getTargetExpressions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TargetExpression
name|tExpr
init|=
name|sConstraint
operator|.
name|getTargetExpressions
argument_list|()
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
literal|"java"
argument_list|,
name|tExpr
operator|.
name|getTargetKey
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TargetType
operator|.
name|NODE_ATTRIBUTE
argument_list|,
name|tExpr
operator|.
name|getTargetType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tExpr
operator|.
name|getTargetValues
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1.8"
argument_list|,
name|tExpr
operator|.
name|getTargetValues
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAndConstraint ()
specifier|public
name|void
name|testAndConstraint
parameter_list|()
block|{
name|AbstractConstraint
name|constraintExpr
init|=
name|and
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
name|And
name|andExpr
init|=
operator|(
name|And
operator|)
name|constraintExpr
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|andExpr
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|SingleConstraint
name|sConstr
init|=
operator|(
name|SingleConstraint
operator|)
name|andExpr
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|TargetExpression
name|tExpr
init|=
name|sConstr
operator|.
name|getTargetExpressions
argument_list|()
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
literal|"spark"
argument_list|,
name|tExpr
operator|.
name|getTargetValues
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|sConstr
operator|=
operator|(
name|SingleConstraint
operator|)
name|andExpr
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sConstr
operator|.
name|getMinCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|sConstr
operator|.
name|getMaxCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|sConstr
operator|=
operator|(
name|SingleConstraint
operator|)
name|andExpr
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|sConstr
operator|.
name|getMinCardinality
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|sConstr
operator|.
name|getMaxCardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

