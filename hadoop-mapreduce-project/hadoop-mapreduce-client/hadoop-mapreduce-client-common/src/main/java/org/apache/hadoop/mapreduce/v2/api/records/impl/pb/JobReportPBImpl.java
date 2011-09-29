begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.records.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobReport
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobState
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
name|mapreduce
operator|.
name|v2
operator|.
name|proto
operator|.
name|MRProtos
operator|.
name|JobIdProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|proto
operator|.
name|MRProtos
operator|.
name|JobReportProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|proto
operator|.
name|MRProtos
operator|.
name|JobReportProtoOrBuilder
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
name|mapreduce
operator|.
name|v2
operator|.
name|proto
operator|.
name|MRProtos
operator|.
name|JobStateProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRProtoUtils
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

begin_class
DECL|class|JobReportPBImpl
specifier|public
class|class
name|JobReportPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|JobReportProto
argument_list|>
implements|implements
name|JobReport
block|{
DECL|field|proto
name|JobReportProto
name|proto
init|=
name|JobReportProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|JobReportProto
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
DECL|field|jobId
specifier|private
name|JobId
name|jobId
init|=
literal|null
decl_stmt|;
DECL|method|JobReportPBImpl ()
specifier|public
name|JobReportPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|JobReportProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|JobReportPBImpl (JobReportProto proto)
specifier|public
name|JobReportPBImpl
parameter_list|(
name|JobReportProto
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
name|JobReportProto
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
name|jobId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setJobId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|jobId
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
name|JobReportProto
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
DECL|method|getJobId ()
specifier|public
name|JobId
name|getJobId
parameter_list|()
block|{
name|JobReportProtoOrBuilder
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
name|jobId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|jobId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasJobId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|jobId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|jobId
return|;
block|}
annotation|@
name|Override
DECL|method|setJobId (JobId jobId)
specifier|public
name|void
name|setJobId
parameter_list|(
name|JobId
name|jobId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|jobId
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearJobId
argument_list|()
expr_stmt|;
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getJobState ()
specifier|public
name|JobState
name|getJobState
parameter_list|()
block|{
name|JobReportProtoOrBuilder
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
name|hasJobState
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
name|getJobState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setJobState (JobState jobState)
specifier|public
name|void
name|setJobState
parameter_list|(
name|JobState
name|jobState
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|jobState
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearJobState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setJobState
argument_list|(
name|convertToProtoFormat
argument_list|(
name|jobState
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMapProgress ()
specifier|public
name|float
name|getMapProgress
parameter_list|()
block|{
name|JobReportProtoOrBuilder
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
name|getMapProgress
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setMapProgress (float mapProgress)
specifier|public
name|void
name|setMapProgress
parameter_list|(
name|float
name|mapProgress
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setMapProgress
argument_list|(
operator|(
name|mapProgress
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReduceProgress ()
specifier|public
name|float
name|getReduceProgress
parameter_list|()
block|{
name|JobReportProtoOrBuilder
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
name|getReduceProgress
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setReduceProgress (float reduceProgress)
specifier|public
name|void
name|setReduceProgress
parameter_list|(
name|float
name|reduceProgress
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setReduceProgress
argument_list|(
operator|(
name|reduceProgress
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCleanupProgress ()
specifier|public
name|float
name|getCleanupProgress
parameter_list|()
block|{
name|JobReportProtoOrBuilder
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
name|getCleanupProgress
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setCleanupProgress (float cleanupProgress)
specifier|public
name|void
name|setCleanupProgress
parameter_list|(
name|float
name|cleanupProgress
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setCleanupProgress
argument_list|(
operator|(
name|cleanupProgress
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSetupProgress ()
specifier|public
name|float
name|getSetupProgress
parameter_list|()
block|{
name|JobReportProtoOrBuilder
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
name|getSetupProgress
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setSetupProgress (float setupProgress)
specifier|public
name|void
name|setSetupProgress
parameter_list|(
name|float
name|setupProgress
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setSetupProgress
argument_list|(
operator|(
name|setupProgress
operator|)
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
name|JobReportProtoOrBuilder
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
name|getStartTime
argument_list|()
operator|)
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
operator|(
name|startTime
operator|)
argument_list|)
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
name|JobReportProtoOrBuilder
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
name|getFinishTime
argument_list|()
operator|)
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
operator|(
name|finishTime
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
name|JobReportProtoOrBuilder
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
name|getUser
argument_list|()
operator|)
return|;
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
DECL|method|getJobName ()
specifier|public
name|String
name|getJobName
parameter_list|()
block|{
name|JobReportProtoOrBuilder
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
name|getJobName
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setJobName (String jobName)
specifier|public
name|void
name|setJobName
parameter_list|(
name|String
name|jobName
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setJobName
argument_list|(
operator|(
name|jobName
operator|)
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
name|JobReportProtoOrBuilder
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
name|getTrackingUrl
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setTrackingUrl (String trackingUrl)
specifier|public
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|trackingUrl
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setTrackingUrl
argument_list|(
name|trackingUrl
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
name|JobReportProtoOrBuilder
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
DECL|method|convertFromProtoFormat (JobIdProto p)
specifier|private
name|JobIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|JobIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|JobIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (JobId t)
specifier|private
name|JobIdProto
name|convertToProtoFormat
parameter_list|(
name|JobId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|JobIdPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertToProtoFormat (JobState e)
specifier|private
name|JobStateProto
name|convertToProtoFormat
parameter_list|(
name|JobState
name|e
parameter_list|)
block|{
return|return
name|MRProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|e
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (JobStateProto e)
specifier|private
name|JobState
name|convertFromProtoFormat
parameter_list|(
name|JobStateProto
name|e
parameter_list|)
block|{
return|return
name|MRProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
end_class

end_unit

