begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Represents a directive from the {@link org.apache.hadoop.mapred.JobTracker}   * to the {@link org.apache.hadoop.mapred.TaskTracker} to commit the output  * of the task.  *   */
end_comment

begin_class
DECL|class|CommitTaskAction
class|class
name|CommitTaskAction
extends|extends
name|TaskTrackerAction
block|{
DECL|field|taskId
specifier|private
name|TaskAttemptID
name|taskId
decl_stmt|;
DECL|method|CommitTaskAction ()
specifier|public
name|CommitTaskAction
parameter_list|()
block|{
name|super
argument_list|(
name|ActionType
operator|.
name|COMMIT_TASK
argument_list|)
expr_stmt|;
name|taskId
operator|=
operator|new
name|TaskAttemptID
argument_list|()
expr_stmt|;
block|}
DECL|method|CommitTaskAction (TaskAttemptID taskId)
specifier|public
name|CommitTaskAction
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|)
block|{
name|super
argument_list|(
name|ActionType
operator|.
name|COMMIT_TASK
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskId
operator|=
name|taskId
expr_stmt|;
block|}
DECL|method|getTaskID ()
specifier|public
name|TaskAttemptID
name|getTaskID
parameter_list|()
block|{
return|return
name|taskId
return|;
block|}
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|taskId
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|taskId
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

