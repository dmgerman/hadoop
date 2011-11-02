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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|FinalApplicationStatus
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
name|ApplicationReport
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
name|ApplicationResourceUsageReport
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
name|ApplicationReportProto
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
name|ApplicationReportProtoOrBuilder
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
name|FinalApplicationStatusProto
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
name|ApplicationResourceUsageReportProto
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
name|util
operator|.
name|ProtoUtils
import|;
end_import

begin_class
DECL|class|ApplicationReportPBImpl
specifier|public
class|class
name|ApplicationReportPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|ApplicationReportProto
argument_list|>
implements|implements
name|ApplicationReport
block|{
DECL|field|proto
name|ApplicationReportProto
name|proto
init|=
name|ApplicationReportProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ApplicationReportProto
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
DECL|field|applicationId
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|method|ApplicationReportPBImpl ()
specifier|public
name|ApplicationReportPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ApplicationReportProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ApplicationReportPBImpl (ApplicationReportProto proto)
specifier|public
name|ApplicationReportPBImpl
parameter_list|(
name|ApplicationReportProto
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
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|applicationId
return|;
block|}
name|ApplicationReportProtoOrBuilder
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
name|hasApplicationId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|applicationId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|applicationId
return|;
block|}
DECL|method|setApplicationResourceUsageReport (ApplicationResourceUsageReport appInfo)
specifier|public
name|void
name|setApplicationResourceUsageReport
parameter_list|(
name|ApplicationResourceUsageReport
name|appInfo
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|appInfo
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAppResourceUsage
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setAppResourceUsage
argument_list|(
name|convertToProtoFormat
argument_list|(
name|appInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationResourceUsageReport ()
specifier|public
name|ApplicationResourceUsageReport
name|getApplicationResourceUsageReport
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasAppResourceUsage
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
name|getAppResourceUsage
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasTrackingUrl
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
name|getTrackingUrl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOriginalTrackingUrl ()
specifier|public
name|String
name|getOriginalTrackingUrl
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasOriginalTrackingUrl
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
name|getOriginalTrackingUrl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasName
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
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
DECL|method|getYarnApplicationState ()
specifier|public
name|YarnApplicationState
name|getYarnApplicationState
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasYarnApplicationState
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
name|getYarnApplicationState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHost ()
specifier|public
name|String
name|getHost
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasHost
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
name|getHost
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRpcPort ()
specifier|public
name|int
name|getRpcPort
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|getRpcPort
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getClientToken ()
specifier|public
name|String
name|getClientToken
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasClientToken
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
name|getClientToken
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasUser
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
name|getUser
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasDiagnostics
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
name|getDiagnostics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|getStartTime
argument_list|()
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
name|ApplicationReportProtoOrBuilder
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
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
name|ApplicationReportProtoOrBuilder
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
name|hasFinalApplicationStatus
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
name|getFinalApplicationStatus
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationId (ApplicationId applicationId)
specifier|public
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationId
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearStatus
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|applicationId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTrackingUrl (String url)
specifier|public
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearTrackingUrl
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setTrackingUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setOriginalTrackingUrl (String url)
specifier|public
name|void
name|setOriginalTrackingUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearOriginalTrackingUrl
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setOriginalTrackingUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearName
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
return|return;
block|}
name|builder
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setYarnApplicationState (YarnApplicationState state)
specifier|public
name|void
name|setYarnApplicationState
parameter_list|(
name|YarnApplicationState
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
name|clearYarnApplicationState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setYarnApplicationState
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
DECL|method|setHost (String host)
specifier|public
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
name|builder
operator|.
name|clearHost
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setHost
argument_list|(
operator|(
name|host
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setRpcPort (int rpcPort)
specifier|public
name|void
name|setRpcPort
parameter_list|(
name|int
name|rpcPort
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setRpcPort
argument_list|(
operator|(
name|rpcPort
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setClientToken (String clientToken)
specifier|public
name|void
name|setClientToken
parameter_list|(
name|String
name|clientToken
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|clientToken
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearClientToken
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setClientToken
argument_list|(
operator|(
name|clientToken
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearUser
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setUser
argument_list|(
operator|(
name|user
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDiagnostics (String diagnostics)
specifier|public
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
if|if
condition|(
name|diagnostics
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearDiagnostics
argument_list|()
expr_stmt|;
return|return;
block|}
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
DECL|method|setStartTime (long startTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setStartTime
argument_list|(
name|startTime
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
DECL|method|setFinalApplicationStatus (FinalApplicationStatus finishState)
specifier|public
name|void
name|setFinalApplicationStatus
parameter_list|(
name|FinalApplicationStatus
name|finishState
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|finishState
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearFinalApplicationStatus
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setFinalApplicationStatus
argument_list|(
name|convertToProtoFormat
argument_list|(
name|finishState
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProto ()
specifier|public
name|ApplicationReportProto
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
name|applicationId
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ApplicationIdPBImpl
operator|)
name|this
operator|.
name|applicationId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setApplicationId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|applicationId
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
name|ApplicationReportProto
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
DECL|method|convertFromProtoFormat (ApplicationResourceUsageReportProto s)
specifier|private
name|ApplicationResourceUsageReport
name|convertFromProtoFormat
parameter_list|(
name|ApplicationResourceUsageReportProto
name|s
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ApplicationResourceUsageReport s)
specifier|private
name|ApplicationResourceUsageReportProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationResourceUsageReport
name|s
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat ( ApplicationIdProto applicationId)
specifier|private
name|ApplicationIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ApplicationIdProto
name|applicationId
parameter_list|)
block|{
return|return
operator|new
name|ApplicationIdPBImpl
argument_list|(
name|applicationId
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (YarnApplicationStateProto s)
specifier|private
name|YarnApplicationState
name|convertFromProtoFormat
parameter_list|(
name|YarnApplicationStateProto
name|s
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (YarnApplicationState s)
specifier|private
name|YarnApplicationStateProto
name|convertToProtoFormat
parameter_list|(
name|YarnApplicationState
name|s
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (FinalApplicationStatusProto s)
specifier|private
name|FinalApplicationStatus
name|convertFromProtoFormat
parameter_list|(
name|FinalApplicationStatusProto
name|s
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (FinalApplicationStatus s)
specifier|private
name|FinalApplicationStatusProto
name|convertToProtoFormat
parameter_list|(
name|FinalApplicationStatus
name|s
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
end_class

end_unit

