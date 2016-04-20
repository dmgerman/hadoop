begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentLinkedQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|NodeId
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
name|LogAggregationReport
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
name|records
operator|.
name|NodeHealthStatus
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
name|application
operator|.
name|Application
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
name|container
operator|.
name|Container
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
name|recovery
operator|.
name|NMStateStoreService
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
name|security
operator|.
name|NMContainerTokenSecretManager
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
name|security
operator|.
name|NMTokenSecretManagerInNM
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
name|security
operator|.
name|ApplicationACLsManager
import|;
end_import

begin_comment
comment|/**  * Context interface for sharing information across components in the  * NodeManager.  */
end_comment

begin_interface
DECL|interface|Context
specifier|public
interface|interface
name|Context
block|{
comment|/**    * Interface exposing methods related to the queuing of containers in the NM.    */
DECL|interface|QueuingContext
interface|interface
name|QueuingContext
block|{
DECL|method|getQueuedContainers ()
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerTokenIdentifier
argument_list|>
name|getQueuedContainers
parameter_list|()
function_decl|;
DECL|method|getKilledQueuedContainers ()
name|ConcurrentMap
argument_list|<
name|ContainerTokenIdentifier
argument_list|,
name|String
argument_list|>
name|getKilledQueuedContainers
parameter_list|()
function_decl|;
block|}
comment|/**    * Return the nodeId. Usable only when the ContainerManager is started.    *     * @return the NodeId    */
DECL|method|getNodeId ()
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
comment|/**    * Return the node http-address. Usable only after the Webserver is started.    *     * @return the http-port    */
DECL|method|getHttpPort ()
name|int
name|getHttpPort
parameter_list|()
function_decl|;
DECL|method|getApplications ()
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|Application
argument_list|>
name|getApplications
parameter_list|()
function_decl|;
DECL|method|getSystemCredentialsForApps ()
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|Credentials
argument_list|>
name|getSystemCredentialsForApps
parameter_list|()
function_decl|;
DECL|method|getContainers ()
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|getContainers
parameter_list|()
function_decl|;
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
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
name|Container
argument_list|>
DECL|method|getIncreasedContainers ()
name|getIncreasedContainers
parameter_list|()
function_decl|;
DECL|method|getContainerTokenSecretManager ()
name|NMContainerTokenSecretManager
name|getContainerTokenSecretManager
parameter_list|()
function_decl|;
DECL|method|getNMTokenSecretManager ()
name|NMTokenSecretManagerInNM
name|getNMTokenSecretManager
parameter_list|()
function_decl|;
DECL|method|getNodeHealthStatus ()
name|NodeHealthStatus
name|getNodeHealthStatus
parameter_list|()
function_decl|;
DECL|method|getContainerManager ()
name|ContainerManagementProtocol
name|getContainerManager
parameter_list|()
function_decl|;
DECL|method|getNodeResourceMonitor ()
name|NodeResourceMonitor
name|getNodeResourceMonitor
parameter_list|()
function_decl|;
DECL|method|getLocalDirsHandler ()
name|LocalDirsHandlerService
name|getLocalDirsHandler
parameter_list|()
function_decl|;
DECL|method|getApplicationACLsManager ()
name|ApplicationACLsManager
name|getApplicationACLsManager
parameter_list|()
function_decl|;
DECL|method|getNMStateStore ()
name|NMStateStoreService
name|getNMStateStore
parameter_list|()
function_decl|;
DECL|method|getDecommissioned ()
name|boolean
name|getDecommissioned
parameter_list|()
function_decl|;
DECL|method|setDecommissioned (boolean isDecommissioned)
name|void
name|setDecommissioned
parameter_list|(
name|boolean
name|isDecommissioned
parameter_list|)
function_decl|;
name|ConcurrentLinkedQueue
argument_list|<
name|LogAggregationReport
argument_list|>
DECL|method|getLogAggregationStatusForApps ()
name|getLogAggregationStatusForApps
parameter_list|()
function_decl|;
DECL|method|getNodeStatusUpdater ()
name|NodeStatusUpdater
name|getNodeStatusUpdater
parameter_list|()
function_decl|;
comment|/**    * Returns a<code>QueuingContext</code> that provides information about the    * number of Containers Queued as well as the number of Containers that were    * queued and killed.    */
DECL|method|getQueuingContext ()
name|QueuingContext
name|getQueuingContext
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

