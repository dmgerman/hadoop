begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_class
DECL|class|TaskAttemptFailEvent
specifier|public
class|class
name|TaskAttemptFailEvent
extends|extends
name|TaskAttemptEvent
block|{
DECL|field|fastFail
specifier|private
name|boolean
name|fastFail
decl_stmt|;
comment|/**    * Create a new TaskAttemptFailEvent, with task fastFail disabled.    *    * @param id the id of the task attempt    */
DECL|method|TaskAttemptFailEvent (TaskAttemptId id)
specifier|public
name|TaskAttemptFailEvent
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|)
block|{
name|this
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new TaskAttemptFailEvent.    *    * @param id the id of the task attempt    * @param fastFail should the task fastFail or not.    */
DECL|method|TaskAttemptFailEvent (TaskAttemptId id, boolean fastFail)
specifier|public
name|TaskAttemptFailEvent
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|,
name|boolean
name|fastFail
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_FAILMSG
argument_list|)
expr_stmt|;
name|this
operator|.
name|fastFail
operator|=
name|fastFail
expr_stmt|;
block|}
comment|/**    * Check if task should fast fail or retry    * @return boolean value where true indicates the task should not retry    */
DECL|method|isFastFail ()
specifier|public
name|boolean
name|isFastFail
parameter_list|()
block|{
return|return
name|fastFail
return|;
block|}
block|}
end_class

end_unit

