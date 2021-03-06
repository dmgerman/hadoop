begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.security.authorize
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|ha
operator|.
name|HAServiceProtocol
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
name|ApplicationMasterProtocolPB
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
name|ApplicationClientProtocolPB
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
name|DistributedSchedulingAMProtocolPB
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
name|ResourceManagerAdministrationProtocolPB
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
name|ResourceTrackerPB
import|;
end_import

begin_comment
comment|/**  * {@link PolicyProvider} for YARN ResourceManager protocols.  */
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
DECL|class|RMPolicyProvider
specifier|public
class|class
name|RMPolicyProvider
extends|extends
name|PolicyProvider
block|{
DECL|field|rmPolicyProvider
specifier|private
specifier|static
name|RMPolicyProvider
name|rmPolicyProvider
init|=
literal|null
decl_stmt|;
DECL|method|RMPolicyProvider ()
specifier|private
name|RMPolicyProvider
parameter_list|()
block|{}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getInstance ()
specifier|public
specifier|static
name|RMPolicyProvider
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|rmPolicyProvider
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|RMPolicyProvider
operator|.
name|class
init|)
block|{
if|if
condition|(
name|rmPolicyProvider
operator|==
literal|null
condition|)
block|{
name|rmPolicyProvider
operator|=
operator|new
name|RMPolicyProvider
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|rmPolicyProvider
return|;
block|}
DECL|field|resourceManagerServices
specifier|private
specifier|static
specifier|final
name|Service
index|[]
name|resourceManagerServices
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
name|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCETRACKER_PROTOCOL
argument_list|,
name|ResourceTrackerPB
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONCLIENT_PROTOCOL
argument_list|,
name|ApplicationClientProtocolPB
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONMASTER_PROTOCOL
argument_list|,
name|ApplicationMasterProtocolPB
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_DISTRIBUTEDSCHEDULING_PROTOCOL
argument_list|,
name|DistributedSchedulingAMProtocolPB
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCEMANAGER_ADMINISTRATION_PROTOCOL
argument_list|,
name|ResourceManagerAdministrationProtocolPB
operator|.
name|class
argument_list|)
block|,
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
name|CommonConfigurationKeys
operator|.
name|SECURITY_HA_SERVICE_PROTOCOL_ACL
argument_list|,
name|HAServiceProtocol
operator|.
name|class
argument_list|)
block|,   }
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
name|resourceManagerServices
return|;
block|}
block|}
end_class

end_unit

