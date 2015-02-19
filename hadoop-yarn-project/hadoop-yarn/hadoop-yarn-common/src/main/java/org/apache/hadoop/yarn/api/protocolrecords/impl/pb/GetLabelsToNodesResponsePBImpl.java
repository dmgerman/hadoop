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
name|HashMap
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
name|Map
operator|.
name|Entry
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
name|Evolving
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetLabelsToNodesResponse
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
name|LabelsToNodeIdsProto
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
name|GetLabelsToNodesResponseProto
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
name|GetLabelsToNodesResponseProtoOrBuilder
import|;
end_import

begin_class
DECL|class|GetLabelsToNodesResponsePBImpl
specifier|public
class|class
name|GetLabelsToNodesResponsePBImpl
extends|extends
name|GetLabelsToNodesResponse
block|{
DECL|field|proto
name|GetLabelsToNodesResponseProto
name|proto
init|=
name|GetLabelsToNodesResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetLabelsToNodesResponseProto
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
DECL|field|labelsToNodes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|labelsToNodes
decl_stmt|;
DECL|method|GetLabelsToNodesResponsePBImpl ()
specifier|public
name|GetLabelsToNodesResponsePBImpl
parameter_list|()
block|{
name|this
operator|.
name|builder
operator|=
name|GetLabelsToNodesResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetLabelsToNodesResponsePBImpl (GetLabelsToNodesResponseProto proto)
specifier|public
name|GetLabelsToNodesResponsePBImpl
parameter_list|(
name|GetLabelsToNodesResponseProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|this
operator|.
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|initLabelsToNodes ()
specifier|private
name|void
name|initLabelsToNodes
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|labelsToNodes
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetLabelsToNodesResponseProtoOrBuilder
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
name|LabelsToNodeIdsProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getLabelsToNodesList
argument_list|()
decl_stmt|;
name|this
operator|.
name|labelsToNodes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|LabelsToNodeIdsProto
name|c
range|:
name|list
control|)
block|{
name|Set
argument_list|<
name|NodeId
argument_list|>
name|setNodes
init|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeIdProto
name|n
range|:
name|c
operator|.
name|getNodeIdList
argument_list|()
control|)
block|{
name|NodeId
name|node
init|=
operator|new
name|NodeIdPBImpl
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|setNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|setNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|labelsToNodes
operator|.
name|put
argument_list|(
name|c
operator|.
name|getNodeLabels
argument_list|()
argument_list|,
name|setNodes
argument_list|)
expr_stmt|;
block|}
block|}
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
name|GetLabelsToNodesResponseProto
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
DECL|method|addLabelsToNodesToProto ()
specifier|private
name|void
name|addLabelsToNodesToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearLabelsToNodes
argument_list|()
expr_stmt|;
if|if
condition|(
name|labelsToNodes
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|LabelsToNodeIdsProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|LabelsToNodeIdsProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|LabelsToNodeIdsProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|LabelsToNodeIdsProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
argument_list|>
name|iter
init|=
name|labelsToNodes
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
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
annotation|@
name|Override
specifier|public
name|LabelsToNodeIdsProto
name|next
parameter_list|()
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|now
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|NodeIdProto
argument_list|>
name|nodeProtoSet
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
name|n
range|:
name|now
operator|.
name|getValue
argument_list|()
control|)
block|{
name|nodeProtoSet
operator|.
name|add
argument_list|(
name|convertToProtoFormat
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|LabelsToNodeIdsProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setNodeLabels
argument_list|(
name|now
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|addAllNodeId
argument_list|(
name|nodeProtoSet
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
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
block|}
return|;
block|}
block|}
decl_stmt|;
name|builder
operator|.
name|addAllLabelsToNodes
argument_list|(
name|iterable
argument_list|)
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
name|labelsToNodes
operator|!=
literal|null
condition|)
block|{
name|addLabelsToNodesToProto
argument_list|()
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
DECL|method|getProto ()
specifier|public
name|GetLabelsToNodesResponseProto
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
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
assert|assert
literal|false
operator|:
literal|"hashCode not designed"
assert|;
return|return
literal|0
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
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|setLabelsToNodes (Map<String, Set<NodeId>> map)
specifier|public
name|void
name|setLabelsToNodes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
name|initLabelsToNodes
argument_list|()
expr_stmt|;
name|labelsToNodes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|labelsToNodes
operator|.
name|putAll
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getLabelsToNodes ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|getLabelsToNodes
parameter_list|()
block|{
name|initLabelsToNodes
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|labelsToNodes
return|;
block|}
block|}
end_class

end_unit

