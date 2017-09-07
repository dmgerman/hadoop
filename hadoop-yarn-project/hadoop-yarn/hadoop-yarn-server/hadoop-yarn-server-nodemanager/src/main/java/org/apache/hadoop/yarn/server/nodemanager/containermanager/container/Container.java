begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.container
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
name|containermanager
operator|.
name|container
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
name|fs
operator|.
name|Path
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
name|Credentials
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
name|ContainerLaunchContext
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
name|ContainerStatus
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
name|Priority
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
name|Resource
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|protocolrecords
operator|.
name|NMContainerStatus
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
name|containermanager
operator|.
name|localizer
operator|.
name|ResourceSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_interface
DECL|interface|Container
specifier|public
interface|interface
name|Container
extends|extends
name|EventHandler
argument_list|<
name|ContainerEvent
argument_list|>
block|{
DECL|method|getContainerId ()
name|ContainerId
name|getContainerId
parameter_list|()
function_decl|;
DECL|method|getContainerStartTime ()
name|long
name|getContainerStartTime
parameter_list|()
function_decl|;
DECL|method|getResource ()
name|Resource
name|getResource
parameter_list|()
function_decl|;
DECL|method|getContainerTokenIdentifier ()
name|ContainerTokenIdentifier
name|getContainerTokenIdentifier
parameter_list|()
function_decl|;
DECL|method|setContainerTokenIdentifier (ContainerTokenIdentifier token)
name|void
name|setContainerTokenIdentifier
parameter_list|(
name|ContainerTokenIdentifier
name|token
parameter_list|)
function_decl|;
DECL|method|getUser ()
name|String
name|getUser
parameter_list|()
function_decl|;
DECL|method|getContainerState ()
name|ContainerState
name|getContainerState
parameter_list|()
function_decl|;
DECL|method|getLaunchContext ()
name|ContainerLaunchContext
name|getLaunchContext
parameter_list|()
function_decl|;
DECL|method|getCredentials ()
name|Credentials
name|getCredentials
parameter_list|()
function_decl|;
DECL|method|getLocalizedResources ()
name|Map
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getLocalizedResources
parameter_list|()
function_decl|;
DECL|method|cloneAndGetContainerStatus ()
name|ContainerStatus
name|cloneAndGetContainerStatus
parameter_list|()
function_decl|;
DECL|method|getNMContainerStatus ()
name|NMContainerStatus
name|getNMContainerStatus
parameter_list|()
function_decl|;
DECL|method|isRetryContextSet ()
name|boolean
name|isRetryContextSet
parameter_list|()
function_decl|;
DECL|method|shouldRetry (int errorCode)
name|boolean
name|shouldRetry
parameter_list|(
name|int
name|errorCode
parameter_list|)
function_decl|;
DECL|method|getWorkDir ()
name|String
name|getWorkDir
parameter_list|()
function_decl|;
DECL|method|setWorkDir (String workDir)
name|void
name|setWorkDir
parameter_list|(
name|String
name|workDir
parameter_list|)
function_decl|;
DECL|method|getLogDir ()
name|String
name|getLogDir
parameter_list|()
function_decl|;
DECL|method|setLogDir (String logDir)
name|void
name|setLogDir
parameter_list|(
name|String
name|logDir
parameter_list|)
function_decl|;
DECL|method|setIpAndHost (String[] ipAndHost)
name|void
name|setIpAndHost
parameter_list|(
name|String
index|[]
name|ipAndHost
parameter_list|)
function_decl|;
DECL|method|toString ()
name|String
name|toString
parameter_list|()
function_decl|;
DECL|method|getPriority ()
name|Priority
name|getPriority
parameter_list|()
function_decl|;
DECL|method|getResourceSet ()
name|ResourceSet
name|getResourceSet
parameter_list|()
function_decl|;
DECL|method|isRunning ()
name|boolean
name|isRunning
parameter_list|()
function_decl|;
DECL|method|setIsReInitializing (boolean isReInitializing)
name|void
name|setIsReInitializing
parameter_list|(
name|boolean
name|isReInitializing
parameter_list|)
function_decl|;
DECL|method|isReInitializing ()
name|boolean
name|isReInitializing
parameter_list|()
function_decl|;
DECL|method|isMarkedForKilling ()
name|boolean
name|isMarkedForKilling
parameter_list|()
function_decl|;
DECL|method|canRollback ()
name|boolean
name|canRollback
parameter_list|()
function_decl|;
DECL|method|commitUpgrade ()
name|void
name|commitUpgrade
parameter_list|()
function_decl|;
DECL|method|sendLaunchEvent ()
name|void
name|sendLaunchEvent
parameter_list|()
function_decl|;
DECL|method|sendKillEvent (int exitStatus, String description)
name|void
name|sendKillEvent
parameter_list|(
name|int
name|exitStatus
parameter_list|,
name|String
name|description
parameter_list|)
function_decl|;
DECL|method|isRecovering ()
name|boolean
name|isRecovering
parameter_list|()
function_decl|;
comment|/**    * Get assigned resource mappings to the container.    *    * @return Resource Mappings of the container    */
DECL|method|getResourceMappings ()
name|ResourceMappings
name|getResourceMappings
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

