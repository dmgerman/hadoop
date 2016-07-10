begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.records.impl.pb
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
name|ApplicationId
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
name|ApplicationIdPBImpl
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
name|records
operator|.
name|AppCollectorsMap
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
name|ApplicationIdProto
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
operator|.
name|AppCollectorsMapProto
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
operator|.
name|AppCollectorsMapProtoOrBuilder
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
DECL|class|AppCollectorsMapPBImpl
specifier|public
class|class
name|AppCollectorsMapPBImpl
extends|extends
name|AppCollectorsMap
block|{
DECL|field|proto
specifier|private
name|AppCollectorsMapProto
name|proto
init|=
name|AppCollectorsMapProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|AppCollectorsMapProto
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
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
init|=
literal|null
decl_stmt|;
DECL|field|collectorAddr
specifier|private
name|String
name|collectorAddr
init|=
literal|null
decl_stmt|;
DECL|method|AppCollectorsMapPBImpl ()
specifier|public
name|AppCollectorsMapPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|AppCollectorsMapProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|AppCollectorsMapPBImpl (AppCollectorsMapProto proto)
specifier|public
name|AppCollectorsMapPBImpl
parameter_list|(
name|AppCollectorsMapProto
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
name|AppCollectorsMapProto
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
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
name|AppCollectorsMapProtoOrBuilder
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
name|appId
operator|==
literal|null
operator|&&
name|p
operator|.
name|hasAppId
argument_list|()
condition|)
block|{
name|this
operator|.
name|appId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getAppId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|appId
return|;
block|}
annotation|@
name|Override
DECL|method|getCollectorAddr ()
specifier|public
name|String
name|getCollectorAddr
parameter_list|()
block|{
name|AppCollectorsMapProtoOrBuilder
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
name|collectorAddr
operator|==
literal|null
operator|&&
name|p
operator|.
name|hasAppCollectorAddr
argument_list|()
condition|)
block|{
name|this
operator|.
name|collectorAddr
operator|=
name|p
operator|.
name|getAppCollectorAddr
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|collectorAddr
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationId (ApplicationId id)
specifier|public
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAppId
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|appId
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCollectorAddr (String collectorAddr)
specifier|public
name|void
name|setCollectorAddr
parameter_list|(
name|String
name|collectorAddr
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|collectorAddr
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAppCollectorAddr
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|collectorAddr
operator|=
name|collectorAddr
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ApplicationIdProto p)
specifier|private
name|ApplicationIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ApplicationIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ApplicationIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ApplicationId t)
specifier|private
name|ApplicationIdProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationIdPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
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
name|AppCollectorsMapProto
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
name|appId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setAppId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|appId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|collectorAddr
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setAppCollectorAddr
argument_list|(
name|this
operator|.
name|collectorAddr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

