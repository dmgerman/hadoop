begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.security.authorize
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
name|nodemanager
operator|.
name|security
operator|.
name|authorize
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
name|classification
operator|.
name|InterfaceAudience
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
name|authorize
operator|.
name|PolicyProvider
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
name|authorize
operator|.
name|Service
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
name|ContainerManagementProtocolPB
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
name|server
operator|.
name|api
operator|.
name|CollectorNodemanagerProtocolPB
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
name|nodemanager
operator|.
name|api
operator|.
name|LocalizationProtocolPB
import|;
end_import

begin_comment
comment|/**  * {@link PolicyProvider} for YARN NodeManager protocols.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|NMPolicyProvider
specifier|public
class|class
name|NMPolicyProvider
extends|extends
name|PolicyProvider
block|{
DECL|field|NODE_MANAGER_SERVICES
specifier|private
specifier|static
specifier|final
name|Service
index|[]
name|NODE_MANAGER_SERVICES
init|=
operator|new
name|Service
index|[]
block|{
operator|new
name|Service
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_CONTAINER_MANAGEMENT_PROTOCOL
argument_list|,
name|ContainerManagementProtocolPB
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCE_LOCALIZER
argument_list|,
name|LocalizationProtocolPB
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_COLLECTOR_NODEMANAGER_PROTOCOL
argument_list|,
name|CollectorNodemanagerProtocolPB
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|getServices ()
specifier|public
name|Service
index|[]
name|getServices
parameter_list|()
block|{
return|return
name|NODE_MANAGER_SERVICES
return|;
block|}
block|}
end_class

end_unit

