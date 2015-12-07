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
name|ContainerId
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
name|YarnApplicationAttemptState
import|;
end_import

begin_class
DECL|class|AppAttemptFinishedEvent
specifier|public
class|class
name|AppAttemptFinishedEvent
extends|extends
name|SystemMetricsEvent
block|{
DECL|field|appAttemptId
specifier|private
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
DECL|field|trackingUrl
specifier|private
name|String
name|trackingUrl
decl_stmt|;
DECL|field|originalTrackingUrl
specifier|private
name|String
name|originalTrackingUrl
decl_stmt|;
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
name|YarnApplicationAttemptState
name|state
decl_stmt|;
DECL|field|masterContainerId
specifier|private
name|ContainerId
name|masterContainerId
decl_stmt|;
DECL|method|AppAttemptFinishedEvent ( ApplicationAttemptId appAttemptId, String trackingUrl, String originalTrackingUrl, String diagnosticsInfo, FinalApplicationStatus appStatus, YarnApplicationAttemptState state, long finishedTime, ContainerId masterContainerId)
specifier|public
name|AppAttemptFinishedEvent
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|String
name|trackingUrl
parameter_list|,
name|String
name|originalTrackingUrl
parameter_list|,
name|String
name|diagnosticsInfo
parameter_list|,
name|FinalApplicationStatus
name|appStatus
parameter_list|,
name|YarnApplicationAttemptState
name|state
parameter_list|,
name|long
name|finishedTime
parameter_list|,
name|ContainerId
name|masterContainerId
parameter_list|)
block|{
name|super
argument_list|(
name|SystemMetricsEventType
operator|.
name|APP_ATTEMPT_FINISHED
argument_list|,
name|finishedTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|appAttemptId
operator|=
name|appAttemptId
expr_stmt|;
comment|// This is the tracking URL after the application attempt is finished
name|this
operator|.
name|trackingUrl
operator|=
name|trackingUrl
expr_stmt|;
name|this
operator|.
name|originalTrackingUrl
operator|=
name|originalTrackingUrl
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
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|masterContainerId
operator|=
name|masterContainerId
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
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|appAttemptId
return|;
block|}
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
return|return
name|trackingUrl
return|;
block|}
DECL|method|getOriginalTrackingURL ()
specifier|public
name|String
name|getOriginalTrackingURL
parameter_list|()
block|{
return|return
name|originalTrackingUrl
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
DECL|method|getYarnApplicationAttemptState ()
specifier|public
name|YarnApplicationAttemptState
name|getYarnApplicationAttemptState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getMasterContainerId ()
specifier|public
name|ContainerId
name|getMasterContainerId
parameter_list|()
block|{
return|return
name|masterContainerId
return|;
block|}
block|}
end_class

end_unit

