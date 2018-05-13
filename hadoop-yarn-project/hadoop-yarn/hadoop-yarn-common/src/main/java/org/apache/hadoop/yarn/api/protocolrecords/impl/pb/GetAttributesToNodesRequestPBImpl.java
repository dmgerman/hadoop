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
name|protocolrecords
operator|.
name|GetAttributesToNodesRequest
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
name|NodeAttributeKey
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
name|NodeAttributeKeyPBImpl
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
name|NodeAttributeKeyProto
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
name|GetAttributesToNodesRequestProto
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

begin_comment
comment|/**  * Attributes to nodes mapping request.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|GetAttributesToNodesRequestPBImpl
specifier|public
class|class
name|GetAttributesToNodesRequestPBImpl
extends|extends
name|GetAttributesToNodesRequest
block|{
DECL|field|nodeAttributes
specifier|private
name|Set
argument_list|<
name|NodeAttributeKey
argument_list|>
name|nodeAttributes
init|=
literal|null
decl_stmt|;
DECL|field|proto
specifier|private
name|GetAttributesToNodesRequestProto
name|proto
init|=
name|GetAttributesToNodesRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|GetAttributesToNodesRequestProto
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
DECL|method|GetAttributesToNodesRequestPBImpl ()
specifier|public
name|GetAttributesToNodesRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetAttributesToNodesRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetAttributesToNodesRequestPBImpl ( GetAttributesToNodesRequestProto proto)
specifier|public
name|GetAttributesToNodesRequestPBImpl
parameter_list|(
name|GetAttributesToNodesRequestProto
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
name|GetAttributesToNodesRequestProto
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
name|nodeAttributes
operator|!=
literal|null
condition|)
block|{
name|addLocalAttributesToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addLocalAttributesToProto ()
specifier|private
name|void
name|addLocalAttributesToProto
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
if|if
condition|(
name|nodeAttributes
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|NodeAttributeKeyProto
argument_list|>
name|iterable
init|=
parameter_list|()
lambda|->
operator|new
name|Iterator
argument_list|<
name|NodeAttributeKeyProto
argument_list|>
argument_list|()
block|{
specifier|private
name|Iterator
argument_list|<
name|NodeAttributeKey
argument_list|>
name|iter
operator|=
name|nodeAttributes
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
name|NodeAttributeKeyProto
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
empty_stmt|;
name|builder
operator|.
name|addAllNodeAttributes
parameter_list|(
name|iterable
parameter_list|)
constructor_decl|;
block|}
end_class

begin_function
DECL|method|convertFromProtoFormat ( NodeAttributeKeyProto p)
specifier|private
name|NodeAttributeKeyPBImpl
name|convertFromProtoFormat
parameter_list|(
name|NodeAttributeKeyProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeAttributeKeyPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|convertToProtoFormat (NodeAttributeKey t)
specifier|private
name|NodeAttributeKeyProto
name|convertToProtoFormat
parameter_list|(
name|NodeAttributeKey
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeAttributeKeyPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
end_function

begin_function
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
name|GetAttributesToNodesRequestProto
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
end_function

begin_function
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
name|YarnServiceProtos
operator|.
name|GetAttributesToNodesRequestProtoOrBuilder
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
name|NodeAttributeKeyProto
argument_list|>
name|nodeAttributesList
init|=
name|p
operator|.
name|getNodeAttributesList
argument_list|()
decl_stmt|;
name|this
operator|.
name|nodeAttributes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|nodeAttributesList
operator|.
name|forEach
argument_list|(
parameter_list|(
name|v
parameter_list|)
lambda|->
name|nodeAttributes
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
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
end_function

begin_function
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
end_function

begin_function
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
end_function

begin_function
annotation|@
name|Override
DECL|method|setNodeAttributes (Set<NodeAttributeKey> attributes)
specifier|public
name|void
name|setNodeAttributes
parameter_list|(
name|Set
argument_list|<
name|NodeAttributeKey
argument_list|>
name|attributes
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeAttributes
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeAttributes
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|nodeAttributes
operator|=
name|attributes
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getNodeAttributes ()
specifier|public
name|Set
argument_list|<
name|NodeAttributeKey
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
end_function

unit|}
end_unit

