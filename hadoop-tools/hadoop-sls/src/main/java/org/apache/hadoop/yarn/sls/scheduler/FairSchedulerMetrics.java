begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.scheduler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|scheduler
package|;
end_package

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
name|ApplicationId
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
name|FSAppAttempt
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
name|FSQueue
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
name|FairScheduler
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
name|Schedulable
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
name|sls
operator|.
name|SLSRunner
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Gauge
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FairSchedulerMetrics
specifier|public
class|class
name|FairSchedulerMetrics
extends|extends
name|SchedulerMetrics
block|{
DECL|field|totalMemoryMB
specifier|private
name|int
name|totalMemoryMB
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|totalVCores
specifier|private
name|int
name|totalVCores
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|maxReset
specifier|private
name|boolean
name|maxReset
init|=
literal|false
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|enum|Metric
specifier|public
enum|enum
name|Metric
block|{
DECL|enumConstant|DEMAND
name|DEMAND
argument_list|(
literal|"demand"
argument_list|)
block|,
DECL|enumConstant|USAGE
name|USAGE
argument_list|(
literal|"usage"
argument_list|)
block|,
DECL|enumConstant|MINSHARE
name|MINSHARE
argument_list|(
literal|"minshare"
argument_list|)
block|,
DECL|enumConstant|MAXSHARE
name|MAXSHARE
argument_list|(
literal|"maxshare"
argument_list|)
block|,
DECL|enumConstant|FAIRSHARE
name|FAIRSHARE
argument_list|(
literal|"fairshare"
argument_list|)
block|;
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
DECL|method|Metric (String value)
name|Metric
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getValue ()
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|method|FairSchedulerMetrics ()
specifier|public
name|FairSchedulerMetrics
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
for|for
control|(
name|Metric
name|metric
range|:
name|Metric
operator|.
name|values
argument_list|()
control|)
block|{
name|appTrackedMetrics
operator|.
name|add
argument_list|(
name|metric
operator|.
name|value
operator|+
literal|".memory"
argument_list|)
expr_stmt|;
name|appTrackedMetrics
operator|.
name|add
argument_list|(
name|metric
operator|.
name|value
operator|+
literal|".vcores"
argument_list|)
expr_stmt|;
name|queueTrackedMetrics
operator|.
name|add
argument_list|(
name|metric
operator|.
name|value
operator|+
literal|".memory"
argument_list|)
expr_stmt|;
name|queueTrackedMetrics
operator|.
name|add
argument_list|(
name|metric
operator|.
name|value
operator|+
literal|".vcores"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMemorySize (Schedulable schedulable, Metric metric)
specifier|private
name|long
name|getMemorySize
parameter_list|(
name|Schedulable
name|schedulable
parameter_list|,
name|Metric
name|metric
parameter_list|)
block|{
if|if
condition|(
name|schedulable
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|metric
condition|)
block|{
case|case
name|DEMAND
case|:
return|return
name|schedulable
operator|.
name|getDemand
argument_list|()
operator|.
name|getMemorySize
argument_list|()
return|;
case|case
name|USAGE
case|:
return|return
name|schedulable
operator|.
name|getResourceUsage
argument_list|()
operator|.
name|getMemorySize
argument_list|()
return|;
case|case
name|MINSHARE
case|:
return|return
name|schedulable
operator|.
name|getMinShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
return|;
case|case
name|MAXSHARE
case|:
return|return
name|schedulable
operator|.
name|getMaxShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
return|;
case|case
name|FAIRSHARE
case|:
return|return
name|schedulable
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
return|;
default|default:
return|return
literal|0L
return|;
block|}
block|}
return|return
literal|0L
return|;
block|}
DECL|method|getVirtualCores (Schedulable schedulable, Metric metric)
specifier|private
name|int
name|getVirtualCores
parameter_list|(
name|Schedulable
name|schedulable
parameter_list|,
name|Metric
name|metric
parameter_list|)
block|{
if|if
condition|(
name|schedulable
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|metric
condition|)
block|{
case|case
name|DEMAND
case|:
return|return
name|schedulable
operator|.
name|getDemand
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
return|;
case|case
name|USAGE
case|:
return|return
name|schedulable
operator|.
name|getResourceUsage
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
return|;
case|case
name|MINSHARE
case|:
return|return
name|schedulable
operator|.
name|getMinShare
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
return|;
case|case
name|MAXSHARE
case|:
return|return
name|schedulable
operator|.
name|getMaxShare
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
return|;
case|case
name|FAIRSHARE
case|:
return|return
name|schedulable
operator|.
name|getFairShare
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
return|;
default|default:
return|return
literal|0
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|registerAppMetrics (ApplicationId appId, String oldAppId, Metric metric)
specifier|private
name|void
name|registerAppMetrics
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|oldAppId
parameter_list|,
name|Metric
name|metric
parameter_list|)
block|{
name|metrics
operator|.
name|register
argument_list|(
literal|"variable.app."
operator|+
name|oldAppId
operator|+
literal|"."
operator|+
name|metric
operator|.
name|value
operator|+
literal|".memory"
argument_list|,
operator|new
name|Gauge
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|getValue
parameter_list|()
block|{
return|return
name|getMemorySize
argument_list|(
operator|(
name|FSAppAttempt
operator|)
name|getSchedulerAppAttempt
argument_list|(
name|appId
argument_list|)
argument_list|,
name|metric
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|register
argument_list|(
literal|"variable.app."
operator|+
name|oldAppId
operator|+
literal|"."
operator|+
name|metric
operator|.
name|value
operator|+
literal|".vcores"
argument_list|,
operator|new
name|Gauge
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|getValue
parameter_list|()
block|{
return|return
name|getVirtualCores
argument_list|(
operator|(
name|FSAppAttempt
operator|)
name|getSchedulerAppAttempt
argument_list|(
name|appId
argument_list|)
argument_list|,
name|metric
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|trackApp (ApplicationId appId, String oldAppId)
specifier|public
name|void
name|trackApp
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|oldAppId
parameter_list|)
block|{
name|super
operator|.
name|trackApp
argument_list|(
name|appId
argument_list|,
name|oldAppId
argument_list|)
expr_stmt|;
for|for
control|(
name|Metric
name|metric
range|:
name|Metric
operator|.
name|values
argument_list|()
control|)
block|{
name|registerAppMetrics
argument_list|(
name|appId
argument_list|,
name|oldAppId
argument_list|,
name|metric
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|registerQueueMetrics (FSQueue queue, Metric metric)
specifier|private
name|void
name|registerQueueMetrics
parameter_list|(
name|FSQueue
name|queue
parameter_list|,
name|Metric
name|metric
parameter_list|)
block|{
name|metrics
operator|.
name|register
argument_list|(
literal|"variable.queue."
operator|+
name|queue
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|metric
operator|.
name|value
operator|+
literal|".memory"
argument_list|,
operator|new
name|Gauge
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|getValue
parameter_list|()
block|{
return|return
name|getMemorySize
argument_list|(
name|queue
argument_list|,
name|metric
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|register
argument_list|(
literal|"variable.queue."
operator|+
name|queue
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|metric
operator|.
name|value
operator|+
literal|".vcores"
argument_list|,
operator|new
name|Gauge
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|getValue
parameter_list|()
block|{
return|return
name|getVirtualCores
argument_list|(
name|queue
argument_list|,
name|metric
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerQueueMetrics (String queueName)
specifier|protected
name|void
name|registerQueueMetrics
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|super
operator|.
name|registerQueueMetrics
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
name|FairScheduler
name|fair
init|=
operator|(
name|FairScheduler
operator|)
name|scheduler
decl_stmt|;
specifier|final
name|FSQueue
name|queue
init|=
name|fair
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|registerQueueMetrics
argument_list|(
name|queue
argument_list|,
name|Metric
operator|.
name|DEMAND
argument_list|)
expr_stmt|;
name|registerQueueMetrics
argument_list|(
name|queue
argument_list|,
name|Metric
operator|.
name|USAGE
argument_list|)
expr_stmt|;
name|registerQueueMetrics
argument_list|(
name|queue
argument_list|,
name|Metric
operator|.
name|MINSHARE
argument_list|)
expr_stmt|;
name|registerQueueMetrics
argument_list|(
name|queue
argument_list|,
name|Metric
operator|.
name|FAIRSHARE
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|register
argument_list|(
literal|"variable.queue."
operator|+
name|queueName
operator|+
literal|".maxshare.memory"
argument_list|,
operator|new
name|Gauge
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|getValue
parameter_list|()
block|{
if|if
condition|(
operator|!
name|maxReset
operator|&&
name|SLSRunner
operator|.
name|getSimulateInfoMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"Number of nodes"
argument_list|)
operator|&&
name|SLSRunner
operator|.
name|getSimulateInfoMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"Node memory (MB)"
argument_list|)
operator|&&
name|SLSRunner
operator|.
name|getSimulateInfoMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"Node VCores"
argument_list|)
condition|)
block|{
name|int
name|numNMs
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|SLSRunner
operator|.
name|getSimulateInfoMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"Number of nodes"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numMemoryMB
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|SLSRunner
operator|.
name|getSimulateInfoMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"Node memory (MB)"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numVCores
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|SLSRunner
operator|.
name|getSimulateInfoMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"Node VCores"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|totalMemoryMB
operator|=
name|numNMs
operator|*
name|numMemoryMB
expr_stmt|;
name|totalVCores
operator|=
name|numNMs
operator|*
name|numVCores
expr_stmt|;
name|maxReset
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|min
argument_list|(
name|queue
operator|.
name|getMaxShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|totalMemoryMB
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|register
argument_list|(
literal|"variable.queue."
operator|+
name|queueName
operator|+
literal|".maxshare.vcores"
argument_list|,
operator|new
name|Gauge
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|getValue
parameter_list|()
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|queue
operator|.
name|getMaxShare
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|totalVCores
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

