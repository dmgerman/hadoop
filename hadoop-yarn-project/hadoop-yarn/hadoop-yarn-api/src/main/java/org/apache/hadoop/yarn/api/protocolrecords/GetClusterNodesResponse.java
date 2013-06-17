begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
package|package
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
name|protocolrecords
package|;
end_package

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
name|Private
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
name|Stable
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
name|Unstable
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
name|ApplicationClientProtocol
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
name|NodeReport
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
comment|/**  *<p>The response sent by the<code>ResourceManager</code> to a client  * requesting a {@link NodeReport} for all nodes.</p>  *   *<p>The<code>NodeReport</code> contains per-node information such as   * available resources, number of containers, tracking url, rack name, health  * status etc.  *   * @see NodeReport  * @see ApplicationClientProtocol#getClusterNodes(GetClusterNodesRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|GetClusterNodesResponse
specifier|public
specifier|abstract
class|class
name|GetClusterNodesResponse
block|{
specifier|public
specifier|static
name|GetClusterNodesResponse
DECL|method|newInstance (List<NodeReport> nodeReports)
name|newInstance
parameter_list|(
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodeReports
parameter_list|)
block|{
name|GetClusterNodesResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetClusterNodesResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setNodeReports
argument_list|(
name|nodeReports
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Get<code>NodeReport</code> for all nodes in the cluster.    * @return<code>NodeReport</code> for all nodes in the cluster    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNodeReports ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getNodeReports
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNodeReports (List<NodeReport> nodeReports)
specifier|public
specifier|abstract
name|void
name|setNodeReports
parameter_list|(
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodeReports
parameter_list|)
function_decl|;
block|}
end_class

end_unit

