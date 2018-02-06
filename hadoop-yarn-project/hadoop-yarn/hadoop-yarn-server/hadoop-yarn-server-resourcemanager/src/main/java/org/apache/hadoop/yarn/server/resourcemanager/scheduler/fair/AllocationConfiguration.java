begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|Collections
import|;
end_import

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
name|HashSet
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|ReservationACL
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
name|security
operator|.
name|AccessType
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
name|reservation
operator|.
name|ReservationSchedulerConfiguration
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
name|SchedulerUtils
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
name|fair
operator|.
name|allocation
operator|.
name|AllocationFileParser
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
name|fair
operator|.
name|allocation
operator|.
name|QueueProperties
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_class
DECL|class|AllocationConfiguration
specifier|public
class|class
name|AllocationConfiguration
extends|extends
name|ReservationSchedulerConfiguration
block|{
DECL|field|EVERYBODY_ACL
specifier|private
specifier|static
specifier|final
name|AccessControlList
name|EVERYBODY_ACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
DECL|field|NOBODY_ACL
specifier|private
specifier|static
specifier|final
name|AccessControlList
name|NOBODY_ACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
comment|// Minimum resource allocation for each queue
DECL|field|minQueueResources
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|minQueueResources
decl_stmt|;
comment|// Maximum amount of resources per queue
annotation|@
name|VisibleForTesting
DECL|field|maxQueueResources
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigurableResource
argument_list|>
name|maxQueueResources
decl_stmt|;
comment|// Maximum amount of resources for each queue's ad hoc children
DECL|field|maxChildQueueResources
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigurableResource
argument_list|>
name|maxChildQueueResources
decl_stmt|;
comment|// Sharing weights for each queue
DECL|field|queueWeights
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queueWeights
decl_stmt|;
comment|// Max concurrent running applications for each queue and for each user; in addition,
comment|// for users that have no max specified, we use the userMaxJobsDefault.
annotation|@
name|VisibleForTesting
DECL|field|queueMaxApps
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|queueMaxApps
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|userMaxApps
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|userMaxApps
decl_stmt|;
DECL|field|userMaxAppsDefault
specifier|private
specifier|final
name|int
name|userMaxAppsDefault
decl_stmt|;
DECL|field|queueMaxAppsDefault
specifier|private
specifier|final
name|int
name|queueMaxAppsDefault
decl_stmt|;
DECL|field|queueMaxResourcesDefault
specifier|private
specifier|final
name|ConfigurableResource
name|queueMaxResourcesDefault
decl_stmt|;
comment|// Maximum resource share for each leaf queue that can be used to run AMs
DECL|field|queueMaxAMShares
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queueMaxAMShares
decl_stmt|;
DECL|field|queueMaxAMShareDefault
specifier|private
specifier|final
name|float
name|queueMaxAMShareDefault
decl_stmt|;
comment|// ACL's for each queue. Only specifies non-default ACL's from configuration.
DECL|field|queueAcls
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|AccessType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|>
name|queueAcls
decl_stmt|;
comment|// Reservation ACL's for each queue. Only specifies non-default ACL's from
comment|// configuration.
DECL|field|resAcls
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ReservationACL
argument_list|,
name|AccessControlList
argument_list|>
argument_list|>
name|resAcls
decl_stmt|;
comment|// Min share preemption timeout for each queue in seconds. If a job in the queue
comment|// waits this long without receiving its guaranteed share, it is allowed to
comment|// preempt other jobs' tasks.
DECL|field|minSharePreemptionTimeouts
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|minSharePreemptionTimeouts
decl_stmt|;
comment|// Fair share preemption timeout for each queue in seconds. If a job in the
comment|// queue waits this long without receiving its fair share threshold, it is
comment|// allowed to preempt other jobs' tasks.
DECL|field|fairSharePreemptionTimeouts
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|fairSharePreemptionTimeouts
decl_stmt|;
comment|// The fair share preemption threshold for each queue. If a queue waits
comment|// fairSharePreemptionTimeout without receiving
comment|// fairshare * fairSharePreemptionThreshold resources, it is allowed to
comment|// preempt other queues' tasks.
DECL|field|fairSharePreemptionThresholds
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fairSharePreemptionThresholds
decl_stmt|;
DECL|field|reservableQueues
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|reservableQueues
decl_stmt|;
DECL|field|schedulingPolicies
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SchedulingPolicy
argument_list|>
name|schedulingPolicies
decl_stmt|;
DECL|field|defaultSchedulingPolicy
specifier|private
specifier|final
name|SchedulingPolicy
name|defaultSchedulingPolicy
decl_stmt|;
comment|// Policy for mapping apps to queues
annotation|@
name|VisibleForTesting
DECL|field|placementPolicy
name|QueuePlacementPolicy
name|placementPolicy
decl_stmt|;
comment|//Configured queues in the alloc xml
annotation|@
name|VisibleForTesting
DECL|field|configuredQueues
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
decl_stmt|;
comment|// Reservation system configuration
DECL|field|globalReservationQueueConfig
specifier|private
name|ReservationQueueConfiguration
name|globalReservationQueueConfig
decl_stmt|;
DECL|field|nonPreemptableQueues
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nonPreemptableQueues
decl_stmt|;
DECL|method|AllocationConfiguration (QueueProperties queueProperties, AllocationFileParser allocationFileParser, QueuePlacementPolicy newPlacementPolicy, ReservationQueueConfiguration globalReservationQueueConfig)
specifier|public
name|AllocationConfiguration
parameter_list|(
name|QueueProperties
name|queueProperties
parameter_list|,
name|AllocationFileParser
name|allocationFileParser
parameter_list|,
name|QueuePlacementPolicy
name|newPlacementPolicy
parameter_list|,
name|ReservationQueueConfiguration
name|globalReservationQueueConfig
parameter_list|)
throws|throws
name|AllocationConfigurationException
block|{
name|this
operator|.
name|minQueueResources
operator|=
name|queueProperties
operator|.
name|getMinQueueResources
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxQueueResources
operator|=
name|queueProperties
operator|.
name|getMaxQueueResources
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxChildQueueResources
operator|=
name|queueProperties
operator|.
name|getMaxChildQueueResources
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueMaxApps
operator|=
name|queueProperties
operator|.
name|getQueueMaxApps
argument_list|()
expr_stmt|;
name|this
operator|.
name|userMaxApps
operator|=
name|allocationFileParser
operator|.
name|getUserMaxApps
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueMaxAMShares
operator|=
name|queueProperties
operator|.
name|getQueueMaxAMShares
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueWeights
operator|=
name|queueProperties
operator|.
name|getQueueWeights
argument_list|()
expr_stmt|;
name|this
operator|.
name|userMaxAppsDefault
operator|=
name|allocationFileParser
operator|.
name|getUserMaxAppsDefault
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueMaxResourcesDefault
operator|=
name|allocationFileParser
operator|.
name|getQueueMaxResourcesDefault
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueMaxAppsDefault
operator|=
name|allocationFileParser
operator|.
name|getQueueMaxAppsDefault
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueMaxAMShareDefault
operator|=
name|allocationFileParser
operator|.
name|getQueueMaxAMShareDefault
argument_list|()
expr_stmt|;
name|this
operator|.
name|defaultSchedulingPolicy
operator|=
name|allocationFileParser
operator|.
name|getDefaultSchedulingPolicy
argument_list|()
expr_stmt|;
name|this
operator|.
name|schedulingPolicies
operator|=
name|queueProperties
operator|.
name|getQueuePolicies
argument_list|()
expr_stmt|;
name|this
operator|.
name|minSharePreemptionTimeouts
operator|=
name|queueProperties
operator|.
name|getMinSharePreemptionTimeouts
argument_list|()
expr_stmt|;
name|this
operator|.
name|fairSharePreemptionTimeouts
operator|=
name|queueProperties
operator|.
name|getFairSharePreemptionTimeouts
argument_list|()
expr_stmt|;
name|this
operator|.
name|fairSharePreemptionThresholds
operator|=
name|queueProperties
operator|.
name|getFairSharePreemptionThresholds
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueAcls
operator|=
name|queueProperties
operator|.
name|getQueueAcls
argument_list|()
expr_stmt|;
name|this
operator|.
name|resAcls
operator|=
name|queueProperties
operator|.
name|getReservationAcls
argument_list|()
expr_stmt|;
name|this
operator|.
name|reservableQueues
operator|=
name|queueProperties
operator|.
name|getReservableQueues
argument_list|()
expr_stmt|;
name|this
operator|.
name|globalReservationQueueConfig
operator|=
name|globalReservationQueueConfig
expr_stmt|;
name|this
operator|.
name|placementPolicy
operator|=
name|newPlacementPolicy
expr_stmt|;
name|this
operator|.
name|configuredQueues
operator|=
name|queueProperties
operator|.
name|getConfiguredQueues
argument_list|()
expr_stmt|;
name|this
operator|.
name|nonPreemptableQueues
operator|=
name|queueProperties
operator|.
name|getNonPreemptableQueues
argument_list|()
expr_stmt|;
block|}
DECL|method|AllocationConfiguration (Configuration conf)
specifier|public
name|AllocationConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|minQueueResources
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|maxChildQueueResources
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|maxQueueResources
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|queueWeights
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|queueMaxApps
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|userMaxApps
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|queueMaxAMShares
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|userMaxAppsDefault
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|queueMaxAppsDefault
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|queueMaxResourcesDefault
operator|=
operator|new
name|ConfigurableResource
argument_list|(
name|Resources
operator|.
name|unbounded
argument_list|()
argument_list|)
expr_stmt|;
name|queueMaxAMShareDefault
operator|=
literal|0.5f
expr_stmt|;
name|queueAcls
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|resAcls
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|minSharePreemptionTimeouts
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|fairSharePreemptionTimeouts
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|fairSharePreemptionThresholds
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|schedulingPolicies
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|defaultSchedulingPolicy
operator|=
name|SchedulingPolicy
operator|.
name|DEFAULT_POLICY
expr_stmt|;
name|reservableQueues
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|configuredQueues
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|FSQueueType
name|queueType
range|:
name|FSQueueType
operator|.
name|values
argument_list|()
control|)
block|{
name|configuredQueues
operator|.
name|put
argument_list|(
name|queueType
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|placementPolicy
operator|=
name|QueuePlacementPolicy
operator|.
name|fromConfiguration
argument_list|(
name|conf
argument_list|,
name|configuredQueues
argument_list|)
expr_stmt|;
name|nonPreemptableQueues
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the ACLs associated with this queue. If a given ACL is not explicitly    * configured, include the default value for that ACL.  The default for the    * root queue is everybody ("*") and the default for all other queues is    * nobody ("")    */
DECL|method|getQueueAcl (String queue, QueueACL operation)
specifier|public
name|AccessControlList
name|getQueueAcl
parameter_list|(
name|String
name|queue
parameter_list|,
name|QueueACL
name|operation
parameter_list|)
block|{
name|Map
argument_list|<
name|AccessType
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
name|this
operator|.
name|queueAcls
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
if|if
condition|(
name|acls
operator|!=
literal|null
condition|)
block|{
name|AccessControlList
name|operationAcl
init|=
name|acls
operator|.
name|get
argument_list|(
name|SchedulerUtils
operator|.
name|toAccessType
argument_list|(
name|operation
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|operationAcl
operator|!=
literal|null
condition|)
block|{
return|return
name|operationAcl
return|;
block|}
block|}
return|return
operator|(
name|queue
operator|.
name|equals
argument_list|(
literal|"root"
argument_list|)
operator|)
condition|?
name|EVERYBODY_ACL
else|:
name|NOBODY_ACL
return|;
block|}
comment|/**    * Get the map of ACLs of all queues.    * @return the map of ACLs of all queues    */
DECL|method|getQueueAcls ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|AccessType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|>
name|getQueueAcls
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|this
operator|.
name|queueAcls
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|/**    * Get the map of reservation ACLs to {@link AccessControlList} for the    * specified queue.    */
DECL|method|getReservationAcls (String queue)
specifier|public
name|Map
argument_list|<
name|ReservationACL
argument_list|,
name|AccessControlList
argument_list|>
name|getReservationAcls
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|this
operator|.
name|resAcls
operator|.
name|get
argument_list|(
name|queue
argument_list|)
return|;
block|}
comment|/**    * Get a queue's min share preemption timeout configured in the allocation    * file, in milliseconds. Return -1 if not set.    */
DECL|method|getMinSharePreemptionTimeout (String queueName)
specifier|public
name|long
name|getMinSharePreemptionTimeout
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|Long
name|minSharePreemptionTimeout
init|=
name|minSharePreemptionTimeouts
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
return|return
operator|(
name|minSharePreemptionTimeout
operator|==
literal|null
operator|)
condition|?
operator|-
literal|1
else|:
name|minSharePreemptionTimeout
return|;
block|}
comment|/**    * Get a queue's fair share preemption timeout configured in the allocation    * file, in milliseconds. Return -1 if not set.    */
DECL|method|getFairSharePreemptionTimeout (String queueName)
specifier|public
name|long
name|getFairSharePreemptionTimeout
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|Long
name|fairSharePreemptionTimeout
init|=
name|fairSharePreemptionTimeouts
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
return|return
operator|(
name|fairSharePreemptionTimeout
operator|==
literal|null
operator|)
condition|?
operator|-
literal|1
else|:
name|fairSharePreemptionTimeout
return|;
block|}
comment|/**    * Get a queue's fair share preemption threshold in the allocation file.    * Return -1f if not set.    */
DECL|method|getFairSharePreemptionThreshold (String queueName)
specifier|public
name|float
name|getFairSharePreemptionThreshold
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|Float
name|fairSharePreemptionThreshold
init|=
name|fairSharePreemptionThresholds
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
return|return
operator|(
name|fairSharePreemptionThreshold
operator|==
literal|null
operator|)
condition|?
operator|-
literal|1f
else|:
name|fairSharePreemptionThreshold
return|;
block|}
DECL|method|isPreemptable (String queueName)
specifier|public
name|boolean
name|isPreemptable
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
return|return
operator|!
name|nonPreemptableQueues
operator|.
name|contains
argument_list|(
name|queueName
argument_list|)
return|;
block|}
DECL|method|getQueueWeight (String queue)
specifier|private
name|float
name|getQueueWeight
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|Float
name|weight
init|=
name|queueWeights
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
return|return
operator|(
name|weight
operator|==
literal|null
operator|)
condition|?
literal|1.0f
else|:
name|weight
return|;
block|}
DECL|method|getUserMaxApps (String user)
specifier|public
name|int
name|getUserMaxApps
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|Integer
name|maxApps
init|=
name|userMaxApps
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
return|return
operator|(
name|maxApps
operator|==
literal|null
operator|)
condition|?
name|userMaxAppsDefault
else|:
name|maxApps
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getQueueMaxApps (String queue)
name|int
name|getQueueMaxApps
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|Integer
name|maxApps
init|=
name|queueMaxApps
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
return|return
operator|(
name|maxApps
operator|==
literal|null
operator|)
condition|?
name|queueMaxAppsDefault
else|:
name|maxApps
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getQueueMaxAMShare (String queue)
name|float
name|getQueueMaxAMShare
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|Float
name|maxAMShare
init|=
name|queueMaxAMShares
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
return|return
operator|(
name|maxAMShare
operator|==
literal|null
operator|)
condition|?
name|queueMaxAMShareDefault
else|:
name|maxAMShare
return|;
block|}
comment|/**    * Get the minimum resource allocation for the given queue.    *    * @param queue the target queue's name    * @return the min allocation on this queue or {@link Resources#none}    * if not set    */
annotation|@
name|VisibleForTesting
DECL|method|getMinResources (String queue)
name|Resource
name|getMinResources
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|Resource
name|minQueueResource
init|=
name|minQueueResources
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
return|return
operator|(
name|minQueueResource
operator|==
literal|null
operator|)
condition|?
name|Resources
operator|.
name|none
argument_list|()
else|:
name|minQueueResource
return|;
block|}
comment|/**    * Get the maximum resource allocation for the given queue. If the max in not    * set, return the default max.    *    * @param queue the target queue's name    * @return the max allocation on this queue    */
annotation|@
name|VisibleForTesting
DECL|method|getMaxResources (String queue)
name|ConfigurableResource
name|getMaxResources
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|ConfigurableResource
name|maxQueueResource
init|=
name|maxQueueResources
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxQueueResource
operator|==
literal|null
condition|)
block|{
name|maxQueueResource
operator|=
name|queueMaxResourcesDefault
expr_stmt|;
block|}
return|return
name|maxQueueResource
return|;
block|}
comment|/**    * Get the maximum resource allocation for children of the given queue.    *    * @param queue the target queue's name    * @return the max allocation on this queue or null if not set    */
annotation|@
name|VisibleForTesting
DECL|method|getMaxChildResources (String queue)
name|ConfigurableResource
name|getMaxChildResources
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|maxChildQueueResources
operator|.
name|get
argument_list|(
name|queue
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getSchedulingPolicy (String queueName)
name|SchedulingPolicy
name|getSchedulingPolicy
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|SchedulingPolicy
name|policy
init|=
name|schedulingPolicies
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
return|return
operator|(
name|policy
operator|==
literal|null
operator|)
condition|?
name|defaultSchedulingPolicy
else|:
name|policy
return|;
block|}
DECL|method|getDefaultSchedulingPolicy ()
specifier|public
name|SchedulingPolicy
name|getDefaultSchedulingPolicy
parameter_list|()
block|{
return|return
name|defaultSchedulingPolicy
return|;
block|}
DECL|method|getConfiguredQueues ()
specifier|public
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|getConfiguredQueues
parameter_list|()
block|{
return|return
name|configuredQueues
return|;
block|}
DECL|method|getPlacementPolicy ()
specifier|public
name|QueuePlacementPolicy
name|getPlacementPolicy
parameter_list|()
block|{
return|return
name|placementPolicy
return|;
block|}
annotation|@
name|Override
DECL|method|isReservable (String queue)
specifier|public
name|boolean
name|isReservable
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|reservableQueues
operator|.
name|contains
argument_list|(
name|queue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getReservationWindow (String queue)
specifier|public
name|long
name|getReservationWindow
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|globalReservationQueueConfig
operator|.
name|getReservationWindowMsec
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAverageCapacity (String queue)
specifier|public
name|float
name|getAverageCapacity
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|globalReservationQueueConfig
operator|.
name|getAvgOverTimeMultiplier
argument_list|()
operator|*
literal|100
return|;
block|}
annotation|@
name|Override
DECL|method|getInstantaneousMaxCapacity (String queue)
specifier|public
name|float
name|getInstantaneousMaxCapacity
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|globalReservationQueueConfig
operator|.
name|getMaxOverTimeMultiplier
argument_list|()
operator|*
literal|100
return|;
block|}
annotation|@
name|Override
DECL|method|getReservationAdmissionPolicy (String queue)
specifier|public
name|String
name|getReservationAdmissionPolicy
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|globalReservationQueueConfig
operator|.
name|getReservationAdmissionPolicy
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReservationAgent (String queue)
specifier|public
name|String
name|getReservationAgent
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|globalReservationQueueConfig
operator|.
name|getReservationAgent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getShowReservationAsQueues (String queue)
specifier|public
name|boolean
name|getShowReservationAsQueues
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|globalReservationQueueConfig
operator|.
name|shouldShowReservationAsQueues
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReplanner (String queue)
specifier|public
name|String
name|getReplanner
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|globalReservationQueueConfig
operator|.
name|getPlanner
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMoveOnExpiry (String queue)
specifier|public
name|boolean
name|getMoveOnExpiry
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|globalReservationQueueConfig
operator|.
name|shouldMoveOnExpiry
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getEnforcementWindow (String queue)
specifier|public
name|long
name|getEnforcementWindow
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|globalReservationQueueConfig
operator|.
name|getEnforcementWindowMsec
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setReservationWindow (long window)
specifier|public
name|void
name|setReservationWindow
parameter_list|(
name|long
name|window
parameter_list|)
block|{
name|globalReservationQueueConfig
operator|.
name|setReservationWindow
argument_list|(
name|window
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setAverageCapacity (int avgCapacity)
specifier|public
name|void
name|setAverageCapacity
parameter_list|(
name|int
name|avgCapacity
parameter_list|)
block|{
name|globalReservationQueueConfig
operator|.
name|setAverageCapacity
argument_list|(
name|avgCapacity
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize a {@link FSQueue} with queue-specific properties and its    * metrics.    * @param queue the FSQueue needed to be initialized    */
DECL|method|initFSQueue (FSQueue queue)
specifier|public
name|void
name|initFSQueue
parameter_list|(
name|FSQueue
name|queue
parameter_list|)
block|{
comment|// Set queue-specific properties.
name|String
name|name
init|=
name|queue
operator|.
name|getName
argument_list|()
decl_stmt|;
name|queue
operator|.
name|setWeights
argument_list|(
name|getQueueWeight
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setMinShare
argument_list|(
name|getMinResources
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setMaxShare
argument_list|(
name|getMaxResources
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setMaxRunningApps
argument_list|(
name|getQueueMaxApps
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setMaxAMShare
argument_list|(
name|getQueueMaxAMShare
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setMaxChildQueueResource
argument_list|(
name|getMaxChildResources
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set queue metrics.
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|setMinShare
argument_list|(
name|queue
operator|.
name|getMinShare
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|setMaxShare
argument_list|(
name|queue
operator|.
name|getMaxShare
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|setMaxApps
argument_list|(
name|queue
operator|.
name|getMaxRunningApps
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|setSchedulingPolicy
argument_list|(
name|getSchedulingPolicy
argument_list|(
name|name
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

