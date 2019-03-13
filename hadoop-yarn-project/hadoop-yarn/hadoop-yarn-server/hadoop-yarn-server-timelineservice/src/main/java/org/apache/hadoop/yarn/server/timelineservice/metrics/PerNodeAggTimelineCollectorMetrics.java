begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.metrics
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
name|timelineservice
operator|.
name|metrics
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
name|metrics2
operator|.
name|MetricsInfo
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
name|MutableQuantiles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import static
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
name|Interns
operator|.
name|info
import|;
end_import

begin_comment
comment|/**  * Metrics class for TimelineCollectorWebService  * running on each NM.  */
end_comment

begin_class
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"Aggregated metrics of TimelineCollector's running on each NM"
argument_list|,
name|context
operator|=
literal|"timelineservice"
argument_list|)
DECL|class|PerNodeAggTimelineCollectorMetrics
specifier|final
specifier|public
class|class
name|PerNodeAggTimelineCollectorMetrics
block|{
DECL|field|METRICS_INFO
specifier|private
specifier|static
specifier|final
name|MetricsInfo
name|METRICS_INFO
init|=
name|info
argument_list|(
literal|"PerNodeAggTimelineCollectorMetrics"
argument_list|,
literal|"Aggregated Metrics for TimelineCollector's running on each NM"
argument_list|)
decl_stmt|;
DECL|field|isInitialized
specifier|private
specifier|static
name|AtomicBoolean
name|isInitialized
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|PerNodeAggTimelineCollectorMetrics
DECL|field|instance
name|instance
init|=
literal|null
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|about
operator|=
literal|"PUT entities failure latency"
argument_list|,
name|valueName
operator|=
literal|"latency"
argument_list|)
DECL|field|putEntitiesFailureLatency
specifier|private
name|MutableQuantiles
name|putEntitiesFailureLatency
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|about
operator|=
literal|"PUT entities success latency"
argument_list|,
name|valueName
operator|=
literal|"latency"
argument_list|)
DECL|field|putEntitiesSuccessLatency
specifier|private
name|MutableQuantiles
name|putEntitiesSuccessLatency
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|about
operator|=
literal|"async PUT entities failure latency"
argument_list|,
name|valueName
operator|=
literal|"latency"
argument_list|)
DECL|field|asyncPutEntitiesFailureLatency
specifier|private
name|MutableQuantiles
name|asyncPutEntitiesFailureLatency
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|about
operator|=
literal|"async PUT entities success latency"
argument_list|,
name|valueName
operator|=
literal|"latency"
argument_list|)
DECL|field|asyncPutEntitiesSuccessLatency
specifier|private
name|MutableQuantiles
name|asyncPutEntitiesSuccessLatency
decl_stmt|;
DECL|method|PerNodeAggTimelineCollectorMetrics ()
specifier|private
name|PerNodeAggTimelineCollectorMetrics
parameter_list|()
block|{   }
DECL|method|getInstance ()
specifier|public
specifier|static
name|PerNodeAggTimelineCollectorMetrics
name|getInstance
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isInitialized
operator|.
name|get
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|PerNodeAggTimelineCollectorMetrics
operator|.
name|class
init|)
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
operator|.
name|register
argument_list|(
name|METRICS_INFO
operator|.
name|name
argument_list|()
argument_list|,
name|METRICS_INFO
operator|.
name|description
argument_list|()
argument_list|,
operator|new
name|PerNodeAggTimelineCollectorMetrics
argument_list|()
argument_list|)
expr_stmt|;
name|isInitialized
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|instance
return|;
block|}
DECL|method|destroy ()
specifier|public
specifier|synchronized
specifier|static
name|void
name|destroy
parameter_list|()
block|{
name|isInitialized
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|instance
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPutEntitiesSuccessLatency ()
specifier|public
name|MutableQuantiles
name|getPutEntitiesSuccessLatency
parameter_list|()
block|{
return|return
name|putEntitiesSuccessLatency
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPutEntitiesFailureLatency ()
specifier|public
name|MutableQuantiles
name|getPutEntitiesFailureLatency
parameter_list|()
block|{
return|return
name|putEntitiesFailureLatency
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAsyncPutEntitiesSuccessLatency ()
specifier|public
name|MutableQuantiles
name|getAsyncPutEntitiesSuccessLatency
parameter_list|()
block|{
return|return
name|asyncPutEntitiesSuccessLatency
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAsyncPutEntitiesFailureLatency ()
specifier|public
name|MutableQuantiles
name|getAsyncPutEntitiesFailureLatency
parameter_list|()
block|{
return|return
name|asyncPutEntitiesFailureLatency
return|;
block|}
DECL|method|addPutEntitiesLatency ( long durationMs, boolean succeeded)
specifier|public
name|void
name|addPutEntitiesLatency
parameter_list|(
name|long
name|durationMs
parameter_list|,
name|boolean
name|succeeded
parameter_list|)
block|{
if|if
condition|(
name|succeeded
condition|)
block|{
name|putEntitiesSuccessLatency
operator|.
name|add
argument_list|(
name|durationMs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|putEntitiesFailureLatency
operator|.
name|add
argument_list|(
name|durationMs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addAsyncPutEntitiesLatency ( long durationMs, boolean succeeded)
specifier|public
name|void
name|addAsyncPutEntitiesLatency
parameter_list|(
name|long
name|durationMs
parameter_list|,
name|boolean
name|succeeded
parameter_list|)
block|{
if|if
condition|(
name|succeeded
condition|)
block|{
name|asyncPutEntitiesSuccessLatency
operator|.
name|add
argument_list|(
name|durationMs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|asyncPutEntitiesFailureLatency
operator|.
name|add
argument_list|(
name|durationMs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

