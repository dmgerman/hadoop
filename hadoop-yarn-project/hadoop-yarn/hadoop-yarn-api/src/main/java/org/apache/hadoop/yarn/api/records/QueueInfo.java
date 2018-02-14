begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|InterfaceAudience
operator|.
name|Public
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
name|Stable
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
name|ApplicationClientProtocol
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
name|Records
import|;
end_import

begin_comment
comment|/**  * QueueInfo is a report of the runtime information of the queue.  *<p>  * It includes information such as:  *<ul>  *<li>Queue name.</li>  *<li>Capacity of the queue.</li>  *<li>Maximum capacity of the queue.</li>  *<li>Current capacity of the queue.</li>  *<li>Child queues.</li>  *<li>Running applications.</li>  *<li>{@link QueueState} of the queue.</li>  *<li>{@link QueueConfigurations} of the queue.</li>  *</ul>  *  * @see QueueState  * @see QueueConfigurations  * @see ApplicationClientProtocol#getQueueInfo(org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|QueueInfo
specifier|public
specifier|abstract
class|class
name|QueueInfo
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (String queueName, float capacity, float maximumCapacity, float currentCapacity, List<QueueInfo> childQueues, List<ApplicationReport> applications, QueueState queueState, Set<String> accessibleNodeLabels, String defaultNodeLabelExpression, QueueStatistics queueStatistics, boolean preemptionDisabled)
specifier|public
specifier|static
name|QueueInfo
name|newInstance
parameter_list|(
name|String
name|queueName
parameter_list|,
name|float
name|capacity
parameter_list|,
name|float
name|maximumCapacity
parameter_list|,
name|float
name|currentCapacity
parameter_list|,
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|childQueues
parameter_list|,
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applications
parameter_list|,
name|QueueState
name|queueState
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|accessibleNodeLabels
parameter_list|,
name|String
name|defaultNodeLabelExpression
parameter_list|,
name|QueueStatistics
name|queueStatistics
parameter_list|,
name|boolean
name|preemptionDisabled
parameter_list|)
block|{
name|QueueInfo
name|queueInfo
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|QueueInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|queueInfo
operator|.
name|setQueueName
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setCapacity
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setMaximumCapacity
argument_list|(
name|maximumCapacity
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setCurrentCapacity
argument_list|(
name|currentCapacity
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setChildQueues
argument_list|(
name|childQueues
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setApplications
argument_list|(
name|applications
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setQueueState
argument_list|(
name|queueState
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setAccessibleNodeLabels
argument_list|(
name|accessibleNodeLabels
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setDefaultNodeLabelExpression
argument_list|(
name|defaultNodeLabelExpression
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setQueueStatistics
argument_list|(
name|queueStatistics
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setPreemptionDisabled
argument_list|(
name|preemptionDisabled
argument_list|)
expr_stmt|;
return|return
name|queueInfo
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (String queueName, float capacity, float maximumCapacity, float currentCapacity, List<QueueInfo> childQueues, List<ApplicationReport> applications, QueueState queueState, Set<String> accessibleNodeLabels, String defaultNodeLabelExpression, QueueStatistics queueStatistics, boolean preemptionDisabled, Map<String, QueueConfigurations> queueConfigurations)
specifier|public
specifier|static
name|QueueInfo
name|newInstance
parameter_list|(
name|String
name|queueName
parameter_list|,
name|float
name|capacity
parameter_list|,
name|float
name|maximumCapacity
parameter_list|,
name|float
name|currentCapacity
parameter_list|,
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|childQueues
parameter_list|,
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applications
parameter_list|,
name|QueueState
name|queueState
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|accessibleNodeLabels
parameter_list|,
name|String
name|defaultNodeLabelExpression
parameter_list|,
name|QueueStatistics
name|queueStatistics
parameter_list|,
name|boolean
name|preemptionDisabled
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|QueueConfigurations
argument_list|>
name|queueConfigurations
parameter_list|)
block|{
name|QueueInfo
name|queueInfo
init|=
name|QueueInfo
operator|.
name|newInstance
argument_list|(
name|queueName
argument_list|,
name|capacity
argument_list|,
name|maximumCapacity
argument_list|,
name|currentCapacity
argument_list|,
name|childQueues
argument_list|,
name|applications
argument_list|,
name|queueState
argument_list|,
name|accessibleNodeLabels
argument_list|,
name|defaultNodeLabelExpression
argument_list|,
name|queueStatistics
argument_list|,
name|preemptionDisabled
argument_list|)
decl_stmt|;
name|queueInfo
operator|.
name|setQueueConfigurations
argument_list|(
name|queueConfigurations
argument_list|)
expr_stmt|;
return|return
name|queueInfo
return|;
block|}
comment|/**    * Get the<em>name</em> of the queue.    * @return<em>name</em> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueueName ()
specifier|public
specifier|abstract
name|String
name|getQueueName
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setQueueName (String queueName)
specifier|public
specifier|abstract
name|void
name|setQueueName
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Get the<em>configured capacity</em> of the queue.    * @return<em>configured capacity</em> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCapacity ()
specifier|public
specifier|abstract
name|float
name|getCapacity
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCapacity (float capacity)
specifier|public
specifier|abstract
name|void
name|setCapacity
parameter_list|(
name|float
name|capacity
parameter_list|)
function_decl|;
comment|/**    * Get the<em>maximum capacity</em> of the queue.    * @return<em>maximum capacity</em> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getMaximumCapacity ()
specifier|public
specifier|abstract
name|float
name|getMaximumCapacity
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setMaximumCapacity (float maximumCapacity)
specifier|public
specifier|abstract
name|void
name|setMaximumCapacity
parameter_list|(
name|float
name|maximumCapacity
parameter_list|)
function_decl|;
comment|/**    * Get the<em>current capacity</em> of the queue.    * @return<em>current capacity</em> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCurrentCapacity ()
specifier|public
specifier|abstract
name|float
name|getCurrentCapacity
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCurrentCapacity (float currentCapacity)
specifier|public
specifier|abstract
name|void
name|setCurrentCapacity
parameter_list|(
name|float
name|currentCapacity
parameter_list|)
function_decl|;
comment|/**    * Get the<em>child queues</em> of the queue.    * @return<em>child queues</em> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getChildQueues ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|getChildQueues
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setChildQueues (List<QueueInfo> childQueues)
specifier|public
specifier|abstract
name|void
name|setChildQueues
parameter_list|(
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|childQueues
parameter_list|)
function_decl|;
comment|/**    * Get the<em>running applications</em> of the queue.    * @return<em>running applications</em> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplications ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplications
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplications (List<ApplicationReport> applications)
specifier|public
specifier|abstract
name|void
name|setApplications
parameter_list|(
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applications
parameter_list|)
function_decl|;
comment|/**    * Get the<code>QueueState</code> of the queue.    * @return<code>QueueState</code> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueueState ()
specifier|public
specifier|abstract
name|QueueState
name|getQueueState
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setQueueState (QueueState queueState)
specifier|public
specifier|abstract
name|void
name|setQueueState
parameter_list|(
name|QueueState
name|queueState
parameter_list|)
function_decl|;
comment|/**    * Get the<code>accessible node labels</code> of the queue.    * @return<code>accessible node labels</code> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAccessibleNodeLabels ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getAccessibleNodeLabels
parameter_list|()
function_decl|;
comment|/**    * Set the<code>accessible node labels</code> of the queue.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAccessibleNodeLabels (Set<String> labels)
specifier|public
specifier|abstract
name|void
name|setAccessibleNodeLabels
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
function_decl|;
comment|/**    * Get the<code>default node label expression</code> of the queue, this takes    * affect only when the<code>ApplicationSubmissionContext</code> and    *<code>ResourceRequest</code> don't specify their    *<code>NodeLabelExpression</code>.    *     * @return<code>default node label expression</code> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getDefaultNodeLabelExpression ()
specifier|public
specifier|abstract
name|String
name|getDefaultNodeLabelExpression
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setDefaultNodeLabelExpression ( String defaultLabelExpression)
specifier|public
specifier|abstract
name|void
name|setDefaultNodeLabelExpression
parameter_list|(
name|String
name|defaultLabelExpression
parameter_list|)
function_decl|;
comment|/**    * Get the<code>queue stats</code> for the queue    *    * @return<code>queue stats</code> of the queue    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getQueueStatistics ()
specifier|public
specifier|abstract
name|QueueStatistics
name|getQueueStatistics
parameter_list|()
function_decl|;
comment|/**    * Set the queue statistics for the queue    *     * @param queueStatistics    *          the queue statistics    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setQueueStatistics (QueueStatistics queueStatistics)
specifier|public
specifier|abstract
name|void
name|setQueueStatistics
parameter_list|(
name|QueueStatistics
name|queueStatistics
parameter_list|)
function_decl|;
comment|/**    * Get the<em>preemption status</em> of the queue.    * @return if property is not in proto, return null;    *        otherwise, return<em>preemption status</em> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getPreemptionDisabled ()
specifier|public
specifier|abstract
name|Boolean
name|getPreemptionDisabled
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setPreemptionDisabled (boolean preemptionDisabled)
specifier|public
specifier|abstract
name|void
name|setPreemptionDisabled
parameter_list|(
name|boolean
name|preemptionDisabled
parameter_list|)
function_decl|;
comment|/**    * Get the per-node-label queue configurations of the queue.    *    * @return the per-node-label queue configurations of the queue.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueueConfigurations ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|QueueConfigurations
argument_list|>
name|getQueueConfigurations
parameter_list|()
function_decl|;
comment|/**    * Set the per-node-label queue configurations for the queue.    *    * @param queueConfigurations    *          the queue configurations    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setQueueConfigurations ( Map<String, QueueConfigurations> queueConfigurations)
specifier|public
specifier|abstract
name|void
name|setQueueConfigurations
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|QueueConfigurations
argument_list|>
name|queueConfigurations
parameter_list|)
function_decl|;
block|}
end_class

end_unit

