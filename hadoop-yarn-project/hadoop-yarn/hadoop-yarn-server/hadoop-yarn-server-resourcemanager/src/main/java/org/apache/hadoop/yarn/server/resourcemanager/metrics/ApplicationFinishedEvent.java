begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.metrics
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
name|metrics
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|api
operator|.
name|records
operator|.
name|FinalApplicationStatus
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
name|YarnApplicationState
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
name|rmapp
operator|.
name|RMAppMetrics
import|;
end_import

begin_class
DECL|class|ApplicationFinishedEvent
specifier|public
class|class
name|ApplicationFinishedEvent
extends|extends
name|SystemMetricsEvent
block|{
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
decl_stmt|;
empty_stmt|;
DECL|field|diagnosticsInfo
specifier|private
name|String
name|diagnosticsInfo
decl_stmt|;
DECL|field|appStatus
specifier|private
name|FinalApplicationStatus
name|appStatus
decl_stmt|;
DECL|field|state
specifier|private
name|YarnApplicationState
name|state
decl_stmt|;
DECL|field|latestAppAttemptId
specifier|private
name|ApplicationAttemptId
name|latestAppAttemptId
decl_stmt|;
DECL|field|appMetrics
specifier|private
name|RMAppMetrics
name|appMetrics
decl_stmt|;
DECL|method|ApplicationFinishedEvent ( ApplicationId appId, String diagnosticsInfo, FinalApplicationStatus appStatus, YarnApplicationState state, ApplicationAttemptId latestAppAttemptId, long finishedTime, RMAppMetrics appMetrics)
specifier|public
name|ApplicationFinishedEvent
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|diagnosticsInfo
parameter_list|,
name|FinalApplicationStatus
name|appStatus
parameter_list|,
name|YarnApplicationState
name|state
parameter_list|,
name|ApplicationAttemptId
name|latestAppAttemptId
parameter_list|,
name|long
name|finishedTime
parameter_list|,
name|RMAppMetrics
name|appMetrics
parameter_list|)
block|{
name|super
argument_list|(
name|SystemMetricsEventType
operator|.
name|APP_FINISHED
argument_list|,
name|finishedTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|diagnosticsInfo
operator|=
name|diagnosticsInfo
expr_stmt|;
name|this
operator|.
name|appStatus
operator|=
name|appStatus
expr_stmt|;
name|this
operator|.
name|latestAppAttemptId
operator|=
name|latestAppAttemptId
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|appMetrics
operator|=
name|appMetrics
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|appId
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|getDiagnosticsInfo ()
specifier|public
name|String
name|getDiagnosticsInfo
parameter_list|()
block|{
return|return
name|diagnosticsInfo
return|;
block|}
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
return|return
name|appStatus
return|;
block|}
DECL|method|getYarnApplicationState ()
specifier|public
name|YarnApplicationState
name|getYarnApplicationState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getLatestApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getLatestApplicationAttemptId
parameter_list|()
block|{
return|return
name|latestAppAttemptId
return|;
block|}
DECL|method|getAppMetrics ()
specifier|public
name|RMAppMetrics
name|getAppMetrics
parameter_list|()
block|{
return|return
name|appMetrics
return|;
block|}
block|}
end_class

end_unit

