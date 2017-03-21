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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
import|;
end_import

begin_interface
annotation|@
name|Private
annotation|@
name|Unstable
DECL|interface|SchedulerWrapper
specifier|public
interface|interface
name|SchedulerWrapper
block|{
DECL|method|getMetrics ()
name|MetricRegistry
name|getMetrics
parameter_list|()
function_decl|;
DECL|method|getSchedulerMetrics ()
name|SchedulerMetrics
name|getSchedulerMetrics
parameter_list|()
function_decl|;
DECL|method|getQueueSet ()
name|Set
argument_list|<
name|String
argument_list|>
name|getQueueSet
parameter_list|()
function_decl|;
DECL|method|setQueueSet (Set<String> queues)
name|void
name|setQueueSet
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|queues
parameter_list|)
function_decl|;
DECL|method|getTrackedAppSet ()
name|Set
argument_list|<
name|String
argument_list|>
name|getTrackedAppSet
parameter_list|()
function_decl|;
DECL|method|setTrackedAppSet (Set<String> apps)
name|void
name|setTrackedAppSet
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|apps
parameter_list|)
function_decl|;
DECL|method|addTrackedApp (ApplicationId appId, String oldAppId)
name|void
name|addTrackedApp
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|oldAppId
parameter_list|)
function_decl|;
DECL|method|removeTrackedApp (String oldAppId)
name|void
name|removeTrackedApp
parameter_list|(
name|String
name|oldAppId
parameter_list|)
function_decl|;
DECL|method|addAMRuntime (ApplicationId appId, long traceStartTimeMS, long traceEndTimeMS, long simulateStartTimeMS, long simulateEndTimeMS)
name|void
name|addAMRuntime
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|long
name|traceStartTimeMS
parameter_list|,
name|long
name|traceEndTimeMS
parameter_list|,
name|long
name|simulateStartTimeMS
parameter_list|,
name|long
name|simulateEndTimeMS
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

