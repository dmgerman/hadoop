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
name|FinishApplicationMasterRequest
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
name|YarnServiceProtos
operator|.
name|FinishApplicationMasterRequestProto
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
name|FinishApplicationMasterRequestProtoOrBuilder
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FinishApplicationMasterRequestPBImpl
specifier|public
class|class
name|FinishApplicationMasterRequestPBImpl
extends|extends
name|FinishApplicationMasterRequest
block|{
DECL|field|proto
name|FinishApplicationMasterRequestProto
name|proto
init|=
name|FinishApplicationMasterRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|FinishApplicationMasterRequestProto
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
DECL|field|appAttemptId
specifier|private
name|ApplicationAttemptId
name|appAttemptId
init|=
literal|null
decl_stmt|;
DECL|method|FinishApplicationMasterRequestPBImpl ()
specifier|public
name|FinishApplicationMasterRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|FinishApplicationMasterRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|FinishApplicationMasterRequestPBImpl (FinishApplicationMasterRequestProto proto)
specifier|public
name|FinishApplicationMasterRequestPBImpl
parameter_list|(
name|FinishApplicationMasterRequestProto
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
name|FinishApplicationMasterRequestProto
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
name|getProto
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|", "
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
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
name|appAttemptId
operator|!=
literal|null
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
name|appAttemptId
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
name|FinishApplicationMasterRequestProto
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
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
name|FinishApplicationMasterRequestProtoOrBuilder
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
name|appAttemptId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|appAttemptId
return|;
block|}
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
name|appAttemptId
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
name|appAttemptId
return|;
block|}
annotation|@
name|Override
DECL|method|setAppAttemptId (ApplicationAttemptId applicationAttemptId)
specifier|public
name|void
name|setAppAttemptId
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
name|builder
operator|.
name|clearApplicationAttemptId
argument_list|()
expr_stmt|;
name|this
operator|.
name|appAttemptId
operator|=
name|applicationAttemptId
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
name|FinishApplicationMasterRequestProtoOrBuilder
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
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
name|FinishApplicationMasterRequestProtoOrBuilder
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
name|getTrackingUrl
argument_list|()
return|;
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
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
name|FinishApplicationMasterRequestProtoOrBuilder
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
DECL|method|setFinalApplicationStatus (FinalApplicationStatus finalState)
specifier|public
name|void
name|setFinalApplicationStatus
parameter_list|(
name|FinalApplicationStatus
name|finalState
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|finalState
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
name|finalState
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ApplicationAttemptIdProto p)
specifier|private
name|ApplicationAttemptIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ApplicationAttemptIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ApplicationAttemptIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ApplicationAttemptId t)
specifier|private
name|ApplicationAttemptIdProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationAttemptId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationAttemptIdPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
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

