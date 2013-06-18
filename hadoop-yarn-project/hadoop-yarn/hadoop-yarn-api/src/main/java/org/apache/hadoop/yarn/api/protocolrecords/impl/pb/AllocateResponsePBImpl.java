begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords.impl.pb
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
name|protocolrecords
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
name|ArrayList
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|AMCommand
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
name|ContainerStatus
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
name|NMToken
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
name|NodeReport
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
name|PreemptionMessage
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
name|impl
operator|.
name|pb
operator|.
name|ContainerPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ContainerStatusPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|NMTokenPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|NodeReportPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|PreemptionMessagePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ProtoUtils
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
name|impl
operator|.
name|pb
operator|.
name|ResourcePBImpl
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
name|ContainerStatusProto
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
name|NodeReportProto
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
name|PreemptionMessageProto
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
name|YarnServiceProtos
operator|.
name|AllocateResponseProto
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
name|YarnServiceProtos
operator|.
name|AllocateResponseProtoOrBuilder
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
name|YarnServiceProtos
operator|.
name|NMTokenProto
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AllocateResponsePBImpl
specifier|public
class|class
name|AllocateResponsePBImpl
extends|extends
name|AllocateResponse
block|{
DECL|field|proto
name|AllocateResponseProto
name|proto
init|=
name|AllocateResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|AllocateResponseProto
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
DECL|field|limit
name|Resource
name|limit
decl_stmt|;
DECL|field|allocatedContainers
specifier|private
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
init|=
literal|null
decl_stmt|;
DECL|field|nmTokens
specifier|private
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
init|=
literal|null
decl_stmt|;
DECL|field|completedContainersStatuses
specifier|private
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainersStatuses
init|=
literal|null
decl_stmt|;
DECL|field|updatedNodes
specifier|private
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
init|=
literal|null
decl_stmt|;
DECL|field|preempt
specifier|private
name|PreemptionMessage
name|preempt
decl_stmt|;
DECL|method|AllocateResponsePBImpl ()
specifier|public
name|AllocateResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|AllocateResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|AllocateResponsePBImpl (AllocateResponseProto proto)
specifier|public
name|AllocateResponsePBImpl
parameter_list|(
name|AllocateResponseProto
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
specifier|synchronized
name|AllocateResponseProto
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
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|", "
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
return|;
block|}
DECL|method|mergeLocalToBuilder ()
specifier|private
specifier|synchronized
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|allocatedContainers
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAllocatedContainers
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|ContainerProto
argument_list|>
name|iterable
init|=
name|getProtoIterable
argument_list|(
name|this
operator|.
name|allocatedContainers
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAllAllocatedContainers
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nmTokens
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNmTokens
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|NMTokenProto
argument_list|>
name|iterable
init|=
name|getTokenProtoIterable
argument_list|(
name|nmTokens
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAllNmTokens
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|completedContainersStatuses
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearCompletedContainerStatuses
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|ContainerStatusProto
argument_list|>
name|iterable
init|=
name|getContainerStatusProtoIterable
argument_list|(
name|this
operator|.
name|completedContainersStatuses
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAllCompletedContainerStatuses
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|updatedNodes
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearUpdatedNodes
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|NodeReportProto
argument_list|>
name|iterable
init|=
name|getNodeReportProtoIterable
argument_list|(
name|this
operator|.
name|updatedNodes
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAllUpdatedNodes
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|limit
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setLimit
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|limit
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|preempt
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setPreempt
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|preempt
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|mergeLocalToProto ()
specifier|private
specifier|synchronized
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
specifier|synchronized
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
name|AllocateResponseProto
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
DECL|method|getAMCommand ()
specifier|public
specifier|synchronized
name|AMCommand
name|getAMCommand
parameter_list|()
block|{
name|AllocateResponseProtoOrBuilder
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
name|hasAMCommand
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getAMCommand
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setAMCommand (AMCommand command)
specifier|public
specifier|synchronized
name|void
name|setAMCommand
parameter_list|(
name|AMCommand
name|command
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAMCommand
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setAMCommand
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|command
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResponseId ()
specifier|public
specifier|synchronized
name|int
name|getResponseId
parameter_list|()
block|{
name|AllocateResponseProtoOrBuilder
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
name|getResponseId
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setResponseId (int responseId)
specifier|public
specifier|synchronized
name|void
name|setResponseId
parameter_list|(
name|int
name|responseId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setResponseId
argument_list|(
operator|(
name|responseId
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAvailableResources ()
specifier|public
specifier|synchronized
name|Resource
name|getAvailableResources
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|limit
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|limit
return|;
block|}
name|AllocateResponseProtoOrBuilder
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
name|hasLimit
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|limit
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|limit
return|;
block|}
annotation|@
name|Override
DECL|method|setAvailableResources (Resource limit)
specifier|public
specifier|synchronized
name|void
name|setAvailableResources
parameter_list|(
name|Resource
name|limit
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|limit
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearLimit
argument_list|()
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUpdatedNodes ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getUpdatedNodes
parameter_list|()
block|{
name|initLocalNewNodeReportList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|updatedNodes
return|;
block|}
annotation|@
name|Override
DECL|method|setUpdatedNodes ( final List<NodeReport> updatedNodes)
specifier|public
specifier|synchronized
name|void
name|setUpdatedNodes
parameter_list|(
specifier|final
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|)
block|{
if|if
condition|(
name|updatedNodes
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|updatedNodes
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|updatedNodes
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|(
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|updatedNodes
operator|.
name|addAll
argument_list|(
name|updatedNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAllocatedContainers ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|Container
argument_list|>
name|getAllocatedContainers
parameter_list|()
block|{
name|initLocalNewContainerList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|allocatedContainers
return|;
block|}
annotation|@
name|Override
DECL|method|setAllocatedContainers ( final List<Container> containers)
specifier|public
specifier|synchronized
name|void
name|setAllocatedContainers
parameter_list|(
specifier|final
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
block|{
if|if
condition|(
name|containers
operator|==
literal|null
condition|)
return|return;
comment|// this looks like a bug because it results in append and not set
name|initLocalNewContainerList
argument_list|()
expr_stmt|;
name|allocatedContainers
operator|.
name|addAll
argument_list|(
name|containers
argument_list|)
expr_stmt|;
block|}
comment|//// Finished containers
annotation|@
name|Override
DECL|method|getCompletedContainersStatuses ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getCompletedContainersStatuses
parameter_list|()
block|{
name|initLocalFinishedContainerList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|completedContainersStatuses
return|;
block|}
annotation|@
name|Override
DECL|method|setCompletedContainersStatuses ( final List<ContainerStatus> containers)
specifier|public
specifier|synchronized
name|void
name|setCompletedContainersStatuses
parameter_list|(
specifier|final
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|containers
parameter_list|)
block|{
if|if
condition|(
name|containers
operator|==
literal|null
condition|)
return|return;
name|initLocalFinishedContainerList
argument_list|()
expr_stmt|;
name|completedContainersStatuses
operator|.
name|addAll
argument_list|(
name|containers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNMTokens (List<NMToken> nmTokens)
specifier|public
specifier|synchronized
name|void
name|setNMTokens
parameter_list|(
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
parameter_list|)
block|{
if|if
condition|(
name|nmTokens
operator|==
literal|null
operator|||
name|nmTokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|nmTokens
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nmTokens
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|clearNmTokens
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// Implementing it as an append rather than set for consistency
name|initLocalNewNMTokenList
argument_list|()
expr_stmt|;
name|this
operator|.
name|nmTokens
operator|.
name|addAll
argument_list|(
name|nmTokens
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNMTokens ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|NMToken
argument_list|>
name|getNMTokens
parameter_list|()
block|{
name|initLocalNewNMTokenList
argument_list|()
expr_stmt|;
return|return
name|nmTokens
return|;
block|}
annotation|@
name|Override
DECL|method|getNumClusterNodes ()
specifier|public
specifier|synchronized
name|int
name|getNumClusterNodes
parameter_list|()
block|{
name|AllocateResponseProtoOrBuilder
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
name|getNumClusterNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumClusterNodes (int numNodes)
specifier|public
specifier|synchronized
name|void
name|setNumClusterNodes
parameter_list|(
name|int
name|numNodes
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setNumClusterNodes
argument_list|(
name|numNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPreemptionMessage ()
specifier|public
specifier|synchronized
name|PreemptionMessage
name|getPreemptionMessage
parameter_list|()
block|{
name|AllocateResponseProtoOrBuilder
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
name|preempt
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|preempt
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasPreempt
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|preempt
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getPreempt
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|preempt
return|;
block|}
annotation|@
name|Override
DECL|method|setPreemptionMessage (PreemptionMessage preempt)
specifier|public
specifier|synchronized
name|void
name|setPreemptionMessage
parameter_list|(
name|PreemptionMessage
name|preempt
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|preempt
condition|)
block|{
name|builder
operator|.
name|clearPreempt
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|preempt
operator|=
name|preempt
expr_stmt|;
block|}
comment|// Once this is called. updatedNodes will never be null - until a getProto is
comment|// called.
DECL|method|initLocalNewNodeReportList ()
specifier|private
specifier|synchronized
name|void
name|initLocalNewNodeReportList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|updatedNodes
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AllocateResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|List
argument_list|<
name|NodeReportProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getUpdatedNodesList
argument_list|()
decl_stmt|;
name|updatedNodes
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeReportProto
name|n
range|:
name|list
control|)
block|{
name|updatedNodes
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Once this is called. containerList will never be null - until a getProto
comment|// is called.
DECL|method|initLocalNewContainerList ()
specifier|private
specifier|synchronized
name|void
name|initLocalNewContainerList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|allocatedContainers
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AllocateResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|List
argument_list|<
name|ContainerProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getAllocatedContainersList
argument_list|()
decl_stmt|;
name|allocatedContainers
operator|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerProto
name|c
range|:
name|list
control|)
block|{
name|allocatedContainers
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initLocalNewNMTokenList ()
specifier|private
specifier|synchronized
name|void
name|initLocalNewNMTokenList
parameter_list|()
block|{
if|if
condition|(
name|nmTokens
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AllocateResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|List
argument_list|<
name|NMTokenProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getNmTokensList
argument_list|()
decl_stmt|;
name|nmTokens
operator|=
operator|new
name|ArrayList
argument_list|<
name|NMToken
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|NMTokenProto
name|t
range|:
name|list
control|)
block|{
name|nmTokens
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getProtoIterable ( final List<Container> newContainersList)
specifier|private
specifier|synchronized
name|Iterable
argument_list|<
name|ContainerProto
argument_list|>
name|getProtoIterable
parameter_list|(
specifier|final
name|List
argument_list|<
name|Container
argument_list|>
name|newContainersList
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|ContainerProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|ContainerProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ContainerProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|Container
argument_list|>
name|iter
init|=
name|newContainersList
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|ContainerProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|method|getTokenProtoIterable ( final List<NMToken> nmTokenList)
specifier|private
specifier|synchronized
name|Iterable
argument_list|<
name|NMTokenProto
argument_list|>
name|getTokenProtoIterable
parameter_list|(
specifier|final
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokenList
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|NMTokenProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|NMTokenProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|NMTokenProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|NMToken
argument_list|>
name|iter
init|=
name|nmTokenList
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NMTokenProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|synchronized
name|Iterable
argument_list|<
name|ContainerStatusProto
argument_list|>
DECL|method|getContainerStatusProtoIterable ( final List<ContainerStatus> newContainersList)
name|getContainerStatusProtoIterable
parameter_list|(
specifier|final
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|newContainersList
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|ContainerStatusProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|ContainerStatusProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ContainerStatusProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ContainerStatus
argument_list|>
name|iter
init|=
name|newContainersList
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|ContainerStatusProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|synchronized
name|Iterable
argument_list|<
name|NodeReportProto
argument_list|>
DECL|method|getNodeReportProtoIterable ( final List<NodeReport> newNodeReportsList)
name|getNodeReportProtoIterable
parameter_list|(
specifier|final
name|List
argument_list|<
name|NodeReport
argument_list|>
name|newNodeReportsList
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|NodeReportProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|NodeReportProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|NodeReportProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|NodeReport
argument_list|>
name|iter
init|=
name|newNodeReportsList
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|NodeReportProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|// Once this is called. containerList will never be null - until a getProto
comment|// is called.
DECL|method|initLocalFinishedContainerList ()
specifier|private
specifier|synchronized
name|void
name|initLocalFinishedContainerList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|completedContainersStatuses
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AllocateResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|List
argument_list|<
name|ContainerStatusProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getCompletedContainerStatusesList
argument_list|()
decl_stmt|;
name|completedContainersStatuses
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerStatusProto
name|c
range|:
name|list
control|)
block|{
name|completedContainersStatuses
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|convertFromProtoFormat ( NodeReportProto p)
specifier|private
specifier|synchronized
name|NodeReportPBImpl
name|convertFromProtoFormat
parameter_list|(
name|NodeReportProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeReportPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (NodeReport t)
specifier|private
specifier|synchronized
name|NodeReportProto
name|convertToProtoFormat
parameter_list|(
name|NodeReport
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeReportPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( ContainerProto p)
specifier|private
specifier|synchronized
name|ContainerPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Container t)
specifier|private
specifier|synchronized
name|ContainerProto
name|convertToProtoFormat
parameter_list|(
name|Container
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( ContainerStatusProto p)
specifier|private
specifier|synchronized
name|ContainerStatusPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerStatusProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerStatusPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat ( ContainerStatus t)
specifier|private
specifier|synchronized
name|ContainerStatusProto
name|convertToProtoFormat
parameter_list|(
name|ContainerStatus
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerStatusPBImpl
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
specifier|synchronized
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
DECL|method|convertToProtoFormat (Resource r)
specifier|private
specifier|synchronized
name|ResourceProto
name|convertToProtoFormat
parameter_list|(
name|Resource
name|r
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|r
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (PreemptionMessageProto p)
specifier|private
specifier|synchronized
name|PreemptionMessagePBImpl
name|convertFromProtoFormat
parameter_list|(
name|PreemptionMessageProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|PreemptionMessagePBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (PreemptionMessage r)
specifier|private
specifier|synchronized
name|PreemptionMessageProto
name|convertToProtoFormat
parameter_list|(
name|PreemptionMessage
name|r
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PreemptionMessagePBImpl
operator|)
name|r
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertToProtoFormat (NMToken token)
specifier|private
specifier|synchronized
name|NMTokenProto
name|convertToProtoFormat
parameter_list|(
name|NMToken
name|token
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NMTokenPBImpl
operator|)
name|token
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (NMTokenProto proto)
specifier|private
specifier|synchronized
name|NMToken
name|convertFromProtoFormat
parameter_list|(
name|NMTokenProto
name|proto
parameter_list|)
block|{
return|return
operator|new
name|NMTokenPBImpl
argument_list|(
name|proto
argument_list|)
return|;
block|}
block|}
end_class

end_unit

