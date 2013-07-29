begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|security
operator|.
name|SecurityUtil
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|ContainerManagementProtocol
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
name|security
operator|.
name|AMRMTokenIdentifier
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
name|amlauncher
operator|.
name|AMLauncher
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
name|amlauncher
operator|.
name|AMLauncherEventType
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
name|amlauncher
operator|.
name|ApplicationMasterLauncher
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

begin_class
DECL|class|MockRMWithCustomAMLauncher
specifier|public
class|class
name|MockRMWithCustomAMLauncher
extends|extends
name|MockRM
block|{
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManagementProtocol
name|containerManager
decl_stmt|;
DECL|method|MockRMWithCustomAMLauncher (ContainerManagementProtocol containerManager)
specifier|public
name|MockRMWithCustomAMLauncher
parameter_list|(
name|ContainerManagementProtocol
name|containerManager
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|containerManager
argument_list|)
expr_stmt|;
block|}
DECL|method|MockRMWithCustomAMLauncher (Configuration conf, ContainerManagementProtocol containerManager)
specifier|public
name|MockRMWithCustomAMLauncher
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ContainerManagementProtocol
name|containerManager
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAMLauncher ()
specifier|protected
name|ApplicationMasterLauncher
name|createAMLauncher
parameter_list|()
block|{
return|return
operator|new
name|ApplicationMasterLauncher
argument_list|(
name|getRMContext
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Runnable
name|createRunnableLauncher
parameter_list|(
name|RMAppAttempt
name|application
parameter_list|,
name|AMLauncherEventType
name|event
parameter_list|)
block|{
return|return
operator|new
name|AMLauncher
argument_list|(
name|context
argument_list|,
name|application
argument_list|,
name|event
argument_list|,
name|getConfig
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ContainerManagementProtocol
name|getContainerMgrProxy
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
return|return
name|containerManager
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|getAMRMToken
parameter_list|()
block|{
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|amRmToken
init|=
name|super
operator|.
name|getAMRMToken
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|serviceAddr
init|=
name|getConfig
argument_list|()
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_PORT
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|amRmToken
argument_list|,
name|serviceAddr
argument_list|)
expr_stmt|;
return|return
name|amRmToken
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

