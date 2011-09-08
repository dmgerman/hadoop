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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

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
name|Iterator
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
name|protocolrecords
operator|.
name|StartContainerResponse
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
name|ProtoBase
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
name|StartContainerResponseProto
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
name|StartContainerResponseProtoOrBuilder
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
name|StringBytesMapProto
import|;
end_import

begin_class
DECL|class|StartContainerResponsePBImpl
specifier|public
class|class
name|StartContainerResponsePBImpl
extends|extends
name|ProtoBase
argument_list|<
name|StartContainerResponseProto
argument_list|>
implements|implements
name|StartContainerResponse
block|{
DECL|field|proto
name|StartContainerResponseProto
name|proto
init|=
name|StartContainerResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|StartContainerResponseProto
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
DECL|field|serviceResponse
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceResponse
init|=
literal|null
decl_stmt|;
DECL|method|StartContainerResponsePBImpl ()
specifier|public
name|StartContainerResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|StartContainerResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|StartContainerResponsePBImpl (StartContainerResponseProto proto)
specifier|public
name|StartContainerResponsePBImpl
parameter_list|(
name|StartContainerResponseProto
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
name|StartContainerResponseProto
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
specifier|synchronized
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|serviceResponse
operator|!=
literal|null
condition|)
block|{
name|addServiceResponseToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mergeLocalToProto ()
specifier|private
specifier|synchronized
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
name|StartContainerResponseProto
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
DECL|method|getAllServiceResponse ()
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|getAllServiceResponse
parameter_list|()
block|{
name|initServiceResponse
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|serviceResponse
return|;
block|}
annotation|@
name|Override
DECL|method|getServiceResponse (String key)
specifier|public
specifier|synchronized
name|ByteBuffer
name|getServiceResponse
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|initServiceResponse
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|serviceResponse
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|initServiceResponse ()
specifier|private
specifier|synchronized
name|void
name|initServiceResponse
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|serviceResponse
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|StartContainerResponseProtoOrBuilder
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
name|StringBytesMapProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getServiceResponseList
argument_list|()
decl_stmt|;
name|this
operator|.
name|serviceResponse
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|StringBytesMapProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|serviceResponse
operator|.
name|put
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
argument_list|,
name|convertFromProtoFormat
argument_list|(
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addAllServiceResponse (final Map<String, ByteBuffer> serviceResponse)
specifier|public
specifier|synchronized
name|void
name|addAllServiceResponse
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceResponse
parameter_list|)
block|{
if|if
condition|(
name|serviceResponse
operator|==
literal|null
condition|)
return|return;
name|initServiceResponse
argument_list|()
expr_stmt|;
name|this
operator|.
name|serviceResponse
operator|.
name|putAll
argument_list|(
name|serviceResponse
argument_list|)
expr_stmt|;
block|}
DECL|method|addServiceResponseToProto ()
specifier|private
specifier|synchronized
name|void
name|addServiceResponseToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearServiceResponse
argument_list|()
expr_stmt|;
if|if
condition|(
name|serviceResponse
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|StringBytesMapProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|StringBytesMapProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|StringBytesMapProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|StringBytesMapProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|keyIter
init|=
name|serviceResponse
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
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
specifier|synchronized
name|StringBytesMapProto
name|next
parameter_list|()
block|{
name|String
name|key
init|=
name|keyIter
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|StringBytesMapProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|key
argument_list|)
operator|.
name|setValue
argument_list|(
name|convertToProtoFormat
argument_list|(
name|serviceResponse
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|keyIter
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
name|addAllServiceResponse
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setServiceResponse (String key, ByteBuffer val)
specifier|public
specifier|synchronized
name|void
name|setServiceResponse
parameter_list|(
name|String
name|key
parameter_list|,
name|ByteBuffer
name|val
parameter_list|)
block|{
name|initServiceResponse
argument_list|()
expr_stmt|;
name|this
operator|.
name|serviceResponse
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeServiceResponse (String key)
specifier|public
specifier|synchronized
name|void
name|removeServiceResponse
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|initServiceResponse
argument_list|()
expr_stmt|;
name|this
operator|.
name|serviceResponse
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearServiceResponse ()
specifier|public
specifier|synchronized
name|void
name|clearServiceResponse
parameter_list|()
block|{
name|initServiceResponse
argument_list|()
expr_stmt|;
name|this
operator|.
name|serviceResponse
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

