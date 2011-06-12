begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.test.system
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|test
operator|.
name|system
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
name|io
operator|.
name|Writable
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
name|mapred
operator|.
name|TaskStatus
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
name|mapred
operator|.
name|TaskTracker
import|;
end_import

begin_comment
comment|/**  * Task state information as seen by the TT.  */
end_comment

begin_interface
DECL|interface|TTTaskInfo
specifier|public
interface|interface
name|TTTaskInfo
extends|extends
name|Writable
block|{
comment|/**    * Has task occupied a slot? A task occupies a slot once it starts localizing    * on the {@link TaskTracker}<br/>    *     * @return true if task has started occupying a slot.    */
DECL|method|slotTaken ()
name|boolean
name|slotTaken
parameter_list|()
function_decl|;
comment|/**    * Has the task been killed?<br/>    *     * @return true, if task has been killed.    */
DECL|method|wasKilled ()
name|boolean
name|wasKilled
parameter_list|()
function_decl|;
comment|/**    * Gets the task status associated with the particular task trackers task     * view.<br/>    *     * @return status of the particular task    */
DECL|method|getTaskStatus ()
name|TaskStatus
name|getTaskStatus
parameter_list|()
function_decl|;
comment|/**    * Gets the configuration object of the task.    * @return    */
DECL|method|getConf ()
name|Configuration
name|getConf
parameter_list|()
function_decl|;
comment|/**    * Gets the user of the task.    * @return    */
DECL|method|getUser ()
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * Provides information as to whether the task is a cleanup of task.    * @return true if it is a clean up of task.    */
DECL|method|isTaskCleanupTask ()
name|boolean
name|isTaskCleanupTask
parameter_list|()
function_decl|;
comment|/**    * Gets the pid of the running task on the task-tracker.    *     * @return pid of the task.    */
DECL|method|getPid ()
name|String
name|getPid
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

