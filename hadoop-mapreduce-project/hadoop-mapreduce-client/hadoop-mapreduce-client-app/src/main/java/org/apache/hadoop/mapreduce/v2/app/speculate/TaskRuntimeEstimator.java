begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.speculate
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
name|app
operator|.
name|speculate
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
name|conf
operator|.
name|Configuration
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
name|app
operator|.
name|AppContext
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
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptStatusUpdateEvent
operator|.
name|TaskAttemptStatus
import|;
end_import

begin_interface
DECL|interface|TaskRuntimeEstimator
specifier|public
interface|interface
name|TaskRuntimeEstimator
block|{
DECL|method|enrollAttempt (TaskAttemptStatus reportedStatus, long timestamp)
specifier|public
name|void
name|enrollAttempt
parameter_list|(
name|TaskAttemptStatus
name|reportedStatus
parameter_list|,
name|long
name|timestamp
parameter_list|)
function_decl|;
DECL|method|attemptEnrolledTime (TaskAttemptId attemptID)
specifier|public
name|long
name|attemptEnrolledTime
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
function_decl|;
DECL|method|updateAttempt (TaskAttemptStatus reportedStatus, long timestamp)
specifier|public
name|void
name|updateAttempt
parameter_list|(
name|TaskAttemptStatus
name|reportedStatus
parameter_list|,
name|long
name|timestamp
parameter_list|)
function_decl|;
DECL|method|contextualize (Configuration conf, AppContext context)
specifier|public
name|void
name|contextualize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AppContext
name|context
parameter_list|)
function_decl|;
comment|/**    *    * Find a maximum reasonable execution wallclock time.  Includes the time    * already elapsed.    *    * Find a maximum reasonable execution time.  Includes the time    * already elapsed.  If the projected total execution time for this task    * ever exceeds its reasonable execution time, we may speculate it.    *    * @param id the {@link TaskId} of the task we are asking about    * @return the task's maximum reasonable runtime, or MAX_VALUE if    *         we don't have enough information to rule out any runtime,    *         however long.    *    */
DECL|method|thresholdRuntime (TaskId id)
specifier|public
name|long
name|thresholdRuntime
parameter_list|(
name|TaskId
name|id
parameter_list|)
function_decl|;
comment|/**    *    * Estimate a task attempt's total runtime.  Includes the time already    * elapsed.    *    * @param id the {@link TaskAttemptId} of the attempt we are asking about    * @return our best estimate of the attempt's runtime, or {@code -1} if    *         we don't have enough information yet to produce an estimate.    *    */
DECL|method|estimatedRuntime (TaskAttemptId id)
specifier|public
name|long
name|estimatedRuntime
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|)
function_decl|;
comment|/**    *    * Estimates how long a new attempt on this task will take if we start    *  one now    *    * @param id the {@link TaskId} of the task we are asking about    * @return our best estimate of a new attempt's runtime, or {@code -1} if    *         we don't have enough information yet to produce an estimate.    *    */
DECL|method|estimatedNewAttemptRuntime (TaskId id)
specifier|public
name|long
name|estimatedNewAttemptRuntime
parameter_list|(
name|TaskId
name|id
parameter_list|)
function_decl|;
comment|/**    *    * Computes the width of the error band of our estimate of the task    *  runtime as returned by {@link #estimatedRuntime(TaskAttemptId)}    *    * @param id the {@link TaskAttemptId} of the attempt we are asking about    * @return our best estimate of the attempt's runtime, or {@code -1} if    *         we don't have enough information yet to produce an estimate.    *    */
DECL|method|runtimeEstimateVariance (TaskAttemptId id)
specifier|public
name|long
name|runtimeEstimateVariance
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|)
function_decl|;
comment|/**    *    * Returns true if the estimator has no updates records for a threshold time    * window. This helps to identify task attempts that are stalled at the    * beginning of execution.    *    * @param id the {@link TaskAttemptId} of the attempt we are asking about    * @param timeStamp the time of the report we compare with    * @return true if the task attempt has no progress for a given time window    *    */
DECL|method|hasStagnatedProgress (TaskAttemptId id, long timeStamp)
specifier|default
name|boolean
name|hasStagnatedProgress
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|,
name|long
name|timeStamp
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_interface

end_unit

