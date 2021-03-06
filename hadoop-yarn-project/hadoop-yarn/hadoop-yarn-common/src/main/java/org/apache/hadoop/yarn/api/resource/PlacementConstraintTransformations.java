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
name|java
operator|.
name|util
operator|.
name|ListIterator
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|AllocationTagNamespaceType
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
name|CompositeConstraint
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
name|DelayedOr
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
name|TimedPlacementConstraint
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
name|exceptions
operator|.
name|YarnRuntimeException
import|;
end_import

begin_comment
comment|/**  * This class contains inner classes that define transformation on a  * {@link PlacementConstraint} expression.  */
end_comment

begin_class
annotation|@
name|Private
DECL|class|PlacementConstraintTransformations
specifier|public
class|class
name|PlacementConstraintTransformations
block|{
comment|/**    * The default implementation of the {@link PlacementConstraint.Visitor} that    * does a traversal of the constraint tree, performing no action for the lead    * constraints.    */
DECL|class|AbstractTransformer
specifier|public
specifier|static
class|class
name|AbstractTransformer
implements|implements
name|PlacementConstraint
operator|.
name|Visitor
argument_list|<
name|AbstractConstraint
argument_list|>
block|{
DECL|field|placementConstraint
specifier|private
name|PlacementConstraint
name|placementConstraint
decl_stmt|;
DECL|method|AbstractTransformer (PlacementConstraint placementConstraint)
specifier|public
name|AbstractTransformer
parameter_list|(
name|PlacementConstraint
name|placementConstraint
parameter_list|)
block|{
name|this
operator|.
name|placementConstraint
operator|=
name|placementConstraint
expr_stmt|;
block|}
comment|/**      * This method performs the transformation of the      * {@link #placementConstraint}.      *      * @return the transformed placement constraint.      */
DECL|method|transform ()
specifier|public
name|PlacementConstraint
name|transform
parameter_list|()
block|{
name|AbstractConstraint
name|constraintExpr
init|=
name|placementConstraint
operator|.
name|getConstraintExpr
argument_list|()
decl_stmt|;
comment|// Visit the constraint tree to perform the transformation.
name|constraintExpr
operator|=
name|constraintExpr
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
operator|new
name|PlacementConstraint
argument_list|(
name|constraintExpr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|visit (SingleConstraint constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|SingleConstraint
name|constraint
parameter_list|)
block|{
comment|// Do nothing.
return|return
name|constraint
return|;
block|}
annotation|@
name|Override
DECL|method|visit (TargetExpression expression)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|TargetExpression
name|expression
parameter_list|)
block|{
comment|// Do nothing.
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visit (TargetConstraint constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|TargetConstraint
name|constraint
parameter_list|)
block|{
comment|// Do nothing.
return|return
name|constraint
return|;
block|}
annotation|@
name|Override
DECL|method|visit (CardinalityConstraint constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|CardinalityConstraint
name|constraint
parameter_list|)
block|{
comment|// Do nothing.
return|return
name|constraint
return|;
block|}
DECL|method|visitAndOr ( CompositeConstraint<AbstractConstraint> constraint)
specifier|private
name|AbstractConstraint
name|visitAndOr
parameter_list|(
name|CompositeConstraint
argument_list|<
name|AbstractConstraint
argument_list|>
name|constraint
parameter_list|)
block|{
for|for
control|(
name|ListIterator
argument_list|<
name|AbstractConstraint
argument_list|>
name|iter
init|=
name|constraint
operator|.
name|getChildren
argument_list|()
operator|.
name|listIterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AbstractConstraint
name|child
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|child
operator|=
name|child
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|iter
operator|.
name|set
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|constraint
return|;
block|}
annotation|@
name|Override
DECL|method|visit (And constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|And
name|constraint
parameter_list|)
block|{
return|return
name|visitAndOr
argument_list|(
name|constraint
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|visit (Or constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|Or
name|constraint
parameter_list|)
block|{
return|return
name|visitAndOr
argument_list|(
name|constraint
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|visit (DelayedOr constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|DelayedOr
name|constraint
parameter_list|)
block|{
name|constraint
operator|.
name|getChildren
argument_list|()
operator|.
name|forEach
argument_list|(
name|child
lambda|->
name|child
operator|.
name|setConstraint
argument_list|(
name|child
operator|.
name|getConstraint
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|constraint
return|;
block|}
annotation|@
name|Override
DECL|method|visit (TimedPlacementConstraint constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|TimedPlacementConstraint
name|constraint
parameter_list|)
block|{
comment|// Do nothing.
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Visits a {@link PlacementConstraint} tree and substitutes each    * {@link TargetConstraint} and {@link CardinalityConstraint} with an    * equivalent {@link SingleConstraint}.    */
DECL|class|SingleConstraintTransformer
specifier|public
specifier|static
class|class
name|SingleConstraintTransformer
extends|extends
name|AbstractTransformer
block|{
DECL|method|SingleConstraintTransformer (PlacementConstraint constraint)
specifier|public
name|SingleConstraintTransformer
parameter_list|(
name|PlacementConstraint
name|constraint
parameter_list|)
block|{
name|super
argument_list|(
name|constraint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit (TargetConstraint constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|TargetConstraint
name|constraint
parameter_list|)
block|{
name|AbstractConstraint
name|newConstraint
decl_stmt|;
if|if
condition|(
name|constraint
operator|.
name|getOp
argument_list|()
operator|==
name|TargetOperator
operator|.
name|IN
condition|)
block|{
name|newConstraint
operator|=
operator|new
name|SingleConstraint
argument_list|(
name|constraint
operator|.
name|getScope
argument_list|()
argument_list|,
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|constraint
operator|.
name|getTargetExpressions
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constraint
operator|.
name|getOp
argument_list|()
operator|==
name|TargetOperator
operator|.
name|NOT_IN
condition|)
block|{
name|newConstraint
operator|=
operator|new
name|SingleConstraint
argument_list|(
name|constraint
operator|.
name|getScope
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|constraint
operator|.
name|getTargetExpressions
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Encountered unexpected type of constraint target operator: "
operator|+
name|constraint
operator|.
name|getOp
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|newConstraint
return|;
block|}
annotation|@
name|Override
DECL|method|visit (CardinalityConstraint constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|CardinalityConstraint
name|constraint
parameter_list|)
block|{
return|return
operator|new
name|SingleConstraint
argument_list|(
name|constraint
operator|.
name|getScope
argument_list|()
argument_list|,
name|constraint
operator|.
name|getMinCardinality
argument_list|()
argument_list|,
name|constraint
operator|.
name|getMaxCardinality
argument_list|()
argument_list|,
operator|new
name|TargetExpression
argument_list|(
name|TargetExpression
operator|.
name|TargetType
operator|.
name|ALLOCATION_TAG
argument_list|,
name|AllocationTagNamespaceType
operator|.
name|SELF
operator|.
name|toString
argument_list|()
argument_list|,
name|constraint
operator|.
name|getAllocationTags
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Visits a {@link PlacementConstraint} tree and, whenever possible,    * substitutes each {@link SingleConstraint} with a {@link TargetConstraint}.    * When such a substitution is not possible, we keep the original    * {@link SingleConstraint}.    */
DECL|class|SpecializedConstraintTransformer
specifier|public
specifier|static
class|class
name|SpecializedConstraintTransformer
extends|extends
name|AbstractTransformer
block|{
DECL|method|SpecializedConstraintTransformer (PlacementConstraint constraint)
specifier|public
name|SpecializedConstraintTransformer
parameter_list|(
name|PlacementConstraint
name|constraint
parameter_list|)
block|{
name|super
argument_list|(
name|constraint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit (SingleConstraint constraint)
specifier|public
name|AbstractConstraint
name|visit
parameter_list|(
name|SingleConstraint
name|constraint
parameter_list|)
block|{
name|AbstractConstraint
name|transformedConstraint
init|=
name|constraint
decl_stmt|;
comment|// Check if it is a target constraint.
if|if
condition|(
name|constraint
operator|.
name|getMinCardinality
argument_list|()
operator|==
literal|1
operator|&&
name|constraint
operator|.
name|getMaxCardinality
argument_list|()
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|transformedConstraint
operator|=
operator|new
name|TargetConstraint
argument_list|(
name|TargetOperator
operator|.
name|IN
argument_list|,
name|constraint
operator|.
name|getScope
argument_list|()
argument_list|,
name|constraint
operator|.
name|getTargetExpressions
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constraint
operator|.
name|getMinCardinality
argument_list|()
operator|==
literal|0
operator|&&
name|constraint
operator|.
name|getMaxCardinality
argument_list|()
operator|==
literal|0
condition|)
block|{
name|transformedConstraint
operator|=
operator|new
name|TargetConstraint
argument_list|(
name|TargetOperator
operator|.
name|NOT_IN
argument_list|,
name|constraint
operator|.
name|getScope
argument_list|()
argument_list|,
name|constraint
operator|.
name|getTargetExpressions
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|transformedConstraint
return|;
block|}
block|}
block|}
end_class

end_unit

