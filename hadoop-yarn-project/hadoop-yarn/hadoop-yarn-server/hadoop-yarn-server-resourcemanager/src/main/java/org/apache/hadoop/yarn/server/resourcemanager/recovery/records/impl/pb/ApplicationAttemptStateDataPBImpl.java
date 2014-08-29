begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb
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
name|resourcemanager
operator|.
name|recovery
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
name|nio
operator|.
name|ByteBuffer
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
name|ApplicationAttemptId
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
name|impl
operator|.
name|pb
operator|.
name|ApplicationAttemptIdPBImpl
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
name|YarnServerResourceManagerRecoveryProtos
operator|.
name|ApplicationAttemptStateDataProto
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
name|YarnServerResourceManagerRecoveryProtos
operator|.
name|ApplicationAttemptStateDataProtoOrBuilder
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
name|YarnServerResourceManagerRecoveryProtos
operator|.
name|RMAppAttemptStateProto
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|ApplicationAttemptStateData
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptState
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
DECL|class|ApplicationAttemptStateDataPBImpl
specifier|public
class|class
name|ApplicationAttemptStateDataPBImpl
extends|extends
name|ApplicationAttemptStateData
block|{
DECL|field|proto
name|ApplicationAttemptStateDataProto
name|proto
init|=
name|ApplicationAttemptStateDataProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ApplicationAttemptStateDataProto
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
DECL|field|attemptId
specifier|private
name|ApplicationAttemptId
name|attemptId
init|=
literal|null
decl_stmt|;
DECL|field|masterContainer
specifier|private
name|Container
name|masterContainer
init|=
literal|null
decl_stmt|;
DECL|field|appAttemptTokens
specifier|private
name|ByteBuffer
name|appAttemptTokens
init|=
literal|null
decl_stmt|;
DECL|method|ApplicationAttemptStateDataPBImpl ()
specifier|public
name|ApplicationAttemptStateDataPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ApplicationAttemptStateDataProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ApplicationAttemptStateDataPBImpl ( ApplicationAttemptStateDataProto proto)
specifier|public
name|ApplicationAttemptStateDataPBImpl
parameter_list|(
name|ApplicationAttemptStateDataProto
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
DECL|method|getProto ()
specifier|public
name|ApplicationAttemptStateDataProto
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
name|attemptId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setAttemptId
argument_list|(
operator|(
operator|(
name|ApplicationAttemptIdPBImpl
operator|)
name|attemptId
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|masterContainer
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setMasterContainer
argument_list|(
operator|(
operator|(
name|ContainerPBImpl
operator|)
name|masterContainer
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|appAttemptTokens
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setAppAttemptTokens
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|appAttemptTokens
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
name|ApplicationAttemptStateDataProto
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
DECL|method|getAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getAttemptId
parameter_list|()
block|{
name|ApplicationAttemptStateDataProtoOrBuilder
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
name|attemptId
operator|!=
literal|null
condition|)
block|{
return|return
name|attemptId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasAttemptId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|attemptId
operator|=
operator|new
name|ApplicationAttemptIdPBImpl
argument_list|(
name|p
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|attemptId
return|;
block|}
annotation|@
name|Override
DECL|method|setAttemptId (ApplicationAttemptId attemptId)
specifier|public
name|void
name|setAttemptId
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|attemptId
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAttemptId
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|attemptId
operator|=
name|attemptId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMasterContainer ()
specifier|public
name|Container
name|getMasterContainer
parameter_list|()
block|{
name|ApplicationAttemptStateDataProtoOrBuilder
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
name|masterContainer
operator|!=
literal|null
condition|)
block|{
return|return
name|masterContainer
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasMasterContainer
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|masterContainer
operator|=
operator|new
name|ContainerPBImpl
argument_list|(
name|p
operator|.
name|getMasterContainer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|masterContainer
return|;
block|}
annotation|@
name|Override
DECL|method|setMasterContainer (Container container)
specifier|public
name|void
name|setMasterContainer
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|container
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearMasterContainer
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|masterContainer
operator|=
name|container
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAppAttemptTokens ()
specifier|public
name|ByteBuffer
name|getAppAttemptTokens
parameter_list|()
block|{
name|ApplicationAttemptStateDataProtoOrBuilder
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
name|appAttemptTokens
operator|!=
literal|null
condition|)
block|{
return|return
name|appAttemptTokens
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasAppAttemptTokens
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|appAttemptTokens
operator|=
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getAppAttemptTokens
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|appAttemptTokens
return|;
block|}
annotation|@
name|Override
DECL|method|setAppAttemptTokens (ByteBuffer attemptTokens)
specifier|public
name|void
name|setAppAttemptTokens
parameter_list|(
name|ByteBuffer
name|attemptTokens
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|attemptTokens
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAppAttemptTokens
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|appAttemptTokens
operator|=
name|attemptTokens
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|RMAppAttemptState
name|getState
parameter_list|()
block|{
name|ApplicationAttemptStateDataProtoOrBuilder
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
name|hasAppAttemptState
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
name|getAppAttemptState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setState (RMAppAttemptState state)
specifier|public
name|void
name|setState
parameter_list|(
name|RMAppAttemptState
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
name|clearAppAttemptState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setAppAttemptState
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
DECL|method|getFinalTrackingUrl ()
specifier|public
name|String
name|getFinalTrackingUrl
parameter_list|()
block|{
name|ApplicationAttemptStateDataProtoOrBuilder
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
name|hasFinalTrackingUrl
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
name|getFinalTrackingUrl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setFinalTrackingUrl (String url)
specifier|public
name|void
name|setFinalTrackingUrl
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
name|clearFinalTrackingUrl
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setFinalTrackingUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
block|{
name|ApplicationAttemptStateDataProtoOrBuilder
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
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
name|ApplicationAttemptStateDataProtoOrBuilder
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
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
name|ApplicationAttemptStateDataProtoOrBuilder
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
DECL|method|getAMContainerExitStatus ()
specifier|public
name|int
name|getAMContainerExitStatus
parameter_list|()
block|{
name|ApplicationAttemptStateDataProtoOrBuilder
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
name|getAmContainerExitStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAMContainerExitStatus (int exitStatus)
specifier|public
name|void
name|setAMContainerExitStatus
parameter_list|(
name|int
name|exitStatus
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setAmContainerExitStatus
argument_list|(
name|exitStatus
argument_list|)
expr_stmt|;
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
DECL|field|RM_APP_ATTEMPT_PREFIX
specifier|private
specifier|static
name|String
name|RM_APP_ATTEMPT_PREFIX
init|=
literal|"RMATTEMPT_"
decl_stmt|;
DECL|method|convertToProtoFormat (RMAppAttemptState e)
specifier|public
specifier|static
name|RMAppAttemptStateProto
name|convertToProtoFormat
parameter_list|(
name|RMAppAttemptState
name|e
parameter_list|)
block|{
return|return
name|RMAppAttemptStateProto
operator|.
name|valueOf
argument_list|(
name|RM_APP_ATTEMPT_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (RMAppAttemptStateProto e)
specifier|public
specifier|static
name|RMAppAttemptState
name|convertFromProtoFormat
parameter_list|(
name|RMAppAttemptStateProto
name|e
parameter_list|)
block|{
return|return
name|RMAppAttemptState
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|RM_APP_ATTEMPT_PREFIX
argument_list|,
literal|""
argument_list|)
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
block|}
end_class

end_unit

