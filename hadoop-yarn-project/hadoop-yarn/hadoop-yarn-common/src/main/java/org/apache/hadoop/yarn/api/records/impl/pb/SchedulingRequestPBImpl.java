begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.impl.pb
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
name|records
operator|.
name|impl
operator|.
name|pb
package|;
end_package

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
name|pb
operator|.
name|PlacementConstraintFromProtoConverter
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
name|pb
operator|.
name|PlacementConstraintToProtoConverter
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
name|ResourceSizing
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
name|proto
operator|.
name|YarnProtos
operator|.
name|ExecutionTypeRequestProto
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
name|proto
operator|.
name|YarnProtos
operator|.
name|PlacementConstraintProto
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
name|proto
operator|.
name|YarnProtos
operator|.
name|PriorityProto
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
name|proto
operator|.
name|YarnProtos
operator|.
name|ResourceSizingProto
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
name|proto
operator|.
name|YarnProtos
operator|.
name|SchedulingRequestProto
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
name|proto
operator|.
name|YarnProtos
operator|.
name|SchedulingRequestProtoOrBuilder
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SchedulingRequestPBImpl
specifier|public
class|class
name|SchedulingRequestPBImpl
extends|extends
name|SchedulingRequest
block|{
DECL|field|proto
name|SchedulingRequestProto
name|proto
init|=
name|SchedulingRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|SchedulingRequestProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|field|priority
specifier|private
name|Priority
name|priority
init|=
literal|null
decl_stmt|;
DECL|field|executionType
specifier|private
name|ExecutionTypeRequest
name|executionType
init|=
literal|null
decl_stmt|;
DECL|field|allocationTags
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
init|=
literal|null
decl_stmt|;
DECL|field|resourceSizing
specifier|private
name|ResourceSizing
name|resourceSizing
init|=
literal|null
decl_stmt|;
DECL|field|placementConstraint
specifier|private
name|PlacementConstraint
name|placementConstraint
init|=
literal|null
decl_stmt|;
DECL|method|SchedulingRequestPBImpl ()
specifier|public
name|SchedulingRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|SchedulingRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|SchedulingRequestPBImpl (SchedulingRequestProto proto)
specifier|public
name|SchedulingRequestPBImpl
parameter_list|(
name|SchedulingRequestProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|SchedulingRequestProto
name|getProto
parameter_list|()
block|{
name|mergeLocalToProto
argument_list|()
expr_stmt|;
name|proto
operator|=
name|viaProto
condition|?
name|proto
else|:
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
return|return
name|proto
return|;
block|}
DECL|method|mergeLocalToBuilder ()
specifier|private
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|priority
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setPriority
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|priority
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|executionType
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setExecutionType
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|executionType
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|allocationTags
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAllocationTags
argument_list|()
expr_stmt|;
name|builder
operator|.
name|addAllAllocationTags
argument_list|(
name|this
operator|.
name|allocationTags
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|resourceSizing
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setResourceSizing
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|resourceSizing
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|placementConstraint
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setPlacementConstraint
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|placementConstraint
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|mergeLocalToProto ()
specifier|private
name|void
name|mergeLocalToProto
parameter_list|()
block|{
if|if
condition|(
name|viaProto
condition|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
block|}
name|mergeLocalToBuilder
argument_list|()
expr_stmt|;
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|maybeInitBuilder ()
specifier|private
name|void
name|maybeInitBuilder
parameter_list|()
block|{
if|if
condition|(
name|viaProto
operator|||
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
name|SchedulingRequestProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
name|viaProto
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAllocationRequestId ()
specifier|public
name|long
name|getAllocationRequestId
parameter_list|()
block|{
name|SchedulingRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
operator|(
name|p
operator|.
name|getAllocationRequestId
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setAllocationRequestId (long allocationRequestId)
specifier|public
name|void
name|setAllocationRequestId
parameter_list|(
name|long
name|allocationRequestId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setAllocationRequestId
argument_list|(
name|allocationRequestId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
name|SchedulingRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|priority
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|priority
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasPriority
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|priority
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|priority
return|;
block|}
annotation|@
name|Override
DECL|method|setPriority (Priority priority)
specifier|public
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|priority
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearPriority
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExecutionType ()
specifier|public
name|ExecutionTypeRequest
name|getExecutionType
parameter_list|()
block|{
name|SchedulingRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|executionType
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|executionType
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasExecutionType
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|executionType
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getExecutionType
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|executionType
return|;
block|}
annotation|@
name|Override
DECL|method|setExecutionType (ExecutionTypeRequest executionType)
specifier|public
name|void
name|setExecutionType
parameter_list|(
name|ExecutionTypeRequest
name|executionType
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|executionType
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearExecutionType
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|executionType
operator|=
name|executionType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAllocationTags ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAllocationTags
parameter_list|()
block|{
name|initAllocationTags
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|allocationTags
return|;
block|}
annotation|@
name|Override
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
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearAllocationTags
argument_list|()
expr_stmt|;
name|this
operator|.
name|allocationTags
operator|=
name|allocationTags
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResourceSizing ()
specifier|public
name|ResourceSizing
name|getResourceSizing
parameter_list|()
block|{
name|SchedulingRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|resourceSizing
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|resourceSizing
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasResourceSizing
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|resourceSizing
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getResourceSizing
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|resourceSizing
return|;
block|}
annotation|@
name|Override
DECL|method|setResourceSizing (ResourceSizing resourceSizing)
specifier|public
name|void
name|setResourceSizing
parameter_list|(
name|ResourceSizing
name|resourceSizing
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|resourceSizing
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearResourceSizing
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|resourceSizing
operator|=
name|resourceSizing
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPlacementConstraint ()
specifier|public
name|PlacementConstraint
name|getPlacementConstraint
parameter_list|()
block|{
name|SchedulingRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|placementConstraint
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|placementConstraint
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasPlacementConstraint
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|placementConstraint
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getPlacementConstraint
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|placementConstraint
return|;
block|}
annotation|@
name|Override
DECL|method|setPlacementConstraint (PlacementConstraint placementConstraint)
specifier|public
name|void
name|setPlacementConstraint
parameter_list|(
name|PlacementConstraint
name|placementConstraint
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|placementConstraint
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearPlacementConstraint
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|placementConstraint
operator|=
name|placementConstraint
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (PriorityProto p)
specifier|private
name|PriorityPBImpl
name|convertFromProtoFormat
parameter_list|(
name|PriorityProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|PriorityPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Priority p)
specifier|private
name|PriorityProto
name|convertToProtoFormat
parameter_list|(
name|Priority
name|p
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PriorityPBImpl
operator|)
name|p
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( ExecutionTypeRequestProto p)
specifier|private
name|ExecutionTypeRequestPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ExecutionTypeRequestProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ExecutionTypeRequestPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat ( ExecutionTypeRequest p)
specifier|private
name|ExecutionTypeRequestProto
name|convertToProtoFormat
parameter_list|(
name|ExecutionTypeRequest
name|p
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ExecutionTypeRequestPBImpl
operator|)
name|p
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (ResourceSizingProto p)
specifier|private
name|ResourceSizingPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ResourceSizingProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ResourceSizingPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ResourceSizing p)
specifier|private
name|ResourceSizingProto
name|convertToProtoFormat
parameter_list|(
name|ResourceSizing
name|p
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ResourceSizingPBImpl
operator|)
name|p
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( PlacementConstraintProto c)
specifier|private
name|PlacementConstraint
name|convertFromProtoFormat
parameter_list|(
name|PlacementConstraintProto
name|c
parameter_list|)
block|{
name|PlacementConstraintFromProtoConverter
name|fromProtoConverter
init|=
operator|new
name|PlacementConstraintFromProtoConverter
argument_list|(
name|c
argument_list|)
decl_stmt|;
return|return
name|fromProtoConverter
operator|.
name|convert
argument_list|()
return|;
block|}
DECL|method|convertToProtoFormat (PlacementConstraint c)
specifier|private
name|PlacementConstraintProto
name|convertToProtoFormat
parameter_list|(
name|PlacementConstraint
name|c
parameter_list|)
block|{
name|PlacementConstraintToProtoConverter
name|toProtoConverter
init|=
operator|new
name|PlacementConstraintToProtoConverter
argument_list|(
name|c
argument_list|)
decl_stmt|;
return|return
name|toProtoConverter
operator|.
name|convert
argument_list|()
return|;
block|}
DECL|method|initAllocationTags ()
specifier|private
name|void
name|initAllocationTags
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|allocationTags
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|SchedulingRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|this
operator|.
name|allocationTags
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|allocationTags
operator|.
name|addAll
argument_list|(
name|p
operator|.
name|getAllocationTagsList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

