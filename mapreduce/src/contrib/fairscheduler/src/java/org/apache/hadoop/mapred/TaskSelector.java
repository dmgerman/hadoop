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
name|conf
operator|.
name|Configurable
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
name|mapreduce
operator|.
name|server
operator|.
name|jobtracker
operator|.
name|TaskTracker
import|;
end_import

begin_comment
comment|/**  * A pluggable object for selecting tasks to run from a {@link JobInProgress} on  * a given {@link TaskTracker}, for use by the {@link TaskScheduler}. The  *<code>TaskSelector</code> is in charge of managing both locality and  * speculative execution. For the latter purpose, it must also provide counts of  * how many tasks each speculative job needs to launch, so that the scheduler  * can take this into account in its calculations.  */
end_comment

begin_class
DECL|class|TaskSelector
specifier|public
specifier|abstract
class|class
name|TaskSelector
implements|implements
name|Configurable
block|{
DECL|field|conf
specifier|protected
name|Configuration
name|conf
decl_stmt|;
DECL|field|taskTrackerManager
specifier|protected
name|TaskTrackerManager
name|taskTrackerManager
decl_stmt|;
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|setTaskTrackerManager ( TaskTrackerManager taskTrackerManager)
specifier|public
specifier|synchronized
name|void
name|setTaskTrackerManager
parameter_list|(
name|TaskTrackerManager
name|taskTrackerManager
parameter_list|)
block|{
name|this
operator|.
name|taskTrackerManager
operator|=
name|taskTrackerManager
expr_stmt|;
block|}
comment|/**    * Lifecycle method to allow the TaskSelector to start any work in separate    * threads.    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
comment|// do nothing
block|}
comment|/**    * Lifecycle method to allow the TaskSelector to stop any work it is doing.    */
DECL|method|terminate ()
specifier|public
name|void
name|terminate
parameter_list|()
throws|throws
name|IOException
block|{
comment|// do nothing
block|}
comment|/**    * How many speculative map tasks does the given job want to launch?    * @param job The job to count speculative maps for    * @return Number of speculative maps that can be launched for job    */
DECL|method|neededSpeculativeMaps (JobInProgress job)
specifier|public
specifier|abstract
name|int
name|neededSpeculativeMaps
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
function_decl|;
comment|/**    * How many speculative reduce tasks does the given job want to launch?    * @param job The job to count speculative reduces for    * @return Number of speculative reduces that can be launched for job    */
DECL|method|neededSpeculativeReduces (JobInProgress job)
specifier|public
specifier|abstract
name|int
name|neededSpeculativeReduces
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
function_decl|;
comment|/**    * Choose a map task to run from the given job on the given TaskTracker.    * @param taskTracker {@link TaskTrackerStatus} of machine to run on    * @param job Job to select a task for    * @return A {@link Task} to run on the machine, or<code>null</code> if    *         no map should be launched from this job on the task tracker.    * @throws IOException     */
DECL|method|obtainNewMapTask (TaskTrackerStatus taskTracker, JobInProgress job, int localityLevel)
specifier|public
specifier|abstract
name|Task
name|obtainNewMapTask
parameter_list|(
name|TaskTrackerStatus
name|taskTracker
parameter_list|,
name|JobInProgress
name|job
parameter_list|,
name|int
name|localityLevel
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Choose a reduce task to run from the given job on the given TaskTracker.    * @param taskTracker {@link TaskTrackerStatus} of machine to run on    * @param job Job to select a task for    * @return A {@link Task} to run on the machine, or<code>null</code> if    *         no reduce should be launched from this job on the task tracker.    * @throws IOException     */
DECL|method|obtainNewReduceTask (TaskTrackerStatus taskTracker, JobInProgress job)
specifier|public
specifier|abstract
name|Task
name|obtainNewReduceTask
parameter_list|(
name|TaskTrackerStatus
name|taskTracker
parameter_list|,
name|JobInProgress
name|job
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

