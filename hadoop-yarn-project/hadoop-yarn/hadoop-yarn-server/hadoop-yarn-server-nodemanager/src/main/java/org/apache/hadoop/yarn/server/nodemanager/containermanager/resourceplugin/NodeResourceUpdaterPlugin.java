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
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * Plugins to handle resources on a node. This will be used by  * {@link org.apache.hadoop.yarn.server.nodemanager.NodeStatusUpdater}  */
end_comment

begin_class
DECL|class|NodeResourceUpdaterPlugin
specifier|public
specifier|abstract
class|class
name|NodeResourceUpdaterPlugin
block|{
comment|/**    * Update configured resource for the given component.    * @param res resource passed in by external mododule (such as    *            {@link org.apache.hadoop.yarn.server.nodemanager.NodeStatusUpdater}    * @throws YarnException when any issue happens.    */
DECL|method|updateConfiguredResource (Resource res)
specifier|public
specifier|abstract
name|void
name|updateConfiguredResource
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * This method will be called when the node's resource is loaded from    * dynamic-resources.xml in ResourceManager.    *    * @param newResource newResource reported by RM    * @throws YarnException when any mismatch between NM/RM    */
DECL|method|handleUpdatedResourceFromRM (Resource newResource)
specifier|public
name|void
name|handleUpdatedResourceFromRM
parameter_list|(
name|Resource
name|newResource
parameter_list|)
throws|throws
name|YarnException
block|{
comment|// by default do nothing, subclass should implement this method when any
comment|// special activities required upon new resource reported by RM.
block|}
comment|// TODO: add implementation to update node attribute once YARN-3409 merged.
block|}
end_class

end_unit

