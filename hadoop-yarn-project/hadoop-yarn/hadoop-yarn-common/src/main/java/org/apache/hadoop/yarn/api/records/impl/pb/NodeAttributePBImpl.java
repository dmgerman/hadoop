begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|NodeAttributeType
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
name|NodeAttributeProtoOrBuilder
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
name|NodeAttributeTypeProto
import|;
end_import

begin_class
DECL|class|NodeAttributePBImpl
specifier|public
class|class
name|NodeAttributePBImpl
extends|extends
name|NodeAttribute
block|{
DECL|field|proto
specifier|private
name|NodeAttributeProto
name|proto
init|=
name|NodeAttributeProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|NodeAttributeProto
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
DECL|method|NodeAttributePBImpl ()
specifier|public
name|NodeAttributePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|NodeAttributeProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|NodeAttributePBImpl (NodeAttributeProto proto)
specifier|public
name|NodeAttributePBImpl
parameter_list|(
name|NodeAttributeProto
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
name|NodeAttributeProto
name|getProto
parameter_list|()
block|{
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
name|NodeAttributeProto
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
DECL|method|getAttributeName ()
specifier|public
name|String
name|getAttributeName
parameter_list|()
block|{
name|NodeAttributeProtoOrBuilder
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
name|hasAttributeName
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
name|getAttributeName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAttributeName (String attributeName)
specifier|public
name|void
name|setAttributeName
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|attributeName
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAttributeName
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setAttributeName
argument_list|(
name|attributeName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAttributeValue ()
specifier|public
name|String
name|getAttributeValue
parameter_list|()
block|{
name|NodeAttributeProtoOrBuilder
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
name|hasAttributeValue
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
name|getAttributeValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAttributeValue (String attributeValue)
specifier|public
name|void
name|setAttributeValue
parameter_list|(
name|String
name|attributeValue
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|attributeValue
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAttributeValue
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setAttributeValue
argument_list|(
name|attributeValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAttributeType ()
specifier|public
name|NodeAttributeType
name|getAttributeType
parameter_list|()
block|{
name|NodeAttributeProtoOrBuilder
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
name|hasAttributeType
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getAttributeType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setAttributeType (NodeAttributeType attributeType)
specifier|public
name|void
name|setAttributeType
parameter_list|(
name|NodeAttributeType
name|attributeType
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|attributeType
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAttributeType
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setAttributeType
argument_list|(
name|convertToProtoFormat
argument_list|(
name|attributeType
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|convertToProtoFormat ( NodeAttributeType attributeType)
specifier|private
name|NodeAttributeTypeProto
name|convertToProtoFormat
parameter_list|(
name|NodeAttributeType
name|attributeType
parameter_list|)
block|{
return|return
name|NodeAttributeTypeProto
operator|.
name|valueOf
argument_list|(
name|attributeType
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat ( NodeAttributeTypeProto containerState)
specifier|private
name|NodeAttributeType
name|convertFromProtoFormat
parameter_list|(
name|NodeAttributeTypeProto
name|containerState
parameter_list|)
block|{
return|return
name|NodeAttributeType
operator|.
name|valueOf
argument_list|(
name|containerState
operator|.
name|name
argument_list|()
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|getAttributePrefix
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|getAttributePrefix
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|getAttributeName
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|getAttributeName
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
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
name|NodeAttribute
condition|)
block|{
name|NodeAttribute
name|other
init|=
operator|(
name|NodeAttribute
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|compare
argument_list|(
name|getAttributePrefix
argument_list|()
argument_list|,
name|other
operator|.
name|getAttributePrefix
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
operator|!
name|compare
argument_list|(
name|getAttributeName
argument_list|()
argument_list|,
name|other
operator|.
name|getAttributeName
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
DECL|method|compare (Object left, Object right)
specifier|private
specifier|static
name|boolean
name|compare
parameter_list|(
name|Object
name|left
parameter_list|,
name|Object
name|right
parameter_list|)
block|{
if|if
condition|(
name|left
operator|==
literal|null
condition|)
block|{
return|return
name|right
operator|==
literal|null
return|;
block|}
else|else
block|{
return|return
name|left
operator|.
name|equals
argument_list|(
name|right
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAttributePrefix ()
specifier|public
name|String
name|getAttributePrefix
parameter_list|()
block|{
name|NodeAttributeProtoOrBuilder
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
name|hasAttributePrefix
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
name|getAttributePrefix
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAttributePrefix (String attributePrefix)
specifier|public
name|void
name|setAttributePrefix
parameter_list|(
name|String
name|attributePrefix
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|attributePrefix
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAttributePrefix
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setAttributePrefix
argument_list|(
name|attributePrefix
argument_list|)
expr_stmt|;
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
literal|"Prefix-"
operator|+
name|getAttributePrefix
argument_list|()
operator|+
literal|" :Name-"
operator|+
name|getAttributeName
argument_list|()
operator|+
literal|":Value-"
operator|+
name|getAttributeValue
argument_list|()
operator|+
literal|":Type-"
operator|+
name|getAttributeType
argument_list|()
return|;
block|}
block|}
end_class

end_unit

