begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|security
operator|.
name|proto
operator|.
name|SecurityProtos
operator|.
name|TokenProto
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
name|Container
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
name|ContainerId
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
name|ExecutionType
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
name|NodeId
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
name|Token
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
name|ContainerIdProto
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
name|ContainerProto
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
name|ContainerProtoOrBuilder
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
name|NodeIdProto
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
name|ExecutionTypeProto
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
name|Set
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ContainerPBImpl
specifier|public
class|class
name|ContainerPBImpl
extends|extends
name|Container
block|{
DECL|field|proto
name|ContainerProto
name|proto
init|=
name|ContainerProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ContainerProto
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
DECL|field|containerId
specifier|private
name|ContainerId
name|containerId
init|=
literal|null
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
init|=
literal|null
decl_stmt|;
DECL|field|resource
specifier|private
name|Resource
name|resource
init|=
literal|null
decl_stmt|;
DECL|field|priority
specifier|private
name|Priority
name|priority
init|=
literal|null
decl_stmt|;
DECL|field|containerToken
specifier|private
name|Token
name|containerToken
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
DECL|method|ContainerPBImpl ()
specifier|public
name|ContainerPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ContainerProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ContainerPBImpl (ContainerProto proto)
specifier|public
name|ContainerPBImpl
parameter_list|(
name|ContainerProto
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
name|ContainerProto
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
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
operator|.
name|getProto
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
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
name|containerId
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ContainerIdPBImpl
operator|)
name|containerId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|nodeId
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|NodeIdPBImpl
operator|)
name|nodeId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setNodeId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|resource
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setResource
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|resource
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|priority
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|PriorityPBImpl
operator|)
name|this
operator|.
name|priority
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getPriority
argument_list|()
argument_list|)
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
name|containerToken
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|TokenPBImpl
operator|)
name|this
operator|.
name|containerToken
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getContainerToken
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setContainerToken
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|containerToken
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
name|ContainerProto
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
DECL|method|getId ()
specifier|public
name|ContainerId
name|getId
parameter_list|()
block|{
name|ContainerProtoOrBuilder
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
name|containerId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|containerId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|containerId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|containerId
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeId (NodeId nodeId)
specifier|public
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearNodeId
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
name|ContainerProtoOrBuilder
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
name|nodeId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|nodeId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasNodeId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|nodeId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|nodeId
return|;
block|}
annotation|@
name|Override
DECL|method|setId (ContainerId id)
specifier|public
name|void
name|setId
parameter_list|(
name|ContainerId
name|id
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearId
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeHttpAddress ()
specifier|public
name|String
name|getNodeHttpAddress
parameter_list|()
block|{
name|ContainerProtoOrBuilder
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
name|hasNodeHttpAddress
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
name|getNodeHttpAddress
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeHttpAddress (String nodeHttpAddress)
specifier|public
name|void
name|setNodeHttpAddress
parameter_list|(
name|String
name|nodeHttpAddress
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeHttpAddress
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeHttpAddress
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setNodeHttpAddress
argument_list|(
name|nodeHttpAddress
operator|.
name|intern
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
parameter_list|()
block|{
name|ContainerProtoOrBuilder
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
name|resource
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|resource
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasResource
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|resource
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|resource
return|;
block|}
annotation|@
name|Override
DECL|method|setResource (Resource resource)
specifier|public
name|void
name|setResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearResource
argument_list|()
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|resource
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
name|ContainerProtoOrBuilder
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
DECL|method|getContainerToken ()
specifier|public
name|Token
name|getContainerToken
parameter_list|()
block|{
name|ContainerProtoOrBuilder
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
name|containerToken
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|containerToken
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasContainerToken
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|containerToken
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|containerToken
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerToken (Token containerToken)
specifier|public
name|void
name|setContainerToken
parameter_list|(
name|Token
name|containerToken
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|containerToken
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearContainerToken
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerToken
operator|=
name|containerToken
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExecutionType ()
specifier|public
name|ExecutionType
name|getExecutionType
parameter_list|()
block|{
name|ContainerProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getExecutionType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setExecutionType (ExecutionType executionType)
specifier|public
name|void
name|setExecutionType
parameter_list|(
name|ExecutionType
name|executionType
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setExecutionType
argument_list|(
name|convertToProtoFormat
argument_list|(
name|executionType
argument_list|)
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
name|ContainerProtoOrBuilder
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
annotation|@
name|Override
DECL|method|getVersion ()
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
name|ContainerProtoOrBuilder
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
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setVersion (int version)
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
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
name|ContainerProtoOrBuilder
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
DECL|method|convertFromProtoFormat (ContainerIdProto p)
specifier|private
name|ContainerIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (NodeIdProto p)
specifier|private
name|NodeIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|NodeIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ContainerId t)
specifier|private
name|ContainerIdProto
name|convertToProtoFormat
parameter_list|(
name|ContainerId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerIdPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertToProtoFormat (NodeId t)
specifier|private
name|NodeIdProto
name|convertToProtoFormat
parameter_list|(
name|NodeId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeIdPBImpl
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
DECL|method|convertFromProtoFormat (TokenProto p)
specifier|private
name|TokenPBImpl
name|convertFromProtoFormat
parameter_list|(
name|TokenProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|TokenPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Token t)
specifier|private
name|TokenProto
name|convertToProtoFormat
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TokenPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( ExecutionTypeProto e)
specifier|private
name|ExecutionType
name|convertFromProtoFormat
parameter_list|(
name|ExecutionTypeProto
name|e
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|e
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ExecutionType e)
specifier|private
name|ExecutionTypeProto
name|convertToProtoFormat
parameter_list|(
name|ExecutionType
name|e
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|e
argument_list|)
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Container: ["
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ContainerId: "
argument_list|)
operator|.
name|append
argument_list|(
name|getId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"AllocationRequestId: "
argument_list|)
operator|.
name|append
argument_list|(
name|getAllocationRequestId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Version: "
argument_list|)
operator|.
name|append
argument_list|(
name|getVersion
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"NodeId: "
argument_list|)
operator|.
name|append
argument_list|(
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"NodeHttpAddress: "
argument_list|)
operator|.
name|append
argument_list|(
name|getNodeHttpAddress
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Resource: "
argument_list|)
operator|.
name|append
argument_list|(
name|getResource
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Priority: "
argument_list|)
operator|.
name|append
argument_list|(
name|getPriority
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Token: "
argument_list|)
operator|.
name|append
argument_list|(
name|getContainerToken
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ExecutionType: "
argument_list|)
operator|.
name|append
argument_list|(
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//TODO Comparator
annotation|@
name|Override
DECL|method|compareTo (Container other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Container
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getId
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|getNodeId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
name|this
operator|.
name|getResource
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getResource
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|getNodeId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getNodeId
argument_list|()
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
name|this
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

