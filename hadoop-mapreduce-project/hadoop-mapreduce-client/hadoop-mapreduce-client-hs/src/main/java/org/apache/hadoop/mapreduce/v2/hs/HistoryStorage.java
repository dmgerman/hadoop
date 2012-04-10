begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
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
name|hs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobState
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
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|v2
operator|.
name|hs
operator|.
name|webapp
operator|.
name|dao
operator|.
name|JobsInfo
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Provides an API to query jobs that have finished.   */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|HistoryStorage
specifier|public
interface|interface
name|HistoryStorage
block|{
comment|/**    * Give the Storage a reference to a class that can be used to interact with    * history files.    * @param hsManager the class that is used to interact with history files.    */
DECL|method|setHistoryFileManager (HistoryFileManager hsManager)
name|void
name|setHistoryFileManager
parameter_list|(
name|HistoryFileManager
name|hsManager
parameter_list|)
function_decl|;
comment|/**    * Look for a set of partial jobs.    * @param offset the offset into the list of jobs.    * @param count the maximum number of jobs to return.    * @param user only return jobs for the given user.    * @param queue only return jobs for in the given queue.    * @param sBegin only return Jobs that started on or after the given time.    * @param sEnd only return Jobs that started on or before the given time.    * @param fBegin only return Jobs that ended on or after the given time.    * @param fEnd only return Jobs that ended on or before the given time.    * @param jobState only return Jobs that are in the given job state.    * @return The list of filtered jobs.    */
DECL|method|getPartialJobs (Long offset, Long count, String user, String queue, Long sBegin, Long sEnd, Long fBegin, Long fEnd, JobState jobState)
name|JobsInfo
name|getPartialJobs
parameter_list|(
name|Long
name|offset
parameter_list|,
name|Long
name|count
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|Long
name|sBegin
parameter_list|,
name|Long
name|sEnd
parameter_list|,
name|Long
name|fBegin
parameter_list|,
name|Long
name|fEnd
parameter_list|,
name|JobState
name|jobState
parameter_list|)
function_decl|;
comment|/**    * Get all of the cached jobs.  This only returns partial jobs and is here for    * legacy reasons.    * @return all of the cached jobs    */
DECL|method|getAllPartialJobs ()
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|getAllPartialJobs
parameter_list|()
function_decl|;
comment|/**    * Get a fully parsed job.    * @param jobId the id of the job    * @return the job, or null if it is not found.    */
DECL|method|getFullJob (JobId jobId)
name|Job
name|getFullJob
parameter_list|(
name|JobId
name|jobId
parameter_list|)
function_decl|;
comment|/**    * Informs the Storage that a job has been removed from HDFS    * @param jobId the ID of the job that was removed.    */
DECL|method|jobRemovedFromHDFS (JobId jobId)
name|void
name|jobRemovedFromHDFS
parameter_list|(
name|JobId
name|jobId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

