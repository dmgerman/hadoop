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
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|Gson
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
name|ContainerReport
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
name|NodeId
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
name|Priority
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
name|ContainerReportProto
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
name|ContainerReportProtoOrBuilder
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
name|NodeIdProto
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
name|PriorityProto
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
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
DECL|class|ContainerReportPBImpl
specifier|public
class|class
name|ContainerReportPBImpl
extends|extends
name|ContainerReport
block|{
DECL|field|proto
name|ContainerReportProto
name|proto
init|=
name|ContainerReportProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ContainerReportProto
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
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
init|=
literal|null
decl_stmt|;
DECL|field|priority
specifier|private
name|Priority
name|priority
init|=
literal|null
decl_stmt|;
DECL|method|ContainerReportPBImpl ()
specifier|public
name|ContainerReportPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ContainerReportProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ContainerReportPBImpl (ContainerReportProto proto)
specifier|public
name|ContainerReportPBImpl
parameter_list|(
name|ContainerReportProto
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
annotation|@
name|Override
DECL|method|getAllocatedResource ()
specifier|public
name|Resource
name|getAllocatedResource
parameter_list|()
block|{
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
name|ContainerReportProtoOrBuilder
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
DECL|method|getAssignedNode ()
specifier|public
name|NodeId
name|getAssignedNode
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|nodeId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|nodeId
return|;
block|}
name|ContainerReportProtoOrBuilder
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
name|hasNodeId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|nodeId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|nodeId
return|;
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
name|ContainerReportProtoOrBuilder
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
DECL|method|getDiagnosticsInfo ()
specifier|public
name|String
name|getDiagnosticsInfo
parameter_list|()
block|{
name|ContainerReportProtoOrBuilder
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
operator|(
name|p
operator|.
name|getDiagnosticsInfo
argument_list|()
operator|)
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
name|ContainerReportProtoOrBuilder
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
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
name|ContainerReportProtoOrBuilder
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
DECL|method|getLogUrl ()
specifier|public
name|String
name|getLogUrl
parameter_list|()
block|{
name|ContainerReportProtoOrBuilder
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
name|hasLogUrl
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
name|getLogUrl
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|priority
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|priority
return|;
block|}
name|ContainerReportProtoOrBuilder
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
name|hasPriority
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|priority
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|priority
return|;
block|}
annotation|@
name|Override
DECL|method|getCreationTime ()
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
name|ContainerReportProtoOrBuilder
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
name|getCreationTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAllocatedResource (Resource resource)
specifier|public
name|void
name|setAllocatedResource
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
DECL|method|setAssignedNode (NodeId nodeId)
specifier|public
name|void
name|setAssignedNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearNodeId
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
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
DECL|method|setContainerState (ContainerState containerState)
specifier|public
name|void
name|setContainerState
parameter_list|(
name|ContainerState
name|containerState
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|containerState
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
name|containerState
argument_list|)
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
name|ContainerReportProtoOrBuilder
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
annotation|@
name|Override
DECL|method|getExposedPorts ()
specifier|public
name|String
name|getExposedPorts
parameter_list|()
block|{
name|ContainerReportProtoOrBuilder
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
name|getExposedPorts
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setExposedPorts (Map<String, List<Map<String, String>>> ports)
specifier|public
name|void
name|setExposedPorts
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
name|ports
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|ports
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearExposedPorts
argument_list|()
expr_stmt|;
return|return;
block|}
name|Gson
name|gson
init|=
operator|new
name|Gson
argument_list|()
decl_stmt|;
name|String
name|strPorts
init|=
name|gson
operator|.
name|toJson
argument_list|(
name|ports
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setExposedPorts
argument_list|(
name|strPorts
argument_list|)
expr_stmt|;
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
DECL|method|setLogUrl (String logUrl)
specifier|public
name|void
name|setLogUrl
parameter_list|(
name|String
name|logUrl
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|logUrl
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearLogUrl
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setLogUrl
argument_list|(
name|logUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setPriority (Priority priority)
specifier|public
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|priority
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearPriority
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCreationTime (long creationTime)
specifier|public
name|void
name|setCreationTime
parameter_list|(
name|long
name|creationTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setCreationTime
argument_list|(
name|creationTime
argument_list|)
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|ContainerReportProto
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
name|this
operator|.
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
name|nodeId
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|NodeIdPBImpl
operator|)
name|nodeId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setNodeId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|nodeId
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
name|priority
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|PriorityPBImpl
operator|)
name|this
operator|.
name|priority
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getPriority
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setPriority
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|priority
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
name|ContainerReportProto
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
DECL|method|convertFromProtoFormat (NodeIdProto p)
specifier|private
name|NodeIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|NodeIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeIdPBImpl
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
DECL|method|convertToProtoFormat (NodeId t)
specifier|private
name|NodeIdProto
name|convertToProtoFormat
parameter_list|(
name|NodeId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeIdPBImpl
operator|)
name|t
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
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|t
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (PriorityProto p)
specifier|private
name|PriorityPBImpl
name|convertFromProtoFormat
parameter_list|(
name|PriorityProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|PriorityPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Priority p)
specifier|private
name|PriorityProto
name|convertToProtoFormat
parameter_list|(
name|Priority
name|p
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PriorityPBImpl
operator|)
name|p
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
specifier|private
name|ContainerStateProto
DECL|method|convertToProtoFormat (ContainerState containerState)
name|convertToProtoFormat
parameter_list|(
name|ContainerState
name|containerState
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|containerState
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
annotation|@
name|Override
DECL|method|getNodeHttpAddress ()
specifier|public
name|String
name|getNodeHttpAddress
parameter_list|()
block|{
name|ContainerReportProtoOrBuilder
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
name|hasNodeHttpAddress
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
name|getNodeHttpAddress
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeHttpAddress (String nodeHttpAddress)
specifier|public
name|void
name|setNodeHttpAddress
parameter_list|(
name|String
name|nodeHttpAddress
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeHttpAddress
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeHttpAddress
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setNodeHttpAddress
argument_list|(
name|nodeHttpAddress
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExecutionType ()
specifier|public
name|ExecutionType
name|getExecutionType
parameter_list|()
block|{
name|ContainerReportProtoOrBuilder
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
name|ExecutionType
operator|.
name|GUARANTEED
return|;
comment|// default value
block|}
return|return
name|ProtoUtils
operator|.
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
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|executionType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

