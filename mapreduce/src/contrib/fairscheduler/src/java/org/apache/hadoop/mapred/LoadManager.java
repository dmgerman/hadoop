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
name|TaskType
import|;
end_import

begin_comment
comment|/**  * A pluggable object that manages the load on each {@link TaskTracker}, telling  * the {@link TaskScheduler} when it can launch new tasks.   */
end_comment

begin_class
DECL|class|LoadManager
specifier|public
specifier|abstract
class|class
name|LoadManager
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
DECL|field|schedulingLog
specifier|protected
name|FairSchedulerEventLog
name|schedulingLog
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
DECL|method|setEventLog (FairSchedulerEventLog schedulingLog)
specifier|public
name|void
name|setEventLog
parameter_list|(
name|FairSchedulerEventLog
name|schedulingLog
parameter_list|)
block|{
name|this
operator|.
name|schedulingLog
operator|=
name|schedulingLog
expr_stmt|;
block|}
comment|/**    * Lifecycle method to allow the LoadManager to start any work in separate    * threads.    */
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
comment|/**    * Lifecycle method to allow the LoadManager to stop any work it is doing.    */
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
comment|/**    * Can a given {@link TaskTracker} run another map task?    * This method may check whether the specified tracker has    * enough resources to run another map task.    * @param tracker The machine we wish to run a new map on    * @param totalRunnableMaps Set of running jobs in the cluster    * @param totalMapSlots The total number of map slots in the cluster    * @return true if another map can be launched on<code>tracker</code>    */
DECL|method|canAssignMap (TaskTrackerStatus tracker, int totalRunnableMaps, int totalMapSlots)
specifier|public
specifier|abstract
name|boolean
name|canAssignMap
parameter_list|(
name|TaskTrackerStatus
name|tracker
parameter_list|,
name|int
name|totalRunnableMaps
parameter_list|,
name|int
name|totalMapSlots
parameter_list|)
function_decl|;
comment|/**    * Can a given {@link TaskTracker} run another reduce task?    * This method may check whether the specified tracker has    * enough resources to run another reduce task.    * @param tracker The machine we wish to run a new map on    * @param totalRunnableReduces Set of running jobs in the cluster    * @param totalReduceSlots The total number of reduce slots in the cluster    * @return true if another reduce can be launched on<code>tracker</code>    */
DECL|method|canAssignReduce (TaskTrackerStatus tracker, int totalRunnableReduces, int totalReduceSlots)
specifier|public
specifier|abstract
name|boolean
name|canAssignReduce
parameter_list|(
name|TaskTrackerStatus
name|tracker
parameter_list|,
name|int
name|totalRunnableReduces
parameter_list|,
name|int
name|totalReduceSlots
parameter_list|)
function_decl|;
comment|/**    * Can a given {@link TaskTracker} run another new task from a given job?     * This method is provided for use by LoadManagers that take into     * account jobs' individual resource needs when placing tasks.    * @param tracker The machine we wish to run a new map on    * @param job The job from which we want to run a task on this machine    * @param type The type of task that we want to run on    * @return true if this task can be launched on<code>tracker</code>    */
DECL|method|canLaunchTask (TaskTrackerStatus tracker, JobInProgress job, TaskType type)
specifier|public
specifier|abstract
name|boolean
name|canLaunchTask
parameter_list|(
name|TaskTrackerStatus
name|tracker
parameter_list|,
name|JobInProgress
name|job
parameter_list|,
name|TaskType
name|type
parameter_list|)
function_decl|;
block|}
end_class

end_unit

