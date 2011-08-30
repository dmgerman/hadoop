begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
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
name|records
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
name|ClientRMProtocol
import|;
end_import

begin_comment
comment|/**  *<p><code>NodeHealthStatus</code> is a summary of the health status of the  * node.</p>  *  *<p>It includes information such as:  *<ul>  *<li>  *       An indicator of whether the node is healthy, as determined by the   *       health-check script.  *</li>  *<li>The previous time at which the health status was reported.</li>  *<li>A diagnostic report on the health status.</li>  *<li></li>  *<li></li>  *</ul>  *</p>  *   * @see NodeReport  * @see ClientRMProtocol#getClusterNodes(org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|NodeHealthStatus
specifier|public
interface|interface
name|NodeHealthStatus
block|{
comment|/**    * Is the node healthy?    * @return<code>true</code> if the node is healthy, else<code>false</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getIsNodeHealthy ()
name|boolean
name|getIsNodeHealthy
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setIsNodeHealthy (boolean isNodeHealthy)
name|void
name|setIsNodeHealthy
parameter_list|(
name|boolean
name|isNodeHealthy
parameter_list|)
function_decl|;
comment|/**    * Get the<em>diagnostic health report</em> of the node.    * @return<em>diagnostic health report</em> of the node    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getHealthReport ()
name|String
name|getHealthReport
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setHealthReport (String healthReport)
name|void
name|setHealthReport
parameter_list|(
name|String
name|healthReport
parameter_list|)
function_decl|;
comment|/**    * Get the<em>last timestamp</em> at which the health report was received.    * @return<em>last timestamp</em> at which the health report was received    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getLastHealthReportTime ()
name|long
name|getLastHealthReportTime
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setLastHealthReportTime (long lastHealthReport)
name|void
name|setLastHealthReportTime
parameter_list|(
name|long
name|lastHealthReport
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

