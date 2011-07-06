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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|metrics
operator|.
name|MetricsContext
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
name|metrics
operator|.
name|MetricsRecord
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
name|metrics
operator|.
name|MetricsUtil
import|;
end_import

begin_comment
comment|/**  * A Schedulable represents an entity that can launch tasks, such as a job  * or a pool. It provides a common interface so that algorithms such as fair  * sharing can be applied both within a pool and across pools. There are   * currently two types of Schedulables: JobSchedulables, which represent a  * single job, and PoolSchedulables, which allocate among jobs in their pool.  *   * Separate sets of Schedulables are used for maps and reduces. Each pool has  * both a mapSchedulable and a reduceSchedulable, and so does each job.  *   * A Schedulable is responsible for three roles:  * 1) It can launch tasks through assignTask().  * 2) It provides information about the job/pool to the scheduler, including:  *    - Demand (maximum number of tasks required)  *    - Number of currently running tasks  *    - Minimum share (for pools)  *    - Job/pool weight (for fair sharing)  *    - Start time and priority (for FIFO)  * 3) It can be assigned a fair share, for use with fair scheduling.  *   * Schedulable also contains two methods for performing scheduling computations:  * - updateDemand() is called periodically to compute the demand of the various  *   jobs and pools, which may be expensive (e.g. jobs must iterate through all  *   their tasks to count failed tasks, tasks that can be speculated, etc).  * - redistributeShare() is called after demands are updated and a Schedulable's  *   fair share has been set by its parent to let it distribute its share among  *   the other Schedulables within it (e.g. for pools that want to perform fair  *   sharing among their jobs).  */
end_comment

