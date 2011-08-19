begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|TaskId
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
name|TaskIdProto
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
name|TaskIdProtoOrBuilder
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
DECL|class|TaskIdPBImpl
specifier|public
class|class
name|TaskIdPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|TaskIdProto
argument_list|>
implements|implements
name|TaskId
block|{
DECL|field|proto
name|TaskIdProto
name|proto
init|=
name|TaskIdProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|TaskIdProto
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
DECL|field|idFormat
specifier|protected
specifier|static
specifier|final
name|NumberFormat
name|idFormat
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
static|static
block|{
name|idFormat
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|idFormat
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|6
argument_list|)
expr_stmt|;
block|}
DECL|field|jobidFormat
specifier|protected
specifier|static
specifier|final
name|NumberFormat
name|jobidFormat
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
static|static
block|{
name|jobidFormat
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|jobidFormat
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|field|jobId
specifier|private
name|JobId
name|jobId
init|=
literal|null
decl_stmt|;
DECL|method|TaskIdPBImpl ()
specifier|public
name|TaskIdPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|TaskIdProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskIdPBImpl (TaskIdProto proto)
specifier|public
name|TaskIdPBImpl
parameter_list|(
name|TaskIdProto
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
specifier|synchronized
name|TaskIdProto
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
specifier|synchronized
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
operator|&&
operator|!
operator|(
operator|(
name|JobIdPBImpl
operator|)
name|this
operator|.
name|jobId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getJobId
argument_list|()
argument_list|)
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
specifier|synchronized
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
specifier|synchronized
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
name|TaskIdProto
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
DECL|method|getId ()
specifier|public
specifier|synchronized
name|int
name|getId
parameter_list|()
block|{
name|TaskIdProtoOrBuilder
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
name|getId
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setId (int id)
specifier|public
specifier|synchronized
name|void
name|setId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setId
argument_list|(
operator|(
name|id
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getJobId ()
specifier|public
specifier|synchronized
name|JobId
name|getJobId
parameter_list|()
block|{
name|TaskIdProtoOrBuilder
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
name|jobId
return|;
block|}
annotation|@
name|Override
DECL|method|setJobId (JobId jobId)
specifier|public
specifier|synchronized
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
specifier|synchronized
name|TaskType
name|getTaskType
parameter_list|()
block|{
name|TaskIdProtoOrBuilder
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
specifier|synchronized
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
specifier|synchronized
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
specifier|synchronized
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
specifier|synchronized
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
specifier|synchronized
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
annotation|@
name|Override
DECL|method|toString ()
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
name|String
name|jobIdentifier
init|=
operator|(
name|jobId
operator|==
literal|null
operator|)
condition|?
literal|"none"
else|:
name|jobId
operator|.
name|getAppId
argument_list|()
operator|.
name|getClusterTimestamp
argument_list|()
operator|+
literal|"_"
operator|+
name|jobidFormat
operator|.
name|format
argument_list|(
name|jobId
operator|.
name|getAppId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|+
literal|"_"
operator|+
operator|(
operator|(
name|getTaskType
argument_list|()
operator|==
name|TaskType
operator|.
name|MAP
operator|)
condition|?
literal|"m"
else|:
literal|"r"
operator|)
operator|+
literal|"_"
operator|+
name|idFormat
operator|.
name|format
argument_list|(
name|getId
argument_list|()
argument_list|)
decl_stmt|;
return|return
literal|"task_"
operator|+
name|jobIdentifier
return|;
block|}
block|}
end_class

end_unit

