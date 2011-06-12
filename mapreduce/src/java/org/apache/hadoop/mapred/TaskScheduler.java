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
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * Used by a {@link JobTracker} to schedule {@link Task}s on  * {@link TaskTracker}s.  *<p>  * {@link TaskScheduler}s typically use one or more  * {@link JobInProgressListener}s to receive notifications about jobs.  *<p>  * It is the responsibility of the {@link TaskScheduler}  * to initialize tasks for a job, by calling {@link JobInProgress#initTasks()}  * between the job being added (when  * {@link JobInProgressListener#jobAdded(JobInProgress)} is called)  * and tasks for that job being assigned (by  * {@link #assignTasks(TaskTracker)}).  * @see EagerTaskInitializationListener  */
end_comment

begin_class
DECL|class|TaskScheduler
specifier|abstract
class|class
name|TaskScheduler
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
comment|/**    * Lifecycle method to allow the scheduler to start any work in separate    * threads.    * @throws IOException    */
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
comment|/**    * Lifecycle method to allow the scheduler to stop any work it is doing.    * @throws IOException    */
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
comment|/**    * Returns the tasks we'd like the TaskTracker to execute right now.    *     * @param taskTracker The TaskTracker for which we're looking for tasks.    * @return A list of tasks to run on that TaskTracker, possibly empty.    */
DECL|method|assignTasks (TaskTracker taskTracker)
specifier|public
specifier|abstract
name|List
argument_list|<
name|Task
argument_list|>
name|assignTasks
parameter_list|(
name|TaskTracker
name|taskTracker
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a collection of jobs in an order which is specific to     * the particular scheduler.    * @param queueName    * @return    */
DECL|method|getJobs (String queueName)
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|JobInProgress
argument_list|>
name|getJobs
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Abstract QueueRefresher class. Scheduler's can extend this and return an    * instance of this in the {@link #getQueueRefresher()} method. The    * {@link #refreshQueues(List)} method of this instance will be invoked by the    * {@link QueueManager} whenever it gets a request from an administrator to    * refresh its own queue-configuration. This method has a documented contract    * between the {@link QueueManager} and the {@link TaskScheduler}.    *     * Before calling QueueRefresher, the caller must hold the lock to the    * corresponding {@link TaskScheduler} (generally in the {@link JobTracker}).    */
DECL|class|QueueRefresher
specifier|abstract
class|class
name|QueueRefresher
block|{
comment|/**      * Refresh the queue-configuration in the scheduler. This method has the      * following contract.      *<ol>      *<li>Before this method, {@link QueueManager} does a validation of the new      * queue-configuration. For e.g, currently addition of new queues, or      * removal of queues at any level in the hierarchy is not supported by      * {@link QueueManager} and so are not supported for schedulers too.</li>      *<li>Schedulers will be passed a list of {@link JobQueueInfo}s of the root      * queues i.e. the queues at the top level. All the descendants are properly      * linked from these top-level queues.</li>      *<li>Schedulers should use the scheduler specific queue properties from      * the newRootQueues, validate the properties themselves and apply them      * internally.</li>      *<li>      * Once the method returns successfully from the schedulers, it is assumed      * that the refresh of queue properties is successful throughout and will be      * 'committed' internally to {@link QueueManager} too. It is guaranteed that      * at no point, after successful return from the scheduler, is the queue      * refresh in QueueManager failed. If ever, such abnormalities happen, the      * queue framework will be inconsistent and will need a JT restart.</li>      *<li>If scheduler throws an exception during {@link #refreshQueues()},      * {@link QueueManager} throws away the newly read configuration, retains      * the old (consistent) configuration and informs the request issuer about      * the error appropriately.</li>      *</ol>      *       * @param newRootQueues      */
DECL|method|refreshQueues (List<JobQueueInfo> newRootQueues)
specifier|abstract
name|void
name|refreshQueues
parameter_list|(
name|List
argument_list|<
name|JobQueueInfo
argument_list|>
name|newRootQueues
parameter_list|)
throws|throws
name|Throwable
function_decl|;
block|}
comment|/**    * Get the {@link QueueRefresher} for this scheduler. By default, no    * {@link QueueRefresher} exists for a scheduler and is set to null.    * Schedulers need to return an instance of {@link QueueRefresher} if they    * wish to refresh their queue-configuration when {@link QueueManager}    * refreshes its own queue-configuration via an administrator request.    *     * @return    */
DECL|method|getQueueRefresher ()
name|QueueRefresher
name|getQueueRefresher
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

