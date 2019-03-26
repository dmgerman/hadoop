begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.allocation
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
operator|.
name|allocation
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
name|scheduler
operator|.
name|fair
operator|.
name|ConfigurableResource
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
name|FSQueueType
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
name|SchedulingPolicy
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

begin_comment
comment|/**  * This class is a value class, storing queue properties parsed from the  * allocation.xml config file. Since there are a bunch of properties, properties  * should be added via QueueProperties.Builder.  */
end_comment

begin_class
DECL|class|QueueProperties
specifier|public
class|class
name|QueueProperties
block|{
comment|// Create some temporary hashmaps to hold the new allocs, and we only save
comment|// them in our fields if we have parsed the entire allocations file
comment|// successfully.
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
DECL|field|maxQueueResources
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigurableResource
argument_list|>
name|maxQueueResources
decl_stmt|;
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
DECL|field|queueMaxApps
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|queueMaxApps
decl_stmt|;
DECL|field|queueMaxAMShares
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queueMaxAMShares
decl_stmt|;
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
DECL|field|queuePolicies
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SchedulingPolicy
argument_list|>
name|queuePolicies
decl_stmt|;
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
DECL|field|reservationAcls
name|reservationAcls
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
DECL|field|nonPreemptableQueues
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nonPreemptableQueues
decl_stmt|;
DECL|field|configuredQueues
specifier|private
specifier|final
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
DECL|field|queueMaxContainerAllocation
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|queueMaxContainerAllocation
decl_stmt|;
DECL|method|QueueProperties (Builder builder)
name|QueueProperties
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|reservableQueues
operator|=
name|builder
operator|.
name|reservableQueues
expr_stmt|;
name|this
operator|.
name|minQueueResources
operator|=
name|builder
operator|.
name|minQueueResources
expr_stmt|;
name|this
operator|.
name|fairSharePreemptionTimeouts
operator|=
name|builder
operator|.
name|fairSharePreemptionTimeouts
expr_stmt|;
name|this
operator|.
name|queueWeights
operator|=
name|builder
operator|.
name|queueWeights
expr_stmt|;
name|this
operator|.
name|nonPreemptableQueues
operator|=
name|builder
operator|.
name|nonPreemptableQueues
expr_stmt|;
name|this
operator|.
name|configuredQueues
operator|=
name|builder
operator|.
name|configuredQueues
expr_stmt|;
name|this
operator|.
name|queueMaxAMShares
operator|=
name|builder
operator|.
name|queueMaxAMShares
expr_stmt|;
name|this
operator|.
name|queuePolicies
operator|=
name|builder
operator|.
name|queuePolicies
expr_stmt|;
name|this
operator|.
name|fairSharePreemptionThresholds
operator|=
name|builder
operator|.
name|fairSharePreemptionThresholds
expr_stmt|;
name|this
operator|.
name|queueMaxApps
operator|=
name|builder
operator|.
name|queueMaxApps
expr_stmt|;
name|this
operator|.
name|minSharePreemptionTimeouts
operator|=
name|builder
operator|.
name|minSharePreemptionTimeouts
expr_stmt|;
name|this
operator|.
name|maxQueueResources
operator|=
name|builder
operator|.
name|maxQueueResources
expr_stmt|;
name|this
operator|.
name|maxChildQueueResources
operator|=
name|builder
operator|.
name|maxChildQueueResources
expr_stmt|;
name|this
operator|.
name|reservationAcls
operator|=
name|builder
operator|.
name|reservationAcls
expr_stmt|;
name|this
operator|.
name|queueAcls
operator|=
name|builder
operator|.
name|queueAcls
expr_stmt|;
name|this
operator|.
name|queueMaxContainerAllocation
operator|=
name|builder
operator|.
name|queueMaxContainerAllocation
expr_stmt|;
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
DECL|method|getMinSharePreemptionTimeouts ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getMinSharePreemptionTimeouts
parameter_list|()
block|{
return|return
name|minSharePreemptionTimeouts
return|;
block|}
DECL|method|getFairSharePreemptionTimeouts ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getFairSharePreemptionTimeouts
parameter_list|()
block|{
return|return
name|fairSharePreemptionTimeouts
return|;
block|}
DECL|method|getFairSharePreemptionThresholds ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|getFairSharePreemptionThresholds
parameter_list|()
block|{
return|return
name|fairSharePreemptionThresholds
return|;
block|}
DECL|method|getMinQueueResources ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|getMinQueueResources
parameter_list|()
block|{
return|return
name|minQueueResources
return|;
block|}
DECL|method|getMaxQueueResources ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigurableResource
argument_list|>
name|getMaxQueueResources
parameter_list|()
block|{
return|return
name|maxQueueResources
return|;
block|}
DECL|method|getMaxChildQueueResources ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigurableResource
argument_list|>
name|getMaxChildQueueResources
parameter_list|()
block|{
return|return
name|maxChildQueueResources
return|;
block|}
DECL|method|getQueueMaxApps ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getQueueMaxApps
parameter_list|()
block|{
return|return
name|queueMaxApps
return|;
block|}
DECL|method|getQueueWeights ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|getQueueWeights
parameter_list|()
block|{
return|return
name|queueWeights
return|;
block|}
DECL|method|getQueueMaxAMShares ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|getQueueMaxAMShares
parameter_list|()
block|{
return|return
name|queueMaxAMShares
return|;
block|}
DECL|method|getQueuePolicies ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SchedulingPolicy
argument_list|>
name|getQueuePolicies
parameter_list|()
block|{
return|return
name|queuePolicies
return|;
block|}
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
name|queueAcls
return|;
block|}
specifier|public
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
DECL|method|getReservationAcls ()
name|getReservationAcls
parameter_list|()
block|{
return|return
name|reservationAcls
return|;
block|}
DECL|method|getReservableQueues ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getReservableQueues
parameter_list|()
block|{
return|return
name|reservableQueues
return|;
block|}
DECL|method|getNonPreemptableQueues ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getNonPreemptableQueues
parameter_list|()
block|{
return|return
name|nonPreemptableQueues
return|;
block|}
DECL|method|getMaxContainerAllocation ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|getMaxContainerAllocation
parameter_list|()
block|{
return|return
name|queueMaxContainerAllocation
return|;
block|}
comment|/**    * Builder class for {@link QueueProperties}.    * All methods are adding queue properties to the maps of this builder    * keyed by the queue's name except some methods    * like {@link #isAclDefinedForAccessType(String, AccessType)} or    * {@link #getMinQueueResources()}.    *    */
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|minQueueResources
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|minQueueResources
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|maxQueueResources
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigurableResource
argument_list|>
name|maxQueueResources
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|maxChildQueueResources
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigurableResource
argument_list|>
name|maxChildQueueResources
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queueMaxApps
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|queueMaxApps
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queueMaxAMShares
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queueMaxAMShares
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queueMaxContainerAllocation
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|queueMaxContainerAllocation
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queueWeights
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queueWeights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queuePolicies
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|SchedulingPolicy
argument_list|>
name|queuePolicies
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|minSharePreemptionTimeouts
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|minSharePreemptionTimeouts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|fairSharePreemptionTimeouts
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|fairSharePreemptionTimeouts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|fairSharePreemptionThresholds
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fairSharePreemptionThresholds
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queueAcls
specifier|private
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
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
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
DECL|field|reservationAcls
name|reservationAcls
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|reservableQueues
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|reservableQueues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|nonPreemptableQueues
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|nonPreemptableQueues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Remember all queue names so we can display them on web UI, etc.
comment|// configuredQueues is segregated based on whether it is a leaf queue
comment|// or a parent queue. This information is used for creating queues.
DECL|field|configuredQueues
specifier|private
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
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|Builder ()
name|Builder
parameter_list|()
block|{
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
block|}
DECL|method|create ()
specifier|public
specifier|static
name|Builder
name|create
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|method|minQueueResources (String queueName, Resource resource)
specifier|public
name|Builder
name|minQueueResources
parameter_list|(
name|String
name|queueName
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|minQueueResources
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|resource
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxQueueResources (String queueName, ConfigurableResource resource)
specifier|public
name|Builder
name|maxQueueResources
parameter_list|(
name|String
name|queueName
parameter_list|,
name|ConfigurableResource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|maxQueueResources
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|resource
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxChildQueueResources (String queueName, ConfigurableResource resource)
specifier|public
name|Builder
name|maxChildQueueResources
parameter_list|(
name|String
name|queueName
parameter_list|,
name|ConfigurableResource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|maxChildQueueResources
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|resource
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queueMaxApps (String queueName, int value)
specifier|public
name|Builder
name|queueMaxApps
parameter_list|(
name|String
name|queueName
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|queueMaxApps
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queueMaxAMShares (String queueName, float value)
specifier|public
name|Builder
name|queueMaxAMShares
parameter_list|(
name|String
name|queueName
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|this
operator|.
name|queueMaxAMShares
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queueWeights (String queueName, float value)
specifier|public
name|Builder
name|queueWeights
parameter_list|(
name|String
name|queueName
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|this
operator|.
name|queueWeights
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queuePolicies (String queueName, SchedulingPolicy policy)
specifier|public
name|Builder
name|queuePolicies
parameter_list|(
name|String
name|queueName
parameter_list|,
name|SchedulingPolicy
name|policy
parameter_list|)
block|{
name|this
operator|.
name|queuePolicies
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|policy
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|minSharePreemptionTimeouts (String queueName, long value)
specifier|public
name|Builder
name|minSharePreemptionTimeouts
parameter_list|(
name|String
name|queueName
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|minSharePreemptionTimeouts
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fairSharePreemptionTimeouts (String queueName, long value)
specifier|public
name|Builder
name|fairSharePreemptionTimeouts
parameter_list|(
name|String
name|queueName
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|fairSharePreemptionTimeouts
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fairSharePreemptionThresholds (String queueName, float value)
specifier|public
name|Builder
name|fairSharePreemptionThresholds
parameter_list|(
name|String
name|queueName
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|this
operator|.
name|fairSharePreemptionThresholds
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queueAcls (String queueName, AccessType accessType, AccessControlList acls)
specifier|public
name|Builder
name|queueAcls
parameter_list|(
name|String
name|queueName
parameter_list|,
name|AccessType
name|accessType
parameter_list|,
name|AccessControlList
name|acls
parameter_list|)
block|{
name|this
operator|.
name|queueAcls
operator|.
name|putIfAbsent
argument_list|(
name|queueName
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueAcls
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
operator|.
name|put
argument_list|(
name|accessType
argument_list|,
name|acls
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|reservationAcls (String queueName, ReservationACL reservationACL, AccessControlList acls)
specifier|public
name|Builder
name|reservationAcls
parameter_list|(
name|String
name|queueName
parameter_list|,
name|ReservationACL
name|reservationACL
parameter_list|,
name|AccessControlList
name|acls
parameter_list|)
block|{
name|this
operator|.
name|reservationAcls
operator|.
name|putIfAbsent
argument_list|(
name|queueName
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|reservationAcls
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
operator|.
name|put
argument_list|(
name|reservationACL
argument_list|,
name|acls
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|reservableQueues (String queue)
specifier|public
name|Builder
name|reservableQueues
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|reservableQueues
operator|.
name|add
argument_list|(
name|queue
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|nonPreemptableQueues (String queue)
specifier|public
name|Builder
name|nonPreemptableQueues
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|nonPreemptableQueues
operator|.
name|add
argument_list|(
name|queue
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queueMaxContainerAllocation (String queueName, Resource value)
specifier|public
name|Builder
name|queueMaxContainerAllocation
parameter_list|(
name|String
name|queueName
parameter_list|,
name|Resource
name|value
parameter_list|)
block|{
name|queueMaxContainerAllocation
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|configuredQueues (FSQueueType queueType, String queueName)
specifier|public
name|void
name|configuredQueues
parameter_list|(
name|FSQueueType
name|queueType
parameter_list|,
name|String
name|queueName
parameter_list|)
block|{
name|this
operator|.
name|configuredQueues
operator|.
name|get
argument_list|(
name|queueType
argument_list|)
operator|.
name|add
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
block|}
DECL|method|isAclDefinedForAccessType (String queueName, AccessType accessType)
specifier|public
name|boolean
name|isAclDefinedForAccessType
parameter_list|(
name|String
name|queueName
parameter_list|,
name|AccessType
name|accessType
parameter_list|)
block|{
name|Map
argument_list|<
name|AccessType
argument_list|,
name|AccessControlList
argument_list|>
name|aclsForQueue
init|=
name|this
operator|.
name|queueAcls
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
return|return
name|aclsForQueue
operator|!=
literal|null
operator|&&
name|aclsForQueue
operator|.
name|get
argument_list|(
name|accessType
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|getMinQueueResources ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|getMinQueueResources
parameter_list|()
block|{
return|return
name|minQueueResources
return|;
block|}
DECL|method|getMaxQueueResources ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigurableResource
argument_list|>
name|getMaxQueueResources
parameter_list|()
block|{
return|return
name|maxQueueResources
return|;
block|}
DECL|method|build ()
specifier|public
name|QueueProperties
name|build
parameter_list|()
block|{
return|return
operator|new
name|QueueProperties
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

