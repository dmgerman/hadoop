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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|TaskID
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
name|test
operator|.
name|system
operator|.
name|DaemonProtocol
import|;
end_import

begin_comment
comment|/**  * Client side API's exposed from JobTracker.  */
end_comment

begin_interface
DECL|interface|JTProtocol
specifier|public
interface|interface
name|JTProtocol
extends|extends
name|DaemonProtocol
block|{
DECL|field|versionID
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * Get the information pertaining to given job.<br/>    * The returned JobInfo object can be null when the    * specified job by the job id is retired from the     * JobTracker memory which happens after job is     * completed.<br/>    *     * @param id    *          of the job for which information is required.    * @return information of regarding job null if job is     *         retired from JobTracker memory.    * @throws IOException    */
DECL|method|getJobInfo (JobID jobID)
specifier|public
name|JobInfo
name|getJobInfo
parameter_list|(
name|JobID
name|jobID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the information pertaining to a task.<br/>    * The returned TaskInfo object can be null when the     * specified task specified by the task id is retired    * from the JobTracker memory which happens after the    * job is completed.<br/>    * @param id    *          of the task for which information is required.    * @return information of regarding the task null if the     *          task is retired from JobTracker memory.    * @throws IOException    */
DECL|method|getTaskInfo (TaskID taskID)
specifier|public
name|TaskInfo
name|getTaskInfo
parameter_list|(
name|TaskID
name|taskID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the information pertaining to a given TaskTracker.<br/>    * The returned TTInfo class can be null if the given TaskTracker    * information is removed from JobTracker memory which is done    * when the TaskTracker is marked lost by the JobTracker.<br/>    * @param name    *          of the tracker.    * @return information regarding the tracker null if the TaskTracker    *          is marked lost by the JobTracker.    * @throws IOException    */
DECL|method|getTTInfo (String trackerName)
specifier|public
name|TTInfo
name|getTTInfo
parameter_list|(
name|String
name|trackerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets a list of all available jobs with JobTracker.<br/>    *     * @return list of all jobs.    * @throws IOException    */
DECL|method|getAllJobInfo ()
specifier|public
name|JobInfo
index|[]
name|getAllJobInfo
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets a list of tasks pertaining to a job.<br/>    *     * @param id    *          of the job.    *     * @return list of all tasks for the job.    * @throws IOException    */
DECL|method|getTaskInfo (JobID jobID)
specifier|public
name|TaskInfo
index|[]
name|getTaskInfo
parameter_list|(
name|JobID
name|jobID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets a list of TaskTrackers which have reported to the JobTracker.<br/>    *     * @return list of all TaskTracker.    * @throws IOException    */
DECL|method|getAllTTInfo ()
specifier|public
name|TTInfo
index|[]
name|getAllTTInfo
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if a given job is retired from the JobTrackers Memory.<br/>    *     * @param id    *          of the job    * @return true if job is retired.    * @throws IOException    */
DECL|method|isJobRetired (JobID jobID)
name|boolean
name|isJobRetired
parameter_list|(
name|JobID
name|jobID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the location of the history file for a retired job.<br/>    *     * @param id    *          of the job    * @return location of history file    * @throws IOException    */
DECL|method|getJobHistoryLocationForRetiredJob (JobID jobID)
name|String
name|getJobHistoryLocationForRetiredJob
parameter_list|(
name|JobID
name|jobID
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

