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
name|YarnApplicationAttemptState
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
name|ApplicationAttemptFinishDataProto
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
name|ApplicationAttemptFinishDataProtoOrBuilder
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
name|ApplicationAttemptIdProto
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
name|YarnApplicationAttemptStateProto
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
name|ApplicationAttemptFinishData
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
DECL|class|ApplicationAttemptFinishDataPBImpl
specifier|public
class|class
name|ApplicationAttemptFinishDataPBImpl
extends|extends
name|ApplicationAttemptFinishData
block|{
DECL|field|proto
name|ApplicationAttemptFinishDataProto
name|proto
init|=
name|ApplicationAttemptFinishDataProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ApplicationAttemptFinishDataProto
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
DECL|method|ApplicationAttemptFinishDataPBImpl ()
specifier|public
name|ApplicationAttemptFinishDataPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ApplicationAttemptFinishDataProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ApplicationAttemptFinishDataPBImpl ( ApplicationAttemptFinishDataProto proto)
specifier|public
name|ApplicationAttemptFinishDataPBImpl
parameter_list|(
name|ApplicationAttemptFinishDataProto
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
DECL|field|applicationAttemptId
specifier|private
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
annotation|@
name|Override
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationAttemptId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|applicationAttemptId
return|;
block|}
name|ApplicationAttemptFinishDataProtoOrBuilder
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
name|hasApplicationAttemptId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|applicationAttemptId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|applicationAttemptId
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
DECL|method|setApplicationAttemptId (ApplicationAttemptId applicationAttemptId)
name|setApplicationAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationAttemptId
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearApplicationAttemptId
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|applicationAttemptId
operator|=
name|applicationAttemptId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTrackingURL ()
specifier|public
name|String
name|getTrackingURL
parameter_list|()
block|{
name|ApplicationAttemptFinishDataProtoOrBuilder
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
DECL|method|setTrackingURL (String trackingURL)
specifier|public
name|void
name|setTrackingURL
parameter_list|(
name|String
name|trackingURL
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|trackingURL
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
name|trackingURL
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
name|ApplicationAttemptFinishDataProtoOrBuilder
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
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
name|ApplicationAttemptFinishDataProtoOrBuilder
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
DECL|method|setFinalApplicationStatus ( FinalApplicationStatus finalApplicationStatus)
specifier|public
name|void
name|setFinalApplicationStatus
parameter_list|(
name|FinalApplicationStatus
name|finalApplicationStatus
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|finalApplicationStatus
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
name|finalApplicationStatus
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getYarnApplicationAttemptState ()
specifier|public
name|YarnApplicationAttemptState
name|getYarnApplicationAttemptState
parameter_list|()
block|{
name|ApplicationAttemptFinishDataProtoOrBuilder
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
name|hasYarnApplicationAttemptState
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
name|getYarnApplicationAttemptState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setYarnApplicationAttemptState (YarnApplicationAttemptState state)
specifier|public
name|void
name|setYarnApplicationAttemptState
parameter_list|(
name|YarnApplicationAttemptState
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
name|clearYarnApplicationAttemptState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setYarnApplicationAttemptState
argument_list|(
name|convertToProtoFormat
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|ApplicationAttemptFinishDataProto
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
name|applicationAttemptId
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ApplicationAttemptIdPBImpl
operator|)
name|this
operator|.
name|applicationAttemptId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setApplicationAttemptId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|applicationAttemptId
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
name|ApplicationAttemptFinishDataProto
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
DECL|method|convertFromProtoFormat ( ApplicationAttemptIdProto applicationAttemptId)
specifier|private
name|ApplicationAttemptIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ApplicationAttemptIdProto
name|applicationAttemptId
parameter_list|)
block|{
return|return
operator|new
name|ApplicationAttemptIdPBImpl
argument_list|(
name|applicationAttemptId
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat ( ApplicationAttemptId applicationAttemptId)
specifier|private
name|ApplicationAttemptIdProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationAttemptIdPBImpl
operator|)
name|applicationAttemptId
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( FinalApplicationStatusProto finalApplicationStatus)
specifier|private
name|FinalApplicationStatus
name|convertFromProtoFormat
parameter_list|(
name|FinalApplicationStatusProto
name|finalApplicationStatus
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|finalApplicationStatus
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat ( FinalApplicationStatus finalApplicationStatus)
specifier|private
name|FinalApplicationStatusProto
name|convertToProtoFormat
parameter_list|(
name|FinalApplicationStatus
name|finalApplicationStatus
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|finalApplicationStatus
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat ( YarnApplicationAttemptState state)
specifier|private
name|YarnApplicationAttemptStateProto
name|convertToProtoFormat
parameter_list|(
name|YarnApplicationAttemptState
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
DECL|method|convertFromProtoFormat ( YarnApplicationAttemptStateProto yarnApplicationAttemptState)
specifier|private
name|YarnApplicationAttemptState
name|convertFromProtoFormat
parameter_list|(
name|YarnApplicationAttemptStateProto
name|yarnApplicationAttemptState
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|yarnApplicationAttemptState
argument_list|)
return|;
block|}
block|}
end_class

end_unit

