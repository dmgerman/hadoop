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

begin_class
annotation|@
name|Metrics
argument_list|(
name|context
operator|=
literal|"yarn"
argument_list|)
DECL|class|FSQueueMetrics
specifier|public
class|class
name|FSQueueMetrics
extends|extends
name|QueueMetrics
block|{
DECL|field|fairShareMB
annotation|@
name|Metric
argument_list|(
literal|"Fair share of memory in MB"
argument_list|)
name|MutableGaugeLong
name|fairShareMB
decl_stmt|;
DECL|field|fairShareVCores
annotation|@
name|Metric
argument_list|(
literal|"Fair share of CPU in vcores"
argument_list|)
name|MutableGaugeLong
name|fairShareVCores
decl_stmt|;
DECL|field|steadyFairShareMB
annotation|@
name|Metric
argument_list|(
literal|"Steady fair share of memory in MB"
argument_list|)
name|MutableGaugeLong
name|steadyFairShareMB
decl_stmt|;
DECL|field|steadyFairShareVCores
annotation|@
name|Metric
argument_list|(
literal|"Steady fair share of CPU in vcores"
argument_list|)
name|MutableGaugeLong
name|steadyFairShareVCores
decl_stmt|;
DECL|field|minShareMB
annotation|@
name|Metric
argument_list|(
literal|"Minimum share of memory in MB"
argument_list|)
name|MutableGaugeLong
name|minShareMB
decl_stmt|;
DECL|field|minShareVCores
annotation|@
name|Metric
argument_list|(
literal|"Minimum share of CPU in vcores"
argument_list|)
name|MutableGaugeLong
name|minShareVCores
decl_stmt|;
DECL|field|maxShareMB
annotation|@
name|Metric
argument_list|(
literal|"Maximum share of memory in MB"
argument_list|)
name|MutableGaugeLong
name|maxShareMB
decl_stmt|;
DECL|field|maxShareVCores
annotation|@
name|Metric
argument_list|(
literal|"Maximum share of CPU in vcores"
argument_list|)
name|MutableGaugeLong
name|maxShareVCores
decl_stmt|;
DECL|field|maxApps
annotation|@
name|Metric
argument_list|(
literal|"Maximum number of applications"
argument_list|)
name|MutableGaugeInt
name|maxApps
decl_stmt|;
DECL|field|maxAMShareMB
annotation|@
name|Metric
argument_list|(
literal|"Maximum AM share of memory in MB"
argument_list|)
name|MutableGaugeLong
name|maxAMShareMB
decl_stmt|;
DECL|field|maxAMShareVCores
annotation|@
name|Metric
argument_list|(
literal|"Maximum AM share of CPU in vcores"
argument_list|)
name|MutableGaugeInt
name|maxAMShareVCores
decl_stmt|;
DECL|field|amResourceUsageMB
annotation|@
name|Metric
argument_list|(
literal|"AM resource usage of memory in MB"
argument_list|)
name|MutableGaugeLong
name|amResourceUsageMB
decl_stmt|;
DECL|field|amResourceUsageVCores
annotation|@
name|Metric
argument_list|(
literal|"AM resource usage of CPU in vcores"
argument_list|)
name|MutableGaugeInt
name|amResourceUsageVCores
decl_stmt|;
DECL|field|schedulingPolicy
specifier|private
name|String
name|schedulingPolicy
decl_stmt|;
DECL|method|FSQueueMetrics (MetricsSystem ms, String queueName, Queue parent, boolean enableUserMetrics, Configuration conf)
name|FSQueueMetrics
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
DECL|method|setFairShare (Resource resource)
specifier|public
name|void
name|setFairShare
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|fairShareMB
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|fairShareVCores
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getFairShareMB ()
specifier|public
name|long
name|getFairShareMB
parameter_list|()
block|{
return|return
name|fairShareMB
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getFairShareVirtualCores ()
specifier|public
name|long
name|getFairShareVirtualCores
parameter_list|()
block|{
return|return
name|fairShareVCores
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setSteadyFairShare (Resource resource)
specifier|public
name|void
name|setSteadyFairShare
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|steadyFairShareMB
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|steadyFairShareVCores
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getSteadyFairShareMB ()
specifier|public
name|long
name|getSteadyFairShareMB
parameter_list|()
block|{
return|return
name|steadyFairShareMB
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getSteadyFairShareVCores ()
specifier|public
name|long
name|getSteadyFairShareVCores
parameter_list|()
block|{
return|return
name|steadyFairShareVCores
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setMinShare (Resource resource)
specifier|public
name|void
name|setMinShare
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|minShareMB
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|minShareVCores
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getMinShareMB ()
specifier|public
name|long
name|getMinShareMB
parameter_list|()
block|{
return|return
name|minShareMB
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getMinShareVirtualCores ()
specifier|public
name|long
name|getMinShareVirtualCores
parameter_list|()
block|{
return|return
name|minShareVCores
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setMaxShare (Resource resource)
specifier|public
name|void
name|setMaxShare
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|maxShareMB
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|maxShareVCores
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getMaxShareMB ()
specifier|public
name|long
name|getMaxShareMB
parameter_list|()
block|{
return|return
name|maxShareMB
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getMaxShareVirtualCores ()
specifier|public
name|long
name|getMaxShareVirtualCores
parameter_list|()
block|{
return|return
name|maxShareVCores
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getMaxApps ()
specifier|public
name|int
name|getMaxApps
parameter_list|()
block|{
return|return
name|maxApps
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|setMaxApps (int max)
specifier|public
name|void
name|setMaxApps
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|maxApps
operator|.
name|set
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the maximum memory size AM can use in MB.    *    * @return the maximum memory size AM can use    */
DECL|method|getMaxAMShareMB ()
specifier|public
name|long
name|getMaxAMShareMB
parameter_list|()
block|{
return|return
name|maxAMShareMB
operator|.
name|value
argument_list|()
return|;
block|}
comment|/**    * Get the maximum number of VCores AM can use.    *    * @return the maximum number of VCores AM can use    */
DECL|method|getMaxAMShareVCores ()
specifier|public
name|int
name|getMaxAMShareVCores
parameter_list|()
block|{
return|return
name|maxAMShareVCores
operator|.
name|value
argument_list|()
return|;
block|}
comment|/**    * Set the maximum resource AM can use.    *    * @param resource the maximum resource AM can use    */
DECL|method|setMaxAMShare (Resource resource)
specifier|public
name|void
name|setMaxAMShare
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|maxAMShareMB
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|maxAMShareVCores
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the AM memory usage in MB.    *    * @return the AM memory usage    */
DECL|method|getAMResourceUsageMB ()
specifier|public
name|long
name|getAMResourceUsageMB
parameter_list|()
block|{
return|return
name|amResourceUsageMB
operator|.
name|value
argument_list|()
return|;
block|}
comment|/**    * Get the AM VCore usage.    *    * @return the AM VCore usage    */
DECL|method|getAMResourceUsageVCores ()
specifier|public
name|int
name|getAMResourceUsageVCores
parameter_list|()
block|{
return|return
name|amResourceUsageVCores
operator|.
name|value
argument_list|()
return|;
block|}
comment|/**    * Set the AM resource usage.    *    * @param resource the AM resource usage    */
DECL|method|setAMResourceUsage (Resource resource)
specifier|public
name|void
name|setAMResourceUsage
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|amResourceUsageMB
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|amResourceUsageVCores
operator|.
name|set
argument_list|(
name|resource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getSchedulingPolicy ()
specifier|public
name|String
name|getSchedulingPolicy
parameter_list|()
block|{
return|return
name|schedulingPolicy
return|;
block|}
DECL|method|setSchedulingPolicy (String policy)
specifier|public
name|void
name|setSchedulingPolicy
parameter_list|(
name|String
name|policy
parameter_list|)
block|{
name|schedulingPolicy
operator|=
name|policy
expr_stmt|;
block|}
specifier|public
specifier|synchronized
DECL|method|forQueue (String queueName, Queue parent, boolean enableUserMetrics, Configuration conf)
specifier|static
name|FSQueueMetrics
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
name|queueMetrics
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
name|FSQueueMetrics
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
name|queueMetrics
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
name|FSQueueMetrics
operator|)
name|metrics
return|;
block|}
block|}
end_class

end_unit

