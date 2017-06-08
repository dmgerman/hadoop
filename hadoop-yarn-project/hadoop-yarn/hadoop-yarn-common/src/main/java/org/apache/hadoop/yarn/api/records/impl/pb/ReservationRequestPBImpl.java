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
name|ReservationRequest
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
name|ReservationRequestProto
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
name|ReservationRequestProtoOrBuilder
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ReservationRequestPBImpl
specifier|public
class|class
name|ReservationRequestPBImpl
extends|extends
name|ReservationRequest
block|{
DECL|field|proto
name|ReservationRequestProto
name|proto
init|=
name|ReservationRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ReservationRequestProto
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
DECL|field|capability
specifier|private
name|Resource
name|capability
init|=
literal|null
decl_stmt|;
DECL|method|ReservationRequestPBImpl ()
specifier|public
name|ReservationRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ReservationRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ReservationRequestPBImpl (ReservationRequestProto proto)
specifier|public
name|ReservationRequestPBImpl
parameter_list|(
name|ReservationRequestProto
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
name|ReservationRequestProto
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
name|capability
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setCapability
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|capability
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
name|ReservationRequestProto
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
DECL|method|getCapability ()
specifier|public
name|Resource
name|getCapability
parameter_list|()
block|{
name|ReservationRequestProtoOrBuilder
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
name|capability
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|capability
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasCapability
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|capability
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|capability
return|;
block|}
annotation|@
name|Override
DECL|method|setCapability (Resource capability)
specifier|public
name|void
name|setCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|capability
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearCapability
argument_list|()
expr_stmt|;
name|this
operator|.
name|capability
operator|=
name|capability
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumContainers ()
specifier|public
name|int
name|getNumContainers
parameter_list|()
block|{
name|ReservationRequestProtoOrBuilder
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
name|getNumContainers
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNumContainers (int numContainers)
specifier|public
name|void
name|setNumContainers
parameter_list|(
name|int
name|numContainers
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setNumContainers
argument_list|(
operator|(
name|numContainers
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConcurrency ()
specifier|public
name|int
name|getConcurrency
parameter_list|()
block|{
name|ReservationRequestProtoOrBuilder
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
name|hasConcurrency
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getConcurrency
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setConcurrency (int numContainers)
specifier|public
name|void
name|setConcurrency
parameter_list|(
name|int
name|numContainers
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setConcurrency
argument_list|(
name|numContainers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDuration ()
specifier|public
name|long
name|getDuration
parameter_list|()
block|{
name|ReservationRequestProtoOrBuilder
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
name|hasDuration
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getDuration
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDuration (long duration)
specifier|public
name|void
name|setDuration
parameter_list|(
name|long
name|duration
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setDuration
argument_list|(
name|duration
argument_list|)
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
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{Capability: "
operator|+
name|getCapability
argument_list|()
operator|+
literal|", # Containers: "
operator|+
name|getNumContainers
argument_list|()
operator|+
literal|", Concurrency: "
operator|+
name|getConcurrency
argument_list|()
operator|+
literal|", Lease Duration: "
operator|+
name|getDuration
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

