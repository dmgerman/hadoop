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
name|util
operator|.
name|StringUtils
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
name|ContainerState
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
name|ContainerStatus
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
name|ExecutionType
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
name|ExecutionTypeProto
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
name|ContainerStateProto
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
name|ContainerStatusProto
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
name|ContainerStatusProtoOrBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ContainerStatusPBImpl
specifier|public
class|class
name|ContainerStatusPBImpl
extends|extends
name|ContainerStatus
block|{
DECL|field|proto
name|ContainerStatusProto
name|proto
init|=
name|ContainerStatusProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ContainerStatusProto
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
DECL|field|HOST
specifier|private
specifier|static
specifier|final
name|String
name|HOST
init|=
literal|"HOST"
decl_stmt|;
DECL|field|IPS
specifier|private
specifier|static
specifier|final
name|String
name|IPS
init|=
literal|"IPS"
decl_stmt|;
DECL|field|containerAttributes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|containerAttributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ContainerStatusPBImpl ()
specifier|public
name|ContainerStatusPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ContainerStatusProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ContainerStatusPBImpl (ContainerStatusProto proto)
specifier|public
name|ContainerStatusPBImpl
parameter_list|(
name|ContainerStatusProto
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
name|ContainerStatusProto
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ContainerStatus: ["
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ContainerId: "
argument_list|)
operator|.
name|append
argument_list|(
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ExecutionType: "
argument_list|)
operator|.
name|append
argument_list|(
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"State: "
argument_list|)
operator|.
name|append
argument_list|(
name|getState
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Capability: "
argument_list|)
operator|.
name|append
argument_list|(
name|getCapability
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Diagnostics: "
argument_list|)
operator|.
name|append
argument_list|(
name|getDiagnostics
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ExitStatus: "
argument_list|)
operator|.
name|append
argument_list|(
name|getExitStatus
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"IP: "
argument_list|)
operator|.
name|append
argument_list|(
name|getIPs
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Host: "
argument_list|)
operator|.
name|append
argument_list|(
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
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
name|containerId
operator|!=
literal|null
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
name|containerAttributes
operator|!=
literal|null
operator|&&
operator|!
name|containerAttributes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addContainerAttributesToProto
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
name|ContainerStatusProto
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
DECL|method|addContainerAttributesToProto ()
specifier|private
name|void
name|addContainerAttributesToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearContainerAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|containerAttributes
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|YarnProtos
operator|.
name|StringStringMapProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|YarnProtos
operator|.
name|StringStringMapProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|YarnProtos
operator|.
name|StringStringMapProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|YarnProtos
operator|.
name|StringStringMapProto
argument_list|>
argument_list|()
block|{
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|keyIter
init|=
name|containerAttributes
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
name|YarnProtos
operator|.
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
name|String
name|value
init|=
name|containerAttributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
literal|""
expr_stmt|;
block|}
return|return
name|YarnProtos
operator|.
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
name|value
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
name|addAllContainerAttributes
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|initContainerAttributes ()
specifier|private
name|void
name|initContainerAttributes
parameter_list|()
block|{
name|ContainerStatusProtoOrBuilder
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
name|YarnProtos
operator|.
name|StringStringMapProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getContainerAttributesList
argument_list|()
decl_stmt|;
for|for
control|(
name|YarnProtos
operator|.
name|StringStringMapProto
name|c
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|containerAttributes
operator|.
name|containsKey
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|this
operator|.
name|containerAttributes
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
block|}
annotation|@
name|Override
DECL|method|getExecutionType ()
specifier|public
specifier|synchronized
name|ExecutionType
name|getExecutionType
parameter_list|()
block|{
name|ContainerStatusProtoOrBuilder
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
name|hasExecutionType
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
name|getExecutionType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setExecutionType (ExecutionType executionType)
specifier|public
specifier|synchronized
name|void
name|setExecutionType
parameter_list|(
name|ExecutionType
name|executionType
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|executionType
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearExecutionType
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setExecutionType
argument_list|(
name|convertToProtoFormat
argument_list|(
name|executionType
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
specifier|synchronized
name|ContainerState
name|getState
parameter_list|()
block|{
name|ContainerStatusProtoOrBuilder
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
name|hasState
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
name|getState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setState (ContainerState state)
specifier|public
specifier|synchronized
name|void
name|setState
parameter_list|(
name|ContainerState
name|state
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setState
argument_list|(
name|convertToProtoFormat
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainerId ()
specifier|public
specifier|synchronized
name|ContainerId
name|getContainerId
parameter_list|()
block|{
name|ContainerStatusProtoOrBuilder
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
specifier|synchronized
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
DECL|method|getExitStatus ()
specifier|public
specifier|synchronized
name|int
name|getExitStatus
parameter_list|()
block|{
name|ContainerStatusProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getExitStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setExitStatus (int exitStatus)
specifier|public
specifier|synchronized
name|void
name|setExitStatus
parameter_list|(
name|int
name|exitStatus
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setExitStatus
argument_list|(
name|exitStatus
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
specifier|synchronized
name|String
name|getDiagnostics
parameter_list|()
block|{
name|ContainerStatusProtoOrBuilder
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
name|getDiagnostics
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDiagnostics (String diagnostics)
specifier|public
specifier|synchronized
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCapability ()
specifier|public
specifier|synchronized
name|Resource
name|getCapability
parameter_list|()
block|{
name|ContainerStatusProtoOrBuilder
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
name|hasCapability
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
name|getCapability
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setCapability (Resource capability)
specifier|public
specifier|synchronized
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
block|{
name|builder
operator|.
name|clearCapability
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setCapability
argument_list|(
name|convertToProtoFormat
argument_list|(
name|capability
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getIPs ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|String
argument_list|>
name|getIPs
parameter_list|()
block|{
if|if
condition|(
operator|!
name|containerAttributes
operator|.
name|containsKey
argument_list|(
name|IPS
argument_list|)
condition|)
block|{
name|initContainerAttributes
argument_list|()
expr_stmt|;
block|}
name|String
name|ips
init|=
name|containerAttributes
operator|.
name|get
argument_list|(
operator|(
name|IPS
operator|)
argument_list|)
decl_stmt|;
return|return
name|ips
operator|==
literal|null
condition|?
literal|null
else|:
name|Arrays
operator|.
name|asList
argument_list|(
name|ips
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setIPs (List<String> ips)
specifier|public
specifier|synchronized
name|void
name|setIPs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ips
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|ips
operator|==
literal|null
condition|)
block|{
name|containerAttributes
operator|.
name|remove
argument_list|(
name|IPS
argument_list|)
expr_stmt|;
name|addContainerAttributesToProto
argument_list|()
expr_stmt|;
return|return;
block|}
name|containerAttributes
operator|.
name|put
argument_list|(
name|IPS
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|ips
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHost ()
specifier|public
specifier|synchronized
name|String
name|getHost
parameter_list|()
block|{
if|if
condition|(
name|containerAttributes
operator|.
name|get
argument_list|(
name|HOST
argument_list|)
operator|==
literal|null
condition|)
block|{
name|initContainerAttributes
argument_list|()
expr_stmt|;
block|}
return|return
name|containerAttributes
operator|.
name|get
argument_list|(
name|HOST
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setHost (String host)
specifier|public
specifier|synchronized
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
condition|)
block|{
name|containerAttributes
operator|.
name|remove
argument_list|(
name|HOST
argument_list|)
expr_stmt|;
return|return;
block|}
name|containerAttributes
operator|.
name|put
argument_list|(
name|HOST
argument_list|,
name|host
argument_list|)
expr_stmt|;
block|}
DECL|method|convertToProtoFormat (ContainerState e)
specifier|private
name|ContainerStateProto
name|convertToProtoFormat
parameter_list|(
name|ContainerState
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
DECL|method|convertFromProtoFormat (ContainerStateProto e)
specifier|private
name|ContainerState
name|convertFromProtoFormat
parameter_list|(
name|ContainerStateProto
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
DECL|method|convertFromProtoFormat (ExecutionTypeProto e)
specifier|private
name|ExecutionType
name|convertFromProtoFormat
parameter_list|(
name|ExecutionTypeProto
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
DECL|method|convertToProtoFormat (ExecutionType e)
specifier|private
name|ExecutionTypeProto
name|convertToProtoFormat
parameter_list|(
name|ExecutionType
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
DECL|method|convertToProtoFormat (Resource e)
specifier|private
name|ResourceProto
name|convertToProtoFormat
parameter_list|(
name|Resource
name|e
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|e
operator|)
operator|.
name|getProto
argument_list|()
return|;
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
block|}
end_class

end_unit

