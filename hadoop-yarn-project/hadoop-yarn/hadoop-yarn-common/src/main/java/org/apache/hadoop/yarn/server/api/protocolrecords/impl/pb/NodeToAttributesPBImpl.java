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
name|YarnProtos
operator|.
name|NodeAttributeProto
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
name|NodeToAttributesProto
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
name|NodeToAttributesProtoOrBuilder
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
name|NodeToAttributes
import|;
end_import

begin_comment
comment|/**  * Proto class for Node to attributes mapping.  */
end_comment

begin_class
DECL|class|NodeToAttributesPBImpl
specifier|public
class|class
name|NodeToAttributesPBImpl
extends|extends
name|NodeToAttributes
block|{
DECL|field|proto
specifier|private
name|NodeToAttributesProto
name|proto
init|=
name|NodeToAttributesProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|NodeToAttributesProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
specifier|private
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|field|nodeAttributes
specifier|private
name|List
argument_list|<
name|NodeAttribute
argument_list|>
name|nodeAttributes
init|=
literal|null
decl_stmt|;
DECL|method|NodeToAttributesPBImpl ()
specifier|public
name|NodeToAttributesPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|NodeToAttributesProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|NodeToAttributesPBImpl (NodeToAttributesProto proto)
specifier|public
name|NodeToAttributesPBImpl
parameter_list|(
name|NodeToAttributesProto
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
if|if
condition|(
name|this
operator|.
name|nodeAttributes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|NodeAttribute
name|nodeAttribute
range|:
name|nodeAttributes
control|)
block|{
name|builder
operator|.
name|addNodeAttributes
argument_list|(
operator|(
operator|(
name|NodeAttributePBImpl
operator|)
name|nodeAttribute
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|NodeToAttributesProto
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
name|NodeToAttributesProto
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
DECL|method|getNode ()
specifier|public
name|String
name|getNode
parameter_list|()
block|{
name|NodeToAttributesProtoOrBuilder
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
name|hasNode
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|p
operator|.
name|getNode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNode (String node)
specifier|public
name|void
name|setNode
parameter_list|(
name|String
name|node
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
DECL|method|initNodeAttributes ()
specifier|private
name|void
name|initNodeAttributes
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|nodeAttributes
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|NodeToAttributesProtoOrBuilder
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
name|nodeAttributesProtoList
init|=
name|p
operator|.
name|getNodeAttributesList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NodeAttribute
argument_list|>
name|attributes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeAttributesProtoList
operator|==
literal|null
operator|||
name|nodeAttributesProtoList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|nodeAttributes
operator|=
name|attributes
expr_stmt|;
return|return;
block|}
for|for
control|(
name|NodeAttributeProto
name|nodeAttributeProto
range|:
name|nodeAttributesProtoList
control|)
block|{
name|attributes
operator|.
name|add
argument_list|(
operator|new
name|NodeAttributePBImpl
argument_list|(
name|nodeAttributeProto
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|nodeAttributes
operator|=
name|attributes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeAttributes ()
specifier|public
name|List
argument_list|<
name|NodeAttribute
argument_list|>
name|getNodeAttributes
parameter_list|()
block|{
name|initNodeAttributes
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|nodeAttributes
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeAttributes (List<NodeAttribute> attributes)
specifier|public
name|void
name|setNodeAttributes
parameter_list|(
name|List
argument_list|<
name|NodeAttribute
argument_list|>
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|nodeAttributes
operator|==
literal|null
condition|)
block|{
name|nodeAttributes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|nodeAttributes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nodeAttributes
operator|.
name|addAll
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
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
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
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
name|obj
operator|instanceof
name|NodeToAttributes
condition|)
block|{
name|NodeToAttributes
name|other
init|=
operator|(
name|NodeToAttributes
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|getNodeAttributes
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getNodeAttributes
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|getNodeAttributes
argument_list|()
operator|.
name|containsAll
argument_list|(
name|other
operator|.
name|getNodeAttributes
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getNode
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getNode
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|getNode
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNode
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

