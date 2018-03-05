begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint
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
name|scheduler
operator|.
name|constraint
package|;
end_package

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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|Public
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
name|InterfaceStability
operator|.
name|Unstable
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
name|AllocationTagNamespace
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
name|records
operator|.
name|ApplicationId
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
name|SchedulingRequest
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
name|TargetApplications
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
name|PlacementConstraints
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
name|InvalidAllocationTagException
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
name|server
operator|.
name|resourcemanager
operator|.
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|SchedulerNode
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|constraint
operator|.
name|algorithm
operator|.
name|DefaultPlacementAlgorithm
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
name|NODE_PARTITION
import|;
end_import

begin_comment
comment|/**  * This class contains various static methods used by the Placement Algorithms  * to simplify constrained placement.  * (see also {@link DefaultPlacementAlgorithm}).  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|PlacementConstraintsUtil
specifier|public
specifier|final
class|class
name|PlacementConstraintsUtil
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|PlacementConstraintsUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Suppresses default constructor, ensuring non-instantiability.
DECL|method|PlacementConstraintsUtil ()
specifier|private
name|PlacementConstraintsUtil
parameter_list|()
block|{   }
comment|/**    * Try to the namespace of the allocation tags from the given target key.    *    * @param targetKey    * @return allocation tag namespace.    * @throws InvalidAllocationTagsQueryException    * if fail to parse the target key to a valid namespace.    */
DECL|method|getAllocationTagNamespace ( ApplicationId currentAppId, String targetKey, AllocationTagsManager atm)
specifier|private
specifier|static
name|AllocationTagNamespace
name|getAllocationTagNamespace
parameter_list|(
name|ApplicationId
name|currentAppId
parameter_list|,
name|String
name|targetKey
parameter_list|,
name|AllocationTagsManager
name|atm
parameter_list|)
throws|throws
name|InvalidAllocationTagException
block|{
comment|// Parse to a valid namespace.
name|AllocationTagNamespace
name|namespace
init|=
name|AllocationTagNamespace
operator|.
name|parse
argument_list|(
name|targetKey
argument_list|)
decl_stmt|;
comment|// TODO remove such check once we support all forms of namespaces
if|if
condition|(
operator|!
name|namespace
operator|.
name|isIntraApp
argument_list|()
operator|&&
operator|!
name|namespace
operator|.
name|isSingleInterApp
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidAllocationTagException
argument_list|(
literal|"Only support "
operator|+
name|AllocationTagNamespaceType
operator|.
name|SELF
operator|.
name|toString
argument_list|()
operator|+
literal|" and "
operator|+
name|AllocationTagNamespaceType
operator|.
name|APP_ID
operator|+
literal|" now,"
operator|+
name|namespace
operator|.
name|toString
argument_list|()
operator|+
literal|" is not supported yet!"
argument_list|)
throw|;
block|}
comment|// Evaluate the namespace according to the given target
comment|// before it can be consumed.
name|TargetApplications
name|ta
init|=
operator|new
name|TargetApplications
argument_list|(
name|currentAppId
argument_list|,
name|atm
operator|.
name|getAllApplicationIds
argument_list|()
argument_list|)
decl_stmt|;
name|namespace
operator|.
name|evaluate
argument_list|(
name|ta
argument_list|)
expr_stmt|;
return|return
name|namespace
return|;
block|}
comment|// We return a single app Id now, because at present,
comment|// only self and app-id namespace is supported. But moving on,
comment|// this will return a set of application IDs.
comment|// TODO support other forms of namespaces
DECL|method|getNamespaceScope ( AllocationTagNamespace namespace)
specifier|private
specifier|static
name|ApplicationId
name|getNamespaceScope
parameter_list|(
name|AllocationTagNamespace
name|namespace
parameter_list|)
throws|throws
name|InvalidAllocationTagException
block|{
if|if
condition|(
name|namespace
operator|.
name|getNamespaceScope
argument_list|()
operator|==
literal|null
operator|||
name|namespace
operator|.
name|getNamespaceScope
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidAllocationTagException
argument_list|(
literal|"Invalid allocation tag namespace "
operator|+
name|namespace
operator|.
name|toString
argument_list|()
operator|+
literal|", expecting it is not null and only 1 application"
operator|+
literal|" ID in the scope."
argument_list|)
throw|;
block|}
return|return
name|namespace
operator|.
name|getNamespaceScope
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
comment|/**    * Returns true if<b>single</b> placement constraint with associated    * allocationTags and scope is satisfied by a specific scheduler Node.    *    * @param targetApplicationId the application id, which could be override by    *                           target application id specified inside allocation    *                           tags.    * @param sc the placement constraint    * @param te the target expression    * @param node the scheduler node    * @param tm the allocation tags store    * @return true if single application constraint is satisfied by node    * @throws InvalidAllocationTagsQueryException    */
DECL|method|canSatisfySingleConstraintExpression ( ApplicationId targetApplicationId, SingleConstraint sc, TargetExpression te, SchedulerNode node, AllocationTagsManager tm)
specifier|private
specifier|static
name|boolean
name|canSatisfySingleConstraintExpression
parameter_list|(
name|ApplicationId
name|targetApplicationId
parameter_list|,
name|SingleConstraint
name|sc
parameter_list|,
name|TargetExpression
name|te
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|AllocationTagsManager
name|tm
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
comment|// Parse the allocation tag's namespace from the given target key,
comment|// then evaluate the namespace and get its scope,
comment|// which is represented by one or more application IDs.
name|ApplicationId
name|effectiveAppID
decl_stmt|;
try|try
block|{
name|AllocationTagNamespace
name|namespace
init|=
name|getAllocationTagNamespace
argument_list|(
name|targetApplicationId
argument_list|,
name|te
operator|.
name|getTargetKey
argument_list|()
argument_list|,
name|tm
argument_list|)
decl_stmt|;
name|effectiveAppID
operator|=
name|getNamespaceScope
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidAllocationTagException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidAllocationTagsQueryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|long
name|minScopeCardinality
init|=
literal|0
decl_stmt|;
name|long
name|maxScopeCardinality
init|=
literal|0
decl_stmt|;
comment|// Optimizations to only check cardinality if necessary.
name|int
name|desiredMinCardinality
init|=
name|sc
operator|.
name|getMinCardinality
argument_list|()
decl_stmt|;
name|int
name|desiredMaxCardinality
init|=
name|sc
operator|.
name|getMaxCardinality
argument_list|()
decl_stmt|;
name|boolean
name|checkMinCardinality
init|=
name|desiredMinCardinality
operator|>
literal|0
decl_stmt|;
name|boolean
name|checkMaxCardinality
init|=
name|desiredMaxCardinality
operator|<
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|sc
operator|.
name|getScope
argument_list|()
operator|.
name|equals
argument_list|(
name|PlacementConstraints
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkMinCardinality
condition|)
block|{
name|minScopeCardinality
operator|=
name|tm
operator|.
name|getNodeCardinalityByOp
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|effectiveAppID
argument_list|,
name|te
operator|.
name|getTargetValues
argument_list|()
argument_list|,
name|Long
operator|::
name|max
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checkMaxCardinality
condition|)
block|{
name|maxScopeCardinality
operator|=
name|tm
operator|.
name|getNodeCardinalityByOp
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|effectiveAppID
argument_list|,
name|te
operator|.
name|getTargetValues
argument_list|()
argument_list|,
name|Long
operator|::
name|min
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|sc
operator|.
name|getScope
argument_list|()
operator|.
name|equals
argument_list|(
name|PlacementConstraints
operator|.
name|RACK
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkMinCardinality
condition|)
block|{
name|minScopeCardinality
operator|=
name|tm
operator|.
name|getRackCardinalityByOp
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|,
name|effectiveAppID
argument_list|,
name|te
operator|.
name|getTargetValues
argument_list|()
argument_list|,
name|Long
operator|::
name|max
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checkMaxCardinality
condition|)
block|{
name|maxScopeCardinality
operator|=
name|tm
operator|.
name|getRackCardinalityByOp
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|,
name|effectiveAppID
argument_list|,
name|te
operator|.
name|getTargetValues
argument_list|()
argument_list|,
name|Long
operator|::
name|min
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|desiredMinCardinality
operator|<=
literal|0
operator|||
name|minScopeCardinality
operator|>=
name|desiredMinCardinality
operator|)
operator|&&
operator|(
name|desiredMaxCardinality
operator|==
name|Integer
operator|.
name|MAX_VALUE
operator|||
name|maxScopeCardinality
operator|<=
name|desiredMaxCardinality
operator|)
return|;
block|}
DECL|method|canSatisfyNodePartitionConstraintExpresssion ( TargetExpression targetExpression, SchedulerNode schedulerNode)
specifier|private
specifier|static
name|boolean
name|canSatisfyNodePartitionConstraintExpresssion
parameter_list|(
name|TargetExpression
name|targetExpression
parameter_list|,
name|SchedulerNode
name|schedulerNode
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
name|targetExpression
operator|.
name|getTargetValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
operator|||
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|schedulerNode
operator|.
name|getPartition
argument_list|()
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|nodePartition
init|=
name|values
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|nodePartition
operator|.
name|equals
argument_list|(
name|schedulerNode
operator|.
name|getPartition
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|canSatisfySingleConstraint (ApplicationId applicationId, SingleConstraint singleConstraint, SchedulerNode schedulerNode, AllocationTagsManager tagsManager)
specifier|private
specifier|static
name|boolean
name|canSatisfySingleConstraint
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|SingleConstraint
name|singleConstraint
parameter_list|,
name|SchedulerNode
name|schedulerNode
parameter_list|,
name|AllocationTagsManager
name|tagsManager
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
comment|// Iterate through TargetExpressions
name|Iterator
argument_list|<
name|TargetExpression
argument_list|>
name|expIt
init|=
name|singleConstraint
operator|.
name|getTargetExpressions
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|expIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TargetExpression
name|currentExp
init|=
name|expIt
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// Supporting AllocationTag Expressions for now
if|if
condition|(
name|currentExp
operator|.
name|getTargetType
argument_list|()
operator|.
name|equals
argument_list|(
name|TargetType
operator|.
name|ALLOCATION_TAG
argument_list|)
condition|)
block|{
comment|// Check if conditions are met
if|if
condition|(
operator|!
name|canSatisfySingleConstraintExpression
argument_list|(
name|applicationId
argument_list|,
name|singleConstraint
argument_list|,
name|currentExp
argument_list|,
name|schedulerNode
argument_list|,
name|tagsManager
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|currentExp
operator|.
name|getTargetType
argument_list|()
operator|.
name|equals
argument_list|(
name|TargetType
operator|.
name|NODE_ATTRIBUTE
argument_list|)
operator|&&
name|currentExp
operator|.
name|getTargetKey
argument_list|()
operator|.
name|equals
argument_list|(
name|NODE_PARTITION
argument_list|)
condition|)
block|{
comment|// This is a node partition expression, check it.
name|canSatisfyNodePartitionConstraintExpresssion
argument_list|(
name|currentExp
argument_list|,
name|schedulerNode
argument_list|)
expr_stmt|;
block|}
block|}
comment|// return true if all targetExpressions are satisfied
return|return
literal|true
return|;
block|}
comment|/**    * Returns true if all child constraints are satisfied.    * @param appId application id    * @param constraint Or constraint    * @param node node    * @param atm allocation tags manager    * @return true if all child constraints are satisfied, false otherwise    * @throws InvalidAllocationTagsQueryException    */
DECL|method|canSatisfyAndConstraint (ApplicationId appId, And constraint, SchedulerNode node, AllocationTagsManager atm)
specifier|private
specifier|static
name|boolean
name|canSatisfyAndConstraint
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|And
name|constraint
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|AllocationTagsManager
name|atm
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
comment|// Iterate over the constraints tree, if found any child constraint
comment|// isn't satisfied, return false.
for|for
control|(
name|AbstractConstraint
name|child
range|:
name|constraint
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|canSatisfyConstraints
argument_list|(
name|appId
argument_list|,
name|child
operator|.
name|build
argument_list|()
argument_list|,
name|node
argument_list|,
name|atm
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Returns true as long as any of child constraint is satisfied.    * @param appId application id    * @param constraint Or constraint    * @param node node    * @param atm allocation tags manager    * @return true if any child constraint is satisfied, false otherwise    * @throws InvalidAllocationTagsQueryException    */
DECL|method|canSatisfyOrConstraint (ApplicationId appId, Or constraint, SchedulerNode node, AllocationTagsManager atm)
specifier|private
specifier|static
name|boolean
name|canSatisfyOrConstraint
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|Or
name|constraint
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|AllocationTagsManager
name|atm
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
for|for
control|(
name|AbstractConstraint
name|child
range|:
name|constraint
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|canSatisfyConstraints
argument_list|(
name|appId
argument_list|,
name|child
operator|.
name|build
argument_list|()
argument_list|,
name|node
argument_list|,
name|atm
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|canSatisfyConstraints (ApplicationId appId, PlacementConstraint constraint, SchedulerNode node, AllocationTagsManager atm)
specifier|private
specifier|static
name|boolean
name|canSatisfyConstraints
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|PlacementConstraint
name|constraint
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|AllocationTagsManager
name|atm
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
if|if
condition|(
name|constraint
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// If this is a single constraint, transform to SingleConstraint
name|SingleConstraintTransformer
name|singleTransformer
init|=
operator|new
name|SingleConstraintTransformer
argument_list|(
name|constraint
argument_list|)
decl_stmt|;
name|constraint
operator|=
name|singleTransformer
operator|.
name|transform
argument_list|()
expr_stmt|;
name|AbstractConstraint
name|sConstraintExpr
init|=
name|constraint
operator|.
name|getConstraintExpr
argument_list|()
decl_stmt|;
comment|// TODO handle other type of constraints, e.g CompositeConstraint
if|if
condition|(
name|sConstraintExpr
operator|instanceof
name|SingleConstraint
condition|)
block|{
name|SingleConstraint
name|single
init|=
operator|(
name|SingleConstraint
operator|)
name|sConstraintExpr
decl_stmt|;
return|return
name|canSatisfySingleConstraint
argument_list|(
name|appId
argument_list|,
name|single
argument_list|,
name|node
argument_list|,
name|atm
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|sConstraintExpr
operator|instanceof
name|And
condition|)
block|{
name|And
name|and
init|=
operator|(
name|And
operator|)
name|sConstraintExpr
decl_stmt|;
return|return
name|canSatisfyAndConstraint
argument_list|(
name|appId
argument_list|,
name|and
argument_list|,
name|node
argument_list|,
name|atm
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|sConstraintExpr
operator|instanceof
name|Or
condition|)
block|{
name|Or
name|or
init|=
operator|(
name|Or
operator|)
name|sConstraintExpr
decl_stmt|;
return|return
name|canSatisfyOrConstraint
argument_list|(
name|appId
argument_list|,
name|or
argument_list|,
name|node
argument_list|,
name|atm
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|InvalidAllocationTagsQueryException
argument_list|(
literal|"Unsupported type of constraint: "
operator|+
name|sConstraintExpr
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns true if the placement constraint for a given scheduling request    * is<b>currently</b> satisfied by the specific scheduler node. This method    * first validates the constraint specified in the request; if not specified,    * then it validates application level constraint if exists; otherwise, it    * validates the global constraint if exists.    *    * This method only checks whether a scheduling request can be placed    * on a node with respect to the certain placement constraint. It gives no    * guarantee that asked allocations can be eventually allocated because    * it doesn't check resource, that needs to be further decided by a scheduler.    *    * @param applicationId application id    * @param request scheduling request    * @param schedulerNode node    * @param pcm placement constraint manager    * @param atm allocation tags manager    * @return true if the given node satisfies the constraint of the request    * @throws InvalidAllocationTagsQueryException    */
DECL|method|canSatisfyConstraints (ApplicationId applicationId, SchedulingRequest request, SchedulerNode schedulerNode, PlacementConstraintManager pcm, AllocationTagsManager atm)
specifier|public
specifier|static
name|boolean
name|canSatisfyConstraints
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|SchedulingRequest
name|request
parameter_list|,
name|SchedulerNode
name|schedulerNode
parameter_list|,
name|PlacementConstraintManager
name|pcm
parameter_list|,
name|AllocationTagsManager
name|atm
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|sourceTags
init|=
literal|null
decl_stmt|;
name|PlacementConstraint
name|pc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
block|{
name|sourceTags
operator|=
name|request
operator|.
name|getAllocationTags
argument_list|()
expr_stmt|;
name|pc
operator|=
name|request
operator|.
name|getPlacementConstraint
argument_list|()
expr_stmt|;
block|}
return|return
name|canSatisfyConstraints
argument_list|(
name|applicationId
argument_list|,
name|pcm
operator|.
name|getMultilevelConstraint
argument_list|(
name|applicationId
argument_list|,
name|sourceTags
argument_list|,
name|pc
argument_list|)
argument_list|,
name|schedulerNode
argument_list|,
name|atm
argument_list|)
return|;
block|}
block|}
end_class

end_unit

