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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|RMAppState
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
name|attempt
operator|.
name|RMAppAttempt
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
name|attempt
operator|.
name|RMAppAttemptState
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
name|rmcontainer
operator|.
name|RMContainer
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

begin_comment
comment|/**  * This class does nothing when any of the methods are invoked on  * SystemMetricsPublisher.  */
end_comment

begin_class
DECL|class|NoOpSystemMetricPublisher
specifier|public
class|class
name|NoOpSystemMetricPublisher
implements|implements
name|SystemMetricsPublisher
block|{
annotation|@
name|Override
DECL|method|appCreated (RMApp app, long createdTime)
specifier|public
name|void
name|appCreated
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|long
name|createdTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|appFinished (RMApp app, RMAppState state, long finishedTime)
specifier|public
name|void
name|appFinished
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|RMAppState
name|state
parameter_list|,
name|long
name|finishedTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|appACLsUpdated (RMApp app, String appViewACLs, long updatedTime)
specifier|public
name|void
name|appACLsUpdated
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|String
name|appViewACLs
parameter_list|,
name|long
name|updatedTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|appAttemptRegistered (RMAppAttempt appAttempt, long registeredTime)
specifier|public
name|void
name|appAttemptRegistered
parameter_list|(
name|RMAppAttempt
name|appAttempt
parameter_list|,
name|long
name|registeredTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|appAttemptFinished (RMAppAttempt appAttempt, RMAppAttemptState appAttemtpState, RMApp app, long finishedTime)
specifier|public
name|void
name|appAttemptFinished
parameter_list|(
name|RMAppAttempt
name|appAttempt
parameter_list|,
name|RMAppAttemptState
name|appAttemtpState
parameter_list|,
name|RMApp
name|app
parameter_list|,
name|long
name|finishedTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|containerCreated (RMContainer container, long createdTime)
specifier|public
name|void
name|containerCreated
parameter_list|(
name|RMContainer
name|container
parameter_list|,
name|long
name|createdTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|containerFinished (RMContainer container, long finishedTime)
specifier|public
name|void
name|containerFinished
parameter_list|(
name|RMContainer
name|container
parameter_list|,
name|long
name|finishedTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|appUpdated (RMApp app, long currentTimeMillis)
specifier|public
name|void
name|appUpdated
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|long
name|currentTimeMillis
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|appStateUpdated (RMApp app, YarnApplicationState appState, long updatedTime)
specifier|public
name|void
name|appStateUpdated
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|YarnApplicationState
name|appState
parameter_list|,
name|long
name|updatedTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|appLaunched (RMApp app, long launchTime)
specifier|public
name|void
name|appLaunched
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|long
name|launchTime
parameter_list|)
block|{   }
block|}
end_class

end_unit

