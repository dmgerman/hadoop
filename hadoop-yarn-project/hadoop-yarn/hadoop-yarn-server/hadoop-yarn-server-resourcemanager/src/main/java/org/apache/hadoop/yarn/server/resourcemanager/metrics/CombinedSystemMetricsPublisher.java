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
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * A metrics publisher that can publish for a collection of publishers.  */
end_comment

begin_class
DECL|class|CombinedSystemMetricsPublisher
specifier|public
class|class
name|CombinedSystemMetricsPublisher
implements|implements
name|SystemMetricsPublisher
block|{
DECL|field|publishers
specifier|private
name|Collection
argument_list|<
name|SystemMetricsPublisher
argument_list|>
name|publishers
decl_stmt|;
DECL|method|CombinedSystemMetricsPublisher (Collection<SystemMetricsPublisher> publishers)
specifier|public
name|CombinedSystemMetricsPublisher
parameter_list|(
name|Collection
argument_list|<
name|SystemMetricsPublisher
argument_list|>
name|publishers
parameter_list|)
block|{
name|this
operator|.
name|publishers
operator|=
name|publishers
expr_stmt|;
block|}
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
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|appCreated
argument_list|(
name|app
argument_list|,
name|createdTime
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|appLaunched (RMApp app, long launchTime)
annotation|@
name|Override
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
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|appLaunched
argument_list|(
name|app
argument_list|,
name|launchTime
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|appACLsUpdated
argument_list|(
name|app
argument_list|,
name|appViewACLs
argument_list|,
name|updatedTime
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|appUpdated (RMApp app, long updatedTime)
specifier|public
name|void
name|appUpdated
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|long
name|updatedTime
parameter_list|)
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|appUpdated
argument_list|(
name|app
argument_list|,
name|updatedTime
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|appStateUpdated
argument_list|(
name|app
argument_list|,
name|appState
argument_list|,
name|updatedTime
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|appFinished
argument_list|(
name|app
argument_list|,
name|state
argument_list|,
name|finishedTime
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|appAttemptRegistered
argument_list|(
name|appAttempt
argument_list|,
name|registeredTime
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|appAttemptFinished
argument_list|(
name|appAttempt
argument_list|,
name|appAttemtpState
argument_list|,
name|app
argument_list|,
name|finishedTime
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|containerCreated
argument_list|(
name|container
argument_list|,
name|createdTime
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
for|for
control|(
name|SystemMetricsPublisher
name|publisher
range|:
name|this
operator|.
name|publishers
control|)
block|{
name|publisher
operator|.
name|containerFinished
argument_list|(
name|container
argument_list|,
name|finishedTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

