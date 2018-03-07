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
name|AppCollectorData
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
name|ContainerManager
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
name|containermanager
operator|.
name|resourceplugin
operator|.
name|ResourcePluginManager
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
name|metrics
operator|.
name|NodeManagerMetrics
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
name|scheduler
operator|.
name|OpportunisticContainerAllocator
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
name|nodemanager
operator|.
name|timelineservice
operator|.
name|NMTimelinePublisher
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
comment|/**    * Get the list of collectors that are registering with the RM from this node.    * @return registering collectors, or null if the timeline service v.2 is not    * enabled    */
DECL|method|getRegisteringCollectors ()
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|AppCollectorData
argument_list|>
name|getRegisteringCollectors
parameter_list|()
function_decl|;
comment|/**    * Get the list of collectors registered with the RM and known by this node.    * @return known collectors, or null if the timeline service v.2 is not    * enabled.    */
DECL|method|getKnownCollectors ()
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|AppCollectorData
argument_list|>
name|getKnownCollectors
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
name|ContainerManager
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
DECL|method|getConf ()
name|Configuration
name|getConf
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
DECL|method|isDistributedSchedulingEnabled ()
name|boolean
name|isDistributedSchedulingEnabled
parameter_list|()
function_decl|;
DECL|method|getContainerAllocator ()
name|OpportunisticContainerAllocator
name|getContainerAllocator
parameter_list|()
function_decl|;
DECL|method|setNMTimelinePublisher (NMTimelinePublisher nmMetricsPublisher)
name|void
name|setNMTimelinePublisher
parameter_list|(
name|NMTimelinePublisher
name|nmMetricsPublisher
parameter_list|)
function_decl|;
DECL|method|getNMTimelinePublisher ()
name|NMTimelinePublisher
name|getNMTimelinePublisher
parameter_list|()
function_decl|;
DECL|method|getContainerExecutor ()
name|ContainerExecutor
name|getContainerExecutor
parameter_list|()
function_decl|;
DECL|method|getContainerStateTransitionListener ()
name|ContainerStateTransitionListener
name|getContainerStateTransitionListener
parameter_list|()
function_decl|;
DECL|method|getResourcePluginManager ()
name|ResourcePluginManager
name|getResourcePluginManager
parameter_list|()
function_decl|;
DECL|method|getNodeManagerMetrics ()
name|NodeManagerMetrics
name|getNodeManagerMetrics
parameter_list|()
function_decl|;
comment|/**    * Get the {@code DeletionService} associated with the NM.    *    * @return the NM {@code DeletionService}.    */
DECL|method|getDeletionService ()
name|DeletionService
name|getDeletionService
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

