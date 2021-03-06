begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb
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
name|applicationhistoryservice
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
name|ApplicationHistoryServerProtos
operator|.
name|ContainerFinishDataProto
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
name|ApplicationHistoryServerProtos
operator|.
name|ContainerFinishDataProtoOrBuilder
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
name|server
operator|.
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ContainerFinishData
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
DECL|class|ContainerFinishDataPBImpl
specifier|public
class|class
name|ContainerFinishDataPBImpl
extends|extends
name|ContainerFinishData
block|{
DECL|field|proto
name|ContainerFinishDataProto
name|proto
init|=
name|ContainerFinishDataProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ContainerFinishDataProto
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
decl_stmt|;
DECL|method|ContainerFinishDataPBImpl ()
specifier|public
name|ContainerFinishDataPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ContainerFinishDataProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ContainerFinishDataPBImpl (ContainerFinishDataProto proto)
specifier|public
name|ContainerFinishDataPBImpl
parameter_list|(
name|ContainerFinishDataProto
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
annotation|@
name|Override
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
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
name|ContainerFinishDataProtoOrBuilder
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
block|{
name|builder
operator|.
name|clearContainerId
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
name|ContainerFinishDataProtoOrBuilder
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
name|getFinishTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setFinishTime (long finishTime)
specifier|public
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setFinishTime
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDiagnosticsInfo ()
specifier|public
name|String
name|getDiagnosticsInfo
parameter_list|()
block|{
name|ContainerFinishDataProtoOrBuilder
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
name|hasDiagnosticsInfo
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
name|getDiagnosticsInfo
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setDiagnosticsInfo (String diagnosticsInfo)
specifier|public
name|void
name|setDiagnosticsInfo
parameter_list|(
name|String
name|diagnosticsInfo
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|diagnosticsInfo
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearDiagnosticsInfo
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setDiagnosticsInfo
argument_list|(
name|diagnosticsInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainerExitStatus ()
specifier|public
name|int
name|getContainerExitStatus
parameter_list|()
block|{
name|ContainerFinishDataProtoOrBuilder
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
name|getContainerExitStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerState ()
specifier|public
name|ContainerState
name|getContainerState
parameter_list|()
block|{
name|ContainerFinishDataProtoOrBuilder
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
name|hasContainerState
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
name|getContainerState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerState (ContainerState state)
specifier|public
name|void
name|setContainerState
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
name|clearContainerState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setContainerState
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
DECL|method|setContainerExitStatus (int containerExitStatus)
specifier|public
name|void
name|setContainerExitStatus
parameter_list|(
name|int
name|containerExitStatus
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setContainerExitStatus
argument_list|(
name|containerExitStatus
argument_list|)
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|ContainerFinishDataProto
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
name|this
operator|.
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
name|ContainerFinishDataProto
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
DECL|method|convertToProtoFormat (ContainerId containerId)
specifier|private
name|ContainerIdProto
name|convertToProtoFormat
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerIdPBImpl
operator|)
name|containerId
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
specifier|private
name|ContainerIdPBImpl
DECL|method|convertFromProtoFormat (ContainerIdProto containerId)
name|convertFromProtoFormat
parameter_list|(
name|ContainerIdProto
name|containerId
parameter_list|)
block|{
return|return
operator|new
name|ContainerIdPBImpl
argument_list|(
name|containerId
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ContainerState state)
specifier|private
name|ContainerStateProto
name|convertToProtoFormat
parameter_list|(
name|ContainerState
name|state
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat ( ContainerStateProto containerState)
specifier|private
name|ContainerState
name|convertFromProtoFormat
parameter_list|(
name|ContainerStateProto
name|containerState
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|containerState
argument_list|)
return|;
block|}
block|}
end_class

end_unit

