begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
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
name|service
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
name|records
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ClusterDescription
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|SliderFileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|AggregateConf
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|MapOperations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|BadCommandArgumentsException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|SliderException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|launch
operator|.
name|ContainerLauncher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|main
operator|.
name|ExitCodeProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|QueueAccess
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|operations
operator|.
name|RMOperationHandlerActions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|ContainerReleaseSelector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|StateAccessForProviders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|agent
operator|.
name|AgentRestOperations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|yarnregistry
operator|.
name|YarnRegistryViewForProviders
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
DECL|interface|ProviderService
specifier|public
interface|interface
name|ProviderService
extends|extends
name|ProviderCore
extends|,
name|Service
extends|,
name|RMOperationHandlerActions
extends|,
name|ExitCodeProvider
block|{
comment|/**    * Set up the entire container launch context    * @param containerLauncher    * @param instanceDefinition    * @param container    * @param providerRole    * @param sliderFileSystem    * @param generatedConfPath    * @param appComponent    * @param containerTmpDirPath    */
DECL|method|buildContainerLaunchContext (ContainerLauncher containerLauncher, AggregateConf instanceDefinition, Container container, ProviderRole providerRole, SliderFileSystem sliderFileSystem, Path generatedConfPath, MapOperations resourceComponent, MapOperations appComponent, Path containerTmpDirPath)
name|void
name|buildContainerLaunchContext
parameter_list|(
name|ContainerLauncher
name|containerLauncher
parameter_list|,
name|AggregateConf
name|instanceDefinition
parameter_list|,
name|Container
name|container
parameter_list|,
name|ProviderRole
name|providerRole
parameter_list|,
name|SliderFileSystem
name|sliderFileSystem
parameter_list|,
name|Path
name|generatedConfPath
parameter_list|,
name|MapOperations
name|resourceComponent
parameter_list|,
name|MapOperations
name|appComponent
parameter_list|,
name|Path
name|containerTmpDirPath
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
function_decl|;
comment|/**    * Notify the providers of container completion    * @param containerId container that has completed    */
DECL|method|notifyContainerCompleted (ContainerId containerId)
name|void
name|notifyContainerCompleted
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Execute a process in the AM    * @param instanceDefinition cluster description    * @param confDir configuration directory    * @param env environment    * @param execInProgress the callback for the exec events    * @return true if a process was actually started    * @throws IOException    * @throws SliderException    */
DECL|method|exec (AggregateConf instanceDefinition, File confDir, Map<String, String> env, ProviderCompleted execInProgress)
name|boolean
name|exec
parameter_list|(
name|AggregateConf
name|instanceDefinition
parameter_list|,
name|File
name|confDir
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|,
name|ProviderCompleted
name|execInProgress
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
function_decl|;
comment|/**    * Scan through the roles and see if it is supported.    * @param role role to look for    * @return true if the role is known about -and therefore    * that a launcher thread can be deployed to launch it    */
DECL|method|isSupportedRole (String role)
name|boolean
name|isSupportedRole
parameter_list|(
name|String
name|role
parameter_list|)
function_decl|;
comment|/**    * Load a specific XML configuration file for the provider config    * @param confDir configuration directory    * @return a configuration to be included in status    * @throws BadCommandArgumentsException    * @throws IOException    */
DECL|method|loadProviderConfigurationInformation (File confDir)
name|Configuration
name|loadProviderConfigurationInformation
parameter_list|(
name|File
name|confDir
parameter_list|)
throws|throws
name|BadCommandArgumentsException
throws|,
name|IOException
function_decl|;
comment|/**    * The application configuration should be initialized here    *     * @param instanceDefinition    * @param fileSystem    * @throws IOException    * @throws SliderException    */
DECL|method|initializeApplicationConfiguration (AggregateConf instanceDefinition, SliderFileSystem fileSystem)
name|void
name|initializeApplicationConfiguration
parameter_list|(
name|AggregateConf
name|instanceDefinition
parameter_list|,
name|SliderFileSystem
name|fileSystem
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
function_decl|;
comment|/**    * This is a validation of the application configuration on the AM.    * Here is where things like the existence of keytabs and other    * not-seen-client-side properties can be tested, before    * the actual process is spawned.     * @param instanceDefinition clusterSpecification    * @param confDir configuration directory    * @param secure flag to indicate that secure mode checks must exist    * @throws IOException IO problemsn    * @throws SliderException any failure    */
DECL|method|validateApplicationConfiguration (AggregateConf instanceDefinition, File confDir, boolean secure )
name|void
name|validateApplicationConfiguration
parameter_list|(
name|AggregateConf
name|instanceDefinition
parameter_list|,
name|File
name|confDir
parameter_list|,
name|boolean
name|secure
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
function_decl|;
comment|/*      * Build the provider status, can be empty      * @return the provider status - map of entries to add to the info section      */
DECL|method|buildProviderStatus ()
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|buildProviderStatus
parameter_list|()
function_decl|;
comment|/**    * Build a map of data intended for the AM webapp that is specific    * about this provider. The key is some text to be displayed, and the    * value can be a URL that will create an anchor over the key text.    *     * If no anchor is needed/desired, insert the key with a null value.    * @return the details    */
DECL|method|buildMonitorDetails (ClusterDescription clusterSpec)
name|Map
argument_list|<
name|String
argument_list|,
name|MonitorDetail
argument_list|>
name|buildMonitorDetails
parameter_list|(
name|ClusterDescription
name|clusterSpec
parameter_list|)
function_decl|;
comment|/**    * Get a human friendly name for web UIs and messages    * @return a name string. Default is simply the service instance name.    */
DECL|method|getHumanName ()
name|String
name|getHumanName
parameter_list|()
function_decl|;
DECL|method|bind (StateAccessForProviders stateAccessor, QueueAccess queueAccess, List<Container> liveContainers)
specifier|public
name|void
name|bind
parameter_list|(
name|StateAccessForProviders
name|stateAccessor
parameter_list|,
name|QueueAccess
name|queueAccess
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|liveContainers
parameter_list|)
function_decl|;
comment|/**    * Bind to the YARN registry    * @param yarnRegistry YARN registry    */
DECL|method|bindToYarnRegistry (YarnRegistryViewForProviders yarnRegistry)
name|void
name|bindToYarnRegistry
parameter_list|(
name|YarnRegistryViewForProviders
name|yarnRegistry
parameter_list|)
function_decl|;
comment|/**    * Returns the agent rest operations interface.    * @return  the interface if available, null otherwise.    */
DECL|method|getAgentRestOperations ()
name|AgentRestOperations
name|getAgentRestOperations
parameter_list|()
function_decl|;
comment|/**    * Build up the endpoint details for this service    * @param details    */
DECL|method|buildEndpointDetails (Map<String, MonitorDetail> details)
name|void
name|buildEndpointDetails
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|MonitorDetail
argument_list|>
name|details
parameter_list|)
function_decl|;
comment|/**    * Prior to going live -register the initial service registry data    * @param amWebURI URL to the AM. This may be proxied, so use relative paths    * @param agentOpsURI URI for agent operations. This will not be proxied    * @param agentStatusURI URI For agent status. Again: no proxy    * @param serviceRecord service record to build up    */
DECL|method|applyInitialRegistryDefinitions (URL amWebURI, URL agentOpsURI, URL agentStatusURI, ServiceRecord serviceRecord)
name|void
name|applyInitialRegistryDefinitions
parameter_list|(
name|URL
name|amWebURI
parameter_list|,
name|URL
name|agentOpsURI
parameter_list|,
name|URL
name|agentStatusURI
parameter_list|,
name|ServiceRecord
name|serviceRecord
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create the container release selector for this provider...any policy    * can be implemented    * @return the selector to use for choosing containers.    */
DECL|method|createContainerReleaseSelector ()
name|ContainerReleaseSelector
name|createContainerReleaseSelector
parameter_list|()
function_decl|;
comment|/**    * On AM restart (for whatever reason) this API is required to rebuild the AM    * internal state with the containers which were already assigned and running    *     * @param liveContainers    * @param applicationId    * @param providerRoles    */
DECL|method|rebuildContainerDetails (List<Container> liveContainers, String applicationId, Map<Integer, ProviderRole> providerRoles)
name|void
name|rebuildContainerDetails
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|liveContainers
parameter_list|,
name|String
name|applicationId
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|ProviderRole
argument_list|>
name|providerRoles
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

