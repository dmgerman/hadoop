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
comment|/**  * Event types handled by TaskAttempt.  */
end_comment

begin_enum
DECL|enum|TaskAttemptEventType
specifier|public
enum|enum
name|TaskAttemptEventType
block|{
comment|//Producer:Task
DECL|enumConstant|TA_SCHEDULE
name|TA_SCHEDULE
block|,
DECL|enumConstant|TA_RESCHEDULE
name|TA_RESCHEDULE
block|,
DECL|enumConstant|TA_RECOVER
name|TA_RECOVER
block|,
comment|//Producer:Client, Task
DECL|enumConstant|TA_KILL
name|TA_KILL
block|,
comment|//Producer:ContainerAllocator
DECL|enumConstant|TA_ASSIGNED
name|TA_ASSIGNED
block|,
DECL|enumConstant|TA_CONTAINER_COMPLETED
name|TA_CONTAINER_COMPLETED
block|,
comment|//Producer:ContainerLauncher
DECL|enumConstant|TA_CONTAINER_LAUNCHED
name|TA_CONTAINER_LAUNCHED
block|,
DECL|enumConstant|TA_CONTAINER_LAUNCH_FAILED
name|TA_CONTAINER_LAUNCH_FAILED
block|,
DECL|enumConstant|TA_CONTAINER_CLEANED
name|TA_CONTAINER_CLEANED
block|,
comment|//Producer:TaskAttemptListener
DECL|enumConstant|TA_DIAGNOSTICS_UPDATE
name|TA_DIAGNOSTICS_UPDATE
block|,
DECL|enumConstant|TA_COMMIT_PENDING
name|TA_COMMIT_PENDING
block|,
DECL|enumConstant|TA_DONE
name|TA_DONE
block|,
DECL|enumConstant|TA_FAILMSG
name|TA_FAILMSG
block|,
DECL|enumConstant|TA_UPDATE
name|TA_UPDATE
block|,
DECL|enumConstant|TA_TIMED_OUT
name|TA_TIMED_OUT
block|,
comment|//Producer:TaskCleaner
DECL|enumConstant|TA_CLEANUP_DONE
name|TA_CLEANUP_DONE
block|,
comment|//Producer:Job
DECL|enumConstant|TA_TOO_MANY_FETCH_FAILURE
name|TA_TOO_MANY_FETCH_FAILURE
block|, }
end_enum

end_unit

