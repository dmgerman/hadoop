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
name|JobTracker
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
name|mapreduce
operator|.
name|TaskID
import|;
end_import

begin_comment
comment|/**  * Task state information of a TaskInProgress as seen by the {@link JobTracker}  */
end_comment

begin_interface
DECL|interface|TaskInfo
specifier|public
interface|interface
name|TaskInfo
extends|extends
name|Writable
block|{
comment|/**    * Gets the task id of the TaskInProgress.    *     * @return id of the task.    */
DECL|method|getTaskID ()
name|TaskID
name|getTaskID
parameter_list|()
function_decl|;
comment|/**    * Number of times task attempts have failed for the given TaskInProgress.    *<br/>    *     * @return number of failed task attempts.    */
DECL|method|numFailedAttempts ()
name|int
name|numFailedAttempts
parameter_list|()
function_decl|;
comment|/**    * Number of times task attempts have been killed for the given TaskInProgress     *<br/>    *     * @return number of killed task attempts.    */
DECL|method|numKilledAttempts ()
name|int
name|numKilledAttempts
parameter_list|()
function_decl|;
comment|/**    * Gets the progress of the Task in percentage will be in range of 0.0-1.0     *<br/>    *     * @return progress of task in percentage.    */
DECL|method|getProgress ()
name|double
name|getProgress
parameter_list|()
function_decl|;
comment|/**    * Number of attempts currently running for the given TaskInProgress.<br/>    *     * @return number of running attempts.    */
DECL|method|numRunningAttempts ()
name|int
name|numRunningAttempts
parameter_list|()
function_decl|;
comment|/**    * Array of TaskStatus objects that are related to the corresponding    * TaskInProgress object.The task status of the tip is only populated    * once a tracker reports back the task status.<br/>    *     * @return list of task statuses.    */
DECL|method|getTaskStatus ()
name|TaskStatus
index|[]
name|getTaskStatus
parameter_list|()
function_decl|;
comment|/**    * Gets a list of tracker on which the task attempts are scheduled/running.    * Can be empty if the task attempt has succeeded<br/>    *     * @return list of trackers    */
DECL|method|getTaskTrackers ()
name|String
index|[]
name|getTaskTrackers
parameter_list|()
function_decl|;
comment|/**    * Gets if the current TaskInProgress is a setup or cleanup tip.<br/>    *     * @return true if setup/cleanup    */
DECL|method|isSetupOrCleanup ()
name|boolean
name|isSetupOrCleanup
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

