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

begin_comment
comment|/**  * A Schedulable represents an entity that can be scheduled such as an  * application or a queue. It provides a common interface so that algorithms  * such as fair sharing can be applied both within a queue and across queues.  *  * A Schedulable is responsible for three roles:  * 1) Assign resources through {@link #assignContainer}.  * 2) It provides information about the app/queue to the scheduler, including:  *    - Demand (maximum number of tasks required)  *    - Minimum share (for queues)  *    - Job/queue weight (for fair sharing)  *    - Start time and priority (for FIFO)  * 3) It can be assigned a fair share, for use with fair scheduling.  *  * Schedulable also contains two methods for performing scheduling computations:  * - updateDemand() is called periodically to compute the demand of the various  *   jobs and queues, which may be expensive (e.g. jobs must iterate through all  *   their tasks to count failed tasks, tasks that can be speculated, etc).  * - redistributeShare() is called after demands are updated and a Schedulable's  *   fair share has been set by its parent to let it distribute its share among  *   the other Schedulables within it (e.g. for queues that want to perform fair  *   sharing among their jobs).  */
end_comment

begin_interface
annotation|@
name|Private
annotation|@
name|Unstable
DECL|interface|Schedulable
specifier|public
interface|interface
name|Schedulable
block|{
comment|/**    * Name of job/queue, used for debugging as well as for breaking ties in    * scheduling order deterministically.    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Maximum number of resources required by this Schedulable. This is defined as    * number of currently utilized resources + number of unlaunched resources (that    * are either not yet launched or need to be speculated).    */
DECL|method|getDemand ()
name|Resource
name|getDemand
parameter_list|()
function_decl|;
comment|/** Get the aggregate amount of resources consumed by the schedulable. */
DECL|method|getResourceUsage ()
name|Resource
name|getResourceUsage
parameter_list|()
function_decl|;
comment|/** Minimum Resource share assigned to the schedulable. */
DECL|method|getMinShare ()
name|Resource
name|getMinShare
parameter_list|()
function_decl|;
comment|/** Maximum Resource share assigned to the schedulable. */
DECL|method|getMaxShare ()
name|Resource
name|getMaxShare
parameter_list|()
function_decl|;
comment|/**    * Job/queue weight in fair sharing. Weights are only meaningful when    * compared. A weight of 2.0f has twice the weight of a weight of 1.0f,    * which has twice the weight of a weight of 0.5f. A weight of 1.0f is    * considered unweighted or a neutral weight. A weight of 0 is no weight.    *    * @return the weight    */
DECL|method|getWeight ()
name|float
name|getWeight
parameter_list|()
function_decl|;
comment|/** Start time for jobs in FIFO queues; meaningless for QueueSchedulables.*/
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/** Job priority for jobs in FIFO queues; meaningless for QueueSchedulables. */
DECL|method|getPriority ()
name|Priority
name|getPriority
parameter_list|()
function_decl|;
comment|/** Refresh the Schedulable's demand and those of its children if any. */
DECL|method|updateDemand ()
name|void
name|updateDemand
parameter_list|()
function_decl|;
comment|/**    * Assign a container on this node if possible, and return the amount of    * resources assigned.    */
DECL|method|assignContainer (FSSchedulerNode node)
name|Resource
name|assignContainer
parameter_list|(
name|FSSchedulerNode
name|node
parameter_list|)
function_decl|;
comment|/** Get the fair share assigned to this Schedulable. */
DECL|method|getFairShare ()
name|Resource
name|getFairShare
parameter_list|()
function_decl|;
comment|/** Assign a fair share to this Schedulable. */
DECL|method|setFairShare (Resource fairShare)
name|void
name|setFairShare
parameter_list|(
name|Resource
name|fairShare
parameter_list|)
function_decl|;
comment|/**    * Check whether the schedulable is preemptable.    * @return<code>true</code> if the schedulable is preemptable;    *<code>false</code> otherwise    */
DECL|method|isPreemptable ()
name|boolean
name|isPreemptable
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

