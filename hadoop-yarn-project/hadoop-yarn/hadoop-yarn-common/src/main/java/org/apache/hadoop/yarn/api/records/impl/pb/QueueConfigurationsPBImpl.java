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
name|QueueConfigurations
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
name|proto
operator|.
name|YarnProtos
operator|.
name|QueueConfigurationsProto
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
name|QueueConfigurationsProtoOrBuilder
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
DECL|class|QueueConfigurationsPBImpl
specifier|public
class|class
name|QueueConfigurationsPBImpl
extends|extends
name|QueueConfigurations
block|{
DECL|field|proto
name|QueueConfigurationsProto
name|proto
init|=
name|QueueConfigurationsProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|QueueConfigurationsProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|configuredMinResource
name|Resource
name|configuredMinResource
init|=
literal|null
decl_stmt|;
DECL|field|configuredMaxResource
name|Resource
name|configuredMaxResource
init|=
literal|null
decl_stmt|;
DECL|field|effMinResource
name|Resource
name|effMinResource
init|=
literal|null
decl_stmt|;
DECL|field|effMaxResource
name|Resource
name|effMaxResource
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|method|QueueConfigurationsPBImpl ()
specifier|public
name|QueueConfigurationsPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|QueueConfigurationsProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|QueueConfigurationsPBImpl (QueueConfigurationsProto proto)
specifier|public
name|QueueConfigurationsPBImpl
parameter_list|(
name|QueueConfigurationsProto
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
name|QueueConfigurationsProto
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
name|effMinResource
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setEffectiveMinCapacity
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|effMinResource
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|effMaxResource
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setEffectiveMaxCapacity
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|effMaxResource
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|configuredMinResource
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setEffectiveMinCapacity
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|configuredMinResource
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|configuredMaxResource
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setEffectiveMaxCapacity
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|configuredMaxResource
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCapacity ()
specifier|public
name|float
name|getCapacity
parameter_list|()
block|{
name|QueueConfigurationsProtoOrBuilder
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
name|hasCapacity
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getCapacity
argument_list|()
else|:
literal|0f
return|;
block|}
annotation|@
name|Override
DECL|method|setCapacity (float capacity)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|float
name|capacity
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setCapacity
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAbsoluteCapacity ()
specifier|public
name|float
name|getAbsoluteCapacity
parameter_list|()
block|{
name|QueueConfigurationsProtoOrBuilder
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
name|hasAbsoluteCapacity
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getAbsoluteCapacity
argument_list|()
else|:
literal|0f
return|;
block|}
annotation|@
name|Override
DECL|method|setAbsoluteCapacity (float absoluteCapacity)
specifier|public
name|void
name|setAbsoluteCapacity
parameter_list|(
name|float
name|absoluteCapacity
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setAbsoluteCapacity
argument_list|(
name|absoluteCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxCapacity ()
specifier|public
name|float
name|getMaxCapacity
parameter_list|()
block|{
name|QueueConfigurationsProtoOrBuilder
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
name|hasMaxCapacity
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getMaxCapacity
argument_list|()
else|:
literal|0f
return|;
block|}
annotation|@
name|Override
DECL|method|setMaxCapacity (float maxCapacity)
specifier|public
name|void
name|setMaxCapacity
parameter_list|(
name|float
name|maxCapacity
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setMaxCapacity
argument_list|(
name|maxCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAbsoluteMaxCapacity ()
specifier|public
name|float
name|getAbsoluteMaxCapacity
parameter_list|()
block|{
name|QueueConfigurationsProtoOrBuilder
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
name|hasAbsoluteMaxCapacity
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getAbsoluteMaxCapacity
argument_list|()
else|:
literal|0f
return|;
block|}
annotation|@
name|Override
DECL|method|setAbsoluteMaxCapacity (float absoluteMaxCapacity)
specifier|public
name|void
name|setAbsoluteMaxCapacity
parameter_list|(
name|float
name|absoluteMaxCapacity
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setAbsoluteMaxCapacity
argument_list|(
name|absoluteMaxCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxAMPercentage ()
specifier|public
name|float
name|getMaxAMPercentage
parameter_list|()
block|{
name|QueueConfigurationsProtoOrBuilder
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
name|hasMaxAMPercentage
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getMaxAMPercentage
argument_list|()
else|:
literal|0f
return|;
block|}
annotation|@
name|Override
DECL|method|setMaxAMPercentage (float maxAMPercentage)
specifier|public
name|void
name|setMaxAMPercentage
parameter_list|(
name|float
name|maxAMPercentage
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setMaxAMPercentage
argument_list|(
name|maxAMPercentage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEffectiveMinCapacity ()
specifier|public
name|Resource
name|getEffectiveMinCapacity
parameter_list|()
block|{
name|QueueConfigurationsProtoOrBuilder
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
name|effMinResource
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|effMinResource
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasEffectiveMinCapacity
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|effMinResource
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getEffectiveMinCapacity
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|effMinResource
return|;
block|}
annotation|@
name|Override
DECL|method|setEffectiveMinCapacity (Resource capacity)
specifier|public
name|void
name|setEffectiveMinCapacity
parameter_list|(
name|Resource
name|capacity
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|capacity
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearEffectiveMinCapacity
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|effMinResource
operator|=
name|capacity
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEffectiveMaxCapacity ()
specifier|public
name|Resource
name|getEffectiveMaxCapacity
parameter_list|()
block|{
name|QueueConfigurationsProtoOrBuilder
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
name|effMaxResource
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|effMaxResource
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasEffectiveMaxCapacity
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|effMaxResource
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getEffectiveMaxCapacity
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|effMaxResource
return|;
block|}
annotation|@
name|Override
DECL|method|setEffectiveMaxCapacity (Resource capacity)
specifier|public
name|void
name|setEffectiveMaxCapacity
parameter_list|(
name|Resource
name|capacity
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|capacity
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearEffectiveMaxCapacity
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|effMaxResource
operator|=
name|capacity
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ResourceProto p)
specifier|private
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
DECL|method|convertToProtoFormat (Resource t)
specifier|private
name|ResourceProto
name|convertToProtoFormat
parameter_list|(
name|Resource
name|t
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|t
argument_list|)
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
name|QueueConfigurationsProto
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
DECL|method|getConfiguredMinCapacity ()
specifier|public
name|Resource
name|getConfiguredMinCapacity
parameter_list|()
block|{
name|QueueConfigurationsProtoOrBuilder
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
name|configuredMinResource
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|configuredMinResource
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasConfiguredMinCapacity
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|configuredMinResource
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getConfiguredMinCapacity
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|configuredMinResource
return|;
block|}
annotation|@
name|Override
DECL|method|setConfiguredMinCapacity (Resource minResource)
specifier|public
name|void
name|setConfiguredMinCapacity
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
name|minResource
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearConfiguredMinCapacity
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|configuredMinResource
operator|=
name|minResource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConfiguredMaxCapacity ()
specifier|public
name|Resource
name|getConfiguredMaxCapacity
parameter_list|()
block|{
name|QueueConfigurationsProtoOrBuilder
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
name|configuredMaxResource
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|configuredMaxResource
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasConfiguredMaxCapacity
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|configuredMaxResource
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getConfiguredMaxCapacity
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|configuredMaxResource
return|;
block|}
annotation|@
name|Override
DECL|method|setConfiguredMaxCapacity (Resource maxResource)
specifier|public
name|void
name|setConfiguredMaxCapacity
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
name|configuredMaxResource
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearConfiguredMaxCapacity
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|configuredMaxResource
operator|=
name|maxResource
expr_stmt|;
block|}
block|}
end_class

end_unit

