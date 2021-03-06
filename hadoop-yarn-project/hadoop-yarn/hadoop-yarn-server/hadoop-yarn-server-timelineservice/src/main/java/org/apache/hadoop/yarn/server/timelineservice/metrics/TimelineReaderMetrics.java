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
comment|/**  * Metrics class for TimelineReader.  */
end_comment

begin_class
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"Metrics for timeline reader"
argument_list|,
name|context
operator|=
literal|"timelineservice"
argument_list|)
DECL|class|TimelineReaderMetrics
specifier|public
class|class
name|TimelineReaderMetrics
block|{
DECL|field|METRICS_INFO
specifier|private
specifier|final
specifier|static
name|MetricsInfo
name|METRICS_INFO
init|=
name|info
argument_list|(
literal|"TimelineReaderMetrics"
argument_list|,
literal|"Metrics for TimelineReader"
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
DECL|field|instance
specifier|private
specifier|static
name|TimelineReaderMetrics
name|instance
init|=
literal|null
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|about
operator|=
literal|"GET entities failure latency"
argument_list|,
name|valueName
operator|=
literal|"latency"
argument_list|)
DECL|field|getEntitiesFailureLatency
specifier|private
name|MutableQuantiles
name|getEntitiesFailureLatency
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|about
operator|=
literal|"GET entities success latency"
argument_list|,
name|valueName
operator|=
literal|"latency"
argument_list|)
DECL|field|getEntitiesSuccessLatency
specifier|private
name|MutableQuantiles
name|getEntitiesSuccessLatency
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|about
operator|=
literal|"GET entity types failure latency"
argument_list|,
name|valueName
operator|=
literal|"latency"
argument_list|)
DECL|field|getEntityTypesFailureLatency
specifier|private
name|MutableQuantiles
name|getEntityTypesFailureLatency
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|about
operator|=
literal|"GET entity types success latency"
argument_list|,
name|valueName
operator|=
literal|"latency"
argument_list|)
DECL|field|getEntityTypesSuccessLatency
specifier|private
name|MutableQuantiles
name|getEntityTypesSuccessLatency
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|TimelineReaderMetrics ()
specifier|protected
name|TimelineReaderMetrics
parameter_list|()
block|{   }
DECL|method|getInstance ()
specifier|public
specifier|static
name|TimelineReaderMetrics
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
name|TimelineReaderMetrics
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
name|initialize
argument_list|(
literal|"TimelineService"
argument_list|)
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
name|TimelineReaderMetrics
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
DECL|method|getGetEntitiesSuccessLatency ()
specifier|public
name|MutableQuantiles
name|getGetEntitiesSuccessLatency
parameter_list|()
block|{
return|return
name|getEntitiesSuccessLatency
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getGetEntitiesFailureLatency ()
specifier|public
name|MutableQuantiles
name|getGetEntitiesFailureLatency
parameter_list|()
block|{
return|return
name|getEntitiesFailureLatency
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getGetEntityTypesSuccessLatency ()
specifier|public
name|MutableQuantiles
name|getGetEntityTypesSuccessLatency
parameter_list|()
block|{
return|return
name|getEntityTypesSuccessLatency
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getGetEntityTypesFailureLatency ()
specifier|public
name|MutableQuantiles
name|getGetEntityTypesFailureLatency
parameter_list|()
block|{
return|return
name|getEntityTypesFailureLatency
return|;
block|}
DECL|method|addGetEntitiesLatency ( long durationMs, boolean succeeded)
specifier|public
name|void
name|addGetEntitiesLatency
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
name|getEntitiesSuccessLatency
operator|.
name|add
argument_list|(
name|durationMs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getEntitiesFailureLatency
operator|.
name|add
argument_list|(
name|durationMs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addGetEntityTypesLatency ( long durationMs, boolean succeeded)
specifier|public
name|void
name|addGetEntityTypesLatency
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
name|getEntityTypesSuccessLatency
operator|.
name|add
argument_list|(
name|durationMs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getEntityTypesFailureLatency
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

