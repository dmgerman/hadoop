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
name|IncreaseContainersResourceResponse
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
name|SerializedException
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
name|ContainerIdPBImpl
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
name|SerializedExceptionPBImpl
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
name|SerializedExceptionProto
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
name|ContainerExceptionMapProto
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
name|IncreaseContainersResourceResponseProto
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
name|IncreaseContainersResourceResponseProtoOrBuilder
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
DECL|class|IncreaseContainersResourceResponsePBImpl
specifier|public
class|class
name|IncreaseContainersResourceResponsePBImpl
extends|extends
name|IncreaseContainersResourceResponse
block|{
DECL|field|proto
name|IncreaseContainersResourceResponseProto
name|proto
init|=
name|IncreaseContainersResourceResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|IncreaseContainersResourceResponseProto
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
DECL|field|succeededRequests
specifier|private
name|List
argument_list|<
name|ContainerId
argument_list|>
name|succeededRequests
init|=
literal|null
decl_stmt|;
DECL|field|failedRequests
specifier|private
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|SerializedException
argument_list|>
name|failedRequests
init|=
literal|null
decl_stmt|;
DECL|method|IncreaseContainersResourceResponsePBImpl ()
specifier|public
name|IncreaseContainersResourceResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|IncreaseContainersResourceResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|IncreaseContainersResourceResponsePBImpl ( IncreaseContainersResourceResponseProto proto)
specifier|public
name|IncreaseContainersResourceResponsePBImpl
parameter_list|(
name|IncreaseContainersResourceResponseProto
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
name|IncreaseContainersResourceResponseProto
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
name|succeededRequests
operator|!=
literal|null
condition|)
block|{
name|addSucceededRequestsToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|failedRequests
operator|!=
literal|null
condition|)
block|{
name|addFailedRequestsToProto
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
name|IncreaseContainersResourceResponseProto
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
DECL|method|getSuccessfullyIncreasedContainers ()
specifier|public
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getSuccessfullyIncreasedContainers
parameter_list|()
block|{
name|initSucceededRequests
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|succeededRequests
return|;
block|}
annotation|@
name|Override
DECL|method|setSuccessfullyIncreasedContainers ( List<ContainerId> succeededRequests)
specifier|public
name|void
name|setSuccessfullyIncreasedContainers
parameter_list|(
name|List
argument_list|<
name|ContainerId
argument_list|>
name|succeededRequests
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|succeededRequests
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearSucceededRequests
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|succeededRequests
operator|=
name|succeededRequests
expr_stmt|;
block|}
DECL|method|initSucceededRequests ()
specifier|private
name|void
name|initSucceededRequests
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|succeededRequests
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|IncreaseContainersResourceResponseProtoOrBuilder
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
name|ContainerIdProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getSucceededRequestsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|succeededRequests
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerIdProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|succeededRequests
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
DECL|method|addSucceededRequestsToProto ()
specifier|private
name|void
name|addSucceededRequestsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearSucceededRequests
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|succeededRequests
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|ContainerIdProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|ContainerIdProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ContainerIdProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ContainerIdProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ContainerId
argument_list|>
name|iter
init|=
name|succeededRequests
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
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ContainerIdProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
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
name|builder
operator|.
name|addAllSucceededRequests
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFailedRequests ()
specifier|public
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|SerializedException
argument_list|>
name|getFailedRequests
parameter_list|()
block|{
name|initFailedRequests
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|failedRequests
return|;
block|}
annotation|@
name|Override
DECL|method|setFailedRequests ( Map<ContainerId, SerializedException> failedRequests)
specifier|public
name|void
name|setFailedRequests
parameter_list|(
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|SerializedException
argument_list|>
name|failedRequests
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|failedRequests
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearFailedRequests
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|failedRequests
operator|=
name|failedRequests
expr_stmt|;
block|}
DECL|method|initFailedRequests ()
specifier|private
name|void
name|initFailedRequests
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|failedRequests
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|IncreaseContainersResourceResponseProtoOrBuilder
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
name|ContainerExceptionMapProto
argument_list|>
name|protoList
init|=
name|p
operator|.
name|getFailedRequestsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|failedRequests
operator|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|SerializedException
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerExceptionMapProto
name|ce
range|:
name|protoList
control|)
block|{
name|this
operator|.
name|failedRequests
operator|.
name|put
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|ce
operator|.
name|getContainerId
argument_list|()
argument_list|)
argument_list|,
name|convertFromProtoFormat
argument_list|(
name|ce
operator|.
name|getException
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addFailedRequestsToProto ()
specifier|private
name|void
name|addFailedRequestsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearFailedRequests
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|failedRequests
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|List
argument_list|<
name|ContainerExceptionMapProto
argument_list|>
name|protoList
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerExceptionMapProto
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|SerializedException
argument_list|>
name|entry
range|:
name|this
operator|.
name|failedRequests
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|protoList
operator|.
name|add
argument_list|(
name|ContainerExceptionMapProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setException
argument_list|(
name|convertToProtoFormat
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addAllFailedRequests
argument_list|(
name|protoList
argument_list|)
expr_stmt|;
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
DECL|method|convertFromProtoFormat ( SerializedExceptionProto p)
specifier|private
name|SerializedExceptionPBImpl
name|convertFromProtoFormat
parameter_list|(
name|SerializedExceptionProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|SerializedExceptionPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (SerializedException t)
specifier|private
name|SerializedExceptionProto
name|convertToProtoFormat
parameter_list|(
name|SerializedException
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|SerializedExceptionPBImpl
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

