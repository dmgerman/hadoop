begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job.event
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
name|job
operator|.
name|event
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
name|TaskAttemptId
import|;
end_import

begin_comment
comment|/**  * TaskAttemptTooManyFetchFailureEvent is used for TA_TOO_MANY_FETCH_FAILURE.  */
end_comment

begin_class
DECL|class|TaskAttemptTooManyFetchFailureEvent
specifier|public
class|class
name|TaskAttemptTooManyFetchFailureEvent
extends|extends
name|TaskAttemptEvent
block|{
DECL|field|reduceID
specifier|private
name|TaskAttemptId
name|reduceID
decl_stmt|;
DECL|field|reduceHostname
specifier|private
name|String
name|reduceHostname
decl_stmt|;
comment|/**    * Create a new TaskAttemptTooManyFetchFailureEvent.    * @param attemptId the id of the mapper task attempt    * @param reduceId the id of the reporting reduce task attempt.    * @param reduceHost the hostname of the reporting reduce task attempt.    */
DECL|method|TaskAttemptTooManyFetchFailureEvent (TaskAttemptId attemptId, TaskAttemptId reduceId, String reduceHost)
specifier|public
name|TaskAttemptTooManyFetchFailureEvent
parameter_list|(
name|TaskAttemptId
name|attemptId
parameter_list|,
name|TaskAttemptId
name|reduceId
parameter_list|,
name|String
name|reduceHost
parameter_list|)
block|{
name|super
argument_list|(
name|attemptId
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_TOO_MANY_FETCH_FAILURE
argument_list|)
expr_stmt|;
name|this
operator|.
name|reduceID
operator|=
name|reduceId
expr_stmt|;
name|this
operator|.
name|reduceHostname
operator|=
name|reduceHost
expr_stmt|;
block|}
DECL|method|getReduceId ()
specifier|public
name|TaskAttemptId
name|getReduceId
parameter_list|()
block|{
return|return
name|reduceID
return|;
block|}
DECL|method|getReduceHost ()
specifier|public
name|String
name|getReduceHost
parameter_list|()
block|{
return|return
name|reduceHostname
return|;
block|}
block|}
end_class

end_unit

