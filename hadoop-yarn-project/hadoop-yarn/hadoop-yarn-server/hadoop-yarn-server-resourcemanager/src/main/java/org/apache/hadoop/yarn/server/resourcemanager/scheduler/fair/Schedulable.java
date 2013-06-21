begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Unstable
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Priority
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|resource
operator|.
name|ResourceWeights
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
name|yarn
operator|.
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_comment
comment|/**  * A Schedulable represents an entity that can launch tasks, such as a job  * or a queue. It provides a common interface so that algorithms such as fair  * sharing can be applied both within a queue and across queues. There are  * currently two types of Schedulables: JobSchedulables, which represent a  * single job, and QueueSchedulables, which allocate among jobs in their queue.  *  * Separate sets of Schedulables are used for maps and reduces. Each queue has  * both a mapSchedulable and a reduceSchedulable, and so does each job.  *  * A Schedulable is responsible for three roles:  * 1) It can launch tasks through assignTask().  * 2) It provides information about the job/queue to the scheduler, including:  *    - Demand (maximum number of tasks required)  *    - Number of currently running tasks  *    - Minimum share (for queues)  *    - Job/queue weight (for fair sharing)  *    - Start time and priority (for FIFO)  * 3) It can be assigned a fair share, for use with fair scheduling.  *  * Schedulable also contains two methods for performing scheduling computations:  * - updateDemand() is called periodically to compute the demand of the various  *   jobs and queues, which may be expensive (e.g. jobs must iterate through all  *   their tasks to count failed tasks, tasks that can be speculated, etc).  * - redistributeShare() is called after demands are updated and a Schedulable's  *   fair share has been set by its parent to let it distribute its share among  *   the other Schedulables within it (e.g. for queues that want to perform fair  *   sharing among their jobs).  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|Schedulable
specifier|public
specifier|abstract
class|class
name|Schedulable
block|{
comment|/** Fair share assigned to this Schedulable */
DECL|field|fairShare
specifier|private
name|Resource
name|fairShare
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Name of job/queue, used for debugging as well as for breaking ties in    * scheduling order deterministically.    */
DECL|method|getName ()
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Maximum number of resources required by this Schedulable. This is defined as    * number of currently utilized resources + number of unlaunched resources (that    * are either not yet launched or need to be speculated).    */
DECL|method|getDemand ()
specifier|public
specifier|abstract
name|Resource
name|getDemand
parameter_list|()
function_decl|;
comment|/** Get the aggregate amount of resources consumed by the schedulable. */
DECL|method|getResourceUsage ()
specifier|public
specifier|abstract
name|Resource
name|getResourceUsage
parameter_list|()
function_decl|;
comment|/** Minimum Resource share assigned to the schedulable. */
DECL|method|getMinShare ()
specifier|public
specifier|abstract
name|Resource
name|getMinShare
parameter_list|()
function_decl|;
comment|/** Job/queue weight in fair sharing. */
DECL|method|getWeights ()
specifier|public
specifier|abstract
name|ResourceWeights
name|getWeights
parameter_list|()
function_decl|;
comment|/** Start time for jobs in FIFO queues; meaningless for QueueSchedulables.*/
DECL|method|getStartTime ()
specifier|public
specifier|abstract
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/** Job priority for jobs in FIFO queues; meaningless for QueueSchedulables. */
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|Priority
name|getPriority
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
comment|/**    * Assign a container on this node if possible, and return the amount of    * resources assigned.    */
DECL|method|assignContainer (FSSchedulerNode node)
specifier|public
specifier|abstract
name|Resource
name|assignContainer
parameter_list|(
name|FSSchedulerNode
name|node
parameter_list|)
function_decl|;
comment|/** Assign a fair share to this Schedulable. */
DECL|method|setFairShare (Resource fairShare)
specifier|public
name|void
name|setFairShare
parameter_list|(
name|Resource
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
name|Resource
name|getFairShare
parameter_list|()
block|{
return|return
name|fairShare
return|;
block|}
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
literal|"[%s, demand=%s, running=%s, share=%s, w=%s]"
argument_list|,
name|getName
argument_list|()
argument_list|,
name|getDemand
argument_list|()
argument_list|,
name|getResourceUsage
argument_list|()
argument_list|,
name|fairShare
argument_list|,
name|getWeights
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

