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
name|util
operator|.
name|HashMap
import|;
end_import

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
name|mapred
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
name|tools
operator|.
name|rumen
operator|.
name|JobStory
import|;
end_import

begin_comment
comment|/**  * A static ({@link JobID}, {@link JobStory}) mapping, used by {@link JobClient}  * and {@link JobTracker} for job submission.  */
end_comment

begin_class
DECL|class|SimulatorJobCache
specifier|public
class|class
name|SimulatorJobCache
block|{
DECL|field|submittedJobs
specifier|private
specifier|static
name|Map
argument_list|<
name|JobID
argument_list|,
name|JobStory
argument_list|>
name|submittedJobs
init|=
operator|new
name|HashMap
argument_list|<
name|JobID
argument_list|,
name|JobStory
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Put ({@link JobID}, {@link JobStory}) into the mapping.    * @param jobId id of the job.    * @param job {@link JobStory} object of the job.    */
DECL|method|put (JobID jobId, JobStory job)
specifier|public
specifier|static
name|void
name|put
parameter_list|(
name|JobID
name|jobId
parameter_list|,
name|JobStory
name|job
parameter_list|)
block|{
name|submittedJobs
operator|.
name|put
argument_list|(
name|jobId
argument_list|,
name|job
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the job identified by {@link JobID} and remove it from the mapping.    * @param jobId id of the job.    * @return {@link JobStory} object of the job.    */
DECL|method|get (JobID jobId)
specifier|public
specifier|static
name|JobStory
name|get
parameter_list|(
name|JobID
name|jobId
parameter_list|)
block|{
return|return
name|submittedJobs
operator|.
name|remove
argument_list|(
name|jobId
argument_list|)
return|;
block|}
comment|/**    * Check the job at the head of queue, without removing it from the mapping.    * @param jobId id of the job.    * @return {@link JobStory} object of the job.    */
DECL|method|peek (JobID jobId)
specifier|public
specifier|static
name|JobStory
name|peek
parameter_list|(
name|JobID
name|jobId
parameter_list|)
block|{
return|return
name|submittedJobs
operator|.
name|get
argument_list|(
name|jobId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

