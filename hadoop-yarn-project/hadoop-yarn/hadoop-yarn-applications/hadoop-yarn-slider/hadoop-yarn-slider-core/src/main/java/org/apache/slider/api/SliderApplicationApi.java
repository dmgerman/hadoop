begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.api
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
package|;
end_package

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
name|ContainerInformation
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
name|api
operator|.
name|types
operator|.
name|PingInformation
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
name|ConfTree
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
name|ConfTreeOperations
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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * API exported by the slider remote REST/IPC endpoints.  */
end_comment

begin_interface
DECL|interface|SliderApplicationApi
specifier|public
interface|interface
name|SliderApplicationApi
block|{
comment|/**    * Get the aggregate desired model    * @return the aggregate configuration of what was asked for    * -before resolution has taken place    * @throws IOException on any failure    */
DECL|method|getDesiredModel ()
name|AggregateConf
name|getDesiredModel
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the desired application configuration    * @return the application configuration asked for    * -before resolution has taken place    * @throws IOException on any failure    */
DECL|method|getDesiredAppconf ()
name|ConfTreeOperations
name|getDesiredAppconf
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the desired YARN resources    * @return the resources asked for    * -before resolution has taken place    * @throws IOException on any failure    */
DECL|method|getDesiredResources ()
name|ConfTreeOperations
name|getDesiredResources
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the aggregate resolved model    * @return the aggregate configuration of what was asked for    * -after resolution has taken place    * @throws IOException on any failure    */
DECL|method|getResolvedModel ()
name|AggregateConf
name|getResolvedModel
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the resolved application configuration    * @return the application configuration asked for    * -after resolution has taken place    * @throws IOException on any failure    */
DECL|method|getResolvedAppconf ()
name|ConfTreeOperations
name|getResolvedAppconf
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the resolved YARN resources    * @return the resources asked for    * -after resolution has taken place    * @throws IOException on any failure    */
DECL|method|getResolvedResources ()
name|ConfTreeOperations
name|getResolvedResources
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the live YARN resources    * @return the live set of resources in the cluster    * @throws IOException on any failure    */
DECL|method|getLiveResources ()
name|ConfTreeOperations
name|getLiveResources
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a map of live containers [containerId:info]    * @return a possibly empty list of serialized containers    * @throws IOException on any failure    */
DECL|method|enumContainers ()
name|Map
argument_list|<
name|String
argument_list|,
name|ContainerInformation
argument_list|>
name|enumContainers
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a container from the container Id    * @param containerId YARN container ID    * @return the container information    * @throws IOException on any failure    */
DECL|method|getContainer (String containerId)
name|ContainerInformation
name|getContainer
parameter_list|(
name|String
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * List all components into a map of [name:info]    * @return a possibly empty map of components    * @throws IOException on any failure    */
DECL|method|enumComponents ()
name|Map
argument_list|<
name|String
argument_list|,
name|ComponentInformation
argument_list|>
name|enumComponents
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get information about a component    * @param componentName name of the component    * @return the component details    * @throws IOException on any failure    */
DECL|method|getComponent (String componentName)
name|ComponentInformation
name|getComponent
parameter_list|(
name|String
name|componentName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * List all nodes into a map of [name:info]    * @return a possibly empty list of nodes    * @throws IOException on any failure    */
DECL|method|getLiveNodes ()
name|NodeInformationList
name|getLiveNodes
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get information about a node    * @param hostname name of the node    * @return the node details    * @throws IOException on any failure    */
DECL|method|getLiveNode (String hostname)
name|NodeInformation
name|getLiveNode
parameter_list|(
name|String
name|hostname
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Ping as a GET    * @param text text to include    * @return the response    * @throws IOException on any failure    */
DECL|method|ping (String text)
name|PingInformation
name|ping
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Stop the AM (async operation)    * @param text text to include    * @throws IOException on any failure    */
DECL|method|stop (String text)
name|void
name|stop
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the application liveness    * @return current liveness information    * @throws IOException    */
DECL|method|getApplicationLiveness ()
name|ApplicationLivenessInformation
name|getApplicationLiveness
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

