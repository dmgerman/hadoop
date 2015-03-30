begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb
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
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
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
name|proto
operator|.
name|YarnServerCommonServiceProtos
operator|.
name|NodeHeartbeatResponseProto
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
name|NodeHeartbeatResponseProtoOrBuilder
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
name|SystemCredentialsForAppsProto
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
name|protocolrecords
operator|.
name|NodeHeartbeatResponse
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
name|impl
operator|.
name|pb
operator|.
name|MasterKeyPBImpl
import|;
end_import

begin_class
DECL|class|NodeHeartbeatResponsePBImpl
specifier|public
class|class
name|NodeHeartbeatResponsePBImpl
extends|extends
name|ProtoBase
argument_list|<
name|NodeHeartbeatResponseProto
argument_list|>
implements|implements
name|NodeHeartbeatResponse
block|{
DECL|field|proto
name|NodeHeartbeatResponseProto
name|proto
init|=
name|NodeHeartbeatResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|NodeHeartbeatResponseProto
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
DECL|field|containersToBeRemovedFromNM
specifier|private
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containersToBeRemovedFromNM
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
DECL|field|systemCredentials
specifier|private
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|systemCredentials
init|=
literal|null
decl_stmt|;
DECL|field|containerTokenMasterKey
specifier|private
name|MasterKey
name|containerTokenMasterKey
init|=
literal|null
decl_stmt|;
DECL|field|nmTokenMasterKey
specifier|private
name|MasterKey
name|nmTokenMasterKey
init|=
literal|null
decl_stmt|;
DECL|method|NodeHeartbeatResponsePBImpl ()
specifier|public
name|NodeHeartbeatResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|NodeHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|NodeHeartbeatResponsePBImpl (NodeHeartbeatResponseProto proto)
specifier|public
name|NodeHeartbeatResponsePBImpl
parameter_list|(
name|NodeHeartbeatResponseProto
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
name|NodeHeartbeatResponseProto
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
name|containersToBeRemovedFromNM
operator|!=
literal|null
condition|)
block|{
name|addContainersToBeRemovedFromNMToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|containerTokenMasterKey
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setContainerTokenMasterKey
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|containerTokenMasterKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|nmTokenMasterKey
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setNmTokenMasterKey
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|nmTokenMasterKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|systemCredentials
operator|!=
literal|null
condition|)
block|{
name|addSystemCredentialsToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addSystemCredentialsToProto ()
specifier|private
name|void
name|addSystemCredentialsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearSystemCredentialsForApps
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|entry
range|:
name|systemCredentials
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|addSystemCredentialsForApps
argument_list|(
name|SystemCredentialsForAppsProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAppId
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
name|setCredentialsForApp
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|duplicate
argument_list|()
argument_list|)
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
name|NodeHeartbeatResponseProto
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
name|NodeHeartbeatResponseProtoOrBuilder
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
DECL|method|getContainerTokenMasterKey ()
specifier|public
name|MasterKey
name|getContainerTokenMasterKey
parameter_list|()
block|{
name|NodeHeartbeatResponseProtoOrBuilder
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
name|containerTokenMasterKey
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|containerTokenMasterKey
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasContainerTokenMasterKey
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|containerTokenMasterKey
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getContainerTokenMasterKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|containerTokenMasterKey
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerTokenMasterKey (MasterKey masterKey)
specifier|public
name|void
name|setContainerTokenMasterKey
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
name|clearContainerTokenMasterKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerTokenMasterKey
operator|=
name|masterKey
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNMTokenMasterKey ()
specifier|public
name|MasterKey
name|getNMTokenMasterKey
parameter_list|()
block|{
name|NodeHeartbeatResponseProtoOrBuilder
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
name|nmTokenMasterKey
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|nmTokenMasterKey
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasNmTokenMasterKey
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|nmTokenMasterKey
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getNmTokenMasterKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|nmTokenMasterKey
return|;
block|}
annotation|@
name|Override
DECL|method|setNMTokenMasterKey (MasterKey masterKey)
specifier|public
name|void
name|setNMTokenMasterKey
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
name|clearNmTokenMasterKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|nmTokenMasterKey
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
name|NodeHeartbeatResponseProtoOrBuilder
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
DECL|method|getDiagnosticsMessage ()
specifier|public
name|String
name|getDiagnosticsMessage
parameter_list|()
block|{
name|NodeHeartbeatResponseProtoOrBuilder
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
name|hasDiagnosticsMessage
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
name|getDiagnosticsMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setDiagnosticsMessage (String diagnosticsMessage)
specifier|public
name|void
name|setDiagnosticsMessage
parameter_list|(
name|String
name|diagnosticsMessage
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|diagnosticsMessage
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearDiagnosticsMessage
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setDiagnosticsMessage
argument_list|(
operator|(
name|diagnosticsMessage
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainersToCleanup ()
specifier|public
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getContainersToCleanup
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
DECL|method|getContainersToBeRemovedFromNM ()
specifier|public
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getContainersToBeRemovedFromNM
parameter_list|()
block|{
name|initContainersToBeRemovedFromNM
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|containersToBeRemovedFromNM
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
name|NodeHeartbeatResponseProtoOrBuilder
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
DECL|method|initContainersToBeRemovedFromNM ()
specifier|private
name|void
name|initContainersToBeRemovedFromNM
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|containersToBeRemovedFromNM
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|NodeHeartbeatResponseProtoOrBuilder
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
name|getContainersToBeRemovedFromNmList
argument_list|()
decl_stmt|;
name|this
operator|.
name|containersToBeRemovedFromNM
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
name|containersToBeRemovedFromNM
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
DECL|method|addAllContainersToCleanup ( final List<ContainerId> containersToCleanup)
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
annotation|@
name|Override
specifier|public
name|void
DECL|method|addContainersToBeRemovedFromNM (final List<ContainerId> containers)
name|addContainersToBeRemovedFromNM
parameter_list|(
specifier|final
name|List
argument_list|<
name|ContainerId
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
return|return;
name|initContainersToBeRemovedFromNM
argument_list|()
expr_stmt|;
name|this
operator|.
name|containersToBeRemovedFromNM
operator|.
name|addAll
argument_list|(
name|containers
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
DECL|method|addContainersToBeRemovedFromNMToProto ()
specifier|private
name|void
name|addContainersToBeRemovedFromNMToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearContainersToBeRemovedFromNm
argument_list|()
expr_stmt|;
if|if
condition|(
name|containersToBeRemovedFromNM
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
name|containersToBeRemovedFromNM
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
name|addAllContainersToBeRemovedFromNm
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationsToCleanup ()
specifier|public
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getApplicationsToCleanup
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
name|NodeHeartbeatResponseProtoOrBuilder
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
DECL|method|addAllApplicationsToCleanup ( final List<ApplicationId> applicationsToCleanup)
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
DECL|method|getSystemCredentialsForApps ()
specifier|public
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|getSystemCredentialsForApps
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|systemCredentials
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|systemCredentials
return|;
block|}
name|initSystemCredentials
argument_list|()
expr_stmt|;
return|return
name|systemCredentials
return|;
block|}
DECL|method|initSystemCredentials ()
specifier|private
name|void
name|initSystemCredentials
parameter_list|()
block|{
name|NodeHeartbeatResponseProtoOrBuilder
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
name|SystemCredentialsForAppsProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getSystemCredentialsForAppsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|systemCredentials
operator|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|SystemCredentialsForAppsProto
name|c
range|:
name|list
control|)
block|{
name|ApplicationId
name|appId
init|=
name|convertFromProtoFormat
argument_list|(
name|c
operator|.
name|getAppId
argument_list|()
argument_list|)
decl_stmt|;
name|ByteBuffer
name|byteBuffer
init|=
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|c
operator|.
name|getCredentialsForApp
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|systemCredentials
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|byteBuffer
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setSystemCredentialsForApps ( Map<ApplicationId, ByteBuffer> systemCredentials)
specifier|public
name|void
name|setSystemCredentialsForApps
parameter_list|(
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|systemCredentials
parameter_list|)
block|{
if|if
condition|(
name|systemCredentials
operator|==
literal|null
operator|||
name|systemCredentials
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|systemCredentials
operator|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|systemCredentials
operator|.
name|putAll
argument_list|(
name|systemCredentials
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNextHeartBeatInterval ()
specifier|public
name|long
name|getNextHeartBeatInterval
parameter_list|()
block|{
name|NodeHeartbeatResponseProtoOrBuilder
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
name|getNextHeartBeatInterval
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNextHeartBeatInterval (long nextHeartBeatInterval)
specifier|public
name|void
name|setNextHeartBeatInterval
parameter_list|(
name|long
name|nextHeartBeatInterval
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setNextHeartBeatInterval
argument_list|(
name|nextHeartBeatInterval
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
annotation|@
name|Override
DECL|method|getAreNodeLabelsAcceptedByRM ()
specifier|public
name|boolean
name|getAreNodeLabelsAcceptedByRM
parameter_list|()
block|{
name|NodeHeartbeatResponseProtoOrBuilder
name|p
init|=
name|this
operator|.
name|viaProto
condition|?
name|this
operator|.
name|proto
else|:
name|this
operator|.
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getAreNodeLabelsAcceptedByRM
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAreNodeLabelsAcceptedByRM (boolean areNodeLabelsAcceptedByRM)
specifier|public
name|void
name|setAreNodeLabelsAcceptedByRM
parameter_list|(
name|boolean
name|areNodeLabelsAcceptedByRM
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|builder
operator|.
name|setAreNodeLabelsAcceptedByRM
argument_list|(
name|areNodeLabelsAcceptedByRM
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

