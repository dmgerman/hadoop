begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
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
name|api
operator|.
name|protocolrecords
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
name|Public
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
name|Evolving
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
name|api
operator|.
name|records
operator|.
name|ResourceOption
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
name|ResourceManagerAdministrationProtocol
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>The request sent by admin to change a list of nodes' resource to the   *<code>ResourceManager</code>.</p>  *   *<p>The request contains details such as a map from {@link NodeId} to   * {@link ResourceOption} for updating the RMNodes' resources in   *<code>ResourceManager</code>.  *   * @see ResourceManagerAdministrationProtocol#updateNodeResource(  *      UpdateNodeResourceRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|UpdateNodeResourceRequest
specifier|public
specifier|abstract
class|class
name|UpdateNodeResourceRequest
block|{
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|newInstance ( Map<NodeId, ResourceOption> nodeResourceMap)
specifier|public
specifier|static
name|UpdateNodeResourceRequest
name|newInstance
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|ResourceOption
argument_list|>
name|nodeResourceMap
parameter_list|)
block|{
name|UpdateNodeResourceRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|UpdateNodeResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setNodeResourceMap
argument_list|(
name|nodeResourceMap
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Get the map from<code>NodeId</code> to<code>ResourceOption</code>.    * @return the map of {@code<NodeId, ResourceOption>}    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getNodeResourceMap ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|NodeId
argument_list|,
name|ResourceOption
argument_list|>
name|getNodeResourceMap
parameter_list|()
function_decl|;
comment|/**    * Set the map from<code>NodeId</code> to<code>ResourceOption</code>.    * @param nodeResourceMap the map of {@code<NodeId, ResourceOption>}    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|setNodeResourceMap (Map<NodeId, ResourceOption> nodeResourceMap)
specifier|public
specifier|abstract
name|void
name|setNodeResourceMap
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|ResourceOption
argument_list|>
name|nodeResourceMap
parameter_list|)
function_decl|;
block|}
end_class

end_unit

