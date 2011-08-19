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
comment|/**  * Event types handled by Task.  */
end_comment

begin_enum
DECL|enum|TaskEventType
specifier|public
enum|enum
name|TaskEventType
block|{
comment|//Producer:Client, Job
DECL|enumConstant|T_KILL
name|T_KILL
block|,
comment|//Producer:Job
DECL|enumConstant|T_SCHEDULE
name|T_SCHEDULE
block|,
comment|//Producer:Speculator
DECL|enumConstant|T_ADD_SPEC_ATTEMPT
name|T_ADD_SPEC_ATTEMPT
block|,
comment|//Producer:TaskAttempt
DECL|enumConstant|T_ATTEMPT_LAUNCHED
name|T_ATTEMPT_LAUNCHED
block|,
DECL|enumConstant|T_ATTEMPT_COMMIT_PENDING
name|T_ATTEMPT_COMMIT_PENDING
block|,
DECL|enumConstant|T_ATTEMPT_FAILED
name|T_ATTEMPT_FAILED
block|,
DECL|enumConstant|T_ATTEMPT_SUCCEEDED
name|T_ATTEMPT_SUCCEEDED
block|,
DECL|enumConstant|T_ATTEMPT_KILLED
name|T_ATTEMPT_KILLED
block|}
end_enum

end_unit

