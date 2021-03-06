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
name|Resource
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
name|ResourceProto
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
name|ResourceRequestProto
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
name|ResourceRequestProtoOrBuilder
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ResourceRequestPBImpl
specifier|public
class|class
name|ResourceRequestPBImpl
extends|extends
name|ResourceRequest
block|{
DECL|field|proto
name|ResourceRequestProto
name|proto
init|=
name|ResourceRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ResourceRequestProto
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
DECL|field|capability
specifier|private
name|Resource
name|capability
init|=
literal|null
decl_stmt|;
DECL|field|executionTypeRequest
specifier|private
name|ExecutionTypeRequest
name|executionTypeRequest
init|=
literal|null
decl_stmt|;
DECL|method|ResourceRequestPBImpl ()
specifier|public
name|ResourceRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ResourceRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ResourceRequestPBImpl (ResourceRequestProto proto)
specifier|public
name|ResourceRequestPBImpl
parameter_list|(
name|ResourceRequestProto
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
name|ResourceRequestProto
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
name|capability
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setCapability
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|capability
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|executionTypeRequest
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setExecutionTypeRequest
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|executionTypeRequest
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
name|maybeInitBuilder
argument_list|()
expr_stmt|;
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
name|ResourceRequestProto
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
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
name|ResourceRequestProtoOrBuilder
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
name|builder
operator|.
name|clearPriority
argument_list|()
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
DECL|method|getExecutionTypeRequest ()
specifier|public
name|ExecutionTypeRequest
name|getExecutionTypeRequest
parameter_list|()
block|{
name|ResourceRequestProtoOrBuilder
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
name|executionTypeRequest
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|executionTypeRequest
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasExecutionTypeRequest
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|executionTypeRequest
operator|=
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getExecutionTypeRequest
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|executionTypeRequest
return|;
block|}
DECL|method|setExecutionTypeRequest (ExecutionTypeRequest execSpec)
specifier|public
name|void
name|setExecutionTypeRequest
parameter_list|(
name|ExecutionTypeRequest
name|execSpec
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|execSpec
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearExecutionTypeRequest
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|executionTypeRequest
operator|=
name|execSpec
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResourceName ()
specifier|public
name|String
name|getResourceName
parameter_list|()
block|{
name|ResourceRequestProtoOrBuilder
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
operator|!
name|p
operator|.
name|hasResourceName
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getResourceName
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setResourceName (String resourceName)
specifier|public
name|void
name|setResourceName
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|resourceName
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearResourceName
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setResourceName
argument_list|(
operator|(
name|resourceName
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCapability ()
specifier|public
name|Resource
name|getCapability
parameter_list|()
block|{
name|ResourceRequestProtoOrBuilder
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
name|capability
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|capability
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasCapability
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|capability
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|capability
return|;
block|}
annotation|@
name|Override
DECL|method|setCapability (Resource capability)
specifier|public
name|void
name|setCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|capability
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearCapability
argument_list|()
expr_stmt|;
name|this
operator|.
name|capability
operator|=
name|capability
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumContainers ()
specifier|public
specifier|synchronized
name|int
name|getNumContainers
parameter_list|()
block|{
name|ResourceRequestProtoOrBuilder
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
name|getNumContainers
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNumContainers (int numContainers)
specifier|public
specifier|synchronized
name|void
name|setNumContainers
parameter_list|(
name|int
name|numContainers
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setNumContainers
argument_list|(
operator|(
name|numContainers
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRelaxLocality ()
specifier|public
name|boolean
name|getRelaxLocality
parameter_list|()
block|{
name|ResourceRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getRelaxLocality
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setRelaxLocality (boolean relaxLocality)
specifier|public
name|void
name|setRelaxLocality
parameter_list|(
name|boolean
name|relaxLocality
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setRelaxLocality
argument_list|(
name|relaxLocality
argument_list|)
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
name|ResourceRequestProtoOrBuilder
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
DECL|method|setAllocationRequestId (long allocationRequestID)
specifier|public
name|void
name|setAllocationRequestId
parameter_list|(
name|long
name|allocationRequestID
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setAllocationRequestId
argument_list|(
name|allocationRequestID
argument_list|)
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
DECL|method|convertToProtoFormat (Priority t)
specifier|private
name|PriorityProto
name|convertToProtoFormat
parameter_list|(
name|Priority
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PriorityPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (ResourceProto p)
specifier|private
name|ResourcePBImpl
name|convertFromProtoFormat
parameter_list|(
name|ResourceProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ResourcePBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Resource t)
specifier|private
name|ResourceProto
name|convertToProtoFormat
parameter_list|(
name|Resource
name|t
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|t
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{AllocationRequestId: "
operator|+
name|getAllocationRequestId
argument_list|()
operator|+
literal|", Priority: "
operator|+
name|getPriority
argument_list|()
operator|+
literal|", Capability: "
operator|+
name|getCapability
argument_list|()
operator|+
literal|", # Containers: "
operator|+
name|getNumContainers
argument_list|()
operator|+
literal|", Location: "
operator|+
name|getResourceName
argument_list|()
operator|+
literal|", Relax Locality: "
operator|+
name|getRelaxLocality
argument_list|()
operator|+
literal|", Execution Type Request: "
operator|+
name|getExecutionTypeRequest
argument_list|()
operator|+
literal|", Node Label Expression: "
operator|+
name|getNodeLabelExpression
argument_list|()
operator|+
literal|"}"
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeLabelExpression ()
specifier|public
name|String
name|getNodeLabelExpression
parameter_list|()
block|{
name|ResourceRequestProtoOrBuilder
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
operator|!
name|p
operator|.
name|hasNodeLabelExpression
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getNodeLabelExpression
argument_list|()
operator|.
name|trim
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeLabelExpression (String nodeLabelExpression)
specifier|public
name|void
name|setNodeLabelExpression
parameter_list|(
name|String
name|nodeLabelExpression
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeLabelExpression
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeLabelExpression
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setNodeLabelExpression
argument_list|(
name|nodeLabelExpression
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

