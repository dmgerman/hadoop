begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Map
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
name|metrics2
operator|.
name|MetricsSystem
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
name|metrics2
operator|.
name|annotation
operator|.
name|Metric
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
name|metrics2
operator|.
name|annotation
operator|.
name|Metrics
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableGaugeFloat
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableGaugeInt
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableGaugeLong
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
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|CSQueueMetricsForCustomResources
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|QueueMetrics
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
name|ResourceUtils
import|;
end_import

begin_class
annotation|@
name|Metrics
argument_list|(
name|context
operator|=
literal|"yarn"
argument_list|)
DECL|class|CSQueueMetrics
specifier|public
class|class
name|CSQueueMetrics
extends|extends
name|QueueMetrics
block|{
comment|//Metrics updated only for "default" partition
annotation|@
name|Metric
argument_list|(
literal|"AM memory limit in MB"
argument_list|)
DECL|field|AMResourceLimitMB
name|MutableGaugeLong
name|AMResourceLimitMB
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"AM CPU limit in virtual cores"
argument_list|)
DECL|field|AMResourceLimitVCores
name|MutableGaugeLong
name|AMResourceLimitVCores
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Used AM memory limit in MB"
argument_list|)
DECL|field|usedAMResourceMB
name|MutableGaugeLong
name|usedAMResourceMB
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Used AM CPU limit in virtual cores"
argument_list|)
DECL|field|usedAMResourceVCores
name|MutableGaugeLong
name|usedAMResourceVCores
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Percent of Capacity Used"
argument_list|)
DECL|field|usedCapacity
name|MutableGaugeFloat
name|usedCapacity
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Percent of Absolute Capacity Used"
argument_list|)
DECL|field|absoluteUsedCapacity
name|MutableGaugeFloat
name|absoluteUsedCapacity
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Guaranteed memory in MB"
argument_list|)
DECL|field|guaranteedMB
name|MutableGaugeLong
name|guaranteedMB
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Guaranteed CPU in virtual cores"
argument_list|)
DECL|field|guaranteedVCores
name|MutableGaugeInt
name|guaranteedVCores
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Maximum memory in MB"
argument_list|)
DECL|field|maxCapacityMB
name|MutableGaugeLong
name|maxCapacityMB
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Maximum CPU in virtual cores"
argument_list|)
DECL|field|maxCapacityVCores
name|MutableGaugeInt
name|maxCapacityVCores
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Guaranteed capacity in percentage relative to parent"
argument_list|)
DECL|field|guaranteedCapacity
specifier|private
name|MutableGaugeFloat
name|guaranteedCapacity
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Guaranteed capacity in percentage relative to total partition"
argument_list|)
DECL|field|guaranteedAbsoluteCapacity
specifier|private
name|MutableGaugeFloat
name|guaranteedAbsoluteCapacity
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Maximum capacity in percentage relative to parent"
argument_list|)
DECL|field|maxCapacity
specifier|private
name|MutableGaugeFloat
name|maxCapacity
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Maximum capacity in percentage relative to total partition"
argument_list|)
DECL|field|maxAbsoluteCapacity
specifier|private
name|MutableGaugeFloat
name|maxAbsoluteCapacity
decl_stmt|;
DECL|field|GUARANTEED_CAPACITY_METRIC_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|GUARANTEED_CAPACITY_METRIC_PREFIX
init|=
literal|"GuaranteedCapacity."
decl_stmt|;
DECL|field|GUARANTEED_CAPACITY_METRIC_DESC
specifier|private
specifier|static
specifier|final
name|String
name|GUARANTEED_CAPACITY_METRIC_DESC
init|=
literal|"GuaranteedCapacity of NAME"
decl_stmt|;
DECL|field|MAX_CAPACITY_METRIC_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|MAX_CAPACITY_METRIC_PREFIX
init|=
literal|"MaxCapacity."
decl_stmt|;
DECL|field|MAX_CAPACITY_METRIC_DESC
specifier|private
specifier|static
specifier|final
name|String
name|MAX_CAPACITY_METRIC_DESC
init|=
literal|"MaxCapacity of NAME"
decl_stmt|;
DECL|method|CSQueueMetrics (MetricsSystem ms, String queueName, Queue parent, boolean enableUserMetrics, Configuration conf)
name|CSQueueMetrics
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|,
name|String
name|queueName
parameter_list|,
name|Queue
name|parent
parameter_list|,
name|boolean
name|enableUserMetrics
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|enableUserMetrics
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Register all custom resources metrics as part of initialization. As and    * when this metric object construction happens for any queue, all custom    * resource metrics value would be initialized with '0' like any other    * mandatory resources metrics    */
DECL|method|registerCustomResources ()
specifier|protected
name|void
name|registerCustomResources
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|customResources
init|=
name|initAndGetCustomResources
argument_list|()
decl_stmt|;
name|registerCustomResources
argument_list|(
name|customResources
argument_list|,
name|GUARANTEED_CAPACITY_METRIC_PREFIX
argument_list|,
name|GUARANTEED_CAPACITY_METRIC_DESC
argument_list|)
expr_stmt|;
name|registerCustomResources
argument_list|(
name|customResources
argument_list|,
name|MAX_CAPACITY_METRIC_PREFIX
argument_list|,
name|MAX_CAPACITY_METRIC_DESC
argument_list|)
expr_stmt|;
name|super
operator|.
name|registerCustomResources
argument_list|()
expr_stmt|;
block|}
DECL|method|getAMResourceLimitMB ()
specifier|public
name|long
name|getAMResourceLimitMB
parameter_list|()
block|{
return|return
name|AMResourceLimitMB
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getAMResourceLimitVCores ()
specifier|public
name|long
name|getAMResourceLimitVCores
parameter_list|()
block|{
return|return
name|AMResourceLimitVCores
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getUsedAMResourceMB ()
specifier|public
name|long
name|getUsedAMResourceMB
parameter_list|()
block|{
return|return
name|usedAMResourceMB
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getUsedAMResourceVCores ()
specifier|public
name|long
name|getUsedAMResourceVCores
parameter_list|()
block|{
return|return
name|usedAMResourceVCores
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setAMResouceLimit (String partition, Resource res)
specifier|public
name|void
name|setAMResouceLimit
parameter_list|(
name|String
name|partition
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
if|if
condition|(
name|partition
operator|==
literal|null
operator|||
name|partition
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|AMResourceLimitMB
operator|.
name|set
argument_list|(
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|AMResourceLimitVCores
operator|.
name|set
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setAMResouceLimitForUser (String partition, String user, Resource res)
specifier|public
name|void
name|setAMResouceLimitForUser
parameter_list|(
name|String
name|partition
parameter_list|,
name|String
name|user
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|CSQueueMetrics
name|userMetrics
init|=
operator|(
name|CSQueueMetrics
operator|)
name|getUserMetrics
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|userMetrics
operator|!=
literal|null
condition|)
block|{
name|userMetrics
operator|.
name|setAMResouceLimit
argument_list|(
name|partition
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|incAMUsed (String partition, String user, Resource res)
specifier|public
name|void
name|incAMUsed
parameter_list|(
name|String
name|partition
parameter_list|,
name|String
name|user
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
if|if
condition|(
name|partition
operator|==
literal|null
operator|||
name|partition
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|usedAMResourceMB
operator|.
name|incr
argument_list|(
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|usedAMResourceVCores
operator|.
name|incr
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|CSQueueMetrics
name|userMetrics
init|=
operator|(
name|CSQueueMetrics
operator|)
name|getUserMetrics
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|userMetrics
operator|!=
literal|null
condition|)
block|{
name|userMetrics
operator|.
name|incAMUsed
argument_list|(
name|partition
argument_list|,
name|user
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|decAMUsed (String partition, String user, Resource res)
specifier|public
name|void
name|decAMUsed
parameter_list|(
name|String
name|partition
parameter_list|,
name|String
name|user
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
if|if
condition|(
name|partition
operator|==
literal|null
operator|||
name|partition
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|usedAMResourceMB
operator|.
name|decr
argument_list|(
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|usedAMResourceVCores
operator|.
name|decr
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|CSQueueMetrics
name|userMetrics
init|=
operator|(
name|CSQueueMetrics
operator|)
name|getUserMetrics
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|userMetrics
operator|!=
literal|null
condition|)
block|{
name|userMetrics
operator|.
name|decAMUsed
argument_list|(
name|partition
argument_list|,
name|user
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getUsedCapacity ()
specifier|public
name|float
name|getUsedCapacity
parameter_list|()
block|{
return|return
name|usedCapacity
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setUsedCapacity (String partition, float usedCap)
specifier|public
name|void
name|setUsedCapacity
parameter_list|(
name|String
name|partition
parameter_list|,
name|float
name|usedCap
parameter_list|)
block|{
if|if
condition|(
name|partition
operator|==
literal|null
operator|||
name|partition
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|this
operator|.
name|usedCapacity
operator|.
name|set
argument_list|(
name|usedCap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAbsoluteUsedCapacity ()
specifier|public
name|float
name|getAbsoluteUsedCapacity
parameter_list|()
block|{
return|return
name|absoluteUsedCapacity
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setAbsoluteUsedCapacity (String partition, Float absoluteUsedCap)
specifier|public
name|void
name|setAbsoluteUsedCapacity
parameter_list|(
name|String
name|partition
parameter_list|,
name|Float
name|absoluteUsedCap
parameter_list|)
block|{
if|if
condition|(
name|partition
operator|==
literal|null
operator|||
name|partition
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|this
operator|.
name|absoluteUsedCapacity
operator|.
name|set
argument_list|(
name|absoluteUsedCap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getGuaranteedMB ()
specifier|public
name|long
name|getGuaranteedMB
parameter_list|()
block|{
return|return
name|guaranteedMB
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getGuaranteedVCores ()
specifier|public
name|int
name|getGuaranteedVCores
parameter_list|()
block|{
return|return
name|guaranteedVCores
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setGuaranteedResources (String partition, Resource res)
specifier|public
name|void
name|setGuaranteedResources
parameter_list|(
name|String
name|partition
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
if|if
condition|(
name|partition
operator|==
literal|null
operator|||
name|partition
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|guaranteedMB
operator|.
name|set
argument_list|(
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|guaranteedVCores
operator|.
name|set
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getQueueMetricsForCustomResources
argument_list|()
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|CSQueueMetricsForCustomResources
operator|)
name|getQueueMetricsForCustomResources
argument_list|()
operator|)
operator|.
name|setGuaranteedCapacity
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|registerCustomResources
argument_list|(
operator|(
operator|(
name|CSQueueMetricsForCustomResources
operator|)
name|getQueueMetricsForCustomResources
argument_list|()
operator|)
operator|.
name|getGuaranteedCapacity
argument_list|()
argument_list|,
name|GUARANTEED_CAPACITY_METRIC_PREFIX
argument_list|,
name|GUARANTEED_CAPACITY_METRIC_DESC
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getMaxCapacityMB ()
specifier|public
name|long
name|getMaxCapacityMB
parameter_list|()
block|{
return|return
name|maxCapacityMB
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getMaxCapacityVCores ()
specifier|public
name|int
name|getMaxCapacityVCores
parameter_list|()
block|{
return|return
name|maxCapacityVCores
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setMaxCapacityResources (String partition, Resource res)
specifier|public
name|void
name|setMaxCapacityResources
parameter_list|(
name|String
name|partition
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
if|if
condition|(
name|partition
operator|==
literal|null
operator|||
name|partition
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|maxCapacityMB
operator|.
name|set
argument_list|(
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|maxCapacityVCores
operator|.
name|set
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getQueueMetricsForCustomResources
argument_list|()
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|CSQueueMetricsForCustomResources
operator|)
name|getQueueMetricsForCustomResources
argument_list|()
operator|)
operator|.
name|setMaxCapacity
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|registerCustomResources
argument_list|(
operator|(
operator|(
name|CSQueueMetricsForCustomResources
operator|)
name|getQueueMetricsForCustomResources
argument_list|()
operator|)
operator|.
name|getMaxCapacity
argument_list|()
argument_list|,
name|MAX_CAPACITY_METRIC_PREFIX
argument_list|,
name|MAX_CAPACITY_METRIC_DESC
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createQueueMetricsForCustomResources ()
specifier|protected
name|void
name|createQueueMetricsForCustomResources
parameter_list|()
block|{
if|if
condition|(
name|ResourceUtils
operator|.
name|getNumberOfKnownResourceTypes
argument_list|()
operator|>
literal|2
condition|)
block|{
name|setQueueMetricsForCustomResources
argument_list|(
operator|new
name|CSQueueMetricsForCustomResources
argument_list|()
argument_list|)
expr_stmt|;
name|registerCustomResources
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|forQueue (String queueName, Queue parent, boolean enableUserMetrics, Configuration conf)
specifier|public
specifier|synchronized
specifier|static
name|CSQueueMetrics
name|forQueue
parameter_list|(
name|String
name|queueName
parameter_list|,
name|Queue
name|parent
parameter_list|,
name|boolean
name|enableUserMetrics
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
name|QueueMetrics
name|metrics
init|=
name|QueueMetrics
operator|.
name|getQueueMetrics
argument_list|()
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|metrics
operator|==
literal|null
condition|)
block|{
name|metrics
operator|=
operator|new
name|CSQueueMetrics
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|enableUserMetrics
argument_list|,
name|conf
argument_list|)
operator|.
name|tag
argument_list|(
name|QUEUE_INFO
argument_list|,
name|queueName
argument_list|)
expr_stmt|;
comment|// Register with the MetricsSystems
if|if
condition|(
name|ms
operator|!=
literal|null
condition|)
block|{
name|metrics
operator|=
name|ms
operator|.
name|register
argument_list|(
name|sourceName
argument_list|(
name|queueName
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Metrics for queue: "
operator|+
name|queueName
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
name|QueueMetrics
operator|.
name|getQueueMetrics
argument_list|()
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|CSQueueMetrics
operator|)
name|metrics
return|;
block|}
annotation|@
name|Override
DECL|method|getUserMetrics (String userName)
specifier|public
specifier|synchronized
name|QueueMetrics
name|getUserMetrics
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
if|if
condition|(
name|users
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|CSQueueMetrics
name|metrics
init|=
operator|(
name|CSQueueMetrics
operator|)
name|users
operator|.
name|get
argument_list|(
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|metrics
operator|==
literal|null
condition|)
block|{
name|metrics
operator|=
operator|new
name|CSQueueMetrics
argument_list|(
name|metricsSystem
argument_list|,
name|queueName
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|users
operator|.
name|put
argument_list|(
name|userName
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
name|metricsSystem
operator|.
name|register
argument_list|(
name|sourceName
argument_list|(
name|queueName
argument_list|)
operator|.
name|append
argument_list|(
literal|",user="
argument_list|)
operator|.
name|append
argument_list|(
name|userName
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Metrics for user '"
operator|+
name|userName
operator|+
literal|"' in queue '"
operator|+
name|queueName
operator|+
literal|"'"
argument_list|,
operator|(
operator|(
name|CSQueueMetrics
operator|)
name|metrics
operator|.
name|tag
argument_list|(
name|QUEUE_INFO
argument_list|,
name|queueName
argument_list|)
operator|)
operator|.
name|tag
argument_list|(
name|USER_INFO
argument_list|,
name|userName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|metrics
return|;
block|}
DECL|method|getGuaranteedCapacity ()
specifier|public
name|float
name|getGuaranteedCapacity
parameter_list|()
block|{
return|return
name|guaranteedCapacity
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getGuaranteedAbsoluteCapacity ()
specifier|public
name|float
name|getGuaranteedAbsoluteCapacity
parameter_list|()
block|{
return|return
name|guaranteedAbsoluteCapacity
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setGuaranteedCapacities (String partition, float capacity, float absoluteCapacity)
specifier|public
name|void
name|setGuaranteedCapacities
parameter_list|(
name|String
name|partition
parameter_list|,
name|float
name|capacity
parameter_list|,
name|float
name|absoluteCapacity
parameter_list|)
block|{
if|if
condition|(
name|partition
operator|==
literal|null
operator|||
name|partition
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|guaranteedCapacity
operator|.
name|set
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|guaranteedAbsoluteCapacity
operator|.
name|set
argument_list|(
name|absoluteCapacity
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMaxCapacity ()
specifier|public
name|float
name|getMaxCapacity
parameter_list|()
block|{
return|return
name|maxCapacity
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getMaxAbsoluteCapacity ()
specifier|public
name|float
name|getMaxAbsoluteCapacity
parameter_list|()
block|{
return|return
name|maxAbsoluteCapacity
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setMaxCapacities (String partition, float capacity, float absoluteCapacity)
specifier|public
name|void
name|setMaxCapacities
parameter_list|(
name|String
name|partition
parameter_list|,
name|float
name|capacity
parameter_list|,
name|float
name|absoluteCapacity
parameter_list|)
block|{
if|if
condition|(
name|partition
operator|==
literal|null
operator|||
name|partition
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|maxCapacity
operator|.
name|set
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|maxAbsoluteCapacity
operator|.
name|set
argument_list|(
name|absoluteCapacity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

