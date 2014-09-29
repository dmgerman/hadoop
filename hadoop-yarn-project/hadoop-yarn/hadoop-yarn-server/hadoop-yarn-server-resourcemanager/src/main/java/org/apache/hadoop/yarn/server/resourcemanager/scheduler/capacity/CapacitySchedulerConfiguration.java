begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|capacity
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|util
operator|.
name|ReflectionUtils
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
name|util
operator|.
name|StringUtils
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
name|conf
operator|.
name|YarnConfiguration
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
name|DefaultResourceCalculator
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
name|ResourceCalculator
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
DECL|class|CapacitySchedulerConfiguration
specifier|public
class|class
name|CapacitySchedulerConfiguration
extends|extends
name|Configuration
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CS_CONFIGURATION_FILE
specifier|private
specifier|static
specifier|final
name|String
name|CS_CONFIGURATION_FILE
init|=
literal|"capacity-scheduler.xml"
decl_stmt|;
annotation|@
name|Private
DECL|field|PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"yarn.scheduler.capacity."
decl_stmt|;
annotation|@
name|Private
DECL|field|DOT
specifier|public
specifier|static
specifier|final
name|String
name|DOT
init|=
literal|"."
decl_stmt|;
annotation|@
name|Private
DECL|field|MAXIMUM_APPLICATIONS_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|MAXIMUM_APPLICATIONS_SUFFIX
init|=
literal|"maximum-applications"
decl_stmt|;
annotation|@
name|Private
DECL|field|MAXIMUM_SYSTEM_APPLICATIONS
specifier|public
specifier|static
specifier|final
name|String
name|MAXIMUM_SYSTEM_APPLICATIONS
init|=
name|PREFIX
operator|+
name|MAXIMUM_APPLICATIONS_SUFFIX
decl_stmt|;
annotation|@
name|Private
DECL|field|MAXIMUM_AM_RESOURCE_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|MAXIMUM_AM_RESOURCE_SUFFIX
init|=
literal|"maximum-am-resource-percent"
decl_stmt|;
annotation|@
name|Private
DECL|field|MAXIMUM_APPLICATION_MASTERS_RESOURCE_PERCENT
specifier|public
specifier|static
specifier|final
name|String
name|MAXIMUM_APPLICATION_MASTERS_RESOURCE_PERCENT
init|=
name|PREFIX
operator|+
name|MAXIMUM_AM_RESOURCE_SUFFIX
decl_stmt|;
annotation|@
name|Private
DECL|field|QUEUES
specifier|public
specifier|static
specifier|final
name|String
name|QUEUES
init|=
literal|"queues"
decl_stmt|;
annotation|@
name|Private
DECL|field|CAPACITY
specifier|public
specifier|static
specifier|final
name|String
name|CAPACITY
init|=
literal|"capacity"
decl_stmt|;
annotation|@
name|Private
DECL|field|MAXIMUM_CAPACITY
specifier|public
specifier|static
specifier|final
name|String
name|MAXIMUM_CAPACITY
init|=
literal|"maximum-capacity"
decl_stmt|;
annotation|@
name|Private
DECL|field|USER_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|USER_LIMIT
init|=
literal|"minimum-user-limit-percent"
decl_stmt|;
annotation|@
name|Private
DECL|field|USER_LIMIT_FACTOR
specifier|public
specifier|static
specifier|final
name|String
name|USER_LIMIT_FACTOR
init|=
literal|"user-limit-factor"
decl_stmt|;
annotation|@
name|Private
DECL|field|STATE
specifier|public
specifier|static
specifier|final
name|String
name|STATE
init|=
literal|"state"
decl_stmt|;
annotation|@
name|Private
DECL|field|RESERVE_CONT_LOOK_ALL_NODES
specifier|public
specifier|static
specifier|final
name|String
name|RESERVE_CONT_LOOK_ALL_NODES
init|=
name|PREFIX
operator|+
literal|"reservations-continue-look-all-nodes"
decl_stmt|;
annotation|@
name|Private
DECL|field|DEFAULT_RESERVE_CONT_LOOK_ALL_NODES
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_RESERVE_CONT_LOOK_ALL_NODES
init|=
literal|true
decl_stmt|;
annotation|@
name|Private
DECL|field|DEFAULT_MAXIMUM_SYSTEM_APPLICATIIONS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAXIMUM_SYSTEM_APPLICATIIONS
init|=
literal|10000
decl_stmt|;
annotation|@
name|Private
specifier|public
specifier|static
specifier|final
name|float
DECL|field|DEFAULT_MAXIMUM_APPLICATIONMASTERS_RESOURCE_PERCENT
name|DEFAULT_MAXIMUM_APPLICATIONMASTERS_RESOURCE_PERCENT
init|=
literal|0.1f
decl_stmt|;
annotation|@
name|Private
DECL|field|UNDEFINED
specifier|public
specifier|static
specifier|final
name|float
name|UNDEFINED
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Private
DECL|field|MINIMUM_CAPACITY_VALUE
specifier|public
specifier|static
specifier|final
name|float
name|MINIMUM_CAPACITY_VALUE
init|=
literal|0
decl_stmt|;
annotation|@
name|Private
DECL|field|MAXIMUM_CAPACITY_VALUE
specifier|public
specifier|static
specifier|final
name|float
name|MAXIMUM_CAPACITY_VALUE
init|=
literal|100
decl_stmt|;
annotation|@
name|Private
DECL|field|DEFAULT_MAXIMUM_CAPACITY_VALUE
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_MAXIMUM_CAPACITY_VALUE
init|=
operator|-
literal|1.0f
decl_stmt|;
annotation|@
name|Private
DECL|field|DEFAULT_USER_LIMIT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_USER_LIMIT
init|=
literal|100
decl_stmt|;
annotation|@
name|Private
DECL|field|DEFAULT_USER_LIMIT_FACTOR
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_USER_LIMIT_FACTOR
init|=
literal|1.0f
decl_stmt|;
annotation|@
name|Private
DECL|field|ALL_ACL
specifier|public
specifier|static
specifier|final
name|String
name|ALL_ACL
init|=
literal|"*"
decl_stmt|;
annotation|@
name|Private
DECL|field|NONE_ACL
specifier|public
specifier|static
specifier|final
name|String
name|NONE_ACL
init|=
literal|" "
decl_stmt|;
DECL|field|ENABLE_USER_METRICS
annotation|@
name|Private
specifier|public
specifier|static
specifier|final
name|String
name|ENABLE_USER_METRICS
init|=
name|PREFIX
operator|+
literal|"user-metrics.enable"
decl_stmt|;
DECL|field|DEFAULT_ENABLE_USER_METRICS
annotation|@
name|Private
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_ENABLE_USER_METRICS
init|=
literal|false
decl_stmt|;
comment|/** ResourceComparator for scheduling. */
DECL|field|RESOURCE_CALCULATOR_CLASS
annotation|@
name|Private
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_CALCULATOR_CLASS
init|=
name|PREFIX
operator|+
literal|"resource-calculator"
decl_stmt|;
annotation|@
name|Private
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ResourceCalculator
argument_list|>
DECL|field|DEFAULT_RESOURCE_CALCULATOR_CLASS
name|DEFAULT_RESOURCE_CALCULATOR_CLASS
init|=
name|DefaultResourceCalculator
operator|.
name|class
decl_stmt|;
annotation|@
name|Private
DECL|field|ROOT
specifier|public
specifier|static
specifier|final
name|String
name|ROOT
init|=
literal|"root"
decl_stmt|;
annotation|@
name|Private
DECL|field|NODE_LOCALITY_DELAY
specifier|public
specifier|static
specifier|final
name|String
name|NODE_LOCALITY_DELAY
init|=
name|PREFIX
operator|+
literal|"node-locality-delay"
decl_stmt|;
annotation|@
name|Private
DECL|field|DEFAULT_NODE_LOCALITY_DELAY
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NODE_LOCALITY_DELAY
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Private
DECL|field|SCHEDULE_ASYNCHRONOUSLY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|SCHEDULE_ASYNCHRONOUSLY_PREFIX
init|=
name|PREFIX
operator|+
literal|"schedule-asynchronously"
decl_stmt|;
annotation|@
name|Private
DECL|field|SCHEDULE_ASYNCHRONOUSLY_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|SCHEDULE_ASYNCHRONOUSLY_ENABLE
init|=
name|SCHEDULE_ASYNCHRONOUSLY_PREFIX
operator|+
literal|".enable"
decl_stmt|;
annotation|@
name|Private
DECL|field|DEFAULT_SCHEDULE_ASYNCHRONOUSLY_ENABLE
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_SCHEDULE_ASYNCHRONOUSLY_ENABLE
init|=
literal|false
decl_stmt|;
annotation|@
name|Private
DECL|field|QUEUE_MAPPING
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_MAPPING
init|=
name|PREFIX
operator|+
literal|"queue-mappings"
decl_stmt|;
annotation|@
name|Private
DECL|field|ENABLE_QUEUE_MAPPING_OVERRIDE
specifier|public
specifier|static
specifier|final
name|String
name|ENABLE_QUEUE_MAPPING_OVERRIDE
init|=
name|QUEUE_MAPPING
operator|+
literal|"-override.enable"
decl_stmt|;
annotation|@
name|Private
DECL|field|DEFAULT_ENABLE_QUEUE_MAPPING_OVERRIDE
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_ENABLE_QUEUE_MAPPING_OVERRIDE
init|=
literal|false
decl_stmt|;
annotation|@
name|Private
DECL|class|QueueMapping
specifier|public
specifier|static
class|class
name|QueueMapping
block|{
DECL|enum|MappingType
specifier|public
enum|enum
name|MappingType
block|{
DECL|enumConstant|USER
name|USER
argument_list|(
literal|"u"
argument_list|)
block|,
DECL|enumConstant|GROUP
name|GROUP
argument_list|(
literal|"g"
argument_list|)
block|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|method|MappingType (String type)
specifier|private
name|MappingType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
empty_stmt|;
DECL|field|type
name|MappingType
name|type
decl_stmt|;
DECL|field|source
name|String
name|source
decl_stmt|;
DECL|field|queue
name|String
name|queue
decl_stmt|;
DECL|method|QueueMapping (MappingType type, String source, String queue)
specifier|public
name|QueueMapping
parameter_list|(
name|MappingType
name|type
parameter_list|,
name|String
name|source
parameter_list|,
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
block|}
DECL|method|CapacitySchedulerConfiguration ()
specifier|public
name|CapacitySchedulerConfiguration
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CapacitySchedulerConfiguration (Configuration configuration)
specifier|public
name|CapacitySchedulerConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|this
argument_list|(
name|configuration
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|CapacitySchedulerConfiguration (Configuration configuration, boolean useLocalConfigurationProvider)
specifier|public
name|CapacitySchedulerConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|boolean
name|useLocalConfigurationProvider
parameter_list|)
block|{
name|super
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
if|if
condition|(
name|useLocalConfigurationProvider
condition|)
block|{
name|addResource
argument_list|(
name|CS_CONFIGURATION_FILE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getQueuePrefix (String queue)
specifier|private
name|String
name|getQueuePrefix
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|String
name|queueName
init|=
name|PREFIX
operator|+
name|queue
operator|+
name|DOT
decl_stmt|;
return|return
name|queueName
return|;
block|}
DECL|method|getMaximumSystemApplications ()
specifier|public
name|int
name|getMaximumSystemApplications
parameter_list|()
block|{
name|int
name|maxApplications
init|=
name|getInt
argument_list|(
name|MAXIMUM_SYSTEM_APPLICATIONS
argument_list|,
name|DEFAULT_MAXIMUM_SYSTEM_APPLICATIIONS
argument_list|)
decl_stmt|;
return|return
name|maxApplications
return|;
block|}
DECL|method|getMaximumApplicationMasterResourcePercent ()
specifier|public
name|float
name|getMaximumApplicationMasterResourcePercent
parameter_list|()
block|{
return|return
name|getFloat
argument_list|(
name|MAXIMUM_APPLICATION_MASTERS_RESOURCE_PERCENT
argument_list|,
name|DEFAULT_MAXIMUM_APPLICATIONMASTERS_RESOURCE_PERCENT
argument_list|)
return|;
block|}
comment|/**    * Get the maximum applications per queue setting.    * @param queue name of the queue    * @return setting specified or -1 if not set    */
DECL|method|getMaximumApplicationsPerQueue (String queue)
specifier|public
name|int
name|getMaximumApplicationsPerQueue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|int
name|maxApplicationsPerQueue
init|=
name|getInt
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|MAXIMUM_APPLICATIONS_SUFFIX
argument_list|,
operator|(
name|int
operator|)
name|UNDEFINED
argument_list|)
decl_stmt|;
return|return
name|maxApplicationsPerQueue
return|;
block|}
comment|/**    * Get the maximum am resource percent per queue setting.    * @param queue name of the queue    * @return per queue setting or defaults to the global am-resource-percent     *         setting if per queue setting not present    */
DECL|method|getMaximumApplicationMasterResourcePerQueuePercent (String queue)
specifier|public
name|float
name|getMaximumApplicationMasterResourcePerQueuePercent
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|getFloat
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|MAXIMUM_AM_RESOURCE_SUFFIX
argument_list|,
name|getMaximumApplicationMasterResourcePercent
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getCapacity (String queue)
specifier|public
name|float
name|getCapacity
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|float
name|capacity
init|=
name|queue
operator|.
name|equals
argument_list|(
literal|"root"
argument_list|)
condition|?
literal|100.0f
else|:
name|getFloat
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|CAPACITY
argument_list|,
name|UNDEFINED
argument_list|)
decl_stmt|;
if|if
condition|(
name|capacity
argument_list|<
name|MINIMUM_CAPACITY_VALUE
operator|||
name|capacity
argument_list|>
name|MAXIMUM_CAPACITY_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal "
operator|+
literal|"capacity of "
operator|+
name|capacity
operator|+
literal|" for queue "
operator|+
name|queue
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"CSConf - getCapacity: queuePrefix="
operator|+
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
literal|", capacity="
operator|+
name|capacity
argument_list|)
expr_stmt|;
return|return
name|capacity
return|;
block|}
DECL|method|setCapacity (String queue, float capacity)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|String
name|queue
parameter_list|,
name|float
name|capacity
parameter_list|)
block|{
if|if
condition|(
name|queue
operator|.
name|equals
argument_list|(
literal|"root"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot set capacity, root queue has a fixed capacity of 100.0f"
argument_list|)
throw|;
block|}
name|setFloat
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|CAPACITY
argument_list|,
name|capacity
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"CSConf - setCapacity: queuePrefix="
operator|+
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
literal|", capacity="
operator|+
name|capacity
argument_list|)
expr_stmt|;
block|}
DECL|method|getMaximumCapacity (String queue)
specifier|public
name|float
name|getMaximumCapacity
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|float
name|maxCapacity
init|=
name|getFloat
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|MAXIMUM_CAPACITY
argument_list|,
name|MAXIMUM_CAPACITY_VALUE
argument_list|)
decl_stmt|;
name|maxCapacity
operator|=
operator|(
name|maxCapacity
operator|==
name|DEFAULT_MAXIMUM_CAPACITY_VALUE
operator|)
condition|?
name|MAXIMUM_CAPACITY_VALUE
else|:
name|maxCapacity
expr_stmt|;
return|return
name|maxCapacity
return|;
block|}
DECL|method|setMaximumCapacity (String queue, float maxCapacity)
specifier|public
name|void
name|setMaximumCapacity
parameter_list|(
name|String
name|queue
parameter_list|,
name|float
name|maxCapacity
parameter_list|)
block|{
if|if
condition|(
name|maxCapacity
operator|>
name|MAXIMUM_CAPACITY_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal "
operator|+
literal|"maximum-capacity of "
operator|+
name|maxCapacity
operator|+
literal|" for queue "
operator|+
name|queue
argument_list|)
throw|;
block|}
name|setFloat
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|MAXIMUM_CAPACITY
argument_list|,
name|maxCapacity
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"CSConf - setMaxCapacity: queuePrefix="
operator|+
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
literal|", maxCapacity="
operator|+
name|maxCapacity
argument_list|)
expr_stmt|;
block|}
DECL|method|getUserLimit (String queue)
specifier|public
name|int
name|getUserLimit
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|int
name|userLimit
init|=
name|getInt
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|USER_LIMIT
argument_list|,
name|DEFAULT_USER_LIMIT
argument_list|)
decl_stmt|;
return|return
name|userLimit
return|;
block|}
DECL|method|setUserLimit (String queue, int userLimit)
specifier|public
name|void
name|setUserLimit
parameter_list|(
name|String
name|queue
parameter_list|,
name|int
name|userLimit
parameter_list|)
block|{
name|setInt
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|USER_LIMIT
argument_list|,
name|userLimit
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"here setUserLimit: queuePrefix="
operator|+
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
literal|", userLimit="
operator|+
name|getUserLimit
argument_list|(
name|queue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getUserLimitFactor (String queue)
specifier|public
name|float
name|getUserLimitFactor
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|float
name|userLimitFactor
init|=
name|getFloat
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|USER_LIMIT_FACTOR
argument_list|,
name|DEFAULT_USER_LIMIT_FACTOR
argument_list|)
decl_stmt|;
return|return
name|userLimitFactor
return|;
block|}
DECL|method|setUserLimitFactor (String queue, float userLimitFactor)
specifier|public
name|void
name|setUserLimitFactor
parameter_list|(
name|String
name|queue
parameter_list|,
name|float
name|userLimitFactor
parameter_list|)
block|{
name|setFloat
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|USER_LIMIT_FACTOR
argument_list|,
name|userLimitFactor
argument_list|)
expr_stmt|;
block|}
DECL|method|getState (String queue)
specifier|public
name|QueueState
name|getState
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|String
name|state
init|=
name|get
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|STATE
argument_list|)
decl_stmt|;
return|return
operator|(
name|state
operator|!=
literal|null
operator|)
condition|?
name|QueueState
operator|.
name|valueOf
argument_list|(
name|state
operator|.
name|toUpperCase
argument_list|()
argument_list|)
else|:
name|QueueState
operator|.
name|RUNNING
return|;
block|}
comment|/*    * Returns whether we should continue to look at all heart beating nodes even    * after the reservation limit was hit. The node heart beating in could    * satisfy the request thus could be a better pick then waiting for the    * reservation to be fullfilled.  This config is refreshable.    */
DECL|method|getReservationContinueLook ()
specifier|public
name|boolean
name|getReservationContinueLook
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|RESERVE_CONT_LOOK_ALL_NODES
argument_list|,
name|DEFAULT_RESERVE_CONT_LOOK_ALL_NODES
argument_list|)
return|;
block|}
DECL|method|getAclKey (QueueACL acl)
specifier|private
specifier|static
name|String
name|getAclKey
parameter_list|(
name|QueueACL
name|acl
parameter_list|)
block|{
return|return
literal|"acl_"
operator|+
name|acl
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
DECL|method|getAcl (String queue, QueueACL acl)
specifier|public
name|AccessControlList
name|getAcl
parameter_list|(
name|String
name|queue
parameter_list|,
name|QueueACL
name|acl
parameter_list|)
block|{
name|String
name|queuePrefix
init|=
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
decl_stmt|;
comment|// The root queue defaults to all access if not defined
comment|// Sub queues inherit access if not defined
name|String
name|defaultAcl
init|=
name|queue
operator|.
name|equals
argument_list|(
name|ROOT
argument_list|)
condition|?
name|ALL_ACL
else|:
name|NONE_ACL
decl_stmt|;
name|String
name|aclString
init|=
name|get
argument_list|(
name|queuePrefix
operator|+
name|getAclKey
argument_list|(
name|acl
argument_list|)
argument_list|,
name|defaultAcl
argument_list|)
decl_stmt|;
return|return
operator|new
name|AccessControlList
argument_list|(
name|aclString
argument_list|)
return|;
block|}
DECL|method|setAcl (String queue, QueueACL acl, String aclString)
specifier|public
name|void
name|setAcl
parameter_list|(
name|String
name|queue
parameter_list|,
name|QueueACL
name|acl
parameter_list|,
name|String
name|aclString
parameter_list|)
block|{
name|String
name|queuePrefix
init|=
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|set
argument_list|(
name|queuePrefix
operator|+
name|getAclKey
argument_list|(
name|acl
argument_list|)
argument_list|,
name|aclString
argument_list|)
expr_stmt|;
block|}
DECL|method|getAcls (String queue)
specifier|public
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|getAcls
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
operator|new
name|HashMap
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueueACL
name|acl
range|:
name|QueueACL
operator|.
name|values
argument_list|()
control|)
block|{
name|acls
operator|.
name|put
argument_list|(
name|acl
argument_list|,
name|getAcl
argument_list|(
name|queue
argument_list|,
name|acl
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|acls
return|;
block|}
DECL|method|setAcls (String queue, Map<QueueACL, AccessControlList> acls)
specifier|public
name|void
name|setAcls
parameter_list|(
name|String
name|queue
parameter_list|,
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|acls
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|e
range|:
name|acls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|setAcl
argument_list|(
name|queue
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getQueues (String queue)
specifier|public
name|String
index|[]
name|getQueues
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"CSConf - getQueues called for: queuePrefix="
operator|+
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|queues
init|=
name|getStrings
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|QUEUES
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"CSConf - getQueues: queuePrefix="
operator|+
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
literal|", queues="
operator|+
operator|(
operator|(
name|queues
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|queues
argument_list|)
operator|)
argument_list|)
expr_stmt|;
return|return
name|queues
return|;
block|}
DECL|method|setQueues (String queue, String[] subQueues)
specifier|public
name|void
name|setQueues
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
index|[]
name|subQueues
parameter_list|)
block|{
name|set
argument_list|(
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
name|QUEUES
argument_list|,
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|subQueues
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"CSConf - setQueues: qPrefix="
operator|+
name|getQueuePrefix
argument_list|(
name|queue
argument_list|)
operator|+
literal|", queues="
operator|+
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|subQueues
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getMinimumAllocation ()
specifier|public
name|Resource
name|getMinimumAllocation
parameter_list|()
block|{
name|int
name|minimumMemory
init|=
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|)
decl_stmt|;
name|int
name|minimumCores
init|=
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES
argument_list|)
decl_stmt|;
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|minimumMemory
argument_list|,
name|minimumCores
argument_list|)
return|;
block|}
DECL|method|getMaximumAllocation ()
specifier|public
name|Resource
name|getMaximumAllocation
parameter_list|()
block|{
name|int
name|maximumMemory
init|=
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|)
decl_stmt|;
name|int
name|maximumCores
init|=
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|)
decl_stmt|;
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|maximumMemory
argument_list|,
name|maximumCores
argument_list|)
return|;
block|}
DECL|method|getEnableUserMetrics ()
specifier|public
name|boolean
name|getEnableUserMetrics
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|ENABLE_USER_METRICS
argument_list|,
name|DEFAULT_ENABLE_USER_METRICS
argument_list|)
return|;
block|}
DECL|method|getNodeLocalityDelay ()
specifier|public
name|int
name|getNodeLocalityDelay
parameter_list|()
block|{
name|int
name|delay
init|=
name|getInt
argument_list|(
name|NODE_LOCALITY_DELAY
argument_list|,
name|DEFAULT_NODE_LOCALITY_DELAY
argument_list|)
decl_stmt|;
return|return
operator|(
name|delay
operator|==
name|DEFAULT_NODE_LOCALITY_DELAY
operator|)
condition|?
literal|0
else|:
name|delay
return|;
block|}
DECL|method|getResourceCalculator ()
specifier|public
name|ResourceCalculator
name|getResourceCalculator
parameter_list|()
block|{
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|getClass
argument_list|(
name|RESOURCE_CALCULATOR_CLASS
argument_list|,
name|DEFAULT_RESOURCE_CALCULATOR_CLASS
argument_list|,
name|ResourceCalculator
operator|.
name|class
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|getUsePortForNodeName ()
specifier|public
name|boolean
name|getUsePortForNodeName
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_INCLUDE_PORT_IN_NODE_NAME
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_USE_PORT_FOR_NODE_NAME
argument_list|)
return|;
block|}
DECL|method|setResourceComparator ( Class<? extends ResourceCalculator> resourceCalculatorClass)
specifier|public
name|void
name|setResourceComparator
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|ResourceCalculator
argument_list|>
name|resourceCalculatorClass
parameter_list|)
block|{
name|setClass
argument_list|(
name|RESOURCE_CALCULATOR_CLASS
argument_list|,
name|resourceCalculatorClass
argument_list|,
name|ResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|getScheduleAynschronously ()
specifier|public
name|boolean
name|getScheduleAynschronously
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|SCHEDULE_ASYNCHRONOUSLY_ENABLE
argument_list|,
name|DEFAULT_SCHEDULE_ASYNCHRONOUSLY_ENABLE
argument_list|)
return|;
block|}
DECL|method|setScheduleAynschronously (boolean async)
specifier|public
name|void
name|setScheduleAynschronously
parameter_list|(
name|boolean
name|async
parameter_list|)
block|{
name|setBoolean
argument_list|(
name|SCHEDULE_ASYNCHRONOUSLY_ENABLE
argument_list|,
name|async
argument_list|)
expr_stmt|;
block|}
DECL|method|getOverrideWithQueueMappings ()
specifier|public
name|boolean
name|getOverrideWithQueueMappings
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|ENABLE_QUEUE_MAPPING_OVERRIDE
argument_list|,
name|DEFAULT_ENABLE_QUEUE_MAPPING_OVERRIDE
argument_list|)
return|;
block|}
comment|/**    * Returns a collection of strings, trimming leading and trailing whitespeace    * on each value    *    * @param str    *          String to parse    * @param delim    *          delimiter to separate the values    * @return Collection of parsed elements.    */
DECL|method|getTrimmedStringCollection (String str, String delim)
specifier|private
specifier|static
name|Collection
argument_list|<
name|String
argument_list|>
name|getTrimmedStringCollection
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|delim
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|str
operator|==
literal|null
condition|)
return|return
name|values
return|;
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|str
argument_list|,
name|delim
argument_list|)
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|next
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
operator|||
name|next
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|values
operator|.
name|add
argument_list|(
name|next
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
comment|/**    * Get user/group mappings to queues.    *    * @return user/groups mappings or null on illegal configs    */
DECL|method|getQueueMappings ()
specifier|public
name|List
argument_list|<
name|QueueMapping
argument_list|>
name|getQueueMappings
parameter_list|()
block|{
name|List
argument_list|<
name|QueueMapping
argument_list|>
name|mappings
init|=
operator|new
name|ArrayList
argument_list|<
name|CapacitySchedulerConfiguration
operator|.
name|QueueMapping
argument_list|>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|mappingsString
init|=
name|getTrimmedStringCollection
argument_list|(
name|QUEUE_MAPPING
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|mappingValue
range|:
name|mappingsString
control|)
block|{
name|String
index|[]
name|mapping
init|=
name|getTrimmedStringCollection
argument_list|(
name|mappingValue
argument_list|,
literal|":"
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapping
operator|.
name|length
operator|!=
literal|3
operator|||
name|mapping
index|[
literal|1
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|mapping
index|[
literal|2
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal queue mapping "
operator|+
name|mappingValue
argument_list|)
throw|;
block|}
name|QueueMapping
name|m
decl_stmt|;
try|try
block|{
name|QueueMapping
operator|.
name|MappingType
name|mappingType
decl_stmt|;
if|if
condition|(
name|mapping
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"u"
argument_list|)
condition|)
block|{
name|mappingType
operator|=
name|QueueMapping
operator|.
name|MappingType
operator|.
name|USER
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mapping
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"g"
argument_list|)
condition|)
block|{
name|mappingType
operator|=
name|QueueMapping
operator|.
name|MappingType
operator|.
name|GROUP
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown mapping prefix "
operator|+
name|mapping
index|[
literal|0
index|]
argument_list|)
throw|;
block|}
name|m
operator|=
operator|new
name|QueueMapping
argument_list|(
name|mappingType
argument_list|,
name|mapping
index|[
literal|1
index|]
argument_list|,
name|mapping
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal queue mapping "
operator|+
name|mappingValue
argument_list|)
throw|;
block|}
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|mappings
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|mappings
return|;
block|}
block|}
end_class

end_unit

