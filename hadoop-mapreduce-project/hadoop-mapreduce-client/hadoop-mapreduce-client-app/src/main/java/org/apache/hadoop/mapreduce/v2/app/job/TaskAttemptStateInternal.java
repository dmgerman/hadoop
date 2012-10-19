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