begin_class
DECL|class|Schedulable
specifier|abstract
class|class
name|Schedulable
block|{
comment|/** Fair share assigned to this Schedulable */
DECL|field|fairShare
specifier|private
name|double
name|fairShare
init|=
literal|0
decl_stmt|;
DECL|field|metrics
specifier|protected
name|MetricsRecord
name|metrics
decl_stmt|;
comment|/**    * Name of job/pool, used for debugging as well as for breaking ties in    * scheduling order deterministically.     */
DECL|method|getName ()
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * @return the type of tasks that this pool schedules    */
DECL|method|getTaskType ()
specifier|public
specifier|abstract
name|TaskType
name|getTaskType
parameter_list|()
function_decl|;
comment|/**    * Maximum number of tasks required by this Schedulable. This is defined as    * number of currently running tasks + number of unlaunched tasks (tasks that    * are either not yet launched or need to be speculated).    */
DECL|method|getDemand ()
specifier|public
specifier|abstract
name|int
name|getDemand
parameter_list|()
function_decl|;
comment|/** Number of tasks the schedulable is currently running. */
DECL|method|getRunningTasks ()
specifier|public
specifier|abstract
name|int
name|getRunningTasks
parameter_list|()
function_decl|;
comment|/** Minimum share slots assigned to the schedulable. */
DECL|method|getMinShare ()
specifier|public
specifier|abstract
name|int
name|getMinShare
parameter_list|()
function_decl|;
comment|/** Job/pool weight in fair sharing. */
DECL|method|getWeight ()
specifier|public
specifier|abstract
name|double
name|getWeight
parameter_list|()
function_decl|;
comment|/** Job priority for jobs in FIFO pools; meaningless for PoolSchedulables. */
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|JobPriority
name|getPriority
parameter_list|()
function_decl|;
comment|/** Start time for jobs in FIFO pools; meaningless for PoolSchedulables. */
DECL|method|getStartTime ()
specifier|public
specifier|abstract
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/** Refresh the Schedulable's demand and those of its children if any. */
DECL|method|updateDemand ()
specifier|public
specifier|abstract
name|void
name|updateDemand
parameter_list|()
function_decl|;
comment|/**     * Distribute the fair share assigned to this Schedulable among its     * children (used in pools where the internal scheduler is fair sharing).     */
DECL|method|redistributeShare ()
specifier|public
specifier|abstract
name|void
name|redistributeShare
parameter_list|()
function_decl|;
comment|/**    * Obtain a task for a given TaskTracker, or null if the Schedulable has    * no tasks to launch at this moment or does not wish to launch a task on    * this TaskTracker (e.g. is waiting for a TaskTracker with local data).     * In addition, if a job is skipped during this search because it is waiting    * for a TaskTracker with local data, this method is expected to add it to    * the<tt>visited</tt> collection passed in, so that the scheduler can    * properly mark it as skipped during this heartbeat. Please see    * {@link FairScheduler#getAllowedLocalityLevel(JobInProgress, long)}    * for details of delay scheduling (waiting for trackers with local data).    *     * @param tts      TaskTracker that the task will be launched on    * @param currentTime Cached time (to prevent excessive calls to gettimeofday)    * @param visited  A Collection to which this method must add all jobs that    *                 were considered during the search for a job to assign.    * @return Task to launch, or null if Schedulable cannot currently launch one.    * @throws IOException Possible if obtainNew(Map|Reduce)Task throws exception.    */
DECL|method|assignTask (TaskTrackerStatus tts, long currentTime, Collection<JobInProgress> visited)
specifier|public
specifier|abstract
name|Task
name|assignTask
parameter_list|(
name|TaskTrackerStatus
name|tts
parameter_list|,
name|long
name|currentTime
parameter_list|,
name|Collection
argument_list|<
name|JobInProgress
argument_list|>
name|visited
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Assign a fair share to this Schedulable. */
DECL|method|setFairShare (double fairShare)
specifier|public
name|void
name|setFairShare
parameter_list|(
name|double
name|fairShare
parameter_list|)
block|{
name|this
operator|.
name|fairShare
operator|=
name|fairShare
expr_stmt|;
block|}
comment|/** Get the fair share assigned to this Schedulable. */
DECL|method|getFairShare ()
specifier|public
name|double
name|getFairShare
parameter_list|()
block|{
return|return
name|fairShare
return|;
block|}
comment|/** Return the name of the metrics context for this schedulable */
DECL|method|getMetricsContextName ()
specifier|protected
specifier|abstract
name|String
name|getMetricsContextName
parameter_list|()
function_decl|;
comment|/**    * Set up metrics context    */
DECL|method|initMetrics ()
specifier|protected
name|void
name|initMetrics
parameter_list|()
block|{
name|MetricsContext
name|metricsContext
init|=
name|MetricsUtil
operator|.
name|getContext
argument_list|(
literal|"fairscheduler"
argument_list|)
decl_stmt|;
name|this
operator|.
name|metrics
operator|=
name|MetricsUtil
operator|.
name|createRecord
argument_list|(
name|metricsContext
argument_list|,
name|getMetricsContextName
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setTag
argument_list|(
literal|"name"
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setTag
argument_list|(
literal|"taskType"
argument_list|,
name|getTaskType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|cleanupMetrics ()
name|void
name|cleanupMetrics
parameter_list|()
block|{
name|metrics
operator|.
name|remove
argument_list|()
expr_stmt|;
name|metrics
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setMetricValues (MetricsRecord metrics)
specifier|protected
name|void
name|setMetricValues
parameter_list|(
name|MetricsRecord
name|metrics
parameter_list|)
block|{
name|metrics
operator|.
name|setMetric
argument_list|(
literal|"fairShare"
argument_list|,
operator|(
name|float
operator|)
name|getFairShare
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setMetric
argument_list|(
literal|"minShare"
argument_list|,
name|getMinShare
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setMetric
argument_list|(
literal|"demand"
argument_list|,
name|getDemand
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setMetric
argument_list|(
literal|"weight"
argument_list|,
operator|(
name|float
operator|)
name|getWeight
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setMetric
argument_list|(
literal|"runningTasks"
argument_list|,
name|getRunningTasks
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|updateMetrics ()
specifier|abstract
name|void
name|updateMetrics
parameter_list|()
function_decl|;
comment|/** Convenient toString implementation for debugging. */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"[%s, demand=%d, running=%d, share=%.1f, w=%.1f]"
argument_list|,
name|getName
argument_list|()
argument_list|,
name|getDemand
argument_list|()
argument_list|,
name|getRunningTasks
argument_list|()
argument_list|,
name|fairShare
argument_list|,
name|getWeight
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

