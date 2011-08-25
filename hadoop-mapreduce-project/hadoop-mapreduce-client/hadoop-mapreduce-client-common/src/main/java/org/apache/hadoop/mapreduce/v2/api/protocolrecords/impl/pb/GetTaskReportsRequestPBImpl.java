begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.protocolrecords.impl.pb
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetTaskReportsRequest
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
name|TaskType
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
name|impl
operator|.
name|pb
operator|.
name|JobIdPBImpl
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
name|TaskTypeProto
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
name|MRServiceProtos
operator|.
name|GetTaskReportsRequestProto
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
name|MRServiceProtos
operator|.
name|GetTaskReportsRequestProtoOrBuilder
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
DECL|class|GetTaskReportsRequestPBImpl
specifier|public
class|class
name|GetTaskReportsRequestPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|GetTaskReportsRequestProto
argument_list|>
implements|implements
name|GetTaskReportsRequest
block|{
DECL|field|proto
name|GetTaskReportsRequestProto
name|proto
init|=
name|GetTaskReportsRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetTaskReportsRequestProto
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
DECL|method|GetTaskReportsRequestPBImpl ()
specifier|public
name|GetTaskReportsRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetTaskReportsRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetTaskReportsRequestPBImpl (GetTaskReportsRequestProto proto)
specifier|public
name|GetTaskReportsRequestPBImpl
parameter_list|(
name|GetTaskReportsRequestProto
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
name|GetTaskReportsRequestProto
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
name|GetTaskReportsRequestProto
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
name|GetTaskReportsRequestProtoOrBuilder
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
DECL|method|getTaskType ()
specifier|public
name|TaskType
name|getTaskType
parameter_list|()
block|{
name|GetTaskReportsRequestProtoOrBuilder
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
name|hasTaskType
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
name|getTaskType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setTaskType (TaskType taskType)
specifier|public
name|void
name|setTaskType
parameter_list|(
name|TaskType
name|taskType
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|taskType
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearTaskType
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setTaskType
argument_list|(
name|convertToProtoFormat
argument_list|(
name|taskType
argument_list|)
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
DECL|method|convertToProtoFormat (TaskType e)
specifier|private
name|TaskTypeProto
name|convertToProtoFormat
parameter_list|(
name|TaskType
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
DECL|method|convertFromProtoFormat (TaskTypeProto e)
specifier|private
name|TaskType
name|convertFromProtoFormat
parameter_list|(
name|TaskTypeProto
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

