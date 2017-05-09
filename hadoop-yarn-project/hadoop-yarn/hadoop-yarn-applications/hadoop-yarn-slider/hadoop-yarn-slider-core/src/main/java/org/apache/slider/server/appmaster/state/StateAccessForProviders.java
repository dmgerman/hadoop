begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.state
package|package
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|LoadingCache
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRuntimeException
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
name|ClusterNode
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
name|StatusKeys
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
name|resource
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
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|ConfigFile
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
name|types
operator|.
name|ApplicationLivenessInformation
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
name|types
operator|.
name|ComponentInformation
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
name|types
operator|.
name|NodeInformation
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
name|types
operator|.
name|RoleStatistics
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
name|NoSuchNodeException
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
name|registry
operator|.
name|docstore
operator|.
name|PublishedConfigSet
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
name|registry
operator|.
name|docstore
operator|.
name|PublishedExportsSet
import|;
end_import

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

begin_comment
comment|/**  * The methods to offer state access to the providers and other parts of  * the system which want read-only access to the state.  */
end_comment

begin_interface
DECL|interface|StateAccessForProviders
specifier|public
interface|interface
name|StateAccessForProviders
block|{
comment|/**    * Get a map of role status entries by role Id    * @return the map of currently defined roles.    */
DECL|method|getRoleStatusMap ()
name|Map
argument_list|<
name|Integer
argument_list|,
name|RoleStatus
argument_list|>
name|getRoleStatusMap
parameter_list|()
function_decl|;
comment|/**    * Get the name of the application    * @return the name    */
DECL|method|getApplicationName ()
name|String
name|getApplicationName
parameter_list|()
function_decl|;
comment|/**    * Get the published configurations    * @return the configuration set    */
DECL|method|getPublishedSliderConfigurations ()
name|PublishedConfigSet
name|getPublishedSliderConfigurations
parameter_list|()
function_decl|;
comment|/**    * Get the published exports set    * @return    */
DECL|method|getPublishedExportsSet ()
name|PublishedExportsSet
name|getPublishedExportsSet
parameter_list|()
function_decl|;
comment|/**    * Get a named published config set    * @param name name to look up    * @return the instance or null    */
DECL|method|getPublishedConfigSet (String name)
name|PublishedConfigSet
name|getPublishedConfigSet
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Get a named published config set, creating it if need be.    * @param name name to look up    * @return the instance -possibly a new one    */
DECL|method|getOrCreatePublishedConfigSet (String name)
name|PublishedConfigSet
name|getOrCreatePublishedConfigSet
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * List the config sets -this takes a clone of the current set    * @return a list of config sets    */
DECL|method|listConfigSets ()
name|List
argument_list|<
name|String
argument_list|>
name|listConfigSets
parameter_list|()
function_decl|;
comment|/**    * Get a map of all the failed containers    * @return map of recorded failed containers    */
DECL|method|getFailedContainers ()
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|RoleInstance
argument_list|>
name|getFailedContainers
parameter_list|()
function_decl|;
comment|/**    * Get the live containers.    *     * @return the live nodes    */
DECL|method|getLiveContainers ()
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|RoleInstance
argument_list|>
name|getLiveContainers
parameter_list|()
function_decl|;
comment|/**    * Get the current cluster description     * @return the actual state of the cluster    */
DECL|method|getApplication ()
name|Application
name|getApplication
parameter_list|()
function_decl|;
comment|/**    * Flag set to indicate the application is live -this only happens    * after the buildInstance operation    */
DECL|method|isApplicationLive ()
name|boolean
name|isApplicationLive
parameter_list|()
function_decl|;
comment|/**    * Look up a role from its key -or fail    *    * @param key key to resolve    * @return the status    * @throws YarnRuntimeException on no match    */
DECL|method|lookupRoleStatus (int key)
name|RoleStatus
name|lookupRoleStatus
parameter_list|(
name|int
name|key
parameter_list|)
function_decl|;
comment|/**    * Look up a role from its key -or fail    *    * @param c container in a role    * @return the status    * @throws YarnRuntimeException on no match    */
DECL|method|lookupRoleStatus (Container c)
name|RoleStatus
name|lookupRoleStatus
parameter_list|(
name|Container
name|c
parameter_list|)
throws|throws
name|YarnRuntimeException
function_decl|;
comment|/**    * Look up a role from its key -or fail     *    * @param name container in a role    * @return the status    * @throws YarnRuntimeException on no match    */
DECL|method|lookupRoleStatus (String name)
name|RoleStatus
name|lookupRoleStatus
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|YarnRuntimeException
function_decl|;
comment|/**    * Clone a list of active containers    * @return the active containers at the time    * the call was made    */
DECL|method|cloneOwnedContainerList ()
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|cloneOwnedContainerList
parameter_list|()
function_decl|;
comment|/**    * Get the number of active containers    * @return the number of active containers the time the call was made    */
DECL|method|getNumOwnedContainers ()
name|int
name|getNumOwnedContainers
parameter_list|()
function_decl|;
comment|/**    * Get any active container with the given ID    * @param id container Id    * @return the active container or null if it is not found    */
DECL|method|getOwnedContainer (ContainerId id)
name|RoleInstance
name|getOwnedContainer
parameter_list|(
name|ContainerId
name|id
parameter_list|)
function_decl|;
comment|/**    * Get any active container with the given ID    * @param id container Id    * @return the active container or null if it is not found    */
DECL|method|getOwnedContainer (String id)
name|RoleInstance
name|getOwnedContainer
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|NoSuchNodeException
function_decl|;
comment|/**    * Create a clone of the list of live cluster nodes.    * @return the list of nodes, may be empty    */
DECL|method|cloneLiveContainerInfoList ()
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|cloneLiveContainerInfoList
parameter_list|()
function_decl|;
comment|/**    * Get the {@link RoleInstance} details on a container.    * This is an O(n) operation    * @param containerId the container ID    * @return null if there is no such node    * @throws NoSuchNodeException if the node cannot be found    */
DECL|method|getLiveInstanceByContainerID (String containerId)
name|RoleInstance
name|getLiveInstanceByContainerID
parameter_list|(
name|String
name|containerId
parameter_list|)
throws|throws
name|NoSuchNodeException
function_decl|;
comment|/**    * Get the details on a list of instaces referred to by ID.    * Unknown nodes are not returned    *<i>Important: the order of the results are undefined</i>    * @param containerIDs the containers    * @return list of instances    */
DECL|method|getLiveInstancesByContainerIDs ( Collection<String> containerIDs)
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|getLiveInstancesByContainerIDs
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|containerIDs
parameter_list|)
function_decl|;
comment|/**    * Update the cluster description with anything interesting    */
DECL|method|refreshClusterStatus ()
name|Application
name|refreshClusterStatus
parameter_list|()
function_decl|;
comment|/**    * get application liveness information    * @return a snapshot of the current liveness information    */
DECL|method|getApplicationLivenessInformation ()
name|ApplicationLivenessInformation
name|getApplicationLivenessInformation
parameter_list|()
function_decl|;
comment|/**    * Get a snapshot of component information.    *<p>    *   This does<i>not</i> include any container list, which     *   is more expensive to create.    * @return a map of current role status values.    */
DECL|method|getComponentInfoSnapshot ()
name|Map
argument_list|<
name|String
argument_list|,
name|ComponentInformation
argument_list|>
name|getComponentInfoSnapshot
parameter_list|()
function_decl|;
comment|/**    * Find out about the nodes for specific roles    * Component_name -> ContainerId -> ClusterNode    * @return     */
DECL|method|getRoleClusterNodeMapping ()
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterNode
argument_list|>
argument_list|>
name|getRoleClusterNodeMapping
parameter_list|()
function_decl|;
comment|/**    * Enum all role instances by role.    * @param role role, or "" for all roles    * @return a list of instances, may be empty    */
DECL|method|enumLiveInstancesInRole (String role)
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|enumLiveInstancesInRole
parameter_list|(
name|String
name|role
parameter_list|)
function_decl|;
comment|/**    * Look up all containers of a specific component name     * @param component component/role name    * @return list of instances. This is a snapshot    */
DECL|method|lookupRoleContainers (String component)
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|lookupRoleContainers
parameter_list|(
name|String
name|component
parameter_list|)
function_decl|;
comment|/**    * Get the JSON serializable information about a component    * @param component component to look up    * @return a structure describing the component.    */
DECL|method|getComponentInformation (String component)
name|ComponentInformation
name|getComponentInformation
parameter_list|(
name|String
name|component
parameter_list|)
function_decl|;
comment|/**    * Get a clone of the nodemap.    * The instances inside are not cloned    * @return a possibly empty map of hostname top info    */
DECL|method|getNodeInformationSnapshot ()
name|Map
argument_list|<
name|String
argument_list|,
name|NodeInformation
argument_list|>
name|getNodeInformationSnapshot
parameter_list|()
function_decl|;
comment|/**    * get information on a node    * @param hostname hostname to look up    * @return the information, or null if there is no information held.    */
DECL|method|getNodeInformation (String hostname)
name|NodeInformation
name|getNodeInformation
parameter_list|(
name|String
name|hostname
parameter_list|)
function_decl|;
comment|/**    * Get the aggregate statistics across all roles    * @return role statistics    */
DECL|method|getRoleStatistics ()
name|RoleStatistics
name|getRoleStatistics
parameter_list|()
function_decl|;
comment|/**    * Get global substitution tokens.    */
DECL|method|getGlobalSubstitutionTokens ()
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getGlobalSubstitutionTokens
parameter_list|()
function_decl|;
comment|/**    * Get config file cache.    */
DECL|method|getConfigFileCache ()
name|LoadingCache
argument_list|<
name|ConfigFile
argument_list|,
name|Object
argument_list|>
name|getConfigFileCache
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

