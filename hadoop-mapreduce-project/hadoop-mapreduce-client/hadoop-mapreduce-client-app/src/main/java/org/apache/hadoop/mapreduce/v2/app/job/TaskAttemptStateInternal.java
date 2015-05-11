begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
import|;
end_import

begin_comment
comment|/** * TaskAttemptImpl internal state machine states. * */
end_comment

begin_enum
annotation|@
name|Private
DECL|enum|TaskAttemptStateInternal
specifier|public
enum|enum
name|TaskAttemptStateInternal
block|{
DECL|enumConstant|NEW
name|NEW
block|,
DECL|enumConstant|UNASSIGNED
name|UNASSIGNED
block|,
DECL|enumConstant|ASSIGNED
name|ASSIGNED
block|,
DECL|enumConstant|RUNNING
name|RUNNING
block|,
DECL|enumConstant|COMMIT_PENDING
name|COMMIT_PENDING
block|,
comment|// Transition into SUCCESS_FINISHING_CONTAINER
comment|// After the attempt finishes successfully from
comment|// TaskUmbilicalProtocol's point of view, it will transition to
comment|// SUCCESS_FINISHING_CONTAINER state. That will give a chance for the
comment|// container to exit by itself. In the transition,
comment|// the attempt will notify the task via T_ATTEMPT_SUCCEEDED so that
comment|// from job point of view, the task is considered succeeded.
comment|// Transition out of SUCCESS_FINISHING_CONTAINER
comment|// The attempt will transition from SUCCESS_FINISHING_CONTAINER to
comment|// SUCCESS_CONTAINER_CLEANUP if it doesn't receive container exit
comment|// notification within TASK_EXIT_TIMEOUT;
comment|// Or it will transition to SUCCEEDED if it receives container exit
comment|// notification from YARN.
DECL|enumConstant|SUCCESS_FINISHING_CONTAINER
name|SUCCESS_FINISHING_CONTAINER
block|,
comment|// Transition into FAIL_FINISHING_CONTAINER
comment|// After the attempt fails from
comment|// TaskUmbilicalProtocol's point of view, it will transition to
comment|// FAIL_FINISHING_CONTAINER state. That will give a chance for the container
comment|// to exit by itself. In the transition,
comment|// the attempt will notify the task via T_ATTEMPT_FAILED so that
comment|// from job point of view, the task is considered failed.
comment|// Transition out of FAIL_FINISHING_CONTAINER
comment|// The attempt will transition from FAIL_FINISHING_CONTAINER to
comment|// FAIL_CONTAINER_CLEANUP if it doesn't receive container exit
comment|// notification within TASK_EXIT_TIMEOUT;
comment|// Or it will transition to FAILED if it receives container exit
comment|// notification from YARN.
DECL|enumConstant|FAIL_FINISHING_CONTAINER
name|FAIL_FINISHING_CONTAINER
block|,
DECL|enumConstant|SUCCESS_CONTAINER_CLEANUP
name|SUCCESS_CONTAINER_CLEANUP
block|,
DECL|enumConstant|SUCCEEDED
name|SUCCEEDED
block|,
DECL|enumConstant|FAIL_CONTAINER_CLEANUP
name|FAIL_CONTAINER_CLEANUP
block|,
DECL|enumConstant|FAIL_TASK_CLEANUP
name|FAIL_TASK_CLEANUP
block|,
DECL|enumConstant|FAILED
name|FAILED
block|,
DECL|enumConstant|KILL_CONTAINER_CLEANUP
name|KILL_CONTAINER_CLEANUP
block|,
DECL|enumConstant|KILL_TASK_CLEANUP
name|KILL_TASK_CLEANUP
block|,
DECL|enumConstant|KILLED
name|KILLED
block|, }
end_enum

end_unit

