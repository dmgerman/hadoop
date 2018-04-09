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
import|import static
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
name|*
import|;
end_import

begin_import
import|import static
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
name|*
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
name|GetClusterNodeAttributesResponse
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
name|NodeAttribute
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
name|NodeAttributePBImpl
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
name|GetClusterNodeAttributesResponseProto
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
name|NodeAttributeProto
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

begin_comment
comment|/**  * Cluster node attributes response.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|GetClusterNodeAttributesResponsePBImpl
specifier|public
class|class
name|GetClusterNodeAttributesResponsePBImpl
extends|extends
name|GetClusterNodeAttributesResponse
block|{
DECL|field|proto
specifier|private
name|GetClusterNodeAttributesResponseProto
name|proto
init|=
name|GetClusterNodeAttributesResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|GetClusterNodeAttributesResponseProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|updatedNodeAttributes
specifier|private
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|updatedNodeAttributes
decl_stmt|;
DECL|field|viaProto
specifier|private
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|method|GetClusterNodeAttributesResponsePBImpl ()
specifier|public
name|GetClusterNodeAttributesResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetClusterNodeAttributesResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetClusterNodeAttributesResponsePBImpl ( GetClusterNodeAttributesResponseProto proto)
specifier|public
name|GetClusterNodeAttributesResponsePBImpl
parameter_list|(
name|GetClusterNodeAttributesResponseProto
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
name|GetClusterNodeAttributesResponseProto
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
name|updatedNodeAttributes
operator|!=
literal|null
condition|)
block|{
name|addNodeAttributesToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addNodeAttributesToProto ()
specifier|private
name|void
name|addNodeAttributesToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearNodeAttributes
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NodeAttributeProto
argument_list|>
name|protoList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeAttribute
name|r
range|:
name|this
operator|.
name|updatedNodeAttributes
control|)
block|{
name|protoList
operator|.
name|add
argument_list|(
name|convertToProtoFormat
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addAllNodeAttributes
argument_list|(
name|protoList
argument_list|)
expr_stmt|;
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
block|{
return|return
literal|false
return|;
block|}
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
name|GetClusterNodeAttributesResponseProto
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
DECL|method|setNodeAttributes (Set<NodeAttribute> attributes)
specifier|public
specifier|synchronized
name|void
name|setNodeAttributes
parameter_list|(
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|attributes
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|updatedNodeAttributes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeAttributes
argument_list|()
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|updatedNodeAttributes
operator|.
name|addAll
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeAttributes ()
specifier|public
specifier|synchronized
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|getNodeAttributes
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|updatedNodeAttributes
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|updatedNodeAttributes
return|;
block|}
name|initLocalNodeAttributes
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|updatedNodeAttributes
return|;
block|}
DECL|method|initLocalNodeAttributes ()
specifier|private
name|void
name|initLocalNodeAttributes
parameter_list|()
block|{
name|YarnServiceProtos
operator|.
name|GetClusterNodeAttributesResponseProtoOrBuilder
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
name|NodeAttributeProto
argument_list|>
name|attributesProtoList
init|=
name|p
operator|.
name|getNodeAttributesList
argument_list|()
decl_stmt|;
name|this
operator|.
name|updatedNodeAttributes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|NodeAttributeProto
name|r
range|:
name|attributesProtoList
control|)
block|{
name|this
operator|.
name|updatedNodeAttributes
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|convertFromProtoFormat (NodeAttributeProto p)
specifier|private
name|NodeAttribute
name|convertFromProtoFormat
parameter_list|(
name|NodeAttributeProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeAttributePBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (NodeAttribute t)
specifier|private
name|NodeAttributeProto
name|convertToProtoFormat
parameter_list|(
name|NodeAttribute
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeAttributePBImpl
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
return|;
block|}
block|}
end_class

end_unit

