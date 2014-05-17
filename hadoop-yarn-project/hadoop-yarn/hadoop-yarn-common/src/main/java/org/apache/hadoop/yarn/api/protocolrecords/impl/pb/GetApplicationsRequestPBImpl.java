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
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|math
operator|.
name|LongRange
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
name|ApplicationsRequestScope
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
name|GetApplicationsRequest
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
name|YarnApplicationState
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
name|proto
operator|.
name|YarnProtos
operator|.
name|YarnApplicationStateProto
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
name|GetApplicationsRequestProto
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
name|GetApplicationsRequestProtoOrBuilder
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
DECL|class|GetApplicationsRequestPBImpl
specifier|public
class|class
name|GetApplicationsRequestPBImpl
extends|extends
name|GetApplicationsRequest
block|{
DECL|field|proto
name|GetApplicationsRequestProto
name|proto
init|=
name|GetApplicationsRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetApplicationsRequestProto
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
DECL|field|applicationTypes
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
init|=
literal|null
decl_stmt|;
DECL|field|applicationStates
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|applicationStates
init|=
literal|null
decl_stmt|;
DECL|field|users
name|Set
argument_list|<
name|String
argument_list|>
name|users
init|=
literal|null
decl_stmt|;
DECL|field|queues
name|Set
argument_list|<
name|String
argument_list|>
name|queues
init|=
literal|null
decl_stmt|;
DECL|field|limit
name|long
name|limit
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|start
DECL|field|finish
name|LongRange
name|start
init|=
literal|null
decl_stmt|,
name|finish
init|=
literal|null
decl_stmt|;
DECL|field|applicationTags
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTags
decl_stmt|;
DECL|field|scope
specifier|private
name|ApplicationsRequestScope
name|scope
decl_stmt|;
DECL|method|GetApplicationsRequestPBImpl ()
specifier|public
name|GetApplicationsRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetApplicationsRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetApplicationsRequestPBImpl (GetApplicationsRequestProto proto)
specifier|public
name|GetApplicationsRequestPBImpl
parameter_list|(
name|GetApplicationsRequestProto
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
name|GetApplicationsRequestProto
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
name|applicationTypes
operator|!=
literal|null
condition|)
block|{
name|addLocalApplicationTypesToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|applicationStates
operator|!=
literal|null
condition|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearApplicationStates
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|YarnApplicationStateProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|YarnApplicationStateProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|YarnApplicationStateProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|YarnApplicationStateProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|YarnApplicationState
argument_list|>
name|iter
init|=
name|applicationStates
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
name|YarnApplicationStateProto
name|next
parameter_list|()
block|{
return|return
name|ProtoUtils
operator|.
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
name|addAllApplicationStates
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|applicationTags
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|applicationTags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addAllApplicationTags
argument_list|(
name|this
operator|.
name|applicationTags
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|scope
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setScope
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|scope
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|start
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setStartBegin
argument_list|(
name|start
operator|.
name|getMinimumLong
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setStartEnd
argument_list|(
name|start
operator|.
name|getMaximumLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|finish
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setFinishBegin
argument_list|(
name|finish
operator|.
name|getMinimumLong
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setFinishEnd
argument_list|(
name|finish
operator|.
name|getMaximumLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|users
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|users
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addAllUsers
argument_list|(
name|this
operator|.
name|users
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|queues
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|queues
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addAllQueues
argument_list|(
name|this
operator|.
name|queues
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addLocalApplicationTypesToProto ()
specifier|private
name|void
name|addLocalApplicationTypesToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearApplicationTypes
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|applicationTypes
operator|==
literal|null
condition|)
return|return;
name|builder
operator|.
name|addAllApplicationTypes
argument_list|(
name|applicationTypes
argument_list|)
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
name|GetApplicationsRequestProto
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
DECL|method|initApplicationTypes ()
specifier|private
name|void
name|initApplicationTypes
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationTypes
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetApplicationsRequestProtoOrBuilder
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
name|appTypeList
init|=
name|p
operator|.
name|getApplicationTypesList
argument_list|()
decl_stmt|;
name|this
operator|.
name|applicationTypes
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationTypes
operator|.
name|addAll
argument_list|(
name|appTypeList
argument_list|)
expr_stmt|;
block|}
DECL|method|initApplicationStates ()
specifier|private
name|void
name|initApplicationStates
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationStates
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetApplicationsRequestProtoOrBuilder
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
name|YarnApplicationStateProto
argument_list|>
name|appStatesList
init|=
name|p
operator|.
name|getApplicationStatesList
argument_list|()
decl_stmt|;
name|this
operator|.
name|applicationStates
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|YarnApplicationState
operator|.
name|class
argument_list|)
expr_stmt|;
for|for
control|(
name|YarnApplicationStateProto
name|c
range|:
name|appStatesList
control|)
block|{
name|this
operator|.
name|applicationStates
operator|.
name|add
argument_list|(
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initUsers ()
specifier|private
name|void
name|initUsers
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|users
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetApplicationsRequestProtoOrBuilder
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
name|usersList
init|=
name|p
operator|.
name|getUsersList
argument_list|()
decl_stmt|;
name|this
operator|.
name|users
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|users
operator|.
name|addAll
argument_list|(
name|usersList
argument_list|)
expr_stmt|;
block|}
DECL|method|initQueues ()
specifier|private
name|void
name|initQueues
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|queues
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetApplicationsRequestProtoOrBuilder
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
name|queuesList
init|=
name|p
operator|.
name|getQueuesList
argument_list|()
decl_stmt|;
name|this
operator|.
name|queues
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|queues
operator|.
name|addAll
argument_list|(
name|queuesList
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationTypes ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getApplicationTypes
parameter_list|()
block|{
name|initApplicationTypes
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|applicationTypes
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationTypes (Set<String> applicationTypes)
specifier|public
name|void
name|setApplicationTypes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationTypes
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearApplicationTypes
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationTypes
operator|=
name|applicationTypes
expr_stmt|;
block|}
DECL|method|initApplicationTags ()
specifier|private
name|void
name|initApplicationTags
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationTags
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetApplicationsRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|this
operator|.
name|applicationTags
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationTags
operator|.
name|addAll
argument_list|(
name|p
operator|.
name|getApplicationTagsList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationTags ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getApplicationTags
parameter_list|()
block|{
name|initApplicationTags
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|applicationTags
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationTags (Set<String> tags)
specifier|public
name|void
name|setApplicationTags
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|tags
operator|==
literal|null
operator|||
name|tags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|clearApplicationTags
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationTags
operator|=
literal|null
expr_stmt|;
return|return;
block|}
comment|// Convert applicationTags to lower case and add
name|this
operator|.
name|applicationTags
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|tags
control|)
block|{
name|this
operator|.
name|applicationTags
operator|.
name|add
argument_list|(
name|tag
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getApplicationStates ()
specifier|public
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|getApplicationStates
parameter_list|()
block|{
name|initApplicationStates
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|applicationStates
return|;
block|}
DECL|method|initScope ()
specifier|private
name|void
name|initScope
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|scope
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetApplicationsRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|this
operator|.
name|scope
operator|=
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getScope ()
specifier|public
name|ApplicationsRequestScope
name|getScope
parameter_list|()
block|{
name|initScope
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|scope
return|;
block|}
DECL|method|setScope (ApplicationsRequestScope scope)
specifier|public
name|void
name|setScope
parameter_list|(
name|ApplicationsRequestScope
name|scope
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|scope
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearScope
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setApplicationStates (EnumSet<YarnApplicationState> applicationStates)
specifier|public
name|void
name|setApplicationStates
parameter_list|(
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|applicationStates
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationStates
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearApplicationStates
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|applicationStates
operator|=
name|applicationStates
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setApplicationStates (Set<String> applicationStates)
specifier|public
name|void
name|setApplicationStates
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|applicationStates
parameter_list|)
block|{
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|appStates
init|=
literal|null
decl_stmt|;
for|for
control|(
name|YarnApplicationState
name|state
range|:
name|YarnApplicationState
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|applicationStates
operator|.
name|contains
argument_list|(
name|state
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|appStates
operator|==
literal|null
condition|)
block|{
name|appStates
operator|=
name|EnumSet
operator|.
name|of
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|appStates
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|setApplicationStates
argument_list|(
name|appStates
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUsers ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getUsers
parameter_list|()
block|{
name|initUsers
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|users
return|;
block|}
DECL|method|setUsers (Set<String> users)
specifier|public
name|void
name|setUsers
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|users
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|users
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearUsers
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|users
operator|=
name|users
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQueues ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getQueues
parameter_list|()
block|{
name|initQueues
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|queues
return|;
block|}
annotation|@
name|Override
DECL|method|setQueues (Set<String> queues)
specifier|public
name|void
name|setQueues
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|queues
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|queues
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearQueues
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|queues
operator|=
name|queues
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLimit ()
specifier|public
name|long
name|getLimit
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|limit
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|GetApplicationsRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|this
operator|.
name|limit
operator|=
name|p
operator|.
name|hasLimit
argument_list|()
condition|?
name|p
operator|.
name|getLimit
argument_list|()
else|:
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
return|return
name|this
operator|.
name|limit
return|;
block|}
annotation|@
name|Override
DECL|method|setLimit (long limit)
specifier|public
name|void
name|setLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStartRange ()
specifier|public
name|LongRange
name|getStartRange
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|start
operator|==
literal|null
condition|)
block|{
name|GetApplicationsRequestProtoOrBuilder
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
name|p
operator|.
name|hasStartBegin
argument_list|()
operator|||
name|p
operator|.
name|hasStartEnd
argument_list|()
condition|)
block|{
name|long
name|begin
init|=
name|p
operator|.
name|hasStartBegin
argument_list|()
condition|?
name|p
operator|.
name|getStartBegin
argument_list|()
else|:
literal|0L
decl_stmt|;
name|long
name|end
init|=
name|p
operator|.
name|hasStartEnd
argument_list|()
condition|?
name|p
operator|.
name|getStartEnd
argument_list|()
else|:
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|this
operator|.
name|start
operator|=
operator|new
name|LongRange
argument_list|(
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
operator|.
name|start
return|;
block|}
annotation|@
name|Override
DECL|method|setStartRange (long begin, long end)
specifier|public
name|void
name|setStartRange
parameter_list|(
name|long
name|begin
parameter_list|,
name|long
name|end
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|begin
operator|>
name|end
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"begin> end in range (begin, "
operator|+
literal|"end): ("
operator|+
name|begin
operator|+
literal|", "
operator|+
name|end
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|start
operator|=
operator|new
name|LongRange
argument_list|(
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFinishRange ()
specifier|public
name|LongRange
name|getFinishRange
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|finish
operator|==
literal|null
condition|)
block|{
name|GetApplicationsRequestProtoOrBuilder
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
name|p
operator|.
name|hasFinishBegin
argument_list|()
operator|||
name|p
operator|.
name|hasFinishEnd
argument_list|()
condition|)
block|{
name|long
name|begin
init|=
name|p
operator|.
name|hasFinishBegin
argument_list|()
condition|?
name|p
operator|.
name|getFinishBegin
argument_list|()
else|:
literal|0L
decl_stmt|;
name|long
name|end
init|=
name|p
operator|.
name|hasFinishEnd
argument_list|()
condition|?
name|p
operator|.
name|getFinishEnd
argument_list|()
else|:
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|this
operator|.
name|finish
operator|=
operator|new
name|LongRange
argument_list|(
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
operator|.
name|finish
return|;
block|}
annotation|@
name|Override
DECL|method|setFinishRange (long begin, long end)
specifier|public
name|void
name|setFinishRange
parameter_list|(
name|long
name|begin
parameter_list|,
name|long
name|end
parameter_list|)
block|{
if|if
condition|(
name|begin
operator|>
name|end
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"begin> end in range (begin, "
operator|+
literal|"end): ("
operator|+
name|begin
operator|+
literal|", "
operator|+
name|end
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|finish
operator|=
operator|new
name|LongRange
argument_list|(
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

