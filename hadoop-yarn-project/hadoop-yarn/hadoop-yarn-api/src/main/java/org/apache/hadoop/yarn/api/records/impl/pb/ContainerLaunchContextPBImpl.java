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
name|ArrayList
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
name|List
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
name|ApplicationAccessType
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
name|ContainerLaunchContext
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
name|proto
operator|.
name|YarnProtos
operator|.
name|ApplicationACLMapProto
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
name|ContainerLaunchContextProto
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
name|ContainerLaunchContextProtoOrBuilder
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
name|StringBytesMapProto
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
name|StringLocalResourceMapProto
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
name|StringStringMapProto
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
name|util
operator|.
name|ProtoUtils
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
name|ByteString
import|;
end_import

begin_class
DECL|class|ContainerLaunchContextPBImpl
specifier|public
class|class
name|ContainerLaunchContextPBImpl
extends|extends
name|ContainerLaunchContext
block|{
DECL|field|proto
name|ContainerLaunchContextProto
name|proto
init|=
name|ContainerLaunchContextProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ContainerLaunchContextProto
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
DECL|field|localResources
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
init|=
literal|null
decl_stmt|;
DECL|field|tokens
specifier|private
name|ByteBuffer
name|tokens
init|=
literal|null
decl_stmt|;
DECL|field|serviceData
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceData
init|=
literal|null
decl_stmt|;
DECL|field|environment
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
literal|null
decl_stmt|;
DECL|field|commands
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
literal|null
decl_stmt|;
DECL|field|applicationACLS
specifier|private
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|applicationACLS
init|=
literal|null
decl_stmt|;
DECL|method|ContainerLaunchContextPBImpl ()
specifier|public
name|ContainerLaunchContextPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ContainerLaunchContextProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ContainerLaunchContextPBImpl (ContainerLaunchContextProto proto)
specifier|public
name|ContainerLaunchContextPBImpl
parameter_list|(
name|ContainerLaunchContextProto
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
name|ContainerLaunchContextProto
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
name|getProto
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|", "
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (ByteString byteString)
specifier|protected
specifier|final
name|ByteBuffer
name|convertFromProtoFormat
parameter_list|(
name|ByteString
name|byteString
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|byteString
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ByteBuffer byteBuffer)
specifier|protected
specifier|final
name|ByteString
name|convertToProtoFormat
parameter_list|(
name|ByteBuffer
name|byteBuffer
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|byteBuffer
argument_list|)
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
name|localResources
operator|!=
literal|null
condition|)
block|{
name|addLocalResourcesToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|tokens
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setTokens
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|tokens
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|serviceData
operator|!=
literal|null
condition|)
block|{
name|addServiceDataToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|environment
operator|!=
literal|null
condition|)
block|{
name|addEnvToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|commands
operator|!=
literal|null
condition|)
block|{
name|addCommandsToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|applicationACLS
operator|!=
literal|null
condition|)
block|{
name|addApplicationACLs
argument_list|()
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
name|ContainerLaunchContextProto
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
DECL|method|getCommands ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getCommands
parameter_list|()
block|{
name|initCommands
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|commands
return|;
block|}
DECL|method|initCommands ()
specifier|private
name|void
name|initCommands
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|commands
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|ContainerLaunchContextProtoOrBuilder
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
name|String
argument_list|>
name|list
init|=
name|p
operator|.
name|getCommandList
argument_list|()
decl_stmt|;
name|this
operator|.
name|commands
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|commands
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setCommands (final List<String> commands)
specifier|public
name|void
name|setCommands
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|commands
parameter_list|)
block|{
if|if
condition|(
name|commands
operator|==
literal|null
condition|)
return|return;
name|initCommands
argument_list|()
expr_stmt|;
name|this
operator|.
name|commands
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|commands
operator|.
name|addAll
argument_list|(
name|commands
argument_list|)
expr_stmt|;
block|}
DECL|method|addCommandsToProto ()
specifier|private
name|void
name|addCommandsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearCommand
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|commands
operator|==
literal|null
condition|)
return|return;
name|builder
operator|.
name|addAllCommand
argument_list|(
name|this
operator|.
name|commands
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
name|ContainerLaunchContextProtoOrBuilder
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
name|hasUser
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getUser
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearUser
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setUser
argument_list|(
operator|(
name|user
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLocalResources ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|getLocalResources
parameter_list|()
block|{
name|initLocalResources
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|localResources
return|;
block|}
DECL|method|initLocalResources ()
specifier|private
name|void
name|initLocalResources
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|localResources
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|ContainerLaunchContextProtoOrBuilder
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
name|StringLocalResourceMapProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getLocalResourcesList
argument_list|()
decl_stmt|;
name|this
operator|.
name|localResources
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|StringLocalResourceMapProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|localResources
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
DECL|method|setLocalResources ( final Map<String, LocalResource> localResources)
specifier|public
name|void
name|setLocalResources
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
parameter_list|)
block|{
if|if
condition|(
name|localResources
operator|==
literal|null
condition|)
return|return;
name|initLocalResources
argument_list|()
expr_stmt|;
name|this
operator|.
name|localResources
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|localResources
operator|.
name|putAll
argument_list|(
name|localResources
argument_list|)
expr_stmt|;
block|}
DECL|method|addLocalResourcesToProto ()
specifier|private
name|void
name|addLocalResourcesToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearLocalResources
argument_list|()
expr_stmt|;
if|if
condition|(
name|localResources
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|StringLocalResourceMapProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|StringLocalResourceMapProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|StringLocalResourceMapProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|StringLocalResourceMapProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|keyIter
init|=
name|localResources
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
name|StringLocalResourceMapProto
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
name|StringLocalResourceMapProto
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
name|localResources
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
name|addAllLocalResources
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTokens ()
specifier|public
name|ByteBuffer
name|getTokens
parameter_list|()
block|{
name|ContainerLaunchContextProtoOrBuilder
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
name|tokens
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|tokens
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasTokens
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|tokens
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getTokens
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|tokens
return|;
block|}
annotation|@
name|Override
DECL|method|setTokens (ByteBuffer tokens)
specifier|public
name|void
name|setTokens
parameter_list|(
name|ByteBuffer
name|tokens
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|tokens
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearTokens
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getServiceData ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|getServiceData
parameter_list|()
block|{
name|initServiceData
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|serviceData
return|;
block|}
DECL|method|initServiceData ()
specifier|private
name|void
name|initServiceData
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|serviceData
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|ContainerLaunchContextProtoOrBuilder
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
name|getServiceDataList
argument_list|()
decl_stmt|;
name|this
operator|.
name|serviceData
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
name|serviceData
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
DECL|method|setServiceData (final Map<String, ByteBuffer> serviceData)
specifier|public
name|void
name|setServiceData
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceData
parameter_list|)
block|{
if|if
condition|(
name|serviceData
operator|==
literal|null
condition|)
return|return;
name|initServiceData
argument_list|()
expr_stmt|;
name|this
operator|.
name|serviceData
operator|.
name|putAll
argument_list|(
name|serviceData
argument_list|)
expr_stmt|;
block|}
DECL|method|addServiceDataToProto ()
specifier|private
name|void
name|addServiceDataToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearServiceData
argument_list|()
expr_stmt|;
if|if
condition|(
name|serviceData
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
name|serviceData
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
name|serviceData
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
name|addAllServiceData
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnvironment ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEnvironment
parameter_list|()
block|{
name|initEnv
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|environment
return|;
block|}
DECL|method|initEnv ()
specifier|private
name|void
name|initEnv
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|environment
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|ContainerLaunchContextProtoOrBuilder
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
name|StringStringMapProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getEnvironmentList
argument_list|()
decl_stmt|;
name|this
operator|.
name|environment
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|StringStringMapProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|environment
operator|.
name|put
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
argument_list|,
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setEnvironment (final Map<String, String> env)
specifier|public
name|void
name|setEnvironment
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
if|if
condition|(
name|env
operator|==
literal|null
condition|)
return|return;
name|initEnv
argument_list|()
expr_stmt|;
name|this
operator|.
name|environment
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|environment
operator|.
name|putAll
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
DECL|method|addEnvToProto ()
specifier|private
name|void
name|addEnvToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearEnvironment
argument_list|()
expr_stmt|;
if|if
condition|(
name|environment
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|StringStringMapProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|StringStringMapProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|StringStringMapProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|StringStringMapProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|keyIter
init|=
name|environment
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
name|StringStringMapProto
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
name|StringStringMapProto
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
operator|(
name|environment
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
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
name|addAllEnvironment
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationACLs ()
specifier|public
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|getApplicationACLs
parameter_list|()
block|{
name|initApplicationACLs
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|applicationACLS
return|;
block|}
DECL|method|initApplicationACLs ()
specifier|private
name|void
name|initApplicationACLs
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationACLS
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|ContainerLaunchContextProtoOrBuilder
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
name|ApplicationACLMapProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getApplicationACLsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|applicationACLS
operator|=
operator|new
name|HashMap
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ApplicationACLMapProto
name|aclProto
range|:
name|list
control|)
block|{
name|this
operator|.
name|applicationACLS
operator|.
name|put
argument_list|(
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|aclProto
operator|.
name|getAccessType
argument_list|()
argument_list|)
argument_list|,
name|aclProto
operator|.
name|getAcl
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addApplicationACLs ()
specifier|private
name|void
name|addApplicationACLs
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearApplicationACLs
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationACLS
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|?
extends|extends
name|ApplicationACLMapProto
argument_list|>
name|values
init|=
operator|new
name|Iterable
argument_list|<
name|ApplicationACLMapProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ApplicationACLMapProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ApplicationACLMapProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ApplicationAccessType
argument_list|>
name|aclsIterator
init|=
name|applicationACLS
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
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|aclsIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ApplicationACLMapProto
name|next
parameter_list|()
block|{
name|ApplicationAccessType
name|key
init|=
name|aclsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|ApplicationACLMapProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAcl
argument_list|(
name|applicationACLS
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|setAccessType
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|key
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
return|;
block|}
block|}
decl_stmt|;
name|this
operator|.
name|builder
operator|.
name|addAllApplicationACLs
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setApplicationACLs ( final Map<ApplicationAccessType, String> appACLs)
specifier|public
name|void
name|setApplicationACLs
parameter_list|(
specifier|final
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|appACLs
parameter_list|)
block|{
if|if
condition|(
name|appACLs
operator|==
literal|null
condition|)
return|return;
name|initApplicationACLs
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationACLS
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationACLS
operator|.
name|putAll
argument_list|(
name|appACLs
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (LocalResourceProto p)
specifier|private
name|LocalResourcePBImpl
name|convertFromProtoFormat
parameter_list|(
name|LocalResourceProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|LocalResourcePBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (LocalResource t)
specifier|private
name|LocalResourceProto
name|convertToProtoFormat
parameter_list|(
name|LocalResource
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|LocalResourcePBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

