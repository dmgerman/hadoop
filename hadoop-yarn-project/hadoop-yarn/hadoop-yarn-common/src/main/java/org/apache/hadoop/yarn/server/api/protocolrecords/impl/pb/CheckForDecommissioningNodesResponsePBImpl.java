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
name|List
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
name|impl
operator|.
name|pb
operator|.
name|NodeIdPBImpl
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
name|YarnServerResourceManagerServiceProtos
operator|.
name|CheckForDecommissioningNodesResponseProto
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
name|YarnServerResourceManagerServiceProtos
operator|.
name|CheckForDecommissioningNodesResponseProtoOrBuilder
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
name|CheckForDecommissioningNodesResponse
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
DECL|class|CheckForDecommissioningNodesResponsePBImpl
specifier|public
class|class
name|CheckForDecommissioningNodesResponsePBImpl
extends|extends
name|CheckForDecommissioningNodesResponse
block|{
DECL|field|proto
name|CheckForDecommissioningNodesResponseProto
name|proto
init|=
name|CheckForDecommissioningNodesResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|CheckForDecommissioningNodesResponseProto
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
DECL|field|decommissioningNodes
specifier|private
name|Set
argument_list|<
name|NodeId
argument_list|>
name|decommissioningNodes
decl_stmt|;
DECL|method|CheckForDecommissioningNodesResponsePBImpl ()
specifier|public
name|CheckForDecommissioningNodesResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|CheckForDecommissioningNodesResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|CheckForDecommissioningNodesResponsePBImpl ( CheckForDecommissioningNodesResponseProto proto)
specifier|public
name|CheckForDecommissioningNodesResponsePBImpl
parameter_list|(
name|CheckForDecommissioningNodesResponseProto
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
name|CheckForDecommissioningNodesResponseProto
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
name|CheckForDecommissioningNodesResponseProto
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
name|decommissioningNodes
operator|!=
literal|null
condition|)
block|{
name|addDecommissioningNodesToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addDecommissioningNodesToProto ()
specifier|private
name|void
name|addDecommissioningNodesToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearDecommissioningNodes
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|decommissioningNodes
operator|==
literal|null
condition|)
return|return;
name|Set
argument_list|<
name|NodeIdProto
argument_list|>
name|nodeIdProtos
init|=
operator|new
name|HashSet
argument_list|<
name|NodeIdProto
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeId
name|nodeId
range|:
name|decommissioningNodes
control|)
block|{
name|nodeIdProtos
operator|.
name|add
argument_list|(
name|convertToProtoFormat
argument_list|(
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addAllDecommissioningNodes
argument_list|(
name|nodeIdProtos
argument_list|)
expr_stmt|;
block|}
DECL|method|convertToProtoFormat (NodeId nodeId)
specifier|private
name|NodeIdProto
name|convertToProtoFormat
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeIdPBImpl
operator|)
name|nodeId
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setDecommissioningNodes (Set<NodeId> decommissioningNodes)
specifier|public
name|void
name|setDecommissioningNodes
parameter_list|(
name|Set
argument_list|<
name|NodeId
argument_list|>
name|decommissioningNodes
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|decommissioningNodes
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearDecommissioningNodes
argument_list|()
expr_stmt|;
name|this
operator|.
name|decommissioningNodes
operator|=
name|decommissioningNodes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDecommissioningNodes ()
specifier|public
name|Set
argument_list|<
name|NodeId
argument_list|>
name|getDecommissioningNodes
parameter_list|()
block|{
name|initNodesDecommissioning
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|decommissioningNodes
return|;
block|}
DECL|method|initNodesDecommissioning ()
specifier|private
name|void
name|initNodesDecommissioning
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|decommissioningNodes
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|CheckForDecommissioningNodesResponseProtoOrBuilder
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
name|NodeIdProto
argument_list|>
name|nodeIds
init|=
name|p
operator|.
name|getDecommissioningNodesList
argument_list|()
decl_stmt|;
name|this
operator|.
name|decommissioningNodes
operator|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|NodeIdProto
name|nodeIdProto
range|:
name|nodeIds
control|)
block|{
name|this
operator|.
name|decommissioningNodes
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|nodeIdProto
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|convertFromProtoFormat (NodeIdProto nodeIdProto)
specifier|private
name|NodeId
name|convertFromProtoFormat
parameter_list|(
name|NodeIdProto
name|nodeIdProto
parameter_list|)
block|{
return|return
operator|new
name|NodeIdPBImpl
argument_list|(
name|nodeIdProto
argument_list|)
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
block|}
end_class

end_unit

