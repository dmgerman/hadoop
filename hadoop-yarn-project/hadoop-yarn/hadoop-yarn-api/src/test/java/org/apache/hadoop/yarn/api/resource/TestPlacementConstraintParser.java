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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParseException
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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParser
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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParser
operator|.
name|SourceTags
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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParser
operator|.
name|TargetConstraintParser
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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParser
operator|.
name|ConstraintParser
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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParser
operator|.
name|CardinalityConstraintParser
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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParser
operator|.
name|ConjunctionConstraintParser
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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParser
operator|.
name|MultipleConstraintsTokenizer
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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParser
operator|.
name|SourceTagsTokenizer
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
name|util
operator|.
name|constraint
operator|.
name|PlacementConstraintParser
operator|.
name|ConstraintTokenizer
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
name|*
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
comment|/**  * Class to test placement constraint parser.  */
end_comment

begin_class
DECL|class|TestPlacementConstraintParser
specifier|public
class|class
name|TestPlacementConstraintParser
block|{
annotation|@
name|Test
DECL|method|testTargetExpressionParser ()
specifier|public
name|void
name|testTargetExpressionParser
parameter_list|()
throws|throws
name|PlacementConstraintParseException
block|{
name|ConstraintParser
name|parser
decl_stmt|;
name|AbstractConstraint
name|constraint
decl_stmt|;
name|SingleConstraint
name|single
decl_stmt|;
comment|// Anti-affinity with single target tag
comment|// NOTIN,NDOE,foo
name|parser
operator|=
operator|new
name|TargetConstraintParser
argument_list|(
literal|"NOTIN, NODE, foo"
argument_list|)
expr_stmt|;
name|constraint
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraint
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|single
operator|=
operator|(
name|SingleConstraint
operator|)
name|constraint
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"node"
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
literal|0
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
literal|0
argument_list|,
name|single
operator|.
name|getMaxCardinality
argument_list|()
argument_list|)
expr_stmt|;
comment|// lower cases is also valid
name|parser
operator|=
operator|new
name|TargetConstraintParser
argument_list|(
literal|"notin, node, foo"
argument_list|)
expr_stmt|;
name|constraint
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraint
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|single
operator|=
operator|(
name|SingleConstraint
operator|)
name|constraint
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"node"
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
literal|0
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
literal|0
argument_list|,
name|single
operator|.
name|getMaxCardinality
argument_list|()
argument_list|)
expr_stmt|;
comment|// Affinity with single target tag
comment|// IN,NODE,foo
name|parser
operator|=
operator|new
name|TargetConstraintParser
argument_list|(
literal|"IN, NODE, foo"
argument_list|)
expr_stmt|;
name|constraint
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraint
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|single
operator|=
operator|(
name|SingleConstraint
operator|)
name|constraint
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"node"
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
comment|// Anti-affinity with multiple target tags
comment|// NOTIN,NDOE,foo,bar,exp
name|parser
operator|=
operator|new
name|TargetConstraintParser
argument_list|(
literal|"NOTIN, NODE, foo, bar, exp"
argument_list|)
expr_stmt|;
name|constraint
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraint
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|single
operator|=
operator|(
name|SingleConstraint
operator|)
name|constraint
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"node"
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
literal|0
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
literal|0
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
literal|1
argument_list|,
name|single
operator|.
name|getTargetExpressions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TargetExpression
name|exp
init|=
name|single
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
literal|"ALLOCATION_TAG"
argument_list|,
name|exp
operator|.
name|getTargetType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exp
operator|.
name|getTargetValues
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Invalid OP
name|parser
operator|=
operator|new
name|TargetConstraintParser
argument_list|(
literal|"XYZ, NODE, foo"
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|PlacementConstraintParseException
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"expecting in or notin"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCardinalityConstraintParser ()
specifier|public
name|void
name|testCardinalityConstraintParser
parameter_list|()
throws|throws
name|PlacementConstraintParseException
block|{
name|ConstraintParser
name|parser
decl_stmt|;
name|AbstractConstraint
name|constraint
decl_stmt|;
name|SingleConstraint
name|single
decl_stmt|;
comment|// cardinality,NODE,foo,0,1
name|parser
operator|=
operator|new
name|CardinalityConstraintParser
argument_list|(
literal|"cardinality, NODE, foo, 0, 1"
argument_list|)
expr_stmt|;
name|constraint
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraint
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|single
operator|=
operator|(
name|SingleConstraint
operator|)
name|constraint
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"node"
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
literal|0
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
literal|1
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
literal|1
argument_list|,
name|single
operator|.
name|getTargetExpressions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TargetExpression
name|exp
init|=
name|single
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
literal|"ALLOCATION_TAG"
argument_list|,
name|exp
operator|.
name|getTargetType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exp
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
literal|"foo"
argument_list|,
name|exp
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
comment|// cardinality,NODE,foo,bar,moo,0,1
name|parser
operator|=
operator|new
name|CardinalityConstraintParser
argument_list|(
literal|"cardinality,RACK,foo,bar,moo,0,1"
argument_list|)
expr_stmt|;
name|constraint
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraint
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|single
operator|=
operator|(
name|SingleConstraint
operator|)
name|constraint
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"rack"
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
literal|0
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
literal|1
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
literal|1
argument_list|,
name|single
operator|.
name|getTargetExpressions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|exp
operator|=
name|single
operator|.
name|getTargetExpressions
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ALLOCATION_TAG"
argument_list|,
name|exp
operator|.
name|getTargetType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exp
operator|.
name|getTargetValues
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedTags
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"moo"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Sets
operator|.
name|difference
argument_list|(
name|expectedTags
argument_list|,
name|exp
operator|.
name|getTargetValues
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Invalid scope string
try|try
block|{
name|parser
operator|=
operator|new
name|CardinalityConstraintParser
argument_list|(
literal|"cardinality,NOWHERE,foo,bar,moo,0,1"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expecting a parsing failure!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlacementConstraintParseException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"expecting scope to node or rack, but met NOWHERE"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Invalid number of expression elements
try|try
block|{
name|parser
operator|=
operator|new
name|CardinalityConstraintParser
argument_list|(
literal|"cardinality,NODE,0,1"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expecting a parsing failure!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlacementConstraintParseException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"at least 5 elements, but only 4 is given"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAndConstraintParser ()
specifier|public
name|void
name|testAndConstraintParser
parameter_list|()
throws|throws
name|PlacementConstraintParseException
block|{
name|ConstraintParser
name|parser
decl_stmt|;
name|AbstractConstraint
name|constraint
decl_stmt|;
name|And
name|and
decl_stmt|;
name|parser
operator|=
operator|new
name|ConjunctionConstraintParser
argument_list|(
literal|"AND(NOTIN,NODE,foo:NOTIN,NODE,bar)"
argument_list|)
expr_stmt|;
name|constraint
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraint
operator|instanceof
name|And
argument_list|)
expr_stmt|;
name|and
operator|=
operator|(
name|And
operator|)
name|constraint
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|and
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|ConjunctionConstraintParser
argument_list|(
literal|"AND(NOTIN,NODE,foo:cardinality,NODE,foo,0,1)"
argument_list|)
expr_stmt|;
name|constraint
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraint
operator|instanceof
name|And
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|and
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|ConjunctionConstraintParser
argument_list|(
literal|"AND(NOTIN,NODE,foo:AND(NOTIN,NODE,foo:cardinality,NODE,foo,0,1))"
argument_list|)
expr_stmt|;
name|constraint
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|constraint
operator|instanceof
name|And
argument_list|)
expr_stmt|;
name|and
operator|=
operator|(
name|And
operator|)
name|constraint
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|and
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|SingleConstraint
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|and
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|And
argument_list|)
expr_stmt|;
name|and
operator|=
operator|(
name|And
operator|)
name|and
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
literal|2
argument_list|,
name|and
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleConstraintsTokenizer ()
specifier|public
name|void
name|testMultipleConstraintsTokenizer
parameter_list|()
throws|throws
name|PlacementConstraintParseException
block|{
name|MultipleConstraintsTokenizer
name|ct
decl_stmt|;
name|SourceTagsTokenizer
name|st
decl_stmt|;
name|TokenizerTester
name|mp
decl_stmt|;
name|ct
operator|=
operator|new
name|MultipleConstraintsTokenizer
argument_list|(
literal|"foo=1,A1,A2,A3:bar=2,B1,B2:moo=3,C1,C2"
argument_list|)
expr_stmt|;
name|mp
operator|=
operator|new
name|TokenizerTester
argument_list|(
name|ct
argument_list|,
literal|"foo=1,A1,A2,A3"
argument_list|,
literal|"bar=2,B1,B2"
argument_list|,
literal|"moo=3,C1,C2"
argument_list|)
expr_stmt|;
name|mp
operator|.
name|verify
argument_list|()
expr_stmt|;
name|ct
operator|=
operator|new
name|MultipleConstraintsTokenizer
argument_list|(
literal|"foo=1,AND(A2:A3):bar=2,OR(B1:AND(B2:B3)):moo=3,C1,C2"
argument_list|)
expr_stmt|;
name|mp
operator|=
operator|new
name|TokenizerTester
argument_list|(
name|ct
argument_list|,
literal|"foo=1,AND(A2:A3)"
argument_list|,
literal|"bar=2,OR(B1:AND(B2:B3))"
argument_list|,
literal|"moo=3,C1,C2"
argument_list|)
expr_stmt|;
name|mp
operator|.
name|verify
argument_list|()
expr_stmt|;
name|ct
operator|=
operator|new
name|MultipleConstraintsTokenizer
argument_list|(
literal|"A:B:C"
argument_list|)
expr_stmt|;
name|mp
operator|=
operator|new
name|TokenizerTester
argument_list|(
name|ct
argument_list|,
literal|"A"
argument_list|,
literal|"B"
argument_list|,
literal|"C"
argument_list|)
expr_stmt|;
name|mp
operator|.
name|verify
argument_list|()
expr_stmt|;
name|ct
operator|=
operator|new
name|MultipleConstraintsTokenizer
argument_list|(
literal|"A:AND(B:C):D"
argument_list|)
expr_stmt|;
name|mp
operator|=
operator|new
name|TokenizerTester
argument_list|(
name|ct
argument_list|,
literal|"A"
argument_list|,
literal|"AND(B:C)"
argument_list|,
literal|"D"
argument_list|)
expr_stmt|;
name|mp
operator|.
name|verify
argument_list|()
expr_stmt|;
name|ct
operator|=
operator|new
name|MultipleConstraintsTokenizer
argument_list|(
literal|"A:AND(B:OR(C:D)):E"
argument_list|)
expr_stmt|;
name|mp
operator|=
operator|new
name|TokenizerTester
argument_list|(
name|ct
argument_list|,
literal|"A"
argument_list|,
literal|"AND(B:OR(C:D))"
argument_list|,
literal|"E"
argument_list|)
expr_stmt|;
name|mp
operator|.
name|verify
argument_list|()
expr_stmt|;
name|ct
operator|=
operator|new
name|MultipleConstraintsTokenizer
argument_list|(
literal|"A:AND(B:OR(C:D)):E"
argument_list|)
expr_stmt|;
name|mp
operator|=
operator|new
name|TokenizerTester
argument_list|(
name|ct
argument_list|,
literal|"A"
argument_list|,
literal|"AND(B:OR(C:D))"
argument_list|,
literal|"E"
argument_list|)
expr_stmt|;
name|mp
operator|.
name|verify
argument_list|()
expr_stmt|;
name|st
operator|=
operator|new
name|SourceTagsTokenizer
argument_list|(
literal|"A=4"
argument_list|)
expr_stmt|;
name|mp
operator|=
operator|new
name|TokenizerTester
argument_list|(
name|st
argument_list|,
literal|"A"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|mp
operator|.
name|verify
argument_list|()
expr_stmt|;
try|try
block|{
name|st
operator|=
operator|new
name|SourceTagsTokenizer
argument_list|(
literal|"A=B"
argument_list|)
expr_stmt|;
name|mp
operator|=
operator|new
name|TokenizerTester
argument_list|(
name|st
argument_list|,
literal|"A"
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
name|mp
operator|.
name|verify
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expecting a parsing failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlacementConstraintParseException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Value of the expression must be an integer"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TokenizerTester
specifier|private
specifier|static
class|class
name|TokenizerTester
block|{
DECL|field|tokenizer
specifier|private
name|ConstraintTokenizer
name|tokenizer
decl_stmt|;
DECL|field|expectedExtractions
specifier|private
name|String
index|[]
name|expectedExtractions
decl_stmt|;
DECL|method|TokenizerTester (ConstraintTokenizer tk, String... expctedStrings)
specifier|protected
name|TokenizerTester
parameter_list|(
name|ConstraintTokenizer
name|tk
parameter_list|,
name|String
modifier|...
name|expctedStrings
parameter_list|)
block|{
name|this
operator|.
name|tokenizer
operator|=
name|tk
expr_stmt|;
name|this
operator|.
name|expectedExtractions
operator|=
name|expctedStrings
expr_stmt|;
block|}
DECL|method|verify ()
name|void
name|verify
parameter_list|()
throws|throws
name|PlacementConstraintParseException
block|{
name|tokenizer
operator|.
name|validate
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|current
init|=
name|tokenizer
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|i
operator|<
name|expectedExtractions
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedExtractions
index|[
name|i
index|]
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testParsePlacementSpec ()
specifier|public
name|void
name|testParsePlacementSpec
parameter_list|()
throws|throws
name|PlacementConstraintParseException
block|{
name|Map
argument_list|<
name|SourceTags
argument_list|,
name|PlacementConstraint
argument_list|>
name|result
decl_stmt|;
name|PlacementConstraint
name|expectedPc1
decl_stmt|,
name|expectedPc2
decl_stmt|;
name|PlacementConstraint
name|actualPc1
decl_stmt|,
name|actualPc2
decl_stmt|;
name|SourceTags
name|tag1
decl_stmt|,
name|tag2
decl_stmt|;
comment|// A single anti-affinity constraint
name|result
operator|=
name|PlacementConstraintParser
operator|.
name|parsePlacementSpec
argument_list|(
literal|"foo=3,notin,node,foo"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|tag1
operator|=
name|result
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|tag1
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tag1
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|)
expr_stmt|;
name|expectedPc1
operator|=
name|targetNotIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|actualPc1
operator|=
name|result
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedPc1
argument_list|,
name|actualPc1
argument_list|)
expr_stmt|;
comment|// Upper case
name|result
operator|=
name|PlacementConstraintParser
operator|.
name|parsePlacementSpec
argument_list|(
literal|"foo=3,NOTIN,NODE,foo"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|tag1
operator|=
name|result
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|tag1
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tag1
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|)
expr_stmt|;
name|expectedPc1
operator|=
name|targetNotIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|actualPc1
operator|=
name|result
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedPc1
argument_list|,
name|actualPc1
argument_list|)
expr_stmt|;
comment|// A single cardinality constraint
name|result
operator|=
name|PlacementConstraintParser
operator|.
name|parsePlacementSpec
argument_list|(
literal|"foo=10,cardinality,node,foo,bar,0,100"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|tag1
operator|=
name|result
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|tag1
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|tag1
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|)
expr_stmt|;
name|expectedPc1
operator|=
name|cardinality
argument_list|(
literal|"node"
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedPc1
argument_list|,
name|result
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// Two constraint expressions
name|result
operator|=
name|PlacementConstraintParser
operator|.
name|parsePlacementSpec
argument_list|(
literal|"foo=3,notin,node,foo:bar=2,in,node,foo"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|SourceTags
argument_list|>
name|keyIt
init|=
name|result
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|tag1
operator|=
name|keyIt
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|tag1
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tag1
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|)
expr_stmt|;
name|tag2
operator|=
name|keyIt
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|tag2
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tag2
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|PlacementConstraint
argument_list|>
name|valueIt
init|=
name|result
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|expectedPc1
operator|=
name|targetNotIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|expectedPc2
operator|=
name|targetIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedPc1
argument_list|,
name|valueIt
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedPc2
argument_list|,
name|valueIt
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// And constraint
name|result
operator|=
name|PlacementConstraintParser
operator|.
name|parsePlacementSpec
argument_list|(
literal|"foo=1000,and(notin,node,bar:in,node,foo)"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|keyIt
operator|=
name|result
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|tag1
operator|=
name|keyIt
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|tag1
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|tag1
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|)
expr_stmt|;
name|actualPc1
operator|=
name|result
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|expectedPc1
operator|=
name|and
argument_list|(
name|targetNotIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|targetIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedPc1
argument_list|,
name|actualPc1
argument_list|)
expr_stmt|;
comment|// Multiple constraints with nested forms.
name|result
operator|=
name|PlacementConstraintParser
operator|.
name|parsePlacementSpec
argument_list|(
literal|"foo=1000,and(notin,node,bar:or(in,node,foo:in,node,moo))"
operator|+
literal|":bar=200,notin,node,foo"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|keyIt
operator|=
name|result
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|tag1
operator|=
name|keyIt
operator|.
name|next
argument_list|()
expr_stmt|;
name|tag2
operator|=
name|keyIt
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|tag1
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|tag1
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|tag2
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|tag2
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|)
expr_stmt|;
name|valueIt
operator|=
name|result
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|actualPc1
operator|=
name|valueIt
operator|.
name|next
argument_list|()
expr_stmt|;
name|actualPc2
operator|=
name|valueIt
operator|.
name|next
argument_list|()
expr_stmt|;
name|expectedPc1
operator|=
name|and
argument_list|(
name|targetNotIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|or
argument_list|(
name|targetIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|targetIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"moo"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|actualPc1
argument_list|,
name|expectedPc1
argument_list|)
expr_stmt|;
name|expectedPc2
operator|=
name|targetNotIn
argument_list|(
literal|"node"
argument_list|,
name|allocationTag
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedPc2
argument_list|,
name|actualPc2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

