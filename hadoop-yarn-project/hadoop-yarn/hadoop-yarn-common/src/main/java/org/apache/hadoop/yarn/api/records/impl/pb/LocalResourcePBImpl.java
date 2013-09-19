begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|LocalResource
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
name|LocalResourceType
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
name|LocalResourceVisibility
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
name|URL
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
name|LocalResourceProto
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
name|LocalResourceProtoOrBuilder
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
name|LocalResourceTypeProto
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
name|LocalResourceVisibilityProto
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
name|URLProto
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
DECL|class|LocalResourcePBImpl
specifier|public
class|class
name|LocalResourcePBImpl
extends|extends
name|LocalResource
block|{
DECL|field|proto
name|LocalResourceProto
name|proto
init|=
name|LocalResourceProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|LocalResourceProto
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
DECL|field|url
specifier|private
name|URL
name|url
init|=
literal|null
decl_stmt|;
DECL|method|LocalResourcePBImpl ()
specifier|public
name|LocalResourcePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|LocalResourceProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|LocalResourcePBImpl (LocalResourceProto proto)
specifier|public
name|LocalResourcePBImpl
parameter_list|(
name|LocalResourceProto
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
name|LocalResourceProto
name|getProto
parameter_list|()
block|{
name|mergeLocalToBuilder
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
DECL|method|mergeLocalToBuilder ()
specifier|private
specifier|synchronized
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
name|LocalResourceProtoOrBuilder
name|l
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
name|url
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|l
operator|.
name|getResource
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|URLPBImpl
operator|)
name|url
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|l
operator|=
name|builder
expr_stmt|;
name|builder
operator|.
name|setResource
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|maybeInitBuilder ()
specifier|private
specifier|synchronized
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
name|LocalResourceProto
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
DECL|method|getSize ()
specifier|public
specifier|synchronized
name|long
name|getSize
parameter_list|()
block|{
name|LocalResourceProtoOrBuilder
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
name|getSize
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setSize (long size)
specifier|public
specifier|synchronized
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setSize
argument_list|(
operator|(
name|size
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTimestamp ()
specifier|public
specifier|synchronized
name|long
name|getTimestamp
parameter_list|()
block|{
name|LocalResourceProtoOrBuilder
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
name|getTimestamp
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setTimestamp (long timestamp)
specifier|public
specifier|synchronized
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setTimestamp
argument_list|(
operator|(
name|timestamp
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
specifier|synchronized
name|LocalResourceType
name|getType
parameter_list|()
block|{
name|LocalResourceProtoOrBuilder
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
name|hasType
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
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setType (LocalResourceType type)
specifier|public
specifier|synchronized
name|void
name|setType
parameter_list|(
name|LocalResourceType
name|type
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearType
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setType
argument_list|(
name|convertToProtoFormat
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResource ()
specifier|public
specifier|synchronized
name|URL
name|getResource
parameter_list|()
block|{
name|LocalResourceProtoOrBuilder
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
name|url
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|url
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasResource
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|url
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|url
return|;
block|}
annotation|@
name|Override
DECL|method|setResource (URL resource)
specifier|public
specifier|synchronized
name|void
name|setResource
parameter_list|(
name|URL
name|resource
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearResource
argument_list|()
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|resource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getVisibility ()
specifier|public
specifier|synchronized
name|LocalResourceVisibility
name|getVisibility
parameter_list|()
block|{
name|LocalResourceProtoOrBuilder
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
name|hasVisibility
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
name|getVisibility
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setVisibility (LocalResourceVisibility visibility)
specifier|public
specifier|synchronized
name|void
name|setVisibility
parameter_list|(
name|LocalResourceVisibility
name|visibility
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|visibility
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearVisibility
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setVisibility
argument_list|(
name|convertToProtoFormat
argument_list|(
name|visibility
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPattern ()
specifier|public
specifier|synchronized
name|String
name|getPattern
parameter_list|()
block|{
name|LocalResourceProtoOrBuilder
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
name|hasPattern
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
name|getPattern
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setPattern (String pattern)
specifier|public
specifier|synchronized
name|void
name|setPattern
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|pattern
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearPattern
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setPattern
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
DECL|method|convertToProtoFormat (LocalResourceType e)
specifier|private
name|LocalResourceTypeProto
name|convertToProtoFormat
parameter_list|(
name|LocalResourceType
name|e
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|e
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (LocalResourceTypeProto e)
specifier|private
name|LocalResourceType
name|convertFromProtoFormat
parameter_list|(
name|LocalResourceTypeProto
name|e
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|e
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (URLProto p)
specifier|private
name|URLPBImpl
name|convertFromProtoFormat
parameter_list|(
name|URLProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|URLPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (URL t)
specifier|private
name|URLProto
name|convertToProtoFormat
parameter_list|(
name|URL
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|URLPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertToProtoFormat (LocalResourceVisibility e)
specifier|private
name|LocalResourceVisibilityProto
name|convertToProtoFormat
parameter_list|(
name|LocalResourceVisibility
name|e
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|e
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (LocalResourceVisibilityProto e)
specifier|private
name|LocalResourceVisibility
name|convertFromProtoFormat
parameter_list|(
name|LocalResourceVisibilityProto
name|e
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
end_class

end_unit

