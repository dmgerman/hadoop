begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|YarnServerCommonProtos
operator|.
name|HeartbeatResponseProto
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
name|YarnServerCommonProtos
operator|.
name|HeartbeatResponseProtoOrBuilder
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
name|YarnServerCommonProtos
operator|.
name|MasterKeyProto
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
name|YarnServerCommonProtos
operator|.
name|NodeActionProto
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
name|HeartbeatResponse
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
name|MasterKey
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
name|NodeAction
import|;
end_import

begin_class
DECL|class|HeartbeatResponsePBImpl
specifier|public
class|class
name|HeartbeatResponsePBImpl
extends|extends
name|ProtoBase
argument_list|<
name|HeartbeatResponseProto
argument_list|>
implements|implements
name|HeartbeatResponse
block|{
DECL|field|proto
name|HeartbeatResponseProto
name|proto
init|=
name|HeartbeatResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|HeartbeatResponseProto
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
DECL|field|containersToCleanup
specifier|private
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containersToCleanup
init|=
literal|null
decl_stmt|;
DECL|field|applicationsToCleanup
specifier|private
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|applicationsToCleanup
init|=
literal|null
decl_stmt|;
DECL|field|masterKey
specifier|private
name|MasterKey
name|masterKey
init|=
literal|null
decl_stmt|;
DECL|method|HeartbeatResponsePBImpl ()
specifier|public
name|HeartbeatResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|HeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|HeartbeatResponsePBImpl (HeartbeatResponseProto proto)
specifier|public
name|HeartbeatResponsePBImpl
parameter_list|(
name|HeartbeatResponseProto
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
name|HeartbeatResponseProto
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
name|containersToCleanup
operator|!=
literal|null
condition|)
block|{
name|addContainersToCleanupToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|applicationsToCleanup
operator|!=
literal|null
condition|)
block|{
name|addApplicationsToCleanupToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|masterKey
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setMasterKey
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|masterKey
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
name|HeartbeatResponseProto
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
DECL|method|getResponseId ()
specifier|public
name|int
name|getResponseId
parameter_list|()
block|{
name|HeartbeatResponseProtoOrBuilder
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
name|getResponseId
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setResponseId (int responseId)
specifier|public
name|void
name|setResponseId
parameter_list|(
name|int
name|responseId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setResponseId
argument_list|(
operator|(
name|responseId
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMasterKey ()
specifier|public
name|MasterKey
name|getMasterKey
parameter_list|()
block|{
name|HeartbeatResponseProtoOrBuilder
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
name|masterKey
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|masterKey
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasMasterKey
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|masterKey
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getMasterKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|masterKey
return|;
block|}
annotation|@
name|Override
DECL|method|setMasterKey (MasterKey masterKey)
specifier|public
name|void
name|setMasterKey
parameter_list|(
name|MasterKey
name|masterKey
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|masterKey
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearMasterKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|masterKey
operator|=
name|masterKey
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeAction ()
specifier|public
name|NodeAction
name|getNodeAction
parameter_list|()
block|{
name|HeartbeatResponseProtoOrBuilder
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
name|hasNodeAction
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getNodeAction
argument_list|()
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeAction (NodeAction nodeAction)
specifier|public
name|void
name|setNodeAction
parameter_list|(
name|NodeAction
name|nodeAction
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeAction
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeAction
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setNodeAction
argument_list|(
name|convertToProtoFormat
argument_list|(
name|nodeAction
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainersToCleanupList ()
specifier|public
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getContainersToCleanupList
parameter_list|()
block|{
name|initContainersToCleanup
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|containersToCleanup
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerToCleanup (int index)
specifier|public
name|ContainerId
name|getContainerToCleanup
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|initContainersToCleanup
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|containersToCleanup
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getContainersToCleanupCount ()
specifier|public
name|int
name|getContainersToCleanupCount
parameter_list|()
block|{
name|initContainersToCleanup
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|containersToCleanup
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|initContainersToCleanup ()
specifier|private
name|void
name|initContainersToCleanup
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|containersToCleanup
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|HeartbeatResponseProtoOrBuilder
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
name|getContainersToCleanupList
argument_list|()
decl_stmt|;
name|this
operator|.
name|containersToCleanup
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
name|containersToCleanup
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
annotation|@
name|Override
DECL|method|addAllContainersToCleanup (final List<ContainerId> containersToCleanup)
specifier|public
name|void
name|addAllContainersToCleanup
parameter_list|(
specifier|final
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containersToCleanup
parameter_list|)
block|{
if|if
condition|(
name|containersToCleanup
operator|==
literal|null
condition|)
return|return;
name|initContainersToCleanup
argument_list|()
expr_stmt|;
name|this
operator|.
name|containersToCleanup
operator|.
name|addAll
argument_list|(
name|containersToCleanup
argument_list|)
expr_stmt|;
block|}
DECL|method|addContainersToCleanupToProto ()
specifier|private
name|void
name|addContainersToCleanupToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearContainersToCleanup
argument_list|()
expr_stmt|;
if|if
condition|(
name|containersToCleanup
operator|==
literal|null
condition|)
return|return;
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
name|containersToCleanup
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
name|addAllContainersToCleanup
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addContainerToCleanup (ContainerId containersToCleanup)
specifier|public
name|void
name|addContainerToCleanup
parameter_list|(
name|ContainerId
name|containersToCleanup
parameter_list|)
block|{
name|initContainersToCleanup
argument_list|()
expr_stmt|;
name|this
operator|.
name|containersToCleanup
operator|.
name|add
argument_list|(
name|containersToCleanup
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeContainerToCleanup (int index)
specifier|public
name|void
name|removeContainerToCleanup
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|initContainersToCleanup
argument_list|()
expr_stmt|;
name|this
operator|.
name|containersToCleanup
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearContainersToCleanup ()
specifier|public
name|void
name|clearContainersToCleanup
parameter_list|()
block|{
name|initContainersToCleanup
argument_list|()
expr_stmt|;
name|this
operator|.
name|containersToCleanup
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationsToCleanupList ()
specifier|public
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getApplicationsToCleanupList
parameter_list|()
block|{
name|initApplicationsToCleanup
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|applicationsToCleanup
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationsToCleanup (int index)
specifier|public
name|ApplicationId
name|getApplicationsToCleanup
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|initApplicationsToCleanup
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|applicationsToCleanup
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationsToCleanupCount ()
specifier|public
name|int
name|getApplicationsToCleanupCount
parameter_list|()
block|{
name|initApplicationsToCleanup
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|applicationsToCleanup
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|initApplicationsToCleanup ()
specifier|private
name|void
name|initApplicationsToCleanup
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationsToCleanup
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|HeartbeatResponseProtoOrBuilder
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
name|ApplicationIdProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getApplicationsToCleanupList
argument_list|()
decl_stmt|;
name|this
operator|.
name|applicationsToCleanup
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ApplicationIdProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|applicationsToCleanup
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
annotation|@
name|Override
DECL|method|addAllApplicationsToCleanup (final List<ApplicationId> applicationsToCleanup)
specifier|public
name|void
name|addAllApplicationsToCleanup
parameter_list|(
specifier|final
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|applicationsToCleanup
parameter_list|)
block|{
if|if
condition|(
name|applicationsToCleanup
operator|==
literal|null
condition|)
return|return;
name|initApplicationsToCleanup
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationsToCleanup
operator|.
name|addAll
argument_list|(
name|applicationsToCleanup
argument_list|)
expr_stmt|;
block|}
DECL|method|addApplicationsToCleanupToProto ()
specifier|private
name|void
name|addApplicationsToCleanupToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearApplicationsToCleanup
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationsToCleanup
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|ApplicationIdProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|ApplicationIdProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ApplicationIdProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ApplicationIdProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ApplicationId
argument_list|>
name|iter
init|=
name|applicationsToCleanup
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
name|ApplicationIdProto
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
name|addAllApplicationsToCleanup
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addApplicationToCleanup (ApplicationId applicationsToCleanup)
specifier|public
name|void
name|addApplicationToCleanup
parameter_list|(
name|ApplicationId
name|applicationsToCleanup
parameter_list|)
block|{
name|initApplicationsToCleanup
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationsToCleanup
operator|.
name|add
argument_list|(
name|applicationsToCleanup
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeApplicationToCleanup (int index)
specifier|public
name|void
name|removeApplicationToCleanup
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|initApplicationsToCleanup
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationsToCleanup
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearApplicationsToCleanup ()
specifier|public
name|void
name|clearApplicationsToCleanup
parameter_list|()
block|{
name|initApplicationsToCleanup
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationsToCleanup
operator|.
name|clear
argument_list|()
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
DECL|method|convertFromProtoFormat (NodeActionProto p)
specifier|private
name|NodeAction
name|convertFromProtoFormat
parameter_list|(
name|NodeActionProto
name|p
parameter_list|)
block|{
return|return
name|NodeAction
operator|.
name|valueOf
argument_list|(
name|p
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (NodeAction t)
specifier|private
name|NodeActionProto
name|convertToProtoFormat
parameter_list|(
name|NodeAction
name|t
parameter_list|)
block|{
return|return
name|NodeActionProto
operator|.
name|valueOf
argument_list|(
name|t
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (MasterKeyProto p)
specifier|private
name|MasterKeyPBImpl
name|convertFromProtoFormat
parameter_list|(
name|MasterKeyProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|MasterKeyPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (MasterKey t)
specifier|private
name|MasterKeyProto
name|convertToProtoFormat
parameter_list|(
name|MasterKey
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|MasterKeyPBImpl
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

