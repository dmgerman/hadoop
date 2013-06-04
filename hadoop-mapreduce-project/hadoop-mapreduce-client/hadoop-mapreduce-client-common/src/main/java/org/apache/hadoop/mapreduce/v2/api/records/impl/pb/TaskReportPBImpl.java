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
name|Counters
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
name|TaskAttemptId
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
name|TaskReport
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
name|TaskState
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
name|CountersProto
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
name|TaskAttemptIdProto
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
name|TaskReportProto
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
name|TaskReportProtoOrBuilder
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
name|TaskStateProto
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
name|impl
operator|.
name|pb
operator|.
name|ProtoBase
import|;
end_import

begin_class
DECL|class|TaskReportPBImpl
specifier|public
class|class
name|TaskReportPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|TaskReportProto
argument_list|>
implements|implements
name|TaskReport
block|{
DECL|field|proto
name|TaskReportProto
name|proto
init|=
name|TaskReportProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|TaskReportProto
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
DECL|field|taskId
specifier|private
name|TaskId
name|taskId
init|=
literal|null
decl_stmt|;
DECL|field|counters
specifier|private
name|Counters
name|counters
init|=
literal|null
decl_stmt|;
DECL|field|runningAttempts
specifier|private
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|runningAttempts
init|=
literal|null
decl_stmt|;
DECL|field|successfulAttemptId
specifier|private
name|TaskAttemptId
name|successfulAttemptId
init|=
literal|null
decl_stmt|;
DECL|field|diagnostics
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|diagnostics
init|=
literal|null
decl_stmt|;
DECL|method|TaskReportPBImpl ()
specifier|public
name|TaskReportPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|TaskReportProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|TaskReportPBImpl (TaskReportProto proto)
specifier|public
name|TaskReportPBImpl
parameter_list|(
name|TaskReportProto
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
name|TaskReportProto
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
name|taskId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setTaskId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|taskId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|counters
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setCounters
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|counters
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|runningAttempts
operator|!=
literal|null
condition|)
block|{
name|addRunningAttemptsToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|successfulAttemptId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setSuccessfulAttempt
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|successfulAttemptId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|diagnostics
operator|!=
literal|null
condition|)
block|{
name|addDiagnosticsToProto
argument_list|()
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
name|TaskReportProto
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
DECL|method|getCounters ()
specifier|public
name|Counters
name|getCounters
parameter_list|()
block|{
name|TaskReportProtoOrBuilder
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
name|counters
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|counters
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasCounters
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|counters
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getCounters
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|counters
return|;
block|}
annotation|@
name|Override
DECL|method|setCounters (Counters counters)
specifier|public
name|void
name|setCounters
parameter_list|(
name|Counters
name|counters
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|counters
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearCounters
argument_list|()
expr_stmt|;
name|this
operator|.
name|counters
operator|=
name|counters
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
name|TaskReportProtoOrBuilder
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
name|TaskReportProtoOrBuilder
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
DECL|method|getTaskId ()
specifier|public
name|TaskId
name|getTaskId
parameter_list|()
block|{
name|TaskReportProtoOrBuilder
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
name|taskId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|taskId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasTaskId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|taskId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getTaskId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|taskId
return|;
block|}
annotation|@
name|Override
DECL|method|setTaskId (TaskId taskId)
specifier|public
name|void
name|setTaskId
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|taskId
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearTaskId
argument_list|()
expr_stmt|;
name|this
operator|.
name|taskId
operator|=
name|taskId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
name|TaskReportProtoOrBuilder
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
name|getProgress
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setProgress (float progress)
specifier|public
name|void
name|setProgress
parameter_list|(
name|float
name|progress
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProgress
argument_list|(
operator|(
name|progress
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTaskState ()
specifier|public
name|TaskState
name|getTaskState
parameter_list|()
block|{
name|TaskReportProtoOrBuilder
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
name|hasTaskState
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
name|getTaskState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setTaskState (TaskState taskState)
specifier|public
name|void
name|setTaskState
parameter_list|(
name|TaskState
name|taskState
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|taskState
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearTaskState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setTaskState
argument_list|(
name|convertToProtoFormat
argument_list|(
name|taskState
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRunningAttemptsList ()
specifier|public
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|getRunningAttemptsList
parameter_list|()
block|{
name|initRunningAttempts
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|runningAttempts
return|;
block|}
annotation|@
name|Override
DECL|method|getRunningAttempt (int index)
specifier|public
name|TaskAttemptId
name|getRunningAttempt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|initRunningAttempts
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|runningAttempts
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRunningAttemptsCount ()
specifier|public
name|int
name|getRunningAttemptsCount
parameter_list|()
block|{
name|initRunningAttempts
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|runningAttempts
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|initRunningAttempts ()
specifier|private
name|void
name|initRunningAttempts
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|runningAttempts
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|TaskReportProtoOrBuilder
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
name|TaskAttemptIdProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getRunningAttemptsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|runningAttempts
operator|=
operator|new
name|ArrayList
argument_list|<
name|TaskAttemptId
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|TaskAttemptIdProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|runningAttempts
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
annotation|@
name|Override
DECL|method|addAllRunningAttempts (final List<TaskAttemptId> runningAttempts)
specifier|public
name|void
name|addAllRunningAttempts
parameter_list|(
specifier|final
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|runningAttempts
parameter_list|)
block|{
if|if
condition|(
name|runningAttempts
operator|==
literal|null
condition|)
return|return;
name|initRunningAttempts
argument_list|()
expr_stmt|;
name|this
operator|.
name|runningAttempts
operator|.
name|addAll
argument_list|(
name|runningAttempts
argument_list|)
expr_stmt|;
block|}
DECL|method|addRunningAttemptsToProto ()
specifier|private
name|void
name|addRunningAttemptsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearRunningAttempts
argument_list|()
expr_stmt|;
if|if
condition|(
name|runningAttempts
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|TaskAttemptIdProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|TaskAttemptIdProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|TaskAttemptIdProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|TaskAttemptIdProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|TaskAttemptId
argument_list|>
name|iter
init|=
name|runningAttempts
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
name|TaskAttemptIdProto
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
name|addAllRunningAttempts
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addRunningAttempt (TaskAttemptId runningAttempts)
specifier|public
name|void
name|addRunningAttempt
parameter_list|(
name|TaskAttemptId
name|runningAttempts
parameter_list|)
block|{
name|initRunningAttempts
argument_list|()
expr_stmt|;
name|this
operator|.
name|runningAttempts
operator|.
name|add
argument_list|(
name|runningAttempts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeRunningAttempt (int index)
specifier|public
name|void
name|removeRunningAttempt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|initRunningAttempts
argument_list|()
expr_stmt|;
name|this
operator|.
name|runningAttempts
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearRunningAttempts ()
specifier|public
name|void
name|clearRunningAttempts
parameter_list|()
block|{
name|initRunningAttempts
argument_list|()
expr_stmt|;
name|this
operator|.
name|runningAttempts
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSuccessfulAttempt ()
specifier|public
name|TaskAttemptId
name|getSuccessfulAttempt
parameter_list|()
block|{
name|TaskReportProtoOrBuilder
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
name|successfulAttemptId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|successfulAttemptId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasSuccessfulAttempt
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|successfulAttemptId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getSuccessfulAttempt
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|successfulAttemptId
return|;
block|}
annotation|@
name|Override
DECL|method|setSuccessfulAttempt (TaskAttemptId successfulAttempt)
specifier|public
name|void
name|setSuccessfulAttempt
parameter_list|(
name|TaskAttemptId
name|successfulAttempt
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|successfulAttempt
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearSuccessfulAttempt
argument_list|()
expr_stmt|;
name|this
operator|.
name|successfulAttemptId
operator|=
name|successfulAttempt
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDiagnosticsList ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDiagnosticsList
parameter_list|()
block|{
name|initDiagnostics
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|diagnostics
return|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics (int index)
specifier|public
name|String
name|getDiagnostics
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|initDiagnostics
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|diagnostics
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDiagnosticsCount ()
specifier|public
name|int
name|getDiagnosticsCount
parameter_list|()
block|{
name|initDiagnostics
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|diagnostics
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|initDiagnostics ()
specifier|private
name|void
name|initDiagnostics
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|diagnostics
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|TaskReportProtoOrBuilder
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
name|String
argument_list|>
name|list
init|=
name|p
operator|.
name|getDiagnosticsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|diagnostics
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|diagnostics
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addAllDiagnostics (final List<String> diagnostics)
specifier|public
name|void
name|addAllDiagnostics
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|diagnostics
parameter_list|)
block|{
if|if
condition|(
name|diagnostics
operator|==
literal|null
condition|)
return|return;
name|initDiagnostics
argument_list|()
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|.
name|addAll
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
block|}
DECL|method|addDiagnosticsToProto ()
specifier|private
name|void
name|addDiagnosticsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearDiagnostics
argument_list|()
expr_stmt|;
if|if
condition|(
name|diagnostics
operator|==
literal|null
condition|)
return|return;
name|builder
operator|.
name|addAllDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addDiagnostics (String diagnostics)
specifier|public
name|void
name|addDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
block|{
name|initDiagnostics
argument_list|()
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|.
name|add
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeDiagnostics (int index)
specifier|public
name|void
name|removeDiagnostics
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|initDiagnostics
argument_list|()
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearDiagnostics ()
specifier|public
name|void
name|clearDiagnostics
parameter_list|()
block|{
name|initDiagnostics
argument_list|()
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (CountersProto p)
specifier|private
name|CountersPBImpl
name|convertFromProtoFormat
parameter_list|(
name|CountersProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|CountersPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Counters t)
specifier|private
name|CountersProto
name|convertToProtoFormat
parameter_list|(
name|Counters
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|CountersPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (TaskIdProto p)
specifier|private
name|TaskIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|TaskIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|TaskIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (TaskId t)
specifier|private
name|TaskIdProto
name|convertToProtoFormat
parameter_list|(
name|TaskId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TaskIdPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertToProtoFormat (TaskState e)
specifier|private
name|TaskStateProto
name|convertToProtoFormat
parameter_list|(
name|TaskState
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
DECL|method|convertFromProtoFormat (TaskStateProto e)
specifier|private
name|TaskState
name|convertFromProtoFormat
parameter_list|(
name|TaskStateProto
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
DECL|method|convertFromProtoFormat (TaskAttemptIdProto p)
specifier|private
name|TaskAttemptIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|TaskAttemptIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|TaskAttemptIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (TaskAttemptId t)
specifier|private
name|TaskAttemptIdProto
name|convertToProtoFormat
parameter_list|(
name|TaskAttemptId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TaskAttemptIdPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

