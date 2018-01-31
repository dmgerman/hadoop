begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
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
name|classification
operator|.
name|InterfaceAudience
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
name|resource
operator|.
name|PlacementConstraint
import|;
end_import

begin_comment
comment|/**  * Interface for storing and retrieving placement constraints (see  * {@link PlacementConstraint}).  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|PlacementConstraintManager
specifier|public
interface|interface
name|PlacementConstraintManager
block|{
comment|/**    * Register all placement constraints of an application.    *    * @param appId the application ID    * @param constraintMap the map of allocation tags to constraints for this    *          application    */
DECL|method|registerApplication (ApplicationId appId, Map<Set<String>, PlacementConstraint> constraintMap)
name|void
name|registerApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|Map
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|,
name|PlacementConstraint
argument_list|>
name|constraintMap
parameter_list|)
function_decl|;
comment|/**    * Add a placement constraint for a given application and a given set of    * (source) allocation tags. The constraint will be used on Scheduling    * Requests that carry this set of allocation tags.    * TODO: Support merge and not only replace when adding a constraint.    *    * @param appId the application ID    * @param sourceTags the set of allocation tags that will enable this    *          constraint    * @param placementConstraint the constraint    * @param replace if true, an existing constraint for these tags will be    *          replaced by the given one    */
DECL|method|addConstraint (ApplicationId appId, Set<String> sourceTags, PlacementConstraint placementConstraint, boolean replace)
name|void
name|addConstraint
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|sourceTags
parameter_list|,
name|PlacementConstraint
name|placementConstraint
parameter_list|,
name|boolean
name|replace
parameter_list|)
function_decl|;
comment|/**    * Add a placement constraint that will be used globally. These constraints    * are added by the cluster administrator.    * TODO: Support merge and not only replace when adding a constraint.    *    * @param sourceTags the allocation tags that will enable this constraint    * @param placementConstraint the constraint    * @param replace if true, an existing constraint for these tags will be    *          replaced by the given one    */
DECL|method|addGlobalConstraint (Set<String> sourceTags, PlacementConstraint placementConstraint, boolean replace)
name|void
name|addGlobalConstraint
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|sourceTags
parameter_list|,
name|PlacementConstraint
name|placementConstraint
parameter_list|,
name|boolean
name|replace
parameter_list|)
function_decl|;
comment|/**    * Retrieve all constraints for a given application, along with the allocation    * tags that enable each constraint.    *    * @param appId the application ID    * @return the constraints for this application with the associated tags    */
DECL|method|getConstraints (ApplicationId appId)
name|Map
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|,
name|PlacementConstraint
argument_list|>
name|getConstraints
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
function_decl|;
comment|/**    * Retrieve the placement constraint that is associated with a set of    * allocation tags for a given application.    *    * @param appId the application ID    * @param sourceTags the allocation tags that enable this constraint    * @return the constraint    */
DECL|method|getConstraint (ApplicationId appId, Set<String> sourceTags)
name|PlacementConstraint
name|getConstraint
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|sourceTags
parameter_list|)
function_decl|;
comment|/**    * Retrieve a global constraint that is associated with a given set of    * allocation tags.    *    * @param sourceTags the allocation tags that enable this constraint    * @return the constraint    */
DECL|method|getGlobalConstraint (Set<String> sourceTags)
name|PlacementConstraint
name|getGlobalConstraint
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|sourceTags
parameter_list|)
function_decl|;
comment|/**    * Remove the constraints that correspond to a given application.    *    * @param appId the application that will be removed.    */
DECL|method|unregisterApplication (ApplicationId appId)
name|void
name|unregisterApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
function_decl|;
comment|/**    * Remove a global constraint that is associated with the given allocation    * tags.    *    * @param sourceTags the allocation tags    */
DECL|method|removeGlobalConstraint (Set<String> sourceTags)
name|void
name|removeGlobalConstraint
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|sourceTags
parameter_list|)
function_decl|;
comment|/**    * Returns the number of currently registered applications in the Placement    * Constraint Manager.    *    * @return number of registered applications.    */
DECL|method|getNumRegisteredApplications ()
name|int
name|getNumRegisteredApplications
parameter_list|()
function_decl|;
comment|/**    * Returns the number of global constraints registered in the Placement    * Constraint Manager.    *    * @return number of global constraints.    */
DECL|method|getNumGlobalConstraints ()
name|int
name|getNumGlobalConstraints
parameter_list|()
function_decl|;
comment|/**    * Validate a placement constraint and the set of allocation tags that will    * enable it.    *    * @param sourceTags the associated allocation tags    * @param placementConstraint the constraint    * @return true if constraint and tags are valid    */
DECL|method|validateConstraint (Set<String> sourceTags, PlacementConstraint placementConstraint)
specifier|default
name|boolean
name|validateConstraint
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|sourceTags
parameter_list|,
name|PlacementConstraint
name|placementConstraint
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
end_interface

end_unit

