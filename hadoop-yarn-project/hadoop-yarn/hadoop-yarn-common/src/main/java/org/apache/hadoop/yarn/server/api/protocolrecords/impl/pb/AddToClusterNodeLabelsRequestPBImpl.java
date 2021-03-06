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
name|ArrayList
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeLabel
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
name|NodeLabelPBImpl
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
name|NodeLabelProto
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
name|AddToClusterNodeLabelsRequestProto
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
name|AddToClusterNodeLabelsRequestProtoOrBuilder
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
name|AddToClusterNodeLabelsRequest
import|;
end_import

begin_class
DECL|class|AddToClusterNodeLabelsRequestPBImpl
specifier|public
class|class
name|AddToClusterNodeLabelsRequestPBImpl
extends|extends
name|AddToClusterNodeLabelsRequest
block|{
DECL|field|proto
name|AddToClusterNodeLabelsRequestProto
name|proto
init|=
name|AddToClusterNodeLabelsRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|AddToClusterNodeLabelsRequestProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|updatedNodeLabels
specifier|private
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|updatedNodeLabels
decl_stmt|;
DECL|field|viaProto
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|method|AddToClusterNodeLabelsRequestPBImpl ()
specifier|public
name|AddToClusterNodeLabelsRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|AddToClusterNodeLabelsRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|AddToClusterNodeLabelsRequestPBImpl ( AddToClusterNodeLabelsRequestProto proto)
specifier|public
name|AddToClusterNodeLabelsRequestPBImpl
parameter_list|(
name|AddToClusterNodeLabelsRequestProto
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
name|AddToClusterNodeLabelsRequestProto
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
name|updatedNodeLabels
operator|!=
literal|null
condition|)
block|{
name|addNodeLabelsToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addNodeLabelsToProto ()
specifier|private
name|void
name|addNodeLabelsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearNodeLabels
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearDeprecatedNodeLabels
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NodeLabelProto
argument_list|>
name|protoList
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeLabelProto
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|protoListString
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeLabel
name|r
range|:
name|this
operator|.
name|updatedNodeLabels
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
name|protoListString
operator|.
name|add
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addAllNodeLabels
argument_list|(
name|protoList
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAllDeprecatedNodeLabels
argument_list|(
name|protoListString
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
name|AddToClusterNodeLabelsRequestProto
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
DECL|method|initLocalNodeLabels ()
specifier|private
name|void
name|initLocalNodeLabels
parameter_list|()
block|{
name|AddToClusterNodeLabelsRequestProtoOrBuilder
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
name|NodeLabelProto
argument_list|>
name|attributesProtoList
init|=
name|p
operator|.
name|getNodeLabelsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|updatedNodeLabels
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeLabel
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|NodeLabelProto
name|r
range|:
name|attributesProtoList
control|)
block|{
name|this
operator|.
name|updatedNodeLabels
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
if|if
condition|(
name|this
operator|.
name|updatedNodeLabels
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|deprecatedLabelsList
init|=
name|p
operator|.
name|getDeprecatedNodeLabelsList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|l
range|:
name|deprecatedLabelsList
control|)
block|{
name|this
operator|.
name|updatedNodeLabels
operator|.
name|add
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
name|l
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|convertFromProtoFormat (NodeLabelProto p)
specifier|private
name|NodeLabel
name|convertFromProtoFormat
parameter_list|(
name|NodeLabelProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeLabelPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (NodeLabel t)
specifier|private
name|NodeLabelProto
name|convertToProtoFormat
parameter_list|(
name|NodeLabel
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeLabelPBImpl
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
annotation|@
name|Override
DECL|method|setNodeLabels (List<NodeLabel> nodeLabels)
specifier|public
name|void
name|setNodeLabels
parameter_list|(
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|nodeLabels
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|updatedNodeLabels
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeLabels
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeLabels
argument_list|()
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|updatedNodeLabels
operator|.
name|addAll
argument_list|(
name|nodeLabels
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeLabels ()
specifier|public
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|getNodeLabels
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|updatedNodeLabels
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|updatedNodeLabels
return|;
block|}
name|initLocalNodeLabels
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|updatedNodeLabels
return|;
block|}
block|}
end_class

end_unit

