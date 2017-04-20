begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.client
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|client
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryOperations
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
name|exceptions
operator|.
name|YarnException
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
name|types
operator|.
name|NodeInformationList
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
name|params
operator|.
name|AbstractClusterBuildingActionArgs
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
name|params
operator|.
name|ActionAMSuicideArgs
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
name|params
operator|.
name|ActionClientArgs
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
name|params
operator|.
name|ActionDependencyArgs
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
name|params
operator|.
name|ActionDiagnosticArgs
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
name|params
operator|.
name|ActionFlexArgs
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
name|params
operator|.
name|ActionFreezeArgs
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
name|params
operator|.
name|ActionKeytabArgs
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
name|params
operator|.
name|ActionNodesArgs
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
name|params
operator|.
name|ActionKillContainerArgs
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
name|params
operator|.
name|ActionListArgs
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
name|params
operator|.
name|ActionRegistryArgs
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
name|params
operator|.
name|ActionResolveArgs
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
name|params
operator|.
name|ActionResourceArgs
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
name|params
operator|.
name|ActionStatusArgs
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
name|params
operator|.
name|ActionThawArgs
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
name|params
operator|.
name|ActionUpgradeArgs
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
name|providers
operator|.
name|AbstractClientProvider
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

begin_comment
comment|/**  * Interface of those method calls in the slider API that are intended  * for direct public invocation.  *<p>  * Stability: evolving  */
end_comment

