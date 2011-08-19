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

begin_comment
comment|/**  * Event types handled by Job.  */
end_comment

begin_enum
DECL|enum|JobEventType
specifier|public
enum|enum
name|JobEventType
block|{
comment|//Producer:Client
DECL|enumConstant|JOB_KILL
name|JOB_KILL
block|,
comment|//Producer:MRAppMaster
DECL|enumConstant|JOB_INIT
name|JOB_INIT
block|,
DECL|enumConstant|JOB_START
name|JOB_START
block|,
comment|//Producer:Task
DECL|enumConstant|JOB_TASK_COMPLETED
name|JOB_TASK_COMPLETED
block|,
DECL|enumConstant|JOB_MAP_TASK_RESCHEDULED
name|JOB_MAP_TASK_RESCHEDULED
block|,
DECL|enumConstant|JOB_TASK_ATTEMPT_COMPLETED
name|JOB_TASK_ATTEMPT_COMPLETED
block|,
comment|//Producer:Job
DECL|enumConstant|JOB_COMPLETED
name|JOB_COMPLETED
block|,
comment|//Producer:Any component
DECL|enumConstant|JOB_DIAGNOSTIC_UPDATE
name|JOB_DIAGNOSTIC_UPDATE
block|,
DECL|enumConstant|INTERNAL_ERROR
name|INTERNAL_ERROR
block|,
DECL|enumConstant|JOB_COUNTER_UPDATE
name|JOB_COUNTER_UPDATE
block|,
comment|//Producer:TaskAttemptListener
DECL|enumConstant|JOB_TASK_ATTEMPT_FETCH_FAILURE
name|JOB_TASK_ATTEMPT_FETCH_FAILURE
block|}
end_enum

end_unit

