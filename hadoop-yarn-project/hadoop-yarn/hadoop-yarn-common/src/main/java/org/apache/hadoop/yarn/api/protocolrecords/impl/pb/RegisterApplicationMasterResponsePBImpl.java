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
name|RegisterApplicationMasterResponse
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
name|Container
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
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|ContainerPBImpl
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
name|ProtoUtils
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
name|ResourcePBImpl
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
name|ContainerProto
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
name|YarnServiceProtos
operator|.
name|RegisterApplicationMasterResponseProto
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
name|RegisterApplicationMasterResponseProtoOrBuilder
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
DECL|class|RegisterApplicationMasterResponsePBImpl
specifier|public
class|class
name|RegisterApplicationMasterResponsePBImpl
extends|extends
name|RegisterApplicationMasterResponse
block|{
DECL|field|proto
name|RegisterApplicationMasterResponseProto
name|proto
init|=
name|RegisterApplicationMasterResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|RegisterApplicationMasterResponseProto
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
DECL|field|maximumResourceCapability
specifier|private
name|Resource
name|maximumResourceCapability
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
DECL|field|containersFromPreviousAttempt
specifier|private
name|List
argument_list|<
name|Container
argument_list|>
name|containersFromPreviousAttempt
init|=
literal|null
decl_stmt|;
DECL|method|RegisterApplicationMasterResponsePBImpl ()
specifier|public
name|RegisterApplicationMasterResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|RegisterApplicationMasterResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|RegisterApplicationMasterResponsePBImpl (RegisterApplicationMasterResponseProto proto)
specifier|public
name|RegisterApplicationMasterResponsePBImpl
parameter_list|(
name|RegisterApplicationMasterResponseProto
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
name|RegisterApplicationMasterResponseProto
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
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
argument_list|)
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
name|maximumResourceCapability
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setMaximumCapability
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|maximumResourceCapability
argument_list|)
argument_list|)
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
if|if
condition|(
name|this
operator|.
name|containersFromPreviousAttempt
operator|!=
literal|null
condition|)
block|{
name|addRunningContainersToProto
argument_list|()
expr_stmt|;
block|}
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
name|RegisterApplicationMasterResponseProto
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
DECL|method|getMaximumResourceCapability ()
specifier|public
name|Resource
name|getMaximumResourceCapability
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|maximumResourceCapability
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|maximumResourceCapability
return|;
block|}
name|RegisterApplicationMasterResponseProtoOrBuilder
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
name|hasMaximumCapability
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|maximumResourceCapability
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getMaximumCapability
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|maximumResourceCapability
return|;
block|}
annotation|@
name|Override
DECL|method|setMaximumResourceCapability (Resource capability)
specifier|public
name|void
name|setMaximumResourceCapability
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
name|maximumResourceCapability
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearMaximumCapability
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|maximumResourceCapability
operator|=
name|capability
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
name|RegisterApplicationMasterResponseProtoOrBuilder
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
annotation|@
name|Override
DECL|method|setClientToAMTokenMasterKey (ByteBuffer key)
specifier|public
name|void
name|setClientToAMTokenMasterKey
parameter_list|(
name|ByteBuffer
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearClientToAmTokenMasterKey
argument_list|()
expr_stmt|;
return|return;
block|}
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setClientToAmTokenMasterKey
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getClientToAMTokenMasterKey ()
specifier|public
name|ByteBuffer
name|getClientToAMTokenMasterKey
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|ByteBuffer
name|key
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getClientToAmTokenMasterKey
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|getContainersFromPreviousAttempt ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getContainersFromPreviousAttempt
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|containersFromPreviousAttempt
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|containersFromPreviousAttempt
return|;
block|}
name|initRunningContainersList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|containersFromPreviousAttempt
return|;
block|}
annotation|@
name|Override
DECL|method|setContainersFromPreviousAttempt (final List<Container> containers)
specifier|public
name|void
name|setContainersFromPreviousAttempt
parameter_list|(
specifier|final
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
block|{
if|if
condition|(
name|containers
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|containersFromPreviousAttempt
operator|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|containersFromPreviousAttempt
operator|.
name|addAll
argument_list|(
name|containers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
name|RegisterApplicationMasterResponseProtoOrBuilder
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
name|hasQueue
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
name|getQueue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setQueue (String queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearQueue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initRunningContainersList ()
specifier|private
name|void
name|initRunningContainersList
parameter_list|()
block|{
name|RegisterApplicationMasterResponseProtoOrBuilder
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
name|ContainerProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getContainersFromPreviousAttemptList
argument_list|()
decl_stmt|;
name|containersFromPreviousAttempt
operator|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerProto
name|c
range|:
name|list
control|)
block|{
name|containersFromPreviousAttempt
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addRunningContainersToProto ()
specifier|private
name|void
name|addRunningContainersToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearContainersFromPreviousAttempt
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ContainerProto
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerProto
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Container
name|c
range|:
name|containersFromPreviousAttempt
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|convertToProtoFormat
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addAllContainersFromPreviousAttempt
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ResourceProto resource)
specifier|private
name|Resource
name|convertFromProtoFormat
parameter_list|(
name|ResourceProto
name|resource
parameter_list|)
block|{
return|return
operator|new
name|ResourcePBImpl
argument_list|(
name|resource
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Resource resource)
specifier|private
name|ResourceProto
name|convertToProtoFormat
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|resource
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (ContainerProto p)
specifier|private
name|ContainerPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Container t)
specifier|private
name|ContainerProto
name|convertToProtoFormat
parameter_list|(
name|Container
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerPBImpl
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