begin_interface
DECL|interface|SliderClientAPI
specifier|public
interface|interface
name|SliderClientAPI
extends|extends
name|Service
block|{
DECL|method|actionDestroy (String clustername)
name|int
name|actionDestroy
parameter_list|(
name|String
name|clustername
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * AM to commit an asynchronous suicide    */
DECL|method|actionAmSuicide (String clustername, ActionAMSuicideArgs args)
name|int
name|actionAmSuicide
parameter_list|(
name|String
name|clustername
parameter_list|,
name|ActionAMSuicideArgs
name|args
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Get the provider for this cluster    * @param provider the name of the provider    * @return the provider instance    * @throws SliderException problems building the provider    */
DECL|method|createClientProvider (String provider)
name|AbstractClientProvider
name|createClientProvider
parameter_list|(
name|String
name|provider
parameter_list|)
throws|throws
name|SliderException
function_decl|;
comment|/**    * Manage keytabs leveraged by slider    *    * @param keytabInfo the arguments needed to manage the keytab    * @throws YarnException Yarn problems    * @throws IOException other problems    * @throws BadCommandArgumentsException bad arguments.    */
DECL|method|actionKeytab (ActionKeytabArgs keytabInfo)
name|int
name|actionKeytab
parameter_list|(
name|ActionKeytabArgs
name|keytabInfo
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Manage file resources leveraged by slider    *    * @param resourceInfo the arguments needed to manage the resource    * @throws YarnException Yarn problems    * @throws IOException other problems    * @throws BadCommandArgumentsException bad arguments.    */
DECL|method|actionResource (ActionResourceArgs resourceInfo)
name|int
name|actionResource
parameter_list|(
name|ActionResourceArgs
name|resourceInfo
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Perform client operations such as install or configure    *    * @param clientInfo the arguments needed for client operations    *    * @throws SliderException bad arguments.    * @throws IOException problems related to package and destination folders    */
DECL|method|actionClient (ActionClientArgs clientInfo)
name|int
name|actionClient
parameter_list|(
name|ActionClientArgs
name|clientInfo
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Update the cluster specification    *    * @param clustername cluster name    * @param buildInfo the arguments needed to update the cluster    * @throws YarnException Yarn problems    * @throws IOException other problems    */
DECL|method|actionUpdate (String clustername, AbstractClusterBuildingActionArgs buildInfo)
name|int
name|actionUpdate
parameter_list|(
name|String
name|clustername
parameter_list|,
name|AbstractClusterBuildingActionArgs
name|buildInfo
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Upgrade the cluster with a newer version of the application    *    * @param clustername cluster name    * @param buildInfo the arguments needed to upgrade the cluster    * @throws YarnException Yarn problems    * @throws IOException other problems    */
DECL|method|actionUpgrade (String clustername, ActionUpgradeArgs buildInfo)
name|int
name|actionUpgrade
parameter_list|(
name|String
name|clustername
parameter_list|,
name|ActionUpgradeArgs
name|buildInfo
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Implement the list action: list all nodes    * @return exit code of 0 if a list was created    */
DECL|method|actionList (String clustername, ActionListArgs args)
name|int
name|actionList
parameter_list|(
name|String
name|clustername
parameter_list|,
name|ActionListArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|actionFlex (String name, ActionFlexArgs args)
name|int
name|actionFlex
parameter_list|(
name|String
name|name
parameter_list|,
name|ActionFlexArgs
name|args
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Test for a cluster existing probe for a cluster of the given name existing    * in the filesystem. If the live param is set, it must be a live cluster    * @return exit code    */
DECL|method|actionExists (String name, boolean checkLive)
name|int
name|actionExists
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|checkLive
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Kill a specific container of the cluster    * @param name cluster name    * @param args arguments    * @return exit code    * @throws YarnException    * @throws IOException    */
DECL|method|actionKillContainer (String name, ActionKillContainerArgs args)
name|int
name|actionKillContainer
parameter_list|(
name|String
name|name
parameter_list|,
name|ActionKillContainerArgs
name|args
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Status operation    *    * @param clustername cluster name    * @param statusArgs status arguments    * @return 0 -for success, else an exception is thrown    * @throws YarnException    * @throws IOException    */
DECL|method|actionStatus (String clustername, ActionStatusArgs statusArgs)
name|int
name|actionStatus
parameter_list|(
name|String
name|clustername
parameter_list|,
name|ActionStatusArgs
name|statusArgs
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Status operation which returns the status object as a string instead of    * printing it to the console or file.    *    * @param clustername cluster name    * @return cluster status details    * @throws YarnException    * @throws IOException    */
DECL|method|actionStatus (String clustername)
name|Application
name|actionStatus
parameter_list|(
name|String
name|clustername
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Version Details    * @return exit code    */
DECL|method|actionVersion ()
name|int
name|actionVersion
parameter_list|()
function_decl|;
comment|/**    * Stop the cluster    *    * @param clustername cluster name    * @param freezeArgs arguments to the stop    * @return EXIT_SUCCESS if the cluster was not running by the end of the operation    */
DECL|method|actionStop (String clustername, ActionFreezeArgs freezeArgs)
name|int
name|actionStop
parameter_list|(
name|String
name|clustername
parameter_list|,
name|ActionFreezeArgs
name|freezeArgs
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Restore a cluster    */
DECL|method|actionStart (String clustername, ActionThawArgs thaw)
name|int
name|actionStart
parameter_list|(
name|String
name|clustername
parameter_list|,
name|ActionThawArgs
name|thaw
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Registry operation    *    * @param args registry Arguments    * @return 0 for success, -1 for some issues that aren't errors, just failures    * to retrieve information (e.g. no configurations for that entry)    * @throws YarnException YARN problems    * @throws IOException Network or other problems    */
DECL|method|actionResolve (ActionResolveArgs args)
name|int
name|actionResolve
parameter_list|(
name|ActionResolveArgs
name|args
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Registry operation    *    * @param registryArgs registry Arguments    * @return 0 for success, -1 for some issues that aren't errors, just failures    * to retrieve information (e.g. no configurations for that entry)    * @throws YarnException YARN problems    * @throws IOException Network or other problems    */
DECL|method|actionRegistry (ActionRegistryArgs registryArgs)
name|int
name|actionRegistry
parameter_list|(
name|ActionRegistryArgs
name|registryArgs
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * diagnostic operation    *    * @param diagnosticArgs diagnostic Arguments    * @return 0 for success, -1 for some issues that aren't errors, just    *         failures to retrieve information (e.g. no application name    *         specified)    * @throws YarnException YARN problems    * @throws IOException Network or other problems    */
DECL|method|actionDiagnostic (ActionDiagnosticArgs diagnosticArgs)
name|int
name|actionDiagnostic
parameter_list|(
name|ActionDiagnosticArgs
name|diagnosticArgs
parameter_list|)
function_decl|;
comment|/**    * Get the registry binding. As this may start the registry, it can take time    * and fail    * @return the registry     */
DECL|method|getRegistryOperations ()
name|RegistryOperations
name|getRegistryOperations
parameter_list|()
throws|throws
name|SliderException
throws|,
name|IOException
function_decl|;
comment|/**    * Upload all Slider AM and agent dependency libraries to HDFS, so that they    * do not need to be uploaded with every create call. This operation is    * Slider version specific. So it needs to be invoked for every single    * version of slider/slider-client.    *     * @throws SliderException    * @throws IOException    */
DECL|method|actionDependency (ActionDependencyArgs dependencyArgs)
name|int
name|actionDependency
parameter_list|(
name|ActionDependencyArgs
name|dependencyArgs
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * List the nodes    * @param args    * @return    * @throws YarnException    * @throws IOException    */
DECL|method|listYarnClusterNodes (ActionNodesArgs args)
name|NodeInformationList
name|listYarnClusterNodes
parameter_list|(
name|ActionNodesArgs
name|args
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

