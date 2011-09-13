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
name|ContainerId
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
name|ContainerIdProto
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
name|ResourceProto
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

begin_class
DECL|class|ContainerLaunchContextPBImpl
specifier|public
class|class
name|ContainerLaunchContextPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|ContainerLaunchContextProto
argument_list|>
implements|implements
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
DECL|field|containerId
specifier|private
name|ContainerId
name|containerId
init|=
literal|null
decl_stmt|;
DECL|field|resource
specifier|private
name|Resource
name|resource
init|=
literal|null
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
DECL|field|containerTokens
specifier|private
name|ByteBuffer
name|containerTokens
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
DECL|field|env
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
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
name|containerId
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ContainerIdPBImpl
operator|)
name|containerId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getContainerId
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setContainerId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|resource
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|this
operator|.
name|resource
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getResource
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setResource
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|resource
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|containerTokens
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setContainerTokens
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|containerTokens
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
name|env
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
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
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
name|resource
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|resource
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
name|resource
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
name|resource
return|;
block|}
annotation|@
name|Override
DECL|method|setResource (Resource resource)
specifier|public
name|void
name|setResource
parameter_list|(
name|Resource
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
name|resource
operator|=
name|resource
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
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
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
name|containerId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|containerId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasContainerId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|containerId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|containerId
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerId (ContainerId containerId)
specifier|public
name|void
name|setContainerId
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|containerId
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearContainerId
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|containerId
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
DECL|method|getContainerTokens ()
specifier|public
name|ByteBuffer
name|getContainerTokens
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
name|containerTokens
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|containerTokens
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasContainerTokens
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|containerTokens
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getContainerTokens
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|containerTokens
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerTokens (ByteBuffer containerTokens)
specifier|public
name|void
name|setContainerTokens
parameter_list|(
name|ByteBuffer
name|containerTokens
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|containerTokens
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearContainerTokens
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerTokens
operator|=
name|containerTokens
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
DECL|method|getEnv ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEnv
parameter_list|()
block|{
name|initEnv
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|env
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
name|env
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
name|getEnvList
argument_list|()
decl_stmt|;
name|this
operator|.
name|env
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
name|env
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
DECL|method|setEnv (final Map<String, String> env)
specifier|public
name|void
name|setEnv
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
name|env
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|env
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
name|clearEnv
argument_list|()
expr_stmt|;
if|if
condition|(
name|env
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
name|env
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
name|env
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
name|addAllEnv
argument_list|(
name|iterable
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
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (ContainerIdProto p)
specifier|private
name|ContainerIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ContainerId t)
specifier|private
name|ContainerIdProto
name|convertToProtoFormat
parameter_list|(
name|ContainerId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerIdPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
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

