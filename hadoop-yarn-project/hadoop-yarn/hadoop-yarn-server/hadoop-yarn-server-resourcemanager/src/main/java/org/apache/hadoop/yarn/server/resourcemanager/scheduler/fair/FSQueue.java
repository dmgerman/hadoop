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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|security
operator|.
name|UserGroupInformation
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
name|ApplicationAttemptId
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
name|QueueACL
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
name|QueueInfo
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
name|QueueState
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|Queue
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FSQueue
specifier|public
specifier|abstract
class|class
name|FSQueue
implements|implements
name|Queue
implements|,
name|Schedulable
block|{
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
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|scheduler
specifier|protected
specifier|final
name|FairScheduler
name|scheduler
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|FSQueueMetrics
name|metrics
decl_stmt|;
DECL|field|parent
specifier|protected
specifier|final
name|FSParentQueue
name|parent
decl_stmt|;
DECL|field|recordFactory
specifier|protected
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|policy
specifier|protected
name|SchedulingPolicy
name|policy
init|=
name|SchedulingPolicy
operator|.
name|DEFAULT_POLICY
decl_stmt|;
DECL|method|FSQueue (String name, FairScheduler scheduler, FSParentQueue parent)
specifier|public
name|FSQueue
parameter_list|(
name|String
name|name
parameter_list|,
name|FairScheduler
name|scheduler
parameter_list|,
name|FSParentQueue
name|parent
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|FSQueueMetrics
operator|.
name|forQueue
argument_list|(
name|getName
argument_list|()
argument_list|,
name|parent
argument_list|,
literal|true
argument_list|,
name|scheduler
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setMinShare
argument_list|(
name|getMinShare
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setMaxShare
argument_list|(
name|getMaxShare
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getPolicy ()
specifier|public
name|SchedulingPolicy
name|getPolicy
parameter_list|()
block|{
return|return
name|policy
return|;
block|}
DECL|method|getParent ()
specifier|public
name|FSParentQueue
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|throwPolicyDoesnotApplyException (SchedulingPolicy policy)
specifier|protected
name|void
name|throwPolicyDoesnotApplyException
parameter_list|(
name|SchedulingPolicy
name|policy
parameter_list|)
throws|throws
name|AllocationConfigurationException
block|{
throw|throw
operator|new
name|AllocationConfigurationException
argument_list|(
literal|"SchedulingPolicy "
operator|+
name|policy
operator|+
literal|" does not apply to queue "
operator|+
name|getName
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|setPolicy (SchedulingPolicy policy)
specifier|public
specifier|abstract
name|void
name|setPolicy
parameter_list|(
name|SchedulingPolicy
name|policy
parameter_list|)
throws|throws
name|AllocationConfigurationException
function_decl|;
annotation|@
name|Override
DECL|method|getWeights ()
specifier|public
name|ResourceWeights
name|getWeights
parameter_list|()
block|{
return|return
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
operator|.
name|getQueueWeight
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMinShare ()
specifier|public
name|Resource
name|getMinShare
parameter_list|()
block|{
return|return
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
operator|.
name|getMinResources
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxShare ()
specifier|public
name|Resource
name|getMaxShare
parameter_list|()
block|{
return|return
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
operator|.
name|getMaxResources
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
name|Priority
name|p
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|p
operator|.
name|setPriority
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueInfo (boolean includeChildQueues, boolean recursive)
specifier|public
name|QueueInfo
name|getQueueInfo
parameter_list|(
name|boolean
name|includeChildQueues
parameter_list|,
name|boolean
name|recursive
parameter_list|)
block|{
name|QueueInfo
name|queueInfo
init|=
name|recordFactory
operator|.
name|newRecordInstance
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
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: we might change these queue metrics around a little bit
comment|// to match the semantics of the fair scheduler.
name|queueInfo
operator|.
name|setCapacity
argument_list|(
operator|(
name|float
operator|)
name|getFairShare
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|/
name|scheduler
operator|.
name|getClusterResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setCapacity
argument_list|(
operator|(
name|float
operator|)
name|getResourceUsage
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|/
name|scheduler
operator|.
name|getClusterResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|QueueInfo
argument_list|>
name|childQueueInfos
init|=
operator|new
name|ArrayList
argument_list|<
name|QueueInfo
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|includeChildQueues
condition|)
block|{
name|Collection
argument_list|<
name|FSQueue
argument_list|>
name|childQueues
init|=
name|getChildQueues
argument_list|()
decl_stmt|;
for|for
control|(
name|FSQueue
name|child
range|:
name|childQueues
control|)
block|{
name|childQueueInfos
operator|.
name|add
argument_list|(
name|child
operator|.
name|getQueueInfo
argument_list|(
name|recursive
argument_list|,
name|recursive
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|queueInfo
operator|.
name|setChildQueues
argument_list|(
name|childQueueInfos
argument_list|)
expr_stmt|;
name|queueInfo
operator|.
name|setQueueState
argument_list|(
name|QueueState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
return|return
name|queueInfo
return|;
block|}
annotation|@
name|Override
DECL|method|getMetrics ()
specifier|public
name|FSQueueMetrics
name|getMetrics
parameter_list|()
block|{
return|return
name|metrics
return|;
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
annotation|@
name|Override
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
name|metrics
operator|.
name|setFairShare
argument_list|(
name|fairShare
argument_list|)
expr_stmt|;
block|}
DECL|method|hasAccess (QueueACL acl, UserGroupInformation user)
specifier|public
name|boolean
name|hasAccess
parameter_list|(
name|QueueACL
name|acl
parameter_list|,
name|UserGroupInformation
name|user
parameter_list|)
block|{
return|return
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
operator|.
name|hasAccess
argument_list|(
name|name
argument_list|,
name|acl
argument_list|,
name|user
argument_list|)
return|;
block|}
comment|/**    * Recomputes the shares for all child queues and applications based on this    * queue's current share    */
DECL|method|recomputeShares ()
specifier|public
specifier|abstract
name|void
name|recomputeShares
parameter_list|()
function_decl|;
comment|/**    * Gets the children of this queue, if any.    */
DECL|method|getChildQueues ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|FSQueue
argument_list|>
name|getChildQueues
parameter_list|()
function_decl|;
comment|/**    * Adds all applications in the queue and its subqueues to the given collection.    * @param apps the collection to add the applications to    */
DECL|method|collectSchedulerApplications ( Collection<ApplicationAttemptId> apps)
specifier|public
specifier|abstract
name|void
name|collectSchedulerApplications
parameter_list|(
name|Collection
argument_list|<
name|ApplicationAttemptId
argument_list|>
name|apps
parameter_list|)
function_decl|;
comment|/**    * Return the number of apps for which containers can be allocated.    * Includes apps in subqueues.    */
DECL|method|getNumRunnableApps ()
specifier|public
specifier|abstract
name|int
name|getNumRunnableApps
parameter_list|()
function_decl|;
comment|/**    * Helper method to check if the queue should attempt assigning resources    *     * @return true if check passes (can assign) or false otherwise    */
DECL|method|assignContainerPreCheck (FSSchedulerNode node)
specifier|protected
name|boolean
name|assignContainerPreCheck
parameter_list|(
name|FSSchedulerNode
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Resources
operator|.
name|fitsIn
argument_list|(
name|getResourceUsage
argument_list|()
argument_list|,
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
operator|.
name|getMaxResources
argument_list|(
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|||
name|node
operator|.
name|getReservedContainer
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|isActive ()
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|getNumRunnableApps
argument_list|()
operator|>
literal|0
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

