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
name|MutableGaugeInt
name|fairShareMB
decl_stmt|;
DECL|field|fairShareVCores
annotation|@
name|Metric
argument_list|(
literal|"Fair share of CPU in vcores"
argument_list|)
name|MutableGaugeInt
name|fairShareVCores
decl_stmt|;
DECL|field|minShareMB
annotation|@
name|Metric
argument_list|(
literal|"Minimum share of memory in MB"
argument_list|)
name|MutableGaugeInt
name|minShareMB
decl_stmt|;
DECL|field|minShareVCores
annotation|@
name|Metric
argument_list|(
literal|"Minimum share of CPU in vcores"
argument_list|)
name|MutableGaugeInt
name|minShareVCores
decl_stmt|;
DECL|field|maxShareMB
annotation|@
name|Metric
argument_list|(
literal|"Maximum share of memory in MB"
argument_list|)
name|MutableGaugeInt
name|maxShareMB
decl_stmt|;
DECL|field|maxShareVCores
annotation|@
name|Metric
argument_list|(
literal|"Maximum share of CPU in vcores"
argument_list|)
name|MutableGaugeInt
name|maxShareVCores
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
name|getMemory
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
name|int
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
name|int
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
name|getMemory
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
name|int
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
name|int
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
name|getMemory
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
name|int
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
name|int
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

