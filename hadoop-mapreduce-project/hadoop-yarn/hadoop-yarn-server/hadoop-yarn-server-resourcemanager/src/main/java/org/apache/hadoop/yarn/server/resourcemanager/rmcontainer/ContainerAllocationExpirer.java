begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmcontainer
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
name|rmcontainer
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
name|yarn
operator|.
name|SystemClock
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
name|conf
operator|.
name|YarnConfiguration
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
name|event
operator|.
name|Dispatcher
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
name|event
operator|.
name|EventHandler
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
name|event
operator|.
name|ContainerExpiredSchedulerEvent
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
name|util
operator|.
name|AbstractLivelinessMonitor
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|ContainerAllocationExpirer
specifier|public
class|class
name|ContainerAllocationExpirer
extends|extends
name|AbstractLivelinessMonitor
argument_list|<
name|ContainerId
argument_list|>
block|{
DECL|field|dispatcher
specifier|private
name|EventHandler
name|dispatcher
decl_stmt|;
DECL|method|ContainerAllocationExpirer (Dispatcher d)
specifier|public
name|ContainerAllocationExpirer
parameter_list|(
name|Dispatcher
name|d
parameter_list|)
block|{
name|super
argument_list|(
name|ContainerAllocationExpirer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|SystemClock
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|d
operator|.
name|getEventHandler
argument_list|()
expr_stmt|;
block|}
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|expireIntvl
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
argument_list|)
decl_stmt|;
name|setExpireInterval
argument_list|(
name|expireIntvl
argument_list|)
expr_stmt|;
name|setMonitorInterval
argument_list|(
name|expireIntvl
operator|/
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expire (ContainerId containerId)
specifier|protected
name|void
name|expire
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|dispatcher
operator|.
name|handle
argument_list|(
operator|new
name|ContainerExpiredSchedulerEvent
argument_list|(
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

