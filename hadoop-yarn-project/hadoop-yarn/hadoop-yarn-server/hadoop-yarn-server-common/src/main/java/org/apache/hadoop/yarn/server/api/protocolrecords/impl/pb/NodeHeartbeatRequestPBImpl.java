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
name|hadoop
operator|.
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|StringArrayProto
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
name|LogAggregationReportProto
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
name|LogAggregationReport
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
DECL|field|lastKnownContainerTokenMasterKey
specifier|private
name|MasterKey
name|lastKnownContainerTokenMasterKey
init|=
literal|null
decl_stmt|;
DECL|field|lastKnownNMTokenMasterKey
specifier|private
name|MasterKey
name|lastKnownNMTokenMasterKey
init|=
literal|null
decl_stmt|;
DECL|field|labels
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|labels
init|=
literal|null
decl_stmt|;
DECL|field|logAggregationReportsForApps
specifier|private
name|List
argument_list|<
name|LogAggregationReport
argument_list|>
name|logAggregationReportsForApps
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
name|lastKnownContainerTokenMasterKey
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setLastKnownContainerTokenMasterKey
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|lastKnownContainerTokenMasterKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|lastKnownNMTokenMasterKey
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setLastKnownNmTokenMasterKey
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|lastKnownNMTokenMasterKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|labels
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeLabels
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setNodeLabels
argument_list|(
name|StringArrayProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllElements
argument_list|(
name|this
operator|.
name|labels
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|logAggregationReportsForApps
operator|!=
literal|null
condition|)
block|{
name|addLogAggregationStatusForAppsToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addLogAggregationStatusForAppsToProto ()
specifier|private
name|void
name|addLogAggregationStatusForAppsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearLogAggregationReportsForApps
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|logAggregationReportsForApps
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|LogAggregationReportProto
argument_list|>
name|it
init|=
operator|new
name|Iterable
argument_list|<
name|LogAggregationReportProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|LogAggregationReportProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|LogAggregationReportProto
argument_list|>
argument_list|()
block|{
specifier|private
name|Iterator
argument_list|<
name|LogAggregationReport
argument_list|>
name|iter
init|=
name|logAggregationReportsForApps
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
name|LogAggregationReportProto
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
name|addAllLogAggregationReportsForApps
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
DECL|method|convertToProtoFormat ( LogAggregationReport value)
specifier|private
name|LogAggregationReportProto
name|convertToProtoFormat
parameter_list|(
name|LogAggregationReport
name|value
parameter_list|)
block|{
return|return
operator|(
operator|(
name|LogAggregationReportPBImpl
operator|)
name|value
operator|)
operator|.
name|getProto
argument_list|()
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
DECL|method|getLastKnownContainerTokenMasterKey ()
specifier|public
name|MasterKey
name|getLastKnownContainerTokenMasterKey
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
name|lastKnownContainerTokenMasterKey
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|lastKnownContainerTokenMasterKey
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasLastKnownContainerTokenMasterKey
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|lastKnownContainerTokenMasterKey
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getLastKnownContainerTokenMasterKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|lastKnownContainerTokenMasterKey
return|;
block|}
annotation|@
name|Override
DECL|method|setLastKnownContainerTokenMasterKey (MasterKey masterKey)
specifier|public
name|void
name|setLastKnownContainerTokenMasterKey
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
name|clearLastKnownContainerTokenMasterKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastKnownContainerTokenMasterKey
operator|=
name|masterKey
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLastKnownNMTokenMasterKey ()
specifier|public
name|MasterKey
name|getLastKnownNMTokenMasterKey
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
name|lastKnownNMTokenMasterKey
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|lastKnownNMTokenMasterKey
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasLastKnownNmTokenMasterKey
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|lastKnownNMTokenMasterKey
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getLastKnownNmTokenMasterKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|lastKnownNMTokenMasterKey
return|;
block|}
annotation|@
name|Override
DECL|method|setLastKnownNMTokenMasterKey (MasterKey masterKey)
specifier|public
name|void
name|setLastKnownNMTokenMasterKey
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
name|clearLastKnownNmTokenMasterKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastKnownNMTokenMasterKey
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
annotation|@
name|Override
DECL|method|getNodeLabels ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getNodeLabels
parameter_list|()
block|{
name|initNodeLabels
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|labels
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeLabels (Set<String> nodeLabels)
specifier|public
name|void
name|setNodeLabels
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabels
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearNodeLabels
argument_list|()
expr_stmt|;
name|this
operator|.
name|labels
operator|=
name|nodeLabels
expr_stmt|;
block|}
DECL|method|initNodeLabels ()
specifier|private
name|void
name|initNodeLabels
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|labels
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
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
operator|!
name|p
operator|.
name|hasNodeLabels
argument_list|()
condition|)
block|{
name|labels
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|StringArrayProto
name|nodeLabels
init|=
name|p
operator|.
name|getNodeLabels
argument_list|()
decl_stmt|;
name|labels
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|nodeLabels
operator|.
name|getElementsList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLogAggregationReportsForApps ()
specifier|public
name|List
argument_list|<
name|LogAggregationReport
argument_list|>
name|getLogAggregationReportsForApps
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|logAggregationReportsForApps
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|logAggregationReportsForApps
return|;
block|}
name|initLogAggregationReportsForApps
argument_list|()
expr_stmt|;
return|return
name|logAggregationReportsForApps
return|;
block|}
DECL|method|initLogAggregationReportsForApps ()
specifier|private
name|void
name|initLogAggregationReportsForApps
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
name|List
argument_list|<
name|LogAggregationReportProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getLogAggregationReportsForAppsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|logAggregationReportsForApps
operator|=
operator|new
name|ArrayList
argument_list|<
name|LogAggregationReport
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|LogAggregationReportProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|logAggregationReportsForApps
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
DECL|method|convertFromProtoFormat ( LogAggregationReportProto logAggregationReport)
specifier|private
name|LogAggregationReport
name|convertFromProtoFormat
parameter_list|(
name|LogAggregationReportProto
name|logAggregationReport
parameter_list|)
block|{
return|return
operator|new
name|LogAggregationReportPBImpl
argument_list|(
name|logAggregationReport
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setLogAggregationReportsForApps ( List<LogAggregationReport> logAggregationStatusForApps)
specifier|public
name|void
name|setLogAggregationReportsForApps
parameter_list|(
name|List
argument_list|<
name|LogAggregationReport
argument_list|>
name|logAggregationStatusForApps
parameter_list|)
block|{
if|if
condition|(
name|logAggregationStatusForApps
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearLogAggregationReportsForApps
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|logAggregationReportsForApps
operator|=
name|logAggregationStatusForApps
expr_stmt|;
block|}
block|}
end_class

end_unit

