begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
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
name|mapred
operator|.
name|JobConf
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
name|InputSplit
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
name|JobID
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
name|TaskType
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
name|tools
operator|.
name|rumen
operator|.
name|Pre21JobHistoryConstants
operator|.
name|Values
import|;
end_import

begin_comment
comment|/**  * {@link JobStory} represents the runtime information available for a  * completed Map-Reduce job.  */
end_comment

begin_interface
DECL|interface|JobStory
specifier|public
interface|interface
name|JobStory
block|{
comment|/**    * Get the {@link JobConf} for the job.    * @return the<code>JobConf</code> for the job    */
DECL|method|getJobConf ()
specifier|public
name|JobConf
name|getJobConf
parameter_list|()
function_decl|;
comment|/**    * Get the job name.    * @return the job name    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Get the job ID    * @return the job ID    */
DECL|method|getJobID ()
specifier|public
name|JobID
name|getJobID
parameter_list|()
function_decl|;
comment|/**    * Get the user who ran the job.    * @return the user who ran the job    */
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * Get the job submission time.    * @return the job submission time    */
DECL|method|getSubmissionTime ()
specifier|public
name|long
name|getSubmissionTime
parameter_list|()
function_decl|;
comment|/**    * Get the number of maps in the {@link JobStory}.    * @return the number of maps in the<code>Job</code>    */
DECL|method|getNumberMaps ()
specifier|public
name|int
name|getNumberMaps
parameter_list|()
function_decl|;
comment|/**    * Get the number of reduce in the {@link JobStory}.    * @return the number of reduces in the<code>Job</code>    */
DECL|method|getNumberReduces ()
specifier|public
name|int
name|getNumberReduces
parameter_list|()
function_decl|;
comment|/**    * Get the input splits for the job.    * @return the input splits for the job    */
DECL|method|getInputSplits ()
specifier|public
name|InputSplit
index|[]
name|getInputSplits
parameter_list|()
function_decl|;
comment|/**    * Get {@link TaskInfo} for a given task.    * @param taskType {@link TaskType} of the task    * @param taskNumber Partition number of the task    * @return the<code>TaskInfo</code> for the given task    */
DECL|method|getTaskInfo (TaskType taskType, int taskNumber)
specifier|public
name|TaskInfo
name|getTaskInfo
parameter_list|(
name|TaskType
name|taskType
parameter_list|,
name|int
name|taskNumber
parameter_list|)
function_decl|;
comment|/**    * Get {@link TaskAttemptInfo} for a given task-attempt, without regard to    * impact of locality (e.g. not needed to make scheduling decisions).    * @param taskType {@link TaskType} of the task-attempt    * @param taskNumber Partition number of the task-attempt    * @param taskAttemptNumber Attempt number of the task    * @return the<code>TaskAttemptInfo</code> for the given task-attempt    */
DECL|method|getTaskAttemptInfo (TaskType taskType, int taskNumber, int taskAttemptNumber)
specifier|public
name|TaskAttemptInfo
name|getTaskAttemptInfo
parameter_list|(
name|TaskType
name|taskType
parameter_list|,
name|int
name|taskNumber
parameter_list|,
name|int
name|taskAttemptNumber
parameter_list|)
function_decl|;
comment|/**    * Get {@link TaskAttemptInfo} for a given task-attempt, considering impact    * of locality.    * @param taskNumber Partition number of the task-attempt    * @param taskAttemptNumber Attempt number of the task    * @param locality Data locality of the task as scheduled in simulation    * @return the<code>TaskAttemptInfo</code> for the given task-attempt    */
specifier|public
name|TaskAttemptInfo
DECL|method|getMapTaskAttemptInfoAdjusted (int taskNumber, int taskAttemptNumber, int locality)
name|getMapTaskAttemptInfoAdjusted
parameter_list|(
name|int
name|taskNumber
parameter_list|,
name|int
name|taskAttemptNumber
parameter_list|,
name|int
name|locality
parameter_list|)
function_decl|;
comment|/**    * Get the outcome of the job execution.    * @return The outcome of the job execution.    */
DECL|method|getOutcome ()
specifier|public
name|Values
name|getOutcome
parameter_list|()
function_decl|;
comment|/**    * Get the queue where the job is submitted.    * @return the queue where the job is submitted.    */
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

