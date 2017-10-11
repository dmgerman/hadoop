begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin
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
name|resourceplugin
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
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|Context
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
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperationExecutor
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
name|linux
operator|.
name|resources
operator|.
name|CGroupsHandler
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
name|linux
operator|.
name|resources
operator|.
name|ResourceHandler
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
name|linux
operator|.
name|resources
operator|.
name|ResourceHandlerChain
import|;
end_import

begin_comment
comment|/**  * {@link ResourcePlugin} is an interface for node manager to easier support  * discovery/manage/isolation for new resource types.  *  *<p>  * It has two major part: {@link ResourcePlugin#createResourceHandler(Context,  * CGroupsHandler, PrivilegedOperationExecutor)} and  * {@link ResourcePlugin#getNodeResourceHandlerInstance()}, see javadocs below  * for more details.  *</p>  */
end_comment

begin_interface
DECL|interface|ResourcePlugin
specifier|public
interface|interface
name|ResourcePlugin
block|{
comment|/**    * Initialize the plugin, this will be invoked during NM startup.    * @param context NM Context    * @throws YarnException when any issue occurs    */
DECL|method|initialize (Context context)
name|void
name|initialize
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Plugin needs to return {@link ResourceHandler} when any special isolation    * required for the resource type. This will be added to    * {@link ResourceHandlerChain} during NodeManager startup. When no special    * isolation need, return null.    *    * @param nmContext NodeManager context.    * @param cGroupsHandler CGroupsHandler    * @param privilegedOperationExecutor Privileged Operation Executor.    * @return ResourceHandler    */
DECL|method|createResourceHandler (Context nmContext, CGroupsHandler cGroupsHandler, PrivilegedOperationExecutor privilegedOperationExecutor)
name|ResourceHandler
name|createResourceHandler
parameter_list|(
name|Context
name|nmContext
parameter_list|,
name|CGroupsHandler
name|cGroupsHandler
parameter_list|,
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|)
function_decl|;
comment|/**    * Plugin needs to return {@link NodeResourceUpdaterPlugin} when any discovery    * mechanism required for the resource type. For example, if we want to set    * resource-value during NM registration or send update during NM-RM heartbeat    * We can implement a {@link NodeResourceUpdaterPlugin} and update fields of    * {@link org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest}    * or {@link org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest}    *    * This will be invoked during every node status update or node registration,    * please avoid creating new instance every time.    *    * @return NodeResourceUpdaterPlugin, could be null when no discovery needed.    */
DECL|method|getNodeResourceHandlerInstance ()
name|NodeResourceUpdaterPlugin
name|getNodeResourceHandlerInstance
parameter_list|()
function_decl|;
comment|/**    * Do cleanup of the plugin, this will be invoked when    * {@link org.apache.hadoop.yarn.server.nodemanager.NodeManager} stops    * @throws YarnException if any issue occurs    */
DECL|method|cleanup ()
name|void
name|cleanup
parameter_list|()
throws|throws
name|YarnException
function_decl|;
block|}
end_interface

end_unit

