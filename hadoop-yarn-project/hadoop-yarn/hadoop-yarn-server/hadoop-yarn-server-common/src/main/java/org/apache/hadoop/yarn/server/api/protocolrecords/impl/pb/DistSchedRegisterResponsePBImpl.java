begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
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
name|RegisterApplicationMasterResponse
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
name|impl
operator|.
name|pb
operator|.
name|RegisterApplicationMasterResponsePBImpl
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
name|proto
operator|.
name|YarnProtos
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
name|YarnServerCommonServiceProtos
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
name|api
operator|.
name|protocolrecords
operator|.
name|DistSchedRegisterResponse
import|;
end_import

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

begin_class
DECL|class|DistSchedRegisterResponsePBImpl
specifier|public
class|class
name|DistSchedRegisterResponsePBImpl
extends|extends
name|DistSchedRegisterResponse
block|{
DECL|field|proto
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProto
name|proto
init|=
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProto
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
DECL|field|maxAllocatableCapability
specifier|private
name|Resource
name|maxAllocatableCapability
decl_stmt|;
DECL|field|minAllocatableCapability
specifier|private
name|Resource
name|minAllocatableCapability
decl_stmt|;
DECL|field|incrAllocatableCapability
specifier|private
name|Resource
name|incrAllocatableCapability
decl_stmt|;
DECL|field|nodesForScheduling
specifier|private
name|List
argument_list|<
name|NodeId
argument_list|>
name|nodesForScheduling
decl_stmt|;
DECL|field|registerApplicationMasterResponse
specifier|private
name|RegisterApplicationMasterResponse
name|registerApplicationMasterResponse
decl_stmt|;
DECL|method|DistSchedRegisterResponsePBImpl ()
specifier|public
name|DistSchedRegisterResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|DistSchedRegisterResponsePBImpl (YarnServerCommonServiceProtos.DistSchedRegisterResponseProto proto)
specifier|public
name|DistSchedRegisterResponsePBImpl
parameter_list|(
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProto
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
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProto
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
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProto
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
name|nodesForScheduling
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodesForScheduling
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|YarnProtos
operator|.
name|NodeIdProto
argument_list|>
name|iterable
init|=
name|getNodeIdProtoIterable
argument_list|(
name|this
operator|.
name|nodesForScheduling
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAllNodesForScheduling
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|maxAllocatableCapability
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setMaxAllocCapability
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|maxAllocatableCapability
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|minAllocatableCapability
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setMaxAllocCapability
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|minAllocatableCapability
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|registerApplicationMasterResponse
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setRegisterResponse
argument_list|(
operator|(
operator|(
name|RegisterApplicationMasterResponsePBImpl
operator|)
name|this
operator|.
name|registerApplicationMasterResponse
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setRegisterResponse (RegisterApplicationMasterResponse resp)
specifier|public
name|void
name|setRegisterResponse
parameter_list|(
name|RegisterApplicationMasterResponse
name|resp
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|registerApplicationMasterResponse
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearRegisterResponse
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|registerApplicationMasterResponse
operator|=
name|resp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRegisterResponse ()
specifier|public
name|RegisterApplicationMasterResponse
name|getRegisterResponse
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|registerApplicationMasterResponse
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|registerApplicationMasterResponse
return|;
block|}
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProtoOrBuilder
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
name|hasRegisterResponse
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|registerApplicationMasterResponse
operator|=
operator|new
name|RegisterApplicationMasterResponsePBImpl
argument_list|(
name|p
operator|.
name|getRegisterResponse
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|registerApplicationMasterResponse
return|;
block|}
annotation|@
name|Override
DECL|method|setMaxAllocatableCapabilty (Resource maxResource)
specifier|public
name|void
name|setMaxAllocatableCapabilty
parameter_list|(
name|Resource
name|maxResource
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|maxAllocatableCapability
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearMaxAllocCapability
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|maxAllocatableCapability
operator|=
name|maxResource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxAllocatableCapabilty ()
specifier|public
name|Resource
name|getMaxAllocatableCapabilty
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|maxAllocatableCapability
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|maxAllocatableCapability
return|;
block|}
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProtoOrBuilder
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
name|hasMaxAllocCapability
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|maxAllocatableCapability
operator|=
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getMaxAllocCapability
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|maxAllocatableCapability
return|;
block|}
annotation|@
name|Override
DECL|method|setMinAllocatableCapabilty (Resource minResource)
specifier|public
name|void
name|setMinAllocatableCapabilty
parameter_list|(
name|Resource
name|minResource
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|minAllocatableCapability
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearMinAllocCapability
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|minAllocatableCapability
operator|=
name|minResource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMinAllocatableCapabilty ()
specifier|public
name|Resource
name|getMinAllocatableCapabilty
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|minAllocatableCapability
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|minAllocatableCapability
return|;
block|}
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProtoOrBuilder
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
name|hasMinAllocCapability
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|minAllocatableCapability
operator|=
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getMinAllocCapability
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|minAllocatableCapability
return|;
block|}
annotation|@
name|Override
DECL|method|setIncrAllocatableCapabilty (Resource incrResource)
specifier|public
name|void
name|setIncrAllocatableCapabilty
parameter_list|(
name|Resource
name|incrResource
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|incrAllocatableCapability
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearIncrAllocCapability
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|incrAllocatableCapability
operator|=
name|incrResource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getIncrAllocatableCapabilty ()
specifier|public
name|Resource
name|getIncrAllocatableCapabilty
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|incrAllocatableCapability
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|incrAllocatableCapability
return|;
block|}
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProtoOrBuilder
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
name|hasIncrAllocCapability
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|incrAllocatableCapability
operator|=
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getIncrAllocCapability
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|incrAllocatableCapability
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerTokenExpiryInterval (int interval)
specifier|public
name|void
name|setContainerTokenExpiryInterval
parameter_list|(
name|int
name|interval
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setContainerTokenExpiryInterval
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainerTokenExpiryInterval ()
specifier|public
name|int
name|getContainerTokenExpiryInterval
parameter_list|()
block|{
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProtoOrBuilder
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
name|hasContainerTokenExpiryInterval
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|p
operator|.
name|getContainerTokenExpiryInterval
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerIdStart (long containerIdStart)
specifier|public
name|void
name|setContainerIdStart
parameter_list|(
name|long
name|containerIdStart
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setContainerIdStart
argument_list|(
name|containerIdStart
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainerIdStart ()
specifier|public
name|long
name|getContainerIdStart
parameter_list|()
block|{
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProtoOrBuilder
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
name|hasContainerIdStart
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|p
operator|.
name|getContainerIdStart
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNodesForScheduling (List<NodeId> nodesForScheduling)
specifier|public
name|void
name|setNodesForScheduling
parameter_list|(
name|List
argument_list|<
name|NodeId
argument_list|>
name|nodesForScheduling
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodesForScheduling
operator|==
literal|null
operator|||
name|nodesForScheduling
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|nodesForScheduling
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nodesForScheduling
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|clearNodesForScheduling
argument_list|()
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|nodesForScheduling
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodesForScheduling
operator|.
name|addAll
argument_list|(
name|nodesForScheduling
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodesForScheduling ()
specifier|public
name|List
argument_list|<
name|NodeId
argument_list|>
name|getNodesForScheduling
parameter_list|()
block|{
if|if
condition|(
name|nodesForScheduling
operator|!=
literal|null
condition|)
block|{
return|return
name|nodesForScheduling
return|;
block|}
name|initLocalNodesForSchedulingList
argument_list|()
expr_stmt|;
return|return
name|nodesForScheduling
return|;
block|}
DECL|method|initLocalNodesForSchedulingList ()
specifier|private
specifier|synchronized
name|void
name|initLocalNodesForSchedulingList
parameter_list|()
block|{
name|YarnServerCommonServiceProtos
operator|.
name|DistSchedRegisterResponseProtoOrBuilder
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
name|YarnProtos
operator|.
name|NodeIdProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getNodesForSchedulingList
argument_list|()
decl_stmt|;
name|nodesForScheduling
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|YarnProtos
operator|.
name|NodeIdProto
name|t
range|:
name|list
control|)
block|{
name|nodesForScheduling
operator|.
name|add
argument_list|(
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getNodeIdProtoIterable ( final List<NodeId> nodeList)
specifier|private
specifier|synchronized
name|Iterable
argument_list|<
name|YarnProtos
operator|.
name|NodeIdProto
argument_list|>
name|getNodeIdProtoIterable
parameter_list|(
specifier|final
name|List
argument_list|<
name|NodeId
argument_list|>
name|nodeList
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|YarnProtos
operator|.
name|NodeIdProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|YarnProtos
operator|.
name|NodeIdProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|YarnProtos
operator|.
name|NodeIdProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|NodeId
argument_list|>
name|iter
init|=
name|nodeList
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
name|YarnProtos
operator|.
name|NodeIdProto
name|next
parameter_list|()
block|{
return|return
name|ProtoUtils
operator|.
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
block|}
end_class

end_unit

