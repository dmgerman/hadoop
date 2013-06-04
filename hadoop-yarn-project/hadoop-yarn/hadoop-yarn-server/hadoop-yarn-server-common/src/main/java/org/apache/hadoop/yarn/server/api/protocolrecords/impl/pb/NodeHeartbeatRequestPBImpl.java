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
name|NodeStatusProto
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
name|NodeHeartbeatRequestProto
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
name|NodeHeartbeatRequestProtoOrBuilder
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
name|NodeHeartbeatRequest
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
name|NodeStatus
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
name|NodeStatusPBImpl
import|;
end_import

begin_class
DECL|class|NodeHeartbeatRequestPBImpl
specifier|public
class|class
name|NodeHeartbeatRequestPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|NodeHeartbeatRequestProto
argument_list|>
implements|implements
name|NodeHeartbeatRequest
block|{
DECL|field|proto
name|NodeHeartbeatRequestProto
name|proto
init|=
name|NodeHeartbeatRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|NodeHeartbeatRequestProto
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
DECL|field|nodeStatus
specifier|private
name|NodeStatus
name|nodeStatus
init|=
literal|null
decl_stmt|;
DECL|field|lastKnownMasterKey
specifier|private
name|MasterKey
name|lastKnownMasterKey
init|=
literal|null
decl_stmt|;
DECL|method|NodeHeartbeatRequestPBImpl ()
specifier|public
name|NodeHeartbeatRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|NodeHeartbeatRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|NodeHeartbeatRequestPBImpl (NodeHeartbeatRequestProto proto)
specifier|public
name|NodeHeartbeatRequestPBImpl
parameter_list|(
name|NodeHeartbeatRequestProto
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
name|NodeHeartbeatRequestProto
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
name|nodeStatus
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setNodeStatus
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|nodeStatus
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|lastKnownMasterKey
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setLastKnownMasterKey
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|lastKnownMasterKey
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
name|NodeHeartbeatRequestProto
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
DECL|method|getNodeStatus ()
specifier|public
name|NodeStatus
name|getNodeStatus
parameter_list|()
block|{
name|NodeHeartbeatRequestProtoOrBuilder
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
name|nodeStatus
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|nodeStatus
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasNodeStatus
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|nodeStatus
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getNodeStatus
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|nodeStatus
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeStatus (NodeStatus nodeStatus)
specifier|public
name|void
name|setNodeStatus
parameter_list|(
name|NodeStatus
name|nodeStatus
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeStatus
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearNodeStatus
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeStatus
operator|=
name|nodeStatus
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLastKnownMasterKey ()
specifier|public
name|MasterKey
name|getLastKnownMasterKey
parameter_list|()
block|{
name|NodeHeartbeatRequestProtoOrBuilder
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
name|lastKnownMasterKey
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|lastKnownMasterKey
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasLastKnownMasterKey
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|lastKnownMasterKey
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getLastKnownMasterKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|lastKnownMasterKey
return|;
block|}
annotation|@
name|Override
DECL|method|setLastKnownMasterKey (MasterKey masterKey)
specifier|public
name|void
name|setLastKnownMasterKey
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
name|clearLastKnownMasterKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastKnownMasterKey
operator|=
name|masterKey
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (NodeStatusProto p)
specifier|private
name|NodeStatusPBImpl
name|convertFromProtoFormat
parameter_list|(
name|NodeStatusProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeStatusPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (NodeStatus t)
specifier|private
name|NodeStatusProto
name|convertToProtoFormat
parameter_list|(
name|NodeStatus
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeStatusPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
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

