begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.util
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
name|util
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
name|api
operator|.
name|records
operator|.
name|Phase
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
name|TaskAttemptCompletionEventStatus
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
name|TaskAttemptState
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
name|proto
operator|.
name|MRProtos
operator|.
name|PhaseProto
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
name|TaskAttemptCompletionEventStatusProto
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
name|TaskAttemptStateProto
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
name|proto
operator|.
name|MRProtos
operator|.
name|TaskTypeProto
import|;
end_import

begin_class
DECL|class|MRProtoUtils
specifier|public
class|class
name|MRProtoUtils
block|{
comment|/*    * JobState    */
DECL|field|JOB_STATE_PREFIX
specifier|private
specifier|static
name|String
name|JOB_STATE_PREFIX
init|=
literal|"J_"
decl_stmt|;
DECL|method|convertToProtoFormat (JobState e)
specifier|public
specifier|static
name|JobStateProto
name|convertToProtoFormat
parameter_list|(
name|JobState
name|e
parameter_list|)
block|{
return|return
name|JobStateProto
operator|.
name|valueOf
argument_list|(
name|JOB_STATE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (JobStateProto e)
specifier|public
specifier|static
name|JobState
name|convertFromProtoFormat
parameter_list|(
name|JobStateProto
name|e
parameter_list|)
block|{
return|return
name|JobState
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
name|JOB_STATE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * Phase    */
DECL|field|PHASE_PREFIX
specifier|private
specifier|static
name|String
name|PHASE_PREFIX
init|=
literal|"P_"
decl_stmt|;
DECL|method|convertToProtoFormat (Phase e)
specifier|public
specifier|static
name|PhaseProto
name|convertToProtoFormat
parameter_list|(
name|Phase
name|e
parameter_list|)
block|{
return|return
name|PhaseProto
operator|.
name|valueOf
argument_list|(
name|PHASE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (PhaseProto e)
specifier|public
specifier|static
name|Phase
name|convertFromProtoFormat
parameter_list|(
name|PhaseProto
name|e
parameter_list|)
block|{
return|return
name|Phase
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
name|PHASE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * TaskAttemptCompletionEventStatus    */
DECL|field|TACE_PREFIX
specifier|private
specifier|static
name|String
name|TACE_PREFIX
init|=
literal|"TACE_"
decl_stmt|;
DECL|method|convertToProtoFormat (TaskAttemptCompletionEventStatus e)
specifier|public
specifier|static
name|TaskAttemptCompletionEventStatusProto
name|convertToProtoFormat
parameter_list|(
name|TaskAttemptCompletionEventStatus
name|e
parameter_list|)
block|{
return|return
name|TaskAttemptCompletionEventStatusProto
operator|.
name|valueOf
argument_list|(
name|TACE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (TaskAttemptCompletionEventStatusProto e)
specifier|public
specifier|static
name|TaskAttemptCompletionEventStatus
name|convertFromProtoFormat
parameter_list|(
name|TaskAttemptCompletionEventStatusProto
name|e
parameter_list|)
block|{
return|return
name|TaskAttemptCompletionEventStatus
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
name|TACE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * TaskAttemptState    */
DECL|field|TASK_ATTEMPT_STATE_PREFIX
specifier|private
specifier|static
name|String
name|TASK_ATTEMPT_STATE_PREFIX
init|=
literal|"TA_"
decl_stmt|;
DECL|method|convertToProtoFormat (TaskAttemptState e)
specifier|public
specifier|static
name|TaskAttemptStateProto
name|convertToProtoFormat
parameter_list|(
name|TaskAttemptState
name|e
parameter_list|)
block|{
return|return
name|TaskAttemptStateProto
operator|.
name|valueOf
argument_list|(
name|TASK_ATTEMPT_STATE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (TaskAttemptStateProto e)
specifier|public
specifier|static
name|TaskAttemptState
name|convertFromProtoFormat
parameter_list|(
name|TaskAttemptStateProto
name|e
parameter_list|)
block|{
return|return
name|TaskAttemptState
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
name|TASK_ATTEMPT_STATE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * TaskState    */
DECL|field|TASK_STATE_PREFIX
specifier|private
specifier|static
name|String
name|TASK_STATE_PREFIX
init|=
literal|"TS_"
decl_stmt|;
DECL|method|convertToProtoFormat (TaskState e)
specifier|public
specifier|static
name|TaskStateProto
name|convertToProtoFormat
parameter_list|(
name|TaskState
name|e
parameter_list|)
block|{
return|return
name|TaskStateProto
operator|.
name|valueOf
argument_list|(
name|TASK_STATE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (TaskStateProto e)
specifier|public
specifier|static
name|TaskState
name|convertFromProtoFormat
parameter_list|(
name|TaskStateProto
name|e
parameter_list|)
block|{
return|return
name|TaskState
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
name|TASK_STATE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * TaskType    */
DECL|method|convertToProtoFormat (TaskType e)
specifier|public
specifier|static
name|TaskTypeProto
name|convertToProtoFormat
parameter_list|(
name|TaskType
name|e
parameter_list|)
block|{
return|return
name|TaskTypeProto
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (TaskTypeProto e)
specifier|public
specifier|static
name|TaskType
name|convertFromProtoFormat
parameter_list|(
name|TaskTypeProto
name|e
parameter_list|)
block|{
return|return
name|TaskType
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

