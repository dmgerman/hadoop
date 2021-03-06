begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
package|;
end_package

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
name|ExecutionTypeRequest
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
name|Priority
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
name|ResourceRequest
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
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
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
comment|/**  * Simple class representing a resource request.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"resourceRequests"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ResourceRequestInfo
specifier|public
class|class
name|ResourceRequestInfo
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"priority"
argument_list|)
DECL|field|priority
specifier|private
name|int
name|priority
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"allocationRequestId"
argument_list|)
DECL|field|allocationRequestId
specifier|private
name|long
name|allocationRequestId
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"resourceName"
argument_list|)
DECL|field|resourceName
specifier|private
name|String
name|resourceName
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"capability"
argument_list|)
DECL|field|capability
specifier|private
name|ResourceInfo
name|capability
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"numContainers"
argument_list|)
DECL|field|numContainers
specifier|private
name|int
name|numContainers
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"relaxLocality"
argument_list|)
DECL|field|relaxLocality
specifier|private
name|boolean
name|relaxLocality
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"nodeLabelExpression"
argument_list|)
DECL|field|nodeLabelExpression
specifier|private
name|String
name|nodeLabelExpression
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"executionTypeRequest"
argument_list|)
DECL|field|executionTypeRequest
specifier|private
name|ExecutionTypeRequestInfo
name|executionTypeRequest
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"placementConstraint"
argument_list|)
DECL|field|placementConstraint
specifier|private
name|String
name|placementConstraint
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"allocationTags"
argument_list|)
DECL|field|allocationTags
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
decl_stmt|;
DECL|method|ResourceRequestInfo ()
specifier|public
name|ResourceRequestInfo
parameter_list|()
block|{   }
DECL|method|ResourceRequestInfo (ResourceRequest request)
specifier|public
name|ResourceRequestInfo
parameter_list|(
name|ResourceRequest
name|request
parameter_list|)
block|{
name|priority
operator|=
name|request
operator|.
name|getPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
expr_stmt|;
name|allocationRequestId
operator|=
name|request
operator|.
name|getAllocationRequestId
argument_list|()
expr_stmt|;
name|resourceName
operator|=
name|request
operator|.
name|getResourceName
argument_list|()
expr_stmt|;
name|capability
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
name|numContainers
operator|=
name|request
operator|.
name|getNumContainers
argument_list|()
expr_stmt|;
name|relaxLocality
operator|=
name|request
operator|.
name|getRelaxLocality
argument_list|()
expr_stmt|;
name|nodeLabelExpression
operator|=
name|request
operator|.
name|getNodeLabelExpression
argument_list|()
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getExecutionTypeRequest
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|executionTypeRequest
operator|=
operator|new
name|ExecutionTypeRequestInfo
argument_list|(
name|request
operator|.
name|getExecutionTypeRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|ResourceRequestInfo (SchedulingRequest request)
specifier|public
name|ResourceRequestInfo
parameter_list|(
name|SchedulingRequest
name|request
parameter_list|)
block|{
name|priority
operator|=
name|request
operator|.
name|getPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
expr_stmt|;
name|allocationRequestId
operator|=
name|request
operator|.
name|getAllocationRequestId
argument_list|()
expr_stmt|;
name|capability
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|request
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getResources
argument_list|()
argument_list|)
expr_stmt|;
name|numContainers
operator|=
name|request
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getExecutionType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|executionTypeRequest
operator|=
operator|new
name|ExecutionTypeRequestInfo
argument_list|(
name|request
operator|.
name|getExecutionType
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|allocationTags
operator|=
name|request
operator|.
name|getAllocationTags
argument_list|()
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getPlacementConstraint
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|placementConstraint
operator|=
name|request
operator|.
name|getPlacementConstraint
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
return|return
name|Priority
operator|.
name|newInstance
argument_list|(
name|priority
argument_list|)
return|;
block|}
DECL|method|setPriority (Priority priority)
specifier|public
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
operator|.
name|getPriority
argument_list|()
expr_stmt|;
block|}
DECL|method|getResourceName ()
specifier|public
name|String
name|getResourceName
parameter_list|()
block|{
return|return
name|resourceName
return|;
block|}
DECL|method|setResourceName (String resourceName)
specifier|public
name|void
name|setResourceName
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
name|this
operator|.
name|resourceName
operator|=
name|resourceName
expr_stmt|;
block|}
DECL|method|getCapability ()
specifier|public
name|ResourceInfo
name|getCapability
parameter_list|()
block|{
return|return
name|capability
return|;
block|}
DECL|method|setCapability (ResourceInfo capability)
specifier|public
name|void
name|setCapability
parameter_list|(
name|ResourceInfo
name|capability
parameter_list|)
block|{
name|this
operator|.
name|capability
operator|=
name|capability
expr_stmt|;
block|}
DECL|method|getNumContainers ()
specifier|public
name|int
name|getNumContainers
parameter_list|()
block|{
return|return
name|numContainers
return|;
block|}
DECL|method|setNumContainers (int numContainers)
specifier|public
name|void
name|setNumContainers
parameter_list|(
name|int
name|numContainers
parameter_list|)
block|{
name|this
operator|.
name|numContainers
operator|=
name|numContainers
expr_stmt|;
block|}
DECL|method|getRelaxLocality ()
specifier|public
name|boolean
name|getRelaxLocality
parameter_list|()
block|{
return|return
name|relaxLocality
return|;
block|}
DECL|method|setRelaxLocality (boolean relaxLocality)
specifier|public
name|void
name|setRelaxLocality
parameter_list|(
name|boolean
name|relaxLocality
parameter_list|)
block|{
name|this
operator|.
name|relaxLocality
operator|=
name|relaxLocality
expr_stmt|;
block|}
DECL|method|getNodeLabelExpression ()
specifier|public
name|String
name|getNodeLabelExpression
parameter_list|()
block|{
return|return
name|nodeLabelExpression
return|;
block|}
DECL|method|setNodeLabelExpression (String nodeLabelExpression)
specifier|public
name|void
name|setNodeLabelExpression
parameter_list|(
name|String
name|nodeLabelExpression
parameter_list|)
block|{
name|this
operator|.
name|nodeLabelExpression
operator|=
name|nodeLabelExpression
expr_stmt|;
block|}
DECL|method|setExecutionTypeRequest ( ExecutionTypeRequest executionTypeRequest)
specifier|public
name|void
name|setExecutionTypeRequest
parameter_list|(
name|ExecutionTypeRequest
name|executionTypeRequest
parameter_list|)
block|{
name|this
operator|.
name|executionTypeRequest
operator|=
operator|new
name|ExecutionTypeRequestInfo
argument_list|(
name|executionTypeRequest
argument_list|)
expr_stmt|;
block|}
DECL|method|getExecutionTypeRequest ()
specifier|public
name|ExecutionTypeRequestInfo
name|getExecutionTypeRequest
parameter_list|()
block|{
return|return
name|executionTypeRequest
return|;
block|}
DECL|method|getPlacementConstraint ()
specifier|public
name|String
name|getPlacementConstraint
parameter_list|()
block|{
return|return
name|placementConstraint
return|;
block|}
DECL|method|setPlacementConstraint (String placementConstraint)
specifier|public
name|void
name|setPlacementConstraint
parameter_list|(
name|String
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
DECL|method|getAllocationTags ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAllocationTags
parameter_list|()
block|{
return|return
name|allocationTags
return|;
block|}
DECL|method|setAllocationTags (Set<String> allocationTags)
specifier|public
name|void
name|setAllocationTags
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
parameter_list|)
block|{
name|this
operator|.
name|allocationTags
operator|=
name|allocationTags
expr_stmt|;
block|}
DECL|method|getAllocationRequestId ()
specifier|public
name|long
name|getAllocationRequestId
parameter_list|()
block|{
return|return
name|allocationRequestId
return|;
block|}
DECL|method|setAllocationRequestId (long allocationRequestId)
specifier|public
name|void
name|setAllocationRequestId
parameter_list|(
name|long
name|allocationRequestId
parameter_list|)
block|{
name|this
operator|.
name|allocationRequestId
operator|=
name|allocationRequestId
expr_stmt|;
block|}
block|}
end_class

end_unit

