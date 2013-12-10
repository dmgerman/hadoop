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
name|AllocateRequest
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
name|ContainerResourceIncreaseRequest
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
name|ResourceBlacklistRequest
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
name|impl
operator|.
name|pb
operator|.
name|ContainerIdPBImpl
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
name|ContainerResourceIncreaseRequestPBImpl
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
name|ResourceBlacklistRequestPBImpl
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
name|ResourceRequestPBImpl
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
name|ContainerResourceIncreaseRequestProto
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
name|ResourceBlacklistRequestProto
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
name|YarnServiceProtos
operator|.
name|AllocateRequestProto
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
name|AllocateRequestProtoOrBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AllocateRequestPBImpl
specifier|public
class|class
name|AllocateRequestPBImpl
extends|extends
name|AllocateRequest
block|{
DECL|field|proto
name|AllocateRequestProto
name|proto
init|=
name|AllocateRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|AllocateRequestProto
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
DECL|field|ask
specifier|private
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
init|=
literal|null
decl_stmt|;
DECL|field|release
specifier|private
name|List
argument_list|<
name|ContainerId
argument_list|>
name|release
init|=
literal|null
decl_stmt|;
DECL|field|increaseRequests
specifier|private
name|List
argument_list|<
name|ContainerResourceIncreaseRequest
argument_list|>
name|increaseRequests
init|=
literal|null
decl_stmt|;
DECL|field|blacklistRequest
specifier|private
name|ResourceBlacklistRequest
name|blacklistRequest
init|=
literal|null
decl_stmt|;
DECL|method|AllocateRequestPBImpl ()
specifier|public
name|AllocateRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|AllocateRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|AllocateRequestPBImpl (AllocateRequestProto proto)
specifier|public
name|AllocateRequestPBImpl
parameter_list|(
name|AllocateRequestProto
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
name|AllocateRequestProto
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
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
argument_list|)
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
name|ask
operator|!=
literal|null
condition|)
block|{
name|addAsksToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|release
operator|!=
literal|null
condition|)
block|{
name|addReleasesToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|increaseRequests
operator|!=
literal|null
condition|)
block|{
name|addIncreaseRequestsToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|blacklistRequest
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setBlacklistRequest
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|blacklistRequest
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
name|AllocateRequestProto
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
DECL|method|getResponseId ()
specifier|public
name|int
name|getResponseId
parameter_list|()
block|{
name|AllocateRequestProtoOrBuilder
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
name|getResponseId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setResponseId (int id)
specifier|public
name|void
name|setResponseId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setResponseId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
name|AllocateRequestProtoOrBuilder
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
name|getProgress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setProgress (float progress)
specifier|public
name|void
name|setProgress
parameter_list|(
name|float
name|progress
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProgress
argument_list|(
name|progress
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAskList ()
specifier|public
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getAskList
parameter_list|()
block|{
name|initAsks
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|ask
return|;
block|}
annotation|@
name|Override
DECL|method|setAskList (final List<ResourceRequest> resourceRequests)
specifier|public
name|void
name|setAskList
parameter_list|(
specifier|final
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
parameter_list|)
block|{
if|if
condition|(
name|resourceRequests
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|initAsks
argument_list|()
expr_stmt|;
name|this
operator|.
name|ask
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|ask
operator|.
name|addAll
argument_list|(
name|resourceRequests
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getIncreaseRequests ()
specifier|public
name|List
argument_list|<
name|ContainerResourceIncreaseRequest
argument_list|>
name|getIncreaseRequests
parameter_list|()
block|{
name|initIncreaseRequests
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|increaseRequests
return|;
block|}
annotation|@
name|Override
DECL|method|setIncreaseRequests ( List<ContainerResourceIncreaseRequest> increaseRequests)
specifier|public
name|void
name|setIncreaseRequests
parameter_list|(
name|List
argument_list|<
name|ContainerResourceIncreaseRequest
argument_list|>
name|increaseRequests
parameter_list|)
block|{
if|if
condition|(
name|increaseRequests
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|initIncreaseRequests
argument_list|()
expr_stmt|;
name|this
operator|.
name|increaseRequests
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|increaseRequests
operator|.
name|addAll
argument_list|(
name|increaseRequests
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResourceBlacklistRequest ()
specifier|public
name|ResourceBlacklistRequest
name|getResourceBlacklistRequest
parameter_list|()
block|{
name|AllocateRequestProtoOrBuilder
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
name|blacklistRequest
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|blacklistRequest
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasBlacklistRequest
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|blacklistRequest
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getBlacklistRequest
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|blacklistRequest
return|;
block|}
annotation|@
name|Override
DECL|method|setResourceBlacklistRequest (ResourceBlacklistRequest blacklistRequest)
specifier|public
name|void
name|setResourceBlacklistRequest
parameter_list|(
name|ResourceBlacklistRequest
name|blacklistRequest
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|blacklistRequest
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearBlacklistRequest
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|blacklistRequest
operator|=
name|blacklistRequest
expr_stmt|;
block|}
DECL|method|initAsks ()
specifier|private
name|void
name|initAsks
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|ask
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AllocateRequestProtoOrBuilder
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
name|ResourceRequestProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getAskList
argument_list|()
decl_stmt|;
name|this
operator|.
name|ask
operator|=
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ResourceRequestProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|ask
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
DECL|method|addAsksToProto ()
specifier|private
name|void
name|addAsksToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearAsk
argument_list|()
expr_stmt|;
if|if
condition|(
name|ask
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|ResourceRequestProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|ResourceRequestProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ResourceRequestProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ResourceRequestProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ResourceRequest
argument_list|>
name|iter
init|=
name|ask
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
name|ResourceRequestProto
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
decl_stmt|;
name|builder
operator|.
name|addAllAsk
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|initIncreaseRequests ()
specifier|private
name|void
name|initIncreaseRequests
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|increaseRequests
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AllocateRequestProtoOrBuilder
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
name|ContainerResourceIncreaseRequestProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getIncreaseRequestList
argument_list|()
decl_stmt|;
name|this
operator|.
name|increaseRequests
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContainerResourceIncreaseRequest
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerResourceIncreaseRequestProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|increaseRequests
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
DECL|method|addIncreaseRequestsToProto ()
specifier|private
name|void
name|addIncreaseRequestsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearIncreaseRequest
argument_list|()
expr_stmt|;
if|if
condition|(
name|increaseRequests
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|ContainerResourceIncreaseRequestProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|ContainerResourceIncreaseRequestProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ContainerResourceIncreaseRequestProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ContainerResourceIncreaseRequestProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ContainerResourceIncreaseRequest
argument_list|>
name|iter
init|=
name|increaseRequests
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
name|ContainerResourceIncreaseRequestProto
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
decl_stmt|;
name|builder
operator|.
name|addAllIncreaseRequest
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReleaseList ()
specifier|public
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getReleaseList
parameter_list|()
block|{
name|initReleases
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|release
return|;
block|}
annotation|@
name|Override
DECL|method|setReleaseList (List<ContainerId> releaseContainers)
specifier|public
name|void
name|setReleaseList
parameter_list|(
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releaseContainers
parameter_list|)
block|{
if|if
condition|(
name|releaseContainers
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|initReleases
argument_list|()
expr_stmt|;
name|this
operator|.
name|release
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|release
operator|.
name|addAll
argument_list|(
name|releaseContainers
argument_list|)
expr_stmt|;
block|}
DECL|method|initReleases ()
specifier|private
name|void
name|initReleases
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|release
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AllocateRequestProtoOrBuilder
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
name|ContainerIdProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getReleaseList
argument_list|()
decl_stmt|;
name|this
operator|.
name|release
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerIdProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|release
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
DECL|method|addReleasesToProto ()
specifier|private
name|void
name|addReleasesToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearRelease
argument_list|()
expr_stmt|;
if|if
condition|(
name|release
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|ContainerIdProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|ContainerIdProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ContainerIdProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ContainerIdProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ContainerId
argument_list|>
name|iter
init|=
name|release
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
name|ContainerIdProto
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
decl_stmt|;
name|builder
operator|.
name|addAllRelease
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ResourceRequestProto p)
specifier|private
name|ResourceRequestPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ResourceRequestProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ResourceRequestPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ResourceRequest t)
specifier|private
name|ResourceRequestProto
name|convertToProtoFormat
parameter_list|(
name|ResourceRequest
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ResourceRequestPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( ContainerResourceIncreaseRequestProto p)
specifier|private
name|ContainerResourceIncreaseRequestPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerResourceIncreaseRequestProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerResourceIncreaseRequestPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat ( ContainerResourceIncreaseRequest t)
specifier|private
name|ContainerResourceIncreaseRequestProto
name|convertToProtoFormat
parameter_list|(
name|ContainerResourceIncreaseRequest
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerResourceIncreaseRequestPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
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
DECL|method|convertFromProtoFormat (ResourceBlacklistRequestProto p)
specifier|private
name|ResourceBlacklistRequestPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ResourceBlacklistRequestProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ResourceBlacklistRequestPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ResourceBlacklistRequest t)
specifier|private
name|ResourceBlacklistRequestProto
name|convertToProtoFormat
parameter_list|(
name|ResourceBlacklistRequest
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ResourceBlacklistRequestPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

